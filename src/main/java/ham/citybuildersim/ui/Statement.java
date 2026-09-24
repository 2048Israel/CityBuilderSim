package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * The rows a statement is built from: a head, a line, a note, a total, a
 * disclosure that opens, and the two-column book the sector pages and the
 * bank's income statement use - its lines able to stay open through the
 * clock's redraw since 0.7.9 (opens()).
 *
 * Static for the same reason as Money: they hold no state and every screen
 * uses them, so they belong to no screen. Called unqualified through an
 * import static, exactly as they were when they were methods of the window.
 */
public final class Statement {

    /* =====================================================================
       A STATEMENT WITH TWO COLUMNS.

       Everything below composes these three, so every statement on this tab
       lines up with every other one whatever sector it belongs to.
       ===================================================================== */

    /** How wide a statement is. Wide enough for a sentence, narrow enough to read. */
    public static final double STATEMENT = 560;

    /** A statement's heading, with a rule under it. */
    public static VBox statementHead(String title) { return statementHead(title, STATEMENT); }

    /** @param width for the one screen wider than a statement - the graph. */
    public static VBox statementHead(String title, double width) {

        Label head = new Label(title.toUpperCase());
        head.setStyle(Palette.words(Palette.SIZE_HEADING, Palette.ACCENT)
                + " -fx-font-weight: bold; -fx-padding: 0 0 4 0;");

        Region rule = new Region();
        rule.setMinHeight(1);
        rule.setPrefHeight(1);
        rule.setMaxHeight(1);
        rule.setStyle("-fx-background-color: " + Palette.EDGE + ";");

        VBox box = new VBox(0, head, rule);
        box.setMaxWidth(width);
        box.setPrefWidth(width);
        box.setStyle("-fx-padding: 18 0 6 0;");
        return box;
    }

    public static HBox statementLine(String label, String value) {
        return statementLine(label, value, null);
    }

    /**
     * @param tone a colour for the FIGURE when it is saying something. The
     *             label stays grey whatever happens, because the label is never
     *             the news.
     */
    public static HBox statementLine(String label, String value, String tone) {

        Label what = new Label(label);
        what.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_LABEL));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label reads = new Label(value);
        reads.setStyle(Palette.figure(Palette.SIZE_BODY,
                tone == null ? Palette.TEXT_BODY : tone));

        HBox row = new HBox(Palette.GAP_LOOSE, what, gap, reads);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);
        row.setPrefWidth(STATEMENT);
        row.setStyle("-fx-padding: 3 0 3 0;");
        return row;
    }

    /** The sentence under a line - what it means, or where it comes from. */
    public static Label statementNote(String text) {
        Label note = new Label(text);
        note.setWrapText(true);
        note.setMaxWidth(STATEMENT - 16);
        note.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)
                + " -fx-padding: 0 0 6 14;");
        return note;
    }

    /** The line a statement adds up to: a rule, then the figure in full. */
    public static VBox statementTotal(String label, String value, String tone) {

        Region rule = new Region();
        rule.setMinHeight(1);
        rule.setPrefHeight(1);
        rule.setMaxHeight(1);
        rule.setMaxWidth(STATEMENT);
        rule.setPrefWidth(STATEMENT);
        rule.setStyle("-fx-background-color: " + Palette.HAIRLINE + ";");

        Label what = new Label(label);
        what.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY)
                + " -fx-font-weight: bold;");

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label reads = new Label(value);
        reads.setStyle(Palette.figure(Palette.SIZE_SECTION,
                tone == null ? Palette.TEXT_HEAD : tone));

        HBox row = new HBox(Palette.GAP_LOOSE, what, gap, reads);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);
        row.setPrefWidth(STATEMENT);
        row.setStyle("-fx-padding: 5 0 3 0;");

        VBox box = new VBox(0, rule, row);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 4 0 0 0;");
        return box;
    }

    public static final double BOOK_NOW = 116;

    public static final double BOOK_THEN = 104;

    /** The column headings, once, at the top of a statement. */
    public static HBox bookHead(String left) {
        Label what = new Label(left);
        what.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label a = new Label("this month");
        a.setPrefWidth(BOOK_NOW);
        a.setMinWidth(BOOK_NOW);
        a.setAlignment(Pos.CENTER_RIGHT);
        a.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        Label b = new Label("last month");
        b.setPrefWidth(BOOK_THEN);
        b.setMinWidth(BOOK_THEN);
        b.setAlignment(Pos.CENTER_RIGHT);
        b.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        HBox row = new HBox(Palette.GAP, what, gap, a, b);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);
        row.setPrefWidth(STATEMENT);
        row.setStyle("-fx-padding: 4 0 2 0;");
        return row;
    }

    /**
     * One line of a set of books: what it is, this month, and last month.
     *
     * LAST MONTH IS QUIETER, on purpose. It is context rather than news, and a
     * comparative column at full strength turns every statement into two
     * statements competing for the same eye.
     */
    public static HBox bookLine(String label, double now, double then, boolean known, String tone) {

        Label what = new Label(label);
        what.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_LABEL));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label a = new Label(tightMoney(toDollars(now), false));
        a.setPrefWidth(BOOK_NOW);
        a.setMinWidth(BOOK_NOW);
        a.setAlignment(Pos.CENTER_RIGHT);
        a.setStyle(Palette.figure(Palette.SIZE_BODY,
                tone == null ? Palette.TEXT_BODY : tone));

        Label b = new Label(known ? tightMoney(toDollars(then), false) : "—");
        b.setPrefWidth(BOOK_THEN);
        b.setMinWidth(BOOK_THEN);
        b.setAlignment(Pos.CENTER_RIGHT);
        b.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_SPENT));

        HBox row = new HBox(Palette.GAP, what, gap, a, b);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);
        row.setPrefWidth(STATEMENT);
        row.setStyle("-fx-padding: 3 0 3 0;");
        return row;
    }

    /** A statement line that opens into its parts. `word` is the one-word hint on the mark. */
    public static VBox bookLine(String label, double now, double then, boolean known, String tone,
                          VBox detail, String word) {
        return bookLine(label, now, then, known, tone, detail, word, null);
    }

    /**
     * ...and remembers whether it was open: see opens(). `open` is the
     * screen's set of the lines it has open, by label; null remembers nothing.
     */
    public static VBox bookLine(String label, double now, double then, boolean known, String tone,
                          VBox detail, String word, java.util.Set<String> open) {

        HBox row = bookLine(label, now, then, known, tone);
        VBox box = new VBox(0, row);
        box.setMaxWidth(STATEMENT);
        if (detail == null || detail.getChildren().isEmpty()) return box;

        /*
         * THE MARK GOES NEXT TO THE LABEL, NOT AT THE END OF THE ROW. The two
         * money columns are right-aligned to fixed widths so that every figure
         * on the statement lines up; a caret after them would push the whole
         * line out of that grid, and a statement whose columns do not line up
         * is not a statement. Same reason budgetLine() puts its mark where it
         * does.
         */
        Label mark = new Label(CLOSED + " " + word);
        mark.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.ACCENT));
        row.getChildren().add(1, mark);
        row.setStyle("-fx-padding: 3 0 3 0; -fx-cursor: hand;");

        detail.setStyle("-fx-padding: 1 0 6 16;");
        opens(row, mark, word, detail, label, open);
        box.getChildren().add(detail);
        return box;
    }

    /**
     * A row that opens its detail - DRAWN AS THE SCREEN LAST LEFT IT (0.7.9).
     *
     * Every screen is rebuilt on the clock, so a line that was drawn closed
     * every time snapped shut each month while the player was reading what
     * it had opened into. A screen that minds keeps a set of the lines it
     * has open, by key, and hands it in: a line in the set is drawn open, and
     * opening or closing one updates the set. Null is the old behaviour -
     * drawn closed, remembered nowhere.
     */
    static void opens(javafx.scene.Node row, Label mark, String word, VBox detail,
                      String key, java.util.Set<String> open) {
        boolean shown = open != null && open.contains(key);
        detail.setVisible(shown);
        detail.setManaged(shown);
        mark.setText((shown ? OPENED : CLOSED) + " " + word);
        row.setOnMouseClicked(e -> {
            boolean now = !detail.isVisible();
            detail.setVisible(now);
            detail.setManaged(now);
            mark.setText((now ? OPENED : CLOSED) + " " + word);
            if (open != null) {
                if (now) open.add(key);
                else open.remove(key);
            }
        });
    }

    /**
     * The line a section adds up to: a rule, then this month's figure at full
     * weight and last month's quieter beside it. The sector books' first; in
     * the toolkit since the bank's income statement wanted it too (0.7.9).
     */
    public static VBox bookTotal(String label, double now, double then, boolean known, String tone) {

        Region rule = new Region();
        rule.setMinHeight(1);
        rule.setPrefHeight(1);
        rule.setMaxHeight(1);
        rule.setMaxWidth(STATEMENT);
        rule.setPrefWidth(STATEMENT);
        rule.setStyle("-fx-background-color: " + Palette.HAIRLINE + ";");

        Label what = new Label(label);
        what.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY)
                + " -fx-font-weight: bold;");

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label a = new Label(tightMoney(toDollars(now), false));
        a.setPrefWidth(BOOK_NOW);
        a.setMinWidth(BOOK_NOW);
        a.setAlignment(Pos.CENTER_RIGHT);
        a.setStyle(Palette.figure(Palette.SIZE_LEAD,
                tone == null ? Palette.TEXT_HEAD : tone));

        Label b = new Label(known ? tightMoney(toDollars(then), false) : "—");
        b.setPrefWidth(BOOK_THEN);
        b.setMinWidth(BOOK_THEN);
        b.setAlignment(Pos.CENTER_RIGHT);
        b.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_SPENT));

        HBox row = new HBox(Palette.GAP, what, gap, a, b);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);
        row.setPrefWidth(STATEMENT);
        row.setStyle("-fx-padding: 4 0 6 0;");

        VBox box = new VBox(0, rule, row);
        box.setMaxWidth(STATEMENT);
        return box;
    }

    /**
     * A statement line that opens something underneath it.
     *
     * disclosure() takes a preformatted Courier string, which is right for the
     * report screens it was written for and wrong next to these: the label and
     * the figure landed at whatever column %-30s put them in, an inch left of
     * every other row on the screen. This is statementLine's shape with the
     * marker on the end, so an expandable row sits in the same two columns as
     * the rest of the statement.
     */
    public static VBox statementDisclosure(String label, String value, VBox detail) {
        return statementDisclosure(label, value, detail, "who?");
    }

    /** As above, naming what opening it shows. */
    public static VBox statementDisclosure(String label, String value, VBox detail, String hint) {
        return statementDisclosure(label, value, detail, hint, null);
    }

    /** ...and remembering whether it was open: see opens(). */
    public static VBox statementDisclosure(String label, String value, VBox detail, String hint,
                                           java.util.Set<String> open) {

        Label what = new Label(label);
        what.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_LABEL));

        Label mark = new Label(CLOSED + " " + hint);
        mark.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.ACCENT));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label reads = new Label(value);
        reads.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.TEXT_BODY));

        HBox row = new HBox(Palette.GAP, what, mark, gap, reads);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);
        row.setPrefWidth(STATEMENT);
        row.setStyle("-fx-padding: 3 0 3 0; -fx-cursor: hand;");
        opens(row, mark, hint, detail, label, open);

        return new VBox(0, row, detail);
    }

    /** A statement line: label left, figure right, in one fixed-width column. */
    public static Label bookLine(String label, double value, boolean bold, String colour) {
        Label line = monoLabel(String.format("%-28s%12s", label, tightMoney(value)));
        line.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;"
                + (bold ? " -fx-font-weight: bold;" : "")
                + (colour == null ? "" : " -fx-text-fill: " + colour + ";"));
        return line;
    }

    public static Label bookRule() {
        Label line = monoLabel(String.format("%-28s%12s", "", "------------"));
        line.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;"
                + " -fx-text-fill: #55636d;");
        return line;
    }

    /** A short grey line under a figure, for the one sentence it needs. */
    public static Label bookNote(String text) {
        Label line = monoLabel("  " + text);
        line.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;"
                + " -fx-text-fill: #7b8f9c;");
        return line;
    }

    /**
     * A line you can click to open the working behind it.
     *
     * Jerus asked for this on the inflow: the screen said 34 people moved in and
     * would not say who they were, which is the one thing that decides whether
     * the city can staff itself. It is a disclosure rather than a permanent
     * block because the answer is four rows most months and the question is not
     * asked most months - a report that shows everything shows nothing.
     *
     * Toggles visible AND managed together. Visible alone leaves the space
     * behind, so a closed section would sit there as a hole in the column.
     */
    public static VBox disclosure(String line, String hint, VBox detail) {
        return disclosure(line, hint, detail,
                "-fx-font-family: 'Courier New'; -fx-font-size: 11px;"
                + " -fx-text-fill: #8ed4ff;");
    }

    /** As above, keeping a row's own styling - used by the tier table. */
    public static VBox disclosure(String line, String hint, VBox detail, String style) {
        detail.setVisible(false);
        detail.setManaged(false);

        Label head = monoLabel(line + "   " + CLOSED + " " + hint);
        head.setStyle(style + " -fx-cursor: hand;");
        head.setOnMouseClicked(e -> {
            boolean open = !detail.isVisible();
            detail.setVisible(open);
            detail.setManaged(open);
            head.setText(line + "   " + (open ? OPENED : CLOSED) + " " + hint);
        });

        VBox box = new VBox(1, head, detail);
        return box;
    }

    public static final String CLOSED = "\u25b8";

    public static final String OPENED = "\u25be";

    public static VBox reportSection(String heading, String... rows) {
        VBox box = new VBox(3);
        // CENTER_LEFT, not CENTER: each row used to be centred individually, which
        // threw away the %-24s padding and left the value column ragged. Left-
        // aligning inside a centred fixed-width column makes the padding line up.
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-padding: 14 0 0 0;");

        /*
         * ...and hug the content, so the box can be centred by whatever holds it.
         *
         * A VBox child stretches to its parent's full width by default. Combined
         * with CENTER_LEFT above, that put every row hard against the left edge
         * of whatever pane the section landed in - invisible inside the report
         * columns, which are pref-sized already, and glaring on the construction
         * shedding banner, which is added straight to the centred root menu. It
         * read as detached from the menu because it was the only thing on the
         * screen not lining up with it.
         *
         * Fixed here rather than at the banner so any other section added
         * directly to a pane gets it too. Sections inside a pref-sized column are
         * unaffected: they were already only as wide as their widest row.
         */
        box.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);

        Label headingLabel = new Label(heading);
        headingLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold;");
        box.getChildren().add(headingLabel);

        for (String row : rows) {
            box.getChildren().add(monoLabel(row));
        }

        return box;
    }
}
