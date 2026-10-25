package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.function.Predicate;

/**
 * The city's ground counted by landmass, and the island rule, checked (ET5, slice A; the ET5 design): a landmass is read
 * from the Earth's data at every plot, the city's books are kept by landmass, ground on a landmass the city has not linked
 * is owned, counted and priced as ever but is no room for anyone, and no works stands on its fields.
 *
 * WHY THIS EXISTS. Until ET5 the model's ground was one pool with no place in it: an island bought across a strait was
 * room for any building, and the Rotterdam city could "build" on Goeree-Overflakkee from its own streets. Nothing could
 * say so, because nothing read a plot's landmass. This holds the reader, the books and the rule against fixtures that
 * CAUSE each condition (an island square found by search with the model's own reader, bought through the harness door
 * LandManager.buyRectForTest(); a city founded on the island, so that the mainland is across the water), and every
 * figure worked out again from the model's constants and getters, never typed.
 *
 * WHAT IT HAS TO PROVE (this slice: sections 0 to 4 and 8; the ports, the save, the map and the words are the other slices'):
 *
 *   0. A GENERATED WORLD HAS NO LANDMASSES. hasLandmasses() false, the city a single landmass, nothing read (plotsRead()
 *      0), every offer's unlinked figure 0, and the free ground, the share used, the zones and the coast test the old
 *      expressions written out inline - on a land of three holdings far apart, on the default seed after 120 months, and
 *      on a city founded on the Earth whose ground lies on its home landmass.
 *   1. THE READER. tileLandmass() is landmassAt() at every plot; every dry plot on a cell the layer calls no land takes
 *      the nearest land cell's id within LANDMASS_REACH, as a brute-force loop over the data says (such plots exist, one
 *      resolves to the island); idsNear() holds every id a tile's plots resolve to, the reach's included (the gate's premise);
 *      two worlds of one key agree; home is the id the founding card reads at the snapped site.
 *   2. THE BOOKS BY LANDMASS. Each landmass's dry plots by relief, forest, shore and first holding equal a brute-force
 *      count over every owned plot, a shore across a tile's edge included; they add up to the land's dry ground to the plot; a recount after a purchase is a
 *      fresh recount; the names are "Home" and "Island 2"; the linked set (closure()) is home, the landmasses a join
 *      reaches and, once a linked one has a port, every landmass with one.
 *   3. FREE GROUND IS LINKED ONLY. A city with the island square and its twin without it have the same free ground,
 *      share used, zone size and overFull within FREE_SLACK; what fits city-wide is refused; the unlinked free ground is
 *      the square's dry plots; the investors' refusal ends with the clause, word for word.
 *   4. ONLY A PORT ON UNLINKED GROUND. A House past the linked free ground is NO_LAND though the island would hold it;
 *      a works on a field centred on an unlinked plot is NO_DEPOSIT and the field is in no site count; the same field at
 *      home is a deposit. (Section 4's coast assertions are the ports slice's.)
 *   8. THE PICKERS. An all-island offer has no buildable ground and is never the best for room, a shortfall or a
 *      deposit; ISLAND is tagged by the share; a shortfall only the island's dry ground would cover buys no island.
 *
 * Every premise is asserted before what rests on it. Each full-file section prints one line and skips without
 * -Dcbs.earth (none here: the fixture holds Rotterdam's two landmasses).
 *
 * @author Jerus
 */
public class LandmassCheck {

    static int fails = 0;
    static final PrintStream out = System.out;
    static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    /** The seed every world here is drawn with: the default world's. */
    static final long SEED = Founding.DEFAULT_WORLD_SEED;

    /** Two square-foot figures of one ground are the same within a square foot (the two landmass counts and the land's sum differ in the last bits of a double). */
    static final double FREE_SLACK = 1.0;

    /** The area of a plot, square feet, as the books reckon it. */
    static final double SQ_FT_PER_PLOT = World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2;

    /** The side of an island square, in plots. */
    static final int SQUARE = 64;

    /** Rotterdam, the fixture's window: where the city is founded. */
    static final double RLAT = 51.90625, RLON = 4.48125;

    /** Where a city on Goeree-Overflakkee is founded (the mainland is then across the water): candidate spots, the first that serves. */
    static final double[][] ON_THE_ISLAND = { { 51.77, 4.15 }, { 51.75, 4.20 }, { 51.80, 4.10 } };

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-112s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-112s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(QUIET);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** Two square-foot figures within FREE_SLACK. */
    static boolean near(double a, double b) { return Math.abs(a - b) <= FREE_SLACK; }

    /* =====================================================================
       THE FIXTURES
       ===================================================================== */

    /** A city on a fixture world: its game, its land office and its world. */
    static final class City {
        final Game g;
        final LandManager lm;
        final World w;
        final World.Key key;

        City(String label, World.Key key, int houses) {
            this.key = key;
            this.g = new Game(GameFiles.scratch(label));
            quietly(() -> {
                g.newGame(Founding.defaults().withWorldKey(key));
                g.setCashForTest(Founding.WEALTHY_CASH);
                if (houses > 0) g.buildStack(g.getBuildingManager().getTemplateByName("House"), houses, true);
            });
            this.lm = g.getLandManager();
            this.w = World.of(key);
        }

        Landmasses books() { return lm.getLandmasses(); }

        /** Buys a square (as an offer of it would be, at no price) and returns the parcel. */
        LandParcel buy(Square s) {
            LandParcel[] p = new LandParcel[1];
            quietly(() -> p[0] = lm.buyRectForTest(s.x0, s.y0, s.x0 + SQUARE, s.y0 + SQUARE, g.getMonth()));
            return p[0];
        }
    }

    /** A square of SQUARE x SQUARE plots, measured by the model's own reader: what lies on a landmass in it. */
    record Square(long x0, long y0, int id, long onId, long dry, long salt, long shore, boolean pure) { }

    /** Measures the square at (x0, y0) for landmass `id`: its dry plots on it, its salt, its shore (a flat or foothill dry plot of it beside salt in the square) and whether every dry plot of it is `id`'s. */
    static Square measure(World w, long x0, long y0, int id) {
        long onId = 0, dry = 0, salt = 0, shore = 0;
        boolean pure = true;
        for (long y = y0; y < y0 + SQUARE; y++) {
            for (long x = x0; x < x0 + SQUARE; x++) {
                byte c = w.terrainAt(x, y);
                if (World.salt(c)) salt++;
                if (!World.dry(c)) continue;
                dry++;
                if (w.landmassAt(x, y) != id) { pure = false; continue; }
                onId++;
                if (w.reliefAt(x, y) > World.FOOTHILL) continue;
                if (saltIn(w, x0, y0, x + 1, y) || saltIn(w, x0, y0, x - 1, y) || saltIn(w, x0, y0, x, y + 1) || saltIn(w, x0, y0, x, y - 1)) shore++;
            }
        }
        return new Square(x0, y0, id, onId, dry, salt, shore, pure);
    }

    private static boolean saltIn(World w, long x0, long y0, long x, long y) {
        return x >= x0 && x < x0 + SQUARE && y >= y0 && y < y0 + SQUARE && World.salt(w.terrainAt(x, y));
    }

    /**
     * The nearest square (tile-aligned, SQUARE plots a side) to (sx, sy) that serves, searched out to `tiles` tiles each
     * way with the model's reader and bounded - a FAIL for the caller, never a hang. Only squares a tile of which can
     * reach the landmass are measured.
     */
    static Square squareWith(World w, int id, long sx, long sy, int tiles, Predicate<Square> serves) {
        long tx0 = Math.floorDiv(sx, World.TILE), ty0 = Math.floorDiv(sy, World.TILE);
        List<long[]> candidates = new ArrayList<>();
        for (long dy = -tiles; dy <= tiles; dy++) for (long dx = -tiles; dx <= tiles; dx++) candidates.add(new long[] { tx0 + dx, ty0 + dy, dx * dx + dy * dy });
        candidates.sort(Comparator.comparingLong((long[] c) -> c[2]).thenComparingLong(c -> c[1]).thenComparingLong(c -> c[0]));
        for (long[] c : candidates) {
            boolean near = false;
            for (int j = 0; j < 2 && !near; j++) for (int i = 0; i < 2 && !near; i++) near = Arrays.binarySearch(w.idsNear(c[0] + i, c[1] + j), id) >= 0;
            if (!near) continue;
            Square s = measure(w, c[0] * World.TILE, c[1] * World.TILE, id);
            if (serves.test(s)) return s;
        }
        return null;
    }

    /** The plot of a latitude and longitude on a world. */
    static long[] plotOf(World w, double lat, double lon) {
        double[] p = new double[2];
        w.earthTerrain().projection().plotOf(lat, lon, p);
        return new long[] { (long) Math.floor(p[0]), (long) Math.floor(p[1]) };
    }

    /* =====================================================================
       MAIN
       ===================================================================== */

    public static void main(String[] args) throws Exception {
        out.println("--- the fixture ---");
        byte[] fixture = EarthCheck.resource(EarthCheck.FIXTURE);
        check("EarthCheck's fixture is beside the classes (" + EarthCheck.FIXTURE + ")", fixture != null);
        if (fixture == null) {
            out.println("\n" + fails + " FAILED");
            System.exit(1);
        }
        EarthData data = EarthData.of(fixture);
        String id = EarthTerrain.use(data);
        World.Key rotterdam = World.Key.earth(SEED, RLAT, RLON, id);

        generatedWorld();
        theReader(data, rotterdam);
        Fixtures f = fixtures(rotterdam);
        theBooks(f);
        freeGroundIsLinked(f);
        onlyAPort(f, id);
        thePickers(f, id);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       0. A GENERATED WORLD HAS NO LANDMASSES
       ===================================================================== */

    /** The old expressions of the free ground, written out from the public getters: what a city with one landmass reads. */
    static boolean oldExpressions(String what, LandManager lm, Game g) {
        double owned = lm.getOwnedSqFt(), allocated = lm.getAllocatedSqFt();
        boolean ok = lm.getAvailableSqFt() == Math.max(owned - allocated, 0)
                && lm.getFreeSqFt() == Math.max(owned - allocated, 0)
                && lm.getBuildableSqFt() == owned
                && (!(owned > 0) || lm.getUsedShare() == allocated / owned)
                && lm.getLinkedOwnedSqFt() == owned && lm.getLinkedAllocatedSqFt() == allocated
                && lm.getUnlinkedFreeSqFt() == 0 && lm.getUnlinkedNames().isEmpty();
        for (LandManager.Use u : LandManager.Use.values()) {
            ok &= lm.freeFor("Retail", u) == Math.max(owned - allocated, 0);
            ok &= lm.freeForAnyone(u) == Math.max(owned - allocated, 0);
        }
        coastChecked = 0;
        if (g != null) {
            for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
                if (!(t.isPort() || t.isSeaWater())) continue;
                ok &= g.hasCoastFor(t, 1) == (lm.getSeaKm2() > 0);
                coastChecked++;
            }
            ok &= AutoBuilder.overFull(g) == Math.max(0, allocated - owned);
        }
        return ok;
    }

    /** How many sea terminals and desalination plants the last oldExpressions() asked hasCoastFor() about (the five terminals and the plants). */
    static int coastChecked;

    static void generatedWorld() {
        out.println("\n--- 0. a generated world has no landmasses ---");
        World gen = World.of(SEED);
        int[] ids = new int[World.TILE * World.TILE];
        gen.tileLandmass(gen.foundingX() / World.TILE, gen.foundingY() / World.TILE, ids);
        boolean allZero = true;
        for (int v : ids) allZero &= v == 0;
        check("a generated world reads no landmass: hasLandmasses() false, every plot of a tile 0, no ids near a tile, none at a plot",
                !gen.hasLandmasses() && allZero && gen.idsNear(gen.foundingX() / World.TILE, gen.foundingY() / World.TILE).length == 0
                        && gen.landmassAt(gen.foundingX(), gen.foundingY()) == 0);

        // A land of three holdings far apart on the default world (IslandNetCheck 7's shape: islands of a city): one landmass, home.
        LandManager lm = new LandManager();
        lm.getCityLand();
        long sx = lm.getCityLand().siteX(), sy = lm.getCityLand().siteY();
        quietly(() -> {
            lm.buyRectForTest(sx + 3_000, sy, sx + 3_000 + SQUARE, sy + SQUARE, 1);
            lm.buyRectForTest(sx + 6_000, sy, sx + 6_000 + SQUARE, sy + SQUARE, 1);
            lm.setAllocatedSqFt(lm.getOwnedSqFt() / 3);
            lm.updateMarket(10_000);
        });
        Landmasses lms = lm.getLandmasses();
        boolean flat = true;
        for (LandParcel p : lm.getListing()) flat &= p.getUnlinkedDryKm2() == 0 && p.getUnlinkedBuildableKm2() == 0 && p.getUnlinkedLargest() == 0;
        report("a land of three holdings far apart on a generated world: one landmass, nothing read, nothing unlinked, every offer's figure 0",
                lm.getCityLand().purchases().size() == 2 && lms.single() && !lms.anyUnlinked() && !lms.active() && lms.plotsRead() == 0
                        && lms.rows().size() == 1 && flat && lm.getListing().size() > 0,
                lm.getCityLand().purchases().size() + " purchases, " + lm.getListing().size() + " offers");
        check("...its free ground, share used, zone size and the unlinked figures the old expressions written out inline", oldExpressions("three holdings", lm, null));

        // The default seed after 120 months.
        Game g = new Game(GameFiles.scratch("landmass-default"));
        long[] t0 = { System.nanoTime() };
        quietly(() -> {
            g.newGame(Founding.defaults());
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.buildStack(g.getBuildingManager().getTemplateByName("House"), 60, true);
            g.simulateMonths(120);
        });
        LandManager dm = g.getLandManager();
        boolean old = oldExpressions("default", dm, g);
        report("the default seed after 120 months: one landmass, nothing read, no offer with a figure, the old expressions for the free ground, the share used, the coast (five terminals and the desalination plants) and overFull",
                g.getMonth() > 100 && dm.getLandmasses().single() && dm.getLandmasses().plotsRead() == 0 && !dm.getLandmasses().anyUnlinked()
                        && dm.getListing().stream().allMatch(p -> p.getUnlinkedDryKm2() == 0) && old && coastChecked >= 6,
                String.format("month %d, %d coast templates asked, %.1f s", g.getMonth(), coastChecked, (System.nanoTime() - t0[0]) / 1e9));
    }

    /* =====================================================================
       1. THE READER
       ===================================================================== */

    /** The nearest land cell's id by a loop over the data's own landmassAt(): the cell holding the point, or the least dr squared + dc squared within LANDMASS_REACH, the lowest id on a tie, 0 for none. */
    static int bruteResolved(EarthData d, double lat, double lon) {
        int own = d.landmassAt(lat, lon);
        if (own != 0) return own;
        int cpd = 240;
        long g = Math.min(180L * cpd - 1, Math.max(0, (long) Math.floor((90 - lat) * cpd)));
        double lon360 = ((lon + 180) % 360 + 360) % 360;
        long c = (long) Math.floor(lon360 * cpd) % (360L * cpd);
        int best = 0;
        long bestD = Long.MAX_VALUE;
        for (int dr = -EarthTerrain.LANDMASS_REACH; dr <= EarthTerrain.LANDMASS_REACH; dr++) {
            long r = g + dr;
            if (r < 0 || r >= 180L * cpd) continue;
            for (int dc = -EarthTerrain.LANDMASS_REACH; dc <= EarthTerrain.LANDMASS_REACH; dc++) {
                if (dr == 0 && dc == 0) continue;
                double la = 90 - (r + 0.5) / cpd, lo = -180 + (Math.floorMod(c + dc, 360L * cpd) + 0.5) / cpd;
                int id = d.landmassAt(la, lo);
                long dist = (long) dr * dr + (long) dc * dc;
                if (id == 0 || dist > bestD) continue;
                if (dist < bestD || id < best) { bestD = dist; best = id; }
            }
        }
        return best;
    }

    static void theReader(EarthData d, World.Key key) {
        out.println("\n--- 1. the reader: a plot's landmass, and the nearest land cell for a dry plot on a cell with none ---");
        World w = World.of(key);
        EarthTerrain et = w.earthTerrain();
        check("the Earth fixture's world has landmasses to read", w.hasLandmasses() && w.isEarth() && d.hasRelief());
        long sx = w.foundingX(), sy = w.foundingY();
        // The window of tiles about the island west of the city (the bake's Goeree-Overflakkee is 648).
        long[] c648 = plotOf(w, 51.848, 3.998);
        long txa = Math.floorDiv(c648[0], World.TILE) - 8, tya = Math.floorDiv(c648[1], World.TILE) - 8, txb = txa + 32, tyb = tya + 32;
        int[] tile = new int[World.TILE * World.TILE];
        byte[] cover = new byte[World.TILE * World.TILE];
        boolean same = true;
        long plots = 0, rawZeroDry = 0, wrong = 0, toIsland = 0, toHome = 0, toNone = 0;
        // The near-ids gate (Landmasses.near()): a tile whose idsNear() holds only home is read as all home's, so idsNear() must hold every id its plots resolve to.
        long reachTiles = 0, outsideNear = 0;
        double[] ll = new double[2];
        for (long ty = tya; ty < tyb; ty++) {
            for (long tx = txa; tx < txb; tx++) {
                w.tileLandmass(tx, ty, tile);
                w.tileTerrain(tx, ty, cover);
                EarthProj.Blend b = et.projection().blend(tx * World.TILE, ty * World.TILE, World.TILE);
                for (int i = 0; i < tile.length; i++) {
                    long x = tx * World.TILE + i % World.TILE, y = ty * World.TILE + i / World.TILE;
                    plots++;
                    same &= tile[i] == w.landmassAt(x, y);
                    boolean land = World.dry(cover[i]) || cover[i] == World.ICE;
                    if (!land) { same &= tile[i] == 0; continue; }
                    if (!b.latLon(x + 0.5, y + 0.5, ll)) continue;
                    int want = bruteResolved(d, ll[0], ll[1]);
                    if (tile[i] != want) wrong++;
                    if (d.landmassAt(ll[0], ll[1]) == 0 && World.dry(cover[i])) {
                        rawZeroDry++;
                        if (tile[i] == 648) toIsland++;
                        else if (tile[i] == 1) toHome++;
                        else if (tile[i] == 0) toNone++;
                    }
                }
                // The ids the tile's own cells hold (the cell under every plot's centre, water plots too), against the ids its plots resolve to and idsNear().
                java.util.TreeSet<Integer> own = new java.util.TreeSet<>();
                for (int i = 0; i < tile.length; i++) {
                    if (b.latLon(tx * World.TILE + i % World.TILE + 0.5, ty * World.TILE + i / World.TILE + 0.5, ll)) {
                        int raw = d.landmassAt(ll[0], ll[1]);
                        if (raw != 0) own.add(raw);
                    }
                }
                int[] near = w.idsNear(tx, ty);
                boolean reaches = false;
                for (int id : tile) {
                    if (id == 0) continue;
                    if (Arrays.binarySearch(near, id) < 0) outsideNear++;
                    if (!own.contains(id)) reaches = true;
                }
                if (reaches) reachTiles++;
            }
        }
        report("tileLandmass() is landmassAt() at every plot of " + plots + " plots, and water reads 0", same, plots + " plots");
        report("premise: dry plots on a cell the layer calls no land exist, and some resolve to the island, some to the mainland",
                rawZeroDry > 0 && toIsland > 0 && toHome > 0, String.format("%,d such plots: %,d to the island (648), %,d to home (1), %,d to none", rawZeroDry, toIsland, toHome, toNone));
        report("every land plot is the nearest land cell's id (a brute-force loop over the data's own landmassAt(), the lowest id on a tie)", wrong == 0, wrong + " of " + plots + " differ");
        report("premise: tiles exist whose plots resolve to an id none of the tile's own cells holds, only a cell within the reach does", reachTiles > 0, reachTiles + " such tiles of the window's " + (tyb - tya) * (txb - txa));
        report("idsNear() holds every id the tile's plots resolve to, the reach's included (the gate reads a tile whose idsNear() is home's alone as all home's)", outsideNear == 0, outsideNear + " plots resolve to an id idsNear() lacks");

        // Two worlds of one key agree.
        EarthTerrain twin = new EarthTerrain(key);
        int[] other = new int[World.TILE * World.TILE];
        boolean agree = true;
        for (long ty = tya; ty < tya + 6; ty++) {
            for (long tx = txa + 8; tx < txa + 14; tx++) {
                w.tileLandmass(tx, ty + 8, tile);
                twin.tileLandmass(tx, ty + 8, other);
                agree &= Arrays.equals(tile, other);
            }
        }
        check("two worlds of one key agree on a window of tiles (the reader is a pure function of the key)", agree);

        // Home is the card's id.
        EarthSite.Card card = EarthSite.measure(key);
        int home = w.landmassAt(sx, sy);
        report("home is the id the founding card reads at the snapped site: Afro-Eurasia's, 1", card.landmass() != null && card.landmass().id() == home && home == 1,
                "card " + (card.landmass() == null ? "none" : card.landmass().id()) + ", plot " + home);
        // ...and every window of the fixture: the card is the reader's at its own site.
        boolean cards = true;
        int sites = 0;
        String detail = "";
        byte[] expect = null;
        try { expect = EarthCheck.resource(EarthCheck.EXPECT); } catch (java.io.IOException e) { detail = e.toString(); }
        if (expect != null) {
            for (String line : new String(expect, StandardCharsets.UTF_8).split("\n")) {
                String[] f = line.split("\t", -1);
                if (f.length < 5 || !f[1].equals("window")) continue;
                World.Key k = World.Key.earth(SEED, Double.parseDouble(f[3]), Double.parseDouble(f[4]), key.data());
                EarthSite.Card c = EarthSite.measure(k);
                if (!c.hasSite()) continue;
                sites++;
                World ww = World.of(k);
                EarthData.Landmass want = ww.hasLandmasses() ? d.landmass(ww.landmassAt(c.siteX(), c.siteY())) : null;
                boolean here = c.landmass() == null ? want == null : c.landmass().equals(want);
                cards &= here;
                detail += " " + f[2] + (c.landmass() == null ? ":-" : ":" + c.landmass().id());
            }
        }
        report("the card's landmass is the reader's at the snapped site, for each window of the fixture", cards && sites >= 3, sites + " sites:" + detail);
    }

    /* =====================================================================
       THE FIXTURE CITIES
       ===================================================================== */

    /** The squares and cities sections 2 to 8 share. */
    static final class Fixtures {
        World.Key key;
        City a, b;
        Square withShore, withoutShore, homeSalt;
        LandParcel island;
        boolean ok;
    }

    static Fixtures fixtures(World.Key key) {
        out.println("\n--- the fixture cities: Rotterdam with the island square (A) and without it (B), the squares found by search ---");
        Fixtures f = new Fixtures();
        f.key = key;
        World w = World.of(key);
        long[] island = plotOf(w, 51.848, 3.998), south = plotOf(w, 51.75, 4.20), homeSalt = plotOf(w, 51.906, 4.2667);
        f.withShore = squareWith(w, 648, island[0], island[1], 40, s -> s.pure() && s.onId() >= 2000 && s.shore() >= 20);
        f.withoutShore = squareWith(w, 648, south[0], south[1], 40, s -> s.pure() && s.onId() >= 3500 && s.salt() == 0);
        f.homeSalt = squareWith(w, 1, homeSalt[0], homeSalt[1], 30, s -> s.pure() && s.onId() >= 1000 && s.shore() >= 60);
        report("fixture: an island square with dry ground of 648 and salt beside it (the island's north-west tip), pure",
                f.withShore != null, f.withShore == null ? "none" : String.format("at (%d, %d): %d dry plots of 648, %d shore plots, %d salt", f.withShore.x0, f.withShore.y0, f.withShore.onId, f.withShore.shore, f.withShore.salt));
        report("fixture: an island square of 648 with no salt in it (Overflakkee's fresh Haringvliet side), pure",
                f.withoutShore != null, f.withoutShore == null ? "none" : String.format("at (%d, %d): %d dry plots of 648, %d salt", f.withoutShore.x0, f.withoutShore.y0, f.withoutShore.onId, f.withoutShore.salt));
        report("fixture: a home square with salt beside its dry ground, pure", f.homeSalt != null,
                f.homeSalt == null ? "none" : String.format("at (%d, %d): %d dry plots of home, %d shore plots", f.homeSalt.x0, f.homeSalt.y0, f.homeSalt.onId, f.homeSalt.shore));
        if (f.withShore == null || f.withoutShore == null || f.homeSalt == null) return f;
        f.a = new City("landmass-a", key, 40);
        f.b = new City("landmass-b", key, 40);
        report("the founded Rotterdam city, a single landmass: its free ground, share used, coast and overFull are the old expressions written out inline",
                oldExpressions("rotterdam", f.b.lm, f.b.g) && coastChecked >= 6 && f.b.books().plotsRead() == 0 && f.b.books().active(), coastChecked + " coast templates asked");
        // Zoned, so that a zone's size is a figure to compare.
        String retail = f.a.g.getSectors().retail().key();
        quietly(() -> {
            f.a.g.setZone(retail, Zoning.Size.SHARE, 0.1, 0.8);
            f.b.g.setZone(retail, Zoning.Size.SHARE, 0.1, 0.8);
        });
        f.island = f.a.buy(f.withShore);
        f.ok = true;
        return f;
    }

    /* =====================================================================
       2. THE BOOKS BY LANDMASS
       ===================================================================== */

    /** One landmass's owned ground counted plot by plot over the box of the city's ground. */
    static final class Brute {
        final long[] relief = new long[World.RELIEFS];
        long forest, shore;
        int firstHolding = Integer.MAX_VALUE;
        long dry() { long d = 0; for (long r : relief) d += r; return d; }
    }

    static TreeMap<Integer, Brute> bruteBooks(World w, CityLand land, int home) {
        TreeMap<Integer, Brute> by = new TreeMap<>();
        LandGrid grid = land.grid();
        for (long y = grid.minY(); y < grid.maxY(); y++) {
            for (long x = grid.minX(); x < grid.maxX(); x++) {
                int h = land.holdingOf(x, y);
                if (h < 0) continue;
                byte c = w.terrainAt(x, y);
                if (!World.dry(c)) continue;
                int id = w.landmassAt(x, y);
                if (id == 0) id = home;
                Brute b = by.computeIfAbsent(id, k -> new Brute());
                byte rel = w.reliefAt(x, y);
                b.relief[rel]++;
                if (c == World.FOREST) b.forest++;
                b.firstHolding = Math.min(b.firstHolding, h);
                if (rel <= World.FOOTHILL && (ownedSalt(w, land, x + 1, y) || ownedSalt(w, land, x - 1, y) || ownedSalt(w, land, x, y + 1) || ownedSalt(w, land, x, y - 1))) b.shore++;
            }
        }
        return by;
    }

    private static boolean ownedSalt(World w, CityLand land, long x, long y) {
        return land.ownsPlot(x, y) && World.salt(w.terrainAt(x, y));
    }

    static void theBooks(Fixtures f) {
        out.println("\n--- 2. the books by landmass: counted plot by plot, adding up to the land's dry ground, named, and the linked set ---");
        if (!f.ok) { check("fixtures were found", false); return; }
        City a = f.a;
        Landmasses lms = a.books();
        CityLand land = a.g.getCityLand();
        int home = lms.home();
        report("premise: the founded Rotterdam twin owns nothing near another landmass - its tiles hold no landmass but home's - and reads no plot",
                f.b.books().single() && f.b.books().plotsRead() == 0 && !f.b.books().anyUnlinked() && f.b.books().rows().size() == 1,
                "B read " + f.b.books().plotsRead() + " plots");
        TreeMap<Integer, Brute> brute = bruteBooks(a.w, land, home);
        List<Landmasses.Row> rows = lms.rows();
        boolean equal = sameAsBrute(rows, brute) && !lms.single();
        long sum = 0;
        for (Landmasses.Row r : rows) sum += r.dryPlots();
        report("each landmass's dry plots by relief, forest, shore and first holding equal a brute-force count over every owned plot",
                equal, rows.size() + " landmasses: " + describe(rows));
        long books = Math.round(land.totalKm2(CityLand.DRY) / World.KM2_PER_PLOT);
        long ownedPlots = 0;
        for (Brute b : brute.values()) ownedPlots += b.dry();
        report("the landmasses' dry plots, home's included, add up to the land's dry ground to the plot - and to the dry plots the grid owns",
                sum == books && sum == ownedPlots, sum + " plots, the books " + books + ", the grid " + ownedPlots);
        long island = 0;
        for (Landmasses.Row r : rows) if (!r.home()) island += r.dryPlots();
        report("premise: the island square's dry plots are on the island, and its shore is on it", island >= f.withShore.onId() && lms.shorePlots(648) > 0 && lms.shorePlots(648) >= f.withShore.shore(),
                island + " plots, " + lms.shorePlots(648) + " shore");

        // A recount is a fresh recount.
        Landmasses fresh = new Landmasses();
        fresh.recount(a.w, land);
        fresh.refresh();
        check("a recount after a purchase is a fresh recount: the same rows, the same unlinked ground", fresh.rows().equals(lms.rows())
                && fresh.unlinkedDrySqFt() == lms.unlinkedDrySqFt() && fresh.unlinkedFreeSqFt() == lms.unlinkedFreeSqFt());
        // The founded city read no plot; the one with the island read some, and not many.
        report("the city that owns the island read plots (it had to); the founded twin read none", lms.plotsRead() > 0 && f.b.books().plotsRead() == 0,
                lms.plotsRead() + " plots read against 0");

        // Names.
        EarthData.Landmass data648 = a.w.earthTerrain().data().landmass(648);
        report("names: home is \"Home\", an island the data leaves unnamed is \"Island 2\" (N from 2 in the order the city first owned them)",
                data648 != null && data648.name().isBlank() && lms.name(home).equals("Home") && lms.name(648).equals("Island 2")
                        && rows.get(0).name().equals("Home") && rows.get(1).name().equals("Island 2") && lms.unlinkedNames().equals("Island 2"),
                rows.get(0).name() + ", " + rows.get(1).name());
        check("...rows() lists home first, the island's row linked false, home's linked", rows.get(0).home() && rows.get(0).linked() && !rows.get(1).linked() && rows.get(1).id() == 648);
        check("the island is unlinked (no port stands): home and 'no land' (0) are linked, 648 is not; the linked set is home", lms.linked(home) && lms.linked(0) && !lms.linked(648)
                && lms.anyUnlinked() && lms.unlinkedIds().equals(List.of(648)));
        report("the unlinked sums are the island's: dry, by relief, forest, in square feet",
                near(lms.unlinkedDrySqFt(), island * SQ_FT_PER_PLOT) && near(lms.unlinkedReliefSqFt(World.FLAT) + lms.unlinkedReliefSqFt(World.FOOTHILL), island * SQ_FT_PER_PLOT)
                        && near(lms.unlinkedForestSqFt(), brute.get(648).forest * SQ_FT_PER_PLOT) && lms.unlinkedPortsSqFt() == 0,
                String.format("%,.0f sq ft unlinked dry", lms.unlinkedDrySqFt()));

        settledShore(f);
        shoreAcrossTileEdges(f);

        // The closure, unit-tested.
        out.println("  the closure of the linked set:");
        TreeMap<Integer, Integer> ports = new TreeMap<>();
        SortedSet<Integer> c0 = Landmasses.closure(1, ports, Landmasses.Joins.NONE);
        ports.put(648, 1);
        SortedSet<Integer> c1 = Landmasses.closure(1, ports, Landmasses.Joins.NONE);
        ports.put(1, 1);
        SortedSet<Integer> c2 = Landmasses.closure(1, ports, Landmasses.Joins.NONE);
        ports.put(432, 2);
        ports.put(5164, 1);
        SortedSet<Integer> c3 = Landmasses.closure(1, ports, Landmasses.Joins.NONE);
        TreeMap<Integer, Integer> alone = new TreeMap<>(Map.of(648, 1, 432, 1));
        SortedSet<Integer> c4 = Landmasses.closure(1, alone, Landmasses.Joins.NONE);
        SortedSet<Integer> c5 = Landmasses.closure(1, new TreeMap<>(Map.of(1, 0, 648, 0)), Landmasses.Joins.NONE);
        Landmasses.Joins bridge = id -> id == 1 ? new int[] { 77 } : id == 77 ? new int[] { 1, 88 } : new int[0];
        SortedSet<Integer> c6 = Landmasses.closure(1, new TreeMap<>(), bridge);
        SortedSet<Integer> c7 = Landmasses.closure(1, new TreeMap<>(Map.of(88, 1, 648, 1)), bridge);
        check("no ports: the closure is home alone", c0.equals(java.util.Set.of(1)));
        check("a port on an island and none at home links nothing: two ports make a route, one makes none", c1.equals(java.util.Set.of(1)));
        check("a port at home and one on the island link the island", c2.equals(java.util.Set.of(1, 648)));
        check("...and a port on a third landmass is linked by home's port with the island's, whatever they number", c3.equals(java.util.Set.of(1, 648, 432, 5164)));
        check("two islands each with a port, and none at home, link nothing", c4.equals(java.util.Set.of(1)));
        check("a landmass whose ports are all gone (count 0) is not linked by them", c5.equals(java.util.Set.of(1)));
        check("a join reaches from a linked landmass to the ones it joins, and on from those (ET6's slot): 1 to 77 to 88", c6.equals(java.util.Set.of(1, 77, 88)));
        check("...and a port on a landmass the joins reach links every landmass with one", c7.equals(java.util.Set.of(1, 77, 88, 648)));
    }

    /** Whether the rows are a brute-force count's: each landmass's dry plots by relief, forest, shore and (off home) first holding. */
    static boolean sameAsBrute(List<Landmasses.Row> rows, TreeMap<Integer, Brute> brute) {
        boolean equal = rows.size() == brute.size();
        for (Landmasses.Row r : rows) {
            Brute b = brute.get(r.id());
            equal &= b != null && r.dryPlots() == b.dry() && r.flatPlots() == b.relief[World.FLAT] && r.foothillPlots() == b.relief[World.FOOTHILL]
                    && r.steepPlots() == b.relief[World.STEEP] && r.verySteepPlots() == b.relief[World.VERY_STEEP] && r.forestPlots() == b.forest
                    && r.shorePlots() == b.shore && (r.home() || r.firstHolding() == b.firstHolding);
        }
        return equal;
    }

    /**
     * A block of home's coast bought beside the island: its interior tiles are wholly owned with wholly owned neighbours, so their shore can
     * never change and the next recount adds it without reading them - and the count is still the brute-force count and a fresh recount.
     */
    static void settledShore(Fixtures f) {
        City c = new City("landmass-c", f.key, 0);
        c.buy(f.withShore);
        long bx = f.homeSalt.x0 - World.TILE, by = f.homeSalt.y0 - World.TILE;
        long r0 = c.books().plotsRead();
        quietly(() -> c.lm.buyRectForTest(bx, by, bx + 4L * World.TILE, by + 4L * World.TILE, c.g.getMonth()));
        long r1 = c.books().plotsRead();
        quietly(() -> c.lm.landChanged());
        long r2 = c.books().plotsRead();
        TreeMap<Integer, Brute> brute = bruteBooks(c.w, c.g.getCityLand(), c.books().home());
        Landmasses fresh = new Landmasses();
        fresh.recount(c.w, c.g.getCityLand());
        fresh.refresh();
        report("a block of home's coast beside the island: the books equal the brute-force count, the recount after it and a fresh recount",
                sameAsBrute(c.books().rows(), brute) && fresh.rows().equals(c.books().rows()) && c.books().shorePlots(c.books().home()) > f.homeSalt.shore() / 2,
                describe(c.books().rows()));
        report("...and the tiles wholly owned with wholly owned neighbours are read once: the next recount reads fewer plots than the one that bought them",
                r2 - r1 < r1 - r0 && r1 - r0 - (r2 - r1) >= World.TILE * World.TILE, (r1 - r0) + " plots read the first time, " + (r2 - r1) + " the second");
    }

    /** Two plots across a tile's edge: a dry flat or foothill plot of home's at the edge of its tile, and the salt plot in the tile beside it. */
    record EdgePair(long ax, long ay, long bx, long by) { }

    /**
     * The nearest pair of plots to (sx, sy), searched out `tiles` tiles each way with the model's reader and bounded: a dry flat or
     * foothill plot of home's on the (dx, dy) side of its tile, with a salt plot straight across that edge. Null when none serves.
     */
    static EdgePair edgePair(World w, int home, long sx, long sy, int tiles, int dx, int dy) {
        long tx0 = Math.floorDiv(sx, World.TILE), ty0 = Math.floorDiv(sy, World.TILE);
        List<long[]> candidates = new ArrayList<>();
        for (long j = -tiles; j <= tiles; j++) for (long i = -tiles; i <= tiles; i++) candidates.add(new long[] { tx0 + i, ty0 + j, i * i + j * j });
        candidates.sort(Comparator.comparingLong((long[] c) -> c[2]).thenComparingLong(c -> c[1]).thenComparingLong(c -> c[0]));
        for (long[] t : candidates) {
            for (int k = 0; k < World.TILE; k++) {
                long ax = t[0] * World.TILE + (dx > 0 ? World.TILE - 1 : dx < 0 ? 0 : k);
                long ay = t[1] * World.TILE + (dy > 0 ? World.TILE - 1 : dy < 0 ? 0 : k);
                if (!World.salt(w.terrainAt(ax + dx, ay + dy))) continue;
                if (!World.dry(w.terrainAt(ax, ay)) || w.reliefAt(ax, ay) > World.FOOTHILL) continue;
                int id = w.landmassAt(ax, ay);
                if (id != 0 && id != home) continue;
                return new EdgePair(ax, ay, ax + dx, ay + dy);
            }
        }
        return null;
    }

    /**
     * The shore at a tile's edge: a dry plot on the edge of its tile with owned salt across it, in the tile beside, is shore though its own
     * tile holds no owned salt. One such pair is bought, one for each side of the tile (east, west, south, north), each as a two-plot
     * rectangle bought through the harness door after the island, so that the city owns ground off home and the shore is counted; the books
     * then gain one shore plot a pair, as the brute-force count says and a fresh recount does.
     */
    static void shoreAcrossTileEdges(Fixtures f) {
        City c = new City("landmass-d", f.key, 0);
        c.buy(f.withShore);
        Landmasses lms = c.books();
        int home = lms.home();
        long[] seed = plotOf(c.w, 51.906, 4.2667);
        String[] sides = { "east", "west", "south", "north" };
        int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        EdgePair[] pairs = new EdgePair[dirs.length];
        boolean found = true;
        StringBuilder detail = new StringBuilder();
        for (int i = 0; i < dirs.length; i++) {
            pairs[i] = edgePair(c.w, home, seed[0], seed[1], 40, dirs[i][0], dirs[i][1]);
            found &= pairs[i] != null;
            detail.append(sides[i]).append(pairs[i] == null ? " none; " : " (" + pairs[i].ax() + ", " + pairs[i].ay() + "); ");
        }
        report("premise: a dry plot of home's with salt across the edge of its tile exists on each side of a tile, near the coast (found by search)", found, detail.toString());
        if (!found) return;
        long before = lms.shorePlots(home);
        long bruteBefore = bruteBooks(c.w, c.g.getCityLand(), home).get(home).shore;
        for (EdgePair p : pairs) {
            quietly(() -> c.lm.buyRectForTest(Math.min(p.ax(), p.bx()), Math.min(p.ay(), p.by()), Math.max(p.ax(), p.bx()) + 1, Math.max(p.ay(), p.by()) + 1, c.g.getMonth()));
        }
        TreeMap<Integer, Brute> brute = bruteBooks(c.w, c.g.getCityLand(), home);
        Landmasses fresh = new Landmasses();
        fresh.recount(c.w, c.g.getCityLand());
        fresh.refresh();
        long gained = lms.shorePlots(home) - before;
        report("a dry plot at a tile's edge beside owned salt across it is shore though its own tile holds none: one more shore plot for each of the four pairs, as the brute-force count says",
                gained == pairs.length && brute.get(home).shore - bruteBefore == pairs.length && sameAsBrute(lms.rows(), brute) && fresh.rows().equals(lms.rows()),
                gained + " shore plots gained, the brute-force count " + (brute.get(home).shore - bruteBefore) + ", for " + pairs.length + " pairs");
    }

    static String describe(List<Landmasses.Row> rows) {
        StringBuilder s = new StringBuilder();
        for (Landmasses.Row r : rows) s.append(r.name()).append(" ").append(r.dryPlots()).append(" dry, ").append(r.shorePlots()).append(" shore; ");
        return s.toString();
    }

    /* =====================================================================
       3. FREE GROUND IS LINKED ONLY
       ===================================================================== */

    static void freeGroundIsLinked(Fixtures f) {
        out.println("\n--- 3. free ground is linked only: the island is owned and priced, and no room for anyone ---");
        if (!f.ok) { check("fixtures were found", false); return; }
        LandManager la = f.a.lm, lb = f.b.lm;
        String retail = f.a.g.getSectors().retail().key();
        double island = f.withShore.onId() * SQ_FT_PER_PLOT;
        report("premise: A owns the island's ground and B does not - A's owned ground is B's and the island's, priced as ever",
                near(la.getOwnedSqFt(), lb.getOwnedSqFt() + island) && la.getAllocatedSqFt() == lb.getAllocatedSqFt() && la.getLandmasses().anyUnlinked()
                        && !lb.getLandmasses().anyUnlinked() && la.getOwnedKm2() > lb.getOwnedKm2(),
                String.format("A %,.0f sq ft, B %,.0f; the island %,.0f", la.getOwnedSqFt(), lb.getOwnedSqFt(), island));
        report("the free ground is the same with the island and without it: getAvailableSqFt, the free ground and the buildable ground",
                near(la.getAvailableSqFt(), lb.getAvailableSqFt()) && near(la.getFreeSqFt(), lb.getFreeSqFt()) && near(la.getBuildableSqFt(), lb.getBuildableSqFt()),
                String.format("A %,.1f, B %,.1f", la.getAvailableSqFt(), lb.getAvailableSqFt()));
        boolean uses = true;
        String shown = "";
        for (LandManager.Use u : LandManager.Use.values()) {
            uses &= near(la.freeFor(retail, u), lb.freeFor(retail, u)) && near(la.freeForAnyone(u), lb.freeForAnyone(u));
            shown += u + " " + String.format("%,.1f/%,.1f ", la.freeFor(retail, u), lb.freeFor(retail, u));
        }
        report("...for each use of the ground, the zoned sector's and anyone's: freeFor and freeForAnyone", uses, shown);
        check("...and a works's: freeForAnyone(true) and a run's", near(la.freeForAnyone(true), lb.freeForAnyone(true)) && near(la.freeForAnyone(LandManager.Use.RUN), lb.freeForAnyone(LandManager.Use.RUN)));
        report("the share used, the utilisation and the zone's size are the same: the island is not in them", Math.abs(la.getUsedShare() - lb.getUsedShare()) < 1e-9
                && Math.abs(la.getUtilisation() - lb.getUtilisation()) < 1e-9 && near(la.zoneSizeOf(retail), lb.zoneSizeOf(retail)),
                String.format("used %.9f / %.9f, zone %,.1f / %,.1f", la.getUsedShare(), lb.getUsedShare(), la.zoneSizeOf(retail), lb.zoneSizeOf(retail)));
        report("the linked ground is the owned less the island's: getLinkedOwnedSqFt, getLinkedAllocatedSqFt", near(la.getLinkedOwnedSqFt(), lb.getOwnedSqFt()) && la.getLinkedAllocatedSqFt() == la.getAllocatedSqFt()
                && near(la.getLinkedOwnedSqFt(), la.getOwnedSqFt() - la.getUnlinkedFreeSqFt()),
                String.format("%,.1f linked of %,.1f owned", la.getLinkedOwnedSqFt(), la.getOwnedSqFt()));

        // What fits city-wide is refused.
        BuildingsTemplate house = f.a.g.getBuildingManager().getTemplateByName("House");
        double plot = house.getLandSqFt();
        double ask = lb.getAvailableSqFt() + plot;
        report("premise: a plot past B's free ground fits A's owned less built ground (the island would hold it)", la.getOwnedSqFt() - la.getAllocatedSqFt() >= ask && ask > lb.getAvailableSqFt(),
                String.format("asking %,.1f of %,.1f unbuilt", ask, la.getOwnedSqFt() - la.getAllocatedSqFt()));
        double askZoned = lb.freeFor(retail, LandManager.Use.BUILDING) + plot;
        check("canAllocate(B's free ground and one plot) is refused on A as it is on B - for anyone and for the zoned sector - and B's whole free ground is allowed on both",
                !la.canAllocate(ask) && !lb.canAllocate(ask) && !la.canAllocate(askZoned, retail, LandManager.Use.BUILDING) && !lb.canAllocate(askZoned, retail, LandManager.Use.BUILDING)
                        && la.canAllocate(lb.getAvailableSqFt()) && lb.canAllocate(lb.getAvailableSqFt())
                        && la.canAllocate(lb.freeFor(retail, LandManager.Use.BUILDING), retail, LandManager.Use.BUILDING));
        // overFull: a city built past its linked ground is over full though the island is unbuilt.
        double was = la.getAllocatedSqFt();
        quietly(() -> la.setAllocatedSqFt(la.getLinkedOwnedSqFt() + 5_000));
        report("AutoBuilder.overFull() sees the linked ground: 5,000 sq ft built past it is over full though the island is unbuilt",
                near(AutoBuilder.overFull(f.a.g), 5_000) && la.getAllocatedSqFt() < la.getOwnedSqFt(), String.format("overFull %,.1f", AutoBuilder.overFull(f.a.g)));
        quietly(() -> la.setAllocatedSqFt(was));
        check("...and the twins' overFull is 0 both (nothing built past the linked ground)", AutoBuilder.overFull(f.a.g) == 0 && AutoBuilder.overFull(f.b.g) == 0);

        // The investors' feed: what the sector planners see.
        BusinessInvestment ia = f.a.g.getBusinessInvestment(), ib = f.b.g.getBusinessInvestment();
        quietly(() -> {
            ia.setLandAvailable(la.getAvailableSqFt(), la.getPricePerSqFt());
            ib.setLandAvailable(lb.getAvailableSqFt(), lb.getPricePerSqFt());
        });
        report("the plots the investors see for a House are the same with the island and without it", ia.plotsAvailableFor(house) == ib.plotsAvailableFor(house),
                ia.plotsAvailableFor(house) + " against " + ib.plotsAvailableFor(house));

        // The unlinked free ground, and the words.
        report("getUnlinkedFreeSqFt() is the square's dry plots in square feet (the island's, nothing built on it)", near(la.getUnlinkedFreeSqFt(), f.withShore.onId() * SQ_FT_PER_PLOT)
                && lb.getUnlinkedFreeSqFt() == 0, String.format("%,.1f sq ft against %d plots", la.getUnlinkedFreeSqFt(), f.withShore.onId()));
        quietly(() -> {
            f.a.g.simulateMonths(1);
            f.b.g.simulateMonths(1);
        });
        String reasonA = ia.landReason(house), reasonB = ib.landReason(house);
        String clause = "; " + LandmassView.unlinkedWords(la.getUnlinkedFreeSqFt(), la.getUnlinkedNames());
        report("the investors' refusal ends with the clause, word for word: \"...; N km2 is free on Island 2, which no port links\"", reasonA.endsWith(clause)
                && reasonA.startsWith("no land - needs ") && !reasonB.contains("no port links"), reasonA);
        check("...and the clause names the island and says no port links it", clause.contains("Island 2") && clause.contains("which no port links") && clause.contains(LandManager.areaWords(la.getUnlinkedFreeSqFt())));
    }

    /* =====================================================================
       4. ONLY A PORT ON UNLINKED GROUND
       ===================================================================== */

    /** A field of a sited resource centred on a dry plot of a landmass but the city's home, nearest to the site, found by search. */
    record FieldAt(Resource resource, Deposit field, int landmass, double km) { }

    static FieldAt nearestField(World w, Resource r, int notLandmass, boolean offHome) {
        long sx = w.foundingX(), sy = w.foundingY();
        int cx = (int) (sx / World.CELL), cy = (int) (sy / World.CELL);
        FieldAt best = null;
        for (int y = cy - 3; y <= cy + 3; y++) {
            for (int x = cx - 3; x <= cx + 3; x++) {
                for (Deposit d : w.fieldsInCell(y * World.CELLS + x, r)) {
                    if (!World.dry(w.terrainAt(d.x(), d.y()))) continue;
                    int l = w.landmassAt(d.x(), d.y());
                    if (l == 0 || (offHome ? l == notLandmass : l != notLandmass)) continue;
                    double km = Math.hypot(d.x() - sx, d.y() - sy) * World.PLOT_M / 1000;
                    if (best == null || km < best.km) best = new FieldAt(r, d, l, km);
                }
            }
        }
        return best;
    }

    /** The city on the island whose mainland holds a field of iron: found by search over the candidate spots. */
    static final class IslandCity {
        City city;
        FieldAt away, home;
        double[] spot;
    }

    static IslandCity onTheIsland(String id) {
        for (double[] spot : ON_THE_ISLAND) {
            World.Key key = World.Key.earth(SEED, spot[0], spot[1], id);
            if (Founding.defaults().withWorldKey(key).problem() != null) continue;
            World w = World.of(key);
            int home = w.landmassAt(w.foundingX(), w.foundingY());
            FieldAt away = nearestField(w, Resource.IRON, home, true);
            if (away == null) continue;
            IslandCity c = new IslandCity();
            c.spot = spot;
            c.away = away;
            c.home = nearestField(w, Resource.IRON, home, false);
            c.city = new City("landmass-island", key, 0);
            return c;
        }
        return null;
    }

    /** Buys a square round a plot (as an offer would be) on a city; the parcel. */
    static LandParcel buyAround(City c, long x, long y) {
        LandParcel[] p = new LandParcel[1];
        quietly(() -> p[0] = c.lm.buyRectForTest(x - SQUARE / 2, y - SQUARE / 2, x + SQUARE / 2, y + SQUARE / 2, c.g.getMonth()));
        return p[0];
    }

    static void onlyAPort(Fixtures f, String dataId) {
        out.println("\n--- 4. only a port on unlinked ground: a House past the linked free ground, and a works on a field across the water ---");
        if (f.ok) {
            BuildingsTemplate house = f.a.g.getBuildingManager().getTemplateByName("House");
            double plot = house.getLandSqFt();
            // The order is sized from the twin without the island - the ground a city on that landmass alone has - not from A's own figure, which is what is under test.
            int past = (int) Math.ceil((f.b.lm.getAvailableSqFt() + plot) / plot);
            report("premise: the island would hold the order - " + past + " Houses are more than the twin without the island has room for, and fit A's owned ground", past * plot > f.b.lm.getAvailableSqFt()
                    && past * plot <= f.a.lm.getOwnedSqFt() - f.a.lm.getAllocatedSqFt(), String.format("%,.0f sq ft asked of %,.0f free without the island, %,.0f unbuilt with it", past * plot, f.b.lm.getAvailableSqFt(), f.a.lm.getOwnedSqFt() - f.a.lm.getAllocatedSqFt()));
            Game.BuildResult[] r = new Game.BuildResult[2];
            quietly(() -> r[0] = f.a.g.buildStack(house, past, true));
            check("a House order past the linked free ground gets NO_LAND on A though the island would hold it", r[0] == Game.BuildResult.NO_LAND);
            int fits = (int) Math.floor(f.a.lm.getAvailableSqFt() / plot);
            quietly(() -> r[1] = f.a.g.buildStack(house, Math.max(1, Math.min(5, fits / 2)), true));
            check("...and an order that fits the linked ground is placed (SUCCESS): the refusal is the island's, not the order's", r[1] == Game.BuildResult.SUCCESS);
        }

        IslandCity ic = onTheIsland(dataId);
        report("fixture: a city founded on Goeree-Overflakkee whose mainland across the water holds a field of iron (found by search)", ic != null,
                ic == null ? "none of the candidate spots serves" : String.format("spot %.2f, %.2f; home %d; a field of %d sites on landmass %d, %.0f km away", ic.spot[0], ic.spot[1],
                        ic.city.w.landmassAt(ic.city.w.foundingX(), ic.city.w.foundingY()), ic.away.field().sites(), ic.away.landmass(), ic.away.km()));
        if (ic == null) return;
        City c = ic.city;
        BuildingsTemplate mine = c.g.getBuildingManager().getTemplateByName("Iron Mine");
        int homeId = c.books().home();
        Deposit field = ic.away.field();
        int before = c.lm.getSites(Resource.IRON);
        LandParcel parcel = buyAround(c, field.x(), field.y());
        Landmasses lms = c.books();
        report("premise: the square holds the mainland's field whole, and the field is centred on a dry plot of a landmass that is not home's",
                parcel.getSites(Resource.IRON) >= field.sites() && parcel.getAmount(Resource.IRON) > 0 && c.w.landmassAt(field.x(), field.y()) == ic.away.landmass()
                        && ic.away.landmass() != homeId && World.dry(c.w.terrainAt(field.x(), field.y())) && lms.anyUnlinked() && !lms.linked(ic.away.landmass()),
                String.format("%d sites in the parcel, field on landmass %d, home %d", parcel.getSites(Resource.IRON), ic.away.landmass(), homeId));
        report("premise: the books hold the field's sites (the land owns them), and the model counts none of them as a site a mine may stand on",
                c.lm.getCityLand().totalSites(Resource.IRON) >= field.sites() && c.lm.getSites(Resource.IRON) == before,
                c.lm.getCityLand().totalSites(Resource.IRON) + " owned, " + c.lm.getSites(Resource.IRON) + " usable");
        check("getSites(), getSites(r, true) and getSites(r, type) all leave the field out", c.lm.getSites(Resource.IRON) == 0 && c.lm.getSites(Resource.IRON, true) == 0
                && c.lm.getSites(Resource.IRON, mine.site().type()) == 0 && c.lm.getIronDeposits() == 0 && c.lm.getOilSites() == 0);
        Game.BuildResult[] built = new Game.BuildResult[1];
        quietly(() -> built[0] = c.g.buildStack(mine, 1, true));
        check("a works on a field centred on an unlinked plot gets NO_DEPOSIT", built[0] == Game.BuildResult.NO_DEPOSIT && !c.g.hasDepositFor(mine, 1));
        // The same kind of field on the home landmass is a deposit.
        if (ic.home != null) {
            LandParcel at = buyAround(c, ic.home.field().x(), ic.home.field().y());
            report("control: a field of iron on the home landmass is a deposit - its sites count, and the same works is not refused for a deposit",
                    ic.home.landmass() == homeId && at.getSites(Resource.IRON) >= ic.home.field().sites() && c.lm.getSites(Resource.IRON) >= ic.home.field().sites()
                            && c.g.hasDepositFor(mine, 1),
                    String.format("%d sites usable, the unlinked field's %d left out", c.lm.getSites(Resource.IRON), field.sites()));
            quietly(() -> built[0] = c.g.buildStack(mine, 1, true));
            check("...and the order is placed or wants only money or land, never NO_DEPOSIT", built[0] != Game.BuildResult.NO_DEPOSIT);
        } else {
            out.println("  no field of iron on the home landmass within reach of the island city: the control was not run");
        }
    }

    /* =====================================================================
       8. THE PICKERS
       ===================================================================== */

    static void thePickers(Fixtures f, String dataId) {
        out.println("\n--- 8. the pickers: an island offer is never the best for room, a shortfall or a deposit ---");
        if (!f.ok) { check("fixtures were found", false); return; }
        City c = new City("landmass-pickers", f.key, 0);
        LandManager lm = c.lm;
        LandMarket market = lm.getMarket();
        Square isl = f.withoutShore;
        // Two squares of home's ground with less dry ground than the island's: the island's dry ground is the only that covers a shortfall.
        long[] near0 = plotOf(c.w, RLAT, 4.2667);
        Square h1 = squareWith(c.w, 1, near0[0], near0[1], 30, s -> s.pure() && s.onId() >= 500 && s.onId() <= 3000);
        Square h2 = h1 == null ? null : squareWith(c.w, 1, h1.x0, h1.y0, 30, s -> s.pure() && s.onId() >= 500 && s.onId() <= 3000
                && (Math.abs(s.x0 - h1.x0) >= SQUARE || Math.abs(s.y0 - h1.y0) >= SQUARE));
        report("fixture: two squares of home's ground, each with less dry ground than the island square, for the shelf", h1 != null && h2 != null && isl.onId() > 3000,
                h1 == null || h2 == null ? "none" : h1.onId() + " and " + h2.onId() + " dry plots against the island's " + isl.onId());
        if (h1 == null || h2 == null) return;
        LandParcel islandOffer = market.measure(new GridOffers.Rect(0, 0, lm.getCityLand().level(), isl.x0, isl.y0, isl.x0 + SQUARE, isl.y0 + SQUARE));
        LandParcel homeOffer = market.measure(new GridOffers.Rect(0, 1, lm.getCityLand().level(), h1.x0, h1.y0, h1.x0 + SQUARE, h1.y0 + SQUARE));
        LandParcel homeBig = market.measure(new GridOffers.Rect(0, 2, lm.getCityLand().level(), h2.x0, h2.y0, h2.x0 + SQUARE, h2.y0 + SQUARE));
        quietly(() -> market.putOffers(List.of(islandOffer, homeOffer, homeBig)));
        LandParcel isle = market.find(islandOffer.getId()), near = market.find(homeOffer.getId()), big = market.find(homeBig.getId());
        report("premise: the shelf holds an all-island offer and two offers of home's ground; the island's is split as unlinked, home's are not",
                isle != null && near != null && big != null && isle.getUnlinkedDryKm2() > 0 && near.getUnlinkedDryKm2() == 0 && big.getUnlinkedDryKm2() == 0,
                String.format("island offer %.4f km2 unlinked of %.4f dry", isle == null ? -1 : isle.getUnlinkedDryKm2(), isle == null ? -1 : isle.getDryKm2()));
        if (isle == null || near == null || big == null) return;
        check("the island offer is all on the island: its unlinked dry ground is its dry ground, its largest landmass 648, and its price is as listed (priced as today)",
                Math.abs(isle.getUnlinkedDryKm2() - isle.getDryKm2()) <= FREE_SLACK / LandManager.SQ_FT_PER_KM2 && isle.getUnlinkedLargest() == 648 && isle.getPriceUsd() > 0);
        report("it has no buildable ground: buildableKm2() and buildableSqFt() are 0, and a home offer has some",
                LandMarket.buildableKm2(isle) <= FREE_SLACK / LandManager.SQ_FT_PER_KM2 && LandMarket.buildableSqFt(isle) <= FREE_SLACK
                        && LandMarket.buildableSqFt(near) > 0 && LandMarket.buildableSqFt(big) > 0 && !LandMarket.anyBuildable(List.of(isle)),
                String.format("%.1f sq ft against %.1f and %.1f", LandMarket.buildableSqFt(isle), LandMarket.buildableSqFt(near), LandMarket.buildableSqFt(big)));
        check("ranked by the buildable ground a dollar, the island offer is worth nothing; by dry ground a dollar, nothing", market.rankPerUsd(isle, true) <= 1e-12 && market.rankPerUsd(isle, false) <= 1e-12
                && market.rankPerUsd(near, true) > 0 && market.rankPerUsd(near, false) > 0);
        for (double cash : new double[] { 1e13, 0 }) {
            quietly(() -> c.g.setCashForTest(cash));
            LandParcel room = c.g.bestOffer(Game.LandNeed.room());
            check(String.format("bestOffer(ROOM) with cash %.0f is a home offer, never the island's", cash), room != null && room.getId() != isle.getId());
        }
        quietly(() -> c.g.setCashForTest(1e13));
        LandParcel value = market.bestValue();
        check("bestValue() is a home offer, never the island's", value != null && value.getId() != isle.getId());
        double shortfall = Math.max(near.getSizeSqFt(), big.getSizeSqFt()) + 1;
        report("premise: only the island's dry ground would cover this shortfall - the island offer is the only one whose dry ground does", isle.getSizeSqFt() >= shortfall
                && near.getSizeSqFt() < shortfall && big.getSizeSqFt() < shortfall, String.format("%,.0f sq ft asked; the island %,.0f, home's %,.0f and %,.0f", shortfall, isle.getSizeSqFt(), near.getSizeSqFt(), big.getSizeSqFt()));
        LandParcel covering = c.g.bestOffer(Game.LandNeed.shortfall(shortfall));
        check("bestOffer(SHORTFALL) of it is not the island's - its dry ground is no ground a building could stand on", covering != null && covering.getId() != isle.getId());
        if (covering != null) {
            boolean[] got = { false };
            quietly(() -> got[0] = c.g.buyLandParcel(covering.getId()));
            check("buying what the shortfall's picker offered buys no island: the offer is bought, and the city still owns ground on one landmass", got[0] && c.lm.getLandmasses().single() && !c.lm.getLandmasses().anyUnlinked());
        }

        // The tag by the share, with the offers' own figures and with a mixed one.
        check("ISLAND is tagged for the island offer and for no offer of home's ground", LandmassView.islandTagged(isle) && !LandmassView.islandTagged(near) && !LandmassView.islandTagged(big));
        double dry = 10, share = LandmassView.ISLAND_TAG_SHARE;
        LandParcel mixed = new LandParcel(900, 0, 3, 0, 0, 0, 0, 0, dryArea(dry), null, new int[CityLand.KINDS], new double[CityLand.KINDS], 100, 0);
        mixed.setUnlinked(dry * share - 0.01, dry * share - 0.01, 648);
        boolean under = !LandmassView.islandTagged(mixed);
        mixed.setUnlinked(dry * share, dry * share, 648);
        boolean at = LandmassView.islandTagged(mixed);
        mixed.setUnlinked(0, 0, 0);
        check("ISLAND is tagged when the unlinked share reaches ISLAND_TAG_SHARE of the offer's dry ground, not below it, and never at 0", under && at && !LandmassView.islandTagged(mixed));

        // A deposit across the water is no deposit for the picker.
        IslandCity ic = onTheIsland(dataId);
        if (ic == null) { check("fixture: a city on the island with a field across the water for the DEPOSIT picker", false); return; }
        City d = ic.city;
        LandMarket dm = d.lm.getMarket();
        Deposit field = ic.away.field();
        LandParcel fieldOffer = dm.measure(new GridOffers.Rect(0, 0, d.lm.getCityLand().level(), field.x() - SQUARE / 2, field.y() - SQUARE / 2, field.x() + SQUARE / 2, field.y() + SQUARE / 2));
        quietly(() -> dm.putOffers(List.of(fieldOffer)));
        LandParcel standing = dm.find(fieldOffer.getId());
        report("premise: the one offer holds the mainland's field of iron and is split as unlinked (the city is on the island)", standing != null && standing.getSites(Resource.IRON) > 0
                && standing.getAmount(Resource.IRON) > 0 && standing.getUnlinkedDryKm2() > 0, standing == null ? "none" : standing.getSites(Resource.IRON) + " sites");
        check("bestOffer(DEPOSIT iron) passes over the offer with a field across the water: nothing to buy", d.g.bestOffer(Game.LandNeed.deposit(Resource.IRON)) == null && dm.cheapestWith(Resource.IRON) == null);
        if (ic.home != null) {
            Deposit hf = ic.home.field();
            LandParcel homeField = dm.measure(new GridOffers.Rect(0, 1, d.lm.getCityLand().level(), hf.x() - SQUARE / 2, hf.y() - SQUARE / 2, hf.x() + SQUARE / 2, hf.y() + SQUARE / 2));
            quietly(() -> dm.putOffers(List.of(fieldOffer, homeField)));
            LandParcel pick = d.g.bestOffer(Game.LandNeed.deposit(Resource.IRON));
            check("...and with a field on the home landmass beside it, the deposit picker takes that one", pick != null && pick.getId() == homeField.getId() && pick.getUnlinkedDryKm2() == 0);
        }
    }

    /** The five areas of an offer of this many square kilometres of dry ground. */
    static double[] dryArea(double km2) {
        double[] a = new double[CityLand.AREAS];
        a[CityLand.TOTAL] = km2;
        a[CityLand.DRY] = km2;
        return a;
    }
}
