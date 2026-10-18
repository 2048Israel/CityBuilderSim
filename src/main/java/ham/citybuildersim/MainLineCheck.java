package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The main line is the railway's run from its gateway to the freight station,
 * the yard nearest the gate arm's end, and it is drawn double where the city
 * has a Freight Line.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. NO YARD: with a gateway and track but no yard, fill() marks M_MAIN on
 *      the whole gate arm (the hub and the hub's row toward the gateway) and
 *      on no other plot of the track, and freightStation() is null.
 *
 *   2. A YARD ON THE GATE ARM: M_MAIN runs from the yard's anchor out to the
 *      arm's end, not between the hub and the yard.
 *
 *   3. A YARD ON THE OPPOSITE ARM: the whole gate arm, the hub and the
 *      opposite arm from the hub to the yard are M_MAIN; the opposite arm
 *      beyond the yard is not.
 *
 *   4. A YARD ON A LATER CORRIDOR: the chain through its interchange is
 *      M_MAIN - the later arm from its start to the yard and not beyond it,
 *      and the earlier arm's plot at the interchange.
 *
 *   5. TWO YARDS: the one nearer the gate arm's end is freightStation().
 *
 *   6. NO GATEWAY: nothing is marked M_MAIN, and freightStation() is null.
 *
 *   7. DOUBLE TRACK: setDoubleMain(true) marks M_DOUBLE on exactly the
 *      M_MAIN plots; false marks none; signature() differs between the two.
 *
 *   8. THE HASHES: fill()'s hash with marks changes when a nearer yard moves
 *      the station over a box on the old path; with marks == null it does
 *      not, and frameHash() does not.
 *
 *   9. ON A REAL MAP: MapCheck.yardCity() has a Freight Line, so
 *      runs().doubleMain() is true, its freightStation() is not null and some
 *      tile's marks carry M_MAIN | M_DOUBLE; with NO_GATEWAY on that seed,
 *      nothing is marked instead.
 *
 *  10. A GATE ARM THAT BENDS: an arm of three stretches - east, south-east,
 *      east - is marked step for step, each 45-degree step's corner with it:
 *      a stretch's steps are counted from the arm's start, not the stretch's.
 *
 * WHY THIS EXISTS: batch FR0 slice M sets the main-line marks that the painter
 * already draws. A fixture CAUSES each condition, and every assertion is
 * against the model's own constants, never a bare number.
 */
public class MainLineCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-80s %s%n", label, ok ? "ok" : "FAIL");
    }

    /** A square of owned dry ground the runs may lay on, -1 outside: the GatewayCheck fixture's shape. */
    static class BoxGround implements CityRuns.Ground {
        final long x0, y0, x1, y1;

        BoxGround(long x0, long y0, long x1, long y1) {
            this.x0 = x0; this.y0 = y0; this.x1 = x1; this.y1 = y1;
        }

        @Override public int at(long x, long y, int dir) {
            return x >= x0 && x <= x1 && y >= y0 && y <= y1 ? World.GRASS : -1;
        }

        @Override public long version() { return 1; }

        @Override public long[] box() { return new long[] { x0, y0, x1, y1 }; }
    }

    static BoxGround ground(long x0, long y0, long x1, long y1) { return new BoxGround(x0, y0, x1, y1); }

    static void layRail(CityRuns runs, BoxGround g, long track, long siteX, long siteY) {
        runs.layTo(0, track, 0, 0, 0, g, siteX, siteY, siteX, siteY, new ArrayList<>());
    }

    /** The main plot of step k along an arm: step 0 its start plot. */
    static long[] plotOnArm(CityRuns.Arm a, int step) {
        if (step == 0) return new long[] { a.sx, a.sy };
        int seen = 0;
        for (int s = 0; s < a.segs; s++) {
            int m = a.segSteps[s];
            if (step <= seen + m) {
                int k = step - seen, h = a.segHead[s];
                return new long[] { a.segX[s] + (long) CityRuns.DX[h] * k, a.segY[s] + (long) CityRuns.DY[h] * k };
            }
            seen += m;
        }
        return null;
    }

    static void addYardAtStep(CityRuns runs, int arm, int step) {
        long[] p = plotOnArm(runs.rail.arms.get(arm), step);
        if (p != null) runs.yards.add(new long[] { p[0], p[1], 1, 1 });
    }

    static int markAt(long x0, long y0, int w, byte[] marks, long x, long y) {
        int h = marks.length / w;
        if (x < x0 || y < y0 || x >= x0 + w || y >= y0 + h) return 0;
        return marks[(int) ((y - y0) * w + (x - x0))] & 0xff;
    }

    static long fillBox(CityRuns runs, BoxGround g, byte[] fixed, byte[] marks) {
        int w = (int) (g.x1 - g.x0 + 1), h = (int) (g.y1 - g.y0 + 1);
        return runs.fill(g.x0, g.y0, w, h, fixed, marks, false);
    }

    public static void main(String[] args) {
        long siteX = 1000, siteY = 1000;

        /* ============ 1. no yard ============ */
        System.out.println("\n--- no yard ---");
        BoxGround one = ground(siteX - 300, siteY - 100, siteX + 10, siteY + 100);
        CityRuns runs = new CityRuns();
        runs.setGateway(0);
        layRail(runs, one, 40, siteX, siteY);
        CityRuns.Net rail = runs.rail;
        int gate = CityRuns.gateArm(rail, 0);
        check("the fixture lays a railway with an east gate arm", gate == 0 && rail.arms.get(gate).steps > 0);
        check("...and no yard, so freightStation() is null", runs.freightStation() == null);
        int w1 = (int) (one.x1 - one.x0 + 1), h1 = (int) (one.y1 - one.y0 + 1);
        byte[] fixed1 = new byte[w1 * h1], marks1 = new byte[w1 * h1];
        fillBox(runs, one, fixed1, marks1);
        long[] hub = rail.hub();
        boolean allGate = true, noOther = true;
        int gateMarked = 0;
        for (long y = one.y0; y <= one.y1; y++) {
            for (long x = one.x0; x <= one.x1; x++) {
                if (!CityRuns.covered(rail, x, y)) continue;
                int[] loc = CityRuns.locate(rail, x, y);
                boolean onGate = (x == hub[0] && y == hub[1]) || (loc != null && loc[0] == gate);
                boolean main = (markAt(one.x0, one.y0, w1, marks1, x, y) & TilePainter.M_MAIN) != 0;
                if (onGate) {
                    if (main) gateMarked++;
                    else allGate = false;
                } else if (main) {
                    noOther = false;
                }
            }
        }
        check("every plot of the gate arm (the hub and the row east of it) is M_MAIN", allGate && gateMarked > 0);
        check("...and no other plot of the track is M_MAIN", noOther);

        /* ============ 2. a yard on the gate arm ============ */
        System.out.println("\n--- a yard on the gate arm ---");
        BoxGround two = ground(siteX - 200, siteY - 60, siteX + 200, siteY + 60);
        runs = new CityRuns();
        runs.setGateway(0);
        layRail(runs, two, 20, siteX, siteY);
        rail = runs.rail;
        gate = CityRuns.gateArm(rail, 0);
        CityRuns.Arm gateArm = rail.arms.get(gate);
        int anchor = Math.min(5, gateArm.steps - 1);
        check("the gate arm is long enough for a yard part way out", gateArm.steps > anchor && anchor > 0);
        addYardAtStep(runs, gate, anchor);
        int w2 = (int) (two.x1 - two.x0 + 1), h2 = (int) (two.y1 - two.y0 + 1);
        byte[] fixed2 = new byte[w2 * h2], marks2 = new byte[w2 * h2];
        fillBox(runs, two, fixed2, marks2);
        hub = rail.hub();
        boolean fromAnchor = true, beforeAnchor = true;
        for (long y = two.y0; y <= two.y1; y++) {
            for (long x = two.x0; x <= two.x1; x++) {
                if (!CityRuns.covered(rail, x, y)) continue;
                int[] loc = CityRuns.locate(rail, x, y);
                boolean main = (markAt(two.x0, two.y0, w2, marks2, x, y) & TilePainter.M_MAIN) != 0;
                if (loc != null && loc[0] == gate) {
                    if (loc[1] >= anchor && main == false) fromAnchor = false;
                    if (loc[1] > 0 && loc[1] < anchor && main) beforeAnchor = false;
                }
                if (x == hub[0] && y == hub[1] && main) beforeAnchor = false;
            }
        }
        check("M_MAIN runs from the yard's anchor out to the gate arm's end", fromAnchor);
        check("...and not between the hub and the yard", beforeAnchor);

        /* ============ 3. a yard on the opposite arm ============ */
        System.out.println("\n--- a yard on the opposite arm ---");
        BoxGround three = ground(siteX - 300, siteY - 100, siteX + 10, siteY + 100);
        runs = new CityRuns();
        runs.setGateway(0);
        layRail(runs, three, 80, siteX, siteY);
        rail = runs.rail;
        gate = CityRuns.gateArm(rail, 0);
        CityRuns.Arm west = rail.arms.get(1);
        check("the fixture lays the opposite (west) arm", west.home == 4 && west.steps > 0);
        int westAnchor = Math.min(3, west.steps - 1);
        check("...far enough to put a yard before its end", west.steps > westAnchor && westAnchor > 0);
        addYardAtStep(runs, 1, westAnchor);
        int w3 = (int) (three.x1 - three.x0 + 1), h3 = (int) (three.y1 - three.y0 + 1);
        byte[] fixed3 = new byte[w3 * h3], marks3 = new byte[w3 * h3];
        fillBox(runs, three, fixed3, marks3);
        hub = rail.hub();
        boolean gateAll = true, westToYard = true, westBeyond = true, otherArms = true;
        for (long y = three.y0; y <= three.y1; y++) {
            for (long x = three.x0; x <= three.x1; x++) {
                if (!CityRuns.covered(rail, x, y)) continue;
                int[] loc = CityRuns.locate(rail, x, y);
                boolean main = (markAt(three.x0, three.y0, w3, marks3, x, y) & TilePainter.M_MAIN) != 0;
                if (x == hub[0] && y == hub[1]) {
                    if (!main) gateAll = false;
                } else if (loc == null) {
                    gateAll = false;
                } else if (loc[0] == gate) {
                    if (!main) gateAll = false;
                } else if (loc[0] == 1) {
                    if (loc[1] <= westAnchor && !main) westToYard = false;
                    if (loc[1] > westAnchor && main) westBeyond = false;
                } else if (main) {
                    otherArms = false;
                }
            }
        }
        check("the whole gate arm and the hub are M_MAIN", gateAll);
        check("...the opposite arm from the hub to the yard is M_MAIN", westToYard);
        check("...the opposite arm beyond the yard is not", westBeyond);
        check("...and no other arm is M_MAIN", otherArms);

        /* ============ 4. a yard on a later corridor ============ */
        System.out.println("\n--- a yard on a later corridor ---");
        BoxGround four = ground(siteX - 600, siteY - 600, siteX + 600, siteY + 600);
        runs = new CityRuns();
        runs.setGateway(0);
        layRail(runs, four, 6000, siteX, siteY);
        rail = runs.rail;
        gate = CityRuns.gateArm(rail, 0);
        int laterAi = -1;
        CityRuns.Arm later = null;
        for (int ai = 0; ai < rail.arms.size(); ai++) {
            CityRuns.Arm a = rail.arms.get(ai);
            if (a.corridor >= 2 && a.steps >= 3) { later = a; laterAi = ai; break; }
        }
        check("a third corridor opens and one of its arms has steps", later != null);
        if (later != null) {
            int laterAnchor = 2;
            addYardAtStep(runs, laterAi, laterAnchor);
            int[] prev = CityRuns.locate(rail, later.sx, later.sy);
            check("...its interchange lies on an earlier arm", prev != null && rail.arms.get(prev[0]).corridor < later.corridor);
            int w4 = (int) (four.x1 - four.x0 + 1), h4 = (int) (four.y1 - four.y0 + 1);
            byte[] fixed4 = new byte[w4 * h4], marks4 = new byte[w4 * h4];
            fillBox(runs, four, fixed4, marks4);
            boolean laterStartToYard = true, laterBeyond = true;
            if (prev != null) {
                boolean chainMarked = false;
                long[] atInterchange = plotOnArm(rail.arms.get(prev[0]), prev[1]);
                long px = atInterchange[0], py = atInterchange[1];
                chainMarked = (markAt(four.x0, four.y0, w4, marks4, px, py) & TilePainter.M_MAIN) != 0;
                check("...the earlier arm's plot at the interchange is M_MAIN", chainMarked);
            }
            for (int s = 0; s <= later.steps; s++) {
                long[] p = plotOnArm(later, s);
                if (p == null) continue;
                boolean main = (markAt(four.x0, four.y0, w4, marks4, p[0], p[1]) & TilePainter.M_MAIN) != 0;
                if (s <= laterAnchor && !main) laterStartToYard = false;
                if (s > laterAnchor && main) laterBeyond = false;
            }
            check("...the later arm from its start to the yard is M_MAIN", laterStartToYard);
            check("...the later arm beyond the yard is not", laterBeyond);
        }

        /* ============ 5. two yards ============ */
        System.out.println("\n--- two yards ---");
        BoxGround five = ground(siteX - 200, siteY - 60, siteX + 200, siteY + 60);
        runs = new CityRuns();
        runs.setGateway(0);
        layRail(runs, five, 20, siteX, siteY);
        rail = runs.rail;
        gate = CityRuns.gateArm(rail, 0);
        long[] near = plotOnArm(rail.arms.get(gate), 8);
        long[] far = plotOnArm(rail.arms.get(gate), 2);
        runs.yards.add(new long[] { near[0], near[1], 1, 1 });
        runs.yards.add(new long[] { far[0], far[1], 1, 1 });
        long[] station = runs.freightStation();
        check("of two yards on the gate arm, the one nearer its end is the freight station",
                station != null && Arrays.equals(station, new long[] { near[0], near[1], 1, 1 }));

        /* ============ 6. no gateway ============ */
        System.out.println("\n--- no gateway ---");
        BoxGround six = ground(siteX - 300, siteY - 100, siteX + 300, siteY + 100);
        runs = new CityRuns();
        runs.setGateway(CityRuns.NO_GATEWAY);
        layRail(runs, six, 40, siteX, siteY);
        int w6 = (int) (six.x1 - six.x0 + 1), h6 = (int) (six.y1 - six.y0 + 1);
        byte[] fixed6 = new byte[w6 * h6], marks6 = new byte[w6 * h6];
        fillBox(runs, six, fixed6, marks6);
        boolean noneMarked = true;
        for (byte b : marks6) if ((b & TilePainter.M_MAIN) != 0) noneMarked = false;
        check("with NO_GATEWAY nothing is marked M_MAIN", noneMarked);
        check("...and freightStation() is null", runs.freightStation() == null);

        /* ============ 7. double track ============ */
        System.out.println("\n--- double track ---");
        BoxGround seven = ground(siteX - 300, siteY - 100, siteX + 10, siteY + 100);
        runs = new CityRuns();
        runs.setGateway(0);
        layRail(runs, seven, 40, siteX, siteY);
        int w7 = (int) (seven.x1 - seven.x0 + 1), h7 = (int) (seven.y1 - seven.y0 + 1);
        byte[] fixed7 = new byte[w7 * h7], marks7 = new byte[w7 * h7];
        runs.setDoubleMain(true);
        fillBox(runs, seven, fixed7, marks7);
        boolean exactlyDouble = true;
        int mainPlots = 0;
        for (byte b : marks7) {
            boolean main = (b & TilePainter.M_MAIN) != 0, doub = (b & TilePainter.M_DOUBLE) != 0;
            if (main) mainPlots++;
            if (main != doub) exactlyDouble = false;
        }
        check("setDoubleMain(true) marks M_DOUBLE on exactly the M_MAIN plots", exactlyDouble && mainPlots > 0);
        long sigDouble = runs.signature();
        Arrays.fill(marks7, (byte) 0);
        runs.setDoubleMain(false);
        fillBox(runs, seven, fixed7, marks7);
        boolean noneDouble = true;
        for (byte b : marks7) if ((b & TilePainter.M_DOUBLE) != 0) noneDouble = false;
        check("...false marks no M_DOUBLE", noneDouble);
        check("...and signature() differs between the two", sigDouble != runs.signature());

        /* ============ 8. the hashes ============ */
        System.out.println("\n--- the hashes ---");
        BoxGround eight = ground(siteX - 200, siteY - 60, siteX + 200, siteY + 60);
        CityRuns oldYard = new CityRuns();
        oldYard.setGateway(0);
        layRail(oldYard, eight, 20, siteX, siteY);
        addYardAtStep(oldYard, CityRuns.gateArm(oldYard.rail, 0), 10);
        CityRuns nearYard = new CityRuns();
        nearYard.setGateway(0);
        layRail(nearYard, eight, 20, siteX, siteY);
        addYardAtStep(nearYard, CityRuns.gateArm(nearYard.rail, 0), 15);
        long[] oldBox = plotOnArm(oldYard.rail.arms.get(CityRuns.gateArm(oldYard.rail, 0)), 12);
        byte[] fixed8 = new byte[1], marks8 = new byte[1], fixed8b = new byte[1], marks8b = new byte[1];
        long oldMarksHash = oldYard.fill(oldBox[0], oldBox[1], 1, 1, fixed8, marks8, false);
        long oldNullHash = oldYard.fill(oldBox[0], oldBox[1], 1, 1, fixed8b, null, false);
        long oldFrame = oldYard.frameHash(oldBox[0], oldBox[1], 1, 1);
        long nearMarksHash = nearYard.fill(oldBox[0], oldBox[1], 1, 1, fixed8, marks8b, false);
        long nearNullHash = nearYard.fill(oldBox[0], oldBox[1], 1, 1, fixed8b, null, false);
        long nearFrame = nearYard.frameHash(oldBox[0], oldBox[1], 1, 1);
        check("a nearer yard moving the station changes fill()'s hash with marks", oldMarksHash != nearMarksHash);
        check("...with marks == null it does not", oldNullHash == nearNullHash);
        check("...and frameHash() does not", oldFrame == nearFrame);

        /* ============ 9. on a real map ============ */
        System.out.println("\n--- on a real map ---");
        MapCheck.Squares sq = new MapCheck.Squares();
        CityMap m = MapCheck.yardCity(sq);
        check("yardCity's runs have a Freight Line, so the main line is double", m.runs().doubleMain());
        boolean foundMarks = marksCarry(m, TilePainter.M_MAIN | TilePainter.M_DOUBLE);
        if (m.gateway() == CityRuns.NO_GATEWAY) {
            System.out.println("  yardCity's gateway is NO_GATEWAY on this seed; nothing is marked instead");
            check("...with no gateway its freight station is null", m.runs().freightStation() == null);
            check("...and no tile's marks carry M_MAIN | M_DOUBLE", !foundMarks);
        } else {
            check("...its freight station is not null", m.runs().freightStation() != null);
            check("...and some tile's marks carry M_MAIN | M_DOUBLE", foundMarks);
        }

        /* ============ 10. a gate arm that bends ============ */
        System.out.println("\n--- a gate arm that bends ---");
        BoxGround bendGround = ground(siteX - 100, siteY - 100, siteX + 100, siteY + 100);
        CityRuns bent = new CityRuns();
        bent.setGateway(0);
        check("the fixture starts a railway whose gate arm heads east", CityRuns.start(bent.rail, bendGround, siteX, siteY, 0)
                && CityRuns.gateArm(bent.rail, 0) == 0 && bent.rail.arms.get(0).home == 0);
        CityRuns.Arm arm = bent.rail.arms.get(0);
        int[] path = { 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0 };
        for (int hd : path) {
            arm.push(hd);
            arm.x += CityRuns.DX[hd];
            arm.y += CityRuns.DY[hd];
            arm.steps++;
            arm.head = hd;
        }
        check("...and bends it by hand into three stretches", arm.segs == 3 && arm.steps == path.length);
        int wb = (int) (bendGround.x1 - bendGround.x0 + 1), hb = (int) (bendGround.y1 - bendGround.y0 + 1);
        byte[] fixedB = new byte[wb * hb], marksB = new byte[wb * hb];
        fillBox(bent, bendGround, fixedB, marksB);
        boolean everyStep = true, everyCorner = true;
        for (int k = 0; k <= arm.steps; k++) {
            long[] q = plotOnArm(arm, k);
            if ((markAt(bendGround.x0, bendGround.y0, wb, marksB, q[0], q[1]) & TilePainter.M_MAIN) == 0) everyStep = false;
            int hd = k == 0 ? -1 : path[k - 1];
            if (hd >= 0 && CityRuns.DX[hd] != 0 && CityRuns.DY[hd] != 0
                    && (markAt(bendGround.x0, bendGround.y0, wb, marksB, q[0], q[1] - CityRuns.DY[hd]) & TilePainter.M_MAIN) == 0) everyCorner = false;
        }
        int markedB = 0;
        for (byte b : marksB) if ((b & TilePainter.M_MAIN) != 0) markedB++;
        check("every step of the bent gate arm, past its first stretch too, is M_MAIN", everyStep);
        check("...and each 45-degree step's corner with it", everyCorner);
        check("...and no other plot: as many marked as the arm's steps, its start and its corners", markedB == path.length + 1 + 3);

        System.out.println();
        System.out.println(fails == 0 ? "The main line runs to the freight station." : fails + " FAILED");
        System.exit(fails);
    }

    static boolean marksCarry(CityMap m, int bits) {
        MapTiles tiles = new MapTiles(m);
        TilePainter.Input in = new TilePainter.Input();
        long bx = m.baseDX(), by = m.baseDY();
        for (CityMap.District d : m.districts()) {
            for (int k = 0; k < CityMap.TILES; k++) {
                long tx = (bx + d.dx) * CityMap.TILES_A_SIDE + k % CityMap.TILES_A_SIDE;
                long ty = (by + d.dy) * CityMap.TILES_A_SIDE + k / CityMap.TILES_A_SIDE;
                tiles.input(tx, ty, in);
                for (byte b : in.marks) if ((b & bits) == bits) return true;
            }
        }
        return false;
    }
}
