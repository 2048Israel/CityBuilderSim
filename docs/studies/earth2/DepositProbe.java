package ham.citybuildersim;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Earth 2 study's deposit probe (docs/studies/spec-earth2-terrain.md, the
 * Nova Scotia check): what the game's own Earth worlds put in the ground round
 * a spot, by resource, type, size, distance, province, class and whether the
 * field's centre is at sea - and what a generated world with the same seed puts
 * round its own founding site. A design aid, not game code: it is not in
 * AllChecks, writes no save and touches no slot; it reads earth-1.bin through
 * the game's classes (World.of(World.Key.earth(...)), the founding path's key)
 * and prints.
 *
 * Build against the model's classes (in this package for World.earthTerrain()):
 *   javac -d OUT -cp CLASSES docs/studies/earth2/DepositProbe.java
 *   java -Xmx3g -cp OUT:CLASSES ham.citybuildersim.DepositProbe path/to/earth-1.bin [mode]
 * Modes: provinces (the provinces under four boxes, their classes and
 * multipliers), expected (the fields the geology alone puts in a disc, by
 * province, land and sea - the provinces under real Nova Scotian deposits
 * first), fields (each spot's fields within 50, 150 and 300 km, and the
 * generated worlds'), all (those three), and verify <rows> <seed> <lat> <lon>
 * (a save's holdings with fields, extracted to a text file, against the
 * fields this build draws for its key). Its output on 0.7.135 and earth-1 is
 * deposit-probe-output.txt beside it.
 */
public class DepositProbe {

    static final Resource[] FIELDS = { Resource.IRON, Resource.OIL, Resource.STONE, Resource.COAL, Resource.COPPER, Resource.URANIUM };
    static final double[] BANDS_KM = { 50, 150, 300 };

    record Spot(String name, double lat, double lon, long seed) { }

    public static void main(String[] a) throws Exception {
        Locale.setDefault(Locale.ROOT);
        EarthData data = EarthData.open(Path.of(a[0]));
        String id = EarthTerrain.use(data);
        Geology geo = Geology.of(data);
        System.out.println("data " + id);

        String only = a.length > 1 ? a[1] : "all";
        if (only.equals("all") || only.equals("provinces")) {
            provinces(data, geo, "Nova Scotia and the Scotian Shelf", 43.0, 47.3, -66.6, -59.4, 0.05);
            provinces(data, geo, "Calgary and the Alberta basin", 50.0, 52.0, -116.0, -112.0, 0.05);
            provinces(data, geo, "Mesabi, Minnesota", 46.8, 48.2, -94.0, -91.0, 0.05);
            provinces(data, geo, "Pilbara", -23.5, -20.5, 116.5, 120.5, 0.05);
        }
        if (only.equals("all") || only.equals("expected")) {
            places(data, geo);
            expected(data, geo, "Halifax", 44.65, -63.6, 150);
            expected(data, geo, "Halifax", 44.65, -63.6, 50);
            expected(data, geo, "Eastern Shore (slot-02)", 45.16948591665112, -61.7246324783713, 50);
            expected(data, geo, "Eastern Shore (slot-02)", 45.16948591665112, -61.7246324783713, 150);
            expected(data, geo, "Calgary", 51.05, -114.07, 150);
        }
        if (only.equals("verify")) {
            // a[2]: a file of a save's holdings with fields, a line each: x0 y0 x1 y1, six sites, six amounts, the row; a[3..5]: seed, lat, lon.
            verify(data, id, Path.of(a[2]), Long.parseLong(a[3]), Double.parseDouble(a[4]), Double.parseDouble(a[5]));
        }
        if (only.equals("all") || only.equals("fields")) {
            List<Spot> spots = new ArrayList<>();
            spots.add(new Spot("Halifax (slot-01's key)", 44.65, -63.6, 758809812L));
            spots.add(new Spot("Eastern Shore, Guysborough Co. (slot-02's key)", 45.16948591665112, -61.7246324783713, 284719415L));
            long[] seeds = { 1L, 42L, 20261012L };
            for (long s : seeds) spots.add(new Spot("Halifax", 44.65, -63.58, s));
            for (long s : seeds) spots.add(new Spot("Calgary", 51.05, -114.07, s));
            for (long s : seeds) spots.add(new Spot("Mesabi (Hibbing)", 47.43, -92.94, s));
            for (long s : seeds) spots.add(new Spot("Pilbara (Tom Price)", -22.69, 117.79, s));
            for (Spot sp : spots) earth(data, geo, id, sp);
            long[] gen = { 758809812L, 284719415L, 1L, 42L, 20261012L };
            for (long s : gen) generated(s);
        }
    }

    /* ----- the provinces under a box: each one's land and sea samples, class and multipliers ----- */

    static void provinces(EarthData data, Geology geo, String title, double lat0, double lat1, double lon0, double lon1, double step) {
        System.out.println();
        System.out.println("=== provinces: " + title + String.format(" (%.2f..%.2f N, %.2f..%.2f E, every %.2f deg)", lat0, lat1, lon0, lon1, step));
        Map<Integer, int[]> n = new TreeMap<>();
        for (double lat = lat0 + step / 2; lat < lat1; lat += step) {
            for (double lon = lon0 + step / 2; lon < lon1; lon += step) {
                int row = geo.provinceAt(lat, lon);
                boolean sea = data.at("sea", lat, lon) >= EarthTerrain.COAST_BELOW;
                int[] c = n.computeIfAbsent(row, k -> new int[2]);
                c[sea ? 1 : 0]++;
            }
        }
        System.out.println(String.format("%5s %6s %-34s %5s %5s %-9s %-9s %8s %5s | %6s %6s %6s %6s %6s %6s", "row", "code", "name", "land", "sea", "rules", "class", "MMBOE", "pf",
                "iron", "oil", "stone", "coal", "copper", "uran"));
        for (Map.Entry<Integer, int[]> e : n.entrySet()) {
            int row = e.getKey();
            EarthData.Province p = data.province(row);
            String name = p == null ? "(none)" : p.name();
            StringBuilder m = new StringBuilder();
            for (Resource r : FIELDS) m.append(String.format(" %6.3f", geo.multiplier(r, row)));
            System.out.println(String.format("%5d %6s %-34.34s %5d %5d %-9s %-9s %8.0f %5.2f |%s%s", row, p == null ? "-" : p.code(), name, e.getValue()[0], e.getValue()[1],
                    geo.rulesKindOf(row).label(), geo.kindOf(row).label(), p == null ? 0 : p.knownMmboe(), geo.petroleumOf(row), m,
                    geo.seaHoldsOil(row) ? "  (sea holds oil)" : ""));
        }
    }

    /* ----- a save's holdings against this build's fields: is the ground he owned the ground this build draws? ----- */

    static void verify(EarthData data, String id, Path rows, long seed, double lat, double lon) throws java.io.IOException {
        World w = World.of(World.Key.earth(seed, lat, lon, id));
        System.out.println();
        System.out.println(String.format("=== a save's holdings with fields against this build's fields: seed %d, %.6f, %.6f (founding plot %d, %d)", seed, lat, lon, w.foundingX(), w.foundingY()));
        List<long[]> before = new ArrayList<>();   // the rectangles of the holdings listed before: a field centred on one is that holding's
        for (String line : java.nio.file.Files.readAllLines(rows)) {
            if (line.isBlank()) continue;
            String[] f = line.trim().split("\\s+");
            long x0 = Long.parseLong(f[0]), y0 = Long.parseLong(f[1]), x1 = Long.parseLong(f[2]), y1 = Long.parseLong(f[3]);
            StringBuilder b = new StringBuilder(String.format("  holding row %s [%d,%d)-[%d,%d):", f[16], x0, y0, x1, y1));
            for (int k = 0; k < FIELDS.length; k++) {
                Resource r = FIELDS[k];
                int savedSites = Integer.parseInt(f[4 + k]);
                double savedAmount = Double.parseDouble(f[10 + k]);
                int sites = 0, n = 0;
                double amount = 0;
                for (long cy = Math.floorDiv(y0, World.CELL); cy <= Math.floorDiv(y1 - 1, World.CELL); cy++) {
                    for (long cx = Math.floorDiv(x0, World.CELL); cx <= Math.floorDiv(x1 - 1, World.CELL); cx++) {
                        for (Deposit d : w.fieldsInCell((int) (cy * World.CELLS + cx), r)) {
                            if (d.x() < x0 || d.x() >= x1 || d.y() < y0 || d.y() >= y1) continue;
                            boolean earlier = false;
                            for (long[] e : before) earlier |= d.x() >= e[0] && d.x() < e[2] && d.y() >= e[1] && d.y() < e[3];
                            if (earlier) continue;
                            n++;
                            sites += d.sites();
                            amount += d.amount();
                        }
                    }
                }
                if (savedSites == 0 && sites == 0) continue;
                b.append(String.format(" %s saved %d sites %.4g t, drawn now %d fields %d sites %.4g t%s;", r.name().toLowerCase(), savedSites, savedAmount, n, sites, amount,
                        savedSites == sites && Math.abs(savedAmount - amount) < 1 ? " (same)" : " (DIFFERS)"));
            }
            System.out.println(b);
            before.add(new long[] { x0, y0, x1, y1 });
        }
    }

    /* ----- the provinces under real Nova Scotian deposits ----- */

    static void places(EarthData data, Geology geo) {
        Object[][] ps = {
            { "Halifax", 44.65, -63.58, "the city" },
            { "Stellarton (Pictou coalfield, Westray)", 45.56, -62.66, "coal, mined to 1990s" },
            { "Sydney coalfield (Cape Breton)", 46.14, -60.19, "coal, mined to 2001" },
            { "Springhill (Cumberland coalfield)", 45.65, -64.06, "coal, mined to 1970" },
            { "Joggins", 45.70, -64.45, "coal" },
            { "Windsor / Walton (Hants Co.)", 44.99, -64.13, "gypsum, barite" },
            { "Londonderry (Cobequid iron)", 45.48, -63.60, "iron, small, closed 1908" },
            { "Torbrook (Annapolis iron)", 44.98, -65.00, "iron, small" },
            { "Sable Island (Sable Offshore Energy)", 43.93, -59.90, "offshore gas" },
            { "Deep Panuke", 43.80, -60.70, "offshore gas" },
            { "Lake Ainslie (Cape Breton)", 46.13, -61.18, "small onshore oil shows" },
        };
        System.out.println();
        System.out.println("=== the places nearest Samuel's two spots (Natural Earth, the file's places layer)");
        double[][] spots = { { 44.65, -63.6 }, { 45.16948591665112, -61.7246324783713 } };
        for (double[] s : spots) {
            List<EarthData.Place> near = new ArrayList<>(data.places());
            near.sort((x, y) -> Double.compare(EarthProj.distanceM(s[0], s[1], x.lat(), x.lon()), EarthProj.distanceM(s[0], s[1], y.lat(), y.lon())));
            StringBuilder b = new StringBuilder(String.format("  %.4f, %.4f:", s[0], s[1]));
            for (int i = 0; i < 4; i++) b.append(String.format(" %s (%.0f km);", near.get(i).name(), EarthProj.distanceM(s[0], s[1], near.get(i).lat(), near.get(i).lon()) / 1000));
            System.out.println(b);
        }
        System.out.println();
        System.out.println("=== provinces under real Nova Scotian deposits (the geology layer at 0.25 deg)");
        for (Object[] p : ps) {
            double lat = (double) p[1], lon = (double) p[2];
            int row = geo.provinceAt(lat, lon);
            boolean sea = data.at("sea", lat, lon) >= EarthTerrain.COAST_BELOW;
            System.out.println(String.format("  %-40s %7.2f %7.2f  %-5s %-32s %-9s  oil x%.3f coal x%.3f iron x%.3f  (%s)", p[0], lat, lon, sea ? "sea" : "land",
                    provName(data, row), geo.kindOf(row).label(), geo.multiplier(Resource.OIL, row), geo.multiplier(Resource.COAL, row), geo.multiplier(Resource.IRON, row), p[3]));
        }
    }

    /* ----- what the geology alone says a disc should hold: expected fields by province, land and sea ----- */

    static void expected(EarthData data, Geology geo, String name, double lat0, double lon0, double km) {
        double step = 0.02;
        double dLat = km / 111.2 + step, dLon = km / (111.2 * Math.cos(Math.toRadians(lat0))) + step;
        Map<String, double[]> e = new TreeMap<>();
        Resource[] rs = { Resource.IRON, Resource.OIL, Resource.COAL, Resource.STONE, Resource.COPPER };
        double[] all = new double[rs.length * 2];
        double landKm2 = 0, seaKm2 = 0;
        for (double lat = lat0 - dLat; lat <= lat0 + dLat; lat += step) {
            double a = (step * 111.2) * (step * 111.2 * Math.cos(Math.toRadians(lat)));
            for (double lon = lon0 - dLon; lon <= lon0 + dLon; lon += step) {
                if (EarthProj.distanceM(lat0, lon0, lat, lon) / 1000 > km) continue;
                int row = geo.provinceAt(lat, lon);
                boolean sea = data.at("sea", lat, lon) >= EarthTerrain.COAST_BELOW;
                if (sea) seaKm2 += a; else landKm2 += a;
                double[] v = e.computeIfAbsent(provName(data, row) + " [" + geo.kindOf(row).label() + "]", k -> new double[rs.length * 2 + 2]);
                v[rs.length * 2 + (sea ? 1 : 0)] += a;
                for (int i = 0; i < rs.length; i++) {
                    Resource r = rs[i];
                    boolean holds = !sea || (r == Resource.OIL && geo.seaHoldsOil(row));
                    if (!holds) continue;
                    double n = r.fieldsPerKm2() * geo.multiplier(r, row) * a / World.FIELD_SCALE;
                    v[2 * i + (sea ? 1 : 0)] += n;
                    all[2 * i + (sea ? 1 : 0)] += n;
                }
            }
        }
        System.out.println();
        System.out.println(String.format("=== expected fields within %.0f km of %s (%.4f, %.4f), from the geology alone, every %.2f deg: land %.0f km2, sea %.0f km2", km, name, lat0, lon0, step, landKm2, seaKm2));
        StringBuilder h = new StringBuilder(String.format("  %-48s %8s %8s", "province [class]", "land km2", "sea km2"));
        for (Resource r : rs) h.append(String.format(" %13s", r.name().toLowerCase() + " land/sea"));
        System.out.println(h);
        for (Map.Entry<String, double[]> x : e.entrySet()) {
            double[] v = x.getValue();
            StringBuilder s = new StringBuilder(String.format("  %-48.48s %8.0f %8.0f", x.getKey(), v[rs.length * 2], v[rs.length * 2 + 1]));
            for (int i = 0; i < rs.length; i++) s.append(String.format(" %6.1f/%-6.1f", v[2 * i], v[2 * i + 1]));
            System.out.println(s);
        }
        StringBuilder s = new StringBuilder(String.format("  %-48s %8.0f %8.0f", "ALL", landKm2, seaKm2));
        for (int i = 0; i < rs.length; i++) s.append(String.format(" %6.1f/%-6.1f", all[2 * i], all[2 * i + 1]));
        System.out.println(s);
    }

    /* ----- an Earth world round a spot ----- */

    static final class Tally {
        int fields, sites, sea, fresh;
        double amount;
        final Map<String, int[]> byProvince = new TreeMap<>();
        final Map<String, Integer> byType = new TreeMap<>();
    }

    static void earth(EarthData data, Geology geo, String id, Spot sp) {
        long t0 = System.nanoTime();
        World w = World.of(World.Key.earth(sp.seed, sp.lat, sp.lon, id));
        EarthTerrain et = w.earthTerrain();
        EarthProj proj = et.projection();
        long sx = w.foundingX(), sy = w.foundingY();
        double[] site = new double[2];
        proj.latLonOfPlot(sx + 0.5, sy + 0.5, site);
        int siteRow = w.provinceAt(sx, sy);
        System.out.println();
        System.out.println(String.format("=== EARTH %s, seed %d: spot %.4f, %.4f; founding plot %d, %d at %.4f, %.4f, province %s (%s); world built in %.1f s",
                sp.name, sp.seed, sp.lat, sp.lon, sx, sy, site[0], site[1], provName(data, siteRow), geo.kindOf(siteRow).label(), (System.nanoTime() - t0) / 1e9));
        int cell = World.cellOf(sx, sy);
        // The expected fields a cell (its field land x density / FIELD_SCALE) for the site's cell.
        StringBuilder ex = new StringBuilder("  site's cell " + cell + ": land share " + fmt(et.cellLand(cell)) + "; mean fields drawn a cell (field land x density x cell km2 / FIELD_SCALE):");
        for (Resource r : FIELDS) ex.append(" ").append(r.name().toLowerCase()).append(" ").append(fmt(et.cellFieldLand(cell, r) * r.fieldsPerKm2() * World.CELL_KM2 / World.FIELD_SCALE));
        System.out.println(ex);
        double[] iron = EarthSite.nearestIron(w, sx, sy);
        if (iron != null) System.out.println("  founding card's nearest iron: " + java.util.Arrays.toString(iron));
        int reachCells = (int) Math.ceil(BANDS_KM[BANDS_KM.length - 1] * 1000 / World.PLOT_M / World.CELL) + 1;
        int cx = cell % World.CELLS, cy = cell / World.CELLS;
        double[] ll = new double[2];
        for (Resource r : FIELDS) {
            Tally[] t = new Tally[BANDS_KM.length];
            for (int b = 0; b < t.length; b++) t[b] = new Tally();
            List<String> nearest = new ArrayList<>();
            double best = Double.MAX_VALUE;
            String bestLine = null;
            for (int y = cy - reachCells; y <= cy + reachCells; y++) {
                for (int x = cx - reachCells; x <= cx + reachCells; x++) {
                    if (x < 0 || y < 0 || x >= World.CELLS || y >= World.CELLS) continue;
                    for (Deposit d : w.fieldsInCell(y * World.CELLS + x, r)) {
                        if (!proj.latLonOfPlot(d.x() + 0.5, d.y() + 0.5, ll)) continue;
                        double km = EarthProj.distanceM(site[0], site[1], ll[0], ll[1]) / 1000;
                        if (km > BANDS_KM[BANDS_KM.length - 1]) continue;
                        byte cls = w.terrainAt(d.x(), d.y());
                        int row = w.provinceAt(d.x(), d.y());
                        String pn = provName(data, row) + " [" + geo.kindOf(row).label() + "]";
                        String type = r.hasTypes() ? d.type().name().toLowerCase() : "-";
                        if (km < best) {
                            best = km;
                            bestLine = String.format("nearest %.1f km at %.3f, %.3f: %s, %d sites, %.3g t, ground %s, %s", km, ll[0], ll[1], type, d.sites(), d.amount(), className(cls), pn);
                        }
                        for (int b = 0; b < BANDS_KM.length; b++) {
                            if (km > BANDS_KM[b]) continue;
                            Tally tb = t[b];
                            tb.fields++;
                            tb.sites += d.sites();
                            tb.amount += d.amount();
                            if (cls == World.SALT) tb.sea++;
                            if (cls == World.FRESH) tb.fresh++;
                            int[] pc = tb.byProvince.computeIfAbsent(pn, k -> new int[2]);
                            pc[cls == World.SALT ? 1 : 0]++;
                            tb.byType.merge(type, 1, Integer::sum);
                        }
                    }
                }
            }
            System.out.println("  " + r.name() + (bestLine == null ? ": none within " + (int) BANDS_KM[BANDS_KM.length - 1] + " km" : ": " + bestLine));
            for (int b = 0; b < BANDS_KM.length; b++) {
                Tally tb = t[b];
                if (tb.fields == 0) { System.out.println(String.format("    <=%3.0f km: none", BANDS_KM[b])); continue; }
                System.out.println(String.format("    <=%3.0f km: %4d fields (%d at sea, %d in fresh water, %d on dry land), %6d sites, %.3g t; types %s",
                        BANDS_KM[b], tb.fields, tb.sea, tb.fresh, tb.fields - tb.sea - tb.fresh, tb.sites, tb.amount, tb.byType));
                StringBuilder pb = new StringBuilder("             by province (land/sea):");
                for (Map.Entry<String, int[]> e : tb.byProvince.entrySet()) pb.append(" ").append(e.getKey()).append(" ").append(e.getValue()[0]).append("/").append(e.getValue()[1]).append(";");
                System.out.println(pb);
            }
        }
    }

    /* ----- a generated world with the same seed, round its own founding site ----- */

    static void generated(long seed) {
        World w = World.of(seed);
        long sx = w.foundingX(), sy = w.foundingY();
        System.out.println();
        System.out.println(String.format("=== GENERATED seed %d: founding plot %d, %d", seed, sx, sy));
        int cell = World.cellOf(sx, sy);
        int reachCells = (int) Math.ceil(BANDS_KM[BANDS_KM.length - 1] * 1000 / World.PLOT_M / World.CELL) + 1;
        int cx = cell % World.CELLS, cy = cell / World.CELLS;
        for (Resource r : FIELDS) {
            int[] f = new int[BANDS_KM.length], sea = new int[BANDS_KM.length];
            double best = Double.MAX_VALUE;
            String bestLine = null;
            for (int y = cy - reachCells; y <= cy + reachCells; y++) {
                for (int x = cx - reachCells; x <= cx + reachCells; x++) {
                    if (x < 0 || y < 0 || x >= World.CELLS || y >= World.CELLS) continue;
                    for (Deposit d : w.fieldsInCell(y * World.CELLS + x, r)) {
                        double km = Math.hypot(d.x() - sx, d.y() - sy) * World.PLOT_M / 1000;
                        if (km > BANDS_KM[BANDS_KM.length - 1]) continue;
                        byte cls = w.terrainAt(d.x(), d.y());
                        if (km < best) {
                            best = km;
                            bestLine = String.format("nearest %.1f km: %s, %d sites, %.3g t, ground %s", km, r.hasTypes() ? d.type().name().toLowerCase() : "-", d.sites(), d.amount(), className(cls));
                        }
                        for (int b = 0; b < BANDS_KM.length; b++) {
                            if (km > BANDS_KM[b]) continue;
                            f[b]++;
                            if (cls == World.SALT) sea[b]++;
                        }
                    }
                }
            }
            System.out.println(String.format("  %-7s <=50 km %4d (%d sea), <=150 km %4d (%d sea), <=300 km %5d (%d sea); %s", r.name(), f[0], sea[0], f[1], sea[1], f[2], sea[2],
                    bestLine == null ? "none within 300 km" : bestLine));
        }
    }

    static String provName(EarthData data, int row) {
        EarthData.Province p = data.province(row);
        return p == null ? "(none)" : p.code() + " " + p.name();
    }

    static String className(byte c) {
        switch (c) {
            case World.GRASS: return "grass";
            case World.FOREST: return "forest";
            case World.FRESH: return "fresh";
            case World.SALT: return "sea";
            case World.SAND: return "beach";
            case World.ICE: return "ice";
            case World.DESERT: return "desert";
            default: return "?" + c;
        }
    }

    static String fmt(double v) { return String.format("%.3g", v); }
}
