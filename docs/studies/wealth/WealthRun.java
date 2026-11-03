package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The wealth study's measuring driver (docs/studies/spec-wealth.md): one seeded
 * city founded and played as the ensemble's AUTO2 plays it (LongPlaytest's
 * founding, its advisor and its rhythm, the autopilot at 2%), for as many
 * months as asked, writing every sector's balance sheet and the year's money
 * flows to a CSV a year.
 *
 * WHY. Jerus, 2026-11-02: "in the long run everyone inventivly ends up super
 * cash rich". Money cannot pile up everywhere at once unless it arrives from
 * somewhere: the world, the central bank, or a leak. MoneyAudit closes the
 * pools (the treasury, the sectors, the order book, the bank, the fund) and
 * leaves the households outside by design, so this also strikes the
 * households' own identity each month: what their savings moved by against
 * what the audit says crossed to them, what they moved abroad themselves, and
 * what migration carried in and out. What is left is money no flow paid for.
 *
 * Not game code: compiled beside target/classes by run.sh, never in the tree.
 * The city lives in GameFiles.scratch() (TempDirs), never a save slot.
 *
 *   java ... ham.citybuildersim.WealthRun SEED MONTHS OUT.csv
 */
public class WealthRun {

    static final PrintStream real = System.out;
    static final PrintStream quiet = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
        @Override public void write(byte[] b, int off, int len) { }
    });

    static int seed, endMonth;
    static boolean stuck;
    static long started;
    static PrintWriter csv;
    static boolean header;

    /* ---- the year's flows, summed month by month off the audit and the households ---- */
    static final String[] FLOWS = {
            "trade", "income", "financial", "valuation", "reserveSold", "moneyMade", "auditResidual",
            "toHouseholds", "fromHouseholds", "hhSent", "hhHome", "hhForeignInt", "hhTakenAway",
            "hhSavingsDelta", "hhResidual", "hhNewcomerEst", "gdp", "revenue", "expenses",
            "remittance", "cbInterest", "treasuryDelta", "depositInterest", "dividendsHh",
            "hhDisposable", "hhGrowth",
            // the trade in each direction, and GDP's own parts (NationalAccounts)
            "exports", "imports", "naC", "naI", "naG", "naNX",
            // the revenue's main lines, and the treasury's capital spending
            "taxBusiness", "taxIndustrial", "taxSales", "taxWage", "propertyTax", "capitalSpending",
            "utilityIncome", "landSales", "contributions", "fundTransfer", "portDues", "fuelDuty", "healthFees", "eiPremiums", "payroll"};
    static final Map<String, Double> year = new LinkedHashMap<>();
    static final Map<String, Double> life = new LinkedHashMap<>();

    static void add(String k, double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return;
        year.merge(k, v, Double::sum);
        life.merge(k, v, Double::sum);
    }

    /** Disposable income per household of each cell, as the last strike handed it (the newcomer's opening buffer is OPENING_BUFFER_MONTHS of it). */
    static double disposableTotal(HouseholdBalance hb) {
        double t = 0;
        for (int r = 0; r < HouseholdBalance.ROWS; r++) {
            double d = hb.getDisposable(r) * hb.getHouseholds(r);
            if (!Double.isNaN(d)) t += d;
        }
        return t;
    }

    static double households(HouseholdBalance hb) {
        double t = 0;
        for (int r = 0; r < HouseholdBalance.ROWS; r++) t += hb.getHouseholds(r);
        return t;
    }

    static void step(Game g) {
        HouseholdBalance hb = g.getHouseholdBalance();
        double savingsBefore = hb.totalSavings();
        double cashBefore = g.getCash();
        double hhBefore = households(hb);
        int before = g.getMonth();
        LongPlaytest.run(g, 1);
        if (g.getMonth() == before) { stuck = true; return; }

        MoneyAudit.Result a = g.getLastMoneyAudit();
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        add("trade", a.tradeBalance());
        add("income", a.incomeBalance());
        add("financial", a.financialAccount());
        add("valuation", a.valuationChange());
        add("reserveSold", a.reserveIn - a.reserveOut);
        add("moneyMade", a.moneyMade());
        add("auditResidual", a.residual);
        // what the pools paid the households, and what they paid the pools: the
        // audit's domestic lines less the central bank's (Scope.MONEY)
        double toHh = a.domesticOut() - a.moneyOut;
        double fromHh = a.domesticIn() - a.moneyIn;
        add("toHouseholds", toHh);
        add("fromHouseholds", fromHh);
        double sent = hb.getSentAbroad(), home = hb.getBroughtHome(), gone = hb.getTakenAway();
        add("hhSent", sent);
        add("hhHome", home);
        add("hhForeignInt", hb.getForeignInterest());
        add("hhTakenAway", gone);
        double dS = hb.totalSavings() - savingsBefore;
        add("hhSavingsDelta", dS);
        // What the savings moved by that no declared crossing explains. The
        // households' own foreign moves (sent abroad, brought home, the coupon
        // abroad) are declared by the audit as pairs through the pools, so they
        // are inside toHh and fromHh already; what left with the leavers is not.
        double residual = dS - (toHh - fromHh) + gone;
        add("hhResidual", residual);
        if (monthly != null) monthlyRow(g, hb, dS, toHh, fromHh, gone, residual, hhBefore);
        lastResidual = residual;
        lastDS = dS;
        if (hook != null) hook.accept(g);
        double hhAfter = households(hb);
        double disp = disposableTotal(hb);
        double perHh = hhAfter > 0 ? disp / hhAfter : 0;
        add("hhNewcomerEst", Math.max(0, hhAfter - hhBefore) * perHh * HouseholdBalance.OPENING_BUFFER_MONTHS);
        add("hhGrowth", hhAfter - hhBefore);
        add("hhDisposable", disp);
        add("gdp", na.getGdp());
        add("revenue", na.getTotalRevenue());
        add("expenses", na.getTotalExpenses());
        add("remittance", na.getCentralBankRemittance());
        add("cbInterest", na.getCentralBankInterest());
        add("treasuryDelta", g.getCash() - cashBefore);
        add("depositInterest", hb.getDepositInterest());
        add("dividendsHh", hb.totalDividends());
        add("exports", a.tradeIn);
        add("imports", a.tradeOut);
        add("naC", na.getConsumption());
        add("naI", na.getInvestment());
        add("naG", na.getGovernment());
        add("naNX", na.getNetExports());
        add("taxBusiness", na.getTaxBusiness());
        add("taxIndustrial", na.getTaxIndustrial());
        add("taxSales", na.getTaxSales());
        add("taxWage", na.getTaxWage());
        add("propertyTax", na.getPropertyTax());
        add("capitalSpending", na.getCapitalSpending());
        add("utilityIncome", na.getUtilityIncome());
        add("landSales", na.getLandSales());
        add("contributions", na.getContributions());
        add("fundTransfer", na.getFundTransfer());
        add("portDues", na.getPortDues());
        add("fuelDuty", na.getFuelDuty());
        add("healthFees", na.getHealthFees());
        add("eiPremiums", na.getEiPremiums());
        // every wage the businesses and the bank paid: the labour share's numerator
        double pay = g.getBank().getPayroll();
        for (Sector s : g.getSectors().all()) pay += s.statement().payroll;
        add("payroll", pay);

        if (g.getMonth() % 12 == 0) { revenueYear = year.getOrDefault("revenue", 0.0); row(g); }
        if (g.getMonth() % 600 == 0) {
            real.printf(Locale.ROOT, "  seed %d month %d pop %.0f (%.0fs)%n", seed, g.getMonth(),
                    (double) g.getEconomyManager().getPopulation(), (System.currentTimeMillis() - started) / 1000.0);
        }
    }

    /** The month's households' gap, and a hook a leak-hunting driver hangs on the end of each month. */
    static double lastResidual, lastDS;
    static java.util.function.Consumer<Game> hook;

    /* ---- -Dwealth.monthly=PATH: the households' gap and every household flow the game exposes, a row a month ---- */
    static PrintWriter monthly;
    static boolean monthlyHeader;

    static double rowSum(HouseholdBalance hb, java.util.function.IntToDoubleFunction per) {
        double t = 0;
        for (int r = 0; r < HouseholdBalance.ROWS; r++) {
            double v = per.applyAsDouble(r) * hb.getHouseholds(r);
            if (!Double.isNaN(v)) t += v;
        }
        return t;
    }

    static void monthlyRow(Game g, HouseholdBalance hb, double dS, double toHh, double fromHh, double gone,
                           double residual, double hhBefore) {
        LinkedHashMap<String, Double> c = new LinkedHashMap<>();
        c.put("month", (double) g.getMonth());
        c.put("residual", residual);
        c.put("dS", dS);
        c.put("toHh", toHh);
        c.put("fromHh", fromHh);
        c.put("gone", gone);
        c.put("savings", hb.totalSavings());
        c.put("hhGrowth", households(hb) - hhBefore);
        c.put("disposable", disposableTotal(hb));
        c.put("banked", rowSum(hb, hb::getBanked));
        c.put("drawn", rowSum(hb, hb::getDrawn));
        c.put("borrowedRow", rowSum(hb, hb::getBorrowed));
        c.put("repaidRow", rowSum(hb, hb::getRepaid));
        c.put("fixed", rowSum(hb, hb::getFixedCosts));
        c.put("dividends", hb.totalDividends());
        c.put("depositInterest", hb.getDepositInterest());
        c.put("foreignInterest", hb.getForeignInterest());
        c.put("sent", hb.getSentAbroad());
        c.put("home", hb.getBroughtHome());
        c.put("sharesSold", hb.totalSold());
        c.put("paperSold", hb.totalPaperSold());
        c.put("paperIncome", hb.totalPaperIncome());
        c.put("bondsSold", hb.totalBondsSold());
        c.put("bondIncome", hb.totalBondIncome());
        c.put("foodAid", hb.getFoodAssistancePaid());
        c.put("studentBorrowed", hb.totalStudentBorrowed());
        c.put("studentRepaid", hb.totalStudentRepaid());
        c.put("studentInterest", hb.totalStudentInterest());
        c.put("writtenOff", hb.getWrittenOff());
        c.put("borrowed", hb.totalBorrowed());
        c.put("repaid", hb.totalRepaid());
        c.put("interest", hb.totalInterest());
        c.put("mealSpend", hb.getMealSpend());
        c.put("luxurySpend", hb.getLuxurySpend());
        c.put("usedCarSpend", hb.getUsedCarSpend());
        c.put("careSkipped", hb.getCareSkipped());
        c.put("accountFees", hb.totalAccountFees());
        c.put("abroadTakenAway", hb.getAbroadTakenAway());
        // the households' share trades with the world and with the emigrants,
        // which move savings and no pool (the exchange counts them; the audit has no line)
        c.put("exHhBoughtAbroad", g.getExchange().getHouseholdsBoughtAbroad());
        c.put("exHhSoldAbroad", g.getExchange().getHouseholdsSoldAbroad());
        c.put("exEmigrantsPaid", g.getExchange().getEmigrantsPaid());
        c.put("pop", (double) g.getEconomyManager().getPopulation());
        if (!monthlyHeader) {
            monthly.println(String.join(",", c.keySet()));
            monthlyHeader = true;
        }
        StringBuilder b = new StringBuilder();
        for (double v : c.values()) {
            if (b.length() > 0) b.append(',');
            if (!Double.isNaN(v)) b.append(String.format(Locale.ROOT, "%.12g", v));
        }
        monthly.println(b);
    }

    static void row(Game g) {
        LinkedHashMap<String, Double> c = new LinkedHashMap<>();
        c.put("month", (double) g.getMonth());
        c.put("pop", (double) g.getEconomyManager().getPopulation());
        c.put("index", g.getPriceIndex().getIndex());
        c.put("fx", g.getForeignAccounts().getRate());
        c.put("parity", g.getForeignAccounts().getParity());
        c.put("worldLevel", g.getWorldEconomy().getPriceLevel());
        c.put("policyRate", g.getDebtManager().getPolicyRate());
        c.put("unemployment", g.getPopulationManager().getUnemploymentRate());
        c.put("realGdp", g.realGdp2015Usd());
        c.put("profitTax", g.getEconomyManager().getTaxPolicy().getProfitTaxRate());
        c.put("pensionRepl", g.getEconomyManager().getTaxPolicy().getPensionReplacement());
        // the government
        c.put("treasuryCash", g.getCash());
        c.put("fundCash", g.getFund().getCash());
        c.put("vault", g.getForeignAccounts().getReserves());
        c.put("cityDebt", g.getDebtManager().getAllPrincipal());
        c.put("cityDebtHh", g.getDebtManager().householdPrincipal());
        c.put("cityDebtCb", g.getDebtManager().centralBankPrincipal());
        c.put("cityDebtWorld", g.getDebtManager().worldPrincipal());
        c.put("cityDebtBank", g.getDebtManager().bankBook());
        c.put("cityDebtForeign", g.getDebtManager().getForeignPrincipal());
        c.put("studentLoans", g.getHouseholdBalance().totalStudentDebt());
        // the central bank
        c.put("m0", g.getCentralBank().m0());
        c.put("cbAdvBank", g.getCentralBank().getAdvancesToBank());
        c.put("cbAdvTreasury", g.getCentralBank().getAdvancesToTreasury());
        c.put("cbPaper", g.getCentralBank().getPaperHeld());
        // the businesses
        double cash = 0, top = 0;
        String topKey = "";
        for (Sector s : g.getSectors().all()) {
            cash += s.getCash();
            if (s.getCash() > top) { top = s.getCash(); topKey = s.getClass().getSimpleName(); }
        }
        c.put("bizCash", cash);
        c.put("bizTopCash", top);
        c.put("orderBook", g.getSectors().construction().getOrderBookForAudit());
        c.put("bizAbroad", g.getOutwardInvestment().totalLocalValue());
        c.put("bizPaper", g.getOutwardInvestment().totalPaper());
        c.put("bizLoans", g.getEconomyManager().getBusinessDebtManager().getAllPrincipal());
        c.put("bizBonds", g.getBondMarket().totalFace());
        // the bank
        Bank bk = g.getBank();
        c.put("bankCash", bk.getCash());
        c.put("bankEquity", bk.equity());
        c.put("bankBook", bk.getBook());
        c.put("bankForeignDeposits", bk.getForeignDeposits());
        // the households
        HouseholdBalance hb = g.getHouseholdBalance();
        c.put("hhSavings", hb.totalSavings());
        c.put("hhDebt", hb.totalDebt());
        c.put("hhPaper", hb.totalPaper());
        c.put("hhBonds", hb.totalBonds());
        c.put("hhAbroad", hb.totalAbroadValue());
        double shares = Double.NaN;
        try { shares = g.getExchange().marketValueOfHouseholds(g.getEquity(), hb); } catch (RuntimeException e) { }
        c.put("hhShares", shares);
        c.put("households", households(hb));
        // who owns the companies, weighted by each one's fair value: the city's fund, the world, the households
        double vCity = 0, vWorld = 0, vHome = 0;
        Equity eq = g.getEquity();
        for (int k = 0; k < Equity.COMPANIES.length; k++) {
            double n = eq.getShares(k), px = g.getExchange().fair(k);
            if (!(n > 0) || !(px > 0)) continue;
            vCity += eq.getCityShares(k) * px; vWorld += eq.getForeignShares(k) * px; vHome += eq.getDomesticShares(k) * px;
        }
        c.put("eqCity", vCity); c.put("eqWorld", vWorld); c.put("eqHouseholds", vHome);
        double vRescue = 0;
        for (int k = 0; k < Equity.COMPANIES.length; k++) {
            double px = g.getExchange().fair(k);
            if (px > 0) vRescue += eq.getCityRescueShares(k) * px;
        }
        c.put("eqCityRescue", vRescue);
        // the world
        c.put("netForeign", g.getForeignAccounts().netForeignPosition());
        c.put("m2", g.getM2());
        for (String k : FLOWS) c.put("y_" + k, year.getOrDefault(k, 0.0));
        for (String k : FLOWS) c.put("l_" + k, life.getOrDefault(k, 0.0));
        year.clear();
        if (!header) {
            csv.println(String.join(",", c.keySet()) + ",topSector");
            header = true;
        }
        StringBuilder b = new StringBuilder();
        for (double v : c.values()) {
            if (b.length() > 0) b.append(',');
            if (!Double.isNaN(v)) b.append(String.format(Locale.ROOT, "%.9g", v));
        }
        b.append(',').append(topKey);
        csv.println(b);
        csv.flush();
    }

    /* =====================================================================
       -Dwealth.rule=on: THE PROTOTYPE PLAYER WHO READS THE GOVERNMENT'S
       WEALTH (spec-wealth.md section 3.2). Each January it reads the
       treasury's and the fund's cash against a year's revenue and moves the
       player's own levers: past RULE_RICH years it cuts the profit tax by
       five points and the sales tax by two (to nought at most), raises the
       pension's replacement by five points (to 100% at most) and sets the
       fund's withdrawal to its most, so the fund's idle cash reaches the
       budget; under RULE_BROKE it moves each back one step. The scripted
       tax steps of the playtest are left out while it plays (they would
       fight it). No code of the game's changes: these are the setters the
       Policy and Finances screens call.
       ===================================================================== */
    static final boolean RULE = "on".equals(System.getProperty("wealth.rule"));
    static final double RULE_RICH = Double.parseDouble(System.getProperty("wealth.rich", "2"));
    static final double RULE_BROKE = Double.parseDouble(System.getProperty("wealth.broke", "0.5"));
    static double revenueYear;

    static void rule(Game g) {
        if (!RULE || g.getMonth() % 12 != 0 || !(revenueYear > 0)) return;
        TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
        double years = (g.getCash() + g.getFund().getCash()) / revenueYear;
        if (years > RULE_RICH) {
            tax.setProfitTaxRate(Math.max(0, tax.getProfitTaxRate() - .05));
            tax.setSalesTaxRate(Math.max(0, tax.getSalesTaxRate() - .02));
            tax.setPensionReplacement(Math.min(1.0, tax.getPensionReplacement() + .05));
            g.getFund().setWithdrawal(TreasuryFund.MAX_WITHDRAWAL_STEPS * TreasuryFund.WITHDRAWAL_STEP);
        } else if (years < RULE_BROKE) {
            tax.setPensionReplacement(Math.max(.3, tax.getPensionReplacement() - .05));
            tax.setSalesTaxRate(Math.min(.15, tax.getSalesTaxRate() + .02));
            tax.setProfitTaxRate(Math.min(.30, tax.getProfitTaxRate() + .05));
            g.getFund().setWithdrawal(TreasuryFund.WITHDRAWAL_STEP * TreasuryFund.DEFAULT_WITHDRAWAL_STEPS);
        }
    }

    static void run(Game g, int months) {
        for (int i = 0; i < months && !stuck && g.getMonth() < endMonth; i++) {
            step(g);
            rule(g);
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);
        seed = Integer.parseInt(args[0]);
        endMonth = Integer.parseInt(args[1]);
        System.setProperty("playtest.seed", Integer.toString(seed));
        try {
            Field f = LongPlaytest.class.getDeclaredField("out");
            f.setAccessible(true);
            f.set(null, quiet);
        } catch (ReflectiveOperationException | RuntimeException e) { }
        String monthlyPath = System.getProperty("wealth.monthly");
        if (monthlyPath != null) monthly = new PrintWriter(Files.newBufferedWriter(Paths.get(monthlyPath)), true);
        GameFiles files = GameFiles.scratch("wealth");
        started = System.currentTimeMillis();
        Game g;
        System.setOut(quiet);
        try {
            g = new Game(files, LongPlaytest.founding());
            g.run();
        } finally {
            System.setOut(real);
        }
        String threw = null;
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(Paths.get(args[2])))) {
            csv = w;
            try {
                foundAndPlay(g);
            } catch (Throwable t) {
                threw = t.toString();
                System.setOut(real);
                t.printStackTrace(real);
            }
        }
        real.printf(Locale.ROOT, "DONE seed %d months %d of %d in %.1fs%s%n", seed, g.getMonth(), endMonth,
                (System.currentTimeMillis() - started) / 1000.0, threw == null ? (stuck ? " (stuck)" : "") : " THREW " + threw);
    }

    /** Ensemble.foundAndPlay's AUTO2, line for line (itself LongPlaytest.main's). */
    static void foundAndPlay(Game g) {
        System.setOut(quiet);
        try {
            g.getDebtManager().setInflationTarget(.02);
            g.getDebtManager().setAutopilot(true);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.setFundDial(LongPlaytest.FUND_DIAL);
            LongPlaytest.seedMarket(g, seed);
            if (LongPlaytest.FOUNDING == Founding.Preset.INSANE) LongPlaytest.borrowForTheVillage(g);
            LongPlaytest.villageBuild(g, "House", 40 + seed % 4);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            run(g, 3 + (seed / 4) % 3);
            LongPlaytest.villageBuild(g, "House", 20 + (seed / 12) % 3);
            run(g, 4);
            LongPlaytest.villageBuild(g, "Convenience Store", 2);
            LongPlaytest.villageBuild(g, "Construction Depot", 1);
            run(g, 5);
            LongPlaytest.advise(g);
            run(g, 6);
            LongPlaytest.ensureSchools(g);
            int stop = 0;
            while (g.getMonth() < endMonth && !stuck) {
                stop++;
                int skip = switch (stop % 6) {
                    case 0 -> 100;
                    case 1 -> 12;
                    case 2 -> 24;
                    case 3 -> 60;
                    case 4 -> 6;
                    default -> 120;
                };
                skip = Math.min(skip, LongPlaytest.longestSkip());
                run(g, skip);
                if (g.getMonth() >= endMonth || stuck) break;
                LongPlaytest.ensureSchools(g);
                for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                    if (LongPlaytest.advise(g) == null) break;
                    run(g, 1);
                }
                run(g, 2);
                if (stop % 11 == 0 && !RULE) {
                    TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
                    double income = tax.getIncomeTaxRate();
                    tax.setIncomeTaxRate(income > .25 ? .12 : income + .06);
                }
                if (stop % 17 == 0 && !RULE) {
                    TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
                    double prop = tax.getPropertyTaxRate();
                    tax.setPropertyTaxRate(prop > .03 ? .01 : prop + .01);
                }
                if (stop % 19 == 0) {
                    DebtManager m = g.getDebtManager();
                    if (m.foreignWindowOpen() && m.foreignRate() < m.getRate()) {
                        double ask = Math.max(5_000, g.getEconomyManager().getMonthGdp() * .5
                                / Math.max(.01, LongPlaytest.fxRate(g)));
                        if (LongPlaytest.BORROW_AT_HOME) {
                            g.handleLongBondLogic(ask * Math.max(.01, LongPlaytest.fxRate(g)), 20, 100);
                        } else {
                            g.handleForeignLogic("Term", ask, 20, 100, false);
                        }
                    }
                }
                if (stop % 13 == 0) {
                    LandParcel spare = g.getLandManager().getMarket().bestValue();
                    if (spare != null && spare.localPrice(g.getForeignAccounts().getRate()) < g.getCash() * .1) {
                        g.buyLandParcel(spare.getId());
                    }
                }
                if (stop % 19 == 0) {
                    BuildingsTemplate house = LongPlaytest.template(g, "House");
                    if (house != null && g.getBuildingManager().getQuantity(house.getId()) > 60) {
                        g.getBuildingManager().retire(house, 10);
                    }
                }
            }
        } finally {
            System.setOut(real);
        }
    }
}
