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
 * the icon, the ring and the bar (0.7.24), and since 0.7.26 what a redrawn
 * screen is built from - the wide page, the section head and its hint, the
 * grid of equal columns, the icon square, the tag, the stepper, a bar of
 * segments and ticks, and a loan offered as a card; and since 0.7.27 the
 * page's head, the chip, a ring as a card, a waterfall and a bullet bar;
 * and since 0.7.28 a cause bar, a supply bar, a funnel, an effect scale,
 * cohort bars, a door and the header's sparkline at any size; and since
 * 0.7.29 rows on one scale, a hero card, a details fold kept on its
 * screen, and a chip strip with an icon a chip; and since 0.7.30 pips and a
 * grid of a sector's own figures; and since 0.7.31 a split ring and its
 * key, ranked bars, and a bridge of figures with the steps between them;
 * and since 0.7.32 columns of stacked segments and a setting's chips with
 * the chosen one's line; and since 0.7.33 a ratio in its band, a ladder of
 * rates drawn in their parts, two bars on one scale and a status banner;
 * and since 0.7.34 a button that asks to be pressed and a door as a pill;
 * and since 0.7.35 mirrored bars, a band track and a gauge card around it,
 * diverging bars, and the Trade tab's band meter, moved here; and since
 * 0.7.36 a figure before and after a move, and the staged tray; and since
 * 0.7.37 a chart card's head and a range bar; and since 0.7.39 a search box
 * and a gain or a loss in its colour.
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

    /**
     * ...with an icon before each name (0.7.29: the Infrastructure tab's
     * pages, the road, the bus, the train and the lorry), in the accent while
     * its page is open and the label grey otherwise. Left-aligned, as Build's
     * strip and the Services pages are.
     */
    public static javafx.scene.layout.FlowPane chipStrip(String[] names, String[] icons, String current,
                                                         int size, Consumer<String> pick) {
        javafx.scene.layout.FlowPane strip = chipStrip(names, current, size, pick);
        strip.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < names.length && i < strip.getChildren().size(); i++) {
            if (icons == null || i >= icons.length || icons[i] == null) continue;
            Button chip = (Button) strip.getChildren().get(i);
            chip.setGraphic(icon(icons[i], names[i].equals(current) ? Palette.ACCENT : Palette.TEXT_LABEL, size + 3));
            chip.setGraphicTextGap(6);
        }
        return strip;
    }

    /** The bar itself: its cells - four, and People's five since 0.7.27 - the last one without its dividing rule. */
    public static HBox vitalsBar(VBox... cells) {
        if (cells.length > 0) {
            VBox last = cells[cells.length - 1];
            last.setStyle("-fx-padding: 0 14 0 14;");
            /*
             * ...AND A DOOR AT THE END KEEPS IT (0.7.27): limitCell's hover
             * put the rule back on the way out, and its hand only came with
             * the hover - the People page's GOING SHORT is the first door
             * that is the last cell.
             */
            if (last.getOnMouseClicked() != null) {
                String plain = "-fx-padding: 0 14 0 14; -fx-cursor: hand;";
                last.setStyle(plain);
                last.setOnMouseEntered(e -> last.setStyle(plain + " -fx-background-color: " + Palette.CONTROL + ";"));
                last.setOnMouseExited(e -> last.setStyle(plain));
            }
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
        return stackedBar(parts, width, v -> tightMoney(toDollars(v)));
    }

    /**
     * ...with each part's tooltip figure written by `figure` (0.7.27): the
     * bar printed money whatever it held, so it could not carry people. The
     * People page's bars hold people; GDP's still hold money.
     */
    public static VBox stackedBar(java.util.List<Slice> parts, double width,
                                  java.util.function.DoubleFunction<String> figure) {

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
            Tooltip tip = new Tooltip(s.name() + "\n" + figure.apply(s.amount()));
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

    /**
     * A count, or a dot where there is nothing - a grid of zeros reads as data.
     *
     * IN WHOLE ONES (0.7.27). It printed the fraction the model holds -
     * NumberFormat keeps three decimals - so the People page's household
     * matrix read "4,910.54" households and its age table "21.48" orphans.
     */
    public static String cell(double value) {
        return value >= .5 ? formatter.format(Math.round(value)) : ".";
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
        return keyedBar(parts, width, v -> tightMoney(toDollars(v)));
    }

    /** ...with its tooltips' figures written by `figure` (0.7.27), as stackedBar's. */
    public static VBox keyedBar(java.util.List<Slice> parts, double width,
                                java.util.function.DoubleFunction<String> figure) {

        VBox box = stackedBar(parts, width, figure);

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
        openPopover(anchor, card);
    }

    /**
     * ...or anything else on the same card (0.7.27): the People page's
     * "Moved in" opens a skill bar with its note under it. `body` is laid on
     * the raised ground and put away as the text is.
     */
    public static void openPopover(javafx.scene.Node anchor, javafx.scene.Node body) {
        if (popover != null) popover.hide();
        javafx.geometry.Bounds at = anchor.localToScreen(anchor.getBoundsInLocal());
        if (at == null || anchor.getScene() == null) return;
        VBox card = body instanceof VBox v && v.getStyle().isEmpty() ? v : new VBox(6, body);
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
        name.setWrapText(true);      // never cut (0.7.31): "Principal repaid - not an expense" is wider than 150
        name.setStyle(Palette.words(Palette.SIZE_CAPTION,
                amount > 0 ? Palette.TEXT_BODY : Palette.TEXT_SPENT));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        // A true minus (0.7.31, the Government spec's B15: tightMoney() writes a
        // hyphen, and a refund sat under figures signed with a minus).
        String shown = tightMoney(toDollars(Math.abs(amount)));
        Label money = new Label(amount < 0 && !"$0".equals(shown) ? "\u2212" + shown : shown);
        money.setPrefWidth(94);
        money.setMinWidth(94);
        money.setAlignment(Pos.CENTER_RIGHT);
        money.setStyle(Palette.figure(Palette.SIZE_CAPTION,
                amount > 0 ? Palette.TEXT_BODY : Palette.TEXT_SPENT));

        Label share = new Label(String.format("%.0f%%", total > 0 ? amount / total * 100 : 0).replace('-', '\u2212'));
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

    /** The gap between cards in a row or a grid: Build's cards in their flow, the land office's shelf. */
    public static final double TILE_GAP = 10;

    /* =====================================================================
       AN ICON, AND A RING (0.7.24)

       The Build overview and its categories draw an icon per category and
       per building, and a ring per need, as the round-2 mockups do; the
       header's money block and the construction tab borrow the icon. Both
       here, because more than one screen draws them.
       ===================================================================== */

    /**
     * An outline icon from Icons, stroked in a colour at a size: its 24-unit
     * grid in a Pane of exactly that size, scaled - the rail's way
     * (UserInterface.railButton()), so every icon sits at the coordinates it
     * was drawn at whatever its ink's bounds.
     */
    public static Region icon(String svg, String colour, double size) {
        javafx.scene.shape.SVGPath mark = new javafx.scene.shape.SVGPath();
        mark.setContent(svg);
        mark.setFill(null);
        mark.setStrokeWidth(2);
        mark.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        mark.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        mark.setStroke(javafx.scene.paint.Color.web(colour));
        javafx.scene.layout.Pane grid = new javafx.scene.layout.Pane(mark);
        grid.setPrefSize(24, 24);
        grid.setMinSize(24, 24);
        grid.setMaxSize(24, 24);
        grid.setScaleX(size / 24);
        grid.setScaleY(size / 24);
        // Held at the drawn size, so a layout sees the icon's size and not the grid's.
        javafx.scene.layout.StackPane holder = new javafx.scene.layout.StackPane(new javafx.scene.Group(grid));
        holder.setMinSize(size, size);
        holder.setPrefSize(size, size);
        holder.setMaxSize(size, size);
        holder.setMouseTransparent(true);
        return holder;
    }

    /**
     * A ring: a track, an arc of `share` round it from twelve o'clock in a
     * colour, and a label in the middle - or a tick, for "nothing needed".
     *
     * @param share  0 to 1, how much of the ring is drawn
     * @param label  the figure in the middle, or null for the tick
     */
    public static Region ring(double share, String colour, double size, double stroke, String label, double labelSize) {
        double r = (size - stroke) / 2;
        javafx.scene.shape.Circle track = new javafx.scene.shape.Circle(size / 2, size / 2, r);
        track.setFill(null);
        track.setStroke(javafx.scene.paint.Color.web(Palette.EDGE));
        track.setStrokeWidth(stroke);
        double s = Double.isFinite(share) ? Math.max(0, Math.min(1, share)) : 0;
        javafx.scene.shape.Arc arc = new javafx.scene.shape.Arc(size / 2, size / 2, r, r, 90, -360 * s);
        arc.setType(javafx.scene.shape.ArcType.OPEN);
        arc.setFill(null);
        arc.setStroke(javafx.scene.paint.Color.web(colour));
        arc.setStrokeWidth(stroke);
        arc.setStrokeLineCap(s > 0 && s < 1 ? javafx.scene.shape.StrokeLineCap.ROUND : javafx.scene.shape.StrokeLineCap.BUTT);
        javafx.scene.layout.Pane drawn = new javafx.scene.layout.Pane(track);
        if (s > 0) drawn.getChildren().add(arc);
        drawn.setMinSize(size, size);
        drawn.setPrefSize(size, size);
        drawn.setMaxSize(size, size);
        javafx.scene.Node middle;
        if (label == null) {
            javafx.scene.shape.SVGPath tick = new javafx.scene.shape.SVGPath();
            tick.setContent("M5 12.5l4.5 4.5L19 7.5");
            tick.setFill(null);
            tick.setStroke(javafx.scene.paint.Color.web(colour));
            tick.setStrokeWidth(2.6);
            tick.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
            tick.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
            javafx.scene.layout.Pane grid = new javafx.scene.layout.Pane(tick);
            grid.setPrefSize(24, 24);
            grid.setMinSize(24, 24);
            grid.setMaxSize(24, 24);
            middle = new javafx.scene.Group(grid);
        } else {
            Label figure = new Label(label);
            figure.setStyle(Fonts.monoSemiBold() + " -fx-font-size: " + labelSize + "px; -fx-text-fill: " + Palette.TEXT_HEAD + ";");
            figure.setMinWidth(Region.USE_PREF_SIZE);
            middle = figure;
        }
        javafx.scene.layout.StackPane ring = new javafx.scene.layout.StackPane(drawn, middle);
        ring.setMinSize(size, size);
        ring.setPrefSize(size, size);
        ring.setMaxSize(size, size);
        return ring;
    }

    /**
     * A bar: a track the width given and a fill of `share` of it, in a
     * colour - the Build cards' two bars (0.7.24 on the city's, every
     * building's since 0.7.25).
     */
    public static Region bar(double share, String colour, double width, double height) {
        Region track = new Region();
        track.setMinSize(width, height);
        track.setPrefSize(width, height);
        track.setMaxSize(width, height);
        track.setStyle("-fx-background-color: " + Palette.EDGE + "; -fx-background-radius: " + height / 2 + ";");
        double s = Double.isFinite(share) ? Math.max(0, Math.min(1, share)) : 0;
        Region fill = new Region();
        double w = Math.max(s > 0 ? height : 0, width * s);
        fill.setMinSize(w, height);
        fill.setPrefSize(w, height);
        fill.setMaxSize(w, height);
        fill.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: " + height / 2 + ";");
        javafx.scene.layout.StackPane bar = new javafx.scene.layout.StackPane(track, fill);
        javafx.scene.layout.StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        bar.setMinSize(width, height);
        bar.setPrefSize(width, height);
        bar.setMaxSize(width, height);
        return bar;
    }

    /* =====================================================================
       THE PIECES A REDRAWN SCREEN IS BUILT FROM (0.7.26)

       Jerus, after Build's redesign: "the others are still full of text and
       the design could be more intuitive and fun" - redone one screen at a
       time, in the style Build got in 0.7.24 and 0.7.25. The land office was
       the first, and it needed most of what Build draws its pages with: the
       wide page, a section's heading with words or a control at its right, a
       grid of equal columns, an icon in a tinted square, a tag, the
       stepper's buttons. Those were Build's instance methods (and the
       window's, for the square) that read nothing of either; they are here
       so the next screen calls them rather than copying them. New with the
       office: a bar of segments and ticks - Construction's queue bar,
       generalised, which Construction now draws its bars with - and a loan
       offered as a card.

       The land office's 250 px tile and its 172 px plot went with its cards
       (TILE_WIDTH and TILE_HEIGHT, the second unread since 0.7.25).
       ===================================================================== */

    /** The widest a redrawn page is laid out - Build's Overview and categories since 0.7.24, the land office since 0.7.26 - so a 1,920 window does not stretch a row of cards across the glass. */
    public static final double PAGE_WIDE = 1480;

    /** A page as wide as the stage, up to PAGE_WIDE, with the room under it every page keeps. */
    public static VBox widePage() {
        VBox page = new VBox(Palette.GAP_LOOSE);
        page.setMaxWidth(PAGE_WIDE);
        page.setFillWidth(true);
        page.setPadding(new javafx.geometry.Insets(0, 18, UserInterface.PAGE_FOOT, 18));
        return page;
    }

    /** A section's heading, and whatever sits at its right. */
    public static HBox sectionHead(String title, javafx.scene.Node right) {
        return sectionHead(title, null, right);
    }

    /**
     * ...with an (i) after the heading when `info` is not null (0.7.26: the
     * land office's "ON OFFER", whose paragraph moved in there). The heading
     * keeps its width; the gap before the right-hand side gives way first.
     */
    public static HBox sectionHead(String title, String info, javafx.scene.Node right) {
        Label head = new Label(title);
        head.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        head.setMinWidth(Region.USE_PREF_SIZE);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox row = new HBox(Palette.GAP, head);
        if (info != null) row.getChildren().add(infoButton(info, false));
        row.getChildren().add(gap);
        if (right != null) row.getChildren().add(right);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 6 0 0 0;");
        return row;
    }

    /** A section heading's quiet words at its right: "tap one to build for it". */
    public static Label hint(String text) {
        Label l = new Label(text);
        l.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        return l;
    }

    /** A grid of equal columns, as wide as its page. */
    public static javafx.scene.layout.GridPane equalColumns(int n, double gap) {
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(gap);
        grid.setVgap(gap);
        grid.setMaxWidth(Double.MAX_VALUE);
        for (int i = 0; i < n; i++) {
            javafx.scene.layout.ColumnConstraints c = new javafx.scene.layout.ColumnConstraints();
            c.setPercentWidth(100.0 / n);
            c.setHgrow(Priority.ALWAYS);
            c.setFillWidth(true);
            grid.getColumnConstraints().add(c);
        }
        return grid;
    }

    /** An icon in a rounded square tinted with its colour, as the mockups set one beside a heading (0.7.24; the window's until 0.7.26). */
    public static Region iconSquare(String svg, String colour, double box, double size) {
        javafx.scene.layout.StackPane square = new javafx.scene.layout.StackPane(icon(svg, colour, size));
        square.setMinSize(box, box);
        square.setPrefSize(box, box);
        square.setMaxSize(box, box);
        square.setStyle("-fx-background-color: " + colour + "1f; -fx-background-radius: " + Math.round(box / 4) + ";");
        return square;
    }

    /**
     * A tag: a few words in a pill outlined and tinted in a colour - a Build
     * card's "cheapest per resident" in green, and since 0.7.26 the land
     * office's BEST VALUE (green), ORE and MOST ORE (Palette.ORE) and NEW
     * (the building pink).
     */
    public static Label tag(String text, String colour) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 9.5px; -fx-text-fill: " + colour + "; -fx-padding: 1 7 1 7;"
                + " -fx-border-color: " + colour + "; -fx-border-radius: 9; -fx-background-radius: 9;"
                + " -fx-background-color: " + colour + "1f;");
        l.setMinWidth(Region.USE_PREF_SIZE);
        return l;
    }

    /** A small square button in a quantity stepper: Build's "−", "+", "+10", "+100" and "↺", the land office's "−" and "+". */
    public static Button stepper(String glyph, double width, Runnable go) {
        Button b = new Button(glyph);
        b.setMinSize(width, 22);
        b.setPrefSize(width, 22);
        /*
         * -fx-padding: 0 IN THE STYLE, not just setPadding.
         *
         * The theme's .button rule carries "-fx-padding: 6 14 6 14", and on a
         * button pinned to 24px wide that leaves the label a content box 4px
         * narrower than nothing - so JavaFX drew three empty grey squares where
         * the minus, the plus and the +10 should have been. Play-tested.
         *
         * An inline style beats a stylesheet, which setPadding did not.
         */
        b.setPadding(javafx.geometry.Insets.EMPTY);
        b.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY)
                + " -fx-padding: 0; -fx-background-color: " + Palette.FIELD + ";"
                + " -fx-border-color: transparent;"
                + " -fx-background-radius: " + Palette.RADIUS_TIGHT + "; -fx-cursor: hand;");
        b.setOnAction(e -> go.run());
        return b;
    }

    /* ----------------------------- a bar of segments ----------------------------- */

    /**
     * One stretch of a segment bar: how much of the bar's units, its colour,
     * and - each may be null - whether it is a GHOST (outlined and tinted,
     * not filled: what would be there, not what is), a stripe along its foot
     * in another colour, a short label in its middle (drawn when it fits), a
     * tooltip, and what a click on it does.
     */
    public record Segment(double amount, String colour, boolean ghost, String stripe, String label,
                          String tip, Runnable go) {
        /** A plain filled stretch. */
        public static Segment of(double amount, String colour) {
            return new Segment(amount, colour, false, null, null, null, null);
        }
    }

    /** A mark across a segment bar: where, in the bar's units; its colour and width in pixels; its name under the bar, and a tooltip (either may be null). */
    public record Tick(double at, String colour, double width, String name, String tip) { }

    /**
     * A bar of segments and ticks (0.7.26): a track, the segments laid end
     * to end from the left, each `amount` of `scale` (scale 0 or less: their
     * sum), and the ticks drawn across it, each named under the bar if it has
     * a name. `width` fixes the bar's width; 0 or less, it fills what holds
     * it. `band` is the track's height; a bar with ticks is 8 px taller, the
     * ticks standing 4 px proud of it each way, and a named tick adds a line
     * of caption under it. A tick outside the scale is not drawn.
     *
     * Construction's private queue bar, generalised (ConstructionScreen.Bar,
     * 0.7.22): its gauge's queue against the landlords' twelve months, and
     * its sites' and stopped shells' progress, are this bar since 0.7.26, at
     * the same band, track and mark. The land office draws the ground with
     * it - free ground solid, the next plots as numbered ghosts - and a
     * plot's price a square foot, and the funding page's money against the
     * gap.
     */
    public static SegmentBar segmentBar(List<Segment> parts, double scale, List<Tick> ticks,
                                        double width, double band) {
        return new SegmentBar(parts, scale, ticks, width, band);
    }

    /** The bar segmentBar() draws: a Pane that lays its parts out at whatever width it is given. */
    public static final class SegmentBar extends javafx.scene.layout.Pane {
        private final List<Segment> parts;
        private final List<Tick> ticks;
        private final double scale, band;
        private final Region track = new Region();
        private final List<Region> drawn = new java.util.ArrayList<>(), stripes = new java.util.ArrayList<>();
        private final List<Label> labels = new java.util.ArrayList<>(), names = new java.util.ArrayList<>();
        private final List<Region> marks = new java.util.ArrayList<>();
        /** The first segment's amount while it grows (animateFirst()), or NaN when it stands still. */
        private final javafx.beans.property.DoubleProperty first =
                new javafx.beans.property.SimpleDoubleProperty(Double.NaN);

        SegmentBar(List<Segment> parts, double scale, List<Tick> ticks, double width, double band) {
            this.parts = parts == null ? List.of() : parts;
            this.ticks = ticks == null ? List.of() : ticks;
            double sum = 0;
            for (Segment s : this.parts) sum += Math.max(0, s.amount());
            this.scale = scale > 0 ? scale : sum;
            this.band = band;
            track.setStyle("-fx-background-color: " + Palette.CONTROL + "; -fx-background-radius: " + band / 2 + ";");
            getChildren().add(track);
            for (Segment s : this.parts) {
                Region r = new Region();
                r.setStyle(s.ghost()
                        ? "-fx-background-color: " + s.colour() + "40; -fx-border-color: " + s.colour() + ";"
                          + " -fx-border-width: 1; -fx-background-radius: " + band / 2 + "; -fx-border-radius: " + band / 2 + ";"
                        : "-fx-background-color: " + s.colour() + "; -fx-background-radius: " + band / 2 + ";");
                if (s.tip() != null) {
                    Tooltip tip = new Tooltip(s.tip());
                    tip.setShowDelay(Duration.millis(200));
                    Tooltip.install(r, tip);
                }
                if (s.go() != null) {
                    r.setCursor(javafx.scene.Cursor.HAND);
                    r.setOnMouseClicked(e -> s.go().run());
                }
                drawn.add(r);
                getChildren().add(r);
                Region stripe = new Region();
                stripe.setMouseTransparent(true);
                stripe.setVisible(s.stripe() != null);
                if (s.stripe() != null) stripe.setStyle("-fx-background-color: " + s.stripe() + ";");
                stripes.add(stripe);
                getChildren().add(stripe);
                Label label = new Label(s.label() == null ? "" : s.label());
                label.setMouseTransparent(true);
                label.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_HEAD));
                labels.add(label);
                getChildren().add(label);
            }
            for (Tick t : this.ticks) {
                Region m = new Region();
                m.setStyle("-fx-background-color: " + t.colour() + ";");
                if (t.tip() != null) {
                    Tooltip tip = new Tooltip(t.tip());
                    tip.setShowDelay(Duration.millis(200));
                    Tooltip.install(m, tip);
                }
                marks.add(m);
                getChildren().add(m);
                Label name = new Label(t.name() == null ? "" : t.name());
                name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
                name.setVisible(t.name() != null);
                names.add(name);
                getChildren().add(name);
            }
            setMinWidth(width > 0 ? width : 0);
            setPrefWidth(width > 0 ? width : 200);
            setMaxWidth(width > 0 ? width : Double.MAX_VALUE);
            setMinHeight(tall());
            setPrefHeight(tall());
            first.addListener((o, was, now) -> requestLayout());
        }

        /** How far the ticks stand proud of the band, each way. */
        private double proud() { return ticks.isEmpty() ? 0 : 4; }

        /** Whether any tick carries a name, which takes a line under the bar. */
        private boolean named() {
            for (Tick t : ticks) if (t.name() != null) return true;
            return false;
        }

        /** The bar's own height: the band, the ticks proud of it, and the names' line. */
        private double tall() { return band + 2 * proud() + (named() ? 14 : 0); }

        /**
         * The first segment grows from `from` to its own amount over `millis`
         * - the land office's free ground widening after a purchase - with
         * the segments after it carried along. The scale stays.
         */
        public void animateFirst(double from, double millis) {
            if (parts.isEmpty() || !(Math.abs(from - parts.get(0).amount()) > 0)) return;
            first.set(from);
            javafx.animation.Timeline grow = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(Duration.millis(millis),
                            new javafx.animation.KeyValue(first, parts.get(0).amount(),
                                    javafx.animation.Interpolator.EASE_OUT)));
            grow.setOnFinished(e -> first.set(Double.NaN));
            grow.play();
        }

        @Override protected void layoutChildren() {
            double w = getWidth(), h = getHeight();
            double top = Math.max(0, (h - tall()) / 2);
            double y = top + proud();
            track.resizeRelocate(0, y, w, band);
            double x = 0;
            int n = parts.size();
            for (int i = 0; i < n; i++) {
                Segment s = parts.get(i);
                double amount = i == 0 && !Double.isNaN(first.get()) ? first.get() : s.amount();
                double wide = scale > 0 ? w * Math.max(0, amount) / scale : 0;
                double left = x + (i > 0 && n > 1 ? 1 : 0);
                double right = Math.min(w, x + wide) - (i < n - 1 ? 1 : 0);
                double shown = amount > 0 ? Math.max(1, right - left) : 0;
                Region r = drawn.get(i);
                r.setVisible(shown > 0);
                r.resizeRelocate(left, y, shown, band);
                double stripe = Math.max(2, Math.round(band / 5));
                stripes.get(i).resizeRelocate(left + 2, y + band - stripe - 1, Math.max(0, shown - 4), stripe);
                Label label = labels.get(i);
                double lw = label.prefWidth(-1), lh = label.prefHeight(-1);
                label.setVisible(s.label() != null && shown >= lw + 4 && band >= lh - 2);
                label.resizeRelocate(left + (shown - lw) / 2, y + (band - lh) / 2, lw, lh);
                x += wide;
            }
            for (int i = 0; i < ticks.size(); i++) {
                Tick t = ticks.get(i);
                Region m = marks.get(i);
                Label name = names.get(i);
                boolean on = scale > 0 && t.at() >= 0 && t.at() <= scale;
                m.setVisible(on);
                name.setVisible(on && t.name() != null);
                if (!on) continue;
                double at = Math.round(w * t.at() / scale);
                m.resizeRelocate(Math.max(0, Math.min(w - t.width(), at - t.width() / 2)), top, t.width(),
                        band + 2 * proud());
                double nw = name.prefWidth(-1), nh = name.prefHeight(-1);
                name.resizeRelocate(Math.max(0, Math.min(w - nw, at - nw / 2)), top + band + 2 * proud() + 1, nw, nh);
            }
        }
    }

    /* ----------------------------- a loan as a card ----------------------------- */

    /**
     * A quoted rate's verdict colour, by how far up the market's own band it
     * sits - BuildScreen.rateStyle()'s rule, here since 0.7.26 so a card can
     * colour its offer.
     *
     * Not decoration. The whole point of showing the quote is that a player
     * can see they are being charged for the size of the ask, and a number
     * that looks the same at 1% and at 20% does not communicate that at a
     * glance. Measured against the market's OWN band rather than typed-in
     * numbers, so re-shaping the curve cannot leave this colouring behind.
     * When the spread ran 1%-20% a flat "red above 15%" was about right; the
     * moment the curve was made gentler the same thresholds would have
     * painted ordinary municipal leverage green and nothing else anything at
     * all.
     */
    public static String rateColour(DebtQuote quote, DebtManager market) {
        double floor = market.floorRate();
        double span = Math.max(1e-9, market.ceilingRate() - floor);
        double howFarUp = (quote.marketRate() - floor) / span;
        if (howFarUp >= .55) return Palette.BAD;    // deep into the expensive half
        if (howFarUp >= .25) return Palette.WARN;   // getting dear
        return Palette.GOOD;                        // ordinary money
    }

    /**
     * A loan on offer, as a card (0.7.26): the land office's funding page sets
     * two or three side by side, where BuildScreen.fundingOffer() stacks the
     * same figures as a statement (Build's credit page, INSUFFICIENT FUNDS, is
     * to take this in Build's own pass). Its name, with an (i) holding `info`
     * when it is not null; the rate in `tone`, the verdict rateColour() gives
     * it; the face, the cash it brings, a month's cost and the cost all in,
     * each written by `written`; what happens at the end, in a line; the
     * credit impact in the rate's colour; and the button across its foot,
     * which books exactly this quote - here a green one saying `action`
     * (Finances' Borrow, the Bank's preferred offer); the land office's has
     * been the action button since 0.7.34 (the overload below).
     */
    public static VBox offerCard(String name, String info, DebtQuote quote, String rate, String tone,
                                 String ending, String action, Runnable issue,
                                 java.util.function.DoubleFunction<String> written) {
        Button go = new Button(action);
        go.setWrapText(true);
        go.setMaxWidth(Double.MAX_VALUE);
        go.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + Palette.CONFIRM + ";");
        go.setOnAction(e -> issue.run());
        return offerCard(name, info, quote, rate, tone, ending, go, written);
    }

    /**
     * ...with the action button across its foot (0.7.34): the land office's
     * offers, whose press borrows and then buys - "Buy 5 plots · D$9.8B",
     * and the paper under it.
     */
    public static VBox offerCard(String name, String info, DebtQuote quote, String rate, String tone,
                                 String ending, Press press, String svg, String accent, Runnable issue,
                                 java.util.function.DoubleFunction<String> written) {
        return offerCard(name, info, quote, rate, tone, ending, actionButton(svg, accent, ACTION_TALL, press, issue), written);
    }

    static VBox offerCard(String name, String info, DebtQuote quote, String rate, String tone,
                          String ending, javafx.scene.Node go, java.util.function.DoubleFunction<String> written) {
        Label title = new Label(name);
        title.setWrapText(true);
        title.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        HBox head = new HBox(Palette.GAP_TIGHT, title);
        if (info != null) head.getChildren().add(infoButton(info, false));
        head.setAlignment(Pos.CENTER_LEFT);

        Label rateLine = new Label(rate);
        rateLine.setWrapText(true);
        rateLine.setStyle(Palette.figure(Palette.SIZE_BODY, tone));

        VBox figures = new VBox(3,
                offerLine("Face - what the city owes", written.apply(quote.faceValue()), Palette.TEXT_BODY),
                offerLine("Cash it brings", written.apply(quote.cashReceived()), Palette.GOOD),
                offerLine("Monthly cost", quote.monthlyInterest() > 0
                        ? written.apply(quote.monthlyInterest()) + " a month"
                        : "none - it pays no coupon", Palette.TEXT_BODY),
                offerLine("Cost of the credit, all in", written.apply(quote.totalCost()), Palette.TEXT_BODY));

        Label end = new Label(ending);
        end.setWrapText(true);
        end.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        Label impact = new Label(quote.creditImpact());
        impact.setWrapText(true);
        impact.setStyle(Palette.words(Palette.SIZE_LABEL, tone) + " -fx-font-weight: bold;");

        Region push = new Region();
        VBox.setVgrow(push, Priority.ALWAYS);

        VBox card = new VBox(6, head, rateLine, figures, end, impact, push, go);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMaxHeight(Double.MAX_VALUE);
        card.setStyle("-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return card;
    }

    /** One figure on an offer card: what it is at the left, wrapping, and the figure at the right. */
    static HBox offerLine(String label, String value, String tone) {
        Label what = new Label(label);
        what.setWrapText(true);
        what.setMinWidth(0);
        what.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        HBox.setHgrow(what, Priority.ALWAYS);
        what.setMaxWidth(Double.MAX_VALUE);
        Label figure = new Label(value);
        figure.setMinWidth(Region.USE_PREF_SIZE);
        figure.setStyle(Palette.figure(Palette.SIZE_BODY, tone));
        HBox row = new HBox(Palette.GAP, what, figure);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /* =====================================================================
       THE PIECES THE PEOPLE PAGE ADDED (0.7.27)

       The second of the rail's screens redrawn in Build's style needed five
       more, and every screen after it will too: the page's head (its title
       with its area's swatch, a sub-page after "›", an (i), and whatever
       sits at its right); the chip - a few words in a colour on a faint
       ground of it, Construction's since 0.7.22; a ring as a card, the body
       of Build's measure card, which Build's rings now draw with and the
       People page's care row borrows; a waterfall, a total walked through
       what was added and taken away, for People's month and the
       households' month (and the Government's treasury bridge to come); and
       a bullet bar, a figure against the line it is read against. The design
       study is the project's spec-people-0727.md, section 6.
       ===================================================================== */

    /** A page's head: its title, its area's swatch, and what sits at its right. */
    public static HBox pageHead(String title, String areaColour, javafx.scene.Node... right) {
        return pageHead(title, areaColour, null, null, null, right);
    }

    /**
     * ...with a sub-page after "›" - the title is then a way back, `back` -
     * and an (i) holding `info` after them when it is not null: "People ›
     * Household money (i)". The swatch is the one UserInterface.pageTitle()
     * draws, in the colour given.
     */
    public static HBox pageHead(String title, String areaColour, Runnable back, String sub, String info,
                                javafx.scene.Node... right) {
        Label t = new Label(title);
        t.setStyle(Palette.strong(Palette.SIZE_TITLE, Palette.TEXT_HEAD) + " -fx-padding: 8 0 2 0;");
        t.setMinWidth(Region.USE_PREF_SIZE);
        if (areaColour != null) {
            Region swatch = new Region();
            swatch.setMinSize(10, 10);
            swatch.setPrefSize(10, 10);
            swatch.setMaxSize(10, 10);
            swatch.setStyle("-fx-background-color: " + areaColour + "; -fx-background-radius: 3;");
            t.setGraphic(swatch);
            t.setGraphicTextGap(10);
        }
        HBox titled = new HBox(10, t);
        titled.setAlignment(Pos.CENTER_LEFT);
        if (sub != null) {
            if (back != null) {
                t.setStyle(t.getStyle() + " -fx-cursor: hand;");
                t.setOnMouseClicked(e -> back.run());
            }
            Label sep = new Label("›");
            sep.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_MUTED) + " -fx-padding: 8 0 2 0;");
            Label where = new Label(sub);
            where.setStyle(Palette.strong(Palette.SIZE_TITLE, Palette.TEXT_HEAD) + " -fx-padding: 8 0 2 0;");
            where.setMinWidth(Region.USE_PREF_SIZE);
            titled.getChildren().addAll(sep, where);
        }
        if (info != null) {
            javafx.scene.Node dot = infoButton(info, false);
            HBox.setMargin(dot, new javafx.geometry.Insets(6, 0, 0, 0));
            titled.getChildren().add(dot);
        }
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox head = new HBox(Palette.GAP_LOOSE, titled, gap);
        for (javafx.scene.Node n : right) if (n != null) head.getChildren().add(n);
        head.setAlignment(Pos.CENTER_LEFT);
        head.setMaxWidth(Double.MAX_VALUE);
        head.setStyle("-fx-padding: 0 0 4 0;");
        return head;
    }

    /** A chip: the words in a colour on a faint ground of it (Construction's, 0.7.22; here since 0.7.27). */
    public static Label chip(String text, String colour) {
        Label l = new Label(text);
        l.setStyle(Palette.words(Palette.SIZE_CAPTION, colour)
                + " -fx-background-color: " + tint(colour, .16) + "; -fx-background-radius: 10;"
                + " -fx-padding: 2 8 2 8;");
        l.setMinWidth(Region.USE_PREF_SIZE);
        return l;
    }

    /** A colour at an opacity, for a chip's ground. */
    public static String tint(String hex, double alpha) {
        javafx.scene.paint.Color c = javafx.scene.paint.Color.web(hex);
        return String.format("rgba(%d, %d, %d, %.2f)", (int) Math.round(c.getRed() * 255),
                (int) Math.round(c.getGreen() * 255), (int) Math.round(c.getBlue() * 255), alpha);
    }

    /* ----------------------------- a ring as a card ----------------------------- */

    /**
     * A ring as a card: the ring at the left with its figure in it, and
     * beside it a title, a line and a foot - Build's measure card (0.7.24),
     * on the Build card ground (RAISED, radius 8, a 1 px EDGE that turns
     * ACCENT under the pointer). A click runs `onClick`; the tooltip says
     * where it goes.
     */
    public static HBox ringCard(String figure, double arc, String tone, String title, String line, String foot,
                                double ringSize, Runnable onClick, String tooltip) {
        return ringCard(figure, arc, tone, title, null, line, foot, Palette.TEXT_MUTED, ringSize, false, null,
                onClick, tooltip);
    }

    /**
     * ...with an (i) after the title holding `info`, the foot in a colour of
     * its own (Build's "N on site" in the building pink), the PICKED form -
     * the pinned ground and a pink edge, Build's ring whose buildings are
     * shown under it - and chips under the foot (the People page's
     * "Outbreak, month 3"). Null for any of them leaves it out.
     */
    public static HBox ringCard(String figure, double arc, String tone, String title, String info,
                                String line, String foot, String footColour, double ringSize, boolean picked,
                                List<? extends javafx.scene.Node> chips, Runnable onClick, String tooltip) {
        Label name = new Label(title);
        name.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        name.setWrapText(true);
        javafx.scene.Node head = name;
        if (info != null) {
            HBox titled = new HBox(Palette.GAP_TIGHT, name, infoButton(info, false));
            titled.setAlignment(Pos.CENTER_LEFT);
            head = titled;
        }
        VBox words = new VBox(1, head);
        if (line != null) {
            Label sl = new Label(line);
            sl.setWrapText(true);
            sl.setStyle("-fx-font-size: 10.5px; -fx-text-fill: " + Palette.TEXT_LABEL + ";");
            words.getChildren().add(sl);
        }
        if (foot != null) {
            Label os = new Label(foot);
            os.setWrapText(true);
            os.setStyle(Palette.words(Palette.SIZE_LABEL, footColour == null ? Palette.TEXT_MUTED : footColour));
            words.getChildren().add(os);
        }
        if (chips != null && !chips.isEmpty()) {
            javafx.scene.layout.FlowPane row = new javafx.scene.layout.FlowPane(Palette.GAP_TIGHT, Palette.GAP_TIGHT);
            row.getChildren().addAll(chips);
            row.setStyle("-fx-padding: 3 0 0 0;");
            words.getChildren().add(row);
        }
        words.setMinWidth(0);
        HBox.setHgrow(words, Priority.ALWAYS);
        HBox card = new HBox(10, ring(arc, tone, ringSize, 6, figure, 12.5), words);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(Double.MAX_VALUE);
        String rest = "-fx-padding: 10 12 10 12; -fx-background-radius: 8; -fx-border-radius: 8;"
                + (onClick != null ? " -fx-cursor: hand;" : "")
                + " -fx-background-color: " + (picked ? Palette.PINNED : Palette.RAISED) + "; -fx-border-color: ";
        String edge = picked ? Palette.BUILDING : Palette.EDGE;
        card.setStyle(rest + edge + ";");
        if (onClick != null) {
            card.setOnMouseEntered(e -> card.setStyle(rest + (picked ? Palette.BUILDING : Palette.ACCENT) + ";"));
            card.setOnMouseExited(e -> card.setStyle(rest + edge + ";"));
            card.setOnMouseClicked(e -> onClick.run());
        }
        if (tooltip != null) {
            Tooltip tip = new Tooltip(tooltip);
            tip.setShowDelay(Duration.millis(300));
            Tooltip.install(card, tip);
        }
        return card;
    }

    /* ------------------------------- a waterfall ------------------------------- */

    /**
     * One column of a waterfall: its name; its amount, which a plain step
     * adds to the running total (negative takes away) and a TOTAL step sets
     * - drawn from zero, the model's own figure, so a column the model sums
     * is never re-summed here; its colour; and, each may be null, the parts
     * its bar is stacked from (their amounts' sizes split the bar, in their
     * own colours, each with its tooltip), an icon under it, its tooltip,
     * and what a click on the bar does, handed the bar to anchor a popover.
     */
    public record Step(String name, double amount, String colour, boolean total, List<Slice> parts,
                       String icon, String tip, java.util.function.Consumer<javafx.scene.Node> go) {
        /** A plain step. */
        public static Step of(String name, double amount, String colour) {
            return new Step(name, amount, colour, false, null, null, null, null);
        }
        /** A total, drawn from zero to the figure given. */
        public static Step total(String name, double amount, String colour) {
            return new Step(name, amount, colour, true, null, null, null, null);
        }
        public Step parts(List<Slice> p)   { return new Step(name, amount, colour, total, p, icon, tip, go); }
        public Step icon(String svg)      { return new Step(name, amount, colour, total, parts, svg, tip, go); }
        public Step tip(String t)         { return new Step(name, amount, colour, total, parts, icon, t, go); }
        public Step go(java.util.function.Consumer<javafx.scene.Node> g) {
            return new Step(name, amount, colour, total, parts, icon, tip, g);
        }
    }

    /**
     * A waterfall (0.7.27): the steps as columns, each bar standing where the
     * running total was before it and reaching where it is after, a zero line
     * across, a hairline from each bar to the next, the figure over each bar
     * written by `figure` and its name (and icon) under it. Scaled to the
     * steps, not to anything they are steps of: the People page's month is a
     * hundred people moving on a city of a hundred thousand. `width` 0 or
     * less fills what holds it; `height` is the whole of it, names included.
     */
    public static Waterfall waterfall(List<Step> steps, java.util.function.DoubleFunction<String> figure,
                                      double width, double height) {
        return new Waterfall(steps, figure, width, height);
    }

    /** The bars waterfall() draws: a Pane that lays its columns out at whatever width it is given. */
    public static final class Waterfall extends javafx.scene.layout.Pane {
        private final List<Step> steps;
        private final double[] from, to;
        private final double low, high;
        private final List<List<Region>> bars = new java.util.ArrayList<>();
        private final List<Label> figures = new java.util.ArrayList<>(), names = new java.util.ArrayList<>();
        private final List<javafx.scene.Node> icons = new java.util.ArrayList<>();
        private final List<javafx.scene.shape.Line> links = new java.util.ArrayList<>();
        private final javafx.scene.shape.Line zero = new javafx.scene.shape.Line();
        /** How far the bars have grown out of the zero line, 0 to 1 (animate()). */
        private final javafx.beans.property.DoubleProperty grown = new javafx.beans.property.SimpleDoubleProperty(1);

        /** The figures' line over the plot, the least the names under it take, and an icon's size. */
        private static final double FIGURE_ROOM = 16, NAME_ROOM = 12, ICON = 13;

        Waterfall(List<Step> steps, java.util.function.DoubleFunction<String> figure, double width, double height) {
            this.steps = steps == null ? List.of() : steps;
            int n = this.steps.size();
            from = new double[n];
            to = new double[n];
            double run = 0, lo = 0, hi = 0;
            for (int i = 0; i < n; i++) {
                Step s = this.steps.get(i);
                double a = Double.isFinite(s.amount()) ? s.amount() : 0;
                from[i] = s.total() ? 0 : run;
                to[i] = s.total() ? a : run + a;
                run = to[i];
                lo = Math.min(lo, Math.min(from[i], to[i]));
                hi = Math.max(hi, Math.max(from[i], to[i]));
            }
            low = lo;
            high = hi;
            zero.setStroke(javafx.scene.paint.Color.web(Palette.EDGE));
            getChildren().add(zero);
            for (int i = 0; i < n; i++) {
                Step s = this.steps.get(i);
                List<Region> parts = new java.util.ArrayList<>();
                List<Slice> slices = s.parts() == null || s.parts().isEmpty()
                        ? List.of(new Slice(s.name(), s.amount(), s.colour())) : s.parts();
                for (Slice p : slices) {
                    Region r = new Region();
                    r.setStyle("-fx-background-color: " + p.colour() + "; -fx-background-radius: 2;");
                    String tip = s.parts() == null || s.parts().isEmpty()
                            ? (s.tip() != null ? s.tip() : s.name() + "\n" + figure.apply(s.amount()))
                            : p.name() + "\n" + figure.apply(p.amount())
                              + (s.tip() != null ? "\n\n" + s.tip() : "");
                    Tooltip t = new Tooltip(tip);
                    t.setShowDelay(Duration.millis(200));
                    Tooltip.install(r, t);
                    if (s.go() != null) {
                        r.setCursor(javafx.scene.Cursor.HAND);
                        r.setOnMouseClicked(e -> s.go().accept(r));
                    }
                    parts.add(r);
                    getChildren().add(r);
                }
                bars.add(parts);
                Label f = new Label(figure.apply(s.amount()));
                f.setStyle(Palette.figure(Palette.SIZE_CAPTION, s.total() ? Palette.TEXT_HEAD : Palette.TEXT_LABEL));
                f.setMinWidth(Region.USE_PREF_SIZE);
                figures.add(f);
                getChildren().add(f);
                Label name = new Label(s.name());
                name.setWrapText(true);
                name.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
                name.setAlignment(Pos.TOP_CENTER);
                name.setStyle(Palette.words(Palette.SIZE_CAPTION, s.total() ? Palette.TEXT_LABEL : Palette.TEXT_MUTED));
                names.add(name);
                getChildren().add(name);
                javafx.scene.Node mark = s.icon() == null ? null : icon(s.icon(), Palette.TEXT_MUTED, ICON);
                icons.add(mark);
                if (mark != null) getChildren().add(mark);
                if (i < n - 1) {
                    javafx.scene.shape.Line link = new javafx.scene.shape.Line();
                    link.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_SPENT));
                    link.getStrokeDashArray().addAll(2.0, 2.0);
                    links.add(link);
                    getChildren().add(link);
                }
            }
            setMinWidth(width > 0 ? width : 0);
            setPrefWidth(width > 0 ? width : 400);
            setMaxWidth(width > 0 ? width : Double.MAX_VALUE);
            setMinHeight(height);
            setPrefHeight(height);
            grown.addListener((o, was, now) -> requestLayout());
        }

        /** The bars grow out of the zero line over `millis` - the People page's month landing. */
        public void animate(double millis) {
            grown.set(0);
            javafx.animation.Timeline grow = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(Duration.millis(millis),
                            new javafx.animation.KeyValue(grown, 1, javafx.animation.Interpolator.EASE_OUT)));
            grow.play();
        }

        @Override protected void layoutChildren() {
            int n = steps.size();
            if (n == 0) return;
            double w = getWidth(), h = getHeight();
            // Tighter between many columns, so a name of one long word keeps its room.
            double gap = n > 10 ? 4 : 10, col = (w - gap * (n - 1)) / n;
            double barW = Math.max(6, Math.min(64, col * .62));
            // THE NAMES ARE NEVER CUT: they take the height they need and the plot gives way.
            double nameRoom = NAME_ROOM;
            for (Label name : names) nameRoom = Math.max(nameRoom, name.prefHeight(col));
            boolean anyIcon = false;
            for (javafx.scene.Node mark : icons) anyIcon |= mark != null;
            double iconRoom = anyIcon ? ICON + 2 : 0;
            double top = FIGURE_ROOM, plot = Math.max(10, h - FIGURE_ROOM - nameRoom - iconRoom - 4);
            double span = high - low;
            // Grown out of the zero line: every value is drawn at g of itself.
            double g = grown.get();
            double y0 = span <= 0 ? top + plot : top + high / span * plot;
            zero.setStartX(0);
            zero.setEndX(w);
            zero.setStartY(Math.floor(y0) + .5);
            zero.setEndY(Math.floor(y0) + .5);
            for (int i = 0; i < n; i++) {
                double x = i * (col + gap);
                double left = x + (col - barW) / 2;
                double a = y0 + (span <= 0 ? 0 : -(from[i] * g) / span * plot);
                double b = y0 + (span <= 0 ? 0 : -(to[i] * g) / span * plot);
                double upper = Math.min(a, b), lower = Math.max(a, b);
                double tall = Math.max(1, lower - upper);
                List<Region> parts = bars.get(i);
                Step s = steps.get(i);
                double sum = 0;
                if (s.parts() != null && !s.parts().isEmpty()) for (Slice p : s.parts()) sum += Math.abs(p.amount());
                double yy = upper;
                for (int k = 0; k < parts.size(); k++) {
                    double share = sum > 0 ? Math.abs(s.parts().get(k).amount()) / sum : 1;
                    double ph = parts.size() == 1 ? tall : tall * share;
                    Region r = parts.get(k);
                    r.setVisible(ph > 0);
                    r.resizeRelocate(left, yy, barW, Math.max(parts.size() == 1 ? 1 : 0, ph));
                    yy += ph;
                }
                Label f = figures.get(i);
                double fw = f.prefWidth(-1), fh = f.prefHeight(-1);
                f.resizeRelocate(x + (col - fw) / 2, Math.max(0, upper - fh - 1), fw, fh);
                javafx.scene.Node mark = icons.get(i);
                double nameTop = top + plot + 4;
                if (mark != null) {
                    mark.resizeRelocate(x + (col - ICON) / 2, nameTop, ICON, ICON);
                }
                Label name = names.get(i);
                name.resizeRelocate(x, nameTop + iconRoom, col, name.prefHeight(col));
                if (i < links.size()) {
                    double ly = Math.floor(b) + .5;
                    javafx.scene.shape.Line link = links.get(i);
                    link.setStartX(left + barW);
                    link.setEndX((i + 1) * (col + gap) + (col - barW) / 2);
                    link.setStartY(ly);
                    link.setEndY(ly);
                }
            }
        }
    }

    /* ------------------------------- a bullet bar ------------------------------- */

    /**
     * A figure against the line it is read against (0.7.27): a bar of
     * `value` in a colour with a tick at `target`, on a scale of the larger
     * of the two, and `label` under it - the People page's living here
     * against what the city draws, the EI premiums against what EI paid,
     * and Policy's pension cover to come. `width` 0 or less fills what holds
     * it.
     */
    public static VBox bulletBar(double value, double target, String colour, double width, String label) {
        double v = Double.isFinite(value) ? Math.max(0, value) : 0;
        double t = Double.isFinite(target) ? Math.max(0, target) : 0;
        double scale = Math.max(v, t);
        SegmentBar bar = segmentBar(List.of(Segment.of(v, colour)), scale > 0 ? scale : 1,
                t > 0 ? List.of(new Tick(t, Palette.TEXT_HEAD, 2, null, null)) : List.of(), width, 10);
        VBox box = new VBox(3, bar);
        if (label != null) {
            Label says = new Label(label);
            says.setWrapText(true);
            says.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            box.getChildren().add(says);
        }
        box.setMaxWidth(width > 0 ? width : Double.MAX_VALUE);
        return box;
    }

    /* =====================================================================
       THE PIECES THE SERVICES SCREEN ADDED (0.7.28)

       The third of the rail's screens redrawn in Build's style answers its
       questions with five more pictures, each with a second user waiting,
       and a door: a CAUSE BAR, a whole split into what makes it - named
       under a part wide enough to carry its name, keyed beside the rest, a
       part that is a door opening where it is fixed (the sick rate's
       causes, the crime's, who draws the power and the water); a SUPPLY
       BAR, what is working inside what is built against the line it is
       needed to (care places, the school stages, officers, cells, the
       ground); a FUNNEL, the steps a figure narrows through with the one
       that binds outlined (a course's gates, who gets a doctor); an EFFECT
       SCALE, what a coverage buys from nobody covered to everybody with
       today marked (childcare's infant deaths, the sick who get better);
       COHORT BARS, everybody part way through something a bar a month (a
       course, a sentence, an illness - the Services screen's pipeline bars
       of 2026-09-11, generalised); and a DOOR, a few words and a "›" that go where the
       figure is decided. The header's sparkline is lent to a screen at a
       size of its own. The design study is the project's
       spec-services-0728.md, section 6.
       ===================================================================== */

    /* ------------------------------- a cause bar ------------------------------- */

    /**
     * One part of a cause bar: its name, its amount in the bar's units (one
     * at zero or below draws nothing and is keyed as "none"), its colour,
     * what a click on it opens (null: nothing), whether it is HATCHED - what
     * is left over rather than a cause - and its tooltip (null: its name
     * and figure).
     */
    public record Part(String name, double amount, String colour, Runnable go, boolean hatched, String tip) {
        /** A plain part. */
        public static Part of(String name, double amount, String colour) {
            return new Part(name, amount, colour, null, false, null);
        }
        public Part go(Runnable g)  { return new Part(name, amount, colour, g, hatched, tip); }
        public Part tip(String t)   { return new Part(name, amount, colour, go, hatched, t); }
        public Part leftOver()      { return new Part(name, amount, colour, go, true, tip); }
    }

    /** How wide a part must be drawn to carry its name and figure under it; a narrower one is keyed. */
    public static final double CAUSE_LABEL_ROOM = 70;

    /**
     * A cause bar (0.7.28): the parts laid end to end on a scale of their
     * sum, so the bar is the whole they make - a part wide enough named
     * under itself with its figure, the narrower ones and the parts at
     * nothing in a key under that ("none", greyed), each part's figure
     * written by `figure`. A part with a door is a hand and opens it.
     * `width` 0 or less fills what holds it; `band` is the bar's height.
     * Stacked bars elsewhere print money in their tooltips (stackedBar());
     * this one prints whatever its parts are counted in - points of the sick
     * rate, crimes, kilowatts.
     */
    public static CauseBar causeBar(List<Part> parts, double width, double band,
                                    java.util.function.DoubleFunction<String> figure) {
        return new CauseBar(parts, width, band, figure);
    }

    /** The bar causeBar() draws: a Pane that lays its parts, their names and its key out at whatever width it is given. */
    public static final class CauseBar extends javafx.scene.layout.Pane {
        private final List<Part> parts;
        private final double band, total;
        private final List<Region> blocks = new java.util.ArrayList<>();
        private final List<VBox> names = new java.util.ArrayList<>();
        private final List<HBox> keys = new java.util.ArrayList<>();
        /** Between the parts, under the bar, and between the key's entries and rows. */
        private static final double GAP = 2, UNDER = 3, KEY_GAP = 12, KEY_ROW = 3;

        CauseBar(List<Part> parts, double width, double band, java.util.function.DoubleFunction<String> figure) {
            this.parts = parts == null ? List.of() : parts;
            this.band = band;
            double sum = 0;
            for (Part p : this.parts) sum += Math.max(0, finite(p.amount()));
            this.total = sum;
            for (Part p : this.parts) {
                double a = finite(p.amount());
                Region r = new Region();
                r.setStyle(p.hatched()
                        ? "-fx-background-color: linear-gradient(from 0px 0px to 6px 6px, repeat, " + p.colour()
                          + " 0%, " + p.colour() + " 40%, transparent 40%, transparent 100%);"
                          + " -fx-border-color: " + p.colour() + "; -fx-border-width: 1; -fx-background-radius: 2; -fx-border-radius: 2;"
                        : "-fx-background-color: " + p.colour() + "; -fx-background-radius: 2;");
                String says = p.tip() != null ? p.tip() : p.name() + "\n" + (a > 0 ? figure.apply(a) : "none");
                Tooltip tip = new Tooltip(says);
                tip.setShowDelay(Duration.millis(200));
                Tooltip.install(r, tip);
                Label name = new Label(p.name());
                name.setWrapText(true);
                name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
                Label fig = new Label(figure.apply(a));
                fig.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_HEAD));
                VBox under = new VBox(0, name, fig);
                Region swatch = new Region();
                swatch.setMinSize(10, 10);
                swatch.setPrefSize(10, 10);
                swatch.setMaxSize(10, 10);
                swatch.setStyle("-fx-background-color: " + (a > 0 ? p.colour() : Palette.EDGE) + "; -fx-background-radius: 2;");
                Label keyed = new Label(p.name() + "  " + (a > 0 ? figure.apply(a) : "none"));
                keyed.setStyle(Palette.words(Palette.SIZE_CAPTION, a > 0 ? Palette.TEXT_MUTED : Palette.TEXT_SPENT));
                keyed.setMinWidth(Region.USE_PREF_SIZE);
                HBox key = new HBox(5, swatch, keyed);
                key.setAlignment(Pos.CENTER_LEFT);
                Tooltip.install(key, new Tooltip(says));
                if (p.go() != null) {
                    for (javafx.scene.Node n : List.of(r, under, key)) {
                        n.setCursor(javafx.scene.Cursor.HAND);
                        n.setOnMouseClicked(e -> p.go().run());
                    }
                    r.setOnMouseEntered(e -> r.setOpacity(.8));
                    r.setOnMouseExited(e -> r.setOpacity(1));
                }
                blocks.add(r);
                names.add(under);
                keys.add(key);
                getChildren().addAll(r, under, key);
            }
            setMinWidth(width > 0 ? width : 0);
            setPrefWidth(width > 0 ? width : 400);
            setMaxWidth(width > 0 ? width : Double.MAX_VALUE);
        }

        private static double finite(double v) { return Double.isFinite(v) ? v : 0; }

        /** Each part's drawn width at a bar this wide, 0 for a part at nothing. */
        private double[] widths(double w) {
            double[] out = new double[parts.size()];
            int drawn = 0;
            for (Part p : parts) if (finite(p.amount()) > 0) drawn++;
            double room = Math.max(0, w - GAP * Math.max(0, drawn - 1));
            for (int i = 0; i < out.length; i++) {
                double a = finite(parts.get(i).amount());
                out[i] = total > 0 && a > 0 ? room * a / total : 0;
            }
            return out;
        }

        /** Whether a part is named under the bar, at that width. */
        private static boolean named(double drawn) { return drawn >= CAUSE_LABEL_ROOM; }

        @Override public javafx.geometry.Orientation getContentBias() { return javafx.geometry.Orientation.HORIZONTAL; }

        @Override protected double computeMinHeight(double width) { return computePrefHeight(width); }

        @Override protected double computePrefHeight(double width) {
            double w = width > 0 ? width : getWidth() > 0 ? getWidth() : getPrefWidth();
            double[] ws = widths(w);
            double underH = 0;
            for (int i = 0; i < ws.length; i++) {
                if (named(ws[i])) underH = Math.max(underH, names.get(i).prefHeight(ws[i]));
            }
            double h = band + (underH > 0 ? UNDER + underH : 0);
            double x = 0, rowH = 0, keyH = 0;
            boolean any = false;
            for (int i = 0; i < ws.length; i++) {
                if (named(ws[i])) continue;
                HBox k = keys.get(i);
                double kw = k.prefWidth(-1), kh = k.prefHeight(-1);
                if (any && x + kw > w) { keyH += rowH + KEY_ROW; x = 0; rowH = 0; }
                x += kw + KEY_GAP;
                rowH = Math.max(rowH, kh);
                any = true;
            }
            if (any) keyH += rowH;
            return h + (any ? UNDER + 2 + keyH : 0);
        }

        @Override protected void layoutChildren() {
            double w = getWidth();
            double[] ws = widths(w);
            double x = 0, underH = 0;
            for (int i = 0; i < ws.length; i++) {
                Region r = blocks.get(i);
                r.setVisible(ws[i] > 0);
                r.resizeRelocate(x, 0, Math.max(ws[i] > 0 ? 1 : 0, ws[i]), band);
                VBox under = names.get(i);
                boolean shown = named(ws[i]);
                under.setVisible(shown);
                if (shown) {
                    double uh = under.prefHeight(ws[i]);
                    under.resizeRelocate(x, band + UNDER, ws[i], uh);
                    underH = Math.max(underH, uh);
                }
                if (ws[i] > 0) x += ws[i] + GAP;
            }
            double top = band + (underH > 0 ? UNDER + underH : 0) + UNDER + 2;
            double kx = 0, rowH = 0;
            boolean any = false;
            for (int i = 0; i < ws.length; i++) {
                HBox k = keys.get(i);
                boolean keyed = !named(ws[i]);
                k.setVisible(keyed);
                if (!keyed) continue;
                double kw = k.prefWidth(-1), kh = k.prefHeight(-1);
                if (any && kx + kw > w) { top += rowH + KEY_ROW; kx = 0; rowH = 0; }
                k.resizeRelocate(kx, top, kw, kh);
                kx += kw + KEY_GAP;
                rowH = Math.max(rowH, kh);
                any = true;
            }
        }
    }

    /* ------------------------------- a supply bar ------------------------------- */

    /**
     * A supply bar (0.7.28): what is working, inside what is built, against
     * what is needed - a segment bar of `working` in `colour` and the rest
     * of `built` in a light step of it, on a scale of the larger of the need
     * and what is built (a city that has built past its need draws the bar
     * past the line), the need a white tick named `needName` under the bar
     * when that is not null, and `more` ticks after it. The whole bar's
     * tooltip is `tip`. Care places, the school stages, the police and the
     * cells, the ground, the ovens.
     */
    public static SegmentBar supplyBar(double need, double built, double working, String colour,
                                       String needName, List<Tick> more, String tip, double width, double band) {
        double w = Double.isFinite(working) ? Math.max(0, working) : 0;
        double b = Math.max(w, Double.isFinite(built) ? built : 0);
        double n = Double.isFinite(need) ? Math.max(0, need) : 0;
        double scale = Math.max(n, b);
        List<Tick> ticks = new java.util.ArrayList<>();
        if (n > 0) ticks.add(new Tick(n, Palette.TEXT_HEAD, 2, needName, null));
        if (more != null) ticks.addAll(more);
        SegmentBar bar = segmentBar(List.of(Segment.of(w, colour), Segment.of(b - w, colour + "59")),
                scale > 0 ? scale : 1, ticks, width, band);
        if (tip != null) {
            Tooltip t = new Tooltip(tip);
            t.setShowDelay(Duration.millis(200));
            Tooltip.install(bar, t);
        }
        return bar;
    }

    /* -------------------------------- a funnel -------------------------------- */

    /** One step of a funnel: its name, its amount on the funnel's scale, its figure as written, and its tooltip (null: none). */
    public record FunnelStep(String name, double amount, String figure, String tip) { }

    /**
     * A funnel (0.7.28): the steps a figure narrows through, a row each -
     * its name at the left, a bar on the scale of the largest step, its
     * figure at the right - with the step at `binding` (-1: none) outlined
     * and its name in bold: a course's gates (could enrol, willing, start a
     * month, seats free, enrolling), who gets a doctor (to serve, built,
     * staffed, treated). A step too small to see keeps a sliver, so 37 under
     * 3,109 is still there. `width` 0 or less fills what holds it.
     */
    public static VBox funnel(List<FunnelStep> steps, double width, int binding, String colour) {
        double most = 0;
        for (FunnelStep s : steps) if (Double.isFinite(s.amount())) most = Math.max(most, s.amount());
        VBox box = new VBox(5);
        for (int i = 0; i < steps.size(); i++) {
            FunnelStep s = steps.get(i);
            boolean binds = i == binding;
            Label name = new Label(s.name());
            name.setWrapText(true);
            name.setMinWidth(120);
            name.setPrefWidth(120);
            name.setMaxWidth(120);
            name.setStyle(binds ? Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_HEAD)
                                : Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            double share = most > 0 && Double.isFinite(s.amount()) ? Math.max(0, s.amount()) / most : 0;
            Region fill = new Region();
            fill.setMinWidth(share > 0 ? 3 : 0);
            fill.setMinHeight(12);
            fill.setMaxHeight(12);
            fill.setMaxWidth(Region.USE_PREF_SIZE);
            fill.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 2;"
                    + (binds ? " -fx-border-color: " + Palette.TEXT_HEAD + "; -fx-border-width: 1.5; -fx-border-radius: 2;" : ""));
            javafx.scene.layout.StackPane holder = new javafx.scene.layout.StackPane(fill);
            holder.setAlignment(Pos.CENTER_LEFT);
            holder.setMinWidth(10);
            holder.setPrefWidth(10);
            holder.setMaxWidth(Double.MAX_VALUE);
            fill.prefWidthProperty().bind(holder.widthProperty().multiply(share));
            HBox.setHgrow(holder, Priority.ALWAYS);
            Label figure = new Label(s.figure());
            figure.setMinWidth(Region.USE_PREF_SIZE);
            figure.setStyle(Palette.figure(Palette.SIZE_LABEL, binds ? Palette.TEXT_HEAD : Palette.TEXT_BODY));
            HBox row = new HBox(Palette.GAP, name, holder, figure);
            row.setAlignment(Pos.CENTER_LEFT);
            if (s.tip() != null) {
                Tooltip t = new Tooltip(s.tip());
                t.setShowDelay(Duration.millis(200));
                Tooltip.install(row, t);
            }
            box.getChildren().add(row);
        }
        box.setMaxWidth(width > 0 ? width : Double.MAX_VALUE);
        if (width > 0) box.setPrefWidth(width);
        return box;
    }

    /* ------------------------------ an effect scale ------------------------------ */

    /**
     * An effect scale (0.7.28): what a coverage buys, as a line from what it
     * is with nobody covered to what it is with everybody, today marked on
     * it with its figure over the mark - `label` at the left in a column
     * `labelWidth` wide, the scale in the middle, `words` at the right in a
     * column `wordsWidth` wide (each 0: none). `log` lays the line out on a
     * logarithmic scale, for a factor that runs from x40 to x0.025. Its
     * tooltip gives the three figures, each written by `figure`.
     * Childcare's infant deaths, senior care's, the sick who get better;
     * Policy's "effect beside each dial" to come.
     */
    public static HBox effectScale(String label, double now, double atNone, double atAll, boolean log,
                                   java.util.function.DoubleFunction<String> figure, String colour,
                                   double labelWidth, javafx.scene.Node words, double wordsWidth) {
        EffectScale scale = new EffectScale(now, atNone, atAll, log, figure, colour);
        HBox.setHgrow(scale, Priority.ALWAYS);
        HBox row = new HBox(Palette.GAP_LOOSE);
        row.setAlignment(Pos.CENTER_LEFT);
        if (labelWidth > 0) {
            Label l = new Label(label);
            l.setWrapText(true);
            l.setMinWidth(labelWidth);
            l.setPrefWidth(labelWidth);
            l.setMaxWidth(labelWidth);
            l.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_HEAD));
            row.getChildren().add(l);
        }
        row.getChildren().add(scale);
        if (words != null) {
            if (words instanceof Region r && wordsWidth > 0) {
                r.setMinWidth(wordsWidth);
                r.setPrefWidth(wordsWidth);
                r.setMaxWidth(wordsWidth);
            }
            row.getChildren().add(words);
        }
        Tooltip tip = new Tooltip("at nobody covered " + figure.apply(atNone) + "\nnow " + figure.apply(now)
                + "\nat everyone covered " + figure.apply(atAll));
        tip.setShowDelay(Duration.millis(200));
        Tooltip.install(scale, tip);
        return row;
    }

    /** The line effectScale() draws: a Pane that lays its ends and today's mark out at whatever width it is given. */
    public static final class EffectScale extends javafx.scene.layout.Pane {
        private final double at;
        private final Region line = new Region(), mark = new Region(), left = new Region(), right = new Region();
        private final Label now, none, all;
        /** Its height, how far down it the line runs, and the mark's size. */
        private static final double TALL = 44, Y = 22, DOT = 10;

        EffectScale(double now, double atNone, double atAll, boolean log,
                    java.util.function.DoubleFunction<String> figure, String colour) {
            double t;
            if (log && now > 0 && atNone > 0 && atAll > 0 && atNone != atAll) {
                t = (Math.log(now) - Math.log(atNone)) / (Math.log(atAll) - Math.log(atNone));
            } else {
                t = atAll != atNone ? (now - atNone) / (atAll - atNone) : 0;
            }
            this.at = Double.isFinite(t) ? Math.max(0, Math.min(1, t)) : 0;
            line.setStyle("-fx-background-color: " + Palette.EDGE + "; -fx-background-radius: 1;");
            left.setStyle("-fx-background-color: " + Palette.TEXT_SPENT + ";");
            right.setStyle("-fx-background-color: " + Palette.TEXT_SPENT + ";");
            mark.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: " + DOT / 2 + ";"
                    + " -fx-border-color: " + Palette.TEXT_HEAD + "; -fx-border-radius: " + DOT / 2 + "; -fx-border-width: 1.5;");
            this.now = new Label(figure.apply(now));
            this.now.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT_HEAD));
            this.none = new Label("nobody " + figure.apply(atNone));
            this.none.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            this.all = new Label("everybody " + figure.apply(atAll));
            this.all.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            getChildren().addAll(line, left, right, mark, this.now, none, all);
            setMinWidth(120);
            setPrefWidth(400);
            setMaxWidth(Double.MAX_VALUE);
            setMinHeight(TALL);
            setPrefHeight(TALL);
        }

        @Override protected void layoutChildren() {
            double w = getWidth();
            double pad = DOT / 2;
            line.resizeRelocate(pad, Y - 1, Math.max(0, w - 2 * pad), 2);
            left.resizeRelocate(pad, Y - 5, 1, 10);
            right.resizeRelocate(w - pad - 1, Y - 5, 1, 10);
            double x = pad + at * Math.max(0, w - 2 * pad);
            mark.resizeRelocate(x - DOT / 2, Y - DOT / 2, DOT, DOT);
            double nw = now.prefWidth(-1), nh = now.prefHeight(-1);
            now.resizeRelocate(Math.max(0, Math.min(w - nw, x - nw / 2)), Math.max(0, Y - DOT / 2 - nh - 1), nw, nh);
            double aw = none.prefWidth(-1), ah = none.prefHeight(-1);
            none.resizeRelocate(0, Y + 7, aw, ah);
            double bw = all.prefWidth(-1), bh = all.prefHeight(-1);
            all.resizeRelocate(Math.max(aw + 8, w - bw), Y + 7, bw, bh);
        }
    }

    /* ------------------------------- cohort bars ------------------------------- */

    /**
     * Cohort bars (0.7.28): everybody part way through something, a bar
     * each on the tallest bar's scale - the nearest end at the left, named
     * `near` under it, the farthest at the right, `far` - each bar's figure
     * and `slotName` in its tooltip. From `dangerFrom` on (-1: never) the
     * bars take `danger` and a dashed line stands before them named
     * `dangerName`. THE LAG IS THE MECHANIC (the course pages' bars, 2026-09-11): a
     * medical school built today is doctors years from now, and a row of
     * cohorts says it as no figure does. A course's cohorts, a sentence's
     * months, an illness's.
     */
    public static CohortBars cohortBars(double[] counts, double width, double height, String colour,
                                        int dangerFrom, String danger, String dangerName, String near, String far,
                                        java.util.function.DoubleFunction<String> figure,
                                        java.util.function.IntFunction<String> slotName) {
        return new CohortBars(counts, width, height, colour, dangerFrom, danger, dangerName, near, far, figure, slotName);
    }

    /** The bars cohortBars() draws: a Pane that lays them out at whatever width it is given. */
    public static final class CohortBars extends javafx.scene.layout.Pane {
        private final double[] counts;
        private final double peak;
        private final int dangerFrom;
        private final List<Region> bars = new java.util.ArrayList<>();
        private final javafx.scene.shape.Line divider = new javafx.scene.shape.Line();
        private final Label dividerName, near, far;
        /** Room under the bars for the near and far names. */
        private static final double AXIS = 15;

        CohortBars(double[] counts, double width, double height, String colour, int dangerFrom, String danger,
                   String dangerName, String near, String far, java.util.function.DoubleFunction<String> figure,
                   java.util.function.IntFunction<String> slotName) {
            this.counts = counts == null ? new double[0] : counts;
            this.dangerFrom = dangerFrom;
            double top = 0;
            for (double c : this.counts) if (Double.isFinite(c)) top = Math.max(top, c);
            this.peak = top;
            for (int i = 0; i < this.counts.length; i++) {
                double c = this.counts[i];
                Region r = new Region();
                boolean late = dangerFrom >= 0 && i >= dangerFrom;
                r.setStyle("-fx-background-color: " + (c > 0 ? (late ? danger : colour) : Palette.HAIRLINE) + ";"
                        + " -fx-background-radius: 2 2 0 0;");
                Tooltip tip = new Tooltip((slotName == null ? "" : slotName.apply(i) + "\n")
                        + (Double.isFinite(c) ? figure.apply(c) : "—"));
                tip.setShowDelay(Duration.millis(150));
                Tooltip.install(r, tip);
                bars.add(r);
                getChildren().add(r);
            }
            divider.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_MUTED));
            divider.getStrokeDashArray().addAll(3.0, 3.0);
            divider.setVisible(dangerFrom > 0 && dangerFrom < this.counts.length);
            this.dividerName = new Label(dangerName == null ? "" : dangerName);
            this.dividerName.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            this.dividerName.setVisible(divider.isVisible() && dangerName != null);
            this.near = new Label(near == null ? "" : near);
            this.near.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
            this.far = new Label(far == null ? "" : far);
            this.far.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
            getChildren().addAll(divider, dividerName, this.near, this.far);
            setMinWidth(width > 0 ? width : 0);
            setPrefWidth(width > 0 ? width : 400);
            setMaxWidth(width > 0 ? width : Double.MAX_VALUE);
            setMinHeight(height);
            setPrefHeight(height);
        }

        @Override protected void layoutChildren() {
            double w = getWidth(), h = getHeight();
            int n = counts.length;
            if (n == 0) return;
            double nameRoom = dividerName.isVisible() ? dividerName.prefHeight(-1) + 2 : 0;
            double plot = Math.max(4, h - AXIS - nameRoom);
            double gap = n > 40 ? 1 : 3, each = Math.max(1, (w - gap * (n - 1)) / n);
            for (int i = 0; i < n; i++) {
                double c = Double.isFinite(counts[i]) ? Math.max(0, counts[i]) : 0;
                double tall = peak > 0 ? Math.max(1, c / peak * plot) : 1;
                bars.get(i).resizeRelocate(i * (each + gap), nameRoom + plot - tall, each, tall);
            }
            if (divider.isVisible()) {
                double x = Math.floor(dangerFrom * (each + gap) - gap / 2) + .5;
                divider.setStartX(x);
                divider.setEndX(x);
                divider.setStartY(nameRoom);
                divider.setEndY(nameRoom + plot);
                double dw = dividerName.prefWidth(-1);
                dividerName.resizeRelocate(Math.max(0, Math.min(w - dw, x + 3)), 0, dw, dividerName.prefHeight(-1));
            }
            double nw = near.prefWidth(-1), fw = far.prefWidth(-1);
            near.resizeRelocate(0, nameRoom + plot + 2, nw, near.prefHeight(-1));
            far.resizeRelocate(Math.max(nw + 8, w - fw), nameRoom + plot + 2, fw, far.prefHeight(-1));
        }
    }

    /* ---------------------------- a door, and a spark ---------------------------- */

    /**
     * A door (0.7.28): a few words and a "›" in a colour, underlined under
     * the pointer - Build's "why ›" in the people teal, "Set the fee ›"; the
     * Services cards' "Build for it ›" was one until 0.7.34, when a door to
     * Build or the land office became a pill (doorPill()). A door
     * inside a clickable card opens itself and not the card.
     */
    public static Label door(String text, String colour, Runnable go) {
        Label l = new Label(text + " ›");
        String rest = Palette.words(Palette.SIZE_LABEL, colour) + " -fx-cursor: hand;";
        l.setStyle(rest);
        l.setMinWidth(Region.USE_PREF_SIZE);
        l.setOnMouseEntered(e -> l.setStyle(rest + " -fx-underline: true;"));
        l.setOnMouseExited(e -> l.setStyle(rest));
        l.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            e.consume();
            go.run();
        });
        return l;
    }

    /**
     * A sparkline of a history series (0.7.28): the header tiles' own
     * (UserInterface.Sparkline - the last ten years, a dot on the latest
     * month, each January marked), at the caller's size: the Services KPI
     * cells' 64 x 16.
     */
    public static Region sparkline(double[] series, List<Integer> months, String colour, double width, double height) {
        UserInterface.Sparkline line = new UserInterface.Sparkline(series, months, colour);
        line.setMinSize(width, height);
        line.setPrefSize(width, height);
        line.setMaxSize(width, height);
        return line;
    }

    /* =====================================================================
       THE PIECES THE INFRASTRUCTURE SCREEN ADDED (0.7.29)

       The fourth of the rail's screens redrawn in Build's style needed three
       more. ROWS ON ONE SCALE: a stack of bars sharing one scale, each a
       name, its runs and its figure, with rules drawn through every row -
       the Roads page's walk from the trips the city makes to what is on its
       road (each step starting where the running total stood, what is taken
       away drawn hollow, the capacity and the free-flow line through all of
       them), Transit's funnel through its three ceilings, and the Freight
       page's bar a good with its "by lorry" mark. A HERO CARD: the one
       picture a page leads with, its words at the left. And a DETAILS fold
       whose state lives on the screen that draws it and whose inside is
       built only when it is open - Services' fold in shape (it keeps its own). The
       page chips take an icon each (chipStrip() above). The design study is
       the project's spec-infra-0729.md, sections 6 and 7.
       ===================================================================== */

    /** A page's lead picture as a card (0.7.29): `left`, `leftWidth` wide, its words; `right`, taking the rest, the picture. */
    public static HBox heroCard(javafx.scene.Node left, double leftWidth, javafx.scene.Node right) {
        if (left instanceof Region r) {
            r.setMinWidth(leftWidth);
            r.setPrefWidth(leftWidth);
            r.setMaxWidth(leftWidth);
        }
        HBox card = new HBox(24, left);
        if (right != null) {
            HBox.setHgrow(right, Priority.ALWAYS);
            if (right instanceof Region r) {
                r.setMinWidth(0);
                r.setMaxWidth(Double.MAX_VALUE);
            }
            card.getChildren().add(right);
        }
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-padding: 14 16 14 16; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return card;
    }

    /**
     * A fold (0.7.29): "details ▸ caption", and what is inside when it is
     * open - built only then, by `body`. Its state is `key` in `open`, a set
     * the drawing screen keeps, so a month's redraw leaves it as the player
     * left it; a click toggles it and runs `redraw`. The old statements and
     * grids live in these, so nothing a page said before its redesign is
     * gone.
     */
    public static VBox details(String key, String caption, java.util.Set<String> open, Runnable redraw,
                               java.util.function.Supplier<javafx.scene.Node> body) {
        boolean shown = open.contains(key);
        Label toggle = new Label((shown ? "details ▾  " : "details ▸  ") + caption);
        toggle.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.ACCENT) + " -fx-cursor: hand;");
        toggle.setWrapText(true);
        VBox box = new VBox(4, toggle);
        if (shown) {
            javafx.scene.Node inside = body.get();
            if (inside != null) box.getChildren().add(inside);
        }
        toggle.setOnMouseClicked(e -> {
            if (!open.remove(key)) open.add(key);
            redraw.run();
        });
        box.setStyle("-fx-padding: 4 0 0 0;");
        return box;
    }

    /* ----------------------------- rows on one scale ----------------------------- */

    /**
     * One stretch of a row on a shared scale (0.7.29): where it starts and
     * how long it is, in the scale's units; its colour; whether it is HOLLOW
     * (outlined()) - outlined and faintly tinted, what is taken away or what
     * is not there - and its tooltip and what a click on it does (each may be
     * null).
     */
    public record Run(double from, double amount, String colour, boolean hollow, String tip, Runnable go) {
        /** A filled stretch. */
        public static Run of(double from, double amount, String colour) {
            return new Run(from, amount, colour, false, null, null);
        }
        public Run outlined()     { return new Run(from, amount, colour, true, tip, go); }
        public Run tip(String t)  { return new Run(from, amount, colour, hollow, t, go); }
        public Run go(Runnable g) { return new Run(from, amount, colour, hollow, tip, g); }
    }

    /**
     * One row of scaleRows() (0.7.29): its name at the left, which wraps and
     * is never cut; its runs; its figure at the right; and - each may be
     * null - marks across this row alone (a good's "by lorry"), the words a
     * row at nothing shows in place of its bar ("no transit yet"), an (i)
     * after the name, a tag after the last run in a colour of its own
     * ("lowest"), an icon before the name, a tooltip over the whole row, a
     * click on its name and figure, and whether the name is in bold
     * (strong()). A
     * CAPTION is a name across the row and no bar: a group's heading inside
     * the stack ("Three ceilings: the lowest wins").
     */
    public record ScaleRow(String name, String figure, List<Run> runs, List<Tick> marks, String empty,
                           String info, String tag, String tagColour, String icon, String tip,
                           Runnable go, boolean bold, boolean caption) {
        public static ScaleRow of(String name, String figure, List<Run> runs) {
            return new ScaleRow(name, figure, runs, List.of(), null, null, null, null, null, null, null, false, false);
        }
        public static ScaleRow caption(String name, String info) {
            return new ScaleRow(name, null, List.of(), List.of(), null, info, null, null, null, null, null, false, true);
        }
        public ScaleRow marks(List<Tick> m) {
            return new ScaleRow(name, figure, runs, m, empty, info, tag, tagColour, icon, tip, go, bold, caption);
        }
        public ScaleRow empty(String words) {
            return new ScaleRow(name, figure, runs, marks, words, info, tag, tagColour, icon, tip, go, bold, caption);
        }
        public ScaleRow info(String i) {
            return new ScaleRow(name, figure, runs, marks, empty, i, tag, tagColour, icon, tip, go, bold, caption);
        }
        public ScaleRow tag(String t, String colour) {
            return new ScaleRow(name, figure, runs, marks, empty, info, t, colour, icon, tip, go, bold, caption);
        }
        public ScaleRow icon(String svg) {
            return new ScaleRow(name, figure, runs, marks, empty, info, tag, tagColour, svg, tip, go, bold, caption);
        }
        public ScaleRow tip(String t) {
            return new ScaleRow(name, figure, runs, marks, empty, info, tag, tagColour, icon, t, go, bold, caption);
        }
        public ScaleRow go(Runnable g) {
            return new ScaleRow(name, figure, runs, marks, empty, info, tag, tagColour, icon, tip, g, bold, caption);
        }
        public ScaleRow strong() {
            return new ScaleRow(name, figure, runs, marks, empty, info, tag, tagColour, icon, tip, go, true, caption);
        }
    }

    /** A rule through every row of scaleRows() (0.7.29): where, in the scale's units; its colour; dashed or solid; its name over the rows, its tooltip, and what a click on the name does (each may be null). */
    public record Rule(double at, String colour, boolean dashed, String name, String tip, Runnable go) { }

    /**
     * Rows on one scale (0.7.29): each row's name in a column `nameWidth`
     * wide, its bar - a track `band` tall with its runs laid on it where
     * they start, on a scale of `scale` - and its figure in a column
     * `figureWidth` wide; the rules drawn through every row, named over
     * them. A run past the end of the scale is cut at the track's end and a
     * "›" says so; a rule or a mark outside it is not drawn. The Roads page's
     * walk from trips to the road, Transit's funnel through its ceilings, a
     * bar a good on the Freight page - and Services' capacity bars, the
     * bank's balance sheet to come.
     */
    public static ScaleRows scaleRows(List<ScaleRow> rows, double scale, List<Rule> rules,
                                      double nameWidth, double figureWidth, double band) {
        return new ScaleRows(rows, scale, rules, nameWidth, figureWidth, band);
    }

    /** The stack scaleRows() draws: a Pane that lays its rows, their runs and its rules out at whatever width it is given. */
    public static final class ScaleRows extends javafx.scene.layout.Pane {
        /** A row's least height, the gap either side of the bar, the room the rules' names take over the rows, the gap before a tag, and an icon's size. */
        private static final double ROW = 26, GAP = 10, RULE_NAMES = 16, TAG_GAP = 6, ICON = 12;
        private final List<ScaleRow> rows;
        private final List<Rule> rules;
        private final double scale, nameWidth, figureWidth, band;
        private final List<Label> names = new java.util.ArrayList<>(), figures = new java.util.ArrayList<>();
        private final List<Label> empties = new java.util.ArrayList<>(), tags = new java.util.ArrayList<>();
        private final List<Label> overs = new java.util.ArrayList<>();
        private final List<javafx.scene.Node> infos = new java.util.ArrayList<>(), icons = new java.util.ArrayList<>();
        private final List<Region> tracks = new java.util.ArrayList<>();
        private final List<List<Region>> drawn = new java.util.ArrayList<>(), marked = new java.util.ArrayList<>();
        private final List<javafx.scene.shape.Line> lines = new java.util.ArrayList<>();
        private final List<Label> ruleNames = new java.util.ArrayList<>();

        ScaleRows(List<ScaleRow> rows, double scale, List<Rule> rules, double nameWidth, double figureWidth,
                  double band) {
            this.rows = rows == null ? List.of() : rows;
            this.rules = rules == null ? List.of() : rules;
            this.scale = scale > 0 && Double.isFinite(scale) ? scale : 1;
            this.nameWidth = nameWidth;
            this.figureWidth = figureWidth;
            this.band = band;
            for (ScaleRow row : this.rows) {
                Label name = new Label(row.name() == null ? "" : row.name());
                name.setWrapText(true);
                name.setStyle(row.caption() ? Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL)
                        : row.bold() ? Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_HEAD)
                        : Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
                names.add(name);
                getChildren().add(name);
                javafx.scene.Node info = row.info() == null ? null : infoButton(row.info(), true);
                infos.add(info);
                if (info != null) getChildren().add(info);
                javafx.scene.Node mark = row.icon() == null ? null : icon(row.icon(), Palette.TEXT_MUTED, ICON);
                icons.add(mark);
                if (mark != null) getChildren().add(mark);

                Region track = new Region();
                track.setStyle("-fx-background-color: " + Palette.CONTROL + "; -fx-background-radius: " + band / 2 + ";");
                tracks.add(track);
                getChildren().add(track);
                List<Region> parts = new java.util.ArrayList<>();
                for (Run run : row.runs() == null ? List.<Run>of() : row.runs()) {
                    Region r = new Region();
                    r.setStyle(run.hollow()
                            ? "-fx-background-color: " + run.colour() + "33; -fx-border-color: " + run.colour() + ";"
                              + " -fx-border-width: 1.5; -fx-background-radius: " + band / 2 + "; -fx-border-radius: " + band / 2 + ";"
                            : "-fx-background-color: " + run.colour() + "; -fx-background-radius: " + band / 2 + ";");
                    if (run.tip() != null || row.tip() != null) {
                        Tooltip t = new Tooltip(run.tip() != null ? run.tip() : row.tip());
                        t.setShowDelay(Duration.millis(200));
                        Tooltip.install(r, t);
                    }
                    if (run.go() != null) {
                        r.setCursor(javafx.scene.Cursor.HAND);
                        r.setOnMouseClicked(e -> run.go().run());
                    }
                    parts.add(r);
                    getChildren().add(r);
                }
                drawn.add(parts);
                List<Region> ticks = new java.util.ArrayList<>();
                for (Tick t : row.marks() == null ? List.<Tick>of() : row.marks()) {
                    Region m = new Region();
                    m.setStyle("-fx-background-color: " + t.colour() + ";");
                    if (t.tip() != null) {
                        Tooltip tip = new Tooltip(t.tip());
                        tip.setShowDelay(Duration.millis(200));
                        Tooltip.install(m, tip);
                    }
                    ticks.add(m);
                    getChildren().add(m);
                }
                marked.add(ticks);
                Label over = new Label("›");
                over.setStyle(Palette.strong(Palette.SIZE_BODY, Palette.TEXT_LABEL));
                over.setMouseTransparent(true);
                overs.add(over);
                getChildren().add(over);
                Label empty = new Label(row.empty() == null ? "" : row.empty());
                empty.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
                empties.add(empty);
                getChildren().add(empty);
                Label tag = new Label(row.tag() == null ? "" : row.tag());
                if (row.tag() != null) {
                    String c = row.tagColour() == null ? Palette.TEXT_LABEL : row.tagColour();
                    tag.setStyle(Palette.words(Palette.SIZE_CAPTION, c) + " -fx-background-color: " + tint(c, .16) + ";"
                            + " -fx-background-radius: 9; -fx-padding: 1 7 1 7;");
                }
                tag.setMinWidth(Region.USE_PREF_SIZE);
                tags.add(tag);
                getChildren().add(tag);
                Label figure = new Label(row.figure() == null ? "" : row.figure());
                figure.setMinWidth(Region.USE_PREF_SIZE);
                figure.setStyle(Palette.figure(Palette.SIZE_LABEL, row.bold() ? Palette.TEXT_HEAD : Palette.TEXT_BODY));
                figures.add(figure);
                getChildren().add(figure);
                if (row.tip() != null) {
                    for (javafx.scene.Node n : new javafx.scene.Node[] {name, track, figure}) {
                        Tooltip t = new Tooltip(row.tip());
                        t.setShowDelay(Duration.millis(200));
                        Tooltip.install(n, t);
                    }
                }
                if (row.go() != null) {
                    for (Label l : new Label[] {name, figure}) {
                        l.setCursor(javafx.scene.Cursor.HAND);
                        l.setOnMouseClicked(e -> row.go().run());
                    }
                }
            }
            for (Rule rule : this.rules) {
                javafx.scene.shape.Line line = new javafx.scene.shape.Line();
                line.setStroke(javafx.scene.paint.Color.web(rule.colour()));
                line.setStrokeWidth(rule.dashed() ? 1 : 1.5);
                if (rule.dashed()) line.getStrokeDashArray().addAll(4.0, 3.0);
                line.setMouseTransparent(true);
                lines.add(line);
                getChildren().add(line);
                Label name = new Label(rule.name() == null ? "" : rule.name());
                name.setStyle(Palette.words(Palette.SIZE_CAPTION, rule.go() != null ? Palette.ACCENT : Palette.TEXT_LABEL));
                name.setMinWidth(Region.USE_PREF_SIZE);
                if (rule.tip() != null) {
                    Tooltip t = new Tooltip(rule.tip());
                    t.setShowDelay(Duration.millis(200));
                    Tooltip.install(name, t);
                }
                if (rule.go() != null) {
                    String rest = name.getStyle() + " -fx-cursor: hand;";
                    name.setStyle(rest);
                    name.setOnMouseEntered(e -> name.setStyle(rest + " -fx-underline: true;"));
                    name.setOnMouseExited(e -> name.setStyle(rest));
                    name.setOnMouseClicked(e -> rule.go().run());
                }
                ruleNames.add(name);
                getChildren().add(name);
            }
            setMinWidth(nameWidth + figureWidth + 2 * GAP + 60);
            setPrefWidth(nameWidth + figureWidth + 2 * GAP + 400);
            setMaxWidth(Double.MAX_VALUE);
        }

        /**
         * Where each rule's name goes at a width, as {left, line} per rule,
         * and how many lines of names that takes (the last entry's line + 1,
         * 0 with none named). A name is centred on its rule; two that would
         * touch are pulled apart - the left one ending at its rule, the right
         * one starting at its - and a pair still touching takes a second line.
         */
        private double[][] ruleNameAt(double w) {
            double x0 = nameWidth + GAP, x1 = Math.max(x0 + 10, w - figureWidth - GAP), barW = x1 - x0;
            int n = rules.size();
            double[][] at = new double[n + 1][2];
            Integer[] order = new Integer[n];
            for (int k = 0; k < n; k++) order[k] = k;
            java.util.Arrays.sort(order, (p, q) -> Double.compare(rules.get(p).at(), rules.get(q).at()));
            int lines = 0;
            double[] lastRight = {-1e9, -1e9};
            int prev = -1;
            for (int k : order) {
                Rule rule = rules.get(k);
                if (rule.name() == null || !(rule.at() >= 0 && rule.at() <= scale)) { at[k][0] = Double.NaN; continue; }
                double x = x0 + barW * rule.at() / scale, rw = ruleNames.get(k).prefWidth(-1);
                double left = Math.max(x0, Math.min(x1 - rw, x - rw / 2));
                int line = 0;
                if (left < lastRight[0] + 6 && prev >= 0) {
                    // Pull the two apart: the earlier ends at its rule, this one starts at its.
                    double px = x0 + barW * rules.get(prev).at() / scale, pw = ruleNames.get(prev).prefWidth(-1);
                    double pLeft = Math.max(x0, px - pw - 2);
                    at[prev][0] = pLeft;
                    lastRight[0] = pLeft + pw;
                    left = Math.max(x0, Math.min(x1 - rw, x + 2));
                    if (left < lastRight[0] + 6) line = 1;
                }
                at[k][0] = left;
                at[k][1] = line;
                lastRight[line] = left + rw;
                lines = Math.max(lines, line + 1);
                prev = k;
            }
            at[n][0] = lines;
            return at;
        }

        /** The room the rules' names take over the rows, at a width. */
        private double ruleRoom(double w) {
            return rules.isEmpty() ? 0 : ruleNameAt(w)[rules.size()][0] * RULE_NAMES;
        }

        /** The room a row's name has, after its icon and its (i). */
        private double nameRoom(int i, double w) {
            ScaleRow row = rows.get(i);
            double room = row.caption() ? Math.max(40, w - 24) : nameWidth;
            if (icons.get(i) != null) room -= ICON + 4;
            if (infos.get(i) != null) room -= INFO_SIZE + 4;
            return Math.max(30, room);
        }

        /** A row's height: its name's lines, and never less than ROW (a caption a little less). */
        private double rowHeight(int i, double w) {
            Label name = names.get(i);
            double h = name.prefHeight(nameRoom(i, w));
            return Math.max(rows.get(i).caption() ? ROW - 6 : ROW, h + 6);
        }

        /** Its height follows its width - the rules' names take a second line when two would touch - so a parent asks at the width it will give. */
        @Override public javafx.geometry.Orientation getContentBias() { return javafx.geometry.Orientation.HORIZONTAL; }

        @Override protected double computePrefHeight(double width) {
            double w = width > 0 ? width : getPrefWidth();
            double h = ruleRoom(w);
            for (int i = 0; i < rows.size(); i++) h += rowHeight(i, w);
            return h;
        }

        @Override protected double computeMinHeight(double width) { return computePrefHeight(width); }

        @Override protected void layoutChildren() {
            double w = getWidth();
            double x0 = nameWidth + GAP, x1 = Math.max(x0 + 10, w - figureWidth - GAP), barW = x1 - x0;
            double[][] nameAt = ruleNameAt(w);
            double top = rules.isEmpty() ? 0 : nameAt[rules.size()][0] * RULE_NAMES, y = top;
            for (int i = 0; i < rows.size(); i++) {
                ScaleRow row = rows.get(i);
                double h = rowHeight(i, w), mid = y + h / 2;
                Label name = names.get(i);
                double room = nameRoom(i, w);
                double nx = 0;
                javafx.scene.Node mark = icons.get(i);
                if (mark != null) {
                    mark.resizeRelocate(0, mid - ICON / 2, ICON, ICON);
                    nx = ICON + 4;
                }
                double nw = Math.min(name.prefWidth(-1), room), nh = name.prefHeight(nw);
                name.resizeRelocate(nx, mid - nh / 2, nw, nh);
                javafx.scene.Node info = infos.get(i);
                if (info != null) info.resizeRelocate(nx + nw + 4, mid - INFO_SIZE / 2, INFO_SIZE, INFO_SIZE);

                Region track = tracks.get(i);
                Label empty = empties.get(i), tag = tags.get(i), figure = figures.get(i), over = overs.get(i);
                List<Region> parts = drawn.get(i);
                boolean anything = false;
                for (Run run : row.runs() == null ? List.<Run>of() : row.runs()) anything |= run.amount() > 0;
                boolean showEmpty = !row.caption() && row.empty() != null && !anything;
                track.setVisible(!row.caption() && !showEmpty);
                empty.setVisible(showEmpty);
                double by = mid - band / 2;
                track.resizeRelocate(x0, by, barW, band);
                if (showEmpty) {
                    double ew = Math.min(empty.prefWidth(-1), barW), eh = empty.prefHeight(ew);
                    empty.resizeRelocate(x0, mid - eh / 2, ew, eh);
                }
                double end = x0;
                boolean past = false;
                for (int k = 0; k < parts.size(); k++) {
                    Run run = row.runs().get(k);
                    Region r = parts.get(k);
                    double from = Double.isFinite(run.from()) ? run.from() : 0;
                    double to = from + (Double.isFinite(run.amount()) ? Math.max(0, run.amount()) : 0);
                    past |= to > scale * (1 + 1e-9);
                    double a = Math.max(0, Math.min(scale, from)), b = Math.max(0, Math.min(scale, to));
                    double left = x0 + barW * a / scale, right = x0 + barW * b / scale;
                    boolean on = !showEmpty && !row.caption() && run.amount() > 0;
                    r.setVisible(on);
                    double rw = on ? Math.max(2, right - left) : 0;
                    r.resizeRelocate(left, by, rw, band);
                    if (on) end = Math.max(end, left + rw);
                }
                over.setVisible(past);
                double ow = over.prefWidth(-1), oh = over.prefHeight(-1);
                over.resizeRelocate(x1 + 1, mid - oh / 2, ow, oh);
                List<Region> ticks = marked.get(i);
                for (int k = 0; k < ticks.size(); k++) {
                    Tick t = row.marks().get(k);
                    Region m = ticks.get(k);
                    boolean on = !row.caption() && Double.isFinite(t.at()) && t.at() >= 0 && t.at() <= scale;
                    m.setVisible(on);
                    double at = x0 + Math.round(barW * (on ? t.at() : 0) / scale);
                    m.resizeRelocate(Math.max(x0, Math.min(x1 - t.width(), at - t.width() / 2)), by - 4, t.width(), band + 8);
                }
                tag.setVisible(row.tag() != null && !row.caption());
                double tw = tag.prefWidth(-1), th = tag.prefHeight(-1);
                tag.resizeRelocate(Math.min(end + TAG_GAP, Math.max(x0, x1 - tw)), mid - th / 2, tw, th);
                figure.setVisible(!row.caption());
                double fw = figure.prefWidth(-1), fh = figure.prefHeight(-1);
                figure.resizeRelocate(w - fw, mid - fh / 2, fw, fh);
                y += h;
            }
            for (int k = 0; k < rules.size(); k++) {
                Rule rule = rules.get(k);
                javafx.scene.shape.Line line = lines.get(k);
                Label name = ruleNames.get(k);
                boolean on = Double.isFinite(rule.at()) && rule.at() >= 0 && rule.at() <= scale;
                line.setVisible(on);
                name.setVisible(on && rule.name() != null);
                if (!on) continue;
                double x = Math.floor(x0 + barW * rule.at() / scale) + .5;
                line.setStartX(x);
                line.setEndX(x);
                line.setStartY(top - 2);
                line.setEndY(y);
                double rw = name.prefWidth(-1), rh = name.prefHeight(-1);
                if (Double.isNaN(nameAt[k][0])) continue;
                name.resizeRelocate(nameAt[k][0], nameAt[k][1] * RULE_NAMES, rw, rh);
            }
        }
    }

    /* =====================================================================
       THE PIECES THE SECTORS SCREEN ADDED (0.7.30)

       The fifth of the rail's screens redrawn in Build's style needed two
       more. PIPS: a count against a limit, a dot each - the Investors page's
       months of losses against the six at which a business starts selling
       its buildings back, and the bank's watch months and the bond ban to
       come. A FACT GRID: a sector's own lines (Sector.ownLines()) laid out
       as two columns of label and figure under small headings, each note
       the (i) of the line above it, so forty-six notes on fifteen pages stop
       being paragraphs without one being rewritten (the project's
       spec-sectors-0730.md, D7). The waterfall, the segment bar, the ring
       and the chips it draws everything else with were here already.
       ===================================================================== */

    /**
     * Pips: `of` dots in a row, the first `n` filled in `colour` and the rest
     * an outline - "4 of 6". A count past `of` fills them all; the caller
     * writes the count out.
     */
    public static HBox pips(int n, int of, String colour, double size) {
        HBox row = new HBox(Math.max(2, size / 2));
        for (int i = 0; i < of; i++) {
            Region dot = new Region();
            dot.setMinSize(size, size);
            dot.setPrefSize(size, size);
            dot.setMaxSize(size, size);
            boolean on = i < n;
            dot.setStyle("-fx-background-radius: " + size / 2 + "; -fx-border-radius: " + size / 2 + ";"
                    + " -fx-background-color: " + (on ? colour : "transparent") + ";"
                    + " -fx-border-color: " + (on ? colour : Palette.CONTROL_EDGE) + "; -fx-border-width: 1.2;");
            row.getChildren().add(dot);
        }
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * A fact grid: a sector's lines as figures. A HEAD is a small heading; a
     * LINE is its label at the left, wrapping, and its figure at the right in
     * the colour its tone asks for; a NOTE is the (i) of the line above it -
     * or, with nothing above it, a line of its own in the muted grey with the
     * whole sentence behind its (i); a note in two layers keeps its short
     * line on the grid and its paragraph behind the (i). The headings' groups
     * are laid into `columns` columns, each kept whole, the next group to the
     * shortest column; `tone` turns a line's tone into a colour.
     */
    public static javafx.scene.layout.GridPane factGrid(List<ham.citybuildersim.Sector.Line> lines, int columns,
                                                        java.util.function.Function<ham.citybuildersim.Sector.Line.Tone, String> tone) {
        List<VBox> groups = new java.util.ArrayList<>();
        VBox group = null;
        HBox last = null;
        for (ham.citybuildersim.Sector.Line l : lines) {
            switch (l.kind()) {
                case HEAD -> {
                    group = new VBox(2);
                    Label h = new Label(l.label().toUpperCase());
                    h.setWrapText(true);
                    h.setStyle(Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL) + " -fx-padding: 0 0 2 0;");
                    group.getChildren().add(h);
                    groups.add(group);
                    last = null;
                }
                case NOTE -> {
                    if (group == null) { group = new VBox(2); groups.add(group); }
                    boolean layered = !l.value().isEmpty();
                    if (last != null && !layered) {
                        javafx.scene.Node dot = infoButton(l.label(), false);
                        last.getChildren().add(1, dot);
                        continue;
                    }
                    HBox note = infoLine(layered ? l.label() : firstWords(l.label()), layered ? l.value() : l.label(),
                            false, Palette.SIZE_CAPTION, Palette.TEXT_MUTED, STATEMENT);
                    group.getChildren().add(note);
                }
                default -> {
                    if (group == null) { group = new VBox(2); groups.add(group); }
                    Label what = new Label(l.label());
                    what.setWrapText(true);
                    what.setMinWidth(0);
                    what.setStyle(Palette.words(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
                    Region gap = new Region();
                    HBox.setHgrow(gap, Priority.ALWAYS);
                    Label figure = new Label(l.value());
                    figure.setWrapText(true);
                    figure.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
                    figure.setAlignment(Pos.CENTER_RIGHT);
                    figure.setMinWidth(Region.USE_PREF_SIZE);
                    String colour = tone == null ? null : tone.apply(l.tone());
                    figure.setStyle(Palette.figure(Palette.SIZE_LABEL + 1, colour == null ? Palette.TEXT_HEAD : colour));
                    HBox row = new HBox(Palette.GAP_TIGHT, what, gap, figure);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setStyle("-fx-padding: 2 0 2 0; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0;");
                    HBox.setHgrow(what, Priority.SOMETIMES);
                    group.getChildren().add(row);
                    last = row;
                }
            }
        }
        javafx.scene.layout.GridPane grid = equalColumns(Math.max(1, columns), 24);
        int[] used = new int[Math.max(1, columns)];
        VBox[] col = new VBox[Math.max(1, columns)];
        for (int c = 0; c < col.length; c++) {
            col[c] = new VBox(Palette.GAP_LOOSE);
            grid.add(col[c], c, 0);
            javafx.scene.layout.GridPane.setValignment(col[c], javafx.geometry.VPos.TOP);
        }
        for (VBox g : groups) {
            int at = 0;
            for (int c = 1; c < col.length; c++) if (used[c] < used[at]) at = c;
            col[at].getChildren().add(g);
            used[at] += g.getChildren().size() + 1;
        }
        return grid;
    }

    /** A note's first sentence, for a note shown on its own line with the whole of it behind the (i). */
    static String firstWords(String text) {
        int stop = text.indexOf(". ");
        return stop > 0 && stop < 140 ? text.substring(0, stop + 1) : text;
    }

    /* =====================================================================
       THE PIECES THE GOVERNMENT SCREEN ADDED (0.7.31)

       The sixth of the rail's screens redrawn in Build's style needed three
       more, and moved three it had kept to itself. A SPLIT RING, ITS KEY AND
       ITS SLICES: the Government's two rings - the walkthrough's keeper,
       GovernmentScreen's own donut, its key and its slices until now -
       here so Trade's "what we trade" and the bank's loan book can draw
       theirs the same way; an arc and a key row take a click now, and the
       key's shares are of the arcs drawn, so they add to 100% (the spec's
       B14; donutKey's unused GDP went, B13). RANKED BARS: a row a line, each
       a bar at its share of a whole on one scale, a negative line leftward
       from the axis, a line that opens into what it is made of with its
       state kept on the screen, and a door to where the line is decided -
       Revenue's and Spending's lists. A BRIDGE: figures as tiles with the
       named steps between each pair, the card's bars on one scale, a long
       column folded past a few - EARNED -> SURPLUS -> BANKED; a sector's
       cash flow has the same shape. The design study is the project's
       spec-government-0731.md, section 7.
       ===================================================================== */

    /**
     * Slices, biggest first, with everything past the ramp's last folded into
     * one grey "Everything else" (RAMP_REST).
     *
     * FIVE AND A REST, not nine. A ring with nine segments is a ring nobody
     * reads - the eye can hold about five areas at a glance, and past that the
     * small wedges are indistinguishable from each other and from the gaps.
     * The tail is not lost: it is one grey segment here and a full list on the
     * page behind this one. A line at nothing or below has no arc: a ring
     * cannot draw a wedge that subtracts.
     */
    public static List<Slice> topSlices(List<String> names, List<Double> amounts, String[] ramp) {
        List<Integer> order = new java.util.ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            if (amounts.get(i) > 0) order.add(i);
        }
        order.sort((a, b) -> Double.compare(amounts.get(b), amounts.get(a)));

        List<Slice> out = new java.util.ArrayList<>();
        double rest = 0;
        for (int rank = 0; rank < order.size(); rank++) {
            int i = order.get(rank);
            if (rank < ramp.length) {
                out.add(new Slice(names.get(i), amounts.get(i), ramp[rank]));
            } else {
                rest += amounts.get(i);
            }
        }
        if (rest > 0) out.add(new Slice(EVERYTHING_ELSE, rest, Palette.RAMP_REST));
        return out;
    }

    /** What topSlices() calls the slices past its ramp, folded into one. */
    public static final String EVERYTHING_ELSE = "Everything else";

    /**
     * A ring split into its slices, with a caption and a figure in the hole -
     * the Government's two (GovernmentScreen's own donut until 0.7.31).
     *
     * AN ARC PER SLICE, stroked rather than filled, because a stroked open arc
     * IS a ring segment - no path arithmetic, no hole to cut out, and the
     * thickness is one property. Each segment is shortened by a degree and a
     * half so the ring reads as separate pieces rather than one banded circle;
     * that gap is the surface showing through, which is what keeps two adjacent
     * steps of the same hue from merging. Each arc's tooltip is its name, its
     * money and its share of the arcs drawn; a click hands its slice to `pick`
     * (null: no click).
     */
    public static javafx.scene.layout.StackPane splitRing(List<Slice> slices, String top, String figure,
                                                          String tone, double size, Consumer<Slice> pick) {
        javafx.scene.layout.Pane ring = new javafx.scene.layout.Pane();
        ring.setPrefSize(size, size);
        ring.setMinSize(size, size);
        ring.setMaxSize(size, size);

        double total = 0;
        for (Slice s : slices) total += s.amount();

        double radius = (size - Palette.RING) / 2;
        double at = 90;                       // twelve o'clock, going clockwise
        double gap = slices.size() > 1 ? 1.5 : 0;

        for (Slice s : slices) {
            double span = total > 0 ? s.amount() / total * 360 : 0;
            javafx.scene.shape.Arc arc = new javafx.scene.shape.Arc(
                    size / 2, size / 2, radius, radius,
                    at - span + gap / 2, Math.max(0, span - gap));
            arc.setType(javafx.scene.shape.ArcType.OPEN);
            arc.setFill(null);
            arc.setStroke(javafx.scene.paint.Color.web(s.colour()));
            arc.setStrokeWidth(Palette.RING);
            arc.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.BUTT);

            Tooltip tip = new Tooltip(String.format("%s%n%s   %.1f%% of the total",
                    s.name(), money(s.amount()), total > 0 ? s.amount() / total * 100 : 0));
            tip.setShowDelay(Duration.millis(200));
            Tooltip.install(arc, tip);
            if (pick != null) {
                arc.setCursor(javafx.scene.Cursor.HAND);
                arc.setOnMouseClicked(e -> pick.accept(s));
            }
            ring.getChildren().add(arc);
            at -= span;
        }

        Label caption = new Label(top);
        caption.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Label middle = new Label(figure);
        middle.setStyle(Palette.figure(Palette.SIZE_LEAD, tone));
        middle.setMinWidth(Region.USE_PREF_SIZE);
        VBox inside = new VBox(0, caption, middle);
        inside.setAlignment(Pos.CENTER);
        inside.setMouseTransparent(true);

        javafx.scene.layout.StackPane box = new javafx.scene.layout.StackPane(ring, inside);
        box.setPrefSize(size, size);
        box.setMinSize(size, size);
        box.setMaxSize(size, size);
        return box;
    }

    /**
     * The key beside a split ring: a row a slice - its swatch, its name
     * (wrapping, never cut), its share of the arcs drawn and its money - each
     * a click that hands its slice to `pick` (null: none). The shares are of
     * the slices given, the arcs' own whole, so they add to 100% whatever the
     * ring's caption says (0.7.31: they were of the budget's total, which
     * nets a negative line the ring cannot draw, and added to 100.3%).
     *
     * ALWAYS PRESENT, and it carries the figures. A ring can say "this one is
     * about a third"; only the list can say which one and how much, and a
     * player deciding a tax rate needs the second. The swatch carries the
     * identity and the text stays in the ordinary text colours - a legend
     * written in its own series colours is a legend that shouts.
     */
    public static VBox ringKey(List<Slice> slices, double width, Consumer<Slice> pick) {
        double total = 0;
        for (Slice s : slices) total += s.amount();
        VBox key = new VBox(2);
        for (Slice s : slices) {
            Region swatch = new Region();
            swatch.setMinSize(10, 10);
            swatch.setPrefSize(10, 10);
            swatch.setMaxSize(10, 10);
            swatch.setStyle("-fx-background-color: " + s.colour() + "; -fx-background-radius: 2;");

            Label name = new Label(s.name());
            name.setWrapText(true);
            name.setMinWidth(0);
            name.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_BODY));
            HBox.setHgrow(name, Priority.ALWAYS);
            name.setMaxWidth(Double.MAX_VALUE);

            Label share = new Label(String.format("%.1f%%", total > 0 ? s.amount() / total * 100 : 0));
            share.setMinWidth(46);
            share.setPrefWidth(46);
            share.setAlignment(Pos.CENTER_RIGHT);
            share.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT_HEAD));

            Label amount = new Label(money(s.amount()));
            amount.setMinWidth(Region.USE_PREF_SIZE);
            amount.setPrefWidth(64);
            amount.setAlignment(Pos.CENTER_RIGHT);
            amount.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT_MUTED));

            HBox row = new HBox(Palette.GAP_TIGHT + 2, swatch, name, share, amount);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMinHeight(22);
            row.setPrefWidth(width);
            row.setMaxWidth(width);
            String rest = "-fx-padding: 1 4 1 4; -fx-background-radius: 4;";
            row.setStyle(rest);
            if (pick != null) {
                row.setStyle(rest + " -fx-cursor: hand;");
                row.setOnMouseEntered(e -> row.setStyle(rest + " -fx-cursor: hand; -fx-background-color: " + Palette.CONTROL + ";"));
                row.setOnMouseExited(e -> row.setStyle(rest + " -fx-cursor: hand;"));
                row.setOnMouseClicked(e -> pick.accept(s));
            }
            key.getChildren().add(row);
        }
        return key;
    }

    /* ----------------------------- ranked bars ----------------------------- */

    /**
     * One line of rankBars() (0.7.31): `key`, the name its open state is kept
     * under; its icon (null: none) and its name, which wraps and is never
     * cut; its amount, which the bar draws - leftward from the axis when it
     * is below nothing; the bar's colour; the figures at the right, one a
     * column; the bar's tooltip (null: none); what it opens into, built only
     * when it is open, and the word its mark says ("who"), or null for a line
     * that does not open; and a door at the end of the row to where the line
     * is decided (null: none).
     */
    public record RankRow(String key, String icon, String name, double amount, String colour,
                          List<String> figures, String tip, java.util.function.Supplier<javafx.scene.Node> opened,
                          String openWord, String doorWords, Runnable door) { }

    /** How tall a ranked bar's band is, and the least a row takes. */
    static final double RANK_BAND = 12, RANK_ROW = 36;

    /** The property a ranked bar's row carries its line's key under, so a screen can find the row to scroll to. */
    public static final String RANK_KEY = "rankBars.key";

    /**
     * Ranked bars (0.7.31): a row a line - its icon in a square tinted in its
     * colour, its name and, when it opens, "▸ who"; its bar on one scale for
     * every row, `scale` of the bar's units across (widened to the largest
     * line if one is bigger) with the room left of an axis for the largest
     * line below nothing; its figures right-aligned in columns `columns` wide;
     * and its door. A row that opens shows what it is made of under itself
     * while its key is in `open`, a set the drawing screen keeps, so a month
     * landing leaves it as the player left it; a click on the row toggles it
     * and runs `redraw`. The rows are laid in the order given: the caller
     * ranks them.
     */
    public static VBox rankBars(List<RankRow> rows, double scale, double[] columns, double doorWidth,
                                java.util.Set<String> open, Runnable redraw) {
        double pos = Math.max(0, scale), neg = 0;
        for (RankRow r : rows) {
            if (r.amount() > pos) pos = r.amount();
            if (-r.amount() > neg) neg = -r.amount();
        }
        VBox list = new VBox(0);
        for (RankRow r : rows) {
            boolean opens = r.opened() != null;
            boolean shown = opens && open.contains(r.key());

            HBox lead = new HBox(8);
            lead.setAlignment(Pos.CENTER_LEFT);
            lead.setMinWidth(200);
            lead.setPrefWidth(236);
            lead.setMaxWidth(236);
            if (r.icon() != null) lead.getChildren().add(iconSquare(r.icon(), r.colour(), 24, 13));
            Label name = new Label(r.name());
            name.setWrapText(true);
            name.setMinWidth(0);
            name.setStyle(Palette.words(Palette.SIZE_BODY + 1, r.amount() == 0 ? Palette.TEXT_MUTED : Palette.TEXT_BODY));
            VBox named = new VBox(0, name);
            if (opens) {
                Label mark = new Label((shown ? "▾ " : "▸ ") + r.openWord());
                mark.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.ACCENT));
                named.getChildren().add(mark);
            }
            HBox.setHgrow(named, Priority.ALWAYS);
            lead.getChildren().add(named);

            RankBar bar = new RankBar(r.amount(), pos, neg, r.colour());
            HBox.setHgrow(bar, Priority.ALWAYS);
            if (r.tip() != null) {
                Tooltip tip = new Tooltip(r.tip());
                tip.setShowDelay(Duration.millis(200));
                Tooltip.install(bar, tip);
            }

            HBox row = new HBox(10, lead, bar);
            for (int c = 0; c < r.figures().size(); c++) {
                Label f = new Label(r.figures().get(c));
                double w = c < columns.length ? columns[c] : 70;
                f.setMinWidth(Region.USE_PREF_SIZE);
                f.setPrefWidth(w);
                f.setAlignment(Pos.CENTER_RIGHT);
                f.setStyle(Palette.figure(c == 0 ? Palette.SIZE_BODY + 1 : Palette.SIZE_LABEL,
                        c == 0 ? Palette.TEXT_HEAD : Palette.TEXT_MUTED));
                row.getChildren().add(f);
            }
            if (doorWidth > 0) {
                javafx.scene.Node d = r.door() == null ? new Region() : door(r.doorWords(), Palette.ACCENT, r.door());
                HBox slot = new HBox(d);
                slot.setAlignment(Pos.CENTER_RIGHT);
                slot.setMinWidth(doorWidth);
                slot.setPrefWidth(doorWidth);
                row.getChildren().add(slot);
            }
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMinHeight(RANK_ROW);
            row.getProperties().put(RANK_KEY, r.key());
            String rest = "-fx-padding: 4 6 4 6; -fx-background-radius: 6;"
                    + " -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0;";
            row.setStyle(rest);
            if (opens) {
                row.setStyle(rest + " -fx-cursor: hand;");
                row.setOnMouseEntered(e -> row.setStyle(rest + " -fx-cursor: hand; -fx-background-color: " + Palette.CONTROL + ";"));
                row.setOnMouseExited(e -> row.setStyle(rest + " -fx-cursor: hand;"));
                row.setOnMouseClicked(e -> {
                    if (!open.remove(r.key())) open.add(r.key());
                    redraw.run();
                });
            }
            list.getChildren().add(row);
            if (shown) {
                javafx.scene.Node inside = r.opened().get();
                if (inside != null) {
                    VBox under = new VBox(inside);
                    under.setStyle("-fx-padding: 6 0 10 46;");
                    list.getChildren().add(under);
                }
            }
        }
        return list;
    }

    /** The bar rankBars() draws for one row: a Pane that lays its track, its fill and its axis out at whatever width it is given. */
    static final class RankBar extends javafx.scene.layout.Pane {
        private final double amount, pos, neg;
        private final Region track = new Region(), fill = new Region(), axis = new Region();

        RankBar(double amount, double pos, double neg, String colour) {
            this.amount = amount;
            this.pos = pos;
            this.neg = neg;
            track.setStyle("-fx-background-color: " + Palette.EDGE + "; -fx-background-radius: " + RANK_BAND / 2 + ";");
            fill.setStyle("-fx-background-color: " + (amount < 0 ? Palette.TEXT_SPENT : colour)
                    + "; -fx-background-radius: " + RANK_BAND / 2 + ";");
            axis.setStyle("-fx-background-color: " + Palette.TEXT_MUTED + ";");
            getChildren().addAll(track, fill);
            if (neg > 0) getChildren().add(axis);
            setMinSize(120, RANK_BAND + 4);
            setPrefSize(520, RANK_BAND + 4);
            setMaxHeight(RANK_BAND + 4);
        }

        @Override protected void layoutChildren() {
            double w = getWidth(), h = getHeight();
            double y = (h - RANK_BAND) / 2;
            double unit = pos + neg > 0 ? w / (pos + neg) : 0;
            double zero = neg * unit;
            track.resizeRelocate(0, y, w, RANK_BAND);
            double len = Math.abs(amount) * unit;
            if (Math.abs(amount) > 0 && len < 2) len = 2;
            fill.setVisible(len > 0);
            fill.resizeRelocate(amount < 0 ? zero - len : zero, y, len, RANK_BAND);
            axis.resizeRelocate(zero, y - 2, 1, RANK_BAND + 4);
        }
    }

    /* -------------------------------- a bridge -------------------------------- */

    /**
     * One figure a bridge walks from or to (0.7.31): its caption and its
     * icon, the figure in large type, one line under it, any chips, and what
     * a click on it does (null: nothing).
     */
    public record Anchor(String caption, String icon, String figure, String line, List<javafx.scene.Node> chips,
                         Runnable go) { }

    /**
     * One step between two anchors (0.7.31): its words, its amount as it
     * moves the figure (+ adds, - takes away), its icon (null: none), its
     * tooltip (null: none), what a click on it opens (null: nothing), and
     * whether it is shown at nothing too (always()) - "not accounted for",
     * which says so when it is nothing.
     */
    public record BridgeStep(String label, double amount, String icon, String tip, Runnable go, boolean atNothing) {
        public static BridgeStep of(String label, double amount, String icon) {
            return new BridgeStep(label, amount, icon, null, null, false);
        }
        public BridgeStep tip(String t)    { return new BridgeStep(label, amount, icon, t, go, atNothing); }
        public BridgeStep go(Runnable g)   { return new BridgeStep(label, amount, icon, tip, g, atNothing); }
        public BridgeStep always()         { return new BridgeStep(label, amount, icon, tip, go, true); }
    }

    /** A bridge's tiles' width, its step bars' and its figures' (0.7.31). */
    static final double BRIDGE_TILE = 200, BRIDGE_BAR = 56, BRIDGE_FIGURE = 64;

    /** A step under this, in the model's thousands, is nothing: half a thousand, below which signedTight() writes "$0". */
    static final double BRIDGE_NOTHING = .5;

    /**
     * A bridge (0.7.31): the anchors as tiles with a column of steps between
     * each pair and a muted "›" between each - steps.get(i) between anchor i
     * and anchor i + 1. A step row is its sign, its icon, its words
     * (wrapping), a bar on one scale for the whole card (its largest step) in
     * `in` when it adds and `out` when it takes away, and its figure written
     * by `figure`. Each column is sorted by size with the steps at nothing
     * left out (but an always() step); past `foldPast` rows it shows one
     * fewer and "N more ▸", which opens the rest in place while `key` and the
     * column's index are in `open`, kept by the drawing screen. A column
     * with nothing in it says so.
     */
    public static HBox bridge(List<Anchor> anchors, List<List<BridgeStep>> steps,
                              java.util.function.DoubleFunction<String> figure, String in, String out,
                              int foldPast, java.util.Set<String> open, String key, Runnable redraw) {
        double scale = 0;
        for (List<BridgeStep> col : steps) for (BridgeStep s : col) scale = Math.max(scale, Math.abs(s.amount()));

        HBox card = new HBox(6);
        card.setAlignment(Pos.TOP_LEFT);
        for (int i = 0; i < anchors.size(); i++) {
            if (i > 0) {
                card.getChildren().add(bridgeChevron());
                List<BridgeStep> col = i - 1 < steps.size() ? steps.get(i - 1) : List.of();
                VBox column = bridgeColumn(col, scale, figure, in, out, foldPast, open, key + ":" + (i - 1), redraw);
                HBox.setHgrow(column, Priority.ALWAYS);
                card.getChildren().add(column);
                card.getChildren().add(bridgeChevron());
            }
            card.getChildren().add(bridgeTile(anchors.get(i)));
        }
        return card;
    }

    /** The muted "›" between a tile and a column. */
    static Label bridgeChevron() {
        Label l = new Label("›");
        l.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_SPENT) + " -fx-padding: 18 0 0 0;");
        l.setMinWidth(Region.USE_PREF_SIZE);
        return l;
    }

    /** One anchor as a tile: caption and icon, the figure, its line, its chips. */
    static VBox bridgeTile(Anchor a) {
        HBox head = new HBox(6);
        head.setAlignment(Pos.CENTER_LEFT);
        if (a.icon() != null) head.getChildren().add(icon(a.icon(), Palette.TEXT_LABEL, 13));
        Label caption = new Label(a.caption());
        caption.setWrapText(true);
        caption.setStyle(Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        head.getChildren().add(caption);
        Label fig = new Label(a.figure());
        fig.setMinWidth(Region.USE_PREF_SIZE);
        fig.setStyle(Fonts.monoSemiBold() + " -fx-font-size: 22px; -fx-text-fill: " + Palette.TEXT_HEAD + ";");
        VBox tile = new VBox(2, head, fig);
        if (a.line() != null) {
            Label line = new Label(a.line());
            line.setWrapText(true);
            line.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            tile.getChildren().add(line);
        }
        if (a.chips() != null) {
            for (javafx.scene.Node c : a.chips()) {
                if (c == null) continue;
                VBox.setMargin(c, new javafx.geometry.Insets(4, 0, 0, 0));
                tile.getChildren().add(c);
            }
        }
        tile.setMinWidth(170);
        tile.setPrefWidth(BRIDGE_TILE);
        tile.setMaxWidth(BRIDGE_TILE);
        String rest = "-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.PANEL + "; -fx-background-radius: 8;"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-border-radius: 8;";
        tile.setStyle(rest);
        if (a.go() != null) {
            tile.setStyle(rest + " -fx-cursor: hand;");
            tile.setOnMouseClicked(e -> a.go().run());
        }
        return tile;
    }

    /** One column of a bridge's steps. */
    static VBox bridgeColumn(List<BridgeStep> col, double scale, java.util.function.DoubleFunction<String> figure,
                             String in, String out, int foldPast, java.util.Set<String> open, String key,
                             Runnable redraw) {
        List<BridgeStep> shown = new java.util.ArrayList<>();
        List<BridgeStep> kept = new java.util.ArrayList<>();
        for (BridgeStep s : col) {
            if (Math.abs(s.amount()) >= BRIDGE_NOTHING) shown.add(s);
            else if (s.atNothing()) kept.add(s);
        }
        shown.sort((a, b) -> Double.compare(Math.abs(b.amount()), Math.abs(a.amount())));
        shown.addAll(kept);

        VBox column = new VBox(2);
        column.setMinWidth(220);
        column.setStyle("-fx-padding: 6 0 0 0;");
        if (shown.isEmpty()) {
            Label none = new Label("nothing between them");
            none.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_SPENT));
            column.getChildren().add(none);
            return column;
        }
        boolean all = open.contains(key) || shown.size() <= foldPast;
        int count = all ? shown.size() : Math.max(1, foldPast - 1);
        for (int i = 0; i < count; i++) column.getChildren().add(bridgeRow(shown.get(i), scale, figure, in, out));
        if (shown.size() > foldPast) {
            Label more = new Label(all ? "fewer ▴" : (shown.size() - count) + " more ▸");
            more.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.ACCENT) + " -fx-cursor: hand;");
            more.setOnMouseClicked(e -> {
                if (!open.remove(key)) open.add(key);
                redraw.run();
            });
            column.getChildren().add(more);
        }
        return column;
    }

    /** One step: sign, icon, words, bar, figure. */
    static HBox bridgeRow(BridgeStep s, double scale, java.util.function.DoubleFunction<String> figure,
                          String in, String out) {
        boolean adds = s.amount() >= 0;
        Label sign = new Label(Math.abs(s.amount()) < BRIDGE_NOTHING ? "" : adds ? "+" : "−");
        sign.setMinWidth(10);
        sign.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        HBox row = new HBox(5, sign);
        if (s.icon() != null) row.getChildren().add(icon(s.icon(), Palette.TEXT_LABEL, 12));
        Label words = new Label(s.label());
        words.setWrapText(true);
        words.setMinWidth(0);
        words.setStyle(Palette.words(Palette.SIZE_LABEL, s.go() == null ? Palette.TEXT_BODY : Palette.ACCENT));
        HBox.setHgrow(words, Priority.ALWAYS);
        words.setMaxWidth(Double.MAX_VALUE);
        row.getChildren().add(words);
        row.getChildren().add(bar(scale > 0 ? Math.abs(s.amount()) / scale : 0, adds ? in : out, BRIDGE_BAR, 6));
        Label fig = new Label(figure.apply(s.amount()));
        fig.setMinWidth(Region.USE_PREF_SIZE);
        fig.setPrefWidth(BRIDGE_FIGURE);
        fig.setAlignment(Pos.CENTER_RIGHT);
        fig.setStyle(Palette.figure(Palette.SIZE_LABEL, Math.abs(s.amount()) < BRIDGE_NOTHING ? Palette.TEXT_SPENT : Palette.TEXT_HEAD));
        row.getChildren().add(fig);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(22);
        if (s.tip() != null) {
            Tooltip tip = new Tooltip(s.tip());
            tip.setShowDelay(Duration.millis(200));
            tip.setWrapText(true);
            tip.setMaxWidth(POPOVER_WIDTH);
            Tooltip.install(row, tip);
        }
        if (s.go() != null) {
            String rest = "-fx-background-radius: 4; -fx-cursor: hand;";
            row.setStyle(rest);
            row.setOnMouseEntered(e -> row.setStyle(rest + " -fx-background-color: " + Palette.CONTROL + ";"));
            row.setOnMouseExited(e -> row.setStyle(rest));
            row.setOnMouseClicked(e -> s.go().run());
        }
        return row;
    }

    /* =====================================================================
       THE PIECES THE FINANCES SCREEN ADDED (0.7.32)

       The seventh of the rail's screens redrawn in Build's style needed two
       more. COLUMNS: a bar chart standing up - a column each, its segments
       (the 0.7.26 Segment's colour, ghost and stripe; the tooltip and the
       click are the column's) stacked from the foot, its figure over it
       when the caller gives one, a tag over that, its label and a second
       line under it - the maturity ladder, a column a calendar year
       stacked by instrument, on the Finances hub, its Borrow page and its
       receipt, and the term chips drawn as columns of their rate. And A
       SETTING: a choice of two or three as chips, with the chosen one's
       line under them - the rollover's three, the bank's rescue's two,
       Borrow's dollars' two. The design study is the project's
       spec-finances-0732.md, section 6.
       ===================================================================== */

    /**
     * One column of columns() (0.7.32): its label and a second line under it
     * (either may be null); its figure, written over its top (null: none -
     * the caller says only what is worth saying); its segments, stacked from
     * the foot in the order given; whether the whole column is a GHOST
     * (outlined and tinted, what stands apart from the rest - the ladder's
     * "later"); a tag over its figure in a colour (null: none); its tooltip;
     * what a click on it does (null: nothing); and since 0.7.34 whether it
     * is BROKEN - drawn to the top of the plot whatever its total, its
     * segments in proportion, with a two-slash break across it: the ladder's
     * "later" when it would flatten the years.
     */
    public record Column(String label, String sub, String figure, List<Segment> parts, boolean ghost,
                         String tag, String tagColour, String tip, Runnable go, boolean broken) {

        /** ...whole, as every column was until 0.7.34. */
        public Column(String label, String sub, String figure, List<Segment> parts, boolean ghost,
                      String tag, String tagColour, String tip, Runnable go) {
            this(label, sub, figure, parts, ghost, tag, tagColour, tip, go, false);
        }
    }

    /**
     * Columns (0.7.32): each column's segments stacked from the foot on one
     * scale - `scale` of its units to the plot's height, the largest
     * column's total when it is 0 or less - the columns side by side across
     * `width` (0 or less: what holds it) with a gap between, `height` tall
     * with its figures' and labels' room. A segment's stripe runs down its
     * left edge; a ghost segment, or every segment of a ghost column, is
     * outlined and tinted; a column taller than the scale is drawn to the top,
     * and a broken one (0.7.34) fills the plot in proportion with a break
     * across it.
     */
    public static Columns columns(List<Column> cols, double scale, double width, double height) {
        return new Columns(cols, scale, width, height);
    }

    /** The chart columns() draws: a Pane that lays its columns out at whatever width it is given. */
    public static final class Columns extends javafx.scene.layout.Pane {
        /** The room over the plot for a tag and a figure, under it for a label and its second line, and the gap between columns. */
        static final double TOP = 34, FOOT = 30, GAP = 8;
        private final List<Column> cols;
        private final double scale;
        private final List<List<Region>> drawn = new java.util.ArrayList<>(), stripes = new java.util.ArrayList<>();
        private final List<Label> figures = new java.util.ArrayList<>(), labels = new java.util.ArrayList<>(),
                subs = new java.util.ArrayList<>();
        private final List<javafx.scene.Node> tags = new java.util.ArrayList<>();
        private final List<Region> hits = new java.util.ArrayList<>();
        /** A broken column's break (0.7.34): the gap, in the card's ground, and its two slashes; null for a whole column. */
        private final List<javafx.scene.shape.Shape[]> breaks = new java.util.ArrayList<>();

        Columns(List<Column> cols, double scale, double width, double height) {
            this.cols = cols == null ? List.of() : cols;
            double most = 0;
            for (Column c : this.cols) {
                double t = 0;
                for (Segment s : c.parts()) t += Math.max(0, s.amount());
                most = Math.max(most, t);
            }
            this.scale = scale > 0 ? scale : most;
            for (Column c : this.cols) {
                List<Region> rs = new java.util.ArrayList<>(), st = new java.util.ArrayList<>();
                for (Segment s : c.parts()) {
                    boolean ghost = c.ghost() || s.ghost();
                    Region r = new Region();
                    r.setStyle(ghost
                            ? "-fx-background-color: " + s.colour() + "33; -fx-border-color: " + s.colour() + ";"
                              + " -fx-border-width: 1; -fx-background-radius: 2; -fx-border-radius: 2;"
                            : "-fx-background-color: " + s.colour() + "; -fx-background-radius: 2;");
                    r.setMouseTransparent(true);
                    rs.add(r);
                    getChildren().add(r);
                    Region stripe = new Region();
                    stripe.setMouseTransparent(true);
                    stripe.setVisible(s.stripe() != null);
                    if (s.stripe() != null) stripe.setStyle("-fx-background-color: " + s.stripe() + ";");
                    st.add(stripe);
                    getChildren().add(stripe);
                }
                drawn.add(rs);
                stripes.add(st);
                if (c.broken()) {
                    javafx.scene.shape.Polygon gap = new javafx.scene.shape.Polygon();
                    gap.setFill(javafx.scene.paint.Color.web(Palette.RAISED));
                    javafx.scene.shape.Line upper = new javafx.scene.shape.Line(), lower = new javafx.scene.shape.Line();
                    for (javafx.scene.shape.Line slash : new javafx.scene.shape.Line[] { upper, lower }) {
                        slash.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_MUTED));
                        slash.setStrokeWidth(1.5);
                    }
                    javafx.scene.shape.Shape[] b = { gap, upper, lower };
                    for (javafx.scene.shape.Shape s : b) {
                        s.setMouseTransparent(true);
                        s.setManaged(false);
                        getChildren().add(s);
                    }
                    breaks.add(b);
                } else {
                    breaks.add(null);
                }
                Label f = new Label(c.figure() == null ? "" : c.figure());
                f.setMouseTransparent(true);
                f.setStyle(Palette.figure(Palette.SIZE_CAPTION, c.ghost() ? Palette.TEXT_MUTED : Palette.TEXT_LABEL));
                f.setVisible(c.figure() != null);
                figures.add(f);
                getChildren().add(f);
                javafx.scene.Node tg = c.tag() == null ? new Region() : tag(c.tag(), c.tagColour() == null
                        ? Palette.TEXT_LABEL : c.tagColour());
                tg.setMouseTransparent(true);
                tg.setVisible(c.tag() != null);
                tags.add(tg);
                getChildren().add(tg);
                Label l = new Label(c.label() == null ? "" : c.label());
                l.setMouseTransparent(true);
                l.setStyle(Palette.words(Palette.SIZE_CAPTION + 1, c.ghost() ? Palette.TEXT_MUTED : Palette.TEXT_LABEL));
                labels.add(l);
                getChildren().add(l);
                Label sb = new Label(c.sub() == null ? "" : c.sub());
                sb.setMouseTransparent(true);
                sb.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
                subs.add(sb);
                getChildren().add(sb);
                Region hit = new Region();
                String rest = "-fx-background-color: transparent; -fx-background-radius: 4;";
                hit.setStyle(rest);
                if (c.tip() != null) {
                    Tooltip tip = new Tooltip(c.tip());
                    tip.setShowDelay(Duration.millis(200));
                    tip.setWrapText(true);
                    tip.setMaxWidth(POPOVER_WIDTH);
                    Tooltip.install(hit, tip);
                }
                if (c.go() != null) {
                    hit.setCursor(javafx.scene.Cursor.HAND);
                    hit.setOnMouseClicked(e -> c.go().run());
                }
                hit.setOnMouseEntered(e -> hit.setStyle("-fx-background-color: " + tint(Palette.ACCENT, .07)
                        + "; -fx-background-radius: 4;"));
                hit.setOnMouseExited(e -> hit.setStyle(rest));
                hits.add(hit);
                getChildren().add(0, hit);
            }
            setMinWidth(width > 0 ? width : 0);
            setPrefWidth(width > 0 ? width : 400);
            setMaxWidth(width > 0 ? width : Double.MAX_VALUE);
            setMinHeight(height);
            setPrefHeight(height);
            setMaxHeight(height);
        }

        @Override protected void layoutChildren() {
            int n = cols.size();
            if (n == 0) return;
            double w = getWidth(), h = getHeight();
            double slot = Math.max(4, (w - GAP * (n - 1)) / n);
            double barW = Math.max(4, Math.min(slot, slot - 6));
            double plot = Math.max(10, h - TOP - FOOT);
            double base = TOP + plot;
            for (int i = 0; i < n; i++) {
                Column c = cols.get(i);
                double x = i * (slot + GAP);
                double bx = x + (slot - barW) / 2;
                hits.get(i).resizeRelocate(x, 0, slot, h);
                double y = base;
                List<Region> rs = drawn.get(i), st = stripes.get(i);
                // A broken column is drawn on its own total, so it fills the plot.
                double own = scale;
                if (c.broken()) {
                    own = 0;
                    for (Segment s : c.parts()) own += Math.max(0, s.amount());
                }
                for (int k = 0; k < rs.size(); k++) {
                    double amount = Math.max(0, c.parts().get(k).amount());
                    double tall = own > 0 ? amount / own * plot : 0;
                    if (y - tall < TOP) tall = Math.max(0, y - TOP);
                    boolean on = amount > 0 && tall > 0;
                    double shown = on ? Math.max(2, tall) : 0;
                    Region r = rs.get(k);
                    r.setVisible(on);
                    r.resizeRelocate(bx, y - shown, barW, shown);
                    Region stripe = st.get(k);
                    stripe.setVisible(on && c.parts().get(k).stripe() != null);
                    stripe.resizeRelocate(bx, y - shown, Math.min(3, barW), shown);
                    if (on) y -= shown + (shown > 4 ? 1 : 0);
                }
                javafx.scene.shape.Shape[] b = breaks.get(i);
                if (b != null) {
                    // Two slashes a third of the way down what is drawn, rising to the right, the
                    // card's ground between them: the column goes on past where it is cut.
                    boolean on = base - y > 16;
                    double yb = y + (base - y) / 3, x0 = bx - 3, x1 = bx + barW + 3;
                    ((javafx.scene.shape.Polygon) b[0]).getPoints().setAll(x0, yb + 3, x1, yb - 3, x1, yb + 2, x0, yb + 8);
                    javafx.scene.shape.Line upper = (javafx.scene.shape.Line) b[1], lower = (javafx.scene.shape.Line) b[2];
                    upper.setStartX(x0); upper.setStartY(yb + 3); upper.setEndX(x1); upper.setEndY(yb - 3);
                    lower.setStartX(x0); lower.setStartY(yb + 8); lower.setEndX(x1); lower.setEndY(yb + 2);
                    for (javafx.scene.shape.Shape s : b) s.setVisible(on);
                }
                Label f = figures.get(i);
                double fw = f.prefWidth(-1), fh = f.prefHeight(-1);
                double fy = Math.max(TOP - fh, y - fh - 1);
                f.resizeRelocate(x + (slot - fw) / 2, fy, fw, fh);
                javafx.scene.Node tg = tags.get(i);
                double tw = tg.prefWidth(-1), th = tg.prefHeight(-1);
                tg.resizeRelocate(x + (slot - tw) / 2, Math.max(0, fy - th - 1), tw, th);
                Label l = labels.get(i);
                double lw = l.prefWidth(-1), lh = l.prefHeight(-1);
                l.resizeRelocate(x + (slot - lw) / 2, base + 3, lw, lh);
                Label sb = subs.get(i);
                double sw = sb.prefWidth(-1), sh = sb.prefHeight(-1);
                sb.resizeRelocate(x + (slot - sw) / 2, base + 3 + lh, sw, sh);
            }
        }
    }

    /**
     * A setting (0.7.32): its title in capitals with an (i) holding `info`
     * (either may be null), its choices as chips with `tips` as their
     * tooltips (null: none), and under them the chosen one's line from
     * `lines` - what the setting does as it stands, in a sentence. A pick
     * runs `pick` with the choice's name; the caller sets it and redraws.
     */
    public static VBox setting(String title, String info, String[] names, String current, String[] lines,
                               String[] tips, Consumer<String> pick) {
        VBox box = new VBox(6);
        if (title != null) {
            Label t = new Label(title);
            t.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
            t.setMinWidth(Region.USE_PREF_SIZE);
            HBox head = new HBox(Palette.GAP, t);
            if (info != null) head.getChildren().add(infoButton(info, true));
            head.setAlignment(Pos.CENTER_LEFT);
            box.getChildren().add(head);
        }
        javafx.scene.layout.FlowPane chips = chipStrip(names, current, Palette.SIZE_LABEL, pick);
        chips.setAlignment(Pos.CENTER_LEFT);
        chips.setPrefWrapLength(420);
        for (int i = 0; tips != null && i < tips.length && i < chips.getChildren().size(); i++) {
            if (tips[i] != null && chips.getChildren().get(i) instanceof Button b) {
                Tooltip tip = new Tooltip(tips[i]);
                tip.setShowDelay(Duration.millis(250));
                tip.setWrapText(true);
                tip.setMaxWidth(POPOVER_WIDTH);
                b.setTooltip(tip);
            }
        }
        box.getChildren().add(chips);
        for (int i = 0; i < names.length && lines != null && i < lines.length; i++) {
            if (!names[i].equals(current) || lines[i] == null) continue;
            Label line = new Label(lines[i]);
            line.setWrapText(true);
            line.setMinWidth(0);
            line.setMaxWidth(Double.MAX_VALUE);
            line.setStyle(Palette.words(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
            box.getChildren().add(line);
        }
        return box;
    }

    /* =====================================================================
       THE PIECES THE BANK SCREEN ADDED (0.7.33)

       The eighth of the rail's screens redrawn in Build's style (the
       project's spec-bank-0733.md, section 6) needed four: A RATIO IN ITS
       BAND, the bank's capital against the minimum, its own target and the
       top of its band, on a scale past the top so the band sits in the left
       of it (the Bank tab's own capital band until 0.7.33, generalised - the
       Sectors' leverage scale, Trade's currency band and Policy's inflation
       target are its next readers); A LADDER OF RATES DRAWN IN THEIR PARTS,
       a row a rung - its icon, its name, the parts its rate is built from
       on the ladder's one scale, the rate, its step from the rung it is
       built on, and an (i) holding why; TWO BARS ON ONE SCALE, what
       something owns against what it owes and what is its owners', each
       with its words at the left; and A STATUS BANNER, a state's word and
       the model's sentence for it.
       ===================================================================== */

    /** How far past the top of its band a ratio's bar runs, as a multiple of the top: the band sits in the left of it, so a ratio well over its band reads as full (BankScreen's capital band's since 0.7.9). */
    public static final double BAND_SCALE = 1.6;

    /**
     * A ratio in its band (0.7.33): a bar of `value` in `tone` on a scale of
     * BAND_SCALE times the top, pinned at the end past it, with ticks at the
     * minimum and the top in the muted grey and at the target in the heading
     * ink - each named by its figure under the bar when `named` (the caller
     * says which is which in a line of its own, so three names never run
     * into each other where the band is narrow). `tip`, when not null, is
     * the whole bar's tooltip. `value` NaN draws no fill.
     */
    public static SegmentBar bandBar(double value, double min, double target, double top, String tone,
                                     double width, double band, boolean named, String tip) {
        double scale = Math.max(1e-9, top * BAND_SCALE);
        List<Segment> fill = new java.util.ArrayList<>();
        if (Double.isFinite(value) && value > 0) fill.add(new Segment(Math.min(value, scale), tone, false, null, null, null, null));
        List<Tick> ticks = List.of(
                new Tick(min, Palette.TEXT_MUTED, 2, named ? share1(min) : null, null),
                new Tick(target, Palette.TEXT_HEAD, 2, named ? share1(target) : null, null),
                new Tick(top, Palette.TEXT_MUTED, 2, named ? share1(top) : null, null));
        SegmentBar bar = segmentBar(fill, scale, ticks, width, band);
        if (tip != null) {
            Tooltip t = new Tooltip(tip);
            t.setShowDelay(Duration.millis(250));
            t.setWrapText(true);
            t.setMaxWidth(POPOVER_WIDTH);
            Tooltip.install(bar, t);
        }
        return bar;
    }

    /* ----------------------------- a ladder of rates ----------------------------- */

    /**
     * One rung of rateLadder() (0.7.33): its icon and the icon's colour (null:
     * none); its name, which wraps and is never cut, and the name's ink
     * (null: the body's; a rung that is shut out reads red); its rate's parts
     * as segments on the ladder's one scale - a ghost for what it is built on
     * or a quote nobody pays - and ticks across its bar alone (prime, a bond's
     * coupon); the rate; its step from the rung it is built on, as a chip;
     * the (i)'s text; where a click on its name goes; a tag after the name
     * ("shut 4 mo") in a colour; whether it sits under the rung before it
     * (a sector's bond under its loan); and CAPTION, a sub-heading row with
     * no bar, its name the heading.
     */
    public record Rung(String svg, String colour, String name, String nameTone, List<Segment> parts,
                       List<Tick> ticks, double rate, String step, String info, Runnable go,
                       String tag, String tagColour, boolean indent, boolean caption) {
        public static Rung of(String svg, String colour, String name, List<Segment> parts, double rate, String step) {
            return new Rung(svg, colour, name, null, parts, List.of(), rate, step, null, null, null, null, false, false);
        }
        public static Rung caption(String heading) {
            return new Rung(null, null, heading, null, List.of(), List.of(), Double.NaN, null, null, null, null, null,
                    false, true);
        }
        public Rung ticks(List<Tick> t) { return new Rung(svg, colour, name, nameTone, parts, t, rate, step, info, go, tag, tagColour, indent, caption); }
        public Rung info(String i)      { return new Rung(svg, colour, name, nameTone, parts, ticks, rate, step, i, go, tag, tagColour, indent, caption); }
        public Rung go(Runnable g)      { return new Rung(svg, colour, name, nameTone, parts, ticks, rate, step, info, g, tag, tagColour, indent, caption); }
        public Rung tone(String c)      { return new Rung(svg, colour, name, c, parts, ticks, rate, step, info, go, tag, tagColour, indent, caption); }
        public Rung tag(String t, String c) { return new Rung(svg, colour, name, nameTone, parts, ticks, rate, step, info, go, t, c, indent, caption); }
        /** ...set under the rung before it. */
        public Rung under()             { return new Rung(svg, colour, name, nameTone, parts, ticks, rate, step, info, go, tag, tagColour, true, caption); }
    }

    /** A rung's least height, and its bar's band. */
    public static final double RUNG_ROW = 30, RUNG_BAND = 12;

    /**
     * A ladder of rates drawn in their parts (0.7.33): a row a rung - its
     * icon, its name (in the accent with "›" when it is a door: a click
     * opens where the rate is decided; in its own tone when it has one, a
     * door or not), its parts on one scale for every row
     * (`scale` of the rates, the dearest on the ladder), the rate in a column
     * `rateWidth` wide headed "a year", its step as a chip in a column
     * `stepWidth` wide, and its (i). Caption rungs are sub-headings across
     * the rows. The names take `nameWidth`; the bars take what is left.
     */
    public static VBox rateLadder(List<Rung> rungs, double scale, double nameWidth, double rateWidth,
                                  double stepWidth) {
        VBox ladder = new VBox(2);
        Label head = new Label("a year");
        head.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        head.setMinWidth(Region.USE_PREF_SIZE);
        HBox headRow = new HBox(head);
        headRow.setAlignment(Pos.CENTER_RIGHT);
        headRow.setPadding(new javafx.geometry.Insets(0, stepWidth + 24 + 2 * Palette.GAP, 0, 0));
        ladder.getChildren().add(headRow);
        for (Rung r : rungs) {
            if (r.caption()) {
                Label c = new Label(r.name());
                c.setWrapText(true);
                c.setStyle(Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL) + " -fx-padding: 8 0 2 0;");
                ladder.getChildren().add(c);
                continue;
            }
            HBox row = new HBox(Palette.GAP);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMinHeight(RUNG_ROW);
            double indent = r.indent() ? 18 : 0;
            Region mark = r.svg() == null ? new Region() : icon(r.svg(), r.colour() == null ? Palette.TEXT_LABEL : r.colour(), 14);
            javafx.scene.layout.StackPane box = new javafx.scene.layout.StackPane(mark);
            box.setMinSize(20, 20);
            box.setPrefSize(20, 20);
            box.setMaxSize(20, 20);
            HBox.setMargin(box, new javafx.geometry.Insets(0, 0, 0, indent));
            Label name = new Label(r.go() == null ? r.name() : r.name() + " ›");
            name.setWrapText(true);
            name.setMinWidth(0);
            // A rung's own tone over the door's accent (0.7.38): a shut-out sector stays a door, in red.
            String ink = r.nameTone() != null ? r.nameTone() : r.go() != null ? Palette.ACCENT : Palette.TEXT_BODY;
            String rest = Palette.words(Palette.SIZE_BODY, ink) + (r.go() != null ? " -fx-cursor: hand;" : "");
            name.setStyle(rest);
            if (r.go() != null) {
                name.setOnMouseEntered(e -> name.setStyle(rest + " -fx-underline: true;"));
                name.setOnMouseExited(e -> name.setStyle(rest));
                name.setOnMouseClicked(e -> r.go().run());
            }
            HBox named = new HBox(6, name);
            named.setAlignment(Pos.CENTER_LEFT);
            if (r.tag() != null) named.getChildren().add(chip(r.tag(), r.tagColour() == null ? Palette.TEXT_MUTED : r.tagColour()));
            double nw = Math.max(80, nameWidth - indent);
            named.setMinWidth(nw);
            named.setPrefWidth(nw);
            named.setMaxWidth(nw);
            SegmentBar bar = segmentBar(r.parts(), scale, r.ticks(), 0, RUNG_BAND);
            HBox.setHgrow(bar, Priority.ALWAYS);
            Label rate = new Label(Double.isFinite(r.rate()) ? ratePct(r.rate()) : "\u2014");
            rate.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD));
            rate.setAlignment(Pos.CENTER_RIGHT);
            rate.setMinWidth(rateWidth);
            rate.setPrefWidth(rateWidth);
            HBox stepBox = new HBox();
            stepBox.setAlignment(Pos.CENTER_LEFT);
            stepBox.setMinWidth(stepWidth);
            stepBox.setPrefWidth(stepWidth);
            stepBox.setMaxWidth(stepWidth);
            if (r.step() != null) {
                Label step = chip(r.step(), Palette.TEXT_MUTED);
                step.setMinWidth(0);
                step.setWrapText(true);
                stepBox.getChildren().add(step);
            }
            javafx.scene.layout.StackPane dot = new javafx.scene.layout.StackPane();
            dot.setMinWidth(24);
            dot.setPrefWidth(24);
            if (r.info() != null) dot.getChildren().add(infoButton(r.info(), true));
            row.getChildren().addAll(box, named, bar, rate, stepBox, dot);
            ladder.getChildren().add(row);
        }
        return ladder;
    }

    /* ----------------------------- two bars on one scale ----------------------------- */

    /**
     * Two bars on one scale (0.7.33): `top` and `under`, each a segment bar
     * on `scale` with its own ticks, and each with its words in a column
     * `labelWidth` wide at its left, which wrap and are never cut - the
     * Bank's balance sheet, what it owns against what it owes and what is
     * its owners'. `band` is each bar's height.
     */
    public static VBox balanceBars(String topWords, List<Segment> top, List<Tick> topTicks,
                                   String underWords, List<Segment> under, List<Tick> underTicks,
                                   double scale, double labelWidth, double band) {
        VBox both = new VBox(10);
        String[] words = {topWords, underWords};
        List<List<Segment>> parts = List.of(top == null ? List.of() : top, under == null ? List.of() : under);
        List<List<Tick>> ticks = List.of(topTicks == null ? List.of() : topTicks, underTicks == null ? List.of() : underTicks);
        for (int i = 0; i < 2; i++) {
            Label l = new Label(words[i]);
            l.setWrapText(true);
            l.setMinWidth(labelWidth);
            l.setPrefWidth(labelWidth);
            l.setMaxWidth(labelWidth);
            l.setStyle(Palette.words(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
            SegmentBar bar = segmentBar(parts.get(i), scale, ticks.get(i), 0, band);
            HBox.setHgrow(bar, Priority.ALWAYS);
            HBox row = new HBox(Palette.GAP_LOOSE, l, bar);
            row.setAlignment(Pos.CENTER_LEFT);
            both.getChildren().add(row);
        }
        return both;
    }

    /* ----------------------------- a status banner ----------------------------- */

    /**
     * A status banner (0.7.33): a state's icon in a square tinted in its
     * colour, its word as a chip, and the model's own sentence for it,
     * whole and wrapping - on the raised ground with a 3 px edge at the left
     * in the same colour. The Bank's state; Build's "Your job" line and the
     * Government's and Trade's verdicts are its next readers.
     */
    public static HBox statusBanner(String word, String tone, String sentence, String svg) {
        Label says = new Label(sentence);
        says.setWrapText(true);
        says.setMinWidth(0);
        says.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
        HBox.setHgrow(says, Priority.ALWAYS);
        says.setMaxWidth(Double.MAX_VALUE);
        HBox banner = new HBox(Palette.GAP_LOOSE, iconSquare(svg, tone, 28, 15), chip(word, tone), says);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setMinHeight(44);
        banner.setMaxWidth(Double.MAX_VALUE);
        banner.setStyle("-fx-padding: 8 14 8 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + " "
                + Palette.EDGE + " " + Palette.EDGE + " " + tone + "; -fx-border-width: 1 1 1 3;");
        return banner;
    }

    /* =====================================================================
       THE BUTTON THAT ASKS TO BE PRESSED (0.7.34)

       Jerus, playing 0.7.31: "just tiny thing, everywhere you have build,
       like the build button, it should be more intuitive aka like an actual
       button that is basically asking to be pressed, cause currently its a
       tiny text". A card's Build was a small grey button the size of "+100",
       disabled until the stepper left 0; the land office's cards ended in a
       small "Buy · borrow"; Services' cards in a pink "Build for it ›". Two
       pieces now, wherever the player commits to building or buying and
       wherever a link's job is to send them there:
         AN ACTION BUTTON - the action itself: as wide as its card, about
           40 px tall, rounded as the cards are, in its area's colour (the
           building pink: Build and the land office are one area), its icon
           at the left of a bold label that carries the order - "Build 3 ·
           $37.5M", the count and the quote's all-in total - and a smaller
           line under it when there is more to say. Four looks (Look): GO,
           filled, the one thing on the card to press; CHOOSE, outlined,
           nothing chosen yet; CREDIT, outlined, it goes ahead on a loan;
           HELD, neutral, it cannot go ahead as it stands, and says why.
           GO and HELD lighter under the pointer and darker pressed, CHOOSE
           and CREDIT tinted deeper each time; the hand cursor. Beside a
           heading's words (ACTION_INLINE) it is 32 px and as wide as its own.
         A DOOR AS A PILL - a way to Build or the land office, not the action
           itself: outlined in the area's colour, its icon, its words and an
           arrow, about 30 px tall.
       What a button says is a Press, worked out without a node, so a probe
       reads every label a screen composes. Both are regions rather than
       Buttons, as door() is a Label: a Button's graphic does not wrap, and
       nothing may end in "...". A double-click is one press: its second
       click does nothing, so a click that sets a card's count from none can
       never become an order (BuildScreen.orderControls()).
       ===================================================================== */

    /** How an action button looks: GO filled, ready; CHOOSE outlined, nothing chosen yet; CREDIT outlined, it goes ahead on a loan; HELD neutral, it cannot go ahead as it stands. */
    public enum Look { GO, CHOOSE, CREDIT, HELD }

    /** What an action button says: its look, its words, and a smaller line under them (null: none). */
    public record Press(Look look, String main, String sub) {
        /** Both lines as one, for a probe and the reader's accessible text. */
        public String words() { return sub == null ? main : main + " / " + sub; }
    }

    /** An action button's height on a card, where it is the thing the card is for. */
    public static final double ACTION_TALL = 40;

    /** ...beside a heading, where it shares a row with words: Build's "Build all three", the land office's "Buy the next 5". */
    public static final double ACTION_INLINE = 32;

    /** A door pill's height. */
    public static final double DOOR_TALL = 30;

    /** An action button: a Press in its look, and what a press does. */
    public static ActionButton actionButton(String svg, String accent, double tall, Press press, Runnable go) {
        return new ActionButton(svg, accent, tall, press, go);
    }

    /**
     * The action button (0.7.34): a region the height of `tall` or what its
     * words need, as wide as its parent gives it, its words wrapping inside
     * it; show() changes what it says in place - a card's stepper reprices
     * it without a redraw.
     */
    public static final class ActionButton extends javafx.scene.layout.StackPane {
        private final String svg, accent;
        private final double tall;
        private final HBox row = new HBox(8);
        private final Label main = new Label(), sub = new Label();
        private Press press;
        private Runnable go;

        ActionButton(String svg, String accent, double tall, Press press, Runnable go) {
            this.svg = svg;
            this.accent = accent;
            this.tall = tall;
            this.go = go;
            main.setWrapText(true);
            main.setMinWidth(0);
            sub.setWrapText(true);
            sub.setMinWidth(0);
            VBox words = new VBox(0, main, sub);
            words.setAlignment(Pos.CENTER_LEFT);
            words.setMinWidth(0);
            row.setAlignment(Pos.CENTER);
            row.setMinWidth(0);
            row.setMouseTransparent(true);
            row.getChildren().addAll(new Region(), words);
            getChildren().add(row);
            setCursor(javafx.scene.Cursor.HAND);
            setPickOnBounds(true);
            hoverProperty().addListener((o, was, is) -> dress());
            pressedProperty().addListener((o, was, is) -> dress());
            // One press a click: the second click of a double-click is not a second press.
            addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
                e.consume();
                if (e.getButton() != javafx.scene.input.MouseButton.PRIMARY || e.getClickCount() > 1) return;
                if (this.go != null) this.go.run();
            });
            show(press);
        }

        /** Says a new Press, restyled in place. */
        public ActionButton show(Press p) {
            press = p;
            main.setText(p.main());
            sub.setText(p.sub() == null ? "" : p.sub());
            sub.setVisible(p.sub() != null);
            sub.setManaged(p.sub() != null);
            setAccessibleText(p.words());
            dress();
            return this;
        }

        /** What it says now. */
        public Press press() { return press; }

        /** What a press does from now on. */
        public void onPress(Runnable go) { this.go = go; }

        private void dress() {
            boolean over = isHover(), down = isPressed();
            String ground, edge, ink, faint;
            switch (press.look()) {
                case GO -> {
                    // The area's darker step at rest, so white words read on it; half way back to the
                    // area's own colour under the pointer, a fifth of the way to black pressed.
                    String[] steps = Palette.stepsOf(accent);
                    String rest = steps.length == 2 ? steps[1] : accent;
                    ground = down ? mix(rest, "#000000", .2) : over ? mix(rest, accent, .5) : rest;
                    edge = ground;
                    ink = "white";
                    faint = tint("#ffffff", .85);
                }
                case HELD -> {
                    ground = down ? Palette.FIELD : over ? Palette.HOVER : Palette.CONTROL;
                    edge = Palette.CONTROL_EDGE;
                    ink = Palette.TEXT_LABEL;
                    faint = Palette.TEXT_MUTED;
                }
                default -> {
                    ground = tint(accent, down ? .26 : over ? .15 : .05);
                    edge = accent;
                    ink = accent;
                    faint = Palette.TEXT_LABEL;
                }
            }
            // KEEPS ITS SIZE: 13 px and 9 px of Plex Sans are 29 px of two lines, which with the padding and
            // the border are ACTION_TALL - so a look with a line under its words is as tall as one without.
            setStyle("-fx-background-color: " + ground + "; -fx-background-radius: 8; -fx-border-color: " + edge
                    + "; -fx-border-radius: 8; -fx-border-width: 1.5; -fx-padding: 4 12 4 12;");
            main.setStyle("-fx-font-family: " + Fonts.sans() + "; -fx-font-weight: bold; -fx-font-size: 13px;"
                    + " -fx-text-fill: " + ink + ";");
            sub.setStyle(Palette.words(Palette.SIZE_CAPTION, faint));
            row.getChildren().set(0, icon(svg, ink, 16));
        }

        @Override protected double computeMinHeight(double width) {
            return Math.max(tall, super.computeMinHeight(width));
        }

        @Override protected double computePrefHeight(double width) {
            return Math.max(tall, super.computePrefHeight(width));
        }

        @Override protected double computeMaxHeight(double width) {
            return computePrefHeight(width);
        }
    }

    /** Two colours mixed, `t` of the way from `a` to `b`, as "#rrggbb". */
    static String mix(String a, String b, double t) {
        javafx.scene.paint.Color c = javafx.scene.paint.Color.web(a).interpolate(javafx.scene.paint.Color.web(b), t);
        return String.format("#%02x%02x%02x", (int) Math.round(c.getRed() * 255),
                (int) Math.round(c.getGreen() * 255), (int) Math.round(c.getBlue() * 255));
    }

    /**
     * A door as a pill (0.7.34): a way to Build or the land office - "Build
     * for it", "Build · Roads & transit", "Land office" - outlined in the
     * area's colour, its icon, its words whole and an arrow; tinted a little
     * deeper under the pointer and deeper again pressed. Like door(), it
     * opens itself and not a card it sits in, and a double-click opens it
     * once.
     */
    public static HBox doorPill(String text, String svg, String accent, Runnable go) {
        Label words = new Label(text);
        words.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, accent));
        words.setMinWidth(Region.USE_PREF_SIZE);
        Label arrow = new Label("›");
        arrow.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, accent));
        arrow.setMinWidth(Region.USE_PREF_SIZE);
        HBox pill = new HBox(6, icon(svg, accent, 14), words, arrow);
        pill.setAlignment(Pos.CENTER);
        pill.setMinHeight(DOOR_TALL);
        pill.setPrefHeight(DOOR_TALL);
        pill.setMaxHeight(DOOR_TALL);
        pill.setMinWidth(Region.USE_PREF_SIZE);
        pill.setMaxWidth(Region.USE_PREF_SIZE);
        pill.setCursor(javafx.scene.Cursor.HAND);
        pill.setPickOnBounds(true);
        Runnable dress = () -> pill.setStyle("-fx-padding: 0 12 0 10; -fx-background-radius: " + DOOR_TALL / 2
                + "; -fx-border-radius: " + DOOR_TALL / 2 + "; -fx-border-color: " + accent + "; -fx-border-width: 1;"
                + " -fx-background-color: " + tint(accent, pill.isPressed() ? .26 : pill.isHover() ? .15 : .06) + ";");
        dress.run();
        pill.hoverProperty().addListener((o, was, is) -> dress.run());
        pill.pressedProperty().addListener((o, was, is) -> dress.run());
        pill.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            e.consume();
            if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY && e.getClickCount() == 1) go.run();
        });
        return pill;
    }

    /* =====================================================================
       THE PIECES THE TRADE SCREEN ADDED (0.7.35)

       The ninth of the rail's screens redrawn in Build's style (the
       project's spec-trade-0734.md, section 6) needed four. MIRRORED BARS:
       a row a good, what the city bought of it growing left from a centre
       axis and what it sold growing right, both on one scale, each figure at
       its bar's end - the Trade tab's goods and businesses, and the shape
       Government's net exports and Sectors' home-and-world split could take.
       A BAND TRACK: a meter's coloured bands and the city's mark at any
       width, and A GAUGE CARD around it - the reading, its verdict as a
       chip, the bands and a foot line - for the three quiet gauges (import
       cover, what backs the hot money, the distance from parity).
       DIVERGING BARS: forces either side of a centre line, stronger to the
       left and weaker to the right, each with its sentence behind an (i) -
       the currency's push and pull. And bandMeter(), moved here from the
       Trade tab unchanged (the spec's D24): its 560 px statement row was
       the three gauges' shape, and it lent two gauges to the
       infrastructure pages (ServicesScreen's, then InfrastructureScreen's)
       until 0.7.29. Nothing draws with it since this batch; it stays for
       a statement column that wants a meter.
       ===================================================================== */

    /**
     * A meter with named bands and the city's mark on it (TradeScreen's
     * THE THREE QUIET GAUGES until 0.7.35, moved unchanged; the gauge cards
     * draw the same bands with bandTrack()).
     *
     * @param at    where the city sits, 0 to 1 across the whole meter
     * @param edges the right-hand edge of each band, in the same 0-to-1 space
     * @param muted true when the reading does not apply, so the mark is hidden
     *              rather than parked at zero and read as a bad one
     */
    public static VBox bandMeter(String label, String reading, double at,
                           double[] edges, String[] tones, String note, boolean muted) {

        final double WIDE = STATEMENT - 230;

        Label name = new Label(label);
        name.setPrefWidth(200);
        name.setMinWidth(200);
        name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_BODY));

        HBox track = new HBox(0);
        track.setAlignment(Pos.CENTER_LEFT);
        double from = 0;
        for (int i = 0; i < edges.length; i++) {
            double w = Math.max(2, (edges[i] - from) * WIDE);
            Region band = new Region();
            band.setPrefSize(w, 10);
            band.setMinSize(w, 10);
            band.setMaxSize(w, 10);
            band.setStyle("-fx-background-color: " + tones[i] + "; -fx-opacity: .30;");
            track.getChildren().add(band);
            from = edges[i];
        }

        Region mark = new Region();
        mark.setPrefSize(3, 18);
        mark.setMinSize(3, 18);
        mark.setStyle("-fx-background-color: " + Palette.TEXT_MAX + ";");
        showIf(mark, !muted);
        javafx.scene.layout.Pane over = new javafx.scene.layout.Pane(mark);
        over.setPrefSize(WIDE, 18);
        over.setMinSize(WIDE, 18);
        over.setMaxSize(WIDE, 18);
        mark.setLayoutX(Math.max(0, Math.min(WIDE - 3, at * WIDE)));
        mark.setLayoutY(-4);

        javafx.scene.layout.StackPane bar = new javafx.scene.layout.StackPane(track, over);
        javafx.scene.layout.StackPane.setAlignment(track, Pos.CENTER_LEFT);
        javafx.scene.layout.StackPane.setAlignment(over, Pos.CENTER_LEFT);
        bar.setMaxWidth(WIDE);

        Label figure = new Label(reading);
        figure.setPrefWidth(140);
        figure.setMinWidth(140);
        figure.setAlignment(Pos.CENTER_RIGHT);
        figure.setStyle(Palette.figure(Palette.SIZE_CAPTION,
                muted ? Palette.TEXT_SPENT : Palette.TEXT_HEAD));

        HBox row = new HBox(Palette.GAP_TIGHT, name, bar, figure);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);

        Label under = new Label("      " + note);
        under.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_SPENT));

        VBox box = new VBox(1, row, under);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 5 0 8 0;");
        return box;
    }

    /* ----------------------------- a band track ----------------------------- */

    /**
     * A meter's bands at any width (0.7.35): the bands laid end to end in
     * their tones, faint, each `edges[i]` the right-hand end of band i in
     * a 0-to-1 space; the city's mark at `at` (0 to 1), standing proud of
     * the band, or none when `muted` - a reading that does not apply is
     * not parked at zero; and named ticks across it (`ticks`, in the same
     * 0-to-1 space; null for none). `width` 0 or less fills what holds it.
     */
    public static BandTrack bandTrack(double at, double[] edges, String[] tones, List<Tick> ticks,
                                      double width, double band, boolean muted) {
        return new BandTrack(at, edges, tones, ticks, width, band, muted);
    }

    /** The track bandTrack() draws: a Pane that lays its bands, ticks and mark out at whatever width it is given. */
    public static final class BandTrack extends javafx.scene.layout.Pane {
        private final double at, band;
        private final double[] edges;
        private final List<Tick> ticks;
        private final boolean muted;
        private final List<Region> bands = new java.util.ArrayList<>(), marks = new java.util.ArrayList<>();
        private final List<Label> names = new java.util.ArrayList<>();
        private final Region mark = new Region();

        BandTrack(double at, double[] edges, String[] tones, List<Tick> ticks, double width, double band, boolean muted) {
            this.at = Double.isFinite(at) ? Math.max(0, Math.min(1, at)) : 0;
            this.edges = edges == null ? new double[0] : edges;
            this.ticks = ticks == null ? List.of() : ticks;
            this.band = band;
            this.muted = muted;
            for (int i = 0; i < this.edges.length; i++) {
                Region r = new Region();
                String tone = tones != null && i < tones.length ? tones[i] : Palette.CONTROL;
                double rl = i == 0 ? band / 2 : 0, rr = i == this.edges.length - 1 ? band / 2 : 0;
                r.setStyle("-fx-background-color: " + tint(tone, .32) + ";"
                        + " -fx-background-radius: " + rl + " " + rr + " " + rr + " " + rl + ";");
                bands.add(r);
                getChildren().add(r);
            }
            for (Tick t : this.ticks) {
                Region m = new Region();
                m.setStyle("-fx-background-color: " + t.colour() + ";");
                if (t.tip() != null) {
                    Tooltip tip = new Tooltip(t.tip());
                    tip.setShowDelay(Duration.millis(200));
                    Tooltip.install(m, tip);
                }
                marks.add(m);
                getChildren().add(m);
                Label name = new Label(t.name() == null ? "" : t.name());
                name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
                name.setVisible(t.name() != null);
                names.add(name);
                getChildren().add(name);
            }
            mark.setStyle("-fx-background-color: " + Palette.TEXT_MAX + "; -fx-background-radius: 1;");
            mark.setVisible(!muted);
            getChildren().add(mark);
            setMinWidth(width > 0 ? width : 0);
            setPrefWidth(width > 0 ? width : 200);
            setMaxWidth(width > 0 ? width : Double.MAX_VALUE);
            setMinHeight(tall());
            setPrefHeight(tall());
        }

        private boolean named() {
            for (Tick t : ticks) if (t.name() != null) return true;
            return false;
        }

        /** The band, the mark standing 4 px proud of it each way, and a line of names under it when a tick has one. */
        private double tall() { return band + 8 + (named() ? 14 : 0); }

        @Override protected void layoutChildren() {
            double w = getWidth();
            double y = 4;
            double from = 0;
            for (int i = 0; i < edges.length; i++) {
                double to = Math.max(from, Math.min(1, edges[i]));
                double x0 = Math.round(w * from), x1 = Math.round(w * to);
                bands.get(i).resizeRelocate(x0, y, Math.max(0, x1 - x0 - (i < edges.length - 1 ? 1 : 0)), band);
                from = to;
            }
            for (int i = 0; i < ticks.size(); i++) {
                Tick t = ticks.get(i);
                boolean on = t.at() >= 0 && t.at() <= 1;
                Region m = marks.get(i);
                Label name = names.get(i);
                m.setVisible(on);
                name.setVisible(on && t.name() != null);
                if (!on) continue;
                double x = Math.round(w * t.at());
                m.resizeRelocate(Math.max(0, Math.min(w - t.width(), x - t.width() / 2)), y - 2, t.width(), band + 4);
                double nw = name.prefWidth(-1), nh = name.prefHeight(-1);
                name.resizeRelocate(Math.max(0, Math.min(w - nw, x - nw / 2)), y + band + 5, nw, nh);
            }
            double x = Math.round(w * at);
            mark.resizeRelocate(Math.max(0, Math.min(w - 3, x - 1.5)), 0, 3, band + 8);
        }
    }

    /* ----------------------------- a gauge card ----------------------------- */

    /**
     * A gauge as a card (0.7.35): its icon in a tinted square, its title and
     * an (i) holding `info`; the reading in mono 22 beside its verdict as a
     * chip in `chipTone` (no chip when `chip` is null); the bands with the
     * city's mark at `at` (bandTrack(); `muted` hides the mark); and `foot`,
     * a muted line under it. A click on the card runs `go` (null: none), the
     * edge lighting under the pointer as Build's cards do.
     */
    public static VBox gaugeCard(String svg, String colour, String title, String info, String reading,
                                 String readingTone, String chip, String chipTone, double at, double[] edges,
                                 String[] tones, List<Tick> ticks, String foot, boolean muted, Runnable go) {
        Label t = new Label(title);
        t.setWrapText(true);
        t.setMinWidth(0);
        t.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        HBox head = new HBox(Palette.GAP, iconSquare(svg, colour, 28, 15), t);
        if (info != null) head.getChildren().add(infoButton(info, true));
        head.setAlignment(Pos.CENTER_LEFT);
        Label big = new Label(reading);
        big.setMinWidth(Region.USE_PREF_SIZE);
        big.setStyle(Palette.figure(22, readingTone == null ? Palette.TEXT_HEAD : readingTone));
        HBox figure = new HBox(Palette.GAP, big);
        figure.setAlignment(Pos.CENTER_LEFT);
        if (chip != null) figure.getChildren().add(chip(chip, chipTone == null ? Palette.TEXT_MUTED : chipTone));
        VBox card = new VBox(8, head, figure, bandTrack(at, edges, tones, ticks, 0, 12, muted));
        if (foot != null) {
            Label f = new Label(foot);
            f.setWrapText(true);
            f.setMinWidth(0);
            f.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            card.getChildren().add(f);
        }
        card.setMaxWidth(Double.MAX_VALUE);
        String rest = "-fx-padding: 12; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: ";
        card.setStyle(rest + Palette.EDGE + ";");
        if (go != null) {
            card.setCursor(javafx.scene.Cursor.HAND);
            card.setOnMouseEntered(e -> card.setStyle(rest + Palette.ACCENT + ";"));
            card.setOnMouseExited(e -> card.setStyle(rest + Palette.EDGE + ";"));
            card.setOnMouseClicked(e -> go.run());
        }
        return card;
    }

    /* ----------------------------- mirrored bars ----------------------------- */

    /**
     * One row of mirrorRows() (0.7.35): its icon (null: none) and its name,
     * which wraps and is never cut; what goes left of the axis and what goes
     * right, in the rows' one unit, each with its figure as written (null:
     * the side is empty and draws nothing); a figure for the net column
     * (null: none); its tooltip and what a click on it does, handed the
     * row to anchor a popover (each may be null).
     */
    public record Mirror(String svg, String name, double left, double right, String leftFigure,
                         String rightFigure, String net, String tip, java.util.function.Consumer<javafx.scene.Node> go) { }

    /**
     * Mirrored bars (0.7.35): a row each, its icon and name at the left, then
     * a lane either side of a centre axis - `left` growing leftward from it
     * in `leftColour`, `right` rightward in `rightColour`, both on `scale`
     * (0 or less: the largest side of any row) - each figure just past its
     * bar's end, and a net column at the right when `netWidth` is over
     * nothing. `leftHead` and `rightHead` (null: none) name the two sides
     * over the lanes, against the axis. The bars grow out of the axis with
     * grow() (People's animate()).
     */
    public static MirrorRows mirrorRows(List<Mirror> rows, double scale, String leftColour, String rightColour,
                                        String leftHead, String rightHead, double nameWidth, double netWidth) {
        return new MirrorRows(rows, scale, leftColour, rightColour, leftHead, rightHead, nameWidth, netWidth);
    }

    /** The rows mirrorRows() draws: a Pane that lays them out at whatever width it is given. */
    public static final class MirrorRows extends javafx.scene.layout.Pane {
        private final List<Mirror> rows;
        private final double scale, nameWidth, netWidth;
        private final List<javafx.scene.Node> icons = new java.util.ArrayList<>();
        private final List<Label> names = new java.util.ArrayList<>(), lefts = new java.util.ArrayList<>(),
                rights = new java.util.ArrayList<>(), nets = new java.util.ArrayList<>();
        private final List<Region> leftBars = new java.util.ArrayList<>(), rightBars = new java.util.ArrayList<>(),
                grounds = new java.util.ArrayList<>();
        private final Region axis = new Region();
        private final Label leftHead, rightHead;
        /** How far the bars have grown out of the axis, 0 to 1 (grow()). */
        private final javafx.beans.property.DoubleProperty grown = new javafx.beans.property.SimpleDoubleProperty(1);

        /** A row's least height, its bar's thickness, an icon's size and the gaps between the columns. */
        static final double ROW = 28, BAR = 12, ICON = 18, GAP = 8;

        MirrorRows(List<Mirror> rows, double scale, String leftColour, String rightColour, String leftHead,
                   String rightHead, double nameWidth, double netWidth) {
            this.rows = rows == null ? List.of() : rows;
            double most = 0;
            for (Mirror m : this.rows) most = Math.max(most, Math.max(finite(m.left()), finite(m.right())));
            this.scale = scale > 0 ? scale : most;
            this.nameWidth = nameWidth;
            this.netWidth = Math.max(0, netWidth);
            axis.setStyle("-fx-background-color: " + Palette.TEXT_SPENT + ";");
            getChildren().add(axis);
            this.leftHead = head(leftHead, leftColour);
            this.rightHead = head(rightHead, rightColour);
            for (Mirror m : this.rows) {
                Region ground = new Region();
                ground.setStyle("-fx-background-radius: 4;");
                grounds.add(ground);
                getChildren().add(0, ground);
                javafx.scene.Node mark = m.svg() == null ? null : icon(m.svg(), Palette.TEXT_LABEL, ICON - 2);
                icons.add(mark);
                if (mark != null) getChildren().add(mark);
                Label name = new Label(m.name());
                name.setWrapText(true);
                name.setStyle(Palette.words(Palette.SIZE_LABEL + 1, Palette.TEXT_BODY));
                names.add(name);
                getChildren().add(name);
                leftBars.add(bar(leftColour));
                rightBars.add(bar(rightColour));
                lefts.add(figure(m.leftFigure()));
                rights.add(figure(m.rightFigure()));
                Label net = figure(m.net());
                net.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT_HEAD));
                nets.add(net);
                if (m.tip() != null || m.go() != null) {
                    if (m.tip() != null) {
                        Tooltip tip = new Tooltip(m.tip());
                        tip.setShowDelay(Duration.millis(250));
                        tip.setWrapText(true);
                        tip.setMaxWidth(POPOVER_WIDTH);
                        Tooltip.install(ground, tip);
                    }
                    if (m.go() != null) {
                        ground.setCursor(javafx.scene.Cursor.HAND);
                        ground.setOnMouseEntered(e -> ground.setStyle("-fx-background-radius: 4; -fx-background-color: "
                                + Palette.CONTROL + ";"));
                        ground.setOnMouseExited(e -> ground.setStyle("-fx-background-radius: 4;"));
                        ground.setOnMouseClicked(e -> {
                            e.consume();
                            m.go().accept(ground);
                        });
                    }
                }
            }
            // The bars, figures and names let the pointer through to the row's ground under them.
            for (javafx.scene.Node n : getChildren()) if (!grounds.contains(n)) n.setMouseTransparent(true);
            setMinWidth(0);
            setPrefWidth(600);
            setMaxWidth(Double.MAX_VALUE);
            grown.addListener((o, was, now) -> requestLayout());
        }

        private static double finite(double v) { return Double.isFinite(v) ? Math.max(0, v) : 0; }

        /** Row i's ground - the node its click is handed, to anchor a popover - or null past the rows. */
        public Region row(int i) { return i >= 0 && i < grounds.size() ? grounds.get(i) : null; }

        private Label head(String text, String colour) {
            if (text == null) return null;
            Label l = new Label(text);
            l.setStyle(Palette.strong(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
            l.setMinWidth(Region.USE_PREF_SIZE);
            Region swatch = new Region();
            swatch.setMinSize(8, 8);
            swatch.setPrefSize(8, 8);
            swatch.setMaxSize(8, 8);
            swatch.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 2;");
            l.setGraphic(swatch);
            l.setGraphicTextGap(5);
            getChildren().add(l);
            return l;
        }

        private Region bar(String colour) {
            Region r = new Region();
            r.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 2;");
            getChildren().add(r);
            return r;
        }

        private Label figure(String text) {
            Label l = new Label(text == null ? "" : text);
            l.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
            l.setMinWidth(Region.USE_PREF_SIZE);
            l.setVisible(text != null);
            getChildren().add(l);
            return l;
        }

        /** The bars grow out of the axis over `millis` - a month landing on the page. */
        public void grow(double millis) {
            grown.set(0);
            javafx.animation.Timeline t = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(Duration.millis(millis),
                            new javafx.animation.KeyValue(grown, 1, javafx.animation.Interpolator.EASE_OUT)));
            t.play();
        }

        private double headRoom() { return leftHead != null || rightHead != null ? 18 : 0; }

        /** The name column's own width: what is left of it after the icon. */
        private double nameRoom() { return Math.max(40, nameWidth - (ICON + 6)); }

        private double rowHeight(int i) {
            return Math.max(ROW, names.get(i).prefHeight(nameRoom()) + 6);
        }

        @Override public javafx.geometry.Orientation getContentBias() { return null; }

        @Override protected double computePrefHeight(double width) {
            double h = headRoom();
            for (int i = 0; i < rows.size(); i++) h += rowHeight(i);
            return h;
        }

        @Override protected double computeMinHeight(double width) { return computePrefHeight(width); }

        @Override protected void layoutChildren() {
            double w = getWidth();
            double lanes = Math.max(40, w - nameWidth - GAP - 2 - (netWidth > 0 ? GAP + netWidth : 0));
            double lane = lanes / 2;
            double axisX = nameWidth + GAP + lane;
            // The longest figure either side, so every bar shares one scale and each figure fits past its end.
            double leftFig = 0, rightFig = 0;
            for (int i = 0; i < rows.size(); i++) {
                if (lefts.get(i).isVisible()) leftFig = Math.max(leftFig, lefts.get(i).prefWidth(-1));
                if (rights.get(i).isVisible()) rightFig = Math.max(rightFig, rights.get(i).prefWidth(-1));
            }
            double reach = Math.max(4, lane - Math.max(leftFig, rightFig) - 6);
            double g = grown.get();
            double y = 0;
            if (leftHead != null) {
                double hw = leftHead.prefWidth(-1), hh = leftHead.prefHeight(-1);
                leftHead.resizeRelocate(Math.max(nameWidth + GAP, axisX - 6 - hw), 0, hw, hh);
            }
            if (rightHead != null) {
                double hw = rightHead.prefWidth(-1), hh = rightHead.prefHeight(-1);
                rightHead.resizeRelocate(axisX + 8, 0, hw, hh);
            }
            y += headRoom();
            double top = y;
            for (int i = 0; i < rows.size(); i++) {
                Mirror m = rows.get(i);
                double h = rowHeight(i);
                grounds.get(i).resizeRelocate(0, y, w, h);
                javafx.scene.Node mark = icons.get(i);
                if (mark != null) mark.resizeRelocate(0, y + (h - ICON) / 2, ICON, ICON);
                Label name = names.get(i);
                double nh = name.prefHeight(nameRoom());
                name.resizeRelocate(ICON + 6, y + (h - nh) / 2, nameRoom(), nh);
                double mid = y + h / 2;
                double ll = scale > 0 ? reach * finite(m.left()) / scale * g : 0;
                double rl = scale > 0 ? reach * finite(m.right()) / scale * g : 0;
                Region lb = leftBars.get(i), rb = rightBars.get(i);
                lb.setVisible(m.leftFigure() != null && ll > 0);
                rb.setVisible(m.rightFigure() != null && rl > 0);
                lb.resizeRelocate(axisX - Math.max(1, ll), mid - BAR / 2, Math.max(1, ll), BAR);
                rb.resizeRelocate(axisX + 2, mid - BAR / 2, Math.max(1, rl), BAR);
                Label lf = lefts.get(i), rf = rights.get(i);
                double lw = lf.prefWidth(-1), lh = lf.prefHeight(-1);
                lf.resizeRelocate(axisX - Math.max(1, ll) - 4 - lw, mid - lh / 2, lw, lh);
                double rw = rf.prefWidth(-1), rh = rf.prefHeight(-1);
                rf.resizeRelocate(axisX + 2 + Math.max(1, rl) + 4, mid - rh / 2, rw, rh);
                Label net = nets.get(i);
                if (netWidth > 0) {
                    double tw = net.prefWidth(-1), th = net.prefHeight(-1);
                    net.resizeRelocate(w - tw, mid - th / 2, tw, th);
                }
                net.setVisible(netWidth > 0 && m.net() != null);
                y += h;
            }
            axis.resizeRelocate(axisX, top, 2, Math.max(0, y - top));
        }
    }

    /* ----------------------------- diverging bars ----------------------------- */

    /**
     * One row of divergingBars() (0.7.35): its name; its value, signed - below
     * nothing drawn left of the centre line, above it right; a TOTAL row is
     * the model's own sum, drawn in the stronger colour; a GHOST row is
     * outlined, what would be there and is not (on the Trade tab, a pinned
     * rate's push, pull and move); `info` its sentence behind an (i) and
     * `line` a muted line under the name (each may be null).
     */
    public record Force(String name, double value, boolean total, boolean ghost, String info, String line) { }

    /**
     * Diverging bars (0.7.35): a row each - its name with its (i) and line
     * in a column `nameWidth` wide, a lane split by a centre line, the bar
     * from the line to the value on a scale of plus or minus `scale` (one
     * past it stops at the lane's end, its figure still whole), and the
     * figure written by `fmt` at the right. `leftWords` and `rightWords`
     * (null: none) name the two directions over the lane. The colours say
     * the kind - `colour` for a row, `totalColour` for a total - and the
     * words say the direction, never a verdict's colour (the Trade spec's
     * B19). It fills the width it is given.
     */
    public static DivergingBars divergingBars(List<Force> rows, double scale, double nameWidth, double figureWidth,
                                              String colour, String totalColour, String leftWords, String rightWords,
                                              java.util.function.DoubleFunction<String> fmt) {
        return new DivergingBars(rows, scale, nameWidth, figureWidth, colour, totalColour, leftWords, rightWords, fmt);
    }

    /** The rows divergingBars() draws: a Pane that lays them out at whatever width it is given. */
    public static final class DivergingBars extends javafx.scene.layout.Pane {
        private final List<Force> rows;
        private final double scale, nameWidth, figureWidth;
        private final List<VBox> names = new java.util.ArrayList<>();
        private final List<Region> bars = new java.util.ArrayList<>(), tracks = new java.util.ArrayList<>();
        private final List<Label> figures = new java.util.ArrayList<>();
        private final Region axis = new Region();
        private final Label leftWords, rightWords;

        /** A row's least height and its bar's thickness. */
        static final double ROW = 30, BAR = 12;

        DivergingBars(List<Force> rows, double scale, double nameWidth, double figureWidth, String colour,
                      String totalColour, String leftWords, String rightWords,
                      java.util.function.DoubleFunction<String> fmt) {
            this.rows = rows == null ? List.of() : rows;
            this.scale = scale > 0 ? scale : 1;
            this.nameWidth = nameWidth;
            this.figureWidth = figureWidth;
            for (Force f : this.rows) {
                Region track = new Region();
                track.setStyle("-fx-background-color: " + Palette.CONTROL + "; -fx-background-radius: 3;");
                tracks.add(track);
                getChildren().add(track);
            }
            axis.setStyle("-fx-background-color: " + Palette.TEXT_SPENT + ";");
            getChildren().add(axis);
            this.leftWords = words(leftWords);
            this.rightWords = words(rightWords);
            for (Force f : this.rows) {
                Label name = new Label(f.name());
                name.setWrapText(true);
                name.setMinWidth(0);
                name.setStyle(f.total() ? Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_HEAD)
                        : Palette.words(Palette.SIZE_LABEL + 1, Palette.TEXT_BODY));
                HBox titled = new HBox(Palette.GAP_TIGHT, name);
                titled.setAlignment(Pos.CENTER_LEFT);
                if (f.info() != null) titled.getChildren().add(infoButton(f.info(), true));
                VBox box = new VBox(1, titled);
                if (f.line() != null) {
                    Label l = new Label(f.line());
                    l.setWrapText(true);
                    l.setMinWidth(0);
                    l.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
                    box.getChildren().add(l);
                }
                box.setMinWidth(0);
                names.add(box);
                getChildren().add(box);
                String c = f.total() ? totalColour : colour;
                Region bar = new Region();
                bar.setStyle(f.ghost()
                        ? "-fx-background-color: " + c + "33; -fx-border-color: " + c + "; -fx-border-width: 1;"
                          + " -fx-background-radius: 2; -fx-border-radius: 2;"
                        : "-fx-background-color: " + c + "; -fx-background-radius: 2;");
                bars.add(bar);
                getChildren().add(bar);
                Label fig = new Label(fmt.apply(f.value()));
                fig.setMinWidth(Region.USE_PREF_SIZE);
                fig.setStyle(Palette.figure(Palette.SIZE_LABEL, f.total() ? Palette.TEXT_HEAD : Palette.TEXT_LABEL));
                figures.add(fig);
                getChildren().add(fig);
            }
            setMinWidth(0);
            setPrefWidth(500);
            setMaxWidth(Double.MAX_VALUE);
        }

        private Label words(String text) {
            if (text == null) return null;
            Label l = new Label(text);
            l.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            l.setMinWidth(Region.USE_PREF_SIZE);
            getChildren().add(l);
            return l;
        }

        private double headRoom() { return leftWords != null || rightWords != null ? 16 : 0; }

        private double rowHeight(int i) { return Math.max(ROW, names.get(i).prefHeight(nameWidth) + 6); }

        @Override protected double computePrefHeight(double width) {
            double h = headRoom();
            for (int i = 0; i < rows.size(); i++) h += rowHeight(i);
            return h;
        }

        @Override protected double computeMinHeight(double width) { return computePrefHeight(width); }

        @Override protected void layoutChildren() {
            double w = getWidth();
            double laneX = nameWidth + 10;
            double lane = Math.max(40, w - laneX - 10 - figureWidth);
            double half = lane / 2, axisX = laneX + half;
            if (leftWords != null) {
                double lw = leftWords.prefWidth(-1), lh = leftWords.prefHeight(-1);
                leftWords.resizeRelocate(Math.max(laneX, axisX - 6 - lw), 0, lw, lh);
            }
            if (rightWords != null) {
                double rw = rightWords.prefWidth(-1), rh = rightWords.prefHeight(-1);
                rightWords.resizeRelocate(axisX + 6, 0, rw, rh);
            }
            double y = headRoom(), top = y;
            for (int i = 0; i < rows.size(); i++) {
                Force f = rows.get(i);
                double h = rowHeight(i);
                VBox name = names.get(i);
                double nh = name.prefHeight(nameWidth);
                name.resizeRelocate(0, y + (h - nh) / 2, nameWidth, nh);
                double mid = y + h / 2;
                tracks.get(i).resizeRelocate(laneX, mid - BAR / 2 - 1, lane, BAR + 2);
                double v = Double.isFinite(f.value()) ? f.value() : 0;
                double len = Math.min(half, Math.abs(v) / scale * half);
                Region bar = bars.get(i);
                bar.setVisible(len >= .5);
                if (v < 0) bar.resizeRelocate(axisX - len, mid - BAR / 2, len, BAR);
                else       bar.resizeRelocate(axisX + 1, mid - BAR / 2, len, BAR);
                Label fig = figures.get(i);
                double fw = fig.prefWidth(-1), fh = fig.prefHeight(-1);
                fig.resizeRelocate(w - fw, mid - fh / 2, fw, fh);
                y += h;
            }
            axis.resizeRelocate(axisX - .5, top, 2, Math.max(0, y - top));
        }
    }

    /* =====================================================================
       THE PIECES THE POLICY SCREEN ADDED (0.7.36)

       Every dial on the Policy tab says what it would do before it is
       applied, and it said it as a statement under the dial: "What it would
       do", a line a figure "now -> then", a paragraph. The redrawn tab puts
       the dial on a card with what it does beside it (the Policy spec's
       dial card, section 6.1): each effect a row - its words, the figure
       before and after, the move as a chip, and the two as a pair of bars
       on one scale - the after in the accent, ghosted, as a staged change
       has always been. And the dials a page batches (the taxes', the
       schools') have their staged set in a tray at the foot of the stage
       rather than at the foot of a page twelve screens long.
       ===================================================================== */

    /**
     * One effect of a move (0.7.36): its words; the figure before and after,
     * written by `fmt`; the move written by `delta` (null: no chip); the
     * bars' colour; the move's VERDICT colour when the after crosses a line
     * the model draws (null: none - a figure is not good or bad news by
     * moving: the spec's D5); the bars' scale (0: the larger of the two); an
     * (i) after the words (null: none).
     */
    public record Effect(String label, double before, double after, java.util.function.DoubleFunction<String> fmt,
                         java.util.function.DoubleFunction<String> delta, String colour, String verdict,
                         double scale, String info) {
        /** An effect in money's blue with no chip, no verdict and its own scale. */
        public static Effect of(String label, double before, double after, java.util.function.DoubleFunction<String> fmt) {
            return new Effect(label, before, after, fmt, null, Palette.MONEY, null, 0, null);
        }
        public Effect delta(java.util.function.DoubleFunction<String> d) {
            return new Effect(label, before, after, fmt, d, colour, verdict, scale, info);
        }
        public Effect colour(String c) { return new Effect(label, before, after, fmt, delta, c, verdict, scale, info); }
        public Effect verdict(String v) { return new Effect(label, before, after, fmt, delta, colour, v, scale, info); }
        public Effect scale(double s) { return new Effect(label, before, after, fmt, delta, colour, verdict, s, info); }
        public Effect info(String i) { return new Effect(label, before, after, fmt, delta, colour, verdict, scale, i); }
        /** Whether the move shows: the two figures are written differently, or the move's chip says something (a $52k move on a $5.1M budget). */
        public boolean moved() {
            if (!fmt.apply(before).equals(fmt.apply(after))) return true;
            return delta != null && Math.abs(after - before) > 1e-12 && !delta.apply(after - before).equals(delta.apply(0));
        }
        /** The row in words, for a probe: "Profit tax $23.3M -> $24.8M (+$1.5M)". */
        public String words() {
            if (!moved()) return label + " " + fmt.apply(before);
            return label + " " + fmt.apply(before) + " → " + fmt.apply(after)
                    + (delta == null ? "" : " (" + delta.apply(after - before) + ")");
        }
    }

    /**
     * An effect as a row (0.7.36): its words at the left, wrapping and never
     * cut, with its (i); at the right the figure before and, once something
     * has moved it, an arrow and the after in the accent and the move as a
     * chip; under them the two as a pair of bars on one scale - the before
     * filled in the effect's colour, the after a ghost in the accent. A
     * figure below nothing draws no bar: the words carry it.
     */
    public static VBox beforeAfter(Effect e, double width) {
        Label words = new Label(e.label());
        words.setWrapText(true);
        words.setMinWidth(0);
        words.setStyle(Palette.words(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        HBox left = new HBox(Palette.GAP_TIGHT, words);
        left.setAlignment(Pos.CENTER_LEFT);
        if (e.info() != null) left.getChildren().add(infoButton(e.info(), true));
        HBox.setHgrow(left, Priority.ALWAYS);
        left.setMinWidth(0);
        left.setMaxWidth(Double.MAX_VALUE);

        boolean moved = e.moved();
        Label before = new Label(e.fmt().apply(e.before()));
        before.setMinWidth(Region.USE_PREF_SIZE);
        before.setStyle(Palette.figure(Palette.SIZE_LABEL + 1, moved ? Palette.TEXT_MUTED : Palette.TEXT_HEAD));
        HBox right = new HBox(Palette.GAP_TIGHT, before);
        right.setAlignment(Pos.CENTER_RIGHT);
        right.setMinWidth(Region.USE_PREF_SIZE);
        if (moved) {
            Label arrow = new Label("→");
            arrow.setStyle(Palette.words(Palette.SIZE_LABEL + 1, Palette.TEXT_MUTED));
            Label after = new Label(e.fmt().apply(e.after()));
            after.setMinWidth(Region.USE_PREF_SIZE);
            after.setStyle(Palette.figure(Palette.SIZE_LABEL + 1, Palette.ACCENT));
            right.getChildren().addAll(arrow, after);
            if (e.delta() != null) {
                Label chip = chip(e.delta().apply(e.after() - e.before()), e.verdict() != null ? e.verdict() : Palette.TEXT_LABEL);
                chip.setMinWidth(Region.USE_PREF_SIZE);
                right.getChildren().add(chip);
            }
        }
        HBox top = new HBox(Palette.GAP, left, right);
        top.setAlignment(Pos.CENTER_LEFT);
        VBox row = new VBox(3, top);

        boolean drawable = Double.isFinite(e.before()) && Double.isFinite(e.after()) && e.before() >= 0 && e.after() >= 0
                && (e.before() > 0 || e.after() > 0);
        if (drawable) {
            double scale = e.scale() > 0 ? e.scale() : Math.max(e.before(), e.after());
            row.getChildren().add(segmentBar(List.of(Segment.of(Math.min(e.before(), scale), e.colour())), scale,
                    List.of(), width > 0 ? width : 0, 5));
            if (moved) {
                row.getChildren().add(segmentBar(List.of(new Segment(Math.min(e.after(), scale), Palette.ACCENT, true,
                        null, null, null, null)), scale, List.of(), width > 0 ? width : 0, 5));
            }
        }
        row.setMaxWidth(width > 0 ? width : Double.MAX_VALUE);
        return row;
    }

    /** A staged change as a chip in the tray (0.7.36): its words in the accent, and a × that takes it back out of the set. */
    public static HBox stagedChip(String text, Runnable remove) {
        Label words = new Label(text);
        words.setWrapText(true);
        words.setMinWidth(0);
        words.setStyle(Palette.words(Palette.SIZE_LABEL + 1, Palette.ACCENT));
        HBox chip = new HBox(6, words);
        chip.setAlignment(Pos.CENTER_LEFT);
        if (remove != null) {
            Label x = new Label("×");
            x.setMinWidth(Region.USE_PREF_SIZE);
            x.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.ACCENT) + " -fx-cursor: hand;");
            Tooltip tip = new Tooltip("Take it out of the staged set");
            tip.setShowDelay(Duration.millis(250));
            Tooltip.install(x, tip);
            x.setOnMouseClicked(e -> { e.consume(); remove.run(); });
            chip.getChildren().add(x);
        }
        chip.setStyle("-fx-padding: 3 8 3 10; -fx-background-radius: 12; -fx-border-radius: 12;"
                + " -fx-border-color: " + Palette.ACCENT + "; -fx-background-color: " + tint(Palette.ACCENT, .10) + ";");
        return chip;
    }

    /**
     * The staged tray (0.7.36): what a page has staged, as chips that each
     * come out with their ×, THE BUDGET before and after the set (`budget`,
     * null: none), and the page's Apply and Discard - on the raised ground
     * with an accent edge on top, meant for the foot of the stage, where it
     * stays while the page scrolls. `info` (null: none) is an (i) after its
     * caption.
     */
    public static HBox stagedTray(List<? extends javafx.scene.Node> chips, Effect budget, javafx.scene.Node apply,
                                  javafx.scene.Node discard, String info) {
        Label caption = new Label("STAGED");
        caption.setStyle(Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        HBox head = new HBox(Palette.GAP_TIGHT, caption);
        head.setAlignment(Pos.CENTER_LEFT);
        if (info != null) head.getChildren().add(infoButton(info, true));
        javafx.scene.layout.FlowPane list = new javafx.scene.layout.FlowPane(8, 6);
        list.getChildren().addAll(chips);
        VBox left = new VBox(6, head, list);
        HBox.setHgrow(left, Priority.ALWAYS);
        left.setMinWidth(0);
        left.setMaxWidth(Double.MAX_VALUE);
        HBox tray = new HBox(18, left);
        if (budget != null) {
            VBox money = beforeAfter(budget, 300);
            money.setMinWidth(300);
            money.setPrefWidth(320);
            money.setMaxWidth(340);
            tray.getChildren().add(money);
        }
        VBox buttons = new VBox(6);
        buttons.setAlignment(Pos.CENTER);
        buttons.setMinWidth(220);
        buttons.setPrefWidth(220);
        buttons.setMaxWidth(220);
        if (apply != null) buttons.getChildren().add(apply);
        if (discard != null) buttons.getChildren().add(discard);
        tray.getChildren().add(buttons);
        tray.setAlignment(Pos.CENTER_LEFT);
        tray.setStyle("-fx-padding: 10 18 10 18; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-border-color: " + Palette.ACCENT + " transparent transparent transparent; -fx-border-width: 2 0 0 0;");
        return tray;
    }

    /* =====================================================================
       THE PIECES CITY HISTORY ADDED (0.7.37)

       The last of the rail's screens redrawn in Build's style (the project's
       spec-history-0736.md, section 6) needed two. A CHART CARD'S HEAD: a
       line's name, its figure now and how far it moved over the chart's
       window, with whatever controls the card carries at the right - City
       History's two pinned charts, and the Bank's history cards and the
       Sectors cards when they are next touched. A RANGE BAR: where a line
       ended in its own range over a window - a track from its low to its
       high, a hollow tick where it started and a dot where it ended - the
       reading cards under History's big chart.
       ===================================================================== */

    /** A chart card's head (0.7.37): its parts, so a month landing can count the figure up and a click can open the line. */
    public static final class CardHead extends HBox {
        /** The line's name, its figure now, and its move over the window. */
        public final Label name, figure, change;

        CardHead(Label name, Label figure, Label change) {
            super(8);
            this.name = name;
            this.figure = figure;
            this.change = change;
        }
    }

    /**
     * A chart card's head (0.7.37): `name` in the label grey, `figure` in
     * Plex Mono 17, `change` beside it in the body's ink - never a verdict's
     * colour: a line rising is not good news by rising - then a gap and
     * whatever `right` holds. A click on the name runs `open` (null: the
     * name is only words). The name wraps rather than end in "...".
     */
    public static CardHead chartCardHead(String name, String figure, String change, Runnable open,
                                         javafx.scene.Node... right) {
        Label n = new Label(name);
        n.setWrapText(true);
        n.setMinWidth(60);
        String rest = Palette.words(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL);
        n.setStyle(rest);
        if (open != null) {
            n.setStyle(rest + " -fx-cursor: hand;");
            n.setOnMouseEntered(e -> n.setStyle(rest + " -fx-cursor: hand; -fx-underline: true;"));
            n.setOnMouseExited(e -> n.setStyle(rest + " -fx-cursor: hand;"));
            n.setOnMouseClicked(e -> open.run());
        }
        Label f = new Label(figure);
        f.setStyle(Palette.figure(Palette.SIZE_LEAD, Palette.TEXT_HEAD));
        f.setMinWidth(Region.USE_PREF_SIZE);
        Label c = new Label(change == null ? "" : change);
        c.setStyle(Palette.figure(Palette.SIZE_LABEL + 1, Palette.TEXT_BODY));
        c.setMinWidth(Region.USE_PREF_SIZE);
        CardHead head = new CardHead(n, f, c);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        head.getChildren().addAll(n, f, c, gap);
        for (javafx.scene.Node r : right) if (r != null) head.getChildren().add(r);
        head.setAlignment(Pos.CENTER_LEFT);
        head.setMinHeight(28);
        return head;
    }

    /**
     * A range bar (0.7.37): where a line ended in its own range over a
     * window. The track is the range, `lo` at its left and `hi` at its
     * right; a hollow tick stands where the line started (`first`) and a dot
     * in its colour where it ended (`last`). A flat line - `lo` equal to `hi`
     * - draws its one dot in the middle and no tick. Pure arithmetic of four
     * figures the caller read off the model; `width` is its preferred width,
     * and it lays out at whatever it is given.
     */
    public static RangeBar rangeBar(double lo, double hi, double first, double last, String colour, double width) {
        return new RangeBar(lo, hi, first, last, colour, width);
    }

    /** The bar rangeBar() draws: a Pane that lays its track, its tick and its dot out at whatever width it is given. */
    public static final class RangeBar extends javafx.scene.layout.Pane {
        /** The track's height, the dot's size, the tick's width and height, and the room either end so the dot is never cut. */
        private static final double TRACK = 6, DOT = 10, TICK_W = 5, TICK_H = 14, PAD = 6;
        private final double lo, hi, first, last;
        private final Region track = new Region(), tick = new Region(), dot = new Region();
        /** Where the dot is drawn, as a share of the track: the end's own, or on its way there (slideFrom()). */
        private final javafx.beans.property.DoubleProperty at = new javafx.beans.property.SimpleDoubleProperty();

        RangeBar(double lo, double hi, double first, double last, String colour, double width) {
            this.lo = lo;
            this.hi = hi;
            this.first = first;
            this.last = last;
            track.setStyle("-fx-background-color: " + Palette.CONTROL + "; -fx-background-radius: " + TRACK / 2 + ";");
            tick.setStyle("-fx-background-color: " + Palette.RAISED + "; -fx-border-color: " + Palette.TEXT_LABEL + ";"
                    + " -fx-border-width: 1.5; -fx-background-radius: 2; -fx-border-radius: 2;");
            dot.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: " + DOT / 2 + ";"
                    + " -fx-border-color: " + Palette.RAISED + "; -fx-border-width: 1.5; -fx-border-radius: " + DOT / 2 + ";");
            tick.setVisible(!flat() && Double.isFinite(first));
            dot.setVisible(Double.isFinite(last));
            getChildren().addAll(track, tick, dot);
            at.set(share(last));
            at.addListener((o, was, is) -> requestLayout());
            setPrefSize(width, TICK_H);
            setMinSize(60, TICK_H);
            setMaxSize(Double.MAX_VALUE, TICK_H);
        }

        /** Whether the line never moved over the window: one dot, no tick. */
        public boolean flat() { return !(hi > lo); }

        /** Where a value sits on the track, 0 at the low and 1 at the high; a flat line's one value in the middle. */
        public double share(double v) {
            if (flat() || !Double.isFinite(v)) return .5;
            return Math.max(0, Math.min(1, (v - lo) / (hi - lo)));
        }

        /** The dot slides to where it ends from `was` (a share of the track) over `millis` - a month landing on the page. */
        public void slideFrom(double was, double millis) {
            if (!Double.isFinite(was) || flat()) return;
            double to = share(last);
            if (Math.abs(was - to) < 1e-4) return;
            at.set(Math.max(0, Math.min(1, was)));
            new javafx.animation.Timeline(new javafx.animation.KeyFrame(Duration.millis(millis),
                    new javafx.animation.KeyValue(at, to, javafx.animation.Interpolator.EASE_OUT))).play();
        }

        @Override protected void layoutChildren() {
            double w = getWidth(), h = getHeight(), mid = h / 2, run = Math.max(1, w - 2 * PAD);
            track.resizeRelocate(PAD - TRACK / 2, mid - TRACK / 2, run + TRACK, TRACK);
            tick.resizeRelocate(PAD + run * share(first) - TICK_W / 2, mid - TICK_H / 2, TICK_W, TICK_H);
            dot.resizeRelocate(PAD + run * at.get() - DOT / 2, mid - DOT / 2, DOT, DOT);
        }
    }

    /* =====================================================================
       THE PIECES THE FUND ADDED (0.7.39)

       The city's fund as a brokerage (the project's spec-fund-0739.md, D11)
       needed two. A SEARCH BOX: City History's "type to find a line", as a
       piece - a box that never takes the focus on a redraw, whose every key
       refills what it filters IN PLACE (a redraw would make a new box and
       lose the key after next), and whose Enter hands the keys back to the
       page. History keeps its own copy until it is next touched. A GAIN OR
       A LOSS: the one home for P&L's colour (D7) - a holding's gain or loss
       is a verdict, did this purchase make or lose the city money, so green
       for a gain and red for a loss, with a sign and an arrow always (but
       on a figure that rounds to no money at all), and the body's ink
       under BRIDGE_NOTHING; never on a price, a price's move, a yield or a
       chart line (chartCardHead()'s rule). And MONEY AT A UNIT'S EDGE: Money writes 999,600 as "$1000k"
       and 999.96 million as "$1000.0M"; tidyMoney() says them in the next
       unit's words, for these two.
       ===================================================================== */

    /** A search box (0.7.39): `text` in it, `prompt` when empty, `width` wide; `typed` on every key, `entered` on Enter. */
    public static javafx.scene.control.TextField searchBox(String text, String prompt, double width,
                                                           Consumer<String> typed, Runnable entered) {
        javafx.scene.control.TextField box = new javafx.scene.control.TextField(text == null ? "" : text);
        box.setPromptText(prompt);
        box.setMinWidth(Math.min(width, 200));
        box.setPrefWidth(width);
        box.setMaxWidth(width);
        box.setMinHeight(32);
        box.setPrefHeight(32);
        box.setStyle(Palette.words(Palette.SIZE_BODY + 1, Palette.TEXT_HEAD) + " -fx-padding: 4 10 4 10;"
                + " -fx-background-color: " + Palette.FIELD + "; -fx-background-radius: 8;"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-border-radius: 8;");
        // Never handed the focus by the window - only by a click into it (HistoryScreen.historyPickerRows()).
        box.setFocusTraversable(false);
        box.textProperty().addListener((o, was, now) -> typed.accept(now == null ? "" : now));
        box.setOnAction(e -> { if (entered != null) entered.run(); });
        return box;
    }

    /** A gain or a loss in words and its colour: "▲ +D$149.6M (+420.2%)" green, "▼ −D$5,141 (−87.0%)" red, "▲ +D$368 (+0.2%)" and "D$0" in the body's ink. */
    public record Pnl(String text, String tone) { }

    /**
     * P&L (0.7.39, the spec's D7): `amount` in the city's money with its mark
     * `sym`, signed, with an arrow, and `share` of the cost after it as a
     * percentage (NaN: none). Not known - a NaN amount - says so, in the
     * muted grey.
     */
    public static Pnl pnl(String sym, double amount, double share) {
        if (!Double.isFinite(amount)) return new Pnl("not known", Palette.TEXT_MUTED);
        String shown = tidyMoney(Money.money(Math.abs(amount)));
        boolean nothing = "$0".equals(shown);
        String figure = Money.marked(sym, shown);
        String sign = nothing ? "" : amount < 0 ? "\u2212" : "+";
        String arrow = nothing ? "" : amount > 0 ? "\u25B2 " : "\u25BC ";
        // A percentage to one place, two under a twentieth of a point, none at all under a two-hundredth: never "+0.0%".
        double p = Double.isFinite(share) ? Math.abs(share) * 100 : 0;
        String pct = !Double.isFinite(share) || nothing || p < 0.005 ? ""
                : String.format(p >= 0.05 ? " (%s%,.1f%%)" : " (%s%.2f%%)", share < 0 ? "\u2212" : "+", p);
        // The verdict's colour from BRIDGE_NOTHING up (the spec's D7); under it the body's ink, its sign and arrow kept.
        String tone = nothing || Math.abs(amount) < BRIDGE_NOTHING ? Palette.TEXT_BODY : amount > 0 ? Palette.GOOD_MONEY : Palette.BAD;
        return new Pnl(arrow + sign + figure + pct, tone);
    }

    /** Money.money()'s words at a unit's edge in the next unit's: "$1000k" is "$1.0M", "$1000.0M" "$1.0B", "$1000.0B" "$1.0T" (0.7.39). */
    public static String tidyMoney(String shown) {
        if (shown == null) return null;
        return shown.replace("$1000k", "$1.0M").replace("$1000.0M", "$1.0B").replace("$1000.0B", "$1.0T");
    }

    /** ...as a figure that is never cut, at a size. */
    public static Label pnlLabel(String sym, double amount, double share, double size) {
        Pnl p = pnl(sym, amount, share);
        Label l = new Label(p.text());
        l.setMinWidth(Region.USE_PREF_SIZE);
        l.setStyle(BuildScreen.figureAt(size, p.tone()));
        return l;
    }
}
