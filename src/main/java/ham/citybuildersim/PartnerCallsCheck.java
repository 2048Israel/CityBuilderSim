package ham.citybuildersim;

import com.google.gson.Gson;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The ships and trains that go to their partners, as a model (batch WN7, 2026-11-01; docs/studies/spec-nations.md 9): PartnerTrade's month tonnes by
 * kind of cargo, CallPartners' draw of a nation for a call, the boats' exits (an offing per bearing) and the trains' land partners. The map
 * (ui/MapView) draws them in a later slice; nothing in the model's month reads any of it, so no trace moves.
 *
 * <p>WHY. The map drew every boat out of the offing the way its route went and every train in from the one gateway, whoever the cargo was
 * for; the month's trade by partner (WN3) says exactly who bought what. A call now carries the nation it is bound for, drawn by the tonnes
 * of its kind of cargo, and a boat leaves on that nation's bearing. A drawing that is wrong is a ship sailing the wrong way for good, so this
 * holds the draw to the tonnes and the bearing to the sea.
 *
 * <p>What this has to prove:
 * <ol>
 *   <li>PARTNERTRADE BY CARGO: the month's tonnes by kind of cargo add, per direction and nation, to the month's tonnes of the goods that
 *       have a cargo; a good with none is in the month's tonnes by nation and in none of these; the table is cleared at the month's turn;
 *       through State (and Gson) and back it is equal, and a save without it restores none.</li>
 *   <li>PICK() DRAWS BY TONNES: two nations 3 to 1 in dry bulk exports are drawn 3 to 1; a nation with none of that cargo is never drawn;
 *       the boxes draw on both ways; a kind with no tonnes falls back on the direction's tonnes of every kind; NONE, an empty month and a
 *       hash at the ends of its range answer as they should; a nation's bearing, reach by land and name.</li>
 *   <li>WITH NO PARTNERS NOTHING MOVES: with CallPartners.NONE every call, arrival, route and position, and a frame on the legs, equals what
 *       the schedule made without them (the routes with exits and without), and the same inputs twice make the same partners and exits;
 *       partners change no call's kind, route or arrival; the calls draw by tonnes.</li>
 *   <li>EXITS ON A MASK OF THE SEA: the compass in the plane; open water has all 72 exits open at ABYSS_M; a joined route has its lane's
 *       exits, found once, a straight lane has none, and a route extended onto kept ones has those it would whole; on a coast with an island
 *       beside the offing a bearing into the island is closed, every open exit's leg (and either side of it by SPREAD_DEG) keeps to the sea;
 *       a call whose partner's bearing is closed leaves on the nearest open one, one whose bearing is open on that; a boat rides the route
 *       to the offing unturned and heads on its exit within the spread, fading from the offing to the exit's end; and frame() finds the
 *       boats on an exit, those and no others in a rectangle only the exit crosses.</li>
 *   <li>TRAINS DRAW LAND PARTNERS ONLY: a far nation with tonnes of the kind never boards a train, the host always may, a boat may draw the
 *       far one; partners change no train's kind, route or arrival; a frame's trains carry them.</li>
 *   <li>THE COMPASS IS THE EARTH'S: from a spot a step along the plane's (sin b, -cos b) has the bearing b by EarthProj and
 *       TradeLanes.bearingDeg, within a degree, for b every 45 degrees; from a founded city's site too when the Earth file is named.</li>
 *   <li>AT TEN BILLION a frame's boats, on MapCheck's 20,000 lanes with exits and partners over 196 nations, take no more than
 *       BOAT_FRAME_MS on its three views.</li>
 * </ol>
 */
public class PartnerCallsCheck {

    static int fails = 0;
    static final PrintStream out = System.out;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, Timing.verdict(ok, label));
    }

    /** The compass, as the section's fixtures stand on it: the city at the equator on the prime meridian, nodes 15 degrees off it north, east, south and west (1,668 km: by land). */
    static final double[] NODE_LAT = { 0, 15, -15, 0, 0 }, NODE_LON = { 15, 0, 0, -15, 150 };

    /** The draws a share is measured over: fixed hashes, so a figure that passes once passes for ever. */
    static final int DRAWS = 20_000;

    /** How far a measured share may be from its probability, in standard deviations of a binomial of DRAWS draws: 5, which 20,000 hashes keep to under 0.016 at a share of three quarters. */
    static final double SIGMAS = 5;

    /** A model's length measured by another road may differ by what the model's own sampling lets by: a sample's spacing, a third of a cell, and a plot. */
    static final double SAMPLE_SLACK = SeaRoutes.CELL / 3.0 + 1;

    public static void main(String[] args) throws Exception {
        tonnesByCargo();
        pickByTonnes();
        Coast c = coast();
        nothingMovesWithoutPartners(c);
        exitsOnAMask(c);
        trainsByLand();
        compassOnTheEarth();
        frameAtTenBillion();
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       FIXTURES
       ===================================================================== */

    /** One good's month: its units (tonnes: one to the tonne) by nation, sold and bought. */
    record Row(Good good, double[] out, double[] in) { }

    /** The first good that ships in a kind of cargo, the next after it, or null. */
    static Good goodOf(Ports.Cargo c, int skip) {
        for (Good g : Good.values()) if (g.cargo() == c && skip-- == 0) return g;
        return null;
    }

    /** A good that ships in no kind of cargo (Good.cargo() null: check). */
    static Good noCargo() {
        for (Good g : Good.values()) if (g.cargo() == null) return g;
        return null;
    }

    static double sum(double[] a) {
        double s = 0;
        for (double v : a) s += v;
        return s;
    }

    /** n nations' figures, the first ones given. */
    static double[] nat(int n, double... first) {
        return Arrays.copyOf(first, n);
    }

    static void tally(PartnerTrade pt, int month, Good g, double[] out, double[] in, double perTonne) {
        double so = sum(out), si = sum(in);
        pt.tally(month, g, new WorldMarket.Allocation(out, in), so, so * 2, si, si * 2, perTonne);
    }

    /** A record of n nations with a month tallied (closed) from the rows. */
    static PartnerTrade trade(int n, int month, Row... rows) {
        PartnerTrade pt = new PartnerTrade(n);
        for (Row r : rows) tally(pt, month, r.good(), r.out(), r.in(), 1);
        pt.close(month);
        return pt;
    }

    static long hash(int i) {
        return World.mix(0xC0FFEEL + i * 0x9E3779B97F4A7C15L);
    }

    /** The share of DRAWS hashes each nation is drawn for (index 0: none). */
    static double[] shares(CallPartners p, Ports.Cargo c, boolean inbound, boolean byLand, int nations) {
        double[] n = new double[nations + 1];
        for (int i = 0; i < DRAWS; i++) n[p.pick(c, inbound, byLand, hash(i))]++;
        for (int j = 0; j < n.length; j++) n[j] /= DRAWS;
        return n;
    }

    /** How far a share of `draws` draws may be from probability p: SIGMAS standard deviations of a binomial. */
    static double tolerance(double p, int draws) {
        return Math.max(SIGMAS * Math.sqrt(p * (1 - p) / draws), 1e-3);
    }

    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    /* =====================================================================
       1. PARTNERTRADE BY CARGO
       ===================================================================== */

    static void tonnesByCargo() {
        out.println("--- 1. PartnerTrade's month tonnes by kind of cargo ---");
        int n = 6, m = 300, kinds = Ports.Cargo.values().length;
        List<Good> goods = new ArrayList<>();
        for (Ports.Cargo c : Ports.Cargo.values()) { goods.add(goodOf(c, 0)); goods.add(goodOf(c, 1)); }
        goods.add(noCargo());
        goods.add(noCargo());
        boolean every = goods.stream().allMatch(Objects::nonNull);
        check("fixture: two goods of every kind of cargo, and a good that ships in none", every && noCargo() != null);
        PartnerTrade pt = new PartnerTrade(n);
        double[][] wantOut = new double[kinds][n], wantIn = new double[kinds][n];
        double[] noOut = new double[n], noIn = new double[n];
        for (Good g : goods) {
            double[] to = new double[n], from = new double[n];
            for (int i = 0; i < n; i++) {
                long h = World.mix(g.ordinal() * 31L + i);
                to[i] = h % 5 == 0 ? 0 : 10 + (h >>> 40) % 900;
                from[i] = h % 7 == 0 ? 0 : 5 + (h >>> 20) % 700;
            }
            double perTonne = 1 + g.ordinal() % 3;
            tally(pt, m, g, to, from, perTonne);
            Ports.Cargo c = g.cargo();
            for (int i = 0; i < n; i++) {
                (c == null ? noOut : wantOut[c.ordinal()])[i] += to[i] / perTonne;
                (c == null ? noIn : wantIn[c.ordinal()])[i] += from[i] / perTonne;
            }
        }
        pt.close(m);
        PartnerTrade.Snapshot s = pt.snapshot();
        boolean equal = true, filled = true;
        for (Ports.Cargo c : Ports.Cargo.values()) {
            for (int i = 0; i < n; i++) {
                equal &= near(s.cargoTonnes(c, true)[i], wantOut[c.ordinal()][i]) && near(s.cargoTonnes(c, false)[i], wantIn[c.ordinal()][i]);
            }
            filled &= sum(s.cargoTonnes(c, true)) > 0 && sum(s.cargoTonnes(c, false)) > 0;
        }
        check("the month's tonnes of a kind of cargo are its goods' tonnes by nation, each good's units over its units to the tonne, out and in", equal && filled);
        boolean adds = true;
        for (int i = 0; i < n; i++) {
            double byKindOut = 0, byKindIn = 0;
            for (Ports.Cargo c : Ports.Cargo.values()) { byKindOut += s.cargoTonnes(c, true)[i]; byKindIn += s.cargoTonnes(c, false)[i]; }
            adds &= near(byKindOut + noOut[i], s.exportTonnes()[i]) && near(byKindIn + noIn[i], s.importTonnes()[i]);
        }
        check("...the kinds add, per direction and nation, to the month's tonnes of the goods that have a cargo: the good with none is in the month's tonnes and in no kind",
                adds && sum(noOut) > 0 && sum(noIn) > 0 && sum(s.exportTonnes()) > sum(wantOut[0]) + sum(wantOut[1]) + sum(wantOut[2]) + sum(wantOut[3]));
        double[][] keep = new double[kinds][];
        for (Ports.Cargo c : Ports.Cargo.values()) keep[c.ordinal()] = s.cargoTonnes(c, true).clone();
        // The state: through a copy of the record, and through Gson as a save is.
        PartnerTrade.State st = pt.toState();
        PartnerTrade back = new PartnerTrade(n);
        boolean restored = back.restore(st);
        Gson gson = new Gson();
        PartnerTrade viaGson = new PartnerTrade(n);
        boolean restoredGson = viaGson.restore(gson.fromJson(gson.toJson(st), PartnerTrade.State.class));
        boolean same = restored && restoredGson;
        for (Ports.Cargo c : Ports.Cargo.values()) {
            for (boolean o : new boolean[] { true, false }) {
                same &= Arrays.equals(back.snapshot().cargoTonnes(c, o), s.cargoTonnes(c, o)) && Arrays.equals(viaGson.snapshot().cargoTonnes(c, o), s.cargoTonnes(c, o));
            }
        }
        check("...through State and back, and through Gson as a save is, equal to the bit", same);
        // A save without them (before WN7: Gson leaves the keys null), or with them of another length or not a number: none restored, the rest as it was.
        PartnerTrade.State old = pt.toState();
        old.cargoExportTonnes = null;
        old.cargoImportTonnes = null;
        PartnerTrade bare = new PartnerTrade(n);
        boolean ok = bare.restore(old);
        PartnerTrade.State odd = pt.toState();
        odd.cargoExportTonnes = new double[3];
        odd.cargoImportTonnes = odd.cargoImportTonnes.clone();
        odd.cargoImportTonnes[2] = Double.NaN;
        PartnerTrade wrong = new PartnerTrade(n);
        ok &= wrong.restore(odd);
        boolean none = true;
        for (Ports.Cargo c : Ports.Cargo.values()) {
            for (boolean o : new boolean[] { true, false }) none &= sum(bare.snapshot().cargoTonnes(c, o)) == 0 && sum(wrong.snapshot().cargoTonnes(c, o)) == 0;
        }
        check("...a save without them, or with the wrong length or a figure that is not a number, restores none, and the rest of the month as it was",
                ok && none && Arrays.equals(bare.snapshot().exportTonnes(), s.exportTonnes()) && Arrays.equals(wrong.snapshot().importTonnes(), s.importTonnes()));
        // The turn of the month clears it: a month later with one good tallied holds that good's tonnes alone.
        Good dry = goodOf(Ports.Cargo.DRY_BULK, 0);
        double[] only = nat(n, 0, 40, 0, 0, 0, 0);
        tally(pt, m + 1, dry, only, new double[n], 1);
        pt.close(m + 1);
        PartnerTrade.Snapshot next = pt.snapshot();
        boolean cleared = true;
        for (Ports.Cargo c : Ports.Cargo.values()) {
            cleared &= Arrays.equals(next.cargoTonnes(c, true), c == Ports.Cargo.DRY_BULK ? only : new double[n]) && sum(next.cargoTonnes(c, false)) == 0;
        }
        boolean old1 = true;
        for (Ports.Cargo c : Ports.Cargo.values()) old1 &= Arrays.equals(s.cargoTonnes(c, true), keep[c.ordinal()]);
        check("...cleared at the month's turn: a month later holds that month's tonnes alone, and the snapshot published before is not written to", cleared && old1);
    }

    /* =====================================================================
       2. PICK() DRAWS BY TONNES
       ===================================================================== */

    static void pickByTonnes() {
        out.println("\n--- 2. pick() draws a nation by the month's tonnes of the call's kind ---");
        int n = 5;
        Good dry = goodOf(Ports.Cargo.DRY_BULK, 0), liq = goodOf(Ports.Cargo.LIQUID, 0), box = goodOf(Ports.Cargo.CONTAINER, 0), plain = noCargo();
        PartnerTrade pt = trade(n, 9,
                new Row(dry, nat(n, 3000, 1000), nat(n, 0, 0, 0, 700)),
                new Row(liq, nat(n, 0, 0, 500), new double[n]),
                new Row(plain, nat(n, 0, 0, 0, 2000), new double[n]),
                new Row(box, nat(n, 1000), nat(n, 0, 3000)));
        PartnerTrade.Snapshot s = pt.snapshot();
        CallPartners p = CallPartners.of(s, 0, 0, NODE_LAT, NODE_LON, 1);
        double[] bulkOut = shares(p, Ports.Cargo.DRY_BULK, false, false, n);
        boolean split = Math.abs(bulkOut[1] - 0.75) <= tolerance(0.75, DRAWS) && Math.abs(bulkOut[2] - 0.25) <= tolerance(0.25, DRAWS);
        check("two nations 3 to 1 in dry bulk exports are drawn 3 to 1 for a bulker leaving, within " + SIGMAS + " standard deviations of " + DRAWS + " fixed draws",
                split && bulkOut[0] == 0);
        check("...a nation with none of that cargo is never drawn, though it has other cargo, or none at all", bulkOut[3] == 0 && bulkOut[4] == 0 && bulkOut[5] == 0);
        double[] bulkIn = shares(p, Ports.Cargo.DRY_BULK, true, false, n);
        check("...the direction is the call's: a bulker coming in draws the nation the dry bulk imports come from, and only that one", bulkIn[4] == 1);
        double[] liqOut = shares(p, Ports.Cargo.LIQUID, false, false, n);
        check("...the nation with the liquid exports is the only one a tanker leaving draws", liqOut[3] == 1);
        double[] boxOut = shares(p, Ports.Cargo.CONTAINER, false, false, n), boxIn = shares(p, Ports.Cargo.CONTAINER, true, false, n);
        boolean sameDraw = true;
        for (int i = 0; i < 2000; i++) sameDraw &= p.pick(Ports.Cargo.CONTAINER, false, false, hash(i)) == p.pick(Ports.Cargo.CONTAINER, true, false, hash(i));
        check("...the boxes draw on both ways at once (exports 1,000 t, imports 3,000 t is one in four and three in four), the same nation either way for a hash",
                sameDraw && Math.abs(boxOut[1] - 0.25) <= tolerance(0.25, DRAWS) && Math.abs(boxOut[2] - 0.75) <= tolerance(0.75, DRAWS) && Arrays.equals(boxOut, boxIn));
        double[] fallOut = shares(p, Ports.Cargo.GENERAL, false, false, n);
        double allOut = sum(s.exportTonnes());
        boolean falls = true;
        for (int i = 1; i <= n; i++) falls &= Math.abs(fallOut[i] - s.exportTonnes()[i - 1] / allOut) <= tolerance(s.exportTonnes()[i - 1] / allOut, DRAWS);
        check("a kind with no tonnes the month falls back on the direction's tonnes of every kind by nation, those of the good with no cargo too", falls && fallOut[4] > 0.2 && fallOut[5] == 0);
        double[] fallIn = shares(p, Ports.Cargo.GENERAL, true, false, n);
        double allIn = sum(s.importTonnes());
        check("...in the direction the call is in: the imports", Math.abs(fallIn[2] - s.importTonnes()[1] / allIn) <= tolerance(s.importTonnes()[1] / allIn, DRAWS) && fallIn[1] == 0 && fallIn[3] == 0);
        boolean low = p.pick(Ports.Cargo.DRY_BULK, false, false, 0L) == 1 && p.pick(Ports.Cargo.DRY_BULK, false, false, -1L) == 2;
        check("...a hash at either end of its range draws the first nation with tonnes and the last, never one with none or none at all", low);
        // NONE, an empty month, no snapshot.
        boolean zero = true;
        for (Ports.Cargo c : Ports.Cargo.values()) for (boolean in : new boolean[] { true, false }) for (boolean land : new boolean[] { true, false }) {
            zero &= CallPartners.NONE.pick(c, in, land, hash(3)) == 0;
        }
        PartnerTrade empty = trade(n, 9);
        CallPartners none = CallPartners.of(empty.snapshot(), 0, 0, NODE_LAT, NODE_LON, 1);
        for (Ports.Cargo c : Ports.Cargo.values()) zero &= none.pick(c, true, false, hash(3)) == 0 && none.pick(c, false, true, hash(4)) == 0;
        check("NONE draws no one, nor does a month with no trade at all; no snapshot is NONE", zero && CallPartners.of(null, 0, 0, NODE_LAT, NODE_LON, 1) == CallPartners.NONE
                && CallPartners.NONE.bearing(1) == -1 && !CallPartners.NONE.byLand(1) && CallPartners.NONE.nameOf(1) == null);
        // Bearings, the reach by land, names.
        boolean bearings = Math.abs(p.bearing(1) - 90) < 1e-6 && Math.abs(p.bearing(2) - 0) < 1e-6 && Math.abs(p.bearing(3) - 180) < 1e-6 && Math.abs(p.bearing(4) - 270) < 1e-6;
        check("a nation's bearing is the great circle's compass degrees from the city to its node: east 90, north 0, south 180, west 270", bearings && p.bearing(0) == -1 && p.bearing(99) == -1);
        double far = EarthProj.distanceM(0, 0, NODE_LAT[4], NODE_LON[4]) / 1000, near = EarthProj.distanceM(0, 0, NODE_LAT[0], NODE_LON[0]) / 1000;
        CallPartners hostFar = CallPartners.of(s, 0, 0, NODE_LAT, NODE_LON, 5);
        check("the reach by land is WorldMarket.MAX_LAND_KM from the city: the near nodes, not the far one, and the host wherever its node is",
                near < WorldMarket.MAX_LAND_KM && far > WorldMarket.MAX_LAND_KM && p.byLand(1) && p.byLand(2) && p.byLand(3) && p.byLand(4) && !p.byLand(5)
                        && hostFar.byLand(5) && !p.byLand(0) && !p.byLand(99));
        double[] lat = NODE_LAT.clone(), lon = NODE_LON.clone();
        lat[3] = Double.NaN;
        CallPartners unknown = CallPartners.of(s, 0, 0, lat, lon, 1);
        check("...a node with no place has no bearing and no reach by land, and can still be drawn", unknown.bearing(4) == -1 && !unknown.byLand(4)
                && shares(unknown, Ports.Cargo.GENERAL, false, false, n)[4] > 0.2);
        pt.setPlace(0, 0, new String[] { "Aland", "Bland", "Cland", "Dland", "Eland" });
        CallPartners named = CallPartners.of(pt.snapshot(), 0, 0, NODE_LAT, NODE_LON, 1);
        check("a nation's name is the snapshot's", "Cland".equals(named.nameOf(3)) && named.nameOf(0) == null && named.nameOf(9) == null);
        check("a map keeps the partners until the snapshot or the place moves: matches() says whether they are its",
                named.matches(pt.snapshot(), 0, 0, 1) && !named.matches(pt.snapshot(), 0, 1, 1) && !named.matches(pt.snapshot(), 0, 0, 2) && !named.matches(s, 0, 0, 1)
                        && !named.matches(null, 0, 0, 1));
    }

    /* =====================================================================
       THE MASK OF THE SEA (sections 3 and 4)
       ===================================================================== */

    /** A coast and its routes: the sea north of cell row COAST; an island of land beside the first route's offing in a second mask. */
    static final class Coast {
        static final long COAST = 20_000;
        final SeaRoutes.Sea plain = (ci, cj) -> cj < COAST;
        SeaRoutes.Sea island;
        final List<BoatSchedule.Berth> berths = new ArrayList<>();
        long siteX, siteY;
        double radius;
        SeaRoutes.Grid plainGrid, islandGrid;
        SeaRoutes.Found plainFound, found;
        List<BoatSchedule.Route> routes;
        /** The first route's offing, in plots, and its cell. */
        double ox, oy;
        long oc, oj;
    }

    private static Coast coastMemo;

    /**
     * Four dry bulk berths on a straight coast: the first two a few cells apart (the second joins the first's lane), the third far along the
     * shore (a lane of its own), the fourth inland (a straight lane). Found on the open sea first, to learn where the first route's offing
     * is, and then with an island of land beside it, three cells east, big enough to close the bearings that face it.
     */
    static Coast coast() {
        if (coastMemo != null) return coastMemo;
        Coast c = new Coast();
        long[][] at = { { 20_000, Coast.COAST - 1 }, { 20_006, Coast.COAST - 1 }, { 20_400, Coast.COAST - 1 }, { 20_002, Coast.COAST + 20 } };
        for (long[] a : at) c.berths.add(new BoatSchedule.Berth(Ports.Cargo.DRY_BULK, a[0] * SeaRoutes.CELL + SeaRoutes.CELL / 2, a[1] * SeaRoutes.CELL + SeaRoutes.CELL / 2));
        c.siteX = 20_200L * SeaRoutes.CELL;
        c.siteY = (Coast.COAST + 2_000) * SeaRoutes.CELL;
        c.radius = SeaRoutes.band(2_000);
        c.plainGrid = SeaRoutes.grid(c.plain);
        c.plainFound = SeaRoutes.find(c.plainGrid, c.berths, c.siteX, c.siteY, c.radius);
        BoatSchedule.Route first = c.plainFound.routes().get(0);
        c.ox = first.xs()[first.offing()];
        c.oy = first.ys()[first.offing()];
        c.oc = Math.floorDiv((long) Math.floor(c.ox), SeaRoutes.CELL);
        c.oj = Math.floorDiv((long) Math.floor(c.oy), SeaRoutes.CELL);
        final long oc = c.oc, oj = c.oj;
        c.island = (ci, cj) -> cj < Coast.COAST && !(ci >= oc + 3 && ci < oc + 40 && cj >= oj - 14 && cj <= oj + 14);
        c.islandGrid = SeaRoutes.grid(c.island);
        c.found = SeaRoutes.find(c.islandGrid, c.berths, c.siteX, c.siteY, c.radius);
        c.routes = c.found.routes();
        return coastMemo = c;
    }

    /** The first land on a straight leg from (x, y) along a compass bearing, in plots, looked at every quarter plot; infinity if none within max. */
    static double firstLand(SeaRoutes.Sea sea, double x, double y, double bearing, double max) {
        double ux = BoatSchedule.compassX(bearing), uy = BoatSchedule.compassY(bearing);
        for (double d = 0; d <= max; d += 0.25) {
            if (!sea.at(Math.floorDiv((long) Math.floor(x + ux * d), SeaRoutes.CELL), Math.floorDiv((long) Math.floor(y + uy * d), SeaRoutes.CELL))) return d;
        }
        return Double.POSITIVE_INFINITY;
    }

    /** The exit nearest a bearing by a plain scan of all of them: the least angle, the smaller bearing on a tie; -1 if none is open. */
    static int oracleExit(double[] exits, double bearing) {
        int best = -1;
        double bestAngle = Double.MAX_VALUE;
        for (int k = 0; k < exits.length; k++) {
            if (!(exits[k] > 0)) continue;
            double angle = Math.abs(Math.IEEEremainder(bearing - (double) k * SeaRoutes.BEARING_STEP, 360));
            if (angle < bestAngle) { bestAngle = angle; best = k * SeaRoutes.BEARING_STEP; }
        }
        return best;
    }

    /** A month's trade at sea: tonnes of dry bulk shipped out (and a quarter of that in liquid, general cargo and boxes, for which the coast has no berth: calls with none). */
    static Ports tradeOf(double dryBulk) {
        return MapCheck.tradeAtSea(dryBulk / 4, dryBulk, dryBulk / 4, dryBulk / 4);
    }

    /** The routes with their exits taken off: what they were before WN7. */
    static List<BoatSchedule.Route> withoutExits(List<BoatSchedule.Route> routes) {
        List<BoatSchedule.Route> out = new ArrayList<>();
        for (BoatSchedule.Route r : routes) out.add(r.withExits(null));
        return out;
    }

    /** A month's trade of dry bulk, in tonnes: about 130 Capesize calls. */
    static final double DRY_TONNES = 2e7;

    /* =====================================================================
       3. WITH NO PARTNERS NOTHING MOVES
       ===================================================================== */

    static void nothingMovesWithoutPartners(Coast c) {
        out.println("\n--- 3. with no partners every call, arrival, route and position is what it was ---");
        Ports ports = tradeOf(DRY_TONNES);
        List<BoatSchedule.Route> was = withoutExits(c.routes);
        BoatSchedule old = BoatSchedule.of(7, ports, was), none = BoatSchedule.of(7, ports, c.routes, Ports.BULK_TERMINAL_SHIPS, CallPartners.NONE);
        BoatSchedule viaNull = BoatSchedule.of(7, ports, c.routes, Ports.BULK_TERMINAL_SHIPS, null), viaShort = BoatSchedule.of(7, ports, c.routes);
        List<BoatSchedule.Call> calls = old.calls();
        long withRoute = calls.stream().filter(k -> k.route() >= 0).count();
        check("fixture: a month of calls, most on the coast's routes and some with none", calls.size() > 50 && withRoute > 0 && withRoute < calls.size()
                && calls.stream().anyMatch(k -> k.route() == 3));
        check("with NONE (the shorter forms and null too) the calls are the old schedule's, one for one: kind, class, direction, route, arrival, no partner, no exit",
                none.calls().equals(calls) && viaNull.calls().equals(calls) && viaShort.calls().equals(calls) && none.callCount() == old.callCount()
                        && calls.stream().allMatch(k -> k.partner() == 0 && k.exit() == -1));
        boolean same = true;
        int boats = 0;
        double leg = BoatSchedule.LEG_SECONDS / BoatSchedule.MONTH_SECONDS, berth = BoatSchedule.BERTH_SECONDS / BoatSchedule.MONTH_SECONDS;
        for (BoatSchedule.Call k : calls) {
            if (k.route() < 0) continue;
            double a = k.arrives();
            double[] times = { a - leg * 0.99, a - leg * 0.5, a - 1e-9, a, a + berth / 2, a + berth + leg * 0.5, a + berth + leg * 0.99 };
            for (double t : times) {
                BoatSchedule.Boat b0 = BoatSchedule.position(k, was.get(k.route()), t), b1 = BoatSchedule.position(k, c.routes.get(k.route()), t);
                same &= Objects.equals(b0, b1);
                if (b0 != null) boats++;
            }
        }
        check("...every boat's place, heading and fade is the same on the routes that have exits as on the routes that have none, at the times of its whole visit", same && boats > 200);
        long cx = Math.round(c.routes.get(0).berth().x()), cy = Math.round(c.routes.get(0).berth().y());
        Set<BoatSchedule.Boat> a0 = new HashSet<>(old.frame(cx - 200, cy - 200, cx + 200, cy + 200, 0.5)), a1 = new HashSet<>(none.frame(cx - 200, cy - 200, cx + 200, cy + 200, 0.5));
        check("...and a frame on the first berth's legs finds the same boats", a0.equals(a1) && !a0.isEmpty());
        // Partners twice, and what they leave alone.
        PartnerTrade pt = trade(5, 7, new Row(goodOf(Ports.Cargo.DRY_BULK, 0), nat(5, 3000, 1000), new double[5]));
        PartnerTrade.Snapshot snap = pt.snapshot();
        CallPartners p1 = CallPartners.of(snap, 0, 0, NODE_LAT, NODE_LON, 1), p2 = CallPartners.of(snap, 0, 0, NODE_LAT, NODE_LON, 1);
        BoatSchedule s1 = BoatSchedule.of(7, ports, c.routes, Ports.BULK_TERMINAL_SHIPS, p1), s2 = BoatSchedule.of(7, ports, c.routes, Ports.BULK_TERMINAL_SHIPS, p2);
        List<BoatSchedule.Call> partnered = s1.calls();
        check("the same inputs twice make the same partners and exits, call for call", s1.calls().equals(s2.calls()) && partnered.stream().anyMatch(k -> k.partner() > 0)
                && partnered.stream().anyMatch(k -> k.exit() >= 0));
        boolean untouched = partnered.size() == calls.size();
        for (int i = 0; untouched && i < calls.size(); i++) {
            BoatSchedule.Call a = calls.get(i), b = partnered.get(i);
            untouched &= a.cargo() == b.cargo() && a.ship() == b.ship() && a.inbound() == b.inbound() && a.route() == b.route() && a.arrives() == b.arrives();
        }
        check("...and a partner changes no call's kind, class, direction, route or arrival: the hash it is drawn by is its own", untouched);
        // The calls draw by tonnes: dry bulk leaving, 3 to 1 between two nations, over months of schedules.
        int calls3 = 0, to1 = 0, to2 = 0, other = 0;
        for (int month = 0; month < 60; month++) {
            BoatSchedule month1 = BoatSchedule.of(month, ports, c.routes, Ports.BULK_TERMINAL_SHIPS, p1);
            for (BoatSchedule.Call k : month1.calls()) {
                if (k.cargo() != Ports.Cargo.DRY_BULK || k.inbound()) continue;
                calls3++;
                if (k.partner() == 1) to1++; else if (k.partner() == 2) to2++; else other++;
            }
        }
        check("the month's calls draw by tonnes: dry bulk leaving goes to the nation with three quarters of the tonnes in three quarters of its calls (" + calls3 + " calls)",
                calls3 > 3000 && other == 0 && Math.abs(to1 / (double) calls3 - 0.75) <= tolerance(0.75, calls3) && Math.abs(to2 / (double) calls3 - 0.25) <= tolerance(0.25, calls3));
    }

    /* =====================================================================
       4. EXITS ON A MASK OF THE SEA
       ===================================================================== */

    static void exitsOnAMask(Coast c) {
        out.println("\n--- 4. an offing per bearing: exits on a mask of the sea ---");
        boolean compass = Math.abs(BoatSchedule.compassX(90) - 1) < 1e-12 && Math.abs(BoatSchedule.compassY(90)) < 1e-12
                && Math.abs(BoatSchedule.compassX(0)) < 1e-12 && Math.abs(BoatSchedule.compassY(0) + 1) < 1e-12
                && Math.abs(BoatSchedule.compassY(180) - 1) < 1e-12 && Math.abs(BoatSchedule.compassX(270) + 1) < 1e-12;
        check("the compass in the plane: east is +x, north is -y (y runs south), south +y, west -x", compass);
        List<BoatSchedule.Route> plain = c.plainFound.routes(), routes = c.routes;
        boolean open = true, sameWay = plain.size() == routes.size();
        double full = SeaRoutes.ABYSS_M / World.PLOT_M;
        for (int i = 0; i < 3; i++) {
            double[] ex = plain.get(i).exits();
            open &= ex != null && ex.length == SeaRoutes.EXITS && Arrays.stream(ex).allMatch(e -> e == full);
        }
        for (int i = 0; sameWay && i < plain.size(); i++) {
            sameWay &= Arrays.equals(plain.get(i).xs(), routes.get(i).xs()) && Arrays.equals(plain.get(i).ys(), routes.get(i).ys()) && plain.get(i).offing() == routes.get(i).offing();
        }
        check("fixture: the open sea's routes have every one of the 72 exits open at ABYSS_M", open && SeaRoutes.EXITS == 72);
        check("fixture: the island beside the first offing changes none of the ways (it takes away the spread of the routes that share the lane, whose fan would touch it; the third route is turned)",
                sameWay && routes.get(0).spread() == 0 && routes.get(2).spread() > 0);
        BoatSchedule.Route r0 = routes.get(0);
        double[] ex = r0.exits();
        int closed = 0, partial = 0, openN = 0;
        boolean land = true, sea = true, fullRun = true, least = true;
        double leastPlots = SeaRoutes.EXIT_LEAST_M / World.PLOT_M;
        for (int k = 0; k < SeaRoutes.EXITS; k++) {
            double b = (double) k * SeaRoutes.BEARING_STEP;
            double[] sides = { b - SeaRoutes.SPREAD_DEG, b, b + SeaRoutes.SPREAD_DEG };
            double hit = Double.POSITIVE_INFINITY;
            for (double side : sides) hit = Math.min(hit, firstLand(c.island, c.ox, c.oy, side, full + SAMPLE_SLACK));
            if (ex[k] == 0) {
                closed++;
            } else {
                openN++;
                if (ex[k] < full) partial++;
                least &= ex[k] >= leastPlots && ex[k] <= full;
                // Every leg of the run keeps to the sea, on the bearing and SPREAD_DEG either side of it (within the model's own sampling).
                for (double side : sides) sea &= firstLand(c.island, c.ox, c.oy, side, ex[k]) > ex[k] - SAMPLE_SLACK;
            }
            // Land within the least run (less the sampling's slack) on the bearing or either side of it: closed.
            if (hit < leastPlots - SAMPLE_SLACK) land &= ex[k] == 0;
            // No land on any of the three within the full run: open at the full run.
            if (Double.isInfinite(hit)) fullRun &= ex[k] == full;
        }
        out.printf("      the island beside the first offing: %d exits closed, %d open (%d of them short of ABYSS_M)%n", closed, openN, partial);
        check("EXITS: a bearing into the island is closed (0), whatever the sampling lets by a leg's end", land && closed >= 8);
        check("...every open exit's run keeps to the sea on its bearing and SPREAD_DEG either side, is at least EXIT_LEAST_M and at most ABYSS_M", sea && least && openN >= 30);
        check("...a bearing whose sea and turned legs are clear for ABYSS_M is open at the full ABYSS_M, and some are open short of it (the island's near edge)", fullRun && partial > 0);
        // Sharing: the joined route has its lane's exits, found once; a straight lane has none.
        BoatSchedule.Route joined = routes.get(1), lane = routes.get(3);
        check("a joined route has its lane's exits, the same array, found once (it joined the first; the third has an offing of its own)", c.found.joinAt()[1] >= 0
                && joined.exits() == r0.exits() && routes.get(2).exits() != null && routes.get(2).exits() != r0.exits());
        check("...a straight lane (no sea within reach) has none", lane.xs().length == 2 && lane.exits() == null && c.found.joinAt()[3] < 0);
        List<BoatSchedule.Route> kept = SeaRoutes.find(c.islandGrid, c.berths.subList(0, 1), c.siteX, c.siteY, c.radius).routes();
        SeaRoutes.Found more = SeaRoutes.extend(c.islandGrid, kept, c.berths, c.siteX, c.siteY, c.radius);
        check("routes kept keep their exits, the same objects, and the rest routed onto them are those found whole, exits too, a joined one sharing the kept lane's",
                more.routes().get(0) == kept.get(0) && more.routes().get(1).exits() == kept.get(0).exits() && MapCheck.sameRoutes(more.routes(), routes));
        // The calls of a partner east (90), whose bearing the island closes on the first two routes and the sea leaves open on the third.
        Ports ports = tradeOf(DRY_TONNES);
        PartnerTrade east = trade(5, 7, new Row(goodOf(Ports.Cargo.DRY_BULK, 0), nat(5, 1000), new double[5]));
        CallPartners pe = CallPartners.of(east.snapshot(), 0, 0, NODE_LAT, NODE_LON, 1);
        BoatSchedule se = BoatSchedule.of(7, ports, routes, Ports.BULK_TERMINAL_SHIPS, pe);
        List<BoatSchedule.Call> calls = se.calls();
        boolean nearest = true, onBearing = true, noExit = true, anyClosed = false, anyOpen = false, partnered = true;
        int onClosed = 0, onOpen = 0;
        for (BoatSchedule.Call k : calls) {
            partnered &= k.partner() == 1;
            if (k.route() < 0 || k.route() == 3) { noExit &= k.exit() == -1; continue; }
            BoatSchedule.Route rt = routes.get(k.route());
            double bearing = pe.bearing(1);
            nearest &= k.exit() == oracleExit(rt.exits(), bearing);
            if (rt.exitRun((int) Math.round(bearing)) == 0) { anyClosed = true; onClosed++; nearest &= k.exit() != 90 && k.exit() >= 0; }
            else { anyOpen = true; onOpen++; onBearing &= k.exit() == 90; }
        }
        out.printf("      a partner due east: %d calls on routes whose east is closed, %d on routes whose east is open%n", onClosed, onOpen);
        check("a call whose partner's bearing is closed leaves on the open exit nearest it (a plain scan of all 72 says which), not on the closed one", nearest && anyClosed && onClosed > 5);
        check("...one whose bearing is open leaves on it", onBearing && anyOpen && onOpen > 5);
        check("...a call on a straight lane or with no route has its partner and no exit; every call has the partner the east nation is", noExit && partnered);
        double[] lat = NODE_LAT.clone();
        lat[0] = Double.NaN;
        CallPartners pu = CallPartners.of(east.snapshot(), 0, 0, lat, NODE_LON, 1);
        List<BoatSchedule.Call> blind = BoatSchedule.of(7, ports, routes, Ports.BULK_TERMINAL_SHIPS, pu).calls();
        check("...a partner whose node has no place has no bearing, and its calls leave by no exit", blind.stream().allMatch(k -> k.partner() == 1 && k.exit() == -1));
        // Boats.
        positions(c, routes, calls);
        framesOnExits();
    }

    /** The boats of calls that have exits: the way to the offing unturned, the exit within the spread, the fade, the sea. */
    static void positions(Coast c, List<BoatSchedule.Route> routes, List<BoatSchedule.Call> calls) {
        boolean ride = true, head = true, line = true, fade = true, keeps = true, rev = true;
        int steps = 0, exitCalls = 0;
        for (BoatSchedule.Call k : calls) {
            if (k.exit() < 0) continue;
            BoatSchedule.Route rt = routes.get(k.route());
            double run = rt.exitRun(k.exit()), at = rt.fadeFrom(), total = at + run;
            double turn = rt.spread() > 0 ? BoatSchedule.turnOf(k, rt) : 0;
            double want = Math.atan2(BoatSchedule.compassY(k.exit()), BoatSchedule.compassX(k.exit())) + turn;
            double lastAlpha = 1.0;
            exitCalls++;
            for (int s = 0; s <= 40; s++) {
                double f = s / 40.0, d = f * total;
                BoatSchedule.Boat b = BoatSchedule.along(k, rt, f, false, true, false), in = BoatSchedule.along(k, rt, f, true, true, false);
                steps++;
                if (d <= at) {
                    double[] p = walk(rt, d);
                    ride &= Math.hypot(b.x() - p[0], b.y() - p[1]) < 1e-6 && b.alpha() == 1;
                } else {
                    double e = d - at;
                    line &= Math.abs(Math.hypot(b.x() - rt.xs()[rt.offing()], b.y() - rt.ys()[rt.offing()]) - e) < 1e-6
                            && Math.abs(Math.IEEEremainder(Math.atan2(b.y() - rt.ys()[rt.offing()], b.x() - rt.xs()[rt.offing()]) - want, 2 * Math.PI)) < 1e-9;
                    head &= Math.abs(Math.IEEEremainder(b.heading() - Math.atan2(BoatSchedule.compassY(k.exit()), BoatSchedule.compassX(k.exit())), 2 * Math.PI))
                            <= rt.spread() + 1e-9;
                    fade &= b.alpha() < 1 && b.alpha() <= lastAlpha + 1e-12;
                    if (e <= run - SAMPLE_SLACK) keeps &= c.island.at(Math.floorDiv((long) Math.floor(b.x()), SeaRoutes.CELL), Math.floorDiv((long) Math.floor(b.y()), SeaRoutes.CELL));
                }
                if (d > at) lastAlpha = b.alpha();
                rev &= Math.abs(Math.IEEEremainder(in.heading() - b.heading() - Math.PI, 2 * Math.PI)) < 1e-9 && in.x() == b.x() && in.y() == b.y();
            }
            fade &= BoatSchedule.along(k, rt, 1, false, true, false).alpha() < 1e-9 && BoatSchedule.along(k, rt, at / total, false, true, false).alpha() > 1 - 1e-9;
        }
        check("fixture: boats of " + exitCalls + " calls with exits, " + steps + " places along them", exitCalls > 20 && steps > 800);
        check("a boat rides its route's own points to the offing, unturned and whole", ride);
        check("...then leaves the offing on its exit's bearing, turned by the call's spread about the offing: its place is the exit's run turned, its heading within the spread of the exit", line && head);
        check("...fading from the offing (whole there) to the exit's end (nothing), and shows less the farther it goes", fade);
        check("...coming in it heads the other way, in the same places", rev);
        check("...and every place on the exit's run keeps to the sea", keeps);
    }

    /** A distance d along a route's points (the harness's own walk). */
    static double[] walk(BoatSchedule.Route rt, double d) {
        double[] xs = rt.xs(), ys = rt.ys();
        double left = d;
        for (int i = 0; i + 1 < xs.length; i++) {
            double seg = Math.hypot(xs[i + 1] - xs[i], ys[i + 1] - ys[i]);
            if (left <= seg || i + 2 == xs.length) {
                double u = seg > 0 ? left / seg : 0;
                return new double[] { xs[i] + (xs[i + 1] - xs[i]) * u, ys[i] + (ys[i + 1] - ys[i]) * u };
            }
            left -= seg;
        }
        return new double[] { xs[0], ys[0] };
    }

    static boolean inRect(BoatSchedule.Boat b, long x0, long y0, long x1, long y1) {
        return b.x() >= x0 && b.x() < x1 && b.y() >= y0 && b.y() < y1;
    }

    /**
     * frame() on a rectangle only an exit crosses: exactly the boats position() puts there. A hand-made route north, its last leg short (30
     * plots, so the spread's swing past the pivot is a few plots wide, where a found route's long last leg swings its box wider than any
     * exit) with every exit open; all its calls go to a partner due east.
     */
    static void framesOnExits() {
        double x = 50_000, y = 50_000;
        BoatSchedule.Berth berth = new BoatSchedule.Berth(Ports.Cargo.DRY_BULK, (long) x, (long) (y + 900));
        double[] exits = new double[SeaRoutes.EXITS];
        Arrays.fill(exits, SeaRoutes.ABYSS_M / World.PLOT_M);
        BoatSchedule.Route rt = BoatSchedule.Route.through(berth, new double[] { x, x, x, x }, new double[] { y + 900, y + 130, y + 100, y }, 2, 1,
                Math.toRadians(SeaRoutes.SPREAD_DEG)).withExits(exits);
        long x0 = (long) (x + 30), x1 = (long) (x + 90), y0 = (long) (y + 85), y1 = (long) (y + 115);
        double[][] boxes = BoatSchedule.legBoxes(rt);
        double[][] legsOnly = Arrays.copyOf(boxes, boxes.length - 1);
        check("fixture: a rectangle beside the offing that the route's legs do not cross and its exits' box does",
                boxes.length == rt.xs().length && !BoatSchedule.crosses(legsOnly, x0, y0, x1, y1) && BoatSchedule.crosses(boxes, x0, y0, x1, y1)
                        && BoatSchedule.crosses(rt, x0, y0, x1, y1) && !BoatSchedule.crosses(rt.withExits(null), x0, y0, x1, y1));
        PartnerTrade east = trade(5, 7, new Row(goodOf(Ports.Cargo.DRY_BULK, 0), nat(5, 1000), new double[5]));
        CallPartners p = CallPartners.of(east.snapshot(), 0, 0, NODE_LAT, NODE_LON, 1);
        BoatSchedule s = BoatSchedule.of(7, tradeOf(DRY_TONNES * 3), List.of(rt), Ports.BULK_TERMINAL_SHIPS, p);
        List<BoatSchedule.Call> calls = s.calls();
        check("fixture: calls on the route, all bound east and leaving on due east", calls.stream().anyMatch(k -> k.route() == 0)
                && calls.stream().filter(k -> k.route() == 0).allMatch(k -> k.partner() == 1 && k.exit() == 90));
        boolean exact = true;
        int boats = 0, times = 50;
        for (int i = 0; i < times; i++) {
            double t = (i + 0.5) / times;
            Set<BoatSchedule.Boat> want = new HashSet<>(), got = new HashSet<>();
            for (BoatSchedule.Call k : calls) {
                if (k.route() != 0) continue;
                BoatSchedule.Boat b = BoatSchedule.position(k, rt, t);
                if (b != null && inRect(b, x0, y0, x1, y1)) want.add(b);
            }
            for (BoatSchedule.Boat b : s.frame(x0, y0, x1, y1, t)) if (inRect(b, x0, y0, x1, y1)) got.add(b);
            exact &= want.equals(got);
            boats += want.size();
        }
        check("frame() on it returns exactly the boats position() puts in the rectangle, at " + times + " times of the month (" + boats + " boats on the exit east)", exact && boats > 0);
    }

    /* =====================================================================
       5. TRAINS DRAW LAND PARTNERS ONLY
       ===================================================================== */

    static void trainsByLand() {
        out.println("\n--- 5. trains go by land: only the host and the nodes within MAX_LAND_KM ---");
        int n = 5, kinds = Ports.Cargo.values().length;
        // Nation 1 near, nation 2 far, nation 3 the host (its node far), nation 4 near, nation 5 far.
        double[] lat = { 0, 0, 0, 15, 0 }, lon = { 15, 150, 80, 0, -150 };
        Good dry = goodOf(Ports.Cargo.DRY_BULK, 0);
        PartnerTrade pt = trade(n, 7,
                new Row(dry, nat(n, 1000, 3000, 2000), nat(n, 0, 1000, 0, 3000)),
                new Row(noCargo(), nat(n, 0, 0, 0, 0, 500), new double[n]));
        CallPartners p = CallPartners.of(pt.snapshot(), 0, 0, lat, lon, 3);
        check("fixture: nation 1 and 4 within MAX_LAND_KM, 2 and 5 beyond it, the host (3) far and the one it always may reach",
                p.byLand(1) && p.byLand(4) && !p.byLand(2) && !p.byLand(5) && p.byLand(3) && EarthProj.distanceM(0, 0, lat[2], lon[2]) / 1000 > WorldMarket.MAX_LAND_KM);
        double[] byLand = shares(p, Ports.Cargo.DRY_BULK, false, true, n), byBoat = shares(p, Ports.Cargo.DRY_BULK, false, false, n);
        double landTonnes = 1000 + 2000;
        check("pick() by land draws the land nations by their tonnes, one to two between nation 1 and the host, and never the far one",
                byLand[2] == 0 && byLand[5] == 0 && byLand[4] == 0 && Math.abs(byLand[1] - 1000 / landTonnes) <= tolerance(1000 / landTonnes, DRAWS)
                        && Math.abs(byLand[3] - 2000 / landTonnes) <= tolerance(2000 / landTonnes, DRAWS));
        check("...a boat may draw the far one: half of the dry bulk exports are to nation 2", Math.abs(byBoat[2] - 0.5) <= tolerance(0.5, DRAWS) && byBoat[0] == 0);
        double[] inLand = shares(p, Ports.Cargo.DRY_BULK, true, true, n);
        check("...coming in by land only the near seller is drawn (the far one sold 1,000 t of the 4,000)", inLand[4] == 1);
        double[] fall = shares(p, Ports.Cargo.GENERAL, false, true, n);
        check("a kind with no tonnes falls back on the exports of every kind by land: the host and the near nation, not the far ones with the good that has no cargo", fall[2] == 0 && fall[5] == 0
                && fall[1] > 0.2 && fall[3] > 0.5);
        PartnerTrade farOnly = trade(n, 7, new Row(dry, nat(n, 0, 3000), new double[n]));
        CallPartners pf = CallPartners.of(farOnly.snapshot(), 0, 0, lat, lon, 3);
        check("...and where only a far nation has any, a train draws no one though a boat draws it", shares(pf, Ports.Cargo.DRY_BULK, false, true, n)[0] == 1
                && shares(pf, Ports.Cargo.DRY_BULK, false, false, n)[2] == 1);
        // Trains.
        int[] seen = new int[n + 1];
        int[] seenIn = new int[n + 1];
        List<TrainSchedule.Route> routes = MapCheck.handYard(100, 100);
        double[] in = new double[kinds], outT = new double[kinds];
        in[Ports.Cargo.DRY_BULK.ordinal()] = 200_000;
        outT[Ports.Cargo.DRY_BULK.ordinal()] = 200_000;
        boolean untouched = true;
        int trains = 0;
        for (int month = 0; month < 200; month++) {
            TrainSchedule plain = TrainSchedule.of(month, in, outT, routes), with = TrainSchedule.of(month, in, outT, routes, p);
            List<TrainSchedule.Call> a = plain.calls(), b = with.calls();
            untouched &= a.size() == b.size();
            for (int i = 0; untouched && i < a.size(); i++) {
                TrainSchedule.Call x = a.get(i), y = b.get(i);
                untouched &= x.kind() == y.kind() && x.inbound() == y.inbound() && x.route() == y.route() && x.arrives() == y.arrives() && x.partner() == 0;
                (y.inbound() ? seenIn : seen)[y.partner()]++;
                trains++;
            }
        }
        double outShare1 = seen[1] / (double) (seen[1] + seen[3]);
        check("trains draw only land partners: outbound hoppers go to nation 1 and the host in one to two, never to the far nations (" + trains + " trains)",
                seen[0] == 0 && seen[2] == 0 && seen[4] == 0 && seen[5] == 0 && seen[3] > 0 && Math.abs(outShare1 - 1.0 / 3) <= tolerance(1.0 / 3, seen[1] + seen[3]));
        check("...inbound ones come from the near seller alone, the far one never boards", seenIn[0] == 0 && seenIn[1] == 0 && seenIn[2] == 0
                && seenIn[3] == 0 && seenIn[5] == 0 && seenIn[4] > 0);
        check("...and a partner changes no train's kind, direction, route or arrival, and the schedule without them has none", untouched);
        // A frame's trains carry them.
        TrainSchedule s1 = TrainSchedule.of(7, in, outT, routes, p), s2 = TrainSchedule.of(7, in, outT, routes, CallPartners.of(pt.snapshot(), 0, 0, lat, lon, 3));
        List<TrainSchedule.Call> all = s1.calls();
        boolean carried = true;
        int found = 0;
        for (double t = 0.05; t < 1; t += 0.05) {
            for (TrainSchedule.Train tr : s1.frame(-200, -50, 400, 50, t)) {
                carried &= all.contains(tr.call()) && tr.call().partner() > 0;
                found++;
            }
        }
        check("a frame's trains carry their partners, the same calls as the schedule lists, and the same inputs twice make the same ones", carried && found > 0 && all.equals(s2.calls())
                && TrainSchedule.of(7, in, outT, routes, null).calls().stream().allMatch(k -> k.partner() == 0));
    }

    /* =====================================================================
       6. THE COMPASS IS THE EARTH'S
       ===================================================================== */

    /** The longest step the compass is held over, in plots: 2,000, 60 km. */
    static final double STEP_PLOTS = 2_000;

    /** The largest error found, in degrees. */
    static double worst;

    static boolean compassFrom(EarthProj proj, double px, double py) {
        double[] here = new double[2], there = new double[2];
        boolean ok = proj.latLonOfPlot(px, py, here);
        for (int b = 0; ok && b < 360; b += 45) {
            ok = proj.latLonOfPlot(px + STEP_PLOTS * BoatSchedule.compassX(b), py + STEP_PLOTS * BoatSchedule.compassY(b), there);
            double got = TradeLanes.bearingDeg(here[0], here[1], there[0], there[1]);
            double err = Math.abs(Math.IEEEremainder(got - b, 360));
            worst = Math.max(worst, err);
            ok &= err <= 1;
        }
        return ok;
    }

    static void compassOnTheEarth() {
        out.println("\n--- 6. the plane's compass is the Earth's ---");
        double[][] spots = { { 51.9, 4.5 }, { -31.95, 115.86 }, { -0.18, -78.47 }, { 64.1, -21.9 } };
        boolean centres = true;
        worst = 0;
        for (double[] sp : spots) {
            EarthProj proj = new EarthProj(sp[0], sp[1]);
            centres &= compassFrom(proj, EarthProj.plotX(0), EarthProj.plotY(0));
        }
        check("from the spot a world is centred on, a step along (sin b, -cos b) has the bearing b to a degree for b every 45 degrees, in Rotterdam, Perth, Quito and Reykjavik (worst "
                + String.format("%.4f", worst) + " deg)", centres);
        Path file = EarthData.locate();
        if (file == null || !Files.isRegularFile(file)) {
            out.println("SKIPPED: earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": the founded world's site was not checked");
            return;
        }
        worst = 0;
        boolean founded = true;
        for (double[] sp : spots) {
            World w = World.of(World.Key.earth(Founding.DEFAULT_WORLD_SEED, sp[0], sp[1], EarthData.EARTH_2));
            EarthProj proj = w.earthTerrain().projection();
            founded &= compassFrom(proj, w.foundingX() + 0.5, w.foundingY() + 0.5);
        }
        check("...and from the founded city's own site on the Earth file, within the same degree (worst " + String.format("%.4f", worst) + " deg)", founded);
    }

    /* =====================================================================
       7. TIMING
       ===================================================================== */

    static void frameAtTenBillion() {
        out.println("\n--- 7. at 10 billion a frame's boats with exits and partners over 196 nations ---");
        long sx = World.SIDE / 2, sy = World.SIDE / 2;
        double[] exits = new double[SeaRoutes.EXITS];
        for (int k = 0; k < exits.length; k++) exits[k] = k >= 12 && k <= 24 ? 0 : SeaRoutes.ABYSS_M / World.PLOT_M;
        List<BoatSchedule.Route> lanes = new ArrayList<>();
        for (BoatSchedule.Route r : MapCheck.protoLanes(sx, sy)) lanes.add(r.withExits(exits));
        int nations = 196;
        PartnerTrade pt = new PartnerTrade(nations);
        for (Ports.Cargo c : Ports.Cargo.values()) {
            double[] to = new double[nations], from = new double[nations];
            for (int i = 0; i < nations; i++) {
                long h = World.mix(c.ordinal() * 1000L + i);
                to[i] = World.unit(h) < 0.3 ? 0 : 1 + World.unit(h) * 1000;
                from[i] = World.unit(World.mix(h)) < 0.3 ? 0 : 1 + World.unit(World.mix(h)) * 1000;
            }
            tally(pt, 7, goodOf(c, 0), to, from, 1);
        }
        pt.close(7);
        double[] lat = new double[nations], lon = new double[nations];
        for (int i = 0; i < nations; i++) {
            lat[i] = -70 + 140 * World.unit(World.mix(2L * i + 1));
            lon[i] = -180 + 360 * World.unit(World.mix(2L * i + 2));
        }
        CallPartners partners = CallPartners.of(pt.snapshot(), 0, 0, lat, lon, 1);
        double tonnes10B = MapCheck.PROTO_TONNES * 1e10 / MapCheck.PROTO_PEOPLE;
        BoatSchedule with = BoatSchedule.of(7, MapCheck.tradeAtSea(tonnes10B / 2, tonnes10B / 2, 0, 0), lanes, Ports.BULK_TERMINAL_SHIPS, partners);
        BoatSchedule without = BoatSchedule.of(7, MapCheck.tradeAtSea(tonnes10B / 2, tonnes10B / 2, 0, 0), lanes, Ports.BULK_TERMINAL_SHIPS, CallPartners.NONE);
        long midX = sx + MapCheck.PROTO_LANE_DX / 2, midY = sy + MapCheck.PROTO_LANE_DY / 2;
        long[][] views = {
                { Math.round(MapCheck.SCREEN_W_PX / MapFrame.L0_FROM), Math.round(MapCheck.SCREEN_H_PX / MapFrame.L0_FROM) },
                { Math.round(MapCheck.SCREEN_W_PX / MapFrame.L1_FROM), Math.round(MapCheck.SCREEN_H_PX / MapFrame.L1_FROM) },
                { 8L * World.DISTRICT, Math.abs(MapCheck.PROTO_LANE_DY) + 2 } };
        double[] best = new double[views.length], plainBest = new double[views.length];
        Arrays.fill(best, Double.MAX_VALUE);
        Arrays.fill(plainBest, Double.MAX_VALUE);
        int[] seen = new int[views.length];
        int[] exiting = new int[views.length];
        for (int round = 0; round < MapCheck.WARM_ROUNDS + MapCheck.TIMED_ROUNDS; round++) {
            for (int v = 0; v < views.length; v++) {
                long ax = midX - views[v][0] / 2, ay = midY - views[v][1] / 2, bx = ax + views[v][0], by = ay + views[v][1];
                for (int which = 0; which < 2; which++) {
                    BoatSchedule s = which == 0 ? with : without;
                    long f0 = System.nanoTime();
                    int count = 0;
                    for (int k = 0; k < MapCheck.BOAT_FRAMES; k++) {
                        List<BoatSchedule.Boat> boats = s.frame(ax, ay, bx, by, (k + 0.5) / MapCheck.BOAT_FRAMES);
                        count += boats.size();
                    }
                    double ms = (System.nanoTime() - f0) / 1e6 / MapCheck.BOAT_FRAMES;
                    if (round >= MapCheck.WARM_ROUNDS) {
                        if (which == 0) best[v] = Math.min(best[v], ms); else plainBest[v] = Math.min(plainBest[v], ms);
                    }
                    if (which == 0) seen[v] = count / MapCheck.BOAT_FRAMES;
                }
            }
        }
        // Untimed: the boats on each view at mid-month that leave by an exit.
        for (int v = 0; v < views.length; v++) {
            long ax = midX - views[v][0] / 2, ay = midY - views[v][1] / 2;
            for (BoatSchedule.Boat b : with.frame(ax, ay, ax + views[v][0], ay + views[v][1], 0.5)) if (b.call().exit() >= 0) exiting[v]++;
        }
        out.printf("      %,d calls a month on %,d lanes: a frame with partners takes %.3f ms on an L0 screen (%,d boats), %.3f on an L1 one (%,d), %.3f on 8 districts (%,d);"
                + " without them %.3f, %.3f, %.3f%n", with.callCount(), lanes.size(), best[0], seen[0], best[1], seen[1], best[2], seen[2], plainBest[0], plainBest[1], plainBest[2]);
        check("fixture: boats on every view, and boats that leave by an exit among them", seen[0] > 0 && seen[1] > 0 && seen[2] > 0 && exiting[0] + exiting[1] + exiting[2] > 0);
        check("a frame's boats with exits and partners over 196 nations take no more than BOAT_FRAME_MS (0.5 ms): an L0 screen, an L1 one and 8 districts",
                Timing.within(best[0], MapCheck.BOAT_FRAME_MS) && Timing.within(best[1], MapCheck.BOAT_FRAME_MS) && Timing.within(best[2], MapCheck.BOAT_FRAME_MS));
    }
}
