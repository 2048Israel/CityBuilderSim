package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
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

    /** Households: a hundred unskilled couples with this much saved each, struck once so they have a take-home. */
    static HouseholdBalance savers(double savedEach, double takeHome) {
        HouseholdBalance hb = new HouseholdBalance();
        double[][] mix = new double[FamilyStructure.values().length][PayTier.values().length];
        mix[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = 100;
        double[] income = new double[HouseholdBalance.ROWS];
        income[PayTier.UNSKILLED.ordinal()] = 100 * takeHome;
        double[] none = new double[HouseholdBalance.ROWS];
        double[] spent = new double[HouseholdBalance.ROWS];
        // One month, with nothing spent, so they hold the buffer plus the
        // month; then the savings are set to the figure the fixture wants.
        hb.advanceMonth((s, t) -> mix[s.ordinal()][t.ordinal()], income, 0, none, spent, .25, .05, 1);
        Household c = hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
        c.savings = savedEach;
        return hb;
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
        Equity earning = withRecord(INDUSTRY, profits);
        earning.offer(INDUSTRY, 100, 0, buyers, DebtManager.WORLD_BASE_RATE);
        double earningsValue = Equity.PAYOUT * 1_200 / (DebtManager.WORLD_BASE_RATE + Equity.FOREIGN_PREMIUM);
        double before = earning.getShares(INDUSTRY);
        earning.offer(INDUSTRY, 100, 0.001, buyers, DebtManager.WORLD_BASE_RATE);   // no book, but earnings
        close("...and a company with no book but earnings sells at the earnings' value, not for nothing",
                earning.getLastPrice(INDUSTRY), earningsValue / before, 1e-9);

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
        cellsBack.restoreCells(rich.cellKeys(), rich.toCellSaveArray());
        close("the households' shares ride in the cell save",
                cellsBack.sharesHeld(INDUSTRY), rich.sharesHeld(INDUSTRY), 1e-12);

        /* ================= 9. a live city ================= */
        out.println("\n--- and a city whose companies have owners still adds up ---");

        Path root = Files.createTempDirectory("equitycheck");
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
        System.setOut(quiet);
        double worstResidual = 0;
        int monthsWithDividends = 0;
        double paidHome = 0, paidAbroad = 0;
        try {
            city.run();
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
             */
            assertTrue("retail is listed once it has a book",
                    register.getShares(RETAIL) > 0);
            close("...and every share of it is held, at home or abroad",
                    register.getDomesticShares(RETAIL) + register.getForeignShares(RETAIL),
                    register.getShares(RETAIL), 1e-6);
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
        assertTrue("the city's companies went to the market on their own", city.getEquity().getOfferings() > 1);
        assertTrue("...and paid their owners", monthsWithDividends > 0 && paidHome + paidAbroad > 0);
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

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
