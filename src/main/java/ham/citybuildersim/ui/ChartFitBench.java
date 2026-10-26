package ham.citybuildersim.ui;

import ham.citybuildersim.ChartModel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * The full-screen chart on a scene of its own - no stage, no window - laid out at a window's size
 * with some lines picked, for ChartFitCheck (FW10): the harness's hands on the chart, since what it
 * reaches is the ui package's own. Public so the harness in ham.citybuildersim can call it.
 *
 * <p>WHY. The full-screen chart is sized to the window by what stands beside its plot, and that
 * was a sum of constants that counted the toolbar as one row; with many lines picked the legend
 * wraps to two or three rows and the chart was a row or two taller than the window gave it, its
 * foot (the overview strip and the caption line) cut off at the bottom. Nobody could see that
 * without a window of every size and a city with every count of lines picked; a Scene with no
 * stage takes CSS, layout and a snapshot all the same, so the bench builds the chart as
 * HistoryScreen.setChartFull() does - through the screen's own newFullPane(), newFullTitle(),
 * newFullClock() and fitToWindow(), not a copy of them - and reads where the foot lands.
 * The FX toolkit must be up and every call made on its thread; ChartFitCheck sees to both.
 */
public final class ChartFitBench {

    private ChartFitBench() { }

    /**
     * One window's layout, read after the screen's fit and its refit: the window, how many lines were
     * picked, how many rows the legend wrapped to, where the chart and its caption end in the scene,
     * the plot's height the chart was given, the toolbar's height as laid out, and the room below the
     * chart the screen means to leave (the pane's bottom padding and the canvas's SLACK).
     */
    public record Fit(double windowW, double windowH, int lines, int legendRows, double chartBottom,
                      double captionBottom, double plotHeight, double toolbarHeight, double designGap) { }

    /** The line names City History offers, as long as the real ones; the first lineCount of these are picked. */
    private static final String[] LABELS = {
        "Unemployment rate", "Median household income", "Consumer price index", "Bank lending rate",
        "Government debt per head", "Housing rent index", "Business profit margin", "Retail sales volume",
        "Savings rate of households", "Land price index", "Wage bill of the city", "Population growth rate",
        "Tax revenue per month", "Trade balance", "Average firm size", "Construction output"
    };

    /** The four areas' colours in turn, which Palette.lineColours() spreads over the lines as City History does. */
    private static final String[] AREAS = {Palette.MONEY, Palette.PEOPLE, Palette.BUSINESS, Palette.BUILDING};

    /** The months in the history the chart is handed. */
    public static final int MONTHS = 240;

    /**
     * The full-screen chart in a window of this size with lineCount lines picked, laid out as the screen
     * lays it (fit, layout, fit again, layout), and where its foot is. With png not null the window's
     * picture is written there. Call it on the FX thread.
     */
    public static Fit fullScreen(double windowW, double windowH, int lineCount, Path png) throws IOException {
        Palette.Fonts.load();
        TimeChart chart = new TimeChart(new ChartModel(), new HashSet<>(), true);

        List<Integer> months = new ArrayList<>();
        for (int m = 0; m < MONTHS; m++) months.add(m);
        boolean squashed = lineCount > 2;
        List<String> areas = new ArrayList<>();
        for (int k = 0; k < lineCount; k++) areas.add(AREAS[k % AREAS.length]);
        String[] colours = Palette.lineColours(areas);
        List<TimeChart.Line> lines = new ArrayList<>();
        for (int k = 0; k < lineCount; k++) {
            double[] values = new double[MONTHS];
            for (int m = 0; m < MONTHS; m++) values[m] = 100 + 20 * Math.sin(m / (9.0 + k) + k);
            int side = squashed ? 0 : k;
            String unitWords = squashed ? "low to high" : (side == 0 ? "left, per cent" : "right, index, founding = 1");
            lines.add(new TimeChart.Line("line" + k, LABELS[k % LABELS.length], colours[k], side, values,
                    v -> v, v -> String.format("%.1f", v), unitWords));
        }
        chart.setData(months, lines,
                lineCount == 0 || squashed ? null : HistoryScreen.axisFor("percent"),
                lineCount == 2 ? HistoryScreen.axisFor("index") : null,
                squashed, false, null, List.of(), List.of(), List.of(), "");

        Label clock = HistoryScreen.newFullClock();
        clock.setText("March 2031 · running at 20x");
        chart.lead().getChildren().setAll(HistoryScreen.newFullTitle(), clock);
        chart.setFull(true);
        VBox pane = HistoryScreen.newFullPane();
        pane.getChildren().setAll(chart);
        StackPane root = new StackPane(pane);
        Scene scene = new Scene(root, windowW, windowH);
        new UserInterface().applyTheme(scene);

        // The screen fits on the first pass and refits on the pane's size listeners and every month.
        HistoryScreen.fitToWindow(chart, windowW, windowH);
        root.applyCss();
        root.layout();
        HistoryScreen.fitToWindow(chart, windowW, windowH);
        root.applyCss();
        root.layout();

        Node toolbar = chart.getChildren().get(0);
        Node caption = chart.getChildren().get(chart.getChildren().size() - 1);
        Fit fit = new Fit(windowW, windowH, lineCount, chart.legendRows(),
                chart.localToScene(chart.getLayoutBounds()).getMaxY(),
                caption.localToScene(caption.getLayoutBounds()).getMaxY(),
                chart.plotHeight(), toolbar.getLayoutBounds().getHeight(),
                HistoryScreen.FULL_PAD_BOTTOM + HistoryScreen.SLACK);
        if (png != null) write(root, png);
        return fit;
    }

    /** The scene's picture as a PNG, its folder made. */
    private static void write(Node root, Path to) throws IOException {
        WritableImage img = root.snapshot(null, null);
        int w = (int) img.getWidth(), h = (int) img.getHeight();
        int[] px = new int[w * h];
        img.getPixelReader().getPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), px, 0, w);
        java.awt.image.BufferedImage out = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        out.setRGB(0, 0, w, h, px, 0, w);
        Files.createDirectories(to.toAbsolutePath().getParent());
        javax.imageio.ImageIO.write(out, "png", to.toFile());
    }
}
