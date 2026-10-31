package ham.citybuildersim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The policy rates of the eight central banks of the moneys the vault holds (XR4; spec-exchange-rates 3.8, Jerus's calls 9 and 11): the Federal
 * Reserve's, the ECB's, the Bank of Japan's, the Bank of England's, the People's Bank's, the Bank of Canada's, the Reserve Bank of Australia's and the
 * Swiss National Bank's, a month at a time. On the Historical path their real rates January 2000 to December 2025 (policy-rates.tsv), then a rule;
 * on Random the rule from February 2000. The US's rate is the world's price of money to the city (DebtManager.worldRate()). A pure model: it reads
 * the table, the nations' price levels through WorldMoneys and its own state, and it is handed to the city by Game.
 *
 * WHY THIS EXISTS. The world's price of money was DebtManager.WORLD_BASE_RATE, a constant 2%, against a world whose inflation is a path: the world's
 * real rate swung with the path (in slot 3's world, 0.4% a year, which made the world's real rate 1.6%), and the city's real gap against the world
 * with it. The D$ is a small open economy's money, and a small open economy's money is priced against the US's rate (the US$ is on one side of 89%
 * of all currency trades; spec-exchange-rates 3.8, star 9). The vault earned nothing on the moneys it held, and the carry and the hot money had no
 * rate abroad to compare the dial with. These are the rates, one for each of the eight.
 *
 * THE RULE. From its first month a bank's target is its neutral real rate plus the inflation it reads plus TAYLOR_WEIGHT of that inflation's
 * distance over its own target, never under LOWER_BOUND (Taylor, 1993). A shadow rate closes half its gap to the target in RULE_HALF_LIFE months (the
 * smoothing an estimated rule shows), and the policy rate moves in whole STEPs toward the shadow, never past it: the step central banks move in.
 * The inflation it reads is the anchor nation's price level over the year before (WorldMoneys.priceLevel), the year 2000's pace for the first
 * twelve months. A bank whose money has no anchor nation holds its last rate.
 *
 * THE SAVE is the month and each bank's rate, shadow, target and inflation (toSaveArray()), nothing NaN so it goes through JSON; a PolicyRates of
 * the same moneys that restores it steps on as this one does.
 */
public final class PolicyRates {

    /* =====================================================================
       THE DIALS - each with its source, or (est.) the game's estimate
       ===================================================================== */

    /** The step a policy rate moves in: a quarter point, the step central banks move in. */
    public static final double STEP = .0025;

    /** The least the rule's target is: the rule does not go below zero (est.: the negative rates of 2014-2024 are replayed on Historical, and the banks that ran them had left them by 2024). */
    public static final double LOWER_BOUND = 0;

    /** Half a point of rate over inflation for each point inflation runs over the target (Taylor, 1993). */
    public static final double TAYLOR_WEIGHT = .5;

    /** The months for the rate's shadow to close half its gap to the rule's target (est.: an estimated rule's quarterly smoothing of about 0.8). */
    public static final int RULE_HALF_LIFE = 9;

    /**
     * Each bank's inflation target, in WorldMoneys.EIGHT's order: 2% for all but the Reserve Bank of Australia's 2-3% band at its middle and the Swiss
     * National Bank's 0-2% at its middle; China's "around 2%" since 2025. A dial: not for a caller to change.
     */
    static final double[] TARGET = {.02, .02, .02, .02, .02, .02, .025, .01};

    /**
     * Each bank's neutral real rate, in WorldMoneys.EIGHT's order: its own published long-run neutral nominal rate less its target (est.: the Fed's
     * 3%, the ECB's about 2%, the Bank of Japan's 1-2.5% range low, the Bank of England's 3-3.5%, China's loan prime rate at 3%, the Bank of Canada's
     * 2.25-3.25% middle, the Reserve Bank of Australia's about 3.5%, the SNB's about 1%). A dial: not for a caller to change.
     */
    static final double[] NEUTRAL_REAL = {.01, 0, -.005, .01, .01, .0075, .01, 0};

    /** The share of the gap to its target the shadow closes in a month: one less 0.5 to the 1 over RULE_HALF_LIFE. */
    private static final double CLOSE = 1 - StrictMath.pow(.5, 1.0 / RULE_HALF_LIFE);

    /** The banks, in WorldMoneys.EIGHT's order. */
    static final int MONEYS = 8;

    /** The months the table covers: January 2000 to December 2025, the world file's (WorldMoneys.HISTORICAL_RULE_START). */
    static final int MONTHS = WorldMoneys.HISTORICAL_RULE_START;

    /** Where the table is, on the classpath. */
    static final String RESOURCE = "/policy-rates.tsv";

    /* =====================================================================
       THE TABLE - the real rates, loaded once
       ===================================================================== */

    private static double[][] table;

    /** The table: each bank's rate in each month, a fraction a year; loaded the first time it is asked, and refused, naming the line, if it is not whole. */
    private static synchronized double[][] table() {
        if (table == null) table = load();
        return table;
    }

    private static double[][] load() {
        try (InputStream in = PolicyRates.class.getResourceAsStream(RESOURCE)) {
            if (in == null) throw new IllegalStateException("policy rates: " + RESOURCE + " is not on the classpath");
            return parse(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("policy rates: " + RESOURCE + " could not be read", e);
        }
    }

    /**
     * A table's text read: one line for each change (code, tab, yyyy-mm, tab, percent a year), the comment lines and the blank ones skipped; each bank's
     * rate in each of the 312 months. Throws IllegalStateException, naming the line, for one that is not whole. Package-private for the check.
     */
    static double[][] parse(java.io.Reader source) throws IOException {
        List<List<double[]>> changes = new ArrayList<>();
        for (int k = 0; k < MONEYS; k++) changes.add(new ArrayList<>());
        BufferedReader reader = new BufferedReader(source);
        String line;
        int number = 0;
        while ((line = reader.readLine()) != null) {
            number++;
            if (line.isBlank() || line.startsWith("#")) continue;
            String where = RESOURCE + " line " + number + " (" + line + ")";
            String[] field = line.split("\t", -1);
            if (field.length != 3) throw new IllegalStateException("policy rates: " + where + " does not have three fields");
            int k = WorldMoneys.EIGHT.indexOf(field[0]);
            if (k < 0) throw new IllegalStateException("policy rates: " + where + " names a money that is not one of the eight");
            int t = monthOf(field[1], where);
            double rate;
            try {
                rate = new BigDecimal(field[2].trim()).movePointLeft(2).doubleValue();
            } catch (NumberFormatException e) {
                throw new IllegalStateException("policy rates: " + where + " has no rate", e);
            }
            if (!Double.isFinite(rate)) throw new IllegalStateException("policy rates: " + where + " has no rate");
            List<double[]> own = changes.get(k);
            if (own.isEmpty() && t != 0) throw new IllegalStateException("policy rates: " + where + " is the first of its money and is not January 2000");
            if (!own.isEmpty() && t <= (int) own.get(own.size() - 1)[0])
                throw new IllegalStateException("policy rates: " + where + " is not after the money's last change");
            own.add(new double[]{t, rate});
        }
        double[][] out = new double[MONEYS][MONTHS];
        for (int k = 0; k < MONEYS; k++) {
            List<double[]> own = changes.get(k);
            if (own.isEmpty()) throw new IllegalStateException("policy rates: " + RESOURCE + " has no line for " + WorldMoneys.EIGHT.get(k));
            for (int i = 0; i < own.size(); i++) {
                int from = (int) own.get(i)[0], to = i + 1 < own.size() ? (int) own.get(i + 1)[0] : MONTHS;
                Arrays.fill(out[k], from, to, own.get(i)[1]);
            }
        }
        return out;
    }

    /** The month index of "yyyy-mm" (months since January 2000); refused, naming the line, if it is not a month of 2000 to 2025. */
    private static int monthOf(String text, String where) {
        if (text.length() != 7 || text.charAt(4) != '-') throw new IllegalStateException("policy rates: " + where + " has no month yyyy-mm");
        int year, month;
        try {
            year = Integer.parseInt(text.substring(0, 4));
            month = Integer.parseInt(text.substring(5));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("policy rates: " + where + " has no month yyyy-mm", e);
        }
        int t = (year - Nations.FIRST_YEAR) * 12 + month - 1;
        if (month < 1 || month > 12 || t < 0 || t >= MONTHS) throw new IllegalStateException("policy rates: " + where + " is outside January 2000 to December 2025");
        return t;
    }

    /** A bank's real policy rate in a month (months since January 2000, WorldMoneys.monthIndex), a fraction a year: the last change at or before it. NaN outside the 312 months of the table and for a code that is not one of the eight. */
    public static double historical(String code, int t) {
        int k = WorldMoneys.EIGHT.indexOf(code);
        return k < 0 || t < 0 || t >= MONTHS ? Double.NaN : table()[k][t];
    }

    /** The bank k's (WorldMoneys.EIGHT's order) real rate in the month, NaN outside the table. */
    private static double historical(int k, int t) {
        return t < 0 || t >= MONTHS ? Double.NaN : table()[k][t];
    }

    /** The inflation target of a bank, a fraction a year; NaN for a code that is not one of the eight. */
    public static double inflationTargetOf(String code) {
        int k = WorldMoneys.EIGHT.indexOf(code);
        return k < 0 ? Double.NaN : TARGET[k];
    }

    /** The neutral real rate of a bank, a fraction a year; NaN for a code that is not one of the eight. */
    public static double neutralRealOf(String code) {
        int k = WorldMoneys.EIGHT.indexOf(code);
        return k < 0 ? Double.NaN : NEUTRAL_REAL[k];
    }

    /* =====================================================================
       THE BANKS - made once from the moneys
       ===================================================================== */

    private final WorldMoneys moneys;
    private final int ruleStart;

    /** Each bank's anchor nation (1-based), whose price level it reads: the US's for the dollar; 0, held, for a money with none. */
    private final int[] anchor = new int[MONEYS];

    /* ----- the state: saved ----- */
    private int month;
    private final double[] rate = new double[MONEYS], shadow = new double[MONEYS], target = new double[MONEYS], inflation = new double[MONEYS];

    /**
     * The banks of the moneys' world, standing in January 2000 (month() is 0): each bank's rate and shadow the table's of that month. The moneys give
     * the path (the rule's first month) and the nations' price levels; step() reads them, so they are the moneys the city steps.
     */
    public PolicyRates(WorldMoneys moneys) {
        this.moneys = Objects.requireNonNull(moneys, "moneys");
        ruleStart = moneys.ruleStart();
        for (int k = 0; k < MONEYS; k++) {
            String code = WorldMoneys.EIGHT.get(k);
            anchor[k] = k == 0 ? moneys.usNation() : moneys.anchorOf(code);
            rate[k] = shadow[k] = historical(k, 0);
            target[k] = inflation[k] = Double.NaN;
        }
        month = 0;
    }

    /* =====================================================================
       THE MONTH
       ===================================================================== */

    /** The month now, months since January 2000: 0 after the constructor. */
    public int month() { return month; }

    /** The first month the rule rules: 1 on Random, 312 on Historical (the moneys'). */
    public int ruleStart() { return ruleStart; }

    /** Whether the rule rules this month (Historical replays the table before it). */
    public boolean ruling() { return month >= ruleStart; }

    /**
     * Steps to month t, which has to be the next. Before the rule's first month a bank takes the table's rate, and its shadow with it. After it, each
     * bank reads its anchor's inflation, takes the rule's target for it, closes CLOSE of the shadow's gap to it, and moves its rate in whole STEPs
     * toward the shadow, never past it. A bank that reads no inflation holds its rate and its shadow.
     */
    public void step(int t) {
        if (t != month + 1) throw new IllegalArgumentException("policy rates: the month after " + month + " is " + (month + 1) + ", not " + t);
        month = t;
        for (int k = 0; k < MONEYS; k++) {
            if (t < ruleStart) {
                double real = historical(k, t);
                if (Double.isFinite(real)) rate[k] = shadow[k] = real;
                target[k] = inflation[k] = Double.NaN;
                continue;
            }
            double pi = inflationAt(anchor[k], t);
            if (!Double.isFinite(pi)) continue;
            inflation[k] = pi;
            target[k] = targetFor(k, pi);
            shadow[k] += CLOSE * (target[k] - shadow[k]);
            int steps = (int) ((shadow[k] - rate[k]) / STEP);
            rate[k] += steps * STEP;
        }
    }

    /** The rule's target for bank k (WorldMoneys.EIGHT's order) reading this inflation: its neutral real rate, the inflation and TAYLOR_WEIGHT of the inflation's distance over the bank's target, not under LOWER_BOUND. Pure. */
    static double targetFor(int k, double inflation) {
        return Math.max(LOWER_BOUND, NEUTRAL_REAL[k] + inflation + TAYLOR_WEIGHT * (inflation - TARGET[k]));
    }

    /** A nation's inflation at month t: its price level over the year before's, less one; for the first twelve months the year 2000's pace. NaN for no nation. */
    private double inflationAt(int nation, int t) {
        if (nation < 1) return Double.NaN;
        double now = moneys.priceLevel(nation, t >= 12 ? t : 12);
        double then = moneys.priceLevel(nation, t >= 12 ? t - 12 : 0);
        return now / then - 1;
    }

    /* =====================================================================
       WHAT A CALLER READS - a code that is not one of the eight reads NaN
       ===================================================================== */

    private static int at(String code) { return WorldMoneys.EIGHT.indexOf(code); }

    /** A bank's policy rate this month, a fraction a year; NaN for a code that is not one of the eight. */
    public double rate(String code) { return rate(at(code)); }

    /** The policy rate of the bank numbered k in WorldMoneys.EIGHT's order, a fraction a year; NaN for a number outside it. */
    public double rate(int k) { return k < 0 || k >= MONEYS ? Double.NaN : rate[k]; }

    /** The US's policy rate this month, a fraction a year: the world's price of money to the city. */
    public double usRate() { return rate[0]; }

    /** Where the rule has a bank's rate, a fraction a year: its shadow, which the rate follows in whole steps. */
    public double shadow(String code) { int k = at(code); return k < 0 ? Double.NaN : shadow[k]; }

    /** This month's rule target for a bank, a fraction a year; NaN before the rule rules (and for a bank that has read no inflation). */
    public double target(String code) { int k = at(code); return k < 0 ? Double.NaN : target[k]; }

    /** The inflation the rule last read for a bank, a fraction a year; NaN before the rule rules. */
    public double inflation(String code) { int k = at(code); return k < 0 ? Double.NaN : inflation[k]; }

    /* =====================================================================
       THE SAVE - the state, not what the table and the moneys give
       ===================================================================== */

    /** The current format of toSaveArray. */
    static final int SAVE_FORMAT = 1;

    private static final int HEADER = 3;

    /** What a target or an inflation not read yet is saved as (the state is no NaN, so that it goes through JSON). */
    private static final double NOT_READ = -1e9;

    /** The state as numbers: the format, the count of banks, the month, then each bank's rate, shadow, target and inflation. Nothing is NaN. */
    public double[] toSaveArray() {
        double[] out = new double[HEADER + 4 * MONEYS];
        int p = 0;
        out[p++] = SAVE_FORMAT;
        out[p++] = MONEYS;
        out[p++] = month;
        double[][] columns = {rate, shadow, target, inflation};
        for (double[] column : columns) {
            for (int k = 0; k < MONEYS; k++) out[p++] = Double.isNaN(column[k]) ? NOT_READ : column[k];
        }
        return out;
    }

    /** Puts the state back from toSaveArray; throws IllegalArgumentException, and changes nothing, for a save of another format, another count of banks or another length. */
    public void restore(double[] saved) {
        Objects.requireNonNull(saved, "saved");
        if (saved.length < HEADER) throw new IllegalArgumentException("policy rates: the save is " + saved.length + " numbers long and has no header");
        if (saved[0] != SAVE_FORMAT) throw new IllegalArgumentException("policy rates: the save is format " + saved[0] + ", this reads " + SAVE_FORMAT);
        if (saved[1] != MONEYS) throw new IllegalArgumentException("policy rates: the save holds " + (long) saved[1] + " banks, this has " + MONEYS);
        if (saved.length != HEADER + 4 * MONEYS)
            throw new IllegalArgumentException("policy rates: the save is " + saved.length + " numbers long, this one's is " + (HEADER + 4 * MONEYS));
        if (!(saved[2] >= 0) || saved[2] != Math.rint(saved[2])) throw new IllegalArgumentException("policy rates: the save's month is " + saved[2]);
        for (int i = HEADER; i < saved.length; i++)
            if (!Double.isFinite(saved[i])) throw new IllegalArgumentException("policy rates: the save holds a number that is none at " + i);
        month = (int) saved[2];
        int p = HEADER;
        double[][] columns = {rate, shadow, target, inflation};
        for (double[] column : columns) {
            for (int k = 0; k < MONEYS; k++) {
                double v = saved[p++];
                column[k] = v == NOT_READ ? Double.NaN : v;
            }
        }
    }
}
