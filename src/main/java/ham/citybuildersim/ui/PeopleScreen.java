package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * The People tab and its second page, Household money.
 *
 * Since 0.7.27 in the style Build got in 0.7.24 and 0.7.25 and the land
 * office in 0.7.26 (Jerus, on the screens not yet redone: "the others are
 * still full of text and the design could be more intuitive and fun").
 * People is one scrolling page that leads with pictures - the age pyramid
 * with a settled city's shape as a ghost, the month as a waterfall, why
 * people come as a bridge, care as Build's rings, the homes as a gauge, the
 * households as a mosaic of who lives in them, the people outside the
 * families as tiles, and the skill ladder as bars - with every table behind
 * "details" and every paragraph behind an (i). Household money is a page of
 * its own, joined to People by a two-chip strip in the head: the grid of
 * household cells with the open cell's books beside it, the city's month as
 * a waterfall, and what the households have put by.
 *
 * Nothing the old page printed is gone: the design study's inventory (the
 * project's spec-people-0727.md, section 1) says where each figure and each
 * paragraph went. What the page says is worked out in methods that make no
 * node - peopleCells(), homes(), bridge(), careLine(), outsideTiles(),
 * ladder(), verdict(), cityMonth() and the rest - so a probe can read every
 * word on a played city without the toolkit; the drawing methods lay them
 * out.
 *
 * Split out of UserInterface on 2026-09-18. The panels the grid opens into
 * (THE MONEY BLOCKS BOTH PANELS SHARE) and the tier table's bar are as they
 * were, but for the fees named apart and the rows below the rule measured
 * against a basket (0.7.27). The page reveals the open cell itself now:
 * revealTop and revealBottom are its own, read by its own scroll.
 */
final class PeopleScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    PeopleScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       PEOPLE (0.7.27)

       The question is "why is my city this size, and is anything wrong with
       the people in it?", and the page answers it top down:

         the head        - "People" with the teal swatch, its (i) (what the
                           model does not do yet), and the strip to
                           Household money;
         the vitals      - five cells, each a door to where it is decided;
         who and the month - the pyramid, a settled city's shape as a ghost
                           behind it, beside the month as a waterfall;
         why they come   - the draw as a bridge, jobs and homes to the
                           target, and the city against it;
         care            - four rings, Build's, each a door to Build;
         homes and households - the homes as a gauge, the households as a
                           mosaic of who lives in them;
         outside the families - five tiles, the pool's month and EI;
         work            - the skill ladder as bars.

       ONE PAGE STILL (Jerus, 2026-09-08: "fill it up, with the population
       info so that all the info is visible"); what made the old one long was
       its eleven statement sections, five tables and twenty-seven
       paragraphs, and they are behind "details" and the (i)s now.

       VERDICT COLOURS ARE FOR VERDICTS. The pyramid's bands are three steps
       of the people teal, never green (the working bands were GOOD before);
       the waterfall's columns and the mosaic's tiles are categories. Green,
       amber and red are on the vitals, the rings, the homes' verdict and a
       shortfall of doors, the why line and its housing ring, the labour line
       and UNFILLED, the pay chips over 1.05x, the chips that name a trouble
       (the month's events, an outbreak, a shrinking trade, a profession short
       of its licences), the No home and Orphans counts, the alerts, and on
       Household money the verdict, the cells, the month's Saved and the
       put-by figures.
       ===================================================================== */

    /*
     * HOW MUCH OF THE LABOUR FORCE OUT OF WORK IS A VERDICT (named in 0.7.21,
     * when the header's OUT OF WORK tile began reading them too). Under the
     * first, nobody is spare and every new job goes unfilled: a labour
     * shortage, which is a watch, not a failure - it was red until 0.7.21,
     * the colour of a city in trouble, and "Out of work 0.0%" in a city that
     * cannot staff its jobs read as the opposite of what it was.
     */

    /** Under this share out of work, nobody is spare: amber, "jobs going unfilled". */
    static final double OUT_OF_WORK_SHORT = .03;

    /** Over this, high: amber. */
    static final double OUT_OF_WORK_HIGH = .15;

    /** Over this, far too many adults with nothing to do: red. */
    static final double OUT_OF_WORK_FAR = .25;

    /** The out-of-work rate's verdict: red far too high, amber high or short of hands, green between. */
    static String outOfWorkTone(double jobless) {
        return jobless > OUT_OF_WORK_FAR ? Palette.BAD
                : jobless > OUT_OF_WORK_HIGH || jobless < OUT_OF_WORK_SHORT ? Palette.WARN
                : Palette.GOOD;
    }

    /** Over this share of the city eating less than a basket, GOING SHORT is amber; the dashboard's HUNGRY reads it too (0.7.27). */
    static final double GOING_SHORT_WARN = .01;

    /** ...and over this, red. */
    static final double GOING_SHORT_BAD = .10;

    /** GOING SHORT's verdict, on the share of people eating less than a basket (HouseholdBalance.getHungerRate()). */
    static String goingShortTone(double hunger) {
        return hunger > GOING_SHORT_BAD ? Palette.BAD : hunger > GOING_SHORT_WARN ? Palette.WARN : Palette.GOOD;
    }

    /** OFF SICK's verdict: red over 12% of the workforce, amber over 6%. */
    static String sickTone(double sick) {
        return sick > .12 ? Palette.BAD : sick > .06 ? Palette.WARN : Palette.GOOD;
    }

    /* ------------------- what the page remembers between draws -------------------
     *
     * Only the moving parts and what the player opened. Every figure is read
     * from the model on every draw.
     * ------------------------------------------------------------------------- */

    /** The month at the last draw of the page being watched, or -1: a month landing on it counts LIVING HERE up. */
    private int shownMonth = -1;

    /** Whether the month's waterfall shows the last twelve months, from the history, instead. */
    boolean yearView = false;

    /** Which "details" are open, by name; kept while the game runs, as the page's place is. */
    final java.util.Set<String> detailsOpen = new java.util.HashSet<>();

    /** The cards the vitals' doors scroll to, as last drawn. */
    private Region monthCard, homesCard, workCard;

    /** A new city or a load: nothing has just landed. */
    void forget() {
        shownMonth = -1;
    }

    void showPopulationInfoMenu() {
        // The count-up is for a page being watched as the month turns, as the clock's is: arriving, nothing moves.
        if (!"showPopulationInfoMenu".equals(ui.currentScreen)) shownMonth = -1;
        boolean landed = shownMonth >= 0 && shownMonth != ui.game.getMonth();
        ui.clearMenu("showPopulationInfoMenu", () -> showPopulationInfoMenu());

        VBox page = widePage();
        page.getChildren().add(pageHead("People", Palette.PEOPLE, null, null, STILL_FAKED,
                pageChips(true)));

        VBox vitals = vitals();
        page.getChildren().add(vitals);

        javafx.scene.layout.GridPane rowA = equalColumns(2, TILE_GAP);
        Pieces.Waterfall[] grow = new Pieces.Waterfall[1];
        rowA.add(pyramidCard(), 0, 0);
        VBox month = monthCard(grow);
        rowA.add(month, 1, 0);
        monthCard = month;
        page.getChildren().add(rowA);

        page.getChildren().addAll(
                sectionHead("WHY PEOPLE COME", WHY_INFO, hint("how big a city this good draws")),
                bridgeCard(),
                sectionHead("CARE", hint("Build's rings: click one to build for it")),
                careRow());
        page.getChildren().addAll(careAlerts());

        javafx.scene.layout.GridPane rowD = new javafx.scene.layout.GridPane();
        rowD.setHgap(TILE_GAP);
        rowD.setMaxWidth(Double.MAX_VALUE);
        javafx.scene.layout.ColumnConstraints left = new javafx.scene.layout.ColumnConstraints();
        left.setPercentWidth(33);
        left.setFillWidth(true);
        javafx.scene.layout.ColumnConstraints right = new javafx.scene.layout.ColumnConstraints();
        right.setPercentWidth(67);
        right.setFillWidth(true);
        rowD.getColumnConstraints().addAll(left, right);
        VBox homes = homesCard();
        homesCard = homes;
        rowD.add(homes, 0, 0);
        rowD.add(householdsCard(), 1, 0);
        page.getChildren().addAll(sectionHead("HOMES AND HOUSEHOLDS", hint("who has a door, and who lives behind it")), rowD);

        page.getChildren().add(sectionHead("OUTSIDE THE FAMILIES", OUTSIDE_INFO,
                hint("nobody's household, and no wage")));
        page.getChildren().addAll(outsideRow());

        VBox work = workCard();
        workCard = work;
        page.getChildren().addAll(sectionHead("WORK", hint("is there work, and who can do it")), work);

        ui.rootMenu.getChildren().add(page);

        // A month that landed while the page was watched: the headcount counts and the month's bars grow.
        if (landed) {
            countUp(vitals);
            if (grow[0] != null) grow[0].animate(COUNT_MILLIS);
        }
        shownMonth = ui.game.getMonth();
    }

    /** How long the headcount counts and the month's bars grow when a month lands (Jerus's "fun": the number moves). */
    static final double COUNT_MILLIS = 600;

    /** The two-chip strip in both pages' heads: People and Household money. */
    javafx.scene.layout.FlowPane pageChips(boolean onPeople) {
        String[] names = onPeople ? new String[] {"People", "Household money ›"}
                : new String[] {"‹ People", "Household money"};
        javafx.scene.layout.FlowPane chips = chipStrip(names, onPeople ? names[0] : names[1], Palette.SIZE_LABEL,
                name -> {
                    if (name.equals(names[0]) && !onPeople) showPopulationInfoMenu();
                    else if (name.equals(names[1]) && onPeople) showHouseholdMenu();
                });
        chips.setAlignment(Pos.CENTER_RIGHT);
        chips.setPrefWrapLength(320);
        chips.setMinWidth(Region.USE_PREF_SIZE);
        return chips;
    }

    /**
     * What the model does not do yet: the four lines of the old page's eight
     * that are still true (0.7.27). Cut as no longer true: births respond to
     * care (Healthcare.birthFactor()), education gates work (the ladder and
     * the licences), the households remember (since 2026-09-11), and crowding
     * costs (RealEstate's scarcity rent and Migration.affordabilityPull()).
     */
    static final String STILL_FAKED = "What this model does not do yet:\n"
            + "· Nobody has an age. Each band holds a mean, so an intake spreads out rather than "
            + "moving through as a wave.\n"
            + "· Mortality is one figure per band, so an adult of twenty carries the same risk as "
            + "one of sixty-nine.\n"
            + "· Arrivals copy the city's age mix, so a growing city imports its own dependency "
            + "ratio. Real migrants skew young.\n"
            + "· No multi-generational households, and couples share one tier.";

    /* =====================================================================
       THE WORDS. Everything the page says, worked out without a node, each
       from the model's own getters. The drawing further down lays them out.
       ===================================================================== */

    /** One of the vitals: what it reads, its note, its verdict and where its door goes. */
    record Cell(String label, String value, String note, String tone, String where) { }

    /** The five vitals: living here, out of work, the homes, off sick, going short. */
    Cell[] peopleCells() {
        PopulationManager pm = ui.game.getPopulationManager();
        double net = month(false).net();
        double jobless = pm.getUnemploymentRate();
        int unfilled = 0;
        for (int v : pm.getJobVacancy()) unfilled += v;
        Homes h = homes();
        double sick = ui.game.getHealth().getSickRate();
        double hunger = ui.game.getHouseholdBalance().getHungerRate();
        return new Cell[] {
            new Cell("LIVING HERE", formatter.format(pm.getPopulation()),
                    flowSigned(net >= 0 ? "+" : "-", Math.abs(net)) + " a month",
                    net >= 0 ? Palette.TEXT_HEAD : Palette.BAD, "Why the number moved: this month, below"),
            new Cell("OUT OF WORK", String.format("%.1f%%", jobless * 100),
                    outOfWorkNote(jobless, pm.getUnemployed(), unfilled), outOfWorkTone(jobless),
                    "Who works, and at what: the Work card below"),
            new Cell(!h.isShort() ? "SPARE HOMES" : "HOMES SHORT", people(Math.max(0, h.isShort() ? -h.spare() : h.spare())),
                    h.cellNote(), h.tone(), "Who has a door: the Homes card below"),
            new Cell("OFF SICK", String.format("%.1f%%", sick * 100),
                    people(pm.getWorkforce() * sick) + " of the workforce", sickTone(sick),
                    "Services › Health › General care"),
            new Cell("GOING SHORT", String.format("%.1f%%", hunger * 100), goingShortNote(),
                    goingShortTone(hunger), "What a month leaves each kind of household: Household money"),
        };
    }

    /** OUT OF WORK's note: the count, and the verdict the old page printed under it. */
    static String outOfWorkNote(double jobless, double unemployed, int unfilled) {
        if (jobless < OUT_OF_WORK_SHORT) {
            return unfilled > 0 ? "nobody spare · every new job goes unfilled"
                    : people(unemployed) + " with nothing to do · nobody spare";
        }
        String n = people(unemployed) + " with nothing to do";
        return jobless > OUT_OF_WORK_FAR ? n + " · far too many"
                : jobless > OUT_OF_WORK_HIGH ? n + " · high" : n + " · a healthy slack";
    }

    /**
     * GOING SHORT's note: which of the two hungers it is (0.7.27). The share
     * is mostly empty shelves in an old city and the page never said so -
     * HouseholdBalance.getHungryAtFullShelves() is the money half, and the
     * share the shops handed over (getDeliveredShare()) says the rest.
     */
    String goingShortNote() {
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        if (bal.getHungryPeople() < .5) return "everybody can eat";
        double money = Math.min(bal.getHungryPeople(), bal.getHungryAtFullShelves());
        double handed = bal.getDeliveredShare();
        // Full shelves and nobody short of money, and still hungry: a save from before the split was
        // kept, read before its first month. The old words until the month runs.
        if (handed >= 1 && money <= 0) return "eating less than a full basket";
        String shelves = String.format("the shops handed over %s of what was planned", shareWords(handed));
        if (handed >= .995) return flowText(money) + " can't afford a full basket";
        if (money < .5) return shelves;
        return flowText(money) + " can't afford · " + shelves;
    }

    /** A share in words, "under 1%" when it rounds to nothing but is not nothing. */
    static String shareWords(double share) {
        double p = share * 100;
        if (p > 0 && p < .5) return "under 1%";
        return String.format("%.0f%%", p);
    }

    /* ---------------------------------- homes ---------------------------------- */

    /**
     * The homes, in one verdict (0.7.27). The old page coloured SPARE HOMES
     * amber and the sentence under Homes red for the same doubled-up
     * households - two colours for one fact. The sentence's reading wins:
     * doubling up is the last resort, red; flatsharing is what people do
     * first, amber; short of doors, red.
     */
    record Homes(double built, double needed, double spare, double onSite, double shares, double doubled,
                 String tone, String word, String line, String whole) {
        /** SPARE HOMES' note: the old cell's words. */
        String cellNote() {
            return doubled >= .5 ? people(doubled) + " doubled up anyway"
                    : shares >= .5 ? people(shares) + " flatsharing"
                    : isShort() ? "households without a door" : "everyone has a front door";
        }

        /** Short of doors by a whole household or more: the verdict's red, and the gauge's short segment. */
        boolean isShort() { return spare <= -.5; }
    }

    Homes homes() {
        BuildingManager bm = ui.game.getBuildingManager();
        FamilyModel families = ui.game.getFamilies();
        double built = bm.getTotalHomes();
        double needed = families.homesNeeded();
        double spare = built - needed;
        double shares = families.getSharedHouseholds();
        double doubled = families.getDoubledUpHouseholds();
        String tone, word, line, whole;
        if (spare <= -.5) {
            tone = Palette.BAD;
            word = "short of doors";
        } else if (doubled >= .5) {
            tone = Palette.BAD;
            word = "doubled up";
        } else if (shares >= .5) {
            tone = Palette.WARN;
            word = "flatsharing";
        } else {
            tone = Palette.GOOD;
            word = "a door each";
        }
        if (shares < .5 && doubled < .5) {
            line = "Everyone who wants their own front door has one.";
            whole = line;
        } else if (doubled < .5) {
            line = String.format("%s households are flatshares: five single adults to a home.", people(shares));
            whole = String.format("%s households are flatshares — five single adults to a home, which is what "
                    + "people do first when housing is tight. They would rather live alone.", people(shares));
        } else {
            line = String.format("%s flatshares, and %s households doubled up two to a home.",
                    people(shares), people(doubled));
            whole = String.format("%s flatshares, and %s households are doubled up two to a home. Doubling up is "
                    + "the last resort — the city has run out of single adults to crowd and is now crowding "
                    + "families.", people(shares), people(doubled));
        }
        if (spare <= -.5) {
            whole += String.format(" The city is short of %s homes for the households that want one.",
                    people(-spare));
        }
        return new Homes(built, needed, spare, bm.getHomesUnderConstruction(), shares, doubled, tone, word,
                line, whole);
    }

    /* --------------------------------- the month --------------------------------- */

    /**
     * The month's flows, or the year's (yearView): where the headcount
     * started, the four flows with what made up the dead and the leavers,
     * and where it ended. The month reads the model's last month - saved
     * since 0.7.27, so it reads the same after a load - and its start is
     * the history's population a month back; the year sums the history's
     * last twelve months.
     */
    record Month(boolean year, boolean available, double start, double end, double born, double died,
                 double in, double out, List<Slice> deaths, List<Slice> leavers) {
        double net() { return born - died + in - out; }
    }

    Month month(boolean year) {
        HistorySave h = ui.game.getHistorySave();
        List<Integer> pop = h.getPopulation();
        int n = pop.size();
        if (year) {
            if (n < 13) return new Month(true, false, 0, 0, 0, 0, 0, 0, List.of(), List.of());
            double[] births = h.aligned("births"), deaths = h.aligned("deaths"),
                    arrivals = h.aligned("arrivals"), departures = h.aligned("departures");
            double b = 0, d = 0, i = 0, o = 0;
            for (int k = n - 12; k < n; k++) {
                b += finite(births[k]);
                d += finite(deaths[k]);
                i += finite(arrivals[k]);
                o += finite(departures[k]);
            }
            return new Month(true, true, pop.get(n - 13), pop.get(n - 1), b, d, i, o, List.of(), List.of());
        }
        PopulationCohorts cohorts = ui.game.getCohorts();
        Migration migration = ui.game.getMigration();
        Sickness sickness = ui.game.getSickness();
        /*
         * A SAVE FROM BEFORE 0.7.27, READ BEFORE ITS FIRST MONTH, carries no
         * migration month (drawStruck() is false): its arrivals and departures
         * are the history's last month instead, which the history always kept,
         * and whatever of the dead and the leavers the save did not split is
         * a grey part of its own rather than painted as one cause.
         */
        double in = migration.getLastArrivals(), out = migration.getLastDepartures();
        if (!drawStruck() && n >= 1) {
            in = finite(h.aligned("arrivals")[n - 1]);
            out = finite(h.aligned("departures")[n - 1]);
        }
        List<Slice> deaths = new ArrayList<>();
        deaths.add(new Slice("of age", cohorts.getLastDeathsOfAge(), DIED_OF_AGE));
        deaths.add(new Slice("of illness they did not get over", sickness.getLastDeaths(), DIED_OF_ILLNESS));
        deaths.add(new Slice("killed", cohorts.getLastKilled(), DIED_KILLED));
        deaths.add(new Slice("aged out at 120", cohorts.getLastAgedOut(), DIED_AGED_OUT));
        unsplit(deaths, cohorts.getLastDeaths());
        List<Slice> leavers = new ArrayList<>();
        leavers.add(new Slice("for want of work", migration.getLastWorkDepartures(), LEFT_WORK));
        leavers.add(new Slice("went broke", migration.getLastBankruptcyDepartures(), LEFT_BROKE));
        leavers.add(new Slice("driven out by crime", migration.getLastCrimeDepartures(), LEFT_CRIME));
        unsplit(leavers, out);
        double start = n >= 2 ? pop.get(n - 2) : Double.NaN;
        return new Month(false, true, start, ui.game.getPopulationManager().getPopulation(),
                cohorts.getLastBirths(), cohorts.getLastDeaths(), in, out, deaths, leavers);
    }

    /** Whatever of a total its parts do not account for, as a grey part of its own: an older save's month. */
    static void unsplit(List<Slice> parts, double total) {
        double sum = 0;
        for (Slice s : parts) sum += s.amount();
        if (total - sum >= .5) parts.add(new Slice("not split: a save from before 0.7.27", total - sum, Palette.TEXT_SPENT));
    }

    /**
     * Whether Migration struck (or the save carried) this month's draw: a
     * city with people draws somebody. False for a save from before 0.7.27
     * until its first month runs - its migration month was not kept.
     */
    boolean drawStruck() {
        return ui.game.getMigration().getLastTarget() > 0 || ui.game.getPopulationManager().getPopulation() <= 0;
    }

    private static double finite(double v) { return Double.isFinite(v) ? v : 0; }

    /*
     * THE MONTH'S COLOURS ARE CATEGORIES: the people teal for who came, its
     * light step for who was born, and the other areas' colours for the
     * causes - never green for the born (bug 13 in the design study), never
     * red for the dead.
     */
    /** The born and the arrived: the people teal's light step and the teal. */
    static final String BORN = Palette.PEOPLE_LIGHT, MOVED_IN = Palette.PEOPLE;

    /** The dead by cause: of age the dark teal, of illness the violet, the killed the pink, the aged out a light blue. */
    static final String DIED_OF_AGE = Palette.PEOPLE_DARK, DIED_OF_ILLNESS = Palette.BUSINESS,
            DIED_KILLED = Palette.BUILDING, DIED_AGED_OUT = Palette.MONEY_LIGHT;

    /** The leavers by why: for want of work the dark teal, broke the blue, driven out by crime the pink. */
    static final String LEFT_WORK = Palette.PEOPLE_DARK, LEFT_BROKE = Palette.MONEY, LEFT_CRIME = Palette.BUILDING;

    /** The month's events, as chips in the card's head: each a state of the model, nothing random. */
    List<String[]> monthEvents() {
        List<String[]> out = new ArrayList<>();
        Migration migration = ui.game.getMigration();
        if (migration.getLastCrimeDepartures() >= .5) {
            out.add(new String[] {"Crime drove out " + flowText(migration.getLastCrimeDepartures()), Palette.BAD});
        }
        if (migration.getLastBankruptcyDepartures() >= .5) {
            out.add(new String[] {flowText(migration.getLastBankruptcyDepartures()) + " went broke and left",
                    Palette.WARN});
        }
        double lost = ui.game.getUnemployment().getNewlyUnhoused();
        if (lost >= .5) out.add(new String[] {flowText(lost) + " lost their home", Palette.BAD});
        String milestone = milestone();
        if (milestone != null) out.add(new String[] {milestone, Palette.PEOPLE});
        return out;
    }

    /** "100,000 people in March 2031": the last of 1k, 10k, 100k and 1M the history's population crossed in the last twelve months, or null. */
    String milestone() {
        HistorySave h = ui.game.getHistorySave();
        List<Integer> pop = h.getPopulation();
        List<Integer> axis = h.getMonth();
        String found = null;
        for (int k = Math.max(1, pop.size() - 12); k < pop.size(); k++) {
            for (int mark : new int[] {1_000, 10_000, 100_000, 1_000_000}) {
                if (pop.get(k - 1) < mark && pop.get(k) >= mark) {
                    int month = k < axis.size() ? axis.get(k) : ui.game.getMonth();
                    found = formatter.format(mark) + " people in " + CityCalendar.format(month);
                }
            }
        }
        return found;
    }

    /* ------------------------------ why they come ------------------------------ */

    /**
     * The draw as Migration struck it: the jobs' half and the homes' half,
     * the three pulls, the target, and the city against it, with the old
     * page's why sentence - its first sentence on the card, the whole behind
     * the (i).
     */
    record Bridge(double jobs, double perJob, double jobDraw, double homesFor, double homeDraw,
                  double beforePulls, double senior, double rent, double crime, double draws,
                  double living, double crowding, String why, String whyWhole, String tone, String word) { }

    Bridge bridge() {
        Migration m = ui.game.getMigration();
        double target = m.getLastTarget();
        int population = ui.game.getPopulationManager().getPopulation();
        String why, tone, word;
        if (m.getLastCrowding() <= 0) {
            why = "Nobody else can fit. Every flatshare and every doubled-up household "
                + "the city can form has already formed — build homes and the queue "
                + "outside starts moving again.";
            tone = Palette.BAD;
            word = "full";
        } else if (m.getLastCrowding() < .95) {
            why = String.format("Housing is tight enough to turn people away: only %.0f%% "
                    + "of the people this city attracts can find somewhere to live. The "
                    + "jobs are still pulling.", m.getLastCrowding() * 100);
            tone = Palette.WARN;
            word = "tight";
        } else if (m.getLastDepartures() > 0) {
            why = String.format("People are leaving. %.0f%% of the city's payroll sits in "
                    + "trades that have been shrinking for a year or have stopped paying "
                    + "altogether.", m.getLastDecliningShare() * 100);
            tone = Palette.BAD;
            word = "leaving";
        } else if (target > population) {
            why = "Work is going begging and there is room to house whoever takes it. "
                + "People are still arriving.";
            tone = Palette.GOOD;
            word = "growing";
        } else {
            why = "The city is about the size its jobs and housing support. Nobody is "
                + "leaving — a shortage of work is unemployment, not an exodus, until a "
                + "whole trade has been dying for a year.";
            tone = Palette.TEXT_MUTED;
            word = "settled";
        }
        return new Bridge(m.getLastJobs(), m.getLastResidentsPerJob(), m.getLastJobDraw(), m.getLastHomeCapacity(),
                m.getLastHomeDraw(), m.getLastDrawBeforePulls(), m.getLastSeniorPull(),
                m.getLastAffordabilityPull(), m.getLastCrimePull(), target, population, m.getLastCrowding(),
                firstSentence(why), why, tone, word);
    }

    /** A paragraph's first sentence: what a card shows, the rest behind its (i). */
    static String firstSentence(String text) {
        int at = text.indexOf(". ");
        int colon = text.indexOf(": ");
        if (colon > 0 && (at < 0 || colon < at)) return text.substring(0, colon) + ".";
        return at < 0 ? text : text.substring(0, at + 1);
    }

    /** "1,158 room to grow", or "more than it draws by 5,989". */
    static String roomWords(double draws, double living) {
        return living > draws ? "more than it draws by " + people(living - draws)
                : people(draws - living) + " room to grow";
    }

    /** A multiplier: "×1.01". */
    static String times(double x) { return String.format("×%.2f", x); }

    /* ---------------------------------- care ---------------------------------- */

    /** A care ring's line: what the city has against who needs it. */
    String careLine(BuildAdvice.Measure m) {
        BuildingManager bm = ui.game.getBuildingManager();
        PopulationCohorts cohorts = ui.game.getCohorts();
        Healthcare service = ui.game.getHealthcare();
        double[] fill = ui.game.getPopulationManager().getJobFillRate();
        if (m.kind() == BuildAdvice.Kind.CARE) {
            CareType type = m.care();
            double beds = bm.getStaffedCareCapacity(type, fill);
            if (type == CareType.GENERAL) {
                return people(beds) + " staffed beds for " + people(cohorts.total());
            }
            return people(beds) + " places for " + people(type.populationServed(cohorts));
        }
        double plots = bm.getCareCapacity(CareType.BURIAL);
        double plotsLeft = Healthcare.plotsRemaining(plots, service.getPlotsUsed());
        return people(plotsLeft) + " plots free · " + String.format("%.0f%% full", service.getPlotUtilisation() * 100);
    }

    /** A care ring's (i): the old page's paragraphs for it, and where Services shows it in full. */
    String careInfo(BuildAdvice.Measure m) {
        Health health = ui.game.getHealth();
        Healthcare service = ui.game.getHealthcare();
        Sickness sickness = ui.game.getSickness();
        PopulationCohorts cohorts = ui.game.getCohorts();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] fill = ui.game.getPopulationManager().getJobFillRate();
        if (m.kind() == BuildAdvice.Kind.CARE && m.care() == CareType.GENERAL) {
            double sick = health.getSickRate();
            String illness;
            if (health.isOutbreak()) {
                illness = String.format("An outbreak that began in %s is adding %.1f points on "
                        + "top of the usual %.1f%%. It fades on its own over a few months, and "
                        + "hospitals make it milder rather than shorter.",
                        CityCalendar.format(health.getOutbreakStarted()),
                        health.getOutbreakSeverity() * 100, health.getBaselineRate() * 100);
            } else if (health.getCoverage() <= 0) {
                illness = String.format("There is no general care in this city at all, so %.0f%% "
                        + "of every month's work simply does not happen. A walk-in clinic is the "
                        + "cheapest thing on the healthcare list.", sick * 100);
            } else if (health.getCoverage() < 1) {
                illness = String.format("Clinics and hospitals reach %.0f%% of the city. Covering "
                        + "the rest would take the absence rate down toward %.0f%%.",
                        health.getCoverage() * 100, Health.WELL_SERVED_RATE * 100);
            } else {
                illness = "Everyone can get seen. This is as healthy as a workforce gets — "
                    + "people still fall ill, and an outbreak can still arrive.";
            }
            return String.format("General care covers %.0f%% - the month's figure, the ring's beds less anybody "
                    + "the fee turned away. %s staffed beds for %s people: the figure is the people treated over the "
                    + "people, not the beds%s\n\nIt also cures %.0f%% of the sick a month. %s have been ill for "
                    + "more than two months, and %s died of illness last month.\n\n%s\n\nIn full on Services › "
                    + "Health › General care.",
                    health.getCoverage() * 100,
                    people(bm.getStaffedCareCapacity(CareType.GENERAL, fill)), people(cohorts.total()),
                    service.getPricedOut(CareType.GENERAL) > 0
                            ? String.format(" - %s had a bed and were priced out of it this month: a household "
                                    + "that cannot pay after its savings, its shares and its credit goes "
                                    + "without care rather than without food.",
                                    people(service.getPricedOut(CareType.GENERAL)))
                            : ".",
                    Sickness.recovery(health.getCoverage()) * 100,
                    people(sickness.peoplePastTwoMonths(cohorts)), flowText(sickness.getLastDeaths()), illness);
        }
        if (m.kind() == BuildAdvice.Kind.CARE) {
            CareType type = m.care();
            double cover = service.getCoverage(type);
            double pricedOut = service.getPricedOut(type);
            String effects = type == CareType.CHILDCARE
                    ? String.format("Infant deaths ×%.3f, illness ×%.2f, births ×%.2f.",
                            Healthcare.mortalityFactor(AgeBand.BABY, cover, .5, 0),
                            1 + Sickness.EXTRA_SICKNESS * (1 - cover), Healthcare.birthFactor(cover))
                    : String.format("Senior deaths ×%.2f, illness ×%.2f, +%.0f%% on the city's draw.",
                            Healthcare.mortalityFactor(AgeBand.SENIOR, 0, .5, cover),
                            1 + Sickness.EXTRA_SICKNESS * (1 - cover),
                            (Migration.seniorCarePull(cover) - 1) * 100);
            return String.format("%s covers %.0f%% - the month's figure, the ring's places less anybody the "
                    + "fee turned away: %s places for %s%s. At this cover: %s\n\nIn full on "
                    + "Services › Health › %s.",
                    type.getLabel(), cover * 100,
                    people(bm.getStaffedCareCapacity(type, fill)), people(type.populationServed(cohorts)),
                    pricedOut > 0 ? String.format(", %s priced out this month", people(pricedOut)) : "",
                    effects, type.getLabel());
        }
        double plots = bm.getCareCapacity(CareType.BURIAL);
        double ovens = bm.getStaffedCareCapacity(CareType.CREMATION, fill);
        double plotsLeft = Healthcare.plotsRemaining(plots, service.getPlotsUsed());
        double monthsLeft = service.monthsOfPlotsLeft(plots);
        return String.format("Funerals dealt with: %.1f%% (%s). Deaths: %s a month - %s buried, %s cremated.\n\n"
                + "Ground left: %s of %s plots, %.0f%% full%s. Crematoria: %s a month, %.0f%% used.\n\n"
                + "A cemetery is a stock, not a rate: it is fine until the month it is full. In full on "
                + "Services › Health › Death care.",
                service.getDeathCareRatio() * 100, service.getStatus(),
                flowText(service.getDeaths()), flowText(service.getBurials()), flowText(service.getCremations()),
                people(plotsLeft), people(plots), service.getPlotUtilisation() * 100,
                plots > 0 && monthsLeft < 600 ? String.format(", about %,.0f months left at this rate", monthsLeft) : "",
                people(ovens), service.getCremationUtilisation() * 100);
    }

    /* --------------------------- outside the families --------------------------- */

    /** One of the five tiles: its name, icon, count, its split, the history series its sparkline draws, and its door. */
    record Tile(String name, String icon, double count, String split, String tone, String series, String where) { }

    List<Tile> outsideTiles() {
        Unemployment u = ui.game.getUnemployment();
        FamilyModel families = ui.game.getFamilies();
        Crime crime = ui.game.getCrime();
        double[] noDoor = families.unhousedPeopleByBand();
        noDoor[AgeBand.ADULT.ordinal()] += u.getUnhoused();
        double unhoused = 0;
        for (double v : noDoor) unhoused += v;
        double orphans = families.getOrphansTotal();
        List<Tile> out = new ArrayList<>();
        out.add(new Tile("Out of work", Icons.STAFF, u.getPool(),
                people(u.onEi()) + " on EI · " + people(u.getOffEi()) + " run out"
                        + (u.getUnhoused() >= .5 ? " · " + people(u.getUnhoused()) + " lost their home" : ""),
                Palette.TEXT_HEAD, "outOfWork", "Their books: Household money"));
        out.add(new Tile("Students", Icons.EDUCATION, families.getSeekers(FamilyModel.Seeker.STUDENT),
                "full-time, on a grant and a loan", Palette.TEXT_HEAD, "students", "Services › Education"));
        out.add(new Tile("No home", Icons.HOMES, unhoused, byBand(noDoor),
                unhoused >= .5 ? Palette.BAD : Palette.TEXT_HEAD, "unhoused", "Their books: Household money"));
        double[] orphanBands = new double[AgeBand.values().length];
        for (AgeBand b : AgeBand.values()) orphanBands[b.ordinal()] = families.getOrphans(b);
        out.add(new Tile("Orphans", Icons.CHILD, orphans, byBand(orphanBands),
                orphans >= .5 ? Palette.BAD : Palette.TEXT_HEAD, "orphans", "Their books: Household money"));
        out.add(new Tile("In prison", Icons.SAFETY, crime.prisoners(),
                crime.getNotHeld() >= .5 ? people(crime.getNotHeld()) + " caught, not held" : "a cell for everyone caught",
                Palette.TEXT_HEAD, "prisoners", "Services › Safety › Prisons"));
        return out;
    }

    /** "21 babies · 1 child · 27 teens": who, by age, where there is anybody; "nobody" where there is not. */
    static String byBand(double[] perBand) {
        StringBuilder s = new StringBuilder();
        for (AgeBand b : AgeBand.values()) {
            double n = perBand[b.ordinal()];
            if (n < .5) continue;
            if (s.length() > 0) s.append(" · ");
            s.append(people(n)).append(' ').append(Math.round(n) == 1 ? oneOf(b) : b.getLabel().toLowerCase());
        }
        return s.length() == 0 ? "nobody" : s.toString();
    }

    /** One of a band: "baby", "child", "teen". */
    static String oneOf(AgeBand b) {
        switch (b) {
            case BABY:  return "baby";
            case CHILD: return "child";
            case TEEN:  return "teen";
            case ADULT: return "adult";
            case SENIOR: return "senior";
            default:    return "elder";
        }
    }

    /** The pool's month: in, out, and the EI line under it. */
    record Pool(List<Slice> in, List<Slice> out, double droppedOffEi, double paid, double premiums,
                double perClaimant, double premiumRate, String cover, String coverWhole) {
        double inTotal()  { double s = 0; for (Slice p : in) s += p.amount(); return s; }
        double outTotal() { double s = 0; for (Slice p : out) s += p.amount(); return s; }
    }

    Pool pool() {
        Unemployment u = ui.game.getUnemployment();
        EconomyManager em = ui.game.getEconomyManager();
        List<Slice> in = List.of(
                new Slice("jobs that disappeared", u.getJobsLost(), Palette.BUILDING),
                new Slice("arrived and found no work", u.getArrivalsUnhired(), Palette.PEOPLE),
                new Slice("came of age, or finished school, with no post", u.getEntrants(), Palette.PEOPLE_LIGHT));
        List<Slice> out = List.of(
                new Slice("hired out of the pool", u.getLocalHires(), Palette.PEOPLE_DARK),
                new Slice("gave up and left the city", u.getLeftWhenBroke(), Palette.BUSINESS),
                new Slice("lost their home", u.getNewlyUnhoused(), Palette.BUILDING_DARK));
        // What the treasury PAID this month (0.7.3): the bill is paid at the top of the month on the
        // pool it opened with; the pool's own figure is next month's bill. See EconomyManager.
        double paid = em.getEiBenefits();
        double premiums = em.getEiPremiums();
        String cover, whole;
        if (paid <= 0) {
            cover = "Nobody is drawing EI, so the premiums are all revenue.";
            whole = cover;
        } else if (premiums >= paid) {
            cover = "The premiums cover what EI pays out; the rest is revenue.";
            whole = cover;
        } else {
            cover = String.format("The premiums cover %.0f%% of what EI paid.", premiums / paid * 100);
            whole = String.format("The premiums cover %.0f%% of it. The rest is general revenue - a bust costs "
                    + "the treasury twice, in EI and in the premiums on the wages that went.", premiums / paid * 100);
        }
        return new Pool(in, out, u.getDroppedOffEi(), paid, premiums,
                u.onEi() >= .5 ? u.getBenefitPerClaimant() : 0, em.getTaxPolicy().getEiPremiumRate(), cover, whole);
    }

    /* ---------------------------------- work ---------------------------------- */

    /** One rung of the skill ladder: the posts this band can be put into, everyone queueing for them, the chance, the pay and how far it reaches. */
    record Rung(WageBand band, double open, double queue, double chance, double premium, double reach) { }

    List<Rung> ladder() {
        PopulationManager pm = ui.game.getPopulationManager();
        LabourMarket market = ui.game.getLabourMarket();
        double[] open = pm.staffablePostsByBand();
        double[] queue = pm.supplyByBand();
        List<Rung> out = new ArrayList<>();
        for (WageBand band : WageBand.values()) {
            int b = band.ordinal();
            // CHANCE is the number that decides who moves here: open posts against everybody queueing.
            double chance = queue[b] > 0 ? Math.min(1, open[b] / queue[b]) : 1;
            double premium = market.bandPremium(band);
            double reach = band == WageBand.NONE || band == WageBand.DIPLOMA ? 0 : Migration.reach(premium);
            out.add(new Rung(band, open[b], queue[b], chance, premium, reach));
        }
        return out;
    }

    /** The pay chip's verdict: red over 1.05x, the city paying over the odds to staff a band (as the ladder's rows were). */
    static String payTone(double premium) {
        return premium > 1.05 ? Palette.BAD : Palette.TEXT_LABEL;
    }

    /** The labour line: surplus, shortage or balance, in the old page's words. */
    String[] labourWords() {
        PopulationManager pm = ui.game.getPopulationManager();
        int workforce = pm.getWorkforce();
        int totalJobs = pm.getTotalJobs();
        int unfilled = 0;
        for (int v : pm.getJobVacancy()) unfilled += v;
        if (workforce > totalJobs) {
            return new String[] {
                String.format("Labour surplus: %s adults with nowhere to work.", people(workforce - totalJobs)),
                String.format("Labour surplus: %s adults with nowhere to work. That is unemployment, not an "
                        + "exodus — people do not leave over it until a whole trade has been dying for a year.",
                        people(workforce - totalJobs)),
                Palette.WARN };
        }
        if (unfilled > 0) {
            String s = String.format("Labour shortage: %s positions across the city stand empty, and every one of "
                    + "them is pulling people toward the city.", people(unfilled));
            return new String[] { String.format("Labour shortage: %s positions stand empty.", people(unfilled)), s,
                    Palette.GOOD };
        }
        String s = "Every position is filled and every worker has one.";
        return new String[] { s, s, Palette.TEXT_MUTED };
    }

    /** The pay chips' (i): what paying over the going rate is pulling, in the old page's words. */
    String pulledWords() {
        LabourMarket market = ui.game.getLabourMarket();
        StringBuilder pulled = new StringBuilder();
        for (WageBand wb : WageBand.values()) {
            if (wb == WageBand.NONE || wb == WageBand.DIPLOMA) continue;
            double reach = Migration.reach(market.bandPremium(wb));
            if (reach <= 0) continue;
            if (pulled.length() > 0) pulled.append(", ");
            pulled.append(String.format("%s at %.0f%% of what the world can spare",
                    wb.label().toLowerCase(), reach * 100));
        }
        return pulled.length() > 0
                ? "Paying over the going rate is pulling " + pulled + ". At the going rate "
                  + "the pull is nothing at all, which is why a school is the other answer."
                : "Nobody above a diploma is being drawn here: every band above one is paid the going "
                  + "rate or less, and the going rate is what that trade costs everywhere. "
                  + "Bid a band up or build the school that makes your own.";
    }

    /* =====================================================================
       THE DRAWING
       ===================================================================== */

    /** A card on the Build card ground: RAISED, radius 8, a 1 px EDGE. */
    static VBox card(double gap) {
        VBox c = new VBox(gap);
        c.setMaxWidth(Double.MAX_VALUE);
        c.setStyle(CARD);
        return c;
    }

    /** The Build card ground every card on both pages stands on. */
    static final String CARD = "-fx-padding: 10 12 10 12; -fx-background-color: " + Palette.RAISED + ";"
            + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + Palette.EDGE + ";";

    /** A card's own small heading, in the section heads' grey, and whatever sits at its right. */
    static HBox cardHead(String title, String info, javafx.scene.Node... right) {
        Label t = new Label(title);
        t.setStyle(Palette.strong(Palette.SIZE_LABEL + 1, Palette.TEXT_LABEL));
        t.setMinWidth(Region.USE_PREF_SIZE);
        HBox row = new HBox(Palette.GAP_TIGHT, t);
        if (info != null) row.getChildren().add(infoButton(info, false));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        row.getChildren().add(gap);
        for (javafx.scene.Node n : right) if (n != null) row.getChildren().add(n);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** Words that wrap, at a size, in a colour. */
    static Label words(String text, int size, String colour) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setMinWidth(0);
        l.setStyle(Palette.words(size, colour));
        return l;
    }

    /** A figure, in the mono face, at a size, in a colour. */
    static Label figure(String text, int size, String colour) {
        Label l = new Label(text);
        l.setStyle(Palette.figure(size, colour));
        l.setMinWidth(Region.USE_PREF_SIZE);
        return l;
    }

    /**
     * "details ▸": a fold the player opens, remembered by `name` while the
     * game runs - every table on the page is behind one.
     */
    VBox details(String name, String caption, javafx.scene.Node... inside) {
        boolean open = detailsOpen.contains(name);
        Label toggle = new Label((open ? "details ▾  " : "details ▸  ") + caption);
        toggle.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.ACCENT) + " -fx-cursor: hand;");
        toggle.setWrapText(true);
        VBox box = new VBox(4, toggle);
        if (open) for (javafx.scene.Node n : inside) if (n != null) box.getChildren().add(n);
        toggle.setOnMouseClicked(e -> {
            if (!detailsOpen.remove(name)) detailsOpen.add(name);
            ui.redraw();
        });
        return box;
    }

    /** The page brought to a card: the menu's scroller set so it sits near the top. */
    void scrollTo(javafx.scene.Node target) {
        javafx.scene.control.ScrollPane scroller = ui.menuScroller;
        if (scroller == null || target == null || target.getScene() == null) return;
        javafx.scene.Node content = scroller.getContent();
        if (content == null) return;
        javafx.geometry.Bounds at = content.sceneToLocal(target.localToScene(target.getBoundsInLocal()));
        double span = content.getBoundsInLocal().getHeight() - scroller.getViewportBounds().getHeight();
        if (at == null || span <= 0) return;
        scroller.setVvalue(Math.max(0, Math.min(1, (at.getMinY() - 12) / span)));
    }

    /** Services on one of its pages: an area and a page of it, as its strips name them. */
    void toServices(String area, String page) {
        ui.servicesScreen.serviceArea = area;
        ui.servicesScreen.servicePage = page;
        ui.servicesScreen.openPage();
    }

    /** Build's Healthcare page with a ring picked, as NEEDS YOU's doors open it. */
    void toHealthcare(BuildAdvice.Measure m) {
        BuildAdvice.Category c = BuildAdvice.category(BuildAdvice.HEALTHCARE);
        if (c == null) {
            ui.buildScreen.showBuildMenu();
            return;
        }
        ui.buildScreen.measurePicked.put(c.name(), m);
        ui.buildScreen.showCityCategory(c);
    }

    /** Household money with a cell open: its key as openCell keeps it. */
    void toBooks(String cellKey) {
        openCell = cellKey == null ? "" : cellKey;
        revealOpened = !openCell.isEmpty();
        showHouseholdMenu();
    }

    /* ------------------------------- the vitals ------------------------------- */

    /** The five cells, each a door: the waterfall, the Work card, the Homes card, Services, Household money. */
    VBox vitals() {
        Cell[] cells = peopleCells();
        Runnable[] doors = {
            () -> scrollTo(monthCard),
            () -> scrollTo(workCard),
            () -> scrollTo(homesCard),
            () -> toServices("Health", "General care"),
            this::showHouseholdMenu,
        };
        VBox[] drawn = new VBox[cells.length];
        for (int i = 0; i < cells.length; i++) {
            Cell c = cells[i];
            drawn[i] = limitCell(c.label(), c.value(), c.note(), c.tone(), c.where(), doors[i]);
        }
        HBox bar = vitalsBar(drawn);
        VBox holder = new VBox(bar);
        holder.setAlignment(Pos.CENTER);
        return holder;
    }

    /**
     * LIVING HERE counts from last month's headcount to this one's (the
     * history's population a month back to the model's now) over
     * COUNT_MILLIS - only on a month that landed while the page was watched.
     */
    void countUp(VBox vitals) {
        if (vitals.getChildren().isEmpty() || !(vitals.getChildren().get(0) instanceof HBox bar)) return;
        if (bar.getChildren().isEmpty() || !(bar.getChildren().get(0) instanceof VBox cell)) return;
        if (cell.getChildren().size() < 2 || !(cell.getChildren().get(1) instanceof Label figure)) return;
        List<Integer> pop = ui.game.getHistorySave().getPopulation();
        if (pop.size() < 2) return;
        double from = pop.get(pop.size() - 2);
        double to = ui.game.getPopulationManager().getPopulation();
        if (from == to) return;
        javafx.beans.property.DoubleProperty n = new javafx.beans.property.SimpleDoubleProperty(from);
        n.addListener((o, was, now) -> figure.setText(formatter.format(Math.round(now.doubleValue()))));
        figure.setText(formatter.format(Math.round(from)));
        new javafx.animation.Timeline(new javafx.animation.KeyFrame(Duration.millis(COUNT_MILLIS),
                new javafx.animation.KeyValue(n, to, javafx.animation.Interpolator.EASE_OUT))).play();
    }

    /* ------------------------------- the pyramid ------------------------------- */

    /** How wide half of the widest band is drawn, at most. */
    static final double PYRAMID_HALF = 200;

    /**
     * A band's colour: who it is, not how it is doing - the dependants the
     * light teal, the working age the teal, the retired the dark (0.7.27;
     * the working bands were GOOD green before, a verdict on nothing).
     */
    static String bandColour(AgeBand b) {
        if (b.isWorkingAge()) return Palette.PEOPLE;
        return b == AgeBand.SENIOR || b == AgeBand.ELDER ? Palette.PEOPLE_DARK : Palette.PEOPLE_LIGHT;
    }

    /** The ages a band holds, as its caption: "18–69", "85–120". */
    static String ages(AgeBand b) {
        return b.getFromAge() + "–" + (b.next() == null ? 120 : b.getToAge() - 1);
    }

    /**
     * WHO LIVES HERE: the six bands mirrored about an axis, widest at the
     * top's scale, each with the shape a settled city would have as a 1 px
     * outline behind it (PopulationCohorts.equilibriumShare()) - the city's
     * 68% adults against a settled 60% is a fact you see.
     */
    VBox pyramidCard() {
        PopulationCohorts cohorts = ui.game.getCohorts();
        Sickness sickness = ui.game.getSickness();
        double total = cohorts.total();
        double widest = 0;
        for (AgeBand b : AgeBand.values()) {
            widest = Math.max(widest, Math.max(cohorts.share(b), PopulationCohorts.equilibriumShare(b)));
        }
        VBox rows = new VBox(0);
        for (int i = AgeBand.values().length - 1; i >= 0; i--) {
            AgeBand b = AgeBand.values()[i];
            Label name = figure(b.getLabel(), Palette.SIZE_BODY, Palette.TEXT_BODY);
            name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
            Label age = words(ages(b), Palette.SIZE_CAPTION, Palette.TEXT_MUTED);
            VBox who = new VBox(0, name, age);
            who.setMinWidth(96);
            who.setPrefWidth(96);
            who.setAlignment(Pos.CENTER_LEFT);

            Mirror bar = new Mirror(cohorts.share(b), PopulationCohorts.equilibriumShare(b), widest,
                    bandColour(b));
            HBox.setHgrow(bar, Priority.ALWAYS);

            Label count = figure(people(cohorts.get(b)), Palette.SIZE_BODY, Palette.TEXT_HEAD);
            Label share = figure(String.format("%.1f%%", cohorts.share(b) * 100), Palette.SIZE_CAPTION,
                    Palette.TEXT_MUTED);
            VBox figures = new VBox(0, count, share);
            figures.setAlignment(Pos.CENTER_RIGHT);
            figures.setMinWidth(86);
            figures.setPrefWidth(86);

            HBox row = new HBox(8, who, bar, figures);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMinHeight(34);
            row.setPrefHeight(34);
            Tooltip tip = new Tooltip(String.format(
                    "%s, %s: %s people, %.1f%% of the city%nA settled city: %.1f%%%n%.0f%% of them off sick"
                    + "%nDied of illness this month: %s",
                    b.getLabel(), ages(b), people(cohorts.get(b)), cohorts.share(b) * 100,
                    PopulationCohorts.equilibriumShare(b) * 100, sickness.share(b) * 100,
                    flowText(sickness.getLastDeaths(b))));
            tip.setShowDelay(Duration.millis(250));
            Tooltip.install(row, tip);
            rows.getChildren().add(row);
        }

        HBox key = new HBox(Palette.GAP_LOOSE, keySwatch(Palette.PEOPLE_LIGHT, "children"),
                keySwatch(Palette.PEOPLE, "working age"), keySwatch(Palette.PEOPLE_DARK, "retired"),
                LandScreen.ghostSwatch(Palette.TEXT_MUTED, "a settled city"));
        key.setAlignment(Pos.CENTER_LEFT);

        HBox foot = infoLine(String.format("Each 100 working adults carry %.0f", cohorts.dependencyRatio()),
                "Children and pensioners, who do not work and still eat. Ages 0-120; adults are "
                        + AgeBand.ADULT.getFromAge() + "-" + (AgeBand.ADULT.getToAge() - 1)
                        + " and are the whole workforce.",
                false, Palette.SIZE_LABEL, Palette.TEXT_LABEL, 400);

        VBox c = card(6);
        c.getChildren().addAll(
                cardHead("WHO LIVES HERE", null, words(String.format("%s of working age (%.0f%%)",
                        people(cohorts.workingAge()), total > 0 ? cohorts.workingAge() / total * 100 : 0),
                        Palette.SIZE_LABEL, Palette.TEXT_MUTED)),
                rows, key, foot);
        return c;
    }

    /** One band of the pyramid: a bar centred on the axis, `share` of the widest's scale, and the settled share's outline. */
    static final class Mirror extends javafx.scene.layout.Pane {
        private final double share, settled, widest;
        private final Region bar = new Region(), ghost = new Region(), axis = new Region();

        Mirror(double share, double settled, double widest, String colour) {
            this.share = share;
            this.settled = settled;
            this.widest = widest;
            bar.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 2;");
            ghost.setStyle("-fx-border-color: " + Palette.TEXT_MUTED + "; -fx-border-width: 1;"
                    + " -fx-border-radius: 2; -fx-background-color: transparent;");
            ghost.setMouseTransparent(true);
            axis.setStyle("-fx-background-color: " + Palette.EDGE + ";");
            getChildren().addAll(axis, bar, ghost);
            setMinWidth(60);
            setPrefWidth(2 * PYRAMID_HALF);
            setMinHeight(34);
            setPrefHeight(34);
        }

        @Override protected void layoutChildren() {
            double w = getWidth(), h = getHeight();
            double half = Math.min(PYRAMID_HALF, w / 2 - 2);
            double mid = w / 2;
            double bh = 20, top = (h - bh) / 2;
            double bw = widest > 0 ? Math.max(2, share / widest * half) : 2;
            double gw = widest > 0 ? settled / widest * half : 0;
            axis.resizeRelocate(Math.floor(mid), 0, 1, h);
            bar.resizeRelocate(mid - bw, top, 2 * bw, bh);
            ghost.setVisible(gw > 0);
            ghost.resizeRelocate(mid - gw, top - 3, 2 * gw, bh + 6);
        }
    }

    /* -------------------------------- the month -------------------------------- */

    /** The people a waterfall column moves, signed: "+142", "−293", "under 1". */
    static String signedPeople(double v) {
        return flowSigned(v >= 0 ? "+" : "−", Math.abs(v));
    }

    /**
     * THIS MONTH: the headcount at the start and the end over the columns,
     * and the waterfall - born, died (stacked by cause), moved in, moved out
     * (stacked by why), and the net. A click on Moved in or Moved out opens
     * who they were by skill. The toggle shows the year instead.
     */
    VBox monthCard(Pieces.Waterfall[] grow) {
        Month m = month(yearView);
        javafx.scene.layout.FlowPane toggle = chipStrip(new String[] {"month", "year"}, yearView ? "year" : "month",
                Palette.SIZE_CAPTION, name -> {
                    yearView = "year".equals(name);
                    ui.redraw();
                });
        toggle.setAlignment(Pos.CENTER_RIGHT);
        toggle.setPrefWrapLength(160);
        toggle.setMinWidth(Region.USE_PREF_SIZE);
        VBox c = card(6);
        c.getChildren().add(cardHead(yearView ? "THE LAST TWELVE MONTHS" : "THIS MONTH", null, toggle));
        if (!m.available()) {
            c.getChildren().add(words("Not a year of history yet: the year shows once the city has lived thirteen "
                    + "months.", Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            return c;
        }
        HBox ends = new HBox(Palette.GAP, figure(Double.isFinite(m.start()) ? people(m.start()) : "—",
                Palette.SIZE_SECTION, Palette.TEXT_LABEL),
                words("→", Palette.SIZE_SECTION, Palette.TEXT_MUTED),
                figure(people(m.end()), Palette.SIZE_SECTION, Palette.TEXT_HEAD),
                words(yearView ? "living here a year ago, and now" : "living here a month ago, and now",
                        Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        ends.setAlignment(Pos.BASELINE_LEFT);
        c.getChildren().add(ends);

        List<String[]> events = yearView ? List.of() : monthEvents();
        if (!events.isEmpty()) {
            javafx.scene.layout.FlowPane chips = new javafx.scene.layout.FlowPane(Palette.GAP_TIGHT, Palette.GAP_TIGHT);
            for (String[] ev : events) chips.getChildren().add(chip(ev[0], ev[1]));
            c.getChildren().add(chips);
        }

        StringBuilder byAge = new StringBuilder();
        Sickness sickness = ui.game.getSickness();
        for (AgeBand b : AgeBand.values()) {
            if (byAge.length() > 0) byAge.append(" · ");
            byAge.append(b.getLabel().toLowerCase()).append(' ').append(flowText(sickness.getLastDeaths(b)));
        }
        List<Pieces.Step> steps = new ArrayList<>();
        steps.add(Pieces.Step.of("Born", m.born(), BORN).icon(Icons.BIRTH));
        Pieces.Step died = Pieces.Step.of("Died", -m.died(), DIED_OF_AGE).icon(Icons.DEATH);
        if (!yearView) {
            died = died.parts(negated(m.deaths()))
                    .tip("Died of illness, by age: " + byAge);
        }
        steps.add(died);
        Pieces.Step in = Pieces.Step.of("Moved in", m.in(), MOVED_IN).icon(Icons.ARRIVE);
        Pieces.Step out = Pieces.Step.of("Moved out", -m.out(), LEFT_WORK).icon(Icons.LEAVE);
        if (!yearView) {
            in = in.tip("Moved in: " + flowText(m.in()) + "\nClick for who they were.").go(this::arrivalsPopover);
            out = out.parts(negated(m.leavers())).tip("Click for who they were.").go(this::departuresPopover);
        }
        steps.add(in);
        steps.add(out);
        steps.add(Pieces.Step.total("Net", m.net(), Palette.PEOPLE));
        Pieces.Waterfall fall = waterfall(steps, PeopleScreen::signedPeople, 0, 190);
        grow[0] = fall;
        c.getChildren().add(fall);

        if (!yearView) {
            javafx.scene.layout.FlowPane key = new javafx.scene.layout.FlowPane(Palette.GAP_LOOSE, 4);
            for (Slice s : m.deaths()) if (s.amount() >= .5) key.getChildren().add(keySwatch(s.colour(), "died " + s.name()));
            for (Slice s : m.leavers()) if (s.amount() >= .5) key.getChildren().add(keySwatch(s.colour(), "left: " + s.name()));
            if (!key.getChildren().isEmpty()) c.getChildren().add(key);
        } else {
            c.getChildren().add(words("Twelve months of the history's births, deaths and moves, in whole people "
                    + "each month.", Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        }
        return c;
    }

    /**
     * What the bridge and the month's popovers say before the month's draw
     * has been struck: a city that has not run its first month, or one saved
     * before 0.7.27 kept the month, read before its first month here.
     */
    String notStruck() {
        return ui.game.getHistorySave().months() <= 1
                ? "Nothing yet: the draw, and who came for it, are struck when the city's first month runs."
                : "Not recorded yet: this city was saved before 0.7.27 kept the month's draw and who came and "
                  + "went by skill. The month's counts are the history's. Play a month and it is all here.";
    }

    /** The same parts taken away: a waterfall's step down is negative, and so are its parts. */
    static List<Slice> negated(List<Slice> parts) {
        List<Slice> out = new ArrayList<>();
        for (Slice s : parts) if (s.amount() > 0) out.add(new Slice(s.name(), -s.amount(), s.colour()));
        return out;
    }

    /** A skill mix as a bar, in whole people, with its table under it. */
    VBox skillBar(double[] mix) {
        List<Slice> parts = new ArrayList<>();
        String[] colours = { Palette.PEOPLE_LIGHT, Palette.PEOPLE, Palette.PEOPLE_DARK, Palette.MONEY };
        double total = 0;
        for (WageBand band : WageBand.values()) {
            double n = mix[band.ordinal()];
            total += n;
            if (n > 0) parts.add(new Slice(band.label(), n, colours[band.ordinal() % colours.length]));
        }
        VBox box = new VBox(4);
        if (!parts.isEmpty()) box.getChildren().add(keyedBar(parts, Pieces.POPOVER_WIDTH, Money::people));
        box.getChildren().add(mixTable(mix, total));
        return box;
    }

    /** Moved in's popover: who arrived by skill, which of them hold a licence, and why. */
    void arrivalsPopover(javafx.scene.Node anchor) {
        if (!drawStruck()) {
            openPopover(anchor, notStruck(), false);
            return;
        }
        Migration migration = ui.game.getMigration();
        LabourMarket market = ui.game.getLabourMarket();
        double[] inMix = migration.getLastArrivalMix();
        double[] inLic = migration.getLastArrivalLicences();
        VBox body = new VBox(6);
        body.getChildren().add(words("Moved in: " + flowText(migration.getLastArrivals()) + ", by the skill they hold",
                Palette.SIZE_BODY, Palette.TEXT_HEAD));
        body.getChildren().add(skillBar(inMix));
        boolean anyLicence = false;
        StringBuilder licensed = new StringBuilder();
        for (EducationType type : EducationType.values()) {
            if (!type.isProfessional()) continue;
            JobType job = type.licenses();
            double n = inLic[job.ordinal()];
            if (n <= 0) continue;
            anyLicence = true;
            if (licensed.length() > 0) licensed.append(" · ");
            // In whole people (0.7.27): it printed "0.02 at 1.01x".
            licensed.append(flowText(n)).append(' ').append(ui.buildScreen.jobPlural(job))
                    .append(String.format(" at %.2f× over their band", market.licencePremium(job)));
        }
        if (anyLicence) {
            body.getChildren().add(words("Already licensed: " + licensed, Palette.SIZE_LABEL, Palette.ACCENT));
        }
        body.getChildren().add(words(anyLicence
                        || inMix[WageBand.COLLEGE.ordinal()] > 0
                        || inMix[WageBand.UNIVERSITY.ordinal()] > 0
                        || inMix[WageBand.NONE.ordinal()] > 0
                ? "A diploma is what an ordinary arrival has. Anything else is bought: "
                  + "those rows are here because the city is paying over the going rate "
                  + "for them — labourers without a diploma for a dear unskilled wage, "
                  + "graduates for a dear graduate one."
                : "Nobody arrives without a diploma, and nobody above one either — at the "
                  + "going rate nobody has a reason to prefer this city. Bid a band's "
                  + "wage up, or build the school.", Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        body.setMaxWidth(Pieces.POPOVER_WIDTH + 40);
        openPopover(anchor, body);
    }

    /** Moved out's popover: who left by skill, and why the educated go first. */
    void departuresPopover(javafx.scene.Node anchor) {
        if (!drawStruck()) {
            openPopover(anchor, notStruck(), false);
            return;
        }
        Migration migration = ui.game.getMigration();
        VBox body = new VBox(6);
        body.getChildren().add(words("Moved out: " + flowText(migration.getLastDepartures())
                + " - " + flowText(migration.getLastWorkDepartures()) + " for want of work, "
                + flowText(migration.getLastBankruptcyDepartures()) + " broke, "
                + flowText(migration.getLastCrimeDepartures()) + " driven out by crime",
                Palette.SIZE_BODY, Palette.TEXT_HEAD));
        body.getChildren().add(skillBar(migration.getLastDepartureMix()));
        body.getChildren().add(words("The educated leave first, and by a wide margin — their labour market is "
                + "national while a labourer's is local. A band whose wage has hit its "
                + "floor with people to spare sheds them every month.", Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        body.setMaxWidth(Pieces.POPOVER_WIDTH + 40);
        openPopover(anchor, body);
    }

    /* ------------------------------ why they come ------------------------------ */

    /** WHY PEOPLE COME's (i): what the bridge is. */
    static final String WHY_INFO = "The city draws people the way Migration strikes it each month: every post "
            + "supports a number of residents - its worker and the household behind them - and the homes "
            + "comfortably hold their people; jobs weigh three parts to the homes' one. Good senior care, dear "
            + "rent and crime then multiply that. Arrivals close part of the gap between the draw and the city "
            + "each month, slowed as housing runs out; people leave only when a trade has been dying for a year, "
            + "a band is pinned with people to spare, they go broke, or crime drives them out.";

    /** One box of the bridge: a caption over a figure, and what a click on it does. */
    VBox bridgeBox(String caption, String value, String under, String tone, Runnable go, String tip) {
        Label cap = words(caption, Palette.SIZE_CAPTION, Palette.TEXT_LABEL);
        Label fig = figure(value, Palette.SIZE_SECTION, tone);
        VBox box = new VBox(1, cap, fig);
        if (under != null) box.getChildren().add(words(under, Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        String rest = "-fx-padding: 6 10 6 10; -fx-background-color: " + Palette.PANEL + ";"
                + " -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: ";
        box.setStyle(rest + Palette.EDGE + ";" + (go != null ? " -fx-cursor: hand;" : ""));
        if (go != null) {
            box.setOnMouseEntered(e -> box.setStyle(rest + Palette.ACCENT + "; -fx-cursor: hand;"));
            box.setOnMouseExited(e -> box.setStyle(rest + Palette.EDGE + "; -fx-cursor: hand;"));
            box.setOnMouseClicked(e -> go.run());
        }
        if (tip != null) {
            Tooltip t = new Tooltip(tip);
            t.setShowDelay(Duration.millis(250));
            Tooltip.install(box, t);
        }
        box.setMaxWidth(Region.USE_PREF_SIZE);
        return box;
    }

    /** An operator between the bridge's boxes. */
    static Label op(String glyph) {
        Label l = new Label(glyph);
        l.setStyle(Palette.words(Palette.SIZE_SECTION, Palette.TEXT_MUTED));
        l.setMinWidth(Region.USE_PREF_SIZE);
        return l;
    }

    VBox bridgeCard() {
        if (!drawStruck()) {
            VBox c = card(4);
            c.getChildren().add(words(notStruck(), Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            return c;
        }
        Bridge b = bridge();
        javafx.scene.layout.FlowPane boxes = new javafx.scene.layout.FlowPane(Palette.GAP, Palette.GAP);
        boxes.setAlignment(Pos.CENTER_LEFT);
        boxes.setRowValignment(javafx.geometry.VPos.CENTER);
        javafx.scene.layout.FlowPane pulls = new javafx.scene.layout.FlowPane(Palette.GAP_TIGHT, Palette.GAP_TIGHT);
        pulls.getChildren().addAll(chip("senior care " + times(b.senior()), Palette.TEXT_LABEL),
                chip("rent " + times(b.rent()), Palette.TEXT_LABEL),
                chip("crime " + times(b.crime()), Palette.TEXT_LABEL));
        pulls.setPrefWrapLength(150);
        pulls.setMaxWidth(Region.USE_PREF_SIZE);
        boxes.getChildren().addAll(
                bridgeBox("jobs " + people(b.jobs()) + " × " + String.format("%.2f", b.perJob()) + " people · ¾",
                        people(b.jobDraw()), null, Palette.TEXT_HEAD, () -> scrollTo(workCard),
                        "Every post supports its worker and the household behind them: "
                                + String.format("%.2f", b.perJob()) + " people a post at this city's ages, "
                                + "weighted three parts in four. Click for the Work card."),
                op("+"),
                bridgeBox("homes for " + people(b.homesFor()) + " · ¼", people(b.homeDraw()), null,
                        Palette.TEXT_HEAD, () -> ui.buildScreen.openCategory(BuildAdvice.HOMES),
                        "The people the city's homes comfortably hold, weighted one part in four. Click for "
                                + "Build › Homes."),
                op("="),
                bridgeBox("jobs and homes", people(b.beforePulls()), null, Palette.TEXT_LABEL, null, null),
                op("×"),
                pulls,
                op("="),
                bridgeBox("a city this good draws", people(b.draws()), null, Palette.TEXT_HEAD, null,
                        "The month's target, which arrivals chase."));
        HBox.setHgrow(boxes, Priority.ALWAYS);
        boxes.setMinWidth(0);
        boxes.setPrefWrapLength(720);

        VBox against = bulletBar(b.living(), b.draws(), Palette.PEOPLE, 0,
                people(b.living()) + " living here against the draw · " + roomWords(b.draws(), b.living()));
        String crowdTone = b.crowding() <= 0 ? Palette.BAD : b.crowding() < .95 ? Palette.WARN : Palette.GOOD;
        HBox housing = new HBox(Palette.GAP, ring(b.crowding(), crowdTone, 44, 5,
                String.format("%.0f%%", b.crowding() * 100), 10.5),
                words("of the people it draws can find somewhere to live", Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        housing.setAlignment(Pos.CENTER_LEFT);
        HBox whyLine = new HBox(Palette.GAP_TIGHT, chip(b.word(), b.tone()),
                infoLine(b.why(), b.whyWhole(), false, Palette.SIZE_LABEL, Palette.TEXT_BODY, 360));
        whyLine.setAlignment(Pos.CENTER_LEFT);
        VBox right = new VBox(8, against, housing, whyLine);
        javafx.scene.layout.FlowPane shrinking = new javafx.scene.layout.FlowPane(Palette.GAP_TIGHT, Palette.GAP_TIGHT);
        Migration migration = ui.game.getMigration();
        for (PayTier tier : PayTier.values()) {
            if (!migration.isDeclining(tier)) continue;
            shrinking.getChildren().add(chip(tier.getLabel() + " work shrinking "
                    + migration.getDecliningStreak(tier) + " months", Palette.BAD));
        }
        if (!shrinking.getChildren().isEmpty()) right.getChildren().add(shrinking);
        right.setMinWidth(300);
        right.setPrefWidth(440);
        right.setMaxWidth(460);

        HBox body = new HBox(Palette.GAP_SECTION, boxes, right);
        body.setAlignment(Pos.CENTER_LEFT);
        VBox c = card(0);
        c.getChildren().add(body);
        return c;
    }

    /* ---------------------------------- care ---------------------------------- */

    /** Four of Build's rings, with Build's verdicts: General care, Childcare, Senior care, Death care. */
    javafx.scene.layout.GridPane careRow() {
        List<CityNeeds.Need> all = CityNeeds.measure(ui.game, SummaryScreen.WORDS);
        List<BuildAdvice.Measure> measures = BuildAdvice.measuresOf(BuildAdvice.HEALTHCARE);
        javafx.scene.layout.GridPane row = equalColumns(Math.max(1, measures.size()), TILE_GAP);
        Health health = ui.game.getHealth();
        for (int i = 0; i < measures.size(); i++) {
            BuildAdvice.Measure m = measures.get(i);
            BuildScreen.RingWords w = ui.buildScreen.ringWords(m, all);
            List<javafx.scene.Node> chips = new ArrayList<>();
            if (m.kind() == BuildAdvice.Kind.CARE && m.care() == CareType.GENERAL && health.isOutbreak()) {
                chips.add(chip(String.format("Outbreak, month %d",
                        Math.max(1, ui.game.getMonth() - health.getOutbreakStarted() + 1)), Palette.BAD));
            }
            HBox card = ringCard(w.figure(), w.arc(), w.tone(), m.label(), careInfo(m), careLine(m), w.shortLine(),
                    Palette.TEXT_MUTED, 56, false, chips, () -> toHealthcare(m),
                    m.label() + ": " + w.shortLine() + "\nClick to build for it: Build › Healthcare.");
            row.add(card, i, 0);
        }
        return row;
    }

    /** Under the rings, only when they fire: death care's two alerts, and the unburied. */
    List<javafx.scene.Node> careAlerts() {
        List<javafx.scene.Node> out = new ArrayList<>();
        Healthcare service = ui.game.getHealthcare();
        Health health = ui.game.getHealth();
        BuildingManager bm = ui.game.getBuildingManager();
        double plots = bm.getCareCapacity(CareType.BURIAL);
        double plotsLeft = Healthcare.plotsRemaining(plots, service.getPlotsUsed());
        double monthsLeft = service.monthsOfPlotsLeft(plots);
        if (service.isOverwhelmed()) {
            out.add(alert("Nowhere to bury them", String.format(
                    "%s people are lying unburied. It is adding %.0f points to the sick rate "
                    + "above, and it does not go away on its own. %s",
                    people(service.getUnburied()),
                    health.getUnburiedRate() * 100,
                    plotsLeft <= 0 && plots > 0
                            ? "Every plot in the city is full. Build another cemetery, or a "
                              + "crematorium, which needs almost no land."
                            : "Build a cemetery or a crematorium. A cemetery is cheap and "
                              + "vast; a crematorium is the opposite.")));
        } else if (service.isStrained()) {
            out.add(alert("Death care is filling up",
                    (service.getCremationUtilisation() >= Healthcare.STRAINED
                        ? String.format("The crematoria are at %.0f%% of what they can handle. ",
                                service.getCremationUtilisation() * 100)
                        : String.format("The ground runs out in about %,.0f months at this "
                                + "rate. ", monthsLeft))
                    + "A cemetery is a stock, not a rate: it is fine until the month it is "
                    + "full, and then people start piling up immediately. Plots are consumed "
                    + "permanently — the land never comes back."));
        }
        /*
         * The unburied get their own line rather than a place in a ring,
         * because they are the one thing here that is an emergency rather than
         * a level - and because a player seeing the sick rate climb with full
         * hospital coverage has no other way to find out why.
         */
        if (service.getUnburied() > 0 && !service.isOverwhelmed()) {
            out.add(sentence(String.format("%s lying unburied, adding %.1f points to the sick rate.",
                    people(service.getUnburied()), health.getUnburiedRate() * 100), Palette.BAD_TEXT));
        }
        return out;
    }

    /* ---------------------------------- homes ---------------------------------- */

    /** HOMES: households wanting a door against the doors, what is on site, one verdict and one line. */
    VBox homesCard() {
        Homes h = homes();
        List<Segment> parts = new ArrayList<>();
        double scale;
        if (!h.isShort()) {
            parts.add(new Segment(h.needed(), Palette.PEOPLE, false, null, null,
                    people(h.needed()) + " households want a home", null));
            parts.add(new Segment(h.spare(), Palette.PEOPLE_LIGHT, false, null, null,
                    people(h.spare()) + " homes spare", null));
            scale = h.built() + h.onSite();
        } else {
            parts.add(new Segment(h.built(), Palette.PEOPLE, false, null, null,
                    people(h.built()) + " households with a home", null));
            parts.add(new Segment(-h.spare(), Palette.BAD, true, null, null,
                    people(-h.spare()) + " short of a door", null));
            scale = h.needed() + h.onSite();
        }
        if (h.onSite() >= .5) {
            parts.add(new Segment(h.onSite(), Palette.BUILDING, true, null, null,
                    people(h.onSite()) + " homes on site", null));
        }
        SegmentBar bar = segmentBar(parts, scale, null, 0, 16);

        HBox key = new HBox(Palette.GAP, keySwatch(Palette.PEOPLE, "wanting one"),
                keySwatch(!h.isShort() ? Palette.PEOPLE_LIGHT : Palette.BAD, !h.isShort() ? "spare" : "short"),
                LandScreen.ghostSwatch(Palette.BUILDING, "on site"));
        key.setAlignment(Pos.CENTER_LEFT);

        Label figures = words(people(h.needed()) + " households want a home · " + people(h.built()) + " homes · "
                + people(h.onSite()) + " on site", Palette.SIZE_LABEL, Palette.TEXT_LABEL);
        HBox line = infoLine(h.line(), h.whole(), false, Palette.SIZE_LABEL, Palette.TEXT_BODY, 360);
        javafx.scene.layout.FlowPane chips = new javafx.scene.layout.FlowPane(Palette.GAP_TIGHT, Palette.GAP_TIGHT);
        if (h.shares() >= .5) chips.getChildren().add(chip(people(h.shares()) + " flatshares", Palette.TEXT_LABEL));
        if (h.doubled() >= .5) chips.getChildren().add(chip(people(h.doubled()) + " doubled up", Palette.TEXT_LABEL));
        HBox build = doorPill("Build homes", Icons.BUILD, Palette.BUILDING, () -> ui.buildScreen.openCategory(BuildAdvice.HOMES));

        VBox c = card(8);
        c.getChildren().addAll(cardHead("HOMES", null, chip(h.word(), h.tone())), bar, key, figures, line);
        if (!chips.getChildren().isEmpty()) c.getChildren().add(chips);
        c.getChildren().add(build);
        return c;
    }

    /* -------------------------------- households -------------------------------- */

    /** A household shape's tile words: short, the full name in its tooltip (0.7.27; the members are drawn). */
    static String shortShape(FamilyStructure s) {
        switch (s) {
            case SENIOR_ALONE:        return "senior alone";
            case SENIOR_COUPLE:       return "senior couple";
            case ELDER_ALONE:         return "elder alone";
            case ELDER_COUPLE:        return "elder couple";
            case SINGLE_ADULT:        return "single adult";
            case COUPLE:              return "couple";
            case SINGLE_PARENT:       return "single parent";
            case COUPLE_BABY:         return "+ baby";
            case COUPLE_CHILD:        return "+ child";
            case COUPLE_TEEN:         return "+ teen";
            case COUPLE_BABY_CHILD:   return "+ baby, child";
            case COUPLE_CHILD_TEEN:   return "+ child, teen";
            case COUPLE_TWO_CHILDREN: return "+ two children";
            case LARGE_FAMILY:        return "large family";
            case SHARED_ADULTS:       return "five sharing";
            default:                  return s.getLabel().toLowerCase();
        }
    }

    /**
     * A household's members, drawn: an adult a full dot, a teen and a child
     * smaller, a baby smaller still, a senior or an elder half filled - in
     * the teal's steps, so "Large family" reads as two adults and four
     * children and "Five adults sharing" as five adults.
     */
    static HBox members(int[] byBand) {
        HBox row = new HBox(2);
        row.setAlignment(Pos.CENTER_LEFT);
        AgeBand[] order = { AgeBand.ADULT, AgeBand.SENIOR, AgeBand.ELDER, AgeBand.TEEN, AgeBand.CHILD, AgeBand.BABY };
        for (AgeBand b : order) {
            for (int k = 0; k < byBand[b.ordinal()]; k++) row.getChildren().add(member(b));
        }
        return row;
    }

    /** One member's glyph. */
    static javafx.scene.Node member(AgeBand b) {
        double r = b == AgeBand.ADULT || b == AgeBand.SENIOR || b == AgeBand.ELDER ? 4
                : b == AgeBand.TEEN ? 3.2 : b == AgeBand.CHILD ? 2.6 : 1.8;
        javafx.scene.paint.Color colour = javafx.scene.paint.Color.web(
                b == AgeBand.ADULT ? Palette.PEOPLE
                        : b == AgeBand.SENIOR || b == AgeBand.ELDER ? Palette.PEOPLE_DARK : Palette.PEOPLE_LIGHT);
        javafx.scene.layout.StackPane holder = new javafx.scene.layout.StackPane();
        holder.setMinSize(9, 9);
        holder.setPrefSize(9, 9);
        holder.setMaxSize(9, 9);
        if (b == AgeBand.SENIOR || b == AgeBand.ELDER) {
            javafx.scene.shape.Circle ring = new javafx.scene.shape.Circle(r - .5);
            ring.setFill(null);
            ring.setStroke(colour);
            ring.setStrokeWidth(1);
            javafx.scene.shape.Arc half = new javafx.scene.shape.Arc(0, 0, r - .5, r - .5, 90, 180);
            half.setType(javafx.scene.shape.ArcType.ROUND);
            half.setFill(colour);
            holder.getChildren().add(new javafx.scene.Group(ring, half));
        } else {
            javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(r);
            dot.setFill(colour);
            holder.getChildren().add(dot);
        }
        holder.setMouseTransparent(true);
        return holder;
    }

    /** The members of a shape, as members() counts them. */
    static int[] membersOf(FamilyStructure s) {
        int[] out = new int[AgeBand.values().length];
        for (AgeBand b : AgeBand.values()) out[b.ordinal()] = s.membersOf(b);
        return out;
    }

    /** One adult, for the outside strip's tiles: a household of one. */
    static int[] one(AgeBand b) {
        int[] out = new int[AgeBand.values().length];
        out[b.ordinal()] = 1;
        return out;
    }

    /** One tile of the mosaic: its members, its words, how many, its tier mix (families only) and where a click opens. */
    record Piece(String name, String full, double count, int[] members, double[] tiers, String cellKey) { }

    /** The mosaic's three strips: the retired, the families, and the people outside them. */
    List<List<Piece>> mosaic() {
        FamilyModel families = ui.game.getFamilies();
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        Unemployment u = ui.game.getUnemployment();
        List<Piece> retired = new ArrayList<>(), working = new ArrayList<>(), outside = new ArrayList<>();
        for (FamilyStructure s : FamilyStructure.values()) {
            double n = families.totalOf(s);
            if (n < .5) continue;
            if (s.isRetired()) {
                Household cell = bal == null ? null : bal.cell(s);
                retired.add(new Piece(shortShape(s), s.getLabel(), n, membersOf(s), null,
                        cell == null ? null : "OUT:" + cell.key()));
            } else {
                double[] tiers = new double[PayTier.values().length];
                int biggest = 0;
                for (PayTier t : PayTier.values()) {
                    tiers[t.ordinal()] = families.get(s, t);
                    if (tiers[t.ordinal()] > tiers[biggest]) biggest = t.ordinal();
                }
                working.add(new Piece(shortShape(s), s.getLabel(), n, membersOf(s), tiers, biggest + ":" + s.name()));
            }
        }
        retired.sort((a, b) -> Double.compare(b.count(), a.count()));
        working.sort((a, b) -> Double.compare(b.count(), a.count()));
        if (bal != null) {
            String poolKey = null;
            double most = -1;
            for (UnemployedHousehold.Status st : UnemployedHousehold.Status.values()) {
                Household h = bal.unemployed(st);
                if (h != null && h.households() > most) { most = h.households(); poolKey = "OUT:" + h.key(); }
            }
            if (u.getPool() >= .5) {
                outside.add(new Piece("out of work", "Out of work", u.getPool(), one(AgeBand.ADULT), null, poolKey));
            }
            double students = families.getSeekers(FamilyModel.Seeker.STUDENT);
            if (students >= .5 && bal.students() != null) {
                outside.add(new Piece("students", "Full-time students", students, one(AgeBand.ADULT), null,
                        "OUT:" + bal.students().key()));
            }
            for (AgeBand b : AgeBand.values()) {
                double n = families.getOrphans(b);
                if (n < .5) continue;
                Household o = bal.orphans(b);
                outside.add(new Piece("orphans, " + b.getLabel().toLowerCase(), "Orphans, " + b.getLabel().toLowerCase(),
                        n, one(b), null, o == null ? null : "OUT:" + o.key()));
            }
            double prisoners = ui.game.getCrime().prisoners();
            if (prisoners >= .5) {
                outside.add(new Piece("in prison", "In prison", prisoners, one(AgeBand.ADULT), null,
                        bal.prisoners() == null ? null : "OUT:" + bal.prisoners().key()));
            }
        }
        outside.sort((a, b) -> Double.compare(b.count(), a.count()));
        return List.of(retired, working, outside);
    }

    /** The narrowest a tile is drawn; anything smaller folds into "+n more". */
    static final double TILE_MIN = 56;

    /** HOUSEHOLDS: the mosaic, the totals, the (i) and the matrix behind details. */
    VBox householdsCard() {
        FamilyModel families = ui.game.getFamilies();
        List<List<Piece>> strips = mosaic();
        String[] names = { "retired", "families", "outside" };
        double widest = 0;
        for (List<Piece> s : strips) {
            double sum = 0;
            for (Piece p : s) sum += p.count();
            widest = Math.max(widest, sum);
        }
        VBox rows = new VBox(6);
        for (int i = 0; i < strips.size(); i++) {
            double sum = 0;
            for (Piece p : strips.get(i)) sum += p.count();
            Label label = words(names[i] + "\n" + people(sum), Palette.SIZE_CAPTION, Palette.TEXT_LABEL);
            label.setMinWidth(64);
            label.setPrefWidth(64);
            Strip strip = new Strip(strips.get(i), sum, widest, i == 1, this);
            HBox.setHgrow(strip, Priority.ALWAYS);
            HBox row = new HBox(Palette.GAP, label, strip);
            row.setAlignment(Pos.CENTER_LEFT);
            rows.getChildren().add(row);
        }

        String kept = families.hasRecord()
                ? String.format("Kept from last month, before flatmates pair up: %s. %s re-formed on their own "
                        + "(%.0f%% a month) · %s no longer fitted — a child grown, a death, a job lost · %s "
                        + "formed from the people left over. The flatshares then pair five single adults into "
                        + "one household, which is why the kept can outnumber the households after it.",
                        people(families.getLastKept()), people(families.getLastReformed()),
                        FamilyModel.REFORMING_EACH_MONTH * 100, people(families.getLastNoLongerFit()),
                        people(families.getLastNew()))
                : "The households were built this month from scratch: there is no last month to keep them from.";
        HBox totals = infoLine(String.format("%s households · average %.2f people · %s adults unplaced",
                        people(families.totalHouseholds()), families.averageHouseholdSize(),
                        people(families.getUnhousedAdults())),
                kept, false, Palette.SIZE_LABEL, Palette.TEXT_LABEL, 600);
        VBox matrix = new VBox(2, shapeMatrix(families), statementNote(
                "Seniors carry no tier — they have no earner — so their row sits under the "
                + "first column by convention rather than by wage. The pay row is what one earner "
                + "of the tier was paid this month."));

        VBox c = card(8);
        c.getChildren().addAll(
                cardHead("HOUSEHOLDS", null, hint("click one for its books"),
                        stepChip("Household money ›", this::showHouseholdMenu, true)),
                rows,
                rampKey(),
                totals,
                details("matrix", "every household by shape and pay tier", matrix));
        return c;
    }

    /** The key to the ramp under a family's tile: a sample of the six steps and what they are. */
    static HBox rampKey() {
        HBox sample = Strip.ramp(new double[] {1, 1, 1, 1, 1, 1});
        sample.setMinWidth(36);
        sample.setPrefWidth(36);
        sample.setMaxWidth(36);
        HBox key = new HBox(5, sample, words("along a family's foot: its households by pay tier, unskilled at the "
                + "left to elite", Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        key.setAlignment(Pos.CENTER_LEFT);
        return key;
    }

    /**
     * One strip of the mosaic, laid out at whatever width the card gives it.
     * The strip is as long as its households on the widest strip's scale -
     * the families' fills the card, the retired's and the outside's are as
     * long as they are against it - and its tiles share that length as
     * their households do, none narrower than TILE_MIN: the biggest are
     * proportional and the rest sit at the minimum. Only when even the
     * minimums do not fit are the smallest folded into one "+n more" tile,
     * whose tooltip names them.
     */
    static final class Strip extends javafx.scene.layout.Pane {
        private final List<Piece> pieces;
        private final double total, widest;
        private final List<VBox> tiles = new ArrayList<>();
        private final VBox more;
        private final Label moreLabel = new Label();
        private final Tooltip moreTip = new Tooltip();

        Strip(List<Piece> pieces, double total, double widest, boolean families, PeopleScreen screen) {
            this.pieces = pieces;
            this.total = total;
            this.widest = widest;
            for (Piece p : pieces) {
                VBox t = tile(p, families, screen);
                tiles.add(t);
                getChildren().add(t);
            }
            moreLabel.setWrapText(true);
            moreLabel.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            more = new VBox(moreLabel);
            more.setAlignment(Pos.CENTER);
            more.setStyle("-fx-padding: 4; -fx-background-color: " + Palette.PANEL + "; -fx-background-radius: 4;");
            moreTip.setShowDelay(Duration.millis(250));
            Tooltip.install(more, moreTip);
            getChildren().add(more);
            if (pieces.isEmpty()) {
                moreLabel.setText("nobody");
            }
            setMinHeight(TILE_TALL);
            setPrefHeight(TILE_TALL);
            setMinWidth(TILE_MIN);
            setPrefWidth(600);
        }

        /** How tall a strip is. */
        static final double TILE_TALL = 62;

        @Override protected void layoutChildren() {
            double w = getWidth(), h = getHeight();
            int n = pieces.size();
            if (n == 0) {
                more.resizeRelocate(0, 0, Math.min(w, 120), h);
                return;
            }
            // The strip's own length: its households on the widest strip's scale, at least one tile.
            double length = Math.max(Math.min(w, TILE_MIN), Math.min(w, widest > 0 ? w * total / widest : w));
            // As many tiles as fit at the minimum, the "+n more" taking a place when any fold.
            int k = n;
            while (k > 0 && k * TILE_MIN + (k < n ? TILE_MIN : 0) > length + .5) k--;
            double room = length - (k < n ? TILE_MIN : 0);
            // The room shared as the households are, none under the minimum: the smallest are
            // floored in turn until the rest, shared proportionally, all clear it.
            double[] tw = new double[k];
            boolean[] floored = new boolean[k];
            for (int pass = 0; pass <= k; pass++) {
                double fixedWidth = 0, flexible = 0;
                for (int i = 0; i < k; i++) {
                    if (floored[i]) fixedWidth += TILE_MIN;
                    else flexible += pieces.get(i).count();
                }
                double px = flexible > 0 ? Math.max(0, room - fixedWidth) / flexible : 0;
                boolean changed = false;
                for (int i = 0; i < k; i++) {
                    if (!floored[i] && pieces.get(i).count() * px < TILE_MIN) {
                        floored[i] = true;
                        changed = true;
                    }
                }
                if (!changed || pass == k) {
                    for (int i = 0; i < k; i++) tw[i] = floored[i] ? TILE_MIN : pieces.get(i).count() * px;
                    break;
                }
            }
            double x = 0;
            for (int i = 0; i < n; i++) {
                VBox t = tiles.get(i);
                t.setVisible(i < k);
                if (i >= k) continue;
                double width = Math.min(tw[i], Math.max(0, w - x));
                t.resizeRelocate(x, 0, Math.max(0, width - 3), h);
                x += width;
            }
            more.setVisible(k < n);
            if (k < n) {
                double folded = 0;
                StringBuilder names = new StringBuilder();
                for (int i = k; i < n; i++) {
                    folded += pieces.get(i).count();
                    names.append(i > k ? "\n" : "").append(pieces.get(i).full()).append(": ")
                            .append(people(pieces.get(i).count()));
                }
                String text = "+" + (n - k) + " more\n" + people(folded);
                if (!text.equals(moreLabel.getText())) moreLabel.setText(text);
                if (!names.toString().equals(moreTip.getText())) moreTip.setText(names.toString());
                more.resizeRelocate(x, 0, Math.max(0, Math.min(TILE_MIN, w - x) - 3), h);
            }
        }

        /** One tile: the members drawn, the short name, the count, and under a family the tier ramp. */
        static VBox tile(Piece p, boolean families, PeopleScreen screen) {
            Label name = new Label(p.name());
            name.setWrapText(true);
            name.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
            Label count = new Label(people(p.count()));
            count.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_HEAD));
            Region push = new Region();
            VBox.setVgrow(push, Priority.ALWAYS);
            VBox t = new VBox(1, members(p.members()), name, push, count);
            if (families && p.tiers() != null) t.getChildren().add(ramp(p.tiers()));
            String rest = "-fx-padding: 4 5 3 5; -fx-background-color: " + Palette.PANEL + ";"
                    + " -fx-background-radius: 4; -fx-border-radius: 4; -fx-border-color: ";
            t.setStyle(rest + Palette.EDGE + ";" + (p.cellKey() != null ? " -fx-cursor: hand;" : ""));
            t.setMinWidth(0);
            if (p.cellKey() != null) {
                t.setOnMouseEntered(e -> t.setStyle(rest + Palette.ACCENT + "; -fx-cursor: hand;"));
                t.setOnMouseExited(e -> t.setStyle(rest + Palette.EDGE + "; -fx-cursor: hand;"));
                t.setOnMouseClicked(e -> screen.toBooks(p.cellKey()));
            }
            StringBuilder tip = new StringBuilder(p.full() + "\n" + people(p.count()) + " households");
            if (p.tiers() != null) {
                for (PayTier tier : PayTier.values()) {
                    double n = p.tiers()[tier.ordinal()];
                    if (n >= .5) tip.append("\n  ").append(tier.getLabel()).append(": ").append(people(n));
                }
            }
            if (p.cellKey() != null) tip.append("\nclick for its books");
            Tooltip tt = new Tooltip(tip.toString());
            tt.setShowDelay(Duration.millis(250));
            Tooltip.install(t, tt);
            return t;
        }

        /** A colour as the style sheets write it: "#93e2da". */
        static String hex(javafx.scene.paint.Color c) {
            return String.format("#%02x%02x%02x", (int) Math.round(c.getRed() * 255),
                    (int) Math.round(c.getGreen() * 255), (int) Math.round(c.getBlue() * 255));
        }

        /** A family's tiers along its foot: six segments as wide as their households, unskilled light to elite dark. */
        static HBox ramp(double[] tiers) {
            HBox r = new HBox(0);
            r.setMinHeight(3);
            r.setPrefHeight(3);
            r.setMaxHeight(3);
            double sum = 0;
            for (double v : tiers) sum += v;
            javafx.scene.paint.Color light = javafx.scene.paint.Color.web(Palette.PEOPLE_LIGHT);
            javafx.scene.paint.Color dark = javafx.scene.paint.Color.web(Palette.PEOPLE_DARK);
            for (int i = 0; i < tiers.length; i++) {
                if (sum <= 0 || tiers[i] <= 0) continue;
                Region seg = new Region();
                javafx.scene.paint.Color c = light.interpolate(dark, tiers.length > 1 ? (double) i / (tiers.length - 1) : 0);
                seg.setStyle("-fx-background-color: " + hex(c) + ";");
                seg.setMinHeight(3);
                seg.setPrefHeight(3);
                seg.setMinWidth(0);
                HBox.setHgrow(seg, Priority.ALWAYS);
                seg.setPrefWidth(tiers[i] / sum * 1000);
                r.getChildren().add(seg);
            }
            return r;
        }
    }

    /* --------------------------- outside the families --------------------------- */

    /** OUTSIDE THE FAMILIES' (i): who they are, the old page's words. */
    static final String OUTSIDE_INFO = "Families are built from the adults who work, and their children. "
            + "Everybody else is here: the out of work and the students keep their own books, the unhoused "
            + "have no home, and the orphans are the children no family took.";

    /** The five tiles, the pool's month and EI, the orphans' alert, and the table by age behind details. */
    List<javafx.scene.Node> outsideRow() {
        List<javafx.scene.Node> out = new ArrayList<>();
        List<Tile> tiles = outsideTiles();
        javafx.scene.layout.GridPane row = equalColumns(tiles.size(), TILE_GAP);
        for (int i = 0; i < tiles.size(); i++) row.add(outsideTile(tiles.get(i)), i, 0);
        out.add(row);
        out.add(poolCard());

        FamilyModel families = ui.game.getFamilies();
        Unemployment u = ui.game.getUnemployment();
        double[] noDoor = families.unhousedPeopleByBand();
        noDoor[AgeBand.ADULT.ordinal()] += u.getUnhoused();
        double unhoused = 0;
        for (double v : noDoor) unhoused += v;
        if (families.getOrphansTotal() >= .5 || unhoused >= .5) {
            out.add(alert("Nobody feeds the orphans",
                    "Nobody feeds or cares for the orphans: they go hungry, get sick and die "
                    + "far faster than other children. The unhoused get sick and die faster "
                    + "too. Both are in the sick rate and in the deaths above."));
        }
        out.add(details("outside", "everybody outside the families, by age", outsideTable(u, families, noDoor)));
        return out;
    }

    /** One tile: its icon, name, count, split, and a sparkline of the last ten years. */
    VBox outsideTile(Tile t) {
        Label name = words(t.name(), Palette.SIZE_LABEL, Palette.TEXT_LABEL);
        Label count = figure(people(t.count()), Palette.SIZE_SECTION, t.tone());
        HBox top = new HBox(Palette.GAP, iconSquare(t.icon(), Palette.PEOPLE, 28, 15), new VBox(0, name, count));
        top.setAlignment(Pos.CENTER_LEFT);
        Label split = words(t.split(), Palette.SIZE_CAPTION, Palette.TEXT_MUTED);
        VBox c = new VBox(4, top, split, spark(ui.game.getHistorySave().aligned(t.series()), 120, 24));
        c.setMaxWidth(Double.MAX_VALUE);
        String rest = CARD + " -fx-cursor: hand; -fx-border-color: ";
        c.setStyle(rest + Palette.EDGE + ";");
        c.setOnMouseEntered(e -> c.setStyle(rest + Palette.ACCENT + ";"));
        c.setOnMouseExited(e -> c.setStyle(rest + Palette.EDGE + ";"));
        c.setOnMouseClicked(e -> openOutside(t.name()));
        Tooltip tip = new Tooltip(t.name() + ": " + people(t.count()) + "\n" + t.split()
                + "\nthe line: the last ten years\nClick: " + t.where());
        tip.setShowDelay(Duration.millis(250));
        Tooltip.install(c, tip);
        return c;
    }

    /** Where an outside tile's click goes: Services for the students and the prisons, the books for the rest. */
    void openOutside(String name) {
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        switch (name) {
            case "Students":  toServices("Education", null); return;
            case "In prison": toServices("Safety", "Prisons"); return;
            case "Orphans": {
                Household most = null;
                for (AgeBand b : AgeBand.values()) {
                    Household o = bal == null ? null : bal.orphans(b);
                    if (o != null && (most == null || o.households() > most.households())) most = o;
                }
                toBooks(most == null ? null : "OUT:" + most.key());
                return;
            }
            default: {
                Household most = null;
                if (bal != null) {
                    for (UnemployedHousehold.Status st : UnemployedHousehold.Status.values()) {
                        Household h = bal.unemployed(st);
                        if (h != null && (most == null || h.households() > most.households())) most = h;
                    }
                }
                toBooks(most == null ? null : "OUT:" + most.key());
            }
        }
    }

    /** A sparkline of the last `months` of a history series, in the people teal; a dash with no history. */
    javafx.scene.Node spark(double[] series, int months, double tall) {
        double w = 120;
        int from = Math.max(0, series.length - months);
        double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE;
        int points = 0;
        for (int i = from; i < series.length; i++) {
            if (!Double.isFinite(series[i])) continue;
            lo = Math.min(lo, series[i]);
            hi = Math.max(hi, series[i]);
            points++;
        }
        if (points < 2) {
            Label none = words("no history yet", Palette.SIZE_CAPTION, Palette.TEXT_SPENT);
            none.setMinHeight(tall);
            return none;
        }
        lo = Math.min(lo, 0);
        javafx.scene.shape.Polyline line = new javafx.scene.shape.Polyline();
        line.setFill(null);
        line.setStroke(javafx.scene.paint.Color.web(Palette.PEOPLE));
        line.setStrokeWidth(1.5);
        int n = series.length - from;
        for (int i = from; i < series.length; i++) {
            if (!Double.isFinite(series[i])) continue;
            double x = n > 1 ? (double) (i - from) / (n - 1) * w : 0;
            double y = hi > lo ? tall - (series[i] - lo) / (hi - lo) * (tall - 2) - 1 : tall / 2;
            line.getPoints().addAll(x, y);
        }
        javafx.scene.layout.Pane holder = new javafx.scene.layout.Pane(line);
        holder.setMinSize(w, tall);
        holder.setPrefSize(w, tall);
        holder.setMaxSize(w, tall);
        holder.setMouseTransparent(true);
        return holder;
    }

    /** The pool's month, in against out, and EI: what it paid against what the premiums raised. */
    VBox poolCard() {
        Pool p = pool();
        double scale = Math.max(p.inTotal(), p.outTotal());
        VBox flows = new VBox(4,
                poolBar("in " + flowText(p.inTotal()), p.in(), scale),
                poolBar("out " + flowText(p.outTotal()), p.out(), scale),
                infoLine(flowText(p.droppedOffEi()) + " reached the end of their EI this month",
                        "Only jobs that actually close put somebody on EI; an arrival who finds no "
                        + "work is on it too. It lasts twelve months. Past that they live on what "
                        + "they saved and what the bank will lend, and when both are gone and the "
                        + "rent is not paid, a quarter leave and the rest lose their home.",
                        false, Palette.SIZE_CAPTION, Palette.TEXT_MUTED, 520));
        HBox.setHgrow(flows, Priority.ALWAYS);
        flows.setMinWidth(0);

        String paidLine = "EI paid " + money(p.paid())
                + (p.perClaimant() > 0 ? " (" + moneyFull(p.perClaimant()) + " a claimant)" : "")
                + " · premiums " + money(p.premiums()) + String.format(" at %.2f%% of wages", p.premiumRate() * 100);
        VBox ei = new VBox(4, words(paidLine, Palette.SIZE_LABEL, Palette.TEXT_LABEL),
                bulletBar(p.premiums(), p.paid(), Palette.MONEY, 0, null),
                infoLine(p.cover(), p.coverWhole(), false, Palette.SIZE_CAPTION, Palette.TEXT_MUTED, 400));
        ei.setMinWidth(300);
        ei.setPrefWidth(440);

        HBox body = new HBox(Palette.GAP_SECTION, flows, ei);
        body.setAlignment(Pos.TOP_LEFT);
        VBox c = card(6);
        c.getChildren().addAll(cardHead("THE POOL THIS MONTH", null, hint("who joined the out of work, and who left")), body);
        return c;
    }

    /** One of the pool's two bars, on the two's common scale, with its parts named under it. */
    VBox poolBar(String caption, List<Slice> parts, double scale) {
        List<Segment> segs = new ArrayList<>();
        StringBuilder named = new StringBuilder(caption + ": ");
        boolean first = true;
        for (Slice s : parts) {
            segs.add(new Segment(Math.max(0, s.amount()), s.colour(), false, null, null,
                    s.name() + ": " + flowText(s.amount()), null));
            if (!first) named.append(" · ");
            named.append(flowText(s.amount())).append(' ').append(s.name());
            first = false;
        }
        SegmentBar bar = segmentBar(segs, scale > 0 ? scale : 1, null, 0, 10);
        Label words = words(named.toString(), Palette.SIZE_CAPTION, Palette.TEXT_LABEL);
        return new VBox(2, bar, words);
    }

    /* ---------------------------------- work ---------------------------------- */

    /** WORK: five figures, the labour line, the ladder drawn, the professions, three tables behind details. */
    VBox workCard() {
        PopulationManager pm = ui.game.getPopulationManager();
        LabourMarket market = ui.game.getLabourMarket();
        int unfilled = 0;
        for (int v : pm.getJobVacancy()) unfilled += v;
        HBox strip = new HBox(Palette.GAP_SECTION,
                mini("WORKERS", people(pm.getWorkforce()), Palette.TEXT_HEAD),
                mini("POSTS", people(pm.getTotalJobs()), Palette.TEXT_HEAD),
                mini("UNFILLED", people(unfilled), unfilled > 0 ? Palette.WARN : Palette.TEXT_HEAD),
                mini("IN A JOB", people(pm.getJobsFilled()), Palette.TEXT_HEAD),
                mini("LOOKING", people(pm.getUnemployed()), Palette.TEXT_HEAD));
        strip.setAlignment(Pos.CENTER_LEFT);
        String[] labour = labourWords();
        HBox labourLine = infoLine(labour[0], labour[1], false, Palette.SIZE_LABEL, labour[2], 700);

        List<Rung> rungs = ladder();
        double scale = 0;
        for (Rung r : rungs) scale = Math.max(scale, Math.max(r.open(), r.queue()));
        VBox ladder = new VBox(4);
        String pulled = pulledWords();
        for (Rung r : rungs) ladder.getChildren().add(rung(r, scale, pulled));
        HBox ladderKey = new HBox(Palette.GAP_LOOSE, keySwatch(Palette.PEOPLE, "open: posts this band can be put into"),
                LandScreen.ghostSwatch(Palette.PEOPLE_LIGHT, "queue: everyone who can take them"));
        HBox ladderHead = cardHead("THE SKILL LADDER", LADDER_INFO, ladderKey);

        VBox c = card(8);
        c.getChildren().addAll(strip, labourLine, ladderHead, ladder);
        String oneMarket = oneMarketNote(pm);
        if (oneMarket != null) c.getChildren().add(words(oneMarket, Palette.SIZE_LABEL, Palette.TEXT_LABEL));

        javafx.scene.layout.FlowPane professions = new javafx.scene.layout.FlowPane(Palette.GAP_TIGHT, Palette.GAP_TIGHT);
        for (EducationType type : EducationType.values()) {
            if (!type.isProfessional()) continue;
            JobType job = type.licenses();
            int posts = pm.getJobs()[job.ordinal()];
            double held = pm.getLicensed(job);
            if (posts <= 0 && held < .5) continue;
            professions.getChildren().add(chip(String.format("%s %s / %s · %s",
                    capitalised(ui.buildScreen.jobPlural(job)), people(held), people(posts),
                    times(market.licencePremium(job))), held < posts ? Palette.BAD : Palette.TEXT_LABEL));
        }
        if (!professions.getChildren().isEmpty()) {
            HBox prof = new HBox(Palette.GAP, words("Licensed:", Palette.SIZE_LABEL, Palette.TEXT_LABEL), professions);
            prof.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(professions, Priority.ALWAYS);
            c.getChildren().add(prof);
        }

        javafx.scene.layout.GridPane gated = professionTable(pm, market);
        c.getChildren().addAll(
                details("ladder", "the skill ladder as a table",
                        ladderTable(pm, market, ui.game.getEducation().getStudying())),
                details("professions", "the licensed professions",
                        gated != null ? gated : words("No licensed posts and nobody licensed.", Palette.SIZE_LABEL,
                                Palette.TEXT_MUTED)),
                details("posts", "every post in the city",
                        jobTable(pm.getJobs(), pm.getJobVacancy(), pm.getJobFillRate(), pm.getWagesPerType())),
                words(String.format("Minimum wage %s a month; every wage in the city is a multiple of it.",
                        cash(market.cashMinimumWage())), Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        return c;
    }

    /** The ladder's (i): the old legend. */
    static final String LADDER_INFO = "open = posts this band can be put into (licensed posts are on the "
            + "chips under the ladder). queue = its own workers plus everyone over-qualified who came down for "
            + "the same jobs, less any drawn further down by better pay. chance = open/queue, which is what "
            + "decides whether anybody in this band moves here at all. pay = the band's wage against the going "
            + "rate, red over 1.05x: the shortage showing up as money before it shows up as a gap.";

    /** "Doctors" from "doctors". */
    static String capitalised(String s) {
        return s == null || s.isEmpty() ? "" : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /** A small figure under a caption, for the Work card's strip. */
    static VBox mini(String caption, String value, String tone) {
        return new VBox(0, words(caption, Palette.SIZE_CAPTION, Palette.TEXT_LABEL),
                figure(value, Palette.SIZE_SECTION, tone));
    }

    /** One rung: its name, the queue as an outline with the open posts filled over it, the figures, the chips. */
    HBox rung(Rung r, double scale, String pulled) {
        Label name = words(r.band().label(), Palette.SIZE_LABEL, Palette.TEXT_BODY);
        name.setMinWidth(110);
        name.setPrefWidth(110);
        Pair pair = new Pair(r.open(), r.queue(), scale);
        HBox.setHgrow(pair, Priority.ALWAYS);
        Label figures = words(people(r.open()) + " open · " + people(r.queue()) + " queue", Palette.SIZE_CAPTION,
                Palette.TEXT_LABEL);
        figures.setMinWidth(150);
        figures.setPrefWidth(150);
        HBox chips = new HBox(Palette.GAP_TIGHT, chip(String.format("chance %.0f%%", r.chance() * 100), Palette.TEXT_LABEL),
                chip(String.format("pay %.2f×", r.premium()), payTone(r.premium())));
        if (r.reach() > 0) chips.getChildren().add(chip(String.format("reach %.0f%%", r.reach() * 100), Palette.TEXT_LABEL));
        chips.getChildren().add(infoButton(pulled, false));
        chips.setAlignment(Pos.CENTER_LEFT);
        chips.setMinWidth(Region.USE_PREF_SIZE);
        HBox row = new HBox(Palette.GAP, name, pair, figures, chips);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(30);
        return row;
    }

    /** The queue as a tinted outline and the open posts filled over it, on one scale. */
    static final class Pair extends javafx.scene.layout.Pane {
        private final double open, queue, scale;
        private final Region q = new Region(), o = new Region(), track = new Region();

        Pair(double open, double queue, double scale) {
            this.open = open;
            this.queue = queue;
            this.scale = scale;
            track.setStyle("-fx-background-color: " + Palette.CONTROL + "; -fx-background-radius: 4;");
            q.setStyle("-fx-background-color: " + Palette.PEOPLE_LIGHT + "33; -fx-border-color: " + Palette.PEOPLE_LIGHT
                    + "; -fx-border-width: 1; -fx-background-radius: 4; -fx-border-radius: 4;");
            o.setStyle("-fx-background-color: " + Palette.PEOPLE + "; -fx-background-radius: 4;");
            getChildren().addAll(track, q, o);
            setMinWidth(80);
            setPrefWidth(640);
            setMinHeight(16);
            setPrefHeight(16);
        }

        @Override protected void layoutChildren() {
            double w = getWidth(), h = getHeight();
            double band = 14, y = (h - band) / 2;
            track.resizeRelocate(0, y, w, band);
            double qw = scale > 0 ? w * queue / scale : 0, ow = scale > 0 ? w * open / scale : 0;
            q.setVisible(qw > 0);
            q.resizeRelocate(0, y, Math.max(1, qw), band);
            o.setVisible(ow > 0);
            o.resizeRelocate(0, y + 3, Math.max(1, ow), band - 6);
        }
    }

    /* =====================================================================
       THE TABLES BEHIND "details", and the pieces they share with the
       Household money page.
       ===================================================================== */

    /** Who arrived, or who left, by the skill they hold. */
    javafx.scene.layout.GridPane mixTable(double[] mix, double total) {

        javafx.scene.layout.GridPane table =
                grid(new double[] {150, 84, 70}, rightAfterFirst(3));
        gridHead(table, "skill", "people", "share");

        int line = 1;
        for (WageBand band : WageBand.values()) {
            double n = mix[band.ordinal()];
            String tone = n <= 0 ? Palette.TEXT_SPENT : Palette.TEXT_BODY;
            table.add(gridCell(band.label(), tone, Palette.SIZE_CAPTION, false), 0, line);
            table.add(gridCell(people(n), tone, Palette.SIZE_CAPTION, true), 1, line);
            table.add(gridCell(String.format("%.1f%%", total > 0 ? n / total * 100 : 0),
                    tone, Palette.SIZE_CAPTION, true), 2, line);
            line++;
        }
        return table;
    }


    /** Everybody outside the families, by age. A dot where nobody is. */
    javafx.scene.layout.GridPane outsideTable(Unemployment u, FamilyModel families,
                                                     double[] noDoor) {
        javafx.scene.layout.GridPane table = grid(
                new double[] {SHAPE_COL, 76, 70, 70, 70, 70, 70}, rightAfterFirst(7));
        gridHead(table, "age", "out of work", "students", "no home", "orphans", "in prison", "total");

        double students = families.getSeekers(FamilyModel.Seeker.STUDENT);
        double prisoners = ui.game.getCrime().prisoners();
        double[] sum = new double[6];
        int line = 1;
        for (AgeBand band : AgeBand.values()) {
            int b = band.ordinal();
            double out = band == AgeBand.ADULT ? u.getPool() : 0;
            double study = band == AgeBand.ADULT ? students : 0;
            double home = noDoor[b];
            double orphan = families.getOrphans(band);
            double inside = band == AgeBand.ADULT ? prisoners : 0;
            // The out of work who lost their home are in both columns; the total counts them once.
            double total = out + study + orphan + inside
                    + home - (band == AgeBand.ADULT ? Math.min(home, u.getUnhoused()) : 0);
            double[] row = { out, study, home, orphan, inside, total };
            table.add(gridCell(band.getLabel().toLowerCase(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            for (int c = 0; c < row.length; c++) {
                String tone = row[c] < .5 ? Palette.TEXT_SPENT
                        : c == 2 || c == 3 ? Palette.BAD_SOFT : Palette.TEXT_BODY;
                table.add(gridCell(cell(row[c]), tone, Palette.SIZE_CAPTION, true), c + 1, line);
                sum[c] += row[c];
            }
            line++;
        }
        table.add(gridCell("everybody", Palette.TEXT_HEAD, Palette.SIZE_CAPTION, false), 0, line);
        for (int c = 0; c < sum.length; c++) {
            table.add(gridCell(cell(sum[c]), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), c + 1, line);
        }
        return table;
    }

    /**
     * Household shape down the side, pay tier across the top.
     *
     * The same two axes the affordability grid crosses, counting households
     * instead of dollars - so a player can read "the city's large families are
     * the poor ones" off one screen and "and here is what that costs them" off
     * the other.
     */
    javafx.scene.layout.GridPane shapeMatrix(FamilyModel families) {

        int columns = PayTier.values().length + 2;
        double[] widths = new double[columns];
        widths[0] = SHAPE_COL;
        for (int i = 1; i < columns; i++) widths[i] = 64;

        javafx.scene.layout.GridPane table = grid(widths, rightAfterFirst(columns));

        String[] heads = new String[columns];
        heads[0] = "household";
        int at = 1;
        for (PayTier tier : PayTier.values()) heads[at++] = shortTier(tier);
        heads[at] = "total";
        gridHead(table, heads);

        /*
         * WHAT ONE EARNER WAS PAID THIS MONTH (0.7.27), the household money
         * page's figure (HouseholdAccounts.wagePerEarner()). The row read
         * PayTier.getMonthlyWage(), the tiers' founding anchors - $3,460 to
         * $15,600 - beside a live wage of $3,819 to $19,563.
         */
        HouseholdAccounts hh = ui.game.getHouseholds();
        int line = 1;
        table.add(gridCell("one earner was paid", Palette.TEXT_MUTED,
                Palette.SIZE_CAPTION, false), 0, line);
        int col = 1;
        for (PayTier tier : PayTier.values()) {
            double paid = hh.wagePerEarner(families, tier);
            table.add(gridCell(paid > 0 ? tightMoney(toDollars(paid), false) : ".",
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), col++, line);
        }
        line++;
        double[] working = new double[PayTier.values().length];

        for (FamilyStructure shape : FamilyStructure.values()) {
            double count = families.totalOf(shape);
            if (count < .5) continue;

            // Flatshares in amber wherever they appear: they are the one shape
            // nobody chooses, so a row of them is a housing shortage with a
            // number on it.
            String tone = shape == FamilyStructure.SHARED_ADULTS
                    ? Palette.WARN : Palette.TEXT_BODY;

            table.add(gridCell(shape.getLabel(), tone, Palette.SIZE_CAPTION, false), 0, line);
            col = 1;
            for (PayTier tier : PayTier.values()) {
                double n = families.get(shape, tier);
                if (!shape.isRetired()) working[tier.ordinal()] += n;
                table.add(gridCell(cell(n), n < .5 ? Palette.TEXT_SPENT : tone,
                        Palette.SIZE_CAPTION, true), col++, line);
            }
            table.add(gridCell(shape.isRetired() ? "no earner" : people(count),
                    tone, Palette.SIZE_CAPTION, true), col, line);
            line++;
        }

        /*
         * The column totals, which is the answer to "how rich is this city" -
         * of the WORKING households, the rows above that have a tier (0.7.27).
         * FamilyModel.totalOf(tier) counts the retired in the first column,
         * where they sit by convention, so the unskilled column read 24,697
         * households of which 9,725 earned nothing. The last cell is every
         * household, the retired with them.
         */
        table.add(gridCell("working households", Palette.TEXT_HEAD,
                Palette.SIZE_CAPTION, false), 0, line);
        col = 1;
        for (PayTier tier : PayTier.values()) {
            table.add(gridCell(cell(working[tier.ordinal()]), Palette.TEXT_HEAD,
                    Palette.SIZE_CAPTION, true), col++, line);
        }
        table.add(gridCell(people(families.totalHouseholds()),
                Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), col, line);
        return table;
    }

    /**
     * The bands this month's fill joined into one market, in a sentence, or
     * null when none were (0.7.18): workers take the best-paid post they
     * qualify for, so bands paying the same share the shortage.
     */
    String oneMarketNote(PopulationManager pm) {
        PopulationManager.BandFill fill = pm.fillByBand();
        StringBuilder names = new StringBuilder();
        WageBand[] bands = WageBand.values();
        for (int b = bands.length - 1; b >= 0; b--) {
            if (!fill.joinedAbove(bands[b])) continue;
            // A run of joined bands, named from the top down.
            java.util.List<String> run = new java.util.ArrayList<>();
            run.add(bands[b + 1].label());
            while (b >= 0 && fill.joinedAbove(bands[b])) run.add(bands[b--].label());
            b++;
            if (names.length() > 0) names.append("; ");
            for (int i = 0; i < run.size(); i++) {
                names.append(i == 0 ? "" : i == run.size() - 1 ? " and " : ", ").append(run.get(i));
            }
        }
        if (names.length() == 0) return null;
        int none = WageBand.NONE.ordinal();
        double own = pm.workforceByBand()[none];
        double down = Math.max(0, fill.placed[none] - Math.min(own, fill.placed[none]));
        return names + " fill as one market this month, their wages headed for the same level: workers take the "
                + "best-paid post they qualify for, so the shortage is shared up the ladder"
                + (down >= 1 ? String.format(" (%s over-qualified workers hold unskilled posts).", people(down)) : ".");
    }

    /** The skill ladder: what each band has, what it can take, and what it costs. */
    javafx.scene.layout.GridPane ladderTable(PopulationManager pm,
                                                     LabourMarket market,
                                                     double[] studying) {

        javafx.scene.layout.GridPane table = grid(
                new double[] {96, 74, 62, 70, 74, 62, 54, 54}, rightAfterFirst(8));
        gridHead(table, "skill", "workers", "study", "open", "queue", "chance", "tight", "pay");

        double[] own = pm.workforceByBand();
        // OPEN, not posts. A doctor post is not a job an ordinary graduate can
        // take, so counting it here would say the graduate band is short when
        // what is short is doctors - and the wage is priced on this number, so
        // the column has to be the one the market used. The posts only a
        // licence holder can fill are in the professions table below.
        double[] open = pm.staffablePostsByBand();
        double[] queue = pm.supplyByBand();

        int line = 1;
        for (WageBand band : WageBand.values()) {
            int b = band.ordinal();
            // Off an ungated job: the first university job in enum order is
            // the doctor, whose own licence premium is not the band's.
            double premium = market.bandPremium(band);

            /*
             * CHANCE is the number that decides who moves here, and it is not
             * the wage. It is this band's open posts against everybody queueing
             * for them - its own people PLUS every over-qualified worker who
             * came down a rung, less (0.7.18) any drawn further down by better
             * pay - which is why a city full of graduates doing
             * diploma work is not a city an incoming diploma-holder wants.
             */
            double chance = queue[b] > 0 ? Math.min(1, open[b] / queue[b]) : 1;

            // Red when the city is paying over the odds to staff it, which is
            // the shortage showing up as money before it shows up as a gap.
            String tone = premium > 1.05 ? Palette.BAD
                    : premium < .95 ? Palette.TEXT_MUTED : Palette.TEXT_BODY;

            String[] cells = {
                people(own[b]),
                people(studying[b]),
                people(open[b]),
                people(queue[b]),
                String.format("%.0f%%", chance * 100),
                String.format("%.2f", market.getTightness(band)),
                String.format("%.2fx", premium)
            };
            table.add(gridCell(band.label(), tone, Palette.SIZE_CAPTION, false), 0, line);
            for (int i = 0; i < cells.length; i++) {
                table.add(gridCell(cells[i], tone, Palette.SIZE_CAPTION, true), i + 1, line);
            }
            line++;
        }
        return table;
    }

    /** The posts only a licence can fill. Null when the city has none of them. */
    javafx.scene.layout.GridPane professionTable(PopulationManager pm,
                                                         LabourMarket market) {
        boolean any = false;
        for (JobType job : JobType.values()) {
            if (!PopulationManager.isGated(job)) continue;
            if (pm.getJobs()[job.ordinal()] > 0 || pm.getLicensed(job) > 0) any = true;
        }
        if (!any) return null;

        javafx.scene.layout.GridPane table = grid(
                new double[] {150, 78, 66, 62, 66, 118},
                new javafx.geometry.HPos[] {
                    javafx.geometry.HPos.LEFT, javafx.geometry.HPos.RIGHT,
                    javafx.geometry.HPos.RIGHT, javafx.geometry.HPos.RIGHT,
                    javafx.geometry.HPos.RIGHT, javafx.geometry.HPos.LEFT});
        gridHead(table, "profession", "licensed", "posts", "paying", "pulling", "school");

        int line = 1;
        for (EducationType type : EducationType.values()) {
            if (!type.isProfessional()) continue;
            JobType job = type.licenses();
            int posts = pm.getJobs()[job.ordinal()];
            double held = pm.getLicensed(job);
            if (posts <= 0 && held <= 0) continue;

            boolean built = ui.game.getBuildingManager()
                    .getBuiltEducationPlaces()[type.ordinal()] > 0;

            /*
             * PAYING is the realised premium over this profession's own band -
             * what the city actually hands a doctor above what it hands an
             * ordinary graduate. PULLING is what that buys in migrants, and it
             * is its own number: a profession draws on its own shortage, so a
             * hospital can import doctors into a city drowning in graduates.
             */
            double paying = market.licencePremium(job);
            String tone = held < posts ? Palette.BAD : Palette.TEXT_BODY;

            table.add(gridCell(ui.buildScreen.jobLabel(job), tone, Palette.SIZE_CAPTION, false), 0, line);
            table.add(gridCell(people(held), tone,
                    Palette.SIZE_CAPTION, true), 1, line);
            table.add(gridCell(people(posts), tone,
                    Palette.SIZE_CAPTION, true), 2, line);
            table.add(gridCell(String.format("%.2fx", paying), tone,
                    Palette.SIZE_CAPTION, true), 3, line);
            table.add(gridCell(String.format("%.0f%%", Migration.reach(paying) * 100),
                    tone, Palette.SIZE_CAPTION, true), 4, line);
            table.add(gridCell(built ? "yes" : "none — imports only",
                    built ? Palette.TEXT_MUTED : Palette.WARN,
                    Palette.SIZE_CAPTION, false), 5, line);
            line++;
        }
        return table;
    }

    /** Every kind of post the city has built, and how well it is staffed. */
    javafx.scene.layout.GridPane jobTable(int[] jobs, int[] vacancies,
                                                  double[] fillRates, double[] jobWage) {

        javafx.scene.layout.GridPane table = grid(
                new double[] {150, 74, 74, 62, 74, 112}, rightAfterFirst(6));
        gridHead(table, "post", "built", "unfilled", "staffed", "wage", "payroll");

        JobType[] kinds = JobType.values();
        int line = 1;
        for (int i = 0; i < jobs.length; i++) {
            if (jobs[i] <= 0) continue;
            String tone = fillRates[i] < .9 ? Palette.BAD : Palette.TEXT_BODY;
            table.add(gridCell(ui.buildScreen.jobLabel(kinds[i]), tone, Palette.SIZE_CAPTION, false), 0, line);
            table.add(gridCell(people(jobs[i]), tone,
                    Palette.SIZE_CAPTION, true), 1, line);
            table.add(gridCell(people(vacancies[i]),
                    vacancies[i] > 0 ? tone : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 2, line);
            table.add(gridCell(String.format("%.0f%%", fillRates[i] * 100), tone,
                    Palette.SIZE_CAPTION, true), 3, line);
            table.add(gridCell(cash(jobWage[i]), tone,
                    Palette.SIZE_CAPTION, true), 4, line);
            table.add(gridCell(tightMoney(toDollars(jobWage[i] * jobs[i])), tone,
                    Palette.SIZE_CAPTION, true), 5, line);
            line++;
        }
        return table;
    }

    /**
     * Whether the per-tier table shows one family or the whole city.
     *
     * A screen-level toggle rather than two tables, because they answer two
     * different questions and only one of them is being asked at a time: "can a
     * family like this live here" and "where is the city's money". Per family is
     * the default because it is the one a player can act on - a total of $12.4M
     * does not tell you whether anybody is short.
     */
    boolean householdPerFamily = true;

    /* =====================================================================
       HOUSEHOLD MONEY: CAN THE PEOPLE OF THIS CITY AFFORD TO LIVE IN IT?

       That is the whole question this page exists to answer, and the old one
       answered it with two nested tables and seven clicks. It had a row per pay
       tier, and behind each row a second table of the eleven household shapes
       inside that tier - so the answer, which is "an unskilled single parent
       cannot and an unskilled couple can", was seven disclosures deep and never
       visible in one place.

       The two tables were the same data cut twice. Household shape and pay tier
       are two axes of one grid, and every cell of it is already computable -
       HouseholdAccounts.statementFor(families, shape, tier) has been there the
       whole time. So the grid is the page: a row of household for every shape
       somebody lives in, six columns of tier, and in each cell what that
       household has left at the end of the month, tinted by how badly.

       WHY THE SHAPES ARE THE ROWS. There are eleven of them and six tiers, and
       a label like "Couple, a baby and a child" needs width a column cannot
       give it. Down the side it reads in full; across the top it would have had
       to be abbreviated into something nobody could parse.

       WHAT THE GRID SAYS THAT NEITHER TABLE COULD. Rent is one figure per front
       door and the shopping basket is one figure per head, so a row gets
       cheaper to the right as income climbs, and a column gets dearer downward
       as mouths are added. The diagonal where those two meet is the line
       between a household that saves and one that does not, and on the grid you
       can see exactly where it falls.

       A PAGE OF ITS OWN SINCE 0.7.27, "People › Household money", joined to
       People by the chip strip in both heads - it was a sector report under a
       bare button below the People page's scroller. The open cell's books sit
       BESIDE the grid at 1,200 px and wider, held at the top of the view as
       the page scrolls, so opening a cell never scrolls the grid away; under
       that they stack under the grid as before, and the page brings the two
       into view itself (revealTop, revealBottom). The tier heads carry the
       wage one earner of the tier was paid (HouseholdAccounts.wagePerEarner()).
       The verdict counts every row drawn, the retired and the people outside
       the families as well as the families (it said "every kind of household
       covers its month" over five red rows), and the rows below the rule are
       measured against a basket, Household.subsistence(), not against what a
       household with savings would like to spend. The city's month is a
       waterfall, with the bank's account fees a step of their own (the
       statement left them out and did not foot).
       ===================================================================== */

    /** Which cell of the grid is open, as "tier:shape", or "OUT:" and a ledger's key. Empty for none. */
    String openCell = "";

    /** Set by a click that opens a cell, so the panel it opened is brought into view. */
    boolean revealOpened = false;

    /** What this draw should bring into view, once, when the books stack under the grid: the grid's top and the panel's foot. */
    javafx.scene.Node revealTop = null;
    javafx.scene.Node revealBottom = null;

    /** The page's (i): what its money is counted in. */
    static final String IN_DOLLARS = "Every figure on this page is in dollars, not the thousands the rest of the "
            + "game counts in. What a household has left is its month: what came in, less the tax, the rent, the "
            + "fees and the shopping.";

    /** The order a household pays in, and what happens when it comes up short: the verdict's (i). */
    static final String PAID_IN_ORDER = "The order is fixed: the payslip first, then rent, then the fees, and "
            + "the shop takes what is left. A household short of the last one "
            + "spends its savings, then borrows — at a rate that climbs with what "
            + "it already owes — and only when both are gone does it eat less. "
            + "That last step is a health problem: hunger is in the sick rate.";

    /** The four money vitals: what they keep, what rent takes, who goes short, and the income behind it. */
    Cell[] moneyCells() {
        HouseholdAccounts hh = ui.game.getHouseholds();
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        double savingRate = hh.getSavingRate();
        double burden = hh.getRentBurden();
        double hunger = bal.getHungerRate();
        return new Cell[] {
            new Cell("THEY KEEP", String.format("%.1f%%", savingRate * 100), "of what they take home",
                    savingRate < 0 ? Palette.BAD : savingRate < .03 ? Palette.WARN : Palette.GOOD, null),
            new Cell("RENT TAKES", String.format("%.0f%%", burden * 100),
                    burden > .35 ? "over a third — rent-burdened" : "of take-home pay",
                    burden > .35 ? Palette.BAD : burden > .25 ? Palette.WARN : Palette.GOOD, null),
            new Cell("GOING SHORT", String.format("%.1f%%", hunger * 100), goingShortNote(),
                    goingShortTone(hunger), "The shops that feed them: Build › Shops"),
            // INCOME PER RESIDENT (0.7.27): PER WORKER read population over workforce under a
            // caption that said "people carried by each job" - neither of the People page's two.
            new Cell("INCOME PER RESIDENT", tightMoney(toDollars(hh.getIncomePerResident()), false),
                    "an average filled job pays " + tightMoney(toDollars(hh.getAverageWage()), false),
                    Palette.TEXT_HEAD, null),
        };
    }

    /**
     * The verdict over the grid (0.7.27), counting every row drawn: the
     * working households' cells by what their month leaves; the rows below
     * the rule - the retired, the out of work, the students, the orphans,
     * the prison - by whether they could plan a basket at all (red, going
     * hungry) or cover it only out of what they had put by or borrowed
     * (amber, living on savings).
     */
    record Verdict(String tone, String word, String words, String whole, int kinds, int shortKinds,
                   List<String> onSavings, List<String> hungry) { }

    Verdict verdict() {
        HouseholdAccounts hh = ui.game.getHouseholds();
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        FamilyModel families = ui.game.getFamilies();
        int kinds = 0, shortKinds = 0;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (shape.isRetired()) continue;
            for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
                HouseholdAccounts.Statement s = hh.statementFor(families, shape, PayTier.values()[t]);
                if (s.households() < .5) continue;
                kinds++;
                if (s.left() < 0) shortKinds++;
            }
        }
        List<String> savings = new ArrayList<>(), hungry = new ArrayList<>();
        for (Household own : belowTheRule(bal, families)) {
            if (rowLeft(own) >= 0) continue;
            String who = lowerFirst(own.label()) + " (" + people(own.people()) + ")";
            if (hungry(own)) hungry.add(who);
            else savings.add(who);
        }
        String words = shortKinds == 0
                ? String.format("All %d kinds of working household cover their month.", kinds)
                : String.format("%d of the %d kinds of working household cannot cover their month.", shortKinds, kinds);
        if (!savings.isEmpty()) words += " Living on their savings: " + String.join(", ", savings) + ".";
        if (!hungry.isEmpty()) words += " Going hungry: " + String.join(", ", hungry) + ".";
        String whole = String.format("Rent is %s a home whoever lives in it and the basket is %s a head, so what "
                + "separates one kind of household from another is earners against mouths.\n\n%s",
                tightMoney(toDollars(hh.rentPerHousehold()), false),
                tightMoney(toDollars(hh.shoppingPerHead()), false), PAID_IN_ORDER);
        String tone = shortKinds > 0 || !hungry.isEmpty() ? Palette.BAD
                : !savings.isEmpty() ? Palette.WARN : Palette.GOOD;
        String word = shortKinds > 0 ? "short" : !hungry.isEmpty() ? "hungry"
                : !savings.isEmpty() ? "on savings" : "covered";
        return new Verdict(tone, word, words, whole, kinds, shortKinds, savings, hungry);
    }

    /** "out of work, EI run out": a label in a sentence, its first letter only lowered - and not an acronym's. */
    static String lowerFirst(String s) {
        if (s == null || s.isEmpty()) return "";
        if (s.length() > 1 && Character.isUpperCase(s.charAt(1))) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * Going hungry, for a row below the rule: its own plan could not buy a
     * basket - the money half of the hunger, as getHungryAtFullShelves()
     * counts it, but for the meals eaten out, which that figure adds to the
     * plan and this does not. (Household.isGoingShort() is a plan short of what the
     * household WANTED, which for a pensioner with savings is a television,
     * not a meal.)
     */
    static boolean hungry(Household own) {
        return own.households() >= .5 && own.subsistence() > 0 && own.planned() < own.subsistence() * (1 - 1e-9);
    }

    /** What a month leaves one of a row below the rule, against a basket (0.7.27; against what it wanted before). */
    static double leftAgainstBasket(Household own) {
        return own.afterFixed() - own.subsistence();
    }

    /**
     * What the grid's one wide cell says a month leaves one of a row below
     * the rule, in thousands: the retired through their statement, as their
     * pension and their rent make one, and everybody else against a basket.
     * Short of it and still able to buy a basket is LIVING ON SAVINGS - what
     * they had put by, what they sold or what they borrowed paid for it.
     */
    double rowLeft(Household own) {
        if (own.shape() != null && own.shape().isRetired()) {
            return ui.game.getHouseholds().statementFor(ui.game.getFamilies(), own.shape(), PayTier.values()[0]).left();
        }
        return leftAgainstBasket(own);
    }

    /** The rows below the rule, in the grid's order: the retired, then everybody outside the families. */
    List<Household> belowTheRule(HouseholdBalance bal, FamilyModel families) {
        List<Household> out = new ArrayList<>();
        if (bal == null) return out;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (!shape.isRetired()) continue;
            Household cell = bal.cell(shape);
            if (cell != null && cell.households() >= .5) out.add(cell);
        }
        for (UnemployedHousehold.Status st : UnemployedHousehold.Status.values()) {
            Household h = bal.unemployed(st);
            if (h != null && h.households() >= .5) out.add(h);
        }
        if (bal.students() != null && bal.students().households() >= .5) out.add(bal.students());
        for (AgeBand b : AgeBand.values()) {
            Household o = bal.orphans(b);
            if (o != null && o.households() >= .5) out.add(o);
        }
        if (bal.prisoners() != null && bal.prisoners().households() >= .5) out.add(bal.prisoners());
        return out;
    }

    /** The grid's key for a cell: "tier:SHAPE" for a family, "OUT:" and its ledger's key for the rest. */
    static String gridKey(Household c) {
        if (c instanceof WorkingHousehold && c.tier() != null) return c.tier().ordinal() + ":" + c.shape().name();
        return "OUT:" + c.key();
    }

    /**
     * The city's month, as the waterfall's steps, in dollars: the wages, what
     * comes off them and what is added, take-home, what it is spent on - the
     * bank's account fees a step of their own (0.7.27; the statement left
     * them out, and its lines left $667,264 more than "Saved" in the
     * 2,400-month city: exactly the fees) - and what is left.
     */
    List<Pieces.Step> cityMonth() {
        HouseholdAccounts hh = ui.game.getHouseholds();
        TaxPolicy policy = ui.game.getEconomyManager().getTaxPolicy();
        List<Pieces.Step> s = new ArrayList<>();
        s.add(Pieces.Step.total("Wages", toDollars(hh.getWages()), Palette.PEOPLE).tip("Wages earned"));
        s.add(Pieces.Step.of("Wage tax", -toDollars(hh.getWageTax()), Palette.MONEY)
                .tip(String.format("Wage tax at %.0f%%", hh.getEffectiveTaxRate() * 100)));
        s.add(Pieces.Step.of("Contributions", -toDollars(hh.getContributions()), Palette.MONEY)
                .tip(String.format("Pension contributions at %.1f%%", policy.getContributionRate() * 100)));
        s.add(Pieces.Step.of("EI premiums", -toDollars(hh.getEiPremiums()), Palette.MONEY)
                .tip(String.format("EI premiums at %.2f%%", policy.getEiPremiumRate() * 100)));
        s.add(Pieces.Step.of("Health premium", -toDollars(hh.getHealthPremiums()), Palette.MONEY)
                .tip(String.format("Health premium at %.2f%%", policy.getHealthPremiumRate() * 100)));
        s.add(Pieces.Step.of("Pensions", toDollars(hh.getPensions()), Palette.MONEY_LIGHT).tip("Pensions received"));
        s.add(Pieces.Step.of("EI", toDollars(hh.getEiBenefits()), Palette.MONEY_LIGHT).tip("EI received"));
        s.add(Pieces.Step.of("Grants", toDollars(hh.getStudentGrants()), Palette.MONEY_LIGHT)
                .tip("Student grants received"));
        s.add(Pieces.Step.total("Take-home", toDollars(hh.getDisposableIncome()), Palette.PEOPLE));
        s.add(Pieces.Step.of("Rent", -toDollars(hh.getRent()), Palette.BUILDING).tip("Rent to landlords"));
        s.add(Pieces.Step.of("Healthcare", -toDollars(hh.getHealthcare()), Palette.BUSINESS).tip("Healthcare fees"));
        s.add(Pieces.Step.of("School fees", -toDollars(hh.getTuition()), Palette.BUSINESS));
        s.add(Pieces.Step.of("Fares", -toDollars(hh.getFares()), Palette.BUSINESS).tip("Transit fares"));
        s.add(Pieces.Step.of("Interest", -toDollars(hh.getInterest()), Palette.BUSINESS).tip("Interest on debt"));
        s.add(Pieces.Step.of("Bank fees", -toDollars(hh.getAccountFees()), Palette.BUSINESS)
                .tip("The bank's account fees"));
        s.add(Pieces.Step.of("Shops", -toDollars(hh.getShopping()), Palette.PEOPLE_LIGHT).tip("Spent in the shops"));
        double saved = hh.getNetSaving();
        s.add(Pieces.Step.total(hh.isLivingBeyondIncome() ? "Short by" : "Saved", toDollars(saved),
                saved < 0 ? Palette.BAD : Palette.GOOD));
        return s;
    }

    /** Money on the waterfall, signed and compact: "−$36.8M". */
    static String signedMoney(double dollars) {
        String t = tightMoney(Math.abs(dollars));
        return Math.abs(dollars) < .5 ? t : (dollars < 0 ? "−" : "+") + t;
    }

    /* =====================================================================
       HOUSEHOLD MONEY, DRAWN
       ===================================================================== */

    /** The narrowest page that sets the open cell's books beside the grid; under it they stack. */
    static final double BESIDE = 1200;

    /** The books' pane as last drawn, for the scroll to hold the panel at the top of the view. */
    private Books books;

    /** Whether the scroll listener is on the menu's scroller yet: once, for the window's life. */
    private boolean booksHooked = false;

    /**
     * The residents' own books - the last participant in this economy that did
     * not have any.
     *
     * REBUILT 2026-09-07 (Jerus: "that part is basically unreadable"), and as
     * a page in Build's style since 0.7.27: the head and its chips, four
     * vitals, the verdict, the grid with the open cell's books, the city's
     * month as a waterfall, and what the households have put by.
     */
    void showHouseholdMenu() {
        ui.clearMenu("showHouseholdMenu", () -> showHouseholdMenu());

        HouseholdAccounts hh = ui.game.getHouseholds();
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        FamilyModel families = ui.game.getFamilies();

        VBox page = widePage();
        page.getChildren().add(pageHead("People", Palette.PEOPLE, this::showPopulationInfoMenu,
                "Household money", IN_DOLLARS, pageChips(false)));

        Cell[] cells = moneyCells();
        VBox[] drawn = new VBox[cells.length];
        for (int i = 0; i < cells.length; i++) {
            Cell c = cells[i];
            drawn[i] = c.where() == null ? limitCell(c.label(), c.value(), c.note(), c.tone())
                    : limitCell(c.label(), c.value(), c.note(), c.tone(), c.where(),
                            () -> ui.buildScreen.openCategory(BuildAdvice.SHOPS));
        }
        VBox vitals = new VBox(vitalsBar(drawn));
        vitals.setAlignment(Pos.CENTER);
        page.getChildren().add(vitals);

        Verdict v = verdict();
        HBox verdictLine = new HBox(Palette.GAP, chip(v.word(), v.tone()),
                infoLine(v.words(), v.whole(), false, Palette.SIZE_BODY, Palette.TEXT_BODY, 760));
        verdictLine.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(verdictLine, Priority.ALWAYS);
        javafx.scene.layout.FlowPane views = chipStrip(new String[] {"One household", "All of them"},
                householdPerFamily ? "One household" : "All of them", Palette.SIZE_LABEL, name -> {
                    householdPerFamily = "One household".equals(name);
                    showHouseholdMenu();
                });
        views.setAlignment(Pos.CENTER_RIGHT);
        views.setPrefWrapLength(240);
        views.setMinWidth(Region.USE_PREF_SIZE);
        HBox verdictRow = new HBox(Palette.GAP_LOOSE, verdictLine, views);
        verdictRow.setAlignment(Pos.CENTER_LEFT);
        page.getChildren().add(verdictRow);

        VBox grid = affordabilityGrid(hh, bal, families);
        VBox opened = openStatement(hh, bal, families);
        javafx.scene.Node panel = opened != null ? opened : quietBooks();
        books = new Books(grid, panel);
        page.getChildren().add(books);
        hookBooks();
        if (revealOpened) {
            revealTop = grid;
            revealBottom = opened;
            revealOpened = false;
        }

        page.getChildren().addAll(
                sectionHead("THE CITY'S MONTH", hint("every household together, from the wages to what was saved")),
                cityMonthCard(hh),
                sectionHead("WHAT THEY HAVE PUT BY", hint("every household together")),
                putByCards(bal));
        page.getChildren().addAll(putByChips(bal, families));

        ui.rootMenu.getChildren().add(page);
        reveal();
    }

    /** With no cell open, the books' place says what goes there. */
    VBox quietBooks() {
        VBox c = card(4);
        c.getChildren().addAll(words("Click a cell to open its books", Palette.SIZE_BODY, Palette.TEXT_LABEL),
                words("What one household of that kind earned, paid and kept this month, what it has put by and "
                        + "owes, and where its money goes.", Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        return c;
    }

    /**
     * The grid and the open cell's books: side by side at BESIDE and wider -
     * the grid at its own width, the books in the rest, held at the top of
     * the view while the grid scrolls past - and the books under the grid
     * below that.
     */
    static final class Books extends javafx.scene.layout.Pane {
        private final VBox grid;
        private final javafx.scene.Node panel;

        Books(VBox grid, javafx.scene.Node panel) {
            this.grid = grid;
            this.panel = panel;
            getChildren().addAll(grid, panel);
        }

        boolean beside(double w) { return w >= BESIDE; }

        /** Its height follows its width: beside or stacked. */
        @Override public javafx.geometry.Orientation getContentBias() {
            return javafx.geometry.Orientation.HORIZONTAL;
        }

        @Override protected double computePrefHeight(double width) {
            double w = width > 0 ? width : getWidth();
            double gw = grid.prefWidth(-1);
            if (beside(w)) {
                return Math.max(grid.prefHeight(gw), panel.prefHeight(Math.max(0, w - gw - TILE_GAP)));
            }
            return grid.prefHeight(gw) + TILE_GAP + panel.prefHeight(Math.min(w, STATEMENT));
        }

        @Override protected double computeMinHeight(double width) { return computePrefHeight(width); }

        @Override protected double computePrefWidth(double height) { return grid.prefWidth(-1); }

        @Override protected void layoutChildren() {
            double w = getWidth();
            double gw = grid.prefWidth(-1);
            if (beside(w)) {
                double pw = Math.max(0, w - gw - TILE_GAP);
                double gh = grid.prefHeight(gw), ph = panel.prefHeight(pw);
                grid.resizeRelocate(0, 0, gw, gh);
                panel.resizeRelocate(gw + TILE_GAP, held(ph), pw, ph);
            } else {
                double gh = grid.prefHeight(gw);
                double pw = Math.min(w, STATEMENT);
                grid.resizeRelocate(0, 0, Math.min(w, gw), gh);
                panel.resizeRelocate(0, gh + TILE_GAP, pw, panel.prefHeight(pw));
            }
        }

        /** How far down the books sit so their top stays at the top of the view, within the grid's height. */
        private double held(double panelHeight) {
            if (getScene() == null) return 0;
            javafx.scene.control.ScrollPane scroller = scrollerOf(this);
            if (scroller == null || scroller.getContent() == null) return 0;
            javafx.scene.Node content = scroller.getContent();
            javafx.geometry.Bounds me = content.sceneToLocal(localToScene(getBoundsInLocal()));
            if (me == null) return 0;
            double view = scroller.getViewportBounds().getHeight();
            double span = content.getBoundsInLocal().getHeight() - view;
            double top = span > 0 ? scroller.getVvalue() * span : 0;
            double room = getHeight() - panelHeight;
            return Math.max(0, Math.min(room, top - me.getMinY() + 8));
        }

        /** The scroll pane this sits in - up through its skin's viewport - or null. */
        static javafx.scene.control.ScrollPane scrollerOf(javafx.scene.Node n) {
            for (javafx.scene.Node p = n.getParent(); p != null; p = p.getParent()) {
                if (p instanceof javafx.scene.control.ScrollPane s) return s;
            }
            return null;
        }
    }

    /** The menu's scroll moves the books with it: one listener, for the window's life, laying the books out again. */
    void hookBooks() {
        if (booksHooked || ui.menuScroller == null) return;
        booksHooked = true;
        javafx.beans.InvalidationListener relayout = o -> {
            if (books != null && books.getScene() != null && "showHouseholdMenu".equals(ui.currentScreen)) {
                books.requestLayout();
            }
        };
        ui.menuScroller.vvalueProperty().addListener(relayout);
        ui.menuScroller.viewportBoundsProperty().addListener(relayout);
    }

    /**
     * THE GRID AND ITS BOOKS ARE WHAT GETS REVEALED, when they stack: the top
     * of the grid to the top of the view and the panel landing under it, both
     * on screen at once - or, when they do not both fit, the panel's foot,
     * which is its total. One-shot, on the click that opened it, so a month
     * landing does not yank a page the player has scrolled. Beside the grid
     * nothing needs revealing: the books are held in view.
     */
    void reveal() {
        if (revealTop == null) return;
        javafx.scene.Node first = revealTop;
        javafx.scene.Node last = revealBottom == null ? revealTop : revealBottom;
        revealTop = null;
        revealBottom = null;
        javafx.scene.control.ScrollPane scroller = ui.menuScroller;
        if (scroller == null) return;
        javafx.application.Platform.runLater(() -> {
            scroller.applyCss();
            scroller.layout();
            if (books != null && books.beside(books.getWidth())) return;
            javafx.scene.Node content = scroller.getContent();
            if (content == null || first.getScene() == null || last.getScene() == null) return;
            double tall = content.getBoundsInLocal().getHeight();
            double view = scroller.getViewportBounds().getHeight();
            if (tall <= view) return;
            javafx.geometry.Bounds a = content.sceneToLocal(first.localToScene(first.getBoundsInLocal()));
            javafx.geometry.Bounds b = content.sceneToLocal(last.localToScene(last.getBoundsInLocal()));
            double want = Math.max(a.getMinY() - 8, b.getMaxY() + 8 - view);
            scroller.setVvalue(Math.max(0, Math.min(1, want / (tall - view))));
        });
    }

    /** THE CITY'S MONTH: the waterfall, what was saved since the founding, and the statement behind details. */
    VBox cityMonthCard(HouseholdAccounts hh) {
        Pieces.Waterfall fall = waterfall(cityMonth(), PeopleScreen::signedMoney, 0, 210);
        String end = hh.isLivingBeyondIncome()
                ? "Spending more than they earn — nothing in the model funds this."
                : String.format("%s saved since founding. A record, not a pot.",
                        tightMoney(toDollars(hh.getCumulativeSaving())));
        VBox c = card(6);
        c.getChildren().addAll(fall, words(end, Palette.SIZE_LABEL, Palette.TEXT_MUTED),
                details("statement", "the month as a statement", cityStatement(hh)));
        return c;
    }

    /** The city's month as the statement it was, with the bank's account fees on a line of their own (0.7.27). */
    VBox cityStatement(HouseholdAccounts hh) {
        TaxPolicy policy = ui.game.getEconomyManager().getTaxPolicy();
        VBox column = new VBox(0);
        column.getChildren().add(statementLine("Wages earned",
                tightMoney(toDollars(hh.getWages()), false)));
        column.getChildren().add(statementLine(
                String.format("Wage tax at %.0f%%", hh.getEffectiveTaxRate() * 100),
                tightMoney(toDollars(-hh.getWageTax()), false), Palette.WARN));
        column.getChildren().add(statementLine(
                String.format("Pension contributions at %.1f%%", policy.getContributionRate() * 100),
                tightMoney(toDollars(-hh.getContributions()), false), Palette.WARN));
        column.getChildren().add(statementLine("Pensions received",
                tightMoney(toDollars(hh.getPensions()), false), Palette.ACCENT));
        // EI and the grants (2026-09-11): off the same payslips, and in to the out of work and the students.
        column.getChildren().add(statementLine(
                String.format("EI premiums at %.2f%%", policy.getEiPremiumRate() * 100),
                tightMoney(toDollars(-hh.getEiPremiums()), false), Palette.WARN));
        // ...and the health premium (2026-09-19), off the same payslips.
        column.getChildren().add(statementLine(
                String.format("Health premium at %.2f%%", policy.getHealthPremiumRate() * 100),
                tightMoney(toDollars(-hh.getHealthPremiums()), false), Palette.WARN));
        column.getChildren().add(statementLine("EI received",
                tightMoney(toDollars(hh.getEiBenefits()), false), Palette.ACCENT));
        column.getChildren().add(statementLine("Student grants received",
                tightMoney(toDollars(hh.getStudentGrants()), false), Palette.ACCENT));
        column.getChildren().add(statementTotal("Take-home",
                tightMoney(toDollars(hh.getDisposableIncome()), false), null));
        column.getChildren().add(statementLine("Rent to landlords",
                tightMoney(toDollars(-hh.getRent()), false)));
        column.getChildren().add(statementLine("Healthcare fees",
                tightMoney(toDollars(-hh.getHealthcare()), false)));
        column.getChildren().add(statementLine("School fees",
                tightMoney(toDollars(-hh.getTuition()), false)));
        // The third fee, and the one the city used to collect from nobody. See HouseholdAccounts.fares.
        column.getChildren().add(statementLine("Transit fares",
                tightMoney(toDollars(-hh.getFares()), false)));
        column.getChildren().add(statementLine("Interest on debt",
                tightMoney(toDollars(-hh.getInterest()), false)));
        // ...and the bank's account fee (0.7.7), which getSpending() always counted and this never printed.
        column.getChildren().add(statementLine("The bank's account fees",
                tightMoney(toDollars(-hh.getAccountFees()), false)));
        column.getChildren().add(statementLine("Spent in the shops",
                tightMoney(toDollars(-hh.getShopping()), false)));
        double saved = hh.getNetSaving();
        column.getChildren().add(statementTotal(
                hh.isLivingBeyondIncome() ? "Short by" : "Saved",
                tightMoney(toDollars(saved), false),
                saved < 0 ? Palette.BAD : Palette.GOOD));
        return column;
    }

    /** The cars' (i): the old note. */
    static final String CARS_INFO = "Bought out of savings past the same cushion a share is, so income "
            + "decides who can afford one - and a city with transit that could "
            + "carry everybody sees half of its households never bother. What it "
            + "costs the road is on Infrastructure.";

    /** A small figure card: a caption, the figure, a line, and its (i). */
    static VBox statCard(String caption, String value, String tone, String line, String info) {
        VBox c = card(2);
        c.getChildren().addAll(cardHead(caption, info), figure(value, Palette.SIZE_SECTION, tone),
                words(line, Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        return c;
    }

    /** WHAT THEY HAVE PUT BY: saved, owed, the month's dividends, and the cars. */
    javafx.scene.layout.GridPane putByCards(HouseholdBalance bal) {
        javafx.scene.layout.GridPane row = equalColumns(4, TILE_GAP);
        double saved = bal.totalSavings(), owed = bal.totalDebt(), dividends = bal.totalDividends();
        row.add(statCard("SAVED", tightMoney(toDollars(saved), false), saved > 0 ? Palette.GOOD : Palette.TEXT_HEAD,
                "put by, every household together", null), 0, 0);
        row.add(statCard("OWED", tightMoney(toDollars(owed), false), owed > 0 ? Palette.BAD : Palette.TEXT_HEAD,
                "to lenders · interest " + tightMoney(toDollars(bal.totalInterest()), false) + " this month", null), 1, 0);
        row.add(statCard("DIVIDENDS THIS MONTH", tightMoney(toDollars(dividends), false),
                dividends > 0 ? Palette.GOOD : Palette.TEXT_HEAD,
                "over the city's life " + tightMoney(toDollars(ui.game.getEquity().getLifetimeDividendsHome())),
                null), 2, 0);
        double bought = ui.game.getHouseholdCarsBought();
        row.add(statCard("CARS", formatter.format(Math.round(bal.totalCars())), Palette.TEXT_HEAD,
                String.format("%.0f%% of households", bal.carsPerHousehold() * 100)
                        + (bought > 0 ? " · " + formatter.format(Math.round(bought)) + " bought this month for "
                                + tightMoney(toDollars(ui.game.getHouseholdCarSpend()), false) : ""),
                CARS_INFO), 3, 0);
        return row;
    }

    /** The two chips under the cards: who is at the credit ceiling, and who flatshares because one wage won't cover a home - each opens the cell. */
    List<javafx.scene.Node> putByChips(HouseholdBalance bal, FamilyModel families) {
        List<javafx.scene.Node> out = new ArrayList<>();
        javafx.scene.layout.FlowPane chips = new javafx.scene.layout.FlowPane(Palette.GAP, Palette.GAP_TIGHT);
        // Every cell keeps its own books, so this can be said by household rather than by tier:
        // who the bank has stopped lending to, and who has been discharged and is waiting out the year.
        double cutOff = 0, lockedOut = 0;
        Household worst = null;
        for (Household c : bal.cells()) {
            if (c.isLockedOut()) lockedOut += c.households();
            else if (c.isCutOff()) cutOff += c.households();
            if ((c.isLockedOut() || c.isCutOff()) && c.households() >= .5
                    && (worst == null || c.households() > worst.households())) worst = c;
        }
        if (cutOff + lockedOut >= .5) {
            Household open = worst;
            chips.getChildren().add(stepChip(String.format("%s at their credit ceiling · %s discharged ›",
                    formatter.format(Math.round(cutOff)), formatter.format(Math.round(lockedOut))),
                    () -> toBooks(open == null ? null : gridKey(open)), true));
        }
        double priced = families == null ? 0 : families.getPricedOutShares();
        if (priced >= .5) {
            int biggest = 0;
            for (PayTier t : PayTier.values()) {
                if (families.get(FamilyStructure.SHARED_ADULTS, t) > families.get(FamilyStructure.SHARED_ADULTS,
                        PayTier.values()[biggest])) biggest = t.ordinal();
            }
            String key = biggest + ":" + FamilyStructure.SHARED_ADULTS.name();
            chips.getChildren().add(stepChip(String.format("%s flatsharing because one wage won't cover a home ›",
                    formatter.format(Math.round(priced))), () -> toBooks(key), true));
        }
        if (!chips.getChildren().isEmpty()) out.add(chips);
        return out;
    }

    /** How wide a tier column is. Six of them, plus the household name. */
    static final double TIER_COL = 88;
    static final double SHAPE_COL = 168;

    /**
     * Household shape down the side, pay tier across the top, and in every cell
     * what that household has left at the end of the month.
     */
    VBox affordabilityGrid(HouseholdAccounts hh, HouseholdBalance bal,
                                   FamilyModel families) {

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(3);
        grid.setVgap(3);

        javafx.scene.layout.ColumnConstraints nameCol =
                new javafx.scene.layout.ColumnConstraints(SHAPE_COL);
        nameCol.setHalignment(javafx.geometry.HPos.LEFT);
        grid.getColumnConstraints().add(nameCol);
        for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
            javafx.scene.layout.ColumnConstraints spec =
                    new javafx.scene.layout.ColumnConstraints(TIER_COL);
            spec.setHalignment(javafx.geometry.HPos.RIGHT);
            grid.getColumnConstraints().add(spec);
        }

        grid.add(gridCell("household", Palette.TEXT_LABEL, Palette.SIZE_CAPTION, false), 0, 0);
        for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
            Label head = gridCell(hh.getRowLabel(t), Palette.TEXT_LABEL,
                    Palette.SIZE_CAPTION, true);
            head.setWrapText(true);
            /*
             * A WRAPPED LABEL ALIGNS ITS BOX, NOT ITS LINES. gridCell sets
             * CENTER_RIGHT, which puts the label at the right of its column;
             * the text inside it still ran left, so "Senior professional"
             * broke over two lines flush against the column's LEFT edge and
             * read as though it had collided with "Professional" next door.
             */
            head.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
            /*
             * ...AND UNDER IT, WHAT ONE EARNER OF THE TIER WAS PAID (0.7.27):
             * the wage the column's households live on, the same figure
             * statementFor() divides each cell's wages from.
             */
            double paid = hh.wagePerEarner(families, PayTier.values()[t]);
            Label wage = gridCell(paid > 0 ? tightMoney(toDollars(paid), false) + " a wage" : "no earners",
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true);
            VBox heads = new VBox(0, head, wage);
            heads.setAlignment(Pos.BOTTOM_RIGHT);
            grid.add(heads, t + 1, 0);
        }

        int line = 1;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (shape.isRetired()) continue;

            // A shape nobody in the city lives in is not a row worth a line.
            boolean lived = false;
            for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
                if (hh.statementFor(families, shape, PayTier.values()[t]).households() >= .5) {
                    lived = true;
                }
            }
            if (!lived) continue;

            grid.add(gridCell(shape.getLabel(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);

            for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
                grid.add(cashCell(hh, families, shape, PayTier.values()[t], t), t + 1, line);
            }
            line++;
        }

        /* THE TIER ITSELF, on the bottom line - the average of the column above
         * it, which is the household nobody actually lives in and the figure the
         * old screen led with. Kept, and kept last. */
        grid.add(gridCell("every household of that tier", Palette.TEXT_LABEL,
                Palette.SIZE_CAPTION, false), 0, line);
        for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
            double homes = hh.getRowHouseholds(t);
            double per = householdPerFamily && homes >= .5 ? homes : 1;
            double left = toDollars(hh.getRowSaving(t)) / per;
            Label cell = gridCell(homes < .5 ? "—" : tightMoney(left, !householdPerFamily),
                    homes < .5 ? Palette.TEXT_LABEL
                            : left < 0 ? Palette.BAD : Palette.GOOD,
                    Palette.SIZE_CAPTION, true);
            cell.setStyle(cell.getStyle() + " -fx-padding: 4 4 4 4;"
                    + (bal.isGoingShort(t) ? " -fx-border-color: " + Palette.ALERT_EDGE
                            + "; -fx-border-width: 0 0 2 0;" : ""));
            grid.add(cell, t + 1, line);
        }
        line++;

        /*
         * ...AND EVERYBODY WHO HAS NO PAY TIER, IN THE SAME GRID.
         *
         * Jerus: "the others should be in the matrix in some way, not on their
         * own thing in the bottom." They were two more tables underneath this
         * one, each with its own columns in its own widths, so the retired and
         * the out of work read as a different kind of citizen from a couple
         * with a child - which they are not. What they have not got is a PAY
         * TIER, and a pay tier is the only thing the six columns above say.
         *
         * So the row keeps the shape column and gives up the tier columns: one
         * wide cell across all six, tinted and clickable on exactly cashCell's
         * rule, carrying how many of them there are and what a month leaves
         * one of them.
         */
        line = otherRows(grid, hh, bal, families, line);

        VBox box = new VBox(grid);
        box.setStyle("-fx-padding: 2 0 8 0;");
        return box;
    }

    /** A rule and a caption across the matrix, with the (i) that says who is below it: a different kind of household. */
    int gridSectionRow(javafx.scene.layout.GridPane grid, String caption, int line) {
        Label head = new Label(caption);
        head.setWrapText(true);
        head.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        HBox row = new HBox(Palette.GAP_TIGHT, head, infoButton(BELOW_THE_RULE, false));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setStyle("-fx-padding: 14 0 3 0;"
                + " -fx-border-color: " + Palette.EDGE + " transparent transparent transparent;"
                + " -fx-border-width: 1 0 0 0;");
        grid.add(row, 0, line);
        javafx.scene.layout.GridPane.setColumnSpan(row, HouseholdAccounts.RETIRED + 1);
        return line + 1;
    }

    /** The section captions' (i): who is below the rule, and what their one cell says. */
    static final String BELOW_THE_RULE = "Below the rule: the retired, on a pension and no wage; the out of work, "
            + "on EI for twelve months and then on what they saved and what the bank will lend; students, on a "
            + "grant, their savings and a student loan that never runs out; the orphans, who have nothing; and "
            + "the prison, which feeds its own. None of them has a pay tier, so none of them has a row of six. "
            + "The retired's one cell is what their pension leaves after the rent, the fees and the shop a head, "
            + "worked out as a family's cell is; everybody else's is what a month leaves them against a full "
            + "basket. Green when something is left, grey when exactly nothing is, amber when only their savings "
            + "or a loan cover the gap, red when they could not buy a basket at all.";

    /**
     * The retired, the out of work, the students, the orphans and the prison,
     * as rows of the same matrix.
     *
     * Both kinds are drawn from the same two things - a Household ledger and
     * what a month leaves it - so neither can drift from the other. The retired
     * reach their figure through HouseholdAccounts.statementFor(), because a
     * pension and a rent make a statement; everybody else reaches it through
     * their own books, because they have no shape to derive one from.
     */
    int otherRows(javafx.scene.layout.GridPane grid, HouseholdAccounts hh,
                          HouseholdBalance bal, FamilyModel families, int line) {

        if (bal == null) return line;

        /* ------------------------------ the retired ------------------------------ */
        boolean headed = false;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (!shape.isRetired()) continue;
            HouseholdAccounts.Statement s =
                    hh.statementFor(families, shape, PayTier.values()[0]);
            if (s.households() < .5) continue;
            if (!headed) {
                line = gridSectionRow(grid, "the retired · no tier, and a pension of "
                        + tightMoney(toDollars(hh.getPensionPerSenior()), false)
                        + " a head", line);
                headed = true;
            }
            line = otherRow(grid, shape.getLabel(), bal.cell(shape),
                    toDollars(s.left()), s.households(), line);
        }

        /* ------------------------ and outside the families ------------------------ */
        java.util.List<Household> rest = new java.util.ArrayList<>();
        for (UnemployedHousehold.Status st : UnemployedHousehold.Status.values()) {
            rest.add(bal.unemployed(st));
        }
        rest.add(bal.students());
        for (AgeBand b : AgeBand.values()) if (bal.orphans(b) != null) rest.add(bal.orphans(b));
        // The prisoners kept their own books from the day they were built and
        // no grid ever listed them. Out of the labour force, debts frozen.
        if (bal.prisoners() != null) rest.add(bal.prisoners());

        headed = false;
        for (Household own : rest) {
            if (own == null || own.households() < .5) continue;
            if (!headed) {
                line = gridSectionRow(grid,
                        "outside the families · no tier, and no wage to have one", line);
                headed = true;
            }
            /*
             * MEASURED AGAINST A FULL BASKET rather than against what they ate.
             * Orphans have no income, no bills and nothing put by: their month
             * nets to exactly zero, and a column of zeroes would paint starving
             * babies the same colour as a household that broke even. What they
             * are short of is the FOOD.
             *
             * THE BASKET IS subsistence() (0.7.27). It read want(), which is
             * what a household with savings would like to spend - one person
             * on EI wanted $5,936 against a $399 basket - and is how the
             * students reached -$29,448 in Jerus's city.
             */
            line = otherRow(grid, own.label(), own,
                    toDollars(leftAgainstBasket(own)), own.households(), line);
        }
        return line;
    }

    /** One matrix row for a household with no tier: a name, then one wide cell. */
    int otherRow(javafx.scene.layout.GridPane grid, String label, Household own,
                         double perHousehold, double homes, int line) {

        boolean trouble = own.isCutOff() || hungry(own);
        grid.add(gridCell(label, trouble ? Palette.BAD_SOFT : Palette.TEXT_BODY,
                Palette.SIZE_CAPTION, false), 0, line);

        javafx.scene.Node cell = wideCell(own, perHousehold, homes);
        grid.add(cell, 1, line);
        javafx.scene.layout.GridPane.setColumnSpan(cell, HouseholdAccounts.RETIRED);
        return line + 1;
    }

    /**
     * cashCell's cell, one row wide: the same tint, the same ring, the same
     * click - with the headcount inside it, because the six tier columns it
     * spans have nothing of their own to say about this household.
     *
     * A ZERO IS NOT A SURPLUS. The prison feeds its own, so its basket is zero
     * and its month nets to exactly nothing; painting that the same green as a
     * couple banking $7,000 was the old grid's worst line. Nothing-to-say gets
     * a grey wash and a grey figure.
     *
     * AND SHORT IS TWO THINGS (0.7.27): a month that leaves less than a
     * basket and a household that still bought one - out of savings, shares
     * or a loan - is amber, living on savings; red only when they could not
     * buy the basket at all (hungry()). The verdict over the grid says the
     * same of each row (verdict(), rowLeft()). The retired's figure is their
     * statement's, as the grid always drew it, not one against a basket; the
     * colours read it the same way.
     */
    javafx.scene.Node wideCell(Household own, double perHousehold, double homes) {

        double shown = perHousehold * (householdPerFamily ? 1 : Math.max(1, homes));
        double base = Math.max(toDollars(own.subsistence()),
                Math.max(toDollars(own.disposable()), 1));
        double severity = Math.min(1, Math.abs(perHousehold) / base);

        boolean quiet = Math.abs(shown) < .005;
        boolean amber = shown < 0 && !hungry(own);
        String tone = quiet ? Palette.TEXT_MUTED : shown >= 0 ? Palette.GOOD : amber ? Palette.WARN : Palette.BAD;
        String wash = quiet ? "rgba(255,255,255,0.04)"
                : "rgba(" + (shown >= 0 ? "95,214,138," : amber ? "227,179,65," : "255,107,107,")
                        + String.format("%.2f", .08 + severity * .30) + ")";

        String key = "OUT:" + own.key();
        boolean open = key.equals(openCell);

        Label many = new Label(shortNumber(homes) + " of them");
        many.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label figure = new Label(tightMoney(shown, !householdPerFamily));
        figure.setStyle(Palette.figure(Palette.SIZE_CAPTION, tone));

        HBox box = new HBox(8, many, gap, figure);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle("-fx-padding: 4 6 4 6; -fx-cursor: hand;"
                + " -fx-background-radius: " + Palette.RADIUS_TIGHT + ";"
                + " -fx-background-color: " + wash + ";"
                + (open ? " -fx-border-color: " + Palette.ACCENT
                        + "; -fx-border-radius: " + Palette.RADIUS_TIGHT + ";" : ""));

        Tooltip tip = new Tooltip(own.label()
                + "\n" + people(own.households()) + " of them"
                + (own.shape() != null && own.shape().isRetired()
                        ? "\nwhat the pension leaves one of them, after the rent, the fees and the shop a head"
                        : "\nwhat a month leaves one of them, against a full basket")
                + "\nclick for the month");
        tip.setShowDelay(Duration.millis(300));
        Tooltip.install(box, tip);

        box.setOnMouseClicked(e -> {
            openCell = open ? "" : key;
            revealOpened = !openCell.isEmpty();
            showHouseholdMenu();
        });
        return box;
    }

    /**
     * One cell: what this household has left, and how badly.
     *
     * TINTED BY SEVERITY rather than just coloured by sign. A household $12
     * short and a household $1,300 short are both in the red and only one of
     * them is a crisis; a wash whose strength follows the shortfall against the
     * income says which without another column of figures.
     */
    Label cashCell(HouseholdAccounts hh, FamilyModel families,
                           FamilyStructure shape, PayTier tier, int tierIndex) {

        HouseholdAccounts.Statement s = hh.statementFor(families, shape, tier);

        if (s.households() < .5) {
            Label none = gridCell("—", Palette.TEXT_LABEL, Palette.SIZE_CAPTION, true);
            none.setStyle(none.getStyle() + " -fx-padding: 4 4 4 4;");
            return none;
        }

        // statementFor gives ONE household of this shape and tier; multiplied up
        // when the player asked for the whole city rather than one family.
        double left = toDollars(s.left()) * (householdPerFamily ? 1 : s.households());
        double severity = s.income() > 0
                ? Math.min(1, Math.abs(s.left()) / Math.abs(s.income())) : 0;
        double alpha = .08 + severity * .30;

        String key = tierIndex + ":" + shape.name();
        boolean open = key.equals(openCell);

        Label cell = gridCell(tightMoney(left, !householdPerFamily),
                s.left() < 0 ? Palette.BAD : Palette.GOOD, Palette.SIZE_CAPTION, true);
        cell.setStyle(cell.getStyle()
                + " -fx-padding: 4 4 4 4; -fx-cursor: hand;"
                + " -fx-background-radius: " + Palette.RADIUS_TIGHT + ";"
                + " -fx-background-color: rgba("
                + (s.left() < 0 ? "255,107,107," : "95,214,138,")
                + String.format("%.2f", alpha) + ");"
                + (open ? " -fx-border-color: " + Palette.ACCENT
                        + "; -fx-border-radius: " + Palette.RADIUS_TIGHT + ";" : ""));

        Tooltip tip = new Tooltip(shape.getLabel() + ", " + hh.getRowLabel(tierIndex)
                + "\n" + formatter.format(Math.round(s.households())) + " of them"
                + "\nclick for the month");
        tip.setShowDelay(Duration.millis(300));
        Tooltip.install(cell, tip);

        cell.setOnMouseClicked(e -> {
            openCell = open ? "" : key;
            revealOpened = !openCell.isEmpty();
            showHouseholdMenu();
        });
        return cell;
    }

    /**
     * What a full basket would have been, what they ate, and where the
     * difference came from.
     *
     * SHARED BY BOTH PANELS since the families asked for it too. Every cell in
     * the game carries this - `want`, `planned`, `unfunded`, `drawn`,
     * `borrowed`, `banked` - written by the month as it happened rather than
     * derived afterwards from a shape and a tier, and until now only the
     * families' statement was drawn at all and it did not include any of it.
     * A household that ate its full basket out of its savings and one that ate
     * two thirds of it looked identical.
     */
    VBox basketBlock(Household cell) {

        VBox block = new VBox(0);
        double wanted = toDollars(cell.want());
        double spent = toDollars(cell.planned());
        if (wanted <= .5 && spent <= .5) return block;

        Label head = new Label("The shopping, and how it was paid for");
        head.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL)
                + " -fx-padding: 10 0 2 0;");
        block.getChildren().add(head);

        block.getChildren().add(statementLine("A full basket would be",
                tightMoney(wanted, false), Palette.TEXT_MUTED));
        block.getChildren().add(statementLine("What they actually ate",
                tightMoney(spent, false),
                spent < wanted - .5 ? Palette.BAD : Palette.GOOD));

        double shortBy = toDollars(cell.unfunded());
        if (shortBy > .5) {
            block.getChildren().add(statementLine("Could not fund",
                    tightMoney(shortBy, false), Palette.BAD));
        }
        if (spent < wanted - .5 && shortBy <= .5) {
            block.getChildren().add(statementNote(
                    "Nothing funds the difference: there is no income, nothing put by and "
                    + "nobody to borrow from, so the basket is simply not bought."));
        }

        double drawn = toDollars(cell.drawn());
        double borrowed = toDollars(cell.borrowed());
        double banked = toDollars(cell.banked());
        if (drawn > .5) {
            block.getChildren().add(statementLine("Drawn from savings",
                    tightMoney(drawn, false), Palette.WARN));
        }
        if (borrowed > .5) {
            block.getChildren().add(statementLine("Borrowed",
                    tightMoney(borrowed, false), Palette.BAD));
        }
        if (banked > .5) {
            block.getChildren().add(statementLine("Put by",
                    tightMoney(banked, false), Palette.GOOD));
        }
        return block;
    }

    /* =====================================================================
       THE MONEY BLOCKS BOTH PANELS SHARE

       Jerus: "i also want a better panel (the financial stuff) thats what i
       mean by more detailed, i want to expand that further, its great, but
       more."

       The month was six lines and the position was four. Everything added
       below was already in the model and had never been drawn: what the bank
       will still lend, what was borrowed and paid off this month, what a
       student loan has left to run, how long savings cover a shortfall, and
       what share of a pay packet each claim on it takes.

       WRITTEN ONCE AND CALLED TWICE on purpose. Two panels that have to agree
       agree because they are the same code, not because somebody kept them in
       step - the rule the rent-units bug taught this codebase.
       ===================================================================== */

    /** A cost, printed negative - but a cost of nothing reads "$0", never "-$0". */
    String costMoney(double dollars) {
        double a = Math.abs(dollars);
        return a < .005 ? "$0" : tightMoney(-a, false);
    }

    /** A stake, with enough decimals left on it to still say something. */
    static String stakePct(double share) {
        double p = share * 100;
        if (p >= 1)   return String.format("%.1f%%", p);
        if (p >= .1)  return String.format("%.2f%%", p);
        if (p >= .01) return String.format("%.3f%%", p);
        return String.format("%.4f%%", p);
    }

    /**
     * What part is of whole - or a dash, when there is no whole to be part of.
     *
     * A SHARE THAT ROUNDS TO NOTHING SAYS SO IN WORDS. 1,361 out-of-work
     * households in a city of half a million printed "0% of the city's
     * households", which reads as none of them rather than as very few.
     */
    static String shareOf(double part, double whole) {
        if (Math.abs(whole) < 1e-9) return "—";
        double p = part / whole * 100;
        if (p > 0 && p < .05) return "under 1%";
        if (Math.abs(p) < 10)  return String.format("%.1f%%", p);
        return String.format("%.0f%%", p);
    }

    /** A run of months, said the way a person would say it. */
    static String monthsRun(double months) {
        if (!Double.isFinite(months) || months >= 600) return "longer than a lifetime";
        if (months >= 24) return String.format("%.0f years", months / 12);
        if (months < 1)   return "less than a month";
        return String.format("%.0f months", months);
    }

    /** The small heading that divides a statement panel into blocks. */
    Label panelBlockHead(String text) {
        Label head = new Label(text);
        head.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL)
                + " -fx-padding: 12 0 2 0;");
        return head;
    }

    /** How many of them there are, how big each one is, and how much of the city that is. */
    Label panelWho(HouseholdBalance bal, Household cell) {
        double allHomes = 0, allPeople = 0;
        for (Household c : bal.cells()) {
            allHomes += c.households();
            allPeople += c.people();
        }
        Label many = new Label(people(cell.households()) + " of them, "
                + (cell.size() == 1 ? "one person each" : cell.size() + " people each")
                + "  ·  " + shareOf(cell.households(), allHomes)
                + " of the city's households, "
                + shareOf(cell.people(), allPeople) + " of its people");
        many.setWrapText(true);
        many.setMaxWidth(STATEMENT - 28);
        many.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)
                + " -fx-padding: 0 0 6 0;");
        return many;
    }

    /**
     * What one of these households HAS, what it OWES, what it OWNS and what
     * all of that leaves it worth.
     *
     * The month above is a flow. This is the stock behind it, and it is the
     * half that decides whether a bad month is survivable: a household short
     * $400 with six months of credit left and one at the ceiling are the same
     * red figure and completely different situations.
     */
    void positionBlock(VBox panel, HouseholdBalance bal, Household own) {

        panel.getChildren().add(panelBlockHead("What one of them has"));

        double put = toDollars(own.savings());
        double owe = toDollars(own.debt());

        panel.getChildren().add(statementLine("Put by", tightMoney(put, false),
                own.savings() > 0 ? Palette.GOOD : null));
        double moved = toDollars(own.banked() - own.drawn());
        if (Math.abs(moved) > .005) {
            panel.getChildren().add(statementLine(
                    moved > 0 ? "...added this month" : "...drawn down this month",
                    tightMoney(Math.abs(moved), false),
                    moved > 0 ? Palette.GOOD : Palette.WARN));
        }

        panel.getChildren().add(statementLine("Owed to lenders", costMoney(owe),
                own.debt() > 0 ? Palette.BAD : null));
        if (own.debt() > 0) {
            panel.getChildren().add(statementLine(
                    String.format("Interest at %.1f%% a year", own.rate() * 100),
                    costMoney(toDollars(own.interest()))));
        }
        if (own.borrowed() > .005) {
            panel.getChildren().add(statementLine("...borrowed this month",
                    tightMoney(toDollars(own.borrowed()), false), Palette.BAD));
        }
        if (own.repaid() > .005) {
            panel.getChildren().add(statementLine("...paid off this month",
                    tightMoney(toDollars(own.repaid()), false), Palette.GOOD));
        }
        if (own.bankrupt() > .005) {
            panel.getChildren().add(statementLine("...written off this month",
                    tightMoney(toDollars(own.bankrupt()), false), Palette.BAD));
        }

        /*
         * WHAT THE BANK WILL STILL LEND, which is the figure that decides
         * whether a short month is survivable or is the month they stop eating.
         * Read from Household.creditRoom - the same call the month itself
         * makes, so the screen shows the number the model used rather than a
         * second opinion about it.
         */
        double room = toDollars(own.creditRoom(own.disposable()));
        panel.getChildren().add(statementLine("Room still to borrow",
                own.isLockedOut() ? "none - locked out" : tightMoney(room, false),
                own.isLockedOut() ? Palette.BAD : room > .5 ? null : Palette.WARN));

        if (own.studentDebt() > 0) {
            panel.getChildren().add(statementLine("Student loan outstanding",
                    costMoney(toDollars(own.studentDebt())), Palette.WARN));
            if (own.studentBorrowed() > .005) {
                panel.getChildren().add(statementLine("...drawn this month",
                        tightMoney(toDollars(own.studentBorrowed()), false), Palette.WARN));
            }
            if (own.studentRepaid() > .005) {
                panel.getChildren().add(statementLine("...repaid this month",
                        tightMoney(toDollars(own.studentRepaid()), false), Palette.GOOD));
                // ...and the interest on top of it (2026-09-21), when the
                // treasury charges any: the Schools page's dial.
                if (own.studentInterest() > .005) {
                    panel.getChildren().add(statementLine("...and interest on it",
                            tightMoney(toDollars(own.studentInterest()), false), Palette.WARN));
                }
                panel.getChildren().add(statementNote("At this month's rate it has "
                        + monthsRun(own.studentDebt() / own.studentRepaid())
                        + " left to run."));
            } else {
                panel.getChildren().add(statementNote(
                        "Nothing is coming off it: a student loan is only collected out of a wage."));
            }
        }

        /*
         * AND WHAT IT OWNS. Shares in the city's companies - bought at
         * offerings out of what was past the cushion, or held since the
         * founding. At the price - the last trade on each company's book, or
         * fair value before its first (0.7.12 round 2; the desk's quote until
         * then, and book with no bank).
         */
        Equity register = ui.game.getEquity();
        Exchange exchange = ui.game.getExchange();
        double shareWorth = 0;
        int held = 0;
        StringBuilder holdings = new StringBuilder();
        StringBuilder names = new StringBuilder();
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (own.shares(c) <= 0 || register.getShares(c) <= 0) continue;
            double stake = own.shares(c) / register.getShares(c);
            shareWorth += own.shares(c) * exchange.price(c);
            held++;
            if (holdings.length() > 0) holdings.append(", ");
            holdings.append(Equity.COMPANIES[c]).append(' ').append(stakePct(stake));
            if (names.length() > 0) names.append(", ");
            names.append(Equity.COMPANIES[c]);
        }
        double shares = toDollars(shareWorth);
        if (held > 0) {
            panel.getChildren().add(statementLine(
                    "Shares, at the last trade",
                    tightMoney(shares, false), Palette.GOOD));
            panel.getChildren().add(statementLine("Dividends this month",
                    tightMoney(toDollars(own.dividends()), false),
                    own.dividends() > 0 ? Palette.GOOD : null));
            if (own.sold() > 0) {
                panel.getChildren().add(statementLine("Sold to cover the month",
                        tightMoney(toDollars(own.sold()), false), Palette.WARN));
            }
            /*
             * ELEVEN SECTORS AT THE SAME STAKE IS NOT ELEVEN FACTS. A household
             * that bought at every offering owns the same sliver of everything,
             * and the line that listed them printed that sliver eleven times -
             * a wall of "0.0000%", which is the one thing a percentage cannot
             * usefully carry. What it is worth is already on the line above;
             * what this line is for is WHICH companies, so once there are more
             * than a couple of them that is all it says.
             */
            panel.getChildren().add(statementNote(
                    held >= Equity.COMPANIES.length
                            ? "A slice of every company in the city."
                            : held >= 3
                                    ? "A slice of " + held + " of the city's companies: "
                                            + names + "."
                                    : "Owns " + holdings + "."));
        }

        /* ...AND WHAT IT KEEPS ABROAD: the world's paper, bought when the world
         * paid more than the bank. See HouseholdBalance.investAbroad(). */
        double away = 0;
        if (own.abroad() > 0) {
            away = toDollars(own.abroadValue(bal.getExchangeRate()));
            panel.getChildren().add(statementLine("Kept abroad",
                    tightMoney(away, false)
                            + " (US" + tightMoney(toDollars(own.abroad()), false) + ")",
                    Palette.GOOD));
            if (own.sentAbroad() > 0) {
                panel.getChildren().add(statementLine("...sent out this month",
                        tightMoney(toDollars(own.sentAbroad()), false)));
            }
            if (own.broughtHome() > 0) {
                panel.getChildren().add(statementLine("...brought home this month",
                        tightMoney(toDollars(own.broughtHome()), false)));
            }
            if (own.foreignInterest() > 0) {
                panel.getChildren().add(statementLine("...interest earned out there",
                        tightMoney(toDollars(own.foreignInterest()), false), Palette.GOOD));
            }
        }

        /* ...AND THE CITY'S OWN PAPER (0.7.1): bought when an issue paid more than
         * the bank, at this month's value - the households' book at the curve
         * over its face. See HouseholdBalance's THE CITY'S PAPER, AT HOME. */
        double bonds = 0;
        if (own.paper() > 0) {
            bonds = toDollars(own.paper() * bal.getPaperRatio());
            panel.getChildren().add(statementLine("The city's own paper",
                    tightMoney(bonds, false) + " (" + tightMoney(toDollars(own.paper()), false)
                            + " of face)", Palette.GOOD));
            if (own.paperIncome() > 0) {
                panel.getChildren().add(statementLine("...paid on it this month",
                        tightMoney(toDollars(own.paperIncome()), false), Palette.GOOD));
            }
            if (own.paperSold() > 0) {
                panel.getChildren().add(statementLine("...sold to the bank this month",
                        tightMoney(toDollars(own.paperSold()), false), Palette.WARN));
            }
        }

        /* ...AND THE BUSINESSES' BONDS (0.7.12): the cell's own, bond by bond
         * since round 2, at this month's value - bought when a bond's return, less
         * what a holder expects to lose, beat the deposit rate. See
         * BondMarket, THE PARTICIPANTS. */
        double corporate = 0;
        if (own.bonds() > 0) {
            corporate = toDollars(own.bonds() * bal.getBondRatio());
            panel.getChildren().add(statementLine("The businesses' bonds",
                    tightMoney(corporate, false) + " (" + tightMoney(toDollars(own.bonds()), false)
                            + " of face)", Palette.GOOD));
            if (own.bondIncome() > 0) {
                panel.getChildren().add(statementLine("...paid on them this month",
                        tightMoney(toDollars(own.bondIncome()), false), Palette.GOOD));
            }
            if (own.bondsSold() > 0) {
                panel.getChildren().add(statementLine("...sold on their books this month",
                        tightMoney(toDollars(own.bondsSold()), false), Palette.WARN));
            }
        }

        double net = put + shares + away + bonds + corporate - owe;
        panel.getChildren().add(statementTotal("What one of them is worth",
                tightMoney(net, false), net < 0 ? Palette.BAD : Palette.GOOD));

        /*
         * AND HOW LONG THAT LASTS, which is the question a red cell actually
         * raises. Measured against a FULL basket, so a household eating less
         * than it wants is counted as short by the difference rather than as
         * having balanced its month by going hungry - the basket, subsistence(),
         * since 0.7.27; it read want(), a saver's plan, before.
         */
        double shortfall = -toDollars(own.afterFixed() - own.subsistence());
        if (shortfall > .005) {
            double cushion = Math.max(0, put + shares + away + bonds + corporate);
            panel.getChildren().add(statementNote(cushion > .005
                    ? "Short " + tightMoney(shortfall, false) + " a month, with "
                            + monthsRun(cushion / shortfall) + " of cover behind it."
                    : "Short " + tightMoney(shortfall, false)
                            + " a month with nothing behind it."));
        }

        if (own.isLockedOut()) {
            panel.getChildren().add(statementNote(String.format(
                    "Discharged: nothing will lend to them for another %d month%s, and what "
                    + "they cannot pay for they go without.",
                    own.lockout(), own.lockout() == 1 ? "" : "s")));
        } else if (own.isCutOff()) {
            panel.getChildren().add(statementNote(
                    "At the credit ceiling: what they cannot fund, they go without."));
        } else if (own.isGoingShort()) {
            panel.getChildren().add(statementNote(
                    "Buying less than they want this month."));
        }
        if (own.evicted() > .5) {
            panel.getChildren().add(statementNote(people(own.evicted())
                    + " of them lost their home this month: the rent went unpaid, and a "
                    + "quarter of those evicted leave the city rather than stay in it."));
        }
    }

    /**
     * The same month again, as shares rather than figures.
     *
     * Two households on very different money can be in identical trouble, and
     * the figures do not say so - $400 left on $6,000 and $40 left on $600 read
     * as a tenfold difference and are the same household. The percentages are
     * the comparison the grid above cannot make.
     */
    void ratioBlock(VBox panel, Household own, double income, double tax,
                            String billsLabel, double bills, double fees,
                            double shopping, double left) {

        double takeHome = income - tax;
        if (takeHome <= .005) return;

        panel.getChildren().add(panelBlockHead("Where the money goes"));
        if (tax > .005) {
            panel.getChildren().add(statementLine("Tax and contributions take",
                    shareOf(tax, income) + " of what they earn", Palette.WARN));
        }
        double burden = bills / takeHome;
        panel.getChildren().add(statementLine(billsLabel,
                shareOf(bills, takeHome) + " of take-home",
                burden > .35 ? Palette.BAD : burden > .25 ? Palette.WARN : null));
        if (fees > .005) {
            panel.getChildren().add(statementLine("Fees and interest take",
                    shareOf(fees, takeHome) + " of take-home"));
        }
        panel.getChildren().add(statementLine("The shopping takes",
                shareOf(shopping, takeHome) + " of take-home"));
        panel.getChildren().add(statementLine("They keep",
                shareOf(left, takeHome) + " of take-home",
                left < 0 ? Palette.BAD : Palette.GOOD));
        if (own.debt() > 0) {
            panel.getChildren().add(statementLine("What they owe is",
                    monthsRun(toDollars(own.debt()) / takeHome) + " of take-home",
                    Palette.WARN));
        }
    }

    /**
     * One of the people outside the families, and what a month does to them.
     *
     * The families' panel is built from HouseholdAccounts.statementFor(), which
     * derives a month for a shape and a tier. These cells have no tier and no
     * shape - what they have is their OWN ledger, which the month wrote as it
     * happened. The retired come through here too since 2026-09-14: they have a
     * shape and no tier, and a pension is not a wage.
     */
    VBox outsideStatement(HouseholdBalance bal) {

        if (bal == null) return null;
        String want = openCell.substring(4);
        Household cell = null;
        for (Household c : bal.cells()) if (c.key().equals(want)) { cell = c; break; }
        if (cell == null || cell.households() < .5) { openCell = ""; return null; }

        VBox panel = new VBox(0);
        panel.setMaxWidth(STATEMENT);
        panel.setStyle("-fx-padding: 10 14 12 14;"
                + Palette.block(Palette.PANEL, Palette.ACCENT));

        Label who = new Label(cell.label());
        who.setStyle(Palette.words(Palette.SIZE_HEADING, Palette.ACCENT)
                + " -fx-font-weight: bold;");
        panel.getChildren().add(who);
        panel.getChildren().add(panelWho(bal, cell));

        /* ------------------------------ the month ------------------------------ */
        double spend = toDollars(cell.disposable());
        double after = toDollars(cell.afterFixed());
        double bills = spend - after;
        double ate = toDollars(cell.planned());

        panel.getChildren().add(panelBlockHead("The month"));
        panel.getChildren().add(statementLine("Money to spend",
                tightMoney(spend, false), spend > 0 ? null : Palette.TEXT_SPENT));
        panel.getChildren().add(statementLine("Rent, fees and interest",
                costMoney(bills), bills > .005 ? Palette.WARN : Palette.TEXT_SPENT));
        panel.getChildren().add(statementTotal("Left for the shopping",
                tightMoney(after, false), after < 0 ? Palette.BAD : null));

        panel.getChildren().add(basketBlock(cell));
        positionBlock(panel, bal, cell);
        ratioBlock(panel, cell, spend, 0, "The bills take", bills, 0, ate, after - ate);
        return panel;
    }

    /**
     * The month of whichever cell is open, in full.
     *
     * The detail is one click from anywhere on the grid rather than seven
     * disclosures deep, and it is one panel rather than eleven rows - so the
     * figures behind a cell can be read without losing the grid that made you
     * curious about it.
     */
    VBox openStatement(HouseholdAccounts hh, HouseholdBalance bal,
                               FamilyModel families) {

        if (openCell.isEmpty()) return null;

        /*
         * EVERYBODY OUTSIDE THE FAMILY MATRIX GETS THE SAME PANEL - the out of
         * work, the students, the orphans, the prison, and since 2026-09-14 the
         * retired as well. They have their own ledgers, which are richer than
         * anything statementFor() can derive.
         */
        if (openCell.startsWith("OUT:")) return outsideStatement(bal);

        if (families == null) return null;

        String[] parts = openCell.split(":", 2);
        int tierIndex;
        FamilyStructure shape;
        try {
            tierIndex = Integer.parseInt(parts[0]);
            shape = FamilyStructure.valueOf(parts[1]);
        } catch (RuntimeException bad) {
            openCell = "";
            return null;
        }
        if (tierIndex < 0 || tierIndex >= HouseholdAccounts.RETIRED) {
            openCell = "";
            return null;
        }

        PayTier tier = PayTier.values()[tierIndex];
        HouseholdAccounts.Statement s = hh.statementFor(families, shape, tier);
        Household own = bal.cell(shape, tier);
        double left = toDollars(s.left());

        VBox panel = new VBox(0);
        panel.setMaxWidth(STATEMENT);
        panel.setStyle("-fx-padding: 10 14 12 14;"
                + Palette.block(Palette.PANEL, Palette.ACCENT));

        Label who = new Label(shape.getLabel() + "  ·  " + hh.getRowLabel(tierIndex));
        who.setStyle(Palette.words(Palette.SIZE_HEADING, Palette.ACCENT)
                + " -fx-font-weight: bold;");
        panel.getChildren().add(who);
        panel.getChildren().add(panelWho(bal, own));

        /*
         * WHAT COMES IN, SPLIT IN TWO. statementFor() adds a wage per earner to
         * a pension per senior and hands back the sum; they are different money
         * and a household with a grandparent in it is a different proposition
         * from one without. The split is exact - the pension side is seniors
         * times the pension, which is where the sum came from.
         */
        double income = toDollars(s.income());
        double pensioners = RetiredHousehold.pensionersIn(shape);
        double pension = pensioners * toDollars(hh.getPensionPerSenior());
        double wages = income - pension;

        panel.getChildren().add(panelBlockHead("What comes in"));
        if (shape.earners() > 0) {
            panel.getChildren().add(statementLine(
                    shape.earners() == 1 ? "One wage"
                            : shape.earners() + " wages, at " + hh.getRowLabel(tierIndex).toLowerCase(),
                    tightMoney(wages, false)));
        }
        if (pension > .005) {
            panel.getChildren().add(statementLine(
                    pensioners == 1 ? "A pension" : "Pensions",
                    tightMoney(pension, false), Palette.ACCENT));
        }

        /*
         * AND WHAT COMES OFF IT, split the same way and for the same reason:
         * the wage tax is a dial the player turns and the pension contribution
         * is not. The share between them is this tier's own - rowTax against
         * rowContributions - so the two lines add up to the tax the cell was
         * actually charged rather than to a number of this screen's invention.
         */
        double taxAll = toDollars(s.tax());
        double tierTax = hh.getRowTax(tierIndex);
        double tierCont = hh.getRowContributions(tierIndex);
        double split = tierTax + tierCont > 0 ? tierTax / (tierTax + tierCont) : 1;
        double onWages = hh.getRowWages(tierIndex);

        panel.getChildren().add(statementLine(
                String.format("Wage tax at %.0f%%",
                        onWages > 0 ? tierTax / onWages * 100 : 0),
                costMoney(taxAll * split), Palette.WARN));
        if (taxAll * (1 - split) > .005) {
            panel.getChildren().add(statementLine(
                    String.format("Pension contributions at %.1f%%",
                            ui.game.getEconomyManager().getTaxPolicy().getContributionRate() * 100),
                    costMoney(taxAll * (1 - split)), Palette.WARN));
        }
        panel.getChildren().add(statementTotal("Take-home",
                tightMoney(income - taxAll, false), null));

        /*
         * WHAT THE MONTH COSTS. The fees line was one figure covering unlike
         * things, and two of them were named wrong until 0.7.27: "Healthcare
         * and school fees" carried the fares (the per-head charge is the
         * clinics', the schools' and the buses'), and "Interest on what they
         * owe" was the rest of the line - the tier's interest AND the bank's
         * account fee, so it read interest where nobody owed anything. Each
         * is its own line now, read from the accounts' own split of the fees
         * statementFor() charges.
         */
        double feesAll = toDollars(s.fees());
        double care = toDollars(hh.careAndSchoolPerHead()) * s.people();
        double fares = toDollars(hh.faresPerHead()) * s.people();
        double interest = toDollars(hh.interestPerHousehold(tier));
        double bankFees = toDollars(hh.accountFeePerHousehold(tier));

        panel.getChildren().add(panelBlockHead("What the month costs"));
        panel.getChildren().add(statementLine("Rent", costMoney(toDollars(s.rent()))));
        panel.getChildren().add(statementLine("Healthcare and school fees", costMoney(care)));
        if (fares > .005) {
            panel.getChildren().add(statementLine("Transit fares", costMoney(fares)));
        }
        if (interest > .005) {
            panel.getChildren().add(statementLine("Interest on what they owe",
                    costMoney(interest), Palette.WARN));
        }
        if (bankFees > .005) {
            panel.getChildren().add(statementLine("Bank fees", costMoney(bankFees)));
        }
        panel.getChildren().add(statementLine("The shopping basket",
                costMoney(toDollars(s.shopping()))));
        panel.getChildren().add(statementTotal(left < 0 ? "Short by" : "Left over",
                tightMoney(left, false), left < 0 ? Palette.BAD : Palette.GOOD));

        HBox bar = flowBar(toDollars(s.tax()), toDollars(s.rent()), toDollars(s.fees()),
                toDollars(s.shopping()), left, STATEMENT - 28);
        VBox.setMargin(bar, new javafx.geometry.Insets(8, 0, 0, 0));
        panel.getChildren().add(bar);
        panel.getChildren().add(flowKey());

        /*
         * ...AND THE HOUSEHOLD'S OWN BOOKS, which are its own since 2026-09-10.
         * Everything above is a flow the accounts derive; everything below is
         * the cell's own ledger, written by the month as it happened.
         */
        if (own.households() >= .5) {
            /*
             * AND WHERE THE TWO DISAGREE, SAY SO. The month above is DERIVED -
             * a wage per earner off the tier's totals, a rent per door, a
             * basket per head. The ledger below was WRITTEN, by the month, as
             * it happened. On most cells the two land within a rounding of each
             * other and on some they do not, and a panel that quietly showed a
             * derived month above a written position would be inventing a
             * household that is the top half of one and the bottom half of
             * another.
             *
             * The house rule, from the reporting audit: a breakdown that
             * silently absorbs its own gap is worse than no breakdown.
             */
            double ledgerTakeHome = toDollars(own.disposable());
            double gap = ledgerTakeHome - (income - taxAll);
            if (Math.abs(gap) > Math.max(1, Math.abs(income - taxAll) * .01)) {
                panel.getChildren().add(statementNote(String.format(
                        "The books below are this household's own and they open on %s of "
                        + "take-home, not the %s above - %s apart. The month above is derived "
                        + "from the tier's totals; the ledger was written as the month happened.",
                        tightMoney(ledgerTakeHome, false),
                        tightMoney(income - taxAll, false),
                        tightMoney(Math.abs(gap), false))));
            }
            panel.getChildren().add(basketBlock(own));
            positionBlock(panel, bal, own);
            ratioBlock(panel, own, income, taxAll, "Rent takes",
                    toDollars(s.rent()), feesAll, toDollars(s.shopping()), left);
        }

        VBox holder = new VBox(panel);
        holder.setStyle("-fx-padding: 4 0 10 0;");
        return holder;
    }

    /* =====================================================================
       THE TIER TABLE, AS A TABLE.

       What this replaces was ten columns lined up by padding a String, and it
       had two faults that no amount of care with the padding could fix.

       THE ROW WAS ONE LABEL, so it was one colour. A tier that earns well and
       banks nothing, and a tier that earns badly and banks nothing, came out
       the same shade - because the only thing the screen could colour was the
       whole line. The one figure on the row that is NEWS is what is left at the
       end of it, and it could not be said any louder than the rest.

       AND THE ALIGNMENT WAS A GUESS. There is a comment in the old code about
       "Retired (no earner)" being nineteen characters against a nineteen-wide
       column, which shunted every figure on that row one place right and made
       the whole table unreadable. A column width should not be a fact somebody
       has to remember.

       A GridPane fixes both: the columns line up because the layout lines them
       up, and every cell is its own node with its own colour.

       AND THE BAR ON THE END is the point of the screen. Jerus's own note on
       this data: income across these rows varies about tenfold, while rent and
       the shopping basket per head do not vary at all - so the bottom rows run
       a deficit and the top rows bank almost everything. That is the finding,
       and reading it out of eight columns of figures took work. One stacked bar
       per row says it at a glance, and the figures are still there for the
       reading.
       ===================================================================== */

    /**
     * Where a household's month went, as one bar.
     *
     * Proportional to whichever is larger, what came in or what went out - so a
     * household spending more than it earns fills the bar and the shortfall
     * shows as red on the end rather than silently rescaling everything else.
     *
     * The colours are the categories, not a gradient - and since 0.7.21 the
     * four area colours, because amber and green are verdicts: tax the money
     * blue (it is the one the player sets, on the Policy tab), rent the
     * building pink, fees the business violet, the shops the people teal -
     * and what survives green, or red when nothing does, which IS a verdict.
     */
    HBox flowBar(double tax, double rent, double fees, double shops,
                         double left, double width) {

        double out = Math.max(0, tax) + Math.max(0, rent)
                   + Math.max(0, fees) + Math.max(0, shops);
        double span = out + Math.abs(left);
        if (span <= 0) return new HBox();

        HBox bar = new HBox(0);
        bar.setPrefWidth(width);
        bar.setMinWidth(width);
        bar.setMaxWidth(width);
        bar.setPrefHeight(9);
        bar.setMinHeight(9);
        bar.setStyle("-fx-background-radius: 2;");

        bar.getChildren().addAll(
                barPart(tax / span * width, Palette.MONEY, "tax"),
                barPart(rent / span * width, Palette.BUILDING, "rent"),
                barPart(fees / span * width, Palette.BUSINESS, "fees"),
                barPart(shops / span * width, Palette.PEOPLE, "the shops"),
                barPart(Math.abs(left) / span * width,
                        left < 0 ? Palette.BAD : Palette.GOOD,
                        left < 0 ? "short" : "left over"));
        return bar;
    }

    /** One segment. Zero-width segments are skipped rather than drawn as slivers. */
    Region barPart(double width, String colour, String what) {
        Region part = new Region();
        double w = Math.max(0, width);
        part.setPrefWidth(w);
        part.setMinWidth(w);
        part.setMaxWidth(w);
        part.setStyle("-fx-background-color: " + colour + ";");
        if (w >= 2) Tooltip.install(part, new Tooltip(what));
        return part;
    }

    /** The key under the bar, so the colours mean something the first time. */
    HBox flowKey() {
        HBox key = new HBox(Palette.GAP_LOOSE);
        key.setAlignment(Pos.CENTER_LEFT);
        key.setStyle("-fx-padding: 6 0 0 0;");
        String[][] parts = {
            {"tax", Palette.MONEY}, {"rent", Palette.BUILDING}, {"fees", Palette.BUSINESS},
            {"the shops", Palette.PEOPLE}, {"what is left", Palette.GOOD},
        };
        for (String[] part : parts) {
            Region swatch = new Region();
            swatch.setPrefSize(9, 9);
            swatch.setMinSize(9, 9);
            swatch.setStyle("-fx-background-color: " + part[1] + "; -fx-background-radius: 2;");
            Label what = new Label(part[0]);
            what.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            HBox one = new HBox(4, swatch, what);
            one.setAlignment(Pos.CENTER_LEFT);
            key.getChildren().add(one);
        }
        return key;
    }

    /* ---------------------------------------------------------------------
       Small helpers so the report screens stay readable. Shared by the sector
       screens - Industrial and Utility can reuse these when they get ported.
       --------------------------------------------------------------------- */
}
