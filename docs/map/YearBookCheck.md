# YearBookCheck.java - 761 lines · 33 methods · 2 constants · harnesses

`ham/citybuildersim/YearBookCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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

**Uses:** [YearBook](YearBook.md) (49), [HistorySave](HistorySave.md) (21), [Game](Game.md) (7), [CityCalendar](CityCalendar.md) (6), [GameFiles](GameFiles.md) (5), [PopulationManager](PopulationManager.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (1)

## Sections

| line | section |
|---:|---|
| 69 | 1 - EVERY SERIES HAS A RULE |
| 111 | 2 - A FLOW IS ADDED |
| 124 | 3 - A LEVEL IS THE LAST MONTH, NOT THE SUM AND NOT THE MEAN |
| 136 | 4 - A RATE IS AVERAGED, AND ITS WORST MONTH SURVIVES |
| 155 | 4b - THE DIAL AND THE BANK'S PRICES ARE RATES, ITS FEES A FLOW (0.7.7) |
| 178 | 4c - THE BANK'S CAPITAL: THREE RATES, TWO FLOWS AND A LEVEL (0.7.8) |
| 207 | 5 - A SERIES THAT STARTED LATE IS BLANK, NOT ZERO |
| 229 | 6 - A SHORT LAST ROW SAYS SO |
| 241 | 7 - A DECADE IS TEN YEARS OF MONTHS |
| 255 | 8 - THE EPISODE LIST FINDS AN EPISODE A FIXTURE CAUSED |
| 275 | 9 - NO DECIMAL COMMAS, EVER |
| 312 | 10 - THE FILES LAND SOMEWHERE THE PLAYER CAN FIND THEM |
| 325 | FIXTURES AND READING |
| 329 | 11 - THE UNEMPLOYMENT COLUMN IS THE POOL OVER THE LABOUR FORCE |
| 366 | 13 - THE BOOK AGREES WITH THE MODEL, ON A CITY THAT HAS BEEN PLAYED |
| 417 | · · the premise |
| 430 | · · and the assertions |
| 507 | 12 - THE CURRENCY NOTE READS THE SAME WAY AS THE RATE |
| 523 | 14 - THE NAMED EPISODES ARE THE ONES THE FIXTURE CAUSED, AND ONLY THOSE |
| 588 | · · the edges |
| 728 | · assertions |

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
| 34 | 728 | **type** `public class YearBookCheck` | Proves the year book folds each series the way that series has to be folded, and that the file says so. |
| 45 | 23 | `public static void main(String[] args)` |  |

### 1 - EVERY SERIES HAS A RULE (lines 69-110)

| line | len | member | says |
|---:|---:|---|---|
| 78 | 32 | `private static void everySeriesHasARule()` |  |

### 2 - A FLOW IS ADDED (lines 111-123)

| line | len | member | says |
|---:|---:|---|---|
| 114 | 9 | `private static void aFlowIsAdded()` |  |

### 3 - A LEVEL IS THE LAST MONTH, NOT THE SUM AND NOT THE MEAN (lines 124-135)

| line | len | member | says |
|---:|---:|---|---|
| 127 | 8 | `private static void aLevelIsTheLastMonth()` |  |

### 4 - A RATE IS AVERAGED, AND ITS WORST MONTH SURVIVES (lines 136-154)

| line | len | member | says |
|---:|---:|---|---|
| 143 | 11 | `private static void aRateIsAveragedAndItsWorstMonthIsKept()` |  |

### 4b - THE DIAL AND THE BANK'S PRICES ARE RATES, ITS FEES A FLOW (0.7.7) (lines 155-177)

| line | len | member | says |
|---:|---:|---|---|
| 164 | 13 | `private static void theBanksPricesFoldByTheirKinds()` |  |

### 4c - THE BANK'S CAPITAL: THREE RATES, TWO FLOWS AND A LEVEL (0.7.8) (lines 178-206)

| line | len | member | says |
|---:|---:|---|---|
| 188 | 18 | `private static void theBanksCapitalFoldsByItsKinds()` |  |

### 5 - A SERIES THAT STARTED LATE IS BLANK, NOT ZERO (lines 207-228)

| line | len | member | says |
|---:|---:|---|---|
| 215 | 13 | `private static void aSeriesThatStartedLateStaysBlank()` |  |

### 6 - A SHORT LAST ROW SAYS SO (lines 229-240)

| line | len | member | says |
|---:|---:|---|---|
| 232 | 8 | `private static void aShortLastRowSaysSo()` |  |

### 7 - A DECADE IS TEN YEARS OF MONTHS (lines 241-254)

| line | len | member | says |
|---:|---:|---|---|
| 244 | 10 | `private static void decadesAreTenYears()` |  |

### 8 - THE EPISODE LIST FINDS AN EPISODE A FIXTURE CAUSED (lines 255-274)

| line | len | member | says |
|---:|---:|---|---|
| 262 | 12 | `private static void theEpisodeListFindsAnEpisode()` |  |

### 9 - NO DECIMAL COMMAS, EVER (lines 275-311)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 29 | `private static void theFileNeverWritesADecimalComma()` |  |

### 10 - THE FILES LAND SOMEWHERE THE PLAYER CAN FIND THEM (lines 312-324)

| line | len | member | says |
|---:|---:|---|---|
| 315 | 9 | `private static void theExportPathsAreBesideTheSaves()` |  |

### FIXTURES AND READING (lines 325-328)

### 11 - THE UNEMPLOYMENT COLUMN IS THE POOL OVER THE LABOUR FORCE (lines 329-365)

| line | len | member | says |
|---:|---:|---|---|
| 341 | 24 | `private static void theUnemploymentColumnIsThePool()` |  |

### 13 - THE BOOK AGREES WITH THE MODEL, ON A CITY THAT HAS BEEN PLAYED (lines 366-506)

| line | len | member | says |
|---:|---:|---|---|
| 391 | 83 | `private static void theBookAgreesWithTheModelOnAPlayedCity()` |  |
| 476 | 6 | `private static void put(Game g, String name, int n)` | One template, n of them, paid for and standing - EducationCheck's shape. |
| 483 | 7 | `private static void near(String what, double got, double wanted, double slack)` |  |
| 492 | 14 | `private static HistorySave history(int workforce, int students, int jobs, int outOfWork)` | Twelve identical months of workforce, students, posts and (if not negative) the pool. |

### 12 - THE CURRENCY NOTE READS THE SAME WAY AS THE RATE (lines 507-522)

| line | len | member | says |
|---:|---:|---|---|
| 515 | 7 | `private static void theCurrencyNoteReadsTheRightWay()` |  |

### 14 - THE NAMED EPISODES ARE THE ONES THE FIXTURE CAUSED, AND ONLY THOSE (lines 523-727)

| line | len | member | says |
|---:|---:|---|---|
| 546 | 86 | `private static void theEpisodesAreTheOnesTheFixtureCaused()` |  |
| 638 | 5 | `private static double[] steppedOutput(int months, int at, int loss)` | Output growing by one a month from 100, that loses `loss` a month for good from index `at` on. |
| 644 | 5 | `private static double[] flat(int months, double value)` |  |
| 651 | 14 | `private static HistorySave built(int months, String[] series, double[]...values)` | A history of `months` months with several series filled in, each as long as the axis. |
| 673 | 10 | `private static HistorySave built(int months, String series, double[] values)` | A history of `months` months with one series filled in. |
| 684 | 5 | `private static double[] ramp(int n)` |  |
| 690 | 5 | `private static double sum(int from, int to)` |  |
| 697 | 4 | `private static double cell(String text, String column, int row)` | The value a reader would take out of the file, found the way a reader finds it. |
| 702 | 3 | `private static boolean blank(String text, String column, int row)` |  |
| 706 | 21 | `private static String rawCell(String text, String column, int row)` |  |

### assertions (lines 728-761)

| line | len | member | says |
|---:|---:|---|---|
| 730 | 7 | `private static void same(String what, double got, double wanted)` |  |
| 738 | 7 | `private static void same(String what, String got, String wanted)` |  |
| 746 | 7 | `private static void same(String what, boolean got, boolean wanted)` |  |
| 754 | 7 | `private static void yes(String what, boolean got)` |  |

