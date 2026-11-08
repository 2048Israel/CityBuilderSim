package ham.citybuildersim;

import java.util.ArrayDeque;
import java.util.Arrays;

/**
 * MF2 slice D, item D2 (2026-11-08): A DIAGONAL HIGHWAY CUTS THE BLOCKS - the fixture, red on main, and the design for its fix.
 * A PROBE, not part of the suite: nothing in `src/` calls it. Compile and run it from the worktree root, after
 * `./batch-check.sh --fast` has built target/classes (it reads PlanCheck's package-private helpers, so it is in their package):
 *   javac -nowarn -cp "target/classes;$(cat target/classpath.txt)" -d target/p/cls docs/queue/MF2/MF2DiagonalProbe.java
 *   java -Dcbs.earth=... -Dcbs.world=... -cp "target/p/cls;target/classes;$(cat target/classpath.txt)" ham.citybuildersim.MF2DiagonalProbe
 * Its exit is 1 while the plan has the fault, so it belongs in PlanCheck as section 20 the day the plan lays the frontage avenue below.
 *
 * WHAT IT SHOWS (Jerus's Bergen city, picture 3, "the highway cuts the blocks"). A full district, Jerus's mix at the fill PlanCheck's
 * section 4 calls full, has a highway across it at 45 degrees (a 4-connected staircase of FIXED_HIGHWAY plots, CityRuns' convention, off
 * the avenues' arms). The plan's blocks are a grid. Along both sides of the highway's verge (H5: no building plot touches a highway
 * plot) the grid's blocks are cut into triangles too small for a building; the wedges stay empty, and the grid's streets meet the
 * verge with nothing there but the way beneath the highway.
 *   (1) the share of owned dry plots in the open cells within STRIP plots of the verge, on each side, that hold a building or a street,
 *       against the same share over the district's other plots: the strip is used like the rest within STRIP_MARGIN, or the fault stands;
 *   (2) the street plots of the strip that end at the verge: a street plot just outside the verge that goes on away from the highway and
 *       whose next plot towards it is the verge, and no street of the strip runs along the highway to join it: none may.
 *
 * THE DESIGN THE FIX WANTS (the starred way; not built - see the report). A FRONTAGE AVENUE along each side of a diagonal highway, just
 * outside its verge, made of CL4's avenue machinery (a staircase street of ROLE_ARTERIAL, blocks built along it, junctions with the
 * grid's streets under the + floor). It was not built in MF2 because the machinery assumes what the highway breaks:
 *   - an avenue is laid when its cell OPENS from inputs that never change (the origin, the cell's place, the ground the plan has then),
 *     so no later month moves a box (CL4, THE ORDER OF ARRIVAL); a highway arm is a RUN laid month by month (THE RUNS DATED, a plot
 *     a month as the arm grows) and the line of a frontage avenue is not known before the arm is: a frontage avenue laid on the
 *     records of a month would have to take plots standing boxes hold (MF1 moves a box off the run's own verge, not off a street
 *     two plots from it), or be laid on the arm's final line from the first month and kept off by every arrival until the arm comes
 *     (the line is today's `fixed`, the plan's end state, which the plan is not allowed to read before the records - FX7);
 *   - the arms are four, from the founding site's lattice node, so a crossing with the lattice falls on a node every LATTICE steps and the
 *     + floor along it is the walk of avFloor() over steps; a frontage line is no arm: it needs its own origin (the lattice node
 *     nearest its start, as the arms'), its own heading code (the painter reads the mark's heading, not an origin) and a line of its
 *     own in avMark/avOuter/avList/avDone/avKept, where avKept keys on arm << 28 | step - an arm is 0 to 3 in all of it;
 *   - a frontage line offset from the lattice by up to LATTICE - 1 plots leaves a wedge between it and the verge of up to that
 *     width, which no building fills unless the outer plots' rule (no building on an outer plot) is the line's own too.
 * A design that fits: the frontage line is the lattice-aligned diagonal nearest outside the verge on each side (its offset from the
 * highway's staircase 2 to LATTICE + 1 plots), laid as a FIFTH TO N-TH ARM of the avenue tables (arms indexed beyond 3, each its own
 * origin), when the cell opens after the arm's plots are in `fx`, with the streets of the cell that meet it stopping a plot short
 * (avFloor()'s rule) - and for a highway laid across built ground, the cells already open keep their grid: the frontage avenue
 * takes the free plots of the strip (MF1's vacate() does not apply: no box stands on it), and the wedge beyond it is built as any
 * avenue's is. The streets keep passing beneath the highway (UNDER) as they do: "no street crosses at grade" is what H5 has
 * always meant by a street beneath a deck.
 */
public class MF2DiagonalProbe {

    /** How far from the verge the strip reaches, in plots beyond it: 8, the width of one block of the lattice (TilePainter.JUNCTION_APART). */
    static final int STRIP = TilePainter.JUNCTION_APART;

    /**
     * The most the highway may cost the strip, in share of its plots (a building or a street) used, against the same district with no highway (the same
     * fill, the same arrivals; the verge, which H5 bars to buildings, is counted out of both): 0.02, 139 of the strip's 6,936 plots. A frontage street
     * along the verge would leave each block it cuts one corner triangle, at most LATTICE x LATTICE / 2 = 32 plots, and the strip holds about 108 blocks
     * (6,936 / 64) of which a corner triangle is empty in a few; 0.02 is four of them. Main loses 0.085, some 590 plots of the blocks the
     * highway cuts, with nothing on them. (The cells' own spread of the used share is 0.20: one cell differs from the next by far more, so the margin is
     * not drawn from it.)
     */
    static final double STRIP_MARGIN = 0.02;

    /** The highway's offset, in plots: its staircase is the plots with y = x - OFFSET and y = x - OFFSET - 1, off the avenues' arms (which lie on x - y = 0). */
    static final int OFFSET = 37;

    public static void main(String[] args) {
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        PlanCheck.out.println("\n--- MF2 D2. a diagonal highway through a full district: the strips beside its verge are used like the rest, and no street ends at the verge ---");
        final int n = DistrictPlan.FRAME;
        DistrictPlan.Input in = PlanCheck.flatMix(types, PlanCheck.FULL_FILL, true, false);
        int highwayPlots = 0;
        for (int x = 0; x < n; x++) {
            for (int dy = 0; dy <= 1; dy++) {
                int y = x - OFFSET - dy;
                if (y < 0 || y >= n) continue;
                in.fixed[y * n + x] = DistrictPlan.FIXED_HIGHWAY;
                highwayPlots++;
            }
        }
        DistrictPlan p = DistrictPlan.make(in);
        // The same district with no highway: what the strip's plots hold when nothing cuts them.
        DistrictPlan control = DistrictPlan.make(PlanCheck.flatMix(types, PlanCheck.FULL_FILL, true, false));
        // Chebyshev distance to the highway's plots (8-connected BFS from them): 0 on it, 1 on its verge, 2 to 1 + STRIP the strip.
        int[] d = new int[DistrictPlan.AREA];
        Arrays.fill(d, Integer.MAX_VALUE);
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int q = 0; q < DistrictPlan.AREA; q++) if (in.fixed[q] == DistrictPlan.FIXED_HIGHWAY) { d[q] = 0; queue.add(q); }
        while (!queue.isEmpty()) {
            int q = queue.poll(), x = q % n, y = q / n;
            for (int yy = Math.max(0, y - 1); yy <= Math.min(n - 1, y + 1); yy++) {
                for (int xx = Math.max(0, x - 1); xx <= Math.min(n - 1, x + 1); xx++) {
                    int a = yy * n + xx;
                    if (d[a] != Integer.MAX_VALUE) continue;
                    d[a] = d[q] + 1;
                    queue.add(a);
                }
            }
        }
        // The plots a building covers.
        boolean[] built = new boolean[DistrictPlan.AREA];
        for (int i = 0; i < p.buildings; i++) for (int y = p.by[i]; y < p.by[i] + p.bh[i]; y++) for (int x = p.bx[i]; x < p.bx[i] + p.bw[i]; x++) built[y * n + x] = true;
        boolean[] builtControl = new boolean[DistrictPlan.AREA];
        for (int i = 0; i < control.buildings; i++) for (int y = control.by[i]; y < control.by[i] + control.bh[i]; y++) for (int x = control.bx[i]; x < control.bx[i] + control.bw[i]; x++) builtControl[y * n + x] = true;
        long stripOwned = 0, stripUsed = 0, restOwned = 0, restUsed = 0, controlUsed = 0;
        final int cells = DistrictPlan.CELLS_A_SIDE, cell = DistrictPlan.CELL;
        long[] cellOwned = new long[DistrictPlan.CELLS], cellUsed = new long[DistrictPlan.CELLS];
        for (int q = 0; q < DistrictPlan.AREA; q++) {
            int x = q % n, y = q / n;
            if (!in.owned[q] || d[q] <= 1 || !PlanCheck.inOpenBox(p, x, y)) continue;
            boolean used = built[q] || p.isStreet(q);
            if (d[q] <= 1 + STRIP) { stripOwned++; if (used) stripUsed++; if (builtControl[q] || control.isStreet(q)) controlUsed++; }
            else {
                restOwned++;
                if (used) restUsed++;
                int c = Math.min(cells - 1, x / cell) + cells * Math.min(cells - 1, y / cell);
                cellOwned[c]++;
                if (used) cellUsed[c]++;
            }
        }
        double stripShare = stripOwned == 0 ? 0 : (double) stripUsed / stripOwned, restShare = restOwned == 0 ? 0 : (double) restUsed / restOwned;
        // What the highway costs the strip: its plots' used share in the same district with no highway, less what they hold with it.
        double controlShare = stripOwned == 0 ? 0 : (double) controlUsed / stripOwned, lost = controlShare - stripShare;
        // THE STREETS THAT END AT THE VERGE: the street plots within the strip's inner two rings (distance 2 and 3 from the highway), 4-connected into
        // pieces; a piece is a frontage street when it is as long as two blocks (2 x LATTICE plots) - a grid street's own plots there are two or three.
        // Every other piece that has a plot at distance 2 and a street plot at distance 4 beside one of its own is a grid street that runs to the verge
        // and meets no street along it.
        int[] piece = new int[DistrictPlan.AREA];
        int pieces = 0, frontage = 0, ends = 0, reaching = 0;
        for (int q = 0; q < DistrictPlan.AREA; q++) {
            if ((d[q] != 2 && d[q] != 3) || !p.isStreet(q) || p.kindAt(q) == DistrictPlan.UNDER || piece[q] != 0) continue;
            pieces++;
            int size = 0;
            boolean atVerge = false, arrives = false;
            ArrayDeque<Integer> walk = new ArrayDeque<>();
            piece[q] = pieces;
            walk.add(q);
            while (!walk.isEmpty()) {
                int u = walk.poll(), ux = u % n, uy = u / n;
                size++;
                if (d[u] == 2) atVerge = true;
                for (int k = 0; k < 4; k++) {
                    int a = (uy + TilePainter.DY[k]) * n + ux + TilePainter.DX[k];
                    if (a < 0 || a >= DistrictPlan.AREA || !p.isStreet(a) || p.kindAt(a) == DistrictPlan.UNDER) continue;
                    if (d[a] == 4) arrives = true;
                    if ((d[a] == 2 || d[a] == 3) && piece[a] == 0) { piece[a] = pieces; walk.add(a); }
                }
            }
            if (size >= 2 * TilePainter.JUNCTION_APART) { frontage++; continue; }
            if (atVerge && arrives) ends++;
            if (atVerge) reaching++;
        }
        PlanCheck.out.printf("      the plan: %,d buildings, %d cells open, %d highway plots; the strip (%d plots beyond the verge): %,d owned plots, %.1f%% used; the rest %,d plots, %.1f%% used; the same plots with no highway %.1f%% (lost %.1f points)%n",
                p.buildings, p.cellsOpen, highwayPlots, STRIP, stripOwned, 100 * stripShare, restOwned, 100 * restShare, 100 * controlShare, 100 * lost);
        PlanCheck.out.printf("      street pieces in the strip's inner rings: %d, %d of them as long as two blocks (a street along the highway); %d reach the verge, %d of those a grid street arriving from away and meeting none%n",
                pieces, frontage, reaching, ends);
        PlanCheck.check("fixture: the district is full (every cell open) and the highway runs through it across its cells, with ground on both sides of it", p.cellsOpen == DistrictPlan.CELLS && stripOwned > 1000 && restOwned > 1000);
        PlanCheck.check("the strips beside the highway's verge are used as the same district uses them with no highway: " + String.format("%.1f%% against %.1f%% (the rest of the district %.1f%%)", 100 * stripShare, 100 * controlShare, 100 * restShare)
                + ", the loss within STRIP_MARGIN (" + STRIP_MARGIN + ")", lost <= STRIP_MARGIN);
        PlanCheck.check("no grid street runs to the verge and ends there without a street along the highway to join (" + ends + " of the " + reaching + " pieces that reach it)", ends == 0);
        System.out.println(PlanCheck.fails == 0 ? "\nAll checks passed." : "\n" + PlanCheck.fails + " FAILED");
        System.exit(PlanCheck.fails == 0 ? 0 : 1);
    }
}
