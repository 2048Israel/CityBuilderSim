# HistoryCheck.java - 1,058 lines · 19 methods · 3 constants · harnesses

`ham/citybuildersim/HistoryCheck.java` - generated 2026-11-16 by CodeMap; line numbers are as of that run.

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

**Uses:** [HistorySave](HistorySave.md) (54), [YearBook](YearBook.md) (24), [Game](Game.md) (16), [GameFiles](GameFiles.md) (7), [Equity](Equity.md) (6), [MoneyAudit](MoneyAudit.md) (5), [Bank](Bank.md) (4), [EducationCheck](EducationCheck.md) (4), [Exchange](Exchange.md) (4), [Household](Household.md) (2), [OrderBook](OrderBook.md) (2), [ChartModel](ChartModel.md) (2), [Currency](Currency.md) (2), [SavedCities](SavedCities.md) (2), [TempDirs](TempDirs.md) (1), [SectorBooks](SectorBooks.md) (1), [Sector](Sector.md) (1), [NationalAccounts](NationalAccounts.md) (1), [PriceLevel](PriceLevel.md) (1)

## Sections

| line | section |
|---:|---|
| 82 | · 1. every series is recorded, every month |
| 143 | · 2. it survives a save and a reload |
| 181 | · 2b. every sector's two series (0.7.4) |
| 226 | · 2c. GDP's four parts (0.7.6) |
| 279 | · 2d. the dial and the bank's three prices (0.7.7) |
| 316 | · 2e. the bank's capital (0.7.8) |
| 374 | · 2f. the month's graduates (A6, 0.7.46) |
| 403 | · 2g. a consolidated company's price (C5, 0.7.48) |
| 442 | · 3. A SHORT SERIES LINES UP WITH THE END |
| 520 | · 3a. a year of a flow (B7, 0.7.47) |
| 549 | · 3b. a 0.7.6 history still loads (0.7.7) |
| 619 | · 4. the derived series do not divide by zero |
| 643 | · 5. a new game forgets it |
| 663 | 6. past five hundred years, a year to a point (0.7.55) |
| 863 | 7. the eight prices write at today's prices (CU2, REV-CU2-1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `HistoryCheck.OUT` | `System.out` | The game narrates every month to stdout; the findings are the output here. |
| 55 | `HistoryCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |
| 876 | `HistoryCheck.PRICES` | `{ "minimumWage", "landPrice", "foodPrice", "materialsPrice", "orePrice", "ren...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 51 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 1010 | **type** `public class HistoryCheck` | Verifies the graph history: recording, alignment, and the round trip. |
| 60 | 4 | `static void quietly(Runnable work)` | Runs a stretch of months without the monthly report burying the results. |
| 65 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 70 | 6 | `static void close(String label, double a, double b)` |  |
| 77 | 585 | `public static void main(String[] args) throws Exception` |  |

### 6. past five hundred years, a year to a point (0.7.55) (lines 663-862)

| line | len | member | says |
|---:|---:|---|---|
| 675 | 128 | `static void folds(Path root) throws Exception` |  |
| 805 | 7 | `static double value(String name, int month)` | The value grown for a series in a month: a flow and a level whole numbers, a rate a quarter. |
| 819 | 16 | `static double[] expected(String name, List<Double> in, int from, int years)` | What a series must hold after the fold, worked out here and not by the fold: its months from `from`, the first `years` calendar years folded by its rule - a flow added oldest first, a rate's months added and divided, ... |
| 837 | 5 | `static String bookCell(double v)` | The year book's cell for a figure, as the book prints it: its own compact(), through a one-row history. |
| 844 | 7 | `static HistorySave withSeries(HistorySave h, String map, String key)` | A history with one more company's series, empty, so it begins with the next month appended. |
| 853 | 9 | `static String dropSeries(String json, String name)` | Removes one "name": [ ... |

### 7. the eight prices write at today's prices (CU2, REV-CU2-1) (lines 863-1058)

| line | len | member | says |
|---:|---:|---|---|
| 879 | 8 | `static Map<String, List<? extends Number>> eight(HistorySave h)` | The eight prices of a history, by the name seriesByName() gives them: the six, and each company's share price and value. |
| 889 | 6 | `static JsonElement inFile(JsonObject file, String name)` | One of the eight in the written file: the field by its name, or a company's series inside its share field. |
| 897 | 7 | `static int figures(String number)` | The significant digits a number is written with: its mantissa's, from the first to the last that is not a zero. |
| 906 | 12 | `static boolean sameBits(List<? extends Number> a, List<? extends Number> b)` | Two series the same length, with a null where the other has one and every other entry the same double to the bit. |
| 920 | 8 | `static int sameEight(HistorySave a, HistorySave b)` | How many of the eight in `a` are, to the bit, what `b` has under the same name. |
| 930 | 6 | `static int plainBytes(HistorySave h)` | The text the eight take when each is written by Gson as the plain array of its doubles, the form before CU2. |
| 938 | 6 | `static int writtenBytes(HistorySave h, JsonObject file)` | The text the eight take in the file as written: each one's element, as Gson writes it. |
| 945 | 105 | `static void todaysPrices(Path root, GameFiles files, Game city) throws Exception` |  |
| 1051 | 7 | `static void cleanUp(Path root)` |  |

