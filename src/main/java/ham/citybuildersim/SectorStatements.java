package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A business's month as formal statements (0.7.74): the income statement
 * classified down through gross and operating profit, the balance sheet in
 * current and long-lived parts, the cash flow in three sections, the
 * statement of changes in equity with its remainder named - and the bank's
 * income statement and sheet in a bank's own order. Each is a list of rows
 * the Sectors and Bank screens draw and SectorStatementCheck adds up.
 *
 * WHY. Jerus, 2026-10-07: "make the financial statements of the sectors ui
 * better, cause right now its good, but its not a financial statement, just
 * summarized, so i need both a summarized and a detailed actual statement ...
 * also that means that a banks income statement is different, cause interest
 * income". The pages drew one statement in plain words: no gross profit, no
 * current against long-lived, the month's cash as a flat list of up to twenty
 * flows, equity one plug and debt one line - a summary, not a statement. The
 * project's spec-sector-statements.md is the design (its section 10, batch 1,
 * is this class, and batch 2 (0.7.75) gave it share capital, what the prices
 * did and the debt schedule: R3, R6, R7); its D1-D14 are the orchestrator's,
 * to be confirmed.
 *
 * PRESENTATION ONLY. Every bottom line is the model's own figure: profit
 * before tax is preTaxIncome, the profit netIncome, the cash at the end the
 * cash, the totals the sheet's. What this class adds is the arithmetic in
 * between - the subtotals are its own running sums of its own lines, which is
 * what lets the harness catch a line that is missing or counted twice: a
 * statement that foots only because its total was copied from the model
 * proves nothing.
 *
 * Pure: it reads SectorBooks (and the bank, and since 0.7.75 the register
 * for note 10) and changes nothing; nothing in the model reads it.
 */
public final class SectorStatements {

    private SectorStatements() { }

    /* =====================================================================
       THE FORMATS (spec 4.6)

       One template with a format per kind of business: the format names the
       revenue, the cost of sales and the middle line, and the bottom three
       lines are the model's in every one. Sector.statementFormat() chooses,
       so a new sector says what kind of business it is where it is declared.
       ===================================================================== */

    /** A kind of business, as its income statement reads: what it calls its revenue, its cost of sales, and its middle line. */
    public enum Format {
        MAKERS("Makers", "Revenue", "Cost of sales", "GROSS PROFIT", "OPERATING PROFIT"),
        MERCHANTS("Merchants", "Sales", "Cost of goods sold", "GROSS MARGIN", "OPERATING PROFIT"),
        LANDLORDS("Landlords", "Rents and charges", "Bought in", null, "NET OPERATING INCOME"),
        BUILDERS("Builders", "Contract revenue", "Cost of sales", "GROSS PROFIT ON CONTRACTS", "OPERATING PROFIT"),
        CARRIERS("Carriers and services", "Revenue", "Cost of sales", "GROSS PROFIT", "OPERATING PROFIT"),
        BANK("The bank", "Interest income", null, null, "NET INTEREST INCOME");

        /** The format's name, as the screens say it. */
        public final String word;
        /** The revenue line's label. */
        public final String revenue;
        /** The cost of sales line's label. */
        public final String cost;
        /** The gross line's label, or null for a format with none (the landlords: what they buy in is an operating cost). */
        public final String gross;
        /** The operating line's label. */
        public final String operating;

        Format(String word, String revenue, String cost, String gross, String operating) {
            this.word = word;
            this.revenue = revenue;
            this.cost = cost;
            this.gross = gross;
            this.operating = operating;
        }
    }

    /* =====================================================================
       A STATEMENT AS ROWS
       ===================================================================== */

    /** What a row is: a section's title, a line that adds, a subtotal (a rule over it), a bottom line (a double rule under it), or a figure for the record outside the sums. */
    public enum Kind { HEAD, LINE, SUBTOTAL, TOTAL, MEMO }

    /**
     * A residual line - NOT ACCOUNTED FOR - is shown from a dollar (the
     * cash page's rule since 0.7.30): under that it is the floating point.
     * In the model's thousands.
     */
    public static final double NOTICE = .001;

    /**
     * One row of a statement.
     *
     * @param id       what the screens and the harness find it by
     * @param label    what it says
     * @param kind     what it is
     * @param now      this month, signed as it adds (a cost negative); NaN not known
     * @param then     last month, the same way; NaN not known
     * @param note     the note it opens, or 0
     * @param showFrom a line shows when either month is at least this far from nothing: 0 for every line
     *                 but a residual (NOTICE)
     * @param parts    the row's columns when a statement has more than one - the equity statement's share
     *                 capital and what it kept (0.7.75, R3), adding to `now` - or null
     */
    public record Row(String id, String label, Kind kind, double now, double then, int note, double showFrom,
                      double[] parts) {

        public Row(String id, String label, Kind kind, double now, double then, int note, double showFrom) {
            this(id, label, kind, now, then, note, showFrom, null);
        }

        /** A line shows when either month, or any of its columns, is not nothing - today's rule; a head, a subtotal and a total always. */
        public boolean shown() {
            if (kind != Kind.LINE && kind != Kind.MEMO) return true;
            if (beyond(now, showFrom) || beyond(then, showFrom)) return true;
            if (parts != null) for (double p : parts) if (beyond(p, showFrom)) return true;
            return false;
        }

        /** One of its columns; NaN without them. */
        public double part(int i) {
            return parts == null || i < 0 || i >= parts.length ? Double.NaN : parts[i];
        }

        private static boolean beyond(double v, double from) {
            return Double.isFinite(v) && (from > 0 ? Math.abs(v) >= from : v != 0);
        }
    }

    /**
     * A statement: its title, its rows in order, whether last month is
     * known, and its notes - each the rows a note number opens into, this
     * month's only (D12: SectorMonth keeps no per-good maps, and the notes
     * answer "what is this made of", a question about now).
     */
    public record Table(String title, List<Row> rows, boolean thenKnown, Map<Integer, List<Row>> notes) {

        /** The row with this id, or null. */
        public Row row(String id) {
            for (Row r : rows) if (r.id().equals(id)) return r;
            return null;
        }

        /** This month's figure on a row; NaN with no such row. */
        public double now(String id) {
            Row r = row(id);
            return r == null ? Double.NaN : r.now();
        }

        /** ...and last month's. */
        public double then(String id) {
            Row r = row(id);
            return r == null ? Double.NaN : r.then();
        }

        /** The rows a note opens into; empty for a note it does not keep rows for. */
        public List<Row> note(int n) {
            List<Row> l = notes.get(n);
            return l == null ? List.of() : l;
        }

        /** The largest figure the statement shows, either month or any column: what its units are chosen by (D2). */
        public double largest() {
            double most = 0;
            for (Row r : rows) {
                if (!r.shown() || r.kind() == Kind.HEAD) continue;
                if (Double.isFinite(r.now())) most = Math.max(most, Math.abs(r.now()));
                if (Double.isFinite(r.then())) most = Math.max(most, Math.abs(r.then()));
                if (r.parts() != null) for (double p : r.parts()) if (Double.isFinite(p)) most = Math.max(most, Math.abs(p));
            }
            return most;
        }
    }

    /**
     * D2: a statement is in $ thousands - the model's own unit - until its
     * largest figure passes seven digits, and then in $ millions, so the
     * landlords' $1.2B a month does not run to ten. In the model's thousands.
     */
    public static final double THOUSANDS_UNTIL = 10_000_000;

    /** Whether a statement is printed in millions (D2). */
    public static boolean inMillions(Table t) {
        return t.largest() >= THOUSANDS_UNTIL;
    }

    /**
     * The builder: lines add to a running total from the top and to the open
     * group's; a subtotal prints the one, a group total the other. Last
     * month's add the same way, and anything unknown stays unknown (NaN).
     */
    static final class Builder {
        private final List<Row> rows = new ArrayList<>();
        private final Map<Integer, List<Row>> notes = new LinkedHashMap<>();
        private double runNow, runThen, groupNow, groupThen;
        /** The running total of each column, for a statement in columns (split()). */
        private final double[] runParts = new double[2];
        private final boolean known;

        Builder(boolean known) { this.known = known; }

        private double then(double v) { return known ? v : Double.NaN; }

        Builder head(String id, String label) { return head(id, label, 0); }

        Builder head(String id, String label, int note) {
            rows.add(new Row(id, label, Kind.HEAD, Double.NaN, Double.NaN, note, 0));
            return this;
        }

        /** A new group: its total counts from here. */
        Builder group() {
            groupNow = 0;
            groupThen = 0;
            return this;
        }

        /** A new running total: the next section of the statement adds from nothing (the sheet's liabilities). */
        Builder restart() {
            runNow = 0;
            runThen = 0;
            return group();
        }

        Builder line(String id, String label, double now, double then, int note) {
            return add(new Row(id, label, Kind.LINE, now, then(then), note, 0));
        }

        /** A residual: shown from NOTICE, and counted like any line. */
        Builder residual(String id, String label, double now, double then) {
            return add(new Row(id, label, Kind.LINE, now, then(then), 0, NOTICE));
        }

        private Builder add(Row r) {
            rows.add(r);
            runNow += r.now();
            runThen += r.then();
            groupNow += r.now();
            groupThen += r.then();
            return this;
        }

        /** The running total, under a rule. */
        Builder subtotal(String id, String label) {
            rows.add(new Row(id, label, Kind.SUBTOTAL, runNow, runThen, 0, 0));
            return this;
        }

        /** The running total as a bottom line; last month's from `fallback` when the lines could not say it. */
        Builder total(String id, String label, double fallback) {
            double then = Double.isNaN(runThen) && known ? fallback : runThen;
            rows.add(new Row(id, label, Kind.TOTAL, runNow, then, 0, 0));
            return this;
        }

        Builder total(String id, String label) { return total(id, label, Double.NaN); }

        /** The open group's total, under a rule. */
        Builder groupTotal(String id, String label) {
            rows.add(new Row(id, label, Kind.SUBTOTAL, groupNow, groupThen, 0, 0));
            return this;
        }

        /** A line in columns: share capital and what it kept, adding to the row's figure (0.7.75, R3); this month only. */
        Builder split(String id, String label, double capital, double kept, int note) {
            return split(id, label, capital, kept, note, 0);
        }

        /** ...and a residual in columns, shown from NOTICE. */
        Builder splitResidual(String id, String label, double capital, double kept) {
            return split(id, label, capital, kept, 0, NOTICE);
        }

        private Builder split(String id, String label, double capital, double kept, int note, double showFrom) {
            runParts[0] += capital;
            runParts[1] += kept;
            return add(new Row(id, label, Kind.LINE, capital + kept, Double.NaN, note, showFrom, new double[] { capital, kept }));
        }

        /** The running totals in columns, as a bottom line. */
        Builder splitTotal(String id, String label) {
            rows.add(new Row(id, label, Kind.TOTAL, runNow, Double.NaN, 0, 0, runParts.clone()));
            return this;
        }

        /** A figure for the record: printed, added to nothing. */
        Builder memo(String id, String label, double now, double then) {
            rows.add(new Row(id, label, Kind.MEMO, now, then(then), 0, 0));
            return this;
        }

        Builder note(int n, List<Row> lines) {
            notes.put(n, lines);
            return this;
        }

        double runNow() { return runNow; }

        Table table(String title) {
            return new Table(title, Collections.unmodifiableList(rows), known, Collections.unmodifiableMap(notes));
        }
    }

    /* =====================================================================
       THE INCOME STATEMENT (spec 4.1): STATEMENT OF PROFIT OR LOSS

       Revenue less the sales tax it remitted (D4: revenue is booked net of
       VAT, Sector.bank()'s own note), less its cost of sales, is GROSS
       PROFIT; less wages, power, water, repairs and property tax (D5) is
       OPERATING PROFIT; less finance costs is PROFIT BEFORE TAX - which must
       be preTaxIncome to the cent; less business tax is PROFIT FOR THE MONTH,
       netIncome, what the tax, the dividend and the six-months-losing rule are
       struck on. Then the flows that move its equity and reach no statement
       (F1, D6), and the TOTAL RESULT. The model's operatingIncome (before
       property and sales tax) is not a line: the screen keeps it behind the
       operating line's (i).
       ===================================================================== */

    /** The statement of profit or loss's row ids, which the screens and SectorStatementCheck look a row up by (Table.row()). */
    public static final String REVENUE = "revenue", SALES_TAX = "salesTax", NET_REVENUE = "netRevenue",
            INPUTS = "inputs", GROSS = "gross", PAYROLL = "payroll", ELECTRICITY = "electricity", WATER = "water",
            MAINTENANCE = "maintenance", PROPERTY_TAX = "propertyTax", OPERATING = "operating", INTEREST = "interest",
            PRE_TAX = "preTax", TAX = "tax", PROFIT = "profit", OUTSIDE = "outside", RESULT = "result",
            // ...and since 0.7.102 (A16) what its borrowing cost it up front, a sixtieth a month, beside the interest.
            BORROWING_COSTS = "borrowingCosts";

    /** The outside-the-trading-result lines' ids, in their order (F1; since 0.7.75 the bonds written off and F2's three). */
    public static final String SUBSIDY = "subsidy", ARREARS = "arrears", DEPOSIT_INTEREST = "depositInterest",
            COUPONS = "coupons", FOREIGN_INTEREST = "foreignInterest", FORGIVEN = "forgiven",
            WRITTEN_OFF = "writtenOff", BONDS_WRITTEN_OFF = "bondsWrittenOff", STOLEN = "stolen",
            LOAN_FEES = "loanFees", PREMIUMS = "premiums", BOND_COSTS = "bondCosts";

    /** The finance costs' note. */
    public static final int FINANCE_NOTE = 5;
    /** The outside-the-trading-result note. */
    public static final int OUTSIDE_NOTE = 6;

    /**
     * The flows that move a business's equity and reach no statement (F1),
     * each signed as it moves the equity: the subsidy and the city's
     * arrears, the bank's interest on its till, the coupons it is paid, the
     * interest abroad rolled into what it holds there, the overdraft a
     * restructure forgave, the loans its lenders wrote off, and the theft.
     * {label, id} order, now and then.
     *
     * ...AND SINCE 0.7.75, the bonds its holders wrote off in a default
     * (the loans' twin, which S1 left in the remainder because it was not
     * saved), and what its borrowing cost it up front (F2, R5): the fee the
     * bank kept out of a loan, a mortgage's insurance premium, a bond's
     * issuing costs - each paid out of the proceeds, so it owes the whole
     * principal and its cash had the rest. Presentation only: none of them
     * was taxed or in what its dividend is struck on (F1's and F2's model
     * questions) - until 0.7.102, when Jerus's A16 made the three deductible
     * expenses: carried as an asset from the month they are paid and
     * expensed over each debt's life inside the profit (Sector, WHAT ITS
     * BORROWING COST IT UP FRONT), so they move its equity through the
     * profit and are outside nothing. Their three lines read nothing on a
     * month struck since (SectorMonth.borrowingCostsDeferred()), and what
     * an older save's months paid, which moved its equity then, as before.
     */
    static double[] outside(SectorBooks.SectorMonth m) {
        boolean deferred = m.borrowingCostsDeferred();
        return new double[] { m.fromTheCity(), m.arrearsPaid(), m.depositInterest(), m.bondCoupons(),
                m.foreignInterest(), m.forgiven(), m.writtenOff(), m.bondsWrittenOff(), -m.stolen(),
                deferred ? 0 : -m.loanFees(), deferred ? 0 : -m.premiums(), deferred ? 0 : -m.bondCosts() };
    }

    /** The outside lines' row ids, in outside()'s order. */
    static final String[] OUTSIDE_IDS = { SUBSIDY, ARREARS, DEPOSIT_INTEREST, COUPONS, FOREIGN_INTEREST, FORGIVEN,
            WRITTEN_OFF, BONDS_WRITTEN_OFF, STOLEN, LOAN_FEES, PREMIUMS, BOND_COSTS };

    /** ...and their labels, in the same order. */
    static final String[] OUTSIDE_LABELS = { "Subsidy from the city", "Arrears the city paid",
            "Interest on its bank balance", "Coupons on the bonds it holds",
            "Interest abroad, rolled into its holdings", "Overdraft forgiven at a restructure",
            "Loans its lenders wrote off", "Bonds its holders wrote off", "Stolen from its till",
            "Fees the bank kept out of its loans", "Its mortgages' insurance premiums", "Its bonds' issuing costs" };

    /** The outside lines together, this month: what the equity statement names in one line. */
    public static double outsideTotal(SectorBooks.SectorMonth m) {
        double sum = 0;
        for (double v : outside(m)) sum += v;
        return sum;
    }

    /** One business's income statement, this month against last, its finance costs' note from R1 when counted. */
    public static Table income(Format f, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then,
                               SectorBooks.Debt debt) {
        Builder b = new Builder(!then.isEmpty());
        b.line(REVENUE, f.revenue, now.revenue(), then.revenue(), 1);
        b.line(SALES_TAX, now.salesTaxPaid() < 0 ? "Sales tax refunded" : "Sales tax remitted",
                -now.salesTaxPaid(), -then.salesTaxPaid(), 2);
        b.subtotal(NET_REVENUE, "Revenue after sales tax");
        b.line(INPUTS, f.cost, -now.inputs(), -then.inputs(), 3);
        if (f.gross != null) b.subtotal(GROSS, f.gross);
        b.line(PAYROLL, "Wages", -now.payroll(), -then.payroll(), 4);
        b.line(ELECTRICITY, "Electricity", -now.electricity(), -then.electricity(), 0);
        b.line(WATER, "Water", -now.water(), -then.water(), 0);
        b.line(MAINTENANCE, "Repairs", -now.maintenance(), -then.maintenance(), 0);
        b.line(PROPERTY_TAX, "Property tax", -now.propertyTax(), -then.propertyTax(), 0);
        b.subtotal(OPERATING, f.operating);
        b.line(INTEREST, "Finance costs", -now.interest(), -then.interest(), FINANCE_NOTE);
        // ...and what its borrowing cost it up front, over each debt's life (0.7.102, A16): only when either month had some.
        if (now.borrowingCosts() != 0 || then.borrowingCosts() != 0) {
            b.line(BORROWING_COSTS, "Borrowing costs, over each debt's life", -now.borrowingCosts(), -then.borrowingCosts(), 0);
        }
        b.subtotal(PRE_TAX, "PROFIT BEFORE TAX");
        b.line(TAX, "Business tax", -now.tax(), -then.tax(), 0);
        b.total(PROFIT, "PROFIT FOR THE MONTH");

        // Outside the trading result (F1, D6): only when something is, either month.
        double[] o = outside(now), p = outside(then);
        boolean any = false;
        for (int i = 0; i < o.length; i++) any |= o[i] != 0 || (!then.isEmpty() && p[i] != 0);
        if (any) {
            b.head(OUTSIDE, "Outside the trading result", OUTSIDE_NOTE);
            for (int i = 0; i < o.length; i++) b.line(OUTSIDE_IDS[i], OUTSIDE_LABELS[i], o[i], p[i], 0);
            b.total(RESULT, "TOTAL RESULT FOR THE MONTH");
        }

        // Note 5: the finance costs by instrument, as the statement was struck (R1).
        if (debt != null && debt.interest() != null) {
            List<Row> parts = new ArrayList<>();
            for (int k = 0; k < SectorBooks.Debt.KINDS.length; k++) {
                parts.add(new Row("interest." + k, SectorBooks.Debt.KINDS[k], Kind.LINE, -debt.interest()[k],
                        Double.NaN, 0, 0));
            }
            b.note(FINANCE_NOTE, parts);
        }
        return b.table("Statement of profit or loss");
    }

    /** The income statement's bottom line as the trading result: the result with the outside lines, or the profit when there are none. */
    public static double result(Table income) {
        double r = income.now(RESULT);
        return Double.isNaN(r) ? income.now(PROFIT) : r;
    }

    /* =====================================================================
       THE BALANCE SHEET (spec 4.2): STATEMENT OF FINANCIAL POSITION

       Current assets (cash, stock at today's price, what its buyers owe it)
       and long-lived ones (land, buildings, what it holds abroad, other
       businesses' bonds); what it owes within a year (its suppliers, and the
       debt falling due inside twelve months - R2) and later (its debt by
       kind, less that); its owners' equity in two parts since 0.7.75 (R3,
       D11): its share capital, paid in - derived at the load of a save from
       before it was kept, and saying so - and what it kept and the model
       revalued, the remainder. Without R2 - the month after a load - the
       debt is one line and the sheet says when it falls due is not counted
       yet.
       ===================================================================== */

    /** The balance sheet's row ids, which the screens and SectorStatementCheck look a row up by. */
    public static final String ASSETS_HEAD = "assetsHead", CURRENT_HEAD = "currentHead", CASH = "cash",
            STOCK = "stock", RECEIVABLES = "receivables", CURRENT = "current", LONG_HEAD = "longHead", LAND = "land",
            BUILDINGS = "buildings", ABROAD = "abroad", BONDS_HELD = "bondsHeld", LONG = "long",
            TOTAL_ASSETS = "totalAssets", LIABILITIES_HEAD = "liabilitiesHead", SOON_HEAD = "soonHead",
            SUPPLIERS = "suppliers", DEBT_SOON = "debtSoon", SOON = "soon", LATER_HEAD = "laterHead",
            LATER = "later", DEBT_WHOLE = "debtWhole", TOTAL_LIABILITIES = "totalLiabilities",
            EQUITY_HEAD = "equityHead", SHARE_CAPITAL = "shareCapital", RETAINED = "retained",
            TOTAL_EQUITY = "totalEquity", TOTAL_CLAIMS = "totalClaims",
            // ...and since 0.7.102 (A16) what its borrowing cost it up front and it is still to expense.
            BORROWING_TO_EXPENSE = "borrowingToExpense";

    /** The stock's note, the buildings' and what it holds abroad - and since 0.7.75 its share capital's (R3). */
    public static final int STOCK_NOTE = 7, BUILDINGS_NOTE = 8, ABROAD_NOTE = 9, CAPITAL_NOTE = 10;

    /** A kind of debt's line on the sheet, due later: "later.0" for bank loans, in Debt.KINDS' order. */
    public static String laterId(int kind) { return "later." + kind; }

    /** One business's balance sheet, as at the month's close against last month's, its debt by when it falls due from R2 when counted. */
    public static Table sheet(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then,
                              SectorBooks.Debt debt, SectorBooks.Debt debtThen) {
        boolean known = !then.isEmpty();
        Builder b = new Builder(known);
        b.head(ASSETS_HEAD, "ASSETS");
        b.head(CURRENT_HEAD, "Current assets").group();
        b.line(CASH, "Cash", now.cash(), then.cash(), 0);
        b.line(STOCK, "Stock, at today's price", now.inventory(), then.inventory(), STOCK_NOTE);
        b.line(RECEIVABLES, "Owed by its buyers", now.tradeReceivables(), then.tradeReceivables(), 0);
        b.groupTotal(CURRENT, "Total current assets");
        b.head(LONG_HEAD, "Long-lived assets").group();
        b.line(LAND, "Land", now.land(), then.land(), 0);
        b.line(BUILDINGS, "Buildings, at cost", now.buildings(), then.buildings(), BUILDINGS_NOTE);
        b.line(ABROAD, "Held abroad", now.foreignAssets(), then.foreignAssets(), ABROAD_NOTE);
        b.line(BONDS_HELD, "Other businesses' bonds", now.bondAssets(), then.bondAssets(), 0);
        if (now.borrowingCostsToExpense() != 0 || then.borrowingCostsToExpense() != 0) {
            b.line(BORROWING_TO_EXPENSE, "Borrowing costs still to expense", now.borrowingCostsToExpense(),
                    then.borrowingCostsToExpense(), 0);
        }
        b.groupTotal(LONG, "Total long-lived assets");
        b.total(TOTAL_ASSETS, "TOTAL ASSETS");

        b.restart();
        b.head(LIABILITIES_HEAD, "LIABILITIES");
        boolean split = debt != null && debt.owed() != null;
        boolean splitThen = debtThen != null && debtThen.owed() != null;
        if (split) {
            b.head(SOON_HEAD, "Due within a year").group();
            b.line(SUPPLIERS, "Owed to its suppliers", now.tradePayables(), then.tradePayables(), 0);
            b.line(DEBT_SOON, "Debt falling due within the year", debt.withinYearTotal(),
                    splitThen ? debtThen.withinYearTotal() : Double.NaN, 0);
            b.groupTotal(SOON, "Total due within a year");
            b.head(LATER_HEAD, "Due later").group();
            for (int k = 0; k < SectorBooks.Debt.KINDS.length; k++) {
                b.line(laterId(k), SectorBooks.Debt.KINDS[k], debt.owed()[k] - debt.withinYear()[k],
                        splitThen ? debtThen.owed()[k] - debtThen.withinYear()[k] : Double.NaN, 0);
            }
            b.groupTotal(LATER, "Total due later");
        } else {
            b.line(SUPPLIERS, "Owed to its suppliers", now.tradePayables(), then.tradePayables(), 0);
            b.line(DEBT_WHOLE, "Loans and bonds", now.bondsPayable(), then.bondsPayable(), 0);
        }
        b.total(TOTAL_LIABILITIES, "TOTAL LIABILITIES", then.totalLiabilities());
        double liabilitiesThen = b.runThen;

        b.head(EQUITY_HEAD, "EQUITY").group();
        b.line(SHARE_CAPITAL, now.paidInDerived() ? "Share capital, derived" : "Share capital, paid in", now.paidIn(),
                then.paidIn(), CAPITAL_NOTE);
        b.line(RETAINED, "Retained earnings and revaluation", now.retained(), then.retained(), 0);
        b.groupTotal(TOTAL_EQUITY, "TOTAL EQUITY");
        b.total(TOTAL_CLAIMS, "TOTAL LIABILITIES AND EQUITY",
                (Double.isNaN(liabilitiesThen) ? then.totalLiabilities() : liabilitiesThen) + then.equity());
        return b.table("Statement of financial position");
    }

    /* =====================================================================
       THE CASH FLOWS (spec 4.3): INDIRECT, IN THREE SECTIONS

       Every term of SectorMonth.unexplained() lands in exactly one section,
       so the three plus the cash at the start are the cash at the end, and
       NOT ACCOUNTED FOR is unexplained() itself, shown only when it is not
       nothing. Interest received is operating (IAS 7 allows it); the subsidy
       and the arrears are grants, operating; the forgiven overdraft is
       financing, the creditors' money.
       ===================================================================== */

    /** The cash flow statement's row ids, which the screens and SectorStatementCheck look a row up by. */
    public static final String OPERATING_HEAD = "operatingHead", NET_INCOME = "netIncome",
            PAID_EARLIER = "paidEarlier", BORROWING_PAID_EARLIER = "borrowingPaidEarlier",
            TRADE_CREDIT = "tradeCredit", CASH_DEPOSIT_INTEREST = "cash.depositInterest",
            CASH_COUPONS = "cash.coupons", CASH_SUBSIDY = "cash.subsidy", CASH_ARREARS = "cash.arrears",
            CASH_STOLEN = "cash.stolen", FROM_OPERATING = "fromOperating", INVESTING_HEAD = "investingHead",
            PREMISES = "premises", SALVAGE = "salvage", BONDS_BOUGHT = "bondsBought",
            SENT_ABROAD = "sentAbroad", FROM_INVESTING = "fromInvesting", FINANCING_HEAD = "financingHead",
            BORROWED = "borrowed", REPAID = "repaid", BONDS_ISSUED = "bondsIssued", BONDS_REPAID = "bondsRepaid",
            SHARES_ISSUED = "sharesIssued", DIVIDENDS = "dividends", BOUGHT_BACK = "boughtBack",
            CASH_FORGIVEN = "cash.forgiven", FROM_FINANCING = "fromFinancing", NET_CHANGE = "netChange",
            CASH_START = "cashStart", UNEXPLAINED = "unexplained", CASH_END = "cashEnd",
            RECORD_HEAD = "recordHead", INTEREST_PAID = "interestPaid", TAX_PAID = "taxPaid",
            INTEREST_ABROAD = "interestAbroad", FREE_CASH = "freeCash";

    /** One business's cash flows, this month against last. */
    public static Table cashFlow(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        Builder b = new Builder(!then.isEmpty());
        double premisesNow = now.spentOnBuildings() - now.salvage(), premisesThen = then.spentOnBuildings() - then.salvage();
        boolean buyer = now.tradePayables() > 0 || then.tradePayables() > 0
                || (now.tradeReceivables() == 0 && then.tradeReceivables() == 0);

        b.head(OPERATING_HEAD, "Cash flows from operating").group();
        b.line(NET_INCOME, "Profit for the month", now.netIncome(), then.netIncome(), 0);
        b.line(PAID_EARLIER, "Stock used, paid for in an earlier month", now.paidEarlier(), then.paidEarlier(), 0);
        // ...and its borrowing's costs expensed, paid when it borrowed (0.7.102, A16).
        if (now.borrowingCosts() != 0 || then.borrowingCosts() != 0) {
            b.line(BORROWING_PAID_EARLIER, "Borrowing costs, paid when it borrowed", now.borrowingCosts(),
                    then.borrowingCosts(), 0);
        }
        b.line(TRADE_CREDIT, buyer ? "Its suppliers' credit, net" : "Its buyers' credit, net",
                now.tradeCredit(), then.tradeCredit(), 0);
        b.line(CASH_DEPOSIT_INTEREST, "Interest on its bank balance", now.depositInterest(), then.depositInterest(), 0);
        b.line(CASH_COUPONS, "Coupons on the bonds it holds", now.bondCoupons(), then.bondCoupons(), 0);
        b.line(CASH_SUBSIDY, "Subsidy from the city", now.fromTheCity(), then.fromTheCity(), 0);
        b.line(CASH_ARREARS, "Arrears the city paid", now.arrearsPaid(), then.arrearsPaid(), 0);
        b.line(CASH_STOLEN, "Stolen from its till", -now.stolen(), -then.stolen(), 0);
        b.groupTotal(FROM_OPERATING, "NET CASH FROM OPERATING");

        b.head(INVESTING_HEAD, "Cash flows from investing").group();
        b.line(PREMISES, premisesNow >= 0 ? "Its own premises" : "Buildings sold back", -premisesNow, -premisesThen, 0);
        b.line(SALVAGE, (now.salvage() != 0 ? now.salvage() : then.salvage()) > 0 ? "Material bought from scrapped plant"
                : "Scrapped plant's material, sold", -now.salvage(), -then.salvage(), 0);
        b.line(BONDS_BOUGHT, now.bondsBought() >= 0 ? "Other businesses' bonds bought" : "Other businesses' bonds sold",
                -now.bondsBought(), -then.bondsBought(), 0);
        b.line(SENT_ABROAD, now.investedAbroad() >= 0 ? "Sent abroad" : "Brought home from abroad",
                -now.investedAbroad(), -then.investedAbroad(), 0);
        b.groupTotal(FROM_INVESTING, "NET CASH FROM INVESTING");

        b.head(FINANCING_HEAD, "Cash flows from financing").group();
        b.line(BORROWED, "Borrowed, after fees", now.borrowed(), then.borrowed(), 0);
        b.line(REPAID, "Loans repaid", -now.repaid(), -then.repaid(), 0);
        b.line(BONDS_ISSUED, "Raised on bonds, after their costs", now.bondsIssued(), then.bondsIssued(), 0);
        b.line(BONDS_REPAID, "Bonds repaid", -now.bondsRepaid(), -then.bondsRepaid(), 0);
        b.line(SHARES_ISSUED, "Shares issued", now.equityRaised(), then.equityRaised(), 0);
        b.line(DIVIDENDS, "Paid to its shareholders", -now.dividendsPaid(), -then.dividendsPaid(), 0);
        b.line(BOUGHT_BACK, "Its own shares bought back", -now.sharesBoughtBack(), -then.sharesBoughtBack(), 0);
        b.line(CASH_FORGIVEN, "Overdraft forgiven at a restructure", now.forgiven(), then.forgiven(), 0);
        b.groupTotal(FROM_FINANCING, "NET CASH FROM FINANCING");

        b.subtotal(NET_CHANGE, "NET CHANGE IN CASH");
        b.line(CASH_START, "Cash at the start", now.openingCash(), then.openingCash(), 0);
        b.residual(UNEXPLAINED, "NOT ACCOUNTED FOR", now.unexplained(), then.unexplained());
        b.total(CASH_END, "CASH AT THE END");

        b.head(RECORD_HEAD, "For the record");
        b.memo(INTEREST_PAID, "Interest paid, inside the profit", now.interest(), then.interest());
        b.memo(TAX_PAID, "Business tax paid, inside the profit", now.tax(), then.tax());
        b.memo(INTEREST_ABROAD, "Interest abroad, rolled in: no cash", now.foreignInterest(), then.foreignInterest());
        b.memo(FREE_CASH, "Free cash flow: operating less premises", freeCashOf(now), freeCashOf(then));
        return b.table("Statement of cash flows");
    }

    /** Free cash flow: the operating section less what its premises and scrapped plant took (the investing section's buildings). */
    public static double freeCashOf(SectorBooks.SectorMonth m) {
        return m.netIncome() + m.paidEarlier() + m.borrowingCosts() + m.tradeCredit() + m.depositInterest()
                + m.bondCoupons() + m.fromTheCity() + m.arrearsPaid() - m.stolen() - m.spentOnBuildings();
    }

    /* =====================================================================
       THE STATEMENT OF CHANGES IN EQUITY (spec 4.2), IN COLUMNS (0.7.75)

       Share capital, what it kept and the model revalued, and the total. At
       the start; the month's profit and what moved it outside the trading
       result (what it kept); its founders' shares, issued against its book
       (from what it kept to share capital, the total unmoved); shares issued
       and bought back (share capital: a buyback all of what it paid, as the
       bank takes one off its paid-in); paid to its shareholders (what it
       kept); then what the prices did (R6) - its stock at the start at
       today's prices, its land and the materials in its buildings at
       today's prices, what it holds abroad revalued - and the rest: whatever
       else the model moves equity by and names nowhere, worked out (a
       building bought for more or less than the sheet carries it at, a bond
       bought off its face). Share capital is the register's at each close
       (SectorMonth.paidIn), so its column closes on its own lines - a
       residual there is NOT ACCOUNTED FOR, shown from NOTICE; what it kept is
       the rest of the equity. This month only, because the month before
       last's sheet is not kept.
       ===================================================================== */

    /** The statement of changes in equity's row ids, which the screens and SectorStatementCheck look a row up by. */
    public static final String EQ_START = "eq.start", EQ_PROFIT = "eq.profit", EQ_OUTSIDE = "eq.outside",
            EQ_FOUNDED = "eq.founded", EQ_ISSUED = "eq.issued", EQ_PAID = "eq.paid", EQ_BOUGHT = "eq.bought",
            EQ_STOCK = "eq.stock", EQ_LAND = "eq.land", EQ_BUILDINGS = "eq.buildings", EQ_ABROAD = "eq.abroad",
            EQ_REVALUED = "eq.revalued", EQ_CAPITAL_REST = "eq.capitalRest", EQ_END = "eq.end";

    /** The equity statement's two columns, each row's parts: share capital, and what it kept and revalued. */
    public static final int CAPITAL = 0, KEPT = 1;

    /** R6's four parts, ids: what the prices did to what it began the month with. */
    public static final String[] REVALUED_IDS = { EQ_STOCK, EQ_LAND, EQ_BUILDINGS, EQ_ABROAD };

    /** Last month's stock at this month's prices, less at last month's - NaN when last month's stock was not in memory (a load). */
    public static double stockRevalued(SectorBooks.SectorMonth now) {
        return now.stockCounted() ? now.stockRevalued() : Double.NaN;
    }

    /** Last month's land at this month's price a square foot, less at last month's - NaN when either month's price was not read. */
    public static double landRevalued(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        return now.landPrice() > 0 && then.landPrice() > 0 ? then.landSqFt() * (now.landPrice() - then.landPrice()) : Double.NaN;
    }

    /** ...the materials in last month's buildings at this month's price a unit, less at last month's. */
    public static double buildingsRevalued(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        return now.materialsPrice() > 0 && then.materialsPrice() > 0
                ? then.buildingMaterials() * (now.materialsPrice() - then.materialsPrice()) : Double.NaN;
    }

    /** ...and what it holds abroad, less what it sent there and the interest rolled in: the rate it is valued at. */
    public static double abroadRevalued(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        return now.foreignAssets() - then.foreignAssets() - now.investedAbroad() - now.foreignInterest();
    }

    /** The month's statement of changes in equity, in columns; null when last month's sheet is not on file. */
    public static Table equity(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        if (then.isEmpty()) return null;
        Builder b = new Builder(false);
        b.split(EQ_START, "At the start of the month", then.paidIn(), then.retained(), 0);
        b.split(EQ_PROFIT, "Profit for the month", 0, now.netIncome(), 0);
        b.split(EQ_OUTSIDE, "Outside the trading result", 0, outsideTotal(now), OUTSIDE_NOTE);
        b.split(EQ_FOUNDED, "Its founders' shares, issued against its book", now.founded(), -now.founded(), 0);
        b.split(EQ_ISSUED, "Shares issued", now.equityRaised(), 0, 0);
        b.split(EQ_PAID, "Paid to its shareholders", 0, -now.dividendsPaid(), 0);
        b.split(EQ_BOUGHT, "Its own shares bought back", -now.sharesBoughtBack(), 0, 0);
        double[] revalued = { stockRevalued(now), landRevalued(now, then), buildingsRevalued(now, then),
                abroadRevalued(now, then) };
        String[] words = { "Its stock at the start, at today's prices", "Its land at the start, at today's price",
                "Its buildings at the start, at today's materials", "What it holds abroad, revalued" };
        for (int i = 0; i < revalued.length; i++) {
            if (Double.isFinite(revalued[i])) b.split(REVALUED_IDS[i], words[i], 0, revalued[i], 0);
        }
        b.split(EQ_REVALUED, "And the rest", 0, now.retained() - b.runParts[KEPT], 0);
        b.splitResidual(EQ_CAPITAL_REST, "NOT ACCOUNTED FOR", now.paidIn() - b.runParts[CAPITAL], 0);
        b.splitTotal(EQ_END, "AT THE END OF THE MONTH");
        return b.table("Statement of changes in equity");
    }

    /** What the equity statement says the prices did, together (R6's four parts, those counted): the summary's bridge. */
    public static double revaluedTotal(Table equity) {
        double sum = 0;
        for (String id : REVALUED_IDS) {
            double v = equity.now(id);
            if (Double.isFinite(v)) sum += v;
        }
        return sum;
    }

    /**
     * Note 10, its share capital's parts as the register keeps them (R3):
     * the book its founders' shares were issued against, what it raised at
     * home and abroad, and what its buybacks paid. This month only (D12),
     * read off the register as it stands; empty for the bank and a company
     * not listed.
     */
    public static List<Row> capitalNote(Equity register, String company) {
        int c = Equity.indexOf(company);
        if (register == null || c < 0 || c == Equity.BANK) return List.of();
        List<Row> out = new ArrayList<>();
        out.add(new Row("capital.founders", "Its founders' shares, at the book they were issued against", Kind.LINE,
                register.getFoundersBook(c), Double.NaN, 0, 0));
        out.add(new Row("capital.home", "Subscribed by the city's households", Kind.LINE,
                register.getLifetimeRaisedHome(c), Double.NaN, 0, 0));
        out.add(new Row("capital.abroad", "Subscribed abroad", Kind.LINE, register.getLifetimeRaisedAbroad(c), Double.NaN, 0, 0));
        out.add(new Row("capital.bought", "Less its own shares bought back, at what it paid", Kind.LINE,
                -register.getBoughtBackPaid(c), Double.NaN, 0, 0));
        return out;
    }

    /* =====================================================================
       THE DEBT SCHEDULE (spec 4.3; 0.7.75, R7): BY KIND, FROM SHEET TO SHEET

       What it owed of each kind at last month's sheet (R2 then), what it
       borrowed, repaid and had written off since, and what it owes at this
       month's (R2 now) - with the rate it pays, the month's interest (R1) and
       when the last of it falls due. The three flows are the differences of
       the running totals BusinessDebtManager keeps where each moves, read
       with R2 - so the window is the sheet's. Until 0.7.102 that was from
       last month's settle to this month's (F-S1-2: the sheet read its debt
       before the month's building loans, which were next month's here, as on
       the sheet), and `after` was what it borrowed this month after the
       sheet was read. Since 0.7.102 (Jerus's A16) the sheet is read at the
       month's close (Game.recordMonth()), so the window is the calendar
       month, close to close, and `after` is nothing. A kind's residual is
       NOT ACCOUNTED FOR, which the harness holds at nothing. Null when either
       month's R2 or totals are not counted: the month after a load, and the
       month after that for last month's.
       ===================================================================== */

    /** One sector's debt schedule: each in DEBT_KINDS' order. */
    public record Schedule(double[] start, double[] borrowed, double[] repaid, double[] writtenOff, double[] end,
                           double[] rest, double[] interest, double[] rate, double[] runsTo, double[] after) {

        /** A column's total. */
        public static double total(double[] a) {
            double s = 0;
            if (a != null) for (double v : a) if (Double.isFinite(v)) s += v;
            return s;
        }
    }

    public static Schedule schedule(SectorBooks.Debt now, SectorBooks.Debt before) {
        if (now == null || before == null || now.owed() == null || before.owed() == null
                || now.moved() == null || before.moved() == null) return null;
        int n = SectorBooks.Debt.KINDS.length;
        double[] start = new double[n], borrowed = new double[n], repaid = new double[n], off = new double[n],
                end = new double[n], rest = new double[n], after = new double[n];
        for (int k = 0; k < n; k++) {
            start[k] = before.owed()[k];
            end[k] = now.owed()[k];
            borrowed[k] = now.moved()[BusinessDebtManager.BORROWED][k] - before.moved()[BusinessDebtManager.BORROWED][k];
            repaid[k] = now.moved()[BusinessDebtManager.REPAID][k] - before.moved()[BusinessDebtManager.REPAID][k];
            off[k] = now.moved()[BusinessDebtManager.WRITTEN_OFF][k] - before.moved()[BusinessDebtManager.WRITTEN_OFF][k];
            rest[k] = end[k] - (start[k] + borrowed[k] - repaid[k] - off[k]);
            after[k] = now.movedAtClose() == null ? 0
                    : now.movedAtClose()[BusinessDebtManager.BORROWED][k] - now.moved()[BusinessDebtManager.BORROWED][k];
        }
        return new Schedule(start, borrowed, repaid, off, end, rest,
                now.interest() == null ? new double[n] : now.interest().clone(),
                now.rate() == null ? new double[n] : now.rate().clone(),
                now.runsTo() == null ? new double[n] : now.runsTo().clone(), after);
    }

    /* =====================================================================
       THE RATIOS (spec 4.1-4.3), each NaN where it means nothing
       ===================================================================== */

    /** A share of a base, NaN on no base. */
    public static double of(double part, double base) {
        return Double.isFinite(part) && Double.isFinite(base) && base != 0 ? part / base : Double.NaN;
    }

    /** Interest cover on the statement's operating profit (D10, F6): how many times its trading profit, after property and sales tax, covers its finance costs. */
    public static double interestCover(Table income) {
        double interest = -income.now(INTEREST);
        return interest > 0 ? income.now(OPERATING) / interest : Double.NaN;
    }

    /** Business tax over profit before tax: NaN on a loss, where the rate means nothing. */
    public static double effectiveTax(Table income) {
        double pbt = income.now(PRE_TAX);
        return pbt > 0 ? -income.now(TAX) / pbt : Double.NaN;
    }

    /** Current assets over what falls due within a year; NaN while R2 is not counted or nothing falls due. */
    public static double currentRatio(Table sheet) {
        double soon = sheet.now(SOON);
        return soon > 0 ? sheet.now(CURRENT) / soon : Double.NaN;
    }

    /** ...without the stock: what it could pay at once. */
    public static double quickRatio(Table sheet) {
        double soon = sheet.now(SOON);
        return soon > 0 ? (sheet.now(CURRENT) - sheet.now(STOCK)) / soon : Double.NaN;
    }

    /** Return on equity a year: twelve months at this month's profit, over the average of the two months' equity. */
    public static double returnOnEquity(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        if (then.isEmpty()) return Double.NaN;
        double average = (now.equity() + then.equity()) / 2;
        return average > 0 ? now.netIncome() * 12 / average : Double.NaN;
    }

    /** Cash on hand in days of what trading costs it: its cash over a thirtieth of the month's operating costs, interest and tax. */
    public static double daysOfCash(SectorBooks.SectorMonth m) {
        double spend = m.operatingCost() + m.propertyTax() + m.interest() + m.tax() + Math.max(0, m.salesTaxPaid());
        return spend > 0 ? m.cash() / (spend / 30) : Double.NaN;
    }

    /** Debt over a year of trading cash: what it owes over twelve months of the operating section, NaN when that is nothing or less. */
    public static double debtYears(Table cash, SectorBooks.SectorMonth m) {
        double year = cash.now(FROM_OPERATING) * 12;
        return year > 0 ? m.bondsPayable() / year : Double.NaN;
    }

    /* =====================================================================
       THE BANK (spec 4.5): ITS OWN FORMAT, ON ITS OWN TAB (D9)

       A bank's income statement leads with interest: interest income by who
       paid it less interest expense is NET INTEREST INCOME; with its fees,
       the desk and its gains, TOTAL OPERATING INCOME; less provisions, after
       provisions; less staff and branches, PROFIT BEFORE TAX; less the tax,
       paid a month in arrears, PROFIT; less what its owners took, KEPT IN THE
       BANK. Every total here is a sum of Bank.Lines, and SectorStatementCheck
       holds each to the model's own (NET_INTEREST, REVENUE, PRE_TAX, NET,
       RETAINED). Its sheet most liquid first, loans gross less the
       allowance, equity in its parts, the deposits it holds as a memorandum.
       ===================================================================== */

    /** The bank's statement of profit or loss's row ids, which the screens and SectorStatementCheck look a row up by. */
    public static final String B_INCOME_HEAD = "b.incomeHead", B_OTHER_INTEREST = "b.otherInterest",
            B_INTEREST = "b.interest", B_EXPENSE_HEAD = "b.expenseHead", B_EXPENSE = "b.expense",
            B_NII = "b.nii", B_FEES_HEAD = "b.feesHead", B_FEES = "b.fees", B_TOI = "b.toi",
            B_PROVISIONS = "b.provisions", B_AFTER_PROVISIONS = "b.afterProvisions", B_PAYROLL = "b.payroll",
            B_UPKEEP = "b.upkeep", B_PRE_TAX = "b.preTax", B_TAX = "b.tax", B_PROFIT = "b.profit",
            B_DIVIDENDS = "b.dividends", B_BUYBACKS = "b.buybacks", B_KEPT = "b.kept";

    /** The interest income lines: {label, line}. */
    static final Object[][] BANK_INTEREST = {
            { "From the businesses", Bank.Line.FROM_BUSINESSES },
            { "On the businesses' bonds it holds", Bank.Line.FROM_BONDS },
            { "From the families", Bank.Line.FROM_HOUSEHOLDS },
            { "From the city, on its paper", Bank.Line.FROM_CITY },
            { "The discount on the city's paper, earned", Bank.Line.DISCOUNT },
            { "From the carry trade", Bank.Line.FROM_CARRY },
            { "On its reserves at the central bank", Bank.Line.FROM_RESERVES } };

    /** ...and the fees. */
    static final Object[][] BANK_FEES = {
            { "On the families' accounts", Bank.Line.ACCOUNT_FEES },
            { "On the businesses' new loans", Bank.Line.LOAN_FEES_PAID },
            { "On the families' new borrowing", Bank.Line.LOAN_FEES_OWED },
            { "Underwriting the businesses' bonds", Bank.Line.UNDERWRITING } };

    /** A bank line's id on the statement. */
    public static String bankId(Bank.Line line) { return "b." + line.name(); }

    /** The bank's income statement, this month against last. */
    public static Table bankIncome(Bank bank) {
        boolean known = bank.knowsLastMonth();
        Builder b = new Builder(known);
        java.util.function.ToDoubleFunction<Bank.Line> now = bank::thisMonth, then = bank::lastMonth;

        b.head(B_INCOME_HEAD, "Interest income").group();
        double partsNow = 0, partsThen = 0;
        for (Object[] w : BANK_INTEREST) {
            Bank.Line l = (Bank.Line) w[1];
            b.line(bankId(l), (String) w[0], now.applyAsDouble(l), then.applyAsDouble(l), 0);
            partsNow += now.applyAsDouble(l);
            partsThen += then.applyAsDouble(l);
        }
        b.residual(B_OTHER_INTEREST, "Other interest", now.applyAsDouble(Bank.Line.INTEREST) - partsNow,
                then.applyAsDouble(Bank.Line.INTEREST) - partsThen);
        b.groupTotal(B_INTEREST, "Interest income");
        b.head(B_EXPENSE_HEAD, "Interest expense").group();
        b.line(bankId(Bank.Line.SAVERS), "Paid to savers", -now.applyAsDouble(Bank.Line.SAVERS),
                -then.applyAsDouble(Bank.Line.SAVERS), 0);
        b.line(bankId(Bank.Line.WINDOW), "Borrowed overnight at the window", -now.applyAsDouble(Bank.Line.WINDOW),
                -then.applyAsDouble(Bank.Line.WINDOW), 0);
        b.groupTotal(B_EXPENSE, "Interest expense");
        b.subtotal(B_NII, "NET INTEREST INCOME");

        b.head(B_FEES_HEAD, "Fees and other income").group();
        for (Object[] f : BANK_FEES) {
            Bank.Line l = (Bank.Line) f[1];
            b.line(bankId(l), (String) f[0], now.applyAsDouble(l), then.applyAsDouble(l), 0);
        }
        b.groupTotal(B_FEES, "Fees");
        b.line(bankId(Bank.Line.TRADING), "The trading desk", now.applyAsDouble(Bank.Line.TRADING),
                then.applyAsDouble(Bank.Line.TRADING), 0);
        b.line(bankId(Bank.Line.PAPER_GAINS), "Gains on the city's paper", now.applyAsDouble(Bank.Line.PAPER_GAINS),
                then.applyAsDouble(Bank.Line.PAPER_GAINS), 0);
        b.line(bankId(Bank.Line.BOND_GAINS), "Gains on the businesses' bonds", now.applyAsDouble(Bank.Line.BOND_GAINS),
                then.applyAsDouble(Bank.Line.BOND_GAINS), 0);
        b.subtotal(B_TOI, "TOTAL OPERATING INCOME");
        b.line(B_PROVISIONS, "Provisions for loans expected to go bad", -now.applyAsDouble(Bank.Line.PROVISIONS),
                -then.applyAsDouble(Bank.Line.PROVISIONS), 0);
        b.subtotal(B_AFTER_PROVISIONS, "AFTER PROVISIONS");
        b.line(B_PAYROLL, "Its staff", -now.applyAsDouble(Bank.Line.PAYROLL), -then.applyAsDouble(Bank.Line.PAYROLL), 0);
        b.line(B_UPKEEP, "Its branches' upkeep", -now.applyAsDouble(Bank.Line.UPKEEP),
                -then.applyAsDouble(Bank.Line.UPKEEP), 0);
        b.subtotal(B_PRE_TAX, "PROFIT BEFORE TAX");
        b.line(B_TAX, "Tax, a month in arrears", -now.applyAsDouble(Bank.Line.TAX), -then.applyAsDouble(Bank.Line.TAX), 0);
        b.total(B_PROFIT, "PROFIT FOR THE MONTH");
        b.line(B_DIVIDENDS, "Paid to its shareholders", -now.applyAsDouble(Bank.Line.DIVIDENDS),
                -then.applyAsDouble(Bank.Line.DIVIDENDS), 0);
        b.line(B_BUYBACKS, "Its own shares bought back", -now.applyAsDouble(Bank.Line.BUYBACKS),
                -then.applyAsDouble(Bank.Line.BUYBACKS), 0);
        b.total(B_KEPT, "KEPT IN THE BANK");
        return b.table("Statement of profit or loss");
    }

    /** The bank's balance sheet's row ids, which the screens and SectorStatementCheck look a row up by. */
    public static final String BS_ASSETS_HEAD = "bs.assetsHead", BS_LOANS_HEAD = "bs.loansHead",
            BS_GROSS = "bs.gross", BS_NET = "bs.net", BS_ASSETS_REST = "bs.assetsRest", BS_ASSETS = "bs.assets",
            BS_LIABILITIES_HEAD = "bs.liabilitiesHead", BS_LIABILITIES_REST = "bs.liabilitiesRest",
            BS_LIABILITIES = "bs.liabilities", BS_EQUITY_HEAD = "bs.equityHead", BS_EQUITY_WHOLE = "bs.equityWhole",
            BS_EQUITY_REST = "bs.equityRest", BS_EQUITY = "bs.equity", BS_CLAIMS = "bs.claims",
            BS_MEMO_HEAD = "bs.memoHead";

    /** A sheet line's id on the statement. */
    public static String sheetId(Bank.Sheet line) { return "bs." + line.name(); }

    /** The bank's balance sheet, this month against a year ago - the bank keeps a year of sheets, not last month's. */
    public static Table bankSheet(Bank bank) {
        boolean known = bank.knowsYearAgo();
        Builder b = new Builder(known);
        java.util.function.ToDoubleFunction<Bank.Sheet> now = bank::sheet, then = bank::yearAgo;
        boolean split = bank.knowsEquitySplit();

        b.head(BS_ASSETS_HEAD, "ASSETS");
        sheetLine(b, "Reserves at the central bank", Bank.Sheet.RESERVES, now, then);
        sheetLine(b, "The city's paper", Bank.Sheet.CITY_PAPER, now, then);
        sheetLine(b, "The businesses' bonds it holds", Bank.Sheet.BONDS, now, then);
        b.head(BS_LOANS_HEAD, "Loans and advances").group();
        sheetLine(b, "To the businesses", Bank.Sheet.BUSINESS_LOANS, now, then);
        sheetLine(b, "Interim financing", Bank.Sheet.INTERIM, now, then);
        sheetLine(b, "The landlords' insured mortgages", Bank.Sheet.MORTGAGES, now, then);
        sheetLine(b, "The families' credit lines", Bank.Sheet.FAMILIES, now, then);
        sheetLine(b, "Lent to the carry trade", Bank.Sheet.CARRY, now, then);
        b.groupTotal(BS_GROSS, "Loans and advances, gross");
        sheetLine(b, "Less the allowance for bad loans", Bank.Sheet.ALLOWANCE, now, then);
        b.groupTotal(BS_NET, "Loans and advances, net");
        sheetLine(b, "The trading desk's shares, at their mark", Bank.Sheet.DESK, now, then);
        double named = 0, namedThen = 0;
        for (Bank.Sheet l : Bank.SHEET_ASSETS) { named += now.applyAsDouble(l); namedThen += then.applyAsDouble(l); }
        b.residual(BS_ASSETS_REST, "NOT ACCOUNTED FOR", now.applyAsDouble(Bank.Sheet.ASSETS) - named,
                then.applyAsDouble(Bank.Sheet.ASSETS) - namedThen);
        b.total(BS_ASSETS, "TOTAL ASSETS");

        b.restart();
        b.head(BS_LIABILITIES_HEAD, "LIABILITIES");
        sheetLine(b, "Lent past its own cash, on its deposits", Bank.Sheet.DEPOSIT_FUNDING, now, then);
        sheetLine(b, "Borrowed overnight at the window", Bank.Sheet.WINDOW, now, then);
        sheetLine(b, "Deposits from abroad", Bank.Sheet.FOREIGN_DEPOSITS, now, then);
        sheetLine(b, "Shares the desk has sold short", Bank.Sheet.DESK_SHORT, now, then);
        sheetLine(b, "The discount on the city's paper, unearned", Bank.Sheet.UNEARNED_DISCOUNT, now, then);
        named = 0;
        namedThen = 0;
        for (Bank.Sheet l : Bank.SHEET_LIABILITIES) { named += now.applyAsDouble(l); namedThen += then.applyAsDouble(l); }
        b.residual(BS_LIABILITIES_REST, "NOT ACCOUNTED FOR", now.applyAsDouble(Bank.Sheet.LIABILITIES) - named,
                then.applyAsDouble(Bank.Sheet.LIABILITIES) - namedThen);
        b.total(BS_LIABILITIES, "TOTAL LIABILITIES");

        b.head(BS_EQUITY_HEAD, "EQUITY").group();
        if (split) {
            sheetLine(b, "Paid-in capital", Bank.Sheet.PAID_IN, now, then);
            sheetLine(b, "Retained earnings", Bank.Sheet.RETAINED, now, then);
            sheetLine(b, "Preferred shares, the city's", Bank.Sheet.PREFERRED, now, then);
            double parts = now.applyAsDouble(Bank.Sheet.PAID_IN) + now.applyAsDouble(Bank.Sheet.RETAINED)
                    + now.applyAsDouble(Bank.Sheet.PREFERRED);
            double partsThen = then.applyAsDouble(Bank.Sheet.PAID_IN) + then.applyAsDouble(Bank.Sheet.RETAINED)
                    + then.applyAsDouble(Bank.Sheet.PREFERRED);
            b.residual(BS_EQUITY_REST, "NOT ACCOUNTED FOR", now.applyAsDouble(Bank.Sheet.EQUITY) - parts,
                    then.applyAsDouble(Bank.Sheet.EQUITY) - partsThen);
        } else {
            b.line(BS_EQUITY_WHOLE, "Its equity, whole: a save from before its parts were kept",
                    now.applyAsDouble(Bank.Sheet.EQUITY), then.applyAsDouble(Bank.Sheet.EQUITY), 0);
        }
        b.groupTotal(BS_EQUITY, "TOTAL EQUITY");
        b.total(BS_CLAIMS, "TOTAL LIABILITIES AND EQUITY");

        b.head(BS_MEMO_HEAD, "Memorandum: deposits it holds, not on its sheet");
        b.memo(sheetId(Bank.Sheet.HOUSEHOLD_DEPOSITS), "The families' deposits",
                now.applyAsDouble(Bank.Sheet.HOUSEHOLD_DEPOSITS), then.applyAsDouble(Bank.Sheet.HOUSEHOLD_DEPOSITS));
        b.memo(sheetId(Bank.Sheet.SECTOR_DEPOSITS), "The businesses' tills",
                now.applyAsDouble(Bank.Sheet.SECTOR_DEPOSITS), then.applyAsDouble(Bank.Sheet.SECTOR_DEPOSITS));
        return b.table("Statement of financial position");
    }

    private static void sheetLine(Builder b, String label, Bank.Sheet line,
                                  java.util.function.ToDoubleFunction<Bank.Sheet> now,
                                  java.util.function.ToDoubleFunction<Bank.Sheet> then) {
        b.line(sheetId(line), label, now.applyAsDouble(line), then.applyAsDouble(line), 0);
    }

    /** The bank's loans and advances, gross: what its provisions and allowance are read against. */
    public static double bankGrossLoans(Bank bank) {
        return bank.sheet(Bank.Sheet.BUSINESS_LOANS) + bank.sheet(Bank.Sheet.INTERIM) + bank.sheet(Bank.Sheet.MORTGAGES)
                + bank.sheet(Bank.Sheet.FAMILIES) + bank.sheet(Bank.Sheet.CARRY);
    }
}
