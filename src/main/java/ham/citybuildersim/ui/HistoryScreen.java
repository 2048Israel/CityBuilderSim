package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ham.citybuildersim.ui.Pieces.Look;
import ham.citybuildersim.ui.Pieces.Press;
import ham.citybuildersim.ui.Pieces.Rule;
import ham.citybuildersim.ui.Pieces.Run;
import ham.citybuildersim.ui.Pieces.ScaleRow;
import ham.citybuildersim.ui.Pieces.Tick;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * City History: the city as a shape over time.
 *
 * Since 0.7.37 the page is laid out at its own width, in Build's style
 * (THE PAGE AT ITS WIDTH): a head with "Write the year book" in it, a strip
 * about the city's life - its age, its hard times, what is running now and
 * the decisions it has made - then two pinned charts as cards, the big
 * chart, a card per line it draws, the hard times and decisions in view
 * (each a click that moves the chart there), the picker, and the month's
 * prices against the world's, folded.
 *
 * The charts: two small pinned ones, then one big chart with lines
 * overlaid - one unit on a real axis, two on two real axes, three or more
 * mapped onto 0-100 by their own range over the window - with recessions
 * shaded and named, the episodes on a lane under it and the player's
 * decisions as flags; a legend that carries the real values, presets for
 * the questions a player usually has, a picker folded into its groups, and
 * the year book written out on request (the page as of 0.7.5 is under THE
 * PAGE, REDRAWN; real GDP drawn in layers on a toggle since 0.7.6, under
 * GDP IN LAYERS). Since 0.7.23 the charts are TimeCharts - dragged, wheeled
 * and ranged like a market chart, with an overview and a full screen - over
 * one window of months (THE CHART, REBUILT; FULL SCREEN). The first screen
 * split out of UserInterface (2026-09-18), chosen because it is the most
 * self-contained: it reads the window's game and root, calls clearMenu()
 * and scrolled(), and nothing else in the shell reads it but the rail.
 *
 * The text came over exactly as it was inside UserInterface - the two banners,
 * THE HISTORY SCREEN and THE RECORD, and everything under them - with the
 * shell's members reached through ui. Nothing was rewritten on the way. The
 * goods table followed later the same day (it had sat under the shell's THE
 * STATEMENT banner); since 0.7.37 it is PRICES THIS MONTH.
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

    /**
     * The window of months the charts show (0.7.23): the big chart's, which
     * the two small ones follow - dragged, wheeled and ranged by the player,
     * and following the newest month while its right edge is on it
     * (ChartModel). It replaced historyWindow, the three chips' "10 years",
     * "50 years" and "all". Screen state, like the picks: kept while the game
     * runs, never saved.
     */
    final ChartModel chartWindow = new ChartModel();

    /** The lines the big chart's legend has switched off, by key: still picked and read below, not drawn (0.7.23). */
    final java.util.Set<String> hiddenLines = new java.util.HashSet<>();

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

    /**
     * The folds this page keeps open - the hard times by kind, and every
     * good's price - by name (0.7.37). Screen state like the picker's groups:
     * kept while the game runs, never saved.
     */
    final java.util.Set<String> folds = new java.util.HashSet<>();

    /**
     * What each pin and each reading card last showed, by "pin:side:key" or
     * "card:key" (0.7.37), so a month landing on the open page counts the
     * figure on from it and slides the range bar's dot (SectorScreen.countUp()).
     */
    private final java.util.Map<String, Double> shownBefore = new java.util.HashMap<>();

    /** The game month this page was last drawn at; -1 before its first draw. */
    private int drawnMonth = -1;

    /**
     * The month each named episode was first listed, by kind and first
     * month (0.7.37): an episode the list did not have at the previous draw
     * is NEW for the month it appears. Screen state, not saved; a jump
     * (a load, a skip of more than a month) takes the list as it finds it.
     */
    private final java.util.Map<String, Integer> episodeSeen = new java.util.HashMap<>();
    private int episodesSeenAt = -1;

    /** Where the next draw scrolls the page to - the chart's top, or the hard times - once it is laid out; null: the scroll memory's. */
    private String scrollTarget;
    /** The two places a draw can be asked to scroll to: the row above the big chart, and the hard times' heading. */
    static final String TO_CHART = "chart", TO_HARD_TIMES = "hard times";

    /** This draw's nodes a scroll can land on: the row above the big chart and the hard times' heading. */
    private javafx.scene.Node chartTop, hardTimesTop;

    /**
     * The page's inside width, last laid out (0.7.37; GRAPH's 760 until
     * then): the big chart, the pins and the rows that wrap are drawn at it,
     * and the canvases follow it when the window is resized. A first draw
     * guesses the 1,389-pixel window's 1,234.
     */
    private double pageWidth = 1234;

    /** The preset row's flow, so a resize can rewrap it. */
    private javafx.scene.layout.FlowPane presetFlow;

    /**
     * One plottable line: where it comes from and how to read it.
     *
     * @param key    the stored series name, or a derived one computed in
     *               historyValues()
     * @param unit   how to format a value of it - the difference between "0.18"
     *               and "18%" and "$0.18"
     */
    record Trace(String key, String label, String group, String unit) { }

    static final Trace[] TRACES = withTheCrime(withTheHouseholds(withTheSectors(withTheMarket(new Trace[] {
        new Trace("gdp",            "GDP",                "MONEY",      "money"),
        new Trace("gdpPerCapita",   "GDP per capita (yr)","MONEY",      "money"),
        new Trace("realGdp",        "GDP, real (yr)",     "MONEY",      "money"),
        // GDP's four parts, a month each in its own money (0.7.37: kept since 0.7.6 for the layers, never offered).
        new Trace("consumption",    "GDP: consumption",   "MONEY",      "money"),
        new Trace("investment",     "GDP: investment",    "MONEY",      "money"),
        new Trace("government",     "GDP: government",    "MONEY",      "money"),
        new Trace("netExports",     "GDP: net exports",   "MONEY",      "money"),
        new Trace("cash",           "Treasury",           "MONEY",      "money"),
        new Trace("debt",           "Public debt",        "MONEY",      "money"),
        new Trace("revenue",        "Revenue",            "MONEY",      "money"),
        new Trace("surplus",        "Surplus / deficit",  "MONEY",      "money"),
        new Trace("interestRate",   "Borrowing rate",     "MONEY",      "percent"),
        new Trace("totalWage",      "Wage bill",          "MONEY",      "money"),
        new Trace("averageWage",    "Average wage",       "MONEY",      "money"),
        // The dial as HistorySave keeps it, in founding money (0.7.38's label): the floor in today's money is not a series.
        new Trace("minimumWage",    "Minimum wage, founding money", "MONEY", "money"),
        new Trace("unskilledPremium","Unskilled vs base", "MONEY",      "ratio"),
        new Trace("skilledShare",   "Workforce trained",  "PEOPLE",     "percent"),
        new Trace("schoolCoverage", "Children in school", "PEOPLE",     "percent"),
        new Trace("schoolBill",     "Schools",            "MONEY",      "money"),
        // The central bank's year (0.7.0), kept for the year book and never offered here until 0.7.37.
        new Trace("m0",             "M0, the central bank's money", "MONEY", "money"),
        new Trace("m2",             "M2, the money people hold", "MONEY", "money"),
        new Trace("reserves",       "Reserves at the central bank", "MONEY", "money"),
        new Trace("advancesToTreasury", "Advances to the treasury", "MONEY", "money"),
        new Trace("remittance",     "Central bank remittance", "MONEY",  "money"),

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
        /* ...and each band's dead a month, as recorded (0.7.37; "Killed" a month is under CRIME). */
        new Trace("deathsBabies",              "Babies a month",      "THE DEAD", "count"),
        new Trace("deathsChildren",            "Children a month",    "THE DEAD", "count"),
        new Trace("deathsTeens",               "Teens a month",       "THE DEAD", "count"),
        new Trace("deathsAdults",              "Adults a month",      "THE DEAD", "count"),
        new Trace("deathsSeniors",             "Seniors a month",     "THE DEAD", "count"),
        new Trace("deathsElders",              "Elders a month",      "THE DEAD", "count"),
        new Trace("deathsOrphans",             "Orphans a month",     "THE DEAD", "count"),
        new Trace("deathsUnhoused",            "With no home a month", "THE DEAD", "count"),

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
        new Trace("fxParity",       "Parity",             "TRADE",      "rate"),
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
        // The bank's capital (0.7.8), kept for the Bank tab and never offered here until 0.7.37.
        new Trace("bankProvisions", "Bank provisions",    "CREDIT",     "money"),
        new Trace("bankDividends",  "Bank dividends",     "CREDIT",     "money"),
        new Trace("bankAllowance",  "Bank loss allowance","CREDIT",     "money"),
        new Trace("bankCapitalRatio","Bank capital ratio","CREDIT",     "percent"),
        new Trace("bankCapitalTarget","Bank capital target","CREDIT",   "percent"),
        new Trace("bankReturnOnEquity","Bank return on equity","CREDIT","percent"),

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
    }))));

    /** One series per reason for crime, added to CRIME. See HistorySave.crimeKey(). */
    static Trace[] withTheCrime(Trace[] fixed) {
        Crime.Cause[] causes = Crime.Cause.values();
        Trace[] all = java.util.Arrays.copyOf(fixed, fixed.length + causes.length);
        for (int c = 0; c < causes.length; c++) {
            all[fixed.length + c] = new Trace(HistorySave.crimeKey(causes[c]),
                    "Crime: " + causes[c].label().toLowerCase(), "CRIME", "count");
        }
        return all;
    }

    /** One series per household shape, appended after the sectors. See HistorySave.householdKey(). */
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
     * anybody remembering to - and since 0.7.37 its fair value beside it,
     * what the register says a founding share is worth, which the history
     * kept and the picker never offered. THE MARKET's unit is a founding
     * share - see HistorySave's market block.
     */
    static Trace[] withTheMarket(Trace[] fixed) {
        Trace[] all = java.util.Arrays.copyOf(fixed, fixed.length + 2 * Equity.COMPANIES.length);
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            String company = Equity.COMPANIES[c];
            all[fixed.length + 2 * c] = new Trace(HistorySave.priceKey(company),
                    company + " shares", "THE MARKET", "share");
            all[fixed.length + 2 * c + 1] = new Trace(HistorySave.valueKey(company),
                    company + " fair value", "THE MARKET", "share");
        }
        return all;
    }

    /**
     * ...and each business's month (0.7.37): its net income after tax and
     * its posts filled, which the history has kept for every sector since
     * 0.7.4 for the Sectors cards' sparklines and the picker never offered
     * (City History's spec, B11). Generated off the registry's own order,
     * as THE MARKET is, so a sector added there is offered here.
     */
    static Trace[] withTheSectors(Trace[] fixed) {
        Trace[] all = java.util.Arrays.copyOf(fixed, fixed.length + 2 * Sectors.KEYS.length);
        for (int s = 0; s < Sectors.KEYS.length; s++) {
            String sector = Sectors.KEYS[s];
            all[fixed.length + 2 * s] = new Trace(HistorySave.netIncomeKey(sector),
                    sector + " net income", "SECTORS", "money");
            all[fixed.length + 2 * s + 1] = new Trace(HistorySave.workersKey(sector),
                    sector + " workers", "SECTORS", "count");
        }
        return all;
    }

    /**
     * Which of the four areas a line belongs to, for its colour (0.7.23):
     * money and policy blue, people and services teal, business and trade
     * violet, building and land pink - the picker's groups mapped onto the
     * rail's areas, with output, the goods' prices and the ground put where
     * the header and the land office already put them. It replaced
     * TRACE_COLOURS, eight colours of which three were the verdicts.
     */
    static String areaOf(Trace t) {
        switch (t.key()) {
            case "gdp": case "gdpPerCapita": case "realGdp":        return Palette.BUSINESS;
            case "consumption": case "investment": case "government": case "netExports": return Palette.BUSINESS;
            case "constructionCapacity": case "landUse": case "landPrice": return Palette.BUILDING;
            case "foodPrice": case "materialsPrice": case "orePrice": return Palette.BUSINESS;
            default: break;
        }
        return groupArea(t.group());
    }

    /** A picker group's area: what its heading is coloured in. */
    static String groupArea(String group) {
        return switch (group) {
            case "MONEY", "CREDIT", "BUDGET" -> Palette.MONEY;
            case "TRADE", "THE MARKET", "PRICES", "SECTORS" -> Palette.BUSINESS;
            case "HOUSING" -> Palette.BUILDING;
            default -> Palette.PEOPLE;
        };
    }

    /** A picker group's icon, beside its heading (0.7.37): the rail's or Build's for what the group is about. */
    static String groupIcon(String group) {
        return switch (group) {
            case "MONEY"                -> Icons.FINANCES;
            case "PEOPLE"               -> Icons.POPULATION;
            case "THE DEAD"             -> Icons.DEATH;
            case "CRIME"                -> Icons.SAFETY;
            case "THROUGHPUT"           -> Icons.UTILITIES;
            case "PRICES"               -> Icons.COIN;
            case "TRADE"                -> Icons.TRADE;
            case "CREDIT"               -> Icons.BANK;
            case "BUDGET"               -> Icons.GOVERNMENT;
            case "HOUSING", "HOUSEHOLDS" -> Icons.HOMES;
            case "SCHOOLS"              -> Icons.EDUCATION;
            case "OUTSIDE THE FAMILIES" -> Icons.CANE;
            case "THE MARKET", "SECTORS" -> Icons.SECTOR;
            default                     -> Icons.REPORTS;
        };
    }

    /** Each picked line's colour, in the order picked: its area's, then the others (Palette.lineColours()). */
    String[] pickedColours() {
        List<String> areas = new ArrayList<>();
        for (String key : historyPicked) areas.add(areaOf(traceFor(key)));
        return Palette.lineColours(areas);
    }

    /** One picked line's colour, or null if it is not picked. */
    String colourOf(String key) {
        String[] colours = pickedColours();
        int k = 0;
        for (String picked : historyPicked) {
            if (picked.equals(key)) return colours[k];
            k++;
        }
        return null;
    }

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

       So, top to bottom: the range (the big chart's own buttons since
       0.7.23); TWO PINNED CHARTS, one line each in its
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
       chart is a chart nobody can read, and the axis says so (since 0.7.23
       each legend chip, "low to high").

       NOTHING HERE DECIDES WHAT HAPPENED. The recession bands and the episode
       marks come off YearBook (recessions() - since 0.7.23 through
       recessionBands(), which names each - and episodes()), the same functions
       the year book's WHAT HAPPENED section prints from, so the file and the
       chart cannot disagree about what to call a year. The page formats.
       ===================================================================== */

    /* =====================================================================
       THE PAGE AT ITS WIDTH (0.7.37)

       Jerus, on the screens not yet redone: "the others are still full of
       text and the design could be more intuitive and fun". City History was
       the last of the rail's screens still in the 0.7.5 column: GRAPH, 760
       pixels, centred in a stage of about 1,270, the big chart drawn 730
       wide and every reading a label and a number on a row. The design study
       is the project's spec-history-0736.md; its decisions D1-D11 are built
       as it recommends.

       THE FRAME DOES NOT SCROLL, as no redrawn screen's does: the head - the
       title with its (i), and "Write the year book", the page's one action,
       which sat under thirty-odd goods at the foot of the page (D10) - the
       book's result card when there is one, and a strip about the city
       rather than the chart: THE CITY, HARD TIMES, RUNNING NOW and YOUR
       DECISIONS (D3). What the old strip said moved where it belongs: IN
       VIEW is the chart's own caption, DRAWING the picker's head, IT MOVED
       the first reading card.

       THE PAGE UNDER IT IS AS WIDE AS THE STAGE (D1), up to PAGE_WIDE: the
       two pins as cards above the big chart (Jerus, 0.7.5, kept: D2), 120
       tall so the big plot ends above the fold at 1,389 x 868; the big chart
       at the page's width; a card for each line it draws; the hard times and
       the decisions in view, each a click that moves the chart there (D6);
       the picker; and the month's prices, folded (D7). The canvases follow
       the page when the window is resized (follow()).

       NO VERDICT ON A LINE'S MOVE (D5). Every reading's change was green
       when its line rose and amber when it fell, so rising unemployment read
       green and a shrinking deficit amber. A move is neutral ink with an up
       or a down arrow: the model has no "better when" for a series, and this
       page does not invent one.
       ===================================================================== */

    void showHistoryMenu() {
        // A month landing while the player drags the chart waits for the
        // release (0.7.23): a rebuild would take the chart out from under the
        // pointer. The release redraws the page.
        if (bigChart != null && bigChart.isDragging()) return;

        // Whether the player was typing in the filter box when this redraw came:
        // the month rebuilds the page under them, and the new box takes the
        // focus back, or the next key they press is a shortcut.
        boolean typing = filterField != null && filterField.isFocused();
        // Whether the page is being redrawn where it stands - a month landing,
        // a click on it - rather than arrived at: only then do figures count on.
        boolean here = ui.isShowing("showHistoryMenu");

        ui.clearMenu("showHistoryMenu", () -> showHistoryMenu());
        named.clear();
        chartTop = null;
        hardTimesTop = null;

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
           draw it (a blank plot over the window, "0 drawn", "Nothing picked").

           So the seed is a first-visit courtesy and nothing more. The flag, not
           the emptiness of the set, is what says whether the courtesy is spent.
           ===================================================================== */
        if (!historySeeded) {
            historySeeded = true;
            historyPicked.addAll(java.util.Arrays.asList(PRESETS[0].keys()));
        }

        if (h.months() < 2) {
            VBox frame = new VBox(Palette.GAP, pageHead("City History", Palette.MONEY, null, null, LEAD_INFO),
                    alert("Nothing to draw yet", "The city has lived " + h.months() + " month"
                            + (h.months() == 1 ? "" : "s") + ". A line needs two points."));
            frame.setMaxWidth(PAGE_WIDE);
            frame.setFillWidth(true);
            frame.setPadding(new javafx.geometry.Insets(0, 18, 4, 18));
            ui.rootMenu.getChildren().add(frame);
            return;
        }

        int month = ui.game.getMonth();
        boolean fresh = here && drawnMonth >= 0 && month != drawnMonth;
        drawnMonth = month;

        // The window is told the history's ends first: the strip, the small
        // charts and the readings below all read it.
        List<Integer> axis = h.getMonth();
        chartWindow.setData(axis.get(0), axis.get(axis.size() - 1));

        // Read once a draw and handed to every part that names them (ChartModel,
        // off YearBook and the DecisionLog): the recession bands shade every
        // chart, the episodes and the flags run under the big one, and the
        // strip and the hard times list them. A decision made before the
        // history's first month - the founding month's - is on that first
        // month (ChartModel.onAxis(), the spec's B4).
        List<YearBook.Band> bands = ChartModel.bands(h);
        List<YearBook.Episode> episodes = ChartModel.episodes(h);
        List<ChartModel.Flag> flags = ChartModel.onAxis(ChartModel.flags(ui.game.getDecisions()), axis.get(0));
        noteEpisodes(episodes, month);

        VBox body = widePage();
        TimeChart big = bigChart(h, bands, episodes, flags);
        body.getChildren().add(pins(h, bands, fresh));
        HBox controls = chartControls(h);
        chartTop = controls;
        body.getChildren().add(controls);
        if (chartFull) {
            Label away = new Label("The chart is open over the whole window. Esc brings it back here.");
            away.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED) + " -fx-padding: 8 0 8 0;");
            body.getChildren().add(away);
            refreshFullScreen();
        } else {
            body.getChildren().add(big);
            // Real GDP alone, in layers (0.7.6): the key under the chart, as it always had.
            if (bigInLayers()) body.getChildren().add(layersKey(gdpStack(h, traceFor(LAYERED).unit()).layers(), pageWidth));
        }
        // Nothing picked, no section: the chart's own empty words say what to do (the spec's R3).
        if (!historyPicked.isEmpty()) body.getChildren().add(readings(h, fresh));
        body.getChildren().add(hardTimes(h, episodes, flags));
        body.getChildren().add(historyPickerRows());
        body.getChildren().add(prices());

        VBox frame = new VBox(Palette.GAP, head());
        HBox written = bookCard();
        if (written != null) frame.getChildren().add(written);
        frame.getChildren().add(strip(h, episodes, flags));

        /*
         * KEPT FROM THE BOTTOM, AND THE THING PRESSED HELD STILL.
         *
         * From the bottom because the readings above the picker grow a card
         * per line picked, and the picker is what the player is pressing - see
         * keptScrollerFromBottom. That covers a month's redraw. A click can
         * now also change what is BELOW the pointer - a group opening, a fold,
         * a preset or "clear all" above readings that change length - so the
         * chip that was pressed is found again in the new page and put back
         * where it was on the screen (holdInPlace), after the memory has had
         * its go. Since 0.7.37 the head and the strip stay put over it, as
         * every redrawn screen's frame does, and only the page scrolls.
         */
        frameOver(frame, body);

        if (pressed != null) {
            holdInPlace(named.get(pressed), pressedAt);
            pressed = null;
            pressedAt = Double.NaN;
        }
        if (typing && filterField != null) {
            filterField.requestFocus();
            filterField.positionCaret(filterField.getText().length());
        } else {
            // ...and the focus is the page's (0.7.20), as the filter's Enter hands it
            // back, so a redraw never leaves it to be found by traversal.
            page.requestFocus();
        }
        // A row that moved the chart, or a door, asked to land somewhere: after
        // the memory's restore and holdInPlace(), so it has the last word.
        if (scrollTarget != null) {
            String target = scrollTarget;
            scrollTarget = null;
            javafx.application.Platform.runLater(() -> scrollTo(TO_CHART.equals(target) ? chartTop : hardTimesTop));
        }
    }

    /** The frame's height before it is laid out, for the scroller's first guess at what is left of the stage. */
    static final double FRAME_CHROME = 160;

    /** What the stage keeps under the scroller once the frame is laid out - BankScreen's and Trade's. */
    static final double STAGE_REST = 36;

    /**
     * The fixed frame over a scrolling page, at the page's width; the page as
     * tall as what is left under the frame as laid out (BankScreen's), its
     * position kept from the bottom (THE PAGE, REDRAWN), and its width the
     * scroller's, which the canvases follow (follow()).
     */
    private void frameOver(VBox frame, VBox body) {
        frame.setMaxWidth(PAGE_WIDE);
        frame.setFillWidth(true);
        frame.setPadding(new javafx.geometry.Insets(0, 18, 4, 18));
        javafx.scene.control.ScrollPane scroller = ui.scrolled(body, FRAME_CHROME, true);
        scroller.prefHeightProperty().unbind();
        scroller.prefHeightProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                () -> Math.max(260, ui.menuScroller.getHeight()
                        - (frame.getHeight() > 0 ? frame.getHeight() + STAGE_REST : FRAME_CHROME)),
                ui.menuScroller.heightProperty(), frame.heightProperty()));
        body.prefWidthProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
                () -> Math.min(PAGE_WIDE, Math.max(320, scroller.getViewportBounds().getWidth())),
                scroller.viewportBoundsProperty()));
        // The page's inside width: widePage()'s 18 a side off what it is given.
        body.widthProperty().addListener((o, was, now) -> follow(now.doubleValue() - 36));
        ui.rootMenu.getChildren().addAll(frame, scroller);
        page = scroller;
    }

    /**
     * The page laid out at a new width (0.7.37, D1): the big chart, the two
     * pins and the preset row follow it, without a redraw. The cards and the
     * picker's rows are laid out at whatever width they are given already.
     */
    private void follow(double width) {
        if (!(width > 0) || Math.abs(width - pageWidth) < 1) return;
        pageWidth = width;
        if (bigChart != null && !chartFull) bigChart.setSize(pageWidth - 30, BIG_CHART);
        for (TimeChart small : smallCharts) if (small != null) small.setSize(pinChartWidth(), SMALL_CHART);
        if (presetFlow != null) {
            presetFlow.setPrefWrapLength(pageWidth - CONTROLS);
            presetFlow.setMaxWidth(pageWidth - CONTROLS);
        }
    }

    /** Scrolls the page so `target` is at its top, once laid out (InfrastructureScreen's and Trade's way). */
    private void scrollTo(javafx.scene.Node target) {
        javafx.scene.control.ScrollPane scroller = page;
        if (scroller == null || target == null || target.getScene() == null || scroller.getContent() == null) return;
        scroller.applyCss();
        scroller.layout();
        double contentH = scroller.getContent().getBoundsInLocal().getHeight();
        double viewH = scroller.getViewportBounds().getHeight();
        if (contentH <= viewH) return;
        double y = scroller.getContent().sceneToLocal(target.localToScene(0, 0)).getY();
        scroller.setVvalue(Math.max(0, Math.min(1, (y - 6) / (contentH - viewH))));
    }

    /** The chart moved to a stretch of months - an episode's, a decision's - and the page scrolled to it; nothing else changes (D6). */
    void moveChartTo(double from, double to) {
        chartWindow.setWindow(from, to);
        scrollTarget = TO_CHART;
        showHistoryMenu();
    }

    /** ...an episode, with a year either side. */
    void moveChartTo(YearBook.Episode e) {
        moveChartTo(e.fromMonth() - 12, e.toMonth() + 12);
    }

    /* ----------------------------- the head ----------------------------- */

    /** The title's (i): the old lead, and how the big chart is read. */
    static final String LEAD_INFO = "Every month the city has lived, and what it did. The big chart draws the lines "
            + "you pick, with the recessions shaded on it and named; under it the named hard times run on their "
            + "lane and your decisions stand as flags. Drag it, wheel it, or pick a range; the two small charts "
            + "above it and the cards under it follow its window.";

    /** What "Write the year book" writes (the old SEND THIS RUN TO SOMEBODY paragraph). */
    static final String YEAR_BOOK_INFO = "Writes the whole run out as plain text - every series the history "
            + "keeps, folded one row a year, and again one row a decade, with a list of what actually happened "
            + "and when. Each column says whether it was added, taken at the end of the row or averaged, and "
            + "what it means, so it can be handed to somebody - or something - that has never seen this city. "
            + "The tables go out again beside it as .csv files, which Excel opens as columns and rows.";

    /**
     * The head: "City History" in the money blue with its (i), and at its
     * right the page's one action, 0.7.34's button (D10) - it sat at the foot
     * of the page under every good. NEVER HANDED THE FOCUS BY THE WINDOW
     * (0.7.20): it was the only control on the page that took focus, so
     * JavaFX gave it the focus whenever the last screen's went, and the
     * scroller scrolled to show it - City History opened at the price table
     * every time. The action button is a region and takes no focus at all.
     */
    HBox head() {
        Pieces.ActionButton write = actionButton(Icons.REPORTS, Palette.MONEY, ACTION_INLINE,
                new Press(Look.GO, "Write the year book", null), this::writeTheBooks);
        write.setMinWidth(Region.USE_PREF_SIZE);
        javafx.scene.Node about = infoButton(YEAR_BOOK_INFO, false);
        HBox right = new HBox(6, write, about);
        right.setAlignment(Pos.CENTER_RIGHT);
        right.setMinWidth(Region.USE_PREF_SIZE);
        return pageHead("City History", Palette.MONEY, null, null, LEAD_INFO, right);
    }

    /**
     * What the last export did, kept so it survives the redraw - the whole
     * of it in a few lines (bookExportSaid), and its parts for the card.
     *
     * The click redraws this whole screen, and so does the month turning under
     * it, so a message held in a local would be gone before it was read.
     */
    String bookExportSaid;
    private String bookFolder;
    private final List<String> bookFiles = new ArrayList<>(), bookFailed = new ArrayList<>();

    /**
     * Six files since 0.7.16 - each book's text and its two tables as CSV -
     * all in one folder, so the folder is named once and each book is a line
     * of file names; anything that failed says so on a line of its own.
     */
    void writeTheBooks() {
        StringBuilder wrote = new StringBuilder(), failed = new StringBuilder();
        java.nio.file.Path folder = null;
        bookFiles.clear();
        bookFailed.clear();
        for (GameFiles.Result[] book : ui.game.writeBooks()) {
            StringBuilder names = new StringBuilder();
            for (GameFiles.Result written : book) {
                if (written.ok) {
                    if (folder == null) folder = written.file.getParent();
                    if (names.length() > 0) names.append("  ·  ");
                    names.append(written.file.getFileName());
                } else {
                    String line = "Could not write " + written.file + " - " + written.error;
                    failed.append(line).append('\n');
                    bookFailed.add(line);
                }
            }
            if (names.length() > 0) {
                wrote.append("  ").append(names).append('\n');
                bookFiles.add(names.toString());
            }
        }
        StringBuilder said = new StringBuilder();
        if (folder != null) said.append("Written to ").append(folder).append('\n').append(wrote);
        said.append(failed);
        bookFolder = folder == null ? null : folder.toString();
        bookExportSaid = said.toString().trim();
        showHistoryMenu();
    }

    /**
     * The book's result as a card under the head (0.7.37): where it went, a
     * line of file names a book in figures, what could not be written in the
     * red, and a × that puts it away. It stays until then, or the next write.
     */
    HBox bookCard() {
        if (bookExportSaid == null) return null;
        VBox lines = new VBox(2);
        if (bookFolder != null) {
            Label where = new Label("Written to " + bookFolder);
            where.setWrapText(true);
            where.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
            lines.getChildren().add(where);
        }
        for (String files : bookFiles) {
            Label l = new Label(files);
            l.setWrapText(true);
            l.setStyle(Palette.figureRegular(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
            lines.getChildren().add(l);
        }
        for (String failed : bookFailed) {
            Label l = new Label(failed);
            l.setWrapText(true);
            l.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.BAD));
            lines.getChildren().add(l);
        }
        if (lines.getChildren().isEmpty()) {
            Label l = new Label(bookExportSaid);
            l.setWrapText(true);
            l.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
            lines.getChildren().add(l);
        }
        HBox.setHgrow(lines, Priority.ALWAYS);
        lines.setMinWidth(0);
        lines.setMaxWidth(Double.MAX_VALUE);
        Label close = new Label("×");
        close.setStyle(Palette.strong(Palette.SIZE_HEADING + 4, Palette.TEXT_LABEL) + " -fx-cursor: hand; -fx-padding: 0 4 0 4;");
        close.setMinWidth(Region.USE_PREF_SIZE);
        tip(close, "Puts this away.");
        close.setOnMouseClicked(e -> {
            bookExportSaid = null;
            bookFolder = null;
            bookFiles.clear();
            bookFailed.clear();
            showHistoryMenu();
        });
        HBox card = new HBox(12, icon(Icons.REPORTS, Palette.MONEY, 16), lines, close);
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(Palette.block(Palette.RAISED, Palette.EDGE) + " -fx-padding: 8 12 8 12;");
        return card;
    }

    /* ----------------------------- the strip ----------------------------- */

    /** One cell of the strip, as words, so a probe reads them without drawing: its label, figure, note and tone, where its door goes (null: none), and whether it is NEW this month. */
    record Cell(String label, String value, String note, String tone, String where, boolean isNew) { }

    /**
     * The strip's four (0.7.37, D3): THE CITY, its age and its record;
     * HARD TIMES, every named episode since founding and how many touch the
     * chart's window; RUNNING NOW, the trouble the history has not closed,
     * the worst first (YearBook.running(), D4), in its own verdict's colour;
     * and YOUR DECISIONS, the log's size, how many are in the window and the
     * last. Pure: no node is made.
     */
    List<Cell> stripCells(HistorySave h, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags) {
        List<Integer> axis = h.getMonth();
        int last = axis.get(axis.size() - 1);
        int from = chartWindow.firstMonthShown(), to = chartWindow.lastMonthShown();
        List<Cell> cells = new ArrayList<>();

        cells.add(new Cell("THE CITY", cityAge(h.months()),
                String.format("%,d months recorded · since %s", h.months(), CityCalendar.formatShort(axis.get(0))),
                Palette.TEXT_HEAD, null, false));

        int inView = 0;
        for (YearBook.Episode e : episodes) if (touches(e, from, to)) inView++;
        cells.add(new Cell("HARD TIMES", String.format("%,d", episodes.size()),
                episodes.isEmpty() ? "none named since founding"
                        : "named since founding · " + (inView == 0 ? "none" : String.format("%,d", inView)) + " in view",
                Palette.TEXT_HEAD, "Scrolls to the hard times in view, and every one since founding", false));

        List<YearBook.Episode> running = YearBook.running(episodes, last);
        if (running.isEmpty()) {
            cells.add(new Cell("RUNNING NOW", "nothing named", "no crisis this month", Palette.GOOD, null, false));
        } else {
            YearBook.Episode e = running.get(0);
            String note;
            if (running.size() > 1) {
                StringBuilder others = new StringBuilder("+ ");
                for (int i = 1; i < running.size(); i++) others.append(i > 1 ? " · " : "").append(running.get(i).name());
                note = others.toString();
            } else if (YearBook.isChronic(e)) {
                note = "running since " + CityCalendar.formatShort(e.fromMonth());
            } else {
                note = "since " + CityCalendar.formatShort(e.fromMonth()) + " · " + months(e.months());
            }
            cells.add(new Cell("RUNNING NOW", e.name(), note, episodeTone(e.kind()),
                    "Moves the chart to " + e.name(), isNew(e)));
        }

        DecisionLog log = ui.game.getDecisions();
        int decided = 0;
        for (ChartModel.Flag f : flags) if (f.month() >= from && f.month() <= to) decided += f.count();
        cells.add(new Cell("YOUR DECISIONS", String.format("%,d", log.size()),
                log.size() == 0 ? "none made yet"
                        : "since founding · " + (decided == 0 ? "none" : String.format("%,d", decided)) + " in view · last "
                          + CityCalendar.formatShort(log.last().month()),
                Palette.TEXT_HEAD, log.size() == 0 ? null : "Scrolls to your decisions in view", false));
        return cells;
    }

    /** The strip drawn: four limit cells, three of them doors - to the hard times, to what is running on the chart, to the decisions. */
    HBox strip(HistorySave h, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags) {
        List<Cell> cells = stripCells(h, episodes, flags);
        List<Integer> axis = h.getMonth();
        List<YearBook.Episode> running = YearBook.running(episodes, axis.get(axis.size() - 1));
        VBox[] drawn = new VBox[cells.size()];
        for (int i = 0; i < cells.size(); i++) {
            Cell c = cells.get(i);
            Runnable go = null;
            if (c.where() != null) {
                go = "RUNNING NOW".equals(c.label()) && !running.isEmpty()
                        ? () -> moveChartTo(running.get(0))
                        : () -> scrollTo(hardTimesTop);
            }
            VBox cell = limitCell(c.label(), c.value(), c.note(), c.tone(), c.where(), go);
            // A name is the figure here (RUNNING NOW): it wraps inside the cell rather than end in "...".
            if (cell.getChildren().get(1) instanceof Label figure) {
                figure.setWrapText(true);
                figure.setMaxWidth(LIMIT_CELL - 28);
            }
            if (c.isNew()) cell.getChildren().add(tag("new", c.tone()));
            drawn[i] = cell;
        }
        return vitalsBar(drawn);
    }

    /**
     * An episode's verdict colour, red for a crisis and amber for a watch:
     * TimeChart.episodeColour()'s rule (YearBook.isSevere()), here so the
     * strip's words are composed without the chart's class, which a probe
     * without a toolkit cannot load.
     */
    static String episodeTone(String kind) {
        return YearBook.isSevere(kind) ? Palette.BAD : Palette.WARN;
    }

    /** A city's age in the words a person uses: months under a year, years after. */
    static String cityAge(int months) {
        if (months < 12) return months == 1 ? "1 month" : months + " months";
        int years = months / 12;
        return years == 1 ? "1 year" : String.format("%,d years", years);
    }

    /** "1 month", "6 months", "2,213 months". */
    static String months(int n) {
        return n == 1 ? "1 month" : String.format("%,d months", n);
    }

    /** Whether an episode touches the months from..to. */
    static boolean touches(YearBook.Episode e, int from, int to) {
        return e.toMonth() >= from && e.fromMonth() <= to;
    }

    /** An episode's name for "new": its kind and its first month, which do not change as it runs. */
    static String episodeKey(YearBook.Episode e) {
        return e.kind() + ":" + e.fromMonth();
    }

    /**
     * Notes the episodes this draw lists (0.7.37): one not listed before is
     * NEW from the month it is first seen in. A first look, a load, a month
     * gone backwards or a skip of more than one takes the list as it finds
     * it, so nothing is new for having been named while nobody was looking.
     */
    void noteEpisodes(List<YearBook.Episode> episodes, int month) {
        boolean jump = episodesSeenAt < 0 || month < episodesSeenAt || month > episodesSeenAt + 1;
        if (jump) episodeSeen.clear();
        for (YearBook.Episode e : episodes) episodeSeen.putIfAbsent(episodeKey(e), jump ? Integer.MIN_VALUE : month);
        episodesSeenAt = month;
    }

    /** Whether an episode was first listed this month. */
    boolean isNew(YearBook.Episode e) {
        Integer at = episodeSeen.get(episodeKey(e));
        return at != null && at == ui.game.getMonth();
    }

    /**
     * The window's first and last months as indices into the history's axis,
     * {from, to} - what the pins, the readings and the log scale's check fold
     * over (0.7.23; they read historyWindow's last N months before).
     */
    int[] shownIndices(HistorySave h) {
        List<Integer> months = h.getMonth();
        if (months.isEmpty()) return new int[] {0, -1};
        int from = ChartModel.nearest(months, chartWindow.firstMonthShown());
        int to = ChartModel.nearest(months, chartWindow.lastMonthShown());
        if (months.get(from) < chartWindow.lo() - 1e-9 && from < months.size() - 1) from++;
        if (months.get(to) > chartWindow.hi() + 1e-9 && to > 0) to--;
        return new int[] {from, Math.max(from, to)};
    }

    /** A chip that is on or off, which is most of the controls on this screen. */
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

    /** How tall a pinned chart's plot is: 150 until 0.7.37, 120 since, so the big plot ends above the fold at 1,389 x 868 (D2). */
    static final double SMALL_CHART = 120;

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
     * City History opened on these lines - THE ONE DOOR IN (0.7.37, D11):
     * a header tile's click (UserInterface.openHistory(), which marks it an
     * arrival first), the Infrastructure tab's "The road over the years ›",
     * the Government tab's "Over the years ›" (the revenue and the surplus;
     * real GDP from its output card), Finances' and the Bank's, Policy's
     * "History" and Trade's - and a pin's name on this page. The big chart's
     * picks become those lines - the first visit's preset spent, every line
     * shown, the last ten years in view; a key History does not know is left
     * out, and with none it knows the picks stay as they were; and NOT
     * pinned, because pin() writes the preferences file, and a door should
     * not change what the player pinned. From another tab the page opens at
     * its top; from this one, at the chart.
     */
    void openOn(String... keys) {
        historySeeded = true;
        boolean any = false;
        for (String key : keys) any |= known(key);
        if (any) {
            historyPicked.clear();
            hiddenLines.clear();
            for (String key : keys) if (known(key)) historyPicked.add(key);
            chartWindow.showRange(UserInterface.SPARK_MONTHS);
        }
        if (ui.isShowing("showHistoryMenu")) scrollTarget = TO_CHART;
        showHistoryMenu();
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

    /**
     * The two pins as cards side by side (0.7.37; two bare charts at 374
     * wide until then), each half the page, on the big chart's window.
     */
    GridPane pins(HistorySave h, List<YearBook.Band> bands, boolean fresh) {
        GridPane row = equalColumns(2, 10);
        VBox left = pinCard(h, true, bands, fresh), right = pinCard(h, false, bands, fresh);
        for (VBox card : new VBox[] {left, right}) {
            GridPane.setFillHeight(card, true);
            card.setMaxHeight(Double.MAX_VALUE);
        }
        row.add(left, 0, 0);
        row.add(right, 1, 0);
        return row;
    }

    /** A pin's chart: its card's half of the page, less the card's padding and edge. */
    double pinChartWidth() {
        return Math.max(200, (pageWidth - 10) / 2 - 26);
    }

    /**
     * The two small charts' nodes, kept from one redraw to the next, as the
     * big chart is: each follows the big chart's window as it is dragged,
     * which a chart rebuilt every month could not (0.7.23).
     */
    private final TimeChart[] smallCharts = new TimeChart[2];

    /** One pin's head, as words (0.7.37): its line, its figure in the window's last month, and how far it moved over the window. Pure. */
    record PinHead(String key, String label, String figure, String move, String moveTip, double last, boolean recorded) { }

    PinHead pinHead(HistorySave h, boolean left) {
        String key = pinned(left);
        Trace t = traceFor(key);
        Span s = span(historyValues(h, key), shownIndices(h));
        boolean recorded = !Double.isNaN(s.last());
        return new PinHead(key, t.label(), recorded ? shown(t.unit(), s.last()) : "not recorded",
                recorded ? moveWords(t.unit(), s.first(), s.last()) : "",
                recorded ? "Over the chart's view: " + shown(t.unit(), s.first()) + " → " + shown(t.unit(), s.last()) : null,
                s.last(), recorded);
    }

    /**
     * One pin as a card (0.7.37): its head - the line's name, a click that
     * draws it alone on the big chart (openOn(), which never pins), its
     * figure, its move over the window in neutral ink, "unpin" and "layers"
     * - and the line in its own units under it, 120 tall (D2), its years
     * under that.
     */
    VBox pinCard(HistorySave h, boolean left, List<YearBook.Band> bands, boolean fresh) {

        PinHead p = pinHead(h, left);
        String key = p.key();
        Trace t = traceFor(key);
        List<Integer> months = h.getMonth();
        double[] all = historyValues(h, key);
        boolean never = true;
        for (double v : all) if (!Double.isNaN(v)) { never = false; break; }

        // Jerus: "pinnable defaults, but not fixed" - so a pin can be taken
        // off, and taking it off puts the default back.
        Label unpin = null;
        String back = unpinned(left);
        if (!back.equals(key)) {
            unpin = chip(left ? "unpin:left" : "unpin:right", "unpin", false,
                    () -> { unpin(left); showHistoryMenu(); });
            tip(unpin, "Puts " + traceFor(back).label() + " back here.");
        }
        // Real GDP can be drawn in layers (0.7.6), whichever side it is pinned to.
        boolean layered = LAYERED.equals(key) && gdpLayers;
        Label layers = LAYERED.equals(key) ? layersChip(left ? "left" : "right") : null;

        Pieces.CardHead head = chartCardHead(p.label(), p.figure(), p.move(), () -> openOn(key), layers, unpin);
        tip(head.name, "Draws " + p.label() + " on the big chart, over the last ten years.");
        if (p.moveTip() != null) tip(head.change, p.moveTip());
        if (!p.recorded()) head.figure.setStyle(Palette.figure(Palette.SIZE_LEAD, Palette.TEXT_SPENT));
        String was = (left ? "pin:left:" : "pin:right:") + key;
        if (fresh && p.recorded() && shownBefore.containsKey(was)) {
            SectorScreen.countUp(head.figure, shownBefore.get(was), p.last(), v -> shown(t.unit(), v));
        }
        if (p.recorded()) shownBefore.put(was, p.last());

        double wide = pinChartWidth();
        /*
         * NOTHING RECORDED, SAID IN ONE LINE (0.7.20) - not an empty frame with
         * a flat line in it, which is what "GDP, real (yr) - not recorded"
         * drew through a city's first year. A line kept over a rolling year
         * has its first point when the first year ends; the rest say they
         * have nothing yet. Held at the chart's height, so the page does not
         * move when the line arrives.
         */
        VBox card;
        if (never) {
            Label none = new Label(t.label().endsWith("(yr)")
                    ? "from the end of the first year" : "nothing recorded yet");
            none.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_SPENT));
            StackPane empty = new StackPane(none);
            empty.setMinHeight(SMALL_CHART + 26);
            empty.setPrefHeight(SMALL_CHART + 26);
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setStyle(Palette.block(Palette.FIELD));
            card = new VBox(6, head, empty);
        } else {
            // The line in its area's colour (0.7.23; it was the accent for every
            // line), over the big chart's window, with its years under it.
            int side = left ? 0 : 1;
            if (smallCharts[side] == null) {
                smallCharts[side] = new TimeChart(chartWindow, java.util.Set.of(), false);
                smallCharts[side].follow(bigChart);
            }
            TimeChart chart = smallCharts[side];
            TimeChart.Stack stack = layered ? gdpStack(h, t.unit()) : null;
            chart.setData(months,
                    List.of(new TimeChart.Line(key, t.label(), layered ? LAYERED_LINE : areaOf(t), 0, all,
                            v -> plotScale(t.unit(), v), v -> fmtUnit(t.unit(), v), "")),
                    axisFor(t.unit()), null, false, false, stack, bands, List.of(), List.of(),
                    t.label().endsWith("(yr)") ? "from the end of the first year" : "nothing recorded yet");
            chart.setSize(wide, SMALL_CHART);
            card = new VBox(6, head, chart);
            if (layered) card.getChildren().add(layersKey(stack.layers(), wide));
        }
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(Palette.block(Palette.RAISED, Palette.EDGE) + " -fx-padding: 10 12 10 12;");
        return card;
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

       THE STACK IS DRAWN UNDER THE LINE, on the same canvas and the same
       axis (TimeChart.Stack, since 0.7.23): a polygon per layer, each from
       the top of the one under it, a month a point; the crosshair reads all
       four parts. Until 0.7.23 it was a second chart, a StackedAreaChart
       behind the LineChart with its axes made invisible.

       Older saves have no parts until they play a month, and no year of
       them until twelve: the layers start where the series do, and the line
       is drawn as it always was.
       ===================================================================== */

    /** The one line this page can draw in layers. */
    static final String LAYERED = "realGdp";

    /** What the GDP line is drawn in over the layers: the headings' ink, which no step of the blue ramp is near. */
    static final String LAYERED_LINE = Palette.TEXT_HEAD;

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

    /**
     * GDP's four parts, a rolling year in founding money each - C, I, G, then
     * NX - as a chart stacks them (0.7.23): C, I and G drawn from zero in the
     * three steps of the ramp, net exports read on the crosshair, in the
     * muted ink, as the gap between the stack and the line.
     */
    TimeChart.Stack gdpStack(HistorySave h, String unit) {
        double[][] layers = new double[YearBook.GDP_PARTS.length][];
        for (int p = 0; p < layers.length; p++) layers[p] = YearBook.realYear(h, YearBook.GDP_PARTS[p]);
        String[] colours = {Palette.GDP_LAYERS[0], Palette.GDP_LAYERS[1], Palette.GDP_LAYERS[2], Palette.TEXT_MUTED};
        return new TimeChart.Stack(layers, LAYER_NAMES, colours, v -> plotScale(unit, v), v -> fmtUnit(unit, v));
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
                : "The layers start a year after this city began keeping them.");
        // Why, on the tooltip (0.7.37): the cause is not the page's to explain in a line.
        if (!any) tip(says, "This city was played before the game kept GDP's parts, so the first year of them "
                + "ends a year after it began keeping them.");
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

    /** How tall the big chart's plot is on the page; the lanes and the overview are under it. */
    static final double BIG_CHART = 380;

    /** Room kept at the right of the preset row for "clear all" and "log". */
    static final double CONTROLS = 130;

    /**
     * The row above the big chart: the presets on the left, "clear all" and
     * "log" on the right. Jerus: "'clear all' should just be beside the graph".
     */
    HBox chartControls(HistorySave h) {

        javafx.scene.layout.FlowPane presets = new javafx.scene.layout.FlowPane(6, 6);
        presets.setPrefWrapLength(pageWidth - CONTROLS);
        presets.setMaxWidth(pageWidth - CONTROLS);
        presetFlow = presets;
        for (int i = 0; i < PRESETS.length; i++) {
            Preset p = PRESETS[i];
            boolean on = historyPicked.size() == p.keys().length
                    && historyPicked.containsAll(java.util.Arrays.asList(p.keys()));
            Label c = chip("preset:" + i, p.name(), on, () -> {
                historyPicked.clear();
                historyPicked.addAll(java.util.Arrays.asList(p.keys()));
                hiddenLines.clear();
                showHistoryMenu();
            });
            tip(c, p.blurb());
            presets.getChildren().add(c);
        }

        Label clear = chip("clear", "clear all", false, () -> {
            historyPicked.clear();
            hiddenLines.clear();
            showHistoryMenu();
        });
        tip(clear, "Takes every line off the big chart. The two small ones stay.");

        String refused = logRefusal(h);
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
        row.setMaxWidth(Double.MAX_VALUE);
        row.setStyle("-fx-padding: 4 0 0 0;");
        return row;
    }

    /**
     * Why the log scale cannot draw what is picked, or null when it can.
     *
     * A logarithm has no zero and no negatives, and a deficit or an empty
     * treasury is exactly that - so one such month in the window and the
     * chip says so rather than drawing a line with a hole in it. Three units
     * are ranks on 0-100, which a log scale has nothing to say about. The
     * window is the chart's as it last rested (0.7.23); a drag into a month
     * at or below zero leaves a gap there until the page redraws and says so.
     */
    String logRefusal(HistorySave h) {
        List<String> units = pickedUnits();
        if (units.isEmpty()) return "nothing is drawn.";
        if (bigInLayers()) return "real GDP is drawn in layers, stacked from zero, and a log scale has no zero.";
        if (units.size() > 2) {
            return "three or more units are each drawn low-to-high, and a log scale has nothing to measure there.";
        }
        int[] shown = shownIndices(h);
        for (String key : historyPicked) {
            double[] all = historyValues(h, key);
            for (int i = shown[0]; i <= shown[1] && i < all.length; i++) {
                if (!Double.isNaN(all[i]) && all[i] <= 0) {
                    return traceFor(key).label() + " reaches zero or below in this window, and a log scale has no zero.";
                }
            }
        }
        return null;
    }

    /* =====================================================================
       THE CHART, REBUILT (0.7.23)

       Jerus: "in teh graphs you should be able to pan the chart just like
       yahoo finance does if you get what i mean, and it actually have
       interactive graph, and like also the crisis labels and all, dont you
       think that it should be more clear", and "let the player click
       fullscreen on the graph so the whole screen concentrates on the
       graph". The plan he agreed is the project's playing-0-7-19-ui-notes.md
       section 6.4; the mockups are History and History, full screen.

       ONE CHART, KEPT. The big chart is a TimeChart made once and handed new
       data on every redraw, so a month landing does not take the window, the
       hover or a card the player pinned away from under the pointer; a month
       that lands mid-drag waits for the release (showHistoryMenu()). Its
       window is chartWindow, which the two small charts follow, and which
       the vitals and the readings fold over once it rests (the chart's
       onSettled redraws the page).

       WHAT IT IS HANDED. Each picked line in its stored units, with
       plotScale() to put it in the axis's and fmtUnit() to read it - the same
       two tables the page always used; one unit is one real axis, two units
       two, each with its own nice scale (ChartModel.niceScale(): a per cent
       starts at zero, which is what kept a new city's flat rate off its flat
       price level), three or more each its own low-to-high. The bands are
       YearBook.recessionBands(), the lane YearBook.episodes(), the flags the
       city's DecisionLog - read, never recomputed here.
       ===================================================================== */

    /** The big chart, made once; see the banner. */
    private TimeChart bigChart;

    /** Whether the big chart has the whole window (0.7.23); see FULL SCREEN. */
    private boolean chartFull;

    /** The big chart with this redraw's lines, bands, episodes and flags - the flags as its lane draws them (ChartModel.onAxis(), 0.7.37) - at the page's width. */
    TimeChart bigChart(HistorySave h, List<YearBook.Band> bands, List<YearBook.Episode> episodes,
                       List<ChartModel.Flag> flags) {
        if (bigChart == null) {
            bigChart = new TimeChart(chartWindow, hiddenLines, true);
            // Only while City History is still the screen (after the docs pass): a rail
            // button or the menu inside the settle's 280 ms is not drawn over.
            bigChart.onSettled(() -> { if (ui.isShowing("showHistoryMenu")) showHistoryMenu(); });
            bigChart.onFullScreen(() -> setChartFull(!chartFull, true));
        }
        List<String> keys = new ArrayList<>(historyPicked);
        hiddenLines.retainAll(keys);
        List<String> units = pickedUnits();
        boolean squashed = units.size() > 2;
        boolean layered = bigInLayers();
        boolean log = historyLog && logRefusal(h) == null;
        String[] colours = pickedColours();
        Currency money = ui.game.getCurrency();

        List<TimeChart.Line> lines = new ArrayList<>();
        boolean recorded = false;
        for (int k = 0; k < keys.size(); k++) {
            Trace t = traceFor(keys.get(k));
            double[] all = historyValues(h, keys.get(k));
            for (double v : all) if (!Double.isNaN(v)) { recorded = true; break; }
            int side = squashed ? 0 : Math.max(0, units.indexOf(t.unit()));
            // The legend says what each line is measured in, and on two axes which one it reads against.
            String unitWords = squashed ? "low to high"
                    : (units.size() == 2 ? (side == 0 ? "left, " : "right, ") : "") + unitName(t.unit(), money);
            lines.add(new TimeChart.Line(keys.get(k), t.label(), layered ? LAYERED_LINE : colours[k], side, all,
                    v -> plotScale(t.unit(), v), v -> fmtUnit(t.unit(), v), unitWords));
        }

        String empty = "";
        if (keys.isEmpty()) {
            empty = "Nothing picked: a preset above the chart, or a line below.";
        } else if (!recorded) {
            boolean yearly = true;
            for (String key : keys) yearly &= traceFor(key).label().endsWith("(yr)");
            empty = yearly ? "from the end of the first year" : "nothing recorded yet";
        }

        bigChart.setData(h.getMonth(), lines,
                units.isEmpty() || squashed ? null : axisFor(units.get(0)),
                units.size() == 2 ? axisFor(units.get(1)) : null,
                squashed, log, layered ? gdpStack(h, units.get(0)) : null,
                bands, episodes, flags, empty);
        if (!chartFull) bigChart.setSize(pageWidth - 30, BIG_CHART);
        return bigChart;
    }

    /** One value axis: its gridlines in the unit's own words (axisTick()), and from zero when a per cent. */
    static TimeChart.Axis axisFor(String unit) {
        return new TimeChart.Axis((v, step) -> axisTick(unit, v, step), "percent".equals(unit));
    }

    /* =====================================================================
       FULL SCREEN (0.7.23)

       Jerus: "let the player click fullscreen on the graph so the whole
       screen concentrates on the graph". The button gives the big chart the
       whole window - no rail, no panels, no header, laid over everything
       the way the main menu is (UserInterface.showChartFullScreen()) - with
       the page's title and the date in its toolbar, and the legend, the
       ranges, the lanes and the overview as they were. Esc or the button
       comes back. The clock is not touched: it runs on, or stays paused, and
       every month redraws the chart in place; space and the arrows still
       work it. F11 is the WINDOW's full screen, the operating system's, and
       is separate: it toggles either way, in the page or over it, and
       leaving one leaves the other as it was.
       ===================================================================== */

    /** The pane the chart is laid in over the whole window, and its clock line. */
    private VBox fullPane;
    private Label fullClock;

    /**
     * Gives the big chart the whole window, or takes it back - and redraws
     * the page, unless another screen is about to be drawn instead (the
     * main menu, a rail button: UserInterface.leaveChartFullScreen()).
     */
    void setChartFull(boolean on, boolean redraw) {
        if (on == chartFull || bigChart == null) return;
        chartFull = on;
        // No drag or settle carried across: Esc or P can arrive with the button held (TimeChart.letGo()).
        bigChart.letGo();
        bigChart.unpin();
        if (on) {
            if (fullPane == null) {
                fullPane = new VBox();
                fullPane.setStyle("-fx-background-color: " + Palette.STAGE + "; -fx-padding: 14 22 12 22;");
                fullPane.widthProperty().addListener((o, was, now) -> fitFull());
                fullPane.heightProperty().addListener((o, was, now) -> fitFull());
                fullClock = new Label();
                fullClock.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
            }
            Label title = new Label("City History");
            title.setStyle(Palette.Fonts.sansSemiBold() + " -fx-font-size: 15px; -fx-text-fill: " + Palette.TEXT + ";");
            bigChart.lead().getChildren().setAll(title, fullClock);
            bigChart.setFull(true);
            fullPane.getChildren().setAll(bigChart);
            ui.showChartFullScreen(fullPane, fullClock, back -> setChartFull(false, back));
            fitFull();
            if (redraw) showHistoryMenu();
        } else {
            ui.closeChartFullScreen();
            bigChart.lead().getChildren().clear();
            bigChart.setFull(false);
            if (fullPane != null) fullPane.getChildren().clear();
            if (redraw) showHistoryMenu();
        }
    }

    /**
     * The page is going: another screen is being drawn (UserInterface.clearMenu()).
     * The big chart lets go of any drag and any redraw it was waiting to make.
     */
    void leaving() {
        if (bigChart != null) bigChart.letGo();
    }

    /** The chart sized to the pane: the whole width, and the plot whatever height the rest leaves. */
    private void fitFull() {
        if (!chartFull || fullPane == null || bigChart == null) return;
        double w = fullPane.getWidth() - 44, h = fullPane.getHeight() - 26;
        if (w <= 0 || h <= 0) return;
        bigChart.setSize(w - 30, h - bigChart.heightBesidePlot());
    }

    /** A redraw in full screen: the clock's line, and the chart's size if the window moved. */
    private void refreshFullScreen() {
        if (fullClock != null) fullClock.setText(ui.clockWords());
        fitFull();
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
        // A gridline the arithmetic left a hair below zero is zero, and has no sign (0.7.20).
        String sign = v < 0 && a > Math.abs(step) * 1e-6 ? "-" : "";
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

    /* =====================================================================
       WHAT EACH LINE DID (0.7.37)

       A card a line, three across, in the order picked (D1): its swatch and
       name, which axis it is read against when there are two; its figure in
       the window's last month and its move over the window, NEUTRAL INK with
       an arrow (D5, the spec's B1 - the old row coloured a rise green and a
       fall amber whatever the line); and a range bar - where it ended in its
       own range over the window (Pieces.rangeBar()). The old row's under
       line, with its hand-made indent (B9), is the card's caption. The first
       card is what the old strip's IT MOVED said.
       ===================================================================== */

    /** A line over the window: its first and last recorded values, its low and high and the indices of each; NaN where nothing is recorded. */
    record Span(double first, double last, double lo, double hi, int loAt, int hiAt) {
        /** Whether it never moved over the window. */
        boolean flat() { return !(hi > lo); }
    }

    /** One series over the window's indices {from, to} (shownIndices()). */
    static Span span(double[] all, int[] shown) {
        double first = Double.NaN, last = Double.NaN, lo = Double.NaN, hi = Double.NaN;
        int loAt = -1, hiAt = -1;
        for (int i = shown[0]; i <= shown[1] && i < all.length; i++) {
            if (Double.isNaN(all[i])) continue;
            if (Double.isNaN(first)) first = all[i];
            last = all[i];
            if (Double.isNaN(lo) || all[i] < lo) { lo = all[i]; loAt = i; }
            if (Double.isNaN(hi) || all[i] > hi) { hi = all[i]; hiAt = i; }
        }
        return new Span(first, last, lo, hi, loAt, hiAt);
    }

    /** One reading card, as words and figures (0.7.37): what the probe reads. Pure. */
    record Reading(String key, String label, String latest, String move, String caption, String rangeTip,
                   double first, double last, double lo, double hi, boolean never) { }

    Reading reading(HistorySave h, String key, String axis) {
        Trace t = traceFor(key);
        Span s = span(historyValues(h, key), shownIndices(h));
        boolean never = Double.isNaN(s.first());
        List<Integer> months = h.getMonth();
        String caption;
        if (never) {
            caption = "This city was played before the game kept that number.";
        } else if (s.flat()) {
            caption = "unchanged over the view";
        } else {
            caption = "from " + shown(t.unit(), s.first()) + " · low " + shown(t.unit(), s.lo())
                    + " · high " + shown(t.unit(), s.hi());
        }
        if (!never && axis != null) caption += " · " + axis;
        if (!never && hiddenLines.contains(key)) caption += " · hidden on the chart";
        String tip = never || s.flat() ? null
                : "low " + shown(t.unit(), s.lo()) + " in " + CityCalendar.formatShort(months.get(s.loAt()))
                  + " · high " + shown(t.unit(), s.hi()) + " in " + CityCalendar.formatShort(months.get(s.hiAt()))
                  + "\nThe hollow mark is where the view starts, the dot where it ends.";
        return new Reading(key, t.label(), never ? "not recorded here" : shown(t.unit(), s.last()),
                never ? "" : moveWords(t.unit(), s.first(), s.last()), caption, tip,
                s.first(), s.last(), s.lo(), s.hi(), never);
    }

    /** A figure as this page's cards and pins write it: fmtUnit()'s, with a true minus (0.7.37) - the chart's own card keeps fmtUnit()'s. */
    String shown(String unit, double v) {
        return fmtUnit(unit, v).replace('-', '\u2212');
    }

    /**
     * How far a line moved, as this page writes it (0.7.37, D5): an up or a
     * down arrow and changeText()'s figure without its sign - points for a
     * rate, per cent for a quantity - in neutral ink; a move too small to
     * show in the unit's places has no arrow ("0.0 pts").
     */
    String moveWords(String unit, double first, double last) {
        String said = changeText(unit, first, last);
        if (said.startsWith("+") || said.startsWith("-")) said = said.substring(1);
        said = said.replace('-', '−');
        if (last == first || said.matches("0(\\.0+)?( pts|%)?") || said.equals("no change")) return said;
        return (last > first ? "▲ " : "▼ ") + said;
    }

    /** The section: its head, the cards three across, and the line that says when two lines share a colour. */
    VBox readings(HistorySave h, boolean fresh) {
        VBox box = new VBox(8);
        box.getChildren().add(sectionHead("WHAT EACH LINE DID", null, hint("over the chart's view")));
        GridPane grid = equalColumns(3, 10);
        // On two axes, each card says which one its line is read against.
        List<String> units = pickedUnits();
        String[] colours = pickedColours();
        int k = 0;
        for (String key : historyPicked) {
            String side = units.size() != 2 ? null
                    : units.indexOf(traceFor(key).unit()) == 0 ? "left axis" : "right axis";
            VBox card = readingCard(h, key, bigInLayers() ? LAYERED_LINE : colours[k], side, fresh);
            GridPane.setFillHeight(card, true);
            card.setMaxHeight(Double.MAX_VALUE);
            grid.add(card, k % 3, k / 3);
            k++;
        }
        box.getChildren().add(grid);
        if (historyPicked.size() > Palette.LINE_COLOURS) {
            box.getChildren().add(infoLine(String.format("%d colours: two lines share one", Palette.LINE_COLOURS),
                    String.format("There are more lines than colours, so two of them share one. %d is as many "
                            + "as a chart can tell apart, and more than anybody reads at once.", Palette.LINE_COLOURS),
                    false, Palette.SIZE_LABEL, Palette.TEXT_MUTED, pageWidth));
        }
        return box;
    }

    /**
     * One line's card: its swatch, name and axis, "pin" or "pinned" (and
     * "layers" for real GDP alone); its figure and its move; its range bar;
     * the caption. A line switched off in the chart's legend is still read
     * here, at half strength. A month landing counts the figure on from the
     * last one and slides the dot (§5 of the spec).
     */
    VBox readingCard(HistorySave h, String key, String colour, String axis, boolean fresh) {
        Reading r = reading(h, key, axis);
        Trace t = traceFor(key);

        Region swatch = new Region();
        swatch.setMinSize(9, 9);
        swatch.setPrefSize(9, 9);
        swatch.setMaxSize(9, 9);
        swatch.setStyle("-fx-background-color: " + colour + "; -fx-background-radius: 2;");
        Label name = new Label(r.label());
        name.setWrapText(true);
        name.setMinWidth(40);
        name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
        HBox top = new HBox(6, swatch, name);
        top.setAlignment(Pos.CENTER_LEFT);
        if (axis != null) {
            Label side = new Label(axis);
            side.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            side.setMinWidth(Region.USE_PREF_SIZE);
            top.getChildren().add(side);
        }
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        top.getChildren().add(gap);
        // Real GDP picked alone can go into layers on the big chart (0.7.6).
        if (LAYERED.equals(key) && historyPicked.size() == 1) top.getChildren().add(layersChip("reading"));
        // Jerus: "pinnable defaults, but not fixed" - any line here can go up
        // onto a small chart, and one already there says so.
        boolean up = key.equals(pinned(true)) || key.equals(pinned(false));
        Label pin = up
                ? stillChip("pinned", true, "Already on a small chart above.")
                : chip("pin:" + key, "pin", false, () -> { pin(key); showHistoryMenu(); });
        if (!up) tip(pin, "Puts this line on a small chart above, in place of the older of the two.");
        pin.setMinWidth(Region.USE_PREF_SIZE);
        top.getChildren().add(pin);

        Label latest = new Label(r.latest());
        latest.setStyle(Palette.figure(Palette.SIZE_TITLE, r.never() ? Palette.TEXT_SPENT : Palette.TEXT_HEAD));
        latest.setMinWidth(Region.USE_PREF_SIZE);
        Label move = new Label(r.move());
        move.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.TEXT_BODY));
        move.setMinWidth(Region.USE_PREF_SIZE);
        HBox figures = new HBox(10, latest, move);
        figures.setAlignment(Pos.BASELINE_LEFT);

        VBox card = new VBox(5, top, figures);
        String was = "card:" + key;
        if (!r.never()) {
            Pieces.RangeBar bar = rangeBar(r.lo(), r.hi(), r.first(), r.last(), colour, 380);
            if (r.rangeTip() != null) tip(bar, r.rangeTip());
            card.getChildren().add(bar);
            if (fresh && shownBefore.containsKey(was)) {
                double before = shownBefore.get(was);
                SectorScreen.countUp(latest, before, r.last(), v -> shown(t.unit(), v));
                bar.slideFrom(bar.share(before), MOVE_MILLIS);
            }
            shownBefore.put(was, r.last());
        }
        Label caption = new Label(r.caption());
        caption.setWrapText(true);
        caption.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        card.getChildren().add(caption);
        if (hiddenLines.contains(key)) card.setOpacity(.5);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(Palette.block(Palette.RAISED, Palette.EDGE) + " -fx-padding: 10 12 10 12;");
        return card;
    }

    /** How long a figure takes to slide to a month's new reading, in milliseconds - the range bar's dot; the figure counts on over SectorScreen's own time. */
    static final double MOVE_MILLIS = 600;

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
                return String.format("%+.1f pts", unsigned0(delta * 100, 1));
            case "ratio": case "index": case "rate":
                return String.format("%+.3f", unsigned0(delta, 3));
            default:
                if (Math.abs(first) < 1e-9) return delta == 0 ? "no change" : "from nothing";
                // A line that crossed zero - a surplus turned deficit - moved by
                // neither a share nor a multiple of where it began: by its own
                // amount, in its own unit (0.7.37: it read "×-7").
                if ((first < 0) != (last < 0) && last != 0) return (delta > 0 ? "+" : "-") + fmtUnit(unit, Math.abs(delta));
                double share = delta / Math.abs(first);
                // A city that grew its output a thousandfold did not grow it by
                // "+136683.6%". Past a fivefold move the multiple is the figure
                // a reader can hold, and the percentage is noise with a sign on
                // it.
                if (Math.abs(share) >= 5) return String.format("\u00d7%,.0f", last / first);
                return String.format("%+.1f%%", unsigned0(share * 100, 1));
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
     *
     * A SECTION SINCE 0.7.37, PICK WHAT TO DRAW: the axes' paragraph in its
     * (i), and at its right how many lines are drawn of how many the page
     * offers (the old strip's DRAWING, which said "of 148 the city keeps"
     * when the history kept 69 more than the picker offered: D9 offers them
     * all) and the box; every group's heading with its icon, its rows as
     * wide as the page.
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

        VBox groups = new VBox(2);
        groups.setMaxWidth(Double.MAX_VALUE);
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

        Label drawn = hint(historyPicked.size() + " drawn · " + TRACES.length + " lines");
        HBox right = new HBox(10, drawn, find);
        right.setAlignment(Pos.CENTER_RIGHT);
        VBox all = new VBox(4, sectionHead("PICK WHAT TO DRAW", AXES_INFO, right), groups);
        all.setMaxWidth(Double.MAX_VALUE);
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
            // The heading in its area's colour (0.7.23), as the rail and the lines are.
            head.setStyle(head.getStyle() + " -fx-text-fill: " + groupArea(group) + ";");
            // ...with its icon (0.7.37).
            head.setGraphic(icon(groupIcon(group), groupArea(group), 12));
            head.setGraphicTextGap(6);
            Label count = new Label(picked + " of " + traces.size() + " picked");
            count.setStyle(Palette.words(Palette.SIZE_CAPTION,
                    picked > 0 ? Palette.TEXT_MUTED : Palette.TEXT_FAINT));
            HBox header = new HBox(Palette.GAP, head, count);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setStyle("-fx-padding: 6 0 2 0;");
            groups.getChildren().add(header);

            if (!open) continue;
            javafx.scene.layout.FlowPane row = new javafx.scene.layout.FlowPane(5, 5);
            row.setPrefWrapLength(pageWidth - 14);
            row.setMaxWidth(Double.MAX_VALUE);
            row.setStyle("-fx-padding: 2 0 6 14;");
            String[] colours = pickedColours();
            List<String> order = new ArrayList<>(historyPicked);
            for (Trace t : matching) {
                Label c = chip("line:" + t.key(), t.label(), historyPicked.contains(t.key()),
                        () -> {
                            if (!historyPicked.remove(t.key())) historyPicked.add(t.key());
                            // stays open under the pointer, even when its last line goes
                            groupOpen.put(group, true);
                            showHistoryMenu();
                        });
                // A picked line is underlined in the colour it is drawn in (0.7.23).
                int at = order.indexOf(t.key());
                if (at >= 0) c.setStyle(c.getStyle() + " -fx-border-color: " + colours[at] + ";");
                row.getChildren().add(c);
            }
            groups.getChildren().add(row);
        }

        if (groups.getChildren().isEmpty()) {
            groups.getChildren().add(sentence("No line is called that.", Palette.TEXT_MUTED));
        }
    }

    /** PICK WHAT TO DRAW's (i): how the lines share axes. */
    static final String AXES_INFO = "Lines measured in the same thing are drawn against each other on a real axis; "
            + "two units get an axis each, left and right; mix three or more and the chart falls back to each "
            + "line's own low-to-high, which compares shapes rather than sizes.";

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
            case "percent":   return String.format("%.1f%%", unsigned0(v * 100, 1));
            // Ground is kept per square foot in thousands, and the land screen
            // already shows it multiplied out. Two screens, one convention -
            // and in US dollars since 0.7.6, which is what the world asks.
            case "land":      return String.format("US$%.2f", unsigned0(v * 1000, 2));
            // A world price - food, materials, ore - kept in thousands like
            // every other price in the game. Multiplied out for the same reason
            // rent is: printed raw it reads as cents.
            case "unitprice": return String.format("$%,.2f", unsigned0(v * 1000, 2));
            case "ratio":     return String.format("%.2fx", unsigned0(v, 2));
            // Four places, because the whole point of watching a currency is
            // the small moves - a rate that reads 1.20 for thirty months is a
            // rate nobody can see drifting.
            case "rate":      return String.format("%.4f", unsigned0(v, 4));
            // Somebody else's money, and it says so. A dollar figure printed
            // with the same $ as the local one is the single most confusing
            // thing this screen could do.
            case "usd":       return "US" + tightMoney(v * 1000);
            case "index":     return String.format("%.3f", unsigned0(v, 3));
            /*
             * Rent is a price per person of housing capacity, kept in thousands
             * like every other price in the game - about 0.08 of them. Printed
             * with the "money" unit it rounded to $0 for the whole history,
             * which is what the play-test showed. Multiplied out it is the
             * figure a player recognises: about $82 a head a month.
             */
            case "rent":      return String.format("$%.2f", unsigned0(v * 1000, 2));
            // A share's price, kept in thousands like every price here, and
            // per FOUNDING share so a split is not a cliff on the chart.
            case "share":     return tightMoney(v * 1000, false);
            default:          return formatter.format(Math.round(v));
        }
    }

    /* =====================================================================
       HARD TIMES AND YOUR DECISIONS (0.7.37)

       What happened in the view (D6): the named episodes that touch the
       chart's window and the decisions inside it, two short lists side by
       side, each row a click that moves the chart there - an episode with a
       year either side, a decision five years either side - and nothing
       else: the picks and the pins stay. The whole history is behind
       "details": a row a kind that has happened, its episodes on one scale
       of the city's months with the window marked, and the decisions under
       them. The episodes are YearBook.episodes() and the decisions the
       DecisionLog, read; this page names nothing itself.

       NEW (§5 of the spec): an episode this page lists for the first time,
       the month it appears (noteEpisodes()), and a decision made this month.
       ===================================================================== */

    /** At most this many hard times are listed in view; the rest are counted, and in the details. */
    static final int HARD_TIMES_SHOWN = 5;

    /** At most this many decisions are listed in view. */
    static final int DECISIONS_SHOWN = 8;

    /** The kinds of episode, in the order the details list them. */
    static final String[] KINDS = {"recession", "depression", "slump", "epidemic", "financial", "currency",
            "inflation", "deflation", "treasury"};
    /** ...and what the details call each, in that order. */
    static final String[] KIND_NAMES = {"Recessions", "Depressions", "Slumps", "Epidemics", "Financial crises",
            "Currency crises", "Inflations", "Deflations", "Treasury crises"};

    /** The section's (i): the rules that name an episode, the year book's own. */
    static String hardTimesInfo() {
        StringBuilder b = new StringBuilder("A stretch of the city's life is named by the year book's own rules, each "
                + YearBook.triggerFooter() + ":");
        for (String kind : KINDS) b.append("\n\u2022 ").append(kind).append(": ").append(YearBook.trigger(kind));
        b.append("\nOne still running ").append(YearBook.CHRONIC_MONTHS / 12)
                .append(" years or more is listed after the others running.");
        return b.toString();
    }

    /**
     * The hard times touching the window, as this page lists them (pure):
     * those still running first, in RUNNING NOW's order, but a chronic one
     * last of all; then those the history closed, the most recently ended
     * first.
     */
    List<YearBook.Episode> hardTimesInView(List<YearBook.Episode> episodes, int lastMonth) {
        int from = chartWindow.firstMonthShown(), to = chartWindow.lastMonthShown();
        List<YearBook.Episode> running = YearBook.running(episodes, lastMonth);
        List<YearBook.Episode> out = new ArrayList<>(), chronic = new ArrayList<>(), closed = new ArrayList<>();
        for (YearBook.Episode e : running) {
            if (!touches(e, from, to)) continue;
            if (YearBook.isChronic(e)) chronic.add(e); else out.add(e);
        }
        for (YearBook.Episode e : episodes) if (touches(e, from, to) && !running.contains(e)) closed.add(e);
        closed.sort((a, b) -> a.toMonth() != b.toMonth() ? Integer.compare(b.toMonth(), a.toMonth())
                : Integer.compare(b.fromMonth(), a.fromMonth()));
        out.addAll(closed);
        out.addAll(chronic);
        return out;
    }

    /** The decisions inside the window, newest first (pure): a founding-month one counts at the history's first month, where its flag stands. */
    static List<DecisionLog.Entry> decisionsInView(List<ChartModel.Flag> flags, int from, int to) {
        List<DecisionLog.Entry> out = new ArrayList<>();
        for (ChartModel.Flag f : flags) if (f.month() >= from && f.month() <= to) out.addAll(f.entries());
        java.util.Collections.reverse(out);
        return out;
    }

    /** "Jun 2192 – May 2197 · 60 months", or "Aug 2199 – now · 6 months" while it runs. */
    static String spanWords(YearBook.Episode e, int lastMonth) {
        return CityCalendar.formatShort(e.fromMonth()) + " – "
                + (e.toMonth() == lastMonth ? "now" : CityCalendar.formatShort(e.toMonth())) + " · " + months(e.months());
    }

    /** "real output 13.78% below the year before at Feb 2193": the episode's worst, the year book's words. */
    static String worstLine(YearBook.Episode e) {
        return YearBook.worstWords(e.kind(), e.worst()) + " at " + CityCalendar.formatShort(e.worstMonth());
    }

    /** What the window spans, for an empty list: "these 10 years", "these 18 months", or "the whole history". */
    String viewWords() {
        if (chartWindow.showsAll()) return "the whole history";
        int n = chartWindow.monthsShown();
        return n >= 24 && n % 12 == 0 ? "these " + n / 12 + " years" : "these " + n + " months";
    }

    VBox hardTimes(HistorySave h, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags) {
        List<Integer> axis = h.getMonth();
        int last = axis.get(axis.size() - 1);
        VBox box = new VBox(8);
        HBox head = sectionHead("HARD TIMES AND YOUR DECISIONS", hardTimesInfo(), hint("click one to see it on the chart"));
        hardTimesTop = head;
        box.getChildren().add(head);

        // Left: the hard times touching the window.
        List<YearBook.Episode> shown = hardTimesInView(episodes, last);
        VBox left = new VBox(4, columnHead("Hard times in view", shown.size()));
        for (int i = 0; i < shown.size() && i < HARD_TIMES_SHOWN; i++) left.getChildren().add(episodeRow(shown.get(i), last));
        if (shown.size() > HARD_TIMES_SHOWN) {
            left.getChildren().add(door("+" + (shown.size() - HARD_TIMES_SHOWN) + " more in view", Palette.ACCENT, () -> {
                folds.add("hard times");
                scrollTarget = TO_HARD_TIMES;
                showHistoryMenu();
            }));
        }
        if (shown.isEmpty()) {
            left.getChildren().add(quiet("no named trouble in " + viewWords()));
            if (!chartWindow.showsAll()) left.getChildren().add(showAll());
        }

        // Right: the decisions inside it.
        DecisionLog log = ui.game.getDecisions();
        List<DecisionLog.Entry> decided = decisionsInView(flags, chartWindow.firstMonthShown(), chartWindow.lastMonthShown());
        VBox right = new VBox(4, columnHead("Your decisions in view", decided.size()));
        for (int i = 0; i < decided.size() && i < DECISIONS_SHOWN; i++) right.getChildren().add(decisionRow(decided.get(i)));
        if (decided.size() > DECISIONS_SHOWN) {
            right.getChildren().add(door("+" + (decided.size() - DECISIONS_SHOWN) + " more", Palette.ACCENT, () -> {
                folds.add("hard times");
                scrollTarget = TO_HARD_TIMES;
                showHistoryMenu();
            }));
        }
        if (decided.isEmpty()) {
            right.getChildren().add(quiet("no decisions in " + viewWords()
                    + (log.size() == 0 ? "" : String.format(" · %,d since founding", log.size()))));
            if (!chartWindow.showsAll() && log.size() > 0) right.getChildren().add(showAll());
        }

        GridPane columns = equalColumns(2, 10);
        for (VBox col : new VBox[] {left, right}) {
            col.setMaxWidth(Double.MAX_VALUE);
            col.setMaxHeight(Double.MAX_VALUE);
            GridPane.setFillHeight(col, true);
            col.setStyle(Palette.block(Palette.RAISED, Palette.EDGE) + " -fx-padding: 10 12 10 12;");
        }
        columns.add(left, 0, 0);
        columns.add(right, 1, 0);
        box.getChildren().add(columns);
        box.getChildren().add(fold("hard times", String.format("every named one since founding (%,d), by kind", episodes.size()),
                () -> byKind(h, episodes, flags)));
        return box;
    }

    /** A column's head: its words, and how many. */
    HBox columnHead(String words, int count) {
        Label l = new Label(words);
        l.setStyle(Palette.strong(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        Label n = new Label(count == 0 ? "none" : String.format("%,d", count));
        n.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        HBox row = new HBox(8, l, n);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 0 0 4 0;");
        return row;
    }

    /** A quiet line: an empty list's words. */
    static Label quiet(String text) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_MUTED));
        return l;
    }

    /** "show All ›": the whole history in view. */
    Label showAll() {
        return door("show All", Palette.ACCENT, () -> {
            chartWindow.showRange(ChartModel.ALL);
            showHistoryMenu();
        });
    }

    /** A row that moves the chart: the pointer's hand, a ground under it, and the click. */
    void rowGoes(javafx.scene.layout.Pane row, String tipText, Runnable go) {
        String rest = "-fx-padding: 4 6 4 6; -fx-background-radius: 6; -fx-cursor: hand;";
        row.setStyle(rest);
        row.setOnMouseEntered(e -> row.setStyle(rest + " -fx-background-color: " + Palette.CONTROL + ";"));
        row.setOnMouseExited(e -> row.setStyle(rest));
        row.setOnMouseClicked(e -> go.run());
        tip(row, tipText);
    }

    /**
     * One hard time: its kind's icon and tag in its colour (the chart's
     * verdict colours, 0.7.23), its name, its span; under it its worst, in
     * the year book's words. NEW the month it is first listed.
     */
    HBox episodeRow(YearBook.Episode e, int lastMonth) {
        String colour = TimeChart.episodeColour(e.kind());
        Label name = new Label(e.name());
        name.setWrapText(true);
        name.setMinWidth(60);
        name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
        HBox line = new HBox(6, tag(e.kind(), colour), name);
        if (isNew(e)) line.getChildren().add(tag("new", colour));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label span = new Label(spanWords(e, lastMonth));
        span.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        span.setMinWidth(Region.USE_PREF_SIZE);
        line.getChildren().addAll(gap, span);
        line.setAlignment(Pos.CENTER_LEFT);
        Label worst = new Label(worstLine(e));
        worst.setWrapText(true);
        worst.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
        VBox words = new VBox(1, line, worst);
        HBox.setHgrow(words, Priority.ALWAYS);
        words.setMinWidth(0);
        HBox row = new HBox(8, icon(Icons.ofEpisode(e.kind()), colour, 14), words);
        row.setAlignment(Pos.TOP_LEFT);
        rowGoes(row, "Moves the chart to " + e.name() + ", a year either side.", () -> moveChartTo(e));
        return row;
    }

    /** One decision: its icon in its flag's area colour, its month, its words - wrapping, never cut; "this month" when it is. */
    HBox decisionRow(DecisionLog.Entry d) {
        String colour = TimeChart.flagColour(d.kind());
        Label when = new Label(CityCalendar.formatShort(d.month()));
        when.setStyle(Palette.figure(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        when.setMinWidth(Region.USE_PREF_SIZE);
        Label what = new Label(d.label());
        what.setWrapText(true);
        what.setMinWidth(60);
        what.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
        HBox.setHgrow(what, Priority.ALWAYS);
        HBox row = new HBox(8, icon(Icons.ofDecision(d.kind()), colour, 13), when, what);
        if (d.month() == ui.game.getMonth()) row.getChildren().add(tag("this month", colour));
        row.setAlignment(Pos.CENTER_LEFT);
        rowGoes(row, "Moves the chart to " + CityCalendar.formatShort(d.month()) + ", five years either side.",
                () -> moveChartTo(d.month() - 60, d.month() + 60));
        return row;
    }

    /**
     * A fold on this page (Pieces.details()), its toggle held under the
     * pointer as it opens or closes, a chip's way (D7, D8): the page under
     * it changes length and the scroll memory keeps the bottom.
     */
    VBox fold(String key, String caption, java.util.function.Supplier<javafx.scene.Node> body) {
        VBox box = details(key, caption, folds, this::showHistoryMenu, body);
        if (!box.getChildren().isEmpty() && box.getChildren().get(0) instanceof Label toggle) {
            named.put("fold:" + key, toggle);
            toggle.setOnMouseClicked(e -> {
                pressed = "fold:" + key;
                pressedAt = toggle.localToScene(0, 0).getY();
                if (!folds.remove(key)) folds.add(key);
                showHistoryMenu();
            });
        }
        return box;
    }

    /** One kind of episode in the details, as words (pure): its kind, how many, how long all told, and its episodes. */
    record KindRow(String kind, String name, int count, int months, List<YearBook.Episode> episodes) { }

    static List<KindRow> kindRows(List<YearBook.Episode> episodes) {
        List<KindRow> out = new ArrayList<>();
        for (int k = 0; k < KINDS.length; k++) {
            List<YearBook.Episode> of = new ArrayList<>();
            for (YearBook.Episode e : episodes) if (KINDS[k].equals(e.kind())) of.add(e);
            if (of.isEmpty()) continue;
            out.add(new KindRow(KINDS[k], KIND_NAMES[k], of.size(), YearBook.monthsIn(episodes, KINDS[k]), of));
        }
        return out;
    }

    /** "42 years" all told, or "9 months" under two years. */
    static String howLong(int months) {
        return months >= 24 ? String.format("%,d years", months / 12) : months(months);
    }

    /**
     * Every named episode since founding, by kind (0.7.37): a row a kind
     * that has happened, its episodes on one scale of the history's months
     * in their colour - each at least two pixels, its name, span and worst
     * on its tooltip and a click that moves the chart to it - and the
     * decisions under them, a tick each; the window in view between two
     * dashed rules.
     */
    javafx.scene.Node byKind(HistorySave h, List<YearBook.Episode> episodes, List<ChartModel.Flag> flags) {
        List<Integer> axis = h.getMonth();
        int first = axis.get(0), last = axis.get(axis.size() - 1);
        double scale = last - first + 1;
        List<ScaleRow> rows = new ArrayList<>();
        for (KindRow k : kindRows(episodes)) {
            List<Run> runs = new ArrayList<>();
            for (YearBook.Episode e : k.episodes()) {
                runs.add(Run.of(e.fromMonth() - first, e.months(), TimeChart.episodeColour(e.kind()))
                        .tip(e.name() + "\n" + spanWords(e, last) + "\n" + worstLine(e))
                        .go(() -> moveChartTo(e)));
            }
            rows.add(ScaleRow.of(k.name(), String.format("%,d · %s", k.count(), howLong(k.months())), runs)
                    .icon(Icons.ofEpisode(k.kind())));
        }
        List<Tick> ticks = new ArrayList<>();
        int decisions = 0;
        for (ChartModel.Flag f : flags) {
            for (DecisionLog.Entry d : f.entries()) {
                ticks.add(new Tick(f.month() - first + .5, TimeChart.flagColour(d.kind()), 2, null,
                        CityCalendar.formatShort(d.month()) + "  " + d.label()));
                decisions++;
            }
        }
        rows.add(ScaleRow.of("Your decisions", String.format("%,d", decisions), List.of()).marks(ticks)
                .icon(Icons.PIN));
        List<Rule> rules = List.of(
                new Rule(chartWindow.firstMonthShown() - first, Palette.ACCENT, true, "in view",
                        "The chart's view: " + CityCalendar.formatShort(chartWindow.firstMonthShown()) + " – "
                                + CityCalendar.formatShort(chartWindow.lastMonthShown()), null),
                new Rule(chartWindow.lastMonthShown() - first + 1, Palette.ACCENT, true, null, null, null));
        return scaleRows(rows, scale, rules, 170, 130, 10);
    }

    /* =====================================================================
       PRICES THIS MONTH (0.7.37; EVERY GOOD, ON ONE PAGE before)

       Jerus: "somewhere somehow, i should be able to see all the goods, and
       the current prices and some quick info". There was nowhere: a good's
       price appeared only on the screen of whichever sector happened to
       make it, and the foods had no screen at all because no sector makes
       them. Every good in the city and no index of them is a model you have
       to read the source to see. And, on the proposal: "leave goods there".

       The band is the whole story for a traded good - what the world pays
       for one and what it charges to land one - so the row says where in
       that band the city's price has ended up (GoodsMarket.getPriceIndex()),
       and what crossed the border this month. Since 0.7.37 that is one line
       of counts over a fold (D7), and in the fold a row a good on the band's
       own scale: the band hollow from the world's floor to its ceiling, the
       city's price a tick across it. A good open at one end says which; a
       good its seller prices (Pricing.SELLER) says so and opens the seller,
       never last month's price (B7: its market keeps no local price, and its
       trades are not saved).
       ===================================================================== */

    /** How near an end of its band a price must be to be AT it - a rounding hair of the band (the market strikes an end exactly). */
    static final double AT_END = 1e-6;

    /** The scale a good's row is drawn on: the band from 0 to 1, and room past its ceiling for the month's trade. */
    static final double BAND_ROOM = 1.25;

    /** The section's (i). */
    static final String GOODS_INFO = "What a unit costs here this month, against what the world pays for one and "
            + "what it charges to land one - the band a traded good's price is struck inside. A good open at one "
            + "end has no world price at the other; a good with no band is priced by whoever sells it, in its own "
            + "sales.";

    /** A good's market, or null. */
    GoodsMarket market(Good g) {
        return ui.game.getMarkets().get(g);
    }

    /** The business that sells a seller-priced good: the sector that makes it, or null. */
    Sector seller(Good g) {
        for (Sector s : ui.game.getSectors().all()) if (s.isMaker(g)) return s;
        return null;
    }

    /** A good's price, to the cent: "$296.67", "$11,454.16" - tiny ones to the place that shows them. */
    static String goodPrice(double thousands) {
        double d = thousands * 1000;
        if (Math.abs(d) >= .01) return String.format("$%,.2f", unsigned0(d, 2));
        return unitPrice(thousands);
    }

    /** "imported 54,201", "exported 87,294", both, or null when nothing crossed; a fraction of a unit is "under 1" (a wagon set's worth read "imported 0"). */
    static String flowWords(GoodsMarket m) {
        if (m == null) return null;
        double in = m.getImported(), out = m.getExported();
        String said = (in > .005 ? "imported " + units(in) : "")
                + (in > .005 && out > .005 ? " · " : "")
                + (out > .005 ? "exported " + units(out) : "");
        return said.isEmpty() ? null : said;
    }

    /** A count of units that crossed: whole ones grouped, or "under 1". */
    static String units(double v) {
        return v >= .5 ? String.format("%,.0f", v) : "under 1";
    }

    /** "1 at", "none at": a count in the summary's words. */
    static String some(int n) {
        return n == 0 ? "none" : String.valueOf(n);
    }

    /**
     * The line over the fold (pure): how many goods trade with the world and
     * where in their bands the both-ways ones stand - at what the world
     * charges, between, at what it pays - how many are open at one end, and
     * how many their seller prices.
     */
    String priceSummary() {
        int all = 0, traded = 0, ceiling = 0, between = 0, floor = 0, oneSide = 0, seller = 0;
        for (Good g : Good.values()) {
            all++;
            if (!g.traded()) { seller++; continue; }
            traded++;
            if (!(g.exportable() && g.importable())) { oneSide++; continue; }
            GoodsMarket m = market(g);
            double at = m == null ? Double.NaN : m.getPriceIndex();
            if (at >= 1 - AT_END) ceiling++;
            else if (at <= AT_END) floor++;
            else between++;
        }
        return String.format("%d of %d goods trade with the world: %s at what the world charges, %s between, "
                + "%s at what it pays · %s open on one side · %s set by their seller",
                traded, all, some(ceiling), some(between), some(floor), some(oneSide), some(seller));
    }

    /** The section: its head with the (i), the line of counts, and every good behind "details". */
    VBox prices() {
        VBox box = new VBox(8);
        box.getChildren().add(sectionHead("PRICES THIS MONTH", GOODS_INFO, null));
        Label summary = new Label(priceSummary());
        summary.setWrapText(true);
        summary.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_BODY));
        box.getChildren().add(summary);
        box.getChildren().add(fold("goods", "every good's price this month (" + Good.values().length + ")", this::goodsRows));
        return box;
    }

    /** One good as a row of the fold, as words (pure): what the probe reads. */
    record GoodLine(Good good, String figure, String says, double at, String flow) { }

    /** Every good, as the fold draws it: the both-ways goods with their place in the band, the rest with their words. */
    List<GoodLine> goodLines() {
        List<GoodLine> out = new ArrayList<>();
        for (Good g : Good.values()) {
            GoodsMarket m = market(g);
            double local = m == null ? 0 : m.getLocalPrice();
            String figure = local > 0 ? goodPrice(local) + " /" + g.unit() : "";
            String flow = flowWords(m);
            if (!g.traded()) {
                Sector s = seller(g);
                out.add(new GoodLine(g, s == null ? "" : s.key() + " ›", "set by the seller", Double.NaN, null));
            } else if (m == null) {
                out.add(new GoodLine(g, figure, "not traded this month", Double.NaN, flow));
            } else if (g.exportable() && g.importable()) {
                out.add(new GoodLine(g, figure, "between " + goodPrice(m.exportPrice()) + " and " + goodPrice(m.importPrice()),
                        m.getPriceIndex(), flow));
            } else if (g.exportable()) {
                out.add(new GoodLine(g, figure, "above the world's floor of " + goodPrice(m.exportPrice()) + " · no ceiling"
                        + (flow == null ? "" : " · " + flow), Double.NaN, flow));
            } else {
                out.add(new GoodLine(g, figure, "under the world's ceiling of " + goodPrice(m.importPrice()) + " · no floor"
                        + (flow == null ? "" : " · " + flow), Double.NaN, flow));
            }
        }
        return out;
    }

    /**
     * The fold: the goods traded both ways on their bands, the world's floor
     * and ceiling a rule through every row; then those open at one end and
     * those their seller prices, in words. Right after a load the railway's
     * freight on each good is not struck yet, so a band can step a month on;
     * a line says so (TradeScreen's B14).
     */
    javafx.scene.Node goodsRows() {
        VBox box = new VBox(10);
        if (!ui.game.getForeignAccounts().isMonthCounted()) {
            box.getChildren().add(quiet("Just loaded: the railway's freight on each good is struck when the month "
                    + "turns, so these bands may step a month on."));
        }
        List<ScaleRow> both = new ArrayList<>(), oneSide = new ArrayList<>(), sellers = new ArrayList<>();
        for (GoodLine l : goodLines()) {
            Good g = l.good();
            if (!g.traded()) {
                Sector s = seller(g);
                ScaleRow r = ScaleRow.of(g.label(), l.figure(), List.of()).empty(l.says()).icon(Icons.ofGood(g));
                if (s != null) {
                    r = r.go(() -> ui.sectorScreen.openSectorBooks(s, SectorScreen.SECTOR_PAGES[0]))
                            .tip("Its price is struck in " + s.label() + "'s own sales: opens Sectors › " + s.label() + ".");
                }
                sellers.add(r);
            } else if (!Double.isNaN(l.at())) {
                GoodsMarket m = market(g);
                double at = Math.max(0, Math.min(1, l.at()));
                String where = String.format("%s here, %.0f%% of the way from what the world pays (%s) to what it charges (%s)",
                        goodPrice(m.getLocalPrice()), unsigned0(l.at() * 100, 0), goodPrice(m.exportPrice()), goodPrice(m.importPrice()));
                ScaleRow r = ScaleRow.of(g.label(), l.figure(), List.of(Run.of(0, 1, Palette.BUSINESS).outlined().tip(where)))
                        .marks(List.of(new Tick(at, Palette.TEXT_HEAD, 3, null, where)))
                        .icon(Icons.ofGood(g));
                if (l.flow() != null) r = r.tag(l.flow(), Palette.TEXT_LABEL);
                both.add(r);
            } else {
                oneSide.add(ScaleRow.of(g.label(), l.figure(), List.of()).empty(l.says()).icon(Icons.ofGood(g)));
            }
        }
        if (!both.isEmpty()) {
            box.getChildren().add(scaleRows(both, BAND_ROOM, List.of(
                    new Rule(0, Palette.TEXT_LABEL, true, "what the world pays", "The export price: what the world pays for one, here", null),
                    new Rule(1, Palette.TEXT_LABEL, true, "what it charges", "The import price: what one costs landed from the world", null)),
                    220, 170, 10));
        }
        List<ScaleRow> rest = new ArrayList<>();
        if (!oneSide.isEmpty()) {
            rest.add(ScaleRow.caption("Open at one end", "A good the world only buys has a floor and no ceiling; one it only sells, a ceiling and no floor. On the open side the city's market sets the bound: twice the floor where the world sets no ceiling, and zero where it sets no floor."));
            rest.addAll(oneSide);
        }
        if (!sellers.isEmpty()) {
            rest.add(ScaleRow.caption("Set by their seller", "These have no world band: the business that sells them sets the price, and it is in that business's sales, month by month. Their trades are not saved, so this page shows no price for them."));
            rest.addAll(sellers);
        }
        if (!rest.isEmpty()) box.getChildren().add(scaleRows(rest, 1, List.of(), 220, 170, 10));
        return box;
    }
}
