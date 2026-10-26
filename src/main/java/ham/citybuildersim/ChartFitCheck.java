package ham.citybuildersim;

import ham.citybuildersim.ui.ChartFitBench;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The full-screen chart's foot is on the screen (FW10): with the legend wrapped to two rows or three,
 * the chart still ends inside the window and the plot takes the room that is left.
 *
 * WHY THIS EXISTS. Full screen gives the City History chart the whole window and sizes its plot to
 * the window's height less what stands beside the plot (TimeChart.heightBesidePlot()). That was a sum
 * of constants that counted the toolbar as one row; with many lines picked the legend, a flow pane in
 * the toolbar, wraps to two rows and more, and the chart came out a row taller than the window gave it:
 * the overview strip and the caption line were cut off the bottom of the screen (found in 0.7.40 and
 * 0.7.41, open until FW10). Nothing in the model is involved, so no city is played: the chart is laid
 * out on a scene of its own at four window sizes (ChartFitBench, through the screen's own fitToWindow)
 * and the harness reads where its foot lands. Run alone with -Dchartfit.out=DIR it also writes the
 * pictures.
 *
 * What it has to prove:
 *
 *   1. THE FIXTURE CAUSES THE CONDITION. At every window size two lines lay out one legend row, and
 *      some count of lines lays out two rows, and three at some size (a size where sixteen lines never
 *      wrap to three is reported as a note) - otherwise the checks below prove nothing about a
 *      wrapped legend.
 *   2. THE FOOT STANDS INSIDE THE SCREEN. At every size, at two lines, at the counts that wrap to
 *      two rows and to three, and at the most lines tried, the caption's bottom and the chart's
 *      bottom are inside the window.
 *   3. AND THE PLOT TAKES THE ROOM. The chart stops where the screen means it to, not short of it;
 *      and the plot loses to a wrapped legend exactly what the toolbar grew.
 *   4. THE RENDERS. With -Dchartfit.out=DIR, a picture of each size at two lines and at the counts
 *      that wrap to two rows and to three, for a look by a person; none otherwise.
 */
public class ChartFitCheck {

    /** The window sizes the checks run at: a laptop, a desk monitor, full HD, and a big one. */
    static final double[][] SIZES = {{1280, 720}, {1600, 900}, {1920, 1080}, {2560, 1440}};

    /** The most lines tried when looking for the counts that wrap the legend. */
    static final int MOST_LINES = 16;

    private static int fails;

    static void check(String label, boolean ok, String detail) {
        System.out.println((ok ? "  ok   " : "  FAIL ") + label + (detail == null || detail.isEmpty() ? "" : " - " + detail));
        if (!ok) fails++;
    }

    static String size(double[] s) { return (int) s[0] + "x" + (int) s[1]; }

    static String px(double v) { return String.format("%.1f", v); }

    public static void main(String[] args) throws Exception {
        System.out.println("=== ChartFitCheck: the full-screen chart's foot is on the screen (FW10) ===");
        String down = KeptCheck.startJavaFx();
        if (down != null) {
            System.out.println("skipped: the JavaFX toolkit cannot start here (" + down + "); the chart needs it to be laid out");
            System.exit(0);
        }
        String outDir = System.getProperty("chartfit.out");

        // Every size's layout at every count, tried once: what the sections below read.
        Map<String, ChartFitBench.Fit[]> fits = new LinkedHashMap<>();
        for (double[] s : SIZES) {
            ChartFitBench.Fit[] row = new ChartFitBench.Fit[MOST_LINES + 1];
            for (int n = 2; n <= MOST_LINES; n++) {
                final int count = n;
                row[n] = KeptCheck.fx(() -> ChartFitBench.fullScreen(s[0], s[1], count, null));
            }
            fits.put(size(s), row);
        }

        System.out.println("--- the fixture causes the condition: the legend wraps ---");
        Map<String, int[]> picked = new LinkedHashMap<>();
        int reachedThree = 0;
        for (double[] s : SIZES) {
            ChartFitBench.Fit[] row = fits.get(size(s));
            int twoRows = 0, threeRows = 0;
            for (int n = 2; n <= MOST_LINES; n++) {
                if (twoRows == 0 && row[n].legendRows() == 2) twoRows = n;
                if (threeRows == 0 && row[n].legendRows() == 3) threeRows = n;
            }
            check(size(s) + ": 2 lines lay out one legend row", row[2].legendRows() == 1, "rows " + row[2].legendRows());
            check(size(s) + ": some count of lines lays out two rows", twoRows > 0,
                    twoRows > 0 ? twoRows + " lines" : "no count up to " + MOST_LINES + " wraps to two rows");
            if (threeRows > 0) {
                reachedThree++;
                System.out.println("  note " + size(s) + ": " + twoRows + " lines wrap to two rows, " + threeRows + " to three; "
                        + MOST_LINES + " lines lay out " + row[MOST_LINES].legendRows());
            } else {
                // A window wide enough that sixteen lines never need a third row is reported, not dropped silently.
                System.out.println("  note " + size(s) + ": no count up to " + MOST_LINES + " lines wraps to three rows ("
                        + twoRows + " lines wrap to two; " + MOST_LINES + " lay out " + row[MOST_LINES].legendRows() + ")");
            }
            // The counts the sections below run at: two lines, the first to wrap to two rows and to three, and the most tried.
            java.util.TreeSet<Integer> counts = new java.util.TreeSet<>(java.util.List.of(2, MOST_LINES));
            if (twoRows > 0) counts.add(twoRows);
            if (threeRows > 0) counts.add(threeRows);
            int[] list = new int[counts.size()];
            int k = 0;
            for (int n : counts) list[k++] = n;
            picked.put(size(s), list);
        }
        check("three rows are reached at some window size", reachedThree > 0, reachedThree + " of " + SIZES.length + " sizes");

        System.out.println("--- the foot stands inside the screen ---");
        for (double[] s : SIZES) {
            for (int n : picked.get(size(s))) {
                ChartFitBench.Fit f = fits.get(size(s))[n];
                String at = size(s) + ", " + n + " lines, " + f.legendRows() + " legend rows";
                check(at + ": the caption's bottom is inside the window", f.captionBottom() <= f.windowH(),
                        "caption bottom " + px(f.captionBottom()) + " against window " + px(f.windowH()));
                check(at + ": the chart's bottom is inside the window", f.chartBottom() <= f.windowH(),
                        "chart bottom " + px(f.chartBottom()) + " against window " + px(f.windowH()));
            }
        }

        System.out.println("--- and the plot takes the room ---");
        for (double[] s : SIZES) {
            ChartFitBench.Fit two = fits.get(size(s))[2];
            for (int n : picked.get(size(s))) {
                ChartFitBench.Fit f = fits.get(size(s))[n];
                String at = size(s) + ", " + n + " lines, " + f.legendRows() + " legend rows";
                double left = f.windowH() - f.chartBottom();
                check(at + ": the chart stops where the screen means it to, not short of it",
                        left <= f.designGap() + 1,
                        "room left below the chart " + px(left) + ", the design leaves " + px(f.designGap()));
                double plotLost = two.plotHeight() - f.plotHeight(), toolbarGrew = f.toolbarHeight() - two.toolbarHeight();
                check(at + ": the plot loses to a wrapped legend what the toolbar grew",
                        Math.abs(plotLost - toolbarGrew) <= 1,
                        "plot lost " + px(plotLost) + ", toolbar grew " + px(toolbarGrew));
            }
        }

        System.out.println("--- the renders ---");
        if (outDir == null) {
            System.out.println("  (no -Dchartfit.out=DIR: none written)");
        } else {
            for (double[] s : SIZES) {
                for (int n : picked.get(size(s))) {
                    final int count = n;
                    ChartFitBench.Fit f = fits.get(size(s))[n];
                    Path to = Path.of(outDir, size(s) + "-" + n + "lines-" + f.legendRows() + "rows.png");
                    KeptCheck.fx(() -> ChartFitBench.fullScreen(s[0], s[1], count, to));
                    check(size(s) + ", " + n + " lines: the picture is written", java.nio.file.Files.size(to) > 0, to.toString());
                }
            }
        }

        System.out.println(fails == 0 ? "ChartFitCheck: all ok" : "ChartFitCheck: " + fails + " FAILED");
        System.exit(fails);
    }
}
