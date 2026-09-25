package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * The Reports tab: the city as a shape over time.
 *
 * Two small pinned charts, then one big chart with lines overlaid - one unit
 * on a real axis, two on two real axes, three or more mapped onto 0-100 by
 * their own range over the window - with recessions shaded and the named
 * episodes marked under it; a legend that carries the real values, presets
 * for the questions a player usually has, a picker folded into its groups,
 * and the year book written out on request (the page as of 0.7.5 is under
 * THE PAGE, REDRAWN; real GDP drawn in layers on a toggle since 0.7.6, under
 * GDP IN LAYERS). The first screen split out of
 * UserInterface (2026-09-18), chosen because it is the most self-contained:
 * it reads the window's game and root, calls clearMenu() and scrolled(), and
 * nothing else in the shell reads it but the rail.
 *
 * The text came over exactly as it was inside UserInterface - the two banners,
 * THE HISTORY SCREEN and THE RECORD, and everything under them - with the
 * shell's members reached through ui. Nothing was rewritten on the way. THE
 * GOODS ROW followed later the same day: it had sat under the shell's THE
 * STATEMENT banner, and this screen was the only thing that drew it.
 */
final class HistoryScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    HistoryScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       THE HISTORY SCREEN

       The accounts screen says what happened THIS month. Everything in it is a
       stock or a flow at one instant, and a city is a shape over time - the
       month the rate spiked, the decade growth stalled, the point arrivals went
       to zero and births carried the population on alone. None of that is
       visible one month at a time, and the game has been recording it since the
       first build without ever showing it.

       ONE CHART WITH THINGS OVERLAID, by request. Which forces the awkward
       question the accounts screen never had to answer: GDP is in thousands,
       population is a headcount, and the interest rate is a fraction between
       zero and one. Plotted together on one axis, the rate is a flat line on
       the floor and everything else is invisible.

       So: one series draws in its own units, and two or more are each mapped
       onto 0-100 by their OWN range over the window on screen. (That was the
       first rule. THE RECORD below gave lines that share a unit a real axis,
       and since 0.7.5 two units get two real axes and only three or more are
       mapped - see THE PAGE, REDRAWN.) Not indexed to
       100 at the start, which is the usual answer and which breaks here -
       debt, jobs and population all START AT ZERO in a new city, and there is
       no percentage of zero. Min-to-max also survives the series that go
       negative, which the surplus does routinely.

       What the normalisation costs is the values, so the legend under the chart
       carries them: first, last, low and high, in real units, for everything
       selected. The shape comes off the chart and the numbers come off the
       legend, and neither pretends to be the other.
       ===================================================================== */

    /** What the player has ticked, and how far back they are looking. */
    final java.util.LinkedHashSet<String> historyPicked = new java.util.LinkedHashSet<>();

    /** Whether the first-visit preset has been handed over; see showHistoryMenu. */
    boolean historySeeded = false;
    int historyWindow = 120;

    /**
     * The big chart on a log scale, when what is picked allows it; see
     * logRefusal(). Screen state, like the picks: kept while the game runs,
     * never saved.
     */
    boolean historyLog = false;

    /**
     * Real GDP drawn in layers - consumption, investment and government
     * stacked from zero, the line over them (0.7.6) - on its small chart,
     * and on the big one when it is picked alone. Screen state, like the
     * log scale: kept while the game runs, never saved; the pins are not
     * touched by it. See GDP IN LAYERS.
     */
    boolean gdpLayers = false;

    /** What is typed in the picker's filter box, kept so a month's redraw does not wipe it. */
    String historyFilter = "";

    /**
     * Which of the picker's groups the player has opened or closed, by name.
     * A group nobody has touched is open when one of its lines is picked and
     * closed otherwise. Screen state, not saved.
     */
    final java.util.Map<String, Boolean> groupOpen = new java.util.HashMap<>();

    /** Above this many points a line is bucket-averaged; see bucketSize(). */
    static final int MAX_PLOT_POINTS = 400;

    /**
     * One plottable line: where it comes from and how to read it.
     *
     * @param key    the stored series name, or a derived one computed in
     *               historyValues()
     * @param unit   how to format a value of it - the difference between "0.18"
     *               and "18%" and "$0.18"
     */
    record Trace(String key, String label, String group, String unit) { }

    static final Trace[] TRACES = withTheCrime(withTheHouseholds(withTheMarket(new Trace[] {
        new Trace("gdp",            "GDP",                "MONEY",      "money"),
        new Trace("gdpPerCapita",   "GDP per capita (yr)","MONEY",      "money"),
        new Trace("realGdp",        "GDP, real (yr)",     "MONEY",      "money"),
        new Trace("cash",           "Treasury",           "MONEY",      "money"),
        new Trace("debt",           "Public debt",        "MONEY",      "money"),
        new Trace("revenue",        "Revenue",            "MONEY",      "money"),
        new Trace("surplus",        "Surplus / deficit",  "MONEY",      "money"),
        new Trace("interestRate",   "Borrowing rate",     "MONEY",      "percent"),
        new Trace("totalWage",      "Wage bill",          "MONEY",      "money"),
        new Trace("averageWage",    "Average wage",       "MONEY",      "money"),
        new Trace("minimumWage",    "Minimum wage",       "MONEY",      "money"),
        new Trace("unskilledPremium","Unskilled vs base", "MONEY",      "ratio"),
        new Trace("skilledShare",   "Workforce trained",  "PEOPLE",     "percent"),
        new Trace("schoolCoverage", "Children in school", "PEOPLE",     "percent"),
        new Trace("schoolBill",     "Schools",            "MONEY",      "money"),

        new Trace("population",     "Population",         "PEOPLE",     "count"),
        new Trace("workforce",      "Workforce",          "PEOPLE",     "count"),
        new Trace("labourForce",    "Labour force",       "PEOPLE",     "count"),
        new Trace("jobs",           "Jobs",               "PEOPLE",     "count"),
        new Trace("unemployment",   "Unemployment",       "PEOPLE",     "percent"),
        new Trace("outOfWork",      "Out of work",        "PEOPLE",     "count"),
        new Trace("births",         "Births",             "PEOPLE",     "count"),
        new Trace("deaths",         "Deaths",             "PEOPLE",     "count"),
        new Trace("diedOfIllness",  "Died of illness",    "PEOPLE",     "count"),
        new Trace("sickPastTwoMonths","Sick past two months","PEOPLE",  "count"),
        new Trace("arrivals",       "Arrivals",           "PEOPLE",     "count"),
        new Trace("departures",     "Departures",         "PEOPLE",     "count"),
        new Trace("netMigration",   "Net migration",      "PEOPLE",     "count"),
        new Trace("naturalIncrease","Births - deaths",    "PEOPLE",     "count"),

        /* The running totals of who has died (2026-09-11) - derived, see historyValues(). */
        new Trace("cumulative:deaths",         "Died since founding", "THE DEAD", "count"),
        new Trace("cumulative:deathsBabies",   "Babies",              "THE DEAD", "count"),
        new Trace("cumulative:deathsChildren", "Children",            "THE DEAD", "count"),
        new Trace("cumulative:deathsTeens",    "Teens",               "THE DEAD", "count"),
        new Trace("cumulative:deathsAdults",   "Adults",              "THE DEAD", "count"),
        new Trace("cumulative:deathsSeniors",  "Seniors",             "THE DEAD", "count"),
        new Trace("cumulative:deathsElders",   "Elders",              "THE DEAD", "count"),
        new Trace("cumulative:deathsOrphans",  "Orphans",             "THE DEAD", "count"),
        new Trace("cumulative:deathsUnhoused", "With no home",        "THE DEAD", "count"),
        new Trace("cumulative:deathsKilled",   "Killed",              "THE DEAD", "count"),

        /* Crime, the police and the prisons (2026-09-11). Each reason's crimes are added below. */
        new Trace("crimeRate",      "Crime a year /100k", "CRIME",      "count"),
        new Trace("policeCoverage", "Police coverage",    "CRIME",      "percent"),
        new Trace("prisoners",      "In prison",          "CRIME",      "count"),
        new Trace("caughtNotHeld",  "Caught, not held",   "CRIME",      "count"),
        new Trace("deathsKilled",   "Killed",             "CRIME",      "count"),
        new Trace("stolen",         "Stolen",             "CRIME",      "money"),
        new Trace("safetyBill",     "Police and prisons", "CRIME",      "money"),

        new Trace("energyRatio",    "Power supplied",     "THROUGHPUT", "percent"),
        new Trace("waterRatio",     "Water supplied",     "THROUGHPUT", "percent"),
        new Trace("roadRatio",      "Road throughput",    "THROUGHPUT", "percent"),
        new Trace("sickRate",       "Off sick",           "THROUGHPUT", "percent"),
        new Trace("sickRecovery",   "Sick who got better", "THROUGHPUT", "percent"),
        new Trace("careCoverage",   "Health coverage",    "THROUGHPUT", "percent"),

        new Trace("landPrice",      "Land",               "PRICES",     "land"),
        new Trace("foodPrice",      "Food",               "PRICES",     "unitprice"),
        new Trace("materialsPrice", "Materials",          "PRICES",     "unitprice"),
        new Trace("orePrice",       "Ore",                "PRICES",     "unitprice"),

        /* =====================================================================
           EVERYTHING THE CITY GREW AFTER THE GRAPH WAS WRITTEN.

           The four groups above were the whole game once. Since then the city
           has acquired an edge it trades across, a bank, a budget with parts, a
           rent that moves and a school system - and none of it left a mark on
           this screen, which is a graph of a city that no longer exists.

           Grouped by the question each answers rather than by where the number
           is kept: TRADE is "how is the city doing against the world", CREDIT
           is "what does money cost", BUDGET is "which tax paid for this",
           HOUSING is "can people afford to live here", SCHOOLS is "is the
           workforce getting better".
           ===================================================================== */

        new Trace("fxRate",         "Exchange rate",      "TRADE",      "rate"),
        new Trace("reservesUsd",    "FX reserves",        "TRADE",      "usd"),
        new Trace("foreignDebtUsd", "Foreign debt",       "TRADE",      "usd"),
        new Trace("foreignDebtLocal","Foreign debt (local)","TRADE",    "money"),
        new Trace("currentAccount", "Current account",    "TRADE",      "money"),
        new Trace("exportsAbroad",  "Sold abroad",        "TRADE",      "money"),
        new Trace("importsAbroad",  "Bought abroad",      "TRADE",      "money"),
        new Trace("tradeBalance",   "Trade balance",      "TRADE",      "money"),

        new Trace("priceIndex",     "Price level",        "CREDIT",     "index"),
        new Trace("inflation",      "Inflation (yr)",     "CREDIT",     "percent"),
        new Trace("businessDebt",   "Business debt",      "CREDIT",     "money"),
        new Trace("policyRate",     "Policy rate",        "CREDIT",     "percent"),
        new Trace("bankPrime",      "Bank prime",         "CREDIT",     "percent"),
        new Trace("bankDepositRate","Savers' rate",       "CREDIT",     "percent"),
        new Trace("bankFees",       "Bank fees",          "CREDIT",     "money"),
        new Trace("bankDeposits",   "Bank deposits",      "CREDIT",     "money"),
        new Trace("bankLent",       "Bank lending",       "CREDIT",     "money"),
        new Trace("bankEquity",     "Bank equity",        "CREDIT",     "money"),
        new Trace("bankWriteOffs",  "Written off",        "CREDIT",     "money"),
        new Trace("bankCapacity",   "Bank capacity",      "CREDIT",     "money"),
        new Trace("bankStrain",     "Bank strain",        "CREDIT",     "ratio"),
        new Trace("bankProfit",     "Bank profit",        "CREDIT",     "money"),
        new Trace("bankBranches",   "Bank branches",      "CREDIT",     "count"),
        new Trace("householdSavings","Household savings", "CREDIT",     "money"),

        new Trace("taxWage",        "Income tax",         "BUDGET",     "money"),
        new Trace("taxProperty",    "Property tax",       "BUDGET",     "money"),
        new Trace("taxSales",       "Sales tax",          "BUDGET",     "money"),
        new Trace("taxBusiness",    "Commerce profit tax","BUDGET",     "money"),
        new Trace("taxIndustrial",  "Industry profit tax","BUDGET",     "money"),
        new Trace("contributions",  "CPP contributions",  "BUDGET",     "money"),
        new Trace("pensionBill",    "Pensions paid",      "BUDGET",     "money"),
        new Trace("healthBill",     "Healthcare (net)",   "BUDGET",     "money"),

        new Trace("rentPrice",      "Rent a head",        "HOUSING",    "rent"),
        new Trace("homes",          "Homes",              "HOUSING",    "count"),
        new Trace("households",     "Households",         "HOUSING",    "count"),
        new Trace("vacancy",        "Homes standing empty","HOUSING",   "percent"),

        new Trace("students",       "Students",           "SCHOOLS",    "count"),
        new Trace("graduates",      "Graduates",          "SCHOOLS",    "count"),
        new Trace("licences",       "New licences",       "SCHOOLS",    "count"),

        new Trace("unburied",       "Unburied",           "PEOPLE",     "count"),

        new Trace("constructionCapacity", "Builders",     "THROUGHPUT", "count"),
        new Trace("landUse",        "Land in use",        "THROUGHPUT", "percent"),

        // The people outside the families (2026-09-11).
        new Trace("outOfWorkOnEi",  "Out of work, on EI", "OUTSIDE THE FAMILIES", "count"),
        new Trace("outOfWorkOffEi", "Out of work, EI run out", "OUTSIDE THE FAMILIES", "count"),
        new Trace("unhoused",       "No home",            "OUTSIDE THE FAMILIES", "count"),
        new Trace("orphans",        "Orphans",            "OUTSIDE THE FAMILIES", "count"),
        new Trace("evicted",        "Lost their home",    "OUTSIDE THE FAMILIES", "count"),
        new Trace("eiPaid",         "EI paid",            "OUTSIDE THE FAMILIES", "money"),
        new Trace("eiPremiums",     "EI premiums",        "OUTSIDE THE FAMILIES", "money"),
        new Trace("healthPremiums", "Health premiums",    "OUTSIDE THE FAMILIES", "money"),
        new Trace("studentGrants",  "Student grants",     "OUTSIDE THE FAMILIES", "money"),
        new Trace("studentLoansOwed","Student loans owed","OUTSIDE THE FAMILIES", "money"),
        new Trace("studentLoanInterest","Student loan interest","OUTSIDE THE FAMILIES", "money"),
    })));

    /** One series per household shape, appended after the market. See HistorySave.householdKey(). */
    static Trace[] withTheCrime(Trace[] fixed) {
        Crime.Cause[] causes = Crime.Cause.values();
        Trace[] all = java.util.Arrays.copyOf(fixed, fixed.length + causes.length);
        for (int c = 0; c < causes.length; c++) {
            all[fixed.length + c] = new Trace(HistorySave.crimeKey(causes[c]),
                    "Crime: " + causes[c].label().toLowerCase(), "CRIME", "count");
        }
        return all;
    }

    static Trace[] withTheHouseholds(Trace[] fixed) {
        FamilyStructure[] shapes = FamilyStructure.values();
        Trace[] all = java.util.Arrays.copyOf(fixed, fixed.length + shapes.length);
        for (int s = 0; s < shapes.length; s++) {
            all[fixed.length + s] = new Trace(HistorySave.householdKey(shapes[s]),
                    shapes[s].getLabel(), "HOUSEHOLDS", "count");
        }
        return all;
    }

    /**
     * ...and a share price per company, one trace each, generated off the
     * register's own list so a company added there is graphed here without
     * anybody remembering to. THE MARKET is the last group on the screen,
     * and its unit is a founding share - see HistorySave's market block.
     */
    static Trace[] withTheMarket(Trace[] fixed) {
        Trace[] all = java.util.Arrays.copyOf(fixed, fixed.length + Equity.COMPANIES.length);
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            String company = Equity.COMPANIES[c];
            all[fixed.length + c] = new Trace(HistorySave.priceKey(company),
                    company + " shares", "THE MARKET", "share");
        }
        return all;
    }

    /** Eight, then it wraps - and the legend swatch uses the same list. */
    static final String[] TRACE_COLOURS = {
        "#5cb8ff", "#ff6b6b", "#5fd68a", "#ffb454",
        "#ce93d8", "#4dd0e1", "#d4e157", "#c8b0a5"
    };

    /* =====================================================================
       THE RECORD

       Jerus: "reports lets do this, this one isnt really much, just make it
       cleaner, or if you think something else then go for it."

       It was the last screen in Courier: a title, three range buttons, a raw
       LineChart, a monospaced four-column table and sixty-nine tick boxes in
       ten groups. Everything it needed was on it, and finding any of it meant
       reading a filing cabinet.

       WHAT CHANGED, BEYOND THE PAINT:

       ONE UNIT MEANS A REAL AXIS. The chart normalised every line to 0-100 the
       moment there was more than one, and then had to label the axis "each line
       across its own low-to-high in this window" - which is honest and means the
       numbers up the side say nothing. When every picked line shares a unit -
       revenue against surplus, births against deaths - they are now drawn
       against each other on a real axis with real figures. Normalising is what
       happens when the units disagree, not what happens when you pick two
       things. (Since 0.7.5 two units get an axis each, and only three or more
       are normalised - see THE PAGE, REDRAWN.)

       SIX PRESETS. Sixty-nine chips is a filing cabinet rather than a question.
       The presets are the questions a player actually has, and each one is three
       or four lines that belong on the same chart.

       AND THE LEGEND ANSWERS THE QUESTION. A graph is nearly always being asked
       "what happened", and the old table answered "first, latest, low, high" in
       four columns of monospace. It now leads with the latest figure and the
       move since the window opened, in the unit's own terms - points for a rate,
       per cent for a quantity - because +1.0 points on a 2% rate is not "+50%"
       to anybody who has ever read a rate.
       ===================================================================== */

    /** How wide this one screen runs. A graph earns more room than a statement. */
    static final double GRAPH = 760;

    /** A curated set of lines that belong on one chart. */
    record Preset(String name, String blurb, String[] keys) { }

    static final Preset[] PRESETS = {
        /*
         * FIRST, BECAUSE IT IS WHAT A FIRST VISIT DRAWS. Jerus, 2026-09-23:
         * "the bigger one, the one where you set the stuff, it defaults to the
         * borrowing rate, price level and inflation year on year". Two units -
         * two per cents and an index - so it comes out on two real axes.
         */
        new Preset("What money costs", "the borrowing rate, the price level, and how fast it is rising",
                new String[] {"interestRate", "priceIndex", "inflation"}),
        new Preset("How it is going", "output, people, and what money costs",
                new String[] {"gdp", "population", "interestRate"}),
        new Preset("The people", "how many, and whether there is work for them",
                new String[] {"population", "jobs", "unemployment", "netMigration"}),
        new Preset("The budget", "what the city took, and what it kept",
                new String[] {"revenue", "surplus", "debt"}),
        new Preset("Money and credit", "prices, and what the bank is carrying",
                new String[] {"inflation", "bankLent", "bankCapacity"}),
        new Preset("The world", "the city's edge, in both directions",
                new String[] {"exportsAbroad", "importsAbroad", "currentAccount"}),
        new Preset("Can people live here", "wages against rent, school and room",
                new String[] {"averageWage", "rentPrice", "vacancy", "schoolCoverage"}),
        new Preset("The market", "what a founding share of each company is worth",
                marketKeys()),
        new Preset("Outside the families", "the out of work, the unhoused and the orphans",
                new String[] {"outOfWorkOnEi", "outOfWorkOffEi", "unhoused", "orphans"}),
        new Preset("Who has died", "the running totals, since the city began",
                new String[] {"cumulative:deathsBabies", "cumulative:deathsAdults",
                              "cumulative:deathsSeniors", "cumulative:deathsOrphans"}),
        new Preset("Crime", "how much, why, and whether the city can hold who it catches",
                new String[] {"crimeRate", "policeCoverage", "prisoners", "caughtNotHeld"}),
        new Preset("Why there is crime", "each reason's crimes a month",
                new String[] {HistorySave.crimeKey(Crime.Cause.PAST_EI),
                              HistorySave.crimeKey(Crime.Cause.SHORT_OF_MONEY),
                              HistorySave.crimeKey(Crime.Cause.CROWDED),
                              HistorySave.crimeKey(Crime.Cause.FEW_POLICE)}),
        new Preset("The households", "how many of each kind of household the city holds",
                new String[] {HistorySave.householdKey(FamilyStructure.SINGLE_ADULT),
                              HistorySave.householdKey(FamilyStructure.COUPLE),
                              HistorySave.householdKey(FamilyStructure.COUPLE_TWO_CHILDREN),
                              HistorySave.householdKey(FamilyStructure.SENIOR_ALONE)}),
    };

    /** Every company's share price, for the market preset. */
    static String[] marketKeys() {
        String[] keys = new String[Equity.COMPANIES.length];
        for (int c = 0; c < keys.length; c++) keys[c] = HistorySave.priceKey(Equity.COMPANIES[c]);
        return keys;
    }

    /* =====================================================================
       THE PAGE, REDRAWN (0.7.5)

       Jerus, 2026-09-23: "for the graphs, im thinking, first of all, have it
       be a collapsable list, and also, there are two graphs always displayed
       on the graph rail, the real gdp yearly figure, and the other is the
       population, and then the third one, the bigger one, the one where you
       set the stuff, it defaults to the borrowing rate, price level and
       inflation year on year, and then you can scroll to clear all - 'clear
       all' should just be beside the graph - and you can then expand the list
       to click on the specific stuff you want." And, to the proposal: "pinnable
       defaults, but not fixed, and leave goods there, and event marks".

       So, top to bottom: the range; TWO PINNED CHARTS, one line each in its
       own units, remembered in GamePrefs because which two lines a player
       keeps in view is how they read and not a fact about the city; THE BIG
       CHART, with the presets, "clear all" and "log" in a row above it and the
       named episodes marked under it; the readings, each with a pin chip; the
       picker, its groups closed until wanted, with a box to find a line by
       name; then the goods and the year book, as they were.

       TWO UNITS MEAN TWO REAL AXES. The 0-100 mapping was the answer to "the
       units disagree", and for two units it threw both scales away to solve a
       problem a second axis solves without losing either. A rate against a
       price level is the commonest question on this page - the first-visit
       trio is exactly that. Three or more still map, since three scales on one
       chart is a chart nobody can read, and the axis says so.

       NOTHING HERE DECIDES WHAT HAPPENED. The recession bands and the episode
       marks come off YearBook (recessions(), episodes()), the same functions
       the year book's WHAT HAPPENED section prints from, so the file and the
       chart cannot disagree about what to call a year. The page formats.
       ===================================================================== */

    void showHistoryMenu() {
        // Whether the player was typing in the filter box when this redraw came:
        // the month rebuilds the page under them, and the new box takes the
        // focus back, or the next key they press is a shortcut.
        boolean typing = filterField != null && filterField.isFocused();

        ui.clearMenu("showHistoryMenu", () -> showHistoryMenu());
        named.clear();

        HistorySave h = ui.game.getHistorySave();

        /* =====================================================================
           SEEDED ONCE, AND THEN LEFT ALONE.

           Jerus: "if you have one clicked, and you unclick it, it automatically
           brings up three. That should not be the case, if zero clicked then
           the graph is just empty."

           This used to re-seed whenever the set was empty, which made "empty"
           unreachable: unticking the last line, or pressing "clear them all"
           (now "clear all", beside the chart), put the first preset straight
           back. The screen was answering a
           question about a MISSING value when the player had given it a real
           one - nothing is a choice here, and the screen already knows how to
           draw it (a blank plot over the window, "0 lines", "nothing picked").

           So the seed is a first-visit courtesy and nothing more. The flag, not
           the emptiness of the set, is what says whether the courtesy is spent.
           ===================================================================== */
        if (!historySeeded) {
            historySeeded = true;
            historyPicked.addAll(java.util.Arrays.asList(PRESETS[0].keys()));
        }

        Label title = new Label("CITY HISTORY");
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        Label lead = new Label("Every month the city has lived, and what it did.");
        lead.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED)
                + " -fx-padding: 0 0 10 0;");

        if (h.months() < 2) {
            ui.rootMenu.getChildren().addAll(title, lead, alert("Nothing to draw yet",
                    "The city has lived " + h.months() + " month"
                    + (h.months() == 1 ? "" : "s") + ". A line needs two points."));
            return;
        }

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        // Read once a draw and handed to all three charts: the recession months
        // shade every one of them, and the episodes are marked under the big one.
        List<int[]> recessions = YearBook.recessions(h);
        List<YearBook.Episode> episodes = YearBook.episodes(h);

        column.getChildren().add(historyRange(h));
        column.getChildren().add(pinnedCharts(h, recessions));
        column.getChildren().add(chartControls(h));
        column.getChildren().add(historyChart(h, recessions, episodes));

        column.getChildren().add(statementHead("What each line did", GRAPH));
        if (historyPicked.isEmpty()) {
            column.getChildren().add(sentence(
                    "Nothing is selected. Pick a preset above the chart, or a line below.",
                    Palette.TEXT_MUTED));
        } else {
            // On two axes, each reading says which one its line is read against.
            List<String> units = pickedUnits();
            int colour = 0;
            for (String key : historyPicked) {
                String axis = units.size() != 2 ? null
                        : units.indexOf(traceFor(key).unit()) == 0 ? "left axis" : "right axis";
                column.getChildren().add(historyReading(h, key,
                        bigInLayers() ? LAYERED_LINE : TRACE_COLOURS[colour % TRACE_COLOURS.length], axis));
                colour++;
            }
            if (historyPicked.size() > TRACE_COLOURS.length) {
                column.getChildren().add(statementNote(
                        "There are more lines than colours, so the palette has wrapped "
                        + "and two of them are the same. Eight is as many as a chart can "
                        + "tell apart anyway."));
            }
        }

        column.getChildren().add(statementHead("Pick what to draw", GRAPH));
        column.getChildren().add(statementNote(
                "Lines measured in the same thing are drawn against each other on a real axis; "
                + "two units get an axis each, left and right; mix three or more and the chart "
                + "falls back to each line's own low-to-high, which compares shapes rather than "
                + "sizes."));
        column.getChildren().add(historyPickerRows());

        /* =====================================================================
           EVERY GOOD, ON ONE PAGE.

           Jerus: "somewhere somehow, i should be able to see all the goods, and
           the current prices and some quick info". There was nowhere: a good's
           price appeared only on the screen of whichever sector happened to
           make it, and the thirteen foods had no screen at all because no
           sector makes them. Twenty-five goods and no index is a model you have
           to read the source to see.

           The band is the whole story for a traded good - what the world charges
           to land one and what it pays for one - so the row says where in that
           band the city has ended up, and what moved this month.
           ===================================================================== */
        column.getChildren().add(statementHead("Every good in the city", GRAPH));
        column.getChildren().add(statementNote(
                "What a unit costs here this month, the world's band around it, and what crossed the "
                + "border. A good with no band is priced by whoever sells it."));
        for (Good g : Good.values()) column.getChildren().add(goodsRow(g));

        column.getChildren().add(statementHead("Send this run to somebody", GRAPH));
        column.getChildren().add(statementNote(
                "Writes the whole run out as plain text - every series the history keeps, folded "
                + "one row a year, and again one row a decade, with a list of what actually "
                + "happened and when. Each column says whether it was added, taken at the end of "
                + "the row or averaged, and what it means, so it can be handed to somebody - or "
                + "something - that has never seen this city."));
        Button books = new Button("Write the year book  \u2192");
        books.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + Palette.CONFIRM + ";");
        books.setOnAction(e -> writeTheBooks());
        VBox.setMargin(books, new javafx.geometry.Insets(4, 0, 4, 14));
        column.getChildren().add(books);
        if (bookExportSaid != null) column.getChildren().add(statementNote(bookExportSaid));

        /*
         * KEPT FROM THE BOTTOM, AND THE THING PRESSED HELD STILL.
         *
         * From the bottom because the legend above the picker grows a block per
         * line picked, and the picker is what the player is pressing - see
         * keptScrollerFromBottom. That covers a month's redraw. A click can
         * now also change what is BELOW the pointer - a group opening, a preset
         * or "clear all" above a legend that changes length - so the chip that
         * was pressed is found again in the new page and put back where it was
         * on the screen (holdInPlace), after the memory has had its go.
         */
        javafx.scene.control.ScrollPane scroller = ui.scrolled(column, 210, true);
        ui.rootMenu.getChildren().addAll(title, lead, historyVitals(h), scroller);
        page = scroller;

        if (pressed != null) {
            holdInPlace(named.get(pressed), pressedAt);
            pressed = null;
            pressedAt = Double.NaN;
        }
        if (typing && filterField != null) {
            filterField.requestFocus();
            filterField.positionCaret(filterField.getText().length());
        }
    }

    /**
     * What the last export did, kept so it survives the redraw.
     *
     * The click redraws this whole screen, and so does the month turning under
     * it, so a message held in a local would be gone before it was read.
     */
    String bookExportSaid;

    void writeTheBooks() {
        StringBuilder said = new StringBuilder();
        for (GameFiles.Result written : ui.game.writeBooks()) {
            if (written.ok) said.append("Written: ").append(written.file).append('\n');
            else said.append("Could not write ").append(written.file)
                     .append(" - ").append(written.message()).append('\n');
        }
        bookExportSaid = said.toString().trim();
        showHistoryMenu();
    }

    /* --------------------------------------------------------------------- */

    HBox historyVitals(HistorySave h) {

        List<Integer> months = h.getMonth();
        int drawn = Math.min(h.months(), historyWindow);
        int from = Math.max(0, months.size() - historyWindow);
        int bucket = bucketSize(drawn);

        String span = months.isEmpty() ? "" : CityCalendar.formatShort(months.get(from))
                + " to " + CityCalendar.formatShort(months.get(months.size() - 1));

        // The headline: what the FIRST picked line did over the window, because
        // that is the question a graph is nearly always being asked and the one
        // a reader should not have to work out from an axis.
        String moved = "nothing picked", movedBy = "";
        String tone = Palette.TEXT_SPENT;
        if (!historyPicked.isEmpty()) {
            String key = historyPicked.iterator().next();
            Trace t = traceFor(key);
            double[] all = historyValues(h, key);
            double first = Double.NaN, last = Double.NaN;
            for (int i = from; i < all.length; i++) {
                if (Double.isNaN(all[i])) continue;
                if (Double.isNaN(first)) first = all[i];
                last = all[i];
            }
            if (Double.isNaN(first)) {
                moved = "not recorded";
                movedBy = t.label() + ", over this window";
            } else {
                moved = changeText(t.unit(), first, last);
                movedBy = t.label() + ", over this window";
                tone = last > first ? Palette.GOOD : last < first ? Palette.WARN
                        : Palette.TEXT_SPENT;
            }
        }

        return vitalsBar(
                limitCell("RECORDED", String.format("%,d months", h.months()),
                        String.format("%,d years of city", h.months() / 12),
                        Palette.TEXT_HEAD),
                limitCell("IN VIEW", String.format("%,d months", drawn),
                        bucket > 1 ? "averaged " + bucket + " at a time" : span,
                        Palette.ACCENT),
                limitCell("DRAWING",
                        historyPicked.size() + " line"
                                + (historyPicked.size() == 1 ? "" : "s"),
                        "of " + TRACES.length + " the city keeps",
                        historyPicked.isEmpty() ? Palette.TEXT_SPENT : Palette.ACCENT),
                limitCell("IT MOVED", moved, movedBy, tone));
    }

    /** 10 years, 50 years, or the whole life of the city. */
    VBox historyRange(HistorySave h) {

        javafx.scene.layout.FlowPane row = new javafx.scene.layout.FlowPane(6, 6);
        row.setMaxWidth(GRAPH);
        row.setStyle("-fx-padding: 10 0 6 0;");

        int[] windows = {120, 600, Integer.MAX_VALUE};
        String[] names = {"10 years", "50 years",
                          String.format("all %,d months", h.months())};

        for (int i = 0; i < windows.length; i++) {
            final int window = windows[i];
            row.getChildren().add(chip("range:" + window, names[i], historyWindow == window,
                    () -> { historyWindow = window; showHistoryMenu(); }));
        }

        VBox box = new VBox(0, row);
        box.setMaxWidth(GRAPH);
        return box;
    }

    /**
     * How many months go into one drawn point.
     *
     * A LineChart draws a Path node per point, so four thousand months across
     * three lines is twelve thousand nodes and a screen that visibly hangs. The
     * range picker is the player's control over WHAT they look at; this is
     * about whether it can be drawn at all, and it only ever engages on windows
     * too wide to distinguish single months by eye anyway.
     */
    int bucketSize(int points) {
        return Math.max(1, (int) Math.ceil(points / (double) MAX_PLOT_POINTS));
    }

    /** A chip that is on or off, which is every control on this screen. */
    Label pickChip(String text, boolean on, Runnable act) {
        Label chip = new Label(text);
        chip.setStyle("-fx-padding: 4 10 4 10; -fx-cursor: hand;"
                + " -fx-background-radius: " + Palette.RADIUS_TIGHT + ";"
                + " -fx-background-color: " + (on ? Palette.RAISED : Palette.CONTROL) + ";"
                + " -fx-border-color: " + (on ? Palette.ACCENT : "transparent") + ";"
                + " -fx-border-width: 0 0 2 0;"
                + Palette.words(Palette.SIZE_CAPTION,
                        on ? Palette.TEXT_HEAD : Palette.TEXT_LABEL));
        chip.setOnMouseClicked(e -> act.run());
        return chip;
    }

    /* ----- a chip on this page, and the thing pressed held still (0.7.5) ----- */

    /** The page's scroller, kept so a click can be put back where it was after the rebuild. */
    private javafx.scene.control.ScrollPane page;

    /** The chip last pressed, by name, and how far down the window it was; the next rebuild spends it. */
    private String pressed;
    private double pressedAt = Double.NaN;

    /** This build's chips by name, so the rebuild can find the one that was pressed. */
    private final java.util.Map<String, javafx.scene.Node> named = new java.util.HashMap<>();

    /** The picker's filter box, so a redraw can hand the focus back to it; see showHistoryMenu. */
    private TextField filterField;

    /**
     * A chip on this page: pickChip, remembered by name, and held where it was
     * on the screen when pressed. Every act given here redraws the page, which
     * is what spends the press - a chip whose act does nothing is stillChip().
     */
    Label chip(String name, String text, boolean on, Runnable act) {
        Label chip = pickChip(text, on, act);
        named.put(name, chip);
        chip.setOnMouseClicked(e -> {
            pressed = name;
            pressedAt = chip.localToScene(0, 0).getY();
            act.run();
        });
        return chip;
    }

    /** A chip that only says something: lit or not, no act, and why on hover. */
    Label stillChip(String text, boolean on, String why) {
        Label chip = pickChip(text, on, () -> { });
        chip.setStyle(chip.getStyle() + " -fx-cursor: default;");
        tip(chip, why);
        return chip;
    }

    static void tip(javafx.scene.Node node, String text) {
        Tooltip tip = new Tooltip(text);
        tip.setShowDelay(Duration.millis(200));
        Tooltip.install(node, tip);
    }

    /**
     * Puts `node` back `wasAt` scene pixels down the window, once the page
     * has been laid out.
     *
     * The scroll memory keeps the page's distance from the BOTTOM, which is
     * right for a month's redraw and for a line picked in the picker (the
     * legend above it grows, nothing below it moves). It is wrong for a
     * group opening under the pointer, or a preset pressed above a legend
     * that changes length - both change the page below the thing pressed. So
     * the thing pressed is measured after the memory's own restore and the
     * page is moved by however far it drifted. Queued after the memory's
     * runLater (ui.scrolled() queues that first), so it has the last word; a
     * page too short to scroll is left alone.
     */
    private void holdInPlace(javafx.scene.Node node, double wasAt) {
        javafx.scene.control.ScrollPane scroller = page;
        if (scroller == null || node == null || Double.isNaN(wasAt)) return;
        javafx.application.Platform.runLater(() -> {
            // redrawn again since, or navigated away from: nothing to hold
            if (node.getScene() == null || scroller.getScene() == null) return;
            scroller.applyCss();
            scroller.layout();
            javafx.geometry.Bounds view = scroller.getViewportBounds();
            javafx.scene.Node content = scroller.getContent();
            if (view == null || content == null) return;
            double span = content.getLayoutBounds().getHeight() - view.getHeight();
            if (span <= 1) return;
            double drifted = node.localToScene(0, 0).getY() - wasAt;
            if (Math.abs(drifted) < 0.5) return;
            scroller.setVvalue(Math.max(0, Math.min(1, scroller.getVvalue() + drifted / span)));
        });
    }

    /**
     * The units of the picked lines, each once, in the order they were picked.
     *
     * One is a real axis; two are two real axes, the first unit's on the left;
     * three or more are each line's own low-to-high. Revenue against surplus
     * is a comparison; revenue against the sick rate is not, and pretending
     * otherwise is what the 0-100 scale is for.
     */
    List<String> pickedUnits() {
        List<String> units = new ArrayList<>();
        for (String key : historyPicked) {
            String u = traceFor(key).unit();
            if (!units.contains(u)) units.add(u);
        }
        return units;
    }

    /* =====================================================================
       THE PINS (0.7.5)

       Jerus: "there are two graphs always displayed on the graph rail, the
       real gdp yearly figure, and the other is the population" - and then
       "pinnable defaults, but not fixed". So two small charts, one line each
       in its own units, over the same window as the big one; they start as
       real GDP and the population, any reading below the big chart can be
       pinned in, and the older of the two pins is the one that goes, so
       there are always two. Which two is a preference (GamePrefs), not a
       fact about this city, so it follows the player from city to city and
       never reaches a save.
       ===================================================================== */

    /** How tall a pinned chart is. */
    static final double SMALL_CHART = 150;

    /**
     * The line on a small chart. A pin naming a line this page does not draw
     * - a series renamed or dropped since the preference was written - falls
     * back to that side's default, and the right never repeats the left.
     */
    String pinned(boolean left) {
        String key = left ? ui.prefs.getPinnedLeft() : ui.prefs.getPinnedRight();
        if (!known(key)) key = left ? GamePrefs.DEFAULT_PINNED_LEFT : GamePrefs.DEFAULT_PINNED_RIGHT;
        if (!left && key.equals(pinned(true))) {
            key = key.equals(GamePrefs.DEFAULT_PINNED_RIGHT)
                    ? GamePrefs.DEFAULT_PINNED_LEFT : GamePrefs.DEFAULT_PINNED_RIGHT;
        }
        return key;
    }

    /** Whether this page draws a line by that name. */
    static boolean known(String key) {
        for (Trace t : TRACES) if (t.key().equals(key)) return true;
        return false;
    }

    /**
     * Pins a line. THE OLDER PIN GOES, and the right is always the newer: the
     * right-hand line moves over to the left, the left-hand one is dropped,
     * and the new one takes the right. Saved at once, the way fullScreen is.
     */
    void pin(String key) {
        String left = pinned(true), right = pinned(false);
        if (key.equals(left) || key.equals(right)) return;
        ui.prefs.setPinnedLeft(right);
        ui.prefs.setPinnedRight(key);
        ui.prefs.save(ui.game.getGameFiles());
    }

    /** What unpinning a side would put back: its default, or the other default if that one is showing beside it. */
    String unpinned(boolean left) {
        String back = left ? GamePrefs.DEFAULT_PINNED_LEFT : GamePrefs.DEFAULT_PINNED_RIGHT;
        if (back.equals(pinned(!left))) {
            back = left ? GamePrefs.DEFAULT_PINNED_RIGHT : GamePrefs.DEFAULT_PINNED_LEFT;
        }
        return back;
    }

    void unpin(boolean left) {
        String back = unpinned(left);
        if (left) ui.prefs.setPinnedLeft(back);
        else ui.prefs.setPinnedRight(back);
        ui.prefs.save(ui.game.getGameFiles());
    }

    /** The two small charts, side by side. */
    HBox pinnedCharts(HistorySave h, List<int[]> recessions) {
        double wide = (GRAPH - Palette.GAP_LOOSE) / 2;
        HBox row = new HBox(Palette.GAP_LOOSE,
                smallChart(h, true, wide, recessions),
                smallChart(h, false, wide, recessions));
        row.setMaxWidth(GRAPH);
        row.setStyle("-fx-padding: 4 0 4 0;");
        return row;
    }

    /** One pinned line: its name and latest reading, then the line in its own units. */
    VBox smallChart(HistorySave h, boolean left, double wide, List<int[]> recessions) {

        String key = pinned(left);
        Trace t = traceFor(key);
        List<Integer> months = h.getMonth();
        int from = Math.max(0, months.size() - historyWindow);
        int bucket = bucketSize(months.size() - from);
        int[] at = bucketMonths(months, from, bucket);
        double[] all = historyValues(h, key);
        double[] shown = bucketed(all, from, bucket, at.length);

        double latest = Double.NaN;
        for (int i = all.length - 1; i >= from; i--) {
            if (!Double.isNaN(all[i])) { latest = all[i]; break; }
        }

        Label name = new Label(t.label());
        name.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        Label reads = new Label(Double.isNaN(latest) ? "not recorded" : fmtUnit(t.unit(), latest));
        reads.setStyle(Palette.figure(Palette.SIZE_BODY,
                Double.isNaN(latest) ? Palette.TEXT_SPENT : Palette.TEXT_HEAD));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox head = new HBox(Palette.GAP, name, gap, reads);
        head.setAlignment(Pos.CENTER_LEFT);
        head.setPrefWidth(wide);
        head.setMaxWidth(wide);
        head.setStyle("-fx-padding: 0 4 0 4;");

        // Jerus: "pinnable defaults, but not fixed" - so a pin can be taken
        // off, and taking it off puts the default back.
        String back = unpinned(left);
        if (!back.equals(key)) {
            Label unpin = chip(left ? "unpin:left" : "unpin:right", "unpin", false,
                    () -> { unpin(left); showHistoryMenu(); });
            tip(unpin, "Puts " + traceFor(back).label() + " back here.");
            head.getChildren().add(unpin);
        }
        // Real GDP can be drawn in layers (0.7.6), whichever side it is pinned to.
        boolean layered = LAYERED.equals(key) && gdpLayers;
        if (LAYERED.equals(key)) head.getChildren().add(layersChip(left ? "left" : "right"));

        NumberAxis x = monthAxis(months, from);
        x.setLabel(null);
        x.setTickLabelsVisible(false);    // the window is the big chart's, and the vitals say it
        NumberAxis y = new NumberAxis();
        Plot chart = new Plot(x, y, wide, SMALL_CHART);

        javafx.scene.chart.XYChart.Series<Number, Number> line =
                new javafx.scene.chart.XYChart.Series<>();
        line.setName(t.label());
        double low = Double.MAX_VALUE, high = -Double.MAX_VALUE;
        for (int b = 0; b < at.length; b++) {
            if (Double.isNaN(shown[b])) continue;
            double v = plotScale(t.unit(), shown[b]);
            line.getData().add(new javafx.scene.chart.XYChart.Data<>(at[b], v));
            low = Math.min(low, v);
            high = Math.max(high, v);
        }
        chart.getData().add(line);
        styleLine(line, layered ? LAYERED_LINE : Palette.ACCENT);

        double[][] layers = layered ? layerValues(h, from, bucket, at.length) : null;
        if (layered) {
            double[] reach = stackReach(layers, t.unit());
            low = Math.min(low, reach[0]);
            high = Math.max(high, reach[1]);
        }
        rangeAxis(y, t.unit(), low, high, false, null, 4);
        chart.shade(recessions);

        VBox box = new VBox(2, head);
        if (layered) {
            javafx.scene.chart.StackedAreaChart<Number, Number> under =
                    layersBehind(chart, months, from, at, layers, t.unit(), wide, SMALL_CHART, SMALL_Y_AXIS);
            rangeAxis((NumberAxis) under.getYAxis(), t.unit(), low, high, false, null, 4);
            box.getChildren().add(stacked(chart, under, wide, SMALL_CHART));
            box.getChildren().add(layersKey(layers, wide));
        } else {
            box.getChildren().add(chart);
        }
        box.setPrefWidth(wide);
        box.setMaxWidth(wide);
        return box;
    }

    /* =====================================================================
       GDP IN LAYERS (0.7.6)

       Jerus: "have it so the gdp graph can be a toggle, and if toggled it
       switches from line to mountain graph is it? layered, aka showing how
       much is made up of investments, net exports, government spending, aka
       breaking it down."

       A "layers" chip on the real-GDP small chart, and on the big chart's
       reading when real GDP is picked alone. Toggled, the chart draws
       CONSUMPTION, INVESTMENT AND GOVERNMENT STACKED FROM ZERO, in three
       steps of one Palette ramp, and the real GDP line over the stack in the
       ink the headings are - so THE GAP BETWEEN THE STACK'S TOP AND THE LINE
       IS NET EXPORTS: the line above the stack when the city sells the world
       more than it buys, below it when not. A StackedAreaChart cannot hold a
       negative layer, and net exports go negative whenever the city buys
       more than it sells; drawing them as the gap reads the truth without
       pretending it can. The layers and the line are the same money - YearBook.realYear()
       beside realGdpYear(), a rolling year in founding money - so on a city
       that has kept the parts, the stack plus the gap is the line.

       THE STACK IS A SECOND CHART BEHIND THE FIRST, not a LineChart that
       fakes areas: a StackedAreaChart the same size, on the same months and
       the same value axis ranged the same way, its axes kept but made
       invisible (opacity, for the reason the big chart's second axis is),
       its y-axis held to the same width as the line's, and the line chart's
       plot made transparent over it. The recession shading, the crosshair
       and the episode marks stay the line chart's, as they were.

       Older saves have no parts until they play a month, and no year of
       them until twelve: the layers start where the series do, and the line
       is drawn as it always was.
       ===================================================================== */

    /** The one line this page can draw in layers. */
    static final String LAYERED = "realGdp";

    /** What the GDP line is drawn in over the layers: the headings' ink, which no step of the blue ramp is near. */
    static final String LAYERED_LINE = Palette.TEXT_HEAD;

    /** How wide a small chart's y-axis is held when a stack is drawn behind it, so the two plots line up. */
    static final double SMALL_Y_AXIS = 56;

    /** What each part is called on the key and in the crosshair, in YearBook.GDP_PARTS' order. */
    static final String[] LAYER_NAMES = { "consumption", "investment", "government", "net exports" };

    /** Whether the big chart draws its one line in layers: real GDP picked alone, with the toggle on. */
    boolean bigInLayers() {
        return gdpLayers && historyPicked.size() == 1 && historyPicked.contains(LAYERED);
    }

    /** The chip that toggles the layers, on the small chart's head and on the big chart's reading. */
    Label layersChip(String where) {
        Label c = chip("layers:" + where, "layers", gdpLayers, () -> {
            gdpLayers = !gdpLayers;
            showHistoryMenu();
        });
        tip(c, gdpLayers
                ? "Back to the line alone."
                : "Draws what real GDP is made of under its line: consumption, investment and "
                  + "government stacked, and the gap between the stack and the line is net exports.");
        return c;
    }

    /** GDP's four parts, a rolling year in founding money each, averaged into the drawn points - C, I, G, then NX. */
    static double[][] layerValues(HistorySave h, int from, int bucket, int points) {
        double[][] out = new double[YearBook.GDP_PARTS.length][];
        for (int p = 0; p < out.length; p++) {
            out[p] = bucketed(YearBook.realYear(h, YearBook.GDP_PARTS[p]), from, bucket, points);
        }
        return out;
    }

    /**
     * The lowest and highest the stack reaches, in the axis's units: from
     * zero to its top, and below zero wherever a layer is negative (a month
     * of run-down stock is negative investment). Net exports are not in it;
     * they are the gap.
     */
    static double[] stackReach(double[][] layers, String unit) {
        double low = 0, high = 0;
        for (int b = 0; b < layers[0].length; b++) {
            double sum = 0;
            for (int p = 0; p < 3; p++) {
                if (Double.isNaN(layers[p][b])) continue;
                sum += plotScale(unit, layers[p][b]);
                low = Math.min(low, sum);
                high = Math.max(high, sum);
            }
        }
        return new double[] { low, high };
    }

    /**
     * The three layers as a chart to stand behind `front`: the same size, the
     * same months, the axes there and invisible, the y-axis held to `axisWide`
     * on both, no legend and no mouse. The caller ranges its y-axis exactly
     * as the front's, and stacks the two with stacked().
     */
    javafx.scene.chart.StackedAreaChart<Number, Number> layersBehind(Plot front, List<Integer> months,
            int from, int[] at, double[][] layers, String unit, double wide, double tall, double axisWide) {

        NumberAxis frontX = (NumberAxis) front.getXAxis();
        NumberAxis x = monthAxis(months, from);
        x.setLabel(frontX.getLabel());
        x.setTickLabelsVisible(frontX.isTickLabelsVisible());
        x.setOpacity(0);
        NumberAxis y = new NumberAxis();
        y.setOpacity(0);

        javafx.scene.chart.StackedAreaChart<Number, Number> under =
                new javafx.scene.chart.StackedAreaChart<>(x, y);
        under.setCreateSymbols(false);
        under.setAnimated(false);
        under.setLegendVisible(false);
        under.setMouseTransparent(true);
        under.setMinSize(wide, tall);
        under.setPrefSize(wide, tall);
        under.setMaxSize(wide, tall);
        under.setStyle(front.getStyle());
        for (javafx.scene.chart.XYChart<Number, Number> c
                : List.<javafx.scene.chart.XYChart<Number, Number>>of(front, under)) {
            c.getYAxis().setMinWidth(axisWide);
            c.getYAxis().setPrefWidth(axisWide);
            c.getYAxis().setMaxWidth(axisWide);
        }

        for (int p = 0; p < 3; p++) {
            javafx.scene.chart.XYChart.Series<Number, Number> layer =
                    new javafx.scene.chart.XYChart.Series<>();
            layer.setName(LAYER_NAMES[p]);
            for (int b = 0; b < at.length; b++) {
                if (Double.isNaN(layers[p][b])) continue;
                layer.getData().add(new javafx.scene.chart.XYChart.Data<>(at[b], plotScale(unit, layers[p][b])));
            }
            under.getData().add(layer);
            styleArea(layer, Palette.GDP_LAYERS[p]);
        }

        // The line's own plot goes clear over the stack, and its grid with it:
        // the stack's chart draws the grid underneath.
        front.setHorizontalGridLinesVisible(false);
        front.setVerticalGridLinesVisible(false);
        front.setAlternativeRowFillVisible(false);
        front.setAlternativeColumnFillVisible(false);
        javafx.scene.Node ground = front.lookup(".chart-plot-background");
        if (ground != null) ground.setStyle("-fx-background-color: transparent;");
        return under;
    }

    /** The stack behind, the line chart in front, in one pane of the chart's size. */
    static StackPane stacked(Plot front, javafx.scene.chart.StackedAreaChart<Number, Number> under,
                             double wide, double tall) {
        StackPane pane = new StackPane(under, front);
        pane.setAlignment(Pos.TOP_LEFT);
        pane.setMinSize(wide, tall);
        pane.setPrefSize(wide, tall);
        pane.setMaxSize(wide, tall);
        return pane;
    }

    /**
     * Paints one layer: its fill in its ramp step, a little translucent so
     * the grid shows through, and its edge in the step itself. A stacked
     * area's node is a group of the fill and the edge, made when the series
     * is added; styled now or as soon as it exists, as styleLine() does.
     */
    void styleArea(javafx.scene.chart.XYChart.Series<Number, Number> layer, String colour) {
        Color c = Color.web(colour);
        String fill = String.format("-fx-fill: rgba(%d,%d,%d,0.78);",
                (int) Math.round(c.getRed() * 255), (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue() * 255));
        String edge = "-fx-stroke: " + colour + "; -fx-stroke-width: 1px;";
        Runnable paint = () -> {
            if (!(layer.getNode() instanceof javafx.scene.Group group)) return;
            for (javafx.scene.Node part : group.getChildren()) {
                if (part.getStyleClass().contains("chart-series-area-line")) part.setStyle(edge);
                else part.setStyle(fill + " -fx-stroke: transparent;");
            }
        };
        if (layer.getNode() != null) paint.run();
        else javafx.application.Platform.runLater(paint);
    }

    /**
     * The key under a layered chart - a swatch per layer and one for the
     * line - and the sentence that says how to read the gap. When no part
     * has a year behind it yet, it says that instead of drawing an empty key.
     */
    VBox layersKey(double[][] layers, double wide) {
        boolean any = false;
        for (int p = 0; p < 3 && !any; p++) {
            for (double v : layers[p]) if (!Double.isNaN(v)) { any = true; break; }
        }
        javafx.scene.layout.FlowPane key = new javafx.scene.layout.FlowPane(10, 2);
        key.setPrefWrapLength(wide);
        for (int p = 0; p < 3; p++) key.getChildren().add(keySwatch(Palette.GDP_LAYERS[p], LAYER_NAMES[p]));
        key.getChildren().add(keySwatch(LAYERED_LINE, "real GDP, the line"));
        Label says = new Label(any
                ? "The line is GDP; the gap to the stack is net exports, negative below it."
                : "The line is GDP. Its parts have not been kept for a year yet - this city was "
                  + "played before they were - so the layers start a year after they did.");
        says.setWrapText(true);
        says.setMaxWidth(wide);
        says.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        VBox box = new VBox(2, key, says);
        box.setMaxWidth(wide);
        box.setStyle("-fx-padding: 2 4 4 4;");
        return box;
    }

    /* =====================================================================
       THE BIG CHART (0.7.5)
       ===================================================================== */

    /** How tall the big chart is. */
    static final double BIG_CHART = 380;

    /**
     * How wide each y-axis is held when there are two. The two charts stacked
     * for two units are laid out separately, and their plots only line up if
     * each leaves the other's axis the same room - so both axes are this wide
     * and each chart is padded by it on the side the other's axis is on.
     */
    static final double Y_AXIS = 76;

    /** How strongly a recession is shaded: enough to see, not enough to read as a colour. */
    static final double RECESSION_SHADE = 0.12;

    /** Room kept at the right of the preset row for "clear all" and "log". */
    static final double CONTROLS = 130;

    /**
     * The row above the big chart: the presets on the left, "clear all" and
     * "log" on the right. Jerus: "'clear all' should just be beside the graph".
     */
    HBox chartControls(HistorySave h) {

        javafx.scene.layout.FlowPane presets = new javafx.scene.layout.FlowPane(6, 6);
        presets.setPrefWrapLength(GRAPH - CONTROLS);
        presets.setMaxWidth(GRAPH - CONTROLS);
        for (int i = 0; i < PRESETS.length; i++) {
            Preset p = PRESETS[i];
            boolean on = historyPicked.size() == p.keys().length
                    && historyPicked.containsAll(java.util.Arrays.asList(p.keys()));
            Label c = chip("preset:" + i, p.name(), on, () -> {
                historyPicked.clear();
                historyPicked.addAll(java.util.Arrays.asList(p.keys()));
                showHistoryMenu();
            });
            tip(c, p.blurb());
            presets.getChildren().add(c);
        }

        Label clear = chip("clear", "clear all", false, () -> {
            historyPicked.clear();
            showHistoryMenu();
        });
        tip(clear, "Takes every line off the big chart. The two small ones stay.");

        String refused = logRefusal(h, Math.max(0, h.months() - historyWindow));
        Label log;
        if (refused == null) {
            log = chip("log", "log", historyLog, () -> {
                historyLog = !historyLog;
                showHistoryMenu();
            });
            tip(log, historyLog
                    ? "On a log scale: equal steps up the axis are equal multiples. Press for the plain scale."
                    : "Draws the big chart on a log scale, where steady growth is a straight line.");
        } else {
            log = stillChip("log", false, "Off: " + refused);
            log.setStyle(log.getStyle() + " " + Palette.fill(Palette.TEXT_FAINT));
        }

        HBox right = new HBox(6, clear, log);
        right.setAlignment(Pos.TOP_RIGHT);
        right.setMinWidth(Region.USE_PREF_SIZE);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox row = new HBox(Palette.GAP, presets, gap, right);
        row.setPrefWidth(GRAPH);
        row.setMaxWidth(GRAPH);
        row.setStyle("-fx-padding: 12 0 6 0;");
        return row;
    }

    /**
     * Why the log scale cannot draw what is picked, or null when it can.
     *
     * A logarithm has no zero and no negatives, and a deficit or an empty
     * treasury is exactly that - so one such month in the window and the
     * chip says so rather than drawing a line with a hole in it. Three units
     * are ranks on 0-100, which a log scale has nothing to say about.
     */
    String logRefusal(HistorySave h, int from) {
        List<String> units = pickedUnits();
        if (units.isEmpty()) return "nothing is drawn.";
        if (bigInLayers()) return "real GDP is drawn in layers, stacked from zero, and a log scale has no zero.";
        if (units.size() > 2) {
            return "three or more units are each drawn low-to-high, and a log scale has nothing to measure there.";
        }
        for (String key : historyPicked) {
            double[] all = historyValues(h, key);
            for (int i = from; i < all.length; i++) {
                if (!Double.isNaN(all[i]) && all[i] <= 0) {
                    return traceFor(key).label() + " reaches zero or below in this window, and a log scale has no zero.";
                }
            }
        }
        return null;
    }

    /**
     * The big chart: the picked lines on one axis, two, or each its own
     * low-to-high; the recessions shaded behind them; a crosshair that reads
     * every line at the month under the pointer; and the named episodes
     * marked under it.
     */
    VBox historyChart(HistorySave h, List<int[]> recessions, List<YearBook.Episode> episodes) {

        List<Integer> months = h.getMonth();
        int from = Math.max(0, months.size() - historyWindow);
        int bucket = bucketSize(months.size() - from);
        int[] at = bucketMonths(months, from, bucket);

        List<String> keys = new ArrayList<>(historyPicked);
        List<String> units = pickedUnits();
        boolean squashed = units.size() > 2;
        boolean log = historyLog && logRefusal(h, from) == null;

        Plot base = new Plot(monthAxis(months, from), new NumberAxis(), GRAPH, BIG_CHART);

        /*
         * THE SECOND AXIS. LineChart has one y-axis, so a second unit is drawn
         * on a second LineChart stacked over the first: transparent, its y-axis
         * on the right, its x-axis the same months and invisible (opacity, not
         * visibility - XYChart sets an axis visible itself on every layout, and
         * an invisible axis must still take the same height or the two plots'
         * bottoms part company). Both y-axes are held Y_AXIS wide and each
         * chart is padded by Y_AXIS where the other's axis stands, so the two
         * plot areas cover the same pixels. It takes no mouse events; the
         * first chart takes them for both.
         */
        Plot right = null;
        if (units.size() == 2) {
            right = new Plot(monthAxis(months, from), new NumberAxis(), GRAPH, BIG_CHART);
            for (Plot p : new Plot[] {base, right}) {
                p.getYAxis().setMinWidth(Y_AXIS);
                p.getYAxis().setPrefWidth(Y_AXIS);
                p.getYAxis().setMaxWidth(Y_AXIS);
            }
            base.setStyle("-fx-padding: 4 " + (4 + Y_AXIS) + " 4 4;");
            right.setStyle("-fx-padding: 4 4 4 " + (4 + Y_AXIS) + ";");
            right.getYAxis().setSide(javafx.geometry.Side.RIGHT);
            right.getXAxis().setOpacity(0);
            right.setMouseTransparent(true);
            right.setHorizontalGridLinesVisible(false);
            right.setVerticalGridLinesVisible(false);
            right.setHorizontalZeroLineVisible(false);
            right.setVerticalZeroLineVisible(false);
            right.setAlternativeRowFillVisible(false);
            right.setAlternativeColumnFillVisible(false);
            javafx.scene.Node ground = right.lookup(".chart-plot-background");
            if (ground != null) ground.setStyle("-fx-background-color: transparent;");
        }

        // Real GDP alone with the toggle on is drawn over its layers (0.7.6).
        boolean layered = bigInLayers();
        double[][] layers = layered ? layerValues(h, from, bucket, at.length) : null;

        // Each line averaged into the drawn points, in its own stored units -
        // what the crosshair reads, whatever the axis has done to it.
        double[][] shown = new double[keys.size()][];
        double[] low = {Double.MAX_VALUE, Double.MAX_VALUE};
        double[] high = {-Double.MAX_VALUE, -Double.MAX_VALUE};

        for (int k = 0; k < keys.size(); k++) {

            Trace t = traceFor(keys.get(k));
            double[] all = historyValues(h, keys.get(k));
            shown[k] = bucketed(all, from, bucket, at.length);
            int side = squashed ? 0 : units.indexOf(t.unit());

            // Normalised against THIS window, not the whole history: a decade
            // that is flat next to the founding boom should look flat, and it
            // does not if the scale is set by a spike off the left of the screen.
            double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE;
            for (int i = from; i < all.length; i++) {
                if (Double.isNaN(all[i])) continue;
                lo = Math.min(lo, all[i]);
                hi = Math.max(hi, all[i]);
            }
            boolean flat = hi <= lo;

            javafx.scene.chart.XYChart.Series<Number, Number> line =
                    new javafx.scene.chart.XYChart.Series<>();
            line.setName(t.label());
            for (int b = 0; b < at.length; b++) {
                // NaN is "not being recorded yet", which is not zero - see
                // HistorySave.aligned(). A bucket of nothing draws nothing.
                double v = shown[k][b];
                if (Double.isNaN(v)) continue;
                double plotted = squashed ? (flat ? 50 : (v - lo) / (hi - lo) * 100)
                        : log ? Math.log10(plotScale(t.unit(), v))
                        : plotScale(t.unit(), v);
                line.getData().add(new javafx.scene.chart.XYChart.Data<>(at[b], plotted));
                low[side] = Math.min(low[side], plotted);
                high[side] = Math.max(high[side], plotted);
            }
            (side == 1 ? right : base).getData().add(line);
            styleLine(line, layered ? LAYERED_LINE : TRACE_COLOURS[k % TRACE_COLOURS.length]);
        }

        /*
         * REAL GDP ALONE, IN LAYERS (0.7.6): the stack behind the line, the
         * axis reaching from the stack's floor to whichever of the two is
         * higher. See GDP IN LAYERS.
         */
        if (layered) {
            double[] reach = stackReach(layers, units.get(0));
            low[0] = Math.min(low[0], reach[0]);
            high[0] = Math.max(high[0], reach[1]);
        }

        if (keys.isEmpty()) {
            rangeAxis(base.yAxis(), null, 1, 0, false, "nothing picked", 8);
        } else if (squashed) {
            rangeAxis(base.yAxis(), null, low[0], high[0], false, "low to high, each line its own", 8);
        } else {
            rangeAxis(base.yAxis(), units.get(0), low[0], high[0], log, axisLabel(units.get(0), log), 8);
            if (right != null) {
                rangeAxis(right.yAxis(), units.get(1), low[1], high[1], log, axisLabel(units.get(1), log), 8);
            }
        }

        javafx.scene.chart.StackedAreaChart<Number, Number> under = null;
        if (layered) {
            under = layersBehind(base, months, from, at, layers, units.get(0), GRAPH, BIG_CHART, Y_AXIS);
            rangeAxis((NumberAxis) under.getYAxis(), units.get(0), low[0], high[0], false,
                    axisLabel(units.get(0), false), 8);
        }

        base.shade(recessions);
        base.withCursor();

        /* ----- the crosshair: a line at the month under the pointer, and what every line read there ----- */
        Pane over = new Pane();
        over.setMinSize(GRAPH, BIG_CHART);
        over.setPrefSize(GRAPH, BIG_CHART);
        over.setMaxSize(GRAPH, BIG_CHART);
        over.setMouseTransparent(true);

        VBox box = new VBox(1);
        box.setStyle(Palette.block(Palette.PINNED, Palette.EDGE) + " -fx-padding: 4 8 4 8;");
        box.setVisible(false);
        Label when = new Label();
        when.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_HEAD) + " -fx-font-weight: bold;");
        box.getChildren().add(when);
        Label[] reads = new Label[keys.size()];
        for (int k = 0; k < keys.size(); k++) {
            reads[k] = new Label();
            reads[k].setStyle(Palette.figure(Palette.SIZE_CAPTION,
                    layered ? LAYERED_LINE : TRACE_COLOURS[k % TRACE_COLOURS.length]));
            box.getChildren().add(reads[k]);
        }
        // ...and in layers, what each part read there: the three layers in
        // their steps, net exports - the gap - in the muted ink.
        Label[] partReads = new Label[layered ? YearBook.GDP_PARTS.length : 0];
        for (int p = 0; p < partReads.length; p++) {
            partReads[p] = new Label();
            partReads[p].setStyle(Palette.figure(Palette.SIZE_CAPTION,
                    p < 3 ? Palette.GDP_LAYERS[p] : Palette.TEXT_MUTED));
            box.getChildren().add(partReads[p]);
        }
        over.getChildren().add(box);

        StackPane stack = new StackPane();
        stack.setAlignment(Pos.TOP_LEFT);
        if (under != null) stack.getChildren().add(under);
        stack.getChildren().add(base);
        if (right != null) stack.getChildren().add(right);
        stack.getChildren().add(over);
        stack.setMinSize(GRAPH, BIG_CHART);
        stack.setPrefSize(GRAPH, BIG_CHART);
        stack.setMaxSize(GRAPH, BIG_CHART);

        NumberAxis x = (NumberAxis) base.getXAxis();
        Region yAxis = base.getYAxis();
        stack.setOnMouseMoved(e -> {
            // Inside the plot, measured on the axes themselves: the x-axis spans
            // the plot's width, the y-axis its height.
            javafx.geometry.Point2D across = x.sceneToLocal(e.getSceneX(), e.getSceneY());
            javafx.geometry.Point2D down = yAxis.sceneToLocal(e.getSceneX(), e.getSceneY());
            if (at.length == 0 || across == null || down == null
                    || across.getX() < 0 || across.getX() > x.getWidth()
                    || down.getY() < 0 || down.getY() > yAxis.getHeight()) {
                base.hideCursor();
                box.setVisible(false);
                return;
            }
            // The nearest DRAWN point, so a bucketed chart reads the bucket's
            // month and the bucket's average - what the line actually shows.
            int b = nearest(at, x.getValueForDisplay(across.getX()).doubleValue());
            base.showCursor(at[b]);
            when.setText(CityCalendar.formatShort(at[b])
                    + (bucket > 1 ? "  (" + bucket + " months averaged)" : ""));
            for (int k = 0; k < keys.size(); k++) {
                Trace t = traceFor(keys.get(k));
                reads[k].setText(t.label() + "  " + (Double.isNaN(shown[k][b])
                        ? "not recorded" : fmtUnit(t.unit(), shown[k][b])));
            }
            for (int p = 0; p < partReads.length; p++) {
                partReads[p].setText("  " + LAYER_NAMES[p] + "  " + (Double.isNaN(layers[p][b])
                        ? "not recorded" : fmtUnit(units.get(0), layers[p][b])));
            }
            javafx.geometry.Point2D line = over.sceneToLocal(
                    x.localToScene(x.getDisplayPosition(at[b]), 0));
            javafx.geometry.Point2D top = over.sceneToLocal(yAxis.localToScene(0, 0));
            box.setVisible(true);
            box.applyCss();
            double w = box.prefWidth(-1), tall = box.prefHeight(w);
            box.resize(w, tall);
            // beside the line, on whichever side has room
            double left = line.getX() + 10 + w <= GRAPH ? line.getX() + 10 : line.getX() - 10 - w;
            box.relocate(Math.max(0, left), top.getY() + 6);
        });
        stack.setOnMouseExited(e -> {
            base.hideCursor();
            box.setVisible(false);
        });

        /* ----- the named episodes: a tick under the chart where each began, and their names ----- */
        Pane strip = new Pane();
        strip.setMinSize(GRAPH, 12);
        strip.setPrefSize(GRAPH, 12);
        strip.setMaxSize(GRAPH, 12);

        int first = months.get(from), last = months.get(months.size() - 1);
        List<String> names = new ArrayList<>();
        for (YearBook.Episode ep : episodes) {
            if (ep.fromMonth() < first || ep.fromMonth() > last) continue;
            names.add(ep.name());
            Region tick = new Region();
            tick.setMinSize(10, 12);
            tick.setPrefSize(10, 12);
            tick.setMaxSize(10, 12);
            // ten pixels to hover, two to see
            tick.setStyle("-fx-background-color: " + Palette.TEXT_MUTED + ";"
                    + " -fx-background-insets: 0 4 0 4; -fx-cursor: hand;");
            tip(tick, ep.name() + "\n" + CityCalendar.formatShort(ep.fromMonth())
                    + " to " + CityCalendar.formatShort(ep.toMonth()));
            base.mark(strip, ep.fromMonth(), tick);
        }

        VBox chart = new VBox(0, stack, strip);
        chart.setMaxWidth(GRAPH);
        if (layered) chart.getChildren().add(layersKey(layers, GRAPH));
        if (!names.isEmpty()) {
            // Oldest first, at most eight, and the rest counted rather than dropped.
            int shownNames = Math.min(8, names.size());
            String said = String.join("  ·  ", names.subList(0, shownNames))
                    + (names.size() > shownNames ? "  ·  and " + (names.size() - shownNames) + " more" : "");
            Label caption = new Label(said);
            caption.setWrapText(true);
            caption.setMaxWidth(GRAPH);
            caption.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)
                    + " -fx-padding: 2 0 4 0;");
            chart.getChildren().add(caption);
        }
        return chart;
    }

    /** What an axis is called: the line's own name when it carries one line, its unit when more. */
    String axisLabel(String unit, boolean log) {
        String only = null;
        int on = 0;
        for (String key : historyPicked) {
            if (traceFor(key).unit().equals(unit)) { only = traceFor(key).label(); on++; }
        }
        return (on == 1 ? only : unitName(unit, ui.game.getCurrency())) + (log ? ", log scale" : "");
    }

    /**
     * The months of the window, first to last - and the same on every chart
     * on the page.
     *
     * SET, NOT AUTO-RANGED (0.7.5). Two charts stacked for two units must agree
     * about the month to the pixel, and a pinned chart covers exactly the
     * window the big one does; an axis that ranges itself off its own data
     * would give a line that began late a different axis from its neighbour.
     */
    static NumberAxis monthAxis(List<Integer> months, int from) {
        NumberAxis x = new NumberAxis();
        x.setLabel("month");
        /*
         * NumberAxis forces zero into its range by default, and on a graph of a
         * city's LATER years that is most of the chart wasted. A 120-month
         * window on a 322-month city drew months 203-322 across the right third
         * and left two thirds of empty grid to the left of it - which also
         * squashes every line into a corner. The axis should show the window,
         * not the origin.
         */
        x.setForceZeroInRange(false);
        x.setAutoRanging(false);
        int first = months.get(from), last = months.get(months.size() - 1);
        x.setLowerBound(first);
        x.setUpperBound(Math.max(last, first + 1));
        x.setTickUnit(Math.max(1, niceStep((last - first) / 8.0)));
        return x;
    }

    /**
     * Ranges a value axis over what is drawn on it, and labels it.
     *
     * @param unit  what every line on this axis is measured in, or null when
     *              they are mapped onto 0-100 (or nothing is drawn)
     * @param low   the lowest value drawn on it, already through plotScale()
     *              (and log10 when log); low above high means nothing is drawn
     * @param log   whether the values are logarithms
     * @param ticks about how many gridlines to aim for
     */
    static void rangeAxis(NumberAxis y, String unit, double low, double high,
                          boolean log, String label, int ticks) {
        y.setLabel(label);
        y.setForceZeroInRange(false);
        y.setAutoRanging(false);

        if (low > high) {
            /*
             * NOTHING PICKED, so there is nothing to range against - and an
             * auto-ranging axis with no data invents 0-100 on both sides and
             * labels the months -1.0 to 1.0, which reads as a broken chart
             * rather than an empty one. The window is known whether or not
             * anything is drawn on it (monthAxis), so the frame stays honest
             * and only the plot is bare.
             */
            y.setLowerBound(0);
            y.setUpperBound(100);
            y.setTickUnit(20);
            y.setTickLabelsVisible(false);
            return;
        }
        if (unit == null) {
            // Normalised: the values ARE nought to a hundred, so say so
            // rather than padding a scale that has no units to pad.
            y.setLowerBound(0);
            y.setUpperBound(100);
            y.setTickUnit(10);
            return;
        }
        if (log) {
            /*
             * A LOG AXIS IS A NUMBER AXIS OF LOGARITHMS. JavaFX has no log
             * axis, so the lines are drawn as log10 of the value, the bounds
             * snap to whole powers of ten, and each gridline is labelled with
             * the value it stands for - "$1M", "10%" - through the same
             * axisTick() a plain axis uses. The crosshair reads the stored
             * value, never the logarithm.
             */
            double lo = Math.floor(low), hi = Math.ceil(high);
            if (hi <= lo) hi = lo + 1;
            y.setLowerBound(lo);
            y.setUpperBound(hi);
            y.setTickUnit(1);
            final String logUnit = unit;
            y.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
                @Override public String toString(Number n) {
                    double real = Math.pow(10, n.doubleValue());
                    return axisTick(logUnit, real, real);
                }
                @Override public Number fromString(String text) { return 0; }
            });
            return;
        }
        /*
         * THE RANGE IS SET HERE RATHER THAN LEFT TO THE AXIS.
         *
         * NumberAxis auto-ranging on a real-unit chart put the whole of "The
         * budget" preset between -$55M and -$15M - three deficit spikes filled
         * the range and revenue, which is positive, was drawn off the top of
         * the plot. A chart that silently omits one of its own lines is worse
         * than no chart, and the fix is not to trust the axis with a question
         * this screen can answer itself.
         *
         * Six per cent of headroom top and bottom so a line that touches its
         * extreme is not drawn along the frame.
         */
        double pad = high > low ? (high - low) * .06
                                : Math.max(1, Math.abs(high) * .1);
        double step = niceStep((high + pad - (low - pad)) / ticks);
        y.setLowerBound(Math.floor((low - pad) / step) * step);
        y.setUpperBound(Math.ceil((high + pad) / step) * step);
        y.setTickUnit(step);
        /*
         * AND THE TICKS SAY WHAT THEY ARE.
         *
         * niceStep() fixed the STEP - 1, 2 or 5 times a power of ten, so the
         * gridlines are numbers a reader can add up. It did nothing about the
         * MAGNITUDE, so the budget preset drew an axis of 500,000,000 /
         * 0 / -500,000,000 / -1,000,000,000 and the population one drew
         * 100,000 under a chart eight hundred pixels wide. Every other money
         * figure in the game has been abbreviated since the units pass; the one
         * place with no room for the digits was the one still printing them.
         *
         * Only when the lines AGREE about their unit - which is why this is
         * after the two returns above. With the units mapped onto 0-100 the
         * axis is a rank, not a quantity, and "$50M" on it would be a lie
         * about a number that means nothing.
         *
         * THE STEP DECIDES THE DECIMALS, not the value. The first cut asked
         * each label how big it was, and a price axis came out reading "$0.00
         * $500.00  $1k" - three conventions on one ruler, because 0 is small,
         * 500 is medium and 1000 is large. How fine the gridlines are is a
         * property of the AXIS, so it is the step that is asked: a land axis
         * stepping by $20 wants no cents and one stepping by $0.50 wants two,
         * and every label on it agrees either way.
         */
        final String tickUnit = unit;
        final double tickStep = step;
        y.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
            @Override public String toString(Number n) {
                return axisTick(tickUnit, n.doubleValue(), tickStep);
            }
            @Override public Number fromString(String text) { return 0; }
        });
    }

    /** The month each drawn point stands for - the middle of its bucket. */
    static int[] bucketMonths(List<Integer> months, int from, int bucket) {
        int points = (months.size() - from + bucket - 1) / bucket;
        int[] at = new int[points];
        for (int b = 0; b < points; b++) {
            at[b] = months.get(Math.min(from + b * bucket + bucket / 2, months.size() - 1));
        }
        return at;
    }

    /** A line averaged into those points; NaN where a bucket had nothing recorded. */
    static double[] bucketed(double[] all, int from, int bucket, int points) {
        double[] out = new double[points];
        for (int b = 0; b < points; b++) {
            int i = from + b * bucket;
            double sum = 0;
            int n = 0;
            for (int j = i; j < Math.min(i + bucket, all.length); j++) {
                if (Double.isNaN(all[j])) continue;
                sum += all[j];
                n++;
            }
            out[b] = n == 0 ? Double.NaN : sum / n;
        }
        return out;
    }

    /** The drawn point nearest a month. */
    static int nearest(int[] at, double month) {
        int best = 0;
        for (int b = 1; b < at.length; b++) {
            if (Math.abs(at[b] - month) < Math.abs(at[best] - month)) best = b;
        }
        return best;
    }

    /**
     * A LineChart that also draws what its lines sit on and what points at them.
     *
     * JavaFX gives a chart's plot area to its subclasses only (getPlotChildren),
     * and that is the one place a band can sit BEHIND the lines and still move
     * with the axis: an overlay on top would tint the lines, and one placed by
     * hand would drift from the axis it was measured against. So the recession
     * bands, the crosshair's line and the episode ticks are all positioned in
     * layoutPlotChildren(), from the axis's own getDisplayPosition(), every
     * time the chart lays out.
     */
    static final class Plot extends javafx.scene.chart.LineChart<Number, Number> {

        /** Runs of months shaded as recession, {first, last}, and the shapes drawn for them. */
        private final List<int[]> bands = new ArrayList<>();
        private final List<javafx.scene.shape.Rectangle> bandShapes = new ArrayList<>();

        /** The crosshair's line, over the lines once withCursor() has moved it there. */
        private final javafx.scene.shape.Line cursor = new javafx.scene.shape.Line();

        /** Ticks outside the chart - in a strip laid out under it - at the month each belongs to. */
        private final List<Integer> markMonths = new ArrayList<>();
        private final List<Region> marks = new ArrayList<>();
        private Pane markStrip;

        Plot(NumberAxis x, NumberAxis y, double width, double height) {
            super(x, y);
            setCreateSymbols(false);   // a dot per month is unreadable and slow
            setAnimated(false);        // and an animation per redraw is worse
            setLegendVisible(false);   // the readings below carry real values
            setMinSize(width, height);
            setPrefSize(width, height);
            setMaxSize(width, height);
            cursor.setStroke(Color.web(Palette.TEXT_MUTED));
            cursor.setStrokeWidth(1);
            cursor.setMouseTransparent(true);
            cursor.setVisible(false);
            getPlotChildren().add(cursor);
        }

        NumberAxis yAxis() { return (NumberAxis) getYAxis(); }

        /** Shades these runs of months, {first, last}, behind the lines. */
        void shade(List<int[]> runs) {
            for (int[] run : runs) {
                javafx.scene.shape.Rectangle band = new javafx.scene.shape.Rectangle();
                band.setFill(Color.web(Palette.TEXT_MUTED, RECESSION_SHADE));
                band.setMouseTransparent(true);
                bands.add(run);
                bandShapes.add(band);
                getPlotChildren().add(0, band);    // first child, so under every line
            }
        }

        /** Moves the crosshair's line over the lines - call after they are added. */
        void withCursor() {
            getPlotChildren().remove(cursor);
            getPlotChildren().add(cursor);
        }

        /** The crosshair at a month, in the plot's own coordinates; no layout pass needed. */
        void showCursor(double month) {
            double at = Math.round(getXAxis().getDisplayPosition(month)) + 0.5;
            cursor.setStartX(at);
            cursor.setEndX(at);
            cursor.setStartY(0);
            cursor.setEndY(getYAxis().getHeight());
            cursor.setVisible(true);
        }

        void hideCursor() { cursor.setVisible(false); }

        /**
         * A tick at a month, in `strip` - a pane laid out beside this chart,
         * under it - positioned whenever the chart lays out.
         */
        void mark(Pane strip, int month, Region tick) {
            markStrip = strip;
            markMonths.add(month);
            marks.add(tick);
            strip.getChildren().add(tick);
        }

        @Override protected void layoutPlotChildren() {
            super.layoutPlotChildren();
            NumberAxis x = (NumberAxis) getXAxis();
            double lower = x.getLowerBound(), upper = x.getUpperBound();
            double tall = getYAxis().getHeight();

            // A run is its months, so it covers half a month either side of
            // their points; clipped to the window, and hidden when outside it.
            for (int i = 0; i < bands.size(); i++) {
                javafx.scene.shape.Rectangle band = bandShapes.get(i);
                double a = Math.max(lower, bands.get(i)[0] - 0.5);
                double b = Math.min(upper, bands.get(i)[1] + 0.5);
                band.setVisible(b > a);
                if (b <= a) continue;
                double left = x.getDisplayPosition(a), right = x.getDisplayPosition(b);
                band.setX(left);
                band.setY(0);
                band.setWidth(Math.max(1, right - left));
                band.setHeight(tall);
            }
            if (cursor.isVisible()) cursor.setEndY(tall);

            // The ticks live outside the chart, so their position goes through
            // the scene: the axis's pixel for the month, as the strip sees it.
            for (int i = 0; i < marks.size(); i++) {
                Region tick = marks.get(i);
                javafx.geometry.Point2D inStrip = markStrip.sceneToLocal(
                        x.localToScene(x.getDisplayPosition(markMonths.get(i)), 0));
                if (inStrip == null) continue;
                tick.relocate(inStrip.getX() - tick.getPrefWidth() / 2, 0);
            }
        }
    }

    /**
     * One gridline's label: short enough to fit, honest about its unit.
     *
     * The value has already been through plotScale(), so money is in dollars
     * rather than the model's thousands and a percentage is out of a hundred.
     * That is what makes this a formatter and not a converter - it does no
     * arithmetic on the number, only on how many characters it spends.
     *
     * Steps are always 1, 2 or 5 times a power of ten, so one decimal place is
     * the most any label can need, and trim() takes the ".0" off the ones that
     * do not need it: "$500M" rather than "$500.0M".
     */
    static String axisTick(String unit, double v, double step) {
        double a = Math.abs(v);
        String sign = v < 0 ? "-" : "";
        return switch (unit) {
            case "money"     -> sign + shortCash(a);
            // Somebody else's money, and it says so - the same rule fmtUnit
            // follows, for the same reason.
            case "usd"       -> sign + "US" + shortCash(a);
            // Prices, which live in the range where the cents can be the news.
            // Ground is the world's price, in its money since 0.7.6.
            case "land"      -> sign + "US" + priceTick(a, step);
            case "unitprice", "rent", "share" -> sign + priceTick(a, step);
            case "percent"   -> sign + trim(a) + "%";
            case "ratio"     -> sign + trim(a) + "x";
            // A currency needs its small moves; three places is the axis's
            // share of fmtUnit's four.
            case "rate"      -> sign + String.format("%.3f", a);
            case "index"     -> sign + String.format("%.2f", a);
            case "count"     -> sign + (a >= 1000 ? shortCount(a) : trim(a));
            default          -> sign + trim(a);
        };
    }

    /**
     * A price on an axis: cents only when the gridlines are finer than a dollar.
     *
     * Abbreviated later than money is, because a price axis is read against the
     * prices on the screens beside it and those say "$1,250". The threshold is
     * the STEP again rather than the value: materials step by $5,000 and read
     * $0 / $5k / $10k, while food steps by $500 and reads $0 / $500 / $1,000.
     * Asking each value gave one axis both conventions at once.
     */
    static String priceTick(double a, double step) {
        if (step >= 1_000) return shortCash(a);
        if (step < 1) return String.format("$%.2f", a);
        return "$" + formatter.format(Math.round(a));
    }

    /** Dollars, abbreviated from a thousand up - an axis has no room for digits. */
    static String shortCash(double a) {
        if (a >= 1_000_000_000) return "$" + trim(a / 1_000_000_000) + "B";
        if (a >= 1_000_000)     return "$" + trim(a / 1_000_000) + "M";
        if (a >= 1_000)         return "$" + trim(a / 1_000) + "k";
        return "$" + trim(a);
    }

    /** People, homes, jobs - the same abbreviation without the dollar. */
    static String shortCount(double a) {
        if (a >= 1_000_000_000) return trim(a / 1_000_000_000) + "B";
        if (a >= 1_000_000)     return trim(a / 1_000_000) + "M";
        return trim(a / 1_000) + "k";
    }

    /** One decimal at most, and none at all when it would read ".0". */
    static String trim(double a) {
        String s = String.format("%.1f", a);
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
    }

    /**
     * One, two or five times a power of ten - the only steps a reader can add up.
     *
     * Dividing the range by eight gives ticks at -13,019,917.2, which is a
     * correct number and an unreadable axis. Rounding the STEP and then snapping
     * the bounds outward to it costs a little empty margin and buys labels
     * somebody can hold in their head.
     */
    static double niceStep(double raw) {
        if (!(raw > 0)) return 1;
        double power = Math.pow(10, Math.floor(Math.log10(raw)));
        double n = raw / power;
        return (n <= 1 ? 1 : n <= 2 ? 2 : n <= 5 ? 5 : 10) * power;
    }

    /** What the y-axis is measured in, when every line agrees - the rate in this city's own money (0.7.10). */
    static String unitName(String unit, Currency money) {
        return switch (unit) {
            case "money"     -> "dollars a month";
            case "percent"   -> "per cent";
            case "count"     -> "how many";
            case "ratio"     -> "times";
            case "rate"      -> money.rateUnit();
            case "usd"       -> "US dollars";
            case "index"     -> "index, founding = 1";
            case "land"      -> "US dollars a square foot";
            case "unitprice" -> "dollars a unit";
            case "rent"      -> "dollars a head a month";
            case "share"     -> "dollars a founding share";
            default          -> unit;
        };
    }

    /**
     * The stored value, in the units the axis is labelled in.
     *
     * The model counts money in thousands and rates as fractions, and an axis
     * that reads 0.03 for a 3% rate is an axis nobody can use. fmtUnit() already
     * knows every one of these conversions for text; this is the same table for
     * a number.
     */
    static double plotScale(String unit, double v) {
        return switch (unit) {
            case "money", "usd", "land", "unitprice", "rent", "share" -> v * 1000;
            case "percent" -> v * 100;
            default        -> v;
        };
    }

    /**
     * Paints one line, now or as soon as it has a node.
     *
     * A series' Path is created when the chart lays the series out, which has
     * usually happened by the time the series is added and occasionally has
     * not. Colouring only when it happens to be ready would give a chart whose
     * lines sometimes did not match its own legend, which is worse than an
     * uncoloured chart.
     */
    void styleLine(javafx.scene.chart.XYChart.Series<Number, Number> line,
                           String colour) {
        String css = "-fx-stroke: " + colour + "; -fx-stroke-width: 2px;";
        if (line.getNode() != null) {
            line.getNode().setStyle(css);
        } else {
            javafx.application.Platform.runLater(() -> {
                if (line.getNode() != null) line.getNode().setStyle(css);
            });
        }
    }

    /**
     * One line's reading: what it is now, and what it did.
     *
     * The old table gave first / latest / low / high in four monospaced columns
     * and left the reader to subtract. The move is the answer, so the move is
     * the second-largest thing on the row.
     */
    VBox historyReading(HistorySave h, String key, String colour, String axis) {

        Trace t = traceFor(key);
        double[] all = historyValues(h, key);
        List<Integer> months = h.getMonth();
        int from = Math.max(0, months.size() - historyWindow);

        double first = Double.NaN, last = Double.NaN;
        double lo = Double.MAX_VALUE, hi = -Double.MAX_VALUE;
        for (int i = from; i < all.length; i++) {
            if (Double.isNaN(all[i])) continue;
            if (Double.isNaN(first)) first = all[i];
            last = all[i];
            lo = Math.min(lo, all[i]);
            hi = Math.max(hi, all[i]);
        }

        Region swatch = new Region();
        swatch.setMinSize(9, 9);
        swatch.setPrefSize(9, 9);
        swatch.setMaxSize(9, 9);
        swatch.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 2;");

        Label name = new Label(t.label());
        name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));

        HBox left = new HBox(6, swatch, name);
        left.setAlignment(Pos.CENTER_LEFT);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        boolean nothing = Double.isNaN(first);

        Label reads = new Label(nothing ? "not recorded here" : fmtUnit(t.unit(), last));
        reads.setStyle(Palette.figure(Palette.SIZE_BODY,
                nothing ? Palette.TEXT_SPENT : Palette.TEXT_HEAD));
        reads.setPrefWidth(130);
        reads.setMinWidth(130);
        reads.setAlignment(Pos.CENTER_RIGHT);

        Label move = new Label(nothing ? "" : changeText(t.unit(), first, last));
        move.setStyle(Palette.figure(Palette.SIZE_BODY,
                last > first ? Palette.GOOD : last < first ? Palette.WARN
                        : Palette.TEXT_SPENT));
        move.setPrefWidth(96);
        move.setMinWidth(96);
        move.setAlignment(Pos.CENTER_RIGHT);

        // Jerus: "pinnable defaults, but not fixed" - any line here can go up
        // onto a small chart, and one already there says so.
        boolean up = key.equals(pinned(true)) || key.equals(pinned(false));
        Label pin = up
                ? stillChip("pinned", true, "Already on a small chart above.")
                : chip("pin:" + key, "pin", false, () -> { pin(key); showHistoryMenu(); });
        if (!up) tip(pin, "Puts this line on a small chart above, in place of the older of the two.");

        HBox row = new HBox(Palette.GAP_LOOSE, left, gap, reads, move, pin);
        // Real GDP picked alone can go into layers on the big chart (0.7.6).
        if (LAYERED.equals(key) && historyPicked.size() == 1) {
            row.getChildren().add(row.getChildren().size() - 1, layersChip("reading"));
        }
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(GRAPH);
        row.setPrefWidth(GRAPH);

        Label under = new Label(nothing
                ? "This city was played before the game kept that number."
                : String.format("      from %s   ·   low %s   ·   high %s",
                        fmtUnit(t.unit(), first), fmtUnit(t.unit(), lo),
                        fmtUnit(t.unit(), hi))
                  + (axis == null ? "" : "   ·   " + axis));
        under.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_SPENT));

        VBox box = new VBox(0, row, under);
        box.setMaxWidth(GRAPH);
        box.setStyle("-fx-padding: 4 0 5 0;");
        return box;
    }

    /**
     * How far it moved, in terms the unit deserves.
     *
     * A rate that went from 2% to 3% did not rise 50%; it rose a point. Points
     * for anything already a proportion, per cent for anything that is a
     * quantity, and a plain difference where a proportion of the thing would be
     * meaningless.
     */
    String changeText(String unit, double first, double last) {
        double delta = last - first;
        switch (unit) {
            case "percent":
                return String.format("%+.1f pts", delta * 100);
            case "ratio": case "index": case "rate":
                return String.format("%+.3f", delta);
            default:
                if (Math.abs(first) < 1e-9) return delta == 0 ? "no change" : "from nothing";
                double share = delta / Math.abs(first);
                // A city that grew its output a thousandfold did not grow it by
                // "+136683.6%". Past a fivefold move the multiple is the figure
                // a reader can hold, and the percentage is noise with a sign on
                // it.
                if (Math.abs(share) >= 5) return String.format("\u00d7%,.0f", last / first);
                return String.format("%+.1f%%", share * 100);
        }
    }

    /**
     * The chips, one heading per group - each group closed until it is
     * wanted, and a box over them that finds a line by name (0.7.5).
     *
     * Jerus: "have it be a collapsable list ... and you can then expand the
     * list to click on the specific stuff you want." A hundred chips in ten
     * groups was the whole bottom of the page, and a player looking for one
     * line read all of them. So a group shows its name and how many of its
     * lines are picked, and opens on a click; a group with a line picked
     * opens by itself, so what is on the chart is always in view. Typing in
     * the box narrows every group to the lines whose names match and opens
     * the ones that have any. Open and closed is screen state, like the
     * picks: kept while the game runs, never saved.
     *
     * BUCKETED RATHER THAN WALKED IN ORDER, and that is a fix rather than a
     * preference. The old version started a new heading whenever the group
     * changed from one entry to the next, which is only correct while the array
     * happens to be sorted by group - and it was not: schoolBill sat between two
     * PEOPLE entries, so the screen drew MONEY, PEOPLE, MONEY, PEOPLE and the
     * player got two boxes with the same name.
     *
     * A LinkedHashMap keeps the groups in the order they are first mentioned, so
     * the layout is still decided by the TRACES array and nothing had to be
     * reordered to fix it.
     */
    VBox historyPickerRows() {

        TextField find = new TextField(historyFilter);
        find.setPromptText("type to find a line");
        find.setMinWidth(260);
        find.setPrefWidth(260);
        find.setMaxWidth(260);
        find.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_HEAD) + " -fx-padding: 4 8 4 8;");
        // Never handed the focus by the window - only by a click into it. The
        // page is rebuilt every month, and when the button that had the focus
        // goes with it JavaFX gives the focus to the first control that will
        // take it; a text box that took it would switch off every shortcut
        // (space, the arrows, P) on this page without the player touching it.
        find.setFocusTraversable(false);
        VBox.setMargin(find, new javafx.geometry.Insets(4, 0, 4, 0));

        VBox groups = new VBox(2);
        groups.setMaxWidth(GRAPH);
        fillPicker(groups);

        // Refilled IN PLACE as the box is typed in, not by redrawing the page:
        // a redraw makes a new box, and the key after next would go to
        // whatever had the focus instead. The box itself is held where it was
        // while the groups under it open and close.
        find.textProperty().addListener((o, was, now) -> {
            double wasAt = find.localToScene(0, 0).getY();
            historyFilter = now == null ? "" : now;
            fillPicker(groups);
            holdInPlace(find, wasAt);
        });
        // Enter hands the keys back to the page, so the window's shortcuts work again.
        find.setOnAction(e -> { if (page != null) page.requestFocus(); });
        filterField = find;

        VBox all = new VBox(0, find, groups);
        all.setMaxWidth(GRAPH);
        return all;
    }

    /** The groups, as the filter and the player have left them. */
    void fillPicker(VBox groups) {

        groups.getChildren().clear();
        String find = historyFilter.trim().toLowerCase(java.util.Locale.ROOT);
        boolean finding = !find.isEmpty();

        java.util.Map<String, List<Trace>> byGroup = new java.util.LinkedHashMap<>();
        for (Trace t : TRACES) byGroup.computeIfAbsent(t.group(), g -> new ArrayList<>()).add(t);

        for (java.util.Map.Entry<String, List<Trace>> entry : byGroup.entrySet()) {
            String group = entry.getKey();
            List<Trace> traces = entry.getValue();

            int picked = 0;
            List<Trace> matching = new ArrayList<>();
            for (Trace t : traces) {
                if (historyPicked.contains(t.key())) picked++;
                if (!finding || t.label().toLowerCase(java.util.Locale.ROOT).contains(find)) matching.add(t);
            }
            if (matching.isEmpty()) continue;

            boolean open = finding || groupOpen.getOrDefault(group, picked > 0);
            String heading = (open ? "\u25BE  " : "\u25B8  ") + group;
            // While a filter is typed every match is open, so the heading has nothing to toggle.
            Label head = finding
                    ? stillChip(heading, true, "Open while the box above has something in it.")
                    : chip("group:" + group, heading, open, () -> {
                        groupOpen.put(group, !open);
                        showHistoryMenu();
                    });
            Label count = new Label(picked + " of " + traces.size() + " picked");
            count.setStyle(Palette.words(Palette.SIZE_CAPTION,
                    picked > 0 ? Palette.TEXT_MUTED : Palette.TEXT_FAINT));
            HBox header = new HBox(Palette.GAP, head, count);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setStyle("-fx-padding: 6 0 2 0;");
            groups.getChildren().add(header);

            if (!open) continue;
            javafx.scene.layout.FlowPane row = new javafx.scene.layout.FlowPane(5, 5);
            row.setPrefWrapLength(GRAPH);
            row.setMaxWidth(GRAPH);
            row.setStyle("-fx-padding: 2 0 6 14;");
            for (Trace t : matching) {
                row.getChildren().add(chip("line:" + t.key(), t.label(), historyPicked.contains(t.key()),
                        () -> {
                            if (!historyPicked.remove(t.key())) historyPicked.add(t.key());
                            // stays open under the pointer, even when its last line goes
                            groupOpen.put(group, true);
                            showHistoryMenu();
                        }));
            }
            groups.getChildren().add(row);
        }

        if (groups.getChildren().isEmpty()) {
            groups.getChildren().add(sentence("No line is called that.", Palette.TEXT_MUTED));
        }
    }

    Trace traceFor(String key) {
        for (Trace t : TRACES) if (t.key().equals(key)) return t;
        return new Trace(key, key, "", "count");
    }

    /**
     * A series, aligned to the month axis, derived ones included.
     *
     * NOTHING DERIVED IS STORED. Unemployment is the workforce and the jobs,
     * GDP per capita is GDP and the population - and a stored copy of either is
     * a second number that can disagree with the two it came from, with nothing
     * to say which is right. They are computed here, on the way to the screen,
     * from the aligned series so a month missing from one of the inputs is
     * missing from the result rather than dividing by a zero that was never
     * recorded.
     */
    double[] historyValues(HistorySave h, String key) {
        switch (key) {
            /*
              * THE CHART READS THE SAME DEFINITION THE YEAR BOOK DOES.
              *
              * This case used to be (workforce - jobs) / workforce, struck
              * here and struck again in YearBook, and both were wrong the same
              * way: `workforce` still carries the students and the prisoners,
              * and `jobs` is posts OFFERED. On a slot-3 city it drew 5.5%
              * unemployment for seventy years against out-of-work ledgers that
              * were empty and eleven thousand posts nobody could fill - the
              * line was the student count wearing an unemployment label.
              *
              * PopulationManager.getLabourForce() carries a comment saying
              * this exact mistake was found and fixed once already, on the
              * People screen, in September. It came back a layer up because
              * the chart had its own copy. It does not have one now.
              */
            case "unemployment":    return YearBook.unemployment(h);
            case "labourForce":     return YearBook.labourForce(h);
            /*
             * REAL GDP, A ROLLING YEAR OF IT - output with the price level
             * divided out and twelve months summed. Jerus, 2026-09-14: "i want
             * real gdp, aka a graph that shows inflation adjusted gdp", then
             * "make real gdp a rolling figure, not the monthly snapshot". It was
             * struck here and again in YearBook; since 0.7.5 it is struck once
             * there, where the recession bands are read off the same line - see
             * YearBook.realGdp() and realGdpYear() for the why of each half.
             * gdpPerCapita annualises one month by multiplying by twelve, which
             * is the cheap version of this and is why it jumps about; that line
             * is left alone for now.
             */
            case "realGdp":         return YearBook.realGdpYear(h);
            case "gdpPerCapita": {
                double[] g = h.aligned("gdp"), p = h.aligned("population");
                double[] out = new double[g.length];
                // Annualised, to match the figure on the accounts screen. A
                // monthly per-capita number is correct and unrecognisable.
                for (int i = 0; i < out.length; i++) {
                    out[i] = p[i] > 0 ? g[i] * 12 / p[i] : Double.NaN;
                }
                return out;
            }
            /*
              * Over the posts that are FILLED, not over the workforce - see
              * YearBook.averageWage(). Dividing the wage bill by a workforce
              * that carries 96,000 students who are paid nothing is how the
              * line a player reads against rent came out low, and further out
              * the more the city studied.
              */
            case "averageWage":     return YearBook.averageWage(h);
            case "netMigration":    return minus(h.aligned("arrivals"), h.aligned("departures"));

            /*
             * THE RUNNING TOTALS OF THE DEAD (2026-09-11). Jerus: "track how
             * many of each category have died cumulative over time". Summed
             * here from the monthly series rather than stored, like everything
             * derived. The total runs from the founding; a band's series began
             * when it was first recorded, so its line starts there, at zero,
             * and is not drawn across months nobody was counting.
             */
            case "cumulative:deaths":
            case "cumulative:deathsBabies":
            case "cumulative:deathsChildren":
            case "cumulative:deathsTeens":
            case "cumulative:deathsAdults":
            case "cumulative:deathsElders":
            case "cumulative:deathsSeniors":
            case "cumulative:deathsOrphans":
            case "cumulative:deathsUnhoused":
            case "cumulative:deathsKilled":
                return HistorySave.runningTotal(h.aligned(key.substring("cumulative:".length())));
            case "naturalIncrease": return minus(h.aligned("births"), h.aligned("deaths"));

            /*
             * What the city's foreign debt is worth AT HOME, which is not what
             * it owes. The stored series is in dollars because that is what the
             * obligation is; multiplying by the rate of the same month is the
             * only honest translation, and it is why a devaluation shows up
             * here as the debt getting bigger without the city borrowing a
             * cent.
             */
            case "foreignDebtLocal": {
                double[] usd = h.aligned("foreignDebtUsd"), rate = h.aligned("fxRate");
                double[] out = new double[usd.length];
                for (int i = 0; i < out.length; i++) out[i] = usd[i] * rate[i];
                return out;
            }
            case "tradeBalance":
                return minus(h.aligned("exportsAbroad"), h.aligned("importsAbroad"));

            /*
             * YEAR ON YEAR, the same window PriceIndex uses - YearBook's one
             * definition since 0.7.5, which the book's column reads too.
             */
            case "inflation":       return YearBook.inflation(h);

            /*
             * Empty homes as a share of the homes standing. Negative when the
             * city is over-full - more households than homes, which is
             * doubling up rather than a bug - and left that way, because a
             * clamp here would hide the housing shortage this line exists to
             * show.
             */
            case "vacancy": {
                double[] built = h.aligned("homes"), hh = h.aligned("households");
                double[] out = new double[built.length];
                for (int i = 0; i < out.length; i++) {
                    out[i] = built[i] > 0 ? (built[i] - hh[i]) / built[i] : Double.NaN;
                }
                return out;
            }

            default:                return h.aligned(key);
        }
    }

    double[] minus(double[] a, double[] b) {
        double[] out = new double[a.length];
        for (int i = 0; i < out.length; i++) out[i] = a[i] - b[i];
        return out;
    }

    /** A value in the units it is actually kept in. */
    String fmtUnit(String unit, double v) {
        switch (unit) {
            // Thousands, like everything else the model counts - see money().
            case "money":     return tightMoney(v * 1000);
            case "percent":   return String.format("%.1f%%", v * 100);
            // Ground is kept per square foot in thousands, and the land screen
            // already shows it multiplied out. Two screens, one convention -
            // and in US dollars since 0.7.6, which is what the world asks.
            case "land":      return String.format("US$%.2f", v * 1000);
            // A world price - food, materials, ore - kept in thousands like
            // every other price in the game. Multiplied out for the same reason
            // rent is: printed raw it reads as cents.
            case "unitprice": return String.format("$%,.2f", v * 1000);
            case "ratio":     return String.format("%.2fx", v);
            // Four places, because the whole point of watching a currency is
            // the small moves - a rate that reads 1.20 for thirty months is a
            // rate nobody can see drifting.
            case "rate":      return String.format("%.4f", v);
            // Somebody else's money, and it says so. A dollar figure printed
            // with the same $ as the local one is the single most confusing
            // thing this screen could do.
            case "usd":       return "US" + tightMoney(v * 1000);
            case "index":     return String.format("%.3f", v);
            /*
             * Rent is a price per person of housing capacity, kept in thousands
             * like every other price in the game - about 0.08 of them. Printed
             * with the "money" unit it rounded to $0 for the whole history,
             * which is what the play-test showed. Multiplied out it is the
             * figure a player recognises: about $82 a head a month.
             */
            case "rent":      return String.format("$%.2f", v * 1000);
            // A share's price, kept in thousands like every price here, and
            // per FOUNDING share so a split is not a cliff on the chart.
            case "share":     return tightMoney(v * 1000, false);
            default:          return formatter.format(Math.round(v));
        }
    }

    /**
     * How wide the paragraph above the buyback table wraps.
     *
     * The table sizes itself - scrolled() pins it to its own preferred width -
     * so this only has to stop the explanatory line running the full width of a
     * maximised window and reading as a different column from the table under
     * it.
     */
    static final double TABLE_WIDTH = 660;

    /* =====================================================================
       THE GOODS ROW

       One good on the Reports tab's price table. It was built under THE
       STATEMENT (the land office's banner now, in LandScreen), on the
       statement primitives, and the history screen is the only thing that
       reads it.
       ===================================================================== */

    /**
     * One good: what it is, what it costs here, and what happened to it.
     *
     * Three columns rather than the statement line's two, because a price on
     * its own does not say whether it is dear - the band beside it does, and
     * the flow says whether anybody actually traded at it.
     */
    HBox goodsRow(Good g) {

        GoodsMarket m = ui.game.getMarkets().get(g);

        Label name = new Label(g.label());
        name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
        name.setMinWidth(150);
        name.setPrefWidth(150);

        double local = m == null ? 0 : m.getLocalPrice();
        Label price = new Label(local > 0 ? unitPrice(local) + " /" + g.unit() : "\u2014");
        price.setStyle(Palette.figure(Palette.SIZE_BODY,
                local > 0 ? Palette.TEXT_BODY : Palette.TEXT_MUTED));
        price.setMinWidth(130);
        price.setPrefWidth(130);

        StringBuilder said = new StringBuilder();
        if (!g.traded()) {
            said.append("priced by the seller");
        } else {
            said.append("band ");
            said.append(g.exportable() ? unitPrice(m.exportPrice()) : "no floor");
            said.append(" \u2013 ");
            said.append(g.importable() ? unitPrice(m.importPrice()) : "no ceiling");
        }
        if (m != null) {
            double in = m.getImported(), out = m.getExported();
            if (in  > .005) said.append("   \u00b7   imported ").append(String.format("%,.0f", in));
            if (out > .005) said.append("   \u00b7   exported ").append(String.format("%,.0f", out));
        }
        Label detail = new Label(said.toString());
        detail.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

        HBox row = new HBox(Palette.GAP_LOOSE, name, price, detail);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(GRAPH);
        row.setPrefWidth(GRAPH);
        row.setStyle("-fx-padding: 2 0 2 0;");
        return row;
    }
}
