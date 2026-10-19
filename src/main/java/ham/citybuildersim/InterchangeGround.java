package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The ground a highway interchange keeps: a 19 x 19 plot reserve centred on
 * each highway x highway crossing (a cloverleaf), an 11 x 11 one beside each
 * highway arm that ends at another highway (a trumpet) - and, where the box's
 * ground is short, none, so the crossing stays a stack. The map's alone:
 * nothing in the model reads it, and nothing of it is saved.
 *
 * WHY THIS EXISTS (batch FR7, slice 4; spec-freight 4.6, D19; FR0-L). FR0
 * drew every crossing as a stack, because a cloverleaf needs ground that the
 * runs, laid city-wide before any district plan, could not keep: nothing in
 * the runs knew where a cell's streets would stand, so a loop drawn beside the
 * crossing would have run over a house. The spec's answer is the one H5 gave
 * the verges: the reserve is laid WITH THE CORRIDOR, before cells open round
 * it - here as a pure function of the runs and the ground, found for the
 * region asked about (sitesIn(), through CityRuns.reservesIn()) and kept
 * against the runs' version, the ground's and the map's works, so a sidecar
 * carries nothing new - and a plan treats it as street ground
 * (CityRuns.F_RESERVE, DistrictPlan.FIXED_RESERVE): no lot, no building, no
 * street across it. It is the ground of a loop, not a building (D19).
 *
 * LOCAL, NOT CITY-WIDE. A city of billions has crossings by the hundred
 * thousand, and a reserve is only asked after where a plan or a tile is drawn:
 * a region's sites are found from the highways' plots in it (CityRuns.fill()
 * over the region and a margin), never from a list of every interchange.
 *
 * THE RULE. A highway plot with a highway on all four sides is a cloverleaf's
 * crossing, one with a highway on three a trumpet's T (its stub the side with
 * none opposite it); a 45-degree stretch beside either (a highway on a
 * diagonal neighbour) makes it neither. The box (19 or 11 plots a side) gets
 * its reserve only when every plot of it that is not a highway's own is owned
 * dry ground, carries no track, no yard, is on no cell's arterial line (the
 * plans' grid stays whole), and no work the map has already placed there - a
 * mine on its site, a terminal on the shore (the Guard) - stands in it.
 * Anything less and the crossing stays a stack; every candidate is kept with
 * whether it got its reserve (Site.got()).
 *
 * @author Jerus
 */
public final class InterchangeGround {

    private InterchangeGround() { }

    /** A reserve's two kinds: a cloverleaf at a highway x highway crossing, a trumpet at an arm that ends at another highway. */
    public static final int CLOVERLEAF = 0, TRUMPET = 1;

    /** A cloverleaf's reserve, in plots a side: 19, 570 m square, about 32 ha - about 20 acres a quadrant (spec-freight 4.6, [R82]). */
    public static final int CLOVERLEAF_SIDE = 19;

    /** A trumpet's reserve, in plots a side: 11, about 11 ha (spec-freight 4.6, est.). */
    public static final int TRUMPET_SIDE = 11;

    /** How far a box reaches from its junction plot, in plots: a cloverleaf's 9 either way - the most of the two kinds (a trumpet's runs 11 out on its stub's side). */
    static final int REACH = TRUMPET_SIDE;

    /**
     * What the map has already placed that a reserve may not take: a mine on
     * its site, a terminal on the shore. `blocked` is true when any such work
     * lies within the box (x0, y0) to (x1, y1) inclusive, in world plots;
     * `version` moves whenever the works do (what a kept answer is held against).
     */
    public interface Guard {
        boolean blocked(long x0, long y0, long x1, long y1);

        long version();
    }

    /**
     * One interchange candidate: where it is ({x, y}, the crossing or the T's
     * junction plot), its kind, the side its stub lies on for a trumpet (0
     * north to 3 west, the direction from the junction toward the stub; -1 for
     * a cloverleaf), its box in world plots, and whether it got its reserve.
     */
    public record Site(long x, long y, int kind, int stub, long x0, long y0, int w, int h, boolean got) {
        /** Whether world plot (px, py) is in the box. */
        public boolean holds(long px, long py) { return px >= x0 && py >= y0 && px < x0 + w && py < y0 + h; }

        /** Whether the box meets the w x h plots from (bx, by). */
        public boolean meets(long bx, long by, int bw, int bh) { return x0 < bx + bw && y0 < by + bh && x0 + w > bx && y0 + h > by; }
    }

    /** North, east, south, west as steps. */
    private static final int[] SX = { 0, 1, 0, -1 }, SY = { -1, 0, 1, 0 };

    /**
     * The interchange sites whose box meets the w x h plots from (x0, y0), in
     * plot order of their junctions, each with whether it got its reserve
     * over the ground g (`guard` null for no works to keep clear of). None
     * while the highways are not drawn (CityRuns.highwaysReach()).
     */
    static List<Site> sitesIn(CityRuns runs, CityRuns.Ground g, Guard guard, long x0, long y0, int w, int h) {
        List<Site> out = new ArrayList<>();
        if (!runs.highways.started || !runs.highwaysReach()) return out;
        // The runs over the region, a box's reach beyond it for the junctions, and a box's more beyond them for its plots.
        int pad = 2 * REACH;
        int bw = w + 2 * pad, bh = h + 2 * pad;
        long bx0 = x0 - pad, by0 = y0 - pad;
        byte[] f = new byte[bw * bh];
        runs.fill(bx0, by0, bw, bh, f, null, false);
        for (int j = REACH; j < bh - REACH; j++) {
            for (int i = REACH; i < bw - REACH; i++) {
                if (!highway(f, bw, i, j)) continue;
                boolean[] side = new boolean[4];
                int n = 0;
                for (int d = 0; d < 4; d++) {
                    side[d] = highway(f, bw, i + SX[d], j + SY[d]);
                    if (side[d]) n++;
                }
                if (n < 3) continue;
                // A 45-degree stretch beside it (a staircase's corners) is neither: a stack, drawn as before.
                boolean diagonal = false;
                for (int dx = -1; dx <= 1; dx += 2) for (int dy = -1; dy <= 1; dy += 2) diagonal |= highway(f, bw, i + dx, j + dy);
                if (diagonal) continue;
                long jx = bx0 + i, jy = by0 + j;
                int kind, stub = -1, side0, hh;
                long sx0, sy0;
                if (n == 4) {
                    kind = CLOVERLEAF;
                    side0 = hh = CLOVERLEAF_SIDE;
                    sx0 = jx - side0 / 2;
                    sy0 = jy - hh / 2;
                } else {
                    kind = TRUMPET;
                    for (int d = 0; d < 4; d++) if (side[d] && !side[(d + 2) % 4]) stub = d;
                    if (stub < 0) continue;
                    side0 = hh = TRUMPET_SIDE;
                    // The box beside the junction on the stub's side: the stub along its middle, the through highway on its edge.
                    sx0 = SX[stub] == 0 ? jx - side0 / 2 : SX[stub] < 0 ? jx - side0 : jx + 1;
                    sy0 = SY[stub] == 0 ? jy - hh / 2 : SY[stub] < 0 ? jy - hh : jy + 1;
                }
                if (sx0 >= x0 + w || sy0 >= y0 + h || sx0 + side0 <= x0 || sy0 + hh <= y0) continue;
                out.add(new Site(jx, jy, kind, stub, sx0, sy0, side0, hh, fits(runs, g, guard, f, bw, bx0, by0, sx0, sy0, side0, hh)));
            }
        }
        return out;
    }

    /** Whether plot (i, j) of the runs' buffer is a highway's (a highway under the railway too). */
    private static boolean highway(byte[] f, int bw, int i, int j) {
        byte c = f[j * bw + i];
        return c == CityRuns.F_HIGHWAY || c == CityRuns.F_RAIL_OVER;
    }

    /** Whether a box has the ground for its reserve (the class's rule), the highways and track read from the runs' buffer f (bw across, its first plot (bx0, by0)). */
    private static boolean fits(CityRuns runs, CityRuns.Ground g, Guard guard, byte[] f, int bw, long bx0, long by0, long x0, long y0, int w, int h) {
        for (long y = y0; y < y0 + h; y++) {
            for (long x = x0; x < x0 + w; x++) {
                byte c = f[(int) ((y - by0) * bw + (x - bx0))];
                if (c == CityRuns.F_HIGHWAY || c == CityRuns.F_RAIL_OVER) continue;
                if (!CityRuns.dry(g.at(x, y, -1))) return false;
                if (c == CityRuns.F_RAIL) return false;
                if (Math.floorMod(x, CityRuns.CELL) == 0 || Math.floorMod(y, CityRuns.CELL) == 0) return false;
            }
        }
        for (long[] yd : runs.yards) if (yd[0] < x0 + w && yd[1] < y0 + h && yd[0] + yd[2] > x0 && yd[1] + yd[3] > y0) return false;
        return guard == null || !guard.blocked(x0, y0, x0 + w - 1, y0 + h - 1);
    }

    /** A hash of the sites, for a plan's or a tile's stamp: where each is and whether it got its reserve. */
    static long hash(List<Site> sites) {
        long hash = World.mix(0x52455345L);
        for (Site s : sites) hash = World.mix(hash ^ s.x() * 0x9E3779B97F4A7C15L ^ (s.y() << 20) ^ ((long) s.kind() << 50) ^ (s.got() ? 1L << 61 : 0));
        return hash;
    }
}
