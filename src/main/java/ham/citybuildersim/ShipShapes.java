package ham.citybuildersim;

/**
 * How the map draws a boat: its length and beam by its class, its outline and deck marks as points in plots about where it is and the way it heads, its colours by what it carries and which way, and how much bigger than true it is drawn at a zoom - what ui/MapView fills and strokes, and a Java2D mirror can draw the same; pure.
 *
 * WHY THIS EXISTS (0.7.97, batch O13; the research's 4.4, mockup 3). The
 * view is JavaFX, which cannot draw here, so the shapes are worked out in the
 * model - as RefineryView's picture is - and the view only fills them.
 *
 * SIZE. The research gives one ship's length, a 245 m Aframax (4.4; mockup 3
 * draws it 245 x 42 m), and the classes' cargo [P30][P40][P42][P46]: a hull
 * of one shape scales its length with the cube root of what it carries, so
 * a class is LENGTH_M x (cargo / AFRAMAX_T)^(1/3) long (star O13-8): an MR
 * 194 m (mockup 3's 183), a VLCC 373, a Capesize 319, a feeder 174, a 5,500 t
 * general cargo ship 102 - each within about a tenth of its class's real
 * length - and BEAM of it wide (the mockup's 42 / 245).
 *
 * DRAWN. True size from BIG_TILES_FROM px a plot; below it BOOST times true
 * (the research's 4.4: "draw boats at 1.6x, and at true size from close zoom
 * in"), and never shorter than LEAST_PX, where it is a dart of its colour.
 *
 * COLOURS (the research's 4.4, mockup 3's): an import coming in full blue,
 * an export going out full orange, an empty leg grey, a box ship - loaded
 * both ways - white; the hull dark, darker laden.
 */
public final class ShipShapes {

    private ShipShapes() { }

    /** The Aframax's length, the research's one ship drawn to scale (4.4; mockup 3): 245 m. */
    public static final double LENGTH_M = 245;

    /** ...and its cargo, the class's (Ports.Ship.AFRAMAX): what the cube root is taken of. */
    public static final double AFRAMAX_T = 75_000;

    /** A hull's beam over its length: mockup 3's Aframax, 42 m on 245. */
    public static final double BEAM = 42.0 / 245;

    /** Boats are drawn this many times their size below BIG_TILES_FROM px a plot: 1.6 (the research's 4.4, mockup 3's legend). */
    public static final double BOOST = 1.6;

    /** ...and at true size from here, px a plot: MapFrame.BIG_TILES_FROM, 6 - the close zoom. */
    public static final double TRUE_FROM = MapFrame.BIG_TILES_FROM;

    /** The shortest a boat is drawn whole, in px: 7 - BEAM of it is 1.2 px, its deck and bridge under a pixel each; shorter, its hull is a dart of its colour (spec-oil 6's fallback, "dots at L1"; star O13-9). */
    public static final double LEAST_PX = 7;

    /** An import coming in full: the research's blue, mockup 3's #5aa9ff. */
    public static final int IMPORT = 0xff5aa9ff;

    /** An export going out full: mockup 3's orange, #f2a65a. */
    public static final int EXPORT = 0xfff2a65a;

    /** An empty leg, riding high: mockup 3's grey, #8496ab. */
    public static final int EMPTY = 0xff8496ab;

    /** A box ship, loaded both ways: mockup 3's white, #d6dde4. */
    public static final int BOXES = 0xffd6dde4;

    /** A hull's fill, laden and empty: mockup 3's #1a2836 and #22303e. */
    public static final int HULL_LADEN = 0xff1a2836, HULL_EMPTY = 0xff22303e;

    /** The bridge at the stern: mockup 3's #c3ccd3. */
    public static final int BRIDGE = 0xffc3ccd3;

    /** A class's length in metres: LENGTH_M x (cargo / AFRAMAX_T)^(1/3). */
    public static double lengthM(Ports.Ship ship) {
        return LENGTH_M * Math.cbrt(Math.max(1, ship.tonnes()) / AFRAMAX_T);
    }

    /** How many times its size a boat is drawn at a scale (px a plot): BOOST below TRUE_FROM, else 1. */
    public static double boost(double scale) {
        return scale < TRUE_FROM ? BOOST : 1;
    }

    /** A boat's colour: white for boxes, else blue coming in full, orange going out full, grey empty. */
    public static int colour(BoatSchedule.Boat b) {
        if (b.call().cargo() == Ports.Cargo.CONTAINER) return BOXES;
        if (!b.loaded()) return EMPTY;
        return b.call().inbound() ? IMPORT : EXPORT;
    }

    /** A boat's drawn length in plots at a scale: its class's, boosted, at least LEAST_PX on screen. */
    public static double drawnLength(BoatSchedule.Boat b, double scale) {
        double plots = lengthM(b.call().ship()) / World.PLOT_M * boost(scale);
        return Math.max(plots, LEAST_PX / Math.max(1e-9, scale));
    }

    /**
     * A boat's outline in plots, five points from the stern's port corner
     * round to the bow's point: a hull of its drawn length, BEAM of it wide,
     * the bow its last beam drawn to a point, along its heading about where
     * it is. {xs, ys}.
     */
    public static double[][] hull(BoatSchedule.Boat b, double scale) {
        double l = drawnLength(b, scale), w = l * BEAM;
        double[] u = { -l / 2, l / 2 - w, l / 2, l / 2 - w, -l / 2 };
        double[] v = { -w / 2, -w / 2, 0, w / 2, w / 2 };
        return place(b, u, v);
    }

    /** Its bridge at the stern, a box {xs, ys} of four points: 7% of its length from the stern, three quarters of its beam. */
    public static double[][] bridge(BoatSchedule.Boat b, double scale) {
        double l = drawnLength(b, scale), w = l * BEAM, s = -l / 2 + l * 0.06, e = s + Math.max(l * 0.07, 0);
        return place(b, new double[] { s, e, e, s }, new double[] { -w * 0.38, -w * 0.38, w * 0.38, w * 0.38 });
    }

    /**
     * Its deck's marks, by kind, as boxes of four points each: a tanker's
     * centre line (one long thin box), a bulk carrier's hatches, a box ship's
     * rows of boxes, a general cargo ship's two holds.
     */
    public static double[][][] deck(BoatSchedule.Boat b, double scale) {
        double l = drawnLength(b, scale), w = l * BEAM;
        java.util.List<double[][]> out = new java.util.ArrayList<>();
        switch (b.call().cargo()) {
            case LIQUID: {
                double s = -l / 2 + l * 0.18, e = l / 2 - w * 1.1, t = Math.max(w * 0.08, 0.01);
                out.add(place(b, new double[] { s, e, e, s }, new double[] { -t / 2, -t / 2, t / 2, t / 2 }));
                break;
            }
            case DRY_BULK: {
                int n = Math.max(4, (int) Math.round(l / (w * 0.95)) - 1);
                for (int i = 0; i < n; i++) {
                    double c = -l / 2 + l * 0.2 + i * (l * 0.68 / Math.max(1, n - 1));
                    out.add(place(b, new double[] { c - w * 0.22, c + w * 0.22, c + w * 0.22, c - w * 0.22 }, new double[] { -w * 0.3, -w * 0.3, w * 0.3, w * 0.3 }));
                }
                break;
            }
            case CONTAINER: {
                int n = Math.max(4, (int) Math.round(l * 0.68 / (w * 0.5)));
                double step = l * 0.66 / n;
                for (int i = 0; i < n; i++) {
                    double c = -l / 2 + l * 0.2 + i * step;
                    out.add(place(b, new double[] { c, c + step * 0.9, c + step * 0.9, c }, new double[] { -w * 0.4, -w * 0.4, w * 0.4, w * 0.4 }));
                }
                break;
            }
            default: {
                for (double c : new double[] { -l * 0.1, l * 0.15 }) {
                    out.add(place(b, new double[] { c - w * 0.3, c + w * 0.3, c + w * 0.3, c - w * 0.3 }, new double[] { -w * 0.28, -w * 0.28, w * 0.28, w * 0.28 }));
                }
            }
        }
        return out.toArray(new double[0][][]);
    }

    /** The colour of a box ship's k-th box: mockup 3's six (blue, red, green, ochre, violet, steel), by the box and its call. */
    public static int boxColour(BoatSchedule.Boat b, int k) {
        int[] pal = { 0xff4f7aa8, 0xffa8574f, 0xff4f9a7a, 0xffa8904f, 0xff7a5fa8, 0xff5f8fa8 };
        long h = World.mix(Double.doubleToLongBits(b.call().arrives()) ^ k * 0x9E3779B97F4A7C15L);
        return pal[(int) Long.remainderUnsigned(h, pal.length)];
    }

    /** Points along (u) and across (v) a boat, in plots, turned to its heading about where it is. */
    static double[][] place(BoatSchedule.Boat b, double[] u, double[] v) {
        double c = Math.cos(b.heading()), s = Math.sin(b.heading());
        double[] xs = new double[u.length], ys = new double[u.length];
        for (int i = 0; i < u.length; i++) {
            xs[i] = b.x() + u[i] * c - v[i] * s;
            ys[i] = b.y() + u[i] * s + v[i] * c;
        }
        return new double[][] { xs, ys };
    }
}
