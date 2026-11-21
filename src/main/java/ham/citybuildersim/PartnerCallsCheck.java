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
 * kind of cargo, CallPartners' draw of a nation for a call, the boats' exits (an offing per bearing) and the trains' land partners, and (slice U)
 * what the map draws of them: a boat's or a train's words, and the forks of a lane at the offing. The map (ui/MapView) draws them from those;
 * nothing in the model's month reads any of it, so no trace moves.
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
 *   <li>A BOAT AND A TRAIN NAME THEIR PARTNER: the words are the class's or kind's, then " to " the nation for a boat or train going out laden with
 *       exports, " from " it for one coming in with imports, the nation alone for the boxes, which go both ways; the class alone with no partner,
 *       none named, or NONE; and in a month's calls and trains each says what pick() drew it by.</li>
 *   <li>THE LANE FORKS AT THE OFFING: seaPartners(kind) is exactly the nations pick() draws for the kind, biggest first, and seaShares(kind) their
 *       shares of its tonnes; a route's forks are exactly the exits (by a scan of all 72) whose nations' shares add to at least FORK_SHARE - an
 *       exit under it has none, two nations on one exit add, more than MAX_FORKS over it keep the largest, the smaller bearing on a tie - none for a
 *       route without exits, for NONE, or for partners with no bearing, and a small partner's boat still leaves on its own exit; forks() on a
 *       rectangle gives each offing's once whatever routes share it, the union of their kinds' (each capped already); and a frame of them takes
 *       no more than BOAT_FRAME_MS.</li>
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
        wordsOfACall(c);
        forksAtTheOffing(c);
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
        if (Timing.skip("PartnerCallsCheck.frameAtTenBillion")) return;
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

    /* =====================================================================
       8. A BOAT AND A TRAIN NAME THEIR PARTNER
       ===================================================================== */

    /** The middle dot the words carry between a class and its partner. */
    static final String DOT = " · ";

    /** A month's trade at sea both ways: dry bulk out and in, and boxes (which count one way and carry both). */
    static Ports tradeBothWays(double dryOut, double dryIn, double boxes) {
        Ports p = new Ports();
        p.beginMonth();
        p.tally(goodOf(Ports.Cargo.DRY_BULK, 0), dryIn, dryOut, 1);
        p.tally(goodOf(Ports.Cargo.CONTAINER, 0), boxes, boxes, 1);
        return p;
    }

    static void wordsOfACall(Coast c) {
        out.println("\n--- 8. a boat's or a train's words name its partner and the way it goes ---");
        int n = 5;
        String[] names = { "Aland", "Bland", "Cland", "Dland", "Eland" };
        Good dry = goodOf(Ports.Cargo.DRY_BULK, 0);
        PartnerTrade pt = trade(n, 9, new Row(dry, nat(n, 100), nat(n, 0, 50)));
        pt.setPlace(0, 0, names);
        CallPartners p = CallPartners.of(pt.snapshot(), 0, 0, NODE_LAT, NODE_LON, 1);
        BoatSchedule.Call goingOut = new BoatSchedule.Call(Ports.Cargo.DRY_BULK, Ports.Ship.CAPESIZE, false, 0, 0.5, 2, -1);
        BoatSchedule.Call coming = new BoatSchedule.Call(Ports.Cargo.DRY_BULK, Ports.Ship.PANAMAX, true, 0, 0.5, 1, -1);
        check("a boat going out laden with exports says its class and 'to' its partner; one coming in with imports 'from' it",
                p.words(goingOut).equals(Ports.Ship.CAPESIZE.label() + DOT + "to Bland") && p.words(coming).equals(Ports.Ship.PANAMAX.label() + DOT + "from Aland"));
        BoatSchedule.Call boxesIn = new BoatSchedule.Call(Ports.Cargo.CONTAINER, Ports.Ship.FEEDER, true, 0, 0.5, 3, 90);
        BoatSchedule.Call boxesOut = new BoatSchedule.Call(Ports.Cargo.CONTAINER, Ports.Ship.FEEDER, false, 0, 0.5, 3, -1);
        check("...a feeder, which carries boxes both ways, says the nation alone, whichever way its call is flagged",
                p.words(boxesIn).equals(Ports.Ship.FEEDER.label() + DOT + "Cland") && p.words(boxesOut).equals(p.words(boxesIn)));
        TrainSchedule.Call hopperOut = new TrainSchedule.Call(TrainSchedule.Kind.HOPPER, false, 0, 0.5, 4);
        TrainSchedule.Call tankIn = new TrainSchedule.Call(TrainSchedule.Kind.TANK, true, 0, 0.5, 5);
        TrainSchedule.Call boxTrain = new TrainSchedule.Call(TrainSchedule.Kind.INTERMODAL, true, 0, 0.5, 1);
        check("a train says its kind and the nation the same way: 'to' going out, 'from' coming in, the nation alone for the boxes",
                p.words(hopperOut).equals(TrainSchedule.Kind.HOPPER.label() + DOT + "to Dland") && p.words(tankIn).equals(TrainSchedule.Kind.TANK.label() + DOT + "from Eland")
                        && p.words(boxTrain).equals(TrainSchedule.Kind.INTERMODAL.label() + DOT + "Aland")
                        && p.words(new TrainSchedule.Call(TrainSchedule.Kind.INTERMODAL, false, 0, 0.5, 1)).equals(p.words(boxTrain)));
        PartnerTrade bare = trade(n, 9, new Row(dry, nat(n, 100), new double[n]));
        CallPartners unnamed = CallPartners.of(bare.snapshot(), 0, 0, NODE_LAT, NODE_LON, 1);
        BoatSchedule.Call noOne = new BoatSchedule.Call(Ports.Cargo.DRY_BULK, Ports.Ship.CAPESIZE, false, 0, 0.5);
        check("with no partner the class alone: a call with none, NONE, a nation the snapshot has no name for, a null call says nothing",
                p.words(noOne).equals(Ports.Ship.CAPESIZE.label()) && p.words(new TrainSchedule.Call(TrainSchedule.Kind.HOPPER, false, 0, 0.5)).equals(TrainSchedule.Kind.HOPPER.label())
                        && CallPartners.NONE.words(goingOut).equals(Ports.Ship.CAPESIZE.label()) && CallPartners.NONE.words(hopperOut).equals(TrainSchedule.Kind.HOPPER.label())
                        && unnamed.words(goingOut).equals(Ports.Ship.CAPESIZE.label()) && p.words((BoatSchedule.Call) null).isEmpty() && p.words((TrainSchedule.Call) null).isEmpty());
        // The words are the way the call was drawn: dry bulk exports to nation 1, imports from nation 2, boxes both ways with nation 3.
        PartnerTrade ways = trade(n, 7, new Row(dry, nat(n, 1000), nat(n, 0, 1000)), new Row(goodOf(Ports.Cargo.CONTAINER, 0), nat(n, 0, 0, 500), nat(n, 0, 0, 500)));
        ways.setPlace(0, 0, names);
        CallPartners pw = CallPartners.of(ways.snapshot(), 0, 0, NODE_LAT, NODE_LON, 1);
        List<BoatSchedule.Call> calls = BoatSchedule.of(7, tradeBothWays(DRY_TONNES, DRY_TONNES, 9e6), c.routes, Ports.BULK_TERMINAL_SHIPS, pw).calls();
        int outs = 0, ins = 0, boxes = 0;
        boolean told = true;
        for (BoatSchedule.Call k : calls) {
            String want;
            if (k.cargo() == Ports.Cargo.DRY_BULK && !k.inbound()) { want = k.ship().label() + DOT + "to Aland"; outs++; }
            else if (k.cargo() == Ports.Cargo.DRY_BULK) { want = k.ship().label() + DOT + "from Bland"; ins++; }
            else if (k.cargo() == Ports.Cargo.CONTAINER) { want = k.ship().label() + DOT + "Cland"; boxes++; }
            else continue;
            told &= pw.words(k).equals(want);
        }
        check("...in a month's calls each says what pick() drew it by: the bulkers leaving go to the nation they sell to, those coming in come from the one they buy of, the feeders name the boxes' (" + outs + ", " + ins + ", " + boxes + " calls)",
                told && outs > 20 && ins > 20 && boxes > 0);
        List<TrainSchedule.Route> yard = MapCheck.handYard(100, 100);
        double[] in = new double[Ports.Cargo.values().length], outT = new double[in.length];
        // Ten hopper trains each way and ten box trains: under TrainSchedule.MOST_A_MONTH together, so each stands once.
        in[Ports.Cargo.DRY_BULK.ordinal()] = outT[Ports.Cargo.DRY_BULK.ordinal()] = 10 * TrainSchedule.Kind.HOPPER.tonnes();
        in[Ports.Cargo.CONTAINER.ordinal()] = outT[Ports.Cargo.CONTAINER.ordinal()] = 10 * TrainSchedule.Kind.INTERMODAL.tonnes();
        int tOut = 0, tIn = 0, tBox = 0;
        boolean trainsTold = true;
        for (TrainSchedule.Call k : TrainSchedule.of(7, in, outT, yard, pw).calls()) {
            String want;
            if (k.kind() == TrainSchedule.Kind.HOPPER && !k.inbound()) { want = k.kind().label() + DOT + "to Aland"; tOut++; }
            else if (k.kind() == TrainSchedule.Kind.HOPPER) { want = k.kind().label() + DOT + "from Bland"; tIn++; }
            else if (k.kind() == TrainSchedule.Kind.INTERMODAL) { want = k.kind().label() + DOT + "Cland"; tBox++; }
            else continue;
            trainsTold &= pw.words(k).equals(want);
        }
        check("...and a month's trains: the hoppers leaving to the nation they sell to, those coming in from the one they buy of, the intermodal ones the boxes' (" + tOut + ", " + tIn + ", " + tBox + " trains)",
                trainsTold && tOut > 5 && tIn > 5 && tBox > 0);
    }

    /* =====================================================================
       9. THE FORKS AT THE OFFING
       ===================================================================== */

    /**
     * A kind's tonnes by nation as pick() draws a boat of it, read off the snapshot with no help from CallPartners: in each direction the kind's own
     * tonnes if it has any there, else every kind's (the boxes both ways added); the two directions added.
     */
    static double[] weights(PartnerTrade.Snapshot s, Ports.Cargo kind) {
        int n = s.nations();
        double[] to = s.cargoTonnes(kind, true), from = s.cargoTonnes(kind, false), w = new double[n];
        if (kind == Ports.Cargo.CONTAINER) {
            boolean own = sum(to) + sum(from) > 0;
            for (int i = 0; i < n; i++) w[i] = own ? to[i] + from[i] : s.exportTonnes()[i] + s.importTonnes()[i];
        } else {
            boolean ownOut = sum(to) > 0, ownIn = sum(from) > 0;
            for (int i = 0; i < n; i++) w[i] = (ownOut ? to[i] : s.exportTonnes()[i]) + (ownIn ? from[i] : s.importTonnes()[i]);
        }
        return w;
    }

    /**
     * The forks of a route held to a plain read of the snapshot: each nation's share of the kind's tonnes (weights() over their sum, the nations with
     * no bearing in the sum) added to the exit (oracleExit(), a scan of all 72) of its bearing; the exits at FORK_SHARE or more, the MAX_FORKS largest
     * of them (the smaller bearing first on a tie), ascending.
     */
    static int[] expectedForks(BoatSchedule.Route rt, PartnerTrade.Snapshot s, CallPartners p) {
        if (rt.exits() == null) return new int[0];
        double[] w = weights(s, rt.berth().cargo()), byExit = new double[SeaRoutes.EXITS];
        double total = sum(w);
        for (int i = 0; i < w.length; i++) {
            if (!(w[i] > 0) || p.bearing(i + 1) < 0) continue;
            int e = oracleExit(rt.exits(), p.bearing(i + 1));
            if (e >= 0) byExit[e / SeaRoutes.BEARING_STEP] += w[i] / total;
        }
        List<Integer> over = new ArrayList<>();
        for (int k = 0; k < byExit.length; k++) if (byExit[k] >= BoatSchedule.FORK_SHARE) over.add(k);
        over.sort((x, y) -> byExit[x] == byExit[y] ? Integer.compare(x, y) : Double.compare(byExit[y], byExit[x]));
        return over.stream().limit(BoatSchedule.MAX_FORKS).mapToInt(k -> k * SeaRoutes.BEARING_STEP).sorted().toArray();
    }

    /** The legs forks() should give for some routes: per distinct offing the union of their routes' expected forks, each as {offing x, y, end x, y}. */
    static Set<List<Double>> expectedLegs(List<BoatSchedule.Route> routes, PartnerTrade.Snapshot s, CallPartners p) {
        java.util.Map<List<Double>, java.util.TreeSet<Integer>> byOffing = new java.util.HashMap<>();
        java.util.Map<List<Double>, BoatSchedule.Route> routeAt = new java.util.HashMap<>();
        for (BoatSchedule.Route rt : routes) {
            int[] f = expectedForks(rt, s, p);
            List<Double> at = List.of(rt.xs()[rt.offing()], rt.ys()[rt.offing()]);
            if (f.length == 0) continue;
            for (int e : f) byOffing.computeIfAbsent(at, k -> new java.util.TreeSet<>()).add(e);
            routeAt.putIfAbsent(at, rt);
        }
        Set<List<Double>> legs = new HashSet<>();
        for (java.util.Map.Entry<List<Double>, java.util.TreeSet<Integer>> e : byOffing.entrySet()) {
            double ox = e.getKey().get(0), oy = e.getKey().get(1);
            for (int bearing : e.getValue()) {
                double run = routeAt.get(e.getKey()).exitRun(bearing);
                legs.add(List.of(ox, oy, ox + BoatSchedule.compassX(bearing) * run, oy + BoatSchedule.compassY(bearing) * run));
            }
        }
        return legs;
    }

    /** Nations standing 0.1 radian from the city at (0, 0) on the given compass bearings, one each: {latitudes, longitudes}. */
    static double[][] ringNodes(double... bearings) {
        double[] lat = new double[bearings.length], lon = new double[bearings.length];
        double d = 0.1;
        for (int i = 0; i < bearings.length; i++) {
            double b = Math.toRadians(bearings[i]);
            lat[i] = Math.toDegrees(Math.asin(Math.sin(d) * Math.cos(b)));
            lon[i] = Math.toDegrees(Math.atan2(Math.sin(b) * Math.sin(d), Math.cos(d)));
        }
        return new double[][] { lat, lon };
    }

    /** A schedule of the given routes whose dry bulk goes to nations standing on `bearings`, `weights` tonnes each, and the partners it draws by. */
    static CallPartners ringPartners(double[] bearings, double[] weights, Good good) {
        double[][] at = ringNodes(bearings);
        return CallPartners.of(trade(bearings.length, 7, new Row(good, weights, new double[bearings.length])).snapshot(), 0, 0, at[0], at[1], 1);
    }

    static Set<List<Double>> legsOf(double[] forks) {
        Set<List<Double>> out = new HashSet<>();
        for (int i = 0; i < forks.length; i += 4) out.add(List.of(forks[i], forks[i + 1], forks[i + 2], forks[i + 3]));
        return out;
    }

    static void forksAtTheOffing(Coast c) {
        out.println("\n--- 9. the lane forks at the offing into the exits this month's partners take ---");
        int n = 5;
        Good dry = goodOf(Ports.Cargo.DRY_BULK, 0), liq = goodOf(Ports.Cargo.LIQUID, 0), box = goodOf(Ports.Cargo.CONTAINER, 0), gen = goodOf(Ports.Cargo.GENERAL, 0);
        // Nations 1 and 5 lie due east, 2 north, 3 south, 4 west: dry bulk is sold to 1, 2 and 5 and bought of 4; nation 3 has the other cargo only.
        PartnerTrade pt = trade(n, 7, new Row(dry, nat(n, 100, 50, 0, 0, 20), nat(n, 0, 0, 0, 30)), new Row(gen, nat(n, 0, 0, 40), new double[n]),
                new Row(box, nat(n, 25), nat(n, 0, 25)), new Row(liq, nat(n, 0, 0, 60), new double[n]));
        PartnerTrade.Snapshot s = pt.snapshot();
        CallPartners p = CallPartners.of(s, 0, 0, NODE_LAT, NODE_LON, 1);
        // seaPartners(): the nations pick() can draw for a boat of the kind, either way, biggest first.
        Ports.Cargo[] kinds = Ports.Cargo.values();
        boolean sameSet = true, ordered = true;
        for (Ports.Cargo k : kinds) {
            Set<Integer> drawn = new HashSet<>();
            for (int i = 0; i < 3000; i++) {
                drawn.add(p.pick(k, true, false, hash(i)));
                drawn.add(p.pick(k, false, false, hash(i)));
            }
            int[] sea = p.seaPartners(k);
            Set<Integer> sea1 = new HashSet<>();
            for (int v : sea) sea1.add(v);
            sameSet &= drawn.equals(sea1) && sea.length == sea1.size();
        }
        int[] dryOrder = p.seaPartners(Ports.Cargo.DRY_BULK), boxOrder = p.seaPartners(Ports.Cargo.CONTAINER);
        ordered = Arrays.equals(dryOrder, new int[] { 1, 2, 4, 5 }) && Arrays.equals(boxOrder, new int[] { 1, 2 });
        check("seaPartners(kind) is exactly the nations pick() draws for the kind, going out or coming in (a direction with none of the kind falls back on every kind's)", sameSet);
        check("...biggest first, the lower number first on a tie: dry bulk 1 (100), 2 (50), 4 (30), 5 (20); the boxes 1 and 2 at 25 each", ordered);
        check("...none for NONE, an empty month and a null kind; a new array each time", CallPartners.NONE.seaPartners(Ports.Cargo.DRY_BULK).length == 0
                && CallPartners.of(trade(n, 7).snapshot(), 0, 0, NODE_LAT, NODE_LON, 1).seaPartners(Ports.Cargo.DRY_BULK).length == 0 && p.seaPartners(null).length == 0
                && p.seaPartners(Ports.Cargo.DRY_BULK) != p.seaPartners(Ports.Cargo.DRY_BULK));
        boolean sharesOk = true;
        for (Ports.Cargo k : kinds) {
            double[] sh = p.seaShares(k);
            sharesOk &= sh.length == p.seaPartners(k).length && sh.length > 0 && Math.abs(sum(sh) - 1) < 1e-9;
            for (int i = 1; i < sh.length; i++) sharesOk &= sh[i] <= sh[i - 1];
        }
        double[] dryShares = p.seaShares(Ports.Cargo.DRY_BULK);
        check("seaShares(kind) are those nations' shares of the kind's tonnes in the same order, adding to 1, never rising: dry bulk .50, .25, .15, .10 of the 200 t",
                sharesOk && dryShares.length == 4 && near(dryShares[0], 0.5) && near(dryShares[1], 0.25) && near(dryShares[2], 0.15) && near(dryShares[3], 0.10)
                        && CallPartners.NONE.seaShares(Ports.Cargo.DRY_BULK).length == 0 && p.seaShares(null).length == 0 && p.seaShares(Ports.Cargo.DRY_BULK) != p.seaShares(Ports.Cargo.DRY_BULK));
        // A route's forks.
        List<BoatSchedule.Route> routes = c.routes;
        BoatSchedule sch = BoatSchedule.of(7, tradeOf(DRY_TONNES), routes, Ports.BULK_TERMINAL_SHIPS, p);
        boolean exact = true, distinct = true, sorted = true;
        int total = 0;
        for (int r = 0; r < routes.size(); r++) {
            int[] want = expectedForks(routes.get(r), s, p), got = sch.forksOf(r);
            exact &= Arrays.equals(want, got);
            for (int i = 0; i < got.length; i++) {
                distinct &= i == 0 || got[i] != got[i - 1];
                sorted &= i == 0 || got[i] > got[i - 1];
                distinct &= got[i] % SeaRoutes.BEARING_STEP == 0 && routes.get(r).exitRun(got[i]) > 0;
            }
            total += got.length;
        }
        check("a route's forks are exactly the exits whose nations' shares reach FORK_SHARE (dry bulk: nations 1, 2, 4, 5 at .50, .25, .15, .10 - not 3 - and 1 and 5 share a bearing), by a scan of all 72, ascending",
                exact && distinct && sorted && sch.forksOf(0).length > 0 && sch.forksOf(0).length < 4 && dryShares[3] >= BoatSchedule.FORK_SHARE);
        int east = 90;
        check("...a route whose east the island closes forks on the open exit nearest it, not on east; the route on the open sea forks on it",
                Arrays.stream(sch.forksOf(0)).noneMatch(b -> b == east) && Arrays.stream(sch.forksOf(2)).anyMatch(b -> b == east) && !Arrays.equals(sch.forksOf(0), sch.forksOf(2)));
        check("...a joined route has its lane's forks, and a straight lane none, nor a route that does not exist", Arrays.equals(sch.forksOf(0), sch.forksOf(1)) && sch.forksOf(3).length == 0
                && sch.forksOf(-1).length == 0 && sch.forksOf(routes.size()).length == 0);
        BoatSchedule none = BoatSchedule.of(7, tradeOf(DRY_TONNES), routes), noExits = BoatSchedule.of(7, tradeOf(DRY_TONNES), withoutExits(routes), Ports.BULK_TERMINAL_SHIPS, p);
        double[] lat = NODE_LAT.clone();
        Arrays.fill(lat, Double.NaN);
        BoatSchedule blind = BoatSchedule.of(7, tradeOf(DRY_TONNES), routes, Ports.BULK_TERMINAL_SHIPS, CallPartners.of(s, 0, 0, lat, NODE_LON, 1));
        long huge = 1_000_000_000L;
        boolean nothing = true;
        for (int r = 0; r < routes.size(); r++) nothing &= none.forksOf(r).length == 0 && noExits.forksOf(r).length == 0 && blind.forksOf(r).length == 0;
        check("with NONE, with routes that have no exits, and with partners that have no bearing there are no forks at all, and none is drawn",
                nothing && none.forks(-huge, -huge, huge, huge).length == 0 && noExits.forks(-huge, -huge, huge, huge).length == 0 && blind.forks(-huge, -huge, huge, huge).length == 0);
        // The legs on a rectangle: each offing's once.
        double[] everywhere = sch.forks(-huge, -huge, huge, huge);
        Set<List<Double>> want = expectedLegs(routes, s, p), got = legsOf(everywhere);
        check("the forks on a rectangle that covers the map are the legs of the forks of each offing, each once: " + got.size() + " legs for the routes' " + total + " forks",
                got.equals(want) && everywhere.length / 4 == got.size() && got.size() > 0 && got.size() < total);
        BoatSchedule.Route r0 = routes.get(0), r2 = routes.get(2);
        int sharedForks = sch.forksOf(0).length;
        long ox0 = Math.round(r0.xs()[r0.offing()]), oy0 = Math.round(r0.ys()[r0.offing()]);
        long dist = Math.round(SeaRoutes.ABYSS_M / World.PLOT_M) + 2;
        double[] near0 = sch.forks(ox0 - dist, oy0 - dist, ox0 + dist, oy0 + dist);
        int atOffing = 0;
        for (int i = 0; i < near0.length; i += 4) if (near0[i] == r0.xs()[r0.offing()] && near0[i + 1] == r0.ys()[r0.offing()]) atOffing++;
        check("two routes that share an offing draw its forks once: the joined pair's " + sharedForks + " forks are " + atOffing + " legs, not " + 2 * sharedForks + ", on a rectangle round it",
                atOffing == sharedForks && near0.length / 4 == sharedForks && legsOf(near0).size() == sharedForks);
        long ox2 = Math.round(r2.xs()[r2.offing()]), oy2 = Math.round(r2.ys()[r2.offing()]);
        double[] near2 = sch.forks(ox2 - dist, oy2 - dist, ox2 + dist, oy2 + dist);
        boolean only2 = near2.length / 4 == sch.forksOf(2).length;
        for (int i = 0; i < near2.length; i += 4) only2 &= near2[i] == r2.xs()[r2.offing()] && near2[i + 1] == r2.ys()[r2.offing()];
        check("...a rectangle round another offing has that offing's forks alone", only2 && near2.length > 0);
        // A rectangle only a fork's far end is in draws that fork (the offing is outside it), and one nowhere near an offing draws none.
        double ex = near2[2], ey = near2[3];
        long ex0 = (long) Math.floor(ex) - 1, ey0 = (long) Math.floor(ey) - 1;
        double[] tip = sch.forks(ex0, ey0, ex0 + 3, ey0 + 3);
        boolean tipOnly = legsOf(tip).contains(List.of(near2[0], near2[1], ex, ey)) && !(near2[0] >= ex0 && near2[0] < ex0 + 3 && near2[1] >= ey0 && near2[1] < ey0 + 3);
        check("...one only a fork's far end is in draws that fork, its offing outside it; one far from every offing draws none",
                tipOnly && sch.forks(ox2 + 100_000, oy2 + 100_000, ox2 + 100_100, oy2 + 100_100).length == 0 && sch.forks(ox2 - 100_100, oy2 - 100_100, ox2 - 100_000, oy2 - 100_000).length == 0);
        // An offing two kinds of cargo share draws the union of their forks.
        BoatSchedule.Berth quay = new BoatSchedule.Berth(Ports.Cargo.LIQUID, r2.berth().x(), r2.berth().y());
        BoatSchedule.Route tanker = new BoatSchedule.Route(quay, r2.x1(), r2.y1(), r2.xs(), r2.ys(), r2.along(), r2.offing(), r2.pivot(), r2.spread(), r2.exits());
        BoatSchedule both = BoatSchedule.of(7, tradeOf(DRY_TONNES), List.of(r2, tanker), Ports.BULK_TERMINAL_SHIPS, p);
        java.util.TreeSet<Integer> union = new java.util.TreeSet<>();
        for (int e : both.forksOf(0)) union.add(e);
        for (int e : both.forksOf(1)) union.add(e);
        double[] bothLegs = both.forks(ox2 - dist, oy2 - dist, ox2 + dist, oy2 + dist);
        check("an offing that a dry bulk berth's lane and a tanker berth's share forks both their partners' exits, once each: the tanker's (nation 3 and, in the direction it has none, every kind's imports) and the bulker's",
                !Arrays.equals(both.forksOf(0), both.forksOf(1)) && bothLegs.length / 4 == union.size() && union.size() > both.forksOf(0).length && union.size() > both.forksOf(1).length
                        && legsOf(bothLegs).equals(expectedLegs(List.of(r2, tanker), s, p)));
        // The share rule, on an open sea (all 72 exits open), nations standing on given bearings with given weights of dry bulk sold to them.
        long huge2 = huge;
        BoatSchedule.Route open = c.plainFound.routes().get(0);
        // An exit under FORK_SHARE has no fork, one at it has; the shares of two nations on one exit add (nations C: 120, D and E: 180 together).
        double[] bearings = { 0, 60, 120, 180, 180, 240 }, weights = { 700, 50, 40, 30, 30, 150 };
        double total6 = sum(weights);
        boolean fixtureOk = weights[1] / total6 >= BoatSchedule.FORK_SHARE && weights[2] / total6 < BoatSchedule.FORK_SHARE
                && weights[3] / total6 < BoatSchedule.FORK_SHARE && (weights[3] + weights[4]) / total6 >= BoatSchedule.FORK_SHARE;
        CallPartners six = ringPartners(bearings, weights, dry);
        BoatSchedule bs = BoatSchedule.of(7, tradeOf(DRY_TONNES), List.of(open), Ports.BULK_TERMINAL_SHIPS, six);
        check("fixture: six nations on 0, 60, 120, 180, 180 and 240 degrees with 700, 50, 40, 30, 30 and 150 t of dry bulk: shares .70, .05 (FORK_SHARE), .04, two of .03, .15", fixtureOk
                && Arrays.equals(bs.forksOf(0), expectedForks(open, trade(6, 7, new Row(dry, weights, new double[6])).snapshot(), six)));
        check("an exit under FORK_SHARE has no fork (120, at .04), one at it has (60, at .05), and two nations on one exit add (180: .03 and .03 make .06)",
                Arrays.equals(bs.forksOf(0), new int[] { 0, 60, 180, 240 }));
        // A boat bound for the small partner still leaves on its exit: the call's exit is 120, and no fork is drawn there.
        boolean smallLeaves = false, others = true;
        for (int month = 0; month < 60; month++) {
            for (BoatSchedule.Call k : BoatSchedule.of(month, tradeOf(DRY_TONNES), List.of(open), Ports.BULK_TERMINAL_SHIPS, six).calls()) {
                if (k.cargo() != Ports.Cargo.DRY_BULK || k.inbound()) continue;
                if (k.partner() == 3) smallLeaves |= k.exit() == 120;
                others &= k.exit() == oracleExit(open.exits(), six.bearing(k.partner()));
            }
        }
        check("...though a boat bound for the small partner still leaves the offing on its own exit (120), off the drawn forks, as every partner's does", smallLeaves && others);
        // More exits over the share than MAX_FORKS keep the largest, the smaller bearing on a tie.
        int m = BoatSchedule.MAX_FORKS + 4;
        double[] descBearings = new double[m], desc = new double[m];
        for (int k = 0; k < m; k++) {
            descBearings[k] = k * SeaRoutes.BEARING_STEP;
            desc[k] = 100 + 4 * (m - k);
        }
        CallPartners many = ringPartners(descBearings, desc, dry);
        int[] wantLargest = new int[BoatSchedule.MAX_FORKS];
        for (int k = 0; k < wantLargest.length; k++) wantLargest[k] = k * SeaRoutes.BEARING_STEP;
        BoatSchedule bm = BoatSchedule.of(7, tradeOf(DRY_TONNES), List.of(open), Ports.BULK_TERMINAL_SHIPS, many);
        check("with " + m + " exits over FORK_SHARE (the smallest at " + String.format("%.3f", desc[m - 1] / sum(desc)) + ") only the MAX_FORKS largest are forks: the nations with the most tonnes, at bearings 0 to "
                        + (BoatSchedule.MAX_FORKS - 1) * SeaRoutes.BEARING_STEP,
                desc[m - 1] / sum(desc) >= BoatSchedule.FORK_SHARE && m > BoatSchedule.MAX_FORKS && Arrays.equals(bm.forksOf(0), wantLargest));
        int ties = BoatSchedule.MAX_FORKS + 2;
        double[] tieBearings = new double[ties], equal = new double[ties];
        for (int k = 0; k < ties; k++) {
            tieBearings[k] = 100 + (ties - 1 - k) * SeaRoutes.BEARING_STEP;      // the higher the nation, the smaller its bearing
            equal[k] = 100;
        }
        BoatSchedule bt = BoatSchedule.of(7, tradeOf(DRY_TONNES), List.of(open), Ports.BULK_TERMINAL_SHIPS, ringPartners(tieBearings, equal, dry));
        int[] wantSmallest = new int[BoatSchedule.MAX_FORKS];
        for (int k = 0; k < wantSmallest.length; k++) wantSmallest[k] = 100 + k * SeaRoutes.BEARING_STEP;
        check("...and where shares tie (" + ties + " nations at " + String.format("%.3f", 1.0 / ties) + " each) the MAX_FORKS smaller bearings are kept, whatever the nations' order",
                1.0 / ties >= BoatSchedule.FORK_SHARE && Arrays.equals(bt.forksOf(0), wantSmallest));
        // An offing shared by two kinds draws the union of their forks, each kind's capped already: no cut of the union.
        Good tank = liq;
        double[] bothBearings = new double[2 * BoatSchedule.MAX_FORKS], bothDry = new double[bothBearings.length], bothLiq = new double[bothBearings.length];
        for (int k = 0; k < bothBearings.length; k++) {
            bothBearings[k] = k * SeaRoutes.BEARING_STEP;
            (k < BoatSchedule.MAX_FORKS ? bothDry : bothLiq)[k] = 100;
        }
        double[][] ring2 = ringNodes(bothBearings);
        PartnerTrade twoKinds = trade(bothBearings.length, 7, new Row(dry, bothDry, new double[bothBearings.length]), new Row(tank, bothLiq, new double[bothBearings.length]));
        CallPartners pk = CallPartners.of(twoKinds.snapshot(), 0, 0, ring2[0], ring2[1], 1);
        BoatSchedule.Route tanker2 = new BoatSchedule.Route(new BoatSchedule.Berth(Ports.Cargo.LIQUID, open.berth().x(), open.berth().y()), open.x1(), open.y1(),
                open.xs(), open.ys(), open.along(), open.offing(), open.pivot(), open.spread(), open.exits());
        BoatSchedule kinds2 = BoatSchedule.of(7, tradeOf(DRY_TONNES), List.of(open, tanker2), Ports.BULK_TERMINAL_SHIPS, pk);
        double[] union2 = kinds2.forks(-huge2, -huge2, huge2, huge2);
        check("an offing shared by a dry bulk lane and a tanker lane draws both kinds' forks, " + union2.length / 4 + " of them: MAX_FORKS each, none cut from the union",
                kinds2.forksOf(0).length == BoatSchedule.MAX_FORKS && kinds2.forksOf(1).length == BoatSchedule.MAX_FORKS && union2.length / 4 == 2 * BoatSchedule.MAX_FORKS
                        && legsOf(union2).size() == 2 * BoatSchedule.MAX_FORKS);
        forksOnTheEarth();
        forksAtTenBillion();
    }

    /**
     * Printed, not asserted: how many forks a route draws on the open sea (all 72 exits open, the most there can be) from thirteen ports, were the
     * Earth's nations trading dry bulk with the city in proportion to their GDP in 2020 (the world file's gdp_usd) divided by their distance in km
     * (a gravity month), and by GDP alone. Needs the world file named (-Dcbs.world); without it a line says so.
     */
    static void forksOnTheEarth() {
        java.nio.file.Path file = WorldData.locate();
        if (file == null) {
            out.println("      (no world file named: the forks on the Earth are not printed)");
            return;
        }
        WorldData data = null;
        try {
            data = WorldData.open(file);
            NationsMap nm = NationsMap.of(data);
            int nations = nm.count();
            double[][] ports = { { 51.95, 4.14 }, { 1.26, 103.8 }, { 40.66, -74.04 }, { 31.2, 121.5 }, { 33.74, -118.27 }, { -33.86, 151.2 }, { -23.96, -46.3 },
                    { -29.87, 31.03 }, { 25.0, 55.1 }, { 35.0, 139.7 }, { 64.1, -21.9 }, { -41.3, 174.8 }, { 10.0, -83.0 } };
            double[] nodeLat = new double[nations], nodeLon = new double[nations], gdp = new double[nations];
            for (int i = 0; i < nations; i++) {
                nodeLat[i] = nm.nodeLat(i + 1);
                nodeLon[i] = nm.nodeLon(i + 1);
                double g = data.pathValue("gdp_usd", i + 1, 2020);
                gdp[i] = Double.isFinite(g) && g > 0 ? g : 0;
            }
            Good dry = goodOf(Ports.Cargo.DRY_BULK, 0);
            BoatSchedule.Route open = coast().plainFound.routes().get(0);
            StringBuilder line = new StringBuilder();
            int most = 0, mostAlone = 0;
            for (double[] port : ports) {
                double[] gravity = new double[nations];
                for (int i = 0; i < nations; i++) gravity[i] = gdp[i] / Math.max(100, EarthProj.distanceM(port[0], port[1], nodeLat[i], nodeLon[i]) / 1000);
                int[] counts = new int[2];
                double[][] weights = { gravity, gdp };
                for (int w = 0; w < 2; w++) {
                    PartnerTrade.Snapshot snap = trade(nations, 7, new Row(dry, weights[w], new double[nations])).snapshot();
                    counts[w] = BoatSchedule.of(7, tradeOf(DRY_TONNES), List.of(open), Ports.BULK_TERMINAL_SHIPS, CallPartners.of(snap, port[0], port[1], nodeLat, nodeLon, 0)).forksOf(0).length;
                }
                most = Math.max(most, counts[0]);
                mostAlone = Math.max(mostAlone, counts[1]);
                line.append(String.format(" %d/%d", counts[0], counts[1]));
            }
            out.println("      forks a dry bulk route draws on the open sea from 13 ports (Rotterdam, Singapore, New York, Shanghai, Los Angeles, Sydney, Santos, Durban, Dubai, Tokyo,"
                    + " Reykjavik, Wellington, Limon), a gravity month / GDP alone:" + line + " (at most " + most + " / " + mostAlone + ", MAX_FORKS " + BoatSchedule.MAX_FORKS + ")");
        } catch (java.io.IOException | RuntimeException e) {
            out.println("      (the world file would not open: " + e + ")");
        } finally {
            if (data != null) try { data.close(); } catch (java.io.IOException ignored) { }
        }
    }

    /** The forks of an offing a frame draws, on the 10-billion lanes sharing 200 offings (a late city's lanes share, SL1): built once, then a frame's forks in no more than BOAT_FRAME_MS. */
    static void forksAtTenBillion() {
        if (Timing.skip("PartnerCallsCheck.forksAtTenBillion")) return;
        long sx = World.SIDE / 2, sy = World.SIDE / 2;
        double[] exits = new double[SeaRoutes.EXITS];
        for (int k = 0; k < exits.length; k++) exits[k] = k >= 12 && k <= 24 ? 0 : SeaRoutes.ABYSS_M / World.PLOT_M;
        long span = (long) (Math.sqrt(1e10 / 5000.0) * 33), step = Math.max(1, span / 200);
        List<BoatSchedule.Route> lanes = new ArrayList<>();
        for (BoatSchedule.Route r : MapCheck.protoLanes(sx, sy)) {
            long qx = Math.floorDiv(r.x1(), step) * step;
            lanes.add(new BoatSchedule.Route(r.berth(), qx, r.y1()).withExits(exits));
        }
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
        BoatSchedule sch = BoatSchedule.of(7, MapCheck.tradeAtSea(1, 1, 0, 0), lanes, Ports.BULK_TERMINAL_SHIPS, partners);
        long b0 = System.nanoTime();
        sch.forks(sx, sy, sx + 1, sy + 1);
        double buildMs = (System.nanoTime() - b0) / 1e6;
        // The views are centred on an offing: the forks are out at the lanes' ends, not along them.
        long midX = lanes.get(0).x1(), midY = lanes.get(0).y1();
        long[][] views = {
                { Math.round(MapCheck.SCREEN_W_PX / MapFrame.L0_FROM), Math.round(MapCheck.SCREEN_H_PX / MapFrame.L0_FROM) },
                { Math.round(MapCheck.SCREEN_W_PX / MapFrame.L1_FROM), Math.round(MapCheck.SCREEN_H_PX / MapFrame.L1_FROM) },
                { 8L * World.DISTRICT, Math.abs(MapCheck.PROTO_LANE_DY) + 2 } };
        double[] best = new double[views.length];
        int[] legs = new int[views.length];
        Arrays.fill(best, Double.MAX_VALUE);
        for (int round = 0; round < MapCheck.WARM_ROUNDS + MapCheck.TIMED_ROUNDS; round++) {
            for (int v = 0; v < views.length; v++) {
                long ax = midX - views[v][0] / 2, ay = midY - views[v][1] / 2, bx = ax + views[v][0], by = ay + views[v][1];
                long f0 = System.nanoTime();
                int count = 0;
                for (int k = 0; k < MapCheck.BOAT_FRAMES; k++) count += sch.forks(ax + k % 3, ay, bx, by).length / 4;
                double ms = (System.nanoTime() - f0) / 1e6 / MapCheck.BOAT_FRAMES;
                if (round >= MapCheck.WARM_ROUNDS) best[v] = Math.min(best[v], ms);
                legs[v] = count / MapCheck.BOAT_FRAMES;
            }
        }
        out.printf("      %,d lanes on 200 offings over 196 nations: the offings made once in %.1f ms; a frame's forks take %.3f ms on an L0 screen (%,d legs), %.3f on an L1 one (%,d), %.3f on 8 districts (%,d)%n",
                lanes.size(), buildMs, best[0], legs[0], best[1], legs[1], best[2], legs[2]);
        check("fixture: forks on every view", legs[0] > 0 && legs[1] > 0 && legs[2] > 0);
        check("a frame's forks take no more than BOAT_FRAME_MS (0.5 ms): an L0 screen, an L1 one and 8 districts",
                Timing.within(best[0], MapCheck.BOAT_FRAME_MS) && Timing.within(best[1], MapCheck.BOAT_FRAME_MS) && Timing.within(best[2], MapCheck.BOAT_FRAME_MS));
    }
}
