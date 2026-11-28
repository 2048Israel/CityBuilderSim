# SectorStatementCheck.java - 1,367 lines · 30 methods · 7 constants · harnesses

`ham/citybuildersim/SectorStatementCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> The sector statements (0.7.74, batch S1): SectorStatements' formal
> statements held to the model's own figures, every sector and the bank,
> every month of two played cities, and across a save.
> 
> WHY. Jerus asked for "both a summarized and a detailed actual statement"
> of every sector, and for the bank's in a bank's own order. A statement
> whose subtotals are its own sums has to be shown to land on the model's
> figures, or it is a second set of books that can drift from the first -
> the screen would then print a gross profit, an operating profit and a
> cash flow nobody had checked. The spec (the project's
> spec-sector-statements.md, section 7) lists what must hold; this is it.
> 
> What this has to prove:
>   1. THE INCOME STATEMENT: its profit before tax is preTaxIncome to the
>      cent and its profit netIncome; each subtotal is the model's own
>      identity (revenue after sales tax, gross, operating); its total result
>      is the profit and the named outside lines; the finance costs' note
>      (R1) adds to the line in every month it is counted.
>   2. THE CASH FLOW: the three sections and the cash at the start, with
>      unexplained() as the residual, are the cash at the end - which is the
>      cash - so every term of unexplained() is in exactly one section.
>   3. THE CLASSIFIED SHEET: current and long-lived assets are the total
>      assets; what falls due within a year and later are the liabilities;
>      R2's kinds are the debt line; equity and claims close; last month's
>      column is last month's sheet.
>   4. THE EQUITY STATEMENT closes from last month's equity to this month's,
>      its owners' lines are equityRaised, dividendsPaid and
>      sharesBoughtBack, and its outside line is the income statement's.
>   5. THE BANK: its net interest income, total operating income, profit
>      before tax, profit and what it kept are its Bank.Lines; its interest
>      income and its fees are their lines; its sheet's totals are the
>      bank's, its equity in its parts.
>   6. A SAVE AND A LOAD keep every comparative: every row of every
>      statement built from saved figures is the same to the cent; R1, R2
>      and the shares read not counted after the load and counted a month on.
>   7. THE FORMATS: each sector reads as the kind of business it declares.
> 
> Both cities cause what is checked: a month with the outside lines, debt
> falling due within a year, the interest split counted, bonds repaid.
> 
> BATCH S2 (0.7.75) adds, in the same sections and three of its own:
>   1. the outside lines' new four - the bonds written off, the bank's fees,
>      the mortgages' premiums and the bonds' issuing costs (F2, R5) - the
>      last three outside only in a month struck before 0.7.102, which
>      expenses them over each debt's life inside the profit (A16);
>   4. THE EQUITY STATEMENT IN COLUMNS (R3, R6): its share capital starts
>      at last month's paid-in and ends at this month's, closing on its own
>      lines - the founders' shares, issued, bought back - so the register's
>      money moves exactly as the month's books say; every row's columns
>      add to its figure; the sheet's land is its square feet at its price;
>   6. a save keeps the columns and the register's paid-in, R7 counts again
>      two months on, and a FORMAT-32 SAVE is refused, below the floor, its
>      file untouched and nothing derived (since 0.7.120; until then it
>      loaded with its paid-in derived and said so, closed, and saved again
>      in this build's format loaded back to the cent);
>   8. THE DEBT SCHEDULE (R7): each kind's start, borrowed, repaid, written
>      off and end close, and from close to close the running totals are
>      the month's own loans, bonds, repayments and write-offs - and since
>      0.7.102 (A16, F-S1-2) the sheet is read at the close, so nothing is
>      lent after it and section 3 holds its debt, land and buildings to
> ... (3 more lines in the source)

**Uses:** [SectorStatements](SectorStatements.md) (148), [Game](Game.md) (39), [Sector](Sector.md) (24), [SectorBooks](SectorBooks.md) (22), [LongPlaytest](LongPlaytest.md) (18), [Bank](Bank.md) (15), [Sectors](Sectors.md) (15), [Construction](Construction.md) (12), [Equity](Equity.md) (11), [BuildCard](BuildCard.md) (11), [Debt](Debt.md) (8), [GameFiles](GameFiles.md) (5), [GameVersion](GameVersion.md) (5), [ConstructionTrapCheck](ConstructionTrapCheck.md) (3), [DeathCareFirstCheck](DeathCareFirstCheck.md) (2), [Statement](Statement.md) (2), [JobType](JobType.md) (2), [Good](Good.md) (2), [TempDirs](TempDirs.md) (1), [TreasuryFund](TreasuryFund.md) (1), [EconomyManager](EconomyManager.md) (1), [SaveFileCheck](SaveFileCheck.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [LandManager](LandManager.md) (1), [Oil](Oil.md) (1), [Founding](Founding.md) (1), [SectorState](SectorState.md) (1)

**Used by (3):** [RunWorksCheck](RunWorksCheck.md), [SlopeCheck](SlopeCheck.md), [SupplierCreditCheck](SupplierCreditCheck.md)

## Sections

| line | section |
|---:|---|
| 171 | · the two cities |
| 260 | · one month |
| 275 | · · 1. the income statement |
| 334 | · · 2. the cash flow |
| 350 | · · 3. the classified sheet |
| 398 | · · 4. the equity statement |
| 446 | · · 5. the bank |
| 482 | · 8, a month |
| 516 | · 1-5 |
| 549 | · 6 |
| 821 | · 7 |
| 861 | · 6, an older save |
| 919 | · 8 |
| 940 | · 9 |
| 1059 | 4. AND EVERY FIGURE ON THE PAGE IS A FIGURE (2026-09-16) |
| 1140 | · · the sheet |
| 1145 | · · the income statement |
| 1159 | · · the cash flow |
| 1253 | · AND IT HAS TO SURVIVE A SAVE. |
| 1285 | · · and the month after |
| 1291 | · 5. CONSTRUCTION'S MATERIALS ARE THE STATEMENT'S MONTH (A5, 0.7.46) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 94 | `SectorStatementCheck.CENT` | `.00001` | A cent, in the model's thousands. |
| 132 | `SectorStatementCheck.tallies` | `new LinkedHashMap<>()` | The tallies of one city's run, by identity. |
| 263 | `SectorStatementCheck.bondsSoonBefore` | `new LinkedHashMap<>()` | Last month's bonds falling due within a year, by sector: what this month's bond repayments must be inside. |
| 607 | `SectorStatementCheck.SOLD_MONTHS` | `48` | The months the calm town is given to sell its builders' spare points abroad (ForeignCrewsCheck 5 gives it 24 to run its queue down, and 24 more). |
| 609 | `SectorStatementCheck.SOLD_SLOT` | `11, STRUCK_SLOT = 12` | The scratch slots the builders' sale is saved in: the month it is pending, and the month it is struck. |
| 864 | `SectorStatementCheck.NEW_FIELDS` | `{ "paidIn", "founded", "paidInDerived", "loanFees", "premiums", "bondCosts", ...` | The S2 fields a save from before 0.7.75 does not carry in its books' months. |
| 1102 | `SectorStatementCheck.BooksAudit.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 89 | `static int fails` |  |
| 90 | `static PrintStream out` |  |
| 91 | `static PrintStream quiet` |  |
| 109 | `int checked, missed` |  |
| 110 | `double worst` |  |
| 111 | `String where` |  |
| 137 | `static int statements, outsideMonths, splitCounted, splitMissing, owedMissing, dueSoonMonths, residualShown...` | What the cities caused: sector-months with each condition. |
| 141 | `static int upfrontMonths, bondCostMonths, foundedMonths, issuedMonths, boughtBackMonths, derivedMonths, sto...` | ...and S2's (0.7.75): sector-months with each of the new lines, and with each flow of the debt schedule. |
| 146 | `static int expensedMonths` | ...and 0.7.102's (A16): sector-months that expensed some of what their borrowing cost them up front. |
| 582 | `static String worstRow` | The row the last comparison missed worst on, for the message. |
| 1054 | `static int fails` |  |
| 1055 | `static int months` |  |
| 1056 | `static int statements` |  |
| 1057 | `static int screenLines` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 87 | 1281 | **type** `public class SectorStatementCheck` | The sector statements (0.7.74, batch S1): SectorStatements' formal statements held to the model's own figures, every sector and the bank, every month of two played cities, and across a save. |
| 96 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 101 | 5 | `static void quietly(Runnable r)` |  |
| 108 | 22 | **type** `static final class Tally` | One identity's tally over a run: how many times it was checked, how many missed by more than a cent, and the worst miss. |
| 113 | 11 | `void near(double actual, double expected, String at)` _(in SectorStatementCheck.Tally)_ |  |
| 125 | 4 | `String words()` _(in SectorStatementCheck.Tally)_ |  |
| 134 | 1 | `static Tally t(String name)` |  |
| 148 | 22 | `public static void main(String[] args) throws Exception` |  |

### the two cities (lines 171-259)

| line | len | member | says |
|---:|---:|---|---|
| 174 | 43 | `static Game playersCity(Path root)` | The test player's city (LongPlaytest.main()'s founding and its rhythm), audited every month: thirty years. |
| 219 | 14 | `static Game plainCity(Path root)` | SectorBooksCheck's city: a plain founding, played on, audited every month. |
| 240 | 11 | `static Game youngCity(Path root)` | A plain founding three years old, for section 9's F11: its labour is slack (about four in ten of its workers have no job), so a building that would lose money still passes its staffing gate and stops first at the land. |
| 253 | 6 | `static void months(Game g, int n)` | Months played one at a time, each audited. |

### one month (lines 260-481)

| line | len | member | says |
|---:|---:|---|---|
| 266 | 215 | `static void audit(Game g)` | Every sector's four statements and the bank's two, held to the model. |

### 8, a month (lines 482-515)

| line | len | member | says |
|---:|---:|---|---|
| 492 | 23 | `static void scheduleOf(String key, SectorBooks books, SectorBooks.SectorMonth now, String at)` | The debt schedule (R7), this month: each kind closes from last month's sheet to this month's; and from close to close the running totals are the month's own figures - the loans' principal written (what it was handed a... |

### 1-5 (lines 516-548)

| line | len | member | says |
|---:|---:|---|---|
| 518 | 30 | `static void everyMonth()` |  |

### 6 (lines 549-820)

| line | len | member | says |
|---:|---:|---|---|
| 552 | 10 | `static Map<String, double[]> rows(SectorStatements.Table t)` | Every row of a table, this month and last, as text to the cent: what a save must keep. |
| 564 | 16 | `static double compare(Map<String, double[]> a, Map<String, double[]> b, Set<String> skip)` | The rows two tables share, compared to the cent; @return the worst miss, infinite for a figure one knows and the other does not. |
| 585 | 6 | `static boolean sameDebt(SectorBooks.Debt x, SectorBooks.Debt y)` | Two of R1 and R2's records the same to the cent, field by field: both null, or every array alike. |
| 592 | 6 | `static boolean near(double[][] x, double[][] y)` |  |
| 599 | 6 | `static boolean near(double[] x, double[] y)` |  |
| 620 | 49 | `static void theWorkSoldAbroadSaved(Path root)` | REV-FC2b: the builders' spare points sold abroad (FC2) are an export with no good behind it, named on the opened revenue line (Sector.Ledger.servicesSold, struck into Statement.servicesSold). |
| 671 | 5 | `static boolean sameParts(Map<String, Double> x, Map<String, Double> y)` | Two named maps the same to the cent, name by name. |
| 677 | 143 | `static void savedAndLoaded(Path root, Game g)` |  |

### 7 (lines 821-860)

| line | len | member | says |
|---:|---:|---|---|
| 823 | 37 | `static void theFormats(Game g)` |  |

### 6, an older save (lines 861-918)

| line | len | member | says |
|---:|---:|---|---|
| 877 | 41 | `static void anOlderSave(Game g) throws Exception` | A FORMAT-32 SAVE (0.7.74's shape), made from the test player's city by taking out what 0.7.75 added - the register's three slots a company and the books' new fields - is refused since 0.7.120 (batch I3: the floor, Gam... |

### 8 (lines 919-939)

| line | len | member | says |
|---:|---:|---|---|
| 921 | 18 | `static void theSchedule()` |  |

### 9 (lines 940-1367)

| line | len | member | says |
|---:|---:|---|---|
| 951 | 63 | `static void everyGate(Game...cities)` | Every building at every gate (R4), on the test player's city as it stands: each one's first failure is the build card's gate (gate()); a gate it has not is NONE - no ore without a site, no licence, no posts, no ground... |
| 1052 | 315 | **type** `static final class BooksAudit` | Plays a city and audits every sector's statements, every month. |
| 1075 | 10 | `static boolean isAFigure(String value)` _(in SectorStatementCheck.BooksAudit)_ |  |
| 1086 | 14 | `static void pageIsReadable(Game game)` _(in SectorStatementCheck.BooksAudit)_ |  |
| 1105 | 4 | `static void check(String label, boolean ok, String detail)` _(in SectorStatementCheck.BooksAudit)_ | One labelled assertion, printed either way (0.7.46): the rest of this harness speaks only when it fails. |
| 1110 | 9 | `static void near(String what, String sector, int month, double actual, double expected)` _(in SectorStatementCheck.BooksAudit)_ |  |
| 1120 | 246 | `static void run(String[] args)` _(in SectorStatementCheck.BooksAudit)_ |  |

