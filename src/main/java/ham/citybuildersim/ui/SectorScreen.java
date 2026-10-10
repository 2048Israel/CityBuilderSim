package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import ham.citybuildersim.sectors.LuxuryRetail;
import ham.citybuildersim.sectors.Restaurants;
import ham.citybuildersim.sectors.Retail;
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
                       List<Sector> losing, String losers, String losingInfo, double workers, long posts,
                       String range, String throttles, String door) { }

    static ListFigures listFigures(SectorBooks books, List<Sector> order) {
        double total = 0, before = 0, workers = 0;
        long posts = 0;
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
        running.getChildren().add(caption(runningWords(sector, plant), plant.none() ? Palette.TEXT_SPENT : Palette.TEXT_LABEL));
        if (!plant.none()) running.getChildren().add(bar(sector instanceof Retail shops ? shops.getHouseholdShare()
                : Double.isFinite(plant.let()) ? plant.let() : plant.rate(), Palette.BUSINESS, 120, 4));
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

    /** ...the grocers' in theirs (0.7.45): "handed over 58% of what was asked · roads 70%". */
    static String runningWords(Sector sector, SectorFlow.Plant plant) {
        if (!(sector instanceof Retail shops) || plant.none()) return runningWords(plant);
        int low = plant.lowest();
        // A save from before 0.7.43 carries the old sale's want, not the baskets asked for: nothing to say until a month runs.
        if (!shops.isSaleCounted()) return "the sale is not counted yet: a month has to run after a load";
        return "handed over " + BuildScreen.pct(shops.getHouseholdShare()) + " of what was asked"
                + (plant.ratios()[low] < .995 ? " · " + SectorFlow.THROTTLES[low] + " " + BuildScreen.pct(plant.ratios()[low]) : "");
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
            // Summary or Statement (0.7.74, D1): Operations has no statement.
            switch (sectorPage) {
                case "Income" -> {
                    if (statementView) incomeStatementView(page, sector, now, then);
                    else incomePage(page, sector, now, then, animate);
                }
                case "Balance sheet" -> {
                    if (statementView) balanceStatementView(page, sector, now, then);
                    else balancePage(page, sector, now, then);
                }
                case "Cash & debt" -> {
                    if (statementView) cashStatementView(page, sector, now, then);
                    else cashAndDebtPage(page, sector, now, then);
                }
                case "Investors" -> {
                    if (statementView) investorStatementView(page, sector, now, then, si);
                    else investorPage(page, sector, now, si, animate);
                }
                default -> operationsPage(page, sector, animate);
            }
        }

        // The pages, and at the strip's right end the switch (0.7.74, board SectorFrame).
        HBox strip = StatementView.stripWithSwitch(
                chipStrip(SECTOR_PAGES, PAGE_ICONS, sectorPage, Palette.SIZE_LABEL, this::open),
                SECTOR_HOME.equals(sectorPage) ? null : statementView, this::pickView);

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
        String[] run = sector instanceof Retail shops ? handedCell(shops, plant) : runningCell(plant);
        String runTone = plant.none() ? Palette.TEXT_MUTED
                : sector instanceof Retail shops ? handedTone(shops) : Palette.TEXT_HEAD;
        VBox running = kpi(run[0], run[1], run[2], runTone, null,
                "Operations: the plant, and what holds it back", () -> open("Operations"));
        return vitalsBar(kept, margin, cash, owes, running);
    }

    /**
     * The grocers' fifth figure (0.7.45; the UI spec's D8), {label, figure,
     * note}: the share of the baskets asked for at the price that the shops
     * handed over (Retail.getHouseholdShare()) - for groceries the outcome, as
     * LET is for the landlords; the operating rate is one cause of it, and the
     * throttle that cuts the shops most is named under it.
     */
    static String[] handedCell(Retail shops, SectorFlow.Plant plant) {
        if (plant.none()) return new String[] { "HANDED OVER", "—", "no shops standing" };
        if (!shops.isSaleCounted()) return new String[] { "HANDED OVER", "—", "not counted yet: a month has to run after a load" };
        int low = plant.lowest();
        return new String[] { "HANDED OVER", BuildScreen.pct(shops.getHouseholdShare()),
                "of the baskets asked for" + (plant.ratios()[low] < .995
                        ? " · " + SectorFlow.THROTTLES[low] + " cut most, " + BuildScreen.pct(plant.ratios()[low]) : "") };
    }

    /** ...its verdict: what went unhanded, on GOING SHORT's own lines (the UI spec's D10) - one hunger rule, not a second. */
    static String handedTone(Retail shops) {
        if (!shops.isSaleCounted() || SectorFlow.plant(shops).none()) return Palette.TEXT_HEAD;
        return PeopleScreen.goingShortTone(1 - shops.getHouseholdShare());
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
        long[] posts = sector.postsOfferedPerTier();
        if (pay == null) return box;

        java.util.List<Integer> order = new java.util.ArrayList<>();
        for (int i = 0; i < pay.length && i < JobType.values().length; i++) {
            if (Math.abs(pay[i]) >= .0000005) order.add(i);
        }
        order.sort((x, y) -> Double.compare(pay[y], pay[x]));
        for (int i : order) {
            JobType job = JobType.values()[i];
            long n = posts != null && i < posts.length ? posts[i] : 0;
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
        long standing = 0;
        for (long p : sector.postsPerTier()) standing += p;
        long offered = sector.getPostsOffered();
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
       INCOME (0.7.30; 0.7.74): A WATERFALL, IN SHORT, AND THE STATEMENT

       The question: where does a dollar of revenue go? The Summary: revenue,
       less the sales tax it remitted and what it bought, to its gross profit;
       less its wages, its running costs and its property tax, to its
       operating profit - the statement's own subtotals (D14); its interest
       to profit before tax, and its business tax to what it kept, each a bar,
       a refund stepping up. A click on a bar opens its note in the Statement
       view. Under it IN SHORT and the ratios, and OF EVERY DOLLAR IT TOOK in
       cents. The Statement (STATEMENT VIEWS, below): the statement of profit
       or loss, every note opening in place. Paying a tax is not a verdict
       (B10): the taxes and the interest are plain figures, and revenue is not
       green.
       ===================================================================== */

    /** The operating line's (i): what it is, and the model's own operating income, which is before the ground and the sales tax (D4, D5). */
    static final String OPERATING_INFO = "What the business made from trading, after its sales tax and the ground it "
            + "stands on, before the money it borrowed.";

    /** The sales tax refund's (i). */
    static final String REFUND_INFO = "The sales tax line is a REFUND this month: the credit on what this sector "
            + "bought came to more than the tax on what it sold. That is what zero-rating an export means, and the "
            + "city pays it.";

    /** The waterfall's (i). */
    static final String FALL_INFO = "Revenue at the left, less the sales tax it remitted and what it bought, to its "
            + "gross profit; less its wages, its running costs and the ground it stands on, to its operating profit - "
            + "the statement's own subtotals; then its interest to profit before tax, and the business tax to what it "
            + "kept. A refund steps up. A click on a bar opens its note in the Statement view.";

    void incomePage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then, boolean animate) {

        SectorStatements.Table t = incomeTable(sector, now, then);

        // The waterfall, its subtotals the statement's (D14).
        Waterfall fall = waterfall(incomeSteps(sector, t, now), SectorScreen::m, 0, 190);
        if (animate) fall.animate(500);
        page.getChildren().add(card(head("WHERE A DOLLAR OF REVENUE WENT, " + CityCalendar.format(now.month()).toUpperCase(),
                FALL_INFO, null), fall));

        // In short and the ratios; and every dollar it took, in cents.
        page.getChildren().add(StatementView.beside(incomeShort(sector, t), incomeRatios(sector, t, now)));
        page.getChildren().add(everyDollar(now));
    }

    /** The waterfall's steps: revenue, each cost down to what it kept, the statement's subtotals between them (D14); a bar with a note opens it in the Statement view. */
    List<Step> incomeSteps(Sector sector, SectorStatements.Table t, SectorBooks.SectorMonth now) {
        SectorStatements.Format f = sector.statementFormat();
        List<Step> steps = new ArrayList<>();
        steps.add(Step.of("Revenue", now.revenue(), Palette.REVENUE_RAMP[2]).go(n -> openNote(sector, INCOME, 1)));
        if (now.salesTaxPaid() != 0) steps.add(Step.of(now.salesTaxPaid() < 0 ? "Sales tax refund" : "Sales tax",
                -now.salesTaxPaid(), now.salesTaxPaid() < 0 ? Palette.REVENUE_RAMP[2] : Palette.SPENDING_RAMP[2])
                .go(n -> openNote(sector, INCOME, 2)));
        if (now.inputs() != 0) steps.add(Step.of("Bought in", -now.inputs(), Palette.SPENDING_RAMP[2])
                .tip(inputLabel(sector) + "\n" + m(now.inputs())).go(n -> openNote(sector, INCOME, 3)));
        if (f.gross != null) steps.add(fallTotal(StatementView.sentence(f.gross), t.now(SectorStatements.GROSS)));
        if (now.payroll() != 0) steps.add(Step.of("Wages", -now.payroll(), Palette.SPENDING_RAMP[2])
                .go(n -> openNote(sector, INCOME, 4)));
        double running = now.electricity() + now.water() + now.maintenance();
        if (running != 0) {
            steps.add(Step.of("Power, water, repairs", -running, Palette.SPENDING_RAMP[2]).parts(List.of(
                    new Slice("Electricity", now.electricity(), Palette.SPENDING_RAMP[1]),
                    new Slice("Water", now.water(), Palette.SPENDING_RAMP[2]),
                    new Slice("Repairs", now.maintenance(), Palette.SPENDING_RAMP[3]))));
        }
        if (now.propertyTax() != 0) steps.add(Step.of("Property tax", -now.propertyTax(), Palette.SPENDING_RAMP[2]));
        steps.add(fallTotal(StatementView.sentence(f.operating), t.now(SectorStatements.OPERATING)));
        if (now.interest() != 0) steps.add(Step.of("Interest", -now.interest(), Palette.SPENDING_RAMP[2])
                .go(n -> openNote(sector, INCOME, SectorStatements.FINANCE_NOTE)));
        // ...and what its borrowing cost it up front, a sixtieth a month (0.7.102, A16).
        if (now.borrowingCosts() != 0) steps.add(Step.of("Borrowing costs", -now.borrowingCosts(), Palette.SPENDING_RAMP[2])
                .tip("What its loans' fees, its mortgages' insurance and its bonds' issuing cost it, a month's share of "
                        + "each over its debt's life\n" + m(now.borrowingCosts())));
        steps.add(fallTotal("Profit before tax", now.preTaxIncome()));
        if (now.tax() != 0) steps.add(Step.of("Business tax", -now.tax(), Palette.SPENDING_RAMP[2]));
        steps.add(fallTotal("What it kept", now.netIncome()));
        return steps;
    }

    /** A waterfall's total: the business's violet, red under nothing. */
    static Step fallTotal(String name, double amount) {
        return Step.total(name, amount, amount < 0 ? Palette.BAD : Palette.BUSINESS);
    }

    /** IN SHORT on Income: revenue, the middle line, operating profit, profit before tax and the profit - the statement's own lines (D14). */
    static VBox incomeShort(Sector sector, SectorStatements.Table t) {
        SectorStatements.Format f = sector.statementFormat();
        String[] ids = { SectorStatements.REVENUE, f.gross != null ? SectorStatements.GROSS : SectorStatements.NET_REVENUE,
                SectorStatements.OPERATING, SectorStatements.PRE_TAX, SectorStatements.PROFIT };
        String[] labels = { f.revenue, f.gross != null ? StatementView.sentence(f.gross) : "Revenue after sales tax",
                StatementView.sentence(f.operating), "Profit before tax", "Profit for the month" };
        return StatementView.inShort(labels, nows(t, ids), thens(t, ids), t.thenKnown(),
                "Five lines of the statement of profit or loss, this month against last, in millions: the Statement "
                + "view has every line and its notes.");
    }

    /** A table's figures this month, by id. */
    static double[] nows(SectorStatements.Table t, String[] ids) {
        double[] v = new double[ids.length];
        for (int i = 0; i < ids.length; i++) v[i] = t.now(ids[i]);
        return v;
    }

    /** ...and last month. */
    static double[] thens(SectorStatements.Table t, String[] ids) {
        double[] v = new double[ids.length];
        for (int i = 0; i < ids.length; i++) v[i] = t.then(ids[i]);
        return v;
    }

    /** A share as a ratio chip writes it: a tenth of a per cent, a dash where it means nothing. */
    static String pctWords(double share) {
        return Double.isFinite(share) ? String.format("%.1f%%", unsigned0(share * 100, 1)) : "—";
    }

    /** Times over, a dash where it means nothing. */
    static String timesWords(double x) {
        return Double.isFinite(x) ? String.format("%.1fx", unsigned0(x, 1)) : "—";
    }

    /**
     * The Income page's ratios, both views: gross, operating and net margin,
     * interest cover on the statement's operating profit (D10, F6), the
     * effective tax rate, the wages' take and earnings a share - and its
     * format's own: a merchant's stock turns, a landlord's rent against its
     * mortgage payments, a builder's months of work booked, a carrier's
     * revenue a worker.
     */
    VBox incomeRatios(Sector sector, SectorStatements.Table t, SectorBooks.SectorMonth now) {
        SectorStatements.Format f = sector.statementFormat();
        double rev = now.revenue();
        List<Node> rows = new ArrayList<>();
        if (f.gross != null) {
            double g = SectorStatements.of(t.now(SectorStatements.GROSS), rev);
            rows.add(ratioRow("Gross margin", pctWords(g), g < 0 ? Palette.BAD : Palette.TEXT_LABEL,
                    "What it kept of every dollar after the sales tax and what it bought."));
        }
        double op = SectorStatements.of(t.now(SectorStatements.OPERATING), rev);
        rows.add(ratioRow("Operating margin", pctWords(op), op < 0 ? Palette.BAD : Palette.TEXT_LABEL,
                "What it kept of every dollar from trading, before the money it borrowed and its business tax."));
        rows.add(ratioRow("Net margin", marginWords(now.margin()),
                now.margin() < 0 ? Palette.BAD : now.margin() < .05 ? Palette.WARN : Palette.GOOD,
                "What it kept of every dollar it took, after tax. Under 5% is thin."));
        double cover = SectorStatements.interestCover(t);
        if (Double.isFinite(cover)) {
            rows.add(ratioRow("Interest cover", timesWords(cover),
                    cover < 1.5 ? Palette.BAD : cover < 3 ? Palette.WARN : Palette.GOOD,
                    "How many times over its operating profit - after its sales tax and the ground, D10 - covers its "
                    + "finance costs. Under one and it is borrowing to pay its lenders."));
        }
        double tax = SectorStatements.effectiveTax(t);
        if (Double.isFinite(tax)) rows.add(ratioRow("Tax rate it paid", pctWords(tax), Palette.TEXT_LABEL,
                "Its business tax over its profit before tax. A loss pays none, and earns no refund."));
        if (rev > 0) rows.add(ratioRow("Wages take", pctWords(now.payroll() / rev), Palette.TEXT_LABEL, null));
        SectorBooks.Shares share = ui.game.getSectorBooks().shares(sector.key());
        if (share != null && share.shares() > 0) {
            double eps = now.netIncome() / share.shares();
            rows.add(ratioRow("Earnings a share", tightMoney(toDollars(eps), false) + " a month",
                    eps < 0 ? Palette.BAD : Palette.TEXT_LABEL, "What it kept this month over its shares."));
        }
        // ...and its format's own (spec 4.6).
        switch (f) {
            case MERCHANTS -> {
                double turns = now.inventory() > 0 ? now.inputs() * 12 / now.inventory() : Double.NaN;
                if (Double.isFinite(turns)) rows.add(ratioRow("Stock turns a year", timesWords(turns), Palette.TEXT_LABEL,
                        String.format("A year of what it buys at this month's rate over the stock on its shelves: %.0f days"
                                + " of stock.", 360 / turns)));
            }
            case LANDLORDS -> {
                double payment = ui.game.getEconomyManager().getBusinessDebtManager().getMortgagePayment(sector.key());
                if (payment > 0) rows.add(ratioRow("Rent covers its mortgages", timesWords(t.now(SectorStatements.OPERATING) / payment),
                        t.now(SectorStatements.OPERATING) < payment ? Palette.BAD : Palette.TEXT_LABEL,
                        "Its net operating income over its mortgages' monthly payments today, principal and interest: "
                        + "the lender's debt service cover."));
            }
            case BUILDERS -> {
                if (sector instanceof ham.citybuildersim.sectors.Construction builders && rev > 0) {
                    rows.add(ratioRow("Months of work booked", String.format("%.1f", builders.getUnearnedRevenue() / rev),
                            Palette.TEXT_LABEL, "What its order book holds and has not yet earned, over this month's "
                            + "revenue: the work ahead of it, in months at this pace."));
                }
            }
            case CARRIERS -> {
                if (sector.getWorkers() > 0 && rev > 0) rows.add(ratioRow("Revenue a worker",
                        tightMoney(toDollars(rev / sector.getWorkers()), false) + " a month", Palette.TEXT_LABEL,
                        "This month's revenue over the workers it has today."));
            }
            default -> { }
        }
        return ratioColumn("READ AS RATIOS", rows.toArray(new Node[0]));
    }

    /**
     * OF EVERY DOLLAR IT TOOK (the Income summary): each cost as cents of a
     * dollar of its revenue, and what it kept, on one bar - a refund is not
     * a cost and is left out, so the cents can come to more than a dollar.
     */
    VBox everyDollar(SectorBooks.SectorMonth now) {
        HBox title = head("OF EVERY DOLLAR IT TOOK", "Each line of the month as cents of a dollar of its revenue, on "
                + "one bar: what the sales tax, its suppliers, its workers, its power, water and repairs, the ground, its "
                + "lenders - their interest, and a month's share of what its borrowing cost it up front - and the "
                + "business tax took, and what it kept. A month it lost money, the costs come to more than the dollar.", null);
        double rev = now.revenue();
        if (!(rev > 0)) return card(title, caption("It took nothing this month.", Palette.TEXT_MUTED));
        String[] names = { "sales tax", "bought in", "wages", "power, water, repairs", "property tax", "interest",
                "business tax", "kept" };
        double[] parts = { now.salesTaxPaid(), now.inputs(), now.payroll(), now.electricity() + now.water() + now.maintenance(),
                now.propertyTax(), now.interest() + now.borrowingCosts(), now.tax(), now.netIncome() };
        String[] colours = { Palette.SPENDING_RAMP[4], Palette.SPENDING_RAMP[3], Palette.SPENDING_RAMP[2],
                Palette.SPENDING_RAMP[1], Palette.SPENDING_RAMP[0], Palette.RAMP_REST, Palette.TEXT_SPENT, Palette.BUSINESS };
        List<Segment> bar = new ArrayList<>();
        List<HBox> keyParts = new ArrayList<>();
        double sum = 0;
        for (int i = 0; i < parts.length; i++) {
            if (!(parts[i] > 0)) continue;
            double cents = parts[i] / rev * 100;
            sum += parts[i];
            bar.add(new Segment(parts[i], colours[i], false, null, null, String.format("%s  %.1f¢", names[i], cents), null));
            keyParts.add(keyPart(colours[i], names[i], String.format("%.1f¢", cents)));
        }
        VBox c = card(title, segmentBar(bar, Math.max(sum, rev), List.of(), 0, 14), key(keyParts));
        if (now.netIncome() < 0) c.getChildren().add(caption(String.format("It lost %.1f¢ of every dollar it took.",
                -now.netIncome() / rev * 100), Palette.BAD));
        return c;
    }

    /** A summary's door into the Statement view with one note open: the waterfall's bars. */
    void openNote(Sector sector, String table, int note) {
        linesOpen(sector).add(StatementView.noteKey(table, note));
        pickView(true);
    }

    /* =====================================================================
       THE BALANCE SHEET (0.7.30; 0.7.74): TWO BARS, IN SHORT, ITS EQUITY'S
       MONTH AND ITS OWNERS

       What it owns against who has a claim on it, on one scale: its cash,
       stock, land, buildings, what it holds abroad and other businesses'
       bonds, against its loans and bonds and its owners' equity. The sheet
       had listed four of its six kinds of asset and totalled all six (B1) -
       Construction's $40B held abroad was in "Everything it owns" and on no
       line. Under the bars IN SHORT and the ratios, the month of its equity
       as a bridge, and a door to its owners, whose card - the one the Bank's
       Owners page draws - is on Investors since 0.7.75 (D7). The Statement
       view draws the classified sheet, its equity in share capital and what
       it kept (R3), and the statement of changes in equity in those columns.
       ===================================================================== */

    void balancePage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {

        // The sheet, on one scale.
        String[] ownsColours = { Palette.BUSINESS_LIGHT, Palette.BUSINESS, Palette.BUSINESS_DARK,
                Palette.RAMP_REST, Palette.ORE, Palette.TEXT_SPENT, Palette.MONEY_LIGHT };
        String[] ownsNames = { "cash", "stock", "land", "buildings", "held abroad", "other businesses' bonds",
                "owed by the shops" };
        double[] owns = { Math.max(0, now.cash()), now.inventory(), now.land(), now.buildings(),
                now.foreignAssets(), now.bondAssets(), now.tradeReceivables() };
        double equity = now.equity();
        double scale = Math.max(Math.max(0, now.totalAssets()), now.totalLiabilities() + Math.max(0, equity));
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
        if (now.tradePayables() > 0) {
            owedParts.add(new Segment(now.tradePayables(), Palette.MONEY_LIGHT, false, null, null, "owed to its suppliers  " + m(now.tradePayables()), null));
            owedKey.add(keyPart(Palette.MONEY_LIGHT, "owed to its suppliers", m(now.tradePayables())));
        }
        if (equity > 0) {
            owedParts.add(new Segment(equity, Palette.BUSINESS, false, null, null, "owners' equity  " + m(equity), null));
        }
        owedKey.add(keyPart(equity < 0 ? Palette.BAD : Palette.BUSINESS, "owners' equity", m(equity)));
        VBox sheet = card(head("WHAT IT OWNS, AND WHO HAS A CLAIM ON IT", "One scale for both bars: what it owns "
                        + "above, its lenders' claim and its owners' below. The two are equal - equity is whatever is "
                        + "left when the lenders are paid off - unless it owes more than it owns.", null),
                sheetRow("OWNS", ownParts, scale, m(now.totalAssets()), ownKey),
                sheetRow("OWES + EQUITY", owedParts, scale, m(now.totalLiabilities() + equity), owedKey));
        page.getChildren().add(sheet);
        if (equity < 0) {
            page.getChildren().add(alertLine(Icons.ALERT, Palette.BAD, "It is worth less than it owes", String.format(
                    "Everything this business owns comes to %s and it owes %s. It is insolvent on paper and still "
                    + "trading, which the model allows: the credit side stops lending long before the accountants "
                    + "would stop the trading.", m(now.totalAssets()), m(now.totalLiabilities())), null));
        }

        // In short and the ratios; its equity's month; its owners.
        SectorStatements.Table t = sheetTable(sector, now, then);
        String[] ids = { SectorStatements.TOTAL_ASSETS, SectorStatements.TOTAL_LIABILITIES, SectorStatements.TOTAL_EQUITY,
                SectorStatements.CASH };
        double[] n = nows(t, ids), p = thens(t, ids);
        page.getChildren().add(StatementView.beside(StatementView.inShort(
                new String[] { "What it owns", "What it owes", "Owners' equity", "Cash", "Loans and bonds" },
                new double[] { n[0], n[1], n[2], n[3], now.bondsPayable() },
                new double[] { p[0], p[1], p[2], p[3], then.bondsPayable() }, t.thenKnown(),
                "Five lines of the statement of financial position, as this month closed against last, in millions: "
                + "the Statement view has the sheet classified, and how its equity moved."),
                balanceRatios(sector, t, now, then)));
        Node bridge = equityBridge(now, then);
        if (bridge != null) page.getChildren().add(bridge);

        // Its owners' card is on Investors (0.7.75, D7): a door to it.
        if (ui.game.getEquity().getShares(Equity.indexOf(sector.key())) > 0) {
            page.getChildren().add(controlDoor(Icons.EXCHANGE, OWNERS_DOOR, () -> open("Investors")));
        }
    }

    /** The Balance sheet's door to its owners' card (D7). */
    static final String OWNERS_DOOR = "its owners, its share's price and what it pays: Investors";

    /**
     * The Balance sheet's ratios, both views: current and quick (what falls
     * due within a year needs R2, so they read a dash the month after a
     * load), debt to equity and to assets, the owners' share, return on
     * equity a year on its average, and book a share - and a merchant's days
     * of stock, a landlord's loan to value.
     */
    VBox balanceRatios(Sector sector, SectorStatements.Table t, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        double assets = now.totalAssets(), equity = now.equity();
        List<Node> rows = new ArrayList<>();
        double current = SectorStatements.currentRatio(t), quick = SectorStatements.quickRatio(t);
        String soon = "What falls due within a year is counted at the next month's books after a load.";
        rows.add(ratioRow("Current ratio", Double.isFinite(current) ? String.format("%.2f", current) : "—",
                Double.isFinite(current) && current < 1 ? Palette.WARN : Palette.TEXT_LABEL,
                "Its current assets - cash, stock, what its buyers owe it - over what falls due within a year. "
                + (Double.isFinite(current) ? "Under one, a year's bills outrun what it could turn to cash." : soon)));
        rows.add(ratioRow("Quick ratio", Double.isFinite(quick) ? String.format("%.2f", quick) : "—", Palette.TEXT_LABEL,
                "The same without its stock: what it could pay at once."));
        double debtToEquity = equity > 0 ? now.bondsPayable() / equity : Double.NaN;
        rows.add(ratioRow("Debt to equity", Double.isFinite(debtToEquity) ? String.format("%.2f", debtToEquity) : "—",
                equity <= 0 ? Palette.BAD : Palette.TEXT_LABEL, "Its loans and bonds over its owners' equity."));
        double debtToAssets = assets > 0 ? now.bondsPayable() / assets : 0;
        rows.add(ratioRow("Debt to assets", String.format("%.0f%%", debtToAssets * 100),
                debtToAssets > .7 ? Palette.BAD : debtToAssets > .5 ? Palette.WARN : Palette.GOOD, null));
        rows.add(ratioRow("Owners' share", assets > 0 ? pctWords(equity / assets) : "—", Palette.TEXT_LABEL,
                "Its owners' equity over everything it owns."));
        double roe = SectorStatements.returnOnEquity(now, then);
        rows.add(ratioRow("Return on equity", Double.isFinite(roe) ? pctWords(roe) + " a year" : "—",
                Double.isFinite(roe) && roe < 0 ? Palette.BAD : Palette.TEXT_LABEL,
                "Twelve months at this month's profit, over its owners' equity averaged across the two months."));
        SectorBooks.Shares share = ui.game.getSectorBooks().shares(sector.key());
        if (share != null && share.shares() > 0) {
            rows.add(ratioRow("Book a share", tightMoney(toDollars(equity / share.shares()), false),
                    equity < 0 ? Palette.BAD : Palette.TEXT_LABEL, "Its owners' equity over its shares."));
        }
        SectorStatements.Format f = sector.statementFormat();
        if (f == SectorStatements.Format.MERCHANTS && now.inputs() > 0 && now.inventory() > 0) {
            rows.add(ratioRow("Days of stock", String.format("%.0f", now.inventory() / (now.inputs() / 30)), Palette.TEXT_LABEL,
                    "The stock on its shelves, in days of what it bought this month."));
        }
        if (f == SectorStatements.Format.LANDLORDS && now.land() + now.buildings() > 0) {
            rows.add(ratioRow("Loan to value", pctWords(now.bondsPayable() / (now.land() + now.buildings())), Palette.TEXT_LABEL,
                    "What it owes over its land and buildings: the mortgage lender's measure."));
        }
        return ratioColumn("READ AS RATIOS", rows.toArray(new Node[0]));
    }

    /** ITS EQUITY THIS MONTH (the Balance summary): the statement of changes in equity as a bridge, or null without last month's sheet. */
    Node equityBridge(SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        SectorStatements.Table eq = SectorStatements.equity(now, then);
        if (eq == null) return null;
        List<Step> steps = new ArrayList<>();
        steps.add(fallTotal("At the start", eq.now(SectorStatements.EQ_START)));
        // ...its owners' three lines (the founders' shares move nothing in all), and the prices and the rest (R6).
        double[] moves = { eq.now(SectorStatements.EQ_PROFIT), eq.now(SectorStatements.EQ_OUTSIDE),
                eq.now(SectorStatements.EQ_ISSUED) + eq.now(SectorStatements.EQ_PAID) + eq.now(SectorStatements.EQ_BOUGHT),
                SectorStatements.revaluedTotal(eq) + eq.now(SectorStatements.EQ_REVALUED) };
        String[] names = { "Its profit", "Outside its trading", "Its owners", "Revalued, and the rest" };
        for (int i = 0; i < moves.length; i++) {
            if (Math.abs(toDollars(moves[i])) < .5) continue;
            steps.add(Step.of(names[i], moves[i], moves[i] >= 0 ? Palette.REVENUE_RAMP[2] : Palette.SPENDING_RAMP[2]));
        }
        steps.add(fallTotal("At the end", eq.now(SectorStatements.EQ_END)));
        Waterfall fall = waterfall(steps, SectorScreen::m, 0, 150);
        return card(head("ITS EQUITY THIS MONTH", "Its owners' equity at the start of the month and at its end, and what "
                + "moved it: its profit; what reached it outside its trading (the subsidy, the interest on its till and "
                + "its bonds, what its lenders forgave or wrote off, a theft); what its owners put in, were paid and had "
                + "bought back; and what the model revalued - the stock at today's price, the land and the buildings "
                + "at today's prices, what it holds abroad - with anything else no line names. The Statement view sets "
                + "it out as the statement of changes in equity, its share capital in a column of its own.", null), fall);
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
     * (0.7.30, D11), on a sector's Investors page since 0.7.75 (D7; its
     * Balance sheet until then, which keeps a door to it - `wide`: the
     * figures beside the share price's chart) and on the Bank's Owners page
     * (narrow, in its statement column, through ownersBlock()).
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
       CASH AND CREDIT (0.7.30; 0.7.74): A BRIDGE IN THREE STEPS, AND WHAT
       ITS BORROWING COSTS

       The question: why did its cash move, and can it carry what it owes?
       The month's cash as a bridge - the cash it started with and ended with
       written at the ends - in the cash flow statement's three steps (it was
       up to twenty, one a flow): from trading, investing and financing, each
       with its biggest items behind its tooltip, and NOT ACCOUNTED FOR when
       there is any. Then its free cash flow, what its owners took of it and
       the cash it has in days of trading; whether it can carry what it owes;
       its rate in parts, its leverage on a scale to the default point, what
       it owes by kind and when it falls due. Every credit line stays, in a
       grid. The Statement view draws the statement of cash flows, line by
       line, and its debt by when it falls due.
       ===================================================================== */

    void cashAndDebtPage(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {

        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        String key = sector.key();
        SectorStatements.Table cash = SectorStatements.cashFlow(now, then);
        SectorStatements.Table inc = incomeTable(sector, now, then);

        // The bridge: the three sections, a step each.
        List<Step> steps = cashSteps(cash);
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
        page.getChildren().add(card(head("WHERE THE CASH WENT, " + CityCalendar.format(now.month()).toUpperCase(),
                "The cash it started the month with and ended it with, at the ends; between them the statement of cash "
                + "flows' three sections - what its trading brought in, what it invested, what its lenders and owners "
                + "put in or took out - each a step, in blue or sand; hover one for its biggest items. The Statement "
                + "view has every line.", null), bridge));
        if (credit.isBorrowingBlocked(key)) {
            page.getChildren().add(alertLine(Icons.BANK, Palette.BAD,
                    "It cannot borrow for another " + credit.getBlockedMonths(key) + " months", String.format(
                    "This sector went under - it had nothing left, and its loans were written off whole - and no "
                    + "lender will write it a loan for another %d months. It can still build whatever its own cash "
                    + "covers, and nothing else — which is why a sector that goes under tends to stay small long "
                    + "after the month that broke it.", credit.getBlockedMonths(key)), null));
        }

        // Its free cash flow, what its owners took of it, and its cash in days.
        double free = cash.now(SectorStatements.FREE_CASH);
        double owners = now.dividendsPaid() + now.sharesBoughtBack();
        double days = SectorStatements.daysOfCash(now);
        javafx.scene.layout.FlowPane strip = new javafx.scene.layout.FlowPane(28, 8);
        strip.getChildren().addAll(
                miniFigure("FREE CASH FLOW", m(free), "from trading, less its premises", free < 0 ? Palette.BAD : Palette.TEXT_HEAD,
                        "What its trading brought in, less what it spent on its own premises and scrapped plant: the cash "
                        + "it could pay its owners or its lenders without borrowing."),
                miniFigure("PAID TO ITS OWNERS", m(owners), free > 0 && owners > 0
                        ? String.format("%.0f%% of its free cash flow", owners / free * 100) : "dividends and buybacks",
                        Palette.TEXT_HEAD, null),
                miniFigure("CASH ON HAND", Double.isFinite(days) ? String.format("%,.0f days", days) : "—",
                        "of what trading costs it", now.cash() < 0 ? Palette.BAD : Palette.TEXT_HEAD,
                        "Its cash over a day of the month's costs - what it bought, its wages, power, water, repairs, "
                        + "the ground, its interest and its taxes."));
        page.getChildren().add(card(strip));

        // Can it carry what it owes; and what its borrowing costs, and when it falls due.
        VBox pictures = creditPictures(sector, now);
        pictures.getChildren().addAll(whenDue(sector));
        page.getChildren().add(StatementView.beside(carryCard(sector, inc, cash, now), pictures));

        page.getChildren().add(sectionHead("WHAT IT BORROWS ON", null, hint("every line of its credit")));
        page.getChildren().add(factGrid(creditLines(sector, now), 2, SectorScreen::toneColour));
    }

    /** The bridge's steps: the cash flow statement's three sections, each with its three biggest items as its tooltip, and NOT ACCOUNTED FOR when there is any. */
    static List<Step> cashSteps(SectorStatements.Table cash) {
        String[][] sections = {
                { "From trading", SectorStatements.OPERATING_HEAD, SectorStatements.FROM_OPERATING },
                { "Investing", SectorStatements.INVESTING_HEAD, SectorStatements.FROM_INVESTING },
                { "Financing", SectorStatements.FINANCING_HEAD, SectorStatements.FROM_FINANCING } };
        List<Step> steps = new ArrayList<>();
        for (String[] s : sections) {
            double v = cash.now(s[2]);
            if (Math.abs(toDollars(v)) < .5) continue;
            steps.add(Step.of(s[0], v, v >= 0 ? Palette.REVENUE_RAMP[2] : Palette.SPENDING_RAMP[2])
                    .tip(biggest(cash, s[1], s[2])));
        }
        SectorStatements.Row gap = cash.row(SectorStatements.UNEXPLAINED);
        if (gap != null && Math.abs(gap.now()) >= SectorStatements.NOTICE) {
            steps.add(Step.of("Not accounted for", gap.now(), Palette.WARN));
        }
        return steps;
    }

    /** A section's three biggest lines this month, in words: "Profit for the month +$1.2M\nIts suppliers' credit, net −$310k". */
    static String biggest(SectorStatements.Table t, String head, String total) {
        List<SectorStatements.Row> lines = new ArrayList<>();
        boolean in = false;
        for (SectorStatements.Row r : t.rows()) {
            if (r.id().equals(head)) { in = true; continue; }
            if (r.id().equals(total)) break;
            if (in && r.kind() == SectorStatements.Kind.LINE && Math.abs(toDollars(r.now())) >= .5) lines.add(r);
        }
        lines.sort((a, b) -> Double.compare(Math.abs(b.now()), Math.abs(a.now())));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(3, lines.size()); i++) {
            if (sb.length() > 0) sb.append('\n');
            sb.append(lines.get(i).label()).append("  ").append(lines.get(i).now() >= 0 ? "+" : "−")
                    .append(m(Math.abs(lines.get(i).now())));
        }
        return sb.toString();
    }

    /**
     * CAN IT CARRY WHAT IT OWES (the Cash & debt summary): interest cover on
     * the statement's operating profit (D10), its debt over a year of what
     * its trading brings in, and whether it can borrow at all.
     */
    VBox carryCard(Sector sector, SectorStatements.Table inc, SectorStatements.Table cash, SectorBooks.SectorMonth now) {
        BusinessDebtManager credit = ui.game.getEconomyManager().getBusinessDebtManager();
        double cover = SectorStatements.interestCover(inc);
        double years = SectorStatements.debtYears(cash, now);
        boolean blocked = credit.isBorrowingBlocked(sector.key());
        return ratioColumn("CAN IT CARRY WHAT IT OWES",
                ratioRow("Interest cover", timesWords(cover), !Double.isFinite(cover) ? Palette.TEXT_LABEL
                                : cover < 1.5 ? Palette.BAD : cover < 3 ? Palette.WARN : Palette.GOOD,
                        "How many times over its operating profit covers its finance costs; a dash when it pays none."),
                ratioRow("Its debt, in years of trading cash", Double.isFinite(years) ? String.format("%.1f", years)
                                : now.bondsPayable() > 0 ? "never" : "—",
                        !Double.isFinite(years) && now.bondsPayable() > 0 ? Palette.WARN : Palette.TEXT_LABEL,
                        "What it owes over twelve months of what its trading brought in this month: how long it would take "
                        + "to repay at this pace, borrowing nothing more. \"Never\" when its trading brought nothing in."),
                ratioRow("Can it borrow", blocked ? "no — " + credit.getBlockedMonths(sector.key()) + " months" : "yes",
                        blocked ? Palette.BAD : Palette.GOOD,
                        blocked ? "It went under, and no lender will write it a loan until the ban runs out."
                                : "No ban: at its own rate, within the bank's limits."));
    }

    /** WHEN IT FALLS DUE (R2), under what it owes by kind: within a year, in one to five, after five - the city debt's ladder colours; or not counted yet. */
    List<Node> whenDue(Sector sector) {
        SectorBooks.Debt d = ui.game.getSectorBooks().debt(sector.key());
        List<Node> out = new ArrayList<>();
        String info = "What its loans, bonds and mortgages ask at the settles ahead, by when: a loan whole in its last "
                + "month, a mortgage's principal as its payments run it down, a bond at its maturity. A mortgage term "
                + "that ends is counted renewed, as its lender renews it while it lends.";
        if (d == null || d.owed() == null) {
            out.add(head("WHEN IT FALLS DUE", info, null));
            out.add(caption("Not counted yet: a month has to run after a load.", Palette.TEXT_MUTED));
            return out;
        }
        double owed = d.owedTotal(), year = d.withinYearTotal(), five = d.withinFiveTotal();
        if (!(owed > 0)) return out;
        double[] parts = { year, five - year, owed - five };
        String[] names = { "within a year", "in one to five", "after five years" };
        List<Segment> bar = new ArrayList<>();
        List<HBox> keyParts = new ArrayList<>();
        for (int i = 0; i < parts.length; i++) {
            if (!(parts[i] > 0)) continue;
            bar.add(new Segment(parts[i], Palette.LADDER[i], false, null, null, names[i] + "  " + m(parts[i]), null));
            keyParts.add(keyPart(Palette.LADDER[i], names[i], m(parts[i])));
        }
        out.add(head("WHEN IT FALLS DUE", info, figure(m(year), Palette.SIZE_BODY + 3, Palette.TEXT_HEAD)));
        out.add(segmentBar(bar, 0, List.of(), 0, 10));
        out.add(key(keyParts));
        return out;
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
        // ...and its suppliers (0.7.44's trade credit, here since 0.7.45), in the balance sheet's colour for them.
        String[] mixNames = { "bank loans", "bonds", "mortgages", "interim financing", "suppliers" };
        String[] mixColours = { Palette.LADDER[0], Palette.LADDER[1], Palette.LADDER[2], Palette.RAMP_REST, Palette.MONEY_LIGHT };
        double[] mixFigures = { loans, bonds, mortgages, interim, now.tradePayables() };
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
                        + Game.financingWords(plan).replaceFirst(" - ", ": ") + "." : "")
                + (now.tradePayables() > 0 ? "\n\nIts suppliers wait a month for the stock they let it have: what it owes"
                        + " them is beside its borrowing, at no interest, and paid out of the sale that stock goes to." : "");

        VBox c = ratioColumn("WHAT ITS BORROWING COSTS",
                head("NEW BORROWING COSTS", spreadInfo(key), rateFigure), rateBar, caption(rateWords, Palette.TEXT_MUTED),
                head("LEVERAGE", "What it owes over what it owns, to the default point at "
                        + String.format("%.2f", BusinessDebtManager.INSOLVENCY_TRIGGER) + " - past it the bank lends it "
                        + "nothing until it is under. The bar is its leverage now; its rate is priced off its last "
                        + "quarter's.", figure(String.format("%.2f", unsigned0(lev, 2)), Palette.SIZE_BODY + 3, levTone)),
                levBar,
                head("WHAT IT OWES, BY KIND", mixInfo, figure(m(now.tradePayables() > 0 ? now.totalLiabilities()
                        : credit.getPrincipal(key)), Palette.SIZE_BODY + 3, Palette.TEXT_HEAD)),
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
       STATEMENT VIEWS (0.7.74): A SUMMARY AND A STATEMENT

       Jerus, 2026-10-07: "both a summarized and a detailed actual statement
       ... only focusing on the income statement, balance sheet, cash and
       debt, and investor". One switch at the right of the page strip,
       Summary | Statement, on every page but Operations (D1: one choice for
       every business, and for the Bank tab's Profit and Balance sheet, kept
       while the game runs and never saved). The Statement view draws
       SectorStatements' tables through StatementView - each in its
       business's format (Sector.statementFormat()), every note opening in
       place, the ratios beside - and on Investors the investor report. The
       project's spec-sector-statements.md is the design; this is its batch 1.
       ===================================================================== */

    /** Whether the pages draw the formal statement rather than the summary (D1): kept while the game runs, never saved; the Bank tab's two statements read it too. */
    boolean statementView = false;

    /** The Statement view's change and common-size columns, each on or off from its toolbar, on every statement at once. */
    boolean showChange = true, showShare = true;

    /** Each statement's key in a business's open set: an open note on one is "income:note:5" in linesOpen(). */
    static final String INCOME = "income", SHEET = "sheet", EQUITY = "equity", CASH = "cash";

    /** Note 6, and the equity statement's outside line: what the outside lines are (F1). */
    static final String OUTSIDE_INFO = "Money that reached its books outside its trading: the city's subsidy and the "
            + "arrears it paid, the bank's interest on its till, the coupons on the bonds it holds, the interest its money "
            + "abroad earned, an overdraft forgiven, loans and bonds its lenders wrote off, and a theft. Each moved its "
            + "equity and reached no statement until this one, so none of it is taxed or in what its dividend is struck "
            + "on. What its borrowing cost it up front - the bank's fee, a mortgage's insurance premium, a bond's issuing "
            + "costs - was here until 0.7.102; it is a cost above the profit now, a month's share over each debt's life, "
            + "and deducted from its tax, and is here only for a month from an older save.";

    /** Share capital's line and note, when a save from before 0.7.75 was loaded (R3). */
    static final String DERIVED_INFO = "Derived when the city was loaded: its save was made before share capital was "
            + "kept, so this is what its shareholders have subscribed since its founding, at home and abroad, less what "
            + "its buybacks have paid since the load. Its founders' book and its buybacks before the load are not known; "
            + "they sit in what it kept.";

    /** Share capital's line and note (R3). */
    static final String CAPITAL_INFO = "What its owners put in: the book its founders' shares were issued against, and "
            + "every share it has sold since, at home and abroad - less all it paid to buy its own shares back, as the "
            + "bank keeps its paid-in.";

    /** Note 7: the stock. */
    static final String STOCK_WORDS = "Stock is held at what it would fetch today, so a price collapse shrinks this "
            + "business's balance sheet without anything being sold, and a rise lifts it.";

    /** Note 9: what it holds abroad. */
    static final String ABROAD_WORDS = "What it has sent abroad for the world's rate, in the city's money at the rate it "
            + "was last valued at, with the interest it earned there rolled in. It comes home before the business "
            + "borrows a cent.";

    /** What falls due within a year (R2), behind its line's and its card's (i). */
    static final String DUE_WORDS = "What its loans, bonds and mortgages ask at the twelve settles ahead: a loan whole in "
            + "its last month, a mortgage's principal as its payments run it down, a bond at its maturity. A mortgage "
            + "term that ends is counted renewed, as its lender renews it while it lends.";

    /** The month after a load, R2's line. */
    static final String NOT_SPLIT_WORDS = "When it falls due is counted at the next month's books: a load keeps the debt, "
            + "not its split.";

    /** The switch picked: this page in the other view, at its top. */
    void pickView(boolean statement) {
        statementView = statement;
        ui.innerScrollAt.remove("showSectorMenu:body");
        showSectorMenu();
    }

    /** One business's income statement this month, its finance note from R1. */
    SectorStatements.Table incomeTable(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        return SectorStatements.income(sector.statementFormat(), now, then, ui.game.getSectorBooks().debt(sector.key()));
    }

    /** ...and its sheet, its debt by when it falls due from R2. */
    SectorStatements.Table sheetTable(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        SectorBooks books = ui.game.getSectorBooks();
        return SectorStatements.sheet(now, then, books.debt(sector.key()), books.debtBefore(sector.key()));
    }

    /** A statement's columns as the toolbar left them; `shareWord` null for one with no common size. */
    StatementView.Columns columns(String shareWord) {
        return new StatementView.Columns(true, showChange, showShare, shareWord);
    }

    /** A statement's toolbar: its two columns, and its notes opened and closed together (none without notes). */
    HBox toolbar(SectorStatements.Table t, String table, StatementView.Columns c, java.util.Set<String> open,
                 java.util.function.IntFunction<Node> notes) {
        return StatementView.toolbar(c, () -> { showChange = !showChange; ui.redraw(); },
                c.shareWord() == null ? null : () -> { showShare = !showShare; ui.redraw(); },
                notes == null ? null : () -> { StatementView.openAll(t, table, open, notes); ui.redraw(); },
                notes == null ? null : () -> { StatementView.closeAll(table, open); ui.redraw(); });
    }

    /** "For the month of January 2200". */
    static String period(SectorBooks.SectorMonth now) { return "For the month of " + CityCalendar.format(now.month()); }

    /** "As January 2200 closed". */
    static String asAt(SectorBooks.SectorMonth now) { return "As " + CityCalendar.format(now.month()) + " closed"; }

    /* ------------------------------ Income ------------------------------ */

    /** The statement of profit or loss, its notes, and its ratios beside it. */
    void incomeStatementView(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        SectorStatements.Table t = incomeTable(sector, now, then);
        java.util.Set<String> open = linesOpen(sector);
        boolean millions = SectorStatements.inMillions(t);
        java.util.function.IntFunction<Node> notes = n -> switch (n) {
            case 1 -> revenueDetail(sector);
            case 2 -> salesTaxDetail(sector);
            case 3 -> inputsDetail(sector);
            case 4 -> wagesDetail(sector);
            case SectorStatements.FINANCE_NOTE -> t.note(n).isEmpty()
                    ? StatementView.noteSentence("By instrument it is counted at the next month's books: a load keeps the "
                            + "line, not its parts.")
                    : StatementView.noteRows(t.note(n), millions, "Each kind's interest as the month's bill was struck: the "
                            + "bank's loans and its interim financing at their rates, the bonds at their coupons, the "
                            + "mortgages at theirs.");
            case SectorStatements.OUTSIDE_NOTE -> StatementView.noteSentence(OUTSIDE_INFO);
            default -> null;
        };
        StatementView.Columns c = columns("of revenue");
        VBox table = StatementView.table(t, INCOME, "this month", "last month", c, now.revenue(), then.revenue(), open,
                notes, id -> incomeInfo(id, now), ui::redraw);
        VBox card = StatementView.card(StatementView.titleBlock(sector.label(), t.title() + " · "
                        + sector.statementFormat().word.toLowerCase(), period(now), millions),
                toolbar(t, INCOME, c, open, notes), table);
        page.getChildren().add(StatementView.beside(card, incomeRatios(sector, t, now)));
    }

    /** An income statement row's (i), by id. */
    static String incomeInfo(String id, SectorBooks.SectorMonth now) {
        return switch (id) {
            case SectorStatements.OPERATING -> OPERATING_INFO + " The model's own operating income, before the property "
                    + "tax and the sales tax, is " + m(now.operatingIncome()) + ".";
            case SectorStatements.SALES_TAX -> now.salesTaxPaid() < 0 ? REFUND_INFO : "The sales tax it charged on what "
                    + "it sold here, less the credit for what its suppliers had already remitted: under its revenue, "
                    + "because revenue is booked net of it.";
            case SectorStatements.PROPERTY_TAX -> "The tax on the ground it stands on and its buildings: a cost of "
                    + "operating, above its operating profit.";
            case SectorStatements.PRE_TAX -> "The model's own profit before tax, to the cent.";
            case SectorStatements.PROFIT -> "What it kept from trading: the business tax, the dividend and the rule that "
                    + "sells buildings after six losing months are all struck on this.";
            case SectorStatements.RESULT -> "Its profit and what reached it outside its trading: what its equity grew by "
                    + "before its owners and the revaluation.";
            default -> null;
        };
    }

    /* --------------------------- Balance sheet --------------------------- */

    /** The classified sheet with its ratios, then the statement of changes in equity. */
    void balanceStatementView(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        SectorStatements.Table t = sheetTable(sector, now, then);
        java.util.Set<String> open = linesOpen(sector);
        boolean millions = SectorStatements.inMillions(t);
        java.util.function.IntFunction<Node> notes = n -> switch (n) {
            case SectorStatements.STOCK_NOTE -> StatementView.noteSentence(STOCK_WORDS);
            case SectorStatements.BUILDINGS_NOTE -> StatementView.noteSentence(String.format("Its %,d buildings standing, "
                    + "at what they cost to put up: cash, and materials at the price of the day - so a move in the "
                    + "materials price moves the whole estate, which the equity statement counts as a revaluation. "
                    + "Nothing here depreciates.", sector.buildingsStanding()));
            case SectorStatements.ABROAD_NOTE -> StatementView.noteSentence(ABROAD_WORDS);
            case SectorStatements.CAPITAL_NOTE -> StatementView.noteRows(
                    SectorStatements.capitalNote(ui.game.getEquity(), sector.key()), millions,
                    now.paidInDerived() ? DERIVED_INFO : CAPITAL_INFO + " As it stands now.");
            default -> null;
        };
        StatementView.Columns c = columns("of assets");
        VBox table = StatementView.table(t, SHEET, "this month", "last month", c, now.totalAssets(), then.totalAssets(),
                open, notes, id -> sheetInfo(id, now), ui::redraw);
        VBox card = StatementView.card(StatementView.titleBlock(sector.label(), t.title(), asAt(now), millions),
                toolbar(t, SHEET, c, open, notes), table);
        page.getChildren().add(StatementView.beside(card, balanceRatios(sector, t, now, then)));

        SectorStatements.Table eq = SectorStatements.equity(now, then);
        if (eq == null) {
            page.getChildren().add(caption("How its equity moved needs last month's sheet: a month on, it is here.",
                    Palette.TEXT_MUTED));
            return;
        }
        VBox moved = StatementView.columnsTable(eq, EQUITY, EQUITY_HEADS, open,
                n -> n == SectorStatements.OUTSIDE_NOTE ? StatementView.noteSentence(OUTSIDE_INFO) : null,
                id -> equityInfo(id, now), ui::redraw);
        page.getChildren().add(StatementView.card(StatementView.titleBlock(sector.label(), eq.title()
                + (now.paidInDerived() ? " · share capital derived" : ""), period(now), SectorStatements.inMillions(eq)),
                null, moved));
    }

    /** The equity statement's columns (R3). */
    static final String[] EQUITY_HEADS = { "share capital", "kept, revalued", "total" };

    /** The equity statement's remainder's (i). */
    static final String REVALUED_INFO = "Whatever else moved its equity, worked out as what is left: a building bought "
            + "for more or less than the sheet carries it at, a bond bought off its face, stock bought at one price and "
            + "on the sheet at another.";

    /** An equity statement row's (i), by id: R6's four parts, the founders' shares and the rest. */
    static String equityInfo(String id, SectorBooks.SectorMonth now) {
        return switch (id) {
            case SectorStatements.EQ_START, SectorStatements.EQ_END -> now.paidInDerived() ? DERIVED_INFO : null;
            case SectorStatements.EQ_FOUNDED -> "Its first shares, issued to the city's households against the book it "
                    + "already had: what it had kept becomes share capital, and no cash moves.";
            case SectorStatements.EQ_BOUGHT -> "All of what it paid for its own shares, off its share capital - as the "
                    + "bank takes a buyback off its paid-in.";
            case SectorStatements.EQ_STOCK -> "The stock it began the month with, at this month's prices less last "
                    + "month's: stock is held at what it would fetch today.";
            case SectorStatements.EQ_LAND -> "The ground it held at last month's sheet, at this month's price a square "
                    + "foot less last month's.";
            case SectorStatements.EQ_BUILDINGS -> "The construction materials in the buildings it had at last month's "
                    + "sheet, at this month's price a unit less last month's: its buildings are carried at what they "
                    + "would cost today, so the materials price moves the whole estate.";
            case SectorStatements.EQ_ABROAD -> "What it holds abroad, less what it sent there and the interest rolled "
                    + "in: the rate it is valued at in the city's money.";
            case SectorStatements.EQ_REVALUED -> REVALUED_INFO;
            case SectorStatements.EQ_CAPITAL_REST -> "Its share capital moved by something none of the lines above "
                    + "names. SectorStatementCheck holds this at nothing, so a figure here is news.";
            default -> null;
        };
    }

    /** A sheet row's (i), by id. */
    static String sheetInfo(String id, SectorBooks.SectorMonth now) {
        return switch (id) {
            case SectorStatements.CASH -> now.cash() < 0 ? "A negative balance is not an error. A maturing loan takes "
                    + "cash under before the replacement is written, which is what a business with no spare cash "
                    + "actually does." : null;
            case SectorStatements.RECEIVABLES -> "What the shops owe it for stock it let them have on credit this month; "
                    + "they pay it at the next month's books, out of the sale it stocks.";
            case SectorStatements.SUPPLIERS -> "The month's stock its suppliers let it have on credit. It is paid at the "
                    + "next month's books, out of the takings of the sale that stock goes to.";
            case SectorStatements.DEBT_SOON -> DUE_WORDS;
            case SectorStatements.DEBT_WHOLE -> NOT_SPLIT_WORDS;
            case SectorStatements.SHARE_CAPITAL -> now.paidInDerived() ? DERIVED_INFO : CAPITAL_INFO;
            case SectorStatements.RETAINED -> "The rest of its equity: every month's result it kept after paying its "
                    + "shareholders, and what the model revalued - its stock, land and buildings at today's prices, what "
                    + "it holds abroad at today's rate.";
            default -> null;
        };
    }

    /* ---------------------------- Cash & debt ---------------------------- */

    /** The statement of cash flows, then what it owes by kind and by when it falls due. */
    void cashStatementView(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        SectorStatements.Table t = SectorStatements.cashFlow(now, then);
        java.util.Set<String> open = linesOpen(sector);
        boolean millions = SectorStatements.inMillions(t);
        StatementView.Columns c = columns(null);
        VBox table = StatementView.table(t, CASH, "this month", "last month", c, 0, 0, open, null,
                id -> cashInfo(id, now), ui::redraw);
        page.getChildren().add(StatementView.card(StatementView.titleBlock(sector.label(), t.title(), period(now), millions),
                toolbar(t, CASH, c, open, null), table));
        page.getChildren().add(scheduleCard(sector, millions));
        page.getChildren().add(debtCard(sector, millions));
    }

    /** The debt schedule's columns (R7): the kind (DEBT_KIND), five figures (DEBT_FIGURE), the rate and when the last of it falls due. */
    static final double SCHEDULE_RATE = 70, SCHEDULE_RUNS = 80;

    /** The schedule's (i). */
    static final String SCHEDULE_INFO = "What it owed of each kind at last month's sheet, what it borrowed, repaid and had "
            + "written off since, and what it owes at this month's; the rate it pays, weighted by what it owes, and the "
            + "month the last of it falls due. Counted from sheet to sheet, and the sheet reads what it owes as the month "
            + "closes - the loans for the buildings it ordered that month among it - so its borrowing is the calendar "
            + "month's, as the cash flow's is.";

    /**
     * ITS DEBT THIS MONTH, BY KIND (the Cash & debt statement; R7): the
     * roll-forward from last month's sheet to this month's - at the start,
     * borrowed, repaid, written off, at the end - with each kind's rate and
     * when the last of it falls due, its suppliers on a memo row, and what it
     * borrowed after the sheet was read - nothing since 0.7.102, when the
     * sheet is read at the month's close (A16), so its caption never shows
     * on a city played since; kept for the rule it states. Not counted until
     * two months have run after a load: one for each sheet.
     */
    VBox scheduleCard(Sector sector, boolean millions) {
        SectorBooks books = ui.game.getSectorBooks();
        SectorBooks.Debt now = books.debt(sector.key()), before = books.debtBefore(sector.key());
        SectorStatements.Schedule s = SectorStatements.schedule(now, before);
        VBox c = new VBox(4, head("ITS DEBT THIS MONTH, BY KIND", SCHEDULE_INFO, null));
        if (s == null) {
            c.getChildren().add(caption("Not counted yet: after a load, two months have to run - one for each sheet.",
                    Palette.TEXT_MUTED));
        } else {
            String head = Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL);
            c.getChildren().add(scheduleRow(new String[] { StatementView.units(millions), "at the start", "borrowed",
                    "repaid", "written off", "at the end", "rate", "runs to" }, head, head));
            double[] total = new double[5];
            int n = SectorBooks.Debt.KINDS.length;
            for (int k = 0; k < n; k++) {
                double[] v = { s.start()[k], s.borrowed()[k], -s.repaid()[k], -s.writtenOff()[k], s.end()[k] };
                boolean any = false;
                for (double x : v) any |= Math.abs(x) >= SectorStatements.NOTICE;
                if (!any) continue;
                for (int i = 0; i < 5; i++) total[i] += v[i];
                c.getChildren().add(scheduleRow(SectorBooks.Debt.KINDS[k], v, s.rate()[k], s.runsTo()[k], millions, false));
            }
            SectorBooks.SectorMonth m = books.get(sector), p = books.previous(sector);
            if (m.tradePayables() != 0 || p.tradePayables() != 0) {
                c.getChildren().add(scheduleRow("Its suppliers", new double[] { p.tradePayables(), Double.NaN, Double.NaN,
                        Double.NaN, m.tradePayables() }, Double.NaN, 0, millions, false));
            }
            c.getChildren().add(scheduleRow("Loans and bonds", total, Double.NaN, 0, millions, true));
            double rest = 0;
            for (double x : s.rest()) rest += Math.abs(x);
            if (rest >= SectorStatements.NOTICE) {
                c.getChildren().add(caption("NOT ACCOUNTED FOR: " + m(SectorStatements.Schedule.total(s.rest()))
                        + " moved what it owes and no line names it. SectorStatementCheck holds this at nothing, so this "
                        + "is news.", Palette.BAD));
            }
            double after = SectorStatements.Schedule.total(s.after());
            if (after >= SectorStatements.NOTICE) {
                c.getChildren().add(caption("It borrowed " + m(after) + " more this month after its sheet was read, for the "
                        + "buildings it ordered: next month's here, as on the sheet.", Palette.TEXT_MUTED));
            }
        }
        c.setMaxWidth(DEBT_KIND + 5 * DEBT_FIGURE + SCHEDULE_RATE + SCHEDULE_RUNS + 7 * Palette.GAP + 32);
        c.setStyle("-fx-padding: 14 16 14 16; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return c;
    }

    /** One kind's row of the schedule: its five figures, its rate and when the last of it falls due. */
    private static HBox scheduleRow(String kind, double[] v, double rate, double runsTo, boolean millions, boolean total) {
        String[] words = new String[8];
        words[0] = kind;
        for (int i = 0; i < 5; i++) words[i + 1] = Double.isNaN(v[i]) ? "" : StatementView.figure(v[i], millions);
        words[6] = Double.isFinite(rate) && rate > 0 ? String.format("%.2f%%", rate * 100) : "";
        words[7] = runsTo > 0 ? CityCalendar.formatShort((int) runsTo) : "";
        return scheduleRow(words, Palette.words(Palette.SIZE_BODY, total ? Palette.TEXT_HEAD : Palette.TEXT_LABEL),
                total ? Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD) : Palette.figureRegular(Palette.SIZE_BODY, Palette.TEXT_BODY));
    }

    private static HBox scheduleRow(String[] words, String labelStyle, String figureStyle) {
        HBox row = new HBox(Palette.GAP);
        double[] widths = { DEBT_KIND, DEBT_FIGURE, DEBT_FIGURE, DEBT_FIGURE, DEBT_FIGURE, DEBT_FIGURE, SCHEDULE_RATE,
                SCHEDULE_RUNS };
        for (int i = 0; i < words.length; i++) {
            Label f = new Label(words[i]);
            f.setMinWidth(widths[i]);
            f.setPrefWidth(widths[i]);
            f.setAlignment(i == 0 ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
            f.setStyle(i == 0 ? labelStyle : figureStyle);
            row.getChildren().add(f);
        }
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** A cash flow row's (i), by id. */
    static String cashInfo(String id, SectorBooks.SectorMonth now) {
        return switch (id) {
            case SectorStatements.PAID_EARLIER -> "Stock it built or sold with this month and paid for when it bought it: "
                    + "a cost in its profit, and no cash this month.";
            case SectorStatements.TRADE_CREDIT -> "The statement charged the whole stock bill and booked the whole sale; "
                    + "the cash moved by less, because a supplier waits a month for what it let a buyer have on credit.";
            case SectorStatements.CASH_DEPOSIT_INTEREST, SectorStatements.CASH_COUPONS -> "Interest received, among its "
                    + "operating flows as IAS 7 allows: it reached its till at the bottom of the month, after its "
                    + "statement was struck.";
            case SectorStatements.UNEXPLAINED -> "The lines above are every way this model moves a sector's cash. A "
                    + "residual means something else touched it - worth knowing about rather than worth hiding. "
                    + "SectorBooksCheck audits this identity on every sector every month, so a line appearing here is news.";
            case SectorStatements.CASH_END -> now.cash() < 0 ? "A negative balance is not an error. A maturing loan takes "
                    + "cash under before the replacement is written." : null;
            case SectorStatements.FREE_CASH -> "What its trading brought in, less what it spent on its own premises and "
                    + "scrapped plant: what it could pay its owners or its lenders without borrowing.";
            default -> null;
        };
    }

    /** The debt card's columns: the kind, then owed, within a year, in one to five, after five, and the month's interest. */
    static final double DEBT_KIND = 130, DEBT_FIGURE = 100;

    /**
     * ITS DEBT, BY KIND AND BY WHEN IT FALLS DUE (the Cash & debt
     * statement): each kind it owes on (R2) with when it falls due and the
     * month's interest on it (R1), its suppliers on a line at no interest,
     * and the city debt's ladder under it. The month after a load: not
     * counted yet.
     */
    VBox debtCard(Sector sector, boolean millions) {
        SectorBooks.Debt d = ui.game.getSectorBooks().debt(sector.key());
        SectorBooks.SectorMonth now = ui.game.getSectorBooks().get(sector);
        VBox c = new VBox(4, head("ITS DEBT, BY KIND AND BY WHEN IT FALLS DUE", DUE_WORDS, null));
        if (d == null || d.owed() == null) {
            c.getChildren().add(caption("Not counted yet: a month has to run after a load. It owes "
                    + m(now.bondsPayable()) + " in all.", Palette.TEXT_MUTED));
        } else {
            String head = Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL);
            c.getChildren().add(debtRow(new String[] { StatementView.units(millions), "owed", "within a year",
                    "in one to five", "after five", "interest" }, head, head));
            double[] total = new double[5];
            for (int k = 0; k < SectorBooks.Debt.KINDS.length; k++) {
                double[] v = { d.owed()[k], d.withinYear()[k], d.withinFive()[k] - d.withinYear()[k],
                        d.owed()[k] - d.withinFive()[k], d.interest() == null ? Double.NaN : d.interest()[k] };
                if (v[0] == 0 && !(v[4] > 0)) continue;
                for (int i = 0; i < 5; i++) total[i] += v[i];
                c.getChildren().add(debtRow(SectorBooks.Debt.KINDS[k], v, millions, false));
            }
            if (now.tradePayables() != 0) {
                c.getChildren().add(debtRow("Its suppliers", new double[] { now.tradePayables(), now.tradePayables(), 0, 0, 0 },
                        millions, false));
                total[0] += now.tradePayables();
                total[1] += now.tradePayables();
            }
            c.getChildren().add(debtRow("All it owes", total, millions, true));
            c.getChildren().addAll(whenDue(sector));
        }
        c.setMaxWidth(StatementView.TABLE + 32);
        c.setStyle("-fx-padding: 14 16 14 16; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return c;
    }

    private static HBox debtRow(String kind, double[] v, boolean millions, boolean total) {
        String[] words = new String[6];
        words[0] = kind;
        for (int i = 0; i < 5; i++) words[i + 1] = StatementView.figure(v[i], millions);
        return debtRow(words, Palette.words(Palette.SIZE_BODY, total ? Palette.TEXT_HEAD : Palette.TEXT_LABEL),
                total ? Palette.figure(Palette.SIZE_BODY, Palette.TEXT_HEAD) : Palette.figureRegular(Palette.SIZE_BODY, Palette.TEXT_BODY));
    }

    private static HBox debtRow(String[] words, String labelStyle, String figureStyle) {
        HBox row = new HBox(Palette.GAP);
        Label k = new Label(words[0]);
        k.setMinWidth(DEBT_KIND);
        k.setPrefWidth(DEBT_KIND);
        k.setStyle(labelStyle);
        row.getChildren().add(k);
        for (int i = 1; i < words.length; i++) {
            Label f = new Label(words[i]);
            f.setMinWidth(DEBT_FIGURE);
            f.setPrefWidth(DEBT_FIGURE);
            f.setAlignment(Pos.CENTER_RIGHT);
            f.setStyle(figureStyle);
            row.getChildren().add(f);
        }
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /* ------------------------------ Investors ------------------------------ */

    /**
     * The investor report (spec 4.4): a share this month against last, who
     * holds it and what it has raised and paid, how it is financed against
     * the equity share it aims for, and since 0.7.75 every building at every
     * gate (R4, D8) - where the summary's list stops at the first.
     */
    void investorStatementView(VBox page, Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then,
                               BuildCard.SectorInvestors si) {
        page.getChildren().add(shareReport(sector, now, then));
        int company = Equity.indexOf(sector.key());
        VBox holds = whoHoldsIt(company);
        page.getChildren().add(StatementView.beside(holds, howFinanced(company, now)));
        page.getChildren().add(sectionHead("EVERY BUILDING AT EVERY GATE", EVERY_GATE_INFO, hint("a row opens it on Build")));
        page.getChildren().add(everyGate(sector));
    }

    /** EVERY BUILDING AT EVERY GATE's (i). */
    static final String EVERY_GATE_INFO = "Each building it can put up at every gate investors ask - ore, a licence, "
            + "staff, land, and whether it pays - ticked, crossed, or a dash where it is not one this building has; "
            + "what one would make its owner a month on the investors' estimate, what it would cost to build with its "
            + "ground, the months that would take to pay back, and how it would pay: what its register would ask its "
            + "owners for, what its till and its money abroad would put in, and what it would borrow. Under each, what "
            + "that means. The summary stops at the first gate, which can hide a second.";

    /** The grid's columns: the building, a gate's mark, a figure, the payback, how it would pay. */
    static final double GATE_NAME = 200, GATE_MARK = 44, GATE_FIGURE = 96, GATE_PAYBACK = 84, GATE_PAYS = 210;

    /** The gates' heads, in BuildCard.GateKind's order. */
    static final String[] GATE_HEADS = { "ore", "licence", "staff", "land", "pays" };

    /** Every building it owns at every gate (R4): one row each, and what it means under it. */
    VBox everyGate(Sector sector) {
        List<BuildCard.Appraisal> all = BuildCard.appraiseAll(ui.game, sector);
        if (all.isEmpty()) return new VBox(caption("No building of its own on the Build menu.", Palette.TEXT_MUTED));
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(0);
        double[] widths = { GATE_NAME, GATE_MARK, GATE_MARK, GATE_MARK, GATE_MARK, GATE_MARK, GATE_FIGURE, GATE_FIGURE,
                GATE_PAYBACK, GATE_PAYS };
        for (double w : widths) {
            javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints();
            cc.setPrefWidth(w);
            cc.setMinWidth(w);
            grid.getColumnConstraints().add(cc);
        }
        String[] heads = { "building", GATE_HEADS[0], GATE_HEADS[1], GATE_HEADS[2], GATE_HEADS[3], GATE_HEADS[4],
                "a month", "to build", "pays back", "how it would pay" };
        for (int i = 0; i < heads.length; i++) {
            Label h = new Label(heads[i]);
            h.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
            grid.add(h, i, 0);
            GridPane.setHalignment(h, i == 0 || i == heads.length - 1 ? javafx.geometry.HPos.LEFT
                    : i <= 5 ? javafx.geometry.HPos.CENTER : javafx.geometry.HPos.RIGHT);
        }
        int row = 1;
        for (BuildCard.Appraisal a : all) {
            final BuildingsTemplate template = a.template();
            BuildAdvice.Category cat = BuildAdvice.categoryOf(template.getCategory());
            Label name = new Label(template.getName());
            name.setWrapText(true);
            name.setStyle(Palette.strong(Palette.SIZE_BODY, Palette.TEXT_HEAD));
            HBox who = new HBox(8, icon(cat == null ? Icons.SECTOR : Icons.ofCategory(cat.name()), Palette.BUSINESS, 16), name);
            who.setAlignment(Pos.CENTER_LEFT);
            List<Node> cells = new ArrayList<>();
            cells.add(who);
            for (BuildCard.GateMark g : a.gates()) cells.add(gateMark(g));
            cells.add(figure(m(a.estimate()), Palette.SIZE_BODY, a.estimate() <= 0 ? Palette.BAD : Palette.TEXT_HEAD));
            cells.add(figure(m(a.cost()), Palette.SIZE_BODY, Palette.TEXT_HEAD));
            cells.add(figure(paybackWords(a.payback()), Palette.SIZE_BODY, Double.isFinite(a.payback()) ? Palette.TEXT_HEAD
                    : Palette.TEXT_SPENT));
            cells.add(caption(paysWords(a), Palette.TEXT_BODY));
            for (int i = 0; i < cells.size(); i++) {
                javafx.scene.layout.StackPane cell = new javafx.scene.layout.StackPane(cells.get(i));
                javafx.scene.layout.StackPane.setAlignment(cells.get(i), i == 0 || i == cells.size() - 1 ? Pos.CENTER_LEFT
                        : i <= 5 ? Pos.CENTER : Pos.CENTER_RIGHT);
                cell.setMinHeight(34);
                cell.setStyle("-fx-padding: 4 0 0 0; -fx-border-color: " + Palette.HAIRLINE + "; -fx-border-width: 1 0 0 0;"
                        + " -fx-cursor: hand;");
                cell.setOnMouseClicked(e -> {
                    BuildAdvice.Category c = BuildAdvice.categoryOf(template.getCategory());
                    if (c != null) ui.buildScreen.openCategory(c.name());
                });
                grid.add(cell, i, row);
            }
            Label so = caption(soWords(a), a.failures().isEmpty() ? Palette.TEXT_MUTED : Palette.TEXT_LABEL);
            so.setWrapText(true);
            so.setStyle(so.getStyle() + " -fx-padding: 0 0 6 24;");
            grid.add(so, 0, row + 1, widths.length, 1);
            row += 2;
        }
        VBox box = new VBox(grid);
        box.setStyle("-fx-padding: 8 14 8 14; -fx-background-color: " + Palette.RAISED + "; -fx-background-radius: 8;");
        return box;
    }

    /** One gate's mark: a tick, a cross with why behind a tooltip, or a dash where it is not one this building has. */
    Node gateMark(BuildCard.GateMark g) {
        if (g.mark() == BuildCard.Mark.NONE) return caption("–", Palette.TEXT_SPENT);
        if (g.mark() == BuildCard.Mark.PASS) return icon(Icons.TICK, Palette.GOOD, 13);
        Node cross = icon(Icons.CLOSE, Palette.WARN, 13);
        javafx.scene.layout.StackPane box = new javafx.scene.layout.StackPane(cross);
        javafx.scene.control.Tooltip.install(box, new javafx.scene.control.Tooltip(ui.buildScreen.gateWords(g.failure())));
        return box;
    }

    /** The months its estimate would take to earn what one costs, in months under two years and years past them; "never" when it would not pay. */
    static String paybackWords(double months) {
        if (!Double.isFinite(months) || months < 0) return "never";
        if (months < 24) return String.format("%.0f months", Math.max(1, months));
        double years = months / 12;
        return years >= 100 ? "a century+" : String.format(years < 10 ? "%.1f years" : "%.0f years", years);
    }

    /** How one would be paid for, as shares of its cost: "40% shares · 12% its own · 48% borrowed", the parts that are not nothing. */
    static String paysWords(BuildCard.Appraisal a) {
        if (!(a.cost() > 0)) return "—";
        List<String> parts = new ArrayList<>();
        double[] v = { a.owners(), a.own(), a.borrowed() };
        String[] w = { "shares", "its own", "borrowed" };
        for (int i = 0; i < v.length; i++) {
            double share = v[i] / a.cost();
            if (share >= .005) parts.add(String.format("%.0f%% %s", share * 100, w[i]));
        }
        return parts.isEmpty() ? "—" : String.join(" · ", parts);
    }

    /** A failed gate in two or three words, for the "so" line's second refusal. */
    static String gateShort(BuildCard.GateKind k) {
        return switch (k) {
            case DEPOSIT -> "no deposit";
            case LICENCE -> "too few licensed";
            case STAFFING -> "too few to staff it";
            case LAND -> "no ground";
            default -> "it would not pay";
        };
    }

    /**
     * What a building's gates mean (R4's "so"): it passes every one; ground is
     * all that stops it, which the player can buy; it would not pay, which no
     * gate opening would change; or its first refusal, and the others behind
     * it - ground the player bought would not build one that would also lose
     * money (F11).
     */
    String soWords(BuildCard.Appraisal a) {
        List<BuildCard.GateMark> fails = a.failures();
        if (fails.isEmpty()) return "So: it passes every gate. It builds this when its demand asks for more.";
        BuildCard.GateMark first = fails.get(0);
        String words = ui.buildScreen.gateWords(first.failure());
        if (fails.size() == 1) {
            return switch (first.kind()) {
                case LAND -> "So: ground is all that stops it - " + words + ". The Land office sells it.";
                case LOSS -> "So: it " + words + " at today's prices, so it would not build however the other gates stood.";
                default -> "So: " + words + ".";
            };
        }
        List<String> rest = new ArrayList<>();
        for (int i = 1; i < fails.size(); i++) rest.add(gateShort(fails.get(i).kind()));
        boolean landToo = a.at(BuildCard.GateKind.LAND) == BuildCard.Mark.FAIL;
        boolean loses = a.at(BuildCard.GateKind.LOSS) == BuildCard.Mark.FAIL;
        return "So: " + words + "; past that, " + String.join(", ", rest)
                + (landToo && loses ? " - ground alone would not build it" : "") + ".";
    }

    /** A share's figures, this month or last, as the report's rows: {shares, EPS, DPS, payout, book, price, fair, P/E, P/B, yield, market value}; NaN where it means nothing. */
    static double[] shareFigures(SectorBooks.Shares s, SectorBooks.SectorMonth m) {
        if (s == null || m.isEmpty() || !(s.shares() > 0)) return null;
        double eps = m.netIncome() / s.shares(), dps = s.dividend() / s.shares(), book = m.equity() / s.shares();
        return new double[] { s.shares(), eps, dps, m.netIncome() > 0 ? m.dividendsPaid() / m.netIncome() : Double.NaN,
                book, s.price(), s.fair(), eps > 0 ? s.price() / (eps * 12) : Double.NaN,
                book > 0 ? s.price() / book : Double.NaN, s.price() > 0 ? s.dividendYear() / s.price() : Double.NaN,
                s.outstanding() * s.price() };
    }

    /** The report's rows: {label, kind} - "n" a count, "$" money a share, "%" a share, "x" times, "M" money. */
    static final String[][] SHARE_ROWS = { { "Shares", "n" }, { "Earnings a share, the month", "$" },
            { "Dividend a share, the month", "$" }, { "Payout, of the month's profit", "%" }, { "Book a share", "$" },
            { "Last trade, or fair value", "$" }, { "Fair value, the register's", "$" }, { "Price to earnings, a year", "x" },
            { "Price to book", "x" }, { "Yield, on a year's dividend", "%" }, { "Market value", "M" } };

    /** A SHARE: the report's first block, this month against last; not counted the month after a load. */
    VBox shareReport(Sector sector, SectorBooks.SectorMonth now, SectorBooks.SectorMonth then) {
        SectorBooks books = ui.game.getSectorBooks();
        double[] a = shareFigures(books.shares(sector.key()), now), b = shareFigures(books.sharesBefore(sector.key()), then);
        VBox c = new VBox(4, StatementView.titleBlock(sector.label(), "Investor report", period(now)));
        if (a == null) {
            c.getChildren().add(caption(Equity.indexOf(sector.key()) < 0 ? "It is not listed." : "Not counted yet: a month "
                    + "has to run after a load.", Palette.TEXT_MUTED));
        } else {
            String head = Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL);
            c.getChildren().add(shareRow("A SHARE", "this month", "last month", Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL), head));
            for (int i = 0; i < SHARE_ROWS.length; i++) {
                c.getChildren().add(shareRow(SHARE_ROWS[i][0], shareWords(a[i], SHARE_ROWS[i][1]),
                        b == null ? "—" : shareWords(b[i], SHARE_ROWS[i][1]),
                        Palette.words(Palette.SIZE_BODY, Palette.TEXT_LABEL), Palette.figureRegular(Palette.SIZE_BODY, Palette.TEXT_BODY)));
            }
            double roe = SectorStatements.returnOnEquity(now, then);
            c.getChildren().add(shareRow("Return on equity, a year", Double.isFinite(roe) ? pctWords(roe) : "—", "",
                    Palette.words(Palette.SIZE_BODY, Palette.TEXT_LABEL), Palette.figureRegular(Palette.SIZE_BODY, Palette.TEXT_BODY)));
        }
        c.setMaxWidth(StatementView.TABLE + 32);
        c.setStyle("-fx-padding: 14 16 14 16; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return c;
    }

    /** One of the report's figures in words, by its kind. */
    static String shareWords(double v, String kind) {
        if (!Double.isFinite(v)) return "—";
        return switch (kind) {
            case "n" -> String.format("%,.0f", v);
            case "$" -> tightMoney(toDollars(v), false);
            case "%" -> pctWords(v);
            case "x" -> timesWords(v);
            default -> m(v);
        };
    }

    /** The report's columns. */
    static final double SHARE_LABEL = 220, SHARE_FIGURE = 120;

    private static HBox shareRow(String label, String now, String then, String labelStyle, String figureStyle) {
        Label l = new Label(label);
        l.setMinWidth(SHARE_LABEL);
        l.setPrefWidth(SHARE_LABEL);
        l.setStyle(labelStyle);
        Label a = new Label(now), b = new Label(then);
        for (Label f : new Label[] { a, b }) {
            f.setMinWidth(SHARE_FIGURE);
            f.setPrefWidth(SHARE_FIGURE);
            f.setAlignment(Pos.CENTER_RIGHT);
            f.setStyle(figureStyle);
        }
        HBox row = new HBox(Palette.GAP, l, a, b);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** WHO HOLDS IT: the households, the world and the bank's desk, and what it has raised from them and paid them since founding. */
    VBox whoHoldsIt(int company) {
        Equity register = ui.game.getEquity();
        if (company < 0 || !(register.getShares(company) > 0)) return null;
        double abroad = register.foreignShare(company), desk = register.deskShare(company);
        double home = Math.max(0, 1 - abroad - desk);
        List<Segment> held = new ArrayList<>();
        List<HBox> heldKey = new ArrayList<>();
        held.add(new Segment(home, Palette.PEOPLE, false, null, null, "the city's households  " + BuildScreen.pct(home), null));
        heldKey.add(keyPart(Palette.PEOPLE, "the city's households", BuildScreen.pct(home)));
        held.add(new Segment(abroad, Palette.ORE, false, null, null, "held abroad  " + BuildScreen.pct(abroad), null));
        heldKey.add(keyPart(Palette.ORE, "held abroad", BuildScreen.pct(abroad)));
        if (desk > 0) {
            held.add(new Segment(desk, Palette.MONEY, false, null, null, "on the bank's desk  " + BuildScreen.pct(desk), null));
            heldKey.add(keyPart(Palette.MONEY, "on the bank's desk", BuildScreen.pct(desk)));
        }
        javafx.scene.layout.FlowPane since = new javafx.scene.layout.FlowPane(18, 8);
        since.getChildren().addAll(
                miniFigure("RAISED AT HOME", m(register.getLifetimeRaisedHome(company)), "since founding", Palette.TEXT_HEAD, null),
                miniFigure("RAISED ABROAD", m(register.getLifetimeRaisedAbroad(company)), "since founding", Palette.TEXT_HEAD, null),
                miniFigure("PAID AT HOME", m(register.getLifetimeDividendsHome(company)), "since founding", Palette.TEXT_HEAD, null),
                miniFigure("PAID ABROAD", m(register.getLifetimeDividendsAbroad(company)), "since founding", Palette.TEXT_HEAD, null));
        VBox c = card(head("WHO HOLDS IT", ownersPolicy(company), null), segmentBar(held, 1, List.of(), 0, 14), key(heldKey), since);
        c.setMaxWidth(StatementView.TABLE / 2 + 80);
        return c;
    }

    /** HOW IT IS FINANCED: what it owes and its owners' equity, each a share of what it owns, against the equity share it aims for. */
    VBox howFinanced(int company, SectorBooks.SectorMonth now) {
        double assets = now.totalAssets();
        if (!(assets > 0)) return null;
        double owed = Math.max(0, now.totalLiabilities()) / assets, owners = Math.max(0, now.equity()) / assets;
        double target = company < 0 ? Double.NaN : ui.game.getEquity().getTargetEquityShare(company);
        List<Segment> parts = List.of(
                new Segment(owners, Palette.BUSINESS, false, null, null, "its owners  " + BuildScreen.pct(owners), null),
                new Segment(owed, Palette.MONEY, false, null, null, "its lenders and suppliers  " + BuildScreen.pct(owed), null));
        List<Tick> ticks = Double.isFinite(target) ? List.of(new Tick(target, Palette.TEXT_HEAD, 2, "aims for "
                + BuildScreen.pct(target), "The equity share its register aims for, which moves with how profitable it is.")) : List.of();
        VBox c = ratioColumn("HOW IT IS FINANCED", segmentBar(parts, Math.max(1, owners + owed), ticks, 0, 12),
                key(List.of(keyPart(Palette.BUSINESS, "its owners", BuildScreen.pct(owners)),
                        keyPart(Palette.MONEY, "its lenders and suppliers", BuildScreen.pct(owed)))));
        c.setPrefWidth(320);
        c.setMaxWidth(360);
        return c;
    }

    /**
     * FOR ITS SHAREHOLDERS (the Investors summary): the last trade and fair
     * value, earnings and the dividend a share, the yield, price to earnings
     * and to book, and who holds it - the reads Equity and Exchange already
     * keep, as the month closed.
     */
    VBox forShareholders(Sector sector, SectorBooks.SectorMonth now) {
        SectorBooks.Shares s = ui.game.getSectorBooks().shares(sector.key());
        double[] f = shareFigures(s, now);
        int company = Equity.indexOf(sector.key());
        if (f == null || company < 0) return null;
        Equity register = ui.game.getEquity();
        double abroad = register.foreignShare(company), desk = register.deskShare(company);
        javafx.scene.layout.FlowPane strip = new javafx.scene.layout.FlowPane(18, 8);
        strip.getChildren().addAll(
                miniFigure(s.traded() ? "LAST TRADE" : "NOT TRADED: FAIR VALUE", shareWords(f[5], "$"), null, Palette.TEXT_HEAD, null),
                miniFigure("FAIR VALUE", shareWords(f[6], "$"), "the register's", Palette.TEXT_HEAD, null),
                miniFigure("EARNINGS A SHARE", shareWords(f[1], "$"), "the month", f[1] < 0 ? Palette.BAD : Palette.TEXT_HEAD, null),
                miniFigure("DIVIDEND A SHARE", shareWords(f[2], "$"), "the month", Palette.TEXT_HEAD, null),
                miniFigure("YIELD", shareWords(f[9], "%"), "on a year's dividend", Palette.TEXT_HEAD, null),
                miniFigure("P/E", shareWords(f[7], "x"), "on a year at this month's", Palette.TEXT_HEAD, null),
                miniFigure("P/B", shareWords(f[8], "x"), "on its book", Palette.TEXT_HEAD, null),
                miniFigure("HELD", BuildScreen.pct(Math.max(0, 1 - abroad - desk)) + " here",
                        BuildScreen.pct(abroad) + " abroad" + (desk > 0 ? ", " + BuildScreen.pct(desk) + " on the desk" : ""),
                        Palette.TEXT_HEAD, null));
        return card(head("FOR ITS SHAREHOLDERS", "A share as the month closed: the last trade on the book (fair value until "
                + "it trades) and the register's fair value, what it earned and paid a share this month, the yield on a "
                + "year's dividend, price to a year's earnings at this month's and to its book, and who holds it. The "
                + "Statement view has the investor report, against last month.", null), strip);
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
        // ...and for its shareholders (0.7.74, spec 4.4) - and its owners' card, moved here from the Balance sheet (0.7.75, D7).
        VBox holders = forShareholders(sector, now);
        if (holders != null) page.getChildren().add(holders);
        VBox owners = ownersCard(Equity.indexOf(key), now.equity(), now.netIncome(), true);
        if (owners != null) page.getChildren().add(owners);

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
                    + "loan with the insurance premium added - read at %.2f%%, the rate less the inflation "
                    + "people expect, never under %.0f%% of it. What its till and its owners cannot put "
                    + "down, it does not build; an order the lender will not carry is trimmed down until "
                    + "it does, and dropped if even one would not.",
                    credit.getInsuredMortgageRate() * 100, Mortgage.MORTGAGE_AMORTIZATION_MONTHS / 12,
                    plans.realTestRate(credit.getInsuredMortgageRate()) * 100,
                    BusinessInvestment.REAL_HURDLE_FLOOR * 100);
            out.add(new String[] {String.format("puts %.0f%% down of its own", (1 - Mortgage.MORTGAGE_MAX_LOAN_TO_COST) * 100), mortgage});
            out.add(new String[] {String.format("rent covers the mortgage %.2f×", Mortgage.MORTGAGE_DEBT_COVERAGE), mortgage});
        } else {
            out.add(new String[] {String.format("earns %.2f× its interest", BusinessInvestment.PROFIT_OVER_INTEREST),
                    String.format("At %.2f%%: this sector's own rate of %.2f%% less the inflation people expect, never "
                            + "under %.0f%% of it, on whatever it has to borrow after its cash is spent. A plan that fails "
                            + "this is trimmed down until it passes, and dropped if even one unit cannot carry it.",
                            plans.realTestRate(now.rate()) * 100, now.rate() * 100,
                            BusinessInvestment.REAL_HURDLE_FLOOR * 100)});
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
        // The refinery as a picture first (0.7.95): OPERATIONS · THE REFINERY.
        if (sector instanceof ham.citybuildersim.sectors.Refining) page.getChildren().add(refineryCard(RefineryView.of(ui.game)));
        // ...and the oil industry on the wells' page (0.7.96): OPERATIONS · THE OIL INDUSTRY.
        if (sector instanceof ham.citybuildersim.sectors.Oil) page.getChildren().addAll(oilPanels(OilView.of(ui.game)));
        SectorFlow.Flow flow = SectorFlow.of(ui.game, sector);
        page.getChildren().add(flowCard(sector, flow, animate));
        // The new price model, read (0.7.45): the grocers' shelf, the kitchens' and the counters' margin.
        if (sector instanceof Retail shops) page.getChildren().add(shelfCard(shops));
        if (sector instanceof Restaurants kitchens) page.getChildren().add(marginCard(chargesWords(kitchens)));
        if (sector instanceof LuxuryRetail counters) page.getChildren().add(marginCard(chargesWords(counters)));
        List<Sector.Line> own = sector.ownLines(ui.game);
        if (!own.isEmpty()) {
            page.getChildren().add(sectionHead("ITS OWN FIGURES", "The lines this business writes about itself, as "
                    + "figures; each note is the (i) of the line above it.", null));
            page.getChildren().add(factGrid(own, 2, SectorScreen::toneColour));
        }
        page.getChildren().add(details(sector.key() + ":every", "every line, as the page listed them before 0.7.30",
                detailsOpen, ui::redraw, () -> everyLine(sector)));
    }

    /* ------------------- OPERATIONS · THE REFINERY (0.7.95) -------------------
       Batch O11 (runs/spec-oil.md 2.12; the research's mockup 1, Jerus's idea):
       Refining's page opens on the refinery as a picture - the crude in on the
       left, the column filled with its cuts, the conversion units, the tank
       filled by product, and who took each on the right, every ribbon to one
       scale - and a strip of the products under it: made, imported, exported,
       in or out of the tanks, and the price here. The generic flow card follows
       it, with the money.

       NOTHING HERE IS WORKED OUT: every figure, every position, every word and
       its colour is RefineryView's (the model's, pure, held by
       RefineryViewCheck), and this paints it - a ribbon a Path of two curves, a
       box a Rectangle, an outline an SVGPath, the words Plex at the picture's
       sizes, the icons Icons' outlines - and puts what each unit, band and
       taker says under the pointer. Laid out again at its own width when the
       page's changes; the scale is the height's, so it does not move. */

    /** The card's heading, its line, and what its (i) says. */
    static final String REFINERY_HEAD = "THE REFINERY THIS MONTH";
    static final String REFINERY_SUB = "where the crude went · ribbons to scale";
    static final String REFINERY_INFO = "The month's crude comes in on the left, from the wells, the reserve or the world. "
            + "The column cuts it by boiling point - gas at the top, residue at the bottom. Each cut goes through a "
            + "conversion unit, or straight to the product it is worth as it is. The tank in the middle is filled by "
            + "product, as the month made them; under it, what was taken out of the refiners' tanks and what was "
            + "imported, hatched. On the right is who took each product: the cars' petrol at the pump, the vans' and "
            + "the railway's diesel, the factories' lubricants, the roads' bitumen, the tanks, the world. Every ribbon "
            + "is to the same scale. Point at a unit for its spread and what one more would need.";

    /** Refining's picture and strip, or a line saying why there is nothing to draw. */
    VBox refineryCard(RefineryView.View v) {
        Label head = new Label(REFINERY_HEAD);
        head.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        head.setMinWidth(Region.USE_PREF_SIZE);
        Label sub = new Label(REFINERY_SUB);
        sub.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        sub.setMinWidth(Region.USE_PREF_SIZE);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox row = new HBox(Palette.GAP, head, infoButton(REFINERY_INFO, true), sub, gap);
        row.setAlignment(Pos.CENTER_LEFT);
        if (!v.drawn()) return card(row, caption(RefineryView.emptyWords(v), Palette.TEXT_MUTED));
        RefineryPicture picture = new RefineryPicture(v, ui.game.getSectors());
        Label scale = new Label(RefineryView.scaleWords(v.picture(PAGE_WIDE, RefineryView.HEIGHT).scale()));
        scale.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 10px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
        scale.setMinWidth(Region.USE_PREF_SIZE);
        row.getChildren().add(scale);
        return card(row, picture, refineryStrip(v));
    }

    /** The picture: RefineryView.picture() at the pane's width, painted. */
    static final class RefineryPicture extends javafx.scene.layout.Pane {
        private final RefineryView.View view;
        private final Sectors sectors;
        private double drawnAt = -1;

        RefineryPicture(RefineryView.View view, Sectors sectors) {
            this.view = view;
            this.sectors = sectors;
            setMinSize(RefineryView.LEAST_WIDTH, RefineryView.HEIGHT);
            setPrefSize(PAGE_WIDE, RefineryView.HEIGHT);
            setMaxSize(Double.MAX_VALUE, RefineryView.HEIGHT);
            widthProperty().addListener((o, was, now) -> draw(now.doubleValue()));
        }

        private void draw(double width) {
            if (!(width > 0) || Math.abs(width - drawnAt) < .5) return;
            drawnAt = width;
            getChildren().setAll(paint(view.picture(width, RefineryView.HEIGHT), sectors));
        }
    }

    /** A picture's shapes as nodes, in its painting order: the ribbons, the boxes over them, the outlines, lines, icons and words. */
    static List<Node> paint(RefineryView.Picture p, Sectors sectors) {
        List<Node> out = new ArrayList<>();
        for (RefineryView.Ribbon r : p.ribbons()) {
            double xm = (r.x1() + r.x2()) / 2;
            javafx.scene.shape.Path path = new javafx.scene.shape.Path(
                    new javafx.scene.shape.MoveTo(r.x1(), r.a0()),
                    new javafx.scene.shape.CubicCurveTo(xm, r.a0(), xm, r.b0(), r.x2(), r.b0()),
                    new javafx.scene.shape.LineTo(r.x2(), r.b1()),
                    new javafx.scene.shape.CubicCurveTo(xm, r.b1(), xm, r.a1(), r.x1(), r.a1()),
                    new javafx.scene.shape.ClosePath());
            path.setStroke(null);
            path.setFill(r.hatched() ? hatch(r.from(), r.opacity())
                    : r.from().equals(r.to()) ? javafx.scene.paint.Color.web(r.from(), r.opacity())
                    : new javafx.scene.paint.LinearGradient(r.x1(), 0, r.x2(), 0, false, javafx.scene.paint.CycleMethod.NO_CYCLE,
                            new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.web(r.from(), r.opacity())),
                            new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.web(r.to(), r.opacity()))));
            path.setMouseTransparent(true);
            out.add(path);
        }
        for (RefineryView.Box b : p.boxes()) {
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(b.x(), b.y(), b.w(), b.h());
            rect.setArcWidth(2 * b.round());
            rect.setArcHeight(2 * b.round());
            rect.setFill(b.fill() == null ? javafx.scene.paint.Color.TRANSPARENT
                    : b.hatched() ? hatch(b.fill(), b.opacity()) : javafx.scene.paint.Color.web(b.fill(), b.opacity()));
            if (b.stroke() != null) {
                rect.setStroke(javafx.scene.paint.Color.web(b.stroke()));
                rect.setStrokeWidth(b.dashed() ? 1 : 1.2);
                if (b.dashed()) rect.getStrokeDashArray().setAll(3.0, 2.0);
            } else {
                rect.setStroke(null);
            }
            if (b.tip() != null) {
                Tooltip t = new Tooltip(b.tip());
                t.setWrapText(true);
                t.setMaxWidth(420);
                t.setShowDelay(Duration.millis(250));
                Tooltip.install(rect, t);
            } else {
                rect.setMouseTransparent(true);
            }
            out.add(rect);
        }
        for (RefineryView.Outline o : p.outlines()) {
            javafx.scene.shape.SVGPath s = new javafx.scene.shape.SVGPath();
            s.setContent(o.path());
            s.setFill(o.fill() == null ? null : javafx.scene.paint.Color.web(o.fill(), o.opacity()));
            s.setStroke(o.stroke() == null ? null : javafx.scene.paint.Color.web(o.stroke()));
            s.setMouseTransparent(true);
            out.add(s);
        }
        for (RefineryView.Line l : p.lines()) {
            javafx.scene.shape.Line line = new javafx.scene.shape.Line(l.x1(), l.y1(), l.x2(), l.y2());
            line.setStroke(javafx.scene.paint.Color.web(l.colour()));
            line.setMouseTransparent(true);
            out.add(line);
        }
        for (RefineryView.Mark m : p.marks()) {
            Region icon = icon(iconOf(m, sectors), m.colour(), m.size());
            icon.relocate(m.x(), m.y());
            out.add(icon);
        }
        for (RefineryView.Words w : p.words()) out.addAll(text(w));
        return out;
    }

    /** A word and the run after it on its line, from its baseline, aligned on its x; over a halo of the ground if it asks. */
    static List<Node> text(RefineryView.Words w) {
        List<javafx.scene.text.Text> runs = new ArrayList<>();
        double width = 0;
        for (RefineryView.Words r = w; r != null; r = r.then()) {
            javafx.scene.text.Text t = new javafx.scene.text.Text(r.text());
            t.setFont(font(r.face(), r.size()));
            t.setFill(javafx.scene.paint.Color.web(r.colour()));
            runs.add(t);
            width += t.getLayoutBounds().getWidth();
        }
        double x = w.align() == RefineryView.Align.MIDDLE ? w.x() - width / 2
                : w.align() == RefineryView.Align.END ? w.x() - width : w.x();
        List<Node> out = new ArrayList<>();
        for (javafx.scene.text.Text t : runs) {
            t.setX(x);
            t.setY(w.y());
            t.setMouseTransparent(true);
            if (w.halo()) {
                javafx.scene.text.Text halo = new javafx.scene.text.Text(t.getText());
                halo.setFont(t.getFont());
                halo.setX(x);
                halo.setY(w.y());
                halo.setFill(javafx.scene.paint.Color.web(RefineryView.GROUND));
                halo.setStroke(javafx.scene.paint.Color.web(RefineryView.GROUND));
                halo.setStrokeWidth(3.5);
                halo.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
                halo.setMouseTransparent(true);
                out.add(halo);
            }
            out.add(t);
            x += t.getLayoutBounds().getWidth();
        }
        return out;
    }

    /** A picture's face at a size: Plex Sans at its three weights, Plex Mono at two. */
    static javafx.scene.text.Font font(RefineryView.Face f, double size) {
        return switch (f) {
            case SANS -> Palette.Fonts.sansFont(size);
            case SANS_MEDIUM -> Palette.Fonts.sansMediumFont(size);
            case SANS_STRONG -> Palette.Fonts.sansStrongFont(size);
            case MONO -> Palette.Fonts.monoFont(size);
            case MONO_STRONG -> Palette.Fonts.monoStrongFont(size);
        };
    }

    /** What an icon of the picture's is drawn as. */
    static String iconOf(RefineryView.Mark m, Sectors sectors) {
        return switch (m.icon()) {
            case WELL -> Icons.WELL;
            case PLATFORM -> Icons.PLATFORM;
            case RESERVE -> Icons.DROP;
            case TANKER -> Icons.TANKER;
            case TANK -> Icons.TANK;
            case CAR -> Icons.VEHICLES;
            case LORRY -> Icons.LORRY;
            case TRAIN -> Icons.RAIL;
            case FACTORY -> Icons.INDUSTRY;
            case ROAD -> Icons.ROADS;
            case SECTOR -> Icons.ofSector(sectors == null ? null : sectors.byKey(m.sector()));
            case GLOBE -> Icons.TRADE;
            case IDLE -> Icons.ALERT;
            case UNIT -> Icons.VESSEL;
            case FLAME -> Icons.FLAME;
        };
    }

    /** The import's hatching in a colour (mockup 1's: a stripe at .85 every eight pixels on a ground at .14), at an opacity; one pattern a colour and opacity. */
    static javafx.scene.paint.ImagePattern hatch(String colour, double opacity) {
        return HATCHES.computeIfAbsent(colour + "@" + opacity, k -> {
            int n = 8;
            javafx.scene.image.WritableImage tile = new javafx.scene.image.WritableImage(n, n);
            javafx.scene.paint.Color on = javafx.scene.paint.Color.web(colour, .85 * opacity),
                    off = javafx.scene.paint.Color.web(colour, .14 * opacity);
            for (int y = 0; y < n; y++) for (int x = 0; x < n; x++) tile.getPixelWriter().setColor(x, y, (x + y) % n < 3 ? on : off);
            return new javafx.scene.paint.ImagePattern(tile, 0, 0, n, n, false);
        });
    }

    private static final java.util.Map<String, javafx.scene.paint.ImagePattern> HATCHES = new java.util.HashMap<>();

    /** THIS MONTH, BY PRODUCT: its heading, and a cell a product in equal columns. */
    static VBox refineryStrip(RefineryView.View v) {
        Label head = new Label(RefineryView.STRIP_HEAD);
        head.setStyle(Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        head.setMinWidth(Region.USE_PREF_SIZE);
        Label words = new Label(RefineryView.STRIP_WORDS);
        words.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        HBox top = new HBox(Palette.GAP_LOOSE, head, words);
        top.setAlignment(Pos.BASELINE_LEFT);
        GridPane grid = new GridPane();
        grid.setHgap(6);
        List<RefineryView.Cell> cells = RefineryView.strip(v);
        for (int i = 0; i < cells.size(); i++) {
            javafx.scene.layout.ColumnConstraints c = new javafx.scene.layout.ColumnConstraints();
            c.setPercentWidth(100.0 / cells.size());
            grid.getColumnConstraints().add(c);
            grid.add(stripCell(cells.get(i)), i, 0);
        }
        return new VBox(6, top, grid);
    }

    /** One product's cell: its swatch and name, its bar, and its five lines. */
    static VBox stripCell(RefineryView.Cell c) {
        String colour = RefineryView.colour(c.good());
        Region swatch = new Region();
        swatch.setMinSize(10, 10);
        swatch.setMaxSize(10, 10);
        swatch.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 2;");
        Label name = new Label(c.name());
        name.setStyle(Palette.Fonts.sansMedium() + " -fx-font-size: 11.5px; -fx-text-fill: " + Palette.TEXT + ";");
        HBox nameRow = new HBox(6, swatch, name);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        // The bar: taken here, imported (hatched), exported (pale), each its share.
        GridPane bar = new GridPane();
        bar.setMinHeight(7);
        bar.setPrefHeight(7);
        bar.setMaxHeight(7);
        bar.setStyle("-fx-background-color: #0e1620; -fx-background-radius: 2;");
        double[] share = c.share();
        javafx.scene.paint.Paint[] fills = { javafx.scene.paint.Color.web(colour), hatch(colour, 1),
                javafx.scene.paint.Color.web(colour, .35) };
        for (int i = 0; i < 3; i++) {
            javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints();
            cc.setPercentWidth(100 * share[i]);
            bar.getColumnConstraints().add(cc);
            Region seg = new Region();
            seg.setMinHeight(7);
            seg.setMaxWidth(Double.MAX_VALUE);
            seg.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(fills[i], null, null)));
            bar.add(seg, i, 0);
        }

        VBox cell = new VBox(4, nameRow, bar);
        String[] figures = { c.made(), c.imported(), c.exported(), c.tanks(), c.price() };
        VBox lines = new VBox(0);
        for (int i = 0; i < figures.length; i++) {
            Label l = new Label(RefineryView.CELL_LINES[i]);
            l.setStyle("-fx-font-size: 10.5px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
            l.setMinWidth(Region.USE_PREF_SIZE);
            Label f = new Label(figures[i]);
            f.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: 10.5px; -fx-text-fill: "
                    + (i == 1 && c.hot() ? Palette.WARN : Palette.TEXT_LABEL) + ";");
            f.setMinWidth(Region.USE_PREF_SIZE);
            Region spring = new Region();
            HBox.setHgrow(spring, Priority.ALWAYS);
            lines.getChildren().add(new HBox(4, l, spring, f));
        }
        cell.getChildren().add(lines);
        cell.setStyle("-fx-padding: 6 8 7 8; -fx-background-color: " + Palette.PANEL + "; -fx-background-radius: 4;"
                + " -fx-border-color: " + Palette.EDGE + "; -fx-border-radius: 4;");
        cell.setMinWidth(0);
        Tooltip.install(cell, new Tooltip(c.name() + ": made here " + c.made() + ", imported " + c.imported() + ", exported "
                + c.exported() + ", into (+) or out of (−) the refiners' tanks " + c.tanks() + ", " + c.price() + " here."));
        return cell;
    }

    /* ----------------- OPERATIONS · THE OIL INDUSTRY (0.7.96) -----------------
       Batch O12 (runs/spec-oil.md 2.13; the research's mockup 2, Jerus's oil
       industry screen): Oil's page opens on the whole chain - four figures
       (what the wells lifted, the crude left in the two pools, the crude
       bought abroad, the products across the edge); THE WELLS by type, land
       and offshore, and LOCAL CRUDE, a bar a year of what they would lift if
       nothing new were built; THE REFINERY's units, each with its spread at
       the city's own prices and the gate that stops one more; PRODUCTS, each
       one's price and month and who took it; and THE STRATEGIC RESERVE with
       its two levers, Fill and Release. The generic flow card follows.

       NOTHING HERE IS WORKED OUT: every figure, word and colour, the boxes'
       and the tables' widths and the chart's shapes are OilView's (the
       model's, pure, held by OilViewCheck); the chart is painted by paint(),
       the refinery picture's painter; the levers are Levers' dial cards,
       applied through Game.fillReserve() and releaseReserve(). */

    /** What the oil industry's (i)s say. */
    static final String OIL_WELLS_INFO = "The city's wells by kind. A land well lifts the ground pool - the oil under the city's dry"
            + " ground - and loses a tenth of its lift a year; a platform's wells lift the offshore pool, hold their lift for three"
            + " years and then lose 8.5% a year. Each wears out under ten barrels a day. The chart is what the wells standing"
            + " would lift over the next ten years if nothing new were built: this month solid, each year ahead pale, each pool"
            + " lifted no further than it has oil.";
    static final String OIL_UNITS_INFO = "Every kind of refinery unit. Its spread is what it makes of a litre of its feed, less"
            + " what that litre would fetch as it is, at the city's own prices - the import price for a product the city is short"
            + " of, the export price for one it has spare. The refiners order the building that earns most on its cost and passes"
            + " every gate - feed, ground, staff, money; the state says what stops one more. Point at a row for the whole of it.";
    static final String OIL_PRODUCTS_INFO = "Every product of the refinery, and crude: its price here and the world's, the"
            + " world's over crude's (the research's ladder), and this month's - made here, used here, imported, exported - with"
            + " who took it, the same takers as Refining's picture.";
    static final String OIL_RESERVE_INFO = "The city's own crude, in its Strategic Reserves' tanks. Fill orders crude for the next"
            + " clearing - the wells' first, beside the refiners, then the world's - cut to the room left and to what the treasury"
            + " can pay at crude's import price, paid at the next strike. Release offers that many tonnes a month to the"
            + " refiners, and ships what they do not take at the export price. A fill stops a release; a release cancels a fill."
            + " Both are lines of the budget: \"Crude for the reserve\" in spending, \"Crude sold from the reserve\" in revenue.";
    static final String RESERVE_CAVEAT = "bought at the next clearing at what crude then costs; what the room cannot take lapses";
    static final String RESERVE_CAVEAT_INFO = "A fill is an order for the next clearing: the wells' crude pro rata with the"
            + " refiners, the world's for the rest, each at its price then - this reads it at today's import price. What the"
            + " room left cannot hold lapses with the month. The treasury pays at the next strike.";
    static final String RELEASE_CAVEAT = "offered to the refiners first; what they do not take ships at the export price";

    /** The lever's ladder width on its dial card. */
    static final double RESERVE_LADDER = 380;

    /** Oil's panels, in the page's order. */
    List<Node> oilPanels(OilView.View v) {
        List<Node> out = new ArrayList<>();
        out.add(oilFigures(v));
        VBox wells = oilWells(v), units = oilUnits(v);
        wells.setMinWidth(OilView.WELLS_W);
        wells.setPrefWidth(OilView.WELLS_W);
        wells.setMaxWidth(OilView.WELLS_W);
        units.setMinWidth(0);
        units.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(units, Priority.ALWAYS);
        HBox two = new HBox(OilView.BOX_GAP, wells, units);
        two.setAlignment(Pos.TOP_LEFT);
        out.add(two);
        out.add(oilProducts(v));
        out.add(oilReserve(v));
        return out;
    }

    /** A box of the page (mockup 2's .box): the panel's ground, an edge, its rows. */
    static VBox oilBox(Node... rows) {
        VBox b = new VBox(8);
        for (Node n : rows) if (n != null) b.getChildren().add(n);
        b.setStyle(String.format("-fx-padding: %s %s %s %s; -fx-background-color: %s; -fx-background-radius: 4;"
                        + " -fx-border-color: %s; -fx-border-radius: 4;", OilView.BOX_PAD_Y, OilView.BOX_PAD_X, OilView.BOX_PAD_Y,
                OilView.BOX_PAD_X, Palette.PANEL, Palette.EDGE));
        return b;
    }

    /** A box's heading: its title, its line, an (i), and something at its right. */
    static HBox oilHead(String title, String line, String info, Node right) {
        Label t = new Label(title);
        t.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        t.setMinWidth(Region.USE_PREF_SIZE);
        HBox row = new HBox(Palette.GAP, t);
        if (info != null) row.getChildren().add(infoButton(info, true));
        if (line != null) {
            Label l = new Label(line);
            l.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            l.setMinWidth(0);
            row.getChildren().add(l);
        }
        if (right != null) {
            Region gap = new Region();
            HBox.setHgrow(gap, Priority.ALWAYS);
            row.getChildren().addAll(gap, right);
        }
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** Words at a size in a colour, wrapping. */
    static Label oilWords(String text, double size, String colour) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setMinWidth(0);
        l.setStyle("-fx-font-size: " + size + "px; -fx-text-fill: " + colour + ";");
        return l;
    }

    /** A figure at a size in a colour, the regular weight, never cut. */
    static Label oilFigure(String text, double size, String colour) {
        Label l = new Label(text);
        l.setMinWidth(Region.USE_PREF_SIZE);
        l.setStyle("-fx-font-family: " + Palette.mono() + "; -fx-font-size: " + size + "px; -fx-text-fill: " + colour + ";");
        return l;
    }

    /** A tooltip on a node. */
    static void oilTip(Node n, String tip) {
        if (tip == null || tip.isEmpty()) return;
        Tooltip t = new Tooltip(tip);
        t.setWrapText(true);
        t.setMaxWidth(420);
        t.setShowDelay(Duration.millis(250));
        Tooltip.install(n, t);
    }

    /** A swatch: a product's or a series' colour in a small square. */
    static Region oilSwatch(String colour) {
        Region s = new Region();
        s.setMinSize(10, 10);
        s.setMaxSize(10, 10);
        s.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 2;");
        return s;
    }

    /** The four figures across the page (mockup 2's .figs). */
    static GridPane oilFigures(OilView.View v) {
        GridPane grid = new GridPane();
        grid.setHgap(OilView.TILE_GAP);
        List<OilView.Figure> figures = OilView.figures(v);
        for (int i = 0; i < figures.size(); i++) {
            OilView.Figure f = figures.get(i);
            javafx.scene.layout.ColumnConstraints c = new javafx.scene.layout.ColumnConstraints();
            c.setPercentWidth(100.0 / figures.size());
            grid.getColumnConstraints().add(c);
            Label label = new Label(f.label());
            label.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            Label value = new Label(f.value());
            value.setStyle(Palette.figure((int) OilView.FIGURE_SIZE, f.colour()));
            value.setMinWidth(Region.USE_PREF_SIZE);
            Label line = new Label(f.line());
            line.setStyle("-fx-font-size: " + OilView.TILE_WORDS + "px; -fx-text-fill: " + Palette.TEXT_LABEL + ";");
            line.setMinWidth(0);
            VBox tile = new VBox(2, label, value, line);
            tile.setMaxWidth(Double.MAX_VALUE);
            tile.setStyle(String.format("-fx-padding: 8 %s 8 %s; -fx-background-color: %s; -fx-background-radius: 4;"
                    + " -fx-border-color: %s; -fx-border-radius: 4;", OilView.TILE_PAD_X, OilView.TILE_PAD_X, Palette.RAISED, Palette.EDGE));
            oilTip(tile, f.tip());
            grid.add(tile, i, 0);
        }
        return grid;
    }

    /** THE WELLS: the two cards by kind, the chart with its legend, the investors' words. */
    VBox oilWells(OilView.View v) {
        VBox land = oilCard(Icons.WELL, "Land wells", String.format("%,d", v.landWells()));
        for (OilView.Fact f : OilView.landFacts(v)) land.getChildren().add(oilFact(f));
        VBox sea = oilCard(Icons.PLATFORM, "Offshore platforms", String.format("%,d", v.platforms().size()));
        if (v.platforms().isEmpty()) {
            sea.getChildren().add(oilWords(OilView.noPlatformWords(v), OilView.CARD_WORDS, Palette.TEXT_MUTED));
        } else {
            for (OilView.PlatformLine p : OilView.platformLines(v)) sea.getChildren().add(oilPlatform(p));
        }
        for (OilView.Fact f : OilView.seaFacts(v)) sea.getChildren().add(oilFact(f));
        // ...side by side, as tall as the taller (mockup 2's .wt grid).
        land.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        sea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane cards = new GridPane();
        cards.setHgap(OilView.CARD_GAP);
        for (int i = 0; i < 2; i++) {
            javafx.scene.layout.ColumnConstraints c = new javafx.scene.layout.ColumnConstraints();
            c.setPercentWidth(50);
            cards.getColumnConstraints().add(c);
        }
        cards.add(land, 0, 0);
        cards.add(sea, 1, 0);

        HBox legend = new HBox(10);
        legend.setAlignment(Pos.CENTER_RIGHT);
        for (OilView.Series s : v.series()) {
            Label name = new Label(s.name());
            name.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            name.setMinWidth(Region.USE_PREF_SIZE);
            HBox item = new HBox(4, oilSwatch(s.colour()), name);
            item.setAlignment(Pos.CENTER_LEFT);
            legend.getChildren().add(item);
        }
        javafx.scene.layout.Pane chart = new javafx.scene.layout.Pane();
        chart.setMinSize(OilView.CHART_W, OilView.CHART_HEIGHT);
        chart.setPrefSize(OilView.CHART_W, OilView.CHART_HEIGHT);
        chart.setMaxSize(OilView.CHART_W, OilView.CHART_HEIGHT);
        chart.getChildren().setAll(paint(v.chart(OilView.CHART_W, OilView.CHART_HEIGHT), ui.game.getSectors()));

        VBox box = oilBox(oilHead("THE WELLS", "by type", OIL_WELLS_INFO, null), cards,
                oilHead("LOCAL CRUDE · B/D", null, null, legend), chart);
        for (String w : OilView.drillingWords(v)) box.getChildren().add(oilWords(w, Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        return box;
    }

    /** A wells card (mockup 2's .wc): its icon, its name, its count at the right. */
    static VBox oilCard(String svg, String name, String count) {
        Label n = new Label(name);
        n.setStyle(Palette.strong((int) OilView.CARD_HEAD, Palette.TEXT));
        n.setMinWidth(0);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox head = new HBox(7, icon(svg, Palette.ORE, 17), n, gap, oilFigure(count, 12, Palette.TEXT_LABEL));
        head.setAlignment(Pos.CENTER_LEFT);
        VBox card = new VBox(1, head);
        card.setStyle(String.format("-fx-padding: 8 %s 8 %s; -fx-background-color: %s; -fx-background-radius: 4;"
                + " -fx-border-color: %s; -fx-border-radius: 4;", OilView.CARD_PAD_X, OilView.CARD_PAD_X, Palette.RAISED, Palette.EDGE));
        return card;
    }

    /** A line of a wells card (mockup 2's .kv): its words, its figure at the right. */
    static HBox oilFact(OilView.Fact f) {
        Label l = new Label(f.label());
        l.setStyle("-fx-font-size: " + OilView.CARD_WORDS + "px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
        l.setMinWidth(0);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox row = new HBox(8, l, gap, oilFigure(f.value(), OilView.CARD_WORDS, f.colour()));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** A platform's lines (mockup 2's .plat): its colour, its name and its figure, and under them its words. */
    static VBox oilPlatform(OilView.PlatformLine p) {
        Label name = new Label(p.name());
        name.setStyle("-fx-font-size: " + OilView.CARD_WORDS + "px; -fx-text-fill: " + Palette.TEXT_LABEL + ";");
        name.setMinWidth(0);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox top = new HBox(OilView.DOT_GAP, oilSwatch(p.colour()), name, gap, oilFigure(p.figure(), OilView.CARD_WORDS, Palette.TEXT));
        top.setAlignment(Pos.CENTER_LEFT);
        Label words = oilWords(p.words(), OilView.CARD_WORDS - 1, Palette.TEXT_MUTED);
        words.setStyle(words.getStyle() + " -fx-padding: 0 0 0 " + (OilView.DOT + OilView.DOT_GAP) + ";");
        VBox lines = new VBox(0, top, words);
        lines.setStyle("-fx-padding: 3 0 3 0; -fx-border-color: #1d2b3c transparent transparent transparent;");
        oilTip(lines, p.tip());
        return lines;
    }

    /** A table's grid and its heads: a column a width (0: it takes the rest), each column's cells to the right where `right` says. */
    static GridPane oilTable(double[] widths, String[] heads, boolean[] right) {
        GridPane g = new GridPane();
        g.setVgap(3);
        for (int i = 0; i < widths.length; i++) {
            javafx.scene.layout.ColumnConstraints c = new javafx.scene.layout.ColumnConstraints();
            if (widths[i] > 0) {
                c.setMinWidth(widths[i]);
                c.setPrefWidth(widths[i]);
                c.setMaxWidth(widths[i]);
            } else {
                c.setMinWidth(0);
                c.setHgrow(Priority.ALWAYS);
            }
            c.setHalignment(right[i] ? javafx.geometry.HPos.RIGHT : javafx.geometry.HPos.LEFT);
            g.getColumnConstraints().add(c);
            Label h = new Label(heads[i].toUpperCase());
            h.setStyle(Palette.words((int) OilView.HEAD_WORDS, Palette.TEXT_MUTED));
            h.setMinWidth(0);
            g.add(oilCell(h, right[i]), i, 0);
        }
        return g;
    }

    /** A cell: its node in the cell's padding, to the left or the right; a left cell's words wrap in the column's width. */
    static HBox oilCell(Node n, boolean right) {
        HBox c = new HBox(n);
        if (!right) HBox.setHgrow(n, Priority.ALWAYS);
        c.setAlignment(right ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        c.setMinWidth(0);
        c.setStyle(String.format("-fx-padding: 3 %s 3 %s;", OilView.CELL_PAD_X, OilView.CELL_PAD_X));
        return c;
    }

    /** A rule under a table's row. */
    static Region oilRule() {
        Region r = new Region();
        r.setMinHeight(1);
        r.setMaxHeight(1);
        r.setMaxWidth(Double.MAX_VALUE);
        r.setStyle("-fx-background-color: #18263a;");
        return r;
    }

    /** A pill (mockup 2's .pill): its words in its colour, an edge of it. */
    static Label oilPill(String text, String colour) {
        Label l = new Label(text);
        l.setMinWidth(Region.USE_PREF_SIZE);
        l.setStyle(String.format("-fx-font-size: %spx; -fx-text-fill: %s; -fx-padding: 1 %s 1 %s; -fx-border-color: %s;"
                + " -fx-border-radius: 3; -fx-background-radius: 3;", OilView.PILL_WORDS, colour, OilView.PILL_PAD_X,
                OilView.PILL_PAD_X, tint(colour, .45)));
        return l;
    }

    /** THE REFINERY: its units as a table, and the refiners' word. */
    static VBox oilUnits(OilView.View v) {
        boolean[] right = { false, true, true, true, false };
        GridPane t = oilTable(OilView.UNIT_WIDTHS, OilView.UNIT_HEADS, right);
        int row = 1;
        for (OilView.UnitLine u : OilView.unitLines(v)) {
            t.add(oilRule(), 0, row++, OilView.UNIT_WIDTHS.length, 1);
            Label name = new Label(u.name());
            name.setStyle(Palette.Fonts.sansMedium() + " -fx-font-size: " + OilView.CELL + "px; -fx-text-fill: " + Palette.TEXT + ";");
            name.setMinWidth(Region.USE_PREF_SIZE);
            HBox top = new HBox(6, name, oilFigure(u.count(), OilView.CELL, Palette.TEXT_MUTED));
            top.setAlignment(Pos.BASELINE_LEFT);
            VBox first = new VBox(1, top, oilWords(u.what(), OilView.HEAD_WORDS, Palette.TEXT_MUTED));
            first.setMinWidth(0);
            HBox c0 = oilCell(first, false);
            oilTip(c0, u.tip());
            t.add(c0, 0, row);
            t.add(oilCell(oilFigure(u.barrels(), OilView.CELL, Palette.TEXT_LABEL), true), 1, row);
            t.add(oilCell(oilFigure(u.running(), OilView.CELL, "on site".equals(u.running()) ? Palette.ACCENT
                    : "idle".equals(u.running()) ? Palette.WARN : Palette.TEXT_LABEL), true), 2, row);
            t.add(oilCell(oilFigure(u.spread(), OilView.CELL, u.spreadColour()), true), 3, row);
            HBox state = new HBox(OilView.PILL_GAP, oilPill(u.state(), u.stateColour()),
                    oilWords(u.words(), OilView.CELL_NOTE, Palette.TEXT_MUTED));
            state.setAlignment(Pos.CENTER_LEFT);
            HBox c4 = oilCell(state, false);
            oilTip(c4, u.tip());
            t.add(c4, 4, row++);
        }
        VBox box = oilBox(oilHead("THE REFINERY", "its units · spread: what a unit makes less its feed, a litre of feed, at the"
                + " city's own prices", OIL_UNITS_INFO, null), t);
        String w = OilView.orderingWords(v);
        if (w != null) box.getChildren().add(oilWords(w, Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        return box;
    }

    /** PRODUCTS: crude and the nine, a row each. */
    static VBox oilProducts(OilView.View v) {
        boolean[] right = { false, true, true, true, true, true, true, true, false };
        GridPane t = oilTable(OilView.PRODUCT_WIDTHS, OilView.PRODUCT_HEADS, right);
        int row = 1;
        for (OilView.ProductLine p : OilView.productLines(v)) {
            t.add(oilRule(), 0, row++, OilView.PRODUCT_WIDTHS.length, 1);
            Label name = new Label(p.name());
            name.setStyle("-fx-font-size: " + OilView.CELL + "px; -fx-text-fill: " + Palette.TEXT + ";");
            name.setMinWidth(Region.USE_PREF_SIZE);
            HBox first = new HBox(7, oilSwatch(p.colour()), name);
            if (p.note() != null) first.getChildren().add(oilWords("(" + p.note() + ")", OilView.CELL_NOTE, Palette.TEXT_MUTED));
            first.setAlignment(Pos.CENTER_LEFT);
            HBox c0 = oilCell(first, false);
            oilTip(c0, p.tip());
            t.add(c0, 0, row);
            String[] figs = { p.price(), p.world(), p.ladder(), p.made(), p.used(), p.imported(), p.exported() };
            for (int i = 0; i < figs.length; i++) {
                t.add(oilCell(oilFigure(figs[i], OilView.CELL, i == 5 && p.hot() ? Palette.WARN : Palette.TEXT_LABEL), true), i + 1, row);
            }
            t.add(oilCell(oilWords(p.where(), OilView.CELL_NOTE, Palette.TEXT_MUTED), false), 8, row++);
        }
        return oilBox(oilHead("PRODUCTS", OilView.PRODUCTS_WORDS, OIL_PRODUCTS_INFO, oilWords(OilView.PRODUCTS_RIGHT,
                Palette.SIZE_LABEL, Palette.TEXT_MUTED)), t);
    }

    /** THE STRATEGIC RESERVE: its lines, and its two levers on dial cards; or the words that none stands. */
    VBox oilReserve(OilView.View v) {
        OilView.Reserve r = v.reserve();
        HBox head = oilHead("THE STRATEGIC RESERVE", "the city's own crude, in its own tanks", OIL_RESERVE_INFO, null);
        if (!r.shown()) return oilBox(head, oilWords(OilView.NO_RESERVE, Palette.SIZE_LABEL + 1, Palette.TEXT_MUTED));
        GridPane facts = new GridPane();
        facts.setHgap(24);
        facts.setVgap(2);
        for (int i = 0; i < 2; i++) {
            javafx.scene.layout.ColumnConstraints c = new javafx.scene.layout.ColumnConstraints();
            c.setPercentWidth(50);
            facts.getColumnConstraints().add(c);
        }
        List<OilView.Fact> lines = OilView.reserveFacts(v);
        for (int i = 0; i < lines.size(); i++) facts.add(oilFact(lines.get(i)), i % 2, i / 2);
        Game g = ui.game;

        double fillMost = Math.max(r.fillMost(), r.fill());
        Ladder fill = fillMost > 0 ? ui.policyScreen.ownLadder("reserveFill", r.fill(), 0, fillMost, OilView.step(fillMost),
                RefineryView::tonnes) : null;
        double fillWant = ui.policyScreen.staged("reserveFill", r.fill());
        VBox fillCard = Levers.dialCard(new Levers.DialCard(Icons.TANK, Palette.ORE, "FILL", OIL_RESERVE_INFO,
                OilView.fillReading(r), OilView.fillStatus(r), null, fill, fillWant, effects(g, x -> OilView.fillEffects(r, x)),
                RESERVE_CAVEAT, RESERVE_CAVEAT_INFO, ui.policyScreen.applyFoot("reserveFill",
                        "Order " + RefineryView.tonnes(fillWant) + " of crude", () -> g.fillReserve(fillWant))),
                RESERVE_LADDER, false);

        double releaseMost = r.releaseMost();
        Ladder release = releaseMost > 0 ? ui.policyScreen.ownLadder("reserveRelease", r.release(), 0, releaseMost,
                OilView.step(releaseMost), t -> RefineryView.tonnes(t) + " a month") : null;
        double releaseWant = ui.policyScreen.staged("reserveRelease", r.release());
        VBox releaseCard = Levers.dialCard(new Levers.DialCard(Icons.DROP, Palette.ORE, "RELEASE", OIL_RESERVE_INFO,
                OilView.releaseReading(r), OilView.releaseStatus(r), null, release, releaseWant,
                effects(g, x -> OilView.releaseEffects(r, x)), RELEASE_CAVEAT, OIL_RESERVE_INFO,
                ui.policyScreen.applyFoot("reserveRelease", releaseWant > 0 ? "Release " + RefineryView.tonnes(releaseWant)
                        + " a month" : "Stop the release", () -> g.releaseReserve(releaseWant))),
                RESERVE_LADDER, false);
        return oilBox(head, facts, fillCard, releaseCard);
    }

    /** A lever's effects (OilView.Effect) as the dial card's rows: tonnes in the model's words, money in the city's mark. */
    static java.util.function.DoubleFunction<List<Pieces.Effect>> effects(Game g,
            java.util.function.DoubleFunction<List<OilView.Effect>> of) {
        return x -> {
            List<Pieces.Effect> out = new ArrayList<>();
            for (OilView.Effect e : of.apply(x)) {
                java.util.function.DoubleFunction<String> fmt = e.unit() == OilView.Unit.MONEY ? t -> FundScreen.d(g, t)
                        : RefineryView::tonnes;
                out.add(Pieces.Effect.of(e.label(), e.before(), e.after(), fmt));
            }
            return out;
        };
    }

    /* ---------------------------- THE SHELF (0.7.45) ----------------------------
       The UI spec's 2.5: what a basket costs - the shelf against its floor, its
       cap and the price that would clear the sale, which is a price only above
       the floor (D9) - and the baskets: needed, asked for at the price, what
       the shops could hand over and what they sold, and what limited the sale,
       named from the sale (D15) with a door to its fix. Every figure is
       Retail's own read; the words are pure, for the probe. */

    /** THE SHELF's words and figures, worked out without drawing them. */
    record ShelfWords(boolean counted, double shelf, double floor, double cap, double clearing, boolean clearingDrawn,
                      String clearsTag, String shelfLine, String floorLine, String clearingLine, String clearingTone,
                      double needed, double asked, long pricedOut, double handOver, double supply, double sold,
                      String limit, String limitDoor, String creditLine) { }

    /** A share of the way in words: "a sixth", "a quarter", "half". */
    static String wayWords(double speed) {
        if (Math.abs(speed - 1.0 / 6) < 1e-9) return "a sixth";
        if (Math.abs(speed - .25) < 1e-9) return "a quarter";
        if (Math.abs(speed - 1.0 / 3) < 1e-9) return "a third";
        if (Math.abs(speed - .5) < 1e-9) return "half";
        return String.format("%.0f%%", speed * 100);
    }

    ShelfWords shelfWords(Retail r) {
        boolean counted = r.isSaleCounted();
        double shelf = r.getStoreSellPrice(), floor = r.getFloorPrice(), cap = r.getCapPrice(), clearing = r.getClearingPrice();
        boolean slack = r.isSlack(), past = r.clearsPastTheCap();
        boolean empty = !(r.getDemandAtPrice() > 0) && !(r.getSupplyBaskets() > 0);
        boolean drawn = counted && !empty && !slack && clearing <= 2 * cap;
        String tag = counted && !empty && !slack && clearing > 2 * cap ? "clears at " + unitPrice(clearing) + " ›" : null;
        String shelfLine = "the shelf " + unitPrice(shelf) + String.format(" · %.2f× its floor", r.shelfOverFloor())
                + " · moves " + wayWords(Retail.CLEAR_SPEED) + " of the way a month";
        double opening = r.getOpeningSellPrice();
        // The level this month's constants are struck at (B2, 0.7.47): Retail's
        // expected level is the one the next month strikes at, a month ahead.
        double struck = ui.game.getExpectations().getStruckLevel();
        String floorLine = Math.abs(floor - opening) <= 1e-12 * Math.max(1e-12, opening)
                ? String.format("the floor is the opening price struck at ×%.3f this month", struck)
                : String.format("the floor is its stock's cost × %.1f - over the opening price, %s struck at ×%.3f",
                        Retail.RETAIL_MARKUP, unitPrice(opening), struck);
        String clearingLine, clearingTone = Palette.TEXT_LABEL;
        if (!counted) {
            clearingLine = "the price that clears it: not counted yet - a month has to run after a load";
        } else if (empty) {
            // A city with nobody asking and nothing to hand over: the bisection's top is no price either.
            clearingLine = "nothing to clear: nobody asked for a basket and the shops had none to hand over";
        } else if (slack) {
            clearingLine = "clears on its floor: the shops could hand over more than is asked for";
        } else if (past) {
            clearingLine = "a price cannot fix this: even at the cap "
                    + people(ui.game.getHouseholdBalance().groceriesWanted(cap)) + " are asked for against "
                    + people(r.getSupplyBaskets());
            clearingTone = handedTone(r);
        } else {
            clearingLine = "clears at " + unitPrice(clearing) + ": the shelf moves toward it, at most to the cap";
        }
        SectorFlow.Plant plant = SectorFlow.plant(r);
        String limit, door = null;
        if (!counted) {
            limit = "not counted yet: a month has to run after a load";
        } else if (r.getProductsSold() >= Math.floor(r.getDemandAtPrice())) {
            limit = "every basket asked for at the price was handed over";
        } else if (r.isShelfBound()) {
            limit = "the shelf ran out: it bought what its cash and credit reached";
            door = "Cash & debt";
        } else if (!plant.none() && plant.rate() < .995) {
            int low = plant.lowest();
            limit = "the shops run at " + BuildScreen.pct(plant.rate()) + " of the " + people(r.getStoreCoverage())
                    + " they can serve: " + SectorFlow.THROTTLES[low] + " " + BuildScreen.pct(plant.ratios()[low]);
            door = throttleCategory(plant);
        } else {
            limit = "the shops can serve no more";
            door = BuildCard.categoryOf(ui.game, r);
        }
        SupplierCredit credit = r.supplierCredit();
        String creditLine = credit.boughtTotal() > 0 || credit.owedTotal() > 0
                ? "bought " + m(credit.boughtTotal()) + " of its stock on its suppliers' credit, to be paid from this month's"
                  + " takings · may owe up to " + m(credit.getLimit()) + " (a month of the stock it expects to sell, at cost)"
                : null;
        return new ShelfWords(counted, shelf, floor, cap, clearing, drawn, tag, shelfLine, floorLine, clearingLine,
                clearingTone, r.getBasketsNeeded(), r.getDemandAtPrice(), r.getUnaffordableDemand(), r.getHandOver(),
                r.getSupplyBaskets(), r.getProductsSold(), limit, door, creditLine);
    }

    /** The Build category that relieves the thinnest of a plant's power, road and health (D13's rule, one plant), or null. */
    static String throttleCategory(SectorFlow.Plant plant) {
        String go = null;
        double least = Double.POSITIVE_INFINITY;
        String[][] doors = { { "1", "Utilities" }, { "3", "Roads & transit" }, { "4", "Healthcare" } };
        for (String[] d : doors) {
            double r = plant.ratios()[Integer.parseInt(d[0])];
            if (Double.isFinite(r) && r < least && r < .995) { least = r; go = d[1]; }
        }
        return go;
    }

    /** THE SHELF's (i). */
    static final String SHELF_INFO = "A basket is one person's groceries for a month. The shelf moves a sixth of the way "
            + "a month toward the price that would clear the sale - where what the households ask for meets what the "
            + "shops can hand over - never under its floor and never past its cap, half again over the floor. Under "
            + "the floor the price that clears is no price at all: the shops have more than is asked for. Past the cap "
            + "no price the shelf will charge clears it, and only more baskets will.";

    /** THE SHELF: what a basket costs, beside the baskets and what limited them. */
    VBox shelfCard(Retail r) {
        ShelfWords w = shelfWords(r);
        double scale = Math.max(w.shelf(), w.cap()) * 1.1;
        if (w.clearingDrawn()) scale = Math.max(scale, w.clearing() * 1.1);
        List<Rule> rules = new ArrayList<>();
        rules.add(new Rule(w.floor(), Palette.TEXT_MUTED, true, "floor " + unitPrice(w.floor()), "The floor: " + w.floorLine(), null));
        rules.add(new Rule(w.cap(), Palette.TEXT_MUTED, true, "cap " + unitPrice(w.cap()), "The most the shelf goes to", null));
        if (w.clearingDrawn()) {
            rules.add(new Rule(w.clearing(), Palette.TEXT_LABEL, true, "clears " + unitPrice(w.clearing()),
                    "The price that would clear the last sale", null));
        }
        ScaleRow cost = ScaleRow.of("a basket", unitPrice(w.shelf()), List.of(Run.of(0, w.shelf(), Palette.BUSINESS)
                .tip("The shelf price " + unitPrice(w.shelf()))));
        if (w.clearsTag() != null) cost = cost.tag(w.clearsTag(), Palette.TEXT_LABEL);
        VBox left = new VBox(Palette.GAP, head("WHAT A BASKET COSTS", SHELF_INFO, null),
                scaleRows(List.of(cost), scale, rules, 80, 90, 12),
                caption(w.shelfLine(), Palette.TEXT_LABEL), caption(w.floorLine(), Palette.TEXT_MUTED),
                caption(w.clearingLine(), w.clearingTone()));

        double most = Math.max(Math.max(w.needed(), w.asked()), Math.max(w.supply(), w.sold()));
        List<ScaleRow> rows = List.of(
                ScaleRow.of("needed, one a head", people(w.needed()), List.of(Run.of(0, w.needed(), Palette.PEOPLE_LIGHT))),
                ScaleRow.of("asked for at the price", people(w.asked()), List.of(Run.of(0, w.asked(), Palette.PEOPLE)))
                        .tag(w.pricedOut() > 0 ? "priced out " + people(w.pricedOut()) : null, Palette.TEXT_MUTED),
                ScaleRow.of("the shops could hand over", people(w.supply()), List.of(Run.of(0, w.supply(), Palette.BUSINESS_LIGHT))),
                ScaleRow.of("sold", people(w.sold()), List.of(Run.of(0, w.sold(), Palette.BUSINESS))));
        VBox right = new VBox(Palette.GAP, head("THE BASKETS", "The month's sale, in baskets: what the households "
                        + "needed, a basket a head; what they asked for at the shelf price; what the shops could hand over "
                        + "- their reach at the operating rate, or the shelf if it ran out first; and what they sold.", null),
                scaleRows(rows, Math.max(1, most), List.of(), 150, 80, 10),
                caption(w.limit(), Palette.TEXT_LABEL));
        if (w.limitDoor() != null) {
            String where = w.limitDoor();
            right.getChildren().add("Cash & debt".equals(where)
                    ? doorPill("Its cash & debt", Icons.COIN, Palette.BUSINESS, () -> open("Cash & debt"))
                    : doorPill("Build · " + where, Icons.BUILD, Palette.BUILDING, () -> ui.buildScreen.openCategory(where)));
        }
        if (w.creditLine() != null) right.getChildren().add(caption(w.creditLine(), Palette.TEXT_LABEL));
        left.setMinWidth(0);
        right.setMinWidth(0);
        left.setPrefWidth(560);
        right.setPrefWidth(560);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        HBox both = new HBox(24, left, right);
        return card(head("THE SHELF", null, null), both);
    }

    /* ------------------------- WHAT IT CHARGES (0.7.45) -------------------------
       The UI spec's 2.6: a kitchen's or a counter's margin, sticky since
       0.7.43 - what it charges against what it aims at, between its floor and
       its ceiling. No verdict colours. */

    /** WHAT IT CHARGES' words and figures, worked out without drawing them. */
    record ChargesWords(double floor, double ceiling, double charged, double target, String line, String served, String info) { }

    /**
     * "Served nothing" when a month counted a reach or a want and served none
     * of it; a reload counts none of the three until its month runs (they are
     * the month's flows, not saved), and that is not a month that served nothing.
     */
    static String served(double served, double reach, double wanted) {
        return served <= 0 && (reach > 0 || wanted > 0) ? "served nothing this month: the index holds its last price" : null;
    }

    static ChargesWords chargesWords(Restaurants k) {
        return new ChargesWords(Restaurants.MARGIN_FLOOR, Restaurants.MARGIN_CEILING, k.getMargin(), k.getTargetMargin(),
                String.format("charges %.2f× the food in a plate · aiming at %.2f× · moves %s of the way a month",
                        k.getMargin(), k.getTargetMargin(), wayWords(Restaurants.MARGIN_SPEED)),
                served(k.getServed(), k.getSeats(), k.getWanted()),
                "What a meal costs over the food in it. The kitchens aim higher the fuller their seats, between "
                + String.format("%.0f× and %.0f×", Restaurants.MARGIN_FLOOR, Restaurants.MARGIN_CEILING)
                + ", and the menu moves a sixth of the way there a month: a price that sticks, as a menu's does.");
    }

    static ChargesWords chargesWords(LuxuryRetail l) {
        return new ChargesWords(LuxuryRetail.MARGIN_FLOOR, LuxuryRetail.MARGIN_CEILING, l.getMargin(), l.getTargetMargin(),
                String.format("charges %.2f× what a piece lands at · aiming at %.2f× · moves %s of the way a month",
                        l.getMargin(), l.getTargetMargin(), wayWords(LuxuryRetail.MARGIN_SPEED)),
                served(l.getServed(), l.getCoverage(), l.getWanted()),
                "What a piece costs over what it lands at. The counters aim higher the more is wanted against what they "
                + String.format("can serve, between %.2f× and %.0f×", LuxuryRetail.MARGIN_FLOOR, LuxuryRetail.MARGIN_CEILING)
                + ", and the price moves a sixth of the way there a month.");
    }

    /** WHAT IT CHARGES: the margin as a run from its floor to its ceiling, the target a dashed rule. */
    VBox marginCard(ChargesWords w) {
        double span = Math.max(1e-9, w.ceiling() - w.floor());
        List<Rule> rules = List.of(
                new Rule(0, Palette.TEXT_MUTED, false, String.format("%.2f×", w.floor()), "The least it charges", null),
                new Rule(Math.max(0, Math.min(span, w.target() - w.floor())), Palette.TEXT_LABEL, true,
                        String.format("aims at %.2f×", w.target()), "What it aims at", null),
                new Rule(span, Palette.TEXT_MUTED, false, String.format("%.0f×", w.ceiling()), "The most it charges", null));
        ScaleRow row = ScaleRow.of("charged", String.format("%.2f×", w.charged()),
                List.of(Run.of(0, Math.max(0, Math.min(span, w.charged() - w.floor())), Palette.BUSINESS)));
        VBox c = card(head("WHAT IT CHARGES", w.info(), null), scaleRows(List.of(row), span, rules, 80, 70, 12),
                caption(w.line(), Palette.TEXT_LABEL));
        if (w.served() != null) c.getChildren().add(caption(w.served(), Palette.TEXT_MUTED));
        return c;
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

    /** The railway's work against its track, off Build's note (RAIL {the city's trade in tonnes, the network's capacity}); null for anyone else. Before a month has run since a load the tonnes are "not counted yet" (SectorFlow's counted, the page's rule for a month not run; the month's trade itself crosses a save since 0.7.46, A1). */
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
