package ham.citybuildersim.ui;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Statement.STATEMENT;
import static ham.citybuildersim.ui.Levers.*;

/**
 * One dial, drawn the one way: a "−" worth one step, a slider that snaps to
 * the step, a "+" worth one step, the reading, and a line under them saying
 * where the ends are, what one step is and what the city is charging.
 *
 * WHY THIS IS A CLASS (0.7.6). Jerus: "all the dials in the policy in the
 * promises section, make them a slider with steps, and a + − on the ends,
 * basically i think it's better if you create an object or class of slider,
 * and then whenever you need it you just call that class and plug in the
 * specific sensitivity, max, min and steps type and all no? or is that
 * already in place?" Half of it was. PolicyScreen.taxLadder was this shape
 * for the tax pages; stageSlider was the same slider without the buttons,
 * for the wage floor, the policy rate, the promises and the fare; and the
 * inflation target, the central bank's holdings and its ceiling were chips,
 * one of them with an ad-hoc pair of buttons of its own. Three shapes of one
 * control, each with its own idea of how wide the reading was and whether
 * the ends were said. This replaced all three: every dial on the policy tab,
 * and the fare on Services, is Ladder.of(...).build().
 *
 * THE SENSITIVITY IS THE STEP, AND NOTHING ELSE. The buttons move one step,
 * the slider snaps to it, and the ends line says what it is. There is no
 * second knob - a drag coarser than a click would be two dials in one.
 *
 * STAGED, OR AT ONCE - exactly one of the two. A staged ladder hands the
 * value to its stage callback, which puts it in the policy tab's staged set
 * (or takes it out, back at the city's value) and redraws; the foot bar, or
 * the page's own apply bar, is what makes it real, as it always was. An
 * apply-at-once ladder hands the value to the model straight away - the
 * monetary page's target, holdings and ceiling, which move no price this
 * month and apply at once by design (PolicyScreen's ruleCard(), holdingsCard()
 * and ceilingCard(); THE TARGET, AS A DIAL until 0.7.36).
 * Either way the ladder knows neither the staged set nor the model: it hands
 * a snapped value inside the ends to the callback it was given, and the
 * callback redraws the screen.
 *
 * THE READING TRACKS THE THUMB, THE CALLBACK WAITS FOR THE RELEASE.
 * Restaging on every pixel of a drag would redraw the whole screen under the
 * pointer, which is both slow and how you lose the thing you were dragging.
 * Since 0.7.36 a caller can follow the thumb too (onTrack()): the Policy
 * tab's dial cards redraw their own "before -> after" rows on it, and the
 * page waits for the release as before.
 *
 * AND THREE THINGS THE POLICY TAB ASKED FOR (0.7.36): named marks on the
 * track (marks() - the three income rates once they have parted, the rule's
 * rate, the clinic's break-even); words the reading says before the dial is
 * moved (idle() - "three rates", where a figure would be one of three and
 * read as the whole: the Policy spec's B1); and the onTrack() above.
 */
public final class Ladder {

    /** How wide the reading is held, so the slider does not shift as the figure's width does. */
    static final double READING = 118;

    /** How wide and tall each step button is: square, and room for the glyph. */
    static final double BUTTON = 28;

    /** The gap between the buttons, the slider and the reading. */
    static final double GAP = 10;

    private final double min, max, step;
    private final DoubleFunction<String> reads;
    private DoubleFunction<String> stepReads;
    private double current;
    private double shown = Double.NaN;
    private DoubleConsumer stage, apply;
    private boolean offAtCurrent;
    private String itIs;
    private boolean greyed;
    private double wide = STATEMENT;
    private double[] markAt = new double[0];
    private String[] markNames = new String[0];
    private DoubleConsumer track;
    private String idle;

    private Ladder(double min, double max, double step, DoubleFunction<String> reads) {
        this.min = min;
        this.max = Math.max(min, max);
        this.step = step;
        this.reads = reads;
        this.stepReads = reads;
        this.current = min;
    }

    /**
     * A dial from min to max in steps of step, read by reads - the same
     * formatter for the reading, the ends and, unless stepReads() says
     * otherwise, the step.
     */
    public static Ladder of(double min, double max, double step, DoubleFunction<String> reads) {
        return new Ladder(min, max, step, reads);
    }

    /** What the city is charging now: the reading is in accent when the thumb is off it. */
    public Ladder current(double value) { current = value; return this; }

    /** Where the thumb sits when that is not the current value - a staged one. */
    public Ladder showing(double value) { shown = value; return this; }

    /** Staged: every move hands the value to this, which stages it (or unstages it) and redraws. */
    public Ladder stages(DoubleConsumer to) { stage = to; return this; }

    /** At once: a move off the current value hands it to this, which sets it and redraws. */
    public Ladder appliesAtOnce(DoubleConsumer to) { apply = to; return this; }

    /** How one step reads on the ends line, when the reading's own formatter would say it wrongly. */
    public Ladder stepReads(DoubleFunction<String> words) { stepReads = words; return this; }

    /**
     * Marks the dial as moved even at its current value - "Every tax at once"
     * once the three income bases have parted, "Every school at once" once
     * the nine prices have: its current is one of several, and setting them
     * all to it still changes something.
     */
    public Ladder offAtCurrent(boolean yes) { offAtCurrent = yes; return this; }

    /** What "it is" says on the ends line once the dial has moved; the current value by default. */
    public Ladder itIs(String words) { itIs = words; return this; }

    /** Drawn but not moveable, and dimmed: a dial with nothing under it, such as a kind of school the city has not built. */
    public Ladder greyed(boolean yes) { greyed = yes; return this; }

    /** The whole ladder's width; the slider takes what the buttons and the reading leave. STATEMENT by default. */
    public Ladder wide(double width) { wide = width; return this; }

    /** Named marks on the track, in the dial's units (0.7.36): drawn under the slider where each value sits, a mark outside the ends left out. */
    public Ladder marks(double[] at, String[] names) {
        markAt = at == null ? new double[0] : at.clone();
        markNames = names == null ? new String[markAt.length] : names.clone();
        return this;
    }

    /** Hands every value the thumb passes through to this while it is dragged or stepped (0.7.36), snapped and inside the ends - before the release that stages it. */
    public Ladder onTrack(DoubleConsumer to) { track = to; return this; }

    /** What the reading says, in grey, while the thumb sits on the current value and nothing is staged (0.7.36): "three rates" for a dial whose current is one of several. */
    public Ladder idle(String words) { idle = words; return this; }

    /** The dial: the row of −, slider, + and reading over the ends line. */
    public VBox build() {

        if ((stage == null) == (apply == null)) {
            throw new IllegalStateException("a ladder either stages or applies at once - exactly one callback");
        }
        double at = bounded(Double.isNaN(shown) ? current : shown);
        boolean off = moved(at, current, step) || offAtCurrent;

        Slider bar = new Slider(min, max, at);
        bar.setBlockIncrement(step);
        bar.setMajorTickUnit(Math.max(step, 1e-9));
        bar.setMinorTickCount(0);
        bar.setSnapToTicks(true);
        double slider = Math.max(60, wide - READING - 2 * BUTTON - 3 * GAP);
        bar.setPrefWidth(slider);
        bar.setMaxWidth(slider);
        bar.setDisable(greyed);

        Label reading = new Label(reads.apply(at));
        reading.setPrefWidth(READING);
        reading.setMinWidth(READING);
        reading.setAlignment(Pos.CENTER_RIGHT);
        reading.setWrapText(true);
        reading.setStyle(Palette.figure(Palette.SIZE_BODY, tone(off)));
        if (!off && idle != null) {
            reading.setText(idle);
            reading.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_MUTED));
        }

        bar.valueProperty().addListener((o, was, now) -> {
            double v = bounded(snapped(now.doubleValue(), min, step));
            boolean away = moved(v, current, step);
            if (!away && idle != null) {
                reading.setText(idle);
                reading.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_MUTED));
            } else {
                reading.setText(reads.apply(v));
                reading.setStyle(Palette.figure(Palette.SIZE_BODY, tone(away)));
            }
            if (track != null) track.accept(v);
        });
        Runnable commit = () -> hand(bar.getValue());
        bar.setOnMouseReleased(e -> commit.run());
        bar.setOnKeyReleased(e -> commit.run());

        HBox row = new HBox(GAP,
                stepButton("−", !greyed && at - step >= min - 1e-12, () -> hand(at - step)),
                bar,
                stepButton("+", !greyed && at + step <= max + 1e-12, () -> hand(at + step)),
                reading);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(wide);

        Label ends = new Label(reads.apply(min) + " to " + reads.apply(max)
                + "   ·   one step is " + stepReads.apply(step)
                + (off ? "   ·   it is " + (itIs != null ? itIs : reads.apply(current)) : "")
                + (apply != null ? "   ·   set at once" : ""));
        ends.setStyle(Palette.words(Palette.SIZE_CAPTION,
                greyed ? Palette.TEXT_SPENT : off ? Palette.ACCENT : Palette.TEXT_SPENT));

        VBox box = new VBox(1, row);
        if (markAt.length > 0) {
            Marks under = new Marks(min, this.max, markAt, markNames, slider);
            HBox lane = new HBox(under);
            lane.setPadding(new javafx.geometry.Insets(0, 0, 0, BUTTON + GAP));
            lane.setMaxWidth(wide);
            box.getChildren().add(lane);
        }
        box.getChildren().add(ends);
        box.setMaxWidth(wide);
        box.setStyle("-fx-padding: 6 0 10 0;");
        if (greyed) box.setOpacity(.55);
        return box;
    }

    /**
     * The marks under the slider (0.7.36): a short tick where each value sits
     * on the track - the thumb's travel, inset by half the thumb at either
     * end - and its name under it, a name that would touch the one before
     * dropped a line, to the third at most. As wide as the slider.
     */
    static final class Marks extends javafx.scene.layout.Pane {
        /** Half the slider's thumb: the track's travel starts and ends this far in. */
        static final double INSET = 8;
        private final double min, max;
        private final double[] at;
        private final java.util.List<javafx.scene.layout.Region> ticks = new java.util.ArrayList<>();
        private final java.util.List<Label> names = new java.util.ArrayList<>();

        Marks(double min, double max, double[] at, String[] names, double width) {
            this.min = min;
            this.max = max;
            this.at = at;
            for (int i = 0; i < at.length; i++) {
                javafx.scene.layout.Region t = new javafx.scene.layout.Region();
                t.setStyle("-fx-background-color: " + Palette.TEXT_LABEL + ";");
                ticks.add(t);
                Label n = new Label(i < names.length && names[i] != null ? names[i] : "");
                n.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
                this.names.add(n);
                getChildren().addAll(t, n);
            }
            setMinWidth(width);
            setPrefWidth(width);
            setMaxWidth(width);
        }

        private double x(double v, double w) {
            double span = max - min;
            return INSET + (span > 0 ? (v - min) / span : 0) * Math.max(0, w - 2 * INSET);
        }

        /** Where each name sits, {left, line}, at a width; a name that would touch the last on its line goes one line down. */
        private double[][] placed(double w) {
            double[][] out = new double[at.length][2];
            double[] lineEnd = {-1e9, -1e9, -1e9};
            for (int i = 0; i < at.length; i++) {
                Label n = names.get(i);
                double nw = n.prefWidth(-1);
                double left = Math.max(0, Math.min(w - nw, x(at[i], w) - nw / 2));
                int line = 0;
                while (line < lineEnd.length - 1 && left < lineEnd[line] + 6) line++;
                lineEnd[line] = left + nw;
                out[i][0] = left;
                out[i][1] = line;
            }
            return out;
        }

        private int lines(double w) {
            int most = 0;
            for (double[] p : placed(w)) most = Math.max(most, (int) p[1] + 1);
            return most;
        }

        @Override protected double computePrefHeight(double width) {
            double w = width > 0 ? width : getPrefWidth();
            return 6 + lines(w) * 12;
        }

        @Override protected double computeMinHeight(double width) { return computePrefHeight(width); }

        @Override protected void layoutChildren() {
            double w = getWidth();
            double[][] p = placed(w);
            for (int i = 0; i < at.length; i++) {
                boolean on = at[i] >= min - 1e-12 && at[i] <= max + 1e-12;
                ticks.get(i).setVisible(on);
                names.get(i).setVisible(on);
                if (!on) continue;
                ticks.get(i).resizeRelocate(Math.round(x(at[i], w)) - 1, 0, 2, 5);
                Label n = names.get(i);
                n.resizeRelocate(p[i][0], 5 + p[i][1] * 12, n.prefWidth(-1), n.prefHeight(-1));
            }
        }
    }

    /** The reading's colour: accent off the city's value, dimmed when greyed. */
    private String tone(boolean off) {
        return greyed ? Palette.TEXT_SPENT : off ? Palette.ACCENT : Palette.TEXT_HEAD;
    }

    /** A value inside the ends. */
    private double bounded(double value) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * A move, snapped and held inside the ends, handed to the one callback:
     * every move to a staged dial (back at the city's value it unstages), a
     * move off the current value to an at-once one.
     */
    private void hand(double value) {
        if (greyed) return;
        double v = bounded(snapped(value, min, step));
        if (stage != null) stage.accept(v);
        else if (moved(v, current, step)) apply.accept(v);
    }

    /**
     * One notch, in the units the dial is read in.
     *
     * PADDING ZERO INLINE. The theme's .button rule carries 6 14 6 14, and on a
     * button pinned to 28px that leaves the glyph a content box narrower than
     * nothing - JavaFX draws an ellipsis. setPadding(EMPTY) does not fix it;
     * only an inline style beats a stylesheet. Third time in the interface,
     * after the build card's buttons and the shell's round ones.
     */
    static Button stepButton(String glyph, boolean live, Runnable go) {
        Button button = new Button(glyph);
        button.setMinSize(BUTTON, BUTTON);
        button.setPrefSize(BUTTON, BUTTON);
        button.setMaxSize(BUTTON, BUTTON);
        button.setDisable(!live);
        button.setStyle("-fx-padding: 0; -fx-font-size: 14px; -fx-font-weight: bold;"
                + " -fx-background-radius: 4; -fx-background-insets: 0;"
                + " -fx-background-color: " + (live ? Palette.CONTROL : Palette.PANEL) + ";"
                + " -fx-text-fill: " + (live ? Palette.TEXT_HEAD : Palette.TEXT_SPENT) + ";"
                + (live ? " -fx-cursor: hand;" : ""));
        button.setOnAction(e -> go.run());
        return button;
    }
}
