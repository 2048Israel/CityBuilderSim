# SectorState.java - 185 lines · 6 methods · 0 constants · model

`ham/citybuildersim/SectorState.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> One sector, as a save carries it.
> 
> KEYED BY NAME, EVERYWHERE. The sector by its key, the stocks by the good's
> name, the purchases by the supplier's key, the extras by whatever the
> sector called them. Gson matches by name, so a save from a build with a
> sector or a good this one does not have loses that line and not the load,
> and a build that adds a field reads zero from an older save - which is
> what the older city had.
> 
> This replaced five differently-shaped report arrays, three arrays indexed
> by BuildingType.ordinal(), and a dozen loose fields (commercialCash,
> industryFoodInventory, retailFillBasis...) that DataSave carried one by
> one. Jerus: "clean break" - the save format moved, and nothing here reads
> the old shape.

**Uses:** [Sector](Sector.md) (16), [Good](Good.md) (13), [Statement](Statement.md) (4)

**Used by (5):** [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [Sector](Sector.md), [Sectors](Sectors.md), [VanCheck](VanCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `public String key` |  |
| 25 | `public double cash` |  |
| 28 | `public double interest, propertyTax, maintenance, taxRate` | The three bills of the month and the rate it was taxed at, as set at the top of it. |
| 39 | `public boolean vansKnown` | Whether this sector's van fleet is a fact about the sector. |
| 41 | `public Map<String, Double> stock` |  |
| 42 | `public Map<String, Double> pantry` |  |
| 43 | `public Map<String, Double> pantryUsed` |  |
| 45 | `public LedgerState ledger` |  |
| 46 | `public StatementState statement` |  |
| 58 | `public double atHome, abroad` |  |
| 86 | `public Map<String, Double> extras` | A sector's own state - a price it walks, an order book - by name. |
| 90 | `public double localSales, exports, otherRevenue, imports, salesToHouseholds` |  |
| 91 | `public Map<String, Double> purchasesBySupplier` |  |
| 92 | `public Map<String, Double> unitsSold` |  |
| 93 | `public Map<String, Double> unitsBought` |  |
| 94 | `public Map<String, SplitState> sold` |  |
| 95 | `public Map<String, SplitState> bought` |  |
| 97 | `public Map<String, Double> otherInputs` | The named non-goods part of the month's purchases. |
| 140 | `public double revenue, inputs, payroll, electricity, water, maintenance` |  |
| 141 | `public double operatingIncome, interest, propertyTax, salesTax, preTaxIncome, profitTax, netIncome` |  |
| 142 | `public double localSales, exports, otherRevenue, salesToHouseholds, localPurchases, imports` |  |
| 143 | `public Map<String, Double> purchasesBySupplier` |  |
| 144 | `public Map<String, SplitState> sold` |  |
| 145 | `public Map<String, SplitState> bought` |  |
| 146 | `public Map<String, Double> otherParts` |  |
| 148 | `public Map<String, Double> otherInputs` | The named non-goods part of the input line. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 164 | **type** `public final class SectorState` | One sector, as a save carries it. |
| 57 | 3 | **type** `public static final class SplitState` | A good's money in a save: what it sold or bought at home and abroad. |
| 61 | 10 | `static Map<String, SplitState> splitsOf(Map<Good, Sector.Split> from)` |  |
| 72 | 12 | `static Map<Good, Sector.Split> splitsTo(Map<String, SplitState> from)` |  |
| 89 | 48 | **type** `public static final class LedgerState` | The month in progress, unstruck. |
| 99 | 15 | `static LedgerState of(Sector.Ledger l)` _(in SectorState.LedgerState)_ |  |
| 115 | 21 | `Sector.Ledger toLedger()` _(in SectorState.LedgerState)_ |  |
| 139 | 46 | **type** `public static final class StatementState` | The month last struck. |
| 150 | 16 | `static StatementState of(Sector.Statement t)` _(in SectorState.StatementState)_ |  |
| 167 | 17 | `Sector.Statement toStatement()` _(in SectorState.StatementState)_ |  |

