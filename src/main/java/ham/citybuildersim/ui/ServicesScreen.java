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

/**
 * The services tab: the four systems the city runs for its people - health,
 * education, utilities and safety - each opening on an Overview whose one
 * picture answers the system's question, then the pages behind it and the
 * books it keeps, under the four figures for whichever is open.
 *
 * WHY. Split out of UserInterface on 2026-09-18, the banners from SERVICES
 * to THE BOOKS exactly as they were; redrawn in 0.7.28 in Build's style
 * (the project's spec-services-0728.md). Each page was a 560 px statement
 * column of label-and-figure rows with a grey paragraph under most of them,
 * and more than half the centre stood empty. The pictures lead now - the
 * sick rate as its causes over the care the city runs, the schools as a
 * pipeline, the utilities as capacity rows, the crime as its causes - the
 * tables sit behind "details" and the paragraphs behind an (i). Services
 * shows what the city's buildings DO and why; what is on site and what to
 * order is Build's, one door away both ways. The Infrastructure tab shared
 * this file until 0.7.28 (InfrastructureScreen). The shell reads which
 * system and page are open (serviceArea, servicePage) for the rail and the
 * scroll memory.
 */
final class ServicesScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    ServicesScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       SERVICES - WHAT THE CITY PROVIDES, AND HOW WELL IT COVERS.

       Three systems that had no home. Healthcare was reachable only as a build
       menu, so a player could see what a clinic COSTS and never what the
       city's clinics were COVERING. The schools were behind a tuition slider on
       the policy screen. The utilities were filed under private enterprise,
       which is not who owns them (they are the city's: AN OVERVIEW FIRST,
       below) and is useless to somebody asking whether the lights are on.

       COVERAGE IS THE FIGURE, not spending. What a city spends on clinics says
       nothing about whether anybody can see a doctor; a percentage of the
       people who need care and can get it says exactly that, and it is the
       number that moves the mortality rates.

       TWO RAILS, per Jerus. The first version was one scroll with three
       headings on it, and it showed a coverage percentage for each of nine
       things and nothing else - which is a table of contents, not a screen. The
       top strip picks the system and the second picks the part of it, and every
       part now gets the room to say what it actually does: what it is measured
       against, what it buys, what it costs, and - for the schools - which of
       the three enrolment gates is the one holding it shut.

       CONSTRUCTION IS NOT HERE. Jerus: "construction goes in the business, not
       service, in this game construction is private." It is a sector with a
       balance sheet and a borrowing rate, and it already sits on the sector
       economy screen with the mills and the mines.

       AN OVERVIEW FIRST (0.7.28). Every system opens on the one picture that
       answers its question - why are people off sick, where do the city's
       skills come from, is there enough power, water and road, where does the
       crime come from - and the old pages stay behind it as chips, so the
       scroll memory and every door into them still land. Utilities is its
       Overview and its books: power and water are rows on it, and the road
       is one card (0.7.29; a row until then) whose door is Infrastructure's
       Roads page, which owns the road. The utilities are the city's own plants, not a
       private business (their net income is the city's cash, Game's month).
       ===================================================================== */

    /** A system, and the parts of it the second strip offers. */
    record ServiceArea(String name, String[] pages) { }

    /** The page every system opens on (0.7.28), and the last chip of each, its books. */
    static final String OVERVIEW = "Overview", BOOKS = "Books";

    static ServiceArea[] serviceAreas() {
        return new ServiceArea[] {
            new ServiceArea("Health",
                    new String[] {OVERVIEW, "General care", "Childcare", "Senior care",
                                  "Death care", BOOKS}),
            new ServiceArea("Education",
                    new String[] {OVERVIEW, "Basic ladder", "College", "University",
                                  "Professions", BOOKS}),
            new ServiceArea("Utilities",
                    new String[] {OVERVIEW, BOOKS}),
            new ServiceArea("Safety",
                    new String[] {OVERVIEW, "Police", "Prisons", BOOKS}),
        };
    }

    /** Which system, and which part of it - remembered like the build category. */
    static final String SERVICE_AREA_HOME = "Health";
    String serviceArea = SERVICE_AREA_HOME;
    /** ...and every system opens on its Overview (0.7.28; General care until then). */
    static final String SERVICE_HOME  = OVERVIEW;
    String servicePage = SERVICE_HOME;

    ServiceArea currentArea() {
        for (ServiceArea area : serviceAreas()) {
            if (area.name().equals(serviceArea)) return area;
        }
        return serviceAreas()[0];
    }

    /** Which "details" folds are open, by name - remembered while the game runs, not saved. */
    private final java.util.Set<String> detailsOpen = new java.util.HashSet<>();

    /** The events whose system has been opened since they happened, so their "!" goes (by kind and month). */
    private final java.util.Set<String> seen = new java.util.HashSet<>();

    /* =====================================================================
       THE FRAME (0.7.28)

       The head, the strip of systems, the four figures and the strip of
       pages stay where they are; only the page under them scrolls - as
       since the first version (THE RAILS DO NOT SCROLL, below), and not
       Build's whole-page scroll, which would lose the strips again.
       ===================================================================== */

    /** How much of the stage the fixed frame takes above the page's scroller: the head, the systems, the four figures with their sparklines and changes, the pages, and their gaps. */
    static final double FRAME_CHROME = 268;

    void showServicesStatsMenu() {
        ui.clearMenu("showServicesStatsMenu", () -> showServicesStatsMenu());

        ServiceArea area = currentArea();

        // A page name left over from another area would render nothing at all,
        // so the second strip always falls back to its own first entry.
        boolean known = false;
        for (String page : area.pages()) if (page.equals(servicePage)) known = true;
        if (!known) servicePage = area.pages()[0];

        List<CityNeeds.Need> all = CityNeeds.measure(ui.game, SummaryScreen.WORDS);
        List<Event> events = events();
        // Opening a system is seeing its news: its "!" goes.
        for (Event e : events) if (e.area().equals(serviceArea)) seen.add(e.key());

        VBox page = widePage();
        switch (serviceArea) {
            case "Education" -> educationPage(page, all, events);
            case "Utilities" -> utilityPage(page, all, events);
            case "Safety"    -> safetyPage(page, all, events);
            default          -> healthPage(page, all, events);
        }

        javafx.scene.layout.FlowPane pages = chipStrip(area.pages(), servicePage, Palette.SIZE_LABEL, name -> {
            servicePage = name;
            openPage();
        });
        pages.setAlignment(Pos.CENTER_LEFT);

        VBox frame = new VBox(Palette.GAP, head(), areaStrip(all, events), areaVitals(all), pages);
        frame.setMaxWidth(PAGE_WIDE);
        frame.setFillWidth(true);
        frame.setPadding(new javafx.geometry.Insets(0, 18, 4, 18));

        /*
         * THE RAILS DO NOT SCROLL. They were inside the scroller in the first
         * version and vanished off the top of it the moment you read anything,
         * so switching from senior care to death care meant scrolling back up
         * to find the strip you had just used. Only the page moves; the four
         * figures and both strips stay where you left them. The page is as
         * wide as the scroller shows, up to PAGE_WIDE (0.7.28).
         */
        javafx.scene.control.ScrollPane body = ui.scrolled(page, FRAME_CHROME);
        page.prefWidthProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                () -> Math.min(PAGE_WIDE, Math.max(320, body.getViewportBounds().getWidth())),
                body.viewportBoundsProperty()));
        ui.rootMenu.getChildren().addAll(frame, body);
    }

    /**
     * Draw a page the player just picked, at the top of itself.
     *
     * The scroll machinery keeps a position per screen, which is right for
     * everything it was built for - a monthly redraw should not move a screen
     * you scrolled on purpose - and wrong for a strip that swaps the whole
     * page underneath the same screen name. Opening death care three quarters
     * of the way down death care is not where anybody wants to arrive.
     */

    void openPage() {
        ui.innerScrollAt.remove(ui.currentScreen + ":body");
        showServicesStatsMenu();
    }

    /** One system on one of its pages, at the top of it (0.7.28): every door into Services - the left panel's OFF SICK, Build's "why ›", a card, a part of a bar. */
    void open(String area, String page) {
        serviceArea = area;
        servicePage = page;
        ui.innerScrollAt.remove("showServicesStatsMenu:body");
        showServicesStatsMenu();
    }

    /**
     * The page that explains a Build ring (0.7.28), as {system, page}: Build's
     * "why ›". The road and transit rings are Infrastructure's, and land on
     * the Utilities Overview if asked here.
     */
    static String[] pageFor(BuildAdvice.Measure m) {
        switch (m.kind()) {
            case CARE:
                return new String[] {"Health", m.care() == CareType.CHILDCARE ? "Childcare"
                        : m.care() == CareType.SENIOR ? "Senior care" : "General care"};
            case DEATH: case PLOTS:
                return new String[] {"Health", "Death care"};
            case SCHOOL:
                return new String[] {"Education", m.school().isBasic() ? "Basic ladder"
                        : m.school() == EducationType.COLLEGE ? "College"
                        : m.school() == EducationType.UNIVERSITY ? "University" : "Professions"};
            case POLICE:
                return new String[] {"Safety", "Police"};
            case CELLS:
                return new String[] {"Safety", "Prisons"};
            default:
                return new String[] {"Utilities", OVERVIEW};
        }
    }

    static String[] areaNames() {
        ServiceArea[] areas = serviceAreas();
        String[] names = new String[areas.length];
        for (int i = 0; i < areas.length; i++) names[i] = areas[i].name();
        return names;
    }

    static ServiceArea currentAreaFor(String name) {
        for (ServiceArea area : serviceAreas()) {
            if (area.name().equals(name)) return area;
        }
        return serviceAreas()[0];
    }

    /** The head: "Services › Health" with the people teal's swatch - the title a way back to the system's Overview - and Build's door at the right (a pill since 0.7.34). */
    HBox head() {
        return pageHead("Services", Palette.PEOPLE, () -> open(serviceArea, OVERVIEW), serviceArea, null,
                doorPill("Build them on Build", Icons.BUILD, Palette.BUILDING, () -> ui.buildScreen.openCategory(BuildAdvice.OVERVIEW)));
    }

    /** A system's icon: the Build category's it is built in, Utilities the power's. */
    static String areaIcon(String area) {
        switch (area) {
            case "Education": return Icons.EDUCATION;
            case "Utilities": return Icons.UTILITIES;
            case "Safety":    return Icons.SAFETY;
            default:          return Icons.HEALTH;
        }
    }

    /** The NEEDS YOU rows a system answers: Health's healthcare, Education's, Utilities' power, water and the road, Safety's. */
    static boolean answers(String area, CityNeeds.Go go) {
        switch (area) {
            case "Education": return go == CityNeeds.Go.EDUCATION;
            case "Utilities": return go == CityNeeds.Go.UTILITIES || go == CityNeeds.Go.ROADS;
            case "Safety":    return go == CityNeeds.Go.SAFETY;
            default:          return go == CityNeeds.Go.HEALTHCARE;
        }
    }

    /** A system's verdict: the worst NEEDS YOU level of the rows it answers, and that row (null when none is near its line). */
    static CityNeeds.Need areaWorst(String area, List<CityNeeds.Need> all) {
        CityNeeds.Need worst = null;
        for (CityNeeds.Need n : all) {
            if (!answers(area, n.go())) continue;
            if (worst == null || n.level() > worst.level()) worst = n;
        }
        return worst;
    }

    /**
     * The systems, as chips (0.7.28): each its icon, its name and a dot in
     * the colour of the worst NEEDS YOU row it answers, and a "!" while
     * something happened there this month that the player has not opened.
     * A click opens the system's Overview. Left, as Build's strip is.
     */
    javafx.scene.layout.FlowPane areaStrip(List<CityNeeds.Need> all, List<Event> events) {
        javafx.scene.layout.FlowPane strip = new javafx.scene.layout.FlowPane(6, 6);
        strip.setAlignment(Pos.CENTER_LEFT);
        for (ServiceArea a : serviceAreas()) {
            boolean on = a.name().equals(serviceArea);
            CityNeeds.Need worst = areaWorst(a.name(), all);
            int level = worst == null ? 0 : worst.level();
            boolean news = false;
            for (Event e : events) if (e.area().equals(a.name()) && !seen.contains(e.key())) news = true;

            Label name = new Label(a.name());
            name.setStyle(Palette.words(Palette.SIZE_BODY, on ? Palette.TEXT_HEAD : Palette.TEXT_BODY));
            name.setMinWidth(Region.USE_PREF_SIZE);
            Region dot = new Region();
            dot.setMinSize(7, 7);
            dot.setPrefSize(7, 7);
            dot.setMaxSize(7, 7);
            dot.setStyle("-fx-background-color: " + BuildScreen.verdict(level) + "; -fx-background-radius: 4;");
            HBox inside = new HBox(6, icon(areaIcon(a.name()), on ? Palette.PEOPLE : Palette.TEXT_LABEL, 14), name, dot);
            inside.setAlignment(Pos.CENTER_LEFT);
            if (news) inside.getChildren().add(chip("!", Palette.WARN));
            inside.setStyle("-fx-padding: 5 12 5 10; -fx-cursor: hand;"
                    + " -fx-background-color: " + (on ? Palette.RAISED : Palette.CONTROL) + ";"
                    + " -fx-background-radius: " + Palette.RADIUS_TIGHT + ";"
                    + " -fx-border-color: " + (on ? Palette.ACCENT : "transparent") + "; -fx-border-width: 0 0 2 0;");
            Tooltip tip = new Tooltip(a.name() + (worst == null || worst.level() == 0
                    ? ": nothing near its line" : ": " + worst.label().toLowerCase() + " · " + worst.reading())
                    + (news ? "\nSomething happened here this month." : ""));
            tip.setShowDelay(Duration.millis(300));
            Tooltip.install(inside, tip);
            inside.setOnMouseClicked(e -> open(a.name(), OVERVIEW));
            strip.getChildren().add(inside);
        }
        return strip;
    }

    /* =====================================================================
       THE FOUR FIGURES FOR WHICHEVER SYSTEM IS OPEN.

       They stay put while the second strip moves underneath them, so drilling
       into senior care never costs you sight of the sick rate that senior care
       is one of the answers to. Each is a door (0.7.28): the bills to their
       books, the thinnest cover to Build on that ring, the roads to
       Infrastructure, the rest to the page that explains them. Where the
       history keeps the figure, a sparkline of ten years and its change on
       last month.
       ===================================================================== */

    /**
     * One of the four: its label, figure, note and colour (a verdict only
     * where a NEEDS YOU row judges it), its door - where, and what it opens -
     * the history series its sparkline draws (null: none kept) and how its
     * change on last month is written.
     */
    record Kpi(String label, String value, String note, String tone, String where, Runnable go,
               String series, java.util.function.DoubleFunction<String> change) { }

    HBox areaVitals(List<CityNeeds.Need> all) {
        List<Kpi> kpis = kpis(serviceArea, all);
        VBox[] cells = new VBox[kpis.size()];
        for (int i = 0; i < cells.length; i++) cells[i] = kpiCell(kpis.get(i));
        return vitalsBar(cells);
    }

    List<Kpi> kpis(String area, List<CityNeeds.Need> all) {
        return switch (area) {
            case "Education" -> educationKpis(all);
            case "Utilities" -> utilityKpis(all);
            case "Safety"    -> safetyKpis(all);
            default          -> healthKpis(all);
        };
    }

    /** A limit cell with its sparkline at the right of its figure and its change under the note. */
    VBox kpiCell(Kpi k) {
        VBox cell = limitCell(k.label(), k.value(), k.note(), k.tone(), k.where(), k.go());
        if (k.series() == null) return cell;
        HistorySave history = ui.game.getHistorySave();
        Node figure = cell.getChildren().get(1);
        cell.getChildren().remove(1);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox top = new HBox(6, figure, gap, sparkline(history.aligned(k.series()), history.getMonth(), Palette.PEOPLE, 64, 16));
        top.setAlignment(Pos.CENTER_LEFT);
        cell.getChildren().add(1, top);
        String d = change(k.series(), k.change());
        if (d != null) {
            Label change = new Label(d);
            change.setWrapText(true);
            change.setMaxWidth(LIMIT_CELL - 28);
            change.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            cell.getChildren().add(change);
        }
        return cell;
    }

    /** The last two months a series recorded, oldest first; NaN where it has fewer. */
    double[] lastTwo(String series) {
        double[] s = ui.game.getHistorySave().aligned(series);
        double last = Double.NaN, before = Double.NaN;
        for (int i = s.length - 1; i >= 0; i--) {
            if (!Double.isFinite(s[i])) continue;
            if (Double.isNaN(last)) last = s[i];
            else { before = s[i]; break; }
        }
        return new double[] {before, last};
    }

    /** A figure's change on last month, from its history: "▲ 0.1 pts on last month", "no change on last month"; null with under two months kept. */
    String change(String series, java.util.function.DoubleFunction<String> written) {
        if (series == null || written == null) return null;
        double[] two = lastTwo(series);
        if (!Double.isFinite(two[0]) || !Double.isFinite(two[1])) return null;
        double d = two[1] - two[0];
        String shown = written.apply(Math.abs(d));
        if (shown.equals(written.apply(0))) return "no change on last month";
        return (d > 0 ? "▲ " : "▼ ") + shown + " on last month";
    }

    /** A share of the workforce or a rate, in points: "13.8 pts", "0.03 pts" under a tenth. */
    static String points(double rate) {
        double p = Math.abs(rate) * 100;
        return (p > 0 && p < .1 ? String.format("%.2f", p) : String.format("%.1f", p)) + " pts";
    }

    /** A share, as Build's rings write it. */
    static String pct(double share) { return BuildScreen.pct(share); }

    /** A row's verdict colour, or the plain figure's where no row judges it. */
    static String tone(CityNeeds.Need n) { return n == null ? Palette.TEXT_HEAD : BuildScreen.verdict(n.level()); }

    /** The NEEDS YOU row of a kind - for CARE, of a care type; for HIGHER_SCHOOL, of a school - or null. */
    static CityNeeds.Need need(List<CityNeeds.Need> all, CityNeeds.Kind kind, CareType care, EducationType school) {
        for (CityNeeds.Need n : all) {
            if (n.kind() != kind) continue;
            if (care != null && n.care() != care) continue;
            if (school != null && n.school() != school) continue;
            return n;
        }
        return null;
    }

    /* =====================================================================
       EVENTS (0.7.28)

       What happened this month, said once: a line at the top of its
       system's Overview for the month it happens, and a "!" on its chip
       until the system is opened. Read off the history and the state a save
       already keeps, so a loaded city says the same: an outbreak begins, a
       brownout begins, the last plot is taken, the first of the dead lie
       unburied, the basic ladder passes its line. (A profession's first
       licence needs each profession's licences kept month by month, which
       the history does not; it waits for that.)
       ===================================================================== */

    /** One event: its system, a key unique to it and its month, the line, its (i) (null: none) and its colour - a verdict, the event is news. */
    record Event(String area, String key, String words, String whole, String tone) { }

    List<Event> events() {
        List<Event> out = new ArrayList<>();
        int month = ui.game.getMonth();
        Health health = ui.game.getHealth();
        Healthcare service = ui.game.getHealthcare();

        if (health.isOutbreak() && health.getOutbreakStarted() == month) {
            out.add(new Event("Health", "outbreak:" + month, String.format(
                    "An outbreak began this month: +%s on the sick rate. It fades on its own in a few months.",
                    points(health.getOutbreakSeverity())), outbreakWhole(), Palette.BAD));
        }
        double plots = ui.game.getBuildingManager().getCareCapacity(CareType.BURIAL);
        if (plots > 0 && Healthcare.plotsRemaining(plots, service.getPlotsUsed()) < .5 && service.getBurials() > 0) {
            out.add(new Event("Health", "ground:" + month,
                    "The last burial plot was taken this month: from now the dead go to the crematoria, or wait.",
                    PLOTS_INFO, Palette.BAD));
        }
        double[] unburied = lastTwo("unburied");
        if (service.getUnburied() >= .5 && Double.isFinite(unburied[0]) && unburied[0] < .5) {
            out.add(new Event("Health", "unburied:" + month, String.format(
                    "The dead have nowhere to go: %s lie unburied this month, where last month none did.",
                    people(service.getUnburied())), null, Palette.BAD));
        }
        double[] power = lastTwo("energyRatio");
        if (power[1] < 1 && power[0] >= 1) {
            out.add(new Event("Utilities", "brownout:" + month, String.format(
                    "A brownout began this month: the grid supplies %s of what the city asks.", pct(power[1])),
                    BROWNOUT_INFO, Palette.BAD));
        }
        double[] ladder = lastTwo("schoolCoverage");
        if (ladder[1] > CityNeeds.SCHOOLS_YELLOW && ladder[0] <= CityNeeds.SCHOOLS_YELLOW) {
            out.add(new Event("Education", "ladder:" + month, String.format(
                    "The basic ladder passed %s this month: every stage has a seat for nearly every child.",
                    pct(CityNeeds.SCHOOLS_YELLOW)), null, Palette.GOOD));
        }
        return out;
    }

    /** An event, or a state worth a line: a tinted line with its icon, its words wrapping, and its (i). */
    HBox eventLine(String words, String whole, String tone) {
        Label says = new Label(words);
        says.setWrapText(true);
        says.setStyle(Palette.words(Palette.SIZE_BODY, tone.equals(Palette.BAD) ? Palette.BAD_TEXT : Palette.TEXT_HEAD));
        HBox.setHgrow(says, Priority.ALWAYS);
        says.setMaxWidth(Double.MAX_VALUE);
        HBox line = new HBox(Palette.GAP, icon(tone.equals(Palette.GOOD) ? Icons.TICK : Icons.ALERT, tone, 15), says);
        if (whole != null) line.getChildren().add(infoButton(whole, false));
        line.setAlignment(Pos.CENTER_LEFT);
        line.setMinHeight(28);
        line.setStyle("-fx-padding: 4 10 4 10; -fx-background-color: " + tint(tone, .10) + ";"
                + " -fx-background-radius: 6; -fx-border-color: " + tint(tone, .45) + "; -fx-border-radius: 6;");
        return line;
    }

    /** This system's events as lines. */
    void eventLines(VBox page, String area, List<Event> events) {
        for (Event e : events) if (e.area().equals(area)) page.getChildren().add(eventLine(e.words(), e.whole(), e.tone()));
    }

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

    /** A card on the Build ground: raised, rounded, a hairline edge. */
    static VBox card(Node... children) {
        VBox c = new VBox(8);
        for (Node n : children) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(Double.MAX_VALUE);
        c.setStyle("-fx-padding: 12 14 12 14; -fx-background-color: " + Palette.RAISED + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";");
        return c;
    }

    /** ...made a door: a hand, an accent edge under the pointer, a click anywhere opens `go`; its tooltip says where. */
    static VBox doorCard(VBox c, Runnable go, String where) {
        String rest = c.getStyle() + " -fx-cursor: hand;";
        c.setStyle(rest);
        c.setOnMouseEntered(e -> c.setStyle(rest.replace("-fx-border-color: " + Palette.EDGE, "-fx-border-color: " + Palette.ACCENT)));
        c.setOnMouseExited(e -> c.setStyle(rest));
        c.setOnMouseClicked(e -> go.run());
        if (where != null) {
            Tooltip tip = new Tooltip(where);
            tip.setShowDelay(Duration.millis(300));
            Tooltip.install(c, tip);
        }
        return c;
    }

    /** A card's head: its icon in a teal square, its name, and its (i) at the right. */
    static HBox cardHead(String svg, String name, String info, Node right) {
        Label n = new Label(name);
        n.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        n.setWrapText(true);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox head = new HBox(8, iconSquare(svg, Palette.PEOPLE, 28, 15), n, gap);
        if (right != null) head.getChildren().add(right);
        if (info != null) head.getChildren().add(infoButton(info, false));
        head.setAlignment(Pos.CENTER_LEFT);
        return head;
    }

    /**
     * A fold: "details ▸ caption", and what is inside when it is open -
     * remembered by name while the game runs (the land office's and the
     * People page's way). The old statements live in these, verbatim but for
     * the fixes, so nothing the screen said before 0.7.28 is gone.
     */
    VBox details(String name, String caption, Node... inside) {
        boolean open = detailsOpen.contains(name);
        Label toggle = new Label((open ? "details ▾  " : "details ▸  ") + caption);
        toggle.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.ACCENT) + " -fx-cursor: hand;");
        toggle.setWrapText(true);
        VBox box = new VBox(4, toggle);
        if (open) for (Node n : inside) if (n != null) box.getChildren().add(n);
        toggle.setOnMouseClicked(e -> {
            if (!detailsOpen.remove(name)) detailsOpen.add(name);
            ui.redraw();
        });
        box.setStyle("-fx-padding: 4 0 0 0;");
        return box;
    }

    /** A statement column, for a fold: the old rows at their old width. */
    static VBox column(Node... rows) {
        VBox c = new VBox(0);
        for (Node n : rows) if (n != null) c.getChildren().add(n);
        c.setMaxWidth(STATEMENT);
        return c;
    }

    /** "fee $10 a visit · Set the fee ›": a price, and the door to the dial on the Policy tab. */
    HBox priceLine(String text, String whole, String door, String policyPage) {
        HBox line = new HBox(Palette.GAP, infoLine(text, whole, false, Palette.SIZE_BODY, Palette.TEXT_LABEL, 700),
                Pieces.door(door, Palette.MONEY, () -> {
                    ui.policyScreen.policyArea = "Promises";
                    ui.policyScreen.policyPage = policyPage;
                    ui.policyScreen.dropProposal();
                    ui.policyScreen.showPolicyMenu();
                }));
        line.setAlignment(Pos.CENTER_LEFT);
        return line;
    }

    /**
     * Two stacked bars on one money scale, each with its total at the
     * right, a key under them, and the bottom line beside them (0.7.28): the
     * books' cost against what came back, a utility's sales against its
     * wages. Money in the model's thousands.
     */
    VBox moneyBars(String topName, List<Slice> top, String bottomName, List<Slice> bottom, Node right) {
        double a = 0, b = 0;
        for (Slice s : top) a += Math.max(0, s.amount());
        for (Slice s : bottom) b += Math.max(0, s.amount());
        double scale = Math.max(a, b);
        VBox bars = new VBox(6, moneyRow(topName, top, a, scale), moneyRow(bottomName, bottom, b, scale));
        javafx.scene.layout.FlowPane key = new javafx.scene.layout.FlowPane(Palette.GAP_LOOSE, 4);
        for (Slice s : top) key.getChildren().add(keySwatch(s.colour(), s.name()));
        for (Slice s : bottom) key.getChildren().add(keySwatch(s.colour(), s.name()));
        bars.getChildren().add(key);
        HBox.setHgrow(bars, Priority.ALWAYS);
        bars.setMaxWidth(Double.MAX_VALUE);
        HBox row = new HBox(24, bars);
        if (right != null) row.getChildren().add(right);
        row.setAlignment(Pos.CENTER_LEFT);
        return card(row);
    }

    /** One of moneyBars()'s rows: its name, the bar of its parts, its total. */
    static HBox moneyRow(String name, List<Slice> parts, double total, double scale) {
        Label n = words(name, Palette.SIZE_LABEL, Palette.TEXT_LABEL);
        n.setMinWidth(130);
        n.setPrefWidth(130);
        n.setMaxWidth(130);
        List<Segment> segs = new ArrayList<>();
        for (Slice s : parts) {
            segs.add(new Segment(Math.max(0, s.amount()), s.colour(), false, null, null,
                    s.name() + "\n" + money(s.amount()), null));
        }
        SegmentBar bar = segmentBar(segs, scale > 0 ? scale : 1, List.of(), 0, 16);
        HBox.setHgrow(bar, Priority.ALWAYS);
        Label t = figure(money(total), Palette.SIZE_BODY, Palette.TEXT_HEAD);
        HBox row = new HBox(Palette.GAP, n, bar, t);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** The bottom line beside a books card: a figure, its name and a line under. */
    static VBox bottomLine(String value, String name, String line, String tone) {
        VBox box = new VBox(1, figure(value, 20, tone), words(name, Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        if (line != null) box.getChildren().add(words(line, Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        box.setMinWidth(180);
        box.setMaxWidth(220);
        return box;
    }

    /** "Build for it ›": Build's category opened on this ring, in the building pink - a pill since 0.7.34 (Pieces.doorPill()). */
    HBox buildFor(BuildAdvice.Measure m) {
        return doorPill("Build for it", Icons.BUILD, Palette.BUILDING, () -> ui.buildScreen.openOn(m));
    }

    /** A double[] in one figure - the education arrays are per-type. */
    static double sum(double[] values) {
        if (values == null) return 0;
        double total = 0;
        for (double v : values) total += v;
        return total;
    }

    /* =====================================================================
       HEALTH

       The Overview's question: why are people off sick, and who goes
       without care? The sick rate as one bar of its causes - the floor no
       care takes away, what the missing doctors add, then hunger, no home,
       the unburied, an outbreak and violence - each a door to where it is
       fixed; then the four kinds of care the city runs as cards, each its
       ring (the coverage the month applied), the one thing it buys, and its
       places against the people it serves. Behind them, a page each: the
       long sick, what childcare and senior care buy, the ground.
       ===================================================================== */

    /** The four figures: the sick rate, the thinnest care, the ground (or the unburied), the bill. */
    List<Kpi> healthKpis(List<CityNeeds.Need> all) {
        Health health = ui.game.getHealth();
        Healthcare service = ui.game.getHealthcare();
        double sick = health.getSickRate();
        List<Kpi> out = new ArrayList<>();

        out.add(new Kpi("OFF SICK", String.format("%.1f%%", sick * 100),
                people(ui.game.getPopulationManager().getWorkforce() * sick) + " of the workforce",
                BuildScreen.verdict(CityNeeds.level(sick, CityNeeds.SICK_YELLOW, CityNeeds.SICK_RED, true)),
                "Where the sick rate comes from", () -> open("Health", OVERVIEW),
                "sickRate", ServicesScreen::points));

        /* WHICH ONE IS THINNEST, and named. The old screen printed three
         * coverages and left the comparison to the player; the useful output of
         * three coverages is the smallest of them, because that is the one that
         * names a building. */
        CareType worst = thinnest();
        double cover = service.getCoverage(worst);
        out.add(new Kpi("THINNEST COVER", pct(cover),
                // Nothing to build next when every kind is covered (0.7.20); it read
                // "100% general care - build that next".
                cover >= .995 ? "everything covered" : worst.getLabel().toLowerCase() + " — build that next",
                tone(need(all, CityNeeds.Kind.CARE, worst, null)),
                "Build › Healthcare, on " + worst.getLabel().toLowerCase(),
                () -> ui.buildScreen.openOn(BuildAdvice.Measure.care(worst)), null, null));

        if (service.getUnburied() > 0) {
            out.add(new Kpi("UNBURIED", people(service.getUnburied()), "nowhere to put them",
                    tone(need(all, CityNeeds.Kind.DEAD, null, null)), "Death care",
                    () -> open("Health", "Death care"), "unburied", Money::people));
        } else {
            CityNeeds.Need plots = need(all, CityNeeds.Kind.PLOTS, null, null);
            out.add(new Kpi("GROUND LEFT", groundWords(), groundNote(),
                    tone(plots), "Death care", () -> open("Health", "Death care"), null, null));
        }

        out.add(new Kpi("THE BILL", tightMoney(toDollars(-service.getNetCost())) + "/mo",
                tightMoney(toDollars(service.getFees())) + " back in fees", Palette.TEXT_HEAD,
                "Health's books", () -> open("Health", BOOKS), "healthBill", Money::money));
        return out;
    }

    /** The living care with the least coverage the month applied: general care first of equals. */
    CareType thinnest() {
        CareType worst = CareType.GENERAL;
        double worstCover = 2;
        for (CareType care : new CareType[]{CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR}) {
            double cover = ui.game.getHealthcare().getCoverage(care);
            if (cover < worstCover) { worstCover = cover; worst = care; }
        }
        return worst;
    }

    /**
     * The ground in a few words. It read "plenty" on the strip and "not for
     * centuries" on the page whenever nobody was buried - including a city
     * whose every plot was taken, the crematorium burning the rest
     * (monthsOfPlotsLeft() is the largest double with no burials) - so: no
     * ground, full, not in use, a century and more, or the months.
     */
    String groundWords() {
        Healthcare service = ui.game.getHealthcare();
        double plots = ui.game.getBuildingManager().getCareCapacity(CareType.BURIAL);
        double left = Healthcare.plotsRemaining(plots, service.getPlotsUsed());
        double months = service.monthsOfPlotsLeft(plots);
        if (plots <= 0) return "no ground";
        if (left < .5) return "full";
        if (!(service.getBurials() > 0) || months > 1e9) return "not in use";
        if (months >= 1200) return "100+ years";
        return String.format("%,.0f mo", months);
    }

    /** ...and the line under it. */
    String groundNote() {
        String g = groundWords();
        switch (g) {
            case "no ground":  return "no burial ground built";
            case "full":       return "every plot is taken";
            case "not in use": return "nobody is being buried";
            default:           return "before the plots run out";
        }
    }

    void healthPage(VBox page, List<CityNeeds.Need> all, List<Event> events) {
        switch (servicePage) {
            case "General care" -> generalCarePage(page, all, events);
            case "Childcare"    -> livingCarePage(page, CareType.CHILDCARE, all);
            case "Senior care"  -> livingCarePage(page, CareType.SENIOR, all);
            case "Death care"   -> deathCarePage(page, all, events);
            case BOOKS          -> healthBooksPage(page);
            default             -> healthOverview(page, all, events);
        }
    }

    /* ------------------------------ the Overview ------------------------------ */

    void healthOverview(VBox page, List<CityNeeds.Need> all, List<Event> events) {
        eventLines(page, "Health", events);
        deathNotices(page);

        page.getChildren().add(sectionHead("WHERE THE SICK RATE COMES FROM", SICK_INFO,
                hint("a part opens where it is fixed")));
        page.getChildren().add(sickCard());

        page.getChildren().add(sectionHead("THE CARE THE CITY RUNS", null, careScarceNote()));
        javafx.scene.layout.GridPane cards = equalColumns(4, TILE_GAP);
        int i = 0;
        for (CareType care : new CareType[]{CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR}) {
            cards.add(careCard(careWords(care, all)), i++, 0);
        }
        cards.add(careCard(deathWords(all)), i, 0);
        page.getChildren().add(cards);

        page.getChildren().add(details("health:sick", "the sick rate, line by line", sickStatement()));
    }

    /** The sick rate's (i): what the bar is, its floor and its cap, and why it is a cost of output and not of wages. */
    static final String SICK_INFO = String.format(
            "The bar is today's sick rate split into what makes it, in points of the workforce. "
            + "With nobody covered by general care the rate is %.0f%%; with everybody covered it settles "
            + "near %.0f%%, the floor no care takes away. Hunger, no home, the unburied, an outbreak and "
            + "violent crime add their own points on top.%n%n"
            + "They stay on the payroll and still get paid — what the city loses is their output, not "
            + "their wages. Capped at %.0f%%.",
            Health.UNTREATED_RATE * 100, Health.WELL_SERVED_RATE * 100, Health.MAX_SICK_RATE * 100);

    /**
     * The sick rate's parts, in points, in the order the bar draws them -
     * the floor, what the missing doctors add, hunger, no home, the
     * unburied, an outbreak, violence - and anything left over, hatched:
     * Health's own figures, the floor its constant (the doctors' part is
     * the rate from coverage less it). A part with a door opens where it is
     * fixed. AND WHATEVER IS LEFT OVER, said out loud: Health adds its parts
     * and caps the sum, so a breakdown that does not reconcile is a
     * breakdown that is lying (the trimmed part is sickTrimmed()).
     */
    List<Part> sickParts() {
        Health h = ui.game.getHealth();
        double floor = Math.min(Health.WELL_SERVED_RATE, h.getBaselineRate());
        List<Part> parts = new ArrayList<>();
        parts.add(Part.of("the floor", floor, Palette.RAMP_REST)
                .tip("the floor\n" + points(floor) + "\nWhat the rate settles near with everybody covered: no care takes it away."));
        parts.add(Part.of("no doctor", h.getBaselineRate() - floor, Palette.MONEY)
                .go(() -> open("Health", "General care")));
        parts.add(Part.of("hunger", h.getHungerRate(), Palette.PEOPLE)
                .go(() -> ui.peopleScreen.showHouseholdMenu()));
        parts.add(Part.of("no home", h.getUnhousedRate(), Palette.ORE)
                .go(() -> ui.buildScreen.openCategory(BuildAdvice.HOMES)));
        parts.add(Part.of("the unburied", h.getUnburiedRate(), Palette.BUILDING)
                .go(() -> open("Health", "Death care")));
        parts.add(Part.of("outbreak", h.getOutbreakSeverity(), Palette.BUSINESS)
                .tip("outbreak\n" + (h.isOutbreak() ? points(h.getOutbreakSeverity())
                        + "\nIt fades on its own over a few months." : "none")));
        parts.add(Part.of("violence", h.getInjuryRate(), Palette.PEOPLE_DARK)
                .go(() -> open("Safety", OVERVIEW)));
        double gap = h.getSickRate() - named(h);
        if (gap > .0005) parts.add(Part.of("not accounted for", gap, Palette.RAMP_REST).leftOver());
        return parts;
    }

    /** Health's named parts, added: what the old breakdown summed. */
    static double named(Health h) {
        return h.getBaselineRate() + h.getOutbreakSeverity() + h.getUnburiedRate() + h.getHungerRate()
                + h.getUnhousedRate() + h.getInjuryRate();
    }

    /** What the cap took off the parts, as a positive rate; 0 when it took nothing. */
    double sickTrimmed() {
        Health h = ui.game.getHealth();
        double gap = h.getSickRate() - named(h);
        return gap < -.0005 ? -gap : 0;
    }

    /** The sick rate's card: the rate itself, big, in OFF SICK's colour, and the bar of its causes. */
    HBox sickCard() {
        Health h = ui.game.getHealth();
        double sick = h.getSickRate();
        String tone = BuildScreen.verdict(CityNeeds.level(sick, CityNeeds.SICK_YELLOW, CityNeeds.SICK_RED, true));
        VBox left = new VBox(2, figure(String.format("%.1f%%", sick * 100), 30, tone),
                words("off sick · " + people(ui.game.getPopulationManager().getWorkforce() * sick) + " workers",
                        Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        String d = change("sickRate", ServicesScreen::points);
        if (d != null) left.getChildren().add(words(d, Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        left.setMinWidth(210);
        left.setPrefWidth(210);
        left.setMaxWidth(210);

        VBox right = new VBox(4, causeBar(sickParts(), 0, 24, ServicesScreen::points));
        HBox.setHgrow(right, Priority.ALWAYS);
        right.setMaxWidth(Double.MAX_VALUE);
        double trimmed = sickTrimmed();
        if (trimmed > 0) {
            right.getChildren().add(words("The parts come to " + points(sick + trimmed) + "; the cap at "
                    + pct(Health.MAX_SICK_RATE) + " trimmed " + points(trimmed) + ".", Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        }
        HBox row = new HBox(18, left, right);
        row.setAlignment(Pos.TOP_LEFT);
        VBox c = card(row);
        HBox holder = new HBox(c);
        HBox.setHgrow(c, Priority.ALWAYS);
        return holder;
    }

    /** The old breakdown, every line as it was: the fold under the bar. */
    VBox sickStatement() {
        Health health = ui.game.getHealth();
        VBox col = column();
        col.getChildren().add(statementLine("Baseline, from coverage",
                String.format("%.1f pts", health.getBaselineRate() * 100)));
        col.getChildren().add(statementLine("Outbreak",
                health.isOutbreak() ? String.format("%.1f pts", health.getOutbreakSeverity() * 100) : "none",
                health.isOutbreak() ? Palette.BAD : Palette.TEXT_SPENT));
        col.getChildren().add(statementLine("The unburied",
                health.getUnburiedRate() > 0 ? String.format("%.1f pts", health.getUnburiedRate() * 100) : "none",
                health.getUnburiedRate() > 0 ? Palette.BAD : Palette.TEXT_SPENT));
        col.getChildren().add(statementLine("Hunger",
                health.getHungerRate() > 0 ? String.format("%.1f pts", health.getHungerRate() * 100) : "none",
                health.getHungerRate() > 0 ? Palette.BAD : Palette.TEXT_SPENT));
        col.getChildren().add(statementLine("No home",
                health.getUnhousedRate() > 0 ? String.format("%.1f pts", health.getUnhousedRate() * 100) : "none",
                health.getUnhousedRate() > 0 ? Palette.BAD : Palette.TEXT_SPENT));
        // ...and the injured, since the police (2026-09-11).
        col.getChildren().add(statementLine("Hurt by violent crime",
                health.getInjuryRate() > 0 ? String.format("%.2f pts", health.getInjuryRate() * 100) : "none",
                health.getInjuryRate() > .0005 ? Palette.WARN : Palette.TEXT_SPENT));
        double gap = health.getSickRate() - named(health);
        if (Math.abs(gap) > .0005) {
            col.getChildren().add(statementLine(gap > 0 ? "Not accounted for" : "Trimmed by the cap",
                    String.format("%+.1f pts", gap * 100), Palette.WARN));
        }
        double sick = health.getSickRate();
        col.getChildren().add(statementTotal("Off sick", String.format("%.1f%%", sick * 100),
                BuildScreen.verdict(CityNeeds.level(sick, CityNeeds.SICK_YELLOW, CityNeeds.SICK_RED, true))));
        return col;
    }

    /** At the right of the care's heading: which of the city's healthcare buildings' staff it fills worst (Build's note, on what stands). */
    Node careScarceNote() {
        return hint(careScarceWords());
    }

    /** ...its words. */
    String careScarceWords() {
        BuildingManager bm = ui.game.getBuildingManager();
        List<BuildingsTemplate> standing = new ArrayList<>();
        for (BuildingsTemplate t : bm.getTemplatesByCategory(java.util.EnumSet.of(BuildingType.HEALTHCARE))) {
            if (bm.getQuantity(t.getId()) > 0) standing.add(t);
        }
        if (standing.isEmpty()) return "nothing built yet: the founding doctor, nursery, almshouse and churchyard";
        return ui.buildScreen.scarceWords(standing, ui.game.getPopulationManager().getJobFillRate());
    }

    /* ------------------------------- the care cards ------------------------------- */

    /**
     * What one care card says, worked out without drawing it (a probe reads
     * it): its ring - the coverage the month applied, Healthcare.getCoverage(),
     * in its NEEDS YOU row's colour - the one thing it buys and that
     * figure's range, its places (to serve, built, staffed) and the line
     * under them, its (i), the ring Build opens on, and its page.
     */
    record CareWords(String name, String icon, String ring, double arc, String tone, String buys, String buysWords,
                     String range, double serve, double built, double staffed, String caption, String info,
                     BuildAdvice.Measure measure, String page) { }

    CareWords careWords(CareType care, List<CityNeeds.Need> all) {
        Healthcare service = ui.game.getHealthcare();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] fill = ui.game.getPopulationManager().getJobFillRate();
        double served = care.populationServed(ui.game.getCohorts());
        double built = bm.getCareCapacity(care);
        double staffed = bm.getStaffedCareCapacity(care, fill);
        double cover = service.getCoverage(care);
        String tone = tone(need(all, CityNeeds.Kind.CARE, care, null));
        String toServe = care == CareType.SENIOR ? " places needed" : " to serve";
        String caption = people(staffed) + " staffed of " + people(built) + " built · " + people(served) + toServe;
        String buys, buysWords, range, icon, page;
        switch (care) {
            case CHILDCARE: {
                buys = times(Healthcare.mortalityFactor(AgeBand.BABY, cover, .5, 0));
                buysWords = " infant deaths";
                range = times(Healthcare.mortalityFactor(AgeBand.BABY, 0, .5, 0)) + " → "
                        + times(Healthcare.mortalityFactor(AgeBand.BABY, 1, .5, 0)) + " if everyone";
                icon = Icons.CHILD;
                page = "Childcare";
                break;
            }
            case SENIOR: {
                buys = String.format("%+.1f%%", (Migration.seniorCarePull(cover) - 1) * 100);
                buysWords = " more newcomers";
                range = String.format("%+.0f%% → %+.0f%% if everyone",
                        (Migration.seniorCarePull(0) - 1) * 100, (Migration.seniorCarePull(1) - 1) * 100);
                icon = Icons.CANE;
                page = "Senior care";
                break;
            }
            default: {
                buys = pct(ui.game.getSickness().getLastRecovery());
                buysWords = " of the sick get better";
                range = pct(Sickness.RECOVERY_UNTREATED) + " → " + pct(Sickness.RECOVERY_SERVED) + " if everyone";
                icon = Icons.HEALTH;
                page = "General care";
            }
        }
        return new CareWords(care.getLabel(), icon, pct(cover), cover, tone, buys, buysWords, range,
                served, built, staffed, caption, careInfo(care, cover), BuildAdvice.Measure.care(care), page);
    }

    /** A factor, as the care pages write one: "×36.5", "×0.025". */
    static String times(double factor) {
        if (!Double.isFinite(factor)) return "—";
        double a = Math.abs(factor);
        return "×" + (a >= 10 ? String.format("%.1f", factor) : a >= .1 ? String.format("%.2f", factor) : String.format("%.3f", factor));
    }

    /** Who a kind of care serves, and what its ring is (the old page's three notes, and the month's coverage). */
    String careInfo(CareType care, double cover) {
        String who = care == CareType.CHILDCARE ? "Babies and children — the band it is measured against."
                : care == CareType.SENIOR ? seniorNeedWords()
                : "Everybody in the city. General care is not means-tested or age-banded.";
        return who + "\n\nThe ring is the coverage the month applied, " + pct(cover) + ": the staffed places "
                + "less anybody the fee turned away. A place with no staff treats nobody — check the "
                + "professions table on the People screen.";
    }

    /**
     * SENIOR CARE'S DENOMINATOR IS PLACES, not people (0.7.28; the page read
     * "People to serve" over a figure of places): a senior needs a fifth of
     * a place, an elder a whole one (CareType.placesPerHead()).
     */
    String seniorNeedWords() {
        PopulationCohorts cohorts = ui.game.getCohorts();
        return String.format("Everybody over the retirement age, in places: a senior (70-84) needs %.2f of a "
                + "place, an elder (85 and over) a whole one. This city has %s seniors and %s elders, "
                + "so it needs %s places.",
                CareType.SENIOR.placesPerHead(AgeBand.SENIOR), people(cohorts.get(AgeBand.SENIOR)),
                people(cohorts.get(AgeBand.ELDER)), people(CareType.SENIOR.populationServed(cohorts)));
    }

    /** Death care's card: the dead dealt with as its ring, the ground left as what it buys, the plots as its bar. */
    CareWords deathWords(List<CityNeeds.Need> all) {
        Healthcare service = ui.game.getHealthcare();
        double plots = ui.game.getBuildingManager().getCareCapacity(CareType.BURIAL);
        double used = service.getPlotsUsed();
        double free = Healthcare.plotsRemaining(plots, used);
        double ratio = service.getDeathCareRatio();
        BuildAdvice.Measure m = BuildAdvice.Measure.of(BuildAdvice.Kind.DEATH);
        CityNeeds.Need n = BuildAdvice.needFor(all, m);
        String g = groundWords();
        String buys = g.endsWith(" mo") ? g.substring(0, g.length() - 3) + " months" : g;
        return new CareWords("Death care", Icons.DEATH, pct(ratio), ratio, tone(n), buys,
                g.endsWith(" mo") || g.equals("100+ years") ? " of ground" : "",
                flowText(service.getDeaths()) + " died · " + pct(ratio) + " dealt with",
                0, plots, used, people(used) + " used of " + people(plots) + " plots · " + people(free) + " free",
                BURIAL_INFO, m, "Death care");
    }

    /** The burial choice (the death care page's note). */
    static final String BURIAL_INFO = "A household that can save a plot's price over ten years chooses burial; "
            + "the ones that cannot are cremated. Which is to say the split is set by how much your households "
            + "have left over, not by anything on this screen.\n\nThe ring is how much of this month's dead "
            + "the city dealt with; the bar is the burial ground, the plots used inside the plots built.";

    /** One care card: head, ring and what it buys, its places as a supply bar, and its two doors. */
    VBox careCard(CareWords w) {
        VBox body = new VBox(1, new javafx.scene.text.TextFlow(textRun(w.buys(), 15, Palette.TEXT_HEAD, true),
                        textRun(w.buysWords(), Palette.SIZE_LABEL, Palette.TEXT_LABEL, false)),
                words(w.range(), Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        body.setMinWidth(0);
        HBox.setHgrow(body, Priority.ALWAYS);
        HBox middle = new HBox(10, ring(w.arc(), w.tone(), 64, 6, w.ring(), 13), body);
        middle.setAlignment(Pos.CENTER_LEFT);

        SegmentBar bar = supplyBar(w.serve(), w.built(), w.staffed(), Palette.PEOPLE, null, null, w.caption(), 0, 8);
        Region push = new Region();
        HBox.setHgrow(push, Priority.ALWAYS);
        HBox foot = new HBox(Palette.GAP, door("details", Palette.ACCENT, () -> open("Health", w.page())), push,
                buildFor(w.measure()));
        foot.setAlignment(Pos.CENTER_LEFT);
        Region grow = new Region();
        VBox.setVgrow(grow, Priority.ALWAYS);
        VBox c = card(cardHead(w.icon(), w.name(), w.info(), null), middle, bar,
                words(w.caption(), Palette.SIZE_CAPTION, Palette.TEXT_MUTED), grow, foot);
        c.setMaxHeight(Double.MAX_VALUE);
        return doorCard(c, () -> open("Health", w.page()), w.name() + ": click for its page.");
    }

    /** A run of text in a flow: a figure in mono, or words. */
    static javafx.scene.text.Text textRun(String text, double size, String tone, boolean mono) {
        javafx.scene.text.Text t = new javafx.scene.text.Text(text);
        t.setStyle((mono ? Palette.Fonts.monoSemiBold() : "-fx-font-family: " + Palette.Fonts.sans() + ";")
                + " -fx-font-size: " + size + "px; -fx-fill: " + tone + ";");
        return t;
    }

    /* ------------------------------- general care ------------------------------- */

    /**
     * General care: how many of the sick does care cure, and how many stay
     * ill long enough to die? THE LONG SICK (2026-09-11): general care no
     * longer scales the adults' death rate; it saves them by curing them
     * before they have been ill two months. So the page leads with the sick
     * by how long they have been ill, and what care cures.
     */
    void generalCarePage(VBox page, List<CityNeeds.Need> all, List<Event> events) {
        Healthcare service = ui.game.getHealthcare();
        Sickness sickness = ui.game.getSickness();
        PopulationCohorts cohorts = ui.game.getCohorts();
        for (Event e : events) if (e.key().startsWith("outbreak:")) page.getChildren().add(eventLine(e.words(), e.whole(), e.tone()));
        if (ui.game.getHealth().isOutbreak() && events.stream().noneMatch(e -> e.key().startsWith("outbreak:"))) {
            page.getChildren().add(eventLine("An outbreak is running: " + points(ui.game.getHealth().getOutbreakSeverity())
                    + " on the sick rate.", outbreakWhole(), Palette.BAD));
        }

        page.getChildren().add(sectionHead("THE SICK, BY HOW LONG THEY HAVE BEEN ILL", LONG_SICK_INFO,
                hint("from two months on, they can die of it")));
        double[] slots = longSick();
        VBox left = new VBox(2,
                figure(people(sickness.peopleSick(cohorts)), 22, Palette.TEXT_HEAD),
                words("sick this month", Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                figure(people(sickness.peoplePastTwoMonths(cohorts)), 15, Palette.TEXT_HEAD),
                words("past two months", Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                figure(flowText(sickness.getLastDeaths()), 15, Palette.TEXT_HEAD),
                words("died of it last month", Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        left.setMinWidth(230);
        left.setPrefWidth(230);
        CohortBars bars = cohortBars(slots, 0, 120, Palette.PEOPLE, Sickness.DEADLY_FROM, Palette.PEOPLE_DARK,
                "can die from here", "under a month", "a year or more", Money::people,
                k -> k == Sickness.RING - 1 ? "ill a year or more" : k == 0 ? "ill under a month" : "ill " + k + (k == 1 ? " month" : " months"));
        HBox.setHgrow(bars, Priority.ALWAYS);
        HBox top = new HBox(18, left, bars);
        top.setAlignment(Pos.CENTER_LEFT);
        page.getChildren().add(card(top));

        double cover = service.getCoverage(CareType.GENERAL);
        javafx.scene.layout.GridPane two = equalColumns(2, TILE_GAP);
        VBox cures = card(cardHead(Icons.HEALTH, "What care cures", RECOVERY_INFO, null),
                effectScale("", sickness.getLastRecovery(), Sickness.RECOVERY_UNTREATED, Sickness.RECOVERY_SERVED,
                        false, ServicesScreen::pct, Palette.PEOPLE, 0,
                        words(pct(cover) + " covered", Palette.SIZE_LABEL, Palette.TEXT_LABEL), 90),
                words("of the sick get better each month: " + pct(Sickness.RECOVERY_UNTREATED)
                        + " with nobody covered, " + pct(Sickness.RECOVERY_SERVED) + " with everybody.",
                        Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        two.add(cures, 0, 0);
        two.add(card(cardHead(Icons.STAFF, "Who gets a doctor", careInfo(CareType.GENERAL, cover), null),
                careFunnel(CareType.GENERAL)), 1, 0);
        page.getChildren().add(two);

        page.getChildren().add(feeLine(CareType.GENERAL, "a visit"));
        page.getChildren().add(details("health:general", "death chances by age, and the sick rate",
                deathChances(), column(statementLine("The sick rate",
                        String.format("%.1f%%", ui.game.getHealth().getSickRate() * 100)),
                        statementNote(String.format("With nobody covered it is %.0f%%; fully covered it settles near %.0f%%.",
                                Health.UNTREATED_RATE * 100, Health.WELL_SERVED_RATE * 100)))));
    }

    /** The long sick, a bar a slot: everybody ill under a month to a year or more, in people (Sickness.peopleInSlot()). */
    double[] longSick() {
        double[] out = new double[Sickness.RING];
        for (int k = 0; k < out.length; k++) out[k] = ui.game.getSickness().peopleInSlot(ui.game.getCohorts(), k);
        return out;
    }

    /** The long sick's (i): who can die of it, at what chance by age (the elders' too, which the old note left out). */
    static final String LONG_SICK_INFO = String.format(
            "Everybody sick this month, by how long they have been ill: the left bar fell ill this month, "
            + "the right one has been ill a year or more. Each month the bars move one to the right, less "
            + "whoever got better.%n%nAnyone ill for more than two months can die of it: %.0f%% a month for "
            + "babies and seniors, %.0f%% for elders, %.0f%% for adults, %.0f%% for children and teenagers.",
            Sickness.deathChance(AgeBand.BABY) * 100, Sickness.deathChance(AgeBand.ELDER) * 100,
            Sickness.deathChance(AgeBand.ADULT) * 100, Sickness.deathChance(AgeBand.CHILD) * 100);

    /** What care cures, in words. */
    static final String RECOVERY_INFO = String.format(
            "The share of the sick who get better in a month: %.0f%% with no general care, %.0f%% with "
            + "everybody covered, and in between by the coverage the month applied. The rest stay ill "
            + "another month; from two months on they can die of it.",
            Sickness.RECOVERY_UNTREATED * 100, Sickness.RECOVERY_SERVED * 100);

    /** The death chances by age, with the share of each band past two months and its dead last month. */
    javafx.scene.layout.GridPane deathChances() {
        Sickness sickness = ui.game.getSickness();
        javafx.scene.layout.GridPane table = grid(new double[] {110, 80, 110, 110}, rightAfterFirst(4));
        gridHead(table, "age", "chance", "past two months", "died last month");
        int row = 1;
        for (AgeBand b : AgeBand.values()) {
            table.add(gridCell(b.getLabel(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, row);
            table.add(gridCell(String.format("%.0f%%", Sickness.deathChance(b) * 100), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 1, row);
            table.add(gridCell(String.format("%.1f%%", sickness.pastTwoMonths(b) * 100), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 2, row);
            table.add(gridCell(flowText(sickness.getLastDeaths(b)), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, row);
            row++;
        }
        return table;
    }

    /**
     * Who a kind of care reaches, as a funnel: the people (or, for senior
     * care, the places) it is measured against, the places built, the places
     * staffed, and those it treated - with the people the fee turned away as
     * a step when there are any. Healthcare.getServed() is the month's, and
     * is not kept by a save: until a month runs after a load, the last step
     * says so rather than nought.
     */
    VBox careFunnel(CareType care) {
        BuildingManager bm = ui.game.getBuildingManager();
        double served = care.populationServed(ui.game.getCohorts());
        double staffed = bm.getStaffedCareCapacity(care, ui.game.getPopulationManager().getJobFillRate());
        VBox box = funnel(careSteps(care), 0, -1, Palette.PEOPLE);
        double built = bm.getCareCapacity(care);
        /*
         * STAFFED, NOT BUILT, and the gap is worth its own line. Pouring
         * concrete was the one way in this game to get output for free, and a
         * player who has built the wards and seen nothing change has no other
         * way to find out that nobody is in them.
         */
        if (built - staffed > .5) {
            box.getChildren().add(infoLine(people(built - staffed) + " places stand empty for want of staff",
                    "A ward with no doctors treats nobody — check the professions table on the People screen.",
                    false, Palette.SIZE_LABEL, Palette.TEXT_HEAD, 560));
        }
        double gap = Math.max(0, served - staffed);
        box.getChildren().add(words(gap >= .5 ? "short by " + people(gap) + (care == CareType.SENIOR ? " places" : " people")
                : "enough places for everyone", Palette.SIZE_LABEL, gap >= .5 ? Palette.TEXT_HEAD : Palette.TEXT_LABEL));
        return box;
    }

    /** ...its steps, worked out without drawing them. */
    List<FunnelStep> careSteps(CareType care) {
        Healthcare service = ui.game.getHealthcare();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] fill = ui.game.getPopulationManager().getJobFillRate();
        double served = care.populationServed(ui.game.getCohorts());
        double built = bm.getCareCapacity(care);
        double staffed = bm.getStaffedCareCapacity(care, fill);
        double priced = service.getPricedOut(care);
        boolean measured = !healthNotRunYet();
        List<FunnelStep> steps = new ArrayList<>();
        steps.add(new FunnelStep(care == CareType.SENIOR ? "places needed" : "to serve", served, people(served), null));
        steps.add(new FunnelStep("places built", built, people(built), null));
        steps.add(new FunnelStep("staffed", staffed, people(staffed),
                built - staffed > .5 ? people(built - staffed) + " places stand empty for want of staff" : null));
        if (priced > .5) steps.add(new FunnelStep("priced out", priced, people(priced),
                "turned away at the door for want of the fee"));
        steps.add(new FunnelStep("treated", measured ? service.getServed(care) : 0,
                measured ? people(service.getServed(care)) : "after a month", measured ? null
                : "Not measured yet: a loaded city counts who was treated when a month runs."));
        return steps;
    }

    /**
     * True when a save has been loaded and no month has run since: care that
     * stands treated nobody, by the count (Healthcare's served[] is not
     * saved), while the fees it raised are. The Health books read "—" for it.
     */
    boolean healthNotRunYet() {
        Healthcare service = ui.game.getHealthcare();
        double seen = 0;
        for (CareType care : new CareType[]{CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR}) seen += service.getServed(care);
        return seen <= 0 && service.getTreatmentFees() > 0;
    }

    /** "fee $10 a visit · Set the fee ›": the price of a kind of care, and the dial (Policy › Promises › Health). */
    HBox feeLine(CareType care, String per) {
        Healthcare service = ui.game.getHealthcare();
        return priceLine("fee " + cash(service.feeNow(care)) + " " + per + " · fees at "
                        + String.format("x%.2f", service.getFeeScale()) + " the founding fee",
                String.format("Fees cover %.0f%% of what care costs. The city funds the rest — it runs at a "
                        + "deficit by design, and the return is on the People screen rather than on this page.",
                        service.getCostRecovery() * 100),
                "Set the fee", "Health");
    }

    /* ---------------------------- childcare and senior care ---------------------------- */

    /**
     * Childcare or senior care: what does it buy? Each effect as a scale
     * from nobody covered to everybody, today marked - the model's own
     * factors at the coverage the month applied, and at none and at all -
     * then who it reaches, and its fee. THE SAME PAGE TWICE on purpose, as
     * it always was: the two differ in their denominator and in what they
     * change, and in nothing else.
     */
    void livingCarePage(VBox page, CareType care, List<CityNeeds.Need> all) {
        double c = ui.game.getHealthcare().getCoverage(care);
        page.getChildren().add(sectionHead("WHAT " + care.getLabel().toUpperCase() + " BUYS", null,
                hint(pct(c) + " covered: the dot is today, the ends nobody covered and everybody")));
        VBox scales = new VBox(10);
        if (care == CareType.CHILDCARE) {
            scales.getChildren().add(scaleRow("Infant deaths", Healthcare.mortalityFactor(AgeBand.BABY, c, .5, 0),
                    Healthcare.mortalityFactor(AgeBand.BABY, 0, .5, 0), Healthcare.mortalityFactor(AgeBand.BABY, 1, .5, 0),
                    true, "The largest single effect in the health model — an uncovered city loses babies at many "
                    + "times the rate a covered one does."));
            scales.getChildren().add(scaleRow("How often babies fall ill", 1 + Sickness.EXTRA_SICKNESS * (1 - c),
                    1 + Sickness.EXTRA_SICKNESS, 1, false,
                    "Babies get sick twice as often as everybody else with no childcare, and no more often with "
                    + "enough of it — and a baby ill for more than two months can die of it."));
            scales.getChildren().add(scaleRow("Births", Healthcare.birthFactor(c), Healthcare.birthFactor(0),
                    Healthcare.birthFactor(1), false, "Nurseries make a city somewhere people are willing to have children."));
        } else {
            scales.getChildren().add(scaleRow("Senior deaths", Healthcare.mortalityFactor(AgeBand.SENIOR, 0, .5, c),
                    Healthcare.mortalityFactor(AgeBand.SENIOR, 0, .5, 0), Healthcare.mortalityFactor(AgeBand.SENIOR, 0, .5, 1),
                    false, "The seniors' death rate (70-84), as a multiple of the band's own rate - what half covered gives it."));
            scales.getChildren().add(scaleRow("Elder deaths", Healthcare.mortalityFactor(AgeBand.ELDER, 0, .5, c),
                    Healthcare.mortalityFactor(AgeBand.ELDER, 0, .5, 0), Healthcare.mortalityFactor(AgeBand.ELDER, 0, .5, 1),
                    false, "The elders' death rate (85 and over), which senior care moves further than the seniors'."));
            scales.getChildren().add(scaleRow("How often seniors fall ill", 1 + Sickness.EXTRA_SICKNESS * (1 - c),
                    1 + Sickness.EXTRA_SICKNESS, 1, false,
                    "Seniors and elders get sick twice as often as everybody else with no senior care, and no more "
                    + "often with enough of it."));
            HBox pull = effectScale("Draw on newcomers", Migration.seniorCarePull(c), Migration.seniorCarePull(0),
                    Migration.seniorCarePull(1), false, v -> String.format("%+.1f%%", (v - 1) * 100), Palette.PEOPLE, 220,
                    infoLine(String.format("%+.1f%% now", (Migration.seniorCarePull(c) - 1) * 100),
                            "Somewhere to grow old is a reason to move here. It is the only care type that pulls "
                            + "people rather than just keeping them alive.", false, Palette.SIZE_BODY, Palette.TEXT_HEAD, 250), 250);
            scales.getChildren().add(pull);
        }
        page.getChildren().add(card(scales));

        javafx.scene.layout.GridPane two = equalColumns(2, TILE_GAP);
        two.add(card(cardHead(care == CareType.CHILDCARE ? Icons.CHILD : Icons.CANE, "Who it reaches",
                careInfo(care, c), null), careFunnel(care)), 0, 0);
        page.getChildren().add(two);
        page.getChildren().add(feeLine(care, "a month"));
        page.getChildren().add(details("health:" + care.name(), "the places, as a statement", careStatement(care)));
    }

    /** One effect: its name, the scale, and today's figure with the old note behind its (i). */
    HBox scaleRow(String label, double now, double atNone, double atAll, boolean log, String whole) {
        return effectScale(label, now, atNone, atAll, log, ServicesScreen::times, Palette.PEOPLE, 220,
                infoLine(times(now) + " now", whole, false, Palette.SIZE_BODY, Palette.TEXT_HEAD, 250), 250);
    }

    /** The old page's first block - covers, the people or places it serves, built, staffed, short by - as it was. */
    VBox careStatement(CareType care) {
        Healthcare service = ui.game.getHealthcare();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();
        double served = care.populationServed(ui.game.getCohorts());
        double built = bm.getCareCapacity(care);
        double staffed = bm.getStaffedCareCapacity(care, staffing);
        double cover = service.getCoverage(care);
        VBox col = column(statementHead(care.getLabel()),
                statementLine("Covers", pct(cover)),
                statementLine(care == CareType.SENIOR ? "Places needed" : "People to serve", people(served)),
                statementLine("Places built", people(built)),
                statementLine("Places staffed", people(staffed), staffed < built * .95 ? Palette.WARN : null));
        if (built - staffed > .5) {
            col.getChildren().add(statementNote(String.format(
                    "%s places stand empty for want of staff. A ward with no doctors "
                    + "treats nobody — check the professions table on the People screen.",
                    people(built - staffed))));
        }
        col.getChildren().add(statementTotal("Short by",
                served - staffed < .5 ? "nothing" : people(Math.max(0, served - staffed)), null));
        return col;
    }

    /* ------------------------------- death care ------------------------------- */

    /**
     * Death care, which is the one service in the game that is a STOCK.
     *
     * A power station short of demand is short every month until somebody
     * builds another. A cemetery is completely fine right up to the month it is
     * full and then permanently useless, so the warning that matters is the one
     * that arrives before, and it is a countdown rather than a level.
     */
    void deathCarePage(VBox page, List<CityNeeds.Need> all, List<Event> events) {
        Healthcare service = ui.game.getHealthcare();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();
        for (Event e : events) {
            if (e.key().startsWith("ground:") || e.key().startsWith("unburied:")) {
                page.getChildren().add(eventLine(e.words(), e.whole(), e.tone()));
            }
        }
        deathNotices(page);

        double plots = bm.getCareCapacity(CareType.BURIAL);
        double used = service.getPlotsUsed();
        double left = Healthcare.plotsRemaining(plots, used);
        double ovens = bm.getStaffedCareCapacity(CareType.CREMATION, staffing);

        javafx.scene.layout.GridPane two = equalColumns(2, TILE_GAP);
        String g = groundWords();
        VBox ground = card(cardHead(Icons.DEATH, "The ground", PLOTS_INFO, null),
                new HBox(8, figure(g.endsWith(" mo") ? g.replace(" mo", " months") : g, 22, Palette.TEXT_HEAD),
                        words(g.endsWith(" mo") || g.equals("100+ years") ? "left at this rate" : groundNote(),
                                Palette.SIZE_LABEL, Palette.TEXT_LABEL)),
                supplyBar(0, plots, used, Palette.PEOPLE, null, null,
                        people(used) + " plots used of " + people(plots), 0, 10),
                words(people(left) + " plots free · " + flowText(service.getBurials()) + " buried a month · "
                        + pct(service.getPlotUtilisation()) + " full", Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        two.add(ground, 0, 0);
        List<Part> dead = List.of(Part.of("buried", service.getBurials(), Palette.PEOPLE),
                Part.of("cremated", service.getCremations(), Palette.PEOPLE_LIGHT),
                Part.of("lying unburied", service.getUnburied(), Palette.BUILDING));
        VBox month = card(cardHead(Icons.DEATH, "This month's dead", BURIAL_INFO, null),
                new HBox(8, figure(flowText(service.getDeaths()), 22, Palette.TEXT_HEAD),
                        words("died · " + String.format("%.1f%%", service.getDeathCareRatio() * 100) + " dealt with",
                                Palette.SIZE_LABEL, Palette.TEXT_LABEL)),
                causeBar(dead, 0, 14, Pieces::flowText));
        two.add(month, 1, 0);
        page.getChildren().add(two);

        page.getChildren().add(sectionHead("THE CREMATORIA", CREMATORIA_INFO, hint(people(ovens) + " a month · "
                + pct(service.getCremationUtilisation()) + " used")));
        page.getChildren().add(card(supplyBar(0, ovens, service.getCremations(), Palette.PEOPLE, null, null,
                flowText(service.getCremations()) + " cremated of " + people(ovens) + " the staffed ovens take", 0, 8)));

        page.getChildren().add(priceLine("a burial " + cash(service.feeNow(CareType.BURIAL)) + " · a cremation "
                        + cash(service.feeNow(CareType.CREMATION)) + " · funeral fees "
                        + tightMoney(toDollars(service.getFuneralFees()), false) + " this month",
                "The funeral fees are not on the dial: the Policy tab's Health page scales the three care fees, and the "
                + "cemetery against the crematorium is its own design.",
                "Set the fee", "Health"));
        page.getChildren().add(details("health:death", "the dead and the ground, as a statement", deathStatement()));
    }

    /** Plots are permanent (the ground's note). */
    static final String PLOTS_INFO = "Plots are consumed permanently — the land never comes back, and a cemetery "
            + "cannot be un-filled. This is the only capacity in the game that is spent rather than used.";

    /** The crematoria's note. */
    static final String CREMATORIA_INFO = "A rate rather than a stock, and it needs almost no land — which makes it "
            + "the answer when the ground has run out and the answer when it is about to.";

    /** The two alerts the death care page always had - nowhere to bury them, filling up - as lines, on that page and on Health's Overview. */
    void deathNotices(VBox page) {
        Healthcare service = ui.game.getHealthcare();
        Health health = ui.game.getHealth();
        double plots = ui.game.getBuildingManager().getCareCapacity(CareType.BURIAL);
        double left = Healthcare.plotsRemaining(plots, service.getPlotsUsed());
        if (service.getUnburied() > 0) {
            page.getChildren().add(eventLine(String.format("Nowhere to bury them: %s people are lying unburied, adding %s to the sick rate.",
                    people(service.getUnburied()), points(health.getUnburiedRate())), String.format(
                    "%s people are lying unburied, adding %.1f points to the sick rate — "
                    + "a health problem, not a decency one, and it does not go away on its "
                    + "own. %s The backlog stops growing at %d months' worth, which is a cap "
                    + "on the bookkeeping and not on the damage.",
                    people(service.getUnburied()), health.getUnburiedRate() * 100,
                    left <= 0 && plots > 0
                            ? "Every plot in the city is full, so a cemetery or a crematorium."
                            : "Build a cemetery or a crematorium.",
                    Healthcare.MAX_BACKLOG_MONTHS), Palette.BAD));
        } else if (service.isStrained()) {
            page.getChildren().add(eventLine("Death care is filling up.",
                    service.getCremationUtilisation() >= Healthcare.STRAINED
                        ? String.format("The crematoria are at %.0f%% of what they can handle. "
                                + "Past full, the overflow has nowhere to go but the ground.",
                                service.getCremationUtilisation() * 100)
                        : String.format("The ground runs out in about %,.0f months at this "
                                + "rate, and it is fine until the month it is not.",
                                service.monthsOfPlotsLeft(plots)), Palette.WARN));
        }
    }

    /** The outbreak's (i), as the old alert said it. */
    String outbreakWhole() {
        Health health = ui.game.getHealth();
        return String.format("It began in %s and is adding %.1f points on top of the usual %.1f%%. "
                + "It fades on its own over a few months. Hospitals make an outbreak "
                + "milder rather than shorter — coverage cuts it by up to %.0f%%.",
                CityCalendar.format(health.getOutbreakStarted()), health.getOutbreakSeverity() * 100,
                health.getBaselineRate() * 100, Health.OUTBREAK_MITIGATION * 100);
    }

    /** The old page's three blocks - this month, the ground, the crematoria - as they were, but for the ground's words. */
    VBox deathStatement() {
        Healthcare service = ui.game.getHealthcare();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();
        double plots = bm.getCareCapacity(CareType.BURIAL);
        double ovens = bm.getStaffedCareCapacity(CareType.CREMATION, staffing);
        double left = Healthcare.plotsRemaining(plots, service.getPlotsUsed());
        return column(statementHead("This month"),
                statementLine("Died", flowText(service.getDeaths())),
                statementLine("Buried", flowText(service.getBurials())),
                statementLine("Cremated", flowText(service.getCremations())),
                statementTotal("Dealt with", String.format("%.1f%%", service.getDeathCareRatio() * 100), null),
                statementHead("The ground"),
                statementLine("Plots built", people(plots)),
                statementLine("Plots used", people(service.getPlotsUsed())),
                statementLine("Left", people(left)),
                statementLine("Full", String.format("%.0f%%", service.getPlotUtilisation() * 100)),
                statementTotal("Runs out in", groundWords(), null),
                statementHead("The crematoria"),
                statementLine("Capacity", people(ovens) + " a month"),
                statementLine("Used", String.format("%.0f%%", service.getCremationUtilisation() * 100)));
    }

    /* =====================================================================
       EDUCATION

       The system with the most model behind it and the least of it on screen.
       Until now the whole of it was three lines - a coverage percentage, an
       enrolment count and a net cost - for a class that runs a real pipeline
       with course lengths, an affordability gate and a return-on-study
       calculation that reads live wages.

       The question these pages answer is the one the old screen could not:
       THE UNIVERSITY IS BUILT AND EMPTY, WHY. There are exactly three
       possible answers - no seats free, nobody eligible, or nobody willing -
       and every course page names which of the three is the binding one.

       THE PIPELINE (0.7.28): the Overview draws the schools as the road a
       child takes through them - the basic ladder to a diploma, the diploma
       to college or university, university to the four professions - a
       node a school, each with its ring, who is in it, who finishes next
       month and the gate that holds it.
       ===================================================================== */

    List<Kpi> educationKpis(List<CityNeeds.Need> all) {
        Education schools = ui.game.getEducation();
        double basic = schools.basicCoverage();
        List<Kpi> out = new ArrayList<>();
        out.add(new Kpi("BASIC LADDER", pct(basic),
                "held up by " + schools.basicBottleneck().getLabel().toLowerCase(),
                tone(need(all, CityNeeds.Kind.BASIC_SCHOOLS, null, null)),
                "Education's Overview: the pipeline", () -> open("Education", OVERVIEW),
                "schoolCoverage", ServicesScreen::points));
        out.add(new Kpi("IN CLASS", people(sum(schools.getStudying())), "adults out of the workforce",
                Palette.TEXT_HEAD, "Education's Overview: the pipeline", () -> open("Education", OVERVIEW),
                "students", Money::people));
        /*
         * NEW LICENCES, not TAUGHT EVER (0.7.28): the running total added
         * only the months a band grew, net of those who moved on, so it
         * undercounted every band (it is on the basic ladder's page, said
         * for what it is); the licences are a gross flow, kept month by month.
         */
        double licences = sum(schools.getLicences());
        // The month's licences are not kept by a save, as the diplomas are not:
        // until a month runs after a load, say so rather than "none".
        boolean struck = !Double.isNaN(schools.getNewDiplomas());
        out.add(new Kpi("NEW LICENCES", struck ? flowText(licences) : "—",
                !struck ? "not recorded yet: advance a month"
                        : licences > 0 ? "from the professional schools this month" : "none this month",
                Palette.TEXT_HEAD, "Education's Overview: the pipeline", () -> open("Education", OVERVIEW),
                "licences", Pieces::flowText));
        out.add(new Kpi("THE BILL", tightMoney(toDollars(-schools.getNetCost())) + "/mo",
                String.format("%.0f%% back in tuition", schools.getCostRecovery() * 100),
                Palette.TEXT_HEAD, "Education's books", () -> open("Education", BOOKS),
                "schoolBill", Money::money));
        return out;
    }

    /**
     * True when a save has been loaded and no month has run since.
     *
     * Education carries only the dial, the running totals and the students in
     * flight; coverage, enrolment and the bill are this month's flow and are
     * rebuilt from the buildings and the pyramid on the first month back. Which
     * is a defensible saving - but it means a freshly loaded city shows a
     * hundred per cent staffed school with nobody in it and a bill of nothing,
     * and that reads as a broken screen rather than as an empty variable.
     *
     * The test is a school that exists and a bill of zero: the payroll of a
     * staffed school is never nought once a month has actually been run.
     */
    boolean educationNotRunYet() {
        double[] places = ui.game.getBuildingManager()
                .getStaffedEducationPlaces(ui.game.getPopulationManager().getJobFillRate());
        return sum(places) > 0 && ui.game.getEducation().getGrossCost() <= 0;
    }

    void educationPage(VBox page, List<CityNeeds.Need> all, List<Event> events) {

        if (educationNotRunYet()) {
            page.getChildren().add(eventLine("Nothing measured yet: advance a month and this fills in.",
                    "This city was loaded and no month has run since. The schools carry "
                    + "their students and their running totals through a save; coverage, "
                    + "enrolment and the bill are worked out fresh each month, so they read "
                    + "as nothing until you advance one. Press the arrow and this fills in.", Palette.WARN));
        }

        switch (servicePage) {
            case "Basic ladder" -> basicLadderPage(page, all);
            case "College"      -> coursePage(page, EducationType.COLLEGE, all);
            case "University"   -> coursePage(page, EducationType.UNIVERSITY, all);
            case "Professions"  -> professionsPage(page, all);
            case BOOKS          -> educationBooksPage(page);
            default             -> educationOverview(page, all, events);
        }
    }

    /* ------------------------------- the pipeline ------------------------------- */

    /**
     * One school as the pipeline draws it, worked out without drawing it: its
     * ring - a basic stage's coverage in Build's verdict for it, a school
     * above the ladder its seats in use in the people teal (who would come is
     * Build's verdict, not this page's) - its two lines, and the gate that
     * holds it with that gate's colour (grey; a verdict only where NEEDS YOU
     * lists the school for its seats).
     */
    record SchoolNode(EducationType type, String ring, double arc, String ringTone, String line1, String line2,
                      String gate, String gateTone, String page) { }

    SchoolNode schoolNode(EducationType t, List<CityNeeds.Need> all) {
        Education schools = ui.game.getEducation();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] fill = ui.game.getPopulationManager().getJobFillRate();
        double seats = bm.getStaffedEducationPlaces(fill)[t.ordinal()];
        double built = bm.getBuiltEducationPlaces()[t.ordinal()];
        String page = t.isBasic() ? "Basic ladder" : t == EducationType.COLLEGE ? "College"
                : t == EducationType.UNIVERSITY ? "University" : "Professions";
        if (t.isBasic()) {
            double cover = schools.getCoverage(t);
            int level = ui.buildScreen.levelOf(BuildAdvice.Measure.school(t), all);
            double[] sd = BuildAdvice.supplyDemand(ui.game, BuildAdvice.Measure.school(t), java.util.Map.of());
            boolean narrowest = t == schools.basicBottleneck();
            return new SchoolNode(t, pct(cover), cover, BuildScreen.verdict(level),
                    "taught " + people(schools.getEnrolled(t)), "of " + people(sd[1]) + " to teach",
                    narrowest ? "the narrowest stage" : null,
                    narrowest ? BuildScreen.verdict(level) : Palette.TEXT_LABEL, page);
        }
        Gate g = gate(t);
        double inFlight = schools.getEnrolled(t);
        double[] queue = schools.cohortsInFlight(t);
        double use = seats > 0 ? Math.min(1, inFlight / seats) : 0;
        String ring = seats > 0 ? pct(use) : built > 0 ? "0%" : "—";
        CityNeeds.Need row = need(all, CityNeeds.Kind.HIGHER_SCHOOL, null, t);
        String gateTone = g.binding().equals("seats") && row != null && row.level() > 0
                ? BuildScreen.verdict(row.level()) : Palette.TEXT_LABEL;
        return new SchoolNode(t, ring, use, Palette.PEOPLE,
                seats <= 0 && inFlight <= 0 ? (built > 0 ? people(built) + " seats, none staffed" : "not built")
                        : "in class " + people(inFlight) + " of " + people(seats),
                queue.length > 0 && queue[0] > 0 ? flowText(queue[0]) + " finish next month" : "nobody finishes next month",
                g.words(), gateTone, page);
    }

    /**
     * Which gate holds a course above the ladder, from the reads the course
     * page draws its funnel from: no seats built (and how many would come),
     * built and unstaffed, the seats, nobody able to afford it, or the
     * students - with the wage return that is not drawing them.
     */
    record Gate(String binding, String words) { }

    Gate gate(EducationType t) {
        Education schools = ui.game.getEducation();
        BuildingManager bm = ui.game.getBuildingManager();
        PopulationManager pm = ui.game.getPopulationManager();
        LabourMarket market = ui.game.getLabourMarket();
        double seats = bm.getStaffedEducationPlaces(pm.getJobFillRate())[t.ordinal()];
        double built = bm.getBuiltEducationPlaces()[t.ordinal()];
        double inFlight = schools.getEnrolled(t);
        if (seats <= 0 && inFlight <= 0) {
            return built > 0 ? new Gate("staff", "built: no teachers")
                    : new Gate("built", "not built: " + people(CityNeeds.wouldCome(ui.game, t)) + " would come");
        }
        double free = Math.max(0, seats - inFlight);
        double wanted = schools.eligibleFor(t, pm) * schools.willingShare(t, market) * Education.ENROLMENT_RATE;
        if (free <= wanted) return new Gate("seats", "held by seats");
        if (schools.studyAffordability(t, market) <= 0) return new Gate("afford", "nobody can afford it");
        return new Gate("students", "held by students: " + pct(schools.studyReturn(t, market)) + " return");
    }

    void educationOverview(VBox page, List<CityNeeds.Need> all, List<Event> events) {
        eventLines(page, "Education", events);
        Education schools = ui.game.getEducation();

        page.getChildren().add(sectionHead("THE BASIC LADDER", LADDER_INFO,
                hint("every child, elementary to high school: the ladder is as wide as its narrowest stage")));
        HBox basic = new HBox(6);
        basic.setAlignment(Pos.CENTER_LEFT);
        EducationType[] stages = {EducationType.ELEMENTARY, EducationType.MIDDLE, EducationType.HIGH};
        for (EducationType t : stages) {
            basic.getChildren().addAll(node(schoolNode(t, all)), chevron());
        }
        basic.getChildren().add(diplomaNode());
        page.getChildren().add(lane(basic));

        page.getChildren().add(sectionHead("ADULT STUDY", null,
                hint("the diploma-holders go on to college or to university")));
        HBox adult = new HBox(6, node(schoolNode(EducationType.COLLEGE, all)), chevron(),
                node(schoolNode(EducationType.UNIVERSITY, all)));
        adult.setAlignment(Pos.CENTER_LEFT);
        page.getChildren().add(lane(adult));

        page.getChildren().add(sectionHead("THE LICENSED PROFESSIONS", PROFESSIONS_INFO,
                hint("from university: each licenses a job no other school can")));
        HBox pro = new HBox(6);
        pro.setAlignment(Pos.CENTER_LEFT);
        for (EducationType t : EducationType.values()) {
            if (t.isProfessional()) pro.getChildren().add(node(schoolNode(t, all)));
        }
        page.getChildren().add(lane(pro));
        page.getChildren().add(words(people(sum(schools.getStudying())) + " adults are in class, out of the workforce until they finish.",
                Palette.SIZE_LABEL, Palette.TEXT_MUTED));
    }

    /** A lane of nodes that wraps rather than overflowing a narrow window. */
    static Node lane(HBox row) {
        javafx.scene.layout.FlowPane flow = new javafx.scene.layout.FlowPane(6, 8);
        // A copy: each node leaves the row as it joins the flow.
        flow.getChildren().addAll(new ArrayList<>(row.getChildren()));
        flow.setAlignment(Pos.CENTER_LEFT);
        return flow;
    }

    static Label chevron() {
        Label c = new Label("›");
        c.setStyle(Palette.words(18, Palette.TEXT_MUTED));
        c.setMinWidth(Region.USE_PREF_SIZE);
        return c;
    }

    /** One node of the pipeline: icon, name and ring; two lines; the gate as a chip. A click opens its page. */
    VBox node(SchoolNode n) {
        Label name = new Label(n.type().getLabel());
        name.setStyle(Palette.strong(Palette.SIZE_HEADING, Palette.TEXT_HEAD));
        name.setWrapText(true);
        name.setMinWidth(0);
        HBox.setHgrow(name, Priority.ALWAYS);
        HBox head = new HBox(8, iconSquare(Icons.EDUCATION, Palette.PEOPLE, 24, 13), name,
                ring(n.arc(), n.ringTone(), 40, 5, n.ring(), 10));
        head.setAlignment(Pos.CENTER_LEFT);
        VBox c = card(head, words(n.line1(), Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                words(n.line2(), Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        c.setSpacing(4);
        if (n.gate() != null) c.getChildren().add(chip(n.gate(), n.gateTone()));
        c.setMinWidth(228);
        c.setPrefWidth(228);
        c.setMaxWidth(228);
        c.setMinHeight(112);
        return doorCard(c, () -> open("Education", n.page()), n.type().getLabel() + ": click for its page.");
    }

    /** The ladder's end: the diplomas the school leavers took this month (Education.getNewDiplomas(), gross), or "not recorded yet" after a load. */
    VBox diplomaNode() {
        double d = ui.game.getEducation().getNewDiplomas();
        Label name = new Label("Diplomas");
        name.setStyle(Palette.strong(Palette.SIZE_HEADING, Palette.TEXT_HEAD));
        HBox head = new HBox(8, iconSquare(Icons.ARRIVE, Palette.PEOPLE, 24, 13), name);
        head.setAlignment(Pos.CENTER_LEFT);
        VBox c = card(head, Double.isFinite(d) ? figure(flowText(d), 18, Palette.TEXT_HEAD)
                        : words("not recorded yet", Palette.SIZE_LABEL, Palette.TEXT_MUTED),
                infoLine("diplomas this month", DIPLOMA_INFO, false, Palette.SIZE_CAPTION, Palette.TEXT_MUTED, 200));
        c.setSpacing(4);
        c.setMinWidth(170);
        c.setPrefWidth(170);
        c.setMaxWidth(170);
        c.setMinHeight(112);
        return doorCard(c, () -> open("Education", "Basic ladder"), "The basic ladder: click for its page.");
    }

    /** The diplomas' (i): the teens' note, and what the figure is (and is not). */
    static final String DIPLOMA_INFO = "Teens age out at a steady rate and the ones who were in school leave with "
            + "a diploma. Every arrival to this city already has one unless a dear unskilled wage brought them "
            + "in — otherwise the unskilled band is only ever your own children, so this line is the only "
            + "thing that grows it.\n\nThe school leavers' diplomas, gross. The band's own movement nets off "
            + "the diploma-holders who finished college or university, so it can fall in a month a high school handed "
            + "diplomas out. Not kept by a save: after a load it reads \"not recorded yet\" until a month runs.";

    /** The basic ladder's notes: the minimum of three, and the four-and-three split. */
    static final String LADDER_INFO = String.format(
            "The ladder covers the minimum of its three stages, not the average — the narrowest is the one "
            + "holding the rest up, and spare places anywhere else cannot make up for it.%n%n"
            + "Elementary and middle both teach the child band, so it is split %.0f/%.0f between them — the "
            + "real four years then three.",
            Education.ELEMENTARY_SHARE * 100, (1 - Education.ELEMENTARY_SHARE) * 100);

    /** The professions page's sentence. */
    static final String PROFESSIONS_INFO = "A band row on the People screen can say the city has eight hundred "
            + "graduates and two hundred graduate posts, and every one of those posts still stands empty if "
            + "they are doctor posts and nobody is a doctor. These four schools are the only way to make your "
            + "own — and the only other source is migration, which brings a licence roughly one time in ten.";

    /* ------------------------------- the basic ladder ------------------------------- */

    /**
     * The three stages a child passes through, and the one holding up the rest.
     *
     * COVERAGE IS THE MINIMUM of the three, not the average - the average would
     * let a city paper over a missing high school with spare primary places,
     * which is the one thing a pipeline cannot do. So the bottleneck is both the
     * honest answer and the actionable one, because it names a building.
     */
    void basicLadderPage(VBox page, List<CityNeeds.Need> all) {
        Education schools = ui.game.getEducation();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();
        double[] staffed = bm.getStaffedEducationPlaces(staffing);
        double[] built = bm.getBuiltEducationPlaces();

        page.getChildren().add(sectionHead("THE BASIC LADDER · " + pct(schools.basicCoverage()) + " covered", LADDER_INFO,
                hint("held up by " + schools.basicBottleneck().getLabel().toLowerCase())));
        VBox rows = new VBox(12);
        for (EducationType stage : new EducationType[]{EducationType.ELEMENTARY, EducationType.MIDDLE, EducationType.HIGH}) {
            double[] sd = BuildAdvice.supplyDemand(ui.game, BuildAdvice.Measure.school(stage), java.util.Map.of());
            double cover = schools.getCoverage(stage);
            int level = ui.buildScreen.levelOf(BuildAdvice.Measure.school(stage), all);
            Label name = words(stage.getLabel(), Palette.SIZE_BODY, Palette.TEXT_HEAD);
            name.setMinWidth(150);
            name.setPrefWidth(150);
            Label figure = figure(pct(cover), Palette.SIZE_SECTION, BuildScreen.verdict(level));
            figure.setMinWidth(54);
            SegmentBar bar = supplyBar(sd[1], built[stage.ordinal()], staffed[stage.ordinal()], Palette.PEOPLE,
                    "to teach", List.of(new Tick(schools.getEnrolled(stage), Palette.PEOPLE_LIGHT, 2, null, "in class")),
                    null, 0, 10);
            HBox.setHgrow(bar, Priority.ALWAYS);
            HBox top = new HBox(Palette.GAP_LOOSE, name, figure, bar);
            top.setAlignment(Pos.CENTER_LEFT);
            Label line = words(people(built[stage.ordinal()]) + " seats · " + people(staffed[stage.ordinal()]) + " staffed · "
                    + people(schools.getEnrolled(stage)) + " in class · " + people(sd[1]) + " to teach"
                    + (stage == schools.basicBottleneck() ? " · the narrowest stage" : ""),
                    Palette.SIZE_CAPTION, Palette.TEXT_MUTED);
            VBox.setMargin(line, new javafx.geometry.Insets(0, 0, 0, 150 + 54 + 2 * Palette.GAP_LOOSE));
            rows.getChildren().add(new VBox(2, top, line));
        }
        page.getChildren().add(card(rows));

        double d = schools.getNewDiplomas();
        page.getChildren().add(infoLine(Double.isFinite(d) ? flowText(d) + " diplomas this month, from the school leavers"
                : "Diplomas this month: not recorded yet — advance a month.", DIPLOMA_INFO, false,
                Palette.SIZE_BODY, Palette.TEXT_HEAD, 700));
        page.getChildren().add(tuitionLine(EducationType.HIGH));
        page.getChildren().add(details("education:ladder", "the ladder as a table, and the diplomas since the founding",
                ladderTable(), column(
                        statementTotal("The ladder covers", pct(schools.basicCoverage()), null),
                        statementLine("Diploma-holders gained since the founding",
                                people(schools.getEverGraduated()[WageBand.DIPLOMA.ordinal()])),
                        statementNote("A running total of the months the diploma band grew, net of those who "
                                + "finished college or university - the months it shrank are not taken off, and the "
                                + "months it grew count only the growth. Not a count of diplomas handed out."))));
    }

    /** The old ladder table: stage, to teach, seats, staffed, in class, covered. */
    javafx.scene.layout.GridPane ladderTable() {
        Education schools = ui.game.getEducation();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();
        double[] staffed = bm.getStaffedEducationPlaces(staffing);
        double[] built = bm.getBuiltEducationPlaces();
        javafx.scene.layout.GridPane table = grid(new double[] {124, 74, 74, 74, 74, 78}, rightAfterFirst(6));
        gridHead(table, "stage", "to teach", "seats", "staffed", "in class", "covered");
        EducationType[] stages = {EducationType.ELEMENTARY, EducationType.MIDDLE, EducationType.HIGH};
        for (int i = 0; i < stages.length; i++) {
            EducationType stage = stages[i];
            double[] sd = BuildAdvice.supplyDemand(ui.game, BuildAdvice.Measure.school(stage), java.util.Map.of());
            String tone = stage == schools.basicBottleneck() ? Palette.WARN : Palette.TEXT_BODY;
            table.add(gridCell(stage.getLabel(), tone, Palette.SIZE_CAPTION, false), 0, i + 1);
            table.add(gridCell(people(sd[1]), tone, Palette.SIZE_CAPTION, true), 1, i + 1);
            table.add(gridCell(people(built[stage.ordinal()]), tone, Palette.SIZE_CAPTION, true), 2, i + 1);
            table.add(gridCell(people(staffed[stage.ordinal()]), tone, Palette.SIZE_CAPTION, true), 3, i + 1);
            table.add(gridCell(people(schools.getEnrolled(stage)), tone, Palette.SIZE_CAPTION, true), 4, i + 1);
            table.add(gridCell(pct(schools.getCoverage(stage)), tone, Palette.SIZE_CAPTION, true), 5, i + 1);
        }
        return table;
    }

    /* ------------------------------- a course ------------------------------- */

    /**
     * One adult course: why that many and not more. WRITTEN ONCE FOR SIX
     * COURSES. College, university and the four professional schools differ
     * in their length, their entry requirement and what they produce, and in
     * nothing else - so one method makes that structure the visible fact it
     * is. The page leads with the gates as a funnel, the binding one
     * outlined; beside it the two things that make somebody willing, as
     * gauges; under it everybody part way through, a bar a month.
     */
    void coursePage(VBox page, EducationType course, List<CityNeeds.Need> all) {
        page.getChildren().add(sectionHead(course.getLabel().toUpperCase() + " · " + courseHead(course), GATES_INFO,
                chip(gate(course).words(), schoolNode(course, all).gateTone())));
        page.getChildren().add(courseView(course, false));
        page.getChildren().add(tuitionLine(course));
        page.getChildren().add(details("education:" + course.name(), "the course as a statement", courseStatement(course)));
    }

    /** A course's heading line: its length and who it takes. */
    static String courseHead(EducationType course) {
        return String.format("%d months (%.0f years)", course.months(), course.months() / 12.0)
                + (course.requires() == null ? "" : " · takes " + course.requires().label().toLowerCase() + "-holders");
    }

    /**
     * The course's picture: the funnel (could enrol, willing, start a month,
     * seats free, enrolling) with the gate that binds outlined, the two
     * gauges (the wage return, who can afford it), and the cohort bars - or,
     * for a course the city has not built, the gauges and how many would
     * come, with Build's door. `compact` is the professions page's card.
     */
    VBox courseView(EducationType course, boolean compact) {
        Education schools = ui.game.getEducation();
        PopulationManager pm = ui.game.getPopulationManager();
        LabourMarket market = ui.game.getLabourMarket();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] staffing = pm.getJobFillRate();
        double seats = bm.getStaffedEducationPlaces(staffing)[course.ordinal()];
        double built = bm.getBuiltEducationPlaces()[course.ordinal()];
        double[] queue = schools.cohortsInFlight(course);
        double inFlight = schools.getEnrolled(course);
        double eligible = schools.eligibleFor(course, pm);
        double willing = schools.willingShare(course, market);
        double back = schools.studyReturn(course, market);
        double afford = schools.studyAffordability(course, market);

        HBox gauges = new HBox(compact ? 10 : 18,
                gauge(back, "wage return", RETURN_INFO_PREFIX + returnNote(course, market, back), compact ? 72 : 96),
                gauge(afford, "can afford it", affordNote(afford), compact ? 72 : 96));
        gauges.setAlignment(Pos.CENTER_LEFT);

        if (seats <= 0 && inFlight <= 0) {
            VBox none = new VBox(8, words(built > 0
                    ? "The building is up and nobody is teaching in it — every seat here is "
                      + "discounted by how much of the staff turned up, and none of them did."
                    : "The city has not built one. " + (course.requires() == null ? ""
                      : "It would take " + course.requires().label().toLowerCase()
                        + "-holders as students, and the city has " + people(eligible) + " of them."),
                    Palette.SIZE_BODY, Palette.TEXT_LABEL),
                    new HBox(Palette.GAP, figure(people(CityNeeds.wouldCome(ui.game, course)), 18, Palette.TEXT_HEAD),
                            words("would come, at today's wages and tuition", Palette.SIZE_LABEL, Palette.TEXT_LABEL)),
                    gauges,
                    words("Both read off live wages, so they answer the question before the money is spent: a "
                            + "school built where neither of these is above zero teaches nobody.",
                            Palette.SIZE_CAPTION, Palette.TEXT_MUTED),
                    buildFor(BuildAdvice.Measure.school(course)));
            return compact ? none : card(none);
        }

        List<FunnelStep> steps = courseSteps(course);
        boolean seatsBind = gate(course).binding().equals("seats");
        VBox funnel = funnel(steps, 0, seatsBind ? 3 : 2, Palette.PEOPLE);
        HBox.setHgrow(funnel, Priority.ALWAYS);

        CohortBars bars = cohortBars(queue, 0, compact ? 44 : 90, Palette.PEOPLE, -1, null, null,
                "finish next month", "just enrolled", Pieces::flowText,
                k -> k == 0 ? "finishes next month" : "finishes in " + (k + 1) + " months");
        String line = "in class " + people(inFlight) + " of " + people(seats) + " staffed seats"
                + (built - seats > .5 ? " (" + people(built) + " built)" : "") + " · "
                + (queue.length > 0 && queue[0] > 0 ? flowText(queue[0]) + " finish next month" : "nobody finishes next month");
        if (built - seats > .5) {
            funnel.getChildren().add(infoLine(people(built - seats) + " seats stand empty for want of teachers",
                    "A school with no teachers teaches nobody, exactly as a hospital with no doctors treats nobody.",
                    false, Palette.SIZE_LABEL, Palette.TEXT_HEAD, 560));
        }
        if (compact) {
            return new VBox(8, funnel, gauges, bars, words(line, Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        }
        HBox top = new HBox(24, funnel, gauges);
        top.setAlignment(Pos.CENTER_LEFT);
        return card(top, sectionHead("EVERYBODY PART WAY THROUGH", null, hint(line)), bars);
    }

    /**
     * A course's gates as funnel steps, worked out without drawing them:
     * who could enrol, of whom willing, who start in a month, the seats
     * free and who enrols - the old page's figures, from the same reads.
     */
    List<FunnelStep> courseSteps(EducationType course) {
        Education schools = ui.game.getEducation();
        PopulationManager pm = ui.game.getPopulationManager();
        LabourMarket market = ui.game.getLabourMarket();
        double seats = ui.game.getBuildingManager().getStaffedEducationPlaces(pm.getJobFillRate())[course.ordinal()];
        double inFlight = schools.getEnrolled(course);
        double eligible = schools.eligibleFor(course, pm);
        double willing = schools.willingShare(course, market);
        double free = Math.max(0, seats - inFlight);
        double wanted = eligible * willing * Education.ENROLMENT_RATE;
        double intake = Math.min(free, wanted);
        boolean seatsBind = free <= wanted;
        return List.of(
                new FunnelStep("could enrol", eligible, people(eligible), COULD_ENROL),
                new FunnelStep("willing", eligible * willing, pct(willing), "of whom willing: the two gauges, multiplied"),
                new FunnelStep("start a month", wanted, flowText(wanted), String.format(
                        "About one in %.0f of the willing starts in any given month — a person spends roughly "
                        + "five years of a working life in a position to consider going back to study.",
                        1 / Education.ENROLMENT_RATE)),
                new FunnelStep("seats free", free, people(free), "seats not taken by the students in flight"),
                new FunnelStep("enrolling", intake, flowText(intake), seatsBind
                        ? "The seats are the binding constraint: more people want in than the city can teach. "
                          + "Another school is the answer."
                        : "The students are the binding constraint: there are seats going spare. Another school "
                          + "changes nothing — the two gauges are what to move."));
    }

    /** The gates' (i): why that many and not more, as the old page said it under its five lines. */
    static final String GATES_INFO = String.format(
            "The funnel is why that many and not more. Who could enrol holds the level the course takes, "
            + "in the workforce - for a professional school, those who do not already hold its licence: "
            + "nobody trains twice. The two gauges, multiplied, are who of them is willing. About one in "
            + "%.0f of the willing starts in any given month — a person spends roughly five years of a "
            + "working life in a position to consider going back to study. The seats free are those the "
            + "students in flight do not fill, and the smaller of the last two is who enrols: the outlined "
            + "step is the one holding it shut. Seats binding, another school is the answer; students "
            + "binding, another school changes nothing — the gauges are what to move.",
            1 / Education.ENROLMENT_RATE);

    /** Who could enrol, in words (the old page's note). */
    static final String COULD_ENROL = "Who holds the level this course takes, in the workforce - and for a "
            + "professional school, those who do not already hold its licence: nobody trains twice.";

    /** The wage return's (i) opens on this, then says what the return is measured against (returnNote()). */
    static final String RETURN_INFO_PREFIX = "How much better off somebody is for doing it - 0 means not worth it.\n\n";

    /** Who can afford it, in words. */
    static String affordNote(double afford) {
        return (afford <= 0
                ? "Nobody. The un-subsidised part is more than "
                  + String.format("%.0f%%", Education.MAX_BURDEN * 100)
                  + " of what these people earn, which is the point where enrolment stops "
                  + "entirely. Raise the subsidy or raise their wages."
                : "Falls straight from everybody at no burden to nobody at "
                  + String.format("%.0f%%", Education.MAX_BURDEN * 100)
                  + " of a month's pay. This is the poverty trap, stated: a city of "
                  + "unskilled workers produces no graduates however many schools it builds.")
                + "\n\nMultiplied, not averaged — either one being zero is a complete answer on "
                + "its own. A degree that pays no more than the job you have is not worth "
                + "doing at any price, and one you cannot pay for is not worth doing at "
                + "any salary.";
    }

    /** What the wage return is actually comparing, in words. */
    String returnNote(EducationType course, LabourMarket market, double back) {
        if (back <= 0) {
            return course.isProfessional()
                    ? "The licence pays no better than the best job these graduates can "
                      + "already hold without it. Nobody studies for years to stand still."
                    : "The level above pays no better than the level these people are on. "
                      + "Nobody studies for years to stand still.";
        }
        return course.isProfessional()
                ? "Measured against the best job a graduate can hold WITHOUT the licence — "
                  + "not against the licensed salary they are studying for, which would "
                  + "compare the wage to itself. Read off live wages, so a city short of "
                  + "this profession is a city whose people are choosing it."
                : "Read off live wages, so a city short of a skill is a city whose people "
                  + "are choosing to learn it — the labour market's scarcity signal arriving "
                  + "in the classroom with nothing connecting them deliberately. A doubling "
                  + "of the return is full participation; past that the answer is already yes.";
    }

    /**
     * A gauge: half a ring, filled to a share (held to one - a return past
     * a doubling is full participation already), its figure in the middle and
     * its name under it, with its (i). In the people teal: a gauge is a
     * reading, and the gate it makes is named, not coloured.
     */
    static VBox gauge(double share, String name, String info, double size) {
        double s = Double.isFinite(share) ? Math.max(0, Math.min(1, share)) : 0;
        double r = size / 2 - 5;
        javafx.scene.shape.Arc track = new javafx.scene.shape.Arc(size / 2, size / 2, r, r, 180, -180);
        track.setType(javafx.scene.shape.ArcType.OPEN);
        track.setFill(null);
        track.setStroke(javafx.scene.paint.Color.web(Palette.EDGE));
        track.setStrokeWidth(8);
        javafx.scene.shape.Arc fill = new javafx.scene.shape.Arc(size / 2, size / 2, r, r, 180, -180 * s);
        fill.setType(javafx.scene.shape.ArcType.OPEN);
        fill.setFill(null);
        fill.setStroke(javafx.scene.paint.Color.web(Palette.PEOPLE));
        fill.setStrokeWidth(8);
        javafx.scene.layout.Pane arcs = new javafx.scene.layout.Pane(track);
        if (s > 0) arcs.getChildren().add(fill);
        arcs.setMinSize(size, size / 2 + 6);
        arcs.setPrefSize(size, size / 2 + 6);
        arcs.setMaxSize(size, size / 2 + 6);
        Label figure = figure(pct(share), size >= 90 ? 15 : 12, Palette.TEXT_HEAD);
        javafx.scene.layout.StackPane drawn = new javafx.scene.layout.StackPane(arcs, figure);
        javafx.scene.layout.StackPane.setAlignment(figure, Pos.BOTTOM_CENTER);
        drawn.setMinSize(size, size / 2 + 6);
        drawn.setMaxSize(size, size / 2 + 6);
        HBox label = new HBox(Palette.GAP_TIGHT, words(name, Palette.SIZE_CAPTION, Palette.TEXT_LABEL), infoButton(info, false));
        label.setAlignment(Pos.CENTER);
        VBox box = new VBox(3, drawn, label);
        box.setAlignment(Pos.TOP_CENTER);
        box.setMinWidth(size + 20);
        return box;
    }

    /** The four schools that gate a job rather than raise a level, two by two. */
    void professionsPage(VBox page, List<CityNeeds.Need> all) {
        page.getChildren().add(sectionHead("THE LICENSED PROFESSIONS", PROFESSIONS_INFO,
                hint("which licence is the city short of, and why")));
        javafx.scene.layout.GridPane grid = equalColumns(2, TILE_GAP);
        int i = 0;
        for (EducationType course : EducationType.values()) {
            if (!course.isProfessional()) continue;
            SchoolNode n = schoolNode(course, all);
            VBox c = card(cardHead(Icons.EDUCATION, course.getLabel(), null, chip(n.gate(), n.gateTone())),
                    words(courseHead(course), Palette.SIZE_CAPTION, Palette.TEXT_MUTED),
                    courseView(course, true), tuitionLine(course));
            grid.add(c, i % 2, i / 2);
            i++;
        }
        page.getChildren().add(grid);
        page.getChildren().add(details("education:professions", "the four courses as statements",
                courseStatement(EducationType.MEDICAL), courseStatement(EducationType.LAW),
                courseStatement(EducationType.BUSINESS), courseStatement(EducationType.ENGINEERING)));
    }

    /** The old course block's lines - the pipeline, the gates, the two things that move it - as they were. */
    VBox courseStatement(EducationType course) {
        Education schools = ui.game.getEducation();
        PopulationManager pm = ui.game.getPopulationManager();
        LabourMarket market = ui.game.getLabourMarket();
        BuildingManager bm = ui.game.getBuildingManager();
        double seats = bm.getStaffedEducationPlaces(pm.getJobFillRate())[course.ordinal()];
        double built = bm.getBuiltEducationPlaces()[course.ordinal()];
        double[] queue = schools.cohortsInFlight(course);
        double inFlight = schools.getEnrolled(course);
        double eligible = schools.eligibleFor(course, pm);
        double willing = schools.willingShare(course, market);
        double free = Math.max(0, seats - inFlight);
        double wanted = eligible * willing * Education.ENROLMENT_RATE;
        double intake = Math.min(free, wanted);
        return column(statementHead(course.getLabel()),
                statementLine("Seats", people(built)),
                built - seats > .5 ? statementLine("...staffed", people(seats)) : null,
                statementLine("Course length", String.format("%d months  (%.0f years)", course.months(), course.months() / 12.0)),
                statementLine("In class now", people(inFlight)),
                statementLine("Graduating next month", queue.length > 0 ? flowText(queue[0]) : "nobody"),
                statementLine("Seats free this month", people(free)),
                statementLine("Who could enrol", people(eligible)),
                statementLine("Of whom willing", String.format("%.1f%%", willing * 100)),
                statementLine("Who start in a month", people(wanted)),
                statementTotal("Enrolling this month", people(intake), null),
                statementLine("The wage return", pct(schools.studyReturn(course, market))),
                statementLine("Who can afford it", pct(schools.studyAffordability(course, market))));
    }

    /**
     * The price of a seat, and the dial that decides who pays it, in one line
     * (0.7.28: "What a seat costs" and its three lines). READ-ONLY HERE, per
     * Jerus - the subsidy is a policy and policies are set on the policy
     * screen. What it is doing belongs wherever its effect is visible, and
     * that is here.
     */
    HBox tuitionLine(EducationType course) {
        Education schools = ui.game.getEducation();
        double full = schools.feeFor(course);
        double subsidy = schools.getTuitionSubsidy();
        return priceLine("a seat " + cash(full) + " a month · the city pays " + pct(subsidy) + " · the household "
                        + cash(schools.outOfPocket(course)),
                "The subsidy is one dial for every course in the city, on the Policy "
                + "screen, beside the price of a place, the grant and the loan's rate. At "
                + "nothing, only the top pay tiers attend; at everything, education becomes "
                + "one of the largest lines on the budget.",
                "Set the subsidy", "Schools");
    }

    /* =====================================================================
       UTILITIES

       Coverage, load and what a shortage throttles: the answer to "are the
       lights on", which is a different question from "did the power company
       make money" and has a different answer. The utilities are the city's
       own plants - what they sell less their wages goes into the city's cash
       (Game's month) - and their books are this system's last chip.

       THREE CAPACITY ROWS (0.7.28): power, water and the road, each what it
       could do at full staff, what it does now and what the city asks of it,
       on one bar; under each, who draws it; at its right how short or spare
       it is, and its door. Since 0.7.29 the road is one card - its two
       figures and the door to Infrastructure › Roads, which owns the road.
       ===================================================================== */

    List<Kpi> utilityKpis(List<CityNeeds.Need> all) {
        ServicesManager services = ui.game.getServicesManager();
        double power = services.getEnergyRatio();
        double water = services.getWaterRatio();
        double roads = services.getRoadRatio();
        double net = services.getServiceNetIncome();
        List<Kpi> out = new ArrayList<>();
        out.add(new Kpi("POWER", pct(power), power >= 1 ? "the grid is stable" : "brownout",
                tone(need(all, CityNeeds.Kind.POWER, null, null)), "The utilities: power, water and the road",
                () -> open("Utilities", OVERVIEW), "energyRatio", ServicesScreen::points));
        out.add(new Kpi("WATER", pct(water), water >= 1 ? "supply is adequate" : "rationing",
                tone(need(all, CityNeeds.Kind.WATER, null, null)), "The utilities: power, water and the road",
                () -> open("Utilities", OVERVIEW), "waterRatio", ServicesScreen::points));
        out.add(new Kpi("ROADS", pct(roads),
                ui.game.getServicesManager().getInfrastructureManager().getStatus().toLowerCase(),
                tone(need(all, CityNeeds.Kind.ROADS, null, null)), "Infrastructure › Roads",
                () -> ui.infrastructureScreen.open("Roads"), "roadRatio", ServicesScreen::points));
        out.add(new Kpi("THEY EARN", tightMoney(toDollars(net)) + "/mo", "the city's own, after wages",
                Palette.TEXT_HEAD, "The utilities' books", () -> open("Utilities", BOOKS), null, null));
        return out;
    }

    void utilityPage(VBox page, List<CityNeeds.Need> all, List<Event> events) {
        if (BOOKS.equals(servicePage)) {
            utilityBooksPage(page);
            return;
        }
        eventLines(page, "Utilities", events);
        page.getChildren().add(sectionHead("IS THERE ENOUGH FOR WHAT THE CITY DRAWS", null,
                hint("the bar: now solid, at full staff light, the city's ask the white tick")));
        for (UtilityRow r : utilityRows(all)) page.getChildren().add(utilityCard(r));
        page.getChildren().add(roadCard(all));
    }

    /**
     * The road, as one card (0.7.29): its two figures as every screen writes
     * them - "162% full · 56% flow" - in its NEEDS YOU row's colour, and the
     * door to Infrastructure › Roads, which owns the road (the project's
     * spec-infra-0729.md, D10). It was a capacity row with its streams and a
     * statement of its own, a second copy of the Roads page's figures.
     */
    VBox roadCard(List<CityNeeds.Need> all) {
        InfrastructureManager roads = ui.game.getServicesManager().getInfrastructureManager();
        String tone = tone(need(all, CityNeeds.Kind.ROADS, null, null));
        Label name = new Label("The road");
        name.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        HBox titled = new HBox(8, iconSquare(Icons.ROADS, Palette.PEOPLE, 28, 15), name);
        titled.setAlignment(Pos.CENTER_LEFT);
        HBox pair = new HBox(6, figure(pct(roads.getUtilisation()), 24, tone),
                words("full ·", Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                figure(pct(roads.getThroughputRatio()), 24, Palette.TEXT_HEAD),
                words("flow", Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        pair.setAlignment(Pos.BASELINE_LEFT);
        VBox left = new VBox(4, titled, pair);
        left.setMinWidth(220);
        Label says = words("How full the road is, and what every business gets through it. What is on it, the "
                + "curve that links the two and what the cars and transit do to it are on Infrastructure.",
                Palette.SIZE_BODY, Palette.TEXT_LABEL);
        HBox.setHgrow(says, Priority.ALWAYS);
        says.setMaxWidth(Double.MAX_VALUE);
        Label go = door("Infrastructure › Roads", Palette.PEOPLE, () -> ui.infrastructureScreen.open("Roads"));
        HBox row = new HBox(18, left, says, go);
        row.setAlignment(Pos.CENTER_LEFT);
        return doorCard(card(row), () -> ui.infrastructureScreen.open("Roads"), "Infrastructure › Roads");
    }

    /**
     * One capacity row, worked out without drawing it: its name and icon;
     * the verdict figure (power and water supplied) in its
     * NEEDS YOU row's colour; what it could do at full staff, what it does
     * now and what is asked, in its unit, with its other ticks; who draws it;
     * how short or spare; its door; its (i); and the old page's lines.
     */
    record UtilityRow(String name, String icon, String figure, String figureWords, String tone,
                      double could, double now, double asked, List<Tick> ticks, List<Part> draws,
                      String shortWords, String info, Runnable go, String goWords,
                      java.util.function.DoubleFunction<String> unit, String key) { }

    List<UtilityRow> utilityRows(List<CityNeeds.Need> all) {
        UtilitiesHandler uh = ui.game.getServicesManager().getUtilitiesHandler();
        List<UtilityRow> out = new ArrayList<>();

        double asked = uh.getConsumption(), now = uh.getProduction(), could = uh.getBaseProduction();
        double billed = uh.getBilledElectricityDraw(), homes = uh.getHomesElectricityDraw();
        out.add(new UtilityRow("Power", Icons.UTILITIES, pct(uh.getEnergyRatio()), "supplied",
                tone(need(all, CityNeeds.Kind.POWER, null, null)), could, now, asked,
                List.of(new Tick(CityNeeds.NETWORK_YELLOW * now, Palette.TEXT_MUTED, 1, null,
                        "NEEDS YOU lists the grid from here: " + pct(CityNeeds.NETWORK_YELLOW) + " of what it generates")),
                List.of(Part.of("business, billed", billed, Palette.BUSINESS),
                        Part.of("homes", homes, Palette.PEOPLE),
                        Part.of("the city's own", Math.max(0, asked - billed - homes), Palette.MONEY)),
                shortWords(asked, now, could, Money::power), POWER_INFO,
                () -> ui.buildScreen.openOn(BuildAdvice.Measure.of(BuildAdvice.Kind.POWER)), "Build for it",
                Money::power, "power"));

        double wAsked = uh.getWaterConsumption(), wNow = uh.getWaterProduction(), wCould = uh.getBaseWaterProduction();
        double wBilled = uh.getBilledWaterDraw(), wHomes = uh.getHomesWaterDraw();
        out.add(new UtilityRow("Water", Icons.DROP, pct(uh.getWaterRatio()), "supplied",
                tone(need(all, CityNeeds.Kind.WATER, null, null)), wCould, wNow, wAsked,
                List.of(new Tick(CityNeeds.NETWORK_YELLOW * wNow, Palette.TEXT_MUTED, 1, null,
                        "NEEDS YOU lists the water from here: " + pct(CityNeeds.NETWORK_YELLOW) + " of what it treats")),
                List.of(Part.of("business, billed", wBilled, Palette.BUSINESS),
                        Part.of("homes", wHomes, Palette.PEOPLE),
                        Part.of("residents", uh.getResidentWaterDraw(), Palette.PEOPLE_LIGHT),
                        Part.of("the city's own", Math.max(0, uh.getBuildingWaterDraw() - wBilled - wHomes), Palette.MONEY)),
                shortWords(wAsked, wNow, wCould, v -> people(v) + " units"), WATER_INFO,
                () -> ui.buildScreen.openOn(BuildAdvice.Measure.of(BuildAdvice.Kind.WATER)), "Build for it",
                v -> people(v) + " units", "water"));
        return out;
    }

    /**
     * How short or spare a supply is, both ways (spec bug 17: the page's
     * "Short by" was full staff against the draw and its alert was now
     * against the draw, and neither said which): "short 38.9 MW now · 26.6
     * MW even fully staffed", "short 12 MW now · none at full staff", or
     * "spare 4.1 MW now".
     */
    static String shortWords(double asked, double now, double could, java.util.function.DoubleFunction<String> unit) {
        if (asked > now) {
            return "short " + unit.apply(asked - now) + " now · "
                    + (asked > could ? unit.apply(asked - could) + " even fully staffed" : "none at full staff");
        }
        return "spare " + unit.apply(now - asked) + " now";
    }

    /** One capacity row: the name and its verdict figure; the bar with who draws it under it; how short, and the door. */
    VBox utilityCard(UtilityRow r) {
        Label name = new Label(r.name());
        name.setStyle(Palette.strong(Palette.SIZE_HEADING + 1, Palette.TEXT_HEAD));
        HBox titled = new HBox(8, iconSquare(r.icon(), Palette.PEOPLE, 28, 15), name, infoButton(r.info(), false));
        titled.setAlignment(Pos.CENTER_LEFT);
        VBox left = new VBox(4, titled, new HBox(6, figure(r.figure(), 24, r.tone()),
                words(r.figureWords(), Palette.SIZE_LABEL, Palette.TEXT_LABEL)));
        ((HBox) left.getChildren().get(1)).setAlignment(Pos.BASELINE_LEFT);
        left.setMinWidth(220);
        left.setPrefWidth(220);
        left.setMaxWidth(220);

        double scale = Math.max(r.asked(), r.could()) * 1.05;
        List<Tick> ticks = new ArrayList<>(r.ticks());
        ticks.add(new Tick(r.asked(), Palette.TEXT_HEAD, 2, "asked", "the city asks " + r.unit().apply(r.asked())));
        List<Segment> segs = List.of(new Segment(r.now(), Palette.PEOPLE, false, null, null, "now " + r.unit().apply(r.now()), null),
                new Segment(Math.max(0, r.could() - r.now()), Palette.PEOPLE + "59", false, null, null,
                        "at full staff " + r.unit().apply(r.could()), null));
        SegmentBar bar = segmentBar(segs, scale > 0 ? scale : 1, ticks, 0, 14);
        String says = "now " + r.unit().apply(r.now()) + " · at full staff " + r.unit().apply(r.could())
                + " · the city asks " + r.unit().apply(r.asked());
        VBox middle = new VBox(4, bar, words(says, Palette.SIZE_CAPTION, Palette.TEXT_MUTED),
                causeBar(r.draws(), 0, 6, r.unit()));
        HBox.setHgrow(middle, Priority.ALWAYS);
        middle.setMaxWidth(Double.MAX_VALUE);

        Label shortLine = words(r.shortWords(), Palette.SIZE_BODY, Palette.TEXT_HEAD);
        VBox right = new VBox(8, shortLine, doorPill(r.goWords(), Icons.BUILD, Palette.BUILDING, r.go()));
        right.setMinWidth(254);
        right.setPrefWidth(254);
        right.setMaxWidth(254);

        HBox row = new HBox(18, left, middle, right);
        row.setAlignment(Pos.TOP_LEFT);
        return card(row, details("utilities:" + r.key(), "price, billed, unbilled and staff turnout",
                utilityStatement(r.key())));
    }

    /** The power row's (i): the unit, the staff discount, one workforce, who is billed. */
    static final String POWER_INFO = "Power is counted in kilowatts, a rate - the screens wrote watts until "
            + "0.7.28, a thousand times out.\n\nGeneration is discounted by how much of the plant's staff "
            + "turned up, so what it makes now can sit under what it could with every station built. One "
            + "workforce runs power and water both.\n\nOnly businesses are invoiced. Households are not billed "
            + "for power or water, so the homes' draw, and the city's own buildings', is real load and no "
            + "revenue.\n\nShort, every industrial and commercial building's output is cut in proportion — a "
            + "brownout shows up as a smaller economy, not as a dark screen, which is why it can run for years "
            + "unnoticed.";

    /** A brownout's (i), as the grid's old alert said it. */
    static final String BROWNOUT_INFO = "Every industrial and commercial building's output is cut in proportion "
            + "— a brownout shows up as a smaller economy, not as a dark screen, which is why it can run for "
            + "years unnoticed.";

    /** The water row's (i). */
    static final String WATER_INFO = "Water is counted in units of 10,000 gallons a month. The people draw "
            + "their own (residents), and every building standing draws its own on top - landscaping, cooling, "
            + "process water - split here by who: the businesses, which are billed; the homes; and the city's own "
            + "buildings. Splitting the residents from the buildings is the difference between \"stop building "
            + "housing\" and \"stop building food plants\", and no single total says which.\n\nOnly commercial and "
            + "industrial draw is invoiced; households are not billed for water. Short, industrial and "
            + "commercial output is cut in proportion.";

    /** A row's old lines, in its fold - kW for watts, and the homes for "resident draw". */
    VBox utilityStatement(String key) {
        UtilitiesHandler uh = ui.game.getServicesManager().getUtilitiesHandler();
        if (key.equals("power")) {
            return column(statementLine("Price per kW a month", cash(uh.getPricerPerWatt())),
                    statementLine("Billed draw, the businesses", power(uh.getBilledElectricityDraw())),
                    statementLine("Unbilled draw, the homes and the city's own buildings", power(uh.getUnbilledElectricityDraw())),
                    statementLine("...of which the homes", power(uh.getHomesElectricityDraw())),
                    statementLine("Staff turnout", pct(uh.getAverageUtilityFill())),
                    statementNote("Households are not billed for power or water, so the homes' draw is real load "
                            + "and no revenue. One workforce runs power and water both."));
        }
        // The water's; the road's statement went with its row (0.7.29: Infrastructure › Roads has it).
        return column(statementLine("Price per unit", cash(uh.getPricePerWaterUnit())),
                statementLine("Residents", people(uh.getResidentWaterDraw()) + " units"),
                statementLine("Buildings", people(uh.getBuildingWaterDraw()) + " units"),
                statementTotal("Total draw", people(uh.getWaterConsumption()) + " units", null),
                statementLine("Billed", people(uh.getBilledWaterDraw()) + " units"),
                statementLine("Unbilled", people(uh.getUnbilledWaterDraw()) + " units"),
                statementLine("Produced", people(uh.getWaterProduction()) + " units"),
                statementLine("Could produce", people(uh.getBaseWaterProduction()) + " units"));
    }

    /* =====================================================================
       SAFETY (2026-09-11)

       Crime, the police and the prisons. The page a player opens to ask why
       there is crime has to answer with the reasons first, because the reasons
       are the only thing that removes it - Jerus: "if there is a reason for
       crime there is no way to actually remove it without changing the
       underlying reason." The police come second, as what they take off, and
       the prisons third, as whether the people the police catch can be held.

       THE OVERVIEW (0.7.28) is the crime page it always was, drawn: the
       month's crime as one bar of its reasons beside how it stands against
       Canada's, then the police, the cells and what it did as three cards.
       ===================================================================== */

    List<Kpi> safetyKpis(List<CityNeeds.Need> all) {
        Crime crime = ui.game.getCrime();
        double vs = crime.getRateVsCanada();
        List<Kpi> out = new ArrayList<>();
        out.add(new Kpi("CRIME", people(crime.getRatePer100k()),
                String.format("a year per 100,000 — %.1fx Canada's", vs),
                tone(need(all, CityNeeds.Kind.CRIME, null, null)), "Where the crime comes from",
                () -> open("Safety", OVERVIEW), "crimeRate", Money::people));
        out.add(new Kpi("POLICE COVER", pct(crime.getCoverage()),
                people(crime.getOfficers()) + " officers on shift", Palette.TEXT_HEAD, "The police",
                () -> open("Safety", "Police"), "policeCoverage", ServicesScreen::points));
        out.add(new Kpi("IN PRISON", people(crime.prisoners()),
                crime.getNotHeld() >= .5 ? people(crime.getNotHeld()) + " caught and not held"
                        : "every one caught was held",
                tone(need(all, CityNeeds.Kind.CELLS, null, null)), "The prisons",
                () -> open("Safety", "Prisons"), "prisoners", Money::people));
        out.add(new Kpi("THE BILL", tightMoney(toDollars(-crime.getGrossCost())) + "/mo",
                "police and prisons, no fees", Palette.TEXT_HEAD, "Safety's books",
                () -> open("Safety", BOOKS), "safetyBill", Money::money));
        return out;
    }

    void safetyPage(VBox page, List<CityNeeds.Need> all, List<Event> events) {
        switch (servicePage) {
            case "Police"  -> policePage(page);
            case "Prisons" -> prisonsPage(page);
            case BOOKS     -> safetyBooksPage(page);
            default        -> crimeOverview(page, all);
        }
    }

    /**
     * The crime's reasons as parts of the month's crime: Crime's own split
     * (getCrimes(cause), each cause's share of the pressure), in the causes'
     * order and the categories' colours - never a verdict's. Too few police
     * is a door to the police; no home and crowding to Build's homes.
     */
    List<Part> crimeParts() {
        Crime crime = ui.game.getCrime();
        String[] colours = {Palette.MONEY, Palette.PEOPLE, Palette.BUILDING, Palette.BUSINESS, Palette.ORE,
                            Palette.MONEY_LIGHT, Palette.PEOPLE_LIGHT, Palette.BUILDING_LIGHT, Palette.BUSINESS_LIGHT};
        List<Part> parts = new ArrayList<>();
        int i = 0;
        for (Crime.Cause cause : Crime.Cause.values()) {
            Part p = Part.of(cause.label(), crime.getCrimes(cause), colours[i++ % colours.length]);
            if (cause == Crime.Cause.FEW_POLICE) p = p.go(() -> open("Safety", "Police"));
            if (cause == Crime.Cause.NO_HOME || cause == Crime.Cause.CROWDED) p = p.go(() -> ui.buildScreen.openCategory(BuildAdvice.HOMES));
            parts.add(p);
        }
        return parts;
    }

    /** Where the crime comes from, and what the police and the cells are doing about it. */
    void crimeOverview(VBox page, List<CityNeeds.Need> all) {
        Crime crime = ui.game.getCrime();
        BuildingManager bm = ui.game.getBuildingManager();
        double vs = crime.getRateVsCanada();
        String tone = tone(need(all, CityNeeds.Kind.CRIME, null, null));

        page.getChildren().add(sectionHead("WHERE THE CRIME COMES FROM", CAUSES_INFO,
                hint("police take a share off all of it; only work, homes and money take the reasons away")));
        double top = Math.max(2, Math.ceil(vs * 2) / 2);
        SegmentBar scale = segmentBar(List.of(new Segment(Math.min(vs, top), tone, false, null, null,
                        String.format("%.2fx Canada's rate", vs), null)), top,
                List.of(new Tick(1, Palette.TEXT_HEAD, 2, "Canada", "Canada's rate"),
                        new Tick(CityNeeds.CRIME_YELLOW, Palette.TEXT_MUTED, 1, null, "NEEDS YOU lists crime from here"),
                        new Tick(CityNeeds.CRIME_RED, Palette.TEXT_MUTED, 1, null, "...and it is red from here")), 190, 8);
        VBox left = new VBox(2, figure(flowText(crime.getCrimes()), 30, Palette.TEXT_HEAD),
                words("crimes a month · " + String.format("%.2fx", vs) + " Canada's", Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                infoLine(people(crime.getRatePer100k()) + " a year per 100,000",
                        String.format("This city is at %.2f times Canada's rate (%s a year per 100,000). Above it, "
                                + "fewer people move here and some leave.", vs, people(Crime.CANADA_CRIMES_PER_100K)),
                        false, Palette.SIZE_CAPTION, Palette.TEXT_MUTED, 210),
                scale);
        String d = change("crimeRate", Money::people);
        if (d != null) left.getChildren().add(words(d + " (a year per 100,000)", Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        left.setMinWidth(210);
        left.setPrefWidth(210);
        left.setMaxWidth(210);
        VBox right = new VBox(4, causeBar(crimeParts(), 0, 24, v -> String.format("%,.1f", v)));
        HBox.setHgrow(right, Priority.ALWAYS);
        right.setMaxWidth(Double.MAX_VALUE);
        HBox row = new HBox(18, left, right);
        page.getChildren().add(card(row));

        page.getChildren().add(sectionHead("THE POLICE, THE CELLS, AND WHAT IT DID", null, null));
        javafx.scene.layout.GridPane three = equalColumns(3, TILE_GAP);
        double[] police = BuildAdvice.supplyDemand(ui.game, BuildAdvice.Measure.of(BuildAdvice.Kind.POLICE), java.util.Map.of());
        double without = crime.crimesWithoutPolice();
        VBox p = card(cardHead(Icons.SAFETY, "Police", OFFICERS_INFO.apply(crime), null),
                new HBox(8, figure(without > 0 ? pct(1 - crime.getCrimes() / without) : "—", 22, Palette.TEXT_HEAD),
                        words("of the crime taken off", Palette.SIZE_LABEL, Palette.TEXT_LABEL)),
                supplyBar(police[1], bm.getSafetyCapacity(SafetyType.POLICE), police[0], Palette.PEOPLE, "full cover",
                        null, people(police[0]) + " on shift of " + people(bm.getSafetyCapacity(SafetyType.POLICE))
                        + " the stations hold · " + people(police[1]) + " for full cover", 0, 8),
                words(people(police[0]) + " on shift · " + people(police[1]) + " for full cover · " + pct(crime.getCoverage()),
                        Palette.SIZE_CAPTION, Palette.TEXT_MUTED),
                footDoors("Police", BuildAdvice.Measure.of(BuildAdvice.Kind.POLICE)));
        three.add(doorCard(p, () -> open("Safety", "Police"), "The police: click for their page."), 0, 0);
        double[] cells = BuildAdvice.supplyDemand(ui.game, BuildAdvice.Measure.of(BuildAdvice.Kind.CELLS), java.util.Map.of());
        VBox c = card(cardHead(Icons.CELL, "Cells", CAUGHT_INFO, null),
                new HBox(8, figure(people(crime.getNotHeld()), 22, crime.getNotHeld() >= .5
                                ? tone(need(all, CityNeeds.Kind.CELLS, null, null)) : Palette.TEXT_HEAD),
                        words("caught, not held", Palette.SIZE_LABEL, Palette.TEXT_LABEL)),
                supplyBar(cells[1], bm.getSafetyCapacity(SafetyType.PRISON), cells[0], Palette.PEOPLE, "needed",
                        null, people(cells[0]) + " staffed cells of " + people(bm.getSafetyCapacity(SafetyType.PRISON))
                        + " built · " + people(cells[1]) + " needed: six months of the caught", 0, 8),
                words(people(crime.prisoners()) + " in prison · " + people(cells[0]) + " staffed cells · "
                        + people(cells[1]) + " needed", Palette.SIZE_CAPTION, Palette.TEXT_MUTED),
                footDoors("Prisons", BuildAdvice.Measure.of(BuildAdvice.Kind.CELLS)));
        three.add(doorCard(c, () -> open("Safety", "Prisons"), "The prisons: click for their page."), 1, 0);
        VBox did = card(cardHead(Icons.ALERT, "What it did", STOLEN_INFO, null),
                didLine("violent", String.format("%,.1f", crime.getViolent()), String.format("%.2f%% of the city injured, off work", crime.getInjuredShare() * 100)),
                didLine("killed", String.format("%,.2f", crime.getKilled()), "next month's deaths among the adults"),
                didLine("property", String.format("%,.1f", crime.getProperty()),
                        money(crime.getStolenFromHouseholds()) + " from households · " + money(crime.getStolenFromBusinesses()) + " from businesses"),
                words("Since the founding: " + people(crime.getEverCrimes()) + " crimes, " + people(crime.getEverKilled())
                        + " killed, " + money(crime.getEverStolen()) + " stolen", Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        three.add(did, 2, 0);
        page.getChildren().add(three);
        page.getChildren().add(details("safety:causes", "the reasons as a table", causesTable()));
    }

    /** A card's two doors: its page, and Build on its ring. */
    HBox footDoors(String pageName, BuildAdvice.Measure m) {
        Region push = new Region();
        HBox.setHgrow(push, Priority.ALWAYS);
        HBox foot = new HBox(Palette.GAP, door("details", Palette.ACCENT, () -> open("Safety", pageName)), push, buildFor(m));
        foot.setAlignment(Pos.CENTER_LEFT);
        return foot;
    }

    /** One line of what the crime did: a count, its name, and a smaller line. */
    static VBox didLine(String name, String value, String line) {
        HBox top = new HBox(6, figure(value, Palette.SIZE_SECTION, Palette.TEXT_HEAD), words(name, Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        top.setAlignment(Pos.BASELINE_LEFT);
        return new VBox(0, top, words(line, Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
    }

    /** The causes' (i). */
    static final String CAUSES_INFO = "Every adult at liberty is counted once, at the heaviest reason they have. The"
            + " last part is not a group: it is every adult, tempted by the police the city"
            + " does not have. Police take a share off all of it; only work, homes and money"
            + " take the reasons away.";

    /** What is stolen, and the injured (the old "what it did" note). */
    static final String STOLEN_INFO = "What is stolen goes to the offenders' households. The killings are next"
            + " month's deaths among the adults; the injured are on this month's sick rate.";

    /** Anybody caught with no cell (the prisons' note). */
    static final String CAUGHT_INFO = "Anybody caught with no staffed cell free stays on the street and keeps"
            + " offending. The released go looking for work like anyone. A sentence is "
            + Crime.SENTENCE_MONTHS + " months, so the cells needed are six months of the caught.";

    /** The officers against Canada, and the founding constabulary. */
    static final java.util.function.Function<Crime, String> OFFICERS_INFO = crime ->
            String.format("%s officers per 100,000 people. Canada has %s; full coverage is twice that.",
                    people(crime.getOfficersPer100k()), people(Crime.CANADA_OFFICERS_PER_100K))
            + (SafetyType.POLICE.foundingCapacity() > 0 ? String.format(
                    " The first %s officers are the constabulary the city was founded with, enough for %s people.",
                    String.format("%.1f", SafetyType.POLICE.foundingCapacity()), people(Healthcare.FOUNDING_CITY)) : "");

    /** The old "Where it comes from" table: reason, adults, weight, crimes. */
    VBox causesTable() {
        Crime crime = ui.game.getCrime();
        javafx.scene.layout.GridPane from = grid(new double[] {170, 80, 64, 80}, rightAfterFirst(4));
        gridHead(from, "reason", "adults", "weight", "crimes");
        int line = 1;
        for (Crime.Cause cause : Crime.Cause.values()) {
            double crimes = crime.getCrimes(cause);
            double adults = cause.isGroup() ? crime.getPressure(cause) / cause.weight() : crime.getAdultsAtLiberty();
            double weight = cause.isGroup() ? cause.weight() : cause.weight() * (1 - crime.getCoverage());
            if (adults < .5 && crimes < .05) continue;
            from.add(gridCell(cause.label(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            from.add(gridCell(people(adults), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 1, line);
            from.add(gridCell(String.format("%.2f", weight), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
            from.add(gridCell(String.format("%,.1f", crimes),
                    cause == Crime.Cause.NO_CAUSE ? Palette.TEXT_MUTED : Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, line);
            line++;
        }
        return column(from, statementNote(CAUSES_INFO));
    }

    /** What the police are doing, and what more of them would: with no police against with these, the officers, one more station. */
    void policePage(VBox page) {
        Crime crime = ui.game.getCrime();
        BuildingManager bm = ui.game.getBuildingManager();
        double population = crime.getPopulation();
        double without = crime.crimesWithoutPolice();
        double[] police = BuildAdvice.supplyDemand(ui.game, BuildAdvice.Measure.of(BuildAdvice.Kind.POLICE), java.util.Map.of());

        page.getChildren().add(sectionHead("WHAT THE POLICE TAKE OFF", null,
                hint(without > 0 ? pct(1 - crime.getCrimes() / without) + " of the crime the reasons make" : "")));
        double scale = Math.max(without, crime.getCrimes());
        VBox bars = new VBox(6, crimeRow("with no police at all", without, scale, Palette.RAMP_REST),
                crimeRow("with these police", crime.getCrimes(), scale, Palette.PEOPLE),
                words(String.format("caught and sentenced: %,.1f (%.1f%% of crimes)", crime.getCaught(),
                        100 * Crime.caughtShare(crime.getCoverage())), Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        page.getChildren().add(card(bars));

        page.getChildren().add(sectionHead("THE OFFICERS", OFFICERS_INFO.apply(crime), hint(pct(crime.getCoverage()) + " of full cover")));
        page.getChildren().add(card(supplyBar(police[1], bm.getSafetyCapacity(SafetyType.POLICE), police[0], Palette.PEOPLE,
                        "full cover", null, null, 0, 12),
                words(people(police[0]) + " on shift · " + people(bm.getSafetyCapacity(SafetyType.POLICE))
                        + " the stations hold · " + people(police[1]) + " for full cover", Palette.SIZE_CAPTION, Palette.TEXT_MUTED),
                buildFor(BuildAdvice.Measure.of(BuildAdvice.Kind.POLICE))));

        BuildingsTemplate station = bm.getTemplateByName("Police Station");
        if (station != null && population > 0) {
            double more = Crime.coverageOf(crime.getOfficers() + station.getCapacity(), population);
            page.getChildren().add(infoLine(String.format("One more police station: coverage %.0f%% → %.0f%%, crimes a month %,.1f → %,.1f",
                            crime.getCoverage() * 100, more * 100, crime.getCrimes(), crime.crimesAt(more)),
                    "Past full coverage another station adds nothing: the rest of the crime is the city's reasons.",
                    false, Palette.SIZE_BODY, Palette.TEXT_HEAD, 900));
        }
        page.getChildren().add(details("safety:police", "the police as a statement", column(
                statementLine("Officers the buildings hold", people(bm.getSafetyCapacity(SafetyType.POLICE))),
                statementLine("...on shift, with the staff the city has", people(crime.getOfficers())),
                statementLine("Full coverage for this city", people(police[1])),
                statementTotal("Coverage", pct(crime.getCoverage()), null),
                statementLine("Crimes with no police at all", String.format("%,.1f", without)),
                statementLine("Crimes with these police", String.format("%,.1f", crime.getCrimes())),
                statementTotal("Taken off", without > 0 ? pct(1 - crime.getCrimes() / without) : "—", null))));
    }

    /** One of the police page's two bars: its name, the bar on the shared scale, the crimes a month. */
    static HBox crimeRow(String name, double crimes, double scale, String colour) {
        Label n = words(name, Palette.SIZE_LABEL, Palette.TEXT_LABEL);
        n.setMinWidth(150);
        n.setPrefWidth(150);
        SegmentBar bar = segmentBar(List.of(Segment.of(crimes, colour)), scale > 0 ? scale : 1, List.of(), 0, 16);
        HBox.setHgrow(bar, Priority.ALWAYS);
        HBox row = new HBox(Palette.GAP, n, bar, figure(String.format("%,.1f", crimes) + " a month", Palette.SIZE_BODY, Palette.TEXT_HEAD));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** Who is inside, and whether the city can hold who the police catch. */
    void prisonsPage(VBox page) {
        Crime crime = ui.game.getCrime();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] cells = BuildAdvice.supplyDemand(ui.game, BuildAdvice.Measure.of(BuildAdvice.Kind.CELLS), java.util.Map.of());

        page.getChildren().add(sectionHead("CAN THE CITY HOLD WHO THE POLICE CATCH", CAUGHT_INFO,
                hint(people(crime.prisoners()) + " in prison · " + people(crime.getPrisonersPer100k())
                        + " per 100,000 · Canada holds about " + people(Crime.CANADA_PRISONERS_PER_100K))));
        page.getChildren().add(card(supplyBar(cells[1], bm.getSafetyCapacity(SafetyType.PRISON), cells[0], Palette.PEOPLE,
                        "needed", null, null, 0, 12),
                words(people(cells[0]) + " staffed cells of " + people(bm.getSafetyCapacity(SafetyType.PRISON)) + " built · "
                        + people(cells[1]) + " needed: six months of the caught", Palette.SIZE_CAPTION, Palette.TEXT_MUTED),
                buildFor(BuildAdvice.Measure.of(BuildAdvice.Kind.CELLS))));

        page.getChildren().add(sectionHead("BY MONTHS SERVED", null, hint("the newest at the left; each month they move one along")));
        double[] months = new double[Crime.SENTENCE_MONTHS];
        for (int m = 0; m < months.length; m++) months[m] = crime.cohort(m);
        page.getChildren().add(card(cohortBars(months, 0, 90, Palette.PEOPLE, -1, null, null, "went in this month",
                "out next month", v -> String.format("%,.1f", v), m -> m == 0 ? "went in this month" : "month " + (m + 1))));

        page.getChildren().add(infoLine(String.format("This month: %,.1f caught · %,.1f sent down · %,.1f caught and not held · "
                        + "%,.1f released, sentence served", crime.getCaught(), crime.getAdmitted(), crime.getNotHeld(), crime.getReleased())
                        + (crime.getReleasedEarly() > .05 ? String.format(" · %,.1f released early, no staffed cell", crime.getReleasedEarly()) : ""),
                CAUGHT_INFO, false, Palette.SIZE_BODY, Palette.TEXT_HEAD, 1000));
        PrisonerHousehold ledger = ui.game.getHouseholdBalance().prisoners();
        page.getChildren().add(infoLine("The prisoners' ledger: " + money(ledger.totalSavings()) + " of savings held · "
                        + money(ledger.totalDebt()) + " of debt, frozen",
                "A prisoner's money waits for them: no interest runs on the debt, nothing is"
                + " invested, and it goes back out with them.", false, Palette.SIZE_BODY, Palette.TEXT_LABEL, 900));
        javafx.scene.layout.GridPane ring = grid(new double[] {120, 90}, rightAfterFirst(2));
        gridHead(ring, "month", "prisoners");
        for (int m = 0; m < Crime.SENTENCE_MONTHS; m++) {
            ring.add(gridCell(m == 0 ? "went in this month" : "month " + (m + 1), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, m + 1);
            ring.add(gridCell(String.format("%,.1f", crime.cohort(m)), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 1, m + 1);
        }
        page.getChildren().add(details("safety:prisons", "the prisons as a statement", column(
                statementLine("Cells the buildings hold", people(bm.getSafetyCapacity(SafetyType.PRISON))),
                statementLine("...staffed", people(crime.getCells())),
                statementTotal("In prison", people(crime.prisoners()), null),
                statementNote(String.format("%s per 100,000 people. Canada holds about %s.",
                        people(crime.getPrisonersPer100k()), people(Crime.CANADA_PRISONERS_PER_100K)))), ring));
    }

    /* =====================================================================
       THE BOOKS.

       One per system, and every service in this game has a set now. They are
       written as statements rather than as the old INCOME STATEMENT blocks of
       padded Courier, and they all say the same three things in the same order:
       what came in, what went out, and what it left the city carrying.

       WHAT THESE ARE NOT is the business income statements on the sector
       screen. A hospital is not trying to make money and neither is a school -
       the useful figure is not profit, it is what share of the cost comes back
       and where the rest of it went. So the bottom line here is NET COST and
       the line under it is cost recovery, and both are stated as costs rather
       than dressed up as revenue.

       DRAWN FIRST (0.7.28): each set opens on its cost against what came
       back, two bars on one money scale, the net beside them; every line and
       table it printed is in the fold under them.
       ===================================================================== */

    /** One line of a set of books: label, figure, and a colour when it matters. */
    HBox bookRow(String label, double thousands, String tone) {
        return statementLine(label, tightMoney(toDollars(thousands), false), tone);
    }

    /* --------------------------------- HEALTH --------------------------------- */

    void healthBooksPage(VBox page) {
        Healthcare service = ui.game.getHealthcare();
        page.getChildren().add(sectionHead("WHAT CARE COSTS, AND WHAT COMES BACK", null,
                hint("fees cover " + pct(service.getCostRecovery()) + " of it")));
        page.getChildren().add(moneyBars("it costs",
                List.of(new Slice("wages", service.getPayroll(), Palette.MONEY),
                        new Slice("buildings", service.getUpkeep(), Palette.MONEY_LIGHT)),
                "fees back",
                List.of(new Slice("treatment fees", service.getTreatmentFees(), Palette.PEOPLE),
                        new Slice("funeral fees", service.getFuneralFees(), Palette.PEOPLE_LIGHT)),
                bottomLine(tightMoney(toDollars(-service.getNetCost())), "net cost to the city a month",
                        pct(service.getCostRecovery()) + " comes back in fees", Palette.TEXT_HEAD)));
        page.getChildren().add(infoLine(service.getPricedOutTotal() > 0
                        ? people(service.getPricedOutTotal()) + " priced out of care this month"
                        : "nobody priced out of care this month", pricedOutWords(), false,
                Palette.SIZE_BODY, Palette.TEXT_HEAD, 900));
        page.getChildren().add(details("books:health", "every line and table", healthBooksStatement()));
    }

    /** ...and who the price turned away (2026-09-19): the dial is on the Policy tab's Health page; this line says what it did. */
    String pricedOutWords() {
        Healthcare service = ui.game.getHealthcare();
        return service.getPricedOutTotal() > 0
                ? String.format("Fees at x%.2f the founding fee. %s were priced out of care this month "
                        + "(childcare %s, general %s, senior %s): a household that cannot pay after its "
                        + "savings, its shares and its credit goes without care rather than without food.",
                        service.getFeeScale(), people(service.getPricedOutTotal()),
                        people(service.getPricedOut(CareType.CHILDCARE)),
                        people(service.getPricedOut(CareType.GENERAL)),
                        people(service.getPricedOut(CareType.SENIOR)))
                : String.format("Fees at x%.2f the founding fee, and nobody was priced out of care this month.",
                        service.getFeeScale());
    }

    /**
     * The old Health books, every line: what care charges for, what it costs,
     * where the money goes. AFTER A LOAD the month's patients are not kept
     * (Healthcare's served[] is not saved), so "seen" and "raised" read "—"
     * until a month runs, where they read nought beside the saved fees.
     */
    VBox healthBooksStatement() {
        Healthcare service = ui.game.getHealthcare();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] wages = ui.game.getPopulationManager().getWagesPerType();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();
        boolean measured = !healthNotRunYet();
        VBox column = column();

        column.getChildren().add(statementHead("What care charges for"));
        javafx.scene.layout.GridPane earns = grid(new double[] {124, 86, 82, 96}, rightAfterFirst(4));
        gridHead(earns, "care", "seen", "fee each", "raised");
        CareType[] living = {CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR};
        int line = 1;
        for (CareType care : living) {
            earns.add(gridCell(care.getLabel(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            earns.add(gridCell(measured ? people(service.getServed(care)) : "—", Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, true), 1, line);
            earns.add(gridCell(cash(service.feeNow(care)), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
            earns.add(gridCell(measured ? tightMoney(toDollars(service.feesFrom(care))) : "—",
                    Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, line);
            line++;
        }
        earns.add(gridCell("burials", Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
        earns.add(gridCell(people(service.getBurials()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 1, line);
        earns.add(gridCell(cash(service.feeNow(CareType.BURIAL)), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
        earns.add(gridCell(tightMoney(toDollars(service.getBurials() * service.feeNow(CareType.BURIAL))),
                Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, line);
        line++;
        earns.add(gridCell("cremations", Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
        earns.add(gridCell(people(service.getCremations()), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 1, line);
        earns.add(gridCell(cash(service.feeNow(CareType.CREMATION)), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
        earns.add(gridCell(tightMoney(toDollars(service.getCremations() * service.feeNow(CareType.CREMATION))),
                Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, line);
        column.getChildren().add(earns);
        if (!measured) {
            column.getChildren().add(statementNote("This city was loaded and no month has run since: who was "
                    + "treated is counted when a month runs, so \"seen\" and \"raised\" wait for it. The month's "
                    + "treatment fees below were kept by the save."));
        }

        column.getChildren().add(bookRow("Treatment fees", service.getTreatmentFees(), null));
        column.getChildren().add(bookRow("Funeral fees", service.getFuneralFees(), null));
        column.getChildren().add(statementTotal("Fees collected", tightMoney(toDollars(service.getFees()), false), null));
        column.getChildren().add(statementNote(pricedOutWords()));

        /* ------------------------------ what it costs ------------------------------ */
        column.getChildren().add(statementHead("What it costs"));
        column.getChildren().add(bookRow("Wages", -service.getPayroll(), null));
        column.getChildren().add(bookRow("Buildings", -service.getUpkeep(), null));
        column.getChildren().add(statementTotal("Gross cost", tightMoney(toDollars(-service.getGrossCost()), false), null));
        column.getChildren().add(bookRow("Fees back", service.getFees(), null));
        column.getChildren().add(statementTotal("Net cost to the city", tightMoney(toDollars(-service.getNetCost()), false), null));
        column.getChildren().add(statementNote(String.format(
                "Fees cover %.0f%% of it. A hospital is not trying to make money — what "
                + "the rest buys is on the four pages behind this one, in deaths avoided "
                + "and work done.", service.getCostRecovery() * 100)));

        /* --------------------------- where the money goes --------------------------- */
        column.getChildren().add(subHead("Which kind of care the money goes on"));
        javafx.scene.layout.GridPane split = grid(new double[] {124, 78, 86, 82, 90}, rightAfterFirst(5));
        gridHead(split, "care", "places", "wages", "buildings", "net");
        line = 1;
        double namedCost = 0;
        for (CareType care : new CareType[]{CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR,
                                            CareType.BURIAL, CareType.CREMATION}) {
            double pay = bm.getCarePayroll(care, wages, staffing);
            double keep = bm.getCareUpkeep(care);
            if (pay <= 0 && keep <= 0 && bm.getCareCapacity(care) <= 0) continue;
            namedCost += pay + keep;
            double back = care == CareType.BURIAL ? service.getBurials() * service.feeNow(care)
                    : care == CareType.CREMATION ? service.getCremations() * service.feeNow(care)
                    : service.feesFrom(care);
            double net = pay + keep - back;
            split.add(gridCell(care.getLabel(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            split.add(gridCell(people(bm.getCareCapacity(care)), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 1, line);
            split.add(gridCell(tightMoney(toDollars(pay)), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 2, line);
            split.add(gridCell(tightMoney(toDollars(keep)), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, line);
            split.add(gridCell(measured || care == CareType.BURIAL || care == CareType.CREMATION
                    ? tightMoney(toDollars(-net)) : "—", Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        column.getChildren().add(split);
        column.getChildren().add(statementNote(
                "Wages here are the posts each building holds, discounted by how many of "
                + "them are filled — the same figure the coverage pages are discounted by, "
                + "so an understaffed ward is cheap and useless at the same time."));

        /*
         * THE ENDOWMENT, which has no building and therefore no cost. Worth a
         * line, because a player reading this table and adding it up will be
         * short of the gross cost by exactly the founding capacity's share and
         * will go looking for the missing money.
         */
        double gap = service.getGrossCost() - namedCost;
        if (Math.abs(toDollars(gap)) > 1) {
            column.getChildren().add(statementLine("Not in the table above", tightMoney(toDollars(gap), false), Palette.TEXT_MUTED));
            column.getChildren().add(statementNote(
                    "Healthcare buildings that provide no care type of their own, and the "
                    + "doctor, nursery, almshouse and churchyard the city was founded with "
                    + "— those have capacity and no payroll, which is why the coverage "
                    + "pages count them and this table does not."));
        }
        return column;
    }

    /* -------------------------------- EDUCATION -------------------------------- */

    void educationBooksPage(VBox page) {
        Education schools = ui.game.getEducation();
        page.getChildren().add(sectionHead("WHAT THE SCHOOLS COST, AND WHO PAYS THE TUITION", null,
                hint("the subsidy dial is at " + pct(schools.getTuitionSubsidy()))));
        page.getChildren().add(moneyBars("they cost",
                List.of(new Slice("teachers", schools.getPayroll(), Palette.MONEY),
                        new Slice("buildings", schools.getUpkeep(), Palette.MONEY_LIGHT)),
                "tuition billed",
                List.of(new Slice("households paid", schools.getFees(), Palette.PEOPLE),
                        new Slice("the city forgave", schools.getSubsidy(), Palette.RAMP_REST)),
                bottomLine(tightMoney(toDollars(-schools.getNetCost())), "net cost to the city a month",
                        pct(schools.getCostRecovery()) + " comes back in tuition", Palette.TEXT_HEAD)));
        page.getChildren().add(infoLine("What the city forgave is tuition it did not charge, not a cost: "
                        + money(schools.getSubsidy()) + " this month",
                "The schools ARE the city, so the subsidy is the city declining to bill itself - forgone revenue, "
                + "not a second cheque. It is not in the cost (Education.getGrossCost() is staff and buildings); a "
                + "higher subsidy means less tuition collected, so the city recovers less of the same cost.",
                false, Palette.SIZE_BODY, Palette.TEXT_LABEL, 900));
        page.getChildren().add(details("books:education", "every line and table", educationBooksStatement()));
    }

    /**
     * The old Education books, every line but two: "Tuition the city covers"
     * listed as a cost and the alert that the subsidy was counted twice. The
     * model stopped counting it (Education.getGrossCost()), so the lines over
     * "Gross cost" no longer added up to it, and the alert compared a figure
     * with itself.
     */
    VBox educationBooksStatement() {
        Education schools = ui.game.getEducation();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] wages = ui.game.getPopulationManager().getWagesPerType();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();
        double subsidy = schools.getTuitionSubsidy();
        VBox column = column();

        column.getChildren().add(statementHead("What tuition raises"));
        javafx.scene.layout.GridPane fees = grid(new double[] {130, 74, 74, 84, 84}, rightAfterFirst(5));
        gridHead(fees, "course", "in class", "a seat", "households", "the city");
        int line = 1;
        double billed = 0;
        for (EducationType course : EducationType.values()) {
            if (course == EducationType.NONE) continue;
            double students = schools.getEnrolled(course);
            if (students < .5) continue;
            double charge = schools.feeFor(course) * students;
            billed += charge;
            fees.add(gridCell(course.getLabel(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            fees.add(gridCell(people(students), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 1, line);
            fees.add(gridCell(cash(schools.feeFor(course)), Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
            fees.add(gridCell(tightMoney(toDollars(charge * (1 - subsidy))), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, line);
            fees.add(gridCell(tightMoney(toDollars(charge * subsidy)), Palette.ACCENT, Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        if (line == 1) {
            column.getChildren().add(sentence("Nobody is in a classroom this month, so nothing was billed.", Palette.TEXT_MUTED));
        } else {
            column.getChildren().add(fees);
        }
        column.getChildren().add(bookRow("Billed in tuition", billed, null));
        column.getChildren().add(bookRow("Households paid", schools.getFees(), null));
        column.getChildren().add(bookRow("The city forgave", schools.getSubsidy(), Palette.ACCENT));
        column.getChildren().add(statementNote(String.format(
                "The subsidy dial is at %.0f%%, so that is the split. It is one dial for "
                + "every course in the city.", subsidy * 100)));

        /* ------------------------------ what it costs ------------------------------ */
        column.getChildren().add(statementHead("What it costs"));
        column.getChildren().add(bookRow("Teachers", -schools.getPayroll(), null));
        column.getChildren().add(bookRow("Buildings", -schools.getUpkeep(), null));
        column.getChildren().add(statementTotal("Gross cost", tightMoney(toDollars(-schools.getGrossCost()), false), null));
        column.getChildren().add(bookRow("Tuition collected", schools.getFees(), null));
        column.getChildren().add(statementTotal("Net cost to the city", tightMoney(toDollars(-schools.getNetCost()), false), null));
        column.getChildren().add(statementNote(String.format("Tuition covers %.0f%% of it.", schools.getCostRecovery() * 100)));

        /* --------------------------- where the money goes --------------------------- */
        column.getChildren().add(subHead("Which schools the money goes on"));
        javafx.scene.layout.GridPane split = grid(new double[] {130, 74, 86, 82, 90}, rightAfterFirst(5));
        gridHead(split, "course", "seats", "teachers", "buildings", "net");
        line = 1;
        for (EducationType course : EducationType.values()) {
            if (course == EducationType.NONE) continue;
            double pay = bm.getSchoolPayroll(course, wages, staffing);
            double keep = bm.getSchoolUpkeep(course);
            if (pay <= 0 && keep <= 0) continue;
            double back = schools.feeFor(course) * schools.getEnrolled(course) * (1 - subsidy);
            double net = pay + keep - back;
            split.add(gridCell(course.getLabel(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            split.add(gridCell(people(bm.getBuiltEducationPlaces()[course.ordinal()]), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 1, line);
            split.add(gridCell(tightMoney(toDollars(pay)), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 2, line);
            split.add(gridCell(tightMoney(toDollars(keep)), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, line);
            split.add(gridCell(tightMoney(toDollars(-net)), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        if (line > 1) column.getChildren().add(split);
        column.getChildren().add(statementNote(
                "A course with seats and no students still costs its teachers and its "
                + "upkeep every month — which is what makes a school built in the wrong "
                + "city expensive rather than merely idle."));
        return column;
    }

    /* -------------------------------- UTILITIES -------------------------------- */

    void utilityBooksPage(VBox page) {
        UtilitiesHandler uh = ui.game.getServicesManager().getUtilitiesHandler();
        double elecSales = uh.getElectricityRevenue(), elecPay = uh.getElectricityPayroll();
        double waterSales = uh.getWaterRevenue(), waterPay = uh.getWaterPayroll();
        double net = ui.game.getServicesManager().getServiceNetIncome();
        double roadUpkeep = roadUpkeep();

        page.getChildren().add(sectionHead("DO THE PLANTS PAY FOR THEMSELVES", null,
                hint("the city's own plants: what they sell less their wages is the city's")));
        page.getChildren().add(moneyBars("power: sales",
                List.of(new Slice("power sales", elecSales, Palette.PEOPLE)),
                "power: wages",
                List.of(new Slice("power wages", elecPay, Palette.MONEY)),
                bottomLine(tightMoney(toDollars(elecSales - elecPay)), "power's net income",
                        power(uh.getBilledElectricityDraw()) + " billed, " + pct(billedShare(uh.getBilledElectricityDraw(), uh.getConsumption())) + " of the draw",
                        Palette.TEXT_HEAD)));
        page.getChildren().add(moneyBars("water: sales",
                List.of(new Slice("water sales", waterSales, Palette.PEOPLE)),
                "water: wages",
                List.of(new Slice("water wages", waterPay, Palette.MONEY)),
                bottomLine(tightMoney(toDollars(waterSales - waterPay)), "water's net income",
                        pct(billedShare(uh.getBilledWaterDraw(), uh.getWaterConsumption())) + " of it billed",
                        Palette.TEXT_HEAD)));
        page.getChildren().add(new HBox(24,
                bottomLine(tightMoney(toDollars(net)), "both together, a month", "staff turnout " + pct(uh.getAverageUtilityFill()), Palette.TEXT_HEAD),
                bottomLine(tightMoney(toDollars(-roadUpkeep)), "the roads' upkeep, a month", "nobody is tolled", Palette.TEXT_HEAD)));
        page.getChildren().add(details("books:utilities", "every line", utilityBooksStatement()));
    }

    /**
     * THE ROADS' REPAIR BILL, which until 2026-09-09 could only ever read
     * ZERO. This asked BuildingManager for the INFRASTRUCTURE category's
     * `upkeep`, and no road has ever had an upkeep figure - the field was
     * only ever filled in for healthcare and education. So the Services
     * screen carried a line that was structurally incapable of being
     * non-zero, which is worse than not carrying it: a player reads $0 and
     * concludes roads are free to keep.
     *
     * They are not, and now the model says so. This is the real charge,
     * from the same per-category maintenance the whole city pays.
     */
    double roadUpkeep() {
        return ui.game.getEconomyManager().maintenanceBillFor(BuildingType.INFRASTRUCTURE,
                Math.max(0, ui.game.getBuildingManager().getConstructionMaterialPrice()));
    }

    /** The share of a draw that is billed: the measured share, where the books said "four fifths" (0.7.28). */
    static double billedShare(double billed, double draw) { return draw > 0 ? billed / draw : 0; }

    /**
     * The old utilities' books, every line - but: kW for watts; the unbilled
     * draw is the homes and the city's own buildings, not "resident draw";
     * households are not billed, rather than "have no cash account" (they
     * have balance sheets); the water's "four fifths" unpaid for is the
     * measured share; and the closing note no longer calls the utilities a
     * private business the city regulates - their net income is the city's.
     */
    VBox utilityBooksStatement() {
        UtilitiesHandler uh = ui.game.getServicesManager().getUtilitiesHandler();
        double elecSales = uh.getElectricityRevenue(), elecPay = uh.getElectricityPayroll();
        double waterSales = uh.getWaterRevenue(), waterPay = uh.getWaterPayroll();
        double roadUpkeep = roadUpkeep();
        double net = ui.game.getServicesManager().getServiceNetIncome();
        /*
         * WHAT THEY SELL, unlike the two above it. The utilities sell what
         * they make and the figure at the bottom is an income rather than a
         * net cost - which is why this page reads the other way up, and why
         * it is the only one of the four whose bottom line can be positive.
         */
        return column(statementHead("Electric power"),
                bookRow("Sales", elecSales, null),
                bookRow("Wages", -elecPay, null),
                statementTotal("Net income", tightMoney(toDollars(elecSales - elecPay), false), null),
                statementNote(String.format("%s billed at %s a kW a month. The other %s is the homes and the "
                        + "city's own buildings, which is real load and no revenue.",
                        power(uh.getBilledElectricityDraw()), cash(uh.getPricerPerWatt()), power(uh.getUnbilledElectricityDraw()))),
                statementHead("Water"),
                bookRow("Sales", waterSales, null),
                bookRow("Wages", -waterPay, null),
                statementTotal("Net income", tightMoney(toDollars(waterSales - waterPay), false), null),
                statementNote(String.format("%s of %s units billed, %s of it. Households are not billed for "
                        + "power or water, so the rest is supplied and never paid for.",
                        people(uh.getBilledWaterDraw()), people(uh.getWaterConsumption()),
                        pct(billedShare(uh.getBilledWaterDraw(), uh.getWaterConsumption())))),
                statementHead("Both together"),
                bookRow("Revenue", elecSales + waterSales, null),
                bookRow("Payroll", -(elecPay + waterPay), null),
                statementTotal("Net income", tightMoney(toDollars(net), false), null),
                statementLine("Staff turnout", pct(uh.getAverageUtilityFill())),
                statementNote("One workforce runs both, so they cannot be short-staffed independently."),
                statementHead("Roads"),
                bookRow("Revenue", 0, Palette.TEXT_SPENT),
                bookRow("Upkeep", -roadUpkeep, null),
                statementTotal("Net cost to the city", tightMoney(toDollars(-roadUpkeep), false), null),
                statementNote(roadUpkeep > 0
                        ? "Nobody is tolled. Roads are the one utility with nothing to sell, so "
                          + "the only one that can never pay for itself — what it buys is on "
                          + "Infrastructure's Roads page, in output that happens instead of not happening."
                        : "Nobody is tolled, and no road in the catalogue carries an upkeep cost at "
                          + "all — the network is bought once and runs free for ever after. Which "
                          + "is not quite true even inside this model: every road draws power, so "
                          + "its running cost is real and lands on the grid's bill above rather "
                          + "than on this line."),
                statementNote("These are the utilities' own books. The plants are the city's own: what they "
                        + "sell less their wages goes into the city's cash each month, which is why this page "
                        + "has an income at the bottom and the other three have a cost."));
    }

    /* --------------------------------- SAFETY --------------------------------- */

    /** What it costs. */
    void safetyBooksPage(VBox page) {
        Crime crime = ui.game.getCrime();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] wages = ui.game.getPopulationManager().getWagesPerType();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();
        double policePay = bm.getSafetyPayroll(SafetyType.POLICE, wages, staffing);
        double policeKeep = bm.getSafetyUpkeep(SafetyType.POLICE);
        double prisonPay = bm.getSafetyPayroll(SafetyType.PRISON, wages, staffing);
        double prisonKeep = bm.getSafetyUpkeep(SafetyType.PRISON);

        page.getChildren().add(sectionHead("WHAT SAFETY COSTS", null, hint("no fees: the city pays all of it")));
        page.getChildren().add(moneyBars("the police",
                List.of(new Slice("police wages", policePay, Palette.MONEY),
                        new Slice("police buildings", policeKeep, Palette.MONEY_LIGHT)),
                "the prisons",
                List.of(new Slice("prison wages", prisonPay, Palette.PEOPLE),
                        new Slice("prison upkeep, food included", prisonKeep, Palette.PEOPLE_LIGHT)),
                bottomLine(tightMoney(toDollars(-crime.getGrossCost())), "cost to the city a month", perHead(crime,
                        policePay + policeKeep, prisonPay + prisonKeep), Palette.TEXT_HEAD)));
        page.getChildren().add(infoLine("Crime took " + money(crime.getStolenFromHouseholds()) + " from households and "
                        + money(crime.getStolenFromBusinesses()) + " from businesses this month",
                "A theft moves money; it does not destroy it. What the businesses lose shows on"
                + " their cash flow as its own line.", false, Palette.SIZE_BODY, Palette.TEXT_LABEL, 900));
        page.getChildren().add(details("books:safety", "every line", column(
                statementHead("What it costs"),
                bookRow("Police wages", -policePay, null),
                bookRow("Police buildings", -policeKeep, null),
                bookRow("Prison wages", -prisonPay, null),
                bookRow("Prison upkeep, food included", -prisonKeep, null),
                statementTotal("Cost to the city", tightMoney(toDollars(-crime.getGrossCost()), false), null),
                crime.getOfficers() > 0 ? statementNote(perHead(crime, policePay + policeKeep, prisonPay + prisonKeep) + ".") : null,
                statementHead("What crime cost the city's people"),
                bookRow("Stolen from households", -crime.getStolenFromHouseholds(), null),
                bookRow("Stolen from businesses", -crime.getStolenFromBusinesses(), null),
                bookRow("...handed to the offenders' households", crime.getStolen(), null),
                statementNote("A theft moves money; it does not destroy it. What the businesses lose shows on"
                        + " their cash flow as its own line."))));
    }

    /** "$X an officer on shift a year, and $Y a prisoner a year" - the officers past the founding constabulary, which costs nothing. */
    static String perHead(Crime crime, double police, double prisons) {
        if (crime.getOfficers() <= 0) return null;
        return String.format("%s an officer on shift a year, and %s a prisoner a year",
                tightMoney(toDollars(police * 12 / Math.max(1, crime.getOfficers() - SafetyType.POLICE.foundingCapacity())), false),
                crime.prisoners() >= .5 ? tightMoney(toDollars(prisons * 12 / crime.prisoners()), false) : "nothing");
    }
}
