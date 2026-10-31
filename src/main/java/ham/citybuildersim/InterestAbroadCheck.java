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
 *   7. THE VAULT EARNS EACH MONEY'S RATE TO THE CENT (no world file needed): a ForeignAccounts with balances in three moneys earns balance x rate / 12
 *      in each one's own units on a positive, a zero and a negative rate, and keeps it; the month's US$ is their sum at the month's cross rates and
 *      the cross move is the same before and after; a negative rate never takes a balance below zero; a money with no rate this month, or a bank
 *      whose rate is no number, earns nothing; a month it is not called reads 0 (the lifetime kept); the month's, the lifetime, the driver and the
 *      yield ride the array at slots 142-146 and an older array reads nothing earned.
 *   8. A RISK-OFF UNWINDS THE HOT MONEY AND THE CARRY BY THEIR STOCKS (no world file needed): settled to a stock, a month of a risk-off at size 1
 *      sends home RISK_OFF_HOT_UNWIND of the stock the ordinary month left, inside the month's departures; twice the stock unwinds twice as much,
 *      half the level half; a level past 1 / the dial takes the whole stock and no more; level 0 (or no number, or below zero) is the untouched twin
 *      to the bit; the carry the same with RISK_OFF_CARRY_UNWIND, inside the month's repaid and the change returned; the months are counted once
 *      though both books are in one; the save, reset and a reform carry the lot.
 *   9. IN A CITY: the vault's interest each month is the sum over the eight of the balance before x that bank's rate / 12 at the month's cross
 *      rates, to the cent; a forced risk-off in a city that holds hot money sends some home the month it starts, the audit sees it cross the edge
 *      and closes, the log says so; a city quieted for the D$ earns nothing and unwinds nothing though its own moneys are in a risk-off; a save
 *      carries the lot and the loaded city does not earn the saved month twice.
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

    /** Two figures that should be one to the bit. */
    static void exact(String label, double got, double wanted) {
        assertTrue(String.format("%s: %.17g against %.17g", label, got, wanted), same(got, wanted));
    }

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
            else out.println("world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": sections 2 to 6 and 9 did not run");
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
        // Sections 7 and 8 need no world file: a ForeignAccounts and a CapitalFlows, told what a city would hand them.
        theVaultEarns();
        theUnwind();
        if (fullPath != null) inACity();
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

    /* =====================================================================
       7. THE VAULT EARNS (ForeignAccounts, no world file needed)
       ===================================================================== */

    /** The cross rates the section hands: US$ per unit of the US$, euro, yen, pound, yuan, Canadian and Australian dollar and franc. */
    static final double[] CROSS = {1, 1.10, 0.0070, 1.27, 0.14, 0.73, 0.65, 1.12};

    /** A vault holding US$50, 100 euros and 10,000 yen at CROSS (bought at a rate of 2), the month booked: the fixture the earning is asked of. */
    static ForeignAccounts threeMoneys() {
        ForeignAccounts fx = new ForeignAccounts();
        fx.pinRate(2.0);
        fx.revalueVault();
        fx.setMoneyRates(CROSS);
        fx.buyReserves(50 * 2.0, "USD");
        fx.buyReserves(100 * CROSS[1] * 2.0, "EUR");
        fx.buyReserves(10_000 * CROSS[2] * 2.0, "JPY");
        fx.revalueVault();
        return fx;
    }

    /** The eight rates a section hands: the dollar's 4.8%, the euro's nothing, the yen's -0.1%, and the rest at 1%. */
    static double[] mixedRates() {
        double[] r = new double[WorldMoneys.EIGHT.size()];
        Arrays.fill(r, .01);
        r[0] = .048;
        r[1] = 0;
        r[2] = -.001;
        return r;
    }

    static void theVaultEarns() {
        out.println("--- 7. the vault earns each money's rate to the cent: balance x rate / 12 in its own units, kept in the vault ---");
        ForeignAccounts fx = threeMoneys();
        close("fixture: US$50, 100 euros and 10,000 yen are worth US$" + (50 + 110 + 70) + " at the month's cross rates", fx.getReservesUsd(), 50 + 110 + 70, 1e-9);
        assertTrue("fixture: the three balances are in their own units and nothing has been earned yet", Math.abs(fx.getHolding(0) - 50) <= 1e-9 && Math.abs(fx.getHolding(1) - 100) <= 1e-9
                && Math.abs(fx.getHolding(2) - 10_000) <= 1e-6 && fx.getVaultInterestUsd() == 0 && fx.getVaultInterestUsdLifetime() == 0 && fx.getVaultInterestDriver() == -1
                && fx.getVaultYield() == 0);

        // A new month, the euro moved: the cross move is struck first, and the interest changes nothing in it.
        double[] moved = CROSS.clone();
        moved[1] = 1.00;
        fx.setMoneyRates(moved);
        double crossBefore = fx.getLastVaultCrossUsd();
        close("fixture: the euro fell to 1.00, a cross move of -10 under 100 euros", crossBefore, -10, 1e-12);
        double usdBefore = fx.getHolding(0), eurBefore = fx.getHolding(1), jpyBefore = fx.getHolding(2);
        double[] rates = mixedRates();
        fx.earnVaultInterest(rates);
        close("the dollars grow by balance x rate / 12 (4.8%): US$50 earns 0.2", fx.getHolding(0), usdBefore + usdBefore * .048 / 12, 1e-12);
        exact("a zero rate adds nothing: the euros are exactly what they were", fx.getHolding(1), eurBefore);
        close("a negative rate takes from the balance (-0.1% on 10,000 yen is -0.8333 yen)", fx.getHolding(2), jpyBefore + jpyBefore * -.001 / 12, 1e-9);
        double wanted = usdBefore * .048 / 12 * moved[0] + eurBefore * 0 * moved[1] + jpyBefore * -.001 / 12 * moved[2];
        close("the month's US$ is the sum of the three at the month's cross rates", fx.getVaultInterestUsd(), wanted, 1e-12);
        close("...and so is the lifetime, after one month", fx.getVaultInterestUsdLifetime(), wanted, 1e-12);
        double base = usdBefore + eurBefore * moved[1] + jpyBefore * moved[2];
        close("...the vault is worth that much more in US$", fx.getReservesUsd(), base + wanted, 1e-9);
        exact("the cross move is the same before and after the interest", fx.getLastVaultCrossUsd(), crossBefore);
        assertTrue("the money that earned most (by US$, signed) is the dollar, at +0.2", fx.getVaultInterestDriver() == 0 && Math.abs(fx.getVaultInterestDriverUsd() - usdBefore * .048 / 12) <= 1e-12);
        close("the vault's yield is the interest times twelve over the US$ it was earned on (US$" + String.format("%.2f", base) + ")", fx.getVaultYield(), 12 * wanted / base, 1e-12);
        close("the interest at home is the US$ at this month's rate (2)", fx.getVaultInterest(), wanted * fx.getRate(), 1e-12);

        // The booked revaluation reads the grown vault as it would one that bought: the dollars at the rate's move plus the cross move at the last rate.
        fx.pinRate(2.5);
        fx.revalueVault();
        close("revalueVault() books V1 (r1 - r0) + cross x r0, with the interest in V1 as a purchase would be", fx.getLastVaultRevaluation(),
                fx.getReservesUsd() * (2.5 - 2.0) + crossBefore * 2.0, 1e-9);

        // A negative rate never takes a balance below zero.
        ForeignAccounts deep = threeMoneys();
        deep.setMoneyRates(CROSS);
        double[] crash = new double[WorldMoneys.EIGHT.size()];
        crash[2] = -50;
        double yenBefore = deep.getHolding(2);
        deep.earnVaultInterest(crash);
        exact("a rate of -5000% takes the yen to zero and no further", deep.getHolding(2), 0);
        close("...the interest is minus what they were worth (" + String.format("%,.0f", yenBefore) + " yen)", deep.getVaultInterestUsd(), -yenBefore * CROSS[2], 1e-12);
        assertTrue("...and the dollars and euros, on a rate of nothing, are untouched", deep.getHolding(0) == threeMoneys().getHolding(0) && deep.getHolding(1) == threeMoneys().getHolding(1));

        // A money with no rate this month earns nothing, whatever its bank pays; one whose policy rate is no number the same.
        ForeignAccounts blind = threeMoneys();
        double[] noYen = CROSS.clone();
        noYen[2] = Double.NaN;
        blind.setMoneyRates(noYen);
        assertTrue("fixture: the yen has no rate this month, the euro has", !blind.hasRate(2) && blind.hasRate(1));
        double[] pays = new double[WorldMoneys.EIGHT.size()];
        Arrays.fill(pays, .06);
        double yen = blind.getHolding(2), euros = blind.getHolding(1);
        blind.earnVaultInterest(pays);
        exact("the yen, with no rate to value them at, earn nothing at 6%", blind.getHolding(2), yen);
        close("...the euros earn their half a percent a month", blind.getHolding(1), euros * (1 + .06 / 12), 1e-12);
        ForeignAccounts nan = threeMoneys();
        nan.setMoneyRates(CROSS);
        double[] gap = new double[WorldMoneys.EIGHT.size()];
        Arrays.fill(gap, .06);
        gap[1] = Double.NaN;
        double eurs = nan.getHolding(1);
        nan.earnVaultInterest(gap);
        exact("a bank whose rate is no number pays nothing: the euros are as they were", nan.getHolding(1), eurs);
        ForeignAccounts none = threeMoneys();
        none.setMoneyRates(CROSS);
        none.earnVaultInterest(null);
        assertTrue("earnVaultInterest(null) is a month with nothing earned", none.getVaultInterestUsd() == 0 && none.getHolding(0) == threeMoneys().getHolding(0) && none.getVaultInterestDriver() == -1);

        // A month it is not called reads 0, the lifetime kept: the next hand of the rates starts the month from nothing.
        double life = fx.getVaultInterestUsdLifetime();
        fx.setMoneyRates(moved);
        assertTrue("a month the world did not hand it reads 0 (the next month's rates start it from nothing), with no driver and no yield",
                fx.getVaultInterestUsd() == 0 && fx.getVaultInterestDriver() == -1 && fx.getVaultYield() == 0 && fx.getVaultInterestDriverUsd() == 0);
        exact("...and the lifetime is kept", fx.getVaultInterestUsdLifetime(), life);
        fx.earnVaultInterest(rates);
        assertTrue("the month after earns again on the grown balances, and the lifetime is the sum of the two",
                fx.getVaultInterestUsd() > 0 && Math.abs(fx.getVaultInterestUsdLifetime() - (life + fx.getVaultInterestUsd())) <= 1e-12);

        // The save: the month's and the lifetime ride the array at slots 142 to 146; a save without them reads nothing earned.
        double[] saved = fx.toSaveArray();
        assertTrue("the array runs to slot 146 (" + saved.length + " slots)", saved.length == 147);
        assertTrue("slots 142-146 are the month's interest, the lifetime, the driver, its US$ and the base the yield is struck on",
                same(saved[142], fx.getVaultInterestUsd()) && same(saved[143], fx.getVaultInterestUsdLifetime()) && saved[144] == fx.getVaultInterestDriver()
                        && same(saved[145], fx.getVaultInterestDriverUsd()) && saved[146] > 0);
        ForeignAccounts back = new ForeignAccounts();
        back.restore(saved);
        assertTrue("restored, every slot reads back to the bit", Arrays.equals(back.toSaveArray(), saved));
        assertTrue("...the month's interest, the lifetime, the driver and the yield as they were", same(back.getVaultInterestUsd(), fx.getVaultInterestUsd())
                && same(back.getVaultInterestUsdLifetime(), fx.getVaultInterestUsdLifetime()) && back.getVaultInterestDriver() == fx.getVaultInterestDriver()
                && same(back.getVaultInterestDriverUsd(), fx.getVaultInterestDriverUsd()) && same(back.getVaultYield(), fx.getVaultYield()));
        ForeignAccounts older = new ForeignAccounts();
        older.restore(Arrays.copyOf(saved, 142));
        assertTrue("a save from before it (142 slots) reads nothing earned, and the balances it has", older.getVaultInterestUsd() == 0 && older.getVaultInterestUsdLifetime() == 0
                && older.getVaultInterestDriver() == -1 && older.getVaultYield() == 0 && same(older.getHolding(1), fx.getHolding(1)));
        double[] bad = saved.clone();
        bad[142] = Double.NaN;
        bad[144] = 99;
        ForeignAccounts garbled = new ForeignAccounts();
        garbled.restore(bad);
        assertTrue("a slot that is no number reads 0, and a driver that is none of the eight reads -1", garbled.getVaultInterestUsd() == 0 && garbled.getVaultInterestDriver() == -1
                && garbled.getVaultInterestDriverUsd() == 0);
        back.reset();
        assertTrue("reset() clears the month's and the lifetime", back.getVaultInterestUsd() == 0 && back.getVaultInterestUsdLifetime() == 0 && back.getVaultInterestDriver() == -1);

        // A redenomination leaves the US$ figures alone and moves the one at home with the rate.
        ForeignAccounts reform = new ForeignAccounts();
        reform.restore(saved);
        double usdMonth = reform.getVaultInterestUsd(), atHome = reform.getVaultInterest();
        reform.redenominate(.01);
        exact("a redenomination leaves the month's US$ alone", reform.getVaultInterestUsd(), usdMonth);
        exact("...and the lifetime", reform.getVaultInterestUsdLifetime(), fx.getVaultInterestUsdLifetime());
        close("...while the interest at home moves with the rate, a hundredth", reform.getVaultInterest(), atHome * .01, 1e-12);
    }

    /* =====================================================================
       8. A RISK-OFF UNWINDS THE HOT MONEY AND THE CARRY BY THEIR STOCKS (CapitalFlows)
       ===================================================================== */

    /** One month of hot money at a city paying 9% against a 2% world with a point of risk, GDP as given, a risk-off at this level. */
    static void hotMonth(CapitalFlows f, int m, double gdp, double level) {
        f.setRiskOff(level);
        f.setMonth(m);
        f.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01, gdp, 1e9, 0, false, false, m);
    }

    /** Hot money settled to its target (a hundred and twenty months of calm). */
    static CapitalFlows hotSettled(double gdp) {
        CapitalFlows f = new CapitalFlows();
        for (int m = 1; m <= 120; m++) hotMonth(f, m, gdp, 0);
        return f;
    }

    /** A carry book settled to its target (a city paying 0.5% against a 2% world, a billion of headroom). */
    static CapitalFlows carrySettled(double headroom) {
        CapitalFlows f = new CapitalFlows();
        for (int m = 1; m <= 240; m++) {
            f.setRiskOff(0);
            f.carryTakeMonth(.005, DebtManager.WORLD_BASE_RATE, 0, headroom);
        }
        return f;
    }

    /** What a risk-off of this level sends home from a settled hot money at 10,000 GDP. */
    static double riskOffAt(double level) {
        CapitalFlows f = hotSettled(10_000);
        hotMonth(f, 121, 10_000, level);
        return f.getRiskOffDeparted();
    }

    static void theUnwind() {
        out.println("--- 8. a risk-off unwinds the hot money and the carry by their stocks: the dial's share of what is there, scaled by the level ---");
        // THE HOT MONEY.
        CapitalFlows twin = hotSettled(10_000), hit = hotSettled(10_000);
        assertTrue("fixture: the hot money has settled on a real stock, and the twin is the same to the bit", hit.getStock() > 1000 && Arrays.equals(twin.toSaveArray(), hit.toSaveArray()));
        double stockBefore = hit.getStock();
        double tol = 1e-9 * stockBefore;
        hotMonth(twin, 121, 10_000, 0);
        hotMonth(hit, 121, 10_000, 1);
        double share = Math.min(1, CapitalFlows.RISK_OFF_HOT_UNWIND);
        close("a risk-off of size 1 sends home the dial's share (" + CapitalFlows.RISK_OFF_HOT_UNWIND + ") of the stock the ordinary month left", hit.getRiskOffDeparted(), twin.getStock() * share, tol);
        close("...and the stock is what it left, less that", hit.getStock(), twin.getStock() - hit.getRiskOffDeparted(), tol);
        close("...inside the month's departures, on top of the twin's", hit.getDeparted(), twin.getDeparted() + hit.getRiskOffDeparted(), tol);
        close("...and the lifetime's, the arrivals untouched", hit.getLifetimeDeparted(), twin.getLifetimeDeparted() + hit.getRiskOffDeparted(), tol);
        close("...the arrivals are the twin's", hit.getArrived(), twin.getArrived(), tol);
        assertTrue("the twin, at level 0, sent nothing home", twin.getRiskOffDeparted() == 0 && twin.getRiskOffDepartedLifetime() == 0 && twin.getRiskOffMonths() == 0);
        assertTrue("...and the month is counted, once, with the lifetime the month's", hit.getRiskOffMonths() == 1 && hit.getRiskOffDepartedLifetime() == hit.getRiskOffDeparted());

        // Twice the stock unwinds twice as much; half the level, half.
        CapitalFlows big = hotSettled(20_000);
        hotMonth(big, 121, 20_000, 1);
        close("twice the stock (twice the GDP) unwinds twice as much", big.getRiskOffDeparted(), 2 * hit.getRiskOffDeparted(), tol);
        CapitalFlows half = hotSettled(10_000);
        hotMonth(half, 121, 10_000, .5);
        close("half the level (a risk-off that has faded by half) unwinds half as much of the stock", half.getRiskOffDeparted(), hit.getRiskOffDeparted() / 2, tol);
        assertTrue("...and a larger one more: the level 1.5 takes more than the level 1", riskOffAt(1.5) > riskOffAt(1));

        // A level past 1 / the dial caps the share at the whole stock.
        double wholeAt = 1 / CapitalFlows.RISK_OFF_HOT_UNWIND;
        CapitalFlows all = hotSettled(10_000), past = hotSettled(10_000);
        hotMonth(all, 121, 10_000, wholeAt);
        hotMonth(past, 121, 10_000, wholeAt * 3);
        exact("at a level of 1 / the dial the whole stock runs home", all.getStock(), 0);
        assertTrue("...and a level three times that takes no more than the whole of it (the share is capped at 1): the same, and the stock is zero",
                past.getStock() == 0 && Math.abs(past.getRiskOffDeparted() - all.getRiskOffDeparted()) <= tol);

        // Level 0, a level that is no number and a negative one are the untouched twin, to the bit.
        CapitalFlows plain = hotSettled(10_000), junk = hotSettled(10_000);
        for (int m = 121; m <= 126; m++) {
            hotMonth(plain, m, 10_000, 0);
            hotMonth(junk, m, 10_000, m % 3 == 0 ? Double.NaN : m % 3 == 1 ? -1 : Double.NEGATIVE_INFINITY);
        }
        assertTrue("a level of 0, of NaN or below zero changes nothing: every slot of the array is the untouched twin's, to the bit", Arrays.equals(plain.toSaveArray(), junk.toSaveArray()));

        // The count of months, once a month though the carry and the hot money are both in it.
        CapitalFlows counted = hotSettled(10_000);
        double lifetime = 0;
        for (int m = 121; m <= 126; m++) {
            counted.setRiskOff(m <= 123 ? 1 : 0);
            counted.carryTakeMonth(.005, DebtManager.WORLD_BASE_RATE, 0, 1e9);
            counted.setMonth(m);
            counted.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01, 10_000, 1e9, 0, false, false, m);
            lifetime += counted.getRiskOffDeparted();
        }
        CapitalFlows tail = hotSettled(10_000);
        hotMonth(tail, 121, 10_000, CapitalFlows.RISK_OFF_FELT / 5);
        CapitalFlows calmTwin = hotSettled(10_000);
        hotMonth(calmTwin, 121, 10_000, 0);
        assertTrue("the tail of an old risk-off (a fifth of RISK_OFF_FELT) moves nothing and counts no month: the calm twin, to the bit",
                tail.getRiskOffDeparted() == 0 && tail.getRiskOffMonths() == 0 && Arrays.equals(tail.toSaveArray(), calmTwin.toSaveArray()));
        assertTrue("three months of risk-off, each unwinding hot money and the carry, are three months, not six (" + counted.getRiskOffMonths() + ")", counted.getRiskOffMonths() == 3);
        close("...the lifetime of what ran home is the sum of the months", counted.getRiskOffDepartedLifetime(), lifetime, tol);

        // THE CARRY.
        CapitalFlows cTwin = carrySettled(1e9), cHit = carrySettled(1e9);
        double book = cHit.getCarryStock();
        double ctol = 1e-9 * book;
        assertTrue("fixture: the carry has settled on a book, and the twin is the same to the bit", book > 1000 && Arrays.equals(cTwin.toSaveArray(), cHit.toSaveArray()));
        cTwin.setRiskOff(0);
        double twinChange = cTwin.carryTakeMonth(.005, DebtManager.WORLD_BASE_RATE, 0, 1e9);
        cHit.setRiskOff(1);
        double hitChange = cHit.carryTakeMonth(.005, DebtManager.WORLD_BASE_RATE, 0, 1e9);
        double cShare = Math.min(1, CapitalFlows.RISK_OFF_CARRY_UNWIND);
        close("a risk-off of size 1 repays the dial's share (" + CapitalFlows.RISK_OFF_CARRY_UNWIND + ") of the book the ordinary month left", cHit.getCarryRiskRepaid(), cTwin.getCarryStock() * cShare, ctol);
        close("...the book is what it left, less that", cHit.getCarryStock(), cTwin.getCarryStock() - cHit.getCarryRiskRepaid(), ctol);
        close("...inside the month's repaid, on top of the twin's", cHit.getCarryRepaid(), cTwin.getCarryRepaid() + cHit.getCarryRiskRepaid(), ctol);
        close("...and the change the month returns says so", hitChange, twinChange - cHit.getCarryRiskRepaid(), ctol);
        assertTrue("...the twin repaid none of it on the risk-off's account, and the lifetime is the month's", cTwin.getCarryRiskRepaid() == 0 && cHit.getCarryRiskRepaidLifetime() == cHit.getCarryRiskRepaid()
                && cHit.getRiskOffMonths() == 1);
        CapitalFlows cAll = carrySettled(1e9);
        cAll.setRiskOff(3 / CapitalFlows.RISK_OFF_CARRY_UNWIND);
        cAll.carryTakeMonth(.005, DebtManager.WORLD_BASE_RATE, 0, 1e9);
        exact("a level past 1 / the dial repays the whole book, no more", cAll.getCarryStock(), 0);

        // The save: the month's, the lifetimes and the count ride the array; an older array reads nothing; the level is not saved; a reform scales them.
        double[] saved = hit.toSaveArray();
        CapitalFlows read = new CapitalFlows();
        read.restore(saved);
        assertTrue("saved and restored, the hot money's month, its lifetime and the count read back to the bit", same(read.getRiskOffDeparted(), hit.getRiskOffDeparted())
                && same(read.getRiskOffDepartedLifetime(), hit.getRiskOffDepartedLifetime()) && read.getRiskOffMonths() == hit.getRiskOffMonths() && Arrays.equals(read.toSaveArray(), saved));
        double[] carrySaved = cHit.toSaveArray();
        CapitalFlows carryRead = new CapitalFlows();
        carryRead.restore(carrySaved);
        assertTrue("...and the carry's", same(carryRead.getCarryRiskRepaid(), cHit.getCarryRiskRepaid()) && same(carryRead.getCarryRiskRepaidLifetime(), cHit.getCarryRiskRepaidLifetime()));
        CapitalFlows older = new CapitalFlows();
        older.restore(Arrays.copyOf(saved, 19));
        assertTrue("an array from before it (19 slots) reads nothing unwound, and the stock it has", older.getRiskOffDeparted() == 0 && older.getRiskOffDepartedLifetime() == 0
                && older.getCarryRiskRepaid() == 0 && older.getRiskOffMonths() == 0 && same(older.getStock(), hit.getStock()));
        CapitalFlows cleared = new CapitalFlows();
        cleared.restore(saved);
        cleared.reset();
        assertTrue("reset() clears them", cleared.getRiskOffDeparted() == 0 && cleared.getRiskOffDepartedLifetime() == 0 && cleared.getCarryRiskRepaid() == 0
                && cleared.getCarryRiskRepaidLifetime() == 0 && cleared.getRiskOffMonths() == 0);
        CapitalFlows reform = new CapitalFlows();
        reform.restore(saved);
        reform.redenominate(.01);
        close("a reform scales what ran home (a hundredth) and not the months", reform.getRiskOffDeparted(), hit.getRiskOffDeparted() * .01, 1e-12);
        assertTrue("...the lifetime too, the count left alone", Math.abs(reform.getRiskOffDepartedLifetime() - hit.getRiskOffDepartedLifetime() * .01) <= 1e-12
                && reform.getRiskOffMonths() == hit.getRiskOffMonths());
    }

    /* =====================================================================
       9. IN A CITY: THE VAULT EARNS, MONEY RUNS HOME, THE AUDIT CLOSES
       ===================================================================== */

    /** The dial the hot-money city is put at: well over the US's rate in any year, so the spread is there. */
    static final double HOT_DIAL = .15;

    /** A town with banks to take hot money into, as CapitalFlowCheck's city has them. */
    static Game hotTown(String label, boolean quietForTheD) {
        Game city = town(label, quietForTheD, false);
        quietly(() -> {
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.buildStack(ForeignCheck.template(city, "Commercial Bank"), 3, false);
            city.buildStack(ForeignCheck.template(city, "Paved Road"), 30, false);
        });
        return city;
    }

    /** One month of a city with what it printed captured. */
    static String playCaptured(Game city) {
        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        PrintStream was = System.out;
        System.setOut(new PrintStream(bytes, true, java.nio.charset.StandardCharsets.UTF_8));
        try {
            city.simulateMonths(1);
        } finally {
            System.setOut(was);
        }
        return bytes.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

    static void inACity() {
        out.println("--- 9. in a city: the vault earns each bank's rate every month, a risk-off sends hot money home, and the month's money audit closes ---");
        Game g = hotTown("interest-abroad-earn", false);
        ForeignAccounts fx = g.getForeignAccounts();
        DebtManager dm = g.getDebtManager();
        // The condition: hot money comes for a city paying well over the world, with the dial in the player's hand.
        dm.setAutopilot(false);
        dm.setPolicyRate(HOT_DIAL);
        for (int m = 1; m <= 2; m++) play(g);
        fx.setReserveMix(ForeignAccounts.Mix.WORLD_MIX);
        double spend = g.getCash() / 10;
        g.buyForeignCurrency(spend);
        int held = 0;
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) if (fx.getHolding(k) > 0) held++;
        assertTrue("fixture: a purchase by the world's mix puts a balance in each of the eight (" + held + ")", held == ForeignAccounts.MONEYS);

        int months = 24, earned = 0, positive = 0;
        double worst = 0, lifetime = fx.getVaultInterestUsdLifetime(), yield = 0;
        Tally each = new Tally();
        for (int m = 1; m <= months; m++) {
            double[] before = new double[ForeignAccounts.MONEYS];
            for (int k = 0; k < before.length; k++) before[k] = fx.getHolding(k);
            play(g);
            PolicyRates p = g.getPolicyRates();
            double expected = 0, base = 0;
            for (int k = 0; k < before.length; k++) {
                expected += before[k] * p.rate(k) / 12 * fx.getMoneyUsd(k);
                base += before[k] * fx.getMoneyUsd(k);
            }
            final double want = expected;
            final int month = m;
            each.see(Math.abs(fx.getVaultInterestUsd() - want) <= 1e-9 * Math.max(1, Math.abs(want)), () -> "month " + month + ": the vault earned " + fx.getVaultInterestUsd() + ", the eight rates say " + want);
            if (fx.getVaultInterestUsd() != 0) earned++;
            if (fx.getVaultInterestUsd() > 0) positive++;
            lifetime += fx.getVaultInterestUsd();
            yield = base > 0 ? 12 * expected / base : 0;
            worst = Math.max(worst, Math.abs(g.getLastMoneyAudit().relative()));
        }
        tally("the vault's interest each month is the sum over the eight of the balance before x that bank's rate / 12 at the month's cross rates, " + months + " months", each);
        assertTrue("...it earned in every one (" + earned + " of " + months + ", " + positive + " of them a gain) and the lifetime is their sum",
                earned == months && Math.abs(fx.getVaultInterestUsdLifetime() - lifetime) <= 1e-9 * Math.abs(lifetime));
        close("...the yield is the interest times twelve over the US$ it was earned on", fx.getVaultYield(), yield, 1e-9);
        assertTrue("...and the audit closed every month of it (worst residual " + String.format("%.2e", worst) + ")", worst < 1e-9);

        CapitalFlows hot = g.getCapitalFlows();
        double stockNow = hot.getStock();
        out.printf("   after %d months at a %.0f%% dial: hot money D$%,.0fk here (target D$%,.0fk, a %.2f-point spread), the carry D$%,.0fk, the world's risk-off %.2f%n", months + 2,
                HOT_DIAL * 100, stockNow, hot.getTarget(), hot.getSpread() * 100, hot.getCarryStock(), g.getWorldMoneys().riskOff());
        assertTrue("fixture: the city holds hot money to unwind (D$" + String.format("%,.0fk", stockNow) + ") and the world is calm", stockNow > 1000 && g.getWorldMoneys().riskOff() < .5);

        // A risk-off of size one starts at the next step.
        g.getWorldMoneys().forceRiskOff(1);
        String log = playCaptured(g);
        double level = g.getWorldMoneys().riskOff(), ranHome = hot.getRiskOffDeparted();
        assertTrue("the world is in a risk-off of at least the size forced (" + String.format("%.2f", level) + ")", level >= 1 - 1e-9 && g.getWorldMoneys().riskOffStarted());
        assertTrue("the hot money's risk-off departure the month is over zero (D$" + String.format("%,.1fk", ranHome) + ")", ranHome > 0);
        close("...the share of the stock it took is the dial times the level (capped at the whole): " + CapitalFlows.RISK_OFF_HOT_UNWIND + " x " + String.format("%.2f", level),
                ranHome / (hot.getStock() + ranHome), Math.min(1, CapitalFlows.RISK_OFF_HOT_UNWIND * level), 1e-9);
        assertTrue("...inside the month's departures", hot.getDeparted() >= ranHome - 1e-9);
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        assertTrue("...the audit saw it cross the edge (financial out D$" + String.format("%,.1fk", audit.financialOut) + ") and the month closed (residual " + String.format("%.2e", audit.relative()) + ")",
                audit.financialOut >= ranHome - 1e-6 && Math.abs(audit.relative()) < 1e-9);
        close("...the bank's foreign deposits are the stock that is left", g.getBank().getForeignDeposits(), hot.getStock(), 1e-9);
        assertTrue("...and the log says money ran home", log.contains("Money ran home:") && log.contains("of hot money left"));
        assertTrue("the risk-off is counted, with the lifetime", hot.getRiskOffMonths() >= 1 && hot.getRiskOffDepartedLifetime() >= ranHome);
        // The months after, the risk-off fades by half in three months and takes less; the audit still closes.
        double levelAfter = 0, worstAfter = 0;
        for (int m = 1; m <= 4; m++) {
            play(g);
            worstAfter = Math.max(worstAfter, Math.abs(g.getLastMoneyAudit().relative()));
            if (m == 1) levelAfter = g.getWorldMoneys().riskOff();
        }
        assertTrue("the month after the level has faded (" + String.format("%.2f", levelAfter) + " from " + String.format("%.2f", level) + ")", levelAfter < level);
        assertTrue("...four more months, and every one's audit closed (worst " + String.format("%.2e", worstAfter) + ")", worstAfter < 1e-9);

        // A city quieted for the D$ earns nothing and unwinds nothing, though its own moneys are in a risk-off and it holds hot money.
        Game q = hotTown("interest-abroad-quiet-earn", true);
        DebtManager qdm = q.getDebtManager();
        qdm.setAutopilot(false);
        qdm.setPolicyRate(HOT_DIAL);
        for (int m = 1; m <= months; m++) play(q);
        q.getWorldMoneys().forceRiskOff(1);
        play(q);
        CapitalFlows qHot = q.getCapitalFlows();
        assertTrue("fixture: the quieted city holds hot money (D$" + String.format("%,.0fk", qHot.getStock()) + ") and its own moneys are in a risk-off (" + String.format("%.2f", q.getWorldMoneys().riskOff()) + ")",
                qHot.getStock() > 1000 && q.getWorldMoneys().riskOff() >= 1 - 1e-9);
        ForeignAccounts qFx = q.getForeignAccounts();
        assertTrue("the quieted city's vault earned nothing, month or lifetime", qFx.getVaultInterestUsd() == 0 && qFx.getVaultInterestUsdLifetime() == 0 && qFx.getVaultYield() == 0);
        assertTrue("...and the risk-off unwound none of its hot money or its carry", qHot.getRiskOffDeparted() == 0 && qHot.getRiskOffDepartedLifetime() == 0 && qHot.getCarryRiskRepaidLifetime() == 0
                && qHot.getRiskOffMonths() == 0);

        // A city on the literals has no policy rates and nothing to earn.
        Game lit = town("interest-abroad-literal-earn", false, true);
        for (int m = 1; m <= 3; m++) play(lit);
        assertTrue("a city on the literals (no world, no policy rates) earns nothing and unwinds nothing",
                lit.getForeignAccounts().getVaultInterestUsd() == 0 && lit.getForeignAccounts().getVaultInterestUsdLifetime() == 0 && lit.getCapitalFlows().getRiskOffMonths() == 0);

        // A save carries the lot: the loaded city continues bit for bit and does not earn the saved month twice.
        double[] savedFx = fx.toSaveArray(), savedHot = hot.toSaveArray();
        assertTrue("saved, to its scratch folder", g.saveGame(2, "earned").ok);
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(2));
        assertTrue("the loaded city has the vault's balances and what it earned, to the bit, so the saved month is not earned a second time", back.getLoadFailure() == null
                && Arrays.equals(back.getForeignAccounts().toSaveArray(), savedFx));
        assertTrue("...and the hot money's unwinding as it was", Arrays.equals(back.getCapitalFlows().toSaveArray(), savedHot));
        for (int m = 1; m <= 3; m++) {
            play(g);
            play(back);
        }
        assertTrue("both play three months more and stand in the same state, the vault and the hot money to the bit",
                Arrays.equals(g.getForeignAccounts().toSaveArray(), back.getForeignAccounts().toSaveArray()) && Arrays.equals(g.getCapitalFlows().toSaveArray(), back.getCapitalFlows().toSaveArray()));
        out.printf("   the vault earned US$%,.0fk over %d months (a %.2f%% yield now); %d risk-off month(s) sent D$%,.0fk of hot money home%n", g.getForeignAccounts().getVaultInterestUsdLifetime(),
                months + 8, g.getForeignAccounts().getVaultYield() * 100, g.getCapitalFlows().getRiskOffMonths(), g.getCapitalFlows().getRiskOffDepartedLifetime());
    }
}
