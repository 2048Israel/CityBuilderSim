# YearBookCheck.java - 707 lines · 31 methods · 2 constants · harnesses

`ham/citybuildersim/YearBookCheck.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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

**Uses:** [YearBook](YearBook.md) (44), [HistorySave](HistorySave.md) (21), [Game](Game.md) (7), [CityCalendar](CityCalendar.md) (6), [GameFiles](GameFiles.md) (5), [PopulationManager](PopulationManager.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (1)

## Sections

| line | section |
|---:|---|
| 67 | 1 - EVERY SERIES HAS A RULE |
| 109 | 2 - A FLOW IS ADDED |
| 122 | 3 - A LEVEL IS THE LAST MONTH, NOT THE SUM AND NOT THE MEAN |
| 134 | 4 - A RATE IS AVERAGED, AND ITS WORST MONTH SURVIVES |
| 153 | 5 - A SERIES THAT STARTED LATE IS BLANK, NOT ZERO |
| 175 | 6 - A SHORT LAST ROW SAYS SO |
| 187 | 7 - A DECADE IS TEN YEARS OF MONTHS |
| 201 | 8 - THE EPISODE LIST FINDS AN EPISODE A FIXTURE CAUSED |
| 221 | 9 - NO DECIMAL COMMAS, EVER |
| 258 | 10 - THE FILES LAND SOMEWHERE THE PLAYER CAN FIND THEM |
| 271 | FIXTURES AND READING |
| 275 | 11 - THE UNEMPLOYMENT COLUMN IS THE POOL OVER THE LABOUR FORCE |
| 312 | 13 - THE BOOK AGREES WITH THE MODEL, ON A CITY THAT HAS BEEN PLAYED |
| 363 | · · the premise |
| 376 | · · and the assertions |
| 453 | 12 - THE CURRENCY NOTE READS THE SAME WAY AS THE RATE |
| 469 | 14 - THE NAMED EPISODES ARE THE ONES THE FIXTURE CAUSED, AND ONLY THOSE |
| 534 | · · the edges |
| 674 | · assertions |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 40 | `YearBookCheck.A_CENT` | `0.005` | What HistorySave.round2() can lose on a figure it stores. |
| 43 | `YearBookCheck.HALF_A_PERSON` | `0.51` | What storing the pool as a whole person can lose on a figure derived from it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 36 | `static int fails` |  |
| 37 | `static int checks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 674 | **type** `public class YearBookCheck` | Proves the year book folds each series the way that series has to be folded, and that the file says so. |
| 45 | 21 | `public static void main(String[] args)` |  |

### 1 - EVERY SERIES HAS A RULE (lines 67-108)

| line | len | member | says |
|---:|---:|---|---|
| 76 | 32 | `private static void everySeriesHasARule()` |  |

### 2 - A FLOW IS ADDED (lines 109-121)

| line | len | member | says |
|---:|---:|---|---|
| 112 | 9 | `private static void aFlowIsAdded()` |  |

### 3 - A LEVEL IS THE LAST MONTH, NOT THE SUM AND NOT THE MEAN (lines 122-133)

| line | len | member | says |
|---:|---:|---|---|
| 125 | 8 | `private static void aLevelIsTheLastMonth()` |  |

### 4 - A RATE IS AVERAGED, AND ITS WORST MONTH SURVIVES (lines 134-152)

| line | len | member | says |
|---:|---:|---|---|
| 141 | 11 | `private static void aRateIsAveragedAndItsWorstMonthIsKept()` |  |

### 5 - A SERIES THAT STARTED LATE IS BLANK, NOT ZERO (lines 153-174)

| line | len | member | says |
|---:|---:|---|---|
| 161 | 13 | `private static void aSeriesThatStartedLateStaysBlank()` |  |

### 6 - A SHORT LAST ROW SAYS SO (lines 175-186)

| line | len | member | says |
|---:|---:|---|---|
| 178 | 8 | `private static void aShortLastRowSaysSo()` |  |

### 7 - A DECADE IS TEN YEARS OF MONTHS (lines 187-200)

| line | len | member | says |
|---:|---:|---|---|
| 190 | 10 | `private static void decadesAreTenYears()` |  |

### 8 - THE EPISODE LIST FINDS AN EPISODE A FIXTURE CAUSED (lines 201-220)

| line | len | member | says |
|---:|---:|---|---|
| 208 | 12 | `private static void theEpisodeListFindsAnEpisode()` |  |

### 9 - NO DECIMAL COMMAS, EVER (lines 221-257)

| line | len | member | says |
|---:|---:|---|---|
| 228 | 29 | `private static void theFileNeverWritesADecimalComma()` |  |

### 10 - THE FILES LAND SOMEWHERE THE PLAYER CAN FIND THEM (lines 258-270)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 9 | `private static void theExportPathsAreBesideTheSaves()` |  |

### FIXTURES AND READING (lines 271-274)

### 11 - THE UNEMPLOYMENT COLUMN IS THE POOL OVER THE LABOUR FORCE (lines 275-311)

| line | len | member | says |
|---:|---:|---|---|
| 287 | 24 | `private static void theUnemploymentColumnIsThePool()` |  |

### 13 - THE BOOK AGREES WITH THE MODEL, ON A CITY THAT HAS BEEN PLAYED (lines 312-452)

| line | len | member | says |
|---:|---:|---|---|
| 337 | 83 | `private static void theBookAgreesWithTheModelOnAPlayedCity()` |  |
| 422 | 6 | `private static void put(Game g, String name, int n)` | One template, n of them, paid for and standing - EducationCheck's shape. |
| 429 | 7 | `private static void near(String what, double got, double wanted, double slack)` |  |
| 438 | 14 | `private static HistorySave history(int workforce, int students, int jobs, int outOfWork)` | Twelve identical months of workforce, students, posts and (if not negative) the pool. |

### 12 - THE CURRENCY NOTE READS THE SAME WAY AS THE RATE (lines 453-468)

| line | len | member | says |
|---:|---:|---|---|
| 461 | 7 | `private static void theCurrencyNoteReadsTheRightWay()` |  |

### 14 - THE NAMED EPISODES ARE THE ONES THE FIXTURE CAUSED, AND ONLY THOSE (lines 469-673)

| line | len | member | says |
|---:|---:|---|---|
| 492 | 86 | `private static void theEpisodesAreTheOnesTheFixtureCaused()` |  |
| 584 | 5 | `private static double[] steppedOutput(int months, int at, int loss)` | Output growing by one a month from 100, that loses `loss` a month for good from index `at` on. |
| 590 | 5 | `private static double[] flat(int months, double value)` |  |
| 597 | 14 | `private static HistorySave built(int months, String[] series, double[]...values)` | A history of `months` months with several series filled in, each as long as the axis. |
| 619 | 10 | `private static HistorySave built(int months, String series, double[] values)` | A history of `months` months with one series filled in. |
| 630 | 5 | `private static double[] ramp(int n)` |  |
| 636 | 5 | `private static double sum(int from, int to)` |  |
| 643 | 4 | `private static double cell(String text, String column, int row)` | The value a reader would take out of the file, found the way a reader finds it. |
| 648 | 3 | `private static boolean blank(String text, String column, int row)` |  |
| 652 | 21 | `private static String rawCell(String text, String column, int row)` |  |

### assertions (lines 674-707)

| line | len | member | says |
|---:|---:|---|---|
| 676 | 7 | `private static void same(String what, double got, double wanted)` |  |
| 684 | 7 | `private static void same(String what, String got, String wanted)` |  |
| 692 | 7 | `private static void same(String what, boolean got, boolean wanted)` |  |
| 700 | 7 | `private static void yes(String what, boolean got)` |  |

