package ham.citybuildersim;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A highway net never stops while the city has ground, and a net is laid on every piece of ground the water parts (CL5).
 *
 * WHAT THIS HAS TO PROVE (the sections below are numbered to match):
 *
 *   1. A STUCK NET OPENS ITS NEXT CORRIDOR BY A LINK ARM. A ground whose four
 *      spine arms stop at its edges short of APART, no line crossing the net,
 *      and more of the city's dry ground a line away that a link reaches: with
 *      CityRuns.linkArms off the highways stall (highwayShort() > 0: the
 *      premise, the fixture causes it); with it on a LINK corridor opens, its
 *      link leaves the net at its start and stands on its line at its end, the
 *      corridor's two line arms open there, every plot is on owned dry ground,
 *      and the model's plots are all laid.
 *
 *   2. TWO ISLANDS EACH WITH BUILDINGS GET A NET EACH, NONE ACROSS THE SEA.
 *      Two dry squares parted by sea (SALT, which no arm crosses) and a Pieces
 *      stub of two pieces, weights 3:1: each has highway plots, the split is
 *      the shares (floor(T * weight / total) for the far piece, the home
 *      piece the rest; a piece may be a plot or two under its share where a
 *      45-degree step would lay two), no highway plot is on salt or unowned
 *      ground, highwayShort() is 0. With ONE_PIECE on the same ground the far
 *      island has none (the premise: today's fault). A piece that cannot hold
 *      its share is made up by the other (the deal's D).
 *
 *   3. GROWTH AND TRIM ARE NEWEST-END, PIECE BY PIECE (H4). The plots grown in
 *      steps and taken back in steps: each month's arms are the next month's
 *      (nothing laid moves) while growing and the reverse while trimming; the
 *      far piece's hub plot is the last of its piece to go.
 *
 *   4. A PURCHASE JOINING TWO PIECES MOVES NOTHING. Section 2's ground, then a
 *      land bridge between the islands and the stub reporting one piece: the
 *      month it joins moves nothing; as the plots grow every stretch laid is
 *      still there, the younger net frozen, the home net grown on.
 *
 *   5. THE SIDECAR KEEPS EVERY PIECE'S NET. Written and read at the current
 *      FORMAT it is same() and fills a box alike, with its pieces' hubs,
 *      corridors' pieces and roles and piece counts; a net of one piece
 *      written at FORMAT 11 and read back is same() as it was.
 *
 *   6. NOTHING ELSE MOVED. On a ground where the rule never sticks (a large
 *      square) layTo() with ONE_PIECE gives the same stretches() and
 *      signature() with the link arms on and off.
 *
 *   7. THE MAP FINDS ITS PIECES AND EVERY BUILT PIECE HAS A HIGHWAY. A real
 *      CityMap (CityMap.canonical()) on a land of three islands of 2 x 2
 *      districts, dry in the default world and a district of unowned ground
 *      apart, buildings dealt over them: its MapPieces finds three pieces, the
 *      home one (the founding site's) 0 and the others by their first tile;
 *      their weights are the plots the buildings stand on, worked again from
 *      the districts; their hubs the tile nearest the site; the ground is
 *      read once and a month never reads it again. Month by month the runs
 *      lay a net on each (highway plots on every island, none in the gaps or
 *      on salt, fresh water or unowned ground, every stretch a prefix of the
 *      next month's; with ONE_PIECE the other islands have none: the fault).
 *      A purchase between two islands is an update of the pieces, not
 *      another flood, joins them and moves nothing; the younger net is
 *      frozen. On MapCheck's 5-billion copy the first flood and a purchase's
 *      update are timed, the update against MapCheck.RECONCILE_MS.
 *
 * WHY THIS EXISTS: Jerus's Bergen (1.7M) laid 603 of its 7,602 highway plots
 * and drew highways on 3 of its 35 built districts: the spine arms stopped at
 * its fjords before APART, so no corridor line crossed the net and none
 * opened; and an arm crosses no sea, so the island a city built on had no
 * highway at all (study spec-city-layout 5.6). A fixture CAUSES each case: a
 * ground to stall on, two islands, a purchase.
 */
public class IslandNetCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-110s %s%n", label, ok ? "ok" : "FAIL");
    }

    /** The founding site: a highway line crossing, so that the hub is the site itself. */
    static final long SITE = 128L * CityRuns.CELL + CityRuns.HIGHWAY_AT;

    /** A ground: dry (World.GRASS) where one of its pieces of land says so, salt (World.SALT) where it says sea inside its box, unowned (-1) elsewhere. */
    static final class Land implements CityRuns.Ground {
        interface Dry { boolean at(long x, long y); }

        final long x0, y0, x1, y1;
        final boolean seaAround;
        final List<Dry> lands = new ArrayList<>();
        long version = 1;

        Land(long x0, long y0, long x1, long y1, boolean seaAround) {
            this.x0 = x0; this.y0 = y0; this.x1 = x1; this.y1 = y1;
            this.seaAround = seaAround;
        }

        Land add(Dry d) {
            lands.add(d);
            return this;
        }

        /** Ground owned in the plots x0..x1 by y0..y1 (inclusive). */
        static Dry rect(long x0, long y0, long x1, long y1) {
            return (x, y) -> x >= x0 && x <= x1 && y >= y0 && y <= y1;
        }

        @Override public int at(long x, long y, int dir) {
            for (Dry d : lands) if (d.at(x, y)) return World.GRASS;
            if (seaAround && x >= x0 && x <= x1 && y >= y0 && y <= y1) return World.SALT;
            return -1;
        }

        @Override public long version() {
            return version;
        }

        @Override public long[] box() {
            return new long[] { x0, y0, x1, y1 };
        }
    }

    /** A Pieces stub: the city's ground in the pieces a test says. */
    static final class Isles implements CityRuns.Pieces {
        int count;
        final long[] weights;
        final long[][] hubs;
        final List<Land.Dry> pieces = new ArrayList<>();
        /** After a purchase joined them: every owned dry plot is piece 0. */
        boolean joined;

        Isles(long[] weights, long[][] hubs, Land.Dry... pieces) {
            this.count = weights.length;
            this.weights = weights;
            this.hubs = hubs;
            for (Land.Dry d : pieces) this.pieces.add(d);
        }

        @Override public int count() { return joined ? 1 : count; }

        @Override public int pieceOf(long x, long y) {
            for (int p = 0; p < pieces.size(); p++) if (pieces.get(p).at(x, y)) return joined ? 0 : p;
            return -1;
        }

        @Override public long weight(int p) { return p < weights.length ? weights[p] : 0; }

        @Override public long[] hub(int p) { return p < hubs.length ? hubs[p] : null; }
    }

    /** The highway plots the net covers in the box x0..x1 by y0..y1, counted plot by plot. */
    static long coveredIn(CityRuns.Net n, long x0, long y0, long x1, long y1) {
        long c = 0;
        for (long y = y0; y <= y1; y++) for (long x = x0; x <= x1; x++) if (CityRuns.covered(n, x, y)) c++;
        return c;
    }

    /** The highway plots the net covers inside the box that are not owned dry ground. */
    static long offGround(CityRuns.Net n, Land g) {
        long c = 0;
        for (long y = g.y0; y <= g.y1; y++) {
            for (long x = g.x0; x <= g.x1; x++) if (CityRuns.covered(n, x, y) && !CityRuns.dry(g.at(x, y, -1))) c++;
        }
        return c;
    }

    /** A fresh runs for a city with no gateway (the spine's order of hw.py's), laid to `plots` highways with the pieces given. */
    static CityRuns lay(Land g, long plots, CityRuns.Pieces pieces) {
        CityRuns runs = new CityRuns();
        runs.setGateway(CityRuns.NO_GATEWAY);
        again(runs, g, plots, pieces);
        return runs;
    }

    /** ...laid again to `plots`. */
    static void again(CityRuns runs, Land g, long plots, CityRuns.Pieces pieces) {
        runs.layTo(plots, 0, new ArrayList<>(), 0, g, SITE, SITE, SITE, SITE, new ArrayList<>(), new ArrayList<>(), pieces);
    }

    /** The net's arms by start and heading, each as its stretches {x0, y0, heading, steps}: a key can stand for the arm that has no stretches. */
    static Map<String, List<long[]>> armsOf(CityRuns.Net n) {
        Map<String, List<long[]>> out = new HashMap<>();
        Map<String, Integer> seen = new HashMap<>();
        for (CityRuns.Arm a : n.arms) {
            String base = a.sx + "," + a.sy + "," + a.home;
            int k = seen.merge(base, 1, Integer::sum);
            List<long[]> st = new ArrayList<>();
            for (int s = 0; s < a.segs; s++) st.add(new long[] { a.segX[s], a.segY[s], a.segHead[s], a.segSteps[s] });
            out.put(base + "#" + k, st);
        }
        return out;
    }

    /** Whether every arm of `small` is a prefix of the same arm of `big`: all its stretches the same but the last, which only grows. */
    static boolean prefix(Map<String, List<long[]>> small, Map<String, List<long[]>> big) {
        for (Map.Entry<String, List<long[]>> e : small.entrySet()) {
            List<long[]> s = e.getValue();
            if (s.isEmpty()) continue;
            List<long[]> b = big.get(e.getKey());
            if (b == null || b.size() < s.size()) return false;
            for (int i = 0; i < s.size(); i++) {
                long[] p = s.get(i), q = b.get(i);
                if (p[0] != q[0] || p[1] != q[1] || p[2] != q[2]) return false;
                if (i < s.size() - 1 ? p[3] != q[3] : p[3] > q[3]) return false;
            }
        }
        return true;
    }

    /** The first corridor of a role, or -1. */
    static int corridorOf(CityRuns.Net n, int role, boolean reaching) {
        for (int ci = 0; ci < n.corridors.size(); ci++) {
            if (CityRuns.corridorRole(n.corridors.get(ci)) != role) continue;
            if (!reaching || CityRuns.linkReaches(n, CityRuns.linkArm(n, ci))) return ci;
        }
        return -1;
    }

    /** How many corridors of a role the net has. */
    static int corridorsOf(CityRuns.Net n, int role) {
        int c = 0;
        for (long[] k : n.corridors) if (CityRuns.corridorRole(k) == role) c++;
        return c;
    }

    public static void main(String[] args) {
        CityRuns.linkArms = true;
        int cell = CityRuns.CELL;

        /* ============ 1. a stuck net opens its next corridor by a link arm ============ */
        System.out.println("--- a stuck net opens its next corridor by a link arm ---");
        // S1: an owned dry square of 100 plots each way round the hub, everything else unowned (the city's edge). S2: dry ground against its east
        // edge whose rows end one above the hub's row, 150 rows high: the east arm's row ends at S1's edge, and the 45 degrees beyond it run 150
        // plots, short of JUNCTION_COST; every arm waits at S1's edge, none reaches APART, no line crosses the net. A link from the hub's
        // neighbour on the row above runs east along S2 to the column APART out.
        long half = 100, s2Rows = 150, s2Reach = 700;
        Land stuck = new Land(SITE - half, SITE - s2Rows, SITE + s2Reach, SITE + half, false)
                .add(Land.rect(SITE - half, SITE - half, SITE + half, SITE + half))
                .add(Land.rect(SITE + half - 10, SITE - s2Rows, SITE + s2Reach, SITE - 1));
        long target = 1100;
        CityRuns.linkArms = false;
        CityRuns off = lay(stuck, target, CityRuns.ONE_PIECE);
        CityRuns.linkArms = true;
        long armsOnly = off.highways.plots;
        check("fixture: without link arms the highways stall short of the model's plots (premise)", off.highwayShort() > 0);
        check("fixture: ...with no corridor but the spine's two, and every arm shorter than APART", off.highways.corridors() == 2 && off.highways.plots < 4L * CityRuns.APART);
        check("fixture: ...and no line crosses the net: newCorridor() finds none", !CityRuns.newCorridor(off.highways, stuck));
        CityRuns on = lay(stuck, target, CityRuns.ONE_PIECE);
        CityRuns.Net hw = on.highways;
        System.out.printf("  target %,d plots: highwayShort %,d without link arms (%,d laid), %,d with (%,d laid, %d corridors, %d of them links)%n",
                target, off.highwayShort(), armsOnly, on.highwayShort(), hw.plots, hw.corridors(), corridorsOf(hw, CityRuns.LINK));
        check("a LINK corridor opens", corridorsOf(hw, CityRuns.LINK) >= 1);
        check("the model's highway plots are all laid: highwayShort() is 0", on.highwayShort() == 0 && hw.plots == target);
        int ci = corridorOf(hw, CityRuns.LINK, true);
        check("a link stands on its line", ci >= 0);
        if (ci >= 0) {
            int li = CityRuns.linkArm(hw, ci);
            CityRuns.Arm link = hw.arms.get(li);
            long[] c = hw.corridors.get(ci);
            boolean startOnNet = CityRuns.covered(hw, link.sx, link.sy);
            boolean endOnLine = (c[0] == 0 ? link.y : link.x) == c[1];
            check("...it leaves the net at its start (a plot of the net) and stands on the line at its end", startOnNet && endOnLine && link.steps > 0);
            boolean atStart = false, atEnd = false;
            for (CityRuns.Interchange ic : hw.interchanges) {
                if (ic.corridor() != ci) continue;
                atStart |= ic.x() == link.sx && ic.y() == link.sy;
                atEnd |= ic.x() == link.x && ic.y() == link.y;
            }
            check("...with an interchange at each", atStart && atEnd);
            int lines = 0;
            for (CityRuns.Arm a : hw.arms) if (a.corridor == ci && a != link && a.sx == link.x && a.sy == link.y) lines++;
            check("...and the corridor's two line arms open where it meets the line", lines == 2);
            boolean along = true;
            for (CityRuns.Arm a : hw.arms) {
                if (a.corridor != ci || a == link) continue;
                for (int s = 0; s < a.segs; s++) along &= c[0] == 0 ? (CityRuns.DY[a.segHead[s]] == 0 && a.segY[s] == c[1]) : (CityRuns.DX[a.segHead[s]] == 0 && a.segX[s] == c[1]);
            }
            check("...laid along the line", along);
            check("the link grew by the arms' own rules: every step moved it a plot nearer its line", linkNearer(link, c));
        }
        check("every plot is on owned dry ground", offGround(hw, stuck) == 0);
        check("plots counted are the plots covered", hw.plots == CityRuns.countPlots(hw));

        /* ============ 2. two islands each with buildings get a net each, none across the sea ============ */
        System.out.println("\n--- two islands each with buildings get a net each, none across the sea ---");
        long isle = 150, gap = 300;
        long ax = SITE, ay = SITE, bx0 = SITE + isle + gap, bx1 = bx0 + 2 * isle;
        // The far island's hub: the first highway line crossing in it, 14 plots or so from its west edge.
        long hubBx = bx0 + 10;
        while (Math.floorMod(hubBx - CityRuns.HIGHWAY_AT, cell) != 0) hubBx++;
        long[] hubB = { hubBx, ay };
        Land.Dry isleA = Land.rect(ax - isle, ay - isle, ax + isle, ay + isle), isleB = Land.rect(bx0, ay - isle, bx1, ay + isle);
        Land sea = new Land(ax - isle - 50, ay - isle - 50, bx1 + 50, ay + isle + 50, true).add(isleA).add(isleB);
        Isles isles = new Isles(new long[] { 3, 1 }, new long[][] { null, hubB }, isleA, isleB);
        long total = 600;
        CityRuns two = lay(sea, total, isles);
        CityRuns.Net h2 = two.highways;
        long onA = coveredIn(h2, ax - isle, ay - isle, ax + isle, ay + isle), onB = coveredIn(h2, bx0, ay - isle, bx1, ay + isle);
        long shareB = total * 1 / 4, shareA = total - shareB;
        System.out.printf("  %,d plots on two islands, weights 3:1: shares %,d and %,d, laid %,d and %,d (pieces count %,d and %,d), %d pieces, highwayShort %,d%n",
                total, shareA, shareB, onA, onB, h2.piecePlots(0), h2.piecePlots(1), h2.pieces(), two.highwayShort());
        check("the far island has a net of its own: a second piece", h2.pieces() == 2 && h2.hub() != null && h2.pieceHub(1) != null && h2.pieceHub(1)[0] == hubB[0] && h2.pieceHub(1)[1] == hubB[1]);
        check("each island has highway plots", onA > 0 && onB > 0);
        check("...its hub plot laid, drawn with the rest", CityRuns.covered(h2, hubB[0], hubB[1]) && CityRuns.covered(h2, h2.hubX, h2.hubY));
        check("the plots are dealt by the pieces' weights: the far piece floor(T * 1 / 4), the home piece the rest (within 2 plots)",
                Math.abs(onB - shareB) <= 2 && Math.abs(onA - shareA) <= 2);
        check("...as the pieces count them", h2.piecePlots(0) == onA && h2.piecePlots(1) == onB);
        check("no highway plot is on salt or unowned ground", offGround(h2, sea) == 0);
        check("none is across the sea", coveredIn(h2, ax + isle + 1, ay - isle - 50, bx0 - 1, ay + isle + 50) == 0);
        check("highwayShort() is 0: the islands hold the model's plots", two.highwayShort() == 0 && h2.plots == total);
        check("the far piece's spine is its own: corridors 2 and 3 are SPINE corridors of piece 1",
                h2.corridors.size() >= 4 && CityRuns.corridorRole(h2.corridors.get(2)) == CityRuns.SPINE && CityRuns.corridorPiece(h2.corridors.get(2)) == 1
                        && CityRuns.corridorRole(h2.corridors.get(3)) == CityRuns.SPINE && CityRuns.corridorPiece(h2.corridors.get(3)) == 1);
        // The home island's whole ground, with the link arms: what it holds at most.
        CityRuns full = lay(sea, 1_000_000, CityRuns.ONE_PIECE);
        long capA = full.highways.plots;
        long big = capA * 4 / 3 + 100;
        CityRuns one = lay(sea, big, CityRuns.ONE_PIECE);
        long oneA = coveredIn(one.highways, ax - isle, ay - isle, ax + isle, ay + isle), oneB = coveredIn(one.highways, bx0, ay - isle, bx1, ay + isle);
        System.out.printf("  %,d plots with ONE_PIECE: %,d on the home island (its ground holds %,d), %,d on the far one, highwayShort %,d%n", big, oneA, capA, oneB, one.highwayShort());
        check("premise: with ONE_PIECE the far island has none and the plots the home island cannot hold are never laid (today's fault)", oneB == 0 && one.highwayShort() > 0);
        // A piece that cannot hold its share is made up by the other: the home piece's share, three quarters of `big`, is more than its island holds.
        CityRuns made = lay(sea, big, isles);
        long madeA = coveredIn(made.highways, ax - isle, ay - isle, ax + isle, ay + isle), madeB = coveredIn(made.highways, bx0, ay - isle, bx1, ay + isle);
        System.out.printf("  %,d plots on two islands, weights 3:1: the home piece's share is %,d: laid %,d on the home island, %,d on the far one, highwayShort %,d%n",
                big, big - big / 4, madeA, madeB, made.highwayShort());
        check("fixture: the home piece's share is more than its island holds", big - big / 4 > capA);
        check("the home piece, stopped by its ground short of its share, is made up by the far piece: all the plots laid", madeA <= capA && madeB > big / 4 && made.highwayShort() == 0 && madeA + madeB == big);

        /* ============ 3. growth and trim are newest-end, piece by piece (H4) ============ */
        System.out.println("\n--- growth and trim are newest-end, piece by piece ---");
        CityRuns grown = new CityRuns();
        grown.setGateway(CityRuns.NO_GATEWAY);
        Map<String, List<long[]>> last = armsOf(grown.highways);
        boolean growing = true, trimming = true, hubLast = true, hubFirst = true;
        List<Long> steps = new ArrayList<>();
        for (long t = 50; t <= 900; t += 50) steps.add(t);
        for (long t : steps) {
            again(grown, sea, t, isles);
            Map<String, List<long[]>> now = armsOf(grown.highways);
            growing &= prefix(last, now);
            last = now;
            hubFirst &= grown.highways.piecePlots(1) == 0 || CityRuns.covered(grown.highways, hubB[0], hubB[1]);
        }
        check("growing 50 plots a month to 900: every arm of a month is a prefix of the next month's (nothing laid moves)", growing);
        check("...the far piece's hub is laid as soon as it has a plot", hubFirst);
        check("...and the model's plots are all laid", grown.highwayShort() == 0 && grown.highways.plots == 900);
        long farBefore = grown.highways.piecePlots(1);
        for (int k = steps.size() - 2; k >= -1; k--) {
            long t = k >= 0 ? steps.get(k) : 0;
            Map<String, List<long[]>> before = armsOf(grown.highways);
            again(grown, sea, t, isles);
            Map<String, List<long[]>> now = armsOf(grown.highways);
            trimming &= prefix(now, before);
            boolean hubIn = CityRuns.covered(grown.highways, hubB[0], hubB[1]);
            hubLast &= grown.highways.piecePlots(1) > 0 ? hubIn : !hubIn;
        }
        check("taken back from 900 to nothing in steps: every arm of a month is a prefix of the month before's", trimming);
        check("...the far piece's hub plot is the last of its piece to go: laid while the piece has a plot, gone with the last", hubLast);
        check("...nothing is left of the far piece but its corridors, and the net is reset with the last plot", farBefore > 0 && grown.highways.plots == 0 && !grown.highways.started);

        /* ============ 4. a purchase joining two pieces moves nothing ============ */
        System.out.println("\n--- a purchase joining two pieces moves nothing ---");
        CityRuns bought = lay(sea, total, isles);
        Map<String, List<long[]>> beforeBuy = armsOf(bought.highways);
        long homeBefore = bought.highways.piecePlots(0), farHeld = bought.highways.piecePlots(1);
        long sigBefore = bought.signature();
        sea.add(Land.rect(ax + isle - 10, ay - 20, bx0 + 10, ay + 20));
        sea.version++;
        isles.joined = true;
        again(bought, sea, total, isles);
        check("the month a purchase joins them, with the same plots: nothing moves (the runs' signature is the same)", bought.signature() == sigBefore);
        again(bought, sea, total + 200, isles);
        Map<String, List<long[]>> afterBuy = armsOf(bought.highways);
        check("as the plots grow, every arm laid before is a prefix of the arm after", prefix(beforeBuy, afterBuy));
        check("the younger net is frozen: its plots are what they were", bought.highways.piecePlots(1) == farHeld);
        boolean farSame = true;
        for (Map.Entry<String, List<long[]>> e : beforeBuy.entrySet()) {
            List<long[]> was = e.getValue(), is = afterBuy.get(e.getKey());
            String[] key = e.getKey().split(",");
            boolean far = Long.parseLong(key[0]) >= hubB[0] - 1 && Long.parseLong(key[0]) <= bx1;
            if (far && (is == null || is.size() != was.size() || !prefix(Map.of("k", was), Map.of("k", is)) || !prefix(Map.of("k", is), Map.of("k", was)))) farSame = false;
        }
        check("...every arm of it exactly as it was", farSame);
        check("the home net grows on: past what it held, to the plots the model has less the frozen net's", bought.highways.piecePlots(0) > homeBefore && bought.highways.plots == total + 200 && bought.highwayShort() == 0);
        System.out.printf("  home piece %,d plots before the purchase, %,d after (plots %,d + 200); the far piece's %,d held%n", homeBefore, bought.highways.piecePlots(0), total, farHeld);

        /* ============ 5. the sidecar keeps every piece's net ============ */
        System.out.println("\n--- the sidecar keeps every piece's net ---");
        isles.joined = false;
        Land sea2 = new Land(sea.x0, sea.y0, sea.x1, sea.y1, true).add(isleA).add(isleB);
        CityRuns kept = lay(sea2, 900, isles);
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            kept.write(new DataOutputStream(bytes), CityMap.FORMAT);
            CityRuns back = CityRuns.read(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())), CityMap.FORMAT);
            if (back != null) back.setGateway(kept.gateway());   // the gateway is the map's to keep, not the runs'
            check("written and read at FORMAT " + CityMap.FORMAT + ", the runs are the same (hubs, corridors' pieces and roles, arms)", back != null && kept.same(back));
            if (back != null) {
                check("...with the pieces' hubs", back.highways.pieces() == 2 && java.util.Arrays.equals(back.highways.pieceHub(1), kept.highways.pieceHub(1)));
                boolean roles = back.highways.corridors.size() == kept.highways.corridors.size();
                for (int i = 0; roles && i < kept.highways.corridors.size(); i++) roles = CityRuns.corridorRole(back.highways.corridors.get(i)) == CityRuns.corridorRole(kept.highways.corridors.get(i)) && CityRuns.corridorPiece(back.highways.corridors.get(i)) == CityRuns.corridorPiece(kept.highways.corridors.get(i));
                check("...the corridors' roles and pieces, with a link among them", roles && corridorsOf(kept.highways, CityRuns.LINK) >= 1);
                check("...each piece's plots counted afresh the same", back.highways.piecePlots(0) == kept.highways.piecePlots(0) && back.highways.piecePlots(1) == kept.highways.piecePlots(1));
                int w = (int) (sea2.x1 - sea2.x0 + 1), h = (int) (sea2.y1 - sea2.y0 + 1);
                byte[] f1 = new byte[w * h], f2 = new byte[w * h], m1 = new byte[w * h], m2 = new byte[w * h];
                kept.fill(sea2.x0, sea2.y0, w, h, f1, m1, false);
                back.fill(sea2.x0, sea2.y0, w, h, f2, m2, false);
                check("...and they fill a box alike", java.util.Arrays.equals(f1, f2) && java.util.Arrays.equals(m1, m2) && kept.highwaysReach());
            }
            // A net of one piece, written at FORMAT 11 (no pieces, no roles) and read back, is as it was.
            Land flat = new Land(SITE - 400, SITE - 400, SITE + 400, SITE + 400, false).add(Land.rect(SITE - 400, SITE - 400, SITE + 400, SITE + 400));
            CityRuns single = lay(flat, 3000, CityRuns.ONE_PIECE);
            ByteArrayOutputStream old = new ByteArrayOutputStream();
            single.write(new DataOutputStream(old), 11);
            CityRuns older = CityRuns.read(new DataInputStream(new ByteArrayInputStream(old.toByteArray())), 11);
            if (older != null) older.setGateway(single.gateway());
            check("a net of one piece written at FORMAT 11 and read back is the same (its corridors a spine and crossings, one piece)", older != null && single.same(older)
                    && older.highways.pieces() == 1 && older.highways.corridors() > 2 && single.highways.corridors() > 2);
            ByteArrayOutputStream cur = new ByteArrayOutputStream();
            single.write(new DataOutputStream(cur), CityMap.FORMAT);
            CityRuns now = CityRuns.read(new DataInputStream(new ByteArrayInputStream(cur.toByteArray())), CityMap.FORMAT);
            if (now != null) now.setGateway(single.gateway());
            check("...and at FORMAT " + CityMap.FORMAT + " too", now != null && single.same(now));
        } catch (java.io.IOException e) {
            check("the sidecar writes and reads (" + e + ")", false);
        }

        /* ============ 6. nothing else moved ============ */
        System.out.println("\n--- nothing else moved ---");
        Land square = new Land(SITE - 400, SITE - 400, SITE + 400, SITE + 400, false).add(Land.rect(SITE - 400, SITE - 400, SITE + 400, SITE + 400));
        CityRuns.linkArms = true;
        CityRuns withLinks = lay(square, 3000, CityRuns.ONE_PIECE);
        CityRuns.linkArms = false;
        CityRuns without = lay(square, 3000, CityRuns.ONE_PIECE);
        CityRuns.linkArms = true;
        boolean sameStretches = withLinks.highways.stretches().size() == without.highways.stretches().size();
        for (int i = 0; sameStretches && i < withLinks.highways.stretches().size(); i++) sameStretches = java.util.Arrays.equals(withLinks.highways.stretches().get(i), without.highways.stretches().get(i));
        System.out.printf("  a square 801 plots each way, %,d plots: %d corridors, %d stretches, %d links%n", withLinks.highways.plots, withLinks.highways.corridors(), withLinks.highways.stretches().size(), corridorsOf(withLinks.highways, CityRuns.LINK));
        check("fixture: the rule never sticks here: crossings open corridors and no link does", withLinks.highways.corridors() > 2 && corridorsOf(withLinks.highways, CityRuns.LINK) == 0 && withLinks.highwayShort() == 0);
        check("layTo() with ONE_PIECE gives the same stretches() with link arms on and off", sameStretches);
        check("...the same signature()", withLinks.signature() == without.signature());
        // A grown and trimmed one, as before CL5: the same on and off.
        CityRuns.linkArms = true;
        CityRuns a1 = lay(square, 3000, CityRuns.ONE_PIECE);
        again(a1, square, 1200, CityRuns.ONE_PIECE);
        CityRuns.linkArms = false;
        CityRuns a2 = lay(square, 3000, CityRuns.ONE_PIECE);
        again(a2, square, 1200, CityRuns.ONE_PIECE);
        CityRuns.linkArms = true;
        check("...and after the plots are taken back to 1,200", a1.signature() == a2.signature() && a1.highways.plots == 1200 && a1.same(a2));

        /* ============ 7. the map finds its pieces and every built piece has a highway ============ */
        System.out.println("\n--- the map finds its pieces and every built piece has a highway ---");
        mapPieces();

        System.out.println(fails == 0 ? "\nThe islands have their highways." : "\n" + fails + " FAILED");
        System.exit(fails);
    }

    /** The highway line crossing of tile (tx, ty). */
    static long[] crossingOf(long tx, long ty) {
        return new long[] { tx * World.TILE + CityRuns.HIGHWAY_AT, ty * World.TILE + CityRuns.HIGHWAY_AT };
    }

    /** Whether the district (dx, dy) of the world is dry at every plot of a stride of 8 (the plots 8 and 24 into each tile among them: the tile rule's samples). */
    static boolean dryDistrict(World world, long dx, long dy, Map<Long, Boolean> seen) {
        Long key = (dx << 32) ^ (dy & 0xffffffffL);
        Boolean known = seen.get(key);
        if (known != null) return known;
        boolean dry = true;
        for (int j = 0; j < CityMap.DISTRICT && dry; j += 8) {
            for (int i = 0; i < CityMap.DISTRICT && dry; i += 8) dry = World.dry(world.terrainAt(dx * CityMap.DISTRICT + i, dy * CityMap.DISTRICT + j));
        }
        seen.put(key, dry);
        return dry;
    }

    /** The north-west district {column, row} of a strip of w x h dry districts nearest the district of `place`, or null. */
    static long[] dryStrip(World world, long[] place, int w, int h) {
        long bx = Math.floorDiv(place[0], CityMap.DISTRICT), by = Math.floorDiv(place[1], CityMap.DISTRICT);
        Map<Long, Boolean> seen = new HashMap<>();
        for (int ring = 0; ring <= 40; ring++) {
            for (long dy = -ring; dy <= ring; dy++) {
                for (long dx = -ring; dx <= ring; dx++) {
                    if (Math.max(Math.abs(dx), Math.abs(dy)) != ring) continue;
                    boolean all = true;
                    for (int j = 0; j < h && all; j++) for (int i = 0; i < w && all; i++) all = dryDistrict(world, bx + dx + i, by + dy + j, seen);
                    if (all) return new long[] { bx + dx, by + dy };
                }
            }
        }
        return null;
    }

    /** The highway plots the net covers on an island {x0, y0, x1, y1} (exclusive upper ends), counted plot by plot. */
    static long coveredOn(CityRuns.Net n, long[] box) {
        return coveredIn(n, box[0], box[1], box[2] - 1, box[3] - 1);
    }

    /** Section 7: a CityMap on a land of three islands, the pieces it finds, the nets the runs lay on them, a purchase that joins two, and what the pieces cost. */
    static void mapPieces() {
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        long seed = Founding.DEFAULT_WORLD_SEED;
        World world = World.of(seed);
        long[] place = MapCheck.dryPlace(world);
        int dist = CityMap.DISTRICT;
        long[] strip = dryStrip(world, place, 8, 2);
        check("fixture: the default world has a strip of 8 x 2 districts, dry at every sample, near its dry place", strip != null);
        if (strip == null) return;
        long bx = strip[0], by = strip[1];
        // Three islands of 2 x 2 districts a district of unowned ground apart: the west one, the middle one (the founding site's) and the east one.
        long[][] isle = {
            { bx * dist, by * dist, (bx + 2) * dist, (by + 2) * dist },
            { (bx + 3) * dist, by * dist, (bx + 5) * dist, (by + 2) * dist },
            { (bx + 6) * dist, by * dist, (bx + 8) * dist, (by + 2) * dist } };
        long sx = (bx + 4) * dist + CityRuns.HIGHWAY_AT, sy = (by + 1) * dist + CityRuns.HIGHWAY_AT;
        double[] centre = new double[CityLand.CENTRE_FIELDS];
        centre[CityLand.CENTRE_FIELDS - 5] = -1;
        centre[CityLand.CENTRE_FIELDS - 4] = -1;
        centre[CityLand.CENTRE_FIELDS - 2] = sx;
        centre[CityLand.CENTRE_FIELDS - 1] = sy;
        double[][] rects = new double[3][];
        for (int i = 0; i < 3; i++) rects[i] = new double[] { isle[i][0], isle[i][1], isle[i][2], isle[i][3] };
        CityLand land = CityLand.restore(World.Key.generated(seed), centre, rects, null, null, null);
        int hwType = GatewayCheck.highwayType(types);
        long[] counts = new long[types.length];
        for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < counts.length; t++) {
            if (types[t] == null || types[t].site() != null || types[t].track() || types[t].sea() || types[t].road() != BuildingVisual.NOT_A_ROAD) continue;
            counts[t] = Math.round(MapCheck.JERUS_COUNTS[t] * 5);
        }
        int cellsPer = BuildingVisual.cells(types[hwType]);
        counts[hwType] = 50;
        CityMap map = CityMap.canonical(land, r -> land.amountsInOrder(r), types, counts);
        CityMap.MapPieces mp = map.pieces();
        long buildings = 0;
        for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].drawn()) buildings += counts[t];
        System.out.printf("  three islands of 2 x 2 districts at district (%d, %d), a district of unowned ground between; %,d buildings, %d districts; the founding site (%d, %d)%n",
                bx, by, buildings, map.districts().size(), sx, sy);
        Map<String, List<long[]>> last = armsOf(map.runs().highways);
        boolean growing = true;
        int month = 1;
        for (; month <= 7; month++) {
            counts[hwType] += 50;
            map.reconcile(counts, month);
            Map<String, List<long[]>> now = armsOf(map.runs().highways);
            growing &= prefix(last, now);
            last = now;
        }
        CityRuns runs = map.runs();
        CityRuns.Net hw = runs.highways;
        long model = counts[hwType] * cellsPer;

        // The pieces.
        check("the map finds three pieces of ground", mp.count() == 3);
        check("the home piece, 0, is the middle island's, the founding site's; the others follow by their first tile, west then east",
                mp.pieceOf(sx, sy) == 0 && mp.pieceOf(isle[0][0] + 100, isle[0][1] + 100) == 1 && mp.pieceOf(isle[2][0] + 100, isle[2][1] + 100) == 2);
        check("a plot in the unowned ground between two islands, or beyond them, is on no piece",
                mp.pieceOf(isle[0][2] + 100, isle[0][1] + 100) == -1 && mp.pieceOf(isle[2][2] + 100, isle[2][1] + 100) == -1 && mp.pieceOf(sx, isle[0][1] - 100) == -1);
        int tilesAcross = 2 * dist / World.TILE;
        check("each piece holds its island's tiles: 16 x 16 of them", mp.tiles(0) == (long) tilesAcross * tilesAcross && mp.tiles(1) == mp.tiles(0) && mp.tiles(2) == mp.tiles(0) && mp.landTiles() == 3 * mp.tiles(0));
        check("the ground was read once (a flood) and not again by the " + (month - 1) + " months since", mp.floods == 1 && mp.updates == 0);
        // The weights, worked again from the districts' counts.
        long[] expect = new long[3];
        for (CityMap.District d : map.districts()) {
            long x0 = (map.baseDX() + d.dx) * dist, y0 = (map.baseDY() + d.dy) * dist;
            int island = -1;
            for (int i = 0; i < 3; i++) if (x0 >= isle[i][0] && x0 < isle[i][2] && y0 >= isle[i][1] && y0 < isle[i][3]) island = i;
            if (island < 0) continue;
            for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].drawn()) expect[island] += d.count(t) * (long) BuildingVisual.cells(types[t]);
        }
        // Island i is piece 1, 0, 2.
        int[] pieceOfIsle = { 1, 0, 2 };
        boolean weights = true;
        for (int i = 0; i < 3; i++) weights &= mp.weight(pieceOfIsle[i]) == expect[i] && expect[i] > 0;
        System.out.printf("  built ground by piece: %,d (home), %,d, %,d plots (the buildings' cells, not the roads', highways' or track's)%n", mp.weight(0), mp.weight(1), mp.weight(2));
        check("each piece's weight is the plots its buildings stand on, worked again from the districts: every island has some", weights);
        // The hubs: the tile nearest the founding site's, by brute force.
        boolean hubs = true;
        long stx = Math.floorDiv(sx, World.TILE), sty = Math.floorDiv(sy, World.TILE);
        for (int i = 0; i < 3; i++) {
            long best = Long.MAX_VALUE, bt = 0;
            for (long ty = isle[i][1] / World.TILE; ty < isle[i][3] / World.TILE; ty++) {
                for (long tx = isle[i][0] / World.TILE; tx < isle[i][2] / World.TILE; tx++) {
                    long k = (((tx - stx) * (tx - stx) + (ty - sty) * (ty - sty)) << 30) | (ty << 15) | tx;
                    if (k < best) { best = k; bt = (ty << 32) | tx; }
                }
            }
            long[] want = crossingOf(bt & 0xffffffffL, bt >>> 32), got = mp.hub(pieceOfIsle[i]);
            hubs &= got != null && got[0] == want[0] && got[1] == want[1] && mp.pieceOf(got[0], got[1]) == pieceOfIsle[i] && CityRuns.dry(map.runGround().at(got[0], got[1], -1));
        }
        check("each piece's hub is its tile nearest the founding site's (the northern, then the western, on a tie), its highway line crossing on dry ground", hubs);

        // The runs.
        System.out.printf("  %,d highway plots (the model's) over %d months: %d nets, laid %,d, short %,d%n", model, month - 1, hw.pieces(), hw.plots, runs.highwayShort());
        check("the runs have a net for each piece: three, each from its piece's hub", hw.pieces() == 3 && hw.hubX == sx && hw.hubY == sy
                && java.util.Arrays.equals(hw.pieceHub(1), mp.hub(1)) && java.util.Arrays.equals(hw.pieceHub(2), mp.hub(2)));
        long[] on = new long[3];
        for (int i = 0; i < 3; i++) on[i] = coveredOn(hw, isle[i]);
        System.out.printf("  highway plots on the west island %,d, the middle (home) %,d, the east %,d%n", on[0], on[1], on[2]);
        check("every island has highway plots", on[0] > 0 && on[1] > 0 && on[2] > 0);
        check("the model's plots are all laid: highwayShort() is 0", runs.highwayShort() == 0 && hw.plots == model && on[0] + on[1] + on[2] == model);
        long off = 0, gaps = 0;
        for (long y = isle[0][1]; y < isle[0][3]; y++) {
            for (long x = isle[0][0]; x < isle[2][2]; x++) {
                if (!CityRuns.covered(hw, x, y)) continue;
                int c = map.runGround().at(x, y, -1);
                if (c < 0 || !CityRuns.dry(c) || c == World.SALT || c == World.FRESH) off++;
                boolean inIsle = false;
                for (int i = 0; i < 3; i++) inIsle |= x >= isle[i][0] && x < isle[i][2];
                if (!inIsle) gaps++;
            }
        }
        check("no highway plot is on salt, fresh water or ground the city does not own, and none crosses between the islands", off == 0 && gaps == 0);
        check("growing a month at a time, every arm of a month is a prefix of the next (nothing laid moves)", growing);
        // The premise: the runs handed no pieces lay on the home island alone.
        CityRuns blind = new CityRuns();
        blind.setGateway(map.gateway());
        blind.layTo(model, 0, new ArrayList<>(), 0, map.runGround(), sx, sy, sx, sy, new ArrayList<>(), new ArrayList<>());
        check("premise: the same runs on the same ground with ONE_PIECE lay no highway on the other two islands (today's fault)",
                coveredOn(blind.highways, isle[0]) == 0 && coveredOn(blind.highways, isle[2]) == 0 && coveredOn(blind.highways, isle[1]) > 0);

        // A purchase between the west and the middle islands joins them.
        Map<String, List<long[]>> beforeBuy = armsOf(hw);
        long plotsBefore = hw.plots, westHeld = hw.piecePlots(1), eastBefore = hw.piecePlots(2);
        land.extend(new LandParcel(-5, 1, 0, land.level(), isle[0][2], isle[0][1], isle[1][0], isle[0][3], null, null, null, 0, 0), month, 0);
        map.reconcile(counts, month++);
        check("a purchase of the ground between the west and the middle islands: the map reads only its tiles (an update, not another flood)", mp.floods == 1 && mp.updates == 1);
        check("...they are one piece now, the home piece, and the east island is piece 1", mp.count() == 2 && mp.pieceOf(isle[0][0] + 100, isle[0][1] + 100) == 0 && mp.pieceOf(sx, sy) == 0
                && mp.pieceOf(isle[2][0] + 100, isle[2][1] + 100) == 1);
        check("...the month it joins them nothing moves: the plots and every stretch laid are where they were", hw.plots == plotsBefore && prefix(beforeBuy, armsOf(hw)) && prefix(armsOf(hw), beforeBuy));
        counts[hwType] += 100;
        map.reconcile(counts, month++);
        Map<String, List<long[]>> afterBuy = armsOf(hw);
        System.out.printf("  after the purchase and %d more plots: home net %,d, the west net %,d (held at %,d), the east net %,d (was %,d); laid %,d, short %,d%n",
                100 * cellsPer, hw.piecePlots(0), hw.piecePlots(1), westHeld, hw.piecePlots(2), eastBefore, hw.plots, runs.highwayShort());
        check("as the plots grow every stretch laid before is a prefix of the one after", prefix(beforeBuy, afterBuy));
        check("the west net, no longer its piece's owner, is frozen as it was; the model's plots are all laid", hw.piecePlots(1) == westHeld && runs.highwayShort() == 0 && hw.plots == counts[hwType] * cellsPer);

        timePieces(types, seed);
    }

    /** What the pieces cost on a city of billions (MapCheck section 5's copy, 5 billion people): the first flood and one purchase's update. */
    static void timePieces(BuildingVisual.Type[] types, long seed) {
        double k = MapCheck.TIMES[1];
        World w = World.of(seed);
        long[] place = MapCheck.dryPlace(w);
        long[] c = new long[types.length];
        for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < c.length; t++) c[t] = Math.round(MapCheck.JERUS_COUNTS[t] * k);
        for (BuildingVisual.Type t : types) if (t != null && t.site() != null && t.sites() > 0) c[t.id()] = 0;
        int side = (int) Math.ceil(Math.sqrt(MapCheck.groundKm2(types) * k / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
        int capacity = (int) Math.round(MapCheck.JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        CityMap m = CityMap.square(seed, place[0], place[1], types, side, capacity, c);
        CityMap.MapPieces mp = m.pieces();
        mp.count();
        double flood = mp.lastMs;
        long tiles = mp.landTiles();
        // One purchase: a district of ground beside the square's east edge, then the month.
        long[] box = m.runGround().box();
        long px = box[2] + 1, py = Math.floorDiv(box[1] + box[3], 2L * CityMap.DISTRICT) * CityMap.DISTRICT;
        m.land().extend(new LandParcel(-5, 1, 0, m.land().level(), px, py, px + CityMap.DISTRICT, py + CityMap.DISTRICT, null, null, null, 0, 0), 1, 0);
        m.reconcile(c, 1);
        double update = mp.lastMs;
        System.out.printf("  on the 5-billion-people copy (%d districts a side, %,d land tiles, %d pieces, %,d of them with buildings): the first flood %.0f ms (the terrain read, each tile's samples kept);"
                + " one purchase's update %.2f ms (floods %d, updates %d)%n", side, tiles, mp.count(), countWeighted(mp), flood, update, mp.floods, mp.updates);
        check("on the city of billions a purchase is an update of the map's pieces, not another flood", mp.floods == 1 && mp.updates == 1);
        check("...and the update takes no more than MapCheck's RECONCILE_MS (" + (int) MapCheck.RECONCILE_MS + " ms) of the month", Timing.within(update, MapCheck.RECONCILE_MS));
    }

    private static int countWeighted(CityMap.MapPieces mp) {
        int n = 0;
        for (int p = 0; p < mp.count(); p++) if (mp.weight(p) > 0) n++;
        return n;
    }

    /** Whether every step of the link moved it a plot nearer its line: its coordinate across the line's moved by one each step. */
    static boolean linkNearer(CityRuns.Arm link, long[] corridor) {
        long across = corridor[0] == 0 ? link.sy : link.sx;
        long end = corridor[0] == 0 ? link.y : link.x;
        long perStep = Math.abs(end - across);
        // Along the link's own heading each step moves one plot toward the line when the line is ahead: the distance covered is the steps along it.
        long sum = 0;
        for (int s = 0; s < link.segs; s++) {
            int h = link.segHead[s];
            int d = corridor[0] == 0 ? CityRuns.DY[h] : CityRuns.DX[h];
            if (Math.signum(d) != Math.signum(end - across) || d == 0) return false;
            sum += link.segSteps[s];
        }
        return sum == perStep;
    }
}
