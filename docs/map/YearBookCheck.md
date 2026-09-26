# YearBookCheck.java - 784 lines · 35 methods · 3 constants · harnesses

`ham/citybuildersim/YearBookCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [YearBook](YearBook.md) (35), [HistorySave](HistorySave.md) (23), [Game](Game.md) (7), [CityCalendar](CityCalendar.md) (6), [GameFiles](GameFiles.md) (5), [Currency](Currency.md) (2), [PopulationManager](PopulationManager.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (1)

## Sections

| line | section |
|---:|---|
| 81 | 1 - EVERY SERIES HAS A RULE |
| 123 | 2 - A FLOW IS ADDED |
| 136 | 3 - A LEVEL IS THE LAST MONTH, NOT THE SUM AND NOT THE MEAN |
| 148 | 4 - A RATE IS AVERAGED, AND ITS WORST MONTH SURVIVES |
| 167 | 4b - THE DIAL AND THE BANK'S PRICES ARE RATES, ITS FEES A FLOW (0.7.7) |
| 190 | 4c - THE BANK'S CAPITAL: THREE RATES, TWO FLOWS AND A LEVEL (0.7.8) |
| 219 | 5 - A SERIES THAT STARTED LATE IS BLANK, NOT ZERO |
| 241 | 6 - A SHORT LAST ROW SAYS SO |
| 253 | 7 - A DECADE IS TEN YEARS OF MONTHS |
| 267 | 8 - THE EPISODE LIST FINDS AN EPISODE A FIXTURE CAUSED |
| 287 | 9 - NO DECIMAL COMMAS, EVER |
| 324 | 10 - THE FILES LAND SOMEWHERE THE PLAYER CAN FIND THEM |
| 337 | FIXTURES AND READING |
| 341 | 11 - THE UNEMPLOYMENT COLUMN IS THE POOL OVER THE LABOUR FORCE |
| 378 | 13 - THE BOOK AGREES WITH THE MODEL, ON A CITY THAT HAS BEEN PLAYED |
| 429 | · · the premise |
| 442 | · · and the assertions |
| 519 | 12 - THE CURRENCY NOTE READS THE SAME WAY AS THE RATE |
| 546 | 14 - THE NAMED EPISODES ARE THE ONES THE FIXTURE CAUSED, AND ONLY THOSE |
| 611 | · · the edges |
| 751 | · assertions |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 40 | `YearBookCheck.A_CENT` | `0.005` | What HistorySave.round2() can lose on a figure it stores. |
| 43 | `YearBookCheck.HALF_A_PERSON` | `0.51` | What storing the pool as a whole person can lose on a figure derived from it. |
| 52 | `YearBookCheck.ARDEN` | `Currency.fromCityName("Arden")` | The money a hand-built history is written in (0.7.10). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 36 | `static int fails` |  |
| 37 | `static int checks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 751 | **type** `public class YearBookCheck` | Proves the year book folds each series the way that series has to be folded, and that the file says so. |
| 54 | 1 | `private static String years(HistorySave h)` |  |
| 55 | 1 | `private static String decades(HistorySave h)` |  |
| 57 | 23 | `public static void main(String[] args)` |  |

### 1 - EVERY SERIES HAS A RULE (lines 81-122)

| line | len | member | says |
|---:|---:|---|---|
| 90 | 32 | `private static void everySeriesHasARule()` |  |

### 2 - A FLOW IS ADDED (lines 123-135)

| line | len | member | says |
|---:|---:|---|---|
| 126 | 9 | `private static void aFlowIsAdded()` |  |

### 3 - A LEVEL IS THE LAST MONTH, NOT THE SUM AND NOT THE MEAN (lines 136-147)

| line | len | member | says |
|---:|---:|---|---|
| 139 | 8 | `private static void aLevelIsTheLastMonth()` |  |

### 4 - A RATE IS AVERAGED, AND ITS WORST MONTH SURVIVES (lines 148-166)

| line | len | member | says |
|---:|---:|---|---|
| 155 | 11 | `private static void aRateIsAveragedAndItsWorstMonthIsKept()` |  |

### 4b - THE DIAL AND THE BANK'S PRICES ARE RATES, ITS FEES A FLOW (0.7.7) (lines 167-189)

| line | len | member | says |
|---:|---:|---|---|
| 176 | 13 | `private static void theBanksPricesFoldByTheirKinds()` |  |

### 4c - THE BANK'S CAPITAL: THREE RATES, TWO FLOWS AND A LEVEL (0.7.8) (lines 190-218)

| line | len | member | says |
|---:|---:|---|---|
| 200 | 18 | `private static void theBanksCapitalFoldsByItsKinds()` |  |

### 5 - A SERIES THAT STARTED LATE IS BLANK, NOT ZERO (lines 219-240)

| line | len | member | says |
|---:|---:|---|---|
| 227 | 13 | `private static void aSeriesThatStartedLateStaysBlank()` |  |

### 6 - A SHORT LAST ROW SAYS SO (lines 241-252)

| line | len | member | says |
|---:|---:|---|---|
| 244 | 8 | `private static void aShortLastRowSaysSo()` |  |

### 7 - A DECADE IS TEN YEARS OF MONTHS (lines 253-266)

| line | len | member | says |
|---:|---:|---|---|
| 256 | 10 | `private static void decadesAreTenYears()` |  |

### 8 - THE EPISODE LIST FINDS AN EPISODE A FIXTURE CAUSED (lines 267-286)

| line | len | member | says |
|---:|---:|---|---|
| 274 | 12 | `private static void theEpisodeListFindsAnEpisode()` |  |

### 9 - NO DECIMAL COMMAS, EVER (lines 287-323)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 29 | `private static void theFileNeverWritesADecimalComma()` |  |

### 10 - THE FILES LAND SOMEWHERE THE PLAYER CAN FIND THEM (lines 324-336)

| line | len | member | says |
|---:|---:|---|---|
| 327 | 9 | `private static void theExportPathsAreBesideTheSaves()` |  |

### FIXTURES AND READING (lines 337-340)

### 11 - THE UNEMPLOYMENT COLUMN IS THE POOL OVER THE LABOUR FORCE (lines 341-377)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 24 | `private static void theUnemploymentColumnIsThePool()` |  |

### 13 - THE BOOK AGREES WITH THE MODEL, ON A CITY THAT HAS BEEN PLAYED (lines 378-518)

| line | len | member | says |
|---:|---:|---|---|
| 403 | 83 | `private static void theBookAgreesWithTheModelOnAPlayedCity()` |  |
| 488 | 6 | `private static void put(Game g, String name, int n)` | One template, n of them, paid for and standing - EducationCheck's shape. |
| 495 | 7 | `private static void near(String what, double got, double wanted, double slack)` |  |
| 504 | 14 | `private static HistorySave history(int workforce, int students, int jobs, int outOfWork)` | Twelve identical months of workforce, students, posts and (if not negative) the pool. |

### 12 - THE CURRENCY NOTE READS THE SAME WAY AS THE RATE (lines 519-545)

| line | len | member | says |
|---:|---:|---|---|
| 533 | 12 | `private static void theCurrencyNoteReadsTheRightWay()` |  |

### 14 - THE NAMED EPISODES ARE THE ONES THE FIXTURE CAUSED, AND ONLY THOSE (lines 546-750)

| line | len | member | says |
|---:|---:|---|---|
| 569 | 86 | `private static void theEpisodesAreTheOnesTheFixtureCaused()` |  |
| 661 | 5 | `private static double[] steppedOutput(int months, int at, int loss)` | Output growing by one a month from 100, that loses `loss` a month for good from index `at` on. |
| 667 | 5 | `private static double[] flat(int months, double value)` |  |
| 674 | 14 | `private static HistorySave built(int months, String[] series, double[]...values)` | A history of `months` months with several series filled in, each as long as the axis. |
| 696 | 10 | `private static HistorySave built(int months, String series, double[] values)` | A history of `months` months with one series filled in. |
| 707 | 5 | `private static double[] ramp(int n)` |  |
| 713 | 5 | `private static double sum(int from, int to)` |  |
| 720 | 4 | `private static double cell(String text, String column, int row)` | The value a reader would take out of the file, found the way a reader finds it. |
| 725 | 3 | `private static boolean blank(String text, String column, int row)` |  |
| 729 | 21 | `private static String rawCell(String text, String column, int row)` |  |

### assertions (lines 751-784)

| line | len | member | says |
|---:|---:|---|---|
| 753 | 7 | `private static void same(String what, double got, double wanted)` |  |
| 761 | 7 | `private static void same(String what, String got, String wanted)` |  |
| 769 | 7 | `private static void same(String what, boolean got, boolean wanted)` |  |
| 777 | 7 | `private static void yes(String what, boolean got)` |  |

