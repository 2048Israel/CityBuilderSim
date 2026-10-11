package ham.citybuildersim;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Earth's reader (0.7.111, batch E1; the project's spec-earth.md 2 and
 * 8): EarthProj's projection, blend and areas held to the spec's measures,
 * and EarthData's reading of earth-1.bin held to tools/earth/earthread.py
 * figure for figure.
 *
 * WHY. E2 lays the real Earth's ground on the world through these two, and a
 * wrong figure there is a coast in the wrong place under a city that cannot
 * be moved off it. The projection is checked against the sphere itself, not
 * against its own formulas; the reader against the Python that wrote the file
 * and reads it independently.
 *
 * THE DATA. The full earth-1.bin (52.8 MB) is not in the repository; since
 * f99d62c it is the asset of the GitHub release earth-1, which
 * tools/earth/get-earth.bat fetches into data/ (EarthData.DATA_FILE). This
 * runs on a fixture cut from it by
 * tools/earth/fixture.py - the same layout holding only the tiles read here,
 * the three windows the study drew (Rotterdam, Perth, Paris) and Fiji's four
 * on the 180th meridian - a resource beside the classes
 * (earth/earth-1-fixture.bin), with what earthread.py read from it and from
 * the full file beside it (earth/earth-1-fixture.expect). The full file's
 * sections run when it is named, -Dcbs.earth=<path> (EarthData.locate(); in
 * AllChecks through JDK_JAVA_OPTIONS, which every harness's JVM inherits),
 * and say so in one line when it is not.
 *
 * What this has to prove:
 *   1. INVERSE THEN FORWARD COMES BACK: from spots at the three windows,
 *      Fiji and both poles, every plane point within ROUND_TRIP_REACH_M goes
 *      to the sphere and back within ROUND_TRIP_M (spec: 2.4e-7 m measured);
 *      the spot is the origin, and a point due north or south lands at
 *      2R sin(c/2) on its axis, the projection's own radial law.
 *   2. EQUAL AREA: a world cell's area measured on the sphere - its edge
 *      carried there point by point - is World.CELL_KM2 within
 *      AREA_TOLERANCE, at the spot and 10,000 km out; the closed form the
 *      game uses agrees, cell by cell, with the same area integrated along
 *      its columns where the antipode's circle crosses it; and Rotterdam's
 *      square has the spec's OFF_PLANET_CELLS cells whose sixteen samples are
 *      all off the planet.
 *   3. THE BLEND: a tile's points blended from its four corners lie within
 *      BLEND_M of the exact ones at the spot and out to BLEND_REACH_M (spec:
 *      0.054 m measured), across the date line too; two tiles agree along the
 *      edge they share; above EarthProj.BLEND_MAX_LAT a tile projects exactly.
 *   4. THE FILE: the fixture opens; its layer table, indexes, meta and
 *      identity are what earthread.py read, and it names EarthData.EARTH_1 as
 *      the file it was cut from.
 *   5. THE READER IS earthread.py'S: every fixture tile's bytes, cells of every
 *      value and the spots' cells found by position, every river run and
 *      small lake decoded, the lake areas, the geology at the spots and the
 *      provinces it names, and the places.
 *   6. A RIVER STORED TWICE IS READ ONCE: across a window's tiles each segment
 *      comes back once, as many as earthread.py counts, and a segment that
 *      crosses a tile's edge is in both tiles' runs but once in the read.
 *   7. THE LRU: a tile read again is not inflated again, the least recently
 *      read goes first, the bytes kept stay within the budget, and the
 *      default budget holds the spec's 64 tiles of sea and lake.
 *   8. A TILE INFLATES IN UNDER INFLATE_MS (spec: 0.13 ms measured; wall
 *      clock, so run under the JVM lock).
 *   9. THE FULL FILE, when named: its identity is EarthData.EARTH_1, and 4 to 6
 *      hold on it, with a ring of tiles round each window, nine far places and
 *      every far-view band besides.
 *
 * Every fixture causes its condition: the windows hold coasts, lakes, rivers
 * crossing tiles' edges and every land-cover level they have.
 */
public class EarthCheck {

    /** Inverse then forward comes back within this, metres (spec-earth 8, E1; 2.4e-7 measured). */
    static final double ROUND_TRIP_M = 1e-6;
    /** ...for every plane point within this of the spot, metres (spec-earth 8: 1,050 km; a 10B city's half-diagonal is 938). */
    static final double ROUND_TRIP_REACH_M = 1_050_000;
    /** A cell's area measured on the sphere is World.CELL_KM2 within this share of it (spec-earth 8: 3,774.87 km2 to 1e-9). */
    static final double AREA_TOLERANCE = 1e-9;
    /** Points a side the sphere's measure of a cell takes: 15 m apart, so the great circles between them stay within a millionth of a square metre of the edge's curve. */
    static final int AREA_SAMPLES = 4096;
    /** A blended point lies within this of the exact one, metres (spec-earth 8: 0.1 m; 0.054 measured 10,000 km out). */
    static final double BLEND_M = 0.1;
    /** How far out the blend is measured, metres of the plane (spec-earth 8: 10,000 km). */
    static final double BLEND_REACH_M = 10_000_000;
    /** A 15" tile inflates within this, milliseconds (spec-earth 8; 0.13 measured). */
    static final double INFLATE_MS = 1;
    /** Rotterdam's square: the cells whose 4 x 4 samples are all off the planet (spec-earth 2: 12,108 of 135,424 measured; the square is the same round any spot). */
    static final int OFF_PLANET_CELLS = 12_108;
    /** The fixture and its expected figures, beside the classes. */
    static final String FIXTURE = "earth/earth-1-fixture.bin", EXPECT = "earth/earth-1-fixture.expect";

    static int fails = 0;
    /** Where the harness prints: the console. */
    static final PrintStream out = System.out;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-96s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /** Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. */
    static final class Tally {
        int n, bad;
        String first;

        void see(boolean ok, String what) {
            n++;
            if (!ok) {
                bad++;
                if (first == null) first = what;
            }
        }
    }

    static void tally(String label, Tally t) {
        boolean ok = t.n > 0 && t.bad == 0;
        if (!ok) fails++;
        out.printf("%-96s %s  (%d of %d agree)%s%n", label, ok ? "OK" : "FAIL", t.n - t.bad, t.n,
                t.bad > 0 ? "; the first that does not: " + t.first : t.n == 0 ? "; none read" : "");
    }

    public static void main(String[] args) throws Exception {
        byte[] fixtureBytes = resource(FIXTURE);
        byte[] expectBytes = resource(EXPECT);
        List<String[]> expect = new ArrayList<>();
        if (expectBytes != null) {
            for (String line : new String(expectBytes, StandardCharsets.UTF_8).split("\n")) {
                if (!line.isEmpty() && !line.startsWith("#")) expect.add(line.split("\t", -1));
            }
        }
        Map<String, String[]> windows = new LinkedHashMap<>();
        for (String[] f : expect) if (f[1].equals("window")) windows.put(f[2], f);

        projection(windows);
        areas();
        blend(windows);

        out.println("--- 4. the fixture is beside the classes ---");
        boolean haveFixture = fixtureBytes != null && expectBytes != null;
        assertTrue("the fixture and its expected figures are beside the classes (" + FIXTURE + ", " + EXPECT + ")", haveFixture);
        if (!haveFixture) {
            out.println("  a build that copies only src/main/resources/*.json leaves earth/ behind: copy src/main/resources/earth/ beside the classes");
        } else {
            EarthData fixture = EarthData.of(fixtureBytes);
            agree(fixture, "fixture", expect, windows);
            lru(fixtureBytes);
            inflateTime(fixture, fixtureBytes);
        }

        out.println("--- 9. the full file, when it is named ---");
        Path full = EarthData.locate();
        String named = System.getProperty(EarthData.PROPERTY);
        if (named != null && !named.isBlank() && !Files.isRegularFile(full)) {
            report("the file -Dcbs.earth names is there", false, named);
        } else if (full == null) {
            out.println("earth-1.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": its sections did not run");
        } else {
            out.println("reading " + full.toAbsolutePath());
            try (EarthData d = EarthData.open(full)) {
                report("the named file is the one this build reads: its SHA-256 is EarthData.EARTH_1", d.identity().equals(EarthData.EARTH_1), d.identity());
                if (haveFixture) agree(d, "full", expect, windows);
            }
        }

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** A resource beside the classes, or failing that the source tree's copy from the working directory; null if neither. */
    static byte[] resource(String name) throws IOException {
        try (InputStream in = EarthCheck.class.getResourceAsStream("/" + name)) {
            if (in != null) return in.readAllBytes();
        }
        Path p = Path.of("src/main/resources", name);
        return Files.isRegularFile(p) ? Files.readAllBytes(p) : null;
    }

    /* =====================================================================
       1. INVERSE THEN FORWARD
       ===================================================================== */

    /** The spots: the windows' (Rotterdam, Perth, Paris, Fiji) and both poles. */
    static List<double[]> spots(Map<String, String[]> windows) {
        List<double[]> s = new ArrayList<>();
        for (String[] w : windows.values()) s.add(new double[] { Double.parseDouble(w[3]), Double.parseDouble(w[4]) });
        if (s.isEmpty()) {
            s.add(new double[] { 51.90625, 4.48125 });              // the windows' centres, should the expected figures be missing
            s.add(new double[] { -31.95625, 115.76041666666666 });
            s.add(new double[] { 48.85625, 2.3395833333333336 });
            s.add(new double[] { -17.0, 180.0 });
        }
        s.add(new double[] { 90, 0 });
        s.add(new double[] { -90, 0 });
        return s;
    }

    static void projection(Map<String, String[]> windows) {
        out.println("--- 1. inverse then forward comes back, within 1,050 km of a spot ---");
        double worst = 0;
        String where = "";
        int points = 0;
        double[] ll = new double[2], xy = new double[2];
        boolean allOn = true, origin = true, radial = true;
        double worstRadial = 0;
        for (double[] s : spots(windows)) {
            EarthProj p = new EarthProj(s[0], s[1]);
            List<double[]> plane = new ArrayList<>();
            int n = 30;
            for (int i = -n / 2; i <= n / 2; i++)
                for (int j = -n / 2; j <= n / 2; j++) plane.add(new double[] { ROUND_TRIP_REACH_M * i / (n / 2), ROUND_TRIP_REACH_M * j / (n / 2) });
            for (double d : new double[] { 0, 1e-3, 1, 30, 1000, 30_000 })
                for (int q = 0; q < 8; q++) plane.add(new double[] { d * StrictMath.cos(q * Math.PI / 4), d * StrictMath.sin(q * Math.PI / 4) });
            for (double[] q : plane) {
                if (!p.inverse(q[0], q[1], ll) || !p.forward(ll[0], ll[1], xy)) { allOn = false; continue; }
                double e = Math.hypot(xy[0] - q[0], xy[1] - q[1]);
                points++;
                if (e > worst) {
                    worst = e;
                    where = String.format("from (%.4f, %.4f) at (%.0f, %.0f) m", s[0], s[1], q[0], q[1]);
                }
            }
            p.forward(s[0], s[1], xy);
            origin &= xy[0] == 0 && xy[1] == 0;
            // the radial law: ground distance c along the meridian lands 2R sin(c/2) out on the axis
            for (double ground : new double[] { 1_000_000, 5_000_000, 10_000_000 }) {
                double c = ground / EarthProj.RADIUS_M;
                double lat = s[0] >= 0 ? s[0] - StrictMath.toDegrees(c) : s[0] + StrictMath.toDegrees(c);
                double lon = s[1];
                if (lat < -90 || lat > 90) continue;
                p.forward(lat, lon, xy);
                double want = EarthProj.REACH_M * StrictMath.sin(c / 2);
                double e = Math.abs(Math.hypot(xy[0], xy[1]) - want) + (Math.abs(s[0]) == 90 ? 0 : Math.abs(xy[0]));
                worstRadial = Math.max(worstRadial, e);
                radial &= e <= ROUND_TRIP_M;
            }
        }
        assertTrue("every point within 1,050 km of every spot is on the planet", allOn);
        report("inverse then forward comes back within ROUND_TRIP_M at every point (Rotterdam, Perth, Paris, Fiji, both poles)",
                points > 0 && worst <= ROUND_TRIP_M, String.format("worst %.2e m of %d points, %s (spec 2.4e-7)", worst, points, where));
        assertTrue("the spot is the plane's origin", origin);
        report("a point c along the meridian lands 2R sin(c/2) out on the axis (1,000, 5,000 and 10,000 km)", radial, String.format("worst %.2e m", worstRadial));
    }

    /* =====================================================================
       2. EQUAL AREA
       ===================================================================== */

    static void areas() {
        out.println("--- 2. a 61.44 km cell's area, measured on the sphere ---");
        int mid = World.CELLS / 2;
        int[][] cells = { { mid, mid }, { mid - 1, mid - 1 }, { mid + 16, mid }, { mid, mid - 81 }, { mid + 115, mid + 115 }, { mid - 163, mid }, { mid + 3, mid - 160 } };
        double[][] at = { { 51.90625, 4.48125 }, { -31.95625, 115.76041666666666 }, { 90, 0 } };
        double worst = 0;
        String where = "";
        int measured = 0;
        double farthest = 0;
        for (double[] s : at) {
            EarthProj p = new EarthProj(s[0], s[1]);
            for (int[] c : cells) {
                double x0 = EarthProj.metresX((double) c[0] * World.CELL), x1 = EarthProj.metresX((double) (c[0] + 1) * World.CELL);
                double y0 = EarthProj.metresY((double) c[1] * World.CELL), y1 = EarthProj.metresY((double) (c[1] + 1) * World.CELL);
                double km2 = p.sphericalAreaM2(x0, y0, x1, y1, AREA_SAMPLES) / 1e6;
                double e = Math.abs(km2 / World.CELL_KM2 - 1);
                measured++;
                farthest = Math.max(farthest, Math.hypot((x0 + x1) / 2, (y0 + y1) / 2));
                if (!(e <= worst)) {
                    worst = e;
                    where = String.format("cell (%d, %d) from (%.2f, %.2f): %.9f km2", c[0], c[1], s[0], s[1], km2);
                }
            }
        }
        report("a cell's area on the sphere is World.CELL_KM2 (3,774.8736 km2) within AREA_TOLERANCE, to 10,000 km out",
                worst <= AREA_TOLERANCE && farthest >= BLEND_REACH_M, String.format("worst %.1e of %d, %s; farthest %.0f km", worst, measured, where, farthest / 1000));
        boolean closed = true;
        for (int[] c : cells) closed &= EarthProj.cellAreaKm2(c[0], c[1]) == World.CELL_KM2;
        assertTrue("and the closed form the game uses gives it exactly for those cells", closed);

        out.println("--- 2. the square on the planet: the antipode's circle ---");
        double total = 0, worstCut = 0;
        int cut = 0, past = 0, allOff = 0;
        for (int cx = 0; cx < World.CELLS; cx++) {
            for (int cy = 0; cy < World.CELLS; cy++) {
                double a = EarthProj.cellAreaKm2(cx, cy);
                total += a;
                if (a == 0) past++;
                else if (a < World.CELL_KM2) {
                    cut++;
                    double x0 = EarthProj.metresX((double) cx * World.CELL), x1 = EarthProj.metresX((double) (cx + 1) * World.CELL);
                    double y0 = EarthProj.metresY((double) (cy + 1) * World.CELL), y1 = EarthProj.metresY((double) cy * World.CELL);
                    double numeric = columns(x0, x1, y0, y1) / 1e6;
                    worstCut = Math.max(worstCut, Math.abs(numeric - a) / World.CELL_KM2);
                }
                boolean off = true;
                for (int i = 0; i < 4 && off; i++)
                    for (int j = 0; j < 4 && off; j++)
                        off = !EarthProj.onPlanet(EarthProj.metresX((cx + (i + 0.5) / 4) * World.CELL), EarthProj.metresY((cy + (j + 0.5) / 4) * World.CELL));
                if (off) allOff++;
            }
        }
        report("where the circle cuts a cell, its area on the planet is the same area integrated along its columns",
                cut > 0 && worstCut <= 1e-6, String.format("%d cells cut, worst %.1e of a cell", cut, worstCut));
        double sphere = 4 * Math.PI * EarthProj.RADIUS_M * EarthProj.RADIUS_M / 1e6;
        report("the cells whose sixteen samples are all off the planet are the spec's OFF_PLANET_CELLS",
                allOff == OFF_PLANET_CELLS, String.format("%,d of %,d; by them %.2f%% of the Earth is in the square (spec 91.3%%), by the exact area %.2f%%; %,d cells wholly past the circle",
                        allOff, World.CELLS * World.CELLS, 100 * (World.CELLS * World.CELLS - allOff) * World.CELL_KM2 / sphere, 100 * total / sphere, past));
    }

    /** The area of the rectangle within the reach, integrated along x by Simpson's rule on the exact chord: the same figure as areaOnPlanetM2 by another road. */
    static double columns(double x0, double x1, double y0, double y1) {
        int n = 20_000;
        double h = (x1 - x0) / n, sum = 0;
        for (int i = 0; i <= n; i++) {
            double x = x0 + h * i;
            double half = x * x >= EarthProj.REACH_M * EarthProj.REACH_M ? 0 : Math.sqrt(EarthProj.REACH_M * EarthProj.REACH_M - x * x);
            double len = Math.max(0, Math.min(y1, half) - Math.max(y0, -half));
            sum += len * (i == 0 || i == n ? 1 : i % 2 == 1 ? 4 : 2);
        }
        return sum * h / 3;
    }

    /* =====================================================================
       3. THE BLEND
       ===================================================================== */

    static void blend(Map<String, String[]> windows) {
        out.println("--- 3. a tile's corners blended, at the spot and 10,000 km out ---");
        double worstSpot = 0, worstFar = 0, worstSeam = 0;
        int blended = 0, exact = 0, seams = 0;
        String whereFar = "";
        double[] ll = new double[2];
        for (double[] s : spots(windows)) {
            EarthProj p = new EarthProj(s[0], s[1]);
            List<double[]> at = new ArrayList<>();
            at.add(new double[] { 0, 0 });
            for (double d : new double[] { 1_000_000, 5_000_000, BLEND_REACH_M })
                for (int q = 0; q < 8; q++) at.add(new double[] { d * StrictMath.cos(q * Math.PI / 4), d * StrictMath.sin(q * Math.PI / 4) });
            for (double[] a : at) {
                double tx = Math.floor(EarthProj.plotX(a[0]) / World.TILE) * World.TILE;
                double ty = Math.floor(EarthProj.plotY(a[1]) / World.TILE) * World.TILE;
                EarthProj.Blend b = p.blend(tx, ty, World.TILE);
                if (b.exact()) { exact++; continue; }
                blended++;
                double e = blendError(p, b, tx, ty);
                if (a[0] == 0 && a[1] == 0) worstSpot = Math.max(worstSpot, e);
                else if (e > worstFar) {
                    worstFar = e;
                    whereFar = String.format("from (%.2f, %.2f), %.0f km out", s[0], s[1], Math.hypot(a[0], a[1]) / 1000);
                }
                // the tile east and the tile south share an edge with it, and blend it from the same two corners
                EarthProj.Blend east = p.blend(tx + World.TILE, ty, World.TILE), south = p.blend(tx, ty + World.TILE, World.TILE);
                double[] mine = new double[2], theirs = new double[2];
                for (int k = 0; k <= World.TILE; k++) {
                    if (!east.exact()) {
                        b.latLon(tx + World.TILE, ty + k, mine);
                        east.latLon(tx + World.TILE, ty + k, theirs);
                        worstSeam = Math.max(worstSeam, EarthProj.distanceM(mine[0], mine[1], theirs[0], theirs[1]));
                        seams++;
                    }
                    if (!south.exact()) {
                        b.latLon(tx + k, ty + World.TILE, mine);
                        south.latLon(tx + k, ty + World.TILE, theirs);
                        worstSeam = Math.max(worstSeam, EarthProj.distanceM(mine[0], mine[1], theirs[0], theirs[1]));
                        seams++;
                    }
                }
            }
        }
        report("blended at the spot, a tile's points (and its ring) lie within BLEND_M of the exact ones", blended > 0 && worstSpot <= BLEND_M,
                String.format("worst %.3f m (spec 0.025)", worstSpot));
        report("and out to BLEND_REACH_M, 10,000 km, in eight directions from every spot", worstFar > 0 && worstFar <= BLEND_M,
                String.format("worst %.3f m, %s (spec 0.054); %d tiles blended, %d exact", worstFar, whereFar, blended, exact));
        report("two blended tiles that share an edge agree along it", seams > 0 && worstSeam <= 1e-6, String.format("worst %.1e m over %,d points", worstSeam, seams));

        // across the date line: Fiji's spot sits on it, and its tile's corners are on both sides
        EarthProj fiji = new EarthProj(-17.0, 180.0);
        EarthProj.Blend f = fiji.blend(World.SIDE / 2.0 - World.TILE / 2.0, World.SIDE / 2.0 - World.TILE / 2.0, World.TILE);
        double[] w = new double[2], e = new double[2];
        fiji.latLonOfPlot(World.SIDE / 2.0 - World.TILE / 2.0, World.SIDE / 2.0, w);
        fiji.latLonOfPlot(World.SIDE / 2.0 + World.TILE / 2.0, World.SIDE / 2.0, e);
        double across = blendError(fiji, f, World.SIDE / 2.0 - World.TILE / 2.0, World.SIDE / 2.0 - World.TILE / 2.0);
        report("a tile astride the 180th meridian blends without a seam", w[1] > 0 && e[1] < 0 && !f.exact() && across <= BLEND_M,
                String.format("its west edge at %.5f, its east at %.5f; worst %.3f m", w[1], e[1], across));

        // near a pole: from Rotterdam, the North Pole's tile projects exactly, and one just under BLEND_MAX_LAT blends
        EarthProj rot = new EarthProj(51.90625, 4.48125);
        double[] pole = new double[2];
        rot.plotOf(90, 0, pole);
        double px = Math.floor(pole[0] / World.TILE) * World.TILE, py = Math.floor(pole[1] / World.TILE) * World.TILE;
        EarthProj.Blend atPole = rot.blend(px, py, World.TILE);
        report("the tile on the North Pole projects every point exactly", atPole.exact() && blendError(rot, atPole, px, py) == 0, "");
        double[] under = new double[2];
        rot.plotOf(EarthProj.BLEND_MAX_LAT - 0.5, 4.48125, under);
        px = Math.floor(under[0] / World.TILE) * World.TILE;
        py = Math.floor(under[1] / World.TILE) * World.TILE;
        EarthProj.Blend high = rot.blend(px, py, World.TILE);
        double highErr = blendError(rot, high, px, py);
        report("a tile half a degree under BLEND_MAX_LAT blends, within BLEND_M", !high.exact() && highErr <= BLEND_M, String.format("worst %.3f m", highErr));
    }

    /** The farthest a tile's blended point lies from the exact one, metres, over its plots' centres and the ring of plots round it. */
    static double blendError(EarthProj p, EarthProj.Blend b, double tx, double ty) {
        double worst = 0;
        double[] got = new double[2], want = new double[2];
        for (int i = -1; i <= World.TILE; i++) {
            for (int j = -1; j <= World.TILE; j++) {
                double x = tx + i + 0.5, y = ty + j + 0.5;
                boolean on = p.latLonOfPlot(x, y, want);
                if (!b.latLon(x, y, got) || !on) continue;
                worst = Math.max(worst, EarthProj.distanceM(got[0], got[1], want[0], want[1]));
            }
        }
        return worst;
    }

    /* =====================================================================
       4 TO 6. THE READER AGAINST earthread.py
       ===================================================================== */

    static String sha(byte[] b) throws Exception {
        return EarthData.hex(MessageDigest.getInstance("SHA-256").digest(b));
    }

    /** A tile's runs as the integers they were stored as, written as tools/earth/fixture.py writes them. */
    static String riverInts(List<EarthData.Run> runs) {
        StringBuilder sb = new StringBuilder();
        for (EarthData.Run r : runs) {
            sb.append(r.size()).append(';');
            for (int k = 0; k < r.size(); k++) sb.append(r.latUnits(k)).append(',').append(r.lonUnits(k)).append(',').append(r.width(k)).append(',').append(r.flag(k)).append(';');
            sb.append('|');
        }
        return sb.toString();
    }

    static String lakeInts(EarthData.SmallLakes l) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < l.size(); k++) sb.append(l.latUnits(k)).append(',').append(l.lonUnits(k)).append(',').append(l.code(k)).append(';');
        return sb.toString();
    }

    static int segmentsOf(List<EarthData.Run> runs) {
        int n = 0;
        for (EarthData.Run r : runs) n += Math.max(0, r.size() - 1);
        return n;
    }

    static int[] tilesOf(String[] f, int from) {
        int[] t = new int[f.length - from];
        for (int i = from; i < f.length; i++) t[i - from] = Integer.parseInt(f[i]);
        return t;
    }

    /** Every expected figure of scope "both" and of this scope, read through EarthData and compared. */
    static void agree(EarthData d, String scope, List<String[]> expect, Map<String, String[]> windows) throws Exception {
        Map<String, Tally> t = new HashMap<>();
        for (String k : new String[] { "file", "of", "table", "index", "whole", "tile", "cell", "spot", "rivers", "vertex", "lakes", "lake", "area",
                "segments", "geology", "province", "provinces", "places", "place" }) t.put(k, new Tally());
        for (String[] f : expect) {
            if (!f[0].equals("both") && !f[0].equals(scope)) continue;
            String kind = f[1];
            Tally y = t.get(kind);
            String line = String.join(" ", f);
            switch (kind) {
                case "file" -> y.see(d.size() == Long.parseLong(f[3]) && d.identity().equals("earth-1:" + f[2]), line + " / " + d.size() + " " + d.identity());
                case "of" -> {
                    String of = com.google.gson.JsonParser.parseString(d.meta()).getAsJsonObject().getAsJsonObject("fixture").get("of").getAsString();
                    y.see(of.equals(f[2]) && of.equals(EarthData.EARTH_1), line + " / " + of);
                }
                case "table" -> {
                    EarthData.Layer l = d.layer(f[2]);
                    y.see(l.kind == Integer.parseInt(f[3]) && l.cellsPerDegree == Integer.parseInt(f[4]) && l.indexOffset == Long.parseLong(f[5])
                            && l.indexLength == Integer.parseInt(f[6]) && l.dataOffset == Long.parseLong(f[7]) && l.dataLength == Long.parseLong(f[8]), line);
                }
                case "index" -> {
                    EarthData.Layer l = d.layer(f[2]);
                    ByteBuffer b = ByteBuffer.allocate(4 * l.count());
                    for (int i = 0; i < l.count(); i++) b.putInt(d.entry(f[2], i));
                    y.see(sha(b.array()).equals(f[3]), line);
                }
                case "whole" -> y.see(sha(d.raw(f[2])).equals(f[3]), line);
                case "tile" -> {
                    int i = Integer.parseInt(f[3]);
                    EarthData.Layer l = d.layer(f[2]);
                    byte[] b = l.cellsPerDegree > 0 ? d.grid(f[2], i) : d.raw(f[2], i);
                    y.see(d.entry(f[2], i) == Integer.parseInt(f[4]) && (b == null ? f[5].equals("-") : sha(b).equals(f[5])), line);
                }
                case "cell" -> {
                    int i = Integer.parseInt(f[3]), r = Integer.parseInt(f[4]), c = Integer.parseInt(f[5]), v = Integer.parseInt(f[6]);
                    EarthData.Layer l = d.layer(f[2]);
                    int cpd = l.cellsPerDegree;
                    double lat, lon;
                    if (l.kind == EarthData.KIND_TILES) {
                        lat = EarthData.tileLat0(i) + 1 - (r + 0.5) / cpd;
                        lon = EarthData.tileLon0(i) + (c + 0.5) / cpd;
                    } else {
                        lat = i - 90 + 1 - (r + 0.5) / cpd;
                        lon = -180 + (c + 0.5) / cpd;
                    }
                    // the cell by its number, and by a point at its centre (FORMAT.txt: cells centred)
                    y.see(d.cell(f[2], i, r, c) == v && d.at(f[2], lat, lon) == v, line + " / " + d.cell(f[2], i, r, c) + " " + d.at(f[2], lat, lon));
                }
                case "spot" -> {
                    int v = Integer.parseInt(f[8]);
                    int got = d.at(f[2], Double.parseDouble(f[3]), Double.parseDouble(f[4]));
                    y.see(got == v && d.cell(f[2], Integer.parseInt(f[5]), Integer.parseInt(f[6]), Integer.parseInt(f[7])) == v, line + " / " + got);
                }
                case "rivers" -> {
                    List<EarthData.Run> runs = d.rivers(Integer.parseInt(f[2]));
                    int vertices = 0;
                    for (EarthData.Run r : runs) vertices += r.size();
                    y.see(runs.size() == Integer.parseInt(f[3]) && vertices == Integer.parseInt(f[4])
                            && sha(riverInts(runs).getBytes(StandardCharsets.UTF_8)).equals(f[5]), line + " / " + runs.size() + " " + vertices);
                }
                case "vertex" -> {
                    EarthData.Run r = d.rivers(Integer.parseInt(f[2])).get(Integer.parseInt(f[3]));
                    int k = Integer.parseInt(f[4]);
                    y.see(r.lat(k) == Double.parseDouble(f[5]) && r.lon(k) == Double.parseDouble(f[6]) && r.width(k) == Double.parseDouble(f[7])
                            && r.flag(k) == Double.parseDouble(f[8]), line + " / " + r.lat(k) + " " + r.lon(k) + " " + r.width(k) + " " + r.flag(k));
                }
                case "lakes" -> {
                    EarthData.SmallLakes l = d.smallLakes(Integer.parseInt(f[2]));
                    y.see(l.size() == Integer.parseInt(f[3]) && sha(lakeInts(l).getBytes(StandardCharsets.UTF_8)).equals(f[4]), line + " / " + l.size());
                }
                case "lake" -> {
                    EarthData.SmallLakes l = d.smallLakes(Integer.parseInt(f[2]));
                    int k = Integer.parseInt(f[3]);
                    y.see(l.lat(k) == Double.parseDouble(f[4]) && l.lon(k) == Double.parseDouble(f[5]) && l.areaKm2(k) == Double.parseDouble(f[6]),
                            line + " / " + l.lat(k) + " " + l.lon(k) + " " + l.areaKm2(k));
                }
                case "area" -> y.see(EarthData.areaOfCode(Integer.parseInt(f[2])) == Double.parseDouble(f[3]), line + " / " + EarthData.areaOfCode(Integer.parseInt(f[2])));
                case "segments" -> {
                    int[] tiles = f.length > 5 ? tilesOf(f, 5) : tilesOf(windows.get(f[2]), 5);
                    int stored = 0;
                    for (int i : tiles) stored += segmentsOf(d.rivers(i));
                    int kept = segmentsOf(d.riversIn(tiles));
                    y.see(stored == Integer.parseInt(f[3]) && kept == Integer.parseInt(f[4]), line + " / " + stored + " " + kept);
                }
                case "geology" -> {
                    int got = d.provinceAt(Double.parseDouble(f[3]), Double.parseDouble(f[4]));
                    y.see(got == Integer.parseInt(f[5]), line + " / " + got);
                }
                case "province" -> {
                    EarthData.Province p = d.province(Integer.parseInt(f[2]));
                    y.see(p != null && p.code().equals(f[3]) && p.name().equals(f[4]) && p.kind().equals(f[5]) && p.knownMmboe() == Double.parseDouble(f[6]), line + " / " + p);
                }
                case "provinces" -> y.see(d.provinces().size() == Integer.parseInt(f[2]), line + " / " + d.provinces().size());
                case "places" -> y.see(d.places().size() == Integer.parseInt(f[2]), line + " / " + d.places().size());
                case "place" -> {
                    EarthData.Place p = d.places().get(Integer.parseInt(f[2]));
                    y.see(p.name().equals(f[3]) && p.ascii().equals(f[4].isEmpty() ? f[3] : f[4]) && p.country().equals(f[5])
                            && p.lat() == Double.parseDouble(f[6]) && p.lon() == Double.parseDouble(f[7]) && p.population() == Long.parseLong(f[8]), line + " / " + p);
                }
                case "window" -> { }
                default -> report("an expected figure of a kind this harness knows", false, line);
            }
        }
        boolean fixture = scope.equals("fixture");
        if (fixture) out.println("--- 4. the fixture's file, as earthread.py read it ---");
        else out.println("--- 9. the full file's layout, as earthread.py read it ---");
        tally("the file is the one earthread.py read: its length and its SHA-256", t.get("file"));
        if (fixture) tally("its meta names the full file it was cut from, EarthData.EARTH_1", t.get("of"));
        tally("the layer table is the file's own: every layer's kind, cells a degree, offsets and lengths", t.get("table"));
        tally("every layer's index is earthread.py's, entry by entry", t.get("index"));
        tally("the meta, the geology, the provinces and the places inflate to earthread.py's bytes", t.get("whole"));
        if (fixture) out.println("--- 5. the reader is earthread.py's, on the fixture's tiles ---");
        else out.println("--- 9. the reader is earthread.py's, on the full file's tiles, the windows' rings and nine far places ---");
        tally("every tile's and band's bytes (the share layers' every cell, the lists as stored)", t.get("tile"));
        tally("a cell of every value in every tile, by its number and by a point at its centre", t.get("cell"));
        tally("the windows' spots' cells, found by position", t.get("spot"));
        tally("every tile's river runs decoded: the runs, the vertices and every stored integer", t.get("rivers"));
        tally("river vertices read in degrees and metres, as earthread.py gives them", t.get("vertex"));
        tally("every tile's small lakes decoded: their number and every stored integer", t.get("lakes"));
        tally("small lakes read in degrees and km2", t.get("lake"));
        tally("a small lake's area for each of the 256 codes", t.get("area"));
        tally("the geology at the spots, the windows' corners and the Yilgarn", t.get("geology"));
        tally("the provinces it names: code, name, class and known petroleum", t.get("province"));
        tally("the provinces and the places, counted", merge(t.get("provinces"), t.get("places")));
        tally("the places read: the first three and Rotterdam, Perth and Paris", t.get("place"));
        if (fixture) out.println("--- 6. a river stored twice is read once ---");
        else out.println("--- 9. a river stored twice is read once, over the windows and their rings ---");
        tally("across a window's tiles each segment comes back once, as many as earthread.py counts", t.get("segments"));
        if (fixture) duplicates(d, windows);
    }

    static Tally merge(Tally a, Tally b) {
        Tally m = new Tally();
        m.n = a.n + b.n;
        m.bad = a.bad + b.bad;
        m.first = a.first != null ? a.first : b.first;
        return m;
    }

    /** A segment crossing a tile's edge: in both tiles' runs, once in the read across them. */
    static void duplicates(EarthData d, Map<String, String[]> windows) {
        String[] w = windows.get("Rotterdam");
        if (w == null) {
            assertTrue("Rotterdam's window is in the expected figures", false);
            return;
        }
        int[] tiles = tilesOf(w, 5);
        Map<List<Integer>, Integer> where = new HashMap<>();
        List<Integer> twice = null;
        for (int i : tiles) {
            for (EarthData.Run r : d.rivers(i)) {
                for (int k = 0; k + 1 < r.size(); k++) {
                    List<Integer> key = List.of(r.latUnits(k), r.lonUnits(k), r.latUnits(k + 1), r.lonUnits(k + 1));
                    Integer before = where.put(key, i);
                    if (before != null && before != i && twice == null) twice = key;
                }
            }
        }
        int inRead = 0;
        if (twice != null) {
            for (EarthData.Run r : d.riversIn(tiles))
                for (int k = 0; k + 1 < r.size(); k++)
                    if (List.of(r.latUnits(k), r.lonUnits(k), r.latUnits(k + 1), r.lonUnits(k + 1)).equals(twice)) inRead++;
        }
        report("Rotterdam's window has a segment stored in two tiles, and the read across them has it once", twice != null && inRead == 1,
                twice == null ? "none stored twice" : "segment " + twice + " read " + inRead + " times");
        assertTrue("a tile read alone keeps it, both ends (rivers(), as stored)", twice != null);
    }

    /* =====================================================================
       7. THE LRU
       ===================================================================== */

    static void lru(byte[] fixtureBytes) throws IOException {
        out.println("--- 7. the LRU ---");
        EarthData probe = EarthData.of(fixtureBytes);
        List<Integer> sea = new ArrayList<>();
        for (int i = 0; i < EarthData.TILES; i++) if (probe.entry("sea", i) > 0) sea.add(i);
        int cell = probe.layer("sea").cellsPerDegree;
        long tileBytes = (long) cell * cell;
        assertTrue("the fixture has four sea tiles with blobs to read", sea.size() >= 4);
        if (sea.size() < 4) return;
        EarthData d = EarthData.of(fixtureBytes, 3 * tileBytes);
        int a = sea.get(0), b = sea.get(1), c = sea.get(2), e = sea.get(3);
        byte[] first = d.raw("sea", a);
        d.raw("sea", b);
        d.raw("sea", c);
        boolean three = d.inflations() == 3 && d.cachedBytes() == 3 * tileBytes;
        byte[] again = d.raw("sea", a);
        boolean hit = d.inflations() == 3 && again == first;
        d.raw("sea", e);                                            // over the budget: b, the least recently read, goes
        boolean bounded = d.cachedBytes() <= 3 * tileBytes;
        d.raw("sea", a);
        boolean aKept = d.inflations() == 4;
        d.raw("sea", b);
        boolean bGone = d.inflations() == 5;
        assertTrue("three tiles read are three inflated, and their bytes kept", three);
        assertTrue("a tile read again comes from the LRU, the same bytes, nothing inflated", hit);
        assertTrue("a fourth tile over the budget keeps the bytes within it", bounded && d.cachedBytes() <= 3 * tileBytes);
        assertTrue("the tile read most recently stays", aKept);
        assertTrue("the least recently read went first, and is inflated again", bGone);
        report("the default budget holds the spec's 64 1-degree tiles of sea and lake", EarthData.CACHE_BYTES >= 64 * 2 * tileBytes,
                String.format("%,d bytes against 64 x 2 x %,d", EarthData.CACHE_BYTES, tileBytes));
    }

    /* =====================================================================
       8. A TILE INFLATES IN UNDER A MILLISECOND
       ===================================================================== */

    static void inflateTime(EarthData d, byte[] fixtureBytes) throws Exception {
        out.println("--- 8. a 15\" tile inflates in under a millisecond ---");
        EarthData.Layer sea = d.layer("sea");
        int best = -1;
        for (int i = 0; i < EarthData.TILES; i++) if (sea.entries[i] > 0 && (best < 0 || sea.entries[i] > sea.entries[best])) best = i;
        if (best < 0) {
            assertTrue("the fixture has a sea tile to inflate", false);
            return;
        }
        byte[] z = Arrays.copyOfRange(fixtureBytes, (int) sea.offsets[best], (int) sea.offsets[best] + sea.entries[best]);
        int expect = sea.cellsPerDegree * sea.cellsPerDegree;
        for (int i = 0; i < 3000; i++) EarthData.inflate(z, expect);
        int rounds = 31, each = 50;
        double[] us = new double[rounds];
        for (int r = 0; r < rounds; r++) {
            long t0 = System.nanoTime();
            for (int i = 0; i < each; i++) EarthData.inflate(z, expect);
            us[r] = (System.nanoTime() - t0) / 1e3 / each;
        }
        Arrays.sort(us);
        double median = us[rounds / 2];
        report("the fullest sea tile of the fixture inflates in under INFLATE_MS, the median of 31 rounds of 50", median <= INFLATE_MS * 1000,
                String.format("tile %d, %,d bytes to %,d: median %.0f us, slowest round %.0f us (spec 126-130 us)", best, z.length, expect, median, us[rounds - 1]));
    }
}
