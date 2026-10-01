package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ham.citybuildersim.ui.Palette.Fonts;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Levers.*;
import javafx.scene.layout.Priority;

/**
 * The small pieces of text and layout every screen is made from: a sentence,
 * an alert, a sub-heading, a grid and its cells, a chip strip, a vitals bar, a
 * stacked bar, a limit cell - and, under the divider half way down, the
 * helpers two screens turned out to share: the trend chart, the swatch, the
 * step chip, the keyed bar, the (i) and its popover (0.7.21), the payer row,
 * and the tile the build menu and the land office both lay out three across.
 *
 * Static for the same reason as Money and Statement: no state, used
 * everywhere, called unqualified through an import static. The scroller is
 * not here: scrolled() reads the shell's current screen, so it stayed there.
 */
public final class Pieces {

    /** One figure in the constraints bar: what it is, what it reads, what it means. */
    public static VBox limitCell(String label, String value, String note, String tone) {
        return limitCell(label, value, note, tone, null, null);
    }

    /* =====================================================================
       A FIGURE THAT GOES WHERE IT IS DECIDED.

       Jerus: "in the finances tab if you click on the rate at the top, it just
       takes you directly to where the rates are made"; and "in the building
       section, if you click the land on the top, make it so that it takes you
       to the land office."

       Both are the same observation. These bars are the constraints a screen is
       working under, and a constraint is nearly always set SOMEWHERE ELSE - the
       rate on the Policies dial, the free land at the land office. The player
       reads the number, decides to do something about it, and then has to go
       and find the screen that owns it. The number already knows.

       The affordance is a chevron on the caption rather than a button: these
       are readings first and doors second, and four buttons in a row across the
       top of a screen would read as a toolbar. Hover fills the cell so it is
       obvious which of them are live, and the tooltip says where it goes rather
       than making the player click to find out.
       ===================================================================== */
    public static VBox limitCell(String label, String value, String note, String tone,
                           String where, Runnable go) {

        Label what = new Label(go == null ? label : label + "  \u203a");
        what.setStyle(Palette.words(Palette.SIZE_CAPTION,
                go == null ? Palette.TEXT_LABEL : Palette.ACCENT));

        Label figure = new Label(value);
        figure.setStyle(Palette.figure(Palette.SIZE_SECTION, tone));

        // WRAPPED, not cut (0.7.20): the Bank strip's "over the last 6 months;
        // its owners want 12.0%" ended "its owners wan..." at the cell's edge.
        // The cell keeps its width; a long note takes a second line.
        Label says = new Label(note);
        says.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        says.setWrapText(true);
        says.setMaxWidth(LIMIT_CELL - 28);

        VBox cell = new VBox(0, what, figure, says);
        cell.setAlignment(Pos.CENTER_LEFT);
        cell.setPrefWidth(LIMIT_CELL);

        String rest = "-fx-padding: 0 14 0 14;"
                + " -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 1 0 0;";
        cell.setStyle(rest);

        if (go != null) {
            cell.setStyle(rest + " -fx-cursor: hand;");
            cell.setOnMouseEntered(e -> cell.setStyle(rest + " -fx-cursor: hand;"
                    + " -fx-background-color: " + Palette.CONTROL + ";"));
            cell.setOnMouseExited(e -> cell.setStyle(rest + " -fx-cursor: hand;"));
            cell.setOnMouseClicked(e -> go.run());
            Tooltip tip = new Tooltip(where);
            tip.setShowDelay(Duration.millis(250));
            Tooltip.install(cell, tip);
        }
        return cell;
    }

    /** How wide a limit cell is, its padding included; its note wraps inside it. */
    static final double LIMIT_CELL = 190;

    /**
     * A row of chips, which is the build strip's shape at two sizes.
     *
     * A FlowPane rather than an HBox for the reason the build strip is one:
     * these wrap rather than clip on a small window, and the one thing a
     * navigation strip must never do is hide an entry.
     */
    public static javafx.scene.layout.FlowPane chipStrip(String[] names, String current,
                                                   int size, Consumer<String> pick) {

        javafx.scene.layout.FlowPane strip = new javafx.scene.layout.FlowPane(6, 6);
        strip.setAlignment(Pos.CENTER);
        strip.setPrefWrapLength(Palette.BUILD_ROW + 160);

        for (String name : names) {
            boolean on = name.equals(current);
            Button chip = new Button(name);
            chip.setStyle(Palette.words(size, on ? Palette.TEXT_HEAD : Palette.TEXT_BODY)
                    + " -fx-background-color: " + (on ? Palette.RAISED : Palette.CONTROL) + ";"
                    + " -fx-background-radius: " + Palette.RADIUS_TIGHT + ";"
                    + " -fx-border-color: " + (on ? Palette.ACCENT : "transparent") + ";"
                    + " -fx-border-width: 0 0 2 0; -fx-cursor: hand;");
            chip.setOnAction(e -> pick.accept(name));
            strip.getChildren().add(chip);
        }
        return strip;
    }

    /** The bar itself: four cells, the last one without its dividing rule. */
    public static HBox vitalsBar(VBox... cells) {
        if (cells.length > 0) {
            cells[cells.length - 1].setStyle("-fx-padding: 0 14 0 14;");
        }
        HBox bar = new HBox(0, cells);
        bar.setAlignment(Pos.CENTER);
        bar.setMaxWidth(Region.USE_PREF_SIZE);
        bar.setStyle("-fx-padding: 8 6 8 6;" + Palette.block(Palette.PANEL));
        return bar;
    }

    /** A paragraph in the statement column: wrapped, at body size, in a tone. */
    public static Label sentence(String text, String tone) {
        Label line = new Label(text);
        line.setWrapText(true);
        line.setMaxWidth(STATEMENT);
        line.setPrefWidth(STATEMENT);
        line.setStyle(Palette.words(Palette.SIZE_BODY, tone) + " -fx-padding: 6 0 4 0;");
        return line;
    }

    /** A heading inside a section, for a table that needs naming. */
    public static Label subHead(String text) {
        Label head = new Label(text);
        head.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED)
                + " -fx-font-weight: bold; -fx-padding: 12 0 2 0;");
        return head;
    }

    /**
     * Something that is going wrong, in a block of its own.
     *
     * Replaces criticalSection's box of monospaced lines. Same job - a red
     * ground, a heading, and what to do about it - written as a sentence,
     * because the old one was hand-wrapped at fifty characters and every one of
     * its lines had to be counted by whoever wrote it.
     */
    public static VBox alert(String heading, String body) {
        Label head = new Label(heading.toUpperCase());
        head.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.BAD)
                + " -fx-font-weight: bold;");

        Label says = new Label(body);
        says.setWrapText(true);
        says.setMaxWidth(STATEMENT - 30);
        says.setStyle(Palette.words(Palette.SIZE_BODY, Palette.BAD_TEXT));

        VBox box = new VBox(4, head, says);
        box.setMaxWidth(STATEMENT);
        box.setPrefWidth(STATEMENT);
        box.setStyle("-fx-padding: 10 12 10 12;"
                + Palette.block(Palette.ALERT_GROUND, Palette.ALERT_EDGE));
        return box;
    }

    /** A table with fixed columns, each aligned the way its figures want. */
    public static javafx.scene.layout.GridPane grid(double[] widths, javafx.geometry.HPos[] align) {
        javafx.scene.layout.GridPane table = new javafx.scene.layout.GridPane();
        table.setHgap(6);
        table.setVgap(2);
        for (int i = 0; i < widths.length; i++) {
            javafx.scene.layout.ColumnConstraints spec =
                    new javafx.scene.layout.ColumnConstraints(widths[i]);
            spec.setHalignment(align[i]);
            table.getColumnConstraints().add(spec);
        }
        table.setStyle("-fx-padding: 2 0 8 0;");
        return table;
    }

    /** Row zero: the column names, in the label grey, aligned as their column is. */
    public static void gridHead(javafx.scene.layout.GridPane table, String... names) {
        for (int i = 0; i < names.length; i++) {
            boolean right = table.getColumnConstraints().get(i).getHalignment()
                    == javafx.geometry.HPos.RIGHT;
            table.add(gridCell(names[i], Palette.TEXT_LABEL,
                    Palette.SIZE_CAPTION, right), i, 0);
        }
    }

    /** Right-hand columns are all the same width in every table on this screen. */
    public static javafx.geometry.HPos[] rightAfterFirst(int columns) {
        javafx.geometry.HPos[] align = new javafx.geometry.HPos[columns];
        align[0] = javafx.geometry.HPos.LEFT;
        for (int i = 1; i < columns; i++) align[i] = javafx.geometry.HPos.RIGHT;
        return align;
    }

    /** One cell of a table: a figure right, a name left. */
    public static Label gridCell(String text, String tone, int size, boolean rightAlign) {
        Label cell = new Label(text);
        cell.setStyle(rightAlign
                ? Palette.figureRegular(size, tone)
                : Palette.words(size, tone));
        cell.setMaxWidth(Double.MAX_VALUE);
        cell.setAlignment(rightAlign ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return cell;
    }

    public static Label monoLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family: " + Palette.mono() + ";");
        return label;
    }

    /** A named amount, with the colour it was assigned and what it opens into. */
    public record Slice(String name, double amount, String colour) { }

    /**
     * A part-to-whole bar, which is the right form when the parts can be
     * negative and a ring cannot be drawn at all.
     *
     * Used for GDP: net exports can be a subtraction, and a pie of a negative
     * wedge is nonsense.
     */
    public static VBox stackedBar(java.util.List<Slice> parts, double width) {

        double total = 0;
        for (Slice s : parts) total += Math.abs(s.amount());

        HBox bar = new HBox(2);
        bar.setMaxWidth(width);
        for (Slice s : parts) {
            double wide = total > 0 ? Math.abs(s.amount()) / total * (width - 2 * parts.size()) : 0;
            Region block = new Region();
            block.setMinSize(Math.max(1, wide), 18);
            block.setPrefSize(Math.max(1, wide), 18);
            block.setMaxSize(Math.max(1, wide), 18);
            block.setStyle("-fx-background-color: " + s.colour() + ";"
                    + " -fx-background-radius: 2;");
            Tooltip tip = new Tooltip(s.name() + "\n"
                    + tightMoney(toDollars(s.amount())));
            tip.setShowDelay(Duration.millis(200));
            Tooltip.install(block, tip);
            bar.getChildren().add(block);
        }

        VBox box = new VBox(4, bar);
        box.setMaxWidth(width);
        box.setStyle("-fx-padding: 4 0 6 0;");
        return box;
    }

    /* ---------------------------------------------------------------------
       THE HELPERS THE SCREENS SHARE WITH EACH OTHER, moved 2026-09-18 (second pass):
       found when the bank screen turned out to call the finances screen's swatch.
       --------------------------------------------------------------------- */

    /** Show or hide an overlay without it still taking up its space. */
    public static void showIf(javafx.scene.Node node, boolean visible) {
        if (node == null) return;
        node.setVisible(visible);
        node.setManaged(visible);
    }

    /** A count, or a dot where there is nothing - a grid of zeros reads as data. */
    public static String cell(double value) {
        return value >= .5 ? formatter.format(value) : ".";
    }

    /**
     * Pay tiers, shortened to fit six across.
     *
     * "Senior professional" is nineteen characters and the column is nine, so
     * something has to give. Cutting to the distinguishing word keeps the six
     * readable as a ladder, which is what the column is for.
     */
    public static String shortTier(PayTier tier) {
        return switch (tier) {
            case UNSKILLED           -> "unskilled";
            case SKILLED             -> "skilled";
            case COLLEGE             -> "college";
            case PROFESSIONAL        -> "prof";
            case SENIOR_PROFESSIONAL -> "sr prof";
            case ELITE               -> "elite";
        };
    }

    /**
     * A monthly flow of people, in whole people (0.7.20).
     *
     * It kept a decimal under ten, so that a village of two hundred would not
     * print "0 born" every month - and printed "+0.1 born", "Moved in 8.9" and,
     * with a sign put in front of it, "Died -0.0". Nobody is a tenth of a
     * person, and Money.people() already said so for every count. So a flow
     * is whole people too, and a flow that is happening but rounds to none
     * reads "under 1" - the form the screens already use for a share too small
     * to print ("under 1%") and a build too quick to count ("under a month") -
     * so the only thing moving is still not rounded away. The pyramid keeps
     * its fractions; only the display rounds.
     */
    public static String flowText(double value) {
        double a = Math.abs(value);
        if (a > 0 && a < .5) return "under 1";
        return formatter.format(Math.round(value));
    }

    /**
     * The same with its direction in front - "+12", "-3" - and none on a flow
     * that reads "0" or "under 1", which have no direction to give.
     */
    public static String flowSigned(String sign, double value) {
        String text = flowText(value);
        return Math.round(Math.abs(value)) == 0 ? text : sign + text;
    }

    /**
     * A small line chart, in the statement's own language.
     *
     * NOT City History's big chart. That one gives each unit its own axis and,
     * past two units, draws every line across its own low-to-high so
     * unrelated series can share one (HistoryScreen); here the whole point
     * of drawing the book next to the capacity is that they are the same units
     * and one of them is above the other. So: ONE scale for every line on the
     * chart, and the axis labelled with the actual figures.
     *
     * Which means every series passed in together has to BE in the same units.
     * There is no flag for it - a flag would only let a caller declare that two
     * incomparable series are comparable, which is the mistake rather than the
     * guard against it.
     *
     * AND THE YEARS UNDER IT (0.7.23): `axis` is the history's axis the
     * series are aligned to - h.getMonth(), or the same slice of it - and the
     * chart draws ChartModel's year ticks under the plot and a faint line up
     * it at each. It had no time axis at all; "the last 280 months" was the
     * only clue to when anything happened.
     */
    public static VBox trendChart(List<Integer> axis, String[] names, double[][] series, String[] colours) {
        return trendChart(axis, names, series, colours, null);
    }

    /**
     * ...with the figures written by `figure` rather than by the first
     * series' name (chartFigure()): for a chart whose unit no name says - a
     * rate, a ratio. Null is chartFigure(). Added for the bank's rates and
     * capital (0.7.9); the one-scale rule is unchanged.
     */
    public static VBox trendChart(List<Integer> axis, String[] names, double[][] series, String[] colours,
                                  java.util.function.DoubleFunction<String> figure) {

        final double WIDE = STATEMENT - 40;
        final double TALL = 130;

        /* --------------------- where the recording starts ---------------------
         *
         * A SERIES ADDED LATER IS NOT A SERIES THAT WAS ZERO, and drawing it
         * from the beginning of the city says it was. HistorySave.aligned()
         * pads the front with NaN for exactly that reason, and the first build
         * of this chart then spread five recorded months across two hundred
         * and eighty of axis - three invisible dashes hard against the right
         * edge, on a chart that looked simply broken.
         *
         * So the axis starts where the recording does. What is lost is the
         * ability to see that the series is younger than the city, and the
         * caption under the chart says so instead.
         * --------------------------------------------------------------------- */
        int months = 0;
        int from = Integer.MAX_VALUE;
        for (double[] one : series) {
            months = Math.max(months, one.length);
            for (int i = 0; i < one.length; i++) {
                if (!Double.isNaN(one[i])) { from = Math.min(from, i); break; }
            }
        }
        if (from == Integer.MAX_VALUE) {
            return new VBox(statementNote("Nothing recorded for this yet."));
        }
        int points = months - from;

        /* ----------------------------- the scale ----------------------------- */
        double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE;
        for (double[] one : series) {
            for (int i = from; i < one.length; i++) {
                if (Double.isNaN(one[i])) continue;
                lo = Math.min(lo, one[i]);
                hi = Math.max(hi, one[i]);
            }
        }
        if (points < 2 || hi <= -Double.MAX_VALUE) {
            return new VBox(statementNote(String.format(
                    "Only %d month%s of this has been recorded. A line needs two points.",
                    points, points == 1 ? "" : "s")));
        }
        // Zero belongs on a money chart: a book that halved should look halved,
        // and it does not on an axis that starts at its own minimum.
        lo = Math.min(0, lo);
        if (hi <= lo) hi = lo + 1;
        // ...AND A LITTLE AIR ABOVE THE PEAK, so the line never runs along the
        // top edge and a spike is not half-clipped by it. It also leaves the
        // corner free for the axis label, which was otherwise drawn straight
        // through whichever series happened to be highest on the left.
        double peak = hi;
        hi = lo + (hi - lo) * 1.12;

        javafx.scene.layout.Pane plot = new javafx.scene.layout.Pane();
        plot.setPrefSize(WIDE, TALL);
        plot.setMinSize(WIDE, TALL);
        plot.setMaxSize(WIDE, TALL);
        plot.setStyle(Palette.block(Palette.PANEL));

        /* ------------------------------ the grid ------------------------------ */
        for (int g = 1; g <= 3; g++) {
            javafx.scene.shape.Line rule = new javafx.scene.shape.Line(
                    0, TALL * g / 4.0, WIDE, TALL * g / 4.0);
            rule.setStroke(javafx.scene.paint.Color.web(Palette.CONTROL));
            rule.setStrokeWidth(1);
            plot.getChildren().add(rule);
        }

        /* ------------------------ the years (0.7.23) ------------------------ */
        // The months the drawn part covers, first to last; a point's x is its
        // place between them, as the lines below put it.
        int firstMonth = axis != null && from < axis.size() ? axis.get(from) : from;
        int lastMonth = axis != null && axis.size() >= points + from ? axis.get(from + points - 1)
                : firstMonth + points - 1;
        javafx.scene.canvas.Canvas years = new javafx.scene.canvas.Canvas(WIDE, 15);
        javafx.scene.canvas.GraphicsContext yg = years.getGraphicsContext2D();
        yg.setFont(Palette.Fonts.monoFont(9.5));
        yg.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        double labelEnd = -1e9;
        for (ChartModel.Tick t : ChartModel.timeTicks(firstMonth, lastMonth, WIDE)) {
            double x = ChartModel.x(t.month(), firstMonth, lastMonth, 0, WIDE);
            if (t.year()) {
                javafx.scene.shape.Line up = new javafx.scene.shape.Line(Math.floor(x) + .5, 0, Math.floor(x) + .5, TALL);
                up.setStroke(javafx.scene.paint.Color.web(Palette.CONTROL));
                up.setStrokeWidth(1);
                plot.getChildren().add(up);
            }
            double w = TimeChart.textWidth(t.label(), yg.getFont());
            double left = Math.max(0, Math.min(x - w / 2, WIDE - w));
            if (left < labelEnd + 6) continue;
            yg.setFill(javafx.scene.paint.Color.web(t.year() ? Palette.TEXT_2 : Palette.TEXT_3));
            yg.fillText(t.label(), left + w / 2, 11);
            labelEnd = left + w;
        }

        /* ------------------------------ the lines ------------------------------ */
        for (int s = 0; s < series.length; s++) {

            double[] one = series[s];
            javafx.scene.shape.Polyline line = new javafx.scene.shape.Polyline();
            /*
             * FILL NULL. A JavaFX Polyline is a Shape and a Shape's default
             * fill is BLACK, so an unfilled-looking line paints the whole area
             * under itself. On the dark panel (Palette.PANEL) that is invisible
             * and wrong at the same time.
             */
            line.setFill(null);
            line.setStroke(javafx.scene.paint.Color.web(colours[s]));
            line.setStrokeWidth(2);
            line.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);

            // One point per pixel column at most - a five-thousand-month city
            // would otherwise put four thousand vertices on a six-hundred-pixel
            // line, which is slower and no more legible.
            int step = Math.max(1, points / (int) WIDE);
            for (int i = from; i < one.length; i += step) {
                if (Double.isNaN(one[i])) continue;
                double x = points > 1 ? (double) (i - from) / (points - 1) * WIDE : 0;
                double y = TALL - (one[i] - lo) / (hi - lo) * TALL;
                line.getPoints().addAll(x, Math.max(0, Math.min(TALL, y)));
            }
            if (line.getPoints().size() >= 4) plot.getChildren().add(line);
        }

        /* ------------------------------ the labels ------------------------------ */
        String unit = names.length > 0 ? names[0] : null;

        // THE REAL PEAK, not the padded top of the axis - the air above the
        // line is for the label to sit in, not a value anything reached.
        Label top = new Label(figure == null ? chartFigure(peak, unit) : figure.apply(peak));
        top.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        top.setLayoutX(4);
        top.setLayoutY(2);
        plot.getChildren().add(top);

        Label bottom = new Label(figure == null ? chartFigure(lo, unit) : figure.apply(lo));
        bottom.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        bottom.setLayoutX(4);
        bottom.setLayoutY(TALL - 16);
        plot.getChildren().add(bottom);

        /* ------------------------------- the key ------------------------------- */
        javafx.scene.layout.FlowPane key = new javafx.scene.layout.FlowPane(14, 4);
        key.setStyle("-fx-padding: 6 0 0 0;");
        for (int s = 0; s < names.length; s++) {
            double last = latest(series[s]);
            key.getChildren().add(keySwatch(colours[s],
                    names[s] + (Double.isNaN(last) ? ""
                            : "  " + (figure == null ? chartFigure(last, names[s]) : figure.apply(last)))));
        }

        Label span = new Label(from > 0
                ? String.format("the last %d months of a %d-month city — this was not "
                        + "recorded before that", points, months)
                : String.format("%d months, oldest on the left", points));
        span.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_SPENT));

        VBox box = new VBox(2, plot, years, key, span);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 6 0 12 0;");
        return box;
    }

    /** The last recorded value of a series, or NaN if there is none. */
    public static double latest(double[] series) {
        for (int i = series.length - 1; i >= 0; i--) {
            if (!Double.isNaN(series[i])) return series[i];
        }
        return Double.NaN;
    }

    /**
     * A figure on a chart axis, in whatever the series is counted in.
     *
     * Keyed off the series NAME rather than a unit parameter, because these
     * charts are built from four call sites and threading a unit through all
     * of them to distinguish three cases is more ceremony than it is worth.
     */
    public static String chartFigure(double value, String name) {
        if (name != null && name.startsWith("Book against")) {
            return String.format("%.2f", unsigned0(value, 2));
        }
        if (name != null && name.startsWith("Points")) {
            return String.format("%.1f pts", unsigned0(value * 100, 1));
        }
        if (name != null && name.startsWith("Branches")) {
            return formatter.format(Math.round(value));
        }
        return money(value);
    }

    public static HBox keySwatch(String colour, String name) {
        Region dot = new Region();
        dot.setMinSize(10, 10);
        dot.setPrefSize(10, 10);
        dot.setMaxSize(10, 10);
        dot.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 2;");
        Label text = new Label(name);
        text.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        HBox row = new HBox(5, dot, text);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** A small clickable chip that does something rather than picking a page. */
    public static Label stepChip(String text, Runnable act, boolean quiet) {
        Label chip = new Label(text);
        chip.setStyle("-fx-padding: 4 10 4 10; -fx-cursor: hand;"
                + Palette.block(quiet ? Palette.PANEL : Palette.CONTROL)
                + Palette.words(Palette.SIZE_CAPTION,
                        quiet ? Palette.TEXT_LABEL : Palette.TEXT_BODY));
        chip.setOnMouseClicked(e -> act.run());
        return chip;
    }

    /**
     * A stacked bar with its own swatch legend under it.
     *
     * The bare bar is fine where the lines directly under it name the same
     * things in the same order. Where it stands on its own it needs a key,
     * because a colour with nothing beside it is a colour saying nothing.
     */
    public static VBox keyedBar(java.util.List<Slice> parts, double width) {

        VBox box = stackedBar(parts, width);

        HBox key = new HBox(Palette.GAP_LOOSE);
        key.setAlignment(Pos.CENTER_LEFT);
        for (Slice s : parts) {
            Region swatch = new Region();
            swatch.setMinSize(9, 9);
            swatch.setPrefSize(9, 9);
            swatch.setMaxSize(9, 9);
            swatch.setStyle("-fx-background-color: " + s.colour()
                    + "; -fx-background-radius: 2;");
            Label what = new Label(s.name());
            what.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            HBox one = new HBox(4, swatch, what);
            one.setAlignment(Pos.CENTER_LEFT);
            key.getChildren().add(one);
        }
        box.getChildren().add(key);
        return box;
    }

    /**
     * A year of output — annualised when the city has not lived a year yet.
     *
     * NationalAccounts.getAnnualGdp() sums the last twelve months of history
     * and returns whatever it finds, so a city with eight months of history
     * returns eight months and calls it a year. Every percentage taken against
     * that is then inflated by 12/n, which is how this screen first drew
     * "973% of annual GDP" for a budget that is actually about 64% of it.
     *
     * So: scale a short history up to a year, and say so on screen. An
     * extrapolated figure a player can read beats an exact figure that is
     * wrong by a factor of eight.
     */
    public static double annualGdp(NationalAccounts na) {
        int months = na.getMonthsRecorded();
        if (months <= 0) return 0;
        if (months >= 12) return na.getAnnualGdp();
        return na.getAnnualGdp() / months * 12;
    }

    /**
     * A build time, as every place that quotes one writes it (0.7.20): "~8
     * mo", "under a month" below one, "stalled" with no site output (NaN) -
     * the order line's own forms.
     */
    public static String monthsWait(double months) {
        if (Double.isNaN(months)) return "stalled";
        return months < 1 ? "under a month" : "~" + formatter.format(Math.round(months)) + " mo";
    }

    /**
     * ...and said for what it is (0.7.20): "~5 mo at today's queue". The
     * quote is the wait at this month's shares of the builders' crews
     * (BuildingManager.waitFor()), and orders placed after it take crews
     * from it - the wind farm in Jerus's play-through was quoted ~5 mo,
     * read ~9 mo, and still read ~5 mo left eight months later. Once per
     * place it is quoted: the order line, a card's "N on site", and each
     * site on the construction panel.
     */
    public static String atTodaysQueue(double months) {
        String wait = monthsWait(months);
        return Double.isNaN(months) ? wait : wait + " at today's queue";
    }

    /**
     * What a ratio to that year of output is OF (0.7.20): "of annual GDP", or
     * "of GDP, annualised" while the year is scaled up from fewer than twelve
     * months - so a founding city's "169.7%" says which kind of year it is
     * against. Every ratio to annualGdp() written out in words ends in
     * these; the grids' "of GDP" column heads keep theirs.
     */
    public static String ofAnnualGdp(NationalAccounts na) {
        return gdpEstimated(na) ? "of GDP, annualised" : "of annual GDP";
    }

    public static boolean gdpEstimated(NationalAccounts na) {
        int months = na.getMonthsRecorded();
        return months > 0 && months < 12;
    }

    /* =====================================================================
       TEXT IN THREE LAYERS: THE (i) (0.7.21)

       Jerus asked for the interface to be less of a textbook, and the plan
       he agreed put its text in three layers (the project's
       playing-0-7-19-ui-notes.md, section 6): on the screen, the number, a
       label of a few words and one short line at most; one click away, the
       full explanation; in the manual, the history. This is the middle
       layer's one piece: a short line, an (i) beside it, and a click on the
       (i) opens a popover - the receipt's (0.7.20), a card over the page
       under what was clicked, gone on a click anywhere else or on Esc - with
       the whole text in it. Nothing is deleted: the long text moves in
       here - except where the mockups wrote the popover, whose words replaced
       the paragraph that was there.
       The mockups' four examples are in it word for word (TextLayers.dc.html):
       the build tab's investors, Construction's billing, the one tax dial and
       the Government's last row.
       ===================================================================== */

    /** How wide a popover's text wraps. */
    static final double POPOVER_WIDTH = 360;

    /** The line a popover may end with: the third layer's door, with no link until the manual has one. */
    static final String MORE_IN_THE_MANUAL = "More in the manual";

    /** The popover showing now, so opening another puts the first away. */
    private static javafx.stage.Popup popover;

    /**
     * The (i): a small circled i that opens `whole` in a popover under it.
     * Its click goes no further, so an (i) inside something clickable - a
     * header tile - opens the text and not the tile.
     *
     * @param manual whether the popover ends "More in the manual"
     */
    public static javafx.scene.Node infoButton(String whole, boolean manual) {
        javafx.scene.shape.SVGPath mark = new javafx.scene.shape.SVGPath();
        mark.setContent(Icons.INFO);
        mark.setFill(null);
        mark.setStrokeWidth(2);
        mark.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        mark.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        mark.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_MUTED));
        javafx.scene.layout.Pane box = new javafx.scene.layout.Pane(mark);
        box.setPrefSize(24, 24);
        box.setMinSize(24, 24);
        box.setMaxSize(24, 24);
        box.setScaleX(INFO_SIZE / 24);
        box.setScaleY(INFO_SIZE / 24);
        javafx.scene.layout.StackPane button = new javafx.scene.layout.StackPane(box);
        button.setMinSize(INFO_SIZE, INFO_SIZE);
        button.setPrefSize(INFO_SIZE, INFO_SIZE);
        button.setMaxSize(INFO_SIZE, INFO_SIZE);
        button.setStyle("-fx-cursor: hand;");
        button.setOnMouseEntered(e -> mark.setStroke(javafx.scene.paint.Color.web(Palette.ACCENT)));
        button.setOnMouseExited(e -> mark.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_MUTED)));
        button.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            e.consume();
            openPopover(button, whole, manual);
        });
        button.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, javafx.event.Event::consume);
        return button;
    }

    /** How big the (i) is drawn: its 24-unit grid at this many pixels. */
    static final double INFO_SIZE = 16;

    /**
     * A short line with its (i): `shown` on the screen, `whole` one click
     * away. The line wraps at `wide`; the (i) sits after its last word.
     */
    public static HBox infoLine(String shown, String whole, boolean manual, int size, String tone, double wide) {
        Label line = new Label(shown);
        line.setWrapText(true);
        line.setMaxWidth(wide - INFO_SIZE - Palette.GAP_TIGHT);
        line.setStyle(Palette.words(size, tone));
        HBox row = new HBox(Palette.GAP_TIGHT, line, infoButton(whole, manual));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Region.USE_PREF_SIZE);
        return row;
    }

    /** The popover itself: the whole text on the raised ground, under the (i), until a click elsewhere or Esc. */
    static void openPopover(javafx.scene.Node anchor, String whole, boolean manual) {
        if (popover != null) popover.hide();
        javafx.geometry.Bounds at = anchor.localToScreen(anchor.getBoundsInLocal());
        if (at == null || anchor.getScene() == null) return;

        Label text = new Label(whole);
        text.setWrapText(true);
        text.setMaxWidth(POPOVER_WIDTH);
        text.setStyle("-fx-font-family: " + Fonts.sans() + "; -fx-font-size: 12.5px;"
                + " -fx-text-fill: " + Palette.TEXT_LABEL + "; -fx-line-spacing: 2;");
        VBox card = new VBox(6, text);
        if (manual) {
            Label more = new Label(MORE_IN_THE_MANUAL);
            more.setStyle("-fx-font-family: " + Fonts.sans() + "; -fx-font-size: 12px;"
                    + " -fx-text-fill: " + Palette.TEXT_MUTED + ";");
            card.getChildren().add(more);
        }
        card.setStyle("-fx-background-color: " + Palette.PINNED + "; -fx-border-color: " + Palette.EDGE + ";"
                + " -fx-border-width: 1; -fx-background-radius: 8; -fx-border-radius: 8;"
                + " -fx-padding: 10 12 10 12;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 12, 0, 0, 3);");

        javafx.stage.Popup pop = new javafx.stage.Popup();
        pop.setAutoHide(true);
        pop.setHideOnEscape(true);
        pop.getContent().add(card);
        pop.setOnHidden(e -> { if (popover == pop) popover = null; });
        popover = pop;
        pop.show(anchor, at.getMinX() - 8, at.getMaxY() + 6);
    }

    /** The popover put away, if one is open: clearMenu() calls it when the screen changes; a redraw of the same screen (a month landing) leaves it up. */
    public static void closePopover() {
        if (popover != null) popover.hide();
    }

    /** One payer inside an opened line: who, how much, at what rate. */
    public static HBox payerRow(String who, double amount, double total, String rate) {

        Label name = new Label(who);
        name.setPrefWidth(150);
        name.setMinWidth(150);
        name.setStyle(Palette.words(Palette.SIZE_CAPTION,
                amount > 0 ? Palette.TEXT_BODY : Palette.TEXT_SPENT));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label money = new Label(tightMoney(toDollars(amount)));
        money.setPrefWidth(94);
        money.setMinWidth(94);
        money.setAlignment(Pos.CENTER_RIGHT);
        money.setStyle(Palette.figure(Palette.SIZE_CAPTION,
                amount > 0 ? Palette.TEXT_BODY : Palette.TEXT_SPENT));

        Label share = new Label(String.format("%.0f%%", total > 0 ? amount / total * 100 : 0));
        share.setPrefWidth(48);
        share.setMinWidth(48);
        share.setAlignment(Pos.CENTER_RIGHT);
        share.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

        Label at = new Label(rate == null ? "" : rate);
        at.setPrefWidth(80);
        at.setMinWidth(80);
        at.setAlignment(Pos.CENTER_RIGHT);
        at.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_SPENT));

        HBox row = new HBox(Palette.GAP_TIGHT, name, gap, money, share, at);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT - 22);
        row.setStyle("-fx-padding: 2 0 2 0;");
        return row;
    }

    /** A tile's width. Three across is the layout - the build menu's cards and
     *  the land office's plots are laid out the same way. */
    public static final double TILE_WIDTH = 250;
    /** A tile's height, the same for a card and a plot. */
    public static final double TILE_HEIGHT = 232;
    /** The gap between tiles. */
    public static final double TILE_GAP = 10;
}
