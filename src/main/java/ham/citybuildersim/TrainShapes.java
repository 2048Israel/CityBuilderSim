package ham.citybuildersim;

/**
 * How the map draws a train: each vehicle's body and its load's mark as
 * points in plots about where it is and the way it heads, and their colours
 * by what the train carries and which way - what ui/MapView fills, and a
 * Java2D mirror can draw the same; pure (0.8.2, batch FR1; spec-freight 4.4).
 *
 * WHY THIS EXISTS. ShipShapes' reason: the view is JavaFX, which cannot draw
 * here, so the shapes are worked out in the model and the view only fills
 * them.
 *
 * SIZE. Each vehicle's length to scale (TrainSchedule.CAR_PLOTS and
 * LOCO_PLOTS) less a coupling's GAP, and WIDTH wide, about the track bed's:
 * a true wagon, about 3 m, is a tenth of a plot, under a pixel at the near
 * view. Vehicle by vehicle from CARS_FROM px a plot, the near view; below it
 * (the middle view) a train is a dash of its colour along the line.
 *
 * COLOURS (the boats', spec-freight 4.4): a full import blue, a full export
 * orange, an empty train grey, boxes white - ShipShapes' four; the
 * locomotives dark. From MARKS_FROM px a plot each wagon shows its kind: a
 * hopper's heap of ore, a tank's barrel, the gap between a flat's two boxes,
 * a van's roof.
 */
public final class TrainShapes {

    private TrainShapes() { }

    /** A locomotive, dark (spec-freight 4.4). */
    public static final int LOCO = 0xff2b2f36;

    /** A vehicle's drawn width in plots: 0.42, about the track bed's (TileRaster.BED 0.52), so the train covers its rails. */
    public static final double WIDTH = 0.42;

    /** The gap left between two vehicles, in plots: 0.08, so the wagons read one by one from MARKS_FROM in. */
    public static final double GAP = 0.08;

    /** Pixels a plot from which a train is drawn vehicle by vehicle: MapFrame.L0_FROM, the near view; below it, a dash. */
    public static final double CARS_FROM = MapFrame.L0_FROM;

    /** Pixels a plot from which a wagon shows its kind's mark: 8, where a wagon is 4 px long. */
    public static final double MARKS_FROM = 8;

    /** A hopper's heap of ore: TileRaster.ORE_HEAP, the yards' stockpiles' sand. */
    public static final int HEAP = TileRaster.ORE_HEAP;

    /** A train's colour by what it carries and which way: an empty train grey, boxes white, a full import blue, a full export orange. */
    public static int colour(TrainSchedule.Train t) {
        if (!t.loaded()) return ShipShapes.EMPTY;
        if (t.call().kind() == TrainSchedule.Kind.INTERMODAL) return ShipShapes.BOXES;
        return t.call().inbound() ? ShipShapes.IMPORT : ShipShapes.EXPORT;
    }

    /** A vehicle's body: its outline as {xs, ys} in plots, a rectangle along its heading. */
    public static double[][] body(TrainSchedule.Car c) {
        double len = c.loco() ? TrainSchedule.LOCO_PLOTS : TrainSchedule.CAR_PLOTS;
        return rect(c, (len - GAP) / 2, WIDTH / 2);
    }

    /** A wagon's mark of its kind, {xs, ys} in plots, or null: a locomotive's, an empty flat's. */
    public static double[][] mark(TrainSchedule.Car c, TrainSchedule.Train t) {
        if (c.loco()) return null;
        double half = (TrainSchedule.CAR_PLOTS - GAP) / 2, w = WIDTH / 2;
        switch (t.call().kind()) {
            case HOPPER: return rect(c, half * 0.62, w * 0.55);
            case TANK:   return rect(c, half * 0.80, w * 0.30);
            case MIXED:  return rect(c, half * 0.86, w * 0.22);
            default:     return t.loaded() ? rect(c, half * 0.10, w) : null;
        }
    }

    /** The mark's colour: a full hopper's heap, an empty one's hold, a tank's light barrel, a van's dark roof, the dark gap between two boxes. */
    public static int markColour(TrainSchedule.Train t) {
        int body = colour(t);
        switch (t.call().kind()) {
            case HOPPER: return t.loaded() ? HEAP : TileRaster.blend(body, 0xff000000, 0.35);
            case TANK:   return TileRaster.blend(body, 0xffffffff, 0.40);
            case MIXED:  return TileRaster.blend(body, 0xff000000, 0.30);
            default:     return LOCO;
        }
    }

    /** A rectangle about a vehicle's middle: hl along its heading either way, hw across. */
    static double[][] rect(TrainSchedule.Car c, double hl, double hw) {
        double ax = Math.cos(c.heading()), ay = Math.sin(c.heading()), nx = -ay, ny = ax;
        double[] xs = {
            c.x() + ax * hl + nx * hw, c.x() + ax * hl - nx * hw, c.x() - ax * hl - nx * hw, c.x() - ax * hl + nx * hw };
        double[] ys = {
            c.y() + ay * hl + ny * hw, c.y() + ay * hl - ny * hw, c.y() - ay * hl - ny * hw, c.y() - ay * hl + ny * hw };
        return new double[][] { xs, ys };
    }
}
