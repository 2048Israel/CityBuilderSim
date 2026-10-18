package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 *  11. THE MAIN ARM (0.8.3, batch FX2): where the gate arm waits at the sea
 *      the city owns ahead of it and the opposite arm runs to the city's
 *      edge, the main line, the freight station and the trains' way run from
 *      the arm the map sets (CityRuns.setMainArm(), mainArm()). With none set
 *      mainArm() is the gate arm and mainLine() the gate arm's; with the west
 *      arm set, the freight station is the yard nearest its end, the M_MAIN
 *      marks lie on the west arm from that yard's anchor and on no plot of
 *      the gate arm, and wayTo() starts at the west arm's end plot's middle;
 *      -1 and an arm with no steps fall back to the gate arm; and a yard on a
 *      corridor that starts on the main arm is reached along that arm, no
 *      plot of the way twice and every quarter plot of it on track.
 *
 *  12. TWO YARDS STACKED ON A CORRIDOR (batch FX3, red until the fix): yard B
 *      stands over a north-south corridor's interchange with the spine, yard
 *      A a plot above it on the corridor's north arm, so A's way runs north
 *      through B's box. Each yard's routes (CityMap.trainRoutesInto()) end on
 *      north-south sidings, enter the fan at an apex on the corridor's
 *      column, and no leg of any route crosses a siding.
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

        theMainArm(siteX, siteY);
        stackedOnACorridor(siteX, siteY);

        System.out.println();
        System.out.println(fails == 0 ? "The main line runs to the freight station." : fails + " FAILED");
        System.exit(fails);
    }

    /** BoxGround with the city's own sea in it: the plots from seaX east to the box's east edge are owned salt water, the rest owned grass (FX2: Jerus's Halifax, where the gate arm waits at the sea the city owns beyond it). */
    static class SeaGround extends BoxGround {
        final long seaX;

        SeaGround(long x0, long y0, long x1, long y1, long seaX) {
            super(x0, y0, x1, y1);
            this.seaX = seaX;
        }

        @Override public int at(long x, long y, int dir) {
            if (x < x0 || x > x1 || y < y0 || y > y1) return -1;
            return x >= seaX ? World.SALT : World.GRASS;
        }
    }

    /** A railway of `track` plots laid on g with its gateway east, from the site (siteX, siteY). */
    static CityRuns seaCity(SeaGround g, long track, long siteX, long siteY) {
        CityRuns runs = new CityRuns();
        runs.setGateway(0);
        layRail(runs, g, track, siteX, siteY);
        return runs;
    }

    /** The index of the arm opposite the gate arm of a gateway east: corridor 0, home heading west (4); -1 for none. */
    static int westArm(CityRuns.Net rail) {
        for (int i = 0; i < rail.arms.size(); i++) if (rail.arms.get(i).corridor == 0 && rail.arms.get(i).home == 4) return i;
        return -1;
    }

    /** The plots fill() marks M_MAIN over the box, counted by the arm locate() puts them on; the last entry counts those on no arm (the hub's plot among them). */
    static int[] mainByArm(CityRuns runs, BoxGround g) {
        int w = (int) (g.x1 - g.x0 + 1), h = (int) (g.y1 - g.y0 + 1);
        byte[] fixed = new byte[w * h], marks = new byte[w * h];
        fillBox(runs, g, fixed, marks);
        int[] by = new int[runs.rail.arms.size() + 1];
        for (int i = 0; i < marks.length; i++) {
            if ((marks[i] & TilePainter.M_MAIN) == 0) continue;
            int[] loc = CityRuns.locate(runs.rail, g.x0 + i % w, g.y0 + i / w);
            by[loc == null ? by.length - 1 : loc[0]]++;
        }
        return by;
    }

    /** What the counts hold apart from entry keep: the plots on every other arm and on none. */
    static int others(int[] by, int keep) {
        int n = 0;
        for (int i = 0; i < by.length; i++) if (i != keep) n += by[i];
        return n;
    }

    /**
     * A way (wayTo()'s points, each a plot's middle) walked every quarter plot:
     * {positions walked, positions that enter a plot the walk had already
     * left (a way doubling back), positions on a plot the railway does not
     * cover}.
     */
    static int[] walkWay(CityRuns runs, double[][] way) {
        double[] xs = way[0], ys = way[1];
        Set<Long> visited = new HashSet<>();
        long here = Long.MIN_VALUE;
        int walked = 0, again = 0, off = 0;
        for (int i = 0; i < xs.length; i++) {
            int n = i == 0 ? 0 : Math.max(1, (int) Math.ceil(Math.hypot(xs[i] - xs[i - 1], ys[i] - ys[i - 1]) * 4));
            for (int k = 0; k <= n; k++) {
                if (i > 0 && k == 0) continue;
                double x = i == 0 ? xs[0] : xs[i - 1] + (xs[i] - xs[i - 1]) * k / n;
                double y = i == 0 ? ys[0] : ys[i - 1] + (ys[i] - ys[i - 1]) * k / n;
                long px = (long) Math.floor(x), py = (long) Math.floor(y);
                long key = (px << 32) ^ (py & 0xffffffffL);
                walked++;
                if (!CityRuns.covered(runs.rail, px, py)) off++;
                if (key != here && !visited.add(key)) again++;
                here = key;
            }
        }
        return new int[] { walked, again, off };
    }

    /* ============ 11. the main arm (FX2) ============ */

    /**
     * Section 11. Jerus's Halifax: the gate arm waits at the sea and the city
     * owns the sea beyond it, so the railway meets the world from another
     * arm. The fixture is SeaGround - the gate arm (east) ends a few plots
     * short of owned salt water, the arm opposite it (west) runs to the box's
     * edge - and the main arm is set the way the map sets it.
     */
    static void theMainArm(long siteX, long siteY) {
        System.out.println("\n--- the main arm (FX2) ---");
        long seaX = siteX + 60;
        SeaGround sea = new SeaGround(siteX - 300, siteY - 100, siteX + 90, siteY + 100, seaX);
        CityRuns runs = seaCity(sea, 1000, siteX, siteY);
        CityRuns.Net rail = runs.rail;
        int gate = CityRuns.gateArm(rail, 0), west = westArm(rail);
        boolean laid = gate >= 0 && west >= 0 && rail.arms.get(gate).steps > 0 && rail.arms.get(west).steps > 0;
        check("the fixture lays a railway with an east gate arm and the arm opposite it, both with track", laid);
        if (!laid) return;
        CityRuns.Arm gateA = rail.arms.get(gate), westA = rail.arms.get(west);
        int ahead = (int) (seaX - 1 - gateA.x), edge = (int) (westA.x - sea.x0);
        System.out.printf("  gate arm %d: heading %d, %d steps, ends at (%d, %d), %d plots short of the sea; west arm %d: heading %d, %d steps, ends at (%d, %d), %d plots from the box's edge%n",
                gate, gateA.head * 45, gateA.steps, gateA.x, gateA.y, ahead, west, westA.head * 45, westA.steps, westA.x, westA.y, edge);
        check("the gate arm waits at the sea the city owns: its end is fewer than LOOK plots short of it", gateA.home == 0 && ahead >= 0 && ahead < CityRuns.LOOK);
        check("...and the arm opposite it runs to the box's west edge, within LOOK plots of it", westA.home == 4 && edge >= 0 && edge < CityRuns.LOOK);

        // With no main arm set the gate arm is the main arm: today's rule.
        List<int[]> line = runs.mainLine();
        int[] by = mainByArm(runs, sea);
        check("with no main arm set, mainArm() is the gate arm", runs.mainArm() == gate);
        check("...mainLine() is the gate arm whole, from its start to its end", line.size() == 1 && Arrays.equals(line.get(0), new int[] { gate, 0, gateA.steps }));
        check("...and fill() marks M_MAIN on every step of the gate arm and on no plot of any other arm", by[gate] == gateA.steps && others(by, gate) == by[by.length - 1]);

        // setMainArm(west): the freight station, the marks and the trains' way run from the west arm.
        runs = seaCity(sea, 1000, siteX, siteY);
        rail = runs.rail;
        gateA = rail.arms.get(gate);
        westA = rail.arms.get(west);
        int gateStep = 10, westStep = 100;
        check("fixture: both arms are long enough for a yard part way along them", westA.steps > westStep && gateA.steps > gateStep);
        addYardAtStep(runs, gate, gateStep);
        addYardAtStep(runs, west, westStep);
        long[] gp = plotOnArm(gateA, gateStep), wp = plotOnArm(westA, westStep);
        long[] gateYard = { gp[0], gp[1], 1, 1 }, westYard = { wp[0], wp[1], 1, 1 };
        check("with no main arm set, the freight station is the yard nearest the gate arm's end: the gate arm's", Arrays.equals(runs.freightStation(), gateYard));
        runs.setMainArm(west);
        check("setMainArm(west): mainArm() is the west arm", runs.mainArm() == west);
        check("...the freight station is the yard nearest the west arm's end: the west arm's, with a yard on the gate arm too", Arrays.equals(runs.freightStation(), westYard));
        by = mainByArm(runs, sea);
        System.out.printf("  with the west arm the main arm: %d plots marked on it (its steps %d to %d), %d on the gate arm, %d elsewhere%n",
                by[west], westStep, westA.steps, by[gate], others(by, west) - by[gate]);
        check("...fill() marks M_MAIN from that yard's anchor out to the west arm's end", by[west] == westA.steps - westStep + 1);
        check("...on no plot of the gate arm past the hub", by[gate] == 0);
        check("...and on no plot of any other arm, or off the arms", others(by, west) == 0);
        double[][] way = runs.wayTo(runs.freightStation());
        check("...wayTo(the station) starts at the west arm's end plot's middle", way != null && way[0][0] == westA.x + 0.5 && way[1][0] == westA.y + 0.5);
        if (way != null) {
            int[] walked = walkWay(runs, way);
            double nearest = Double.NEGATIVE_INFINITY;
            for (double x : way[0]) nearest = Math.max(nearest, x);
            check("...runs along the west arm to the yard and goes no nearer the hub than it", nearest == westYard[0] + 0.5 && walked[1] == 0 && walked[2] == 0);
        }

        // -1, an arm with no steps and an index past the arms all fall back to the gate arm.
        CityRuns shortRun = seaCity(sea, 70, siteX, siteY);
        CityRuns.Net sr = shortRun.rail;
        int sGate = CityRuns.gateArm(sr, 0), sWest = westArm(sr), idle = -1;
        for (int i = 0; i < sr.arms.size() && idle < 0; i++) if (sr.arms.get(i).steps == 0) idle = i;
        boolean shorter = sGate >= 0 && sWest >= 0 && idle >= 0 && sr.arms.get(sGate).steps > 0 && sr.arms.get(sWest).steps > 0;
        check("fixture: a shorter railway has track on its gate and west arms and an arm with no steps", shorter);
        if (shorter) {
            shortRun.setMainArm(sWest);
            check("an arm with steps set as the main arm is the main arm", shortRun.mainArm() == sWest);
            shortRun.setMainArm(-1);
            check("setMainArm(-1): the main arm is the gate arm", shortRun.mainArm() == sGate);
            shortRun.setMainArm(idle);
            check("setMainArm(an arm with no steps): the main arm is the gate arm", shortRun.mainArm() == sGate);
            shortRun.setMainArm(sr.arms.size());
            check("...and so is an index past the net's arms", shortRun.mainArm() == sGate);
            List<int[]> back = shortRun.mainLine();
            check("...and the main line is the gate arm whole", back.size() == 1 && back.get(0)[0] == sGate && back.get(0)[1] == 0 && back.get(0)[2] == sr.arms.get(sGate).steps);
        }

        // A yard on a corridor that starts on the main arm: the way turns there, not at the hub.
        CityRuns grown = null;
        int gWest = -1, corr = -1, track = 1000;
        for (; track <= 4000 && corr < 0; track += 1000) {
            grown = seaCity(sea, track, siteX, siteY);
            gWest = westArm(grown.rail);
            for (int ai = 0; ai < grown.rail.arms.size() && corr < 0; ai++) {
                CityRuns.Arm a = grown.rail.arms.get(ai);
                int[] on = a.corridor >= 2 && a.steps >= 3 ? CityRuns.locate(grown.rail, a.sx, a.sy) : null;
                if (on != null && on[0] == gWest) corr = ai;
            }
        }
        check("a corridor opens on the west arm, and its arm has track", corr >= 0);
        if (corr < 0) return;
        CityRuns.Arm cArm = grown.rail.arms.get(corr), gWestA = grown.rail.arms.get(gWest);
        int[] joinedAt = CityRuns.locate(grown.rail, cArm.sx, cArm.sy);
        int yardStep = 2;
        grown.setMainArm(gWest);
        addYardAtStep(grown, corr, yardStep);
        long[] cp = plotOnArm(cArm, yardStep);
        long[] corrYard = { cp[0], cp[1], 1, 1 };
        System.out.printf("  corridor %d's arm %d (%d steps) starts on the west arm at its step %d; the yard stands %d steps out; %d plots of track laid%n",
                cArm.corridor, corr, cArm.steps, joinedAt[1], yardStep, grown.rail.plots());
        check("...the yard on it is the freight station, with the west arm the main arm", grown.mainArm() == gWest && Arrays.equals(grown.freightStation(), corrYard));
        by = mainByArm(grown, sea);
        check("...fill() marks M_MAIN on the west arm whole and on the corridor's arm to the yard", by[gWest] == gWestA.steps && by[corr] == yardStep);
        check("...and on no plot of the gate arm, nor anywhere else but the hub", by[gate] == 0 && others(by, gWest) - by[corr] == 1);
        double[][] cWay = grown.wayTo(corrYard);
        check("wayTo() reaches the corridor's yard", cWay != null);
        if (cWay != null) {
            int[] walked = walkWay(grown, cWay);
            double farthest = Double.NEGATIVE_INFINITY;
            for (double x : cWay[0]) farthest = Math.max(farthest, x);
            System.out.printf("  the way: %d points, %d quarter plots walked, %d of them entering a plot already left, %d off the track%n",
                    cWay[0].length, walked[0], walked[1], walked[2]);
            check("...it starts at the west arm's end plot's middle", cWay[0][0] == gWestA.x + 0.5 && cWay[1][0] == gWestA.y + 0.5);
            check("...it turns at the corridor's start and never comes in to the hub's column", farthest < grown.rail.hubX);
            check("...it has no plot twice: no doubling back", walked[0] > 0 && walked[1] == 0);
            check("...and every quarter plot of it is a plot the railway covers", walked[2] == 0);
        }
    }

    /* ============ 12. two yards stacked on a corridor (FX3) ============ */

    /** The cross product of (b - a) and (c - a): its sign is the side of a-b that c lies on. */
    static double turn(double[] a, double[] b, double[] c) {
        return (b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0]);
    }

    /** Whether segments a-b and c-d cross at a point strictly inside both: parallel, collinear and touching segments do not. */
    static boolean crossesInside(double[] a, double[] b, double[] c, double[] d) {
        return turn(a, b, c) * turn(a, b, d) < 0 && turn(c, d, a) * turn(c, d, b) < 0;
    }

    /** Route r's point k as {x, y}. */
    static double[] pointOf(TrainSchedule.Route r, int k) {
        return new double[] { r.xs()[k], r.ys()[k] };
    }

    /**
     * Section 12. Jerus's second yard: it stands over the interchange of a
     * north-south corridor with the spine, so the spine crosses its box, and a
     * yard a plot above it on the corridor's north arm is reached along the
     * corridor, through its box. The fixture is a railway laid on a box of
     * ground until a corridor of kind 1 opens whose interchange is on the
     * spine and whose track runs clear through both boxes; yard B's box covers
     * the interchange (24 x 24, the interchange 7 plots in from its north-west
     * corner), yard A's lies directly above it, a plot's gap between.
     */
    /** Plots: a yard's box a side in the fixture, his Rail Terminals' (CityRuns.spotsIn() lays one at either end of its cell: at 1, or CELL - side). */
    static final int YARD_SIDE = 24;

    static void stackedOnACorridor(long siteX, long siteY) {
        System.out.println("\n--- two yards stacked on a corridor (FX3) ---");
        BoxGround g = ground(siteX - 700, siteY - 700, siteX + 700, siteY + 700);
        CityRuns runs = null;
        int ci = -1, north = -1, south = -1;
        long ix = 0, iy = 0, track = 0;
        for (long t = 3000; t <= 15000 && ci < 0; t += 1500) {
            CityRuns r = new CityRuns();
            r.setGateway(0);
            layRail(r, g, t, siteX, siteY);
            CityRuns.Net net = r.rail;
            for (CityRuns.Interchange x : net.interchanges) {
                if (x.corridor() < 2 || net.corridors.get(x.corridor())[0] != 1 || x.y() != net.hubY) continue;
                int n = -1, s = -1;
                for (int ai = 0; ai < net.arms.size(); ai++) {
                    CityRuns.Arm a = net.arms.get(ai);
                    if (a.corridor != x.corridor()) continue;
                    if (a.home == 6) n = ai; else if (a.home == 2) s = ai;
                }
                long cx = Math.floorDiv(x.x(), CityRuns.CELL), cy = Math.floorDiv(x.y(), CityRuns.CELL);
                boolean clear = n >= 0 && s >= 0;
                // The corridor's column runs through both boxes (yard A's top row to yard B's bottom row), the spine's row through B's.
                for (long y = (cy - 1) * CityRuns.CELL + CityRuns.CELL - YARD_SIDE; clear && y < cy * CityRuns.CELL + 1 + YARD_SIDE; y++) clear = CityRuns.covered(net, x.x(), y);
                for (long px = cx * CityRuns.CELL + 1; clear && px < cx * CityRuns.CELL + 1 + YARD_SIDE; px++) clear = CityRuns.covered(net, px, x.y());
                if (!clear) continue;
                runs = r; ci = x.corridor(); north = n; south = s; ix = x.x(); iy = x.y(); track = t;
                break;
            }
        }
        check("the fixture lays a north-south corridor whose interchange is on the spine and whose track runs clear through both boxes", ci >= 0);
        if (ci < 0) return;
        CityRuns.Net rail = runs.rail;
        long cx = Math.floorDiv(ix, CityRuns.CELL), cy = Math.floorDiv(iy, CityRuns.CELL);
        CityRuns.Arm nArm = rail.arms.get(north), sArm = rail.arms.get(south);
        int main = runs.mainArm();
        CityRuns.Arm mainA = rail.arms.get(main);
        System.out.printf("  %d plots of track: corridor %d is the column x = %d, its interchange on the spine at (%d, %d); north arm %d: %d steps, south arm %d: %d steps; the hub (%d, %d); the main arm %d ends at (%d, %d)%n",
                track, ci, rail.corridors.get(ci)[1], ix, iy, north, nArm.steps, south, sArm.steps, rail.hubX, rail.hubY, main, mainA.x, mainA.y);

        long[] yardA = { cx * CityRuns.CELL + 1, (cy - 1) * CityRuns.CELL + CityRuns.CELL - YARD_SIDE, YARD_SIDE, YARD_SIDE };
        long[] yardB = { cx * CityRuns.CELL + 1, cy * CityRuns.CELL + 1, YARD_SIDE, YARD_SIDE };
        runs.yards.add(yardA);
        runs.yards.add(yardB);
        long[][] yards = { yardA, yardB };
        String[] names = { "A (above)", "B (over the interchange)" };
        double wx = mainA.x + 0.5, wy = mainA.y + 0.5, fx = wx + TrainSchedule.WORLD_PLOTS, fy = wy;
        List<TrainSchedule.Route> routes = new ArrayList<>();
        int[] made = new int[2];
        for (int y = 0; y < 2; y++) {
            double[][] way = runs.wayTo(yards[y]);
            check("wayTo() reaches yard " + names[y], way != null);
            if (way == null) return;
            int before = routes.size();
            CityMap.trainRoutesInto(routes, y, false, yards[y], way, fx, fy, wx, wy);
            made[y] = routes.size() - before;
        }
        check("each yard has a route to every one of its TileRaster.YARD_TRACKS sidings", made[0] == TileRaster.YARD_TRACKS && made[1] == TileRaster.YARD_TRACKS);

        boolean allNorthSouth = true, allOnColumn = true;
        for (int y = 0; y < 2; y++) {
            for (int i = 0; i < routes.size(); i++) {
                TrainSchedule.Route r = routes.get(i);
                if (r.yard() != y) continue;
                int n = r.xs().length;
                if (n < 4) { allNorthSouth = false; allOnColumn = false; continue; }
                if (r.xs()[n - 1] != r.xs()[n - 2] || r.ys()[n - 1] == r.ys()[n - 2]) allNorthSouth = false;
                if (Math.abs(r.xs()[n - 3] - (ix + 0.5)) > 1e-9) allOnColumn = false;
            }
        }
        // The last three points of each yard's routes: the fan's apex, then the siding's two ends.
        for (int y = 0; y < 2; y++) {
            StringBuilder sb = new StringBuilder("  yard " + names[y] + " at (" + yards[y][0] + ", " + yards[y][1] + "), the column x = " + (ix + 0.5) + ":");
            for (int i = 0; i < routes.size(); i++) {
                TrainSchedule.Route r = routes.get(i);
                if (r.yard() != y) continue;
                int n = r.xs().length;
                sb.append(String.format("%n    siding %d:", r.siding()));
                for (int k = Math.max(0, n - 3); k < n; k++) sb.append(String.format(" (%.2f, %.2f)", r.xs()[k], r.ys()[k]));
            }
            System.out.println(sb);
        }
        check("each yard's every route ends on a north-south siding: its last leg has the same x at both ends", allNorthSouth);
        check("...and enters the fan at an apex on the corridor's column: the point before its last two has x = ix + 0.5", allOnColumn);

        // The sidings are the routes' last legs; no leg of any route crosses one at a point strictly inside both.
        int crossings = 0;
        String first = null;
        for (int i = 0; i < routes.size(); i++) {
            TrainSchedule.Route s = routes.get(i);
            int sn = s.xs().length;
            double[] sa = pointOf(s, sn - 2), sb = pointOf(s, sn - 1);
            for (int j = 0; j < routes.size(); j++) {
                TrainSchedule.Route r = routes.get(j);
                for (int k = 0; k + 1 < r.xs().length; k++) {
                    if (j == i && k + 2 == sn) continue;
                    double[] a = pointOf(r, k), b = pointOf(r, k + 1);
                    if (crossesInside(a, b, sa, sb)) {
                        crossings++;
                        if (first == null) first = "yard " + r.yard() + " siding " + r.siding() + " leg " + k + " (" + a[0] + ", " + a[1] + ")-(" + b[0] + ", " + b[1]
                                + ") crosses yard " + s.yard() + " siding " + s.siding() + " (" + sa[0] + ", " + sa[1] + ")-(" + sb[0] + ", " + sb[1] + ")";
                    }
                }
            }
        }
        check("no train runs across a siding: no leg of any route crosses another route's siding strictly inside both", crossings == 0);
        System.out.println("  " + crossings + " leg-over-siding crossings" + (first == null ? "" : ", the first: " + first));
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
