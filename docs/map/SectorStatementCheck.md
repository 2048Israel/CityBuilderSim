# SectorStatementCheck.java - 848 lines · 19 methods · 5 constants · harnesses

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
>      the mortgages' premiums and the bonds' issuing costs (F2, R5);
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
>      the month's own loans, bonds, repayments and write-offs;
>   9. EVERY GATE (R4): each building's first failure is its build card's
>      gate, a gate it has not is no gate, its cost is paid for in full.

**Uses:** [SectorStatements](SectorStatements.md) (170), [Game](Game.md) (23), [SectorBooks](SectorBooks.md) (18), [Bank](Bank.md) (18), [LongPlaytest](LongPlaytest.md) (16), [Equity](Equity.md) (15), [Sector](Sector.md) (14), [BuildCard](BuildCard.md) (11), [Sectors](Sectors.md) (8), [GameVersion](GameVersion.md) (6), [Debt](Debt.md) (5), [GameFiles](GameFiles.md) (2), [JobType](JobType.md) (2), [TreasuryFund](TreasuryFund.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [LandManager](LandManager.md) (1)

## Sections

| line | section |
|---:|---|
| 157 | · the two cities |
| 228 | · one month |
| 243 | · · 1. the income statement |
| 280 | · · 2. the cash flow |
| 294 | · · 3. the classified sheet |
| 335 | · · 4. the equity statement |
| 383 | · · 5. the bank |
| 419 | · 8, a month |
| 453 | · 1-5 |
| 484 | · 6 |
| 629 | · 7 |
| 669 | · 6, an older save |
| 762 | · 8 |
| 776 | · 9 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 87 | `SectorStatementCheck.CENT` | `.00001` | A cent, in the model's thousands. |
| 125 | `SectorStatementCheck.tallies` | `new LinkedHashMap<>()` | The tallies of one city's run, by identity. |
| 231 | `SectorStatementCheck.bondsSoonBefore` | `new LinkedHashMap<>()` | Last month's bonds falling due within a year, by sector: what this month's bond repayments must be inside. |
| 520 | `SectorStatementCheck.SPLIT_ROWS` | `Set.of(SectorStatements.SOON_HEAD, SectorStatements.DEBT_SOON, SectorStatemen...` | The rows R2 splits the debt into, which a load does not keep. |
| 672 | `SectorStatementCheck.NEW_FIELDS` | `{ "paidIn", "founded", "paidInDerived", "loanFees", "premiums", "bondCosts", ...` | The S2 fields a save from before 0.7.75 does not carry in its books' months. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 82 | `static int fails` |  |
| 83 | `static PrintStream out` |  |
| 84 | `static PrintStream quiet` |  |
| 102 | `int checked, missed` |  |
| 103 | `double worst` |  |
| 104 | `String where` |  |
| 130 | `static int statements, outsideMonths, splitCounted, splitMissing, owedMissing, dueSoonMonths, residualShown...` | What the cities caused: sector-months with each condition. |
| 134 | `static int upfrontMonths, bondCostMonths, foundedMonths, issuedMonths, boughtBackMonths, derivedMonths, sto...` | ...and S2's (0.7.75): sector-months with each of the new lines, and with each flow of the debt schedule. |
| 517 | `static String worstRow` | The row the last comparison missed worst on, for the message. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 80 | 769 | **type** `public class SectorStatementCheck` | The sector statements (0.7.74, batch S1): SectorStatements' formal statements held to the model's own figures, every sector and the bank, every month of two played cities, and across a save. |
| 89 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 94 | 5 | `static void quietly(Runnable r)` |  |
| 101 | 22 | **type** `static final class Tally` | One identity's tally over a run: how many times it was checked, how many missed by more than a cent, and the worst miss. |
| 106 | 11 | `void near(double actual, double expected, String at)` _(in SectorStatementCheck.Tally)_ |  |
| 118 | 4 | `String words()` _(in SectorStatementCheck.Tally)_ |  |
| 127 | 1 | `static Tally t(String name)` |  |
| 138 | 18 | `public static void main(String[] args) throws Exception` |  |

### the two cities (lines 157-227)

| line | len | member | says |
|---:|---:|---|---|
| 160 | 43 | `static Game playersCity(Path root)` | The test player's city (LongPlaytest.main()'s founding and its rhythm), audited every month: thirty years. |
| 205 | 14 | `static Game plainCity(Path root)` | SectorBooksCheck's city: a plain founding, played on, audited every month. |
| 221 | 6 | `static void months(Game g, int n)` | Months played one at a time, each audited. |

### one month (lines 228-418)

| line | len | member | says |
|---:|---:|---|---|
| 234 | 184 | `static void audit(Game g)` | Every sector's four statements and the bank's two, held to the model. |

### 8, a month (lines 419-452)

| line | len | member | says |
|---:|---:|---|---|
| 429 | 23 | `static void scheduleOf(String key, SectorBooks books, SectorBooks.SectorMonth now, String at)` | The debt schedule (R7), this month: each kind closes from last month's sheet to this month's; and from close to close the running totals are the month's own figures - the loans' principal written (what it was handed a... |

### 1-5 (lines 453-483)

| line | len | member | says |
|---:|---:|---|---|
| 455 | 28 | `static void everyMonth()` |  |

### 6 (lines 484-628)

| line | len | member | says |
|---:|---:|---|---|
| 487 | 10 | `static Map<String, double[]> rows(SectorStatements.Table t)` | Every row of a table, this month and last, as text to the cent: what a save must keep. |
| 499 | 16 | `static double compare(Map<String, double[]> a, Map<String, double[]> b, Set<String> skip)` | The rows two tables share, compared to the cent; @return the worst miss, infinite for a figure one knows and the other does not. |
| 524 | 104 | `static void savedAndLoaded(Path root, Game g)` |  |

### 7 (lines 629-668)

| line | len | member | says |
|---:|---:|---|---|
| 631 | 37 | `static void theFormats(Game g)` |  |

### 6, an older save (lines 669-761)

| line | len | member | says |
|---:|---:|---|---|
| 684 | 77 | `static void anOlderSave(Game g) throws Exception` | A FORMAT-32 SAVE (0.7.74's shape), made from the test player's city by taking out what 0.7.75 added - the register's three slots a company and the books' new fields - loads with every listed sector's share capital der... |

### 8 (lines 762-775)

| line | len | member | says |
|---:|---:|---|---|
| 764 | 11 | `static void theSchedule()` |  |

### 9 (lines 776-848)

| line | len | member | says |
|---:|---:|---|---|
| 787 | 61 | `static void everyGate(Game...cities)` | Every building at every gate (R4), on the test player's city as it stands: each one's first failure is the build card's gate (gate()); a gate it has not is NONE - no ore without a site, no licence, no posts, no ground... |

