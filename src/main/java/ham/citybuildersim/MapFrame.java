package ham.citybuildersim;

/**
 * The map view's arithmetic without the toolkit: where the view looks and how close, how a drag and a notch of the wheel move it, which level of detail it draws and which tiles or far nodes that takes, the land's overlay on whole pixels, and how long its scale bar is - what ui/MapView draws by and MapCheck holds.
 *
 * WHY THIS EXISTS (0.7.61, batch J4; the project's spec-land.md 2.6). The
 * map is drawn on a JavaFX Canvas, and JavaFX cannot draw headless in the
 * cloud loop, so the look and a smooth pan are checked by eye on the PC. The
 * half of the view that is arithmetic is kept out of the toolkit, as
 * ChartModel is for TimeChart's charts, so a harness can hold what the eye
 * cannot: that a plot under the pointer stays under it through a zoom, that
 * the zoom stops at the whole world and at 16 px a plot, that the level of
 * detail changes at the mockup's thresholds, and that a screen asks for the
 * tiles it shows and no more.
 *
 * Coordinates are the world's plots (World, 0 to SIDE each way, y south);
 * the view is a centre in plots and a scale in screen pixels a plot.
 *
 * THE LEVELS (the mockup's thresholds, spec-land 2.6):
 *   - L0, L0_FROM px a plot and closer: painted tiles, buildings one by one,
 *     rastered at SMALL_TILE_PX a plot, at BIG_TILE_PX past BIG_TILES_FROM
 *     (where the raster draws edges and the road casings), drawn scaled;
 *   - L1, L1_FROM to L0_FROM: the painted tile as the mockup's middle-view
 *     blocks of four plots (TileRaster.blocks()), rastered at MIDDLE_TILE_PX;
 *   - L2, below L1_FROM: the far view's blocks of eight, gravel hidden, at a
 *     pixel a plot - while the view holds no more than NEAR_TILES_MOST tiles;
 *   - FAR, past that: node images NODE_PX across from the world's terrain
 *     and the districts' counts, one image pixel nodePlots() plots.
 */
public final class MapFrame {

    /** A notch of the wheel zooms by this much, at the pointer: 1.25 (spec-land 2.6). */
    public static final double ZOOM_STEP = 1.25;

    /** The closest the view comes: 16 px a plot (spec-land 2.6), a plot's buildings and roads drawn at the raster's own widths. */
    public static final double MOST_PX_A_PLOT = 16;

    /** L0 from here: 3.2 px a plot (the mockup's): buildings one by one. */
    public static final double L0_FROM = 3.2;

    /** L1 from here to L0: 1.4 px a plot (the mockup's): blocks of four plots; below it, blocks of eight. */
    public static final double L1_FROM = 1.4;

    /** Past this the L0 tiles are rastered at BIG_TILE_PX: 6 px a plot (spec-land 2.6), where TileRaster begins to edge the buildings (EDGE_FROM) and case the roads. */
    public static final double BIG_TILES_FROM = 6;

    /** Pixels a plot an L0 tile is rastered at up to BIG_TILES_FROM: 4 (spec-land 2.6), TileRaster's first width with every road's line drawn. */
    public static final int SMALL_TILE_PX = 4;

    /** ...and past it: 8 (spec-land 2.6). */
    public static final int BIG_TILE_PX = 8;

    /** Pixels a plot an L1 tile is rastered at: 2 - the blocks of four drawn at the middle view's own scale, as J3b's renders drew them (round(1.74) at Jerus's expanded view), so a road is a line, not a band, when drawn up to 1.6 times. */
    public static final int MIDDLE_TILE_PX = 2;

    /** ...and an L2 tile: 1, a pixel a plot, drawn smaller than it is. */
    public static final int FAR_TILE_PX = 1;

    /** L2 draws painted tiles while the view holds no more than this many (star): 1,024 - a city the size of Jerus's (about 350 tiles fitted into the land office's small map) is drawn from its painted streets, as J3b's renders were; past it, as at five and ten billion or the whole world, the far nodes. */
    public static final int NEAR_TILES_MOST = 1024;

    /** A far node image's side, in pixels: 128 (star) - shown between 128 and 256 px, so about 96 of them at most fill a 1,345 x 806 view. */
    public static final int NODE_PX = 128;

    /** A fitted view leaves this much of itself round what it fits: 0.94 of the view (J3b's renders), a margin of 3% each side. */
    public static final double FIT_MARGIN = 0.94;

    /** The land office opens on the city's own ground with this share of its half-size round it each way (star): 0.35 - the near part of every offer is in view, where fitting every offer whole would shrink the city (on the lanes, 0.7.61: Jerus's city to a third of the map, his reach 486 plots out against his centre's 173). */
    public static final double OPENING_MARGIN = 0.35;

    /** The levels. */
    public static final int L0 = 0, L1 = 1, L2 = 2, FAR = 3;

    /** The city's block lines are drawn from here: a block at least 6 px across on screen (spec-grid 2.4), so the lines never crowd the ground they mark. */
    public static final double BLOCK_LINES_FROM = 6;

    /** An offer's place is written on it from here: its box at least 11 px each way on screen (0.7.69, star) - a digit of Plex Mono at 11 px is 6.6 px wide and 7.7 px of capital, so it stands clear of the box's edge, its halo just meeting it; MapCheck's town of ten years opened at 2.98 px a plot then, where an offer one block of 120 m across is 11 to 12 px (at 2.74 from 0.7.78, 10 to 11 px: OPENING_OFFER_PX). */
    public static final double PLACE_LABEL_FROM = 11;

    /** The land office opens close enough that its smallest offer is this many px across (0.7.79, star O3-2; LandMap.open()): PLACE_LABEL_FROM and a pixel for the grid's phase, since a box PLACE_LABEL_FROM px across on screen can be drawn a pixel short on whole pixels - so every offer the opening view shows whole holds its number. */
    public static final double OPENING_OFFER_PX = PLACE_LABEL_FROM + 1;

    /** Whether a box on screen (onScreen()) holds an offer's place number: PLACE_LABEL_FROM px each way or more. */
    public static boolean labelFits(double[] box) {
        return box != null && box[2] - box[0] >= PLACE_LABEL_FROM && box[3] - box[1] >= PLACE_LABEL_FROM;
    }

    /** How far past the view's edges the overlay is clipped, in px: 4 - a clipped box's 2 px edge, and a line's square cap, stay off the screen. */
    public static final double CLIP_PX = 4;

    private double width, height, cx, cy, scale;

    /** A view of width x height pixels, looking at the whole world. */
    public MapFrame(double width, double height) {
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
        this.cx = World.SIDE / 2.0;
        this.cy = World.SIDE / 2.0;
        this.scale = leastScale();
    }

    public double width()   { return width; }
    public double height()  { return height; }
    /** Where it looks, in plots. */
    public double centreX() { return cx; }
    public double centreY() { return cy; }
    /** How close: screen pixels a plot. */
    public double scale()   { return scale; }

    /** The farthest the view goes: the whole world across its shorter side - about 28 km a pixel at the land office's expanded size. */
    public double leastScale() { return Math.min(width, height) / World.SIDE; }

    /** A new size, the centre and scale kept (clamped to the new least). */
    public void resize(double w, double h) {
        width = Math.max(1, w);
        height = Math.max(1, h);
        clamp();
    }

    /** Looks at (x, y) in plots, at a scale; both clamped. */
    public void set(double x, double y, double s) {
        cx = x;
        cy = y;
        scale = s;
        clamp();
    }

    private void clamp() {
        if (!(scale > 0)) scale = leastScale();
        scale = Math.max(leastScale(), Math.min(MOST_PX_A_PLOT, scale));
        cx = Math.max(0, Math.min(World.SIDE, cx));
        cy = Math.max(0, Math.min(World.SIDE, cy));
    }

    /** Fits a box of plots (x0, y0) to (x1, y1) into the view, FIT_MARGIN of it, centred. */
    public void fit(double x0, double y0, double x1, double y1) {
        double w = Math.max(1, x1 - x0), h = Math.max(1, y1 - y0);
        set((x0 + x1) / 2, (y0 + y1) / 2, Math.min(width / w, height / h) * FIT_MARGIN);
    }

    /** Zooms by a factor about a screen point: the plot under it stays under it, unless a clamp stops it. */
    public void zoomAt(double sx, double sy, double factor) {
        double px = plotX(sx), py = plotY(sy);
        scale = scale * factor;
        clamp();
        cx = px - (sx - width / 2) / scale;
        cy = py - (sy - height / 2) / scale;
        clamp();
    }

    /** ...by notches of the wheel, ZOOM_STEP each, in (positive) or out. */
    public void zoomNotches(double sx, double sy, double notches) {
        zoomAt(sx, sy, Math.pow(ZOOM_STEP, notches));
    }

    /** A drag of (dx, dy) screen pixels: the ground moves with the pointer. */
    public void pan(double dx, double dy) {
        cx -= dx / scale;
        cy -= dy / scale;
        clamp();
    }

    /** A plot's place on the screen. */
    public double screenX(double x) { return (x - cx) * scale + width / 2; }
    public double screenY(double y) { return (y - cy) * scale + height / 2; }

    /** The plot coordinate under a screen point. */
    public double plotX(double sx) { return cx + (sx - width / 2) / scale; }
    public double plotY(double sy) { return cy + (sy - height / 2) / scale; }

    /* ----------------------------- the overlay on whole pixels (0.7.69, batch M5) -----------------------------
     *
     * The land's overlay - the offers' rectangles, the city's edge, the block
     * grid - lies on plot edges, which the transform puts between pixels at
     * any scale. Drawn there, a line a pixel wide smears over two at half
     * strength. So every plot edge is put on a pixel line first (the nearest
     * pixel boundary: pixelX(), pixelY()), the same for every shape that
     * shares the edge, and a line a pixel wide is drawn through the middle of
     * the pixels on one side of it - crisp, as spec-grid 2.4's "crisp stepped
     * outline" asks.
     * ------------------------------------------------------------------------------------------------------------ */

    /** The pixel line a plot edge lies on, across: the screen column boundary nearest it, a half rounded up (never to even, so edges a plot apart are as many pixels apart wherever they fall), and two shapes sharing the edge share the line. */
    public double pixelX(double x) { return Math.floor(screenX(x) + 0.5); }

    /** ...and down. */
    public double pixelY(double y) { return Math.floor(screenY(y) + 0.5); }

    /**
     * A box of plots [x0, x1) x [y0, y1) on the screen: its edges on their
     * pixel lines, clipped to the view and CLIP_PX round it, as {x0, y0, x1,
     * y1} in pixels - at least a pixel each way - or null when no pixel of it
     * is in view. A box larger than the view covers all of it.
     */
    public double[] onScreen(double x0, double y0, double x1, double y1) {
        double sx0 = pixelX(x0), sy0 = pixelY(y0), sx1 = Math.max(sx0 + 1, pixelX(x1)), sy1 = Math.max(sy0 + 1, pixelY(y1));
        if (sx1 <= 0 || sy1 <= 0 || sx0 >= width || sy0 >= height) return null;
        return new double[] { Math.max(sx0, -CLIP_PX), Math.max(sy0, -CLIP_PX), Math.min(sx1, width + CLIP_PX),
                Math.min(sy1, height + CLIP_PX) };
    }

    /**
     * A run along plot edges on the screen, crisp: from (x0, y0) to (x1, y1),
     * horizontal or vertical, with what it bounds on its right as it runs (a
     * run east has it to the south, as LandMap.outline() gives them) - the
     * pixels a pixel deep inside that ground along the run's pixel line, as a
     * box {x, y, w, h} in whole pixels, clipped to the view and CLIP_PX round
     * it; null when it covers no pixel there (out of view, or under a pixel
     * long). Filled as one shape, the runs of an outline meet at its corners
     * with no pixel drawn twice.
     */
    public double[] runOnScreen(double x0, double y0, double x1, double y1) {
        boolean flat = y0 == y1;
        double line = flat ? pixelY(y0) : pixelX(x0);
        double a = flat ? pixelX(Math.min(x0, x1)) : pixelY(Math.min(y0, y1));
        double b = flat ? pixelX(Math.max(x0, x1)) : pixelY(Math.max(y0, y1));
        // The side it bounds: south of a run east, north of one west, east of one north, west of one south.
        double at = flat ? (x1 > x0 ? line : line - 1) : (y1 > y0 ? line - 1 : line);
        double side = flat ? height : width, along = flat ? width : height;
        a = Math.max(a, -CLIP_PX);
        b = Math.min(b, along + CLIP_PX);
        if (b <= a || at < -CLIP_PX || at >= side + CLIP_PX) return null;
        return flat ? new double[] { a, at, b - a, 1 } : new double[] { at, a, 1, b - a };
    }

    /**
     * The block grid's lines in view (spec-grid 2.4): every multiple of
     * 2^level plots across (columns) or down, as its pixel line - a line a
     * pixel wide is the column or row of pixels after it, crisp; none when a
     * block is under BLOCK_LINES_FROM pixels on screen, and none off the
     * world.
     */
    public double[] blockLines(int level, boolean columns) {
        long b = 1L << level;
        if (b * scale < BLOCK_LINES_FROM) return new double[0];
        double lo = columns ? plotX(0) : plotY(0), hi = columns ? plotX(width) : plotY(height);
        long k0 = Math.max(0, (long) Math.ceil(lo / b)), k1 = Math.min(World.SIDE / b, (long) Math.floor(hi / b));
        if (k1 < k0) return new double[0];
        double[] out = new double[(int) (k1 - k0 + 1)];
        for (int i = 0; i < out.length; i++) out[i] = columns ? pixelX((k0 + i) * b) : pixelY((k0 + i) * b);
        return out;
    }

    /** The level a scale draws at, before the count of tiles is asked: L0, L1 or L2. */
    public static int levelOf(double scale) {
        return scale >= L0_FROM ? L0 : scale >= L1_FROM ? L1 : L2;
    }

    /** The level this view draws at: L0, L1, L2, or FAR when an L2 view holds more than NEAR_TILES_MOST tiles. */
    public int level() {
        int l = levelOf(scale);
        if (l == L2 && tilesInView() > NEAR_TILES_MOST) return FAR;
        return l;
    }

    /** Pixels a plot a tile is rastered at for a level and scale: SMALL_TILE_PX or BIG_TILE_PX at L0, MIDDLE_TILE_PX at L1, FAR_TILE_PX at L2; 0 at FAR. */
    public static int tilePx(int level, double scale) {
        switch (level) {
            case L0: return scale > BIG_TILES_FROM ? BIG_TILE_PX : SMALL_TILE_PX;
            case L1: return MIDDLE_TILE_PX;
            case L2: return FAR_TILE_PX;
            default: return 0;
        }
    }

    /** The block a level's tiles are drawn in (TileRaster.blocks()): 0 at L0 (buildings one by one), BLOCK_MIDDLE at L1, BLOCK_FAR at L2. */
    public static int blockOf(int level) {
        return level == L1 ? TileRaster.BLOCK_MIDDLE : level == L2 ? TileRaster.BLOCK_FAR : 0;
    }

    /** The first and last tile columns and rows in view (inclusive). */
    public long tileX0() { return Math.floorDiv((long) Math.floor(plotX(0)), World.TILE); }
    public long tileX1() { return Math.floorDiv((long) Math.floor(plotX(width)), World.TILE); }
    public long tileY0() { return Math.floorDiv((long) Math.floor(plotY(0)), World.TILE); }
    public long tileY1() { return Math.floorDiv((long) Math.floor(plotY(height)), World.TILE); }

    /** How many tiles the view holds, whole and in part. */
    public long tilesInView() { return (tileX1() - tileX0() + 1) * (tileY1() - tileY0() + 1); }

    /** The most tiles a view this size holds at a scale, wherever it looks: what a level's cache must hold for one screen. */
    public long tilesAt(double s) {
        double t = World.TILE * s;
        return ((long) Math.ceil(width / t) + 1) * ((long) Math.ceil(height / t) + 1);
    }

    /** Plots a far node's pixel stands for: the least power of two at least a screen pixel wide, so a node is shown at NODE_PX to twice that. */
    public long nodePlots() {
        long p = 1;
        while (p * scale < 1 && p < World.SIDE) p <<= 1;
        return p;
    }

    /** A far node's side in plots. */
    public long nodeSpan() { return NODE_PX * nodePlots(); }

    /** The first and last far nodes in view (inclusive), in node spans from the world's corner, clipped to the world. */
    public long nodeX0() { return Math.max(0, Math.floorDiv((long) Math.floor(plotX(0)), nodeSpan())); }
    public long nodeX1() { return Math.min((World.SIDE - 1) / nodeSpan(), Math.floorDiv((long) Math.floor(plotX(width)), nodeSpan())); }
    public long nodeY0() { return Math.max(0, Math.floorDiv((long) Math.floor(plotY(0)), nodeSpan())); }
    public long nodeY1() { return Math.min((World.SIDE - 1) / nodeSpan(), Math.floorDiv((long) Math.floor(plotY(height)), nodeSpan())); }

    /** The most far nodes a view this size holds: what the node cache must hold. */
    public long nodesAtMost() {
        return ((long) Math.ceil(width / NODE_PX) + 1) * ((long) Math.ceil(height / NODE_PX) + 1);
    }

    /** The scale bar: the longest of 1, 2 and 5 times a power of ten metres no wider than mostPx, as {metres, pixels}. */
    public double[] scaleBar(double mostPx) {
        double perPx = World.PLOT_M / scale;
        double most = mostPx * perPx, metres = 1;
        while (metres * 10 <= most) metres *= 10;
        if (metres * 5 <= most) metres *= 5;
        else if (metres * 2 <= most) metres *= 2;
        return new double[] { metres, metres / perPx };
    }

    /** A scale bar's words: "500 m", "2 km", "1,000 km". */
    public static String scaleWords(double metres) {
        return metres < 1000 ? String.format("%,.0f m", metres) : String.format("%,.0f km", metres / 1000);
    }
}
