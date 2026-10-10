package ham.citybuildersim;

import java.util.Arrays;

/**
 * A painted tile as pixels: an int[] of 0xAARRGGBB, a row at a time, at a whole number of pixels a plot - what the map view hands PixelWriter.setPixels() for a tile's image.
 *
 * WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.6). The
 * tile painter says what each plot is; the map draws a tile as one image,
 * kept in a cache, at 4 px a plot for the near view (8 px above 6 px a plot)
 * and, as blocks, at 2 px for the middle view and 1 px for the far (MapFrame,
 * 0.7.61). This turns a painted tile into that image, in the mockup's
 * colours: its ground (the legend's grass, forest, fresh water, sea and
 * beach), ground the city does not own dimmed toward the
 * map's background, a resource's sites tinted with its colour - half grey
 * where its holding is being worked, grey where it is worked out (the
 * mockup's star 7) - the roads by kind, and every building in its class's
 * fill, edged from 6 px a plot (the mockup's), the flats darker; a mine or
 * well grey on its site with a mark of its resource's colour at its centre.
 * Pure: the same tile gives the same pixels. The design measured 24 to 31 us
 * a tile at 4 px, 81 to 134 at 8, 8 at 1.
 *
 * THE MOCKUP'S WIDTHS AND SIZES (J3b). J3 filled a road's whole plot and drew
 * every building a pixel in from its plot, so at 4 px a plot a home was a
 * 2 x 2 speck on the grass. From 4 px a road was the mockup's line - gravel
 * 0.3 of its plot, paved 0.52 with a dark casing, a highway 0.86 with its
 * own and a dashed centre line from 6 px - joined to each road beside it at
 * the narrower one's width, with its bridge's deck under it (a highway's
 * still is; a street's since 0.7.88 is its verge and surface, below); a one-plot
 * building is drawn at its own land's side (0.7.64: a House 0.91 of its
 * plot; the mockup's 0.6 to 0.92 by a hash before), set against the road it
 * faces, so a street's homes line it; and
 * below 4 px a paved road is drawn in its casing's grey, which a pixel of
 * stands out from the grass where its own does not. BLOCKS (blocks()) are
 * the mockup's middle and far views: each 4 or 8 plots square in its
 * dominant kind's colour at an opacity by how much of it is built, the roads
 * over them - what the whole city looks like at the land office's zoom.
 *
 * TRACK (0.7.72, batch N3). The railway's lines are drawn plot by plot as
 * track (TilePainter.RAIL): from 4 px a plot a line of a paved road's
 * width, cased in RAIL_LINE and dashed white plot by plot, joined to the
 * track beside it, on a deck over fresh water; a road crossing it drawn over
 * it; below 4 px and in the blocks, its plots in RAIL_LINE. The mockup drew
 * no railway, so the colours are a map's convention (star N3-9).
 *
 * STREETS AS VERGE AND SURFACE (0.7.88, batch RD2; the project's
 * spec-roads-and-ports.md 2.1 and 5, its prototype's render()). A street's
 * plot is its right of way: drawn as VERGE, with its surface down its middle
 * - gravel tan, paved grey, a band half the plot wide for a street of half
 * width (15 m) and the whole plot for one of full width (30 m) - joined to
 * the street beside it each way it runs; a TRACK, a street the city has
 * bought no road for, a dashed brown line on the verge; an arterial and a
 * boulevard's two rows drawn as the streets they are; a bridge's deck under
 * its surface. A highway is drawn over a street that passes beneath it, dark
 * with its dashed centre line. Ground under a building is drawn cleared
 * (forest as grass; spec 2.6, the zoning study's Z7), and a building packed
 * at the city's edge without a street (R7) a shade darker.
 */
public final class TileRaster {

    private TileRaster() { }

    /** Each ground class's colour, by World's class (GRASS, FOREST, FRESH, SALT, SAND): the mockup's legend. */
    public static final int[] GROUND = { 0xffa8c47e, 0xff527c45, 0xff78b4dc, 0xff2f5d88, 0xffe6daaa };

    /** Each road kind's colour (gravel, paved, highway): the mockup's MAP. */
    public static final int[] ROAD = { 0, 0xffcdb07c, 0xffa4aab0, 0xff3b4048 };

    /** The railway's line, and its casing from 4 px a plot (0.7.72): openstreetmap-carto's rail grey, #707070 (the mockup drew no railway; star N3-9)... */
    public static final int RAIL_LINE = 0xff707070;

    /** ...and its dashes, white on every other plot along it: carto's rail dash. */
    public static final int RAIL_DASH = 0xffffffff;

    /** A bridge's deck, edging a road over fresh water from 4 px a plot: the mockup's. */
    public static final int DECK = 0xff5d4c3c;

    /** Ground no one in the city owns is drawn this much of the way to the map's background: 40%. */
    public static final double UNOWNED_DIM = 0.4;

    /** The map's background, which unowned ground is dimmed toward: the mockup's --map-void. */
    public static final int VOID = 0xff16222c;

    /** A site's tint over its ground, its resource's colour this much of the way: 36% (the mockup's most for a deposit's body). */
    public static final double SITE_TINT = 0.36;

    /** ...and on the plots its own hash speckles, 75% (the mockup's ore showing through). */
    public static final double SITE_SPECKLE = 0.75;

    /** The share of a site's plots speckled: 15%. */
    public static final double SPECKLE_SHARE = 0.15;

    /** A worked-out site's grey: the mockup's mined grey. */
    public static final int WORKED_GREY = 0xff929496;

    /** A mine's ring and centre mark: the mockup's ring. */
    public static final int RING = 0xff2b2d31;

    /** Pixels a plot from which a building of more than one plot is inset a pixel: 3 (a one-plot building takes its own land's side: SMALL_MOST). */
    public static final int INSET_FROM = 3;

    /** ...and from which it is edged: 6 (the mockup's outlines). */
    public static final int EDGE_FROM = 6;

    /** A road's width as a share of its plot, by kind (gravel, paved, highway): the mockup's 0.3, 0.52 and 0.86 - since 0.7.88 the highway's and (as a paved road's) the track's; a street's surface is its width's (SURFACE). */
    public static final double[] ROAD_WIDTH = { 0, 0.30, 0.52, 0.86 };

    /** ...and its least, in pixels, at the near view: 1, 2 and 3 (the mockup's 1, 1.5 and 2.5, whole). */
    public static final int[] ROAD_LEAST_PX = { 0, 1, 2, 3 };

    /** A highway's casing, a pixel either side from 6 px (the mockup's highwayCase)... */
    public static final int HIGHWAY_CASE = 0xff1d2126;

    /** ...and its dashed centre line from 6 px a plot, a pixel wide, on the first 55% of each plot along it (the mockup's centre, its dash 0.7 on and 0.55 off). */
    public static final int CENTRE_LINE = 0xfff0cf5a;

    /** Pixels a plot from which a road is drawn as a line of its own width on its ground - a street as verge and surface (0.7.88) - and below, the whole plot in its colour: 4, the near view's image. */
    public static final int LINES_FROM = 4;

    /** A one-plot building's side as a share of its plot: its own land's (BuildingVisual.plotSide(), 0.7.64 - a House 0.91 of the plot, a Convenience Store 0.72; the mockup's 0.6 to 0.92 by a hash before), never more than 0.92, so a street's buildings stay apart (the mockup's)... */
    public static final double SMALL_MOST = 0.92;

    /** ...and set this far from the road it faces: 0.07 of a plot, so it hugs its street (the mockup's). */
    public static final double SET_BACK = 0.07;


    /** A street's verge, its right of way either side of its surface (0.7.88): the prototype's VERGE (spec-roads-and-ports.md 5, roads_proto.py). */
    public static final int VERGE = 0xffbfd39a;

    /** A track's dashes, a street the city has bought no road for (0.7.88): the prototype's brown (spec 2.5, 5: "a dashed brown line on the verge"). */
    public static final int TRACK_DASH = 0xff8a6a3c;

    /** A street's surface across its plot, by its width (Painted.width: 0, half, full): none, half the plot (15 m), the whole plot (30 m) - the right of way is 30 m (spec 2.1; the prototype's W_MIN and W_FULL). */
    public static final double[] SURFACE = { 0, DistrictPlan.HALF, DistrictPlan.FULL };

    /** A building packed at the city's edge without a street (R7) is drawn this much of the way to the map's background: 35% (star RD2-2) - a shade darker, so it reads as standing apart from the streets, at its own size and place. */
    public static final double PACKED_DIM = 0.35;

    /**
     * Fills img with the tile at px pixels a plot: (32 px) squared pixels,
     * row by row. Below LINES_FROM a road plot is its colour; from it, the
     * streets are drawn as verge and surface, the track and the highways as
     * lines, joined across the tile's edge where the plans have them go on
     * (ways()). A one-plot building is drawn the mockup's size, hugging the
     * street it faces; a larger one inset a pixel from 3 px a plot; all
     * edged from 6; one packed without a street a shade darker.
     */
    public static void raster(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        boolean lines = px >= LINES_FROM;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            int c = lines ? groundColour(in, p, i) : plotColour(in, p, i);
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            for (int y = 0; y < px; y++) {
                int o = (y0 + y) * w + x0;
                for (int x = 0; x < px; x++) img[o + x] = c;
            }
        }
        if (lines) ways(in, p, px, img);
        for (int b = 0; b < p.buildings; b++) {
            BuildingVisual.Type t = p.btype[b] >= 0 && p.btype[b] < in.types.length ? in.types[p.btype[b]] : null;
            // Its type's fill and edge: its class's, the flats darker, and (0.7.97) the refinery's units, a terminal and a tank farm their own.
            int fill = t == null ? BuildingVisual.FILL[BuildingVisual.INDUSTRY] : t.fill();
            int edge = t == null ? BuildingVisual.EDGE[BuildingVisual.INDUSTRY] : t.edge();
            if (p.bpacked[b]) {
                fill = blend(fill, VOID, PACKED_DIM);
                edge = blend(edge, VOID, PACKED_DIM);
            }
            int x0, y0, x1, y1;
            int site = p.bsite[b];
            if (p.bw[b] == 1 && p.bh[b] == 1 && site < 0) {
                // One plot: its own land's side (BuildingVisual.plotSide(), 0.7.64), hugging the street it faces.
                int i = p.by[b] * tile + p.bx[b];
                int sw = Math.max(1, Math.min(px, (int) Math.round(px * Math.min(SMALL_MOST, BuildingVisual.plotSide(t)))));
                int sh = sw;
                int ox = (px - sw) / 2, oy = (px - sh) / 2;
                int set = (int) Math.round(SET_BACK * px);
                int face = faceOf(p, i);
                if (face == 0) oy = set; else if (face == 2) oy = px - set - sh;
                else if (face == 3) ox = set; else if (face == 1) ox = px - set - sw;
                ox = Math.max(0, Math.min(px - sw, ox));
                oy = Math.max(0, Math.min(px - sh, oy));
                x0 = p.bx[b] * px + ox;
                y0 = p.by[b] * px + oy;
                x1 = x0 + sw;
                y1 = y0 + sh;
            } else {
                int inset = px >= INSET_FROM ? 1 : 0;
                x0 = p.bx[b] * px + inset;
                y0 = p.by[b] * px + inset;
                x1 = Math.min(tile, p.bx[b] + p.bw[b]) * px - inset;
                y1 = Math.min(tile, p.by[b] + p.bh[b]) * px - inset;
            }
            boolean edged = px >= EDGE_FROM;
            for (int y = y0; y < y1; y++) {
                for (int x = x0; x < x1; x++) {
                    boolean onEdge = edged && (y == y0 || y == y1 - 1 || x == x0 || x == x1 - 1);
                    img[y * w + x] = onEdge ? edge : fill;
                }
            }
            if (site >= 0 && px >= 4) {
                // A mine's mark: its resource's colour at its centre, ringed.
                int cx = (x0 + x1) / 2, cy = (y0 + y1) / 2, r = Math.max(1, px / 2);
                int colour = 0xff000000 | Resource.values()[in.siteKind[site]].colour();
                for (int y = cy - r; y <= cy + r; y++) {
                    for (int x = cx - r; x <= cx + r; x++) {
                        if (x < x0 || y < y0 || x >= x1 || y >= y1) continue;
                        boolean ring = y == cy - r || y == cy + r || x == cx - r || x == cx + r;
                        img[y * w + x] = ring ? RING : colour;
                    }
                }
            }
        }
        overBuildings(in, p, px, img);
        atSea(in, p, px, img);
    }

    /* =====================================================================
       AT SEA (0.7.97, batch O13; TilePainter's AT SEA; mockup 3's colours)
       ===================================================================== */

    /** A quay's deck: mockup 3's jetty, #3a4655... */
    public static final int QUAY_DECK = 0xff3a4655;

    /** ...and its edge from EDGE_FROM px a plot: the mockup's #5a6676. */
    public static final int QUAY_EDGE = 0xff5a6676;

    /** A platform's jacket: mockup 3's #2a3540... */
    public static final int JACKET_FILL = 0xff2a3540;

    /** ...edged in the mockup's tan, #c9b68f - its ring's and the pipe's colour too (the ore's). */
    public static final int OIL_TAN = 0xffc9b68f;

    /** A platform's 500 m ring over the water: the tan at the mockup's 25%... */
    public static final double RING_ALPHA = 0.25;

    /** ...dashed 3 px on, 3 off along its arc (the mockup's "3 3"). */
    public static final double RING_DASH_PX = 3;

    /** A crude pipeline: the tan at the mockup's 50%, dashed 2 px on and 4 off (its "2 4"), 1.5 px wide (drawn 1 below 4 px a plot, 2 from it). */
    public static final double PIPE_ALPHA = 0.5;
    public static final double PIPE_ON_PX = 2, PIPE_OFF_PX = 4;

    /**
     * The works at sea over the tile's picture: each quay plot the deck,
     * edged from EDGE_FROM; a jacket the mockup's platform, edged; a well its
     * tan dot; then every pixel of a plot a ring or a pipe crosses that lies
     * on its line (within half its width) and on a dash - the dashes measured
     * from the ring's east and along the pipe from its field, so they run on
     * across a tile's edge.
     */
    static void atSea(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        boolean edged = px >= EDGE_FROM;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            int s = p.sea[i] & TilePainter.SEA_WORK;
            if (s == 0) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            if (s == TilePainter.WELL) {
                int r = Math.max(1, px / 4), cx = x0 + px / 2, cy = y0 + px / 2;
                fillRect(img, w, Math.max(x0, cx - r), Math.max(y0, cy - r), Math.min(x0 + px, cx + r), Math.min(y0 + px, cy + r), OIL_TAN);
                continue;
            }
            int fill = s == TilePainter.QUAY ? QUAY_DECK : JACKET_FILL, edge = s == TilePainter.QUAY ? QUAY_EDGE : OIL_TAN;
            fillRect(img, w, x0, y0, x0 + px, y0 + px, fill);
            if (!edged) continue;
            // Its edge where the next plot is not the same work: a jetty's sides, a jacket's outline.
            for (int dir = 0; dir < 4; dir++) {
                int nx = i % tile + TilePainter.DX[dir], ny = i / tile + TilePainter.DY[dir];
                boolean same = nx >= 0 && ny >= 0 && nx < tile && ny < tile && (p.sea[ny * tile + nx] & TilePainter.SEA_WORK) == s;
                if (same) continue;
                if (dir == 0) fillRect(img, w, x0, y0, x0 + px, y0 + 1, edge);
                else if (dir == 2) fillRect(img, w, x0, y0 + px - 1, x0 + px, y0 + px, edge);
                else if (dir == 3) fillRect(img, w, x0, y0, x0 + 1, y0 + px, edge);
                else fillRect(img, w, x0 + px - 1, y0, x0 + px, y0 + px, edge);
            }
        }
        if (in.rings == 0 && in.pipes == 0) return;
        double pipeHalf = (px >= LINES_FROM ? 2 : 1) / 2.0, ringHalf = 0.5;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            int s = p.sea[i];
            if ((s & (TilePainter.SEA_RING | TilePainter.SEA_PIPE)) == 0) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            for (int y = y0; y < y0 + px; y++) {
                for (int x = x0; x < x0 + px; x++) {
                    double fx = (x + 0.5) / px, fy = (y + 0.5) / px;
                    boolean on = false;
                    double alpha = 0;
                    if ((s & TilePainter.SEA_RING) != 0) {
                        for (int r = 0; r < in.rings && !on; r++) {
                            double dx = fx - in.ringX[r], dy = fy - in.ringY[r], d = Math.hypot(dx, dy);
                            if (Math.abs(d - TilePainter.RING_PLOTS) * px > ringHalf) continue;
                            double arc = (Math.atan2(dy, dx) + Math.PI) * TilePainter.RING_PLOTS * px;
                            if (((long) Math.floor(arc / RING_DASH_PX)) % 2 == 0) { on = true; alpha = RING_ALPHA; }
                        }
                    }
                    if ((s & TilePainter.SEA_PIPE) != 0) {
                        for (int q = 0; q < in.pipes; q++) {
                            double ax = in.pipeAX[q], ay = in.pipeAY[q], bx = in.pipeBX[q], by = in.pipeBY[q];
                            double vx = bx - ax, vy = by - ay, len = Math.hypot(vx, vy);
                            if (!(len > 0)) continue;
                            double t = ((fx - ax) * vx + (fy - ay) * vy) / (len * len);
                            if (t < 0 || t > 1) continue;
                            double off = Math.abs((fx - ax) * vy - (fy - ay) * vx) / len;
                            if (off * px > pipeHalf) continue;
                            double along = t * len * px;
                            if (along % (PIPE_ON_PX + PIPE_OFF_PX) < PIPE_ON_PX) { on = true; alpha = Math.max(alpha, PIPE_ALPHA); }
                        }
                    }
                    if (on) img[y * w + x] = blend(img[y * w + x], OIL_TAN, alpha);
                }
            }
        }
    }

    /** Whether plot i of the runs is a highway's (its own, or the track bridging it), under a building or not (0.7.89). */
    static boolean runHighway(TilePainter.Painted p, int i) {
        return p.run[i] == BuildingVisual.HIGHWAY || p.run[i] == TilePainter.RAIL_OVER;
    }

    /**
     * A highway passing over a building (0.7.89): a mine standing on its site
     * where the elevated highway crosses it - drawn over the building, its
     * arms to the highway beside it (spec 2.7: elevated).
     */
    private static void overBuildings(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        int[] cs = crossSection(BuildingVisual.HIGHWAY, px);
        int width = px >= LINES_FROM ? cs[0] : px, o = (px - width) / 2;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.use[i] != TilePainter.BUILDING || !runHighway(p, i)) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            int c = in.owned[i] ? ROAD[BuildingVisual.HIGHWAY] : blend(ROAD[BuildingVisual.HIGHWAY], VOID, UNOWNED_DIM);
            int a = o, z = o + width;
            fillRect(img, w, x0 + a, y0 + a, x0 + z, y0 + z, c);
            if (runHighwayAt(in, p, i, 0)) fillRect(img, w, x0 + a, y0, x0 + z, y0 + a, c);
            if (runHighwayAt(in, p, i, 2)) fillRect(img, w, x0 + a, y0 + z, x0 + z, y0 + px, c);
            if (runHighwayAt(in, p, i, 3)) fillRect(img, w, x0, y0 + a, x0 + a, y0 + z, c);
            if (runHighwayAt(in, p, i, 1)) fillRect(img, w, x0 + z, y0 + a, x0 + px, y0 + z, c);
        }
    }

    /** Whether the plot a step from plot i, across the tile's edge too, is a highway's of the runs (under a building or not). */
    private static boolean runHighwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir) {
        int tile = TilePainter.TILE, x = i % tile + TilePainter.DX[dir], y = i / tile + TilePainter.DY[dir];
        if (x >= 0 && y >= 0 && x < tile && y < tile) return runHighway(p, y * tile + x);
        int along = dir == 0 || dir == 2 ? i % tile : i / tile;
        byte f = in.fixedBeyond[dir * tile + along];
        return f == BuildingVisual.HIGHWAY || f == TilePainter.RAIL_OVER;
    }

    /* =====================================================================
       THE MIDDLE AND FAR VIEWS AS BLOCKS (J3b: the mockup's levels 1 and 2)
       ===================================================================== */

    /** A block's opacity over its ground by its share built: 40%, 60%, 80% and 96% (the mockup's ALPHA)... */
    public static final double[] BLOCK_ALPHA = { 0.4, 0.6, 0.8, 0.96 };

    /** ...at a share built under 12%, under 28%, under 50%, and above (the mockup's). */
    public static final double[] BLOCK_FILLS = { 0.12, 0.28, 0.5 };

    /** Blocks the mockup drew at its middle view, in plots a side: 4 (120 m), from 1.4 to 3.2 px a plot... */
    public static final int BLOCK_MIDDLE = 4;

    /** ...and at its far view: 8 (240 m), below 1.4 px a plot, with gravel hidden. */
    public static final int BLOCK_FAR = 8;

    /**
     * A tile at px pixels a plot as the mockup drew its middle and far
     * views: its ground, then blocks of `block` plots a side, each in its
     * dominant kind's colour - by its buildings' plots, the homes' houses and
     * flats together and drawn as flats where flats are the more or the
     * block is the densest - over its ground at BLOCK_ALPHA by its share
     * built, then the roads in their colours over them, gravel and tracks
     * only when asked. Pure: the same tile gives the same pixels.
     */
    public static void blocks(TilePainter.Input in, TilePainter.Painted p, int px, int block, boolean gravel, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            int c = groundColour(in, p, i);
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            fillRect(img, w, x0, y0, x0 + px, y0 + px, c);
        }
        int kinds = BuildingVisual.CLASSES + 1, flatsKey = BuildingVisual.CLASSES;
        int[] key = new int[p.buildings];
        for (int b = 0; b < p.buildings; b++) {
            BuildingVisual.Type t = p.btype[b] >= 0 && p.btype[b] < in.types.length ? in.types[p.btype[b]] : null;
            int cls = t == null ? BuildingVisual.INDUSTRY : t.cls();
            key[b] = t != null && t.flats() ? flatsKey : cls;
        }
        int[] plots = new int[kinds];
        for (int by = 0; by < tile; by += block) {
            for (int bx = 0; bx < tile; bx += block) {
                Arrays.fill(plots, 0);
                int built = 0, area = 0;
                for (int y = by; y < Math.min(tile, by + block); y++) {
                    for (int x = bx; x < Math.min(tile, bx + block); x++) {
                        int i = y * tile + x;
                        area++;
                        if (p.use[i] != TilePainter.BUILDING || p.bld[i] == 0) continue;
                        plots[key[p.bld[i] - 1]]++;
                        built++;
                    }
                }
                if (built == 0) continue;
                int homes = plots[BuildingVisual.HOME] + plots[flatsKey], best = BuildingVisual.HOME, most = homes;
                for (int k = 1; k < BuildingVisual.CLASSES; k++) if (plots[k] > most) { most = plots[k]; best = k; }
                double f = built / (double) area;
                int lvl = f < BLOCK_FILLS[0] ? 0 : f < BLOCK_FILLS[1] ? 1 : f < BLOCK_FILLS[2] ? 2 : 3;
                int colour = best == BuildingVisual.HOME
                        ? (plots[flatsKey] >= plots[BuildingVisual.HOME] || lvl == 3 ? BuildingVisual.FLATS_FILL : BuildingVisual.FILL[BuildingVisual.HOME])
                        : BuildingVisual.FILL[best];
                for (int y = by; y < Math.min(tile, by + block); y++) {
                    for (int x = bx; x < Math.min(tile, bx + block); x++) {
                        int o = (y * px) * w + x * px;
                        int c = blend(img[o], colour, BLOCK_ALPHA[lvl]);
                        fillRect(img, w, x * px, y * px, x * px + px, y * px + px, c);
                    }
                }
            }
        }
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            int kind = roadKind(p, i);
            boolean hidden = !gravel && (kind == BuildingVisual.GRAVEL || kind == TilePainter.TRACK);
            if (p.use[i] == TilePainter.RAIL && (kind == 0 || hidden)) {
                // Track (0.7.72), and where a hidden street crosses it.
                int x0 = (i % tile) * px, y0 = (i / tile) * px;
                fillRect(img, w, x0, y0, x0 + px, y0 + px, in.owned[i] ? RAIL_LINE : blend(RAIL_LINE, VOID, UNOWNED_DIM));
                continue;
            }
            if (kind == 0 || hidden) continue;
            int c = kind == TilePainter.TRACK ? TRACK_DASH : ROAD[kind];
            if (!in.owned[i]) c = blend(c, VOID, UNOWNED_DIM);
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            fillRect(img, w, x0, y0, x0 + px, y0 + px, c);
            // A highway at least HIGHWAY_LEAST_PX wide: the next plot across it too (the mockup's 1.8 px at its far view).
            if (isHighway(p, i) && px < HIGHWAY_LEAST_PX) {
                int x = i % tile, y = i / tile;
                boolean ns = (y > 0 && isHighway(p, i - tile)) || (y < tile - 1 && isHighway(p, i + tile));
                if (ns && x < tile - 1) fillRect(img, w, x0 + px, y0, x0 + 2 * px, y0 + px, c);
                if (!ns && y < tile - 1) fillRect(img, w, x0, y0 + px, x0 + px, y0 + 2 * px, c);
            }
        }
        // The works at sea (0.7.97): the quays and jackets in their colours, the rings and pipes as lines.
        atSea(in, p, px, img);
    }

    /** The least a highway is drawn across, in pixels, in the blocks' views: 2 (the mockup's 1.8 at its far view, 2 at its middle). */
    public static final int HIGHWAY_LEAST_PX = 2;

    /** Whether plot i is a highway's own (a run's, not a street of highway plots). */
    static boolean isHighway(TilePainter.Painted p, int i) {
        return p.use[i] == TilePainter.ROAD && p.road[i] == BuildingVisual.HIGHWAY && p.role[i] == 0;
    }

    /** Whether plot i is a street (a track among them), or a highway's plot with a street beneath. */
    static boolean isStreet(TilePainter.Painted p, int i) {
        return (p.use[i] == TilePainter.ROAD || p.use[i] == TilePainter.RAIL) && (p.role[i] != 0 || p.beneath[i]);
    }

    /** The kind of road on plot i - GRAVEL, PAVED, HIGHWAY or (0.7.88) TilePainter.TRACK - a road's plot, or where a street crosses the track - or 0. */
    static int roadKind(TilePainter.Painted p, int i) {
        byte u = p.use[i];
        return u == TilePainter.ROAD || u == TilePainter.RAIL ? p.road[i] : 0;
    }

    /** The side of plot i a street lies on, north, east, south, west, or -1 for none. */
    static int faceOf(TilePainter.Painted p, int i) {
        int tile = TilePainter.TILE, x = i % tile, y = i / tile;
        if (y > 0 && roadKind(p, i - tile) != 0) return 0;
        if (x < tile - 1 && roadKind(p, i + 1) != 0) return 1;
        if (y < tile - 1 && roadKind(p, i + tile) != 0) return 2;
        if (x > 0 && roadKind(p, i - 1) != 0) return 3;
        return -1;
    }

    /** A road's line across its plot, by kind at px pixels a plot: {its whole width, its casing either side} - the highway's and (as a paved road's) the track's. */
    static int[] crossSection(int kind, int px) {
        if (kind == BuildingVisual.GRAVEL) return new int[] { Math.min(px, Math.max(ROAD_LEAST_PX[1], (int) Math.round(ROAD_WIDTH[1] * px))), 0 };
        int pavedFill = Math.max(ROAD_LEAST_PX[2], (int) Math.round(ROAD_WIDTH[2] * px));
        int pavedCase = px >= 4 ? 1 : 0;
        int paved = Math.min(px, pavedFill + 2 * pavedCase);
        if (kind == BuildingVisual.PAVED) return new int[] { paved, pavedCase };
        int fill = Math.max(ROAD_LEAST_PX[3], (int) Math.round(ROAD_WIDTH[3] * px));
        int cas = px >= 6 ? 1 : 0;
        return new int[] { Math.max(paved, Math.min(px, fill + 2 * cas)), cas };
    }

    /** Whether the plot a step from plot i - across the tile's edge too, as the plans have it (Input.beyond) - is a way of the street at i: a street, or a highway's plot a street passes beneath. */
    private static boolean streetAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir) {
        int tile = TilePainter.TILE, x = i % tile + TilePainter.DX[dir], y = i / tile + TilePainter.DY[dir];
        if (x >= 0 && y >= 0 && x < tile && y < tile) return isStreet(p, y * tile + x);
        int along = dir == 0 || dir == 2 ? i % tile : i / tile;
        return (in.beyond[dir * tile + along] & TilePainter.S_KIND) != DistrictPlan.NONE;
    }

    /** ...and a highway's own plot, a step from plot i. */
    private static boolean highwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir) {
        int tile = TilePainter.TILE, x = i % tile + TilePainter.DX[dir], y = i / tile + TilePainter.DY[dir];
        if (x >= 0 && y >= 0 && x < tile && y < tile) return isHighway(p, y * tile + x);
        int along = dir == 0 || dir == 2 ? i % tile : i / tile;
        byte f = in.fixedBeyond[dir * tile + along];
        return f == BuildingVisual.HIGHWAY || f == TilePainter.RAIL_OVER;
    }

    /** ...and the railway's track (since 0.7.89 where it bridges a highway too: the runs' RAIL_OVER). */
    private static boolean railAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir) {
        int tile = TilePainter.TILE, x = i % tile + TilePainter.DX[dir], y = i / tile + TilePainter.DY[dir];
        if (x >= 0 && y >= 0 && x < tile && y < tile) return p.use[y * tile + x] == TilePainter.RAIL || p.run[y * tile + x] == TilePainter.RAIL_OVER;
        int along = dir == 0 || dir == 2 ? i % tile : i / tile;
        byte f = in.fixedBeyond[dir * tile + along];
        return f == TilePainter.RAIL || f == TilePainter.RAIL_OVER;
    }

    /** Whether plot i is a 45-degree stretch's (the runs' M_DIAG): drawn as a band along its line. */
    static boolean diagonal(TilePainter.Input in, int i) {
        return (in.marks[i] & CityRuns.M_DIAG) != 0;
    }

    /**
     * A 45-degree stretch's band across plot i (spec 2.7: "a curve is drawn
     * smooth; in plots it is a staircase"): the pixels within `half` pixels
     * of the stretch's line - through the plot's middle, or for a
     * staircase's corner (CityRuns.M_CORNER) the middle of the plot before it
     * - in colour c; and with `centre`, every other plot's run of the line
     * itself in it instead (the dashes).
     */
    static void diagBand(int[] img, int w, int i, int px, byte mark, double half, int c) {
        int tile = TilePainter.TILE, x0 = (i % tile) * px, y0 = (i / tile) * px;
        int code = (mark >> CityRuns.M_DIAG_SHIFT) & 3;
        int dx = code == 0 || code == 3 ? 1 : -1, dy = code == 0 || code == 1 ? 1 : -1;
        double pxl = (mark & CityRuns.M_CORNER) != 0 ? (0.5 - dx) * px : 0.5 * px, pyl = 0.5 * px;
        for (int v = 0; v < px; v++) {
            for (int u = 0; u < px; u++) {
                double qx = u + 0.5 - pxl, qy = v + 0.5 - pyl;
                if (Math.abs(qx * dy - qy * dx) / Math.sqrt(2) <= half) img[(y0 + v) * w + x0 + u] = c;
            }
        }
    }

    /**
     * The ways as lines (from LINES_FROM px a plot; spec 5): each street's
     * verge, its whole plot on dry ground; the decks of bridges; the track;
     * each street's surface down its middle along each way it runs - half
     * the plot for a half-width street, all of it for a full one - in its
     * kind's colour, or a track's dashes; then the highways over all, cased,
     * with a dashed centre line from 6 px.
     */
    private static void ways(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        // The verges.
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isStreet(p, i) || p.bridge[i]) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            fillRect(img, w, x0, y0, x0 + px, y0 + px, in.owned[i] ? VERGE : blend(VERGE, VOID, UNOWNED_DIM));
        }
        // Bridge decks: a pixel wider than the surface (or the track, or a highway) either side, along it.
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!p.bridge[i] || (p.use[i] != TilePainter.ROAD && p.use[i] != TilePainter.RAIL)) continue;
            int across;
            boolean ns, ew;
            if (isHighway(p, i)) {
                across = crossSection(BuildingVisual.HIGHWAY, px)[0];
                ns = highwayAt(in, p, i, 0) || highwayAt(in, p, i, 2);
                ew = !ns;
            } else if (p.use[i] == TilePainter.RAIL && p.road[i] == 0) {
                across = crossSection(BuildingVisual.PAVED, px)[0];
                ns = railAt(in, p, i, 0) || railAt(in, p, i, 2);
                ew = !ns;
            } else {
                across = Math.max(1, (int) Math.round(SURFACE[Math.max(1, (int) p.width[i])] * px));
                ns = streetAt(in, p, i, 0) || streetAt(in, p, i, 2);
                ew = streetAt(in, p, i, 1) || streetAt(in, p, i, 3) || !ns;
            }
            int width = Math.min(px, across + 2), o = (px - width) / 2;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            int deck = in.owned[i] ? DECK : blend(DECK, VOID, UNOWNED_DIM);
            if (ns) fillRect(img, w, x0 + o, y0, x0 + o + width, y0 + px, deck);
            if (ew) fillRect(img, w, x0, y0 + o, x0 + px, y0 + o + width, deck);
        }
        track(in, p, px, img);
        // The streets' surfaces, and the tracks' dashes.
        int dashT = Math.max(2, px / 5), dashStep = Math.max(2, px / 2), dashLen = Math.max(2, px / 3);
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isStreet(p, i) || p.beneath[i]) continue;
            int kind = p.road[i];
            boolean h = streetAt(in, p, i, 1) || streetAt(in, p, i, 3), v = streetAt(in, p, i, 0) || streetAt(in, p, i, 2);
            if (!h && !v) h = true;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            if (kind == TilePainter.TRACK) {
                int c = in.owned[i] ? TRACK_DASH : blend(TRACK_DASH, VOID, UNOWNED_DIM);
                int mid = (px - dashT) / 2;
                for (int s = 0; s < px; s += dashStep) {
                    int e = Math.min(px, s + dashLen);
                    if (h) fillRect(img, w, x0 + s, y0 + mid, x0 + e, y0 + mid + dashT, c);
                    if (v) fillRect(img, w, x0 + mid, y0 + s, x0 + mid + dashT, y0 + e, c);
                }
                continue;
            }
            if (kind < BuildingVisual.GRAVEL || kind > BuildingVisual.HIGHWAY) continue;
            int t = Math.max(1, (int) Math.round(SURFACE[Math.max(1, Math.min(2, (int) p.width[i]))] * px)), o = (px - t) / 2;
            int c = in.owned[i] ? ROAD[kind] : blend(ROAD[kind], VOID, UNOWNED_DIM);
            if (h) fillRect(img, w, x0, y0 + o, x0 + px, y0 + o + t, c);
            if (v) fillRect(img, w, x0 + o, y0, x0 + o + t, y0 + px, c);
        }
        // The highways: cased, their middles and their arms to the highway beside them, across the tile's edge too.
        int[] cs = crossSection(BuildingVisual.HIGHWAY, px);
        int width = cs[0], cas = cs[1], o = (px - width) / 2;
        for (int pass = cas > 0 ? 0 : 1; pass < 2; pass++) {
            int in0 = pass == 0 ? 0 : cas;
            for (int i = 0; i < TilePainter.PLOTS; i++) {
                if (!isHighway(p, i)) continue;
                int x0 = (i % tile) * px, y0 = (i / tile) * px;
                int c = pass == 0 ? HIGHWAY_CASE : ROAD[BuildingVisual.HIGHWAY];
                if (!in.owned[i]) c = blend(c, VOID, UNOWNED_DIM);
                if (diagonal(in, i)) {
                    diagBand(img, w, i, px, in.marks[i], (width - 2 * in0) / 2.0, c);
                    continue;
                }
                int a = o + in0, z = o + width - in0;
                fillRect(img, w, x0 + a, y0 + a, x0 + z, y0 + z, c);
                if (highwayAt(in, p, i, 0)) fillRect(img, w, x0 + a, y0, x0 + z, y0 + a, c);
                if (highwayAt(in, p, i, 2)) fillRect(img, w, x0 + a, y0 + z, x0 + z, y0 + px, c);
                if (highwayAt(in, p, i, 3)) fillRect(img, w, x0, y0 + a, x0 + a, y0 + z, c);
                if (highwayAt(in, p, i, 1)) fillRect(img, w, x0 + z, y0 + a, x0 + px, y0 + z, c);
            }
        }
        // The railway bridging a highway (0.7.89; spec 2.8): its deck and track across the highway, the way the track runs.
        int[] rs = crossSection(BuildingVisual.PAVED, px);
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.run[i] != TilePainter.RAIL_OVER || p.use[i] == TilePainter.BUILDING) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            boolean ns = railAt(in, p, i, 0) || railAt(in, p, i, 2);
            int deck = Math.min(px, rs[0] + 2), od = (px - deck) / 2, ot = (px - rs[0]) / 2;
            int dc = in.owned[i] ? DECK : blend(DECK, VOID, UNOWNED_DIM), rc = in.owned[i] ? RAIL_LINE : blend(RAIL_LINE, VOID, UNOWNED_DIM);
            if (ns) {
                fillRect(img, w, x0 + od, y0, x0 + od + deck, y0 + px, dc);
                fillRect(img, w, x0 + ot, y0, x0 + ot + rs[0], y0 + px, rc);
            } else {
                fillRect(img, w, x0, y0 + od, x0 + px, y0 + od + deck, dc);
                fillRect(img, w, x0, y0 + ot, x0 + px, y0 + ot + rs[0], rc);
            }
        }
        ramps(in, p, px, img);
        // The highways' dashed centre line, from 6 px a plot.
        if (px < 6) return;
        int dash = (int) Math.round(0.55 * px), mid = px / 2;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isHighway(p, i) || p.run[i] == TilePainter.RAIL_OVER) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            int c = in.owned[i] ? CENTRE_LINE : blend(CENTRE_LINE, VOID, UNOWNED_DIM);
            if (diagonal(in, i)) {
                if (((i % tile) + (i / tile)) % 2 == 0) diagBand(img, w, i, px, in.marks[i], 0.5, c);
                continue;
            }
            boolean ns = highwayAt(in, p, i, 0) || highwayAt(in, p, i, 2);
            if (ns) fillRect(img, w, x0 + mid, y0, x0 + mid + 1, y0 + dash, c);
            else fillRect(img, w, x0, y0 + mid, x0 + dash, y0 + mid + 1, c);
        }
    }

    /** A ramp's slip road's width, as a share of a plot: a gravel street's, 0.3 (ROAD_WIDTH). */
    public static final double RAMP_WIDTH = ROAD_WIDTH[BuildingVisual.GRAVEL];

    /**
     * The ramps (0.7.89; spec 2.7: "ramps where an arterial crosses, every
     * other one, and at an arm's end"): at a highway's plot the runs mark a
     * ramp, where a street passes beneath it, a slip road on each verge plot
     * at its corners - from the street beside that plot to the highway beside
     * it, across the plot's corner nearest the ramp - paved. A verge plot
     * across the tile's edge from its ramp is drawn by the tile it is on.
     */
    private static void ramps(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        double half = Math.max(0.6, RAMP_WIDTH * px / 2);
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.use[i] != TilePainter.EMPTY && p.use[i] != TilePainter.FIELD) continue;
            int vx = i % tile, vy = i / tile;
            for (int oy = -1; oy <= 1; oy += 2) {
                for (int ox = -1; ox <= 1; ox += 2) {
                    int rx = vx + ox, ry = vy + oy;
                    // The ramp's plot: in the tile, or across one edge of it (not a corner).
                    boolean inX = rx >= 0 && rx < tile, inY = ry >= 0 && ry < tile;
                    if (!inX && !inY) continue;
                    byte mark, fx;
                    if (inX && inY) { mark = in.marks[ry * tile + rx]; fx = p.run[ry * tile + rx]; }
                    else {
                        int dir = !inY ? (ry < 0 ? 0 : 2) : (rx < 0 ? 3 : 1), along = dir == 0 || dir == 2 ? rx : ry;
                        mark = in.marksBeyond[dir * tile + along];
                        fx = in.fixedBeyond[dir * tile + along];
                    }
                    if ((mark & CityRuns.M_RAMP) == 0 || (fx != BuildingVisual.HIGHWAY && fx != TilePainter.RAIL_OVER)) continue;
                    // The highway runs along the ramp's row (its plot beside this one, across) or column; the street beneath runs the other way.
                    boolean rowHw = isRunHighway(in, p, vx, ry), colHw = isRunHighway(in, p, rx, vy);
                    if (rowHw == colHw) continue;
                    // The street: beside this plot toward the ramp's column (a highway along a row) or row.
                    boolean street = rowHw ? isStreetAt(in, p, rx, vy) : isStreetAt(in, p, vx, ry);
                    if (!street) continue;
                    // Across the corner nearest the ramp: from the middle of the side the street is on to the middle of the side the highway is on.
                    double ax, ay, bx, by;
                    if (rowHw) { ax = ox < 0 ? 0 : px; ay = px / 2.0; bx = px / 2.0; by = oy < 0 ? 0 : px; }
                    else { ax = px / 2.0; ay = oy < 0 ? 0 : px; bx = ox < 0 ? 0 : px; by = px / 2.0; }
                    int c = in.owned[i] ? ROAD[BuildingVisual.PAVED] : blend(ROAD[BuildingVisual.PAVED], VOID, UNOWNED_DIM);
                    segment(img, w, vx * px, vy * px, px, ax, ay, bx, by, half, c);
                }
            }
        }
    }

    /** Whether plot (x, y) - in the tile, or just across one of its edges - is a highway's of the runs. */
    private static boolean isRunHighway(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE;
        if (x >= 0 && y >= 0 && x < tile && y < tile) return runHighway(p, y * tile + x);
        if ((x < 0 || x >= tile) && (y < 0 || y >= tile)) return false;
        int dir = y < 0 ? 0 : y >= tile ? 2 : x < 0 ? 3 : 1, along = dir == 0 || dir == 2 ? x : y;
        byte f = in.fixedBeyond[dir * tile + along];
        return f == BuildingVisual.HIGHWAY || f == TilePainter.RAIL_OVER;
    }

    /** Whether plot (x, y) - in the tile, or just across one of its edges - is a street's. */
    private static boolean isStreetAt(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE;
        if (x >= 0 && y >= 0 && x < tile && y < tile) return isStreet(p, y * tile + x);
        if ((x < 0 || x >= tile) && (y < 0 || y >= tile)) return false;
        int dir = y < 0 ? 0 : y >= tile ? 2 : x < 0 ? 3 : 1, along = dir == 0 || dir == 2 ? x : y;
        return (in.beyond[dir * tile + along] & TilePainter.S_KIND) != DistrictPlan.NONE;
    }

    /** The pixels of a plot (its corner at x0, y0, px a side) within `half` of the segment from (ax, ay) to (bx, by), in the plot's pixels: colour c. */
    private static void segment(int[] img, int w, int x0, int y0, int px, double ax, double ay, double bx, double by, double half, int c) {
        double vx = bx - ax, vy = by - ay, len2 = vx * vx + vy * vy;
        for (int v = 0; v < px; v++) {
            for (int u = 0; u < px; u++) {
                double qx = u + 0.5 - ax, qy = v + 0.5 - ay;
                double t = len2 > 0 ? Math.max(0, Math.min(1, (qx * vx + qy * vy) / len2)) : 0;
                double ex = qx - t * vx, ey = qy - t * vy;
                if (ex * ex + ey * ey <= half * half) img[(y0 + v) * w + x0 + u] = c;
            }
        }
    }

    /**
     * The track (0.7.72): each plot's middle and its arms to the track beside
     * it - past the tile's edge too, where the line runs on - at a paved
     * road's cross-section, cased in RAIL_LINE and filled RAIL_DASH on every
     * other plot along it (the rest RAIL_LINE), under the streets that cross it.
     */
    private static void track(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        int[] cs = crossSection(BuildingVisual.PAVED, px);
        int width = cs[0], cas = cs[1], o = (px - width) / 2;
        for (int pass = cas > 0 ? 0 : 1; pass < 2; pass++) {
            int in0 = pass == 0 ? 0 : cas;
            for (int i = 0; i < TilePainter.PLOTS; i++) {
                if (p.use[i] != TilePainter.RAIL) continue;
                int x = i % tile, y = i / tile, x0 = x * px, y0 = y * px;
                int c = pass == 0 || ((x + y) & 1) == 1 ? RAIL_LINE : RAIL_DASH;
                if (!in.owned[i]) c = blend(c, VOID, UNOWNED_DIM);
                if (diagonal(in, i)) {
                    diagBand(img, w, i, px, in.marks[i], (width - 2 * in0) / 2.0, c);
                    continue;
                }
                int a = o + in0, z = o + width - in0;
                fillRect(img, w, x0 + a, y0 + a, x0 + z, y0 + z, c);
                if (railAt(in, p, i, 0)) fillRect(img, w, x0 + a, y0, x0 + z, y0 + a, c);
                if (railAt(in, p, i, 2)) fillRect(img, w, x0 + a, y0 + z, x0 + z, y0 + px, c);
                if (railAt(in, p, i, 3)) fillRect(img, w, x0, y0 + a, x0 + a, y0 + z, c);
                if (railAt(in, p, i, 1)) fillRect(img, w, x0 + z, y0 + a, x0 + px, y0 + z, c);
            }
        }
    }

    private static void fillRect(int[] img, int w, int x0, int y0, int x1, int y1, int c) {
        for (int y = y0; y < y1; y++) {
            int o = y * w;
            for (int x = x0; x < x1; x++) img[o + x] = c;
        }
    }

    /** A plot's ground: its terrain - forest under a building cleared (0.7.88; spec 2.6) - its site's tint or grey, dimmed when unowned: what a road is drawn on from LINES_FROM px. */
    static int groundColour(TilePainter.Input in, TilePainter.Painted p, int i) {
        byte ground = in.terrain[i];
        if (ground == World.FOREST && p.use[i] == TilePainter.BUILDING) ground = World.GRASS;
        int c = GROUND[ground];
        int s = p.site[i] - 1;
        boolean wet = ground == World.SALT || ground == World.FRESH;
        if (s >= 0 && (!wet || in.siteKind[s] == Resource.OIL.ordinal())) c = siteTint(in, i, s, c);
        if (!in.owned[i]) c = blend(c, VOID, UNOWNED_DIM);
        return c;
    }

    /** A site's plot over its ground: its resource's colour, speckled, half grey while its holding is worked, grey worked out. */
    static int siteTint(TilePainter.Input in, int i, int s, int c) {
        int state = in.siteState[s];
        int colour = 0xff000000 | Resource.values()[in.siteKind[s]].colour();
        double speckle = World.unit(World.mix((in.tx * TilePainter.TILE + i % TilePainter.TILE) * 0x9E3779B97F4A7C15L
                ^ (in.ty * TilePainter.TILE + i / TilePainter.TILE) * 0xC2B2AE3D27D4EB4FL ^ in.seed));
        int tinted = blend(c, colour, speckle < SPECKLE_SHARE ? SITE_SPECKLE : SITE_TINT);
        return state == TilePainter.WORKED_OUT ? WORKED_GREY
                : state == TilePainter.WORKING ? blend(tinted, WORKED_GREY, 0.5) : tinted;
    }

    /** A road plot's colour below LINES_FROM px a plot, where it fills its plot, by kind: gravel's and the highway's own, a paved road's casing - its own grey is the grass's brightness, and a pixel of it vanishes among the homes. */
    public static final int[] ROAD_SMALL = { 0, 0xffcdb07c, 0xff6a7077, 0xff3b4048 };

    /** A plot's colour before its building: its ground, its site's tint or grey, its road's colour - a track's brown (0.7.88) - or (0.7.72) its track's, where no street crosses it - and dimmed when unowned. */
    static int plotColour(TilePainter.Input in, TilePainter.Painted p, int i) {
        int kind = roadKind(p, i);
        if (kind == 0 && p.use[i] != TilePainter.RAIL) return groundColour(in, p, i);
        int c = kind == 0 ? RAIL_LINE : kind == TilePainter.TRACK ? TRACK_DASH : ROAD_SMALL[kind];
        if (!in.owned[i]) c = blend(c, VOID, UNOWNED_DIM);
        return c;
    }

    /** a toward b by share f, channel by channel, opaque. */
    static int blend(int a, int b, double f) {
        int r = (int) Math.round(((a >> 16) & 255) * (1 - f) + ((b >> 16) & 255) * f);
        int g = (int) Math.round(((a >> 8) & 255) * (1 - f) + ((b >> 8) & 255) * f);
        int bl = (int) Math.round((a & 255) * (1 - f) + (b & 255) * f);
        return 0xff000000 | (r << 16) | (g << 8) | bl;
    }
}
