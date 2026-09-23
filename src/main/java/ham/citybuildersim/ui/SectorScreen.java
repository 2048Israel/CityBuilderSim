package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * The sector economy: the businesses as a list, and each one's five pages -
 * operations, the income statement with last month beside it, the balance
 * sheet, cash and credit, and what its investors decided - every line of the
 * statement opening into where the figure came from.
 *
 * Split out of UserInterface on 2026-09-18: the three banners THE SECTOR
 * ECONOMY, ONE BUSINESS, FIVE PAGES and A STATEMENT LINE THAT OPENS exactly as
 * they were, the shell's members reached through ui. The shell still reads
 * which sector and page are open (openSector, sectorPage) for the rail and
 * the scroll memory, and other screens open a sector's books through
 * openSectorBooks().
 */
final class SectorScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    SectorScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       THE SECTOR ECONOMY.

       Every business in the city, each with a real set of books - six when
       this was written, every sector in Sectors.KEYS now.

       WHAT IT WAS: six rows and a click into a wall of Courier. Two of the six
       were wrong - the utilities are a service and are on the Services tab now,
       and "Retail & consumer services" was two separate companies added
       together. BusinessDebtManager has always treated the shops and the
       landlords as different borrowers with different rates, different leverage
       and different books; only the screen pretended otherwise.

       WHAT THE STATEMENTS ARE. Every sector reports the same statement in the
       same order, and it is a real one: revenue down to net income through
       operating income, interest and property tax, with LAST MONTH beside it.
       The comparative column is the difference between a statement that is
       correct and a statement that is readable - a $2.1M profit says nothing;
       $2.1M against $3.4M says the sector halved.

       That comes out of SectorBooks rather than out of the handlers, because a
       handler holds this month and nothing else, and because five handlers in
       five shapes is not something six screens should each have to know.
       ===================================================================== */

    /** Which sector's books are open, or null for the list. */
    Sector openSector = null;
    static final String SECTOR_HOME   = "Operations";
    String sectorPage = SECTOR_HOME;

    /** Whether every card on the list is open to its second row (0.7.4) - kept while the game runs, like the page, and never saved. */
    boolean sectorsExpanded = false;

    static final String[] SECTOR_PAGES =
            {"Operations", "Income", "Balance sheet", "Cash & debt", "Investors"};

    /** Open one business's books from somewhere else in the game. */
    void openSectorBooks(Sector sector, String page) {
        openSector = sector;
        sectorPage = page;
        ui.innerScrollAt.remove("showSectorMenu:body");
        showSectorMenu();
    }

    /** The tab lands here: what every business in the city earned. */
    void showSectorMenu() {
        ui.clearMenu("showSectorMenu", () -> showSectorMenu());

        if (openSector != null) {
            drawSectorScreen();
            return;
        }

        SectorBooks books = ui.game.getSectorBooks();

        Label title = new Label("SECTOR ECONOMY");
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        Label lead = new Label("What the city's businesses kept this month, after tax.");
        lead.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED)
                + " -fx-padding: 0 0 10 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        if (books.isEmpty()) {
            column.getChildren().add(alert("Nothing recorded yet",
                    "The sector books are written when a month closes. This city has not "
                    + "closed one since it was loaded — press the arrow once and every "
                    + "statement behind these rows fills in."));
        }

        /*
         * SHOW MORE, for every card at once (0.7.4). Jerus: "perhaps a button
         * in which you can click to expand to show some more info for all at
         * once while still in that screen." A chip at the top of the list, in
         * the chip style the policy page's toggles use, that opens every card
         * to its second row; the state is this screen's, kept while the game
         * runs and never saved, like the page a sector's books are open at.
         */
        Label listSays = new Label("Net income, the last two years of it, and who works there.");
        listSays.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Region toggleGap = new Region();
        HBox.setHgrow(toggleGap, Priority.ALWAYS);
        HBox toggleRow = new HBox(Palette.GAP, listSays, toggleGap,
                stepChip(sectorsExpanded ? "Show less" : "Show more", () -> {
                    sectorsExpanded = !sectorsExpanded;
                    showSectorMenu();
                }, false));
        toggleRow.setAlignment(Pos.CENTER_LEFT);
        toggleRow.setPrefWidth(STATEMENT);
        toggleRow.setMaxWidth(STATEMENT);
        toggleRow.setStyle("-fx-padding: 0 0 6 0;");
        column.getChildren().add(toggleRow);

        // Every series once, for the sparklines: HistorySave's
        // netIncome:<sector> since 0.7.4.
        java.util.Map<String, List<? extends Number>> series = ui.game.getHistorySave().seriesByName();

        double total = 0;
        double totalBefore = 0;
        for (Sector sector : ui.game.getSectors().all()) {
            SectorBooks.SectorMonth now = books.get(sector);
            SectorBooks.SectorMonth then = books.previous(sector);
            total += now.netIncome();
            totalBefore += then.netIncome();
            column.getChildren().add(sectorCard(sector, now, then,
                    series.get(HistorySave.netIncomeKey(sector.key()))));
        }

        column.getChildren().add(statementTotal("All " + numberWord(ui.game.getSectors().size()) + " together",
                tightMoney(toDollars(total)) + "/mo",
                total < 0 ? Palette.BAD : Palette.GOOD));
        if (Math.abs(totalBefore) > 0) {
            column.getChildren().add(statementNote(String.format(
                    "Last month %s. Business tax is charged on each company's own profit "
                    + "and never refunded on a loss, so a city where half the sectors lose "
                    + "money still collects from the other half.",
                    tightMoney(toDollars(totalBefore)))));
        }

        ui.rootMenu.getChildren().addAll(title, lead, ui.scrolled(column));
    }

    /**
     * One business on the list: what it kept, which way it moved, and a click in.
     *
     * The movement is the second figure because the level on its own cannot be
     * read - every one of these is a number nobody has an intuition for, and
     * "down a third" is a fact anybody can act on.
     *
     * AND SINCE 0.7.4 A LINE AND A HEAD COUNT. Jerus: "beside each sector to
     * show some quick info, not only net income and change but also a little
     * graph of its net income, perhaps how much workers it employs total".
     * Between the blurb and the figures, the last SPARK_MONTHS of net income
     * off the history (netIncome:<sector>); under the move, its workers -
     * Sector.getWorkers(), the posts at the operations page's "Staffed"
     * share. With the list opened (sectorsExpanded), a second row of five
     * figures off SectorMonth and the sector. A click anywhere on the card
     * still opens the books.
     */
    VBox sectorCard(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then,
                    List<? extends Number> netIncomeSeries) {

        Label name = new Label(sector.label());
        name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));

        // Wrapped at a fixed width now that the sparkline sits beside it: a
        // long blurb takes a second line rather than pushing the figures off.
        Label what = new Label(sectorBlurb(sector));
        what.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        what.setWrapText(true);
        what.setMaxWidth(CARD_LEFT);

        VBox left = new VBox(0, name, what);
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPrefWidth(CARD_LEFT);
        left.setMinWidth(CARD_LEFT);
        left.setMaxWidth(CARD_LEFT);

        javafx.scene.Node spark = sparkline(netIncomeSeries);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label figure = new Label(tightMoney(toDollars(now.netIncome())) + "/mo");
        figure.setStyle(Palette.figure(Palette.SIZE_LEAD,
                now.netIncome() < 0 ? Palette.BAD : Palette.TEXT_HEAD));

        double move = now.netIncome() - then.netIncome();
        Label change = new Label(then.isEmpty() ? "no month to compare"
                : (move >= 0 ? "+" : "-") + tightMoney(Math.abs(toDollars(move))));
        change.setStyle(Palette.words(Palette.SIZE_CAPTION,
                then.isEmpty() ? Palette.TEXT_SPENT
                        : move < 0 ? Palette.BAD : Palette.GOOD));

        Label workers = new Label(String.format("%,.0f workers", sector.getWorkers()));
        workers.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));

        VBox right = new VBox(0, figure, change, workers);
        right.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(Palette.GAP, left, spark, gap, right);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(0, row);
        if (sectorsExpanded) card.getChildren().add(sectorCardMore(sector, now));
        card.setPrefWidth(STATEMENT);
        card.setMaxWidth(STATEMENT);
        String rest = "-fx-padding: 8 12 8 12; -fx-cursor: hand;";
        card.setStyle(rest + Palette.block(Palette.CONTROL));
        card.setOnMouseClicked(e -> {
            openSector = sector;
            sectorPage = "Operations";
            ui.innerScrollAt.remove("showSectorMenu:body");
            showSectorMenu();
        });
        card.setOnMouseEntered(e -> card.setStyle(rest
                + Palette.block(Palette.RAISED, Palette.ACCENT)));
        card.setOnMouseExited(e -> card.setStyle(rest + Palette.block(Palette.CONTROL)));
        VBox.setMargin(card, new javafx.geometry.Insets(0, 0, 6, 0));
        return card;
    }

    /** The width the card's name and blurb are held to, so the sparkline and the figures always have their room (0.7.4). */
    static final double CARD_LEFT = 280;

    /** The sparkline's width on a sector's card (0.7.4): a word's size, between the blurb and the figures. */
    static final double SPARK_WIDTH = 90;

    /** ...and its height, a line of caption and a half. */
    static final double SPARK_HEIGHT = 22;

    /** How many months of net income the sparkline draws (0.7.4): two years. */
    static final int SPARK_MONTHS = 24;

    /**
     * A sector's net income as a line (0.7.4): the last SPARK_MONTHS of the
     * history's netIncome:<sector>, the zero line faint under it, the line
     * GOOD when the latest month made money and BAD when it did not. No
     * axes and no labels - the figure beside it is the number - and fewer
     * than two months is said in words.
     */
    javafx.scene.Node sparkline(List<? extends Number> series) {

        List<Double> months = new ArrayList<>();
        if (series != null) {
            for (int i = Math.max(0, series.size() - SPARK_MONTHS); i < series.size(); i++) {
                Number v = series.get(i);
                if (v != null && Double.isFinite(v.doubleValue())) months.add(v.doubleValue());
            }
        }
        if (months.size() < 2) {
            Label none = new Label("no history yet");
            none.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_SPENT));
            none.setMinWidth(SPARK_WIDTH);
            none.setPrefWidth(SPARK_WIDTH);
            return none;
        }

        // The range always holds zero, so the faint line is always on it.
        double lo = 0, hi = 0;
        for (double v : months) { lo = Math.min(lo, v); hi = Math.max(hi, v); }
        final double low = lo, span = hi - lo, pad = 2;
        final double w = SPARK_WIDTH - 2 * pad, h = SPARK_HEIGHT - 2 * pad;
        java.util.function.DoubleUnaryOperator y =
                v -> span <= 0 ? pad + h / 2 : pad + h - (v - low) / span * h;

        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(SPARK_WIDTH, SPARK_HEIGHT);
        javafx.scene.canvas.GraphicsContext g = canvas.getGraphicsContext2D();

        double zero = Math.floor(y.applyAsDouble(0)) + .5;
        g.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_SPENT, .5));
        g.setLineWidth(1);
        g.strokeLine(pad, zero, pad + w, zero);

        double latest = months.get(months.size() - 1);
        g.setStroke(javafx.scene.paint.Color.web(latest > 0 ? Palette.GOOD : Palette.BAD));
        g.setLineWidth(1.5);
        g.beginPath();
        for (int i = 0; i < months.size(); i++) {
            double x = pad + w * i / (months.size() - 1);
            if (i == 0) g.moveTo(x, y.applyAsDouble(months.get(i)));
            else g.lineTo(x, y.applyAsDouble(months.get(i)));
        }
        g.stroke();

        javafx.scene.control.Tooltip.install(canvas, new javafx.scene.control.Tooltip(String.format(
                "Net income after tax, the last %d months: between %s and %s a month.",
                months.size(), tightMoney(toDollars(lo)), tightMoney(toDollars(hi)))));
        return canvas;
    }

    /**
     * The card's second row, with the list opened (0.7.4): five figures at the
     * caption's size - revenue, margin, cash, what it owes, and its workers
     * against the posts it offers - each off SectorMonth or the sector,
     * nothing recomputed.
     */
    HBox sectorCardMore(Sector sector, SectorBooks.SectorMonth now) {
        boolean known = !now.isEmpty();
        HBox more = new HBox(Palette.GAP,
                moreCell("revenue", known ? tightMoney(toDollars(now.revenue())) + "/mo" : "—",
                        known ? Palette.TEXT_BODY : Palette.TEXT_SPENT),
                moreCell("margin", known ? String.format("%.1f%%", now.margin() * 100) : "—",
                        !known ? Palette.TEXT_SPENT : now.margin() < 0 ? Palette.BAD : Palette.TEXT_BODY),
                moreCell("cash", known ? tightMoney(toDollars(now.cash())) : "—",
                        !known ? Palette.TEXT_SPENT : now.cash() < 0 ? Palette.BAD : Palette.TEXT_BODY),
                moreCell("owes the bank", known ? tightMoney(toDollars(now.bondsPayable())) : "—",
                        known ? Palette.TEXT_BODY : Palette.TEXT_SPENT),
                moreCell("posts filled", String.format("%,.0f of %,d",
                        sector.getWorkers(), sector.getPostsOffered()), Palette.TEXT_BODY));
        more.setAlignment(Pos.CENTER_LEFT);
        more.setStyle("-fx-padding: 6 0 0 0;");
        return more;
    }

    /** One labelled figure on a card's second row: the word over the figure, a fifth of the card wide. */
    static VBox moreCell(String word, String figure, String tone) {
        Label head = new Label(word);
        head.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Label value = new Label(figure);
        value.setStyle(Palette.words(Palette.SIZE_CAPTION, tone));
        VBox cell = new VBox(0, head, value);
        double wide = (STATEMENT - 24 - 4 * Palette.GAP) / 5;
        cell.setPrefWidth(wide);
        cell.setMinWidth(wide);
        return cell;
    }

    /** What the business actually does, in a few words - the sector's own first sentence. */
    static String sectorBlurb(Sector sector) {
        String blurb = sector.blurb();
        if (blurb == null || blurb.isBlank()) return sector.label();
        int stop = blurb.indexOf('.');
        String first = stop > 0 ? blurb.substring(0, stop) : blurb;
        return first.length() > 72 ? first.substring(0, 69) + "..." : first;
    }

    /** "six", "seven" - for the total line, which names the count. */
    static String numberWord(int n) {
        String[] words = { "no", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve" };
        return n >= 0 && n < words.length ? words[n] : String.valueOf(n);
    }

    /* =====================================================================
       ONE BUSINESS, FIVE PAGES
       ===================================================================== */

    void drawSectorScreen() {

        Sector sector = openSector;
        SectorBooks books = ui.game.getSectorBooks();
        SectorBooks.SectorMonth now = books.get(sector);
        SectorBooks.SectorMonth then = books.previous(sector);

        Label title = new Label(sector.label().toUpperCase());
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        HBox vitals = sectorVitals(sector, now, then);

        javafx.scene.layout.FlowPane strip =
                chipStrip(SECTOR_PAGES, sectorPage, Palette.SIZE_LABEL, name -> {
                    sectorPage = name;
                    ui.innerScrollAt.remove("showSectorMenu:body");
                    showSectorMenu();
                });
        strip.setStyle("-fx-padding: 8 0 10 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        if (now.isEmpty()) {
            column.getChildren().add(alert("Nothing recorded yet",
                    "This city has not closed a month since it was loaded, so there is no "
                    + "statement to draw. Press the arrow once."));
        } else {
            switch (sectorPage) {
                case "Income"        -> incomePage(column, sector, now, then);
                case "Balance sheet" -> balancePage(column, sector, now, then);
                case "Cash & debt"   -> cashAndDebtPage(column, sector, now, then);
                case "Investors"     -> investorPage(column, sector, now);
                default              -> operationsPage(column, sector);
            }
        }

        Button back = new Button("All sectors");
        back.setOnAction(e -> {
            openSector = null;
            ui.innerScrollAt.remove("showSectorMenu:body");
            showSectorMenu();
        });

        ui.rootMenu.getChildren().addAll(title, vitals, strip, ui.scrolled(column, 250), back);
    }

    HBox sectorVitals(Sector sector,
                              SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {

        double move = now.netIncome() - then.netIncome();
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();

        return vitalsBar(
                limitCell("KEPT", tightMoney(toDollars(now.netIncome())),
                        then.isEmpty() ? "after tax"
                                : String.format("%s%s on last month",
                                        move >= 0 ? "+" : "-",
                                        tightMoney(Math.abs(toDollars(move)))),
                        now.netIncome() < 0 ? Palette.BAD : Palette.GOOD),
                limitCell("MARGIN", String.format("%.1f%%", now.margin() * 100),
                        "of every dollar taken",
                        now.margin() < 0 ? Palette.BAD
                                : now.margin() < .05 ? Palette.WARN : Palette.GOOD),
                limitCell("CASH", tightMoney(toDollars(now.cash())),
                        now.cash() < 0 ? "overdrawn — it is borrowing" : "in its own account",
                        now.cash() < 0 ? Palette.BAD : Palette.TEXT_HEAD),
                limitCell("OWES", tightMoney(toDollars(now.bondsPayable())),
                        credit.isBorrowingBlocked(sector.key())
                                ? "cannot borrow — " + credit.getBlockedMonths(
                                        sector.key()) + " months left"
                                : String.format("at %.2f%%, leverage %.2f",
                                        now.rate() * 100, now.leverage()),
                        credit.isBorrowingBlocked(sector.key()) ? Palette.BAD
                                : now.leverage() > .6 ? Palette.WARN : Palette.TEXT_HEAD));
    }

    /* =====================================================================
       A STATEMENT LINE THAT OPENS

       Jerus, 2026-09-16: "can you make it so in the UI when you click on
       revenue or cogs, it expands and shows the individual items".

       Same mechanics as budgetLine() on the city's budget pages, which took
       the same request a week earlier for the same reason: one figure that is
       really ten is a figure a player can read and cannot act on. Revenue is
       five goods at five prices, half of them possibly shipped abroad at a
       floor; the cost of sales is three or four inputs, some grown here and
       some landed. Which one is carrying the sector, and how much of it is the
       world, are both decisions - a farm to build, a tax to move - and neither
       is answerable from a total.

       THE DETAIL IS THIS MONTH ONLY, and the closed row keeps both columns.
       SectorBooks keeps two months of SectorMonth and a SectorMonth carries no
       per-good money - it is a twenty-eight-component positional record with
       three construction sites, and widening it to carry a map would be a lot
       of surface for a comparative column nobody asked for. The breakdown
       answers "what is this made of", which is a question about now.
       ===================================================================== */

    /**
     * One line inside an opened statement line.
     *
     * Right-aligned into the SAME column the figure it explains sits in, so a
     * breakdown reads down the page against its own total rather than floating
     * in the middle of the row. The comparative column is left empty: the
     * detail is this month's (see above), and a dash there would read as a
     * figure the screen could not find rather than one it does not keep.
     */
    HBox bookDetailRow(String label, String value, boolean indented, String tone) {
        Label what = new Label(label);
        what.setStyle(Palette.words(Palette.SIZE_CAPTION,
                indented ? Palette.TEXT_MUTED : Palette.TEXT_BODY));

        Region pad = new Region();
        pad.setMinWidth(indented ? 14 : 0);
        pad.setPrefWidth(indented ? 14 : 0);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label a = new Label(value);
        a.setPrefWidth(BOOK_NOW);
        a.setMinWidth(BOOK_NOW);
        a.setAlignment(Pos.CENTER_RIGHT);
        a.setStyle(Palette.figure(Palette.SIZE_CAPTION,
                tone == null ? (indented ? Palette.TEXT_MUTED : Palette.TEXT_BODY) : tone));

        Region tail = new Region();
        tail.setMinWidth(BOOK_THEN);
        tail.setPrefWidth(BOOK_THEN);

        HBox row = new HBox(Palette.GAP, pad, what, gap, a, tail);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(STATEMENT);
        row.setPrefWidth(STATEMENT);
        row.setStyle("-fx-padding: 1 0 1 0;");
        return row;
    }

    HBox bookDetailRow(String label, double amount, boolean indented) {
        return bookDetailRow(label, tightMoney(toDollars(amount), false), indented, null);
    }

    /** A sentence at the bottom of an opened line, when the split has something to say. */
    Label bookDetailNote(String text) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setMaxWidth(STATEMENT - 40);
        l.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)
                + " -fx-padding: 3 0 2 0;");
        return l;
    }

    /** The line a section adds up to: a rule, then the figure at full weight. */
    VBox bookTotal(String label, double now, double then, boolean known, String tone) {

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

    /* -------------------------- THE INCOME STATEMENT -------------------------- */

    /** What this sector's direct cost is actually called - the sector says. */
    static String inputLabel(Sector sector) {
        return sector.inputLabel();
    }

    /* ------------------- what the two big lines open into ------------------- */

    /**
     * Revenue, by good, each split into what the city took and what was
     * shipped.
     *
     * WHY THE SPLIT IS ON EVERY GOOD AND NOT ONE LINE AT THE BOTTOM. A sector
     * can be selling one good entirely at home at the import ceiling and
     * dumping another abroad at the floor in the same month - Food Processing
     * does exactly that with drinks and cooking fats - and a single "exported
     * $22k" line under the total cannot say which. The two prices are
     * different, so the two lines have to be.
     */
    VBox revenueDetail(Sector sector) {
        VBox box = new VBox(0);
        if (sector == null) return box;
        Sector.Statement st = sector.statement();

        java.util.List<java.util.Map.Entry<Good, Sector.Split>> lines =
                new java.util.ArrayList<>(st.sold.entrySet());
        lines.removeIf(e -> Math.abs(e.getValue().total()) < .0000005);
        lines.sort((x, y) -> Double.compare(y.getValue().total(), x.getValue().total()));

        double abroad = 0;
        for (java.util.Map.Entry<Good, Sector.Split> e : lines) {
            Sector.Split s = e.getValue();
            abroad += s.abroad;
            box.getChildren().add(bookDetailRow(e.getKey().label(), s.total(), false));
            if (s.atHome != 0 && s.abroad != 0) {
                box.getChildren().add(bookDetailRow("in the city", s.atHome, true));
                box.getChildren().add(bookDetailRow("shipped abroad", s.abroad, true));
            } else if (s.abroad != 0) {
                box.getChildren().add(bookDetailRow("all of it shipped abroad", s.abroad, true));
            }
        }
        /*
         * WORK IS NOT A GOOD, and the one sector that sells any is the one this
         * breakdown would otherwise show as empty: the builders recognise
         * building work and bill repairs, and neither clears on a goods
         * market. Named parts rather than one label - see
         * Sector.otherRevenueParts() and what happened without it.
         */
        int named = box.getChildren().size();
        for (java.util.Map.Entry<String, Double> part : sector.otherRevenueParts().entrySet()) {
            if (Math.abs(part.getValue()) < .0000005) continue;
            box.getChildren().add(bookDetailRow(part.getKey(), part.getValue(), false));
            named++;
        }

        /*
         * AND A DISCLOSURE THAT DISCLOSES NOTHING DOES NOT GET DRAWN.
         *
         * Retail sells groceries, the landlords sell housing, a materials
         * plant sells building material: one row, no split, and the caret
         * would promise a breakdown and then repeat the figure above it. The
         * player pays a click to find that out.
         *
         * COUNTED RATHER THAN GUESSED AT, since Jerus asked on 2026-09-16
         * whether this line shows its revenues. The first version tested
         * `lines.size() == 1` on the GOODS - which is zero for the builders,
         * who sell no goods at all - so Construction fell straight through it
         * and drew a caret onto a single row reading $140,781,361, the number
         * already on the line above. A rule about how many rows there are has
         * to count the rows.
         */
        if (named <= 1 && abroad == 0) {
            box.getChildren().clear();
            return box;
        }

        if (abroad > 0 && st.revenue > 0) {
            box.getChildren().add(bookDetailNote(String.format(
                    "%.0f%% of this month went abroad, where the price is the world's floor "
                    + "rather than the city's.", abroad / st.revenue * 100)));
        }
        return box;
    }

    /**
     * The cost of sales, by good, each split into what the city grew or made
     * and what was landed.
     *
     * AND THE SPLIT IS THE POINT HERE, more than on the revenue side. An input
     * bought from a supplier in this city carries a sales-tax credit at that
     * supplier's rate; one bought abroad carries none, because no foreign
     * seller remitted anything (SalesTaxLedger.chargeImport). So the same
     * kilogram at the same price costs an import-fed sector more, and this is
     * the only screen that says which kilograms those were.
     */
    VBox inputsDetail(Sector sector) {
        VBox box = new VBox(0);
        if (sector == null) return box;
        Sector.Statement st = sector.statement();

        java.util.List<java.util.Map.Entry<Good, Sector.Split>> lines =
                new java.util.ArrayList<>(st.bought.entrySet());
        lines.removeIf(e -> Math.abs(e.getValue().total()) < .0000005);
        lines.sort((x, y) -> Double.compare(y.getValue().total(), x.getValue().total()));

        /*
         * THE COST SIDE NEVER SUPPRESSES, and that is not the same rule as the
         * revenue side's because it is not the same question.
         *
         * An opened cost line always says WHERE the input came from, and that
         * is news even when there is one input and it all came from one place:
         * "Crops, all of it from the city" is the difference between a mill
         * with farms behind it and a mill on a ship, and the closed line
         * cannot say which. The draft that suppressed it left the Industry
         * sector's whole input story behind a line that looked like it had
         * nothing under it.
         */
        double landed = 0;
        for (java.util.Map.Entry<Good, Sector.Split> e : lines) {
            Sector.Split s = e.getValue();
            landed += s.abroad;
            box.getChildren().add(bookDetailRow(e.getKey().label(), -s.total(), false));
            if (s.atHome != 0 && s.abroad != 0) {
                box.getChildren().add(bookDetailRow("from the city", -s.atHome, true));
                box.getChildren().add(bookDetailRow("imported", -s.abroad, true));
            } else if (s.abroad != 0) {
                box.getChildren().add(bookDetailRow("all of it imported", -s.abroad, true));
            } else {
                box.getChildren().add(bookDetailRow("all of it from the city", -s.atHome, true));
            }
        }
        /*
         * AND WHAT IS IN THIS LINE THAT IS NOT A GOOD, by name.
         *
         * The haulage the railway billed, and the railway's own fuel. A service
         * has no units and no market, so it can never appear in the per-good
         * rows above - and the first draft of the railway left the opened cost
         * line adding up to less than the closed one, which is exactly the
         * disclosure this screen refuses to draw. Named where it was charged
         * rather than worked out here by subtraction; see
         * Sector.billForService() and Sector.otherInputParts().
         */
        double services = 0;
        for (java.util.Map.Entry<String, Double> e : sector.otherInputParts().entrySet()) {
            if (Math.abs(e.getValue()) < .0000005) continue;
            box.getChildren().add(bookDetailRow(e.getKey(), -e.getValue(), false));
            services += e.getValue();
        }
        if (services != 0 && sector != ui.game.getSectors().rail()) {
            box.getChildren().add(bookDetailNote("Freight. The railway carries what it can "
                    + "of this business's trade and bills for it here; the rest is inside "
                    + "the import and export prices, paid to whoever moved it."));
        }

        if (box.getChildren().isEmpty()) return box;

        if (st.inputs > 0) {
            double share = landed / st.inputs;
            box.getChildren().add(bookDetailNote(share > .5
                    ? String.format("%.0f%% of this bill was landed rather than bought here, and "
                    + "an import carries no sales-tax credit - so the tax below falls on nearly "
                    + "the whole ticket rather than on what this business added.", share * 100)
                    : String.format("%.0f%% of this bill was landed. The rest was bought from "
                    + "businesses in this city, whose own sales tax this one gets credit for.",
                    share * 100)));
        }
        return box;
    }

    /** Wages by pay tier - which kind of worker this business is actually paying. */
    VBox wagesDetail(Sector sector) {
        VBox box = new VBox(0);
        if (sector == null) return box;
        double[] pay = sector.getStaffedPayrollPerType();
        int[] posts = sector.postsPerTier();
        if (pay == null) return box;

        java.util.List<Integer> order = new java.util.ArrayList<>();
        for (int i = 0; i < pay.length && i < JobType.values().length; i++) {
            if (Math.abs(pay[i]) >= .0000005) order.add(i);
        }
        order.sort((x, y) -> Double.compare(pay[y], pay[x]));
        for (int i : order) {
            JobType job = JobType.values()[i];
            int n = posts != null && i < posts.length ? posts[i] : 0;
            box.getChildren().add(bookDetailRow(
                    n > 0 ? String.format("%s  (%d %s)", ui.buildScreen.jobLabel(job), n, n == 1 ? "post" : "posts")
                          : ui.buildScreen.jobLabel(job), -pay[i], false));
        }
        /*
         * AND WHAT THE UNFILLED POSTS WOULD COST, when there are any. The
         * payroll on the statement is the STAFFED payroll - a sector at 70%
         * fill pays 70% of its wage bill - and a player looking at a cheap
         * wage line on a half-staffed sector is reading a saving that is
         * really a shortage. See Sector.getPayroll().
         */
        if (!box.getChildren().isEmpty() && sector.getAverageFill() < .999) {
            double staffed = 0;
            for (double p : pay) staffed += p;
            double full = sector.getAverageFill() > 0 ? staffed / sector.getAverageFill() : staffed;
            box.getChildren().add(bookDetailNote(String.format(
                    "These posts are %.0f%% filled. Fully staffed the wage bill would be %s, "
                    + "and the business would be making what its buildings can make.",
                    sector.getAverageFill() * 100, tightMoney(toDollars(full), false))));
        }
        return box;
    }

    /**
     * The sales tax, as the ledger actually strikes it: charged on what was
     * sold here, credited for what suppliers already remitted, and the
     * difference remitted.
     *
     * A NET FIGURE ON A STATEMENT HIDES A VAT. Sales tax remitted can be a
     * small number because the sector sells little or because its credits are
     * large, and those are opposite situations. Exports are zero-rated and keep
     * their credits, so a heavy exporter can even be in refund - the ledger
     * does not floor it, and neither does this.
     */
    VBox salesTaxDetail(Sector sector) {
        VBox box = new VBox(0);
        if (sector == null || ui.game == null) return box;
        SalesTaxLedger ledger = ui.game.getEconomyManager().getSalesTaxLedger();
        String key = sector.key();
        double payable = ledger.getPayable(key), credit = ledger.getCredit(key);
        if (Math.abs(payable) < .0000005 && Math.abs(credit) < .0000005) return box;

        double rate = ui.game.getEconomyManager().getTaxPolicy().effectiveSalesRate(sector);
        double taxable = ledger.getTaxableSales(key), zeroRated = ledger.getZeroRated(key);
        double importTax = ledger.getImportTax(key);

        /*
         * EVERY ROW HERE IS CONDITIONAL, because three of the four are zero
         * for somebody. A pure exporter charges nothing and is all credit; a
         * sector buying only at home is charged nothing at the border; one
         * buying only abroad has no supplier credit at all. A panel of "$0"
         * rows reads as a screen that could not find the figures.
         */
        if (Math.abs(payable - importTax) >= .0000005) {
            box.getChildren().add(bookDetailRow(
                    String.format("Charged on %s sold here, at %.1f%%",
                            tightMoney(toDollars(taxable), false), rate * 100),
                    -(payable - importTax), false));
        }
        if (Math.abs(importTax) >= .0000005) {
            box.getChildren().add(bookDetailRow("Charged at the border on imports", -importTax, false));
        }
        if (Math.abs(credit) >= .0000005) {
            box.getChildren().add(bookDetailRow("Credit for tax its suppliers remitted", credit, false));
        }
        if (zeroRated > 0) {
            box.getChildren().add(bookDetailRow(
                    String.format("Zero-rated: %s shipped abroad",
                            tightMoney(toDollars(zeroRated), false)), "\u2014", false, null));
        }
        box.getChildren().add(bookDetailNote(payable - credit < 0
                ? "The city owes this business tax this month rather than the other way round. "
                + "That is a refund and it is correct: exports are charged nothing and keep the "
                + "credits behind them."
                : "The tax is on what this business ADDED - what it charged, less what its "
                + "suppliers already remitted. An import carries no such credit, because nobody "
                + "abroad remits anything to this city."));
        return box;
    }

    void incomePage(VBox column, Sector sector,
                            SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {

        boolean known = !then.isEmpty();

        column.getChildren().add(statementHead("Income statement"));
        column.getChildren().add(bookHead(CityCalendar.format(now.month())));

        column.getChildren().add(bookLine("Revenue",
                now.revenue(), then.revenue(), known, Palette.GOOD_MONEY,
                revenueDetail(sector), "what"));

        if (now.inputs() != 0 || then.inputs() != 0) {
            column.getChildren().add(bookLine(inputLabel(sector),
                    -now.inputs(), -then.inputs(), known, null,
                    inputsDetail(sector), "what"));
        }
        if (now.payroll() != 0 || then.payroll() != 0) {
            column.getChildren().add(bookLine("Wages",
                    -now.payroll(), -then.payroll(), known, null,
                    wagesDetail(sector), "who"));
        }
        if (now.electricity() != 0 || then.electricity() != 0) {
            column.getChildren().add(bookLine("Electricity",
                    -now.electricity(), -then.electricity(), known, null));
        }
        if (now.water() != 0 || then.water() != 0) {
            column.getChildren().add(bookLine("Water",
                    -now.water(), -then.water(), known, null));
        }
        if (now.maintenance() != 0 || then.maintenance() != 0) {
            column.getChildren().add(bookLine("Repairs",
                    -now.maintenance(), -then.maintenance(), known, null));
        }

        column.getChildren().add(bookTotal("Operating income",
                now.operatingIncome(), then.operatingIncome(), known,
                now.operatingIncome() < 0 ? Palette.BAD : null));
        column.getChildren().add(statementNote(
                "What the business made from trading, before it pays for the ground it "
                + "stands on or the money it borrowed."));

        column.getChildren().add(bookLine("Property tax",
                -now.propertyTax(), -then.propertyTax(), known,
                now.propertyTax() > 0 ? Palette.WARN : null));
        column.getChildren().add(bookLine("Interest on debt",
                -now.interest(), -then.interest(), known,
                now.interest() > 0 ? Palette.WARN : null));
        if (now.salesTaxPaid() != 0 || then.salesTaxPaid() != 0) {
            column.getChildren().add(bookLine("Sales tax remitted",
                    -now.salesTaxPaid(), -then.salesTaxPaid(), known,
                    now.salesTaxPaid() > 0 ? Palette.WARN : Palette.GOOD,
                    salesTaxDetail(sector), "how"));
        }

        column.getChildren().add(bookTotal("Profit before tax",
                now.preTaxIncome(), then.preTaxIncome(), known,
                now.preTaxIncome() < 0 ? Palette.BAD : null));

        column.getChildren().add(bookLine("Business tax",
                -now.tax(), -then.tax(), known, now.tax() > 0 ? Palette.WARN : null));
        column.getChildren().add(bookTotal("What it kept",
                now.netIncome(), then.netIncome(), known,
                now.netIncome() < 0 ? Palette.BAD : Palette.GOOD));

        /* ----------------------------- and the ratios ----------------------------- */
        column.getChildren().add(subHead("Read as ratios"));
        column.getChildren().add(statementLine("Net margin",
                String.format("%.1f%%", now.margin() * 100),
                now.margin() < 0 ? Palette.BAD
                        : now.margin() < .05 ? Palette.WARN : Palette.GOOD));
        if (now.revenue() > 0) {
            column.getChildren().add(statementLine("Wages take",
                    String.format("%.0f%% of revenue", now.payroll() / now.revenue() * 100)));
            column.getChildren().add(statementLine("Interest takes",
                    String.format("%.1f%% of revenue", now.interest() / now.revenue() * 100),
                    now.interest() / now.revenue() > .15 ? Palette.WARN : null));
        }
        if (now.operatingIncome() > 0 && now.interest() > 0) {
            double cover = now.operatingIncome() / now.interest();
            column.getChildren().add(statementLine("Interest cover",
                    String.format("%.1fx", cover),
                    cover < 1.5 ? Palette.BAD : cover < 3 ? Palette.WARN : Palette.GOOD));
            column.getChildren().add(statementNote(
                    "How many times over its trading profit covers its interest bill. "
                    + "Under one and the business is borrowing to pay its lenders."));
        }

        if (now.salesTaxPaid() < 0) {
            column.getChildren().add(statementNote(
                    "The sales tax line is a REFUND this month: the credit on what this "
                    + "sector bought came to more than the tax on what it sold. That is "
                    + "what zero-rating an export means, and the city pays it."));
        }

    }

    /* --------------------------- THE BALANCE SHEET --------------------------- */

    void balancePage(VBox column, Sector sector,
                             SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {

        boolean known = !then.isEmpty();

        column.getChildren().add(statementHead("What it owns"));
        column.getChildren().add(bookHead("as at " + CityCalendar.format(now.month())));

        column.getChildren().add(bookLine("Cash", now.cash(), then.cash(), known,
                now.cash() < 0 ? Palette.BAD : null));
        if (now.inventory() != 0 || then.inventory() != 0) {
            column.getChildren().add(bookLine("Stock on the shelf",
                    now.inventory(), then.inventory(), known, null));
        }
        column.getChildren().add(bookTotal("Current assets",
                now.cash() + now.inventory(), then.cash() + then.inventory(), known, null));

        column.getChildren().add(bookLine("Land", now.land(), then.land(), known, null));
        column.getChildren().add(bookLine("Buildings, at cost",
                now.buildings(), then.buildings(), known, null));
        column.getChildren().add(bookTotal("Everything it owns",
                now.totalAssets(), then.totalAssets(), known, null));

        column.getChildren().add(statementHead("What it owes, and what is left"));
        column.getChildren().add(bookLine("Loans outstanding",
                now.bondsPayable(), then.bondsPayable(), known,
                now.bondsPayable() > 0 ? Palette.WARN : null));
        column.getChildren().add(bookLine("Owners' equity",
                now.equity(), then.equity(), known,
                now.equity() < 0 ? Palette.BAD : null));
        column.getChildren().add(bookTotal("Owed and owned",
                now.bondsPayable() + now.equity(),
                then.bondsPayable() + then.equity(), known, null));

        /*
         * THE IDENTITY, SAID OUT LOUD. Equity is a plug in this model - it is
         * whatever assets less liabilities comes to - so the sheet cannot fail
         * to balance and printing "it balances" would be worthless. What is
         * worth saying is what that means: nobody put capital in, so equity is
         * the accumulated result of every month this business has traded.
         */
        column.getChildren().add(statementNote(
                "Owners' equity is what is left when the lenders are paid off: what the "
                + "founders started with, what its shareholders have subscribed since, and "
                + "every month's result — less what was paid out to them and what was "
                + "bought back. Who holds it, and what the market makes of it, is under "
                + "Its owners below."));

        if (now.equity() < 0) {
            column.getChildren().add(alert("It is worth less than it owes", String.format(
                    "Everything this business owns comes to %s and it owes %s. It is "
                    + "insolvent on paper and still trading, which the model allows: the "
                    + "credit side stops lending long before the accountants would stop "
                    + "the trading.",
                    tightMoney(toDollars(now.totalAssets())),
                    tightMoney(toDollars(now.bondsPayable())))));
        }

        /* ------------------------------ the ratios ------------------------------ */
        column.getChildren().add(subHead("Read as ratios"));

        double assets = now.totalAssets();
        double debtToAssets = assets > 0 ? now.bondsPayable() / assets : 0;
        column.getChildren().add(statementLine("Debt to assets",
                String.format("%.0f%%", debtToAssets * 100),
                debtToAssets > .7 ? Palette.BAD
                        : debtToAssets > .5 ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("Return on assets",
                String.format("%.2f%% a month",
                        assets > 0 ? now.netIncome() / assets * 100 : 0),
                now.netIncome() < 0 ? Palette.BAD : null));
        if (now.inventory() > 0) {
            column.getChildren().add(statementLine("Stock as a share of assets",
                    String.format("%.0f%%", now.inventory() / assets * 100)));
            column.getChildren().add(statementNote(
                    "Stock is held at what it would fetch today, so a price collapse "
                    + "shrinks this business's balance sheet without anything being sold."));
        }
        column.getChildren().add(statementNote(
                "Buildings are at what they cost to put up — cash plus materials at the "
                + "price of the day. Nothing here depreciates."));

        ownersBlock(column, Equity.indexOf(sector.key()), now.equity(), now.netIncome());
    }

    /**
     * Who owns a company, what a share is worth, and what it pays.
     *
     * Since 2026-09-10 (evening) every sector and the bank is a company with
     * shareholders - the city's households first, the world for what they
     * did not buy. See Equity. Shown as shares of the company rather than
     * counts of shares, because a company that has sold shares at book when
     * its book was small has millions of them and the count says nothing.
     */
    void ownersBlock(VBox column, int company, double bookEquity, double netIncome) {
        Equity register = ui.game.getEquity();
        if (company < 0 || register.getShares(company) <= 0) return;

        column.getChildren().add(statementHead("Its owners"));
        Exchange market = ui.game.getExchange();
        double abroad = register.foreignShare(company);
        double onDesk = register.getShares(company) > 0 ? Math.max(0, register.getDealerShares(company)) / register.getShares(company) : 0;
        column.getChildren().add(statementLine("Held by the city's households",
                String.format("%.0f%%", Math.max(0, 1 - abroad - onDesk) * 100),
                abroad < .5 ? Palette.GOOD : null));
        column.getChildren().add(statementLine("Held abroad",
                String.format("%.0f%%", abroad * 100),
                abroad > .5 ? Palette.WARN : null));
        if (onDesk > 0 && company != Equity.BANK) {
            column.getChildren().add(statementLine("On the bank's trading desk",
                    String.format("%.0f%%", onDesk * 100)));
        }
        column.getChildren().add(statementLine("A share is worth, on the books",
                tightMoney(toDollars(register.bookPerShare(company, bookEquity)), false)));
        /*
         * THE MARKET, since the exchange (2026-09-11). The desk's quote, the
         * yield at it and what the whole company is worth at it - the three
         * figures a shareholder reads before the book.
         */
        if (market.isOpen()) {
            double mid = market.mid(company);
            boolean dear = mid > market.fair(company) * (1 + Exchange.BUYBACK_TOLERANCE);
            boolean cheap = mid < market.fair(company) * (1 - Exchange.BUYBACK_TOLERANCE);
            column.getChildren().add(statementLine("The desk quotes it at",
                    tightMoney(toDollars(mid), false), dear ? Palette.WARN : cheap ? Palette.GOOD : null));
            column.getChildren().add(statementLine("...which yields, on the last year's dividend",
                    String.format("%.1f%%", market.yieldAt(register, company, market.ask(company)) * 100)));
            column.getChildren().add(statementLine("...and values the company at",
                    tightMoney(toDollars(market.marketCap(register, company)))));
        } else {
            column.getChildren().add(statementLine("Last sold at",
                    tightMoney(toDollars(register.getLastPrice(company)), false)));
            column.getChildren().add(statementNote("No market: the bank has no capital to make one."));
        }
        double paid = register.getDividendThisMonth(company);
        column.getChildren().add(statementLine("Dividend this month",
                tightMoney(toDollars(paid), false), paid > 0 ? Palette.GOOD : null));
        double special = market.getSpecialDividend(company);
        if (special > 0) {
            column.getChildren().add(statementLine("...of which a special dividend",
                    tightMoney(toDollars(special), false), Palette.GOOD));
        }
        double retired = register.getBoughtBackThisMonth(company);
        if (retired > 0 && register.getShares(company) + retired > 0) {
            column.getChildren().add(statementLine("Bought back and retired this month",
                    String.format("%.2f%% of the company", retired / (register.getShares(company) + retired) * 100)));
        }
        double split = market.getSplit(company);
        if (split > 1) {
            column.getChildren().add(statementNote(String.format("Split %,.0f for one this month: every holder's count by %,.0f, the price by the inverse.", split, split)));
        } else if (split > 0) {
            column.getChildren().add(statementNote(String.format("Consolidated one for %,.0f this month: every holder's count by the inverse, the price by %,.0f.", 1 / split, 1 / split)));
        }

        /*
         * WHAT A SHARE HAS BEEN WORTH, since 2026-09-11 - Jerus: "a history
         * of the stock price for each company, both in each industry, and in
         * the reports rail." The desk's quote and the register's reckoning,
         * per founding share so a split is not a cliff, from the month the
         * company listed. The same series the Reports tab draws; this is the
         * one company's, on its own page.
         */
        sharePriceChart(column, company);
        String regime = switch (register.getRegime(company)) {
            case NEW -> "new: every plan is part shares, no record yet";
            case GOOD -> "a good year: it raises ahead of its plans";
            case NORMAL -> "a normal year: it borrows for its plans";
            case BAD -> "a bad year: it does not go to the market";
        };
        column.getChildren().add(statementNote(String.format(
                "%s. It wants %.0f%% of its balance sheet as equity and pays out %.0f%% of a"
                + " profitable month. Raised %s from the households and %s abroad since founding;"
                + " paid them %s and %s.",
                regime.substring(0, 1).toUpperCase() + regime.substring(1),
                register.getTargetEquityShare(company) * 100, Equity.PAYOUT * 100,
                tightMoney(toDollars(register.getLifetimeRaisedHome(company))),
                tightMoney(toDollars(register.getLifetimeRaisedAbroad(company))),
                tightMoney(toDollars(register.getLifetimeDividendsHome(company))),
                tightMoney(toDollars(register.getLifetimeDividendsAbroad(company))))));
    }

    /**
     * One company's share price over the city's life: the quote against
     * what the register says a share is worth, both per founding share.
     */
    void sharePriceChart(VBox column, int company) {
        HistorySave h = ui.game.getHistorySave();
        String name = Equity.COMPANIES[company];
        double[] price = h.aligned(HistorySave.priceKey(name));
        double[] worth = h.aligned(HistorySave.valueKey(name));
        boolean any = false;
        for (double v : price) if (!Double.isNaN(v)) { any = true; break; }
        if (!any) return;
        column.getChildren().add(statementHead("What a share has been worth"));
        column.getChildren().add(trendChart(
                new String[] {"The desk's quote", "What the register reckons"},
                new double[][] {price, worth},
                new String[] {Palette.ACCENT, Palette.RAMP_REST}));
        double factor = ui.game.getExchange().getSplitFactor(company);
        column.getChildren().add(statementNote(
                "Per founding share: a split moves every holder's count and the price "
                + "together, so the line does not"
                + (Math.abs(factor - 1) > 1e-9
                        ? String.format(" — one founding share is %s shares today.",
                                factor >= 1 ? String.format("%,.0f", factor) : String.format("%.4f", factor))
                        : ".")
                + " Where the quote sits above what the register reckons, buyers the desk "
                + "could not fill have lifted it; below, the desk is long and finding them."));
    }

    /* ---------------------------- CASH AND CREDIT ---------------------------- */

    void cashAndDebtPage(VBox column, Sector sector,
                                 SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {

        boolean known = !then.isEmpty();
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        String key = sector.key();

        /*
         * A REAL RECONCILIATION, not a plausible-looking list.
         *
         * Sector cash moves in exactly six ways and this is all six: what it
         * kept after tax, what it borrowed, what it repaid, what it spent on
         * its own buildings, the sales tax it remitted, and anything the city
         * paid in as a subsidy. Finding all six took a harness - the first
         * version had four and was out by the price of a Food Processing Plant.
         * If they do not close the gap between opening and closing cash the
         * residual gets its own line rather than being folded into a total: a
         * statement that balances by hiding its difference is not a statement.
         */
        column.getChildren().add(statementHead("Where the cash went"));
        column.getChildren().add(bookHead(CityCalendar.format(now.month())));

        column.getChildren().add(bookLine("Cash at the start",
                now.openingCash(), then.openingCash(), known, null));
        column.getChildren().add(bookLine("Kept from trading",
                now.netIncome(), then.netIncome(), known,
                now.netIncome() < 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(bookLine("Borrowed",
                now.borrowed(), then.borrowed(), known,
                now.borrowed() > 0 ? Palette.WARN : null));
        column.getChildren().add(bookLine("Loans repaid",
                -now.repaid(), -then.repaid(), known, null));
        if (now.spentOnBuildings() != 0 || then.spentOnBuildings() != 0) {
            column.getChildren().add(bookLine(
                    now.spentOnBuildings() >= 0 ? "Spent on its own premises"
                                                : "Sold buildings back",
                    -now.spentOnBuildings(), -then.spentOnBuildings(), known,
                    now.spentOnBuildings() < 0 ? Palette.GOOD : null));
        }
        // Sales tax is NOT a line here any more - it is on the income statement
        // above, so it is already inside "What it kept". See SectorBooks.
        if (now.fromTheCity() != 0 || then.fromTheCity() != 0) {
            column.getChildren().add(bookLine("Subsidy from the city",
                    now.fromTheCity(), then.fromTheCity(), known, Palette.ACCENT));
        }
        if (now.depositInterest() != 0 || then.depositInterest() != 0) {
            column.getChildren().add(bookLine("Interest on its bank balance",
                    now.depositInterest(), then.depositInterest(), known, Palette.GOOD));
        }
        // The four lines the reconciliation had and the screen did not,
        // until 2026-09-10 (evening): what its creditors forgave, what it
        // moved abroad or brought home, what its owners put in, and what it
        // paid them. Without them "Not accounted for" stayed at zero while
        // the lines on the screen did not add up to the cash at the end.
        if (now.forgiven() != 0 || then.forgiven() != 0) {
            column.getChildren().add(bookLine("Forgiven by its creditors",
                    now.forgiven(), then.forgiven(), known, Palette.WARN));
        }
        if (now.investedAbroad() != 0 || then.investedAbroad() != 0) {
            column.getChildren().add(bookLine(
                    now.investedAbroad() >= 0 ? "Sent abroad for the world's rate" : "Brought home from abroad",
                    -now.investedAbroad(), -then.investedAbroad(), known, null));
        }
        if (now.equityRaised() != 0 || then.equityRaised() != 0) {
            column.getChildren().add(bookLine("Raised from its shareholders",
                    now.equityRaised(), then.equityRaised(), known, Palette.ACCENT));
        }
        if (now.dividendsPaid() != 0 || then.dividendsPaid() != 0) {
            column.getChildren().add(bookLine("Paid to its shareholders",
                    -now.dividendsPaid(), -then.dividendsPaid(), known, null));
        }

        double gap = now.unexplained();
        if (Math.abs(toDollars(gap)) > 1) {
            column.getChildren().add(bookLine("Not accounted for",
                    gap, then.unexplained(), known, Palette.WARN));
        }

        column.getChildren().add(bookTotal("Cash at the end",
                now.cash(), then.cash(), known,
                now.cash() < 0 ? Palette.BAD : null));

        if (Math.abs(toDollars(gap)) > 1) {
            column.getChildren().add(statementNote(
                    "The lines above are every way this model moves a sector's cash. A "
                    + "residual means something else touched it — worth knowing about "
                    + "rather than worth hiding. SectorBooksCheck audits this identity on "
                    + "every sector every month, so a line appearing here is news."));
        }
        if (now.cash() < 0) {
            column.getChildren().add(statementNote(
                    "A negative balance is not an error. A maturing loan takes cash under "
                    + "before the replacement is written, which is what a business with no "
                    + "spare cash actually does."));
        }

        /* -------------------------------- credit -------------------------------- */
        column.getChildren().add(statementHead("What it borrows on"));
        column.getChildren().add(statementLine("It pays",
                String.format("%.2f%% a year", now.rate() * 100),
                now.rate() > credit.getRiskFreeRate() * 2 ? Palette.BAD
                        : now.rate() > credit.getRiskFreeRate() * 1.4
                                ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementNote(String.format(
                "The city itself borrows at %.2f%%, so this sector is paying %.2f points "
                + "over. The spread is priced off its leverage, not off its profits.",
                credit.getRiskFreeRate() * 100,
                credit.getSpread(key) * 100)));
        column.getChildren().add(statementLine("Leverage",
                String.format("%.2f", now.leverage()),
                now.leverage() > .7 ? Palette.BAD
                        : now.leverage() > .5 ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("Principal outstanding",
                tightMoney(toDollars(credit.getPrincipal(key)), false)));
        column.getChildren().add(statementLine("Loans running",
                String.valueOf(credit.getLoanCount(key))));
        column.getChildren().add(statementLine("Interest this month",
                tightMoney(toDollars(credit.getMonthlyInterest(key)), false),
                credit.getMonthlyInterest(key) > 0 ? Palette.WARN : null));

        if (credit.getWrittenOffTotal(key) > 0) {
            column.getChildren().add(subHead("What its lenders have lost"));
            column.getChildren().add(statementLine("Written off this month",
                    tightMoney(toDollars(now.writtenOff()), false),
                    now.writtenOff() > 0 ? Palette.BAD : null));
            column.getChildren().add(statementLine("Written off in all",
                    tightMoney(toDollars(credit.getWrittenOffTotal(key)), false),
                    Palette.BAD));
            column.getChildren().add(statementLine("Times restructured",
                    String.valueOf(credit.getRestructureCount(key))));
        }

        if (credit.isBorrowingBlocked(key)) {
            column.getChildren().add(alert("It cannot borrow", String.format(
                    "This sector defaulted, and no lender will write it a loan for another "
                    + "%d months. It can still build whatever its own cash covers, and "
                    + "nothing else — which is why a sector that goes under tends to stay "
                    + "small long after the month that broke it.",
                    credit.getBlockedMonths(key))));
        }
    }

    /* ------------------------------ THE INVESTORS ------------------------------
     *
     * WHAT THE BUSINESS IS THINKING, which is the one thing about these sectors
     * a player cannot see anywhere and can very much act on. Every month each
     * business decides whether to expand, and the decision it reached was
     * already recorded - it was just never shown outside a one-line banner on
     * the land screen.
     *
     * The conditions are here too, because a plan is only readable against the
     * rules it was made under: nobody builds anything that cannot cover its own
     * interest, nobody builds while the ground is full, and a sector that loses
     * money for long enough starts selling its buildings back.
     * -------------------------------------------------------------------- */
    void investorPage(VBox column, Sector sector,
                              SectorBooks.SectorMonth now) {

        BusinessInvestment plans = ui.game.getBusinessInvestment();
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        String key = sector.key();

        /* ------------------------- what it decided ------------------------- */
        column.getChildren().add(statementHead("What it decided this month"));

        String said = ui.game.getLastInvestment(key);
        boolean holding = said != null && said.startsWith("Holding");
        boolean declined = said != null && said.startsWith("Declined");
        column.getChildren().add(sentence(
                said == null || said.isEmpty()
                        ? "Nothing recorded. This sector was not asked, or the city has not "
                          + "closed a month since it was loaded."
                        : said,
                said == null || said.isEmpty() ? Palette.TEXT_MUTED
                        : declined ? Palette.BAD
                        : holding ? Palette.TEXT_MUTED : Palette.GOOD));

        if (sector == ui.game.getSectors().retail()) {
            String bank = ui.game.getLastInvestment("Bank");
            if (bank != null && !bank.isEmpty()) {
                column.getChildren().add(subHead("And its bank branches, separately"));
                column.getChildren().add(sentence(bank, Palette.TEXT_MUTED));
                column.getChildren().add(statementNote(
                        "The bank's counters are retail's money and not retail's shop "
                        + "decision, so the two are asked separately and filed separately."));
            }
        }

        if (ui.game.getLandBlockedSectors().contains(key)) {
            column.getChildren().add(alert("It is waiting on ground",
                    "This business wants to build and there is nowhere to put it. It is the "
                    + "one refusal on this page you can personally clear — buy a plot on "
                    + "the land tab and it will build next month."));
        }

        /* --------------------------- the conditions --------------------------- */
        column.getChildren().add(statementHead("What it takes to get a yes"));

        column.getChildren().add(statementLine("A project must earn",
                String.format("%.2fx its interest", BusinessInvestment.PROFIT_OVER_INTEREST),
                Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(String.format(
                "At this sector's own rate of %.2f%%, on whatever it has to borrow after "
                + "its cash is spent. A plan that fails this is trimmed down until it "
                + "passes, and dropped if even one unit cannot carry it.", now.rate() * 100)));

        column.getChildren().add(statementLine("It plans", String.format(
                "%.0f months ahead", BusinessInvestment.PLANNING_HORIZON)));
        column.getChildren().add(statementLine("Off a trend of", String.format(
                "%d months", BusinessInvestment.TREND_WINDOW)));
        /*
         * PEOPLE A MONTH, not a percentage. getPopulationGrowth() is the average
         * monthly CHANGE in headcount over the window - the first draft of this
         * line multiplied it by a hundred and printed "-25,063.64% a month",
         * which is the kind of figure that makes a player distrust every other
         * number on the screen.
         */
        double trend = plans.getPopulationGrowth();
        column.getChildren().add(statementNote(String.format(
                "Population is moving %s%s people a month on that window, which is the "
                + "demand every one of these plans is drawn against.",
                trend >= 0 ? "+" : "-", people(Math.abs(trend)))));
        column.getChildren().add(statementLine("It builds toward", String.format(
                "%.0f%% spare capacity", BusinessInvestment.TARGET_HEADROOM * 100)));
        column.getChildren().add(statementLine("One order at a time",
                BusinessInvestment.MAX_CONCURRENT_ORDERS == 1 ? "yes" : "no"));
        column.getChildren().add(statementNote(String.format(
                "And never more than %.0f months of the city's construction output in one "
                + "order — a business cannot jam the queue for everybody else.",
                BusinessInvestment.MAX_ORDER_MONTHS)));

        /* ------------------------- and what stops it ------------------------- */
        column.getChildren().add(statementHead("What stops it"));

        column.getChildren().add(statementLine("Cash it can spend",
                tightMoney(toDollars(now.cash()), false),
                now.cash() < 0 ? Palette.BAD : null));
        column.getChildren().add(statementLine("Can it borrow the rest",
                credit.isBorrowingBlocked(key)
                        ? "no — " + credit.getBlockedMonths(key) + " months of ban left"
                        : "yes",
                credit.isBorrowingBlocked(key) ? Palette.BAD : Palette.GOOD));

        int lossMonths = plans.getLossMonths(key);
        column.getChildren().add(statementLine("Months losing money",
                lossMonths == 0 ? "none"
                        : lossMonths + (lossMonths == 1 ? " month" : " months"),
                lossMonths >= BusinessInvestment.RETIREMENT_LOSS_MONTHS ? Palette.BAD
                        : lossMonths > 0 ? Palette.WARN : Palette.GOOD));

        if (lossMonths >= BusinessInvestment.RETIREMENT_LOSS_MONTHS) {
            column.getChildren().add(alert("It is scrapping buildings", String.format(
                    "Six straight months of losses, so this business has stopped expanding "
                    + "and started selling. It sheds capacity it is not using — anything "
                    + "over %.0f%% slack — and never more than %.0f%% of what it owns in "
                    + "one month, so the retreat takes a while and is visible while it "
                    + "happens.",
                    BusinessInvestment.RETIREMENT_SLACK * 100,
                    BusinessInvestment.MAX_RETIREMENT_FRACTION * 100)));
        } else if (lossMonths > 0) {
            column.getChildren().add(statementNote(String.format(
                    "At %d it stops expanding and starts selling buildings back. It is at "
                    + "%d. A single profitable month resets the count.",
                    BusinessInvestment.RETIREMENT_LOSS_MONTHS, lossMonths)));
        } else {
            column.getChildren().add(statementNote(
                    "A business that loses money for six straight months stops expanding "
                    + "and starts selling its buildings back to the city. This one is not "
                    + "close."));
        }

        if (sector == ui.game.getSectors().construction()) {
            column.getChildren().add(statementNote(String.format(
                    "The builders are the exception: they expand off their order book "
                    + "rather than off population, and only once it is more than %.0f "
                    + "months deep.", BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING)));
        }

        column.getChildren().add(statementNote(
                "None of this is yours to set. These are private companies deciding for "
                + "themselves — what you control is the ground they can buy, the tax they "
                + "pay, the wages they compete against and the rate they borrow at."));
    }

    /* ----------------------------- WHAT IT DOES -----------------------------
     *
     * The one page that used to be written six times over: a mine is
     * measured in tonnes and a shop in customers, and the old six methods
     * each read their own handler. SINCE THE SECTOR TEMPLATE (2026-09-11)
     * the sector writes its own page - Sector.operations(Game) returns the
     * lines, as data, and this draws them. A seventh sector is a seventh
     * page for free, and the screen and the console printer read the same
     * lines, so they cannot disagree.
     * -------------------------------------------------------------------- */
    void operationsPage(VBox column, Sector sector) {
        for (Sector.Line line : sector.operations(ui.game)) {
            switch (line.kind()) {
                case HEAD -> column.getChildren().add(statementHead(line.label()));
                case NOTE -> column.getChildren().add(statementNote(line.label()));
                default -> column.getChildren().add(statementLine(line.label(), line.value(),
                        toneColour(line.tone())));
            }
        }
    }

    /** The palette colour a sector's line asked for, or none. */
    static String toneColour(Sector.Line.Tone tone) {
        if (tone == null) return null;
        return switch (tone) {
            case GOOD  -> Palette.GOOD;
            case WARN  -> Palette.WARN;
            case BAD   -> Palette.BAD;
            case MUTED -> Palette.TEXT_SPENT;
            case HEAD  -> Palette.TEXT_HEAD;
            default    -> null;
        };
    }
}
