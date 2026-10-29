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
 *  13. THE RAILWAY'S WAYS (batch FR7 slice 3, Jerus 2026-10-18: "no track is
 *      laid on no train's way; leftover plots go to the legend's surplus"):
 *      with a gateway, mines on both sides of the hub, more track than the
 *      ways use and two spurs, the railway is the gate arm and nothing else
 *      but branches and sidings; every track plot lies on a train's way or in
 *      a yard, and no arm ends in country (the main line at the city's edge,
 *      a branch at a yard, a siding at a works); the plots left over are
 *      counted (CityRuns.trackSurplus()) and the legend's words carry them; a
 *      further yard stands beside the largest group of works a district from
 *      every yard, reached by a branch, not beside the hub's small mine, and
 *      bundled works keep the yards bundled; a siding goes to the largest
 *      bulk works within a district of the line first, none to one farther
 *      or to a well, runs straight or with one turn clear of a highway's
 *      verge, and ends in a shed; the main line is laid first (a short track
 *      is all main line); with no gateway the line is the hub alone; a net
 *      from before the slice has its dead-end arms taken off; the same inputs
 *      lay the same net, and it reads back from its sidecar; the trains: a
 *      route to every siding, a bulk unit train's alone, on a real map too.
 *
 *  14. A STATION ALONE LAYS ITS MAIN LINE (batch FX5, Jerus 2026-10-20, his new
 *      Halifax city: "there is a railwawy depot but no rails"): with a gateway,
 *      one Rail Depot's yard and no track at all, the gate arm still runs to
 *      the city's edge and the yard stands on it; the same ground with no
 *      station and no track lays no line; with a station and a little track
 *      the line still runs to the edge, the sidings' budget and the surplus
 *      as before (none); the station gone, the line is taken back.
 *
 * 15. A YARD IS AS LONG AS A UNIT TRAIN ALONG ITS LINE (batch FX5 Y1, Jerus
 *      2026-10-20: "a long train will be out of the depot"): on open ground a
 *      Rail Terminal and a Rail Depot, laid with their footprints through
 *      layTo(), are CityRuns.YARD_LONG plots along the main line and
 *      min(w, h) of their footprint across; every plot of the line inside the
 *      box is track; the box covers the ring plot between two cells; a plan
 *      given the yards' ground lays no street on any of its plots (and would,
 *      were the ground not given); water on the ring plots beside the line
 *      lays the yard at the cell's interior, water in the cell too at the
 *      footprint's own length; each yard keeps the size it was asked for - laid
 *      again the next month it is the same box, not laid anew - and the
 *      sidecar writes and reads it back (an older one reads the box's own);
 *      a yard across a district's edge is one district's work, clipped to its
 *      frame (CityMap.workIn()).
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

    /**
     * The nets the railway was laid as until FR7 slice 3: every arm grown from the hub (CityRuns.grow(), still the highways'),
     * the gate arm first, then the arm opposite it, the perpendicular pair and new corridors. layTo() no longer grows the
     * railway so: its main line is the gate arm alone, its branches and sidings arms of corridors 2 and 3 (section 13).
     * Sections 3, 4, 11 and 12 prove the main line's marks, its chains and the trains' way on ARBITRARY arms - a branch is
     * a corridor's arm starting on the line, a net read back from before FR7 has the rest - so their premise is a net with
     * an opposite arm and later corridors, which they lay by hand this way.
     */
    static void layOldArms(CityRuns runs, BoxGround g, long track, long siteX, long siteY) {
        CityRuns.grow(runs.rail, runs.highways, track, g, siteX, siteY, runs.gateway());
        runs.version = runs.signature();
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
        layOldArms(runs, three, 80, siteX, siteY);
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
        layOldArms(runs, four, 6000, siteX, siteY);
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
        railWays(siteX, siteY, m, sq);
        stationAlone(siteX, siteY);
        longYards(m);

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
        layOldArms(runs, g, track, siteX, siteY);
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

    /** Plots: a yard's box a side in the fixture, his Rail Terminals' (CityRuns.spotsIn() lays one at either end of its cell: at 1, or CELL - side). */
    static final int YARD_SIDE = 24;

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
    static void stackedOnACorridor(long siteX, long siteY) {
        System.out.println("\n--- two yards stacked on a corridor (FX3) ---");
        BoxGround g = ground(siteX - 700, siteY - 700, siteX + 700, siteY + 700);
        CityRuns runs = null;
        int ci = -1, north = -1, south = -1;
        long ix = 0, iy = 0, track = 0;
        for (long t = 3000; t <= 15000 && ci < 0; t += 1500) {
            CityRuns r = new CityRuns();
            r.setGateway(0);
            layOldArms(r, g, t, siteX, siteY);
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
            CityMap.trainRoutesInto(routes, y, false, yards[y], way, fx, fy, wx, wy, CityMap.yardRail(runs, yards[y]));
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
                // FX5: the ladder is sampled (TrackCurves.ladder()), so the apex is the last point on the column before the siding's
                // start, no more than YARD_LADDER plots along before it (the middle siding stays on the column to its end).
                int k = n - 2;
                while (k >= 0 && Math.abs(r.xs()[k] - (ix + 0.5)) > 1e-9) k--;
                if (k < 0 || Math.abs(r.ys()[k] - r.ys()[n - 2]) > TileRaster.YARD_LADDER + 1e-9) allOnColumn = false;
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
        check("...and enters the fan at an apex on the corridor's column: its last point on x = ix + 0.5 is no more than YARD_LADDER"
                + " plots before the siding (FX5: the ladder is an S of points)", allOnColumn);

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

    /* ============ 13. the railway's ways (FR7 slice 3) ============ */

    /** A works' box {x0, y0, x1, y1} (inclusive) from its north-west plot and its sides. */
    static long[] box(long x0, long y0, int w, int h) {
        return new long[] { x0, y0, x0 + w - 1, y0 + h - 1 };
    }

    static long key(long x, long y) {
        return (x << 32) ^ (y & 0xffffffffL);
    }

    static boolean insideBox(long[] b, long x, long y) {
        return x >= b[0] && x <= b[2] && y >= b[1] && y <= b[3];
    }

    /** Whether plot (x, y) is outside box b and next to it on a side (a siding's last plot, beside its works). */
    static boolean beside(long[] b, long x, long y) {
        return !insideBox(b, x, y) && (x >= b[0] - 1 && x <= b[2] + 1 && y >= b[1] && y <= b[3] || y >= b[1] - 1 && y <= b[3] + 1 && x >= b[0] && x <= b[2]);
    }

    static boolean inYard(long[] yard, long x, long y) {
        return x >= yard[0] && y >= yard[1] && x < yard[0] + yard[2] && y < yard[1] + yard[3];
    }

    /** The plots a way (points, each a plot's middle) walks, every quarter plot. */
    static Set<Long> walkedPlots(double[][] way) {
        Set<Long> out = new HashSet<>();
        double[] xs = way[0], ys = way[1];
        for (int i = 0; i < xs.length; i++) {
            int n = i == 0 ? 0 : Math.max(1, (int) Math.ceil(Math.hypot(xs[i] - xs[i - 1], ys[i] - ys[i - 1]) * 4));
            for (int k = 0; k <= n; k++) {
                double x = i == 0 ? xs[0] : xs[i - 1] + (xs[i] - xs[i - 1]) * k / n;
                double y = i == 0 ? ys[0] : ys[i - 1] + (ys[i] - ys[i - 1]) * k / n;
                out.add(key((long) Math.floor(x), (long) Math.floor(y)));
            }
        }
        return out;
    }

    /** The plots of every arm of net n with steps laid (the arms' plots, not the hub's) that a highway's plot is next to or on: those within one plot of a highway. */
    static int plotsNearHighways(CityRuns runs, CityRuns.Arm a) {
        int near = 0;
        for (int k = 1; k <= a.steps; k++) {
            long[] p = plotOnArm(a, k);
            boolean hit = false;
            for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) hit |= CityRuns.covered(runs.highways, p[0] + dx, p[1] + dy);
            if (hit) near++;
        }
        return near;
    }

    /**
     * Section 13. A railway laid by layTo(): a gateway east, a hub on the
     * line at the site, mines on both sides of it, more track than the ways
     * use and two spurs. The works (box, plots, how far north of the line
     * y = siteY): a small mine by the hub (36, 40), a mine west of it (64),
     * w1 (256, 105), w2 (100, 100 south), w3 (1,024, 169), a farther one (1,600,
     * 461: past a district), a well that is a mine and no bulk works, and a
     * group of four 1,600-plot boxes five plots apart, 650 plots north.
     */
    static void railWays(long siteX, long siteY, CityMap real, MapCheck.Squares sq) {
        System.out.println("\n--- the railway's ways: a main line, sidings, branches and the plots left over (FR7 slice 3) ---");
        BoxGround g = ground(siteX - 300, siteY - 800, siteX + 1700, siteY + 400);
        long[] hubMine = box(siteX - 3, siteY - 40, 6, 6), westMine = box(siteX - 200, siteY - 30, 8, 8);
        long[] w1 = box(siteX + 400, siteY - 120, 16, 16), w2 = box(siteX + 200, siteY + 100, 10, 10), w3 = box(siteX + 600, siteY - 200, 32, 32);
        long[] farWorks = box(siteX + 800, siteY - 500, 40, 40), well = box(siteX + 100, siteY - 60, 8, 8);
        long[][] group = { box(siteX + 700, siteY - 700, 40, 40), box(siteX + 745, siteY - 700, 40, 40), box(siteX + 700, siteY - 655, 40, 40), box(siteX + 745, siteY - 655, 40, 40) };
        List<long[]> works = new ArrayList<>(Arrays.asList(hubMine, westMine, w1, w2, w3, farWorks));
        works.addAll(Arrays.asList(group));
        List<long[]> mines = new ArrayList<>(works);
        mines.add(well);
        List<int[]> sizes = new ArrayList<>(List.of(new int[] { 24, 24 }, new int[] { 8, 7 }));
        long track = 6000;

        CityRuns runs = new CityRuns();
        runs.setGateway(0);
        runs.layTo(0, track, sizes, 2, g, siteX, siteY, siteX, siteY, mines, works);
        CityRuns.Net rail = runs.rail;
        int gate = CityRuns.gateArm(rail, 0);
        CityRuns.Arm gateA = rail.arms.get(gate);
        boolean onlyWays = gateA.steps > 0;
        int armsWithSteps = 0;
        for (CityRuns.Arm a : rail.arms) {
            if (a.steps == 0) continue;
            armsWithSteps++;
            onlyWays &= a == gateA || a.corridor == CityRuns.BRANCH || a.corridor == CityRuns.SIDING;
        }
        List<long[]> sidings = runs.sidings(), branches = runs.branches();
        System.out.printf("  %d plots of track: the main line %d plots to (%d, %d), %d sidings, %d branch(es), the yards %s%n",
                rail.plots(), 1 + gateA.steps, gateA.x, gateA.y, sidings.size(), branches.size(), Arrays.deepToString(runs.yards().toArray()));
        check("the railway is the gate arm, branches and sidings: no opposite arm, no perpendicular pair, no corridor", onlyWays && armsWithSteps == 1 + sidings.size() + branches.size());
        check("the main line waits at the city's ground, fewer than LOOK plots short of the box's east edge", g.x1 - gateA.x >= 0 && g.x1 - gateA.x < CityRuns.LOOK);

        // The surplus: the model's track less the plots drawn, and the legend's words carry it.
        long used = 1 + gateA.steps, sidingPlots = 0, branchPlots = 0;
        for (long[] s : sidings) sidingPlots += s[5];
        for (long[] b : branches) branchPlots += b[5];
        check("the plots drawn are the main line's, the sidings' and the branches'", rail.plots() == used + sidingPlots + branchPlots);
        check("the surplus is the model's track less those drawn, and there is one (the fixture lays more track than the ways use)",
                runs.trackSurplus() == track - rail.plots() && runs.trackSurplus() > 0);
        String words = LandMap.trackSurplusWords(runs.trackSurplus());
        check("the legend's words carry it: \"" + words + "\"", words.contains(String.format("%,d", runs.trackSurplus())) && words.contains("more than the line uses"));

        // Every track plot lies on a train's way or in a yard; no arm ends in country.
        Set<Long> onWay = new HashSet<>();
        boolean waysFound = true;
        for (long[] y : runs.yards()) {
            double[][] way = runs.wayTo(y);
            waysFound &= way != null;
            if (way != null) onWay.addAll(walkedPlots(way));
        }
        for (long[] s : sidings) {
            double[][] way = runs.wayToPlot(s[3], s[4]);
            waysFound &= way != null;
            if (way != null) onWay.addAll(walkedPlots(way));
        }
        check("the trains' way reaches every yard and every siding", waysFound && runs.yards().size() == 2);
        long laidPlots = 0, offWay = 0;
        for (long y = g.y0; y <= g.y1; y++) {
            for (long x = g.x0; x <= g.x1; x++) {
                if (!CityRuns.covered(rail, x, y)) continue;
                laidPlots++;
                boolean inAYard = false;
                for (long[] yd : runs.yards()) inAYard |= inYard(yd, x, y);
                if (!inAYard && !onWay.contains(key(x, y))) offWay++;
            }
        }
        System.out.printf("  %,d plots of track in the box, %,d of them on no way and in no yard%n", laidPlots, offWay);
        check("every drawn track plot lies on a yard's way, a siding's way, or in a yard's box", laidPlots > 0 && offWay == 0);
        boolean branchesEnd = true, sidingsEnd = true;
        for (long[] b : branches) {
            boolean atYard = false;
            for (long[] yd : runs.yards()) atYard |= inYard(yd, b[3], b[4]);
            branchesEnd &= atYard;
        }
        for (long[] s : sidings) {
            boolean atWorks = false;
            for (long[] w : works) atWorks |= beside(w, s[3], s[4]);
            sidingsEnd &= atWorks;
        }
        check("no arm ends in country: each branch ends in a yard's box, each siding beside a works' box", branchesEnd && sidingsEnd);

        // The yards: each its own size; the first on the line; the further one beside the largest group, by a branch.
        long[] y0 = runs.yards().get(0), y1 = runs.yards().get(1);
        // FX5 (Jerus, 2026-10-20, "the rail depots would have to be redesigned to accomodate such large trains"): a yard's box is laid
        // YARD_LONG along its line and its footprint's least side across, so "its own size" is the footprint it was asked for (yardAsked())
        // and a box of that rule, not the footprint as the box.
        check("each yard keeps its own size: the first asked 24 x 24, the second 8 x 7, each laid YARD_LONG along its line and its footprint's"
                        + " least side across (FX5)",
                java.util.Arrays.equals(runs.yardAsked(0), new int[] { 24, 24 }) && java.util.Arrays.equals(runs.yardAsked(1), new int[] { 8, 7 })
                        && Math.max(y0[2], y0[3]) == CityRuns.YARD_LONG && Math.min(y0[2], y0[3]) == 24
                        && Math.max(y1[2], y1[3]) == CityRuns.YARD_LONG && Math.min(y1[2], y1[3]) == 7);
        boolean firstOnLine = false;
        for (long y = y0[1]; y < y0[1] + y0[3]; y++) for (long x = y0[0]; x < y0[0] + y0[2]; x++) firstOnLine |= CityRuns.covered(rail, x, y);
        check("the first yard stands on the main line", firstOnLine);
        double gx = 0, gy = 0;
        for (long[] b : group) { gx += (b[0] + b[2] + 1) / 2.0; gy += (b[1] + b[3] + 1) / 2.0; }
        gx /= group.length;
        gy /= group.length;
        double toGroup = Math.hypot(y1[0] + y1[2] / 2.0 - gx, y1[1] + y1[3] / 2.0 - gy);
        double toHubMine = Math.hypot(y1[0] + y1[2] / 2.0 - (hubMine[0] + hubMine[2] + 1) / 2.0, y1[1] + y1[3] / 2.0 - (hubMine[1] + hubMine[3] + 1) / 2.0);
        System.out.printf("  the second yard stands %.0f plots from the group's middle (%.0f, %.0f) and %.0f from the mine by the hub%n", toGroup, gx, gy, toHubMine);
        check("the further yard stands beside the largest group of works: within YARD_APART of its middle", toGroup <= CityRuns.YARD_APART);
        check("...and not beside the mine by the hub: farther from it than BRANCH_FROM", toHubMine > CityRuns.BRANCH_FROM);
        check("...it is more than a district from the line, so a branch reaches it: one", branches.size() == 1
                && Math.abs(y1[1] + y1[3] / 2.0 - siteY) > CityRuns.BRANCH_FROM);
        if (branches.size() == 1) {
            long[] b = branches.get(0);
            CityRuns.Arm ba = rail.arms.get((int) b[0]);
            int[] on = CityRuns.locate(rail, b[1], b[2]);
            check("...a straight arm off the main line (its first plot on the gate arm, one stretch)", ba.segs == 1 && on != null && on[0] == gate);
            check("...as long as it needs: it ends on the yard's far edge, no farther", b[4] == y1[1] && inYard(y1, b[3], b[4]));
        }

        // A siding goes to the largest bulk works within a district first, none farther, none to a well.
        check("two spurs, two sidings", sidings.size() == 2);
        if (sidings.size() == 2) {
            check("the first goes to the largest bulk works within a district of the line (w3), the second to the next (w1)",
                    beside(w3, sidings.get(0)[3], sidings.get(0)[4]) && beside(w1, sidings.get(1)[3], sidings.get(1)[4]));
            check("...each straight from the line: its plots are the distance to the works' edge", sidings.get(0)[5] == siteY - (w3[3] + 1) && sidings.get(1)[5] == siteY - (w1[3] + 1));
            boolean fromLine = true, shed = true;
            int w = (int) (g.x1 - g.x0 + 1), h = (int) (g.y1 - g.y0 + 1);
            byte[] fixed = new byte[w * h], marks = new byte[w * h];
            fillBox(runs, g, fixed, marks);
            for (long[] s : sidings) {
                int[] on = CityRuns.locate(rail, s[1], s[2]);
                fromLine &= on != null && on[0] == gate;
                shed &= (markAt(g.x0, g.y0, w, marks, s[3], s[4]) & TilePainter.M_SHED) != 0;
            }
            int sheds = 0;
            for (byte b : marks) if ((b & TilePainter.M_SHED) != 0) sheds++;
            check("...leaving the main line, and ending in a loading shed (M_SHED), the only sheds there are", fromLine && shed && sheds == sidings.size());
            boolean none = true;
            for (long[] s : sidings) {
                none &= !beside(w2, s[3], s[4]) && !beside(farWorks, s[3], s[4]) && !beside(well, s[3], s[4]) && !beside(hubMine, s[3], s[4]) && !beside(westMine, s[3], s[4]);
            }
            check("...none to the works a district away (the largest of all), to a well, nor to a smaller one: only two spurs", none);
        }

        // Laid again, the same; read back from the sidecar, the same, and laying it moves nothing.
        boolean moved = runs.layTo(0, track, sizes, 2, g, siteX, siteY, siteX, siteY, mines, works);
        check("laid again to the same counts, nothing moves", !moved);
        try {
            java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
            runs.write(new java.io.DataOutputStream(bytes));
            CityRuns back = CityRuns.read(new java.io.DataInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray())));
            back.setGateway(0);
            check("the net reads back from its sidecar as it was (the branches and sidings are arms)", back != null && back.same(runs));
            boolean backMoved = back.layTo(0, track, sizes, 2, g, siteX, siteY, siteX, siteY, mines, works);
            check("...and laying it again to the same counts moves nothing", !backMoved && back.same(runs));
        } catch (java.io.IOException e) {
            check("the sidecar round trip: " + e, false);
        }

        // Further yards bundled: the works are within a district of the first yard, so the second stays beside it.
        CityRuns bundled = new CityRuns();
        bundled.setGateway(0);
        List<long[]> few = new ArrayList<>(Arrays.asList(hubMine, w2));
        bundled.layTo(0, track, sizes, 0, g, siteX, siteY, siteX, siteY, few, few);
        long[] b0 = bundled.yards().get(0), b1 = bundled.yards().get(1);
        double apart = Math.hypot(b0[0] + b0[2] / 2.0 - b1[0] - b1[2] / 2.0, b0[1] + b0[3] / 2.0 - b1[1] - b1[3] / 2.0);
        check("two yards bundled stay where the works are bundled: no group a district from the first, so the second stands beside it, with no branch",
                bundled.yards().size() == 2 && apart <= CityRuns.YARD_APART && bundled.branches().isEmpty());

        // The main line is laid first: a short track is all main line.
        CityRuns shortRun = new CityRuns();
        shortRun.setGateway(0);
        shortRun.layTo(0, 72, sizes.subList(0, 1), 2, g, siteX, siteY, siteX, siteY, mines, works);
        CityRuns.Arm shortGate = shortRun.rail.arms.get(CityRuns.gateArm(shortRun.rail, 0));
        // FX5 (Jerus, 2026-10-20, "there is a railwawy depot but no rails"): with a station standing the main line runs to the
        // city's edge whatever track the model has, so this fixture's line is no longer its 72 plots; the premise kept is that
        // track short of the line lays no siding. The 72 plots as the whole line, short of the edge, is held with no station.
        check("a short track (72 plots, a spur's) beside a station: the line runs to the city's edge anyway (FX5), so the 72 plots"
                        + " are all main line and no siding is laid yet",
                shortRun.rail.plots() > 72 && g.x1 - shortGate.x <= CityRuns.LOOK && shortRun.sidings().isEmpty() && shortRun.trackSurplus() == 0);
        CityRuns noStation = new CityRuns();
        noStation.setGateway(0);
        noStation.layTo(0, 72, sizes.subList(0, 0), 2, g, siteX, siteY, siteX, siteY, mines, works);
        CityRuns.Arm noStationGate = noStation.rail.arms.get(CityRuns.gateArm(noStation.rail, 0));
        check("...and with no station the 72 plots are the whole line, short of the city's edge, and no siding is laid",
                noStation.rail.plots() == 72 && g.x1 - noStationGate.x > CityRuns.LOOK && noStation.sidings().isEmpty());

        // No gateway: the main line is the hub alone.
        CityRuns island = new CityRuns();
        island.setGateway(CityRuns.NO_GATEWAY);
        island.layTo(0, 3000, sizes.subList(0, 1), 2, g, siteX, siteY, siteX, siteY, mines, works);
        boolean noSteps = true;
        for (CityRuns.Arm a : island.rail.arms) noSteps &= a.steps == 0;
        check("with no gateway the main line is the hub alone: one plot, no arm, no siding, no branch, the rest the surplus",
                island.rail.plots() == 1 && noSteps && island.sidings().isEmpty() && island.branches().isEmpty() && island.trackSurplus() == 3000 - 1);
        check("...and the yard still stands, in the hub's cell", island.yards().size() == 1);

        // A gate arm that waits at the sea the city owns reaches no world (FX2's Halifax): the arm after it is grown too, the perpendicular pair is not.
        SeaGround sea = new SeaGround(siteX - 300, siteY - 100, siteX + 90, siteY + 100, siteX + 60);
        CityRuns coast = new CityRuns();
        coast.setGateway(0);
        coast.layTo(0, 1000, new ArrayList<>(), 0, sea, siteX, siteY, siteX, siteY, new ArrayList<>(), new ArrayList<>());
        CityRuns.Arm seaGate = coast.rail.arms.get(CityRuns.gateArm(coast.rail, 0)), seaWest = coast.rail.arms.get(westArm(coast.rail));
        int perpendicular = 0;
        for (CityRuns.Arm a : coast.rail.arms) if (a.corridor == 1) perpendicular += a.steps;
        check("a gate arm that waits at the sea the city owns: the arm opposite it runs to the city's edge as the main line, the perpendicular pair has no track",
                seaGate.steps > 0 && sea.seaX - 1 - seaGate.x < CityRuns.LOOK && seaWest.steps > 0 && seaWest.x - sea.x0 < CityRuns.LOOK && perpendicular == 0);
        check("...and the rest of the track is the surplus",
                coast.rail.plots() == 1 + seaGate.steps + seaWest.steps && coast.trackSurplus() == 1000 - coast.rail.plots() && coast.trackSurplus() > 0);

        // A net from before the slice: the dead-end arms are taken off at its first month.
        CityRuns old = new CityRuns();
        old.setGateway(0);
        layOldArms(old, g, 3000, siteX, siteY);
        int oldWithSteps = 0;
        for (CityRuns.Arm a : old.rail.arms) if (a.steps > 0) oldWithSteps++;
        old.layTo(0, 3000, sizes.subList(0, 1), 0, g, siteX, siteY, siteX, siteY, mines, works);
        int nowWithSteps = 0;
        for (CityRuns.Arm a : old.rail.arms) if (a.steps > 0) nowWithSteps++;
        CityRuns.Arm oldGate = old.rail.arms.get(CityRuns.gateArm(old.rail, 0));
        check("a railway grown as before (" + oldWithSteps + " arms with track) has the gate arm alone at its first month, the rest of its track the surplus",
                oldWithSteps > 1 && nowWithSteps == 1 && old.rail.plots() == 1 + oldGate.steps && old.trackSurplus() == 3000 - old.rail.plots());

        // A highway's verge: the siding's straight way crosses it, so it goes round.
        BoxGround hg = ground(siteX - 300, siteY - 400, siteX + 1700, siteY + 400);
        long[] w6 = box(siteX + 50, siteY + 100, 10, 10);
        List<long[]> only = new ArrayList<>(List.of(w6));
        CityRuns hw = new CityRuns();
        hw.setGateway(0);
        hw.layTo(120, 6000, new ArrayList<>(), 1, hg, siteX, siteY, siteX, siteY, only, only);
        List<long[]> hs = hw.sidings();
        boolean straightCrosses = false;
        for (long y = siteY + 1; y < w6[1]; y++) straightCrosses |= CityRuns.covered(hw.highways, w6[0], y);
        check("fixture: a highway runs across the works' column, so the straight way south is across it", straightCrosses);
        check("...yet the works has its siding", hs.size() == 1 && beside(w6, hs.get(0)[3], hs.get(0)[4]));
        if (hs.size() == 1) {
            CityRuns.Arm sa = hw.rail.arms.get((int) hs.get(0)[0]);
            check("...which turns once (two stretches) and has no plot on a highway or beside one", sa.segs == 2 && plotsNearHighways(hw, sa) == 0);
        }

        trainsToTheSidings(siteX, siteY, runs, real, sq);
    }

    /** The trains: a route to every siding, a bulk unit train's alone, on the fixture's net and on a real map. */
    static void trainsToTheSidings(long siteX, long siteY, CityRuns runs, CityMap real, MapCheck.Squares sq) {
        // The schedule: boxes and general cargo to the station, tank wagons to the yards, hoppers to the yards and the sidings.
        double[] xs = { -64, 0, 20 }, ys = { 0, 0, 0 };
        List<TrainSchedule.Route> routes = new ArrayList<>();
        routes.add(TrainSchedule.Route.through(0, true, 0, xs, ys, 64));
        routes.add(TrainSchedule.Route.through(1, false, 0, xs, ys, 64));
        routes.add(TrainSchedule.Route.toWorks(xs, ys, 64));
        check("a siding's route is a works' route: yard -1, not the station, no yard siding", routes.get(2).works() && routes.get(2).yard() == -1
                && !routes.get(2).station() && routes.get(2).siding() == -1 && !routes.get(0).works() && !routes.get(1).works());
        int kinds = Ports.Cargo.values().length;
        double[] in = new double[kinds], out = new double[kinds];
        in[Ports.Cargo.DRY_BULK.ordinal()] = 40 * TrainSchedule.UNIT_TONNES;
        out[Ports.Cargo.LIQUID.ordinal()] = 40 * TrainSchedule.UNIT_TONNES;
        in[Ports.Cargo.CONTAINER.ordinal()] = 10 * TrainSchedule.Kind.INTERMODAL.tonnes();
        out[Ports.Cargo.GENERAL.ordinal()] = 10 * TrainSchedule.MIXED_TONNES;
        TrainSchedule month = TrainSchedule.of(7, in, out, routes);
        int hoppersOnWorks = 0, hoppers = 0, otherOnWorks = 0, boxesElsewhere = 0;
        for (TrainSchedule.Call c : month.calls()) {
            boolean onWorks = c.route() >= 0 && routes.get(c.route()).works();
            if (c.kind() == TrainSchedule.Kind.HOPPER) { hoppers++; if (onWorks) hoppersOnWorks++; }
            else if (onWorks) otherOnWorks++;
            if (c.kind().toStation() && !(c.route() >= 0 && routes.get(c.route()).station())) boxesElsewhere++;
        }
        System.out.printf("  a month of every kind: %d hopper trains, %d of them to the sidings; %d other trains on a siding%n", hoppers, hoppersOnWorks, otherOnWorks);
        check("a bulk unit train (hoppers) runs to a siding too, and to the yards; no tank, mixed or container train goes to a siding",
                hoppers > 0 && hoppersOnWorks > 0 && hoppersOnWorks < hoppers && otherOnWorks == 0 && boxesElsewhere == 0);
        TrainSchedule onlySidings = TrainSchedule.of(7, in, out, List.of(routes.get(2)));
        boolean noneOnSiding = true;
        for (TrainSchedule.Call c : onlySidings.calls()) noneOnSiding &= c.kind() == TrainSchedule.Kind.HOPPER || c.route() < 0;
        check("with only a siding's route, only hopper trains run on it: the others have no route", noneOnSiding);

        // On a real map: one route to every siding the trains' way reaches, ending on its last plot.
        if (real.gateway() == CityRuns.NO_GATEWAY) {
            System.out.println("  yardCity's gateway is NO_GATEWAY on this seed; no sidings' routes to hold");
            return;
        }
        sidingRoutes(real, "yardCity", true);

        // ...and on the same map with a bulk works beside its main line, so that a spur has a siding to serve (the map's own mines
        // stand round the hub, which the line touches: no siding there). The runs are laid again to the map's own counts and that works.
        CityMap lit = MapCheck.yardCity(sq);
        CityRuns lr = lit.runs();
        int ai = CityRuns.gateArm(lr.rail(), lit.gateway());
        if (ai < 0 || lr.rail().arms.get(ai).steps < 80) {
            System.out.println("  the yard city's gate arm is too short to lay a works beside it on this seed");
            return;
        }
        CityRuns.Arm line = lr.rail().arms.get(ai);
        long[] at = plotOnArm(line, 60);
        List<long[]> mines = lit.minedSites(), works = lit.bulkSites();
        long[] extra = null;
        for (int off = 30; off <= 90 && extra == null; off += 10) {
            for (int side = -1; side <= 1 && extra == null; side += 2) {
                boolean alongX = CityRuns.DX[line.segHead[0]] != 0;
                long bx = alongX ? at[0] - 6 : at[0] + side * off - (side < 0 ? 11 : 0), by = alongX ? at[1] + side * off - (side < 0 ? 11 : 0) : at[1] - 6;
                long[] b = { bx, by, bx + 11, by + 11 };
                boolean free = true;
                for (long y = b[1] - 1; y <= b[3] + 1 && free; y++) for (long x = b[0] - 1; x <= b[2] + 1 && free; x++) free = CityRuns.dry(lit.runGround().at(x, y, -1)) && !CityRuns.covered(lr.rail(), x, y) && !CityRuns.covered(lr.highways(), x, y);
                for (long[] m : mines) if (b[0] - 1 <= m[2] && m[0] <= b[2] + 1 && b[1] - 1 <= m[3] && m[1] <= b[3] + 1) free = false;
                if (free) extra = b;
            }
        }
        if (extra == null) {
            System.out.println("  no free ground for a works beside the yard city's line on this seed");
            return;
        }
        mines.add(extra);
        works.add(extra);
        List<int[]> sizes = new ArrayList<>();
        for (int i = 0; i < lr.yards().size(); i++) sizes.add(BuildingVisual.footprint(lit.types()[lit.yardTypeOf(i)]));
        int spurs = 0;
        for (int t = 0; t < lit.types().length; t++) if (lit.types()[t] != null && lit.types()[t].siding()) spurs += lit.totals()[t];
        long[] site = lit.site();
        long track = lr.rail().plots() + lr.trackSurplus();
        lr.layTo(lr.highways().plots(), track, sizes, spurs, lit.runGround(), site[0], site[1], lr.rail().hub()[0], lr.rail().hub()[1], mines, works);
        System.out.printf("  the works %s beside the yard city's line at %s: %d spurs, %d siding(s) laid%n", Arrays.toString(extra), Arrays.toString(at), spurs, lr.sidings().size());
        check("the yard city's spur is laid as a siding into the works beside its line: one, ending beside the works' box",
                lr.sidings().size() == 1 && beside(extra, lr.sidings().get(0)[3], lr.sidings().get(0)[4]));
        sidingRoutes(lit, "yardCity with a works beside its line", false);
    }

    /** Section 13's trains on a map: one bulk route to every siding the trains' way reaches, each ending on its siding's last plot; every other route a yard's. `maybeNone`: no siding to find is no failure. */
    static void sidingRoutes(CityMap map, String what, boolean maybeNone) {
        // MF1: a district's plan's sidings have routes of their own, ending on the shed (the way's last plot), beside the runs' sidings'. The plans
        // are drawn first - the routes know a plan's sidings once it is adopted - and their sheds noted.
        java.util.Set<Long> sheds = new java.util.HashSet<>();
        int planWays = 0, f = DistrictPlan.FRAME;
        for (CityMap.District d : map.districts()) {
            CityMap.Drawn dr = map.drawn(d);
            if (dr == null || dr.sidingWays == null) continue;
            long ox = (map.baseDX() + d.dx) * CityMap.DISTRICT, oy = (map.baseDY() + d.dy) * CityMap.DISTRICT;
            for (int[] way : dr.sidingWays) {
                int shed = way[way.length - 1];
                sheds.add(((ox + shed % f) << 32) ^ ((oy + shed / f) & 0xffffffffL));
                planWays++;
            }
        }
        List<TrainSchedule.Route> mapRoutes = map.trainRoutes();
        List<long[]> mapSidings = map.runs().sidings();
        int reached = 0;
        boolean endsOnPlot = true;
        for (long[] s : mapSidings) if (map.runs().wayToPlot(s[3], s[4]) != null) reached++;
        int worksRoutes = 0;
        for (TrainSchedule.Route r : mapRoutes) {
            if (!r.works()) continue;
            worksRoutes++;
            double lx = r.xs()[r.xs().length - 1], ly = r.ys()[r.ys().length - 1];
            boolean found = false;
            for (long[] s : mapSidings) found |= lx == s[3] + 0.5 && ly == s[4] + 0.5;
            for (long sh : sheds) found |= lx == (sh >> 32) + 0.5 && ly == (int) sh + 0.5;
            endsOnPlot &= found;
        }
        boolean yardRoutesAreYards = true;
        for (TrainSchedule.Route r : mapRoutes) if (!r.works()) yardRoutesAreYards &= r.yard() >= 0 && r.yard() < map.runs().yards().size();
        System.out.printf("  %s: %d sidings, %d reached by the trains' way, %d plan sidings, %d routes in all, %d of them to a siding%n",
                what, mapSidings.size(), reached, planWays, mapRoutes.size(), worksRoutes);
        check(what + ": one bulk route to every siding the trains' way reaches and every siding the plans lay (MF1), each ending on its last plot", worksRoutes == reached + planWays && endsOnPlot && (maybeNone || reached + planWays > 0));
        check("...and every other route belongs to a yard, as before", yardRoutesAreYards);
    }

    /** The Rail Depot's yard size, {across, down}: BuildingVisual.footprint() of the catalogue's depot, the size CityMap.layRuns() gives the runs. */
    static int[] depotYard() {
        for (BuildingVisual.Type t : BuildingVisual.table(new BuildingCatalog().load())) if (BuildingVisual.depot(t)) return BuildingVisual.footprint(t);
        throw new IllegalStateException("no Rail Depot type");
    }

    /** How many plots of the net's arm `gate` (its start and every step) lie inside the yard's box {x, y, w, h}. */
    static int armInside(CityRuns.Arm gate, long[] yard) {
        int in = 0;
        for (int k = 0; k <= gate.steps; k++) {
            long[] q = plotOnArm(gate, k);
            if (q != null && q[0] >= yard[0] && q[0] < yard[0] + yard[2] && q[1] >= yard[1] && q[1] < yard[1] + yard[3]) in++;
        }
        return in;
    }

    /** Section 14: a station alone lays its main line (batch FX5). */
    static void stationAlone(long siteX, long siteY) {
        System.out.println("\n--- a station alone lays its main line (FX5) ---");
        BoxGround g = ground(siteX - 300, siteY - 100, siteX + 600, siteY + 100);
        int[] fp = depotYard();
        List<int[]> oneDepot = new ArrayList<>(List.of(fp));
        check("fixture: the Rail Depot's yard is smaller than the district's interior, so a spot on the line takes it",
                fp[0] >= 1 && fp[1] >= 1 && fp[0] <= DistrictPlan.INTERIOR && fp[1] <= DistrictPlan.INTERIOR);

        CityRuns alone = new CityRuns();
        alone.setGateway(0);
        alone.layTo(0, 0, oneDepot, 0, g, siteX, siteY, siteX, siteY, new ArrayList<>(), new ArrayList<>());
        int gate = CityRuns.gateArm(alone.rail, 0);
        CityRuns.Arm gateA = gate < 0 ? null : alone.rail.arms.get(gate);
        System.out.printf("  a depot (%d x %d) and no track: the gate arm %s%n", fp[0], fp[1], gateA == null ? "does not exist" : gateA.steps + " steps to (" + gateA.x + ", " + gateA.y + ")");
        check("a city with one Rail Depot and no track lays the main line: the gate arm has steps", gateA != null && gateA.steps > 0);
        check("...and it waits at the city's ground, fewer than LOOK plots short of the box's east edge",
                gateA != null && g.x1 - gateA.x >= 0 && g.x1 - gateA.x < CityRuns.LOOK);
        check("...the model has no track, so none is a siding's or a branch's, and there is no surplus",
                alone.sidings().isEmpty() && alone.branches().isEmpty() && alone.trackSurplus() == 0 && alone.rail.plots() == 1 + (gateA == null ? 0 : gateA.steps));
        // FX5 (slice Y1): the depot's box is YARD_LONG along its line and its footprint's least side across; it was asked for its footprint.
        check("...the depot's yard is laid, asked for its own footprint, YARD_LONG along its line (FX5)", alone.yards().size() == 1
                && java.util.Arrays.equals(alone.yardAsked(0), fp) && alone.yards().get(0)[2] == CityRuns.YARD_LONG && alone.yards().get(0)[3] == Math.min(fp[0], fp[1]));
        check("...and its box covers the main line: the yard's whole length along the east gate arm, " + fp[0] + " plots, is inside it",
                gateA != null && !alone.yards().isEmpty() && armInside(gateA, alone.yards().get(0)) >= fp[0]);

        CityRuns none = new CityRuns();
        none.setGateway(0);
        none.layTo(0, 0, new ArrayList<>(), 0, g, siteX, siteY, siteX, siteY, new ArrayList<>(), new ArrayList<>());
        int noneGate = CityRuns.gateArm(none.rail, 0);
        check("control: the same ground with no station and no track lays no line",
                none.rail.plots() == 0 && (noneGate < 0 || none.rail.arms.get(noneGate).steps == 0) && none.yards().isEmpty());

        // A little track with a station: the line still runs to the edge; no siding, no surplus, as before.
        long little = 40;
        CityRuns some = new CityRuns();
        some.setGateway(0);
        some.layTo(0, little, oneDepot, 1, g, siteX, siteY, siteX, siteY, new ArrayList<>(), new ArrayList<>());
        CityRuns.Arm someGate = some.rail.arms.get(CityRuns.gateArm(some.rail, 0));
        check("a station with " + little + " plots of track runs the main line to the city's edge, past the track the model has",
                someGate.steps + 1 > little && g.x1 - someGate.x >= 0 && g.x1 - someGate.x < CityRuns.LOOK);
        check("...and lays no siding (the track is short of the line) and shows no surplus", some.sidings().isEmpty() && some.trackSurplus() == 0);

        // The station gone: its yard stands idle and the line with it (MF1, YARD_IDLE_MONTHS); once its time is up the model's track alone sets the line again.
        long lineWas = alone.rail.plots();
        alone.setMonth(10);
        alone.layTo(0, 0, new ArrayList<>(), 0, g, siteX, siteY, siteX, siteY, new ArrayList<>(), new ArrayList<>());
        check("with the station gone and no track, its yard stands idle and the main line is held, not taken back (MF1)",
                alone.yards().size() == 1 && alone.yardsActive() == 0 && alone.rail.plots() == lineWas && lineWas > 0);
        alone.setMonth(10 + CityRuns.YARD_IDLE_MONTHS);
        alone.layTo(0, 0, new ArrayList<>(), 0, g, siteX, siteY, siteX, siteY, new ArrayList<>(), new ArrayList<>());
        check("...and with its idle time up, no station and no track, the line is taken back", alone.rail.plots() == 0 && alone.yards().isEmpty());
    }

    /** The Rail Terminal's yard size, {across, down}: BuildingVisual.footprint() of the catalogue's terminal that is no depot. */
    static int[] terminalYard() {
        for (BuildingVisual.Type t : BuildingVisual.table(new BuildingCatalog().load())) if (t != null && t.terminal() && !BuildingVisual.depot(t)) return BuildingVisual.footprint(t);
        throw new IllegalStateException("no Rail Terminal type");
    }

    /** A yard laid on the long fixture's ground: the gateway east, the founding site JunctionCheck.SITE, no mines, `track` plots of track, the sizes asked in order. */
    static CityRuns yardsRun(BoxGround g, List<int[]> sizes, long track) {
        CityRuns r = new CityRuns();
        r.setGateway(0);
        layYards(r, g, sizes, track);
        return r;
    }

    /** ...laid (again) to the sizes and track given. */
    static boolean layYards(CityRuns r, BoxGround g, List<int[]> sizes, long track) {
        long s = JunctionCheck.SITE;
        return r.layTo(0, track, sizes, 0, g, s, s, s, s, new ArrayList<>(), new ArrayList<>());
    }

    /** The ground of the long fixture: owned grass round the founding site, as JunctionCheck lays its cities (the frame at the world's origin lies inside it). */
    static JunctionCheck.PatchGround longGround() {
        long s = JunctionCheck.SITE;
        return new JunctionCheck.PatchGround(s - 150, s - 150, s + 150, s + 150);
    }

    /** A yard's line: {along, across, 1 for a row}, the RAIL_AT line of its cell through its box being track the box's whole length; null where it is not. */
    static int[] lineOf(CityRuns runs, long[] y) {
        long ly = Math.floorDiv(y[1], CityRuns.CELL) * CityRuns.CELL + CityRuns.RAIL_AT, lx = Math.floorDiv(y[0], CityRuns.CELL) * CityRuns.CELL + CityRuns.RAIL_AT;
        if (ly >= y[1] && ly < y[1] + y[3]) {
            boolean all = true;
            for (long x = y[0]; x < y[0] + y[2]; x++) all &= CityRuns.covered(runs.rail, x, ly);
            if (all) return new int[] { (int) y[2], (int) y[3], 1 };
        }
        if (lx >= y[0] && lx < y[0] + y[2]) {
            boolean all = true;
            for (long yy = y[1]; yy < y[1] + y[3]; yy++) all &= CityRuns.covered(runs.rail, lx, yy);
            if (all) return new int[] { (int) y[3], (int) y[2], 0 };
        }
        return null;
    }

    /** Whether the yard's line holds a ring plot (a multiple of CELL along it): the box runs on across the street between two cells. */
    static boolean coversRing(long[] y, int[] line) {
        long from = line[2] == 1 ? y[0] : y[1], to = from + line[0];
        for (long p = from; p < to; p++) if (Math.floorMod(p, CityRuns.CELL) == 0) return true;
        return false;
    }

    /** How many plots of the yards' boxes (in the frame at the world's origin) the plan lays a street on. */
    static int streetsOnYards(DistrictPlan p, List<long[]> yards) {
        int n = 0, f = DistrictPlan.FRAME;
        for (long[] y : yards) {
            for (long yy = y[1]; yy < y[1] + y[3]; yy++) {
                for (long x = y[0]; x < y[0] + y[2]; x++) if (x >= 0 && yy >= 0 && x < f && yy < f && p.isStreet((int) (yy * f + x))) n++;
            }
        }
        return n;
    }

    /** Section 15: a yard is as long as a unit train along its line (batch FX5 Y1). `real` is the yard city of section 9, laid by the map. */
    static void longYards(CityMap real) {
        System.out.println("\n--- a yard is as long as a unit train along its line (FX5) ---");
        int[] term = terminalYard(), depot = depotYard();
        int[][] fps = { term, depot };
        String[] names = { "the Rail Terminal", "the Rail Depot" };
        List<int[]> sizes = new ArrayList<>(List.of(term, depot));
        int unit = (int) Math.ceil(2 * TrainSchedule.LOCO_PLOTS + Math.ceil(TrainSchedule.UNIT_TONNES / TrainSchedule.WAGON_TONNES) * TrainSchedule.CAR_PLOTS);
        check("fixture: a unit train's length rounded up and two ladders (YARD_LONG) is longer than a cell's interior, so its yard must run on into the next cell",
                CityRuns.YARD_LONG == unit + 2 * TileRaster.YARD_LADDER && CityRuns.YARD_LONG > DistrictPlan.INTERIOR
                        && term[0] <= DistrictPlan.INTERIOR && depot[0] <= DistrictPlan.INTERIOR);

        // Open ground: both yards as long as the unit train.
        BoxGround g = longGround();
        CityRuns open = yardsRun(g, sizes, 0);
        check("fixture: both yards are laid on open ground", open.yards().size() == 2);
        if (open.yards().size() != 2) return;
        List<long[]> boxes = new ArrayList<>(open.yards());
        for (int i = 0; i < 2; i++) {
            long[] y = open.yards().get(i);
            int[] ln = lineOf(open, y);
            System.out.printf("  %s: box %s, along %s, asked %s%n", names[i], Arrays.toString(y), ln == null ? "no line" : ln[0] + " (" + (ln[2] == 1 ? "a row" : "a column") + ")", Arrays.toString(open.yardAsked(i)));
            check(names[i] + " stands on the main line, the line's plots inside its box all track", ln != null);
            if (ln == null) continue;
            check("...it is YARD_LONG plots along its line, " + CityRuns.YARD_LONG, ln[0] == CityRuns.YARD_LONG);
            check("...and min(w, h) of its footprint across, " + Math.min(fps[i][0], fps[i][1]), ln[1] == Math.min(fps[i][0], fps[i][1]));
            check("...it covers a ring plot, the street between two cells", coversRing(y, ln));
            check("...and keeps the footprint it was asked for, " + Arrays.toString(fps[i]) + ", which is not its box", Arrays.equals(open.yardAsked(i), fps[i]) && (y[2] != fps[i][0] || y[3] != fps[i][1]));
        }

        // The plan is given the yards' ground and lays no street across it.
        DistrictPlan.Input in = JunctionCheck.planInput(open);
        int given = 0, expect = 0, f = DistrictPlan.FRAME;
        for (int q = 0; q < f * f; q++) if (in.fixed[q] == DistrictPlan.FIXED_YARD) given++;
        for (long[] y : boxes) {
            for (long yy = y[1]; yy < y[1] + y[3]; yy++) for (long x = y[0]; x < y[0] + y[2]; x++) if (x >= 0 && yy >= 0 && x < f && yy < f) expect++;
        }
        DistrictPlan plan = DistrictPlan.make(in);
        int streets = streetsOnYards(plan, boxes);
        DistrictPlan.Input bare = JunctionCheck.planInput(open);
        for (int q = 0; q < f * f; q++) if (bare.fixed[q] == DistrictPlan.FIXED_YARD) bare.fixed[q] = 0;
        int would = streetsOnYards(DistrictPlan.make(bare), boxes);
        System.out.printf("  the plan: %d yard plots given (%d in the boxes), %d street plots on them; without the yards' ground %d%n", given, expect, streets, would);
        check("a plan is given every plot of both long boxes as yard ground", given == expect && given > 0);
        check("...and lays no street on any of them: the ring street stops at the yard on both sides", streets == 0);
        check("...which the fixture causes: without the yards' ground the same plan lays a street across them", would > 0);

        // The ground cuts the line short: water on the ring plots beside the line, the yards at the cell's interior...
        long lineY = open.rail.hub()[1];
        JunctionCheck.PatchGround rings = longGround();
        for (long x = 0; x <= f; x += CityRuns.CELL) for (long y = rings.y0; y <= rings.y1; y++) if (y != lineY) rings.with(x, y, World.SALT);
        CityRuns cell = yardsRun(rings, sizes, 0);
        check("water on every ring plot but the line's: both yards are still laid", cell.yards().size() == 2);
        for (int i = 0; i < cell.yards().size(); i++) {
            long[] y = cell.yards().get(i);
            int[] ln = lineOf(cell, y);
            long from = ln == null ? 0 : (ln[2] == 1 ? y[0] : y[1]);
            System.out.printf("  %s with water on the rings: box %s, asked %s%n", names[i], Arrays.toString(y), Arrays.toString(cell.yardAsked(i)));
            check(names[i] + " is laid at the next length that fits, the cell's interior, " + DistrictPlan.INTERIOR + " plots, across min(w, h) as before",
                    ln != null && ln[0] == DistrictPlan.INTERIOR && ln[1] == Math.min(fps[i][0], fps[i][1]));
            check("...inside one cell, over no ring plot", ln != null && !coversRing(y, ln) && Math.floorDiv(from, CityRuns.CELL) == Math.floorDiv(from + ln[0] - 1, CityRuns.CELL));
            check("...and it was asked for " + Arrays.toString(fps[i]), Arrays.equals(cell.yardAsked(i), fps[i]));
        }
        // ...water in the cell too, and the yards are their footprints' own length.
        JunctionCheck.PatchGround within = longGround();
        for (long x = 0; x <= f; x += CityRuns.CELL) {
            for (long y = within.y0; y <= within.y1; y++) {
                if (y == lineY) continue;
                within.with(x, y, World.SALT);
                within.with(x + 28, y, World.SALT);
            }
        }
        CityRuns own = yardsRun(within, sizes, 0);
        check("water on the rings and 28 plots into every cell: both yards are still laid", own.yards().size() == 2);
        for (int i = 0; i < own.yards().size(); i++) {
            long[] y = own.yards().get(i);
            int[] ln = lineOf(own, y);
            System.out.printf("  %s with water in the cells too: box %s%n", names[i], Arrays.toString(y));
            // FX5 (the tiers by train): the dry stretch is the 27 plots between the two waters, so a yard takes the longest tier
            // that fits in it - a Terminal its own 24, a Depot a mixed train's 15 (TIER_LENGTHS), not its 8-plot footprint, which holds none.
            int fits = fps[i][0];
            for (int t = 0; t < CityRuns.TIERS; t++) if (CityRuns.tierAlong(fps[i][0], t) <= 27) fits = Math.max(fits, CityRuns.tierAlong(fps[i][0], t));
            check(names[i] + " is laid at the longest tier the 27 dry plots allow, " + fits + " plots along its row", ln != null && ln[2] == 1 && ln[0] == fits
                    && ln[1] == (fits == fps[i][0] ? fps[i][1] : Math.min(fps[i][0], fps[i][1])));
        }

        // Each yard keeps the size it was asked for: laid again the next month, it is the same box, not laid anew.
        long[] first = open.yards().get(0), second = open.yards().get(1);
        long[] firstCopy = first.clone(), secondCopy = second.clone();
        layYards(open, g, sizes, 1);
        check("laid again the next month, the long yards are the same boxes", open.yards().size() == 2 && Arrays.equals(open.yards().get(0), firstCopy) && Arrays.equals(open.yards().get(1), secondCopy));
        check("...kept, not taken up and laid anew: the same box objects", open.yards().get(0) == first && open.yards().get(1) == second);
        long[] cellFirst = cell.yards().get(0);
        layYards(cell, rings, sizes, 1);
        check("a yard laid short by the ground is kept short the next month too, the same box", cell.yards().size() == 2 && cell.yards().get(0) == cellFirst);
        // A footprint changed is a different yard asked for: it is laid again, at the new size.
        List<int[]> bigger = new ArrayList<>(List.of(new int[] { term[0] + 2, term[1] + 2 }, depot));
        layYards(cell, g, bigger, 2);
        check("a yard asked for at another footprint is laid again, long where the ground allows, and keeps the new ask",
                cell.yards().size() == 2 && Arrays.equals(cell.yardAsked(0), bigger.get(0)) && cell.yards().get(0)[2] == CityRuns.YARD_LONG);

        // The sidecar writes the asked sizes and reads them back; an older one reads the boxes' own.
        try {
            java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
            open.write(new java.io.DataOutputStream(bytes));
            CityRuns back = CityRuns.read(new java.io.DataInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray())));
            if (back != null) back.setGateway(0);
            check("the long yards read back from the sidecar: the same runs", back != null && back.same(open));
            boolean askedBack = back != null && back.yards().size() == 2;
            for (int i = 0; askedBack && i < 2; i++) askedBack &= Arrays.equals(back.yardAsked(i), fps[i]) && (back.yards().get(i)[2] != fps[i][0] || back.yards().get(i)[3] != fps[i][1]);
            check("...each with the footprint it was asked for, not its box's size", askedBack);
            if (back != null) {
                boolean moved = layYards(back, g, sizes, 0);
                check("...and laid again to the same asks it moves nothing: the yards are not laid anew", !moved && back.same(open));
            }
            java.io.ByteArrayOutputStream oldBytes = new java.io.ByteArrayOutputStream();
            open.write(new java.io.DataOutputStream(oldBytes), 9);
            CityRuns older = CityRuns.read(new java.io.DataInputStream(new java.io.ByteArrayInputStream(oldBytes.toByteArray())), 9);
            boolean boxes9 = older != null && older.yards().size() == 2;
            for (int i = 0; boxes9 && i < 2; i++) boxes9 &= Arrays.equals(older.yardAsked(i), new int[] { (int) older.yards().get(i)[2], (int) older.yards().get(i)[3] });
            check("a sidecar of FORMAT 9 reads each yard's footprint as its box's own width and height, and does not fail", boxes9);
        } catch (java.io.IOException e) {
            check("the sidecar round trip: " + e, false);
        }

        // On a real map the yards are laid through CityMap.layRuns(): each asked for its type's footprint, on its line, at one of the lengths.
        CityRuns rr = real.runs();
        boolean realAsked = !rr.yards().isEmpty(), realLine = true, realLength = true;
        StringBuilder lengths = new StringBuilder();
        for (int i = 0; i < rr.yards().size(); i++) {
            int[] fp = BuildingVisual.footprint(real.types()[real.yardTypeOf(i)]);
            realAsked &= Arrays.equals(rr.yardAsked(i), fp);
            int[] ln = lineOf(rr, rr.yards().get(i));
            realLine &= ln != null;
            if (ln == null) continue;
            int footAlong = ln[2] == 1 ? fp[0] : fp[1];
            realLength &= (ln[0] == CityRuns.YARD_LONG || ln[0] == DistrictPlan.INTERIOR || ln[0] == footAlong) && ln[1] >= Math.min(fp[0], fp[1]);
            lengths.append(ln[0]).append(' ');
        }
        System.out.printf("  the yard city's %d yards are %s plots along their lines%n", rr.yards().size(), lengths.toString().trim());
        check("the yard city's yards, laid by the map, are each asked for their type's footprint", realAsked);
        check("...each stands on its line, YARD_LONG, the cell's interior or its footprint's length along it", realLine && realLength);

        // A yard across a district's edge is one district's work, whole, clipped to its frame.
        int d = CityMap.DISTRICT;
        long[][] across = { { d - 31, 97, CityRuns.YARD_LONG, 24 }, { d - 16, 97, CityRuns.YARD_LONG, 24 }, { 97, d - 31, 24, CityRuns.YARD_LONG }, { 97, d - 16, 24, CityRuns.YARD_LONG } };
        boolean once = true, inside = true, larger = true;
        for (int k = 0; k < across.length; k++) {
            long[] y = across[k];
            int owners = 0;
            for (int dx = 0; dx <= (k < 2 ? 1 : 0); dx++) {
                for (int dy = 0; dy <= (k < 2 ? 0 : 1); dy++) {
                    int[] w = CityMap.workIn(y, (long) dx * d, (long) dy * d);
                    if (w == null) continue;
                    owners++;
                    inside &= w[0] >= 1 && w[1] >= 1 && w[0] + w[2] <= d && w[1] + w[3] <= d;
                    // The district holding more of the yard's plots is its owner: the first for the first and third, the next for the others.
                    larger &= (k % 2 == 0) == (dx + dy == 0);
                }
            }
            once &= owners == 1;
        }
        check("a long yard across a district's edge is one district's work, once", once);
        check("...clipped to the plots that district's frame holds as its own, 1 to DISTRICT - 1", inside);
        check("...the district that holds more of its plots", larger);
        int[] whole = CityMap.workIn(new long[] { 100, 97, term[0], term[1] }, 0, 0);
        check("a yard wholly inside one district is that district's work as it was, its own box", whole != null && whole[0] == 100 && whole[1] == 97 && whole[2] == term[0] && whole[3] == term[1]
                && CityMap.workIn(new long[] { 100, 97, term[0], term[1] }, d, 0) == null);
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
