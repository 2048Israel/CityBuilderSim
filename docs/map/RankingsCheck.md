# RankingsCheck.java - 833 lines · 26 methods · 8 constants · harnesses

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
> Every fixture causes its condition: the founding city is a founded one, a tie is a real file with three
> figures set equal, a missing figure a real file with one removed.

**Uses:** [Rankings](Rankings.md) (133), [WorldMeasures](WorldMeasures.md) (50), [Money](Money.md) (47), [CityMeasures](CityMeasures.md) (38), [Nations](Nations.md) (32), [WorldData](WorldData.md) (9), [AgeBand](AgeBand.md) (8), [HistorySave](HistorySave.md) (7), [WorldMeasuresCheck](WorldMeasuresCheck.md) (6), [Game](Game.md) (4), [LongPlaytest](LongPlaytest.md) (3), [WorldDataCheck](WorldDataCheck.md) (3), [GameFiles](GameFiles.md) (2), [CityCalendar](CityCalendar.md) (2), [YearBook](YearBook.md) (2), [ForeignAccounts](ForeignAccounts.md) (1)

## Sections

| line | section |
|---:|---|
| 214 | 1. THE PIVOT'S IDENTITIES |
| 266 | 2. AT FOUNDING REAL US$ EQUALS GAME US$ (D1) |
| 309 | 3. CONSTANT HOLDS A STEADY REAL FIGURE WHILE ITS RATE MOVES |
| 333 | 4. YOUR WORLD'S ROWS EQUAL NATIONS' FIGURES FOR THE MONTH |
| 442 | 5. LOWER IS BETTER RANKS ASCENDING |
| 474 | 6. TIES SHARE A RANK |
| 513 | 7. A PLACE WITH NO FIGURE IS OFF, NEVER 0 |
| 586 | 8. THE REAL ROWS' PIVOT |
| 652 | 9. AROUND YOU AND YOUR SIZE |
| 701 | 10. THE CITY'S MEASURES FROM HISTORY |
| 792 | 11. THE LOCK AND THE SENTENCE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 69 | `RankingsCheck.out` | `System.out` | Where the harness prints: the console. |
| 72 | `RankingsCheck.SEED` | `7_001` | The seed of the nations' paths. |
| 75 | `RankingsCheck.LIVE_MONTH` | `40` | The month the live city is played to: over the two years real growth needs. |
| 78 | `RankingsCheck.TOLERANCE` | `1e-9` | How near two figures that should be one are: relative to the larger, with 1e-9 a floor. |
| 81 | `RankingsCheck.YEAR_HISTORY` | `2012, YEAR_RULE = 2050` | The years Your world is read at: one inside the world file's history, one the game's rule makes. |
| 84 | `RankingsCheck.MONEY` | `List.of("gdp", "gdp_pp", "hh_spend_pp", "avg_wage", "min_wage")` | The money measures of the file, and the ones Your world ranks of them. |
| 87 | `RankingsCheck.ABSENT` | `Set.of("participation", "tertiary", "yield_10y", "debt_foreign", "interest_re...` | The measures the brief leaves absent from the city, each for a reason CityMeasures' javadoc gives. |
| 91 | `RankingsCheck.CORE` | `List.of("gdp", "gdp_pp", "growth_real", "hh_spend_pp", "investment", "gov_spe...` | What a city of a few years has: every one of these is in its figures. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `static int fails` |  |
| 105 | `int n, bad` |  |
| 106 | `String first` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 769 | **type** `public class RankingsCheck` | The Rankings board held to what it promises (batch LB2; the project's spec-leaderboards.md 3, 4, 6 and 9): Rankings' pivot and display, its two boards, its ranking, its sentence and lock, and CityMeasures' reading of ... |
| 98 | 4 | `static void check(String label, boolean ok)` | Prints a label and OK or FAIL; the exit code is the count of FAILs. |
| 104 | 12 | **type** `static final class Tally` | A check over many things, that says how many there were and the first that was wrong. |
| 108 | 7 | `void see(boolean ok, Supplier<String> what)` _(in RankingsCheck.Tally)_ |  |
| 117 | 6 | `static void tally(String label, Tally t)` |  |
| 124 | 3 | `static boolean near(double a, double b)` |  |
| 128 | 3 | `static Settings set(Board board, Money money, Convert convert)` |  |
| 133 | 3 | `static Row cityRow(Table t)` | The city's row of a table, null when it is off. |
| 138 | 3 | `static Rankings.City at(Rankings.City c, int year)` | The city at a year of its calendar, everything else as it is. |
| 143 | 3 | `static Rankings.City moved(Rankings.City c, double fx, double parity, double usPrices)` | The city with another rate, parity and price level, and its figures as they were. |
| 148 | 3 | `static Rankings.City with(Rankings.City c, Map<String, CityMeasures.Figure> figures)` | The city with other figures. |
| 152 | 3 | `static List<String> ids(Table t)` |  |
| 156 | 5 | `static Map<String, Row> byId(Table t)` |  |
| 162 | 3 | `static PrintStream nothing()` |  |
| 166 | 47 | `public static void main(String[] args) throws Exception` |  |

### 1. THE PIVOT'S IDENTITIES (lines 214-265)

| line | len | member | says |
|---:|---:|---|---|
| 218 | 47 | `static void thePivot(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 2. AT FOUNDING REAL US$ EQUALS GAME US$ (D1) (lines 266-308)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 29 | `static void atFounding(WorldMeasures wm, Nations nations, Rankings.City live, Rankings.City founded)` |  |
| 301 | 7 | `static void sameRows(Tally t, Table a, Table b, String what)` | Whether two tables of one measure have the same rows and each row's shown figure the same, to the digit. |

### 3. CONSTANT HOLDS A STEADY REAL FIGURE WHILE ITS RATE MOVES (lines 309-332)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 19 | `static void theConstant(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 4. YOUR WORLD'S ROWS EQUAL NATIONS' FIGURES FOR THE MONTH (lines 333-441)

| line | len | member | says |
|---:|---:|---|---|
| 337 | 104 | `static void yourWorld(WorldMeasures wm, WorldData data, Nations historical, Nations random, Rankings.City live)` |  |

### 5. LOWER IS BETTER RANKS ASCENDING (lines 442-473)

| line | len | member | says |
|---:|---:|---|---|
| 446 | 27 | `static void theOrder(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 6. TIES SHARE A RANK (lines 474-512)

| line | len | member | says |
|---:|---:|---|---|
| 478 | 34 | `static void theTies(WorldMeasures wm, Nations nations, Rankings.City live) throws IOException` |  |

### 7. A PLACE WITH NO FIGURE IS OFF, NEVER 0 (lines 513-585)

| line | len | member | says |
|---:|---:|---|---|
| 517 | 68 | `static void theMissing(WorldMeasures wm, Nations nations, Rankings.City live) throws IOException` |  |

### 8. THE REAL ROWS' PIVOT (lines 586-651)

| line | len | member | says |
|---:|---:|---|---|
| 590 | 61 | `static void theRealPivot(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 9. AROUND YOU AND YOUR SIZE (lines 652-700)

| line | len | member | says |
|---:|---:|---|---|
| 656 | 44 | `static void theWindows(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

### 10. THE CITY'S MEASURES FROM HISTORY (lines 701-791)

| line | len | member | says |
|---:|---:|---|---|
| 705 | 75 | `static void theCity(WorldMeasures wm, Game g, Rankings.City live)` |  |
| 782 | 9 | `static double constantSeries(String name)` | A history by hand: every series the same every month, but the price index (1), the people and the bands' deaths (so that a life table has them). |

### 11. THE LOCK AND THE SENTENCE (lines 792-833)

| line | len | member | says |
|---:|---:|---|---|
| 796 | 37 | `static void theLockAndSentence(WorldMeasures wm, Nations nations, Rankings.City live)` |  |

