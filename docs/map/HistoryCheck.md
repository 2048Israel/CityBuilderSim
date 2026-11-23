# HistoryCheck.java - 1,243 lines · 21 methods · 3 constants · harnesses

`ham/citybuildersim/HistoryCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> Verifies the graph history: recording, alignment, and the round trip. Not
> part of the game.
> 
> WHY THIS EXISTS
> 
> The history went from eight series to twenty-three, and every one of the
> three ways that can go wrong is silent.
> 
> 1. A SERIES THAT IS NEVER RECORDED looks exactly like one that is, until you
>    open the graph and it is empty - which is a long way from where the
>    mistake was made.
> 2. A SERIES MISSING FROM restoreFrom() records perfectly all session and
>    vanishes on reload. The live game and the reloaded game disagree and
>    nothing says so.
> 3. A SHORT SERIES - one added after a city was already being played - lines
>    up with the END of the month axis and not the start. Draw it from the left
>    instead and last decade's sickness appears in the founding years, plotted
>    confidently against the wrong months.
> 
> The third is the interesting one, because it is the only bug here that
> produces a graph that looks completely fine.
> 
> And since 0.7.55, a fourth (section 6): past HistorySave.MONTHLY_KEPT months
> the oldest years fold a year to an entry, each series by its rule, and
> every reader has to read a folded year as a year - a fold that summed a
> level, or a chart that drew a year's flow as one month's, looks fine too.
> 
> And since CU2 (REV-CU2-1), a fifth (section 7): the eight prices roundSig
> records are written at today's prices to six figures and read back times
> FACTOR, and a reload that moved one bit of one of them would move the bank's
> volatility, and every trace played from the file, without a word.
> 
> FS17: sections 1, 2, 2b and 2c are identities over whatever was played and stay so. What sections 2d, 2e and 2f read off a played city
> is caused now: the bank's fees, loss record and sheet on one fixture bank (a founding with a loan written to Retail, its capital record
> put by hand to no bad year and then to the worst one, its fee taken as the settle takes it - the "lent" city, scratch files), and the
> schools' diplomas at the month they first flow, inside a bound that fails as a fixture.

**Uses:** [HistorySave](HistorySave.md) (58), [YearBook](YearBook.md) (24), [Game](Game.md) (21), [Bank](Bank.md) (12), [PowerView](PowerView.md) (11), [GameFiles](GameFiles.md) (8), [Equity](Equity.md) (6), [MoneyAudit](MoneyAudit.md) (6), [BusinessDebtManager](BusinessDebtManager.md) (4), [EducationCheck](EducationCheck.md) (4), [Exchange](Exchange.md) (4), [Household](Household.md) (2), [OrderBook](OrderBook.md) (2), [PowerPriceCheck](PowerPriceCheck.md) (2), [ChartModel](ChartModel.md) (2), [Currency](Currency.md) (2), [SavedCities](SavedCities.md) (2), [TempDirs](TempDirs.md) (1), [SectorBooks](SectorBooks.md) (1), [Sector](Sector.md) (1), [NationalAccounts](NationalAccounts.md) (1), [Good](Good.md) (1), [PriceLevel](PriceLevel.md) (1)

## Sections

| line | section |
|---:|---|
| 94 | · 1. every series is recorded, every month |
| 163 | · 2. it survives a save and a reload |
| 201 | · 2b. every sector's two series (0.7.4) |
| 246 | · 2c. GDP's four parts (0.7.6) |
| 299 | · 2d. the dial and the bank's three prices (0.7.7) |
| 360 | · 2e. the bank's capital (0.7.8) |
| 432 | · 2f. the month's graduates (A6, 0.7.46) |
| 478 | · 2g. a consolidated company's price (C5, 0.7.48) |
| 517 | · 3. A SHORT SERIES LINES UP WITH THE END |
| 595 | · 3a. a year of a flow (B7, 0.7.47) |
| 624 | · 3b. a 0.7.6 history still loads (0.7.7) |
| 694 | · 4. the derived series do not divide by zero |
| 718 | · 5. a new game forgets it |
| 848 | 6. past five hundred years, a year to a point (0.7.55) |
| 1048 | 7. the eight prices write at today's prices (CU2, REV-CU2-1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `HistoryCheck.OUT` | `System.out` | The game narrates every month to stdout; the findings are the output here. |
| 60 | `HistoryCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |
| 1061 | `HistoryCheck.PRICES` | `{ "minimumWage", "landPrice", "foodPrice", "materialsPrice", "orePrice", "ren...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 1190 | **type** `public class HistoryCheck` | Verifies the graph history: recording, alignment, and the round trip. |
| 65 | 4 | `static void quietly(Runnable work)` | Runs a stretch of months without the monthly report burying the results. |
| 70 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 75 | 6 | `static void close(String label, double a, double b)` |  |
| 82 | 657 | `public static void main(String[] args) throws Exception` |  |
| 741 | 25 | `static void powerSuffix(HistorySave h, String key, double latest)` | Monthly optional observations occupy an actual suffix, with unknown earlier months, never invented zeroes. |
| 768 | 79 | `static void firstPowerObservation() throws Exception` | Four fixed native months expose the first observation; no synthetic history, aging, or search for a passing month. |

### 6. past five hundred years, a year to a point (0.7.55) (lines 848-1047)

| line | len | member | says |
|---:|---:|---|---|
| 860 | 128 | `static void folds(Path root) throws Exception` |  |
| 990 | 7 | `static double value(String name, int month)` | The value grown for a series in a month: a flow and a level whole numbers, a rate a quarter. |
| 1004 | 16 | `static double[] expected(String name, List<Double> in, int from, int years)` | What a series must hold after the fold, worked out here and not by the fold: its months from `from`, the first `years` calendar years folded by its rule - a flow added oldest first, a rate's months added and divided, ... |
| 1022 | 5 | `static String bookCell(double v)` | The year book's cell for a figure, as the book prints it: its own compact(), through a one-row history. |
| 1029 | 7 | `static HistorySave withSeries(HistorySave h, String map, String key)` | A history with one more company's series, empty, so it begins with the next month appended. |
| 1038 | 9 | `static String dropSeries(String json, String name)` | Removes one "name": [ ... |

### 7. the eight prices write at today's prices (CU2, REV-CU2-1) (lines 1048-1243)

| line | len | member | says |
|---:|---:|---|---|
| 1064 | 8 | `static Map<String, List<? extends Number>> eight(HistorySave h)` | The eight prices of a history, by the name seriesByName() gives them: the six, and each company's share price and value. |
| 1074 | 6 | `static JsonElement inFile(JsonObject file, String name)` | One of the eight in the written file: the field by its name, or a company's series inside its share field. |
| 1082 | 7 | `static int figures(String number)` | The significant digits a number is written with: its mantissa's, from the first to the last that is not a zero. |
| 1091 | 12 | `static boolean sameBits(List<? extends Number> a, List<? extends Number> b)` | Two series the same length, with a null where the other has one and every other entry the same double to the bit. |
| 1105 | 8 | `static int sameEight(HistorySave a, HistorySave b)` | How many of the eight in `a` are, to the bit, what `b` has under the same name. |
| 1115 | 6 | `static int plainBytes(HistorySave h)` | The text the eight take when each is written by Gson as the plain array of its doubles, the form before CU2. |
| 1123 | 6 | `static int writtenBytes(HistorySave h, JsonObject file)` | The text the eight take in the file as written: each one's element, as Gson writes it. |
| 1130 | 105 | `static void todaysPrices(Path root, GameFiles files, Game city) throws Exception` |  |
| 1236 | 7 | `static void cleanUp(Path root)` |  |

