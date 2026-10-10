# SectorStatementCheck.java - 891 lines · 19 methods · 5 constants · harnesses

`ham/citybuildersim/SectorStatementCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
>      two months on, and a FORMAT-32 SAVE loads with its paid-in derived and
>      said so, closes, and saved again in this build's format loads back to
>      the cent;
>   8. THE DEBT SCHEDULE (R7): each kind's start, borrowed, repaid, written
>      off and end close, and from close to close the running totals are
>      the month's own loans, bonds, repayments and write-offs - and since
>      0.7.102 (A16, F-S1-2) the sheet is read at the close, so nothing is
>      lent after it and section 3 holds its debt, land and buildings to
>      what the business owes and holds as the month closes;
> ... (2 more lines in the source)

**Uses:** [SectorStatements](SectorStatements.md) (172), [Game](Game.md) (23), [SectorBooks](SectorBooks.md) (18), [Bank](Bank.md) (18), [LongPlaytest](LongPlaytest.md) (16), [Equity](Equity.md) (15), [Sector](Sector.md) (14), [BuildCard](BuildCard.md) (11), [Sectors](Sectors.md) (8), [GameVersion](GameVersion.md) (6), [Debt](Debt.md) (5), [GameFiles](GameFiles.md) (2), [JobType](JobType.md) (2), [TreasuryFund](TreasuryFund.md) (1), [EconomyManager](EconomyManager.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [LandManager](LandManager.md) (1)

## Sections

| line | section |
|---:|---|
| 165 | · the two cities |
| 236 | · one month |
| 251 | · · 1. the income statement |
| 307 | · · 2. the cash flow |
| 321 | · · 3. the classified sheet |
| 369 | · · 4. the equity statement |
| 417 | · · 5. the bank |
| 453 | · 8, a month |
| 487 | · 1-5 |
| 520 | · 6 |
| 665 | · 7 |
| 705 | · 6, an older save |
| 798 | · 8 |
| 819 | · 9 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 92 | `SectorStatementCheck.CENT` | `.00001` | A cent, in the model's thousands. |
| 130 | `SectorStatementCheck.tallies` | `new LinkedHashMap<>()` | The tallies of one city's run, by identity. |
| 239 | `SectorStatementCheck.bondsSoonBefore` | `new LinkedHashMap<>()` | Last month's bonds falling due within a year, by sector: what this month's bond repayments must be inside. |
| 556 | `SectorStatementCheck.SPLIT_ROWS` | `Set.of(SectorStatements.SOON_HEAD, SectorStatements.DEBT_SOON, SectorStatemen...` | The rows R2 splits the debt into, which a load does not keep. |
| 708 | `SectorStatementCheck.NEW_FIELDS` | `{ "paidIn", "founded", "paidInDerived", "loanFees", "premiums", "bondCosts", ...` | The S2 fields a save from before 0.7.75 does not carry in its books' months. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 87 | `static int fails` |  |
| 88 | `static PrintStream out` |  |
| 89 | `static PrintStream quiet` |  |
| 107 | `int checked, missed` |  |
| 108 | `double worst` |  |
| 109 | `String where` |  |
| 135 | `static int statements, outsideMonths, splitCounted, splitMissing, owedMissing, dueSoonMonths, residualShown...` | What the cities caused: sector-months with each condition. |
| 139 | `static int upfrontMonths, bondCostMonths, foundedMonths, issuedMonths, boughtBackMonths, derivedMonths, sto...` | ...and S2's (0.7.75): sector-months with each of the new lines, and with each flow of the debt schedule. |
| 144 | `static int expensedMonths` | ...and 0.7.102's (A16): sector-months that expensed some of what their borrowing cost them up front. |
| 553 | `static String worstRow` | The row the last comparison missed worst on, for the message. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 85 | 807 | **type** `public class SectorStatementCheck` | The sector statements (0.7.74, batch S1): SectorStatements' formal statements held to the model's own figures, every sector and the bank, every month of two played cities, and across a save. |
| 94 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 99 | 5 | `static void quietly(Runnable r)` |  |
| 106 | 22 | **type** `static final class Tally` | One identity's tally over a run: how many times it was checked, how many missed by more than a cent, and the worst miss. |
| 111 | 11 | `void near(double actual, double expected, String at)` _(in SectorStatementCheck.Tally)_ |  |
| 123 | 4 | `String words()` _(in SectorStatementCheck.Tally)_ |  |
| 132 | 1 | `static Tally t(String name)` |  |
| 146 | 18 | `public static void main(String[] args) throws Exception` |  |

### the two cities (lines 165-235)

| line | len | member | says |
|---:|---:|---|---|
| 168 | 43 | `static Game playersCity(Path root)` | The test player's city (LongPlaytest.main()'s founding and its rhythm), audited every month: thirty years. |
| 213 | 14 | `static Game plainCity(Path root)` | SectorBooksCheck's city: a plain founding, played on, audited every month. |
| 229 | 6 | `static void months(Game g, int n)` | Months played one at a time, each audited. |

### one month (lines 236-452)

| line | len | member | says |
|---:|---:|---|---|
| 242 | 210 | `static void audit(Game g)` | Every sector's four statements and the bank's two, held to the model. |

### 8, a month (lines 453-486)

| line | len | member | says |
|---:|---:|---|---|
| 463 | 23 | `static void scheduleOf(String key, SectorBooks books, SectorBooks.SectorMonth now, String at)` | The debt schedule (R7), this month: each kind closes from last month's sheet to this month's; and from close to close the running totals are the month's own figures - the loans' principal written (what it was handed a... |

### 1-5 (lines 487-519)

| line | len | member | says |
|---:|---:|---|---|
| 489 | 30 | `static void everyMonth()` |  |

### 6 (lines 520-664)

| line | len | member | says |
|---:|---:|---|---|
| 523 | 10 | `static Map<String, double[]> rows(SectorStatements.Table t)` | Every row of a table, this month and last, as text to the cent: what a save must keep. |
| 535 | 16 | `static double compare(Map<String, double[]> a, Map<String, double[]> b, Set<String> skip)` | The rows two tables share, compared to the cent; @return the worst miss, infinite for a figure one knows and the other does not. |
| 560 | 104 | `static void savedAndLoaded(Path root, Game g)` |  |

### 7 (lines 665-704)

| line | len | member | says |
|---:|---:|---|---|
| 667 | 37 | `static void theFormats(Game g)` |  |

### 6, an older save (lines 705-797)

| line | len | member | says |
|---:|---:|---|---|
| 720 | 77 | `static void anOlderSave(Game g) throws Exception` | A FORMAT-32 SAVE (0.7.74's shape), made from the test player's city by taking out what 0.7.75 added - the register's three slots a company and the books' new fields - loads with every listed sector's share capital der... |

### 8 (lines 798-818)

| line | len | member | says |
|---:|---:|---|---|
| 800 | 18 | `static void theSchedule()` |  |

### 9 (lines 819-891)

| line | len | member | says |
|---:|---:|---|---|
| 830 | 61 | `static void everyGate(Game...cities)` | Every building at every gate (R4), on the test player's city as it stands: each one's first failure is the build card's gate (gate()); a gate it has not is NONE - no ore without a site, no licence, no posts, no ground... |

