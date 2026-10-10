package ham.citybuildersim;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The sector statements (0.7.74, batch S1): SectorStatements' formal
 * statements held to the model's own figures, every sector and the bank,
 * every month of two played cities, and across a save.
 *
 * WHY. Jerus asked for "both a summarized and a detailed actual statement"
 * of every sector, and for the bank's in a bank's own order. A statement
 * whose subtotals are its own sums has to be shown to land on the model's
 * figures, or it is a second set of books that can drift from the first -
 * the screen would then print a gross profit, an operating profit and a
 * cash flow nobody had checked. The spec (the project's
 * spec-sector-statements.md, section 7) lists what must hold; this is it.
 *
 * What this has to prove:
 *   1. THE INCOME STATEMENT: its profit before tax is preTaxIncome to the
 *      cent and its profit netIncome; each subtotal is the model's own
 *      identity (revenue after sales tax, gross, operating); its total result
 *      is the profit and the named outside lines; the finance costs' note
 *      (R1) adds to the line in every month it is counted.
 *   2. THE CASH FLOW: the three sections and the cash at the start, with
 *      unexplained() as the residual, are the cash at the end - which is the
 *      cash - so every term of unexplained() is in exactly one section.
 *   3. THE CLASSIFIED SHEET: current and long-lived assets are the total
 *      assets; what falls due within a year and later are the liabilities;
 *      R2's kinds are the debt line; equity and claims close; last month's
 *      column is last month's sheet.
 *   4. THE EQUITY STATEMENT closes from last month's equity to this month's,
 *      its owners' lines are equityRaised, dividendsPaid and
 *      sharesBoughtBack, and its outside line is the income statement's.
 *   5. THE BANK: its net interest income, total operating income, profit
 *      before tax, profit and what it kept are its Bank.Lines; its interest
 *      income and its fees are their lines; its sheet's totals are the
 *      bank's, its equity in its parts.
 *   6. A SAVE AND A LOAD keep every comparative: every row of every
 *      statement built from saved figures is the same to the cent; R1, R2
 *      and the shares read not counted after the load and counted a month on.
 *   7. THE FORMATS: each sector reads as the kind of business it declares.
 *
 * Both cities cause what is checked: a month with the outside lines, debt
 * falling due within a year, the interest split counted, bonds repaid.
 *
 * BATCH S2 (0.7.75) adds, in the same sections and three of its own:
 *   1. the outside lines' new four - the bonds written off, the bank's fees,
 *      the mortgages' premiums and the bonds' issuing costs (F2, R5);
 *   4. THE EQUITY STATEMENT IN COLUMNS (R3, R6): its share capital starts
 *      at last month's paid-in and ends at this month's, closing on its own
 *      lines - the founders' shares, issued, bought back - so the register's
 *      money moves exactly as the month's books say; every row's columns
 *      add to its figure; the sheet's land is its square feet at its price;
 *   6. a save keeps the columns and the register's paid-in, R7 counts again
 *      two months on, and a FORMAT-32 SAVE loads with its paid-in derived and
 *      said so, closes, and saved again in this build's format loads back to
 *      the cent;
 *   8. THE DEBT SCHEDULE (R7): each kind's start, borrowed, repaid, written
 *      off and end close, and from close to close the running totals are
 *      the month's own loans, bonds, repayments and write-offs - and since
 *      0.7.102 (A16, F-S1-2) the sheet is read at the close, so nothing is
 *      lent after it and section 3 holds its debt, land and buildings to
 *      what the business owes and holds as the month closes;
 *   9. EVERY GATE (R4): each building's first failure is its build card's
 *      gate, a gate it has not is no gate, its cost is paid for in full.
 */
public class SectorStatementCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    /** A cent, in the model's thousands. */
    static final double CENT = .00001;

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

    /** The tallies of one city's run, by identity. */
    static final Map<String, Tally> tallies = new LinkedHashMap<>();

    static Tally t(String name) { return tallies.computeIfAbsent(name, k -> new Tally()); }

    /** What the cities caused: sector-months with each condition. */
    static int statements, outsideMonths, splitCounted, splitMissing, owedMissing, dueSoonMonths, residualShown, bankMonths,
            bondsRepaidMonths, bondsRepaidOver, equityStatements;

    /** ...and S2's (0.7.75): sector-months with each of the new lines, and with each flow of the debt schedule. */
    static int upfrontMonths, bondCostMonths, foundedMonths, issuedMonths, boughtBackMonths, derivedMonths,
            stockMonths, landMonths, buildingsMonths, abroadMonths, schedules, scheduleBorrowed, scheduleRepaid,
            scheduleWrittenOff, lentAfterSheet;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("sectorstatementcheck");

        Game player = playersCity(root);
        Game plain = plainCity(root);
        everyMonth();
        savedAndLoaded(root, player);
        anOlderSave(player);
        theFormats(player);
        theSchedule();
        everyGate(player, plain);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ------------------------------------------------------- the two cities */

    /** The test player's city (LongPlaytest.main()'s founding and its rhythm), audited every month: thirty years. */
    static Game playersCity(Path root) {
        out.println("--- the cities: the test player's, 360 months, and a plain founding, 120 ---");
        Path dir = root.resolve("player");
        Game g = new Game(new GameFiles(dir.resolve("data"), dir.resolve("no-legacy")), LongPlaytest.founding());
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
                months(g, 3);
                LongPlaytest.villageBuild(g, "House", 20);
                months(g, 4);
                LongPlaytest.villageBuild(g, "Convenience Store", 2);
                LongPlaytest.villageBuild(g, "Construction Depot", 1);
                months(g, 5);
                LongPlaytest.advise(g);
                months(g, 6);
                int stop = 0;
                while (g.getMonth() < 360) {
                    stop++;
                    int skip = switch (stop % 6) { case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120; };
                    months(g, Math.min(skip, 360 - g.getMonth()));
                    if (g.getMonth() >= 360) break;
                    for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                        if (LongPlaytest.advise(g) == null) break;
                        months(g, 1);
                    }
                    months(g, 2);
                }
            });
        } finally {
            LongPlaytest.out = was;
        }
        out.printf("    (the test player's city: %d months, %,d people)%n", g.getMonth(),
                g.getPopulationManager().getPopulation());
        return g;
    }

    /** SectorBooksCheck's city: a plain founding, played on, audited every month. */
    static Game plainCity(Path root) {
        bondsSoonBefore.clear();
        Path dir = root.resolve("plain");
        Game g = new Game(new GameFiles(dir.resolve("data"), dir.resolve("no-legacy")));
        quietly(() -> {
            g.newGame();
            for (int i = 0; i < 120; i++) {
                g.toggleNextMonth();
                audit(g);
            }
        });
        out.printf("    (the plain founding: %d months, %,d people)%n", g.getMonth(), g.getPopulationManager().getPopulation());
        return g;
    }

    /** Months played one at a time, each audited. */
    static void months(Game g, int n) {
        for (int i = 0; i < n; i++) {
            LongPlaytest.run(g, 1);
            audit(g);
        }
    }

    /* --------------------------------------------------------- one month */

    /** Last month's bonds falling due within a year, by sector: what this month's bond repayments must be inside. */
    static final Map<String, Double> bondsSoonBefore = new LinkedHashMap<>();

    /** Every sector's four statements and the bank's two, held to the model. */
    static void audit(Game g) {
        SectorBooks books = g.getSectorBooks();
        for (Sector s : g.getSectors().all()) {
            SectorBooks.SectorMonth now = books.get(s), then = books.previous(s);
            if (now.isEmpty()) continue;
            statements++;
            String at = s.key() + " m" + now.month();
            SectorBooks.Debt debt = books.debt(s.key()), debtThen = books.debtBefore(s.key());

            /* ----- 1. the income statement ----- */
            SectorStatements.Table inc = SectorStatements.income(s.statementFormat(), now, then, debt);
            t("1 profit before tax is preTaxIncome").near(inc.now(SectorStatements.PRE_TAX), now.preTaxIncome(), at);
            t("1 profit for the month is netIncome").near(inc.now(SectorStatements.PROFIT), now.netIncome(), at);
            t("1 revenue after sales tax").near(inc.now(SectorStatements.NET_REVENUE), now.revenue() - now.salesTaxPaid(), at);
            if (inc.row(SectorStatements.GROSS) != null) {
                t("1 gross").near(inc.now(SectorStatements.GROSS), now.revenue() - now.salesTaxPaid() - now.inputs(), at);
            }
            t("1 operating is the model's operating income less property and sales tax").near(
                    inc.now(SectorStatements.OPERATING), now.operatingIncome() - now.propertyTax() - now.salesTaxPaid(), at);
            if (!then.isEmpty()) {
                t("1 last month's profit before tax is last month's preTaxIncome").near(
                        inc.then(SectorStatements.PRE_TAX), then.preTaxIncome(), at);
                t("1 last month's profit is last month's netIncome").near(inc.then(SectorStatements.PROFIT), then.netIncome(), at);
            }
            double outside = now.fromTheCity() + now.arrearsPaid() + now.depositInterest() + now.bondCoupons()
                    + now.foreignInterest() + now.forgiven() + now.writtenOff() - now.stolen()
                    // ...and since 0.7.75 the bonds written off, and what its borrowing cost up front (F2, R5).
                    + now.bondsWrittenOff() - now.loanFees() - now.premiums() - now.bondCosts();
            t("1 the total result is the profit and the outside lines").near(SectorStatements.result(inc),
                    now.netIncome() + outside, at);
            if (inc.row(SectorStatements.RESULT) != null) outsideMonths++;
            if (now.loanFees() + now.premiums() > CENT) upfrontMonths++;
            // R5: a bond's issuing costs are the face it sold less what it handed it - something, when it sold any.
            t("1 R5: its bonds' issuing costs are never under nothing, and something whenever it sold a bond").near(
                    now.bondCosts() >= -CENT && (now.bondsIssued() <= CENT || now.bondCosts() > 0) ? 0 : 1, 0, at);
            if (now.bondCosts() > CENT) bondCostMonths++;
            if (debt != null && debt.interest() != null) {
                splitCounted++;
                double parts = 0;
                for (SectorStatements.Row r : inc.note(SectorStatements.FINANCE_NOTE)) parts += r.now();
                t("1 the finance costs' note (R1) adds to the line").near(parts, inc.now(SectorStatements.INTEREST), at);
            } else {
                splitMissing++;
            }
            if (debt == null || debt.owed() == null) owedMissing++;

            /* ----- 2. the cash flow ----- */
            SectorStatements.Table cash = SectorStatements.cashFlow(now, then);
            double sections = cash.now(SectorStatements.FROM_OPERATING) + cash.now(SectorStatements.FROM_INVESTING)
                    + cash.now(SectorStatements.FROM_FINANCING);
            t("2 the three sections are the net change").near(sections, cash.now(SectorStatements.NET_CHANGE), at);
            t("2 ...and with the cash at the start and unexplained() are the cash at the end, the cash").near(
                    sections + now.openingCash() + now.unexplained(), now.cash(), at);
            t("2 the statement's cash at the end is the cash").near(cash.now(SectorStatements.CASH_END), now.cash(), at);
            t("2 NOT ACCOUNTED FOR is unexplained()").near(cash.now(SectorStatements.UNEXPLAINED), now.unexplained(), at);
            if (!then.isEmpty()) {
                t("2 last month's cash at the end is last month's cash").near(cash.then(SectorStatements.CASH_END), then.cash(), at);
            }
            if (cash.row(SectorStatements.UNEXPLAINED).shown()) residualShown++;

            /* ----- 3. the classified sheet ----- */
            SectorStatements.Table sheet = SectorStatements.sheet(now, then, debt, debtThen);
            t("3 current and long-lived are the total assets").near(
                    sheet.now(SectorStatements.CURRENT) + sheet.now(SectorStatements.LONG),
                    sheet.now(SectorStatements.TOTAL_ASSETS), at);
            t("3 total assets are the model's").near(sheet.now(SectorStatements.TOTAL_ASSETS), now.totalAssets(), at);
            t("3 total liabilities are the model's").near(sheet.now(SectorStatements.TOTAL_LIABILITIES),
                    now.totalLiabilities(), at);
            if (debt != null && debt.owed() != null) {
                t("3 due within a year and due later are the liabilities").near(
                        sheet.now(SectorStatements.SOON) + sheet.now(SectorStatements.LATER),
                        sheet.now(SectorStatements.TOTAL_LIABILITIES), at);
                t("3 R2's kinds are the debt line").near(debt.owedTotal(), now.bondsPayable(), at);
                boolean ordered = true;
                for (int k = 0; k < SectorBooks.Debt.KINDS.length; k++) {
                    ordered &= debt.withinYear()[k] <= debt.withinFive()[k] + CENT
                            && debt.withinFive()[k] <= Math.max(0, debt.owed()[k]) + CENT && debt.withinYear()[k] >= -CENT;
                }
                t("3 R2: nothing falls due within a year that does not within five, nor more than is owed").near(
                        ordered ? 0 : 1, 0, at);
                if (debt.withinYearTotal() > CENT) dueSoonMonths++;
                // ...and the bonds this month's settle repaid were inside last month's year.
                Double soonBefore = bondsSoonBefore.get(s.key());
                if (soonBefore != null && now.bondsRepaid() > CENT) {
                    bondsRepaidMonths++;
                    if (now.bondsRepaid() > soonBefore + CENT) bondsRepaidOver++;
                }
                bondsSoonBefore.put(s.key(), debt.withinYear()[1]);
            } else {
                bondsSoonBefore.remove(s.key());
            }
            t("3 equity is the model's").near(sheet.now(SectorStatements.TOTAL_EQUITY), now.equity(), at);
            // ...AT THE MONTH'S CLOSE (0.7.102, Jerus's A16; F-S1-2): the debt as it stands, and the land and
            // buildings the month's loans paid for - read where the audit runs, straight after the close.
            EconomyManager em = g.getEconomyManager();
            t("3 the sheet's debt is what it owes at the month's close").near(now.bondsPayable(),
                    em.getBusinessDebtManager().getPrincipal(s.key()), at);
            t("3 ...its buildings and land what it holds at the close").near(now.buildings() + now.land(),
                    g.getBuildingManager().getBuildingsValueBySector(s.key()) + em.landValueOf(s), at);
            t("3 liabilities and equity are the total assets").near(sheet.now(SectorStatements.TOTAL_CLAIMS),
                    sheet.now(SectorStatements.TOTAL_ASSETS), at);
            if (!then.isEmpty()) {
                t("3 last month's column is last month's sheet: its assets").near(
                        sheet.then(SectorStatements.TOTAL_ASSETS), then.totalAssets(), at);
                t("3 ...its liabilities").near(sheet.then(SectorStatements.TOTAL_LIABILITIES), then.totalLiabilities(), at);
                t("3 ...and its claims").near(sheet.then(SectorStatements.TOTAL_CLAIMS), then.totalAssets(), at);
            }

            /* ----- 4. the equity statement ----- */
            SectorStatements.Table eq = SectorStatements.equity(now, then);
            if (eq != null) {
                equityStatements++;
                t("4 the equity statement starts at last month's equity").near(eq.now(SectorStatements.EQ_START), then.equity(), at);
                t("4 ...and ends at this month's").near(eq.now(SectorStatements.EQ_END), now.equity(), at);
                t("4 its owners' lines are equityRaised less dividendsPaid less sharesBoughtBack").near(
                        eq.now(SectorStatements.EQ_ISSUED) + eq.now(SectorStatements.EQ_PAID) + eq.now(SectorStatements.EQ_BOUGHT),
                        now.equityRaised() - now.dividendsPaid() - now.sharesBoughtBack(), at);
                t("4 its profit and outside lines are the income statement's").near(
                        eq.now(SectorStatements.EQ_PROFIT) + eq.now(SectorStatements.EQ_OUTSIDE), SectorStatements.result(inc), at);
                // ...in columns (0.7.75, R3): share capital from last month's paid-in to this month's, on its own lines.
                SectorStatements.Row start = eq.row(SectorStatements.EQ_START), end = eq.row(SectorStatements.EQ_END);
                t("4 R3: share capital starts at last month's paid-in").near(start.part(SectorStatements.CAPITAL), then.paidIn(), at);
                t("4 R3: ...ends at this month's").near(end.part(SectorStatements.CAPITAL), now.paidIn(), at);
                t("4 R3: ...and closes on the founders' shares, the shares issued and those bought back: NOT ACCOUNTED FOR"
                        + " nothing").near(eq.now(SectorStatements.EQ_CAPITAL_REST), 0, at);
                t("4 R3: what it kept ends at its equity less its share capital").near(end.part(SectorStatements.KEPT),
                        now.equity() - now.paidIn(), at);
                double worstRow = 0;
                for (SectorStatements.Row r : eq.rows()) {
                    if (r.parts() == null) continue;
                    worstRow = Math.max(worstRow, Math.abs(r.part(0) + r.part(1) - r.now()));
                }
                t("4 R3: every row's two columns add to its figure").near(worstRow, 0, at);
                if (now.founded() > CENT) foundedMonths++;
                if (now.equityRaised() > CENT) issuedMonths++;
                if (now.sharesBoughtBack() > CENT) boughtBackMonths++;
                if (now.paidInDerived()) derivedMonths++;
                // ...and R6: what the prices did, each part where it was read.
                if (Math.abs(eq.now(SectorStatements.EQ_STOCK)) > CENT) stockMonths++;
                if (Math.abs(eq.now(SectorStatements.EQ_LAND)) > CENT) landMonths++;
                if (Math.abs(eq.now(SectorStatements.EQ_BUILDINGS)) > CENT) buildingsMonths++;
                if (Math.abs(eq.now(SectorStatements.EQ_ABROAD)) > CENT) abroadMonths++;
            }
            // The register's paid-in as the books took it (R3), and the sheet's prices (R6).
            int c = Equity.indexOf(s.key());
            Equity register = g.getEquity();
            t("4 R3: the books' share capital is the register's: its founders' book and what it raised, less what its"
                    + " buybacks paid").near(now.paidIn(), register.getFoundersBook(c) + register.getLifetimeRaisedHome(c)
                    + register.getLifetimeRaisedAbroad(c) - register.getBoughtBackPaid(c), at);
            t("4 R6: the sheet's land is its square feet at its price a square foot").near(now.land(),
                    now.landSqFt() * now.landPrice(), at);
            t("4 R6: its buildings carry at least the materials in them at the sheet's price").near(
                    now.buildings() + CENT >= now.buildingMaterials() * now.materialsPrice() ? 0 : 1, 0, at);
            scheduleOf(s.key(), books, now, at);
        }

        /* ----- 5. the bank ----- */
        Bank bank = g.getBank();
        if (bank != null && bank.isMonthKnown()) {
            bankMonths++;
            String at = "bank m" + g.getMonth();
            SectorStatements.Table bi = SectorStatements.bankIncome(bank);
            t("5 net interest income is NET_INTEREST").near(bi.now(SectorStatements.B_NII), bank.thisMonth(Bank.Line.NET_INTEREST), at);
            t("5 total operating income is REVENUE").near(bi.now(SectorStatements.B_TOI), bank.thisMonth(Bank.Line.REVENUE), at);
            t("5 profit before tax is PRE_TAX").near(bi.now(SectorStatements.B_PRE_TAX), bank.thisMonth(Bank.Line.PRE_TAX), at);
            t("5 profit is NET").near(bi.now(SectorStatements.B_PROFIT), bank.thisMonth(Bank.Line.NET), at);
            t("5 kept in the bank is RETAINED").near(bi.now(SectorStatements.B_KEPT), bank.thisMonth(Bank.Line.RETAINED), at);
            t("5 interest income is INTEREST").near(bi.now(SectorStatements.B_INTEREST), bank.thisMonth(Bank.Line.INTEREST), at);
            t("5 ...every dollar of it by who paid it").near(bi.now(SectorStatements.B_OTHER_INTEREST), 0, at);
            t("5 its fees are FEES").near(bi.now(SectorStatements.B_FEES), bank.thisMonth(Bank.Line.FEES), at);
            if (bank.knowsLastMonth()) {
                t("5 last month's profit is last month's NET").near(bi.then(SectorStatements.B_PROFIT), bank.lastMonth(Bank.Line.NET), at);
                t("5 last month's net interest income is last month's NET_INTEREST").near(
                        bi.then(SectorStatements.B_NII), bank.lastMonth(Bank.Line.NET_INTEREST), at);
            }
            SectorStatements.Table bs = SectorStatements.bankSheet(bank);
            t("5 its sheet's total assets are ASSETS").near(bs.now(SectorStatements.BS_ASSETS), bank.sheet(Bank.Sheet.ASSETS), at);
            t("5 ...every asset on a line").near(bs.now(SectorStatements.BS_ASSETS_REST), 0, at);
            t("5 ...its liabilities LIABILITIES, every one on a line").near(
                    bs.now(SectorStatements.BS_LIABILITIES), bank.sheet(Bank.Sheet.LIABILITIES), at);
            t("5 ...its equity EQUITY").near(bs.now(SectorStatements.BS_EQUITY), bank.sheet(Bank.Sheet.EQUITY), at);
            if (bs.row(SectorStatements.BS_EQUITY_REST) != null) {
                t("5 ...its equity's parts are its equity").near(bs.now(SectorStatements.BS_EQUITY_REST), 0, at);
            }
            t("5 ...and its claims its assets").near(bs.now(SectorStatements.BS_CLAIMS), bs.now(SectorStatements.BS_ASSETS), at);
            if (bank.knowsYearAgo()) {
                t("5 a year ago's column is the sheet a year ago").near(
                        bs.then(SectorStatements.BS_ASSETS), bank.yearAgo(Bank.Sheet.ASSETS), at);
            }
        }
    }

    /* ---------------------------------------------------------------- 8, a month */

    /**
     * The debt schedule (R7), this month: each kind closes from last month's
     * sheet to this month's; and from close to close the running totals are
     * the month's own figures - the loans' principal written (what it was
     * handed and the fees and premiums kept out of it) and the bonds' face,
     * the principal repaid and the bonds redeemed, the loans and the bonds
     * written off.
     */
    static void scheduleOf(String key, SectorBooks books, SectorBooks.SectorMonth now, String at) {
        SectorBooks.Debt d = books.debt(key), b = books.debtBefore(key);
        SectorStatements.Schedule s = SectorStatements.schedule(d, b);
        if (s == null) return;
        schedules++;
        double rest = 0;
        for (double x : s.rest()) rest = Math.max(rest, Math.abs(x));
        t("8 R7: each kind's start, borrowed, repaid, written off and end close: NOT ACCOUNTED FOR nothing").near(rest, 0, at);
        double[] close = new double[3];
        for (int f = 0; f < 3; f++) {
            for (int k = 0; k < SectorBooks.Debt.KINDS.length; k++) close[f] += d.movedAtClose()[f][k] - b.movedAtClose()[f][k];
        }
        t("8 R7: from close to close, what it borrowed is the month's loans' principal and its bonds' face").near(close[0],
                now.borrowed() + now.loanFees() + now.premiums() + now.bondsIssued() + now.bondCosts(), at);
        t("8 R7: ...what it repaid, the principal that fell due and the bonds redeemed").near(close[1],
                now.repaid() + now.bondsRepaid(), at);
        t("8 R7: ...and what was written off, its loans' and its bonds'").near(close[2],
                now.writtenOff() + now.bondsWrittenOff(), at);
        if (SectorStatements.Schedule.total(s.borrowed()) > CENT) scheduleBorrowed++;
        if (SectorStatements.Schedule.total(s.repaid()) > CENT) scheduleRepaid++;
        if (SectorStatements.Schedule.total(s.writtenOff()) > CENT) scheduleWrittenOff++;
        if (SectorStatements.Schedule.total(s.after()) > CENT) lentAfterSheet++;
    }

    /* ---------------------------------------------------------------- 1-5 */

    static void everyMonth() {
        out.printf("    (%,d sector statements and %,d of the bank's; %,d sector-months with outside lines, %,d with debt"
                        + " due within a year, %,d with NOT ACCOUNTED FOR shown)%n", statements, bankMonths, outsideMonths,
                dueSoonMonths, residualShown);
        for (Map.Entry<String, Tally> e : tallies.entrySet()) {
            if (e.getKey().startsWith("8")) continue;   // section 8's own
            assertTrue(e.getKey().substring(2) + ": " + e.getValue().words(), e.getValue().missed == 0);
        }
        assertTrue("fixture: the cities had months with the outside lines, debt falling due within a year, and the bank's",
                outsideMonths > 0 && dueSoonMonths > 0 && bankMonths > 0);
        assertTrue(String.format("R1 and R2 are counted in every sector-month a city plays: R1 in %,d, not in %,d;"
                + " R2 not in %,d", splitCounted, splitMissing, owedMissing), splitCounted > 0 && splitMissing == 0
                && owedMissing == 0);
        assertTrue(String.format("the bonds a settle repaid were inside last month's year (R2): %d sector-months repaid"
                + " bonds, %d more than that", bondsRepaidMonths, bondsRepaidOver), bondsRepaidOver == 0);
        assertTrue("every month with last month's sheet drew an equity statement (" + equityStatements + ")",
                equityStatements > 0);
        out.printf("    (S2: %,d sector-months with fees or premiums kept out of a loan, %,d with bonds' issuing costs; founders'"
                        + " shares in %,d, shares issued in %,d, bought back in %,d; R6's stock in %,d, land in %,d, buildings in"
                        + " %,d, abroad in %,d)%n", upfrontMonths, bondCostMonths, foundedMonths, issuedMonths, boughtBackMonths,
                stockMonths, landMonths, buildingsMonths, abroadMonths);
        assertTrue("fixture: the cities had months with fees kept out of a loan, bonds' issuing costs, founders' shares,"
                + " shares issued and bought back", upfrontMonths > 0 && bondCostMonths > 0 && foundedMonths > 0
                && issuedMonths > 0 && boughtBackMonths > 0);
        assertTrue("fixture: ...and R6's parts: the stock, the land, the buildings and what is held abroad each moved in some"
                + " month", stockMonths > 0 && landMonths > 0 && buildingsMonths > 0 && abroadMonths > 0);
        assertTrue("a city founded in this build derives no paid-in: " + derivedMonths + " derived sector-months", derivedMonths == 0);
    }

    /* ---------------------------------------------------------------- 6 */

    /** Every row of a table, this month and last, as text to the cent: what a save must keep. */
    static Map<String, double[]> rows(SectorStatements.Table t) {
        Map<String, double[]> m = new LinkedHashMap<>();
        if (t == null) return m;
        for (SectorStatements.Row r : t.rows()) {
            if (r.kind() == SectorStatements.Kind.HEAD) continue;
            m.put(r.id(), r.parts() == null ? new double[] { r.now(), r.then() }
                    : new double[] { r.now(), r.then(), r.part(0), r.part(1) });
        }
        return m;
    }

    /** The rows two tables share, compared to the cent; @return the worst miss, infinite for a figure one knows and the other does not. */
    static double compare(Map<String, double[]> a, Map<String, double[]> b, Set<String> skip) {
        double worst = 0;
        for (Map.Entry<String, double[]> e : a.entrySet()) {
            if (skip.contains(e.getKey())) continue;
            double[] x = e.getValue(), y = b.get(e.getKey());
            if (y == null) { worst = Double.POSITIVE_INFINITY; worstRow = e.getKey() + " missing"; continue; }
            if (x.length != y.length) { worst = Double.POSITIVE_INFINITY; worstRow = e.getKey() + " columns"; continue; }
            for (int i = 0; i < x.length; i++) {
                if (Double.isNaN(x[i]) && Double.isNaN(y[i])) continue;
                double off = Double.isNaN(x[i]) != Double.isNaN(y[i]) ? Double.POSITIVE_INFINITY : Math.abs(x[i] - y[i]);
                if (off > CENT && off > worst) worstRow = String.format("%s %s: %.5f, %.5f", e.getKey(), i == 0 ? "now" : "then " + i, x[i], y[i]);
                worst = Math.max(worst, off);
            }
        }
        return worst;
    }

    /** The row the last comparison missed worst on, for the message. */
    static String worstRow = "";

    /** The rows R2 splits the debt into, which a load does not keep. */
    static final Set<String> SPLIT_ROWS = Set.of(SectorStatements.SOON_HEAD, SectorStatements.DEBT_SOON, SectorStatements.SOON,
            SectorStatements.LATER_HEAD, SectorStatements.LATER, SectorStatements.laterId(0), SectorStatements.laterId(1),
            SectorStatements.laterId(2), SectorStatements.laterId(3), SectorStatements.DEBT_WHOLE);

    static void savedAndLoaded(Path root, Game g) {
        out.println("\n--- 6. a save and a load keep every comparative ---");
        quietly(() -> g.saveGame(10));
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGame(10));
        SectorBooks a = g.getSectorBooks(), b = back.getSectorBooks();
        double worstIncome = 0, worstCash = 0, worstSheet = 0, worstEquity = 0;
        boolean whole = true, notCounted = true;
        int compared = 0;
        for (Sector s : g.getSectors().all()) {
            Sector t = back.getSectors().byKey(s.key());
            SectorBooks.SectorMonth n1 = a.get(s), p1 = a.previous(s), n2 = b.get(t), p2 = b.previous(t);
            if (n1.isEmpty()) continue;
            compared++;
            worstIncome = Math.max(worstIncome, compare(rows(SectorStatements.income(s.statementFormat(), n1, p1, a.debt(s.key()))),
                    rows(SectorStatements.income(t.statementFormat(), n2, p2, b.debt(t.key()))), Set.of()));
            worstCash = Math.max(worstCash, compare(rows(SectorStatements.cashFlow(n1, p1)),
                    rows(SectorStatements.cashFlow(n2, p2)), Set.of()));
            worstEquity = Math.max(worstEquity, compare(rows(SectorStatements.equity(n1, p1)),
                    rows(SectorStatements.equity(n2, p2)), Set.of()));
            SectorStatements.Table loaded = SectorStatements.sheet(n2, p2, b.debt(t.key()), b.debtBefore(t.key()));
            worstSheet = Math.max(worstSheet, compare(rows(SectorStatements.sheet(n1, p1, a.debt(s.key()), a.debtBefore(s.key()))),
                    rows(loaded), SPLIT_ROWS));
            whole &= loaded.row(SectorStatements.DEBT_WHOLE) != null
                    && Math.abs(loaded.now(SectorStatements.DEBT_WHOLE) - n1.bondsPayable()) <= CENT;
            notCounted &= b.debt(t.key()) == null && b.debtBefore(t.key()) == null && b.shares(t.key()) == null;
        }
        assertTrue(String.format("every sector's income statement, both months, is the same after a load (%d sectors; worst %.2g)",
                compared, worstIncome), compared > 0 && worstIncome <= CENT);
        assertTrue(String.format("...its cash flow (worst %.2g)", worstCash), worstCash <= CENT);
        assertTrue(String.format("...its sheet, every line but R2's split (worst %.2g)", worstSheet), worstSheet <= CENT);
        assertTrue(String.format("...and its equity statement, both its columns (worst %.2g)", worstEquity), worstEquity <= CENT);
        // ...and the register's paid-in (0.7.75, R3: SAVE_FORMAT 33), every company.
        double worstPaidIn = 0;
        boolean flags = true;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            Equity x = g.getEquity(), y = back.getEquity();
            worstPaidIn = Math.max(worstPaidIn, Math.abs(x.getFoundersBook(c) - y.getFoundersBook(c)));
            worstPaidIn = Math.max(worstPaidIn, Math.abs(x.getBoughtBackPaid(c) - y.getBoughtBackPaid(c)));
            flags &= x.isPaidInDerived(c) == y.isPaidInDerived(c);
        }
        assertTrue(String.format("the register's founders' books and what its buybacks paid, every company, the same after a"
                + " load (worst %.2g), and none derived (format %d)", worstPaidIn, GameVersion.SAVE_FORMAT),
                worstPaidIn <= CENT && flags && GameVersion.SAVE_FORMAT >= Equity.PAID_IN_FORMAT);
        assertTrue("R1, R2 and the shares are not saved: after a load they read not counted, and the sheet's debt is one line,"
                + " the debt the books saved", notCounted && whole);
        double worstBank = compare(rows(SectorStatements.bankIncome(g.getBank())), rows(SectorStatements.bankIncome(back.getBank())), Set.of());
        assertTrue(String.format("the bank's income statement, both columns, is the same after a load (worst %.2g%s)",
                worstBank, worstBank > CENT ? ", " + worstRow : ""), worstBank <= CENT);
        /*
         * ...AND ITS SHEET, BUT FOR WHAT A LOAD RE-READS. The bank's deposits
         * (the memorandum's two lines) are read again from the city at a load,
         * and its borrowings' split between what its deposits fund and the
         * window follows them: in the test player's city at 364 months $131.9M
         * moved from the one to the other. Its totals do not move, and a year
         * ago's column does not. A finding of this batch (runs/fixS1-notes.md),
         * not of the statements, which print the bank's own lines: pinned here
         * to those four, so anything else that moves is news.
         */
        Map<String, double[]> sheetBefore = rows(SectorStatements.bankSheet(g.getBank())),
                sheetAfter = rows(SectorStatements.bankSheet(back.getBank()));
        Set<String> reread = Set.of(SectorStatements.sheetId(Bank.Sheet.DEPOSIT_FUNDING), SectorStatements.sheetId(Bank.Sheet.WINDOW),
                SectorStatements.sheetId(Bank.Sheet.HOUSEHOLD_DEPOSITS), SectorStatements.sheetId(Bank.Sheet.SECTOR_DEPOSITS));
        double worstSheetBank = compare(sheetBefore, sheetAfter, reread);
        String rest = worstRow;
        double yearAgo = 0;
        for (String id : reread) yearAgo = Math.max(yearAgo, Math.abs(sheetBefore.get(id)[1] - sheetAfter.get(id)[1]));
        assertTrue(String.format("...and its sheet, both columns, but this month's deposit funding, window and deposits, which a"
                        + " load re-reads (worst %.2g%s; those four a year ago %.2g)", worstSheetBank,
                worstSheetBank > CENT ? ", " + rest : "", yearAgo), worstSheetBank <= CENT && yearAgo <= CENT);

        quietly(() -> back.toggleNextMonth());
        boolean counted = true, thenUnknown = true, totalsKnown = true;
        for (Sector t : back.getSectors().all()) {
            SectorBooks.SectorMonth n = back.getSectorBooks().get(t), p = back.getSectorBooks().previous(t);
            if (n.isEmpty()) continue;
            SectorBooks.Debt d = back.getSectorBooks().debt(t.key());
            counted &= d != null && d.interest() != null && d.owed() != null;
            SectorStatements.Table sh = SectorStatements.sheet(n, p, d, back.getSectorBooks().debtBefore(t.key()));
            thenUnknown &= Double.isNaN(sh.then(SectorStatements.DEBT_SOON)) && Double.isNaN(sh.then(SectorStatements.SOON));
            totalsKnown &= Math.abs(sh.then(SectorStatements.TOTAL_LIABILITIES) - p.totalLiabilities()) <= CENT
                    && Math.abs(sh.then(SectorStatements.TOTAL_CLAIMS) - p.totalAssets()) <= CENT;
        }
        assertTrue("a month on, R1 and R2 are counted again, the split's last month reads not counted, and the sheet's"
                + " totals still have last month's figures", counted && thenUnknown && totalsKnown);
        // R7 needs both sheets: not a month on, two.
        boolean noSchedule = true;
        for (Sector t : back.getSectors().all()) {
            noSchedule &= SectorStatements.schedule(back.getSectorBooks().debt(t.key()), back.getSectorBooks().debtBefore(t.key())) == null;
        }
        quietly(() -> back.toggleNextMonth());
        boolean schedule = false;
        for (Sector t : back.getSectors().all()) {
            schedule |= SectorStatements.schedule(back.getSectorBooks().debt(t.key()), back.getSectorBooks().debtBefore(t.key())) != null;
        }
        assertTrue("R7's schedule reads not counted a month after the load, and is counted the month after", noSchedule && schedule);
        int missed = 0, after = 0;
        for (Tally x : tallies.values()) missed += x.missed;
        bondsSoonBefore.clear();
        audit(back);
        for (Tally x : tallies.values()) after += x.missed;
        assertTrue("...and the loaded city's month holds every identity of sections 1 to 5 (" + (after - missed) + " off)",
                after == missed);
    }

    /* ---------------------------------------------------------------- 7 */

    static void theFormats(Game g) {
        out.println("\n--- 7. the formats ---");
        Sectors s = g.getSectors();
        Map<String, SectorStatements.Format> declared = new LinkedHashMap<>();
        declared.put(Sectors.RETAIL, SectorStatements.Format.MERCHANTS);
        declared.put(Sectors.LUXURY_RETAIL, SectorStatements.Format.MERCHANTS);
        declared.put(Sectors.RESTAURANTS, SectorStatements.Format.MERCHANTS);
        declared.put(Sectors.REAL_ESTATE, SectorStatements.Format.LANDLORDS);
        declared.put(Sectors.CONSTRUCTION, SectorStatements.Format.BUILDERS);
        declared.put(Sectors.RAIL, SectorStatements.Format.CARRIERS);
        declared.put(Sectors.BUSINESS_SERVICES, SectorStatements.Format.CARRIERS);
        boolean right = true;
        Set<SectorStatements.Format> used = EnumSet.noneOf(SectorStatements.Format.class);
        for (Sector x : s.all()) {
            SectorStatements.Format f = x.statementFormat();
            used.add(f);
            right &= f == declared.getOrDefault(x.key(), SectorStatements.Format.MAKERS);
        }
        assertTrue("the shops, the luxury shops and the kitchens are merchants, the landlords landlords, the builders builders,"
                + " the railway and the offices carriers, everyone else a maker", right);
        assertTrue("every format but the bank's is some sector's: " + used,
                used.containsAll(EnumSet.complementOf(EnumSet.of(SectorStatements.Format.BANK))));
        Sector landlords = s.realEstate();
        SectorStatements.Table inc = SectorStatements.income(landlords.statementFormat(), g.getSectorBooks().get(landlords),
                g.getSectorBooks().previous(landlords), null);
        SectorStatements.Table maker = SectorStatements.income(SectorStatements.Format.MAKERS, g.getSectorBooks().get(landlords),
                g.getSectorBooks().previous(landlords), null);
        assertTrue("the landlords' statement has no gross line and its middle line is its net operating income; a maker's"
                        + " has gross profit, and both strike the same profit before tax",
                inc.row(SectorStatements.GROSS) == null
                        && inc.row(SectorStatements.OPERATING).label().equals(SectorStatements.Format.LANDLORDS.operating)
                        && maker.row(SectorStatements.GROSS) != null
                        && Math.abs(inc.now(SectorStatements.PRE_TAX) - maker.now(SectorStatements.PRE_TAX)) <= CENT);
        List<SectorStatements.Row> none = SectorStatements.income(SectorStatements.Format.MAKERS,
                SectorBooks.SectorMonth.none("x"), SectorBooks.SectorMonth.none("x"), null).note(SectorStatements.FINANCE_NOTE);
        assertTrue("an income statement with R1 not counted keeps no finance note", none.isEmpty());
    }

    /* ---------------------------------------------------------------- 6, an older save */

    /** The S2 fields a save from before 0.7.75 does not carry in its books' months. */
    static final String[] NEW_FIELDS = { "paidIn", "founded", "paidInDerived", "loanFees", "premiums", "bondCosts",
            "bondsWrittenOff", "landSqFt", "landPrice", "buildingMaterials", "materialsPrice", "stockRevalued", "stockCounted" };

    /**
     * A FORMAT-32 SAVE (0.7.74's shape), made from the test player's city by
     * taking out what 0.7.75 added - the register's three slots a company and
     * the books' new fields - loads with every listed sector's share capital
     * derived (what it raised since founding) and said so; its equity
     * statement closes; a month on it still closes; and saved again, in this
     * build's format (SAVE_FORMAT), and loaded, it is the same to the cent,
     * derived still.
     */
    static void anOlderSave(Game g) throws Exception {
        out.println("\n--- 6b. a format-32 save loads, its paid-in derived; saved again it loads back to the cent ---");
        quietly(() -> g.saveGame(10));
        Path file = g.getGameFiles().saveFile(10);
        JsonObject json = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
        // (At or past the first with it since 0.7.76, whose fuel split took 34: FuelSplit.)
        assertTrue("the save says format " + GameVersion.SAVE_FORMAT + ", at or past the first with paid-in capital ("
                + Equity.PAID_IN_FORMAT + ")", json.get("saveFormat").getAsInt() == GameVersion.SAVE_FORMAT
                && GameVersion.SAVE_FORMAT >= Equity.PAID_IN_FORMAT);
        json.addProperty("saveFormat", Equity.PAID_IN_FORMAT - 1);
        JsonArray reg = json.getAsJsonArray("equity");
        int companies = json.getAsJsonArray("equityKeys").size();
        JsonArray cut = new JsonArray();
        for (int c = 0; c < companies; c++) {
            for (int i = 0; i < Equity.SLOTS_BEFORE_PAID_IN; i++) cut.add(reg.get(c * Equity.SLOTS + i));
        }
        json.add("equity", cut);
        for (String books : new String[] { "sectorBooks", "sectorBooksBefore" }) {
            for (JsonElement m : json.getAsJsonArray(books)) for (String f : NEW_FIELDS) m.getAsJsonObject().remove(f);
        }
        Files.writeString(file, new GsonBuilder().setPrettyPrinting().create().toJson(json), StandardCharsets.UTF_8);
        Game old = new Game(g.getGameFiles());
        quietly(() -> old.loadGame(10));
        assertTrue("a format-32 save loads", old.getLoadFailure() == null || old.getLoadFailure().isEmpty());
        Equity register = old.getEquity();
        boolean derived = true, raised = true, closes = true, said = true;
        int listed = 0;
        for (Sector s : old.getSectors().all()) {
            int c = Equity.indexOf(s.key());
            if (!(register.getShares(c) > 0)) continue;
            listed++;
            derived &= register.isPaidInDerived(c);
            raised &= Math.abs(register.getPaidIn(c) - register.getLifetimeRaisedHome(c) - register.getLifetimeRaisedAbroad(c)) <= CENT;
            SectorBooks.SectorMonth n = old.getSectorBooks().get(s), p = old.getSectorBooks().previous(s);
            said &= n.paidInDerived() && Math.abs(n.paidIn() - register.getPaidIn(c)) <= CENT
                    && SectorStatements.sheet(n, p, null, null).row(SectorStatements.SHARE_CAPITAL).label().contains("derived");
            SectorStatements.Table eq = SectorStatements.equity(n, p);
            closes &= eq == null || Math.abs(eq.now(SectorStatements.EQ_CAPITAL_REST)) <= CENT;
        }
        assertTrue(String.format("...every listed sector's paid-in derived (%d): what it raised since founding, at home and"
                + " abroad", listed), listed > 0 && derived && raised);
        assertTrue("...its books' share capital the register's, marked derived, the sheet's line saying so", said);
        assertTrue("...and the loaded month's equity statement closes its share capital", closes);
        quietly(() -> old.toggleNextMonth());
        boolean closesOn = true, stillDerived = true;
        for (Sector s : old.getSectors().all()) {
            SectorBooks.SectorMonth n = old.getSectorBooks().get(s), p = old.getSectorBooks().previous(s);
            if (!(register.getShares(Equity.indexOf(s.key())) > 0)) continue;
            SectorStatements.Table eq = SectorStatements.equity(n, p);
            closesOn &= eq != null && Math.abs(eq.now(SectorStatements.EQ_CAPITAL_REST)) <= CENT;
            stillDerived &= n.paidInDerived();
        }
        assertTrue("a month on, its equity statement still closes, still derived", closesOn && stillDerived);
        quietly(() -> old.saveGame(10));
        JsonObject again = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGame(10));
        double worst = 0, worstEquity = 0;
        boolean flags = true;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (c != Equity.BANK) worst = Math.max(worst, Math.abs(old.getEquity().getPaidIn(c) - back.getEquity().getPaidIn(c)));
            flags &= old.getEquity().isPaidInDerived(c) == back.getEquity().isPaidInDerived(c);
        }
        for (Sector s : old.getSectors().all()) {
            Sector t = back.getSectors().byKey(s.key());
            SectorBooks.SectorMonth n1 = old.getSectorBooks().get(s), p1 = old.getSectorBooks().previous(s);
            SectorBooks.SectorMonth n2 = back.getSectorBooks().get(t), p2 = back.getSectorBooks().previous(t);
            worst = Math.max(worst, Math.max(Math.abs(n1.paidIn() - n2.paidIn()), Math.abs(p1.paidIn() - p2.paidIn())));
            flags &= n1.paidInDerived() == n2.paidInDerived();
            worstEquity = Math.max(worstEquity, compare(rows(SectorStatements.equity(n1, p1)), rows(SectorStatements.equity(n2, p2)),
                    Set.of()));
        }
        assertTrue(String.format("saved again as format %d and loaded: every paid-in the same to the cent (worst %.2g), derived"
                        + " still, the equity statements the same (worst %.2g)", again.get("saveFormat").getAsInt(), worst,
                worstEquity), again.get("saveFormat").getAsInt() == GameVersion.SAVE_FORMAT && worst <= CENT && flags
                && worstEquity <= CENT);
    }

    /* ---------------------------------------------------------------- 8 */

    static void theSchedule() {
        out.println("\n--- 8. the debt schedule (R7) ---");
        out.printf("    (%,d schedules; borrowed in %,d, repaid in %,d, written off in %,d; lent after the sheet was read in %,d)%n",
                schedules, scheduleBorrowed, scheduleRepaid, scheduleWrittenOff, lentAfterSheet);
        for (Map.Entry<String, Tally> e : tallies.entrySet()) {
            if (!e.getKey().startsWith("8")) continue;
            assertTrue(e.getKey().substring(2) + ": " + e.getValue().words(), e.getValue().missed == 0);
        }
        /*
         * BEFORE 0.7.102: "fixture: ...and lent after a sheet was read", lentAfterSheet > 0 - the sheet was read at
         * the insolvency settle, before the month's building loans. Since (Jerus's A16, F-S1-2) it is read at the
         * close, so the schedule's window is the calendar month and nothing is lent after it.
         */
        assertTrue("fixture: the cities' schedules borrowed, repaid and wrote off",
                scheduleBorrowed > 0 && scheduleRepaid > 0 && scheduleWrittenOff > 0);
        assertTrue("the sheet is read at the month's close: nothing is lent after it, in any schedule (0.7.102)",
                lentAfterSheet == 0);
    }

    /* ---------------------------------------------------------------- 9 */

    /**
     * Every building at every gate (R4), on the test player's city as it
     * stands: each one's first failure is the build card's gate (gate());
     * a gate it has not is NONE - no ore without a site, no licence, no
     * posts, no ground; its owners, its own and what it borrows add to its
     * cost; its payback is its cost over its estimate when it pays. And the
     * spec's F11 caused: with the city's ground taken away, a building that
     * would lose money shows the land and the loss both. On both cities.
     */
    static void everyGate(Game... cities) {
        out.println("\n--- 9. every building at every gate (R4) ---");
        int buildings = 0, first = 0, none = 0, paid = 0, payback = 0, failing = 0;
        int landFirst = 0;
        List<String> both = new ArrayList<>();
        for (Game g : cities) for (Sector s : g.getSectors().all()) {
            for (BuildCard.Appraisal a : BuildCard.appraiseAll(g, s)) {
                buildings++;
                BuildingsTemplate t = a.template();
                BuildCard.Gate gate = BuildCard.gate(g, t, a.owner(), a.estimate());
                BuildCard.Gate mine = a.first();
                if (gate == null ? mine == null : mine != null && mine.kind() == gate.kind()
                        && Math.abs(mine.a() - gate.a()) <= CENT && Math.abs(mine.b() - gate.b()) <= CENT) first++;
                double posts = 0;
                for (JobType job : JobType.values()) posts += t.getJobs(job);
                boolean[] has = { Game.siteOf(t) != null, t.getRequiresLicence() != null, posts > 0, t.getLandSqFt() > 0, true };
                boolean right = true;
                for (int i = 0; i < has.length; i++) right &= has[i] == (a.gates().get(i).mark() != BuildCard.Mark.NONE);
                if (right) none++;
                if (a.owners() >= 0 && a.own() >= 0 && a.borrowed() >= 0
                        && Math.abs(a.owners() + a.own() + a.borrowed() - a.cost()) <= CENT) paid++;
                if (a.estimate() > 0 ? Math.abs(a.payback() - a.cost() / a.estimate()) <= 1e-9 * Math.max(1, a.payback())
                        : Double.isNaN(a.payback())) payback++;
                if (!a.failures().isEmpty()) failing++;
            }
        }
        out.printf("    (%,d buildings in %d cities, %,d failing at some gate)%n", buildings, cities.length, failing);
        assertTrue(String.format("every building's first failure is its build card's gate (%d of %d)", first, buildings),
                buildings > 0 && first == buildings);
        assertTrue(String.format("...a gate it has not is no gate: no ore without a site, no licence, no posts, no ground (%d)",
                none), none == buildings);
        assertTrue(String.format("...its owners, its own and what it borrows add to its cost, none under nothing (%d)", paid),
                paid == buildings);
        assertTrue(String.format("...its payback its cost over its estimate when it pays, never when it does not (%d)", payback),
                payback == buildings);

        // F11, caused: no ground at all, and a building that would lose money.
        boolean putBack = true;
        for (Game g : cities) {
            LandManager ground = g.getLandManager();
            double free = ground.getAvailableSqFt(), allocated = ground.getAllocatedSqFt();
            ground.setAllocatedSqFt(ground.getOwnedSqFt());
            try {
                for (Sector s : g.getSectors().all()) {
                    for (BuildCard.Appraisal a : BuildCard.appraiseAll(g, s)) {
                        if (a.first() != null && a.first().kind() == BuildCard.GateKind.LAND) {
                            landFirst++;
                            if (a.at(BuildCard.GateKind.LOSS) == BuildCard.Mark.FAIL) both.add(a.template().getName());
                        }
                    }
                }
            } finally {
                ground.setAllocatedSqFt(allocated);
            }
            putBack &= Math.abs(ground.getAvailableSqFt() - free) <= CENT;
        }
        assertTrue(String.format("with no ground, %d buildings stop first at the land; %d of them would also lose money, and"
                + " say so (%s)", landFirst, both.size(), both.isEmpty() ? "none" : String.join(", ", both)),
                landFirst > 0 && !both.isEmpty());
        assertTrue("...and the ground put back, each city is as it was", putBack);
    }
}
