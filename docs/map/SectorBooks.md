# SectorBooks.java - 392 lines · 21 methods · 0 constants · model

`ham/citybuildersim/SectorBooks.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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

**Used by (10):** [BusinessServicesCheck](BusinessServicesCheck.md), [CrimeCheck](CrimeCheck.md), [DataSave](DataSave.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [PolicyScreen](PolicyScreen.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 216 | THE TWO MONTHS |
| 246 | THE MONTH |
| 321 | SAVE AND RESTORE |
| 361 | A REFORM |

## Fields (state)

| line | field | says |
|---:|---|---|
| 220 | `private final Map<String, SectorMonth> now` |  |
| 221 | `private final Map<String, SectorMonth> before` |  |
| 224 | `private final Map<String, Double> lastCash` | Closing cash from the month just recorded, which is next month's opening. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 362 | **type** `public final class SectorBooks` | A month of books for every business in the city, and last month's too. |
| 40 | 175 | **type** `public record SectorMonth(String sector, int month, double revenue, double inputs, double payroll, double e...` | One sector's month. |
| 165 | 3 | `public double equity()` _(in SectorBooks.SectorMonth)_ | What the sheet says the owners have. |
| 169 | 3 | `public double totalAssets()` _(in SectorBooks.SectorMonth)_ |  |
| 174 | 3 | `public double operatingCost()` _(in SectorBooks.SectorMonth)_ | Everything above operating income: goods bought, payroll, utilities, repairs. |
| 187 | 7 | `public double unexplained()` _(in SectorBooks.SectorMonth)_ | What the cash flow statement adds up to, against what the cash actually is. |
| 195 | 3 | `public double margin()` _(in SectorBooks.SectorMonth)_ |  |
| 200 | 10 | `public static SectorMonth none(String sector)` _(in SectorBooks.SectorMonth)_ | An empty month, for a sector that has not been recorded yet. |
| 211 | 3 | `public boolean isEmpty()` _(in SectorBooks.SectorMonth)_ |  |

### THE TWO MONTHS (lines 216-245)

| line | len | member | says |
|---:|---:|---|---|
| 226 | 1 | `public SectorMonth get(Sector sector)` |  |
| 227 | 1 | `public SectorMonth previous(Sector sector)` |  |
| 229 | 3 | `public SectorMonth get(String key)` |  |
| 233 | 3 | `public SectorMonth previous(String key)` |  |
| 237 | 4 | `public boolean hasComparatives()` |  |
| 242 | 3 | `public boolean isEmpty()` |  |

### THE MONTH (lines 246-320)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 14 | `public void takeMonth(Game game)` |  |
| 280 | 40 | `private SectorMonth read(Game game, Sector sector)` | One sector's figures, off the statement it struck this month and the flows the city recorded against its name. |

### SAVE AND RESTORE (lines 321-360)

| line | len | member | says |
|---:|---:|---|---|
| 334 | 1 | `public java.util.List<SectorMonth> thisMonth()` |  |
| 335 | 1 | `public java.util.List<SectorMonth> lastMonth()` |  |
| 337 | 3 | `private static java.util.List<SectorMonth> list(Map<String, SectorMonth> from)` |  |
| 341 | 19 | `public void restoreFrom(java.util.List<SectorMonth> saved, java.util.List<SectorMonth> savedBefore)` |  |

### A REFORM (lines 361-392)

| line | len | member | says |
|---:|---:|---|---|
| 372 | 5 | `public void redenominate(double scale)` |  |
| 378 | 14 | `private static SectorMonth scaled(SectorMonth m, double s)` |  |

