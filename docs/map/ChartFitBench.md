# ChartFitBench.java - 130 lines · 3 methods · 3 constants · interface

`ham/citybuildersim/ui/ChartFitBench.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> The full-screen chart on a scene of its own - no stage, no window - laid out at a window's size
> with some lines picked, for ChartFitCheck (FW10): the harness's hands on the chart, since what it
> reaches is the ui package's own. Public so the harness in ham.citybuildersim can call it.
> 
> <p>WHY. The full-screen chart is sized to the window by what stands beside its plot, and that
> was a sum of constants that counted the toolbar as one row; with many lines picked the legend
> wraps to two or three rows and the chart was a row or two taller than the window gave it, its
> foot (the overview strip and the caption line) cut off at the bottom. Nobody could see that
> without a window of every size and a city with every count of lines picked; a Scene with no
> stage takes CSS, layout and a snapshot all the same, so the bench builds the chart as
> HistoryScreen.setChartFull() does - through the screen's own newFullPane(), newFullTitle(),
> newFullClock() and fitToWindow(), not a copy of them - and reads where the foot lands.
> The FX toolkit must be up and every call made on its thread; ChartFitCheck sees to both.

**Uses:** [HistoryScreen](HistoryScreen.md) (9), [Palette](Palette.md) (6), [TimeChart](TimeChart.md) (4), [ChartModel](ChartModel.md) (1), [UserInterface](UserInterface.md) (1)

**Used by (1):** [ChartFitCheck](ChartFitCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 48 | `ChartFitBench.LABELS` | `{ "Unemployment rate", "Median household income", "Consumer price index", "Ba...` | The line names City History offers, as long as the real ones; the first lineCount of these are picked. |
| 56 | `ChartFitBench.AREAS` | `{ Palette.MONEY, Palette.PEOPLE, Palette.BUSINESS, Palette.BUILDING }` | The four areas' colours in turn, which Palette.lineColours() spreads over the lines as City History does. |
| 59 | `ChartFitBench.MONTHS` | `240` | The months in the history the chart is handed. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 34 | 97 | **type** `public final class ChartFitBench` | The full-screen chart on a scene of its own - no stage, no window - laid out at a window's size with some lines picked, for ChartFitCheck (FW10): the harness's hands on the chart, since what it reaches is the ui packa... |
| 36 | 1 | `private ChartFitBench()` |  |
| 44 | 2 | **type** `public record Fit(double windowW, double windowH, int lines, int legendRows, double chartBottom, double cap...` | One window's layout, read after the screen's fit and its refit: the window, how many lines were picked, how many rows the legend wrapped to, where the chart and its caption end in the scene, the plot's height the char... |
| 66 | 52 | `public static Fit fullScreen(double windowW, double windowH, int lineCount, Path png) throws IOException` | The full-screen chart in a window of this size with lineCount lines picked, laid out as the screen lays it (fit, layout, fit again, layout), and where its foot is. |
| 120 | 10 | `private static void write(Node root, Path to) throws IOException` | The scene's picture as a PNG, its folder made. |

