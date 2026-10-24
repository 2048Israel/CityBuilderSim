package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Deposits where the geology says (0.8.12, batch EG1; docs/studies/spec-earth2-terrain.md 8.5): oil by a province's known petroleum, iron on shields and in recorded districts, the Sydney coalfield in its own basin, fields a third as many and three times wider, and every cluster on its own province's ground - and, counted in a real run, how often each rule fires.
 *
 * WHY. Jerus's Nova Scotia cities found oil and iron all round them (the
 * study, 8.1 and 8.3): within 150 km of Halifax the world drew 417 iron
 * fields and 155 oil fields, 102 of them at sea off a fold belt that holds
 * neither (the shelf's sea weight carried into its nearshore water). The study found the code had not changed and
 * the table was applied as designed: the weights only rescaled the generated
 * world's densities within a narrow range (a fold belt's oil weight was one,
 * iron was nought only on a shelf), known petroleum could only raise oil, and
 * a cluster's centre fell anywhere in its 61 km cell (8.4). EG1 changed the
 * weights, the placement and the size of a field; this is the proof that it
 * did what the study said it would, with Nova Scotia as its natural fixture,
 * and the count, in a real run, of how often each rule fires - a rule that
 * never fires proves nothing about the model that has it.
 *
 * THE DATA. EarthCheck's fixture holds the whole geology grid and the whole
 * province table, so 1 to 4 and 7's first half run anywhere; the field counts
 * of 5 and 6 need the whole planet's far layers and the full file (-Dcbs.earth=
 * <path>, EarthData.locate()), and 7's second half on it too; where it is not
 * named each says so in one line and 8 (the save) runs without any file.
 *
 * What this has to prove (spec-earth2-terrain 8.5):
 *   1. THE HAND TABLES STAND ON THE DATA: every KNOWN row's code and name are
 *      the data's, its file figure nought and Geology's known petroleum for
 *      the code the row's; the patch's code is the data's; every district is
 *      20 km wide at the least and on the planet. Row counts printed.
 *   2. OIL FOLLOWS KNOWN PETROLEUM: each USGS code's area on the sphere and
 *      the Earth's known petroleum a km2 recomputed by brute force over the
 *      geology grid are Geology's; each province's oil multiplier is
 *      (OIL_ALLOWANCE x W_OIL[class] + its code's known a km2 over the mean)
 *      x Z_OIL, and a province with none exactly the allowance's.
 *   3. IRON ON SHIELDS AND IN RECORDED DISTRICTS: a shield's multiplier is
 *      W_IRON[SHIELD] x Z_IRON, every other class's IRON_ALLOWANCE x its
 *      weight x Z_IRON; at a district's centre and 1 km inside its radius the
 *      point's is the district's IRON_DISTRICT_WEIGHT x Z_IRON, and 1 km
 *      outside it the province's.
 *   4. THE SYDNEY COALFIELD: Glace Bay, New Waterford and Sydney Mines read
 *      the Sidney Basin (5220), a basin, twice the Appalachian Foldbelt's
 *      coal; Halifax and Antigonish still read the fold belt (5219).
 *   5. NOVA SCOTIA, AS SPEC 8 FOUND IT (full file): the worlds of Jerus's two
 *      saves, their iron within 150 and 300 km at most a tenth of the study's,
 *      Halifax's oil on land likewise, every oil field at sea in a province
 *      whose sea holds oil, the Scotian Shelf's oil above its allowance; and
 *      controls - Mesabi holds more iron than a shield spot outside every
 *      district, Tom Price a tenth of Mesabi's at the least, Calgary oil.
 *   6. FEWER AND BIGGER (full file): every Earth field holds FIELD_SCALE x
 *      EARTH_FIELD_SCALE sites at the least and MAX_SITES at the most, a
 *      pool's field count is its old count over that, and the mean sites of
 *      the fields drawn is that x MEAN_SITES within SIGMAS standard errors -
 *      a generated world's FIELD_SCALE x MEAN_SITES the same way.
 *   7. A CLUSTER STAYS ON ITS PROVINCE'S GROUND: of the fields within 300 km
 *      of the playtest's Rotterdam (the fixture's, and the full file's, the
 *      world LongPlaytest --earth rotterdam plays) and of Halifax's (the full
 *      file's), those on ground that holds none of their resource are at most
 *      what World counted (fieldsOnNone) and under half a percent of the
 *      fields; World's five counters are printed as how often the rule fires,
 *      and the harness's own detector is tried on a sea plot and an oil sea's,
 *      so that a count of nought is a detector that can see.
 *   8. THE SAVE: an Earth save of the format before DEPOSITS_FORMAT is
 *      refused in BEFORE_DEPOSITS_WORDS, one of this format is not, and a
 *      generated save of the format before still reads.
 *
 * Every fixture CAUSES its condition: the places are the ones the study
 * measured, the districts are walked to their edges, the worlds are drawn
 * fresh (new World, so World's counters begin at nought). A figure the model
 * does not give is reported with its numbers, never loosened to pass.
 */
public class EarthDepositsCheck {

    static int fails = 0;
    /** Where the harness prints: the console. */
    static final PrintStream out = System.out;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-100s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /** A sample's mean is the model's within this many of its own standard errors (the sample's, sd over the root of the fields): the same three standard deviations EarthTerrainCheck holds its draws to. */
    static final double SIGMAS = 3;

    /** A province or code area is the brute force's within this share, relative: a billionth (both sum the same cells, only in another order). */
    static final double AREA_REL = 1e-9;

    /** A multiplier is the formula's within this share, relative: a million millionth (the same sums of a few terms). */
    static final double MULT_REL = 1e-12;

    /** The pools whose field counts and lists 6 compares, at least: 200 (spec 8.5's "fewer and bigger" holds on a world, not on a pool). */
    static final int MIN_POOLS = 200;

    /** The cells round the founding site that 5 and 7 draw the fields of, each way: 6, 368 km, past 300 km and the cell the site stands in. */
    static final int REACH_CELLS = 6;

    /** ...and the controls', each way: 2, 123 km, past their 50 km. */
    static final int CONTROL_CELLS = 2;

    /** The seed the controls' worlds are drawn with (spec-earth2-terrain 8.3's controls ran on several; one is enough to count). */
    static final long CONTROL_SEED = 1;

    /** A tenth: how much of what the study found a finding of 5 may leave (spec 8.5: "oil and iron all round them" to a handful). */
    static final double TENTH = 0.1;

    /** The distances 5 counts the fields within, in km (the study's: spec 8.3). */
    static final double[] KM = { 50, 150, 300 };

    /** Every resource drawn in fields. */
    static final Resource[] FIELD_RESOURCES = Arrays.stream(Resource.values()).filter(Resource::inFields).toArray(Resource[]::new);

    /** A spot of the study: its name, where, and the seed of the world there. */
    record Spot(String name, double lat, double lon, long seed) { }

    /** Jerus's two saves (spec-earth2-terrain 8.1): slot 1 and slot 2 as the study read them, by their seeds. */
    static final Spot HALIFAX = new Spot("Halifax", 44.65, -63.60, 758_809_812L);
    static final Spot EASTERN_SHORE = new Spot("Eastern Shore", 45.17, -61.72, 284_719_415L);

    /** The study's counts within 50, 150 and 300 km (spec-earth2-terrain 8.3), by resource: iron, oil (land and sea together), coal. */
    static final int[] HALIFAX_IRON = { 45, 417, 941 }, HALIFAX_OIL = { 14, 155, 661 }, HALIFAX_COAL = { 2, 11, 20 };
    static final int[] SHORE_IRON = { 72, 320, 699 }, SHORE_OIL = { 8, 182, 646 }, SHORE_COAL = { 0, 3, 15 };

    /** ...and, of Halifax's 155 oil fields within 150 km, the 53 on land (spec 8.3: "53 + 102"). */
    static final int HALIFAX_OIL_LAND_150 = 53;

    public static void main(String[] args) throws Exception {
        long t0 = System.nanoTime();
        out.println("--- the fixture ---");
        byte[] bytes = EarthCheck.resource(EarthCheck.FIXTURE);
        check("EarthCheck's fixture is beside the classes (" + EarthCheck.FIXTURE + ")", bytes != null);
        if (bytes == null) {
            out.println("\n" + fails + " FAILED");
            System.exit(1);
        }
        EarthData fixture = EarthData.of(bytes);
        String fixtureId = EarthTerrain.use(fixture);
        Geology geo = Geology.of(fixture);
        EarthData full = openFull();
        String fullId = full == null ? null : EarthTerrain.use(full);
        out.printf("      the fixture's geology: %,d provinces, %,d USGS codes, %d classes in use%n",
                fixture.provinces().size(), codesOf(fixture).size(), classesOf(fixture, geo).size());

        tables(fixture, geo);
        oil(fixture, geo);
        iron(fixture, geo, full);
        sydney(fixture, geo);
        World halifax = null;
        Near halifaxNear = null;
        if (full == null) {
            out.println("\n--- 5 and 6, and 7's second half: the full file ---");
            out.println("earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": Nova Scotia's fields, the size of a field and Halifax's clusters were not drawn");
        } else {
            halifax = new World(World.Key.earth(HALIFAX.seed(), HALIFAX.lat(), HALIFAX.lon(), fullId));
            halifaxNear = new Near(halifax, REACH_CELLS, FIELD_RESOURCES);
            novaScotia(full, fullId, Geology.of(full), halifax, halifaxNear);
            fewerAndBigger(fullId);
        }
        out.println("\n--- 7. a cluster stays on its province's ground: no field on ground that holds none of its resource, beyond what World counted ---");
        playtestsRotterdam(fixture, fixtureId, "the fixture");
        if (full != null) playtestsRotterdam(full, fullId, "the full file");
        if (halifax != null) ground("the full file, Halifax's world", halifax, halifaxNear);
        theSave();

        out.printf("%nrun time %.1f s%n", (System.nanoTime() - t0) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** The full file's reader when it is named or lies at data/earth-2.bin, else null (and a named file that is not there fails). */
    static EarthData openFull() throws Exception {
        Path file = EarthData.locate();
        String named = System.getProperty(EarthData.PROPERTY);
        if (named != null && !named.isBlank() && !Files.isRegularFile(file)) {
            report("the file -Dcbs.earth names is there", false, named);
            return null;
        }
        if (file == null) return null;
        EarthData d = EarthData.open(file);
        String id = EarthTerrain.use(d);
        report("the named file is the one this build reads: EarthData.EARTH_2", id.equals(EarthData.EARTH_2), id);
        return id.equals(EarthData.EARTH_2) ? d : null;
    }

    static Set<String> codesOf(EarthData d) {
        Set<String> codes = new java.util.LinkedHashSet<>();
        for (EarthData.Province p : d.provinces()) codes.add(p.code());
        return codes;
    }

    static Set<Geology.Kind> classesOf(EarthData d, Geology geo) {
        Set<Geology.Kind> kinds = new java.util.LinkedHashSet<>();
        for (EarthData.Province p : d.provinces()) kinds.add(geo.kindOf(p.row()));
        return kinds;
    }

    /** The row of a USGS code's first province in the data's table, 0 for none. */
    static int rowOf(EarthData d, String code) {
        for (EarthData.Province p : d.provinces()) if (p.code().equals(code)) return p.row();
        return 0;
    }

    /* =====================================================================
       1. THE HAND TABLES STAND ON THE DATA
       ===================================================================== */
    static void tables(EarthData data, Geology geo) {
        out.println("\n--- 1. the hand tables stand on the data ---");
        out.printf("      the tables' rows: %d classes (HAND), %d known petroleum (KNOWN), %d iron districts (DISTRICTS), %d raster patches (PATCHES)%n",
                Geology.TABLE.size(), Geology.KNOWN.size(), Geology.DISTRICTS.size(), Geology.PATCHES.size());
        int absent = 0, renamed = 0, figured = 0, unread = 0, repeated = 0;
        Set<String> seen = new HashSet<>();
        for (Geology.Known k : Geology.KNOWN) {
            List<EarthData.Province> rows = new ArrayList<>();
            for (EarthData.Province p : data.provinces()) if (p.code().equals(k.code())) rows.add(p);
            if (rows.isEmpty()) absent++;
            for (EarthData.Province p : rows) {
                if (!p.name().trim().equals(k.name())) renamed++;
                if (p.knownMmboe() != 0) figured++;
            }
            if (geo.known(k.code()) != k.mmboe()) unread++;
            if (!seen.add(k.code())) repeated++;
        }
        report("every KNOWN row's code is a province of the data", absent == 0, absent + " of " + Geology.KNOWN.size() + " rows name a code the data lacks");
        report("...and its name is the data's name for it (every row of the code)", renamed == 0, renamed + " rows of the data are named otherwise");
        report("...and the file's own figure for it is nought, so the table fills only what the file lacks", figured == 0, figured + " rows of the data hold a figure");
        report("...and the known petroleum Geology reads for the code is the row's", unread == 0, unread + " codes read otherwise");
        check("...and no code is in the table twice", repeated == 0);
        int missing = 0;
        for (Geology.Patch p : Geology.PATCHES) if (rowOf(data, p.code()) == 0) missing++;
        report("every patch's code is a province of the data", missing == 0 && !Geology.PATCHES.isEmpty(), Geology.PATCHES.size() + " patch(es), " + missing + " naming a code the data lacks");
        int narrow = 0, off = 0, twice = 0;
        Set<String> names = new HashSet<>();
        double least = Double.MAX_VALUE, most = 0;
        for (Geology.District d : Geology.DISTRICTS) {
            if (d.radiusKm() < 20) narrow++;
            if (!(Math.abs(d.lat()) <= 90) || !(Math.abs(d.lon()) <= 180)) off++;
            if (!names.add(d.name())) twice++;
            least = Math.min(least, d.radiusKm());
            most = Math.max(most, d.radiusKm());
        }
        report("every district is at least 20 km in radius, on the planet, and named once", narrow == 0 && off == 0 && twice == 0 && !Geology.DISTRICTS.isEmpty(),
                String.format("%d districts, radii %.0f to %.0f km; %d narrower than 20 km, %d off the planet, %d named twice", Geology.DISTRICTS.size(), least, most, narrow, off, twice));
    }

    /* =====================================================================
       2. OIL FOLLOWS KNOWN PETROLEUM
       ===================================================================== */

    /** A relative closeness: both nought is equal. */
    static boolean near(double a, double b, double rel) {
        return Math.abs(a - b) <= rel * Math.max(Math.abs(a), Math.abs(b));
    }

    /** The USGS code under a point as the data and the patches give it, by the harness's own reading: a patch's box first, else the raw grid's province; null for none. */
    static String codeAt(EarthData data, double lat, double lon) {
        for (Geology.Patch p : Geology.PATCHES) {
            if (lat >= p.lat0() && lat < p.lat1() && lon >= p.lon0() && lon < p.lon1()) return p.code();
        }
        int row = data.provinceAt(lat, lon);
        EarthData.Province pr = row > 0 ? data.province(row) : null;
        return pr == null ? null : pr.code();
    }

    static void oil(EarthData data, Geology geo) {
        out.println("\n--- 2. oil follows known petroleum: each code's area and the mean by brute force, and each province's multiplier ---");
        int cpd = data.layer("geology").cellsPerDegree;
        double r2 = EarthProj.RADIUS_M / 1000 * (EarthProj.RADIUS_M / 1000), dLon = Math.toRadians(1.0 / cpd);
        Map<String, Double> area = new HashMap<>(), known = new HashMap<>();
        for (EarthData.Province p : data.provinces()) known.merge(p.code(), p.knownMmboe(), Math::max);
        for (Geology.Known k : Geology.KNOWN) known.put(k.code(), k.mmboe());
        double north = 0;
        for (int g = 0; g < 180 * cpd; g++) {
            double lat1 = 90 - g / (double) cpd, lat0 = lat1 - 1.0 / cpd, lat = (lat0 + lat1) / 2;
            double km2 = r2 * dLon * (Math.sin(Math.toRadians(lat1)) - Math.sin(Math.toRadians(lat0)));
            for (int c = 0; c < 360 * cpd; c++) {
                String code = codeAt(data, lat, -180 + (c + 0.5) / cpd);
                if (code == null) continue;
                area.merge(code, km2, Double::sum);
                if (lat >= Geology.ANTARCTIC_LAT) north += km2;
            }
        }
        double sum = 0;
        for (double v : known.values()) sum += v;
        double mean = sum / north;
        int wrong = 0;
        for (String code : codesOf(data)) {
            if (!near(geo.area(code), area.getOrDefault(code, 0.0), AREA_REL)) wrong++;
        }
        report("every code's area on the sphere is the brute force's over the geology grid (the patches in) to AREA_REL", wrong == 0,
                String.format("%d codes, %d differ; the planet's %,.0f km2 under a province north of 60 S", codesOf(data).size(), wrong, north));
        report("the Earth's known petroleum a km2 is the brute force's to AREA_REL", near(geo.knownMean(), mean, AREA_REL),
                String.format("%.6g MMBOE a km2 against %.6g (%,.0f MMBOE over %,.0f km2)", geo.knownMean(), mean, sum, north));

        int rows = 0, bad = 0, none = 0, exact = 0, withKnown = 0;
        double top = 0;
        List<EarthData.Province> ranked = new ArrayList<>();
        for (EarthData.Province p : data.provinces()) {
            rows++;
            Geology.Kind kind = geo.kindOf(p.row());
            double a = geo.area(p.code()), k = geo.known(p.code());
            double density = a > 0 && geo.knownMean() > 0 ? k / a / geo.knownMean() : 0;
            double want = (Geology.OIL_ALLOWANCE * Geology.W_OIL[kind.ordinal()] + density) * Geology.Z_OIL, got = geo.multiplier(Resource.OIL, p.row());
            if (!near(got, want, MULT_REL)) bad++;
            if (k == 0) {
                none++;
                if (got == Geology.OIL_ALLOWANCE * Geology.W_OIL[kind.ordinal()] * Geology.Z_OIL) exact++;
            } else withKnown++;
            top = Math.max(top, got);
            ranked.add(p);
        }
        report("each province's oil multiplier is (OIL_ALLOWANCE x W_OIL[class] + known a km2 over the mean) x Z_OIL to MULT_REL", bad == 0 && rows > 0,
                rows + " provinces, " + bad + " differ");
        report("a province whose code holds no known petroleum has exactly the allowance's, OIL_ALLOWANCE x W_OIL[class] x Z_OIL", exact == none && none > 0,
                none + " provinces on the allowance alone (" + exact + " exactly), " + withKnown + " on known petroleum");
        ranked.sort(Comparator.comparingDouble((EarthData.Province p) -> -geo.multiplier(Resource.OIL, p.row())).thenComparingInt(EarthData.Province::row));
        StringBuilder five = new StringBuilder();
        Set<String> printed = new HashSet<>();
        for (EarthData.Province p : ranked) {
            if (!printed.add(p.code())) continue;
            if (printed.size() > 5) break;
            five.append(String.format("%s %s %.2f; ", p.code(), p.name().trim(), geo.multiplier(Resource.OIL, p.row())));
        }
        out.println("      the five largest oil multipliers (a code once): " + five);
        StringBuilder named = new StringBuilder();
        for (String code : new String[] { "5219", "5217", "4036", "5243", "5044" }) {
            int row = rowOf(data, code);
            named.append(String.format("%s %s %.3f (%s, known %,.0f MMBOE over %,.0f km2); ", code, row == 0 ? "-" : data.province(row).name().trim(),
                    geo.multiplier(Resource.OIL, row), geo.kindOf(row).label(), geo.known(code), geo.area(code)));
        }
        out.println("      the study's: " + named);
        double appalachian = geo.multiplier(Resource.OIL, rowOf(data, "5219")), allowance = Geology.OIL_ALLOWANCE * Geology.W_OIL[Geology.Kind.FOLD_BELT.ordinal()] * Geology.Z_OIL;
        report("the Appalachian Foldbelt (5219), with no known petroleum, is a fold belt's allowance and not a Zagros", appalachian == allowance && geo.known("5219") == 0,
                String.format("%.4f, an allowance of %.4f; the Anglo-Dutch Basin (4036) %.2f", appalachian, allowance, geo.multiplier(Resource.OIL, rowOf(data, "4036"))));
    }

    /* =====================================================================
       3. IRON ON SHIELDS AND IN RECORDED DISTRICTS
       ===================================================================== */

    /** The point `km` due east of (lat, lon) on the sphere of EarthProj.RADIUS_M: {lat, lon}, the longitude in -180 to 180. */
    static double[] east(double lat, double lon, double km) {
        double d = km * 1000 / EarthProj.RADIUS_M, la = Math.toRadians(lat);
        double la2 = Math.asin(Math.sin(la) * Math.cos(d));
        double lo = Math.toRadians(lon) + Math.atan2(Math.sin(d) * Math.cos(la), Math.cos(d) - Math.sin(la) * Math.sin(la2));
        double deg = Math.toDegrees(lo);
        deg = ((deg + 180) % 360 + 360) % 360 - 180;
        return new double[] { Math.toDegrees(la2), deg };
    }

    static void iron(EarthData data, Geology geo, EarthData full) {
        out.println("\n--- 3. iron on shields and in recorded districts ---");
        EnumMap<Geology.Kind, Integer> rows = new EnumMap<>(Geology.Kind.class);
        int bad = 0;
        for (EarthData.Province p : data.provinces()) {
            Geology.Kind k = geo.kindOf(p.row());
            double base = k == Geology.Kind.SHIELD ? Geology.W_IRON[Geology.Kind.SHIELD.ordinal()] : Geology.IRON_ALLOWANCE * Geology.W_IRON[k.ordinal()];
            if (!near(geo.multiplier(Resource.IRON, p.row()), base * Geology.Z_IRON, MULT_REL)) bad++;
            rows.merge(k, 1, Integer::sum);
        }
        StringBuilder classes = new StringBuilder();
        for (Geology.Kind k : Geology.Kind.values()) {
            int row = 0;
            for (EarthData.Province p : data.provinces()) if (geo.kindOf(p.row()) == k) { row = p.row(); break; }
            if (row > 0) classes.append(String.format("%s %d provinces x%.4f; ", k.label(), rows.getOrDefault(k, 0), geo.multiplier(Resource.IRON, row)));
        }
        report("a shield's iron multiplier is W_IRON[shield] x Z_IRON and every other class's IRON_ALLOWANCE x its weight x Z_IRON", bad == 0 && !rows.isEmpty(),
                bad + " provinces differ; " + classes);
        double shield = geo.multiplier(Resource.IRON, firstOf(data, geo, Geology.Kind.SHIELD)), fold = geo.multiplier(Resource.IRON, firstOf(data, geo, Geology.Kind.FOLD_BELT));
        report("...so a shield's iron is forty times a fold belt's (4 against a tenth)", near(shield / fold, 4 / Geology.IRON_ALLOWANCE, 1e-9), String.format("%.4f against %.4f", shield, fold));

        int centresBad = 0, insideBad = 0, outsideBad = 0, skipped = 0, tested = 0, raised = 0;
        double district = Geology.IRON_DISTRICT_WEIGHT * Geology.Z_IRON;
        for (Geology.District d : Geology.DISTRICTS) {
            int centreRow = geo.provinceAt(d.lat(), d.lon());
            if (geo.districtAt(d.lat(), d.lon()) < 0
                    || geo.multiplierAt(Resource.IRON, centreRow, d.lat(), d.lon()) != Math.max(geo.multiplier(Resource.IRON, centreRow), district)) centresBad++;
            double[] in = east(d.lat(), d.lon(), d.radiusKm() - 1), outside = east(d.lat(), d.lon(), d.radiusKm() + 1);
            int inRow = geo.provinceAt(in[0], in[1]);
            double inside = geo.multiplierAt(Resource.IRON, inRow, in[0], in[1]);
            if (geo.districtAt(in[0], in[1]) < 0 || inside != Math.max(geo.multiplier(Resource.IRON, inRow), district)) insideBad++;
            if (inside > geo.multiplier(Resource.IRON, inRow)) raised++;
            if (geo.districtAt(outside[0], outside[1]) >= 0) {
                skipped++;
                continue;
            }
            tested++;
            int outRow = geo.provinceAt(outside[0], outside[1]);
            if (geo.multiplierAt(Resource.IRON, outRow, outside[0], outside[1]) != geo.multiplier(Resource.IRON, outRow)) outsideBad++;
        }
        int n = Geology.DISTRICTS.size();
        report("at each district's centre the point's iron is the greater of its province's and IRON_DISTRICT_WEIGHT x Z_IRON", centresBad == 0, centresBad + " of " + n + " districts differ");
        report("a point 1 km inside a district's radius due east is the district's, and raises the province's iron", insideBad == 0 && raised == n, insideBad + " of " + n + " differ; " + raised + " raised above the province's");
        report("a point 1 km outside it is the province's, no district's", outsideBad == 0 && tested > 0, outsideBad + " of " + tested + " differ; " + skipped + " districts skipped for another district there");
        int cpd = data.layer("geology").cellsPerDegree, cells = 0;
        for (int g = 0; g < 180 * cpd; g++) {
            double lat = 90 - (g + 0.5) / cpd;
            for (int c = 0; c < 360 * cpd; c++) if (geo.districtAt(lat, -180 + (c + 0.5) / cpd) >= 0) cells++;
        }
        String land = "(the far layers are not in hand here for their land)";
        boolean has = cells > 0;
        if (full != null) {
            Geology.Measure m = Geology.of(full).measure();
            land = String.format("holding %,.0f km2 of land, %.2f%% of the %,.0f km2 north of 60 S", m.districtLand(), 100 * m.districtLand() / m.totalLand(), m.totalLand());
            has &= m.districtLand() > 0;
        }
        report("the districts take in some of the geology grid's 0.25 degree cells by their centres, and land", has, cells + " of " + (180 * cpd * 360 * cpd) + " cells " + land);
    }

    /** The first province row of a class. */
    static int firstOf(EarthData data, Geology geo, Geology.Kind k) {
        for (EarthData.Province p : data.provinces()) if (geo.kindOf(p.row()) == k) return p.row();
        return 0;
    }

    /* =====================================================================
       4. THE SYDNEY COALFIELD
       ===================================================================== */
    static void sydney(EarthData data, Geology geo) {
        out.println("\n--- 4. the Sydney coalfield is in its own basin ---");
        double[][] towns = { { 46.20, -59.96 }, { 46.25, -60.08 }, { 46.24, -60.22 } };
        String[] names = { "Glace Bay", "New Waterford", "Sydney Mines" };
        String patch = Geology.PATCHES.get(0).code();
        int fold = rowOf(data, "5219");
        double foldCoal = geo.multiplier(Resource.COAL, fold), basin = Geology.W_COAL[Geology.Kind.BASIN.ordinal()] * Geology.Z_COAL;
        for (int i = 0; i < towns.length; i++) {
            int row = geo.provinceAt(towns[i][0], towns[i][1]), raw = data.provinceAt(towns[i][0], towns[i][1]);
            report(names[i] + " reads province " + patch + ", a basin, where the raster alone reads 5219", data.province(row).code().equals(patch) && geo.kindOf(row) == Geology.Kind.BASIN
                    && data.province(raw).code().equals("5219"), String.format("%s (%s) against the raster's %s", data.province(row).code(), geo.kindOf(row).label(), data.province(raw).code()));
            report("...and its coal multiplier is W_COAL[basin] x Z_COAL, twice the Appalachian Foldbelt's", near(geo.multiplier(Resource.COAL, row), basin, MULT_REL) && near(geo.multiplier(Resource.COAL, row), 2 * foldCoal, MULT_REL)
                    && geo.kindOf(fold) == Geology.Kind.FOLD_BELT, String.format("%.4f against %.4f", geo.multiplier(Resource.COAL, row), foldCoal));
        }
        double[][] elsewhere = { { 44.65, -63.60 }, { 45.62, -61.99 } };
        String[] others = { "Halifax", "Antigonish" };
        for (int i = 0; i < elsewhere.length; i++) {
            int row = geo.provinceAt(elsewhere[i][0], elsewhere[i][1]);
            report(others[i] + " is still the Appalachian Foldbelt's, 5219, off the patch", data.province(row).code().equals("5219"), data.province(row).code());
        }
        int cpd = data.layer("geology").cellsPerDegree, centres = 0, moved = 0;
        Geology.Patch p = Geology.PATCHES.get(0);
        for (int g = 0; g < 180 * cpd; g++) {
            double lat = 90 - (g + 0.5) / cpd;
            if (lat < p.lat0() || lat >= p.lat1()) continue;
            for (int c = 0; c < 360 * cpd; c++) {
                double lon = -180 + (c + 0.5) / cpd;
                if (lon < p.lon0() || lon >= p.lon1()) continue;
                centres++;
                int raw = data.provinceAt(lat, lon);
                if (!data.province(raw).code().equals(p.code())) moved++;
            }
        }
        report("the patch moves some of the grid's cells (by their centres) out of the province the raster put them in", moved > 0 && moved <= centres, moved + " of the " + centres + " cell centres in its box");
    }

    /* =====================================================================
       THE FIELDS ROUND A SITE
       ===================================================================== */

    /** A field near the site: the deposit, its centre plot's distance from the founding site in km, whether that plot is sea, and the province (row) under it. */
    record Field(Deposit d, double km, boolean sea, int row) { }

    /** The fields of some resources in the cells round a world's founding site, counted by their centre plots' distance (the plots' 30 m, the projection's near-site km). */
    static final class Near {
        final World w;
        final Map<Resource, List<Field>> by = new EnumMap<>(Resource.class);

        Near(World w, int reach, Resource... resources) {
            this.w = w;
            long fx = w.foundingX(), fy = w.foundingY();
            int cx0 = (int) (fx / World.CELL), cy0 = (int) (fy / World.CELL);
            EarthTerrain t = w.earthTerrain();
            for (Resource r : resources) {
                List<Field> list = new ArrayList<>();
                for (int cy = Math.max(0, cy0 - reach); cy <= Math.min(World.CELLS - 1, cy0 + reach); cy++) {
                    for (int cx = Math.max(0, cx0 - reach); cx <= Math.min(World.CELLS - 1, cx0 + reach); cx++) {
                        for (Deposit d : w.fieldsInCell(cy * World.CELLS + cx, r)) {
                            double km = Math.hypot(d.x() - fx, d.y() - fy) * World.PLOT_M / 1000;
                            list.add(new Field(d, km, t.seaAt(d.x() + 0.5, d.y() + 0.5), w.provinceAt(d.x(), d.y())));
                        }
                    }
                }
                by.put(r, list);
            }
        }

        /** The fields of a resource within km of the site; `sea` null for all, true for those at sea, false for those on land. */
        List<Field> within(Resource r, double km, Boolean sea) {
            List<Field> out = new ArrayList<>();
            for (Field f : by.get(r)) if (f.km() <= km && (sea == null || f.sea() == sea)) out.add(f);
            return out;
        }

        int count(Resource r, double km) { return within(r, km, null).size(); }

        int land(Resource r, double km) { return within(r, km, false).size(); }

        int sea(Resource r, double km) { return within(r, km, true).size(); }

        int[] counts(Resource r) { return new int[] { count(r, KM[0]), count(r, KM[1]), count(r, KM[2]) }; }
    }

    static String triple(int[] a) { return a[0] + "/" + a[1] + "/" + a[2]; }

    /* =====================================================================
       5. NOVA SCOTIA, AS SPEC 8 FOUND IT
       ===================================================================== */
    static void novaScotia(EarthData full, String id, Geology geo, World halifaxWorld, Near halifax) {
        out.println("\n--- 5. Nova Scotia as the study found it: the full file, the fields within 50, 150 and 300 km of Jerus's two sites ---");
        World shoreWorld = new World(World.Key.earth(EASTERN_SHORE.seed(), EASTERN_SHORE.lat(), EASTERN_SHORE.lon(), id));
        Near shore = new Near(shoreWorld, REACH_CELLS, Resource.IRON, Resource.OIL, Resource.COAL);
        Object[][] spots = { { HALIFAX, halifaxWorld, halifax, HALIFAX_IRON, HALIFAX_OIL, HALIFAX_COAL }, { EASTERN_SHORE, shoreWorld, shore, SHORE_IRON, SHORE_OIL, SHORE_COAL } };
        for (Object[] s : spots) {
            Spot spot = (Spot) s[0];
            Near near = (Near) s[2];
            int[] ironSpec = (int[]) s[3], oilSpec = (int[]) s[4], coalSpec = (int[]) s[5];
            int[] iron = near.counts(Resource.IRON), oil = near.counts(Resource.OIL), coal = near.counts(Resource.COAL);
            out.printf("      %s (seed %d, founded at plot %d, %d): iron %s (spec 8.3 %s); oil %s, of which on land %d/%d/%d and at sea %d/%d/%d (spec %s); coal %s (spec %s)%n",
                    spot.name(), spot.seed(), near.w.foundingX(), near.w.foundingY(), triple(iron), triple(ironSpec), triple(oil),
                    near.land(Resource.OIL, KM[0]), near.land(Resource.OIL, KM[1]), near.land(Resource.OIL, KM[2]),
                    near.sea(Resource.OIL, KM[0]), near.sea(Resource.OIL, KM[1]), near.sea(Resource.OIL, KM[2]), triple(oilSpec), triple(coal), triple(coalSpec));
            report(spot.name() + ": the iron within 150 and 300 km is at most a tenth of the study's", iron[1] <= TENTH * ironSpec[1] && iron[2] <= TENTH * ironSpec[2],
                    String.format("%d within 150 km (a tenth of %d is %.1f), %d within 300 km (a tenth of %d is %.1f)", iron[1], ironSpec[1], TENTH * ironSpec[1], iron[2], ironSpec[2], TENTH * ironSpec[2]));
            List<Field> atSea = near.within(Resource.OIL, KM[2], true);
            int dry = 0;
            Map<String, Integer> where = new TreeMap<>();
            for (Field f : atSea) {
                if (!geo.seaHoldsOil(f.row())) dry++;
                where.merge(full.province(f.row()) == null ? "none" : full.province(f.row()).name().trim() + " (" + geo.kindOf(f.row()).label() + ")", 1, Integer::sum);
            }
            report(spot.name() + ": every oil field at sea within 300 km lies in a province whose sea holds oil, none in a fold belt's water",
                    dry == 0 && (spot != HALIFAX || !atSea.isEmpty()), atSea.size() + " at sea, " + dry + " in water that holds none: " + where);
        }
        int halifaxLand150 = halifax.land(Resource.OIL, KM[1]);
        report("Halifax: the oil on land within 150 km is at most a tenth of the study's " + HALIFAX_OIL_LAND_150, halifaxLand150 <= TENTH * HALIFAX_OIL_LAND_150,
                String.format("%d on land within 150 km (a tenth is %.1f); %d at sea", halifaxLand150, TENTH * HALIFAX_OIL_LAND_150, halifax.sea(Resource.OIL, KM[1])));
        int shelf = rowOf(full, "5217");
        double allowance = Geology.OIL_ALLOWANCE * Geology.W_OIL[geo.kindOf(shelf).ordinal()] * Geology.Z_OIL;
        report("the Scotian Shelf's oil multiplier is above its allowance alone: the hand row for its petroleum fires", geo.multiplier(Resource.OIL, shelf) > allowance && geo.known("5217") > 0,
                String.format("%.4f against an allowance of %.4f (%.0f MMBOE over %,.0f km2, the Earth's mean %.4g)", geo.multiplier(Resource.OIL, shelf), allowance, geo.known("5217"), geo.area("5217"), geo.knownMean()));
        controls(full, id, geo);
    }

    /** The controls, each its own world on CONTROL_SEED: Mesabi, a shield spot outside every district, Tom Price and Calgary. */
    static void controls(EarthData full, String id, Geology geo) {
        Spot mesabi = new Spot("Mesabi", 47.50, -92.50, CONTROL_SEED), tomPrice = new Spot("Tom Price", -22.70, 117.80, CONTROL_SEED), calgary = new Spot("Calgary", 51.05, -114.07, CONTROL_SEED);
        double[] shield = shieldSpot(full, geo, 49.5, -90.0);
        Spot superior = new Spot("a shield spot of the Superior Province", shield[0], shield[1], CONTROL_SEED);
        out.printf("      the shield control: %.2f N %.2f W%s, province %s (%s), no district within %.0f km%n", superior.lat(), -superior.lon(),
                shield[0] == 49.5 && shield[1] == -90.0 ? "" : " (moved from 49.5 N 90.0 W, which is not a shield outside every district)",
                full.province(geo.provinceAt(shield[0], shield[1])).name().trim(), geo.kindOf(geo.provinceAt(shield[0], shield[1])).label(), shield[2]);
        int iron50 = ironIn50(id, mesabi), shield50 = ironIn50(id, superior), price50 = ironIn50(id, tomPrice);
        report("Mesabi's iron within 50 km is more than the shield spot's outside every district", iron50 > shield50, iron50 + " against " + shield50);
        report("Tom Price's iron within 50 km is at least a tenth of Mesabi's", price50 >= TENTH * iron50, price50 + " against a tenth of " + iron50 + " (" + TENTH * iron50 + ")");
        World cw = new World(World.Key.earth(calgary.seed(), calgary.lat(), calgary.lon(), id));
        Near c = new Near(cw, CONTROL_CELLS, Resource.OIL);
        report("Calgary holds oil within 50 km", c.count(Resource.OIL, KM[0]) > 0, c.count(Resource.OIL, KM[0]) + " fields within 50 km, " + c.land(Resource.OIL, KM[0]) + " of them on land (the study 37 to 44)");
    }

    static int ironIn50(String id, Spot s) {
        World w = new World(World.Key.earth(s.seed(), s.lat(), s.lon(), id));
        Near n = new Near(w, CONTROL_CELLS, Resource.IRON);
        int count = n.count(Resource.IRON, KM[0]);
        out.printf("      %s (%.2f, %.2f), seed %d: iron fields within 50 km %d%n", s.name(), s.lat(), s.lon(), s.seed(), count);
        return count;
    }

    /** A shield spot outside every district, nearest the one given: the spot itself if it is one, else the nearest on a 0.5 degree lattice out to 4 degrees; {lat, lon, km to the nearest district's rim}. Far enough (a district's radius and 60 km) that no district's iron reaches a 50 km count about it. */
    static double[] shieldSpot(EarthData data, Geology geo, double lat, double lon) {
        double[] best = null;
        double bestAway = Double.MAX_VALUE;
        for (int ring = 0; ring <= 8; ring++) {
            for (int dy = -ring; dy <= ring; dy++) {
                for (int dx = -ring; dx <= ring; dx++) {
                    if (Math.max(Math.abs(dx), Math.abs(dy)) != ring) continue;
                    double la = lat + dy * 0.5, lo = lon + dx * 0.5;
                    int row = geo.provinceAt(la, lo);
                    if (geo.kindOf(row) != Geology.Kind.SHIELD || geo.districtAt(la, lo) >= 0) continue;
                    double rim = Double.MAX_VALUE;
                    for (Geology.District d : Geology.DISTRICTS) rim = Math.min(rim, greatCircleKm(la, lo, d.lat(), d.lon()) - d.radiusKm());
                    if (rim < 60) continue;
                    double away = Math.hypot(dx, dy);
                    if (away < bestAway) {
                        bestAway = away;
                        best = new double[] { la, lo, rim };
                    }
                }
            }
            if (best != null) return best;
        }
        throw new IllegalStateException("no shield spot outside every district within 4 degrees of " + lat + ", " + lon);
    }

    static double greatCircleKm(double lat1, double lon1, double lat2, double lon2) {
        double a = Math.toRadians(lat1), b = Math.toRadians(lat2), dl = Math.toRadians(lon2 - lon1);
        double h = Math.pow(Math.sin((b - a) / 2), 2) + Math.cos(a) * Math.cos(b) * Math.pow(Math.sin(dl / 2), 2);
        return 2 * EarthProj.RADIUS_M / 1000 * Math.asin(Math.min(1, Math.sqrt(h)));
    }

    /* =====================================================================
       6. FEWER AND BIGGER
       ===================================================================== */

    /** A pool: a resource and its place among that resource's pools. */
    record PoolRef(Resource r, int px, int py) { }

    /** Every pool of a world that holds at least one field, resource by resource, from the pools' counts alone (nothing is drawn). */
    static List<PoolRef> poolsWithFields(World w) {
        List<PoolRef> all = new ArrayList<>();
        for (Resource r : FIELD_RESOURCES) {
            for (int py = 0; py < World.poolsOnSide(r); py++) {
                for (int px = 0; px < World.poolsOnSide(r); px++) if (w.poolFieldCount(r, px, py) > 0) all.add(new PoolRef(r, px, py));
            }
        }
        return all;
    }

    /** What a world's pools say of its fields' size. */
    record Sample(int pools, int counted, int fields, int least, int most, double mean, double se, int outOfRange) { }

    /**
     * Draws evenly spread pools of a world (at least `want`, every resource's) and compares each pool's field count with its old count over `scale`
     * (atLeastOne() at its two ends, u = 1 and u = 0, bound what the pool's draw can give) and with the fields its list holds; the sites of every field
     * they hold are the sample.
     */
    static Sample sample(World w, int scale, int want) {
        List<PoolRef> all = poolsWithFields(w);
        int stride = Math.max(1, all.size() / (want + want / 10));
        int counted = 0, outOfRange = 0, least = Integer.MAX_VALUE, most = 0;
        double sum = 0, sq = 0;
        long n = 0;
        for (int i = 0; i < all.size(); i += stride) {
            PoolRef p = all.get(i);
            long old = w.poolLegacyCount(p.r(), p.px(), p.py()), count = w.poolFieldCount(p.r(), p.px(), p.py());
            List<Deposit> fields = w.poolFields(p.r(), p.px(), p.py());
            if (count < World.atLeastOne(old, scale, 1.0) || count > World.atLeastOne(old, scale, 0.0) || count != fields.size()) outOfRange++;
            counted++;
            for (Deposit d : fields) {
                least = Math.min(least, d.sites());
                most = Math.max(most, d.sites());
                sum += d.sites();
                sq += (double) d.sites() * d.sites();
                n++;
            }
        }
        double mean = sum / n, se = Math.sqrt(Math.max(0, sq / n - mean * mean) / n);
        return new Sample(all.size(), counted, (int) n, least, most, mean, se, outOfRange);
    }

    static void fewerAndBigger(String id) {
        out.println("\n--- 6. fewer and bigger: a field is " + World.FIELD_SCALE * World.EARTH_FIELD_SCALE + " of the old world's, and the old world's draws are a tenth ---");
        World earth = new World(World.Key.earth(HALIFAX.seed(), HALIFAX.lat(), HALIFAX.lon(), id));
        int scale = World.FIELD_SCALE * World.EARTH_FIELD_SCALE;
        Sample e = sample(earth, scale, MIN_POOLS);
        report("Halifax's world: on at least MIN_POOLS pools with fields a pool holds its old count over FIELD_SCALE x EARTH_FIELD_SCALE, and as many fields as its list",
                e.counted() >= MIN_POOLS && e.outOfRange() == 0, String.format("%d pools of %,d with fields checked (%d fields), %d out of range", e.counted(), e.pools(), e.fields(), e.outOfRange()));
        report("...every field holds at least FIELD_SCALE x EARTH_FIELD_SCALE sites, and none more than MAX_SITES", e.least() >= scale && e.most() <= World.MAX_SITES,
                String.format("from %d to %d sites (the limits %d and %,d)", e.least(), e.most(), scale, World.MAX_SITES));
        double want = scale * World.MEAN_SITES;
        report("...and their mean sites is FIELD_SCALE x EARTH_FIELD_SCALE x MEAN_SITES within SIGMAS standard errors of the sample", Math.abs(e.mean() - want) <= SIGMAS * e.se(),
                String.format("%.2f against %.2f, %.2f standard errors of %.2f over %,d fields", e.mean(), want, Math.abs(e.mean() - want) / e.se(), e.se(), e.fields()));
        World generated = new World(Founding.DEFAULT_WORLD_SEED);
        Sample g = sample(generated, World.FIELD_SCALE, MIN_POOLS);
        report("a generated world, for the contrast: its pools hold their old count over FIELD_SCALE, as many fields as their lists",
                g.counted() >= MIN_POOLS && g.outOfRange() == 0, String.format("%d pools of %,d with fields checked (%d fields), %d out of range", g.counted(), g.pools(), g.fields(), g.outOfRange()));
        double wantG = World.FIELD_SCALE * World.MEAN_SITES;
        report("...and the mean sites of its fields is FIELD_SCALE x MEAN_SITES within SIGMAS standard errors, none above FIELD_SCALE x LEGACY_MAX_SITES",
                Math.abs(g.mean() - wantG) <= SIGMAS * g.se() && g.least() >= World.FIELD_SCALE && g.most() <= World.FIELD_SCALE * World.LEGACY_MAX_SITES,
                String.format("%.2f against %.2f, %.2f standard errors of %.2f over %,d fields, from %d to %d sites", g.mean(), wantG, Math.abs(g.mean() - wantG) / g.se(), g.se(), g.fields(), g.least(), g.most()));
    }

    /* =====================================================================
       7. A CLUSTER STAYS ON ITS PROVINCE'S GROUND
       ===================================================================== */

    /** Whether a field lies on ground that holds none of its resource, by the harness's own reading: the province's multiplier there is nought, or the plot is sea and it is not oil in a province whose sea holds oil. */
    static boolean holdsNone(World w, Resource r, Deposit d) {
        EarthTerrain t = w.earthTerrain();
        double x = d.x() + 0.5, y = d.y() + 0.5;
        double[] ll = new double[2];
        if (!t.projection().latLonOfPlot(x, y, ll)) return true;
        Geology geo = w.geology();
        int row = geo.provinceAt(ll[0], ll[1]);
        if (!(geo.multiplierAt(r, row, ll[0], ll[1]) > 0)) return true;
        return t.seaAt(x, y) && !(r == Resource.OIL && geo.seaHoldsOil(row));
    }

    static void ground(String where, World w, Near near) {
        int fields = 0, none = 0;
        StringBuilder by = new StringBuilder();
        for (Resource r : FIELD_RESOURCES) {
            int n = 0, m = 0;
            for (Field f : near.by.get(r)) {
                n++;
                if (holdsNone(w, r, f.d())) m++;
            }
            fields += n;
            none += m;
            by.append(String.format("%s %d/%d; ", r.name().toLowerCase(), m, n));
        }
        report(where + ": the fields within " + (int) KM[2] + " km on ground that holds none of their resource are at most what World counted (fieldsOnNone)",
                fields > 0 && none <= w.fieldsOnNone(), none + " of " + fields + " fields on none (" + by + "), World counted " + w.fieldsOnNone() + " over every pool it drew");
        report(where + ": ...and they are under half a percent of the fields", fields > 0 && none < 0.005 * fields, String.format("%d of %d, %.3f%%", none, fields, fields == 0 ? 0 : 100.0 * none / fields));
        out.printf("      how often the rule fired on %s: %,d clusters had their centre drawn among the cell's points by weight; %,d centres redrawn for lying on none; %,d centres still on none after %d redraws;"
                        + " %,d fields redrawn; %,d fields still on none after %d redraws%n",
                where, w.clustersByWeight(), w.centreRedraws(), w.centresOnNone(), World.SEA_REDRAWS, w.fieldRedraws(), w.fieldsOnNone(), World.SEA_REDRAWS);
        report(where + ": the rule fires - centres were drawn by weight and some redrawn", w.clustersByWeight() > 0, w.clustersByWeight() + " by weight, " + w.centreRedraws() + " centres and " + w.fieldRedraws() + " fields redrawn");
        detector(where, w);
    }

    /**
     * The first plot, stepping a km at a time out from the founding site along eight bearings to 400 km, that is sea (and, for oil, in a province whose sea holds oil): {x, y}, or null for none. What
     * holdsNone() is tried on, so that "none of them lies on ground that holds none" is a detector that can see.
     */
    static long[] seaPlot(World w, boolean oil) {
        EarthTerrain t = w.earthTerrain();
        long fx = w.foundingX(), fy = w.foundingY();
        for (int step = 1; step <= 400; step++) {
            for (int k = 0; k < 8; k++) {
                double a = k * Math.PI / 4, d = step * 1000 / World.PLOT_M;
                long x = fx + Math.round(d * Math.cos(a)), y = fy + Math.round(d * Math.sin(a));
                if (x < 0 || y < 0 || x >= World.SIDE || y >= World.SIDE || !t.seaAt(x + 0.5, y + 0.5)) continue;
                if (!oil || w.geology().seaHoldsOil(w.provinceAt(x, y))) return new long[] { x, y };
            }
        }
        return null;
    }

    /** The detector sees: iron on a sea plot is on ground that holds none, oil on a plot of a sea that holds oil is not, and the model's fieldGround() says the same of both. */
    static void detector(String where, World w) {
        long[] sea = seaPlot(w, false), oilSea = seaPlot(w, true);
        EarthTerrain t = w.earthTerrain();
        if (sea == null || oilSea == null) {
            report(where + ": the detector is tried on a sea plot and an oil sea's within 400 km", false, "none found: " + (sea == null ? "no sea" : "no sea that holds oil"));
            return;
        }
        Deposit iron = new Deposit(Resource.IRON, 0, 0, sea[0], sea[1], 1, 1.0), oil = new Deposit(Resource.OIL, 0, 0, oilSea[0], oilSea[1], 1, 1.0);
        report(where + ": the detector sees iron on a sea plot as ground that holds none, as the model's fieldGround() does",
                holdsNone(w, Resource.IRON, iron) && !t.fieldGround(Resource.IRON, sea[0] + 0.5, sea[1] + 0.5), "plot " + sea[0] + ", " + sea[1] + ", province " + w.provinceAt(sea[0], sea[1]));
        report(where + ": ...and oil on a plot of a sea that holds oil as ground that holds some, as fieldGround() does",
                !holdsNone(w, Resource.OIL, oil) && t.fieldGround(Resource.OIL, oilSea[0] + 0.5, oilSea[1] + 0.5),
                "plot " + oilSea[0] + ", " + oilSea[1] + ", province " + w.provinceAt(oilSea[0], oilSea[1]) + " (" + w.geology().kindOf(w.provinceAt(oilSea[0], oilSea[1])).label() + ")");
    }

    /** The world `LongPlaytest --earth rotterdam` founds on a data file: Rotterdam as the place search finds it, the default world's seed. */
    static void playtestsRotterdam(EarthData data, String id, String which) {
        List<EarthData.Place> hits = EarthSite.search(data, "Rotterdam", 1);
        out.println("      the playtest's Rotterdam on " + which + ": seed " + Founding.DEFAULT_WORLD_SEED + ", as LongPlaytest --earth rotterdam founds it");
        check(which + "'s places find Rotterdam, as the playtest's search does", !hits.isEmpty());
        if (hits.isEmpty()) return;
        World w = new World(World.Key.earth(Founding.DEFAULT_WORLD_SEED, hits.get(0).lat(), hits.get(0).lon(), id));
        Near near = new Near(w, REACH_CELLS, FIELD_RESOURCES);
        int fields = 0;
        for (Resource r : FIELD_RESOURCES) fields += near.by.get(r).size();
        out.printf("      %s at %.4f, %.4f, founded at plot %d, %d: %d fields in the cells within reach%n", hits.get(0).name(), hits.get(0).lat(), hits.get(0).lon(), w.foundingX(), w.foundingY(), fields);
        ground(which + ", the playtest's Rotterdam", w, near);
    }

    /* =====================================================================
       8. THE SAVE
       ===================================================================== */
    static void theSave() {
        out.println("\n--- 8. the save: an Earth city saved before its fields were drawn on the geology is refused, in words ---");
        String before = EarthSite.loadProblem(EarthData.EARTH_2, 51.92, 4.48, EarthSite.DEPOSITS_FORMAT - 1);
        String at = EarthSite.loadProblem(EarthData.EARTH_2, 51.92, 4.48, EarthSite.DEPOSITS_FORMAT);
        check("an Earth save of the format before DEPOSITS_FORMAT is refused in BEFORE_DEPOSITS_WORDS", EarthSite.BEFORE_DEPOSITS_WORDS.equals(before));
        check("...and one at DEPOSITS_FORMAT is not refused in those words", !EarthSite.BEFORE_DEPOSITS_WORDS.equals(at));
        check("this build saves at DEPOSITS_FORMAT or later", GameVersion.SAVE_FORMAT >= EarthSite.DEPOSITS_FORMAT);
        report("a generated save of the format before still reads: the floor is Earth-only, as E7's ice",
                GameVersion.OLDEST_LOADABLE_FORMAT >= EarthSite.DEPOSITS_FORMAT || !GameVersion.isNoLongerReadable(EarthSite.DEPOSITS_FORMAT - 1),
                "the oldest format read is " + GameVersion.OLDEST_LOADABLE_FORMAT + ", DEPOSITS_FORMAT " + EarthSite.DEPOSITS_FORMAT + ", this build's " + GameVersion.SAVE_FORMAT);
    }
}
