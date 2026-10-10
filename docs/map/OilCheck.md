# OilCheck.java - 1,982 lines · 37 methods · 9 constants · harnesses

`ham/citybuildersim/OilCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> Fuel, from the oil in the ground to the drivers' tanks (0.7.62, batch K;
> the project's spec-land.md 2.7 and section 3's K entry).
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE GOODS AND THE BUILDINGS ARE THE SPEC'S. Crude at US$550 a tonne,
>      fuel at a journey's $2.00 over its 1.2 litres - so a city with no
>      refinery pays what it always paid a journey - freight by Good's rule,
>      oil in the ground at 5% of the crude's price, and two sectors on the
>      end of the registry. SINCE 0.7.76 (batch O1; runs/spec-oil.md 2.1,
>      2.3): FUEL is nine products - all at the research's wholesale ladder
>      (petrol and diesel since 0.7.78, batch O2; at FUEL's band before),
>      each litre weighing its good's tonne
>      over its litres - and the refinery a crude unit whose products are
>      THE SLATE of its crude: a tonne of medium crude into 70 L of petrol
>      and 169 L of diesel, not a thousand litres of fuel, the litres
>      balancing to the litre.
> 
>   2. A WELL LIFTS ONLY OWNED OIL. No oil site, no well - the order, the
>      card, the investors all refuse it; an offer with oil bought is one
>      well a site; and what the wells lift is exactly what leaves the
>      ground, the world's oil conserved to the tonne. A well has one post,
>      a diploma's (0.7.84, batch O7; spec-oil 2.6, the research's Q13 -
>      three until then); its decline, its life and its dry sites are
>      WellCheck's.
> 
>   3. THE OIL RUNS OUT, AND THE WELLS RETIRE, as the mines do when the ore
>      does.
> 
>   4. THE REFINERY TAKES THE WELLS' CRUDE FIRST and imports the rest.
> 
>   5. THE DRIVERS AND THE RAILWAY TAKE THE REFINERS' FUEL FIRST, and import
>      only what the tanks do not hold - the drivers' petrol and the
>      railway's diesel since 0.7.76. The drivers' since 0.7.83 (batch O6)
>      through the grocers' forecourts, which draw it at the refiners'
>      price and sell it on at the pump's (sectors.Retail, THE FORECOURTS).
> 
>   6. FUEL'S MONEY AUDIT CLOSES in every month of a city with wells and a
>      refinery: the imported part is the households' PetrolImports (their
>      FuelImports until 0.7.76) - since 0.7.83 the forecourts' import, on
>      Retail's books, and the households' own nothing - the domestic part
>      is on Refining's statement (a sale to the forecourts since 0.7.83,
>      the drivers' bill Retail's sale to them), and the goods foot to the
>      balance of payments - and the
>      tanks, shared among the products, write none of them off: what nobody
>      here buys fills its share to the dump line, then ships - since 0.7.98
>      (batch O14) whatever its share of the crude: the run's every product
>      leaves, nothing of it idled, in months when its share of the line's
>      whole bill, the crude among it, would not have shipped it (until
>      then it idled: on the wholesale ladder, 0.7.78, a medium crude's
>      slate).
> 
>   7. WITH NO REFINERY, THE HOUSEHOLDS PAY TODAY'S BILL AT THE WORLD'S PRICE
>      LEVEL, as every good is priced - the railway's fuel always was. Since
>      0.7.78 (batch O2) the bill is a journey's litres at petrol's place on
>      the wholesale ladder, 63% under 0.7.49's journey at the pump price -
>      the forecourts' wholesale bill since 0.7.83, the drivers paying the
>      pump's price on it.
> 
>   8. A REFINERY IS BUILT FOR THE CITY'S OWN FUEL OR ITS OWN CRUDE
> ... (83 more lines in the source)

**Uses:** [Good](Good.md) (159), [Refining](Refining.md) (107), [Game](Game.md) (62), [Resource](Resource.md) (43), [Sectors](Sectors.md) (37), [Retail](Retail.md) (27), [Sector](Sector.md) (24), [BuildingsTemplate](BuildingsTemplate.md) (19), [World](World.md) (15), [Deposit](Deposit.md) (15), [Motoring](Motoring.md) (12), [BusinessInvestment](BusinessInvestment.md) (11), [FuelSplit](FuelSplit.md) (11), [GameFiles](GameFiles.md) (10), [CityLand](CityLand.md) (9), [Rail](Rail.md) (9), [LandManager](LandManager.md) (7), [NationalAccounts](NationalAccounts.md) (7), [BuildingManager](BuildingManager.md) (6), [LandParcel](LandParcel.md) (5), [GoodsMarket](GoodsMarket.md) (5), [SpreadPlanner](SpreadPlanner.md) (5), [Founding](Founding.md) (4), [MoneyAudit](MoneyAudit.md) (4), [StrategicReserve](StrategicReserve.md) (4), [Oil](Oil.md) (4), [TaxPolicy](TaxPolicy.md) (4), [MiningCheck](MiningCheck.md) (3), [Traffic](Traffic.md) (3), [BuildCard](BuildCard.md) (3)... and 12 more

**Used by (1):** [WellCheck](WellCheck.md)

## Sections

| line | section |
|---:|---|
| 293 | 15. THE TANK FARM (0.7.85) |
| 414 | 16. THE STRATEGIC RESERVE (0.7.85) |
| 610 | 13. THE PHASE-1 BUYERS (0.7.83) |
| 673 | 14. THE FORECOURTS (0.7.83) |
| 777 | 1. THE GOODS AND THE BUILDINGS |
| 920 | 2. A WELL LIFTS ONLY OWNED OIL |
| 989 | 3. THE OIL RUNS OUT |
| 1027 | 4. THE REFINERY TAKES THE WELLS' CRUDE FIRST |
| 1094 | 5. THE DRIVERS AND THE RAILWAY |
| 1166 | 6. THE AUDIT |
| 1272 | 7. NO REFINERY |
| 1326 | 8. WHEN A REFINERY IS BUILT |
| 1406 | 9. ACROSS A SAVE |
| 1461 | 10. A SAVE FROM BEFORE 0.7.76 |
| 1685 | 11. A REFINERY THAT CLOSES |
| 1721 | 12. CRUDE BY GRADE (0.7.79) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 200 | `OilCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder, for the reloads (section 9). |
| 780 | `OilCheck.PRODUCTS` | `{ Good.LPG, Good.NAPHTHA, Good.PETROL, Good.JET, Good.DIESEL, Good.LUBRICANTS...` | The nine products of 0.7.76 (spec-oil 2.1), in the spec's table's order. |
| 784 | `OilCheck.LITRES_A_TONNE` | `{ 1850, 1351, 1320, 1260, 1180, 1127, 1010, Double.NaN, Double.NaN }` | The spec's table (2.1): litres a tonne for the litre goods ([P35]; naphtha and lubricants est., JODI), NaN for the two in tonnes. |
| 787 | `OilCheck.LADDER` | `{.46, 1.0, 1.20, 1.28, 1.35, 1.89,.98, 1.08,.155 }` | ...and each one's ratio to crude's world middle (the research's ladder, the prototype's RATIO): a litre's, or a tonne's for bitumen and coke. |
| 790 | `OilCheck.PUMP_LITRE` | `Motoring.CAR_FUEL_PER_JOURNEY / Motoring.LITRES_PER_JOURNEY` | FUEL's import price, 0.7.62 to 0.7.75 (petrol's and diesel's at 0.7.76 and 0.7.77): a journey's pump price over its litres. |
| 793 | `OilCheck.PETROL_CUT_PCT` | `63, DIESEL_CUT_PCT = 59` | What the ladder takes off the pump price, as the spec gives it (spec-oil 1, item 4): petrol 63%, diesel 59%, to the percent. |
| 796 | `OilCheck.FOUR_FIGURES` | `5e-4` | The spec's four figures: a price within half a unit in its fourth significant figure. |
| 1729 | `OilCheck.SEA_OIL_SEED` | `518` | The world whose founding site's nearest oil field in the sea is heavy and shallow enough for a jacket: 518, its field 3.5 km out - heavy, 12 sites (a jacket's slots), 58 m deep (fixW1-notes.md, the seed scan) - what s... |
| 1732 | `OilCheck.FIAT_TONNES` | `1_000` | Tonnes of oil a fixture's centre is handed by fiat ahead of the field, so a lift crosses from it into the field: 1,000. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 162 | `static int fails` |  |
| 163 | `static PrintStream out` |  |
| 164 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 160 | 1823 | **type** `public class OilCheck` | Fuel, from the oil in the ground to the drivers' tanks (0.7.62, batch K; the project's spec-land.md 2.7 and section 3's K entry). |
| 166 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 171 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 176 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 183 | 3 | `static boolean same(double a, double b)` | Bitwise, NaN equal to NaN: a figure that crossed a save (0.7.83). |
| 187 | 5 | `static void quietly(Runnable r)` |  |
| 193 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 210 | 19 | `static Game town(String label, int houses, double spare)` | A town that pays its people and drives: houses, shops, bakeries to work in, power, water, roads and builders, standing, on ground a quarter more than they take (and `spare` more square feet) - small enough that its ce... |
| 231 | 20 | `static LandParcel towardOil(Game game)` | The offer standing nearest the oil field on dry ground nearest the city's site that it does not own (MiningCheck.towardIron(), on oil): what a land well stands on (since 0.7.84 a dry site; the nearest field may lie in... |
| 253 | 9 | `static double auditLine(MoneyAudit.Result r, String label)` | One audit line's figure, read off the month's detail (to the cent, as the detail writes it); 0 when the line is absent. |
| 263 | 29 | `public static void main(String[] args) throws Exception` |  |

### 15. THE TANK FARM (0.7.85) (lines 293-413)

| line | len | member | says |
|---:|---:|---|---|
| 296 | 5 | `static double slateLitres(Refining.Slate one)` | The litres a tonne of medium crude's slate makes, every product: what the tanks are shared by with no unit standing (Refining.getStockCapacity()). |
| 309 | 104 | `static void theTankFarm() throws Exception` | A 600-house town with an Oil Refinery (held, as section 4's is) on imported crude, then a Tank Farm beside it: the store, the room, the order; two months with the refiners' till emptied, so their order for crude - sto... |

### 16. THE STRATEGIC RESERVE (0.7.85) (lines 414-609)

| line | len | member | says |
|---:|---:|---|---|
| 417 | 8 | `static double journalled(Game g, String label)` | The amount of a journal line last month (Game.getTreasuryJournal()), summed; NaN when absent. |
| 434 | 175 | `static void theStrategicReserve() throws Exception` | A 600-house town with four wells and room for a Strategic Reserve: no room, no fill; a fill more than the wells lift, with no refinery to share their crude; the strike after; a refinery (held) and a release it takes a... |

### 13. THE PHASE-1 BUYERS (0.7.83) (lines 610-672)

| line | len | member | says |
|---:|---:|---|---|
| 620 | 52 | `static void phaseOneBuyers()` | A town that drives, with two filling stations, two fabrication shops, a machine works and a vehicle works, and a Paved Road ordered: its vans burn diesel, its factories lubricants, the road bitumen and its drivers pet... |

### 14. THE FORECOURTS (0.7.83) (lines 673-776)

| line | len | member | says |
|---:|---:|---|---|
| 675 | 101 | `static void theForecourts() throws Exception` |  |

### 1. THE GOODS AND THE BUILDINGS (lines 777-919)

| line | len | member | says |
|---:|---:|---|---|
| 798 | 121 | `static void goodsAndBuildings()` |  |

### 2. A WELL LIFTS ONLY OWNED OIL (lines 920-988)

| line | len | member | says |
|---:|---:|---|---|
| 922 | 66 | `static void onlyOwnedOil()` |  |

### 3. THE OIL RUNS OUT (lines 989-1026)

| line | len | member | says |
|---:|---:|---|---|
| 991 | 35 | `static void theOilRunsOut()` |  |

### 4. THE REFINERY TAKES THE WELLS' CRUDE FIRST (lines 1027-1093)

| line | len | member | says |
|---:|---:|---|---|
| 1029 | 64 | `static Game refineryTakesLocalCrude()` |  |

### 5. THE DRIVERS AND THE RAILWAY (lines 1094-1165)

| line | len | member | says |
|---:|---:|---|---|
| 1096 | 69 | `static void driversAndRailwayTakeLocalFuel(Game g)` |  |

### 6. THE AUDIT (lines 1166-1271)

| line | len | member | says |
|---:|---:|---|---|
| 1168 | 103 | `static void theAudit(Game g)` |  |

### 7. NO REFINERY (lines 1272-1325)

| line | len | member | says |
|---:|---:|---|---|
| 1274 | 51 | `static void noRefinery()` |  |

### 8. WHEN A REFINERY IS BUILT (lines 1326-1405)

| line | len | member | says |
|---:|---:|---|---|
| 1328 | 68 | `static void whenARefineryIsBuilt()` |  |
| 1398 | 7 | `static double packageEarns(Refining refiners, BusinessInvestment plans, Game g, Refining.Outlook o, double local, double imported)` | The Oil Refinery's package earnings at an outlook with its crude's two prices raised by `local` and `imported` (Refining.packageEstimate()). |

### 9. ACROSS A SAVE (lines 1406-1460)

| line | len | member | says |
|---:|---:|---|---|
| 1408 | 52 | `static void acrossASave(Game g) throws Exception` |  |

### 10. A SAVE FROM BEFORE 0.7.76 (lines 1461-1684)

| line | len | member | says |
|---:|---:|---|---|
| 1464 | 13 | `static void foldUnits(com.google.gson.JsonObject parent, String key)` | A JSON map of numbers by good name: the nine folded into one FUEL figure, as a 0.7.75 refinery made only FUEL. |
| 1479 | 19 | `static void foldMoney(com.google.gson.JsonObject parent, String key)` | ...and a map of money by good name, each side apart. |
| 1499 | 6 | `static com.google.gson.JsonObject sectorOf(com.google.gson.JsonObject save, String key)` |  |
| 1506 | 6 | `static com.google.gson.JsonObject marketOf(com.google.gson.JsonObject save, String good)` |  |
| 1513 | 4 | `static double money(com.google.gson.JsonObject sector, String part, String map, String side)` |  |
| 1528 | 156 | `static Game theSplit(Game g) throws Exception` | A FORMAT-33 SAVE (0.7.75's shape), made from the oil town by folding the nine products back into FUEL - its market (PETROL's price, the nine's strike and history summed), every sector's maps, and the goods held four w... |

### 11. A REFINERY THAT CLOSES (lines 1685-1720)

| line | len | member | says |
|---:|---:|---|---|
| 1693 | 27 | `static void aRefineryCloses(Game g)` | Its refinery bought out and pulled down (the player's hand, so no planner decides it): the next month its tanks are gone, and what they held - the dead stock of the products nobody here buys among it - is shipped at t... |

### 12. CRUDE BY GRADE (0.7.79) (lines 1721-1982)

| line | len | member | says |
|---:|---:|---|---|
| 1735 | 18 | `static Deposit nearestSeaOil(World w)` | The oil field in the sea nearest a world's founding site, among the nine cells round it; null when there is none. |
| 1755 | 10 | `static double[] gradesBetween(double[][] runs, double from, double to)` | The grades of the oil from `from` to `to` tonnes into the city's, by its runs (LandManager.oilRuns()), in Deposit.Grade's order. |
| 1766 | 209 | `static void crudeByGrade() throws Exception` |  |
| 1976 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |

