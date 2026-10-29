# SectorStatementCheck.java - 913 lines · 22 methods · 4 constants · harnesses

`ham/citybuildersim/SectorStatementCheck.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

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

**Uses:** [SectorStatements](SectorStatements.md) (146), [Game](Game.md) (21), [SectorBooks](SectorBooks.md) (17), [LongPlaytest](LongPlaytest.md) (16), [Bank](Bank.md) (15), [Equity](Equity.md) (11), [BuildCard](BuildCard.md) (11), [Sector](Sector.md) (9), [Debt](Debt.md) (8), [Sectors](Sectors.md) (8), [GameVersion](GameVersion.md) (5), [GameFiles](GameFiles.md) (2), [JobType](JobType.md) (2), [TempDirs](TempDirs.md) (1), [TreasuryFund](TreasuryFund.md) (1), [EconomyManager](EconomyManager.md) (1), [SaveFileCheck](SaveFileCheck.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [LandManager](LandManager.md) (1)

**Used by (2):** [RunWorksCheck](RunWorksCheck.md), [SlopeCheck](SlopeCheck.md)

## Sections

| line | section |
|---:|---|
| 166 | · the two cities |
| 237 | · one month |
| 252 | · · 1. the income statement |
| 308 | · · 2. the cash flow |
| 322 | · · 3. the classified sheet |
| 370 | · · 4. the equity statement |
| 418 | · · 5. the bank |
| 454 | · 8, a month |
| 488 | · 1-5 |
| 521 | · 6 |
| 722 | · 7 |
| 762 | · 6, an older save |
| 820 | · 8 |
| 841 | · 9 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 93 | `SectorStatementCheck.CENT` | `.00001` | A cent, in the model's thousands. |
| 131 | `SectorStatementCheck.tallies` | `new LinkedHashMap<>()` | The tallies of one city's run, by identity. |
| 240 | `SectorStatementCheck.bondsSoonBefore` | `new LinkedHashMap<>()` | Last month's bonds falling due within a year, by sector: what this month's bond repayments must be inside. |
| 765 | `SectorStatementCheck.NEW_FIELDS` | `{ "paidIn", "founded", "paidInDerived", "loanFees", "premiums", "bondCosts", ...` | The S2 fields a save from before 0.7.75 does not carry in its books' months. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 88 | `static int fails` |  |
| 89 | `static PrintStream out` |  |
| 90 | `static PrintStream quiet` |  |
| 108 | `int checked, missed` |  |
| 109 | `double worst` |  |
| 110 | `String where` |  |
| 136 | `static int statements, outsideMonths, splitCounted, splitMissing, owedMissing, dueSoonMonths, residualShown...` | What the cities caused: sector-months with each condition. |
| 140 | `static int upfrontMonths, bondCostMonths, foundedMonths, issuedMonths, boughtBackMonths, derivedMonths, sto...` | ...and S2's (0.7.75): sector-months with each of the new lines, and with each flow of the debt schedule. |
| 145 | `static int expensedMonths` | ...and 0.7.102's (A16): sector-months that expensed some of what their borrowing cost them up front. |
| 554 | `static String worstRow` | The row the last comparison missed worst on, for the message. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 86 | 828 | **type** `public class SectorStatementCheck` | The sector statements (0.7.74, batch S1): SectorStatements' formal statements held to the model's own figures, every sector and the bank, every month of two played cities, and across a save. |
| 95 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 100 | 5 | `static void quietly(Runnable r)` |  |
| 107 | 22 | **type** `static final class Tally` | One identity's tally over a run: how many times it was checked, how many missed by more than a cent, and the worst miss. |
| 112 | 11 | `void near(double actual, double expected, String at)` _(in SectorStatementCheck.Tally)_ |  |
| 124 | 4 | `String words()` _(in SectorStatementCheck.Tally)_ |  |
| 133 | 1 | `static Tally t(String name)` |  |
| 147 | 18 | `public static void main(String[] args) throws Exception` |  |

### the two cities (lines 166-236)

| line | len | member | says |
|---:|---:|---|---|
| 169 | 43 | `static Game playersCity(Path root)` | The test player's city (LongPlaytest.main()'s founding and its rhythm), audited every month: thirty years. |
| 214 | 14 | `static Game plainCity(Path root)` | SectorBooksCheck's city: a plain founding, played on, audited every month. |
| 230 | 6 | `static void months(Game g, int n)` | Months played one at a time, each audited. |

### one month (lines 237-453)

| line | len | member | says |
|---:|---:|---|---|
| 243 | 210 | `static void audit(Game g)` | Every sector's four statements and the bank's two, held to the model. |

### 8, a month (lines 454-487)

| line | len | member | says |
|---:|---:|---|---|
| 464 | 23 | `static void scheduleOf(String key, SectorBooks books, SectorBooks.SectorMonth now, String at)` | The debt schedule (R7), this month: each kind closes from last month's sheet to this month's; and from close to close the running totals are the month's own figures - the loans' principal written (what it was handed a... |

### 1-5 (lines 488-520)

| line | len | member | says |
|---:|---:|---|---|
| 490 | 30 | `static void everyMonth()` |  |

### 6 (lines 521-721)

| line | len | member | says |
|---:|---:|---|---|
| 524 | 10 | `static Map<String, double[]> rows(SectorStatements.Table t)` | Every row of a table, this month and last, as text to the cent: what a save must keep. |
| 536 | 16 | `static double compare(Map<String, double[]> a, Map<String, double[]> b, Set<String> skip)` | The rows two tables share, compared to the cent; @return the worst miss, infinite for a figure one knows and the other does not. |
| 557 | 6 | `static boolean sameDebt(SectorBooks.Debt x, SectorBooks.Debt y)` | Two of R1 and R2's records the same to the cent, field by field: both null, or every array alike. |
| 564 | 6 | `static boolean near(double[][] x, double[][] y)` |  |
| 571 | 6 | `static boolean near(double[] x, double[] y)` |  |
| 578 | 143 | `static void savedAndLoaded(Path root, Game g)` |  |

### 7 (lines 722-761)

| line | len | member | says |
|---:|---:|---|---|
| 724 | 37 | `static void theFormats(Game g)` |  |

### 6, an older save (lines 762-819)

| line | len | member | says |
|---:|---:|---|---|
| 778 | 41 | `static void anOlderSave(Game g) throws Exception` | A FORMAT-32 SAVE (0.7.74's shape), made from the test player's city by taking out what 0.7.75 added - the register's three slots a company and the books' new fields - is refused since 0.7.120 (batch I3: the floor, Gam... |

### 8 (lines 820-840)

| line | len | member | says |
|---:|---:|---|---|
| 822 | 18 | `static void theSchedule()` |  |

### 9 (lines 841-913)

| line | len | member | says |
|---:|---:|---|---|
| 852 | 61 | `static void everyGate(Game...cities)` | Every building at every gate (R4), on the test player's city as it stands: each one's first failure is the build card's gate (gate()); a gate it has not is NONE - no ore without a site, no licence, no posts, no ground... |

