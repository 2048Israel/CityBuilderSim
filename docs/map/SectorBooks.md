# SectorBooks.java - 450 lines · 21 methods · 0 constants · model

`ham/citybuildersim/SectorBooks.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [Sector](Sector.md) (5), [Game](Game.md) (2), [EconomyManager](EconomyManager.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1), [Statement](Statement.md) (1), [BalanceSheet](BalanceSheet.md) (1)

**Used by (14):** [BankCheck](BankCheck.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DataSave](DataSave.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistoryCheck](HistoryCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MortgageCheck](MortgageCheck.md), [PolicyScreen](PolicyScreen.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 260 | THE TWO MONTHS |
| 290 | THE MONTH |
| 377 | SAVE AND RESTORE |
| 417 | A REFORM |

## Fields (state)

| line | field | says |
|---:|---|---|
| 264 | `private final Map<String, SectorMonth> now` |  |
| 265 | `private final Map<String, SectorMonth> before` |  |
| 268 | `private final Map<String, Double> lastCash` | Closing cash from the month just recorded, which is next month's opening. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 420 | **type** `public final class SectorBooks` | A month of books for every business in the city, and last month's too. |
| 40 | 219 | **type** `public record SectorMonth(String sector, int month, double revenue, double inputs, double payroll, double e...` | One sector's month. |
| 205 | 3 | `public double equity()` _(in SectorBooks.SectorMonth)_ | What the sheet says the owners have. |
| 209 | 3 | `public double totalAssets()` _(in SectorBooks.SectorMonth)_ |  |
| 214 | 3 | `public double operatingCost()` _(in SectorBooks.SectorMonth)_ | Everything above operating income: goods bought, payroll, utilities, repairs. |
| 227 | 10 | `public double unexplained()` _(in SectorBooks.SectorMonth)_ | What the cash flow statement adds up to, against what the cash actually is. |
| 238 | 3 | `public double margin()` _(in SectorBooks.SectorMonth)_ |  |
| 243 | 11 | `public static SectorMonth none(String sector)` _(in SectorBooks.SectorMonth)_ | An empty month, for a sector that has not been recorded yet. |
| 255 | 3 | `public boolean isEmpty()` _(in SectorBooks.SectorMonth)_ |  |

### THE TWO MONTHS (lines 260-289)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 1 | `public SectorMonth get(Sector sector)` |  |
| 271 | 1 | `public SectorMonth previous(Sector sector)` |  |
| 273 | 3 | `public SectorMonth get(String key)` |  |
| 277 | 3 | `public SectorMonth previous(String key)` |  |
| 281 | 4 | `public boolean hasComparatives()` |  |
| 286 | 3 | `public boolean isEmpty()` |  |

### THE MONTH (lines 290-376)

| line | len | member | says |
|---:|---:|---|---|
| 299 | 14 | `public void takeMonth(Game game)` |  |
| 324 | 52 | `private SectorMonth read(Game game, Sector sector)` | One sector's figures, off the statement it struck this month and the flows the city recorded against its name. |

### SAVE AND RESTORE (lines 377-416)

| line | len | member | says |
|---:|---:|---|---|
| 390 | 1 | `public java.util.List<SectorMonth> thisMonth()` |  |
| 391 | 1 | `public java.util.List<SectorMonth> lastMonth()` |  |
| 393 | 3 | `private static java.util.List<SectorMonth> list(Map<String, SectorMonth> from)` |  |
| 397 | 19 | `public void restoreFrom(java.util.List<SectorMonth> saved, java.util.List<SectorMonth> savedBefore)` |  |

### A REFORM (lines 417-450)

| line | len | member | says |
|---:|---:|---|---|
| 428 | 5 | `public void redenominate(double scale)` |  |
| 434 | 16 | `private static SectorMonth scaled(SectorMonth m, double s)` |  |

