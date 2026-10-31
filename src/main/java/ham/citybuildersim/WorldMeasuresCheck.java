package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * The Rankings' real-world file held to what it promises (batch LB1; the
 * project's spec-leaderboards.md 6-8): WorldMeasures' reading of
 * world-2025.json, and the rules tools/world/FORMAT-2025.txt states of the
 * file, which the pipeline could break without the reader noticing.
 *
 * WHY. The board's Real world 2025 ranks the player's cities beside 196
 * nations and 62 city regions on 70 measures, and a wrong figure there is a
 * rank nobody can trust: a missing figure read as 0 puts a nation last for a
 * number nobody published, a mark or a source that does not resolve is a
 * drawer that cannot say where a figure came from, and a ratio of US prices
 * that is not the CPI-U's turns every real dollar into the wrong 2015 one.
 * The rules are one method, problems(), each problem a short sentence naming
 * the place and key; the real file has none, and for each kind a copy of the
 * real JSON changed in one place has exactly that one.
 *
 * THE DATA. world-2025.json in the resources, always. The full world-1.bin
 * (-Dcbs.world=<path>, WorldData.locate()) is not in the repository; where it
 * is named, section 2 also holds the file to every one of its nations, and a
 * line says when it is not.
 *
 * What this has to prove:
 *   1. THE FILE: it loads; its name, year and window; under 1 MB; 70 measures
 *      with unique keys in ten groups; 13 for city regions, 4 only the player's
 *      cities rank, 10 on Your world; problems() of the real file is empty.
 *   2. EVERY NATION OF world-1.bin IS THERE ONCE: 196 nations in ISO3 order,
 *      no region with a nation's id, the fixture's twelve, and on the full file
 *      every nation it has, with its name, and none it lacks.
 *   3. A MISSING FIGURE IS ABSENT, NEVER 0: Taiwan carries no WDI figure but
 *      world-1's; no place has a figure of a measure nothing publishes or only
 *      the player's cities rank; a region has only city-region measures; every
 *      value is finite, and a measure that cannot be 0 is over it.
 *   4. EVERY MARK AND SOURCE RESOLVES, AND EVERY SOURCE NAMES ITS LICENCE: marks
 *      are 1, 2 or 3 and listed; sources and rules are in the meta and each
 *      used; a mark 1 has a rule and the others none; the IMF's World Economic
 *      Outlook is not a source; the meta's counts are a recount.
 *   5. A CITY REGION'S SHARES OF ITS NATION ARE UNDER 1: its nation, boundary
 *      and both shares resolve, and it is smaller than its nation.
 *   6. THE 2015-OVER-2025 RATIO IS THE CPI-U'S TO THE DIGIT: the BLS's annual
 *      averages for 2015 and 2025, every year of the window between them
 *      rising, and the ratio their division.
 *   7. THE MONEY FIGURES AGREE WITH EACH OTHER: the US is 1 on price level and
 *      rate; PPP over market is 1 over the price level; GDP per person is GDP
 *      over people; a band is the digits of the people; a region is smaller than
 *      its nation; a region on its people's share has no GDP per person.
 *
 * Every fixture causes its condition: a doctored copy is the real JSON parsed,
 * changed in one place and written back, so that what it breaks is the one rule
 * its section names, and the real file is checked by the same method.
 */
public class WorldMeasuresCheck {

    static int fails = 0;
    /** Where the harness prints: the console. */
    static final PrintStream out = System.out;

    /** The nations of world-1.bin (the 193 UN members, Taiwan, Hong Kong and Macau). */
    static final int NATIONS = 196;

    /** The most the file may weigh: it is a resource inside the jar, and it is read whole. */
    static final int MAX_BYTES = 1_000_000;

    /** The CPI-U's annual average of 2015, all urban consumers, US city average, all items, not seasonally adjusted (BLS series CUUR0000SA0). */
    static final double CPI_U_2015 = 237.017;

    /** The CPI-U's annual average of 2025 (BLS series CUUR0000SA0). */
    static final double CPI_U_2025 = 321.943;

    /** The BLS series the CPI-U is of. */
    static final String CPI_SERIES = "CUUR0000SA0";

    /** The ten groups of measures, in the order of the file, each with how many it has. */
    static final List<String> GROUPS = List.of("economy 11", "work 6", "prices 8", "public 8", "banks 5", "people 11", "housing 5", "safety 4", "health 4", "land 8");

    /** The measures whose scope is city regions too (NC), and the ones only the player's cities rank (Y). */
    static final int SCOPE_NC = 13, SCOPE_Y = 4;

    /** The ten measures of Your world (spec-leaderboards D15), in the file's order. */
    static final List<String> YOUR_WORLD = List.of("gdp", "gdp_pp", "growth_real", "growth_10y", "inflation", "price_level", "people", "pop_growth", "area", "density");

    /** The measures (and the GDP variants) a place cannot have 0 of: a figure of 0 for one is a figure nobody published. Oil is not here: 89 nations publish a 0 of it (EIA's). */
    static final Set<String> POSITIVE = Set.of("gdp", "gdp.k", "gdp.ppp", "gdp_pp", "gdp_pp.k", "gdp_pp.ppp", "people", "area", "density",
            "life_exp", "births", "deaths", "child_deaths", "participation", "tertiary", "avg_wage", "min_wage", "kaitz", "reserves_months", "m2",
            "price_level", "fx_vs_parity", "aged_65", "old_age_dep", "health_spend", "homes_per_1000", "electricity_pp", "water_pp");

    /** The share of the money figures that must agree with their identity, and how near is agreeing: a few nations' published figures miss each identity (the WDI converts AGO BDI NPL SYR UGA ZWE at an alternative rate and gives Cyprus a per-person population of its own). */
    static final double AGREE_SHARE = .95, AGREE_TOLERANCE = .01;

    /** The fewest nations an identity may be counted over before the share means anything. */
    static final int MIN_COUNTED = 100;

    /** The nearest the United States' price level and rate against parity are to 1: the base of both is the United States. */
    static final double US_ONE_TOLERANCE = 1e-9;

    /** The nation the doctored copies change (Afghanistan, which has a published GDP and most of the others). */
    static final String DOCTORED_NATION = "AFG";

    /** The city region the doctored copies change (Tokyo, an OECD region with published shares). */
    static final String DOCTORED_REGION = "TYO";

    /* The problems' sentences: problems() writes them and the sections look for them by these words. */
    static final String P_DUP_MEASURE = "a measure key that is in the file twice";
    static final String P_DUP_PLACE = "a place id that is in the file twice";
    static final String P_NOT_A_MEASURE = "is not a measure or a variant of the file";
    static final String P_NO_SOURCE = "is a measure nothing publishes (its source is none:)";
    static final String P_Y_ONLY = "is a measure only the player's cities rank (scope Y)";
    static final String P_REGION_SCOPE = "is not a city-region measure (scope NC)";
    static final String P_NOT_FINITE = "is not a finite number";
    static final String P_NOT_POSITIVE = "is not over 0";
    static final String P_WINDOW = "is outside the window";
    static final String P_MARK = "is not 1, 2 or 3";
    static final String P_MARK_UNLISTED = "is not in meta.marks";
    static final String P_SOURCE = "is not in meta.sources";
    static final String P_RULE_MISSING = "is an estimate (mark 1) with no rule";
    static final String P_RULE_UNKNOWN = "is not in meta.rules";
    static final String P_RULE_SPARE = "is not an estimate and carries a rule";
    static final String P_LICENCE = "licence is blank";
    static final String P_URL = "url is blank";
    static final String P_WEO = "mentions the IMF's World Economic Outlook";
    static final String P_SOURCE_UNUSED = "source is named by no figure";
    static final String P_RULE_UNUSED = "rule is made by no figure";
    static final String P_COUNT = "but the places hold";
    static final String P_REGION_NATION = "is not a nation place";
    static final String P_REGION_ID = "has a nation's id";
    static final String P_REGION_DEF = "is not in meta.definitions";
    static final String P_SHARE_MISSING = "is missing";
    static final String P_SHARE_RANGE = "is not over 0 and under 1";
    static final String P_REGION_PEOPLE = "is not under its nation's";
    static final String P_CPI_YEAR = "is absent";
    static final String P_CPI_RISE = "does not rise";
    static final String P_CPI_CONSTANT = "is not the BLS's";
    static final String P_CPI_SERIES = "series is not";
    static final String P_RATIO = "is not cpi_u 2015 over 2025";

    /** Prints a label and OK or FAIL; the exit code is the count of FAILs. */
    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-120s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** A check over many things, that says how many there were and the first few that were wrong. */
    static void all(String label, int counted, List<String> bad) {
        check(label + " (" + counted + ")", bad.isEmpty() && counted > 0);
        for (int i = 0; i < Math.min(5, bad.size()); i++) out.println("      " + bad.get(i));
        if (bad.size() > 5) out.println("      ... and " + (bad.size() - 5) + " more");
    }

    /* =====================================================================
       THE RULES - one method, each problem a sentence naming the place and key
       ===================================================================== */

    /** Everything wrong with a file, as short sentences naming the place and key; none when it keeps FORMAT-2025.txt. */
    static List<String> problems(WorldMeasures w) {
        List<String> p = new ArrayList<>();
        Set<String> keys = new HashSet<>();
        for (WorldMeasures.Measure m : w.measures()) if (!keys.add(m.key())) p.add(m.key() + ": " + P_DUP_MEASURE);

        // the CPI-U: every year of the window, rising, the BLS's constants, and the ratio their division
        if (!CPI_SERIES.equals(w.cpiSeries())) p.add("cpi_u: " + P_CPI_SERIES + " " + CPI_SERIES + " but " + w.cpiSeries());
        for (int y = w.windowFirst(); y <= w.windowLast(); y++) {
            if (Double.isNaN(w.cpiU(y))) p.add("cpi_u " + y + ": " + P_CPI_YEAR);
            else if (y > w.windowFirst() && !Double.isNaN(w.cpiU(y - 1)) && !(w.cpiU(y) > w.cpiU(y - 1))) p.add("cpi_u " + y + ": " + w.cpiU(y) + " " + P_CPI_RISE + " from " + w.cpiU(y - 1));
        }
        if (w.cpiU(2015) != CPI_U_2015) p.add("cpi_u 2015: " + w.cpiU(2015) + " " + P_CPI_CONSTANT + " " + CPI_U_2015);
        if (w.cpiU(2025) != CPI_U_2025) p.add("cpi_u 2025: " + w.cpiU(2025) + " " + P_CPI_CONSTANT + " " + CPI_U_2025);
        if (w.usPrices2015Over2025() != w.cpiU(2015) / w.cpiU(2025)) {
            p.add("us_prices_2015_over_2025: " + digits17(w.usPrices2015Over2025()) + " " + P_RATIO + " (" + digits17(w.cpiU(2015) / w.cpiU(2025)) + ")");
        }

        Set<String> nationIds = new HashSet<>();
        for (WorldMeasures.Place pl : w.nations()) nationIds.add(pl.id());
        Set<String> usedSources = new HashSet<>(), usedRules = new HashSet<>();
        if (w.cpiSource() != null) usedSources.add(w.cpiSource());
        Set<String> ids = new HashSet<>();
        int figures = 0, estimates = 0;
        for (WorldMeasures.Place pl : w.places()) {
            boolean region = pl.kind() == WorldMeasures.Kind.REGION;
            if (!ids.add(pl.id())) p.add(pl.id() + ": " + (region && nationIds.contains(pl.id()) ? "the region " + P_REGION_ID : P_DUP_PLACE));
            for (var e : pl.figures().entrySet()) {
                String key = e.getKey(), at = pl.id() + " " + key + ": ";
                WorldMeasures.Figure f = e.getValue();
                figures++;
                if (f.mark() == 1) estimates++;
                WorldMeasures.Measure m = w.measure(key);
                if (m == null && !w.variants().containsKey(key)) p.add(at + "the key " + P_NOT_A_MEASURE);
                else if (m != null) {
                    if (m.yoursOnly()) p.add(at + "the measure " + P_Y_ONLY);
                    else if (!m.sourced()) p.add(at + "the measure " + P_NO_SOURCE);
                    else if (region && !m.forRegions()) p.add(at + "the measure (scope " + m.scope() + ") " + P_REGION_SCOPE);
                }
                figureProblems(at, f, w, p, usedSources, usedRules);
                if (!Double.isFinite(f.value())) p.add(at + "value " + f.value() + " " + P_NOT_FINITE);
                else if (POSITIVE.contains(key) && !(f.value() > 0)) p.add(at + "value " + f.value() + " " + P_NOT_POSITIVE);
            }
            if (!region) continue;

            String at = pl.id() + ": ";
            WorldMeasures.Place nation = pl.nation() == null ? null : w.place(pl.nation());
            if (nation == null || nation.kind() != WorldMeasures.Kind.NATION) p.add(at + "nation " + pl.nation() + " " + P_REGION_NATION);
            if (pl.def() == null || !w.definitions().containsKey(pl.def())) p.add(at + "def " + pl.def() + " " + P_REGION_DEF);
            for (String which : List.of("people", "output")) {
                WorldMeasures.Figure share = which.equals("people") ? pl.sharePeople() : pl.shareOutput();
                String sat = pl.id() + " share." + which + ": ";
                if (share == null) {
                    p.add(sat + "the share " + P_SHARE_MISSING);
                    continue;
                }
                figureProblems(sat, share, w, p, usedSources, usedRules);
                if (!(share.value() > 0 && share.value() < 1)) p.add(sat + "value " + share.value() + " " + P_SHARE_RANGE);
            }
            if (nation != null && nation.kind() == WorldMeasures.Kind.NATION) {
                double own = pl.value("people"), theirs = nation.value("people");
                if (Double.isFinite(own) && Double.isFinite(theirs) && !(own < theirs)) p.add(at + "people " + own + " " + P_REGION_PEOPLE + " " + theirs);
            }
        }

        for (var e : w.sources().entrySet()) {
            WorldMeasures.Source s = e.getValue();
            String at = "source " + e.getKey() + ": ";
            if (s.licence() == null || s.licence().isBlank()) p.add(at + P_LICENCE);
            if (s.url() == null || s.url().isBlank()) p.add(at + P_URL);
            if (s.name() != null && s.name().toLowerCase().contains("world economic outlook")) p.add(at + "the name " + P_WEO);
            if (!usedSources.contains(e.getKey())) p.add(at + P_SOURCE_UNUSED);
        }
        for (String rule : w.rules().keySet()) if (!usedRules.contains(rule)) p.add("rule " + rule + ": " + P_RULE_UNUSED);

        if (w.countNations() != w.nations().size()) p.add("counts.nations is " + w.countNations() + " " + P_COUNT + " " + w.nations().size());
        if (w.countRegions() != w.regions().size()) p.add("counts.regions is " + w.countRegions() + " " + P_COUNT + " " + w.regions().size());
        if (w.countFigures() != figures) p.add("counts.figures is " + w.countFigures() + " " + P_COUNT + " " + figures);
        if (w.countEstimates() != estimates) p.add("counts.estimates is " + w.countEstimates() + " " + P_COUNT + " " + estimates);
        return p;
    }

    /** What is wrong with one figure besides its value: its year, its mark, its source and its rule. */
    static void figureProblems(String at, WorldMeasures.Figure f, WorldMeasures w, List<String> p, Set<String> usedSources, Set<String> usedRules) {
        if (f.year() < w.windowFirst() || f.year() > w.windowLast()) p.add(at + "year " + f.year() + " " + P_WINDOW);
        boolean markKnown = f.mark() >= 1 && f.mark() <= 3;
        if (!markKnown) p.add(at + "mark " + f.mark() + " " + P_MARK);
        else if (!w.marks().containsKey(f.mark())) p.add(at + "mark " + f.mark() + " " + P_MARK_UNLISTED);
        if (f.source() == null || !w.sources().containsKey(f.source())) p.add(at + "source " + f.source() + " " + P_SOURCE);
        else usedSources.add(f.source());
        if (!markKnown) return;
        if (f.mark() == 1) {
            if (f.rule() == null) p.add(at + "the figure " + P_RULE_MISSING);
            else if (!w.rules().containsKey(f.rule())) p.add(at + "rule " + f.rule() + " " + P_RULE_UNKNOWN);
            else usedRules.add(f.rule());
        } else if (f.rule() != null) {
            p.add(at + "the figure (mark " + f.mark() + ") " + P_RULE_SPARE + " " + f.rule());
        }
    }

    /** A double to 17 significant digits, which tells any two doubles apart. */
    static String digits17(double v) {
        return new BigDecimal(v).round(new MathContext(17)).toPlainString();
    }

    /* =====================================================================
       THE DOCTORED COPIES
       ===================================================================== */

    /** The real JSON parsed into an object, changed in one place by the change, and read again as a file. */
    static WorldMeasures doctored(String json, Consumer<JsonObject> change) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        change.accept(root);
        return WorldMeasures.parse(root.toString());
    }

    static JsonObject meta(JsonObject root) { return root.getAsJsonObject("meta"); }

    static JsonObject placeOf(JsonObject root, String id) {
        for (JsonElement e : root.getAsJsonArray("places")) {
            if (e.getAsJsonObject().get("id").getAsString().equals(id)) return e.getAsJsonObject();
        }
        throw new IllegalStateException("no place " + id + " in the real file");
    }

    static JsonArray figureOf(JsonObject root, String id, String key) {
        JsonElement f = placeOf(root, id).getAsJsonObject("f").get(key);
        if (f == null) throw new IllegalStateException(id + " has no " + key + " in the real file");
        return f.getAsJsonArray();
    }

    /** A figure added to a place, with the meta's figure count one more so that the count is not what the copy breaks. */
    static void addFigure(JsonObject root, String id, String key, double value, int year, int mark, String source) {
        JsonArray a = new JsonArray();
        a.add(value);
        a.add(year);
        a.add(mark);
        a.add(source);
        placeOf(root, id).getAsJsonObject("f").add(key, a);
        JsonObject counts = meta(root).getAsJsonObject("counts");
        counts.addProperty("figures", counts.get("figures").getAsInt() + 1);
    }

    /** Whether the copy has exactly one problem, and it says the words; prints what it has when it does not. */
    static void caught(String label, WorldMeasures copy, String... words) {
        List<String> pr = problems(copy);
        boolean ok = pr.size() == 1;
        for (String word : words) ok &= pr.size() == 1 && pr.get(0).contains(word);
        check(label, ok);
        if (!ok) for (int i = 0; i < Math.min(3, pr.size()); i++) out.println("      got: " + pr.get(i));
    }

    /** Whether the copy has problems, and every one says the words (a change that breaks many figures alike). */
    static void caughtAll(String label, WorldMeasures copy, String word) {
        List<String> pr = problems(copy);
        boolean ok = !pr.isEmpty() && pr.stream().allMatch(s -> s.contains(word));
        check(label + " (" + pr.size() + ")", ok);
        if (!ok) for (int i = 0; i < Math.min(3, pr.size()); i++) out.println("      got: " + pr.get(i));
    }

    /** Whether the real file has no problem of a kind. */
    static void none(String label, List<String> real, String word) {
        List<String> bad = real.stream().filter(s -> s.contains(word)).toList();
        check(label, bad.isEmpty());
        for (int i = 0; i < Math.min(5, bad.size()); i++) out.println("      " + bad.get(i));
    }

    /* =====================================================================
       THE SECTIONS
       ===================================================================== */

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        byte[] bytes = WorldDataCheck.resource(WorldMeasures.RESOURCE);
        if (bytes == null) {
            check("no file: " + WorldMeasures.RESOURCE + " is not beside the classes", false);
            out.println("\n" + fails + " FAILED");
            System.exit(1);
        }
        String json = new String(bytes, StandardCharsets.UTF_8);
        WorldMeasures w = WorldMeasures.load();
        List<String> real = problems(w);
        out.printf("%s, made %s: %d nations, %d city regions, %d figures, %d of them estimates%n",
                w.name(), w.made(), w.countNations(), w.countRegions(), w.countFigures(), w.countEstimates());

        theFile(w, bytes, real);
        theNations(w);
        theMissing(w, json, real);
        theMarksAndSources(w, json, real);
        theShares(w, json, real);
        theCpi(w, json, real);
        theMoney(w);

        out.printf("%nWorldMeasuresCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** 1. The file loads, is the size and shape FORMAT-2025.txt says, and keeps all its rules. */
    static void theFile(WorldMeasures w, byte[] bytes, List<String> real) {
        out.println("--- 1. the file ---");
        check("it loads from the resources, once: load() twice is one instance", WorldMeasures.load() == w);
        check("its name is world-2025, its year 2025 and its window 2015-2025", w.name().equals("world-2025") && w.year() == 2025 && w.windowFirst() == 2015 && w.windowLast() == 2025);
        check("the resource is " + bytes.length + " bytes, under " + MAX_BYTES, bytes.length < MAX_BYTES);

        List<String> keys = new ArrayList<>(), runs = new ArrayList<>();
        String group = null;
        int n = 0;
        for (WorldMeasures.Measure m : w.measures()) {
            keys.add(m.key());
            if (m.group().equals(group)) n++;
            else {
                if (group != null) runs.add(group + " " + n);
                group = m.group();
                n = 1;
            }
        }
        if (group != null) runs.add(group + " " + n);
        check("it has 70 measures with unique keys (" + w.measures().size() + ")", w.measures().size() == 70 && new HashSet<>(keys).size() == 70);
        check("in ten groups, in this order: " + String.join(", ", GROUPS), runs.equals(GROUPS));
        if (!runs.equals(GROUPS)) out.println("      got: " + String.join(", ", runs));
        long nc = w.measures().stream().filter(WorldMeasures.Measure::forRegions).count();
        long y = w.measures().stream().filter(WorldMeasures.Measure::yoursOnly).count();
        check(nc + " measures are scope NC and " + y + " scope Y (" + SCOPE_NC + " and " + SCOPE_Y + ")", nc == SCOPE_NC && y == SCOPE_Y);
        List<String> mine = w.measures().stream().filter(WorldMeasures.Measure::yourWorld).map(WorldMeasures.Measure::key).toList();
        check("exactly " + YOUR_WORLD.size() + " are on Your world, and they are " + String.join(" ", YOUR_WORLD) + " (spec-leaderboards D15)", mine.equals(YOUR_WORLD));
        if (!mine.equals(YOUR_WORLD)) out.println("      got: " + mine);
        check("every POSITIVE key is a measure or a variant, so the test of section 3 can see it", POSITIVE.stream().allMatch(k -> w.measure(k) != null || w.variants().containsKey(k)));
        List<String> unseen = POSITIVE.stream().filter(k -> w.places().stream().noneMatch(p -> p.figure(k) != null)).sorted().toList();
        check("and every POSITIVE key has a figure somewhere, so that it tests something", unseen.isEmpty());
        if (!unseen.isEmpty()) out.println("      without a figure: " + unseen);
        check("problems() of the real file is empty (" + real.size() + ")", real.isEmpty());
        for (int i = 0; i < Math.min(5, real.size()); i++) out.println("      " + real.get(i));
    }

    /** 2. Every nation of world-1.bin is there once, with its name. */
    static void theNations(WorldMeasures w) throws Exception {
        out.println("--- 2. every nation of world-1.bin is present once ---");
        List<WorldMeasures.Place> nations = w.nations();
        List<String> ids = nations.stream().map(WorldMeasures.Place::id).toList();
        check("there are " + NATIONS + " nation places (" + nations.size() + ")", nations.size() == NATIONS);
        check("every place id is unique, and the nations come first", new HashSet<>(w.places().stream().map(WorldMeasures.Place::id).toList()).size() == w.places().size()
                && w.places().subList(0, nations.size()).equals(nations));
        check("the nations are in ISO3 order, A to Z", ids.equals(new ArrayList<>(new TreeSet<>(ids))));
        check("no city region has a nation's id", w.regions().stream().noneMatch(r -> ids.contains(r.id())));

        try (WorldData fixture = WorldData.of(WorldDataCheck.resource(WorldData.FIXTURE))) {
            presence(w, "the fixture's", fixture, false);
        }
        Path full = WorldData.locate();
        String named = System.getProperty(WorldData.PROPERTY);
        if (named != null && !named.isBlank() && !Files.isRegularFile(full)) {
            check("the file -D" + WorldData.PROPERTY + " names is there (" + named + ")", false);
        } else if (full == null) {
            out.println("world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": the full file's nations were not compared");
        } else {
            out.println("reading " + full.toAbsolutePath());
            try (WorldData data = WorldData.open(full)) {
                presence(w, "the full file's", data, true);
            }
        }
    }

    /** Every nation of a world-1 file is in the measures file once, with its name; for the full file, none is there that the file lacks. */
    static void presence(WorldMeasures w, String of, WorldData data, boolean full) {
        List<String> bad = new ArrayList<>();
        int counted = 0;
        Set<String> theirs = new HashSet<>();
        for (WorldData.Nation n : data.nations()) {
            if (!n.isNation()) continue;
            counted++;
            theirs.add(n.iso3());
            long times = w.nations().stream().filter(p -> p.id().equals(n.iso3())).count();
            WorldMeasures.Place p = w.place(n.iso3());
            if (times != 1) bad.add(n.iso3() + " is there " + times + " times");
            else if (!n.name().equals(p.name())) bad.add(n.iso3() + " is " + p.name() + ", world-1's " + n.name());
        }
        all(of + " nations are each there once with world-1's name", counted, bad);
        if (full) {
            List<String> extra = w.nations().stream().map(WorldMeasures.Place::id).filter(id -> !theirs.contains(id)).toList();
            check("and none is there that world-1.bin lacks (" + extra.size() + " extra, " + theirs.size() + " in the file)", extra.isEmpty() && theirs.size() == w.nations().size());
        }
    }

    /** 3. A figure the sources do not have is absent, never 0. */
    static void theMissing(WorldMeasures w, String json, List<String> real) {
        out.println("--- 3. a missing figure is absent, never 0 ---");
        WorldMeasures.Place twn = w.place("TWN");
        check("Taiwan (the WDI does not cover it) is a nation place and carries no figure from the WDI",
                twn != null && twn.kind() == WorldMeasures.Kind.NATION && twn.figures().values().stream().noneMatch(f -> "wdi".equals(f.source())));
        boolean world1 = twn != null;
        for (String key : List.of("gdp", "gdp.k", "gdp.ppp", "gdp_pp", "gdp_pp.k", "gdp_pp.ppp", "people")) {
            world1 &= twn != null && twn.figure(key) != null && "world-1".equals(twn.figure(key).source());
        }
        check("but its GDP (each money), its GDP per person (each) and its people are there, from world-1", world1);
        check("its PPP figures are its market ones (rule ppp-market)", twn != null && twn.figure("gdp.ppp") != null && twn.figure("gdp_pp.ppp") != null
                && "ppp-market".equals(twn.figure("gdp.ppp").rule()) && "ppp-market".equals(twn.figure("gdp_pp.ppp").rule()));

        int figures = w.places().stream().mapToInt(p -> p.figures().size()).sum();
        none("no place carries a figure of a measure nothing publishes (source none:), in " + figures + " figures", real, P_NO_SOURCE);
        none("no place carries a figure of a measure only the player's cities rank (scope Y)", real, P_Y_ONLY);
        none("a city region carries only city-region measures and the GDP variants", real, P_REGION_SCOPE);
        none("and every figure's key is a measure or a variant", real, P_NOT_A_MEASURE);
        check("the regions carry figures at all (" + w.regions().stream().mapToInt(p -> p.figures().size()).sum() + ")", w.regions().stream().allMatch(p -> !p.figures().isEmpty()));
        none("every figure's value is a finite number", real, P_NOT_FINITE);
        none("every figure of the " + POSITIVE.size() + " measures that cannot be 0 is over 0", real, P_NOT_POSITIVE);

        caught("doctored: a measure twice in the file is caught", doctored(json, r -> {
            JsonArray list = meta(r).getAsJsonArray("measures");
            list.add(list.get(0).deepCopy());
        }), "gdp", P_DUP_MEASURE);
        caught("doctored: a nation's GDP of 0 is caught", doctored(json, r -> figureOf(r, DOCTORED_NATION, "gdp").set(0, new JsonPrimitive(0))), DOCTORED_NATION + " gdp", P_NOT_POSITIVE);
        caught("doctored: a figure of a measure nothing publishes (fund) is caught", doctored(json, r -> addFigure(r, DOCTORED_NATION, "fund", 1.5, 2024, 3, "wdi")), DOCTORED_NATION + " fund", P_NO_SOURCE);
        caught("doctored: a figure of a measure only the player's cities rank (orphans) is caught", doctored(json, r -> addFigure(r, DOCTORED_NATION, "orphans", 1.5, 2024, 3, "wdi")), DOCTORED_NATION + " orphans", P_Y_ONLY);
        caught("doctored: a measure for nations only (investment) on a city region is caught", doctored(json, r -> addFigure(r, DOCTORED_REGION, "investment", 17.5, 2021, 3, "oecd")), DOCTORED_REGION + " investment", P_REGION_SCOPE);
        caught("doctored: a key that is no measure or variant is caught", doctored(json, r -> addFigure(r, DOCTORED_NATION, "nonsense", 1.5, 2024, 3, "wdi")), DOCTORED_NATION + " nonsense", P_NOT_A_MEASURE);
        caught("doctored: a value that is not a number is caught", doctored(json, r -> figureOf(r, DOCTORED_NATION, "gdp").set(0, new JsonPrimitive(Double.NaN))), DOCTORED_NATION + " gdp", P_NOT_FINITE);
        caught("doctored: a year before the window is caught", doctored(json, r -> figureOf(r, DOCTORED_NATION, "gdp").set(1, new JsonPrimitive(2014))), DOCTORED_NATION + " gdp", P_WINDOW);
    }

    /** 4. Every mark and source resolves; every source names its licence; the meta's counts are a recount. */
    static void theMarksAndSources(WorldMeasures w, String json, List<String> real) {
        out.println("--- 4. every mark and source resolves, and every source names its licence ---");
        none("every figure's mark is 1, 2 or 3", real, P_MARK);
        none("and it is in meta.marks (" + w.marks().keySet() + ")", real, P_MARK_UNLISTED);
        none("every figure's source is in meta.sources (" + w.sources().size() + ")", real, P_SOURCE);
        none("a mark 1 figure has a rule in meta.rules (" + w.rules().size() + ")", real, P_RULE_MISSING);
        none("and its rule is one of them", real, P_RULE_UNKNOWN);
        none("a mark 2 or 3 figure has no rule", real, P_RULE_SPARE);
        none("every source has a licence", real, P_LICENCE);
        none("and a url", real, P_URL);
        none("no source is the IMF's World Economic Outlook (spec-nations 3.3)", real, P_WEO);
        none("every source in the meta is named by a figure or a share (bls by cpi_u)", real, P_SOURCE_UNUSED);
        none("every rule in the meta is made by a figure or a share", real, P_RULE_UNUSED);
        none("the meta's counts of nations, regions, figures and estimates are a recount of the places", real, P_COUNT);

        caught("doctored: a figure of source imf is caught", doctored(json, r -> figureOf(r, DOCTORED_NATION, "gdp").set(3, new JsonPrimitive("imf"))), DOCTORED_NATION + " gdp", "imf", P_SOURCE);
        caught("doctored: a mark 1 figure with its rule removed is caught", doctored(json, r -> figureOf(r, "TWN", "gdp").remove(4)), "TWN gdp", P_RULE_MISSING);
        caught("doctored: a mark 3 figure that carries a rule is caught", doctored(json, r -> figureOf(r, DOCTORED_NATION, "gdp").add("world-1")), DOCTORED_NATION + " gdp", P_RULE_SPARE);
        caught("doctored: a mark 1 figure whose rule is not in the meta is caught", doctored(json, r -> figureOf(r, "TWN", "gdp").set(4, new JsonPrimitive("zzz"))), "TWN gdp", "zzz", P_RULE_UNKNOWN);
        caught("doctored: mark 4 is caught", doctored(json, r -> figureOf(r, DOCTORED_NATION, "gdp").set(2, new JsonPrimitive(4))), DOCTORED_NATION + " gdp", "mark 4", P_MARK);
        caught("doctored: a source's licence blanked is caught", doctored(json, r -> meta(r).getAsJsonObject("sources").getAsJsonObject("wdi").addProperty("licence", "  ")), "source wdi", P_LICENCE);
        caught("doctored: a source's url blanked is caught", doctored(json, r -> meta(r).getAsJsonObject("sources").getAsJsonObject("wdi").addProperty("url", "")), "source wdi", P_URL);
        caught("doctored: a source named the IMF's World Economic Outlook is caught", doctored(json, r -> meta(r).getAsJsonObject("sources").getAsJsonObject("eia").addProperty("name", "IMF World Economic Outlook")), "source eia", P_WEO);
        caught("doctored: a source no figure names is caught", doctored(json, r -> {
            JsonObject s = new JsonObject();
            s.addProperty("name", "Spare");
            s.addProperty("url", "https://example.org");
            s.addProperty("licence", "CC BY 4.0");
            meta(r).getAsJsonObject("sources").add("zzz", s);
        }), "source zzz", P_SOURCE_UNUSED);
        caught("doctored: a rule no figure makes is caught", doctored(json, r -> meta(r).getAsJsonObject("rules").addProperty("zzz", "A spare rule.")), "rule zzz", P_RULE_UNUSED);
        caughtAll("doctored: a mark taken out of meta.marks is caught on every figure of it", doctored(json, r -> meta(r).getAsJsonObject("marks").remove("2")), "mark 2 " + P_MARK_UNLISTED);
        caught("doctored: a figure count that is not the recount is caught", doctored(json, r -> {
            JsonObject c = meta(r).getAsJsonObject("counts");
            c.addProperty("figures", c.get("figures").getAsInt() + 1);
        }), "counts.figures", P_COUNT);
        caught("doctored: an estimate count that is not the recount is caught", doctored(json, r -> {
            JsonObject c = meta(r).getAsJsonObject("counts");
            c.addProperty("estimates", c.get("estimates").getAsInt() - 1);
        }), "counts.estimates", P_COUNT);
    }

    /** 5. A city region's shares of its nation are under 1. */
    static void theShares(WorldMeasures w, String json, List<String> real) {
        out.println("--- 5. a city region's shares of its nation are under 1 ---");
        long oecd = w.regions().stream().filter(r -> "oecd".equals(r.def())).count();
        long wup = w.regions().stream().filter(r -> "wup".equals(r.def())).count();
        check("there are city regions (" + w.regions().size() + "), of both boundaries (" + oecd + " oecd, " + wup + " wup)", w.regions().size() == w.countRegions() && oecd > 0 && wup > 0 && oecd + wup == w.regions().size());
        none("every region's nation is a nation place", real, P_REGION_NATION);
        none("and its boundary is in meta.definitions (" + w.definitions().keySet() + ")", real, P_REGION_DEF);
        none("it has both shares", real, P_SHARE_MISSING);
        none("share.people and share.output are each over 0 and under 1", real, P_SHARE_RANGE);
        none("and a region has fewer people than its nation", real, P_REGION_PEOPLE);

        caught("doctored: a share of 1.2 is caught", doctored(json, r -> placeOf(r, DOCTORED_REGION).getAsJsonObject("share").getAsJsonArray("output").set(0, new JsonPrimitive(1.2))), DOCTORED_REGION + " share.output", P_SHARE_RANGE);
        caught("doctored: a region without its output share is caught", doctored(json, r -> placeOf(r, DOCTORED_REGION).getAsJsonObject("share").remove("output")), DOCTORED_REGION + " share.output", P_SHARE_MISSING);
        caught("doctored: a share of 0 is caught", doctored(json, r -> placeOf(r, DOCTORED_REGION).getAsJsonObject("share").getAsJsonArray("people").set(0, new JsonPrimitive(0))), DOCTORED_REGION + " share.people", P_SHARE_RANGE);
        caught("doctored: a region whose nation is XXX is caught", doctored(json, r -> placeOf(r, DOCTORED_REGION).addProperty("nation", "XXX")), DOCTORED_REGION, "XXX", P_REGION_NATION);
        caught("doctored: a region whose boundary is not defined is caught", doctored(json, r -> placeOf(r, DOCTORED_REGION).addProperty("def", "xxx")), DOCTORED_REGION, P_REGION_DEF);
        caught("doctored: a region with a nation's id is caught", doctored(json, r -> placeOf(r, DOCTORED_REGION).addProperty("id", "AFG")), "AFG", P_REGION_ID);
        caught("doctored: a region with more people than its nation is caught", doctored(json, r -> figureOf(r, DOCTORED_REGION, "people").set(0, new JsonPrimitive(1.0e12))), DOCTORED_REGION, P_REGION_PEOPLE);
    }

    /** 6. The 2015-over-2025 ratio is the CPI-U's, to the digit. */
    static void theCpi(WorldMeasures w, String json, List<String> real) {
        out.println("--- 6. the 2015-over-2025 ratio equals the CPI-U's to the digit ---");
        check("the file's CPI-U of 2015 is the BLS's " + CPI_U_2015 + " and of 2025 " + CPI_U_2025 + " (" + w.cpiU(2015) + ", " + w.cpiU(2025) + "), exactly", w.cpiU(2015) == CPI_U_2015 && w.cpiU(2025) == CPI_U_2025);
        double ratio = CPI_U_2015 / CPI_U_2025;
        out.println("  the ratio the file carries " + digits17(w.usPrices2015Over2025()) + ", the CPI-U's divided " + digits17(ratio));
        check("us_prices_2015_over_2025 is the CPI-U's 2015 over 2025, to the last bit", w.usPrices2015Over2025() == ratio);
        check("and under 1, as prices rose", ratio < 1 && w.usPrices2015Over2025() < 1);
        boolean years = true;
        for (int y = w.windowFirst(); y <= w.windowLast(); y++) years &= !Double.isNaN(w.cpiU(y));
        check("it carries a CPI-U for every year of the window, " + w.windowFirst() + " to " + w.windowLast() + " (" + w.cpiYears().size() + " years)", years && w.cpiYears().size() == w.windowLast() - w.windowFirst() + 1);
        boolean rising = true;
        for (int y = w.windowFirst() + 1; y <= w.windowLast(); y++) rising &= w.cpiU(y) > w.cpiU(y - 1);
        check("and it rises from " + w.windowFirst() + " to " + w.windowLast() + " year by year (" + w.cpiU(w.windowFirst()) + " to " + w.cpiU(w.windowLast()) + ")", rising);
        check("a year it does not carry is NaN, not 0 (2014)", Double.isNaN(w.cpiU(2014)));
        none("the series is " + CPI_SERIES, real, P_CPI_SERIES);
        none("and the CPI-U raises no problem of any kind", real, "cpi_u ");

        double nudged = Math.nextUp(w.usPrices2015Over2025());
        caught("doctored: the ratio changed in its last digit is caught", doctored(json, r -> meta(r).addProperty("us_prices_2015_over_2025", nudged)), "us_prices_2015_over_2025", P_RATIO);
        caught("doctored: another series is caught", doctored(json, r -> meta(r).getAsJsonObject("cpi_u").addProperty("series", "CUSR0000SA0")), "cpi_u", P_CPI_SERIES);
        caught("doctored: a CPI-U year taken out is caught", doctored(json, r -> meta(r).getAsJsonObject("cpi_u").remove("2018")), "cpi_u 2018", P_CPI_YEAR);
        caught("doctored: a CPI-U year that falls is caught", doctored(json, r -> meta(r).getAsJsonObject("cpi_u").addProperty("2020", 200.0)), "cpi_u 2020", P_CPI_RISE);
        caught("doctored: a CPI-U of 2025 that is not the BLS's is caught", doctored(json, r -> {
            meta(r).getAsJsonObject("cpi_u").addProperty("2025", 322.0);
            meta(r).addProperty("us_prices_2015_over_2025", CPI_U_2015 / 322.0);
        }), "cpi_u 2025", P_CPI_CONSTANT);
    }

    /** 7. The money figures agree with each other: the builder put each series in its slot. */
    static void theMoney(WorldMeasures w) {
        out.println("--- 7. the money figures agree with each other ---");
        WorldMeasures.Place us = w.place("USA");
        check("the United States' price level and its rate against parity are 1, within " + US_ONE_TOLERANCE,
                us != null && Math.abs(us.value("price_level") - 1) <= US_ONE_TOLERANCE && Math.abs(us.value("fx_vs_parity") - 1) <= US_ONE_TOLERANCE);

        List<String> pppOutside = new ArrayList<>();
        int pppCounted = 0;
        List<String> perPersonOutside = new ArrayList<>();
        int perPersonCounted = 0;
        List<String> bands = new ArrayList<>();
        for (WorldMeasures.Place p : w.nations()) {
            WorldMeasures.Figure gdp = p.figure("gdp"), ppp = p.figure("gdp.ppp"), level = p.figure("price_level"), pp = p.figure("gdp_pp"), people = p.figure("people");
            if (published(gdp) && published(ppp) && published(level) && gdp.year() == ppp.year() && ppp.year() == level.year()) {
                pppCounted++;
                if (!(Math.abs(ppp.value() / gdp.value() * level.value() - 1) <= AGREE_TOLERANCE)) pppOutside.add(p.id());
            }
            if (published(gdp) && published(pp) && published(people) && gdp.year() == pp.year() && pp.year() == people.year()) {
                perPersonCounted++;
                if (!(Math.abs(pp.value() / (gdp.value() / people.value()) - 1) <= AGREE_TOLERANCE)) perPersonOutside.add(p.id());
            }
        }
        for (WorldMeasures.Place p : w.places()) {
            WorldMeasures.Figure people = p.figure("people");
            int want = people == null ? -1 : bandOf(people.value());
            if (p.band() != want) bands.add(p.id() + " band " + p.band() + ", its people " + (people == null ? "absent" : people.value()) + " make " + want);
        }
        agree("nations whose GDP, GDP at PPP and price level are published in one year have GDP at PPP over GDP within " + (int) (AGREE_TOLERANCE * 100) + "% of 1 over the price level", pppCounted, pppOutside);
        agree("nations whose GDP, GDP per person and people are published in one year have GDP per person within " + (int) (AGREE_TOLERANCE * 100) + "% of GDP over people", perPersonCounted, perPersonOutside);
        all("every place's band is the digits of its people (to 6 significant digits) less one, and absent with no people", w.places().size(), bands);

        List<String> bigger = new ArrayList<>();
        int compared = 0;
        for (WorldMeasures.Place r : w.regions()) {
            WorldMeasures.Place nation = w.place(r.nation());
            WorldMeasures.Figure own = r.figure("gdp"), theirs = nation == null ? null : nation.figure("gdp");
            if (own == null || theirs == null || own.year() != theirs.year()) continue;
            compared++;
            if (!(own.value() < theirs.value())) bigger.add(r.id() + " " + own.value() + " against " + nation.id() + " " + theirs.value());
        }
        all("each region's GDP is under its nation's of the same year, where both are of one year", compared, bigger);

        List<String> perPerson = new ArrayList<>();
        int onPeople = 0, onOutput = 0;
        for (WorldMeasures.Place r : w.regions()) {
            boolean shareOfPeople = r.shareOutput() != null && "share-people".equals(r.shareOutput().rule());
            boolean any = r.figure("gdp_pp") != null || r.figure("gdp_pp.k") != null || r.figure("gdp_pp.ppp") != null;
            boolean every = r.figure("gdp_pp") != null && r.figure("gdp_pp.k") != null && r.figure("gdp_pp.ppp") != null;
            if (shareOfPeople) {
                onPeople++;
                if (any) perPerson.add(r.id() + " (output share by people) has a GDP per person");
            }
            if ("oecd".equals(r.def())) {
                onOutput++;
                if (!every) perPerson.add(r.id() + " (oecd) lacks a GDP per person in some money");
            }
        }
        all("a region whose output share is its share of people has no GDP per person (in any money), and every oecd region has all three (" + onPeople + " regions on the people's share, " + onOutput + " oecd)", onPeople + onOutput, perPerson);
    }

    /** Whether a figure is there and published (mark 3). */
    static boolean published(WorldMeasures.Figure f) {
        return f != null && f.mark() == 3 && Double.isFinite(f.value()) && f.value() > 0;
    }

    /** The size band of a people figure: the digits of the number, to 6 significant digits, less one. */
    static int bandOf(double people) {
        return new BigDecimal(people).round(new MathContext(6)).toBigInteger().toString().length() - 1;
    }

    /** At least AGREE_SHARE of the counted agree with an identity: prints the count, the share and the ones outside. */
    static void agree(String label, int counted, List<String> outside) {
        double share = counted == 0 ? 0 : 1.0 - outside.size() / (double) counted;
        out.printf("  %d counted, %d outside (%.1f%% agree): %s%n", counted, outside.size(), 100 * share, outside);
        check(label + " (at least " + (int) (AGREE_SHARE * 100) + "% of " + counted + ", of at least " + MIN_COUNTED + ")", counted >= MIN_COUNTED && share >= AGREE_SHARE);
    }
}
