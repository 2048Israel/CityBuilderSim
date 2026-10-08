package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
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
 * The infrastructure tab: the roads, the trams, the railway and the freight -
 * four pages under the five figures the whole tab is about, each page led by
 * the one picture that answers its question.
 *
 * WHY its own class (0.7.28): it shared ServicesScreen with the Services tab
 * from the 2026-09-18 split, two rail tabs in one file of 2,550 lines, and the
 * two are redrawn one after the other - so the INFRASTRUCTURE banner and its
 * four pages moved here verbatim first. Redrawn in 0.7.29 in Build's style
 * (the project's spec-infra-0729.md): each page was a 560 px statement column
 * of label-and-figure rows, two band meters, three grids and two dozen grey
 * paragraphs. The pictures lead now - the flow curve with the city's dot on
 * it beside the walk from the trips the city makes to what is on its road; a
 * funnel through transit's three ceilings; the month's freight bill split
 * three ways; a bar a good - the old statements and grids sit behind
 * "details" and the paragraphs behind an (i). Infrastructure explains and
 * Build acts: every page has a door to Build's Roads & transit, and Build's
 * rings have one back. The rail's Infrastructure tab opens
 * showInfrastructureMenu(); open() is every other door in (infraPage).
 */
final class InfrastructureScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    InfrastructureScreen(UserInterface ui) { this.ui = ui; }

    /** The tab opened on one of its pages, at the top of it (0.7.28): Services' road card, Build's road and transit doors, a figure or a part of a picture on this tab. */
    void open(String page) {
        infraPage = page;
        ui.innerScrollAt.remove("showInfrastructureMenu:body");
        showInfrastructureMenu();
    }

    /* =====================================================================
       INFRASTRUCTURE - the roads, the trams, the railway and the freight

       Jerus, 2026-09-17: "add a tab called infrastruture or transporation and
       basically it shows all the info about transportation and buses, and even
       a sub tab for specific info about the rail sector system, also in the bus
       section you can modify the price."

       NINE BATCHES SHIPPED BEFORE THIS SCREEN EXISTED, and the gap between what
       the model knew and what a player could see had got embarrassing. The road
       carried three streams and showed one number. The fare was a real dial
       with a real elasticity and NOTHING IN THE GAME COULD TURN IT. The railway
       repriced itself every month against a rule about its own capital and said
       so to nobody. A mill running at 65% because it had not taken delivery of
       its lorries yet looked, on every screen in the game, exactly like a mill
       running at 65% for no reason at all.

       FOUR PAGES, AND EACH ONE ANSWERS A QUESTION A PLAYER ACTUALLY ASKS.
       Roads: why is my flow 56%, and what is on the road. Transit: how many
       ride, what stops more, and what would the fare do. The railway: is this
       business worth anything to me. Freight: what does moving it add to each
       good's price, and who has not got the lorries to do it.

       WHAT IS NOT HERE. Nothing on this screen builds anything - the roads,
       the buses and the track are ordered on the Build tab like everything
       else, because a second place to buy a building is a second place to
       maintain. This is the instrument panel; the controls are the fare, which
       is a policy and has no other home.

       ONE ROAD, ONE VERDICT, ONE WORDING (0.7.29). The road has two figures
       and both are right: how much of its traffic it SERVES (the capacity
       over the load the curve reads - since 0.7.41, Jerus's one rule for the
       gauges; how FULL, the same fraction the other way up, until then) and
       how much it lets through, its FLOW (1 up to free flow, then free flow
       over the use, floored). They are written as a pair in whole per cents
       - "62% served · 56% flow" - here, in the drawer, on Build, on Services'
       road card and in NEEDS YOU, and coloured by one judge: the one verdict
       on what it serves (CityNeeds.verdict(); NEEDS YOU's ROADS row's level,
       amber while road sites were on the way, until 0.7.41). Nothing else on
       this tab takes a verdict colour except the railway's and the lorries'
       own verdicts - sets or lorries short, a railway too big for its city
       or losing money, every sector's lorries in hand; the streams, the
       money and the modes are categories.
       ===================================================================== */

    /** The tab's four pages, in the strip's order; the first is the one it starts on, and falls back to for a page it does not know. */
    static final String[] INFRA_PAGES =
            {"Roads", "Transit", "The railway", "Freight"};

    /** Each page's icon on its chip (0.7.29): the road, the bus, the train and the lorry. */
    static final String[] INFRA_ICONS = {Icons.ROADS, Icons.BUS, Icons.RAIL, Icons.LORRY};

    String infraPage = "Roads";

    /** Which "details" folds are open, by page - kept while the game runs, not saved (0.7.29). */
    private final java.util.Set<String> detailsOpen = new java.util.HashSet<>();

    /** The page's scroller, for a part of a picture that scrolls the page to where it is decided (Transit's fare). */
    private javafx.scene.control.ScrollPane body;

    /** How much of the stage the fixed frame takes above the page's scroller: the head, the five figures with FLOW's change, the pages, and their gaps (0.7.29). */
    static final double FRAME_CHROME = 232;

    /** The flow curve's x scale: what the road serves, from nothing to this (0.7.41; its use, to 260%, until then), fixed so the dot's motion reads month to month; the floor ends at MIN_THROUGHPUT / FREE_FLOW, 39%, and a road serving more sits at the edge with a "›". */
    static final double CURVE_MAX = 2.0;

    /** The three streams' colours - categories, never verdicts: commuters the people teal, goods the business violet, bulk the ore sand. */
    static String streamColour(Traffic s) {
        switch (s) {
            case GOODS: return Palette.BUSINESS;
            case BULK:  return Palette.ORE;
            default:    return Palette.PEOPLE;
        }
    }

    /** ...and their icons: a person, a shop, a plant. */
    static String streamIcon(Traffic s) {
        switch (s) {
            case GOODS: return Icons.SHOPS;
            case BULK:  return Icons.INDUSTRY;
            default:    return Icons.STAFF;
        }
    }

    /** The page that relieves a stream: transit for the commuters, the railway for the freight. */
    String reliefPage(Traffic s) { return s == Traffic.COMMUTERS ? "Transit" : "The railway"; }

    void showInfrastructureMenu() {
        ui.clearMenu("showInfrastructureMenu", () -> showInfrastructureMenu());

        boolean known = false;
        for (String page : INFRA_PAGES) if (page.equals(infraPage)) known = true;
        if (!known) infraPage = INFRA_PAGES[0];

        List<CityNeeds.Need> all = CityNeeds.measure(ui.game, SummaryScreen.WORDS);

        VBox page = widePage();
        switch (infraPage) {
            case "Transit"     -> transitPage(page);
            case "The railway" -> railwayPage(page);
            case "Freight"     -> freightPage(page);
            default            -> roadsPage(page, all);
        }

        javafx.scene.layout.FlowPane strip =
                chipStrip(INFRA_PAGES, INFRA_ICONS, infraPage, Palette.SIZE_LABEL, name -> open(name));

        VBox frame = new VBox(Palette.GAP, head(), vitals(all), strip);
        frame.setMaxWidth(PAGE_WIDE);
        frame.setFillWidth(true);
        frame.setPadding(new javafx.geometry.Insets(0, 18, 4, 18));

        // THE FRAME DOES NOT SCROLL (as Services' since 0.7.28): the head, the
        // five figures and the pages stay put; only the page under them moves.
        body = ui.scrolled(page, FRAME_CHROME);
        final javafx.scene.control.ScrollPane scroller = body;
        page.prefWidthProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                () -> Math.min(PAGE_WIDE, Math.max(320, scroller.getViewportBounds().getWidth())),
                scroller.viewportBoundsProperty()));
        ui.rootMenu.getChildren().addAll(frame, body);
    }

    /**
     * The head: "Infrastructure" with the people teal's swatch, and at its
     * right the two doors every page has - Build's Roads & transit, opened on
     * the ring this page explains (transit's on Transit, the road's
     * elsewhere), in the building pink; and the road over the years, City
     * History on its throughput line.
     */
    HBox head() {
        BuildAdvice.Measure m = BuildAdvice.Measure.of(
                "Transit".equals(infraPage) ? BuildAdvice.Kind.TRANSIT : BuildAdvice.Kind.ROADS);
        return pageHead("Infrastructure", Palette.PEOPLE,
                doorPill("Build · Roads & transit", Icons.BUILD, Palette.BUILDING, () -> ui.buildScreen.openOn(m)),
                door("The road over the years", Palette.ACCENT, () -> ui.historyScreen.openOn("roadRatio")));
    }

    /* ---------------------------------------------------------------------
       THE FIVE FIGURES (0.7.29; four until then)

       Both road figures first - SERVED (FULL until 0.7.41) was the Roads
       page's meter and is the figure NEEDS YOU's row reads; FLOW is the one
       that multiplies every business - in the road's verdict colour, the
       only verdict in the strip. Then who rides,
       who drives and what the railway carries, plain: no line judges them,
       and ON TRANSIT was green as a category. Each opens its page; FLOW
       carries its change on last month, from History's road throughput.
       --------------------------------------------------------------------- */

    HBox vitals(List<CityNeeds.Need> all) {

        InfrastructureManager roads = ui.game.getInfrastructureManager();
        ham.citybuildersim.sectors.Rail rail = ui.game.getSectors().rail();
        CityNeeds.Served served = roadServed(roads);
        String tone = BuildScreen.servedTone(served);

        double commuters = roads.getLoad(Traffic.COMMUTERS);
        double owned = roads.getCarOwnership();
        // In today's money (0.7.45; the UI spec's B7, D13): what a rider is charged, the dial at the struck level.
        double fare = ui.game.getEconomyManager().getTaxPolicy().chargedFare();

        VBox full = limitCell("SERVED", CityNeeds.servedPct(served.share()),
                BuildScreen.servedWords(served) + ": its capacity over the trips on it", tone,
                "The road: how much of its traffic it serves, and what it lets through", () -> open("Roads"));
        VBox flow = limitCell("FLOW", pct(roads.getThroughputRatio()), flowWords(roads), tone,
                "The road: what every business gets through it", () -> open("Roads"));
        String change = flowChange();
        if (change != null) {
            Label c = new Label(change);
            c.setWrapText(true);
            c.setMaxWidth(LIMIT_CELL - 28);
            c.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            flow.getChildren().add(c);
        }
        VBox riding = limitCell("ON TRANSIT", pct(commuters > 0 ? roads.getTransitRiders() / commuters : 0),
                roads.getTransitCapacity() <= 0 ? "nothing built"
                        : fare <= 0 ? "of commuters · riding free" : "of commuters · at " + unitPrice(fare) + " a ride",
                roads.getTransitCapacity() <= 0 ? Palette.TEXT_MUTED : Palette.TEXT_HEAD,
                "Transit: who rides, and the fare", () -> open("Transit"));
        VBox cars = limitCell("CARS", pct(owned),
                owned <= 0 ? "nobody drives" : String.format("per household · %.2f× on the commute", roads.carRoadFactor()),
                Palette.TEXT_HEAD, "The road: what the cars add to it", () -> open("Roads"));
        VBox byRail = limitCell("BY RAIL", pct(roads.getRailShare(Traffic.BULK)),
                rail.trackTonnes() <= 0 ? "no track"
                        : "of bulk · quoting " + pct(rail.getQuote()) + " of a lorry",
                rail.trackTonnes() <= 0 ? Palette.TEXT_MUTED : Palette.TEXT_HEAD,
                "The railway", () -> open("The railway"));
        return vitalsBar(full, flow, riding, cars, byRail);
    }

    /** FLOW's note: the road's status in a word, with what it costs once it bites. */
    static String flowWords(InfrastructureManager roads) {
        if (roads.isCongested()) return "congested: output at " + pct(roads.getThroughputRatio());
        return roads.isStrained() ? "busy: about to slow" : "clear";
    }

    /** FLOW's change on last month, from History's road throughput (its last two months kept): "▼ 0.6 pts on last month"; null with fewer than two. */
    String flowChange() {
        double[] s = ui.game.getHistorySave().aligned("roadRatio");
        double last = Double.NaN, before = Double.NaN;
        for (int i = s.length - 1; i >= 0; i--) {
            if (!Double.isFinite(s[i])) continue;
            if (Double.isNaN(last)) last = s[i];
            else { before = s[i]; break; }
        }
        if (!Double.isFinite(last) || !Double.isFinite(before)) return null;
        double d = last - before;
        String shown = ServicesScreen.points(d);
        if (shown.equals(ServicesScreen.points(0))) return "no change on last month";
        return (d > 0 ? "▲ " : "▼ ") + shown + " on last month";
    }

    /** What the road serves and the one verdict on it (0.7.41) - the road's one judge, as every road gauge reads it; its colour was NEEDS YOU's ROADS row's level, amber while road sites were on the way, until then. */
    static CityNeeds.Served roadServed(InfrastructureManager roads) {
        return CityNeeds.verdict(CityNeeds.Kind.ROADS, CareType.NONE, roads.getServed());
    }

    /** A share as a whole per cent, as Build's rings write it. */
    static String pct(double share) { return BuildScreen.pct(share); }

    /** Money, or a dash for a figure the model does not know yet (the railway's allowed bill and its split, after loading a save from before 0.7.29, until a month runs). */
    static String moneyOr(double thousands) { return Double.isFinite(thousands) ? money(thousands) : "—"; }

    /** What a dash means, where one is shown. */
    static final String UNKNOWN_YET = "known after a month: the save predates 0.7.29";

    /* ----------------------------- the screen's own pieces ----------------------------- */

    /** Words that wrap, at a size, in a colour. */
    static Label words(String text, double size, String tone) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setStyle(BuildScreen.wordsAt(size, tone));
        return l;
    }

    /** A figure that is never cut, at a size, in a colour. */
    static Label figure(String text, double size, String tone) {
        Label l = new Label(text);
        l.setMinWidth(Region.USE_PREF_SIZE);
        l.setStyle(BuildScreen.figureAt(size, tone));
        return l;
    }

    /** A picture's caption: a few words in capitals, with an (i) after them when `info` is not null. */
    static HBox caption(String text, String info) {
        Label l = new Label(text);
        l.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        l.setMinWidth(Region.USE_PREF_SIZE);
        HBox row = new HBox(Palette.GAP, l);
        if (info != null) row.getChildren().add(infoButton(info, true));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** "62% served → 56% flow" ("162% full" until 0.7.41): the two figures at 28 px, the words between them smaller. */
    static javafx.scene.layout.FlowPane pairLine(String a, String aTone, String aWords, String b, String bTone, String bWords) {
        javafx.scene.layout.FlowPane line = new javafx.scene.layout.FlowPane(6, 0);
        line.setPrefWrapLength(280);
        line.setAlignment(Pos.BASELINE_LEFT);
        line.setRowValignment(javafx.geometry.VPos.BASELINE);
        line.getChildren().addAll(figure(a, 28, aTone), words(aWords, Palette.SIZE_BODY + 1, Palette.TEXT_LABEL));
        if (b != null) line.getChildren().addAll(figure(b, 28, bTone), words(bWords, Palette.SIZE_BODY + 1, Palette.TEXT_LABEL));
        return line;
    }

    /** A card's head: its icon in a square tinted in its colour, its name, and its (i). */
    static HBox cardHead(String svg, String colour, String name, String info) {
        Label n = new Label(name);
        n.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        n.setWrapText(true);
        HBox head = new HBox(8, iconSquare(svg, colour, 28, 15), n);
        if (info != null) head.getChildren().add(infoButton(info, true));
        head.setAlignment(Pos.CENTER_LEFT);
        return head;
    }

    /** A line of figures and words that wraps: "102k trips · 135k on the road". */
    static javafx.scene.layout.FlowPane figureLine(Object... parts) {
        javafx.scene.layout.FlowPane line = new javafx.scene.layout.FlowPane(4, 0);
        line.setRowValignment(javafx.geometry.VPos.BASELINE);
        for (int i = 0; i < parts.length; i++) {
            String p = String.valueOf(parts[i]);
            line.getChildren().add(i % 2 == 0 ? figure(p, Palette.SIZE_BODY + 3, Palette.TEXT_HEAD)
                                               : words(p, Palette.SIZE_BODY, Palette.TEXT_LABEL));
        }
        return line;
    }

    /** A statement column, for a fold: the old rows at their old width. */
    static VBox column(Node... rows) {
        VBox c = new VBox(0);
        for (Node n : rows) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(STATEMENT);
        return c;
    }

    /** Scroll the page to a node on it (Transit's "the fare" step, which opens the fare card). */
    void scrollTo(Node target) {
        if (body == null || target == null || body.getContent() == null) return;
        double contentH = body.getContent().getBoundsInLocal().getHeight();
        double viewH = body.getViewportBounds().getHeight();
        if (contentH <= viewH) return;
        javafx.geometry.Bounds at = target.localToScene(target.getBoundsInLocal());
        javafx.geometry.Bounds top = body.getContent().localToScene(body.getContent().getBoundsInLocal());
        if (at == null || top == null) return;
        double y = at.getMinY() - top.getMinY() - 12;
        body.setVvalue(Math.max(0, Math.min(1, y / (contentH - viewH))));
    }

    /* =====================================================================
       ROADS (0.7.29)

       The page's question: why is my flow 56%, and what is on the road? The
       curve that links the two road figures, with the city's dot on it,
       beside the walk from the trips the city makes to what reaches its road
       - the three streams stacked, less what transit carries, plus what the
       cars add, less what the railway and the highways take off, and the
       three streams again - with the capacity and the free-flow line through
       every row. Then a card a stream, the network in four figures, and the
       old statement behind "details".
       ===================================================================== */

    void roadsPage(VBox page, List<CityNeeds.Need> all) {
        InfrastructureManager roads = ui.game.getInfrastructureManager();
        InfrastructureManager.RoadBreakdown walk = roads.roadBreakdown();

        page.getChildren().add(heroCard(roadHeroLeft(roads, all), 340, roadWalk(roads, walk)));
        page.getChildren().add(sectionHead("WHAT IS ON IT", null, hint("a card opens the page that takes it off the road")));
        page.getChildren().add(streamCards(roads, walk));
        page.getChildren().add(networkStrip(roads));
        page.getChildren().add(details("roads", "the network, the streams and the cars, line by line", detailsOpen,
                ui::redraw, () -> roadsStatement(roads, walk)));
    }

    /** The hero's left: THE ROAD, both figures as a pair, what the flow costs, the status chip, the curve and what is on site. */
    VBox roadHeroLeft(InfrastructureManager roads, List<CityNeeds.Need> all) {
        CityNeeds.Served served = roadServed(roads);
        String tone = BuildScreen.servedTone(served);
        double flow = roads.getThroughputRatio();

        VBox left = new VBox(6, caption("THE ROAD", null),
                pairLine(CityNeeds.servedPct(served.share()), tone, BuildScreen.servedWords(served) + "  →",
                        pct(flow), Palette.TEXT_HEAD, "flow"));
        left.getChildren().add(infoLine(flow < 1
                        ? "every shop, plant and site works at " + pct(flow) + " of its output"
                        : "flowing freely: every shop, plant and site works at its full output",
                FLOW_INFO + (roads.isCongested() ? "\n\n" + congestedWords(flow) : ""), true,
                Palette.SIZE_BODY, Palette.TEXT_LABEL, 340));
        if (!"Clear".equals(roads.getStatus())) left.getChildren().add(chip(roads.getStatus().toUpperCase(), tone));

        // The hollow dot: where the road sites on site would leave it, by Build's own figure - served (0.7.41).
        BuildAdvice.Measure m = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        BuildScreen.RingWords ring = ui.buildScreen.ringWords(m, all);
        double then = ring.units() > 0 ? BuildAdvice.served(ui.game, m, BuildAdvice.onSite(ui.game, m)) : Double.NaN;
        left.getChildren().add(new FlowCurve(served.share(), then, tone));
        if (ring.units() > 0) {
            HBox site = new HBox(6, icon(Icons.CRANE, Palette.BUILDING, 13),
                    words(ring.onSite() + " · the hollow dot is where they leave it", Palette.SIZE_LABEL, Palette.BUILDING));
            site.setAlignment(Pos.CENTER_LEFT);
            left.getChildren().add(site);
        }
        return left;
    }

    /** The flow's (i): the old page's paragraph under its big figure, said in served since 0.7.41. */
    static final String FLOW_INFO = String.format("Every business in the city multiplies its output by its "
            + "flow, and so does construction. It is 100%% while the road serves %.0f%% of its traffic or more - "
            + "free flow ends at %.0f%% of its capacity - and then falls away - traffic does not degrade in a "
            + "straight line, which is why a road that coped last month can "
            + "gridlock after one more office opens. It never falls under %.0f%%: a gridlocked city still moves. "
            + "A business loses less of it while the city rides transit: a commuter on a tram is not in the jam.\n\n"
            + "What the road serves and how much it lets through are two measures of one state: under that line "
            + "the flow is %.0f%% of what it serves. The curve under this is the model's own.",
            CityNeeds.servedLines(CityNeeds.Kind.ROADS, CareType.NONE)[1] * 100,
            InfrastructureManager.FREE_FLOW * 100, InfrastructureManager.MIN_THROUGHPUT * 100,
            InfrastructureManager.FREE_FLOW * 100);

    /** The congested alert's words (P5), now the (i)'s second half. */
    static String congestedWords(double flow) {
        return String.format("Everything standing here is producing %.0f%% of what it "
                + "could. Build streets, build a highway if the load is freight, or "
                + "build transit if it is people - and look at the mix beside the curve before "
                + "deciding which.", flow * 100);
    }

    /** The hero's right: FROM TRIPS TO THE ROAD, five rows on one scale with the capacity and the free-flow line through them. */
    VBox roadWalk(InfrastructureManager roads, InfrastructureManager.RoadBreakdown walk) {
        double raw = walk.rawTotal(), effective = walk.effective(), cap = roads.getCapacity();
        // Where each step starts and ends on the bar: the running total, for drawing only - every figure printed is the model's.
        double afterTransit = raw - walk.transitOff(), afterCars = afterTransit + walk.carsAdd();
        double scale = Math.max(Math.max(raw, afterCars), Math.max(effective, cap)) * 1.05;
        int g = Traffic.GOODS.ordinal(), b = Traffic.BULK.ordinal();

        List<ScaleRow> rows = new ArrayList<>();
        rows.add(ScaleRow.of("Trips the city makes", people(raw), streams(walk.raw())).strong()
                .tip("The trips everything standing makes, by stream, before anything is taken off or added."));
        rows.add(ScaleRow.of("− on transit", "−" + people(walk.transitOff()),
                        List.of(Run.of(afterTransit, walk.transitOff(), Palette.PEOPLE).outlined()
                                .tip("Commuters on a tram or a bus: " + people(walk.transitOff()) + " trips off the road")
                                .go(() -> open("Transit"))))
                .empty(roads.getTransitCapacity() <= 0 ? "no transit yet" : "nobody rides")
                .go(() -> open("Transit")));
        rows.add(ScaleRow.of("+ the cars", "+" + people(walk.carsAdd()),
                        List.of(Run.of(afterTransit, walk.carsAdd(), Palette.PEOPLE_LIGHT)
                                .tip(String.format("%s commuters still driving, at %.2f trips of road each"
                                        + "\nwithout cars the road would carry %s", people(walk.driving()),
                                        walk.carFactor(), people(walk.withoutCars())))
                                .go(() -> open("Transit"))))
                .empty("nobody owns a car yet").info(roads.getCarOwnership() > 0 ? CARS_INFO : NO_CARS_INFO)
                .go(() -> open("Transit")));
        rows.add(ScaleRow.of("− rail and highways", "−" + people(walk.freightOffTotal()),
                        List.of(Run.of(afterCars - walk.freightOff()[b] - walk.freightOff()[g], walk.freightOff()[g], Palette.BUSINESS)
                                        .outlined().tip("Goods taken off the road: " + people(walk.freightOff()[g]) + " trips")
                                        .go(() -> open("The railway")),
                                Run.of(afterCars - walk.freightOff()[b], walk.freightOff()[b], Palette.ORE)
                                        .outlined().tip("Bulk taken off the road: " + people(walk.freightOff()[b]) + " trips")
                                        .go(() -> open("The railway"))))
                .empty("no rail or highways yet").go(() -> open("The railway")));
        rows.add(ScaleRow.of("On the road", people(effective), streams(walk.onRoad())).strong()
                .tip("The load the curve reads: " + people(effective) + " trips against a capacity of " + people(cap)));

        List<Rule> rules = List.of(
                new Rule(cap, Palette.TEXT_HEAD, false, "capacity " + shortNumber(cap),
                        String.format("Capacity %s trips a month: %s built and %s always there.\nOpens Build's roads.",
                                people(cap), people(roads.getBuiltCapacity()), people(InfrastructureManager.BASE_CAPACITY)),
                        () -> ui.buildScreen.openOn(BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS))),
                new Rule(roads.getFreeFlowLoad(), Palette.TEXT_MUTED, true, "slows past " + shortNumber(roads.getFreeFlowLoad()),
                        "Past " + pct(InfrastructureManager.FREE_FLOW) + " of capacity the traffic slows, and the flow falls", null));

        VBox box = new VBox(8, caption("FROM TRIPS TO THE ROAD", WALK_INFO),
                scaleRows(rows, scale, rules, 170, 84, 14));
        HBox key = new HBox(Palette.GAP_LOOSE);
        for (Traffic s : Traffic.values()) key.getChildren().add(keySwatch(streamColour(s), s.label().toLowerCase()));
        key.getChildren().add(keySwatch(Palette.PEOPLE_LIGHT, "what the cars add"));
        Label hollow = new Label("hollow: taken off");
        hollow.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        key.getChildren().add(hollow);
        key.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().add(key);
        return box;
    }

    /** The three streams laid end to end from nothing, each a door to the page that relieves it. */
    List<Run> streams(double[] amounts) {
        List<Run> runs = new ArrayList<>();
        double at = 0;
        for (Traffic s : Traffic.values()) {
            double a = amounts[s.ordinal()];
            runs.add(Run.of(at, a, streamColour(s)).tip(s.label() + ": " + people(a) + " trips")
                    .go(() -> open(reliefPage(s))));
            at += a;
        }
        return runs;
    }

    /** The walk's (i). */
    static final String WALK_INFO = "Every building makes trips - its staff to and from work, its goods and its "
            + "bulk in and out - and that is the first row, by stream. Transit takes commuters off the road; the "
            + "cars add road for every commuter still driving; the railway and the highways take part of the "
            + "freight off. What is left is the last row, the load the curve reads. The solid line is the "
            + "capacity, the dashed one where the traffic starts to slow.";

    /** The car row's (i): the old page's car paragraph (P4). */
    static final String CARS_INFO = String.format(
            "A fully motorised city asks %.1f times the commuter road a city where "
            + "nobody drives does, and it only lands on the people who did not get "
            + "on a tram. The two answers are more tarmac and more transit, and "
            + "transit is the one that also stops the next household buying a car.",
            InfrastructureManager.CAR_LOAD_AT_SATURATION);

    /** ...and with nobody driving (P3). */
    static final String NO_CARS_INFO = "Nobody in this city owns one yet, so a commuter costs the road exactly "
            + "one trip and this network is the network it has always been. That "
            + "changes on its own as households get money past their cushion.";

    /** The stream cards' "costs" (P2, rewritten: a commuter is not always one). */
    static final String COSTS_INFO = "\"Costs\" is what one trip of this kind asks of the street once it is on "
            + "it: a lorry's after the highways and the railway have taken their share, a commuter's after the "
            + "cars - a commuter who drives asks the car factor in trips of road, one on a tram none, so the "
            + "commuters' figure is the two blended. \"Gets through\" differs between streams only when somebody "
            + "is on a tram: a lorry in a jam is in a jam, and a clerk on a metro is not.";

    /** A card a stream: what it makes and puts on the road, what gets through, what a trip costs, and what relieves it. */
    javafx.scene.layout.GridPane streamCards(InfrastructureManager roads, InfrastructureManager.RoadBreakdown walk) {
        javafx.scene.layout.GridPane grid = equalColumns(3, TILE_GAP);
        int col = 0;
        for (Traffic s : Traffic.values()) {
            int i = s.ordinal();
            String colour = streamColour(s);
            VBox card = ServicesScreen.card(cardHead(streamIcon(s), colour, s.label(), null),
                    figureLine(shortNumber(walk.raw()[i]), "trips made ·", shortNumber(walk.onRoad()[i]), "on the road"));
            double gets = roads.throughputOf(s);
            SegmentBar bar = segmentBar(List.of(Segment.of(gets, colour)), 1, List.of(), 0, 8);
            card.getChildren().add(new VBox(3, bar, words("gets through " + pct(gets), Palette.SIZE_LABEL, Palette.TEXT_LABEL)));
            card.getChildren().add(infoLine(String.format("costs the road %.2f× a trip", walk.costPerTrip(s)),
                    COSTS_INFO, true, Palette.SIZE_BODY, Palette.TEXT_HEAD, 400));
            String relief;
            if (s == Traffic.COMMUTERS) {
                relief = (walk.transitOff() > 0 ? shortNumber(walk.transitOff()) + " ride transit" : "nobody rides transit")
                        + " · " + (walk.carsAdd() > 0 ? "cars add " + shortNumber(walk.carsAdd()) : "nobody drives a car");
            } else {
                relief = (roads.getRailShare(s) > 0 ? "rail carries " + pct(roads.getRailShare(s)) : "no rail")
                        + " · rail and highways take " + shortNumber(walk.freightOff()[i]) + " trips off";
            }
            card.getChildren().add(words(relief, Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            ServicesScreen.doorCard(card, () -> open(reliefPage(s)),
                    s == Traffic.COMMUTERS ? "Transit: who rides, and what the cars cost" : "The railway: what it carries");
            grid.add(card, col++, 0);
        }
        return grid;
    }

    /** The network in four figures: what it can carry, the room before it slows (on the load the curve reads), the lorry share, the cars. */
    HBox networkStrip(InfrastructureManager roads) {
        double owned = roads.getCarOwnership();
        HBox strip = vitalsBar(
                limitCell("CAPACITY", people(roads.getCapacity()),
                        "trips a month · " + people(roads.getBuiltCapacity()) + " built + "
                        + people(InfrastructureManager.BASE_CAPACITY) + " always there", Palette.TEXT_HEAD),
                limitCell("ROOM BEFORE IT SLOWS", people(roads.getHeadroom()),
                        roads.getHeadroom() > 0 ? "trips, on the load the curve reads" : "none: past the free-flow line",
                        Palette.TEXT_HEAD),
                limitCell("BUILT FOR LORRIES", pct(roads.getHighwayShare()), "of the network is grade-separated",
                        Palette.TEXT_HEAD),
                limitCell("CARS PER HOUSEHOLD", pct(owned),
                        owned <= 0 ? "nobody drives" : String.format("a driver asks %.2f trips of road", roads.carRoadFactor()),
                        Palette.TEXT_HEAD));
        strip.setAlignment(Pos.CENTER_LEFT);
        return strip;
    }

    /** The old Roads page, line by line, in its fold: the network, the streams' grid and the cars (B3's room and B6's costs fixed). */
    VBox roadsStatement(InfrastructureManager roads, InfrastructureManager.RoadBreakdown walk) {
        double capacity = roads.getCapacity(), effective = walk.effective();
        VBox c = column(statementHead("The network"),
                statementLine("Streets the city has built", String.format("%,.0f trips", roads.getBuiltCapacity())),
                statementLine("...and the ones that were always there",
                        String.format("%,.0f trips", InfrastructureManager.BASE_CAPACITY), Palette.TEXT_MUTED),
                statementTotal("Capacity", String.format("%,.0f trips a month", capacity), Palette.TEXT_HEAD),
                statementLine("Trips everything standing generates", String.format("%,.0f", roads.getLoad()), Palette.TEXT_MUTED),
                statementLine(roads.getLoad() > effective + 1
                                ? "...and what is left for the road, after transit, cars, highways and rail"
                                : "What is actually being asked of it",
                        String.format("%,.0f", effective)),
                statementLine("Room before it starts to slow", String.format("%,.0f", roads.getHeadroom())),
                statementLine("Of it built for lorries", String.format("%.0f%%", roads.getHighwayShare() * 100),
                        Palette.TEXT_MUTED),
                statementHead("What is on it"));
        javafx.scene.layout.GridPane mix = grid(new double[] {150, 100, 70, 100, 100}, rightAfterFirst(5));
        gridHead(mix, "", "trips", "share", "costs a trip", "gets through");
        int line = 1;
        for (Traffic stream : Traffic.values()) {
            mix.add(gridCell(stream.label(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            mix.add(gridCell(String.format("%,.0f", roads.getLoad(stream)), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, line);
            mix.add(gridCell(String.format("%.0f%%", roads.getShareOf(stream) * 100), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 2, line);
            mix.add(gridCell(String.format("%.2fx", walk.costPerTrip(stream)), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 3, line);
            mix.add(gridCell(String.format("%.0f%%", roads.throughputOf(stream) * 100), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        c.getChildren().addAll(mix, statementNote(COSTS_INFO));

        double owned = roads.getCarOwnership();
        c.getChildren().add(statementHead("And the cars"));
        if (owned <= 0) {
            c.getChildren().add(statementNote(NO_CARS_INFO));
        } else {
            c.getChildren().addAll(
                    statementLine("Cars per household", String.format("%.0f%%", owned * 100)),
                    statementLine("What one driving commuter now costs the road", String.format("%.2f trips", walk.carFactor())),
                    statementLine("Commuters still driving",
                            String.format("%,.0f of %,.0f", walk.driving(), walk.raw()[Traffic.COMMUTERS.ordinal()])),
                    statementLine("...costing the road", String.format("%,.0f trips", walk.onRoad()[Traffic.COMMUTERS.ordinal()])),
                    statementLine("Commuters on a tram", String.format("%,.0f", walk.transitOff())),
                    statementTotal("What the road would carry with nobody driving",
                            String.format("%,.0f trips", walk.withoutCars()), Palette.TEXT_HEAD),
                    statementNote(CARS_INFO));
        }
        return c;
    }

    /**
     * The flow curve (0.7.29): the road's flow against what it serves (0.7.41;
     * against how full it was until then), from nothing to CURVE_MAX - the
     * model's own curve (InfrastructureManager.throughputAtServed()) as a line
     * over the one verdict's three bands along its foot (red at or under
     * 1/FREE_FLOW served, amber to 1/STRAINED, green past it - NEEDS YOU's
     * lines read the other way up), so better is to the right - the city's
     * dot on it with its two figures dropped to the axes, and, with road sites
     * on site, a hollow pink dot where they would leave it. A dot past the
     * scale sits at its edge with a "›".
     */
    static final class FlowCurve extends javafx.scene.layout.Pane {
        /** The plot's size, and the room for the axes' figures at its left and under it. */
        private static final double W = 270, H = 104, LEFT = 38, TOP = 10, BOTTOM = 18, RIGHT = 16, DOT = 5;
        private final Label useLabel, flowLabel, xLow, xHigh, yHigh, yLow, over;
        /** Where the dot's drops meet the axes. */
        private final double dotX, dotY;

        FlowCurve(double served, double then, String tone) {
            double u = Double.isNaN(served) ? 0 : Math.max(0, served);
            double f = InfrastructureManager.throughputAtServed(u);
            double span = 1 - InfrastructureManager.MIN_THROUGHPUT;
            double[] lines = CityNeeds.servedLines(CityNeeds.Kind.ROADS, CareType.NONE);

            javafx.scene.shape.Line xAxis = new javafx.scene.shape.Line(LEFT, TOP + H + .5, LEFT + W, TOP + H + .5);
            javafx.scene.shape.Line yAxis = new javafx.scene.shape.Line(LEFT - .5, TOP, LEFT - .5, TOP + H);
            for (javafx.scene.shape.Line l : new javafx.scene.shape.Line[] {xAxis, yAxis}) {
                l.setStroke(javafx.scene.paint.Color.web(Palette.EDGE));
            }
            getChildren().addAll(xAxis, yAxis);

            // The one verdict's lines along the foot (0.7.41; NEEDS YOU's on the load, as the band meter drew them, since 0.30).
            double[] cuts = {0, lines[1], lines[0], CURVE_MAX};
            String[] tones = {Palette.BAD, Palette.WARN, Palette.GOOD};
            for (int k = 0; k < 3; k++) {
                Region band = new Region();
                band.setStyle("-fx-background-color: " + tint(tones[k], .30) + ";");
                band.relocate(x(cuts[k]), TOP + H - 5);
                band.setPrefSize(x(cuts[k + 1]) - x(cuts[k]), 5);
                band.resize(x(cuts[k + 1]) - x(cuts[k]), 5);
                Tooltip.install(band, new Tooltip(k == 0 ? CityNeeds.servedPct(lines[1]) + " " + CityNeeds.SERVED
                                   + " or less: congested, the flow falls"
                        : k == 1 ? CityNeeds.servedPct(lines[1]) + " to " + CityNeeds.servedPct(lines[0])
                                   + ": NEEDS YOU lists the road"
                        : "past " + CityNeeds.servedPct(lines[0]) + ": clear"));
                getChildren().add(band);
            }

            javafx.scene.shape.Polyline curve = new javafx.scene.shape.Polyline();
            int n = 130;
            for (int k = 0; k <= n; k++) {
                double at = CURVE_MAX * k / n;
                // The knee where free flow ends, drawn on the line rather than between two samples.
                if (k > 0 && CURVE_MAX * (k - 1) / n < lines[1] && at > lines[1]) {
                    curve.getPoints().addAll(x(lines[1]), y(1, span));
                }
                curve.getPoints().addAll(x(at), y(InfrastructureManager.throughputAtServed(at), span));
            }
            curve.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_LABEL));
            curve.setStrokeWidth(2);
            curve.setFill(null);
            curve.setMouseTransparent(true);
            getChildren().add(curve);

            double dx = x(Math.min(u, CURVE_MAX)), dy = y(f, span);
            dotX = dx;
            dotY = dy;
            javafx.scene.shape.Line down = new javafx.scene.shape.Line(dx, dy, dx, TOP + H);
            javafx.scene.shape.Line across = new javafx.scene.shape.Line(LEFT, dy, dx, dy);
            for (javafx.scene.shape.Line l : new javafx.scene.shape.Line[] {down, across}) {
                l.setStroke(javafx.scene.paint.Color.web(Palette.TEXT_MUTED));
                l.getStrokeDashArray().addAll(3.0, 3.0);
                l.setMouseTransparent(true);
            }
            getChildren().addAll(down, across);

            if (!Double.isNaN(then) && Math.abs(Math.min(then, CURVE_MAX) - Math.min(u, CURVE_MAX)) > .005) {
                double t = Math.max(0, then);
                javafx.scene.shape.Circle hollow = new javafx.scene.shape.Circle(x(Math.min(t, CURVE_MAX)),
                        y(InfrastructureManager.throughputAtServed(t), span), DOT);
                hollow.setFill(null);
                hollow.setStroke(javafx.scene.paint.Color.web(Palette.BUILDING));
                hollow.setStrokeWidth(1.5);
                Tooltip.install(hollow, new Tooltip("With the road sites on site finished: " + CityNeeds.servedPct(t) + " "
                        + CityNeeds.SERVED + " · " + pct(InfrastructureManager.throughputAtServed(t)) + " flow"));
                getChildren().add(hollow);
            }

            javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(dx, dy, DOT);
            dot.setFill(javafx.scene.paint.Color.web(Palette.TEXT_HEAD));
            dot.setStroke(javafx.scene.paint.Color.web(tone));
            dot.setStrokeWidth(2);
            Tooltip.install(dot, new Tooltip("Today: " + CityNeeds.servedPct(u) + " " + CityNeeds.SERVED + " · " + pct(f) + " flow"));
            getChildren().add(dot);

            useLabel = label(CityNeeds.servedPct(u), Palette.TEXT_HEAD);
            flowLabel = label(pct(f), Palette.TEXT_HEAD);
            xLow = label("0%", Palette.TEXT_MUTED);
            xHigh = label(pct(CURVE_MAX), Palette.TEXT_MUTED);
            yHigh = label("100%", Palette.TEXT_MUTED);
            yLow = label(pct(InfrastructureManager.MIN_THROUGHPUT), Palette.TEXT_MUTED);
            over = label("›", Palette.TEXT_HEAD);
            over.setVisible(u > CURVE_MAX);
            getChildren().addAll(xLow, xHigh, yHigh, yLow, useLabel, flowLabel, over);

            double wide = LEFT + W + RIGHT, tall = TOP + H + BOTTOM;
            setMinSize(wide, tall);
            setPrefSize(wide, tall);
            setMaxSize(wide, tall);
        }

        private static Label label(String text, String tone) {
            Label l = new Label(text);
            l.setStyle(Palette.figure(Palette.SIZE_CAPTION, tone));
            l.setMinWidth(Region.USE_PREF_SIZE);
            l.setMouseTransparent(true);
            return l;
        }

        private static double x(double served) { return LEFT + W * Math.max(0, Math.min(CURVE_MAX, served)) / CURVE_MAX; }

        private static double y(double flow, double span) {
            return TOP + H * (1 - flow) / span;
        }

        @Override protected void layoutChildren() {
            double baseY = TOP + H + 3;
            // The dot's figures, where its drops meet the axes; the axes' own ends give way to them.
            double ux = dotX, fy = dotY;
            place(useLabel, ux - useLabel.prefWidth(-1) / 2, baseY);
            place(flowLabel, LEFT - 4 - flowLabel.prefWidth(-1), fy - flowLabel.prefHeight(-1) / 2);
            place(xLow, LEFT - xLow.prefWidth(-1) / 2, baseY);
            place(xHigh, LEFT + W - xHigh.prefWidth(-1) / 2, baseY);
            place(yHigh, LEFT - 4 - yHigh.prefWidth(-1), TOP - yHigh.prefHeight(-1) / 2);
            place(yLow, LEFT - 4 - yLow.prefWidth(-1), TOP + H - yLow.prefHeight(-1) / 2);
            xLow.setVisible(Math.abs(ux - LEFT) > 24);
            xHigh.setVisible(Math.abs(ux - (LEFT + W)) > 28);
            yHigh.setVisible(Math.abs(fy - TOP) > 10);
            yLow.setVisible(Math.abs(fy - (TOP + H)) > 10);
            place(over, LEFT + W + 6, fy - over.prefHeight(-1) / 2);
        }

        private static void place(Label l, double x, double y) {
            double w = l.prefWidth(-1), h = l.prefHeight(-1);
            l.resizeRelocate(Math.max(0, x), Math.max(0, y), w, h);
        }
    }

    /* =====================================================================
       TRANSIT - and the one control on this tab (0.7.29)

       The page's question: how many ride, what stops more, and what does the
       fare do? A funnel from the commuters through the three ceilings - the
       stock, the road under it, the share a city's lines reach - the lowest
       of which wins, then the riders by reason (0.7.49): the commuters with
       no car of their own, who ride at any fare if a line reaches them, and
       the owners, who weigh a ride against a journey's fuel. Then the books
       and the fare, side by side.
       ===================================================================== */

    void transitPage(VBox page) {

        InfrastructureManager roads = ui.game.getInfrastructureManager();
        EconomyManager econ = ui.game.getEconomyManager();
        TaxPolicy policy = econ.getTaxPolicy();
        InfrastructureManager.RoadBreakdown walk = roads.roadBreakdown();

        double commuters = roads.getLoad(Traffic.COMMUTERS);
        double stock = roads.getTransitCapacity();
        double riders = roads.getTransitRiders();
        double fare = policy.chargedFare();   // today's money (0.7.45)
        BuildAdvice.Measure transit = BuildAdvice.Measure.of(BuildAdvice.Kind.TRANSIT);

        VBox fareCard = fareCard(roads, econ, policy);

        if (stock <= 0) {
            VBox left = new VBox(8, caption("WHO RIDES", null), figure("nobody", 28, Palette.TEXT_HEAD),
                    infoLine("No transit yet: a Bus Network is the cheap rung, a Metro Line carries a city.",
                            NO_TRANSIT_INFO, true, Palette.SIZE_BODY, Palette.TEXT_LABEL, 300),
                    doorPill("Build transit", Icons.BUILD, Palette.BUILDING, () -> ui.buildScreen.openOn(transit)));
            List<ScaleRow> rows = List.of(ScaleRow.of("Commuters", people(commuters),
                    List.of(Run.of(0, commuters, Palette.PEOPLE + "59"))).strong());
            page.getChildren().add(heroCard(left, 300, new VBox(8, caption("FROM COMMUTERS TO RIDERS", null),
                    scaleRows(rows, Math.max(1, commuters), List.of(), 230, 84, 14))));
        } else {
            VBox left = new VBox(4, caption("WHO RIDES", null),
                    pairLine(people(riders), Palette.TEXT_HEAD, "ride", null, null, null),
                    words(pct(commuters > 0 ? riders / commuters : 0) + " of the city's commuters",
                            Palette.SIZE_BODY + 1, Palette.TEXT_HEAD),
                    words("driving, they would add " + shortNumber(walk.ridersAsDrivers()) + " trips to the road",
                            Palette.SIZE_BODY, Palette.TEXT_LABEL),
                    words(fare <= 0 ? "free to ride" : unitPrice(fare) + " a ride · " + money(policy.monthlyFare()) + " a month",
                            Palette.SIZE_BODY, Palette.TEXT_LABEL));
            page.getChildren().add(heroCard(left, 300, transitFunnel(roads, policy, transit)));
        }

        javafx.scene.layout.GridPane cards = equalColumns(2, TILE_GAP);
        cards.add(transitBooks(econ), 0, 0);
        cards.add(fareCard, 1, 0);
        page.getChildren().add(cards);
        page.getChildren().add(details("transit", "the ceilings, the riders by reason, the books and the fare, line by line",
                detailsOpen, ui::redraw, () -> transitStatement(roads, econ, policy)));
    }

    /** The no-transit card's (i) (P6). */
    static final String NO_TRANSIT_INFO = "This city has built no transit at all. A Bus Network is the cheap rung "
            + "and a Metro Line is the one that carries a city; all three are on the "
            + "Build tab under Roads & transit. Until one is standing the fare "
            + "charges nobody anything.";

    /** The ceilings' (i) (P7). */
    static final String CEILINGS_INFO = "A city that builds a metro and no streets gets a metro nobody can reach, "
            + "and no city on earth puts everybody on public transport. Those are the "
            + "second and third lines. The first is the only one a player buys.";

    /**
     * The owners' row's (i) (P8, rewritten for 0.7.49): what a ride costs
     * against a journey's fuel and the share that choose the bus on it
     * (InfrastructureManager.ownersChoosingAt()), and what the remembered
     * commute adds (ownersRidingAt()).
     */
    static String carsRideInfo(InfrastructureManager roads, TaxPolicy policy) {
        double dial = policy.getTransitFare();
        double chose = roads.ownersChoosingAt(dial), ride = roads.ownersRidingAt(dial);
        return String.format("%s against a journey's fuel %s: %s choose the bus on cost, and the jam adds "
                        + "the rest. The commute this city remembers is %s of free-flowing, so %s of the owners "
                        + "in reach would ride, and the seats the car-less leave carry %s of them. All on the bus "
                        + "at half the fuel's cost, all in the car at twice it.",
                dial <= 0 ? "A free ride" : "A ride " + unitPrice(policy.chargedFare()), unitPrice(roads.getFuelPerJourney()),
                pct(chose), pct(roads.getRememberedThroughput()), pct(ride), people(roads.getChoiceRiders()));
    }

    /** The funnel: commuters, the three ceilings with the lowest tagged, and the riders by reason (0.7.49) - on one scale, the commuters full. */
    VBox transitFunnel(InfrastructureManager roads, TaxPolicy policy, BuildAdvice.Measure transit) {
        double commuters = roads.getLoad(Traffic.COMMUTERS);
        double stock = roads.getTransitCapacity(), road = roads.getTransitRoadCeiling(), share = roads.getTransitShareCeiling();
        double lowest = roads.getTransitCeiling();
        // Which ceiling binds: the first of the three that is the lowest.
        int binds = stock <= lowest ? 0 : road <= lowest ? 1 : 2;
        double scale = Math.max(1, commuters);

        List<ScaleRow> rows = new ArrayList<>();
        rows.add(ScaleRow.of("Commuters", people(commuters), List.of(Run.of(0, commuters, Palette.PEOPLE + "59"))).strong());
        rows.add(ScaleRow.caption("Three ceilings: the lowest wins", CEILINGS_INFO));
        String[] names = {"the stock could carry",
                String.format("the road under it allows (%.0f× street capacity)", InfrastructureManager.TRANSIT_NEEDS_ROAD),
                pct(InfrastructureManager.TRANSIT_MAX_SHARE) + " of each group, the most a city's lines reach"};
        double[] at = {stock, road, share};
        Runnable[] go = {() -> ui.buildScreen.openOn(transit), () -> open("Roads"), null};
        for (int k = 0; k < 3; k++) {
            Run run = Run.of(0, at[k], Palette.PEOPLE);
            if (k != binds) run = run.outlined();
            if (go[k] != null) run = run.go(go[k]);
            ScaleRow row = ScaleRow.of(names[k], people(at[k]), List.of(run));
            if (k == binds) row = row.tag("lowest", Palette.PEOPLE);
            if (go[k] != null) row = row.go(go[k]);
            rows.add(row);
        }
        rows.addAll(ridersByReason(roads, policy));

        VBox box = new VBox(8, caption("FROM COMMUTERS TO RIDERS", null), scaleRows(rows, scale, List.of(), 230, 84, 14));
        HBox ring = new HBox(Palette.GAP_LOOSE,
                words("Build's transit ring reads " + CityNeeds.servedPct(roads.getTransitServed()) + " " + CityNeeds.SERVED
                        + ": room on the stock for " + CityNeeds.servedPct(roads.getTransitServed()) + " of commuters. "
                        + pct(commuters > 0 ? roads.getTransitRiders() / commuters : 0)
                        + " ride.", Palette.SIZE_LABEL, Palette.TEXT_MUTED),
                doorPill("Build transit", Icons.BUILD, Palette.BUILDING, () -> ui.buildScreen.openOn(transit)));
        ring.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().add(ring);
        return box;
    }

    /**
     * THE RIDERS BY REASON (0.7.49), under the ceilings: the commuters with
     * no car of their own - in reach, riding, and walking, out of reach or
     * with no seat - then the owners - in reach, and who chose the bus - and
     * those who drive, and the riders. Each the network's own read.
     */
    List<ScaleRow> ridersByReason(InfrastructureManager roads, TaxPolicy policy) {
        double carless = roads.getCaptiveCommuters(), inReach = roads.getCaptiveDemand(), riding = roads.getCaptiveRiders();
        double walking = roads.getWalking();
        double owners = roads.getOwnerCommuters(), ownersInReach = owners * InfrastructureManager.TRANSIT_MAX_SHARE;
        double chose = roads.getChoiceRiders(), drivers = roads.getDrivers(), riders = roads.getTransitRiders();
        List<ScaleRow> rows = new ArrayList<>();
        rows.add(ScaleRow.caption("Riders by reason", RIDERS_INFO));
        rows.add(ScaleRow.of("No car of their own: " + people(carless) + " (" + pct(roads.getCaptiveShare()) + ") · in reach "
                        + people(inReach) + " · riding " + people(riding), people(riding),
                List.of(Run.of(0, riding, Palette.PEOPLE), Run.of(riding, walking, Palette.PEOPLE).outlined())));
        rows.add(ScaleRow.of("walking: " + people(walking), people(walking),
                List.of(Run.of(0, walking, Palette.PEOPLE + "59"))).tip("no line near them or no seat"));
        rows.add(ScaleRow.of("With a car: " + people(owners) + " · in reach " + people(ownersInReach)
                        + " · chose the bus " + people(chose), people(chose),
                List.of(Run.of(0, chose, Palette.PEOPLE), Run.of(chose, drivers, Palette.PEOPLE).outlined()))
                .info(carsRideInfo(roads, policy)));
        rows.add(ScaleRow.of("Drive (chose the car): " + people(drivers), people(drivers),
                List.of(Run.of(0, drivers, Palette.PEOPLE + "59"))));
        rows.add(ScaleRow.of("Riders: " + people(riders), people(riders),
                List.of(Run.of(0, riders, Palette.PEOPLE))).strong());
        return rows;
    }

    /** The riders' caption's (i) (0.7.49). */
    static final String RIDERS_INFO = "A household holds one car at most, so a couple's second earner and four of five "
            + "flatmates have none. Those with no car of their own ride whatever the fare if a line reaches them and "
            + "there is a seat, and walk if not - the seats go to them first. Owners weigh a ride against a journey's "
            + "fuel. The same share of each group lives in reach of a line.";

    /** WHAT IT COSTS THE CITY: the bill as a bar, the fares in it, and the net - neutral, with signs: a net cost is a policy, not a failure. */
    VBox transitBooks(EconomyManager econ) {
        double bill = econ.getTransitBill(), fares = econ.getTransitFares(), net = econ.getTransitNet();
        double scale = Math.max(bill, fares);
        SegmentBar bar = segmentBar(List.of(new Segment(fares, Palette.MONEY, false, null, null,
                        "fares collected " + money(fares), null)),
                scale > 0 ? scale : 1, List.of(new Tick(bill, Palette.TEXT_HEAD, 2, "the bill", "drivers, crews and upkeep " + money(bill))),
                0, 14);
        VBox card = ServicesScreen.card(cardHead(Icons.COIN, Palette.MONEY, "What it costs the city", null),
                figureLine(money(bill), "a month to run ·", money(fares), "back in fares"),
                bar,
                words(bill > 0 ? "fares cover " + pct(fares / bill) : "nothing to run yet", Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                figureLine(money(Math.abs(net)), net > 0 ? "net cost to the city a month" : "net profit to the city a month"),
                column(statementLine("Drivers, crews and upkeep", signedTight(bill, true)),
                        statementLine("Fares collected", signedTight(fares, false)),
                        statementTotal(net > 0 ? "Net cost to the city" : "Net profit to the city",
                                signedTight(Math.abs(net), net > 0), Palette.TEXT_HEAD)),
                // ...and who pays it (0.7.49, B9): the treasury, a promise, on the budget's Transit line.
                door("paid by the treasury every month · Government › Spending", Palette.ACCENT,
                        () -> ui.governmentScreen.open("Spending", null)));
        return card;
    }

    /**
     * THE FARE on its dial card (0.7.38, the Policy spec's D18 step 6), as
     * Policy draws every dial: the reading and what a month of riding costs,
     * the ladder, and under it what the fare would do, before and after,
     * following the thumb - each row the model's own read at that fare
     * (fareEffects()) where the page multiplied riders by fares itself until
     * now - with the one sentence the preview owes behind its (i), and the
     * Apply once a fare is staged.
     */
    VBox fareCard(InfrastructureManager roads, EconomyManager econ, TaxPolicy policy) {
        double fare = policy.getTransitFare();
        double want = ui.policyScreen.staged("fare", fare);
        /*
         * IN TODAY'S MONEY (0.7.45; the UI spec's B7, D13): the dial is a ride's
         * price at founding prices, and the month charges it at the expected
         * price level the money constants are struck at - so the card, its
         * ladder and its Apply read the charged fare, and the dial's founding
         * figure and the level are behind the (i), as the floor card does.
         */
        double level = policy.getExpectedLevel();
        java.util.function.DoubleFunction<String> reads = r -> r <= 0 ? "free" : unitPrice(r * level);
        // ...to the cap in today's unit (B6, 0.7.47), as TaxPolicy.setTransitFare() holds it.
        Ladder ladder = ui.policyScreen.ownLadder("fare", fare, 0,
                policy.maxTransitFare(), policy.maxTransitFare() / 20, reads);
        /*
         * ...AND THE ANCHOR ON THE CARD (0.7.49): the dial's founding price
         * and the level it is charged at, which the (i) alone carried; and a
         * third line, what the fare is weighed against and who it weighs on -
         * a drive's fuel, and a month's pass against an unskilled household's
         * take-home (the transit spec's risk 1: a commuter with no car pays
         * whatever the fare is).
         */
        return dialCard(new Levers.DialCard(Icons.BUS, Palette.PEOPLE, "THE FARE", fareInfo(policy),
                        fare <= 0 ? "free" : unitPrice(policy.chargedFare()) + " a ride",
                        fare <= 0 ? "free: nobody pays to ride"
                                : money(policy.monthlyFare()) + " a month for " + String.format("%.0f", TaxPolicy.JOURNEYS_A_MONTH)
                                  + " journeys · set at " + unitPrice(fare) + " at founding prices, "
                                  + String.format("×%.3f", policy.getExpectedLevel()) + " the prices people expect",
                        List.of(words(fareWeighs(roads, policy), Palette.SIZE_LABEL, Palette.TEXT_LABEL)),
                        ladder, want, fareEffects(roads, econ),
                        "this month's ceilings at the new fare - the second round is not in it", PREVIEW_INFO,
                        ui.policyScreen.applyFoot("fare", want <= 0 ? "Make it free" : "Set the fare to " + unitPrice(want * level),
                                () -> policy.setTransitFare(want))),
                FARE_LADDER, true);
    }

    /**
     * The fare card's third line (0.7.49): a drive's fuel a journey, as the
     * month struck it at the exchange rate, and a month's pass as a share of
     * an unskilled household's take-home - its row's take-home over its
     * households (HouseholdAccounts.getRowDisposable()).
     */
    String fareWeighs(InfrastructureManager roads, TaxPolicy policy) {
        HouseholdAccounts books = ui.game.getHouseholds();
        int unskilled = PayTier.UNSKILLED.ordinal();
        double homes = books.getRowHouseholds(unskilled);
        double takeHome = homes > 0 ? books.getRowDisposable(unskilled) / homes : 0;
        String fuel = "a drive's fuel: " + unitPrice(roads.getFuelPerJourney()) + " a journey at today's exchange rate";
        if (!(takeHome > 0) || policy.getTransitFare() <= 0) return fuel;
        return fuel + " · a month's pass is " + String.format("%.1f%%", policy.monthlyFare() / takeHome * 100)
                + " of an unskilled household's take-home";
    }

    /** The fare's (i) with the dial behind it (0.7.45): its founding figure and the level it is charged at. */
    static String fareInfo(TaxPolicy policy) {
        return FARE_INFO + String.format(" The dial is set at founding prices - %s a ride - and charged at the price level"
                        + " people expect, ×%.3f this month (what money constants are struck at): %s.",
                unitPrice(policy.getTransitFare()), policy.getExpectedLevel(), unitPrice(policy.chargedFare()));
    }

    /** The fare's ladder, and its effects under it: the card is one of two across the page. */
    static final double FARE_LADDER = 520;

    /**
     * What the fare would do at any value of its thumb (pure: the probe reads
     * them), each the model's read at that fare against this month's: the
     * riders (InfrastructureManager.ridersAt()) and the car owners among them
     * (choiceRidersAt(), 0.7.49), a month of fares from them
     * (faresAt()), what the system then costs the city net
     * (EconomyManager.transitNetAt()), and the trips the change puts back
     * onto the road (backOnTheRoadAt()). Area colours, never a verdict: a
     * dear fare and a free one are both a policy.
     */
    java.util.function.DoubleFunction<List<Pieces.Effect>> fareEffects(InfrastructureManager roads, EconomyManager econ) {
        // The riders' (i) splits them (0.7.49): the car-less ride at any fare, the owners choose.
        String split = String.format("This month %s of the riders have no car of their own and ride whatever the fare; "
                        + "%s are car owners who chose the bus. A fare moves only the owners.",
                Money.people(roads.getCaptiveRiders()), Money.people(roads.getChoiceRiders()));
        return v -> List.of(
                Pieces.Effect.of("Riders", roads.getTransitRiders(), roads.ridersAt(v), Money::people)
                        .colour(Palette.PEOPLE).info(split),
                Pieces.Effect.of("Car owners on the bus", roads.getChoiceRiders(), roads.choiceRidersAt(v), Money::people)
                        .colour(Palette.PEOPLE),
                Pieces.Effect.of("Fares collected, a month", econ.getTransitFares(), roads.faresAt(v), Money::money)
                        .delta(PolicyScreen::moneyMove),
                Pieces.Effect.of("What it costs the city, net, a month", econ.getTransitNet(),
                        econ.transitNetAt(roads.faresAt(v)), PolicyScreen::amount).delta(PolicyScreen::moneyMove),
                Pieces.Effect.of("Trips back onto the road", 0, roads.backOnTheRoadAt(v), InfrastructureScreen::trips)
                        .colour(Palette.PEOPLE));
    }

    /** Trips put onto the road, signed with a true minus for trips taken off it: "+1,240 trips", "−310 trips", "none". */
    static String trips(double n) {
        if (Math.abs(n) < .5) return "none";
        return String.format("%+,.0f trips", n).replace('-', '\u2212');
    }

    /** The fare's (i) (P9). */
    static final String FARE_INFO = "A FARE IS A PRICE TO CAR OWNERS AND A CHARGE TO EVERYONE ELSE. Commuters with no car "
            + "of their own ride whatever it costs if a line reaches them; owners weigh it against a journey's fuel - all "
            + "on the bus at half its cost, all in the car at twice it. A dearer fare takes more money and puts owners "
            + "back on the road; it does not empty the buses.";

    /** The preview's (i) (P10). */
    static final String PREVIEW_INFO = "The riders line is this month's ceilings at the new fare, which is the "
            + "honest half of the preview. The half it cannot do is the second round: "
            + "a road that gets worse puts some of them back on the tram, and a road "
            + "that clears takes more of them off it. Both take a few months.";

    /** The old Transit page, line by line, in its fold. */
    VBox transitStatement(InfrastructureManager roads, EconomyManager econ, TaxPolicy policy) {
        double stock = roads.getTransitCapacity(), fare = policy.getTransitFare();
        VBox c = column(statementHead("Three ceilings, and the lowest one wins"),
                statementLine("What the stock could carry", String.format("%,.0f", stock)),
                statementLine(String.format("...what the road under it allows (%.0fx street capacity)",
                                InfrastructureManager.TRANSIT_NEEDS_ROAD),
                        String.format("%,.0f", roads.getTransitRoadCeiling()), Palette.TEXT_MUTED),
                statementLine(String.format("...and the most a city's lines reach (%.0f%% of each group)",
                                InfrastructureManager.TRANSIT_MAX_SHARE * 100),
                        String.format("%,.0f", roads.getTransitShareCeiling()), Palette.TEXT_MUTED),
                statementTotal("The lowest of them", String.format("%,.0f", roads.getTransitCeiling()), Palette.TEXT_HEAD),
                statementNote(CEILINGS_INFO),
                statementHead("Riders by reason"),
                statementLine(String.format("No car of their own (%s)", pct(roads.getCaptiveShare())),
                        people(roads.getCaptiveCommuters())),
                statementLine("...in reach of a line", people(roads.getCaptiveDemand()), Palette.TEXT_MUTED),
                statementLine("...riding, whatever the fare", people(roads.getCaptiveRiders())),
                statementLine("...walking: no line near them or no seat", people(roads.getWalking()), Palette.TEXT_MUTED),
                statementLine("With a car", people(roads.getOwnerCommuters())),
                statementLine("...in reach of a line",
                        people(roads.getOwnerCommuters() * InfrastructureManager.TRANSIT_MAX_SHARE), Palette.TEXT_MUTED),
                statementLine("...chose the bus", people(roads.getChoiceRiders())),
                statementNote(carsRideInfo(roads, policy)),
                statementLine("Drive (chose the car)", people(roads.getDrivers())),
                statementTotal("Riders", people(roads.getTransitRiders()), Palette.TEXT_HEAD),
                statementHead("What it costs the city"),
                statementLine("Drivers, crews and upkeep", signedTight(econ.getTransitBill(), true)),
                statementLine("Fares collected", signedTight(econ.getTransitFares(), false)),
                statementTotal(econ.getTransitNet() > 0 ? "Net cost to the city" : "Net profit to the city",
                        signedTight(Math.abs(econ.getTransitNet()), econ.getTransitNet() > 0), Palette.TEXT_HEAD),
                statementHead("The fare"),
                statementLine(String.format("...and what a month of riding costs one commuter (%.0f journeys)",
                                TaxPolicy.JOURNEYS_A_MONTH),
                        fare <= 0 ? "nothing" : money(policy.monthlyFare()), fare <= 0 ? Palette.TEXT_MUTED : Palette.TEXT_HEAD),
                statementLine("...against a drive's fuel, a journey at today's exchange rate",
                        unitPrice(roads.getFuelPerJourney())));
        return c;
    }

    /* =====================================================================
       THE RAILWAY - the twelfth sector, and the only mode the city does not
       own (0.7.29)

       The page's question: is the railway worth it to this city? The month's
       lorry bill - what the tonnes that crossed would have cost by road - as
       one bar split three ways: what the railway billed at home, what still
       went abroad with the lorries, and what the city's shippers kept. Then
       its track and trains, what it hauls, and its business.
       ===================================================================== */

    void railwayPage(VBox page) {

        ham.citybuildersim.sectors.Rail rail = ui.game.getSectors().rail();
        InfrastructureManager roads = ui.game.getInfrastructureManager();
        double track = rail.trackTonnes();

        VBox left;
        if (track <= 0) {
            left = new VBox(8, caption("WHAT THE RAILWAY CHARGES", null), figure("no track", 28, Palette.TEXT_MUTED),
                    infoLine("Nobody has laid a line: every tonne leaves by lorry, and the freight is paid to the world.",
                            NO_TRACK_INFO, true, Palette.SIZE_BODY, Palette.TEXT_LABEL, 300),
                    doorPill("Rail on Build", Icons.BUILD, Palette.BUILDING, () -> ui.buildScreen.openCategory(BuildAdvice.RAIL)));
        } else {
            double quote = rail.getQuote();
            SegmentBar gauge = segmentBar(List.of(Segment.of(Math.min(1, quote), Palette.BUSINESS)), 1,
                    List.of(new Tick(ham.citybuildersim.sectors.Rail.RAIL_FLOOR, Palette.TEXT_MUTED, 1,
                                    "its floor " + pct(ham.citybuildersim.sectors.Rail.RAIL_FLOOR),
                                    "It never quotes under this: freight needs profit"),
                            new Tick(1, Palette.TEXT_HEAD, 2, "a lorry", "It can never charge more than a lorry")),
                    260, 10);
            left = new VBox(8, caption("WHAT THE RAILWAY CHARGES", null),
                    pairLine(pct(quote), Palette.TEXT_HEAD, "of a lorry", null, null, null),
                    gauge,
                    infoLine(String.format("set monthly from its costs + %.1f%% on its track",
                                    ham.citybuildersim.sectors.Rail.TARGET_RETURN * 100),
                            QUOTE_INFO, true, Palette.SIZE_BODY, Palette.TEXT_LABEL, 300));
        }
        page.getChildren().add(heroCard(left, 300, freightBill(rail)));

        if (track > 0) {
            javafx.scene.layout.GridPane cards = equalColumns(3, TILE_GAP);
            cards.add(trackCard(rail), 0, 0);
            cards.add(haulsCard(rail), 1, 0);
            cards.add(businessCard(rail), 2, 0);
            page.getChildren().add(cards);
        }
        HBox rule = new HBox(Palette.GAP_LOOSE, infoLine(String.format("It will not lay a line it cannot fill to %.0f%%, "
                                + "which is why a town does not get a spur.",
                        ham.citybuildersim.sectors.Rail.MIN_LINE_UTILISATION * 100),
                LINE_RULE_INFO, true, Palette.SIZE_LABEL, Palette.TEXT_MUTED, 600),
                doorPill("Rail on Build", Icons.BUILD, Palette.BUILDING, () -> ui.buildScreen.openCategory(BuildAdvice.RAIL)));
        rule.setAlignment(Pos.CENTER_LEFT);
        page.getChildren().add(rule);
        page.getChildren().add(details("railway", "the track, the hauling and the business, line by line",
                detailsOpen, ui::redraw, () -> railwayStatement(rail, roads)));
    }

    /** The quote's (i) (P12). */
    static final String QUOTE_INFO = "Of what a lorry would charge for the same tonne. The railway is a "
            + "private business: it sets this itself, every month, from what the "
            + "month cost it to run plus a return on the track it has sunk - and "
            + "it can never charge more than a lorry, because nobody would pay. It walks a quarter of the way "
            + "to where it should be each month, so the needle moves.";

    /** No track (P11). */
    static final String NO_TRACK_INFO = "Nobody has laid a line in this city, so every tonne that leaves "
            + "it leaves by lorry and the whole of the freight wedge is paid to "
            + "the world. A Rail Spur is on the Build tab.";

    /** The line rule (P17). */
    static final String LINE_RULE_INFO = String.format(
            "It will not lay a line it cannot fill to %.0f%%, which is why a town does "
            + "not get a spur. The full set of books is on the Sector economy tab, where "
            + "every sector's are.", ham.citybuildersim.sectors.Rail.MIN_LINE_UTILISATION * 100);

    /** The bill three ways (P21, rewritten: what neither is paid is kept, not paid abroad). */
    static final String BILL_INFO = "The month's lorry bill is what the tonnes that crossed the city's boundary "
            + "would have cost entirely by lorry. It is three things. Billed at home is the railway's revenue, "
            + "for the part it carried, and it stays in the city - somebody's wages, somebody's profit. Paid "
            + "abroad is what the lorries were paid for the part it did not carry, and it leaves the city with "
            + "the cargo. Kept is what neither is paid: the railway's quote under the lorry rate, on what it "
            + "carried. Billed at home and kept stay in the city: that is the difference a railway makes to the "
            + "trade balance, before it makes any difference to the road.";

    /** The month's freight bill, three ways: one bar as long as the lorry bill, its key with the figures, and the fuel. */
    VBox freightBill(ham.citybuildersim.sectors.Rail rail) {
        double truck = rail.getTruckBill(), home = rail.getHaulageBilled(), abroad = rail.getPaidAbroad(), kept = rail.getKept();
        boolean split = Double.isFinite(abroad);
        List<Segment> parts = new ArrayList<>();
        parts.add(new Segment(home, Palette.BUSINESS, false, null, null, "billed at home " + money(home), null));
        if (split) {
            parts.add(new Segment(abroad, Palette.ORE, false, null, null, "paid abroad " + money(abroad), null));
            parts.add(new Segment(kept, Palette.MONEY, true, null, null, "kept " + money(kept), null));
        } else {
            // A save from before the split was kept: the rest of the bill, unsplit until a month runs.
            parts.add(new Segment(Math.max(0, truck - home), Palette.TEXT_SPENT, true, null, null,
                    "paid abroad or kept: " + UNKNOWN_YET, null));
        }
        SegmentBar bar = segmentBar(parts, truck > 0 ? truck : 1, List.of(), 0, 22);
        javafx.scene.layout.FlowPane key = new javafx.scene.layout.FlowPane(Palette.GAP_LOOSE, 4);
        key.getChildren().add(keySwatch(Palette.BUSINESS, "billed at home " + money(home)));
        if (split) {
            key.getChildren().addAll(keySwatch(Palette.ORE, "paid abroad " + money(abroad)),
                    keySwatch(Palette.MONEY, "kept " + money(kept)));
        } else {
            key.getChildren().add(keySwatch(Palette.TEXT_SPENT, "paid abroad or kept: " + UNKNOWN_YET));
        }
        VBox box = new VBox(8, caption("THE MONTH'S FREIGHT BILL, THREE WAYS", BILL_INFO),
                figureLine(String.format("%,.0f t", rail.getTradeTonnes()), "crossed the boundary · by lorry it would have cost",
                        money(truck), ""),
                bar, key);
        if (rail.getFuelBill() > 0) {
            box.getChildren().add(words(money(rail.getFuelBill()) + " of the railway's own bill is diesel"
                    + abroadWords(rail.getFuelBill(), rail.getFuelImported(), " bought abroad"),
                    Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        }
        if (truck <= 0) box.getChildren().add(words("Nothing crossed the boundary last month.", Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        return box;
    }

    /** TRACK AND TRAINS: the track, the sets against what it needs, what it can carry, and the trains it is short or the ones it wears out. */
    VBox trackCard(ham.citybuildersim.sectors.Rail rail) {
        double fleet = rail.fleet(), needs = rail.setsNeeded(), shortBy = rail.bid(Good.ROLLING_STOCK);
        VBox card = ServicesScreen.card(cardHead(Icons.RAIL, Palette.BUSINESS, "Track and trains", null),
                figureLine(String.format("%,.0f t", rail.trackTonnes()), "a month of track"),
                supplyBar(needs, fleet, fleet, Palette.BUSINESS, "needed", null,
                        String.format("%,.1f wagon sets of %,.1f needed", fleet, needs), 0, 10),
                words(String.format("%,.0f of %,.0f wagon sets · carries %,.0f t a month", fleet, needs, rail.getCapacityTonnes()),
                        Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        if (shortBy > 1e-9) {
            HBox line = new HBox(Palette.GAP, chip(String.format("%,.1f sets short", shortBy), Palette.WARN),
                    infoButton(String.format(
                            "Track with no locomotives carries nothing. This railway is holding "
                            + "%,.0f tonnes of line it cannot run, because a wagon set moves %,.0f "
                            + "tonnes a month and it is %,.1f sets short. It buys them like any "
                            + "other capital good - from a Locomotive Works if the city has one, "
                            + "and from the world if it does not.",
                            rail.trackTonnes() - rail.getCapacityTonnes(),
                            ham.citybuildersim.sectors.Rail.TONNES_PER_SET, shortBy), true));
            line.setAlignment(Pos.CENTER_LEFT);
            card.getChildren().add(line);
        } else {
            card.getChildren().add(infoLine(String.format("%,.2f replacement sets a month", rail.replacementSets()),
                    String.format("A set lasts %.0f years, so this fleet asks for about %,.2f replacement "
                            + "sets a month for ever - which is what makes a Locomotive Works a "
                            + "business rather than a single sale.",
                            ham.citybuildersim.sectors.Rail.SET_LIFE_MONTHS / 12, rail.replacementSets()),
                    true, Palette.SIZE_LABEL, Palette.TEXT_LABEL, 360));
        }
        return card;
    }

    /** WHAT IT HAULS: what crossed and what it took, each stream's share, and what that takes off the road. */
    VBox haulsCard(ham.citybuildersim.sectors.Rail rail) {
        double crossed = rail.getTradeTonnes(), hauled = rail.getHauledTonnes();
        double[] carried = rail.getCarried();
        SegmentBar bar = supplyBar(crossed, crossed, hauled, Palette.BUSINESS, null, null,
                String.format("%,.0f of %,.0f tonnes by rail", hauled, crossed), 0, 10);
        javafx.scene.layout.FlowPane chips = new javafx.scene.layout.FlowPane(Palette.GAP_TIGHT, Palette.GAP_TIGHT);
        for (Traffic s : new Traffic[] {Traffic.BULK, Traffic.GOODS, Traffic.COMMUTERS}) {
            double share = s.ordinal() < carried.length ? carried[s.ordinal()] : 0;
            chips.getChildren().add(chip(s.label().toLowerCase() + " " + (s.isFreight() ? pct(share) : "never"),
                    s.isFreight() ? streamColour(s) : Palette.TEXT_MUTED));
        }
        return ServicesScreen.card(cardHead(Icons.LORRY, Palette.ORE, "What it hauls", null),
                figureLine(String.format("%,.0f t", crossed), "crossed · rail took",
                        crossed > 0 ? pct(hauled / crossed) : "none", ""),
                bar, chips,
                infoLine("takes " + pct(InfrastructureManager.RAIL_ROAD_RELIEF) + " of what it carries off the road",
                        RELIEF_INFO, true, Palette.SIZE_LABEL, Palette.TEXT_LABEL, 360));
    }

    /** The relief's (i) (P15). */
    static final String RELIEF_INFO = String.format(
            "A tonne that leaves by train does not drive across the city to leave by "
            + "lorry - but it still gets to the siding somehow, and that is a truck. So "
            + "the relief is %.0f%% and never all of it, and a terminal carries a road "
            + "load of its own on top: a Rail Spur reads as 94%% lorries.",
            InfrastructureManager.RAIL_ROAD_RELIEF * 100);

    /** THE BUSINESS: billed against what the rule allows, the fuel, the net income, the capital - and the door to its books. */
    VBox businessCard(ham.citybuildersim.sectors.Rail rail) {
        double billed = rail.getHaulageBilled(), allowed = rail.getAllowedRevenue();
        double net = rail.statement().netIncome;
        // Red only below zero, as Sectors colours it: a business losing money (D20).
        HBox netLine = new HBox(6, figure(signedTight(Math.abs(net), net < 0), Palette.SIZE_BODY + 3,
                net < 0 ? Palette.BAD : Palette.TEXT_HEAD), words("net income a month", Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        netLine.setAlignment(Pos.BASELINE_LEFT);
        VBox card = ServicesScreen.card(cardHead(Icons.COIN, Palette.BUSINESS, "The business", null),
                bulletBar(billed, allowed, Palette.BUSINESS, 0,
                        "billed " + money(billed) + " · the rule allows "
                        + (Double.isFinite(allowed) ? money(allowed) : "— (" + UNKNOWN_YET + ")")),
                words("diesel " + money(rail.getFuelBill()) + " a month"
                        + abroadWords(rail.getFuelBill(), rail.getFuelImported(), ", bought abroad"), Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                netLine,
                words("track " + money(rail.getBuildingsValue()) + " and land " + money(rail.getLandValue()) + " on its books",
                        Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        if (rail.trackTonnes() > 0 && billed < allowed * .75) {
            HBox line = new HBox(Palette.GAP, chip("bigger than its city", Palette.WARN), infoButton(BIGGER_INFO, true));
            line.setAlignment(Pos.CENTER_LEFT);
            card.getChildren().add(line);
        }
        card.getChildren().add(door("its books on Sectors", Palette.BUSINESS,
                () -> ui.sectorScreen.openSectorBooks(rail, "Income")));
        return card;
    }

    /** The too-big railway (P16). */
    static final String BIGGER_INFO = "It bills under three quarters of what the rule allows. It is allowed to "
            + "charge for the track it has sunk and it cannot: "
            + "nobody pays more than a lorry, so it pins at the ceiling and earns "
            + "less on more capital. Over-building a railway does not make freight "
            + "cheaper; it makes a railway that cannot pay for itself.";

    /** The old railway page, line by line, in its fold. */
    VBox railwayStatement(ham.citybuildersim.sectors.Rail rail, InfrastructureManager roads) {
        double track = rail.trackTonnes(), fleet = rail.fleet(), needs = rail.setsNeeded(), capacity = rail.getCapacityTonnes();
        VBox c = column(statementHead("Track, and the trains to run on it"),
                statementLine("Track standing", String.format("%,.0f tonnes a month", track)),
                statementLine("Wagon sets owned", String.format("%,.1f", fleet), fleet + 1e-9 >= needs ? null : Palette.WARN),
                statementLine("...and the sets that track needs", String.format("%,.1f", needs), Palette.TEXT_MUTED),
                statementTotal("What it can actually carry", String.format("%,.0f tonnes a month", capacity),
                        capacity < track ? Palette.WARN : Palette.TEXT_HEAD),
                statementHead("What it is hauling"),
                statementLine("The city's cross-border freight", String.format("%,.0f tonnes", rail.getTradeTonnes())),
                statementLine("...of which the railway took", String.format("%,.0f tonnes", rail.getHauledTonnes())));
        double[] carried = rail.getCarried();
        javafx.scene.layout.GridPane byStream = grid(new double[] {170, 120, 120, 120}, rightAfterFirst(4));
        gridHead(byStream, "", "by rail", "off the road", "still on it");
        int line = 1;
        for (Traffic stream : Traffic.values()) {
            double share = stream.ordinal() < carried.length ? carried[stream.ordinal()] : 0;
            byStream.add(gridCell(stream.label(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            byStream.add(gridCell(String.format("%.0f%%", share * 100), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 1, line);
            byStream.add(gridCell(String.format("%.0f%%", InfrastructureManager.RAIL_ROAD_RELIEF * share * 100),
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
            byStream.add(gridCell(String.format("%.2fx", roads.roadCostOf(stream)), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 3, line);
            line++;
        }
        c.getChildren().addAll(byStream, statementNote(RELIEF_INFO),
                statementHead("Whether it is earning it"),
                statementLine("What the rule allows it to bill", moneyOr(rail.getAllowedRevenue()), Palette.TEXT_MUTED),
                statementLine("What it actually billed", money(rail.getHaulageBilled())),
                statementLine("What those tonnes would have cost by lorry", money(rail.getTruckBill()), Palette.TEXT_MUTED),
                statementLine("...of which paid abroad, by lorry", moneyOr(rail.getPaidAbroad()), Palette.TEXT_MUTED),
                statementLine("...and kept by the shippers", moneyOr(rail.getKept()), Palette.TEXT_MUTED),
                statementLine("Diesel", signedTight(rail.getFuelBill(), true)),
                statementTotal("Net income", signedTight(Math.abs(rail.statement().netIncome), rail.statement().netIncome < 0),
                        rail.statement().netIncome >= 0 ? Palette.TEXT_HEAD : Palette.BAD),
                statementLine("Track and land on its books",
                        money(rail.getBuildingsValue()) + " + " + money(rail.getLandValue()), Palette.TEXT_MUTED),
                statementNote(LINE_RULE_INFO));
        return c;
    }

    /* =====================================================================
       FREIGHT - the band, the bill, and the lorries (0.7.29)

       The page's question: what does moving it add to each good's price, and
       does anyone lack lorries? A bar a good, as a share of today's import
       price: the world's own margin, the freight still paid abroad and the
       railway's charge, with a mark at what it would be by lorry - bulk, the
       railway's, first. Then the month's freight bill in five figures, and
       the lorries in one line while nobody is short.
       ===================================================================== */

    void freightPage(VBox page) {

        ham.citybuildersim.sectors.Rail rail = ui.game.getSectors().rail();

        page.getChildren().add(heroCard(new VBox(8, caption("WHAT MOVING IT ADDS TO EACH GOOD'S PRICE", null),
                        infoLine("A railway takes freight out of the band and bills it at home.", BAND_INFO, true,
                                Palette.SIZE_BODY, Palette.TEXT_LABEL, 230),
                        bandKey()), 230, goodsBars()));

        page.getChildren().add(sectionHead("THE MONTH'S FREIGHT BILL", BILL_INFO, hint("each figure opens the railway")));
        HBox strip = vitalsBar(
                limitCell("CROSSED", String.format("%,.0f t", rail.getTradeTonnes()), "the boundary, both ways",
                        Palette.TEXT_HEAD, "The railway", () -> open("The railway")),
                limitCell("BY LORRY", money(rail.getTruckBill()), "what it would all have cost",
                        Palette.TEXT_HEAD, "The railway", () -> open("The railway")),
                limitCell("BILLED AT HOME", money(rail.getHaulageBilled()), "by the railway: stays in the city",
                        Palette.TEXT_HEAD, "The railway", () -> open("The railway")),
                limitCell("PAID ABROAD", moneyOr(rail.getPaidAbroad()), Double.isFinite(rail.getPaidAbroad())
                                ? "to the lorries: leaves with the cargo" : UNKNOWN_YET,
                        Palette.TEXT_HEAD, "The railway", () -> open("The railway")),
                limitCell("KEPT", moneyOr(rail.getKept()), Double.isFinite(rail.getKept())
                                ? "what neither is paid" : UNKNOWN_YET,
                        Palette.TEXT_HEAD, "The railway", () -> open("The railway")));
        strip.setAlignment(Pos.CENTER_LEFT);
        page.getChildren().add(strip);

        page.getChildren().add(sectionHead("AND WHO HAS THE LORRIES TO DO IT", null, null));
        page.getChildren().add(lorriesCard());
        page.getChildren().add(details("freight", "the band and the lorries, as grids", detailsOpen, ui::redraw,
                this::freightStatement));
    }

    /** The bars' (i) (P18 and P20, for bars rather than a grid). */
    static final String BAND_INFO = "Every traded good's price has a cost of MOVING it inside the gap between "
            + "what the world pays and what the world charges. A railway takes part of "
            + "that out of the band and bills it at home instead - so a city with good "
            + "logistics faces a narrower band on everything it trades, and that is "
            + "most of what a railway is worth.\n\nEach bar is the gap on one good, as a share of today's import "
            + "price: the world's own margin (grey), which no logistics touches; the freight still paid abroad "
            + "(sand), the lorries' on both sides; and the railway's charge (violet), billed at home. The first "
            + "two are \"in the band\" - the gap a local buyer and a local seller both do better inside, ALREADY "
            + "narrowed by whatever the railway carries, because that freight is no longer paid abroad. All "
            + "three are \"delivered\", the figure a business actually decides on. The white mark is what it "
            + "would be with every tonne on a lorry. With nothing railed there is no violet, and the band and "
            + "the delivered gap are the same number.";

    /** The bars' key. */
    VBox bandKey() {
        VBox key = new VBox(4, keySwatch(Palette.TEXT_SPENT, "the world's margin"),
                keySwatch(Palette.ORE, "freight paid abroad"),
                keySwatch(Palette.BUSINESS, "the railway's charge"));
        Region mark = new Region();
        mark.setMinSize(2, 12);
        mark.setPrefSize(2, 12);
        mark.setMaxSize(2, 12);
        mark.setStyle("-fx-background-color: " + Palette.TEXT_HEAD + ";");
        Label by = new Label("by lorry");
        by.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        HBox lorry = new HBox(9, mark, by);
        lorry.setAlignment(Pos.CENTER_LEFT);
        lorry.setStyle("-fx-padding: 0 0 0 4;");
        key.getChildren().add(lorry);
        return key;
    }

    /** One good's bar, worked out without drawing it: its shares of today's import price, and its words. */
    record GoodBar(Good good, double margin, double abroad, double rail, double byLorry, double delivered, double band,
                   String tip) { }

    /** Every good the city can both buy and sell, as GoodBar - bulk first, then the rest, each by its delivered share, the widest first. */
    List<GoodBar> goodBars() {
        List<GoodBar> bulk = new ArrayList<>(), goods = new ArrayList<>();
        for (Good g : Good.values()) {
            if (!g.traded() || !g.exportable() || !g.importable()) continue;
            GoodsMarket m = ui.game.getMarkets().get(g);
            double imp = m.importPrice();
            if (!(imp > 0)) continue;
            double band = (m.importPrice() - m.exportPrice()) / imp;
            double delivered = (m.netImportPrice() - m.netExportPrice()) / imp;
            GoodBar b = new GoodBar(g, Math.max(0, m.worldMargin()) / imp, m.freightInBand() / imp, m.railInWedge() / imp,
                    m.bandByLorry() / imp, delivered, band,
                    String.format("%s\nworld pays %s · asks %s\nin the band %s · delivered %s · by lorry %s",
                            g.label(), unitPrice(m.exportPrice()), unitPrice(imp), pct(band), pct(delivered),
                            pct(m.bandByLorry() / imp)));
            (g.traffic() == Traffic.BULK ? bulk : goods).add(b);
        }
        java.util.Comparator<GoodBar> widest = (a, b) -> Double.compare(b.delivered(), a.delivered());
        bulk.sort(widest);
        goods.sort(widest);
        List<GoodBar> out = new ArrayList<>(bulk);
        out.addAll(goods);
        return out;
    }

    /** The bars: a caption a group, a row a good on one scale - today's import price, or more if a good's gap is wider. */
    Node goodsBars() {
        List<GoodBar> bars = goodBars();
        if (bars.isEmpty()) {
            return words("Nothing this city trades has both a world buyer and a world seller yet, "
                    + "so there is no band to show.", Palette.SIZE_BODY, Palette.TEXT_MUTED);
        }
        double scale = 1;
        for (GoodBar b : bars) scale = Math.max(scale, Math.max(b.byLorry(), b.delivered()));
        List<ScaleRow> rows = new ArrayList<>();
        boolean bulkHead = false, goodsHead = false;
        for (GoodBar b : bars) {
            boolean bulk = b.good().traffic() == Traffic.BULK;
            if (bulk && !bulkHead) { rows.add(ScaleRow.caption("BULK · rail-eligible", null)); bulkHead = true; }
            if (!bulk && !goodsHead) { rows.add(ScaleRow.caption("GOODS", null)); goodsHead = true; }
            rows.add(ScaleRow.of(b.good().label(), pct(b.delivered()) + " delivered · " + pct(b.byLorry()) + " by lorry",
                            List.of(Run.of(0, b.margin(), Palette.TEXT_SPENT),
                                    Run.of(b.margin(), b.abroad(), Palette.ORE),
                                    Run.of(b.margin() + b.abroad(), b.rail(), Palette.BUSINESS)))
                    .marks(List.of(new Tick(b.byLorry(), Palette.TEXT_HEAD, 2, null, "by lorry " + pct(b.byLorry()))))
                    .icon(bulk ? Icons.INDUSTRY : Icons.SHOPS).tip(b.tip()));
        }
        return scaleRows(rows, scale, List.of(), 170, 184, 10);
    }

    /** The lorries: one line while nobody is short; a bar a short sector otherwise. */
    VBox lorriesCard() {
        long owned = 0, needed = 0;
        int sectors = 0;
        List<Sector> shortOf = new ArrayList<>();
        for (Sector s : ui.game.getSectors().all()) {
            if (!shownFleet(s)) continue;
            sectors++;
            owned += Math.round(s.vanFleet());
            needed += Math.round(s.vansNeeded());
            if (s.getVanRatio() < 1) shortOf.add(s);
        }
        VBox card;
        if (sectors == 0) {
            card = ServicesScreen.card(cardHead(Icons.LORRY, Palette.ORE, "The lorries", null),
                    words("Nothing standing in this city moves a tonne of anything yet, so nobody "
                            + "needs a lorry. The first farm, mine or mill changes that.", Palette.SIZE_BODY, Palette.TEXT_LABEL));
        } else if (shortOf.isEmpty()) {
            HBox line = new HBox(Palette.GAP, icon(Icons.TICK, Palette.GOOD, 16),
                    infoLine(String.format("Every sector has its lorries: %,d owned, %,d needed, across %d sectors",
                            owned, needed, sectors), LORRY_INFO, true, Palette.SIZE_BODY, Palette.TEXT_HEAD, 760));
            line.setAlignment(Pos.CENTER_LEFT);
            card = ServicesScreen.card(line);
        } else {
            card = ServicesScreen.card(cardHead(Icons.LORRY, Palette.ORE, "The lorries", LORRY_INFO));
            for (Sector s : shortOf) {
                Label name = words(s.label(), Palette.SIZE_LABEL, Palette.TEXT_LABEL);
                name.setMinWidth(150);
                name.setPrefWidth(150);
                name.setMaxWidth(150);
                SegmentBar bar = supplyBar(s.vansNeeded(), s.vanFleet(), s.vanFleet(), Palette.WARN, "needed", null,
                        String.format("%,.0f of %,.0f lorries", s.vanFleet(), s.vansNeeded()), 0, 10);
                HBox.setHgrow(bar, Priority.ALWAYS);
                Label at = figure("running at " + pct(s.getVanRatio()), Palette.SIZE_LABEL, Palette.WARN);
                HBox row = new HBox(Palette.GAP, name, bar, at);
                row.setAlignment(Pos.CENTER_LEFT);
                card.getChildren().add(row);
            }
            HBox chipLine = new HBox(Palette.GAP, chip("short of lorries", Palette.WARN), infoButton(String.format(
                    "A sector cannot put a whole fleet on the road in a month "
                    + "- it takes about %.0f - so anything that has just built a factory is "
                    + "running below nameplate until the vehicles arrive. It is not stopped: "
                    + "a firm short of its own lorries hires haulage and sends fuller loads, "
                    + "which is worth %.0f%% of nameplate however bad it gets. If this never "
                    + "clears, the city is not making or importing enough of them.",
                    Sector.FLEET_DELIVERY_MONTHS, Sector.MIN_VAN_RATE * 100), true));
            chipLine.setAlignment(Pos.CENTER_LEFT);
            card.getChildren().add(chipLine);
        }
        return card;
    }

    /** The lorries' (i) (P23). */
    static final String LORRY_INFO = String.format(
            "A vehicle moves about %,.0f tonnes a month and lasts %.0f years. The "
            + "tonnage is BOTH SIDES of the door - a mill that buys a hundred tonnes of "
            + "ore and ships eighty of steel runs a hundred and eighty tonnes of lorry "
            + "movements - and it is counted at nameplate, because a firm buys lorries "
            + "for the factory it has rather than for the month it is having.",
            Sector.TONNES_PER_VAN, Sector.VAN_LIFE_MONTHS / 12);

    /**
     * Whether a sector's fleet is worth a row: it moves a tonne or owns or
     * needs a lorry - and not a row of noughts (spec bug B11: a fraction of a
     * tonne or of a van got past the old test and printed "0 · 0 · 0 · 100%").
     */
    static boolean shownFleet(Sector s) {
        if (s.tonnesMoved() <= 0 && s.vanFleet() <= 0) return false;
        return Math.round(s.tonnesMoved()) != 0 || Math.round(s.vanFleet()) != 0 || Math.round(s.vansNeeded()) != 0;
    }

    /** The old Freight page's two grids, in its fold. */
    VBox freightStatement() {
        VBox c = column(statementHead("The band, good by good"));
        javafx.scene.layout.GridPane band = grid(new double[] {150, 95, 95, 95, 95}, rightAfterFirst(5));
        gridHead(band, "", "world pays", "world asks", "in the band", "delivered");
        int line = 1;
        for (GoodBar b : goodBars()) {
            GoodsMarket m = ui.game.getMarkets().get(b.good());
            band.add(gridCell(b.good().label(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            band.add(gridCell(unitPrice(m.exportPrice()), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 1, line);
            band.add(gridCell(unitPrice(m.importPrice()), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
            band.add(gridCell(pct(b.band()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 3, line);
            band.add(gridCell(b.rail() > 0 ? pct(b.delivered()) : "—", Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        c.getChildren().add(line == 1 ? statementNote("Nothing this city trades has both a world buyer and a world "
                + "seller yet, so there is no band to show.") : band);
        c.getChildren().add(statementHead("And who has the lorries to do it"));
        javafx.scene.layout.GridPane fleets = grid(new double[] {150, 110, 90, 90, 90}, rightAfterFirst(5));
        gridHead(fleets, "", "tonnes/mo", "needs", "owns", "running at");
        line = 1;
        for (Sector s : ui.game.getSectors().all()) {
            if (!shownFleet(s)) continue;
            double ratio = s.getVanRatio();
            fleets.add(gridCell(s.label(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            fleets.add(gridCell(String.format("%,.0f", s.tonnesMoved()), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 1, line);
            fleets.add(gridCell(String.format("%,.0f", s.vansNeeded()), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
            fleets.add(gridCell(String.format("%,.0f", s.vanFleet()),
                    s.vanFleet() + 1e-9 >= s.vansNeeded() ? Palette.TEXT_HEAD : Palette.WARN, Palette.SIZE_CAPTION, true), 3, line);
            fleets.add(gridCell(String.format("%.0f%%", ratio * 100), ratio >= 1 ? Palette.TEXT_MUTED : Palette.WARN,
                    Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        c.getChildren().add(line == 1 ? statementNote("Nothing standing in this city moves a tonne of anything yet, so "
                + "nobody needs a lorry. The first farm, mine or mill changes that.") : fleets);
        c.getChildren().add(statementNote(LORRY_INFO));
        return c;
    }

    /**
     * How much of a fuel bill was bought abroad (0.7.62), after the bill's own
     * words: all of it, `whole` (", bought abroad"); part, ", $1.2M of it
     * bought abroad"; none, ", off the city's refineries".
     */
    static String abroadWords(double bill, double imported, String whole) {
        if (!(imported < bill - 1e-9 * Math.max(1, bill))) return whole;
        if (!(imported > 0)) return ", off the city's refineries";
        return ", " + money(imported) + " of it bought abroad";
    }
}
