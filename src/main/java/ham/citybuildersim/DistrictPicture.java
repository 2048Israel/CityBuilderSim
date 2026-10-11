package ham.citybuildersim;

import java.util.Arrays;

/**
 * A district's picture for the far views: 32 x 32 pixels, a pixel for 8 x 8 plots (240 m), each the colour of one of its 64 plots - the plot a fixed hash picks from them ranked by class - drawn from the district's own plan as the painter reads it, so a far view shows where things stand, each kind in proportion to the ground it stands on, the same from month to month.
 *
 * WHY THIS EXISTS (0.7.128, batch MM1; docs/studies/spec-globe.md 8.1 and
 * 8.2). Past the painted tiles the far view drew each district one tint
 * (MapTiles' tint, until 0.7.127): the class whose buildings took the
 * most ground in it, at TileRaster.BLOCK_ALPHA by how much of its ground they
 * used. A district is 256 plots, 7.68 km, so a city of a million on a dozen
 * districts was a dozen flat squares at every size - Jerus, 2026-10-11:
 * "at a certain city size you might just see a blob of blue and a blob of
 * purple and thats it". The study's mockup (docs/studies/minimap/) drew a
 * picture of each district instead, a pixel picked from the plots under it,
 * and the core, the rings of homes and the industry on the port came out.
 *
 * WHAT A PIXEL IS. Its 64 plots are counted by code (plotCodes(): the ground,
 * a building by its fill, a paved street, the city's highways and track),
 * the codes taken in their rank (GROUND first, the buildings in
 * BuildingVisual's order and the fills of their own, then the paved
 * streets, the highways and the track), and the plot at rank k is
 * the pixel's, k a fixed hash of the pixel's place in the world over 0 to
 * 63. Over a uniform hash a pixel is a code with the chance its plots give
 * it, so a picture's shares are the district's in expectation, within a
 * binomial spread of a pixel's (spread()); and the hash stands still, so a
 * pixel moves only when the plots under it do. A GROUND pixel is none of
 * the picture's own: the far node shows its own ground there (water, forest,
 * the grass), dimmed where the city does not own it.
 *
 * DRAWN WITH ITS PLAN, KEPT BY ITS STAMP: CityMap's PlanJob draws it from the
 * plan it has just drawn and the ground it read for it (DistrictPlan.Input),
 * on whatever thread runs the job - the map view's worker, "city-map", or a
 * harness's own - and CityMap keeps it with the plan's link, under a stamp of
 * the plots it is drawn from (stampOf()). A plan drawn again to the same
 * plots - its packed plan let go from CityMap.PLANS_KEPT, or drawn again
 * after a district before it moved (its link's stamp: its own inputs, its
 * purchases and what was carried to it, and those of the plans it read) -
 * keeps the picture it has. Not saved (star MM1-1): it is the plan's, which
 * is the counts'.
 *
 * Pure: the same plan and stamp give the same picture.
 */
public final class DistrictPicture {

    /** Plots a pixel takes, a side: 8 (240 m, the study's), TileRaster.BLOCK_FAR - the far view's blocks at L2, so a picture's pixels lie on the blocks the closer view draws. */
    public static final int PLOTS_A_PIXEL = TileRaster.BLOCK_FAR;

    /** Pixels a side: 32, a district's World.DISTRICT plots at PLOTS_A_PIXEL. */
    public static final int SIDE = World.DISTRICT / PLOTS_A_PIXEL;

    /** Pixels in a picture: 1,024. */
    public static final int PIXELS = SIDE * SIDE;

    /** The plots a pixel is picked from: 64. */
    public static final int PLOTS_IN = PLOTS_A_PIXEL * PLOTS_A_PIXEL;

    /* ---------------------------------------- a plot's code, in rank order ---------------------------------------- */

    /** Nothing the picture draws: the ground, owned or not - the far node shows its own there. */
    public static final byte GROUND = 0;
    /** A building of a class, HOME to MINE: its class + FIRST_CLASS (BuildingVisual.FILL's colour). */
    public static final int FIRST_CLASS = 1;
    /** ...the flats (BuildingVisual.FLATS_FILL)... */
    public static final byte FLATS = FIRST_CLASS + BuildingVisual.CLASSES;
    /** ...the refinery's units (CAMPUS_FILL), a sea terminal (TERMINAL_FILL) and a tank farm (TANKS_FILL): each type's own fill, as the painter draws it (BuildingVisual.Type.fill()). */
    public static final byte CAMPUS = FLATS + 1, TERMINAL = FLATS + 2, TANKS = FLATS + 3;
    /** A paved street of the plan, in TileRaster's colour below LINES_FROM px a plot (ROAD_SMALL's paved: its casing) - its gravel streets and tracks are the ground's, hidden as the far view's blocks hide them at L2 (TileRaster.blocks(), star MM1-5)... */
    public static final byte PAVED = TANKS + 1;
    /** ...and a highway's plot - the city's runs' Elevated Highway, or a street of highway plots - and the railway's track: ROAD_SMALL's highway and RAIL_LINE. */
    public static final byte HIGHWAY = TANKS + 2, RAIL = TANKS + 3;
    /** How many codes. */
    public static final int CODES = RAIL + 1;

    /** Each code's colour, 0xAARRGGBB - the painter's (BuildingVisual's fills, TileRaster's roads below LINES_FROM); 0 for GROUND, which is the node's own. */
    static final int[] COLOUR = new int[CODES];
    static {
        for (int c = 0; c < BuildingVisual.CLASSES; c++) COLOUR[FIRST_CLASS + c] = BuildingVisual.FILL[c];
        COLOUR[FLATS] = BuildingVisual.FLATS_FILL;
        COLOUR[CAMPUS] = BuildingVisual.CAMPUS_FILL;
        COLOUR[TERMINAL] = BuildingVisual.TERMINAL_FILL;
        COLOUR[TANKS] = BuildingVisual.TANKS_FILL;
        COLOUR[PAVED] = TileRaster.ROAD_SMALL[BuildingVisual.PAVED];
        COLOUR[HIGHWAY] = TileRaster.ROAD_SMALL[BuildingVisual.HIGHWAY];
        COLOUR[RAIL] = TileRaster.RAIL_LINE;
    }

    /** A code's colour (0 for GROUND: none of the picture's own). */
    public static int colour(int code) { return COLOUR[code]; }

    /** Whether a code is a building's (FIRST_CLASS to TANKS): what a picture's class shares are counted over. */
    public static boolean isBuilding(int code) { return code >= FIRST_CLASS && code <= TANKS; }

    /** A type's code: its fill's - its class's, the flats', or the refinery's units', a terminal's or a tank farm's own (BuildingVisual.Type.fill()); an unknown type industry's, as the painter draws one. */
    public static byte codeOf(BuildingVisual.Type t) {
        if (t == null) return (byte) (FIRST_CLASS + BuildingVisual.INDUSTRY);
        return t.flats() ? FLATS : t.campus() ? CAMPUS : t.berth() >= 0 ? TERMINAL : t.shore() ? TANKS : (byte) (FIRST_CLASS + t.cls());
    }

    /** A plan's street's code by its kind (DistrictPlan's): a paved street PAVED, one of highway plots HIGHWAY; gravel, a track, a seam (a track where no district surfaced it, CityMap.streetAt()) and a street beneath a highway (the run's) the ground. */
    static byte streetCode(int kind) {
        switch (kind) {
            case DistrictPlan.PAVED:   return PAVED;
            case DistrictPlan.HIGHWAY: return HIGHWAY;
            default:                   return GROUND;
        }
    }

    /* ---------------------------------------- the picture ---------------------------------------- */

    /** The stamp of the plots it was drawn from (stampOf()): what it is kept against. */
    public final long stamp;

    /** Each pixel's code, row by row. */
    private final byte[] codes;

    private DistrictPicture(long stamp, byte[] codes) {
        this.stamp = stamp;
        this.codes = codes;
    }

    /** Pixel (x, y)'s code, 0 to SIDE - 1 each way. */
    public int code(int x, int y) { return codes[y * SIDE + x]; }

    /** The code at plot (lx, ly) of the district, 0 to World.DISTRICT - 1 each way: the pixel's that holds it. */
    public int codeAtPlot(int lx, int ly) { return codes[(ly / PLOTS_A_PIXEL) * SIDE + lx / PLOTS_A_PIXEL]; }

    /** Its pixels by code. */
    public int[] counts() {
        int[] n = new int[CODES];
        for (byte c : codes) n[c]++;
        return n;
    }

    /** Whether another picture has the same pixels. */
    public boolean samePixels(DistrictPicture o) { return o != null && Arrays.equals(codes, o.codes); }

    /** The bytes it holds, about: its codes, their array's header and the picture's own. */
    public static final int BYTES = PIXELS + 16 + 24;

    /**
     * Each plot of a district's plan by code, row by row over its
     * World.DISTRICT x World.DISTRICT (its frame less the east column and
     * south row, the next districts'), as the painter would draw them: the
     * plan's paved streets and those of highway plots (its gravel and tracks
     * the ground's, streetCode()); the city's runs over its frame - an Elevated
     * Highway's plots, the railway's track; a mine or well on its site;
     * `extras`, the boxes the runs and the shore stand on its ground ({x, y,
     * w, h, type id} five ints each, in the district's plots: its yards as
     * Rail Terminals, its terminals and tank farms - CityMap.extrasIn()); and
     * every box the plan placed, by its type's code.
     */
    public static byte[] plotCodes(DistrictPlan.Input in, DistrictPlan p, BuildingVisual.Type[] types, int[] extras) {
        int n = World.DISTRICT, f = DistrictPlan.FRAME;
        byte[] out = new byte[n * n];
        byte mine = (byte) (FIRST_CLASS + BuildingVisual.MINE);
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                int i = y * f + x;
                byte c = GROUND;
                if (p.street != null && p.street[i] != 0) c = streetCode(p.street[i] & DistrictPlan.KIND_MASK);
                byte fx = in.fixed[i];
                if (fx == DistrictPlan.FIXED_HIGHWAY) c = HIGHWAY;
                else if (fx == DistrictPlan.FIXED_RAIL) c = RAIL;
                if (in.site[i] == DistrictPlan.SITE_MINED) c = mine;
                out[y * n + x] = c;
            }
        }
        for (int e = 0; extras != null && e + 4 < extras.length; e += 5) {
            int t = extras[e + 4];
            box(out, extras[e], extras[e + 1], extras[e + 2], extras[e + 3], codeOf(t >= 0 && t < types.length ? types[t] : null));
        }
        for (int b = 0; b < p.buildings; b++) {
            int t = p.btype[b];
            box(out, p.bx[b], p.by[b], p.bw[b], p.bh[b], codeOf(t >= 0 && t < types.length ? types[t] : null));
        }
        return out;
    }

    /** A box of plots set to a code, clipped to the district. */
    private static void box(byte[] out, int x0, int y0, int w, int h, byte code) {
        int n = World.DISTRICT;
        for (int y = Math.max(0, y0); y < Math.min(n, y0 + h); y++) {
            for (int x = Math.max(0, x0); x < Math.min(n, x0 + w); x++) out[y * n + x] = code;
        }
    }

    /** A picture's stamp: its plots' codes (plotCodes()) folded, with the world's seed and the district's north-west plot, which its pixels' ranks are drawn on - the same plots there, the same picture. */
    public static long stampOf(byte[] plots, long seed, long x0, long y0) {
        long h = World.mix(seed ^ 0x5049435354414D50L ^ x0 * 0x9E3779B97F4A7C15L);
        h = World.mix(h ^ y0 * 0xC2B2AE3D27D4EB4FL);
        for (int i = 0; i < plots.length; i += 8) {
            long w = 0;
            for (int b = 0; b < 8 && i + b < plots.length; b++) w |= (plots[i + b] & 0xffL) << (8 * b);
            if (w != 0) h = World.mix(h ^ w ^ i);
        }
        return h;
    }

    /** The rank a pixel picks among its PLOTS_IN plots, 0 to PLOTS_IN - 1: a fixed hash of where it lies in the world, its column and row of PLOTS_A_PIXEL plots (px, py), on the world's seed. */
    public static int rankAt(long seed, long px, long py) {
        long h = World.mix(seed ^ 0x5049435455524501L ^ px * 0x9E3779B97F4A7C15L);
        h = World.mix(h ^ py * 0xC2B2AE3D27D4EB4FL);
        return (int) (World.unit(h) * PLOTS_IN);
    }

    /**
     * A district's picture from its plots' codes (plotCodes()), their
     * `stamp` (stampOf()), the world's seed and the district's north-west plot in
     * the world (x0, y0): each pixel the code at its rank (rankAt()) among its
     * plots counted by code, in code order.
     */
    public static DistrictPicture of(byte[] plots, long stamp, long seed, long x0, long y0) {
        int n = World.DISTRICT;
        byte[] codes = new byte[PIXELS];
        int[] count = new int[CODES];
        long px0 = Math.floorDiv(x0, PLOTS_A_PIXEL), py0 = Math.floorDiv(y0, PLOTS_A_PIXEL);
        for (int py = 0; py < SIDE; py++) {
            for (int px = 0; px < SIDE; px++) {
                Arrays.fill(count, 0);
                for (int y = py * PLOTS_A_PIXEL; y < (py + 1) * PLOTS_A_PIXEL; y++) {
                    int row = y * n;
                    for (int x = px * PLOTS_A_PIXEL; x < (px + 1) * PLOTS_A_PIXEL; x++) count[plots[row + x]]++;
                }
                int k = rankAt(seed, px0 + px, py0 + py), c = 0;
                while (k >= count[c]) k -= count[c++];
                codes[py * SIDE + px] = (byte) c;
            }
        }
        return new DistrictPicture(stamp, codes);
    }

    /**
     * The spread a picture's share of a code may take from its plots' share
     * over a uniform hash, one standard deviation: each pixel is the code
     * with the chance q its own plots give it, so the share's variance is
     * the sum of q (1 - q) over the pixels, over PIXELS squared.
     */
    public static double spread(byte[] plots, int code) {
        int n = World.DISTRICT;
        double var = 0;
        for (int py = 0; py < SIDE; py++) {
            for (int px = 0; px < SIDE; px++) {
                int k = 0;
                for (int y = py * PLOTS_A_PIXEL; y < (py + 1) * PLOTS_A_PIXEL; y++) {
                    for (int x = px * PLOTS_A_PIXEL; x < (px + 1) * PLOTS_A_PIXEL; x++) if (plots[y * n + x] == code) k++;
                }
                double q = k / (double) PLOTS_IN;
                var += q * (1 - q);
            }
        }
        return Math.sqrt(var) / PIXELS;
    }

    /* ---------------------------------------- a district too small for its picture ---------------------------------------- */

    /**
     * A district's shares of its owned dry ground by code, from its counts
     * (the study's second rule, for a district drawn under MapTiles.PICTURE_FROM_PX
     * pixels across): each drawn type's buildings on their whole plots
     * (BuildingVisual.cells()), its Paved Roads' plots as streets (its gravel
     * the ground's, as a picture's); the highways and the track are the far
     * node's lines, not shares. Scaled down to the whole when they take more
     * than its ground (a district the plans carry from). Empty for a district
     * with none.
     */
    public static double[] shares(CityMap.District d, BuildingVisual.Type[] types) {
        double[] s = new double[CODES];
        if (d == null || d.owned() <= 0) return s;
        double sum = 0;
        for (int t = 0; t < types.length; t++) {
            BuildingVisual.Type ty = types[t];
            int c = d.count(t);
            if (ty == null || c <= 0) continue;
            int code;
            if (ty.road() == BuildingVisual.PAVED) code = PAVED;
            else if (ty.drawn()) code = codeOf(ty);
            else continue;
            double plots = (double) c * BuildingVisual.cells(ty);
            s[code] += plots;
            sum += plots;
        }
        double scale = 1.0 / Math.max(d.owned(), sum);
        for (int k = 0; k < CODES; k++) s[k] *= scale;
        return s;
    }

    /** The code a node's pixel takes from a district's shares at a hash u, 0 to 1: the code whose share u falls in, in code order, GROUND past them all. */
    public static int pick(double[] shares, double u) {
        for (int k = 1; k < CODES; k++) {
            u -= shares[k];
            if (u < 0) return k;
        }
        return GROUND;
    }
}
