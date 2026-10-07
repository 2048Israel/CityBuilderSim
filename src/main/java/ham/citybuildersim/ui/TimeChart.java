package ham.citybuildersim.ui;

import ham.citybuildersim.ChartModel;
import ham.citybuildersim.CityCalendar;
import ham.citybuildersim.DecisionLog;
import ham.citybuildersim.GameLog;
import ham.citybuildersim.YearBook;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;

/**
 * A chart of months (0.7.23): lines over a window the player drags, wheels
 * and ranges through, years on its axis, recessions named on their bands,
 * the episodes on a lane under it, the player's decisions as flags, a
 * crosshair that reads every line, and an overview of the whole history
 * with the window on it - or, small, the same lines and years without the
 * controls, and since 0.7.38 the decisions it is handed on a smaller lane.
 *
 * WHY. Jerus: "in teh graphs you should be able to pan the chart just like
 * yahoo finance does if you get what i mean, and it actually have interactive
 * graph, and like also the crisis labels and all". City History drew a
 * JavaFX LineChart - a node per point, three chips for the window, a month
 * number on the axis that nobody could read as a date, grey bands nothing
 * named - and a LineChart has no pan, no wheel and one y-axis. This draws on
 * a canvas: one pass a frame, every month of a four-thousand-month city
 * without thinning it to keep the node count down, two axes of its own, and
 * the bands, lanes and flags in the same coordinates as the lines.
 *
 * NOTHING HERE IS ARITHMETIC ABOUT THE CITY. The window, the ticks, the
 * scales, the lanes and the flags are ChartModel's, which ChartCheck holds
 * to, and since 0.7.50 so are the copy of what it is handed that it draws
 * from and the stack's runs and reach (setData(); ChartModel, WHAT A CHART
 * DRAWS FROM); the bands and episodes YearBook's; the decisions the
 * DecisionLog's; each line's values the screen's, in its stored units, with
 * the screen's own formatter to read them. This draws and listens.
 */
final class TimeChart extends VBox {

    /* ----------------------------- the dials ----------------------------- */

    /** How wide a y-axis's labels are held. */
    static final double AXIS_W = 64;

    /** The margin on a side with no axis. */
    static final double NO_AXIS_W = 14;

    /** The row above the plot that recessions' names sit in. */
    static final double BAND_ROW = 20;

    /** The row under the plot that the years sit in. */
    static final double TIME_ROW = 20;

    /** One row of the episode lane. */
    static final double EPISODE_ROW = 18;

    /** The decision lane. */
    static final double FLAG_ROW = 24;

    /** ...on a small chart handed decisions (0.7.38): the same lane, smaller. */
    static final double SMALL_FLAG_ROW = 18;

    /** The overview strip under the lanes. */
    static final double OVERVIEW = 52;

    /** How close, in pixels, the pointer must be to the overview window's edge to take it. */
    static final double HANDLE = 7;

    /** A flag closer than this many pixels to the first of the group before it is drawn in that group, as one flag with the count. */
    static final double FLAG_GAP = 16;

    /** How strongly a recession is shaded: enough to see, not enough to read as a colour. */
    static final double RECESSION_SHADE = .10;

    /** How far a press may wander and still be a click rather than a drag. */
    static final double DRAG_SLOP = 3;

    /** How long the window must rest before the page under the chart is redrawn for it, in milliseconds. */
    static final double SETTLE_MS = 280;

    /** The card's widest. */
    static final double CARD_W = 300;

    /** A wheel notch, in the pixels JavaFX reports it as. */
    static final double NOTCH = 40;

    /** Which node owns the wheel: the window's page-scroll filter leaves a wheel over this alone (UserInterface). */
    static final String WHEEL_OWNER = "TimeChart.wheel";

    /** How far above and below its own box the chart's clip reaches (0.7.40): it cuts the sides only. */
    static final double OVERHANG = 2000;

    /* ------------------------- what it is handed ------------------------- */

    /**
     * One line: its key and name, its colour, the axis it reads against (0
     * left, 1 right), its values in their stored units aligned to the months,
     * what turns a stored value into the axis's units, how a stored value is
     * read out, and what the legend says its unit is.
     */
    record Line(String key, String label, String colour, int side, double[] values,
                DoubleUnaryOperator toPlot, DoubleFunction<String> reads, String unitWords, boolean dashed) {
        /** ...drawn solid, as every line was until 0.7.45. */
        Line(String key, String label, String colour, int side, double[] values,
             DoubleUnaryOperator toPlot, DoubleFunction<String> reads, String unitWords) {
            this(key, label, colour, side, values, toPlot, reads, unitWords, false);
        }

        /** The same line drawn dashed (0.7.45): what people expect beside what happened. */
        Line asDashed() { return new Line(key, label, colour, side, values, toPlot, reads, unitWords, true); }

        /** The same line with its own copy of its values, exactly n months long (0.7.50; ChartModel.aligned()). */
        Line alignedTo(int n) {
            return new Line(key, label, colour, side, ChartModel.aligned(values, n), toPlot, reads, unitWords, dashed);
        }
    }

    /** One value axis: what a gridline says, from the value and the step; and whether it starts at zero. */
    record Axis(java.util.function.BiFunction<Double, Double, String> tick, boolean fromZero) { }

    /** Layers stacked from zero under the first line (GDP in layers, 0.7.6): their values, names and colours. */
    record Stack(double[][] layers, String[] names, String[] colours, DoubleUnaryOperator toPlot,
                 DoubleFunction<String> reads) {
        /** The same stack with its own copy of every layer, exactly n months long (0.7.50; ChartModel.aligned()). */
        Stack alignedTo(int n) {
            return new Stack(ChartModel.aligned(layers == null ? new double[0][] : layers, n),
                    names == null ? new String[0] : names.clone(), colours == null ? new String[0] : colours.clone(),
                    toPlot, reads);
        }

        /** Layer p's name, or nothing for a layer handed no name. */
        String name(int p) { return p < names.length ? names[p] : ""; }

        /** Layer p's colour, or the muted ink for a layer handed none. */
        String colour(int p) { return p < colours.length ? colours[p] : Palette.TEXT_3; }
    }

    private final ChartModel window;
    private final Set<String> hidden;
    private final boolean main;

    private List<Integer> months = List.of();

    /**
     * How many of the months, from the first, are folded years (0.7.55;
     * HistorySave.yearlyPoints()): each drawn at its year's last month, as
     * the history keeps it, and read in the crosshair as its year. 0 for a
     * chart that is never handed a history that old.
     */
    private int yearly;
    private List<Line> lines = List.of();
    private Axis left, right;
    private boolean squashed, log;
    private Stack stack;
    private List<YearBook.Band> bands = List.of();
    private List<YearBook.Episode> episodes = List.of();
    private int[] lanes = new int[0];
    private List<ChartModel.Flag> flags = List.of();
    /** What the model did on its own that a line answers to (0.7.45: the basket's links), drawn over the plot on the big chart. */
    private List<ChartModel.Mark> marks = List.of();
    private String emptySays = "";

    /* ------------------------------ its nodes ------------------------------ */

    private final Canvas plot = new Canvas();
    private final Canvas strip = new Canvas();
    private final Pane over = new Pane();
    private final VBox card = new VBox(2);
    private final FlowPane legend = new FlowPane(6, 6);
    private final HBox ranges = new HBox(2);
    private final HBox lead = new HBox(10);
    private final Label fullButton = new Label();
    private final Label spanSays = new Label();
    private final Label hint = new Label();
    private final HBox toolbar;

    /* ------------------------------ geometry ------------------------------ */

    private double width = 760, plotH = 380;
    private double plotLeft, plotRight, top;

    /* ---------------------------- interaction ---------------------------- */

    private double hoverX = Double.NaN, hoverY = Double.NaN;
    private Object pinned;
    private double pinnedX;
    private double pressX = Double.NaN, pressY;
    private double pressLo, pressHi, pressMonth;
    private boolean dragging;
    private int stripMode;
    private boolean full;
    private final List<Runnable> followers = new ArrayList<>();
    private Runnable onSettled, onFullScreen;
    private final javafx.animation.PauseTransition settle =
            new javafx.animation.PauseTransition(javafx.util.Duration.millis(SETTLE_MS));

    /**
     * @param window the window of months it shows - shared with the charts that follow it
     * @param hidden the keys of lines the legend has switched off - shared with the screen, so a redraw keeps them
     * @param main   the big chart: the controls, both lanes, the overview, and pan and zoom (a small
     *               one draws the decision lane alone, and only when it is handed flags)
     */
    TimeChart(ChartModel window, Set<String> hidden, boolean main) {
        super(main ? 8 : 0);
        this.window = window;
        this.hidden = hidden;
        this.main = main;
        // The big chart zooms on the wheel; a small one leaves it to the page.
        if (main) getProperties().put(WHEEL_OWNER, Boolean.TRUE);
        /*
         * NEVER WIDER THAN IT IS GIVEN (0.7.40). A VBox's minimum width is its
         * widest child's and a canvas's is its width, so a chart drawn a pixel
         * wider than what held it pushed that out a pixel - and City History,
         * which sized its charts off the page's own width, did it again every
         * pulse (Jerus: "the graphs tend to go to the right endlessly"). The
         * chart's minimum is nothing, and what is past its sides is cut off.
         * Only the sides: a hover card may hang below a short chart, as it
         * always could.
         */
        setMinWidth(0);
        javafx.scene.shape.Rectangle sides = new javafx.scene.shape.Rectangle();
        sides.setY(-OVERHANG);
        sides.widthProperty().bind(widthProperty());
        sides.heightProperty().bind(heightProperty().add(2 * OVERHANG));
        setClip(sides);
        // Taken off the screen, it lets go of the pointer (0.7.50): a page torn down no longer tells what is on
        // it that the pointer left (UserInterface.clearMenu, A PAGE TORN DOWN HEARS NOTHING), and City History
        // keeps its charts for the next page - one would come back with a crosshair where the pointer was.
        sceneProperty().addListener((o, was, now) -> { if (now == null) hoverX = hoverY = Double.NaN; });

        card.setStyle(Palette.block(Palette.PINNED, Palette.EDGE) + " -fx-padding: 7 10 8 10;");
        card.setMaxWidth(CARD_W);
        card.setVisible(false);
        card.setMouseTransparent(true);
        over.getChildren().add(card);
        over.setMouseTransparent(true);
        over.setPickOnBounds(false);
        StackPane plotStack = new StackPane(plot, over);
        plotStack.setAlignment(Pos.TOP_LEFT);

        // ...and not while a button is down anywhere in the window (0.7.40): the redraw would take
        // a click's node from under its release (UserInterface, A PRESS IS NEVER REBUILT AWAY).
        settle.setOnFinished(e -> {
            if (onSettled == null || dragging) return;
            if (UserInterface.pressHeld(this)) settle.playFromStart();
            else onSettled.run();
        });

        if (main) {
            setStyle("-fx-background-color: " + Palette.RAISED + "; -fx-background-radius: 10;"
                    + " -fx-border-color: " + Palette.EDGE + "; -fx-border-radius: 10; -fx-border-width: 1;"
                    + " -fx-padding: 12 14 10 14;");
            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            legend.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(legend, Priority.SOMETIMES);
            ranges.setAlignment(Pos.CENTER_RIGHT);
            ranges.setMinWidth(Region.USE_PREF_SIZE);
            fullButton.setMinWidth(Region.USE_PREF_SIZE);
            fullButton.setOnMouseClicked(e -> { if (onFullScreen != null) onFullScreen.run(); });
            lead.setAlignment(Pos.CENTER_LEFT);
            lead.setMinWidth(Region.USE_PREF_SIZE);
            toolbar = new HBox(8, lead, legend, gap, ranges, fullButton);
            toolbar.setAlignment(Pos.CENTER_LEFT);
            hint.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            spanSays.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            Region gap2 = new Region();
            HBox.setHgrow(gap2, Priority.ALWAYS);
            HBox caption = new HBox(8, hint, gap2, spanSays);
            caption.setAlignment(Pos.CENTER_LEFT);
            getChildren().addAll(toolbar, plotStack, strip, caption);
            listen();
            listenToStrip();
            setFull(false);
        } else {
            toolbar = null;
            getChildren().add(plotStack);
            plot.setOnMouseMoved(guarded(e -> { hoverX = e.getX(); hoverY = e.getY(); draw(); }));
            plot.setOnMouseExited(guarded(e -> { hoverX = hoverY = Double.NaN; draw(); }));
        }
    }

    /* =====================================================================
       WHAT IT DRAWS, HANDED OVER ON EVERY REBUILD
       ===================================================================== */

    /**
     * Everything the chart draws. The months are the history's axis; every
     * line's values and the stack's layers are aligned to them. The window
     * is told the data's ends, and follows the newest month if it was on it.
     *
     * ONE SNAPSHOT, TAKEN HERE (0.7.50; ChartModel, WHAT A CHART DRAWS FROM).
     * The chart keeps copies: the months, every line's values and every
     * layer exactly as many months long, and the bands, episodes, flags and
     * marks as lists of their own. It used to keep the history's month list
     * itself, which grew under a chart still on screen when a month landed,
     * and the next redraw read past the end of the stack's layers.
     */
    void setData(List<Integer> months, List<Line> lines, Axis left, Axis right, boolean squashed,
                 boolean log, Stack stack, List<YearBook.Band> bands, List<YearBook.Episode> episodes,
                 List<ChartModel.Flag> flags, String emptySays) {
        setData(months, lines, left, right, squashed, log, stack, bands, episodes, flags, List.of(), emptySays);
    }

    /**
     * ...and the marks over the plot (0.7.45): the basket's links, which City
     * History hands the big chart while it draws an index line. A mark is not
     * a decision - the model did it, not the player - so it is a hairline
     * through the plot, not a flag on the lane.
     */
    void setData(List<Integer> months, List<Line> lines, Axis left, Axis right, boolean squashed,
                 boolean log, Stack stack, List<YearBook.Band> bands, List<YearBook.Episode> episodes,
                 List<ChartModel.Flag> flags, List<ChartModel.Mark> marks, String emptySays) {
        this.marks = fixed(marks);
        this.months = ChartModel.fixedMonths(months);
        int n = this.months.size();
        List<Line> own = new ArrayList<>();
        if (lines != null) for (Line l : lines) own.add(l.alignedTo(n));
        this.lines = Collections.unmodifiableList(own);
        this.left = left;
        this.right = right;
        this.squashed = squashed;
        this.log = log;
        this.stack = stack == null ? null : stack.alignedTo(n);
        this.bands = fixed(bands);
        this.episodes = fixed(episodes);
        this.lanes = ChartModel.lanes(this.episodes);
        this.flags = fixed(flags);
        this.emptySays = emptySays == null ? "" : emptySays;
        if (!this.months.isEmpty()) window.setData(this.months.get(0), this.months.get(this.months.size() - 1));
        // A pinned card on something that is no longer drawn goes with it.
        if (pinned instanceof YearBook.Band b && !this.bands.contains(b)) pinned = sameBand(b);
        if (pinned instanceof YearBook.Episode ep && !this.episodes.contains(ep)) pinned = null;
        if (main) {
            buildLegend();
            buildRanges();
        }
        layoutCanvases();
    }

    /** A list as the chart keeps it: its own copy, so the caller's can change afterwards. */
    private static <T> List<T> fixed(List<T> list) {
        return list == null ? List.of() : Collections.unmodifiableList(new ArrayList<>(list));
    }

    private Object sameBand(YearBook.Band b) {
        for (YearBook.Band n : bands) if (n.fromMonth() == b.fromMonth()) return n;
        return null;
    }

    /** The width the chart is drawn at, and the plot's height; the lanes and the overview take what they need. */
    void setSize(double width, double plotHeight) {
        this.width = Math.max(240, width);
        this.plotH = Math.max(80, plotHeight);
        layoutCanvases();
    }

    /** The height everything but the plot takes, for a caller fitting the chart to a space (full screen). */
    double heightBesidePlot() {
        double rows = Math.max(1, ChartModel.laneCount(lanes));
        return 12 + 10 + 34 + 8 + BAND_ROW + TIME_ROW + 4 + rows * EPISODE_ROW + 4 + FLAG_ROW + 2 + 8 + OVERVIEW + 8 + 18;
    }

    /** Called with nothing when the window moves - a chart that shows the same window redraws. */
    void follow(TimeChart leader) { leader.followers.add(this::draw); }

    /** What runs once the window has rested: the screen's redraw, for the readings that follow the window. */
    void onSettled(Runnable r) { this.onSettled = r; }

    /** What the full-screen button does. */
    void onFullScreen(Runnable r) { this.onFullScreen = r; }

    /** A big chart on another tab (0.7.35: Trade's rate over time) has no full-screen button: only City History lays a chart out full screen. */
    void withoutFullScreen() { if (toolbar != null) toolbar.getChildren().remove(fullButton); }

    /** True while a drag is in hand on a chart that is on screen: a month's redraw waits for the release. */
    boolean isDragging() { return dragging && getScene() != null; }

    /**
     * Leaves the chart at rest (after the docs pass): no drag in hand, no
     * overview edge held, no redraw waiting. Called whenever City History's
     * page or its full screen goes away. A release that never reaches a chart
     * taken out of the scene - Esc or P with the button held - would leave
     * the page waiting on it; and a settle still counting down would draw
     * City History over whatever screen replaced it.
     */
    void letGo() {
        settle.stop();
        dragging = false;
        stripMode = 0;
        pressX = Double.NaN;
        plot.setCursor(Cursor.DEFAULT);
    }

    /** The slot at the left of the toolbar: the title and the date, in full screen. */
    HBox lead() { return lead; }

    /** Full screen or not: the button's words and icon. The window itself is the screen's to lay out. */
    void setFull(boolean full) {
        this.full = full;
        if (!main) return;
        javafx.scene.shape.SVGPath icon = new javafx.scene.shape.SVGPath();
        icon.setContent(full ? "M9 4v5H4M15 4v5h5M9 20v-5H4M15 20v-5h5" : "M4 9V4h5M20 9V4h-5M4 15v5h5M20 15v5h-5");
        icon.setFill(Color.TRANSPARENT);
        icon.setStroke(Color.web(Palette.TEXT));
        icon.setStrokeWidth(1.8);
        icon.setScaleX(.7);
        icon.setScaleY(.7);
        fullButton.setGraphic(icon);
        fullButton.setText(full ? "Exit full screen · Esc" : "Full screen");
        fullButton.setGraphicTextGap(4);
        fullButton.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT) + " -fx-padding: 4 10 4 6;"
                + " -fx-background-color: " + Palette.CONTROL + "; -fx-background-radius: 6; -fx-cursor: hand;");
        HistoryScreen.tip(fullButton, full ? "Back to the page (Esc). F11 is the window's own full screen, separately."
                : "The whole window for the chart. Esc comes back.");
        hint.setText(full
                ? "Drag to pan · scroll to zoom · double-click to reset · click a band or a flag for what happened"
                : "Drag to pan · scroll to zoom · double-click to reset · drag the window below to move through the years");
    }

    /* =====================================================================
       THE LEGEND AND THE RANGES
       ===================================================================== */

    private void buildLegend() {
        legend.getChildren().clear();
        for (Line l : lines) {
            boolean on = !hidden.contains(l.key());
            Region bar = new Region();
            bar.setMinSize(12, 3);
            bar.setPrefSize(12, 3);
            bar.setMaxSize(12, 3);
            bar.setStyle("-fx-background-color: " + (on ? l.colour() : Palette.TEXT_SPENT) + "; -fx-background-radius: 2;");
            Label chip = new Label(l.label() + (l.unitWords() == null || l.unitWords().isEmpty() ? "" : "  " + l.unitWords()));
            chip.setGraphic(bar);
            chip.setGraphicTextGap(6);
            chip.setStyle(Palette.words(Palette.SIZE_LABEL, on ? Palette.TEXT : Palette.TEXT_SPENT)
                    + " -fx-padding: 3 9 3 8; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-width: 1;"
                    + " -fx-border-color: " + (on ? l.colour() : Palette.EDGE) + "; -fx-cursor: hand;");
            HistoryScreen.tip(chip, (on ? "Hides" : "Shows") + " this line; the axes fit what is shown.");
            chip.setOnMouseClicked(guarded(e -> {
                if (!hidden.remove(l.key())) hidden.add(l.key());
                buildLegend();
                draw();
            }));
            legend.getChildren().add(chip);
        }
    }

    private void buildRanges() {
        ranges.getChildren().clear();
        for (int i = 0; i < ChartModel.RANGE_NAMES.length; i++) {
            int months = i < ChartModel.RANGES.length ? ChartModel.RANGES[i] : ChartModel.ALL;
            Label r = new Label(ChartModel.RANGE_NAMES[i]);
            r.setUserData(months);
            r.setOnMouseClicked(guarded(e -> { window.showRange(months); moved(true); }));
            ranges.getChildren().add(r);
        }
        paintRanges();
    }

    /** The range button lit is the one the window still shows exactly; a pan or a wheel lights none. */
    private void paintRanges() {
        for (javafx.scene.Node n : ranges.getChildren()) {
            int months = (Integer) n.getUserData();
            boolean on = window.range() == months && window.onRange();
            n.setStyle(Palette.figure(Palette.SIZE_LABEL, on ? Palette.TEXT : Palette.TEXT_2)
                    + " -fx-padding: 4 9 4 9; -fx-background-radius: 6; -fx-cursor: hand;"
                    + " -fx-background-color: " + (on ? Palette.CONTROL : "transparent") + ";");
        }
    }

    /* =====================================================================
       THE GEOMETRY
       ===================================================================== */

    private int laneRows() { return Math.max(1, ChartModel.laneCount(lanes)); }

    private boolean anyOnRight() {
        if (right == null || squashed) return false;
        for (Line l : lines) if (l.side() == 1) return true;
        return false;
    }

    private double plotBottom()     { return top + plotH; }
    private double episodesTop()    { return plotBottom() + TIME_ROW + 4; }
    private double flagsTop()       { return main ? episodesTop() + laneRows() * EPISODE_ROW + 4 : plotBottom() + TIME_ROW; }
    private double canvasHeight()   { return main ? flagsTop() + FLAG_ROW + 2 : plotBottom() + TIME_ROW + (flagLane() ? SMALL_FLAG_ROW + 2 : 0); }

    /**
     * Whether the decision lane is drawn: always on the big chart, and on a
     * small one handed a decision to show (0.7.38: the Bank's rates and
     * Finances' debt and rate were handed theirs and drew none).
     */
    private boolean flagLane()      { return main || !flags.isEmpty(); }

    /** The lane's height: the big chart's, or a small chart's. */
    private double flagRow()        { return main ? FLAG_ROW : SMALL_FLAG_ROW; }

    private void layoutCanvases() {
        top = main ? BAND_ROW : 6;
        plotLeft = AXIS_W;
        plotRight = width - (anyOnRight() ? AXIS_W : NO_AXIS_W);
        plot.setWidth(width);
        plot.setHeight(canvasHeight());
        over.setPrefSize(width, canvasHeight());
        if (main) {
            strip.setWidth(width);
            strip.setHeight(OVERVIEW);
            legend.setPrefWrapLength(Math.max(200, width - 380));
        }
        setPrefWidth(main ? width + 30 : width);
        setMaxWidth(main ? width + 30 : width);
        draw();
    }

    private double plotW() { return Math.max(1, plotRight - plotLeft); }

    private double xOf(double month) { return ChartModel.x(month, window.lo(), window.hi(), plotLeft, plotW()); }

    private double monthAt(double x) { return ChartModel.month(x, window.lo(), window.hi(), plotLeft, plotW()); }

    /* =====================================================================
       THE SCALES, FIT TO WHAT IS SHOWN
       ===================================================================== */

    private boolean shown(Line l) { return !main || !hidden.contains(l.key()); }

    /** A line's value at index i in the axis's units, or NaN: through toPlot, the log, or its own low-to-high. */
    private double plotted(Line l, int i, double[] own) {
        double v = ChartModel.at(l.values(), i);
        if (Double.isNaN(v)) return Double.NaN;
        if (squashed) {
            if (own == null || !(own[1] > own[0])) return 50;
            return (v - own[0]) / (own[1] - own[0]) * 100;
        }
        double p = l.toPlot().applyAsDouble(v);
        if (log) return p > 0 ? Math.log10(p) : Double.NaN;
        return p;
    }

    /** Each shown line's low and high in the window, for the squashed chart. */
    private double[][] ownRanges() {
        double[][] out = new double[lines.size()][];
        for (int k = 0; k < lines.size(); k++) out[k] = ChartModel.reach(lines.get(k).values(), months, window.lo(), window.hi());
        return out;
    }

    /** The two axes' scales for the window: {left, right}; null for a side with nothing on it. */
    private ChartModel.Scale[] scales(double[][] own) {
        ChartModel.Scale[] out = new ChartModel.Scale[2];
        if (squashed) {
            out[0] = new ChartModel.Scale(0, 100, 20);
            return out;
        }
        int ticks = (int) Math.max(3, Math.min(9, Math.round(plotH / 52)));
        for (int side = 0; side < 2; side++) {
            double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE;
            boolean any = false;
            for (int k = 0; k < lines.size(); k++) {
                Line l = lines.get(k);
                if (l.side() != side || !shown(l)) continue;
                any = true;
                for (int i = 0; i < months.size() && i < l.values().length; i++) {
                    int m = months.get(i);
                    if (m < window.lo() - 1e-9 || m > window.hi() + 1e-9) continue;
                    double p = plotted(l, i, null);
                    if (Double.isNaN(p) || Double.isInfinite(p)) continue;
                    lo = Math.min(lo, p);
                    hi = Math.max(hi, p);
                }
            }
            if (side == 0 && stack != null) {
                any = true;
                double[] reach = stackReach();
                lo = Math.min(lo, reach[0]);
                hi = Math.max(hi, reach[1]);
            }
            if (!any || hi < lo) continue;
            Axis a = side == 0 ? left : right;
            out[side] = log ? ChartModel.logScale(lo, hi)
                    : ChartModel.niceScale(lo, hi, ticks, a != null && a.fromZero());
        }
        return out;
    }

    /** What the stack's first three layers reach in the window, summed from zero (ChartModel.stackReach()). */
    private double[] stackReach() {
        return ChartModel.stackReach(months, stack.layers(), 3, window.lo(), window.hi(), stack.toPlot());
    }

    /* =====================================================================
       DRAWING
       ===================================================================== */

    /**
     * Draws the chart, its overview and its card, and lights the range button
     * the window still matches.
     *
     * A FAULT HERE NEVER REACHES JAVAFX (0.7.50). This runs from the
     * pointer's handlers, and JavaFX fires one of them - the pointer leaving
     * - from inside its own removal of a page. Jerus's 0.7.49 chart threw
     * there, and the exception went up through the page's children list half
     * way through the change: the page's children before the one holding the
     * chart had been taken off the scene and were still in the list. So a
     * frame that fails is skipped:
     * the canvas is cleared, the card hidden, and the fault written to the
     * log with its stack trace, once for this chart.
     */
    void draw() {
        try {
            paint();
        } catch (RuntimeException e) {
            skipFrame(e);
        }
    }

    /** Whether this chart has written a fault to the log: once a chart, so a fault on every frame is one entry (0.7.50). */
    private boolean faultLogged;

    /** A fault in this chart, written to the log with its stack trace the first time only. */
    private void fault(String what, RuntimeException e) {
        if (faultLogged) return;
        faultLogged = true;
        GameLog.failure("A chart failed " + what + " (TimeChart; this chart reports no more of its faults)", e);
    }

    /**
     * A frame that failed, skipped: the state a drawing step saved put back
     * (each saves one at a time, and a restore with nothing saved does
     * nothing), the canvases cleared, the card hidden, and the fault logged.
     */
    private void skipFrame(RuntimeException e) {
        try {
            GraphicsContext g = plot.getGraphicsContext2D();
            g.restore();
            g.setLineDashes((double[]) null);
            g.setTextAlign(TextAlignment.LEFT);
            g.setTextBaseline(VPos.BASELINE);
            g.clearRect(0, 0, plot.getWidth(), plot.getHeight());
            if (main) strip.getGraphicsContext2D().clearRect(0, 0, strip.getWidth(), strip.getHeight());
            card.setVisible(false);
        } catch (RuntimeException ignored) {
            // The fault below is what is worth reading.
        }
        fault("drawing, and the frame was skipped", e);
    }

    /**
     * A handler that cannot throw into JavaFX's event dispatch (0.7.50): what
     * it throws is the chart's fault, logged once, and the event is dropped.
     * Every pointer, wheel and click handler on the chart is one of these.
     */
    private <T extends javafx.event.Event> javafx.event.EventHandler<T> guarded(javafx.event.EventHandler<T> h) {
        return e -> {
            try {
                h.handle(e);
            } catch (RuntimeException ex) {
                fault("handling " + e.getEventType() + ", and the event was dropped", ex);
            }
        };
    }

    /** One frame: draw()'s, which catches what it throws. */
    private void paint() {
        if (!(width > 0)) return;
        plotRight = width - (anyOnRight() ? AXIS_W : NO_AXIS_W);
        GraphicsContext g = plot.getGraphicsContext2D();
        g.clearRect(0, 0, plot.getWidth(), plot.getHeight());
        g.setTextBaseline(VPos.BASELINE);

        double[][] own = squashed ? ownRanges() : null;
        ChartModel.Scale[] scale = scales(own);
        double bottom = plotBottom();

        // The ground of the plot, and the gridlines of whichever axis carries them.
        ChartModel.Scale grid = scale[0] != null ? scale[0] : scale[1];
        g.setFill(Color.web(Palette.STAGE, main ? .35 : .55));
        g.fillRect(plotLeft, top, plotW(), plotH);
        if (grid != null) {
            g.setStroke(Color.web(Palette.EDGE));
            g.setLineWidth(1);
            for (double t : grid.ticks()) {
                double y = Math.floor(grid.y(t, bottom, plotH)) + .5;
                g.strokeLine(plotLeft, y, plotRight, y);
            }
        }

        // The years, down the plot faintly and under it in words.
        List<ChartModel.Tick> ticks = ChartModel.timeTicks(window.lo(), window.hi(), plotW());
        g.setStroke(Color.web(Palette.EDGE, .55));
        for (ChartModel.Tick t : ticks) {
            if (!t.year()) continue;
            double x = Math.floor(xOf(t.month())) + .5;
            g.strokeLine(x, top, x, bottom);
        }

        drawBands(g, bottom);
        if (main) drawMarks(g, bottom);
        if (stack != null && scale[0] != null) drawStack(g, scale[0], bottom);
        boolean drewAny = drawLines(g, scale, own, bottom);
        drawAxes(g, scale, bottom);
        drawTime(g, ticks, bottom);
        if (main) drawEpisodes(g);
        if (flagLane()) drawFlags(g);
        if (!drewAny) {
            // What the screen said when nothing was ever recorded or picked;
            // otherwise the legend has hidden it all, or this window holds none of it.
            boolean allHidden = !lines.isEmpty();
            for (Line l : lines) allHidden &= !shown(l);
            String says = !emptySays.isEmpty() ? emptySays
                    : allHidden ? "every line is hidden - its chip above shows it again"
                    : "nothing recorded in these months";
            g.setFill(Color.web(Palette.TEXT_SPENT));
            g.setFont(Palette.Fonts.sansFont(main ? 13 : 11));
            g.setTextAlign(TextAlignment.CENTER);
            g.fillText(says, plotLeft + plotW() / 2, top + plotH / 2);
            g.setTextAlign(TextAlignment.LEFT);
        }
        drawCrosshair(g, scale, own, bottom);
        if (main) {
            drawStrip();
            spanSays.setText(CityCalendar.formatShort(window.firstMonthShown()) + " – "
                    + CityCalendar.formatShort(window.lastMonthShown()) + " · "
                    + String.format("%,d months", window.monthsShown()));
            paintRanges();
        }
        fillCard(scale, own);
    }

    private void drawBands(GraphicsContext g, double bottom) {
        double lastLabelEnd = -1;
        g.setFont(Palette.Fonts.sansStrongFont(full ? 12 : 11));
        for (YearBook.Band b : bands) {
            double a = Math.max(window.lo(), b.fromMonth() - .5), z = Math.min(window.hi(), b.toMonth() + .5);
            if (z <= a) continue;
            double xa = xOf(a), xz = xOf(z);
            boolean lit = pinned == b || (pinned == null && hovered() == b);
            g.setFill(Color.web(Palette.TEXT_2, lit ? RECESSION_SHADE * 1.8 : RECESSION_SHADE));
            g.fillRect(xa, top, Math.max(1, xz - xa), plotH);
            if (!main) continue;
            // The band's name on it, where the band begins in view - unless the last name is still there.
            double start = xOf(Math.max(window.lo(), b.fromMonth() - .5));
            g.setStroke(Color.web(Palette.TEXT_3, .8));
            g.setLineWidth(1);
            g.setLineDashes(2, 3);
            g.strokeLine(Math.floor(start) + .5, top - 6, Math.floor(start) + .5, bottom);
            g.setLineDashes((double[]) null);
            String name = b.name();
            double w = textWidth(name, g.getFont());
            if (start + 4 > lastLabelEnd && start + 4 + w <= plotRight + AXIS_W - 4) {
                g.setFill(Color.web(lit ? Palette.TEXT : Palette.TEXT_2));
                g.fillText(name, start + 4, top - 6);
                lastLabelEnd = start + 4 + w + 10;
            }
        }
    }

    /** How near a mark the pointer must be, in pixels, for the card to say it (0.7.45). */
    static final double MARK_REACH = 5;

    /** The marks in view: a dashed hairline in the muted ink through the plot, and its word at the plot's top (0.7.45). */
    private void drawMarks(GraphicsContext g, double bottom) {
        if (marks.isEmpty()) return;
        g.setFont(Palette.Fonts.sansFont(10));
        g.setTextAlign(TextAlignment.LEFT);
        for (ChartModel.Mark mk : marks) {
            if (mk.month() < window.lo() - .5 || mk.month() > window.hi() + .5) continue;
            double x = Math.floor(xOf(mk.month())) + .5;
            boolean near = !Double.isNaN(hoverX) && Math.abs(hoverX - x) <= MARK_REACH && inPlot(hoverX, hoverY);
            g.setStroke(Color.web(Palette.TEXT_MUTED, near ? 1 : .75));
            g.setLineWidth(1);
            g.setLineDashes(4, 4);
            g.strokeLine(x, top, x, bottom);
            g.setLineDashes((double[]) null);
            g.setFill(Color.web(near ? Palette.TEXT_2 : Palette.TEXT_MUTED));
            g.fillText("basket", x + 4, top + 12);
        }
    }

    /** The mark within MARK_REACH of the pointer, or null. */
    private ChartModel.Mark markAt(double x) {
        if (marks.isEmpty() || Double.isNaN(x)) return null;
        for (ChartModel.Mark mk : marks) {
            if (mk.month() < window.lo() - .5 || mk.month() > window.hi() + .5) continue;
            if (Math.abs(x - (Math.floor(xOf(mk.month())) + .5)) <= MARK_REACH) return mk;
        }
        return null;
    }

    private void drawStack(GraphicsContext g, ChartModel.Scale s, double bottom) {
        int n = Math.min(3, stack.layers().length);
        g.save();
        clipToPlot(g);
        for (int p = n - 1; p >= 0; p--) {
            Color c = Color.web(stack.colour(p));
            g.setFill(c.deriveColor(0, 1, 1, .78));
            g.setStroke(c);
            g.setLineWidth(1);
            // One polygon per unbroken run of months: the top of this layer, then back along the one under it.
            // The runs are ChartModel's (0.7.50, ChartCheck walks them); this only places them.
            for (List<double[]> run : ChartModel.stackRuns(months, stack.layers(), p, window.lo(), window.hi(), stack.toPlot())) {
                List<double[]> at = new ArrayList<>(run.size());
                for (double[] r : run) at.add(new double[] {xOf(r[0]), s.y(r[1], bottom, plotH), s.y(r[2], bottom, plotH)});
                fillRun(g, at);
            }
        }
        g.restore();
    }

    private void fillRun(GraphicsContext g, List<double[]> run) {
        g.beginPath();
        g.moveTo(run.get(0)[0], run.get(0)[1]);
        for (int k = 1; k < run.size(); k++) g.lineTo(run.get(k)[0], run.get(k)[1]);
        for (int k = run.size() - 1; k >= 0; k--) g.lineTo(run.get(k)[0], run.get(k)[2]);
        g.closePath();
        g.fill();
        g.beginPath();
        g.moveTo(run.get(0)[0], run.get(0)[1]);
        for (int k = 1; k < run.size(); k++) g.lineTo(run.get(k)[0], run.get(k)[1]);
        g.stroke();
    }

    private void clipToPlot(GraphicsContext g) {
        g.beginPath();
        g.rect(plotLeft, top - 1, plotW(), plotH + 2);
        g.clip();
    }

    /** The lines, each month a point, or each bucket of months one where they outnumber the pixels. */
    private boolean drawLines(GraphicsContext g, ChartModel.Scale[] scale, double[][] own, double bottom) {
        boolean drew = false;
        int bucket = ChartModel.bucket(window.span(), plotW());
        g.save();
        clipToPlot(g);
        g.setLineWidth(main ? 2 : 1.8);
        g.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        for (int k = 0; k < lines.size(); k++) {
            Line l = lines.get(k);
            ChartModel.Scale s = squashed ? scale[0] : scale[l.side()];
            if (!shown(l) || s == null) continue;
            g.setStroke(Color.web(l.colour()));
            if (l.dashed()) g.setLineDashes(6, 4); else g.setLineDashes((double[]) null);
            g.beginPath();
            boolean open = false;
            double sum = 0;
            int n = 0, group = Integer.MIN_VALUE;
            double groupMonth = 0;
            for (int i = 0; i <= months.size(); i++) {
                boolean in = i < months.size() && months.get(i) >= window.lo() - bucket && months.get(i) <= window.hi() + bucket;
                int gIdx = i < months.size() ? Math.floorDiv(months.get(i), bucket) : Integer.MAX_VALUE;
                if (gIdx != group || !in) {
                    if (n > 0) {
                        double y = s.y(sum / n, bottom, plotH);
                        double x = xOf(groupMonth / n);
                        if (open) g.lineTo(x, y); else { g.moveTo(x, y); open = true; }
                        drew = true;
                    } else if (group != Integer.MIN_VALUE) {
                        open = false;     // a bucket of nothing breaks the line
                    }
                    sum = 0; n = 0; groupMonth = 0; group = gIdx;
                }
                if (!in) { if (i < months.size() && months.get(i) > window.hi() + bucket) break; continue; }
                double p = i < l.values().length ? plotted(l, i, own == null ? null : own[k]) : Double.NaN;
                if (Double.isNaN(p) || Double.isInfinite(p)) continue;
                sum += p;
                groupMonth += months.get(i);
                n++;
            }
            g.stroke();
        }
        g.setLineDashes((double[]) null);
        g.restore();
        return drew || (stack != null && scale[0] != null);
    }

    private void drawAxes(GraphicsContext g, ChartModel.Scale[] scale, double bottom) {
        g.setFont(Palette.Fonts.monoFont(main ? 10.5 : 10));
        for (int side = 0; side < 2; side++) {
            ChartModel.Scale s = scale[side];
            if (s == null) continue;
            Axis a = side == 0 ? left : right;
            // An axis carrying one line is read in that line's colour.
            String ink = Palette.TEXT_3;
            int on = 0;
            for (Line l : lines) if (l.side() == side && shown(l) && !squashed) { on++; ink = l.colour(); }
            if (on != 1) ink = Palette.TEXT_3;
            g.setFill(Color.web(ink));
            g.setTextAlign(side == 0 ? TextAlignment.RIGHT : TextAlignment.LEFT);
            g.setTextBaseline(VPos.CENTER);
            double x = side == 0 ? plotLeft - 7 : plotRight + 7;
            for (double t : s.ticks()) {
                String text = squashed ? String.format("%.0f", t)
                        : a == null ? trim(t)
                        : log ? a.tick().apply(Math.pow(10, t), Math.pow(10, t)) : a.tick().apply(t, s.step());
                g.fillText(text, x, s.y(t, bottom, plotH));
            }
        }
        g.setTextBaseline(VPos.BASELINE);
        g.setTextAlign(TextAlignment.LEFT);
    }

    private void drawTime(GraphicsContext g, List<ChartModel.Tick> ticks, double bottom) {
        g.setFont(Palette.Fonts.monoFont(10.5));
        g.setTextAlign(TextAlignment.CENTER);
        g.setStroke(Color.web(Palette.TEXT_3));
        g.setLineWidth(1);
        double lastEnd = -1e9;
        for (ChartModel.Tick t : ticks) {
            double x = xOf(t.month());
            if (x < plotLeft - .5 || x > plotRight + .5) continue;
            double xr = Math.floor(x) + .5;
            g.strokeLine(xr, bottom, xr, bottom + (t.year() ? 5 : 3));
            double w = textWidth(t.label(), g.getFont());
            double from = Math.max(plotLeft - AXIS_W + 4, Math.min(x - w / 2, width - w - 2));
            if (from < lastEnd + 6) continue;
            g.setFill(Color.web(t.year() ? Palette.TEXT_2 : Palette.TEXT_3));
            g.fillText(t.label(), from + w / 2, bottom + 16);
            lastEnd = from + w;
        }
        g.setTextAlign(TextAlignment.LEFT);
    }

    /**
     * An episode's colour: a crisis or a depression is bad news, the rest are
     * a watch - verdicts, which is what these are. The rule is the model's
     * since 0.7.37 (YearBook.isSevere()), so City History's RUNNING NOW leads
     * with what this draws red; public for the screens that name episodes.
     */
    public static String episodeColour(String kind) {
        return YearBook.isSevere(kind) ? Palette.BAD : Palette.WARN;
    }

    private void drawEpisodes(GraphicsContext g) {
        double y0 = episodesTop();
        g.setFont(Palette.Fonts.sansFont(10));
        g.setFill(Color.web(Palette.TEXT_3));
        g.setTextAlign(TextAlignment.RIGHT);
        g.fillText("episodes", plotLeft - 8, y0 + 12);
        g.setTextAlign(TextAlignment.LEFT);
        g.save();
        g.beginPath();
        g.rect(plotLeft, y0 - 1, plotW(), laneRows() * EPISODE_ROW + 2);
        g.clip();
        g.setFont(Palette.Fonts.sansStrongFont(10));
        for (int i = 0; i < episodes.size(); i++) {
            YearBook.Episode e = episodes.get(i);
            double a = e.fromMonth() - .5, z = e.toMonth() + .5;
            if (z < window.lo() || a > window.hi()) continue;
            double xa = Math.max(plotLeft, xOf(a)), xz = Math.min(plotRight, xOf(z));
            double y = y0 + lanes[i] * EPISODE_ROW;
            Color c = Color.web(episodeColour(e.kind()));
            boolean lit = pinned == e || (pinned == null && hovered() == e);
            g.setFill(c.deriveColor(0, 1, 1, lit ? .38 : .2));
            g.fillRoundRect(xa, y + 1, Math.max(2, xz - xa), EPISODE_ROW - 3, 6, 6);
            g.setStroke(c);
            g.setLineWidth(1);
            g.strokeRoundRect(xa + .5, y + 1.5, Math.max(1, xz - xa - 1), EPISODE_ROW - 4, 6, 6);
            String name = fit(e.name(), xz - xa - 10, g.getFont());
            if (!name.isEmpty()) {
                g.setFill(c.deriveColor(0, 1, 1.15, 1));
                g.fillText(name, xa + 6, y + 12.5);
            }
        }
        g.restore();
    }

    /** A flag's colour: the area its decision is about - money blue, people teal, building pink. Never a verdict. */
    static String flagColour(String kind) {
        return switch (kind) {
            case DecisionLog.PROMISE -> Palette.PEOPLE;
            case DecisionLog.CONSTRUCTION -> Palette.BUILDING;
            default -> Palette.MONEY;
        };
    }

    private List<ChartModel.Cluster> clusters() {
        return ChartModel.clusters(flags, window.lo(), window.hi(), plotW(), FLAG_GAP);
    }

    private void drawFlags(GraphicsContext g) {
        double y0 = flagsTop();
        // A small chart's lane is the big chart's drawing at its own height: r is 1 on the big one (0.7.38).
        double r = flagRow() / FLAG_ROW, type = main ? 10 : 9;
        g.setFont(Palette.Fonts.sansFont(type));
        g.setFill(Color.web(Palette.TEXT_3));
        g.setTextAlign(TextAlignment.RIGHT);
        g.fillText("you", plotLeft - 8, y0 + 15 * r);
        g.setTextAlign(TextAlignment.LEFT);
        List<ChartModel.Cluster> cs = clusters();
        Object lit = pinned != null ? pinned : hovered();
        for (int k = 0; k < cs.size(); k++) {
            ChartModel.Cluster c = cs.get(k);
            double x = Math.floor(xOf(c.month())) + .5;
            String kind = c.flags().get(0).entries().get(0).kind();
            Color ink = Color.web(flagColour(kind));
            boolean on = lit instanceof ChartModel.Cluster lc && lc.month() == c.month();
            if (on) {
                // The flag's month up through the plot, for the one in hand.
                g.setStroke(ink.deriveColor(0, 1, 1, .7));
                g.setLineDashes(3, 3);
                g.strokeLine(x, top, x, y0 + 2);
                g.setLineDashes((double[]) null);
            }
            g.setStroke(ink);
            g.setFill(ink);
            g.setLineWidth(1.5);
            g.strokeLine(x, y0 + 3, x, y0 + flagRow() - 3);
            g.fillPolygon(new double[] {x, x + 10 * r, x}, new double[] {y0 + 3, y0 + 3 + 4 * r, y0 + 3 + 8 * r}, 3);
            double textX = x + 13 * r;
            double nextX = k + 1 < cs.size() ? xOf(cs.get(k + 1).month()) : plotRight + AXIS_W;
            g.setFont(Palette.Fonts.sansFont(type));
            if (c.count() > 1) {
                String n = String.valueOf(c.count());
                g.setFill(ink);
                g.setFont(Palette.Fonts.monoFont(type));
                g.fillText(n, textX, y0 + 3 + 8 * r);
                textX += textWidth(n, g.getFont()) + 6;
            } else {
                String label = fit(c.flags().get(0).entries().get(0).label(), nextX - textX - 8, g.getFont());
                if (!label.isEmpty()) {
                    g.setFill(ink.deriveColor(0, 1, 1, on ? 1 : .85));
                    g.fillText(label, textX, y0 + 3 + 8 * r);
                }
            }
        }
    }

    private void drawCrosshair(GraphicsContext g, ChartModel.Scale[] scale, double[][] own, double bottom) {
        if (!inPlot(hoverX, hoverY) || months.isEmpty() || dragging) return;
        int i = ChartModel.nearest(months, monthAt(hoverX));
        if (i < 0) return;
        double x = Math.floor(xOf(months.get(i))) + .5;
        if (x < plotLeft || x > plotRight) return;
        g.setStroke(Color.web(Palette.TEXT, .6));
        g.setLineWidth(1);
        g.strokeLine(x, top, x, bottom);
        for (int k = 0; k < lines.size(); k++) {
            Line l = lines.get(k);
            ChartModel.Scale s = squashed ? scale[0] : scale[l.side()];
            if (!shown(l) || s == null || i >= l.values().length) continue;
            double p = plotted(l, i, own == null ? null : own[k]);
            if (Double.isNaN(p) || Double.isInfinite(p)) continue;
            double y = s.y(p, bottom, plotH);
            g.setFill(Color.web(Palette.STAGE));
            g.fillOval(x - 4, y - 4, 8, 8);
            g.setStroke(Color.web(l.colour()));
            g.setLineWidth(2);
            g.strokeOval(x - 4, y - 4, 8, 8);
        }
    }

    /* ---------------------------- the overview ---------------------------- */

    private double stripX(double month) {
        return ChartModel.x(month, window.first(), window.last(), 0, strip.getWidth());
    }

    private double stripMonth(double x) {
        return ChartModel.month(x, window.first(), window.last(), 0, strip.getWidth());
    }

    private void drawStrip() {
        double w = strip.getWidth(), h = strip.getHeight();
        GraphicsContext g = strip.getGraphicsContext2D();
        g.clearRect(0, 0, w, h);
        g.setFill(Color.web(Palette.STAGE));
        g.fillRoundRect(0, 0, w, h, 8, 8);
        double lo = window.first(), hi = window.last();
        if (!(hi > lo)) return;
        // The recessions, faintly, over the whole run.
        g.setFill(Color.web(Palette.TEXT_2, .08));
        for (YearBook.Band b : bands) {
            double xa = stripX(b.fromMonth() - .5), xz = stripX(b.toMonth() + .5);
            g.fillRect(xa, 0, Math.max(1, xz - xa), h);
        }
        // The first line shown, over the whole history, on its own scale.
        Line first = null;
        for (Line l : lines) if (shown(l)) { first = l; break; }
        double ground = h - 16;
        if (first != null) {
            double[] r = new double[] {Double.MAX_VALUE, -Double.MAX_VALUE};
            for (int i = 0; i < months.size() && i < first.values().length; i++) {
                double v = first.values()[i];
                if (Double.isNaN(v)) continue;
                double p = first.toPlot().applyAsDouble(v);
                r[0] = Math.min(r[0], p);
                r[1] = Math.max(r[1], p);
            }
            if (r[1] >= r[0]) {
                double span = r[1] > r[0] ? r[1] - r[0] : 1;
                Color c = Color.web(first.colour());
                int bucket = ChartModel.bucket(hi - lo, w);
                List<double[]> pts = new ArrayList<>();
                double sum = 0, at = 0;
                int n = 0, group = Integer.MIN_VALUE;
                for (int i = 0; i <= months.size(); i++) {
                    int gIdx = i < months.size() ? Math.floorDiv(months.get(i), bucket) : Integer.MAX_VALUE;
                    if (gIdx != group) {
                        if (n > 0) pts.add(new double[] {stripX(at / n), 4 + (ground - 6) * (1 - (sum / n - r[0]) / span)});
                        sum = 0; at = 0; n = 0; group = gIdx;
                    }
                    if (i >= months.size() || i >= first.values().length || Double.isNaN(first.values()[i])) continue;
                    sum += first.toPlot().applyAsDouble(first.values()[i]);
                    at += months.get(i);
                    n++;
                }
                if (pts.size() >= 2) {
                    g.setFill(c.deriveColor(0, 1, 1, .12));
                    g.beginPath();
                    g.moveTo(pts.get(0)[0], ground);
                    for (double[] p : pts) g.lineTo(p[0], p[1]);
                    g.lineTo(pts.get(pts.size() - 1)[0], ground);
                    g.closePath();
                    g.fill();
                    g.setStroke(c);
                    g.setLineWidth(1.4);
                    g.beginPath();
                    g.moveTo(pts.get(0)[0], pts.get(0)[1]);
                    for (double[] p : pts) g.lineTo(p[0], p[1]);
                    g.stroke();
                }
            }
        }
        // The years along its foot.
        g.setFont(Palette.Fonts.monoFont(9.5));
        g.setFill(Color.web(Palette.TEXT_3));
        g.setTextAlign(TextAlignment.CENTER);
        double lastEnd = -1e9;
        for (ChartModel.Tick t : ChartModel.timeTicks(lo, hi, w)) {
            double x = stripX(t.month());
            double tw = textWidth(t.label(), g.getFont());
            double from = Math.max(2, Math.min(x - tw / 2, w - tw - 2));
            if (from < lastEnd + 8) continue;
            g.fillText(t.label(), from + tw / 2, h - 4);
            lastEnd = from + tw;
        }
        g.setTextAlign(TextAlignment.LEFT);
        // Outside the window dimmed; the window framed, with a handle at each edge.
        double xl = stripX(window.lo()), xr = stripX(window.hi());
        g.setFill(Color.web(Palette.STAGE, .55));
        g.fillRect(0, 0, Math.max(0, xl), h);
        g.fillRect(xr, 0, Math.max(0, w - xr), h);
        g.setStroke(Color.web(Palette.TEXT));
        g.setLineWidth(1.5);
        g.strokeRoundRect(xl, 1, Math.max(2, xr - xl), h - 2, 6, 6);
        g.setFill(Color.web(Palette.TEXT));
        g.fillRoundRect(xl - 3, h / 2 - 9, 6, 18, 3, 3);
        g.fillRoundRect(xr - 3, h / 2 - 9, 6, 18, 3, 3);
    }

    /* =====================================================================
       WHAT IS UNDER THE POINTER, AND THE CARD
       ===================================================================== */

    private boolean inPlot(double x, double y) {
        return !Double.isNaN(x) && x >= plotLeft && x <= plotRight && y >= top && y <= plotBottom();
    }

    /** The band, the episode or the flag under the pointer, or null. */
    private Object hovered() { return itemAt(hoverX, hoverY); }

    private Object itemAt(double x, double y) {
        if (Double.isNaN(x) || x < plotLeft || x > plotRight) return null;
        double m = monthAt(x);
        if (main && y >= 0 && y <= plotBottom()) {
            for (YearBook.Band b : bands) if (m >= b.fromMonth() - .5 && m <= b.toMonth() + .5) return b;
            return null;
        }
        if (!main && y >= top && y <= plotBottom()) {
            for (YearBook.Band b : bands) if (m >= b.fromMonth() - .5 && m <= b.toMonth() + .5) return b;
            return null;
        }
        if (!main && !flagLane()) return null;
        double e0 = episodesTop();
        if (main && y >= e0 && y < e0 + laneRows() * EPISODE_ROW) {
            int row = (int) ((y - e0) / EPISODE_ROW);
            for (int i = 0; i < episodes.size(); i++) {
                YearBook.Episode e = episodes.get(i);
                if (lanes[i] == row && m >= e.fromMonth() - .5 && m <= e.toMonth() + .5) return e;
            }
            return null;
        }
        double f0 = flagsTop();
        if (y >= f0 && y <= f0 + flagRow()) {
            ChartModel.Cluster best = null;
            double bestD = FLAG_GAP;
            for (ChartModel.Cluster c : clusters()) {
                double d = x - xOf(c.month());
                if (d >= -6 && d < bestD) { best = c; bestD = d; }
            }
            return best;
        }
        return null;
    }

    /** Fills the card for what is pinned, or else what is under the pointer; hides it when there is nothing. */
    private void fillCard(ChartModel.Scale[] scale, double[][] own) {
        card.getChildren().clear();
        Object item = pinned;
        double at = pinnedX;
        boolean crosshair = false;
        if (item == null && !dragging && !Double.isNaN(hoverX)) {
            item = hovered();
            at = hoverX;
            crosshair = inPlot(hoverX, hoverY);
        }
        if (crosshair) {
            crosshairCard((item instanceof YearBook.Band b) ? b : null);
        } else if (item instanceof YearBook.Band b) {
            bandCard(b);
        } else if (item instanceof YearBook.Episode e) {
            episodeCard(e);
        } else if (item instanceof ChartModel.Cluster c) {
            flagCard(c);
        }
        if (card.getChildren().isEmpty()) {
            card.setVisible(false);
            return;
        }
        if (pinned != null) card.getChildren().add(caption("Click anywhere else to close.", Palette.TEXT_SPENT));
        card.setVisible(true);
        card.applyCss();
        double w = Math.min(CARD_W, card.prefWidth(-1)), h = card.prefHeight(w);
        card.resize(w, h);
        double x = at + 14 + w <= width ? at + 14 : at - 14 - w;
        double y = top + 8;
        if (item instanceof YearBook.Episode) y = Math.max(0, episodesTop() - h - 6);
        if (item instanceof ChartModel.Cluster) y = Math.max(0, flagsTop() - h - 6);
        card.relocate(Math.max(0, x), y);
    }

    private static Label caption(String text, String colour) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setMaxWidth(CARD_W - 20);
        l.setStyle(Palette.words(Palette.SIZE_LABEL, colour));
        return l;
    }

    private static Label head(String text) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setMaxWidth(CARD_W - 20);
        l.setStyle(Palette.Fonts.sansSemiBold() + " -fx-font-size: " + Palette.SIZE_BODY + "px; -fx-text-fill: " + Palette.TEXT + ";");
        return l;
    }

    private static HBox row(String colour, String name, String value) {
        Region dot = new Region();
        dot.setMinSize(8, 8);
        dot.setPrefSize(8, 8);
        dot.setMaxSize(8, 8);
        dot.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 4;");
        Label n = new Label(name);
        n.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_2));
        n.setMinWidth(Region.USE_PREF_SIZE);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label v = new Label(value);
        v.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT));
        v.setMinWidth(Region.USE_PREF_SIZE);
        HBox r = new HBox(6, dot, n, gap, v);
        r.setAlignment(Pos.CENTER_LEFT);
        r.setMinWidth(180);
        return r;
    }

    /** The folded years at the front of the months this chart is handed (0.7.55; HistorySave.yearlyPoints()). */
    void setYearly(int years) { yearly = Math.max(0, years); }

    private void crosshairCard(YearBook.Band in) {
        int i = ChartModel.nearest(months, monthAt(hoverX));
        if (i < 0) return;
        int m = months.get(i);
        // A folded year reads as its year: a month's average for a flow, its
        // end for a level, its average for a rate (0.7.55).
        String when = i < yearly ? "the year " + CityCalendar.yearOf(m) + ", kept whole" : CityCalendar.formatShort(m);
        card.getChildren().add(head(when + (in == null ? "" : "  ·  " + in.name())));
        for (Line l : lines) {
            if (!shown(l)) continue;
            double v = i < l.values().length ? l.values()[i] : Double.NaN;
            card.getChildren().add(row(l.colour(), l.label(), Double.isNaN(v) ? "not recorded" : l.reads().apply(v)));
        }
        if (stack != null) {
            for (int p = 0; p < stack.layers().length; p++) {
                double v = ChartModel.at(stack.layers()[p], i);
                card.getChildren().add(row(stack.colour(p),
                        "  " + stack.name(p), Double.isNaN(v) ? "not recorded" : stack.reads().apply(v)));
            }
        }
        ChartModel.Mark mark = main ? markAt(hoverX) : null;
        if (mark != null) {
            // A mark under the pointer (0.7.45): what the model did that month, in its own words.
            card.getChildren().add(caption(CityCalendar.formatShort(mark.month()) + ": " + mark.words() + ".", Palette.TEXT_2));
        }
        if (in != null) {
            // The band's rule and its numbers, from the year book's record of it; a click keeps them.
            card.getChildren().add(caption(YearBook.trigger("recession") + ": "
                    + YearBook.worstWords("recession", in.depth()) + " at worst ("
                    + CityCalendar.formatShort(in.depthMonth()) + "), " + months(in.months())
                    + ". Click the band for more.", Palette.TEXT_3));
        }
    }

    private void bandCard(YearBook.Band b) {
        YearBook.Episode e = b.episode();
        String kind = e == null ? "recession" : e.kind();
        card.getChildren().add(head(b.name()));
        card.getChildren().add(caption("Named because " + YearBook.trigger(kind) + ", "
                + YearBook.triggerFooter() + ".", Palette.TEXT_2));
        card.getChildren().add(caption("This band: " + CityCalendar.formatShort(b.fromMonth()) + " – "
                + CityCalendar.formatShort(b.toMonth()) + ", " + months(b.months()) + ".", Palette.TEXT));
        card.getChildren().add(caption("Depth: " + YearBook.worstWords("recession", b.depth())
                + " (" + CityCalendar.formatShort(b.depthMonth()) + ").", Palette.TEXT));
        if (e != null && (e.fromMonth() != b.fromMonth() || e.toMonth() != b.toMonth())) {
            card.getChildren().add(caption("The whole " + (kind.equals("depression") ? "depression" : "recession")
                    + ": " + CityCalendar.formatShort(e.fromMonth()) + " – " + CityCalendar.formatShort(e.toMonth())
                    + ", " + months(e.months()) + "; at worst " + YearBook.worstWords(kind, e.worst())
                    + " (" + CityCalendar.formatShort(e.worstMonth()) + ").", Palette.TEXT_2));
        }
    }

    private void episodeCard(YearBook.Episode e) {
        card.getChildren().add(head(e.name()));
        card.getChildren().add(caption("Named because " + YearBook.trigger(e.kind()) + ", "
                + YearBook.triggerFooter() + ".", Palette.TEXT_2));
        card.getChildren().add(caption(CityCalendar.formatShort(e.fromMonth()) + " – "
                + CityCalendar.formatShort(e.toMonth()) + ", " + months(e.months()) + ".", Palette.TEXT));
        card.getChildren().add(caption((YearBook.worstIsHigh(e.kind()) ? "Peak: " : "Depth: ")
                + YearBook.worstWords(e.kind(), e.worst()) + " (" + CityCalendar.formatShort(e.worstMonth()) + ").",
                Palette.TEXT));
    }

    /** At most this many decisions are listed on a flag's card; the rest are counted. */
    static final int CARD_DECISIONS = 10;

    private void flagCard(ChartModel.Cluster c) {
        // The months its decisions were made in (0.7.37): a founding-month flag sits on the axis's first
        // month (ChartModel.onAxis()), and its card still says when each was decided.
        int first = Integer.MAX_VALUE, last = Integer.MIN_VALUE;
        for (ChartModel.Flag f : c.flags()) {
            for (DecisionLog.Entry e : f.entries()) {
                first = Math.min(first, e.month());
                last = Math.max(last, e.month());
            }
        }
        card.getChildren().add(head(CityCalendar.formatShort(first)
                + (last != first ? " – " + CityCalendar.formatShort(last) : "")
                + "  ·  " + c.count() + (c.count() == 1 ? " decision" : " decisions")));
        int shown = 0;
        for (ChartModel.Flag f : c.flags()) {
            for (DecisionLog.Entry e : f.entries()) {
                if (shown++ >= CARD_DECISIONS) continue;
                Label l = caption((last != first ? CityCalendar.formatShort(e.month()) + "  " : "") + e.label(), Palette.TEXT);
                Region dot = new Region();
                dot.setMinSize(6, 6);
                dot.setMaxSize(6, 6);
                dot.setStyle("-fx-background-color: " + flagColour(e.kind()) + "; -fx-background-radius: 3;");
                HBox r = new HBox(6, dot, l);
                r.setAlignment(Pos.CENTER_LEFT);
                card.getChildren().add(r);
            }
        }
        if (shown > CARD_DECISIONS) {
            card.getChildren().add(caption("and " + (shown - CARD_DECISIONS) + " more - zoom in to part them.",
                    Palette.TEXT_3));
        }
    }

    private static String months(int n) { return n == 1 ? "1 month" : String.format("%,d months", n); }

    /* =====================================================================
       LISTENING: THE DRAG, THE WHEEL, THE CLICK, THE OVERVIEW
       ===================================================================== */

    private void listen() {
        plot.setOnMouseMoved(guarded(e -> {
            hoverX = e.getX();
            hoverY = e.getY();
            Object item = itemAt(hoverX, hoverY);
            plot.setCursor(inPlot(hoverX, hoverY) ? Cursor.OPEN_HAND
                    : item != null ? Cursor.HAND : Cursor.DEFAULT);
            draw();
        }));
        plot.setOnMouseExited(guarded(e -> {
            hoverX = hoverY = Double.NaN;
            draw();
        }));
        plot.setOnMousePressed(guarded(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            pressX = e.getX();
            pressY = e.getY();
            pressLo = window.lo();
            pressHi = window.hi();
        }));
        plot.setOnMouseDragged(guarded(e -> {
            if (Double.isNaN(pressX) || !inPlot(pressX, pressY)) return;
            if (!dragging && Math.abs(e.getX() - pressX) < DRAG_SLOP) return;
            dragging = true;
            plot.setCursor(Cursor.CLOSED_HAND);
            double perPixel = (pressHi - pressLo) / plotW();
            window.setWindow(pressLo - (e.getX() - pressX) * perPixel, pressHi - (e.getX() - pressX) * perPixel);
            hoverX = e.getX();
            hoverY = e.getY();
            moved(false);
        }));
        plot.setOnMouseReleased(guarded(e -> {
            boolean was = dragging;
            dragging = false;
            double px = pressX;
            pressX = Double.NaN;
            if (e.getButton() != MouseButton.PRIMARY || Double.isNaN(px)) return;
            if (was) {
                plot.setCursor(Cursor.OPEN_HAND);
                moved(true);
                return;
            }
            if (e.getClickCount() >= 2 && inPlot(e.getX(), e.getY())) {
                // The double-click: back to the range last picked.
                pinned = null;
                window.reset();
                moved(true);
                return;
            }
            Object item = itemAt(e.getX(), e.getY());
            pinned = item == pinned ? null : item;
            pinnedX = e.getX();
            draw();
        }));
        plot.setOnScroll(guarded(this::wheel));
    }

    private void wheel(ScrollEvent e) {
        double x = e.getX();
        if (e.getSource() == strip) x = xOf(stripMonth(e.getX()));
        if (e.getDeltaY() != 0) {
            window.zoom(Math.pow(ChartModel.ZOOM_STEP, e.getDeltaY() / NOTCH), monthAt(x));
        } else if (e.getDeltaX() != 0) {
            window.pan(-e.getDeltaX() * window.span() / plotW());
        } else {
            return;
        }
        e.consume();
        moved(true);
    }

    private void listenToStrip() {
        strip.setOnMouseMoved(guarded(e -> {
            double xl = stripX(window.lo()), xr = stripX(window.hi());
            strip.setCursor(Math.abs(e.getX() - xl) <= HANDLE || Math.abs(e.getX() - xr) <= HANDLE ? Cursor.H_RESIZE
                    : e.getX() > xl && e.getX() < xr ? Cursor.MOVE : Cursor.HAND);
        }));
        strip.setOnMousePressed(guarded(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            double xl = stripX(window.lo()), xr = stripX(window.hi());
            if (Math.abs(e.getX() - xl) <= HANDLE) stripMode = 2;
            else if (Math.abs(e.getX() - xr) <= HANDLE) stripMode = 3;
            else {
                if (e.getX() <= xl || e.getX() >= xr) {
                    // Outside the window: it jumps to be centred there.
                    window.pan(stripMonth(e.getX()) - (window.lo() + window.hi()) / 2);
                    moved(false);
                }
                stripMode = 1;
            }
            dragging = true;
            pressMonth = stripMonth(e.getX());
            pressLo = window.lo();
            pressHi = window.hi();
        }));
        strip.setOnMouseDragged(guarded(e -> {
            if (stripMode == 0) return;
            double dm = stripMonth(e.getX()) - pressMonth;
            double min = Math.min(ChartModel.MIN_SPAN, window.last() - window.first());
            switch (stripMode) {
                case 1 -> window.setWindow(pressLo + dm, pressHi + dm);
                case 2 -> window.setWindow(Math.min(pressLo + dm, pressHi - min), pressHi);
                default -> window.setWindow(pressLo, Math.max(pressHi + dm, pressLo + min));
            }
            moved(false);
        }));
        strip.setOnMouseReleased(guarded(e -> {
            if (stripMode == 0) return;
            stripMode = 0;
            dragging = false;
            moved(true);
        }));
        strip.setOnScroll(guarded(this::wheel));
    }

    /** The window moved: this chart, the charts that follow it, and - once it rests - the page. */
    private void moved(boolean settled) {
        draw();
        for (Runnable r : followers) r.run();
        if (settled) settle.playFromStart();
    }

    /** Drops a pinned card: going into full screen and coming out of it, Esc's way out included (HistoryScreen.setChartFull()). */
    void unpin() {
        pinned = null;
        draw();
    }

    /* ------------------------------ small helpers ------------------------------ */

    /** One Text node, reused to measure a string's width in a font (textWidth()). */
    private static final javafx.scene.text.Text MEASURE = new javafx.scene.text.Text();

    static double textWidth(String s, Font f) {
        MEASURE.setFont(f);
        MEASURE.setText(s);
        return MEASURE.getLayoutBounds().getWidth();
    }

    /** The text, cut with an ellipsis to fit `room` pixels; empty when not even a few letters fit. */
    static String fit(String text, double room, Font f) {
        if (text == null || room < 18) return "";
        if (textWidth(text, f) <= room) return text;
        for (int n = text.length() - 1; n >= 3; n--) {
            String cut = text.substring(0, n).trim() + "…";
            if (textWidth(cut, f) <= room) return cut;
        }
        return "";
    }

    private static String trim(double v) {
        String s = String.format("%.2f", v);
        s = s.replaceAll("0+$", "");
        return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
    }
}
