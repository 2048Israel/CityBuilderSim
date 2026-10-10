# SectorBooks.java - 774 lines · 42 methods · 1 constants · model

`ham/citybuildersim/SectorBooks.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> A month of books for every business in the city, and last month's too.
> 
> WHY THIS EXISTS AT ALL. Six sectors used to keep their own figures in five
> different handlers, in five different shapes, and none of them could show
> LAST month at all. So: one shape, read once a month, kept for two months.
> What the sector screens draw is this. Since the sector template
> (2026-09-11) every sector strikes its statement in this shape itself - see
> Sector.Statement - and this class adds the flows the city recorded
> against its name (credit, subsidy, the owners, the money abroad) and keeps
> the comparative column.
> 
> NOTHING HERE FEEDS BACK. This class reads the model and is read by screens.
> No handler asks it a question, no decision depends on it, and deleting it
> would change nothing about how the city runs. That is the whole design
> constraint: a reporting layer that can affect the thing it reports is not a
> reporting layer, it is a bug waiting for a save/load to expose it.
> 
> IT IS SAVED, and that is not decoration. A statement whose comparative column
> is blank until you have played a month is a statement that is blank exactly
> when a returning player opens it - which is the same trap the schools fell
> into. Two months of six small records is a few hundred bytes.

**Uses:** [Debt](Debt.md) (12), [Equity](Equity.md) (8), [BusinessDebtManager](BusinessDebtManager.md) (7), [Good](Good.md) (5), [Sector](Sector.md) (5), [Game](Game.md) (4), [EconomyManager](EconomyManager.md) (2), [Exchange](Exchange.md) (1), [Statement](Statement.md) (1), [BalanceSheet](BalanceSheet.md) (1)

**Used by (21):** [BankCheck](BankCheck.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CentralBankCheck](CentralBankCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DataSave](DataSave.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistoryCheck](HistoryCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MortgageCheck](MortgageCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [ScaleCheck](ScaleCheck.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [SectorStatements](SectorStatements.md), [SupplierCreditCheck](SupplierCreditCheck.md)

## Sections

| line | section |
|---:|---|
| 451 | THE TWO MONTHS |
| 497 | THE MONTH |
| 683 | SAVE AND RESTORE |
| 730 | A REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 402 | `SectorBooks.Debt.KINDS` | `BusinessDebtManager.DEBT_KINDS` | The four kinds, as the statements name them: BusinessDebtManager.DEBT_KINDS. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 455 | `private final Map<String, SectorMonth> now` |  |
| 456 | `private final Map<String, SectorMonth> before` |  |
| 459 | `private final Map<String, Double> lastCash` | Closing cash from the month just recorded, which is next month's opening. |
| 462 | `private final Map<String, Debt> debtNow` | R1 and R2 for this month and last, and each company's share (0.7.74): in memory only - see Debt and Shares. |
| 463 | `private final Map<String, Shares> sharesNow` |  |
| 466 | `private final Map<String, Map<Good, double[]>> stockAtClose` | Each business's stock by good as the month closed, {units, price} (0.7.75, R6): next month's revaluation is last month's units at its prices. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 744 | **type** `public final class SectorBooks` | A month of books for every business in the city, and last month's too. |
| 40 | 335 | **type** `public record SectorMonth(String sector, int month, double revenue, double inputs, double payroll, double e...` | One sector's month. |
| 280 | 3 | `public double equity()` _(in SectorBooks.SectorMonth)_ | What the sheet says the owners have. |
| 285 | 3 | `public double totalLiabilities()` _(in SectorBooks.SectorMonth)_ | What it owes: its loans and bonds, and since 0.7.44 its suppliers. |
| 289 | 3 | `public double totalAssets()` _(in SectorBooks.SectorMonth)_ |  |
| 294 | 3 | `public double operatingCost()` _(in SectorBooks.SectorMonth)_ | Everything above operating income: goods bought, payroll, utilities, repairs. |
| 307 | 14 | `public double unexplained()` _(in SectorBooks.SectorMonth)_ | What the cash flow statement adds up to, against what the cash actually is. |
| 323 | 8 | `public double unexplainedScale()` _(in SectorBooks.SectorMonth)_ | The size of the figures unexplained() is made of, every one of them as a magnitude: what MoneyAudit.tolerance() reads it against (0.7.54). |
| 332 | 3 | `public double margin()` _(in SectorBooks.SectorMonth)_ |  |
| 337 | 16 | `public static SectorMonth none(String sector)` _(in SectorBooks.SectorMonth)_ | An empty month, for a sector that has not been recorded yet. |
| 355 | 3 | `public double retained()` _(in SectorBooks.SectorMonth)_ | Its equity less its share capital: what it kept and what the model revalued (R3). |
| 360 | 10 | `SectorMonth withPaidIn(double capital, boolean derived)` _(in SectorBooks.SectorMonth)_ | The same month with its share capital put: a save from before 0.7.75, derived at the load (derivePaidIn()). |
| 371 | 3 | `public boolean isEmpty()` _(in SectorBooks.SectorMonth)_ |  |
| 398 | 37 | **type** `public record Debt(double[] interest, double[] owed, double[] withinYear, double[] withinFive, double[] rat...` | R1 AND R2 (0.7.74, the sector statements): one business's interest by instrument as its statement was struck, and what it owes by kind with what of it falls due within a year and within five, as its sheet was read - e... |
| 404 | 1 | `public double interestTotal()` _(in SectorBooks.Debt)_ |  |
| 405 | 1 | `public double owedTotal()` _(in SectorBooks.Debt)_ |  |
| 406 | 1 | `public double withinYearTotal()` _(in SectorBooks.Debt)_ |  |
| 407 | 1 | `public double withinFiveTotal()` _(in SectorBooks.Debt)_ |  |
| 409 | 6 | `private static double sum(double[] a)` _(in SectorBooks.Debt)_ |  |
| 416 | 4 | `Debt scaled(double s)` _(in SectorBooks.Debt)_ |  |
| 421 | 6 | `private static double[][] times(double[][] a, double s)` _(in SectorBooks.Debt)_ |  |
| 428 | 6 | `private static double[] times(double[] a, double s)` _(in SectorBooks.Debt)_ |  |
| 443 | 7 | **type** `public record Shares(double shares, double outstanding, double price, double fair, boolean traded, double d...` | One company's share as its month closed (0.7.74, the investor report): its shares, those outside the bank's desk, the last trade or fair value with whether it has traded, the register's fair value, the month's dividen... |
| 446 | 3 | `Shares scaled(double s)` _(in SectorBooks.Shares)_ |  |

### THE TWO MONTHS (lines 451-496)

| line | len | member | says |
|---:|---:|---|---|
| 469 | 1 | `public Debt debt(String key)` | This month's R1 and R2 for one business, or null while not counted (a load, a founding). |
| 471 | 1 | `public Debt debtBefore(String key)` | ...and last month's. |
| 473 | 1 | `public Shares shares(String key)` | One company's share as this month closed, or null while not counted. |
| 475 | 1 | `public Shares sharesBefore(String key)` | ...and as last month closed. |
| 477 | 1 | `public SectorMonth get(Sector sector)` |  |
| 478 | 1 | `public SectorMonth previous(Sector sector)` |  |
| 480 | 3 | `public SectorMonth get(String key)` |  |
| 484 | 3 | `public SectorMonth previous(String key)` |  |
| 488 | 4 | `public boolean hasComparatives()` |  |
| 493 | 3 | `public boolean isEmpty()` |  |

### THE MONTH (lines 497-682)

| line | len | member | says |
|---:|---:|---|---|
| 506 | 24 | `public void takeMonth(Game game)` |  |
| 532 | 15 | `private static Debt readDebt(Game game, String key)` | R1 and R2 as the month's books read them (0.7.74): EconomyManager's splits, copied - null when it kept neither. |
| 548 | 5 | `private static double[][] copy(double[][] a)` |  |
| 555 | 8 | `private static Shares readShares(Game game, String key)` | A company's share as the month closes (0.7.74): null for one that is not listed or has no shares. |
| 574 | 83 | `private SectorMonth read(Game game, Sector sector)` | One sector's figures, off the statement it struck this month and the flows the city recorded against its name. |
| 667 | 15 | `public void derivePaidIn(Equity register)` | A SAVE FROM BEFORE 0.7.75 KEPT NO SHARE CAPITAL (R3): its two months read nothing there. |

### SAVE AND RESTORE (lines 683-729)

| line | len | member | says |
|---:|---:|---|---|
| 696 | 1 | `public java.util.List<SectorMonth> thisMonth()` |  |
| 697 | 1 | `public java.util.List<SectorMonth> lastMonth()` |  |
| 699 | 3 | `private static java.util.List<SectorMonth> list(Map<String, SectorMonth> from)` |  |
| 703 | 26 | `public void restoreFrom(java.util.List<SectorMonth> saved, java.util.List<SectorMonth> savedBefore)` |  |

### A REFORM (lines 730-774)

| line | len | member | says |
|---:|---:|---|---|
| 741 | 10 | `public void redenominate(double scale)` |  |
| 752 | 22 | `private static SectorMonth scaled(SectorMonth m, double s)` |  |

