package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Rankings' real-world figures: world-2025.json's 196 nations and city
 * regions, the 70 measures each is ranked on, every place's latest figure of
 * each with its year, its mark (published, a published projection, the game's
 * estimate) and its source, and the sources' licences, the estimates' rules
 * and the CPI-U that turns a US dollar of any year into a 2015 one.
 *
 * WHY. The board's Real world 2025 (spec-leaderboards 6-8, batch LB1) ranks
 * the player's cities beside every real place on every measure, and each
 * figure it shows has to say where it comes from, in what year, and whether
 * the game made it. The game never fetches anything, so tools/world/ bakes the
 * figures into the resources once, and this reads them: it trusts the file and
 * never invents - a figure the file does not carry is absent (null, NaN),
 * never 0, because the leaderboard that read a 0 would rank a place last for
 * a number nobody published. What the file must keep to is WorldMeasuresCheck's,
 * not this reader's; the format is tools/world/FORMAT-2025.txt.
 */
public final class WorldMeasures {

    /** Where the file lies, beside the classes. */
    public static final String RESOURCE = "world/world-2025.json";

    /** A figure: its value in the measure's unit, the year it is of, its mark (3 published, 2 a published projection, 1 the game's estimate), the source it was read or made from, and the rule that made it (null unless the mark is 1). */
    public record Figure(double value, int year, int mark, String source, String rule) { }

    /** Which way is better on a measure: more (UP), less (DOWN, ranked ascending), or neither (NONE: ranked largest first, with no verdict colour; spec-leaderboards 6). */
    public enum Arrow { UP, DOWN, NONE }

    /**
     * A measure of the board: its key (the key of every place's figure), its group, name and unit, the arrow, whether it is in US$ and follows the Money toggle, its scope ("N" nations, "NC" nations and city regions, "Y" only the player's cities rank), whether it is one of the ten on Your world, the spec that sourced it ("none:" when nothing publishes it) and its note.
     */
    public record Measure(String key, String group, String name, String unit, Arrow arrow, boolean money, String scope,
                          boolean yourWorld, String source, String note) {
        /** Whether city regions carry it as well as nations. */
        public boolean forRegions() { return "NC".equals(scope); }

        /** Whether only the player's cities are ranked on it: no real place has a figure. */
        public boolean yoursOnly() { return "Y".equals(scope); }

        /** Whether anything publishes it: a measure whose source is "none:..." has no figure for any real place. */
        public boolean sourced() { return source != null && !source.startsWith("none:"); }
    }

    /** A GDP measure in another money (key "gdp.k", of "gdp"): the money's name and the source spec. */
    public record Variant(String key, String of, String money, String source) { }

    /** A source a figure names: its id, name, url, licence and the day it was downloaded. */
    public record Source(String id, String name, String url, String licence, String downloaded) { }

    /** A mark: its number (3, 2 or 1), its symbol and its name. */
    public record Mark(int mark, String symbol, String name) { }

    /** A nation, or a city region. */
    public enum Kind { NATION, REGION }

    /**
     * A place. band is the size band (the digits in its people less one), -1 when it has no people figure. nation and def are a city region's nation and boundary (a key of definitions()), null for a nation. sharePeople and shareOutput are a region's shares of its nation's people and output, null for a nation. figures are its measures' figures, in the file's order.
     */
    public record Place(String id, Kind kind, String name, int band, String nation, String def,
                        Figure sharePeople, Figure shareOutput, Map<String, Figure> figures) {
        public Place {
            figures = Collections.unmodifiableMap(new LinkedHashMap<>(figures));
        }

        /** Its figure for a measure or variant key, null when the place has none. */
        public Figure figure(String key) { return figures.get(key); }

        /** Its value for a measure or variant key, NaN when the place has none. */
        public double value(String key) {
            Figure f = figures.get(key);
            return f == null ? Double.NaN : f.value();
        }
    }

    private final String name, made, cpiSeries, cpiSource;
    private final int year, windowFirst, windowLast, countNations, countRegions, countFigures, countEstimates;
    private final double usPrices2015Over2025;
    private final Map<Integer, Double> cpiU;
    private final List<Measure> measures;
    private final Map<String, Measure> measureByKey;
    private final Map<String, Variant> variants;
    private final Map<String, Source> sources;
    private final Map<String, String> rules, definitions;
    private final Map<Integer, Mark> marks;
    private final List<Place> places, nations, regions;
    private final Map<String, Place> placeById;

    private static WorldMeasures cached;

    /** The file in the resources, read once. Thread-safe; fails with IllegalStateException when the resource is not beside the classes. */
    public static synchronized WorldMeasures load() {
        if (cached == null) {
            try (InputStream in = WorldMeasures.class.getResourceAsStream("/" + RESOURCE)) {
                if (in == null) throw new IllegalStateException(RESOURCE + " is not beside the classes");
                cached = parse(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return cached;
    }

    /** A file's text read as it stands, not cached: the harness's fixtures. Throws IllegalArgumentException naming the place and key of a figure it cannot read. */
    public static WorldMeasures parse(String json) {
        JsonElement root = JsonParser.parseString(json);
        if (!root.isJsonObject()) throw new IllegalArgumentException(RESOURCE + " is not an object");
        return new WorldMeasures(root.getAsJsonObject());
    }

    private WorldMeasures(JsonObject root) {
        JsonObject meta = object(root, "meta", RESOURCE);
        name = need(meta, "name", "meta");
        made = need(meta, "made", "meta");
        year = needInt(meta, "year", "meta");
        JsonArray window = array(meta, "window", "meta");
        if (window.size() != 2) throw new IllegalArgumentException("meta.window is [first, last], not " + window);
        windowFirst = window.get(0).getAsInt();
        windowLast = window.get(1).getAsInt();
        usPrices2015Over2025 = needDouble(meta, "us_prices_2015_over_2025", "meta");

        Map<Integer, Double> cpi = new TreeMap<>();
        JsonObject cpiObject = object(meta, "cpi_u", "meta");
        for (Map.Entry<String, JsonElement> e : cpiObject.entrySet()) {
            if (e.getKey().matches("\\d+")) cpi.put(Integer.parseInt(e.getKey()), e.getValue().getAsDouble());
        }
        cpiU = Collections.unmodifiableMap(cpi);
        cpiSeries = text(cpiObject, "series");
        cpiSource = text(cpiObject, "source");

        Map<Integer, Mark> markMap = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> e : object(meta, "marks", "meta").entrySet()) {
            JsonObject m = e.getValue().getAsJsonObject();
            int number = Integer.parseInt(e.getKey());
            markMap.put(number, new Mark(number, text(m, "symbol"), text(m, "name")));
        }
        marks = Collections.unmodifiableMap(markMap);

        Map<String, Source> sourceMap = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> e : object(meta, "sources", "meta").entrySet()) {
            JsonObject s = e.getValue().getAsJsonObject();
            sourceMap.put(e.getKey(), new Source(e.getKey(), text(s, "name"), text(s, "url"), text(s, "licence"), text(s, "downloaded")));
        }
        sources = Collections.unmodifiableMap(sourceMap);

        rules = strings(object(meta, "rules", "meta"));
        definitions = strings(object(meta, "definitions", "meta"));

        List<Measure> measureList = new ArrayList<>();
        Map<String, Measure> byKey = new LinkedHashMap<>();
        for (JsonElement e : array(meta, "measures", "meta")) {
            JsonObject m = e.getAsJsonObject();
            String key = need(m, "key", "a measure");
            Measure measure = new Measure(key, text(m, "group"), text(m, "name"), text(m, "unit"), arrow(m, key),
                    flag(m, "money"), text(m, "scope"), flag(m, "w"), text(m, "source"), text(m, "note"));
            measureList.add(measure);
            byKey.putIfAbsent(key, measure);
        }
        measures = Collections.unmodifiableList(measureList);
        measureByKey = Collections.unmodifiableMap(byKey);

        Map<String, Variant> variantMap = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> e : object(meta, "variants", "meta").entrySet()) {
            JsonObject v = e.getValue().getAsJsonObject();
            variantMap.put(e.getKey(), new Variant(e.getKey(), text(v, "of"), text(v, "money"), text(v, "source")));
        }
        variants = Collections.unmodifiableMap(variantMap);

        JsonObject counts = object(meta, "counts", "meta");
        countNations = needInt(counts, "nations", "meta.counts");
        countRegions = needInt(counts, "regions", "meta.counts");
        countFigures = needInt(counts, "figures", "meta.counts");
        countEstimates = needInt(counts, "estimates", "meta.counts");

        List<Place> placeList = new ArrayList<>(), nationList = new ArrayList<>(), regionList = new ArrayList<>();
        Map<String, Place> byId = new LinkedHashMap<>();
        for (JsonElement e : array(root, "places", RESOURCE)) {
            Place place = place(e.getAsJsonObject());
            placeList.add(place);
            (place.kind() == Kind.NATION ? nationList : regionList).add(place);
            byId.putIfAbsent(place.id(), place);
        }
        places = Collections.unmodifiableList(placeList);
        nations = Collections.unmodifiableList(nationList);
        regions = Collections.unmodifiableList(regionList);
        placeById = Collections.unmodifiableMap(byId);
    }

    private static Place place(JsonObject p) {
        String id = need(p, "id", "a place");
        String kindText = need(p, "kind", id);
        Kind kind;
        switch (kindText) {
            case "nation" -> kind = Kind.NATION;
            case "region" -> kind = Kind.REGION;
            default -> throw new IllegalArgumentException(id + ": kind is \"nation\" or \"region\", not \"" + kindText + "\"");
        }
        int band = has(p, "band") ? p.get("band").getAsInt() : -1;
        Figure sharePeople = null, shareOutput = null;
        if (has(p, "share")) {
            JsonObject share = p.getAsJsonObject("share");
            if (has(share, "people")) sharePeople = figure(share.get("people"), id, "share.people");
            if (has(share, "output")) shareOutput = figure(share.get("output"), id, "share.output");
        }
        Map<String, Figure> figures = new LinkedHashMap<>();
        if (has(p, "f")) {
            for (Map.Entry<String, JsonElement> e : p.getAsJsonObject("f").entrySet()) {
                figures.put(e.getKey(), figure(e.getValue(), id, e.getKey()));
            }
        }
        return new Place(id, kind, text(p, "name"), band, text(p, "nation"), text(p, "def"), sharePeople, shareOutput, figures);
    }

    /** A figure array of 4 or 5 elements; anything else names the place and key. */
    private static Figure figure(JsonElement e, String place, String key) {
        if (!e.isJsonArray()) throw new IllegalArgumentException(place + " " + key + ": a figure is an array, not " + e);
        JsonArray a = e.getAsJsonArray();
        if (a.size() != 4 && a.size() != 5) {
            throw new IllegalArgumentException(place + " " + key + ": a figure has 4 or 5 elements, not " + a.size());
        }
        try {
            return new Figure(a.get(0).getAsDouble(), a.get(1).getAsInt(), a.get(2).getAsInt(), a.get(3).getAsString(),
                    a.size() == 5 ? a.get(4).getAsString() : null);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException(place + " " + key + ": cannot read the figure " + a + " (" + ex.getMessage() + ")", ex);
        }
    }

    private static Arrow arrow(JsonObject m, String key) {
        String a = need(m, "arrow", key);
        switch (a.toLowerCase(java.util.Locale.ROOT)) {
            case "up": return Arrow.UP;
            case "down": return Arrow.DOWN;
            case "none": return Arrow.NONE;
            default: throw new IllegalArgumentException(key + ": arrow is up, down or none, not \"" + a + "\"");
        }
    }

    private static Map<String, String> strings(JsonObject o) {
        Map<String, String> m = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> e : o.entrySet()) m.put(e.getKey(), e.getValue().getAsString());
        return Collections.unmodifiableMap(m);
    }

    private static boolean has(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull();
    }

    private static String text(JsonObject o, String key) {
        return has(o, key) ? o.get(key).getAsString() : null;
    }

    private static boolean flag(JsonObject o, String key) {
        return has(o, key) && o.get(key).getAsBoolean();
    }

    private static String need(JsonObject o, String key, String where) {
        if (!has(o, key)) throw new IllegalArgumentException(where + " has no \"" + key + "\"");
        return o.get(key).getAsString();
    }

    private static int needInt(JsonObject o, String key, String where) {
        if (!has(o, key)) throw new IllegalArgumentException(where + " has no \"" + key + "\"");
        return o.get(key).getAsInt();
    }

    private static double needDouble(JsonObject o, String key, String where) {
        if (!has(o, key)) throw new IllegalArgumentException(where + " has no \"" + key + "\"");
        return o.get(key).getAsDouble();
    }

    private static JsonObject object(JsonObject o, String key, String where) {
        if (!has(o, key) || !o.get(key).isJsonObject()) throw new IllegalArgumentException(where + " has no object \"" + key + "\"");
        return o.getAsJsonObject(key);
    }

    private static JsonArray array(JsonObject o, String key, String where) {
        if (!has(o, key) || !o.get(key).isJsonArray()) throw new IllegalArgumentException(where + " has no array \"" + key + "\"");
        return o.getAsJsonArray(key);
    }

    /* =====================================================================
       THE FILE'S META
       ===================================================================== */

    /** The file's name ("world-2025"). */
    public String name() { return name; }

    /** The day the file was built (yyyy-mm-dd). */
    public String made() { return made; }

    /** The board's year: each figure is its place's latest in the window. */
    public int year() { return year; }

    /** The first year of the window: a figure older than this is left out. */
    public int windowFirst() { return windowFirst; }

    /** The last year of the window. */
    public int windowLast() { return windowLast; }

    /** The US's consumer prices of 2015 over 2025's (the CPI-U's annual averages divided, unrounded): a real 2025 dollar times this is a 2015 dollar. */
    public double usPrices2015Over2025() { return usPrices2015Over2025; }

    /** The CPI-U's annual average of a year the file carries (the BLS's, series CUUR0000SA0), NaN for a year it does not. */
    public double cpiU(int year) {
        Double v = cpiU.get(year);
        return v == null ? Double.NaN : v;
    }

    /** The years cpiU() has a value for, in order. */
    public List<Integer> cpiYears() { return List.copyOf(cpiU.keySet()); }

    /** The BLS series the CPI-U is of (CUUR0000SA0), null when the file does not say. */
    public String cpiSeries() { return cpiSeries; }

    /** The id in sources() the CPI-U was read from, null when the file does not say. */
    public String cpiSource() { return cpiSource; }

    /** The 70 measures, in the file's order. */
    public List<Measure> measures() { return measures; }

    /** A measure by key, null when there is none. */
    public Measure measure(String key) { return measureByKey.get(key); }

    /** The GDP variants (gdp.k, gdp.ppp, gdp_pp.k, gdp_pp.ppp) by key. */
    public Map<String, Variant> variants() { return variants; }

    /** The sources figures name, by id, each with its licence. */
    public Map<String, Source> sources() { return sources; }

    /** How the game made each kind of estimate (mark 1), by rule id. */
    public Map<String, String> rules() { return rules; }

    /** The city boundaries a region's def names, by id. */
    public Map<String, String> definitions() { return definitions; }

    /** The marks (3, 2, 1), by number. */
    public Map<Integer, Mark> marks() { return marks; }

    /** The nations the meta's counts say. */
    public int countNations() { return countNations; }

    /** The city regions the meta's counts say. */
    public int countRegions() { return countRegions; }

    /** The figures the meta's counts say (the places' figures, not their shares). */
    public int countFigures() { return countFigures; }

    /** The figures of them marked 1 the meta's counts say. */
    public int countEstimates() { return countEstimates; }

    /* =====================================================================
       THE PLACES
       ===================================================================== */

    /** Every place: the nations in ISO3 order, then the city regions. */
    public List<Place> places() { return places; }

    /** The nations, in the file's order. */
    public List<Place> nations() { return nations; }

    /** The city regions, in the file's order. */
    public List<Place> regions() { return regions; }

    /** A place by id, null when there is none. */
    public Place place(String id) { return placeById.get(id); }

    @Override
    public String toString() {
        return name + " (" + nations.size() + " nations, " + regions.size() + " city regions, " + measures.size() + " measures)";
    }
}
