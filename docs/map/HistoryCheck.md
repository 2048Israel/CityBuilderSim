# HistoryCheck.java - 842 lines · 11 methods · 2 constants · harnesses

`ham/citybuildersim/HistoryCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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

**Uses:** [HistorySave](HistorySave.md) (31), [YearBook](YearBook.md) (24), [Game](Game.md) (13), [Equity](Equity.md) (6), [MoneyAudit](MoneyAudit.md) (5), [GameFiles](GameFiles.md) (4), [Bank](Bank.md) (4), [EducationCheck](EducationCheck.md) (4), [Exchange](Exchange.md) (4), [Household](Household.md) (2), [OrderBook](OrderBook.md) (2), [ChartModel](ChartModel.md) (2), [Currency](Currency.md) (2), [SectorBooks](SectorBooks.md) (1), [Sector](Sector.md) (1), [NationalAccounts](NationalAccounts.md) (1)

## Sections

| line | section |
|---:|---|
| 71 | · 1. every series is recorded, every month |
| 132 | · 2. it survives a save and a reload |
| 170 | · 2b. every sector's two series (0.7.4) |
| 215 | · 2c. GDP's four parts (0.7.6) |
| 268 | · 2d. the dial and the bank's three prices (0.7.7) |
| 305 | · 2e. the bank's capital (0.7.8) |
| 363 | · 2f. the month's graduates (A6, 0.7.46) |
| 392 | · 2g. a consolidated company's price (C5, 0.7.48) |
| 431 | · 3. A SHORT SERIES LINES UP WITH THE END |
| 509 | · 3a. a year of a flow (B7, 0.7.47) |
| 538 | · 3b. a 0.7.6 history still loads (0.7.7) |
| 593 | · 4. the derived series do not divide by zero |
| 617 | · 5. a new game forgets it |
| 635 | 6. past five hundred years, a year to a point (0.7.55) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 43 | `HistoryCheck.OUT` | `System.out` | The game narrates every month to stdout; the findings are the output here. |
| 44 | `HistoryCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 38 | 805 | **type** `public class HistoryCheck` | Verifies the graph history: recording, alignment, and the round trip. |
| 49 | 4 | `static void quietly(Runnable work)` | Runs a stretch of months without the monthly report burying the results. |
| 54 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 59 | 6 | `static void close(String label, double a, double b)` |  |
| 66 | 568 | `public static void main(String[] args) throws Exception` |  |

### 6. past five hundred years, a year to a point (0.7.55) (lines 635-842)

| line | len | member | says |
|---:|---:|---|---|
| 647 | 128 | `static void folds(Path root) throws Exception` |  |
| 777 | 7 | `static double value(String name, int month)` | The value grown for a series in a month: a flow and a level whole numbers, a rate a quarter. |
| 791 | 16 | `static double[] expected(String name, List<Double> in, int from, int years)` | What a series must hold after the fold, worked out here and not by the fold: its months from `from`, the first `years` calendar years folded by its rule - a flow added oldest first, a rate's months added and divided, ... |
| 809 | 5 | `static String bookCell(double v)` | The year book's cell for a figure, as the book prints it: its own compact(), through a one-row history. |
| 816 | 7 | `static HistorySave withSeries(HistorySave h, String map, String key)` | A history with one more company's series, empty, so it begins with the next month appended. |
| 825 | 9 | `static String dropSeries(String json, String name)` | Removes one "name": [ ... |
| 835 | 7 | `static void cleanUp(Path root)` |  |

