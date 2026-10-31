# RankingsCheck.java - 1,436 lines · 44 methods · 9 constants · harnesses

`ham/citybuildersim/RankingsCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The Rankings board held to what it promises (batch LB2; the project's spec-leaderboards.md 3, 4, 6 and 9):
> Rankings' pivot and display, its two boards, its ranking, its sentence and lock, and CityMeasures' reading
> of the city's history.
> 
> WHY. A board that ranks a city's GDP beside Japan's has three ways to be quietly wrong that no screen would
> show: a Money toggle that reorders the table (a city passing a nation by changing the unit it is shown in),
> a city's own figure that does not come back to the figure the rest of the game shows, and a nation of the
> city's own world whose row is not the figure Nations gives for that month. Each is an identity, and the rest
> is the rules a rank is read by (lower is better ranks ascending, ties share, a place with no figure is no
> row, never 0) and the sentence that names what is on screen. A city measure whose unit is wrong by a factor
> of a thousand (the model counts money in thousands) would put the city a thousand times poorer than Japan, so
> its figures are held to the history's own reader times the named factor.
> 
> THE DATA. world-2025.json in the resources, and the nations of the world file: the fixture's twelve
> (WorldData.FIXTURE) unless -Dcbs.world names the full world-1.bin, which gives all 196 nations; a line says
> which. The live city is a scratch city (GameFiles.scratch) played to month 40 by the playtest's own probe.
> 
> What this has to prove:
>   1. THE PIVOT'S IDENTITIES: the city's own figure in its own money is X under this month's rate and PPP, and X
>      at founding's prices under Constant (a wage, which has no constant series, X); the order of a board is the
>      same in the city's money, game US$ and real US$, on both boards.
>   2. AT FOUNDING REAL US$ EQUALS GAME US$ (D1): a fresh city's game dollar price level is 1, and every row of
>      every money measure is the same in the two; a played city's is not.
>   3. CONSTANT HOLDS A STEADY REAL FIGURE WHILE ITS RATE MOVES: the same figures at another rate and price
>      level are the same US$ of 2015 under Constant, and not under this month's rate.
>   4. YOUR WORLD'S ROWS EQUAL NATIONS' FIGURES FOR THE MONTH: people, output, inflation, growth, area and
>      density of every nation, on both paths at two years; Historical's equal the world file to 2025; a region is
>      its nation's times its share; the board ranks exactly the ten measures D15 names.
>   5. LOWER IS BETTER RANKS ASCENDING: a DOWN measure's first row has the least value, an UP's and a NONE's the most.
>   6. TIES SHARE A RANK: equal values share one, the next rank skips, the tie is by name.
>   7. A PLACE WITH NO FIGURE IS OFF, NEVER 0: and a measure nothing real publishes has the city alone.
>   8. THE REAL ROWS' PIVOT: current US$ times CPI-U 2015 over the figure's year, the .k and .ppp figures, and
>      household spending and wages by gdp.k and gdp.ppp over gdp.
>   9. AROUND YOU AND YOUR SIZE: the top three and the city with six either side, the places within ten times its people.
>  10. THE CITY'S MEASURES FROM HISTORY: a year of the history's own reader times the named factor, the rules
>      of a short city, a folded history and the life table.
>  11. THE LOCK AND THE SENTENCE: Prices locked where it must be, and the opening sentence names what is on screen.
> 
> LB3, YOUR SAVED CITIES. The fixture is the live city saved to a scratch folder (GameFiles.scratch, never the real save folder) at its
> month, played on a year and saved, then reformed by a factor of 100 and saved again: three slots, one city at three points.
>  12. READING THE SLOTS: slot 0 is written and left out, an empty slot is off, a save from an older build, a newer one, a damaged
>      one and one with no history are off and counted, every file's bytes and modified time are as they were after a read, and a
>      second refresh with nothing changed reuses every slot while one re-saved slot alone is read again.
>  13. PRICES MOVES ONLY SAVED ROWS, NEVER UNDER REAL US$: per year against static changes no other row; under real US$ and
>      under Constant, or for a measure that is not money, it changes nothing; in game US$ a saved city's figure is its own
>      year's (X over its rate) per year and its real figure at this city's prices static; static boards keep one order in the
>      three moneys; the sentence and the drawer name and open a saved city.
>  14. A REFORM LEAVES EVERY RANK: the same city saved before and after a reform by 100 ranks alike, on both boards, in every money,
>      Prices and Convert, for every measure, its values equal to 1e-9; side by side on one board the two share a rank (Rankings.TIE).
>  15. AT YOUR AGE EQUALS THE HISTORY FILE'S MONTH: a saved city older than the age has the figures, rate, parity and price level
>      of its history file at that month; one younger is no row and is listed as stopped, with its last month.
>  16. THE RANK LINE: one point for each December the city has a figure for, the last one the board's own rank for the city at that
>      December, and neither Your world nor the saved cities play any part.
>  17. THE SLIM HISTORY EQUALS THE FULL ONE: a slot keeps only the series the measures read, and the measures from it are the
>      full history's, figure for figure, at every month; the game dollar's price level is recorded each month and the reform
>      does not touch it.
>  18. CITYMEASURES.AT: at the last month it is of(); at the first month it was asked it is what of() said then, however far the
>      city has played since; a month the history never reached or no longer holds as a month has no figures; Extras.UNKNOWN
>      leaves out exactly what needs the land and the bands.
> 
> ... (3 more lines in the source)

**Uses:** [Rankings](Rankings.md) (226), [CityMeasures](CityMeasures.md) (105), [Money](Money.md) (66), [WorldMeasures](WorldMeasures.md) (60), [Nations](Nations.md) (37), [HistorySave](HistorySave.md) (22), [SavedCities](SavedCities.md) (16), [GameFiles](GameFiles.md) (13), [AgeBand](AgeBand.md) (11), [WorldData](WorldData.md) (9), [Game](Game.md) (9), [CityCalendar](CityCalendar.md) (8), [WorldMeasuresCheck](WorldMeasuresCheck.md) (6), [LongPlaytest](LongPlaytest.md) (4), [WorldDataCheck](WorldDataCheck.md) (3), [GameVersion](GameVersion.md) (3), [YearBook](YearBook.md) (2), [LandManager](LandManager.md) (2), [ForeignAccounts](ForeignAccounts.md) (1)

## Sections

| line | section |
|---:|---|
| 254 | 1. THE PIVOT'S IDENTITIES |
| 306 | 2. AT FOUNDING REAL US$ EQUALS GAME US$ (D1) |
| 349 | 3. CONSTANT HOLDS A STEADY REAL FIGURE WHILE ITS RATE MOVES |
| 373 | 4. YOUR WORLD'S ROWS EQUAL NATIONS' FIGURES FOR THE MONTH |
| 482 | 5. LOWER IS BETTER RANKS ASCENDING |
| 514 | 6. TIES SHARE A RANK |
| 553 | 7. A PLACE WITH NO FIGURE IS OFF, NEVER 0 |
| 626 | 8. THE REAL ROWS' PIVOT |
| 692 | 9. AROUND YOU AND YOUR SIZE |
| 741 | 10. THE CITY'S MEASURES FROM HISTORY |
| 834 | 11. THE LOCK AND THE SENTENCE |
| 876 | LB3: YOUR SAVED CITIES. THE FIXTURE AND ITS HELPERS |
| 989 | 12. READING THE SLOTS |
| 1055 | 13. PRICES MOVES ONLY SAVED ROWS, NEVER UNDER REAL US$ |
| 1181 | 14. A REFORM LEAVES EVERY RANK |
| 1229 | 15. AT YOUR AGE EQUALS THE HISTORY FILE'S MONTH |
| 1278 | 16. THE RANK LINE |
| 1337 | 17. THE SLIM HISTORY EQUALS THE FULL ONE |
| 1395 | 18. CITYMEASURES.AT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 99 | `RankingsCheck.out` | `System.out` | Where the harness prints: the console. |
| 102 | `RankingsCheck.SEED` | `7_001` | The seed of the nations' paths. |
| 105 | `RankingsCheck.LIVE_MONTH` | `40` | The month the live city is played to: over the two years real growth needs. |
| 108 | `RankingsCheck.TOLERANCE` | `1e-9` | How near two figures that should be one are: relative to the larger, with 1e-9 a floor. |
| 111 | `RankingsCheck.YEAR_HISTORY` | `2012, YEAR_RULE = 2050` | The years Your world is read at: one inside the world file's history, one the game's rule makes. |
| 114 | `RankingsCheck.MONEY` | `List.of("gdp", "gdp_pp", "hh_spend_pp", "avg_wage", "min_wage")` | The money measures of the file, and the ones Your world ranks of them. |
| 117 | `RankingsCheck.ABSENT` | `Set.of("participation", "tertiary", "yield_10y", "debt_foreign", "interest_re...` | The measures the brief leaves absent from the city, each for a reason CityMeasures' javadoc gives. |
| 121 | `RankingsCheck.CORE` | `List.of("gdp", "gdp_pp", "growth_real", "hh_spend_pp", "investment", "gov_spe...` | What a city of a few years has: every one of these is in its figures. |
| 897 | `RankingsCheck.LB3_FACTOR` | `100` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 97 | `static int fails` |  |
| 135 | `int n, bad` |  |
| 136 | `String first` |  |
| 887 | `Game game` |  |
| 888 | `GameFiles files` |  |
| 889 | `boolean reformed` |  |
| 890 | `int youngLast, oldLast` |  |
| 891 | `Rankings.City before, after` |  |
| 892 | `CityMeasures.Extras youngExtras` |  |
| 893 | `Map<String, CityMeasures.Figure> youngReplayed` |  |
| 894 | `SavedCities snapshot` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 95 | 1342 | **type** `public class RankingsCheck` | The Rankings board held to what it promises (batch LB2; the project's spec-leaderboards.md 3, 4, 6 and 9): Rankings' pivot and display, its two boards, its ranking, its sentence and lock, and CityMeasures' reading of ... |
| 128 | 4 | `static void check(String label, boolean ok)` | Prints a label and OK or FAIL; the exit code is the count of FAILs. |
| 134 | 12 | **type** `static final class Tally` | A check over many things, that says how many there were and the first that was wrong. |
| 138 | 7 | `void see(boolean ok, Supplier<String> what)` _(in RankingsCheck.Tally)_ |  |
| 147 | 6 | `static void tally(String label, Tally t)` |  |
| 154 | 3 | `static boolean near(double a, double b)` |  |
| 158 | 3 | `static Settings set(Board board, Money money, Convert convert)` |  |
| 163 | 3 | `static Row cityRow(Table t)` | The city's row of a table, null when it is off. |
| 168 | 3 | `static Rankings.City at(Rankings.City c, int year)` | The city at a year of its calendar, everything else as it is. |
| 173 | 3 | `static Rankings.City moved(Rankings.City c, double fx, double parity, double usPrices)` | The city with another rate, parity and price level, and its figures as they were. |
| 178 | 3 | `static Rankings.City with(Rankings.City c, Map<String, CityMeasures.Figure> figures)` | The city with other figures. |
| 182 | 3 | `static List<String> ids(Table t)` |  |
| 186 | 5 | `static Map<String, Row> byId(Table t)` |  |
| 192 | 3 | `static PrintStream nothing()` |  |
| 196 | 57 | `public static void main(String[] args) throws Exception` |  |

### 1. THE PIVOT'S IDENTITIES (lines 254-305)

| line | len | member | says |
|---:|---:|---|---|
| 258 | 47 | `static void thePivot(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 2. AT FOUNDING REAL US$ EQUALS GAME US$ (D1) (lines 306-348)

| line | len | member | says |
|---:|---:|---|---|
| 310 | 29 | `static void atFounding(WorldMeasures wm, Nations nations, Rankings.City live, Rankings.City founded)` |  |
| 341 | 7 | `static void sameRows(Tally t, Table a, Table b, String what)` | Whether two tables of one measure have the same rows and each row's shown figure the same, to the digit. |

### 3. CONSTANT HOLDS A STEADY REAL FIGURE WHILE ITS RATE MOVES (lines 349-372)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 19 | `static void theConstant(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 4. YOUR WORLD'S ROWS EQUAL NATIONS' FIGURES FOR THE MONTH (lines 373-481)

| line | len | member | says |
|---:|---:|---|---|
| 377 | 104 | `static void yourWorld(WorldMeasures wm, WorldData data, Nations historical, Nations random, Rankings.City live)` |  |

### 5. LOWER IS BETTER RANKS ASCENDING (lines 482-513)

| line | len | member | says |
|---:|---:|---|---|
| 486 | 27 | `static void theOrder(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 6. TIES SHARE A RANK (lines 514-552)

| line | len | member | says |
|---:|---:|---|---|
| 518 | 34 | `static void theTies(WorldMeasures wm, Nations nations, Rankings.City live) throws IOException` |  |

### 7. A PLACE WITH NO FIGURE IS OFF, NEVER 0 (lines 553-625)

| line | len | member | says |
|---:|---:|---|---|
| 557 | 68 | `static void theMissing(WorldMeasures wm, Nations nations, Rankings.City live) throws IOException` |  |

### 8. THE REAL ROWS' PIVOT (lines 626-691)

| line | len | member | says |
|---:|---:|---|---|
| 630 | 61 | `static void theRealPivot(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 9. AROUND YOU AND YOUR SIZE (lines 692-740)

| line | len | member | says |
|---:|---:|---|---|
| 696 | 44 | `static void theWindows(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 10. THE CITY'S MEASURES FROM HISTORY (lines 741-833)

| line | len | member | says |
|---:|---:|---|---|
| 745 | 77 | `static void theCity(WorldMeasures wm, Game g, Rankings.City live)` |  |
| 824 | 9 | `static double constantSeries(String name)` | A history by hand: every series the same every month, but the price index (1), the people and the bands' deaths (so that a life table has them). |

### 11. THE LOCK AND THE SENTENCE (lines 834-875)

| line | len | member | says |
|---:|---:|---|---|
| 838 | 37 | `static void theLockAndSentence(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### LB3: YOUR SAVED CITIES. THE FIXTURE AND ITS HELPERS (lines 876-988)

| line | len | member | says |
|---:|---:|---|---|
| 886 | 10 | **type** `static final class Saves` | The live city saved three times to its own scratch folder: slot 1 at its month, slot 2 a year on, slot 3 the same month as slot 2 after a reform by 100. |
| 899 | 18 | `static Saves makeSaves(Game g, Rankings.City live)` |  |
| 918 | 9 | `static void quietly(Runnable work)` |  |
| 928 | 3 | `static int lastMonth(HistorySave h)` |  |
| 933 | 5 | `static CityMeasures.Extras liveExtras(Game g)` | What Rankings.City.of(Game) gives CityMeasures for the live city: its land, its bands and the wage floor in today's money. |
| 940 | 5 | `static List<Rankings.Saved> only(List<Rankings.Saved> all, int...slots)` | The saved cities of these slots, in the order the slots are named. |
| 947 | 7 | `static HistorySave fullHistory(GameFiles files, int slot) throws IOException` | A slot's history file read whole, as Game.loadHistory reads it. |
| 956 | 3 | `static boolean same(Map<String, CityMeasures.Figure> a, Map<String, CityMeasures.Figure> b)` | Whether two sets of figures are the same, measure for measure in the same order, each figure to the digit. |
| 961 | 3 | `static boolean relative(double a, double b)` | Two figures equal to a relative 1e-9, with no floor: a small figure is held as tightly as a large one. |
| 966 | 11 | `static void copySlot(GameFiles from, int fromSlot, GameFiles to, int toSlot, int format, boolean history) throws IOException` | Copies slot `from` of one folder to slot `to` of another: the save with its format set to `format` unless that is 0, and the history file if asked. |
| 979 | 9 | `static Map<Path, String> fingerprint(GameFiles files) throws IOException` | Every file under a folder: its size, modified time and a hash of its bytes. |

### 12. READING THE SLOTS (lines 989-1054)

| line | len | member | says |
|---:|---:|---|---|
| 993 | 55 | `static void theSlots(WorldMeasures wm, Nations nations, Saves f) throws IOException` |  |
| 1049 | 5 | `static boolean delete(GameFiles files, int slot) throws IOException` |  |

### 13. PRICES MOVES ONLY SAVED ROWS, NEVER UNDER REAL US$ (lines 1055-1180)

| line | len | member | says |
|---:|---:|---|---|
| 1059 | 121 | `static void thePrices(WorldMeasures wm, Nations nations, Saves f)` |  |

### 14. A REFORM LEAVES EVERY RANK (lines 1181-1228)

| line | len | member | says |
|---:|---:|---|---|
| 1185 | 43 | `static void theReform(WorldMeasures wm, Nations nations, Saves f)` |  |

### 15. AT YOUR AGE EQUALS THE HISTORY FILE'S MONTH (lines 1229-1277)

| line | len | member | says |
|---:|---:|---|---|
| 1233 | 44 | `static void theAge(WorldMeasures wm, Nations nations, Saves f) throws IOException` |  |

### 16. THE RANK LINE (lines 1278-1336)

| line | len | member | says |
|---:|---:|---|---|
| 1282 | 54 | `static void theRankLine(WorldMeasures wm, Nations nations, Saves f)` |  |

### 17. THE SLIM HISTORY EQUALS THE FULL ONE (lines 1337-1394)

| line | len | member | says |
|---:|---:|---|---|
| 1341 | 53 | `static void theSlimHistory(Saves f) throws IOException` |  |

### 18. CITYMEASURES.AT (lines 1395-1436)

| line | len | member | says |
|---:|---:|---|---|
| 1399 | 37 | `static void theMonth(Rankings.City live, Saves f)` |  |

