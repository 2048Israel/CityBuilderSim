# SectorState.java - 274 lines · 6 methods · 0 constants · model

`ham/citybuildersim/SectorState.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

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

**Used by (12):** [BooksCheck](BooksCheck.md), [DataSave](DataSave.md), [EconomyManager](EconomyManager.md), [FreightViewCheck](FreightViewCheck.md), [FuelSplit](FuelSplit.md), [HomeRailCheck](HomeRailCheck.md), [RailCheck](RailCheck.md), [Sector](Sector.md), [SectorBooksCheck](SectorBooksCheck.md), [Sectors](Sectors.md), [TruckCheck](TruckCheck.md), [VanCheck](VanCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `public String key` |  |
| 25 | `public double cash` |  |
| 28 | `public double interest, propertyTax, maintenance, taxRate` | The three bills of the month and the rate it was taxed at, as set at the top of it. |
| 42 | `public Double energyRatio, waterRatio, roadRatio, healthRatio` | ...and the four ratios its month was run at - power, water, the road and the clinics - as the sector held them when the save was taken (0.7.63). |
| 53 | `public boolean vansKnown` | Whether this sector's van fleet is a fact about the sector. |
| 55 | `public Map<String, Double> stock` |  |
| 56 | `public Map<String, Double> pantry` |  |
| 57 | `public Map<String, Double> pantryUsed` |  |
| 59 | `public LedgerState ledger` |  |
| 60 | `public StatementState statement` |  |
| 70 | `public double[] borrowingChargesLeft` | What its borrowing cost it up front and it is still to expense (0.7.102, A16): the month's charge by how many months it has still to run (index k, k months), to its last charge. |
| 82 | `public Map<String, Double> exported, imported` | The month's trade in units by good name: what it shipped and what it landed in the month the save was taken (A1, 0.7.46). |
| 91 | `public Map<String, Double> boughtLocal` | ...and the units of each good it bought at home in that month (FR2, 2026-10-18): the third of a bulk tonne's city-leg tonnes, which Freight's Haulage bills at the top of the next month with the two above (Sector.units... |
| 100 | `public Map<String, Double> writtenOff` | ...and the units of each good written off in that month (0.7.133, batch I8): stock a yard sold back or demolished could not hold, which the national accounts add back at the next strike - see Sector, WHAT A MONTH WROT... |
| 112 | `public double atHome, abroad` |  |
| 140 | `public Map<String, Double> extras` | A sector's own state - a price it walks, an order book - by name. |
| 144 | `public double localSales, exports, otherRevenue, imports, salesToHouseholds` |  |
| 145 | `public Map<String, Double> purchasesBySupplier` |  |
| 146 | `public Map<String, Double> unitsSold` |  |
| 147 | `public Map<String, Double> unitsBought` |  |
| 148 | `public Map<String, SplitState> sold` |  |
| 149 | `public Map<String, SplitState> bought` |  |
| 151 | `public Map<String, Double> otherInputs` | The named non-goods part of the month's purchases. |
| 153 | `public double paidEarlier` | Sector.Ledger.paidEarlier (0.7.8); an older save reads none. |
| 155 | `public Map<String, Double> capitalBySupplier` | Sector.Ledger.capitalBySupplier (0.7.19): the builders' work on its premises this month, which the tax credit is struck on at the next strike. |
| 157 | `public double oldMetalSold` | Sector.Ledger.oldMetalSold (0.7.123, batch I4): its old steel sold to the yards this month, named apart at the strike. |
| 159 | `public double paidToCity` | Sector.Ledger.paidToCity (FR3): the dues it pays the city this month, in its inputs. |
| 210 | `public double revenue, inputs, payroll, electricity, water, maintenance` |  |
| 211 | `public double operatingIncome, interest, propertyTax, salesTax, preTaxIncome, profitTax, netIncome` |  |
| 212 | `public double localSales, exports, otherRevenue, salesToHouseholds, localPurchases, imports` |  |
| 213 | `public Map<String, Double> purchasesBySupplier` |  |
| 214 | `public Map<String, SplitState> sold` |  |
| 215 | `public Map<String, SplitState> bought` |  |
| 216 | `public Map<String, Double> otherParts` |  |
| 218 | `public Map<String, Double> otherInputs` | The named non-goods part of the input line. |
| 220 | `public double paidEarlier` | Sector.Statement.paidEarlier (0.7.8); an older save reads none. |
| 222 | `public Map<String, Double> capitalBySupplier` | Sector.Statement.capitalBySupplier and capitalTaxCredit (0.7.19); an older save reads none. |
| 223 | `public double capitalTaxCredit` |  |
| 225 | `public double borrowingCosts` | Sector.Statement.borrowingCosts (0.7.102, A16); an older save reads none, which is what it expensed. |
| 227 | `public double powerSold` | Sector.Statement.powerSold (0.7.112, batch I2); an older save reads none, which is what it sold. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 253 | **type** `public final class SectorState` | One sector, as a save carries it. |
| 111 | 3 | **type** `public static final class SplitState` | A good's money in a save: what it sold or bought at home and abroad. |
| 115 | 10 | `static Map<String, SplitState> splitsOf(Map<Good, Sector.Split> from)` |  |
| 126 | 12 | `static Map<Good, Sector.Split> splitsTo(Map<String, SplitState> from)` |  |
| 143 | 64 | **type** `public static final class LedgerState` | The month in progress, unstruck. |
| 161 | 19 | `static LedgerState of(Sector.Ledger l)` _(in SectorState.LedgerState)_ |  |
| 181 | 25 | `Sector.Ledger toLedger()` _(in SectorState.LedgerState)_ |  |
| 209 | 65 | **type** `public static final class StatementState` | The month last struck. |
| 229 | 21 | `static StatementState of(Sector.Statement t)` _(in SectorState.StatementState)_ |  |
| 251 | 22 | `Sector.Statement toStatement()` _(in SectorState.StatementState)_ |  |

