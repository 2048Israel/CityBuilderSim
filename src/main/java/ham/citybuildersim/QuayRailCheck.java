package ham.citybuildersim;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The quay lines of the harbour, the spur the railway lays to each, and the railed terminals QuayRail reads from them (batch HB2, Jerus
 * 2026-11-09: "tracks on the quay, with the railway leaning toward a port so a line reaches those tracks").
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE QUAY LINES: CityShore.quayLines() groups the berthed works whose strips join into one wall: a bay of berths WALL_JOIN_GAP apart is
 *      one line of its works in shore order in each of the four sides, berths farther apart are a line each, the line's span and outer
 *      edge are the strip's, and the union of walls() plots is the lines' strips less every box (the walls did not move when they and the lines
 *      came from one grouping); the lines are cached with the walls.
 *
 *   2. THE ENDS: QuayRail.ends() gives two plots a work, BEHIND its land edge at its first and its last column along the shore, none of them
 *      blocked, one past the verge the shore keeps clear; an end a neighbour's verge covers is left out.
 *
 *   3. THE SPUR: with a main line within QUAY_REACH of a bay and the ends set, layTo() lays one quay spur, from the main line to one of the
 *      line's ends, over ground the shore leaves free, as short as any way from the line to any end; the model's railway is the one laid with
 *      no ends (sidings, branches, yards, the surplus), the railway has the spur's plots more, and the spur has no loading shed where a siding
 *      still has.
 *
 *   4. NO SPUR: none to a bay farther than QUAY_REACH, none behind ground the runs cannot take, and two bays in reach get a spur each that
 *      share no plot.
 *
 *   5. THE STAMP: laid again to the same ends nothing moves; other ends, or a berth laid, lay it again; ends cleared take it off and the
 *      railway is the one laid with none.
 *
 *   6. THE SIDECAR: the net with its spurs reads back as it was, its quaySpurs() the same, and laying it again to the same ends moves nothing.
 *
 *   7. THE RAILED TERMINALS: QuayRail.of() gives every work of a line a spur ends at the quay track, open to its neighbours, the lead at the
 *      box the spur stands behind; the plots between the boxes and behind the lead's box are its cells; a line no spur ends at has none.
 *
 * WHY THIS EXISTS: before HB2 no port had rail, and the map draws tracks on a terminal's quay only where the railway reaches it. A fixture
 * CAUSES each condition (PortCheck's bay of berths, the ground the map would give it), and every assertion is against the model's own
 * constants (WALL_JOIN_GAP, CLEAR, BEHIND, QUAY_REACH), never a bare number.
 */
public class QuayRailCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-110s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** The sides' names, by CityShore's number: the side the sea is on. */
    static final String[] SIDES = { "north", "east", "south", "west" };

    /** The pitch of a bay that joins into one wall: the berths' gap is WALL_JOIN_GAP, the widest that still reads as one. */
    static final int JOINED = PortCheck.BULK_ALONG + CityShore.WALL_JOIN_GAP;

    /** The pitch of a bay that does not: one plot past that. */
    static final int APART = JOINED + 1;

    /** A bay of `count` berths facing `side`, `pitch` plots apart along the shore, from (3000, 3000). */
    static CityShore bay(int count, int side, int pitch) {
        return PortCheck.berthRow(count, side, pitch);
    }

    /** The same works moved (dx, dy) plots. */
    static CityShore shifted(CityShore s, long dx, long dy) {
        CityShore out = new CityShore();
        for (CityShore.Work w : s.works()) out.add(new CityShore.Work(w.x0() + dx, w.y0() + dy, w.w(), w.h(), w.type(), w.side(), w.quay()));
        return out;
    }

    static long key(long x, long y) {
        return (x << 32) ^ (y & 0xffffffffL);
    }

    /** A works' box {x0, y0, x1, y1} (inclusive) from its north-west plot and its sides. */
    static long[] box(long x0, long y0, int w, int h) {
        return new long[] { x0, y0, x0 + w - 1, y0 + h - 1 };
    }

    /** The row or column of the railway's lattice at or before v: the plots the railway's lines run along. */
    static long lattice(long v) {
        return v - Math.floorMod(v - CityRuns.RAIL_AT, CityRuns.CELL);
    }

    /* ----- the fixtures ----- */

    /** How far out the fixture's sea reaches from a box's sea edge, in plots, and how far past its ends along the shore. */
    static final int SEA_DEEP = 40, SEA_WIDE = 5;

    /** The column the hub stands at, before the lattice takes it: the main line runs east from it past the bay. */
    static final long X_HUB = 2900;

    /** The model's track in the fixture, in plots: more than the main line to the city's edge and the siding use. */
    static final long TRACK = 3000;

    /**
     * The ground of a bay as the map gives it: owned dry ground in a box, -1 where the shore blocks (CityMap.RunGround.read: no run crosses a
     * terminal) or in a band of rows across the whole box, and the sea seaward of every work.
     */
    static final class BayGround implements CityRuns.Ground {
        final CityShore bay;
        final long x0, y0, x1, y1;
        long bandFrom = 1, bandTo = 0;

        BayGround(CityShore bay, long x0, long y0, long x1, long y1) {
            this.bay = bay;
            this.x0 = x0; this.y0 = y0; this.x1 = x1; this.y1 = y1;
        }

        @Override public int at(long x, long y, int dir) {
            if (x < x0 || x > x1 || y < y0 || y > y1) return -1;
            if (y >= bandFrom && y <= bandTo) return -1;
            if (!bay.isEmpty() && bay.blocks(x, y)) return -1;
            return seaward(x, y) ? World.SALT : World.GRASS;
        }

        /** Whether plot (x, y) is out to sea from a work: within SEA_DEEP of its sea edge and SEA_WIDE of its span along the shore. */
        boolean seaward(long x, long y) {
            for (CityShore.Work k : bay.works()) {
                long out, along, lo, hi;
                switch (k.side()) {
                    case 0:  out = k.y0() - y; along = x; lo = k.x0(); hi = k.x0() + k.w(); break;
                    case 1:  out = x - (k.x0() + k.w() - 1); along = y; lo = k.y0(); hi = k.y0() + k.h(); break;
                    case 2:  out = y - (k.y0() + k.h() - 1); along = x; lo = k.x0(); hi = k.x0() + k.w(); break;
                    default: out = k.x0() - x; along = y; lo = k.y0(); hi = k.y0() + k.h(); break;
                }
                if (out >= 1 && out <= SEA_DEEP && along >= lo - SEA_WIDE && along < hi + SEA_WIDE) return true;
            }
            return false;
        }

        @Override public long version() { return 1 + bay.version(); }

        @Override public long[] box() { return new long[] { x0, y0, x1, y1 }; }
    }

    /** A railway to lay over a bay: the ground, the hub, the model's track, its yards and Rail Spurs, and the bulk works. */
    static final class Rig {
        final BayGround g;
        final long hx, hy, track;
        final List<int[]> yards;
        final int spurs;
        final List<long[]> works;

        Rig(BayGround g, long hx, long hy, long track, List<int[]> yards, int spurs, List<long[]> works) {
            this.g = g; this.hx = hx; this.hy = hy; this.track = track; this.yards = yards; this.spurs = spurs; this.works = works;
        }

        /** The quay ends of the bay as the map hands them over. */
        List<long[]> ends() { return QuayRail.ends(g.bay); }

        /** Lays `runs` to the rig's counts, as CityMap.layRuns() does; whether anything moved. */
        boolean lay(CityRuns runs) {
            return runs.layTo(0, track, yards, spurs, g, hx, hy, hx, hy, works, works);
        }

        /** A new railway with a gateway east, the quay ends set (none left unset when null), laid to the rig's counts. */
        CityRuns fresh(List<long[]> ends) {
            CityRuns runs = new CityRuns();
            runs.setGateway(0);
            if (ends != null) runs.setQuayEnds(ends);
            lay(runs);
            return runs;
        }
    }

    /** A rig over `bay` with the main line along the lattice row `row`: a yard, a Rail Spur and a bulk works south of the line, a hundred plots from the hub's column on. */
    static Rig rig(CityShore bay, long row) {
        long hx = lattice(X_HUB);
        BayGround g = new BayGround(bay, hx - 100, 2850, hx + 450, 3400);
        return new Rig(g, hx, row, TRACK, List.of(new int[] { 8, 7 }), 1, List.of(box(hx + 350, row + 90, 16, 16)));
    }

    /** The lattice row to run the main line along, `gap` plots or more behind the ends of a bay on `side`: behind is south of a north-facing bay, north of a south-facing one, and past the last berth for the sides the shore runs north-south on. */
    static long rowBehind(List<long[]> ends, int side, int gap) {
        long lo = Long.MAX_VALUE, hi = Long.MIN_VALUE;
        for (long[] e : ends) {
            lo = Math.min(lo, e[2]);
            hi = Math.max(hi, e[2]);
        }
        return side == 2 ? lattice(lo - gap) : lattice(hi + gap);
    }

    /** The plots arm `ai` of net n lays after its start, {x, y} in order. */
    static List<long[]> plotsOf(CityRuns.Net n, int ai) {
        CityRuns.Arm a = n.arms.get(ai);
        List<long[]> out = new ArrayList<>();
        for (int s = 0; s < a.segs; s++) {
            int h = a.segHead[s];
            for (int k = 1; k <= a.segSteps[s]; k++) out.add(new long[] { a.segX[s] + (long) CityRuns.DX[h] * k, a.segY[s] + (long) CityRuns.DY[h] * k });
        }
        return out;
    }

    /** The marks a railway lays over the rig's whole ground (CityRuns.fill()). */
    static byte[] marksOf(CityRuns runs, BayGround g) {
        int w = (int) (g.x1 - g.x0 + 1), h = (int) (g.y1 - g.y0 + 1);
        byte[] fixed = new byte[w * h], marks = new byte[w * h];
        runs.fill(g.x0, g.y0, w, h, fixed, marks, false);
        return marks;
    }

    static int markAt(BayGround g, byte[] marks, long x, long y) {
        int w = (int) (g.x1 - g.x0 + 1);
        return marks[(int) ((y - g.y0) * w + (x - g.x0))] & 0xff;
    }

    static int sheds(byte[] marks) {
        int n = 0;
        for (byte b : marks) if ((b & TilePainter.M_SHED) != 0) n++;
        return n;
    }

    /** Whether plot (x, y) is one of the ends {line, x, y}. */
    static boolean isEnd(List<long[]> ends, long x, long y) {
        for (long[] e : ends) if (e[1] == x && e[2] == y) return true;
        return false;
    }

    /** The shortest way, in plots, from any plot of the plain railway to any of `ends`: scanned over the plots about each end, never by the model's candidates. */
    static long nearestWay(CityRuns plain, List<long[]> ends) {
        long best = Long.MAX_VALUE;
        for (long[] e : ends) {
            for (long y = e[2] - CityRuns.QUAY_REACH; y <= e[2] + CityRuns.QUAY_REACH; y++) {
                for (long x = e[1] - CityRuns.QUAY_REACH; x <= e[1] + CityRuns.QUAY_REACH; x++) {
                    long d = Math.abs(x - e[1]) + Math.abs(y - e[2]);
                    if (d < 1 || d >= best || !CityRuns.covered(plain.rail, x, y)) continue;
                    best = d;
                }
            }
        }
        return best;
    }

    public static void main(String[] args) {
        quayLines();
        theEnds();
        theSpur();
        noSpur();
        theStamp();
        theSidecar();
        railedTerminals();

        System.out.println();
        System.out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails);
    }

    /* ============ 1. the quay lines ============ */

    /** The plots of the walls of `s`. */
    static Set<Long> wallPlots(CityShore s) {
        Set<Long> out = new HashSet<>();
        for (CityShore.Wall w : s.walls()) for (long y = w.y0(); y < w.y0() + w.h(); y++) for (long x = w.x0(); x < w.x0() + w.w(); x++) out.add(key(x, y));
        return out;
    }

    /** The plots of the lines' strips of `s`, each from the line's span and outer edge, less every work's box. */
    static Set<Long> stripPlots(CityShore s) {
        Set<Long> out = new HashSet<>();
        int q = CityShore.HARBOUR_QUAY_PLOTS;
        for (CityShore.QuayLine l : s.quayLines()) {
            long x0, y0, x1, y1;
            switch (l.side()) {
                case 0:  x0 = l.from(); x1 = l.to(); y0 = l.line(); y1 = l.line() + q; break;
                case 1:  x0 = l.line() - q; x1 = l.line(); y0 = l.from(); y1 = l.to(); break;
                case 2:  x0 = l.from(); x1 = l.to(); y0 = l.line() - q; y1 = l.line(); break;
                default: x0 = l.line(); x1 = l.line() + q; y0 = l.from(); y1 = l.to(); break;
            }
            for (long y = y0; y < y1; y++) for (long x = x0; x < x1; x++) out.add(key(x, y));
        }
        for (CityShore.Work k : s.works()) for (long y = k.y0(); y < k.y0() + k.h(); y++) for (long x = k.x0(); x < k.x0() + k.w(); x++) out.remove(key(x, y));
        return out;
    }

    static void quayLines() {
        System.out.println("\n--- 1. quayLines(): the works whose strips join into one wall, from the one grouping the walls are made from ---");
        int q = CityShore.HARBOUR_QUAY_PLOTS;
        for (int side = 0; side < 4; side++) {
            String s = SIDES[side] + ": ";
            CityShore joined = bay(4, side, JOINED);
            List<CityShore.QuayLine> lines = joined.quayLines();
            check(s + "four berths JOINED (" + JOINED + ") plots apart, a gap of WALL_JOIN_GAP, are one line of four works in shore order",
                    lines.size() == 1 && lines.get(0).side() == side && lines.get(0).works().equals(joined.works()));
            CityShore.Work first = joined.works().get(0), last = joined.works().get(3);
            boolean along = side % 2 == 0;
            long from = along ? first.x0() : first.y0(), to = along ? last.x0() + last.w() : last.y0() + last.h();
            long edge;
            switch (side) {
                case 0:  edge = first.y0() - q; break;
                case 1:  edge = first.x0() + first.w() + q; break;
                case 2:  edge = first.y0() + first.h() + q; break;
                default: edge = first.x0() - q; break;
            }
            check("...its span runs from the first box's low edge to the last box's high edge, and its line is the strip's outer edge",
                    lines.size() == 1 && lines.get(0).from() == from && lines.get(0).to() == to && lines.get(0).line() == edge);
            CityShore reversed = new CityShore();
            for (int k = 3; k >= 0; k--) reversed.add(joined.works().get(k));
            check("...the works laid in the reverse order are the same line, in shore order",
                    reversed.quayLines().size() == 1 && reversed.quayLines().get(0).works().equals(joined.works()));
            CityShore apart = bay(3, side, APART);
            boolean each = apart.quayLines().size() == 3;
            for (CityShore.QuayLine l : apart.quayLines()) each &= l.works().size() == 1;
            check("...three berths APART (" + APART + ") plots apart, a gap past WALL_JOIN_GAP, are a line each", each);
            check("...the walls' plots are the lines' strips less the boxes, joined and apart: the walls did not move",
                    wallPlots(joined).equals(stripPlots(joined)) && !wallPlots(joined).isEmpty() && wallPlots(apart).equals(stripPlots(apart)) && apart.walls().size() == 3);
        }

        CityShore cut = bay(3, 0, JOINED);
        int wallsBefore = cut.walls().size();
        cut.add(new CityShore.Work(3006, 2996, 2, 4, 0, 1, 0));
        check("a box standing in the strip (a work with no quay) cuts the wall in pieces round it and not the line: still one line of three",
                cut.walls().size() > wallsBefore && cut.quayLines().size() == 1 && cut.quayLines().get(0).works().size() == 3
                        && wallPlots(cut).equals(stripPlots(cut)));

        CityShore c = bay(3, 0, JOINED);
        List<CityShore.QuayLine> kept = c.quayLines();
        boolean sameList = c.quayLines() == kept;
        c.add(PortCheck.bulkWork(3000 + 3L * JOINED, 3000, 0));
        check("the lines are cached with the walls (the same list while nothing moves) and made again when a work is laid",
                sameList && c.quayLines() != kept && c.quayLines().size() == 1 && c.quayLines().get(0).works().size() == 4);
        CityShore piers = new CityShore();
        piers.add(new CityShore.Work(3000, 3000, 5, 4, 0, 0, CityShore.QUAY_PLOTS[0]));
        check("a work on a pier of its own (a quay of QUAY_PLOTS, laid before MF1) is on no line", piers.quayLines().isEmpty() && piers.walls().isEmpty());
    }

    /* ============ 2. the ends ============ */

    /** The plot BEHIND work k's land edge at its first (or last) column, worked out from the box's plots: the one in that column farthest from the sea, BEHIND further on. */
    static long[] expectedEnd(CityShore.Work k, boolean high) {
        int s = k.side();
        long ax = CityShore.DX[s], ay = CityShore.DY[s];
        boolean alongX = s % 2 == 0;
        long col = alongX ? (high ? k.x0() + k.w() - 1 : k.x0()) : (high ? k.y0() + k.h() - 1 : k.y0());
        long bestX = 0, bestY = 0, bestAway = Long.MIN_VALUE;
        for (long y = k.y0(); y < k.y0() + k.h(); y++) {
            for (long x = k.x0(); x < k.x0() + k.w(); x++) {
                if ((alongX ? x : y) != col) continue;
                long away = -(ax * x + ay * y);
                if (away > bestAway) { bestAway = away; bestX = x; bestY = y; }
            }
        }
        return new long[] { bestX - ax * QuayRail.BEHIND, bestY - ay * QuayRail.BEHIND };
    }

    static void theEnds() {
        System.out.println("\n--- 2. QuayRail.ends(): two a work, BEHIND the land edge at its first and last column, none blocked ---");
        for (int side = 0; side < 4; side++) {
            String s = SIDES[side] + ": ";
            CityShore bay = bay(3, side, JOINED);
            List<long[]> ends = QuayRail.ends(bay);
            boolean same = ends.size() == 2 * bay.works().size();
            for (int k = 0; same && k < bay.works().size(); k++) {
                long[] lo = expectedEnd(bay.works().get(k), false), hi = expectedEnd(bay.works().get(k), true);
                same &= ends.get(2 * k)[0] == 0 && ends.get(2 * k)[1] == lo[0] && ends.get(2 * k)[2] == lo[1];
                same &= ends.get(2 * k + 1)[0] == 0 && ends.get(2 * k + 1)[1] == hi[0] && ends.get(2 * k + 1)[2] == hi[1];
            }
            check(s + "three berths give six ends {line, x, y}: each work's first and last column, BEHIND its land edge, low then high, in shore order", same);
            boolean free = true;
            for (long[] e : ends) free &= !bay.blocks(e[1], e[2]);
            check("...none of them blocked by the shore", free);
            CityShore lone = new CityShore();
            lone.add(PortCheck.bulkWork(3000, 3000, side));
            boolean verge = true;
            for (long[] e : QuayRail.ends(lone)) {
                verge &= !lone.blocks(e[1], e[2]) && lone.blocks(e[1] + CityShore.DX[side], e[2] + CityShore.DY[side]);
            }
            check("...one past the verge: the plot a work's verge keeps clear is blocked, the end itself is not", verge && QuayRail.ends(lone).size() == 2);
        }

        CityShore stacked = new CityShore();
        stacked.add(PortCheck.bulkWork(3000, 3000, 0));
        stacked.add(PortCheck.bulkWork(3000, 3000 + PortCheck.BULK_DEEP + CityShore.CLEAR + 1, 0));
        List<long[]> ends = QuayRail.ends(stacked);
        check("a berth built close behind another covers its ends with its verge: they are left out, the second's two stay (line 1)",
                stacked.quayLines().size() == 2 && ends.size() == 2 && ends.get(0)[0] == 1 && ends.get(1)[0] == 1);
        check("an empty shore has no ends", QuayRail.ends(new CityShore()).isEmpty());
    }

    /* ============ 3. the spur ============ */

    static void theSpur() {
        System.out.println("\n--- 3. the spur: one, from the main line to an end of the line, the shortest, and the model's railway untouched ---");
        for (int side = 0; side < 4; side++) {
            String s = SIDES[side] + ": ";
            CityShore bay = bay(3, side, JOINED);
            List<long[]> ends = QuayRail.ends(bay);
            Rig rig = rig(bay, rowBehind(ends, side, 40));
            CityRuns plain = rig.fresh(null), runs = rig.fresh(ends);
            check(s + "the fixture's hub stands where it was put, on the lattice row the main line runs along",
                    runs.rail.hubX == rig.hx && runs.rail.hubY == rig.hy && plain.rail.hubX == rig.hx && plain.rail.hubY == rig.hy);
            List<long[]> spurs = runs.quaySpurs();
            check("the railway laid with the ends has one quay spur, the one laid with none has none", spurs.size() == 1 && plain.quaySpurs().isEmpty());
            if (spurs.size() != 1) continue;
            long[] sp = spurs.get(0);
            int[] on = CityRuns.locate(runs.rail, sp[1], sp[2]);
            check("...it starts on the main line (a plot of the gate arm the plain railway has too)",
                    on != null && runs.rail.arms.get(on[0]).corridor <= 1 && CityRuns.covered(plain.rail, sp[1], sp[2]));
            check("...ends on one of the line's ends", isEnd(ends, sp[3], sp[4]));
            boolean free = true;
            for (long[] p : plotsOf(runs.rail, (int) sp[0])) free &= !bay.blocks(p[0], p[1]) && CityRuns.dry(rig.g.at(p[0], p[1], -1));
            check("...every plot of it is dry ground the shore leaves free", free);
            long least = nearestWay(plain, ends);
            System.out.println("  the spur runs " + sp[5] + " plots from (" + sp[1] + ", " + sp[2] + ") to (" + sp[3] + ", " + sp[4] + "); the nearest way from the line to an end is " + least);
            check("...its length is the least over the ends: the Manhattan distance to the nearest plot of the main line (open ground)",
                    sp[5] == least && sp[5] >= 1 && sp[5] <= CityRuns.QUAY_REACH && runs.quayPlots() == sp[5]);

            check("the model's railway is untouched: sidings, branches, yards and the surplus are those of the railway laid with no ends",
                    plain.sidingsLaid() == 1 && Arrays.deepEquals(plain.sidings().toArray(), runs.sidings().toArray()) && runs.sidingsLaid() == plain.sidingsLaid()
                            && Arrays.deepEquals(plain.branches().toArray(), runs.branches().toArray()) && Arrays.deepEquals(plain.yards().toArray(), runs.yards().toArray())
                            && plain.yards().size() == 1 && runs.trackSurplus() == plain.trackSurplus() && plain.trackSurplus() > 0);
            check("...the railway has the spur's plots more, exactly quayPlots(), and the surplus does not count them",
                    runs.rail().plots() == plain.rail().plots() + runs.quayPlots() && runs.trackSurplus() == TRACK - (runs.rail().plots() - runs.quayPlots()));
            byte[] pm = marksOf(plain, rig.g), qm = marksOf(runs, rig.g);
            long[] siding = runs.sidings().get(0);
            check("...the siding still ends in its loading shed (M_SHED), the only shed there is, and the spur's last plot has none",
                    sheds(pm) == 1 && sheds(qm) == 1 && (markAt(rig.g, qm, siding[3], siding[4]) & TilePainter.M_SHED) != 0
                            && (markAt(rig.g, qm, sp[3], sp[4]) & TilePainter.M_SHED) == 0);
        }
    }

    /* ============ 4. no spur ============ */

    /** Two bays back to back with the main line between them: a north-facing one, and (forty plots south) a south-facing one, so each one's land is the other's. */
    static Rig backToBack() {
        CityShore both = new CityShore();
        for (CityShore.Work w : bay(3, 0, JOINED).works()) both.add(w);
        for (CityShore.Work w : shifted(bay(3, 2, JOINED), 0, 40).works()) both.add(w);
        long row = lattice(3005 + 20);
        return rig(both, row);
    }

    static void noSpur() {
        System.out.println("\n--- 4. no spur: out of reach, behind ground no run can take, and two bays in reach get one each ---");
        CityShore near = bay(3, 0, JOINED);
        long farRow = lattice(3005 + CityRuns.QUAY_REACH + 40);
        Rig far = rig(near, farRow);
        List<long[]> ends = far.ends();
        long toLine = farRow - ends.get(0)[2];
        CityRuns plain = far.fresh(null), runs = far.fresh(ends);
        check("a bay farther from the main line than QUAY_REACH (" + toLine + " plots to " + CityRuns.QUAY_REACH + "): the line passes it, no spur",
                toLine > CityRuns.QUAY_REACH && runs.rail.hubY == farRow && runs.quaySpurs().isEmpty() && runs.quayPlots() == 0 && runs.same(plain));

        Rig walled = rig(bay(3, 0, JOINED), lattice(3005 + 40));
        walled.g.bandFrom = 3008;
        walled.g.bandTo = 3012;
        Rig open = rig(bay(3, 0, JOINED), lattice(3005 + 40));
        List<long[]> wends = walled.ends();
        CityRuns wplain = walled.fresh(null), wruns = walled.fresh(wends);
        check("the same bay behind a band of ground the runs cannot take, right across the city: no spur and the railway is the one with none",
                wruns.rail.hubY > walled.g.bandTo && wends.get(0)[2] < walled.g.bandFrom && wruns.quaySpurs().isEmpty() && wruns.same(wplain));
        check("...and with the band gone the same ends get their spur (the band is what stopped it)", open.fresh(open.ends()).quaySpurs().size() == 1);

        Rig two = backToBack();
        List<long[]> tends = two.ends();
        CityRuns tplain = two.fresh(null), truns = two.fresh(tends);
        List<long[]> spurs = truns.quaySpurs();
        check("two bays in reach on either side of the main line are two lines, and the line between them runs between the two rows of ends",
                two.g.bay.quayLines().size() == 2 && two.hy > 3005 && two.hy < tends.get(tends.size() - 1)[2] && truns.rail.hubY == two.hy);
        check("...they get a spur each, in the order of the lines", spurs.size() == 2 && tplain.quaySpurs().isEmpty());
        if (spurs.size() == 2) {
            boolean ownEnds = true;
            for (int i = 0; i < 2; i++) {
                List<long[]> mine = new ArrayList<>();
                for (long[] e : tends) if (e[0] == i) mine.add(e);
                ownEnds &= isEnd(mine, spurs.get(i)[3], spurs.get(i)[4]);
                int[] on = CityRuns.locate(truns.rail, spurs.get(i)[1], spurs.get(i)[2]);
                ownEnds &= on != null && truns.rail.arms.get(on[0]).corridor <= 1;
            }
            check("...each ends at an end of its own line and starts on the main line", ownEnds);
            Set<Long> first = new HashSet<>();
            boolean shares = false;
            for (long[] p : plotsOf(truns.rail, (int) spurs.get(0)[0])) first.add(key(p[0], p[1]));
            for (long[] p : plotsOf(truns.rail, (int) spurs.get(1)[0])) shares |= first.contains(key(p[0], p[1]));
            check("...and share no plot after the main line's (a spur laid is rail the next line's way sees)",
                    !shares && truns.quayPlots() == spurs.get(0)[5] + spurs.get(1)[5]
                            && truns.rail().plots() == tplain.rail().plots() + truns.quayPlots());
        }
    }

    /* ============ 5. the stamp ============ */

    static void theStamp() {
        System.out.println("\n--- 5. the stamp: the ends are what the spurs are laid for ---");
        CityShore bay = bay(3, 0, JOINED);
        List<long[]> ends = QuayRail.ends(bay);
        Rig rig = rig(bay, rowBehind(ends, 0, 40));
        CityRuns runs = rig.fresh(ends);
        CityRuns plainNone = rig.fresh(null);
        check("the fixture lays a spur", runs.quaySpurs().size() == 1 && isEnd(ends, runs.quaySpurs().get(0)[3], runs.quaySpurs().get(0)[4]));
        check("laid again to the same ends and counts, nothing moves", !rig.lay(runs));
        plainNone.setQuayEnds(List.of());
        check("a railway with no harbour is not moved by being told so: no ends set, then none, lays nothing again", !rig.lay(plainNone));

        runs.setQuayEnds(ends.subList(1, ends.size()));
        boolean moved = rig.lay(runs);
        check("the first end taken away (the ground the same): the spur is laid again, to the next end of the line",
                moved && runs.quaySpurs().size() == 1 && runs.quaySpurs().get(0)[3] == ends.get(1)[1] && runs.quaySpurs().get(0)[4] == ends.get(1)[2]);
        runs.setQuayEnds(ends);
        moved = rig.lay(runs);
        check("...given back, it returns to the first end: the net is the one laid at first", moved && runs.same(rig.fresh(ends)));

        bay.add(PortCheck.bulkWork(3000 - JOINED, 3000, 0));
        List<long[]> more = QuayRail.ends(bay);
        runs.setQuayEnds(more);
        moved = rig.lay(runs);
        long[] spur = runs.quaySpurs().isEmpty() ? null : runs.quaySpurs().get(0);
        check("a berth laid ahead of the bay (new ends: " + more.size() + " of the old " + ends.size() + "): laid again, to the new first end",
                more.size() == ends.size() + 2 && moved && runs.quaySpurs().size() == 1 && spur[3] == more.get(0)[1] && spur[4] == more.get(0)[2]
                        && (spur[3] != ends.get(0)[1] || spur[4] != ends.get(0)[2]));
        check("...and laid again to those, nothing moves", !rig.lay(runs));

        runs.setQuayEnds(List.of());
        moved = rig.lay(runs);
        CityRuns none = rig.fresh(null);
        check("the ends cleared: the spur is taken off, and the railway is the one laid with none",
                moved && runs.quaySpurs().isEmpty() && runs.quayPlots() == 0 && runs.same(none) && runs.rail().plots() == none.rail().plots()
                        && Arrays.deepEquals(runs.sidings().toArray(), none.sidings().toArray()) && runs.trackSurplus() == none.trackSurplus());
    }

    /* ============ 6. the sidecar ============ */

    static void theSidecar() {
        System.out.println("\n--- 6. the sidecar: the spurs read back as laid, and laying again moves nothing ---");
        Rig rig = backToBack();
        List<long[]> ends = rig.ends();
        CityRuns runs = rig.fresh(ends);
        check("the fixture lays two spurs", runs.quaySpurs().size() == 2);
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            runs.write(new DataOutputStream(bytes));
            CityRuns back = CityRuns.read(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())));
            check("the net with its quay spurs reads back (an arm of a corridor the net does not list is not a corrupt one)", back != null);
            if (back == null) return;
            back.setGateway(0);
            check("...equal to the net written, and its quaySpurs() the same", back.same(runs) && Arrays.deepEquals(back.quaySpurs().toArray(), runs.quaySpurs().toArray()));
            back.setQuayEnds(ends);
            boolean moved = rig.lay(back);
            check("...and laid again to the same ends and counts it moves nothing", !moved && back.same(runs) && Arrays.deepEquals(back.quaySpurs().toArray(), runs.quaySpurs().toArray()));
            back.setQuayEnds(List.of());
            rig.lay(back);
            check("...the ends cleared, the spurs come off the net read back as they do off the one laid", back.quaySpurs().isEmpty() && back.same(rig.fresh(null)));
        } catch (IOException e) {
            check("the sidecar round trip: " + e, false);
        }
    }

    /* ============ 7. the railed terminals ============ */

    /** The sea-most row (or column) of work k's box: the coordinate of its plots nearest the sea, across the shore. */
    static long seaRow(CityShore.Work k) {
        int s = k.side();
        long best = Long.MIN_VALUE, at = 0;
        for (long y = k.y0(); y < k.y0() + k.h(); y++) {
            for (long x = k.x0(); x < k.x0() + k.w(); x++) {
                long toSea = CityShore.DX[s] * x + CityShore.DY[s] * y;
                if (toSea > best) { best = toSea; at = s % 2 == 0 ? y : x; }
            }
        }
        return at;
    }

    /** The plots of a line's sea-most row that no box of the line holds, from the line's first plot along the shore to its last: where the quay track runs between the boxes. */
    static Set<Long> gapPlots(CityShore.QuayLine l) {
        Set<Long> out = new HashSet<>();
        long row = seaRow(l.works().get(0));
        for (long t = l.from(); t < l.to(); t++) {
            long x = l.side() % 2 == 0 ? t : row, y = l.side() % 2 == 0 ? row : t;
            boolean held = false;
            for (CityShore.Work k : l.works()) held |= k.holds(x, y);
            if (!held) out.add(key(x, y));
        }
        return out;
    }

    /** The plots strictly between plot a and plot b, which lie on one row or column. */
    static Set<Long> between(long ax, long ay, long bx, long by) {
        Set<Long> out = new HashSet<>();
        long dx = Long.signum(bx - ax), dy = Long.signum(by - ay);
        for (long x = ax + dx, y = ay + dy; x != bx || y != by; x += dx, y += dy) out.add(key(x, y));
        return out;
    }

    /** The plot of work k's box at its land edge in its first (or last) column along the shore: the one in that column farthest from the sea. */
    static long[] landEdge(CityShore.Work k, boolean high) {
        long[] behind = expectedEnd(k, high);
        long ax = CityShore.DX[k.side()], ay = CityShore.DY[k.side()];
        return new long[] { behind[0] + ax * QuayRail.BEHIND, behind[1] + ay * QuayRail.BEHIND };
    }

    static void railedTerminals() {
        System.out.println("\n--- 7. QuayRail.of(): the railed terminals, their quay track, the lead and the plots between the boxes ---");
        int quayBits = HarbourYard.RAIL | HarbourYard.OPEN_LOW | HarbourYard.OPEN_HIGH | HarbourYard.LEAD_LOW | HarbourYard.LEAD_HIGH;
        for (int side = 0; side < 4; side++) {
            String s = SIDES[side] + ": ";
            CityShore bay = bay(3, side, JOINED);
            List<CityShore.Work> ws = bay.works();
            List<long[]> ends = QuayRail.ends(bay);
            QuayRail low = QuayRail.of(bay, List.of(new long[] { ends.get(2)[1], ends.get(2)[2] }));
            check(s + "a line with a spur behind the middle berth's low column: all three have the quay track, and none has TRAIN (the screen's)",
                    !low.isEmpty() && (low.bits(ws.get(0)) & HarbourYard.RAIL) != 0 && (low.bits(ws.get(1)) & HarbourYard.RAIL) != 0 && (low.bits(ws.get(2)) & HarbourYard.RAIL) != 0
                            && (low.bits(ws.get(0)) | low.bits(ws.get(1)) | low.bits(ws.get(2))) < HarbourYard.TRAIN);
            check("...the first is open to its high side only, the last to its low side only, the middle to both",
                    low.bits(ws.get(0)) == (HarbourYard.RAIL | HarbourYard.OPEN_HIGH) && low.bits(ws.get(2)) == (HarbourYard.RAIL | HarbourYard.OPEN_LOW)
                            && (low.bits(ws.get(1)) & (HarbourYard.OPEN_LOW | HarbourYard.OPEN_HIGH)) == (HarbourYard.OPEN_LOW | HarbourYard.OPEN_HIGH));
            check("...the lead is the middle berth's alone, at its low column", low.bits(ws.get(1)) == (quayBits & ~HarbourYard.LEAD_HIGH));
            Set<Long> gaps = gapPlots(bay.quayLines().get(0));
            Set<Long> leads = between(ends.get(2)[1], ends.get(2)[2], landEdge(ws.get(1), false)[0], landEdge(ws.get(1), false)[1]);
            Set<Long> gotGaps = new HashSet<>(), gotLeads = new HashSet<>();
            int gapCells = 0, leadCells = 0, other = 0;
            boolean sideOk = true;
            for (long[] c : low.cells()) {
                sideOk &= c[3] == side;
                if (c[2] == QuayRail.GAP) { gapCells++; gotGaps.add(key(c[0], c[1])); }
                else if (c[2] == QuayRail.LEAD_AT_LOW) { leadCells++; gotLeads.add(key(c[0], c[1])); }
                else other++;
            }
            check("...the cells: a GAP for each plot between the boxes in the sea-most row (" + gaps.size() + "), carrying the line's sea side",
                    !gaps.isEmpty() && gapCells == gaps.size() && gotGaps.equals(gaps) && sideOk);
            check("...and CLEAR (" + CityShore.CLEAR + ") LEAD_AT_LOW cells between the spur's end and the middle box's land edge, no other kind",
                    leads.size() == CityShore.CLEAR && leadCells == CityShore.CLEAR && gotLeads.equals(leads) && other == 0);

            QuayRail high = QuayRail.of(bay, List.of(new long[] { ends.get(5)[1], ends.get(5)[2] }));
            int hc = 0;
            for (long[] c : high.cells()) if (c[2] == QuayRail.LEAD_AT_HIGH) hc++;
            check("a spur behind the last berth's high column: it has LEAD_HIGH and the first two do not, with LEAD_AT_HIGH cells",
                    high.bits(ws.get(2)) == (HarbourYard.RAIL | HarbourYard.OPEN_LOW | HarbourYard.LEAD_HIGH) && high.bits(ws.get(0)) == (HarbourYard.RAIL | HarbourYard.OPEN_HIGH)
                            && (high.bits(ws.get(1)) & (HarbourYard.LEAD_LOW | HarbourYard.LEAD_HIGH)) == 0 && hc == CityShore.CLEAR);

            QuayRail none = QuayRail.of(bay, List.of());
            QuayRail strange = QuayRail.of(bay, List.of(new long[] { 1, 1 }));
            boolean zero = true;
            for (CityShore.Work w : ws) zero &= none.bits(w) == 0 && strange.bits(w) == 0;
            check("with no spur, or a spur that ends at none of the line's ends: empty, every work's bits 0, no cells",
                    none.isEmpty() && strange.isEmpty() && zero && none.cells().isEmpty() && strange.cells().isEmpty());
        }

        CityShore two = bay(3, 0, JOINED);
        for (CityShore.Work w : shifted(bay(2, 0, JOINED), 100, 0).works()) two.add(w);
        List<long[]> ends = QuayRail.ends(two);
        QuayRail q = QuayRail.of(two, List.of(new long[] { ends.get(2)[1], ends.get(2)[2] }));
        boolean second = true;
        for (int k = 3; k < 5; k++) second &= q.bits(two.works().get(k)) == 0;
        boolean cellsFirst = true;
        for (long[] c : q.cells()) cellsFirst &= c[0] < 3000 + 100;
        check("a second line no spur ends at stays unrailed: its works' bits are 0 and none of the cells is its",
                two.quayLines().size() == 2 && q.bits(two.works().get(0)) != 0 && second && cellsFirst);
        check("a work on no line of the shore has bits 0", q.bits(new CityShore.Work(0, 0, 5, 4, 0, 0, 0)) == 0);
    }
}
