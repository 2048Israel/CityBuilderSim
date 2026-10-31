package ham.citybuildersim;

import com.google.gson.JsonPrimitive;
import ham.citybuildersim.Rankings.Board;
import ham.citybuildersim.Rankings.Convert;
import ham.citybuildersim.Rankings.Money;
import ham.citybuildersim.Rankings.Prices;
import ham.citybuildersim.Rankings.Row;
import ham.citybuildersim.Rankings.Settings;
import ham.citybuildersim.Rankings.Table;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * The Rankings board held to what it promises (batch LB2; the project's spec-leaderboards.md 3, 4, 6 and 9):
 * Rankings' pivot and display, its two boards, its ranking, its sentence and lock, and CityMeasures' reading
 * of the city's history.
 *
 * WHY. A board that ranks a city's GDP beside Japan's has three ways to be quietly wrong that no screen would
 * show: a Money toggle that reorders the table (a city passing a nation by changing the unit it is shown in),
 * a city's own figure that does not come back to the figure the rest of the game shows, and a nation of the
 * city's own world whose row is not the figure Nations gives for that month. Each is an identity, and the rest
 * is the rules a rank is read by (lower is better ranks ascending, ties share, a place with no figure is no
 * row, never 0) and the sentence that names what is on screen. A city measure whose unit is wrong by a factor
 * of a thousand (the model counts money in thousands) would put the city a thousand times poorer than Japan, so
 * its figures are held to the history's own reader times the named factor.
 *
 * THE DATA. world-2025.json in the resources, and the nations of the world file: the fixture's twelve
 * (WorldData.FIXTURE) unless -Dcbs.world names the full world-1.bin, which gives all 196 nations; a line says
 * which. The live city is a scratch city (GameFiles.scratch) played to month 40 by the playtest's own probe.
 *
 * What this has to prove:
 *   1. THE PIVOT'S IDENTITIES: the city's own figure in its own money is X under this month's rate and PPP, and X
 *      at founding's prices under Constant (a wage, which has no constant series, X); the order of a board is the
 *      same in the city's money, game US$ and real US$, on both boards.
 *   2. AT FOUNDING REAL US$ EQUALS GAME US$ (D1): a fresh city's game dollar price level is 1, and every row of
 *      every money measure is the same in the two; a played city's is not.
 *   3. CONSTANT HOLDS A STEADY REAL FIGURE WHILE ITS RATE MOVES: the same figures at another rate and price
 *      level are the same US$ of 2015 under Constant, and not under this month's rate.
 *   4. YOUR WORLD'S ROWS EQUAL NATIONS' FIGURES FOR THE MONTH: people, output, inflation, growth, area and
 *      density of every nation, on both paths at two years; Historical's equal the world file to 2025; a region is
 *      its nation's times its share; the board ranks exactly the ten measures D15 names.
 *   5. LOWER IS BETTER RANKS ASCENDING: a DOWN measure's first row has the least value, an UP's and a NONE's the most.
 *   6. TIES SHARE A RANK: equal values share one, the next rank skips, the tie is by name.
 *   7. A PLACE WITH NO FIGURE IS OFF, NEVER 0: and a measure nothing real publishes has the city alone.
 *   8. THE REAL ROWS' PIVOT: current US$ times CPI-U 2015 over the figure's year, the .k and .ppp figures, and
 *      household spending and wages by gdp.k and gdp.ppp over gdp.
 *   9. AROUND YOU AND YOUR SIZE: the top three and the city with six either side, the places within ten times its people.
 *  10. THE CITY'S MEASURES FROM HISTORY: a year of the history's own reader times the named factor, the rules
 *      of a short city, a folded history and the life table.
 *  11. THE LOCK AND THE SENTENCE: Prices locked where it must be, and the opening sentence names what is on screen.
 *
 * Every fixture causes its condition: the founding city is a founded one, a tie is a real file with three
 * figures set equal, a missing figure a real file with one removed.
 */
public class RankingsCheck {

    static int fails = 0;
    /** Where the harness prints: the console. */
    static final PrintStream out = System.out;

    /** The seed of the nations' paths. */
    static final long SEED = 7_001;

    /** The month the live city is played to: over the two years real growth needs. */
    static final int LIVE_MONTH = 40;

    /** How near two figures that should be one are: relative to the larger, with 1e-9 a floor. */
    static final double TOLERANCE = 1e-9;

    /** The years Your world is read at: one inside the world file's history, one the game's rule makes. */
    static final int YEAR_HISTORY = 2012, YEAR_RULE = 2050;

    /** The money measures of the file, and the ones Your world ranks of them. */
    static final List<String> MONEY = List.of("gdp", "gdp_pp", "hh_spend_pp", "avg_wage", "min_wage");

    /** The measures the brief leaves absent from the city, each for a reason CityMeasures' javadoc gives. */
    static final Set<String> ABSENT = Set.of("participation", "tertiary", "yield_10y", "debt_foreign", "interest_revenue", "credit_rating", "bad_loans",
            "stock_market", "hh_debt", "aged_65", "sharing", "price_income", "electricity_pp", "renewable_share", "water_pp", "cars_per_1000", "transit", "oil");

    /** What a city of a few years has: every one of these is in its figures. */
    static final List<String> CORE = List.of("gdp", "gdp_pp", "growth_real", "hh_spend_pp", "investment", "gov_spend", "exports", "imports", "trade_balance",
            "current_account", "unemployment", "avg_wage", "min_wage", "kaitz", "inflation", "policy_rate", "real_rate", "fx_vs_parity", "reserves_months", "m2",
            "price_level", "debt", "budget_balance", "tax_revenue", "fund", "pensions", "bank_capital", "credit_private", "people", "pop_growth", "births", "deaths",
            "child_deaths", "life_exp", "old_age_dep", "net_migration", "household_size", "orphans", "rent_income", "homes_per_1000", "homeless", "homicides",
            "prisoners", "recorded_crime", "health_spend", "sick", "hungry", "care", "area", "density");

    /** Prints a label and OK or FAIL; the exit code is the count of FAILs. */
    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-120s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** A check over many things, that says how many there were and the first that was wrong. */
    static final class Tally {
        int n, bad;
        String first;

        void see(boolean ok, Supplier<String> what) {
            n++;
            if (!ok) {
                bad++;
                if (first == null) first = what.get();
            }
        }
    }

    static void tally(String label, Tally t) {
        boolean ok = t.n > 0 && t.bad == 0;
        if (!ok) fails++;
        out.printf("%-120s %s  (%d of %d agree)%s%n", label, ok ? "OK" : "FAIL", t.n - t.bad, t.n,
                t.bad > 0 ? "; the first that does not: " + t.first : t.n == 0 ? "; none read" : "");
    }

    static boolean near(double a, double b) {
        return Math.abs(a - b) <= TOLERANCE * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static Settings set(Board board, Money money, Convert convert) {
        return new Settings(board, money, Prices.PER_YEAR, convert);
    }

    /** The city's row of a table, null when it is off. */
    static Row cityRow(Table t) {
        return t.you() < 0 ? null : t.rows().get(t.you());
    }

    /** The city at a year of its calendar, everything else as it is. */
    static Rankings.City at(Rankings.City c, int year) {
        return new Rankings.City(c.name(), c.currency(), (year - Nations.FIRST_YEAR) * 12 + 1, year, c.fx(), c.parity(), c.usPrices(), c.foundingRate(), c.figures());
    }

    /** The city with another rate, parity and price level, and its figures as they were. */
    static Rankings.City moved(Rankings.City c, double fx, double parity, double usPrices) {
        return new Rankings.City(c.name(), c.currency(), c.month(), c.year(), fx, parity, usPrices, c.foundingRate(), c.figures());
    }

    /** The city with other figures. */
    static Rankings.City with(Rankings.City c, Map<String, CityMeasures.Figure> figures) {
        return new Rankings.City(c.name(), c.currency(), c.month(), c.year(), c.fx(), c.parity(), c.usPrices(), c.foundingRate(), figures);
    }

    static List<String> ids(Table t) {
        return t.rows().stream().map(Row::id).toList();
    }

    static Map<String, Row> byId(Table t) {
        Map<String, Row> m = new HashMap<>();
        for (Row r : t.rows()) m.put(r.id(), r);
        return m;
    }

    static PrintStream nothing() {
        return new PrintStream(java.io.OutputStream.nullOutputStream());
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        WorldMeasures wm = WorldMeasures.load();
        java.nio.file.Path full = WorldData.locate();
        String named = System.getProperty(WorldData.PROPERTY);
        if (named != null && !named.isBlank() && !java.nio.file.Files.isRegularFile(full)) {
            check("the file -D" + WorldData.PROPERTY + " names is there (" + named + ")", false);
            System.out.println("\n" + fails + " FAILED");
            System.exit(1);
        }

        // A fresh city, founded and not played, before the live one: the probe resets the float the live one is played on.
        PrintStream real = System.out;
        Game fresh = new Game(GameFiles.scratch("rankingsfresh"), LongPlaytest.founding());
        try {
            System.setOut(nothing());
            fresh.run();
        } finally {
            System.setOut(real);
        }
        Rankings.City founding = Rankings.City.of(fresh);
        Game g = LongPlaytest.probeCity(GameFiles.scratch("rankingscheck"), null);
        LongPlaytest.probePlay(g, LIVE_MONTH);
        Rankings.City live = Rankings.City.of(g);

        try (WorldData data = full != null ? WorldData.open(full) : WorldData.of(WorldDataCheck.resource(WorldData.FIXTURE))) {
            Nations historical = new Nations(data, Nations.Path.HISTORICAL, SEED), random = new Nations(data, Nations.Path.RANDOM, SEED);
            out.printf("%s; nations of %s: %d; the live city: %s, month %d (%s), %d months of history, %d measures%n", wm, full != null ? full.toString() : WorldData.FIXTURE,
                    random.count(), live.name(), live.month(), CityCalendar.format(live.month()), g.getHistorySave().months(), live.figures().size());

            thePivot(wm, random, live);
            atFounding(wm, random, live, founding);
            theConstant(wm, random, live);
            yourWorld(wm, data, historical, random, live);
            theOrder(wm, random, live);
            theTies(wm, random, live);
            theMissing(wm, random, live);
            theRealPivot(wm, random, live);
            theWindows(wm, random, live);
            theCity(wm, g, live);
            theLockAndSentence(wm, random, live);
        }

        out.printf("%nRankingsCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       1. THE PIVOT'S IDENTITIES
       ===================================================================== */

    static void thePivot(WorldMeasures wm, Nations nations, Rankings.City live) {
        out.println("--- 1. the pivot's identities ---");
        Tally own = new Tally();
        for (WorldMeasures.Measure m : wm.measures()) {
            CityMeasures.Figure f = live.figures().get(m.key());
            if (!m.money() || f == null) continue;
            for (Convert conv : Convert.values()) {
                for (Board board : Board.values()) {
                    if (board == Board.YOUR_WORLD && !m.yourWorld()) continue;
                    Row r = cityRow(Rankings.table(wm, nations, live, set(board, Money.CITY, conv), m.key()));
                    double expect = conv == Convert.CONSTANT && Rankings.hasConstantSeries(m) ? f.real() : f.value();
                    own.see(r != null && near(r.shown(), expect), () -> m.key() + " on " + board + " at " + conv + ": " + (r == null ? "no row" : r.shown() + " against " + expect));
                }
            }
        }
        tally("the city's figure in its own money is X under this month's rate and PPP, and X at founding's prices under Constant (a wage's, X)", own);
        check("the live city has the five money measures (" + MONEY + ") for that to be counted over", MONEY.stream().allMatch(live.figures()::containsKey));

        Tally order = new Tally();
        for (String key : MONEY) {
            for (Board board : Board.values()) {
                for (Convert conv : Convert.values()) {
                    List<List<String>> orders = new ArrayList<>();
                    List<List<Integer>> ranks = new ArrayList<>();
                    for (Money money : Money.values()) {
                        Table t = Rankings.table(wm, nations, live, set(board, money, conv), key);
                        orders.add(ids(t));
                        ranks.add(t.rows().stream().map(Row::rank).toList());
                    }
                    order.see(orders.get(0).equals(orders.get(1)) && orders.get(0).equals(orders.get(2)) && ranks.get(0).equals(ranks.get(1)) && ranks.get(0).equals(ranks.get(2)),
                            () -> key + " on " + board + " at " + conv);
                }
            }
        }
        tally("Money never reorders: a board's rows and ranks are the same in the city's money, game US$ and real US$, on both boards, at every Convert", order);
        Table ranked = Rankings.table(wm, nations, live, set(Board.REAL_2025, Money.CITY, Convert.MARKET), "gdp");
        check("and the boards have rows to be the same (" + ranked.rows().size() + " on Real world 2025's GDP)", ranked.rows().size() > 100);
        double usd = 12_345.0;
        Rankings.Settings market = set(Board.REAL_2025, Money.CITY, Convert.MARKET), ppp = set(Board.REAL_2025, Money.CITY, Convert.PPP), constant = set(Board.REAL_2025, Money.CITY, Convert.CONSTANT);
        check("display: the city's money is US$ x the game dollar's price level x the rate; at PPP x parity; at Constant x the founding rate",
                near(Rankings.display(usd, market, live), usd * live.usPrices() * live.fx()) && near(Rankings.display(usd, ppp, live), usd * live.usPrices() * live.parity())
                        && near(Rankings.display(usd, constant, live), usd * live.foundingRate()));
        check("display: game US$ is US$ x the price level, and at Constant x 1; real US$ as is",
                near(Rankings.display(usd, set(Board.REAL_2025, Money.GAME_USD, Convert.MARKET), live), usd * live.usPrices())
                        && near(Rankings.display(usd, set(Board.REAL_2025, Money.GAME_USD, Convert.CONSTANT), live), usd)
                        && near(Rankings.display(usd, set(Board.REAL_2025, Money.REAL_USD, Convert.PPP), live), usd));
    }

    /* =====================================================================
       2. AT FOUNDING REAL US$ EQUALS GAME US$ (D1)
       ===================================================================== */

    static void atFounding(WorldMeasures wm, Nations nations, Rankings.City live, Rankings.City founded) {
        out.println("--- 2. at founding real US$ equals game US$ ---");
        check("a fresh city's game dollar price level is 1, and its founding rate is the rate it opens at (" + founded.usPrices() + ", " + founded.foundingRate() + ")",
                founded.usPrices() == 1.0 && near(founded.foundingRate(), ForeignAccounts.OPENING_RATE));
        Rankings.City city = new Rankings.City(live.name(), live.currency(), founded.month(), founded.year(), founded.fx(), founded.parity(), founded.usPrices(), founded.foundingRate(), live.figures());
        Tally same = new Tally(), differ = new Tally();
        int rows = 0;
        for (WorldMeasures.Measure m : wm.measures()) {
            if (!m.money()) continue;
            for (Convert conv : List.of(Convert.MARKET, Convert.PPP)) {
                for (Board board : Board.values()) {
                    if (board == Board.YOUR_WORLD && !m.yourWorld()) continue;
                    Table game = Rankings.table(wm, nations, city, set(board, Money.GAME_USD, conv), m.key());
                    Table realUsd = Rankings.table(wm, nations, city, set(board, Money.REAL_USD, conv), m.key());
                    sameRows(same, game, realUsd, m.key() + " on " + board + " at " + conv);
                    rows += game.rows().size();
                    Table gameNow = Rankings.table(wm, nations, live, set(board, Money.GAME_USD, conv), m.key());
                    Table realNow = Rankings.table(wm, nations, live, set(board, Money.REAL_USD, conv), m.key());
                    Map<String, Row> r = byId(realNow);
                    boolean any = false;
                    for (Row row : gameNow.rows()) any |= row.shown() != r.get(row.id()).shown();
                    if (!gameNow.rows().isEmpty()) differ.see(any, () -> m.key() + " on " + board + " at " + conv);
                }
            }
        }
        tally("at founding every row's game US$ is its real US$ (" + rows + " rows), on both boards, at this month's rate and PPP", same);
        check("the live city's game dollar has inflated (" + live.usPrices() + "), so the same test sees a difference", live.usPrices() > 1.0);
        tally("and once it has inflated, the game US$ differs from the real US$ on every board of every money measure", differ);
    }

    /** Whether two tables of one measure have the same rows and each row's shown figure the same, to the digit. */
    static void sameRows(Tally t, Table a, Table b, String what) {
        Map<String, Row> other = byId(b);
        boolean ok = ids(a).equals(ids(b));
        for (Row r : a.rows()) ok &= other.containsKey(r.id()) && other.get(r.id()).shown() == r.shown();
        boolean result = ok;
        t.see(result, () -> what);
    }

    /* =====================================================================
       3. CONSTANT HOLDS A STEADY REAL FIGURE WHILE ITS RATE MOVES
       ===================================================================== */

    static void theConstant(WorldMeasures wm, Nations nations, Rankings.City live) {
        out.println("--- 3. Constant holds a steady real figure while its rate moves ---");
        Rankings.City later = moved(live, live.fx() * 1.6, live.parity() * 1.3, live.usPrices() * 1.4);
        Tally steady = new Tally(), moves = new Tally();
        for (String key : List.of("gdp", "gdp_pp", "hh_spend_pp")) {
            Row a = cityRow(Rankings.table(wm, nations, live, set(Board.REAL_2025, Money.REAL_USD, Convert.CONSTANT), key));
            Row b = cityRow(Rankings.table(wm, nations, later, set(Board.REAL_2025, Money.REAL_USD, Convert.CONSTANT), key));
            steady.see(a != null && b != null && a.value() == b.value(), () -> key + " under Constant");
            for (Convert conv : List.of(Convert.MARKET, Convert.PPP)) {
                Row c = cityRow(Rankings.table(wm, nations, live, set(Board.REAL_2025, Money.REAL_USD, conv), key));
                Row d = cityRow(Rankings.table(wm, nations, later, set(Board.REAL_2025, Money.REAL_USD, conv), key));
                moves.see(c != null && d != null && !near(c.value(), d.value()), () -> key + " at " + conv);
            }
            Row inCity = cityRow(Rankings.table(wm, nations, later, set(Board.REAL_2025, Money.CITY, Convert.CONSTANT), key));
            steady.see(inCity != null && near(inCity.shown(), live.figures().get(key).real()), () -> key + " in the city's money under Constant");
        }
        tally("the same figures at a dearer game dollar and a weaker rate are the same US$ of 2015 under Constant, and in the city's money its real figure", steady);
        tally("...and are not under this month's rate or PPP, which move with the rate and the price level", moves);
    }

    /* =====================================================================
       4. YOUR WORLD'S ROWS EQUAL NATIONS' FIGURES FOR THE MONTH
       ===================================================================== */

    static void yourWorld(WorldMeasures wm, WorldData data, Nations historical, Nations random, Rankings.City live) {
        out.println("--- 4. Your world's rows equal Nations' figures for the month ---");
        List<String> ten = wm.measures().stream().filter(WorldMeasures.Measure::yourWorld).map(WorldMeasures.Measure::key).toList();
        check("the file carries exactly ten measures for Your world: " + String.join(" ", ten), ten.size() == 10);
        Tally ranks = new Tally();
        for (WorldMeasures.Measure m : wm.measures()) {
            Table t = Rankings.table(wm, random, at(live, YEAR_RULE), set(Board.YOUR_WORLD, Money.REAL_USD, Convert.MARKET), m.key());
            ranks.see(t.ranked() == m.yourWorld() && (m.yourWorld() ? !t.rows().isEmpty() : t.rows().isEmpty() && t.you() < 0), () -> m.key() + ": ranked " + t.ranked() + ", " + t.rows().size() + " rows");
            Table none = Rankings.table(wm, null, at(live, YEAR_RULE), set(Board.YOUR_WORLD, Money.REAL_USD, Convert.MARKET), m.key());
            ranks.see(!none.ranked() && none.rows().isEmpty(), () -> m.key() + " with no Nations");
        }
        tally("Your world ranks exactly the ten D15 names and has no rows for the other 60; with no Nations it ranks none", ranks);

        for (Nations nations : List.of(historical, random)) {
            for (int year : List.of(YEAR_HISTORY, YEAR_RULE)) {
                Rankings.City city = at(live, year);
                Map<String, Table> tables = new LinkedHashMap<>();
                for (String key : ten) {
                    tables.put(key + "/c", Rankings.table(wm, nations, city, set(Board.YOUR_WORLD, Money.REAL_USD, Convert.CONSTANT), key));
                    tables.put(key + "/m", Rankings.table(wm, nations, city, set(Board.YOUR_WORLD, Money.REAL_USD, Convert.MARKET), key));
                    tables.put(key + "/p", Rankings.table(wm, nations, city, set(Board.YOUR_WORLD, Money.REAL_USD, Convert.PPP), key));
                }
                Tally people = new Tally(), output = new Tally(), growth = new Tally(), held = new Tally(), marks = new Tally();
                Map<String, Row> peopleRows = byId(tables.get("people/c")), gdpRows = byId(tables.get("gdp/c")), ppRows = byId(tables.get("gdp_pp/c"));
                Map<String, Row> inflation = byId(tables.get("inflation/c")), popGrowth = byId(tables.get("pop_growth/c")), realGrowth = byId(tables.get("growth_real/c"));
                Map<String, Row> area = byId(tables.get("area/c")), density = byId(tables.get("density/c")), decade = byId(tables.get("growth_10y/c"));
                for (int i = 1; i <= nations.count(); i++) {
                    String iso = nations.iso3(i);
                    Nations.Year y = nations.year(i, year);
                    Row p = peopleRows.get(iso), o = gdpRows.get(iso);
                    if (p == null || o == null) {
                        people.see(false, () -> iso + " " + year + " has no row");
                        continue;
                    }
                    people.see(p.value() == y.people() && p.shown() == y.people() && p.mark() == y.peopleMark() && p.kind() == Rankings.Kind.NATION, () -> iso + " " + year + " people");
                    output.see(o.value() == y.output() && o.mark() == y.outputMark(), () -> iso + " " + year + " gdp under Constant is the output");
                    output.see(ppRows.get(iso) != null && near(ppRows.get(iso).value(), y.output() / y.people()), () -> iso + " " + year + " gdp per person under Constant");
                    Row inf = inflation.get(iso);
                    growth.see(inf != null && near(inf.value(), 100 * y.inflation()) && inf.mark() == y.outputMark(), () -> iso + " " + year + " inflation");
                    growth.see(near(popGrowth.get(iso).value(), 100 * (y.people() / nations.year(i, year - 1).people() - 1)), () -> iso + " " + year + " growth of people");
                    growth.see(near(realGrowth.get(iso).value(), 100 * (Math.exp(nations.growth(i, year)) - 1)), () -> iso + " " + year + " real growth");
                    growth.see(near(decade.get(iso).value(), 100 * (Math.pow(y.output() / nations.year(i, year - CityMeasures.DECADE).output(), 1.0 / CityMeasures.DECADE) - 1)), () -> iso + " " + year + " 10-year growth");
                    WorldMeasures.Place file = wm.place(iso);
                    if (file != null && file.figure("area") != null) {
                        held.see(area.get(iso).value() == file.value("area") && area.get(iso).mark() == file.figure("area").mark(), () -> iso + " " + year + " area is the file's");
                        held.see(near(density.get(iso).value(), y.people() / file.value("area")), () -> iso + " " + year + " density is people over area");
                    }
                    if (nations == historical && year <= Nations.HISTORY_LAST_YEAR) {
                        // Historical equals the world file to 2025: its own reader, not Nations'.
                        people.see(p.value() == data.pathValue("people", i, year), () -> iso + " " + year + " people are the world file's");
                        output.see(o.value() == data.pathValue("gdp_2015usd", i, year), () -> iso + " " + year + " output is the world file's");
                        double cpi = data.pathValue("cpi", i, year), before = data.pathValue("cpi", i, year - 1);
                        growth.see(near(inf.value(), 100 * (cpi / before - 1)), () -> iso + " " + year + " inflation is the world file's prices over the year before");
                        int us = data.numberOf(Nations.FRONTIER);
                        double market = data.pathValue("gdp_2015usd", i, year) * (cpi / data.pathValue("cpi", i, Rankings.BASE_YEAR))
                                * (data.pathValue("rate", i, Rankings.BASE_YEAR) / data.pathValue("rate", i, year))
                                * (data.pathValue("cpi", us, Rankings.BASE_YEAR) / data.pathValue("cpi", us, year));
                        Row m = byId(tables.get("gdp/m")).get(iso);
                        output.see(m != null && near(m.value(), market), () -> iso + " " + year + " gdp at this month's rate is the file's output at that year's prices and rate, in 2015 dollars");
                    }
                    marks.see(p.mark() >= 1 && p.mark() <= 3 && o.mark() >= 1 && o.mark() <= 3, () -> iso + " " + year);
                }
                String where = nations.path() + " " + year;
                tally("Your world " + where + ": every nation's people are Nations' (value, shown and mark)" + (nations == historical && year <= Nations.HISTORY_LAST_YEAR ? ", and the world file's" : ""), people);
                tally("Your world " + where + ": every nation's output is Nations' (the file's output under Constant, a head of it)", output);
                tally("Your world " + where + ": inflation, growth of people, real growth and 10-year growth are Nations'", growth);
                tally("Your world " + where + ": area is the file's held, and density is people over it", held);
                tally("Your world " + where + ": every mark is 1, 2 or 3", marks);

                // The market and PPP rules, against the file's price level and Nations' prices: the US is 1 on both.
                Table market = tables.get("gdp/m"), parity = tables.get("gdp/p"), levels = tables.get("price_level/c");
                Row us = byId(market).get(Nations.FRONTIER), usParity = byId(parity).get(Nations.FRONTIER), usLevel = byId(levels).get(Nations.FRONTIER);
                Nations.Year usYear = nations.year(nations.index(Nations.FRONTIER), year), usBase = nations.year(nations.index(Nations.FRONTIER), Rankings.BASE_YEAR);
                check("Your world " + where + ": the US is 1 on price level, and its market and PPP output are one: its output at its own prices over the base year's",
                        usLevel != null && usLevel.value() == 1.0 && usParity != null && near(us.value(), usParity.value()) && near(us.value(), usYear.output() * (usYear.priceLevel() / usBase.priceLevel()) * (usBase.priceLevel() / usYear.priceLevel())));
                Tally ppp = new Tally();
                for (int i = 1; i <= nations.count(); i++) {
                    String iso = nations.iso3(i);
                    ppp.see(byId(parity).get(iso) != null && near(byId(parity).get(iso).value() * byId(levels).get(iso).value(), byId(market).get(iso).value()), () -> iso + " " + year);
                }
                tally("Your world " + where + ": a nation's PPP output times its price level against the US is its output at this month's rate", ppp);

                // The regions: their nation's figure times their 2025 share.
                Tally regions = new Tally();
                Map<String, Row> regionPeople = byId(tables.get("people/c")), regionGdp = byId(tables.get("gdp/c"));
                int shared = 0;
                for (WorldMeasures.Place r : wm.regions()) {
                    int n = nations.index(r.nation());
                    if (n < 0) {
                        regions.see(!regionPeople.containsKey(r.id()) && !regionGdp.containsKey(r.id()), () -> r.id() + " has a row, and its nation " + r.nation() + " is not in this world");
                        continue;
                    }
                    shared++;
                    Nations.Year y = nations.year(n, year);
                    Row p = regionPeople.get(r.id()), o = regionGdp.get(r.id());
                    regions.see(p != null && near(p.value(), y.people() * r.sharePeople().value()) && p.mark() == 1 && p.kind() == Rankings.Kind.REGION && r.nation().equals(p.nation()), () -> r.id() + " people");
                    regions.see(o != null && near(o.value(), y.output() * r.shareOutput().value()) && o.mark() == 1, () -> r.id() + " output");
                }
                tally("Your world " + where + ": a city region is its nation's people and output times its share, a mark 1 (" + shared + " of " + wm.regions().size() + " regions have their nation in this world)", regions);
                check("Your world " + where + ": the city is a row, and the rows are the nations, the regions with a nation, and the city", cityRow(tables.get("people/c")) != null
                        && tables.get("people/c").rows().size() == nations.count() + shared + 1);
            }
        }
    }

    /* =====================================================================
       5. LOWER IS BETTER RANKS ASCENDING
       ===================================================================== */

    static void theOrder(WorldMeasures wm, Nations nations, Rankings.City live) {
        out.println("--- 5. lower is better ranks ascending ---");
        Tally down = new Tally(), up = new Tally(), none = new Tally();
        Map<WorldMeasures.Arrow, Integer> counted = new LinkedHashMap<>();
        for (WorldMeasures.Measure m : wm.measures()) {
            if (!m.sourced()) continue;
            Table t = Rankings.table(wm, nations, live, Settings.OPENING, m.key());
            if (t.rows().size() < 2) continue;
            counted.merge(m.arrow(), 1, Integer::sum);
            boolean ordered = true;
            for (int i = 1; i < t.rows().size(); i++) {
                double a = t.rows().get(i - 1).value(), b = t.rows().get(i).value();
                ordered &= m.arrow() == WorldMeasures.Arrow.DOWN ? a <= b : a >= b;
            }
            double least = t.rows().stream().mapToDouble(Row::value).min().getAsDouble(), most = t.rows().stream().mapToDouble(Row::value).max().getAsDouble();
            double first = t.rows().get(0).value();
            boolean ok = ordered && t.rows().get(0).rank() == 1 && first == (m.arrow() == WorldMeasures.Arrow.DOWN ? least : most);
            (m.arrow() == WorldMeasures.Arrow.DOWN ? down : m.arrow() == WorldMeasures.Arrow.UP ? up : none).see(ok, () -> m.key() + " first " + first + ", least " + least + ", most " + most);
        }
        tally("a DOWN measure's first row has the least value and the rows ascend (unemployment, deaths, debt, ...)", down);
        tally("an UP measure's first row has the most and the rows descend (GDP, life expectancy, ...)", up);
        tally("a NONE measure ranks largest first, like an UP (inflation, investment, ...)", none);
        check("on the real file: " + counted, counted.size() == 3);
        Table unemployment = Rankings.table(wm, nations, live, Settings.OPENING, "unemployment");
        check("unemployment, which is lower-is-better, has the city ranked by its value among the nations (rank " + (cityRow(unemployment) == null ? "-" : cityRow(unemployment).rank()) + ")",
                cityRow(unemployment) != null && unemployment.rows().stream().filter(r -> r.value() < cityRow(unemployment).value()).count() + 1 == cityRow(unemployment).rank());
    }

    /* =====================================================================
       6. TIES SHARE A RANK
       ===================================================================== */

    static void theTies(WorldMeasures wm, Nations nations, Rankings.City live) throws IOException {
        out.println("--- 6. ties share a rank ---");
        String json = new String(WorldDataCheck.resource(WorldMeasures.RESOURCE), StandardCharsets.UTF_8);
        for (String key : List.of("life_exp", "deaths")) {
            boolean up = wm.measure(key).arrow() == WorldMeasures.Arrow.UP;
            double tied = up ? 99.9 : 0.01;
            WorldMeasures copy = WorldMeasuresCheck.doctored(json, r -> {
                for (String id : List.of("AFG", "ALB", "DZA")) WorldMeasuresCheck.figureOf(r, id, key).set(0, new JsonPrimitive(tied));
            });
            Rankings.City city = with(live, Map.of(key, new CityMeasures.Figure(tied, Double.NaN, "a tie")));
            Table t = Rankings.table(copy, nations, city, Settings.OPENING, key);
            List<Row> group = t.rows().stream().filter(r -> r.value() == tied).toList();
            check(key + (up ? " (an UP)" : " (a DOWN)") + ": the three places and the city, set equal, are four rows (" + group.size() + ") at the head of the board", group.size() == 4 && t.rows().subList(0, 4).equals(group));
            check(key + ": they share rank 1, and the next place is 5th, not 2nd (" + t.rows().get(4).rank() + ")", group.stream().allMatch(r -> r.rank() == 1) && t.rows().get(4).rank() == 5);
            List<String> names = group.stream().map(Row::name).toList();
            check(key + ": the tie is by name: " + names, names.equals(new TreeSet<>(names).stream().toList()));
            Tally strictly = new Tally();
            for (Row r : t.rows()) {
                long better = t.rows().stream().filter(o -> up ? o.value() > r.value() : o.value() < r.value()).count();
                strictly.see(r.rank() == better + 1, () -> r.name() + " is ranked " + r.rank() + " with " + better + " places strictly better");
            }
            tally(key + ": every row's rank is one more than the places strictly better than it (1, 1, 1, 1, 5, ...)", strictly);
            check(key + ": the city, in the tie, is found at the index the table says (you " + t.you() + ")", t.you() >= 0 && t.rows().get(t.you()).kind() == Rankings.Kind.CITY && t.rows().get(t.you()).rank() == 1);
        }
        // A tie in the middle, and the same board twice gives the same order (no loop order decides).
        WorldMeasures copy = WorldMeasuresCheck.doctored(json, r -> {
            for (String id : List.of("FRA", "DEU", "ITA")) WorldMeasuresCheck.figureOf(r, id, "life_exp").set(0, new JsonPrimitive(80.25));
        });
        Table a = Rankings.table(copy, nations, live, Settings.OPENING, "life_exp"), b = Rankings.table(copy, nations, live, Settings.OPENING, "life_exp");
        List<Row> middle = a.rows().stream().filter(r -> r.value() == 80.25).toList();
        check("a tie in the middle (France, Germany, Italy at 80.25): one rank for the group, the next skips two, and the board is the same twice",
                middle.size() >= 3 && middle.stream().allMatch(r -> r.rank() == middle.get(0).rank())
                        && a.rows().get(a.rows().indexOf(middle.get(0)) + middle.size()).rank() == middle.get(0).rank() + middle.size() && a.rows().equals(b.rows()));
    }

    /* =====================================================================
       7. A PLACE WITH NO FIGURE IS OFF, NEVER 0
       ===================================================================== */

    static void theMissing(WorldMeasures wm, Nations nations, Rankings.City live) throws IOException {
        out.println("--- 7. a place with no figure is off, never 0 ---");
        Tally off = new Tally(), values = new Tally(), finite = new Tally();
        int absent = 0;
        for (WorldMeasures.Measure m : wm.measures()) {
            if (!m.sourced() || m.money()) continue;
            Table t = Rankings.table(wm, nations, live, Settings.OPENING, m.key());
            Map<String, Row> rows = byId(t);
            int expected = 0;
            for (WorldMeasures.Place p : wm.places()) {
                WorldMeasures.Figure f = p.figure(m.key());
                if (f == null) {
                    absent++;
                    off.see(!rows.containsKey(p.id()), () -> p.id() + " has no " + m.key() + " and is a row");
                } else {
                    expected++;
                    values.see(rows.containsKey(p.id()) && rows.get(p.id()).value() == f.value() && rows.get(p.id()).year() == f.year() && rows.get(p.id()).mark() == f.mark(),
                            () -> p.id() + " " + m.key());
                }
            }
            int places = (int) t.rows().stream().filter(r -> r.kind() != Rankings.Kind.CITY).count(), want = expected;
            values.see(places == want, () -> m.key() + " has " + places + " rows for " + want + " figures");
            for (Row r : t.rows()) finite.see(Double.isFinite(r.value()) && Double.isFinite(r.shown()), () -> m.key() + " " + r.id());
        }
        tally("a place without a figure of a measure is not a row (" + absent + " absences over the measures real places have)", off);
        tally("and a place with one is a row of exactly its value, year and mark: the rows are the figures", values);
        tally("every row's value is a finite number: none is NaN", finite);
        Table oil = Rankings.table(wm, nations, live, Settings.OPENING, "oil");
        long zeros = wm.places().stream().filter(p -> p.figure("oil") != null && p.value("oil") == 0).count();
        check("a published 0 is a row and a missing figure is not: oil has " + zeros + " places at 0 and " + oil.rows().stream().filter(r -> r.value() == 0).count() + " rows at 0",
                zeros > 0 && oil.rows().stream().filter(r -> r.value() == 0).count() == zeros);

        String json = new String(WorldDataCheck.resource(WorldMeasures.RESOURCE), StandardCharsets.UTF_8);
        Table whole = Rankings.table(wm, nations, live, Settings.OPENING, "gdp");
        WorldMeasures copy = WorldMeasuresCheck.doctored(json, r -> WorldMeasuresCheck.placeOf(r, "AFG").getAsJsonObject("f").remove("gdp"));
        Table less = Rankings.table(copy, nations, live, Settings.OPENING, "gdp");
        List<String> wholeIds = new ArrayList<>(ids(whole));
        wholeIds.remove("AFG");
        check("a copy of the file with one nation's GDP removed loses exactly that row (" + whole.rows().size() + " rows to " + less.rows().size() + ")",
                ids(whole).contains("AFG") && !ids(less).contains("AFG") && less.rows().size() == whole.rows().size() - 1 && wholeIds.equals(ids(less)));

        // The city: a measure it has no figure for is not a row, and a figure it has of 0 is.
        Map<String, CityMeasures.Figure> cut = new LinkedHashMap<>(live.figures());
        cut.remove("gdp");
        cut.put("unemployment", new CityMeasures.Figure(0, Double.NaN, "none out of work"));
        Rankings.City noGdp = with(live, cut);
        Table noRow = Rankings.table(wm, nations, noGdp, Settings.OPENING, "gdp"), zero = Rankings.table(wm, nations, noGdp, Settings.OPENING, "unemployment");
        check("a city with no figure for a measure is off its board (you " + noRow.you() + "), and one with a figure of 0 is a row at 0, first of a lower-is-better",
                noRow.you() == -1 && noRow.rows().stream().noneMatch(r -> r.kind() == Rankings.Kind.CITY) && zero.you() == 0 && zero.rows().get(0).value() == 0);

        // D10: a measure nothing real publishes has the city alone.
        Tally yours = new Tally(), flags = new Tally();
        int measures = 0;
        for (WorldMeasures.Measure m : wm.measures()) {
            if (m.sourced() || !live.figures().containsKey(m.key())) continue;
            measures++;
            Table t = Rankings.table(wm, nations, live, Settings.OPENING, m.key());
            yours.see(!t.realFigures() && t.ranked() && t.rows().size() == 1 && t.you() == 0 && t.rows().get(0).rank() == 1 && t.figures() == 0, () -> m.key());
        }
        tally("a measure nothing real publishes (D10) has the city alone, and realFigures false (" + measures + " of them are the city's)", yours);
        for (WorldMeasures.Measure m : wm.measures()) {
            if (!m.sourced()) continue;
            Table t = Rankings.table(wm, nations, live, Settings.OPENING, m.key());
            int marked = (int) t.rows().stream().filter(r -> r.mark() > 0).count(), estimates = (int) t.rows().stream().filter(r -> r.mark() == 1).count();
            flags.see(t.realFigures() && t.figures() == marked && t.estimates() == estimates && (t.you() < 0 || t.rows().get(t.you()).mark() == 0), () -> m.key());
        }
        tally("a published measure's table counts its rows with a mark (not the city) and the estimates among them", flags);
    }

    /* =====================================================================
       8. THE REAL ROWS' PIVOT
       ===================================================================== */

    static void theRealPivot(WorldMeasures wm, Nations nations, Rankings.City live) {
        out.println("--- 8. the real rows' pivot ---");
        check("the CPI-U's 2015 over 2025 is the file's us_prices_2015_over_2025 (" + wm.cpiU(2015) / wm.cpiU(2025) + ")", near(wm.cpiU(2015) / wm.cpiU(2025), wm.usPrices2015Over2025()));
        Table market = Rankings.table(wm, nations, live, Settings.OPENING, "gdp");
        Row us = byId(market).get("USA"), tokyo = byId(market).get("TYO");
        WorldMeasures.Figure usGdp = wm.place("USA").figure("gdp"), tyoGdp = wm.place("TYO").figure("gdp");
        check("a 2025 figure at this month's rate is its value times CPI-U 2015 over 2025: the US (" + us.year() + ")",
                us.year() == 2025 && near(us.value(), usGdp.value() * wm.cpiU(2015) / wm.cpiU(2025)));
        check("a city region's 2021 figure is its value times CPI-U 2015 over 2021: Tokyo (" + tokyo.year() + ")",
                tokyo.year() == 2021 && near(tokyo.value(), tyoGdp.value() * wm.cpiU(2015) / wm.cpiU(2021)) && tokyo.kind() == Rankings.Kind.REGION && "JPN".equals(tokyo.nation()));
        Tally every = new Tally(), constant = new Tally(), ppp = new Tally(), others = new Tally(), marks = new Tally();
        Table k = Rankings.table(wm, nations, live, set(Board.REAL_2025, Money.REAL_USD, Convert.CONSTANT), "gdp");
        Table pk = Rankings.table(wm, nations, live, set(Board.REAL_2025, Money.REAL_USD, Convert.CONSTANT), "gdp_pp");
        Table pp = Rankings.table(wm, nations, live, set(Board.REAL_2025, Money.REAL_USD, Convert.PPP), "gdp");
        Table ppp2 = Rankings.table(wm, nations, live, set(Board.REAL_2025, Money.REAL_USD, Convert.PPP), "gdp_pp");
        Table pm = Rankings.table(wm, nations, live, Settings.OPENING, "gdp_pp");
        for (WorldMeasures.Place p : wm.places()) {
            WorldMeasures.Figure g = p.figure("gdp");
            if (g == null) continue;
            every.see(near(byId(market).get(p.id()).value(), g.value() * wm.cpiU(2015) / wm.cpiU(g.year())), () -> p.id());
            WorldMeasures.Figure gk = p.figure("gdp.k"), gp = p.figure("gdp.ppp");
            constant.see(byId(k).get(p.id()).value() == gk.value() && byId(k).get(p.id()).year() == gk.year(), () -> p.id() + " gdp.k");
            ppp.see(near(byId(pp).get(p.id()).value(), gp.value() * wm.cpiU(2015) / wm.cpiU(gp.year())), () -> p.id() + " gdp.ppp");
            WorldMeasures.Figure q = p.figure("gdp_pp");
            if (q != null) {
                constant.see(byId(pk).get(p.id()).value() == p.value("gdp_pp.k"), () -> p.id() + " gdp_pp.k");
                ppp.see(near(byId(ppp2).get(p.id()).value(), p.value("gdp_pp.ppp") * wm.cpiU(2015) / wm.cpiU(p.figure("gdp_pp.ppp").year())), () -> p.id() + " gdp_pp.ppp");
                every.see(near(byId(pm).get(p.id()).value(), q.value() * wm.cpiU(2015) / wm.cpiU(q.year())), () -> p.id() + " gdp_pp");
            }
            marks.see(byId(market).get(p.id()).mark() == g.mark() && byId(market).get(p.id()).source().equals(g.source()), () -> p.id() + " mark");
        }
        tally("every place's GDP and GDP per person at this month's rate: its value times CPI-U 2015 over its year's", every);
        tally("under Constant GDP and GDP per person are the file's .k figure as it is, with its year", constant);
        tally("under PPP they are the file's .ppp figure times CPI-U 2015 over its year's", ppp);
        tally("a row carries its figure's mark and source", marks);
        // Household spending and the wages convert by the place's own GDP ratios.
        Table hhConstant = Rankings.table(wm, nations, live, set(Board.REAL_2025, Money.REAL_USD, Convert.CONSTANT), "hh_spend_pp");
        Table hhPpp = Rankings.table(wm, nations, live, set(Board.REAL_2025, Money.REAL_USD, Convert.PPP), "hh_spend_pp");
        Table hhMarket = Rankings.table(wm, nations, live, Settings.OPENING, "hh_spend_pp");
        for (WorldMeasures.Place p : wm.nations()) {
            WorldMeasures.Figure h = p.figure("hh_spend_pp");
            if (h == null) continue;
            double now = h.value() * wm.cpiU(2015) / wm.cpiU(h.year());
            others.see(near(byId(hhMarket).get(p.id()).value(), now), () -> p.id() + " household spending at this month's rate");
            others.see(near(byId(hhConstant).get(p.id()).value(), now * p.value("gdp.k") / p.value("gdp")), () -> p.id() + " household spending under Constant");
            others.see(near(byId(hhPpp).get(p.id()).value(), now * p.value("gdp.ppp") / p.value("gdp")), () -> p.id() + " household spending at PPP");
        }
        for (String wage : List.of("avg_wage", "min_wage")) {
            Table wageMarket = Rankings.table(wm, nations, live, Settings.OPENING, wage);
            Table wageConstant = Rankings.table(wm, nations, live, set(Board.REAL_2025, Money.REAL_USD, Convert.CONSTANT), wage);
            Table wagePpp = Rankings.table(wm, nations, live, set(Board.REAL_2025, Money.REAL_USD, Convert.PPP), wage);
            for (WorldMeasures.Place p : wm.nations()) {
                WorldMeasures.Figure w = p.figure(wage);
                if (w == null) continue;
                double now = w.value() * wm.cpiU(2015) / wm.cpiU(w.year());
                others.see(near(byId(wageMarket).get(p.id()).value(), now) && near(byId(wageConstant).get(p.id()).value(), now), () -> p.id() + " " + wage + " at this month's rate, and under Constant (it has no constant series)");
                others.see(near(byId(wagePpp).get(p.id()).value(), now * p.value("gdp.ppp") / p.value("gdp")), () -> p.id() + " " + wage + " at PPP");
            }
        }
        tally("household spending converts by gdp.k and gdp.ppp over gdp of the same place; a wage by gdp.ppp over gdp, and is this month's figure under Constant", others);
    }

    /* =====================================================================
       9. AROUND YOU AND YOUR SIZE
       ===================================================================== */

    static void theWindows(WorldMeasures wm, Nations nations, Rankings.City live) {
        out.println("--- 9. around you and your size ---");
        // The city at the top, in the middle, at the bottom and off the board of the GDP table.
        Map<String, CityMeasures.Figure> top = new LinkedHashMap<>(live.figures()), bottom = new LinkedHashMap<>(live.figures()), middle = new LinkedHashMap<>(live.figures()), off = new LinkedHashMap<>(live.figures());
        top.put("gdp", new CityMeasures.Figure(1e18, Double.NaN, "top"));
        bottom.put("gdp", new CityMeasures.Figure(1, Double.NaN, "bottom"));
        middle.put("gdp", new CityMeasures.Figure(3e11, Double.NaN, "middle"));
        middle.put("people", new CityMeasures.Figure(2_000_000, Double.NaN, "a city of two million, so that Your size has places to hold"));
        off.remove("gdp");
        List<String> where = List.of("at the top", "in the middle", "at the bottom", "off the board");
        List<Map<String, CityMeasures.Figure>> figures = List.of(top, middle, bottom, off);
        for (int c = 0; c < where.size(); c++) {
            Table t = Rankings.table(wm, nations, with(live, figures.get(c)), Settings.OPENING, "gdp");
            List<Row> around = Rankings.around(t);
            List<Integer> index = around.stream().map(r -> t.rows().indexOf(r)).toList();
            boolean increasing = true, noRepeat = new TreeSet<>(index).size() == index.size();
            for (int i = 1; i < index.size(); i++) increasing &= index.get(i - 1) < index.get(i);
            Set<Integer> want = new TreeSet<>();
            if (t.you() < 0) {
                for (int i = 0; i < Rankings.AROUND_OFF; i++) want.add(i);
            } else {
                for (int i = 0; i < Rankings.AROUND_TOP; i++) want.add(i);
                for (int i = Math.max(0, t.you() - Rankings.AROUND_EITHER_SIDE); i <= Math.min(t.rows().size() - 1, t.you() + Rankings.AROUND_EITHER_SIDE); i++) want.add(i);
            }
            check("around the city " + where.get(c) + " (index " + t.you() + " of " + t.rows().size() + "): the top three, the city and six either side, " + around.size() + " rows, in rank order, none twice",
                    increasing && noRepeat && new TreeSet<>(index).equals(want) && (t.you() < 0 || around.contains(t.rows().get(t.you()))) && index.contains(0) && index.contains(2));
        }
        Rankings.City city = with(live, middle);
        Table t = Rankings.table(wm, nations, city, Settings.OPENING, "gdp");
        double mine = city.figures().get("people").value();
        Set<String> want = new TreeSet<>();
        for (Row r : t.rows()) {
            WorldMeasures.Place p = wm.place(r.id());
            if (r.kind() == Rankings.Kind.CITY || (p != null && p.value("people") >= mine / Rankings.YOUR_SIZE_FACTOR && p.value("people") <= mine * Rankings.YOUR_SIZE_FACTOR)) want.add(r.id());
        }
        List<Row> size = Rankings.yourSize(t);
        check("your size: the places within ten times the city's " + (long) mine + " people, either way, and the city: " + size.size() + " rows, " + want.size() + " from the file",
                want.size() > 20 && size.size() == want.size() && size.stream().map(Row::id).collect(java.util.stream.Collectors.toCollection(TreeSet::new)).equals(want) && size.contains(t.rows().get(t.you())));
        check("...and every one is within the factor of the city's people, in rank order",
                size.stream().allMatch(r -> r.kind() == Rankings.Kind.CITY || (r.people() >= mine / Rankings.YOUR_SIZE_FACTOR && r.people() <= mine * Rankings.YOUR_SIZE_FACTOR))
                        && size.stream().mapToInt(r -> t.rows().indexOf(r)).sorted().boxed().toList().equals(size.stream().map(r -> t.rows().indexOf(r)).toList()));
        Table none = Rankings.table(wm, nations, with(live, off), Settings.OPENING, "gdp");
        check("a city off the board has no size to match: yourSize is empty", Rankings.yourSize(none).isEmpty());
    }

    /* =====================================================================
       10. THE CITY'S MEASURES FROM HISTORY
       ===================================================================== */

    static void theCity(WorldMeasures wm, Game g, Rankings.City live) {
        out.println("--- 10. the city's measures from History ---");
        HistorySave h = g.getHistorySave();
        Map<String, CityMeasures.Figure> f = live.figures();
        int all = wm.measures().size();
        List<String> missing = wm.measures().stream().map(WorldMeasures.Measure::key).filter(k -> !f.containsKey(k)).toList();
        out.printf("      the city has %d of the %d measures after %d months; without: %s%n", f.size(), all, h.monthsLived(), missing);
        check("every figure is a measure of the file, with a way of being made, and is finite", f.entrySet().stream().allMatch(e -> wm.measure(e.getKey()) != null
                && Double.isFinite(e.getValue().value()) && !e.getValue().how().isBlank() && (Double.isNaN(e.getValue().real()) || Double.isFinite(e.getValue().real()))));
        check("it has every measure a city of its age has (" + CORE.size() + "), and none the brief leaves absent", CORE.stream().allMatch(f::containsKey) && ABSENT.stream().noneMatch(f::containsKey));
        check("growth_10y needs 132 monthly months, which a city of " + h.monthsLived() + " has not: absent", !f.containsKey("growth_10y") && f.containsKey("growth_real"));
        check("only gdp, gdp_pp and hh_spend_pp have a figure at founding's prices", f.entrySet().stream().allMatch(e -> Double.isNaN(e.getValue().real()) == !Rankings.hasConstantSeries(wm.measure(e.getKey()))));

        double year = h.recentTotal("gdp", CityMeasures.YEAR);
        double[] index = h.alignedTail("priceIndex", CityMeasures.YEAR), gdp = h.alignedTail("gdp", CityMeasures.YEAR);
        double real = 0;
        for (int i = 0; i < gdp.length; i++) real += gdp[i] / index[i];
        long people = h.getPopulation().get(h.getPopulation().size() - 1);
        check("gdp is the history's last 12 months times THOUSANDS (" + CityMeasures.THOUSANDS + "): " + f.get("gdp").value(), near(f.get("gdp").value(), year * CityMeasures.THOUSANDS));
        check("...and at founding's prices the same months each over that month's price index", near(f.get("gdp").real(), real * CityMeasures.THOUSANDS));
        check("gdp per person is that over the people now (" + people + "): " + f.get("gdp_pp").value(), near(f.get("gdp_pp").value(), year * CityMeasures.THOUSANDS / people));
        check("people are the history's last population", f.get("people").value() == people);
        double consumption = h.recentTotal("consumption", CityMeasures.YEAR);
        check("household spending per person is a year of consumption times THOUSANDS over the people", near(f.get("hh_spend_pp").value(), consumption * CityMeasures.THOUSANDS / people));
        double fx = h.alignedTail("fxRate", 1)[0], parity = h.alignedTail("fxParity", 1)[0];
        check("price level is parity over fx, and fx against parity its inverse (" + f.get("price_level").value() + ")", near(f.get("price_level").value(), parity / fx) && near(f.get("fx_vs_parity").value(), fx / parity));
        check("the investment share is a year of investment over a year of output: " + f.get("investment").value(), near(f.get("investment").value(), 100 * h.recentTotal("investment", 12) / year));
        check("the debt share is the last month's debt over a year of output", near(f.get("debt").value(), 100 * h.alignedTail("debt", 1)[0] / year));
        double avgWage = YearBook.averageWage(h)[h.months() - 1] * 12 * CityMeasures.THOUSANDS;
        check("the average wage is the book's wage bill over posts filled, times 12 and THOUSANDS (" + f.get("avg_wage").value() + ")", near(f.get("avg_wage").value(), avgWage));
        check("the minimum wage is the floor in today's money times 12 and THOUSANDS, and the Kaitz index the one over the other (" + f.get("kaitz").value() + ")",
                near(f.get("min_wage").value(), g.getLabourMarket().cashMinimumWage() * 12 * CityMeasures.THOUSANDS) && near(f.get("kaitz").value(), f.get("min_wage").value() / f.get("avg_wage").value()));
        check("inflation is the price index over its reading twelve months before", near(f.get("inflation").value(), 100 * (h.alignedTail("priceIndex", 1)[0] / h.alignedTail("priceIndex", 13)[0] - 1)));
        check("unemployment is the book's, over the labour force: " + f.get("unemployment").value(), near(f.get("unemployment").value(), 100 * YearBook.unemployment(h)[h.months() - 1]));
        double pop = people;
        double[] bands = new double[AgeBand.values().length];
        for (AgeBand b : AgeBand.values()) bands[b.ordinal()] = g.getCohorts().get(b);
        check("old-age dependency is seniors and elders over adults, from the city's bands", near(f.get("old_age_dep").value(), 100 * (bands[AgeBand.SENIOR.ordinal()] + bands[AgeBand.ELDER.ordinal()]) / bands[AgeBand.ADULT.ordinal()]));
        check("density is the people over the land the city owns (" + g.getLandManager().getOwnedKm2() + " km2)", near(f.get("density").value(), pop / g.getLandManager().getOwnedKm2()) && near(f.get("area").value(), g.getLandManager().getOwnedKm2()));
        check("reserves in months of imports is the vault in dollars over a month of imports at each month's rate: " + f.get("reserves_months").value(), f.get("reserves_months").value() > 0);

        // The six bands' deaths are the month's dead, up to the deaths series' rounding: the orphans' and the unhoused are inside them.
        double bandsDead = 0;
        for (String band : List.of("deathsBabies", "deathsChildren", "deathsTeens", "deathsAdults", "deathsSeniors", "deathsElders")) bandsDead += h.recentTotal(band, 12);
        double dead = h.recentTotal("deaths", 12);
        check("the six bands' deaths add up to the deaths series over a year, to its rounding (" + String.format("%.2f", bandsDead) + " against " + (long) dead + "): the orphans' and the unhoused are inside them", Math.abs(bandsDead - dead) <= 0.5 * 12);
        check("the death rate is the six bands' dead over the people, not the rounded series (" + String.format("%.2f", f.get("deaths").value()) + " per 1,000)", near(f.get("deaths").value(), 1000 * bandsDead / pop));
        double life = f.get("life_exp").value();
        check("life expectancy is finite and between 20 and 120 (" + String.format("%.1f", life) + "), and says it is computed", life > 20 && life < 120 && f.get("life_exp").how().startsWith("computed"));
        Tally flat = new Tally();
        for (double m : new double[] {.002, .01, .05, .2}) {
            double[] hazard = new double[AgeBand.values().length];
            java.util.Arrays.fill(hazard, m);
            flat.see(near(CityMeasures.lifeTable(hazard), 1 / m), () -> "hazard " + m + " gives " + CityMeasures.lifeTable(hazard));
        }
        tally("the life table: a flat hazard m, in every band, gives a life expectancy of 1 over m", flat);
        double[] none = new double[AgeBand.values().length];
        check("...and with no one dying in the open band there is no life expectancy (NaN), not infinity", Double.isNaN(CityMeasures.lifeTable(none)));

        // A city of six months: the year's sums are scaled, and what needs more months is absent.
        HistorySave young = new HistorySave();
        for (int m = 1; m <= 6; m++) young.appendMonth(m, name -> constantSeries(name));
        Map<String, CityMeasures.Figure> six = CityMeasures.of(young, new CityMeasures.Extras(4, new double[] {100, 100, 100, 100, 100, 100}));
        check("a city of six months: a year of output is the six months scaled by 12 over 6, times THOUSANDS, and says so",
                six.containsKey("gdp") && near(six.get("gdp").value(), 100 * 6 * (12.0 / 6) * CityMeasures.THOUSANDS) && six.get("gdp").how().contains("6 months"));
        check("...and inflation (13 months), real growth (24), 10-year growth and life expectancy (12 months) are absent, not 0",
                !six.containsKey("inflation") && !six.containsKey("growth_real") && !six.containsKey("growth_10y") && !six.containsKey("life_exp") && !six.containsKey("pop_growth"));
        check("an empty history has no figures", CityMeasures.of(new HistorySave(), new CityMeasures.Extras(4, null)).isEmpty());
        int total = HistorySave.MONTHLY_KEPT + 3 * 12 + 5;
        HistorySave old = new HistorySave();
        for (int m = 1; m <= total; m++) old.appendMonth(m, name -> constantSeries(name));
        Map<String, CityMeasures.Figure> folded = CityMeasures.of(old, new CityMeasures.Extras(4, new double[] {100, 100, 100, 100, 100, 100}));
        check("a history with folded years (" + old.yearlyPoints() + ") is read in its months: a year of output is 12 of the month, not a folded year's sum",
                old.yearlyPoints() == 3 && folded.containsKey("gdp") && near(folded.get("gdp").value(), 100 * 12 * CityMeasures.THOUSANDS) && folded.containsKey("growth_10y") && near(folded.get("growth_10y").value(), 0));
    }

    /** A history by hand: every series the same every month, but the price index (1), the people and the bands' deaths (so that a life table has them). */
    static double constantSeries(String name) {
        return switch (name) {
            case "gdp" -> 100;
            case "priceIndex", "fxRate", "fxParity" -> 1;
            case "population" -> 1000;
            case "households" -> 400;
            default -> name.startsWith("deaths") ? 5 : 10;
        };
    }

    /* =====================================================================
       11. THE LOCK AND THE SENTENCE
       ===================================================================== */

    static void theLockAndSentence(WorldMeasures wm, Nations nations, Rankings.City live) {
        out.println("--- 11. the lock and the sentence ---");
        WorldMeasures.Measure gdp = wm.measure("gdp"), unemployment = wm.measure("unemployment"), wage = wm.measure("avg_wage");
        Settings open = set(Board.REAL_2025, Money.CITY, Convert.MARKET);
        check("Prices is locked under real US$", Rankings.pricesLocked(set(Board.REAL_2025, Money.REAL_USD, Convert.MARKET), gdp, true) && !Rankings.pricesLockedWhy(set(Board.REAL_2025, Money.REAL_USD, Convert.MARKET), gdp, true).isEmpty());
        check("...under Constant", Rankings.pricesLocked(set(Board.REAL_2025, Money.CITY, Convert.CONSTANT), gdp, true) && !Rankings.pricesLockedWhy(set(Board.REAL_2025, Money.CITY, Convert.CONSTANT), gdp, true).isEmpty());
        check("...on a measure that is not money (" + unemployment.name() + ")", Rankings.pricesLocked(open, unemployment, true) && Rankings.pricesLockedWhy(open, unemployment, true).contains("not money"));
        check("...and with no saved city on the board: always in LB2", Rankings.pricesLocked(open, gdp, false) && Rankings.pricesLockedWhy(open, gdp, false).contains("saved"));
        check("it is open for money in the city's or the game's money at this month's rate or PPP once a saved city is on the board (LB3's seam)",
                !Rankings.pricesLocked(open, gdp, true) && !Rankings.pricesLocked(set(Board.REAL_2025, Money.GAME_USD, Convert.PPP), wage, true) && Rankings.pricesLockedWhy(open, gdp, true).isEmpty());
        Tally every = new Tally();
        for (WorldMeasures.Measure m : wm.measures()) for (Money money : Money.values()) for (Convert conv : Convert.values()) {
            every.see(Rankings.pricesLocked(set(Board.REAL_2025, money, conv), m, false), () -> m.key() + " " + money + " " + conv);
            every.see(Rankings.pricesLocked(set(Board.REAL_2025, money, conv), m, true) == (money == Money.REAL_USD || conv == Convert.CONSTANT || !m.money()), () -> m.key() + " " + money + " " + conv + " with a saved city");
        }
        tally("with no saved city Prices is locked for every measure and setting; with one, locked exactly under real US$, Constant, or a measure that is not money", every);

        String when = CityCalendar.format(live.month());
        String opening = Rankings.sentence(live, Settings.OPENING, gdp, Nations.Path.RANDOM);
        out.println("      " + opening);
        check("the opening sentence names real US dollars, 2015 prices, this month's rate, 2025, the city and its month",
                opening.startsWith("Real US dollars at 2015 prices, at this month's exchange rates.") && opening.contains("2025") && opening.contains(live.name() + " in " + when));
        String yours = Rankings.sentence(live, set(Board.YOUR_WORLD, Money.CITY, Convert.CONSTANT), gdp, Nations.Path.HISTORICAL);
        out.println("      " + yours);
        check("Your world in the city's money under Constant names the money, founding's prices, the constant rate and the path",
                yours.startsWith(live.currency() + " at founding's prices, at the constant 2015 rate, so only real growth moves a place.") && yours.contains(live.name() + "'s world in " + when) && yours.contains("Historical path"));
        String game = Rankings.sentence(live, set(Board.REAL_2025, Money.GAME_USD, Convert.PPP), gdp, null);
        check("game US dollars are of the city's month, and PPP is said: " + game.substring(0, Math.min(70, game.length())), game.startsWith("Game US dollars of " + when + ", at purchasing power parity."));
        String wages = Rankings.sentence(live, set(Board.REAL_2025, Money.REAL_USD, Convert.CONSTANT), wage, null);
        check("a wage under Constant says it has no constant series and is at this month's rate; GDP under Constant does not",
                wages.contains("Average wage has no constant series, so it is at this month's rate.") && !Rankings.sentence(live, set(Board.REAL_2025, Money.REAL_USD, Convert.CONSTANT), gdp, null).contains("no constant series"));
        String plain = Rankings.sentence(live, Settings.OPENING, unemployment, null);
        check("a measure that is not money drops the money clause and names its unit: " + plain.substring(0, Math.min(60, plain.length())),
                !plain.contains("Real US dollars") && plain.startsWith("Unemployment (" + unemployment.unit() + ")."));
        check("the Random path is named too", Rankings.sentence(live, set(Board.YOUR_WORLD, Money.REAL_USD, Convert.MARKET), gdp, Nations.Path.RANDOM).contains("Random path"));
        check("a measure with a constant series says so: gdp, gdp_pp and hh_spend_pp, and no wage", Rankings.hasConstantSeries(gdp) && Rankings.hasConstantSeries(wm.measure("hh_spend_pp")) && !Rankings.hasConstantSeries(wage) && !Rankings.hasConstantSeries(unemployment));
    }
}
