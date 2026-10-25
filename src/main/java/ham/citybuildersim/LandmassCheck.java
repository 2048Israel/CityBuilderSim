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
 * The city's ground counted by landmass, and the island rule, checked (ET5; the ET5 design): a landmass is read from the
 * Earth's data at every plot, the city's books are kept by landmass, ground on a landmass the city has not linked is owned,
 * counted and priced as ever but is no room for anyone except a sea terminal, and no works stands on its fields; a terminal
 * ordered for an island takes the island's room, two standing terminals link it, nothing unlinks, and where the terminals
 * stand is saved.
 *
 * WHY THIS EXISTS. Until ET5 the model's ground was one pool with no place in it: an island bought across a strait was
 * room for any building, and the Rotterdam city could "build" on Goeree-Overflakkee from its own streets. Nothing could
 * say so, because nothing read a plot's landmass. This holds the reader, the books and the rule against fixtures that
 * CAUSE each condition (an island square found by search with the model's own reader, bought through the harness door
 * LandManager.buyRectForTest(); a city founded on the island, so that the mainland is across the water), and every
 * figure worked out again from the model's constants and getters, never typed.
 *
 * WHAT IT HAS TO PROVE:
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
 *      a works on a field centred on an unlinked plot is NO_DEPOSIT and the field is in no site count, a field centred on
 *      a lake plot of an island included (4b: the gate reads the plot's 15" cell, asked of a made-up field on a fresh water plot of the island, the fixture holding none); the same field at home is a deposit.
 *      A desalination plant needs shore on a linked landmass, and an island with no shore gives a terminal NO_COAST (7).
 *   5. TWO PORTS LINK. A terminal ordered for the island (the card's pick) takes the island's room, exactly, and moves
 *      nothing the linked buildings stand on; on site or standing alone it links nothing; with home's standing it links
 *      the island and the linked free ground rises by the island's unbuilt ground; a stopped shell never links; a third
 *      landmass is linked by one terminal once a linked one has a port; the island's room, not the pool's, is the gate,
 *      in an order and in a run; an order short of cash keeps its pick.
 *   6. DEMOLITION. The port that links a landmass cannot be demolished (demolishable() leaves it out, demolish() says
 *      "it is the port that links Island 2"); an island's port keeps its ground on the island, by the demolition's id,
 *      until the demolition is done - across a save too - and then its room returns exactly; a shell likewise.
 *   7. THE CHOICE. The card lists the landmasses with shore, home first; its pick is one order's, spent by an order and
 *      forgotten at the settle; the rule (home, else the linked landmass with shore and most ground, else none) never
 *      takes an island no port links; a pick without shore is passed over; the quote is asked for the landmass the order is for,
 *      so a pick standing steers the card's quote and not an order by the rule.
 *   8. THE PICKERS. An all-island offer has no buildable ground and is never the best for room, a shortfall or a
 *      deposit - and would be without its split; ISLAND is tagged by the share; a shortfall only the island's dry ground
 *      would cover buys no island.
 *   9. THE SAVE. The placements under one key, absent for a city on one landmass; read back, resaved identical, a
 *      reload equal to the city played on; a save without the key reads every terminal at home; a bad row is passed over.
 *  12. GROWN ACROSS LANDMASSES. A city that owns the island for 24 months, then the two terminals by the card's path,
 *      then 96 months: every month the books are consistent (the linked free ground fits, the unlinked terminals' ground
 *      is the placements', the placements are the buildings' counts, and no works stands on a field across the water: getSites() is
 *      the pieces on linked ground, counted apart, and the works standing fit them) - the 24 months of a city that grows, and of a
 *      city on the island itself that owns the mainland's field of iron across the water.
 *  13. THE FULL FILE. The delta's islands read Rotterdam's landmass in the model's reader, Goeree does not; PEI is apart,
 *      Halifax and Dartmouth one, Medellin on 2; Rotterdam played 1,200 months, the books audited at every look.
 *
 * Every premise is asserted before what rests on it. The full-file section prints one line and skips without
 * -Dcbs.earth.
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
        final GameFiles files;
        final LandManager lm;
        final World w;
        final World.Key key;

        City(String label, World.Key key, int houses) {
            this.key = key;
            this.files = GameFiles.scratch(label);
            this.g = new Game(files);
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
        lakeCentredField(f);
        twoPortsLink(f, data);
        demolition(f);
        theChoice(f);
        thePickers(f, id);
        theSave(f);
        grown(f);
        fullFile(theEarth());

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

        BusinessInvestment ia = f.a.g.getBusinessInvestment(), ib = f.b.g.getBusinessInvestment();

        // The unlinked free ground, and the words.
        report("getUnlinkedFreeSqFt() is the square's dry plots in square feet (the island's, nothing built on it)", near(la.getUnlinkedFreeSqFt(), f.withShore.onId() * SQ_FT_PER_PLOT)
                && lb.getUnlinkedFreeSqFt() == 0, String.format("%,.1f sq ft against %d plots", la.getUnlinkedFreeSqFt(), f.withShore.onId()));
        quietly(() -> {
            f.a.g.simulateMonths(1);
            f.b.g.simulateMonths(1);
        });
        // The investors' feed, as the month feeds it (Game.refreshLand(), run for every sector before it plans): the plots the planners see
        // are the twin's, whatever the island holds - and the feed was fed, or two zeros would agree.
        int plotsA = ia.plotsAvailableFor(house), plotsB = ib.plotsAvailableFor(house);
        report("the plots the investors see for a House, fed by the month's own refreshLand(), are the same with the island and without it - and are some",
                plotsA == plotsB && plotsA > 0, plotsA + " against " + plotsB);
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
        // PREMISE (the review of slice A): the island offer would win without its split - the picks below are the split's doing, not the shelf's.
        // Its unlinked figures are put to nothing on the listed offer, the pickers asked, and the figures put back.
        double keptDry = isle.getUnlinkedDryKm2(), keptBuildable = isle.getUnlinkedBuildableKm2();
        int keptLargest = isle.getUnlinkedLargest();
        isle.setUnlinked(0, 0, 0);
        StringBuilder wouldWin = new StringBuilder();
        boolean roomWins = true;
        for (double cash : new double[] { 1e13, 0 }) {
            quietly(() -> c.g.setCashForTest(cash));
            LandParcel room = c.g.bestOffer(Game.LandNeed.room());
            roomWins &= room != null && room.getId() == isle.getId();
            wouldWin.append(String.format("ROOM at cash %.0f: %s; ", cash, room == null ? "none" : room.getId() == isle.getId() ? "the island's" : "a home offer"));
        }
        quietly(() -> c.g.setCashForTest(1e13));
        LandParcel valueUnsplit = market.bestValue();
        wouldWin.append("bestValue: ").append(valueUnsplit == null ? "none" : valueUnsplit.getId() == isle.getId() ? "the island's" : "a home offer");
        double rankUnsplit = market.rankPerUsd(isle, true), rankHome = Math.max(market.rankPerUsd(near, true), market.rankPerUsd(big, true));
        isle.setUnlinked(keptDry, keptBuildable, keptLargest);
        report("premise: without its split the island offer would be the best for room (at both cash levels) and the best value - its buildable ground a dollar beats every home offer's",
                roomWins && valueUnsplit != null && valueUnsplit.getId() == isle.getId() && rankUnsplit > rankHome,
                wouldWin + String.format("; rank %.3e against %.3e", rankUnsplit, rankHome));
        check("...and the split is back: the island offer is unlinked again", isle.getUnlinkedDryKm2() == keptDry && isle.getUnlinkedLargest() == 648);
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
        check("...and so does richest() (the test player buys its oil with it): no works stands on a field across the water", dm.richest(Resource.IRON) == null);
        if (ic.home != null) {
            Deposit hf = ic.home.field();
            LandParcel homeField = dm.measure(new GridOffers.Rect(0, 1, d.lm.getCityLand().level(), hf.x() - SQUARE / 2, hf.y() - SQUARE / 2, hf.x() + SQUARE / 2, hf.y() + SQUARE / 2));
            quietly(() -> dm.putOffers(List.of(fieldOffer, homeField)));
            LandParcel pick = d.g.bestOffer(Game.LandNeed.deposit(Resource.IRON));
            check("...and with a field on the home landmass beside it, the deposit picker takes that one", pick != null && pick.getId() == homeField.getId() && pick.getUnlinkedDryKm2() == 0);
        }
    }

    /* =====================================================================
       4 (continued). A FIELD CENTRED ON A LAKE PLOT OF AN ISLAND
       ===================================================================== */

    /** A field of a sited resource centred on a water plot that is not the sea's (a lake, a river), whose 15" cell the layer puts on a landmass but home's: the nearest to a plot, found by search over the cells round it. */
    static FieldAt lakeCentred(World w, int home, long[] near) {
        FieldAt best = null;
        int cx = (int) (near[0] / World.CELL), cy = (int) (near[1] / World.CELL);
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            for (int y = cy - 8; y <= cy + 8; y++) {
                for (int x = cx - 8; x <= cx + 8; x++) {
                    for (Deposit d : w.fieldsInCell(y * World.CELLS + x, r)) {
                        if (World.dry(w.terrainAt(d.x(), d.y())) || w.depthAt(d.x(), d.y()) > 0) continue;
                        int l = w.landmassOfPlot(d.x(), d.y());
                        if (l == 0 || l == home || w.landmassAt(d.x(), d.y()) != 0) continue;
                        double km = Math.hypot(d.x() - near[0], d.y() - near[1]) * World.PLOT_M / 1000;
                        if (best == null || km < best.km) best = new FieldAt(r, d, l, km);
                    }
                }
            }
        }
        return best;
    }

    /**
     * The review of slice A, (b): the works gate reads a field's landmass from its centre plot's 15" cell resolved by the D2 rule, not from
     * the plot, so a field centred on a lake plot of an island (landmassAt() gives a water plot 0, which reads as home) is still on the island.
     */
    static void lakeCentredField(Fixtures f) {
        out.println("\n--- 4b. a field centred on a lake plot of an island is on the island: the works gate reads the plot's cell, not the plot ---");
        if (!f.ok) { check("fixtures were found", false); return; }
        World w = World.of(f.key);
        int home = w.landmassAt(w.foundingX(), w.foundingY());
        int isle = f.withShore.id();
        // The reader the gate uses: at a dry plot it is landmassAt(); at a fresh water plot beside the island it is the island's, where landmassAt() gives 0. The sea is left out (the gate leaves a sea field its own): the first square holds salt by construction, the second none.
        long same = 0, differ = 0, freshOnIsland = 0, freshElsewhere = 0, sea = 0;
        long[] lake = null;
        for (Square sq : new Square[] { f.withShore, f.withoutShore }) {
            for (long y = sq.y0(); y < sq.y0() + SQUARE; y++) {
                for (long x = sq.x0(); x < sq.x0() + SQUARE; x++) {
                    byte c = w.terrainAt(x, y);
                    int at = w.landmassAt(x, y), cell = w.landmassOfPlot(x, y);
                    if (World.dry(c)) { if (at == cell) same++; else differ++; }
                    else if (at != 0) differ++;
                    else if (World.salt(c) || w.depthAt(x, y) > 0) sea++;
                    else if (cell == isle) { freshOnIsland++; if (lake == null) lake = new long[] { x, y }; }
                    else freshElsewhere++;
                }
            }
        }
        report("the plot's cell reader agrees with landmassAt() at every dry plot of the island squares, and puts the fresh water plots about the island on the island (landmassAt() gives them 0)",
                differ == 0 && same > 0 && freshOnIsland > 0, same + " dry plots agree; " + freshOnIsland + " fresh water plots read the island, " + freshElsewhere + " read none, " + sea + " sea plots left out");
        if (lake == null) { check("premise: a fresh water plot of the island was found to centre a field on", false); return; }
        // The gate, asked of fields made up for the purpose (the fixture holds no real field centred on a lake plot of an island: the search below finds none).
        City c = new City("landmass-lake", f.key, 0);
        c.buy(f.withShore);
        Landmasses lms = c.books();
        long[] sea1 = null, dryIsle = null;
        for (long y = f.withShore.y0(); y < f.withShore.y0() + SQUARE && (sea1 == null || dryIsle == null); y++) {
            for (long x = f.withShore.x0(); x < f.withShore.x0() + SQUARE; x++) {
                if (sea1 == null && w.depthAt(x, y) > 0 && World.salt(w.terrainAt(x, y))) sea1 = new long[] { x, y };
                if (dryIsle == null && World.dry(w.terrainAt(x, y)) && w.landmassAt(x, y) == isle) dryIsle = new long[] { x, y };
            }
        }
        report("premise: the lake plot is not the sea's, its plot reads landmass 0 and its cell the island, which the city owns and no port links; there is a sea plot and a dry plot of the island to compare with",
                !World.dry(w.terrainAt(lake[0], lake[1])) && w.depthAt(lake[0], lake[1]) <= 0 && w.landmassAt(lake[0], lake[1]) == 0 && w.landmassOfPlot(lake[0], lake[1]) == isle
                        && lms.anyUnlinked() && !lms.linked(isle) && sea1 != null && dryIsle != null,
                String.format("lake plot (%d, %d), island %d, home %d", lake[0], lake[1], isle, home));
        if (sea1 == null || dryIsle == null) return;
        Resource r = Resource.IRON;
        int sites = 5;
        CityLand.Piece onLake = pieceAt(r, lake[0], lake[1], sites), onDry = pieceAt(r, dryIsle[0], dryIsle[1], sites), inSea = pieceAt(r, sea1[0], sea1[1], sites),
                onHome = pieceAt(r, w.foundingX(), w.foundingY(), sites);
        check("a field centred on a lake plot of the unlinked island gives no site to a works: the gate reads the plot's cell, which landmassAt() gives home's", c.lm.freeSites(onLake) == 0);
        check("...as a field centred on a dry plot of the island gives none", c.lm.freeSites(onDry) == 0);
        check("...while a field centred on a dry plot of home gives its sites, and a field in the sea gives its own", c.lm.freeSites(onHome) == sites && c.lm.freeSites(inSea) == sites);
        // The real fields of the fixture: any centred on a lake plot of an island?
        FieldAt real = lakeCentred(w, home, plotOf(w, 51.848, 3.998));
        if (real == null) {
            out.println("  no field of a sited resource is centred on a lake or river plot of an island within reach of Goeree-Overflakkee in the fixture: the gate above was asked of a made-up field");
            return;
        }
        City d = new City("landmass-lake-field", f.key, 0);
        Resource rr = real.resource();
        int beforeDry = d.lm.getSites(rr, true);
        LandParcel parcel = buyAround(d, real.field().x(), real.field().y());
        Landmasses dl = d.books();
        report("premise: the square holds the field whole, its centre is a water plot that is not the sea's, the plot reads landmass 0 (home) and its cell the island, which no port links",
                parcel.getSites(rr) >= real.field().sites() && !World.dry(w.terrainAt(real.field().x(), real.field().y())) && w.depthAt(real.field().x(), real.field().y()) <= 0
                        && w.landmassAt(real.field().x(), real.field().y()) == 0 && w.landmassOfPlot(real.field().x(), real.field().y()) == real.landmass()
                        && dl.anyUnlinked() && !dl.linked(real.landmass()),
                String.format("a field of %s, %d sites, on landmass %d, %.1f km from the island's tip", rr, real.field().sites(), real.landmass(), real.km()));
        check("the land owns the field, and no dry site counts it: getSites(r, true) is as it was before the purchase", d.lm.getCityLand().totalSites(rr) >= real.field().sites()
                && d.lm.getSites(rr, true) == beforeDry);
    }

    /** A piece of the ground holding a made-up field of `sites` sites centred on plot (x, y): what the works gate is asked of. */
    static CityLand.Piece pieceAt(Resource r, long x, long y, int sites) {
        Deposit d = new Deposit(r, 0, 0, x, y, sites, 1000.0);
        return new CityLand.Piece(0, d, d.type(), sites, d.amount());
    }

    /* =====================================================================
       5. TWO PORTS LINK
       ===================================================================== */

    static BuildingsTemplate tpl(City c, String name) {
        BuildingsTemplate t = c.g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new IllegalStateException("no template named " + name);
        return t;
    }

    /** A template as this city has it (a city's templates are its own; another city's object is never handed to its buildings). */
    static BuildingsTemplate own(City c, BuildingsTemplate t) { return c.g.getBuildingManager().getTemplate(t.getId()); }

    static int standing(City c, BuildingsTemplate t) { return c.g.getBuildingManager().getQuantity(t.getId()); }

    static int onSite(City c, BuildingsTemplate t) {
        BuildingsStacks s = c.g.getBuildingManager().getStack(own(c, t));
        return s == null ? 0 : s.getUnderConstruction();
    }

    /** Months played quietly until `done` holds, at most `most`; whether it does. */
    static boolean until(City c, java.util.function.BooleanSupplier done, int most) {
        for (int m = 0; m < most && !done.getAsBoolean(); m++) quietly(() -> c.g.simulateMonths(1));
        return done.getAsBoolean();
    }

    /** An order, quietly: `asked` is Landmasses.CHOSEN, RULE or a landmass's id. */
    static Game.BuildResult order(City c, BuildingsTemplate t, int n, boolean instant, int asked) {
        Game.BuildResult[] r = new Game.BuildResult[1];
        quietly(() -> r[0] = c.g.buildStack(own(c, t), n, instant, asked));
        return r[0];
    }

    /** An order of a terminal for a landmass by the card's path: the pick, then the order. */
    static Game.BuildResult orderOn(City c, BuildingsTemplate t, int n, boolean instant, int landmass) {
        c.g.choosePortLandmass(t, landmass);
        return order(c, t, n, instant, Landmasses.CHOSEN);
    }

    /** A city of forty houses that owns the island square (shore on it) and, when asked, a stretch of home's coast. */
    static City portCity(Fixtures f, String label, boolean homeShore) {
        City c = new City(label, f.key, 40);
        // ...and two depots, standing, so that a terminal ordered on site is built in a few months (the village has no builders but its yard).
        quietly(() -> c.g.buildStack(c.g.getBuildingManager().getTemplateByName("Construction Depot"), 2, true));
        c.buy(f.withShore);
        if (homeShore) c.buy(f.homeSalt);
        return c;
    }

    static final com.google.gson.Gson GSON = new com.google.gson.Gson();

    /** The saved placements as JSON: what the save writes under "landmasses" (null for none). */
    static String stateOf(City c) { return GSON.toJson(c.lm.landmassState()); }

    /** The third landmass for the closure's third leg: the first cell of a landmass in the tile west of Goeree, and a square about it with dry ground and shore. */
    static Square thirdLandmass(World w, EarthData d) {
        for (int id : new int[] { 10297, 12025 }) {
            double[] at = null;
            for (double lat = 51.0; lat < 52.0 && at == null; lat += 1.0 / 240) {
                for (double lon = 3.0; lon < 4.0; lon += 1.0 / 240) {
                    if (d.landmassAt(lat + 1.0 / 480, lon + 1.0 / 480) == id) { at = new double[] { lat + 1.0 / 480, lon + 1.0 / 480 }; break; }
                }
            }
            if (at == null) continue;
            long[] p = plotOf(w, at[0], at[1]);
            Square s = squareWith(w, id, p[0], p[1], 8, q -> q.onId() >= 30 && q.shore() >= 6);
            if (s != null) return s;
        }
        return null;
    }

    static void twoPortsLink(Fixtures f, EarthData data) {
        out.println("\n--- 5. two ports link: a terminal for the island takes the island's room, links nothing alone or on site, and with home's standing links it ---");
        if (!f.ok) { check("fixtures were found", false); return; }
        City c = portCity(f, "landmass-ports", false);
        Game g = c.g;
        LandManager lm = c.lm;
        Landmasses lms = c.books();
        BuildingsTemplate gc = tpl(c, "General Cargo Terminal");
        int home = lms.home(), general = Ports.Cargo.GENERAL.ordinal(), id = gc.getId();
        double land = gc.getLandSqFt(), room = lms.portRoomSqFt(648);
        report("premise: the city owns the island square - shore on it, none at home - with room on it for several terminals, and nothing is placed or linked",
                Ports.isPort(gc) && lms.shorePlots(648) > 0 && lms.shorePlots(home) == 0 && room >= 4 * land && lms.anyUnlinked() && !lms.linked(648) && !lms.hasPlacements(),
                String.format("room %,.0f sq ft for %,.0f a terminal; %d shore plots", room, land, lms.shorePlots(648)));
        check("by the rule alone the order is NO_COAST: home owns no shore, and the rule never takes an island no port links",
                order(c, gc, 1, true, Landmasses.RULE) == Game.BuildResult.NO_COAST && !g.hasCoastFor(gc, 1, Landmasses.RULE) && !g.hasCoastFor(gc, 1)
                        && lms.portLandmass(id, Landmasses.RULE) == Landmasses.NO_LANDMASS);
        List<Landmasses.Row> offered = g.portLandmassChoices(gc);
        check("the card offers the landmasses the city owns shore on: the island alone (home owns none); nothing for a building that is no terminal",
                offered.size() == 1 && offered.get(0).id() == 648 && g.portLandmassChoices(tpl(c, "House")).isEmpty());
        g.choosePortLandmass(gc, 648);
        check("with the island chosen the coast test passes - by the choice, not by the rule",
                g.hasCoastFor(gc, 1) && g.hasCoastFor(gc, 1, Landmasses.CHOSEN) && !g.hasCoastFor(gc, 1, Landmasses.RULE) && lms.chosen(id) == 648
                        && lms.portLandmass(id, Landmasses.CHOSEN) == 648 && lms.portLandmass(id, Landmasses.RULE) == Landmasses.NO_LANDMASS);
        double alloc0 = lm.getAllocatedSqFt(), linkedAlloc0 = lm.getLinkedAllocatedSqFt(), avail0 = lm.getAvailableSqFt(), unlinkedFree0 = lm.getUnlinkedFreeSqFt();
        Game.BuildQuote quote = g.quoteBuild(gc, 1);
        check("the quote's free ground is the island's room, not the linked pool's", near(quote.landFree, room) && !near(quote.landFree, lm.freeFor(gc.getSector(), Game.groundUse(gc))));
        check("the order is placed (SUCCESS) with the card's pick, which it spends", order(c, gc, 1, false, Landmasses.CHOSEN) == Game.BuildResult.SUCCESS && lms.chosen(id) == Landmasses.NO_LANDMASS);
        report("it takes the island's room: the room fell by exactly one terminal's land and the allocated ground rose by it, but the ground the linked buildings stand on, the linked free ground"
                        + " and the free ground the investors see did not move; the island is still unlinked and holds the terminal's ground among its unlinked sums",
                near(lms.portRoomSqFt(648), room - land) && near(lm.getAllocatedSqFt(), alloc0 + land) && near(lm.getLinkedAllocatedSqFt(), linkedAlloc0) && near(lm.getAvailableSqFt(), avail0)
                        && !lms.linked(648) && near(lms.unlinkedPortsSqFt(), land) && near(lm.getUnlinkedFreeSqFt(), unlinkedFree0 - land),
                String.format("room %,.0f -> %,.0f; allocated %,.0f -> %,.0f; linked free %,.1f", room, lms.portRoomSqFt(648), alloc0, lm.getAllocatedSqFt(), lm.getAvailableSqFt()));
        check("it is on site, not standing, and a terminal on site links nothing: the island holds none of the city's standing terminals",
                standing(c, gc) == 0 && onSite(c, gc) == 1 && !lms.linked(648) && lms.portsOn(648)[general] == 0 && lms.hasPlacements());
        boolean built = until(c, () -> standing(c, gc) >= 1, 80);
        report("the terminal stands after some months", built, "month " + g.getMonth());
        check("standing alone on the island it links nothing: one port makes no sea route, and it is counted on the island", built && !lms.linked(648) && lms.anyUnlinked()
                && lms.portsOn(648)[general] == 1 && lms.portsOn(home)[general] == 0 && near(lms.unlinkedPortsSqFt(), land));
        // Home's coast, and a terminal for it on site.
        int version = lms.version();
        c.buy(f.homeSalt);
        report("premise: the city now owns shore at home", lms.shorePlots(home) > 0, lms.shorePlots(home) + " shore plots");
        check("by the rule the order is home's now: the coast test passes without a pick", g.hasCoastFor(gc, 1, Landmasses.RULE) && lms.portLandmass(id, Landmasses.RULE) == home);
        check("a terminal for home is placed (SUCCESS) and is on site: the island is not linked while it is", order(c, gc, 1, false, Landmasses.RULE) == Game.BuildResult.SUCCESS
                && onSite(c, gc) == 1 && standing(c, gc) == 1 && !lms.linked(648) && lms.portsOn(home)[general] == 0);
        boolean early = false;
        int months = 0;
        while (standing(c, gc) < 2 && months < 80) {
            quietly(() -> g.simulateMonths(1));
            months++;
            early |= lms.linked(648) && standing(c, gc) < 2;
        }
        check("the island stayed unlinked every month the home terminal was on site", !early && standing(c, gc) == 2);
        report("the month it stood the island was linked: the linked set moved once, nothing is unlinked, each landmass holds its terminal",
                lms.linked(648) && !lms.anyUnlinked() && lms.version() == version + 1 && lms.portsOn(home)[general] == 1 && lms.portsOn(648)[general] == 1 && lm.getUnlinkedFreeSqFt() == 0
                        && lm.getLinkedOwnedSqFt() == lm.getOwnedSqFt(),
                "month " + g.getMonth() + ", version " + lms.version());

        // The arithmetic of the link, exactly: both terminals built at once, so that nothing else moves between the two readings.
        City d = portCity(f, "landmass-ports-exact", true);
        Landmasses ld = d.books();
        LandManager lmd = d.lm;
        Landmasses.Row isl = ld.rows().get(1);
        report("premise: the island's ground is all flat or foothill (no steep ground to sit idle) and home owns shore", isl.id() == 648 && isl.steepPlots() == 0 && isl.verySteepPlots() == 0 && ld.shorePlots(ld.home()) > 0,
                isl.dryPlots() + " dry plots, " + isl.steepPlots() + " steep");
        check("a terminal built at once for the island stands and links nothing", orderOn(d, gc, 1, true, 648) == Game.BuildResult.SUCCESS && standing(d, gc) == 1 && !ld.linked(648)
                && ld.portsOn(648)[general] == 1);
        double availBefore = lmd.getAvailableSqFt(), islandFree = ld.unlinkedFreeSqFt(648), ownedBefore = lmd.getOwnedSqFt();
        int v = ld.version();
        check("one built at home at once links the island, the moment it stands (the same order)", order(d, gc, 1, true, Landmasses.RULE) == Game.BuildResult.SUCCESS && ld.linked(648)
                && ld.version() == v + 1 && !ld.anyUnlinked());
        report("the free ground rose by the island's unbuilt ground less the terminal built at home: the island's buildable ground less its own terminal, less the home one",
                near(lmd.getAvailableSqFt(), availBefore + islandFree - land) && near(islandFree, isl.buildableSqFt() - land) && near(lmd.getOwnedSqFt(), ownedBefore),
                String.format("%,.1f -> %,.1f; the island's free %,.1f", availBefore, lmd.getAvailableSqFt(), islandFree));

        // A shell never links.
        City s = portCity(f, "landmass-shell", true);
        Landmasses ls = s.books();
        check("the island's terminal is ordered and stopped the same month: its site is a shell on the island, nothing on site", orderOn(s, gc, 1, false, 648) == Game.BuildResult.SUCCESS
                && s.g.cancelSite(ConstructionControl.keyOf(gc), true));
        quietly(() -> s.g.simulateMonths(1));
        ConstructionControl.Shell shell = s.g.getBuildingManager().getControl().shellOf(id);
        check("...a stopped order is a shell of one, nothing is on site, nothing stands", shell != null && shell.buildings == 1 && onSite(s, gc) == 0 && standing(s, gc) == 0);
        check("a terminal at home then stands (built at once)", order(s, gc, 1, true, Landmasses.RULE) == Game.BuildResult.SUCCESS && standing(s, gc) == 1);
        Landmasses.State st = s.lm.landmassState();
        report("a shell never links (the island's stopped terminal is not a standing one): one terminal stands, it is home's, and the island is unlinked; the saved row names it a shell",
                !ls.linked(648) && ls.anyUnlinked() && ls.portsOn(648)[general] == 0 && ls.portsOn(ls.home())[general] == 1 && st != null && st.ports.size() == 1
                        && st.ports.get(0).shell.length == 1 && st.ports.get(0).shell[0] == 648 && st.ports.get(0).standing.length == 1,
                stateOf(s));
        check("the shell restarted is on site for the island again, oldest first, and the island is still unlinked", s.g.restartShell(id) && onSite(s, gc) == 1 && !ls.linked(648)
                && s.g.getBuildingManager().getControl().shellOf(id) == null);
        boolean standsAgain = until(s, () -> standing(s, gc) >= 2, 80);
        check("...and when it stands the island is linked", standsAgain && ls.linked(648) && ls.portsOn(648)[general] == 1);

        // The third landmass.
        Square third = thirdLandmass(c.w, data);
        if (third == null) {
            out.println("  no square of landmass 10297 or 12025 with dry ground and shore in the fixture: the third landmass was not run");
        } else {
            City t = portCity(f, "landmass-third", true);
            t.buy(third);
            Landmasses lt = t.books();
            report("premise: the city owns a third landmass, with shore on it; none is linked and no terminal stands", lt.rows().size() == 3 && lt.shorePlots(third.id()) > 0 && !lt.linked(third.id()) && !lt.linked(648),
                    "landmass " + third.id() + ": " + describe(lt.rows()));
            check("a terminal on the island and one at home link the island", orderOn(t, gc, 1, true, 648) == Game.BuildResult.SUCCESS && order(t, gc, 1, true, Landmasses.RULE) == Game.BuildResult.SUCCESS
                    && lt.linked(648) && !lt.linked(third.id()));
            check("...and one terminal on the third landmass links it (a linked landmass has a port standing): the closure is home, the island and the third",
                    orderOn(t, gc, 1, true, third.id()) == Game.BuildResult.SUCCESS && lt.linked(third.id()) && lt.linked(648) && !lt.anyUnlinked());
        }

        // The order that waits for money still lands on the island.
        City n = portCity(f, "landmass-funding", false);
        quietly(() -> n.g.setCashForTest(0));
        check("short of cash the order is NEEDS_FUNDING, and the card's pick waits for the money (it survives the funding page)", orderOn(n, gc, 1, false, 648) == Game.BuildResult.NEEDS_FUNDING
                && n.books().chosen(id) == 648 && !n.books().hasPlacements());
        quietly(() -> n.g.setCashForTest(Founding.WEALTHY_CASH));
        check("...funded, the same order lands on the island", order(n, gc, 1, false, Landmasses.CHOSEN) == Game.BuildResult.SUCCESS && near(n.books().portsGroundSqFt(648), land) && n.books().chosen(id) == Landmasses.NO_LANDMASS);

        // The gate is the island's room.
        City r = portCity(f, "landmass-room", false);
        Landmasses lr = r.books();
        BuildingsTemplate bulk = tpl(r, "Bulk Terminal"), box = tpl(r, "Container Terminal"), house = tpl(r, "House");
        double roomR = lr.portRoomSqFt(648);
        int past = (int) Math.floor(roomR / land) + 1;
        check("an order past the island's room is NO_LAND, and nothing is placed", orderOn(r, gc, past, true, 648) == Game.BuildResult.NO_LAND && !r.books().hasPlacements());
        int a = (int) Math.floor(roomR / bulk.getLandSqFt());
        report("premise: the island holds several bulk terminals, the linked pool not even two - so what is placed on the island below was admitted by the island's room, not the pool's",
                a >= 3 && r.lm.getAvailableSqFt() < 2 * bulk.getLandSqFt(), String.format("%d bulk terminals on %,.0f sq ft; the pool %,.0f", a, roomR, r.lm.getAvailableSqFt()));
        double rest = roomR - a * bulk.getLandSqFt();
        int b = (int) Math.floor(rest / box.getLandSqFt()) + 1;
        r.g.choosePortLandmass(bulk, 648);
        r.g.choosePortLandmass(box, 648);
        java.util.Map<BuildingsTemplate, Integer> run = new java.util.LinkedHashMap<>();
        run.put(bulk, a);
        run.put(box, b);
        report("premise: each order alone fits the island, together they do not",
                a * bulk.getLandSqFt() <= roomR && b * box.getLandSqFt() <= roomR && a * bulk.getLandSqFt() + b * box.getLandSqFt() > roomR,
                String.format("%d bulk (%,.0f) and %d container (%,.0f) on %,.0f", a, a * bulk.getLandSqFt(), b, b * box.getLandSqFt(), roomR));
        check("runAhead() counts both against the island's room: the first order passes (the pool could not hold it), the second is NO_LAND", r.g.buildRunAhead(run) == 1 && r.g.buildRunStop(run) == Game.BuildResult.NO_LAND);
        run.put(bulk, a - 1);
        run.put(box, 1);
        check("...a run that fits the island passes whole", r.g.buildRunAhead(run) == 2 && r.g.buildRunStop(run) == Game.BuildResult.SUCCESS);
        // The linked pool full, the island's room intact: a terminal for the island still goes, a house at home does not.
        quietly(() -> r.lm.setAllocatedSqFt(r.lm.getLinkedOwnedSqFt() + r.books().unlinkedPortsSqFt()));
        report("premise: the linked pool is full", r.lm.getAvailableSqFt() < house.getLandSqFt(), String.format("%,.1f sq ft free", r.lm.getAvailableSqFt()));
        check("a house at home is NO_LAND; a terminal for the island is placed (the island's room is the gate)", order(r, house, 1, true, Landmasses.RULE) == Game.BuildResult.NO_LAND
                && orderOn(r, gc, 1, true, 648) == Game.BuildResult.SUCCESS && near(r.books().portsGroundSqFt(648), land));
        // ...and the bulk terminals the island holds are placed whole, in one order.
        double roomNow = r.books().portRoomSqFt(648);
        int fits = (int) Math.floor(roomNow / bulk.getLandSqFt());
        quietly(() -> r.g.setCashForTest(10 * Founding.WEALTHY_CASH));
        Game.BuildResult bulkResult = fits >= 2 ? orderOn(r, bulk, fits, true, 648) : null;
        report("an order of every bulk terminal the island's room holds is placed (SUCCESS) though the linked pool is full: the island's room, and it alone, is asked",
                fits >= 2 && bulkResult == Game.BuildResult.SUCCESS && near(r.books().portsGroundSqFt(648), land + fits * bulk.getLandSqFt()) && r.books().portRoomSqFt(648) < bulk.getLandSqFt(),
                String.format("%d asked on %,.0f sq ft: %s; cash %,.0f, the order's cost %,.0f; room left %,.0f", fits, roomNow, bulkResult, r.g.getCash(), r.g.calculateTotalCost(bulk, fits) , r.books().portRoomSqFt(648)));
    }

    /* =====================================================================
       6. DEMOLITION
       ===================================================================== */

    static void demolition(Fixtures f) {
        out.println("\n--- 6. demolition: the port that links an island cannot be demolished; an island's port keeps its ground until its demolition is done ---");
        if (!f.ok) { check("fixtures were found", false); return; }
        City c = portCity(f, "landmass-demolish", true);
        Game g = c.g;
        LandManager lm = c.lm;
        Landmasses lms = c.books();
        BuildingsTemplate gc = tpl(c, "General Cargo Terminal");
        int id = gc.getId(), home = lms.home(), general = Ports.Cargo.GENERAL.ordinal();
        double land = gc.getLandSqFt();
        boolean pair = orderOn(c, gc, 1, true, 648) == Game.BuildResult.SUCCESS & order(c, gc, 1, true, Landmasses.RULE) == Game.BuildResult.SUCCESS;
        report("premise: a terminal stands on the island and one at home, and they link it", pair && standing(c, gc) == 2 && lms.linked(648) && lms.portsOn(648)[general] == 1 && lms.portsOn(home)[general] == 1,
                stateOf(c));
        check("neither may be demolished: demolishable() leaves both out (the island's would unlink it, and so would home's)", g.demolishable(gc) == 0 && lms.freeToGo(id) == 0);
        boolean refused = !g.demolish(gc, 1);
        check("demolish() is refused with the words \"it is the port that links Island 2\", and puts nothing on site", refused && "it is the port that links Island 2".equals(g.getLastHandRefusal())
                && g.getBuildingManager().getControl().demolitions().isEmpty() && standing(c, gc) == 2);
        // A spare at home, a second on the island: the newest two may go.
        check("a spare at home and a second on the island are built at once", order(c, gc, 1, true, Landmasses.RULE) == Game.BuildResult.SUCCESS && orderOn(c, gc, 1, true, 648) == Game.BuildResult.SUCCESS
                && standing(c, gc) == 4 && lms.portsOn(648)[general] == 2 && lms.portsOn(home)[general] == 2);
        check("the two newest may go, the older pair may not: demolishable() is 2, and asking for 3 is refused", g.demolishable(gc) == 2 && !g.demolish(gc, 3));
        double room0 = lms.portRoomSqFt(648);
        boolean ok = g.demolish(gc, 1);
        Landmasses.State st = lm.landmassState();
        ConstructionControl.Demolition d = g.getBuildingManager().getControl().demolitions().isEmpty() ? null : g.getBuildingManager().getControl().demolitions().get(0);
        report("demolishing one takes the newest unit whose loss unlinks nothing - the island's second - and keeps it among the units leaving, by the demolition's id",
                ok && d != null && st != null && st.leaving.size() == 1 && st.leaving.get(0).demolition == d.id && st.leaving.get(0).template == id
                        && st.leaving.get(0).landmasses.length == 1 && st.leaving.get(0).landmasses[0] == 648 && st.ports.get(0).standing.length == 3
                        && lms.portsOn(648)[general] == 1 && lms.portsOn(home)[general] == 2,
                stateOf(c));
        check("...it holds its ground on the island while it is demolished: the island's room is as it was, the island still linked", near(lms.portRoomSqFt(648), room0) && lms.linked(648) && g.demolishable(gc) == 1);
        // Across a save: the leaving is saved by the demolition's id.
        c.g.saveGame(1, "demolition on site");
        Game back = new Game(c.files);
        quietly(() -> back.loadGameSave(1));
        report("saved and read back with the demolition on site, the same units are leaving, by the same id, and the island's room is the same",
                back.getLoadFailure() == null && stateOf(c).equals(GSON.toJson(back.getLandManager().landmassState())) && near(back.getLandManager().getLandmasses().portRoomSqFt(648), room0)
                        && back.getLandManager().getLandmasses().linked(648),
                back.getLoadFailure() == null ? stateOf(c) : back.getLoadFailure());
        boolean done = until(c, () -> g.getBuildingManager().getControl().demolitions().isEmpty(), 120);
        check("the demolition finishes: the ground is released and the island's room returns by exactly one terminal's land; nothing is leaving", done
                && near(lms.portRoomSqFt(648), room0 + land) && lm.landmassState() != null && lm.landmassState().leaving.isEmpty() && lms.portsOn(648)[general] == 1);
        // A lone terminal on an island no port links: demolished, it keeps the island's unlinked ground until the demolition is done.
        City u = portCity(f, "landmass-demolish-lone", false);
        Landmasses lu = u.books();
        BuildingsTemplate gcU = tpl(u, "General Cargo Terminal");
        report("premise: a lone terminal stands on the island, which it links nothing", orderOn(u, gcU, 1, true, 648) == Game.BuildResult.SUCCESS && !lu.linked(648) && near(lu.unlinkedPortsSqFt(), land), stateOf(u));
        double roomU = lu.portRoomSqFt(648), linkedAlloc = u.lm.getLinkedAllocatedSqFt(), alloc = u.lm.getAllocatedSqFt(), avail = u.lm.getAvailableSqFt();
        check("it may be demolished (it links nothing)", u.g.demolishable(gcU) == 1 && u.g.demolish(gcU, 1));
        check("...the ground it holds is the island's still while the demolition is on site: the unlinked sums, the linked allocated ground and the room are as they were",
                near(lu.unlinkedPortsSqFt(), land) && near(u.lm.getLinkedAllocatedSqFt(), linkedAlloc) && near(lu.portRoomSqFt(648), roomU) && near(u.lm.getAllocatedSqFt(), alloc));
        boolean doneU = until(u, () -> u.g.getBuildingManager().getControl().demolitions().isEmpty(), 120);
        report("...when it is done the terminal's ground is released from the island: the unlinked terminals hold none, the built ground the linked view takes off is none, the room is whole again and"
                        + " the whole island is unbuilt (the investors built on at home meanwhile, so the allocated ground itself is not the terminal's to read)",
                doneU && lu.unlinkedPortsSqFt() == 0 && near(u.lm.getAllocatedSqFt(), u.lm.getLinkedAllocatedSqFt()) && near(lu.portRoomSqFt(648), roomU + land)
                        && near(u.lm.getUnlinkedFreeSqFt(), lu.rows().get(1).dryPlots() * SQ_FT_PER_PLOT),
                String.format("allocated %,.0f -> %,.0f", alloc, u.lm.getAllocatedSqFt()));
        // A shell demolished likewise.
        City h = portCity(f, "landmass-demolish-shell", false);
        Landmasses lh = h.books();
        BuildingsTemplate gcH = tpl(h, "General Cargo Terminal");
        check("an island terminal ordered and stopped is a shell on the island, holding its ground", orderOn(h, gcH, 1, false, 648) == Game.BuildResult.SUCCESS && h.g.cancelSite(ConstructionControl.keyOf(gcH), true));
        quietly(() -> h.g.simulateMonths(1));
        double roomH = lh.portRoomSqFt(648);
        check("...the shell's ground is the island's (the room is less than whole)", h.g.getBuildingManager().getControl().shellOf(id) != null && roomH < lh.rows().get(1).buildableSqFt() - land / 2);
        boolean shellDown = h.g.demolishShell(id);
        Landmasses.State sh = h.lm.landmassState();
        ConstructionControl.Demolition dh = h.g.getBuildingManager().getControl().demolitions().isEmpty() ? null : h.g.getBuildingManager().getControl().demolitions().get(0);
        report("demolishing the shell keeps its units leaving by the demolition's id, holding the island's ground until it is done", shellDown && dh != null && sh != null && sh.leaving.size() == 1
                && sh.leaving.get(0).demolition == dh.id && sh.leaving.get(0).landmasses.length == 1 && sh.leaving.get(0).landmasses[0] == 648 && near(lh.portRoomSqFt(648), roomH)
                && h.g.getBuildingManager().getControl().shellOf(id) == null, stateOf(h));
        boolean doneH = until(h, () -> h.g.getBuildingManager().getControl().demolitions().isEmpty(), 120);
        check("...and when it is done the room is whole again and nothing is placed", doneH && near(lh.portRoomSqFt(648), roomH + land) && !lh.hasPlacements());
    }

    /* =====================================================================
       7. THE CHOICE
       ===================================================================== */

    /** What Landmasses reads of the buildings, for a unit test of the rule: one terminal template and fixed counts. */
    static final class StubStock implements Landmasses.Stock {
        int standing, onSite, shell;
        @Override public int[] portTemplates() { return new int[] { 97 }; }
        @Override public boolean known(int template) { return template == 97; }
        @Override public int standing(int template) { return standing; }
        @Override public int onSite(int template) { return onSite; }
        @Override public int shell(int template) { return shell; }
        @Override public List<int[]> demolitions(int template) { return List.of(); }
        @Override public double landSqFt(int template) { return 107_639; }
        @Override public int cargo(int template) { return Ports.Cargo.GENERAL.ordinal(); }
    }

    static void theChoice(Fixtures f) {
        out.println("\n--- 7. the choice: the card's pick is one order's, the rule never takes an island no port links ---");
        if (!f.ok) { check("fixtures were found", false); return; }
        City c = portCity(f, "landmass-choice", true);
        Game g = c.g;
        Landmasses lms = c.books();
        BuildingsTemplate gc = tpl(c, "General Cargo Terminal");
        int id = gc.getId(), home = lms.home(), general = Ports.Cargo.GENERAL.ordinal();
        List<Landmasses.Row> offered = g.portLandmassChoices(gc);
        check("the card offers the landmasses the city owns shore on, home first: home, then the island", offered.size() == 2 && offered.get(0).home() && offered.get(0).id() == home && offered.get(1).id() == 648);
        check("by the rule the terminal is home's - home owns shore - and a card's pick of the island is read by CHOSEN alone",
                lms.portLandmass(id, Landmasses.RULE) == home && lms.portLandmass(id, Landmasses.CHOSEN) == home && g.hasCoastFor(gc, 1, Landmasses.RULE));
        g.choosePortLandmass(gc, 648);
        check("a pick is read: CHOSEN is the island, RULE is still home", lms.portLandmass(id, Landmasses.CHOSEN) == 648 && lms.portLandmass(id, Landmasses.RULE) == home && lms.chosen(id) == 648);
        check("an order by the rule (the automatic builder's, the advice's) lands on home though the card picked the island, and the pick is spent",
                order(c, gc, 1, true, Landmasses.RULE) == Game.BuildResult.SUCCESS && lms.portsOn(home)[general] == 1 && lms.portsOn(648)[general] == 0 && lms.chosen(id) == Landmasses.NO_LANDMASS);
        g.choosePortLandmass(gc, 648);
        quietly(() -> g.simulateMonths(1));
        check("a pick not used is forgotten at the month's settle", lms.chosen(id) == Landmasses.NO_LANDMASS && lms.portLandmass(id, Landmasses.CHOSEN) == home);
        g.choosePortLandmass(gc, 12_345);
        check("a pick of a landmass the city owns no shore on is passed over: the rule answers", lms.portLandmass(id, Landmasses.CHOSEN) == home && order(c, gc, 1, true, Landmasses.CHOSEN) == Game.BuildResult.SUCCESS
                && lms.portsOn(home)[general] == 2);
        check("a pick by an order for the island lands there", orderOn(c, gc, 1, true, 648) == Game.BuildResult.SUCCESS && lms.portsOn(648)[general] == 1 && lms.linked(648));
        check("the rule's answer for a landmass named: an id with shore is itself, an id without is none", lms.portLandmass(id, 648) == 648 && lms.portLandmass(id, 12_345) == Landmasses.NO_LANDMASS);

        // The city that owns the island and no coast: the rule never takes the island.
        City i = portCity(f, "landmass-rule", false);
        check("with the island's shore owned and none at home, the rule is none, however it is asked, until a port links the island",
                i.books().portLandmass(id, Landmasses.RULE) == Landmasses.NO_LANDMASS && order(i, gc, 1, true, Landmasses.RULE) == Game.BuildResult.NO_COAST);
        // The rule's second leg, on the books of that city and a stubbed pair of terminals: home owns no shore, a linked island does.
        Landmasses unit = new Landmasses();
        unit.recount(i.w, i.g.getCityLand());
        StubStock stock = new StubStock();
        unit.setStock(stock);
        Landmasses.State pair = new Landmasses.State();
        Landmasses.PortRow row = new Landmasses.PortRow();
        row.template = 97;
        row.standing = new int[] { unit.home(), 648 };
        row.onSite = new int[0];
        row.shell = new int[0];
        pair.ports.add(row);
        stock.standing = 2;
        unit.restore(pair);
        unit.reconcile();
        check("the rule's second leg: home owns no shore but the island, linked by the pair, does - the answer is the linked island with shore", unit.linked(648) && unit.shorePlots(unit.home()) == 0
                && unit.portLandmass(97, Landmasses.RULE) == 648 && unit.linkedShore());
        stock.standing = 1;
        unit.restore(null);
        unit.reconcile();
        check("...and with one terminal at home the island is unlinked, and the answer is none", !unit.linked(648) && unit.portLandmass(97, Landmasses.RULE) == Landmasses.NO_LANDMASS && !unit.linkedShore());

        // An island with no shore: a pick of it is passed over, and the rule has nothing.
        City n = new City("landmass-noshore", f.key, 40);
        n.buy(f.withoutShore);
        check("the island with no salt in the square owns no shore: a terminal chosen for it is NO_COAST (the pick is passed over, the rule has none)",
                n.books().shorePlots(648) == 0 && n.g.portLandmassChoices(tpl(n, "General Cargo Terminal")).isEmpty() && orderOn(n, tpl(n, "General Cargo Terminal"), 1, true, 648) == Game.BuildResult.NO_COAST
                        && !n.g.hasCoastFor(tpl(n, "General Cargo Terminal"), 1));
        // Desalination: shore on a linked landmass.
        BuildingsTemplate desal = null;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (t.isSeaWater()) { desal = t; break; }
        BuildingsTemplate plant = desal;
        if (plant == null) {
            out.println("  no desalination plant in the catalogue: the plant's coast was not run");
        } else {
            check("a desalination plant needs shore on a LINKED landmass: the island's shore alone (unlinked) is NO_COAST, home's shore or a linked island's passes",
                    !i.g.hasCoastFor(plant, 1) && c.g.hasCoastFor(plant, 1) && !n.g.hasCoastFor(plant, 1));
        }

        // The quote is asked for the landmass the order is for (ET5, the review of slice B): a pick standing steers the card's quote (quoteBuild() reads CHOSEN) and not an order by the rule, which the automatic builder's quotes and its order are.
        City q = portCity(f, "landmass-quote", true);
        BuildingsTemplate qt = tpl(q, "General Cargo Terminal");
        q.g.choosePortLandmass(qt, 648);
        Game.BuildQuote byCard = q.g.quoteBuild(qt, 1), byRule = q.g.quoteBuildOn(qt, 1, Landmasses.RULE), onIsland = q.g.quoteBuildOn(qt, 1, 648);
        report("premise: with the island picked, the card's quote stands on the island's room and the rule's on home's free ground, and the two rooms are not one figure",
                near(byCard.landFree, q.books().portRoomSqFt(648)) && near(byRule.landFree, q.g.freeGroundFor(qt, Landmasses.RULE)) && !near(byCard.landFree, byRule.landFree),
                String.format("card's %,.0f sq ft, the rule's %,.0f", byCard.landFree, byRule.landFree));
        check("quoteBuildOn() asks for what the order is for: the island's is the card's quote with the island picked, and the rule's is home's whatever is picked", near(onIsland.landFree, byCard.landFree)
                && near(byRule.landFree, q.g.freeGroundFor(qt, Landmasses.RULE)) && near(q.g.quoteBuildOn(qt, 1, Landmasses.CHOSEN).landFree, byCard.landFree));
        double cashBefore = q.g.getCash();
        Game.BuildResult byTheRule = order(q, qt, 1, false, Landmasses.RULE);
        double charged = cashBefore - q.g.getCash();
        report("an order by the rule with the pick standing is placed and is charged its own quote, the rule's", byTheRule == Game.BuildResult.SUCCESS && Math.abs(charged - byRule.total) < 0.01,
                String.format("%s; charged %,.2f, the rule's quote %,.2f, the card's %,.2f", byTheRule, charged, byRule.total, byCard.total));
        report("...and it takes home's ground and nothing of the island's (no terminal is placed off home), and the pick is spent", q.books().unlinkedPortsSqFt() == 0 && !q.books().hasPlacements() && !q.books().linked(648) && q.books().chosen(qt.getId()) == Landmasses.NO_LANDMASS,
                String.format("unlinked terminals' ground %,.0f, placements %b, island linked %b, pick %d", q.books().unlinkedPortsSqFt(), q.books().hasPlacements(), q.books().linked(648), q.books().chosen(qt.getId())));
    }

    /* =====================================================================
       9. THE SAVE
       ===================================================================== */

    static void theSave(Fixtures f) throws Exception {
        out.println("\n--- 9. the save: the placements under one key, absent for a city on one landmass; read back, resaved, played on; a save without the key reads every terminal at home ---");
        if (!f.ok) { check("fixtures were found", false); return; }
        City c = portCity(f, "landmass-save", true);
        Game g = c.g;
        Landmasses lms = c.books();
        BuildingsTemplate gc = tpl(c, "General Cargo Terminal");
        int id = gc.getId(), home = lms.home();
        boolean built = orderOn(c, gc, 1, true, 648) == Game.BuildResult.SUCCESS & order(c, gc, 2, true, Landmasses.RULE) == Game.BuildResult.SUCCESS & orderOn(c, gc, 1, true, 648) == Game.BuildResult.SUCCESS;
        built &= orderOn(c, gc, 1, false, 648) == Game.BuildResult.SUCCESS && g.cancelSite(ConstructionControl.keyOf(gc), true);
        // ...two months, not one: a game saved in its first month holds no spending figures for the shops that a load re-derives (the plain Rotterdam city shows the same after one month, with no island in it), which would be the only thing a resave moves.
        quietly(() -> g.simulateMonths(2));
        built &= g.demolish(gc, 1);
        Landmasses.State st = c.lm.landmassState();
        report("fixture: standing [island, home, home, island], a shell of the island's, and the newest island terminal being demolished - the island linked",
                built && lms.linked(648) && st != null && st.ports.size() == 1 && st.ports.get(0).standing.length == 3 && st.ports.get(0).shell.length == 1 && st.leaving.size() == 1,
                stateOf(c));
        // The key is in the save, and only off home.
        assertSaved(c, 1);
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(c.files.saveFile(1))).getAsJsonObject();
        check("the save carries the key \"landmasses\": a row for the terminal's template and the demolition leaving", json.has("landmasses") && json.getAsJsonObject("landmasses").getAsJsonArray("ports").size() == 1
                && json.getAsJsonObject("landmasses").getAsJsonArray("leaving").size() == 1 && json.get("saveFormat").getAsInt() == GameVersion.SAVE_FORMAT);
        quietly(() -> f.b.g.saveGame(1, "single"));
        com.google.gson.JsonObject single = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(f.b.files.saveFile(1))).getAsJsonObject();
        check("a city on one landmass writes no such key (its save is the old bytes)", !single.has("landmasses") && f.b.lm.landmassState() == null);
        // Read back: the same placements, the same linked set, resaved identical but for the time.
        Game back = new Game(c.files);
        quietly(() -> back.loadGameSave(1));
        Landmasses lb = back.getLandManager().getLandmasses();
        report("read back: the same placements, the same landmasses linked, the same rooms and unlinked ground", back.getLoadFailure() == null && GSON.toJson(back.getLandManager().landmassState()).equals(stateOf(c))
                && lb.rows().equals(lms.rows()) && lb.linked(648) == lms.linked(648) && near(lb.portRoomSqFt(648), lms.portRoomSqFt(648)) && lb.version() >= 0 && lb.portsOn(648)[Ports.Cargo.GENERAL.ordinal()] == lms.portsOn(648)[Ports.Cargo.GENERAL.ordinal()],
                back.getLoadFailure() == null ? stateOf(c) : back.getLoadFailure());
        quietly(() -> back.saveGame(2, "again"));
        com.google.gson.JsonObject again = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(c.files.saveFile(2))).getAsJsonObject();
        java.util.List<String> moved = SaveFileCheck.jsonDiff(json, again, java.util.Set.of("savedAt", "slotName", "mapStamp"));
        java.util.List<String> paths = new java.util.ArrayList<>();
        if (!moved.isEmpty()) diffPaths("", json, again, paths, 12);
        report("...and written again it is the same bytes but for its time and name", moved.isEmpty(), moved + " " + paths);
        // Played on: the reload and the city played on are one (ReloadCheck's shape: every figure of the save to the bit).
        ReloadCheck.Point p = ReloadCheck.reload(c.files, g, "island city");
        report("a reload equals the city played on: loaded into a second game and both played a month and two, every figure of the save and of the history is the same",
                p.problem == null && p.save1 != null && p.save1.differing == 0 && p.save2.differing == 0 && p.history1.differing == 0 && p.history2.differing == 0,
                p.problem != null ? p.problem : p.save1.differing + " and " + p.save2.differing + " differ");
        // Without the key: all home.
        com.google.gson.JsonObject stripped = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(c.files.saveFile(1))).getAsJsonObject();
        stripped.remove("landmasses");
        java.nio.file.Files.writeString(c.files.saveFile(4), GSON.toJson(stripped));
        java.nio.file.Files.copy(c.files.historyFile(1), c.files.historyFile(4), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Game bare = new Game(c.files);
        quietly(() -> bare.loadGameSave(4));
        Landmasses lbare = bare.getLandManager().getLandmasses();
        int standingNow = bare.getBuildingManager().getQuantity(id) - bare.getBuildingManager().getControl().closingOf(id);
        check("a save without the key loads - it is not refused - and reads every terminal at home: none placed, none on the island, the island unlinked",
                bare.getLoadFailure() == null && lbare.portsOn(648)[Ports.Cargo.GENERAL.ordinal()] == 0 && !lbare.hasPlacements() && bare.getLandManager().landmassState() == null
                        && !lbare.linked(648) && lbare.portsOn(home)[Ports.Cargo.GENERAL.ordinal()] == standingNow);
        // A bad row: a template that is no terminal, and short arrays.
        com.google.gson.JsonObject bad = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(c.files.saveFile(1))).getAsJsonObject();
        com.google.gson.JsonObject section = bad.getAsJsonObject("landmasses");
        com.google.gson.JsonObject nonsense = new com.google.gson.JsonObject();
        nonsense.addProperty("template", tpl(c, "House").getId());
        com.google.gson.JsonArray one = new com.google.gson.JsonArray();
        one.add(648);
        nonsense.add("standing", one);
        section.getAsJsonArray("ports").add(nonsense);
        com.google.gson.JsonObject mine = section.getAsJsonArray("ports").get(0).getAsJsonObject();
        mine.add("standing", new com.google.gson.JsonArray());
        java.nio.file.Files.writeString(c.files.saveFile(5), GSON.toJson(bad));
        java.nio.file.Files.copy(c.files.historyFile(1), c.files.historyFile(5), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Game odd = new Game(c.files);
        quietly(() -> odd.loadGameSave(5));
        Landmasses.State oddState = odd.getLandManager().landmassState();
        check("a bad row is passed over, not refused: a template that is no terminal is skipped, a list shorter than the buildings' count is made up with home's", odd.getLoadFailure() == null
                && oddState != null && oddState.ports.size() == 1 && oddState.ports.get(0).template == id
                && oddState.ports.get(0).standing.length == odd.getBuildingManager().getQuantity(id) - odd.getBuildingManager().getControl().closingOf(id));
    }

    /** The paths at which two saves' JSON differ, down to the leaf (at most `most`): what jsonDiff() names only by its top key. */
    static void diffPaths(String path, com.google.gson.JsonElement a, com.google.gson.JsonElement b, java.util.List<String> out, int most) {
        if (out.size() >= most || java.util.Objects.equals(a, b)) return;
        if (a != null && b != null && a.isJsonObject() && b.isJsonObject()) {
            java.util.Set<String> keys = new java.util.TreeSet<>(a.getAsJsonObject().keySet());
            keys.addAll(b.getAsJsonObject().keySet());
            for (String k : keys) diffPaths(path + "." + k, a.getAsJsonObject().get(k), b.getAsJsonObject().get(k), out, most);
        } else if (a != null && b != null && a.isJsonArray() && b.isJsonArray() && a.getAsJsonArray().size() == b.getAsJsonArray().size()) {
            for (int i = 0; i < a.getAsJsonArray().size(); i++) diffPaths(path + "[" + i + "]", a.getAsJsonArray().get(i), b.getAsJsonArray().get(i), out, most);
        } else {
            out.add(path + " " + a + " -> " + b);
        }
    }

    static void assertSaved(City c, int slot) {
        GameFiles.Result r = quietlyResult(() -> c.g.saveGame(slot, "landmass"));
        check("fixture: the island city is saved", r.ok);
    }

    static GameFiles.Result quietlyResult(java.util.function.Supplier<GameFiles.Result> s) {
        PrintStream real = System.out;
        System.setOut(QUIET);
        try { return s.get(); } finally { System.setOut(real); }
    }

    /* =====================================================================
       12. GROWN ACROSS LANDMASSES
       ===================================================================== */

    /**
     * The model's books of the terminals and the ground, audited at one look: the linked free ground fits the linked owned ground less the linked
     * built ground; the ground the unlinked landmasses' terminals hold is what the saved placements say, times the land a terminal takes; the
     * saved placements are the buildings' own counts; a landmass is linked only if the rule makes it so. Null when all hold, else what does not.
     */
    static String audit(Game g) {
        LandManager lm = g.getLandManager();
        Landmasses lms = lm.getLandmasses();
        BuildingManager bm = g.getBuildingManager();
        StringBuilder bad = new StringBuilder();
        if (lm.getAvailableSqFt() > Math.max(0, lm.getLinkedOwnedSqFt() - lm.getLinkedAllocatedSqFt()) + FREE_SLACK) {
            bad.append(String.format("free %,.1f > linked owned %,.1f - linked allocated %,.1f; ", lm.getAvailableSqFt(), lm.getLinkedOwnedSqFt(), lm.getLinkedAllocatedSqFt()));
        }
        Landmasses.State st = lm.landmassState();
        double unlinkedPorts = 0;
        java.util.Set<Integer> unlinked = new java.util.HashSet<>(lms.unlinkedIds());
        if (st != null) {
            for (Landmasses.PortRow row : st.ports) {
                BuildingsTemplate t = bm.getTemplate(row.template);
                int counted = Math.max(0, bm.getQuantity(row.template) - bm.getControl().closingOf(row.template));
                BuildingsStacks stack = t == null ? null : bm.getStack(t);
                int site = stack == null ? 0 : stack.getUnderConstruction();
                ConstructionControl.Shell shell = bm.getControl().shellOf(row.template);
                if (row.standing.length != counted || row.onSite.length != site || row.shell.length != (shell == null ? 0 : shell.buildings)) {
                    bad.append(String.format("%s: placements %d/%d/%d, buildings %d/%d/%d; ", t == null ? "template " + row.template : t.getName(), row.standing.length, row.onSite.length,
                            row.shell.length, counted, site, shell == null ? 0 : shell.buildings));
                }
                for (int[] list : new int[][] { row.standing, row.onSite, row.shell }) {
                    for (int l : list) if (unlinked.contains(l) && t != null) unlinkedPorts += t.getLandSqFt();
                }
            }
            for (Landmasses.Leaving l : st.leaving) {
                BuildingsTemplate t = bm.getTemplate(l.template);
                for (int m : l.landmasses) if (unlinked.contains(m) && t != null) unlinkedPorts += t.getLandSqFt();
            }
        }
        if (Math.abs(lms.unlinkedPortsSqFt() - unlinkedPorts) > FREE_SLACK) bad.append(String.format("unlinked terminals' ground %,.1f, the placements say %,.1f; ", lms.unlinkedPortsSqFt(), unlinkedPorts));
        if (unlinkedPorts > lm.getAllocatedSqFt() + FREE_SLACK) bad.append("unlinked terminals hold more ground than is built; ");
        for (int u : unlinked) if (lms.unlinkedFreeSqFt(u) < 0) bad.append("an unlinked landmass's free ground is below nothing; ");
        // A landmass off home is linked only if a terminal stands on it and one at home.
        for (Landmasses.Row r : lms.rows()) {
            if (r.home() || !r.linked()) continue;
            int onSum = 0, atSum = 0;
            for (int v : lms.portsOn(r.id())) onSum += v;
            for (int v : lms.portsOn(lms.home())) atSum += v;
            if (onSum == 0 || atSum == 0) bad.append("landmass " + r.id() + " is linked without a terminal standing on it and one at home; ");
        }
        // No works stands on a field across the water. The sites usable are counted here piece by piece, from the plot each field is centred on (Landmasses.fieldLandmass(), the read the gate shares), not from the gate's own counters: getSites() must be them, and the works standing, on site or ordered, on each sited resource fit them.
        World world = World.of(lm.getCityLand().key());
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            long usable = 0;
            for (CityLand.Piece p : lm.getCityLand().typedPieces(r)) {
                if (p.field() == null || lms.linked(Landmasses.fieldLandmass(world, p.field().x(), p.field().y()))) usable += p.sites();
            }
            int usableNow = (int) Math.min(Integer.MAX_VALUE, usable);
            if (lm.getSites(r) != usableNow) bad.append(String.format("%s: getSites() says %d, the pieces on linked ground hold %d; ", r, lm.getSites(r), usableNow));
            if (g.committedOn(r) > usableNow) bad.append(String.format("%s works take %d sites, %d are usable on linked ground (%d owned); ", r, g.committedOn(r), usableNow, lm.getCityLand().totalSites(r)));
        }
        return bad.length() == 0 ? null : bad.toString();
    }

    /** The buildings standing, every template's. */
    static int buildingsOf(Game g) {
        int n = 0;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) n += g.getBuildingManager().getQuantity(t.getId());
        return n;
    }

    static void grown(Fixtures f) {
        out.println("\n--- 12. grown across landmasses: 24 months with the island owned and unlinked, the two terminals by the card's path, then 96 months, the books audited every month ---");
        if (!f.ok) { check("fixtures were found", false); return; }
        City c = portCity(f, "landmass-grown", true);
        Game g = c.g;
        Landmasses lms = c.books();
        BuildingsTemplate gc = tpl(c, "General Cargo Terminal");
        quietly(() -> g.getAutoBuilder().setOn(true, g.getDecisions()));
        int[] audits = { 0, 0 };
        String[] first = { null };
        Runnable look = () -> {
            audits[0]++;
            String s = audit(g);
            if (s != null) {
                audits[1]++;
                if (first[0] == null) first[0] = "month " + g.getMonth() + ": " + s;
            }
        };
        look.run();
        int unlinkedMonths = 0, month648 = -1;
        // ...what the 24 months have to move for "stays unlinked" to mean something: the city builds (ground allocated, buildings standing). The island holds no field of a sited resource in the fixture (the island city below owns one across the water).
        double allocated0 = c.lm.getAllocatedSqFt();
        int buildings0 = buildingsOf(g);
        for (int m = 0; m < 24; m++) {
            quietly(() -> g.simulateMonths(1));
            look.run();
            if (lms.anyUnlinked()) unlinkedMonths++;
        }
        report("premise: the 24 months moved the city - ground was allocated and buildings stand that did not",
                c.lm.getAllocatedSqFt() > allocated0 && buildingsOf(g) > buildings0,
                String.format("allocated %,.0f -> %,.0f sq ft; %d -> %d buildings; linked free %,.0f, the island's %,.0f sq ft", allocated0, c.lm.getAllocatedSqFt(), buildings0, buildingsOf(g), c.lm.getAvailableSqFt(), c.lm.getUnlinkedFreeSqFt()));
        boolean stillUnlinked = lms.anyUnlinked() && !lms.linked(648) && !lms.hasPlacements();
        double unlinkedFree = c.lm.getUnlinkedFreeSqFt();
        String clause = g.getBusinessInvestment().landReason(tpl(c, "House"));
        boolean island = orderOn(c, gc, 1, false, 648) == Game.BuildResult.SUCCESS;
        boolean atHome = order(c, gc, 1, false, Landmasses.RULE) == Game.BuildResult.SUCCESS;
        look.run();
        for (int m = 0; m < 96; m++) {
            quietly(() -> g.simulateMonths(1));
            look.run();
            if (month648 < 0 && lms.linked(648)) month648 = g.getMonth();
            if (lms.anyUnlinked()) unlinkedMonths++;
        }
        report("24 months with the island owned and no terminal: it stays unlinked, owned and counted, and no investor's refusal hides it", stillUnlinked && unlinkedFree > 0, clause);
        report("the two terminals by the card's path (the island's, then home's) are placed, and the island is linked once both stand", island && atHome && month648 > 0 && !lms.anyUnlinked(),
                "linked in month " + month648);
        report(audits[0] + " looks, every month of the 120: the books are consistent - linked free ground fits, the unlinked terminals' ground is the placements', the placements are the buildings' counts, no works stands on an unlinked field",
                audits[1] == 0 && audits[0] >= 120, first[0] == null ? unlinkedMonths + " months with ground unlinked" : first[0]);
        // ...and a city founded on the island itself, owning the mainland's field of iron across the water: 24 months with the automatic builder on, audited every
        // month. Here are sites no works may stand on, so the audit's count of the usable ones (getSites() against the pieces on linked ground, and the works against them) is asked of a field that matters.
        IslandCity ic = onTheIsland(f.key.data());
        if (ic == null) { check("fixture: a city on the island with a field across the water", false); return; }
        City d = new City("landmass-grown-island", ic.city.key, 40);
        Landmasses dl = d.books();
        LandParcel across = buyAround(d, ic.away.field().x(), ic.away.field().y());
        long sitesAcross = 0;
        for (Resource r : Resource.values()) if (r.inFields()) sitesAcross += d.lm.getCityLand().totalSites(r) - d.lm.getSites(r);
        quietly(() -> d.g.getAutoBuilder().setOn(true, d.g.getDecisions()));
        double dAllocated0 = d.lm.getAllocatedSqFt();
        int dBuildings0 = buildingsOf(d.g), dLooks = 0, dBad = 0;
        String dFirst = null;
        for (int m = 0; m < 24; m++) {
            quietly(() -> d.g.simulateMonths(1));
            String s = audit(d.g);
            dLooks++;
            if (s != null) { dBad++; if (dFirst == null) dFirst = "month " + d.g.getMonth() + ": " + s; }
        }
        report("premise: the island city owns the mainland's field across the water - sites no works may use - and its 24 months moved it: ground allocated, buildings standing that did not",
                across != null && across.getSites(ic.away.resource()) >= ic.away.field().sites() && sitesAcross >= ic.away.field().sites() && dl.anyUnlinked() && !dl.linked(ic.away.landmass())
                        && d.lm.getAllocatedSqFt() > dAllocated0 && buildingsOf(d.g) > dBuildings0,
                String.format("%s field of %d sites on landmass %d; %d sites across the water; allocated %,.0f -> %,.0f sq ft; %d -> %d buildings", ic.away.resource(), ic.away.field().sites(),
                        ic.away.landmass(), sitesAcross, dAllocated0, d.lm.getAllocatedSqFt(), dBuildings0, buildingsOf(d.g)));
        report(dLooks + " looks, every month of the 24: no works stands on a field across the water - getSites() is the pieces on linked ground, and the works standing fit them - and the rest of the books agree",
                dBad == 0 && dLooks == 24, dFirst == null ? "" : dFirst);
    }

    /* =====================================================================
       13. THE FULL FILE
       ===================================================================== */

    /** The Earth's data where it is named or lies at its place, or null - and the one line saying so. */
    static EarthData theEarth() {
        java.nio.file.Path p = EarthData.locate();
        if (p == null) {
            out.println("\nearth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": section 13 (the full file) was not run");
            return null;
        }
        EarthSite.Data game = EarthSite.gameData();
        if (!game.here() || !game.reader().identity().equals(EarthData.EARTH_2)) {
            out.println("\nthe game's Earth is not earth-2 (" + game.words() + "): section 13 (the full file) was not run");
            return null;
        }
        return game.reader();
    }

    /** The months the Rotterdam run plays. */
    static final int RUN_MONTHS = 1200;

    static void fullFile(EarthData file) {
        out.println("\n--- 13. the full file: the Rotterdam delta's islands are home's and Goeree is not; PEI is apart; Halifax and Dartmouth are one; Medellin is on 2; Rotterdam played " + RUN_MONTHS + " months ---");
        if (file == null) return;
        String id = EarthTerrain.use(file);
        World w = World.of(World.Key.earth(SEED, RLAT, RLON, id));
        long[] site = { w.foundingX(), w.foundingY() };
        int rotterdam = w.landmassAt(site[0], site[1]);
        String[] delta = { "Voorne-Putten", "Rozenburg", "IJsselmonde", "Hoeksche Waard" };
        double[][] at = { { 51.85, 4.20 }, { 51.905, 4.25 }, { 51.86, 4.55 }, { 51.78, 4.43 } };
        StringBuilder read = new StringBuilder();
        boolean same = rotterdam > 0;
        for (int i = 0; i < delta.length; i++) {
            long[] p = plotOf(w, at[i][0], at[i][1]);
            int l = w.landmassAt(p[0], p[1]);
            same &= l == rotterdam;
            read.append(delta[i]).append(' ').append(l).append("; ");
        }
        long[] goeree = plotOf(w, 51.77, 4.15);
        report("the Rotterdam delta's islands read Rotterdam's landmass in the model's own reader: Voorne-Putten, Rozenburg, IJsselmonde, the Hoeksche Waard", same, "Rotterdam " + rotterdam + "; " + read);
        report("...and Goeree-Overflakkee does not", w.landmassAt(goeree[0], goeree[1]) != rotterdam && w.landmassAt(goeree[0], goeree[1]) > 0, "Goeree " + w.landmassAt(goeree[0], goeree[1]));
        int pei = file.landmassAt(46.24, -63.13), mainland = file.landmassAt(45.0, -63.0), halifax = file.landmassAt(44.65, -63.58), dartmouth = file.landmassAt(44.67, -63.57), medellin = file.landmassAt(6.25, -75.57);
        report("Prince Edward Island is a landmass of its own (101), apart from the mainland across the strait", pei == 101 && mainland != 0 && pei != mainland, "PEI " + pei + ", the mainland " + mainland);
        report("Halifax and Dartmouth are one landmass; Medellin is on the Americas (2)", halifax != 0 && halifax == dartmouth && medellin == 2, "Halifax " + halifax + ", Dartmouth " + dartmouth + ", Medellin " + medellin);

        long started = System.currentTimeMillis();
        LongPlaytest.findings.clear();
        PrintStream was = LongPlaytest.out;
        LongPlaytest.out = QUIET;
        Game g = new Game(GameFiles.scratch("landmass-rotterdam"), LongPlaytest.earthFounding("rotterdam"));
        int[] looks = { 0, 0, 0, 0, 0 };
        String[] first = { null };
        try {
            quietly(() -> {
                g.run();
                g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
                g.setRolloverMode(LongPlaytest.ROLLOVER);
                g.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC);
                LongPlaytest.villageBuild(g, "House", 40);
                LongPlaytest.villageBuild(g, "Convenience Store", 3);
                LongPlaytest.villageBuild(g, "Mixed Farm", 2);
                LongPlaytest.run(g, 3);
                LongPlaytest.villageBuild(g, "House", 20);
                LongPlaytest.run(g, 4);
                LongPlaytest.villageBuild(g, "Convenience Store", 2);
                LongPlaytest.villageBuild(g, "Construction Depot", 1);
                LongPlaytest.run(g, 5);
                LongPlaytest.advise(g);
                LongPlaytest.run(g, 6);
                int stop = 0;
                while (g.getMonth() < RUN_MONTHS) {
                    stop++;
                    int skip = switch (stop % 6) { case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120; };
                    LongPlaytest.run(g, Math.min(skip, RUN_MONTHS - g.getMonth()));
                    look(g, looks, first);
                    if (g.getMonth() >= RUN_MONTHS) break;
                    for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                        if (LongPlaytest.advise(g) == null) break;
                        LongPlaytest.run(g, 1);
                    }
                    LongPlaytest.run(g, 2);
                    look(g, looks, first);
                }
            });
        } finally {
            LongPlaytest.out = was;
        }
        Landmasses lms = g.getLandManager().getLandmasses();
        check("the run played its months and found nothing: every month passed the playtest's audit, " + LongPlaytest.findings.keySet(), g.getMonth() >= RUN_MONTHS && LongPlaytest.findings.isEmpty());
        report(looks[0] + " looks, the books audited at each (section 12's audit): consistent", looks[0] > 0 && looks[1] == 0, first[0] == null ? "" : first[0]);
        check("the test player never builds a terminal off its home landmass (it never links an island)", g.getLandManager().landmassState() == null);
        out.printf("      %d months in %d s: %d looks owned ground off home, %d looks had an offer off home, %d looks had an ISLAND offer; the city ends owning %d landmass%s%n", g.getMonth(),
                (System.currentTimeMillis() - started) / 1000, looks[2], looks[3], looks[4], lms.rows().size(), lms.rows().size() == 1 ? "" : "es");
        out.println("      ITS LANDMASSES AT THE END: " + describe(lms.rows()));
    }

    private static void look(Game g, int[] looks, String[] first) {
        looks[0]++;
        String s = audit(g);
        if (s != null) {
            looks[1]++;
            if (first[0] == null) first[0] = "month " + g.getMonth() + ": " + s;
        }
        Landmasses lms = g.getLandManager().getLandmasses();
        if (!lms.single()) looks[2]++;
        boolean off = false, tagged = false;
        for (LandParcel p : g.getLandManager().getListing()) {
            off |= p.getUnlinkedDryKm2() > 0;
            tagged |= LandmassView.islandTagged(p);
        }
        if (off) looks[3]++;
        if (tagged) looks[4]++;
    }

    /** The five areas of an offer of this many square kilometres of dry ground. */
    static double[] dryArea(double km2) {
        double[] a = new double[CityLand.AREAS];
        a[CityLand.TOTAL] = km2;
        a[CityLand.DRY] = km2;
        return a;
    }
}
