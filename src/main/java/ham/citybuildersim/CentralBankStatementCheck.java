package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The central bank's own statements (CBK1): CentralBankStatements' income statement, balance sheet and money made and
 * destroyed held to the central bank's own figures, every month of two played cities, and across a save.
 *
 * <p>WHY. Jerus, 2026-11-26, on the Bank tab's two banks: "give both the banks proper statements ... make sure to really
 * include alot of stuff into the central bank area". A statement whose subtotals are its own sums has to be shown to land
 * on the bank's figures, or it is a second set of books that can drift from the first - the page would print a profit, a
 * remittance and a change in M0 nobody had checked (SectorStatementCheck's reason, for the sectors and the commercial
 * bank). And the batch may not move a month: its new figures are written beside the bank's and read by nothing in the
 * month, which section 5 holds.
 *
 * <p>What this has to prove:
 * <ol>
 *   <li>THE INCOME STATEMENT sums to the profit: its lines are the month's interest and gains less what reserves cost and
 *       add to the profit closeMonth() struck with nothing unaccounted; what the strike did with it leaves nothing kept;
 *       and the remittance, to the cent: what one month's strike owes the treasury is what the next month pays it, and
 *       the loss carried moves by exactly what was made good and what was added.</li>
 *   <li>THE BALANCE SHEET balances: its assets are totalAssets(), its liabilities M0, and the assets are the liabilities
 *       and equity in both columns; the year-ago column is the sheet as the harness read it twelve months before.</li>
 *   <li>THE MONEY MADE AND DESTROYED: each group is getIssued() and getRetired() with nothing unaccounted, it closes on
 *       M0, and in every played month made less destroyed is the change in M0 the harness measured from the month
 *       before, to the cent.</li>
 *   <li>LAST MONTH: the income statement's and the money statement's comparative columns are what they printed a month
 *       before.</li>
 *   <li>THE PAGE'S FIGURES ARE THE GETTERS': every line is the CentralBank figure it names, the holdings' ladder adds to
 *       what the bank holds at face and to its book at market; and reading all of it every month leaves the month's
 *       arithmetic where it was - a city read every month ends to the bit where its twin, never read, does.</li>
 *   <li>A SAVE AND A LOAD keep every column, and the month after the load prints the saved month as its last; an older
 *       save's bank (its array cut where CBK1's slots begin) knows no last month and no year ago, and every other figure
 *       is what it was.</li>
 * </ol>
 *
 * <p>The cities cause what is checked: the test player's city with the holdings dial moved up and back down (paper bought
 * and sold, coupons, principal), a month of profit and a month of loss, a remittance, the window and the treasury's
 * advances; each is counted, and a count of none fails.
 */
public class CentralBankStatementCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static final double CENT = CentralBankStatements.CENT;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-118s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** One identity's tally over a run: how many times it was checked, how many missed by more than a cent, and the worst miss. */
    static final class Tally {
        int checked, missed;
        double worst;
        String where = "";

        void near(double actual, double expected, String at) {
            checked++;
            double off = Math.abs(actual - expected);
            if (Double.isNaN(actual) != Double.isNaN(expected)) off = Double.POSITIVE_INFINITY;
            else if (Double.isNaN(actual)) off = 0;
            if (off > CENT) {
                missed++;
                if (off > worst || where.isEmpty()) where = at + String.format(" (%.5f against %.5f)", actual, expected);
            }
            if (off > worst) worst = off;
        }

        String words() {
            return String.format("%,d checked, %d off by more than a cent%s", checked, missed,
                    missed > 0 ? ", first worst at " + where : "");
        }
    }

    /** The tallies of the run, by identity, each under its section's number. */
    static final Map<String, Tally> tallies = new LinkedHashMap<>();

    static Tally t(String name) { return tallies.computeIfAbsent(name, k -> new Tally()); }

    /** What the cities caused: months with each condition, by name. */
    static final Map<String, Integer> caused = new LinkedHashMap<>();

    static void count(String what, boolean when) {
        caused.merge(what, when ? 1 : 0, Integer::sum);
    }

    /** One city's memory between its months: last month's statements, the sheet as each month opened, M0 and what the strike owed. */
    static final class Memory {
        SectorStatements.Table income, money;
        /** The sheet as each month opened, by the month it opened: read before the month ran, after whatever the player did. */
        final Map<Integer, SectorStatements.Table> openings = new HashMap<>();
        double m0 = Double.NaN, toRemit = Double.NaN, lossCarried = Double.NaN;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = TempDirs.create("centralbankstatementcheck");

        Game player = playersCity(root);
        readChangesNothing(root);
        everyMonth();
        savedAndLoaded(player);
        anOlderSave(player);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ------------------------------------------------------- the cities */

    /** The month the holdings dial goes up in the test player's city, the month it comes down, and the settings. */
    static final int QE_FROM = 100, QE_BACK = 220, MONTHS = 300;
    static final double QE_UP = .30, QE_DOWN = .05;
    /** ...and the months its treasury is emptied, and filled again. */
    static final int DRY_AT = 60, REFILLED_AT = 84;

    /** The test player's city (LongPlaytest's founding and rhythm, SectorStatementCheck's), the holdings dial moved up and back. */
    static Game playersCity(Path root) {
        out.println("--- the cities: the test player's, " + MONTHS + " months, its holdings dial at " + pct(QE_UP) + " from month "
                + QE_FROM + " and " + pct(QE_DOWN) + " from " + QE_BACK + " ---");
        Path dir = root.resolve("player");
        Game g = new Game(new GameFiles(dir.resolve("data"), dir.resolve("no-legacy")), LongPlaytest.founding());
        Memory mem = new Memory();
        boolean[] sold = { false }, dry = { false, false };
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = quiet;
        try {
            quietly(() -> {
                g.run();
                g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
                g.setRolloverMode(LongPlaytest.ROLLOVER);
                g.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC);
                LongPlaytest.villageBuild(g, "House", 40);
                LongPlaytest.villageBuild(g, "Convenience Store", 3);
                LongPlaytest.villageBuild(g, "Mixed Farm", 2);
                months(g, 3, mem);
                LongPlaytest.villageBuild(g, "House", 20);
                months(g, 4, mem);
                LongPlaytest.villageBuild(g, "Convenience Store", 2);
                LongPlaytest.villageBuild(g, "Construction Depot", 1);
                months(g, 5, mem);
                LongPlaytest.advise(g);
                months(g, 6, mem);
                int stop = 0;
                while (g.getMonth() < MONTHS) {
                    // The dial buys only term paper, and this city's treasury sells none of its own: two serials are sold
                    // into it as the dial goes up (CentralBankCheck's fixture), five and ten years, so the book is bought,
                    // pays coupons, pays principal as the five-year runs off, and is sold back as the dial comes down.
                    if (g.getMonth() >= QE_FROM && !sold[0]) {
                        double ask = Math.max(5_000, g.getHouseholdBalance().spareForPaper() * 3);
                        g.handleMediumBondLogic(ask, 5, 100 * PriceLevel.FACTOR);
                        g.handleMediumBondLogic(ask, 10, 100 * PriceLevel.FACTOR);
                        sold[0] = true;
                    }
                    // ...and this treasury never runs dry on its own: it is emptied once, half its ceiling short (CentralBankCheck's
                    // section 5), so the bank advances it, charges it and, when the cash is put back, is repaid.
                    if (g.getMonth() >= DRY_AT && !dry[0]) {
                        g.setCashForTest(-Math.max(1_000, .5 * g.getCentralBank().ceiling()));
                        dry[0] = true;
                    }
                    if (g.getMonth() >= REFILLED_AT && !dry[1]) {
                        g.setCashForTest(g.getCash() + 1.5 * g.getCentralBank().getAdvancesToTreasury());
                        dry[1] = true;
                    }
                    if (g.getMonth() >= QE_FROM && g.getMonth() < QE_BACK) g.getCentralBank().setTargetShare(QE_UP);
                    if (g.getMonth() >= QE_BACK) g.getCentralBank().setTargetShare(QE_DOWN);
                    stop++;
                    int skip = switch (stop % 6) { case 0 -> 30; case 1 -> 12; case 2 -> 24; case 3 -> 18; case 4 -> 6; default -> 36; };
                    months(g, Math.min(skip, MONTHS - g.getMonth()), mem);
                    if (g.getMonth() >= MONTHS) break;
                    for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                        if (LongPlaytest.advise(g) == null) break;
                        months(g, 1, mem);
                    }
                    months(g, 2, mem);
                }
            });
        } finally {
            LongPlaytest.out = was;
        }
        out.printf("    (the test player's city: %d months, %,d people; the central bank holds %s of the term paper, M0 %s)%n",
                g.getMonth(), g.getPopulationManager().getPopulation(), pct(g.getDebtManager().centralBankShareOfTerm()),
                thousands(g.getCentralBank().m0()));
        return g;
    }

    static String pct(double v) { return String.format("%.0f%%", v * 100); }

    /** Money in the model's thousands, as a line of the log says it. */
    static String thousands(double v) { return String.format("$%,.0fK", v); }

    static void months(Game g, int n, Memory mem) {
        for (int i = 0; i < n; i++) {
            DebtManager dm = g.getDebtManager();
            SectorStatements.Table opening = CentralBankStatements.sheet(g.getCentralBank(), dm.centralBankMarketValue(),
                    dm.centralBankMarkGain());
            LongPlaytest.run(g, 1);
            mem.openings.put(g.getMonth(), opening);
            mem.openings.remove(g.getMonth() - 12);
            audit(g, mem);
        }
    }

    /* --------------------------------------------------------- one month */

    /** Sections 1 to 5 on the month just played. */
    static void audit(Game g, Memory mem) {
        CentralBank cb = g.getCentralBank();
        DebtManager dm = g.getDebtManager();
        String at = "month " + g.getMonth();
        SectorStatements.Table inc = CentralBankStatements.income(cb, g.getForeignAccounts().getVaultInterest());
        SectorStatements.Table sh = CentralBankStatements.sheet(cb, dm.centralBankMarketValue(), dm.centralBankMarkGain());
        SectorStatements.Table mo = CentralBankStatements.money(cb);

        // 1. The income statement.
        t("1 its lines add to the profit closeMonth() struck").near(inc.now(CentralBankStatements.I_PROFIT), cb.getStruckProfit(), at);
        t("1 ...with nothing NOT ACCOUNTED FOR").near(inc.now(CentralBankStatements.I_PROFIT_REST), 0, at);
        t("1 the profit struck is the month's interest and gains less what reserves cost, at the page (monthProfit())")
                .near(cb.getStruckProfit(), cb.monthProfit(), at);
        t("1 what the strike did with it leaves nothing kept").near(inc.now(CentralBankStatements.I_KEPT), 0, at);
        t("1 what it owes the treasury after the strike is the strike's own share (getRemittanceDue() is getToRemit())")
                .near(cb.getRemittanceDue(), cb.getToRemit(), at);
        if (!Double.isNaN(mem.toRemit)) {
            t("1 the remittance paid this month is what last month's strike owed, to the cent").near(cb.getRemitted(), mem.toRemit, at);
            t("1 the loss carried moves by what was made good and what was added")
                    .near(cb.getLossCarried(), mem.lossCarried - cb.getLossRecovered() + cb.getLossAdded(), at);
        }
        count("a month of profit", cb.getStruckProfit() > CENT);
        count("a month of loss", cb.getStruckProfit() < -CENT);
        count("a loss made good", cb.getLossRecovered() > CENT);
        count("a remittance paid", cb.getRemitted() > CENT);
        count("interest earned at the window", cb.getWindowInterest() > CENT);
        count("interest earned on the treasury's advances", cb.getAdvancesInterest() > CENT);
        count("coupons on its paper", cb.getPaperCoupons() > CENT);
        count("interest paid on reserves", cb.getInterestOnReserves() > CENT);

        // 2. The balance sheet.
        t("2 TOTAL ASSETS is totalAssets()").near(sh.now(CentralBankStatements.S_ASSETS), cb.totalAssets(), at);
        t("2 TOTAL LIABILITIES is M0").near(sh.now(CentralBankStatements.S_LIABILITIES), cb.m0(), at);
        t("2 TOTAL EQUITY is equity()").near(sh.now(CentralBankStatements.S_EQUITY), cb.equity(), at);
        t("2 it balances: assets are liabilities and equity").near(sh.now(CentralBankStatements.S_ASSETS),
                sh.now(CentralBankStatements.S_CLAIMS), at);
        t("2 ...with nothing NOT ACCOUNTED FOR on either side").near(Math.abs(sh.now(CentralBankStatements.S_ASSETS_REST))
                + Math.abs(sh.now(CentralBankStatements.S_LIABILITIES_REST)), 0, at);
        if (cb.knowsYearAgo()) {
            t("2 a year ago, it balances too").near(sh.then(CentralBankStatements.S_ASSETS), sh.then(CentralBankStatements.S_CLAIMS), at);
            // The sheet a year ago is the one the bank filed as the month eleven back opened (fileMonth()): the close of the
            // month twelve back, with whatever the player did before the next began: in CBK1's first run, read against the sheet
            // as the month closed instead, 20 of the year-ago vaults differed, moved by the test player's hand between months.
            SectorStatements.Table before = mem.openings.get(g.getMonth() - 11);
            if (before != null) {
                for (SectorStatements.Row r : sh.rows()) {
                    if (r.kind() == SectorStatements.Kind.HEAD || Double.isNaN(r.then())) continue;
                    t("2 the year-ago column is the sheet as the month eleven back opened (the close of the twelfth back), line by line")
                            .near(r.then(), before.now(r.id()), at + " " + r.id());
                }
            }
        }
        count("paper held", cb.getPaperHeld() > CENT);
        count("the treasury owes it advances", cb.getAdvancesToTreasury() > CENT);
        count("a year ago known", cb.knowsYearAgo());

        // 3. The money made and destroyed.
        t("3 the money made is getIssued()").near(mo.now(CentralBankStatements.M_MADE), cb.getIssued(), at);
        t("3 the money destroyed is getRetired()").near(mo.now(CentralBankStatements.M_DESTROYED), -cb.getRetired(), at);
        t("3 ...each with nothing NOT ACCOUNTED FOR").near(Math.abs(mo.now(CentralBankStatements.M_MADE_REST))
                + Math.abs(mo.now(CentralBankStatements.M_DESTROYED_REST)), 0, at);
        t("3 M0 MOVED is getM0Moved()").near(mo.now(CentralBankStatements.M_MOVED), cb.getM0Moved(), at);
        t("3 it closes on M0, from M0 as the month opened, with nothing between").near(mo.now(CentralBankStatements.M_REST), 0, at);
        t("3 M0 AS THE MONTH CLOSED is m0()").near(mo.now(CentralBankStatements.M_CLOSED), cb.m0(), at);
        if (!Double.isNaN(mem.m0)) {
            t("3 made less destroyed is the change in M0 from the month before, as the harness read it, to the cent")
                    .near(cb.getIssued() - cb.getRetired(), cb.m0() - mem.m0, at);
            t("3 M0 as the month opened is M0 as the month before closed").near(cb.getM0AtStart(), mem.m0, at);
        }
        for (Object[] w : CentralBankStatements.MADE) {
            CentralBank.Line l = (CentralBank.Line) w[1];
            count("made: " + w[0], cb.thisMonth(l) > CENT);
        }
        for (Object[] w : CentralBankStatements.DESTROYED) {
            CentralBank.Line l = (CentralBank.Line) w[1];
            count("destroyed: " + w[0], cb.thisMonth(l) > CENT);
        }

        // 4. Last month.
        if (cb.knowsLastMonth() && mem.income != null) {
            lastMonthIs(inc, mem.income, "4 the income statement's last-month column is what it printed last month", at);
            lastMonthIs(mo, mem.money, "4 the money statement's last-month column is what it printed last month", at);
        }
        count("last month known", cb.knowsLastMonth());

        // 5. The page's figures are the getters'.
        lineIs(inc, CentralBankStatements.I_WINDOW, cb.getWindowInterest(), at);
        lineIs(inc, CentralBankStatements.I_ADVANCES, cb.getAdvancesInterest(), at);
        lineIs(inc, CentralBankStatements.I_COUPONS, cb.getPaperCoupons(), at);
        lineIs(inc, CentralBankStatements.I_GAINS, cb.getPaperGains(), at);
        lineIs(inc, CentralBankStatements.I_RESERVES, -cb.getInterestOnReserves(), at);
        lineIs(inc, CentralBankStatements.I_RECOVERED, -cb.getLossRecovered(), at);
        lineIs(inc, CentralBankStatements.I_TO_REMIT, -cb.getToRemit(), at);
        lineIs(inc, CentralBankStatements.I_CARRIED, cb.getLossAdded(), at);
        lineIs(inc, CentralBankStatements.I_REMITTED, cb.getRemitted(), at);
        lineIs(sh, CentralBankStatements.S_WINDOW, cb.getAdvancesToBank(), at);
        lineIs(sh, CentralBankStatements.S_TREASURY, cb.getAdvancesToTreasury(), at);
        lineIs(sh, CentralBankStatements.S_PAPER, cb.getPaperHeld(), at);
        lineIs(sh, CentralBankStatements.S_REDEMPTION, cb.getRedemptionDue(), at);
        lineIs(sh, CentralBankStatements.S_VAULT, cb.getVault(), at);
        lineIs(sh, CentralBankStatements.S_RESERVES, cb.getReserves(), at);
        lineIs(sh, CentralBankStatements.S_CURRENCY, cb.getCurrency(), at);
        lineIs(sh, CentralBankStatements.S_DUE, cb.getRemittanceDue(), at);
        lineIs(sh, CentralBankStatements.S_AT_MARKET, dm.centralBankMarketValue(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.ADVANCED_TO_BANK), cb.getAdvancedToBank(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.ADVANCED_TO_TREASURY), cb.getAdvancedToTreasury(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.RESERVES_INTEREST), cb.getInterestOnReserves(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.REMITTED), cb.getRemitted(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.BOUGHT_FROM_HOUSEHOLDS), cb.getBoughtFromHouseholds(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.BOUGHT_FROM_WORLD), cb.getBoughtFromWorld(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.BOUGHT_AT_ISSUE), cb.getBoughtAtIssue(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.DOLLARS_BOUGHT), cb.getDollarsBought(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.GOLD_MINTED), cb.getGoldMinted(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.REPAID_BY_BANK), -cb.getRepaidByBank(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.WINDOW_INTEREST), -cb.getWindowInterest(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.REPAID_BY_TREASURY), -cb.getRepaidByTreasury(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.ADVANCES_INTEREST), -cb.getAdvancesInterest(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.SOLD_PAPER), -cb.getSoldPaper(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.PAPER_COUPONS), -cb.getPaperCoupons(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.PAPER_REDEEMED), -cb.getPaperRedeemed(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.BOUGHT_BACK), -cb.getBoughtBack(), at);
        lineIs(mo, CentralBankStatements.moneyId(CentralBank.Line.GOLD_REDEEMED), -cb.getGoldRedeemed(), at);
        t("5 the paper bought from the commercial bank and the three others are what it paid for paper")
                .near(cb.getBoughtFromBank() + cb.getBoughtFromHouseholds() + cb.getBoughtFromWorld() + cb.getBoughtAtIssue(),
                        cb.getBoughtPaper(), at);
        List<CentralBankStatements.Rung> ladder = CentralBankStatements.ladder(dm);
        double face = 0, market = 0;
        for (int k = 0; k < ladder.size() - 1; k++) {
            face += ladder.get(k).face();
            market += ladder.get(k).market();
            t("5 each rung's gain is its market less its face").near(ladder.get(k).gain(),
                    ladder.get(k).market() - ladder.get(k).face(), at);
        }
        CentralBankStatements.Rung whole = ladder.get(ladder.size() - 1);
        t("5 the ladder adds to what it holds at face (getPaperHeld())").near(face, cb.getPaperHeld(), at);
        t("5 ...which is the debt book's own figure for it (centralBankPrincipal())").near(dm.centralBankPrincipal(), cb.getPaperHeld(), at);
        t("5 ...and at market to centralBankMarketValue()").near(market, dm.centralBankMarketValue(), at);
        t("5 its last rung is the whole book, at face and at market").near(Math.abs(whole.face() - face)
                + Math.abs(whole.market() - market), 0, at);
        t("5 the mark is the book at market less face (centralBankMarkGain())").near(dm.centralBankMarkGain(),
                dm.centralBankMarketValue() - dm.centralBankPrincipal(), at);
        count("a mark on its book that is not nothing", Math.abs(dm.centralBankMarkGain()) > CENT);

        mem.income = inc;
        mem.money = mo;
        mem.m0 = cb.m0();
        mem.toRemit = cb.getToRemit();
        mem.lossCarried = cb.getLossCarried();
    }

    /** Every row of `now` whose last-month column is known is the same row of the statement a month before. */
    static void lastMonthIs(SectorStatements.Table now, SectorStatements.Table before, String label, String at) {
        for (SectorStatements.Row r : now.rows()) {
            if (r.kind() == SectorStatements.Kind.HEAD || Double.isNaN(r.then())) continue;
            t(label).near(r.then(), before.now(r.id()), at + " " + r.id());
        }
    }

    /** A line of a statement is the getter it names. */
    static void lineIs(SectorStatements.Table table, String id, double getter, String at) {
        t("5 every line is the CentralBank or DebtManager figure it names").near(table.now(id), getter, at + " " + id);
    }

    /** Sections 1 to 5's verdicts: each identity's tally, then what the city caused. */
    static void everyMonth() {
        out.println("\n--- 1-5. every month of the test player's city: the statements against the bank's own figures ---");
        for (Map.Entry<String, Tally> e : tallies.entrySet()) {
            Tally x = e.getValue();
            assertTrue(e.getKey() + ": " + x.words(), x.checked > 0 && x.missed == 0);
        }
        out.println("\n--- what the city caused: months with each ---");
        // The flows a city in this build makes: the advances, interest both ways, the remittance, a year of sheets, paper bought
        // and sold off the dial and run off at maturity. The four the default city never reaches - paper from abroad, the vault's
        // dollars, the mint, the gold taken back - are printed and not asserted: they need a currency regime or a world it has not.
        String[] needed = { "a month of profit", "a month of loss", "a remittance paid", "interest paid on reserves",
                "interest earned on the treasury's advances", "the treasury owes it advances", "made: Advanced to the treasury: printed",
                "destroyed: Repaid by the treasury", "destroyed: The treasury's interest on its advances",
                "interest earned at the window", "made: Lent to the commercial bank at the window",
                "destroyed: Repaid by the commercial bank at the window", "made: Paper bought from the commercial bank",
                "destroyed: Paid it for paper it sold the commercial bank", "a loss made good",
                "coupons on its paper", "paper held", "a year ago known", "last month known",
                "made: Interest paid on the commercial bank's reserves", "made: The remittance: last month's profit, to the treasury",
                "destroyed: Coupons on the paper it holds", "destroyed: Principal repaid on the paper it holds",
                "a mark on its book that is not nothing" };
        for (Map.Entry<String, Integer> e : caused.entrySet()) {
            boolean must = List.of(needed).contains(e.getKey());
            if (must) assertTrue("caused: " + e.getKey() + " (" + e.getValue() + " months)", e.getValue() > 0);
            else out.printf("    %s: %d months%n", e.getKey(), e.getValue());
        }
    }

    /* ------------------------------------------- 5. reading changes nothing */

    /** A plain founding read every month as the pages read it, against its twin never read: the same city to the bit. */
    static void readChangesNothing(Path root) {
        out.println("\n--- 5. reading the statements every month leaves the month where it was ---");
        Game read = plain(root.resolve("read"), true);
        Game unread = plain(root.resolve("unread"), false);
        CentralBank a = read.getCentralBank(), b = unread.getCentralBank();
        assertTrue(String.format("a plain founding read every month and its twin never read end on the same M0 to the bit (%s, %s)",
                thousands(a.m0()), thousands(b.m0())), a.m0() == b.m0());
        assertTrue("...the same central bank, array for array", java.util.Arrays.equals(a.toSaveArray(), b.toSaveArray()));
        assertTrue("...the same treasury and the same people", read.getCash() == unread.getCash()
                && read.getPopulationManager().getPopulation() == unread.getPopulationManager().getPopulation());
    }

    static final int PLAIN_MONTHS = 120;

    static Game plain(Path dir, boolean readEveryMonth) {
        Game g = new Game(new GameFiles(dir.resolve("data"), dir.resolve("no-legacy")));
        quietly(() -> {
            g.newGame();
            g.getCentralBank().setTargetShare(QE_UP);
            for (int i = 0; i < PLAIN_MONTHS; i++) {
                g.toggleNextMonth();
                if (!readEveryMonth) continue;
                CentralBank cb = g.getCentralBank();
                DebtManager dm = g.getDebtManager();
                CentralBankStatements.income(cb, g.getForeignAccounts().getVaultInterest());
                CentralBankStatements.sheet(cb, dm.centralBankMarketValue(), dm.centralBankMarkGain());
                CentralBankStatements.money(cb);
                CentralBankStatements.ladder(dm);
                dm.centralBankMonthsLeft();
            }
        });
        return g;
    }

    /* ------------------------------------------------- 6. a save and a load */

    static void savedAndLoaded(Game g) {
        out.println("\n--- 6. a save and a load keep every column, and the month after prints the saved month as its last ---");
        CentralBank cb = g.getCentralBank();
        DebtManager dm = g.getDebtManager();
        SectorStatements.Table inc = CentralBankStatements.income(cb, g.getForeignAccounts().getVaultInterest());
        SectorStatements.Table sh = CentralBankStatements.sheet(cb, dm.centralBankMarketValue(), dm.centralBankMarkGain());
        SectorStatements.Table mo = CentralBankStatements.money(cb);
        quietly(() -> g.saveGame(10));
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(10));
        assertTrue("fixture: the city loads", back.getLoadFailure() == null);
        CentralBank cb2 = back.getCentralBank();
        DebtManager dm2 = back.getDebtManager();
        assertTrue("the loaded bank knows last month and a year ago as the saved one did ("
                + cb.knowsLastMonth() + ", " + cb.knowsYearAgo() + ")",
                cb2.knowsLastMonth() == cb.knowsLastMonth() && cb2.knowsYearAgo() == cb.knowsYearAgo() && cb.knowsYearAgo());
        Tally kept = new Tally();
        sameTable(kept, inc, CentralBankStatements.income(cb2, back.getForeignAccounts().getVaultInterest()), "income");
        sameTable(kept, sh, CentralBankStatements.sheet(cb2, dm2.centralBankMarketValue(), dm2.centralBankMarkGain()), "sheet");
        sameTable(kept, mo, CentralBankStatements.money(cb2), "money");
        assertTrue("every row of the three statements, both columns, is the same after the load: " + kept.words(),
                kept.checked > 0 && kept.missed == 0);
        assertTrue("...the month's lines with them: the loaded month made and destroyed what the saved one did",
                Math.abs(cb2.getIssued() - cb.getIssued()) <= CENT && Math.abs(cb2.getRetired() - cb.getRetired()) <= CENT
                        && cb.getIssued() > 0);
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = quiet;
        try {
            quietly(() -> LongPlaytest.run(back, 1));
        } finally {
            LongPlaytest.out = was;
        }
        SectorStatements.Table after = CentralBankStatements.income(cb2, back.getForeignAccounts().getVaultInterest());
        SectorStatements.Table afterMoney = CentralBankStatements.money(cb2);
        Tally last = new Tally();
        for (SectorStatements.Row r : after.rows()) {
            if (r.kind() == SectorStatements.Kind.HEAD || Double.isNaN(r.then())) continue;
            last.near(r.then(), inc.now(r.id()), "income " + r.id());
        }
        for (SectorStatements.Row r : afterMoney.rows()) {
            if (r.kind() == SectorStatements.Kind.HEAD || Double.isNaN(r.then())) continue;
            last.near(r.then(), mo.now(r.id()), "money " + r.id());
        }
        assertTrue("a month after the load, its last-month columns are the month that was saved: " + last.words(),
                last.checked > 0 && last.missed == 0);
    }

    static void sameTable(Tally x, SectorStatements.Table a, SectorStatements.Table b, String which) {
        for (SectorStatements.Row r : a.rows()) {
            if (r.kind() == SectorStatements.Kind.HEAD) continue;
            x.near(b.now(r.id()), r.now(), which + " " + r.id() + " now");
            x.near(b.then(r.id()), r.then(), which + " " + r.id() + " then");
        }
    }

    /** The slots of the central bank's array before CBK1's: 13, the revenue ring, and the fourteen 0.7.1 to FB14 appended. */
    static final int BEFORE_CBK1 = 13 + CentralBank.REVENUE_MONTHS + 14;

    static void anOlderSave(Game g) {
        out.println("\n--- 6. an older save's bank: no last month and no year ago, and everything else as it was ---");
        CentralBank cb = g.getCentralBank();
        double[] whole = cb.toSaveArray();
        CentralBank same = new CentralBank(cb::getVault);
        same.restore(whole);
        assertTrue("a bank restored from its own array saves the same array, slot for slot ("
                + whole.length + " slots)", java.util.Arrays.equals(same.toSaveArray(), whole));
        CentralBank older = new CentralBank(cb::getVault);
        older.restore(java.util.Arrays.copyOf(whole, BEFORE_CBK1));
        assertTrue("an array cut where CBK1's slots begin restores a bank that knows no last month and no year ago",
                !older.knowsLastMonth() && !older.knowsYearAgo() && cb.knowsLastMonth() && cb.knowsYearAgo());
        assertTrue("...and none of the month's lines: it made and destroyed nothing that it knows of",
                older.getIssued() == 0 && older.getRetired() == 0 && older.getStruckProfit() == 0);
        assertTrue("...its sheet, its loss carried, its remittance due and its lifetimes are what they were",
                older.m0() == cb.m0() && older.totalAssets() == cb.totalAssets() && older.getLossCarried() == cb.getLossCarried()
                        && older.getRemittanceDue() == cb.getRemittanceDue() && older.getIssuedLifetime() == cb.getIssuedLifetime()
                        && older.getGoldRedeemedLifetime() == cb.getGoldRedeemedLifetime());
        older.startMonth();
        assertTrue("...a month on it has filed one month, the one it was opened in, and still knows no last month",
                !older.knowsLastMonth());
        older.startMonth();
        assertTrue("...and the month after, it does", older.knowsLastMonth());
    }
}
