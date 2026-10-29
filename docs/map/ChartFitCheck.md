# ChartFitCheck.java - 156 lines · 4 methods · 2 constants · harnesses

`ham/citybuildersim/ChartFitCheck.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> The full-screen chart's foot is on the screen (FW10): with the legend wrapped to two rows or three,
> the chart still ends inside the window and the plot takes the room that is left.
> 
> WHY THIS EXISTS. Full screen gives the City History chart the whole window and sizes its plot to
> the window's height less what stands beside the plot (TimeChart.heightBesidePlot()). That was a sum
> of constants that counted the toolbar as one row; with many lines picked the legend, a flow pane in
> the toolbar, wraps to two rows and more, and the chart came out a row taller than the window gave it:
> the overview strip and the caption line were cut off the bottom of the screen (found in 0.7.40 and
> 0.7.41, open until FW10). Nothing in the model is involved, so no city is played: the chart is laid
> out on a scene of its own at four window sizes (ChartFitBench, through the screen's own fitToWindow)
> and the harness reads where its foot lands. Run alone with -Dchartfit.out=DIR it also writes the
> pictures.
> 
> What it has to prove:
> 
>   1. THE FIXTURE CAUSES THE CONDITION. At every window size two lines lay out one legend row, and
>      some count of lines lays out two rows, and three at some size (a size where sixteen lines never
>      wrap to three is reported as a note) - otherwise the checks below prove nothing about a
>      wrapped legend.
>   2. THE FOOT STANDS INSIDE THE SCREEN. At every size, at two lines, at the counts that wrap to
>      two rows and to three, and at the most lines tried, the caption's bottom and the chart's
>      bottom are inside the window.
>   3. AND THE PLOT TAKES THE ROOM. The chart stops where the screen means it to, not short of it;
>      and the plot loses to a wrapped legend exactly what the toolbar grew.
>   4. THE RENDERS. With -Dchartfit.out=DIR, a picture of each size at two lines and at the counts
>      that wrap to two rows and to three, for a look by a person; none otherwise.

**Uses:** [ChartFitBench](ChartFitBench.md) (10), [KeptCheck](KeptCheck.md) (3)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 40 | `ChartFitCheck.SIZES` | `{ { 1280, 720 }, { 1600, 900 }, { 1920, 1080 }, { 2560, 1440 } }` | The window sizes the checks run at: a laptop, a desk monitor, full HD, and a big one. |
| 43 | `ChartFitCheck.MOST_LINES` | `16` | The most lines tried when looking for the counts that wrap the legend. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `private static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 37 | 120 | **type** `public class ChartFitCheck` | The full-screen chart's foot is on the screen (FW10): with the legend wrapped to two rows or three, the chart still ends inside the window and the plot takes the room that is left. |
| 47 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 52 | 1 | `static String size(double[] s)` |  |
| 54 | 1 | `static String px(double v)` |  |
| 56 | 100 | `public static void main(String[] args) throws Exception` |  |

