package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * The world's moneys (batch WN4; spec-exchange-rates 3.7, spec-nations 5.1): WorldMoneys' monthly rate of each money to the US dollar -
 * its parity path, the dollar cycle, the risk-off, the terms of trade and its own swing, with the pegs held and the soft ones broken by
 * the rule - held to the dials it is drawn from, to the file where the file is the truth, and to the rule where a draw cannot be seen.
 *
 * WHY. The rates were WN1's yearly draw, and a monthly model with five terms and a break rule can be wrong in ways no city would show: a
 * seed that does not decide the path (a reload that continues another world), a Historical that is not the file, a cycle or a swing whose size
 * or persistence is not the dial's (a world calmer or wilder than the sources say), a peg that drifts from its anchor or never breaks, a
 * risk-off that moves a money by someone else's loading, a terms-of-trade step that lands in the wrong month or on the wrong money. The
 * statistics are counted over seeded 26-year paths and each says against which constant it is judged; what has no constant to be judged by
 * (the volatility a year, the half-life an economist fits) is printed beside the study's table.
 *
 * THE DATA. On the fixture (WorldData.FIXTURE, twelve nations) always; the full world-1.bin where it is named (-Dcbs.world=<path>,
 * WorldData.locate()), for the sections that need its 196 nations, 158 moneys and pegs, and a line says when it is not. The fixture has no pegs, so a
 * peg is caused by giving Nations a regime (the yuan on the dollar, the peso on the yuan, a loop of the two).
 *
 * What this has to prove:
 *   0. THE MONEYS: every money a nation's year names on the path is carried, each with an anchor nation, and a nation's rate is a number.
 *   1. ONE SEED, ONE PATH: two WorldMoneys of one path and seed step identically for 30 years, bit for bit; another seed differs; a save
 *      at month 150 (and 320) restored into a fresh one steps on bit for bit, on both paths; a save of another file is refused.
 *   2. HISTORICAL IS THE FILE to December 2025: every money with a figure of the file equals it, exactly, in every month; both paths
 *      begin from the file's January 2000.
 *   3. THE SWINGS ARE THE DIALS' (Random, seeded paths, the terms of trade null): the dollar cycle's innovation sd and persistence, and each
 *      major's and an emerging float's own swing's, within 5% of CYCLE_SIGMA / CYCLE_HALF_LIFE and the money's sigma / OWN_HALF_LIFE; printed:
 *      each one's volatility a year and the half-life an economist would fit, beside the study's table.
 *   4. PEGS HOLD AND BREAK BY THE RULE: a held peg's rate over its anchor's is its ratio; a loop holds; no board ever breaks; the breaks
 *      over the paths are the sum of ForeignAccounts.hazard(gap) over the held months, within 3 sd (Poisson).
 *   5. A RISK-OFF moves the havens up and the high yielders down by their loadings, and fades by its half-life; starts counted against RISK_OFF_RATE.
 *   6. THE TERMS OF TRADE: a January's rise of 0.1 in a commodity exporter's ln terms of trade moves its money by TOT_PASS x its commodity share
 *      x 0.1, a third in each of January, February and March, and no other money; the market's two reads (commodityShare, lnTermsOfTrade)
 *      are held to the file's pairs.
 *   7. CROSS RATES go through the dollar; the dollar's rate is 1 always.
 *   8. PARITY: with every other term quiet, a float's ln rate moves by the change in ln(P_anchor / P_US), and its constant is the mean
 *      real gap of the file's years.
 *   9. WHAT IT COSTS: the median month's step, printed and bounded.
 *
 * Every fixture causes its condition: a quiet WorldMoneys has no term but the one the section adds, a peg is given by a regime, a risk-off is
 * forced, and the statistics are counted over enough seeded paths that a wrong dial would show.
 */
public class WorldMoneysCheck {

    static int fails = 0;
    /** Where the harness prints: the console. */
    static final PrintStream out = System.out;

    /** The seed the single-world sections use. */
    static final long BASE_SEED = 9_101;

    /** The seeds the statistics are counted over: the full file's paths (26 years each), and the fixture's. */
    static final int FULL_SEEDS = 1_000, FIXTURE_SEEDS = 300;

    /** The months of a path the statistics are counted over: January 2000 to November 2025, the 26-year run. */
    static final int PATH_MONTHS = 311;

    /** The months the identity and restore sections run: thirty years, so Historical's rule runs 48 months. */
    static final int LONG_MONTHS = 360;

    /** A measured sd or persistence is within this share of the dial it is drawn from (a pooled lag-1 fit over 311 months a path is biased low by about 1.3%). */
    static final double DIAL_TOL = .05;

    /** A count of breaks or of risk-off starts is within this many standard deviations of its expected number. */
    static final double SIGMAS = 3;

    /** Two logs or two rates that should be one differ by less than this (the rates are exp of sums of a few logs). */
    static final double EXACT = 1e-12;

    /** The generous bound on the median month's step, milliseconds (the study measured about 10 microseconds; the gate's timing is not this harness's). */
    static final double STEP_BOUND_MS = 2;

    /** Pegs the fixture is given: the yuan on the dollar, the peso on the yuan (a chain); and the loop of the two. */
    static final Map<String, String> FIXTURE_PEGS = Map.of("CHN", "peg:USD", "COL", "peg:CNY");
    static final Map<String, String> FIXTURE_LOOP = Map.of("CHN", "peg:COP", "COL", "peg:CNY");

    /** The study's table (spec-exchange-rates 3.7), for the findings: volatility a year model and real, the half-life an economist measures, the worst month. */
    static final String[] STUDY_CODES = {"EUR", "JPY", "GBP", "CHF", "CAD", "AUD"};
    static final double[][] STUDY = {{8.9, 9.3, 4.7, 8.3}, {9.9, 9.4, 4.4, 10.9}, {8.6, 8.5, 4.5, 8.8}, {9.1, 9.6, 4.9, 8.6}, {8.1, 8.4, 3.8, 9.2}, {12.3, 11.8, 3.5, 18.1}};
    static final double[] STUDY_EMERGING = {16.6, 14, 3.5, 26.5};

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-130s %s%n", label, ok ? "OK" : "FAIL");
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
        out.printf("%-130s %s  (%d of %d agree)%s%n", label, ok ? "OK" : "FAIL", t.n - t.bad, t.n,
                t.bad > 0 ? "; the first that does not: " + t.first : t.n == 0 ? "; none read" : "");
    }

    static boolean same(double a, double b) { return Double.doubleToLongBits(a) == Double.doubleToLongBits(b); }

    /** A measured figure judged against the constant it is drawn from, within a share of it. */
    static void within(String label, double measured, double dial, double tol) {
        boolean ok = Math.abs(measured - dial) <= tol * Math.abs(dial);
        assertTrue(String.format("%s: %.5f against the dial's %.5f (within %d%%)", label, measured, dial, Math.round(tol * 100)), ok);
    }

    /** A count judged against its expected number, within SIGMAS standard deviations of it. */
    static void poisson(String label, double observed, double expected, double sd) {
        double z = sd > 0 ? (observed - expected) / sd : observed == expected ? 0 : Double.POSITIVE_INFINITY;
        assertTrue(String.format("%s: %.0f against the expected %.1f (sd %.1f, %.2f sd away; within %.0f)", label, observed, expected, sd, z, SIGMAS), Math.abs(z) <= SIGMAS);
    }

    /** The world file and what is built on it: the fixture or the full file, the nations' commodity shares of exports. */
    static final class Scope {
        final String name;
        final WorldData data;
        final WorldMarket.Base base;
        final double[] shares;

        Scope(String name, WorldData data) {
            this.name = name;
            this.data = data;
            this.base = WorldMarket.Base.of(data);
            shares = new double[data.nationCount()];
            for (int i = 1; i <= shares.length; i++) shares[i - 1] = base.commodityShare(i);
        }

        Nations nations(Nations.Path path, long seed, Map<String, String> regimes) {
            return regimes == null ? new Nations(data, path, seed) : new Nations(data, path, seed, regimes);
        }

        WorldMoneys moneys(Nations nations) { return new WorldMoneys(data, nations, shares); }

        WorldMoneys moneys(Nations.Path path, long seed, Map<String, String> regimes) { return moneys(nations(path, seed, regimes)); }

        int nation(String iso3) { return data.numberOf(iso3); }
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        byte[] bytes = WorldDataCheck.resource(WorldData.FIXTURE);
        if (bytes == null) {
            assertTrue("no fixture: " + WorldData.FIXTURE + " is not beside the classes", false);
            System.out.println("\n" + fails + " FAILED");
            System.exit(1);
        }
        Scope fx = new Scope("fixture", WorldData.of(bytes));
        sections(fx, new Pass(fx, FIXTURE_SEEDS, null), new Pass(fx, FIXTURE_SEEDS, FIXTURE_PEGS), FIXTURE_PEGS);

        out.println("--- the full file, when it is named ---");
        Path fullPath = WorldData.locate();
        String named = System.getProperty(WorldData.PROPERTY);
        if (named != null && !named.isBlank() && !Files.isRegularFile(fullPath)) {
            assertTrue("the file -D" + WorldData.PROPERTY + " names is there (" + named + ")", false);
        } else if (fullPath == null) {
            out.println("world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": its sections did not run");
        } else {
            out.println("reading " + fullPath.toAbsolutePath());
            try (WorldData data = WorldData.open(fullPath)) {
                Scope all = new Scope("full", data);
                Pass pass = new Pass(all, FULL_SEEDS, null);
                sections(all, pass, pass, null);
                theFullOnly(all, pass);
            }
        }
        out.printf("%nWorldMoneysCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** The sections both scopes run; floats is the pass over the file's own regimes, pegs the one the pegs are counted on (the fixture's with the regimes it is given). */
    static void sections(Scope sc, Pass floats, Pass pegs, Map<String, String> pegRegimes) {
        theMoneys(sc);
        theSeed(sc, null);
        if (pegRegimes != null) theSeed(sc, pegRegimes);
        theFile(sc);
        theSwings(sc, floats);
        thePegs(sc, pegs, pegRegimes != null);
        theRiskOff(sc, floats);
        theTermsOfTrade(sc);
        theCross(sc);
        theParity(sc);
        theCost(sc);
    }

    /* =====================================================================
       THE RUNS - helpers the sections share
       ===================================================================== */

    /** A January's ln terms of trade for the identity and restore sections: a made-up but deterministic figure per nation and year (the market's own are section 6's). */
    static double[] synthetic(int t, int nations) {
        double[] ln = new double[nations];
        for (int i = 0; i < nations; i++) ln[i] = .08 * Math.sin(i * .7 + t / 12.0);
        return ln;
    }

    /** Steps from the month after w's to last, with a January's terms of trade handed in each January. */
    static void run(WorldMoneys w, int last, int nations) {
        for (int t = w.month() + 1; t <= last; t++) w.step(t, t % 12 == 0 ? synthetic(t, nations) : null);
    }

    /** Whether two WorldMoneys are in the same state: every money's rate, parity and shadow, the cycle, the risk-off, the swings and the terms of trade, bit for bit. */
    static boolean alike(WorldMoneys a, WorldMoneys b) {
        if (a.month() != b.month() || !same(a.cycle(), b.cycle()) || !same(a.riskOff(), b.riskOff()) || a.riskOffStarted() != b.riskOffStarted()) return false;
        if (!a.breaksThisMonth().equals(b.breaksThisMonth())) return false;
        for (String c : a.codes()) {
            if (!same(a.rate(c), b.rate(c)) || !same(a.shadow(c), b.shadow(c)) || !same(a.swing(c), b.swing(c)) || !same(a.totReached(c), b.totReached(c))
                    || a.pegHeld(c) != b.pegHeld(c)) return false;
        }
        return true;
    }

    /** The floats: moneys with no peg that the rule rules (a Historical lira, with no nation on it, keeps its last rate). */
    static List<String> floats(WorldMoneys w) {
        List<String> list = new ArrayList<>();
        for (String c : w.codes()) if (w.pegAnchor(c) == null && w.ruled(c) && !c.equals(Nations.DOLLAR)) list.add(c);
        return list;
    }

    /* =====================================================================
       0. THE MONEYS
       ===================================================================== */

    static void theMoneys(Scope sc) {
        out.println("--- the moneys it carries (" + sc.name + ") ---");
        for (Nations.Path path : Nations.Path.values()) {
            Nations n = sc.nations(path, BASE_SEED, null);
            WorldMoneys w = sc.moneys(n);
            Tally carried = new Tally(), anchored = new Tally(), rated = new Tally();
            int absent = 0;
            for (String c : w.codes()) {
                anchored.see(w.anchorOf(c) >= 1 && w.anchorOf(c) <= n.count(), () -> c + " has anchor " + w.anchorOf(c));
                if (!Double.isFinite(w.rate(c))) absent++;
            }
            for (int t = 0; t <= LONG_MONTHS; t++) {
                if (t > 0) w.step(t, null);
                if (t % 12 != 0) continue;
                for (int i = 1; i <= n.count(); i++) {
                    int year = Nations.FIRST_YEAR + t / 12, nation = i;
                    String money = n.year(i, year).money();
                    carried.see(w.codes().contains(money), () -> n.iso3(nation) + " in " + year + " is on " + money);
                    rated.see(Double.isFinite(w.rateOfNation(i)) && w.rateOfNation(i) > 0, () -> n.iso3(nation) + " in " + year + " (" + money + ") has no rate");
                }
            }
            tally(path + ": every money a nation's year names, 2000-2030, is carried", carried);
            tally(path + ": every money carried has an anchor nation of the file", anchored);
            tally(path + ": every nation's money this January, 2000-2030, has a rate above 0", rated);
            out.println(path + ": " + w.codes().size() + " moneys carried, " + absent + " with no rate in January 2000 (they come in with the file's first figure)");
        }
    }

    /* =====================================================================
       1. ONE SEED, ONE PATH
       ===================================================================== */

    static void theSeed(Scope sc, Map<String, String> regimes) {
        String tag = sc.name + (regimes == null ? "" : ", with pegs");
        out.println("--- one seed, one path (" + tag + ") ---");
        int n = sc.data.nationCount();
        for (Nations.Path path : Nations.Path.values()) {
            WorldMoneys a = sc.moneys(path, BASE_SEED, regimes), b = sc.moneys(path, BASE_SEED, regimes);
            Tally same = new Tally();
            for (int t = 1; t <= LONG_MONTHS; t++) {
                double[] ln = t % 12 == 0 ? synthetic(t, n) : null;
                a.step(t, ln);
                b.step(t, ln);
                int tt = t;
                same.see(alike(a, b), () -> "month " + tt);
            }
            tally(path + ": two WorldMoneys of one seed agree on every rate, shadow, swing and peg, bit for bit, for " + LONG_MONTHS + " months", same);
            WorldMoneys other = sc.moneys(path, BASE_SEED + 1, regimes);
            run(other, LONG_MONTHS, n);
            assertTrue(path + ": another seed gives another path: some money's rate in month " + LONG_MONTHS + " differs", !alike(a, other));
            // a save at 150 and at 320, restored into a fresh one, goes on as the one saved did
            for (int at : new int[] {150, 320}) {
                WorldMoneys saved = sc.moneys(path, BASE_SEED, regimes);
                run(saved, at, n);
                double[] bits = saved.toSaveArray();
                int broken = 0;
                for (String c : saved.codes()) if (saved.ruling() && saved.pegAnchor(c) != null && !saved.pegHeld(c)) broken++;
                WorldMoneys fresh = sc.moneys(path, BASE_SEED, regimes);
                fresh.restore(bits);
                Tally go = new Tally();
                go.see(alike(saved, fresh), () -> "at the save");
                for (int t = at + 1; t <= LONG_MONTHS; t++) {
                    double[] ln = t % 12 == 0 ? synthetic(t, n) : null;
                    saved.step(t, ln);
                    fresh.step(t, ln);
                    int tt = t;
                    go.see(alike(saved, fresh), () -> "month " + tt);
                }
                tally(path + ": a save at month " + at + " (" + broken + " pegs broken by then) restored into a fresh one steps on to " + LONG_MONTHS + " bit for bit", go);
                boolean finite = true;
                for (double x : bits) finite &= Double.isFinite(x);
                assertTrue(path + ": the save at month " + at + " is " + bits.length + " numbers, none NaN or infinite (it goes through JSON)", finite);
            }
        }
        // a save of another file, a wrong format, a short one: refused, the state untouched
        WorldMoneys w = sc.moneys(Nations.Path.RANDOM, BASE_SEED, regimes);
        run(w, 40, n);
        double[] good = w.toSaveArray();
        WorldMoneys target = sc.moneys(Nations.Path.RANDOM, BASE_SEED, regimes);
        WorldMoneys untouched = sc.moneys(Nations.Path.RANDOM, BASE_SEED, regimes);
        double[] moreMoneys = good.clone(), moreNations = good.clone(), format = good.clone();
        moreMoneys[1]++;
        moreNations[2]++;
        format[0]++;
        assertTrue("a save that says one more money is refused, and the message says moneys", refused(target, moreMoneys, "moneys"));
        assertTrue("a save that says one more nation is refused", refused(target, moreNations, "nations"));
        assertTrue("a save of another format is refused", refused(target, format, "format"));
        assertTrue("a save cut short is refused", refused(target, Arrays.copyOf(good, good.length - 1), "numbers long"));
        assertTrue("a refused restore leaves the state as it was", alike(target, untouched));
    }

    /** Whether restoring that save throws an IllegalArgumentException whose message holds the word. */
    static boolean refused(WorldMoneys w, double[] saved, String word) {
        try {
            w.restore(saved);
            return false;
        } catch (IllegalArgumentException e) {
            return e.getMessage().contains(word);
        }
    }

    /* =====================================================================
       2. HISTORICAL IS THE FILE
       ===================================================================== */

    static void theFile(Scope sc) {
        out.println("--- Historical is the file to December 2025 (" + sc.name + ") ---");
        WorldData data = sc.data;
        WorldMoneys h = sc.moneys(Nations.Path.HISTORICAL, BASE_SEED, null), r = sc.moneys(Nations.Path.RANDOM, BASE_SEED, null);
        Tally figures = new Tally(), dollar = new Tally(), start = new Tally(), held = new Tally();
        int compared = 0, without = 0;
        for (int t = 0; t < WorldMoneys.HISTORICAL_RULE_START; t++) {
            if (t > 0) h.step(t, null);
            int month = t;
            dollar.see(h.rate(Nations.DOLLAR) == 1 && h.cross("USD", "USD") == 1, () -> "month " + month);
            for (String c : h.codes()) {
                double file = data.moneyNumber(c) > 0 ? data.rate(c, t) : Double.NaN;
                if (file > 0 && Double.isFinite(file)) {
                    compared++;
                    int tt = t;
                    figures.see(h.rate(c) == file, () -> c + " in month " + tt + ": " + h.rate(c) + " against the file's " + file);
                } else {
                    without++;
                    int tt = t;
                    if (c.equals(Nations.DOLLAR)) continue;
                    // a money the file has no figure for holds its last (or is not there yet): never a number it was not
                    held.see(!Double.isFinite(h.rate(c)) || h.rate(c) > 0, () -> c + " in month " + tt);
                }
            }
        }
        for (String c : h.codes()) {
            double file = data.moneyNumber(c) > 0 ? data.rate(c, 0) : Double.NaN;
            if (file > 0) start.see(h.rateAtStart(c) == file && r.rateAtStart(c) == file || !r.codes().contains(c) && h.rateAtStart(c) == file, () -> c);
        }
        tally("every money with a figure of the file equals it exactly in every month 0-" + (WorldMoneys.HISTORICAL_RULE_START - 1) + " (" + compared + " figures)", figures);
        tally("the dollar's rate is 1 in every month, and cross(USD, USD) is 1", dollar);
        if (without > 0) tally("a month the file has no figure for holds the last or is not there yet (" + without + " such money-months)", held);
        else out.println("  every carried money has a figure of the file in every month here: the full file's section holds the old moneys' held months");
        tally("both paths begin from the file's January 2000: rateAtStart is the file's figure for every money it has one for", start);
        assertTrue("Historical ran the rule from month " + h.ruleStart() + " and stands in month " + h.month(), h.ruleStart() == 312 && h.month() == 311 && !h.ruling());
        h.step(312, null);
        assertTrue("month 312 (January 2026) is the rule's first on Historical, and Random's is month " + r.ruleStart(), h.ruling() && r.ruleStart() == 1);
    }

    /* =====================================================================
       THE PASS - seeded paths, counted once, read by sections 3, 4 and 5
       ===================================================================== */

    /** A lag-1 regression's sums: the persistence and the innovation sd. */
    static final class Acc {
        double xy, xx, yy;
        long n;

        void add(double prev, double now) {
            xy += prev * now;
            xx += prev * prev;
            yy += now * now;
            n++;
        }

        void merge(Acc o) {
            xy += o.xy;
            xx += o.xx;
            yy += o.yy;
            n += o.n;
        }

        double phi() { return xy / xx; }

        double innovationSd() {
            double p = phi();
            return Math.sqrt(Math.max(0, (yy - 2 * p * xy + p * p * xx) / (n - 1)));
        }
    }

    /** One seeded path's counts. */
    static final class Part {
        final Acc cycle = new Acc();
        final Acc[] own;
        final double[] sum12, sumSq12, halfLife, worst;
        final long[] n12;
        long riskStarts, ratioChecked, ratioBad, boardMonths, boardBreaks;
        final long[] breaks;
        final double[] hazard;
        final long[] faced;

        Part(int watch, int pegs) {
            own = new Acc[watch];
            for (int j = 0; j < watch; j++) own[j] = new Acc();
            sum12 = new double[watch];
            sumSq12 = new double[watch];
            halfLife = new double[watch];
            worst = new double[watch];
            n12 = new long[watch];
            breaks = new long[pegs];
            hazard = new double[pegs];
            faced = new long[pegs];
        }
    }

    /** The paths of one scope: Random, seeds seeds, PATH_MONTHS months each, merged in seed order so that the figures do not depend on the threads. */
    static final class Pass {
        final Scope sc;
        final int seeds;
        final Map<String, String> regimes;
        final String[] watch;
        final String[] pegs, pegTarget;
        final boolean[] board;
        final Acc cycle = new Acc();
        final Acc[] own;
        final double[] sum12, sumSq12;
        final long[] n12;
        final List<List<Double>> halfLives = new ArrayList<>(), worsts = new ArrayList<>();
        long riskStarts, ratioChecked, ratioBad, boardMonths, boardBreaks;
        final long[] breaks, faced;
        final double[] hazard;
        final double millis;

        Pass(Scope sc, int seeds, Map<String, String> regimes) {
            long t0 = System.nanoTime();
            this.sc = sc;
            this.seeds = seeds;
            this.regimes = regimes;
            WorldMoneys ref = sc.moneys(Nations.Path.RANDOM, BASE_SEED, regimes);
            // the majors and one emerging float, the ones that float
            List<String> w = new ArrayList<>();
            for (String c : STUDY_CODES) if (ref.pegAnchor(c) == null && Double.isFinite(ref.rate(c))) w.add(c);
            for (String c : new String[] {"BRL", "COP"}) {
                if (ref.pegAnchor(c) == null && Double.isFinite(ref.rate(c)) && ref.kind(c).equals("emerging float")) { w.add(c); break; }
            }
            watch = w.toArray(new String[0]);
            List<String> p = new ArrayList<>();
            for (String c : ref.codes()) if (ref.pegHeld(c)) p.add(c);
            pegs = p.toArray(new String[0]);
            pegTarget = new String[pegs.length];
            board = new boolean[pegs.length];
            for (int q = 0; q < pegs.length; q++) {
                pegTarget[q] = ref.pegAnchor(pegs[q]);
                board[q] = ref.isBoard(pegs[q]);
            }
            Part[] parts = new Part[seeds];
            IntStream.range(0, seeds).parallel().forEach(s -> parts[s] = runSeed(BASE_SEED + 100 + s));
            own = new Acc[watch.length];
            for (int j = 0; j < watch.length; j++) {
                own[j] = new Acc();
                halfLives.add(new ArrayList<>());
                worsts.add(new ArrayList<>());
            }
            sum12 = new double[watch.length];
            sumSq12 = new double[watch.length];
            n12 = new long[watch.length];
            breaks = new long[pegs.length];
            faced = new long[pegs.length];
            hazard = new double[pegs.length];
            for (Part part : parts) {
                cycle.merge(part.cycle);
                riskStarts += part.riskStarts;
                ratioChecked += part.ratioChecked;
                ratioBad += part.ratioBad;
                boardMonths += part.boardMonths;
                boardBreaks += part.boardBreaks;
                for (int j = 0; j < watch.length; j++) {
                    own[j].merge(part.own[j]);
                    sum12[j] += part.sum12[j];
                    sumSq12[j] += part.sumSq12[j];
                    n12[j] += part.n12[j];
                    halfLives.get(j).add(part.halfLife[j]);
                    worsts.get(j).add(part.worst[j]);
                }
                for (int q = 0; q < pegs.length; q++) {
                    breaks[q] += part.breaks[q];
                    faced[q] += part.faced[q];
                    hazard[q] += part.hazard[q];
                }
            }
            millis = (System.nanoTime() - t0) / 1e6;
        }

        private Part runSeed(long seed) {
            WorldMoneys w = sc.moneys(Nations.Path.RANDOM, seed, regimes);
            Part p = new Part(watch.length, pegs.length);
            double[][] ln = new double[watch.length][PATH_MONTHS + 1], gap = new double[watch.length][PATH_MONTHS + 1];
            double[] prevS = new double[watch.length];
            double prevC = w.cycle();
            for (int j = 0; j < watch.length; j++) {
                prevS[j] = w.swing(watch[j]);
                ln[j][0] = Math.log(w.rate(watch[j]));
                gap[j][0] = Math.log(w.rate(watch[j]) / w.ppp(watch[j]));
            }
            for (int t = 1; t <= PATH_MONTHS; t++) {
                w.step(t, null);
                p.cycle.add(prevC, w.cycle());
                prevC = w.cycle();
                for (int j = 0; j < watch.length; j++) {
                    double s = w.swing(watch[j]);
                    p.own[j].add(prevS[j], s);
                    prevS[j] = s;
                    ln[j][t] = Math.log(w.rate(watch[j]));
                    gap[j][t] = Math.log(w.rate(watch[j]) / w.ppp(watch[j]));
                }
                if (w.riskOffStarted()) p.riskStarts++;
                List<String> broke = w.breaksThisMonth();
                for (int q = 0; q < pegs.length; q++) {
                    String c = pegs[q];
                    boolean b = broke.contains(c), held = w.pegHeld(c);
                    if (board[q]) {
                        if (held) p.boardMonths++; else p.boardBreaks++;
                    } else if (held || b) {
                        p.faced[q]++;
                        p.hazard[q] += ForeignAccounts.hazard(Math.max(0, w.pegGap(c)));
                        if (b) p.breaks[q]++;
                    }
                    if (held) {
                        p.ratioChecked++;
                        double now = w.rate(c) / w.rate(pegTarget[q]), then = w.rateAtStart(c) / w.rateAtStart(pegTarget[q]);
                        if (!(Math.abs(now / then - 1) <= EXACT)) p.ratioBad++;
                    }
                }
            }
            for (int j = 0; j < watch.length; j++) {
                for (int t = 12; t <= PATH_MONTHS; t++) {
                    double d = ln[j][t] - ln[j][t - 12];
                    p.sum12[j] += d;
                    p.sumSq12[j] += d * d;
                    p.n12[j]++;
                }
                double w1 = 0;
                for (int t = 1; t <= PATH_MONTHS; t++) w1 = Math.max(w1, Math.abs(ln[j][t] - ln[j][t - 1]));
                p.worst[j] = w1;
                // the economist's fit, as the study's prototype makes it (RatesModel, world()): the slope of gap_t on gap_{t-1}, no constant, and its half-life; 99 years for a slope outside (0, 1)
                double sxy = 0, sxx = 0;
                for (int t = 1; t <= PATH_MONTHS; t++) {
                    sxy += gap[j][t - 1] * gap[j][t];
                    sxx += gap[j][t - 1] * gap[j][t - 1];
                }
                double phi = sxy / sxx;
                p.halfLife[j] = phi > 0 && phi < 1 ? Math.log(.5) / Math.log(phi) / 12 : 99;
            }
            return p;
        }

        /** The sd of the 12-month log changes of a watched money, pooled over the paths. */
        double volatility(int j) {
            double mean = sum12[j] / n12[j];
            return Math.sqrt((sumSq12[j] - n12[j] * mean * mean) / (n12[j] - 1));
        }

        double mean(List<Double> list) {
            double sum = 0;
            for (double x : list) sum += x;
            return sum / list.size();
        }

        double median(List<Double> list) {
            List<Double> sorted = new ArrayList<>(list);
            sorted.sort(null);
            return sorted.get(sorted.size() / 2);
        }
    }

    /* =====================================================================
       3. THE SWINGS ARE THE DIALS'
       ===================================================================== */

    static void theSwings(Scope sc, Pass pass) {
        out.println("--- the swings are the dials' (" + sc.name + ": " + pass.seeds + " Random paths of " + (PATH_MONTHS / 12) + " years, the terms of trade null; " + String.format("%.1f s", pass.millis / 1000) + ") ---");
        WorldMoneys ref = sc.moneys(Nations.Path.RANDOM, BASE_SEED, pass.regimes);
        double phiCycle = Math.pow(.5, 1 / WorldMoneys.CYCLE_HALF_LIFE), phiOwn = Math.pow(.5, 1 / WorldMoneys.OWN_HALF_LIFE);
        within("the dollar cycle's innovation sd a month (" + pass.cycle.n + " months)", pass.cycle.innovationSd(), WorldMoneys.CYCLE_SIGMA, DIAL_TOL);
        within("the dollar cycle's persistence a month, 0.5 to the 1/" + (int) WorldMoneys.CYCLE_HALF_LIFE, pass.cycle.phi(), phiCycle, DIAL_TOL);
        for (int j = 0; j < pass.watch.length; j++) {
            String c = pass.watch[j];
            within(c + " (" + ref.kind(c) + ")'s own swing's innovation sd a month", pass.own[j].innovationSd(), ref.sigma(c), DIAL_TOL);
            within(c + "'s own swing's persistence a month, 0.5 to the 1/" + (int) WorldMoneys.OWN_HALF_LIFE, pass.own[j].phi(), phiOwn, DIAL_TOL);
        }
        out.println("  finding, beside the study's table (3.7): the volatility a year (sd of the 12-month log changes, the opening gap from the file included); the half-life an economist would fit,");
        out.println("    as the study's prototype makes it (per path the slope of the log gap from parity on last month's, no constant; the mean of the paths' half-lives, the median in brackets);");
        out.printf("    the worst month (the median path). The true half-life of every own swing is %.1f years.%n", WorldMoneys.OWN_HALF_LIFE / 12);
        out.printf("  %-24s %24s %30s %24s%n", "money", "volatility a year", "half-life years", "worst month");
        for (int j = 0; j < pass.watch.length; j++) {
            String c = pass.watch[j];
            double[] study = STUDY_EMERGING;
            for (int s = 0; s < STUDY_CODES.length; s++) if (STUDY_CODES[s].equals(c)) study = STUDY[s];
            out.printf("  %-24s %9.1f%% (study %4.1f%%) %6.1f (%4.1f) (study %3.1f) %9.1f%% (study %4.1f%%)%n", c + " " + ref.kind(c), 100 * pass.volatility(j), study[0],
                    pass.mean(pass.halfLives.get(j)), pass.median(pass.halfLives.get(j)), study[2], 100 * pass.median(pass.worsts.get(j)), study[3]);
        }
    }

    /* =====================================================================
       4. PEGS HOLD AND BREAK BY THE RULE
       ===================================================================== */

    static void thePegs(Scope sc, Pass pass, boolean given) {
        out.println("--- pegs hold and break by the rule (" + sc.name + (given ? ", with pegs given by regimes" : "") + ": " + pass.seeds + " Random paths) ---");
        assertTrue("there are pegs to count: " + pass.pegs.length + " moneys held to another from the start", pass.pegs.length > 0);
        assertTrue("every month a peg holds, its rate over the money it is pegged to equals its ratio of January 2000 to " + EXACT + " (" + pass.ratioChecked + " peg-months read, "
                + pass.ratioBad + " off)", pass.ratioChecked > 0 && pass.ratioBad == 0);
        int soft = 0, boards = 0;
        long breaks = 0, faced = 0;
        double hazard = 0;
        for (int q = 0; q < pass.pegs.length; q++) {
            if (pass.board[q]) {
                boards++;
                continue;
            }
            soft++;
            breaks += pass.breaks[q];
            faced += pass.faced[q];
            hazard += pass.hazard[q];
        }
        if (boards > 0) {
            assertTrue("no board ever breaks: " + boards + " boards held in all " + pass.boardMonths + " of their months, " + pass.boardBreaks + " not", pass.boardBreaks == 0 && pass.boardMonths > 0);
        } else {
            out.println("  no currency board (" + String.join(", ", WorldMoneys.CURRENCY_BOARDS) + ") among the moneys here: its section is the full file's");
        }
        poisson(soft + " soft pegs: the breaks over " + pass.seeds + " paths against the sum of hazard(max(0, gap)) over the " + faced + " months they were held", breaks, hazard, Math.sqrt(hazard));
        out.printf("  finding: %d of %d soft pegs on a path broke within %d years (%.1f%%); the hazard they faced was %.2f%% a month on average%n", breaks, (long) soft * pass.seeds,
                PATH_MONTHS / 12, 100.0 * breaks / Math.max(1, (long) soft * pass.seeds), 100 * hazard / Math.max(1, faced));
        if (!given) return;
        // a loop of pegs holds last month's rate, quietly: the two moneys are where they began
        WorldMoneys loop = sc.moneys(Nations.Path.RANDOM, BASE_SEED, FIXTURE_LOOP);
        loop.quiet();
        Tally held = new Tally(), finite = new Tally();
        for (int t = 1; t <= 24; t++) {
            loop.step(t, null);
            int tt = t;
            for (String c : new String[] {"CNY", "COP"}) {
                held.see(Math.abs(loop.rate(c) / loop.rateAtStart(c) - 1) <= EXACT && loop.pegHeld(c), () -> c + " in month " + tt + " is " + loop.rate(c) + " from " + loop.rateAtStart(c));
                finite.see(Double.isFinite(loop.rate(c)) && loop.rate(c) > 0, () -> c + " in month " + tt);
            }
        }
        tally("a loop of pegs (the yuan on the peso on the yuan) holds last month's rate: both moneys are where they began for 24 months, and still pegs", held);
        tally("...and neither rate is ever anything but a number above 0", finite);
        // a chain works anchors first: the peso on the yuan on the dollar
        WorldMoneys chain = sc.moneys(Nations.Path.RANDOM, BASE_SEED, FIXTURE_PEGS);
        chain.quiet();
        Tally linked = new Tally();
        for (int t = 1; t <= 24; t++) {
            chain.step(t, null);
            int tt = t;
            linked.see(Math.abs(chain.rate("COP") / chain.rate("CNY") / (chain.rateAtStart("COP") / chain.rateAtStart("CNY")) - 1) <= EXACT
                    && Math.abs(chain.rate("CNY") / chain.rateAtStart("CNY") - 1) <= EXACT, () -> "month " + tt);
        }
        tally("a chain (the peso on the yuan on the dollar) holds both links the month it is stepped, anchors first, while quiet", linked);
    }

    /* =====================================================================
       5. A RISK-OFF
       ===================================================================== */

    static void theRiskOff(Scope sc, Pass pass) {
        out.println("--- a risk-off moves the havens up and the high yielders down by their loadings (" + sc.name + ") ---");
        WorldMoneys w = sc.moneys(Nations.Path.RANDOM, BASE_SEED, null);
        w.quiet();
        w.step(1, null);
        List<String> floats = floats(w);
        double size = 1, phiRisk = Math.pow(.5, 1 / WorldMoneys.RISK_OFF_HALF_LIFE);
        Tally calm = new Tally(), hit = new Tally(), fade = new Tally();
        for (String c : floats) calm.see(Math.abs(Math.log(w.rate(c) / w.ppp(c))) <= EXACT, () -> c + " is " + Math.log(w.rate(c) / w.ppp(c)) + " from parity before");
        w.forceRiskOff(size);
        w.step(2, null);
        assertTrue("the forced risk-off started this month and R is its size " + size, w.riskOffStarted() && w.riskOff() == size);
        for (String c : floats) hit.see(Math.abs(Math.log(w.rate(c) / w.ppp(c)) - w.riskOffLoading(c) * size) <= EXACT, () -> c + " moved " + Math.log(w.rate(c) / w.ppp(c)) + " for a loading of " + w.riskOffLoading(c));
        double[] first = new double[floats.size()];
        for (int k = 0; k < first.length; k++) first[k] = Math.log(w.rate(floats.get(k)) / w.ppp(floats.get(k)));
        w.step(3, null);
        assertTrue("the next month none started and R has faded by 0.5 to the 1/" + (int) WorldMoneys.RISK_OFF_HALF_LIFE, !w.riskOffStarted() && Math.abs(w.riskOff() - size * phiRisk) <= EXACT);
        for (int k = 0; k < first.length; k++) {
            String c = floats.get(k);
            double moved = Math.log(w.rate(c) / w.ppp(c)) - first[k];
            fade.see(Math.abs(moved - w.riskOffLoading(c) * size * (phiRisk - 1)) <= EXACT, () -> c + " moved " + moved);
        }
        tally("before it, every float is on its parity path (the cycle and the swings are quiet)", calm);
        tally("in its month every float's ln rate over parity moves by its loading x " + size + ", to " + EXACT + ", " + floats.size() + " floats, nothing else", hit);
        tally("the month after, by its loading x " + size + " x (phi - 1): the risk-off fades and nothing else moves", fade);
        assertTrue("the yen and the franc are dearer in a panic (fewer units a dollar), the euro and the pound are not",
                w.riskOffLoading("JPY") < 0 && w.riskOffLoading("CHF") < 0 && w.riskOffLoading("EUR") > 0 && w.riskOffLoading("GBP") > 0);
        assertTrue("the Australian dollar and an emerging float are cheaper (more units a dollar): " + w.riskOffLoading("AUD") + " and the " + pass.watch[pass.watch.length - 1] + "'s " + w.riskOffLoading(pass.watch[pass.watch.length - 1]),
                w.riskOffLoading("AUD") > 0 && w.riskOffLoading(pass.watch[pass.watch.length - 1]) > 0);
        double months = (double) pass.seeds * PATH_MONTHS, expected = months * WorldMoneys.RISK_OFF_RATE;
        poisson("risk-offs started over " + pass.seeds + " paths of " + PATH_MONTHS + " months against RISK_OFF_RATE x months", pass.riskStarts, expected,
                Math.sqrt(months * WorldMoneys.RISK_OFF_RATE * (1 - WorldMoneys.RISK_OFF_RATE)));
        // the risk-offs the draws made: their sizes are in the dials' range
        double smallest = Double.POSITIVE_INFINITY, largest = 0;
        int sizes = 0;
        for (long seed = BASE_SEED + 1; seed <= BASE_SEED + 60; seed++) {
            WorldMoneys x = sc.moneys(Nations.Path.RANDOM, seed, null);
            double before = 0;
            for (int t = 1; t <= PATH_MONTHS; t++) {
                x.step(t, null);
                if (x.riskOffStarted()) {
                    double s = x.riskOff() - before * phiRisk;
                    smallest = Math.min(smallest, s);
                    largest = Math.max(largest, s);
                    sizes++;
                }
                before = x.riskOff();
            }
        }
        assertTrue(String.format("the %d risk-offs the draws made over 60 paths have a size in [%.1f, %.1f]: the smallest %.3f, the largest %.3f", sizes, WorldMoneys.RISK_OFF_SIZE_MIN,
                WorldMoneys.RISK_OFF_SIZE_MAX, smallest, largest), smallest >= WorldMoneys.RISK_OFF_SIZE_MIN - EXACT && largest <= WorldMoneys.RISK_OFF_SIZE_MAX + EXACT && largest > 0);
    }

    /* =====================================================================
       6. THE TERMS OF TRADE
       ===================================================================== */

    /** The nation of the most commodity-heavy exports that is the anchor of its money and floats: Canada on the full file, whatever the fixture has. */
    static int findExporter(Scope sc, WorldMoneys w, Nations n) {
        int best = 0;
        for (int i = 1; i <= n.count(); i++) {
            String money = n.year(i, Nations.FIRST_YEAR + 1).money();
            if (w.anchorOf(money) == i && w.pegAnchor(money) == null && !money.equals(Nations.DOLLAR) && sc.shares[i - 1] > (best == 0 ? 0 : sc.shares[best - 1])) best = i;
        }
        return best;
    }

    static void theTermsOfTrade(Scope sc) {
        out.println("--- the terms of trade (" + sc.name + ") ---");
        Map<String, String> regimes = sc.name.equals("fixture") ? FIXTURE_PEGS : null;   // the fixture has no pegs: give it two, to see that the terms of trade move neither
        Nations n = sc.nations(Nations.Path.RANDOM, BASE_SEED, regimes);
        WorldMoneys w = sc.moneys(n);
        int can = sc.nation("CAN"), exporter = can > 0 && sc.shares[can - 1] > 0 ? can : findExporter(sc, w, n);
        assertTrue("there is a commodity exporter whose money floats on its own prices: " + n.iso3(exporter) + ", commodity share " + String.format("%.3f", sc.shares[exporter - 1]), exporter > 0 && sc.shares[exporter - 1] > 0);
        String money = n.year(exporter, Nations.FIRST_YEAR + 1).money();
        double share = sc.shares[exporter - 1], rise = .1, expected = WorldMoneys.TOT_PASS * share * rise;
        out.printf("  finding: %s's commodity share of exports is %.3f, so its t is TOT_PASS x share = %.3f: a rise of %.1f in its ln terms of trade moves the %s by %.3f%% in the end (t = month 12, January 2001)%n",
                n.iso3(exporter), share, WorldMoneys.TOT_PASS * share, rise, money, 100 * expected);
        w.quiet();
        int count = n.count();
        w.recordTermsOfTrade(new double[count]);
        for (int t = 1; t <= 11; t++) w.step(t, null);
        double[] up = new double[count];
        up[exporter - 1] = rise;
        Tally mine = new Tally(), others = new Tally(), pegsHeld = new Tally();
        int third = WorldMoneys.TOT_MONTHS;
        double[] expect = new double[6];   // January..June of 2001: a third each of the first TOT_MONTHS, then what has been reached
        for (int k = 0; k < expect.length; k++) expect[k] = -expected * Math.min(k + 1, third) / third;
        List<String> floats = floats(w);
        for (int k = 0; k < expect.length; k++) {
            int t = 12 + k;
            w.step(t, k == 0 ? up : null);
            for (String c : floats) {
                double moved = Math.log(w.rate(c) / w.ppp(c));
                int kk = k;
                if (c.equals(money)) mine.see(Math.abs(moved - expect[k]) <= EXACT && Math.abs(w.totReached(c) - expect[k]) <= EXACT, () -> c + " month " + t + " moved " + moved + ", expected " + expect[kk]);
                else others.see(Math.abs(moved) <= EXACT, () -> c + " month " + t + " moved " + moved);
            }
            for (String c : w.codes()) if (w.pegAnchor(c) != null) pegsHeld.see(w.pegHeld(c), () -> c);
        }
        tally("the " + money + " is dearer by TOT_PASS x share x " + rise + " = " + String.format("%.5f", expected) + ": a third in January, February and March, the whole by March, and not a bit more in April, May or June", mine);
        tally("no other float moves: " + (floats.size() - 1) + " moneys, six months, to " + EXACT, others);
        // the change, not the level: the same figure again in January 2002 is no news; a null January leaves it; a fall of the same size takes it back
        w.step(18, null);
        for (int t = 19; t < 24; t++) w.step(t, null);
        w.step(24, up);
        for (int t = 25; t <= 28; t++) w.step(t, null);
        double kept = Math.log(w.rate(money) / w.ppp(money));
        assertTrue("the same ln terms of trade the next January is no change: the money stays where it was (" + String.format("%.5f", kept) + " against " + String.format("%.5f", -expected) + ")", Math.abs(kept + expected) <= EXACT);
        for (int t = 29; t < 36; t++) w.step(t, null);
        w.step(36, null);
        for (int t = 37; t <= 40; t++) w.step(t, null);
        double nulled = Math.log(w.rate(money) / w.ppp(money));
        assertTrue("a January with no solve (null) moves nothing: still " + String.format("%.5f", nulled), Math.abs(nulled + expected) <= EXACT);
        for (int t = 41; t < 48; t++) w.step(t, null);
        w.step(48, new double[count]);
        for (int t = 49; t <= 52; t++) w.step(t, null);
        double back = Math.log(w.rate(money) / w.ppp(money));
        assertTrue("a January in which the ln terms of trade is back where it was takes the money back too: " + String.format("%.2e", back) + " from parity", Math.abs(back) <= EXACT);
        tally("the pegs are all still held through it (the terms of trade do not break a peg)", pegsHeld);
        assertTrue("the market's commodity share is " + share + " for " + n.iso3(exporter) + " and the model gives it back as " + w.commodityShare(exporter), w.commodityShare(exporter) == share);
        theMarketReads(sc);
    }

    /** The market's two reads held to the file's pairs: the share of exports in bulk goods and the ln terms of trade, recomputed from the flows. */
    static void theMarketReads(Scope sc) {
        WorldData data = sc.data;
        int n = data.nationCount();
        List<Good> goods = sc.base.goods();
        double[][] exports = new double[goods.size()][n], imports = new double[goods.size()][n], make = new double[goods.size()][n];
        boolean[] bulk = new boolean[goods.size()];
        for (int g = 0; g < goods.size(); g++) {
            int number = data.goodNumber(goods.get(g).name());
            bulk[g] = "bulk".equals(data.good(number).goodClass());
            WorldData.Flows f = data.trade(number);
            for (int p = 0; p < f.pairs(); p++) {
                int s = f.seller[p] - 1, b = f.buyer[p] - 1;
                double x = f.flow[p];
                if (s < 0 || s >= n || b < 0 || b >= n || !(x > 0) || !Double.isFinite(x)) continue;
                make[g][s] += x;
                if (s != b) {
                    exports[g][s] += x;
                    imports[g][b] += x;
                }
            }
        }
        Tally shares = new Tally();
        double largest = 0;
        for (int i = 0; i < n; i++) {
            double all = 0, bulkSum = 0;
            for (int g = 0; g < goods.size(); g++) {
                all += exports[g][i];
                if (bulk[g]) bulkSum += exports[g][i];
            }
            double expect = all > 0 ? bulkSum / all : 0;
            int nation = i;
            shares.see(Math.abs(sc.shares[i] - expect) <= 1e-9 && sc.shares[i] >= 0 && sc.shares[i] <= 1 + 1e-12, () -> data.nation(nation + 1).iso3() + ": " + sc.shares[nation] + " against " + expect);
            largest = Math.max(largest, sc.shares[i]);
        }
        tally("Base.commodityShare is each nation's base-year exports in bulk goods over all its exports, recomputed from the file's pairs (all " + n + " nations, within 1e-9)", shares);
        // the terms of trade, from the solved prices
        Nations h = sc.nations(Nations.Path.HISTORICAL, BASE_SEED, null);
        WorldMarket market = new WorldMarket(sc.base, h);
        double[] before = market.lnTermsOfTrade();
        boolean zeros = before.length == n;
        for (double x : before) zeros &= x == 0;
        assertTrue("before any year is solved every price is the base year's: the ln terms of trade are 0 for all " + n + " nations", zeros);
        int baseYear = sc.base.baseYear;
        for (int year : new int[] {baseYear, 2008}) {
            market.solveYear(year);
            double[] ln = market.lnTermsOfTrade();
            double[] prices = market.savedPrices();
            Tally recomputed = new Tally();
            for (int i = 0; i < n; i++) {
                double exp = 0, expW = 0, imp = 0, impW = 0;
                for (int g = 0; g < goods.size(); g++) {
                    double worldNum = 0, worldDen = 0, makeNum = 0, makeDen = 0;
                    for (int j = 0; j < n; j++) {
                        double p = prices[g * n + j];
                        worldNum += exports[g][j] * p;
                        worldDen += exports[g][j];
                        makeNum += make[g][j] * p;
                        makeDen += make[g][j];
                    }
                    double world = worldDen > 0 ? worldNum / worldDen : makeDen > 0 ? makeNum / makeDen : 1;
                    exp += exports[g][i] * prices[g * n + i];
                    expW += exports[g][i];
                    imp += imports[g][i] * world;
                    impW += imports[g][i];
                }
                double expect = expW > 0 && impW > 0 ? Math.log((exp / expW) / (imp / impW)) : 0;
                int nation = i;
                recomputed.see(Double.isFinite(ln[i]) && Math.abs(ln[i] - expect) <= 1e-9, () -> data.nation(nation + 1).iso3() + ": " + ln[nation] + " against " + expect);
            }
            tally("WorldMarket.lnTermsOfTrade in " + year + " is the log of the export price index over the import price index, recomputed from the file's pairs and the solved prices (all nations, 1e-9)", recomputed);
            if (year == baseYear) {
                double worst = 0;
                for (double x : ln) worst = Math.max(worst, Math.abs(x));
                assertTrue(String.format("in the base year %d every price is the base year's to the solve's tolerance, so no nation's terms of trade has moved (the largest |ln| %.2e)", year, worst), worst < 10 * WorldMarket.SOLVE_TOL);
            } else {
                double widest = 0;
                String who = "";
                for (int i = 0; i < n; i++) if (Math.abs(ln[i]) > widest) { widest = Math.abs(ln[i]); who = data.nation(i + 1).iso3(); }
                int can = data.numberOf("CAN");
                out.printf("  finding: in %d the widest ln terms of trade against %d is %.3f (%s)%s%n", year, baseYear, widest, who, can > 0 ? ", Canada's " + String.format("%.3f", ln[can - 1]) : "");
                assertTrue("in 2008 the commodity boom moved some nation's terms of trade (the widest |ln| above 0.01)", widest > .01);
            }
        }
    }

    /* =====================================================================
       7. CROSS RATES
       ===================================================================== */

    static void theCross(Scope sc) {
        out.println("--- cross rates go through the dollar (" + sc.name + ") ---");
        Tally cross = new Tally(), dollar = new Tally(), triangle = new Tally();
        for (Nations.Path path : Nations.Path.values()) {
            WorldMoneys w = sc.moneys(path, BASE_SEED, null);
            for (int t = 0; t <= LONG_MONTHS; t++) {
                if (t > 0) w.step(t, null);
                int tt = t;
                dollar.see(w.rate(Nations.DOLLAR) == 1, () -> path + " month " + tt);
                for (String a : WorldMoneys.EIGHT) {
                    for (String b : WorldMoneys.EIGHT) {
                        cross.see(w.cross(a, b) == w.rate(b) / w.rate(a), () -> path + " month " + tt + " " + a + "/" + b);
                        for (String c : new String[] {"EUR", "JPY"})
                            triangle.see(Math.abs(w.cross(a, b) * w.cross(b, c) / w.cross(a, c) - 1) <= EXACT, () -> path + " month " + tt + " " + a + "/" + b + "/" + c);
                    }
                }
            }
        }
        tally("cross(a, b) is rate(b) / rate(a) for the eight, in every month 0-" + LONG_MONTHS + " of both paths", cross);
        tally("the dollar's rate is 1 in every month of both paths", dollar);
        tally("a cross through a third money is the cross: cross(a, b) x cross(b, c) = cross(a, c) for the eight and the euro and the yen", triangle);
        assertTrue("the eight in their load-bearing order: " + WorldMoneys.EIGHT, WorldMoneys.EIGHT.equals(List.of("USD", "EUR", "JPY", "GBP", "CNY", "CAD", "AUD", "CHF")));
    }

    /* =====================================================================
       8. PARITY
       ===================================================================== */

    /** ln(P_a / P_US) in a month, log-linear between Januaries, from Nations itself. */
    static double lnRelative(Nations n, int anchor, int usa, int t) {
        int year = Nations.FIRST_YEAR + t / 12;
        double f = (t % 12) / 12.0;
        double a0 = Math.log(n.year(anchor, year).priceLevel()), a1 = Math.log(n.year(anchor, year + 1).priceLevel());
        double u0 = Math.log(n.year(usa, year).priceLevel()), u1 = Math.log(n.year(usa, year + 1).priceLevel());
        return a0 + f * (a1 - a0) - (u0 + f * (u1 - u0));
    }

    static void theParity(Scope sc) {
        out.println("--- parity (" + sc.name + ") ---");
        WorldData data = sc.data;
        for (Nations.Path path : Nations.Path.values()) {
            Nations n = sc.nations(path, BASE_SEED, null);
            WorldMoneys w = sc.moneys(n);
            int usa = n.index(Nations.FRONTIER), first = path == Nations.Path.RANDOM ? 12 : WorldMoneys.HISTORICAL_RULE_START;
            for (int t = 1; t < first - 1; t++) w.step(t, null);
            w.step(first - 1, null);
            w.quiet();
            Tally moves = new Tally();
            List<String> floats = floats(w);
            double[] base = new double[floats.size()];
            for (int t = first; t <= first + 12; t++) {
                w.step(t, null);
                for (int k = 0; k < base.length; k++) {
                    String c = floats.get(k);
                    double ln = Math.log(w.rate(c));
                    if (t == first) base[k] = ln;
                    double expect = lnRelative(n, w.anchorOf(c), usa, t) - lnRelative(n, w.anchorOf(c), usa, first), moved = ln - base[k];
                    int tt = t;
                    moves.see(Math.abs(moved - expect) <= EXACT, () -> c + " in month " + tt + " moved " + moved + ", parity " + expect);
                }
            }
            tally(path + ": quiet, over a year from month " + first + " each of " + floats.size() + " floats' ln rate moves by exactly the change in ln(P_anchor / P_US)", moves);
            // its constant: the mean over the years the anchor was on the money of the file's real gap from the cpis
            Tally constants = new Tally();
            int checked = 0;
            for (String c : WorldMoneys.EIGHT) {
                int a = w.anchorOf(c);
                if (c.equals(Nations.DOLLAR) || a < 1) continue;
                double sum = 0;
                int count = 0;
                for (int y = Nations.FIRST_YEAR; y <= Nations.HISTORY_LAST_YEAR; y++) {
                    if (!c.equals(data.moneyAt(a, (y - Nations.FIRST_YEAR) * 12 + 11))) continue;
                    double r = data.pathValue("rate", a, y), pa = data.pathValue("cpi", a, y), pu = data.pathValue("cpi", usa, y);
                    if (r > 0 && pa > 0 && pu > 0) {
                        sum += Math.log(r) - Math.log(pa / pu);
                        count++;
                    }
                }
                if (count == 0) continue;
                checked++;
                double constant = Math.log(w.ppp(c)) - lnRelative(n, a, usa, w.month());
                double mean = sum / count;
                constants.see(Math.abs(constant - mean) <= EXACT, () -> c + ": " + constant + " against the file's mean gap " + mean);
            }
            tally(path + ": each of the eight's parity is anchored on the mean real gap of the file's years (" + checked + " moneys), so 2000-2001's strong dollar is a gap the swings close, not parity", constants);
        }
    }

    /* =====================================================================
       9. WHAT IT COSTS
       ===================================================================== */

    static void theCost(Scope sc) {
        out.println("--- what it costs (" + sc.name + ") ---");
        long[] ns = new long[3 * PATH_MONTHS];
        for (int rep = 0; rep < 3; rep++) {
            WorldMoneys w = sc.moneys(Nations.Path.RANDOM, BASE_SEED + rep, null);
            for (int t = 1; t <= PATH_MONTHS; t++) {
                long s = System.nanoTime();
                w.step(t, null);
                ns[rep * PATH_MONTHS + t - 1] = System.nanoTime() - s;
            }
        }
        long[] sorted = ns.clone();
        Arrays.sort(sorted);
        double median = sorted[sorted.length / 2] / 1e3, p90 = sorted[sorted.length * 9 / 10] / 1e3;
        WorldMoneys w = sc.moneys(Nations.Path.RANDOM, BASE_SEED, null);
        out.printf("  finding: %d moneys, a month's step: median %.1f us, 90th percentile %.1f us, worst %.1f us (the study's prototype: about 10 us a month)%n", w.codes().size(), median, p90, sorted[sorted.length - 1] / 1e3);
        assertTrue(String.format("the median month's step is under %.0f ms (a generous bound: the gate's timing is not this harness's)", STEP_BOUND_MS), median / 1e3 < STEP_BOUND_MS);
    }

    /* =====================================================================
       THE FULL FILE'S OWN FINDINGS
       ===================================================================== */

    static void theFullOnly(Scope all, Pass pass) {
        out.println("--- the full file's own: the commodity shares, the boards, the pegs ---");
        WorldData data = all.data;
        Integer[] order = new Integer[all.shares.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Double.compare(all.shares[b], all.shares[a]));
        StringBuilder top = new StringBuilder();
        for (int j = 0; j < 10; j++) top.append(data.nation(order[j] + 1).iso3()).append(String.format(" %.3f", all.shares[order[j]])).append(j < 9 ? ", " : "");
        out.println("  finding: the ten largest commodity shares of exports: " + top);
        int can = data.numberOf("CAN"), aus = data.numberOf("AUS"), deu = data.numberOf("DEU");
        out.printf("  finding: Canada's is %.3f, Australia's %.3f, Germany's %.3f (the study: Canada's about a quarter)%n", all.shares[can - 1], all.shares[aus - 1], all.shares[deu - 1]);
        assertTrue("Canada exports more commodities than Germany, and Australia more than Canada", all.shares[can - 1] > all.shares[deu - 1] && all.shares[aus - 1] > all.shares[can - 1]);
        String[] boards = WorldMoneys.CURRENCY_BOARDS.stream().sorted().toArray(String[]::new);
        WorldMoneys w = all.moneys(Nations.Path.RANDOM, BASE_SEED, null);
        Tally there = new Tally();
        for (String b : boards) there.see(w.isBoard(b) && w.pegHeld(b), () -> b + " is not a held board in the file");
        tally("the file's currency boards " + Arrays.toString(boards) + " are pegs held from the start and boards", there);
        out.println("  " + pass.pegs.length + " moneys are pegged or tied from January 2000 on Random (the full file's regimes)");
    }
}
