# YearBookCheck.java - 1,114 lines · 44 methods · 5 constants · harnesses

`ham/citybuildersim/YearBookCheck.java` - generated 2026-10-05 by CodeMap; line numbers are as of that run.

> Proves the year book folds each series the way that series has to be folded,
> and that the file says so. Not part of the game.
> 
> WHY THIS IS WORTH A HARNESS
> 
> The year book's whole value is that somebody who has never seen this game can
> read a run off it. That makes a wrong fold worse than no file at all: a
> population summed over twelve months reads as a city twelve times its size,
> and reads as a FACT, with no way for the reader to catch it. Every assertion
> here is on the text the reader actually gets, not on an internal the reader
> never sees.
> 
> THE FIXTURES ARE BUILT AS SAVED HISTORY, through Gson, on purpose. It is the
> same path a loaded slot takes, so the harness cannot pass on a history that
> could never come off disk - and it lets a series be given an exact shape,
> which is what makes "the sum is 78 and not 77" assertable at all.
> 
> THE ONE THING IT CANNOT CHECK is whether a rule is the RIGHT rule - that
> bankWriteOffs really is a monthly flow and householdSavings really is a
> running total. That is read off HistorySave.recordMonth and written down in
> YearBook.rules(); what this can do, and does, is refuse to let a series exist
> with no rule at all.

**Uses:** [YearBook](YearBook.md) (52), [HistorySave](HistorySave.md) (32), [GameFiles](GameFiles.md) (10), [Game](Game.md) (9), [CityCalendar](CityCalendar.md) (6), [Health](Health.md) (5), [Currency](Currency.md) (2), [PopulationManager](PopulationManager.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (1), [DecisionLog](DecisionLog.md) (1)

## Sections

| line | section |
|---:|---|
| 83 | 1 - EVERY SERIES HAS A RULE |
| 125 | 2 - A FLOW IS ADDED |
| 138 | 3 - A LEVEL IS THE LAST MONTH, NOT THE SUM AND NOT THE MEAN |
| 150 | 4 - A RATE IS AVERAGED, AND ITS WORST MONTH SURVIVES |
| 169 | 4b - THE DIAL AND THE BANK'S PRICES ARE RATES, ITS FEES A FLOW (0.7.7) |
| 192 | 4c - THE BANK'S CAPITAL: THREE RATES, TWO FLOWS AND A LEVEL (0.7.8) |
| 221 | 5 - A SERIES THAT STARTED LATE IS BLANK, NOT ZERO |
| 243 | 6 - A SHORT LAST ROW SAYS SO |
| 255 | 7 - A DECADE IS TEN YEARS OF MONTHS |
| 269 | 8 - THE EPISODE LIST FINDS AN EPISODE A FIXTURE CAUSED |
| 289 | 9 - NO DECIMAL COMMAS, EVER |
| 326 | 10 - THE FILES LAND SOMEWHERE THE PLAYER CAN FIND THEM |
| 339 | FIXTURES AND READING |
| 343 | 11 - THE UNEMPLOYMENT COLUMN IS THE POOL OVER THE LABOUR FORCE |
| 380 | 13 - THE BOOK AGREES WITH THE MODEL, ON A CITY THAT HAS BEEN PLAYED |
| 431 | · · the premise |
| 444 | · · and the assertions |
| 521 | 12 - THE CURRENCY NOTE READS THE SAME WAY AS THE RATE |
| 548 | 14 - THE NAMED EPISODES ARE THE ONES THE FIXTURE CAUSED, AND ONLY THOSE |
| 613 | · · the edges |
| 658 | 14b - AN EPIDEMIC IS AN OUTBREAK (A8, 0.7.46) |
| 792 | 15 - THE CSV IS THE TEXT'S TABLES, CELL FOR CELL (0.7.16) |
| 815 | · · the tables, read both ways |
| 822 | · · a blank stays empty |
| 830 | · · a French locale keeps the point |
| 862 | · · a name that needs quotes gets them, and only then |
| 877 | · · the export, on a city that has been played |
| 1081 | · assertions |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 40 | `YearBookCheck.A_CENT` | `0.005` | What HistorySave.round2() can lose on a figure it stores. |
| 43 | `YearBookCheck.HALF_A_PERSON` | `0.51` | What storing the pool as a whole person can lose on a figure derived from it. |
| 52 | `YearBookCheck.ARDEN` | `Currency.fromCityName("Arden")` | The money a hand-built history is written in (0.7.10). |
| 935 | `YearBookCheck.COMMA_CO` | `"Acme, Inc."` | Two companies on the fixture's register, named the way a CSV has to quote. |
| 937 | `YearBookCheck.QUOTE_CO` | `"The \"Good\" Co"` | ...and the second, with a double quote in its name, which the CSV doubles. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 36 | `static int fails` |  |
| 37 | `static int checks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 1081 | **type** `public class YearBookCheck` | Proves the year book folds each series the way that series has to be folded, and that the file says so. |
| 54 | 1 | `private static String years(HistorySave h)` |  |
| 55 | 1 | `private static String decades(HistorySave h)` |  |
| 57 | 25 | `public static void main(String[] args)` |  |

### 1 - EVERY SERIES HAS A RULE (lines 83-124)

| line | len | member | says |
|---:|---:|---|---|
| 92 | 32 | `private static void everySeriesHasARule()` |  |

### 2 - A FLOW IS ADDED (lines 125-137)

| line | len | member | says |
|---:|---:|---|---|
| 128 | 9 | `private static void aFlowIsAdded()` |  |

### 3 - A LEVEL IS THE LAST MONTH, NOT THE SUM AND NOT THE MEAN (lines 138-149)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 8 | `private static void aLevelIsTheLastMonth()` |  |

### 4 - A RATE IS AVERAGED, AND ITS WORST MONTH SURVIVES (lines 150-168)

| line | len | member | says |
|---:|---:|---|---|
| 157 | 11 | `private static void aRateIsAveragedAndItsWorstMonthIsKept()` |  |

### 4b - THE DIAL AND THE BANK'S PRICES ARE RATES, ITS FEES A FLOW (0.7.7) (lines 169-191)

| line | len | member | says |
|---:|---:|---|---|
| 178 | 13 | `private static void theBanksPricesFoldByTheirKinds()` |  |

### 4c - THE BANK'S CAPITAL: THREE RATES, TWO FLOWS AND A LEVEL (0.7.8) (lines 192-220)

| line | len | member | says |
|---:|---:|---|---|
| 202 | 18 | `private static void theBanksCapitalFoldsByItsKinds()` |  |

### 5 - A SERIES THAT STARTED LATE IS BLANK, NOT ZERO (lines 221-242)

| line | len | member | says |
|---:|---:|---|---|
| 229 | 13 | `private static void aSeriesThatStartedLateStaysBlank()` |  |

### 6 - A SHORT LAST ROW SAYS SO (lines 243-254)

| line | len | member | says |
|---:|---:|---|---|
| 246 | 8 | `private static void aShortLastRowSaysSo()` |  |

### 7 - A DECADE IS TEN YEARS OF MONTHS (lines 255-268)

| line | len | member | says |
|---:|---:|---|---|
| 258 | 10 | `private static void decadesAreTenYears()` |  |

### 8 - THE EPISODE LIST FINDS AN EPISODE A FIXTURE CAUSED (lines 269-288)

| line | len | member | says |
|---:|---:|---|---|
| 276 | 12 | `private static void theEpisodeListFindsAnEpisode()` |  |

### 9 - NO DECIMAL COMMAS, EVER (lines 289-325)

| line | len | member | says |
|---:|---:|---|---|
| 296 | 29 | `private static void theFileNeverWritesADecimalComma()` |  |

### 10 - THE FILES LAND SOMEWHERE THE PLAYER CAN FIND THEM (lines 326-338)

| line | len | member | says |
|---:|---:|---|---|
| 329 | 9 | `private static void theExportPathsAreBesideTheSaves()` |  |

### FIXTURES AND READING (lines 339-342)

### 11 - THE UNEMPLOYMENT COLUMN IS THE POOL OVER THE LABOUR FORCE (lines 343-379)

| line | len | member | says |
|---:|---:|---|---|
| 355 | 24 | `private static void theUnemploymentColumnIsThePool()` |  |

### 13 - THE BOOK AGREES WITH THE MODEL, ON A CITY THAT HAS BEEN PLAYED (lines 380-520)

| line | len | member | says |
|---:|---:|---|---|
| 405 | 83 | `private static void theBookAgreesWithTheModelOnAPlayedCity()` |  |
| 490 | 6 | `private static void put(Game g, String name, int n)` | One template, n of them, paid for and standing - EducationCheck's shape. |
| 497 | 7 | `private static void near(String what, double got, double wanted, double slack)` |  |
| 506 | 14 | `private static HistorySave history(int workforce, int students, int jobs, int outOfWork)` | Twelve identical months of workforce, students, posts and (if not negative) the pool. |

### 12 - THE CURRENCY NOTE READS THE SAME WAY AS THE RATE (lines 521-547)

| line | len | member | says |
|---:|---:|---|---|
| 535 | 12 | `private static void theCurrencyNoteReadsTheRightWay()` |  |

### 14 - THE NAMED EPISODES ARE THE ONES THE FIXTURE CAUSED, AND ONLY THOSE (lines 548-657)

| line | len | member | says |
|---:|---:|---|---|
| 571 | 86 | `private static void theEpisodesAreTheOnesTheFixtureCaused()` |  |

### 14b - AN EPIDEMIC IS AN OUTBREAK (A8, 0.7.46) (lines 658-791)

| line | len | member | says |
|---:|---:|---|---|
| 670 | 26 | `private static void anEpidemicIsAnOutbreak()` |  |
| 702 | 5 | `private static double[] steppedOutput(int months, int at, int loss)` | Output growing by one a month from 100, that loses `loss` a month for good from index `at` on. |
| 708 | 5 | `private static double[] flat(int months, double value)` |  |
| 715 | 14 | `private static HistorySave built(int months, String[] series, double[]...values)` | A history of `months` months with several series filled in, each as long as the axis. |
| 737 | 10 | `private static HistorySave built(int months, String series, double[] values)` | A history of `months` months with one series filled in. |
| 748 | 5 | `private static double[] ramp(int n)` |  |
| 754 | 5 | `private static double sum(int from, int to)` |  |
| 761 | 4 | `private static double cell(String text, String column, int row)` | The value a reader would take out of the file, found the way a reader finds it. |
| 766 | 3 | `private static boolean blank(String text, String column, int row)` |  |
| 770 | 21 | `private static String rawCell(String text, String column, int row)` |  |

### 15 - THE CSV IS THE TEXT'S TABLES, CELL FOR CELL (0.7.16) (lines 792-1080)

| line | len | member | says |
|---:|---:|---|---|
| 811 | 122 | `private static void theCsvIsTheTextsTables()` |  |
| 947 | 19 | `private static HistorySave csvFixture()` | Thirty months, so a year book of two full years and a stub and a decade book of one row: gdp a ramp (a flow), stolen from month 13 only (a flow that started late, so year 1 is blank), population flat (a level), sickRa... |
| 971 | 27 | `private static void sameTable(String what, String text, String title, String csv)` | Reads one of the text's tables by its title line and the CSV by RFC 4180, and requires the same header, the same rows and the same cells. |
| 1003 | 12 | `private static List<List<String>> textTable(String text, String title)` | The text's table under the line that starts with `title`: the header and every row down to the blank line that ends it, split on tabs. |
| 1017 | 3 | `private static List<List<String>> csvRecords(String csv)` | A CSV's records, with nothing counted - for the assertions that read one cell. |
| 1022 | 12 | `private static String csvCell(String csv, String column, int row)` | The field under `column` in the record whose first field is `row`, as the file holds it. |
| 1040 | 30 | `private static List<List<String>> readCsv(String csv, int[] strays)` | RFC 4180, read strictly: a record ends CRLF, a field in quotes may hold anything with its quotes doubled, and a CR, an LF or a quote anywhere else - or a last record with no CRLF - is counted in strays[0]. |
| 1071 | 9 | `private static String read(java.nio.file.Path p)` |  |

### assertions (lines 1081-1114)

| line | len | member | says |
|---:|---:|---|---|
| 1083 | 7 | `private static void same(String what, double got, double wanted)` |  |
| 1091 | 7 | `private static void same(String what, String got, String wanted)` |  |
| 1099 | 7 | `private static void same(String what, boolean got, boolean wanted)` |  |
| 1107 | 7 | `private static void yes(String what, boolean got)` |  |

