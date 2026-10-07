# OilCheck.java - 592 lines · 19 methods · 1 constants · harnesses

`ham/citybuildersim/OilCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> Fuel, from the oil in the ground to the drivers' tanks (0.7.62, batch K;
> the project's spec-land.md 2.7 and section 3's K entry).
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE GOODS AND THE BUILDINGS ARE THE SPEC'S. Crude at US$550 a tonne,
>      fuel at a journey's $2.00 over its 1.2 litres - so a city with no
>      refinery pays what it always paid a journey - freight by Good's rule,
>      oil in the ground at 5% of the crude's price, and two sectors on the
>      end of the registry.
> 
>   2. A WELL LIFTS ONLY OWNED OIL. No oil site, no well - the order, the
>      card, the investors all refuse it; an offer with oil bought is one
>      well a site; and what the wells lift is exactly what leaves the
>      ground, the world's oil conserved to the tonne.
> 
>   3. THE OIL RUNS OUT, AND THE WELLS RETIRE, as the mines do when the ore
>      does.
> 
>   4. THE REFINERY TAKES THE WELLS' CRUDE FIRST and imports the rest.
> 
>   5. THE DRIVERS AND THE RAILWAY TAKE THE REFINERS' FUEL FIRST, and import
>      only what the tanks do not hold.
> 
>   6. FUEL'S MONEY AUDIT CLOSES in every month of a city with wells and a
>      refinery: the imported part is the households' FuelImports, the
>      domestic part is on Refining's statement, and the goods foot to the
>      balance of payments.
> 
>   7. WITH NO REFINERY, THE HOUSEHOLDS PAY TODAY'S BILL AT THE WORLD'S PRICE
>      LEVEL, as every good is priced - the railway's fuel always was.
> 
>   8. A REFINERY IS BUILT FOR THE CITY'S OWN FUEL OR ITS OWN CRUDE, a whole
>      plant's worth (Refining.plan(), the star the playtest measured: 120
>      export refineries without it), and its estimate pays for the crude it
>      would have to import at the import price.
> 
>   9. THE MONTH'S FUEL CROSSES A SAVE: the bill, the imports and the litres
>      as 6d drew them, and a save from before them derives what that month
>      struck - every litre imported.
> 
> Every fixture CAUSES its condition: the city is handed its oil the way
> MiningCheck hands a city its ore (LandManager.restoreSites()), except in
> section 2, which buys it.

**Uses:** [Good](Good.md) (31), [Game](Game.md) (29), [Resource](Resource.md) (23), [Sectors](Sectors.md) (12), [Motoring](Motoring.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (9), [CityLand](CityLand.md) (9), [Refining](Refining.md) (7), [World](World.md) (6), [BusinessInvestment](BusinessInvestment.md) (5), [GameFiles](GameFiles.md) (4), [BuildingManager](BuildingManager.md) (4), [Rail](Rail.md) (4), [Sector](Sector.md) (4), [GoodsMarket](GoodsMarket.md) (4), [LandManager](LandManager.md) (3), [LandParcel](LandParcel.md) (3), [BuildCard](BuildCard.md) (3), [Oil](Oil.md) (3), [TaxPolicy](TaxPolicy.md) (3), [Deposit](Deposit.md) (2), [MoneyAudit](MoneyAudit.md) (2), [Traffic](Traffic.md) (2), [LandMarket](LandMarket.md) (2), [BuildingType](BuildingType.md) (2), [Founding](Founding.md) (1), [Markets](Markets.md) (1)

## Sections

| line | section |
|---:|---|
| 179 | 1. THE GOODS AND THE BUILDINGS |
| 238 | 2. A WELL LIFTS ONLY OWNED OIL |
| 305 | 3. THE OIL RUNS OUT |
| 343 | 4. THE REFINERY TAKES THE WELLS' CRUDE FIRST |
| 384 | 5. THE DRIVERS AND THE RAILWAY |
| 432 | 6. THE AUDIT |
| 469 | 7. NO REFINERY |
| 499 | 8. WHEN A REFINERY IS BUILT |
| 544 | 9. ACROSS A SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 93 | `OilCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder, for the reloads (section 9). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 60 | `static int fails` |  |
| 61 | `static PrintStream out` |  |
| 62 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 535 | **type** `public class OilCheck` | Fuel, from the oil in the ground to the drivers' tanks (0.7.62, batch K; the project's spec-land.md 2.7 and section 3's K entry). |
| 64 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 69 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 74 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 80 | 5 | `static void quietly(Runnable r)` |  |
| 86 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 102 | 19 | `static Game town(String label, int houses, double spare)` | A town that pays its people and drives: houses, shops, bakeries to work in, power, water, roads and builders, standing, on ground a quarter more than they take (and `spare` more square feet) - small enough that its ce... |
| 123 | 21 | `static LandParcel towardOil(Game game)` | The offer standing in the lane of the oil field nearest the city's site that it does not own (MiningCheck.towardIron(), on oil). |
| 146 | 9 | `static double auditLine(MoneyAudit.Result r, String label)` | One audit line's figure, read off the month's detail (to the cent, as the detail writes it); 0 when the line is absent. |
| 156 | 22 | `public static void main(String[] args) throws Exception` |  |

### 1. THE GOODS AND THE BUILDINGS (lines 179-237)

| line | len | member | says |
|---:|---:|---|---|
| 181 | 56 | `static void goodsAndBuildings()` |  |

### 2. A WELL LIFTS ONLY OWNED OIL (lines 238-304)

| line | len | member | says |
|---:|---:|---|---|
| 240 | 64 | `static void onlyOwnedOil()` |  |

### 3. THE OIL RUNS OUT (lines 305-342)

| line | len | member | says |
|---:|---:|---|---|
| 307 | 35 | `static void theOilRunsOut()` |  |

### 4. THE REFINERY TAKES THE WELLS' CRUDE FIRST (lines 343-383)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 38 | `static Game refineryTakesLocalCrude()` |  |

### 5. THE DRIVERS AND THE RAILWAY (lines 384-431)

| line | len | member | says |
|---:|---:|---|---|
| 386 | 45 | `static void driversAndRailwayTakeLocalFuel(Game g)` |  |

### 6. THE AUDIT (lines 432-468)

| line | len | member | says |
|---:|---:|---|---|
| 434 | 34 | `static void theAudit(Game g)` |  |

### 7. NO REFINERY (lines 469-498)

| line | len | member | says |
|---:|---:|---|---|
| 471 | 27 | `static void noRefinery()` |  |

### 8. WHEN A REFINERY IS BUILT (lines 499-543)

| line | len | member | says |
|---:|---:|---|---|
| 501 | 42 | `static void whenARefineryIsBuilt()` |  |

### 9. ACROSS A SAVE (lines 544-592)

| line | len | member | says |
|---:|---:|---|---|
| 546 | 39 | `static void acrossASave(Game g) throws Exception` |  |
| 586 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |

