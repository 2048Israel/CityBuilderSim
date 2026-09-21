package ham.citybuildersim.ui;

import ham.citybuildersim.*;
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
 * The Reports tab: the city as a shape over time.
 *
 * One chart with lines overlaid, each series in its own units or all of them
 * mapped onto 0-100 by their own range over the window, a legend that carries
 * the real values, presets for the questions a player usually has, and the
 * year book written out on request. The first screen split out of
 * UserInterface (2026-09-18), chosen because it is the most self-contained:
 * it reads the window's game and root, calls clearMenu() and scrolled(), and
 * nothing else in the shell reads it but the rail.
 *
 * The text is exactly what it was inside UserInterface - the two banners, THE
 * HISTORY SCREEN and THE RECORD, and everything under them - with the shell's
 * members reached through ui. Nothing was rewritten on the way. THE GOODS ROW
 * followed later the same day: it had sat under the shell's THE STATEMENT
 * banner, and this screen was the only thing that drew it.
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
       onto 0-100 by their OWN range over the window on screen. Not indexed to
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
        new Trace("bankPremium",    "Bank premium",       "CREDIT",     "percent"),
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
       things.

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

    void showHistoryMenu() {
        ui.clearMenu("showHistoryMenu", () -> showHistoryMenu());

        HistorySave h = ui.game.getHistorySave();

        /* =====================================================================
           SEEDED ONCE, AND THEN LEFT ALONE.

           Jerus: "if you have one clicked, and you unclick it, it automatically
           brings up three. That should not be the case, if zero clicked then
           the graph is just empty."

           This used to re-seed whenever the set was empty, which made "empty"
           unreachable: unticking the last line, or pressing "clear them all",
           put the first preset straight back. The screen was answering a
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

        column.getChildren().add(historyRange(h));
        column.getChildren().add(historyChart(h));

        column.getChildren().add(statementHead("What each line did", GRAPH));
        if (historyPicked.isEmpty()) {
            column.getChildren().add(sentence(
                    "Nothing is selected. Pick a preset or a line below.",
                    Palette.TEXT_MUTED));
        } else {
            int colour = 0;
            for (String key : historyPicked) {
                column.getChildren().add(historyReading(h, key,
                        TRACE_COLOURS[colour % TRACE_COLOURS.length]));
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
        column.getChildren().add(historyPresets());
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

        // kept from the BOTTOM: the legend above the picker grows a block per line
        // picked, and the picker is what the player is pressing - see keptScrollerFromBottom
        ui.rootMenu.getChildren().addAll(title, lead, historyVitals(h),
                ui.scrolled(column, 210, true));
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
            row.getChildren().add(pickChip(names[i], historyWindow == window,
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

    /**
     * True when every selected line is measured in the same thing.
     *
     * The difference between a real axis and a normalised one. Revenue against
     * surplus is a comparison; revenue against the sick rate is not, and
     * pretending otherwise is what the 0-100 scale is for.
     */
    String sharedUnit() {
        String unit = null;
        for (String key : historyPicked) {
            String u = traceFor(key).unit();
            if (unit == null) unit = u;
            else if (!unit.equals(u)) return null;
        }
        return unit;
    }

    javafx.scene.chart.LineChart<Number, Number> historyChart(HistorySave h) {

        String unit = sharedUnit();

        javafx.scene.chart.NumberAxis x = new javafx.scene.chart.NumberAxis();
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

        javafx.scene.chart.NumberAxis y = new javafx.scene.chart.NumberAxis();
        y.setLabel(historyPicked.isEmpty()
                ? "nothing picked"
                : unit != null
                        ? (historyPicked.size() == 1
                                ? traceFor(historyPicked.iterator().next()).label()
                                : unitName(unit))
                        : "low to high, each line its own");
        y.setForceZeroInRange(false);

        javafx.scene.chart.LineChart<Number, Number> chart =
                new javafx.scene.chart.LineChart<>(x, y);
        chart.setCreateSymbols(false);   // a dot per month is unreadable and slow
        chart.setAnimated(false);        // and an animation per redraw is worse
        chart.setLegendVisible(false);   // the readings below carry real values
        chart.setPrefSize(GRAPH, 380);
        chart.setMinSize(GRAPH, 380);
        chart.setMaxSize(GRAPH, 380);

        List<Integer> months = h.getMonth();
        int from = Math.max(0, months.size() - historyWindow);
        int bucket = bucketSize(months.size() - from);

        int colour = 0;
        for (String key : historyPicked) {

            double[] all = historyValues(h, key);
            javafx.scene.chart.XYChart.Series<Number, Number> line =
                    new javafx.scene.chart.XYChart.Series<>();
            line.setName(traceFor(key).label());

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
            boolean real = unit != null;

            for (int i = from; i < all.length; i += bucket) {
                double sum = 0;
                int n = 0;
                for (int j = i; j < Math.min(i + bucket, all.length); j++) {
                    // NaN is "not being recorded yet", which is not zero - see
                    // HistorySave.aligned(). A bucket of nothing draws nothing.
                    if (Double.isNaN(all[j])) continue;
                    sum += all[j];
                    n++;
                }
                if (n == 0) continue;

                double v = sum / n;
                double plotted = real ? plotScale(unit, v)
                        : (flat ? 50 : (v - lo) / (hi - lo) * 100);
                line.getData().add(new javafx.scene.chart.XYChart.Data<>(
                        months.get(Math.min(i + bucket / 2, months.size() - 1)), plotted));
            }

            chart.getData().add(line);
            styleLine(line, TRACE_COLOURS[colour % TRACE_COLOURS.length]);
            colour++;
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
        double low = Double.MAX_VALUE, high = -Double.MAX_VALUE;
        for (javafx.scene.chart.XYChart.Series<Number, Number> line : chart.getData()) {
            for (javafx.scene.chart.XYChart.Data<Number, Number> point : line.getData()) {
                double v = point.getYValue().doubleValue();
                low = Math.min(low, v);
                high = Math.max(high, v);
            }
        }
        if (low > high) {
            /*
             * NOTHING PICKED, so there is nothing to range against - and an
             * auto-ranging axis with no data invents 0-100 on both sides and
             * labels the months -1.0 to 1.0, which reads as a broken chart
             * rather than an empty one. The window is known whether or not
             * anything is drawn on it, so the frame stays honest and only the
             * plot is bare.
             */
            if (!months.isEmpty()) {
                x.setAutoRanging(false);
                x.setLowerBound(months.get(from));
                x.setUpperBound(months.get(months.size() - 1));
                x.setTickUnit(Math.max(1, niceStep(
                        (months.get(months.size() - 1) - months.get(from)) / 8.0)));
            }
            y.setAutoRanging(false);
            y.setLowerBound(0);
            y.setUpperBound(100);
            y.setTickUnit(20);
            y.setTickLabelsVisible(false);
        }
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
         * Only when the lines AGREE about their unit. With mixed units the
         * series are normalised to 0-100 and the axis is a rank, not a
         * quantity - "$50M" on that axis would be a lie about a number that
         * means nothing.
         */
        double tickStep = 10;

        if (low <= high) {
            y.setAutoRanging(false);
            if (unit == null) {
                // Normalised: the values ARE nought to a hundred, so say so
                // rather than padding a scale that has no units to pad.
                y.setLowerBound(0);
                y.setUpperBound(100);
                y.setTickUnit(10);
            } else {
                double pad = high > low ? (high - low) * .06
                                        : Math.max(1, Math.abs(high) * .1);
                double step = niceStep((high + pad - (low - pad)) / 8);
                y.setLowerBound(Math.floor((low - pad) / step) * step);
                y.setUpperBound(Math.ceil((high + pad) / step) * step);
                y.setTickUnit(step);
                tickStep = step;
            }
        }

        /*
         * THE STEP DECIDES THE DECIMALS, not the value.
         *
         * The first cut asked each label how big it was, and a price axis came
         * out reading "$0.00  $500.00  $1k" - three conventions on one ruler,
         * because 0 is small, 500 is medium and 1000 is large. How fine the
         * gridlines are is a property of the AXIS, so it is the step that is
         * asked: a land axis stepping by $20 wants no cents and one stepping by
         * $0.50 wants two, and every label on it agrees either way.
         */
        if (unit != null) {
            final String tickUnit = unit;
            final double step = tickStep;
            y.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
                @Override public String toString(Number n) {
                    return axisTick(tickUnit, n.doubleValue(), step);
                }
                @Override public Number fromString(String text) { return 0; }
            });
        }
        return chart;
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
            case "land", "unitprice", "rent", "share" -> sign + priceTick(a, step);
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

    /** What the y-axis is measured in, when every line agrees. */
    static String unitName(String unit) {
        return switch (unit) {
            case "money"     -> "dollars a month";
            case "percent"   -> "per cent";
            case "count"     -> "how many";
            case "ratio"     -> "times";
            case "rate"      -> Currency.rateUnit();
            case "usd"       -> "US dollars";
            case "index"     -> "index, founding = 1";
            case "land"      -> "dollars a square foot";
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
    VBox historyReading(HistorySave h, String key, String colour) {

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

        HBox row = new HBox(Palette.GAP_LOOSE, left, gap, reads, move);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(GRAPH);
        row.setPrefWidth(GRAPH);

        Label under = new Label(nothing
                ? "This city was played before the game kept that number."
                : String.format("      from %s   ·   low %s   ·   high %s",
                        fmtUnit(t.unit(), first), fmtUnit(t.unit(), lo),
                        fmtUnit(t.unit(), hi)));
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

    /** The questions a player actually has, each one a chart's worth of lines. */
    VBox historyPresets() {

        VBox box = new VBox(4);
        box.setMaxWidth(GRAPH);

        javafx.scene.layout.FlowPane row = new javafx.scene.layout.FlowPane(6, 6);
        row.setMaxWidth(GRAPH);
        row.setPrefWrapLength(GRAPH);

        for (Preset p : PRESETS) {
            boolean on = historyPicked.size() == p.keys().length
                    && historyPicked.containsAll(java.util.Arrays.asList(p.keys()));
            Label chip = pickChip(p.name(), on, () -> {
                historyPicked.clear();
                historyPicked.addAll(java.util.Arrays.asList(p.keys()));
                showHistoryMenu();
            });
            Tooltip tip = new Tooltip(p.blurb());
            tip.setShowDelay(Duration.millis(200));
            Tooltip.install(chip, tip);
            row.getChildren().add(chip);
        }

        box.getChildren().add(row);
        box.getChildren().add(statementNote(
                "Or build your own below. Lines measured in the same thing are drawn "
                + "against each other on a real axis; mix the units and the chart falls "
                + "back to each line's own low-to-high, which compares shapes rather "
                + "than sizes."));
        return box;
    }

    /**
     * The chips, one heading per group.
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

        VBox all = new VBox(2);
        all.setMaxWidth(GRAPH);

        java.util.Map<String, javafx.scene.layout.FlowPane> groups =
                new java.util.LinkedHashMap<>();

        for (Trace t : TRACES) {
            javafx.scene.layout.FlowPane row = groups.get(t.group());
            if (row == null) {
                Label g = new Label(t.group());
                g.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)
                        + " -fx-font-weight: bold; -fx-padding: 10 0 3 0;");
                all.getChildren().add(g);

                row = new javafx.scene.layout.FlowPane(5, 5);
                row.setPrefWrapLength(GRAPH);
                row.setMaxWidth(GRAPH);
                all.getChildren().add(row);
                groups.put(t.group(), row);
            }
            row.getChildren().add(pickChip(t.label(), historyPicked.contains(t.key()),
                    () -> {
                        if (!historyPicked.remove(t.key())) historyPicked.add(t.key());
                        showHistoryMenu();
                    }));
        }

        Label clear = pickChip("clear them all", false, () -> {
            historyPicked.clear();
            showHistoryMenu();
        });
        VBox.setMargin(clear, new javafx.geometry.Insets(12, 0, 0, 0));
        all.getChildren().add(clear);
        return all;
    }

    Trace traceFor(String key) {
        for (Trace t : TRACES) if (t.key().equals(key)) return t;
        return new Trace(key, key, "", "count");
    }

    /**
     * A trailing total over the last `window` readings.
     *
     * NaN until there are enough of them, deliberately: a "year of output"
     * drawn from seven months is not a year of output, and a line that starts
     * low and climbs for its first year would look like growth that never
     * happened. The same reason PriceIndex.hasRate() refuses to quote inflation
     * before there is a year of prices.
     *
     * A NaN inside the window poisons that window and nothing else - the sum
     * resumes as soon as twelve clean readings are behind it.
     */
    static double[] trailingSum(double[] series, int window) {
        double[] out = new double[series.length];
        for (int i = 0; i < series.length; i++) {
            if (i + 1 < window) { out[i] = Double.NaN; continue; }
            double sum = 0;
            boolean clean = true;
            for (int k = i - window + 1; k <= i; k++) {
                if (Double.isNaN(series[k])) { clean = false; break; }
                sum += series[k];
            }
            out[i] = clean ? sum : Double.NaN;
        }
        return out;
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
             * REAL GDP - output with the price level divided out.
             *
             * Jerus, 2026-09-14: "for the graphs, i want real gdp, aka a graph
             * that shows inflation adjusted gdp."
             *
             * Nominal GDP rises when the city makes more AND when the same
             * things cost more, and those are opposite news. This is the line
             * that tells them apart, in founding money: a city whose real GDP
             * is flat while the nominal one climbs has not grown, it has only
             * repriced.
             *
             * Divided by the INDEX rather than deflated month by month, because
             * the index is already a ratio to the founding basket - so this
             * comes out in the same money every other founding-money figure in
             * the game is quoted in, and is comparable across a currency reform
             * for the same reason the index is. See PriceIndex.redenominate().
             */
            case "realGdp": {
                double[] g = h.aligned("gdp"), p = h.aligned("priceIndex");
                double[] real = new double[g.length];
                for (int i = 0; i < real.length; i++) {
                    real[i] = p[i] > 0 ? g[i] / p[i] : Double.NaN;
                }
                /*
                 * A ROLLING YEAR, NOT A MONTH. Jerus, 2026-09-14: "make real
                 * gdp a rolling figure, not the monthly snapshot."
                 *
                 * A single month of this city's output is mostly noise - one
                 * mine opening, one mill shedding a shift, one month where the
                 * shops could not deliver - and a line made of it says nothing
                 * about whether the place is growing. Twelve months summed is
                 * what an economy's output actually means and is the figure
                 * every real statistics office publishes.
                 *
                 * Summed rather than averaged, so the number IS a year of
                 * output and sits in the same units as anything else quoted
                 * per year. gdpPerCapita annualises one month by multiplying by
                 * twelve, which is the cheap version of this and is why it
                 * jumps about; that line is left alone for now.
                 */
                return trailingSum(real, 12);
            }
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
             * YEAR ON YEAR, the same window PriceIndex uses, and for the same
             * reason: a month of this game contains a harvest and a shipping
             * bill, and the month-on-month rate is mostly those. The first
             * twelve months have no reading rather than a made-up zero.
             */
            case "inflation": {
                double[] idx = h.aligned("priceIndex");
                double[] out = new double[idx.length];
                for (int i = 0; i < out.length; i++) {
                    double then = i >= 12 ? idx[i - 12] : Double.NaN;
                    out[i] = then > 0 ? idx[i] / then - 1 : Double.NaN;
                }
                return out;
            }

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
            // already shows it multiplied out. Two screens, one convention.
            case "land":      return String.format("$%.2f", v * 1000);
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
