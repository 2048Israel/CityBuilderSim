# PowerPriceCheck.java - 560 lines · 26 methods · 8 constants · harnesses

`ham/citybuildersim/PowerPriceCheck.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

> The month's power at the market's price, through the game's own month (PW1, 2026-11-11; Jerus's Q1: "Market price"): the struck
> book's prices, what each class is billed at them, the coal the plants burn bought from the market, the utility's margin and the
> money audit. PowerDispatchCheck proves the dispatch on bare rows; this proves the money, on real cities.
> 
> THE TIMING, which every section reads by. The dispatch at the end of month t-1 is struck in month t's updateEconomy
> (UtilitiesHandler.strikePower()); its ratio is month t's, its prices are what the statements are struck at at the top of month
> t+1, and its coal is drawn from the market at that strike and paid in t+1's servicesNet. So a harness reads the book a statement is
> struck at, and the draw and ratio the sector holds, BEFORE the month that strikes it runs (Step), and the statement after it.
> 
> What this has to prove:
>   1. THE STRUCK PRICE IS THE DISPATCH'S: commerce pays (energy + transmission + distribution) x month's hours, industry
>      (energy + transmission), and the energy price is the half-blocks' prices weighted by the energy each served, over the
>      energy served, over (1 - losses), recomputed from the book's own price[], load[] and unserved[].
>   2. EACH CLASS'S BILL IS ITS PRICE ON WHAT WAS DELIVERED, EXACTLY: a commerce sector's statement is draw x ratio x the
>      commerce price, a Mining sector's (every building high-voltage) draw x ratio x the industry price; industry pays no
>      distribution.
>   3. A SHORT HALF-BLOCK BILLS THE CAP: on a city asking twice the plant, every short half-block carries the struck VOLL, and
>      the month's ratio is the energy served over the energy asked.
>   4. A FUEL SHOCK MOVES THE NEXT MONTH'S BILLS: two copies of one city, one with the coal's price tripled in the dispatch: its
>      coal row's marginal, its energy price, its struck prices and then its commerce bills are higher.
>   5. THE UTILITY'S MARGIN IS THE TREASURY'S LINE: income = revenue - payroll - power bought - fuel bill, and the services' net
>      the treasury takes carries it.
>   6. THE FUEL'S MONEY REACHES ITS SELLERS: the coal the book burned is what the market drew for the city, what Coal Mining
>      was paid is fuelLocal, what the world was paid fuelImported, and the upkeep is the book's at the exchange rate.
>   7. THE MONEY AUDIT CLOSES over every month of every fixture, with the coal bought at home and abroad, and the two TRADE
>      lines (utilities FuelImports, utilities PlantUpkeep) carry the imports and the upkeep.
> 
> Every expected figure is the model's own constant or getter; every fixture CAUSES its condition: the founding grid is free
> (UtilitiesHandler.BASE_GRID_KW, a fuelled row at marginal 0) and a town it covers clears at nothing, so the homes' draw is raised
> as MoneyCheck does, and the fixture asserts the coal row did burn before a section reads a price.

**Uses:** [PowerDispatch](PowerDispatch.md) (31), [Sectors](Sectors.md) (15), [UtilitiesHandler](UtilitiesHandler.md) (13), [Game](Game.md) (10), [Good](Good.md) (9), [MoneyAudit](MoneyAudit.md) (7), [Sector](Sector.md) (5), [Resource](Resource.md) (4), [PriceLevel](PriceLevel.md) (4), [Trade](Trade.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [GameFiles](GameFiles.md) (1), [Founding](Founding.md) (1), [LandManager](LandManager.md) (1), [BuildingManager](BuildingManager.md) (1)

## Sections

| line | section |
|---:|---|
| 93 | THE FIXTURES |
| 246 | MAIN |
| 267 | · the fixture, before any section |
| 282 | · 1 |
| 332 | · 2 |
| 366 | · 3 |
| 404 | · 4 |
| 470 | · 5 |
| 496 | · 6 |
| 530 | · 7 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 46 | `PowerPriceCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 49 | `PowerPriceCheck.HOMES` | `400` | How many homes a town stands on: enough that their draw outruns the founding grid. |
| 52 | `PowerPriceCheck.HOME_KW` | `50` | Each home's draw in a town, kW: MoneyCheck's and WoodProductsCheck's lever, so 400 homes outrun the founding grid and the coal plant is the margin. |
| 55 | `PowerPriceCheck.SHORT_ASK` | `2` | How many times the coal plant's production the short town's homes ask of the grid: past the plant and the founding grid in every season. |
| 58 | `PowerPriceCheck.SHOCK` | `3` | How many times the coal's price the shocked city's dispatch is told. |
| 61 | `PowerPriceCheck.WARM` | `8` | The months a town is played before a section reads it: the plants are staffed and the mine is dug and stocked. |
| 135 | `PowerPriceCheck.audits` | `new ArrayList<>()` |  |
| 136 | `PowerPriceCheck.auditCities` | `new ArrayList<>()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `static int fails` |  |
| 100 | `int month` | The month before it ran. |
| 102 | `UtilitiesHandler.Book book, struckAfter` | The book its statements were struck at (struck in the month before), and the book it struck. |
| 104 | `final Map<String, double[]> held` | Per sector key before the month: draw, ratio, high-voltage share, commerce price, industry price. |
| 106 | `final Map<String, Double> bills` | Per sector key after the month: the electricity its statement struck. |
| 108 | `double coalHeld` | The coal Coal Mining held before the month. |
| 110 | `double fuelLocal, fuelImported, upkeep, revenue, payroll, bought, income, waterIncome, net, treasury, charges` | After: the fuel paid, the margin's terms, the treasury's line. |
| 112 | `double localUnits, importedUnits, localCost, importedCost` | After: the coal drawn for the city this month: units from Coal Mining, units from the world, what each cost. |
| 114 | `double exchange, minePaidAtHome, latestEnergyPrice, latestCoalMarginal, latestCoalKwh` | After: the exchange rate, Coal Mining's statement's coal sold at home, the coal row of the dispatch the month ended on. |
| 115 | `MoneyAudit.Result audit` |  |
| 119 | `final String label` |  |
| 120 | `final Game g` |  |
| 121 | `final UtilitiesHandler u` |  |
| 122 | `final boolean mine` |  |
| 123 | `final List<Step> steps` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 43 | 518 | **type** `public class PowerPriceCheck` | The month's power at the market's price, through the game's own month (PW1, 2026-11-11; Jerus's Q1: "Market price"): the struck book's prices, what each class is billed at them, the coal the plants burn bought from th... |
| 63 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 69 | 1 | `static void check(String label, boolean ok)` |  |
| 72 | 3 | `static boolean near(double a, double b, double tol)` | Whether two figures agree to tol of the larger. |
| 76 | 1 | `static String f(double v)` |  |
| 78 | 4 | `static void section(String title)` |  |
| 83 | 3 | `static void sectionEnd()` |  |
| 87 | 5 | `static void quietly(Runnable work)` |  |

### THE FIXTURES (lines 93-245)

| line | len | member | says |
|---:|---:|---|---|
| 98 | 19 | **type** `static final class Step` | One month run: what the strike at its top read before it ran, and what stood after it. |
| 118 | 16 | **type** `static final class City` |  |
| 125 | 6 | `City(String label, Game g, boolean mine)` _(in PowerPriceCheck.City)_ |  |
| 132 | 1 | `Step last()` _(in PowerPriceCheck.City)_ |  |
| 144 | 27 | `static City town(String label, boolean mine, double ask)` | A town: founded, the Wealthy preset's cash and ground, homes, shops, a coal plant, water and roads (MaterialsCheck.town()'s list), standing at once (buildStack(..., noConstruction): LongPlaytest.build() orders real co... |
| 173 | 10 | `static double[] cityDraw(Game g, Good good)` | What the city drew of a good from the market this month, by the draw log the clearing kept with the month's fills: {units local, units imported, cost local, cost imported}. |
| 184 | 4 | `static PowerDispatch.Row coalRow(UtilitiesHandler u)` |  |
| 190 | 42 | `static Step run(City c)` | Runs one month of a city and keeps what the harness reads: the strike's book and the sectors' draws before, the statements and the fuel's money after. |
| 233 | 4 | `static City play(City c, int months)` |  |
| 239 | 6 | `static double unitOf(Game g)` | The unit the city's money constants are struck at: Game.restrikeMoneyConstants()'s own, the denomination's unit over the struck level. |

### MAIN (lines 246-266)

| line | len | member | says |
|---:|---:|---|---|
| 250 | 16 | `public static void main(String[] args) throws Exception` |  |

### the fixture, before any section (lines 267-281)

| line | len | member | says |
|---:|---:|---|---|
| 269 | 12 | `static void fixture(City c)` |  |

### 1 (lines 282-331)

| line | len | member | says |
|---:|---:|---|---|
| 285 | 9 | `static double energyPriceOf(UtilitiesHandler.Book b)` | The energy price recomputed from the book's own half-blocks: the prices weighted by the energy each served, over the energy served, over (1 - losses). |
| 295 | 20 | `static void pricesOfBook(String who, Game g, UtilitiesHandler.Book b)` |  |
| 316 | 15 | `static void struckPrice(City c)` |  |

### 2 (lines 332-365)

| line | len | member | says |
|---:|---:|---|---|
| 334 | 31 | `static void classBills(City c)` |  |

### 3 (lines 366-403)

| line | len | member | says |
|---:|---:|---|---|
| 368 | 35 | `static void shortCity()` |  |

### 4 (lines 404-469)

| line | len | member | says |
|---:|---:|---|---|
| 406 | 63 | `static void fuelShock()` |  |

### 5 (lines 470-495)

| line | len | member | says |
|---:|---:|---|---|
| 472 | 23 | `static void margin(City c)` |  |

### 6 (lines 496-529)

| line | len | member | says |
|---:|---:|---|---|
| 498 | 31 | `static void fuelMoney(City c)` |  |

### 7 (lines 530-560)

| line | len | member | says |
|---:|---:|---|---|
| 532 | 28 | `static void auditCloses(City c)` |  |

