package ham.citybuildersim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The cranes that work a boat at its terminal's quay (HB2, 2026-11-09), as lines and a hook in world plots: where each lies, where its
 * hook is and what hangs from it at a share of the boat's time at the quay - pure, with no toolkit, which the map draws over a berth from
 * MapFrame.DETAIL_FROM in.
 *
 * WHY: Jerus's idea of 2026-10-31, "a small crane at the near view: a pixel line moving an orange box between ship, quay and train",
 * picked as look 3a from renders on 2026-11-09 over a standing gantry and a travelling one (the render tool in docs/queue/HB2/ is the
 * reference). It is a function of the boat's time at the quay (BoatSchedule.atQuay()) and nothing else, so it keeps no state and the
 * boats' frame gains next to nothing: measured in the renders at 0.06 to 0.09 ms a frame for 28 berths.
 */
public final class QuayCrane {

    private QuayCrane() { }

    /** How many moves a crane makes in a boat's time at its quay: each is a load carried one way and the hook back bare. */
    public static final int MOVES = 2;

    /** How long a berth must be, in plots along the shore, to work a boat with two cranes: one unloading, one loading. */
    public static final int TWO_CRANES_FROM = 12;

    /** What hangs on the hook: nothing, a box, a grab of ore, or (a tanker's loading arm, a line with no hook) the arm. */
    public static final int BARE = 0, BOX = 1, GRAB = 2, ARM = 3;

    /** The render's dark, the box's edge and the line's other shade. */
    private static final int DARK = 0xff2b2f36;

    /** The line's colour, the box's, its edge's and the bare hook's, ARGB: the render's LIGHT, ORANGE, DARK and TileRaster.STEEL (a grab's fill is TileRaster.ORE_HEAP). */
    public static final int LINE = TileRaster.blend(TileRaster.STEEL, DARK, 0.3), BOX_FILL = 0xffff9a2e, BOX_EDGE = DARK, HOOK = TileRaster.STEEL;

    /** How far inside the box's sea edge the apron point lies, in plots: half a plot, the middle of its sea-most row. */
    static final double APRON_IN = 0.5;

    /** How far the line runs landward of the apron point, in plots: a quarter. */
    static final double LINE_LANDWARD = 0.25;

    /** How far the line runs seaward of the ship point, in plots: three eighths. */
    static final double LINE_SEAWARD = 0.375;

    /** How far either side of the boat's place along the shore a crane stands when a berth has two, in plots. */
    static final double TWO_APART = 2.0;

    /** How far either side of the boat's place along the shore a tanker's loading arm stands, in plots. */
    static final double ARM_APART = 1.5;

    /** How far out from the quay wall's outer edge a crane reaches for its ship, in plots: a boat moored stern-to has its middle farther out than any boom, and is worked at its stern. */
    static final double OUTREACH = 2.0;

    /** How far a crane stands inside the box's two ends, in plots. */
    private static final double END_MARGIN = 1.0;

    /** How far on in its moves the second crane is from the first, in the share of the boat's time at the quay, so that one loads as the other unloads. */
    static final double SECOND_ON = 0.25;

    /** The phase of a move at which the hook has carried its load across, at which it starts back bare, and at which it has come back (a pause after each). */
    private static final double CARRIED = 0.42, BACK_STARTS = 0.50, BACK = 0.92;

    /**
     * One crane at a moment: its line from over the ship (x0, y0) to over the apron (x1, y1) - the ends of the line as drawn, a quarter plot
     * landward of the apron point and three eighths of a plot seaward of the ship point - its hook at (hx, hy) on that line, and what hangs
     * there (BARE, BOX, a GRAB of ore; ARM for a tanker's loading arm, a line with no hook, whose hook is its ship end).
     */
    public record Reach(double x0, double y0, double x1, double y1, double hx, double hy, int load) { }

    /**
     * The cranes working a boat that lies at (boatX, boatY) off the quay of work w, at `share` of its time there (BoatSchedule.atQuay()): none
     * for a work with no harbour quay (a pier of its own has no wall to stand on), for a share outside 0..1 or a null cargo. The ship point is
     * the boat's place, or OUTREACH from the wall's outer edge when the boat lies farther out than that.
     */
    public static List<Reach> reaches(CityShore.Work w, Ports.Cargo cargo, double boatX, double boatY, double share) {
        List<Reach> out = new ArrayList<>(2);
        if (w == null || cargo == null || w.quay() != CityShore.HARBOUR_QUAY_PLOTS || w.side() < 0 || w.side() >= CityShore.DX.length || !(share >= 0 && share <= 1)) return out;
        double[] boat = localOf(w, boatX, boatY);
        double c = boat[0], depth = depthOf(w), length = lengthOf(w), vShip = Math.min(boat[1], depth + w.quay() + OUTREACH);
        if (cargo == Ports.Cargo.LIQUID) {
            double wall = depth + w.quay() - APRON_IN;      // the wall's outer edge less half a plot
            for (int k = -1; k <= 1; k += 2) {
                double[] ship = worldOf(w, c + k * ARM_APART, vShip), root = worldOf(w, c + k * ARM_APART, wall);
                out.add(new Reach(ship[0], ship[1], root[0], root[1], ship[0], ship[1], ARM));
            }
            return out;
        }
        int cranes = length >= TWO_CRANES_FROM ? 2 : 1;
        int carried = cargo == Ports.Cargo.DRY_BULK ? GRAB : BOX;
        double vDrop = depth - APRON_IN;
        for (int i = 0; i < cranes; i++) {
            double base = c + (cranes == 2 ? (i == 0 ? -TWO_APART : TWO_APART) : 0);
            double u = Math.max(END_MARGIN, Math.min(length - END_MARGIN, base));
            double tt = (share + i * SECOND_ON) % 1.0 * MOVES, p = tt % 1;
            double[] h = i == 0 ? hook(p, vShip, vDrop) : hook(p, vDrop, vShip);
            double[] ship = worldOf(w, u, vShip + LINE_SEAWARD), apron = worldOf(w, u, vDrop - LINE_LANDWARD), at = worldOf(w, u, h[0]);
            out.add(new Reach(ship[0], ship[1], apron[0], apron[1], at[0], at[1], h[1] > 0 ? carried : BARE));
        }
        return out;
    }

    /** Where a crane's hook is at phase p of a move, and whether it carries: {v, 1 or 0}. Out to `to` with the load, a pause, back bare, a pause. */
    private static double[] hook(double p, double from, double to) {
        if (p < CARRIED) return new double[] { from + (to - from) * ease(p / CARRIED), 1 };
        if (p < BACK_STARTS) return new double[] { to, 1 };
        if (p < BACK) return new double[] { to + (from - to) * ease((p - BACK_STARTS) / (BACK - BACK_STARTS)), 0 };
        return new double[] { from, 0 };
    }

    /** x held to 0..1 and eased in and out. */
    private static double ease(double x) {
        x = Math.max(0, Math.min(1, x));
        return x * x * (3 - 2 * x);
    }

    /** The berthed works by their berth's point (CityShore.berths()' x and y, floor of Work.berth()), keyed by berthKey(x, y): how a route's berth finds its terminal. */
    public static Map<Long, CityShore.Work> worksByBerth(CityShore shore) {
        Map<Long, CityShore.Work> out = new HashMap<>();
        for (CityShore.Work w : shore.works()) {
            if (!w.berthed()) continue;
            double[] b = w.berth();
            out.put(berthKey((long) Math.floor(b[0]), (long) Math.floor(b[1])), w);
        }
        return out;
    }

    /** The key worksByBerth() holds a berth under: its x in the high 32 bits, its y in the low. */
    public static long berthKey(long x, long y) {
        return (x << 32) ^ (y & 0xFFFF_FFFFL);
    }

    /*
     * A work's own frame, as the renders' Local has it: u along the sea side and v from the land side (0) out to the sea side (depth), in
     * plots, from the box's land-side corner on the left of one who faces the sea. u runs EAST for a sea on the north (side 0), SOUTH for
     * the east (1), WEST for the south (2) and NORTH for the west (3): to the right of one who faces the sea.
     */

    /** How long the box is along the shore, in plots. */
    private static double lengthOf(CityShore.Work w) {
        return w.side() % 2 == 0 ? w.w() : w.h();
    }

    /** How deep the box is from its land edge to its sea edge, in plots. */
    private static double depthOf(CityShore.Work w) {
        return w.side() % 2 == 0 ? w.h() : w.w();
    }

    /** The world point (x, y) of the work's frame at (u, v) in plots. */
    private static double[] worldOf(CityShore.Work w, double u, double v) {
        int s = w.side();
        double[] o = originOf(w);
        return new double[] { o[0] + -CityShore.DY[s] * u + CityShore.DX[s] * v, o[1] + CityShore.DX[s] * u + CityShore.DY[s] * v };
    }

    /** The inverse: the work's frame's {u, v} of the world point (x, y), in plots. */
    private static double[] localOf(CityShore.Work w, double x, double y) {
        int s = w.side();
        double[] o = originOf(w);
        double dx = x - o[0], dy = y - o[1];
        return new double[] { dx * -CityShore.DY[s] + dy * CityShore.DX[s], dx * CityShore.DX[s] + dy * CityShore.DY[s] };
    }

    /** The world point of the frame's origin: the corner of the box on its land side where u starts. */
    private static double[] originOf(CityShore.Work w) {
        switch (w.side()) {
            case 0:  return new double[] { w.x0(), w.y0() + w.h() };
            case 1:  return new double[] { w.x0(), w.y0() };
            case 2:  return new double[] { w.x0() + w.w(), w.y0() };
            default: return new double[] { w.x0() + w.w(), w.y0() + w.h() };
        }
    }
}
