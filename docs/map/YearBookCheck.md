# YearBookCheck.java - 1,132 lines · 44 methods · 5 constants · harnesses

`ham/citybuildersim/YearBookCheck.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

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

**Uses:** [YearBook](YearBook.md) (52), [HistorySave](HistorySave.md) (32), [GameFiles](GameFiles.md) (10), [Game](Game.md) (9), [CityCalendar](CityCalendar.md) (6), [Health](Health.md) (5), [Currency](Currency.md) (2), [PopulationManager](PopulationManager.md) (2), [Sectors](Sectors.md) (1), [PriceLevel](PriceLevel.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [DecisionLog](DecisionLog.md) (1)

## Sections

| line | section |
|---:|---|
| 83 | 1 - EVERY SERIES HAS A RULE |
| 125 | 2 - A FLOW IS ADDED |
| 144 | 3 - A LEVEL IS THE LAST MONTH, NOT THE SUM AND NOT THE MEAN |
| 156 | 4 - A RATE IS AVERAGED, AND ITS WORST MONTH SURVIVES |
| 175 | 4b - THE DIAL AND THE BANK'S PRICES ARE RATES, ITS FEES A FLOW (0.7.7) |
| 198 | 4c - THE BANK'S CAPITAL: THREE RATES, TWO FLOWS AND A LEVEL (0.7.8) |
| 227 | 5 - A SERIES THAT STARTED LATE IS BLANK, NOT ZERO |
| 249 | 6 - A SHORT LAST ROW SAYS SO |
| 261 | 7 - A DECADE IS TEN YEARS OF MONTHS |
| 275 | 8 - THE EPISODE LIST FINDS AN EPISODE A FIXTURE CAUSED |
| 295 | 9 - NO DECIMAL COMMAS, EVER |
| 332 | 10 - THE FILES LAND SOMEWHERE THE PLAYER CAN FIND THEM |
| 345 | FIXTURES AND READING |
| 349 | 11 - THE UNEMPLOYMENT COLUMN IS THE POOL OVER THE LABOUR FORCE |
| 386 | 13 - THE BOOK AGREES WITH THE MODEL, ON A CITY THAT HAS BEEN PLAYED |
| 446 | · · the premise |
| 462 | · · and the assertions |
| 539 | 12 - THE CURRENCY NOTE READS THE SAME WAY AS THE RATE |
| 566 | 14 - THE NAMED EPISODES ARE THE ONES THE FIXTURE CAUSED, AND ONLY THOSE |
| 631 | · · the edges |
| 676 | 14b - AN EPIDEMIC IS AN OUTBREAK (A8, 0.7.46) |
| 810 | 15 - THE CSV IS THE TEXT'S TABLES, CELL FOR CELL (0.7.16) |
| 833 | · · the tables, read both ways |
| 840 | · · a blank stays empty |
| 848 | · · a French locale keeps the point |
| 880 | · · a name that needs quotes gets them, and only then |
| 895 | · · the export, on a city that has been played |
| 1099 | · assertions |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 40 | `YearBookCheck.A_CENT` | `0.005` | What HistorySave.round2() can lose on a figure it stores. |
| 43 | `YearBookCheck.HALF_A_PERSON` | `0.51` | What storing the pool as a whole person can lose on a figure derived from it. |
| 52 | `YearBookCheck.ARDEN` | `Currency.fromCityName("Arden")` | The money a hand-built history is written in (0.7.10). |
| 953 | `YearBookCheck.COMMA_CO` | `"Acme, Inc."` | Two companies on the fixture's register, named the way a CSV has to quote. |
| 955 | `YearBookCheck.QUOTE_CO` | `"The \"Good\" Co"` | ...and the second, with a double quote in its name, which the CSV doubles. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 36 | `static int fails` |  |
| 37 | `static int checks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 1099 | **type** `public class YearBookCheck` | Proves the year book folds each series the way that series has to be folded, and that the file says so. |
| 54 | 1 | `private static String years(HistorySave h)` |  |
| 55 | 1 | `private static String decades(HistorySave h)` |  |
| 57 | 25 | `public static void main(String[] args)` |  |

### 1 - EVERY SERIES HAS A RULE (lines 83-124)

| line | len | member | says |
|---:|---:|---|---|
| 92 | 32 | `private static void everySeriesHasARule()` |  |

### 2 - A FLOW IS ADDED (lines 125-143)

| line | len | member | says |
|---:|---:|---|---|
| 128 | 15 | `private static void aFlowIsAdded()` |  |

### 3 - A LEVEL IS THE LAST MONTH, NOT THE SUM AND NOT THE MEAN (lines 144-155)

| line | len | member | says |
|---:|---:|---|---|
| 147 | 8 | `private static void aLevelIsTheLastMonth()` |  |

### 4 - A RATE IS AVERAGED, AND ITS WORST MONTH SURVIVES (lines 156-174)

| line | len | member | says |
|---:|---:|---|---|
| 163 | 11 | `private static void aRateIsAveragedAndItsWorstMonthIsKept()` |  |

### 4b - THE DIAL AND THE BANK'S PRICES ARE RATES, ITS FEES A FLOW (0.7.7) (lines 175-197)

| line | len | member | says |
|---:|---:|---|---|
| 184 | 13 | `private static void theBanksPricesFoldByTheirKinds()` |  |

### 4c - THE BANK'S CAPITAL: THREE RATES, TWO FLOWS AND A LEVEL (0.7.8) (lines 198-226)

| line | len | member | says |
|---:|---:|---|---|
| 208 | 18 | `private static void theBanksCapitalFoldsByItsKinds()` |  |

### 5 - A SERIES THAT STARTED LATE IS BLANK, NOT ZERO (lines 227-248)

| line | len | member | says |
|---:|---:|---|---|
| 235 | 13 | `private static void aSeriesThatStartedLateStaysBlank()` |  |

### 6 - A SHORT LAST ROW SAYS SO (lines 249-260)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 8 | `private static void aShortLastRowSaysSo()` |  |

### 7 - A DECADE IS TEN YEARS OF MONTHS (lines 261-274)

| line | len | member | says |
|---:|---:|---|---|
| 264 | 10 | `private static void decadesAreTenYears()` |  |

### 8 - THE EPISODE LIST FINDS AN EPISODE A FIXTURE CAUSED (lines 275-294)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 12 | `private static void theEpisodeListFindsAnEpisode()` |  |

### 9 - NO DECIMAL COMMAS, EVER (lines 295-331)

| line | len | member | says |
|---:|---:|---|---|
| 302 | 29 | `private static void theFileNeverWritesADecimalComma()` |  |

### 10 - THE FILES LAND SOMEWHERE THE PLAYER CAN FIND THEM (lines 332-344)

| line | len | member | says |
|---:|---:|---|---|
| 335 | 9 | `private static void theExportPathsAreBesideTheSaves()` |  |

### FIXTURES AND READING (lines 345-348)

### 11 - THE UNEMPLOYMENT COLUMN IS THE POOL OVER THE LABOUR FORCE (lines 349-385)

| line | len | member | says |
|---:|---:|---|---|
| 361 | 24 | `private static void theUnemploymentColumnIsThePool()` |  |

### 13 - THE BOOK AGREES WITH THE MODEL, ON A CITY THAT HAS BEEN PLAYED (lines 386-538)

| line | len | member | says |
|---:|---:|---|---|
| 411 | 95 | `private static void theBookAgreesWithTheModelOnAPlayedCity()` |  |
| 508 | 6 | `private static void put(Game g, String name, int n)` | One template, n of them, paid for and standing - EducationCheck's shape. |
| 515 | 7 | `private static void near(String what, double got, double wanted, double slack)` |  |
| 524 | 14 | `private static HistorySave history(int workforce, int students, int jobs, int outOfWork)` | Twelve identical months of workforce, students, posts and (if not negative) the pool. |

### 12 - THE CURRENCY NOTE READS THE SAME WAY AS THE RATE (lines 539-565)

| line | len | member | says |
|---:|---:|---|---|
| 553 | 12 | `private static void theCurrencyNoteReadsTheRightWay()` |  |

### 14 - THE NAMED EPISODES ARE THE ONES THE FIXTURE CAUSED, AND ONLY THOSE (lines 566-675)

| line | len | member | says |
|---:|---:|---|---|
| 589 | 86 | `private static void theEpisodesAreTheOnesTheFixtureCaused()` |  |

### 14b - AN EPIDEMIC IS AN OUTBREAK (A8, 0.7.46) (lines 676-809)

| line | len | member | says |
|---:|---:|---|---|
| 688 | 26 | `private static void anEpidemicIsAnOutbreak()` |  |
| 720 | 5 | `private static double[] steppedOutput(int months, int at, int loss)` | Output growing by one a month from 100, that loses `loss` a month for good from index `at` on. |
| 726 | 5 | `private static double[] flat(int months, double value)` |  |
| 733 | 14 | `private static HistorySave built(int months, String[] series, double[]...values)` | A history of `months` months with several series filled in, each as long as the axis. |
| 755 | 10 | `private static HistorySave built(int months, String series, double[] values)` | A history of `months` months with one series filled in. |
| 766 | 5 | `private static double[] ramp(int n)` |  |
| 772 | 5 | `private static double sum(int from, int to)` |  |
| 779 | 4 | `private static double cell(String text, String column, int row)` | The value a reader would take out of the file, found the way a reader finds it. |
| 784 | 3 | `private static boolean blank(String text, String column, int row)` |  |
| 788 | 21 | `private static String rawCell(String text, String column, int row)` |  |

### 15 - THE CSV IS THE TEXT'S TABLES, CELL FOR CELL (0.7.16) (lines 810-1098)

| line | len | member | says |
|---:|---:|---|---|
| 829 | 122 | `private static void theCsvIsTheTextsTables()` |  |
| 965 | 19 | `private static HistorySave csvFixture()` | Thirty months, so a year book of two full years and a stub and a decade book of one row: gdp a ramp (a flow), stolen from month 13 only (a flow that started late, so year 1 is blank), population flat (a level), sickRa... |
| 989 | 27 | `private static void sameTable(String what, String text, String title, String csv)` | Reads one of the text's tables by its title line and the CSV by RFC 4180, and requires the same header, the same rows and the same cells. |
| 1021 | 12 | `private static List<List<String>> textTable(String text, String title)` | The text's table under the line that starts with `title`: the header and every row down to the blank line that ends it, split on tabs. |
| 1035 | 3 | `private static List<List<String>> csvRecords(String csv)` | A CSV's records, with nothing counted - for the assertions that read one cell. |
| 1040 | 12 | `private static String csvCell(String csv, String column, int row)` | The field under `column` in the record whose first field is `row`, as the file holds it. |
| 1058 | 30 | `private static List<List<String>> readCsv(String csv, int[] strays)` | RFC 4180, read strictly: a record ends CRLF, a field in quotes may hold anything with its quotes doubled, and a CR, an LF or a quote anywhere else - or a last record with no CRLF - is counted in strays[0]. |
| 1089 | 9 | `private static String read(java.nio.file.Path p)` |  |

### assertions (lines 1099-1132)

| line | len | member | says |
|---:|---:|---|---|
| 1101 | 7 | `private static void same(String what, double got, double wanted)` |  |
| 1109 | 7 | `private static void same(String what, String got, String wanted)` |  |
| 1117 | 7 | `private static void same(String what, boolean got, boolean wanted)` |  |
| 1125 | 7 | `private static void yes(String what, boolean got)` |  |

