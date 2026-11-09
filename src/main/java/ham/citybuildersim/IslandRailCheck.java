package ham.citybuildersim;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ham.citybuildersim.IslandNetCheck.Land;

/**
 * The railway has a piece of its own on each piece of the city's ground the water parts, its track dealt by built weight and its yards on the
 * islands (RI1).
 *
 * WHAT THIS HAS TO PROVE (the sections below are numbered to match):
 *
 *   1. EACH LINKED ISLAND HAS A RAILWAY HUB AND TRACK OF ITS OWN. A home square and two islands (A, B) parted by sea, a Pieces of three,
 *      and a dry square C the runs' ground does not own (an unlinked island: not owned, so in no piece). With CityRuns.islandRail off the
 *      islands have highways and the railway is one piece with no track off home (the premise: the fault); on, each island has a rail hub -
 *      a railway line crossing (RAIL_AT mod CELL in both coordinates) on its own dry ground, the nearest to the piece's anchor - and track
 *      from it, every rail plot is on owned dry ground, and C has none.
 *
 *   2. THE TRACK IS DEALT BY WEIGHT AS THE HIGHWAYS ARE. With no yards each island's track is the floor of the model's track times its weight
 *      over the total (the home piece counted at least 1), the home piece the rest - against the rule's own formula fed the fixture's
 *      weights; a month whose weights moved and whose track did not (the ground's version moved, so the railway is laid again) moves
 *      nothing; more track grows every piece to its new share; less track takes plots back from the newest end (every arm laid is a prefix
 *      of what it was) to the new shares and no further.
 *
 *   3. YARDS. The model asks 3 yards: yard 0 at home beside the mine, yards 1 and 2 on A's and B's own track, nearest their hubs (their
 *      middles on their pieces); an island with a yard has its line grown to its edges, till every arm waits, as a station's line is at home;
 *      the freight station is the home yard though an island yard is nearer the main line's end (the premise: the fixture causes it), and
 *      wayTo() of an island yard is null (the home yard's is not).
 *
 *   4. SIDINGS. A bulk works on island A within SIDING_REACH of A's line gets a siding from A's line, every plot of it on A's dry ground;
 *      with islandRail off (the premise) A has no line and the works gets none.
 *
 *   5. A PURCHASE JOINING AN ISLAND TO HOME FREEZES ITS RAILWAY. A is joined to the home piece (B becomes the map's second piece): the month
 *      it joins moves nothing; as the track grows, A's rail net is exactly as it was and home goes on growing, B's share dealt on.
 *
 *   6. ONE PIECE IS THE RAILWAY AS IT WAS. On a ground in one piece (ONE_PIECE) islandRail on and off give the same signature() and the same
 *      rail plots, yards and sidings.
 *
 *   7. THE SIDECAR KEEPS EVERY PIECE'S RAILWAY. Runs with island rail, yards and a siding, written and read back, are same(), with the
 *      pieces' hubs and counts and the corridors' pieces and roles, fill a box alike, and laid again to the same inputs move nothing.
 *
 * WHY THIS EXISTS: CL5 gave every piece of the city's ground the water parts a highway net of its own and left the railway the home
 * piece's alone, so a city built on islands had no track and no yard on any but the founding site's (Jerus ticked "Rail on islands",
 * 2026-10-31: CL5-C's call 1, "the railway on other islands: its own track and yards from each piece's hub"). A fixture CAUSES each case:
 * a ground of islands, weights, a mine, a works, a purchase.
 */
public class IslandRailCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-110s %s%n", label, ok ? "ok" : "FAIL");
    }

    static final long SITE = IslandNetCheck.SITE;

    /** An island's half-side in plots. */
    static final long ISLE = 150;

    /** Sea between the home square and an island that is wider than a siding reaches (section 4), and one an island's yard is nearer the main line's end than the home yard is (section 3). */
    static final long GAP_FAR = CityRuns.SIDING_REACH + CityRuns.CELL, GAP_NEAR = 3L * CityRuns.CELL;

    /** The pieces' built weights: home, A, B. */
    static final long[] WEIGHTS = { 3, 2, 1 };

    /** The city's ground in three pieces, and in two once a purchase joins A to home: B is then the map's second piece. */
    static final class Sea implements CityRuns.Pieces {
        final long[] weights;
        final long[][] anchors;
        final List<Land.Dry> lands = new ArrayList<>();
        /** A purchase joined island A to the home piece. */
        boolean aJoined;

        Sea(long[] weights, long[][] anchors, Land.Dry... lands) {
            this.weights = weights;
            this.anchors = anchors;
            this.lands.addAll(Arrays.asList(lands));
        }

        @Override public int count() { return aJoined ? 2 : 3; }

        @Override public int pieceOf(long x, long y) {
            for (int p = 0; p < lands.size(); p++) {
                if (lands.get(p).at(x, y)) return !aJoined ? p : p == 2 ? 1 : 0;
            }
            return -1;
        }

        @Override public long weight(int p) {
            if (!aJoined) return p < weights.length ? weights[p] : 0;
            return p == 0 ? weights[0] + weights[1] : p == 1 ? weights[2] : 0;
        }

        @Override public long[] hub(int p) {
            if (!aJoined) return p < anchors.length ? anchors[p] : null;
            return p == 1 ? anchors[2] : null;
        }
    }

    /** The fixture: a home square with island A east of it and island B west, `gap` plots of sea between, and C, a dry square north that nobody owns. */
    static final class Fix {
        final long gap;
        final Land.Dry home, isleA, isleB;
        final long[] boxHome, boxA, boxB, boxC, anchorA, anchorB;
        final Land sea;
        final Sea pieces;

        Fix(long gap) {
            this.gap = gap;
            boxHome = new long[] { SITE - ISLE, SITE - ISLE, SITE + ISLE, SITE + ISLE };
            boxA = new long[] { SITE + ISLE + gap, SITE - ISLE, SITE + 3 * ISLE + gap, SITE + ISLE };
            boxB = new long[] { SITE - 3 * ISLE - gap, SITE - ISLE, SITE - ISLE - gap, SITE + ISLE };
            home = Land.rect(boxHome[0], boxHome[1], boxHome[2], boxHome[3]);
            isleA = Land.rect(boxA[0], boxA[1], boxA[2], boxA[3]);
            isleB = Land.rect(boxB[0], boxB[1], boxB[2], boxB[3]);
            long x0 = boxB[0] - 50, x1 = boxA[2] + 50, y0 = SITE - ISLE - 50, y1 = SITE + ISLE + 50;
            sea = new Land(x0, y0, x1, y1, true).add(home).add(isleA).add(isleB);
            boxC = new long[] { SITE - ISLE, y0 - gap - 2 * ISLE, SITE + ISLE, y0 - gap };
            // Each island's anchor: the first highway line crossing 10 plots or so in from the shore its hub is nearer.
            long ax = boxA[0] + 10, bx = boxB[2] - 10;
            while (Math.floorMod(ax - CityRuns.HIGHWAY_AT, CityRuns.CELL) != 0) ax++;
            while (Math.floorMod(bx - CityRuns.HIGHWAY_AT, CityRuns.CELL) != 0) bx--;
            anchorA = new long[] { ax, SITE };
            anchorB = new long[] { bx, SITE };
            pieces = new Sea(WEIGHTS.clone(), new long[][] { null, anchorA, anchorB }, home, isleA, isleB);
        }

        /** A fresh runs for a city whose world line leaves east, laid to `track` (and `highway`) with the yards, spurs, mines and works given. */
        CityRuns lay(long highway, long track, List<int[]> yards, int spurs, List<long[]> mines, List<long[]> works) {
            CityRuns runs = new CityRuns();
            runs.setGateway(0);
            again(runs, highway, track, yards, spurs, mines, works);
            return runs;
        }

        void again(CityRuns runs, long highway, long track, List<int[]> yards, int spurs, List<long[]> mines, List<long[]> works) {
            runs.layTo(highway, track, yards, spurs, sea, SITE, SITE, SITE, SITE, mines, works, pieces);
        }
    }

    static final List<int[]> NO_YARDS = new ArrayList<>();
    static final List<long[]> NONE = new ArrayList<>();

    /** The plots of the net's in the box {x0, y0, x1, y1}. */
    static long in(CityRuns.Net n, long[] box) {
        return IslandNetCheck.coveredIn(n, box[0], box[1], box[2], box[3]);
    }

    /** Whether plot (x, y) is in the box {x0, y0, x1, y1}. */
    static boolean inBox(long x, long y, long[] box) {
        return x >= box[0] && x <= box[2] && y >= box[1] && y <= box[3];
    }

    /** The rule's share of `track` for a piece of weight w among weights summing to `total` (CityRuns.deal(): floor(track * w / total)). */
    static long share(long track, long w, long total) {
        return java.math.BigInteger.valueOf(track).multiply(java.math.BigInteger.valueOf(w)).divide(java.math.BigInteger.valueOf(total)).longValue();
    }

    /** The weights' total, the home piece counted at least 1. */
    static long total(long[] w) {
        return Math.max(1, w[0]) + w[1] + w[2];
    }

    /** The distance from plot (x, y) to the box {x0, y0, x1, y1}: 0 inside it. */
    static double boxDistance(long[] box, long x, long y) {
        return Math.hypot(Math.max(0, Math.max(box[0] - x, x - box[2])), Math.max(0, Math.max(box[1] - y, y - box[3])));
    }

    /** The distance from (x, y) to the nearest railway line crossing on the dry ground of box (its corners and sides are dry: the fixture's islands are squares). */
    static double nearestCrossing(Land sea, long[] box, long ax, long ay) {
        double best = Double.MAX_VALUE;
        long cell = CityRuns.CELL;
        for (long y = ay - 4 * cell; y <= ay + 4 * cell; y++) {
            if (Math.floorMod(y - CityRuns.RAIL_AT, cell) != 0) continue;
            for (long x = ax - 4 * cell; x <= ax + 4 * cell; x++) {
                if (Math.floorMod(x - CityRuns.RAIL_AT, cell) != 0 || !inBox(x, y, box) || !CityRuns.dry(sea.at(x, y, -1))) continue;
                best = Math.min(best, Math.hypot(x - ax, y - ay));
            }
        }
        return best;
    }

    /** The plots of piece p of the net's that are a SPINE arm's of that piece, all four arms' ends: each arm of the piece's corridors waits (stoppedBy is not OPEN). */
    static boolean armsWait(CityRuns.Net n, int p) {
        for (int i : CityRuns.armsOf(n, p)) if (n.arms.get(i).stoppedBy == CityRuns.OPEN && !n.arms.get(i).joined) return false;
        return true;
    }

    public static void main(String[] args) {
        boolean wasIslandRail = CityRuns.islandRail;
        try {
            section1();
            section2();
            section3();
            section4();
            section5();
            section6();
            section7();
        } finally {
            CityRuns.islandRail = wasIslandRail;
        }
        System.out.println(fails == 0 ? "\nThe railway has a piece on each island." : "\n" + fails + " FAILED");
        System.exit(fails);
    }

    /* ============ 1. each linked island has a railway hub and track of its own ============ */
    static void section1() {
        System.out.println("--- 1. each linked island has a railway hub and track of its own ---");
        Fix f = new Fix(GAP_FAR);
        long highway = 600, track = 600;
        CityRuns.islandRail = false;
        CityRuns off = f.lay(highway, track, NO_YARDS, 0, NONE, NONE);
        CityRuns.islandRail = true;
        check("fixture (premise): with islandRail off the islands have highways, a net of their own each", off.highways.pieces() == 3
                && off.highways.piecePlots(1) > 0 && off.highways.piecePlots(2) > 0);
        check("...and the railway is one piece with track at home and none on either island", off.rail.pieces() == 1 && off.rail.plots > 0
                && in(off.rail, f.boxHome) == off.rail.plots && in(off.rail, f.boxA) == 0 && in(off.rail, f.boxB) == 0);
        CityRuns on = f.lay(highway, track, NO_YARDS, 0, NONE, NONE);
        CityRuns.Net rail = on.rail;
        System.out.printf("  %,d track plots: home %,d, A %,d, B %,d (%d pieces); hubs A %s, B %s%n", track, rail.piecePlots(0), rail.piecePlots(1), rail.piecePlots(2),
                rail.pieces(), Arrays.toString(rail.pieceHub(1)), Arrays.toString(rail.pieceHub(2)));
        check("islandRail on: the railway has three pieces, a hub for each island", rail.pieces() == 3 && rail.pieceHub(1) != null && rail.pieceHub(2) != null);
        long[][] boxes = { f.boxHome, f.boxA, f.boxB };
        long[][] anchors = { null, f.anchorA, f.anchorB };
        for (int p = 1; p <= 2; p++) {
            String name = p == 1 ? "A" : "B";
            long[] h = rail.pieceHub(p);
            check("island " + name + "'s hub is a railway line crossing (both coordinates RAIL_AT mod CELL) on its own dry ground", h != null
                    && Math.floorMod(h[0] - CityRuns.RAIL_AT, CityRuns.CELL) == 0 && Math.floorMod(h[1] - CityRuns.RAIL_AT, CityRuns.CELL) == 0
                    && f.pieces.pieceOf(h[0], h[1]) == p && CityRuns.dry(f.sea.at(h[0], h[1], -1)));
            double want = nearestCrossing(f.sea, boxes[p], anchors[p][0], anchors[p][1]);
            check("...the nearest to the piece's anchor: " + String.format("%.1f", want) + " plots from it", h != null
                    && Math.abs(Math.hypot(h[0] - anchors[p][0], h[1] - anchors[p][1]) - want) < 1e-9);
            check("...with track of its own, all on the island, counted as the piece's", rail.piecePlots(p) > 0 && in(rail, boxes[p]) == rail.piecePlots(p));
        }
        check("the home piece's railway is on the home square, counted as the piece's", rail.piecePlots(0) > 0 && in(rail, f.boxHome) == rail.piecePlots(0));
        check("every rail plot is on owned dry ground: the plots counted are the plots inside the ground and none is on salt", in(rail, new long[] { f.sea.x0, f.sea.y0, f.sea.x1, f.sea.y1 }) == rail.plots
                && IslandNetCheck.offGround(rail, f.sea) == 0 && rail.plots == CityRuns.countPlots(rail));
        check("the unowned dry square C has no rail, and the runs have no piece for it", in(rail, f.boxC) == 0 && in(on.highways, f.boxC) == 0 && f.pieces.pieceOf(f.boxC[0], f.boxC[1]) == -1);
        check("the highways were laid too, and rail runs on none of them (nothing is both)", on.highways.pieces() == 3 && on.highways.plots > 0);
    }

    /* ============ 2. the track is dealt by weight as the highways are ============ */
    static void section2() {
        System.out.println("\n--- 2. the track is dealt by weight as the highways are ---");
        Fix f = new Fix(GAP_FAR);
        CityRuns.islandRail = true;
        long track = 600;
        CityRuns runs = f.lay(0, track, NO_YARDS, 0, NONE, NONE);
        CityRuns.Net rail = runs.rail;
        long total = total(f.pieces.weights);
        long shareA = share(track, f.pieces.weights[1], total), shareB = share(track, f.pieces.weights[2], total), shareH = track - shareA - shareB;
        System.out.printf("  %,d track plots, weights %s: shares home %,d, A %,d, B %,d; laid %,d, %,d, %,d (total %,d)%n", track, Arrays.toString(f.pieces.weights),
                shareH, shareA, shareB, rail.piecePlots(0), rail.piecePlots(1), rail.piecePlots(2), rail.plots);
        check("with no yards each island's track is its floor share of the model's, by the rule's own formula", rail.piecePlots(1) == shareA && rail.piecePlots(2) == shareB);
        check("...and the home piece has the rest, so all the model's track is laid (trackSurplus is 0)", rail.piecePlots(0) == shareH && rail.plots == track && runs.trackSurplus() == 0);
        check("...no island's line was grown to its edges: with no yard it stops at its share", CityRuns.stuckAt(rail, 1) != f.sea.version() && CityRuns.stuckAt(rail, 2) != f.sea.version());
        // The weights drift, the ground's version moves so the railway is laid again, the track is the same: nothing moves.
        long before = runs.signature();
        Map<String, List<long[]>> armsBefore = IslandNetCheck.armsOf(rail);
        f.pieces.weights[0] = 1;
        f.pieces.weights[1] = 4;
        f.pieces.weights[2] = 4;
        long driftA = share(track, f.pieces.weights[1], total(f.pieces.weights)), driftB = share(track, f.pieces.weights[2], total(f.pieces.weights));
        check("fixture (premise): at the drifted weights the shares are not what is laid (A " + driftA + " against " + shareA + ", B " + driftB + " against " + shareB + ")",
                driftA != shareA && driftB != shareB && driftA + driftB < track);
        f.sea.version++;
        f.again(runs, 0, track, NO_YARDS, 0, NONE, NONE);
        check("the weights drift (3:2:1 to 1:4:4) and the railway is laid again for the same track: nothing moves (the signature is the same)",
                runs.signature() == before && IslandNetCheck.prefix(armsBefore, IslandNetCheck.armsOf(rail)) && IslandNetCheck.prefix(IslandNetCheck.armsOf(rail), armsBefore));
        check("...each piece has what it had, though its share is more or less now", rail.piecePlots(0) == shareH && rail.piecePlots(1) == shareA && rail.piecePlots(2) == shareB);
        System.arraycopy(WEIGHTS, 0, f.pieces.weights, 0, WEIGHTS.length);
        // More track grows each piece to its new share.
        long more = 900;
        Map<String, List<long[]>> armsSmall = IslandNetCheck.armsOf(rail);
        f.again(runs, 0, more, NO_YARDS, 0, NONE, NONE);
        long shareA2 = share(more, f.pieces.weights[1], total), shareB2 = share(more, f.pieces.weights[2], total), shareH2 = more - shareA2 - shareB2;
        System.out.printf("  %,d track plots: shares home %,d, A %,d, B %,d; laid %,d, %,d, %,d%n", more, shareH2, shareA2, shareB2, rail.piecePlots(0), rail.piecePlots(1), rail.piecePlots(2));
        check("more track (" + more + ") grows every piece to its new share", rail.piecePlots(0) == shareH2 && rail.piecePlots(1) == shareA2 && rail.piecePlots(2) == shareB2);
        check("...what was laid is kept: every arm is a prefix of what it is now", IslandNetCheck.prefix(armsSmall, IslandNetCheck.armsOf(rail)));
        // Less track takes plots back from the newest end, to the new shares and no further.
        long less = 450;
        Map<String, List<long[]>> armsBig = IslandNetCheck.armsOf(rail);
        long shareA3 = share(less, f.pieces.weights[1], total), shareB3 = share(less, f.pieces.weights[2], total), shareH3 = less - shareA3 - shareB3;
        f.again(runs, 0, less, NO_YARDS, 0, NONE, NONE);
        System.out.printf("  %,d track plots: shares home %,d, A %,d, B %,d; laid %,d, %,d, %,d%n", less, shareH3, shareA3, shareB3, rail.piecePlots(0), rail.piecePlots(1), rail.piecePlots(2));
        check("less track (" + less + ") takes plots back to the new shares, none below them", rail.piecePlots(0) == shareH3 && rail.piecePlots(1) == shareA3 && rail.piecePlots(2) == shareB3
                && rail.plots == less);
        check("...from the newest end: every arm that is left is a prefix of what it was", IslandNetCheck.prefix(IslandNetCheck.armsOf(rail), armsBig));
        boolean hubKept = true;
        for (int p = 1; p <= 2; p++) hubKept &= CityRuns.covered(rail, rail.pieceHub(p)[0], rail.pieceHub(p)[1]);
        check("...each island's hub plot is still laid (the piece's last plot to go)", hubKept && rail.pieces() == 3);
        // And to nothing: every piece's track goes, the net is reset with the last plot, the islands' pieces with it.
        f.again(runs, 0, 0, NO_YARDS, 0, NONE, NONE);
        check("with no track at all the railway is taken back to nothing and reset: no plot, no piece, not started", rail.plots == 0 && !rail.started && rail.pieces() == 1);
        f.again(runs, 0, track, NO_YARDS, 0, NONE, NONE);
        check("...and laid again from nothing: the islands open afresh, each with its share", rail.pieces() == 3 && rail.piecePlots(1) == shareA && rail.piecePlots(2) == shareB && rail.plots == track);
    }

    /* ============ 3. yards ============ */
    static void section3() {
        System.out.println("\n--- 3. yards ---");
        Fix f = new Fix(GAP_NEAR);
        CityRuns.islandRail = true;
        int[] size = { 24, 24 };
        List<int[]> sizes = new ArrayList<>(List.of(size, size, size));
        // The mine is at the home square's west end, far from the main line's end (the world line leaves east).
        List<long[]> mines = new ArrayList<>(List.<long[]>of(new long[] { SITE - ISLE + 10, SITE + 20, SITE - ISLE + 20, SITE + 30 }));
        long track = 600;
        CityRuns runs = f.lay(0, track, sizes, 0, mines, NONE);
        CityRuns.Net rail = runs.rail;
        List<long[]> yards = runs.yards();
        System.out.printf("  %d yards asked, %d laid: ", sizes.size(), yards.size());
        for (long[] y : yards) System.out.printf("[%d,%d %dx%d] ", y[0], y[1], y[2], y[3]);
        System.out.printf("%n  pieces home %,d, A %,d, B %,d plots%n", rail.piecePlots(0), rail.piecePlots(1), rail.piecePlots(2));
        check("the model asks 3 yards and all 3 are laid (yardsShort is 0)", yards.size() == 3 && runs.yardsShort() == 0);
        long[] y0 = yards.size() > 0 ? yards.get(0) : null, y1 = yards.size() > 1 ? yards.get(1) : null, y2 = yards.size() > 2 ? yards.get(2) : null;
        check("yard 0 is at home, on the first spot in the nearest-a-mine order", y0 != null && f.pieces.pieceOf(y0[0] + y0[2] / 2, y0[1] + y0[3] / 2) == 0
                && Arrays.equals(runs.yardSpots(size[0], size[1], mines).get(0), y0));
        long[][] hubs = { null, rail.pieceHub(1), rail.pieceHub(2) };
        long[][] ys = { null, y1, y2 };
        for (int p = 1; p <= 2; p++) {
            long[] y = ys[p];
            String name = p == 1 ? "A" : "B";
            check("yard " + p + " is on island " + name + " (its middle on the piece), YARD_LONG along its line and track all the length of it", y != null
                    && f.pieces.pieceOf(y[0] + y[2] / 2, y[1] + y[3] / 2) == p && Math.max(y[2], y[3]) == CityRuns.YARD_LONG
                    && trackIn(rail, y) >= CityRuns.YARD_LONG);
            // The nearest its hub: no box of the longest tier on the piece's track, from the hub outward, is nearer.
            double nearest = Double.MAX_VALUE;
            for (long[] b : runs.yardSpotsNear(size[0], size[1], hubs[p][0], hubs[p][1])) {
                if (Math.max(b[2], b[3]) == CityRuns.YARD_LONG && inBox(b[0], b[1], boxOf(f, p)) && inBox(b[0] + b[2] - 1, b[1] + b[3] - 1, boxOf(f, p))) {
                    nearest = Math.min(nearest, Math.hypot(b[0] + b[2] / 2.0 - hubs[p][0], b[1] + b[3] / 2.0 - hubs[p][1]));
                }
            }
            check("...the nearest its hub of the YARD_LONG boxes on the island's track (" + String.format("%.1f", nearest) + " plots)", y != null
                    && Math.abs(Math.hypot(y[0] + y[2] / 2.0 - hubs[p][0], y[1] + y[3] / 2.0 - hubs[p][1]) - nearest) < 1e-9);
            check("...island " + name + "'s line is grown till every arm waits (to its edges, as a station's line is at home)", CityRuns.stuckAt(rail, p) == f.sea.version()
                    && armsWait(rail, p));
        }
        // The premise: an island yard is nearer the main line's end than the home yard, so a station that did not stay home would be an island's.
        CityRuns.Arm main = rail.arms.get(runs.mainArm());
        long[] nearestOfAll = null;
        double bestD = Double.MAX_VALUE;
        for (long[] y : yards) {
            double d = Math.hypot(y[0] + y[2] / 2.0 - main.x, y[1] + y[3] / 2.0 - main.y);
            if (d < bestD) { bestD = d; nearestOfAll = y; }
        }
        check("fixture (premise): the yard nearest the main line's end is not the home yard but one on an island", nearestOfAll != null && y0 != null && !Arrays.equals(nearestOfAll, y0)
                && f.pieces.pieceOf(nearestOfAll[0] + nearestOfAll[2] / 2, nearestOfAll[1] + nearestOfAll[3] / 2) > 0);
        check("freightStation() is the home yard all the same", y0 != null && Arrays.equals(runs.freightStation(), y0));
        check("wayTo() of an island's yard is null: no train runs to it", y1 != null && y2 != null && runs.wayTo(y1) == null && runs.wayTo(y2) == null);
        check("...the home yard's way is not", y0 != null && runs.wayTo(y0) != null);
        // Laid again to the same inputs (the ground's version moved so that the railway is laid again): the yards, and everything, stay.
        long sig = runs.signature();
        f.sea.version++;
        f.again(runs, 0, track, sizes, 0, mines, NONE);
        check("laid again for the same inputs, the yards stand where they were and nothing moves", runs.signature() == sig && runs.yards().size() == 3);
    }

    /** Piece p's square of ground. */
    static long[] boxOf(Fix f, int p) {
        return p == 0 ? f.boxHome : p == 1 ? f.boxA : f.boxB;
    }

    /** Whether two nets have the same arms, stretch for stretch. */
    static boolean sameArms(CityRuns.Net a, CityRuns.Net b) {
        Map<String, List<long[]>> x = IslandNetCheck.armsOf(a), y = IslandNetCheck.armsOf(b);
        return x.size() == y.size() && IslandNetCheck.prefix(x, y) && IslandNetCheck.prefix(y, x);
    }

    /** The rail plots inside a yard's box. */
    static long trackIn(CityRuns.Net rail, long[] y) {
        return IslandNetCheck.coveredIn(rail, y[0], y[1], y[0] + y[2] - 1, y[1] + y[3] - 1);
    }

    /* ============ 4. sidings ============ */
    static void section4() {
        System.out.println("\n--- 4. sidings ---");
        Fix f = new Fix(GAP_FAR);
        // A bulk works on island A, a third of the way across it and 60 plots north of its row: within SIDING_REACH of A's line, and beyond it of home's.
        long wx = f.boxA[0] + 100, wy = SITE - 90;
        List<long[]> works = new ArrayList<>(List.<long[]>of(new long[] { wx, wy, wx + 19, wy + 19 }));
        long track = 3000;   // more than the ground can hold: every line waits at its edges and the track left over pays for the siding
        CityRuns.islandRail = false;
        CityRuns off = f.lay(0, track, NO_YARDS, 1, NONE, works);
        CityRuns.islandRail = true;
        check("fixture (premise): with islandRail off A has no line and the works is no nearer a line than the sea parts it: it gets no siding", off.rail.pieces() == 1
                && in(off.rail, f.boxA) == 0 && off.sidingsLaid() == 0 && off.rail.plots < track);
        CityRuns on = f.lay(0, track, NO_YARDS, 1, NONE, works);
        CityRuns.Net rail = on.rail;
        System.out.printf("  works %s on A: A's line %,d plots, %d sidings, rail plots %,d of %,d%n", Arrays.toString(works.get(0)), rail.piecePlots(1), on.sidingsLaid(), rail.plots, track);
        check("with it on, the works gets a siding", on.sidingsLaid() == 1 && on.sidings().size() == 1);
        long[] siding = on.sidings().isEmpty() ? null : on.sidings().get(0);
        int[] loc = siding == null ? null : CityRuns.locate(rail, siding[1], siding[2]);
        check("...from A's line: it starts on a plot of an arm of A's piece", loc != null && CityRuns.pieceOf(rail, rail.arms.get(loc[0])) == 1);
        boolean onA = siding != null;
        if (siding != null) {
            CityRuns.Arm a = rail.arms.get((int) siding[0]);
            for (int s = 0; s < a.segs; s++) {
                int h = a.segHead[s];
                for (int k = 1; k <= a.segSteps[s]; k++) {
                    long px = a.segX[s] + (long) CityRuns.DX[h] * k, py = a.segY[s] + (long) CityRuns.DY[h] * k;
                    onA &= inBox(px, py, f.boxA) && CityRuns.dry(f.sea.at(px, py, -1));
                }
            }
        }
        check("...every plot of it on A's dry ground", onA && IslandNetCheck.offGround(rail, f.sea) == 0);
        check("...and it ends at the works", siding != null && boxDistance(works.get(0), siding[3], siding[4]) <= CityRuns.SERVED_WITHIN + 1);
        check("the ways are the net's: the siding is an arm of the SIDING corridor, after the pieces' arms", siding != null && rail.arms.get((int) siding[0]).corridor == CityRuns.SIDING
                && siding[0] > CityRuns.armsOf(rail, 2)[CityRuns.armsOf(rail, 2).length - 1]);
        check("the home piece has no siding (no works near it)", on.sidings().size() == 1);
    }

    /* ============ 5. a purchase joining an island to home freezes its railway ============ */
    static void section5() {
        System.out.println("\n--- 5. a purchase joining an island to home freezes its railway ---");
        Fix f = new Fix(GAP_FAR);
        CityRuns.islandRail = true;
        long track = 600;
        CityRuns runs = f.lay(0, track, NO_YARDS, 0, NONE, NONE);
        CityRuns.Net rail = runs.rail;
        Map<String, List<long[]>> armsBefore = IslandNetCheck.armsOf(rail);
        long homeBefore = rail.piecePlots(0), aHeld = rail.piecePlots(1), bBefore = rail.piecePlots(2), sig = runs.signature();
        // A land bridge south of the lines, a purchase: A and home are one piece now, B the map's second.
        f.sea.add(Land.rect(f.boxHome[2] - 10, SITE + 40, f.boxA[0] + 10, SITE + 60));
        f.sea.version++;
        f.pieces.aJoined = true;
        f.again(runs, 0, track, NO_YARDS, 0, NONE, NONE);
        check("the month a purchase joins A to home, with the same track: nothing moves (the signature is the same)", runs.signature() == sig && rail.pieces() == 3);
        long more = track + 200;
        f.again(runs, 0, more, NO_YARDS, 0, NONE, NONE);
        Map<String, List<long[]>> armsAfter = IslandNetCheck.armsOf(rail);
        check("as the track grows, every arm laid before is a prefix of the arm after", IslandNetCheck.prefix(armsBefore, armsAfter));
        check("A's railway, the younger net on the home piece's ground, is frozen: its plots are what they were", rail.piecePlots(1) == aHeld);
        boolean aSame = true;
        for (int i : CityRuns.armsOf(rail, 1)) {
            CityRuns.Arm a = rail.arms.get(i);
            String key = a.sx + "," + a.sy + "," + a.home + "#1";
            List<long[]> was = armsBefore.get(key), is = armsAfter.get(key);
            aSame &= was != null && is != null && was.size() == is.size() && IslandNetCheck.prefix(Map.of("k", was), Map.of("k", is)) && IslandNetCheck.prefix(Map.of("k", is), Map.of("k", was));
        }
        check("...every arm of it exactly as it was", aSame);
        // The deal after the join: weights home 3 + A 2 = 5, B 1; the home piece's target is its share less what A holds.
        long totalNow = Math.max(1, f.pieces.weight(0)) + f.pieces.weight(1);
        long shareB = share(more, f.pieces.weight(1), totalNow), targetHome = more - shareB - aHeld;
        System.out.printf("  home %,d before, %,d after (target %,d); A held %,d; B %,d before, %,d after (share %,d)%n", homeBefore, rail.piecePlots(0), targetHome, aHeld,
                bBefore, rail.piecePlots(2), shareB);
        check("home goes on growing: past what it held, to its share less the frozen net's", rail.piecePlots(0) > homeBefore && rail.piecePlots(0) == targetHome);
        check("B is dealt on by its piece's weight (the map's second piece now): its share of the new track", rail.piecePlots(2) == shareB && rail.piecePlots(2) > bBefore);
        check("the model's track is all laid", rail.plots == more && runs.trackSurplus() == 0);
    }

    /* ============ 6. one piece is the railway as it was ============ */
    static void section6() {
        System.out.println("\n--- 6. one piece is the railway as it was ---");
        Fix f = new Fix(GAP_NEAR);
        int[] size = { 24, 24 };
        List<int[]> sizes = new ArrayList<>(List.of(size, size));
        List<long[]> mines = new ArrayList<>(List.<long[]>of(new long[] { SITE - ISLE + 10, SITE + 20, SITE - ISLE + 20, SITE + 30 }));
        long wx = SITE + 60, wy = SITE - 100;
        List<long[]> works = new ArrayList<>(List.<long[]>of(new long[] { wx, wy, wx + 19, wy + 19 }));
        long track = 900;
        CityRuns[] runs = new CityRuns[2];
        for (int k = 0; k < 2; k++) {
            CityRuns.islandRail = k == 1;
            runs[k] = new CityRuns();
            runs[k].setGateway(0);
            runs[k].layTo(0, track, sizes, 1, f.sea, SITE, SITE, SITE, SITE, mines, works, CityRuns.ONE_PIECE);
        }
        CityRuns.islandRail = true;
        CityRuns off = runs[0], on = runs[1];
        System.out.printf("  one piece: %,d rail plots, %d yards, %d sidings%n", on.rail.plots, on.yards().size(), on.sidingsLaid());
        check("fixture: a railway with a yard, a siding and track on the home square, and none on the islands (the ground is one piece)", on.rail.plots > 0 && on.yards().size() == 2
                && on.sidingsLaid() == 1 && in(on.rail, f.boxA) == 0 && in(on.rail, f.boxB) == 0 && on.rail.pieces() == 1);
        check("islandRail on and off: the same signature()", on.signature() == off.signature());
        boolean yardsSame = on.yards().size() == off.yards().size();
        for (int i = 0; yardsSame && i < on.yards().size(); i++) yardsSame &= Arrays.equals(on.yards().get(i), off.yards().get(i));
        check("...the same rail plots, yards and sidings", on.rail.plots == off.rail.plots && yardsSame && on.sidings().size() == off.sidings().size()
                && sameArms(on.rail, off.rail) && on.rail.corridors() == off.rail.corridors() && on.same(off));
    }

    /* ============ 7. the sidecar ============ */
    static void section7() {
        System.out.println("\n--- 7. the sidecar keeps every piece's railway ---");
        Fix f = new Fix(GAP_NEAR);
        CityRuns.islandRail = true;
        int[] size = { 24, 24 };
        List<int[]> sizes = new ArrayList<>(List.of(size, size, size));
        List<long[]> mines = new ArrayList<>(List.<long[]>of(new long[] { SITE - ISLE + 10, SITE + 20, SITE - ISLE + 20, SITE + 30 }));
        long wx = f.boxA[0] + 100, wy = SITE - 90;
        List<long[]> works = new ArrayList<>(List.<long[]>of(new long[] { wx, wy, wx + 19, wy + 19 }));
        long track = 3000;
        CityRuns kept = f.lay(0, track, sizes, 1, mines, works);
        System.out.printf("  %d pieces, %,d rail plots (home %,d, A %,d, B %,d), %d yards, %d sidings%n", kept.rail.pieces(), kept.rail.plots, kept.rail.piecePlots(0),
                kept.rail.piecePlots(1), kept.rail.piecePlots(2), kept.yards().size(), kept.sidingsLaid());
        check("fixture: a railway on three pieces with 3 yards and a siding", kept.rail.pieces() == 3 && kept.yards().size() == 3 && kept.sidingsLaid() == 1);
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            kept.write(new DataOutputStream(bytes), CityMap.FORMAT);
            CityRuns back = CityRuns.read(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())), CityMap.FORMAT);
            if (back != null) back.setGateway(kept.gateway());   // the gateway is the map's to keep, not the runs'
            check("written and read at FORMAT " + CityMap.FORMAT + ", the runs are the same (hubs, corridors' pieces and roles, arms, yards)", back != null && kept.same(back));
            if (back != null) {
                CityRuns.Net a = kept.rail, b = back.rail;
                check("...the railway's pieces kept: their hubs and their counts", b.pieces() == 3 && Arrays.equals(a.pieceHub(1), b.pieceHub(1)) && Arrays.equals(a.pieceHub(2), b.pieceHub(2))
                        && a.piecePlots(0) == b.piecePlots(0) && a.piecePlots(1) == b.piecePlots(1) && a.piecePlots(2) == b.piecePlots(2));
                boolean roles = a.corridors.size() == b.corridors.size();
                for (int i = 0; roles && i < a.corridors.size(); i++) {
                    roles = CityRuns.corridorRole(a.corridors.get(i)) == CityRuns.corridorRole(b.corridors.get(i)) && CityRuns.corridorPiece(a.corridors.get(i)) == CityRuns.corridorPiece(b.corridors.get(i));
                }
                check("...the corridors' pieces and roles (the ways' placeholders 2 and 3, then each island's two spines)", roles && b.corridors.size() >= 8
                        && CityRuns.corridorPiece(b.corridors.get(4)) == 1 && CityRuns.corridorPiece(b.corridors.get(6)) == 2 && CityRuns.corridorRole(b.corridors.get(4)) == CityRuns.SPINE);
                int w = (int) (f.sea.x1 - f.sea.x0 + 1), h = (int) (f.sea.y1 - f.sea.y0 + 1);
                byte[] f1 = new byte[w * h], f2 = new byte[w * h], m1 = new byte[w * h], m2 = new byte[w * h];
                kept.fill(f.sea.x0, f.sea.y0, w, h, f1, m1, false);
                back.fill(f.sea.x0, f.sea.y0, w, h, f2, m2, false);
                check("...and they fill a box alike", Arrays.equals(f1, f2) && Arrays.equals(m1, m2));
                long sig = kept.signature();
                f.again(back, 0, track, sizes, 1, mines, works);
                check("the runs read back, laid again for the same inputs, move nothing: the signature, the yards and the siding are what they were",
                        back.signature() == sig && back.yards().size() == 3 && back.sidingsLaid() == 1 && kept.same(back));
            }
        } catch (java.io.IOException e) {
            check("the sidecar is written and read: " + e, false);
        }
    }
}
