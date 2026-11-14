package ham.citybuildersim;

import ham.citybuildersim.sectors.WoodProducts;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * The hands-off bank's failures, caused separately and counted across Random cities.
 * WHY. BF1 follows LD2's sawmill loans: a warehouse with no buyers, an empty
 * borrower still owing its plant, growth credit after losses, and an apparent
 * disappearance of shares which must be distinguished from a consolidation.
 */
public final class BankFailureCheck {
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());
    static int fails;

    static void check(String label, boolean ok) {
        out.println(label + (ok ? " OK" : " FAIL"));
        if (!ok) fails++;
    }

    static void close(String label, double a, double b) {
        check(label, Math.abs(a - b) <= 1e-8 * Math.max(1, Math.max(Math.abs(a), Math.abs(b))));
    }

    static Game town(String label) {
        Game g = new Game(GameFiles.scratch(label));
        g.newGame(Founding.defaults().withPath(Nations.Path.RANDOM, 4));
        g.setRescueMode(TreasuryFund.RescueMode.BUTTON);
        return g;
    }

    static void mill() {
        out.println("--- 1. a lightly staffed mill exports to its buyers instead of filling an unwanted shed ---");
        Game g = town("bankfailure-mill");
        BuildingManager buildings = g.getBuildingManager();
        Markets markets = g.getMarkets();
        Sectors sectors = g.getSectors();
        WoodProducts wood = sectors.woodProducts();
        BuildingsTemplate mill = buildings.getTemplate(WoodProducts.PELLET_MILL);
        buildings.addStack(mill, 1, true);
        double[] fill = new double[11];
        Arrays.fill(fill, .2);
        wood.updateJobFillRate(fill);
        wood.updateWages(g.getPopulationManager().getWagesPerType(), wood.postsPerTier());
        check("fixture: no city demand, export price pays the inputs, and output fits below the dump line",
                markets.get(Good.WOOD_PELLETS).getDemand() == 0
                && wood.getOperatingRate() > 0 && wood.getOperatingRate() < Sector.DUMP_THRESHOLD
                && wood.getCapacity(Good.WOOD_PELLETS) * wood.getOperatingRate()
                        < wood.getStockCapacity(Good.WOOD_PELLETS) * Sector.DUMP_THRESHOLD
                && markets.get(Good.WOOD_PELLETS).netExportPrice() > wood.getMarginalCostPerUnit(Good.WOOD_PELLETS));
        markets.clearMonth(sectors, null);
        double opening = wood.getStock(Good.WOOD_PELLETS);
        check("fixture: the mill made a positive first month's stock below the dump line", opening > 0
                && opening < wood.getStockCapacity(Good.WOOD_PELLETS) * Sector.DUMP_THRESHOLD
                && wood.getPayroll() > 0 && wood.pending().imports > 0);
        markets.clearMonth(sectors, null);
        check("the pellet mill sells its stock in the following month", wood.output(Good.WOOD_PELLETS).exported > 0);
        close("with no local buyers the world buys the opening stock", wood.output(Good.WOOD_PELLETS).exported, opening);
        close("the market counts the mill's shipment", wood.output(Good.WOOD_PELLETS).exported,
                markets.get(Good.WOOD_PELLETS).getExported());
        close("the seller books the world's invoice", wood.pending().exports,
                opening * markets.get(Good.WOOD_PELLETS).exportPrice());
    }

    static void closing() throws Exception {
        out.println("--- 2. the last mill closes through the borrower's debt books ---");
        Game g = town("bankfailure-closing");
        BuildingManager buildings = g.getBuildingManager();
        BuildingsTemplate mill = buildings.getTemplate(WoodProducts.PELLET_MILL);
        WoodProducts wood = g.getSectors().woodProducts();
        buildings.addStack(mill, 1, true);
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        double principal = buildings.getBuildingsValueBySector(wood.key());
        credit.issueProjectLoan(wood.key(), principal, g.getMonth());
        wood.setCash(0);
        g.getEconomyManager().pushBalanceSheetInputs();
        Method investor = Game.class.getDeclaredMethod("sectorInvestor", String.class);
        investor.setAccessible(true);
        Method retire = Game.class.getDeclaredMethod("retire", BusinessInvestment.Decision.class, Investor.class, boolean.class);
        retire.setAccessible(true);
        int gone = (int) retire.invoke(g, new BusinessInvestment.Decision(wood.key(), mill, 1, "BF1 fixture", true),
                investor.invoke(g, wood.key()), true);
        g.getEconomyManager().settleInsolvency();
        check("fixture: the only mill was retired and its creditor suffered a loss", gone == 1
                && buildings.getBuildingsValueBySector(wood.key()) == 0 && credit.getWrittenOffThisMonth(wood.key()) > 0);
        close("cash from closing repays the bank before its remaining claim is resolved", credit.getPrincipal(wood.key()), 0);
        check("the empty defaulter serves the model's borrowing exclusion", credit.getBlockedMonths(wood.key())
                >= BusinessDebtManager.exclusionFor(1));
        check("it cannot borrow for a replacement mill", !credit.canFundProject(wood.key(), principal));

        BusinessDebtManager carrying = new BusinessDebtManager();
        carrying.setLoans(new java.util.ArrayList<>(java.util.List.of(
                new InterimLoan(wood.key(), 40, BusinessDebtManager.LOAN_TERM_MONTHS, 1, .04),
                new BusinessLoan(wood.key(), 100, BusinessDebtManager.LOAN_TERM_MONTHS, 1, .04))));
        close("partial retirement pays the interim claim before ordinary loans", carrying.settleRetirement(wood.key(), 60, false), 60);
        close("the senior interim claim was paid whole", carrying.getInterimPrincipal(wood.key()), 0);
        close("the remaining plant carries the unpaid ordinary claim", carrying.getPrincipal(wood.key()), 80);
        close("repayment reaches the bank's cash settlement", carrying.getRepaidThisMonth(wood.key()), 60);
        close("repayment is recorded separately from creditor losses", carrying.getWrittenOffThisMonth(wood.key()), 0);
        check("a partial retirement alone does not ban a continuing borrower", !carrying.isBorrowingBlocked(wood.key()));
    }

    static void credit() throws Exception {
        out.println("--- 3. a new project cannot spend capital the bank does not have ---");
        Game g = town("bankfailure-capital");
        g.simulateMonths(2);
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        String sector = Sectors.WOOD_PRODUCTS;
        credit.setAssets(sector, g.getBank().equity());
        credit.setCash(sector, 0);
        Method investor = Game.class.getDeclaredMethod("sectorInvestor", String.class);
        investor.setAccessible(true);
        Investor borrower = (Investor) investor.invoke(g, sector);
        double request = Math.max(g.getBank().headroom(), g.getBank().equity()) / Bank.CAPITAL_RATIO;
        check("fixture: the bank stands above target and the requested project exceeds its capital headroom",
                !g.getBank().isInsolvent() && Double.isInfinite(credit.getCapitalGrowth())
                && request > g.getBank().headroom());
        check("new project credit is refused at the bank's own capital limit", !borrower.canBorrow(request));

        double room = g.getBank().projectHeadroom();
        BusinessDebtManager shared = new BusinessDebtManager();
        shared.setProjectBudget(room);
        shared.setAssets(Sectors.WOOD_PRODUCTS, request);
        shared.setAssets(Sectors.MANUFACTURING, request);
        check("fixture: an affordable project is still lent", room > 0
                && shared.canFundProject(Sectors.WOOD_PRODUCTS, room * .75));
        shared.issueProjectLoan(Sectors.WOOD_PRODUCTS, room * .75, 1);
        close("one sector's commitment consumes the shared room", shared.projectRoom(), room * .25);
        check("another sector cannot spend the same capital again", !shared.canFundProject(Sectors.MANUFACTURING, room * .5));
        g.getBank().writeOffSector(sector, g.getBank().equity() + g.getBank().getSectorAllowance(sector));
        close("uncovered defaults reach the growth desk before the bank's month-end settle", g.getBank().projectHeadroom(), 0);
    }

    static void shares() {
        out.println("--- 4. a consolidation changes the unit, preserving every holder's value ---");
        for (String key : new String[] { Sectors.WOOD_PRODUCTS, Sectors.MANUFACTURING }) {
            int c = Equity.indexOf(key);
            Equity register = new Equity();
            register.offer(c, 400, 0, null, .02);
            double issued = register.getShares(c);
            register.moveForeign(c, -issued * .35);
            register.moveDesk(c, issued * .35);
            Exchange exchange = new Exchange();
            ExchangeCheck.Firms firms = new ExchangeCheck.Firms();
            double quote = register.foundingPrice();
            firms.wage = quote / (Exchange.CONSOLIDATE_BELOW / 100);
            exchange.attach(register, null, null, firms, 1);
            exchange.bookOf(c).seedLastPrice(quote);
            double world = register.getForeignShares(c) * exchange.price(c);
            double desk = register.getDealerShares(c) * exchange.mark(c);
            double paidIn = register.getPaidIn(c);
            exchange.splitForCheck();
            double k = exchange.getSplit(c);
            check(key + ": fixture actually consolidates under SX1's wage dial", k > 0 && k < 1);
            close(key + ": shares in founding units are conserved", register.getShares(c) / k, issued);
            close(key + ": the desk's value is conserved", register.getDealerShares(c) * exchange.mark(c), desk);
            close(key + ": the world's value is conserved", register.getForeignShares(c) * exchange.price(c), world);
            close(key + ": the register remains whole", register.getForeignShares(c) + register.getDealerShares(c), register.getShares(c));
            close(key + ": paid-in capital is money, not a share count", register.getPaidIn(c), paidIn);
            close(key + ": nothing was bought back", register.getBoughtBackThisMonth(c), 0);
        }
    }

    static void seeds() {
        out.println("--- 5. 24 default Random cities, button unpressed, every one of 120 months audited ---");
        int failed = 0, audited = 0, badAudits = 0, badEquity = 0, selling = 0, splits = 0;
        for (int seed = 0; seed < 24; seed++) {
            Game g = new Game(GameFiles.scratch("bankfailure-seed-" + seed));
            g.newGame(Founding.defaults().withPath(Nations.Path.RANDOM, seed));
            g.setRescueMode(TreasuryFund.RescueMode.BUTTON);
            int failedAt = -1;
            for (int m = 0; m < 120; m++) {
                g.toggleNextMonth();
                MoneyAudit.Result a = g.getLastMoneyAudit();
                audited++;
                if (a == null || !Double.isFinite(a.residual) || Math.abs(a.residual) > MoneyAudit.tolerance(a.moved())) {
                    badAudits++;
                    out.printf("audit seed %d month %d: %s%n", seed, g.getMonth(), a);
                }
                if (g.getBank().isInResolution() && failedAt < 0) failedAt = g.getMonth();
                Bank.EquityMovement movement = g.getBank().equityMovement();
                if (g.getBank().isMonthKnown() && Math.abs(movement.residual()) > MoneyAudit.tolerance(movement.scale())) {
                    badEquity++;
                    out.printf("equity seed %d month %d residual %.8g%n", seed, g.getMonth(), movement.residual());
                }
                if (g.getSectors().woodProducts().output(Good.WOOD_PELLETS).exported > 0) selling++;
                for (int c = 0; c < Equity.COMPANIES.length; c++) if (g.getExchange().getSplit(c) > 0) splits++;
            }
            if (failedAt >= 0) failed++;
            out.printf("seed %d failed %d equity %.3f%n", seed, failedAt, g.getBank().equity());
        }
        out.printf("BANK FAILURES %d/24; audits %d; pellet sale months %d; splits/consolidations %d%n", failed, audited, selling, splits);
        check("MoneyAudit closes in every month of every seed at its own tolerance", audited == 24 * 120 && badAudits == 0);
        // Failure frequency is the observation BF1 asks for, not a promise
        // that a bank taking genuine credit losses can never fail.
        check("every bank's equity movement is explained by its books, including failures", badEquity == 0);
        check("pellet mills actually sell in played cities", selling > 0);
    }

    public static void main(String[] args) throws Exception {
        System.setOut(quiet);
        try {
            if (!Arrays.asList(args).contains("--count")) { mill(); closing(); credit(); shares(); }
            if (!Arrays.asList(args).contains("--fixtures")) seeds();
        } finally { System.setOut(out); }
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
