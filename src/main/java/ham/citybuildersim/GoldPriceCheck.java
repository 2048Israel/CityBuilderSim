package ham.citybuildersim;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntToDoubleFunction;
import java.util.function.Supplier;

/**
 * Gold's price a month (batch GD1; spec-gold-money 4.1): GoldPrice's statutes, its table, its refusal of a table that is not whole, and the rule
 * after the table, each held to the figures the study publishes and to the constants the class publishes, never a formula copied from it (the one
 * exception is the draw's tags, restated once to prove them).
 *
 * WHY. A par that is a digit off, a table read in the wrong unit, a July that is not the year's price, a rule whose pull or sigma is not the dial's,
 * and a walk that depends on the order it is asked in would each leave GD2's vault holding a price that is not the one the study says. Nothing in the
 * economy reads GoldPrice yet, so nothing else would notice.
 *
 * THE DATA. No save, no city and no world file: sections 1 to 3, 6 and 7 read GoldPrice on a fixture US price level (3% a year), sections 4 and 5 on the
 * Nations of the jar's twelve-nation fixture (WorldData.FIXTURE), which is always beside the classes.
 *
 * What this has to prove:
 *   1. THE PARS TO THE DIGIT: PAR_1900 rounds to 20.6718; January 1900 to March 1933 is PAR_1900 exactly and February 1934 to July 1971 is PAR_1934
 *      exactly, on both paths; April 1933 to January 1934 is a geometric path, each step the same ratio, ending on PAR_1934; 1933's mean is the
 *      study's 25.53 (USGS recorded 25.56) and 1934's is 35.00.
 *   2. THE TABLE'S MONTHS: 55 years read, 1971 to 2025; 1980's figure is the source's 19,700,000 a ton in ounces; every July from 1972 is its year's
 *      price on Historical, the months between are geometric, July 1971 is 35 though the table's 1971 is above 41, August 1971 lies between, and
 *      August to December 2025 hold 2025's price.
 *   3. A BROKEN TABLE IS REFUSED, NAMING ITS LINE: a gap, a year out of order or repeated, a value that is zero, negative or no number, an unknown
 *      unit, a line of three fields or of five, a table that ends at 2024, begins at 1972 or runs past 2025, a missing header; the shipped text reads.
 *   4. THE RULE'S SIGMA AND PULL: over eight seeds on Random, 4,800 months each, the slope of x(t) on x(t - 1) is PHI and the residual's sd is SIGMA, the
 *      pooled mean of the last 2,400 months is within four of its expected sd of 0; the walk is the rule's (three months restated through the draw's
 *      tags); on Historical the first month follows from December 2025's US$3,300, and a run to 2400 is finite and above zero.
 *   5. A RELOAD CONTINUES THE PATH: a GoldPrice asked month 6000 first and then month 1000 gives every month 0 to 6000 to the bit what one asked in
 *      order gives, on both paths; another seed is the same to July 1971 (Random) or December 2025 (Historical) and different in every ruled month.
 *   6. THE PATHS AGREE IN THE OFFICIAL YEARS: Random and Historical are identical every month to July 1971, and differ in every month after.
 *   7. GOLD IS CHEAP AT THE START: PAR_1900 over PriceLevel.FACTOR is about US$798 of 2025, and 2025's US$3,300 is about 4.13 times that.
 */
public class GoldPriceCheck {

    static int fails = 0;
    /** Where the harness prints: the console. */
    static final PrintStream out = System.out;

    /** The seed the single-path sections use, and the eight (it and seven more) the rule is regressed over. */
    static final long SEED = Nations.DEFAULT_SEED;
    static final long[] SEEDS = {SEED, 7_001, 7_002, 7_003, 7_004, 7_005, 7_006, 7_007};

    /** The months of a regression run, and the last of them the stationary mean is read over. */
    static final int RUN = 4_800, TAIL = 2_400;

    /** The last month a reload is compared to: January 2400. */
    static final int LAST = at(2400, 1);

    /** The jar's twelve-nation fixture, open for sections 4 and 5. */
    static WorldData fixture;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-130s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Two figures that should be one, to a tolerance. */
    static void close(String label, double got, double wanted, double tol) {
        assertTrue(String.format("%s: %.8f against %.8f", label, got, wanted), Math.abs(got - wanted) <= tol);
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

    /** The month index of a calendar month: months since January of the calendar's first year (Nations.FIRST_YEAR). */
    static int at(int year, int month) { return (year - Nations.FIRST_YEAR) * 12 + month - 1; }

    /** A US price level a year to stand where a world file is not wanted: 3% a year from 8.333 (any base would do). */
    static final IntToDoubleFunction US_LINE = year -> 8.333 * Math.pow(1.03, year - Nations.FIRST_YEAR);

    static GoldPrice gold(Nations.Path path, long seed) { return new GoldPrice(path, seed, US_LINE); }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        thePars();
        theTable();
        aBrokenTable();
        byte[] bytes = WorldDataCheck.resource(WorldData.FIXTURE);
        assertTrue("the twelve-nation fixture is beside the classes (" + WorldData.FIXTURE + "): sections 4 and 5 read it", bytes != null);
        if (bytes != null) {
            try (WorldData data = WorldData.of(bytes)) {
                fixture = data;
                theRule();
                aReloadContinues();
            }
        }
        thePathsAgree();
        cheapAtTheStart();
        out.printf("%nGoldPriceCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static Nations nations(Nations.Path path, long seed) { return new Nations(fixture, path, seed); }

    /* =====================================================================
       1. THE PARS TO THE DIGIT
       ===================================================================== */

    static void thePars() {
        out.println("--- 1. the pars to the digit ---");
        close("PAR_1900 rounds to the Gold Standard Act's 20.6718", GoldPrice.PAR_1900, 20.6718, 5e-5);
        assertTrue("...and is 480 grains an ounce over 23.22 fine, the dollar's 25.8 grains nine-tenths fine: " + GoldPrice.PAR_1900, same(GoldPrice.PAR_1900, 480.0 / 23.22));
        assertTrue("PAR_1934 is Proclamation 2072's 35", GoldPrice.PAR_1934 == 35);
        assertTrue("the float starts in April 1933 and ends in January 1934, the last par month is July 1971",
                GoldPrice.FLOAT_START == at(1933, 4) && GoldPrice.FLOAT_START + GoldPrice.FLOAT_STEPS - 1 == at(1934, 1) && GoldPrice.LAST_PAR_MONTH == at(1971, 7));
        String[] names = {"Random", "Historical"};
        Nations.Path[] paths = {Nations.Path.RANDOM, Nations.Path.HISTORICAL};
        for (int p = 0; p < 2; p++) {
            GoldPrice g = gold(paths[p], SEED);
            Tally old = new Tally(), par = new Tally();
            for (int t = 0; t < GoldPrice.FLOAT_START; t++) {
                final int month = t;
                old.see(same(g.usdPerOunce(t), GoldPrice.PAR_1900), () -> "month " + month + " is " + g.usdPerOunce(month));
            }
            tally("on " + names[p] + " every month from January 1900 to March 1933 is PAR_1900 exactly", old);
            for (int t = GoldPrice.FLOAT_START + GoldPrice.FLOAT_STEPS; t <= GoldPrice.LAST_PAR_MONTH; t++) {
                final int month = t;
                par.see(same(g.usdPerOunce(t), GoldPrice.PAR_1934), () -> "month " + month + " is " + g.usdPerOunce(month));
            }
            tally("on " + names[p] + " every month from February 1934 to July 1971 is PAR_1934 exactly", par);
        }
        GoldPrice g = gold(Nations.Path.HISTORICAL, SEED);
        double ratio = Math.pow(GoldPrice.PAR_1934 / GoldPrice.PAR_1900, 1.0 / GoldPrice.FLOAT_STEPS);
        close("April 1933 is PAR_1900 x r^(1/" + GoldPrice.FLOAT_STEPS + "), r the old par to the new", g.usdPerOunce(GoldPrice.FLOAT_START), GoldPrice.PAR_1900 * ratio, 1e-9);
        close("January 1934 is PAR_1934", g.usdPerOunce(GoldPrice.FLOAT_START + GoldPrice.FLOAT_STEPS - 1), GoldPrice.PAR_1934, 1e-9);
        Tally steps = new Tally();
        for (int t = GoldPrice.FLOAT_START; t < GoldPrice.FLOAT_START + GoldPrice.FLOAT_STEPS; t++) {
            final int month = t;
            double step = g.usdPerOunce(t) / g.usdPerOunce(t - 1);
            steps.see(Math.abs(step / ratio - 1) < 1e-12, () -> "the step into month " + month + " is " + step + ", not " + ratio);
        }
        tally("each of the " + GoldPrice.FLOAT_STEPS + " steps from March 1933 to January 1934 is the same ratio " + ratio, steps);
        double sum33 = 0, sum34 = 0;
        for (int m = 1; m <= 12; m++) {
            sum33 += g.usdPerOunce(at(1933, m));
            sum34 += g.usdPerOunce(at(1934, m));
        }
        out.printf("1933's mean: %.4f here, the study's derivation 25.53, USGS's record 25.56%n", sum33 / 12);
        close("1933's mean rounds to the study's 25.53", sum33 / 12, 25.53, .005);
        close("...and is within 0.5% of USGS's recorded 25.56", sum33 / 12, 25.56, 25.56 * .005);
        close("1934's mean is 35.00", sum34 / 12, 35.00, .005);
        assertTrue("the statute's months: official() is NaN after July 1971, isOfficial() says which, and January 1900 is the first",
                Double.isNaN(GoldPrice.official(GoldPrice.LAST_PAR_MONTH + 1)) && !GoldPrice.isOfficial(GoldPrice.LAST_PAR_MONTH + 1)
                        && GoldPrice.isOfficial(0) && GoldPrice.isOfficial(GoldPrice.LAST_PAR_MONTH) && !GoldPrice.isOfficial(-1));
        boolean refused = false;
        try {
            g.usdPerOunce(-1);
        } catch (IllegalArgumentException e) {
            refused = true;
        }
        assertTrue("a month before January 1900 is refused", refused);
    }

    /* =====================================================================
       2. THE TABLE'S MONTHS
       ===================================================================== */

    static int july(int year) { return at(year, 7); }

    static void theTable() {
        out.println("--- 2. the table's months ---");
        int years = 0;
        boolean whole = true;
        for (int y = GoldPrice.TABLE_FIRST_YEAR; y <= GoldPrice.TABLE_LAST_YEAR; y++) {
            double a = GoldPrice.annual(y);
            whole &= Double.isFinite(a) && a > 0;
            years++;
        }
        assertTrue("the table has 55 years, " + GoldPrice.TABLE_FIRST_YEAR + " to " + GoldPrice.TABLE_LAST_YEAR + ", each above zero (read " + years + ")", years == 55 && whole);
        assertTrue("the table's last year is the nations' last year of history (" + Nations.HISTORY_LAST_YEAR + ")", GoldPrice.TABLE_LAST_YEAR == Nations.HISTORY_LAST_YEAR);
        close("1980's figure is the source's 19,700,000 US$ a metric ton in troy ounces", GoldPrice.annual(1980), 19_700_000 / (1e6 / GoldPrice.GRAMS_PER_TROY_OUNCE), 1e-9);
        close("...and to the cent it is 612.74", GoldPrice.annual(1980), 612.74, .005);
        assertTrue("2023, 2024 and 2025 are Mineral Commodity Summaries 2026's 1,945, 2,388 and 3,300 US$ an ounce as printed",
                GoldPrice.annual(2023) == 1945 && GoldPrice.annual(2024) == 2388 && GoldPrice.annual(2025) == 3300);
        boolean before = false, after = false;
        try {
            GoldPrice.annual(GoldPrice.TABLE_FIRST_YEAR - 1);
        } catch (IllegalArgumentException e) {
            before = true;
        }
        try {
            GoldPrice.annual(GoldPrice.TABLE_LAST_YEAR + 1);
        } catch (IllegalArgumentException e) {
            after = true;
        }
        assertTrue("a year outside the table is refused, before 1971 and after 2025", before && after);
        GoldPrice g = gold(Nations.Path.HISTORICAL, SEED);
        Tally julys = new Tally();
        for (int y = GoldPrice.TABLE_FIRST_YEAR + 1; y <= GoldPrice.TABLE_LAST_YEAR; y++) {
            final int year = y;
            julys.see(Math.abs(g.usdPerOunce(july(y)) - GoldPrice.annual(y)) < 1e-9, () -> "July " + year + " is " + g.usdPerOunce(july(year)) + ", the year's " + GoldPrice.annual(year));
        }
        tally("every July from 1972 to 2025 is its year's price on Historical, to 1e-9", julys);
        double a79 = Math.log(GoldPrice.annual(1979)), a80 = Math.log(GoldPrice.annual(1980));
        double march80 = Math.exp(a79 + (at(1980, 3) - july(1979)) / 12.0 * (a80 - a79));
        out.printf("March 1980: %.4f here, geometric between July 1979 (%.2f) and July 1980 (%.2f): %.4f%n", g.usdPerOunce(at(1980, 3)), GoldPrice.annual(1979), GoldPrice.annual(1980), march80);
        assertTrue("March 1980 is the geometric interpolation between the Julys of 1979 and 1980, to 1e-9", Math.abs(g.usdPerOunce(at(1980, 3)) / march80 - 1) < 1e-9);
        Tally between = new Tally();
        for (int t = july(1971) + 1; t < july(GoldPrice.TABLE_LAST_YEAR); t++) {
            final int month = t;
            int y = Nations.FIRST_YEAR + (t - 6) / 12;
            double lo = y == 1971 ? GoldPrice.PAR_1934 : GoldPrice.annual(y), hi = GoldPrice.annual(y + 1), p = g.usdPerOunce(t);
            between.see(p >= Math.min(lo, hi) * (1 - 1e-12) && p <= Math.max(lo, hi) * (1 + 1e-12), () -> "month " + month + " is " + p + ", off its Julys " + lo + " and " + hi);
        }
        tally("every month from August 1971 to June 2025 lies between its two Julys' figures", between);
        close("July 1971 is PAR_1934", g.usdPerOunce(july(1971)), GoldPrice.PAR_1934, 1e-12);
        assertTrue("...though the table's 1971 is the free market's " + String.format("%.2f", GoldPrice.annual(1971)) + ", above 41", GoldPrice.annual(1971) > 41);
        double aug71 = g.usdPerOunce(july(1971) + 1);
        assertTrue(String.format("August 1971 (%.4f) lies between 35 and 1972's %.4f", aug71, GoldPrice.annual(1972)), aug71 > GoldPrice.PAR_1934 && aug71 < GoldPrice.annual(1972));
        Tally holds = new Tally();
        for (int m = 8; m <= 12; m++) {
            final int month = m;
            holds.see(Math.abs(g.usdPerOunce(at(2025, m)) - GoldPrice.annual(2025)) < 1e-9, () -> "month " + month + " of 2025 is " + g.usdPerOunce(at(2025, month)));
        }
        tally("August to December 2025 equal 2025's price (est.: no July 2026)", holds);
    }

    /* =====================================================================
       3. A BROKEN TABLE IS REFUSED, NAMING ITS LINE
       ===================================================================== */

    /** A good table's rows: a year each, 1971 to 2025, the value 100 plus the year after 1971, so row i is line i + 3 in the text below. */
    static List<String> rows() {
        List<String> rows = new ArrayList<>();
        for (int y = GoldPrice.TABLE_FIRST_YEAR; y <= GoldPrice.TABLE_LAST_YEAR; y++) rows.add(y + "," + (100 + y - GoldPrice.TABLE_FIRST_YEAR) + ",usd_per_ozt,test");
        return rows;
    }

    /** A table's text: a comment line, the header, the rows. */
    static String text(List<String> rows) { return "# a comment\n" + GoldPrice.HEADER + "\n" + String.join("\n", rows) + "\n"; }

    static void refused(String label, String text, int line) {
        String needle = GoldPrice.RESOURCE + " line " + line + " (";
        try {
            GoldPrice.readTable(text);
            assertTrue(label + " is refused", false);
        } catch (IllegalStateException e) {
            assertTrue(label + " is refused, naming line " + line + ": " + e.getMessage(), e.getMessage() != null && e.getMessage().contains(needle));
        }
    }

    static void aBrokenTable() throws Exception {
        out.println("--- 3. a broken table is refused, naming its line ---");
        List<String> good = rows();
        double[] read = GoldPrice.readTable(text(good));
        assertTrue("fixture: a table of 55 rows reads, usd_per_ozt as it stands (1971 and 2025 are " + read[0] + " and " + read[read.length - 1] + ")",
                read.length == 55 && read[0] == 100 && read[54] == 154);
        List<String> unit = rows();
        unit.set(9, "1980,19700000,usd_per_t,test");
        close("...and usd_per_t is divided by the troy ounces in a ton (1980's 19,700,000)", GoldPrice.readTable(text(unit))[9], 612.74, .005);
        List<String> r = rows();
        r.remove(19);
        refused("a gap (1990 missing)", text(r), 22);
        r = rows();
        r.set(20, "1989,120,usd_per_ozt,test");
        refused("a year out of order (1989 after 1990)", text(r), 23);
        r = rows();
        r.set(20, "1990,120,usd_per_ozt,test");
        refused("a year repeated (1990 twice)", text(r), 23);
        r = rows();
        r.set(10, "1981,0,usd_per_ozt,test");
        refused("a value of zero", text(r), 13);
        r.set(10, "1981,-5,usd_per_ozt,test");
        refused("a negative value", text(r), 13);
        r.set(10, "1981,NaN,usd_per_ozt,test");
        refused("a value that is NaN", text(r), 13);
        r.set(10, "1981,abc,usd_per_ozt,test");
        refused("a value that is no number", text(r), 13);
        r.set(10, "1981,100,usd_per_kg,test");
        refused("an unknown unit", text(r), 13);
        r.set(10, "1981,100,usd_per_ozt");
        refused("a line of three fields", text(r), 13);
        r.set(10, "1981,100,usd_per_ozt,test,more");
        refused("a line of five fields", text(r), 13);
        r = rows();
        r.remove(54);
        refused("a table that ends at 2024", text(r), 56);
        r = rows();
        r.remove(0);
        refused("a table that begins at 1972", text(r), 3);
        r = rows();
        r.add("2026,200,usd_per_ozt,test");
        refused("a table that runs past 2025", text(r), 58);
        refused("a table with no header", "# a comment\n" + String.join("\n", rows()) + "\n", 2);
        try {
            GoldPrice.readTable("# nothing but a comment\n");
            assertTrue("a table with nothing in it is refused", false);
        } catch (IllegalStateException e) {
            assertTrue("a table with nothing in it is refused, naming the resource: " + e.getMessage(), e.getMessage().contains(GoldPrice.RESOURCE));
        }
        try (java.io.InputStream in = GoldPrice.class.getResourceAsStream(GoldPrice.RESOURCE)) {
            assertTrue("the shipped table is on the classpath (" + GoldPrice.RESOURCE + ")", in != null);
            if (in != null) {
                double[] shipped = GoldPrice.readTable(new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
                boolean agree = shipped.length == 55;
                for (int y = GoldPrice.TABLE_FIRST_YEAR; agree && y <= GoldPrice.TABLE_LAST_YEAR; y++) agree = same(shipped[y - GoldPrice.TABLE_FIRST_YEAR], GoldPrice.annual(y));
                assertTrue("the shipped text reads, 55 years, and is what annual() gives", agree);
            }
        }
    }

    /* =====================================================================
       4. THE RULE'S SIGMA AND PULL
       ===================================================================== */

    /** ln of the nations' US price level in month t, restated: log-linear between Januaries. */
    static double lnUs(Nations nations, int t) {
        int usa = nations.index(Nations.FRONTIER), year = Nations.FIRST_YEAR + t / 12;
        double a = Math.log(nations.year(usa, year).priceLevel()), b = Math.log(nations.year(usa, year + 1).priceLevel());
        return a + (t % 12) / 12.0 * (b - a);
    }

    /** The draw the rule uses in month t, restated through Nations.scramble with the tags GoldPrice keeps (9_998, 300). */
    static double draw(long seed, int t) { return new Random(Nations.scramble(seed, 9_998, t, 300)).nextGaussian(); }

    /** x of month t: ln of the real price over the target. */
    static double x(GoldPrice g, int t) { return Math.log(g.realPrice(t)) - Math.log(GoldPrice.targetRealPrice()); }

    static void theRule() {
        out.println("--- 4. the rule's sigma and pull ---");
        double target = 0;
        for (int y = GoldPrice.TABLE_LAST_YEAR - GoldPrice.MEAN_YEARS + 1; y <= GoldPrice.TABLE_LAST_YEAR; y++)
            target += GoldPrice.annual(y) * PriceLevel.usCpi(Nations.FIRST_YEAR) / PriceLevel.usCpi(y) / GoldPrice.MEAN_YEARS;
        out.printf("the target: US$%.4f of January 1900 (the mean of 2006 to 2025 by the CPI)%n", GoldPrice.targetRealPrice());
        close("targetRealPrice() is the mean of the last 20 years' prices in dollars of 1900", GoldPrice.targetRealPrice(), target, 1e-9);
        assertTrue("...about US$48.3", Math.abs(GoldPrice.targetRealPrice() - 48.3) < .5);
        double sxx = 0, sxy = 0, syy = 0, tailSum = 0;
        int n = 0;
        for (long seed : SEEDS) {
            GoldPrice g = new GoldPrice(nations(Nations.Path.RANDOM, seed));
            assertTrue("seed " + seed + ": the rule begins in August 1971 on Random (ruleStart " + g.ruleStart() + ")", g.ruleStart() == GoldPrice.RANDOM_RULE_START && g.ruleStart() == at(1971, 8));
            double[] xs = new double[RUN];
            for (int i = 0; i < RUN; i++) xs[i] = x(g, g.ruleStart() + i);
            for (int i = 1; i < RUN; i++) {
                sxx += xs[i - 1] * xs[i - 1];
                sxy += xs[i - 1] * xs[i];
                syy += xs[i] * xs[i];
                n++;
            }
            double tail = 0;
            for (int i = RUN - TAIL; i < RUN; i++) tail += xs[i] / TAIL;
            tailSum += tail;
        }
        double slope = sxy / sxx, rss = syy - 2 * slope * sxy + slope * slope * sxx, sd = Math.sqrt(rss / n);
        out.printf("%d seeds x %d months: slope %.5f (PHI %.5f), residual sd %.5f (SIGMA %.5f, ratio %.4f), n = %d%n", SEEDS.length, RUN, slope, GoldPrice.PHI, sd, GoldPrice.SIGMA, sd / GoldPrice.SIGMA, n);
        close("the slope of x(t) on x(t - 1), through the origin, is PHI", slope, GoldPrice.PHI, .004);
        assertTrue("the residual's sd is within 3% of SIGMA", Math.abs(sd / GoldPrice.SIGMA - 1) < .03);
        double varX = GoldPrice.SIGMA * GoldPrice.SIGMA / (1 - GoldPrice.PHI * GoldPrice.PHI), weighted = 0;
        for (int k = 1; k < TAIL; k++) weighted += (TAIL - k) * Math.pow(GoldPrice.PHI, k);
        double sdMean = Math.sqrt(varX / ((double) TAIL * TAIL) * (TAIL + 2 * weighted) / SEEDS.length), mean = tailSum / SEEDS.length;
        out.printf("the last %d months of %d runs: pooled mean of x %.4f; expected sd of that mean %.4f (stationary sd %.4f, the AR(1)'s autocorrelation counted), %.2f of them%n",
                TAIL, SEEDS.length, mean, sdMean, Math.sqrt(varX), mean / sdMean);
        assertTrue("the pooled mean of x over the last " + TAIL + " months is within 4 of its expected sd of 0", Math.abs(mean) <= 4 * sdMean);
        // the walk is the rule's: three months restated through the draw's tags
        Nations random = nations(Nations.Path.RANDOM, SEED);
        GoldPrice g = new GoldPrice(random);
        int s = g.ruleStart();
        double opening = Math.log(GoldPrice.PAR_1934) - (lnUs(random, s - 1) - lnUs(random, 0)) - Math.log(GoldPrice.targetRealPrice());
        close("July 1971's state follows from the statute's US$35 and the path's US line", x(g, s - 1), opening, 1e-9);
        for (int t : new int[]{s, s + 1, s + 700}) {
            double before = t == s ? opening : x(g, t - 1);
            close("month " + t + ": x = PHI x(t - 1) + SIGMA x the draw (tags 9998, 300)", x(g, t), GoldPrice.PHI * before + GoldPrice.SIGMA * draw(SEED, t), 1e-9);
        }
        // Historical: the first ruled month follows from December 2025
        Nations historical = nations(Nations.Path.HISTORICAL, SEED);
        GoldPrice h = new GoldPrice(historical);
        int hs = h.ruleStart();
        assertTrue("the rule begins in January 2026 on Historical (ruleStart " + hs + ")", hs == GoldPrice.HISTORICAL_RULE_START && hs == at(2026, 1));
        close("December 2025 is the table's US$3,300", h.usdPerOunce(hs - 1), 3300, 1e-9);
        double open = Math.log(3300) - (lnUs(historical, hs - 1) - lnUs(historical, 0)) - Math.log(GoldPrice.targetRealPrice());
        out.printf("December 2025: US$3,300 is %.4f of January 1900 against the target's %.4f, x = %.4f%n", h.realPrice(hs - 1), GoldPrice.targetRealPrice(), open);
        close("January 2026: x = PHI x(December 2025) + SIGMA x the draw", x(h, hs), GoldPrice.PHI * open + GoldPrice.SIGMA * draw(SEED, hs), 1e-9);
        Tally finite = new Tally();
        for (int t = hs; t <= LAST; t++) {
            final int month = t;
            double p = h.usdPerOunce(t);
            finite.see(Double.isFinite(p) && p > 0, () -> "month " + month + " is " + p);
        }
        tally("a Historical run from January 2026 to January 2400 is finite and above zero (" + (LAST - hs + 1) + " months)", finite);
        Tally randomFinite = new Tally();
        for (int t = 0; t <= LAST; t++) {
            final int month = t;
            double p = g.usdPerOunce(t);
            randomFinite.see(Double.isFinite(p) && p > 0, () -> "month " + month + " is " + p);
        }
        tally("a Random run from January 1900 to January 2400 is finite and above zero (" + (LAST + 1) + " months)", randomFinite);
        for (int year : new int[]{2025, 2100, 2200, 2400})
            out.printf("Random at seed %d, July %d: US$%.2f an ounce, US$%.2f of January 1900%n", SEED, year, g.usdPerOunce(july(year)), g.realPrice(july(year)));
        for (int year : new int[]{2100, 2400})
            out.printf("Historical at seed %d, July %d: US$%.2f an ounce, US$%.2f of January 1900%n", SEED, year, h.usdPerOunce(july(year)), h.realPrice(july(year)));
    }

    /* =====================================================================
       5. A RELOAD CONTINUES THE PATH
       ===================================================================== */

    static void aReloadContinues() {
        out.println("--- 5. a reload continues the path ---");
        for (Nations.Path path : Nations.Path.values()) {
            GoldPrice late = new GoldPrice(nations(path, SEED)), inOrder = new GoldPrice(nations(path, SEED));
            double far = late.usdPerOunce(LAST);
            double near = late.usdPerOunce(1_000);
            Tally agree = new Tally();
            for (int t = 0; t <= LAST; t++) {
                final int month = t;
                double a = late.usdPerOunce(t), b = inOrder.usdPerOunce(t);
                agree.see(same(a, b), () -> "month " + month + ": " + a + " against " + b);
            }
            tally(path + ": asked January 2400 first and 1983 after, every month 0 to " + LAST + " is bit for bit what asking in order gives", agree);
            assertTrue(path + ": the first two answers were the ones in order (" + String.format("%.2f", far) + " and " + String.format("%.2f", near) + ")",
                    same(far, inOrder.usdPerOunce(LAST)) && same(near, inOrder.usdPerOunce(1_000)));
            Tally real = new Tally();
            for (int t = 0; t <= LAST; t += 7) {
                final int month = t;
                real.see(same(late.realPrice(t), inOrder.realPrice(t)), () -> "month " + month);
            }
            tally(path + ": the real price is the same to the bit (every seventh month)", real);
        }
        long other = SEED + 1;
        for (Nations.Path path : Nations.Path.values()) {
            GoldPrice a = new GoldPrice(nations(path, SEED)), b = new GoldPrice(nations(path, other));
            int first = path == Nations.Path.RANDOM ? GoldPrice.RANDOM_RULE_START : GoldPrice.HISTORICAL_RULE_START;
            int identical = 0, differ = 0;
            for (int t = 0; t < first; t++) if (same(a.usdPerOunce(t), b.usdPerOunce(t))) identical++;
            for (int t = first; t <= LAST; t++) if (!same(a.usdPerOunce(t), b.usdPerOunce(t))) differ++;
            out.printf("%s, seed %d against %d: %d of the first %d months identical, %d of the %d ruled months differ%n", path, SEED, other, identical, first, differ, LAST - first + 1);
            assertTrue(path + ": another seed is identical to " + (path == Nations.Path.RANDOM ? "July 1971" : "December 2025") + " and different in every ruled month after",
                    identical == first && differ == LAST - first + 1);
        }
    }

    /* =====================================================================
       6. THE PATHS AGREE IN THE OFFICIAL YEARS
       ===================================================================== */

    static void thePathsAgree() {
        out.println("--- 6. the paths agree in the official years ---");
        GoldPrice random = gold(Nations.Path.RANDOM, SEED), historical = gold(Nations.Path.HISTORICAL, SEED);
        Tally agree = new Tally(), differ = new Tally();
        for (int t = 0; t <= GoldPrice.LAST_PAR_MONTH; t++) {
            final int month = t;
            agree.see(same(random.usdPerOunce(t), historical.usdPerOunce(t)), () -> "month " + month + ": " + random.usdPerOunce(month) + " against " + historical.usdPerOunce(month));
        }
        tally("Random and Historical are identical, to the bit, every month from January 1900 to July 1971", agree);
        for (int t = GoldPrice.LAST_PAR_MONTH + 1; t <= LAST; t++) {
            final int month = t;
            differ.see(!same(random.usdPerOunce(t), historical.usdPerOunce(t)), () -> "month " + month + " is " + random.usdPerOunce(month) + " on both");
        }
        tally("...and differ in every month from August 1971 to January 2400", differ);
    }

    /* =====================================================================
       7. GOLD IS CHEAP AT THE START
       ===================================================================== */

    static void cheapAtTheStart() {
        out.println("--- 7. gold is cheap at the start ---");
        double today = GoldPrice.PAR_1900 / PriceLevel.FACTOR, wanted = GoldPrice.PAR_1900 * PriceLevel.usCpi(2025) / PriceLevel.usCpi(Nations.FIRST_YEAR);
        out.printf("PAR_1900 over PriceLevel.FACTOR: US$%.2f of 2025; 2025's US$3,300 is %.3f times that, %.2f at 1900's level%n", today, 3300 / today, 3300 * PriceLevel.FACTOR);
        close("PAR_1900 over PriceLevel.FACTOR is PAR_1900 x the CPI of 2025 over 1900's", today, wanted, 1e-6);
        assertTrue("...about US$798 of 2025", today > 797 && today < 800);
        assertTrue("2025's US$3,300 is about 4.13 times that", Math.abs(3300 / today - 4.13) < .02);
    }
}
