package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The world market (batch WN3; the project's spec-nations.md 4): the nations'
 * January solve held to the equations it solves, the calibration held to the
 * game's literals, and the city's band held to what makes it a band - the
 * floor under the ceiling, a small city a price-taker and a big one not, the
 * month's step near a re-solve with the city's tonnes in.
 *
 * WHY. The world was a price list, and the market that replaces it can be
 * wrong in ways no city would show: a solve that stops before the makers clear
 * (every price off by a percent and nobody the wiser), a calibration that
 * leaves Halifax a different band from the literals the whole economy was
 * balanced on (D12), a floor above a ceiling (a city that makes money buying
 * and selling the same tonne to itself), a band that does not move with the
 * city's own tonnes, a restored save that gives another band. Each is held to
 * a figure the model does not compute for itself: the residuals are
 * recomputed here from the file's pairs, the fill is solved a second way, the
 * literals are Good's.
 *
 * THE DATA. On the fixture (WorldData.FIXTURE, twelve nations) always; on the
 * full world-1.bin where it is named (-Dcbs.world=<path>, WorldData.locate()),
 * for every section, and a line says when it is not.
 *
 * What this has to prove:
 *   1. THE CHAINED SOLVE (CU2; WorldChain, spec-world-prices 3): the January
 *      solves the goods inputs first, cold at the founding (the calendar's first
 *      year, and again at the file's first, 2000) and warm after, on both paths,
 *      and holds three claims, each to a figure recomputed from the file's pairs
 *      and the freight table: a root's reference band stands on its price path
 *      (the anchor); a made good is what its inputs cost the nation landed, less
 *      the copper charge, plus its own part, which clears and meets the
 *      manufactures index's level to SOLVE_TOL; every maker clears (a root's sales
 *      over supply, a made good's supply over its sales). The sweeps are counted.
 *   2. THE CALIBRATION: the reference city (Halifax, 2025, Historical) sees
 *      Good's literals on every calibrated side to 1e-9, except concentrate and
 *      anode, which carry the played year's charge and not 2024's the literals
 *      stand on (call star 6 (a)), and are held to the charge's difference; no
 *      two-sided good goes without; a good that does not calibrate keeps the
 *      freight scale the plain pass gave it, its class's median.
 *   3. THE FLOOR IS NEVER ABOVE THE CEILING: every good, 2000-2040, both
 *      paths, at three sites, the city's exports and imports each 0, 0.1%,
 *      1%, 10% and 50% of the world's use; and the floor falls with the
 *      city's exports as the ceiling rises with its imports.
 *   4. A CITY MOVES ITS PRICE AS BIG AS IT IS: at 0.01% of a good's world use
 *      (an ordinary city) the netback moves by under 1% of the price it is
 *      sold at, at 10% by at least 5% of it, every good; at 0.1% the move is
 *      printed (the re-solve agrees with it: the host's market is small).
 *   5. THE MONTH'S STEP IS NEAR A RE-SOLVE (a made good's makers each supply
 *      what they sell at its chained price, so its re-solve starts from a scale
 *      a maker): the fill's netback (the band's floor)
 *      within 3% of the price it is sold at of the nations' solve again with the
 *      city's tonnes delivered. At 0.1% of world use every good is; at 1% the
 *      median is and lumber, crude, iron and grains each are; 5% and 10% are
 *      printed, the step's reach beyond a giant city's 1%, and so is lumber at
 *      all four volumes (the study's 4.4 table).
 *   6. SEEDS: two seeds differ, one seed repeats to the bit, Historical's 2025
 *      is the same on any seed.
 *   7. THE SAVE: prices and scales restored give the bands and allocations of
 *      the market they came from, to the bit, and a wrong size is refused.
 *   8. THE STAND-IN: a generated world's site is a coastal nation drawn by its
 *      output, the same each time.
 *   9. THE PIECES: the fill against a plain water-filling, the allocation
 *      against its volumes, a berth never dearer, a nation's buyer price never
 *      above its own mill price.
 *  10. THE WOOD CASE (full file; printed, not asserted): a Halifax city's
 *      lumber buyers and where the others join.
 *  11. THE TIMINGS: the Base, the founding solve, a January, a month's bands.
 *  12. THE CHAIN HOLDS (full file; printed on the fixture): at the reference
 *      site and at a generated world's stand-in, every fifth January 1900-2200
 *      on the Historical path and on Random seeds 1, 5, 9 and 48213, rod stands
 *      on cathode (each good's floor over its literal export price, rod's over
 *      cathode's within ROD_ON_CATHODE of 1 every January), wire on rod and
 *      every works' feed over its sales on its literal (the median of all the
 *      Januaries within WIRE_ON_ROD_MEDIAN and WORKS_MEDIAN of 1: a single
 *      draw's January parts wider, printed with its path and year).
 *  13. THE RECIPE TABLE IS buildings.json's: each WorldChain.RECIPE row's shares
 *      are the works' uses over its sales (all it makes) at the literals'
 *      mid-band prices, to RECIPE_TOL; wire's 0.70 rod (Jerus's call) is
 *      printed, not asserted.
 *  14. THE GUARD KEEPS THE SOLVE TO THE NUMBERS: a good's solve started with a
 *      maker's price that is not a number is put back by the guard and ends
 *      clear; on the full file a drawn world whose crude left the numbers on
 *      main is played January by January and stops no good at SOLVE_MAX_PASSES
 *      (the chained solve starts a root on the year's level and anchors it, and
 *      no longer draws the guard there: printed).
 *  15. THE COPPER CHARGE: Nations' swing has its large peaks on the record's
 *      years, stays in its band, is a pure function and moves with the seed,
 *      and has no yearly record; in a solved January concentrate carries it,
 *      anode REFINING_PART of it, and no other good any.
 *
 * Every fixture causes its condition: the city's volumes are shares of the
 * good's own world use, a seed is drawn so that two worlds differ, a save is
 * restored into a market that has solved nothing.
 */
public class WorldMarketCheck {

    static int fails = 0;
    /** Where the harness prints: the console. */
    static final PrintStream out = System.out;

    /** What the band timing adds up, so that the optimiser cannot drop the calls it is timing. */
    static double sink;

    /** The seed the single-world sections use. */
    static final long BASE_SEED = 11;

    /** The city's yearly exports and imports in sections 3 and 4, as shares of the good's world use. */
    static final double[] SHARES = {0, .001, .01, .1, .5};

    /** The volumes of section 5, shares of the world's use. */
    static final double[] STEPS = {.001, .01, .05, .10};

    /** The month's step is within this share of the price (the served buyers' landed price, tonne-weighted) of the re-solve's figure: a freight-heavy good's netback is a small remainder of its price, so a share of the netback is not what the row means. */
    static final double STEP_TOL = .03;

    /** An ordinary city (0.01% of the world's use) moves its netback by less than this share of the price it sells at, a big one (10%) by at least BIG_MOVE of it. */
    static final double SMALL_MOVE = .01, BIG_MOVE = .05;

    /** The shares of a good's world use section 4 reads: an ordinary city's, a large mill town's (printed only: the host's market is small), and a big city's. */
    static final double ORDINARY_SHARE = .0001, LARGE_SHARE = .001, BIG_SHARE = .1;

    /** The goods whose step is asserted within STEP_TOL at 1% of world use, beside the median: the study's and the brief's four. */
    static final Good[] ONE_PERCENT = {Good.LUMBER, Good.CRUDE, Good.IRON, Good.GRAINS};

    /** Two figures that should be one are the same within this share. */
    static final double SAME = 1e-9;

    /** Seeds drawn for the stand-in. */
    static final int STAND_IN_SEEDS = 2_000;

    /** A frequency is judged within this many standard deviations of a binomial. */
    static final double SIGMAS = 4.5;

    /** Two figures that are one piece of arithmetic done twice (a price that is the sum of its parts, a ratio of two charges) agree within this share. */
    static final double ARITHMETIC = 1e-12;

    /** A root's reference band stands on its path's target within this, in logs: ten times the anchor's own stop (WorldMarket.ANCHOR_TOL), the worst measured on these years being 9.4e-11 (a band off its path by percents in main). */
    static final double ANCHOR_CHECK_TOL = 10 * WorldMarket.ANCHOR_TOL;

    /** The played 2025 band of concentrate or anode moves off its literal by the charge's whole difference, within this share of it: the worst measured is 6.0e-14 of it. */
    static final double CHARGE_ROOM = 1e-12;

    /** Rod's floor over its literal export price stands within this of cathode's in every January read: the worst measured is 0.0933 (Random seed 9, the reference site, 1900), main's world stands 1.4 off (rod 2.41 and 2.68 over cathode, spec-world-prices 3.2). */
    static final double ROD_ON_CATHODE = .10;

    /** The median over every January read of wire's floor over its literal export price, over rod's, stands within this of 1: the measured median is 1.038, main's world stands 0.12 to 0.18 off (0.82 and 0.88, 3.2); the worst January is 0.98 off (Random seed 9, 1900) since wire's 30% own part follows the manufactures index and rod the copper path, so it is the median that holds the study's claim. */
    static final double WIRE_ON_ROD_MEDIAN = .05;

    /** A works' median feed over its sales, over every January read, stands within this share of its literal margin: the widest measured is 0.074 (Panel Mill and Pulp Mill), main's Rod Mill stands 0.55 off (0.46 for 1.03, 3.3); the worst single January is far wider (Fabrication Works 12.4 times its literal on Random seed 5 in 2145), the study's table being the drawn worlds' median. */
    static final double WORKS_MEDIAN = .10;

    /** A recipe row's share of an input is within this (absolute) of the works' uses and makes at the literals' mid-band prices: a few points, as the dial is rounded to the hundredth. */
    static final double RECIPE_TOL = .03;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-130s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-130s %s  %s%n", label, Timing.verdict(ok, label), detail);
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

    static boolean near(double a, double b, double rel) { return Math.abs(a - b) <= rel * Math.abs(b); }

    static boolean same(double a, double b) { return Double.doubleToLongBits(a) == Double.doubleToLongBits(b); }

    static boolean same(double[] a, double[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) if (!same(a[i], b[i])) return false;
        return true;
    }

    /** The world file and what is built on it: the fixture or the full file. */
    static final class Scope {
        final String name;
        final WorldData data;
        final WorldMarket.Base base;
        final double baseMillis;

        Scope(String name, WorldData data, WorldMarket.Base base, double baseMillis) {
            this.name = name;
            this.data = data;
            this.base = base;
            this.baseMillis = baseMillis;
        }

        WorldMarket market(Nations.Path path, long seed) { return new WorldMarket(base, new Nations(data, path, seed)); }
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        byte[] bytes = WorldDataCheck.resource(WorldData.FIXTURE);
        if (bytes == null) {
            assertTrue("no fixture: " + WorldData.FIXTURE + " is not beside the classes", false);
            System.out.println("\n" + fails + " FAILED");
            System.exit(1);
        }
        // the full file's Base is built first, while the JVM is cold: BASE_MS is the game's wait, not a warm process's
        Path fullPath = WorldData.locate();
        String named = System.getProperty(WorldData.PROPERTY);
        boolean fullThere = fullPath != null && Files.isRegularFile(fullPath);
        WorldMarket.Base fullBase = null;
        double fullMillis = 0;
        if (fullThere) {
            long t1 = System.nanoTime();
            fullBase = WorldMarket.Base.load(fullPath);
            fullMillis = (System.nanoTime() - t1) / 1e6;
        }
        WorldData fixture = WorldData.of(bytes);
        long t0 = System.nanoTime();
        Scope fx = new Scope("fixture", fixture, WorldMarket.Base.of(fixture), (System.nanoTime() - t0) / 1e6);
        sections(fx);
        theRecipeTable();

        out.println("--- the full file, when it is named ---");
        if (named != null && !named.isBlank() && !fullThere) {
            assertTrue("the file -D" + WorldData.PROPERTY + " names is there (" + named + ")", false);
        } else if (!fullThere) {
            out.println("world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": its sections did not run");
        } else {
            out.println("reading " + fullPath.toAbsolutePath());
            try (WorldData full = WorldData.open(fullPath)) {
                Scope all = new Scope("full", full, fullBase, fullMillis);
                sections(all);
                theWoodCase(all);
            }
        }
        out.printf("%nWorldMarketCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * EP1: the base year growth() and level() read. The trade table is the file's base year (2019); a Random path begins from the file's first year's world
     * in the calendar's first year, so the year that table stands for is the base year less the stand-in's years (1919), as far into the path as it was
     * before the calendar moved; Historical's stand-in holds the file's first year's world and reads the base year itself. Caused on both paths: the
     * year read, and the growth and the price target the market solves against at the founding, a year on and a file year, each recomputed from Nations.
     */
    static void theBaseYear(Scope sc) {
        out.println("--- the base year the market reads: Random's is the table's less the stand-in's years, Historical's the table's (" + sc.name + ") ---");
        int standIn = sc.data.standInYears();
        assertTrue("fixture: the data has a stand-in century to move the base year by (" + standIn + " years)", standIn > 0);
        Tally year = new Tally(), growth = new Tally(), level = new Tally(), apart = new Tally();
        for (Nations.Path path : Nations.Path.values()) {
            Nations nations = new Nations(sc.data, path, BASE_SEED);
            WorldMarket m = new WorldMarket(sc.base, nations);
            int want = path == Nations.Path.HISTORICAL ? sc.base.baseYear : sc.base.baseYear - standIn;
            year.see(m.baseYearRead() == want, () -> path + " reads " + m.baseYearRead() + ", wanted " + want);
            int[] idx = WorldMarket.priceIndices(nations, sc.base.traded);
            for (int solved : new int[] {Nations.FIRST_YEAR, Nations.FIRST_YEAR + 7, WorldData.FILE_FIRST_YEAR + 5}) {
                m.solveYear(solved);
                double[] g = WorldMarket.growth(nations, solved, want, sc.base.n);
                growth.see(Arrays.equals(m.gHat(), g), () -> path + " " + solved + ": the growth solved against is not output over the output of " + want);
                for (int gi = 0; gi < idx.length; gi++) {
                    int good = gi;
                    level.see(m.levelTarget(gi) == WorldMarket.level(nations, idx[gi], solved, want), () -> path + " " + solved + ": good " + good + "'s price target is not over the price of " + want);
                }
                if (path != Nations.Path.HISTORICAL && solved == Nations.FIRST_YEAR)
                    apart.see(!Arrays.equals(g, WorldMarket.growth(nations, solved, sc.base.baseYear, sc.base.n)), () -> "the founding's growth is the same whichever base year is read");
            }
        }
        tally("the market reads the table's base year on Historical and the base year less the data's standInYears() on Random", year);
        tally("...the growth it solves against is each nation's output in the year over its output in that year", growth);
        tally("...and each good's price target is its real price in the year over its price in that year", level);
        tally("...and on Random the founding's growth is not the table's year's (the cause is there to read)", apart);
    }

    static void sections(Scope sc) {
        theSolve(sc);
        theBaseYear(sc);
        theCalibration(sc);
        theFloorUnderCeiling(sc);
        theBigCity(sc);
        theStep(sc);
        theSeeds(sc);
        theSave(sc);
        theStandIn(sc);
        thePieces(sc);
        theTimings(sc);
        theChainHolds(sc);
        theGuard(sc);
        theCopperCharge(sc);
    }

    /* =====================================================================
       1. THE CHAINED SOLVE
       ===================================================================== */

    /** The largest of a figure over many readings, and where it was read. */
    static final class Worst {
        double v;
        String at = "";

        void see(double x, Supplier<String> where) {
            if (x > v || Double.isNaN(x)) {
                v = x;
                at = where.get();
            }
        }

        @Override public String toString() { return String.format(Locale.ROOT, "%.3g (%s)", v, at); }
    }

    /** What a good's makers made at the base year's prices (thousand US$), summed from the file's pairs. */
    static double[] madeIn(WorldData data, WorldMarket.Traded t, int n) {
        WorldData.Flows f = data.trade(t.number);
        double[] make = new double[n];
        for (int k = 0; k < f.pairs(); k++) make[f.seller[k] - 1] += f.flow[k];
        return make;
    }

    /**
     * Each maker's sales of a good (thousand US$ of the base year) at the prices p it asks (a good's phat, over the base year's), recomputed from the
     * file's pairs alone: p^-eps times the pair's flow times the buyer's spending over its price index, as the solve has it; gh is the nations' growth.
     */
    static double[] sales(WorldData data, WorldMarket.Traded t, double[] p, double[] gh) {
        WorldData.Flows f = data.trade(t.number);
        int n = p.length;
        double eps = t.eps;
        double[] use = new double[n], phi = new double[n], sales = new double[n];
        for (int k = 0; k < f.pairs(); k++) use[f.buyer[k] - 1] += f.flow[k];
        for (int k = 0; k < f.pairs(); k++) {
            int s = f.seller[k] - 1, b = f.buyer[k] - 1;
            phi[b] += f.flow[k] / use[b] * Math.pow(p[s], -eps);
        }
        for (int k = 0; k < f.pairs(); k++) {
            int s = f.seller[k] - 1, b = f.buyer[k] - 1;
            double spending = gh[b] * Math.pow(phi[b], -(1 - WorldMarket.DEMAND_ELASTICITY) / eps);
            sales[s] += Math.pow(p[s], -eps) * f.flow[k] * spending / phi[b];
        }
        return sales;
    }

    /** One good's solved state recomputed from the file's pairs alone: {the worst of sales over supply less 1 over the makers, the mean mill price over its path's level less 1 in magnitude}. */
    static double[] residual(WorldData data, WorldMarket m, Good g) {
        int gi = m.indexOf(g);
        WorldMarket.Traded t = m.base().traded[gi];
        int n = m.nationCount();
        double[] p = m.phat(gi), gh = m.gHat();
        double lambda = m.lambda(gi);
        double[] make = madeIn(data, t, n), sales = sales(data, t, p, gh);
        double worst = 0, total = 0, index = 0;
        for (int i = 0; i < n; i++) {
            if (!(make[i] > 0)) continue;
            double supply = make[i] * gh[i] * lambda * Math.pow(p[i], 1 + WorldMarket.SUPPLY_ELASTICITY);
            worst = Math.max(worst, Math.abs(sales[i] / supply - 1));
            total += make[i];
            index += make[i] * p[i];
        }
        return new double[] { worst, Math.abs(index / total / m.levelTarget(gi) - 1) };
    }

    /**
     * What a good costs each nation over what it cost in the base year, recomputed from the market's finished prices and the freight table: the cheapest
     * maker's mill price landed there (its price over the base year's, times its unit value, plus the good's freight scale times the maker's freight),
     * over the same at base-year prices; 1 where no maker reaches the nation. The freight scale is the good's, WorldMarket.PHI_FALLBACK where it has none.
     */
    static double[] landed(WorldMarket m, int pg) {
        WorldMarket.Base base = m.base();
        WorldMarket.Traded t = base.traded[pg];
        int n = base.nationCount();
        double[] ph = m.phat(pg), cost = new double[n];
        double phi = t.phi > 0 ? t.phi : WorldMarket.PHI_FALLBACK;
        for (int i = 0; i < n; i++) {
            double now = Double.POSITIVE_INFINITY, then = Double.POSITIVE_INFINITY;
            for (int k : t.makers) {
                double f = phi * base.freight[k * n + i];
                now = Math.min(now, t.v * ph[k] + f);
                then = Math.min(then, t.v + f);
            }
            cost[i] = now < Double.POSITIVE_INFINITY && then > 0 ? now / then : 1;
        }
        return cost;
    }

    /**
     * What a made good's inputs cost in each of its makers' nations, less the copper charge where the good carries one (never below WorldMarket.CHARGE_LEAVES
     * of the inputs' cost): the sum over its inputs of the recipe's share times what the input costs the nation (landed above); landed is the year's
     * cache of that, by good.
     */
    static double[] inputsOf(WorldMarket m, int gi, double[][] landed) {
        WorldMarket.Base base = m.base();
        WorldMarket.Traded t = base.traded[gi];
        WorldChain chain = base.chain;
        double[] cost = new double[base.nationCount()];
        for (int k = 0; k < chain.parent[gi].length; k++) {
            int pg = chain.parent[gi][k];
            if (landed[pg] == null) landed[pg] = landed(m, pg);
            for (int i : t.makers) cost[i] += chain.share[gi][k] * landed[pg][i];
        }
        double chi = m.charge(t.good);
        if (chi > 0) for (int i : t.makers) cost[i] = Math.max(WorldMarket.CHARGE_LEAVES * cost[i], cost[i] - chi);
        return cost;
    }

    /**
     * The anchor's read of a root, recomputed from its makers' solved prices: the reference city's band over the band the calibration left it, a calibrated
     * good by the geometric middle of its two sides, a one-sided good by its calibrated side; NaN where the side is not above 0 (a price below its freight:
     * the anchor leaves such a year as the solve had it).
     */
    static double anchorRead(WorldMarket m, int gi) {
        WorldMarket.Base base = m.base();
        WorldMarket.Traded t = base.traded[gi];
        double[] ph = m.phat(gi), p = new double[ph.length];
        for (int i = 0; i < p.length; i++) p[i] = t.v * ph[i];
        double[] fl = base.referenceBand(t, p, t.phi);
        if (t.fit == WorldMarket.Fit.CALIBRATED) {
            double f = t.sigma * fl[0] / t.refFloor, c = t.sigma * fl[1] / t.refCeiling;
            return f > 0 && c > 0 && Double.isFinite(f) && Double.isFinite(c) ? Math.sqrt(f * c) : Double.NaN;
        }
        double s = t.good.importable() ? t.sigma * fl[1] / t.refCeiling : t.sigma * fl[0] / t.refFloor;
        return s > 0 && Double.isFinite(s) ? s : Double.NaN;
    }

    static void theSolve(Scope sc) {
        out.println("--- the chained January solve: a root's band meets its path, a made good is its inputs plus its own part, every maker clears (" + sc.name + ") ---");
        WorldChain chain = sc.base.chain;
        int n = sc.base.nationCount();
        Tally anchored = new Tally(), assembled = new Tally(), clearing = new Tally(), years = new Tally();
        Worst anchorMiss = new Worst(), ownClear = new Worst(), ownLevel = new Worst(), priceMiss = new Worst(), supplyMiss = new Worst();
        List<String> left = new ArrayList<>();
        List<Integer> cold = new ArrayList<>(), warm = new ArrayList<>();
        double tol = WorldMarket.SOLVE_TOL * (1 + 1e-9);
        // Two foundings per path: the calendar's first year (EP1: the stand-in century's, where a city begins) and the file's own first, 2000 (real years).
        for (Nations.Path path : Nations.Path.values()) {
            for (int start : new int[] {Nations.FIRST_YEAR, WorldData.FILE_FIRST_YEAR}) {
                WorldMarket m = sc.market(path, BASE_SEED);
                for (int y = start; y <= start + 10; y++) {
                    m.solveYear(y);
                    int yy = y, first = start;
                    years.see(m.solvedYear() == y, () -> "solved " + m.solvedYear() + " for " + yy);
                    double[][] landed = new double[sc.base.traded.length][];
                    double[] gh = m.gHat();
                    for (Good g : sc.base.goods()) {
                        (y == start ? cold : warm).add(m.passes(g));
                        if (y != start && y != start + 5 && y != start + 10) continue;
                        int gi = m.indexOf(g);
                        WorldMarket.Traded t = sc.base.traded[gi];
                        Supplier<String> where = () -> g + " " + path + " " + yy + " (from " + first + ")";
                        if (!chain.made(gi)) {
                            double[] r = residual(sc.data, m, g);
                            clearing.see(r[0] <= tol, () -> where.get() + ": sales over supply off by " + r[0]);
                            if (!(t.sigma > 0) || !(t.phi > 0)) continue;
                            double read = anchorRead(m, gi), target = m.anchorTarget(gi);
                            if (Double.isNaN(read)) {
                                left.add(where.get());
                                continue;
                            }
                            double miss = Math.abs(Math.log(read / target));
                            anchorMiss.see(miss, where);
                            anchored.see(miss <= ANCHOR_CHECK_TOL, () -> where.get() + ": the reference band stands " + read + " of the band the calibration left, the path says " + target);
                            continue;
                        }
                        double[] ph = m.phat(gi), in = inputsOf(m, gi, landed);
                        double own = chain.own[gi];
                        if (own > 0) {
                            // q: its own part, what is left of the price once its inputs are paid; it clears at its own level on the manufactures index
                            double[] q = new double[n];
                            Arrays.fill(q, 1);
                            for (int i : t.makers) q[i] = (ph[i] - in[i]) / own;
                            double[] make = madeIn(sc.data, t, n), sl = sales(sc.data, t, q, gh);
                            double lam = m.lambda(gi), worst = 0, total = 0, index = 0;
                            for (int i : t.makers) {
                                double supply = make[i] * gh[i] * lam * Math.pow(q[i], 1 + WorldMarket.SUPPLY_ELASTICITY);
                                worst = Math.max(worst, Math.abs(sl[i] / supply - 1));
                                total += make[i];
                                index += make[i] * q[i];
                            }
                            double level = Math.abs(index / total / m.ownLevelTarget(gi) - 1), clears = worst;
                            ownClear.see(clears, where);
                            ownLevel.see(level, where);
                            assembled.see(clears <= tol && level <= tol, () -> where.get() + ": its own part's sales are off its supply by " + clears + ", its mean off the level by " + level);
                        } else {
                            double off = 0;
                            for (int i : t.makers) off = Math.max(off, Math.abs(ph[i] / Math.max(in[i], WorldMarket.PRICE_FLOOR) - 1));
                            double miss = off;
                            priceMiss.see(miss, where);
                            assembled.see(miss <= ARITHMETIC, () -> where.get() + ": a good with no part of its own is off its inputs' cost by " + miss);
                        }
                        double[] sup = m.supply(gi), sl = sales(sc.data, t, ph, gh);
                        double worst = 0;
                        for (int i : t.makers) worst = Math.max(worst, Math.abs(sl[i] * 1000 / (t.v * ph[i]) / sup[i] - 1));
                        double clears = worst;
                        supplyMiss.see(clears, where);
                        clearing.see(clears <= tol, () -> where.get() + ": its makers' supply is off their sales at its price by " + clears);
                    }
                }
            }
        }
        tally("the solved year is the year asked for", years);
        tally("a root's reference band stands on its path: over the band the calibration left it, within ANCHOR_CHECK_TOL (" + ANCHOR_CHECK_TOL + ") in logs of the path's level over the literals', the founding, five and ten years on, both starts, both paths", anchored);
        out.println("the worst anchor miss, in logs: " + anchorMiss);
        assertTrue("no year the anchor left (the reference band not above 0 at the solve's prices) on these years: " + left.size() + (left.isEmpty() ? "" : ", the first " + left.get(0)), left.isEmpty());
        tally("a made good is its inputs' landed cost (less the copper charge) plus its own part: the part clears and meets the manufactures index's level to SOLVE_TOL, a good with none is its inputs' cost to 1e-12", assembled);
        out.println("worst own part: sales over supply " + ownClear + "; mean over the level " + ownLevel + "; a good with no part, off its inputs' cost " + priceMiss);
        tally("every maker clears within SOLVE_TOL: a root's sales over supply, a made good's supply over its sales at its price, recomputed from the file's pairs", clearing);
        out.println("worst made good, supply over its sales at its price: " + supplyMiss);
        Collections.sort(cold);
        Collections.sort(warm);
        out.printf("sweeps a good: founding (cold, " + Nations.FIRST_YEAR + " and " + WorldData.FILE_FIRST_YEAR + ") median %d, worst %d; a January after (warm) median %d, worst %d%n",
                cold.get(cold.size() / 2), cold.get(cold.size() - 1), warm.get(warm.size() / 2), warm.get(warm.size() - 1));
        assertTrue("no good stopped at SOLVE_MAX_PASSES (" + WorldMarket.SOLVE_MAX_PASSES + "): the worst took " + Math.max(cold.get(cold.size() - 1), warm.get(warm.size() - 1)),
                Math.max(cold.get(cold.size() - 1), warm.get(warm.size() - 1)) < WorldMarket.SOLVE_MAX_PASSES);
    }

    /* =====================================================================
       2. THE CALIBRATION
       ===================================================================== */

    static void theCalibration(Scope sc) {
        out.println("--- the reference city sees the game's literals (" + sc.name + ") ---");
        WorldMarket.Base base = sc.base;
        WorldMarket m = sc.market(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
        m.solveYear(WorldMarket.REFERENCE_YEAR);
        Tally literals = new Tally(), scales = new Tally(), medians = new Tally();
        List<String> unfit = new ArrayList<>(), onePlace = new ArrayList<>(), none = new ArrayList<>();
        Map<String, List<Double>> byClass = new HashMap<>();
        // the class's median is the plain pass's (CU2: a made good reads its input's freight scale, so the class median is only known after the plain pass)
        for (Good g : base.goods()) {
            int gi = m.indexOf(g);
            if (base.plainFit[gi] == WorldMarket.Fit.CALIBRATED) byClass.computeIfAbsent(base.traded[gi].goodClass, c -> new ArrayList<>()).add(base.plainPhi[gi]);
        }
        for (Good g : base.goods()) {
            WorldMarket.Traded t = base.traded[m.indexOf(g)];
            WorldMarket.Fit fit = base.fit(g);
            double[] b = m.band(g, 0, 0);
            boolean both = g.importable() && g.exportable();
            // concentrate and anode carry the played year's charge, not 2024's the literals stand on (call star 6 (a)): judged apart, below
            boolean charged = (g == Good.COPPER_CONC || g == Good.ANODE) && fit == WorldMarket.Fit.CALIBRATED;
            if (both && fit != WorldMarket.Fit.CALIBRATED) unfit.add(g + " (" + fit + ")");
            if (fit == WorldMarket.Fit.NONE) {
                none.add(g.toString());
                continue;
            }
            if (fit == WorldMarket.Fit.ONE_SIDED) onePlace.add(g.toString());
            scales.see(base.sigma(g) > 0 && Double.isFinite(base.sigma(g)) && base.phi(g) > 0 && Double.isFinite(base.phi(g)), () -> g + ": sigma " + base.sigma(g) + ", phi " + base.phi(g));
            if (charged) {
                literals.see(near(base.referenceFloor(g), g.worldExportPrice(), SAME) && near(base.referenceCeiling(g), g.worldImportPrice(), SAME), () -> g + "'s reference figures (the calibration reads CHARGE_LITERAL): " + base.referenceFloor(g) + " and " + base.referenceCeiling(g));
                continue;
            }
            if (g.importable()) literals.see(near(b[1], g.worldImportPrice(), SAME), () -> g + " (" + fit + ") ceiling " + b[1] + " against the literal " + g.worldImportPrice());
            else literals.see(Double.isNaN(b[1]), () -> g + " has no import side, and its ceiling is " + b[1]);
            if (fit == WorldMarket.Fit.CALIBRATED) {
                literals.see(near(b[0], g.worldExportPrice(), SAME), () -> g + " (" + fit + ") floor " + b[0] + " against the literal " + g.worldExportPrice());
                literals.see(near(base.referenceFloor(g), g.worldExportPrice(), SAME) && near(base.referenceCeiling(g), g.worldImportPrice(), SAME), () -> g + "'s reference figures");
            } else if (g.exportable()) {
                if (!both) literals.see(near(b[0], g.worldExportPrice(), SAME), () -> g + " (" + fit + ") floor " + b[0] + " against the literal " + g.worldExportPrice());
            } else {
                literals.see(Double.isNaN(b[0]), () -> g + " has no export side, and its floor is " + b[0]);
            }
            if (fit != WorldMarket.Fit.CALIBRATED) {
                // it keeps the freight scale the plain pass gave it, and the plain pass gave it its class's median of the goods that calibrated there
                int gi = m.indexOf(g);
                medians.see(near(base.phi(g), base.plainPhi[gi], ARITHMETIC), () -> g + "'s freight scale " + base.phi(g) + " against the plain pass's " + base.plainPhi[gi]);
                if (byClass.containsKey(t.goodClass)) {
                    List<Double> v = new ArrayList<>(byClass.get(t.goodClass));
                    Collections.sort(v);
                    int k = v.size();
                    double median = k % 2 == 1 ? v.get(k / 2) : .5 * (v.get(k / 2 - 1) + v.get(k / 2));
                    medians.see(near(base.plainPhi[gi], median, ARITHMETIC), () -> g + "'s plain freight scale " + base.plainPhi[gi] + " against its class's median " + median + " of the goods the plain pass calibrated");
                }
            }
        }
        tally("the reference city (Halifax, no berth, 2025, Historical) sees the literal on every side the city has, to 1e-9: calibrated goods both, one-sided goods theirs (concentrate and anode: their reference figures)", literals);
        tally("every good the market scales has a price scale and a freight scale above 0", scales);
        tally("a good that does not calibrate keeps the freight scale the plain pass gave it, and that is its class's median of the plain pass's calibrated goods where the class has one", medians);
        out.println("calibrated " + base.fits[WorldMarket.Fit.CALIBRATED.ordinal()] + ", one-sided " + onePlace + ", on the class median " + unfit + ", without a side " + none);
        assertTrue("no two-sided good goes uncalibrated: " + (unfit.isEmpty() ? "none" : unfit), unfit.isEmpty());
        assertTrue("the reference city stands in Canada (" + m.iso3(m.host()) + ")", m.iso3(m.host()).equals("CAN") && m.host() == base.referenceHost());

        // concentrate and anode: the calibration read the charge the literals stand on (CHARGE_LITERAL), the played 2025 carries the year's own, so each side of
        // the played band stands off the literal by the charge's whole difference (times the good's price scale and unit value) and no more, cheaper where the
        // year's charge is the higher
        Tally moved = new Tally();
        for (Good g : new Good[] {Good.COPPER_CONC, Good.ANODE}) {
            int gi = m.indexOf(g);
            if (gi < 0 || base.fit(g) != WorldMarket.Fit.CALIBRATED) {
                out.println(g + " is not a calibrated good on this file: its played band is not read");
                continue;
            }
            WorldMarket.Traded t = base.traded[gi];
            double part = g == Good.COPPER_CONC ? 1 : WorldMarket.REFINING_PART;
            double literalCharge = part * WorldMarket.CHARGE_LITERAL * base.refCopper, yearCharge = m.charge(g), diff = yearCharge - literalCharge;
            double room = base.sigma(g) * t.v * Math.abs(diff);
            double[] b = m.band(g, 0, 0), lit = {g.worldExportPrice(), g.worldImportPrice()};
            String[] side = {"floor", "ceiling"};
            double[] share = new double[2];
            for (int s = 0; s < 2; s++) {
                int ss = s;
                share[s] = Math.abs(b[s] - lit[s]) / room;
                boolean way = diff > 0 ? b[s] < lit[s] : diff < 0 ? b[s] > lit[s] : near(b[s], lit[s], SAME);
                moved.see(way && Math.abs(b[s] - lit[s]) <= room * (1 + CHARGE_ROOM), () -> g + "'s " + side[ss] + " " + b[ss] + " against the literal " + lit[ss]
                        + ", the charge " + yearCharge + " against the literal's " + literalCharge + ", room " + room);
            }
            out.printf(Locale.ROOT, "%s in %d, played over its literal: floor %.4f, ceiling %.4f; the year's charge %.4f against the literal's %.4f of the copper's base-year price; each side's move off its literal is the charge's whole difference to %.2e and %.2e of it%n",
                    g, WorldMarket.REFERENCE_YEAR, b[0] / lit[0], b[1] / lit[1], yearCharge, literalCharge, Math.abs(share[0] - 1), Math.abs(share[1] - 1));
        }
        tally("concentrate and anode in " + WorldMarket.REFERENCE_YEAR + " stand off their literals the way the year's charge says (cheaper where it is above the literal's) and by no more than its difference", moved);

        // elsewhere the world is not the literal: Rotterdam, with a berth
        WorldMarket there = sc.market(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
        there.setSite(51.9, 4.5, true);
        there.solveYear(WorldMarket.REFERENCE_YEAR);
        int differ = 0, read = 0;
        for (Good g : base.goods()) {
            if (!g.exportable() || !(base.sigma(g) > 0)) continue;
            read++;
            if (!near(there.band(g, 0, 0)[0], g.worldExportPrice(), .01)) differ++;
        }
        assertTrue("a city at Rotterdam with a berth does not see the literals: its floor differs from the literal by more than 1% for " + differ + " of " + read + " goods", differ > 0);
    }

    /* =====================================================================
       3. THE FLOOR IS NEVER ABOVE THE CEILING
       ===================================================================== */

    /** The units a month a city trades to move a good's year by tonnes. */
    static double units(WorldMarket m, int gi, double tonnes) { return tonnes * m.base().traded[gi].unitsPerTonne / 12; }

    /** Checks one solved market at its site over the grid of the city's volumes. */
    static void order(WorldMarket m, Tally order, Tally falls, Tally game, String where, int[] combos) {
        for (Good g : m.base().goods()) {
            int gi = m.indexOf(g);
            double use = m.worldUse(gi);
            double lastFloor = Double.POSITIVE_INFINITY, lastCeiling = Double.NEGATIVE_INFINITY;
            for (double sx : SHARES) {
                double[] sold = m.model(gi, sx * use, 0), bought = m.model(gi, 0, sx * use);
                if (!Double.isNaN(sold[0])) {
                    falls.see(sold[0] <= lastFloor * (1 + 1e-12) || lastFloor == Double.POSITIVE_INFINITY, () -> where + " " + g + ": the floor rose to " + sold[0] + " with exports " + sx + " of the world's use");
                    lastFloor = sold[0];
                }
                if (!Double.isNaN(bought[1])) {
                    falls.see(bought[1] >= lastCeiling * (1 - 1e-12), () -> where + " " + g + ": the ceiling fell to " + bought[1] + " with imports " + sx + " of the world's use");
                    lastCeiling = bought[1];
                }
                for (double sm : SHARES) {
                    double[] b = m.model(gi, sx * use, sm * use);
                    combos[0]++;
                    if (Double.isNaN(b[0]) || Double.isNaN(b[1])) continue;
                    order.see(b[0] <= b[1], () -> where + " " + g + ": floor " + b[0] + " above ceiling " + b[1] + " at exports " + sx + " and imports " + sm + " of the world's use");
                    if (g.importable() && g.exportable() && m.base().sigma(g) > 0) {
                        double[] band = m.band(g, units(m, gi, sx * use), units(m, gi, sm * use));
                        game.see(band[0] <= band[1], () -> where + " " + g + ": the band " + band[0] + " to " + band[1]);
                    }
                }
            }
        }
    }

    static void theFloorUnderCeiling(Scope sc) {
        out.println("--- the floor is never above the ceiling (" + sc.name + ") ---");
        Tally order = new Tally(), falls = new Tally(), game = new Tally();
        int[] combos = new int[1];
        for (Nations.Path path : Nations.Path.values()) {
            WorldMarket m = sc.market(path, BASE_SEED);
            for (int y = 2000; y <= 2040; y++) {
                m.solveYear(y);
                order(m, order, falls, game, path + " " + y + " at Halifax", combos);
            }
        }
        // a city with a berth, and a generated world's stand-in, in three years each
        for (Nations.Path path : Nations.Path.values()) {
            WorldMarket rot = sc.market(path, BASE_SEED + 1), stand = sc.market(path, BASE_SEED + 2);
            rot.setSite(51.9, 4.5, true);
            stand.setStandIn(BASE_SEED, true);
            for (int y = 2000; y <= 2040; y++) {
                rot.solveYear(y);
                stand.solveYear(y);
                if (y % 20 != 0) continue;
                order(rot, order, falls, game, path + " " + y + " at Rotterdam", combos);
                order(stand, order, falls, game, path + " " + y + " at the stand-in " + stand.iso3(stand.host()), combos);
            }
        }
        out.println(combos[0] + " (good, exports, imports) combinations read");
        tally("the floor is never above the ceiling: every good, 2000-2040, both paths, exports and imports each 0, 0.1%, 1%, 10% and 50% of the world's use, three sites", order);
        tally("the same in the game's money, for every good with both sides", game);
        tally("the floor falls as the city's exports grow and the ceiling rises as its imports do", falls);
        // past every seller's capacity: the city buying three times the world's use of a good, which no seller can make
        WorldMarket past = sc.market(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
        past.solveYear(WorldMarket.REFERENCE_YEAR);
        int bounded = 0, goods = 0;
        String worst = "none";
        for (Good g : sc.base.goods()) {
            int gi = past.indexOf(g);
            if (gi < 0) continue;
            double[] b = past.model(gi, 0, 3 * past.worldUse(gi));
            if (Double.isNaN(b[1])) continue;
            goods++;
            if (Double.isFinite(b[1]) && b[1] >= (Double.isNaN(b[0]) ? 0 : b[0])) bounded++; else worst = g.name();
        }
        report("past every seller's capacity the ceiling stays finite and over the floor (WorldMarket.SCARCITY_MULTIPLE), every good with sellers",
                goods > 0 && bounded == goods, bounded + " of " + goods + " goods; first unbounded: " + worst);
    }

    /* =====================================================================
       4. A BIG CITY MOVES ITS PRICE, A SMALL ONE BARELY
       ===================================================================== */

    static void theBigCity(Scope sc) {
        out.println("--- a city moves its price as big as it is: an ordinary one barely, a tenth of the world's use a lot (" + sc.name + ") ---");
        WorldMarket m = sc.market(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
        m.solveYear(WorldMarket.REFERENCE_YEAR);
        Tally small = new Tally(), big = new Tally();
        List<String> largeOver = new ArrayList<>(), bigMisses = new ArrayList<>();
        double worstSmall = 0, worstSmallNet = 0, worstLarge = 0, worstLargeNet = 0, leastBig = Double.POSITIVE_INFINITY, leastBigNet = Double.POSITIVE_INFINITY;
        String worstSmallGood = "-";
        int goods = 0;
        for (Good g : sc.base.goods()) {
            if (!g.exportable() || !(sc.base.sigma(g) > 0)) continue;
            int gi = m.indexOf(g);
            double use = m.worldUse(gi), f0 = m.model(gi, 0, 0)[0];
            double xs = ORDINARY_SHARE * use, xl = LARGE_SHARE * use, xb = BIG_SHARE * use;
            double fs = m.model(gi, xs, 0)[0], fl = m.model(gi, xl, 0)[0], fb = m.model(gi, xb, 0)[0];
            double moveSmall = (f0 - fs) / m.servedPrice(gi, xs), moveLarge = (f0 - fl) / m.servedPrice(gi, xl), moveBig = (f0 - fb) / m.servedPrice(gi, xb);
            goods++;
            if (moveSmall > worstSmall) {
                worstSmall = moveSmall;
                worstSmallGood = g.toString();
            }
            worstSmallNet = Math.max(worstSmallNet, 1 - fs / f0);
            worstLarge = Math.max(worstLarge, moveLarge);
            worstLargeNet = Math.max(worstLargeNet, 1 - fl / f0);
            leastBig = Math.min(leastBig, moveBig);
            leastBigNet = Math.min(leastBigNet, 1 - fb / f0);
            small.see(moveSmall < SMALL_MOVE, () -> g + ": " + pct(moveSmall) + " of the price");
            big.see(moveBig >= BIG_MOVE, () -> g + ": " + pct(moveBig) + " of the price");
            if (!(moveLarge < SMALL_MOVE)) largeOver.add(g + " " + pct(moveLarge));
            if (!(moveBig >= BIG_MOVE)) bigMisses.add(g + " " + pct(moveBig));
        }
        tally("a city selling 0.01% of a good's world use moves its netback by under " + pct(SMALL_MOVE) + " of the price it sells at, every exportable good (largest " + pct(worstSmall) + ", " + worstSmallGood + ")", small);
        tally("a city selling 10% of a good's world use moves its netback by at least " + pct(BIG_MOVE) + " of that price, every exportable good (least " + pct(leastBig) + ")", big);
        out.println("  as shares of the netback itself: the 0.01% city moves it by at most " + pct(worstSmallNet) + ", the 10% city by at least " + pct(leastBigNet));
        out.println("  printed, not asserted: a city selling 0.1% of a good's world use (a large mill town) moves its netback by at most " + pct(worstLarge) + " of the price (" + pct(worstLargeNet)
                + " of the netback); " + largeOver.size() + " of " + goods + " goods by " + pct(SMALL_MOVE) + " or more, because the host's market is small and the re-solve agrees: " + largeOver);
        if (!bigMisses.isEmpty()) out.println("  big city, moved under " + pct(BIG_MOVE) + " of the price (" + bigMisses.size() + " of " + goods + "): " + bigMisses);
    }

    static String pct(double share) { return String.format(Locale.ROOT, "%.2f%%", 100 * share); }

    /* =====================================================================
       5. THE MONTH'S STEP IS NEAR A RE-SOLVE
       ===================================================================== */

    /** The goods the study and the brief name, always printed. */
    static final Good[] NAMED = {Good.LUMBER, Good.IRON, Good.CRUDE, Good.GRAINS, Good.MACHINERY, Good.CARS};

    static void theStep(Scope sc) {
        out.println("--- the month's step against a re-solve with the city's tonnes in, in shares of the price (" + sc.name + ") ---");
        WorldMarket m = sc.market(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
        m.solveYear(WorldMarket.REFERENCE_YEAR);
        for (double share : STEPS) {
            Tally within = new Tally();
            List<double[]> offs = new ArrayList<>();
            List<String> misses = new ArrayList<>();
            Map<Good, double[]> named = new java.util.LinkedHashMap<>();
            int goods = 0, overflow = 0;
            for (Good g : sc.base.goods()) {
                if (!g.exportable() || !(sc.base.sigma(g) > 0)) continue;
                int gi = m.indexOf(g);
                double x = share * m.worldUse(gi);
                WorldMarket.Resolved r = m.resolve(gi, x);
                // the step is the band's floor, which is never below 0: what the re-solve says the city nets is compared as the same floor
                double realised = Math.max(0, r.realised());
                double off = (r.step() - realised) / r.price(), offNet = realised > 0 ? r.step() / realised - 1 : r.step() > 0 ? 1 : 0;
                goods++;
                if (x > m.capacities(gi)[0]) overflow++;
                boolean ok = Math.abs(off) <= STEP_TOL;
                within.see(ok, () -> g + ": step " + r.step() + ", re-solved " + r.realised() + ", price " + r.price());
                offs.add(new double[] { Math.abs(off), Math.abs(offNet) });
                if (!ok) misses.add(String.format(Locale.ROOT, "%s %.2f against %.2f of %.0f (%+.1f%% of the price, %+.1f%% of the netback)", g, r.step(), r.realised(), r.price(), 100 * off, 100 * offNet));
                named.put(g, new double[] { r.step(), r.realised(), r.price(), off, offNet, r.served(), r.passes(), m.sellers(gi, x)[0][3] });
            }
            offs.sort((x, y) -> Double.compare(x[0], y[0]));
            List<double[]> byNet = new ArrayList<>(offs);
            byNet.sort((x, y) -> Double.compare(x[1], y[1]));
            String volume = String.format(Locale.ROOT, "%.1f%%", 100 * share);
            double median = offs.get(offs.size() / 2)[0], worst = offs.get(offs.size() - 1)[0];
            String summary = "median off " + pct(median) + ", worst " + pct(worst) + " of the price; as shares of the netback: median " + pct(byNet.get(byNet.size() / 2)[1]) + ", worst "
                    + pct(byNet.get(byNet.size() - 1)[1]) + "; the sale is more than all the sinks can take for " + overflow + " of " + goods + " goods";
            if (share == STEPS[0]) {
                tally("a city selling " + volume + " of a good's world use: the step is within " + pct(STEP_TOL) + " of the price of the re-solve, every exportable good (" + summary + ")", within);
            } else if (share == STEPS[1]) {
                assertTrue("a city selling " + volume + " of a good's world use: the median step is within " + pct(STEP_TOL) + " of the price of the re-solve (" + (goods - within.bad) + " of " + goods + " goods are; " + summary + ")", median <= STEP_TOL);
                for (Good g : ONE_PERCENT) {
                    double[] r = named.get(g);
                    if (r == null) continue;
                    String line = String.format(Locale.ROOT, "%s at %s: step %.3f, re-solved %.3f, off %+.2f%% of the price (%+.2f%% of the netback)", g, volume, r[0], r[1], 100 * r[3], 100 * r[4]);
                    if (Math.abs(r[3]) <= STEP_TOL) assertTrue(line + ", within " + pct(STEP_TOL), true);
                    else out.println("  NOT within " + pct(STEP_TOL) + " and so not asserted: " + line);
                }
            } else {
                out.println("  the step's reach beyond a giant city's 1% (printed, not asserted): a city selling " + volume + " of a good's world use: " + (goods - within.bad) + " of " + goods
                        + " goods have the step within " + pct(STEP_TOL) + " of the price; " + summary);
            }
            if (share != STEPS[0] && !misses.isEmpty() && share == STEPS[1]) out.println("  not within " + pct(STEP_TOL) + " at " + volume + " (" + misses.size() + " of " + goods + "): " + misses.subList(0, Math.min(misses.size(), 8)) + (misses.size() > 8 ? " ..." : ""));
            if (share == STEPS[0] && !misses.isEmpty()) out.println("  misses at " + volume + ": " + misses);
            for (Good g : NAMED) {
                double[] r = named.get(g);
                if (r == null || g == Good.LUMBER) continue;
                out.printf(Locale.ROOT, "  %-9s %5.1f%% of world use: step %10.3f, re-solved %10.3f, off %+6.2f%% of the price %+7.2f%% of the netback, %d sinks, %d sweeps%n", g, 100 * share, r[0], r[1], 100 * r[3], 100 * r[4], (int) r[5], (int) r[6]);
            }
        }
        lumber(sc, m);
    }

    /** Lumber at the four volumes, as the study's 4.4 table has it: the buyers, what the city nets this month, with the world's answer, re-solved, off. */
    static void lumber(Scope sc, WorldMarket m) {
        int gi = m.indexOf(Good.LUMBER);
        if (gi < 0) return;
        double use = m.worldUse(gi);
        out.println("  lumber, the study's 4.4 table (the city at " + m.iso3(m.host()) + ", world use " + String.format(Locale.ROOT, "%,.0f", use) + " t): share of world use, the buyers, nets this month, with the world's answer, re-solved, off");
        for (double share : STEPS) {
            double x = share * use;
            WorldMarket.Resolved r = m.resolve(gi, x);
            double[][] rows = m.sellers(gi, x);
            StringBuilder buyers = new StringBuilder();
            for (int k = 0; k < Math.min(3, rows.length); k++) buyers.append(String.format(Locale.ROOT, "%s %.0f%% ", m.iso3((int) rows[k][0]), x > 0 ? 100 * rows[k][1] / x : 0));
            double realised = Math.max(0, r.realised());
            out.printf(Locale.ROOT, "    LUMBER %5.1f%%: %s(%d buyers) nets %8.2f this month, %8.2f with the world's answer, %8.2f re-solved, off %+6.2f%% of the price (%+6.2f%% of the netback)%n", 100 * share, buyers,
                    rows.length, rows[0][3], r.step(), r.realised(), 100 * (r.step() - realised) / r.price(), realised > 0 ? 100 * (r.step() / realised - 1) : 0.0);
        }
    }

    /* =====================================================================
       6. SEEDS
       ===================================================================== */

    /** Every good's {floor, ceiling} at the site, for exports and imports of 1% and 2% of the world's use, in the game's money. */
    static double[] bands(WorldMarket m) {
        List<Double> list = new ArrayList<>();
        for (Good g : m.base().goods()) {
            int gi = m.indexOf(g);
            double use = m.worldUse(gi);
            for (double b : m.band(g, 0, 0)) list.add(b);
            for (double b : m.band(g, units(m, gi, .01 * use), units(m, gi, .02 * use))) list.add(b);
        }
        double[] a = new double[list.size()];
        for (int i = 0; i < a.length; i++) a[i] = list.get(i);
        return a;
    }

    static void theSeeds(Scope sc) {
        out.println("--- one seed repeats to the bit, two seeds differ, Historical's 2025 is seed-free (" + sc.name + ") ---");
        WorldMarket one = sc.market(Nations.Path.RANDOM, 1), two = sc.market(Nations.Path.RANDOM, 2);
        one.solveYear(2000);
        two.solveYear(2000);
        for (int y = 2001; y <= 2010; y++) {
            one.solveYear(y);
            two.solveYear(y);
        }
        boolean differs = false;
        for (Good g : sc.base.goods()) differs |= !same(one.band(g, 0, 0), two.band(g, 0, 0));
        assertTrue("Random on seeds 1 and 2: some good's band in 2010 differs", differs);

        WorldMarket.forgetSolves();
        WorldMarket a = sc.market(Nations.Path.RANDOM, 3);
        List<double[]> first = new ArrayList<>();
        for (int y = 2000; y <= 2030; y++) {
            a.solveYear(y);
            first.add(bands(a));
        }
        WorldMarket.forgetSolves();
        WorldMarket b = sc.market(Nations.Path.RANDOM, 3);
        WorldMarket c = sc.market(Nations.Path.RANDOM, 3);
        Tally fresh = new Tally(), cached = new Tally();
        for (int y = 2000; y <= 2030; y++) {
            b.solveYear(y);
            int yy = y;
            fresh.see(same(first.get(y - 2000), bands(b)), () -> "the bands of " + yy);
        }
        for (int y = 2000; y <= 2030; y++) {
            c.solveYear(y);
            int yy = y;
            cached.see(same(first.get(y - 2000), bands(c)), () -> "the bands of " + yy + " from the cache");
        }
        tally("two markets on one seed, each solving 2000-2030 afresh, have bit-identical bands every year (the solve repeats itself)", fresh);
        tally("a third, answered from the solved years the first two left, has them too", cached);

        WorldMarket.forgetSolves();
        WorldMarket h1 = sc.market(Nations.Path.HISTORICAL, 1), h2 = sc.market(Nations.Path.HISTORICAL, 2);
        h1.solveYear(WorldMarket.REFERENCE_YEAR);
        WorldMarket.forgetSolves();
        h2.solveYear(WorldMarket.REFERENCE_YEAR);
        assertTrue("Historical on seeds 1 and 2, each solving 2025 afresh: the same bands to the bit", same(bands(h1), bands(h2)));
    }

    /* =====================================================================
       7. THE SAVE ROUND TRIP
       ===================================================================== */

    static void theSave(Scope sc) {
        out.println("--- a restored market gives the bands and allocations it was saved with (" + sc.name + ") ---");
        WorldMarket a = sc.market(Nations.Path.RANDOM, 5);
        a.setSite(51.9, 4.5, true);
        for (int y = 2000; y <= 2012; y++) a.solveYear(y);
        double[] prices = a.savedPrices(), scales = a.savedScales();
        WorldMarket b = sc.market(Nations.Path.RANDOM, 5);
        b.setSite(51.9, 4.5, true);
        assertTrue("a market that has solved nothing saves nothing", b.savedPrices().length == 0 && b.savedScales().length == 0 && b.solvedYear() == -1);
        boolean refusedShort = !b.restore(2012, Arrays.copyOf(prices, prices.length - 1), scales);
        boolean refusedScales = !b.restore(2012, prices, Arrays.copyOf(scales, scales.length + 1));
        double[] bad = prices.clone();
        bad[0] = Double.NaN;
        boolean refusedNaN = !b.restore(2012, bad, scales);
        assertTrue("a wrong length of prices or scales, or a price that is no number, is refused and nothing changes", refusedShort && refusedScales && refusedNaN && b.solvedYear() == -1);
        assertTrue("the right prices and scales are taken", b.restore(2012, prices, scales) && b.solvedYear() == 2012);
        Tally bands = new Tally(), alloc = new Tally();
        for (Good g : sc.base.goods()) {
            int gi = a.indexOf(g);
            double use = a.worldUse(gi);
            for (double sx : SHARES) for (double sm : SHARES) {
                double x = units(a, gi, sx * use), mm = units(a, gi, sm * use);
                bands.see(same(a.band(g, x, mm), b.band(g, x, mm)), () -> g + " at " + sx + " and " + sm);
                WorldMarket.Allocation p = a.allocate(g, x, mm), q = b.allocate(g, x, mm);
                alloc.see(same(p.exportUnitsTo(), q.exportUnitsTo()) && same(p.importUnitsFrom(), q.importUnitsFrom()), () -> g + " at " + sx + " and " + sm);
            }
            for (int nation = 1; nation <= a.nationCount(); nation++) {
                int nn = nation;
                bands.see(same(a.millPrice(g, nation), b.millPrice(g, nation)) && same(a.buyerPrice(g, nation), b.buyerPrice(g, nation)), () -> g + " prices in nation " + nn);
            }
        }
        tally("the restored market's bands and prices are the saved market's to the bit: every good, every volume, every nation", bands);
        tally("and its allocations, who took the exports and who sold the imports", alloc);
        WorldMarket.forgetSolves();
        a.solveYear(2013);
        WorldMarket.forgetSolves();
        b.solveYear(2013);
        assertTrue("the next January from the restored market is the saved market's, to the bit (it was restored to the state it would have started from)", same(bands(a), bands(b)));
    }

    /* =====================================================================
       8. THE STAND-IN
       ===================================================================== */

    static void theStandIn(Scope sc) {
        out.println("--- a generated world's stand-in is a coastal nation drawn by output (" + sc.name + ") ---");
        WorldMarket.Base base = sc.base;
        int n = base.nationCount();
        int[] drawn = new int[n + 1];
        Tally coastal = new Tally(), repeat = new Tally();
        for (long seed = 1; seed <= STAND_IN_SEEDS; seed++) {
            int s = base.standIn(seed);
            long ss = seed;
            coastal.see(s >= 1 && s <= n && !base.landlocked[s - 1], () -> "seed " + ss + " stands in nation " + s);
            repeat.see(s == base.standIn(seed), () -> "seed " + ss);
            drawn[s]++;
        }
        tally("the same seed gives the same nation, and it is never landlocked, over " + STAND_IN_SEEDS + " seeds", merged(coastal, repeat));
        double total = 0;
        for (int i = 0; i < n; i++) if (!base.landlocked[i]) total += base.baseOutput[i];
        Tally weight = new Tally();
        double drawnOutput = 0, uniformOutput = 0;
        int coastalNations = 0;
        for (int i = 0; i < n; i++) {
            if (base.landlocked[i]) continue;
            coastalNations++;
            uniformOutput += base.baseOutput[i];
            drawnOutput += drawn[i + 1] * base.baseOutput[i];
            double p = base.baseOutput[i] / total, expected = STAND_IN_SEEDS * p, sd = Math.sqrt(STAND_IN_SEEDS * p * (1 - p));
            int ii = i;
            if (expected >= 5) weight.see(Math.abs(drawn[i + 1] - expected) <= SIGMAS * sd + 1, () -> base.iso[ii] + " drawn " + drawn[ii + 1] + " times, expected " + expected);
        }
        tally("each coastal nation is drawn as often as its share of the coastal nations' base-year output says (within " + SIGMAS + " standard deviations)", weight);
        double ratio = drawnOutput / STAND_IN_SEEDS / (uniformOutput / coastalNations);
        assertTrue(String.format(Locale.ROOT, "the draw lands where the output is: the drawn nations' mean output is %.1f times the coastal nations' mean", ratio), ratio > 1.5);
        WorldMarket m = sc.market(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
        m.setStandIn(7, true);
        assertTrue("setStandIn puts the city in the nation the seed draws (" + m.iso3(m.host()) + "), with a berth", m.host() == base.standIn(7) && m.berth() && m.standIn(7) == m.host());
    }

    /** Two tallies as one. */
    static Tally merged(Tally a, Tally b) {
        Tally t = new Tally();
        t.n = a.n + b.n;
        t.bad = a.bad + b.bad;
        t.first = a.first != null ? a.first : b.first;
        return t;
    }

    /* =====================================================================
       9. THE PIECES
       ===================================================================== */

    /**
     * The level at which sinks take x tonnes together, each taking min(cap, slope (net - level)+), found by bisection on the total and so with none of the
     * market's sorted events; -infinity past the caps. The purchase side is the same on the landed costs negated.
     */
    static double bruteLevel(double[] net, double[] slope, double[] cap, double x) {
        double capSum = 0, hi = Double.NEGATIVE_INFINITY, lo = Double.POSITIVE_INFINITY;
        for (int k = 0; k < net.length; k++) {
            capSum += cap[k];
            hi = Math.max(hi, net[k]);
            lo = Math.min(lo, net[k] - cap[k] / slope[k]);
        }
        if (x > capSum * (1 + 1e-12)) return Double.NEGATIVE_INFINITY;
        for (int it = 0; it < 300; it++) {
            double mid = .5 * (lo + hi), total = 0;
            for (int k = 0; k < net.length; k++) total += Math.min(cap[k], Math.max(0, slope[k] * (net[k] - mid)));
            if (total >= x) lo = mid; else hi = mid;
        }
        return .5 * (lo + hi);
    }

    /** Two levels are the same: equal infinities, or within a share of the scale of the netbacks they are among. */
    static boolean sameLevel(double a, double b, double scale) {
        return a == b || Math.abs(a - b) <= 1e-9 * scale;
    }

    static void thePieces(Scope sc) {
        out.println("--- the pieces: the fill, the allocation, the berth, the prices (" + sc.name + ") ---");
        WorldMarket m = sc.market(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
        m.solveYear(WorldMarket.REFERENCE_YEAR);
        Tally sell = new Tally(), buy = new Tally(), caps = new Tally(), toTally = new Tally(), fromTally = new Tally(), prices = new Tally();
        int beyond = 0, combos = 0;
        for (Good g : sc.base.goods()) {
            int gi = m.indexOf(g);
            double use = m.worldUse(gi);
            double[][] side = m.sellSide(gi), makers = m.buySide(gi);
            double[] net = new double[side.length], slope = new double[side.length], cap = new double[side.length];
            double scale = 1;
            for (int k = 0; k < side.length; k++) {
                net[k] = side[k][2];
                slope[k] = side[k][1];
                cap[k] = side[k][4];
                scale = Math.max(scale, Math.abs(net[k]));
            }
            double[] negLanded = new double[makers.length], muBuy = new double[makers.length], capBuy = new double[makers.length];
            for (int k = 0; k < makers.length; k++) {
                negLanded[k] = -makers[k][2];
                muBuy[k] = makers[k][1];
                capBuy[k] = makers[k][3];
                scale = Math.max(scale, Math.abs(makers[k][2]));
            }
            double sold = scaleOf(cap), made = scaleOf(capBuy);
            double sc2 = scale;
            for (double share : SHARES) {
                double x = share * use;
                combos++;
                if (side.length > 0 && x > 0) {
                    double lam = m.sellers(gi, x)[0][3], brute = bruteLevel(net, slope, cap, x);
                    sell.see(sameLevel(lam, brute, sc2), () -> g + " at " + share + ": fill " + lam + ", water-filling " + brute);
                    double taken = 0;
                    for (double[] row : m.sellers(gi, x)) {
                        taken += row[1];
                        caps.see(row[1] <= m.demand(gi, (int) row[0]) * (1 + 1e-12), () -> g + " at " + share + ": a buyer takes " + row[1] + " of the " + m.demand(gi, (int) row[0]) + " it uses");
                    }
                    double got = taken, expect = Math.min(x, sold);
                    if (x > sold) beyond++;
                    sell.see(near(got, expect, 1e-9), () -> g + " at " + share + ": the buyers take " + got + " of " + x + " (they can take " + sold + ")");
                    double floor = m.model(gi, x, 0)[0];
                    sell.see(x <= sold || floor == 0, () -> g + " at " + share + ": past every cap the floor is " + floor);
                }
                if (makers.length > 0 && x > 0) {
                    double[] mb = m.model(gi, 0, x);
                    double fromFill = mb[1] - x / m.extremes(gi)[2], brute = -bruteLevel(negLanded, muBuy, capBuy, x);
                    buy.see(sameLevel(fromFill, brute, sc2), () -> g + " at " + share + ": fill " + fromFill + ", water-filling " + brute);
                    buy.see(x <= made || mb[1] == Double.POSITIVE_INFINITY, () -> g + " at " + share + ": past every seller's output the ceiling is " + mb[1]);
                }
                double ux = units(m, gi, x);
                WorldMarket.Allocation al = m.allocate(g, ux, ux);
                double toSum = 0, fromSum = 0;
                for (double q : al.exportUnitsTo()) toSum += q;
                for (double q : al.importUnitsFrom()) fromSum += q;
                double sumTo = toSum, sumFrom = fromSum;
                boolean selling = x > 0 && g.exportable() && side.length > 0, buying = x > 0 && g.importable() && makers.length > 0;
                toTally.see(selling ? near(sumTo, ux, 1e-9) : sumTo == 0, () -> g + " at " + share + ": exports allocated " + sumTo + " of " + ux);
                fromTally.see(buying ? near(sumFrom, ux, 1e-9) : sumFrom == 0, () -> g + " at " + share + ": imports allocated " + sumFrom + " of " + ux);
                if (selling && x <= sold * (1 - 1e-9)) {
                    for (int nation = 1; nation <= m.nationCount(); nation++) {
                        int nn = nation;
                        caps.see(al.exportUnitsTo()[nation - 1] <= units(m, gi, m.demand(gi, nation)) * (1 + 1e-9), () -> g + " at " + share + ": " + m.iso3(nn) + " is allotted " + al.exportUnitsTo()[nn - 1]);
                    }
                }
                if (buying && x <= made * (1 - 1e-9)) {
                    for (double[] row : makers) {
                        int nn = (int) row[0];
                        caps.see(al.importUnitsFrom()[nn - 1] <= units(m, gi, row[3]) * (1 + 1e-9), () -> g + " at " + share + ": " + m.iso3(nn) + " sells " + al.importUnitsFrom()[nn - 1]);
                    }
                }
            }
            for (int nation = 1; nation <= m.nationCount(); nation++) {
                double mill = m.millPrice(g, nation), buyer = m.buyerPrice(g, nation);
                int nn = nation;
                if (!Double.isNaN(mill) && !Double.isNaN(buyer)) prices.see(buyer <= mill * (1 + 1e-12), () -> g + " in " + m.iso3(nn) + ": buyers pay " + buyer + " and its mills ask " + mill);
            }
        }
        out.println(beyond + " of " + combos + " (good, volume) sales are more than all the sinks can take");
        tally("the sale fill equals a bisection on the capped total of the same sinks, and the sinks take what the city sells, or what they can", sell);
        tally("the purchase fill equals a bisection on the capped total of the same makers, and is +infinity past what the world makes", buy);
        tally("no sink takes more than its best buyer uses, no maker sells more than it makes, in the fills and in the allocations", caps);
        tally("the exports' allocation adds up to the exports, and is nothing where the city sells nothing", toTally);
        tally("the imports' allocation adds up to the imports, and is nothing where the city buys nothing", fromTally);
        tally("what the cheapest landed seller asks a nation's buyers is never more than its own mills ask", prices);

        // a berth never makes anything dearer; setBerth is setSite with the flag; the default site is the reference
        WorldMarket dry = sc.market(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED), wet = sc.market(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED), toggled = sc.market(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
        dry.setSite(WorldMarket.REFERENCE_LAT, WorldMarket.REFERENCE_LON, false);
        wet.setSite(WorldMarket.REFERENCE_LAT, WorldMarket.REFERENCE_LON, true);
        toggled.setBerth(true);
        toggled.setSite(WorldMarket.REFERENCE_LAT, WorldMarket.REFERENCE_LON, false);
        toggled.setBerth(true);
        for (WorldMarket x : new WorldMarket[] {m, dry, wet, toggled}) x.solveYear(WorldMarket.REFERENCE_YEAR);
        Tally berth = new Tally(), setters = new Tally(), start = new Tally();
        for (Good g : sc.base.goods()) {
            for (int nation = 1; nation <= m.nationCount(); nation++) {
                double d = dry.buyerPrice(g, nation), w = wet.buyerPrice(g, nation);
                int nn = nation;
                if (!Double.isNaN(d) && !Double.isNaN(w)) berth.see(w <= d * (1 + 1e-12), () -> g + " in " + m.iso3(nn) + ": " + w + " with a berth, " + d + " without");
                setters.see(same(wet.buyerPrice(g, nation), toggled.buyerPrice(g, nation)), () -> g + " in " + m.iso3(nn));
                start.see(same(m.buyerPrice(g, nation), dry.buyerPrice(g, nation)), () -> g + " in " + m.iso3(nn));
            }
            setters.see(same(wet.band(g, 0, 0), toggled.band(g, 0, 0)), () -> g + "'s band");
        }
        // a site on the sea is hosted by the nation whose node is nearest, by great circle
        WorldMarket sea = sc.market(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
        sea.setSite(0, -30, true);
        int nearest = 1;
        for (int i = 1; i <= sea.nationCount(); i++) if (greatCircle(0, -30, sea.nodeLat(i), sea.nodeLon(i)) < greatCircle(0, -30, sea.nodeLat(nearest), sea.nodeLon(nearest))) nearest = i;
        assertTrue("a city in mid-Atlantic (0, -30) is hosted by the nation whose node is nearest, " + sea.iso3(nearest) + ", and that is " + sea.iso3(sea.host()), sea.host() == nearest);
        tally("a berth never makes a nation's buyer price dearer than the same city's without one", berth);
        tally("setBerth gives the legs setSite with the berth gives, to the bit", setters);
        tally("a market that is not told where the city stands stands at the reference site", start);
    }

    /** The great circle distance between two points, km, by the haversine. */
    static double greatCircle(double lat1, double lon1, double lat2, double lon2) {
        double p1 = Math.toRadians(lat1), p2 = Math.toRadians(lat2), dp = p2 - p1, dl = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dp / 2), 2) + Math.cos(p1) * Math.cos(p2) * Math.pow(Math.sin(dl / 2), 2);
        return 2 * 6371 * Math.asin(Math.sqrt(a));
    }

    /** The sum of an array. */
    static double scaleOf(double[] a) {
        double sum = 0;
        for (double v : a) sum += v;
        return sum;
    }

    /* =====================================================================
       10. THE WOOD CASE
       ===================================================================== */

    static void theWoodCase(Scope sc) {
        out.println("--- the wood case: a Halifax city's lumber (" + sc.name + ", printed, not asserted) ---");
        WorldMarket m = sc.market(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
        m.solveYear(WorldMarket.REFERENCE_YEAR);
        int gi = m.indexOf(Good.LUMBER);
        double use = m.worldUse(gi);
        double[][] side = m.sellSide(gi);
        out.printf(Locale.ROOT, "the city stands in %s; the world uses %,.0f t of lumber a year; freight scale %.2f%n", m.iso3(m.host()), use, sc.base.phi(Good.LUMBER));
        out.println("the first five sinks before the city sells, each a supplier that sets its buyers' price, at its best buyer (the study says the US first, then Canada at 4.8%, Portugal at 5.6%):");
        for (int k = 0; k < Math.min(5, side.length); k++) {
            int buyer = (int) side[k][0], supplier = (int) side[k][3];
            out.printf(Locale.ROOT, "  %d. %s's mills, sold at %-26s nets %9.2f US$ a tonne, buyer uses %5.2f%% of the world's, joins the fill at %6.3f%% of world use%n", k + 1, m.iso3(supplier),
                    m.name(buyer) + " (" + m.iso3(buyer) + ")", side[k][2], 100 * demandShare(m, gi, buyer), 100 * m.joinsAt(gi, buyer) / use);
        }
        for (String iso : new String[] {"USA", "CAN", "PRT", "MEX"}) {
            int nation = sc.base.iso.length;
            for (int i = 0; i < sc.base.iso.length; i++) if (sc.base.iso[i].equals(iso)) nation = i + 1;
            if (nation > sc.base.iso.length) continue;
            double joins = m.joinsAt(gi, nation);
            out.printf(Locale.ROOT, "  %s %s%n", iso, Double.isNaN(joins) ? "is no supplier's best buyer: its tonnes go through the best buyer of the supplier that sets its price"
                    : String.format(Locale.ROOT, "is a best buyer and joins the fill at %.3f%% of world use", 100 * joins / use));
        }
        for (double share : new double[] {.01, .05, .10}) {
            StringBuilder sb = new StringBuilder();
            double[][] rows = m.sellers(gi, share * use);
            for (int k = 0; k < Math.min(6, rows.length); k++) sb.append(String.format(Locale.ROOT, "%s %.0f%% ", m.iso3((int) rows[k][0]), 100 * rows[k][1] / (share * use)));
            out.printf(Locale.ROOT, "  selling %.0f%% of world use goes to: %s(%d sinks)%n", 100 * share, sb, rows.length);
        }
    }

    static double demandShare(WorldMarket m, int gi, int nation) {
        double total = m.worldUse(gi);
        return m.demand(gi, nation) / total;
    }

    /* =====================================================================
       11. THE TIMINGS
       ===================================================================== */

    static void theTimings(Scope sc) {
        if (Timing.skip("WorldMarketCheck.theTimings")) return;
        out.println("--- the timings (" + sc.name + ") ---");
        report("the Base on the " + sc.name + " file, read and calibrated, takes under BASE_MS", Timing.within(sc.baseMillis, WorldMarket.BASE_MS),
                String.format(Locale.ROOT, "%.0f ms against %.0f ms", sc.baseMillis, WorldMarket.BASE_MS));
        double[] cold = new double[5], warm = new double[12];
        long[] passes = new long[2];
        for (int k = 0; k < cold.length; k++) {
            WorldMarket.forgetSolves();
            WorldMarket m = sc.market(Nations.Path.RANDOM, 101 + k);
            long t0 = System.nanoTime();
            m.solveYear(Nations.FIRST_YEAR);
            cold[k] = (System.nanoTime() - t0) / 1e6;
            for (Good g : sc.base.goods()) passes[0] += m.passes(g);
        }
        WorldMarket.forgetSolves();
        WorldMarket m = sc.market(Nations.Path.RANDOM, 211);
        m.solveYear(Nations.FIRST_YEAR);
        for (int k = 0; k < warm.length; k++) {
            long t0 = System.nanoTime();
            m.solveYear(Nations.FIRST_YEAR + 1 + k);
            warm[k] = (System.nanoTime() - t0) / 1e6;
            for (Good g : sc.base.goods()) passes[1] += m.passes(g);
        }
        double[] c = cold.clone(), w = warm.clone();
        Arrays.sort(c);
        Arrays.sort(w);
        report("the founding solve (" + Nations.FIRST_YEAR + ", the calendar's first year, every good, from nothing) takes under SOLVE_COLD_MS, the median of " + cold.length, Timing.within(c[c.length / 2], WorldMarket.SOLVE_COLD_MS),
                String.format(Locale.ROOT, "median %.1f ms, slowest %.1f ms; %.1f sweeps a good", c[c.length / 2], c[c.length - 1], passes[0] / (double) cold.length / sc.base.goods().size()));
        report("a January (every good, from last year's answer) takes under SOLVE_WARM_MS, the median of " + warm.length, Timing.within(w[w.length / 2], WorldMarket.SOLVE_WARM_MS),
                String.format(Locale.ROOT, "median %.1f ms, slowest %.1f ms; %.1f sweeps a good", w[w.length / 2], w[w.length - 1], passes[1] / (double) warm.length / sc.base.goods().size()));
        int rounds = 31, each = 20;
        double[] us = new double[rounds];
        List<Good> goods = sc.base.goods();
        double[] ex = new double[goods.size()], im = new double[goods.size()];
        for (int k = 0; k < goods.size(); k++) {
            int gi = m.indexOf(goods.get(k));
            ex[k] = units(m, gi, .01 * m.worldUse(gi));
            im[k] = units(m, gi, .005 * m.worldUse(gi));
        }
        for (int r = -3; r < rounds; r++) {
            long t0 = System.nanoTime();
            for (int i = 0; i < each; i++) for (int k = 0; k < ex.length; k++) sink += m.band(goods.get(k), ex[k], im[k])[0];
            if (r >= 0) us[r] = (System.nanoTime() - t0) / 1e3 / each;
        }
        Arrays.sort(us);
        report("a month's bands for every good take under BAND_MONTH_US, the median of " + rounds + " rounds of " + each, Timing.within(us[rounds / 2], WorldMarket.BAND_MONTH_US, "us"),
                String.format(Locale.ROOT, "median %.0f us for %d goods, slowest round %.0f us", us[rounds / 2], goods.size(), us[rounds - 1]));
    }

    /* =====================================================================
       12. THE CHAIN HOLDS
       ===================================================================== */

    /** The ground seed the stand-in site of section 12 is drawn from (setStandIn: the site a generated world's city is founded at); the single-world sections use 7 as well. */
    static final long CHAIN_GROUND = 7;

    /** The Random seeds section 12 plays beside the Historical path: 9 is the guard's world (its crude left the numbers in 2165 on main), 48213 the game's default. */
    static final long[] CHAIN_SEEDS = {1, 5, 9, Nations.DEFAULT_SEED};

    /** The last January section 12 reads, and the years between the Januaries it reads (from the calendar's first). */
    static final int CHAIN_LAST = 2200, CHAIN_EVERY = 5;

    /** The works section 12 holds beside the recipe's own: the cathode's and the arc furnace's, which no row of the table names. */
    static final String[] EXTRA_WORKS = {"Electrolytic Refinery", "Electric Arc Furnace"};

    /** One works as section 12 reads it: its template, the goods it uses and makes, its feed over its sales at the literals, and what the Januaries read. */
    static final class Works {
        final String name;
        final BuildingsTemplate template;
        final List<Good> used = new ArrayList<>(), made = new ArrayList<>();
        /** The feed (each use at the literal import price) over the sales (each make at the literal export price); NaN where a good has no such literal, and why. */
        double literal = Double.NaN;
        String why = "";
        double low = Double.POSITIVE_INFINITY, high = Double.NEGATIVE_INFINITY;
        String lowAt = "", highAt = "";
        int read, unpriced;
        final List<Double> ratios = new ArrayList<>();

        Works(String name, BuildingsTemplate template) {
            this.name = name;
            this.template = template;
            double feed = 0, sales = 0;
            for (Good g : Good.values()) {
                if (template.uses(g) > 0) used.add(g);
                if (template.makes(g) > 0) made.add(g);
            }
            if (made.isEmpty()) why = "buildings.json lists no makes for it (its slate may live in code)";
            if (used.isEmpty()) why = "buildings.json lists no uses for it";
            for (Good g : used) {
                if (g.importable()) feed += template.uses(g) * g.worldImportPrice();
                else why = g + " has no import literal";
            }
            for (Good g : made) {
                if (g.exportable()) sales += template.makes(g) * g.worldExportPrice();
                else why = g + " has no export literal";
            }
            if (why.isEmpty()) literal = feed / sales;
        }

        /** The feed over the sales in the game's money from the market's bands (a use at the ceiling, a make at the floor), NaN where a good is not priced there. */
        double margin(WorldMarket m) {
            double feed = 0, sales = 0;
            for (Good g : used) {
                double c = m.band(g, 0, 0)[1];
                if (!(c > 0) || !Double.isFinite(c)) return Double.NaN;
                feed += template.uses(g) * c;
            }
            for (Good g : made) {
                double f = m.band(g, 0, 0)[0];
                if (!(f > 0) || !Double.isFinite(f)) return Double.NaN;
                sales += template.makes(g) * f;
            }
            return feed / sales;
        }
    }

    /** A good's played floor over its literal export price, NaN where the market has no positive floor for it. */
    static double floorOver(WorldMarket m, Good g) {
        double f = m.band(g, 0, 0)[0];
        return f > 0 && Double.isFinite(f) ? f / g.worldExportPrice() : Double.NaN;
    }

    /** The templates of buildings.json by name, through the game's own loader; null where it cannot be read. */
    static Map<String, BuildingsTemplate> buildingsByName() {
        List<BuildingsTemplate> templates = new BuildingCatalog().load();
        if (templates == null) return null;
        Map<String, BuildingsTemplate> byName = new HashMap<>();
        for (BuildingsTemplate b : templates) byName.put(b.getName(), b);
        return byName;
    }

    /** A figure's worst in each fifty years, as a line. */
    static String windowed(double[] worst) {
        StringBuilder s = new StringBuilder("[");
        for (int k = 0; k < worst.length; k++) s.append(k > 0 ? ", " : "").append(String.format(Locale.ROOT, "%.3f", worst[k]));
        return s.append("]").toString();
    }

    static void theChainHolds(Scope sc) {
        if (!sc.name.equals("full")) {
            out.println("--- the chain holds rod and wire on cathode, and every works near its literal margin: the full file's section, not run on the " + sc.name + " ---");
            return;
        }
        out.println("--- the chain holds rod and wire on cathode, and every works near its literal margin (" + sc.name + ") ---");
        long t0 = System.nanoTime();
        Map<String, BuildingsTemplate> byName = buildingsByName();
        if (byName == null) {
            assertTrue("buildings.json is read", false);
            return;
        }
        List<String> names = new ArrayList<>();
        for (WorldChain.Row row : WorldChain.RECIPE) if (row.works() != null && !names.contains(row.works())) names.add(row.works());
        for (String extra : EXTRA_WORKS) if (!names.contains(extra)) names.add(extra);
        List<Works> works = new ArrayList<>();
        for (String name : names) {
            BuildingsTemplate b = byName.get(name);
            if (b == null) assertTrue("the works " + name + " is in buildings.json", false);
            else works.add(new Works(name, b));
        }
        Tally rod = new Tally(), medians = new Tally();
        List<Double> wireRatios = new ArrayList<>(), rodRatios = new ArrayList<>();
        Worst rodWorst = new Worst(), wireWorst = new Worst();
        double[] rodWindow = new double[(CHAIN_LAST - Nations.FIRST_YEAR) / 50 + 1], wireWindow = new double[rodWindow.length];
        int rodUnread = 0, wireUnread = 0;
        String[] sites = {"reference site", "stand-in site"};
        for (int p = 0; p <= CHAIN_SEEDS.length; p++) {
            Nations.Path path = p == 0 ? Nations.Path.HISTORICAL : Nations.Path.RANDOM;
            long seed = p == 0 ? Nations.DEFAULT_SEED : CHAIN_SEEDS[p - 1];
            for (int site = 0; site < 2; site++) {
                WorldMarket m = sc.market(path, seed);
                if (site == 1) m.setStandIn(CHAIN_GROUND, false);
                for (int y = Nations.FIRST_YEAR; y <= CHAIN_LAST; y += CHAIN_EVERY) {
                    m.solveYear(y);
                    String where = path + (path == Nations.Path.RANDOM ? " seed " + seed : "") + ", the " + sites[site] + ", " + y;
                    double cathode = floorOver(m, Good.COPPER), rodOver = floorOver(m, Good.COPPER_ROD), wireOver = floorOver(m, Good.WIRE_CABLE);
                    double onCathode = rodOver / cathode, onRod = wireOver / rodOver;
                    if (Double.isNaN(onCathode)) rodUnread++;
                    else {
                        rod.see(Math.abs(onCathode - 1) <= ROD_ON_CATHODE, () -> where + ": rod's floor over its literal is " + onCathode + " of cathode's");
                        rodRatios.add(onCathode);
                        rodWorst.see(Math.abs(onCathode - 1), () -> String.format(Locale.ROOT, "%.4f, %s", onCathode, where));
                        rodWindow[(y - Nations.FIRST_YEAR) / 50] = Math.max(rodWindow[(y - Nations.FIRST_YEAR) / 50], Math.abs(onCathode - 1));
                    }
                    if (Double.isNaN(onRod)) wireUnread++;
                    else {
                        wireRatios.add(onRod);
                        wireWorst.see(Math.abs(onRod - 1), () -> String.format(Locale.ROOT, "%.4f, %s", onRod, where));
                        wireWindow[(y - Nations.FIRST_YEAR) / 50] = Math.max(wireWindow[(y - Nations.FIRST_YEAR) / 50], Math.abs(onRod - 1));
                    }
                    for (Works w : works) {
                        if (Double.isNaN(w.literal)) continue;
                        double margin = w.margin(m);
                        if (Double.isNaN(margin)) {
                            w.unpriced++;
                            continue;
                        }
                        double r = margin / w.literal;
                        w.read++;
                        w.ratios.add(r);
                        if (r < w.low) {
                            w.low = r;
                            w.lowAt = where;
                        }
                        if (r > w.high) {
                            w.high = r;
                            w.highAt = where;
                        }
                    }
                }
            }
        }
        tally("rod stands on cathode: rod's floor over its literal export price over cathode's, within ROD_ON_CATHODE (" + ROD_ON_CATHODE + ") of 1, every fifth January " + Nations.FIRST_YEAR + "-" + CHAIN_LAST + ", Historical and Random seeds 1, 5, 9, 48213, both sites", rod);
        Collections.sort(wireRatios);
        Collections.sort(rodRatios);
        double wireMedian = wireRatios.get(wireRatios.size() / 2);
        assertTrue(String.format(Locale.ROOT, "wire stands on rod: the median over all the Januaries read of wire's floor over its literal export price over rod's is %.4f, within WIRE_ON_ROD_MEDIAN (%s) of 1", wireMedian, WIRE_ON_ROD_MEDIAN), Math.abs(wireMedian - 1) <= WIRE_ON_ROD_MEDIAN);
        out.printf(Locale.ROOT, "spread over the Januaries read, 5th percentile, median, 95th percentile: rod over cathode %.3f %.3f %.3f; wire over rod %.3f %.3f %.3f%n", rodRatios.get(rodRatios.size() / 20), rodRatios.get(rodRatios.size() / 2), rodRatios.get(rodRatios.size() * 19 / 20),
                wireRatios.get(wireRatios.size() / 20), wireMedian, wireRatios.get(wireRatios.size() * 19 / 20));
        out.println("the worst: rod over cathode " + rodWorst + "; wire over rod " + wireWorst + "; Januaries with no floor for them: " + rodUnread + " and " + wireUnread);
        out.println("the worst by 50 years from " + Nations.FIRST_YEAR + ": rod over cathode " + windowed(rodWindow) + "; wire over rod " + windowed(wireWindow));
        out.printf("%-26s %8s  %s%n", "works", "literal", "worst low, worst high (feed over sales, then over its literal; the path, seed, site and year); Januaries read, not priced");
        for (Works w : works) {
            if (Double.isNaN(w.literal)) {
                out.printf("%-26s not priced at the literals, not asserted: %s%n", w.name, w.why);
            } else if (w.read == 0) {
                out.printf(Locale.ROOT, "%-26s %8.3f  not priced by the market in any January read (%d), not asserted%n", w.name, w.literal, w.unpriced);
            } else {
                out.printf(Locale.ROOT, "%-26s %8.3f  low %.3f (%.3f) %s; high %.3f (%.3f) %s; %d, %d%n", w.name, w.literal, w.low * w.literal, w.low, w.lowAt,
                        w.high * w.literal, w.high, w.highAt, w.read, w.unpriced);
                List<Double> sorted = new ArrayList<>(w.ratios);
                Collections.sort(sorted);
                double median = sorted.get(sorted.size() / 2);
                out.printf(Locale.ROOT, "%-26s          over its literal: 5th percentile %.3f, median %.3f, 95th percentile %.3f%n", "", sorted.get(sorted.size() / 20), median, sorted.get(sorted.size() * 19 / 20));
                medians.see(Math.abs(median - 1) <= WORKS_MEDIAN, () -> w.name + ": the median of its feed over its sales is " + median + " of what it is at the literals");
            }
        }
        tally("every priced works' median feed over its sales (uses at the ceiling, makes at the floor), over all the Januaries read, is within WORKS_MEDIAN (" + WORKS_MEDIAN + ") of what it is at the literals", medians);
        out.printf(Locale.ROOT, "section 12 took %.1f s%n", (System.nanoTime() - t0) / 1e9);
    }

    /* =====================================================================
       13. THE RECIPE TABLE IS buildings.json's
       ===================================================================== */

    /** A good's mid-band literal price, (import + export) / 2 for a two-sided good, its one literal otherwise, NaN for a good with neither. */
    static double mid(Good g) {
        if (g.importable() && g.exportable()) return (g.worldImportPrice() + g.worldExportPrice()) / 2;
        if (g.importable()) return g.worldImportPrice();
        if (g.exportable()) return g.worldExportPrice();
        return Double.NaN;
    }

    static void theRecipeTable() {
        out.println("--- the recipe table is buildings.json's: each row's shares are its works' uses over its sales at the literals' mid-band prices ---");
        Map<String, BuildingsTemplate> byName = buildingsByName();
        if (byName == null) {
            assertTrue("buildings.json is read", false);
            return;
        }
        Tally shares = new Tally();
        List<String> notRecomputed = new ArrayList<>(), leftOut = new ArrayList<>(), seen = new ArrayList<>(), off = new ArrayList<>();
        int priceFile = 0, rows = 0;
        double worst = 0;
        String worstAt = "";
        for (WorldChain.Row row : WorldChain.RECIPE) {
            rows++;
            if (row.works() == null) {
                priceFile++;
                continue;
            }
            BuildingsTemplate w = byName.get(row.works());
            if (w == null) {
                shares.see(false, () -> row.made() + ": its works " + row.works() + " is not in buildings.json");
                continue;
            }
            double makes = w.makes(row.made()), out1 = mid(row.made()), sold = 0;
            // the works' sales are all it makes at the literals' mid-band prices: a works with two goods (meat works, bakery, crude unit) spreads its feed over both, so each carries the same share
            for (Good g : Good.values()) if (w.makes(g) > 0 && mid(g) > 0) sold += w.makes(g) * mid(g);
            if (!(makes > 0) || !(out1 > 0)) {
                List<Good> lists = new ArrayList<>();
                for (Good g : Good.values()) if (w.makes(g) > 0) lists.add(g);
                notRecomputed.add(row.made() + " (" + row.works() + (!(makes > 0) ? " does not list it in makes: " + (lists.isEmpty() ? "no makes at all, its slate lives in code" : "it lists " + lists) : " has no literal price for it") + ")");
                continue;
            }
            for (int k = 0; k < row.inputs().length; k++) {
                Good in = row.inputs()[k];
                double price = mid(in), share = w.uses(in) * price / sold, alone = w.uses(in) * price / (makes * out1), dial = row.shares()[k];
                if (!(price > 0)) {
                    notRecomputed.add(row.made() + " on " + in + " (" + in + " has no literal price)");
                    continue;
                }
                if (row.made() == Good.WIRE_CABLE) {
                    out.printf(Locale.ROOT, "%s on %s is Jerus's call (star 3), not the works' recipe: the works' figure %.3f, the dial %.2f; not asserted%n", row.made(), in, share, dial);
                    continue;
                }
                double miss = Math.abs(share - dial);
                if (miss > worst) {
                    worst = miss;
                    worstAt = row.made() + " on " + in + ": " + String.format(Locale.ROOT, "%.3f against the dial %.2f", share, dial);
                }
                shares.see(miss <= RECIPE_TOL, () -> row.made() + " on " + in + ": the works " + row.works() + " gives " + share + ", the dial is " + dial);
                if (miss > RECIPE_TOL) off.add(String.format(Locale.ROOT, "%s on %s: the works %s gives %.4f (%.4f of the row's good alone), the dial is %.2f (off by %.4f)", row.made(), in, row.works(), share, alone, dial, miss));
            }
            for (Good g : Good.values()) {
                if (!(w.uses(g) > 0)) continue;
                boolean named = false;
                for (Good in : row.inputs()) named |= in == g;
                String key = row.works() + "|" + g;
                if (named || seen.contains(key)) continue;
                seen.add(key);
                double price = mid(g);
                leftOut.add(String.format(Locale.ROOT, "%s uses %s, which no row names: %s of %s's price", row.works(), g, price > 0 ? String.format(Locale.ROOT, "%.3f", w.uses(g) * price / sold) : "no literal price", row.made()));
            }
        }
        tally("every row's shares are its works' uses over its sales (all it makes) at the literals' mid-band prices, within RECIPE_TOL (" + RECIPE_TOL + ", absolute), wire's rod apart", shares);
        out.println("the widest row off its works: " + worstAt + " (" + String.format(Locale.ROOT, "%.3f", worst) + "); every row off the dial by more than RECIPE_TOL:" + (off.isEmpty() ? " none" : ""));
        for (String s : off) out.println("    " + s);
        out.println(rows + " rows; " + priceFile + " are the price file's own (no works between them); not recomputed:" + (notRecomputed.isEmpty() ? " none" : ""));
        for (String s : notRecomputed) out.println("    " + s);
        out.println("inputs the works use that the row leaves out, with the share of the row's price they would be:" + (leftOut.isEmpty() ? " none" : ""));
        for (String s : leftOut) out.println("    " + s);
    }

    /* =====================================================================
       14. THE GUARD KEEPS THE SOLVE TO THE NUMBERS
       ===================================================================== */

    /** The drawn world whose crude left the numbers in 2165 on main (spec-world-prices 3.7), and the last January section 14 plays it to. */
    static final long GUARD_SEED = 9;
    static final int GUARD_LAST = 2170;

    static void theGuard(Scope sc) {
        out.println("--- the guard keeps the solve to the numbers (" + sc.name + ") ---");
        // a good's solve started with one maker's price not a number: the guard puts back the plain step, and the solve ends clear
        Good crude = sc.base.goods().contains(Good.CRUDE) ? Good.CRUDE : sc.base.goods().get(0);
        WorldMarket.Traded t = sc.base.traded[sc.base.indexOf[crude.ordinal()]];
        int n = sc.base.nationCount();
        WorldMarket first = sc.market(Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
        first.solveYear(Nations.FIRST_YEAR);
        double[] gh = first.gHat(), phat = new double[n], lam = {1};
        Arrays.fill(phat, 1);
        phat[t.makers[0]] = Double.NaN;
        long before = WorldMarket.guards();
        int sweeps = WorldMarket.solve(t, gh, null, true, 1, WorldMarket.SOLVE_TOL, phat, lam, 0, new WorldMarket.Work(n));
        long put = WorldMarket.guards() - before;
        double[] make = madeIn(sc.data, t, n), sales = sales(sc.data, t, phat, gh);
        double worst = 0, total = 0, index = 0;
        for (int i : t.makers) {
            worst = Math.max(worst, Math.abs(sales[i] / (make[i] * gh[i] * lam[0] * Math.pow(phat[i], 1 + WorldMarket.SUPPLY_ELASTICITY)) - 1));
            total += make[i];
            index += make[i] * phat[i];
        }
        double level = Math.abs(index / total - 1), clears = worst;
        assertTrue(crude + "'s solve started with a maker's price that is not a number: the guard put back the plain step " + put + " times (at least 1)", put >= 1);
        assertTrue("...and the solve ended in " + sweeps + " sweeps, under SOLVE_MAX_PASSES (" + WorldMarket.SOLVE_MAX_PASSES + ")", sweeps < WorldMarket.SOLVE_MAX_PASSES);
        assertTrue("...and its answer clears (sales over supply off by " + String.format(Locale.ROOT, "%.2e", clears) + ") and meets its level (off by " + String.format(Locale.ROOT, "%.2e", level) + ") to SOLVE_TOL, recomputed from the pairs",
                clears <= WorldMarket.SOLVE_TOL * (1 + 1e-9) && level <= WorldMarket.SOLVE_TOL * (1 + 1e-9));
        if (!sc.name.equals("full")) {
            out.println("the drawn world whose crude left the numbers on main is played on the full file only, not on the " + sc.name);
            return;
        }
        WorldMarket.forgetSolves();
        before = WorldMarket.guards();
        long t0 = System.nanoTime();
        WorldMarket m = sc.market(Nations.Path.RANDOM, GUARD_SEED);
        int most = 0;
        String mostAt = "";
        for (int y = Nations.FIRST_YEAR; y <= GUARD_LAST; y++) {
            m.solveYear(y);
            for (Good g : sc.base.goods()) {
                int took = m.passes(g);
                if (took > most) {
                    most = took;
                    mostAt = g + " " + y;
                }
            }
        }
        long fired = WorldMarket.guards() - before;
        int took = most;
        out.printf(Locale.ROOT, "Random seed %d, every January %d-%d (%.1f s): the guard fired %d times%n", GUARD_SEED, Nations.FIRST_YEAR, GUARD_LAST, (System.nanoTime() - t0) / 1e9, fired);
        assertTrue("no good's solve reached SOLVE_MAX_PASSES (" + WorldMarket.SOLVE_MAX_PASSES + ") in any January " + Nations.FIRST_YEAR + "-" + GUARD_LAST + " of Random seed " + GUARD_SEED + ": the worst took " + took + " sweeps (" + mostAt + ")",
                took < WorldMarket.SOLVE_MAX_PASSES);
        // A reading, not an assertion (REV-CU2-1): guards() counts the process's every January, whoever solved it.
        if (fired > 0) out.println("the guard fired " + fired + " times during that run (the process's count): the world whose crude left the numbers on main still reaches it");
        else out.println("guards 0 on the chained solve: it starts a root on the year's level and anchors it, so this world no longer leaves the numbers; the started-from-NaN solve above causes the condition");
    }

    /* =====================================================================
       15. THE COPPER CHARGE
       ===================================================================== */

    /** The years the charge's swing is read over: the calendar's first to well past the paths' end. */
    static final int CHARGE_FROM = 1900, CHARGE_TO = 2230;

    /** The years round the record's peaks the local maxima are listed over (printed beside them, not asserted). */
    static final int PEAKS_FROM = 1990, PEAKS_TO = 2030;

    static void theCopperCharge(Scope sc) {
        out.println("--- the copper charge: the swing's peaks, band, purity and seed; concentrate carries it, anode its refining part, nothing else (" + sc.name + ") ---");
        Nations hist = new Nations(sc.data, Nations.Path.HISTORICAL, Nations.DEFAULT_SEED);
        Nations[] paths = {hist, new Nations(sc.data, Nations.Path.RANDOM, 1), new Nations(sc.data, Nations.Path.RANDOM, 5)};
        Tally peaks = new Tally();
        List<String> heights = new ArrayList<>();
        for (int y = Nations.CHARGE_PEAK_YEAR; y <= 2024; y += (int) Nations.CHARGE_YEARS) {
            int yy = y;
            double here = hist.copperChargeSwing(y), before = hist.copperChargeSwing(y - 1), after = hist.copperChargeSwing(y + 1);
            peaks.see(here > before && here > after && here > Nations.CHARGE_MID && y >= PEAKS_FROM && y <= PEAKS_TO, () -> "the Historical swing at " + yy + " is " + here + ", " + before + " the year before and " + after + " the year after");
            heights.add(String.format(Locale.ROOT, "%d %.4f", y, here));
        }
        tally("the Historical swing has a local maximum above CHARGE_MID on CHARGE_PEAK_YEAR (" + Nations.CHARGE_PEAK_YEAR + ") and every CHARGE_YEARS (" + (int) Nations.CHARGE_YEARS + ") after it through 2024", peaks);
        List<String> maxima = new ArrayList<>();
        for (int y = PEAKS_FROM; y <= PEAKS_TO; y++) {
            if (hist.copperChargeSwing(y) > hist.copperChargeSwing(y - 1) && hist.copperChargeSwing(y) > hist.copperChargeSwing(y + 1)) maxima.add(y + " " + String.format(Locale.ROOT, "%.4f", hist.copperChargeSwing(y)));
        }
        out.println("peaks " + heights + "; every local maximum " + PEAKS_FROM + "-" + PEAKS_TO + ": " + maxima);
        double reach = Nations.CHARGE_HALF * (Nations.CHARGE_MAIN_WEIGHT + Nations.CHARGE_SLOW_WEIGHT);
        Tally band = new Tally(), pure = new Tally(), record = new Tally();
        double lowest = Double.POSITIVE_INFINITY, highest = Double.NEGATIVE_INFINITY;
        for (Nations nations : paths) {
            Nations again = new Nations(sc.data, nations.path(), nations == hist ? Nations.DEFAULT_SEED : nations == paths[1] ? 1 : 5);
            for (int y = CHARGE_FROM; y <= CHARGE_TO; y++) {
                double s = nations.copperChargeSwing(y);
                int yy = y;
                lowest = Math.min(lowest, s);
                highest = Math.max(highest, s);
                band.see(s >= 0 && s >= Nations.CHARGE_MID - reach - ARITHMETIC && s <= Nations.CHARGE_MID + reach + ARITHMETIC, () -> nations.path() + " " + yy + ": the swing is " + s + ", the band is " + Nations.CHARGE_MID + " +- " + reach);
                pure.see(same(s, nations.copperChargeSwing(y)) && same(s, again.copperChargeSwing(y)), () -> nations.path() + " " + yy + ": the swing is not the same twice, nor on a second Nations of the same path and seed");
                record.see(!nations.copperChargeRecorded(y), () -> nations.path() + " " + yy + " has a recorded charge: no yearly record may ship (Pending WP1-C)");
            }
        }
        tally("the swing stays within CHARGE_MID +- CHARGE_HALF x the two weights' sum (" + Nations.CHARGE_MID + " +- " + String.format(Locale.ROOT, "%.4f", reach) + ") and at or above 0, " + CHARGE_FROM + "-" + CHARGE_TO + ", Historical and Random seeds 1 and 5", band);
        out.println("the swing ran from " + String.format(Locale.ROOT, "%.4f to %.4f", lowest, highest));
        tally("the swing is the same twice, and on a second Nations of the path and seed (a pure function)", pure);
        tally("no year has a yearly record (copperChargeRecorded is false on every year of every path read)", record);
        boolean differs = false;
        for (int y = CHARGE_FROM; y <= CHARGE_TO && !differs; y++) differs = !same(paths[1].copperChargeSwing(y), paths[2].copperChargeSwing(y));
        assertTrue("two Random seeds (1 and 5) swing differently", differs);

        // in a solved January: concentrate carries the charge, anode its refining part, no other good any
        WorldMarket.Base base = sc.base;
        if (base.indexOf[Good.COPPER_CONC.ordinal()] < 0 || base.indexOf[Good.ANODE.ordinal()] < 0) {
            out.println("this file does not model concentrate and anode: the solved Januaries are not read");
            return;
        }
        Tally carried = new Tally(), part = new Tally(), none = new Tally();
        Object[][] januaries = {{Nations.Path.HISTORICAL, Nations.DEFAULT_SEED, 2010}, {Nations.Path.HISTORICAL, Nations.DEFAULT_SEED, 2100}, {Nations.Path.RANDOM, 5L, 2100}};
        for (Object[] j : januaries) {
            Nations nations = new Nations(sc.data, (Nations.Path) j[0], (Long) j[1]);
            int year = (Integer) j[2];
            WorldMarket m = new WorldMarket(base, nations);
            m.solveYear(year);
            String where = j[0] + " seed " + j[1] + " " + year;
            double level = m.muv() < 0 ? 1 : m.levelTarget(m.muv());
            double want = nations.copperChargeSwing(year) * level / base.refMuv * base.refCopper;
            double conc = m.charge(Good.COPPER_CONC), anode = m.charge(Good.ANODE);
            carried.see(conc > 0 && near(conc, want, ARITHMETIC), () -> where + ": concentrate's charge is " + conc + ", the swing over the manufactures index over the reference year's, in the copper's base-year price, is " + want);
            part.see(near(anode / conc, WorldMarket.REFINING_PART, ARITHMETIC), () -> where + ": anode's charge over concentrate's is " + anode / conc + ", REFINING_PART is " + WorldMarket.REFINING_PART);
            for (Good g : base.goods()) {
                if (g == Good.COPPER_CONC || g == Good.ANODE) continue;
                none.see(m.charge(g) == 0, () -> where + ": " + g + " carries a charge of " + m.charge(g));
            }
        }
        tally("a solved January's concentrate carries the part-1 charge: the swing times the manufactures index's level over the reference year's, times the copper's, Historical 2010 and 2100 and Random seed 5 at 2100", carried);
        tally("...and anode REFINING_PART of it, to 1e-12", part);
        tally("...and no other good a charge", none);
    }
}
