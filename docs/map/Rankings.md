# Rankings.java - 729 lines · 38 methods · 11 constants · model

`ham/citybuildersim/Rankings.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The Rankings board as the screen draws it: a measure's table of ranked places, the active city among
> the real world's 196 nations and 62 city regions or among the nations of its own world, in the money
> and at the prices the player picked, with the sentence that says what is on screen. A pure model in
> the *View manner: no JavaFX, nothing in the month reads it.
> 
> WHY. A table that ranks a city's GDP beside Japan's is only as honest as the money it is in, and the
> game has three: the city's own, the game's US dollar (which inflates with the world's US prices) and
> the real US dollar of 2015. A screen that converted each row as it drew it would let the Money toggle
> reorder the board (a city could pass a nation by changing the unit it is shown in) and let two
> conversions of one figure disagree. So every money figure is turned ONCE into US$ of 2015 (the pivot),
> ranked there, and only then shown in the money picked (the display): Money can never reorder a board,
> and the city's own figure in its own money comes back to the figure the rest of the game shows. A
> founding game dollar counts as one 2015 dollar (D1), so at founding the game's US dollar and the real
> one are equal.
> 
> Builds spec-leaderboards 3 (the pivot, the display, the identities, the lock, the sentence), 4 (the two
> boards: Real world 2025 from WorldMeasures, Your world from Nations at the city's month, the city
> regions by share), 6 (the city's side, CityMeasures), 7 (the marks), 9 (Around you, Your size, the
> drawer) and 10 (D1, D9, D10, D13, D14, D15). The saved cities (LB3, spec-leaderboards 5) are Kind.SAVED rows,
> each its own world pivoted by its own rate, parity and founding rate, and by its own price level or the active
> city's (Prices, D2): the toggle's reason to exist.

**Uses:** [WorldMeasures](WorldMeasures.md) (53), [Nations](Nations.md) (21), [CityMeasures](CityMeasures.md) (18), [World](World.md) (7), [CityCalendar](CityCalendar.md) (5), [Money](Money.md) (4), [AgeBand](AgeBand.md) (3), [ForeignAccounts](ForeignAccounts.md) (2), [HistorySave](HistorySave.md) (2), [Game](Game.md) (1), [LandManager](LandManager.md) (1)

**Used by (3):** [RankingsCheck](RankingsCheck.md), [RankingsScreen](RankingsScreen.md), [SavedCities](SavedCities.md)

## Sections

| line | section |
|---:|---|
| 128 | · THE CONSTANTS |
| 174 | · THE DISPLAY (spec-leaderboards 3) |
| 200 | · THE LOCK AND THE SENTENCE (spec-leaderboards 3) |
| 284 | · A FIGURE, BEFORE IT IS A ROW |
| 295 | · THE ACTIVE CITY |
| 320 | · A SAVED CITY (spec-leaderboards 3 and 5; LB3) |
| 384 | · THE REAL WORLD, 2025 (spec-leaderboards 3 and LB1-notes 5) |
| 443 | · YOUR WORLD (spec-leaderboards 3 and 4) |
| 564 | · THE TABLE (spec-leaderboards 4 and 9) |
| 683 | · THE DRAWER (spec-leaderboards 7: opening a row) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 40 | `Rankings.Board.REAL_2025` |  |
| 40 | `Rankings.Board.YOUR_WORLD` |  |
| 43 | `Rankings.Money.CITY` |  |
| 43 | `Rankings.Money.GAME_USD` |  |
| 43 | `Rankings.Money.REAL_USD` |  |
| 46 | `Rankings.Prices.PER_YEAR` |  |
| 46 | `Rankings.Prices.STATIC` |  |
| 49 | `Rankings.Convert.MARKET` |  |
| 49 | `Rankings.Convert.CONSTANT` |  |
| 49 | `Rankings.Convert.PPP` |  |
| 52 | `Rankings.Kind.NATION` |  |
| 52 | `Rankings.Kind.REGION` |  |
| 52 | `Rankings.Kind.CITY` |  |
| 52 | `Rankings.Kind.SAVED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `Rankings.Settings.OPENING` | `new Settings(Board.REAL_2025, Money.REAL_USD, Prices.PER_YEAR, Convert.MARKET)` | What the tab opens on (D9, spec-leaderboards 9): Real world 2025, real US$, per year, this month's rate. |
| 133 | `Rankings.BASE_YEAR` | `2015` | The year the real dollar is struck at: US$ of 2015 (D29: "later the start date will move back anyways"). |
| 136 | `Rankings.REAL_YEAR` | `2025` | The real world's year: the board Real world 2025. |
| 139 | `Rankings.PLACE_CITY` | `"city"` | The id the drawer takes for the active city. |
| 142 | `Rankings.SOURCE_CITY` | `"city", SOURCE_WORLD = "world-1", SOURCE_SAVE = "save"` | A row's source: the active city's own figure, a nation of the game's world file (a source id of world-2025.json's meta), and a saved city's figure. |
| 145 | `Rankings.SAVED_PREFIX` | `"saved:"` | What a saved city's row id starts with, then its slot: the id the drawer is asked for. |
| 148 | `Rankings.RULE_REGION` | `"region-output"` | The rule a city region's Your-world figure is an estimate by: its share of its nation's path (a rule id of the file's meta). |
| 151 | `Rankings.YOUR_SIZE_FACTOR` | `10` | The places Your size keeps: those within this factor of the city's people, either way. |
| 154 | `Rankings.AROUND_TOP` | `3, AROUND_EITHER_SIDE = 6, AROUND_OFF = 10` | Around you: the rows from the top, and either side of the city; the rows from the top when the city is off the board. |
| 157 | `Rankings.TIE` | `1e-9` | Two figures within this fraction of each other are one figure and share a rank: a city saved before and after a reform by 100 differs in its last digits only (LB3). |
| 160 | `Rankings.CONSTANT_SERIES` | `Set.of("gdp", "gdp_pp", "hh_spend_pp")` | The money measures with a series at constant prices (gdp, gdp per person, household spending per person); the rest show this month's rate under Constant. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 449 | `final WorldMeasures wm` |  |
| 450 | `final Nations nations` |  |
| 451 | `final int year, us` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 35 | 695 | **type** `public final class Rankings` | The Rankings board as the screen draws it: a measure's table of ranked places, the active city among the real world's 196 nations and 62 city regions or among the nations of its own world, in the money and at the pric... |
| 37 | 1 | `private Rankings()` |  |
| 40 | 1 | **type** `public enum Board` | The two boards: the real world's latest year (spec-leaderboards 4), and the nations of the city's own world at its month. |
| 43 | 1 | **type** `public enum Money` | The money a figure is shown in: the city's own, the game's US dollars, or real US dollars at 2015 prices. |
| 46 | 1 | **type** `public enum Prices` | Whether a saved city's figure is in its own year's dollars (PER_YEAR) or the active city's year (STATIC). |
| 49 | 1 | **type** `public enum Convert` | How a foreign figure becomes this city's: at this month's rate, at the constant 2015 rate, or at purchasing power parity (D14). |
| 52 | 1 | **type** `public enum Kind` | What a row is. |
| 55 | 4 | **type** `public record Settings(Board board, Money money, Prices prices, Convert convert)` | What the player picked on the four controls. |
| 61 | 29 | **type** `public record City(String name, String currency, int month, int year, double fx, double parity, double usPr...` | The active city at its month: what the pivot and its row need. |
| 66 | 3 | `{ ... }` _(in Rankings.City)_ |  |
| 79 | 10 | `public static City of(Game g)` _(in Rankings.City)_ | The live city, off public getters only: its name, its money (Currency.plural()), its month and year, ForeignAccounts' rate and parity, WorldEconomy's price level, the founding rate in today's money (Denomination.money... |
| 98 | 2 | **type** `public record Row(int rank, String id, Kind kind, String name, String nation, double value, double shown, i...` | One ranked place. |
| 106 | 2 | **type** `public record Table(WorldMeasures.Measure measure, Settings settings, List<Row> rows, int you, boolean rank...` | A measure's board, ranked. |
| 114 | 6 | **type** `public record Saved(int slot, City city)` | A saved city on the board (LB3): the slot it was read from and the city as that save has it, a City in the units of its last save (its own fx, parity, P and founding rate, all rescaled together by a reform, so the piv... |
| 116 | 3 | `public String id()` _(in Rankings.Saved)_ | The id of this city's row, and what the drawer is asked for. |
| 122 | 1 | **type** `public record RankPoint(int month, int rank, int of)` | One value of the rank over the years: the active city's rank on one measure at a December (a game month), among `of` places. |
| 125 | 2 | **type** `public record Line(WorldMeasures.Measure measure, double real2025, int realMark, String realSource, double ...` | One measure for one place, in the drawer: its figure on each board in the display money, with its mark and source on the real board, NaN where there is none. |

### THE CONSTANTS (lines 128-173)

| line | len | member | says |
|---:|---:|---|---|
| 163 | 3 | `public static boolean hasConstantSeries(WorldMeasures.Measure m)` | Whether a measure has a series at constant prices: the others (wages) show this month's rate under Constant, and the sentence says so. |
| 168 | 5 | `static Settings effective(Settings s, WorldMeasures.Measure m)` | The settings a measure is really converted at: Constant becomes this month's rate for a money measure with no constant series. |

### THE DISPLAY (spec-leaderboards 3) (lines 174-199)

| line | len | member | says |
|---:|---:|---|---|
| 184 | 11 | `public static double display(double usd15, Settings s, City c)` | Real US$ of 2015 into the money picked: REAL_USD as is; GAME_USD times the game dollar's price level (times 1 under Constant, which is at founding's prices); CITY times that level and the rate (this month's rate, or p... |
| 196 | 3 | `private static double shown(double value, Settings eff, City c, WorldMeasures.Measure m)` |  |

### THE LOCK AND THE SENTENCE (spec-leaderboards 3) (lines 200-283)

| line | len | member | says |
|---:|---:|---|---|
| 208 | 3 | `public static boolean pricesLocked(Settings s, WorldMeasures.Measure m, boolean anySaved)` | Whether Prices is greyed: Money is real US$ (2015 prices by definition), Convert is Constant, the measure is not money, or no saved city is on the board (the caller passes !saved.isEmpty()). |
| 213 | 7 | `public static String pricesLockedWhy(Settings s, WorldMeasures.Measure m, boolean anySaved)` | The sentence for the greyed control's tooltip, saying which reason locks it; empty when it is not locked. |
| 226 | 30 | `public static String sentence(City c, Settings s, WorldMeasures.Measure m, Nations.Path path)` | The line under the controls that names exactly what is on screen (spec-leaderboards 3: "the most important piece of the UI"): the money, the way it was converted, and the board with the city's month. |
| 262 | 10 | `public static String sentence(City c, Settings s, WorldMeasures.Measure m, Nations.Path path, List<Saved> saved, boolean atAge)` | sentence() with the saved cities named when there are any (LB3). |
| 274 | 5 | `public static String ageWords(int months)` | A number of months as a person says an age: "4 years 2 months", "1 year", "7 months". |
| 280 | 3 | `private static String capitalised(String s)` |  |

### A FIGURE, BEFORE IT IS A ROW (lines 284-294)

| line | len | member | says |
|---:|---:|---|---|
| 289 | 1 | **type** `private record Cell(double value, int year, int mark, String source, String rule, double people)` | A place's figure for the measure once pivoted: its value (US$ of 2015 for money), year, mark, source, rule and people. |
| 291 | 3 | `private static boolean usable(double v)` |  |

### THE ACTIVE CITY (lines 295-319)

| line | len | member | says |
|---:|---:|---|---|
| 304 | 15 | `private static Cell cityCell(City c, WorldMeasures.Measure m, Settings eff)` | The city's figure for a measure, pivoted to US$ of 2015 (spec-leaderboards 3): a money figure X (the city's money, whole units) is X over the rate over the game dollar's price level at this month's rate, X over parity... |

### A SAVED CITY (spec-leaderboards 3 and 5; LB3) (lines 320-383)

| line | len | member | says |
|---:|---:|---|---|
| 332 | 17 | `private static Cell savedCell(Saved saved, City active, Settings s, WorldMeasures.Measure m, Settings eff)` | A saved city's figure for a measure, pivoted to US$ of 2015 as cityCell does but by ITS OWN rate, parity and founding rate, all in the units of its last save (a reform rescales them with its money, so a reform never m... |
| 356 | 5 | `public static City cityAt(String name, String currency, double foundingRate, HistorySave h, CityMeasures.Extras extras, int month)` | A city as a history stood at the end of game month `month` (LB3): its name, its money and its founding rate as given, its rate, parity and the game dollar's price level from the history's fxRate, fxParity and usPrices... |
| 368 | 15 | `public static List<RankPoint> rankOverYears(WorldMeasures wm, City city, HistorySave h, Settings s, String measure)` | The active city's rank on one measure at each December of its history (D12, LB3), against Real world 2025 only: its nations and regions, no saved rows and no Your world, a board that holds still, at the money and conv... |

### THE REAL WORLD, 2025 (spec-leaderboards 3 and LB1-notes 5) (lines 384-442)

| line | len | member | says |
|---:|---:|---|---|
| 389 | 3 | `private static double toBase(WorldMeasures wm, int year)` | A US dollar of the given year in 2015 dollars (CPI-U); NaN where the file has no CPI-U for the year. |
| 394 | 5 | `private static Cell lowest(WorldMeasures.Figure primary, double value, WorldMeasures.Figure...also)` | The lower of two marks, and the rule of the lower (the first on a tie). |
| 406 | 36 | `private static Cell realCell(WorldMeasures wm, WorldMeasures.Place p, WorldMeasures.Measure m, Settings eff)` | A real place's figure, pivoted: current US$ of its year times CPI-U 2015 over CPI-U of that year (this month's rate); under Constant the file's .k figure as is for gdp and gdp per person, and household spending's this... |

### YOUR WORLD (spec-leaderboards 3 and 4) (lines 443-563)

| line | len | member | says |
|---:|---:|---|---|
| 448 | 115 | **type** `private static final class World` | The nations of the city's own world at the city's year, and what the pivot needs of the US. |
| 453 | 6 | `World(WorldMeasures wm, Nations nations, City city)` _(in Rankings.World)_ |  |
| 461 | 7 | `double market(int i)` _(in Rankings.World)_ | A nation's output at that year's prices and rate, into US$ of 2015 by the world's own US prices (spec-leaderboards 3, this month's rate). |
| 470 | 9 | `double priceLevel(int i, WorldMeasures.Place file)` _(in Rankings.World)_ | The nation's price level against the US at the city's year: the file's 2025 figure (none: 1) moved by relative prices and the rate since (D13). |
| 481 | 7 | `double output(int i, WorldMeasures.Place file, Settings eff)` _(in Rankings.World)_ | A nation's output in the converted money (US$ of 2015): this month's rate, the output as it is under Constant, or PPP, the market figure over the price level. |
| 490 | 6 | `int outputMark(int i, WorldMeasures.Place file, Settings eff)` _(in Rankings.World)_ | The mark of that figure: the output's, and under PPP the lower of it and the price level's (a nation with none is an estimate). |
| 498 | 33 | `Cell nation(int i, WorldMeasures.Place file, WorldMeasures.Measure m, Settings eff)` _(in Rankings.World)_ | A nation of the game's world as a cell for a measure, or null where it has no figure at that year. |
| 538 | 20 | `Cell region(WorldMeasures.Place r, WorldMeasures.Measure m, Settings eff)` _(in Rankings.World)_ | A city region as a cell: its nation's path times its 2025 share of the nation's people and output, rates held, so every figure is an estimate (spec-leaderboards 4). |
| 559 | 3 | `private String rule(String id)` _(in Rankings.World)_ |  |

### THE TABLE (spec-leaderboards 4 and 9) (lines 564-682)

| line | len | member | says |
|---:|---:|---|---|
| 575 | 3 | `public static Table table(WorldMeasures wm, Nations nations, City city, Settings s, String measure)` | The ranked board of one measure. |
| 585 | 37 | `public static Table table(WorldMeasures wm, Nations nations, City city, List<Saved> saved, Settings s, String measure)` | table() with the saved cities beside the active one (LB3): each is a Kind.SAVED row ranked by the same function, pivoted by its own rate and price level (savedCell) and shown in the active city's money, on any board t... |
| 623 | 4 | `private static Row row(String id, Kind kind, String name, String nation, Cell c, WorldMeasures.Measure m, Settings eff, City city)` |  |
| 629 | 21 | `private static Table rank(WorldMeasures.Measure m, Settings s, List<Row> unranked, boolean ranked, boolean realFigures)` | Orders the rows (DOWN ascending, otherwise descending, ties by name), gives ties one rank and the next rank skips, and counts the marks. |
| 652 | 3 | `static boolean tied(double a, double b)` | Whether two figures, next to each other in rank order, are one figure: equal, or within TIE of each other. |
| 657 | 13 | `public static List<Row> around(Table t)` | The rows Around you shows (spec-leaderboards 1, 11): the top three, then the city and six either side, none twice, in rank order; the first ten when the city is off the board. |
| 672 | 10 | `public static List<Row> yourSize(Table t)` | Your size: the places with people within YOUR_SIZE_FACTOR times the city's, either way, and the city; empty when the city is off the board or its people are not known. |

### THE DRAWER (spec-leaderboards 7: opening a row) (lines 683-729)

| line | len | member | says |
|---:|---:|---|---|
| 692 | 3 | `public static List<Line> drawer(WorldMeasures wm, Nations nations, City city, Settings s, String placeId)` | Every measure for one place: placeId is a nation's or a region's id in the file, or PLACE_CITY for the active city. |
| 701 | 28 | `public static List<Line> drawer(WorldMeasures wm, Nations nations, City city, List<Saved> saved, Settings s, String placeId)` | drawer() that also opens a saved city (LB3): placeId is then its row's id, "saved:" + slot (Saved.id()), and its lines are its own figures pivoted by its own rate and price level (savedCell), on both boards as the act... |

