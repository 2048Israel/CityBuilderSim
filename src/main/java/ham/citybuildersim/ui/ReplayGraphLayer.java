package ham.citybuildersim.ui;

import ham.citybuildersim.ReplayGraphs;
import ham.citybuildersim.ReplayCity;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.TextAlignment;

/**
 * The faint GDP and population lines behind the city in a replay (TR1, 2026-10-31): one mouse-transparent canvas laid over the
 * window's content and under the time bar, drawn from the recording's first month to the month on screen and scaled to the whole
 * recording, so a line rises as time plays - the 0:54-1:35 shot. It stays in cinematic mode: it is part of the shot. The
 * arithmetic (which points, how high, the figure at the head) is {@link ReplayGraphs}; this draws it.
 *
 * <p>The look: wide soft lines across the lower two thirds of the window at about a third of full opacity, a faint fill under each,
 * a dot and a small label at each head with its figure (the game's own formatters, Money). The colours are the output and people
 * areas' (Palette.BUSINESS and Palette.PEOPLE). Redrawn only when the month on screen, the window's size or the room the bar
 * takes changes, never on a frame that has nothing new.
 *
 * WHY (TR1): Jerus's trailer wants "GDP and population graphs rising faintly behind" the city while it expands; the screens' own
 * charts are for reading, and these are for looking at.
 */
final class ReplayGraphLayer {

    /** How opaque a line is: 0.35 - the brief's "about 30-40%": faint enough to sit behind the city. */
    static final double LINE_ALPHA = 0.35;

    /** The fill under a line is this opaque at the line and fades to nothing at the baseline: 0.12. */
    static final double FILL_ALPHA = 0.12;

    /** A line's width as a share of the window's height, at least 3 px: 0.0075, 8 px in a 1080 window. */
    static final double LINE_SHARE = 0.0075;

    /** The lines stand between these shares of the window's height from its top: the lower two thirds, 0.34 to the baseline. */
    static final double TOP_SHARE = 0.34;

    /** The room kept at the left and the right of the window, as shares of its width: the head's label needs the right's. */
    static final double LEFT_SHARE = 0.04, RIGHT_SHARE = 0.12;

    /** At most this many points are drawn a line; a recording of thousands of months is stepped over. */
    static final int POINTS_MOST = 1500;

    private final ReplayMode mode;
    private final UserInterface ui;
    private Canvas canvas;
    private boolean on;
    private ReplayGraphs.Line people, output;
    private double bottomInset;
    private int drawnMonth = Integer.MIN_VALUE;
    private double drawnW = -1, drawnH = -1, drawnInset = -1;

    ReplayGraphLayer(ReplayMode mode, UserInterface ui) {
        this.mode = mode;
        this.ui = ui;
    }

    boolean isOn() { return on; }

    /** The lines read from the recording's history (ReplayCity.master()), once: the recording's first and last month bound them. */
    void load(ReplayCity city) {
        int[] months = city.months();
        int first = months[0], last = months[months.length - 1];
        people = ReplayGraphs.of(city.master(), "population", first, last, false);
        output = ReplayGraphs.of(city.master(), "gdp", first, last, true);
        firstMonth = first;
        lastMonth = last;
    }

    private int firstMonth, lastMonth;

    /** G: the lines on or off. */
    boolean toggle() {
        on = !on;
        if (on) {
            if (canvas == null) {
                canvas = new Canvas();
                canvas.setMouseTransparent(true);
                canvas.setManaged(false);
                canvas.widthProperty().bind(ui.windowStack.widthProperty());
                canvas.heightProperty().bind(ui.windowStack.heightProperty());
            }
            if (!ui.windowStack.getChildren().contains(canvas)) ui.windowStack.getChildren().add(canvas);
            drawnMonth = Integer.MIN_VALUE;
        } else if (canvas != null) {
            ui.windowStack.getChildren().remove(canvas);
        }
        return on;
    }

    /** The replay is left: the canvas comes off the window, the switch goes back to off for the next one. */
    void leave() {
        if (canvas != null) ui.windowStack.getChildren().remove(canvas);
        on = false;
        people = output = null;
    }

    /** The canvas, or null before the graphs were first switched on (ReplayMode tells the Reenactor never to pick it). */
    Node layer() { return canvas; }

    /** The canvas kept just under the node it is given (the ghost layer, which lies just under the time bar) in the window's stack, above the content and the expanded map. */
    void keepUnder(Node bar) {
        if (!on || canvas == null || bar == null) return;
        List<Node> kids = ui.windowStack.getChildren();
        int b = kids.indexOf(bar);
        if (b < 0) return;
        int g = kids.indexOf(canvas);
        if (g == b - 1) return;
        if (g >= 0) kids.remove(g);
        kids.add(Math.max(0, kids.indexOf(bar)), canvas);
        drawnMonth = Integer.MIN_VALUE;
    }

    /** A frame: the lines drawn again if the month on screen, the window or the bar's room moved. */
    void frame(int month, double barRoom) {
        if (!on || canvas == null || people == null) return;
        double w = canvas.getWidth(), h = canvas.getHeight();
        if (month == drawnMonth && w == drawnW && h == drawnH && barRoom == drawnInset) return;
        drawnMonth = month;
        drawnW = w;
        drawnH = h;
        drawnInset = barRoom;
        bottomInset = barRoom;
        draw(month, w, h);
    }

    private void draw(int month, double w, double h) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);
        if (w < 40 || h < 40) return;
        double base = h - Math.max(0.05 * h, bottomInset + 10), top = h * TOP_SHARE;
        double left = w * LEFT_SHARE, span = w * (1 - LEFT_SHARE - RIGHT_SHARE);
        double width = Math.max(3, h * LINE_SHARE), font = Math.max(14, Math.min(30, h * 0.024));
        double[] heads = new double[2];
        String[] words = new String[2];
        Color[] colours = { Color.web(Palette.BUSINESS), Color.web(Palette.PEOPLE) };
        ReplayGraphs.Line[] lines = { output, people };
        for (int k = 0; k < 2; k++) {
            ReplayGraphs.Line line = lines[k];
            int n = line.drawn(month);
            heads[k] = Double.NaN;
            if (n == 0) continue;
            int step = Math.max(1, n / POINTS_MOST);
            double[] xs = new double[n / step + 2], ys = new double[xs.length];
            int p = 0;
            for (int i = 0; i < n; i += step) {
                xs[p] = left + ReplayGraphs.across(line.months()[i], firstMonth, lastMonth) * span;
                ys[p] = base - line.share(i) * (base - top);
                p++;
            }
            if ((n - 1) % step != 0) {
                xs[p] = left + ReplayGraphs.across(line.months()[n - 1], firstMonth, lastMonth) * span;
                ys[p] = base - line.share(n - 1) * (base - top);
                p++;
            }
            Color c = colours[k];
            if (p >= 2) {
                // The fill: the line down to the baseline, faint at the line and none at the foot.
                double[] fx = new double[p + 2], fy = new double[p + 2];
                System.arraycopy(xs, 0, fx, 0, p);
                System.arraycopy(ys, 0, fy, 0, p);
                fx[p] = xs[p - 1];
                fy[p] = base;
                fx[p + 1] = xs[0];
                fy[p + 1] = base;
                gc.setFill(new LinearGradient(0, top, 0, base, false, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.color(c.getRed(), c.getGreen(), c.getBlue(), FILL_ALPHA)),
                        new Stop(1, Color.color(c.getRed(), c.getGreen(), c.getBlue(), 0))));
                gc.fillPolygon(fx, fy, p + 2);
                gc.setStroke(Color.color(c.getRed(), c.getGreen(), c.getBlue(), LINE_ALPHA));
                gc.setLineWidth(width);
                gc.setLineCap(StrokeLineCap.ROUND);
                gc.setLineJoin(StrokeLineJoin.ROUND);
                gc.strokePolyline(xs, ys, p);
            }
            gc.setFill(Color.color(c.getRed(), c.getGreen(), c.getBlue(), Math.min(1, LINE_ALPHA + 0.25)));
            gc.fillOval(xs[p - 1] - width * 0.8, ys[p - 1] - width * 0.8, width * 1.6, width * 1.6);
            heads[k] = ys[p - 1];
            double hx = xs[p - 1];
            words[k] = k == 0 ? "GDP " + Money.money(line.head(month)) + " / yr" : Money.shortNumber(line.head(month)) + " people";
            // The label stands above the head, to its left past the right-hand room, so it never leaves the window.
            gc.setFont(Palette.Fonts.sansFont(font));
            gc.setTextAlign(hx > w * (1 - RIGHT_SHARE - 0.1) ? TextAlignment.RIGHT : TextAlignment.LEFT);
            double ly = ys[p - 1] - width * 1.6 - font * 0.3;
            if (k == 1 && !Double.isNaN(heads[0]) && Math.abs(ly - (heads[0] - width * 1.6 - font * 0.3)) < font * 1.3) ly += font * 1.5;
            gc.setFill(Color.color(c.getRed(), c.getGreen(), c.getBlue(), 0.75));
            gc.fillText(words[k], hx + (gc.getTextAlign() == TextAlignment.RIGHT ? -width : width), ly);
        }
    }
}
