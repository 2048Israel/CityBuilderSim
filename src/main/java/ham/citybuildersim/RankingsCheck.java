package ham.citybuildersim;

import com.google.gson.Gson;
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
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
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
 * LB3, YOUR SAVED CITIES. The fixture is the live city saved to a scratch folder (GameFiles.scratch, never the real save folder) at its
 * month, played on a year and saved, then reformed by a factor of 100 and saved again: three slots, one city at three points.
 *  12. READING THE SLOTS: slot 0 is written and left out, an empty slot is off, a save from an older build, a newer one, a damaged
 *      one and one with no history are off and counted, every file's bytes and modified time are as they were after a read, and a
 *      second refresh with nothing changed reuses every slot while one re-saved slot alone is read again.
 *  13. PRICES MOVES ONLY SAVED ROWS, NEVER UNDER REAL US$: per year against static changes no other row; under real US$ and
 *      under Constant, or for a measure that is not money, it changes nothing; in game US$ a saved city's figure is its own
 *      year's (X over its rate) per year and its real figure at this city's prices static; static boards keep one order in the
 *      three moneys; the sentence and the drawer name and open a saved city.
 *  14. A REFORM LEAVES EVERY RANK: the same city saved before and after a reform by 100 ranks alike, on both boards, in every money,
 *      Prices and Convert, for every measure, its values equal to 1e-9; side by side on one board the two share a rank (Rankings.TIE).
 *  15. AT YOUR AGE EQUALS THE HISTORY FILE'S MONTH: a saved city older than the age has the figures, rate, parity and price level
 *      of its history file at that month; one younger is no row and is listed as stopped, with its last month.
 *  16. THE RANK LINE: one point for each December the city has a figure for, the last one the board's own rank for the city at that
 *      December, and neither Your world nor the saved cities play any part.
 *  17. THE SLIM HISTORY EQUALS THE FULL ONE: a slot keeps only the series the measures read, and the measures from it are the
 *      full history's, figure for figure, at every month; the game dollar's price level is recorded each month and the reform
 *      does not touch it.
 *  18. CITYMEASURES.AT: at the last month it is of(); at the first month it was asked it is what of() said then, however far the
 *      city has played since; a month the history never reached or no longer holds as a month has no figures; Extras.UNKNOWN
 *      leaves out exactly what needs the land and the bands.
 *
 * Every fixture causes its condition: the founding city is a founded one, a tie is a real file with three
 * figures set equal, a missing figure a real file with one removed, a save from an older build a real save with its
 * format set below the oldest this build loads.
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

            // LB3: the live city saved, played on and saved, reformed and saved; the sections read those three slots.
            Saves saves = makeSaves(g, live);
            theSlots(wm, random, saves);
            thePrices(wm, random, saves);
            theReform(wm, random, saves);
            theAge(wm, random, saves);
            theRankLine(wm, random, saves);
            theSlimHistory(saves);
            theMonth(live, saves);
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
        double dry = LandManager.km2(g.getLandManager().getOwnedSqFt());
        check("area is the city's dry ground, the real file's land area (" + dry + " km2 of " + g.getLandManager().getOwnedKm2() + " with its water), and density the people over it",
                near(f.get("area").value(), dry) && dry > 0 && dry <= g.getLandManager().getOwnedKm2() && near(f.get("density").value(), pop / dry));
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

    /* =====================================================================
       LB3: YOUR SAVED CITIES. THE FIXTURE AND ITS HELPERS
       ===================================================================== */

    /**
     * The live city saved three times to its own scratch folder: slot 1 at its month, slot 2 a year on, slot 3 the same month as slot 2 after a
     * reform by 100. youngLast and oldLast are the last months of the histories those saves wrote; before and after the active city at slot 2's
     * month either side of the reform (the units differ by 100); youngReplayed what CityMeasures.at says of slot 1's month once the city has
     * played on, to be held to what of() said then (the live city's figures).
     */
    static final class Saves {
        Game game;
        GameFiles files;
        boolean reformed;
        int youngLast, oldLast;
        Rankings.City before, after;
        CityMeasures.Extras youngExtras;
        Map<String, CityMeasures.Figure> youngReplayed;
        SavedCities snapshot;
    }

    static final long LB3_FACTOR = 100;

    static Saves makeSaves(Game g, Rankings.City live) {
        Saves s = new Saves();
        s.game = g;
        s.files = g.getGameFiles();
        s.youngExtras = liveExtras(g);
        s.youngLast = lastMonth(g.getHistorySave());
        quietly(() -> g.saveGame(1, "Young"));
        quietly(() -> LongPlaytest.probePlay(g, live.month() + 12));
        s.oldLast = lastMonth(g.getHistorySave());
        s.youngReplayed = CityMeasures.at(g.getHistorySave(), s.youngExtras, s.youngLast);
        s.before = Rankings.City.of(g);
        quietly(() -> g.saveGame(2, "Old"));
        quietly(() -> s.reformed = g.reformCurrencyForTest(LB3_FACTOR));
        quietly(() -> g.saveGame(3, "Reformed"));
        s.after = Rankings.City.of(g);
        quietly(() -> s.snapshot = SavedCities.read(s.files));
        return s;
    }

    static void quietly(Runnable work) {
        PrintStream real = System.out;
        System.setOut(nothing());
        try {
            work.run();
        } finally {
            System.setOut(real);
        }
    }

    static int lastMonth(HistorySave h) {
        return h.getMonth().get(h.months() - 1);
    }

    /** What Rankings.City.of(Game) gives CityMeasures for the live city: its land, its bands and the wage floor in today's money. */
    static CityMeasures.Extras liveExtras(Game g) {
        double[] bands = new double[AgeBand.values().length];
        for (AgeBand band : AgeBand.values()) bands[band.ordinal()] = g.getCohorts().get(band);
        return new CityMeasures.Extras(LandManager.km2(g.getLandManager().getOwnedSqFt()), bands, g.getLabourMarket().cashMinimumWage());
    }

    /** The saved cities of these slots, in the order the slots are named. */
    static List<Rankings.Saved> only(List<Rankings.Saved> all, int... slots) {
        List<Rankings.Saved> kept = new ArrayList<>();
        for (int slot : slots) for (Rankings.Saved s : all) if (s.slot() == slot) kept.add(s);
        return kept;
    }

    /** A slot's history file read whole, as Game.loadHistory reads it. */
    static HistorySave fullHistory(GameFiles files, int slot) throws IOException {
        try (Reader in = Files.newBufferedReader(files.historyFile(slot))) {
            HistorySave h = new HistorySave();
            h.restoreFrom(new Gson().fromJson(in, HistorySave.class));
            return h;
        }
    }

    /** Whether two sets of figures are the same, measure for measure in the same order, each figure to the digit. */
    static boolean same(Map<String, CityMeasures.Figure> a, Map<String, CityMeasures.Figure> b) {
        return new ArrayList<>(a.keySet()).equals(new ArrayList<>(b.keySet())) && a.equals(b);
    }

    /** Two figures equal to a relative 1e-9, with no floor: a small figure is held as tightly as a large one. */
    static boolean relative(double a, double b) {
        return a == b || Math.abs(a - b) <= TOLERANCE * Math.max(Math.abs(a), Math.abs(b));
    }

    /** Copies slot `from` of one folder to slot `to` of another: the save with its format set to `format` unless that is 0, and the history file if asked. */
    static void copySlot(GameFiles from, int fromSlot, GameFiles to, int toSlot, int format, boolean history) throws IOException {
        Files.createDirectories(to.savesDirectory());
        String save = Files.readString(from.saveFile(fromSlot));
        if (format != 0) {
            String changed = save.replaceFirst("\"saveFormat\"\\s*:\\s*\\d+", "\"saveFormat\":" + format);
            if (changed.equals(save)) throw new IllegalStateException("the save has no saveFormat to set");
            save = changed;
        }
        Files.writeString(to.saveFile(toSlot), save);
        if (history) Files.copy(from.historyFile(fromSlot), to.historyFile(toSlot), StandardCopyOption.REPLACE_EXISTING);
    }

    /** Every file under a folder: its size, modified time and a hash of its bytes. */
    static Map<Path, String> fingerprint(GameFiles files) throws IOException {
        Map<Path, String> seen = new HashMap<>();
        try (java.util.stream.Stream<Path> walk = Files.walk(files.getDirectory())) {
            for (Path p : (Iterable<Path>) walk::iterator) {
                if (Files.isRegularFile(p)) seen.put(p, Files.size(p) + ":" + Files.getLastModifiedTime(p).toMillis() + ":" + Arrays.hashCode(Files.readAllBytes(p)));
            }
        }
        return seen;
    }

    /* =====================================================================
       12. READING THE SLOTS
       ===================================================================== */

    static void theSlots(WorldMeasures wm, Nations nations, Saves f) throws IOException {
        out.println("--- 12. reading the slots ---");
        check("fixture: the live city was saved in slots 1, 2 and 3 with a history each, and slot 3 is after a reform by " + LB3_FACTOR,
                f.reformed && f.youngLast < f.oldLast && Files.isRegularFile(f.files.saveFile(1)) && Files.isRegularFile(f.files.historyFile(3))
                        && Files.isRegularFile(f.files.saveFile(2)) && Files.isRegularFile(f.files.historyFile(2)) && Files.isRegularFile(f.files.saveFile(3)));
        GameFiles files = GameFiles.scratch("rankingsslots");
        copySlot(f.files, 1, files, 1, 0, true);                                                    // a city
        copySlot(f.files, 2, files, 2, 0, true);                                                    // ...and another
        copySlot(f.files, 1, files, 0, 0, true);                                                    // the autosave, a good save, to be left out
        copySlot(f.files, 1, files, 5, GameVersion.OLDEST_LOADABLE_FORMAT - 1, true);               // a save from an older build
        copySlot(f.files, 1, files, 6, GameVersion.SAVE_FORMAT + 1, true);                          // ...and from a newer
        Files.writeString(files.saveFile(7), "{ this is not a save");                               // a save that cannot be read
        copySlot(f.files, 1, files, 8, 0, false);                                                   // a save with no history file
        copySlot(f.files, 1, files, 9, 0, true);
        Files.writeString(files.historyFile(9), "[[[ this is not a history");                       // ...and one whose history cannot be read
        int saves = 0;
        for (int slot = 1; slot <= GameFiles.SLOT_COUNT; slot++) if (Files.isRegularFile(files.saveFile(slot))) saves++;

        Map<Path, String> before = fingerprint(files);
        SavedCities[] read = new SavedCities[1];
        quietly(() -> read[0] = SavedCities.read(files));
        SavedCities snap = read[0];
        check("a read writes nothing: every file's bytes and modified time are as they were, none added and none removed", before.equals(fingerprint(files)) && before.size() >= 10);

        List<Rankings.Saved> now = snap.now();
        List<Integer> slots = now.stream().map(Rankings.Saved::slot).toList();
        check("the cities on the board are slots 1 and 2, in slot order: " + slots, slots.equals(List.of(1, 2)));
        check("slot 0, the autosave, holds a good save and is left out: not on the board, not counted", Files.isRegularFile(files.saveFile(0)) && !slots.contains(0) && snap.left().total() == 5);
        check("the empty slots (3, 4 and 10) are off the board and not counted as left out", !slots.contains(3) && !slots.contains(4) && !slots.contains(10) && !Files.exists(files.saveFile(3)));
        SavedCities.LeftOut left = snap.left();
        check("a save from an older build (format " + (GameVersion.OLDEST_LOADABLE_FORMAT - 1) + ") is off and counted, and one from a newer build too: " + left,
                left.olderBuild() == 1 && left.newerBuild() == 1);
        check("a save that cannot be read and a history that cannot be read are two damaged", left.damaged() == 2);
        check("a save with no history file is off and counted", left.noHistory() == 1 && !slots.contains(8));
        check("a first read parses every save in slots 1 to " + GameFiles.SLOT_COUNT + " (" + saves + "), the autosave not among them: reads " + snap.reads(), snap.reads() == saves);

        Rankings.City active = f.before;
        Row row = byId(Rankings.table(wm, nations, active, now, Settings.OPENING, "people")).get("saved:1");
        Rankings.City city = now.get(0).city();
        check("a saved city's row is id saved:1, kind SAVED, the city's name, its year, mark 0, source " + Rankings.SOURCE_SAVE + " and its people",
                row != null && row.kind() == Rankings.Kind.SAVED && row.name().equals(city.name()) && row.year() == CityCalendar.yearOf(city.month()) && row.mark() == 0
                        && row.source().equals(Rankings.SOURCE_SAVE) && row.people() == city.figures().get("people").value() && now.get(0).id().equals("saved:1"));
        check("...and it is the city as its history left it: its month is the history's last (" + f.youngLast + "), its money the header's (" + city.currency() + ")",
                city.month() == f.youngLast && city.currency().equals(f.game.getCurrency().plural()) && city.name().equals(f.game.getCityName()));

        SavedCities again = snap.refresh(files);
        boolean reused = again.now().size() == now.size();
        for (int i = 0; i < now.size() && reused; i++) reused = again.now().get(i).city() == now.get(i).city();
        check("a second refresh with nothing changed reads no slot and reuses every one (reads " + again.reads() + ")", again.reads() == 0 && reused && again.left().equals(left));
        copySlot(f.files, 1, files, 2, 0, true);
        SavedCities third = again.refresh(files);
        check("slot 2 saved since is the only slot read again (reads " + third.reads() + "): it is now slot 1's city, and slot 1's city is the very object it was",
                third.reads() == 1 && third.now().size() == 2 && third.now().get(1).city().month() == f.youngLast && third.now().get(0).city() == again.now().get(0).city());
        check("a slot deleted is gone from the next snapshot, and counted nowhere", delete(files, 1) && third.refresh(files).now().stream().map(Rankings.Saved::slot).toList().equals(List.of(2)));
    }

    static boolean delete(GameFiles files, int slot) throws IOException {
        Files.delete(files.saveFile(slot));
        Files.delete(files.historyFile(slot));
        return true;
    }

    /* =====================================================================
       13. PRICES MOVES ONLY SAVED ROWS, NEVER UNDER REAL US$
       ===================================================================== */

    static void thePrices(WorldMeasures wm, Nations nations, Saves f) {
        out.println("--- 13. Prices moves only saved rows, never under real US$ ---");
        Rankings.City active = f.before;
        List<Rankings.Saved> saved = only(f.snapshot.now(), 1, 2);
        Rankings.City young = saved.get(0).city(), old = saved.get(1).city();
        out.printf("      the active city's game dollar stands at %.4f, the young save's at %.4f and the old save's at %.4f%n", active.usPrices(), young.usPrices(), old.usPrices());
        check("fixture: the young save's price level differs from the active city's, and the old save, of the active city's month, has the same",
                !relative(young.usPrices(), active.usPrices()) && relative(old.usPrices(), active.usPrices()));

        Tally others = new Tally(), moves = new Tally(), control = new Tally(), identity = new Tally(), present = new Tally();
        for (String key : MONEY) {
            WorldMeasures.Measure m = wm.measure(key);
            CityMeasures.Figure x = young.figures().get(key);
            for (Board board : Board.values()) {
                if (board == Board.YOUR_WORLD && !m.yourWorld()) continue;
                for (Convert conv : List.of(Convert.MARKET, Convert.PPP)) {
                    double rate = conv == Convert.MARKET ? young.fx() : young.parity(), activeRate = conv == Convert.MARKET ? active.fx() : active.parity();
                    for (Money money : List.of(Money.CITY, Money.GAME_USD)) {
                        Table per = Rankings.table(wm, nations, active, saved, new Settings(board, money, Prices.PER_YEAR, conv), key);
                        Table fixed = Rankings.table(wm, nations, active, saved, new Settings(board, money, Prices.STATIC, conv), key);
                        String what = key + " on " + board + " in " + money + " at " + conv;
                        Map<String, Row> a = byId(per), b = byId(fixed);
                        boolean same = a.size() == b.size();
                        for (Row r : per.rows()) {
                            if (r.kind() == Rankings.Kind.SAVED) continue;
                            Row o = b.get(r.id());
                            same &= o != null && o.value() == r.value() && o.shown() == r.shown();
                        }
                        others.see(same, () -> what);
                        boolean both = a.containsKey("saved:1") && b.containsKey("saved:1") && a.containsKey("saved:2") && b.containsKey("saved:2");
                        present.see(both, () -> what);
                        if (!both) continue;
                        moves.see(!relative(a.get("saved:1").value(), b.get("saved:1").value()) && !relative(a.get("saved:1").shown(), b.get("saved:1").shown()), () -> what);
                        control.see(relative(a.get("saved:2").value(), b.get("saved:2").value()), () -> what + " (the save of the active city's own month)");
                        if (x != null && money == Money.GAME_USD) {
                            identity.see(relative(a.get("saved:1").shown(), x.value() / rate), () -> what + ": per year its own year's game dollars, X over its rate");
                            identity.see(relative(b.get("saved:1").value(), x.value() / rate / young.usPrices()) && relative(b.get("saved:1").shown(), b.get("saved:1").value() * active.usPrices()),
                                    () -> what + ": static its real dollars times this city's price level");
                        }
                        if (x != null && money == Money.CITY) {
                            identity.see(relative(a.get("saved:1").shown(), x.value() / rate * activeRate), () -> what + ": per year X over its rate times this city's rate");
                        }
                    }
                }
            }
        }
        tally("Prices per year against static moves no row that is not a saved city's: value and shown the same, on both boards, in the city's money and the game's, at this month's rate and PPP", others);
        tally("...and the boards had both saved cities to move (money measures)", present);
        tally("...it moves the saved city whose game dollar is dearer or cheaper than this city's", moves);
        tally("...and does not move the one whose price level is this city's", control);
        tally("in game US$ a saved city per year is its own year's dollars (X over its rate), static its real dollars (X over rate over its price level) times this city's level; in the city's money per year X over its rate times this city's", identity);

        Tally fixedPrices = new Tally(), shown = new Tally();
        for (WorldMeasures.Measure m : wm.measures()) {
            for (Board board : Board.values()) {
                if (board == Board.YOUR_WORLD && !m.yourWorld()) continue;
                for (Money money : Money.values()) {
                    for (Convert conv : Convert.values()) {
                        if (m.money() && money != Money.REAL_USD && conv != Convert.CONSTANT) continue;
                        Table per = Rankings.table(wm, nations, active, saved, new Settings(board, money, Prices.PER_YEAR, conv), m.key());
                        Table fixed = Rankings.table(wm, nations, active, saved, new Settings(board, money, Prices.STATIC, conv), m.key());
                        String what = m.key() + " on " + board + " in " + money + " at " + conv;
                        fixedPrices.see(per.rows().equals(fixed.rows()), () -> what);
                        if (m.key().equals("gdp") || m.key().equals("people")) shown.see(byId(per).containsKey("saved:1") && byId(per).containsKey("saved:2"), () -> what);
                    }
                }
            }
        }
        tally("under real US$, under Constant, and for a measure that is not money, Prices changes nothing: the whole table the same, every row, rank, value and figure shown", fixedPrices);
        tally("...with saved cities on those boards to have moved (GDP and people)", shown);

        Tally order = new Tally();
        for (String key : MONEY) {
            WorldMeasures.Measure m = wm.measure(key);
            for (Board board : Board.values()) {
                if (board == Board.YOUR_WORLD && !m.yourWorld()) continue;
                for (Convert conv : Convert.values()) {
                    List<List<String>> orders = new ArrayList<>();
                    List<List<Integer>> ranks = new ArrayList<>();
                    for (Money money : Money.values()) {
                        Table t = Rankings.table(wm, nations, active, saved, new Settings(board, money, Prices.STATIC, conv), key);
                        orders.add(ids(t));
                        ranks.add(t.rows().stream().map(Row::rank).toList());
                    }
                    order.see(orders.get(0).equals(orders.get(1)) && orders.get(0).equals(orders.get(2)) && ranks.get(0).equals(ranks.get(1)) && ranks.get(0).equals(ranks.get(2))
                            && orders.get(0).contains("saved:1"), () -> key + " on " + board + " at " + conv);
                }
            }
        }
        tally("a static board is one order, with the saved cities in it, in the city's money, game US$ and real US$", order);

        Settings perYear = new Settings(Board.REAL_2025, Money.CITY, Prices.PER_YEAR, Convert.MARKET), fixedYear = new Settings(Board.REAL_2025, Money.CITY, Prices.STATIC, Convert.MARKET);
        WorldMeasures.Measure gdp = wm.measure("gdp"), unemployment = wm.measure("unemployment");
        check("with a saved city on the board Prices is open for money in the city's money at this month's rate", !Rankings.pricesLocked(perYear, gdp, !saved.isEmpty()) && Rankings.pricesLockedWhy(perYear, gdp, !saved.isEmpty()).isEmpty());
        String plain = Rankings.sentence(active, perYear, gdp, null);
        check("with no saved city on the board the sentence is the board's alone", Rankings.sentence(active, perYear, gdp, null, List.of(), false).equals(plain));
        String yearly = Rankings.sentence(active, perYear, gdp, null, saved, false);
        String stat = Rankings.sentence(active, fixedYear, gdp, null, saved, true);
        out.println("      " + yearly);
        out.println("      " + stat);
        check("per year it adds that the saved cities are in their own year's dollars, as last saved", yearly.equals(plain + " Your saved cities in their own year's dollars, as last saved."));
        check("static at an age it adds that they are at this city's prices of its year, at its age",
                stat.equals(plain + " Your saved cities at " + active.name() + "'s prices of " + active.year() + ", at " + Rankings.ageWords(active.month()) + "."));
        Settings realUsd = new Settings(Board.REAL_2025, Money.REAL_USD, Prices.PER_YEAR, Convert.MARKET);
        check("under real US$, which Prices cannot move, it names no prices: just the saved cities as last saved",
                Rankings.sentence(active, realUsd, gdp, null, saved, false).endsWith(" Your saved cities as last saved.") && !Rankings.sentence(active, realUsd, gdp, null, saved, false).contains("own year"));
        check("a measure that is not money names none either, at an age too",
                Rankings.sentence(active, perYear, unemployment, null, saved, true).endsWith(" Your saved cities at " + Rankings.ageWords(active.month()) + "."));
        check("an age is said as years and months: 50 months is 4 years 2 months, 12 is 1 year, 13 is 1 year 1 month, 7 is 7 months",
                Rankings.ageWords(50).equals("4 years 2 months") && Rankings.ageWords(12).equals("1 year") && Rankings.ageWords(13).equals("1 year 1 month") && Rankings.ageWords(7).equals("7 months"));

        Settings yourWorld = new Settings(Board.YOUR_WORLD, Money.CITY, Prices.PER_YEAR, Convert.MARKET);
        List<Rankings.Line> lines = Rankings.drawer(wm, nations, active, saved, perYear, "saved:1");
        Rankings.Line line = lines.stream().filter(l -> l.measure().key().equals("gdp")).findFirst().orElseThrow();
        Row onReal = byId(Rankings.table(wm, nations, active, saved, perYear, "gdp")).get("saved:1");
        Row onYours = byId(Rankings.table(wm, nations, active, saved, yourWorld, "gdp")).get("saved:1");
        check("the drawer opens a saved city by its row id: its GDP line is its row on each board, mark 0, source " + Rankings.SOURCE_SAVE,
                line.real2025() == onReal.shown() && line.yourWorld() == onYours.shown() && line.realMark() == 0 && Rankings.SOURCE_SAVE.equals(line.realSource()) && lines.size() == wm.measures().size());
        check("...and with no saved cities handed over, the same id opens nothing: a line of NaN each",
                Rankings.drawer(wm, nations, active, perYear, "saved:1").stream().allMatch(l -> Double.isNaN(l.real2025()) && Double.isNaN(l.yourWorld())));
    }

    /* =====================================================================
       14. A REFORM LEAVES EVERY RANK
       ===================================================================== */

    static void theReform(WorldMeasures wm, Nations nations, Saves f) {
        out.println("--- 14. a reform leaves every rank ---");
        List<Rankings.Saved> pair = only(f.snapshot.now(), 2, 3);
        Rankings.City was = pair.get(0).city(), is = pair.get(1).city();
        out.printf("      slot 2 before the reform: rate %.6f, GDP %.6g; slot 3 after: rate %.6f, GDP %.6g%n", was.fx(), was.figures().get("gdp").value(), is.fx(), is.figures().get("gdp").value());
        check("fixture: slot 3 is slot 2's city in a money a " + LB3_FACTOR + "th the size: its rate, founding rate and GDP are that fraction, its prices, month and people the same",
                relative(is.fx() * LB3_FACTOR, was.fx()) && relative(is.foundingRate() * LB3_FACTOR, was.foundingRate()) && relative(is.figures().get("gdp").value() * LB3_FACTOR, was.figures().get("gdp").value())
                        && is.usPrices() == was.usPrices() && is.month() == was.month() && is.figures().get("people").value() == was.figures().get("people").value());
        Tally ranks = new Tally(), values = new Tally(), absent = new Tally(), money = new Tally(), together = new Tally();
        for (Rankings.City active : List.of(f.before, f.after)) {
            for (WorldMeasures.Measure m : wm.measures()) {
                for (Board board : Board.values()) {
                    if (board == Board.YOUR_WORLD && !m.yourWorld()) continue;
                    for (Money inMoney : Money.values()) {
                        for (Prices prices : Prices.values()) {
                            for (Convert conv : Convert.values()) {
                                Settings s = new Settings(board, inMoney, prices, conv);
                                String what = m.key() + " on " + board + " in " + inMoney + ", " + prices + ", " + conv + " beside the city of " + (active.fx() == f.before.fx() ? "before" : "after");
                                Row a = byId(Rankings.table(wm, nations, active, List.of(pair.get(0)), s, m.key())).get("saved:2");
                                Row b = byId(Rankings.table(wm, nations, active, List.of(pair.get(1)), s, m.key())).get("saved:3");
                                if (a == null || b == null) {
                                    absent.see(a == null && b == null, () -> what + ": a row before the reform and none after, or the other way");
                                    continue;
                                }
                                ranks.see(a.rank() == b.rank(), () -> what + ": rank " + a.rank() + " before the reform, " + b.rank() + " after");
                                values.see(relative(a.value(), b.value()) && relative(a.shown(), b.shown()), () -> what + ": " + a.value() + " against " + b.value());
                                if (m.money()) money.see(true, () -> what);
                                Map<String, Row> both = byId(Rankings.table(wm, nations, active, pair, s, m.key()));
                                Row a2 = both.get("saved:2"), b2 = both.get("saved:3");
                                together.see(a2 != null && b2 != null && a2.rank() == b2.rank(),
                                        () -> what + ": side by side, rank " + (a2 == null ? "none" : a2.rank()) + " and " + (b2 == null ? "none" : b2.rank()));
                            }
                        }
                    }
                }
            }
        }
        tally("the same city saved before and after the reform has the same rank in every measure and every Settings combination, on both boards, beside the city before and after it", ranks);
        tally("...and the same value, and the same figure shown, to a relative 1e-9", values);
        tally("...and where one has no row, so has the other (no figure of that measure)", absent);
        tally("...and side by side on one board, beside the live city they were saved from, the two copies share one rank (Rankings.TIE: the reform leaves noise in the last digits only)", together);
        check("...the money measures among them (" + money.n + " rows compared)", money.n > 100);
    }

    /* =====================================================================
       15. AT YOUR AGE EQUALS THE HISTORY FILE'S MONTH
       ===================================================================== */

    static void theAge(WorldMeasures wm, Nations nations, Saves f) throws IOException {
        out.println("--- 15. at your age ---");
        int age = (f.youngLast + f.oldLast) / 2;
        check("fixture: the age (" + age + ", " + Rankings.ageWords(age) + ") is past slot 1's last month (" + f.youngLast + ") and before slot 2's (" + f.oldLast + ")", f.youngLast < age && age < f.oldLast && age > 24);
        List<Rankings.Saved> rows = f.snapshot.at(age);
        List<SavedCities.Stopped> stopped = f.snapshot.stoppedAt(age);
        check("at that age the cities on the board are slots 2 and 3, and slot 1 is no row", rows.stream().map(Rankings.Saved::slot).toList().equals(List.of(2, 3)));
        check("slot 1 is listed as stopped, with its name and its last month (stopped at " + CityCalendar.formatShort(f.youngLast) + ")",
                stopped.equals(List.of(new SavedCities.Stopped(1, f.game.getCityName(), f.youngLast))));
        Table t = Rankings.table(wm, nations, f.before, rows, Settings.OPENING, "people");
        check("...and the board has no row for it, with slots 2 and 3 on it", !byId(t).containsKey("saved:1") && byId(t).containsKey("saved:2") && byId(t).containsKey("saved:3"));

        HistorySave full = fullHistory(f.files, 2);
        int at = full.getMonth().indexOf(age);
        check("fixture: slot 2's history file holds the age as a month, with a year behind it", at >= CityMeasures.YEAR);
        Rankings.City c = rows.get(0).city();
        Map<String, CityMeasures.Figure> expect = CityMeasures.at(full, CityMeasures.Extras.UNKNOWN, age);
        check("slot 2 at the age has CityMeasures.at of its history file at that month (" + expect.size() + " measures), its month and year the age's",
                expect.size() > 30 && same(expect, c.figures()) && c.month() == age && c.year() == CityCalendar.yearOf(age));
        double[] gdp = full.aligned("gdp");
        double year = 0;
        for (int i = at - CityMeasures.YEAR + 1; i <= at; i++) year += gdp[i];
        check("...its GDP is the history's GDP over the twelve months to the age, times THOUSANDS (" + c.figures().get("gdp").value() + ")", relative(c.figures().get("gdp").value(), year * CityMeasures.THOUSANDS));
        check("...its rate, parity and the game dollar's price level are the history file's fxRate, fxParity and usPrices at that month",
                c.fx() == full.aligned("fxRate")[at] && c.parity() == full.aligned("fxParity")[at] && c.usPrices() == full.aligned("usPrices")[at] && c.usPrices() > 1);
        Set<String> needLand = Set.of("area", "density", "old_age_dep", "life_exp", "orphans");
        check("...and the measures that need the land and the bands are absent, never guessed", needLand.stream().noneMatch(c.figures()::containsKey) && c.figures().containsKey("gdp") && c.figures().containsKey("people"));

        Rankings.City last = only(f.snapshot.now(), 2).get(0).city();
        check("at its last saved month it is the whole history's last month (" + last.month() + "), with the header's land and bands: area, density and life expectancy are there",
                last.month() == f.oldLast && last.fx() == full.aligned("fxRate")[full.months() - 1] && last.usPrices() == full.aligned("usPrices")[full.months() - 1]
                        && needLand.stream().allMatch(last.figures()::containsKey));
        check("...its area is the live city's at the month it was saved (" + last.figures().get("area").value() + " km2): both are the dry ground, the save's and the land manager's",
                f.before.month() == last.month() && relative(last.figures().get("area").value(), f.before.figures().get("area").value()));
        Rankings.City reformed = only(f.snapshot.at(age), 3).get(0).city();
        check("the reform reaches the saved history: slot 3 at the age has a rate a " + LB3_FACTOR + "th of slot 2's and the same price level, and its GDP is that fraction of slot 2's",
                relative(reformed.fx() * LB3_FACTOR, c.fx()) && reformed.usPrices() == c.usPrices() && relative(reformed.figures().get("gdp").value() * LB3_FACTOR, c.figures().get("gdp").value()));

        check("a city still on the board at the month its history ends is not stopped, one past it is: at " + f.youngLast + " slot 1 is a row, at " + (f.youngLast + 1) + " it has stopped",
                f.snapshot.stoppedAt(f.youngLast).isEmpty() && f.snapshot.at(f.youngLast).stream().anyMatch(s -> s.slot() == 1) && f.snapshot.stoppedAt(f.youngLast + 1).size() == 1);
        check("at an age none has reached, there are no rows and every city is stopped, at its own last month, in slot order",
                f.snapshot.at(f.oldLast + 100).isEmpty() && f.snapshot.stoppedAt(f.oldLast + 100).stream().map(SavedCities.Stopped::lastMonth).toList().equals(List.of(f.youngLast, f.oldLast, f.oldLast)));
        check("at an age before the history began there is no row either, and no city has stopped", f.snapshot.at(0).isEmpty() && f.snapshot.stoppedAt(0).isEmpty());
    }

    /* =====================================================================
       16. THE RANK LINE
       ===================================================================== */

    static void theRankLine(WorldMeasures wm, Nations nations, Saves f) {
        out.println("--- 16. the rank over the years ---");
        HistorySave h = f.game.getHistorySave();
        Rankings.City city = f.after;
        List<Integer> decembers = new ArrayList<>();
        for (int m : h.getMonth()) if ((m - 1) % 12 == 11) decembers.add(m);
        out.printf("      the history holds %d months and %d Decembers: %s%n", h.months(), decembers.size(), decembers);
        check("fixture: the history is all months (nothing folded) and holds at least three Decembers", h.yearlyPoints() == 0 && decembers.size() >= 3);
        Settings real = set(Board.REAL_2025, Money.REAL_USD, Convert.MARKET);
        Tally months = new Tally(), every = new Tally(), board = new Tally(), plays = new Tally(), within = new Tally();
        // The measures the real world still publishes, read from the file: a sourced measure with real figures on the board (LC1 left the rest
        // with a none: source, ranking the player's cities alone).
        List<String> published = new ArrayList<>();
        for (WorldMeasures.Measure m : wm.measures()) if (m.sourced() && Rankings.table(wm, nations, city, real, m.key()).realFigures()) published.add(m.key());
        List<String> lined = new ArrayList<>();
        check("fixture: the measures the real world still publishes, read from the file (" + published.size() + " of " + wm.measures().size()
                + "), people, GDP and unemployment among them", published.size() > 20 && published.containsAll(List.of("people", "gdp", "unemployment")));
        for (String key : published) {
            List<Rankings.RankPoint> points = Rankings.rankOverYears(wm, city, h, real, key);
            List<Integer> want = new ArrayList<>();
            for (int m : decembers) if (CityMeasures.at(h, CityMeasures.Extras.UNKNOWN, m).containsKey(key)) want.add(m);
            if (!want.isEmpty()) lined.add(key);
            months.see(points.stream().map(Rankings.RankPoint::month).toList().equals(want), () -> key + ": " + points + " against the Decembers " + want);
            if (key.equals("people") || key.equals("gdp") || key.equals("unemployment")) months.see(points.size() == decembers.size(), () -> key + " has a figure in every December: " + points.size() + " of " + decembers.size());
            for (Rankings.RankPoint p : points) {
                Rankings.City then = Rankings.cityAt(city.name(), city.currency(), city.foundingRate(), h, CityMeasures.Extras.UNKNOWN, p.month());
                Table t = Rankings.table(wm, nations, then, real, key);
                Row me = cityRow(t);
                every.see(me != null && me.rank() == p.rank() && t.rows().size() == p.of(), () -> key + " in " + CityCalendar.format(p.month()) + ": " + p);
                within.see(p.rank() >= 1 && p.rank() <= p.of() && p.of() > 1, () -> key + " in " + CityCalendar.format(p.month()) + ": " + p);
            }
            board.see(Rankings.rankOverYears(wm, city, h, set(Board.YOUR_WORLD, Money.REAL_USD, Convert.MARKET), key).equals(points), () -> key + " on Your world's settings");
            if (!points.isEmpty()) {
                Rankings.RankPoint lastPoint = points.get(points.size() - 1);
                Rankings.City then = Rankings.cityAt(city.name(), city.currency(), city.foundingRate(), h, CityMeasures.Extras.UNKNOWN, lastPoint.month());
                List<Rankings.Saved> saved = f.snapshot.at(lastPoint.month());
                Table withSaved = Rankings.table(wm, nations, then, saved, real, key);
                Table without = Rankings.table(wm, nations, then, real, key);
                plays.see(!saved.isEmpty() && withSaved.rows().size() > without.rows().size() && lastPoint.of() == without.rows().size(), () -> key + ": the board with " + saved.size() + " saved cities has " + withSaved.rows().size() + " rows, the point " + lastPoint.of());
            }
        }
        check("...of which the city has a figure in some December for " + lined.size() + " (the rest need the land or the bands, or the city has none)", lined.size() > 15);
        tally("the rank line has one point for each December the city has a figure for, oldest first, and a point for every December where the city has a figure in all of them", months);
        tally("each point is the board's own rank for the city at that December, among as many places (Real world 2025 and the city)", every);
        tally("...a rank from 1 to the places ranked, among the real places that publish the measure (a wage is published for fewer places than output is)", within);
        WorldMeasures.Measure out1 = wm.measure("inflation");
        List<Rankings.Saved> savedNow = f.snapshot.now();
        Table alone = Rankings.table(wm, nations, city, savedNow, real, "inflation");
        check("a measure LC1 took out (inflation, its source " + out1.source() + ") ranks the player's cities alone: the city and its " + savedNow.size()
                        + " saved cities, no real place, and its rank line is the city alone each December",
                !out1.sourced() && !alone.realFigures() && alone.rows().size() == 1 + savedNow.size() && savedNow.size() > 1
                        && alone.rows().stream().allMatch(r -> r.kind() == Rankings.Kind.CITY || r.kind() == Rankings.Kind.SAVED)
                        && Rankings.rankOverYears(wm, city, h, real, "inflation").stream().allMatch(p -> p.rank() == 1 && p.of() == 1));
        tally("Your world's settings give the same points: the line is against Real world 2025 only", board);
        tally("saved cities would change the places ranked (the board with them has more rows), and the point leaves them out", plays);

        check("a measure that needs the land has no point, since the history does not keep it: area and density", Rankings.rankOverYears(wm, city, h, real, "area").isEmpty() && Rankings.rankOverYears(wm, city, h, real, "density").isEmpty());
        String later = null;
        for (String key : lined) {
            List<Rankings.RankPoint> points = Rankings.rankOverYears(wm, city, h, real, key);
            if (!points.isEmpty() && points.get(0).month() > decembers.get(0)) { later = key; break; }
        }
        String startsLater = later;
        check("a measure the city had no figure for in its first Decembers starts when it has one: " + startsLater + ", published, has no point in the first December and some after",
                startsLater != null && Rankings.rankOverYears(wm, city, h, real, startsLater).stream().noneMatch(p -> p.month() == decembers.get(0)));
        Settings constant = set(Board.REAL_2025, Money.CITY, Convert.CONSTANT);
        List<Rankings.RankPoint> constantPoints = Rankings.rankOverYears(wm, city, h, constant, "gdp");
        Rankings.RankPoint lastConstant = constantPoints.isEmpty() ? null : constantPoints.get(constantPoints.size() - 1);
        Table atLast = lastConstant == null ? null : Rankings.table(wm, nations, Rankings.cityAt(city.name(), city.currency(), city.foundingRate(), h, CityMeasures.Extras.UNKNOWN, lastConstant.month()), constant, "gdp");
        check("under Constant the line is the board's Constant rank at each December: the last of " + constantPoints.size() + " points is " + lastConstant,
                lastConstant != null && cityRow(atLast).rank() == lastConstant.rank() && lastConstant.month() == decembers.get(decembers.size() - 1));
        check("a history of nothing, or none, has no points", Rankings.rankOverYears(wm, city, new HistorySave(), real, "people").isEmpty() && Rankings.rankOverYears(wm, city, null, real, "people").isEmpty());
        Rankings.City first = Rankings.cityAt(city.name(), city.currency(), city.foundingRate(), h, CityMeasures.Extras.UNKNOWN, decembers.get(0));
        check("the city at a December is the history's: its rate, parity and price level at that month, its month and year the calendar's",
                first.month() == decembers.get(0) && first.fx() == CityMeasures.valueAt(h, "fxRate", first.month()) && first.usPrices() == CityMeasures.valueAt(h, "usPrices", first.month())
                        && CityCalendar.monthOfYear(first.month()) == 12 && Rankings.cityAt(city.name(), city.currency(), city.foundingRate(), h, CityMeasures.Extras.UNKNOWN, lastMonth(h) + 1) == null);
    }

    /* =====================================================================
       17. THE SLIM HISTORY EQUALS THE FULL ONE
       ===================================================================== */

    static void theSlimHistory(Saves f) throws IOException {
        out.println("--- 17. the slim history equals the full one ---");
        CityMeasures.Extras x = new CityMeasures.Extras(3.5, new double[] {900, 3000, 1500, 9000, 2000, 600}, 0.4);
        Tally figures = new Tally(), months = new Tally(), kept = new Tally(), axis = new Tally();
        for (int slot = 1; slot <= 3; slot++) {
            HistorySave slim = f.snapshot.history(slot), full = fullHistory(f.files, slot);
            int slotNumber = slot;
            axis.see(slim != null && slim.getMonth().equals(full.getMonth()) && slim.yearlyPoints() == full.yearlyPoints() && slim.months() == full.months(), () -> "slot " + slotNumber + "'s axis");
            figures.see(!CityMeasures.of(full, x).isEmpty() && same(CityMeasures.of(slim, x), CityMeasures.of(full, x)), () -> "slot " + slotNumber + "'s measures at its last month");
            for (int m : full.getMonth()) months.see(same(CityMeasures.at(slim, x, m), CityMeasures.at(full, x, m)), () -> "slot " + slotNumber + " at month " + m);
            int loaded = 0, keptSeries = 0;
            boolean inside = true;
            Map<String, List<? extends Number>> fullByName = full.seriesByName(), slimByName = slim.seriesByName();
            for (Map.Entry<String, List<? extends Number>> e : fullByName.entrySet()) {
                if (!e.getValue().isEmpty()) loaded++;
                // a series is kept as the file has it if it is one of the kept, and empty if it is not
                List<? extends Number> mine = slimByName.get(e.getKey());
                inside &= SavedCities.KEPT.contains(e.getKey()) ? e.getValue().equals(mine) : mine == null || mine.isEmpty();
                if (mine != null && !mine.isEmpty()) keptSeries++;
            }
            int fullSeries = loaded, slimSeries = keptSeries;
            boolean ok = inside && slimSeries * 3 < fullSeries && slimSeries > 40;
            kept.see(ok, () -> "slot " + slotNumber + " keeps " + slimSeries + " of " + fullSeries + " series");
        }
        tally("a slot's slim history has the full one's axis", axis);
        tally("CityMeasures.of over it is the full history's, figure for figure", figures);
        tally("...and so is CityMeasures.at of it at every month of the history", months);
        tally("it keeps only the series the measures read and the pivot's three, each as the file has it, under a third of the series the file has", kept);
        HistorySave probe = fullHistory(f.files, 2);
        check("every series CityMeasures reads is a series a history has, and the pivot's fxRate, fxParity and usPrices are kept",
                probe.seriesByName().keySet().containsAll(CityMeasures.SERIES) && SavedCities.KEPT.containsAll(CityMeasures.SERIES) && SavedCities.KEPT.containsAll(List.of("fxRate", "fxParity", "usPrices")));

        // The game dollar's price level, recorded each month at full precision: the history's last entry is the world's level the month ended at.
        Game g = f.game;
        Tally each = new Tally();
        int steps = 15;
        for (int i = 0; i < steps; i++) {
            quietly(() -> g.simulateMonths(1));
            double level = g.getWorldEconomy().getPriceLevel();
            double recorded = g.getHistorySave().alignedTail("usPrices", 1)[0];
            each.see(recorded == level, () -> "month " + g.getMonth() + ": recorded " + recorded + ", the world's level " + level);
        }
        tally("usPrices is the world economy's price level, to the digit, at the end of each month played (" + steps + " months)", each);
        List<? extends Number> series = g.getHistorySave().seriesByName().get("usPrices");
        check("...and it has an entry for every month of the history (" + series.size() + " of " + g.getHistorySave().months() + "), none rounded to four places",
                series.size() == g.getHistorySave().months() && series.stream().anyMatch(v -> v.doubleValue() != Math.round(v.doubleValue() * 10_000.0) / 10_000.0));
        HistorySave before = fullHistory(f.files, 2), after = fullHistory(f.files, 3);
        double[] fxBefore = before.aligned("fxRate"), fxAfter = after.aligned("fxRate");
        boolean scaled = fxBefore.length == fxAfter.length;
        for (int i = 0; i < fxBefore.length && scaled; i++) scaled = relative(fxAfter[i] * LB3_FACTOR, fxBefore[i]);
        check("the reform divides the rate's whole history by " + LB3_FACTOR + " and leaves usPrices alone: slot 3's series is slot 2's, to the digit",
                scaled && Arrays.equals(before.aligned("usPrices"), after.aligned("usPrices")) && !Double.isNaN(before.aligned("usPrices")[0]));
    }

    /* =====================================================================
       18. CITYMEASURES.AT
       ===================================================================== */

    static void theMonth(Rankings.City live, Saves f) {
        out.println("--- 18. CityMeasures.at ---");
        HistorySave h = f.game.getHistorySave();
        CityMeasures.Extras x = f.youngExtras;
        int last = lastMonth(h);
        check("at the history's last month it is of(), figure for figure (" + CityMeasures.of(h, x).size() + " measures)", !CityMeasures.of(h, x).isEmpty() && same(CityMeasures.at(h, x, last), CityMeasures.of(h, x)));
        check("at the first month it was asked (" + f.youngLast + "), after the city played on " + (last - f.youngLast) + " months, it is what of() said then: the live city's figures",
                same(f.youngReplayed, live.figures()) && f.youngReplayed.size() > 30);
        check("a month the history never reached has no figures, nor month 0 or a negative one, nor a null history",
                CityMeasures.at(h, x, last + 1).isEmpty() && CityMeasures.at(h, x, 0).isEmpty() && CityMeasures.at(h, x, -5).isEmpty() && CityMeasures.at(null, x, last).isEmpty());
        int first = h.getMonth().get(0);
        check("a younger month has fewer measures than an older: inflation needs 13 months, so none in the history's twelfth month (" + (first + 11) + ") and one in its thirteenth",
                !CityMeasures.at(h, x, first + 11).containsKey("inflation") && CityMeasures.at(h, x, first + 12).containsKey("inflation") && CityMeasures.at(h, x, first + 11).containsKey("gdp"));

        Map<String, CityMeasures.Figure> unknown = CityMeasures.at(h, CityMeasures.Extras.UNKNOWN, last), known = CityMeasures.at(h, x, last);
        Set<String> needLand = Set.of("area", "density", "old_age_dep", "life_exp", "orphans");
        boolean others = true;
        for (String key : known.keySet()) if (!needLand.contains(key) && !key.equals("min_wage") && !key.equals("kaitz")) others &= unknown.containsKey(key) && unknown.get(key).equals(known.get(key));
        check("Extras.UNKNOWN leaves out exactly what needs the land and the bands (" + needLand + "): the other measures are the same",
                needLand.stream().noneMatch(unknown::containsKey) && needLand.stream().allMatch(known::containsKey) && others && unknown.containsKey("gdp"));
        double floor = h.aligned("minimumWage")[h.months() - 1] * h.aligned("priceIndex")[h.months() - 1] * CityMeasures.YEAR * CityMeasures.THOUSANDS;
        check("...and its minimum wage is the history's floor lifted by the price index, times 12 and THOUSANDS (" + unknown.get("min_wage").value() + ")", relative(unknown.get("min_wage").value(), floor));

        // A history with folded years: only the entries that are months can be read at.
        int total = HistorySave.MONTHLY_KEPT + 3 * 12 + 5;
        HistorySave folded = new HistorySave();
        for (int m = 1; m <= total; m++) folded.appendMonth(m, name -> constantSeries(name));
        CityMeasures.Extras bands = new CityMeasures.Extras(4, new double[] {100, 100, 100, 100, 100, 100});
        check("fixture: three years of the history are folded (" + folded.yearlyPoints() + ") and the first month it holds as a month is " + (folded.yearlyPoints() * 12 + 1), folded.yearlyPoints() == 3);
        check("a month in a folded year has no figures (months 12 and 36), nor does the month before the fold ends", CityMeasures.at(folded, bands, 12).isEmpty() && CityMeasures.at(folded, bands, 36).isEmpty() && CityMeasures.at(folded, bands, 1).isEmpty());
        check("the first month after the fold is read as a month: a year of output scaled from the one month it has, 12 times the month",
                CityMeasures.at(folded, bands, 37).containsKey("gdp") && relative(CityMeasures.at(folded, bands, 37).get("gdp").value(), 100 * 12 * CityMeasures.THOUSANDS));
        check("a month in the folded history's middle has the figures of that month, a year of output 12 of the month; its last month is of()",
                relative(CityMeasures.at(folded, bands, total - 24).get("gdp").value(), 100 * 12 * CityMeasures.THOUSANDS) && same(CityMeasures.at(folded, bands, total), CityMeasures.of(folded, bands)));
        check("valueAt reads a series at a month and is NaN where the history has no such month or series",
                CityMeasures.valueAt(folded, "gdp", total - 5) == 100 && Double.isNaN(CityMeasures.valueAt(folded, "gdp", 12)) && Double.isNaN(CityMeasures.valueAt(folded, "noSuchSeries", total - 5)) && Double.isNaN(CityMeasures.valueAt(null, "gdp", 5)));
    }
}
