# RankingsCheck.java - 1,691 lines · 50 methods · 9 constants · harnesses

`ham/citybuildersim/RankingsCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

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
> ... (17 more lines in the source)

**Uses:** [Rankings](Rankings.md) (244), [CityMeasures](CityMeasures.md) (108), [SavedCities](SavedCities.md) (79), [Money](Money.md) (66), [WorldMeasures](WorldMeasures.md) (62), [Nations](Nations.md) (37), [HistorySave](HistorySave.md) (35), [GameFiles](GameFiles.md) (23), [AgeBand](AgeBand.md) (11), [Game](Game.md) (10), [WorldData](WorldData.md) (9), [RealDollar](RealDollar.md) (9), [CityCalendar](CityCalendar.md) (8), [WorldMeasuresCheck](WorldMeasuresCheck.md) (6), [LongPlaytest](LongPlaytest.md) (4), [WorldDataCheck](WorldDataCheck.md) (3), [GameVersion](GameVersion.md) (3), [YearBook](YearBook.md) (2), [LandManager](LandManager.md) (2), [ForeignAccounts](ForeignAccounts.md) (1)

## Sections

| line | section |
|---:|---|
| 278 | 1. THE PIVOT'S IDENTITIES |
| 330 | 2. AT FOUNDING A GAME US$ IS THE BRIDGE'S WORTH OF REAL US$ (D1, EP1) |
| 374 | 3. CONSTANT HOLDS A STEADY REAL FIGURE WHILE ITS RATE MOVES |
| 398 | 4. YOUR WORLD'S ROWS EQUAL NATIONS' FIGURES FOR THE MONTH |
| 507 | 5. LOWER IS BETTER RANKS ASCENDING |
| 539 | 6. TIES SHARE A RANK |
| 578 | 7. A PLACE WITH NO FIGURE IS OFF, NEVER 0 |
| 651 | 8. THE REAL ROWS' PIVOT |
| 717 | 9. AROUND YOU AND YOUR SIZE |
| 766 | 10. THE CITY'S MEASURES FROM HISTORY |
| 859 | 11. THE LOCK AND THE SENTENCE |
| 901 | LB3: YOUR SAVED CITIES. THE FIXTURE AND ITS HELPERS |
| 1014 | 12. READING THE SLOTS |
| 1080 | 13. PRICES MOVES ONLY SAVED ROWS, NEVER UNDER REAL US$ |
| 1206 | 14. A REFORM LEAVES EVERY RANK |
| 1254 | 15. AT YOUR AGE EQUALS THE HISTORY FILE'S MONTH |
| 1303 | 16. THE RANK LINE |
| 1385 | 17. THE SLIM HISTORY EQUALS THE FULL ONE |
| 1448 | 18. CITYMEASURES.AT |
| 1490 | 19. THE CITY IN PLAY, AS SAVED (LB4) |
| 1552 | 20. THE PACKED READ OF A SAVED HISTORY (LB4) |
| 1623 | 21. THE STAMP FOLLOWS EACH FILE OF A SLOT (LB5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 118 | `RankingsCheck.out` | `System.out` | Where the harness prints: the console. |
| 121 | `RankingsCheck.SEED` | `7_001` | The seed of the nations' paths. |
| 124 | `RankingsCheck.LIVE_MONTH` | `40` | The month the live city is played to: over the two years real growth needs. |
| 127 | `RankingsCheck.TOLERANCE` | `1e-9` | How near two figures that should be one are: relative to the larger, with 1e-9 a floor. |
| 130 | `RankingsCheck.YEAR_HISTORY` | `2012, YEAR_RULE = 2050` | The years Your world is read at: one inside the world file's history, one the game's rule makes. |
| 133 | `RankingsCheck.MONEY` | `List.of("gdp", "gdp_pp", "hh_spend_pp", "avg_wage", "min_wage")` | The money measures of the file, and the ones Your world ranks of them. |
| 136 | `RankingsCheck.ABSENT` | `Set.of("participation", "tertiary", "yield_10y", "debt_foreign", "interest_re...` | The measures the brief leaves absent from the city, each for a reason CityMeasures' javadoc gives. |
| 140 | `RankingsCheck.CORE` | `List.of("gdp", "gdp_pp", "growth_real", "hh_spend_pp", "investment", "gov_spe...` | What a city of a few years has: every one of these is in its figures. |
| 922 | `RankingsCheck.LB3_FACTOR` | `100` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 116 | `static int fails` |  |
| 154 | `int n, bad` |  |
| 155 | `String first` |  |
| 912 | `Game game` |  |
| 913 | `GameFiles files` |  |
| 914 | `boolean reformed` |  |
| 915 | `int youngLast, oldLast` |  |
| 916 | `Rankings.City before, after` |  |
| 917 | `CityMeasures.Extras youngExtras` |  |
| 918 | `Map<String, CityMeasures.Figure> youngReplayed` |  |
| 919 | `SavedCities snapshot` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 114 | 1578 | **type** `public class RankingsCheck` | The Rankings board held to what it promises (batch LB2; the project's spec-leaderboards.md 3, 4, 6 and 9): Rankings' pivot and display, its two boards, its ranking, its sentence and lock, and CityMeasures' reading of ... |
| 147 | 4 | `static void check(String label, boolean ok)` | Prints a label and OK or FAIL; the exit code is the count of FAILs. |
| 153 | 12 | **type** `static final class Tally` | A check over many things, that says how many there were and the first that was wrong. |
| 157 | 7 | `void see(boolean ok, Supplier<String> what)` _(in RankingsCheck.Tally)_ |  |
| 166 | 6 | `static void tally(String label, Tally t)` |  |
| 173 | 3 | `static boolean near(double a, double b)` |  |
| 177 | 3 | `static Settings set(Board board, Money money, Convert convert)` |  |
| 182 | 3 | `static Row cityRow(Table t)` | The city's row of a table, null when it is off. |
| 187 | 3 | `static Rankings.City at(Rankings.City c, int year)` | The city at a year of its calendar, everything else as it is. |
| 192 | 3 | `static Rankings.City moved(Rankings.City c, double fx, double parity, double usPrices)` | The city with another rate, parity and price level, and its figures as they were. |
| 197 | 3 | `static Rankings.City with(Rankings.City c, Map<String, CityMeasures.Figure> figures)` | The city with other figures. |
| 201 | 3 | `static List<String> ids(Table t)` |  |
| 205 | 5 | `static Map<String, Row> byId(Table t)` |  |
| 211 | 3 | `static PrintStream nothing()` |  |
| 215 | 62 | `public static void main(String[] args) throws Exception` |  |

### 1. THE PIVOT'S IDENTITIES (lines 278-329)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 47 | `static void thePivot(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 2. AT FOUNDING A GAME US$ IS THE BRIDGE'S WORTH OF REAL US$ (D1, EP1) (lines 330-373)

| line | len | member | says |
|---:|---:|---|---|
| 334 | 29 | `static void atFounding(WorldMeasures wm, Nations nations, Rankings.City live, Rankings.City founded)` |  |
| 365 | 8 | `static void sameRows(Tally t, Table a, Table b, String what)` | Whether two tables of one measure have the same rows and each row's shown figure the same, to the digit. |

### 3. CONSTANT HOLDS A STEADY REAL FIGURE WHILE ITS RATE MOVES (lines 374-397)

| line | len | member | says |
|---:|---:|---|---|
| 378 | 19 | `static void theConstant(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 4. YOUR WORLD'S ROWS EQUAL NATIONS' FIGURES FOR THE MONTH (lines 398-506)

| line | len | member | says |
|---:|---:|---|---|
| 402 | 104 | `static void yourWorld(WorldMeasures wm, WorldData data, Nations historical, Nations random, Rankings.City live)` |  |

### 5. LOWER IS BETTER RANKS ASCENDING (lines 507-538)

| line | len | member | says |
|---:|---:|---|---|
| 511 | 27 | `static void theOrder(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 6. TIES SHARE A RANK (lines 539-577)

| line | len | member | says |
|---:|---:|---|---|
| 543 | 34 | `static void theTies(WorldMeasures wm, Nations nations, Rankings.City live) throws IOException` |  |

### 7. A PLACE WITH NO FIGURE IS OFF, NEVER 0 (lines 578-650)

| line | len | member | says |
|---:|---:|---|---|
| 582 | 68 | `static void theMissing(WorldMeasures wm, Nations nations, Rankings.City live) throws IOException` |  |

### 8. THE REAL ROWS' PIVOT (lines 651-716)

| line | len | member | says |
|---:|---:|---|---|
| 655 | 61 | `static void theRealPivot(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 9. AROUND YOU AND YOUR SIZE (lines 717-765)

| line | len | member | says |
|---:|---:|---|---|
| 721 | 44 | `static void theWindows(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 10. THE CITY'S MEASURES FROM HISTORY (lines 766-858)

| line | len | member | says |
|---:|---:|---|---|
| 770 | 77 | `static void theCity(WorldMeasures wm, Game g, Rankings.City live)` |  |
| 849 | 9 | `static double constantSeries(String name)` | A history by hand: every series the same every month, but the price index (1), the people and the bands' deaths (so that a life table has them). |

### 11. THE LOCK AND THE SENTENCE (lines 859-900)

| line | len | member | says |
|---:|---:|---|---|
| 863 | 37 | `static void theLockAndSentence(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### LB3: YOUR SAVED CITIES. THE FIXTURE AND ITS HELPERS (lines 901-1013)

| line | len | member | says |
|---:|---:|---|---|
| 911 | 10 | **type** `static final class Saves` | The live city saved three times to its own scratch folder: slot 1 at its month, slot 2 a year on, slot 3 the same month as slot 2 after a reform by 100. |
| 924 | 18 | `static Saves makeSaves(Game g, Rankings.City live)` |  |
| 943 | 9 | `static void quietly(Runnable work)` |  |
| 953 | 3 | `static int lastMonth(HistorySave h)` |  |
| 958 | 5 | `static CityMeasures.Extras liveExtras(Game g)` | What Rankings.City.of(Game) gives CityMeasures for the live city: its land, its bands and the wage floor in today's money. |
| 965 | 5 | `static List<Rankings.Saved> only(List<Rankings.Saved> all, int...slots)` | The saved cities of these slots, in the order the slots are named. |
| 972 | 7 | `static HistorySave fullHistory(GameFiles files, int slot) throws IOException` | A slot's history file read whole, as Game.loadHistory reads it. |
| 981 | 3 | `static boolean same(Map<String, CityMeasures.Figure> a, Map<String, CityMeasures.Figure> b)` | Whether two sets of figures are the same, measure for measure in the same order, each figure to the digit. |
| 986 | 3 | `static boolean relative(double a, double b)` | Two figures equal to a relative 1e-9, with no floor: a small figure is held as tightly as a large one. |
| 991 | 11 | `static void copySlot(GameFiles from, int fromSlot, GameFiles to, int toSlot, int format, boolean history) throws IOException` | Copies slot `from` of one folder to slot `to` of another: the save with its format set to `format` unless that is 0, and the history file if asked. |
| 1004 | 9 | `static Map<Path, String> fingerprint(GameFiles files) throws IOException` | Every file under a folder: its size, modified time and a hash of its bytes. |

### 12. READING THE SLOTS (lines 1014-1079)

| line | len | member | says |
|---:|---:|---|---|
| 1018 | 55 | `static void theSlots(WorldMeasures wm, Nations nations, Saves f) throws IOException` |  |
| 1074 | 5 | `static boolean delete(GameFiles files, int slot) throws IOException` |  |

### 13. PRICES MOVES ONLY SAVED ROWS, NEVER UNDER REAL US$ (lines 1080-1205)

| line | len | member | says |
|---:|---:|---|---|
| 1084 | 121 | `static void thePrices(WorldMeasures wm, Nations nations, Saves f)` |  |

### 14. A REFORM LEAVES EVERY RANK (lines 1206-1253)

| line | len | member | says |
|---:|---:|---|---|
| 1210 | 43 | `static void theReform(WorldMeasures wm, Nations nations, Saves f)` |  |

### 15. AT YOUR AGE EQUALS THE HISTORY FILE'S MONTH (lines 1254-1302)

| line | len | member | says |
|---:|---:|---|---|
| 1258 | 44 | `static void theAge(WorldMeasures wm, Nations nations, Saves f) throws IOException` |  |

### 16. THE RANK LINE (lines 1303-1384)

| line | len | member | says |
|---:|---:|---|---|
| 1307 | 77 | `static void theRankLine(WorldMeasures wm, Nations nations, Saves f)` |  |

### 17. THE SLIM HISTORY EQUALS THE FULL ONE (lines 1385-1447)

| line | len | member | says |
|---:|---:|---|---|
| 1389 | 58 | `static void theSlimHistory(Saves f) throws IOException` |  |

### 18. CITYMEASURES.AT (lines 1448-1489)

| line | len | member | says |
|---:|---:|---|---|
| 1452 | 37 | `static void theMonth(Rankings.City live, Saves f)` |  |

### 19. THE CITY IN PLAY, AS SAVED (LB4) (lines 1490-1551)

| line | len | member | says |
|---:|---:|---|---|
| 1495 | 6 | `static void editedCopy(GameFiles from, int fromSlot, GameFiles to, int toSlot, Consumer<JsonObject> edit) throws IOException` | Copies a slot to another folder and edits its parsed save before it is written back: one key changed or removed, to cause one condition. |
| 1502 | 49 | `static void theCityInPlay(Saves f) throws IOException` |  |

### 20. THE PACKED READ OF A SAVED HISTORY (LB4) (lines 1552-1622)

| line | len | member | says |
|---:|---:|---|---|
| 1557 | 15 | `static String seriesOf(Collection<String> names, String type, String...not) throws NoSuchFieldException` | The first series name, in name order, of those given that HistorySave keeps in a field of that name typed List of `type` (Double or Long), not one of `not`. |
| 1574 | 48 | `static void thePackedRead() throws NoSuchFieldException` |  |

### 21. THE STAMP FOLLOWS EACH FILE OF A SLOT (LB5) (lines 1623-1691)

| line | len | member | says |
|---:|---:|---|---|
| 1628 | 6 | `static void rewriteSave(Path save, Consumer<JsonObject> edit, FileTime time) throws IOException` | Rewrites a slot's save file alone, one key changed (the history beside it is not touched), and puts its modified time at `time`. |
| 1635 | 56 | `static void theStamp(Saves f) throws IOException` |  |

