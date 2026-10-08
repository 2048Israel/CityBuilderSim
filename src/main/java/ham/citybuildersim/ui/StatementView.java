package ham.citybuildersim.ui;

import ham.citybuildersim.SectorStatements;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Pieces.*;

/**
 * A formal statement on the page (0.7.74): SectorStatements' rows set as an
 * accountant sets them - a title block, the columns (note, this month, last
 * month, change, common size), negatives in parentheses, one rule over a
 * subtotal and two under a bottom line, a note number that opens its note in
 * place - and the Summary | Statement switch the sector pages and the bank's
 * two share, and the IN SHORT card every summary carries.
 *
 * WHY. Jerus asked for "both a summarized and a detailed actual statement"
 * (the project's spec-sector-statements.md). The pages' statement column was
 * Statement's two-column book, plain words with minus signs; that stays, as
 * what a Summary draws where it draws a statement at all, and this is the
 * Statement view's: one shape for every sector and the bank, so a statement
 * on one page lines up with every other. The figures are the model's
 * thousands as they are (D2: in millions past seven digits), never
 * abbreviated - a statement whose column reads 13.6M over 940k does not
 * line up.
 */
final class StatementView {

    private StatementView() { }

    /** The switch's two words: the picture, or the statement (D1: one choice for every page, kept for the session). */
    static final String SUMMARY = "Summary", STATEMENT = "Statement";

    /** A formal statement's width: its five columns and a label of some fifty characters at the body size. */
    static final double TABLE = 760;

    /** The note column. */
    static final double NOTE_COLUMN = 28;

    /** This month's column and last month's: "(9,999,999)" at the body size, and room. */
    static final double FIGURE_COLUMN = 100;

    /** The change column. */
    static final double CHANGE_COLUMN = 96;

    /** The common-size column: "(100.0%)". */
    static final double SHARE_COLUMN = 64;

    /** How far a line sits in under its head. */
    static final double INDENT = 12;

    /** A share of the base past this many times it reads "n/m", not meaningful: an outside line against a month with almost no revenue. */
    static final double SHARE_MOST = 10;

    /** The gap between columns. */
    static final double GAP = Palette.GAP;

    /** What the columns show: last month's (off for a statement of the month's movement alone), and the change and the common-size share, each of which the toolbar turns off; `shareWord` heads the share's ("of revenue"), null for a statement with none. */
    record Columns(boolean then, boolean change, boolean share, String shareWord) { }

    /* --------------------------------------------------------------- the switch */

    /**
     * The switch, at the right of a page strip (board SectorFrame): two
     * segments, the one shown lit. A pick redraws through `pick`.
     */
    static HBox viewSwitch(boolean statement, Consumer<Boolean> pick) {
        HBox box = new HBox(0, segment(SUMMARY, !statement, true, () -> pick.accept(false)),
                segment(STATEMENT, statement, false, () -> pick.accept(true)));
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setMinWidth(Region.USE_PREF_SIZE);
        return box;
    }

    private static Button segment(String word, boolean on, boolean left, Runnable go) {
        Button b = new Button(word);
        String radius = left ? Palette.RADIUS_TIGHT + " 0 0 " + Palette.RADIUS_TIGHT : "0 " + Palette.RADIUS_TIGHT + " " + Palette.RADIUS_TIGHT + " 0";
        b.setStyle(Palette.words(Palette.SIZE_LABEL, on ? Palette.TEXT_HEAD : Palette.TEXT_LABEL)
                + " -fx-background-color: " + (on ? Palette.RAISED : Palette.CONTROL) + ";"
                + " -fx-background-radius: " + radius + "; -fx-border-radius: " + radius + ";"
                + " -fx-border-color: " + (on ? Palette.ACCENT : Palette.CONTROL_EDGE) + "; -fx-border-width: 1;"
                + " -fx-padding: 3 12 3 12; -fx-cursor: hand;");
        b.setMinWidth(Region.USE_PREF_SIZE);
        b.setOnAction(e -> { if (!on) go.run(); });
        return b;
    }

    /** A page strip with the switch at its right end; `statement` null leaves the switch off (Operations, a bank page without one). */
    static HBox stripWithSwitch(Node strip, Boolean statement, Consumer<Boolean> pick) {
        HBox row = new HBox(Palette.GAP_LOOSE, strip);
        HBox.setHgrow(strip, Priority.ALWAYS);
        if (statement != null) row.getChildren().add(viewSwitch(statement, pick));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /* --------------------------------------------------------------- figures */

    /** A figure as the statement prints it: thousands (or millions, D2) grouped, negatives in parentheses (D3), nothing a dash, not known an em dash. */
    static String figure(double thousands, boolean millions) {
        if (Double.isNaN(thousands)) return "—";
        if (Double.isInfinite(thousands)) return "n/m";
        double v = millions ? thousands / 1000 : thousands;
        if (Math.abs(v) < (millions ? .05 : .5)) return "–";
        String s = String.format(millions ? "%,.1f" : "%,.0f", Math.abs(v));
        return v < 0 ? "(" + s + ")" : s;
    }

    /** A part of a base as the common-size column prints it: a tenth of a per cent, in parentheses below nothing; blank on no base, "n/m" past SHARE_MOST. */
    static String share(double part, double base) {
        if (!Double.isFinite(part) || !Double.isFinite(base) || base == 0) return "";
        double r = part / base;
        if (Math.abs(r) >= SHARE_MOST) return "n/m";
        if (Math.abs(r) < .0005) return "–";
        String s = String.format("%.1f%%", Math.abs(r) * 100);
        return r < 0 ? "(" + s + ")" : s;
    }

    /** The units line: "in $ thousands", or millions past seven digits (D2). */
    static String units(boolean millions) {
        return millions ? "in $ millions" : "in $ thousands";
    }

    /* --------------------------------------------------------------- the title block */

    /** The title block: the company, the statement's name, the period and the units. */
    static VBox titleBlock(String company, String title, String period, boolean millions) {
        return titleBlock(company, title, period + " · " + units(millions));
    }

    /** ...for a report whose figures are not all money in one unit (the investor report): the period alone. */
    static VBox titleBlock(String company, String title, String period) {
        Label who = new Label(company);
        who.setStyle(Palette.strong(Palette.SIZE_SECTION, Palette.TEXT_HEAD));
        Label what = new Label(title);
        what.setStyle(Palette.strong(Palette.SIZE_BODY, Palette.TEXT_BODY));
        Label when = new Label(period);
        when.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        VBox box = new VBox(1, who, what, when);
        box.setStyle("-fx-padding: 0 0 6 0;");
        return box;
    }

    /* --------------------------------------------------------------- the toolbar */

    /**
     * The toolbar over a statement: the change column and the common-size
     * column, each on or off, and every note opened or closed at once. A
     * null action leaves its control off.
     */
    static HBox toolbar(Columns c, Runnable change, Runnable share, Runnable openAll, Runnable closeAll) {
        HBox bar = new HBox(6);
        bar.setAlignment(Pos.CENTER_LEFT);
        if (change != null) bar.getChildren().add(toggle("change", c.change(), change));
        if (share != null && c.shareWord() != null) bar.getChildren().add(toggle("% " + c.shareWord(), c.share(), share));
        if (openAll != null) bar.getChildren().add(action("open every note", openAll));
        if (closeAll != null) bar.getChildren().add(action("close them", closeAll));
        return bar;
    }

    private static Button toggle(String word, boolean on, Runnable go) {
        Button b = new Button((on ? "✓ " : "") + word);
        b.setStyle(Palette.words(Palette.SIZE_CAPTION, on ? Palette.TEXT_HEAD : Palette.TEXT_LABEL)
                + " -fx-background-color: " + (on ? Palette.RAISED : Palette.CONTROL) + ";"
                + " -fx-background-radius: " + Palette.RADIUS_TIGHT + "; -fx-padding: 2 8 2 8; -fx-cursor: hand;"
                + " -fx-border-color: " + (on ? Palette.ACCENT : "transparent") + "; -fx-border-width: 0 0 1 0;");
        b.setMinWidth(Region.USE_PREF_SIZE);
        b.setOnAction(e -> go.run());
        return b;
    }

    private static Button action(String word, Runnable go) {
        Button b = new Button(word);
        b.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.ACCENT) + " -fx-background-color: transparent;"
                + " -fx-padding: 2 6 2 6; -fx-cursor: hand;");
        b.setMinWidth(Region.USE_PREF_SIZE);
        b.setOnAction(e -> go.run());
        return b;
    }

    /* --------------------------------------------------------------- the table */

    /** A note's key in the screen's open set. */
    static String noteKey(String table, int note) { return table + ":note:" + note; }

    /** Every note a statement has, opened (the toolbar's "open every note"). */
    static void openAll(SectorStatements.Table t, String table, Set<String> open, IntFunction<Node> notes) {
        for (SectorStatements.Row r : t.rows()) {
            if (r.note() <= 0 || !r.shown()) continue;
            Node detail = notes.apply(r.note());
            if (detail == null || (detail instanceof javafx.scene.layout.Pane p && p.getChildren().isEmpty())) continue;
            open.add(noteKey(table, r.note()));
        }
    }

    /** ...and closed. */
    static void closeAll(String table, Set<String> open) {
        open.removeIf(k -> k.startsWith(table + ":note:"));
    }

    /**
     * The statement itself: its column heads, then each row it shows.
     *
     * @param table    its key in the screen's open set ("income", "bank:sheet")
     * @param nowWord  this month's column head
     * @param thenWord last month's
     * @param base     what the common-size column is a share of, this month (revenue, total assets)
     * @param baseThen ...and last month
     * @param open     the screen's open set: a note in it is drawn open
     * @param notes    what a note number opens into, or null for a note with nothing to open
     * @param info     an (i) for a row, by id, or null
     * @param redraw   what a note's click does after it opens or closes
     */
    static VBox table(SectorStatements.Table t, String table, String nowWord, String thenWord, Columns c,
                      double base, double baseThen, Set<String> open, IntFunction<Node> notes,
                      Function<String, String> info, Runnable redraw) {
        boolean millions = SectorStatements.inMillions(t);
        VBox box = new VBox(0);
        box.setMinWidth(TABLE);
        box.setPrefWidth(TABLE);
        box.setMaxWidth(TABLE);
        box.getChildren().add(heads(nowWord, thenWord, c));
        for (SectorStatements.Row r : t.rows()) {
            if (!r.shown() || nil(r, t, c, millions)) continue;
            Node detail = r.note() > 0 && notes != null ? notes.apply(r.note()) : null;
            if (detail instanceof javafx.scene.layout.Pane p && p.getChildren().isEmpty()) detail = null;
            boolean opened = detail != null && open.contains(noteKey(table, r.note()));
            box.getChildren().add(row(r, t.thenKnown(), millions, c, base, baseThen, detail != null, opened,
                    () -> {
                        String k = noteKey(table, r.note());
                        if (!open.remove(k)) open.add(k);
                        redraw.run();
                    }, info == null ? null : info.apply(r.id())));
            if (opened) {
                VBox.setMargin(detail, new javafx.geometry.Insets(2, 0, 8, NOTE_COLUMN + GAP + INDENT));
                box.getChildren().add(detail);
            }
        }
        return box;
    }

    /**
     * Whether a line prints as nothing in every column it has: under half the
     * statement's unit both months (a few hundred dollars, in thousands), or
     * not known. An accountant leaves a nil line off; the Summary still shows
     * it from a cent (SectorStatements.Row.shown(), today's rule). A residual
     * is never left off: NOT ACCOUNTED FOR shows from a dollar, whatever it
     * prints.
     */
    static boolean nil(SectorStatements.Row r, SectorStatements.Table t, Columns c, boolean millions) {
        if (r.kind() != SectorStatements.Kind.LINE && r.kind() != SectorStatements.Kind.MEMO) return false;
        if (r.showFrom() > 0) return false;
        return blank(figure(r.now(), millions)) && (!c.then() || !t.thenKnown() || blank(figure(r.then(), millions)));
    }

    private static boolean blank(String figure) { return "–".equals(figure) || "—".equals(figure); }

    /** The column heads. */
    static HBox heads(String nowWord, String thenWord, Columns c) {
        HBox row = new HBox(GAP);
        row.getChildren().add(cell("note", NOTE_COLUMN, headStyle(), Pos.CENTER_LEFT));
        Region label = new Region();
        HBox.setHgrow(label, Priority.ALWAYS);
        row.getChildren().add(label);
        row.getChildren().add(cell(nowWord, FIGURE_COLUMN, headStyle(), Pos.CENTER_RIGHT));
        if (c.then()) row.getChildren().add(cell(thenWord, FIGURE_COLUMN, headStyle(), Pos.CENTER_RIGHT));
        if (c.then() && c.change()) row.getChildren().add(cell("change", CHANGE_COLUMN, headStyle(), Pos.CENTER_RIGHT));
        if (c.share() && c.shareWord() != null) row.getChildren().add(cell(c.shareWord(), SHARE_COLUMN, headStyle(), Pos.CENTER_RIGHT));
        row.setAlignment(Pos.BOTTOM_LEFT);
        row.setStyle("-fx-padding: 0 0 4 0; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0;");
        return row;
    }

    private static String headStyle() { return Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL); }

    private static Label cell(String text, double width, String style, Pos at) {
        Label l = new Label(text);
        l.setMinWidth(width);
        l.setPrefWidth(width);
        l.setMaxWidth(width);
        l.setAlignment(at);
        l.setStyle(style);
        return l;
    }

    /** One row: its note number, its label, and its figures, ruled as its kind is. */
    static Node row(SectorStatements.Row r, boolean known, boolean millions, Columns c, double base, double baseThen,
                    boolean hasNote, boolean opened, Runnable toggle, String info) {
        SectorStatements.Kind k = r.kind();
        boolean head = k == SectorStatements.Kind.HEAD;
        boolean top = head && r.label().equals(r.label().toUpperCase());
        boolean sub = k == SectorStatements.Kind.SUBTOTAL, total = k == SectorStatements.Kind.TOTAL;
        boolean memo = k == SectorStatements.Kind.MEMO;

        HBox row = lead(r, hasNote, opened, toggle, info);
        if (head) {
            row.setStyle("-fx-padding: " + (top ? "10" : "6") + " 0 2 0;");
            return row;
        }

        double then = known ? r.then() : Double.NaN;
        String rule = sub ? "over" : total ? "both" : "none";
        String nowTone = total && r.now() < 0 ? Palette.BAD : Palette.TEXT_HEAD;
        String nowStyle = sub || total ? Palette.figure(Palette.SIZE_BODY, nowTone)
                : Palette.figureRegular(Palette.SIZE_BODY, memo ? Palette.TEXT_MUTED : Palette.TEXT_BODY);
        String quiet = Palette.figureRegular(Palette.SIZE_BODY, Palette.TEXT_MUTED);
        row.getChildren().add(ruled(figure(r.now(), millions), FIGURE_COLUMN, nowStyle, rule));
        if (c.then()) row.getChildren().add(ruled(figure(then, millions), FIGURE_COLUMN, quiet, rule));
        if (c.then() && c.change()) {
            double move = Double.isFinite(r.now()) && Double.isFinite(then) ? r.now() - then : Double.NaN;
            row.getChildren().add(ruled(Double.isNaN(move) ? "" : figure(move, millions), CHANGE_COLUMN, quiet, rule));
        }
        if (c.share() && c.shareWord() != null) {
            row.getChildren().add(ruled(memo ? "" : share(r.now(), base), SHARE_COLUMN, quiet, rule));
        }
        row.setStyle("-fx-padding: " + (sub || total ? "2" : "1") + " 0 " + (total ? "2" : "1") + " 0;");
        return row;
    }

    /** A row's note number - a door when it opens something - and its label with its (i), set in as its kind is: what row() and columnsRow() put their figures after. */
    private static HBox lead(SectorStatements.Row r, boolean hasNote, boolean opened, Runnable toggle, String info) {
        SectorStatements.Kind k = r.kind();
        boolean head = k == SectorStatements.Kind.HEAD;
        boolean top = head && r.label().equals(r.label().toUpperCase());
        boolean sub = k == SectorStatements.Kind.SUBTOTAL, total = k == SectorStatements.Kind.TOTAL;
        boolean memo = k == SectorStatements.Kind.MEMO;

        HBox row = new HBox(GAP);
        row.setAlignment(Pos.CENTER_LEFT);

        // The note number: a door when it opens something.
        Label note = cell(r.note() > 0 ? String.valueOf(r.note()) : "", NOTE_COLUMN,
                Palette.words(Palette.SIZE_CAPTION, hasNote ? Palette.ACCENT : Palette.TEXT_MUTED), Pos.CENTER_LEFT);
        if (hasNote) {
            note.setText((opened ? Statement.OPENED : Statement.CLOSED) + " " + r.note());
            note.setStyle(note.getStyle() + " -fx-cursor: hand;");
            note.setOnMouseClicked(e -> toggle.run());
        }
        row.getChildren().add(note);

        Label label = new Label(r.label());
        label.setMinWidth(0);
        label.setStyle(head ? (top ? Palette.strong(Palette.SIZE_BODY, Palette.TEXT_HEAD)
                        : Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL))
                : sub || total ? Palette.strong(Palette.SIZE_BODY, Palette.TEXT_HEAD)
                : Palette.words(Palette.SIZE_BODY, memo ? Palette.TEXT_MUTED : Palette.TEXT_LABEL));
        HBox labelled = new HBox(Palette.GAP_TIGHT, label);
        labelled.setAlignment(Pos.CENTER_LEFT);
        if (info != null) labelled.getChildren().add(infoButton(info, true));
        if (!head && !sub && !total) labelled.setPadding(new javafx.geometry.Insets(0, 0, 0, INDENT));
        if (hasNote) {
            labelled.setStyle("-fx-cursor: hand;");
            label.setOnMouseClicked(e -> toggle.run());
        }
        HBox.setHgrow(labelled, Priority.ALWAYS);
        labelled.setMaxWidth(Double.MAX_VALUE);
        row.getChildren().add(labelled);
        return row;
    }

    /* --------------------------------------------------------------- a statement in columns */

    /**
     * A statement in columns, this month (0.7.75): each row's parts and its
     * figure, under `heads` - the statement of changes in equity's share
     * capital, what it kept and revalued, and the total (R3). The rows, the
     * rules and the notes are table()'s; a line every column of which prints
     * as nothing is left off.
     */
    static VBox columnsTable(SectorStatements.Table t, String table, String[] heads, Set<String> open,
                             IntFunction<Node> notes, Function<String, String> info, Runnable redraw) {
        boolean millions = SectorStatements.inMillions(t);
        VBox box = new VBox(0);
        box.setMinWidth(TABLE);
        box.setPrefWidth(TABLE);
        box.setMaxWidth(TABLE);
        HBox top = new HBox(GAP);
        top.getChildren().add(cell("note", NOTE_COLUMN, headStyle(), Pos.CENTER_LEFT));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        top.getChildren().add(gap);
        for (String h : heads) top.getChildren().add(cell(h, FIGURE_COLUMN, headStyle(), Pos.CENTER_RIGHT));
        top.setAlignment(Pos.BOTTOM_LEFT);
        top.setStyle("-fx-padding: 0 0 4 0; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 0 0 1 0;");
        box.getChildren().add(top);
        for (SectorStatements.Row r : t.rows()) {
            if (!r.shown() || nilColumns(r, millions)) continue;
            Node detail = r.note() > 0 && notes != null ? notes.apply(r.note()) : null;
            if (detail instanceof javafx.scene.layout.Pane p && p.getChildren().isEmpty()) detail = null;
            boolean opened = detail != null && open.contains(noteKey(table, r.note()));
            box.getChildren().add(columnsRow(r, millions, detail != null, opened, () -> {
                String k = noteKey(table, r.note());
                if (!open.remove(k)) open.add(k);
                redraw.run();
            }, info == null ? null : info.apply(r.id())));
            if (opened) {
                VBox.setMargin(detail, new javafx.geometry.Insets(2, 0, 8, NOTE_COLUMN + GAP + INDENT));
                box.getChildren().add(detail);
            }
        }
        return box;
    }

    /** Whether a line in columns prints as nothing in every one of them (nil()'s rule); a residual never is. */
    static boolean nilColumns(SectorStatements.Row r, boolean millions) {
        if (r.kind() != SectorStatements.Kind.LINE && r.kind() != SectorStatements.Kind.MEMO) return false;
        if (r.showFrom() > 0) return false;
        if (!blank(figure(r.now(), millions))) return false;
        if (r.parts() != null) for (double p : r.parts()) if (!blank(figure(p, millions))) return false;
        return true;
    }

    /** One row in columns: its parts, then its figure; ruled as its kind is, and a bottom line's negatives in the verdict's colour. */
    static Node columnsRow(SectorStatements.Row r, boolean millions, boolean hasNote, boolean opened, Runnable toggle,
                           String info) {
        SectorStatements.Kind k = r.kind();
        HBox row = lead(r, hasNote, opened, toggle, info);
        if (k == SectorStatements.Kind.HEAD) {
            row.setStyle("-fx-padding: 6 0 2 0;");
            return row;
        }
        boolean sub = k == SectorStatements.Kind.SUBTOTAL, total = k == SectorStatements.Kind.TOTAL;
        String rule = sub ? "over" : total ? "both" : "none";
        String plain = Palette.figureRegular(Palette.SIZE_BODY, Palette.TEXT_BODY);
        int n = r.parts() == null ? 0 : r.parts().length;
        for (int i = 0; i < n; i++) {
            String style = sub || total ? Palette.figure(Palette.SIZE_BODY, total && r.part(i) < 0 ? Palette.BAD : Palette.TEXT_HEAD) : plain;
            row.getChildren().add(ruled(figure(r.part(i), millions), FIGURE_COLUMN, style, rule));
        }
        String nowStyle = sub || total ? Palette.figure(Palette.SIZE_BODY, total && r.now() < 0 ? Palette.BAD : Palette.TEXT_HEAD)
                : Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD);
        row.getChildren().add(ruled(figure(r.now(), millions), FIGURE_COLUMN, nowStyle, rule));
        row.setStyle("-fx-padding: " + (sub || total ? "2" : "1") + " 0 " + (total ? "2" : "1") + " 0;");
        return row;
    }

    /** A figure in its column, with a rule over it (a subtotal), or over it and two under it (a bottom line). */
    private static Node ruled(String text, double width, String style, String rule) {
        Label l = cell(text, width, style, Pos.CENTER_RIGHT);
        if ("none".equals(rule)) return l;
        VBox v = new VBox(1);
        v.setMinWidth(width);
        v.setMaxWidth(width);
        v.getChildren().add(line());
        v.getChildren().add(l);
        if ("both".equals(rule)) {
            v.getChildren().add(line());
            Region gap = new Region();
            gap.setMinHeight(1);
            gap.setPrefHeight(1);
            v.getChildren().add(gap);
            v.getChildren().add(line());
        }
        return v;
    }

    private static Region line() {
        Region r = new Region();
        r.setMinHeight(1);
        r.setPrefHeight(1);
        r.setMaxHeight(1);
        r.setMaxWidth(Double.MAX_VALUE);
        r.setStyle("-fx-background-color: " + Palette.TEXT_SPENT + ";");
        return r;
    }

    /**
     * A formal statement as a card: its title block, the toolbar, the table.
     * `toolbar` may be null for a statement without one (the equity
     * statement's single column).
     */
    static VBox card(VBox title, HBox toolbar, VBox table) {
        VBox c = new VBox(Palette.GAP, title);
        if (toolbar != null) c.getChildren().add(toolbar);
        c.getChildren().add(table);
        c.setMinWidth(TABLE + 32);
        c.setMaxWidth(TABLE + 32);
        c.setStyle("-fx-padding: 14 16 16 16; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return c;
    }

    /** A statement card beside its ratios, wrapping the ratios under it on a window too narrow for both. */
    static javafx.scene.layout.FlowPane beside(Node statement, Node ratios) {
        javafx.scene.layout.FlowPane f = new javafx.scene.layout.FlowPane(24, 16);
        if (statement != null) f.getChildren().add(statement);
        if (ratios != null) f.getChildren().add(ratios);
        f.setAlignment(Pos.TOP_LEFT);
        f.setRowValignment(javafx.geometry.VPos.TOP);
        return f;
    }

    /** A note's rows, this month: a small list under the line it opens (D12: this month only). */
    static VBox noteRows(List<SectorStatements.Row> rows, boolean millions, String sentence) {
        VBox box = new VBox(1);
        for (SectorStatements.Row r : rows) {
            if (!r.shown()) continue;
            Label what = new Label(r.label());
            what.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            HBox line = new HBox(GAP, what, gap, cell(figure(r.now(), millions), FIGURE_COLUMN,
                    Palette.figureRegular(Palette.SIZE_CAPTION, Palette.TEXT_MUTED), Pos.CENTER_RIGHT));
            line.setMaxWidth(TABLE - NOTE_COLUMN - GAP - INDENT - FIGURE_COLUMN * 1 - GAP);
            box.getChildren().add(line);
        }
        if (sentence != null) {
            Label s = new Label(sentence);
            s.setWrapText(true);
            s.setMaxWidth(TABLE - NOTE_COLUMN - GAP - INDENT - 40);
            s.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED) + " -fx-padding: 2 0 0 0;");
            box.getChildren().add(s);
        }
        return box;
    }

    /** A sentence as a note: what a line is, when it has no parts to list. */
    static VBox noteSentence(String sentence) {
        return noteRows(List.of(), false, sentence);
    }

    /* --------------------------------------------------------------- IN SHORT */

    /** IN SHORT's columns: a label, then this month, last month and the change. */
    static final double SHORT_LABEL = 150, SHORT_FIGURE = 78;

    /**
     * IN SHORT (the summaries' card): five lines, this month, last month and
     * the change, in $ millions - the statement's own subtotals (D14), so the
     * summary and the statement say the same thing in two sizes.
     */
    static VBox inShort(String[] labels, double[] now, double[] then, boolean known, String info) {
        VBox c = new VBox(4, SectorScreen.head("IN SHORT", info, null));
        HBox heads = new HBox(GAP, cell("$ millions", SHORT_LABEL, headStyle(), Pos.CENTER_LEFT),
                cell("this month", SHORT_FIGURE, headStyle(), Pos.CENTER_RIGHT),
                cell("last month", SHORT_FIGURE, headStyle(), Pos.CENTER_RIGHT),
                cell("change", SHORT_FIGURE, headStyle(), Pos.CENTER_RIGHT));
        c.getChildren().add(heads);
        for (int i = 0; i < labels.length; i++) {
            double t = known ? then[i] : Double.NaN;
            double move = Double.isFinite(now[i]) && Double.isFinite(t) ? now[i] - t : Double.NaN;
            HBox row = new HBox(GAP, cell(labels[i], SHORT_LABEL, Palette.words(Palette.SIZE_BODY, Palette.TEXT_LABEL), Pos.CENTER_LEFT),
                    cell(millions(now[i]), SHORT_FIGURE, Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD), Pos.CENTER_RIGHT),
                    cell(millions(t), SHORT_FIGURE, Palette.figureRegular(Palette.SIZE_BODY, Palette.TEXT_MUTED), Pos.CENTER_RIGHT),
                    cell(Double.isNaN(move) ? "—" : signedMillions(move), SHORT_FIGURE,
                            Palette.figureRegular(Palette.SIZE_BODY, Palette.TEXT_MUTED), Pos.CENTER_RIGHT));
            c.getChildren().add(row);
        }
        c.setMinWidth(SHORT_LABEL + 3 * SHORT_FIGURE + 3 * GAP + 32);
        c.setMaxWidth(SHORT_LABEL + 3 * SHORT_FIGURE + 3 * GAP + 32);
        c.setStyle("-fx-padding: 12 16 12 16; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return c;
    }

    /** Thousands as millions to a tenth, a true minus below nothing (the summaries keep their signs, D3). */
    static String millions(double thousands) {
        if (!Double.isFinite(thousands)) return "—";
        double v = thousands / 1000;
        String s = String.format("%,.1f", Math.abs(v));
        return v < 0 && !"0.0".equals(s) ? "−" + s : s;
    }

    /** ...with a sign either way, for a change. */
    static String signedMillions(double thousands) {
        String s = millions(Math.abs(thousands));
        if ("0.0".equals(s)) return s;
        return (thousands < 0 ? "−" : "+") + s;
    }

    /** "Gross profit" from "GROSS PROFIT": a statement's line as a sentence's words, for the summaries. */
    static String sentence(String caps) {
        if (caps == null || caps.isEmpty()) return caps;
        String low = caps.toLowerCase();
        return Character.toUpperCase(low.charAt(0)) + low.substring(1);
    }
}
