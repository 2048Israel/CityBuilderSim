package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * The central banks of the eight moneys the vault holds and the price of money the city reads (batch XR4, interest abroad; spec-exchange-rates 3.8,
 * Jerus's calls 9 and 11): PolicyRates' monthly rate of each bank - the real table to December 2025 on Historical, the rule after it and from
 * February 2000 on Random - held to the table where the table is the truth, to the rule's own constants where it rules, and to the city where the
 * US's rate stands for the world's price of money (DebtManager.worldRate()).
 *
 * WHY. The world's price of money was a constant 2% against a world whose inflation is a path, so the world's real rate swung with the path and the
 * city's real gap with it. A table can be mistranscribed, a rule can step off its grid or through zero, a rate can be handed to the city late or
 * not at all, a save can lose the state that the next month's step reads: each is checked here against the constants the class publishes, never a
 * formula copied from it.
 *
 * THE DATA. Section 1 needs no world file. The rest need the full world-1.bin (-Dcbs.world=<path>, WorldData.locate()), for its 196 nations and their
 * price levels; a line says when it is not named, and those sections did not run.
 *
 * What this has to prove:
 *   1. THE TABLE: each of the eight has a rate in every one of the 312 months, spot values agree with the file's history, historical() is NaN outside
 *      the table and for a code that is not one of the eight, and a table that is not whole is refused naming its line.
 *   2. HISTORICAL REPLAYS, THEN THE RULE: a PolicyRates over a Historical WorldMoneys equals the table in every month to 311 and is ruling from 312;
 *      from 312 to 1,199 every month's change of every money is a whole multiple of STEP, no rate falls below LOWER_BOUND, no rate is further than
 *      STEP from its shadow, and no rate moves past it.
 *   3. RANDOM RUNS THE RULE FROM FEBRUARY 2000: month 0 is the table's January 2000; ruling from month 1; two builds on one seed give identical
 *      rates; the US's mean real rate is within a point of its neutral real rate; the target is the one targetFor() gives for the inflation read, the
 *      real target rises TAYLOR_WEIGHT for each point of inflation over the bank's target, never goes under LOWER_BOUND, and the shadow closes its gap
 *      to the target by the half-life.
 *   4. THE CITY READS THE US'S RATE: a city with the world file has worldRate() equal to the US's policy rate every month; the real rate gap reads
 *      it, and the tied rate, the currency rate, the foreign rate and the peg spread's bounds with it; the equity's required return stays on the constant.
 *   5. NO WORLD, THE CONSTANT: a bare DebtManager, a city on the literals and a city quieted for the D$ all read WORLD_BASE_RATE.
 *   6. THE SAVE: toSaveArray/restore continues bit for bit on both paths, a save that is not this one's is refused and changes nothing, nothing in the
 *      save is a number that is none, and a city saved and loaded has the same eight rates, the same price of money and the same history.
 *
 * Every fixture causes its condition: a Historical moneys for the table's replay, a Random one for the rule from month 1, a town for the city's
 * reading, and a table with one line wrong for each refusal.
 */
public class InterestAbroadCheck {

    static int fails = 0;
    /** Where the harness prints: the console. */
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(java.io.OutputStream.nullOutputStream());

    /** The seed the single-world sections use, and the others the mean real rate is counted over. */
    static final long SEED = 9_101;
    static final long[] SEEDS = {9_101, 9_102, 9_103, 9_104, 9_105};

    /** The months a long run covers: January 2000 to December 2099, the nations' path (the last month stepped is 1,199). */
    static final int LONG = 1_200;

    /** Two rates or two logs that should be one differ by less than this. */
    static final double EXACT = 1e-12;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-130s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Two figures that should be one, to a tolerance. */
    static void close(String label, double got, double wanted, double tol) {
        boolean ok = Math.abs(got - wanted) <= tol;
        assertTrue(String.format("%s: %.8f against %.8f", label, got, wanted), ok);
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

    /** The month index of a calendar month: months since January 2000. */
    static int at(int year, int month) { return (year - Nations.FIRST_YEAR) * 12 + month - 1; }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        theTable();
        out.println("--- the full file, when it is named ---");
        Path fullPath = WorldData.locate();
        String named = System.getProperty(WorldData.PROPERTY);
        if (fullPath == null) {
            if (named != null && !named.isBlank()) assertTrue("the file -D" + WorldData.PROPERTY + " names is there (" + named + ")", false);
            else out.println("world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": sections 2 to 6 did not run");
        } else {
            out.println("reading " + fullPath.toAbsolutePath());
            try (WorldData data = WorldData.open(fullPath)) {
                World world = new World(data);
                theReplay(world);
                theRandomRule(world);
            }
            theCity();
            theConstant();
            try (WorldData data = WorldData.open(fullPath)) {
                theSaveOfTheRates(new World(data));
            }
            theSaveOfTheCity();
        }
        out.printf("%nInterestAbroadCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       THE RUNS - helpers the sections share
       ===================================================================== */

    /** The world file and what is built on it: the nations' commodity shares of exports the moneys are given. */
    static final class World {
        final WorldData data;
        final double[] shares;

        World(WorldData data) {
            this.data = data;
            WorldMarket.Base base = WorldMarket.Base.of(data);
            shares = new double[data.nationCount()];
            for (int i = 1; i <= shares.length; i++) shares[i - 1] = base.commodityShare(i);
        }

        WorldMoneys moneys(Nations.Path path, long seed) { return new WorldMoneys(data, new Nations(data, path, seed), shares); }
    }

    /** Steps a moneys and its rates together, each to the month last (both stand at the same month). */
    static void run(WorldMoneys w, PolicyRates r, int last) {
        for (int t = r.month() + 1; t <= last; t++) {
            w.step(t, null);
            r.step(t);
        }
    }

    /** The rates of the eight as they stand, in WorldMoneys.EIGHT's order. */
    static double[] ratesOf(PolicyRates r) {
        double[] out = new double[WorldMoneys.EIGHT.size()];
        for (int k = 0; k < out.length; k++) out[k] = r.rate(k);
        return out;
    }

    static void quietly(Runnable work) {
        PrintStream was = System.out;
        System.setOut(quiet);
        try { work.run(); } finally { System.setOut(was); }
    }

    /** One month of a city, the narration sent nowhere. */
    static void play(Game city) { quietly(() -> city.simulateMonths(1)); }

    /** A trading town on the default founding, as WorldMoneysCheck builds it, on a scratch folder; quieted for the D$ or on the literals where asked (before its first month). */
    static Game town(String label, boolean quietForTheD, boolean onLiterals) {
        Game city = new Game(GameFiles.scratch(label), Founding.defaults());
        if (quietForTheD) city.quietTheWorldForTheD();
        if (onLiterals) city.playOnLiterals();
        quietly(() -> {
            city.run();
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(ForeignCheck.template(city, "House"), 500, false);
            city.buildStack(ForeignCheck.template(city, "Convenience Store"), 8, false);
            city.buildStack(ForeignCheck.template(city, "Small Grocery Store"), 3, false);
            city.buildStack(ForeignCheck.template(city, "Construction Depot"), 4, false);
            city.buildStack(ForeignCheck.template(city, "Coal Power Plant"), 1, false);
            city.buildStack(ForeignCheck.template(city, "Water Treatment Plant"), 1, false);
            city.buildStack(ForeignCheck.template(city, "Industrial Bakery"), 2, false);
        });
        return city;
    }

    /* =====================================================================
       1. THE TABLE
       ===================================================================== */

    /** The spot values of the table, from the file's own history: bank, year, month, rate. */
    static final Object[][] SPOTS = {
        {"USD", 2000, 1, .055}, {"USD", 2000, 12, .065}, {"USD", 2001, 1, .055}, {"USD", 2009, 6, .00125}, {"USD", 2023, 8, .05375}, {"USD", 2025, 12, .03625},
        {"EUR", 2017, 3, -.004}, {"JPY", 2017, 6, -.001}, {"GBP", 2009, 3, .005}, {"CHF", 2015, 2, -.0075}, {"AUD", 2008, 12, .0425},
        {"CAD", 2009, 4, .0025}, {"CNY", 2015, 10, .0435}
    };

    static void theTable() {
        out.println("--- 1. the table: eight banks, 312 months, the file's history ---");
        Tally every = new Tally();
        for (String code : WorldMoneys.EIGHT) {
            for (int t = 0; t < PolicyRates.MONTHS; t++) {
                final int month = t;
                double r = PolicyRates.historical(code, t);
                every.see(Double.isFinite(r) && r > -.02 && r < .12, () -> code + " in month " + month + " is " + r);
            }
        }
        tally("each of the eight has a rate in every one of the " + PolicyRates.MONTHS + " months, a fraction a year between -2% and 12%", every);
        Tally spots = new Tally();
        for (Object[] spot : SPOTS) {
            String code = (String) spot[0];
            int year = (Integer) spot[1], month = (Integer) spot[2];
            double wanted = (Double) spot[3], got = PolicyRates.historical(code, at(year, month));
            spots.see(Math.abs(got - wanted) <= EXACT, () -> code + " " + year + "-" + month + " is " + got + ", the file's is " + wanted);
        }
        tally("the spot values agree with the file's history (the Fed's two changes in January 2001 leave the second, the euro's negative rates, the pound at 0.5%...)", spots);
        assertTrue("historical() is NaN before January 2000, after December 2025 and for a code that is not one of the eight",
                Double.isNaN(PolicyRates.historical("USD", -1)) && Double.isNaN(PolicyRates.historical("USD", PolicyRates.MONTHS))
                        && Double.isNaN(PolicyRates.historical("XYZ", 0)) && Double.isNaN(PolicyRates.historical("usd", 0)));
        assertTrue("the table covers January 2000 to December 2025: " + PolicyRates.MONTHS + " months, the world file's (WorldMoneys.HISTORICAL_RULE_START)",
                PolicyRates.MONTHS == WorldMoneys.HISTORICAL_RULE_START && PolicyRates.MONTHS == 312);
        assertTrue("the dials a caller reads are the banks' own: a target and a neutral real rate for each of the eight, NaN for a code that is none",
                Double.isFinite(PolicyRates.inflationTargetOf("AUD")) && PolicyRates.inflationTargetOf("AUD") == .025 && PolicyRates.neutralRealOf("JPY") == -.005
                        && Double.isNaN(PolicyRates.inflationTargetOf("XYZ")) && Double.isNaN(PolicyRates.neutralRealOf("XYZ")));

        // A table is read by a parser the harness can hand any text, and each defect is refused naming its line.
        String base = validTable();
        double[][] parsed = parsed(base + "USD\t2000-03\t2.00\n");
        assertTrue("fixture: a table of eight first lines and one change reads (a rate holds until the next change)",
                parsed != null && same(parsed[0][1], .01) && same(parsed[0][2], .02) && same(parsed[0][311], .02) && same(parsed[1][311], .01));
        refused("a money that is not one of the eight", base + "XYZ\t2000-03\t2.00\n", "line 10");
        refused("a change not after the money's last", base + "USD\t2000-03\t2.00\nUSD\t2000-02\t1.00\n", "line 11");
        refused("a change in the same month as the money's last", base + "USD\t2000-03\t2.00\nUSD\t2000-03\t3.00\n", "line 11");
        refused("a month after December 2025", base + "USD\t2026-01\t2.00\n", "line 10");
        refused("a month before January 2000", base + "USD\t1999-12\t2.00\n", "line 10");
        refused("a rate that is no number", base + "USD\t2000-03\tabc\n", "line 10");
        refused("a line of two fields", base + "USD\t2000-03\n", "line 10");
        refused("a month that is not yyyy-mm", base + "USD\t2000-3\t2.00\n", "line 10");
        refused("a money whose first line is not January 2000", base.replace("CHF\t2000-01", "CHF\t2000-02"), "line 9");
        refused("a money with no line at all", base.replace("CHF\t2000-01\t1.00\n", ""), "CHF");
    }

    /** A valid table's text: a comment and January 2000 for each of the eight at 1%, so the first line of a defect added is line 10. */
    static String validTable() {
        StringBuilder s = new StringBuilder("# a comment\n");
        for (String code : WorldMoneys.EIGHT) s.append(code).append("\t2000-01\t1.00\n");
        return s.toString();
    }

    static double[][] parsed(String text) {
        try {
            return PolicyRates.parse(new java.io.StringReader(text));
        } catch (java.io.IOException | IllegalStateException e) {
            return null;
        }
    }

    /** A table with one defect is refused: IllegalStateException, and the message says where. */
    static void refused(String defect, String text, String where) {
        String message = null;
        try {
            PolicyRates.parse(new java.io.StringReader(text));
        } catch (IllegalStateException e) {
            message = e.getMessage();
        } catch (java.io.IOException e) {
            message = "IOException " + e;
        }
        assertTrue("a table with " + defect + " is refused, naming " + where + (message == null ? ": it was read" : " (" + message.replace("/policy-rates.tsv ", "") + ")"),
                message != null && message.contains(where));
    }

    /* =====================================================================
       2. HISTORICAL REPLAYS, THEN THE RULE
       ===================================================================== */

    static void theReplay(World world) {
        out.println("--- 2. Historical replays the table to December 2025; from January 2026 the rule steps in quarter points ---");
        WorldMoneys w = world.moneys(Nations.Path.HISTORICAL, SEED);
        PolicyRates r = new PolicyRates(w);
        assertTrue("the rule's first month is the moneys' (" + w.ruleStart() + ") and the table's end", r.ruleStart() == w.ruleStart() && r.ruleStart() == PolicyRates.MONTHS);
        Tally replay = new Tally(), unread = new Tally();
        for (int t = 0; t < PolicyRates.MONTHS; t++) {
            if (t > 0) {
                w.step(t, null);
                r.step(t);
            }
            for (String code : WorldMoneys.EIGHT) {
                final int month = t;
                replay.see(same(r.rate(code), PolicyRates.historical(code, t)) && same(r.shadow(code), PolicyRates.historical(code, t)),
                        () -> code + " in month " + month + " is " + r.rate(code) + ", the table's " + PolicyRates.historical(code, month));
                unread.see(Double.isNaN(r.target(code)) && Double.isNaN(r.inflation(code)), () -> code + " has a target before the rule rules, in month " + month);
            }
            if (r.ruling()) unread.see(false, () -> "ruling in month " + r.month());
        }
        tally("to December 2025 every bank's rate, and its shadow with it, is the table's, exactly, in every month (0 to 311)", replay);
        tally("...and the rule has read nothing and rules nothing: no target and no inflation, not ruling", unread);

        double[] was = ratesOf(r);
        Tally ruled = new Tally(), multiple = new Tally(), floor = new Tally(), near = new Tally(), notPast = new Tally();
        int[] moves = new int[WorldMoneys.EIGHT.size()];
        for (int t = PolicyRates.MONTHS; t < LONG; t++) {
            w.step(t, null);
            r.step(t);
            if (t == PolicyRates.MONTHS) {
                assertTrue("January 2026 is the rule's first month: ruling, and every bank has read an inflation and has a target", r.ruling()
                        && WorldMoneys.EIGHT.stream().allMatch(c -> Double.isFinite(r.target(c)) && Double.isFinite(r.inflation(c))));
            }
            for (int k = 0; k < moves.length; k++) {
                String code = WorldMoneys.EIGHT.get(k);
                final int month = t;
                double now = r.rate(k), shadow = r.shadow(code), change = now - was[k];
                double steps = change / PolicyRates.STEP;
                ruled.see(r.ruling(), () -> code + " is not ruling in month " + month);
                multiple.see(Math.abs(steps - Math.rint(steps)) * PolicyRates.STEP <= EXACT, () -> code + " moved " + change + " in month " + month);
                floor.see(now >= PolicyRates.LOWER_BOUND - EXACT, () -> code + " stands at " + now + " in month " + month);
                near.see(Math.abs(now - shadow) <= PolicyRates.STEP + EXACT, () -> code + " is " + now + " with its shadow at " + shadow + " in month " + month);
                notPast.see(change > 0 ? now <= shadow + EXACT : change < 0 ? now >= shadow - EXACT : true,
                        () -> code + " moved " + change + " to " + now + " past its shadow " + shadow + " in month " + month);
                if (Math.abs(change) > EXACT) moves[k]++;
                was[k] = now;
            }
        }
        tally("ruling from month " + PolicyRates.MONTHS + " to " + (LONG - 1) + ", every month", ruled);
        tally("...every change of every bank is a whole multiple of STEP (" + PolicyRates.STEP + ")", multiple);
        tally("...no rate is under LOWER_BOUND (" + PolicyRates.LOWER_BOUND + ")", floor);
        tally("...no rate is further than STEP from its shadow", near);
        tally("...and no rate moves past its shadow", notPast);
        StringBuilder moved = new StringBuilder();
        int total = 0;
        for (int k = 0; k < moves.length; k++) {
            moved.append(WorldMoneys.EIGHT.get(k)).append(' ').append(moves[k]).append(k + 1 < moves.length ? ", " : "");
            total += moves[k];
        }
        out.println("  months in which a bank moved its rate, 2026-2099: " + moved);
        assertTrue("fixture: the rule did move the rates (" + total + " moves over the eight in 888 months), so the steps above were tested on something", total > 0);
    }

    /* =====================================================================
       3. RANDOM RUNS THE RULE FROM FEBRUARY 2000
       ===================================================================== */

    static void theRandomRule(World world) {
        out.println("--- 3. Random runs the rule from February 2000: the target, the Taylor weight, the half-life, one seed one path ---");
        WorldMoneys w = world.moneys(Nations.Path.RANDOM, SEED);
        PolicyRates r = new PolicyRates(w);
        boolean januaryTable = true;
        for (String code : WorldMoneys.EIGHT) januaryTable &= same(r.rate(code), PolicyRates.historical(code, 0)) && same(r.shadow(code), PolicyRates.historical(code, 0));
        assertTrue("month 0 is the table's January 2000, not yet ruling, and the rule's first month is February (" + r.ruleStart() + ")",
                r.month() == 0 && !r.ruling() && januaryTable && r.ruleStart() == 1 && r.ruleStart() == w.ruleStart());
        w.step(1, null);
        r.step(1);
        assertTrue("month 1 is ruled: every bank has read an inflation and has a target", r.ruling()
                && WorldMoneys.EIGHT.stream().allMatch(c -> Double.isFinite(r.target(c)) && Double.isFinite(r.inflation(c))));
        assertTrue("the Fed stood at 5.5% in January 2000 and a quarter-point rule has it at 5.5% or one step either side in February (" + r.usRate() + ")",
                Math.abs(r.usRate() - PolicyRates.historical("USD", 0)) <= PolicyRates.STEP + EXACT);
        assertTrue("a step out of turn is refused (the month after 1 is 2) and changes nothing", refusedStep(r, 5, ratesOf(r)));

        // One seed, one path: two builds on one seed give identical rates, month by month, for 600 months; another seed differs.
        WorldMoneys w2 = world.moneys(Nations.Path.RANDOM, SEED);
        PolicyRates twin = new PolicyRates(w2);
        w2.step(1, null);
        twin.step(1);
        WorldMoneys w3 = world.moneys(Nations.Path.RANDOM, SEED + 1);
        PolicyRates other = new PolicyRates(w3);
        w3.step(1, null);
        other.step(1);
        Tally identical = new Tally();
        boolean differs = false;
        for (int t = 2; t <= 600; t++) {
            w.step(t, null);
            r.step(t);
            w2.step(t, null);
            twin.step(t);
            w3.step(t, null);
            other.step(t);
            final int month = t;
            identical.see(Arrays.equals(r.toSaveArray(), twin.toSaveArray()), () -> "the twin differs in month " + month);
            differs |= !same(r.inflation("USD"), other.inflation("USD")) || !same(r.inflation("EUR"), other.inflation("EUR"));
        }
        tally("two builds on one seed give identical rates, shadows, targets and inflations for 600 months, bit for bit", identical);
        assertTrue("...and another seed reads another inflation path (the rule reads the nations' path, the seed's)", differs);

        // The US's real rate, counted over the seeds: the rate less the inflation it read.
        StringBuilder printed = new StringBuilder();
        boolean everySeed = true;
        double widest = 0;
        for (long seed : SEEDS) {
            WorldMoneys wm = world.moneys(Nations.Path.RANDOM, seed);
            PolicyRates pr = new PolicyRates(wm);
            double sum = 0;
            int n = 0;
            for (int t = 1; t < LONG; t++) {
                wm.step(t, null);
                pr.step(t);
                sum += pr.usRate() - pr.inflation("USD");
                n++;
            }
            double mean = sum / n;
            printed.append(String.format("%.2f%% ", 100 * mean));
            widest = Math.max(widest, Math.abs(mean - PolicyRates.neutralRealOf("USD")));
            everySeed &= Math.abs(mean - PolicyRates.neutralRealOf("USD")) <= .01;
        }
        out.println("  the US's mean real rate over " + (LONG - 1) + " months, by seed: " + printed + "(neutral " + 100 * PolicyRates.neutralRealOf("USD") + "%)");
        assertTrue(String.format("the US's mean real rate is within a point of its neutral real rate (%.2f%%) on every seed; the widest is %.2f points away",
                100 * PolicyRates.neutralRealOf("USD"), 100 * widest), everySeed);

        // The rule through the class: the target of a month is targetFor() of the inflation read; the weights are the constants'.
        WorldMoneys wr = world.moneys(Nations.Path.RANDOM, SEED);
        PolicyRates pr = new PolicyRates(wr);
        Tally viaTheClass = new Tally(), halfLife = new Tally(), bounded = new Tally();
        double minPi = Double.MAX_VALUE, maxPi = -Double.MAX_VALUE;
        for (int t = 1; t < LONG; t++) {
            double[] shadowBefore = new double[WorldMoneys.EIGHT.size()];
            for (int k = 0; k < shadowBefore.length; k++) shadowBefore[k] = pr.shadow(WorldMoneys.EIGHT.get(k));
            wr.step(t, null);
            pr.step(t);
            for (int k = 0; k < shadowBefore.length; k++) {
                String code = WorldMoneys.EIGHT.get(k);
                final int month = t, bank = k;
                double pi = pr.inflation(code), target = pr.target(code);
                minPi = Math.min(minPi, pi);
                maxPi = Math.max(maxPi, pi);
                viaTheClass.see(same(target, PolicyRates.targetFor(k, pi)), () -> code + " in month " + month + " has target " + target + ", targetFor gives " + PolicyRates.targetFor(bank, pi));
                bounded.see(target >= PolicyRates.LOWER_BOUND, () -> code + " has target " + target + " in month " + month);
                double gap = target - shadowBefore[k];
                if (Math.abs(gap) > 1e-6) {
                    double closed = (pr.shadow(code) - shadowBefore[k]) / gap;
                    halfLife.see(Math.abs(Math.pow(1 - closed, PolicyRates.RULE_HALF_LIFE) - .5) <= 1e-9, () -> code + " closed " + closed + " of its gap in month " + month);
                }
            }
        }
        out.printf("  inflation read on the seed's path, the eight over %d months: from %.1f%% to %.1f%% a year%n", LONG - 1, 100 * minPi, 100 * maxPi);
        tally("every month's target is what targetFor() gives for the inflation the bank read (step() has no other rule)", viaTheClass);
        tally("...never under LOWER_BOUND", bounded);
        tally("...and the shadow closes the same share of its gap to the target every month, and that share is the half-life's: 0.5 after RULE_HALF_LIFE (" + PolicyRates.RULE_HALF_LIFE + ") months", halfLife);

        Tally taylor = new Tally(), neutral = new Tally(), deflation = new Tally();
        for (int k = 0; k < WorldMoneys.EIGHT.size(); k++) {
            String code = WorldMoneys.EIGHT.get(k);
            double target = PolicyRates.inflationTargetOf(code), neutralReal = PolicyRates.neutralRealOf(code);
            double onTarget = PolicyRates.targetFor(k, target), overByAPoint = PolicyRates.targetFor(k, target + .01);
            taylor.see(Math.abs((overByAPoint - (target + .01)) - (onTarget - target) - PolicyRates.TAYLOR_WEIGHT * .01) <= EXACT,
                    () -> code + ": the real target rose " + ((overByAPoint - (target + .01)) - (onTarget - target)) + " for a point over its inflation target");
            neutral.see(Math.abs((onTarget - target) - neutralReal) <= EXACT, () -> code + ": with inflation on its target the real target is " + (onTarget - target));
            deflation.see(PolicyRates.targetFor(k, -.05) == PolicyRates.LOWER_BOUND, () -> code + ": in a deflation the target is " + PolicyRates.targetFor(0, -.05));
        }
        tally("a point of inflation over a bank's target raises its real target by TAYLOR_WEIGHT (" + PolicyRates.TAYLOR_WEIGHT + ") of a point, for each of the eight", taylor);
        tally("...with inflation on its target the real target is its neutral real rate", neutral);
        tally("...and in a deflation of 5% the target is LOWER_BOUND, for each of the eight", deflation);
    }

    /** A step to a month that is not the next is refused, and the rates are as they were. */
    static boolean refusedStep(PolicyRates r, int month, double[] before) {
        double[] saved = r.toSaveArray();
        boolean threw = false;
        try {
            r.step(month);
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        return threw && Arrays.equals(saved, r.toSaveArray()) && Arrays.equals(before, ratesOf(r));
    }

    /* =====================================================================
       6. THE SAVE (the rates)
       ===================================================================== */

    static void theSaveOfTheRates(World world) {
        out.println("--- 6. the save: a restored state steps on as the unbroken one does; one that is not this one's is refused ---");
        for (Nations.Path path : Nations.Path.values()) {
            int saveAt = path == Nations.Path.HISTORICAL ? 300 : 60;
            WorldMoneys wa = world.moneys(path, SEED), wb = world.moneys(path, SEED);
            PolicyRates unbroken = new PolicyRates(wa), restored = new PolicyRates(wb);
            run(wa, unbroken, saveAt);
            double[] saved = unbroken.toSaveArray();
            // The restored one was never stepped: wb stands at month 0, and the rule reads only the nations' price levels, which do not depend on the month it stands in.
            restored.restore(saved);
            assertTrue(path + ": a save at month " + saveAt + " is as many numbers as its header says (3 and four columns of 8), and none of them is a number that is none",
                    saved.length == 3 + 4 * 8 && Arrays.stream(saved).allMatch(Double::isFinite) && saved[0] == PolicyRates.SAVE_FORMAT && saved[1] == 8 && saved[2] == saveAt);
            assertTrue(path + ": restored, it stands in the saved month with the saved state to the bit", restored.month() == saveAt && Arrays.equals(restored.toSaveArray(), saved));
            Tally continues = new Tally();
            for (int t = saveAt + 1; t <= saveAt + 120; t++) {
                wa.step(t, null);
                unbroken.step(t);
                restored.step(t);
                final int month = t;
                continues.see(Arrays.equals(restored.toSaveArray(), unbroken.toSaveArray()), () -> "the restored one differs from the unbroken in month " + month);
            }
            tally(path + ": the restored one steps on for 120 months (" + (path == Nations.Path.HISTORICAL ? "across the table's end into the rule" : "inside the rule")
                    + ") as the unbroken one does, bit for bit", continues);
        }
        // A fresh one has not read an inflation yet, and saves that without a NaN; the restore gives the NaN back.
        WorldMoneys fresh = world.moneys(Nations.Path.RANDOM, SEED);
        PolicyRates r = new PolicyRates(fresh);
        double[] atStart = r.toSaveArray();
        assertTrue("a fresh one saves no number that is none (its target and inflation not read yet are not NaN in the save), and its restore gives them back as none",
                Arrays.stream(atStart).allMatch(Double::isFinite) && restoredIsUnread(world, atStart));
        run(fresh, r, 24);
        double[] state = r.toSaveArray();
        PolicyRates probe = new PolicyRates(world.moneys(Nations.Path.RANDOM, SEED));
        // Refused, and nothing changed: another format, another count of banks, a length that is not this one's, a header alone, a month that is none, a number that is none.
        double[][] bad = new double[7][];
        bad[0] = state.clone(); bad[0][0] = PolicyRates.SAVE_FORMAT + 1;
        bad[1] = state.clone(); bad[1][1] = 7;
        bad[2] = Arrays.copyOf(state, state.length - 1);
        bad[3] = Arrays.copyOf(state, state.length + 1);
        bad[4] = Arrays.copyOf(state, 2);
        bad[5] = state.clone(); bad[5][2] = -3;
        bad[6] = state.clone(); bad[6][5] = Double.NaN;
        String[] why = {"another format", "another count of banks", "one number short", "one number long", "a header alone", "a month before January 2000", "a number that is none"};
        probe.restore(state);
        double[] held = probe.toSaveArray();
        for (int i = 0; i < bad.length; i++) {
            boolean threw = false;
            try {
                probe.restore(bad[i]);
            } catch (IllegalArgumentException e) {
                threw = true;
            }
            assertTrue("a save of " + why[i] + " is refused with IllegalArgumentException, and the rates are as they were", threw && Arrays.equals(probe.toSaveArray(), held));
        }
    }

    /** The unread state restores as unread: a fresh one's save, restored into another, reads NaN for a target and an inflation again. */
    static boolean restoredIsUnread(World world, double[] atStart) {
        PolicyRates other = new PolicyRates(world.moneys(Nations.Path.RANDOM, SEED));
        other.restore(atStart);
        for (String code : WorldMoneys.EIGHT) if (!Double.isNaN(other.target(code)) || !Double.isNaN(other.inflation(code))) return false;
        return Arrays.equals(other.toSaveArray(), atStart);
    }

    /* =====================================================================
       4. THE CITY READS THE US'S RATE
       ===================================================================== */

    static void theCity() {
        out.println("--- 4. the city reads the US's rate: the world's price of money, month by month ---");
        Game g = town("interest-abroad-city", false, false);
        assertTrue("fixture: a city that has not played a month has no policy rates yet, and its price of money is the constant (" + DebtManager.WORLD_BASE_RATE + ")",
                g.getPolicyRates() == null && g.getDebtManager().worldRate() == DebtManager.WORLD_BASE_RATE);
        int inStep = 0, handed = 0, differential = 0, months = 6;
        double first = Double.NaN;
        for (int m = 1; m <= months; m++) {
            play(g);
            PolicyRates p = g.getPolicyRates();
            DebtManager dm = g.getDebtManager();
            if (p != null && p.month() == WorldMoneys.monthIndex(g.getMonth())) inStep++;
            if (p != null && Double.compare(dm.worldRate(), p.usRate()) == 0) handed++;
            if (m == 1 && p != null) first = p.usRate();
            double city = dm.getPolicyRate() - g.getExpectations().getExpectedInflation();
            double us = dm.worldRate() - g.getWorldEconomy().realisedInflation();
            if (Math.abs(g.realRateDifferential() - (city - us)) <= EXACT) differential++;
        }
        PolicyRates p = g.getPolicyRates();
        DebtManager dm = g.getDebtManager();
        assertTrue("the policy rates stand in the city's month every one of " + months + " months (WorldMoneys.monthIndex(month))", inStep == months);
        assertTrue("the city's price of money is the US's policy rate every month (" + handed + " of " + months + ")", handed == months);
        assertTrue("the first month's is not the constant: the Fed stood at 5.5% (" + first + ") and not at " + DebtManager.WORLD_BASE_RATE,
                first != DebtManager.WORLD_BASE_RATE && first >= .05 && first <= .06);
        assertTrue("realRateDifferential() is the city's real rate (the dial less the expected inflation) less the US's (its policy rate less the world's realised inflation), "
                + differential + " of " + months + " months", differential == months);
        double spread = .01;
        dm.setAim(DebtManager.Aim.INFLATION);
        dm.setPegSpread(spread);
        close("the tied rate (a peg's dial) is the US's rate plus the peg spread, inside the bounds", dm.tiedRate(0), dm.worldRate() + spread, EXACT);
        close("the currency rate with no gap is the US's rate", dm.currencyRate(0), dm.worldRate(), EXACT);
        dm.setPegSpread(-1);
        close("a spread below what keeps the tied rate on the dial is clamped to the dial's floor over the US's rate (MIN_POLICY_RATE - worldRate())",
                dm.getPegSpread(), DebtManager.MIN_POLICY_RATE - dm.worldRate(), EXACT);
        close("...and the tied rate is the dial's floor", dm.tiedRate(0), DebtManager.MIN_POLICY_RATE, EXACT);
        dm.setPegSpread(9);
        close("a spread above is clamped to the dial's ceiling over the US's rate (MAX_POLICY_RATE - worldRate())", dm.getPegSpread(), DebtManager.MAX_POLICY_RATE - dm.worldRate(), EXACT);
        close("...and the tied rate is the dial's ceiling", dm.tiedRate(0), DebtManager.MAX_POLICY_RATE, EXACT);
        close("the foreign rate is the US's rate plus the country premium", dm.foreignRate(), dm.worldRate() + dm.countryPremium(), EXACT);
        close("a quote for no more foreign paper is the foreign rate", dm.quoteForeignRate(0), dm.foreignRate(), EXACT);
        close("what the city's rate stands over the world's is its short rate less the US's", dm.overTheWorld(), dm.getRate() - dm.worldRate(), EXACT);
        assertTrue("the equity's required return stays on the constant (Bank.requiredReturn(): a share's required return is a long-run figure), while the world's price of money is "
                + String.format("%.3f", dm.worldRate()), Math.abs(Bank.requiredReturn() - (DebtManager.WORLD_BASE_RATE + Equity.FOREIGN_PREMIUM) / Equity.PAYOUT) <= 1e-15
                && dm.worldRate() != DebtManager.WORLD_BASE_RATE);
        assertTrue("the history keeps the eight policy rates a point a month (policyUSD ... policyCHF), the last the rates' own to the history's rounding", historyAgrees(g, p));
        assertTrue("...and every one of them has a rule in the year book", YearBook.unruled(g.getHistorySave()).isEmpty());
    }

    static boolean historyAgrees(Game g, PolicyRates p) {
        HistorySave history = g.getHistorySave();
        for (String code : WorldMoneys.EIGHT) {
            double[] series = history.aligned(HistorySave.policyRateKey(code));
            if (series.length != history.months() || Math.abs(series[series.length - 1] - p.rate(code)) > 5e-7) return false;
        }
        return true;
    }

    /* =====================================================================
       5. NO WORLD, THE CONSTANT
       ===================================================================== */

    static void theConstant() {
        out.println("--- 5. no world, the constant: a city without the world file or quieted for the D$ pays the world 2% as it did ---");
        DebtManager bare = new DebtManager();
        assertTrue("a bare DebtManager's price of money is WORLD_BASE_RATE (" + DebtManager.WORLD_BASE_RATE + ")", bare.worldRate() == DebtManager.WORLD_BASE_RATE);
        bare.setWorldRate(.055);
        assertTrue("setWorldRate() hands it the US's rate", bare.worldRate() == .055);
        bare.setWorldRate(Double.NaN);
        assertTrue("...a rate that is no number leaves it at the constant", bare.worldRate() == DebtManager.WORLD_BASE_RATE);
        bare.setWorldRate(.055);
        bare.setWorldRate(Double.POSITIVE_INFINITY);
        assertTrue("...and an infinite one too", bare.worldRate() == DebtManager.WORLD_BASE_RATE);

        Game literals = town("interest-abroad-literals", false, true);
        boolean constant = true;
        for (int m = 1; m <= 3; m++) {
            play(literals);
            constant &= literals.getPolicyRates() == null && literals.getDebtManager().worldRate() == DebtManager.WORLD_BASE_RATE;
        }
        assertTrue("a city on the literals (no world file) has no policy rates and pays the constant, three months on", constant);

        Game quieted = town("interest-abroad-quiet", true, false);
        boolean held = true, built = true;
        for (int m = 1; m <= 3; m++) {
            play(quieted);
            PolicyRates p = quieted.getPolicyRates();
            built &= p != null && p.usRate() != DebtManager.WORLD_BASE_RATE;
            held &= quieted.getDebtManager().worldRate() == DebtManager.WORLD_BASE_RATE;
        }
        assertTrue("fixture: a city quieted for the D$ still builds and steps the policy rates, and the US's rate is not the constant", built);
        assertTrue("...and its money markets feel no world: its price of money is the constant, three months on", held);
    }

    /* =====================================================================
       6. THE SAVE (the city)
       ===================================================================== */

    static void theSaveOfTheCity() {
        out.println("--- 6. the save: a city saved and loaded has the same rates and the same price of money ---");
        Game g = town("interest-abroad-save", false, false);
        for (int m = 1; m <= 8; m++) play(g);
        // A peg spread beyond what the constant would allow, inside what the US's rate does: the load clamps it twice (before the market, then after the rate is handed) and has to keep it.
        DebtManager gdm = g.getDebtManager();
        final double spread = -(gdm.worldRate() - .005);
        gdm.setPegSpread(spread);
        assertTrue("fixture: the spread (" + spread + ") stands as set, though it is under the constant's bound (" + (DebtManager.MIN_POLICY_RATE - DebtManager.WORLD_BASE_RATE) + ")",
                gdm.getPegSpread() == spread && spread < DebtManager.MIN_POLICY_RATE - DebtManager.WORLD_BASE_RATE);
        assertTrue("saved after 8 months, to its scratch folder", g.saveGame(2, "policy").ok);
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(2));
        PolicyRates a = g.getPolicyRates(), b = back.getPolicyRates();
        assertTrue("the loaded city has the policy rates, in the month it was saved in, and the very same state to the bit",
                back.getLoadFailure() == null && a != null && b != null && b.month() == a.month() && Arrays.equals(a.toSaveArray(), b.toSaveArray()));
        assertTrue("...the same eight rates", b != null && Arrays.equals(ratesOf(a), ratesOf(b)));
        assertTrue("...and the same price of money (" + g.getDebtManager().worldRate() + "), though it is not saved: the load hands it",
                Double.compare(g.getDebtManager().worldRate(), back.getDebtManager().worldRate()) == 0 && back.getDebtManager().worldRate() == b.usRate());
        assertTrue("...and the peg spread the player set under the US's rate, which the constant's bound would have clamped, is as it was", back.getDebtManager().getPegSpread() == spread);
        for (int m = 1; m <= 4; m++) {
            play(g);
            play(back);
        }
        assertTrue("both play four months more and stand in the same state, the rates and the price of money to the bit",
                Arrays.equals(g.getPolicyRates().toSaveArray(), back.getPolicyRates().toSaveArray())
                        && Double.compare(g.getDebtManager().worldRate(), back.getDebtManager().worldRate()) == 0);
        assertTrue("...and the history keeps the eight policy rates the same in both",
                Arrays.equals(g.getHistorySave().aligned(HistorySave.policyRateKey("USD")), back.getHistorySave().aligned(HistorySave.policyRateKey("USD")))
                        && Arrays.equals(g.getHistorySave().aligned(HistorySave.policyRateKey("CHF")), back.getHistorySave().aligned(HistorySave.policyRateKey("CHF"))));
    }
}
