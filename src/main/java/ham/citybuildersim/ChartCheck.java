package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The charts (0.7.23): the player's decisions as the model records them,
 * and the arithmetic City History's charts are drawn by - the window, the
 * ticks, the scales, the bands, the lanes and the flags, and the years under
 * every other chart - held to its own rules without a screen.
 *
 * WHY. Jerus asked for charts "just like yahoo finance", crisis labels that
 * are clear, and the player's own decisions on the timeline. A decision is a
 * flow the state cannot give back, so it is recorded where it is applied
 * (DecisionLog) and saved; a chart is a window of months and a set of
 * labels, and the arithmetic of both is ChartModel's, which needs no
 * toolkit. The screen is checked by eye on the PC; everything it draws by
 * is checked here.
 *
 * What this has to prove:
 *   1. THE DECISION LOG records each kind of decision once, at the month it
 *      was made, with its label - a tax, a promise, the central bank, the
 *      money, the paper, the bank, the fund and the queue - and nothing
 *      that changes nothing; a city built and played by the fixture's hand
 *      records nothing, ordinary build orders included; the founding's and
 *      the load's own settings are not decisions; the rollover's issues are
 *      not decisions either.
 *   2. IT SURVIVES A SAVE AND A LOAD, entry for entry, and a format-28 save
 *      loads with an empty log.
 *   3. THE CHART'S SPANS ARE YEARBOOK'S: the bands are exactly recessions(),
 *      each carrying the name of the recession that holds it, its depth the
 *      year book's own; the episode lane is exactly episodes(), and no two
 *      that overlap share a row; every kind says the rule that named it; and
 *      (0.7.37) what is running is what the history has not closed, a
 *      crisis before a watch, the newest first, the chronic last.
 *   4. THE TICKS: every window, at every width, has years on its axis - on
 *      January, every tick at least MONTH_LABEL_PX apart - and months only
 *      when they fit; a value axis steps by one, two or five times a power
 *      of ten and holds what is drawn, a per cent from zero; and the new
 *      city's flat rate and flat price level no longer lie on each other.
 *   5. PAN AND ZOOM CLAMP TO THE DATA: a drag stops at either end, a wheel at
 *      MIN_SPAN and at the whole history, the month under the pointer stays
 *      under it, and a window on the newest month follows it.
 *   6. THE FLAGS: one a month, with the count; flags too close to part drawn
 *      as one; and (0.7.37) a decision from the founding month, before the
 *      history's first month, on the lane at that first month.
 *   7. A YOUNG CITY'S WINDOW GROWS INTO ITS RANGE (after the docs pass): a
 *      history shorter than the range is shown whole and then, as it grows,
 *      is the last ten years - or whichever range was pressed - every
 *      month, with that range still the one lit; a window moved by hand
 *      keeps its own width, and only one the player zoomed out to
 *      everything stays everything; a double-click puts it back on its
 *      range.
 *
 * Every fixture causes its condition.
 */
public class ChartCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-92s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void same(String label, Object actual, Object expected) {
        boolean ok = expected == null ? actual == null : expected.equals(actual);
        if (!ok) fails++;
        out.printf("%-92s %s  %s against %s%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        out.printf("%-92s %s  %.9f against %.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static BuildingsTemplate template(Game g, String name) {
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    /**
     * A played city with a little of everything a decision touches: works,
     * people, a bank, depots, every sector held so no planner orders anything,
     * and on site a University and two Middle Schools of the city's own.
     */
    static Game city(Path root, String name) {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 50_000_000L);
            for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
            for (String[] w : new String[][] {
                    { "Industrial Bakery", "3" }, { "Coal Power Plant", "1" }, { "Water Treatment Plant", "1" },
                    { "Convenience Store", "6" }, { "House", "400" }, { "Elementary School", "3" },
                    { "Walk-in Clinic", "2" }, { "Construction Depot", "4" }, { "Paved Road", "4" } }) {
                g.buildStack(template(g, w[0]), Integer.parseInt(w[1]), true);
            }
            g.simulateMonths(24);
            g.setCashForTest(20_000_000);
            g.buildStack(template(g, "University"), 1, false);
            g.buildStack(template(g, "Middle School"), 2, false);
        });
        return g;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("chartcheck");

        Game g = theLogRecordsEachKind(root);
        theLogSurvivesASave(root, g);
        theSpansAreTheYearBooks();
        theTicks();
        panAndZoomClamp();
        theFlags();
        aYoungCitysWindow();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. THE DECISION LOG ============================ */

    /** The entries made since `from`, by label. */
    static List<String> labelsSince(DecisionLog log, int from) {
        List<String> out = new ArrayList<>();
        for (int i = from; i < log.size(); i++) out.add(log.entries().get(i).label());
        return out;
    }

    /** One decision: the hand, then exactly one new entry, at this month, of this kind, saying this. */
    static void decides(Game g, String what, String kind, String label, Runnable hand) {
        decides(g, what, kind, () -> label, hand);
    }

    /** ...with what it should say worked out after the hand, from what the hand did: a face, a clamped floor. */
    static void decides(Game g, String what, String kind, java.util.function.Supplier<String> saying, Runnable hand) {
        DecisionLog log = g.getDecisions();
        int before = log.size();
        quietly(hand);
        String label = saying.get();
        List<String> made = labelsSince(log, before);
        boolean one = made.size() == 1;
        DecisionLog.Entry e = one ? log.last() : null;
        assertTrue(String.format("%s: one entry, \"%s\"", what, label),
                one && label.equals(e.label()) && kind.equals(e.kind()) && e.month() == g.getMonth());
        if (!one || !label.equals(made.get(0))) out.println("        recorded: " + made);
    }

    /** ...and the same hand again, changing nothing, records nothing. */
    static void changesNothing(Game g, String what, Runnable hand) {
        DecisionLog log = g.getDecisions();
        int before = log.size();
        quietly(hand);
        assertTrue(what + " again, to what it already is: nothing recorded", log.size() == before);
    }

    /** The face of the paper the city issued last: the newest debt on its books. */
    static double newestFace(Game g) {
        List<Debt> debts = g.getDebtManager().getDebt();
        return debts.isEmpty() ? 0 : debts.get(debts.size() - 1).getOustandingPrincipal();
    }

    static Game theLogRecordsEachKind(Path root) {
        out.println("--- 1. the decision log: each kind once, at its month, with its label ---");

        Game fresh = new Game(new GameFiles(root.resolve("fresh"), root.resolve("fresh-no-legacy")));
        quietly(fresh::newGame);
        assertTrue("a new city's own settings - the rule's dial, the rollover, the rescue - are not decisions",
                fresh.getDecisions().size() == 0 && fresh.getDebtManager().isAutopilot()
                        && fresh.getRolloverMode() == Rollover.Mode.SAME_STRUCTURE);

        Game g = city(root, "decided");
        assertTrue("a city built, ordered and played two years by the fixture's hand records nothing - "
                + "ordinary build orders are not decisions", g.getDecisions().size() == 0 && g.getMonth() > 20);
        TaxPolicy tax = g.getEconomyManager().getTaxPolicy();

        // A tax, a promise, the central bank, the money, the paper, the bank, the fund and the queue.
        decides(g, "every tax at once", DecisionLog.TAX, "Taxes to 17%", () -> tax.setIncomeTaxRate(.17));
        changesNothing(g, "every tax at once", () -> tax.setIncomeTaxRate(.17));
        decides(g, "the profit tax alone", DecisionLog.TAX, "Profit tax to 15.5%", () -> tax.setProfitTaxRate(.155));
        decides(g, "the property tax", DecisionLog.TAX, "Property tax to 1.25% a year", () -> tax.setPropertyTaxRate(.0125));
        decides(g, "a sector's offset", DecisionLog.TAX, "Sales tax, " + Sectors.RETAIL + ", to -2 pts",
                () -> tax.setSalesOffset(Sectors.RETAIL, -.02));
        decides(g, "the wage floor", DecisionLog.PROMISE,
                () -> "Wage floor to " + DecisionLog.money(g.getLabourMarket().getMinimumWage()) + " a month",
                () -> g.getLabourMarket().setMinimumWage(g.getLabourMarket().getMinimumWage() * 1.1));
        decides(g, "pensions", DecisionLog.PROMISE, "Pensions to 30% of a wage", () -> tax.setPensionReplacement(.30));
        // The grant's basis and amount in one call, as the Policy screen applies a basis chip (after the
        // docs pass): one line, at the figure chosen - not the old amount under the new basis first.
        decides(g, "the grant to a share of the surplus, basis and amount at once", DecisionLog.PROMISE,
                "Student grant to 3% of the surplus", () -> tax.setGrant(TaxPolicy.GrantBasis.SURPLUS_SHARE, .03));
        decides(g, "the city's share of tuition", DecisionLog.PROMISE, "City's share of tuition to 75%",
                () -> g.getEducation().setTuitionSubsidy(.75));
        decides(g, "a standing subsidy", DecisionLog.PROMISE, Sectors.CONSTRUCTION + " subsidised as standing policy",
                () -> g.setAutoSubsidised(Sectors.CONSTRUCTION, true));
        decides(g, "the central bank's rate, by hand", DecisionLog.CENTRAL_BANK, "Central bank rate to 0.00%",
                () -> g.getDebtManager().takeTheDial(0));
        changesNothing(g, "the rate", () -> g.getDebtManager().takeTheDial(0));
        decides(g, "the rate handed to the rule", DecisionLog.CENTRAL_BANK, "Central bank rate handed to the rule",
                () -> g.getDebtManager().setAutopilot(true));
        decides(g, "the inflation target", DecisionLog.CENTRAL_BANK, "Inflation target to 3%",
                () -> g.getDebtManager().setInflationTarget(.03));
        decides(g, "the holdings dial", DecisionLog.CENTRAL_BANK, "Central bank to hold 20% of the city's paper",
                () -> g.getCentralBank().setTargetShare(.20));
        decides(g, "reserves bought", DecisionLog.CURRENCY, "Bought reserves for $5.0M",
                () -> g.buyForeignCurrency(5_000));
        decides(g, "land paid from the vault", DecisionLog.CURRENCY, "Land paid from the vault",
                () -> g.setLandPaidFromVault(true));
        decides(g, "a note", DecisionLog.BORROWING, () -> "Borrowed " + DecisionLog.money(newestFace(g)) + ": 6-month note",
                () -> g.handleTBillLogic(10_000, 6, Game.BUILD_NOTE_GRANULE));
        decides(g, "a term bond", DecisionLog.BORROWING, () -> "Borrowed " + DecisionLog.money(newestFace(g)) + ": 20-year term bond",
                () -> g.handleLongBondLogic(20_000, 20, Game.BUILD_BOND_GRANULE));
        decides(g, "the rollover's setting", DecisionLog.BORROWING, "Rollover on: each piece into its own kind",
                () -> g.setRolloverMode(Rollover.Mode.SAME_STRUCTURE));
        decides(g, "the rescue setting", DecisionLog.BANK, "A failed bank to be resolved at once",
                () -> g.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC));
        decides(g, "the fund's dial", DecisionLog.FUND, "Fund dial to 50% of the surplus", () -> g.setFundDial(.5));
        decides(g, "money into the fund", DecisionLog.FUND, "Paid $2.0M into the fund", () -> g.fundPayIn(2_000));
        String university = ConstructionControl.keyOf(template(g, "University"));
        assertTrue("fixture: the University and the Middle Schools are the city's sites, the University first",
                g.getBuildingManager().cityOrder().indexOf(university) == 0
                        && g.getBuildingManager().cityOrder().indexOf(ConstructionControl.keyOf(template(g, "Middle School"))) > 0);
        decides(g, "a rush", DecisionLog.CONSTRUCTION, "Rushed University", () -> g.rushSite(university, true));
        changesNothing(g, "the rush", () -> g.rushSite(university, true));
        decides(g, "the order", DecisionLog.CONSTRUCTION, "Middle School up the city's order, to 1",
                () -> g.moveSite(ConstructionControl.keyOf(template(g, "Middle School")),
                        -g.getBuildingManager().cityOrder().indexOf(ConstructionControl.keyOf(template(g, "Middle School")))));
        decides(g, "a cancel", DecisionLog.CONSTRUCTION, "Cancelled Middle School",
                () -> g.cancelSite(ConstructionControl.keyOf(template(g, "Middle School")), true));
        final double[] price = new double[1];
        quietly(() -> price[0] = g.quoteDemolition(template(g, "Walk-in Clinic"), 1).price().total);
        decides(g, "a demolition", DecisionLog.CONSTRUCTION,
                "Demolished 1 Walk-in Clinic for " + DecisionLog.money(price[0]),
                () -> g.demolish(template(g, "Walk-in Clinic"), 1));
        int decidedIn = g.getMonth();

        // A month on, the decisions are dated by the month they were made in.
        quietly(() -> g.simulateMonths(1));
        decides(g, "a decision the month after", DecisionLog.TAX, "Taxes to 18%", () -> tax.setIncomeTaxRate(.18));
        DecisionLog log = g.getDecisions();
        int earlier = 0;
        for (DecisionLog.Entry e : log.entries()) if (e.month() == decidedIn) earlier++;
        assertTrue("every decision of the first month is dated that month, the next one the next",
                earlier == log.size() - 1 && log.last().month() == decidedIn + 1);
        java.util.Set<String> kinds = new java.util.TreeSet<>();
        for (DecisionLog.Entry e : log.entries()) kinds.add(e.kind());
        same("every kind recorded", kinds.size(), 8);

        // The rollover's own issues are the setting at work, not decisions.
        int rolled = g.getRollover().getIssuesLifetime();
        int borrowing = 0;
        for (DecisionLog.Entry e : log.entries()) if (e.kind().equals(DecisionLog.BORROWING)) borrowing++;
        final int sizeBefore = log.size();
        quietly(() -> {
            // Big enough that no surplus could net it and well over the smallest issue worth arranging.
            g.handleTBillLogic(2_000_000, 3, Game.BUILD_NOTE_GRANULE);
            for (int m = 0; m < 4; m++) {
                g.setCashForTest(0);
                g.simulateMonths(1);
            }
        });
        int borrowingAfter = 0;
        for (DecisionLog.Entry e : log.entries()) if (e.kind().equals(DecisionLog.BORROWING)) borrowingAfter++;
        assertTrue("fixture: the treasury emptied and a note falling due, the rollover issued",
                g.getRollover().getIssuesLifetime() > rolled);
        same("...and the log has the player's note and none of the rollover's", borrowingAfter, borrowing + 1);
        same("...nor anything else from the months", log.size(), sizeBefore + 1);
        return g;
    }

    /* ============================ 2. A SAVE AND A LOAD ============================ */

    static void theLogSurvivesASave(Path root, Game g) throws Exception {
        out.println("\n--- 2. the log survives a save and a load; a format-28 save loads with none ---");
        final boolean[] saved = { false };
        quietly(() -> saved[0] = g.saveGame(1, "decided").ok);
        assertTrue("saved", saved[0]);
        GameFiles files = new GameFiles(root.resolve("decided"), root.resolve("decided-no-legacy"));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(1));
        Gson gson = new GsonBuilder().create();
        same("as many decisions back as were written", back.getDecisions().size(), g.getDecisions().size());
        assertTrue("...each with its month, its kind and its label, in order",
                gson.toJson(back.getDecisions().toState()).equals(gson.toJson(g.getDecisions().toState())));
        assertTrue("...and the load's own settings - every dial put back through its setter - added none",
                back.getDecisions().size() == g.getDecisions().size() && !back.getDecisions().isHeld());
        int before = back.getDecisions().size();
        quietly(() -> back.getEconomyManager().getTaxPolicy().setIncomeTaxRate(.2));
        assertTrue("a loaded city records the next decision", back.getDecisions().size() == before + 1
                && back.getDecisions().last().month() == back.getMonth());

        Path file = files.saveFile(1);
        JsonObject json = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
        assertTrue("the save says this build's format, " + GameVersion.SAVE_FORMAT + ", and carries the log",
                json.get("saveFormat").getAsInt() == GameVersion.SAVE_FORMAT && json.has("decisionLog"));
        assertTrue("...the format the log came in, 29, or later", GameVersion.SAVE_FORMAT >= 29);
        json.remove("decisionLog");
        json.addProperty("saveFormat", 28);
        Files.writeString(files.saveFile(2), new GsonBuilder().setPrettyPrinting().create().toJson(json),
                StandardCharsets.UTF_8);
        Game old = new Game(files);
        quietly(() -> old.loadGameSave(2));
        assertTrue("a format-28 save loads", old.getLoadFailure() == null || old.getLoadFailure().isEmpty());
        same("...with an empty log", old.getDecisions().size(), 0);
        quietly(() -> old.setFundDial(1));
        assertTrue("...which records from then on", old.getDecisions().size() == 1);
    }

    /* ============================ 3. THE SPANS ARE THE YEAR BOOK'S ============================ */

    /** A history of `months` months with these series, each as long as the axis. */
    static HistorySave built(int months, String[] series, double[]... values) {
        StringBuilder json = new StringBuilder("{\"month\":[");
        for (int m = 1; m <= months; m++) json.append(m == 1 ? "" : ",").append(m);
        json.append("]");
        for (int s = 0; s < series.length; s++) {
            json.append(",\"").append(series[s]).append("\":[");
            for (int i = 0; i < values[s].length; i++) {
                json.append(i == 0 ? "" : ",").append(String.format(Locale.ROOT, "%.6f", values[s][i]));
            }
            json.append("]");
        }
        json.append("}");
        return new Gson().fromJson(json.toString(), HistorySave.class);
    }

    static double[] flat(int months, double v) {
        double[] out = new double[months];
        java.util.Arrays.fill(out, v);
        return out;
    }

    static void theSpansAreTheYearBooks() {
        out.println("\n--- 3. the bands are recessions(), named for the recession that holds them; the lane is episodes() ---");
        /*
         * Output flat at 100 a month, with a dip and a spike of 50 four times
         * over: the year against the year before falls under zero for three
         * months at each of four places, the first two four months apart and
         * the last two four months apart - under EPISODE_JOIN_MONTHS, so two
         * recessions of two bands each. The bank under water for months 45
         * to 60, inside the first, is a financial crisis that overlaps it.
         */
        int months = 120, j = 40;
        double[] gdp = flat(months, 100);
        gdp[j] -= 50; gdp[j + 3] += 50; gdp[j + 7] -= 50; gdp[j + 10] += 50;
        double[] equity = flat(months, 1000);
        for (int i = 44; i < 60; i++) equity[i] = -80;
        HistorySave h = built(months, new String[] {"gdp", "priceIndex", "bankEquity"}, gdp, flat(months, 1), equity);

        List<int[]> runs = YearBook.recessions(h);
        List<YearBook.Band> bands = ChartModel.bands(h);
        same("fixture: four recession runs", runs.size(), 4);
        boolean spans = bands.size() == runs.size();
        for (int i = 0; spans && i < runs.size(); i++) {
            spans = bands.get(i).fromMonth() == runs.get(i)[0] && bands.get(i).toMonth() == runs.get(i)[1];
        }
        assertTrue("the chart's bands are exactly recessions(), one for one, in order", spans);

        List<YearBook.Episode> episodes = YearBook.episodes(h);
        List<YearBook.Episode> lane = ChartModel.episodes(h);
        assertTrue("the episode lane is exactly episodes(), one for one, in order", lane.equals(episodes));
        int recessions = 0;
        for (YearBook.Episode e : episodes) if (e.kind().equals("recession")) recessions++;
        same("fixture: the four runs make two recessions, each joined across four months of relief", recessions, 2);

        boolean named = true, deep = true;
        double[] growth = YearBook.realGrowth(h);
        for (YearBook.Band b : bands) {
            YearBook.Episode holds = null;
            for (YearBook.Episode e : episodes) {
                if (e.kind().equals("recession") && e.fromMonth() <= b.fromMonth() && b.toMonth() <= e.toMonth()) holds = e;
            }
            named &= holds != null && holds.equals(b.episode()) && b.name().equals(holds.name());
            double min = Double.NaN;
            for (int m = b.fromMonth(); m <= b.toMonth(); m++) {
                double v = growth[m - 1];
                if (Double.isNaN(min) || v < min) min = v;
            }
            deep &= Double.compare(min, b.depth()) == 0 && Double.compare(growth[b.depthMonth() - 1], b.depth()) == 0;
        }
        assertTrue("every band carries the name of the recession that holds it - both bands of a joined one", named);
        same("...the first two \"Recession of " + CityCalendar.yearOf(j + 1) + "\"", bands.get(1).name(),
                "Recession of " + CityCalendar.yearOf(j + 1));
        assertTrue("...and its depth is the year book's real growth at its worst inside it, at its month", deep);

        boolean worst = true;
        for (YearBook.Episode e : episodes) {
            if (!e.kind().equals("recession")) continue;
            worst &= Double.compare(growth[e.worstMonth() - 1], e.worst()) == 0;
        }
        assertTrue("an episode's worst reading is the series at its worst month", worst);

        int[] rows = ChartModel.lanes(episodes);
        boolean apart = true;
        for (int a = 0; a < episodes.size(); a++) {
            for (int b = a + 1; b < episodes.size(); b++) {
                boolean overlap = episodes.get(a).fromMonth() <= episodes.get(b).toMonth()
                        && episodes.get(b).fromMonth() <= episodes.get(a).toMonth();
                if (overlap && rows[a] == rows[b]) apart = false;
            }
        }
        assertTrue("fixture: the bank's crisis overlaps the first recession",
                episodes.stream().anyMatch(e -> e.kind().equals("financial") && e.fromMonth() <= 50 && e.toMonth() >= 45));
        assertTrue("no two episodes that overlap share a row of the lane", apart);
        same("...so the lane has two rows", ChartModel.laneCount(rows), 2);

        boolean said = true;
        for (String kind : new String[] {"financial", "recession", "depression", "currency", "inflation",
                "deflation", "epidemic", "treasury", "slump"}) {
            said &= !YearBook.trigger(kind).isEmpty() && !YearBook.worstWords(kind, -.05).isEmpty();
        }
        assertTrue("every kind the table names says the rule that named it, and its worst in words", said);
        assertTrue("...from the table's own thresholds: the slump's says " + DecisionLog.pct(YearBook.SLUMP_UNEMPLOYMENT),
                YearBook.trigger("slump").contains(DecisionLog.pct(YearBook.SLUMP_UNEMPLOYMENT))
                        && YearBook.trigger("inflation").contains(DecisionLog.pct(YearBook.INFLATION_EPISODE))
                        && YearBook.trigger("depression").contains(String.valueOf(YearBook.DEPRESSION_MONTHS)));

        whatIsRunning();
    }

    /**
     * What City History's RUNNING NOW reads (0.7.37): YearBook.running(). Two
     * hundred months, the workforce off sick the whole way (an epidemic as
     * old as the city), the bank under water twice - once closed, once to
     * the end - the treasury overdrawn for the last twenty months and a
     * quarter of the labour force out of work for the last ten.
     */
    static void whatIsRunning() {
        int months = 200;
        double[] equity = flat(months, 1000), cash = flat(months, 1000), out = flat(months, 10);
        for (int m = 101; m <= 120; m++) equity[m - 1] = -80;
        for (int m = 171; m <= months; m++) equity[m - 1] = -80;
        for (int m = 181; m <= months; m++) cash[m - 1] = -5;
        for (int m = 191; m <= months; m++) out[m - 1] = 25;
        HistorySave h = built(months, new String[] {"sickRate", "bankEquity", "cash", "workforce", "outOfWork"},
                flat(months, .2), equity, cash, flat(months, 100), out);

        List<YearBook.Episode> all = YearBook.episodes(h);
        List<YearBook.Episode> running = YearBook.running(h);
        boolean closedOne = false;
        for (YearBook.Episode e : all) closedOne |= e.kind().equals("financial") && e.toMonth() == 120;
        assertTrue("fixture: a financial crisis closed at month 120, and four kinds held to the last month",
                closedOne && all.size() == 5);
        boolean open = running.size() == 4;
        for (YearBook.Episode e : running) open &= e.toMonth() == months;
        assertTrue("a running episode is one the history has not closed: the four that end at its last month", open);
        List<String> kinds = new ArrayList<>();
        for (YearBook.Episode e : running) kinds.add(e.kind());
        same("...a crisis before a watch, the newest start first, and the chronic one last", kinds,
                List.of("treasury", "financial", "slump", "epidemic"));
        assertTrue("...the epidemic, as old as the city, is chronic at CHRONIC_MONTHS; the ten-month slump is not",
                running.get(3).months() >= YearBook.CHRONIC_MONTHS && YearBook.isChronic(running.get(3))
                        && !YearBook.isChronic(running.get(2)));
        assertTrue("...and read from a list already in hand, the same four in the same order",
                YearBook.running(all, months).equals(running));
        same("the months a kind ran, all told: the two financial crises' twenty and thirty",
                YearBook.monthsIn(all, "financial"), 50);
        assertTrue("a crisis is the kinds the chart draws red: financial, currency, treasury, depression",
                YearBook.isSevere("financial") && YearBook.isSevere("currency") && YearBook.isSevere("treasury")
                        && YearBook.isSevere("depression") && !YearBook.isSevere("recession")
                        && !YearBook.isSevere("slump") && !YearBook.isSevere("epidemic"));
    }

    /* ============================ 4. THE TICKS ============================ */

    static void theTicks() {
        out.println("\n--- 4. years on every axis, months when they fit; nice steps on the value axis ---");
        boolean years = true, onJanuary = true, inside = true, apart = true, ordered = true;
        int windows = 0, withMonths = 0;
        double[] spans = {.5, 1, 3, 6, 11, 12, 13, 24, 36, 60, 119, 240, 600, 1200, 1791, 4000, 40000};
        double[] widths = {120, 180, 360, 730, 1400, 2400};
        for (double span : spans) {
            for (double width : widths) {
                for (double lo : new double[] {1, 2.5, 13, 1500.3, 1791 - span}) {
                    if (lo < 1) continue;
                    double hi = lo + span;
                    windows++;
                    List<ChartModel.Tick> ticks = ChartModel.timeTicks(lo, hi, width);
                    boolean anyYear = false, anyMonth = false;
                    for (int i = 0; i < ticks.size(); i++) {
                        ChartModel.Tick t = ticks.get(i);
                        anyYear |= t.label().matches(".*\\d{4}.*");
                        anyMonth |= !t.year();
                        if (t.label().matches("\\d{4}")) {
                            onJanuary &= CityCalendar.monthOfYear((int) t.month()) == 1
                                    && t.label().equals(String.valueOf(CityCalendar.yearOf((int) t.month())));
                        }
                        if (ticks.size() > 1) inside &= t.month() >= lo - 1e-9 && t.month() <= hi + 1e-9;
                        if (i > 0) {
                            double gap = (t.month() - ticks.get(i - 1).month()) / span * width;
                            ordered &= gap > 0;
                            apart &= gap >= Math.min(ChartModel.MONTH_LABEL_PX, ChartModel.YEAR_LABEL_PX) - 1e-6;
                        }
                    }
                    years &= anyYear;
                    if (anyMonth) withMonths++;
                }
            }
        }
        out.printf("      %d windows from half a month to 40,000 months, 120 to 2,400 pixels wide; %d with months%n",
                windows, withMonths);
        assertTrue("every window has a year in a label - none is left without one", years);
        assertTrue("a year's tick is on its January, and says its year", onJanuary);
        assertTrue("every tick is inside its window", inside);
        assertTrue("ticks run left to right, at least MONTH_LABEL_PX apart", ordered && apart);
        assertTrue("months are labelled in some windows - zoomed in - and not in all", withMonths > 0 && withMonths < windows);
        List<ChartModel.Tick> decade = ChartModel.timeTicks(1673, 1792, 730);
        boolean yearly = true;
        for (ChartModel.Tick t : decade) yearly &= t.year();
        assertTrue("ten years across the page: a tick a year, no months", yearly && decade.size() >= 9);
        List<ChartModel.Tick> year = ChartModel.timeTicks(1781, 1792, 730);
        assertTrue("one year across the page: its months labelled", year.stream().anyMatch(t -> !t.year()));

        boolean nice = true, holds = true, snapped = true, zero = true;
        java.util.Random r = new java.util.Random(23);
        for (int k = 0; k < 4000; k++) {
            double scale = Math.pow(10, r.nextInt(16) - 6);
            double a = (r.nextDouble() * 2 - .5) * scale, b = a + r.nextDouble() * scale * (r.nextInt(4) == 0 ? 0 : 1);
            boolean fromZero = r.nextBoolean();
            ChartModel.Scale s = ChartModel.niceScale(a, b, 3 + r.nextInt(7), fromZero);
            double p = Math.pow(10, Math.floor(Math.log10(s.step())));
            double m = s.step() / p;
            nice &= Math.abs(m - 1) < 1e-9 || Math.abs(m - 2) < 1e-9 || Math.abs(m - 5) < 1e-9 || Math.abs(m - 10) < 1e-9;
            holds &= s.low() <= a + Math.abs(a) * 1e-12 && s.high() >= b - Math.abs(b) * 1e-12 && s.high() > s.low();
            double lowSteps = s.low() / s.step(), highSteps = s.high() / s.step();
            snapped &= Math.abs(lowSteps - Math.round(lowSteps)) < 1e-6 && Math.abs(highSteps - Math.round(highSteps)) < 1e-6;
            if (fromZero && a >= 0) zero &= s.low() == 0;
        }
        assertTrue("4,000 scales: every step one, two or five times a power of ten", nice);
        assertTrue("...every scale holds what is drawn on it", holds);
        assertTrue("...its bottom and top on a step", snapped);
        assertTrue("...and a per cent with nothing negative starts at zero", zero);

        // The new city: the rate flat at 3% and the price level flat at 1.000, on two axes.
        ChartModel.Scale rate = ChartModel.niceScale(3, 3, 8, true);
        ChartModel.Scale index = ChartModel.niceScale(1, 1, 8, false);
        double rateAt = rate.y(3, 380, 380), indexAt = index.y(1, 380, 380);
        out.printf("      a flat 3%% rate on %s-%s draws %.0f px down; a flat 1.000 index on %s-%s, %.0f px%n",
                rate.low(), rate.high(), rateAt, index.low(), index.high(), indexAt);
        assertTrue("a new city's flat rate and flat price level, each on its own scale, no longer lie on each other",
                Math.abs(rateAt - indexAt) > 380 * .2);
        assertTrue("...the rate's from zero, the price level's around its 1.000",
                rate.low() == 0 && index.low() > 0 && index.low() < 1 && index.high() > 1);
    }

    /* ============================ 5. PAN AND ZOOM CLAMP ============================ */

    static void panAndZoomClamp() {
        out.println("\n--- 5. pan and zoom stop at the data; the month under the pointer stays; the window follows ---");
        ChartModel w = new ChartModel();
        w.setData(1, 1792);
        assertTrue("a new window opens on DEFAULT_RANGE, ending at the newest month",
                w.hi() == 1792 && w.monthsShown() == ChartModel.DEFAULT_RANGE);
        w.pan(-1e6);
        assertTrue("a drag past the start stops at the first month, the span kept",
                w.lo() == 1 && Math.abs(w.span() - (ChartModel.DEFAULT_RANGE - 1)) < 1e-9);
        w.pan(1e6);
        assertTrue("...and past the end at the last", w.hi() == 1792 && Math.abs(w.span() - (ChartModel.DEFAULT_RANGE - 1)) < 1e-9);
        w.zoom(1e-6, 1700);
        close("a wheel all the way in stops at MIN_SPAN", w.span(), ChartModel.MIN_SPAN, 1e-9);
        w.zoom(1e6, 1700);
        assertTrue("...all the way out at the whole history", w.lo() == 1 && w.hi() == 1792 && w.showsAll());
        w.showRange(120);
        double anchor = 1750.25, share = (anchor - w.lo()) / w.span();
        w.zoom(ChartModel.ZOOM_STEP, anchor);
        close("a notch in keeps the month under the pointer under it", (anchor - w.lo()) / w.span(), share, 1e-12);
        close("...and shows ZOOM_STEP of the months it did", w.span(), 119 * ChartModel.ZOOM_STEP, 1e-9);
        w.setWindow(-500, 30);
        assertTrue("a window set past the start is moved inside, its span kept", w.lo() == 1 && Math.abs(w.hi() - 531) < 1e-9);
        w.setWindow(10, 12);
        close("...and one narrower than MIN_SPAN is widened to it", w.span(), ChartModel.MIN_SPAN, 1e-9);

        w.showRange(60);
        w.setData(1, 1793);
        assertTrue("a window on the newest month follows the month that lands", w.hi() == 1793 && w.monthsShown() == 60);
        w.pan(-100);
        double was = w.lo();
        w.setData(1, 1794);
        assertTrue("...one dragged back stays where it was put", w.lo() == was);
        w.showRange(ChartModel.ALL);
        w.setData(1, 1795);
        assertTrue("...and one showing everything shows everything still", w.showsAll() && w.hi() == 1795);
        w.reset();
        assertTrue("the double-click goes back to the range last picked", w.showsAll());

        ChartModel young = new ChartModel();
        young.setData(1, 4);
        young.zoom(.01, 2);
        assertTrue("a history shorter than MIN_SPAN is shown whole, and a wheel cannot narrow it",
                young.lo() == 1 && young.hi() == 4);
        young.pan(3);
        assertTrue("...nor a drag move it", young.lo() == 1 && young.hi() == 4);
    }

    /* ============================ 6. THE FLAGS ============================ */

    static void theFlags() {
        out.println("\n--- 6. the flags: one a month with its count; flags too close drawn as one ---");
        int[] month = {100};
        DecisionLog log = new DecisionLog(() -> month[0]);
        log.record(DecisionLog.TAX, "Taxes to 17%");
        log.record(DecisionLog.CENTRAL_BANK, "Central bank rate to 0.00%");
        log.record(DecisionLog.BANK, "Bank rescued for its shares: $1.0B");
        month[0] = 101;
        log.record(DecisionLog.FUND, "Fund dial to 50% of the surplus");
        month[0] = 500;
        log.record(DecisionLog.CONSTRUCTION, "Rushed University");
        log.hold();
        log.record(DecisionLog.TAX, "Taxes to 99%");
        log.release();
        same("a held log records nothing", log.size(), 5);
        List<ChartModel.Flag> flags = ChartModel.flags(log);
        same("five decisions in three months are three flags", flags.size(), 3);
        same("...the first month's carries its three, in the order made", flags.get(0).count(), 3);
        same("...the first of them first", flags.get(0).entries().get(0).label(), "Taxes to 17%");
        List<ChartModel.Cluster> wide = ChartModel.clusters(flags, 90, 600, 20_000, 16);
        same("zoomed in, the flags a month apart are drawn apart", wide.size(), 3);
        List<ChartModel.Cluster> narrow = ChartModel.clusters(flags, 1, 2000, 400, 16);
        same("zoomed out, the two a month apart are drawn as one", narrow.size(), 2);
        same("...counting four decisions", narrow.get(0).count(), 4);
        same("a window that holds none draws none", ChartModel.clusters(flags, 200, 300, 400, 16).size(), 0);

        // The founding month (0.7.37): decided in month 1, before a history whose axis starts at month 2.
        DecisionLog founding = new DecisionLog(() -> month[0]);
        month[0] = 1;
        founding.record(DecisionLog.BORROWING, "Rollover on: each piece into its own kind");
        founding.record(DecisionLog.BANK, "A failed bank to be resolved at once");
        month[0] = 2;
        founding.record(DecisionLog.TAX, "Taxes to 17%");
        month[0] = 40;
        founding.record(DecisionLog.CURRENCY, "Bought reserves for $2.0M");
        int drawnBefore = 0;
        for (ChartModel.Cluster c : ChartModel.clusters(ChartModel.flags(founding), 2, 200, 20_000, 16)) drawnBefore += c.count();
        same("fixture: on an axis from month 2, the founding month's two decisions were on no lane", drawnBefore, 2);
        List<ChartModel.Flag> lane = ChartModel.onAxis(ChartModel.flags(founding), 2);
        assertTrue("a decision in the founding month is on the lane: at the axis's first month, with that month's own",
                lane.size() == 2 && lane.get(0).month() == 2 && lane.get(0).count() == 3 && lane.get(1).month() == 40);
        assertTrue("...each keeping its own month, the founding month's first",
                lane.get(0).entries().get(0).month() == 1 && lane.get(0).entries().get(1).month() == 1
                        && lane.get(0).entries().get(2).month() == 2);
        int drawn = 0;
        for (ChartModel.Cluster c : ChartModel.clusters(lane, 2, 200, 20_000, 16)) drawn += c.count();
        same("...so the whole history drawn shows every decision in the log", drawn, founding.size());
    }

    /* ============================ 7. A YOUNG CITY'S WINDOW ============================ */

    static void aYoungCitysWindow() {
        out.println("\n--- 7. a young city's window grows into its range, and stays on it ---");
        ChartModel w = new ChartModel();
        w.setData(1, 30);
        assertTrue("a city first opened at month 30 shows all of its thirty months, on its range (10Y lit)",
                w.lo() == 1 && w.hi() == 30 && w.onRange() && w.range() == ChartModel.DEFAULT_RANGE);
        boolean grows = true, lit = true;
        for (int m = 31; m <= 600; m++) {
            w.setData(1, m);
            double lo = Math.max(1, m - (ChartModel.DEFAULT_RANGE - 1));
            grows &= w.lo() == lo && w.hi() == m;
            lit &= w.onRange() && w.range() == ChartModel.DEFAULT_RANGE;
        }
        assertTrue("...and month by month to 600 it is all of it while younger than ten years, the last ten after",
                grows);
        assertTrue("...on its range the whole way, so 10Y stays lit", lit);
        same("...at month 600 the window opens at month 481", w.lo(), 481.0);

        ChartModel fifty = new ChartModel();
        fifty.setData(1, 40);
        fifty.showRange(600);
        fifty.setData(1, 700);
        assertTrue("50Y pressed at month 40 is the last fifty years at month 700",
                fifty.lo() == 101 && fifty.hi() == 700 && fifty.onRange() && fifty.range() == 600);

        ChartModel tile = new ChartModel();
        tile.showRange(120);
        tile.setData(1, 50);
        tile.setData(1, 200);
        assertTrue("a header tile's ten years asked before the page has drawn: the last ten years once there are",
                tile.lo() == 81 && tile.hi() == 200 && tile.onRange());

        ChartModel moved = new ChartModel();
        moved.setData(1, 200);
        moved.zoom(.2, 200);
        double span = moved.span();
        moved.setData(1, 260);
        assertTrue("a window the player zoomed on the newest month follows it at the width he left it",
                !moved.onRange() && moved.hi() == 260 && Math.abs(moved.span() - span) < 1e-9);
        moved.zoom(1e6, 230);
        moved.setData(1, 400);
        assertTrue("...and one he zoomed out to everything stays everything", moved.showsAll() && moved.hi() == 400);
        moved.reset();
        moved.setData(1, 401);
        assertTrue("a double-click puts it back on its range, which it then follows",
                moved.onRange() && moved.lo() == 282 && moved.hi() == 401);
    }
}
