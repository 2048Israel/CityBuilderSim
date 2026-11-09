# RankingsCheck.java - 1,609 lines · 48 methods · 9 constants · harnesses

`ham/citybuildersim/RankingsCheck.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

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
>   2. AT FOUNDING A GAME US$ IS THE BRIDGE'S WORTH OF REAL US$ (D1, EP1): a fresh city's game dollar price level is 1, and every row of
>      every money measure in real US$ is RealDollar.BRIDGE times the same row in game US$ (28.4 from 1900: a founding dollar is a 1900 dollar);
>      a played city's is more than that.
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
> ... (11 more lines in the source)

**Uses:** [Rankings](Rankings.md) (241), [CityMeasures](CityMeasures.md) (108), [Money](Money.md) (66), [WorldMeasures](WorldMeasures.md) (62), [SavedCities](SavedCities.md) (60), [Nations](Nations.md) (37), [HistorySave](HistorySave.md) (35), [GameFiles](GameFiles.md) (17), [AgeBand](AgeBand.md) (11), [Game](Game.md) (10), [WorldData](WorldData.md) (9), [RealDollar](RealDollar.md) (9), [CityCalendar](CityCalendar.md) (8), [WorldMeasuresCheck](WorldMeasuresCheck.md) (6), [LongPlaytest](LongPlaytest.md) (4), [WorldDataCheck](WorldDataCheck.md) (3), [GameVersion](GameVersion.md) (3), [YearBook](YearBook.md) (2), [LandManager](LandManager.md) (2), [ForeignAccounts](ForeignAccounts.md) (1)

## Sections

| line | section |
|---:|---|
| 270 | 1. THE PIVOT'S IDENTITIES |
| 322 | 2. AT FOUNDING A GAME US$ IS THE BRIDGE'S WORTH OF REAL US$ (D1, EP1) |
| 366 | 3. CONSTANT HOLDS A STEADY REAL FIGURE WHILE ITS RATE MOVES |
| 390 | 4. YOUR WORLD'S ROWS EQUAL NATIONS' FIGURES FOR THE MONTH |
| 499 | 5. LOWER IS BETTER RANKS ASCENDING |
| 531 | 6. TIES SHARE A RANK |
| 570 | 7. A PLACE WITH NO FIGURE IS OFF, NEVER 0 |
| 643 | 8. THE REAL ROWS' PIVOT |
| 709 | 9. AROUND YOU AND YOUR SIZE |
| 758 | 10. THE CITY'S MEASURES FROM HISTORY |
| 851 | 11. THE LOCK AND THE SENTENCE |
| 893 | LB3: YOUR SAVED CITIES. THE FIXTURE AND ITS HELPERS |
| 1006 | 12. READING THE SLOTS |
| 1072 | 13. PRICES MOVES ONLY SAVED ROWS, NEVER UNDER REAL US$ |
| 1198 | 14. A REFORM LEAVES EVERY RANK |
| 1246 | 15. AT YOUR AGE EQUALS THE HISTORY FILE'S MONTH |
| 1295 | 16. THE RANK LINE |
| 1377 | 17. THE SLIM HISTORY EQUALS THE FULL ONE |
| 1440 | 18. CITYMEASURES.AT |
| 1482 | 19. THE CITY IN PLAY, AS SAVED (LB4) |
| 1539 | 20. THE PACKED READ OF A SAVED HISTORY (LB4) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 112 | `RankingsCheck.out` | `System.out` | Where the harness prints: the console. |
| 115 | `RankingsCheck.SEED` | `7_001` | The seed of the nations' paths. |
| 118 | `RankingsCheck.LIVE_MONTH` | `40` | The month the live city is played to: over the two years real growth needs. |
| 121 | `RankingsCheck.TOLERANCE` | `1e-9` | How near two figures that should be one are: relative to the larger, with 1e-9 a floor. |
| 124 | `RankingsCheck.YEAR_HISTORY` | `2012, YEAR_RULE = 2050` | The years Your world is read at: one inside the world file's history, one the game's rule makes. |
| 127 | `RankingsCheck.MONEY` | `List.of("gdp", "gdp_pp", "hh_spend_pp", "avg_wage", "min_wage")` | The money measures of the file, and the ones Your world ranks of them. |
| 130 | `RankingsCheck.ABSENT` | `Set.of("participation", "tertiary", "yield_10y", "debt_foreign", "interest_re...` | The measures the brief leaves absent from the city, each for a reason CityMeasures' javadoc gives. |
| 134 | `RankingsCheck.CORE` | `List.of("gdp", "gdp_pp", "growth_real", "hh_spend_pp", "investment", "gov_spe...` | What a city of a few years has: every one of these is in its figures. |
| 914 | `RankingsCheck.LB3_FACTOR` | `100` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 110 | `static int fails` |  |
| 148 | `int n, bad` |  |
| 149 | `String first` |  |
| 904 | `Game game` |  |
| 905 | `GameFiles files` |  |
| 906 | `boolean reformed` |  |
| 907 | `int youngLast, oldLast` |  |
| 908 | `Rankings.City before, after` |  |
| 909 | `CityMeasures.Extras youngExtras` |  |
| 910 | `Map<String, CityMeasures.Figure> youngReplayed` |  |
| 911 | `SavedCities snapshot` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 108 | 1502 | **type** `public class RankingsCheck` | The Rankings board held to what it promises (batch LB2; the project's spec-leaderboards.md 3, 4, 6 and 9): Rankings' pivot and display, its two boards, its ranking, its sentence and lock, and CityMeasures' reading of ... |
| 141 | 4 | `static void check(String label, boolean ok)` | Prints a label and OK or FAIL; the exit code is the count of FAILs. |
| 147 | 12 | **type** `static final class Tally` | A check over many things, that says how many there were and the first that was wrong. |
| 151 | 7 | `void see(boolean ok, Supplier<String> what)` _(in RankingsCheck.Tally)_ |  |
| 160 | 6 | `static void tally(String label, Tally t)` |  |
| 167 | 3 | `static boolean near(double a, double b)` |  |
| 171 | 3 | `static Settings set(Board board, Money money, Convert convert)` |  |
| 176 | 3 | `static Row cityRow(Table t)` | The city's row of a table, null when it is off. |
| 181 | 3 | `static Rankings.City at(Rankings.City c, int year)` | The city at a year of its calendar, everything else as it is. |
| 186 | 3 | `static Rankings.City moved(Rankings.City c, double fx, double parity, double usPrices)` | The city with another rate, parity and price level, and its figures as they were. |
| 191 | 3 | `static Rankings.City with(Rankings.City c, Map<String, CityMeasures.Figure> figures)` | The city with other figures. |
| 195 | 3 | `static List<String> ids(Table t)` |  |
| 199 | 5 | `static Map<String, Row> byId(Table t)` |  |
| 205 | 3 | `static PrintStream nothing()` |  |
| 209 | 60 | `public static void main(String[] args) throws Exception` |  |

### 1. THE PIVOT'S IDENTITIES (lines 270-321)

| line | len | member | says |
|---:|---:|---|---|
| 274 | 47 | `static void thePivot(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 2. AT FOUNDING A GAME US$ IS THE BRIDGE'S WORTH OF REAL US$ (D1, EP1) (lines 322-365)

| line | len | member | says |
|---:|---:|---|---|
| 326 | 29 | `static void atFounding(WorldMeasures wm, Nations nations, Rankings.City live, Rankings.City founded)` |  |
| 357 | 8 | `static void sameRows(Tally t, Table a, Table b, String what)` | Whether two tables of one measure have the same rows and each row's shown figure the same, to the digit. |

### 3. CONSTANT HOLDS A STEADY REAL FIGURE WHILE ITS RATE MOVES (lines 366-389)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 19 | `static void theConstant(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 4. YOUR WORLD'S ROWS EQUAL NATIONS' FIGURES FOR THE MONTH (lines 390-498)

| line | len | member | says |
|---:|---:|---|---|
| 394 | 104 | `static void yourWorld(WorldMeasures wm, WorldData data, Nations historical, Nations random, Rankings.City live)` |  |

### 5. LOWER IS BETTER RANKS ASCENDING (lines 499-530)

| line | len | member | says |
|---:|---:|---|---|
| 503 | 27 | `static void theOrder(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 6. TIES SHARE A RANK (lines 531-569)

| line | len | member | says |
|---:|---:|---|---|
| 535 | 34 | `static void theTies(WorldMeasures wm, Nations nations, Rankings.City live) throws IOException` |  |

### 7. A PLACE WITH NO FIGURE IS OFF, NEVER 0 (lines 570-642)

| line | len | member | says |
|---:|---:|---|---|
| 574 | 68 | `static void theMissing(WorldMeasures wm, Nations nations, Rankings.City live) throws IOException` |  |

### 8. THE REAL ROWS' PIVOT (lines 643-708)

| line | len | member | says |
|---:|---:|---|---|
| 647 | 61 | `static void theRealPivot(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 9. AROUND YOU AND YOUR SIZE (lines 709-757)

| line | len | member | says |
|---:|---:|---|---|
| 713 | 44 | `static void theWindows(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 10. THE CITY'S MEASURES FROM HISTORY (lines 758-850)

| line | len | member | says |
|---:|---:|---|---|
| 762 | 77 | `static void theCity(WorldMeasures wm, Game g, Rankings.City live)` |  |
| 841 | 9 | `static double constantSeries(String name)` | A history by hand: every series the same every month, but the price index (1), the people and the bands' deaths (so that a life table has them). |

### 11. THE LOCK AND THE SENTENCE (lines 851-892)

| line | len | member | says |
|---:|---:|---|---|
| 855 | 37 | `static void theLockAndSentence(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### LB3: YOUR SAVED CITIES. THE FIXTURE AND ITS HELPERS (lines 893-1005)

| line | len | member | says |
|---:|---:|---|---|
| 903 | 10 | **type** `static final class Saves` | The live city saved three times to its own scratch folder: slot 1 at its month, slot 2 a year on, slot 3 the same month as slot 2 after a reform by 100. |
| 916 | 18 | `static Saves makeSaves(Game g, Rankings.City live)` |  |
| 935 | 9 | `static void quietly(Runnable work)` |  |
| 945 | 3 | `static int lastMonth(HistorySave h)` |  |
| 950 | 5 | `static CityMeasures.Extras liveExtras(Game g)` | What Rankings.City.of(Game) gives CityMeasures for the live city: its land, its bands and the wage floor in today's money. |
| 957 | 5 | `static List<Rankings.Saved> only(List<Rankings.Saved> all, int...slots)` | The saved cities of these slots, in the order the slots are named. |
| 964 | 7 | `static HistorySave fullHistory(GameFiles files, int slot) throws IOException` | A slot's history file read whole, as Game.loadHistory reads it. |
| 973 | 3 | `static boolean same(Map<String, CityMeasures.Figure> a, Map<String, CityMeasures.Figure> b)` | Whether two sets of figures are the same, measure for measure in the same order, each figure to the digit. |
| 978 | 3 | `static boolean relative(double a, double b)` | Two figures equal to a relative 1e-9, with no floor: a small figure is held as tightly as a large one. |
| 983 | 11 | `static void copySlot(GameFiles from, int fromSlot, GameFiles to, int toSlot, int format, boolean history) throws IOException` | Copies slot `from` of one folder to slot `to` of another: the save with its format set to `format` unless that is 0, and the history file if asked. |
| 996 | 9 | `static Map<Path, String> fingerprint(GameFiles files) throws IOException` | Every file under a folder: its size, modified time and a hash of its bytes. |

### 12. READING THE SLOTS (lines 1006-1071)

| line | len | member | says |
|---:|---:|---|---|
| 1010 | 55 | `static void theSlots(WorldMeasures wm, Nations nations, Saves f) throws IOException` |  |
| 1066 | 5 | `static boolean delete(GameFiles files, int slot) throws IOException` |  |

### 13. PRICES MOVES ONLY SAVED ROWS, NEVER UNDER REAL US$ (lines 1072-1197)

| line | len | member | says |
|---:|---:|---|---|
| 1076 | 121 | `static void thePrices(WorldMeasures wm, Nations nations, Saves f)` |  |

### 14. A REFORM LEAVES EVERY RANK (lines 1198-1245)

| line | len | member | says |
|---:|---:|---|---|
| 1202 | 43 | `static void theReform(WorldMeasures wm, Nations nations, Saves f)` |  |

### 15. AT YOUR AGE EQUALS THE HISTORY FILE'S MONTH (lines 1246-1294)

| line | len | member | says |
|---:|---:|---|---|
| 1250 | 44 | `static void theAge(WorldMeasures wm, Nations nations, Saves f) throws IOException` |  |

### 16. THE RANK LINE (lines 1295-1376)

| line | len | member | says |
|---:|---:|---|---|
| 1299 | 77 | `static void theRankLine(WorldMeasures wm, Nations nations, Saves f)` |  |

### 17. THE SLIM HISTORY EQUALS THE FULL ONE (lines 1377-1439)

| line | len | member | says |
|---:|---:|---|---|
| 1381 | 58 | `static void theSlimHistory(Saves f) throws IOException` |  |

### 18. CITYMEASURES.AT (lines 1440-1481)

| line | len | member | says |
|---:|---:|---|---|
| 1444 | 37 | `static void theMonth(Rankings.City live, Saves f)` |  |

### 19. THE CITY IN PLAY, AS SAVED (LB4) (lines 1482-1538)

| line | len | member | says |
|---:|---:|---|---|
| 1487 | 6 | `static void editedCopy(GameFiles from, int fromSlot, GameFiles to, int toSlot, Consumer<JsonObject> edit) throws IOException` | Copies a slot to another folder and edits its parsed save before it is written back: one key changed or removed, to cause one condition. |
| 1494 | 44 | `static void theCityInPlay(Saves f) throws IOException` |  |

### 20. THE PACKED READ OF A SAVED HISTORY (LB4) (lines 1539-1609)

| line | len | member | says |
|---:|---:|---|---|
| 1544 | 15 | `static String seriesOf(Collection<String> names, String type, String...not) throws NoSuchFieldException` | The first series name, in name order, of those given that HistorySave keeps in a field of that name typed List of `type` (Double or Long), not one of `not`. |
| 1561 | 48 | `static void thePackedRead() throws NoSuchFieldException` |  |

