# Rankings.java - 581 lines · 29 methods · 9 constants · model

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
> drawer) and 10 (D1, D9, D10, D13, D14, D15). The saved cities (LB3) are Kind.SAVED and the Prices
> toggle's reason to exist: none in LB2, so it locks.

**Uses:** [WorldMeasures](WorldMeasures.md) (48), [Nations](Nations.md) (18), [CityMeasures](CityMeasures.md) (9), [World](World.md) (7), [Money](Money.md) (4), [AgeBand](AgeBand.md) (3), [ForeignAccounts](ForeignAccounts.md) (2), [CityCalendar](CityCalendar.md) (2), [Game](Game.md) (1)

**Used by (2):** [RankingsCheck](RankingsCheck.md), [RankingsScreen](RankingsScreen.md)

## Sections

| line | section |
|---:|---|
| 111 | · THE CONSTANTS |
| 151 | · THE DISPLAY (spec-leaderboards 3) |
| 177 | · THE LOCK AND THE SENTENCE (spec-leaderboards 3) |
| 235 | · A FIGURE, BEFORE IT IS A ROW |
| 246 | · THE ACTIVE CITY |
| 271 | · THE REAL WORLD, 2025 (spec-leaderboards 3 and LB1-notes 5) |
| 330 | · YOUR WORLD (spec-leaderboards 3 and 4) |
| 451 | · THE TABLE (spec-leaderboards 4 and 9) |
| 550 | · THE DRAWER (spec-leaderboards 7: opening a row) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 39 | `Rankings.Board.REAL_2025` |  |
| 39 | `Rankings.Board.YOUR_WORLD` |  |
| 42 | `Rankings.Money.CITY` |  |
| 42 | `Rankings.Money.GAME_USD` |  |
| 42 | `Rankings.Money.REAL_USD` |  |
| 45 | `Rankings.Prices.PER_YEAR` |  |
| 45 | `Rankings.Prices.STATIC` |  |
| 48 | `Rankings.Convert.MARKET` |  |
| 48 | `Rankings.Convert.CONSTANT` |  |
| 48 | `Rankings.Convert.PPP` |  |
| 51 | `Rankings.Kind.NATION` |  |
| 51 | `Rankings.Kind.REGION` |  |
| 51 | `Rankings.Kind.CITY` |  |
| 51 | `Rankings.Kind.SAVED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `Rankings.Settings.OPENING` | `new Settings(Board.REAL_2025, Money.REAL_USD, Prices.PER_YEAR, Convert.MARKET)` | What the tab opens on (D9, spec-leaderboards 9): Real world 2025, real US$, per year, this month's rate. |
| 116 | `Rankings.BASE_YEAR` | `2015` | The year the real dollar is struck at: US$ of 2015 (D29: "later the start date will move back anyways"). |
| 119 | `Rankings.REAL_YEAR` | `2025` | The real world's year: the board Real world 2025. |
| 122 | `Rankings.PLACE_CITY` | `"city"` | The id the drawer takes for the active city. |
| 125 | `Rankings.SOURCE_CITY` | `"city", SOURCE_WORLD = "world-1"` | A row's source: the active city's own figure, and a nation of the game's world file (a source id of world-2025.json's meta). |
| 128 | `Rankings.RULE_REGION` | `"region-output"` | The rule a city region's Your-world figure is an estimate by: its share of its nation's path (a rule id of the file's meta). |
| 131 | `Rankings.YOUR_SIZE_FACTOR` | `10` | The places Your size keeps: those within this factor of the city's people, either way. |
| 134 | `Rankings.AROUND_TOP` | `3, AROUND_EITHER_SIDE = 6, AROUND_OFF = 10` | Around you: the rows from the top, and either side of the city; the rows from the top when the city is off the board. |
| 137 | `Rankings.CONSTANT_SERIES` | `Set.of("gdp", "gdp_pp", "hh_spend_pp")` | The money measures with a series at constant prices (gdp, gdp per person, household spending per person); the rest show this month's rate under Constant. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 336 | `final WorldMeasures wm` |  |
| 337 | `final Nations nations` |  |
| 338 | `final int year, us` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 548 | **type** `public final class Rankings` | The Rankings board as the screen draws it: a measure's table of ranked places, the active city among the real world's 196 nations and 62 city regions or among the nations of its own world, in the money and at the pric... |
| 36 | 1 | `private Rankings()` |  |
| 39 | 1 | **type** `public enum Board` | The two boards: the real world's latest year (spec-leaderboards 4), and the nations of the city's own world at its month. |
| 42 | 1 | **type** `public enum Money` | The money a figure is shown in: the city's own, the game's US dollars, or real US dollars at 2015 prices. |
| 45 | 1 | **type** `public enum Prices` | Whether a saved city's figure is in its own year's dollars (PER_YEAR) or this city's year (STATIC). |
| 48 | 1 | **type** `public enum Convert` | How a foreign figure becomes this city's: at this month's rate, at the constant 2015 rate, or at purchasing power parity (D14). |
| 51 | 1 | **type** `public enum Kind` | What a row is. |
| 54 | 4 | **type** `public record Settings(Board board, Money money, Prices prices, Convert convert)` | What the player picked on the four controls. |
| 60 | 28 | **type** `public record City(String name, String currency, int month, int year, double fx, double parity, double usPr...` | The active city at its month: what the pivot and its row need. |
| 65 | 3 | `{ ... }` _(in Rankings.City)_ |  |
| 77 | 10 | `public static City of(Game g)` _(in Rankings.City)_ | The live city, off public getters only: its name, its money (Currency.plural()), its month and year, ForeignAccounts' rate and parity, WorldEconomy's price level, the founding rate in today's money (Denomination.money... |
| 96 | 2 | **type** `public record Row(int rank, String id, Kind kind, String name, String nation, double value, double shown, i...` | One ranked place. |
| 104 | 2 | **type** `public record Table(WorldMeasures.Measure measure, Settings settings, List<Row> rows, int you, boolean rank...` | A measure's board, ranked. |
| 108 | 2 | **type** `public record Line(WorldMeasures.Measure measure, double real2025, int realMark, String realSource, double ...` | One measure for one place, in the drawer: its figure on each board in the display money, with its mark and source on the real board, NaN where there is none. |

### THE CONSTANTS (lines 111-150)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 3 | `public static boolean hasConstantSeries(WorldMeasures.Measure m)` | Whether a measure has a series at constant prices: the others (wages) show this month's rate under Constant, and the sentence says so. |
| 145 | 5 | `static Settings effective(Settings s, WorldMeasures.Measure m)` | The settings a measure is really converted at: Constant becomes this month's rate for a money measure with no constant series. |

### THE DISPLAY (spec-leaderboards 3) (lines 151-176)

| line | len | member | says |
|---:|---:|---|---|
| 161 | 11 | `public static double display(double usd15, Settings s, City c)` | Real US$ of 2015 into the money picked: REAL_USD as is; GAME_USD times the game dollar's price level (times 1 under Constant, which is at founding's prices); CITY times that level and the rate (this month's rate, or p... |
| 173 | 3 | `private static double shown(double value, Settings eff, City c, WorldMeasures.Measure m)` |  |

### THE LOCK AND THE SENTENCE (spec-leaderboards 3) (lines 177-234)

| line | len | member | says |
|---:|---:|---|---|
| 182 | 3 | `public static boolean pricesLocked(Settings s, WorldMeasures.Measure m, boolean anySaved)` | Whether Prices is greyed: Money is real US$ (2015 prices by definition), Convert is Constant, the measure is not money, or no saved city is on the board (always, until LB3). |
| 187 | 7 | `public static String pricesLockedWhy(Settings s, WorldMeasures.Measure m, boolean anySaved)` | The sentence for the greyed control's tooltip, saying which reason locks it; empty when it is not locked. |
| 200 | 30 | `public static String sentence(City c, Settings s, WorldMeasures.Measure m, Nations.Path path)` | The line under the controls that names exactly what is on screen (spec-leaderboards 3: "the most important piece of the UI"): the money, the way it was converted, and the board with the city's month. |
| 231 | 3 | `private static String capitalised(String s)` |  |

### A FIGURE, BEFORE IT IS A ROW (lines 235-245)

| line | len | member | says |
|---:|---:|---|---|
| 240 | 1 | **type** `private record Cell(double value, int year, int mark, String source, String rule, double people)` | A place's figure for the measure once pivoted: its value (US$ of 2015 for money), year, mark, source, rule and people. |
| 242 | 3 | `private static boolean usable(double v)` |  |

### THE ACTIVE CITY (lines 246-270)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 15 | `private static Cell cityCell(City c, WorldMeasures.Measure m, Settings eff)` | The city's figure for a measure, pivoted to US$ of 2015 (spec-leaderboards 3): a money figure X (the city's money, whole units) is X over the rate over the game dollar's price level at this month's rate, X over parity... |

### THE REAL WORLD, 2025 (spec-leaderboards 3 and LB1-notes 5) (lines 271-329)

| line | len | member | says |
|---:|---:|---|---|
| 276 | 3 | `private static double toBase(WorldMeasures wm, int year)` | A US dollar of the given year in 2015 dollars (CPI-U); NaN where the file has no CPI-U for the year. |
| 281 | 5 | `private static Cell lowest(WorldMeasures.Figure primary, double value, WorldMeasures.Figure...also)` | The lower of two marks, and the rule of the lower (the first on a tie). |
| 293 | 36 | `private static Cell realCell(WorldMeasures wm, WorldMeasures.Place p, WorldMeasures.Measure m, Settings eff)` | A real place's figure, pivoted: current US$ of its year times CPI-U 2015 over CPI-U of that year (this month's rate); under Constant the file's .k figure as is for gdp and gdp per person, and household spending's this... |

### YOUR WORLD (spec-leaderboards 3 and 4) (lines 330-450)

| line | len | member | says |
|---:|---:|---|---|
| 335 | 115 | **type** `private static final class World` | The nations of the city's own world at the city's year, and what the pivot needs of the US. |
| 340 | 6 | `World(WorldMeasures wm, Nations nations, City city)` _(in Rankings.World)_ |  |
| 348 | 7 | `double market(int i)` _(in Rankings.World)_ | A nation's output at that year's prices and rate, into US$ of 2015 by the world's own US prices (spec-leaderboards 3, this month's rate). |
| 357 | 9 | `double priceLevel(int i, WorldMeasures.Place file)` _(in Rankings.World)_ | The nation's price level against the US at the city's year: the file's 2025 figure (none: 1) moved by relative prices and the rate since (D13). |
| 368 | 7 | `double output(int i, WorldMeasures.Place file, Settings eff)` _(in Rankings.World)_ | A nation's output in the converted money (US$ of 2015): this month's rate, the output as it is under Constant, or PPP, the market figure over the price level. |
| 377 | 6 | `int outputMark(int i, WorldMeasures.Place file, Settings eff)` _(in Rankings.World)_ | The mark of that figure: the output's, and under PPP the lower of it and the price level's (a nation with none is an estimate). |
| 385 | 33 | `Cell nation(int i, WorldMeasures.Place file, WorldMeasures.Measure m, Settings eff)` _(in Rankings.World)_ | A nation of the game's world as a cell for a measure, or null where it has no figure at that year. |
| 425 | 20 | `Cell region(WorldMeasures.Place r, WorldMeasures.Measure m, Settings eff)` _(in Rankings.World)_ | A city region as a cell: its nation's path times its 2025 share of the nation's people and output, rates held, so every figure is an estimate (spec-leaderboards 4). |
| 446 | 3 | `private String rule(String id)` _(in Rankings.World)_ |  |

### THE TABLE (spec-leaderboards 4 and 9) (lines 451-549)

| line | len | member | says |
|---:|---:|---|---|
| 462 | 32 | `public static Table table(WorldMeasures wm, Nations nations, City city, Settings s, String measure)` | The ranked board of one measure. |
| 495 | 4 | `private static Row row(String id, Kind kind, String name, String nation, Cell c, WorldMeasures.Measure m, Settings eff, City city)` |  |
| 501 | 21 | `private static Table rank(WorldMeasures.Measure m, Settings s, List<Row> unranked, boolean ranked, boolean realFigures)` | Orders the rows (DOWN ascending, otherwise descending, ties by name), gives ties one rank and the next rank skips, and counts the marks. |
| 524 | 13 | `public static List<Row> around(Table t)` | The rows Around you shows (spec-leaderboards 1, 11): the top three, then the city and six either side, none twice, in rank order; the first ten when the city is off the board. |
| 539 | 10 | `public static List<Row> yourSize(Table t)` | Your size: the places with people within YOUR_SIZE_FACTOR times the city's, either way, and the city; empty when the city is off the board or its people are not known. |

### THE DRAWER (spec-leaderboards 7: opening a row) (lines 550-581)

| line | len | member | says |
|---:|---:|---|---|
| 559 | 22 | `public static List<Line> drawer(WorldMeasures wm, Nations nations, City city, Settings s, String placeId)` | Every measure for one place: placeId is a nation's or a region's id in the file, or PLACE_CITY for the active city. |

