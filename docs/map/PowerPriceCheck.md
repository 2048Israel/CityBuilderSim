# PowerPriceCheck.java - 747 lines · 31 methods · 9 constants · harnesses

`ham/citybuildersim/PowerPriceCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The month's power at the market's price, through the game's own month (PW1, 2026-11-11; Jerus's Q1: "Market price"): the struck
> book's prices, what each class is billed at them, the coal the plants burn bought from the market, the utility's margin and the
> money audit. PowerDispatchCheck proves the dispatch on bare rows; this proves the money, on real cities.
> 
> THE TIMING, which every section reads by. The dispatch at the end of month t-1 is struck in month t's updateEconomy
> (UtilitiesHandler.strikePower()); its ratio is month t's, its prices are what the statements are struck at at the top of month
> t+1, and its coal is drawn from the market at that strike and paid in t+1's servicesNet. So a harness reads the book a statement is
> struck at, and the draw and ratio the sector holds, BEFORE the month that strikes it runs (Step), and the statement after it.
> Its season is month t-1's too: month t's statement bills the kWh that dispatch served, June's season for July's (UtilitiesHandler's
> THE LAG, PW1-R), and the coal bought at the same strike is that dispatch's.
> 
> What this has to prove:
>   1. THE STRUCK PRICE IS THE DISPATCH'S: commerce pays (energy + transmission + distribution) x month's hours, industry
>      (energy + transmission), and the energy price is the half-blocks' prices weighted by the energy each served, over the
>      energy served, over (1 - losses), recomputed from the book's own price[], load[] and unserved[].
>   2. EACH CLASS'S BILL IS ITS PRICE ON WHAT WAS DELIVERED, EXACTLY: a commerce sector's statement is draw x ratio x season x
>      the commerce price, a Mining sector's (every building high-voltage) draw x ratio x season x the industry price, the season
>      the book's dispatch ran at (PW1-R: the kWh served, not the year's mean); industry pays no distribution.
>   3. A SHORT HALF-BLOCK BILLS THE CAP: on a city asking twice the plant, every short half-block carries the struck VOLL, and
>      the month's ratio is the energy served over the energy asked.
>   4. A FUEL SHOCK MOVES THE NEXT MONTH'S BILLS: two copies of one city, one with the coal's price tripled in the dispatch: its
>      coal row's marginal, its energy price, its struck prices and then its commerce bills are higher.
>   5. THE UTILITY'S MARGIN IS THE TREASURY'S LINE: income = revenue - payroll - power bought - fuel bill + the water works' power the water's
>      books buy (PW1-W), and the services' net the treasury takes carries it.
>   6. THE FUEL'S MONEY REACHES ITS SELLERS: the coal the book burned is what the market drew for the city, what Coal Mining
>      was paid is fuelLocal, what the world was paid fuelImported, and the upkeep is the book's at the exchange rate.
>   7. IN JULY AND IN APRIL EACH CLASS'S BILL IS THE KWH IT WAS SERVED AT THE PRICE (PW1-R): the town played on through its own
>      calendar to a book struck at a July dispatch and one at an April dispatch, the season read off the game's calendar at the
>      city's latitude; each class's statement is draw x ratio x season x the month's hours x its price a kWh, the mean-kWh bill
>      times the season (above it in July, under it in April), and the utility's revenue is every billed class's kWh served at its
>      price, so its margin no longer takes the season's kWh.
>   8. THE MONEY AUDIT CLOSES over every month of every fixture, with the coal bought at home and abroad, and the two TRADE
>      lines (utilities FuelImports, utilities PlantUpkeep) carry the imports and the upkeep.
> 
> Every expected figure is the model's own constant or getter; every fixture CAUSES its condition: the founding grid is free
> (UtilitiesHandler.BASE_GRID_KW, a fuelled row at marginal 0) and a town it covers clears at nothing, so the homes' draw is raised
> as MoneyCheck does, and the fixture asserts the coal row did burn before a section reads a price.

**Uses:** [PowerDispatch](PowerDispatch.md) (32), [UtilitiesHandler](UtilitiesHandler.md) (22), [Game](Game.md) (20), [Sectors](Sectors.md) (20), [Good](Good.md) (9), [MoneyAudit](MoneyAudit.md) (7), [Sector](Sector.md) (6), [GameFiles](GameFiles.md) (4), [Resource](Resource.md) (4), [Substations](Substations.md) (4), [PriceLevel](PriceLevel.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [Founding](Founding.md) (2), [LandManager](LandManager.md) (2), [BuildingManager](BuildingManager.md) (2), [Trade](Trade.md) (2), [CityCalendar](CityCalendar.md) (2), [HistoricalLicenceCheck](HistoricalLicenceCheck.md) (1)

**Used by (6):** [HistoryCheck](HistoryCheck.md), [PowerPlantsCheck](PowerPlantsCheck.md), [PowerViewCheck](PowerViewCheck.md), [SaveFileCheck](SaveFileCheck.md), [SubstationCheck](SubstationCheck.md), [WaterPowerCheck](WaterPowerCheck.md)

## Sections

| line | section |
|---:|---|
| 103 | THE FIXTURES |
| 263 | · the old save, for SaveFileCheck (REV-PW1c) |
| 316 | · the struck book through a save, for SaveFileCheck (PW1-R) |
| 361 | MAIN |
| 383 | · the fixture, before any section |
| 398 | · 1 |
| 451 | · 2 |
| 489 | · 3 |
| 527 | · 4 |
| 593 | · 5 |
| 621 | · 6 |
| 655 | · 7 |
| 717 | · 8 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 53 | `PowerPriceCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 56 | `PowerPriceCheck.HOMES` | `400` | How many homes a town stands on: enough that their draw outruns the founding grid. |
| 59 | `PowerPriceCheck.HOME_KW` | `50` | Each home's draw in a town, kW: MoneyCheck's and WoodProductsCheck's lever, so 400 homes outrun the founding grid and the coal plant is the margin. |
| 62 | `PowerPriceCheck.SHORT_ASK` | `2` | How many times the coal plant's production the short town's homes ask of the grid: past the plant and the founding grid in every season. |
| 65 | `PowerPriceCheck.OLD_SAVE_DESAL` | `2` | How many desalination plants the old save's town stands on the founding grid alone: their draw, 10,880 kW each, is past its 10,000 kW in every month. |
| 68 | `PowerPriceCheck.SHOCK` | `3` | How many times the coal's price the shocked city's dispatch is told. |
| 71 | `PowerPriceCheck.WARM` | `8` | The months a town is played before a section reads it: the plants are staffed and the mine is dug and stocked. |
| 145 | `PowerPriceCheck.audits` | `new ArrayList<>()` |  |
| 146 | `PowerPriceCheck.auditCities` | `new ArrayList<>()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `static int fails` |  |
| 110 | `int month` | The month before it ran. |
| 112 | `UtilitiesHandler.Book book, struckAfter` | The book its statements were struck at (struck in the month before), and the book it struck. |
| 114 | `final Map<String, double[]> held` | Per sector key before the month: draw, ratio, high-voltage share, commerce price, industry price. |
| 116 | `final Map<String, Double> bills` | Per sector key after the month: the electricity its statement struck. |
| 118 | `double coalHeld` | The coal Coal Mining held before the month. |
| 120 | `double fuelLocal, fuelImported, upkeep, revenue, payroll, bought, income, waterIncome, net, treasury, charg...` | After: the fuel paid, the margin's terms, the treasury's line. |
| 122 | `double localUnits, importedUnits, localCost, importedCost` | After: the coal drawn for the city this month: units from Coal Mining, units from the world, what each cost. |
| 124 | `double exchange, minePaidAtHome, latestEnergyPrice, latestCoalMarginal, latestCoalKwh` | After: the exchange rate, Coal Mining's statement's coal sold at home, the coal row of the dispatch the month ended on. |
| 125 | `MoneyAudit.Result audit` |  |
| 129 | `final String label` |  |
| 130 | `final Game g` |  |
| 131 | `final UtilitiesHandler u` |  |
| 132 | `final boolean mine` |  |
| 133 | `final List<Step> steps` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 698 | **type** `public class PowerPriceCheck` | The month's power at the market's price, through the game's own month (PW1, 2026-11-11; Jerus's Q1: "Market price"): the struck book's prices, what each class is billed at them, the coal the plants burn bought from th... |
| 73 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 79 | 1 | `static void check(String label, boolean ok)` |  |
| 82 | 3 | `static boolean near(double a, double b, double tol)` | Whether two figures agree to tol of the larger. |
| 86 | 1 | `static String f(double v)` |  |
| 88 | 4 | `static void section(String title)` |  |
| 93 | 3 | `static void sectionEnd()` |  |
| 97 | 5 | `static void quietly(Runnable work)` |  |

### THE FIXTURES (lines 103-262)

| line | len | member | says |
|---:|---:|---|---|
| 108 | 19 | **type** `static final class Step` | One month run: what the strike at its top read before it ran, and what stood after it. |
| 128 | 16 | **type** `static final class City` |  |
| 135 | 6 | `City(String label, Game g, boolean mine)` _(in PowerPriceCheck.City)_ |  |
| 142 | 1 | `Step last()` _(in PowerPriceCheck.City)_ |  |
| 154 | 33 | `static City town(String label, boolean mine, double ask)` | A town: founded, the Wealthy preset's cash and ground, homes, shops, a coal plant, water and roads (MaterialsCheck.town()'s list), standing at once (buildStack(..., noConstruction): LongPlaytest.build() orders real co... |
| 189 | 10 | `static double[] cityDraw(Game g, Good good)` | What the city drew of a good from the market this month, by the draw log the clearing kept with the month's fills: {units local, units imported, cost local, cost imported}. |
| 200 | 4 | `static PowerDispatch.Row coalRow(UtilitiesHandler u)` |  |
| 206 | 43 | `static Step run(City c)` | Runs one month of a city and keeps what the harness reads: the strike's book and the sectors' draws before, the statements and the fuel's money after. |
| 250 | 4 | `static City play(City c, int months)` |  |
| 256 | 6 | `static double unitOf(Game g)` | The unit the city's money constants are struck at: Game.restrikeMoneyConstants()'s own, the denomination's unit over the struck level. |

### the old save, for SaveFileCheck (REV-PW1c) (lines 263-315)

| line | len | member | says |
|---:|---:|---|---|
| 273 | 42 | `static boolean saveChecks() throws Exception` | A save from before PW1 (format 58, no powerBooks) of a city short of power loads with the ratio its load dispatched in its book, as the load handed the sectors before PW1, not the founding book's 1, and its first mont... |

### the struck book through a save, for SaveFileCheck (PW1-R) (lines 316-360)

| line | len | member | says |
|---:|---:|---|---|
| 325 | 35 | `static boolean bookChecks() throws Exception` | A saved city that holds a struck book reads it back field for field (REV-PW1's third follow-up: the only round trip was in memory, PowerDispatchCheck 10, so the Gson read-back of Book was unchecked), through the save'... |

### MAIN (lines 361-382)

| line | len | member | says |
|---:|---:|---|---|
| 365 | 17 | `public static void main(String[] args) throws Exception` |  |

### the fixture, before any section (lines 383-397)

| line | len | member | says |
|---:|---:|---|---|
| 385 | 12 | `static void fixture(City c)` |  |

### 1 (lines 398-450)

| line | len | member | says |
|---:|---:|---|---|
| 401 | 9 | `static double energyPriceOf(UtilitiesHandler.Book b)` | The energy price recomputed from the book's own half-blocks: the prices weighted by the energy each served, over the energy served, over (1 - losses). |
| 411 | 23 | `static void pricesOfBook(String who, Game g, UtilitiesHandler.Book b)` |  |
| 435 | 15 | `static void struckPrice(City c)` |  |

### 2 (lines 451-488)

| line | len | member | says |
|---:|---:|---|---|
| 453 | 35 | `static void classBills(City c)` |  |

### 3 (lines 489-526)

| line | len | member | says |
|---:|---:|---|---|
| 491 | 35 | `static void shortCity()` |  |

### 4 (lines 527-592)

| line | len | member | says |
|---:|---:|---|---|
| 529 | 63 | `static void fuelShock()` |  |

### 5 (lines 593-620)

| line | len | member | says |
|---:|---:|---|---|
| 595 | 25 | `static void margin(City c)` |  |

### 6 (lines 621-654)

| line | len | member | says |
|---:|---:|---|---|
| 623 | 31 | `static void fuelMoney(City c)` |  |

### 7 (lines 655-716)

| line | len | member | says |
|---:|---:|---|---|
| 658 | 3 | `static double dispatchedSeason(City c, UtilitiesHandler.Book b)` | The season the book's dispatch ran at, read off the game's own calendar: SEASON at the dispatch's calendar month and the city's latitude; NaN for a book with no dispatch. |
| 663 | 9 | `static Step struckIn(City c, int monthOfYear)` | The first statement struck at a book whose dispatch ran in that calendar month and burned coal, played on through the town's own calendar if none has been yet; null if a year brings none. |
| 680 | 36 | `static void seasonBills(City c)` | PW1-R (REV-PW1's first follow-up): the dispatch serves draw x season x the month's hours, the fuel is bought on those kWh, and the bill was draw x ratio x the price a kW of the year's mean: July billed 16% under, Marc... |

### 8 (lines 717-747)

| line | len | member | says |
|---:|---:|---|---|
| 719 | 28 | `static void auditCloses(City c)` |  |

