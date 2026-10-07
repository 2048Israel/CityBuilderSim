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
 * 2 x 2 speck on the grass. From 4 px a road is the mockup's line - gravel
 * 0.3 of its plot, paved 0.52 with a dark casing, a highway 0.86 with its
 * own and a dashed centre line from 6 px - joined to each road beside it at
 * the narrower one's width, with its bridge's deck under it; a one-plot
 * building is drawn at its own land's side (0.7.64: a House 0.91 of its
 * plot; the mockup's 0.6 to 0.92 by a hash before), set against the road it
 * faces, so a street's homes line it; and
 * below 4 px a paved road is drawn in its casing's grey, which a pixel of
 * stands out from the grass where its own does not. BLOCKS (blocks()) are
 * the mockup's middle and far views: each 4 or 8 plots square in its
 * dominant kind's colour at an opacity by how much of it is built, the roads
 * over them - what the whole city looks like at the land office's zoom.
 */
public final class TileRaster {

    private TileRaster() { }

    /** Each ground class's colour, by World's class (GRASS, FOREST, FRESH, SALT, SAND): the mockup's legend. */
    public static final int[] GROUND = { 0xffa8c47e, 0xff527c45, 0xff78b4dc, 0xff2f5d88, 0xffe6daaa };

    /** Each road kind's colour (gravel, paved, highway): the mockup's MAP. */
    public static final int[] ROAD = { 0, 0xffcdb07c, 0xffa4aab0, 0xff3b4048 };

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

    /** A road's width as a share of its plot, by kind (gravel, paved, highway): the mockup's 0.3, 0.52 and 0.86. */
    public static final double[] ROAD_WIDTH = { 0, 0.30, 0.52, 0.86 };

    /** ...and its least, in pixels, at the near view: 1, 2 and 3 (the mockup's 1, 1.5 and 2.5, whole). */
    public static final int[] ROAD_LEAST_PX = { 0, 1, 2, 3 };

    /** A paved road's casing, a pixel either side from 4 px a plot (the mockup's pavedCase)... */
    public static final int PAVED_CASE = 0xff6a7077;

    /** ...a highway's, from 6 px (the mockup's highwayCase)... */
    public static final int HIGHWAY_CASE = 0xff1d2126;

    /** ...and its dashed centre line from 6 px a plot, a pixel wide, on the first 55% of each plot along it (the mockup's centre, its dash 0.7 on and 0.55 off). */
    public static final int CENTRE_LINE = 0xfff0cf5a;

    /** Pixels a plot from which a road is drawn as a line of its own width on its ground (below, the whole plot in its colour): 4, the near view's image. */
    public static final int LINES_FROM = 4;

    /** A one-plot building's side as a share of its plot: its own land's (BuildingVisual.plotSide(), 0.7.64 - a House 0.91 of the plot, a Convenience Store 0.72; the mockup's 0.6 to 0.92 by a hash before), never more than 0.92, so a street's buildings stay apart (the mockup's)... */
    public static final double SMALL_MOST = 0.92;

    /** ...and set this far from the road it faces: 0.07 of a plot, so it hugs its street (the mockup's). */
    public static final double SET_BACK = 0.07;

    /**
     * Fills img with the tile at px pixels a plot: (32 px) squared pixels,
     * row by row. Below LINES_FROM a road plot is its colour; from it, the
     * road is a line of its kind's width on its ground, joined to each road
     * beside it - casings first and fills after, so junctions run clean -
     * with bridge decks under the water's roads and a highway's centre line
     * from 6 px. A one-plot building is drawn the mockup's size, hugging the
     * road it faces; a larger one inset a pixel from 3 px a plot; all
     * edged from 6.
     */
    public static void raster(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        boolean lines = px >= LINES_FROM;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            int c = lines && p.use[i] == TilePainter.ROAD ? groundColour(in, p, i) : plotColour(in, p, i);
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            for (int y = 0; y < px; y++) {
                int o = (y0 + y) * w + x0;
                for (int x = 0; x < px; x++) img[o + x] = c;
            }
        }
        if (lines) roads(in, p, px, img);
        for (int b = 0; b < p.buildings; b++) {
            BuildingVisual.Type t = p.btype[b] < in.types.length ? in.types[p.btype[b]] : null;
            boolean flats = t != null && t.flats();
            int fill = t == null ? BuildingVisual.FILL[BuildingVisual.INDUSTRY] : flats ? BuildingVisual.FLATS_FILL : BuildingVisual.FILL[t.cls()];
            int edge = t == null ? BuildingVisual.EDGE[BuildingVisual.INDUSTRY] : flats ? BuildingVisual.FLATS_EDGE : BuildingVisual.EDGE[t.cls()];
            int x0, y0, x1, y1;
            int site = p.bsite[b];
            if (p.bw[b] == 1 && p.bh[b] == 1 && site < 0) {
                // One plot: its own land's side (BuildingVisual.plotSide(), 0.7.64), hugging the road it faces.
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
                x1 = (p.bx[b] + p.bw[b]) * px - inset;
                y1 = (p.by[b] + p.bh[b]) * px - inset;
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
     * built, then the roads in their colours over them, gravel only when
     * asked. Pure: the same tile gives the same pixels.
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
            BuildingVisual.Type t = p.btype[b] < in.types.length ? in.types[p.btype[b]] : null;
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
            if (p.use[i] != TilePainter.ROAD || (!gravel && p.road[i] == BuildingVisual.GRAVEL)) continue;
            int c = ROAD[p.road[i]];
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
    }

    /** The least a highway is drawn across, in pixels, in the blocks' views: 2 (the mockup's 1.8 at its far view, 2 at its middle). */
    public static final int HIGHWAY_LEAST_PX = 2;

    /** Whether plot i is a highway's. */
    private static boolean isHighway(TilePainter.Painted p, int i) {
        return p.use[i] == TilePainter.ROAD && p.road[i] == BuildingVisual.HIGHWAY;
    }

    /** The side of plot i a road lies on, north, east, south, west, or -1 for none. */
    static int faceOf(TilePainter.Painted p, int i) {
        int tile = TilePainter.TILE, x = i % tile, y = i / tile;
        if (y > 0 && p.use[i - tile] == TilePainter.ROAD) return 0;
        if (x < tile - 1 && p.use[i + 1] == TilePainter.ROAD) return 1;
        if (y < tile - 1 && p.use[i + tile] == TilePainter.ROAD) return 2;
        if (x > 0 && p.use[i - 1] == TilePainter.ROAD) return 3;
        return -1;
    }

    /** A road's line across its plot, by kind at px pixels a plot: {its whole width, its casing either side}. */
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

    /** The roads as lines (from LINES_FROM px a plot): the decks of bridges, then each kind's casing and fill, gravel, paved, highway. */
    private static void roads(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        // Bridge decks: a pixel wider than the road either side, along it.
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.use[i] != TilePainter.ROAD || !p.bridge[i]) continue;
            int[] cs = crossSection(p.road[i], px);
            int width = Math.min(px, cs[0] + 2), o = (px - width) / 2;
            boolean ns = (i >= tile && p.use[i - tile] == TilePainter.ROAD) || (i + tile < TilePainter.PLOTS && p.use[i + tile] == TilePainter.ROAD);
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            int deck = in.owned[i] ? DECK : blend(DECK, VOID, UNOWNED_DIM);
            if (ns) fillRect(img, w, x0 + o, y0, x0 + o + width, y0 + px, deck);
            else fillRect(img, w, x0, y0 + o, x0 + px, y0 + o + width, deck);
        }
        for (int kind = BuildingVisual.GRAVEL; kind <= BuildingVisual.HIGHWAY; kind++) {
            int[] cs = crossSection(kind, px);
            int width = cs[0], cas = cs[1], o = (px - width) / 2;
            int colour = ROAD[kind], casing = kind == BuildingVisual.HIGHWAY ? HIGHWAY_CASE : PAVED_CASE;
            for (int pass = cas > 0 ? 0 : 1; pass < 2; pass++) {
                int in0 = pass == 0 ? 0 : cas;
                for (int i = 0; i < TilePainter.PLOTS; i++) {
                    if (p.use[i] != TilePainter.ROAD || p.road[i] < kind) continue;
                    int x = i % tile, y = i / tile, x0 = x * px, y0 = y * px, own = p.road[i];
                    int c = pass == 0 ? casing : colour;
                    if (!in.owned[i]) c = blend(c, VOID, UNOWNED_DIM);
                    int a = o + in0, z = o + width - in0;
                    // Its own kind: its middle and its arms to every road at least as wide; a wider road's plot: only
                    // the arm of this narrower road that meets it, which the wider road's middle then covers.
                    if (own == kind) fillRect(img, w, x0 + a, y0 + a, x0 + z, y0 + z, c);
                    if (arm(p, i, y == 0, -tile, kind, own)) fillRect(img, w, x0 + a, y0, x0 + z, y0 + a, c);
                    if (arm(p, i, y == tile - 1, tile, kind, own)) fillRect(img, w, x0 + a, y0 + z, x0 + z, y0 + px, c);
                    if (arm(p, i, x == 0, -1, kind, own)) fillRect(img, w, x0, y0 + a, x0 + a, y0 + z, c);
                    if (arm(p, i, x == tile - 1, 1, kind, own)) fillRect(img, w, x0 + z, y0 + a, x0 + px, y0 + z, c);
                }
            }
        }
        // The highways' dashed centre line, from 6 px a plot.
        if (px < 6) return;
        int dash = (int) Math.round(0.55 * px), mid = px / 2;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.use[i] != TilePainter.ROAD || p.road[i] != BuildingVisual.HIGHWAY) continue;
            int x = i % tile, y = i / tile, x0 = x * px, y0 = y * px;
            boolean ns = (y > 0 && p.use[i - tile] == TilePainter.ROAD && p.road[i - tile] == BuildingVisual.HIGHWAY)
                    || (y < tile - 1 && p.use[i + tile] == TilePainter.ROAD && p.road[i + tile] == BuildingVisual.HIGHWAY);
            int c = in.owned[i] ? CENTRE_LINE : blend(CENTRE_LINE, VOID, UNOWNED_DIM);
            if (ns) fillRect(img, w, x0 + mid, y0, x0 + mid + 1, y0 + dash, c);
            else fillRect(img, w, x0, y0 + mid, x0 + dash, y0 + mid + 1, c);
        }
    }

    /** Whether road plot i draws an arm of kind `kind` toward its neighbour at i + step: the neighbour a road (or past the tile's edge, which a road crosses only at a port or as a highway, taken as its own kind), and the narrower of the two of this kind. */
    private static boolean arm(TilePainter.Painted p, int i, boolean edge, int step, int kind, int own) {
        int other;
        if (edge) other = own;
        else if (p.use[i + step] == TilePainter.ROAD) other = p.road[i + step];
        else return false;
        return Math.min(own, other) == kind;
    }

    private static void fillRect(int[] img, int w, int x0, int y0, int x1, int y1, int c) {
        for (int y = y0; y < y1; y++) {
            int o = y * w;
            for (int x = x0; x < x1; x++) img[o + x] = c;
        }
    }

    /** A plot's ground: its terrain, its site's tint or grey, dimmed when unowned - what a road is drawn on from LINES_FROM px. */
    static int groundColour(TilePainter.Input in, TilePainter.Painted p, int i) {
        int c = GROUND[in.terrain[i]];
        int s = p.site[i] - 1;
        byte ground = in.terrain[i];
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

    /** A plot's colour before its building: its ground, its site's tint or grey, its road's colour, and dimmed when unowned. */
    static int plotColour(TilePainter.Input in, TilePainter.Painted p, int i) {
        if (p.use[i] != TilePainter.ROAD) return groundColour(in, p, i);
        int c = ROAD_SMALL[p.road[i]];
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
