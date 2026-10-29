package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The nations beyond the city and the path they are on (batch WN1; the
 * project's spec-nations.md 7): Nations' year - output, people, prices and the
 * rate of the nation's money - as a pure function of (path, seed, year), the
 * file's real years on Historical, and the draws of the rule held to the dials
 * they are drawn from.
 *
 * WHY. The world outside was one price list and the dollar's draws were seeded
 * by the month alone, so every city met one world (spec-nations 1.4). A path
 * that is drawn gets four ways to be wrong that no city would show: a seed
 * that does not decide it (two runs of one world differ), a Historical that is
 * not the file, shocks whose size is not the dial's (so the economy WN3 builds
 * on them is calmer or wilder than the sources say), and a nation whose output
 * can go negative a century in. The statistics are counted over seeded years,
 * and each says against which constant it is judged.
 *
 * THE DATA. On the fixture (WorldData.FIXTURE, twelve nations) always; the full
 * world-1.bin where it is named (-Dcbs.world=<path>, WorldData.locate()), for
 * the sections that read all 196 nations, and a line says when it is not.
 *
 * What this has to prove:
 *   1. ONE SEED, ONE PATH: two Nations of one path and seed agree on every
 *      component of every year, 2000-2150; one asked 2150 first agrees with one
 *      asked in order; another seed differs; Historical on two seeds agrees to
 *      2025 and differs after.
 *   2. HISTORICAL EQUALS THE FILE TO 2025: output, people, price level, rate and
 *      each good's price, with their marks; Random starts from the file's 2000;
 *      people are the file's to 2100 on both paths.
 *   3. RANDOM'S SWINGS MATCH ITS DIALS over seeded years: the world factor, a
 *      region's, the own shock's persistence, the frontier's swing, a poorer
 *      nation's larger, crises at their rate and depth, the trend inside its
 *      dials, inflation above its floor.
 *   3b. GOODS: each good's price swing and persistence match their dials.
 *   4. HISTORICAL'S FUTURE SWINGS ARE THE SMALLER by HISTORICAL_SHOCK_SHARE, and
 *      it draws no crisis.
 *   5. PEGS HOLD: a peg keeps its ratio, a use is the other money, the euro's
 *      users share one rate; on the full file every peg:USD nation holds.
 *   6. OUTPUT NEVER NEGATIVE: output, people, prices and rates finite and above
 *      0 on both paths, 20 seeds, to 2300.
 *   7. AFTER 2100: people held; Historical's growth has eased to the long run.
 *   8. MISSING FIGURES: a series with none, with a gap or with an early end is
 *      filled and ruled as FIGURES say (Nations.Absent causes them).
 *
 * Every fixture causes its condition: a nation of the file is pegged by the
 * regimes it is given, a figure is missing because the harness says it is, and
 * the statistics are counted over enough seeded years that a wrong dial would
 * show.
 */
public class NationsCheck {

    static int fails = 0;
    /** Where the harness prints: the console. */
    static final PrintStream out = System.out;

    /** The seed the single-world sections use, and the one the others start from. */
    static final long BASE_SEED = 7_001;

    /** The seeds the statistics are counted over, and the years each (2001-2100). */
    static final int SEEDS = 20;

    /** The goods' statistics are counted over this many seeds of 100 years (a lag-1 autocorrelation's error is about 1 over the root of the count). */
    static final int GOOD_SEEDS = 30;

    /** The years a good's price needs to settle from the file's 2000 ratio to its mean before it is counted: 0.7 to the 30th is 2e-5. */
    static final int GOOD_BURN_IN = 30;

    /** A swing's standard deviation is within this share of its dial (the world's, a region's, the frontier's, a good's). */
    static final double SD_TOL = .10, FRONTIER_SD_TOL = .12, GOOD_SD_TOL = .12, SHARE_TOL = .15;

    /** An autocorrelation is within this of its dial (error of a lag-1 estimate over 2,000 years is about .02). */
    static final double LAG_TOL = .06;

    /** The crisis rate among the years a crisis could start is within this of CRISIS_RATE. */
    static final double RATE_TOL = .004;

    /** The Historical frontier's growth a head, 2160-2200 and over twelve seeds, is within this of LONG_RUN_GROWTH: tight, because FRONTIER_GROWTH is only .005 above the long run, so an ease that did not happen would pass a wider one. */
    static final double LONG_RUN_TOL = .003;

    /** A rate held, or a ratio kept, is the same within this share. */
    static final double HELD_TOL = 1e-12;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-120s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. */
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

    /** A sample's count, sum and sum of squares: its standard deviation. */
    static final class Moments {
        int n;
        double sum, sumSq;

        void add(double x) {
            n++;
            sum += x;
            sumSq += x * x;
        }

        double sd() { return Math.sqrt(Math.max(0, (sumSq - sum * sum / n) / (n - 1))); }
    }

    static boolean near(double a, double b, double rel) { return Math.abs(a - b) <= rel * Math.abs(b); }

    /** A measured figure judged against the constant it is drawn from, within a share of it (share) or an amount (absolute). */
    static void within(String label, double measured, double dial, double tol, boolean share) {
        boolean ok = Math.abs(measured - dial) <= (share ? tol * Math.abs(dial) : tol);
        assertTrue(String.format("%s: %.5f against the dial's %.5f (within %s)", label, measured, dial, share ? (int) Math.round(tol * 100) + "%" : String.valueOf(tol)), ok);
    }

    static Nations of(WorldData data, Nations.Path path, long seed) { return new Nations(data, path, seed); }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        byte[] bytes = WorldDataCheck.resource(WorldData.FIXTURE);
        if (bytes == null) {
            assertTrue("no fixture: " + WorldData.FIXTURE + " is not beside the classes", false);
            System.out.println("\n" + fails + " FAILED");
            System.exit(1);
        }
        WorldData fixture = WorldData.of(bytes);
        theSeed("fixture", fixture);
        theFile("fixture", fixture);
        theSwings(fixture);
        theGoods(fixture);
        theHistoricalFuture(fixture);
        thePegs(fixture);
        theNeverNegative("fixture", fixture, SEEDS);
        theAfter2100(fixture);
        theMissingFigures(fixture);

        out.println("--- the full file, when it is named ---");
        Path fullPath = WorldData.locate();
        String named = System.getProperty(WorldData.PROPERTY);
        if (named != null && !named.isBlank() && !Files.isRegularFile(fullPath)) {
            assertTrue("the file -D" + WorldData.PROPERTY + " names is there (" + named + ")", false);
        } else if (fullPath == null) {
            out.println("world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": its sections did not run");
        } else {
            out.println("reading " + fullPath.toAbsolutePath());
            try (WorldData full = WorldData.open(fullPath)) {
                Nations all = of(full, Nations.Path.RANDOM, BASE_SEED);
                Tally names = new Tally();
                for (int i = 1; i <= full.nationCount(); i++) names.see(all.iso3(i).equals(full.nation(i).iso3()) && all.name(i).equals(full.nation(i).name()) && all.index(full.nation(i).iso3()) == i, () -> "nation " + all.iso3(1));
                assertTrue("the full file holds " + WorldDataCheck.FULL_NATIONS + " nations, and Nations numbers and names them as WorldData does", all.count() == WorldDataCheck.FULL_NATIONS && names.bad == 0 && names.n == all.count());
                theSeed("full", full);
                theFile("full", full);
                theFullPegs(full);
                theFullCounts(full);
                theNeverNegative("full", full, 2);
            }
        }

        out.printf("%nNationsCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** Whether every component of a nation's years agree between two paths, 2000 to last, and the goods' prices. */
    static void compare(Tally t, Nations a, Nations b, int nation, int last, String what) {
        for (int y = Nations.FIRST_YEAR; y <= last; y++) {
            int yy = y;
            t.see(a.year(nation, y).equals(b.year(nation, y)) && a.draws(nation, y).equals(b.draws(nation, y)), () -> what + " " + a.iso3(nation) + " " + yy);
        }
        for (int g = 1; nation == 1 && g <= a.goodCount(); g++) {
            int gg = g;
            for (int y = Nations.FIRST_YEAR; y <= last; y++) {
                int yy = y;
                t.see(a.goodPrice(g, y) == b.goodPrice(g, y) && a.goodPriceMark(g, y) == b.goodPriceMark(g, y), () -> what + " good " + gg + " " + yy);
            }
        }
    }

    /* =====================================================================
       1. ONE SEED, ONE PATH
       ===================================================================== */

    static void theSeed(String scope, WorldData data) {
        out.println("--- one seed, one path (" + scope + ") ---");
        Nations a = of(data, Nations.Path.RANDOM, BASE_SEED), b = of(data, Nations.Path.RANDOM, BASE_SEED), jump = of(data, Nations.Path.RANDOM, BASE_SEED);
        Nations other = of(data, Nations.Path.RANDOM, BASE_SEED + 1);
        Tally same = new Tally(), order = new Tally();
        for (int i = 1; i <= a.count(); i++) {
            for (int y = Nations.FIRST_YEAR; y <= 2150; y++) { a.year(i, y); b.year(i, y); }   // in order, a year at a time
            compare(same, a, b, i, 2150, "Random");
        }
        jump.year(1, 2150);   // asked 2150 before any year past 2100, and so before 2030's neighbours were asked in order
        for (int i = 1; i <= a.count(); i++) compare(order, a, jump, i, 2150, "asked 2150 first");
        tally("two Nations of one Random path and seed agree on every component of every nation's year, 2000-2150", same);
        tally("one asked for 2150 first agrees with one asked in order, to the last component", order);
        boolean differs = false;
        for (int i = 1; i <= a.count(); i++) differs |= a.year(i, 2030).output() != other.year(i, 2030).output();
        assertTrue("another seed gives another world: some nation's output in 2030 differs", differs);
        Nations h1 = of(data, Nations.Path.HISTORICAL, BASE_SEED), h2 = of(data, Nations.Path.HISTORICAL, BASE_SEED + 1);
        Tally agree = new Tally();
        boolean after = false;
        for (int i = 1; i <= h1.count(); i++) {
            compare(agree, h1, h2, i, Nations.HISTORY_LAST_YEAR, "Historical");
            after |= h1.year(i, 2030).output() != h2.year(i, 2030).output();
        }
        tally("Historical on two seeds agrees on every component to " + Nations.HISTORY_LAST_YEAR, agree);
        assertTrue("Historical on two seeds differs after " + Nations.HISTORY_LAST_YEAR + ": some nation's output in 2030", after);
    }

    /* =====================================================================
       2. HISTORICAL EQUALS THE FILE TO 2025
       ===================================================================== */

    static void theFile(String scope, WorldData data) {
        out.println("--- Historical equals the file to " + Nations.HISTORY_LAST_YEAR + " (" + scope + ") ---");
        Nations h = of(data, Nations.Path.HISTORICAL, BASE_SEED), r = of(data, Nations.Path.RANDOM, BASE_SEED);
        Tally outputs = new Tally(), people = new Tally(), prices = new Tally(), rates = new Tally(), start = new Tally(), money = new Tally(), goods = new Tally();
        for (int i = 1; i <= data.nationCount(); i++) {
            String iso = data.nation(i).iso3();
            for (int y = Nations.FIRST_YEAR; y <= Nations.HISTORY_LAST_YEAR; y++) {
                int yy = y;
                Nations.Year now = h.year(i, y);
                outputs.see(now.output() == data.pathValue("gdp_2015usd", i, y) && now.outputMark() == data.pathMark("gdp_2015usd", i, y), () -> iso + " " + yy);
                prices.see(now.priceLevel() == data.pathValue("cpi", i, y), () -> iso + " " + yy);
                rates.see(now.rateToUsd() == data.pathValue("rate", i, y), () -> iso + " " + yy);
                if (y > Nations.FIRST_YEAR) prices.see(near(now.inflation(), now.priceLevel() / h.year(i, y - 1).priceLevel() - 1, 1e-12), () -> iso + " " + yy + "'s inflation");
            }
            for (int y = Nations.FIRST_YEAR; y <= Nations.PEOPLE_LAST_YEAR; y++) {
                int yy = y;
                for (Nations p : new Nations[] {h, r})
                    people.see(p.year(i, y).people() == data.pathValue("people", i, y) && p.year(i, y).peopleMark() == data.pathMark("people", i, y), () -> iso + " " + yy + " on " + p.path());
            }
            Nations.Year first = r.year(i, Nations.FIRST_YEAR);
            start.see(first.output() == data.pathValue("gdp_2015usd", i, Nations.FIRST_YEAR) && first.priceLevel() == data.pathValue("cpi", i, Nations.FIRST_YEAR)
                    && first.rateToUsd() == data.pathValue("rate", i, Nations.FIRST_YEAR) && Double.isNaN(first.inflation()), () -> iso);
            money.see(h.year(i, Nations.HISTORY_LAST_YEAR + 5).money().equals(data.nation(i).money()), () -> iso);
        }
        for (int g = 1; g <= data.goodCount(); g++) {
            for (int y = Nations.FIRST_YEAR; y <= Nations.HISTORY_LAST_YEAR; y++) {
                int gg = g, yy = y;
                goods.see(h.goodPrice(g, y) == data.price(g, y) && h.goodPriceMark(g, y) == data.priceMark(g, y), () -> "good " + gg + " " + yy);
            }
            int gg = g;
            goods.see(r.goodPrice(g, Nations.FIRST_YEAR) == data.price(g, Nations.FIRST_YEAR) && h.goodKey(g).equals(data.good(g).key()), () -> "good " + gg + " in 2000 on Random");
        }
        tally("every nation's output and its mark equal the file's, 2000-2025", outputs);
        tally("every nation's price level equals the file's cpi, and its inflation is the change of it", prices);
        tally("every nation's rate equals the file's, 2000-2025", rates);
        tally("every nation's people and their mark equal the file's to 2100, on Historical and on Random", people);
        tally("Random starts from the file's January 2000: output, price level and rate are the file's, inflation is NaN", start);
        tally("a Historical nation's money after the history is the file's money of 2025", money);
        tally("every good's price and its mark equal the file's 2000-2025 on Historical, and 2000's on Random", goods);
    }

    /* =====================================================================
       3. RANDOM'S SWINGS MATCH ITS DIALS
       ===================================================================== */

    static void theSwings(WorldData data) {
        out.println("--- Random's swings match its dials, over " + SEEDS + " seeds of 100 years ---");
        Nations probe = of(data, Nations.Path.RANDOM, BASE_SEED);
        int count = probe.count(), usa = probe.index("USA"), col = probe.index("COL"), chn = probe.index("CHN");
        Moments world = new Moments(), frontier = new Moments(), colSwing = new Moments(), chnSwing = new Moments(), usaInflation = new Moments(), usaEarly = new Moments(), chnEarly = new Moments();
        Map<String, Moments> regions = new LinkedHashMap<>();
        Tally shared = new Tally(), spacing = new Tally(), depth = new Tally(), trend = new Tally(), floor = new Tally();
        double lag = 0, lagBase = 0;
        int starts = 0, eligible = 0, nationYears = 0, fell = 0;
        for (int k = 0; k < SEEDS; k++) {
            Nations r = of(data, Nations.Path.RANDOM, BASE_SEED + 977L * k);
            int[] running = new int[count + 1];
            double[] amount = new double[count + 1];
            for (int y = 2001; y <= 2100; y++) {
                double w = r.draws(1, y).world();
                world.add(w);
                Map<String, Double> regionTerm = new HashMap<>();
                for (int i = 1; i <= count; i++) {
                    Nations.Draws d = r.draws(i, y);
                    int ii = i, yy = y;
                    shared.see(d.world() == w, () -> "the world factor of nation " + ii + " in " + yy);
                    Double seen = regionTerm.putIfAbsent(r.region(i), d.region());
                    if (seen == null) regions.computeIfAbsent(r.region(i), x -> new Moments()).add(d.region());
                    else shared.see(seen == d.region(), () -> "the region factor of nation " + ii + " in " + yy);
                    if (y > 2001) {
                        double before = r.draws(i, y - 1).own();
                        lag += d.own() * before;
                        lagBase += before * before;
                    }
                    if (d.crisisStarts()) {
                        spacing.see(running[i] == 0, () -> "a crisis began in nation " + ii + " in " + yy + " while one ran");
                        starts++;
                        eligible++;
                        double fall = 1 - Math.exp(d.crisis() * Nations.CRISIS_YEARS);
                        depth.see(fall >= Nations.CRISIS_DEPTH_MIN - 1e-9 && fall <= Nations.CRISIS_DEPTH_MAX + 1e-9, () -> "a crisis of " + fall);
                        running[i] = Nations.CRISIS_YEARS - 1;
                        amount[i] = d.crisis();
                    } else if (running[i] > 0) {
                        spacing.see(d.crisis() == amount[i], () -> "nation " + ii + " in " + yy + " is not in the crisis it began");
                        running[i]--;
                    } else {
                        eligible++;
                        spacing.see(d.crisis() == 0, () -> "nation " + ii + " in " + yy + " is in a crisis nothing began");
                    }
                    Nations.Year now = r.year(i, y), before = r.year(i, y - 1);
                    double perHead = Math.log(now.outputPerHead() / before.outputPerHead());
                    double trendNow = perHead - (d.world() + d.region() + d.own() + d.crisis());
                    trend.see(trendNow >= Nations.FRONTIER_GROWTH - 1e-9 && trendNow <= Nations.FRONTIER_GROWTH + Nations.CATCH_UP_MAX + 1e-9, () -> "nation " + ii + " in " + yy + " has a trend of " + trendNow);
                    floor.see(now.inflation() >= Nations.INFLATION_FLOOR - 1e-12, () -> "nation " + ii + " in " + yy + " has inflation " + now.inflation());
                    nationYears++;
                    if (now.output() / before.output() - 1 < -.05) fell++;
                    if (d.crisis() == 0) {
                        double swing = d.world() + d.region() + d.own();
                        if (i == usa) frontier.add(perHead - Nations.FRONTIER_GROWTH);
                        if (i == col) colSwing.add(swing);
                        if (i == chn) chnSwing.add(swing);
                    }
                    if (i == usa) usaInflation.add(now.inflation());
                    if (y <= 2008 && i == usa) usaEarly.add(now.inflation());
                    if (y <= 2008 && i == chn) chnEarly.add(now.inflation());
                }
                int yearNow = y;
                shared.see(new HashSet<>(regionTerm.values()).size() == regionTerm.size(), () -> "two regions drew the same factor in " + yearNow);
            }
        }
        within("the world factor's standard deviation (" + world.n + " years)", world.sd(), Nations.WORLD_SD, SD_TOL, true);
        for (Map.Entry<String, Moments> e : regions.entrySet())
            within("the " + e.getKey() + " factor's standard deviation (" + e.getValue().n + " years)", e.getValue().sd(), Nations.REGION_SD, SD_TOL, true);
        tally("the world factor is every nation's, a region's factor is every nation of the region's, and no two regions draw alike, in every year", shared);
        within("the own shock's lag-1 autocorrelation", lag / lagBase, Nations.OWN_PERSISTENCE, LAG_TOL, false);
        within("the frontier's yearly growth a head less its trend, crisis years left out, standard deviation (" + frontier.n + " years)", frontier.sd(), Nations.SD_RICH, FRONTIER_SD_TOL, true);
        assertTrue(String.format("a poorer nation swings more than the frontier: Colombia %.4f and China %.4f against the frontier's %.4f", colSwing.sd(), chnSwing.sd(), frontier.sd()),
                colSwing.sd() > frontier.sd() && chnSwing.sd() > frontier.sd());
        within("crises start among the years not in one (" + starts + " in " + eligible + ")", starts / (double) eligible, Nations.CRISIS_RATE, RATE_TOL, false);
        tally("every crisis is between the depth dials", depth);
        tally("a crisis runs CRISIS_YEARS at its size, and none begins while one runs", spacing);
        tally("the trend is between FRONTIER_GROWTH and FRONTIER_GROWTH + CATCH_UP_MAX in every nation-year", trend);
        tally("inflation is never below INFLATION_FLOOR", floor);
        within("the frontier's mean inflation (the rich group's)", usaInflation.sum / usaInflation.n, Nations.INFLATION_MEAN[0], .004, false);
        assertTrue(String.format("a poorer nation's inflation is higher while it is poor: China's mean 2001-2008 is %.4f against the frontier's %.4f (the groups' means differ by %.3f)", chnEarly.sum / chnEarly.n, usaEarly.sum / usaEarly.n, Nations.INFLATION_MEAN[2] - Nations.INFLATION_MEAN[0]),
                chnEarly.sum / chnEarly.n - usaEarly.sum / usaEarly.n > (Nations.INFLATION_MEAN[2] - Nations.INFLATION_MEAN[0]) / 2);
        assertTrue("the income groups are the thresholds': below LOW_INCOME_MAX low, up to LOWER_MIDDLE_MAX lower middle, up to UPPER_MIDDLE_MAX upper middle, above high",
                Nations.incomeGroup(Nations.LOW_INCOME_MAX - 1) == 3 && Nations.incomeGroup(Nations.LOW_INCOME_MAX) == 2 && Nations.incomeGroup(Nations.LOWER_MIDDLE_MAX) == 1 && Nations.incomeGroup(Nations.UPPER_MIDDLE_MAX) == 0);
        out.printf("counted: %d nation-years, %d crises begun, %.1f%% of nation-years whose output fell more than 5%% (the spec: 1 in 20, all nations; these twelve are mostly rich)%n",
                nationYears, starts, 100.0 * fell / nationYears);
    }

    /* =====================================================================
       3b. GOODS
       ===================================================================== */

    static void theGoods(WorldData data) {
        out.println("--- each good's price swing and persistence match their dials, over " + GOOD_SEEDS + " seeds of 100 years ---");
        Nations probe = of(data, Nations.Path.RANDOM, BASE_SEED);
        int goods = probe.goodCount();
        Moments[] change = new Moments[goods + 1];
        double[] lag = new double[goods + 1], lagBase = new double[goods + 1];
        for (int g = 1; g <= goods; g++) change[g] = new Moments();
        Tally finite = new Tally();
        for (int k = 0; k < GOOD_SEEDS; k++) {
            Nations r = of(data, Nations.Path.RANDOM, BASE_SEED + 313L * k);
            for (int g = 1; g <= goods; g++) {
                int gg = g;
                for (int y = Nations.FIRST_YEAR + GOOD_BURN_IN; y <= Nations.FIRST_YEAR + GOOD_BURN_IN + 100; y++) {
                    double p = r.goodPrice(g, y), before = r.goodPrice(g, y - 1);
                    int yy = y;
                    finite.see(Double.isFinite(p) && p > 0, () -> "good " + gg + " in " + yy);
                    if (y > Nations.FIRST_YEAR + GOOD_BURN_IN) {
                        change[g].add(Math.log(p / before));
                        lag[g] += Math.log(p) * Math.log(before);
                        lagBase[g] += Math.log(before) * Math.log(before);
                    }
                }
            }
        }
        Tally swing = new Tally(), persistence = new Tally();
        for (int g = 1; g <= goods; g++) {
            int gg = g;
            double dial = probe.priceSwing(g);
            swing.see(Math.abs(change[g].sd() - dial) <= GOOD_SD_TOL * dial, () -> probe.goodKey(gg) + String.format(" swings %.4f against %.4f", change[gg].sd(), dial));
            persistence.see(Math.abs(lag[g] / lagBase[g] - Nations.PRICE_PERSISTENCE) <= LAG_TOL, () -> probe.goodKey(gg) + String.format(" persists %.3f", lag[gg] / lagBase[gg]));
            if (dial < Nations.MIN_PRICE_SWING) swing.see(false, () -> probe.goodKey(gg) + " swings under MIN_PRICE_SWING");
        }
        tally("each good's yearly log change has a standard deviation within " + (int) (GOOD_SD_TOL * 100) + "% of its priceSwing()", swing);
        tally("each good's ln price has a lag-1 autocorrelation within " + LAG_TOL + " of PRICE_PERSISTENCE", persistence);
        tally("every price is finite and above 0", finite);
    }

    /* =====================================================================
       4. HISTORICAL'S FUTURE SWINGS ARE THE SMALLER
       ===================================================================== */

    static void theHistoricalFuture(WorldData data) {
        out.println("--- Historical's future swings are the smaller, and it draws no crisis ---");
        Nations probe = of(data, Nations.Path.HISTORICAL, BASE_SEED);
        int usa = probe.index("USA");
        Moments hist = new Moments(), rand = new Moments();
        Tally crises = new Tally(), quiet = new Tally();
        boolean drawn = false;
        for (int k = 0; k < SEEDS; k++) {
            Nations h = of(data, Nations.Path.HISTORICAL, BASE_SEED + 977L * k), r = of(data, Nations.Path.RANDOM, BASE_SEED + 977L * k);
            for (int y = Nations.HISTORY_LAST_YEAR + 1; y <= 2100; y++) {
                hist.add(Math.log(h.year(usa, y).outputPerHead() / h.year(usa, y - 1).outputPerHead()) - Nations.FRONTIER_GROWTH);
                if (!r.inCrisis(usa, y)) rand.add(Math.log(r.year(usa, y).outputPerHead() / r.year(usa, y - 1).outputPerHead()) - Nations.FRONTIER_GROWTH);
            }
            if (k < 5) {
                for (int i = 1; i <= h.count(); i++) {
                    for (int y = Nations.FIRST_YEAR; y <= 2150; y++) {
                        int ii = i, yy = y;
                        Nations.Draws d = h.draws(i, y);
                        crises.see(d.crisis() == 0 && !d.crisisStarts() && !h.inCrisis(i, y), () -> h.iso3(ii) + " " + yy);
                        if (y <= Nations.HISTORY_LAST_YEAR) quiet.see(d.world() == 0 && d.region() == 0 && d.own() == 0, () -> h.iso3(ii) + " " + yy);
                        else drawn |= d.world() != 0;
                    }
                }
            }
        }
        double ratio = hist.sd() / rand.sd();
        within(String.format("the frontier's Historical swing (%.4f) over Random's (%.4f), 2026-2100, crisis years left out", hist.sd(), rand.sd()), ratio, Nations.HISTORICAL_SHOCK_SHARE, SHARE_TOL, true);
        tally("Historical draws no crisis, in any nation, 2000-2150", crises);
        tally("Historical draws nothing in the years it replays the file", quiet);
        assertTrue("Historical draws a world factor in the years after the file's", drawn);
    }

    /* =====================================================================
       5. PEGS HOLD
       ===================================================================== */

    static void thePegs(WorldData data) {
        out.println("--- pegs hold ---");
        Map<String, String> regimes = Map.of("CHE", "peg:USD", "COL", "peg:EUR", "AUS", "uses:USD");
        Nations h = new Nations(data, Nations.Path.HISTORICAL, BASE_SEED, regimes), r = new Nations(data, Nations.Path.RANDOM, BASE_SEED, regimes);
        int che = h.index("CHE"), col = h.index("COL"), aus = h.index("AUS"), deu = h.index("DEU");
        Tally hPeg = new Tally(), rPeg = new Tally(), hRatio = new Tally(), rRatio = new Tally(), uses = new Tally(), hEuro = new Tally(), rEuro = new Tally();
        double ratio25 = h.year(col, Nations.HISTORY_LAST_YEAR).rateToUsd() / h.year(deu, Nations.HISTORY_LAST_YEAR).rateToUsd();
        double ratio00 = r.year(col, Nations.FIRST_YEAR).rateToUsd() / r.year(deu, Nations.FIRST_YEAR).rateToUsd();
        boolean euroMoves = false;
        for (int y = 2001; y <= 2150; y++) {
            int yy = y;
            if (y > Nations.HISTORY_LAST_YEAR) {
                hPeg.see(near(h.year(che, y).rateToUsd(), h.year(che, Nations.HISTORY_LAST_YEAR).rateToUsd(), HELD_TOL), () -> "CHF in " + yy);
                hRatio.see(near(h.year(col, y).rateToUsd() / h.year(deu, y).rateToUsd(), ratio25, HELD_TOL), () -> "COP in " + yy);
                euroMoves |= !near(h.year(deu, y).rateToUsd(), h.year(deu, Nations.HISTORY_LAST_YEAR).rateToUsd(), 1e-6);
            }
            rPeg.see(near(r.year(che, y).rateToUsd(), r.year(che, Nations.FIRST_YEAR).rateToUsd(), HELD_TOL), () -> "CHF in " + yy);
            rRatio.see(near(r.year(col, y).rateToUsd() / r.year(deu, y).rateToUsd(), ratio00, HELD_TOL), () -> "COP in " + yy);
            uses.see(r.year(aus, y).rateToUsd() == 1 && r.year(aus, y).money().equals(Nations.DOLLAR), () -> "AUS in " + yy);
            for (String iso : new String[] {"BEL", "FRA", "NLD"}) {
                int n = h.index(iso);
                if (y > Nations.HISTORY_LAST_YEAR) hEuro.see(h.year(n, y).rateToUsd() == h.year(deu, y).rateToUsd() && h.year(n, y).money().equals("EUR"), () -> iso + " in " + yy);
                rEuro.see(r.year(r.index(iso), y).rateToUsd() == r.year(deu, y).rateToUsd() && r.year(r.index(iso), y).money().equals("EUR"), () -> iso + " in " + yy);
            }
        }
        tally("a nation pegged to the dollar (CHF) holds its last published rate every year to 2150 on Historical", hPeg);
        tally("...and its rate of 2000 on Random", rPeg);
        tally("a nation pegged to the euro (COP) holds its ratio to the euro's rate of 2025 on Historical, to 2150", hRatio);
        tally("...and its ratio of 2000 on Random", rRatio);
        assertTrue("the euro's own rate moves after 2025 on Historical, so the pegs above held against something moving", euroMoves);
        tally("a nation that uses the dollar has rate 1 in the dollar from 2001 on Random", uses);
        tally("the euro's users share one rate every year after 2025 on Historical", hEuro);
        tally("...and every year after 2000 on Random", rEuro);
    }

    static void theFullPegs(WorldData full) {
        out.println("--- every peg:USD nation holds after 2025 (full) ---");
        Nations h = of(full, Nations.Path.HISTORICAL, BASE_SEED);
        Tally hold = new Tally();
        for (int i = 1; i <= full.nationCount(); i++) {
            if (!"peg:USD".equals(full.nation(i).regime())) continue;
            for (int y = Nations.HISTORY_LAST_YEAR + 1; y <= 2150; y += 5) {
                int ii = i, yy = y;
                hold.see(near(h.year(i, y).rateToUsd(), h.year(i, Nations.HISTORY_LAST_YEAR).rateToUsd(), 1e-9), () -> h.iso3(ii) + " in " + yy);
            }
        }
        tally("each of the file's peg:USD nations keeps its rate of 2025 every fifth year to 2150", hold);
    }

    /* =====================================================================
       6. OUTPUT NEVER NEGATIVE
       ===================================================================== */

    static void theNeverNegative(String scope, WorldData data, int seeds) {
        out.println("--- output, people, prices and rates stay finite and above 0 (" + scope + ", " + seeds + " seeds, to 2300) ---");
        for (Nations.Path path : Nations.Path.values()) {
            Tally t = new Tally();
            for (int k = 0; k < seeds; k++) {
                Nations n = of(data, path, BASE_SEED + 131L * k);
                for (int i = 1; i <= n.count(); i++) {
                    for (int y = Nations.FIRST_YEAR; y <= 2300; y++) {
                        Nations.Year v = n.year(i, y);
                        int ii = i, yy = y;
                        boolean ok = ok(v.output()) && ok(v.people()) && ok(v.priceLevel()) && ok(v.rateToUsd()) && (y == Nations.FIRST_YEAR || Double.isFinite(v.inflation()));
                        t.see(ok, () -> n.iso3(ii) + " " + yy + " " + v);
                    }
                }
                for (int g = 1; g <= n.goodCount(); g++)
                    for (int y = Nations.FIRST_YEAR; y <= 2300; y += 10) {
                        int gg = g, yy = y;
                        t.see(ok(n.goodPrice(g, y)), () -> "good " + gg + " " + yy);
                    }
            }
            tally("on " + path + " every nation's year and every good's price is finite and above 0", t);
        }
    }

    static boolean ok(double x) { return Double.isFinite(x) && x > 0; }

    /* =====================================================================
       7. AFTER 2100
       ===================================================================== */

    static void theAfter2100(WorldData data) {
        out.println("--- after 2100 ---");
        Tally held = new Tally();
        double mean = 0;
        boolean continues = false;
        for (Nations.Path path : Nations.Path.values()) {
            for (int k = 0; k < 12; k++) {
                Nations n = of(data, path, BASE_SEED + 53L * k);
                for (int i = 1; i <= n.count(); i++) {
                    for (int y = 2101; y <= 2200; y++) {
                        int ii = i, yy = y;
                        held.see(n.year(i, y).people() == n.year(i, 2100).people() && n.year(i, y).peopleMark() == WorldData.MARK_ESTIMATE, () -> n.iso3(ii) + " " + yy + " on " + n.path());
                    }
                }
                int usa = n.index("USA");
                if (path == Nations.Path.HISTORICAL) {
                    double sum = 0;
                    for (int y = 2160; y <= 2200; y++) sum += Math.log(n.year(usa, y).outputPerHead() / n.year(usa, y - 1).outputPerHead());
                    mean += sum / 41 / 12;
                } else {
                    for (int y = 2101; y <= 2150; y++) continues |= n.draws(usa, y).world() != 0;
                }
            }
        }
        tally("people are held at 2100's, marked 1, on both paths", held);
        within("Historical's frontier growth a head, 2160-2200, over twelve seeds", mean, Nations.LONG_RUN_GROWTH, LONG_RUN_TOL, false);
        assertTrue("Random's draws go on after 2100", continues);
    }

    /* =====================================================================
       8. MISSING FIGURES
       ===================================================================== */

    static void theMissingFigures(WorldData data) {
        out.println("--- a missing figure is filled, and the rule works what the file does not give ---");
        double[] raw = {Double.NaN, 2, Double.NaN, Double.NaN, 16, Double.NaN, 9};
        int[] marks = {0, 3, 0, 0, 3, 0, 2};
        Nations.Series s = Nations.filled(raw, marks, 4);
        assertTrue("a gap is filled log-linearly (2, 4, 8, 16), the years before the first take it, all marked 1, and the series ends at the last figure under the cap",
                s != null && s.last() == 4 && s.v()[0] == 2 && near(s.v()[2], 4, 1e-12) && near(s.v()[3], 8, 1e-12) && s.mark()[0] == 1 && s.mark()[2] == 1 && s.mark()[3] == 1 && s.mark()[1] == 3 && s.mark()[4] == 3);
        assertTrue("a series with no figure at or under the cap is null", Nations.filled(new double[] {Double.NaN, Double.NaN, 5}, new int[] {0, 0, 3}, 1) == null);

        Nations.Absent gone = (series, iso, year) ->
                series.equals("gdp_2015usd") && (iso.equals("COL") || iso.equals("CHN") && year <= 2002)
                || series.equals("cpi") && (iso.equals("JPN") && year >= 2005 && year <= 2007 || iso.equals("AUS") && year >= 2021)
                || series.equals("rate") && (iso.equals("CAN") || iso.equals("BEL"));
        Nations h = new Nations(data, Nations.Path.HISTORICAL, BASE_SEED, null, gone), r = new Nations(data, Nations.Path.RANDOM, BASE_SEED, null, gone);
        int usa = h.index("USA"), col = h.index("COL"), chn = h.index("CHN"), jpn = h.index("JPN"), aus = h.index("AUS"), can = h.index("CAN"), bel = h.index("BEL"), deu = h.index("DEU");

        Tally none = new Tally();
        none.see(near(h.year(col, 2000).output(), h.year(usa, 2000).outputPerHead() * h.year(col, 2000).people() * Nations.NO_DATA_SHARE, 1e-12), () -> "its 2000 stand-in");
        for (int y = 2000; y <= 2150; y++) {
            int yy = y;
            none.see(h.year(col, y).outputMark() == WorldData.MARK_ESTIMATE && r.year(col, y).outputMark() == WorldData.MARK_ESTIMATE && ok(h.year(col, y).output()), () -> "Colombia in " + yy);
        }
        tally("a nation with no output at all starts from the frontier's output a head x its people x NO_DATA_SHARE, every year marked 1", none);

        int id = data.numberOf("CHN");
        Tally early = new Tally();
        for (int y = 2000; y <= 2025; y++) {
            int yy = y;
            double file = data.pathValue("gdp_2015usd", id, y < 2003 ? 2003 : y);
            early.see(h.year(chn, y).output() == file && h.year(chn, y).outputMark() == (y < 2003 ? WorldData.MARK_ESTIMATE : data.pathMark("gdp_2015usd", id, y)), () -> "China in " + yy);
        }
        early.see(r.year(chn, 2000).output() == data.pathValue("gdp_2015usd", id, 2003), () -> "Random's 2000");
        tally("output missing before its first figure takes the first figure, marked 1; the figures after it are the file's", early);

        int ij = data.numberOf("JPN");
        Tally gap = new Tally();
        for (int y = 2005; y <= 2007; y++) {
            int yy = y;
            double lo = data.pathValue("cpi", ij, 2004), hi = data.pathValue("cpi", ij, 2008);
            gap.see(near(h.year(jpn, y).priceLevel(), lo * Math.pow(hi / lo, (y - 2004) / 4.0), 1e-9), () -> "Japan's price level in " + yy);
        }
        gap.see(h.year(jpn, 2004).priceLevel() == data.pathValue("cpi", ij, 2004) && h.year(jpn, 2008).priceLevel() == data.pathValue("cpi", ij, 2008), () -> "its ends");
        tally("a gap inside the published years is filled between its neighbours, log-linearly", gap);

        int ia = data.numberOf("AUS");
        Tally ended = new Tally();
        ended.see(h.year(aus, 2020).priceLevel() == data.pathValue("cpi", ia, 2020), () -> "its last published year");
        for (int y = 2021; y <= 2150; y++) {
            int yy = y;
            ended.see(h.year(aus, y).priceLevel() != data.pathValue("cpi", ia, Math.min(y, 2025)) && h.year(aus, y).inflation() >= Nations.INFLATION_FLOOR && ok(h.year(aus, y).priceLevel()), () -> "Australia in " + yy);
        }
        tally("a price level that ends before 2025 is the file's to its last year, then the rule's (never the file's after it)", ended);

        Tally rates = new Tally();
        rates.see(h.year(can, 2000).rateToUsd() == 1 && r.year(can, 2000).rateToUsd() == 1, () -> "Canada's stand-in");
        rates.see(h.year(bel, 2000).rateToUsd() == data.pathValue("rate", data.numberOf("DEU"), 2000), () -> "Belgium's stand-in is Germany's euro rate");
        for (int y = 2001; y <= 2025; y++) {
            int yy = y;
            rates.see(h.year(bel, y).rateToUsd() == h.year(deu, y).rateToUsd() && ok(h.year(can, y).rateToUsd()), () -> "in " + yy);
        }
        tally("a nation with no rate takes another's of the same money (else 1), and the rule works its money from 2001", rates);
    }

    /** What Random counts across all the nations, against the spec's one country-year in twenty that grew less than -5% (2000-2024, the WDI) and Barro's crises: printed, for the person who tunes the dials. */
    static void theFullCounts(WorldData full) {
        out.println("--- what Random counts across all the nations (full) ---");
        int nationYears = 0, fell = 0, starts = 0, seeds = 5;
        for (int k = 0; k < seeds; k++) {
            Nations r = of(full, Nations.Path.RANDOM, BASE_SEED + 977L * k);
            for (int i = 1; i <= r.count(); i++) {
                for (int y = 2001; y <= 2100; y++) {
                    nationYears++;
                    if (r.year(i, y).output() / r.year(i, y - 1).output() - 1 < -.05) fell++;
                    if (r.draws(i, y).crisisStarts()) starts++;
                }
            }
        }
        out.printf("counted: %d nation-years in %d seeds, %d crises begun (%.2f%% of them), %.1f%% of nation-years whose output fell more than 5%% (the spec: 1 in 20)%n",
                nationYears, seeds, starts, 100.0 * starts / nationYears, 100.0 * fell / nationYears);
        assertTrue("some nation-years fell more than 5% and not all of them: the share is between 1% and 20%", fell > nationYears / 100 && fell < nationYears / 5);
    }
}
