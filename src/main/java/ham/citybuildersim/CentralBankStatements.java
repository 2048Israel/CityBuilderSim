package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The central bank's month as formal statements (CBK1): its income statement down to the profit and what the strike did
 * with it, its balance sheet against a year ago, and the money it made and destroyed from M0 as the month opened to M0 as
 * it closed - each a SectorStatements.Table the Bank tab's central bank pages draw through StatementView, as the sectors'
 * and the commercial bank's are drawn - and its holdings by how long they have left, at face and at market.
 *
 * <p>WHY. Jerus, 2026-11-26: "when you click bank you have two options, the commercial bank, and the central bank ...
 * give both the banks proper statements, aka ability to not only view summary but also the statements same way the other
 * sectors, but make sure to really include alot of stuff into the central bank area". The central bank had no page: a
 * card on Finances > Money listed what it holds and the month's money made and destroyed, a smaller one on Government
 * its advances, each figure a getter the card read once, with no last month, no year ago and no statement that added up.
 *
 * <p>PRESENTATION ONLY, SectorStatements' rule. Every bottom line is the bank's own figure - the profit closeMonth()
 * struck, the sheet's totals, getIssued() and getRetired(), M0 - and the subtotals are this class's running sums of its
 * own lines, each with a NOT ACCOUNTED FOR line against the figure it must reach, shown from a dollar: a line missing or
 * counted twice shows there, and CentralBankStatementCheck holds every one of them under a cent. Pure: it reads
 * CentralBank (its ITS OWN STATEMENTS) and DebtManager's book of it, and changes nothing; nothing in the model reads it.
 */
public final class CentralBankStatements {

    private CentralBankStatements() { }

    /** A cent, in the model's thousands: what a statement's NOT ACCOUNTED FOR is held under (CentralBankStatementCheck). */
    public static final double CENT = 1e-5;

    /* ===================== THE INCOME STATEMENT ===================== */

    /** The income statement's row ids, which the screen and CentralBankStatementCheck look a row up by. */
    public static final String I_INCOME_HEAD = "cb.incomeHead", I_WINDOW = "cb.window", I_ADVANCES = "cb.advances",
            I_COUPONS = "cb.coupons", I_GAINS = "cb.gains", I_INCOME = "cb.income", I_EXPENSE_HEAD = "cb.expenseHead",
            I_RESERVES = "cb.reserves", I_EXPENSE = "cb.expense", I_PROFIT_REST = "cb.profitRest", I_PROFIT = "cb.profit",
            I_USE_HEAD = "cb.useHead", I_RECOVERED = "cb.recovered", I_TO_REMIT = "cb.toRemit", I_CARRIED = "cb.carried",
            I_KEPT = "cb.kept", I_MEMO_HEAD = "cb.memoHead", I_REMITTED = "cb.remitted", I_LOSS_CARRIED = "cb.lossCarried",
            I_VAULT_INTEREST = "cb.vaultInterest";

    /**
     * Its statement of profit or loss, this month against last: interest earned at the window, on the treasury's advances
     * and on the paper it holds, and the gains against face on that paper; less the interest it pays on reserves; the
     * PROFIT closeMonth() struck; and what the strike did with it - made good a loss carried, owed the rest to the
     * treasury, or carried a loss - to KEPT, which is nothing in every month: a central bank keeps nothing. Below, for the
     * record: the remittance paid this month (last month's profit), the loss still carried, and what the vault earned,
     * which stays in the vault and is not in the profit remitted (ForeignAccounts.earnVaultInterest()).
     *
     * @param vaultInterest the vault's interest this month in local money (ForeignAccounts.getVaultInterest())
     */
    public static SectorStatements.Table income(CentralBank cb, double vaultInterest) {
        boolean known = cb.knowsLastMonth();
        SectorStatements.Builder b = new SectorStatements.Builder(known);
        b.head(I_INCOME_HEAD, "Interest and gains earned").group();
        line(b, cb, I_WINDOW, "On its advances to the commercial bank, at the window", CentralBank.Line.WINDOW_INTEREST, 1);
        line(b, cb, I_ADVANCES, "On its advances to the treasury", CentralBank.Line.ADVANCES_INTEREST, 1);
        line(b, cb, I_COUPONS, "Coupons on the city's paper it holds", CentralBank.Line.PAPER_COUPONS, 1);
        line(b, cb, I_GAINS, "Gains on its paper against face (losses below nothing)", CentralBank.Line.PAPER_GAINS, 1);
        b.groupTotal(I_INCOME, "Earned");
        b.head(I_EXPENSE_HEAD, "Interest paid").group();
        line(b, cb, I_RESERVES, "On the commercial bank's reserves here", CentralBank.Line.RESERVES_INTEREST, -1);
        b.groupTotal(I_EXPENSE, "Paid");
        double named = cb.thisMonth(CentralBank.Line.WINDOW_INTEREST) + cb.thisMonth(CentralBank.Line.ADVANCES_INTEREST)
                + cb.thisMonth(CentralBank.Line.PAPER_COUPONS) + cb.thisMonth(CentralBank.Line.PAPER_GAINS)
                - cb.thisMonth(CentralBank.Line.RESERVES_INTEREST);
        double namedThen = cb.lastMonth(CentralBank.Line.WINDOW_INTEREST) + cb.lastMonth(CentralBank.Line.ADVANCES_INTEREST)
                + cb.lastMonth(CentralBank.Line.PAPER_COUPONS) + cb.lastMonth(CentralBank.Line.PAPER_GAINS)
                - cb.lastMonth(CentralBank.Line.RESERVES_INTEREST);
        b.residual(I_PROFIT_REST, "NOT ACCOUNTED FOR", cb.getStruckProfit() - named,
                cb.lastMonth(CentralBank.Line.PROFIT) - namedThen);
        b.total(I_PROFIT, "PROFIT FOR THE MONTH");
        b.head(I_USE_HEAD, "What the month's strike did with it");
        line(b, cb, I_RECOVERED, "Made good a loss carried from before", CentralBank.Line.LOSS_RECOVERED, -1);
        line(b, cb, I_TO_REMIT, "Owed to the treasury, paid at the next press", CentralBank.Line.TO_REMIT, -1);
        line(b, cb, I_CARRIED, "A loss, carried to be made good from profit", CentralBank.Line.LOSS_ADDED, 1);
        b.total(I_KEPT, "KEPT BY THE CENTRAL BANK");
        b.head(I_MEMO_HEAD, "For the record, outside the profit");
        b.memo(I_REMITTED, "Paid to the treasury this month: last month's profit", cb.thisMonth(CentralBank.Line.REMITTED),
                cb.lastMonth(CentralBank.Line.REMITTED));
        b.memo(I_LOSS_CARRIED, "A loss still carried, before anything is remitted again",
                cb.sheet(CentralBank.Sheet.LOSS_CARRIED), Double.NaN);
        b.memo(I_VAULT_INTEREST, "The vault's interest: kept in the vault, not remitted", vaultInterest, Double.NaN);
        return b.table("Statement of profit or loss");
    }

    /** A line of its month, signed as it adds: this month and last. */
    private static void line(SectorStatements.Builder b, CentralBank cb, String id, String label, CentralBank.Line line, int sign) {
        b.line(id, label, sign * cb.thisMonth(line), sign * cb.lastMonth(line), 0);
    }

    /* ===================== THE BALANCE SHEET ===================== */

    /** The balance sheet's row ids. */
    public static final String S_ASSETS_HEAD = "cbs.assetsHead", S_WINDOW = "cbs.window", S_TREASURY = "cbs.treasury",
            S_PAPER = "cbs.paper", S_REDEMPTION = "cbs.redemption", S_VAULT = "cbs.vault", S_ASSETS_REST = "cbs.assetsRest",
            S_ASSETS = "cbs.assets", S_LIABILITIES_HEAD = "cbs.liabilitiesHead", S_RESERVES = "cbs.reserves",
            S_CURRENCY = "cbs.currency", S_LIABILITIES_REST = "cbs.liabilitiesRest", S_LIABILITIES = "cbs.liabilities",
            S_EQUITY_HEAD = "cbs.equityHead", S_EQUITY_LINE = "cbs.equityLine", S_EQUITY = "cbs.equity",
            S_CLAIMS = "cbs.claims", S_MEMO_HEAD = "cbs.memoHead", S_DUE = "cbs.due", S_LOSS = "cbs.loss",
            S_AT_MARKET = "cbs.atMarket", S_MARK = "cbs.mark", S_WINDOW_INTEREST_DUE = "cbs.windowInterestDue";

    /**
     * Its statement of financial position, this month against a year ago: what it has lent and holds, what it owes - M0,
     * every dollar it has made and not taken back - and its equity, what it owns past that. Below, for the record: of the
     * equity, the remittance owed the treasury and a loss carried; its paper at market, and the gain or loss the market
     * shows against face, unbooked (DebtManager.centralBankMarketValue()).
     *
     * @param atMarket its paper at market today (DebtManager.centralBankMarketValue())
     * @param mark     ...and against face (DebtManager.centralBankMarkGain())
     */
    public static SectorStatements.Table sheet(CentralBank cb, double atMarket, double mark) {
        SectorStatements.Builder b = new SectorStatements.Builder(cb.knowsYearAgo());
        b.head(S_ASSETS_HEAD, "ASSETS");
        sheetLine(b, cb, S_WINDOW, "Advances to the commercial bank, at the window", CentralBank.Sheet.WINDOW);
        sheetLine(b, cb, S_WINDOW_INTEREST_DUE, "Interest on window advances, still to collect", CentralBank.Sheet.WINDOW_INTEREST_DUE);
        sheetLine(b, cb, S_TREASURY, "Advances to the treasury: what it has printed", CentralBank.Sheet.TREASURY);
        sheetLine(b, cb, S_PAPER, "The city's paper it holds, at face", CentralBank.Sheet.PAPER);
        sheetLine(b, cb, S_REDEMPTION, "Its paper the treasury bought back, still to settle", CentralBank.Sheet.REDEMPTION_DUE);
        sheetLine(b, cb, S_VAULT, "The vault: the foreign reserves, at the month's rate", CentralBank.Sheet.VAULT);
        CentralBank.Sheet[] assets = { CentralBank.Sheet.WINDOW, CentralBank.Sheet.TREASURY, CentralBank.Sheet.PAPER,
                CentralBank.Sheet.REDEMPTION_DUE, CentralBank.Sheet.VAULT, CentralBank.Sheet.WINDOW_INTEREST_DUE };
        b.residual(S_ASSETS_REST, "NOT ACCOUNTED FOR", cb.sheet(CentralBank.Sheet.ASSETS) - sum(cb, assets, false),
                cb.yearAgo(CentralBank.Sheet.ASSETS) - sum(cb, assets, true));
        b.total(S_ASSETS, "TOTAL ASSETS");

        b.restart();
        b.head(S_LIABILITIES_HEAD, "LIABILITIES");
        sheetLine(b, cb, S_RESERVES, "Reserves: the commercial bank's account here", CentralBank.Sheet.RESERVES);
        sheetLine(b, cb, S_CURRENCY, "Currency in circulation", CentralBank.Sheet.CURRENCY);
        CentralBank.Sheet[] owes = { CentralBank.Sheet.RESERVES, CentralBank.Sheet.CURRENCY };
        b.residual(S_LIABILITIES_REST, "NOT ACCOUNTED FOR", cb.sheet(CentralBank.Sheet.LIABILITIES) - sum(cb, owes, false),
                cb.yearAgo(CentralBank.Sheet.LIABILITIES) - sum(cb, owes, true));
        b.total(S_LIABILITIES, "TOTAL LIABILITIES: M0");

        b.head(S_EQUITY_HEAD, "EQUITY").group();
        sheetLine(b, cb, S_EQUITY_LINE, "Its equity: what it owns past what it owes", CentralBank.Sheet.EQUITY);
        b.groupTotal(S_EQUITY, "TOTAL EQUITY");
        b.total(S_CLAIMS, "TOTAL LIABILITIES AND EQUITY");

        b.head(S_MEMO_HEAD, "For the record");
        b.memo(S_DUE, "Of its equity: owed to the treasury at the next press", cb.sheet(CentralBank.Sheet.REMITTANCE_DUE),
                cb.yearAgo(CentralBank.Sheet.REMITTANCE_DUE));
        b.memo(S_LOSS, "Of its equity: a loss carried, to make good from profit", -cb.sheet(CentralBank.Sheet.LOSS_CARRIED),
                -cb.yearAgo(CentralBank.Sheet.LOSS_CARRIED));
        b.memo(S_AT_MARKET, "The city's paper it holds, at what the market would pay", atMarket, Double.NaN);
        b.memo(S_MARK, "...against face: the gain or loss the market shows, unbooked", mark, Double.NaN);
        return b.table("Statement of financial position");
    }

    private static void sheetLine(SectorStatements.Builder b, CentralBank cb, String id, String label, CentralBank.Sheet line) {
        b.line(id, label, cb.sheet(line), cb.yearAgo(line), 0);
    }

    private static double sum(CentralBank cb, CentralBank.Sheet[] lines, boolean yearAgo) {
        double total = 0;
        for (CentralBank.Sheet s : lines) total += yearAgo ? cb.yearAgo(s) : cb.sheet(s);
        return total;
    }

    /* ===================== THE MONEY MADE AND DESTROYED ===================== */

    /** The money statement's row ids. */
    public static final String M_MADE_HEAD = "cbm.madeHead", M_MADE_REST = "cbm.madeRest", M_MADE = "cbm.made",
            M_DESTROYED_HEAD = "cbm.destroyedHead", M_DESTROYED_REST = "cbm.destroyedRest", M_DESTROYED = "cbm.destroyed",
            M_MOVED = "cbm.moved", M_OPENED = "cbm.opened", M_REST = "cbm.rest", M_CLOSED = "cbm.closed";

    /** The money made, by flow: {label, line}, in the order the statement prints them. Every issue() in CentralBank is one. */
    static final Object[][] MADE = {
            { "Lent to the commercial bank at the window", CentralBank.Line.ADVANCED_TO_BANK },
            { "Advanced to the treasury: printed", CentralBank.Line.ADVANCED_TO_TREASURY },
            { "Interest paid on the commercial bank's reserves", CentralBank.Line.RESERVES_INTEREST },
            { "The remittance: last month's profit, to the treasury", CentralBank.Line.REMITTED },
            { "Paper bought from the commercial bank", CentralBank.Line.BOUGHT_FROM_BANK },
            { "Paper bought from the families", CentralBank.Line.BOUGHT_FROM_HOUSEHOLDS },
            { "Paper bought from abroad", CentralBank.Line.BOUGHT_FROM_WORLD },
            { "Paper bought from the treasury at issue, rolling its own", CentralBank.Line.BOUGHT_AT_ISSUE },
            { "Paper bought from the treasury at auction, its auction share", CentralBank.Line.BOUGHT_AT_AUCTION },
            { "Dollars bought for the vault", CentralBank.Line.DOLLARS_BOUGHT },
            { "Paid to the city's gold mines for their gold", CentralBank.Line.GOLD_MINTED } };

    /** ...and destroyed. Every retire() in CentralBank is one. */
    static final Object[][] DESTROYED = {
            { "Repaid by the commercial bank at the window", CentralBank.Line.REPAID_BY_BANK },
            { "Window interest collected from the commercial bank", CentralBank.Line.WINDOW_INTEREST },
            { "Repaid by the treasury", CentralBank.Line.REPAID_BY_TREASURY },
            { "The treasury's interest on its advances", CentralBank.Line.ADVANCES_INTEREST },
            { "Paid it for paper it sold the commercial bank", CentralBank.Line.SOLD_PAPER },
            { "Coupons on the paper it holds", CentralBank.Line.PAPER_COUPONS },
            { "Principal repaid on the paper it holds", CentralBank.Line.PAPER_REDEEMED },
            { "Its paper the treasury bought back", CentralBank.Line.BOUGHT_BACK },
            { "Taken back for the gold the mint paid out", CentralBank.Line.GOLD_REDEEMED } };

    /** A flow's row id on the money statement. */
    public static String moneyId(CentralBank.Line line) { return "cbm." + line.name(); }

    /**
     * The money it made and destroyed this month, against last: every flow that made money (MoneyAudit's ISSUED), every
     * flow that destroyed it (RETIRED), each group against getIssued() and getRetired(); the difference, M0 MOVED; and from
     * M0 as the month opened to M0 as it closed. Only a reform in the month (redenominate()) leaves anything between the
     * two, and it is shown.
     */
    public static SectorStatements.Table money(CentralBank cb) {
        boolean known = cb.knowsLastMonth();
        SectorStatements.Builder b = new SectorStatements.Builder(known);
        b.head(M_MADE_HEAD, "MONEY MADE").group();
        double made = 0, madeThen = 0;
        for (Object[] w : MADE) {
            CentralBank.Line l = (CentralBank.Line) w[1];
            b.line(moneyId(l), (String) w[0], cb.thisMonth(l), cb.lastMonth(l), 0);
            made += cb.thisMonth(l);
            madeThen += cb.lastMonth(l);
        }
        b.residual(M_MADE_REST, "NOT ACCOUNTED FOR", cb.thisMonth(CentralBank.Line.ISSUED) - made,
                cb.lastMonth(CentralBank.Line.ISSUED) - madeThen);
        b.groupTotal(M_MADE, "Money made");
        b.head(M_DESTROYED_HEAD, "MONEY DESTROYED").group();
        double gone = 0, goneThen = 0;
        for (Object[] w : DESTROYED) {
            CentralBank.Line l = (CentralBank.Line) w[1];
            double now = cb.thisMonth(l), then = cb.lastMonth(l);
            if (l == CentralBank.Line.WINDOW_INTEREST) {
                now -= cb.thisMonth(CentralBank.Line.WINDOW_INTEREST_DUE_CHANGE);
                then -= cb.lastMonth(CentralBank.Line.WINDOW_INTEREST_DUE_CHANGE);
            }
            b.line(moneyId(l), (String) w[0], -now, -then, 0);
            gone += now;
            goneThen += then;
        }
        b.residual(M_DESTROYED_REST, "NOT ACCOUNTED FOR", -(cb.thisMonth(CentralBank.Line.RETIRED) - gone),
                -(cb.lastMonth(CentralBank.Line.RETIRED) - goneThen));
        b.groupTotal(M_DESTROYED, "Money destroyed");
        b.subtotal(M_MOVED, "M0 MOVED");
        b.line(M_OPENED, "M0 as the month opened", cb.thisMonth(CentralBank.Line.M0_AT_START),
                cb.lastMonth(CentralBank.Line.M0_AT_START), 0);
        double opened = cb.thisMonth(CentralBank.Line.M0_AT_START);
        double moved = cb.thisMonth(CentralBank.Line.ISSUED) - cb.thisMonth(CentralBank.Line.RETIRED);
        double thenOpened = cb.lastMonth(CentralBank.Line.M0_AT_START);
        double thenMoved = cb.lastMonth(CentralBank.Line.ISSUED) - cb.lastMonth(CentralBank.Line.RETIRED);
        // Last month's close is this month's opening: the figure fileMonth() noted, before anything moved.
        b.residual(M_REST, "A reform's new unit, or NOT ACCOUNTED FOR", cb.m0() - opened - moved, opened - thenOpened - thenMoved);
        b.total(M_CLOSED, "M0 AS THE MONTH CLOSED");
        return b.table("Money made and destroyed");
    }

    /* ===================== THE HOLDINGS' LADDER ===================== */

    /** One rung of the ladder: its words, what it holds there at face and at market, and the gain or loss between. */
    public record Rung(String label, double face, double market, double gain) { }

    /** Its holdings by months left (DebtManager.CENTRAL_BANK_LADDER), each at face and at market; then the whole book as a last rung, "Every piece". */
    public static List<Rung> ladder(DebtManager market) {
        double[] face = market.centralBankLadder(false), value = market.centralBankLadder(true);
        List<Rung> out = new ArrayList<>();
        double f = 0, v = 0;
        for (int k = 0; k < face.length; k++) {
            out.add(new Rung(rungWords(k), face[k], value[k], value[k] - face[k]));
            f += face[k];
            v += value[k];
        }
        out.add(new Rung("Every piece", f, v, v - f));
        return out;
    }

    /** A rung's words: "a year or less", "1 to 2 years", "over 10 years". */
    static String rungWords(int k) {
        int[] r = DebtManager.CENTRAL_BANK_LADDER;
        if (k == 0) return r[0] == 12 ? "a year or less" : r[0] + " months or less";
        if (k >= r.length) return "over " + years(r[r.length - 1]);
        return years(r[k - 1]).replace(" years", "").replace(" year", "") + " to " + years(r[k]);
    }

    private static String years(int months) {
        if (months % 12 != 0) return months + " months";
        int y = months / 12;
        return y + (y == 1 ? " year" : " years");
    }

    /* ===================== FIGURES THE PAGES SHOW BESIDE THE STATEMENTS ===================== */

    /** What the public holds for each dollar the central bank has made: M2 over M0; NaN with nothing made. */
    public static double m2OverM0(double m2, CentralBank cb) {
        return cb.m0() > 0 ? m2 / cb.m0() : Double.NaN;
    }

    /** Inflation against the target, in points: what the rule leans against (positive over it). */
    public static double inflationGap(double inflation, DebtManager market) {
        return inflation - market.getInflationTarget();
    }

    /** The share of the treasury's ceiling it has drawn: its advances over CentralBank.ceiling(); NaN with no ceiling. */
    public static double ceilingUsed(CentralBank cb) {
        return cb.ceiling() > 0 ? cb.getAdvancesToTreasury() / cb.ceiling() : Double.NaN;
    }
}
