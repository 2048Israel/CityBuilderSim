package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * The sector economy: every business in the city as a card, and each one's
 * five pages - what goes in and what comes out, the income statement with
 * last month beside it, the balance sheet and its owners, cash and credit,
 * and what its investors decided and why.
 *
 * Split out of UserInterface on 2026-09-18 (the shell still reads which
 * sector and page are open, openSector and sectorPage, for the rail and the
 * scroll memory; other screens open a sector's books through
 * openSectorBooks()). REDRAWN IN BUILD'S STYLE in 0.7.30 (the project's
 * spec-sectors-0730.md): the list was fifteen 560 px rows of a figure and a
 * blurb, and each page a statement column of label-and-figure lines under
 * grey paragraphs. The pictures lead now - fifteen cards under four figures,
 * each with a running-at bar and its investors' word; a business as inputs →
 * the plant (its rate as a ring, the six throttles as a cascade) → outputs; a
 * waterfall over the income statement; the balance sheet as two bars and an
 * owners card; the month's cash as a bridge; and the investors' decision as
 * one line with each building's first gate. The statements stay, whole, and
 * still open into their parts; every paragraph is behind an (i).
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

       THE LIST AS CARDS (0.7.30). Three columns in Build's market order, each
       card the business's net income and its last two years, its workers, how
       much of its plant runs and what cuts it most, and its investors' word
       for the month in a kind - building, selling, land, staff, a licence,
       ore, supply, credit, money, enough - with the kind's icon and colour
       (BuildCard.wordKind()). Over them the city's four: what they kept,
       how many lost, who works there, and the range they run at with the
       city's throttles named, a door to the Build category that relieves the
       thinnest of them.
       ===================================================================== */

    /** Which sector's books are open, or null for the list. */
    Sector openSector = null;
    /** The page a business opens on, and falls back to for a page it does not know. */
    static final String SECTOR_HOME   = "Operations";
    String sectorPage = SECTOR_HOME;

    /** Whether every card on the list is open to its second row (0.7.4) - kept while the game runs, like the page, and never saved. */
    boolean sectorsExpanded = false;

    /** The five pages, in the strip's order: the names other screens open a business's books by. */
    static final String[] SECTOR_PAGES =
            {"Operations", "Income", "Balance sheet", "Cash & debt", "Investors"};

    /** Each page's icon on its chip (0.7.30): the plant, the coin, the ledger, the bank, the investors. */
    static final String[] PAGE_ICONS = {Icons.INDUSTRY, Icons.COIN, Icons.FINANCES, Icons.BANK, Icons.SECTOR};

    /** Which "details" folds are open - kept while the game runs, not saved (0.7.30). */
    private final java.util.Set<String> detailsOpen = new java.util.HashSet<>();

    /** Which statement lines are open, by sector and then by label (0.7.30: a waterfall's bar opens its line). */
    private final java.util.Map<String, java.util.Set<String>> linesOpen = new java.util.HashMap<>();

    /** The month each view was last drawn at: its figures count up and its ring sweeps once a month, not on every redraw. */
    private final java.util.Map<String, Integer> drawnAt = new java.util.HashMap<>();

    /** How much of the stage a business's fixed frame takes above its page's scroller - the head, the five figures, the investors' line, the pages, and their gaps - until the frame is laid out and its own height is read (0.7.30; frameOver()). */
    static final double FRAME_CHROME = 276;

    /** ...and the list's: its head and its four figures (0.7.30). */
    static final double LIST_CHROME = 190;

    /** What the menu spends around the frame and the page: its padding over and under them and the gap between, and the four pixels of slack its height is held to (UserInterface's rootMenu) - so the page is what is left under the frame as laid out (0.7.30). */
    static final double STAGE_REST = 36;

    /** How long a figure takes to count from last month's to this month's (0.7.30). */
    static final double COUNT_MILLIS = 400;

    /** ...and the plant's ring to sweep up from nothing (0.7.30). */
    static final double SWEEP_MILLIS = 700;

    /** Open one business's books from somewhere else in the game. */
    void openSectorBooks(Sector sector, String page) {
        openSector = sector;
        sectorPage = page;
        ui.innerScrollAt.remove("showSectorMenu:body");
        showSectorMenu();
    }

    /** One of the open business's pages, at its top. */
    void open(String page) {
        sectorPage = page;
        ui.innerScrollAt.remove("showSectorMenu:body");
        showSectorMenu();
    }

    /** Back to the list, at its top. */
    void backToList() {
        openSector = null;
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
        listScreen();
    }

    /** Whether this draw of a view is the first since its month changed - the count-ups and the sweep run then and on no other redraw. */
    private boolean freshMonth(String view) {
        Integer was = drawnAt.put(view, ui.game.getMonth());
        return was == null || was != ui.game.getMonth();
    }

    /** The fixed frame over a scrolling page, at the page's width: the shape every redrawn tab has (Infrastructure's, 0.7.29). */
    private void frameOver(VBox frame, VBox page, double chrome) {
        frame.setMaxWidth(PAGE_WIDE);
        frame.setFillWidth(true);
        frame.setPadding(new javafx.geometry.Insets(0, 18, 4, 18));
        body = ui.scrolled(page, chrome);
        // ...AND AS TALL AS WHAT IS LEFT UNDER THE FRAME, whatever the frame's
        // height: the investors' line takes a second line for Retail's bank
        // branches, or for a long word, and a constant would leave the whole
        // menu a scrollbar of its own. `chrome` holds until the frame is laid out.
        body.prefHeightProperty().unbind();
        body.prefHeightProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                () -> Math.max(260, ui.menuScroller.getHeight()
                        - (frame.getHeight() > 0 ? frame.getHeight() + STAGE_REST : chrome)),
                ui.menuScroller.heightProperty(), frame.heightProperty()));
        final javafx.scene.control.ScrollPane scroller = body;
        page.prefWidthProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                () -> Math.min(PAGE_WIDE, Math.max(320, scroller.getViewportBounds().getWidth())),
                scroller.viewportBoundsProperty()));
        ui.rootMenu.getChildren().addAll(frame, body);
    }

    /** The page's scroller. */
    private javafx.scene.control.ScrollPane body;

    /* ---------------------------------------------------------------------
       THE LIST (0.7.30)
       --------------------------------------------------------------------- */

    /** What the list's (i) holds: the old caption, and what a card does. */
    static final String LIST_INFO = "What the city's businesses kept this month, after tax - each card one business, "
            + "with its net income over the last two years, its workers, how much of what its plants could make "
            + "they made, and what cuts that most. The line under it is what its investors decided this month.\n\n"
            + "A card opens its books; its investors' line opens what they decided and why. The cards are in "
            + "the order Build lays its market out, each named with the Build group its buildings are under.";

    /** The empty books' sentence (the old list's alert). */
    static final String NOTHING_YET_LIST = "The sector books are written when a month closes. This city has not "
            + "closed one since it was loaded - press the arrow once and every statement behind these cards fills in.";

    /** ...and a business's (the old page's alert). */
    static final String NOTHING_YET_SECTOR = "This city has not closed a month since it was loaded, so there is no "
            + "statement to draw. Press the arrow once.";

    /** The list's one line while no business has a word for the month (0.7.34), in place of each card's own. */
    static final String NO_WORD_YET_LIST = "Nothing recorded since the city was loaded or founded: a month on, each "
            + "card says what its investors think.";

    void listScreen() {
        SectorBooks books = ui.game.getSectorBooks();
        List<Sector> order = marketOrder();
        boolean animate = freshMonth("list");

        VBox page = widePage();
        if (books.isEmpty()) {
            page.getChildren().add(alertLine(Icons.ALERT, Palette.WARN, "Nothing recorded yet", NOTHING_YET_LIST, null));
        }
        // SAID ONCE (0.7.34): after a load, fourteen cards in fifteen said "no word yet" each.
        boolean quiet = noWordYet(ui.game, order);
        if (quiet) page.getChildren().add(alertLine(Icons.SECTOR, Palette.TEXT_LABEL, NO_WORD_YET_LIST, null, null));
        // Every series once, for the sparklines: HistorySave's
        // netIncome:<sector> since 0.7.4.
        java.util.Map<String, List<? extends Number>> series = ui.game.getHistorySave().seriesByName();
        GridPane grid = equalColumns(3, Palette.GAP_LOOSE);
        int i = 0;
        for (Sector s : order) {
            VBox card = sectorCard(s, books.get(s), books.previous(s),
                    series.get(HistorySave.netIncomeKey(s.key())), animate, quiet);
            GridPane.setFillHeight(card, true);
            grid.add(card, i % 3, i / 3);
            i++;
        }
        page.getChildren().add(grid);

        HBox head = pageHead("Sector economy", Palette.BUSINESS, null, null, LIST_INFO,
                stepChip(sectorsExpanded ? "Show less" : "Show more", () -> {
                    sectorsExpanded = !sectorsExpanded;
                    showSectorMenu();
                }, false));
        VBox frame = new VBox(Palette.GAP, head, listVitals(books, order, animate));
        frameOver(frame, page, LIST_CHROME);
    }

    /**
     * Whether no business has a word for the month (0.7.34): every sector's
     * Game.getLastInvestment() empty, as it is from a load or a founding
     * until a month runs. Investors on site are not a word: a card showing
     * them still shows them.
     */
    static boolean noWordYet(Game game, List<Sector> order) {
        for (Sector s : order) {
            if (!BuildCard.sectorInvestors(game, s).word().isEmpty()) return false;
        }
        return true;
    }

    /** The sectors in Build's market order: by the group their own buildings are under (BuildCard.groupRank()), then as Sectors lists them. */
    List<Sector> marketOrder() {
        List<Sector> all = new ArrayList<>(ui.game.getSectors().all());
        java.util.Map<Sector, Integer> rank = new java.util.HashMap<>();
        for (Sector s : all) rank.put(s, BuildCard.groupRank(BuildCard.groupOf(ui.game, s)));
        all.sort((a, b) -> Integer.compare(rank.get(a), rank.get(b)));
        return all;
    }

    /**
     * The list's four figures: what they all kept, with its move on last
     * month; how many lost, the two that lost most named; who works there,
     * against the posts; and the range they run at, over every business with
     * a plant and posts, with the city's throttles that cut it named - a door
     * to the Build category that relieves the thinnest (D13).
     */
    HBox listVitals(SectorBooks books, List<Sector> order, boolean animate) {
        ListFigures l = listFigures(books, order);

        // KEPT, counted up from last month's on a new month.
        VBox keptCell = kpi("KEPT", m(l.kept()) + "/mo", l.moved(), l.kept() < 0 ? Palette.BAD : Palette.TEXT_HEAD,
                l.keptInfo(), null, null);
        noteTone(keptCell, l.moveTone());
        if (animate && l.compared()) countUp(figureOf(keptCell), l.before(), l.kept(), v -> m(v) + "/mo");

        // LOSING: the two that lose most, and the rest behind the (i).
        VBox losingCell = kpi("LOSING", l.losing().size() + " of " + order.size(), l.losers(),
                l.losing().isEmpty() ? Palette.TEXT_HEAD : Palette.BAD, l.losingInfo(), null, null);

        VBox workCell = kpi("WORKERS", people(l.workers()), String.format("of %,d posts", l.posts()), Palette.TEXT_HEAD,
                null, null, null);

        // RUNNING AT: the range over every business with a plant and posts, and the city's throttles.
        final String category = l.door();
        VBox runCell = kpi("RUNNING AT", l.range(), l.throttles(), Palette.TEXT_HEAD, RUNNING_INFO,
                category == null ? null : "Build › " + category + ": the thinnest throttle",
                category == null ? null : () -> ui.buildScreen.openCategory(category));
        return vitalsBar(keptCell, losingCell, workCell, runCell);
    }

    /**
     * The list's four figures as words, off the books and the sectors' plants
     * - a pure read, for the strip and the probes: what they kept and their
     * move, the sectors losing and the words naming them, the workers and
     * posts, the range they run at, the city's throttles under 100% with
     * their ranges, and the Build category the strip's door opens (D13).
     */
    record ListFigures(double kept, double before, boolean compared, String moved, String moveTone, String keptInfo,
                       List<Sector> losing, String losers, String losingInfo, double workers, int posts,
                       String range, String throttles, String door) { }

    static ListFigures listFigures(SectorBooks books, List<Sector> order) {
        double total = 0, before = 0, workers = 0;
        int posts = 0;
        List<Sector> losing = new ArrayList<>();
        for (Sector s : order) {
            SectorBooks.SectorMonth now = books.get(s), then = books.previous(s);
            total += now.netIncome();
            before += then.netIncome();
            workers += s.getWorkers();
            posts += s.getPostsOffered();
            if (now.netIncome() < 0 && toDollars(now.netIncome()) <= -.5) losing.add(s);
        }
        losing.sort((a, b) -> Double.compare(books.get(a).netIncome(), books.get(b).netIncome()));
        double move = total - before;
        boolean compared = books.hasComparatives();
        String moved = !compared ? "after tax"
                : Math.abs(toDollars(move)) < .5 ? "no change on last month"
                : (move > 0 ? "+" : "-") + m(Math.abs(move)) + " on last month";
        String moveTone = !compared || Math.abs(toDollars(move)) < .5 ? Palette.TEXT_MUTED : move > 0 ? Palette.GOOD : Palette.BAD;
        String keptInfo = compared ? String.format("Last month %s. Business tax is charged on each company's own profit and "
                + "never refunded on a loss, so a city where half the sectors lose money still collects "
                + "from the other half.", m(before)) : null;
        StringBuilder all = new StringBuilder();
        for (Sector s : losing) {
            if (all.length() > 0) all.append("\n");
            all.append(s.label()).append("  ").append(m(books.get(s).netIncome()));
        }
        String named = losing.isEmpty() ? "every business made money"
                : losing.size() == 1 ? losing.get(0).label() + " " + m(books.get(losing.get(0)).netIncome())
                : losing.get(0).label() + " " + m(books.get(losing.get(0)).netIncome()) + ", "
                  + losing.get(1).label() + " " + m(books.get(losing.get(1)).netIncome())
                  + (losing.size() > 2 ? " and " + (losing.size() - 2) + " more" : "");

        double lo = Double.NaN, hi = Double.NaN;
        double[] thin = { Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN };
        double[] thick = { Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN };
        for (Sector s : order) {
            SectorFlow.Plant p = SectorFlow.plant(s);
            if (p.none() || p.posts() <= 0) continue;
            lo = Double.isNaN(lo) ? p.rate() : Math.min(lo, p.rate());
            hi = Double.isNaN(hi) ? p.rate() : Math.max(hi, p.rate());
            for (int k = 1; k < thin.length; k++) {
                double r = p.ratios()[k];
                thin[k] = Double.isNaN(thin[k]) ? r : Math.min(thin[k], r);
                thick[k] = Double.isNaN(thick[k]) ? r : Math.max(thick[k], r);
            }
        }
        StringBuilder throttles = new StringBuilder();
        for (int k = 1; k < thin.length; k++) {
            if (!(thin[k] < .995)) continue;
            if (throttles.length() > 0) throttles.append(" · ");
            String a = BuildScreen.pct(thin[k]), b = BuildScreen.pct(thick[k]);
            throttles.append(SectorFlow.THROTTLES[k]).append(' ').append(a.equals(b) ? a : a.replace("%", "") + "–" + b);
        }
        // D13: the door goes to the Build category that relieves the thinnest of power, the road and health.
        String go = null;
        double least = Double.POSITIVE_INFINITY;
        String[][] doors = { { "1", "Utilities" }, { "3", "Roads & transit" }, { "4", "Healthcare" } };
        for (String[] d : doors) {
            double r = thin[Integer.parseInt(d[0])];
            if (Double.isFinite(r) && r < least && r < .995) { least = r; go = d[1]; }
        }
        String range = Double.isNaN(lo) ? "—" : BuildScreen.pct(lo).equals(BuildScreen.pct(hi)) ? BuildScreen.pct(lo)
                : BuildScreen.pct(lo).replace("%", "") + "–" + BuildScreen.pct(hi);
        String says = Double.isNaN(lo) ? "no plant standing" : throttles.length() == 0 ? "nothing in the city cuts it" : throttles.toString();
        return new ListFigures(total, before, compared, moved, moveTone, keptInfo, losing, named,
                losing.isEmpty() ? null : "Losing money this month, after tax:\n" + all, workers, posts, range, says, go);
    }

    /** RUNNING AT's (i). */
    static final String RUNNING_INFO = "How much of what its plants could make each business made this month: its "
            + "posts filled, times the power, the water, the road and the health the city hands it, times its own "
            + "vans. The six multiply - two thin ones cut more than either alone. The figure is the range over every "
            + "business with a plant and posts; under it, those of the five after the posts that are under 100% "
            + "somewhere, and their range.\n\n"
            + "A click goes to the Build category that relieves the thinnest of power, the road and health.";

    /**
     * One business on the list (0.7.30): its icon, its name with the Build
     * group under it and its blurb on hover, what it kept and its move; its
     * last two years, its workers, and how much of its plant runs with what
     * cuts it most; its investors' word in a kind - a click there opens what
     * they decided - and, with the list opened, five figures more. A click
     * anywhere else opens its operations. While no business has a word
     * (`quiet`, 0.7.34), a card with none keeps its word's line empty.
     */
    VBox sectorCard(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then,
                    List<? extends Number> netIncomeSeries, boolean animate, boolean quiet) {

        Label name = new Label(sector.label());
        name.setWrapText(true);
        name.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        Tooltip whole = new Tooltip(sector.blurb() == null || sector.blurb().isBlank() ? sector.label() : sector.blurb());
        whole.setWrapText(true);
        whole.setMaxWidth(420);
        whole.setShowDelay(Duration.millis(300));
        Tooltip.install(name, whole);
        Label group = new Label(groupWords(sector));
        group.setWrapText(true);
        group.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        VBox names = new VBox(1, name, group);
        names.setMinWidth(0);
        HBox.setHgrow(names, Priority.ALWAYS);

        Label figure = new Label(m(now.netIncome()) + "/mo");
        figure.setMinWidth(Region.USE_PREF_SIZE);
        figure.setStyle(Palette.figure(Palette.SIZE_LEAD, now.netIncome() < 0 ? Palette.BAD : Palette.TEXT_HEAD));
        if (animate && !then.isEmpty()) countUp(figure, then.netIncome(), now.netIncome(), v -> m(v) + "/mo");
        double move = now.netIncome() - then.netIncome();
        Label change = new Label(then.isEmpty() ? "no month to compare"
                : Math.abs(toDollars(move)) < .5 ? "no change"
                : (move >= 0 ? "+" : "-") + m(Math.abs(move)));
        // No move is no news (0.7.21): "+$0" read green.
        change.setStyle(Palette.words(Palette.SIZE_CAPTION,
                then.isEmpty() || Math.abs(toDollars(move)) < .5 ? Palette.TEXT_SPENT
                        : move < 0 ? Palette.BAD : Palette.GOOD));
        VBox right = new VBox(0, figure, change);
        right.setAlignment(Pos.TOP_RIGHT);
        right.setMinWidth(110);

        HBox top = new HBox(10, iconSquare(Icons.ofSector(sector), Palette.BUSINESS, 34, 18), names, right);
        top.setAlignment(Pos.TOP_LEFT);

        // Row 2: two years of net income, the workers, and the plant.
        SectorFlow.Plant plant = SectorFlow.plant(sector);
        Label workers = new Label(String.format("%,.0f workers", sector.getWorkers()));
        workers.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        workers.setMinWidth(Region.USE_PREF_SIZE);
        VBox running = new VBox(3);
        running.setMinWidth(0);
        HBox.setHgrow(running, Priority.ALWAYS);
        running.getChildren().add(caption(runningWords(plant), plant.none() ? Palette.TEXT_SPENT : Palette.TEXT_LABEL));
        if (!plant.none()) running.getChildren().add(bar(Double.isFinite(plant.let()) ? plant.let() : plant.rate(), Palette.BUSINESS, 120, 4));
        HBox middle = new HBox(12, sparkline(netIncomeSeries), workers, running);
        middle.setAlignment(Pos.CENTER_LEFT);

        BuildCard.SectorInvestors si = BuildCard.sectorInvestors(ui.game, sector);
        Node word = investorsMini(si, quiet && si.kind() == BuildCard.WordKind.NONE);

        VBox card = new VBox(8, top, middle, word);
        if (sectorsExpanded) card.getChildren().add(sectorCardMore(sector, now));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMaxHeight(Double.MAX_VALUE);
        String rest = "-fx-padding: 12; -fx-cursor: hand; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: ";
        card.setStyle(rest + Palette.EDGE + ";");
        card.setOnMouseEntered(e -> card.setStyle(rest + Palette.ACCENT + ";"));
        card.setOnMouseExited(e -> card.setStyle(rest + Palette.EDGE + ";"));
        card.setOnMouseClicked(e -> {
            openSector = sector;
            sectorPage = SECTOR_HOME;
            ui.innerScrollAt.remove("showSectorMenu:body");
            showSectorMenu();
        });
        return card;
    }

    /** A card's plant in words: "running at 39% · roads 64%" - the throttle that cuts it most, when one is under 100% - or the homes let, or "no plant standing". */
    static String runningWords(SectorFlow.Plant plant) {
        if (plant.none()) return "no plant standing";
        if (Double.isFinite(plant.let())) return BuildScreen.pct(plant.let()) + " of its homes let";
        int low = plant.lowest();
        return "running at " + BuildScreen.pct(plant.rate())
                + (plant.ratios()[low] < .995 ? " · " + SectorFlow.THROTTLES[low] + " " + BuildScreen.pct(plant.ratios()[low]) : "");
    }

    /** "Industry · food mills": the Build category and the group a sector's own buildings are under, once when they are one. */
    String groupWords(Sector sector) {
        String cat = BuildCard.categoryOf(ui.game, sector), group = BuildCard.groupOf(ui.game, sector);
        if (cat == null) return sector.label();
        if (group == null || group.equalsIgnoreCase(cat)) return cat;
        return cat + " · " + group.toLowerCase();
    }

    /** A line of caption that wraps, in a colour. */
    static Label caption(String text, String tone) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setStyle(Palette.words(Palette.SIZE_CAPTION, tone));
        return l;
    }

    /** The width the sparkline takes on a card (0.7.30; 90 on the old list). */
    static final double SPARK_WIDTH = 120;

    /** ...and its height (0.7.30; 22 on the old list). */
    static final double SPARK_HEIGHT = 28;

    /** How many months of net income the sparkline draws (0.7.4): two years. */
    static final int SPARK_MONTHS = 24;

    /**
     * A sector's net income as a line (0.7.4): the last SPARK_MONTHS of the
     * history's netIncome:<sector>, the zero line faint under it. No labels -
     * the figure beside it is the number - and fewer than two months is said
     * in words.
     *
     * A SECTOR EARNING NOTHING IS GREY (0.7.21). It drew a red flat line,
     * which read as "losing"; a sector that made nothing and lost nothing is
     * not news, and red is only for a loss.
     *
     * IN THE BUSINESS AREA'S VIOLET, WITH ITS YEARS (0.7.23). The line was
     * green when the last month made money and red when it lost some - the
     * verdicts as a line's colour, which is the figure's job beside it (red
     * when negative). A faint mark at each January says where the years
     * turn, and the tooltip names the months.
     */
    javafx.scene.Node sparkline(List<? extends Number> series) {

        // Each point with its month: the series' last element is the history's
        // last month, so its i-th from the end is the axis's i-th from the end.
        List<Integer> axis = ui.game.getHistorySave().getMonth();
        List<Double> months = new ArrayList<>();
        List<Integer> when = new ArrayList<>();
        if (series != null) {
            for (int i = Math.max(0, series.size() - SPARK_MONTHS); i < series.size(); i++) {
                Number v = series.get(i);
                if (v != null && Double.isFinite(v.doubleValue())) {
                    months.add(v.doubleValue());
                    int at = axis.size() - (series.size() - i);
                    when.add(at >= 0 ? axis.get(at) : Integer.MIN_VALUE);
                }
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

        // The turn of each year, faintly, where a January falls (0.7.23).
        g.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_SPENT, .55));
        for (int i = 0; i < when.size(); i++) {
            if (when.get(i) == Integer.MIN_VALUE || CityCalendar.monthOfYear(when.get(i)) != 1) continue;
            double x = Math.floor(pad + w * i / (months.size() - 1)) + .5;
            g.strokeLine(x, pad, x, pad + h);
        }

        double latest = months.get(months.size() - 1);
        g.setStroke(javafx.scene.paint.Color.web(
                Math.abs(toDollars(latest)) < .5 ? Palette.TEXT_SPENT : Palette.BUSINESS));
        g.setLineWidth(1.5);
        g.beginPath();
        for (int i = 0; i < months.size(); i++) {
            double x = pad + w * i / (months.size() - 1);
            if (i == 0) g.moveTo(x, y.applyAsDouble(months.get(i)));
            else g.lineTo(x, y.applyAsDouble(months.get(i)));
        }
        g.stroke();

        int from = when.get(0), to = when.get(when.size() - 1);
        javafx.scene.control.Tooltip.install(canvas, new javafx.scene.control.Tooltip(String.format(
                "Net income after tax, %s: between %s and %s a month. A faint mark is a January.",
                from == Integer.MIN_VALUE ? "the last " + months.size() + " months"
                        : CityCalendar.formatShort(from) + " to " + CityCalendar.formatShort(to),
                tightMoney(toDollars(lo)), tightMoney(toDollars(hi)))));
        return canvas;
    }

    /**
     * The card's five figures more, with the list opened (0.7.4; three and
     * two since 0.7.30): revenue, margin and cash; what it owes and its posts
     * filled - each off SectorMonth or the sector, nothing recomputed.
     */
    GridPane sectorCardMore(Sector sector, SectorBooks.SectorMonth now) {
        boolean known = !now.isEmpty();
        GridPane more = new GridPane();
        more.setHgap(Palette.GAP_LOOSE);
        more.setVgap(4);
        more.add(moreCell("revenue", known ? m(now.revenue()) + "/mo" : "—",
                known ? Palette.TEXT_BODY : Palette.TEXT_SPENT), 0, 0);
        more.add(moreCell("margin", known ? marginWords(now.margin()) : "—",
                !known ? Palette.TEXT_SPENT : now.margin() < 0 ? Palette.BAD : Palette.TEXT_BODY), 1, 0);
        more.add(moreCell("cash", known ? m(now.cash()) : "—",
                !known ? Palette.TEXT_SPENT : now.cash() < 0 ? Palette.BAD : Palette.TEXT_BODY), 2, 0);
        more.add(moreCell("owes lenders", known ? m(now.bondsPayable()) : "—",
                known ? Palette.TEXT_BODY : Palette.TEXT_SPENT), 0, 1);
        more.add(moreCell("posts filled", String.format("%,.0f of %,d",
                sector.getWorkers(), sector.getPostsOffered()), Palette.TEXT_BODY), 1, 1);
        more.setStyle("-fx-padding: 4 0 0 0; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 1 0 0 0;");
        return more;
    }

    /** One labelled figure on a card's second row: the word over the figure, 120 wide. */
    static VBox moreCell(String word, String figure, String tone) {
        Label head = new Label(word);
        head.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Label value = new Label(figure);
        value.setMinWidth(Region.USE_PREF_SIZE);
        value.setStyle(Palette.words(Palette.SIZE_CAPTION, tone));
        VBox cell = new VBox(0, head, value);
        cell.setPrefWidth(120);
        cell.setMinWidth(100);
        return cell;
    }

    /** What the business actually does - the sector's own first sentence, whole (cut at 72 until 0.7.20). */
    static String sectorBlurb(Sector sector) {
        String blurb = sector.blurb();
        if (blurb == null || blurb.isBlank()) return sector.label();
        int stop = blurb.indexOf('.');
        return stop > 0 ? blurb.substring(0, stop) : blurb;
    }

    /* ---------------------------------------------------------------------
       THE INVESTORS' WORD, IN A KIND (0.7.30)

       The sector's word for the month (Game.getLastInvestment()) - on every
       card, under every page's strip, and as the Investors page's card -
       with its kind's icon and colour (BuildCard.wordKind()) and the kind in
       a word before it. Investors on site outrank the word, as on a build
       card; "Holding: " goes, the rest is the word as filed. The colours are
       the kinds', not verdicts, except that selling and credit are red and a
       wait on ground, staff, a licence, ore, supply or money is amber.
       --------------------------------------------------------------------- */

    static String kindIcon(BuildCard.WordKind k) {
        switch (k) {
            case BUILDING: return Icons.CRANE;
            case SELLING:  return Icons.ALERT;
            case LAND:     return Icons.LAND;
            case STAFF:    return Icons.STAFF;
            case LICENCE:  return Icons.EDUCATION;
            case ORE:      return Icons.PICK;
            case SUPPLY:   return Icons.INDUSTRY;
            case CREDIT:   return Icons.BANK;
            case MONEY:    return Icons.COIN;
            case ENOUGH:   return Icons.TICK;
            default:       return Icons.SECTOR;
        }
    }

    /** The kind's colour: its edge and its icon. */
    static String kindColour(BuildCard.WordKind k) {
        switch (k) {
            case BUILDING: return Palette.BUILDING;
            case SELLING: case CREDIT: return Palette.BAD;
            case LAND: case STAFF: case LICENCE: case ORE: case SUPPLY: case MONEY: return Palette.WARN;
            default: return Palette.TEXT_SPENT;
        }
    }

    /** ...and its word, before the sector's. */
    static String kindWord(BuildCard.WordKind k) {
        switch (k) {
            case BUILDING: return "building";
            case SELLING:  return "selling";
            case LAND:     return "land";
            case STAFF:    return "staff";
            case LICENCE:  return "licence";
            case ORE:      return "ore";
            case SUPPLY:   return "supply";
            case CREDIT:   return "credit";
            case MONEY:    return "money";
            case ENOUGH:   return "enough";
            case NONE:     return "no word yet";
            default:       return "holding";
        }
    }

    /** The words after the kind: what is on site of theirs, the build card's "nothing recorded" before a month has run, or the word with "Holding: " gone. */
    String sectorWords(BuildCard.SectorInvestors si) {
        String[] said = ui.buildScreen.investorsWords(si.line());
        if (si.theirs()) return "investors" + said[0];
        if (si.word().isEmpty()) return said[0].replaceFirst("^: ", "");
        return si.word().startsWith("Holding: ") ? si.word().substring("Holding: ".length()) : si.word();
    }

    /** A word's short form for a card: a "Built" or "Sold" word up to its " - " (the frame's line and the tooltip have the rest). */
    static String shortWord(String words) {
        if (!(words.startsWith("Built ") || words.startsWith("Sold "))) return words;
        int cut = words.indexOf(" - ");
        return cut > 0 ? words.substring(0, cut) : words;
    }

    /** "+2 Industrial Bakery": what a "Built" word built, for the month's flash; null for any other word. */
    static String builtTag(String word) {
        if (word == null || !word.startsWith("Built ")) return null;
        String rest = word.substring("Built ".length());
        int cut = rest.length();
        for (String stop : new String[] { " - ", " (", " on an insured mortgage" }) {
            int at = rest.indexOf(stop);
            if (at > 0 && at < cut) cut = at;
        }
        return "+" + rest.substring(0, cut);
    }

    /**
     * The word on a card: the kind's icon at 12, its word, the sector's - wrapping, never cut; a click opens the Investors page.
     * `blank` (0.7.34): the list says once that there is no word yet, and this line keeps a line's height with nothing in it.
     */
    Node investorsMini(BuildCard.SectorInvestors si, boolean blank) {
        BuildCard.WordKind k = si.kind();
        String colour = kindColour(k);
        javafx.scene.text.TextFlow line = new javafx.scene.text.TextFlow(
                BuildScreen.textRun(kindWord(k), Palette.Fonts.sansSemiBold(), 9.5, textColour(k)),
                BuildScreen.textRun("  " + (blank ? "" : shortWord(sectorWords(si))), null, 9.5, Palette.TEXT_MUTED));
        line.setMinWidth(0);
        HBox.setHgrow(line, Priority.ALWAYS);
        Region mark = icon(kindIcon(k), colour, 12);
        HBox row = new HBox(6, mark, line);
        row.setAlignment(Pos.TOP_LEFT);
        if (blank) {
            mark.setVisible(false);
            line.setVisible(false);
            row.setStyle("-fx-padding: 6 0 0 0; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 1 0 0 0;");
            return row;
        }
        row.setStyle("-fx-cursor: hand; -fx-padding: 6 0 0 0; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 1 0 0 0;");
        Tooltip tip = new Tooltip((si.word().isEmpty() ? sectorWords(si) : si.word()) + "\nClick for its Investors page.");
        tip.setWrapText(true);
        tip.setMaxWidth(420);
        tip.setShowDelay(Duration.millis(300));
        Tooltip.install(row, tip);
        row.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            e.consume();
            openSectorBooks(si.owner(), "Investors");
        });
        return row;
    }

    /** A kind's word in a colour that reads as text: the grey kinds' edge is too faint for words. */
    static String textColour(BuildCard.WordKind k) {
        String c = kindColour(k);
        return c.equals(Palette.TEXT_SPENT) ? Palette.TEXT_LABEL : c;
    }

    /**
     * The investors' line (0.7.30): under every page's strip, 36 high on the
     * raised ground with a 3 px edge in the kind's colour - its icon in a
     * tinted square, the kind in a word, the sector's word whole, and
     * "Investors ›" at the right. On a new month whose word is "Built", it
     * flashes once in the building pink with what was built. Retail's bank
     * branches, asked separately, are a second line.
     */
    VBox investorsLine(BuildCard.SectorInvestors si, boolean animate, double iconBox) {
        BuildCard.WordKind k = si.kind();
        HBox first = kindRow(k, sectorWords(si), iconBox);
        String tag = builtTag(si.word());
        if (tag != null) first.getChildren().add(2, tag(tag, Palette.BUILDING));
        VBox lines = new VBox(4, first);
        if (si.owner() == ui.game.getSectors().retail()) {
            String bank = ui.game.getLastInvestment("Bank");
            if (bank != null && !bank.isEmpty()) {
                BuildCard.WordKind bk = BuildCard.wordKind(bank);
                HBox second = kindRow(bk, "its bank branches: "
                        + (bank.startsWith("Holding: ") ? bank.substring("Holding: ".length()) : bank), iconBox * .8);
                second.getChildren().add(infoButton("The bank's counters are retail's money and not retail's shop "
                        + "decision, so the two are asked separately and filed separately.", false));
                lines.getChildren().add(second);
            }
        }
        lines.setMinHeight(iconBox + 14);
        lines.setMaxWidth(Double.MAX_VALUE);
        lines.setAlignment(Pos.CENTER_LEFT);
        lines.setStyle("-fx-padding: 6 12 6 10; -fx-background-color: " + Palette.RAISED + "; -fx-background-radius: 6;"
                + " -fx-border-color: transparent transparent transparent " + kindColour(k) + ";"
                + " -fx-border-width: 0 0 0 3; -fx-border-radius: 6;");
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        first.getChildren().addAll(gap, door("Investors", Palette.ACCENT, () -> open("Investors")));
        Tooltip tip = new Tooltip(si.word().isEmpty() ? sectorWords(si) : si.word());
        tip.setWrapText(true);
        tip.setMaxWidth(460);
        tip.setShowDelay(Duration.millis(300));
        Tooltip.install(lines, tip);
        if (animate && tag != null) flash(lines);
        return lines;
    }

    /** One kind's row: its icon square, its word and the words after it, wrapping. */
    static HBox kindRow(BuildCard.WordKind k, String words, double box) {
        javafx.scene.text.TextFlow line = new javafx.scene.text.TextFlow(
                BuildScreen.textRun(kindWord(k), Palette.Fonts.sansSemiBold(), Palette.SIZE_BODY, textColour(k)),
                BuildScreen.textRun("   " + words, null, Palette.SIZE_BODY, Palette.TEXT_BODY));
        line.setMinWidth(0);
        HBox.setHgrow(line, Priority.SOMETIMES);
        HBox row = new HBox(10, iconSquare(kindIcon(k), kindColour(k), box, Math.round(box * .58)), line);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** A region's one flash in the building pink: a "Built" month landing (0.7.30). */
    static void flash(Region r) {
        javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow(18, javafx.scene.paint.Color.web(Palette.BUILDING));
        glow.setSpread(.25);
        r.setEffect(glow);
        javafx.animation.Timeline fade = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.ZERO, new javafx.animation.KeyValue(glow.radiusProperty(), 18)),
                new javafx.animation.KeyFrame(Duration.millis(1100),
                        new javafx.animation.KeyValue(glow.radiusProperty(), 0, javafx.animation.Interpolator.EASE_OUT)));
        fade.setOnFinished(e -> r.setEffect(null));
        fade.play();
    }

    /* =====================================================================
       ONE BUSINESS, FIVE PAGES

       THE FRAME STAYS PUT (0.7.30, Infrastructure's shape): "Sectors ›" and
       the business's name with its icon, its five figures - KEPT, MARGIN,
       CASH, OWES and RUNNING AT, each a door to the page that explains it -
       its investors' line, and the five pages as chips with their icons. Only
       the page under them scrolls. The page names are the ones other screens
       open by: "Investors" from a build card, NEEDS YOU, the Land office and
       the inbox, "Income" from Infrastructure's railway.
       ===================================================================== */

    void drawSectorScreen() {

        Sector sector = openSector;
        boolean known = false;
        for (String p : SECTOR_PAGES) known |= p.equals(sectorPage);
        if (!known) sectorPage = SECTOR_HOME;
        SectorBooks books = ui.game.getSectorBooks();
        SectorBooks.SectorMonth now = books.get(sector);
        SectorBooks.SectorMonth then = books.previous(sector);
        boolean animate = freshMonth(sector.key() + "|" + sectorPage);
        BuildCard.SectorInvestors si = BuildCard.sectorInvestors(ui.game, sector);
        SectorFlow.Plant plant = SectorFlow.plant(sector);

        VBox page = widePage();
        if (now.isEmpty()) {
            page.getChildren().add(alertLine(Icons.ALERT, Palette.WARN, "Nothing recorded yet", NOTHING_YET_SECTOR, null));
        } else {
            switch (sectorPage) {
                case "Income"        -> incomePage(page, sector, now, then, animate);
                case "Balance sheet" -> balancePage(page, sector, now, then);
                case "Cash & debt"   -> cashAndDebtPage(page, sector, now, then);
                case "Investors"     -> investorPage(page, sector, now, si, animate);
                default              -> operationsPage(page, sector, animate);
            }
        }

        javafx.scene.layout.FlowPane strip =
                chipStrip(SECTOR_PAGES, PAGE_ICONS, sectorPage, Palette.SIZE_LABEL, this::open);

        VBox frame = new VBox(Palette.GAP, sectorHead(sector), sectorVitals(sector, now, then, plant, animate),
                investorsLine(si, animate, 22), strip);
        frameOver(frame, page, FRAME_CHROME);
    }

    /** The head: "Sectors ›" - the way back - the business's icon and name, its blurb behind an (i), and a door to its buildings on Build. */
    HBox sectorHead(Sector sector) {
        Label crumb = new Label("Sectors");
        crumb.setStyle(Palette.strong(Palette.SIZE_TITLE, Palette.TEXT_MUTED) + " -fx-padding: 8 0 2 0; -fx-cursor: hand;");
        crumb.setMinWidth(Region.USE_PREF_SIZE);
        Region swatch = new Region();
        swatch.setMinSize(10, 10);
        swatch.setPrefSize(10, 10);
        swatch.setMaxSize(10, 10);
        swatch.setStyle("-fx-background-color: " + Palette.BUSINESS + "; -fx-background-radius: 3;");
        crumb.setGraphic(swatch);
        crumb.setGraphicTextGap(10);
        crumb.setOnMouseClicked(e -> backToList());
        Tooltip.install(crumb, new Tooltip("All the sectors"));
        Label sep = new Label("›");
        sep.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_MUTED) + " -fx-padding: 8 0 2 0;");
        Label name = new Label(sector.label());
        name.setStyle(Palette.strong(Palette.SIZE_TITLE, Palette.TEXT_HEAD) + " -fx-padding: 8 0 2 0;");
        name.setMinWidth(Region.USE_PREF_SIZE);
        javafx.scene.Node square = iconSquare(Icons.ofSector(sector), Palette.BUSINESS, 30, 16);
        HBox.setMargin(square, new javafx.geometry.Insets(6, 0, 0, 0));
        javafx.scene.Node dot = infoButton((sector.blurb() == null || sector.blurb().isBlank() ? sector.label() : sector.blurb())
                + "\n\nIts buildings are on Build, under " + groupWords(sector) + ".", false);
        HBox.setMargin(dot, new javafx.geometry.Insets(6, 0, 0, 0));
        HBox titled = new HBox(10, crumb, sep, square, name, dot);
        titled.setAlignment(Pos.CENTER_LEFT);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox head = new HBox(Palette.GAP_LOOSE, titled, gap);
        String cat = BuildCard.categoryOf(ui.game, sector);
        if (cat != null) head.getChildren().add(doorPill("Build · " + cat, Icons.BUILD, Palette.BUILDING, () -> ui.buildScreen.openCategory(cat)));
        head.setAlignment(Pos.CENTER_LEFT);
        head.setMaxWidth(Double.MAX_VALUE);
        head.setStyle("-fx-padding: 0 0 4 0;");
        return head;
    }

    /**
     * The five figures: KEPT with its move, MARGIN, CASH, OWES - "owes
     * nothing" when it owes nothing, the rate and leverage when it owes
     * something (B8) - and RUNNING AT with the throttle that cuts it most,
     * or the homes let for the landlords, or "no plant standing". Each is a
     * door to the page that explains it.
     */
    HBox sectorVitals(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then,
                      SectorFlow.Plant plant, boolean animate) {

        double move = now.netIncome() - then.netIncome();
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        boolean blocked = credit.isBorrowingBlocked(sector.key());

        VBox kept = kpi("KEPT", m(now.netIncome()),
                then.isEmpty() ? "after tax"
                        : Math.abs(toDollars(move)) < .5 ? "no change on last month"
                        : String.format("%s%s on last month", move >= 0 ? "+" : "-", m(Math.abs(move))),
                now.netIncome() < 0 ? Palette.BAD : Palette.TEXT_HEAD, null,
                "Income: what it kept, and where the rest went", () -> open("Income"));
        noteTone(kept, then.isEmpty() || Math.abs(toDollars(move)) < .5 ? Palette.TEXT_MUTED : move > 0 ? Palette.GOOD : Palette.BAD);
        if (animate && !then.isEmpty()) countUp(figureOf(kept), then.netIncome(), now.netIncome(), SectorScreen::m);

        VBox margin = kpi("MARGIN", marginWords(now.margin()), "of every dollar taken",
                now.margin() < 0 ? Palette.BAD : now.margin() < .05 ? Palette.WARN : Palette.GOOD, null,
                "Income: the statement and its ratios", () -> open("Income"));
        VBox cash = kpi("CASH", m(now.cash()),
                now.cash() < 0 ? "overdrawn — it is borrowing" : "in its own account",
                now.cash() < 0 ? Palette.BAD : Palette.TEXT_HEAD, null,
                "Cash & debt: where the cash went", () -> open("Cash & debt"));
        VBox owes = kpi("OWES", m(now.bondsPayable()),
                owesWords(blocked, credit.getBlockedMonths(sector.key()), now),
                blocked ? Palette.BAD : now.leverage() > .6 ? Palette.WARN : Palette.TEXT_HEAD, null,
                "Cash & debt: what it borrows on", () -> open("Cash & debt"));
        String[] run = runningCell(plant);
        VBox running = kpi(run[0], run[1], run[2], plant.none() ? Palette.TEXT_MUTED : Palette.TEXT_HEAD, null,
                "Operations: the plant, and what holds it back", () -> open("Operations"));
        return vitalsBar(kept, margin, cash, owes, running);
    }

    /** OWES's note: the ban, "owes nothing" when it owes nothing (B8: it quoted a rate on no debt), or its rate and leverage. */
    static String owesWords(boolean blocked, int blockedMonths, SectorBooks.SectorMonth now) {
        if (blocked) return "cannot borrow — " + blockedMonths + " months left";
        if (!(now.bondsPayable() > 0)) return "owes nothing";
        return String.format("at %.2f%%, leverage %.2f", unsigned0(now.rate() * 100, 2), unsigned0(now.leverage(), 2));
    }

    /** The frame's fifth figure, {label, figure, note}: what it runs at and what cuts it most; the landlords' homes let; or no plant. */
    static String[] runningCell(SectorFlow.Plant plant) {
        if (plant.none()) return new String[] { "RUNNING AT", "—", "no plant standing" };
        if (Double.isFinite(plant.let())) return new String[] { "LET", BuildScreen.pct(plant.let()), "of the homes it owns" };
        int low = plant.lowest();
        return new String[] { "RUNNING AT", BuildScreen.pct(plant.rate()),
                plant.ratios()[low] < .995 ? "most cut by " + SectorFlow.THROTTLES[low] + ", " + BuildScreen.pct(plant.ratios()[low])
                        : "of what its plants could make" };
    }

    /* ----------------------------- the screen's own pieces ----------------------------- */

    /** Money in the model's thousands, as the statements write it: "$13.6M". */
    static String m(double thousands) { return tightMoney(toDollars(thousands)); }

    /** A margin to a tenth of a per cent, with no sign on one that rounds to nothing (B11). */
    static String marginWords(double share) { return String.format("%.1f%%", unsigned0(share * 100, 1)); }

    /** A limit cell with an (i) after its label when `info` is not null. */
    static VBox kpi(String label, String value, String note, String tone, String info, String where, Runnable go) {
        VBox cell = limitCell(label, value, note, tone, where, go);
        if (info != null && !cell.getChildren().isEmpty() && cell.getChildren().get(0) instanceof Label what) {
            cell.getChildren().remove(0);
            HBox titled = new HBox(Palette.GAP_TIGHT, what, infoButton(info, false));
            titled.setAlignment(Pos.CENTER_LEFT);
            cell.getChildren().add(0, titled);
        }
        return cell;
    }

    /** A limit cell's figure, for its count-up. */
    static Label figureOf(VBox cell) { return (Label) cell.getChildren().get(1); }

    /** A limit cell's note in a colour of its own: a move on last month, up or down. */
    static void noteTone(VBox cell, String tone) {
        if (cell.getChildren().size() > 2 && cell.getChildren().get(2) instanceof Label says) {
            says.setStyle(Palette.words(Palette.SIZE_CAPTION, tone));
        }
    }

    /** A figure counted from last month's to this month's over COUNT_MILLIS, ending on this month's exactly (0.7.30). */
    static void countUp(Label label, double from, double to, java.util.function.DoubleFunction<String> words) {
        if (!Double.isFinite(from) || !Double.isFinite(to) || words.apply(from).equals(words.apply(to))) return;
        javafx.beans.property.DoubleProperty value = new javafx.beans.property.SimpleDoubleProperty(from);
        value.addListener((o, was, is) -> label.setText(words.apply(is.doubleValue())));
        label.setText(words.apply(from));
        javafx.animation.Timeline count = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.millis(COUNT_MILLIS),
                        new javafx.animation.KeyValue(value, to, javafx.animation.Interpolator.EASE_OUT)));
        count.setOnFinished(e -> label.setText(words.apply(to)));
        count.play();
    }

    /** A ring (Pieces.ring()) whose arc sweeps up from nothing over SWEEP_MILLIS - there is no last month's rate to sweep from (0.7.30). */
    static Region sweep(Region ring) {
        if (!(ring instanceof javafx.scene.layout.StackPane stack) || stack.getChildren().isEmpty()
                || !(stack.getChildren().get(0) instanceof javafx.scene.layout.Pane drawn)) return ring;
        for (Node n : drawn.getChildren()) {
            if (!(n instanceof javafx.scene.shape.Arc arc)) continue;
            double to = arc.getLength();
            arc.setLength(0);
            new javafx.animation.Timeline(new javafx.animation.KeyFrame(Duration.millis(SWEEP_MILLIS),
                    new javafx.animation.KeyValue(arc.lengthProperty(), to, javafx.animation.Interpolator.EASE_OUT))).play();
        }
        return ring;
    }

    /** One line that something is wrong or waiting: its icon in a colour, a few words, and the whole of it behind an (i). */
    static HBox alertLine(String svg, String colour, String text, String info, Node right) {
        Label words = new Label(text);
        words.setWrapText(true);
        words.setStyle(Palette.strong(Palette.SIZE_BODY, colour));
        HBox row = new HBox(8, icon(svg, colour, 14), words);
        if (info != null) row.getChildren().add(infoButton(info, true));
        if (right != null) {
            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            row.getChildren().addAll(gap, right);
        }
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setStyle("-fx-padding: 8 12 8 12; -fx-background-color: " + tint(colour, .10) + "; -fx-background-radius: 6;");
        return row;
    }

    /** A card on the raised ground: a picture's frame. */
    static VBox card(Node... children) {
        VBox c = new VBox(Palette.GAP_LOOSE);
        for (Node n : children) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(Double.MAX_VALUE);
        c.setStyle("-fx-padding: 14 16 14 16; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return c;
    }

    /** A picture's caption in capitals, with an (i) after it when `info` is not null, and something at its right. */
    static HBox head(String text, String info, Node right) {
        Label l = new Label(text);
        l.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        l.setMinWidth(Region.USE_PREF_SIZE);
        HBox row = new HBox(Palette.GAP, l);
        if (info != null) row.getChildren().add(infoButton(info, true));
        if (right != null) {
            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            row.getChildren().addAll(gap, right);
        }
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** A figure, never cut, at a size, in a colour. */
    static Label figure(String text, double size, String tone) {
        Label l = new Label(text);
        l.setMinWidth(Region.USE_PREF_SIZE);
        l.setStyle(BuildScreen.figureAt(size, tone));
        return l;
    }

    /** An (i) after a statement line's label: the paragraph that sat under it (0.7.30). */
    static Node withInfo(Node line, String info) {
        HBox row = null;
        if (line instanceof HBox h) row = h;
        else if (line instanceof VBox v) {
            for (Node n : v.getChildren()) if (n instanceof HBox h) { row = h; break; }
        }
        if (row != null && info != null) row.getChildren().add(1, infoButton(info, true));
        return line;
    }

    /** The statement lines open on one business's pages. */
    java.util.Set<String> linesOpen(Sector sector) {
        return linesOpen.computeIfAbsent(sector.key(), k -> new java.util.HashSet<>());
    }

    /** A ratio as a chip in a row: its name, the chip, and its (i). */
    static HBox ratioRow(String name, String value, String colour, String info) {
        Label what = new Label(name);
        what.setWrapText(true);
        what.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_LABEL));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label chip = chip(value, colour);
        chip.setStyle(chip.getStyle() + " -fx-font-size: " + Palette.SIZE_BODY + "px;");
        HBox row = new HBox(Palette.GAP, what, gap, chip);
        if (info != null) row.getChildren().add(infoButton(info, true));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** A ratio column, 300 wide, beside a statement. */
    static VBox ratioColumn(String title, Node... rows) {
        VBox c = new VBox(8, head(title, null, null));
        for (Node n : rows) if (n != null) c.getChildren().add(n);
        c.setMinWidth(260);
        c.setPrefWidth(300);
        c.setMaxWidth(340);
        c.setStyle("-fx-padding: 12 14 12 14; -fx-background-color: " + Palette.PANEL + "; -fx-background-radius: 8;");
        return c;
    }

    /** A statement column beside its ratios: the statement at its old width, the ratios to its right. */
    static HBox statementAndRatios(VBox statement, VBox ratios) {
        statement.setMinWidth(STATEMENT);
        statement.setMaxWidth(STATEMENT);
        HBox row = new HBox(24, statement);
        if (ratios != null) row.getChildren().add(ratios);
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    /** One part of a bar's key: its swatch, its name and its figure. */
    static HBox keyPart(String colour, String name, String figure) {
        Region sw = new Region();
        sw.setMinSize(10, 10);
        sw.setPrefSize(10, 10);
        sw.setMaxSize(10, 10);
        sw.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 2;");
        Label n = new Label(name);
        n.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Label f = new Label(figure);
        f.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_HEAD));
        f.setMinWidth(Region.USE_PREF_SIZE);
        HBox k = new HBox(5, sw, n, f);
        k.setAlignment(Pos.CENTER_LEFT);
        return k;
    }

    /** A bar's key, wrapping. */
    static javafx.scene.layout.FlowPane key(List<HBox> parts) {
        javafx.scene.layout.FlowPane f = new javafx.scene.layout.FlowPane(14, 4);
        f.getChildren().addAll(parts);
        return f;
    }

    /* =====================================================================
       A STATEMENT LINE THAT OPENS

       Jerus, 2026-09-16: "can you make it so in the UI when you click on
       revenue or cogs, it expands and shows the individual items".

       Same mechanics as the city's budget lines on the Government pages
       (budgetLine, until 0.7.31's ranked bars), which took
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

    /* bookTotal(), the line a section adds up to, moved to Statement on
       2026-09-23 (0.7.9) when the bank's income statement wanted it too. */

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
        // The posts the payroll is struck on: the builders' laid-off posts
        // are not in it (0.7.17, sectors.Construction, THE CREWS THE WORK NEEDS).
        int[] posts = sector.postsOfferedPerTier();
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
         * payroll on the statement is the STAFFED payroll - each job type's
         * wage for the posts of that type filled, since 0.7.17 - and a player
         * looking at a cheap wage line on a half-staffed sector is reading a
         * saving that is really a shortage. See Sector.getPayroll(). The full
         * bill is the model's own figure (getPayrollAtFullStaffing()), not the
         * staffed one scaled up by the average fill, which it no longer is.
         */
        if (!box.getChildren().isEmpty() && sector.getAverageFill() < .999) {
            double full = sector.getPayrollAtFullStaffing();
            box.getChildren().add(bookDetailNote(String.format(
                    "These posts are %.0f%% filled. Fully staffed the wage bill would be %s, "
                    + "and the business would be making what its buildings can make.",
                    sector.getAverageFill() * 100, tightMoney(toDollars(full), false))));
        }
        int standing = 0;
        for (int p : sector.postsPerTier()) standing += p;
        int offered = sector.getPostsOffered();
        if (!box.getChildren().isEmpty() && offered < standing) {
            box.getChildren().add(bookDetailNote(String.format(
                    "%,d of its %,d posts are laid off this month: its work does not need them. "
                    + "Nobody is paid for them; the workers are unemployed, free to take other "
                    + "work, and hired back when the work returns.",
                    standing - offered, standing)));
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
                            tightMoney(toDollars(zeroRated), false)), "—", false, null));
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

    /* =====================================================================
       INCOME (0.7.30): A WATERFALL OVER THE STATEMENT

       The question: where does a dollar of revenue go? Revenue, down through
       what it bought, its wages, its power, water and repairs to operating
       income; its property tax, interest and sales tax to profit before tax;
       its business tax to what it kept - each a bar, a refund stepping up.
       A click on a bar opens its statement line. The statement is under it,
       whole, opening as it always has, and the ratios are chips beside it in
       their old thresholds. Paying a tax is not a verdict (B10): the taxes
       and the interest are plain figures now, not amber, and revenue is not
       green.
       ===================================================================== */

    /** Operating income's (i): the old page's sentence under it. */
    static final String OPERATING_INFO = "What the business made from trading, before it pays for the ground it "
            + "stands on or the money it borrowed.";

    /** The sales tax refund's (i). */
    static final String REFUND_INFO = "The sales tax line is a REFUND this month: the credit on what this sector "
            + "bought came to more than the tax on what it sold. That is what zero-rating an export means, and the "
            + "city pays it.";

    void incomePage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, boolean animate) {

        boolean known = !then.isEmpty();
        java.util.Set<String> open = linesOpen(sector);
        String inputs = inputLabel(sector);

        // The waterfall.
        Waterfall fall = waterfall(incomeSteps(now, inputs, open), SectorScreen::m, 0, 190);
        if (animate) fall.animate(500);
        page.getChildren().add(card(head("WHERE A DOLLAR OF REVENUE WENT, " + CityCalendar.format(now.month()).toUpperCase(),
                "Revenue at the left, down through what it bought, its wages and its running costs to what it made "
                + "trading; then the ground, the interest and the sales tax to its profit; then the business tax to "
                + "what it kept. A refund steps up. Revenue, what it bought, its wages and the sales tax open their "
                + "lines of the statement under this.", null), fall));

        // The statement.
        incomeStatement(page, sector, now, then, known, open, inputs);
    }

    /** The waterfall's steps: revenue, each cost down to what it kept, the three totals the model strikes; a bar with a statement line that opens opens it. */
    List<Step> incomeSteps(SectorBooks.SectorMonth now, String inputs, java.util.Set<String> open) {
        List<Step> steps = new ArrayList<>();
        steps.add(Step.of("Revenue", now.revenue(), Palette.REVENUE_RAMP[2]).go(n -> openLine(open, "Revenue")));
        if (now.inputs() != 0) steps.add(Step.of("Bought in", -now.inputs(), Palette.SPENDING_RAMP[2])
                .tip(inputs + "\n" + m(now.inputs())).go(n -> openLine(open, inputs)));
        if (now.payroll() != 0) steps.add(Step.of("Wages", -now.payroll(), Palette.SPENDING_RAMP[2]).go(n -> openLine(open, "Wages")));
        double running = now.electricity() + now.water() + now.maintenance();
        if (running != 0) {
            steps.add(Step.of("Power, water, repairs", -running, Palette.SPENDING_RAMP[2]).parts(List.of(
                    new Slice("Electricity", now.electricity(), Palette.SPENDING_RAMP[1]),
                    new Slice("Water", now.water(), Palette.SPENDING_RAMP[2]),
                    new Slice("Repairs", now.maintenance(), Palette.SPENDING_RAMP[3]))));
        }
        steps.add(Step.total("Operating income", now.operatingIncome(),
                now.operatingIncome() < 0 ? Palette.BAD : Palette.BUSINESS));
        if (now.propertyTax() != 0) steps.add(Step.of("Property tax", -now.propertyTax(), Palette.SPENDING_RAMP[2]));
        if (now.interest() != 0) steps.add(Step.of("Interest", -now.interest(), Palette.SPENDING_RAMP[2]));
        if (now.salesTaxPaid() != 0) steps.add(Step.of(now.salesTaxPaid() < 0 ? "Sales tax refund" : "Sales tax",
                -now.salesTaxPaid(), now.salesTaxPaid() < 0 ? Palette.REVENUE_RAMP[2] : Palette.SPENDING_RAMP[2])
                .go(n -> openLine(open, "Sales tax remitted")));
        steps.add(Step.total("Profit before tax", now.preTaxIncome(),
                now.preTaxIncome() < 0 ? Palette.BAD : Palette.BUSINESS));
        if (now.tax() != 0) steps.add(Step.of("Business tax", -now.tax(), Palette.SPENDING_RAMP[2]));
        steps.add(Step.total("What it kept", now.netIncome(), now.netIncome() < 0 ? Palette.BAD : Palette.BUSINESS));
        return steps;
    }

    /** The income statement, whole, opening as it always has, and its ratios beside it. */
    void incomeStatement(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then,
                         boolean known, java.util.Set<String> open, String inputs) {
        VBox column = new VBox(0);
        column.getChildren().add(statementHead("Income statement"));
        column.getChildren().add(bookHead(CityCalendar.format(now.month())));

        column.getChildren().add(bookLine("Revenue",
                now.revenue(), then.revenue(), known, null,
                revenueDetail(sector), "what", open));

        if (now.inputs() != 0 || then.inputs() != 0) {
            column.getChildren().add(bookLine(inputs,
                    -now.inputs(), -then.inputs(), known, null,
                    inputsDetail(sector), "what", open));
        }
        if (now.payroll() != 0 || then.payroll() != 0) {
            column.getChildren().add(bookLine("Wages",
                    -now.payroll(), -then.payroll(), known, null,
                    wagesDetail(sector), "who", open));
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

        column.getChildren().add(withInfo(bookTotal("Operating income",
                now.operatingIncome(), then.operatingIncome(), known,
                now.operatingIncome() < 0 ? Palette.BAD : null), OPERATING_INFO));

        column.getChildren().add(bookLine("Property tax",
                -now.propertyTax(), -then.propertyTax(), known, null));
        column.getChildren().add(bookLine("Interest on debt",
                -now.interest(), -then.interest(), known, null));
        if (now.salesTaxPaid() != 0 || then.salesTaxPaid() != 0) {
            VBox tax = bookLine("Sales tax remitted",
                    -now.salesTaxPaid(), -then.salesTaxPaid(), known, null,
                    salesTaxDetail(sector), "how", open);
            column.getChildren().add(now.salesTaxPaid() < 0 ? withInfo(tax, REFUND_INFO) : tax);
        }

        column.getChildren().add(bookTotal("Profit before tax",
                now.preTaxIncome(), then.preTaxIncome(), known,
                now.preTaxIncome() < 0 ? Palette.BAD : null));

        column.getChildren().add(bookLine("Business tax",
                -now.tax(), -then.tax(), known, null));
        column.getChildren().add(bookTotal("What it kept",
                now.netIncome(), then.netIncome(), known,
                now.netIncome() < 0 ? Palette.BAD : null));

        // The ratios, as chips in their old thresholds.
        VBox ratios = ratioColumn("READ AS RATIOS",
                ratioRow("Net margin", marginWords(now.margin()),
                        now.margin() < 0 ? Palette.BAD : now.margin() < .05 ? Palette.WARN : Palette.GOOD,
                        "What it kept of every dollar it took, after tax. Under 5% is thin."),
                now.revenue() > 0 ? ratioRow("Wages take", String.format("%.0f%% of revenue", now.payroll() / now.revenue() * 100),
                        Palette.TEXT_LABEL, null) : null,
                now.revenue() > 0 ? ratioRow("Interest takes", String.format("%.1f%% of revenue",
                                unsigned0(now.interest() / now.revenue() * 100, 1)),
                        now.interest() / now.revenue() > .15 ? Palette.WARN : Palette.TEXT_LABEL, null) : null,
                now.operatingIncome() > 0 && now.interest() > 0
                        ? ratioRow("Interest cover", String.format("%.1fx", now.operatingIncome() / now.interest()),
                                now.operatingIncome() / now.interest() < 1.5 ? Palette.BAD
                                        : now.operatingIncome() / now.interest() < 3 ? Palette.WARN : Palette.GOOD,
                                "How many times over its trading profit covers its interest bill. Under one and the "
                                + "business is borrowing to pay its lenders.") : null);
        page.getChildren().add(statementAndRatios(column, ratios));
    }

    /** A waterfall's bar opening its statement line: the line put in the open set, and the page drawn again. */
    void openLine(java.util.Set<String> open, String label) {
        open.add(label);
        ui.redraw();
    }

    /* =====================================================================
       THE BALANCE SHEET (0.7.30): TWO BARS AND ITS OWNERS

       What it owns against who has a claim on it, on one scale: its cash,
       stock, land, buildings, what it holds abroad and other businesses'
       bonds, against its loans and bonds and its owners' equity. The sheet
       had listed four of its six kinds of asset and totalled all six (B1) -
       Construction's $40B held abroad was in "Everything it owns" and on no
       line. Both are lines now. Its owners are one card, the same card the
       Bank's Owners page draws (D11).
       ===================================================================== */

    void balancePage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {

        boolean known = !then.isEmpty();

        // The sheet, on one scale.
        String[] ownsColours = { Palette.BUSINESS_LIGHT, Palette.BUSINESS, Palette.BUSINESS_DARK,
                Palette.RAMP_REST, Palette.ORE, Palette.TEXT_SPENT };
        String[] ownsNames = { "cash", "stock", "land", "buildings", "held abroad", "other businesses' bonds" };
        double[] owns = { Math.max(0, now.cash()), now.inventory(), now.land(), now.buildings(),
                now.foreignAssets(), now.bondAssets() };
        double equity = now.equity();
        double scale = Math.max(Math.max(0, now.totalAssets()), now.bondsPayable() + Math.max(0, equity));
        List<Segment> ownParts = new ArrayList<>();
        List<HBox> ownKey = new ArrayList<>();
        for (int i = 0; i < owns.length; i++) {
            if (!(owns[i] > 0)) continue;
            ownParts.add(new Segment(owns[i], ownsColours[i], false, null, null, ownsNames[i] + "  " + m(owns[i]), null));
            ownKey.add(keyPart(ownsColours[i], ownsNames[i], m(owns[i])));
        }
        List<Segment> owedParts = new ArrayList<>();
        List<HBox> owedKey = new ArrayList<>();
        if (now.bondsPayable() > 0) {
            owedParts.add(new Segment(now.bondsPayable(), Palette.MONEY, false, null, null, "loans and bonds  " + m(now.bondsPayable()), null));
            owedKey.add(keyPart(Palette.MONEY, "loans and bonds", m(now.bondsPayable())));
        }
        if (equity > 0) {
            owedParts.add(new Segment(equity, Palette.BUSINESS, false, null, null, "owners' equity  " + m(equity), null));
        }
        owedKey.add(keyPart(equity < 0 ? Palette.BAD : Palette.BUSINESS, "owners' equity", m(equity)));
        VBox sheet = card(head("WHAT IT OWNS, AND WHO HAS A CLAIM ON IT", "One scale for both bars: what it owns "
                        + "above, its lenders' claim and its owners' below. The two are equal - equity is whatever is "
                        + "left when the lenders are paid off - unless it owes more than it owns.", null),
                sheetRow("OWNS", ownParts, scale, m(now.totalAssets()), ownKey),
                sheetRow("OWES + EQUITY", owedParts, scale, m(now.bondsPayable() + equity), owedKey));
        page.getChildren().add(sheet);
        if (equity < 0) {
            page.getChildren().add(alertLine(Icons.ALERT, Palette.BAD, "It is worth less than it owes", String.format(
                    "Everything this business owns comes to %s and it owes %s. It is insolvent on paper and still "
                    + "trading, which the model allows: the credit side stops lending long before the accountants "
                    + "would stop the trading.", m(now.totalAssets()), m(now.bondsPayable())), null));
        }

        // The statement, with the two kinds of asset it left out (B1).
        VBox column = new VBox(0);
        column.getChildren().add(statementHead("What it owns"));
        column.getChildren().add(bookHead("as at " + CityCalendar.format(now.month())));

        column.getChildren().add(bookLine("Cash", now.cash(), then.cash(), known,
                now.cash() < 0 ? Palette.BAD : null));
        if (now.inventory() != 0 || then.inventory() != 0) {
            column.getChildren().add(withInfo(bookLine("Stock on the shelf",
                    now.inventory(), then.inventory(), known, null),
                    "Stock is held at what it would fetch today, so a price collapse shrinks this business's "
                    + "balance sheet without anything being sold."));
        }
        column.getChildren().add(bookTotal("Current assets",
                now.cash() + now.inventory(), then.cash() + then.inventory(), known, null));

        column.getChildren().add(bookLine("Land", now.land(), then.land(), known, null));
        column.getChildren().add(withInfo(bookLine("Buildings, at cost",
                now.buildings(), then.buildings(), known, null),
                "Buildings are at what they cost to put up — cash plus materials at the price of the day. "
                + "Nothing here depreciates."));
        if (now.foreignAssets() != 0 || then.foreignAssets() != 0) {
            column.getChildren().add(withInfo(bookLine("Held abroad",
                    now.foreignAssets(), then.foreignAssets(), known, null),
                    "What it has sent abroad for the world's rate, in the city's money at the rate it was last "
                    + "valued at. It comes home before the business borrows a cent."));
        }
        if (now.bondAssets() != 0 || then.bondAssets() != 0) {
            column.getChildren().add(bookLine("Other businesses' bonds",
                    now.bondAssets(), then.bondAssets(), known, null));
        }
        column.getChildren().add(bookTotal("Everything it owns",
                now.totalAssets(), then.totalAssets(), known, null));

        column.getChildren().add(statementHead("What it owes, and what is left"));
        column.getChildren().add(bookLine("Loans and bonds outstanding",
                now.bondsPayable(), then.bondsPayable(), known, null));
        column.getChildren().add(withInfo(bookLine("Owners' equity",
                now.equity(), then.equity(), known,
                now.equity() < 0 ? Palette.BAD : null),
                "Owners' equity is what is left when the lenders are paid off: what the founders started with, "
                + "what its shareholders have subscribed since, and every month's result — less what was paid out "
                + "to them and what was bought back. Who holds it, and what the market makes of it, is the owners "
                + "card below."));
        column.getChildren().add(bookTotal("Owed and owned",
                now.bondsPayable() + now.equity(),
                then.bondsPayable() + then.equity(), known, null));

        double assets = now.totalAssets();
        double debtToAssets = assets > 0 ? now.bondsPayable() / assets : 0;
        VBox ratios = ratioColumn("READ AS RATIOS",
                ratioRow("Debt to assets", String.format("%.0f%%", debtToAssets * 100),
                        debtToAssets > .7 ? Palette.BAD : debtToAssets > .5 ? Palette.WARN : Palette.GOOD, null),
                ratioRow("Return on assets", String.format("%.2f%% a month",
                                unsigned0(assets > 0 ? now.netIncome() / assets * 100 : 0, 2)),
                        now.netIncome() < 0 ? Palette.BAD : Palette.TEXT_LABEL, null),
                now.inventory() > 0 && assets > 0
                        ? ratioRow("Stock, of its assets", String.format("%.0f%%", now.inventory() / assets * 100),
                                Palette.TEXT_LABEL, null) : null);
        page.getChildren().add(statementAndRatios(column, ratios));

        VBox owners = ownersCard(Equity.indexOf(sector.key()), now.equity(), now.netIncome(), true);
        if (owners != null) page.getChildren().add(owners);
    }

    /** One bar of the sheet: its name, the bar on the sheet's scale, its total, and its key under it. */
    static VBox sheetRow(String name, List<Segment> parts, double scale, String total, List<HBox> keyParts) {
        Label n = new Label(name);
        n.setStyle(Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        n.setMinWidth(110);
        n.setPrefWidth(110);
        SegmentBar bar = segmentBar(parts, scale > 0 ? scale : 1, List.of(), 0, 18);
        HBox.setHgrow(bar, Priority.ALWAYS);
        Label t = figure(total, Palette.SIZE_BODY + 2, Palette.TEXT_HEAD);
        t.setMinWidth(90);
        t.setAlignment(Pos.CENTER_RIGHT);
        HBox row = new HBox(Palette.GAP_LOOSE, n, bar, t);
        row.setAlignment(Pos.CENTER_LEFT);
        javafx.scene.layout.FlowPane k = key(keyParts);
        VBox.setMargin(k, new javafx.geometry.Insets(0, 0, 0, 122));
        return new VBox(4, row, k);
    }

    /**
     * Who owns a company, what a share is worth, and what it pays - as a card
     * (0.7.30, D11), on a sector's Balance sheet (`wide`: the figures beside
     * the share price's chart) and on the Bank's Owners page (narrow, in its
     * statement column, through ownersBlock()).
     *
     * Since 2026-09-10 (evening) every sector and the bank is a company with
     * shareholders - the city's households first, the world for what they
     * did not buy. See Equity. Shown as shares of the company rather than
     * counts of shares, because a company that has sold shares at book when
     * its book was small has millions of them and the count says nothing.
     *
     * THE MARKET, on the order book since 0.7.12 round 2 (the bank's desk
     * quoted it from 2026-09-11 until then). The price is the last trade,
     * with fair value - the register's reckoning - beside it; the yield at
     * the price and what the company is worth at it; the month's volume; and
     * what rests on the book, best first, behind "details". Bids and asks are
     * no longer green and amber: a bid is not good news (B10).
     */
    VBox ownersCard(int company, double bookEquity, double netIncome, boolean wide) {
        Equity register = ui.game.getEquity();
        if (company < 0 || register.getShares(company) <= 0) return null;
        Exchange market = ui.game.getExchange();
        double abroad = register.foreignShare(company);
        double onDesk = company == Equity.BANK ? 0 : register.deskShare(company);
        double home = Math.max(0, 1 - abroad - register.deskShare(company));

        // Who holds it.
        List<Segment> held = new ArrayList<>();
        List<HBox> heldKey = new ArrayList<>();
        held.add(new Segment(home, Palette.PEOPLE, false, null, null, "the city's households  " + BuildScreen.pct(home), null));
        heldKey.add(keyPart(Palette.PEOPLE, "the city's households", BuildScreen.pct(home)));
        held.add(new Segment(abroad, Palette.ORE, false, null, null, "held abroad  " + BuildScreen.pct(abroad), null));
        heldKey.add(keyPart(Palette.ORE, "held abroad", BuildScreen.pct(abroad)));
        if (onDesk > 0) {
            held.add(new Segment(onDesk, Palette.MONEY, false, null, null, "on the bank's trading desk  " + BuildScreen.pct(onDesk), null));
            heldKey.add(keyPart(Palette.MONEY, "on the bank's trading desk", BuildScreen.pct(onDesk)));
        }
        SegmentBar heldBar = segmentBar(held, 1, List.of(), wide ? 0 : STATEMENT - 32, 14);

        // The market, in a strip of small figures.
        double price = market.price(company), fairValue = market.fair(company);
        boolean dear = price > fairValue * (1 + Exchange.BUYBACK_TOLERANCE);
        boolean cheap = price < fairValue * (1 - Exchange.BUYBACK_TOLERANCE);
        OrderBook shareBook = market.bookOf(company);
        javafx.scene.layout.FlowPane strip = new javafx.scene.layout.FlowPane(18, 8);
        strip.getChildren().add(miniFigure(market.hasTraded(company) ? "LAST TRADE" : "NOT TRADED YET: AT FAIR VALUE",
                tightMoney(toDollars(price), false), market.hasTraded(company) ? CityCalendar.format(shareBook.lastTradeMonth()) : null,
                dear ? Palette.WARN : cheap ? Palette.GOOD : Palette.TEXT_HEAD,
                dear ? "Above fair value by more than the companies' buy-back tolerance: buyers paid more than the "
                        + "register reckons it is worth." : cheap ? "Below fair value by more than the tolerance: sellers "
                        + "took less to get out." : null));
        strip.getChildren().add(miniFigure("FAIR VALUE", tightMoney(toDollars(fairValue), false), "the register's reckoning",
                Palette.TEXT_HEAD, null));
        strip.getChildren().add(miniFigure("YIELD", String.format("%.1f%%", unsigned0(market.yieldAt(register, company, price) * 100, 1)),
                "on the dividend it paid over the year", Palette.TEXT_HEAD, null));
        strip.getChildren().add(miniFigure("MARKET CAP", m(market.marketCap(register, company)), "at the last trade",
                Palette.TEXT_HEAD, null));
        strip.getChildren().add(miniFigure("TRADED", market.getVolume(company) > 0
                ? String.format("%,.0f", market.getVolume(company)) : "none", "shares this month", Palette.TEXT_HEAD, null));
        double paid = register.getDividendThisMonth(company);
        strip.getChildren().add(miniFigure("DIVIDEND", tightMoney(toDollars(paid), false), "this month", Palette.TEXT_HEAD, null));
        strip.getChildren().add(miniFigure("BOOK", tightMoney(toDollars(register.bookPerShare(company, bookEquity)), false),
                "a share is worth on the books", Palette.TEXT_HEAD, null));
        double retired = register.getBoughtBackThisMonth(company);
        if (retired > 0 && register.getShares(company) + retired > 0) {
            strip.getChildren().add(miniFigure("BOUGHT BACK", String.format("%.2f%%",
                    retired / (register.getShares(company) + retired) * 100), "of the company, retired this month",
                    Palette.TEXT_HEAD, null));
        }

        VBox left = new VBox(Palette.GAP_LOOSE, heldBar, key(heldKey), strip);
        if (!market.isOpen()) {
            left.getChildren().add(caption("The bank's desk is not posting: it has no capital to trade with. "
                    + "Everybody else still can, and a seller with no buyer waits.", Palette.TEXT_MUTED));
        }
        double split = market.getSplit(company);
        if (split > 1) {
            left.getChildren().add(caption(String.format("Split %,.0f for one this month: every holder's count by %,.0f, "
                    + "the price by the inverse.", split, split), Palette.TEXT_MUTED));
        } else if (split > 0) {
            left.getChildren().add(caption(String.format("Consolidated one for %,.0f this month: every holder's count by "
                    + "the inverse, the price by %,.0f.", 1 / split, 1 / split), Palette.TEXT_MUTED));
        }
        left.getChildren().add(details("book:" + company, "the order book, bids and asks, best first", detailsOpen,
                ui::redraw, () -> orderBook(shareBook)));

        Node chart = sharePriceChart(company);
        Node body;
        if (wide && chart != null) {
            left.setMinWidth(0);
            HBox.setHgrow(left, Priority.ALWAYS);
            HBox both = new HBox(24, left, chart);
            both.setAlignment(Pos.TOP_LEFT);
            body = both;
        } else {
            if (chart != null) left.getChildren().add(chart);
            body = left;
        }
        VBox c = card(head("ITS OWNERS", ownersPolicy(company), null), body);
        if (!wide) {
            c.setMaxWidth(STATEMENT);
            c.setPrefWidth(STATEMENT);
        }
        return c;
    }

    /** The owners card's (i): its regime and payout policy, and what it has raised and paid since founding. */
    String ownersPolicy(int company) {
        Equity register = ui.game.getEquity();
        String regime = switch (register.getRegime(company)) {
            case NEW -> "new: every plan is part shares, no record yet";
            case GOOD -> "a good year: it raises ahead of its plans";
            case NORMAL -> "a normal year: it borrows for its plans";
            case BAD -> "a bad year: it does not go to the market";
        };
        // The bank's payout is its own capital rule since 0.7.8 (Bank, WHAT IT
        // DOES WITH ITS PROFIT), not the register's share of a profitable month.
        Bank lender = ui.game.getBank();
        // ...against everything it has lent too, since round 2 of 0.7.11
        // (Bank.leverageTarget()); and a sector's payout is after the
        // principal its month repaid (Equity.dividendDue()).
        String policy = company == Equity.BANK
                ? String.format("It holds capital to its own target, %.1f%% of its weighted book or %.1f%%"
                        + " of everything it has lent, whichever asks more, and is %s",
                        lender.capitalTarget() * 100, lender.leverageTarget() * 100, lender.payoutDecision())
                : String.format("It wants %.0f%% of its balance sheet as equity and pays out %.0f%% of what a"
                        + " month's profit leaves after the principal it repaid",
                        register.getTargetEquityShare(company) * 100, Equity.PAYOUT * 100);
        return String.format("%s. %s. Raised %s from the households and %s abroad since founding;"
                + " paid them %s and %s.",
                regime.substring(0, 1).toUpperCase() + regime.substring(1), policy,
                tightMoney(toDollars(register.getLifetimeRaisedHome(company))),
                tightMoney(toDollars(register.getLifetimeRaisedAbroad(company))),
                tightMoney(toDollars(register.getLifetimeDividendsHome(company))),
                tightMoney(toDollars(register.getLifetimeDividendsAbroad(company))));
    }

    /** One small figure in a strip: its name over it, a line under it, and an (i) when `info` is not null. */
    static VBox miniFigure(String name, String value, String under, String tone, String info) {
        Label n = new Label(name);
        n.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Node top = n;
        if (info != null) {
            HBox t = new HBox(Palette.GAP_TIGHT, n, infoButton(info, false));
            t.setAlignment(Pos.CENTER_LEFT);
            top = t;
        }
        VBox cell = new VBox(1, top, figure(value, Palette.SIZE_BODY + 3, tone));
        if (under != null) cell.getChildren().add(caption(under, Palette.TEXT_MUTED));
        cell.setMaxWidth(170);
        return cell;
    }

    /** The order book's two sides, five levels each, as the old block's tables - in plain figures (B10). */
    VBox orderBook(OrderBook shareBook) {
        VBox box = new VBox(6);
        for (OrderBook.Side side : OrderBook.Side.values()) {
            boolean bids = side == OrderBook.Side.BUY;
            List<OrderBook.Level> levels = shareBook.levels(side);
            if (levels.isEmpty()) {
                box.getChildren().add(statementLine(bids ? "Bids" : "Asks", bids ? "nobody is bidding" : "nobody is selling",
                        Palette.TEXT_MUTED));
                continue;
            }
            javafx.scene.layout.GridPane t = grid(new double[] {130, 110, 70}, rightAfterFirst(3));
            gridHead(t, bids ? "bid, best first" : "ask, best first", "shares", "orders");
            int line = 1;
            for (OrderBook.Level level : levels) {
                if (line > 5) break;
                t.add(gridCell(tightMoney(toDollars(level.price()), false), Palette.TEXT_BODY,
                        Palette.SIZE_CAPTION, false), 0, line);
                t.add(gridCell(String.format("%,.0f", level.quantity()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, line);
                t.add(gridCell(String.valueOf(level.orders()), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
                line++;
            }
            box.getChildren().add(t);
        }
        return box;
    }

    /**
     * Who owns a company, for the Bank's Owners page: the owners card, in its
     * statement column (0.7.30, D11; the block of statement lines this drew
     * until then is the card's now).
     */
    void ownersBlock(VBox column, int company, double bookEquity, double netIncome) {
        VBox c = ownersCard(company, bookEquity, netIncome, false);
        if (c == null) return;
        column.getChildren().add(statementHead("Its owners"));
        column.getChildren().add(c);
    }

    /**
     * One company's share price over the city's life: the last trade (the
     * desk's quote until 0.7.12 round 2) against what the register says a
     * share is worth, both per founding share - the trend chart, with its
     * note behind an (i) (0.7.30). Null with nothing recorded.
     */
    Node sharePriceChart(int company) {
        HistorySave h = ui.game.getHistorySave();
        String name = Equity.COMPANIES[company];
        double[] price = h.aligned(HistorySave.priceKey(name));
        double[] worth = h.aligned(HistorySave.valueKey(name));
        boolean any = false;
        for (double v : price) if (!Double.isNaN(v)) { any = true; break; }
        if (!any) return null;
        double factor = ui.game.getExchange().getSplitFactor(company);
        String note = "Per founding share: a split moves every holder's count and the price "
                + "together, so the line does not"
                + (Math.abs(factor - 1) > 1e-9
                        ? String.format(" — one founding share is %s shares today.",
                                factor >= 1 ? String.format("%,.0f", factor) : String.format("%.4f", factor))
                        : ".")
                + " Where the last trade sits above what the register reckons, buyers paid more "
                + "than it is worth; below, sellers took less to get out.";
        // The business area's violet (0.7.23; it was the money blue), the
        // register's reckoning in the grey a reference line is drawn in.
        VBox chart = trendChart(h.getMonth(),
                new String[] {"The last trade", "What the register reckons"},
                new double[][] {price, worth},
                new String[] {Palette.BUSINESS, Palette.RAMP_REST});
        VBox box = new VBox(4, head("WHAT A SHARE HAS BEEN WORTH", note, null), chart);
        box.setMinWidth(STATEMENT - 40);
        box.setMaxWidth(STATEMENT);
        return box;
    }

    /* =====================================================================
       CASH AND CREDIT (0.7.30): A BRIDGE, AND WHAT ITS BORROWING COSTS

       The question: why did its cash move, and what does its borrowing cost?
       The month's cash as a bridge - the cash it started with and ended with
       written at the ends, every flow a step between them - over the
       reconciliation, which now carries the two flows it left out (B2): what
       was stolen from its till, and what it spent buying back its own shares.
       Beside it: its rate in parts, its leverage on a scale to the default
       point, and what it owes by kind. Every credit line stays, in a grid.
       ===================================================================== */

    void cashAndDebtPage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {

        boolean known = !then.isEmpty();
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        String key = sector.key();

        // The bridge: every flow this month, a step.
        List<Step> steps = cashSteps(now);
        double premisesNow = now.spentOnBuildings() - now.salvage();
        double gap = now.unexplained();
        Node bridge;
        if (steps.isEmpty()) {
            bridge = caption("Nothing moved its cash this month.", Palette.TEXT_MUTED);
        } else {
            Waterfall fall = waterfall(steps, SectorScreen::m, 0, 170);
            HBox.setHgrow(fall, Priority.ALWAYS);
            HBox row = new HBox(16, endFigure("AT THE START", m(now.openingCash()), Palette.TEXT_HEAD), fall,
                    endFigure("AT THE END", m(now.cash()), now.cash() < 0 ? Palette.BAD : Palette.TEXT_HEAD));
            row.setAlignment(Pos.CENTER_LEFT);
            bridge = row;
        }
        cashPage(page, sector, now, then, known, credit, key, bridge, premisesNow, gap);
    }

    /** The bridge's steps: every flow of the month that moved a dollar, in the reconciliation's order - the two it missed (B2) last but the residual. */
    static List<Step> cashSteps(SectorBooks.SectorMonth now) {
        double premisesNow = now.spentOnBuildings() - now.salvage();
        double[][] flows = {
                { now.netIncome() }, { now.paidEarlier() }, { now.borrowed() }, { -now.repaid() },
                { now.bondsIssued() }, { -now.bondsRepaid() }, { -now.bondsBought() }, { now.bondCoupons() },
                { -premisesNow }, { -now.salvage() }, { now.fromTheCity() }, { now.depositInterest() },
                { now.forgiven() }, { -now.investedAbroad() }, { now.equityRaised() }, { -now.dividendsPaid() },
                { -now.sharesBoughtBack() }, { -now.stolen() } };
        String[] flowNames = { "Kept from trading", "Stock paid for earlier", "Borrowed", "Loans repaid",
                "Raised on bonds", "Bonds repaid", now.bondsBought() >= 0 ? "Bought others' bonds" : "Others' bonds sold",
                "Coupons", premisesNow >= 0 ? "Its premises" : "Buildings sold back",
                now.salvage() > 0 ? "Scrapped plant bought" : "Scrapped plant sold", "Subsidy", "Bank interest",
                "Forgiven", now.investedAbroad() >= 0 ? "Sent abroad" : "Brought home", "From shareholders",
                "To shareholders", "Bought back its shares", "Stolen" };
        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < flows.length; i++) {
            double v = flows[i][0];
            if (Math.abs(toDollars(v)) < .5) continue;
            steps.add(Step.of(flowNames[i], v, v >= 0 ? Palette.REVENUE_RAMP[2] : Palette.SPENDING_RAMP[2]));
        }
        double gap = now.unexplained();
        if (Math.abs(toDollars(gap)) > 1) steps.add(Step.of("Not accounted for", gap, Palette.WARN));
        return steps;
    }

    /** The rest of Cash & debt: the bridge's card, the can't-borrow line, the reconciliation and its pictures, and the credit grid. */
    void cashPage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, boolean known,
                  BusinessDebtManager credit, String key, Node bridge, double premisesNow, double gap) {
        page.getChildren().add(card(head("WHERE THE CASH WENT, " + CityCalendar.format(now.month()).toUpperCase(),
                "The cash it started the month with and ended it with, at the ends; between them every way this "
                + "model moves a business's cash, each a step - in, blue; out, sand - on a scale of the steps, so a "
                + "small one beside a large balance is still seen. The statement under it is the same month, line by "
                + "line.", null), bridge));
        if (credit.isBorrowingBlocked(key)) {
            page.getChildren().add(alertLine(Icons.BANK, Palette.BAD,
                    "It cannot borrow for another " + credit.getBlockedMonths(key) + " months", String.format(
                    "This sector went under - it had nothing left, and its loans were written off whole - and no "
                    + "lender will write it a loan for another %d months. It can still build whatever its own cash "
                    + "covers, and nothing else — which is why a sector that goes under tends to stay small long "
                    + "after the month that broke it.", credit.getBlockedMonths(key)), null));
        }

        // The reconciliation.
        VBox column = new VBox(0);
        column.getChildren().add(statementHead("Where the cash went"));
        column.getChildren().add(bookHead(CityCalendar.format(now.month())));

        column.getChildren().add(bookLine("Cash at the start",
                now.openingCash(), then.openingCash(), known, null));
        column.getChildren().add(bookLine("Kept from trading",
                now.netIncome(), then.netIncome(), known,
                now.netIncome() < 0 ? Palette.BAD : null));
        // ...plus the stock it built with this month and paid for when it
        // bought it (0.7.8): a cost in what it kept, and no cash this month.
        if (now.paidEarlier() != 0 || then.paidEarlier() != 0) {
            column.getChildren().add(bookLine("Stock used, paid for when bought",
                    now.paidEarlier(), then.paidEarlier(), known, null));
        }
        column.getChildren().add(bookLine("Borrowed",
                now.borrowed(), then.borrowed(), known, null));
        column.getChildren().add(bookLine("Loans repaid",
                -now.repaid(), -then.repaid(), known, null));
        // ...and its bonds (0.7.12): what they raised after their costs, the
        // face it repaid, what it spent on other sectors' and their coupons.
        if (now.bondsIssued() != 0 || then.bondsIssued() != 0) {
            column.getChildren().add(bookLine("Raised on bonds, after their costs",
                    now.bondsIssued(), then.bondsIssued(), known, null));
        }
        if (now.bondsRepaid() != 0 || then.bondsRepaid() != 0) {
            column.getChildren().add(bookLine("Bonds repaid",
                    -now.bondsRepaid(), -then.bondsRepaid(), known, null));
        }
        if (now.bondsBought() != 0 || then.bondsBought() != 0) {
            column.getChildren().add(bookLine(
                    now.bondsBought() >= 0 ? "Spent on other businesses' bonds" : "Other businesses' bonds sold or repaid",
                    -now.bondsBought(), -then.bondsBought(), known, null));
        }
        if (now.bondCoupons() != 0 || then.bondCoupons() != 0) {
            column.getChildren().add(bookLine("Coupons on the bonds it holds",
                    now.bondCoupons(), then.bondCoupons(), known, null));
        }
        // ...less the part that was scrapped plant's material (0.7.8), which
        // is its own line: the builders' purchase, or the seller's sale.
        double premisesThen = then.spentOnBuildings() - then.salvage();
        if (premisesNow != 0 || premisesThen != 0) {
            column.getChildren().add(bookLine(
                    premisesNow >= 0 ? "Spent on its own premises"
                                     : "Sold buildings back",
                    -premisesNow, -premisesThen, known, null));
        }
        if (now.salvage() != 0 || then.salvage() != 0) {
            boolean bought = (now.salvage() != 0 ? now.salvage() : then.salvage()) > 0;
            column.getChildren().add(bookLine(
                    bought ? "Material bought from scrapped plant"
                           : "Scrapped plant's material, sold to the builders",
                    -now.salvage(), -then.salvage(), known, null));
        }
        // Sales tax is NOT a line here any more - it is on the income statement
        // above, so it is already inside "What it kept". See SectorBooks.
        if (now.fromTheCity() != 0 || then.fromTheCity() != 0) {
            column.getChildren().add(bookLine("Subsidy from the city",
                    now.fromTheCity(), then.fromTheCity(), known, null));
        }
        if (now.depositInterest() != 0 || then.depositInterest() != 0) {
            column.getChildren().add(bookLine("Interest on its bank balance",
                    now.depositInterest(), then.depositInterest(), known, null));
        }
        // The four lines the reconciliation had and the screen did not,
        // until 2026-09-10 (evening): what its creditors forgave, what it
        // moved abroad or brought home, what its owners put in, and what it
        // paid them. Without them "Not accounted for" stayed at zero while
        // the lines on the screen did not add up to the cash at the end.
        if (now.forgiven() != 0 || then.forgiven() != 0) {
            column.getChildren().add(bookLine("Forgiven by its creditors",
                    now.forgiven(), then.forgiven(), known, null));
        }
        if (now.investedAbroad() != 0 || then.investedAbroad() != 0) {
            column.getChildren().add(bookLine(
                    now.investedAbroad() >= 0 ? "Sent abroad for the world's rate" : "Brought home from abroad",
                    -now.investedAbroad(), -then.investedAbroad(), known, null));
        }
        if (now.equityRaised() != 0 || then.equityRaised() != 0) {
            column.getChildren().add(bookLine("Raised from its shareholders",
                    now.equityRaised(), then.equityRaised(), known, null));
        }
        if (now.dividendsPaid() != 0 || then.dividendsPaid() != 0) {
            column.getChildren().add(bookLine("Paid to its shareholders",
                    -now.dividendsPaid(), -then.dividendsPaid(), known, null));
        }
        // ...and the two it still left out until 0.7.30 (B2): both are in
        // unexplained(), so "Not accounted for" stayed hidden while the lines
        // did not add up to the cash at the end.
        if (now.sharesBoughtBack() != 0 || then.sharesBoughtBack() != 0) {
            column.getChildren().add(bookLine("Bought back its own shares",
                    -now.sharesBoughtBack(), -then.sharesBoughtBack(), known, null));
        }
        if (now.stolen() != 0 || then.stolen() != 0) {
            column.getChildren().add(bookLine("Stolen from its till",
                    -now.stolen(), -then.stolen(), known, null));
        }

        if (Math.abs(toDollars(gap)) > 1) {
            column.getChildren().add(withInfo(bookLine("Not accounted for",
                    gap, then.unexplained(), known, Palette.WARN),
                    "The lines above are every way this model moves a sector's cash. A residual means something "
                    + "else touched it — worth knowing about rather than worth hiding. SectorBooksCheck audits this "
                    + "identity on every sector every month, so a line appearing here is news."));
        }

        VBox end = bookTotal("Cash at the end",
                now.cash(), then.cash(), known,
                now.cash() < 0 ? Palette.BAD : null);
        column.getChildren().add(now.cash() < 0 ? withInfo(end, "A negative balance is not an error. A maturing "
                + "loan takes cash under before the replacement is written, which is what a business with no spare "
                + "cash actually does.") : end);

        page.getChildren().add(statementAndRatios(column, creditPictures(sector, now)));

        page.getChildren().add(sectionHead("WHAT IT BORROWS ON", null, hint("every line of its credit")));
        page.getChildren().add(factGrid(creditLines(sector, now), 2, SectorScreen::toneColour));
    }

    /** A figure at a bridge's end: its name over it. */
    static VBox endFigure(String name, String value, String tone) {
        Label n = new Label(name);
        n.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        VBox v = new VBox(2, n, figure(value, Palette.SIZE_LEAD, tone));
        v.setAlignment(Pos.CENTER);
        v.setMinWidth(Region.USE_PREF_SIZE);
        return v;
    }

    /** The spread paragraph, behind the rate bar's (i) (the old page's note under "It pays"). */
    String spreadInfo(String key) {
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        // Its parts as the rate was struck, as the bar draws them (0.7.38); today's for a sector not priced yet.
        BusinessDebtManager.QuoteParts q = credit.quoteParts(key);
        double risk = q != null ? q.risk() : credit.getRiskSpread(key);
        double record = q != null ? q.record() : credit.getRecordSurcharge(key), spread = credit.getSpread(key);
        double conc = q != null ? q.concentration() : credit.getConcentrationCharge(key);
        return String.format(
                "The bank's prime is %.2f%%, so this sector is paying %.2f points "
                + (spread < 0 ? "under" : "over") + ": its own expected loss, %.2f points - %s of its firms default a year "
                + "at its leverage over its last quarter, %.2f, and the bank loses %.0f%% of what they owe, less the %.1f%% "
                + "prime already carries%s%s. A new loan is priced at the leverage it would leave it "
                + "at, the building it buys counted, not at this one.",
                credit.getPrimeRate() * 100,
                unsigned0(Math.abs(spread) * 100, 2),
                risk * 100,
                BankScreen.defaultShare(credit.getQuarterDefaultRate(key)),
                credit.getQuarterLeverage(key),
                BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT * 100, Bank.BASE_LOSS_RATE * 100,
                record > 0 ? String.format(" - and %.2f points for its record", record * 100) : "",
                conc > 0 ? String.format(" - and %.2f points for the concentration of the "
                        + "bank's book in it", conc * 100)
                : conc < 0 ? String.format(" - less %.2f points because the bank's book is "
                        + "light in it", -conc * 100) : "");
    }

    /**
     * Beside the reconciliation: its rate in parts - prime, its own risk, its
     * record, the book's concentration (BusinessDebtManager's pricing); its
     * leverage on a scale to the default point, with the bank's watch line
     * and owing what it owns marked; and what it owes by kind.
     */
    VBox creditPictures(Sector sector, SectorBooks.SectorMonth now) {
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        String key = sector.key();

        // The rate.
        // What NEW borrowing costs it today, and the parts it was struck from (0.7.38:
        // BusinessDebtManager.quoteParts(), as the Bank's ladder draws them) - today's reads
        // only for a sector not priced yet: the books' month rate is OWES's.
        BusinessDebtManager.QuoteParts q = credit.quoteParts(key);
        double prime = q != null ? q.prime() : credit.getPrimeRate();
        double risk = q != null ? q.risk() : credit.getRiskSpread(key);
        double record = q != null ? q.record() : credit.getRecordSurcharge(key);
        double conc = q != null ? q.concentration() : credit.getConcentrationCharge(key);
        double rate = credit.getRate(key);
        List<Segment> parts = new ArrayList<>();
        parts.add(new Segment(prime, Palette.MONEY, false, null, null, String.format("the bank's prime  %.2f%%", prime * 100), null));
        if (risk > 0) parts.add(new Segment(risk, Palette.MONEY_LIGHT, false, null, null, String.format("its own risk  %.2f points", risk * 100), null));
        if (record > 0) parts.add(new Segment(record, Palette.RAMP_REST, false, null, null, String.format("its record  %.2f points", record * 100), null));
        if (conc > 0) parts.add(new Segment(conc, Palette.TEXT_SPENT, false, null, null, String.format("the book's concentration  %.2f points", conc * 100), null));
        double sum = prime + Math.max(0, risk) + Math.max(0, record) + Math.max(0, conc);
        // A concentration charge can be a discount (a sector the book is light in): then the
        // rate does not end where the bar does, and a white mark says where. (Until 0.7.38 its
        // own risk here was the curve as the statements read now, which a month after the rate
        // was struck parted from it - the Bank tab's B4.)
        List<Tick> at = Math.abs(rate - sum) > 1e-9 ? List.of(new Tick(rate, Palette.TEXT_HEAD, 2, null,
                conc < 0 ? String.format("it pays %.2f%%, after the book's concentration takes %.2f points off", rate * 100, -conc * 100)
                        : String.format("new borrowing costs it %.2f%% today", rate * 100))) : List.of();
        SegmentBar rateBar = segmentBar(parts, Math.max(sum, rate), at, 0, 10);
        Label rateFigure = figure(String.format("%.2f%% a year", unsigned0(rate * 100, 2)), Palette.SIZE_BODY + 3,
                rate > prime * 2 ? Palette.BAD : rate > prime * 1.4 ? Palette.WARN : Palette.GOOD);
        String rateWords = String.format("prime %.2f%% + its risk %.2f", prime * 100, unsigned0(risk * 100, 2))
                + (record > 0 ? String.format(" + its record %.2f", record * 100) : "")
                + (conc > 0 ? String.format(" + concentration %.2f", conc * 100)
                   : conc < 0 ? String.format(" − concentration %.2f", -conc * 100) : "") + " points";

        // The leverage, to the default point.
        double lev = credit.getLeverage(key);
        String levTone = lev >= BusinessDebtManager.INSOLVENCY_TRIGGER ? Palette.BAD
                : lev > Bank.SECTOR_WATCH_LEVERAGE ? Palette.WARN : Palette.GOOD;
        SegmentBar levBar = segmentBar(List.of(Segment.of(Math.min(Math.max(0, lev), BusinessDebtManager.INSOLVENCY_TRIGGER), levTone)),
                BusinessDebtManager.INSOLVENCY_TRIGGER, List.of(
                        new Tick(Bank.SECTOR_WATCH_LEVERAGE, Palette.TEXT_LABEL, 2,
                                String.format("watch %.2f", Bank.SECTOR_WATCH_LEVERAGE), "Past this the bank watches the sector's loans."),
                        new Tick(1.0, Palette.TEXT_LABEL, 2, "owes all it owns", "Leverage 1: it owes what it owns.")),
                0, 8);

        // What it owes, by kind.
        double loans = credit.getTermLoanPrincipal(key), bonds = credit.getBondPrincipal(key);
        double mortgages = credit.getMortgagePrincipal(key), interim = credit.getInterimPrincipal(key);
        List<Segment> mix = new ArrayList<>();
        List<HBox> mixKey = new ArrayList<>();
        String[] mixNames = { "bank loans", "bonds", "mortgages", "interim financing" };
        String[] mixColours = { Palette.LADDER[0], Palette.LADDER[1], Palette.LADDER[2], Palette.RAMP_REST };
        double[] mixFigures = { loans, bonds, mortgages, interim };
        for (int i = 0; i < mixFigures.length; i++) {
            if (!(mixFigures[i] > 0)) continue;
            mix.add(new Segment(mixFigures[i], mixColours[i], false, null, null, mixNames[i] + "  " + m(mixFigures[i]), null));
            mixKey.add(keyPart(mixColours[i], mixNames[i], m(mixFigures[i])));
        }
        BusinessDebtManager.Plan plan = credit.getShortfallPlan(key);
        String mixInfo = "It takes the cheaper of the two: the bank's rate with its fee over the loan's term, or the "
                + "coupon the bond book would sell at with its issuing costs over the bond's ten years - and never more, "
                + "together, than the bank would lend. When its firms default, its bank loans get back more of what they "
                + "are owed than its bonds do; interim financing, lent after a default for the bills it could not pay, "
                + "ranks ahead of both."
                + (plan != null ? "\n\nIts last month's borrowing to cover its cash"
                        + Game.financingWords(plan).replaceFirst(" - ", ": ") + "." : "");

        VBox c = ratioColumn("WHAT ITS BORROWING COSTS",
                head("NEW BORROWING COSTS", spreadInfo(key), rateFigure), rateBar, caption(rateWords, Palette.TEXT_MUTED),
                head("LEVERAGE", "What it owes over what it owns, to the default point at "
                        + String.format("%.2f", BusinessDebtManager.INSOLVENCY_TRIGGER) + " - past it the bank lends it "
                        + "nothing until it is under. The bar is its leverage now; its rate is priced off its last "
                        + "quarter's.", figure(String.format("%.2f", unsigned0(lev, 2)), Palette.SIZE_BODY + 3, levTone)),
                levBar,
                head("WHAT IT OWES, BY KIND", mixInfo, figure(m(credit.getPrincipal(key)), Palette.SIZE_BODY + 3, Palette.TEXT_HEAD)),
                mix.isEmpty() ? caption("it owes nothing", Palette.TEXT_MUTED) : segmentBar(mix, 0, List.of(), 0, 10),
                mix.isEmpty() ? null : key(mixKey));
        c.setPrefWidth(320);
        c.setMaxWidth(360);
        return c;
    }

    /** Every credit line the page had, as lines for the fact grid - its paragraphs as the notes under them (the grid's (i)s). */
    List<Sector.Line> creditLines(Sector sector, SectorBooks.SectorMonth now) {
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        String key = sector.key();
        List<Sector.Line> l = new ArrayList<>();
        Sector.Line.Tone none = Sector.Line.Tone.NONE;
        l.add(Sector.Line.head("Its rate and what it owes"));
        double rate = credit.getRate(key);
        l.add(Sector.Line.of("New borrowing costs it", String.format("%.2f%% a year", unsigned0(rate * 100, 2)),
                rate > credit.getPrimeRate() * 2 ? Sector.Line.Tone.BAD
                        : rate > credit.getPrimeRate() * 1.4 ? Sector.Line.Tone.WARN : Sector.Line.Tone.GOOD));
        if (Math.abs(rate - now.rate()) >= .00005) {
            l.add(Sector.Line.of("...the month's books struck it at", String.format("%.2f%% a year", unsigned0(now.rate() * 100, 2))));
        }
        l.add(Sector.Line.note(spreadInfo(key)));
        l.add(Sector.Line.of("Leverage", String.format("%.2f", unsigned0(now.leverage(), 2)),
                now.leverage() > .7 ? Sector.Line.Tone.BAD : now.leverage() > .5 ? Sector.Line.Tone.WARN : Sector.Line.Tone.GOOD));
        l.add(Sector.Line.of("Principal outstanding", tightMoney(toDollars(credit.getPrincipal(key)), false)));
        l.add(Sector.Line.of("Loans running", String.valueOf(credit.getLoanCount(key))));
        /*
         * INTERIM FINANCING (0.7.12, round 5): what the bank lent it after a
         * default for the bills the month left unpaid, ranked ahead of all
         * its other debt.
         */
        double interim = credit.getInterimPrincipal(key);
        if (interim > 0) {
            int n = credit.getInterimCount(key);
            l.add(Sector.Line.of("...of it, interim financing",
                    tightMoney(toDollars(interim), false) + String.format("   %d loan%s, ranked first", n, n == 1 ? "" : "s"),
                    Sector.Line.Tone.WARN));
        }
        l.add(Sector.Line.of("Interest this month", tightMoney(toDollars(credit.getMonthlyInterest(key)), false), none));
        // A sector is many firms (0.7.8): the share of them that default a
        // year at its leverage, the curve the bank writes the month's slice
        // off and sets its allowance aside by.
        if (credit.getPrincipal(key) > 0) {
            l.add(Sector.Line.of("Its firms that default a year", BankScreen.defaultShare(credit.getDefaultRate(key)),
                    credit.getLeverage(key) >= BusinessDebtManager.INSOLVENCY_TRIGGER ? Sector.Line.Tone.BAD
                            : credit.getLeverage(key) > Bank.SECTOR_WATCH_LEVERAGE ? Sector.Line.Tone.WARN : none));
        }
        /*
         * ITS BANK LOANS AND ITS BONDS (0.7.12), each at what it costs. The
         * screens say "bank loans" for what Jerus calls notes, because the
         * city's own short paper is already called a note (CorporateBond).
         */
        BondMarket market = ui.game.getBondMarket();
        double bonds = credit.getBondPrincipal(key), loans = credit.getLoanPrincipal(key);
        if (bonds > 0 || market.getLifeIssues() > 0) {
            l.add(Sector.Line.head("Its bank loans and its bonds"));
            l.add(Sector.Line.of("Bank loans", tightMoney(toDollars(loans), false)
                    + (loans > 0 ? String.format("   at %.2f%% on average", credit.getLoanInterest(key) * 12 / loans * 100) : "")));
            l.add(Sector.Line.of("Bonds", tightMoney(toDollars(bonds), false)
                    + (bonds > 0 ? String.format("   at %.2f%% on average", market.averageCoupon(key) * 100) : "")));
            if (loans + bonds > 0) {
                l.add(Sector.Line.of("...its debt in bonds", String.format("%.0f%%", bonds / (loans + bonds) * 100)));
            }
        }
        if (market.faceHeldBy(key) > 0) {
            l.add(Sector.Line.of("Other businesses' bonds it holds", tightMoney(toDollars(market.faceHeldBy(key)), false)));
        }
        /*
         * ITS INSURED MORTGAGES (0.7.11), beside its other debt: the
         * landlords buy their buildings on them. Shown for any sector that
         * owes one, and always for the landlords, who say what a new one
         * would cost.
         */
        int mortgages = credit.getMortgageCount(key);
        if (mortgages > 0 || sector == ui.game.getSectors().realEstate()) {
            l.add(Sector.Line.head("Its insured mortgages"));
            if (mortgages == 0) {
                l.add(Sector.Line.of("None yet", String.format("%.2f%% a year", credit.getInsuredMortgageRate() * 100)));
                l.add(Sector.Line.note(String.format("Its next building would be financed at %.2f%% a year, fixed for %d years.",
                        credit.getInsuredMortgageRate() * 100, Mortgage.MORTGAGE_TERM_MONTHS / 12)));
            } else {
                l.add(Sector.Line.of(String.format("Owed on %d mortgage%s", mortgages, mortgages == 1 ? "" : "s"),
                        tightMoney(toDollars(credit.getMortgagePrincipal(key)), false)));
                l.add(Sector.Line.note(String.format(
                        "Insured by the city. The rate is fixed for %d years and renews at the day's; the "
                        + "payment pays it off over %d. Only the interest is a cost on its statement - the "
                        + "principal is money it owes going back.", Mortgage.MORTGAGE_TERM_MONTHS / 12,
                        Mortgage.MORTGAGE_AMORTIZATION_MONTHS / 12)));
                l.add(Sector.Line.of("...its other debt",
                        tightMoney(toDollars(credit.getPrincipal(key) - credit.getMortgagePrincipal(key)), false)));
                l.add(Sector.Line.of("At", String.format("%.2f%% a year, on average", credit.getMortgageRate(key) * 100)));
                l.add(Sector.Line.of("The monthly payment", tightMoney(toDollars(credit.getMortgagePayment(key)), false)));
                l.add(Sector.Line.of("Principal repaid this month",
                        tightMoney(toDollars(credit.getMortgageRepaidThisMonth(key)), false)));
                int next = credit.getNextRenewalMonth(key);
                l.add(Sector.Line.of("Next renewal", next < 0 ? "none"
                        : CityCalendar.format(next) + " (" + CityCalendar.until(ui.game.getMonth(), next) + ")"));
            }
        }
        // Its firms default a few at a time, so there is a figure wherever it
        // owes much; a total under a dollar is the curve's far tail.
        if (toDollars(credit.getWrittenOffTotal(key)) >= 1 || credit.getRestructureCount(key) > 0) {
            l.add(Sector.Line.head("What its lenders have lost"));
            l.add(Sector.Line.of("Written off this month", tightMoney(toDollars(now.writtenOff()), false),
                    credit.defaultsAreNews(key) ? Sector.Line.Tone.BAD : none));
            l.add(Sector.Line.note(String.format(
                    "Its firms default a few at a time as its leverage rises; the bank loses "
                    + "%.0f%% of what they owed it and their bondholders %.0f%%, and they keep their "
                    + "plant. Going under whole is having nothing left, or no lender willing to carry it past a "
                    + "default - most of what it owed is written off and it is shut out for a while.",
                    BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT * 100, BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT * 100)));
            l.add(Sector.Line.of("Written off in all", tightMoney(toDollars(credit.getWrittenOffTotal(key)), false)));
            // ...of which the city's insurance paid the bank the insured part (0.7.11).
            if (credit.getInsuredWrittenOffTotal(key) > 0) {
                l.add(Sector.Line.of("...of it off insured mortgages, which the city paid",
                        tightMoney(toDollars(credit.getInsuredWrittenOffTotal(key)), false)));
            }
            // ...of which its bondholders lost (0.7.12): each class at its own
            // recovery since round 2 (BusinessDebtManager, RECOVERIES BY INSTRUMENT).
            if (credit.getBondWrittenOffTotal(key) > 0) {
                l.add(Sector.Line.of("...and its bondholders lost, in all",
                        tightMoney(toDollars(credit.getBondWrittenOffTotal(key)), false)));
            }
            l.add(Sector.Line.of("Times it went under whole", String.valueOf(credit.getRestructureCount(key))));
        }
        return l;
    }

    /* =====================================================================
       THE INVESTORS (0.7.30): WHY ISN'T IT BUILDING, AND WHAT WOULD LET IT?

       WHAT THE BUSINESS IS THINKING, which is the one thing about these sectors
       a player cannot see anywhere and can very much act on. Every month each
       business decides whether to expand, and the decision it reached was
       already recorded - it was just never shown outside a one-line banner on
       the land screen.

       Its decision as one card in its kind's icon and colour (it read every
       word that was not "Holding" or "Declined" in green, so "Sold 3 ..." and
       "Could not build ..." read as good news - B7); each of its buildings with
       the first gate that building fails for investors now, as its build card
       says it; what stops it in three tiles; the rules it builds by as chips;
       and the four things the player controls as doors.
       ===================================================================== */

    /** WHAT YOU CONTROL's (i): the old page's last paragraph. */
    static final String CONTROL_INFO = "None of this is yours to set. These are private companies deciding for "
            + "themselves — what you control is the ground they can buy, the tax they pay, the wages they compete "
            + "against and the rate they borrow at.";

    /** The waiting-on-ground alert's (i). */
    static final String WAITING_INFO = "This business wants to build and there is nowhere to put it. It is the one "
            + "refusal on this page you can personally clear — buy a plot at the Land office and it will build next month.";

    void investorPage(VBox page, Sector sector, SectorBooks.SectorMonth now, BuildCard.SectorInvestors si, boolean animate) {

        BusinessInvestment plans = ui.game.getBusinessInvestment();
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        String key = sector.key();

        /* ------------------------- what it decided ------------------------- */
        page.getChildren().add(decisionCard(si, animate));
        if (si.landBlocked()) {
            page.getChildren().add(alertLine(Icons.LAND, Palette.WARN, "It is waiting on ground", WAITING_INFO,
                    doorPill("Land office", Icons.LAND, Palette.BUILDING, () -> ui.landScreen.showLandMenu())));
        }

        /* --------------------------- its buildings --------------------------- */
        page.getChildren().add(sectionHead("ITS BUILDINGS", "Each building it can put up, with the first gate "
                        + "investors would stop it at now - ore, a licence, staff, land, then money - the same answer its "
                        + "build card gives. Value added is what one makes less what it uses at today's prices; the "
                        + "investors' estimate is what one would make its owner a month, after wages, at the sector's "
                        + "operating rate.",
                hint("a row opens it on Build")));
        page.getChildren().add(buildingsList(si));

        /* ------------------------- and what stops it ------------------------- */
        int lossMonths = plans.getLossMonths(key);
        int limit = BusinessInvestment.RETIREMENT_LOSS_MONTHS;
        boolean nothingToSell = sector.buildingsStanding() <= 0;
        page.getChildren().add(sectionHead("WHAT STOPS IT", null, null));
        GridPane tiles = equalColumns(3, Palette.GAP_LOOSE);
        tiles.add(tile("CASH IT CAN SPEND", figure(m(now.cash()), Palette.SIZE_LEAD, now.cash() < 0 ? Palette.BAD : Palette.TEXT_HEAD),
                now.cash() < 0 ? "overdrawn" : "its own, before it borrows", null, null), 0, 0);
        boolean blocked = credit.isBorrowingBlocked(key);
        tiles.add(tile("CAN IT BORROW THE REST", figure(blocked ? "no" : "yes", Palette.SIZE_LEAD, blocked ? Palette.BAD : Palette.GOOD),
                blocked ? "ban, " + credit.getBlockedMonths(key) + " months left" : "no ban: at its own rate, within the bank's limits",
                blocked ? Palette.BAD : null, null), 1, 0);
        String losingWords = lossMonths == 0 ? "none"
                : lossMonths >= limit ? (nothingToSell ? "nothing left to sell" : "selling buildings back")
                : lossMonths + " of " + limit;
        HBox pipRow = new HBox(10, pips(Math.min(lossMonths, limit), limit,
                lossMonths >= limit ? Palette.BAD : Palette.WARN, 12),
                figure(lossMonths == 0 ? "none" : lossMonths + (lossMonths == 1 ? " month" : " months"), Palette.SIZE_BODY + 3,
                        lossMonths >= limit ? Palette.BAD : lossMonths > 0 ? Palette.WARN : Palette.TEXT_HEAD));
        pipRow.setAlignment(Pos.CENTER_LEFT);
        tiles.add(tile("MONTHS LOSING MONEY", pipRow, losingWords, lossMonths >= limit ? Palette.BAD : null,
                losingInfo(lossMonths, nothingToSell)), 2, 0);
        page.getChildren().add(tiles);
        if (lossMonths >= limit) {
            page.getChildren().add(alertLine(Icons.ALERT, nothingToSell ? Palette.WARN : Palette.BAD,
                    nothingToSell ? String.format("%d straight months of losses, and nothing standing to sell: it loses only its interest", lossMonths)
                            : "It is scrapping buildings", losingInfo(lossMonths, nothingToSell), null));
        }

        /* --------------------------- the rules --------------------------- */
        page.getChildren().add(sectionHead("THE RULES IT BUILDS BY", null, null));
        javafx.scene.layout.FlowPane rules = new javafx.scene.layout.FlowPane(10, 8);
        for (String[] r : rules(sector, now)) rules.getChildren().add(rule(r[0], r[1]));
        page.getChildren().add(rules);

        /* ------------------------- what you control ------------------------- */
        page.getChildren().add(sectionHead("WHAT YOU CONTROL", CONTROL_INFO, null));
        javafx.scene.layout.FlowPane doors = new javafx.scene.layout.FlowPane(18, 8);
        doors.getChildren().addAll(
                controlDoor(Icons.LAND, "the ground it can buy: Land office", () -> ui.landScreen.showLandMenu()),
                controlDoor(Icons.POLICY, "the tax it pays: Policy › Taxes", () -> openPolicy("Taxes", "Everything")),
                controlDoor(Icons.STAFF, "the wages it competes against: Policy › Wages", () -> openPolicy("Wages", "The floor")),
                controlDoor(Icons.BANK, "the rate it borrows at: Policy › Money", () -> openPolicy("Money", "The policy rate")));
        page.getChildren().add(doors);
    }

    /** The rules it builds by, as {chip, its old note or null}: the landlords' mortgage tests or everybody else's interest test, the horizon and trend, the population, the headroom, the order rule, and the builders' exception. */
    List<String[]> rules(Sector sector, SectorBooks.SectorMonth now) {
        BusinessInvestment plans = ui.game.getBusinessInvestment();
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        List<String[]> out = new ArrayList<>();
        boolean landlords = sector == ui.game.getSectors().realEstate();
        if (landlords) {
            /*
             * THE LANDLORDS ARE ASKED THE MORTGAGE LENDER'S QUESTIONS (0.7.11):
             * a down payment from their own funds, and the building's net rent
             * against the mortgage's payment.
             */
            String mortgage = String.format(
                    "An insured mortgage at %.2f%% for the rest, paid off over %d years: the rent it "
                    + "would let for, less its repairs and its property tax, against the payment on the "
                    + "loan with the insurance premium added. What its till and its owners cannot put "
                    + "down, it does not build; an order the lender will not carry is trimmed down until "
                    + "it does, and dropped if even one would not.",
                    credit.getInsuredMortgageRate() * 100, Mortgage.MORTGAGE_AMORTIZATION_MONTHS / 12);
            out.add(new String[] {String.format("puts %.0f%% down of its own", (1 - Mortgage.MORTGAGE_MAX_LOAN_TO_COST) * 100), mortgage});
            out.add(new String[] {String.format("rent covers the mortgage %.2f×", Mortgage.MORTGAGE_DEBT_COVERAGE), mortgage});
        } else {
            out.add(new String[] {String.format("earns %.2f× its interest", BusinessInvestment.PROFIT_OVER_INTEREST),
                    String.format("At this sector's own rate of %.2f%%, on whatever it has to borrow after its cash is "
                            + "spent. A plan that fails this is trimmed down until it passes, and dropped if even one "
                            + "unit cannot carry it.", now.rate() * 100)});
        }
        out.add(new String[] {String.format("plans %.0f mo ahead off a %d-mo trend",
                BusinessInvestment.PLANNING_HORIZON, BusinessInvestment.TREND_WINDOW), null});
        /*
         * PEOPLE A MONTH, not a percentage. getPopulationGrowth() is the average
         * monthly CHANGE in headcount over the window - the first draft of this
         * line multiplied it by a hundred and printed "-25,063.64% a month",
         * which is the kind of figure that makes a player distrust every other
         * number on the screen.
         */
        double trend = plans.getPopulationGrowth();
        out.add(new String[] {(trend >= 0 ? "+" : "-") + people(Math.abs(trend)) + " people a month",
                "Population is moving this much a month on that window, which is the demand every one of these "
                + "plans is drawn against."});
        out.add(new String[] {String.format("builds to %.0f%% spare", BusinessInvestment.TARGET_HEADROOM * 100), null});
        /*
         * THE LANDLORDS HOLD WORK, NOT ONE ORDER (0.7.17): as many orders as
         * keep what their sites owe within MAX_ORDER_MONTHS of the builders'
         * site output - see BusinessInvestment.withinMonthsOfWork(). Everybody
         * else still waits for one order to open before placing the next,
         * and sizes it to open inside MAX_ORDER_MONTHS at the share of the
         * builders it would get (BusinessInvestment.orderSize()).
         */
        if (landlords) {
            double months = ui.game.getSectors().realEstate().monthsOfWorkOnSite();
            out.add(new String[] {String.format("holds ≤ %.0f months of work on site", BusinessInvestment.MAX_ORDER_MONTHS)
                            + (Double.isNaN(months) ? " · none now" : String.format(" · %.1f now", months)),
                    String.format("It may keep ordering while what its sites owe, the next order included, is at most "
                            + "%.0f months of what the builders have left after the repairs. Each order is sized "
                            + "to stay inside that, and when its best home would not fit it orders the next "
                            + "smaller one that does.", BusinessInvestment.MAX_ORDER_MONTHS)});
        } else {
            out.add(new String[] {String.format("%s, ≤ %.0f months of the builders' work",
                            BusinessInvestment.MAX_CONCURRENT_ORDERS == 1 ? "one order at a time" : "several orders",
                            BusinessInvestment.MAX_ORDER_MONTHS),
                    String.format("And never more in one order than would be finished inside %.0f months at the "
                            + "share of the builders it would get beside everything already on site — a "
                            + "business cannot jam the queue for everybody else.", BusinessInvestment.MAX_ORDER_MONTHS)});
        }
        if (sector == ui.game.getSectors().construction()) {
            out.add(new String[] {String.format("expands past %.0f months of backlog",
                            BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING),
                    String.format("The builders are the exception: they expand off their order book rather than off "
                            + "population, and only once it is more than %.0f months deep.",
                            BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING)});
        }
        return out;
    }

    /** The LOSING tile's (i): where it stands against the rule, in the rule's own number (B9's "six" was a literal). */
    static String losingInfo(int lossMonths, boolean nothingToSell) {
        int limit = BusinessInvestment.RETIREMENT_LOSS_MONTHS;
        if (lossMonths >= limit) {
            return nothingToSell
                    ? String.format("%d straight months of losses, so this business has stopped expanding and would be "
                    + "selling - but nothing of it is standing, so there is nothing to sell. It loses only the interest "
                    + "on what it owes, and a single profitable month resets the count.", limit)
                    : String.format("%d straight months of losses, so this business has stopped expanding and started "
                    + "selling. It sheds capacity it is not using — anything over %.0f%% slack — and never more than "
                    + "%.0f%% of what it owns in one month, so the retreat takes a while and is visible while it happens.",
                    limit, BusinessInvestment.RETIREMENT_SLACK * 100, BusinessInvestment.MAX_RETIREMENT_FRACTION * 100);
        }
        if (lossMonths > 0) {
            return String.format("At %d it stops expanding and starts selling buildings back. It is at %d. A single "
                    + "profitable month resets the count.", limit, lossMonths);
        }
        return String.format("A business that loses money for %d straight months stops expanding and starts selling its "
                + "buildings back to the city. This one is not close.", limit);
    }

    /**
     * Its decision as a card, 56 high: the kind's icon in a square, the kind
     * in a word, and the word whole; Retail's bank branches a second line;
     * a "Built" month flashing once with what it built.
     */
    VBox decisionCard(BuildCard.SectorInvestors si, boolean animate) {
        VBox card = investorsLine(si, animate, 30);
        // The decision card has no door to itself.
        HBox first = (HBox) card.getChildren().get(0);
        first.getChildren().remove(first.getChildren().size() - 1);
        card.setMinHeight(56);
        card.setStyle(card.getStyle().replace("-fx-padding: 6 12 6 10;", "-fx-padding: 10 14 10 12;"));
        if (si.word().isEmpty()) {
            card.getChildren().add(caption("Every month each business decides whether to expand; the word is filed when a "
                    + "month runs, and is not saved.", Palette.TEXT_MUTED));
        }
        return card;
    }

    /** Each building it can put up: its icon and name, how many stand and are on site, its value added, the investors' estimate, and its first gate. */
    VBox buildingsList(BuildCard.SectorInvestors si) {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(0);
        grid.setMaxWidth(Double.MAX_VALUE);
        double[] widths = { 230, 130, 130, 140, -1 };
        for (double w : widths) {
            javafx.scene.layout.ColumnConstraints c = new javafx.scene.layout.ColumnConstraints();
            if (w > 0) { c.setPrefWidth(w); c.setMinWidth(w * .7); }
            else { c.setHgrow(Priority.ALWAYS); c.setMinWidth(160); }
            grid.getColumnConstraints().add(c);
        }
        String[] heads = { "building", "standing", "value added", "investors' estimate", "the first gate it fails" };
        for (int i = 0; i < heads.length; i++) {
            Label h = new Label(heads[i]);
            h.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
            grid.add(h, i, 0);
        }
        int row = 1;
        BuildingManager bm = ui.game.getBuildingManager();
        for (BuildCard.Investors inv : si.buildings()) {
            BuildingsTemplate t = null;
            for (BuildingsTemplate x : bm.getTemplates()) if (x.getName().equals(inv.building())) { t = x; break; }
            if (t == null) continue;
            final BuildingsTemplate template = t;
            BuildAdvice.Category cat = BuildAdvice.categoryOf(t.getCategory());
            Label name = new Label(t.getName());
            name.setWrapText(true);
            name.setStyle(Palette.strong(Palette.SIZE_BODY, Palette.TEXT_HEAD));
            HBox who = new HBox(8, icon(cat == null ? Icons.SECTOR : Icons.ofCategory(cat.name()), Palette.BUSINESS, 16), name);
            who.setAlignment(Pos.CENTER_LEFT);
            int owned = bm.getQuantity(t.getId());
            VBox have = new VBox(0, caption("you have " + String.format("%,d", owned), Palette.TEXT_BODY));
            if (inv.onSite() > 0) have.getChildren().add(caption(String.format("%,d on site", inv.onSite()), Palette.BUILDING));
            BuildCard.Kind kind = BuildCard.kindOf(t);
            boolean measured = kind == BuildCard.Kind.MAKER || kind == BuildCard.Kind.OFFICE;
            double va = measured ? BuildCard.valueAdded(ui.game, t) : Double.NaN;
            Label value = figure(measured ? m(va) + "/mo" : "—", Palette.SIZE_BODY, measured ? Palette.TEXT_HEAD : Palette.TEXT_SPENT);
            Label estimate = figure(Double.isFinite(inv.estimate()) ? m(inv.estimate()) + "/mo" : "—", Palette.SIZE_BODY,
                    inv.estimate() < 0 ? Palette.BAD : Palette.TEXT_HEAD);
            Node gate;
            if (inv.own() == null) {
                HBox ok = new HBox(6, icon(Icons.TICK, Palette.GOOD, 13), caption("passes all five", Palette.TEXT_LABEL));
                ok.setAlignment(Pos.CENTER_LEFT);
                gate = ok;
            } else {
                Label why = caption(ui.buildScreen.gateWords(inv.own()), Palette.WARN);
                why.setMinWidth(0);
                HBox no = new HBox(6, icon(gateIcon(inv.own().kind()), Palette.WARN, 13), why);
                no.setAlignment(Pos.CENTER_LEFT);
                gate = no;
            }
            Node[] cells = { who, have, value, estimate, gate };
            for (int i = 0; i < cells.length; i++) {
                javafx.scene.layout.StackPane cell = new javafx.scene.layout.StackPane(cells[i]);
                javafx.scene.layout.StackPane.setAlignment(cells[i], Pos.CENTER_LEFT);
                cell.setMinHeight(40);
                cell.setStyle("-fx-padding: 4 0 4 0; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 1 0 0 0;");
                grid.add(cell, i, row);
            }
            final int r = row;
            for (Node n : grid.getChildren()) {
                Integer at = GridPane.getRowIndex(n);
                if (at == null || at != r) continue;
                n.setStyle(n.getStyle() + " -fx-cursor: hand;");
                n.setOnMouseClicked(e -> {
                    BuildAdvice.Category c = BuildAdvice.categoryOf(template.getCategory());
                    if (c != null) ui.buildScreen.openCategory(c.name());
                });
            }
            row++;
        }
        if (row == 1) {
            return new VBox(caption("No building of its own on the Build menu.", Palette.TEXT_MUTED));
        }
        VBox box = new VBox(grid);
        box.setStyle("-fx-padding: 8 14 8 14; -fx-background-color: " + Palette.RAISED + "; -fx-background-radius: 8;");
        return box;
    }

    /** A gate's icon: the deposit's pick, the licence's cap, the staffing test's person, the land, the money. */
    static String gateIcon(BuildCard.GateKind k) {
        switch (k) {
            case DEPOSIT:  return Icons.PICK;
            case LICENCE:  return Icons.EDUCATION;
            case STAFFING: return Icons.STAFF;
            case LAND:     return Icons.LAND;
            default:       return Icons.COIN;
        }
    }

    /** One of WHAT STOPS IT's tiles: its name, its figure, a line, a colour for its edge when it is the stop, and an (i). */
    static VBox tile(String name, Node figure, String line, String edge, String info) {
        Node top = head(name, info, null);
        VBox t = new VBox(4, top, figure, caption(line, Palette.TEXT_MUTED));
        t.setMaxWidth(Double.MAX_VALUE);
        t.setMinHeight(80);
        t.setStyle("-fx-padding: 10 14 10 14; -fx-background-color: " + Palette.RAISED + "; -fx-background-radius: 8;"
                + " -fx-border-radius: 8; -fx-border-color: " + (edge == null ? Palette.EDGE : edge) + ";");
        return t;
    }

    /** A rule as a chip, with its old note behind an (i). */
    static HBox rule(String text, String info) {
        Label c = chip(text, Palette.TEXT_LABEL);
        c.setStyle(c.getStyle() + " -fx-font-size: " + Palette.SIZE_LABEL + "px;");
        HBox r = new HBox(Palette.GAP_TIGHT, c);
        if (info != null) r.getChildren().add(infoButton(info, true));
        r.setAlignment(Pos.CENTER_LEFT);
        return r;
    }

    /** A door to a control the player holds: its icon, and where it goes. */
    static HBox controlDoor(String svg, String words, Runnable go) {
        HBox d = new HBox(6, icon(svg, Palette.ACCENT, 14), door(words, Palette.ACCENT, go));
        d.setAlignment(Pos.CENTER_LEFT);
        return d;
    }

    /** Policy, on one of its pages, at its top. */
    void openPolicy(String area, String page) {
        ui.policyScreen.policyArea = area;
        ui.policyScreen.policyPage = page;
        ui.policyScreen.dropProposal();
        ui.innerScrollAt.remove("showPolicyMenu:body");
        ui.policyScreen.showPolicyMenu();
    }

    /* =====================================================================
       OPERATIONS (0.7.30): INPUTS → THE PLANT → OUTPUTS

       The question: what goes in, what does the plant make of it, and what
       holds it back? SectorFlow's figures as a flow - each good bought with
       the city's part and the world's, the services by name; the plant as a
       ring of the rate it runs at and the six throttles that multiply to it,
       the thinnest in amber; each good made against what it could make, sold
       here, shipped, into stock or idled - and under it the sector's own
       lines as a grid (Sector.ownLines()), with the whole old page behind
       "details". The page said output was cut by the thinnest of five; it is
       all six multiplied (B3), and a business with nothing standing has no
       rate at all (B4).

       THE SECTOR WRITES ITS OWN PAGE, since the sector template (2026-09-11):
       Sector.operations(Game) returns the lines, as data, and the console
       printer reads them. Since 0.7.30 the screen draws the factory's block
       (plantLines()) as the flow, from SectorFlow's figures - SectorFlowCheck
       holds them to the same reads - its own lines (ownLines()) as the grid,
       and the whole list in the fold, where the screen and the console read
       the same lines and cannot disagree.
       ===================================================================== */

    /** The flow's money and units: which month each is (SectorFlow's two months). */
    static final String FLOW_INFO = "Each row's money is the month the books closed on - the Income page's - and its "
            + "units the month the plants just ran, the figures the operations page has always shown. In a city that "
            + "is not changing fast the two agree; a row's money is not its units times today's price. Right after a "
            + "city is loaded the units are not counted until a month runs: they are not saved.";

    void operationsPage(VBox page, Sector sector, boolean animate) {
        SectorFlow.Flow flow = SectorFlow.of(ui.game, sector);
        page.getChildren().add(flowCard(sector, flow, animate));
        List<Sector.Line> own = sector.ownLines(ui.game);
        if (!own.isEmpty()) {
            page.getChildren().add(sectionHead("ITS OWN FIGURES", "The lines this business writes about itself, as "
                    + "figures; each note is the (i) of the line above it.", null));
            page.getChildren().add(factGrid(own, 2, SectorScreen::toneColour));
        }
        page.getChildren().add(details(sector.key() + ":every", "every line, as the page listed them before 0.7.30",
                detailsOpen, ui::redraw, () -> everyLine(sector)));
    }

    /** The old page, whole: Sector.operations() drawn as statement lines. */
    VBox everyLine(Sector sector) {
        VBox column = new VBox(0);
        column.setMaxWidth(STATEMENT);
        for (Sector.Line line : sector.operations(ui.game)) {
            switch (line.kind()) {
                case HEAD -> column.getChildren().add(statementHead(line.label()));
                case NOTE -> column.getChildren().add(line.value().isEmpty()
                        ? statementNote(line.label())
                        : noteInLayers(line.label(), line.value()));
                default -> column.getChildren().add(statementLine(line.label(), line.value(),
                        toneColour(line.tone())));
            }
        }
        return column;
    }

    /** The flow: what goes in, a chevron, the plant, a chevron, what comes out. */
    VBox flowCard(Sector sector, SectorFlow.Flow flow, boolean animate) {
        VBox ins = inputsColumn(sector, flow);
        VBox plant = plantCard(sector, flow, animate);
        VBox outs = outputsColumn(flow);
        ins.setPrefWidth(380);
        ins.setMinWidth(240);
        HBox.setHgrow(ins, Priority.ALWAYS);
        outs.setPrefWidth(470);
        outs.setMinWidth(320);
        HBox.setHgrow(outs, Priority.ALWAYS);
        HBox row = new HBox(0, ins, chevron(), plant, chevron(), outs);
        row.setAlignment(Pos.TOP_LEFT);
        VBox c = card(head("WHAT GOES IN, WHAT THE PLANT MAKES OF IT, WHAT COMES OUT", FLOW_INFO,
                flow.counted() ? null : caption("units not counted yet: a month has to run after a load", Palette.TEXT_MUTED)), row);
        return c;
    }

    /** A chevron between the flow's columns. */
    static Label chevron() {
        Label c = new Label("›");
        c.setStyle(Palette.words(28, Palette.TEXT_SPENT));
        c.setMinWidth(22);
        c.setPrefWidth(22);
        c.setAlignment(Pos.CENTER);
        c.setMaxHeight(Double.MAX_VALUE);
        HBox.setMargin(c, new javafx.geometry.Insets(80, 0, 0, 0));
        return c;
    }

    /** The goods a column folds into one row past this many (the shops' and the kitchens' thirteen foods). */
    static final int FOLD_PAST = 6;

    /** WHAT GOES IN: each good bought - the city's part and the world's - and each service, by name. */
    VBox inputsColumn(Sector sector, SectorFlow.Flow flow) {
        VBox col = new VBox(6, head("IN", null, figure(m(flow.inputsMoney()), Palette.SIZE_BODY + 2, Palette.TEXT_HEAD)));
        List<SectorFlow.In> goods = new ArrayList<>(), others = new ArrayList<>();
        for (SectorFlow.In i : flow.inputs()) {
            if (i.service() || i.good() == Good.VANS) others.add(i); else goods.add(i);
        }
        goods.sort((a, b) -> Double.compare(b.money(), a.money()));
        if (goods.isEmpty() && others.isEmpty()) {
            col.getChildren().add(caption("buys nothing it makes anything from", Palette.TEXT_MUTED));
            return col;
        }
        if (goods.size() > FOLD_PAST) {
            double home = 0, money = 0;
            for (SectorFlow.In i : goods) { home += i.moneyHome(); money += i.money(); }
            String key = sector.key() + ":foods";
            boolean opened = detailsOpen.contains(key);
            boolean foods = sector == ui.game.getSectors().retail() || sector == ui.game.getSectors().restaurants();
            String name = goods.size() + (foods ? " foods" : " goods");
            SegmentBar split = segmentBar(List.of(Segment.of(home, Palette.BUSINESS), Segment.of(money - home, Palette.RAMP_REST)),
                    0, List.of(), 220, 8);
            Label toggle = new Label((opened ? "▾ " : "▸ ") + name);
            toggle.setStyle(Palette.strong(Palette.SIZE_BODY, Palette.TEXT_HEAD) + " -fx-cursor: hand;");
            toggle.setOnMouseClicked(e -> {
                if (!detailsOpen.remove(key)) detailsOpen.add(key);
                ui.redraw();
            });
            VBox fold = new VBox(3, rowHead(toggle, null, m(money)), split,
                    caption((money > 0 ? BuildScreen.pct(home / money) : "—") + " of the money to the city's suppliers",
                            Palette.TEXT_MUTED));
            col.getChildren().add(fold);
            if (opened) {
                VBox inside = new VBox(6);
                inside.setStyle("-fx-padding: 0 0 0 12;");
                for (SectorFlow.In i : goods) inside.getChildren().add(inputRow(i, flow.counted()));
                col.getChildren().add(inside);
            }
        } else {
            for (SectorFlow.In i : goods) col.getChildren().add(inputRow(i, flow.counted()));
        }
        for (SectorFlow.In i : others) col.getChildren().add(inputRow(i, flow.counted()));
        return col;
    }

    /** A row's head: its name (and its (i)), and its money at the right. */
    static HBox rowHead(Node name, String info, String money) {
        HBox h = new HBox(Palette.GAP_TIGHT, name);
        if (info != null) h.getChildren().add(infoButton(info, false));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        h.getChildren().addAll(gap, figure(money, Palette.SIZE_BODY, Palette.TEXT_HEAD));
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    /** One good bought: its name, the city's part and the world's on a bar, the units and the share, the money; or a service, by name. */
    VBox inputRow(SectorFlow.In i, boolean counted) {
        Formats f = Formats.INSTANCE;
        Label name = new Label(i.name());
        name.setWrapText(true);
        name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
        if (i.service()) {
            return new VBox(2, rowHead(name, null, m(i.money())), caption("a service: no units", Palette.TEXT_MUTED));
        }
        String info = null;
        if (Double.isFinite(i.priceHere())) {
            info = Double.isFinite(i.priceLanded())
                    ? String.format("Here %s a %s; landed %s. Both work; one keeps the margin here.",
                            f.amount(i.priceHere()), i.good().unit(), f.amount(i.priceLanded()))
                    : String.format("Here %s a %s; it cannot be landed.", f.amount(i.priceHere()), i.good().unit());
        }
        VBox row = new VBox(3, rowHead(name, info, m(i.money())));
        if (!counted) {
            row.getChildren().add(caption(inputWords(i), Palette.TEXT_SPENT));
            return row;
        }
        if (i.units() > 0) {
            row.getChildren().add(segmentBar(List.of(
                    new Segment(i.fromCity(), Palette.BUSINESS, false, null, null, "from the city  " + f.units(i.fromCity(), i.good()), null),
                    new Segment(i.imported(), Palette.RAMP_REST, false, null, null, "landed  " + f.units(i.imported(), i.good()), null)),
                    0, List.of(), 220, 8));
        }
        row.getChildren().add(caption(inputWords(i), Palette.TEXT_MUTED));
        return row;
    }

    /** A good bought, in words: "82,647 t · 7% from the city · on hand 3,478 vans". */
    static String inputWords(SectorFlow.In i) {
        Formats f = Formats.INSTANCE;
        if (i.service()) return "a service: no units";
        if (!Double.isFinite(i.fromCity())) {
            return "units not counted yet" + (Double.isFinite(i.onHand()) ? " · on hand " + f.units(i.onHand(), i.good()) : "");
        }
        String words = i.units() > 0
                ? units(i.units(), i.good()) + " · " + BuildScreen.pct(i.cityShare()) + " from the city"
                : i.wanted() > 0 ? units(i.wanted(), i.good()) + " wanted, none bought"
                // Bought, and no units on its production row: the builders' material, bought as the
                // sites draw it and never counted there (B12, for Jerus).
                : i.money() != 0 ? "bought as it was drawn: no units on its row this month"
                : "none bought";
        if (Double.isFinite(i.onHand())) words += " · on hand " + units(i.onHand(), i.good());
        return words;
    }

    /** A count of a good in its unit, "under 1 van" for a fraction that would read "0 vans". */
    static String units(double n, Good g) {
        if (n > 0 && n < .5) return "under 1 " + g.unit();
        return Formats.INSTANCE.units(n, g);
    }

    /**
     * THE PLANT: how many stand and are on site; a ring of the rate it runs
     * at, sweeping up on a new month; the six throttles as a cascade, each its
     * ratio on a bar and the running product after it, the thinnest amber;
     * and its posts filled. The landlords' ring is their homes let, with no
     * cascade; with nothing standing the ring is empty and says so.
     */
    VBox plantCard(Sector sector, SectorFlow.Flow flow, boolean animate) {
        SectorFlow.Plant p = flow.plant();
        Label count = new Label(p.none() ? "nothing standing"
                : String.format("%,d %s standing", p.standing(), Double.isFinite(p.let()) ? "buildings" : p.standing() == 1 ? "plant" : "plants"));
        count.setWrapText(true);
        count.setStyle(Palette.strong(Palette.SIZE_BODY, Palette.TEXT_HEAD));
        VBox counts = new VBox(1, count);
        if (p.onSite() > 0) counts.getChildren().add(caption(String.format("%,d on site", p.onSite()), Palette.BUILDING));
        String tag = builtTag(ui.game.getLastInvestment(sector.key()));
        HBox head = new HBox(8, iconSquare(Icons.ofSector(sector), Palette.BUSINESS, 26, 14), counts);
        if (tag != null) {
            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            head.getChildren().addAll(gap, tag(tag, Palette.BUILDING));
        }
        head.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(10, head);
        if (p.none()) {
            card.getChildren().addAll(centred(ring(0, Palette.TEXT_SPENT, 96, 10, "—", 20)),
                    centred(caption("no plant standing", Palette.TEXT_MUTED)));
        } else if (Double.isFinite(p.let())) {
            Region r = ring(p.let(), Palette.BUSINESS, 96, 10, BuildScreen.pct(p.let()), 20);
            card.getChildren().addAll(centred(animate ? sweep(r) : r), centred(caption("of the homes it owns are let", Palette.TEXT_LABEL)));
            if (sector instanceof ham.citybuildersim.sectors.RealEstate homes) {
                card.getChildren().add(centred(caption(String.format("%,.0f of %,d homes let", homes.getOccupiedHomes(), homes.getHomes()),
                        Palette.TEXT_MUTED)));
            }
        } else {
            Region r = ring(p.rate(), Palette.BUSINESS, 96, 10, BuildScreen.pct(p.rate()), 20);
            card.getChildren().addAll(centred(animate ? sweep(r) : r),
                    centred(caption("of what its plants could make", Palette.TEXT_LABEL)), cascade(p));
            if (p.posts() > 0) {
                card.getChildren().add(caption(String.format("posts %,.0f of %,d filled", p.workers(), p.posts()), Palette.TEXT_MUTED));
            }
        }
        card.setMinWidth(260);
        card.setPrefWidth(300);
        card.setMaxWidth(300);
        card.setStyle("-fx-padding: 12; -fx-background-color: " + Palette.PANEL + "; -fx-background-radius: 8;"
                + " -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return card;
    }

    /** A node centred in a row of its own. */
    static HBox centred(Node n) {
        HBox h = new HBox(n);
        h.setAlignment(Pos.CENTER);
        return h;
    }

    /** The throttle cascade: each of the six, its ratio on a bar, and the rate after it - the thinnest drawn amber. */
    static GridPane cascade(SectorFlow.Plant p) {
        GridPane g = new GridPane();
        g.setHgap(8);
        g.setVgap(3);
        int low = p.lowest();
        for (int i = 0; i < SectorFlow.THROTTLES.length; i++) {
            double r = p.ratios()[i];
            Label name = new Label(SectorFlow.THROTTLES[i]);
            name.setMinWidth(50);
            name.setStyle(Palette.words(Palette.SIZE_CAPTION, i == low && r < .995 ? Palette.WARN : Palette.TEXT_LABEL));
            Label ratio = figure(BuildScreen.pct(r), Palette.SIZE_CAPTION, Palette.TEXT_HEAD);
            ratio.setMinWidth(34);
            ratio.setAlignment(Pos.CENTER_RIGHT);
            Label after = figure("→ " + BuildScreen.pct(p.cascade()[i]), Palette.SIZE_CAPTION, Palette.TEXT_MUTED);
            g.add(name, 0, i);
            g.add(bar(r, i == low && r < .995 ? Palette.WARN : Palette.BUSINESS, 110, 6), 1, i);
            g.add(ratio, 2, i);
            g.add(after, 3, i);
        }
        Tooltip.install(g, new Tooltip("The six multiply in this order: each row's arrow is the rate after it, and the last is the rate the plant runs at."));
        return g;
    }

    /** WHAT COMES OUT: each good made against what it could make - sold here, shipped, into stock, idled - its price and cost; and work billed. */
    VBox outputsColumn(SectorFlow.Flow flow) {
        Formats f = Formats.INSTANCE;
        VBox col = new VBox(8, head("OUT", null, figure(m(flow.revenueMoney()), Palette.SIZE_BODY + 2, Palette.TEXT_HEAD)));
        if (Double.isFinite(flow.points())) {
            col.getChildren().add(caption(String.format("%s building points a month · busy %s",
                    f.count(flow.points()), BuildScreen.pct(flow.busy())), Palette.TEXT_LABEL));
        }
        String rail = railWords(flow.note(), flow.counted());
        if (rail != null) col.getChildren().add(caption(rail, Palette.TEXT_LABEL));
        if (flow.outputs().isEmpty()) {
            col.getChildren().add(caption("made and sold nothing this month", Palette.TEXT_MUTED));
            return col;
        }
        for (SectorFlow.Out o : flow.outputs()) {
            Label name = new Label(o.name());
            name.setWrapText(true);
            name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
            HBox top = rowHead(name, null, m(o.money()));
            if (Double.isFinite(o.price())) {
                Label price = chip(f.amount(o.price()) + (Double.isFinite(o.cost()) ? " · costs " + f.amount(o.cost()) : ""),
                        o.underwater() ? Palette.BAD : Palette.TEXT_LABEL);
                Tooltip.install(price, new Tooltip(o.underwater() ? "It costs more to make one than the city pays for it."
                        : "The city's price of one today, and what one costs to make."));
                top.getChildren().add(2, price);
            }
            VBox row = new VBox(3, top);
            if (o.work()) {
                row.getChildren().add(caption("billed: work, not a good", Palette.TEXT_MUTED));
                col.getChildren().add(row);
                continue;
            }
            if (!Double.isFinite(o.made())) {
                row.getChildren().add(caption(outputWords(o, flow.note()), Palette.TEXT_SPENT));
                col.getChildren().add(row);
                continue;
            }
            boolean seller = !o.good().traded();
            double scale = Math.max(Double.isFinite(o.capacity()) ? o.capacity() : 0,
                    Math.max(o.made() + o.idled(), o.soldHome() + o.exported() + o.intoStock()));
            if (scale > 0) {
                List<Segment> parts = new ArrayList<>();
                parts.add(new Segment(o.soldHome(), Palette.BUSINESS, false, null, null, "sold here  " + f.units(o.soldHome(), o.good()), null));
                parts.add(new Segment(o.exported(), Palette.BUSINESS_LIGHT, false, null, null, "shipped  " + f.units(o.exported(), o.good()), null));
                if (!seller) {
                    parts.add(new Segment(o.intoStock(), Palette.BUSINESS_DARK, false, null, null, "into stock  " + f.units(o.intoStock(), o.good()), null));
                    parts.add(new Segment(o.idled(), Palette.RAMP_REST, true, null, null, "idled  " + f.units(o.idled(), o.good()), null));
                }
                row.getChildren().add(segmentBar(parts, scale, List.of(), 300, 10));
            }
            row.getChildren().add(caption(outputWords(o, flow.note()), Palette.TEXT_MUTED));
            col.getChildren().add(row);
        }
        return col;
    }

    /** A good made, in words: "made 78,615 t of 203,400 · 32,018 here · 46,598 shipped"; a seller-priced one against the Build note's capacity. */
    static String outputWords(SectorFlow.Out o, BuildCard.Note note) {
        Formats f = Formats.INSTANCE;
        if (o.work()) return "billed: work, not a good";
        boolean seller = !o.good().traded();
        if (!Double.isFinite(o.made())) {
            if (o.good() == Good.HOUSING) return "rent on the homes let, a month";
            return "units not counted yet" + (Double.isFinite(o.capacity()) && o.capacity() > 0
                    ? (seller ? " ·" + coverWords(note) + " " + f.count(o.capacity()) : " · could make " + f.units(o.capacity(), o.good())) : "");
        }
        StringBuilder w = new StringBuilder();
        if (o.good() == Good.HOUSING) {
            // Housing is billed per head of capacity, not per home: its units are not doors.
            w.append("rent on the homes let, a month");
        } else if (seller) {
            w.append(o.good() == Good.GROCERIES ? f.count(o.soldHome()) + " baskets" : f.units(o.soldHome(), o.good())).append(" sold");
            if (Double.isFinite(o.capacity()) && o.capacity() > 0) w.append(" ·").append(coverWords(note)).append(' ').append(f.count(o.capacity()));
        } else {
            w.append("made ").append(f.units(o.made(), o.good()));
            if (o.capacity() > 0) w.append(" of ").append(f.count(o.capacity()));
            w.append(" · ").append(f.count(o.soldHome())).append(" here");
            if (o.exported() > 0) w.append(" · ").append(f.count(o.exported())).append(" shipped");
            if (o.fromStock() > 0) w.append(" · ").append(f.count(o.fromStock())).append(" out of stock");
            if (o.idled() > 0) w.append(" · ").append(f.count(o.idled())).append(" idled");
        }
        if (o.withheld() > 0) w.append(" · held back ").append(f.count(o.withheld()));
        if (o.spoiled() > 0) w.append(" · spoiled ").append(f.count(o.spoiled()));
        if (Double.isFinite(o.stock())) w.append(" · in the warehouse ").append(f.count(o.stock()));
        return w.toString();
    }

    /** What a seller-priced good's capacity is, in the Build note's words. */
    static String coverWords(BuildCard.Note note) {
        if (note == null) return "";
        switch (note.kind()) {
            case SHOPS:  return " the shops can serve";
            case LUXURY: return " the counters can serve";
            case MEALS:  return " the kitchens can seat";
            default:     return "";
        }
    }

    /** The railway's work against its track, off Build's note (RAIL {the city's trade in tonnes, the network's capacity}); null for anyone else. Before a month has run since a load the tonnes are "not counted yet" (B14: the trade is not saved). */
    static String railWords(BuildCard.Note note, boolean counted) {
        if (note == null || note.kind() != BuildCard.NoteKind.RAIL) return null;
        Formats f = Formats.INSTANCE;
        return (counted ? f.count(note.a()) + " t a month cross the boundary" : "the tonnes that cross: not counted yet")
                + " · track for " + f.count(note.b()) + " t a month";
    }

    /**
     * A sector's note in two layers (0.7.21; Sector.Line.note(shown, whole)):
     * the short line where statementNote() would put the paragraph, and the
     * paragraph behind its (i).
     */
    static HBox noteInLayers(String shown, String whole) {
        HBox row = infoLine(shown, whole, true, Palette.SIZE_CAPTION, Palette.TEXT_MUTED, STATEMENT - 16);
        row.setStyle("-fx-padding: 0 0 6 14;");
        return row;
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
