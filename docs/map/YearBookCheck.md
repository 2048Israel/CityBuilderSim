# YearBookCheck.java - 563 lines · 27 methods · 2 constants · harnesses

`ham/citybuildersim/YearBookCheck.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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

**Uses:** [YearBook](YearBook.md) (26), [HistorySave](HistorySave.md) (17), [Game](Game.md) (7), [GameFiles](GameFiles.md) (5), [PopulationManager](PopulationManager.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (1)

## Sections

| line | section |
|---:|---|
| 66 | 1 - EVERY SERIES HAS A RULE |
| 108 | 2 - A FLOW IS ADDED |
| 121 | 3 - A LEVEL IS THE LAST MONTH, NOT THE SUM AND NOT THE MEAN |
| 133 | 4 - A RATE IS AVERAGED, AND ITS WORST MONTH SURVIVES |
| 152 | 5 - A SERIES THAT STARTED LATE IS BLANK, NOT ZERO |
| 174 | 6 - A SHORT LAST ROW SAYS SO |
| 186 | 7 - A DECADE IS TEN YEARS OF MONTHS |
| 200 | 8 - THE EPISODE LIST FINDS AN EPISODE A FIXTURE CAUSED |
| 220 | 9 - NO DECIMAL COMMAS, EVER |
| 257 | 10 - THE FILES LAND SOMEWHERE THE PLAYER CAN FIND THEM |
| 270 | FIXTURES AND READING |
| 274 | 11 - THE UNEMPLOYMENT COLUMN IS THE POOL OVER THE LABOUR FORCE |
| 311 | 13 - THE BOOK AGREES WITH THE MODEL, ON A CITY THAT HAS BEEN PLAYED |
| 362 | · · the premise |
| 375 | · · and the assertions |
| 452 | 12 - THE CURRENCY NOTE READS THE SAME WAY AS THE RATE |
| 530 | · assertions |

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
| 34 | 530 | **type** `public class YearBookCheck` | Proves the year book folds each series the way that series has to be folded, and that the file says so. |
| 45 | 20 | `public static void main(String[] args)` |  |

### 1 - EVERY SERIES HAS A RULE (lines 66-107)

| line | len | member | says |
|---:|---:|---|---|
| 75 | 32 | `private static void everySeriesHasARule()` |  |

### 2 - A FLOW IS ADDED (lines 108-120)

| line | len | member | says |
|---:|---:|---|---|
| 111 | 9 | `private static void aFlowIsAdded()` |  |

### 3 - A LEVEL IS THE LAST MONTH, NOT THE SUM AND NOT THE MEAN (lines 121-132)

| line | len | member | says |
|---:|---:|---|---|
| 124 | 8 | `private static void aLevelIsTheLastMonth()` |  |

### 4 - A RATE IS AVERAGED, AND ITS WORST MONTH SURVIVES (lines 133-151)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 11 | `private static void aRateIsAveragedAndItsWorstMonthIsKept()` |  |

### 5 - A SERIES THAT STARTED LATE IS BLANK, NOT ZERO (lines 152-173)

| line | len | member | says |
|---:|---:|---|---|
| 160 | 13 | `private static void aSeriesThatStartedLateStaysBlank()` |  |

### 6 - A SHORT LAST ROW SAYS SO (lines 174-185)

| line | len | member | says |
|---:|---:|---|---|
| 177 | 8 | `private static void aShortLastRowSaysSo()` |  |

### 7 - A DECADE IS TEN YEARS OF MONTHS (lines 186-199)

| line | len | member | says |
|---:|---:|---|---|
| 189 | 10 | `private static void decadesAreTenYears()` |  |

### 8 - THE EPISODE LIST FINDS AN EPISODE A FIXTURE CAUSED (lines 200-219)

| line | len | member | says |
|---:|---:|---|---|
| 207 | 12 | `private static void theEpisodeListFindsAnEpisode()` |  |

### 9 - NO DECIMAL COMMAS, EVER (lines 220-256)

| line | len | member | says |
|---:|---:|---|---|
| 227 | 29 | `private static void theFileNeverWritesADecimalComma()` |  |

### 10 - THE FILES LAND SOMEWHERE THE PLAYER CAN FIND THEM (lines 257-269)

| line | len | member | says |
|---:|---:|---|---|
| 260 | 9 | `private static void theExportPathsAreBesideTheSaves()` |  |

### FIXTURES AND READING (lines 270-273)

### 11 - THE UNEMPLOYMENT COLUMN IS THE POOL OVER THE LABOUR FORCE (lines 274-310)

| line | len | member | says |
|---:|---:|---|---|
| 286 | 24 | `private static void theUnemploymentColumnIsThePool()` |  |

### 13 - THE BOOK AGREES WITH THE MODEL, ON A CITY THAT HAS BEEN PLAYED (lines 311-451)

| line | len | member | says |
|---:|---:|---|---|
| 336 | 83 | `private static void theBookAgreesWithTheModelOnAPlayedCity()` |  |
| 421 | 6 | `private static void put(Game g, String name, int n)` | One template, n of them, paid for and standing - EducationCheck's shape. |
| 428 | 7 | `private static void near(String what, double got, double wanted, double slack)` |  |
| 437 | 14 | `private static HistorySave history(int workforce, int students, int jobs, int outOfWork)` | Twelve identical months of workforce, students, posts and (if not negative) the pool. |

### 12 - THE CURRENCY NOTE READS THE SAME WAY AS THE RATE (lines 452-529)

| line | len | member | says |
|---:|---:|---|---|
| 460 | 7 | `private static void theCurrencyNoteReadsTheRightWay()` |  |
| 475 | 10 | `private static HistorySave built(int months, String series, double[] values)` | A history of `months` months with one series filled in. |
| 486 | 5 | `private static double[] ramp(int n)` |  |
| 492 | 5 | `private static double sum(int from, int to)` |  |
| 499 | 4 | `private static double cell(String text, String column, int row)` | The value a reader would take out of the file, found the way a reader finds it. |
| 504 | 3 | `private static boolean blank(String text, String column, int row)` |  |
| 508 | 21 | `private static String rawCell(String text, String column, int row)` |  |

### assertions (lines 530-563)

| line | len | member | says |
|---:|---:|---|---|
| 532 | 7 | `private static void same(String what, double got, double wanted)` |  |
| 540 | 7 | `private static void same(String what, String got, String wanted)` |  |
| 548 | 7 | `private static void same(String what, boolean got, boolean wanted)` |  |
| 556 | 7 | `private static void yes(String what, boolean got)` |  |

