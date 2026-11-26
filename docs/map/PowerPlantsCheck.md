# PowerPlantsCheck.java - 672 lines · 23 methods · 16 constants · harnesses

`ham/citybuildersim/PowerPlantsCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> The power plants and their fuels (PW2, 2026-11-23; spec-electricity 2.3 and 2.5): the Oil-Fired Plant, the Diesel Station, the
> Oil-Fired Turbine, the Biomass Plant and the Solar Farm, and the Coal Power Plant and the Wind Farm refigured, each burning its fuel
> from the market by the MWh, each offered from its year, the steam plants standing only on owned water, and each standing no sooner
> than its months. PowerDispatchCheck proves the merit order on bare rows and PowerPriceCheck the coal's money; this proves the rows.
> 
> WHAT THIS HAS TO PROVE:
>   1. THE ROWS ARE THE STUDY'S: each row's price is its all-in cost a kW times its nameplate by HY1's rule (cash, points, materials),
>      its output its nameplate at its availability over the game's month, its fuel a MWh the study's from its heat rate and its fuel's
>      heat content, its ground, posts, cooling, months and year the table's; every ELECTRICITY row is dated.
>   2. THE SUN RUNS AT ITS HOURS: a solar row makes its average x solarAt() in each half-block, nothing in the dark halves of the base
>      and the shoulder, after the wind and before the rest; a solar farm standing in a town is dispatched so and booked as the sun.
>   3. EACH FUEL IS BOUGHT FROM A REAL SELLER AND PAID ONCE: the fuelled rows run by their marginal cost, both sides (on the same diesel
>      the turbine, the lower heat rate, runs first, and the station only past it); a town on each new fuelled row burns that fuel by the dispatch's kWh x its
>      fuel a kWh; the market draws it for the city, what a seller at home holds from it (Refining's fuel oil and diesel, Wood Products'
>      pellets) and the rest from the world; the seller's statement books the sale, the audit's FuelImports line and the Trade tab's
>      good the import, once; the audit closes every month.
>   4. THE COOLING GATE, both sides: a city that owns no sea, lake or river is refused a steam plant (NO_WATER: the order, the card, a
>      run, the advice and the automatic builder) and not a closed-loop or dry row; a city that owns water is not refused one.
>   5. THE BUILD-TIME FLOOR, both sides: a site of a row with months is worked no faster than its points over its months, so one
>      stands in exactly its months with crews to spare and a quote waits them; a building with none is worked as fast as the crews go.
>      A site's preparation is not held to it: a station whose ground owes ten times its points still stands in its months.
>      An order's quote counts a site the floor holds at its cap, not its weight's share; one the floor does not hold, to the bit.
>   6. EACH ROW FROM ITS YEAR (Q2), both sides: a row is known from its since and not the month before, in the build menu, the
>      advice's list and the order; a 1900 city knows the coal and oil plants and not the diesel station, and it knows the diesel
>      station from 1910.
>   7. THE SUN IN A SAVE (the old-saves rule): a book's solar energy round-trips, and a book saved before PW2 reads none.
> 
> Every fixture CAUSES its condition: the homes' draw is raised past the founding grid, a plant of a year not yet come stands by
> fiat, a seller is stocked with half the month's burn, a dry city is founded where its rings hold no water, and a site's crews are
> handed more than it can use. Every city is a scratch game (GameFiles.scratch); none is saved.

**Uses:** [PowerDispatch](PowerDispatch.md) (51), [Game](Game.md) (32), [UtilitiesHandler](UtilitiesHandler.md) (20), [BuildAdvice](BuildAdvice.md) (19), [BuildingsTemplate](BuildingsTemplate.md) (15), [Good](Good.md) (13), [World](World.md) (11), [Sectors](Sectors.md) (9), [BuildingManager](BuildingManager.md) (8), [BuildingType](BuildingType.md) (7), [BuildCard](BuildCard.md) (6), [MoneyAudit](MoneyAudit.md) (4), [LandManager](LandManager.md) (3), [Founding](Founding.md) (3), [PriceLevel](PriceLevel.md) (2), [CityLand](CityLand.md) (2), [Substations](Substations.md) (2), [AutoBuilder](AutoBuilder.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [ResearchCatalog](ResearchCatalog.md) (2), [GameFiles](GameFiles.md) (1), [Sector](Sector.md) (1), [PowerPriceCheck](PowerPriceCheck.md) (1), [SubstationCheck](SubstationCheck.md) (1), [PolicyDates](PolicyDates.md) (1)

## Sections

| line | section |
|---:|---|
| 149 | THE FIXTURES |
| 233 | 1. THE ROWS ARE THE STUDY'S |
| 287 | 2. THE SUN RUNS AT ITS HOURS |
| 348 | 3. EACH FUEL IS BOUGHT FROM A REAL SELLER AND PAID ONCE |
| 426 | 4. THE COOLING GATE |
| 509 | 5. THE BUILD-TIME FLOOR |
| 584 | 6. EACH ROW FROM ITS YEAR |
| 651 | 7. THE SUN IN A SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 47 | `PowerPlantsCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 50 | `PowerPlantsCheck.CASH_PER_THOUSAND` | `23240.0 / 31800` | HY1's cash per thousand dollars of all-in cost: the Wind Farm's old cash, 23,240, for its $31.8M (docs/queue/HY1-notes.md). |
| 53 | `PowerPlantsCheck.POINTS_PER_CASH` | `120000.0 / 1071816` | HY1's construction points to cash: the coal plant's old 120,000 points over its old 1,071,816 of cash. |
| 56 | `PowerPlantsCheck.MATERIALS_SHARE` | `0.0185` | HY1's construction materials: both old plants' 1.85% of cash. |
| 59 | `PowerPlantsCheck.HOURS` | `730.0 / 720` | A row's production1 is its average kW in the game's 720-hour month: a real month's 730 hours over 720 (HY1's arithmetic). |
| 62 | `PowerPlantsCheck.SQ_FT_PER_ACRE` | `43_560` | Square feet in an acre. |
| 65 | `PowerPlantsCheck.HOMES` | `400` | How many homes a town stands on, and each home's draw, kW: PowerPriceCheck's lever, past the founding grid's 10,000 kW. |
| 66 | `PowerPlantsCheck.HOME_KW` | `50` |  |
| 69 | `PowerPlantsCheck.DEPOTS` | `16` | Construction Depots a city short of power is given, so the automatic builder's plant is not held for builders. |
| 72 | `PowerPlantsCheck.WARM` | `8` | The months a town is played before a section reads it: the plant is staffed and has burned. |
| 75 | `PowerPlantsCheck.AMPLE` | `100_000_000` | Site output handed to a site that can use less: past any row's points over its months. |
| 82 | `PowerPlantsCheck.STUDY` | `List.of(new Study("Coal Power Plant", 3271, 325_000, 0.85, 60, true, Good.THE...` | The modern study's table (spec-electricity 2.3) plus PW9's sourced historical station: $/kW, nameplate, availability or capacity factor, build months, cooling, fuel, Btu a kWh, fuel a MWh, acres, posts, year. |
| 95 | `PowerPlantsCheck.FUELLED` | `{ { "Oil-Fired Plant", "FUEL_OIL", Sectors.REFINING }, { "Diesel Station", "D...` | The new fuelled rows, the good each burns and the sector at home that makes it. |
| 154 | `PowerPlantsCheck.MORE_GROUND` | `400_000_000L` | Ground a fixture city is given past its founding's, sq ft: room for any plant and the town round it (PowerPriceCheck's 400,000,000). |
| 224 | `PowerPlantsCheck.audits` | `new ArrayList<>()` |  |
| 225 | `PowerPlantsCheck.auditAt` | `new ArrayList<>()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `static int fails` |  |
| 238 | `static BuildingManager catalogue` | The catalogue section 1 read, for the sections that need only templates. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 629 | **type** `public class PowerPlantsCheck` | The power plants and their fuels (PW2, 2026-11-23; spec-electricity 2.3 and 2.5): the Oil-Fired Plant, the Diesel Station, the Oil-Fired Turbine, the Biomass Plant and the Solar Farm, and the Coal Power Plant and the ... |
| 78 | 2 | **type** `record Study(String name, double usdPerKw, double nameplateKw, double runs, int months, boolean cooling, Go...` | One row of spec-electricity 2.3, as the study prints it. |
| 102 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 108 | 1 | `static void check(String label, boolean ok)` |  |
| 110 | 3 | `static boolean near(double a, double b, double tol)` |  |
| 114 | 1 | `static String f(double v)` |  |
| 116 | 4 | `static void section(String title)` |  |
| 121 | 1 | `static void sectionEnd()` |  |
| 123 | 5 | `static void quietly(Runnable work)` |  |
| 129 | 5 | `static<T> T hushed(java.util.function.Supplier<T> work)` |  |
| 135 | 13 | `public static void main(String[] args) throws Exception` |  |

### THE FIXTURES (lines 149-232)

| line | len | member | says |
|---:|---:|---|---|
| 157 | 12 | `static Game city(String label, boolean dry)` | A founded city, the Wealthy preset's cash and MORE_GROUND more ground; dry, the whole of it founded where it holds no sea, lake or river. |
| 171 | 23 | `static boolean dryLand(Game g, double sqFt)` | Founds a new city's ground at sqFt, round the nearest cell of its world where all of it is dry: CityLand.found() with no fresh patch (CoalMiningCheck.coalTown()'s install). |
| 196 | 22 | `static Game town(String label, String plant, int count)` | A town on one plant: HOMES homes drawing HOME_KW each, shops, water and roads standing at once (PowerPriceCheck.town()'s list), the plant stood by fiat whatever its year, and the wires its peak needs. |
| 219 | 4 | `static PowerDispatch.Row rowOf(UtilitiesHandler u, String name)` |  |
| 227 | 5 | `static void month(Game g, String label)` |  |

### 1. THE ROWS ARE THE STUDY'S (lines 233-286)

| line | len | member | says |
|---:|---:|---|---|
| 240 | 46 | `static void rows()` |  |

### 2. THE SUN RUNS AT ITS HOURS (lines 287-347)

| line | len | member | says |
|---:|---:|---|---|
| 291 | 56 | `static void sun()` |  |

### 3. EACH FUEL IS BOUGHT FROM A REAL SELLER AND PAID ONCE (lines 348-425)

| line | len | member | says |
|---:|---:|---|---|
| 352 | 73 | `static void fuels()` |  |

### 4. THE COOLING GATE (lines 426-508)

| line | len | member | says |
|---:|---:|---|---|
| 436 | 15 | `static void shortOfPower(Game g)` | A city short of power with its wires standing, ready for the automatic builder's pass (EssentialWorksCheck.scarceCash()'s levers, with cash to spare): its homes draw past the founding grid, the substations its forecas... |
| 452 | 8 | `static AutoBuilder.Step powerStep(Game g)` |  |
| 461 | 47 | `static void cooling()` |  |

### 5. THE BUILD-TIME FLOOR (lines 509-583)

| line | len | member | says |
|---:|---:|---|---|
| 513 | 70 | `static void floor()` |  |

### 6. EACH ROW FROM ITS YEAR (lines 584-650)

| line | len | member | says |
|---:|---:|---|---|
| 588 | 62 | `static void years()` |  |

### 7. THE SUN IN A SAVE (lines 651-672)

| line | len | member | says |
|---:|---:|---|---|
| 655 | 17 | `static void save()` |  |

