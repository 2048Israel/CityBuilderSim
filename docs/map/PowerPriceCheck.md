# PowerPriceCheck.java - 745 lines · 31 methods · 9 constants · harnesses

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
| 101 | THE FIXTURES |
| 261 | · the old save, for SaveFileCheck (REV-PW1c) |
| 314 | · the struck book through a save, for SaveFileCheck (PW1-R) |
| 359 | MAIN |
| 381 | · the fixture, before any section |
| 396 | · 1 |
| 449 | · 2 |
| 487 | · 3 |
| 525 | · 4 |
| 591 | · 5 |
| 619 | · 6 |
| 653 | · 7 |
| 715 | · 8 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `PowerPriceCheck.quiet` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 54 | `PowerPriceCheck.HOMES` | `400` | How many homes a town stands on: enough that their draw outruns the founding grid. |
| 57 | `PowerPriceCheck.HOME_KW` | `50` | Each home's draw in a town, kW: MoneyCheck's and WoodProductsCheck's lever, so 400 homes outrun the founding grid and the coal plant is the margin. |
| 60 | `PowerPriceCheck.SHORT_ASK` | `2` | How many times the coal plant's production the short town's homes ask of the grid: past the plant and the founding grid in every season. |
| 63 | `PowerPriceCheck.OLD_SAVE_DESAL` | `2` | How many desalination plants the old save's town stands on the founding grid alone: their draw, 10,880 kW each, is past its 10,000 kW in every month. |
| 66 | `PowerPriceCheck.SHOCK` | `3` | How many times the coal's price the shocked city's dispatch is told. |
| 69 | `PowerPriceCheck.WARM` | `8` | The months a town is played before a section reads it: the plants are staffed and the mine is dug and stocked. |
| 143 | `PowerPriceCheck.audits` | `new ArrayList<>()` |  |
| 144 | `PowerPriceCheck.auditCities` | `new ArrayList<>()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `static int fails` |  |
| 108 | `int month` | The month before it ran. |
| 110 | `UtilitiesHandler.Book book, struckAfter` | The book its statements were struck at (struck in the month before), and the book it struck. |
| 112 | `final Map<String, double[]> held` | Per sector key before the month: draw, ratio, high-voltage share, commerce price, industry price. |
| 114 | `final Map<String, Double> bills` | Per sector key after the month: the electricity its statement struck. |
| 116 | `double coalHeld` | The coal Coal Mining held before the month. |
| 118 | `double fuelLocal, fuelImported, upkeep, revenue, payroll, bought, income, waterIncome, net, treasury, charg...` | After: the fuel paid, the margin's terms, the treasury's line. |
| 120 | `double localUnits, importedUnits, localCost, importedCost` | After: the coal drawn for the city this month: units from Coal Mining, units from the world, what each cost. |
| 122 | `double exchange, minePaidAtHome, latestEnergyPrice, latestCoalMarginal, latestCoalKwh` | After: the exchange rate, Coal Mining's statement's coal sold at home, the coal row of the dispatch the month ended on. |
| 123 | `MoneyAudit.Result audit` |  |
| 127 | `final String label` |  |
| 128 | `final Game g` |  |
| 129 | `final UtilitiesHandler u` |  |
| 130 | `final boolean mine` |  |
| 131 | `final List<Step> steps` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 698 | **type** `public class PowerPriceCheck` | The month's power at the market's price, through the game's own month (PW1, 2026-11-11; Jerus's Q1: "Market price"): the struck book's prices, what each class is billed at them, the coal the plants burn bought from th... |
| 71 | 5 | `static void check(String label, boolean ok, String detail)` |  |
| 77 | 1 | `static void check(String label, boolean ok)` |  |
| 80 | 3 | `static boolean near(double a, double b, double tol)` | Whether two figures agree to tol of the larger. |
| 84 | 1 | `static String f(double v)` |  |
| 86 | 4 | `static void section(String title)` |  |
| 91 | 3 | `static void sectionEnd()` |  |
| 95 | 5 | `static void quietly(Runnable work)` |  |

### THE FIXTURES (lines 101-260)

| line | len | member | says |
|---:|---:|---|---|
| 106 | 19 | **type** `static final class Step` | One month run: what the strike at its top read before it ran, and what stood after it. |
| 126 | 16 | **type** `static final class City` |  |
| 133 | 6 | `City(String label, Game g, boolean mine)` _(in PowerPriceCheck.City)_ |  |
| 140 | 1 | `Step last()` _(in PowerPriceCheck.City)_ |  |
| 152 | 33 | `static City town(String label, boolean mine, double ask)` | A town: founded, the Wealthy preset's cash and ground, homes, shops, a coal plant, water and roads (MaterialsCheck.town()'s list), standing at once (buildStack(..., noConstruction): LongPlaytest.build() orders real co... |
| 187 | 10 | `static double[] cityDraw(Game g, Good good)` | What the city drew of a good from the market this month, by the draw log the clearing kept with the month's fills: {units local, units imported, cost local, cost imported}. |
| 198 | 4 | `static PowerDispatch.Row coalRow(UtilitiesHandler u)` |  |
| 204 | 43 | `static Step run(City c)` | Runs one month of a city and keeps what the harness reads: the strike's book and the sectors' draws before, the statements and the fuel's money after. |
| 248 | 4 | `static City play(City c, int months)` |  |
| 254 | 6 | `static double unitOf(Game g)` | The unit the city's money constants are struck at: Game.restrikeMoneyConstants()'s own, the denomination's unit over the struck level. |

### the old save, for SaveFileCheck (REV-PW1c) (lines 261-313)

| line | len | member | says |
|---:|---:|---|---|
| 271 | 42 | `static boolean saveChecks() throws Exception` | A save from before PW1 (format 58, no powerBooks) of a city short of power loads with the ratio its load dispatched in its book, as the load handed the sectors before PW1, not the founding book's 1, and its first mont... |

### the struck book through a save, for SaveFileCheck (PW1-R) (lines 314-358)

| line | len | member | says |
|---:|---:|---|---|
| 323 | 35 | `static boolean bookChecks() throws Exception` | A saved city that holds a struck book reads it back field for field (REV-PW1's third follow-up: the only round trip was in memory, PowerDispatchCheck 10, so the Gson read-back of Book was unchecked), through the save'... |

### MAIN (lines 359-380)

| line | len | member | says |
|---:|---:|---|---|
| 363 | 17 | `public static void main(String[] args) throws Exception` |  |

### the fixture, before any section (lines 381-395)

| line | len | member | says |
|---:|---:|---|---|
| 383 | 12 | `static void fixture(City c)` |  |

### 1 (lines 396-448)

| line | len | member | says |
|---:|---:|---|---|
| 399 | 9 | `static double energyPriceOf(UtilitiesHandler.Book b)` | The energy price recomputed from the book's own half-blocks: the prices weighted by the energy each served, over the energy served, over (1 - losses). |
| 409 | 23 | `static void pricesOfBook(String who, Game g, UtilitiesHandler.Book b)` |  |
| 433 | 15 | `static void struckPrice(City c)` |  |

### 2 (lines 449-486)

| line | len | member | says |
|---:|---:|---|---|
| 451 | 35 | `static void classBills(City c)` |  |

### 3 (lines 487-524)

| line | len | member | says |
|---:|---:|---|---|
| 489 | 35 | `static void shortCity()` |  |

### 4 (lines 525-590)

| line | len | member | says |
|---:|---:|---|---|
| 527 | 63 | `static void fuelShock()` |  |

### 5 (lines 591-618)

| line | len | member | says |
|---:|---:|---|---|
| 593 | 25 | `static void margin(City c)` |  |

### 6 (lines 619-652)

| line | len | member | says |
|---:|---:|---|---|
| 621 | 31 | `static void fuelMoney(City c)` |  |

### 7 (lines 653-714)

| line | len | member | says |
|---:|---:|---|---|
| 656 | 3 | `static double dispatchedSeason(City c, UtilitiesHandler.Book b)` | The season the book's dispatch ran at, read off the game's own calendar: SEASON at the dispatch's calendar month and the city's latitude; NaN for a book with no dispatch. |
| 661 | 9 | `static Step struckIn(City c, int monthOfYear)` | The first statement struck at a book whose dispatch ran in that calendar month and burned coal, played on through the town's own calendar if none has been yet; null if a year brings none. |
| 678 | 36 | `static void seasonBills(City c)` | PW1-R (REV-PW1's first follow-up): the dispatch serves draw x season x the month's hours, the fuel is bought on those kWh, and the bill was draw x ratio x the price a kW of the year's mean: July billed 16% under, Marc... |

### 8 (lines 715-745)

| line | len | member | says |
|---:|---:|---|---|
| 717 | 28 | `static void auditCloses(City c)` |  |

