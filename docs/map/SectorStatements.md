# SectorStatements.java - 1,072 lines · 66 methods · 22 constants · model

`ham/citybuildersim/SectorStatements.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> A business's month as formal statements (0.7.74): the income statement
> classified down through gross and operating profit, the balance sheet in
> current and long-lived parts, the cash flow in three sections, the
> statement of changes in equity with its remainder named - and the bank's
> income statement and sheet in a bank's own order. Each is a list of rows
> the Sectors and Bank screens draw and SectorStatementCheck adds up.
> 
> WHY. Jerus, 2026-10-07: "make the financial statements of the sectors ui
> better, cause right now its good, but its not a financial statement, just
> summarized, so i need both a summarized and a detailed actual statement ...
> also that means that a banks income statement is different, cause interest
> income". The pages drew one statement in plain words: no gross profit, no
> current against long-lived, the month's cash as a flat list of up to twenty
> flows, equity one plug and debt one line - a summary, not a statement. The
> project's spec-sector-statements.md is the design (its section 10, batch 1,
> is this class, and batch 2 (0.7.75) gave it share capital, what the prices
> did and the debt schedule: R3, R6, R7); its D1-D14 are the orchestrator's,
> to be confirmed.
> 
> PRESENTATION ONLY. Every bottom line is the model's own figure: profit
> before tax is preTaxIncome, the profit netIncome, the cash at the end the
> cash, the totals the sheet's. What this class adds is the arithmetic in
> between - the subtotals are its own running sums of its own lines, which is
> what lets the harness catch a line that is missing or counted twice: a
> statement that foots only because its total was copied from the model
> proves nothing.
> 
> Pure: it reads SectorBooks (and the bank, and since 0.7.75 the register
> for note 10) and changes nothing; nothing in the model reads it.

**Uses:** [Bank](Bank.md) (101), [SectorBooks](SectorBooks.md) (35), [Debt](Debt.md) (10), [BusinessDebtManager](BusinessDebtManager.md) (8), [Equity](Equity.md) (3), [LandManager](LandManager.md) (2)

**Used by (17):** [BankScreen](BankScreen.md), [BusinessServices](BusinessServices.md), [CentralBankCheck](CentralBankCheck.md), [Construction](Construction.md), [Freight](Freight.md), [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandClassView](LandClassView.md), [LuxuryRetail](LuxuryRetail.md), [RealEstate](RealEstate.md), [ReloadCheck](ReloadCheck.md), [Restaurants](Restaurants.md), [Retail](Retail.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [StatementView](StatementView.md)

## Sections

| line | section |
|---:|---|
| 44 | THE FORMATS (spec 4.6) |
| 82 | A STATEMENT AS ROWS |
| 309 | THE INCOME STATEMENT (spec 4.1): STATEMENT OF PROFIT OR LOSS |
| 444 | THE BALANCE SHEET (spec 4.2): STATEMENT OF FINANCIAL POSITION |
| 536 | THE CASH FLOWS (spec 4.3): INDIRECT, IN THREE SECTIONS |
| 626 | THE STATEMENT OF CHANGES IN EQUITY (spec 4.2), IN COLUMNS (0.7.75) |
| 779 | THE DEBT SCHEDULE (spec 4.3; 0.7.75, R7): BY KIND, FROM SHEET TO SHEET |
| 834 | THE RATIOS (spec 4.1-4.3), each NaN where it means nothing |
| 886 | THE BANK (spec 4.5): ITS OWN FORMAT, ON ITS OWN TAB (D9) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 55 | `SectorStatements.Format.MAKERS` |  |
| 56 | `SectorStatements.Format.MERCHANTS` |  |
| 57 | `SectorStatements.Format.LANDLORDS` |  |
| 58 | `SectorStatements.Format.BUILDERS` |  |
| 59 | `SectorStatements.Format.CARRIERS` |  |
| 60 | `SectorStatements.Format.BANK` |  |
| 87 | `SectorStatements.Kind.HEAD` |  |
| 87 | `SectorStatements.Kind.LINE` |  |
| 87 | `SectorStatements.Kind.SUBTOTAL` |  |
| 87 | `SectorStatements.Kind.TOTAL` |  |
| 87 | `SectorStatements.Kind.MEMO` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 94 | `SectorStatements.NOTICE` | `.001` | A residual line - NOT ACCOUNTED FOR - is shown from a dollar (the cash page's rule since 0.7.30): under that it is the floating point. |
| 185 | `SectorStatements.THOUSANDS_UNTIL` | `10_000_000` | D2: a statement is in $ thousands - the model's own unit - until its largest figure passes seven digits, and then in $ millions, so the landlords' $1.2B a month does not run to ten. |
| 325 | `SectorStatements.REVENUE` | `"revenue", SALES_TAX = "salesTax", NET_REVENUE = "netRevenue", INPUTS = "inpu...` | The statement of profit or loss's row ids, which the screens and SectorStatementCheck look a row up by (Table.row()). |
| 333 | `SectorStatements.SUBSIDY` | `"subsidy", ARREARS = "arrears", DEPOSIT_INTEREST = "depositInterest", COUPONS...` | The outside-the-trading-result lines' ids, in their order (F1; since 0.7.75 the bonds written off and F2's three). |
| 339 | `SectorStatements.FINANCE_NOTE` | `5` | The finance costs' note. |
| 341 | `SectorStatements.OUTSIDE_NOTE` | `6` | The outside-the-trading-result note. |
| 374 | `SectorStatements.OUTSIDE_IDS` | `{ SUBSIDY, ARREARS, DEPOSIT_INTEREST, COUPONS, FOREIGN_INTEREST, FORGIVEN, WR...` | The outside lines' row ids, in outside()'s order. |
| 378 | `SectorStatements.OUTSIDE_LABELS` | `{ "Subsidy from the city", "Arrears the city paid", "Interest on its bank bal...` | ...and their labels, in the same order. |
| 460 | `SectorStatements.ASSETS_HEAD` | `"assetsHead", CURRENT_HEAD = "currentHead", CASH = "cash", STOCK = "stock", R...` | The balance sheet's row ids, which the screens and SectorStatementCheck look a row up by. |
| 472 | `SectorStatements.STOCK_NOTE` | `7, BUILDINGS_NOTE = 8, ABROAD_NOTE = 9, CAPITAL_NOTE = 10, LAND_NOTE = 11` | The stock's note, the buildings' and what it holds abroad - and since 0.7.75 its share capital's (R3), and since ET3 its land's, by the ground it stands on. |
| 548 | `SectorStatements.OPERATING_HEAD` | `"operatingHead", NET_INCOME = "netIncome", PAID_EARLIER = "paidEarlier", BORR...` | The cash flow statement's row ids, which the screens and SectorStatementCheck look a row up by. |
| 648 | `SectorStatements.EQ_START` | `"eq.start", EQ_PROFIT = "eq.profit", EQ_OUTSIDE = "eq.outside", EQ_FOUNDED = ...` | The statement of changes in equity's row ids, which the screens and SectorStatementCheck look a row up by. |
| 654 | `SectorStatements.CAPITAL` | `0, KEPT = 1` | The equity statement's two columns, each row's parts: share capital, and what it kept and revalued. |
| 657 | `SectorStatements.REVALUED_IDS` | `{ EQ_STOCK, EQ_LAND, EQ_BUILDINGS, EQ_ABROAD }` | R6's four parts, ids: what the prices did to what it began the month with. |
| 730 | `SectorStatements.LAND_CLASSES` | `3` | The classes of ground the books value land on (ET3): flat, foothill, and the steep and very steep ground only works stand on. |
| 733 | `SectorStatements.LAND_IDS` | `{ "land.flat", "land.foothill", "land.steep" }` | Note 11's rows, by class: ids and labels. |
| 734 | `SectorStatements.LAND_LABELS` | `{ "Flat", "Foothill", "Steep(its mines)" }` |  |
| 737 | `SectorStatements.LAND_AREA` | `0, LAND_PRICE = 1` | In a land note's row, `parts` holds {square kilometres, the price a square metre}; `now` is its value, and the rows add to the sheet's land. |
| 901 | `SectorStatements.B_INCOME_HEAD` | `"b.incomeHead", B_OTHER_INTEREST = "b.otherInterest", B_INTEREST = "b.interes...` | The bank's statement of profit or loss's row ids, which the screens and SectorStatementCheck look a row up by. |
| 909 | `SectorStatements.BANK_INTEREST` | `{ { "From the businesses", Bank.Line.FROM_BUSINESSES }, { "On the businesses'...` | The interest income lines: {label, line}. |
| 919 | `SectorStatements.BANK_FEES` | `{ { "On the families' accounts", Bank.Line.ACCOUNT_FEES }, { "On the business...` | ...and the fees. |
| 984 | `SectorStatements.BS_ASSETS_HEAD` | `"bs.assetsHead", BS_LOANS_HEAD = "bs.loansHead", BS_GROSS = "bs.gross", BS_NE...` | The bank's balance sheet's row ids, which the screens and SectorStatementCheck look a row up by. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 63 | `public final String word` | The format's name, as the screens say it. |
| 65 | `public final String revenue` | The revenue line's label. |
| 67 | `public final String cost` | The cost of sales line's label. |
| 69 | `public final String gross` | The gross line's label, or null for a format with none (the landlords: what they buy in is an operating cost). |
| 71 | `public final String operating` | The operating line's label. |
| 198 | `private final List<Row> rows` |  |
| 199 | `private final Map<Integer, List<Row>> notes` |  |
| 200 | `private double runNow, runThen, groupNow, groupThen` |  |
| 202 | `private final double[] runParts` | The running total of each column, for a statement in columns (split()). |
| 203 | `private final boolean known` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 1033 | **type** `public final class SectorStatements` | A business's month as formal statements (0.7.74): the income statement classified down through gross and operating profit, the balance sheet in current and long-lived parts, the cash flow in three sections, the statem... |
| 42 | 1 | `private SectorStatements()` |  |

### THE FORMATS (spec 4.6) (lines 44-81)

| line | len | member | says |
|---:|---:|---|---|
| 54 | 27 | **type** `public enum Format` | A kind of business, as its income statement reads: what it calls its revenue, its cost of sales, and its middle line. |
| 73 | 7 | `Format(String word, String revenue, String cost, String gross, String operating)` _(in SectorStatements.Format)_ |  |

### A STATEMENT AS ROWS (lines 82-308)

| line | len | member | says |
|---:|---:|---|---|
| 87 | 1 | **type** `public enum Kind` | What a row is: a section's title, a line that adds, a subtotal (a rule over it), a bottom line (a double rule under it), or a figure for the record outside the sums. |
| 110 | 24 | **type** `public record Row(String id, String label, Kind kind, double now, double then, int note, double showFrom, d...` | One row of a statement. |
| 113 | 3 | `public Row(String id, String label, Kind kind, double now, double then, int note, double showFrom)` _(in SectorStatements.Row)_ |  |
| 118 | 6 | `public boolean shown()` _(in SectorStatements.Row)_ | A line shows when either month, or any of its columns, is not nothing - today's rule; a head, a subtotal and a total always. |
| 126 | 3 | `public double part(int i)` _(in SectorStatements.Row)_ | One of its columns; NaN without them. |
| 130 | 3 | `private static boolean beyond(double v, double from)` _(in SectorStatements.Row)_ |  |
| 141 | 38 | **type** `public record Table(String title, List<Row> rows, boolean thenKnown, Map<Integer, List<Row>> notes)` | A statement: its title, its rows in order, whether last month is known, and its notes - each the rows a note number opens into, this month's only (D12: SectorMonth keeps no per-good maps, and the notes answer "what is... |
| 144 | 4 | `public Row row(String id)` _(in SectorStatements.Table)_ | The row with this id, or null. |
| 150 | 4 | `public double now(String id)` _(in SectorStatements.Table)_ | This month's figure on a row; NaN with no such row. |
| 156 | 4 | `public double then(String id)` _(in SectorStatements.Table)_ | ...and last month's. |
| 162 | 4 | `public List<Row> note(int n)` _(in SectorStatements.Table)_ | The rows a note opens into; empty for a note it does not keep rows for. |
| 168 | 10 | `public double largest()` _(in SectorStatements.Table)_ | The largest figure the statement shows, either month or any column: what its units are chosen by (D2). |
| 188 | 3 | `public static boolean inMillions(Table t)` | Whether a statement is printed in millions (D2). |
| 197 | 111 | **type** `static final class Builder` | The builder: lines add to a running total from the top and to the open group's; a subtotal prints the one, a group total the other. |
| 205 | 1 | `Builder(boolean known)` _(in SectorStatements.Builder)_ |  |
| 207 | 1 | `private double then(double v)` _(in SectorStatements.Builder)_ |  |
| 209 | 1 | `Builder head(String id, String label)` _(in SectorStatements.Builder)_ |  |
| 211 | 4 | `Builder head(String id, String label, int note)` _(in SectorStatements.Builder)_ |  |
| 217 | 5 | `Builder group()` _(in SectorStatements.Builder)_ | A new group: its total counts from here. |
| 224 | 5 | `Builder restart()` _(in SectorStatements.Builder)_ | A new running total: the next section of the statement adds from nothing (the sheet's liabilities). |
| 230 | 3 | `Builder line(String id, String label, double now, double then, int note)` _(in SectorStatements.Builder)_ |  |
| 235 | 3 | `Builder residual(String id, String label, double now, double then)` _(in SectorStatements.Builder)_ | A residual: shown from NOTICE, and counted like any line. |
| 239 | 8 | `private Builder add(Row r)` _(in SectorStatements.Builder)_ |  |
| 249 | 4 | `Builder subtotal(String id, String label)` _(in SectorStatements.Builder)_ | The running total, under a rule. |
| 255 | 5 | `Builder total(String id, String label, double fallback)` _(in SectorStatements.Builder)_ | The running total as a bottom line; last month's from `fallback` when the lines could not say it. |
| 261 | 1 | `Builder total(String id, String label)` _(in SectorStatements.Builder)_ |  |
| 264 | 4 | `Builder groupTotal(String id, String label)` _(in SectorStatements.Builder)_ | The open group's total, under a rule. |
| 270 | 3 | `Builder split(String id, String label, double capital, double kept, int note)` _(in SectorStatements.Builder)_ | A line in columns: share capital and what it kept, adding to the row's figure (0.7.75, R3); this month only. |
| 275 | 3 | `Builder splitResidual(String id, String label, double capital, double kept)` _(in SectorStatements.Builder)_ | ...and a residual in columns, shown from NOTICE. |
| 279 | 5 | `private Builder split(String id, String label, double capital, double kept, int note, double showFrom)` _(in SectorStatements.Builder)_ |  |
| 286 | 4 | `Builder splitTotal(String id, String label)` _(in SectorStatements.Builder)_ | The running totals in columns, as a bottom line. |
| 292 | 4 | `Builder memo(String id, String label, double now, double then)` _(in SectorStatements.Builder)_ | A figure for the record: printed, added to nothing. |
| 297 | 4 | `Builder note(int n, List<Row> lines)` _(in SectorStatements.Builder)_ |  |
| 302 | 1 | `double runNow()` _(in SectorStatements.Builder)_ |  |
| 304 | 3 | `Table table(String title)` _(in SectorStatements.Builder)_ |  |

### THE INCOME STATEMENT (spec 4.1): STATEMENT OF PROFIT OR LOSS (lines 309-443)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 6 | `static double[] outside(SectorBooks.SectorMonth m)` | The flows that move a business's equity and reach no statement (F1), each signed as it moves the equity: the subsidy and the city's arrears, the bank's interest on its till, the coupons it is paid, the interest abroad... |
| 385 | 5 | `public static double outsideTotal(SectorBooks.SectorMonth m)` | The outside lines together, this month: what the equity statement names in one line. |
| 392 | 45 | `public static Table income(Format f, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, SectorBooks.Debt debt)` | One business's income statement, this month against last, its finance costs' note from R1 when counted. |
| 439 | 4 | `public static double result(Table income)` | The income statement's bottom line as the trading result: the result with the outside lines, or the profit when there are none. |

### THE BALANCE SHEET (spec 4.2): STATEMENT OF FINANCIAL POSITION (lines 444-535)

| line | len | member | says |
|---:|---:|---|---|
| 475 | 1 | `public static String laterId(int kind)` | A kind of debt's line on the sheet, due later: "later.0" for bank loans, in Debt.KINDS' order. |
| 478 | 57 | `public static Table sheet(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, SectorBooks.Debt debt, SectorBooks.Debt de...` | One business's balance sheet, as at the month's close against last month's, its debt by when it falls due from R2 when counted. |

### THE CASH FLOWS (spec 4.3): INDIRECT, IN THREE SECTIONS (lines 536-625)

| line | len | member | says |
|---:|---:|---|---|
| 563 | 56 | `public static Table cashFlow(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | One business's cash flows, this month against last. |
| 621 | 4 | `public static double freeCashOf(SectorBooks.SectorMonth m)` | Free cash flow: the operating section less what its premises and scrapped plant took (the investing section's buildings). |

### THE STATEMENT OF CHANGES IN EQUITY (spec 4.2), IN COLUMNS (0.7.75) (lines 626-778)

| line | len | member | says |
|---:|---:|---|---|
| 660 | 3 | `public static double stockRevalued(SectorBooks.SectorMonth now)` | Last month's stock at this month's prices, less at last month's - NaN when last month's stock was not in memory (a load). |
| 670 | 7 | `public static double landRevalued(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | Last month's land at this month's price a square foot, less at last month's - NaN when either month's price was not read. |
| 679 | 3 | `public static boolean landByClass(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | Whether the land's revaluation is read by class of ground: both months carry it and either has any land on foothill or steep ground (ET3). |
| 684 | 4 | `public static double buildingsRevalued(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | ...the materials in last month's buildings at this month's price a unit, less at last month's. |
| 690 | 3 | `public static double abroadRevalued(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | ...and what it holds abroad, less what it sent there and the interest rolled in: the rate it is valued at. |
| 695 | 23 | `public static Table equity(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | The month's statement of changes in equity, in columns; null when last month's sheet is not on file. |
| 720 | 8 | `public static double revaluedTotal(Table equity)` | What the equity statement says the prices did, together (R6's four parts, those counted): the summary's bridge. |
| 745 | 12 | `public static List<Row> landNote(SectorBooks.SectorMonth now)` | Note 11 (ET3, spec-earth2-terrain 5.3), its land by the ground it stands on: one row for each class that holds any of it, as the sheet valued the month - its square kilometres, its price a square metre (LandManager.pe... |
| 765 | 13 | `public static List<Row> capitalNote(Equity register, String company)` | Note 10, its share capital's parts as the register keeps them (R3): the book its founders' shares were issued against, what it raised at home and abroad, and what its buybacks paid. |

### THE DEBT SCHEDULE (spec 4.3; 0.7.75, R7): BY KIND, FROM SHEET TO SHEET (lines 779-833)

| line | len | member | says |
|---:|---:|---|---|
| 801 | 10 | **type** `public record Schedule(double[] start, double[] borrowed, double[] repaid, double[] writtenOff, double[] en...` | One sector's debt schedule: each in DEBT_KINDS' order. |
| 805 | 5 | `public static double total(double[] a)` _(in SectorStatements.Schedule)_ | A column's total. |
| 812 | 21 | `public static Schedule schedule(SectorBooks.Debt now, SectorBooks.Debt before)` |  |

### THE RATIOS (spec 4.1-4.3), each NaN where it means nothing (lines 834-885)

| line | len | member | says |
|---:|---:|---|---|
| 839 | 3 | `public static double of(double part, double base)` | A share of a base, NaN on no base. |
| 844 | 4 | `public static double interestCover(Table income)` | Interest cover on the statement's operating profit (D10, F6): how many times its trading profit, after property and sales tax, covers its finance costs. |
| 850 | 4 | `public static double effectiveTax(Table income)` | Business tax over profit before tax: NaN on a loss, where the rate means nothing. |
| 856 | 4 | `public static double currentRatio(Table sheet)` | Current assets over what falls due within a year; NaN while R2 is not counted or nothing falls due. |
| 862 | 4 | `public static double quickRatio(Table sheet)` | ...without the stock: what it could pay at once. |
| 868 | 5 | `public static double returnOnEquity(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then)` | Return on equity a year: twelve months at this month's profit, over the average of the two months' equity. |
| 875 | 4 | `public static double daysOfCash(SectorBooks.SectorMonth m)` | Cash on hand in days of what trading costs it: its cash over a thirtieth of the month's operating costs, interest and tax. |
| 881 | 4 | `public static double debtYears(Table cash, SectorBooks.SectorMonth m)` | Debt over a year of trading cash: what it owes over twelve months of the operating section, NaN when that is nothing or less. |

### THE BANK (spec 4.5): ITS OWN FORMAT, ON ITS OWN TAB (D9) (lines 886-1072)

| line | len | member | says |
|---:|---:|---|---|
| 926 | 1 | `public static String bankId(Bank.Line line)` | A bank line's id on the statement. |
| 929 | 53 | `public static Table bankIncome(Bank bank)` | The bank's income statement, this month against last. |
| 992 | 1 | `public static String sheetId(Bank.Sheet line)` | A sheet line's id on the statement. |
| 995 | 65 | `public static Table bankSheet(Bank bank)` | The bank's balance sheet, this month against a year ago - the bank keeps a year of sheets, not last month's. |
| 1061 | 5 | `private static void sheetLine(Builder b, String label, Bank.Sheet line, java.util.function.ToDoubleFunction<Bank.Sheet> now, ja...` |  |
| 1068 | 4 | `public static double bankGrossLoans(Bank bank)` | The bank's loans and advances, gross: what its provisions and allowance are read against. |

