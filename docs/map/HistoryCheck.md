# HistoryCheck.java - 301 lines · 6 methods · 2 constants · harnesses

`ham/citybuildersim/HistoryCheck.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (8), [HistorySave](HistorySave.md) (6), [GameFiles](GameFiles.md) (2), [Equity](Equity.md) (2)

## Sections

| line | section |
|---:|---|
| 66 | · 1. every series is recorded, every month |
| 127 | · 2. it survives a save and a reload |
| 165 | · 3. A SHORT SERIES LINES UP WITH THE END |
| 243 | · 4. the derived series do not divide by zero |
| 267 | · 5. a new game forgets it |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 38 | `HistoryCheck.OUT` | `System.out` | The game narrates every month to stdout; the findings are the output here. |
| 39 | `HistoryCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } })` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 35 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 33 | 269 | **type** `public class HistoryCheck` | Verifies the graph history: recording, alignment, and the round trip. |
| 44 | 4 | `static void quietly(Runnable work)` | Runs a stretch of months without the monthly report burying the results. |
| 49 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 54 | 6 | `static void close(String label, double a, double b)` |  |
| 61 | 221 | `public static void main(String[] args) throws Exception` |  |
| 284 | 9 | `static String dropSeries(String json, String name)` | Removes one "name": [ ... |
| 294 | 7 | `static void cleanUp(Path root)` |  |

