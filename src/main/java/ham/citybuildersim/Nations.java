package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * The nations beyond the city, each on the world's path: its year - output,
 * people, prices and its money's rate to the US dollar - from the path
 * (Historical or Random), the seed and the world file (WN1; spec-nations 7).
 *
 * WHY THIS EXISTS. The world outside was a price list fixed at founding, and
 * the only thing out there that moved, the dollar's inflation, was drawn from
 * the month alone, so every city met the same world (spec-nations 1.4).
 * Jerus: "perhaps a seed for the path? one being historical and the other
 * being random". Now the world has a path, picked at founding (D9).
 *
 * A NATION'S YEAR IS A PURE FUNCTION OF (path, seed, year) (spec 7.3): every
 * draw is new Random(scramble(seed, nation, year, what)), so nothing about the
 * nations is saved but the path and the seed, and asking 2150 before 2030
 * gives what asking in order gives. The path is worked year by year for all
 * the nations at once - the frontier, the world's factor and the moneys'
 * anchors are shared - eagerly to 2100 (the UN's last year) and on demand past
 * it.
 *
 * HISTORICAL replays the file's real years from January 2000, each series to
 * its last published year, and then the game's own rule with seeded shocks of
 * HISTORICAL_SHOCK_SHARE the size and no crises. RANDOM starts from the file's
 * January 2000 and draws its own path. The rule is the same in both: growth a
 * head in logs (so output is never negative) is a trend that falls as a nation
 * nears the frontier, a world factor, its region's, its own persistent shock
 * and now and then a crisis; its prices are drawn around its income group's
 * inflation; its money follows relative parity plus a shock, or its peg.
 * Past 2100 a Historical nation's growth eases to LONG_RUN_GROWTH (the
 * leaderboard's D8; the nations study's D14) and people are held.
 *
 * The map draws it (WN2) and the world market trades on it (WN3). It copies
 * what it needs from the file and keeps no reference to it.
 */
public final class Nations {

    /** The world's path, picked at founding (D9): the real years from January 2000, or a path drawn from the seed. */
    public enum Path { HISTORICAL, RANDOM }

    /**
     * The path a founding takes when nobody picks one, on the Earth and on a generated world alike: Random (WN3;
     * Jerus, 2026-10-29: "A new world defaults to the Random path instead of Historical").
     * WHY: on the world market a young city on Historical meets the 2000s commodity boom as imported inflation
     * (the default playtest's prices 4.38x by 2012, its treasury dry in 2008, ~5,000 people for 60 years; WN3-B),
     * and most new players found on the Earth. Historical stays the founding screen's first chip on both pages.
     */
    public static final Path DEFAULT_PATH = Path.RANDOM;

    /** The path's seed when nobody rolls one: 48,213, the founding board's (spec-nations, board 4), so every harness and the playtest meet one world. */
    public static final long DEFAULT_SEED = 48_213L;

    /* =====================================================================
       THE DIALS - each with its source: [R..] is the spec's list, (est.) the game's estimate
       ===================================================================== */

    /** The year a path begins in, January of it: the city's calendar's (CityCalendar.EPOCH_YEAR). */
    public static final int FIRST_YEAR = CityCalendar.EPOCH_YEAR;

    /** The last year the file's history runs to (output, prices, rates): Historical replays it to here at most, and the game's own rule goes on [R11]. */
    public static final int HISTORY_LAST_YEAR = 2025;

    /** The last year of the UN's people, the medium variant [R13]; past it people are held and a Historical nation's growth eases. */
    public static final int PEOPLE_LAST_YEAR = 2100;

    /** The nation whose output a head is the frontier the others converge to: the United States [R11]. */
    public static final String FRONTIER = "USA";

    /** The frontier's growth a head a year (est.: the US's grew about 1.3% a year 2000-2024 [R11], about 2% over the long run). */
    public static final double FRONTIER_GROWTH = .015;

    /** The share of the gap to the frontier (in logs) a nation closes a year: Barro and Sala-i-Martin's 2% [R29]. */
    public static final double CONVERGENCE = .02;

    /** The most the catch-up adds to the trend (est.: 2% of the whole gap would give the poorest about 10% a year; the typical developing nation grew about two points faster than the US in 2000-2019 [R11]). */
    public static final double CATCH_UP_MAX = .02;

    /** One standard deviation of a rich nation's yearly growth: the G7's, 2000-2019 [R11]. */
    public static final double SD_RICH = .018;

    /** One standard deviation of an emerging economy's yearly growth: fifteen large ones, 2000-2019 [R11]. */
    public static final double SD_EMERGING = .027;

    /** The gap to the frontier, in logs, at which a nation swings like an emerging economy (est.: the G7's output a head is about 5.5 times those fifteen's). */
    public static final double EMERGING_GAP = 1.7;

    /** The most a year's whole swing is, one standard deviation, however poor the nation (est.). */
    public static final double SD_MAX = .045;

    /** The share of a rich nation's output swings the world factor explains: 27% in the industrial nations, about 11% across 106 [R30]. */
    public static final double WORLD_SHARE_RICH = .27;

    /** The world factor's size, one standard deviation: that share of a rich nation's variance, so a fixed size that is 12% of an emerging one's [R30]. */
    public static final double WORLD_SD = SD_RICH * Math.sqrt(WORLD_SHARE_RICH);

    /** The share of an emerging economy's variance a region's factor is (est., after [R30]). */
    public static final double REGION_SHARE = .10;

    /** A region's factor's size, one standard deviation, drawn per region and year. */
    public static final double REGION_SD = SD_EMERGING * Math.sqrt(REGION_SHARE);

    /** The share of this year's own shock that carries into the next: 0.3 (spec-nations 7.2). */
    public static final double OWN_PERSISTENCE = .3;

    /** The chance a year a nation not in a crisis starts one: Barro's falls of 15% or more in output a head, 1.7% of country-years over a century [R32]. */
    public static final double CRISIS_RATE = .017;

    /** The shallowest a crisis is, the share of output a head lost over its years: Barro's threshold [R32]. */
    public static final double CRISIS_DEPTH_MIN = .15;

    /** The deepest a crisis is (est.: Barro and Ursua's mean fall about 22%). */
    public static final double CRISIS_DEPTH_MAX = .30;

    /** The years a crisis runs, the fall spread evenly over them (est.: "over a few years" [R32]). */
    public static final int CRISIS_YEARS = 3;

    /** The size of a Historical nation's shocks after its data, against Random's: world, region and own (est.: spec 7.1, "sized as Random's but smaller"); it draws no crisis. */
    public static final double HISTORICAL_SHOCK_SHARE = .5;

    /** The growth a head a Historical nation eases to past 2100 (the leaderboard's D8; the nations study's D14: about what the world has sustained over the long run). */
    public static final double LONG_RUN_GROWTH = .01;

    /** The years past 2100 a Historical nation's growth takes to ease from its 2091-2100 average to LONG_RUN_GROWTH. */
    public static final int EASE_YEARS = 50;

    /** The years of growth a head, to 2100, the ease starts from (the leaderboard's D8). */
    public static final int LONG_RUN_WINDOW = 10;

    /** The share of the frontier's output a head a nation with no output figure at all starts from (est.). */
    public static final double NO_DATA_SHARE = .1;

    /** A nation below this output a head (US$ of 2015) is low income (est.: the World Bank's FY2026 US$1,135 of 2024 over the US GDP deflator since 2015). */
    public static final double LOW_INCOME_MAX = 890;

    /** A nation below this is lower middle income (est.: the World Bank's FY2026 US$4,495 of 2024, deflated to 2015). */
    public static final double LOWER_MIDDLE_MAX = 3_510;

    /** A nation below this is upper middle income, above it high (est.: the World Bank's FY2026 US$13,935 of 2024, deflated to 2015). */
    public static final double UPPER_MIDDLE_MAX = 10_890;

    /** A year's mean inflation by income group, high, upper middle, lower middle, low (est., after the WDI's medians 2000-2019 [R11]). */
    static final double[] INFLATION_MEAN = {.02, .035, .055, .065};

    /** One standard deviation of a year's inflation innovation by income group, in the same order (est., after the WDI's medians 2000-2019 [R11]). */
    static final double[] INFLATION_SD = {.012, .025, .035, .045};

    /** The share of this year's inflation, above or below its group's mean, that carries into the next (est., after [R11]). */
    public static final double INFLATION_PERSISTENCE = .6;

    /** The lowest a drawn year's inflation is, a shade of deflation (est.); a published year is the file's whatever it was. */
    public static final double INFLATION_FLOOR = -.05;

    /** The US dollar's code: its rate is 1 always, and a money's rate is against it. */
    public static final String DOLLAR = "USD";

    /** A floating money's yearly change against the dollar beyond relative parity, one standard deviation (est.). */
    public static final double RATE_SD = .10;

    /** The share of a good's price shock that carries into the next year: 0.7 (spec-nations 7.2). */
    public static final double PRICE_PERSISTENCE = .7;

    /** The least a good's yearly price swing, one standard deviation of its log change, is taken to be (est.: manufactured goods move little). */
    public static final double MIN_PRICE_SWING = .03;

    /** The region of a nation the file gives none (Taiwan, which WDI lacks): East Asia & Pacific's. */
    public static final String DEFAULT_REGION = "East Asia & Pacific";

    /* The kinds of draw, and where a draw that belongs to no nation sits among the nations' numbers. */
    private static final int WORLD = 1, REGION = 2, OWN = 3, CRISIS = 4, CRISIS_DEPTH = 5, INFLATION = 6, RATE = 7, PRICE = 8;
    private static final int WORLD_NATION = 0, REGION_BASE = 1000, GOOD_BASE = 2000;

    /* =====================================================================
       THE RECORDS
       ===================================================================== */

    /**
     * A nation's year. output is real, US$ of 2015 (the file's gdp_2015usd); people are persons; priceLevel
     * is the file's cpi, 2015 = 100; inflation is that year's, a share (NaN in 2000, the first year); rateToUsd is the
     * nation's money a US dollar, year average, and money the ISO code of the money it is in; the marks are the file's
     * (3 published, 2 a published projection, 1 the game's estimate): every year the rule makes is 1.
     */
    public record Year(int year, double output, double people, double priceLevel, double inflation,
                       double rateToUsd, String money, int outputMark, int peopleMark) {
        /** Output a head, US$ of 2015. */
        public double outputPerHead() { return output / people; }
    }

    /** The terms of a year's growth a head the path drew, in logs (0 where it draws none): the world's, the region's, the nation's own (the persistent state, not its innovation), the crisis's share of the year, and whether a crisis began this year. */
    record Draws(double world, double region, double own, double crisis, boolean crisisStarts) { }

    /** Says a figure of the file is not there, for a harness that must cause a missing figure: the series ("people", "gdp_2015usd", "cpi" or "rate"), the nation's iso3 and the year. */
    interface Absent { boolean is(String series, String iso3, int year); }

    private static final Draws NO_DRAWS = new Draws(0, 0, 0, 0, false);

    /** A series by year index from 2000 with no holes up to last: the file's figures, the gaps and the years before the first filled in. */
    record Series(double[] v, int[] mark, int last) { }

    /* =====================================================================
       THE NATIONS - copied from the file when this is made
       ===================================================================== */

    private final Path path;
    private final long seed;
    private final boolean historical;
    private final int n;
    private final String[] iso, names, regime, regionName, moneyLater;
    private final String[][] moneyDec;
    private final int[] regionOf;
    private final List<String> regions = new ArrayList<>();
    private final int frontier;
    private final Series[] people, output, cpi, rate;
    private final int[] outLast, cpiLast, rateLast;
    private final Map<String, Integer> anchor = new LinkedHashMap<>();
    private final String[] goodKey;
    private final Series[] price;
    private final int[] priceLast;
    private final double[] swing;

    /**
     * Whose shock each good's price draws after the file's years, by good number: the first good that follows its Pink Sheet series (WN3).
     * WHY: each good drew its own, so a chain on one series - anode, cathode and rod on copper; crude's six fuels; ore and steel - drifted apart
     * after 2025, and on Random from 2000, and a refinery bought anode dearer than its cathode (the playtest's GDP went below zero, months 422-637).
     */
    private final int[] shockOf;

    /* THE PATH SO FAR, a year at a time: the records, what was drawn, each money's rate and each good's price. */
    private final List<Year[]> years = new ArrayList<>();
    private final List<Draws[]> drawn = new ArrayList<>();
    private final List<Map<String, Double>> moneyLevels = new ArrayList<>();
    private final List<double[]> goodPrices = new ArrayList<>();
    private final List<int[]> goodMarks = new ArrayList<>();
    private double[] perHead, priceLevel, inflationState, own, crisisAmount, longRun;
    private int[] crisisLeft;

    /** The nations on this path and seed, from the file. */
    public Nations(WorldData file, Path path, long seed) {
        this(file, path, seed, null, null);
    }

    /** As above, but regimes (iso3 to float, peg:CODE or uses:CODE) stand in for the file's: a harness's, the fixture having no peg to cause. */
    Nations(WorldData file, Path path, long seed, Map<String, String> regimes) {
        this(file, path, seed, regimes, null);
    }

    Nations(WorldData file, Path path, long seed, Map<String, String> regimes, Absent absent) {
        this.path = Objects.requireNonNull(path, "path");
        this.seed = seed;
        this.historical = path == Path.HISTORICAL;
        Absent gone = absent == null ? (series, code, year) -> false : absent;
        if (file.pathFirstYear() != FIRST_YEAR || file.pathYears() != PEOPLE_LAST_YEAR - FIRST_YEAR + 1)
            throw new IllegalArgumentException("nations: the file's paths run " + file.pathFirstYear() + " for " + file.pathYears() + " years, and this reads " + FIRST_YEAR + " to " + PEOPLE_LAST_YEAR);
        n = file.nationCount();
        int dataLast = HISTORY_LAST_YEAR - FIRST_YEAR;
        iso = new String[n];
        names = new String[n];
        regime = new String[n];
        regionName = new String[n];
        regionOf = new int[n];
        moneyLater = new String[n];
        moneyDec = new String[n][dataLast + 1];
        people = new Series[n];
        output = new Series[n];
        cpi = new Series[n];
        rate = new Series[n];
        outLast = new int[n];
        cpiLast = new int[n];
        rateLast = new int[n];
        double[] size = new double[n];
        int front = -1;
        for (int i = 0; i < n; i++) {
            WorldData.Nation nation = file.nation(i + 1);
            iso[i] = nation.iso3();
            names[i] = nation.name();
            if (FRONTIER.equals(iso[i])) front = i;
            String r = regimes != null && regimes.containsKey(iso[i]) ? regimes.get(iso[i]) : nation.regime();
            regime[i] = r == null ? "float" : r;
            regionName[i] = nation.region() == null ? DEFAULT_REGION : nation.region();
            if (!regions.contains(regionName[i])) regions.add(regionName[i]);
            regionOf[i] = regions.indexOf(regionName[i]);
            String later = nation.money() == null ? file.moneyAt(i + 1, 11) : nation.money();
            for (int t = 0; t <= dataLast; t++) {
                String code = file.moneyAt(i + 1, t * 12 + 11);
                moneyDec[i][t] = code == null ? later : code;
            }
            // After the history: Historical's is the 2025 money; Random keeps its money of 2000 unless its regime uses another's.
            moneyLater[i] = historical ? later : regime[i].startsWith("uses:") ? regime[i].substring(5) : moneyDec[i][0];
            double out2025 = rawFigure(file, "gdp_2015usd", i + 1, HISTORY_LAST_YEAR, gone);
            size[i] = Double.isNaN(out2025) ? -1 : out2025;
            people[i] = fromFile(file, "people", i + 1, PEOPLE_LAST_YEAR - FIRST_YEAR, gone);
            if (people[i] == null) throw new IllegalArgumentException("nations: the file has no people for " + iso[i]);
        }
        if (front < 0) throw new IllegalArgumentException("nations: the file has no " + FRONTIER + ", the frontier");
        frontier = front;
        output[frontier] = fromFile(file, "gdp_2015usd", frontier + 1, dataLast, gone);
        if (output[frontier] == null) throw new IllegalArgumentException("nations: the file has no output for " + FRONTIER + ", the frontier");
        double frontierStart = output[frontier].v[0] / people[frontier].v[0];
        for (int i = 0; i < n; i++) {
            // A series with no figure at all starts from a stand-in, marked 1, and the rule works it from 2001 in both paths.
            if (output[i] == null) output[i] = fromFile(file, "gdp_2015usd", i + 1, dataLast, gone);
            if (output[i] == null) output[i] = standIn(frontierStart * people[i].v[0] * NO_DATA_SHARE);
            cpi[i] = fromFile(file, "cpi", i + 1, dataLast, gone);
            if (cpi[i] == null) cpi[i] = standIn(100);
            rate[i] = fromFile(file, "rate", i + 1, dataLast, gone);
        }
        // No rate at all: 1 for a dollar, else another nation's figure for the same money in 2000, else 1.
        Series[] fileRate = rate.clone();
        for (int i = 0; i < n; i++) {
            if (fileRate[i] != null) continue;
            double v = 1;
            for (int j = 0; j < n && !DOLLAR.equals(moneyDec[i][0]); j++)
                if (j != i && fileRate[j] != null && moneyDec[j][0].equals(moneyDec[i][0])) { v = fileRate[j].v[0]; break; }
            rate[i] = standIn(v);
        }
        for (int i = 0; i < n; i++) {
            outLast[i] = historical ? output[i].last : 0;
            cpiLast[i] = historical ? cpi[i].last : 0;
            rateLast[i] = historical ? rate[i].last : 0;
        }
        // A money's anchor: the nation of largest 2025 output using it whose regime is not uses:, else the largest that does; a money no nation uses has none.
        Set<String> decided = new HashSet<>();
        for (int pass = 0; pass < 2; pass++) {
            for (int i = 0; i < n; i++) {
                if (regime[i].startsWith("uses:") != (pass == 1) || pass == 1 && decided.contains(moneyLater[i])) continue;
                Integer a = anchor.get(moneyLater[i]);
                if (a == null || size[i] > size[a]) anchor.put(moneyLater[i], i);
            }
            decided.addAll(anchor.keySet());
        }
        int goods = file.hasPrices() ? file.goodCount() : 0;
        if (goods > 0 && file.priceFirstYear() != FIRST_YEAR) throw new IllegalArgumentException("nations: the prices begin in " + file.priceFirstYear());
        goodKey = new String[goods];
        price = new Series[goods];
        priceLast = new int[goods];
        swing = new double[goods];
        shockOf = new int[goods];
        for (int g = 0; g < goods; g++) {
            goodKey[g] = file.good(g + 1).key();
            int cap = file.priceYears() - 1;
            double[] raw = new double[file.priceYears()];
            int[] mark = new int[raw.length];
            for (int k = 0; k < raw.length; k++) {
                raw[k] = file.price(g + 1, FIRST_YEAR + k);
                mark[k] = file.priceMark(g + 1, FIRST_YEAR + k);
            }
            price[g] = filled(raw, mark, cap);
            if (price[g] == null) {
                double[] ones = new double[cap + 1];
                Arrays.fill(ones, 1);
                int[] marks = new int[cap + 1];
                Arrays.fill(marks, WorldData.MARK_ESTIMATE);
                price[g] = new Series(ones, marks, cap);
            }
            priceLast[g] = historical ? price[g].last : 0;
            swing[g] = swingOf(price[g]);
            // A good draws its series' shock, not its own (WN3): the first good that follows the same Pink Sheet series draws it for all of them.
            String series = file.priceSeries(g + 1);
            shockOf[g] = g + 1;
            if (series != null && !series.isBlank()) {
                for (int e = 0; e < g; e++) {
                    if (series.equals(file.priceSeries(e + 1))) { shockOf[g] = shockOf[e]; break; }
                }
            }
        }
        perHead = new double[n];
        priceLevel = new double[n];
        inflationState = new double[n];
        own = new double[n];
        crisisAmount = new double[n];
        longRun = new double[n];
        crisisLeft = new int[n];
        ensure(PEOPLE_LAST_YEAR);
    }

    /* =====================================================================
       WHAT A CALLER ASKS
       ===================================================================== */

    /** The path this world is on. */
    public Path path() { return path; }

    /** The seed that draws it. */
    public long seed() { return seed; }

    /** How many nations there are (the file's N: 196 in the full file). */
    public int count() { return n; }

    /** A nation's ISO 3166 alpha-3 code; nations are numbered 1..count() as WorldData numbers them. */
    public String iso3(int nation) { return iso[check(nation)]; }

    /** A nation's name. */
    public String name(int nation) { return names[check(nation)]; }

    /** The number of the nation with that code, 1..count(); -1 when there is none. */
    public int index(String iso3) {
        for (int i = 0; i < n; i++) if (iso[i].equals(iso3)) return i + 1;
        return -1;
    }

    /** A nation's year, for any year from FIRST_YEAR on: a pure function of the path, the seed and the year. */
    public synchronized Year year(int nation, int year) {
        int i = check(nation);
        return years.get(reach(year))[i];
    }

    /** A nation's growth of output in a year, in logs; NaN in FIRST_YEAR, which has no year before it. */
    public synchronized double growth(int nation, int year) {
        int i = check(nation), t = reach(year);
        return t == 0 ? Double.NaN : Math.log(years.get(t)[i].output() / years.get(t - 1)[i].output());
    }

    /** How many traded goods have a price path (the file's prices layer; 0 for a file without it). */
    public int goodCount() { return goodKey.length; }

    /** The key of good 1..goodCount() (the file's goods layer: the Good constant's name). */
    public String goodKey(int good) { return goodKey[checkGood(good)]; }

    /** A good's real price in a year, a ratio to 2025's (so 2025 is 1 on Historical): the file's to 2025 on Historical, the path's own AR(1) around 1 after it and on Random. */
    public synchronized double goodPrice(int good, int year) {
        int g = checkGood(good);
        return goodPrices.get(reach(year))[g];
    }

    /** The mark of that price: the file's where it is the file's, else 1. */
    public synchronized int goodPriceMark(int good, int year) {
        int g = checkGood(good);
        return goodMarks.get(reach(year))[g];
    }

    /** The year of the city's calendar a game month is in (CityCalendar.yearOf), so a caller asks for the city's year. */
    public static int yearOf(int gameMonth) { return CityCalendar.yearOf(gameMonth); }

    /** What the year drew for a nation, for the harness. */
    synchronized Draws draws(int nation, int year) {
        int i = check(nation);
        return drawn.get(reach(year))[i];
    }

    /** Whether a nation is in a crisis in a year (the year it starts included). */
    synchronized boolean inCrisis(int nation, int year) { return draws(nation, year).crisis() != 0; }

    /** A nation's region, the World Bank's (the file's; East Asia & Pacific for the one it gives none). */
    String region(int nation) { return regionName[check(nation)]; }

    /** A good's yearly price swing, one standard deviation of its file's log change 2001-2025, never under MIN_PRICE_SWING. */
    double priceSwing(int good) { return swing[checkGood(good)]; }

    /** The income group of a nation by its output a head in US$ of 2015: 0 high, 1 upper middle, 2 lower middle, 3 low. */
    static int incomeGroup(double outputPerHead) {
        return outputPerHead >= UPPER_MIDDLE_MAX ? 0 : outputPerHead >= LOWER_MIDDLE_MAX ? 1 : outputPerHead >= LOW_INCOME_MAX ? 2 : 3;
    }

    private int check(int nation) {
        if (nation < 1 || nation > n) throw new IllegalArgumentException("nations: no nation numbered " + nation);
        return nation - 1;
    }

    private int checkGood(int good) {
        if (good < 1 || good > goodKey.length) throw new IllegalArgumentException("nations: no good numbered " + good);
        return good - 1;
    }

    /** The year's index, the path worked out to it. */
    private int reach(int year) {
        if (year < FIRST_YEAR) throw new IllegalArgumentException("nations: no year " + year + ", the path begins in " + FIRST_YEAR);
        ensure(year);
        return year - FIRST_YEAR;
    }

    private void ensure(int year) {
        while (years.size() <= year - FIRST_YEAR) step(years.size());
    }

    /* =====================================================================
       THE FILE'S FIGURES - gaps filled, the years before the first, the stand-ins
       ===================================================================== */

    /** The file's figure for a nation and year, NaN where it has none or the harness says it is absent. */
    private static double rawFigure(WorldData file, String series, int number, int year, Absent gone) {
        double x = gone.is(series, file.nation(number).iso3(), year) ? Double.NaN : file.pathValue(series, number, year);
        return x > 0 && Double.isFinite(x) ? x : Double.NaN;
    }

    /** A series of the file for a nation, indexed from 2000 and filled to its last figure at or before cap; null when it has none. */
    private static Series fromFile(WorldData file, String series, int number, int cap, Absent gone) {
        double[] raw = new double[file.pathYears()];
        int[] mark = new int[raw.length];
        for (int i = 0; i < raw.length; i++) {
            raw[i] = rawFigure(file, series, number, FIRST_YEAR + i, gone);
            mark[i] = Math.max(WorldData.MARK_ESTIMATE, file.pathMark(series, number, FIRST_YEAR + i));
        }
        return filled(raw, mark, cap);
    }

    /**
     * A series from its figures, a NaN being none: the figures up to cap kept with their marks, the years before the first
     * taking the first figure and a gap between two figures filled log-linearly between them, both marked 1; ends at the last
     * figure at or before cap (the year the file's history ends and the rule begins); null when there is none.
     */
    static Series filled(double[] raw, int[] mark, int cap) {
        int first = -1, last = -1;
        for (int i = 0; i <= cap && i < raw.length; i++) {
            if (raw[i] > 0 && Double.isFinite(raw[i])) {
                if (first < 0) first = i;
                last = i;
            }
        }
        if (first < 0) return null;
        double[] v = new double[last + 1];
        int[] k = new int[last + 1];
        for (int i = 0; i <= last; i++) {
            if (i < first) {
                v[i] = raw[first];
                k[i] = WorldData.MARK_ESTIMATE;
            } else if (raw[i] > 0 && Double.isFinite(raw[i])) {
                v[i] = raw[i];
                k[i] = mark[i];
            } else {
                int lo = i - 1, hi = i + 1;
                while (!(raw[lo] > 0 && Double.isFinite(raw[lo]))) lo--;
                while (!(raw[hi] > 0 && Double.isFinite(raw[hi]))) hi++;
                double f = (i - lo) / (double) (hi - lo);
                v[i] = Math.exp(Math.log(raw[lo]) * (1 - f) + Math.log(raw[hi]) * f);
                k[i] = WorldData.MARK_ESTIMATE;
            }
        }
        return new Series(v, k, last);
    }

    /** A series that is one figure, the game's estimate, in 2000: the rule works the rest. */
    private static Series standIn(double value) {
        return new Series(new double[] {value}, new int[] {WorldData.MARK_ESTIMATE}, 0);
    }

    /** One standard deviation of a good's yearly log change over its file's years 2001 on, never under MIN_PRICE_SWING. */
    private static double swingOf(Series s) {
        int count = s.last;
        if (count < 2) return MIN_PRICE_SWING;
        double mean = Math.log(s.v[s.last] / s.v[0]) / count, sum = 0;
        for (int i = 1; i <= s.last; i++) sum += Math.pow(Math.log(s.v[i] / s.v[i - 1]) - mean, 2);
        return Math.max(MIN_PRICE_SWING, Math.sqrt(sum / (count - 1)));
    }

    /* =====================================================================
       THE DRAWS
       ===================================================================== */

    /** A draw's seed: the path's seed, the nation (a region's, a good's, 0 for the world's), the year and what is drawn, chained through SplitMix64. */
    static long scramble(long seed, long nation, long year, long what) {
        long h = World.mix(seed);
        h = World.mix(h + nation);
        h = World.mix(h + year);
        return World.mix(h + what);
    }

    private double gauss(int what, long nation, int year) { return new Random(scramble(seed, nation, year, what)).nextGaussian(); }

    private double unit(int what, long nation, int year) { return new Random(scramble(seed, nation, year, what)).nextDouble(); }

    /* =====================================================================
       THE YEAR - people, output, prices, moneys, goods, in that order
       ===================================================================== */

    private void step(int t) {
        int year = FIRST_YEAR + t;
        int longT = PEOPLE_LAST_YEAR - FIRST_YEAR;
        double share = historical ? HISTORICAL_SHOCK_SHARE : 1.0;
        if (historical && t == longT + 1) {
            for (int i = 0; i < n; i++) longRun[i] = Math.log(years.get(longT)[i].outputPerHead() / years.get(longT - LONG_RUN_WINDOW)[i].outputPerHead()) / LONG_RUN_WINDOW;
        }
        double worldTerm = t == 0 ? 0 : share * WORLD_SD * gauss(WORLD, WORLD_NATION, year);
        double[] regionTerm = new double[regions.size()];
        for (int r = 0; t > 0 && r < regionTerm.length; r++) regionTerm[r] = share * REGION_SD * gauss(REGION, REGION_BASE + r, year);
        double frontierPerHead = perHead[frontier];
        double[] outNow = new double[n], peopleNow = new double[n], plNow = new double[n], inflNow = new double[n], perHeadNow = new double[n];
        int[] outMark = new int[n], peopleMark = new int[n];
        Draws[] drow = new Draws[n];
        for (int i = 0; i < n; i++) {
            int num = i + 1;
            // People are the file's to its last year (2100) and held after it.
            peopleNow[i] = people[i].v[Math.min(t, people[i].last)];
            peopleMark[i] = t <= people[i].last ? people[i].mark[t] : WorldData.MARK_ESTIMATE;
            double gap = t == 0 ? 0 : Math.max(0, Math.log(frontierPerHead / perHead[i]));
            if (t <= outLast[i]) {
                outNow[i] = output[i].v[t];
                outMark[i] = output[i].mark[t];
                perHeadNow[i] = outNow[i] / peopleNow[i];
                drow[i] = NO_DRAWS;
            } else {
                // Growth a head, in logs: the trend, the world's factor, the region's, its own shock and a crisis.
                double trend = historical && t > longT
                        ? longRun[i] + (LONG_RUN_GROWTH - longRun[i]) * Math.min(1.0, (t - longT) / (double) EASE_YEARS)
                        : FRONTIER_GROWTH + Math.min(CATCH_UP_MAX, CONVERGENCE * gap);
                double sd = Math.min(SD_MAX, SD_RICH + (SD_EMERGING - SD_RICH) * gap / EMERGING_GAP);
                double sdOwn = Math.sqrt(Math.max(0, sd * sd - WORLD_SD * WORLD_SD - REGION_SD * REGION_SD) * (1 - OWN_PERSISTENCE * OWN_PERSISTENCE));
                own[i] = OWN_PERSISTENCE * own[i] + share * sdOwn * gauss(OWN, num, year);
                double crisis = 0;
                boolean starts = false;
                if (!historical) {
                    if (crisisLeft[i] > 0) {
                        crisis = crisisAmount[i];
                        crisisLeft[i]--;
                    } else if (unit(CRISIS, num, year) < CRISIS_RATE) {
                        double depth = CRISIS_DEPTH_MIN + (CRISIS_DEPTH_MAX - CRISIS_DEPTH_MIN) * unit(CRISIS_DEPTH, num, year);
                        crisisAmount[i] = Math.log(1 - depth) / CRISIS_YEARS;
                        crisis = crisisAmount[i];
                        crisisLeft[i] = CRISIS_YEARS - 1;
                        starts = true;
                    }
                }
                Draws d = new Draws(worldTerm, regionTerm[regionOf[i]], own[i], crisis, starts);
                drow[i] = d;
                outMark[i] = WorldData.MARK_ESTIMATE;
                perHeadNow[i] = perHead[i] * Math.exp(trend + d.world() + d.region() + d.own() + d.crisis());
                outNow[i] = perHeadNow[i] * peopleNow[i];
            }
            // Prices: a published year is the file's; after it, the income group's mean plus a persistent deviation.
            int group = incomeGroup(perHead[i]);
            if (t <= cpiLast[i]) {
                plNow[i] = cpi[i].v[t];
                inflNow[i] = t == 0 ? Double.NaN : plNow[i] / priceLevel[i] - 1;
                if (t == cpiLast[i]) inflationState[i] = t == 0 ? 0 : inflNow[i] - INFLATION_MEAN[group];
            } else {
                inflationState[i] = INFLATION_PERSISTENCE * inflationState[i] + share * INFLATION_SD[group] * gauss(INFLATION, num, year);
                inflNow[i] = Math.max(INFLATION_FLOOR, INFLATION_MEAN[group] + inflationState[i]);
                plNow[i] = priceLevel[i] * (1 + inflNow[i]);
            }
        }
        // The moneys: each money's rate this year, then each nation's.
        Map<String, Double> cur = new HashMap<>();
        Set<String> busy = new HashSet<>();
        moneyLevel(DOLLAR, t, year, share, cur, busy, inflNow);
        for (String code : anchor.keySet()) moneyLevel(code, t, year, share, cur, busy, inflNow);
        Year[] row = new Year[n];
        for (int i = 0; i < n; i++) {
            String money = moneyOf(i, t);
            double rateNow;
            if (t <= rateLast[i]) rateNow = rate[i].v[t];
            else {
                double level = moneyLevel(money, t, year, share, cur, busy, inflNow);
                rateNow = level > 0 ? level : years.get(t - 1)[i].rateToUsd();
            }
            row[i] = new Year(year, outNow[i], peopleNow[i], plNow[i], inflNow[i], rateNow, money, outMark[i], peopleMark[i]);
        }
        // The goods: the file's real price to its last year, then an AR(1) round 1 (2025's price, the game's literal).
        double[] gp = new double[goodKey.length];
        int[] gm = new int[goodKey.length];
        for (int g = 0; g < gp.length; g++) {
            if (t <= priceLast[g]) {
                gp[g] = price[g].v[t];
                gm[g] = price[g].mark[t];
            } else {
                double innovation = swing[g] * Math.sqrt((1 + PRICE_PERSISTENCE) / 2);
                gp[g] = Math.exp(PRICE_PERSISTENCE * Math.log(goodPrices.get(t - 1)[g]) + share * innovation * gauss(PRICE, GOOD_BASE + shockOf[g], year));
                gm[g] = WorldData.MARK_ESTIMATE;
            }
        }
        years.add(row);
        drawn.add(drow);
        moneyLevels.add(cur);
        goodPrices.add(gp);
        goodMarks.add(gm);
        perHead = perHeadNow;
        priceLevel = plNow;
    }

    /** The money a nation is in in a year: Historical's is the file's of December to 2025 and its 2025 one after; Random's is its money of 2000, or the one its regime uses from 2001. */
    private String moneyOf(int i, int t) {
        if (t == 0 || historical && t <= HISTORY_LAST_YEAR - FIRST_YEAR) return moneyDec[i][t];
        return moneyLater[i];
    }

    /**
     * A money's rate to the dollar this year, NaN for one no nation uses after the history. The dollar is 1. Else the
     * anchor's: its published figure while it has them (Historical), then by its regime - a float is last year's by
     * relative parity (the anchor's inflation against the dollar's) and a shock, a peg or a use the ratio to the
     * other money's rate held at its last published year's (Random's: 2000's). A loop of pegs holds last year's rate;
     * a money with no figure to go on is NaN, and a nation on it holds its own last rate (the fixtures have none).
     */
    private double moneyLevel(String code, int t, int year, double share, Map<String, Double> cur, Set<String> busy, double[] inflation) {
        Double known = cur.get(code);
        if (known != null) return known;
        double v;
        Integer a = anchor.get(code);
        if (DOLLAR.equals(code)) v = 1;
        else if (a == null) return Double.NaN;
        else if (!busy.add(code)) return held(code, t);
        else {
            if (t <= rateLast[a]) {
                v = moneyOf(a, t).equals(code) ? rate[a].v[t] : Double.NaN;   // its published figure, if it was on this money then
            } else if (regime[a].startsWith("peg:") || regime[a].startsWith("uses:")) {
                String target = regime[a].substring(regime[a].indexOf(':') + 1);
                double mine = levelAt(code, rateLast[a]), other = levelAt(target, rateLast[a]);
                v = mine > 0 && other > 0 ? mine / other * moneyLevel(target, t, year, share, cur, busy, inflation) : Double.NaN;
            } else {
                double drift = Math.log1p(inflation[a]) - Math.log1p(inflation[frontier]) + share * RATE_SD * gauss(RATE, a + 1, year);
                v = held(code, t) * Math.exp(drift);
            }
            busy.remove(code);
            if (!(v > 0) && t > rateLast[a]) v = held(code, t);
            if (!(v > 0)) return Double.NaN;
        }
        cur.put(code, v);
        return v;
    }

    /** A money's rate in an earlier year of the path; NaN where it had none. */
    private double levelAt(String code, int t) {
        if (DOLLAR.equals(code)) return 1;
        Double d = moneyLevels.get(t).get(code);
        return d == null ? Double.NaN : d;
    }

    /** A money's rate last year; NaN where it had none. */
    private double held(String code, int t) {
        return t > 0 ? levelAt(code, t - 1) : Double.NaN;
    }
}
