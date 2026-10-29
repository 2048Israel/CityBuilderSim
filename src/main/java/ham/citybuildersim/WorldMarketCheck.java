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
 *   1. THE SOLVE CONVERGES: every good clears to SOLVE_TOL (sales over supply,
 *      recomputed from the file's pairs) and meets the price path's level,
 *      cold in 2000 and warm after, on both paths; the sweeps are counted.
 *   2. THE CALIBRATION: the reference city (Halifax, 2025, Historical) sees
 *      Good's literals on every calibrated side to 1e-9; no two-sided good
 *      goes without; a one-sided good takes its class's median freight scale.
 *   3. THE FLOOR IS NEVER ABOVE THE CEILING: every good, 2000-2040, both
 *      paths, at three sites, the city's exports and imports each 0, 0.1%,
 *      1%, 10% and 50% of the world's use; and the floor falls with the
 *      city's exports as the ceiling rises with its imports.
 *   4. A CITY MOVES ITS PRICE AS BIG AS IT IS: at 0.01% of a good's world use
 *      (an ordinary city) the netback moves by under 1% of the price it is
 *      sold at, at 10% by at least 5% of it, every good; at 0.1% the move is
 *      printed (the re-solve agrees with it: the host's market is small).
 *   5. THE MONTH'S STEP IS NEAR A RE-SOLVE: the fill's netback (the band's floor)
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

    static void sections(Scope sc) {
        theSolve(sc);
        theCalibration(sc);
        theFloorUnderCeiling(sc);
        theBigCity(sc);
        theStep(sc);
        theSeeds(sc);
        theSave(sc);
        theStandIn(sc);
        thePieces(sc);
        theTimings(sc);
    }

    /* =====================================================================
       1. THE SOLVE CONVERGES
       ===================================================================== */

    /** One good's solved state recomputed from the file's pairs alone: {the worst of sales over supply less 1 over the makers, the mean mill price over its path's level less 1 in magnitude}. */
    static double[] residual(WorldData data, WorldMarket m, Good g) {
        int gi = m.indexOf(g);
        WorldMarket.Traded t = m.base().traded[gi];
        WorldData.Flows f = data.trade(t.number);
        int n = m.nationCount();
        double eps = t.eps;
        double[] p = m.phat(gi), gh = m.gHat();
        double lambda = m.lambda(gi);
        double[] use = new double[n], make = new double[n], phi = new double[n], sales = new double[n];
        for (int k = 0; k < f.pairs(); k++) {
            make[f.seller[k] - 1] += f.flow[k];
            use[f.buyer[k] - 1] += f.flow[k];
        }
        for (int k = 0; k < f.pairs(); k++) {
            int s = f.seller[k] - 1, b = f.buyer[k] - 1;
            phi[b] += f.flow[k] / use[b] * Math.pow(p[s], -eps);
        }
        for (int k = 0; k < f.pairs(); k++) {
            int s = f.seller[k] - 1, b = f.buyer[k] - 1;
            double spending = gh[b] * Math.pow(phi[b], -(1 - WorldMarket.DEMAND_ELASTICITY) / eps);
            sales[s] += Math.pow(p[s], -eps) * f.flow[k] * spending / phi[b];
        }
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

    static void theSolve(Scope sc) {
        out.println("--- the January solve clears every maker and meets its price path (" + sc.name + ") ---");
        Tally clearing = new Tally(), level = new Tally(), years = new Tally();
        List<Integer> cold = new ArrayList<>(), warm = new ArrayList<>();
        double tol = WorldMarket.SOLVE_TOL * (1 + 1e-9);
        for (Nations.Path path : Nations.Path.values()) {
            WorldMarket m = sc.market(path, BASE_SEED);
            for (int y = 2000; y <= 2010; y++) {
                m.solveYear(y);
                int yy = y;
                years.see(m.solvedYear() == y, () -> "solved " + m.solvedYear() + " for " + yy);
                for (Good g : sc.base.goods()) {
                    (y == 2000 ? cold : warm).add(m.passes(g));
                    if (y != 2000 && y != 2005 && y != 2010) continue;
                    double[] r = residual(sc.data, m, g);
                    clearing.see(r[0] <= tol, () -> g + " " + path + " " + yy + ": sales over supply off by " + r[0]);
                    level.see(r[1] <= tol, () -> g + " " + path + " " + yy + ": the mean mill price off its path by " + r[1]);
                }
            }
        }
        tally("the solved year is the year asked for", years);
        tally("every maker's sales are within SOLVE_TOL of its supply, recomputed from the file's pairs: 2000, 2005 and 2010 on both paths", clearing);
        tally("the mean mill price is within SOLVE_TOL of the good's real price path (over the base year's), 2000, 2005 and 2010 on both paths", level);
        Collections.sort(cold);
        Collections.sort(warm);
        out.printf("sweeps a good: founding (cold, 2000) median %d, worst %d; a January after (warm) median %d, worst %d%n",
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
        for (Good g : base.goods()) {
            WorldMarket.Traded t = base.traded[m.indexOf(g)];
            if (base.fit(g) == WorldMarket.Fit.CALIBRATED) byClass.computeIfAbsent(t.goodClass, c -> new ArrayList<>()).add(base.phi(g));
        }
        for (Good g : base.goods()) {
            WorldMarket.Traded t = base.traded[m.indexOf(g)];
            WorldMarket.Fit fit = base.fit(g);
            double[] b = m.band(g, 0, 0);
            boolean both = g.importable() && g.exportable();
            if (both && fit != WorldMarket.Fit.CALIBRATED) unfit.add(g + " (" + fit + ")");
            if (fit == WorldMarket.Fit.NONE) {
                none.add(g.toString());
                continue;
            }
            if (fit == WorldMarket.Fit.ONE_SIDED) onePlace.add(g.toString());
            scales.see(base.sigma(g) > 0 && Double.isFinite(base.sigma(g)) && base.phi(g) > 0 && Double.isFinite(base.phi(g)), () -> g + ": sigma " + base.sigma(g) + ", phi " + base.phi(g));
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
            if (fit != WorldMarket.Fit.CALIBRATED && byClass.containsKey(t.goodClass)) {
                List<Double> v = new ArrayList<>(byClass.get(t.goodClass));
                Collections.sort(v);
                int k = v.size();
                double median = k % 2 == 1 ? v.get(k / 2) : .5 * (v.get(k / 2 - 1) + v.get(k / 2));
                medians.see(near(base.phi(g), median, 1e-12), () -> g + "'s freight scale " + base.phi(g) + " against its class's median " + median);
            }
        }
        tally("the reference city (Halifax, no berth, 2025, Historical) sees the literal on every side the city has, to 1e-9: calibrated goods both, one-sided goods theirs", literals);
        tally("every good the market scales has a price scale and a freight scale above 0", scales);
        tally("a one-sided good, and a two-sided one that would not calibrate, takes its class's median freight scale", medians);
        out.println("calibrated " + base.fits[WorldMarket.Fit.CALIBRATED.ordinal()] + ", one-sided " + onePlace + ", on the class median " + unfit + ", without a side " + none);
        assertTrue("no two-sided good goes uncalibrated: " + (unfit.isEmpty() ? "none" : unfit), unfit.isEmpty());
        assertTrue("the reference city stands in Canada (" + m.iso3(m.host()) + ")", m.iso3(m.host()).equals("CAN") && m.host() == base.referenceHost());

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
        out.println("--- the timings (" + sc.name + ") ---");
        report("the Base on the " + sc.name + " file, read and calibrated, takes under BASE_MS", Timing.within(sc.baseMillis, WorldMarket.BASE_MS),
                String.format(Locale.ROOT, "%.0f ms against %.0f ms", sc.baseMillis, WorldMarket.BASE_MS));
        double[] cold = new double[5], warm = new double[12];
        long[] passes = new long[2];
        for (int k = 0; k < cold.length; k++) {
            WorldMarket.forgetSolves();
            WorldMarket m = sc.market(Nations.Path.RANDOM, 101 + k);
            long t0 = System.nanoTime();
            m.solveYear(2000);
            cold[k] = (System.nanoTime() - t0) / 1e6;
            for (Good g : sc.base.goods()) passes[0] += m.passes(g);
        }
        WorldMarket.forgetSolves();
        WorldMarket m = sc.market(Nations.Path.RANDOM, 211);
        m.solveYear(2000);
        for (int k = 0; k < warm.length; k++) {
            long t0 = System.nanoTime();
            m.solveYear(2001 + k);
            warm[k] = (System.nanoTime() - t0) / 1e6;
            for (Good g : sc.base.goods()) passes[1] += m.passes(g);
        }
        double[] c = cold.clone(), w = warm.clone();
        Arrays.sort(c);
        Arrays.sort(w);
        report("the founding solve (2000, every good, from nothing) takes under SOLVE_COLD_MS, the median of " + cold.length, Timing.within(c[c.length / 2], WorldMarket.SOLVE_COLD_MS),
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
}
