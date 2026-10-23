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

        System.out.println(fails == 0 ? "\nThe islands have their highways." : "\n" + fails + " FAILED");
        System.exit(fails);
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
