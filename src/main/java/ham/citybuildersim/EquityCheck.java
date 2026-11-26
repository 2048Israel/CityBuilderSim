package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

/**
 * Verifies the share register: who buys, at what price, what they are paid,
 * and that a month with owners in it still adds up.
 *
 * Every claim is CAUSED. A register is handed a record and asked what it
 * would do; households are given savings and offered shares; a company is
 * given a month's income and its owners are paid. The one live city at the
 * end is there to show the mechanism runs inside the audited month, not to
 * hope that somebody happens to buy something.
 *
 * The live city (FS14): the default founding in a TempDirs folder, played
 * until Retail has a book (bounded, 36 months) and 240 months more. It
 * asserts only what holds on any path - every share held by somebody, the
 * audit every month, the register agreeing with the cells - and counts the
 * offerings and the months a dividend was paid, which are its path's.
 */
public class EquityCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-60s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-60s FAIL  %,.6f != %,.6f%n", label, actual, expected);
        } else {
            out.printf("%-60s OK%n", label);
        }
    }

    /** A register with twelve months of this income on one company's record. */
    static Equity withRecord(int company, double... monthlyIncome) {
        Equity e = new Equity();
        for (double v : monthlyIncome) e.recordMonth(company, v, 0);
        return e;
    }

    /** Debt-free savers at the offering boundary, before a new card/shock month. */
    static HouseholdBalance savers(double savedEach, double takeHome) {
        HouseholdBalance hb = new HouseholdBalance();
        hb.censusOnly((shape, tier) -> shape == FamilyStructure.COUPLE && tier == PayTier.UNSKILLED ? 100 : 0);
        Household c = hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
        c.disposable = takeHome; c.savings = savedEach;
        return hb;
    }

    /** Two native offerings leave a large quote before the company has an earnings record. */
    static Equity staleOffering(int company, double quote) {
        Equity register = new Equity();
        register.offer(company, register.foundingPrice(), 0, null, DebtManager.WORLD_BASE_RATE);
        register.offer(company, quote, 0, null, DebtManager.WORLD_BASE_RATE, quote);
        return register;
    }

    /* ================= 10. mature value does not revive an offering quote (CBK3) ================= */
    static void maturePricesFollowTheirRecord() {
        out.println("\n--- 10. mature shares follow their actual record, not a stale offering quote ---");
        int company = Equity.indexOf(Sectors.INDUSTRY);
        double worldRate = DebtManager.WORLD_BASE_RATE, required = Equity.requiredReturn(worldRate);
        double quote = 100 * Equity.FOUNDING_PRICE, floor = Exchange.MIN_FAIR;
        String[] cases = { "negative income", "zero income", "positive income below the floor", "tiny positive income" };
        Equity negative = null, zero = null, tiny = null;
        boolean untouched = true;
        for (int k = 0; k < cases.length; k++) {
            Equity register = staleOffering(company, quote);
            double shares = register.getShares(company), abroad = register.getForeignShares(company);
            double raised = register.getLifetimeRaisedAbroad(company);
            double annual = switch (k) {
                case 0 -> -quote;
                case 1 -> 0;
                case 2 -> floor * shares * required / 2;
                default -> 4 * floor * shares * required;
            };
            for (int m = 0; m < Equity.RECORD_MONTHS; m++) register.recordMonth(company, annual / Equity.RECORD_MONTHS, 0);
            assertTrue("fixture: " + cases[k] + " has a mature record and the large offering quote",
                    register.valuedOnRecord(company) && register.getLastPrice(company) == quote && quote > 1_000 * floor);
            double income = register.getTrailingIncome(company);
            close(cases[k] + ": the record keeps the actual income", income, annual, Math.abs(annual) * 1e-12);
            double price = register.fairValue(company, 2 * quote * shares, 0, worldRate);
            close(cases[k] + ": no spare money cannot resurrect the offering quote",
                    price, k == 3 ? 4 * floor : floor, floor * 1e-9);
            untouched &= register.getTrailingIncome(company) == income && register.getShares(company) == shares
                    && register.getForeignShares(company) == abroad && register.getDealerShares(company) == 0
                    && register.getLastPrice(company) == quote && register.getLifetimeRaisedAbroad(company) == raised;
            if (k == 0) negative = register;
            if (k == 1) zero = register;
            if (k == 3) tiny = register;
        }
        assertTrue("valuation changes no actual income, share ownership, offering quote or capital raised", untouched);

        double shares = tiny.getShares(company), spare = shares * Equity.FOUNDING_PRICE;
        close("positive spare remains money at face beside the tiny earnings value",
                tiny.fairValue(company, 0, spare, worldRate), Equity.FOUNDING_PRICE + 4 * floor, 1e-9);
        close("positive spare is still valued when the earnings record is zero",
                zero.fairValue(company, 0, spare, worldRate), spare / zero.getShares(company), 1e-9);
        close("...and when the actual earnings record is negative",
                negative.fairValue(company, 0, spare, worldRate), spare / negative.getShares(company), 1e-9);

        Equity young = staleOffering(company, quote);
        for (int m = 0; m < Equity.RECORD_MONTHS - 1; m++) young.recordMonth(company, -quote, 0);
        double youngShares = young.getShares(company), prospectsBook = 3 * quote * youngShares;
        assertTrue("fixture: the loss-making young company has less than a full record", !young.valuedOnRecord(company));
        close("before a full record its prospects still use book per share",
                young.fairValue(company, prospectsBook, 0, worldRate), prospectsBook / youngShares, 1e-9);
        close("before a full record no book or paid dividends still uses the old offering quote",
                young.fairValue(company, 0, 0, worldRate), quote, 1e-9);

        // The desk bought part of the native offering earlier: both the cash payment and its old mark are booked.
        double issued = zero.getShares(company), held = issued / 2, paid = held * quote;
        zero.moveForeign(company, -held);
        zero.moveDesk(company, held);
        Bank bank = new Bank();
        bank.refresh(1, 0, 0, 0, 0, 0);
        bank.injectCapital(4 * paid);
        bank.deskPays(paid);
        bank.markSecurities(paid);
        bank.startMonth();
        double cash = bank.getCash(), equity = bank.equity();
        double[] books = new double[Equity.COMPANIES.length];
        books[company] = 2 * quote * zero.getShares(company);
        Exchange exchange = new Exchange();
        exchange.takeMonth(zero, null, bank, null, books, worldRate, 0, Equity.RECORD_MONTHS);
        close("the exchange carries the mature zero-income holding at the monetary floor",
                bank.getSecurities(), held * floor, floor * held * 1e-9);
        assertTrue("the stale holding actually marks down", bank.getSecurities() < paid);
        close("the whole mark-down is recorded as the desk's mark change", bank.getMarkChange(), bank.getSecurities() - paid, 1e-9);
        close("the whole mark-down reaches trading income", bank.getTradingIncome(), bank.getMarkChange(), 1e-9);
        close("equity falls by that trading loss, with nothing suppressed", bank.equity() - equity, bank.getTradingIncome(), 1e-9);
        assertTrue("the bank's native equity-movement identity reconciles the mark-down", bank.equityMovement().addsUp());
        close("a mark-down moves no cash", bank.getCash(), cash, 0);
        close("...and no desk shares", zero.getDealerShares(company), held, 0);
        close("...and no shares in issue", zero.getShares(company), issued, 0);
        close("the remaining owners and the desk still hold every issued share",
                zero.getForeignShares(company) + zero.getDealerShares(company), zero.getShares(company), 1e-9);

        double value = shares * tiny.fairValue(company, 0, spare, worldRate), income = tiny.getTrailingIncome(company);
        double consolidation = .01;
        tiny.split(company, consolidation);
        close("a consolidation changes the share count by its factor", tiny.getShares(company), shares * consolidation, 0);
        close("...and preserves total value, including the spare money",
                tiny.getShares(company) * tiny.fairValue(company, 0, spare, worldRate), value, 1e-9);
        close("...without changing the actual income record", tiny.getTrailingIncome(company), income, 0);
        double scale = .01, consolidatedShares = tiny.getShares(company);
        tiny.redenominate(scale);
        close("a currency reform scales total value in the same unit as the spare and income",
                tiny.getShares(company) * tiny.fairValue(company, 0, spare * scale, worldRate), value * scale, 1e-9);
        close("...and scales the actual income once", tiny.getTrailingIncome(company), income * scale, Math.abs(income * scale) * 1e-9);
        close("...without changing the number of shares", tiny.getShares(company), consolidatedShares, 0);
        zero.redenominate(scale);
        close("the mature zero-value floor follows the currency reform too",
                zero.fairValue(company, 0, 0, worldRate), floor * scale, floor * scale * 1e-9);
    }

    /** No buyback money, but a paid wage makes the exchange's automatic split rule active. */
    static Exchange.Companies wageOnlyCompanies(double wage) {
        return new Exchange.Companies() {
            public double averageMonthlyWage() { return wage; }
            public double cashAvailable(int company, double wanted) { return 0; }
            public double till(int company) { return 0; }
            public void payBuyback(int company, double cash) { }
            public double assets(int company) { return 0; }
            public double equity(int company) { return 0; }
            public double monthlyOperatingCost(int company) { return 0; }
            public double monthlyDebtService(int company) { return 0; }
        };
    }

    static boolean finiteListing(Equity register, Exchange market, int company) {
        return register.getShares(company) > 0 && Double.isFinite(register.getShares(company))
                && Double.isFinite(register.getForeignShares(company)) && Double.isFinite(register.getDealerShares(company))
                && Double.isFinite(register.getLastPrice(company)) && Double.isFinite(market.price(company))
                && Double.isFinite(market.fair(company)) && market.getSplitFactor(company) > 0
                && Double.isFinite(market.getSplitFactor(company));
    }

    /* ================= 11. the anchored floor cannot cause a consolidation loop (CBK3) ================= */
    static void anchoredFloorIsNotAConsolidation() {
        out.println("\n--- 11. the anchored presentation floor is not a reason to consolidate a worthless company ---");
        int company = Equity.indexOf(Sectors.INDUSTRY);
        double worldRate = DebtManager.WORLD_BASE_RATE;
        String[] cases = { "zero income", "negative income", "positive income below the floor" };
        for (double denomination : new double[] { 1, 100 }) {
            double expectedLevel = 2, wage = Equity.FOUNDING_PRICE / denomination;
            Exchange.Companies companies = wageOnlyCompanies(wage);
            for (int k = 0; k < cases.length; k++) {
                String label = cases[k] + " at denomination " + denomination;
                Equity register = new Equity();
                register.seedConstants(denomination);
                register.offer(company, register.foundingPrice(), 0, null, worldRate);
                double issued = register.getShares(company), abroad = register.getForeignShares(company);
                // The real monthly anchor moves the register's yardstick, while the venue keeps nominal money.
                register.seedConstants(denomination / expectedLevel);
                double floor = register.noValue();
                double annual = k == 0 ? 0 : k == 1 ? -register.foundingPrice()
                        : floor * issued * Equity.requiredReturn(worldRate) / 2;
                for (int m = 0; m < Equity.RECORD_MONTHS; m++) register.recordMonth(company, annual / Equity.RECORD_MONTHS, 0);
                Exchange market = new Exchange();
                market.seedConstants(denomination);
                double[] books = new double[Equity.COMPANIES.length];
                assertTrue("fixture: " + label + " is mature, never traded and below the active wage band",
                        register.valuedOnRecord(company) && !market.hasTraded(company) && issued > 0 && abroad == issued
                                && floor > Exchange.MIN_FAIR / denomination && floor < wage * Exchange.CONSOLIDATE_BELOW);
                market.startMonth();
                market.takeMonth(register, null, null, companies, books, worldRate, 0, Equity.RECORD_MONTHS);
                boolean firstSafe = finiteListing(register, market, company) && market.atFloor(company)
                        && market.getSplit(company) == 0 && register.getShares(company) == issued;
                assertTrue(label + ": the first step classifies the anchored floor and makes no consolidation", firstSafe);
                // The old floor comparison is already red here: never drive its known shrinking-share loop into overflow.
                if (!firstSafe) continue;
                close(label + ": the market uses the register's anchored floor", market.fair(company), floor, floor * 1e-9);
                boolean stable = true;
                for (int m = 1; m <= 240; m++) {
                    register.recordMonth(company, annual / Equity.RECORD_MONTHS, 0);
                    market.startMonth();
                    market.takeMonth(register, null, null, companies, books, worldRate, 0, Equity.RECORD_MONTHS + m);
                    stable &= finiteListing(register, market, company) && market.atFloor(company)
                            && !market.hasTraded(company) && market.getSplit(company) == 0 && market.getSplitFactor(company) == 1
                            && register.getShares(company) == issued && register.getForeignShares(company) == abroad
                            && register.getDealerShares(company) == 0;
                    if (!stable) break;
                }
                assertTrue(label + ": 240 more monthly steps keep finite prices and every share without a split", stable);
                if (!stable) continue;
                close(label + ": the actual income is preserved", register.getTrailingIncome(company), annual, Math.abs(annual) * 1e-12);
                close(label + ": presentation-floor total value is unchanged", issued * market.fair(company), issued * floor, issued * floor * 1e-9);

                double recovery = 32 * floor * issued * Equity.requiredReturn(worldRate);
                for (int m = 0; m < Equity.RECORD_MONTHS; m++) register.recordMonth(company, recovery / Equity.RECORD_MONTHS, 0);
                double beforePrice = register.fairValue(company, 0, 0, worldRate), beforeValue = issued * beforePrice;
                assertTrue("fixture: " + label + " recovers above the floor but still below the consolidation band",
                        Double.isFinite(beforeValue) && beforePrice > floor && beforePrice < wage * Exchange.CONSOLIDATE_BELOW);
                market.startMonth();
                market.takeMonth(register, null, null, companies, books, worldRate, 0, Equity.RECORD_MONTHS + 241);
                boolean recovered = finiteListing(register, market, company);
                assertTrue(label + ": actual positive income recovers with finite prices, shares and split factor", recovered);
                if (!recovered) continue;
                assertTrue(label + ": recovery leaves the floor and really exercises automatic consolidation",
                        !market.atFloor(company) && market.getSplit(company) > 0 && market.getSplit(company) < 1);
                close(label + ": the recovered total value survives the automatic consolidation",
                        register.getShares(company) * market.fair(company), beforeValue, beforeValue * 1e-9);
                close(label + ": fair recomputation preserves that same recovered total value",
                        register.getShares(company) * register.fairValue(company, 0, 0, worldRate), beforeValue, beforeValue * 1e-9);
                close(label + ": the foreign owners still hold every share", register.getForeignShares(company), register.getShares(company), 0);
                close(label + ": the split changes no actual recovery income", register.getTrailingIncome(company), recovery, recovery * 1e-12);
            }
        }
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        int RETAIL = Equity.indexOf(Sectors.RETAIL);
        int INDUSTRY = Equity.indexOf(Sectors.INDUSTRY);

        /* ================= 1. the households first, then the world ================= */
        out.println("--- an offering goes to the households first, and the world takes the rest ---");

        HouseholdBalance rich = savers(20.0, 4.0);   // $20k saved on $4k a month: 3 months' cushion is $12k
        Household couple = rich.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
        double cushion = HouseholdBalance.SHARE_CUSHION_MONTHS * couple.disposable();
        double excess = couple.savings() - cushion;
        assertTrue("the fixture's households have money past the cushion", excess > 0);

        Equity founding = new Equity();   // no record: a founding offering
        double raised = founding.offer(INDUSTRY, 1_000, 0, rich, DebtManager.WORLD_BASE_RATE);
        double eachPaid = excess * HouseholdBalance.SHARE_OF_EXCESS;
        close("each household puts in the fraction of what is past its cushion",
                20.0 - couple.savings(), eachPaid, 1e-9);
        close("...and holds shares worth exactly what it paid, at the founding price",
                couple.shares(INDUSTRY) * Equity.FOUNDING_PRICE, eachPaid, 1e-9);
        close("the households' part is what a hundred of them paid",
                founding.getRaisedHomeThisMonth(INDUSTRY), 100 * eachPaid, 1e-9);
        close("...and the world took the rest of a founding offering",
                founding.getRaisedAbroadThisMonth(INDUSTRY), 1_000 - 100 * eachPaid, 1e-9);
        close("so the whole offering was raised", raised, 1_000, 1e-9);
        close("the register agrees with the households about what they hold",
                founding.getDomesticShares(INDUSTRY), rich.sharesHeld(INDUSTRY), 1e-9);

        // A household in debt, locked out, or short does not buy.
        HouseholdBalance owing = savers(20.0, 4.0);
        owing.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED).debt = 1.0;
        owing.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED).revolving = 1; // every household in this fixture owes
        Equity e2 = new Equity();
        e2.offer(INDUSTRY, 1_000, 0, owing, DebtManager.WORLD_BASE_RATE);
        close("a household that owes the bank buys nothing", e2.getRaisedHomeThisMonth(INDUSTRY), 0, 1e-9);
        HouseholdBalance thin = savers(cushion * .5, 4.0);
        Equity e3 = new Equity();
        e3.offer(INDUSTRY, 1_000, 0, thin, DebtManager.WORLD_BASE_RATE);
        close("...and one with only its cushion buys nothing", e3.getRaisedHomeThisMonth(INDUSTRY), 0, 1e-9);

        // Pro rata when they would buy more than is offered.
        HouseholdBalance keen = savers(100.0, 4.0);
        Equity e4 = new Equity();
        double small = e4.offer(INDUSTRY, 50, 0, keen, DebtManager.WORLD_BASE_RATE);
        close("a small offering is taken up, not oversubscribed", e4.getRaisedHomeThisMonth(INDUSTRY), 50, 1e-9);
        close("...and nothing goes abroad when the households took it all", e4.getRaisedAbroadThisMonth(INDUSTRY), 0, 1e-9);
        close("...raised is what was offered", small, 50, 1e-9);

        /* ================= 2. the world's test ================= */
        out.println("\n--- the world buys on the record, and a founding offering on prospects ---");

        double[] losses = new double[12];
        java.util.Arrays.fill(losses, -10);
        Equity loser = withRecord(INDUSTRY, losses);
        loser.offer(INDUSTRY, 1_000, 5_000, null, DebtManager.WORLD_BASE_RATE);
        close("twelve months of losses: the world buys nothing", loser.getRaisedAbroadThisMonth(INDUSTRY), 0, 1e-9);

        double[] profits = new double[12];
        java.util.Arrays.fill(profits, 100);   // $1,200k a year; 40% is $480k; on $6,000k of equity that is 8%
        Equity earner = withRecord(INDUSTRY, profits);
        earner.offer(INDUSTRY, 1_000, 5_000, null, DebtManager.WORLD_BASE_RATE);
        close("a record that yields past the world's rate plus the premium: it buys",
                earner.getRaisedAbroadThisMonth(INDUSTRY), 1_000, 1e-9);

        double[] meagre = new double[12];
        java.util.Arrays.fill(meagre, 10);     // $120k a year; 40% is $48k on $6,000k: 0.8%
        Equity dull = withRecord(INDUSTRY, meagre);
        dull.offer(INDUSTRY, 1_000, 5_000, null, DebtManager.WORLD_BASE_RATE);
        close("...and one that yields less than that gets nothing from abroad",
                dull.getRaisedAbroadThisMonth(INDUSTRY), 0, 1e-9);

        /* ================= 3. the regimes ================= */
        out.println("\n--- a company reads its own year ---");

        Equity young = withRecord(RETAIL, 100, 100, 100);
        assertTrue("three months on the books is NEW", young.getRegime(RETAIL) == Equity.Regime.NEW);
        assertTrue("steady profit for a year is GOOD", earner.getRegime(INDUSTRY) == Equity.Regime.GOOD);
        assertTrue("a year of losses is BAD", loser.getRegime(INDUSTRY) == Equity.Regime.BAD);
        Equity fading = withRecord(INDUSTRY, 200, 200, 200, 200, 200, 200, 50, 50, 50, 50, 50, 50);
        assertTrue("profitable but declining is NORMAL, not GOOD",
                fading.getRegime(INDUSTRY) == Equity.Regime.NORMAL);
        Equity patchy = withRecord(INDUSTRY, 100, -50, 100, -50, 100, -50, 100, -50, 100, -50, 100, -50);
        assertTrue("six loss months of twelve is BAD, whatever the total", patchy.getRegime(INDUSTRY) == Equity.Regime.BAD);

        Equity swingy = withRecord(INDUSTRY, 10, 190, 10, 190, 10, 190, 10, 190, 10, 190, 10, 190);
        assertTrue("a business whose income swings wants more equity than a steady one",
                swingy.getTargetEquityShare(INDUSTRY) > earner.getTargetEquityShare(INDUSTRY));
        close("...a steady one runs at the base", earner.getTargetEquityShare(INDUSTRY),
                Equity.BASE_EQUITY_SHARE, 1e-9);
        assertTrue("...and a loss-maker at the ceiling",
                loser.getTargetEquityShare(INDUSTRY) == Equity.MAX_EQUITY_SHARE);

        /* ================= 4. how much it raises ================= */
        out.println("\n--- equity before debt when it is new or doing well; debt when it is not ---");

        close("a new company sells the founding share of every plan",
                young.raiseFor(RETAIL, 0, 0, 1_000), 1_000 * Equity.NEW_EQUITY_SHARE, 1e-9);
        close("a company in a bad year sells nothing", loser.raiseFor(INDUSTRY, 10_000, 3_000, 1_000), 0, 1e-9);

        // GOOD: with $12,000 of building in the record, it raises for three years of it.
        Equity builder = new Equity();
        for (int m = 0; m < 12; m++) builder.recordMonth(INDUSTRY, 100, 1_000);
        assertTrue("the fixture's company is in a good year", builder.getRegime(INDUSTRY) == Equity.Regime.GOOD);
        double target = builder.getTargetEquityShare(INDUSTRY);
        double expected = Math.max(500, 12_000 * Equity.HORIZON_YEARS);
        double ahead = builder.raiseFor(INDUSTRY, 10_000, 3_000, 500);
        close("in a good year it raises AHEAD: the target share of three years' building, less what it has past target",
                ahead, Math.min(expected, target * (10_000 + expected) - 3_000), 1e-9);
        assertTrue("...which is far more than this month's plan", ahead > 500);
        close("...and never more than the expansion itself",
                builder.raiseFor(INDUSTRY, 10_000, 0, 500), Math.min(expected, target * (10_000 + expected)), 1e-9);
        close("a good year with equity already past the target raises nothing",
                builder.raiseFor(INDUSTRY, 10_000, 40_000, 500), 0, 1e-9);

        close("a normal year at target borrows",
                fading.raiseFor(INDUSTRY, 10_000, 10_000 * fading.getTargetEquityShare(INDUSTRY), 1_000), 0, 1e-9);
        double normalTarget = fading.getTargetEquityShare(INDUSTRY);
        double wayUnder = normalTarget * 11_000 * .5;
        assertTrue("...but well under target it raises back up to it",
                fading.raiseFor(INDUSTRY, 10_000, wayUnder, 1_000) > 0
                && fading.raiseFor(INDUSTRY, 10_000, wayUnder, 1_000) <= 1_000);

        /* ================= 5. the price ================= */
        out.println("\n--- a share sells at what the company is worth ---");

        HouseholdBalance buyers = savers(1_000.0, 4.0);
        Equity priced = withRecord(INDUSTRY, new double[12]);   // a year of nothing: book is all it has
        priced.offer(INDUSTRY, 100, 0, buyers, DebtManager.WORLD_BASE_RATE);   // founding: $1,000 a share
        double sharesAtFounding = priced.getShares(INDUSTRY);
        close("a company's first shares sell at the founding price", priced.getLastPrice(INDUSTRY), Equity.FOUNDING_PRICE, 1e-9);
        priced.offer(INDUSTRY, 100, 400, buyers, DebtManager.WORLD_BASE_RATE);   // book $400k on 100 shares: $4k a share
        close("the next sell at book per share", priced.getLastPrice(INDUSTRY), 400 / sharesAtFounding, 1e-9);
        /*
         * ...ON THE DIVIDEND IT ACTUALLY PAID since 0.7.12 round 2 (the brief:
         * participants value a share on what it pays, which since 0.7.11 is
         * paid after principal). This company owes nothing, so it pays PAYOUT
         * of its income - the fixture pays it, month by month, as
         * Game.payDividends() would - and the value is what it was.
         */
        Equity earning = new Equity();
        for (double v : profits) {
            earning.recordMonth(INDUSTRY, v, 0);
            earning.noteDividendPaid(INDUSTRY, Equity.PAYOUT * v);
            earning.closeDividendMonth();
        }
        earning.offer(INDUSTRY, 100, 0, buyers, DebtManager.WORLD_BASE_RATE);
        double earningsValue = Equity.PAYOUT * 1_200 / (DebtManager.WORLD_BASE_RATE + Equity.FOREIGN_PREMIUM);
        double before = earning.getShares(INDUSTRY);
        earning.offer(INDUSTRY, 100, 0.001, buyers, DebtManager.WORLD_BASE_RATE);   // no book, but earnings
        close("...and a company with no book but earnings sells at the dividend's value, not for nothing",
                earning.getLastPrice(INDUSTRY), earningsValue / before, 1e-9);
        Equity unpaid = withRecord(INDUSTRY, profits);
        unpaid.offer(INDUSTRY, 100, 0, buyers, DebtManager.WORLD_BASE_RATE);
        double unpaidBefore = unpaid.getShares(INDUSTRY);
        unpaid.offer(INDUSTRY, 100, 0.001, buyers, DebtManager.WORLD_BASE_RATE);
        close("...while the same earnings with no dividend paid are worth only the book (round 2)",
                unpaid.getLastPrice(INDUSTRY), 0.001 / unpaidBefore, 1e-9);

        /* ================= 6. the founders ================= */
        out.println("\n--- the founders own what they founded ---");

        HouseholdBalance founders = savers(1.0, 4.0);   // nothing past the cushion: they cannot buy
        Equity donated = new Equity();
        donated.offer(RETAIL, 100, 9_489, founders, DebtManager.WORLD_BASE_RATE);
        close("a first offering against a book already there issues the book to the households first",
                founders.sharesHeld(RETAIL) * Equity.FOUNDING_PRICE, 9_489, 1e-9);
        close("...so the new money buys only what it paid for",
                donated.getForeignShares(RETAIL) * Equity.FOUNDING_PRICE, 100, 1e-9);
        assertTrue("...and the founders keep the company", donated.foreignShare(RETAIL) < .02);

        /* ================= 7. the dividend ================= */
        out.println("\n--- the owners are paid a share of a positive month, and nothing on a loss ---");

        close("nothing is due on a loss", founding.dividendDue(INDUSTRY, -50), 0, 1e-9);
        close("...and the payout share of a profit", founding.dividendDue(INDUSTRY, 50), 50 * Equity.PAYOUT, 1e-9);
        Equity unowned = new Equity();
        close("a company with no shares owes nobody", unowned.dividendDue(INDUSTRY, 50), 0, 1e-9);

        double savedBefore = couple.savings();
        double due = founding.dividendDue(INDUSTRY, 50);
        double abroad = founding.payDividend(INDUSTRY, due, rich);
        double perShare = due / founding.getShares(INDUSTRY);
        close("each household is paid on its shares, into its savings",
                couple.savings() - savedBefore, couple.shares(INDUSTRY) * perShare, 1e-9);
        close("...the households' part is theirs", founding.getDividendHomeThisMonth(INDUSTRY),
                rich.sharesHeld(INDUSTRY) * perShare, 1e-9);
        close("...and the rest went abroad", abroad, founding.getForeignShares(INDUSTRY) * perShare, 1e-9);
        close("...every share paid once", founding.getDividendHomeThisMonth(INDUSTRY) + abroad, due, 1e-9);

        /*
         * SHARES THAT LEAVE WITH THEIR HOLDERS. Twenty of the hundred
         * households leave the city; their shares go with them, and from
         * then on they are paid abroad.
         */
        double[][] fewer = new double[FamilyStructure.values().length][PayTier.values().length];
        fewer[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = 80;
        double[] income = new double[HouseholdBalance.ROWS];
        income[PayTier.UNSKILLED.ordinal()] = 80 * 4.0;
        double[] none = new double[HouseholdBalance.ROWS];
        double heldEach = couple.shares(INDUSTRY);
        rich.advanceMonth((s, t) -> fewer[s.ordinal()][t.ordinal()], income, 0, none, none, .25, .05, 1);
        close("twenty households leaving take twenty households' shares",
                rich.getSharesTakenAway(INDUSTRY), 20 * heldEach, 1e-9);
        close("...and the ones who stayed hold what they held", couple.shares(INDUSTRY), heldEach, 1e-9);
        double foreignBefore = founding.getForeignShares(INDUSTRY);
        founding.startMonth();
        founding.followEmigrants(rich);
        founding.payDividend(INDUSTRY, founding.dividendDue(INDUSTRY, 50), rich);
        close("...and are paid abroad from then on",
                founding.getForeignShares(INDUSTRY), foreignBefore + 20 * heldEach, 1e-9);
        close("...with the register still agreeing with the households",
                founding.getDomesticShares(INDUSTRY), rich.sharesHeld(INDUSTRY), 1e-9);

        /* ================= 8. the save ================= */
        out.println("\n--- the register survives a save, by name ---");

        Equity back = new Equity();
        assertTrue("it restores", back.restore(founding.keys(), founding.toSaveArray()));
        close("...the shares", back.getShares(INDUSTRY), founding.getShares(INDUSTRY), 1e-12);
        close("...held abroad", back.getForeignShares(INDUSTRY), founding.getForeignShares(INDUSTRY), 1e-12);
        close("...and the lifetime dividends", back.getLifetimeDividendsAbroad(), founding.getLifetimeDividendsAbroad(), 1e-12);
        assertTrue("...and the regime is re-read from the record", back.getRegime(INDUSTRY) == founding.getRegime(INDUSTRY));
        assertTrue("an array of the wrong length is refused whole",
                !new Equity().restore(founding.keys(), new double[] {1, 2, 3}));
        HouseholdBalance cellsBack = new HouseholdBalance();
        cellsBack.restoreCells(rich.cellKeys(), rich.toCellSaveArray(), Equity.COMPANIES);
        close("the households' shares ride in the cell save",
                cellsBack.sharesHeld(INDUSTRY), rich.sharesHeld(INDUSTRY), 1e-12);

        /* ================= 9. a live city ================= */
        out.println("\n--- and a city whose companies have owners still adds up ---");

        Path root = TempDirs.create("equitycheck");
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
        System.setOut(quiet);
        double worstResidual = 0;
        int monthsWithDividends = 0;
        double paidHome = 0, paidAbroad = 0;
        try {
            city.run();
            // THE DEFAULTS PUT BACK IN THE FIXTURE (R0, a fixture condition; OP1): "every share of it is held, at home or abroad" below is
            // domestic + abroad = in issue, which holds only while the bank's desk and the fund hold none of Retail; in the city this section was
            // written against (every tax open from month 1) they hold none when Retail first has a book, and a 1900 city with no income taxes to
            // 1916-20 (until OP1) played its months differently, so the desk held a sliver (0.001 of a share) of it then. Since OP1 every lever
            // is open from the founding, and the defaults are put back before a month has run.
            city.getEconomyManager().getTaxPolicy().reset();
            city.simulateMonths(2);
            System.setOut(out);
            Equity register = city.getEquity();
            Bank bank = city.getBank();
            close("the founding bank's capital was sold as shares",
                    register.getShares(Equity.BANK) * Equity.FOUNDING_PRICE, Bank.PAID_IN_PER_BRANCH, 1e-6);
            close("...and every dollar of it arrived as capital, home or abroad",
                    register.getLifetimeRaisedHome(Equity.BANK) + register.getLifetimeRaisedAbroad(Equity.BANK),
                    Bank.PAID_IN_PER_BRANCH, 1e-6);
            /*
             * THERE ARE NO FOUNDING STORES ANY MORE. This asserted that the
             * households held Retail shares two months in, and they did -
             * $9.5M of them, granted as founders against a book that was
             * the BANK's premises: a Commercial Bank is a COMMERCIAL
             * building, and until the sector template (2026-09-11) every
             * commercial building was on Retail's balance sheet. The branch
             * is nobody's building now (sector ""), Retail founds with
             * nothing, and its first shares go to whoever pays for them -
             * which, in a city two months old with no savings, is the world.
             * The founders' rule itself is tested in section 6 above. What
             * is asserted here is that Retail is listed, and that the
             * shares that exist are owned by somebody.
             *
             * ...ONCE IT HAS A BOOK (0.7.18, re-caused). Retail had ordered its
             * first shop by month two, before anybody was there to staff it.
             * Every planner that builds posts asks the city for the workers
             * first now (Jerus: "Retail, Restaurants, Luxury and the makers
             * check whether they can staff a building before building it"), and
             * the first shop waits for the first spare hands, so the city is
             * played on, a month at a time and bounded, until Retail owns one.
             */
            System.setOut(quiet);
            for (int m = 0; m < 36 && city.getSectors().retail().postsPerTier() != null
                    && java.util.Arrays.stream(city.getSectors().retail().postsPerTier()).sum() == 0; m++) {
                city.simulateMonths(1);
            }
            System.setOut(out);
            assertTrue("retail is listed once it has a book",
                    register.getShares(RETAIL) > 0);
            // FS14: every holder counted, the cells by their own books (HouseholdBalance.sharesHeld), the world, the bank's desk and the
            // fund (Equity's own dealer and city shares), against the shares in issue plus what the world is short (Exchange.shortShares(),
            // EM2). It read domestic + abroad = in issue, which is register.getDomesticShares()'s own definition and true only while the desk
            // and the fund hold none of Retail: a path premise (OE1's path left the desk 0.000153 of a share when Retail first had a book).
            double retailHeld = city.getHouseholdBalance().sharesHeld(RETAIL) + register.getForeignShares(RETAIL)
                    + register.getDealerShares(RETAIL) + register.getCityShares(RETAIL);
            close("...and every share of it is held, at home or abroad", retailHeld,
                    register.getShares(RETAIL) + city.getExchange().shortShares(RETAIL), 1e-9 * Math.max(1, register.getShares(RETAIL)));
            System.setOut(quiet);
            for (int m = 0; m < 240; m++) {
                city.simulateMonths(1);
                worstResidual = Math.max(worstResidual, Math.abs(city.getLastMoneyAudit().relative()));
                double home = register.getDividendHomeThisMonth(), away = register.getDividendAbroadThisMonth();
                if (home + away > 0) monthsWithDividends++;
                paidHome += home;
                paidAbroad += away;
            }
        } finally {
            System.setOut(out);
        }
        out.printf("   after 20 years: %d offerings, $%,.0fk raised at home and $%,.0fk abroad;"
                + " dividends $%,.0fk to households, $%,.0fk abroad, in %d months%n",
                city.getEquity().getOfferings(), city.getEquity().getLifetimeRaisedHome(),
                city.getEquity().getLifetimeRaisedAbroad(), paidHome, paidAbroad, monthsWithDividends);
        // FS14: the offerings and the dividend months are counted above, not asserted: how often a played city goes to the market and pays
        // its owners is its path's (found), and both rules are caused in sections 1-7 (the offering's split, the founding offering, equity
        // before debt, the dividend of a positive month and none of a loss). The identities below hold on any path, and stay hard.
        assertTrue("...and every month of it is conserved", worstResidual < 1e-9);
        boolean agree = true;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            double reg = city.getEquity().getDomesticShares(c), held = city.getHouseholdBalance().sharesHeld(c);
            if (Math.abs(reg - held) > 1e-9 * Math.max(1, city.getEquity().getShares(c))) {
                agree = false;
                out.printf("   %s: register %.6f, households %.6f%n", Equity.COMPANIES[c], reg, held);
            }
        }
        assertTrue("...with the register and the households agreeing on every company", agree);

        maturePricesFollowTheirRecord();
        anchoredFloorIsNotAConsolidation();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
