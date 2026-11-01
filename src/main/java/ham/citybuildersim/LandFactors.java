package ham.citybuildersim;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Each nation's land factor: what a hectare of its land costs against a hectare of Canada's (1, D21), at the founding and as its output a head moves
 * against Canada's. A pure model; the land office multiplies today's dollar formula by the seller's factor (WN5; LandSellers says who sells which plot).
 *
 * WHY THIS EXISTS. Land was priced by one formula for the whole world and bought from nobody. Jerus: "land costs different prices purely by who you are
 * buying it from", and "borders baked in; land bought from its nation" (spec-nations 6): a plot at Windsor on the Detroit bank is dearer than the one
 * across the river. The figures are the published farmland prices of 25 nations and a fit for the other 171 (tools.LandTable makes the resource from
 * tools/world/land.tsv, land-2024.json), in US dollars of 2024. A factor is a ratio, and a ratio to Canada needs no unit: Canada is 1.0 exactly, however
 * the dollar or the money moves.
 *
 * THE MOVE. From the founding (January of Nations.FIRST_YEAR, 1900 since EP1) the factor moves with the nation's real output a head on its path
 * (Nations), against Canada's, at b, the fit's elasticity of land's price on output a head; log-linear between Januaries, as WorldMoneys moves a price
 * level. With no path handed (a bare one, a harness's), or in month 0, it is the level.
 *
 * WHY NOT ITS MONEY TOO. The study's line has the factor move "with its money" as well. WN5 first moved it with the nation's US dollar price level, its
 * consumer prices over its money's rate, each money against its own rate at the founding as the real effective rate reads a partner (WN4). A price index
 * runs on through a change of money and the new money's rate starts afresh, so every nation that redenominated fell to FACTOR_FLOOR: Zimbabwe's move to
 * 2024 read 1.4e20, Venezuela's 430 against Canada's 47. The real rate's own swings mean-revert, and a nation's land is dear for the output a head it
 * stands for; so the money is left out, and what the city pays follows the seller's money only through the rate it is paid at.
 *
 * THE FOUNDING. The table is 2024's, so the level at the founding is the published ratio taken back along the same move on the file's own figures, the
 * Historical path's: level = published ratio x (Canada's output a head from the founding to 2024 over the nation's)^b (fileMove()). A Historical city in
 * January 2024 therefore pays the published ratio to the rounding of a power, and a nation that has grown richer than Canada since 2000 (the file holds
 * the century before at 2000's real figures) was cheaper then. A Random path meets the same founding and moves on its own.
 */
public final class LandFactors {

    /** The land table on the classpath: tools.LandTable's output. */
    public static final String RESOURCE = "/world/land-2024.json";

    /** The nation whose factor is exactly 1.0 (D21): Canada, whose land the game's formula was struck on. */
    public static final String REFERENCE = "CAN";

    /** The year of the table's figures. */
    public static final int BASE_YEAR = 2024;

    /** The least a factor is, whatever a path does to its nation: a hundredth of Canada's price. */
    public static final double FACTOR_FLOOR = 0.01;

    /** The most a factor is: a hundred times Canada's price. */
    public static final double FACTOR_CEILING = 100;

    private final int n;
    private final int reference;
    private final double elasticity;
    private final double[] usdHa, level;
    private final int[] mark;
    private final String[] source;
    private volatile Nations nations;

    private LandFactors(int n, int reference, double elasticity, double[] usdHa, double[] level, int[] mark, String[] source) {
        this.n = n;
        this.reference = reference;
        this.elasticity = elasticity;
        this.usdHa = usdHa;
        this.level = level;
        this.mark = mark;
        this.source = source;
    }

    /* =====================================================================
       THE TABLE
       ===================================================================== */

    /** One nation's line of the resource. */
    private record Entry(double usdHa, int mark, String source) { }

    /** The resource read: the fit's elasticity and each nation's line by iso3. */
    private record Table(double elasticity, Map<String, Entry> byIso3) { }

    private static Table table;

    private static synchronized Table table() {
        if (table == null) table = read();
        return table;
    }

    private static Table read() {
        try (InputStream in = LandFactors.class.getResourceAsStream(RESOURCE)) {
            if (in == null) throw new IllegalStateException("land factors: " + RESOURCE + " is not on the classpath");
            JsonObject root = JsonParser.parseString(new String(in.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject meta = root.getAsJsonObject("meta");
            if (!REFERENCE.equals(meta.get("reference").getAsString()) || meta.get("year").getAsInt() != BASE_YEAR)
                throw new IllegalStateException("land factors: " + RESOURCE + " is not " + REFERENCE + "'s, " + BASE_YEAR);
            double b = meta.getAsJsonObject("fit").get("output").getAsDouble();
            if (!(b > 0) || !Double.isFinite(b)) throw new IllegalStateException("land factors: " + RESOURCE + " has no elasticity of price on output");
            Map<String, Entry> by = new HashMap<>();
            for (JsonElement e : root.getAsJsonArray("nations")) {
                JsonObject o = e.getAsJsonObject();
                double usd = o.get("usd_ha").getAsDouble();
                if (!(usd > 0) || !Double.isFinite(usd)) throw new IllegalStateException("land factors: " + RESOURCE + " has no price for " + o.get("iso3").getAsString());
                by.put(o.get("iso3").getAsString(), new Entry(usd, o.get("mark").getAsInt(), o.get("source").getAsString()));
            }
            if (!by.containsKey(REFERENCE)) throw new IllegalStateException("land factors: " + RESOURCE + " has no line for " + REFERENCE);
            return new Table(b, by);
        } catch (IOException e) {
            throw new IllegalStateException("land factors: " + RESOURCE + " could not be read", e);
        }
    }

    /**
     * The factors at the founding, for the nations of a world file: the table's published ratio to Canada's, times (Canada's move from the founding to
     * January of BASE_YEAR over the nation's)^elasticity, each move read off the file's own figures as move() reads the path (fileMove()). A nation the
     * table lacks is 1.0; one whose move cannot be read keeps the published ratio. Nothing of the file is kept.
     */
    public static LandFactors load(WorldData file) {
        Table t = table();
        int n = file.nationCount();
        int ref = file.numberOf(REFERENCE);
        double[] usd = new double[n + 1], lvl = new double[n + 1];
        int[] mark = new int[n + 1];
        String[] source = new String[n + 1];
        double refUsd = t.byIso3().get(REFERENCE).usdHa();
        usd[0] = refUsd;
        double refMove = ref >= 1 && ref <= n ? fileMove(file, ref) : Double.NaN;
        for (int i = 1; i <= n; i++) {
            Entry e = t.byIso3().get(file.nation(i).iso3());
            usd[i] = e == null ? refUsd : e.usdHa();
            mark[i] = e == null ? WorldData.MARK_NONE : e.mark();
            source[i] = e == null ? "none" : e.source();
            if (i == ref) {
                lvl[i] = 1.0;
                continue;
            }
            double ratio = usd[i] / refUsd;
            double own = e == null ? Double.NaN : fileMove(file, i);
            if (usable(own) && usable(refMove)) ratio *= Math.pow(refMove / own, t.elasticity());
            lvl[i] = e == null ? 1.0 : clamp(ratio);
        }
        return new LandFactors(n, ref, t.elasticity(), usd, lvl, mark, source);
    }

    /**
     * move() on the file's own figures, from the founding to BASE_YEAR: the output a head (gdp_2015usd over people, what Nations' output and people are on
     * the Historical path) in BASE_YEAR over Nations.FIRST_YEAR's - what move() reads on a Historical path in January of BASE_YEAR. NaN where the file has
     * no such paths or either figure is missing or not positive, as move()'s is.
     */
    static double fileMove(WorldData file, int nation) {
        List<String> series = file.pathSeries();
        if (nation < 1 || nation > file.pathNations() || !series.contains("gdp_2015usd") || !series.contains("people")) return Double.NaN;
        if (Nations.FIRST_YEAR < file.pathFirstYear() || BASE_YEAR >= file.pathFirstYear() + file.pathYears()) return Double.NaN;
        double now = file.pathValue("gdp_2015usd", nation, BASE_YEAR) / file.pathValue("people", nation, BASE_YEAR);
        double then = file.pathValue("gdp_2015usd", nation, Nations.FIRST_YEAR) / file.pathValue("people", nation, Nations.FIRST_YEAR);
        return usable(now) && usable(then) ? now / then : Double.NaN;
    }

    private static boolean usable(double v) { return v > 0 && Double.isFinite(v); }

    private static double clamp(double v) {
        if (Double.isNaN(v)) return 1.0;
        return Math.max(FACTOR_FLOOR, Math.min(FACTOR_CEILING, v));
    }

    /* =====================================================================
       THE FACTOR IN A MONTH
       ===================================================================== */

    /** Hands the factors the nations' path (once the world market is built): its output a head is what a factor moves with. With none handed, factorAt() is the level. */
    public void setPaths(Nations nations) {
        this.nations = nations;
    }

    /** Whether a path has been handed (setPaths()). */
    public boolean hasPaths() { return nations != null; }

    /**
     * The nation's land factor in a game month: 1.0 exactly for the reference, 1.0 for a number outside 1..nationCount(), the level at the founding when
     * no path is handed or in the founding's month, else the level times (the nation's move against the reference's)^elasticity, kept between
     * FACTOR_FLOOR and FACTOR_CEILING. A move that cannot be read (a missing figure) counts 1.
     */
    public double factorAt(int nation, int gameMonth) {
        if (nation == reference || nation < 1 || nation > n) return 1.0;
        Nations paths = nations;
        int t = WorldMoneys.monthIndex(gameMonth);
        if (paths == null || reference < 1 || t <= 0) return level[nation];
        double own = move(paths, nation, t), ref = move(paths, reference, t);
        if (!usable(own) || !usable(ref)) return level[nation];
        return clamp(level[nation] * Math.pow(own / ref, elasticity));
    }

    /**
     * What a nation's output a head has done since the founding, by month t (months since January of Nations.FIRST_YEAR): its year's over its first
     * year's, log-linear between Januaries as WorldMoneys.priceLevel() reads a year's prices; NaN where a figure is missing or not positive.
     */
    private static double move(Nations paths, int nation, int t) {
        int year = Nations.FIRST_YEAR + t / 12;
        double then = paths.year(nation, Nations.FIRST_YEAR).outputPerHead(), a = paths.year(nation, year).outputPerHead();
        if (!usable(then) || !usable(a)) return Double.NaN;
        if (t % 12 == 0) return a / then;
        double b = paths.year(nation, year + 1).outputPerHead();
        if (!usable(b)) return a / then;
        return StrictMath.exp(StrictMath.log(a / then) + (t % 12) / 12.0 * StrictMath.log(b / a));
    }

    /* =====================================================================
       WHAT A CALLER READS
       ===================================================================== */

    /** The nation's factor at the founding (month 0), 1.0 for a number outside 1..nationCount(). */
    public double level(int nation) { return nation == reference || nation < 1 || nation > n ? 1.0 : level[nation]; }

    /** The nation's land a hectare, US dollars of BASE_YEAR (the table's: a published price, the fit's, or Canada's where it lacks the figures); the reference's for a number outside 1..nationCount(). */
    public double usdPerHectare(int nation) { return usdHa[nation < 1 || nation > n ? 0 : nation]; }

    /** How sure the table is of the nation's price: WorldData.MARK_PUBLISHED (3) where a statistical office publishes it, MARK_ESTIMATE (1) for the fit or Canada's price, MARK_NONE (0) for a nation the table lacks. */
    public int mark(int nation) { return nation < 1 || nation > n ? WorldData.MARK_NONE : mark[nation]; }

    /** Where the price comes from: a source id of the table ("eurostat", "usda", "statcan"), "fit", or "none" (Canada's price used). */
    public String source(int nation) { return nation < 1 || nation > n ? "none" : source[nation]; }

    /** The number of the reference nation (Canada), 0 where the world file lacks it. */
    public int reference() { return reference; }

    /** The exponent a factor moves with its nation's output a head against Canada's: the fit's elasticity of land's price on output a head. */
    public double elasticity() { return elasticity; }

    /** How many nations there are (the world file's N). */
    public int nationCount() { return n; }
}
