package ham.citybuildersim;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.function.IntToDoubleFunction;

/**
 * Gold's price in US dollars a fine troy ounce, a month at a time (batch GD1; spec-gold-money 4.1): the statute's par to March 1933, a ten-month path
 * to the new par, that par to July 1971, then on Historical the USGS's annual price to December 2025, and an AR(1) in gold's real price where the
 * table ends (on Random from August 1971). A pure model: it reads the nations' US price level, the seed and the month, saves nothing, and nothing
 * in the economy reads it yet (GD2 hands it to the vault).
 *
 * WHY THIS EXISTS. The game had no gold anywhere. GD2 to GD4 need its price: the vault's ninth reserve, the gold standard the city can hold its
 * money to, the mines that make it. A price a month is wanted for any year the game can reach, 1900 to 2400, on both paths. It is a pure function of
 * the path, the seed and the month, like WorldMoneys' draws (every draw is new Random(Nations.scramble(seed, who, t, what))), so nothing is saved
 * and a reload continues the path; the walk is remembered only so that a month is not walked twice, and asking 2400 first gives what asking in
 * order gives.
 *
 * THE SOURCES. The Gold Standard Act, 14 March 1900, 31 Stat. 45 (public domain): the dollar of 25.8 grains nine-tenths fine, so 23.22 grains of
 * fine gold. The Gold Reserve Act, 30 January 1934, 48 Stat. 337, and Proclamation 2072, 31 January 1934, 48 Stat. 1730 (public domain): the
 * dollar at 35 to the ounce. The table gold-usd-annual.csv: the USGS's Data Series 140 (1971-2022) and Mineral Commodity Summaries 2026 (2023-2025),
 * public domain / CC0. The innovation's sigma is measured from the World Bank's monthly gold (the Pink Sheet) 1973-2024, 624 changes, and ships as a
 * number only: the series is the LBMA's and is not shipped.
 *
 * Months are Nations' and WorldMoneys' index: t = 0 is January of Nations.FIRST_YEAR (1900).
 */
public final class GoldPrice {

    /* =====================================================================
       THE DIALS - each with its source: (est.) is the game's estimate
       ===================================================================== */

    /** US$ an ounce from 1900: the Gold Standard Act's dollar of 25.8 grains nine-tenths fine is 23.22 grains fine, so 480 grains an ounce over 23.22 (20.6718). */
    public static final double PAR_1900 = 480.0 / 23.22;

    /** US$ an ounce under Proclamation 2072, from 31 January 1934 to 15 August 1971. */
    public static final double PAR_1934 = 35.0;

    /** The first month off the old par: April 1933, when the dollar floated (from October the RFC bought gold at prices raised by steps). */
    public static final int FLOAT_START = (1933 - Nations.FIRST_YEAR) * 12 + 3;

    /** The months of the geometric path from the old par to the new, April 1933 to January 1934 (est.): month FLOAT_START + k - 1 is PAR_1900 x (PAR_1934 / PAR_1900)^(k / FLOAT_STEPS), k = 1..FLOAT_STEPS. */
    public static final int FLOAT_STEPS = 10;

    /** The last month at PAR_1934: July 1971 (the gold window closed 15 August). */
    public static final int LAST_PAR_MONTH = (1971 - Nations.FIRST_YEAR) * 12 + 6;

    /** The first year of the table. */
    public static final int TABLE_FIRST_YEAR = 1971;

    /** The last year of the table, the last of the nations' history (2025's price is the USGS's estimate from January to November). */
    public static final int TABLE_LAST_YEAR = Nations.HISTORY_LAST_YEAR;

    /** Grams in a troy ounce, exact by the 1959 international agreement (a metric ton is 1e6 g over it). */
    public static final double GRAMS_PER_TROY_OUNCE = 31.1034768;

    /** The monthly innovation of ln gold's real price, one sd: 4.749%, measured from the World Bank's monthly gold 1973-2024 (624 changes). */
    public static final double SIGMA = .04749;

    /** The half-life of gold's real price toward its mean in months: ten years (est.: it wanders for decades, and a pull is needed only so a 2400 city has a finite price). */
    public static final double HALF_LIFE_MONTHS = 120;

    /** The monthly persistence of the AR(1): 0.5 to the 1 over the half-life. */
    public static final double PHI = StrictMath.pow(.5, 1.0 / HALF_LIFE_MONTHS);

    /** The years the target is the mean real price of, counted back from the table's last (2006 to 2025). */
    public static final int MEAN_YEARS = 20;

    /** The first month the rule rules on Random: August 1971, the month after the last at PAR_1934. */
    public static final int RANDOM_RULE_START = LAST_PAR_MONTH + 1;

    /** The first month the rule rules on Historical: January 2026, after the table (WorldMoneys' too). */
    public static final int HISTORICAL_RULE_START = WorldMoneys.HISTORICAL_RULE_START;

    /** Where the table is, on the classpath. */
    static final String RESOURCE = "/gold-usd-annual.csv";

    /** The table's header line. */
    static final String HEADER = "year,value,unit,source";

    /** The draws' who: gold sits beside WorldMoneys' world (9_999), below its moneys (10_000 on) and above the nations, regions and goods. */
    private static final long GOLD = 9_998;

    /** The draws' what: no Nations draw (1 to 8) or WorldMoneys draw (100 to 104) is it. */
    private static final int GOLD_DRAW = 300;

    /* =====================================================================
       THE OFFICIAL PATH - the statute's price, the same on both paths
       ===================================================================== */

    /** Whether the statute sets the month's price (January 1900 to July 1971): false before the calendar and after, where official() reads NaN. */
    public static boolean isOfficial(int t) {
        return t >= 0 && t <= LAST_PAR_MONTH;
    }

    /**
     * The statute's price in a month: PAR_1900 to March 1933, the geometric path to PAR_1934 over the next FLOAT_STEPS months, PAR_1934 to July 1971,
     * NaN after (isOfficial says which). The same on both paths. Refused before the calendar's first month.
     */
    public static double official(int t) {
        if (t < 0) throw new IllegalArgumentException("gold price: month " + t + " is before January " + Nations.FIRST_YEAR);
        if (t < FLOAT_START) return PAR_1900;
        if (t < FLOAT_START + FLOAT_STEPS) {
            int k = t - FLOAT_START + 1;
            return PAR_1900 * StrictMath.pow(PAR_1934 / PAR_1900, (double) k / FLOAT_STEPS);
        }
        return t <= LAST_PAR_MONTH ? PAR_1934 : Double.NaN;
    }

    /* =====================================================================
       THE TABLE - the USGS's annual price, read once
       ===================================================================== */

    private static double[] table;

    /** The table, US$ an ounce a year from TABLE_FIRST_YEAR; read the first time it is asked, and refused, naming the line, if it is not whole. */
    private static synchronized double[] table() {
        if (table == null) table = load();
        return table;
    }

    private static double[] load() {
        try (InputStream in = GoldPrice.class.getResourceAsStream(RESOURCE)) {
            if (in == null) throw new IllegalStateException("gold price: " + RESOURCE + " is not on the classpath");
            return readTable(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("gold price: " + RESOURCE + " could not be read", e);
        }
    }

    /**
     * A table's text read: comment lines (#) and blank ones skipped, then the header, then a line for each year TABLE_FIRST_YEAR to TABLE_LAST_YEAR (year,
     * value, unit, source); US$ an ounce a year, index year - TABLE_FIRST_YEAR. The unit usd_per_t is divided by the troy ounces in a ton, usd_per_ozt is
     * taken as it stands. Throws IllegalStateException, naming the line, for a table that is not whole. Package-private for the check.
     */
    static double[] readTable(String text) {
        double[] out = new double[TABLE_LAST_YEAR - TABLE_FIRST_YEAR + 1];
        int number = 0, years = 0;
        boolean header = false;
        String lastWhere = null;
        for (String line : (Iterable<String>) text.lines()::iterator) {
            number++;
            if (line.isBlank() || line.startsWith("#")) continue;
            String where = RESOURCE + " line " + number + " (" + line + ")";
            if (!header) {
                if (!line.trim().equals(HEADER)) throw new IllegalStateException("gold price: " + where + " is not the header " + HEADER);
                header = true;
                continue;
            }
            String[] field = line.split(",", -1);
            if (field.length != 4) throw new IllegalStateException("gold price: " + where + " does not have four fields");
            int year;
            try {
                year = Integer.parseInt(field[0].trim());
            } catch (NumberFormatException e) {
                throw new IllegalStateException("gold price: " + where + " has no year", e);
            }
            if (years == out.length) throw new IllegalStateException("gold price: " + where + " is after " + TABLE_LAST_YEAR + ", the table's last year");
            if (year != TABLE_FIRST_YEAR + years)
                throw new IllegalStateException("gold price: " + where + " is " + year + ", not " + (TABLE_FIRST_YEAR + years) + " (the years run " + TABLE_FIRST_YEAR + " to " + TABLE_LAST_YEAR + " in order, none missing)");
            double value;
            try {
                value = Double.parseDouble(field[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalStateException("gold price: " + where + " has no value", e);
            }
            if (!Double.isFinite(value) || !(value > 0)) throw new IllegalStateException("gold price: " + where + " has a value that is not above zero");
            switch (field[2].trim()) {
                case "usd_per_t" -> out[years] = value / (1e6 / GRAMS_PER_TROY_OUNCE);
                case "usd_per_ozt" -> out[years] = value;
                default -> throw new IllegalStateException("gold price: " + where + " has a unit that is neither usd_per_t nor usd_per_ozt");
            }
            lastWhere = where;
            years++;
        }
        if (!header) throw new IllegalStateException("gold price: " + RESOURCE + " has no header line");
        if (years < out.length)
            throw new IllegalStateException("gold price: " + (lastWhere == null ? RESOURCE + " has no year" : lastWhere + " is the last, and the table ends at "
                    + (TABLE_FIRST_YEAR + years - 1) + " not " + TABLE_LAST_YEAR));
        return out;
    }

    /** The table's price for a year, US$ an ounce: TABLE_FIRST_YEAR to TABLE_LAST_YEAR, refused outside them. */
    public static double annual(int year) {
        if (year < TABLE_FIRST_YEAR || year > TABLE_LAST_YEAR)
            throw new IllegalArgumentException("gold price: the table runs " + TABLE_FIRST_YEAR + " to " + TABLE_LAST_YEAR + ", not " + year);
        return table()[year - TABLE_FIRST_YEAR];
    }

    /** July of a year as a month index: the month the year's average is placed at. */
    private static int july(int year) { return (year - Nations.FIRST_YEAR) * 12 + 6; }

    /** The price a July is placed at: the table's year, but July 1971's is PAR_1934 (the free market's 1971 stays in the table as the record). */
    private static double anchor(int year) { return year == TABLE_FIRST_YEAR ? PAR_1934 : annual(year); }

    /**
     * Historical's price in a month from July 1971 (LAST_PAR_MONTH) to December 2025: the year's average placed at July, the months log-linear between
     * Julys, ln p = ln a(y) + (t - July(y)) / 12 (ln a(y + 1) - ln a(y)); July 1971's anchor is PAR_1934, and the months after July 2025 hold 2025's
     * (est.: there is no July 2026). Refused outside those months. Package-private for the check.
     */
    static double tablePath(int t) {
        if (t < LAST_PAR_MONTH || t >= HISTORICAL_RULE_START)
            throw new IllegalArgumentException("gold price: the table's months run " + LAST_PAR_MONTH + " to " + (HISTORICAL_RULE_START - 1) + ", not " + t);
        if (t >= july(TABLE_LAST_YEAR)) return anchor(TABLE_LAST_YEAR);
        int year = Nations.FIRST_YEAR + (t - 6) / 12;
        int k = t - july(year);
        if (k == 0) return anchor(year);
        double a = StrictMath.log(anchor(year)), b = StrictMath.log(anchor(year + 1));
        return StrictMath.exp(a + k / 12.0 * (b - a));
    }

    /* =====================================================================
       THE RULE - ln of the real price an AR(1) toward the last twenty years' mean, its drift zero (est.)
       ===================================================================== */

    /**
     * The target: the mean real price of the table's last MEAN_YEARS years, in dollars of January 1900 by the data's own prices (the US CPI), the mean
     * of annual(y) x usCpi(1900) / usCpi(y). About US$48.3.
     */
    public static double targetRealPrice() {
        double sum = 0;
        for (int y = TABLE_LAST_YEAR - MEAN_YEARS + 1; y <= TABLE_LAST_YEAR; y++) sum += annual(y) * PriceLevel.usCpi(Nations.FIRST_YEAR) / PriceLevel.usCpi(y);
        return sum / MEAN_YEARS;
    }

    /*
     * The rule: x(t) = ln(real price) - ln TARGET, and at the rule's first month s the state x(s - 1) is the official or the table's price at s - 1 (on Random
     * July 1971's US$35, on Historical December 2025's US$3,300), then x(t) = PHI x(t - 1) + SIGMA gauss(t). The real price is in dollars of January 1900 on the
     * path's own US prices, so nominal(t) = exp(ln TARGET + x(t) + lnUs(t) - lnUs(0)). The drift is zero (est.): the measured real drift is +3.30% a year from
     * 1973 but +0.77% from 1980, since 1980-2000 fell from US$675 to US$283.
     */

    private final Nations.Path path;
    private final long seed;
    private final IntToDoubleFunction usPriceLevel;
    private final boolean historical;
    private final int ruleStart;
    private final double lnTarget, lnUs0;
    /** The walk so far: x[i] is the state of month ruleStart + i, the first {@code walked} of them known. */
    private double[] x = new double[64];
    private int walked;
    /** The state of the month before the rule's first: NaN until asked. */
    private double opening = Double.NaN;

    /** The gold price of a Nations: its path, its seed and its US line (index(Nations.FRONTIER)). */
    public GoldPrice(Nations nations) {
        this(nations.path(), nations.seed(), usLine(nations));
    }

    /** The gold price of a path and a seed on a US price level a year, in any base: the harness's fixture with no world file. */
    GoldPrice(Nations.Path path, long seed, IntToDoubleFunction usPriceLevel) {
        this.path = Objects.requireNonNull(path, "gold price: no path");
        this.seed = seed;
        this.usPriceLevel = Objects.requireNonNull(usPriceLevel, "gold price: no US price level");
        historical = path == Nations.Path.HISTORICAL;
        ruleStart = historical ? HISTORICAL_RULE_START : RANDOM_RULE_START;
        lnTarget = StrictMath.log(targetRealPrice());
        lnUs0 = lnUs(0);
    }

    private static IntToDoubleFunction usLine(Nations nations) {
        int usa = nations.index(Nations.FRONTIER);
        if (usa < 1) throw new IllegalArgumentException("gold price: no " + Nations.FRONTIER + " among the nations");
        return year -> nations.year(usa, year).priceLevel();
    }

    /** ln of the path's US price level in month t, log-linear between Januaries as WorldMoneys' is: ln P(t) = ln P(y) + (t mod 12) / 12 (ln P(y + 1) - ln P(y)). */
    private double lnUs(int t) {
        int year = Nations.FIRST_YEAR + t / 12;
        double a = level(year), b = level(year + 1);
        return a + (t % 12) / 12.0 * (b - a);
    }

    private double level(int year) {
        double p = usPriceLevel.applyAsDouble(year);
        if (!(p > 0) || !Double.isFinite(p)) throw new IllegalStateException("gold price: the US price level of " + year + " is " + p);
        return StrictMath.log(p);
    }

    private double gauss(int t) { return new Random(Nations.scramble(seed, GOLD, t, GOLD_DRAW)).nextGaussian(); }

    /** The state x of a month from the one before the rule's first on, walking only the months not yet walked. */
    private double state(int t) {
        if (t == ruleStart - 1) {
            if (Double.isNaN(opening)) {
                double nominal = historical ? tablePath(t) : official(t);
                opening = StrictMath.log(nominal) - (lnUs(t) - lnUs0) - lnTarget;
            }
            return opening;
        }
        int i = t - ruleStart;
        while (walked <= i) {
            if (walked == x.length) x = Arrays.copyOf(x, x.length * 2);
            x[walked] = PHI * (walked == 0 ? state(ruleStart - 1) : x[walked - 1]) + SIGMA * gauss(ruleStart + walked);
            walked++;
        }
        return x[i];
    }

    /* =====================================================================
       WHAT A CALLER READS
       ===================================================================== */

    /**
     * Gold's price in a month, US$ a fine troy ounce: the statute's to July 1971 on both paths; then on Random the rule, on Historical the table to
     * December 2025 and the rule from January 2026. Refused before the calendar's first month.
     */
    public synchronized double usdPerOunce(int t) {
        if (t < 0) throw new IllegalArgumentException("gold price: month " + t + " is before January " + Nations.FIRST_YEAR);
        if (isOfficial(t)) return official(t);
        if (historical && t < HISTORICAL_RULE_START) return tablePath(t);
        return StrictMath.exp(lnTarget + state(t) + lnUs(t) - lnUs0);
    }

    /** Gold's price in a month in dollars of January 1900: usdPerOunce over the path's US price level relative to the calendar's first January. */
    public synchronized double realPrice(int t) {
        return usdPerOunce(t) / StrictMath.exp(lnUs(t) - lnUs0);
    }

    /** The first month the rule rules on this path. */
    public int ruleStart() { return ruleStart; }

    /** The path this price is on. */
    public Nations.Path path() { return path; }

    /** The seed of its draws. */
    public long seed() { return seed; }
}
