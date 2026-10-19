package ham.citybuildersim;

/**
 * How the map draws a truck: its body as points in plots about where it is
 * and the way it heads, and its colour by what it carries - what ui/MapView
 * fills, and a Java2D mirror can draw the same; pure (0.8.8, batch FR5;
 * spec-freight 4.7).
 *
 * WHY THIS EXISTS. TrainShapes' reason: the view is JavaFX, which cannot draw
 * here, so the shape is worked out in the model and the view only fills it.
 * A truck is the smallest thing the map moves, so its shape is the least:
 * one silhouette, a trailer with a narrower cab ahead of it, that reads as a
 * truck and not a dot from the near view, where trucks are drawn (TruckSchedule.drawnAt()),
 * and nothing finer - at the pixels a plot a truck has, a door or a wheel is
 * under a pixel.
 *
 * SIZE. LENGTH by WIDTH plots: the length is a lorry's own (16.5 m
 * articulated, on a 30 m plot), the width is drawn about 2.6 times true
 * (6.6 m for 2.5 m) so that it is more than a line at the near view, where a
 * plot is MapFrame.L0_FROM px or more; and never under MIN_PX long, where it
 * is drawn larger with its shape kept.
 *
 * COLOURS (the boats' and the trains', spec-freight 4.7): a loaded import
 * blue, a loaded export orange, an empty truck grey - ShipShapes' three, the
 * same numbers. A tonne bought at home, hauled from one works to another and
 * never across the city's edge, is neither an import nor an export and has a
 * colour of its own, HOME.
 */
public final class TruckShapes {

    private TruckShapes() { }

    /** A truck's length in plots: the schedule's (TruckSchedule.TRUCK_PLOTS, 0.55, a 16.5 m articulated lorry), which frames the trucks by it. */
    public static final double LENGTH = TruckSchedule.TRUCK_PLOTS;

    /**
     * The fewest pixels a truck is drawn long: 4 (the review, FR5). At the near
     * view's far end (MapFrame.L0_FROM, 3.2 px a plot) a truck true to size is
     * under 2 px by 1 and reads as nothing; under this it is drawn larger, its
     * shape kept, as the boats are drawn larger until 6 px a plot.
     */
    public static final double MIN_PX = 4;

    /** A truck's drawn width in plots: 0.22, a lorry's proportions on the road bed, narrower than a train's (TrainShapes.WIDTH 0.42). */
    public static final double WIDTH = 0.22;

    /** The cab's share of the truck's length: 0.30, the tractor ahead of its trailer. */
    public static final double CAB_SHARE = 0.30;

    /** The cab's width over the trailer's: 0.80, so the silhouette steps in where the tractor starts and reads as a tractor and a trailer. */
    public static final double CAB_NARROW = 0.80;

    /**
     * How far right of the road's middle a truck drives, in plots: 0.2 (the
     * review, FR5), so the trucks going in and those coming out pass side by
     * side and do not draw over each other on the centre line (the renders'
     * first draft); inside a road one plot wide with the truck's width.
     */
    public static final double LANE = 0.2;

    /** A tonne bought at home (works to works, loaded), neither an import nor an export: Palette.BUSINESS, the business area's violet, #a78bfa - the works' own colour, and no other mark on the map's freight. */
    public static final int HOME = 0xffa78bfa;

    /** A truck's colour by what it carries: an empty truck grey, a loaded import blue, a loaded export orange (ShipShapes'), a load bought at home HOME. */
    public static int colour(TruckSchedule.Truck t) {
        if (!t.loaded()) return ShipShapes.EMPTY;
        switch (t.call().load()) {
            case IMPORT: return ShipShapes.IMPORT;
            case EXPORT: return ShipShapes.EXPORT;
            default:     return HOME;
        }
    }

    /** A truck's body: its outline as {xs, ys} in plots, a trailer with the cab ahead of it, at its heading, true to size. */
    public static double[][] body(TruckSchedule.Truck t) { return body(t, Double.POSITIVE_INFINITY); }

    /** ...drawn at px pixels a plot: true to size, or larger, its shape kept, where that would be under MIN_PX long. */
    public static double[][] body(TruckSchedule.Truck t, double px) {
        double grow = px > 0 && LENGTH * px < MIN_PX ? MIN_PX / (LENGTH * px) : 1;
        double len = LENGTH * grow;
        double h = len / 2, w = WIDTH * grow / 2, cw = w * CAB_NARROW, step = h - len * CAB_SHARE;
        // Along the heading (u) and across it (v), rear to front on one side and back on the other.
        double[] us = { -h, step, step, h, h, step, step, -h };
        double[] vs = { -w, -w, -cw, -cw, cw, cw, w, w };
        double ax = Math.cos(t.heading()), ay = Math.sin(t.heading()), nx = -ay, ny = ax;
        // Its lane: LANE to the right of its heading (y runs south, so the right of east is south: (nx, ny)).
        double cx = t.x() + nx * LANE, cy = t.y() + ny * LANE;
        double[] xs = new double[us.length], ys = new double[us.length];
        for (int i = 0; i < us.length; i++) {
            xs[i] = cx + ax * us[i] + nx * vs[i];
            ys[i] = cy + ay * us[i] + ny * vs[i];
        }
        return new double[][] { xs, ys };
    }
}
