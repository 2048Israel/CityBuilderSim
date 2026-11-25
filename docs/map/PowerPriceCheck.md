# PowerPriceCheck.java - 628 lines · 27 methods · 9 constants · harnesses

`ham/citybuildersim/PowerPriceCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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
>   5. THE UTILITY'S MARGIN IS THE TREASURY'S LINE: income = revenue - payroll - power bought - fuel bill + the water works' power the water's
>      books buy (PW1-W), and the services' net the treasury takes carries it.
>   6. THE FUEL'S MONEY REACHES ITS SELLERS: the coal the book burned is what the market drew for the city, what Coal Mining
>      was paid is fuelLocal, what the world was paid fuelImported, and the upkeep is the book's at the exchange rate.
>   7. THE MONEY AUDIT CLOSES over every month of every fixture, with the coal bought at home and abroad, and the two TRADE
>      lines (utilities FuelImports, utilities PlantUpkeep) carry the imports and the upkeep.
> 
> Every expected figure is the model's own constant or getter; every fixture CAUSES its condition: the founding grid is free
> (UtilitiesHandler.BASE_GRID_KW, a fuelled row at marginal 0) and a town it covers clears at nothing, so the homes' draw is raised
> as MoneyCheck does, and the fixture asserts the coal row did burn before a section reads a price.

**Uses:** [PowerDispatch](PowerDispatch.md) (30), [Game](Game.md) (18), [UtilitiesHandler](UtilitiesHandler.md) (16), [Sectors](Sectors.md) (16), [Good](Good.md) (9), [MoneyAudit](MoneyAudit.md) (7), [Sector](Sector.md) (6), [Resource](Resource.md) (4), [Substations](Substations.md) (4), [PriceLevel](PriceLevel.md) (4), [GameFiles](GameFiles.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [Founding](Founding.md) (2), [LandManager](LandManager.md) (2), [BuildingManager](BuildingManager.md) (2), [Trade](Trade.md) (2), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (1)

**Used by (6):** [HistoryCheck](HistoryCheck.md), [PowerPlantsCheck](PowerPlantsCheck.md), [PowerViewCheck](PowerViewCheck.md), [SaveFileCheck](SaveFileCheck.md), [SubstationCheck](SubstationCheck.md), [WaterPowerCheck](WaterPowerCheck.md)

## Sections

| line | section |
|---:|---|
| 96 | THE FIXTURES |
| 256 | · the old save, for SaveFileCheck (REV-PW1c) |
| 309 | MAIN |
| 330 | · the fixture, before any section |
| 345 | · 1 |
| 398 | · 2 |
| 432 | · 3 |
| 470 | · 4 |
| 536 | · 5 |
| 564 | · 6 |
| 598 | · 7 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 46 | `PowerPriceCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 49 | `PowerPriceCheck.HOMES` | `400` | How many homes a town stands on: enough that their draw outruns the founding grid. |
| 52 | `PowerPriceCheck.HOME_KW` | `50` | Each home's draw in a town, kW: MoneyCheck's and WoodProductsCheck's lever, so 400 homes outrun the founding grid and the coal plant is the margin. |
| 55 | `PowerPriceCheck.SHORT_ASK` | `2` | How many times the coal plant's production the short town's homes ask of the grid: past the plant and the founding grid in every season. |
| 58 | `PowerPriceCheck.OLD_SAVE_DESAL` | `2` | How many desalination plants the old save's town stands on the founding grid alone: their draw, 10,880 kW each, is past its 10,000 kW in every month. |
| 61 | `PowerPriceCheck.SHOCK` | `3` | How many times the coal's price the shocked city's dispatch is told. |
| 64 | `PowerPriceCheck.WARM` | `8` | The months a town is played before a section reads it: the plants are staffed and the mine is dug and stocked. |
| 138 | `PowerPriceCheck.audits` | `new ArrayList<>()` |  |
| 139 | `PowerPriceCheck.auditCities` | `new ArrayList<>()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `static int fails` |  |
| 103 | `int month` | The month before it ran. |
| 105 | `UtilitiesHandler.Book book, struckAfter` | The book its statements were struck at (struck in the month before), and the book it struck. |
| 107 | `final Map<String, double[]> held` | Per sector key before the month: draw, ratio, high-voltage share, commerce price, industry price. |
| 109 | `final Map<String, Double> bills` | Per sector key after the month: the electricity its statement struck. |
| 111 | `double coalHeld` | The coal Coal Mining held before the month. |
| 113 | `double fuelLocal, fuelImported, upkeep, revenue, payroll, bought, income, waterIncome, net, treasury, charg...` | After: the fuel paid, the margin's terms, the treasury's line. |
| 115 | `double localUnits, importedUnits, localCost, importedCost` | After: the coal drawn for the city this month: units from Coal Mining, units from the world, what each cost. |
| 117 | `double exchange, minePaidAtHome, latestEnergyPrice, latestCoalMarginal, latestCoalKwh` | After: the exchange rate, Coal Mining's statement's coal sold at home, the coal row of the dispatch the month ended on. |
| 118 | `MoneyAudit.Result audit` |  |
| 122 | `final String label` |  |
| 123 | `final Game g` |  |
| 124 | `final UtilitiesHandler u` |  |
| 125 | `final boolean mine` |  |
| 126 | `final List<Step> steps` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 43 | 586 | **type** `public class PowerPriceCheck` | The month's power at the market's price, through the game's own month (PW1, 2026-11-11; Jerus's Q1: "Market price"): the struck book's prices, what each class is billed at them, the coal the plants burn bought from th... |
| 66 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 72 | 1 | `static void check(String label, boolean ok)` |  |
| 75 | 3 | `static boolean near(double a, double b, double tol)` | Whether two figures agree to tol of the larger. |
| 79 | 1 | `static String f(double v)` |  |
| 81 | 4 | `static void section(String title)` |  |
| 86 | 3 | `static void sectionEnd()` |  |
| 90 | 5 | `static void quietly(Runnable work)` |  |

### THE FIXTURES (lines 96-255)

| line | len | member | says |
|---:|---:|---|---|
| 101 | 19 | **type** `static final class Step` | One month run: what the strike at its top read before it ran, and what stood after it. |
| 121 | 16 | **type** `static final class City` |  |
| 128 | 6 | `City(String label, Game g, boolean mine)` _(in PowerPriceCheck.City)_ |  |
| 135 | 1 | `Step last()` _(in PowerPriceCheck.City)_ |  |
| 147 | 33 | `static City town(String label, boolean mine, double ask)` | A town: founded, the Wealthy preset's cash and ground, homes, shops, a coal plant, water and roads (MaterialsCheck.town()'s list), standing at once (buildStack(..., noConstruction): LongPlaytest.build() orders real co... |
| 182 | 10 | `static double[] cityDraw(Game g, Good good)` | What the city drew of a good from the market this month, by the draw log the clearing kept with the month's fills: {units local, units imported, cost local, cost imported}. |
| 193 | 4 | `static PowerDispatch.Row coalRow(UtilitiesHandler u)` |  |
| 199 | 43 | `static Step run(City c)` | Runs one month of a city and keeps what the harness reads: the strike's book and the sectors' draws before, the statements and the fuel's money after. |
| 243 | 4 | `static City play(City c, int months)` |  |
| 249 | 6 | `static double unitOf(Game g)` | The unit the city's money constants are struck at: Game.restrikeMoneyConstants()'s own, the denomination's unit over the struck level. |

### the old save, for SaveFileCheck (REV-PW1c) (lines 256-308)

| line | len | member | says |
|---:|---:|---|---|
| 266 | 42 | `static boolean saveChecks() throws Exception` | A save from before PW1 (format 58, no powerBooks) of a city short of power loads with the ratio its load dispatched in its book, as the load handed the sectors before PW1, not the founding book's 1, and its first mont... |

### MAIN (lines 309-329)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 16 | `public static void main(String[] args) throws Exception` |  |

### the fixture, before any section (lines 330-344)

| line | len | member | says |
|---:|---:|---|---|
| 332 | 12 | `static void fixture(City c)` |  |

### 1 (lines 345-397)

| line | len | member | says |
|---:|---:|---|---|
| 348 | 9 | `static double energyPriceOf(UtilitiesHandler.Book b)` | The energy price recomputed from the book's own half-blocks: the prices weighted by the energy each served, over the energy served, over (1 - losses). |
| 358 | 23 | `static void pricesOfBook(String who, Game g, UtilitiesHandler.Book b)` |  |
| 382 | 15 | `static void struckPrice(City c)` |  |

### 2 (lines 398-431)

| line | len | member | says |
|---:|---:|---|---|
| 400 | 31 | `static void classBills(City c)` |  |

### 3 (lines 432-469)

| line | len | member | says |
|---:|---:|---|---|
| 434 | 35 | `static void shortCity()` |  |

### 4 (lines 470-535)

| line | len | member | says |
|---:|---:|---|---|
| 472 | 63 | `static void fuelShock()` |  |

### 5 (lines 536-563)

| line | len | member | says |
|---:|---:|---|---|
| 538 | 25 | `static void margin(City c)` |  |

### 6 (lines 564-597)

| line | len | member | says |
|---:|---:|---|---|
| 566 | 31 | `static void fuelMoney(City c)` |  |

### 7 (lines 598-628)

| line | len | member | says |
|---:|---:|---|---|
| 600 | 28 | `static void auditCloses(City c)` |  |

