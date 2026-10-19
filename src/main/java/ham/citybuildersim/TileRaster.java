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
 * TRACK (0.7.72, batch N3; redrawn as rail, 0.8.1, batch FR0). The railway's
 * lines are drawn plot by plot as track (TilePainter.RAIL): from 4 px a plot
 * a ballast bed with one steel centre line and a sleeper tick a plot, and
 * from 6 px two steel rails on sleepers (double track where the main line is
 * marked), joined to the track beside it, on a deck over fresh water; a road
 * crossing it drawn over the bed with the rails showing through and level
 * crossing marks, or bridging it where an arterial or boulevard crosses the
 * main line; below 4 px and in the blocks, its plots in RAIL_DARK with
 * cross-ticks every block from 2 px. The mockup drew no railway, so the
 * colours are a map's convention (star N3-9, spec-freight 4.3).
 *
 * YARDS AND JUNCTIONS (0.8.1, batch FR0; spec-freight 4.2, 4.3 and 4.6,
 * D15 and D19). From 4 px a plot a Rail Terminal is drawn as a yard
 * (drawYard()): a fan of sidings closing to the line at each end, a freight
 * shed, the freight it handles - ore stockpiles, or at the freight station
 * of a railway carrying goods rows of boxes and two gantries - and wagons
 * standing on as many sidings as the fleet's share of its wagon sets gives.
 * From JUNCTIONS_FROM the junctions are drawn by rule, none of them a
 * building: signals where arterials cross, roundabouts where boulevards
 * cross, diamonds where a street passes beneath a ramp, stacks where
 * highways cross; and from 4 px a highway's open end comes down to the
 * ground. (Batch FR7, slice 4) Where a crossing has kept its ground
 * (InterchangeGround: a 19-plot reserve for a cloverleaf, an 11-plot one for a
 * trumpet) the stack gives way to the loops drawn on it (loops()); and a +
 * of streets, a street's T on an arterial, are drawn with kerb corners, wide
 * where an estate's trucks turn (kerbs()).
 *
 * STREETS AS VERGE AND SURFACE (0.7.88, batch RD2; the project's
 * spec-roads-and-ports.md 2.1 and 5, its prototype's render()). A street's
 * plot is its right of way: drawn as VERGE, with its surface down its middle
 * - gravel a speckled light grey (tan until FR7), paved a smooth grey, a band half the plot wide for a street of half
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

    /**
     * Each ground class's colour, by World's class (GRASS, FOREST, FRESH,
     * SALT, SAND): the mockup's legend; and since 0.7.127 (batch E7;
     * spec-globe 3.9) ICE, the founding globe's ice, 0xdfe7ee, and DESERT,
     * CSS's tan (#d2b48c), warmer and darker than the beach's sand.
     */
    public static final int[] GROUND = { 0xffa8c47e, 0xff527c45, 0xff78b4dc, 0xff2f5d88, 0xffe6daaa, 0xffdfe7ee, 0xffd2b48c };

    /**
     * Each road kind's colour (gravel, paved, highway): the mockup's MAP, but
     * gravel's. GRAVEL IS GREY (FR7 2.3, 2026-10-18; Jerus: "i think gravel isnt
     * brown, it should be grey"): the mockup's tan #cdb07c read as earth, and
     * 70% of US crushed stone is limestone or dolomite and 14% granite (USGS
     * 2025), so a gravel road is a light warm grey of loose stone, #bdb8ae, told
     * from paved's smooth #a4aab0 by its lightness and its speckle (GRAVEL_GRAIN).
     * The browns stay for what is earth: the track's dashes and its ballast.
     */
    public static final int[] ROAD = { 0, 0xffbdb8ae, 0xffa4aab0, 0xff3b4048 };

    /** The loose stone's speckle on a gravel street from 4 px a plot (the near view, L0): a shade darker than its grey. */
    public static final int GRAVEL_GRAIN = 0xff9a958b;

    /** The share of a gravel street's pixels speckled: 18%. */
    public static final double GRAVEL_GRAIN_SHARE = .18;

    /** The railway's line, and its casing from 4 px a plot (0.7.72) until 0.8.1 drew the track as rail; since, a track plot's colour in raster() below LINES_FROM px a plot alone (plotColour()): openstreetmap-carto's rail grey, #707070 (the mockup drew no railway; star N3-9)... */
    public static final int RAIL_LINE = 0xff707070;

    /** ...and its dashes, white on every other plot along it until 0.8.1: carto's rail dash. Nothing draws it since. */
    public static final int RAIL_DASH = 0xffffffff;

    /* ===== RAIL DRAWN AS RAIL (0.8.1, batch FR0; spec-freight 4.3, D15, D18) ===== */

    /** The track's ballast bed (spec-freight 4.3's grey-brown #6f675c). */
    public static final int BALLAST = 0xff6f675c;
    /** A sleeper across the bed (spec-freight 4.3's #4a3f35). */
    public static final int SLEEPER = 0xff4a3f35;
    /** The rails' steel (spec-freight 4.3's #c9ced3). */
    public static final int STEEL = 0xffc9ced3;
    /** The line the track is at the middle and far views (spec-freight 4.3's dark #3a3632). */
    public static final int RAIL_DARK = 0xff3a3632;

    /** Pixels a plot from which the track is two rails on sleepers (the 8-px raster). */
    public static final int RAILS_FROM = 6;
    /** A single track's bed, a share of the plot: the paved road's width, as the track has today. */
    public static final double BED = 0.52;
    /** Double track's bed, a share of the plot: the highway's. */
    public static final double BED_DOUBLE = 0.86;
    /** Between a track's two rails, a share of the plot (at 8 px: the rails a pixel either side of the track's middle). */
    public static final double GAUGE = 0.24;
    /** Between double track's two tracks, middle to middle. */
    public static final double TRACKS_APART = 0.44;
    /** A sleeper every quarter plot (spec-freight 4.3: "sleepers across it every 0.25 plot"). */
    public static final double SLEEPERS_EVERY = 0.25;
    /** A level crossing's white crossbuck (spec-freight 4.3 "Crossings"). */
    public static final int CROSSBUCK = 0xffffffff;
    /** ...and its barrier's red (spec-freight 4.3 "Crossings"). */
    public static final int BARRIER = 0xffd04a3c;

    /* ===== JUNCTIONS DRAWN BY RULE (0.8.1, batch FR0; spec-freight 4.6, D19) ===== */

    /** Pixels a plot from which junctions are drawn (the 8-px raster): signals, roundabouts, diamonds and stacks; a highway's end comes down to the ground wherever the ways are drawn, from LINES_FROM. */
    public static final int JUNCTIONS_FROM = 6;
    /** A signalled junction's box, a shade darker than paved. */
    public static final int JUNCTION_BOX = 0xff8e949a;
    /** A crossing's stripes and stop lines. */
    public static final int ZEBRA = 0xfff2f2f2;
    /** Plots: a zebra crossing's depth along its arm. */
    public static final double ZEBRA_DEPTH = 0.3;
    /** Plots: a roundabout's outer radius, spec: "a circle of 1.5 plots". */
    public static final double ROUNDABOUT_RADIUS = 0.75;
    /** Plots: its planted middle's radius. */
    public static final double ROUNDABOUT_ISLAND = 0.35;
    /** The planted middle. */
    public static final int ISLAND = 0xff6f9a4f;
    /** Plots: a diamond's slip road along the verge each side of its street. */
    public static final double SLIP_LENGTH = 2;
    /** Plots: the least run of junction streets through a plot along an axis for a road to cross there (FX3): a road up to three plots wide - a cell's boulevard, two deep, beside the next cell's road, as a tile sees it one plot past its edge - is one road, not a junction on every plot. */
    static final int JUNCTION_RUN = 4;

    /** Pixels a plot from which the blocks' view draws the track's cross-ticks (the L1 raster). */
    public static final int TICKS_FROM = 2;

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

    /* ===== YARDS DRAWN AS YARDS (0.8.1, batch FR0; spec-freight 4.3, D15) ===== */

    /** A yard's ground, light ballast (spec-freight 4.3's yard). */
    public static final int YARD_GROUND = 0xff8c8478;
    /** The fan's sidings (spec-freight 4.3: 6 to 10). */
    public static final int YARD_TRACKS = 8;
    /** Plots between the fan's tracks, middle to middle. */
    public static final double YARD_TRACK_APART = 1.5;
    /** Plots at each end over which the fan closes to the line. */
    public static final int YARD_LADDER = 3;
    /** The freight shed: plots deep, and its share of the fan's length. */
    public static final double SHED_DEPTH = 2.5, SHED_LENGTH = 0.6;
    /** A bulk yard's stockpiles, the spec's sand colour. */
    public static final int ORE_HEAP = 0xffc9b68f;
    /** A container yard's boxes, the boats' white. */
    public static final int CONTAINER = 0xffd6dde4;
    /** Its two cranes. */
    public static final int GANTRY = 0xff2b2f36;
    /** A wagon standing. */
    public static final int WAGON = 0xff7b4b3a;
    /** Plots: a wagon about 15 m. */
    public static final double WAGON_LENGTH = 0.5;

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

    /* =====================================================================
       THE NEAR VIEW (J3: a tile at 4 or 8 px a plot, MapFrame.L0)
       ===================================================================== */

    /**
     * Fills img with the tile at px pixels a plot: (32 px) squared pixels,
     * row by row. Below LINES_FROM a road plot is its colour; from it, the
     * streets are drawn as verge and surface, the track and the highways as
     * lines, joined across the tile's edge where the plans have them go on
     * (ways()). A one-plot building is drawn the mockup's size, hugging the
     * street it faces; a larger one inset a pixel from 3 px a plot; all
     * edged from 6; one packed without a street a shade darker; and from
     * LINES_FROM a Rail Terminal as its yard (drawYard(), 0.8.1).
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
                // ...a site's works inset only where its site ends (0.8.0): a site across tiles, a coal mine's 33 plots, one box.
                int il = site >= 0 && p.bx[b] > in.siteFX0[site] ? 0 : inset, it = site >= 0 && p.by[b] > in.siteFY0[site] ? 0 : inset;
                int ir = site >= 0 && p.bx[b] + p.bw[b] - 1 < in.siteFX1[site] ? 0 : inset, ib = site >= 0 && p.by[b] + p.bh[b] - 1 < in.siteFY1[site] ? 0 : inset;
                x0 = p.bx[b] * px + il;
                y0 = p.by[b] * px + it;
                x1 = Math.min(tile, p.bx[b] + p.bw[b]) * px - ir;
                y1 = Math.min(tile, p.by[b] + p.bh[b]) * px - ib;
            }
            if (px >= LINES_FROM && t != null && t.terminal() && !p.bpacked[b]) {
                drawYard(in, p, b, t, px, img, w, x0, y0, x1, y1);
                continue;
            }
            boolean edged = px >= EDGE_FROM;
            int look = t == null ? BuildingVisual.PLAIN : t.look();
            if (look != BuildingVisual.PLAIN && px >= LOOK_FROM && !p.bpacked[b] && (p.bw[b] > 1 || p.bh[b] > 1 || site >= 0)) {
                // A materials building as itself (0.8.0, batch I14): its pit, quarry, heaps, works, camp or forest.
                drawLook(in, t, site, px, edged, img, w, x0, y0, x1, y1);
            } else {
                for (int y = y0; y < y1; y++) {
                    for (int x = x0; x < x1; x++) {
                        boolean onEdge = edged && (y == y0 || y == y1 - 1 || x == x0 || x == x1 - 1);
                        img[y * w + x] = onEdge ? edge : fill;
                    }
                }
            }
            if (site >= 0 && px >= 4) {
                // A mine's mark: its resource's colour at its centre, ringed - since 0.8.0 the whole site's centre, so a site
                // across tiles is marked once.
                int cx = (in.siteFX0[site] + in.siteFX1[site] + 1) * px / 2, cy = (in.siteFY0[site] + in.siteFY1[site] + 1) * px / 2;
                int r = Math.max(1, px / 2);
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

    /**
     * Where a yard's fan lies in its box, from the box's track (FX3, 0.8.4):
     * rail[y * w + x] is whether the box's plot (x, y) holds track, the box w
     * plots across and h down. Returns {ew, line, a, b}: ew is 1 for an
     * east-west fan and 0 for a north-south one, line the plot row (east-west)
     * or column (north-south) it closes to, in plots from the box's north-west
     * corner, and [a, b) its span along the line, in plots from the box's
     * west edge (east-west) or north edge. No track in the box: {1, -1, 0, w},
     * east-west on the box's middle. WHY: drawYard() took the line as the
     * middle of all the box's track, which put the fan off its line when
     * another line crossed the box (Jerus's second yard, stacked on a
     * corridor with the spine across its head: the fan closed 4.5 plots east
     * of the corridor). Now each column holding track is a north-south
     * candidate and each row an east-west one; a row (for a column) is blocked
     * when it holds track in any other column, and the candidate's runs are
     * the stretches of rows left clear, each scoring the rows of it where the
     * candidate is track. The best run wins: the most track, then the longest,
     * then north-south, then the lowest line, then the earliest. A run shorter
     * than 2 * YARD_LADDER + 2 plots has no room for a fan between crossings:
     * its line and direction stand and the span is the whole box. One straight
     * line through the box wins with the whole box, as it always was. Both
     * drawYard() and CityMap.trainRoutesInto() lay the fan from this, so a
     * route's ladder starts where the drawing's does.
     */
    static int[] yardLayout(boolean[] rail, int w, int h) {
        int[] colN = new int[w], rowN = new int[h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (!rail[y * w + x]) continue;
                colN[x]++;
                rowN[y]++;
            }
        }
        int bestScore = -1, bestLen = 0, bestEw = 1, bestLine = -1, bestA = 0, bestB = 0;
        // North-south first, then east-west; each by index and run in order, so a tie keeps the earlier.
        for (int pass = 0; pass < 2; pass++) {
            boolean ns = pass == 0;
            int lines = ns ? w : h, along = ns ? h : w;
            for (int c = 0; c < lines; c++) {
                if ((ns ? colN[c] : rowN[c]) == 0) continue;
                int start = 0;
                while (start <= along) {
                    int score = 0;
                    int pos = start;
                    while (pos < along) {
                        boolean on = ns ? rail[pos * w + c] : rail[c * w + pos];
                        int others = ns ? rowN[pos] : colN[pos];
                        if (others - (on ? 1 : 0) > 0) break;
                        if (on) score++;
                        pos++;
                    }
                    int len = pos - start;
                    if (len > 0 && (score > bestScore || score == bestScore && len > bestLen)) {
                        bestScore = score;
                        bestLen = len;
                        bestEw = ns ? 0 : 1;
                        bestLine = c;
                        bestA = start;
                        bestB = pos;
                    }
                    start = pos + 1;
                }
            }
        }
        if (bestLine < 0) return new int[] { 1, -1, 0, w };
        if (bestLen < 2 * YARD_LADDER + 2) {
            bestA = 0;
            bestB = bestEw == 1 ? w : h;
        }
        return new int[] { bestEw, bestLine, bestA, bestB };
    }

    /**
     * A Rail Terminal's yard as a yard, not its class's plain box (0.8.1,
     * batch FR0; spec-freight 4.2 and 4.3, D15): ballast ground, a fan of
     * YARD_TRACKS sidings closing to the line at each end over YARD_LADDER
     * plots, a freight shed on one side of the fan, and on the other the
     * freight it handles - a container yard's boxes and gantry cranes where
     * the freight station carries goods, every other yard's ore stockpiles -
     * with wagons standing on the first wagonTracks tracks from the shed.
     * Since FX3 (0.8.4) the fan lies where yardLayout() puts it: on the line
     * with the longest stretch clear of crossing track, the shed, the heaps
     * and the boxes kept to that stretch, and the track that crosses the
     * line drawn over the yard's ground through the head the stretch leaves,
     * so a line crossing the yard meets the fan at its throat.
     */
    private static void drawYard(TilePainter.Input in, TilePainter.Painted p, int b, BuildingVisual.Type t, int px,
                                 int[] img, int w, int x0, int y0, int x1, int y1) {
        int tile = TilePainter.TILE;
        int bx = p.bx[b], by = p.by[b], bw = p.bw[b], bh = p.bh[b];
        fillRect(img, w, x0, y0, x1, y1, YARD_GROUND);
        int boxW = Math.min(tile, bx + bw) - bx, boxH = Math.min(tile, by + bh) - by;
        boolean[] rail = new boolean[boxW * boxH];
        for (int y = 0; y < boxH; y++) {
            for (int x = 0; x < boxW; x++) rail[y * boxW + x] = p.run[(by + y) * tile + bx + x] == TilePainter.RAIL;
        }
        int[] layout = yardLayout(rail, boxW, boxH);
        boolean ew = layout[0] == 1;
        int spanA = layout[2], spanB = layout[3], boxLong = ew ? boxW : boxH;
        int bed = bedWidth(px, false);
        int off = px >= RAILS_FROM ? Math.max(1, (int) Math.round(GAUGE / 2.0 * px)) : 0;
        // The line the fan closes to: the yard's own line, else the box's middle.
        double line = layout[1] < 0
                ? (ew ? (by + bh / 2.0) * px : (bx + bw / 2.0) * px)
                : ((ew ? by : bx) + layout[1] + 0.5) * px;
        // The fan's span along, in pixels: the stretch of the line clear of crossing track, else the box's whole length.
        int start = spanA == 0 ? (ew ? x0 : y0) : ((ew ? bx : by) + spanA) * px;
        int end = spanB >= boxLong ? (ew ? x1 : y1) : ((ew ? bx : by) + spanB) * px;
        // The box as the shed, the heaps and the boxes see it: the span along, the box across.
        int sx0 = ew ? start : x0, sx1 = ew ? end : x1, sy0 = ew ? y0 : start, sy1 = ew ? y1 : end;
        int n = YARD_TRACKS;
        double[] at = new double[n];
        double apart = YARD_TRACK_APART * px;
        for (int i = 0; i < n; i++) at[i] = line + (i - (n - 1) / 2.0) * apart;
        double min = at[0], max = at[n - 1];
        // Keep the fan's whole bed inside the box.
        if (ew) {
            double lo = min - bed / 2.0, hi = max + bed / 2.0;
            if (lo < y0) { double shift = y0 - lo; for (int i = 0; i < n; i++) at[i] += shift; }
            else if (hi > y1) { double shift = y1 - hi; for (int i = 0; i < n; i++) at[i] += shift; }
        } else {
            double lo = min - bed / 2.0, hi = max + bed / 2.0;
            if (lo < x0) { double shift = x0 - lo; for (int i = 0; i < n; i++) at[i] += shift; }
            else if (hi > x1) { double shift = x1 - hi; for (int i = 0; i < n; i++) at[i] += shift; }
        }
        min = at[0]; max = at[n - 1];
        int fanA = start + (int) Math.round(YARD_LADDER * px);
        int fanB = end - (int) Math.round(YARD_LADDER * px);
        int linePx = (int) Math.round(line);
        int steel = STEEL;
        for (int i = 0; i < n; i++) {
            int mid = (int) Math.round(at[i]);
            int a = mid - bed / 2, z = a + bed;
            if (ew) {
                fillRect(img, w, fanA, a, fanB, z, BALLAST);
                if (off == 0) fillRect(img, w, fanA, mid, fanB, mid + 1, steel);
                else {
                    fillRect(img, w, fanA, mid - off, fanB, mid - off + 1, steel);
                    fillRect(img, w, fanA, mid + off, fanB, mid + off + 1, steel);
                }
                line(img, w, fanA, mid, start, linePx, steel);
                line(img, w, fanB - 1, mid, end - 1, linePx, steel);
            } else {
                fillRect(img, w, a, fanA, z, fanB, BALLAST);
                if (off == 0) fillRect(img, w, mid, fanA, mid + 1, fanB, steel);
                else {
                    fillRect(img, w, mid - off, fanA, mid - off + 1, fanB, steel);
                    fillRect(img, w, mid + off, fanA, mid + off + 1, fanB, steel);
                }
                line(img, w, mid, fanA, linePx, start, steel);
                line(img, w, mid, fanB - 1, linePx, end - 1, steel);
            }
        }
        // The freight shed, between the fan and the box's edge.
        int fanNear = (int) Math.round((ew ? min : min) - bed / 2.0);
        int fanFar = (int) Math.round((ew ? max : max) + bed / 2.0);
        int depth = ew
                ? Math.min((int) Math.round(SHED_DEPTH * px), Math.max(0, fanNear - y0))
                : Math.min((int) Math.round(SHED_DEPTH * px), Math.max(0, fanNear - x0));
        int len = Math.max(1, (int) Math.round(SHED_LENGTH * (fanB - fanA)));
        if (depth > 0) {
            int along = (fanA + fanB) / 2;
            int s0 = along - len / 2, s1 = s0 + len;
            if (ew) {
                s0 = Math.max(sx0, s0); s1 = Math.min(sx1, s1);
                int shedTop = fanNear - depth;
                fillRect(img, w, s0, shedTop, s1, fanNear, t.fill());
                if (px >= EDGE_FROM) {
                    for (int x = s0; x < s1; x++) { img[shedTop * w + x] = t.edge(); img[(fanNear - 1) * w + x] = t.edge(); }
                    for (int y = shedTop; y < fanNear; y++) { img[y * w + s0] = t.edge(); img[y * w + s1 - 1] = t.edge(); }
                }
            } else {
                s0 = Math.max(sy0, s0); s1 = Math.min(sy1, s1);
                int shedLeft = fanNear - depth;
                fillRect(img, w, shedLeft, s0, fanNear, s1, t.fill());
                if (px >= EDGE_FROM) {
                    for (int y = s0; y < s1; y++) { img[y * w + shedLeft] = t.edge(); img[y * w + fanNear - 1] = t.edge(); }
                    for (int x = shedLeft; x < fanNear; x++) { img[s0 * w + x] = t.edge(); img[(s1 - 1) * w + x] = t.edge(); }
                }
            }
        }
        boolean station = in.tx * tile + bx == in.stationX && in.ty * tile + by == in.stationY;
        boolean container = station && in.goodsByRail;
        // What it handles, on the other side of the fan from the shed.
        if (container) containers(in, p, px, img, w, sx0, sy0, sx1, sy1, ew, fanA, fanB, fanNear, fanFar);
        else stockpiles(px, img, w, sx0, sy0, sx1, sy1, ew, fanA, fanB, fanFar);
        // Wagons standing on the first wagonTracks tracks from the shed's side.
        for (int k = 0; k < in.wagonTracks && k < n; k++) {
            int mid = (int) Math.round(at[k]);
            int wide = px >= RAILS_FROM ? bed : 1;
            int wagonLen = Math.max(1, (int) Math.round(WAGON_LENGTH * px));
            int top = mid - wide / 2;
            for (int s = fanA; s + wagonLen <= fanB; s += wagonLen + 1) {
                if (ew) {
                    fillRect(img, w, s, top, s + wagonLen, top + wide, WAGON);
                    if (container) {
                        int cw = Math.max(1, wagonLen * 2 / 3), ch = Math.max(1, wide - 2);
                        fillRect(img, w, s + (wagonLen - cw) / 2, mid - ch / 2, s + (wagonLen - cw) / 2 + cw, mid - ch / 2 + ch, CONTAINER);
                    }
                } else {
                    fillRect(img, w, top, s, top + wide, s + wagonLen, WAGON);
                    if (container) {
                        int cw = Math.max(1, wide - 2), ch = Math.max(1, wagonLen * 2 / 3);
                        fillRect(img, w, mid - cw / 2, s + (wagonLen - ch) / 2, mid - cw / 2 + cw, s + (wagonLen - ch) / 2 + ch, CONTAINER);
                    }
                }
            }
        }
        // The track the span leaves out, over the yard's ground: a line crossing the box runs through the head and meets the fan at its throat (FX3).
        if (spanA > 0 || spanB < boxLong) {
            for (int y = 0; y < boxH; y++) {
                for (int x = 0; x < boxW; x++) {
                    if (!rail[y * boxW + x]) continue;
                    int along = ew ? x : y;
                    if (along >= spanA && along < spanB) continue;
                    yardTrack(in, p, (by + y) * tile + bx + x, px, img);
                }
            }
        }
    }

    /**
     * One plot of the track that crosses a yard's head, drawn over the yard's
     * ground (FX3, 0.8.4): drawTrackLook()'s look, with the track beside it
     * read from the runs (p.run), because a building stands on every plot of
     * the yard's box and p.use does not say RAIL there.
     */
    private static void yardTrack(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img) {
        int tile = TilePainter.TILE, arms = 0;
        for (int d = 0; d < 4; d++) {
            int x = i % tile + TilePainter.DX[d], y = i / tile + TilePainter.DY[d];
            byte f = x >= 0 && y >= 0 && x < tile && y < tile ? p.run[y * tile + x]
                    : in.fixedBeyond[d * tile + (d == 0 || d == 2 ? i % tile : i / tile)];
            if (f == TilePainter.RAIL || f == TilePainter.RAIL_OVER) arms |= 1 << d;
        }
        drawTrackLook(in, p, i, px, img, arms);
    }

    /** A container yard's boxes, in rows, and its two gantry cranes across the fan. */
    private static void containers(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int w,
                                   int x0, int y0, int x1, int y1, boolean ew, int fanA, int fanB, int fanNear, int fanFar) {
        int boxLong = px, boxDeep = Math.max(1, (int) Math.round(0.4 * px));
        int left = ew ? x0 : fanFar, right = ew ? x1 : x1;
        int top = ew ? fanFar : y0, bottom = ew ? y1 : y1;
        int alongStep = ew ? boxLong + 1 : boxDeep + 1;
        int acrossStep = ew ? boxDeep + 1 : boxLong + 1;
        for (int a = top; a + (ew ? boxDeep : boxLong) <= bottom; a += acrossStep) {
            for (int s = left; s + (ew ? boxLong : boxDeep) <= right; s += alongStep) {
                if (ew) fillRect(img, w, s, a, s + boxLong, a + boxDeep, CONTAINER);
                else fillRect(img, w, s, a, s + boxDeep, a + boxLong, CONTAINER);
            }
        }
        // The cranes, across the whole fan at a third and two thirds of its length.
        int one = fanA + (fanB - fanA) / 3, two = fanA + 2 * (fanB - fanA) / 3;
        for (int g : new int[] { one, two }) {
            if (ew) {
                fillRect(img, w, g, fanNear, g + 1, fanFar, GANTRY);
                fillRect(img, w, g - 1, fanNear, g + 2, fanNear + 1, GANTRY);
                fillRect(img, w, g - 1, fanFar - 1, g + 2, fanFar, GANTRY);
            } else {
                fillRect(img, w, fanNear, g, fanFar, g + 1, GANTRY);
                fillRect(img, w, fanNear, g - 1, fanNear + 1, g + 2, GANTRY);
                fillRect(img, w, fanFar - 1, g - 1, fanFar, g + 2, GANTRY);
            }
        }
    }

    /** A bulk yard's three stockpiles, side by side along the fan. */
    private static void stockpiles(int px, int[] img, int w, int x0, int y0, int x1, int y1,
                                   boolean ew, int fanA, int fanB, int fanFar) {
        int edge = blend(ORE_HEAP, 0xff000000, 0.25);
        for (int k = 0; k < 3; k++) {
            int cx, cy, rx, ry;
            if (ew) {
                int a0 = x0 + k * (x1 - x0) / 3, a1 = x0 + (k + 1) * (x1 - x0) / 3;
                int b0 = fanFar, b1 = y1;
                cx = (a0 + a1) / 2; cy = (b0 + b1) / 2;
                rx = Math.max(1, (a1 - a0) / 2 - 1); ry = Math.max(1, (b1 - b0) / 2 - 1);
            } else {
                int a0 = fanFar, a1 = x1;
                int b0 = y0 + k * (y1 - y0) / 3, b1 = y0 + (k + 1) * (y1 - y0) / 3;
                cx = (a0 + a1) / 2; cy = (b0 + b1) / 2;
                rx = Math.max(1, (a1 - a0) / 2 - 1); ry = Math.max(1, (b1 - b0) / 2 - 1);
            }
            fillEllipse(img, w, cx, cy, rx, ry, ORE_HEAP, edge);
        }
    }

    /** A filled ellipse, its edge a pixel darker. */
    private static void fillEllipse(int[] img, int w, int cx, int cy, int rx, int ry, int fill, int edge) {
        for (int y = cy - ry; y <= cy + ry; y++) {
            for (int x = cx - rx; x <= cx + rx; x++) {
                double dx = (x - cx) / (double) rx, dy = (y - cy) / (double) ry;
                if (dx * dx + dy * dy > 1) continue;
                boolean rim = outside(x - 1, y, cx, cy, rx, ry) || outside(x + 1, y, cx, cy, rx, ry)
                        || outside(x, y - 1, cx, cy, rx, ry) || outside(x, y + 1, cx, cy, rx, ry);
                img[y * w + x] = rim ? edge : fill;
            }
        }
    }

    /** Whether (x, y) lies outside the ellipse centred at (cx, cy). */
    private static boolean outside(int x, int y, int cx, int cy, int rx, int ry) {
        double dx = (x - cx) / (double) rx, dy = (y - cy) / (double) ry;
        return dx * dx + dy * dy > 1;
    }

    /** A one-pixel line from (ax, ay) to (bx, by), Bresenham. */
    private static void line(int[] img, int w, int ax, int ay, int bx, int by, int c) {
        int dx = Math.abs(bx - ax), sx = ax < bx ? 1 : -1;
        int dy = -Math.abs(by - ay), sy = ay < by ? 1 : -1;
        int err = dx + dy;
        while (true) {
            if (ax >= 0 && ay >= 0 && ax < w && ay * w + ax < img.length) img[ay * w + ax] = c;
            if (ax == bx && ay == by) break;
            int e2 = 2 * err;
            if (e2 >= dy) { err += dy; ax += sx; }
            if (e2 <= dx) { err += dx; ay += sy; }
        }
    }

    /* =====================================================================
       THE MATERIALS AS THEMSELVES (0.8.0, batch I14; spec-materials 5's I14
       row). Each look of BuildingVisual's drawn in its box from LOOK_FROM px
       a plot: an open pit's benches stepping down to its floor and a
       quarry's straight ones, measured from the whole site's square so a
       site across tiles is one pit (TilePainter.Input.siteFX0); a shaft's
       yard and headframe; a leach plant's heaps in rows and its pond; the
       wash plant's conveyor; a camp's log stacks; planted forest's trees in
       rows; a works' roof ridges in its industry's colours. Below LOOK_FROM
       a box of the look's fill, as every building is.
       ===================================================================== */

    /** Pixels a plot from which a materials building is drawn as its look rather than a box of its colour: 3 (TileRaster's INSET_FROM; the L0 image is 4). */
    public static final int LOOK_FROM = 3;

    /** The benches a pit or a quarry steps down by from its rim to its middle: 6. */
    public static final int BENCHES = 6;

    /** A leach plant's pond: #4f8fbf. */
    static final int POND = 0xff4f8fbf;

    /** A shaft's headframe: #2b2622, and the winding house in it #8a7f72. */
    static final int HEADFRAME = 0xff2b2622, WINDING = 0xff8a7f72;

    /** The wash plant's conveyor: #9a9aa2. */
    static final int CONVEYOR = 0xff9a9aa2;

    /** A camp's log stacks: #5a3e22. */
    static final int LOGS = 0xff5a3e22;

    /** Planted forest's crowns, in rows over its darker floor: #6a9a55. */
    static final int CROWN = 0xff6a9a55;

    /** How much darker a works' roof ridges are than its fill: 18%. */
    static final double RIDGE_DARK = 0.18;

    /** One materials building's look over its box [x0, x1) x [y0, y1) in pixels: a site's measured from the site's whole square. */
    static void drawLook(TilePainter.Input in, BuildingVisual.Type t, int site, int px, boolean edged, int[] img, int w,
                         int x0, int y0, int x1, int y1) {
        int look = t.look();
        int fill = t.fill(), edge = t.edge();
        // The square the pattern is measured from: the whole site's, or the box.
        int fx0 = x0, fy0 = y0, fx1 = x1 - 1, fy1 = y1 - 1;
        if (site >= 0) {
            fx0 = in.siteFX0[site] * px; fy0 = in.siteFY0[site] * px;
            fx1 = (in.siteFX1[site] + 1) * px - 1; fy1 = (in.siteFY1[site] + 1) * px - 1;
        }
        int sw = fx1 - fx0 + 1, sh = fy1 - fy0 + 1;
        int half = Math.max(1, Math.min(sw, sh) / 2);
        int step = Math.max(2, half / BENCHES);
        int[] pit = t.site() != null ? BuildingVisual.pitColours(t.site()) : null;
        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                int depth = Math.min(Math.min(x - fx0, fx1 - x), Math.min(y - fy0, fy1 - y));
                boolean onEdge = edged && (y == y0 || y == y1 - 1 || x == x0 || x == x1 - 1);
                int c;
                switch (look) {
                    case BuildingVisual.PIT: {
                        int j = depth / step;
                        c = j <= 0 ? pit[2] : depth >= half - step ? pit[0] : (j & 1) == 1 ? pit[1] : blend(pit[1], pit[0], 0.5);
                        break;
                    }
                    case BuildingVisual.QUARRY: {
                        int j = (y - fy0) / step;
                        c = depth < Math.max(1, step / 2) ? edge : (j & 1) == 0 ? fill : blend(fill, edge, 0.35);
                        break;
                    }
                    case BuildingVisual.LEACH: {
                        boolean pond = x - fx0 < sw / 3 && y - fy0 < sh / 3 && depth >= step / 2;
                        int j = (x - fx0) / step;
                        c = pond ? POND : depth < Math.max(1, step / 2) ? edge : (j & 1) == 0 ? fill : blend(fill, edge, 0.4);
                        break;
                    }
                    case BuildingVisual.SHAFT: {
                        int cx = (fx0 + fx1) / 2, cy = (fy0 + fy1) / 2, r = Math.max(1, Math.min(sw, sh) / 10);
                        int dx = Math.abs(x - cx), dy = Math.abs(y - cy);
                        c = dx <= r && dy <= r ? (dx < r / 2 + 1 && dy < r / 2 + 1 && r > 2 ? WINDING : HEADFRAME)
                                : depth < Math.max(1, step / 2) ? edge : fill;
                        break;
                    }
                    case BuildingVisual.WASH: {
                        int band = Math.max(1, px / 4), mid = (y0 + y1) / 2;
                        c = Math.abs(y - mid) < band && x < (x0 + x1) / 2 ? CONVEYOR : onEdge ? edge : fill;
                        break;
                    }
                    case BuildingVisual.CAMP: {
                        int band = Math.max(1, px / 4), h = y1 - y0;
                        boolean log = (Math.abs(y - (y0 + h / 3)) < band || Math.abs(y - (y0 + 2 * h / 3)) < band)
                                && x > x0 + (x1 - x0) / 5 && x < x1 - (x1 - x0) / 5;
                        c = log ? LOGS : onEdge ? edge : fill;
                        break;
                    }
                    case BuildingVisual.STAND: {
                        int g = Math.max(2, px / 2), row = (y - y0) / g;
                        int ox = (x - x0 + ((row & 1) == 1 ? g / 2 : 0)) % g - g / 2, oy = (y - y0) % g - g / 2;
                        int rr = Math.max(0, g / 3);
                        c = ox * ox + oy * oy <= rr * rr ? CROWN : onEdge ? edge : fill;
                        break;
                    }
                    case BuildingVisual.WORKS: {
                        int g = Math.max(3, px);
                        c = onEdge ? edge : (y - y0) % g == g - 1 ? blend(fill, 0xff000000, RIDGE_DARK) : fill;
                        break;
                    }
                    default:
                        c = onEdge ? edge : fill;
                }
                img[y * w + x] = c;
            }
        }
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
    /** ...its dash, 2 px on and 4 off along the pipe (the mockup's "2 4"). */
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
                // Track (0.7.72), and where a hidden street crosses it; drawn as rail (0.8.1).
                int x0 = (i % tile) * px, y0 = (i / tile) * px;
                fillRect(img, w, x0, y0, x0 + px, y0 + px, in.owned[i] ? RAIL_DARK : blend(RAIL_DARK, VOID, UNOWNED_DIM));
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
        // The track's cross-ticks (0.8.1, spec-freight 4.3): from 2 px a plot, a pixel beside the line at each block
        // boundary, so the dark line reads as rail and not a road.
        if (px >= TICKS_FROM) {
            for (int i = 0; i < TilePainter.PLOTS; i++) {
                int kind = roadKind(p, i);
                boolean hidden = !gravel && (kind == BuildingVisual.GRAVEL || kind == TilePainter.TRACK);
                if (p.use[i] != TilePainter.RAIL || (kind != 0 && !hidden) || diagonal(in, i)) continue;
                int x = i % tile, y = i / tile, x0 = x * px, y0 = y * px;
                boolean ns = railAt(in, p, i, 0) || railAt(in, p, i, 2);
                boolean ew = railAt(in, p, i, 1) || railAt(in, p, i, 3);
                if (!ns && !ew) ew = true;
                int c = in.owned[i] ? RAIL_DARK : blend(RAIL_DARK, VOID, UNOWNED_DIM);
                if (ew) {
                    if (x % block != 0) continue;
                    if (y > 0) img[(y0 - 1) * w + x0] = c;
                    if (y < tile - 1) img[(y0 + px) * w + x0] = c;
                } else {
                    if (y % block != 0) continue;
                    if (x > 0) img[y0 * w + (x0 - 1)] = c;
                    if (x < tile - 1) img[y0 * w + (x0 + px)] = c;
                }
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

    /** ...and a straight highway's, a step from plot i: not a 45-degree stretch's plot, whose marks (across the tile's edge too: marksBeyond) say it is no highway beside a straight one for a stack's test. */
    private static boolean straightHighwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir) {
        if (!highwayAt(in, p, i, dir)) return false;
        int tile = TilePainter.TILE, x = i % tile + TilePainter.DX[dir], y = i / tile + TilePainter.DY[dir];
        byte mark;
        if (x >= 0 && y >= 0 && x < tile && y < tile) {
            mark = in.marks[y * tile + x];
        } else {
            int along = dir == 0 || dir == 2 ? i % tile : i / tile;
            mark = in.marksBeyond[dir * tile + along];
        }
        return (mark & CityRuns.M_DIAG) == 0;
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
     * - in colour c.
     */
    static void diagBand(int[] img, int w, int i, int px, byte mark, double half, int c) {
        int code = (mark >> CityRuns.M_DIAG_SHIFT) & 3;
        int dx = code == 0 || code == 3 ? 1 : -1;
        double pxl = (mark & CityRuns.M_CORNER) != 0 ? (0.5 - dx) * px : 0.5 * px, pyl = 0.5 * px;
        diagLine(img, w, i, px, code, pxl, pyl, half, Double.POSITIVE_INFINITY, c);
    }

    /**
     * The pixels of plot i within `half` pixels of a line at a 45-degree
     * heading's code through the point (pxl, pyl) of the plot's pixels, and
     * no farther along the line than `along` pixels either side of that
     * point (FX3: diagBand() for a line that passes through another plot's
     * middle, and for the dashes), in colour c.
     */
    private static void diagLine(int[] img, int w, int i, int px, int code, double pxl, double pyl, double half, double along, int c) {
        int tile = TilePainter.TILE, x0 = (i % tile) * px, y0 = (i / tile) * px;
        int dx = code == 0 || code == 3 ? 1 : -1, dy = code == 0 || code == 1 ? 1 : -1;
        for (int v = 0; v < px; v++) {
            for (int u = 0; u < px; u++) {
                double qx = u + 0.5 - pxl, qy = v + 0.5 - pyl;
                if (Math.abs(qx * dy - qy * dx) / Math.sqrt(2) > half) continue;
                if (Math.abs(qx * dx + qy * dy) / Math.sqrt(2) > along) continue;
                img[(y0 + v) * w + x0 + u] = c;
            }
        }
    }

    /** The marks of the 45-degree stretch's step plot at (x, y) - in the tile, or just across one of its edges (marksBeyond) - or -1 where that plot is no step: a corner, a straight highway, other ground. */
    private static int stepMarkAt(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE, mark;
        if (x >= 0 && y >= 0 && x < tile && y < tile) {
            int j = y * tile + x;
            if (!isHighway(p, j)) return -1;
            mark = in.marks[j];
        } else {
            if ((x < 0 || x >= tile) && (y < 0 || y >= tile)) return -1;
            int dir = y < 0 ? 0 : y >= tile ? 2 : x < 0 ? 3 : 1, along = dir == 0 || dir == 2 ? x : y;
            byte f = in.fixedBeyond[dir * tile + along];
            if (f != BuildingVisual.HIGHWAY && f != TilePainter.RAIL_OVER) return -1;
            mark = in.marksBeyond[dir * tile + along];
        }
        return (mark & CityRuns.M_DIAG) != 0 && (mark & CityRuns.M_CORNER) == 0 ? mark & 0xff : -1;
    }

    /**
     * Where a 45-degree stretch passes between two of its steps the line
     * clips the corner of the plot that is not the run's - the staircase's
     * corners are on the other side of it - and the band would leave a notch
     * there (FX3, Jerus's eye check of 0.8.3). Plot i, when it is open
     * ground (not a highway, not a building), has a step on one axis and the
     * next step of the same stretch on the other: the band of the stretch's
     * line, through the step's middle, over plot i's pixels, `half` out in
     * colour c. A stretch's end adds nothing: there is no next step.
     */
    private static void diagOpen(TilePainter.Input in, TilePainter.Painted p, int i, int px, double half, int c, int[] img) {
        if (p.use[i] == TilePainter.BUILDING || diagonal(in, i)) return;
        int tile = TilePainter.TILE, x = i % tile, y = i / tile;
        int west = stepMarkAt(in, p, x - 1, y), east = stepMarkAt(in, p, x + 1, y);
        if (west < 0 && east < 0) return;
        int north = stepMarkAt(in, p, x, y - 1), south = stepMarkAt(in, p, x, y + 1);
        if (north < 0 && south < 0) return;
        for (int ex = -1; ex <= 1; ex += 2) {
            for (int ey = -1; ey <= 1; ey += 2) {
                int a = ex < 0 ? west : east, b = ey < 0 ? north : south;
                if (a < 0 || b < 0) continue;
                int code = (a >> CityRuns.M_DIAG_SHIFT) & 3;
                if (((b >> CityRuns.M_DIAG_SHIFT) & 3) != code) continue;
                int dx = code == 0 || code == 3 ? 1 : -1, dy = code == 0 || code == 1 ? 1 : -1;
                // The two steps are diagonal neighbours of each other along the heading: b - a = (-ex, ey) = +-(dx, dy).
                if (!(ex == -dx && ey == dy) && !(ex == dx && ey == -dy)) continue;
                diagLine(img, tile * px, i, px, code, (0.5 + ex) * px, 0.5 * px, half, Double.POSITIVE_INFINITY, c);
            }
        }
    }

    /* =====================================================================
       THE WAYS AS LINES (0.7.88, batch RD2; from LINES_FROM px a plot)
       ===================================================================== */

    /**
     * The ways as lines (from LINES_FROM px a plot; spec 5): each street's
     * verge, its whole plot on dry ground; the decks of bridges; the track
     * (as rail since 0.8.1); each street's surface down its middle along
     * each way it runs - half the plot for a half-width street, all of it
     * for a full one - in its kind's colour, or a track's dashes; (0.8.1)
     * the railway's crossings (railCrossings()) and from JUNCTIONS_FROM the
     * signals, roundabouts and diamonds; then the highways over all, cased,
     * their open ends brought down to the ground (highwayEnds()) and from
     * JUNCTIONS_FROM their stacks; the railway bridging a highway; the
     * ramps below JUNCTIONS_FROM; and a dashed centre line from 6 px. A
     * 45-degree stretch is one carriageway along its line (FX3, Jerus's eye
     * check of 0.8.3): never a stack, its band filled in on the open plot
     * between two steps, and a dash on every step.
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
            // ...and gravel's loose stone, a fine speckle by each pixel's own hash (FR7 2.3).
            if (kind == BuildingVisual.GRAVEL) {
                int grain = in.owned[i] ? GRAVEL_GRAIN : blend(GRAVEL_GRAIN, VOID, UNOWNED_DIM);
                if (h) grain(in, img, w, x0, y0 + o, x0 + px, y0 + o + t, grain);
                if (v) grain(in, img, w, x0 + o, y0, x0 + o + t, y0 + px, grain);
            }
        }
        // The railway's crossings: rails through a level crossing with its marks, an arterial or boulevard over the main line as a bridge.
        railCrossings(in, p, px, img);
        // Junctions on the streets, before the highways are drawn over them.
        if (px >= JUNCTIONS_FROM) {
            signals(in, p, px, img);
            roundabouts(in, p, px, img);
            diamonds(in, p, px, img);
            kerbs(in, p, px, img);
        }
        // The highways: cased, their middles and their arms to the highway beside them, across the tile's edge too.
        int[] cs = crossSection(BuildingVisual.HIGHWAY, px);
        int width = cs[0], cas = cs[1], o = (px - width) / 2;
        for (int pass = cas > 0 ? 0 : 1; pass < 2; pass++) {
            int in0 = pass == 0 ? 0 : cas;
            for (int i = 0; i < TilePainter.PLOTS; i++) {
                int x0 = (i % tile) * px, y0 = (i / tile) * px;
                int c = pass == 0 ? HIGHWAY_CASE : ROAD[BuildingVisual.HIGHWAY];
                if (!in.owned[i]) c = blend(c, VOID, UNOWNED_DIM);
                if (!isHighway(p, i)) {
                    // Open ground between two steps of a 45-degree stretch: the band the corners leave out (FX3).
                    diagOpen(in, p, i, px, (width - 2 * in0) / 2.0, c, img);
                    continue;
                }
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
        // A highway arm's end, from 4 px a plot as every way is (spec 2.7's ramp at an arm's end, which no street need pass
        // beneath), and a stack, drawn over the highways.
        highwayEnds(in, p, px, img);
        if (px >= JUNCTIONS_FROM) {
            stack(in, p, px, img);
            loops(in, p, px, img);
        }
        // The railway bridging a highway (0.7.89; spec 2.8): its deck and, drawn as rail (0.8.1), the track across the highway.
        int[] rs = crossSection(BuildingVisual.PAVED, px);
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.run[i] != TilePainter.RAIL_OVER || p.use[i] == TilePainter.BUILDING) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            boolean ns = railAt(in, p, i, 0) || railAt(in, p, i, 2);
            int deck = Math.min(px, rs[0] + 2), od = (px - deck) / 2;
            int dc = in.owned[i] ? DECK : blend(DECK, VOID, UNOWNED_DIM);
            if (ns) {
                fillRect(img, w, x0 + od, y0, x0 + od + deck, y0 + px, dc);
            } else {
                fillRect(img, w, x0, y0 + od, x0 + px, y0 + od + deck, dc);
            }
            drawTrackLook(in, p, i, px, img);
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
                // A dash on every step, over the middle 55% of its diagonal, as the straight highway has one a plot (FX3: the old every-other-plot rule gave a stretch, whose steps all share a parity, all or none).
                if ((in.marks[i] & CityRuns.M_CORNER) == 0) {
                    int code = (in.marks[i] >> CityRuns.M_DIAG_SHIFT) & 3;
                    diagLine(img, w, i, px, code, 0.5 * px, 0.5 * px, 0.5, 0.275 * Math.sqrt(2) * px, c);
                }
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
     * Since 0.8.1 below JUNCTIONS_FROM px a plot alone: from it the
     * diamond's slips are drawn instead (diamonds()).
     */
    private static void ramps(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        if (px >= JUNCTIONS_FROM) return; // The diamond draws these from 6 px a plot; below it, the corner strokes stay.
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

    /* ----- junctions' helpers ----- */

    /** The role of the street at plot (x, y) - in the tile, or just across one of its edges - ARTERIAL or BOULEVARD; 0 for none, a track, a street beneath a highway, or a corner beyond the tile. */
    private static byte streetRoleAt(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE;
        if (x >= 0 && y >= 0 && x < tile && y < tile) {
            int i = y * tile + x;
            return isStreet(p, i) ? p.role[i] : 0;
        }
        if ((x < 0 || x >= tile) && (y < 0 || y >= tile)) return 0;
        int dir = y < 0 ? 0 : y >= tile ? 2 : x < 0 ? 3 : 1;
        int along = dir == 0 || dir == 2 ? x : y;
        int code = in.beyond[dir * tile + along] & 0xff;
        int kind = code & TilePainter.S_KIND;
        if (kind == DistrictPlan.NONE || kind == DistrictPlan.UNDER) return 0;
        return TilePainter.roleOf(code);
    }

    /** Whether a street role is one a junction is drawn for. */
    private static boolean junctionRole(byte role) {
        return role == TilePainter.ARTERIAL || role == TilePainter.BOULEVARD || role == TilePainter.ACCESS;
    }

    /**
     * The plots in a run of streets through plot (x, y) along (dx, dy), itself
     * counted: its neighbours each way while their role is a junction's
     * (boulevards alone with `boulevards`; FX3, Jerus's eye check of 0.8.3). It
     * reads at most one plot past the tile's edge (streetRoleAt() answers any
     * x or y beyond with that one plot), so it stops at -1 and at TILE.
     */
    private static int junctionRun(TilePainter.Input in, TilePainter.Painted p, int x, int y, int dx, int dy, boolean boulevards) {
        int tile = TilePainter.TILE, n = 1;
        for (int s = -1; s <= 1; s += 2) {
            int cx = x + s * dx, cy = y + s * dy;
            while (cx >= -1 && cy >= -1 && cx <= tile && cy <= tile) {
                byte role = streetRoleAt(in, p, cx, cy);
                if (boulevards ? role != TilePainter.BOULEVARD : !junctionRole(role)) break;
                n++;
                cx += s * dx;
                cy += s * dy;
            }
        }
        return n;
    }

    /** Whether the street at plot (x, y) is a signalled junction: itself an arterial or boulevard, with a run of JUNCTION_RUN or more of them through it on each axis (a road two plots wide is one road), and not a roundabout. */
    private static boolean signalAt(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        byte role = streetRoleAt(in, p, x, y);
        if (!junctionRole(role) || roundaboutAt(in, p, x, y)) return false;
        return junctionRun(in, p, x, y, 1, 0, false) >= JUNCTION_RUN && junctionRun(in, p, x, y, 0, 1, false) >= JUNCTION_RUN;
    }

    /** Whether the street at plot (x, y) is a roundabout: itself a boulevard, with a run of JUNCTION_RUN or more boulevards through it on each axis. */
    private static boolean roundaboutAt(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        if (streetRoleAt(in, p, x, y) != TilePainter.BOULEVARD) return false;
        return junctionRun(in, p, x, y, 1, 0, true) >= JUNCTION_RUN && junctionRun(in, p, x, y, 0, 1, true) >= JUNCTION_RUN;
    }

    /** A street's surface across its plot at px pixels a plot: {its width, its offset from the plot's edge}. */
    private static int[] surfaceAt(TilePainter.Painted p, int i, int px) {
        int t = Math.max(1, (int) Math.round(SURFACE[Math.max(1, Math.min(2, (int) p.width[i]))] * px));
        return new int[] { t, (px - t) / 2 };
    }

    /* ----- 1. signals: arterial x arterial ----- */

    private static void signals(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE;
        // Each junction's surface is the dark box.
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!signalAt(in, p, i % tile, i / tile)) continue;
            int[] s = surfaceAt(p, i, px);
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            int c = in.owned[i] ? JUNCTION_BOX : blend(JUNCTION_BOX, VOID, UNOWNED_DIM);
            boolean h = streetAt(in, p, i, 1) || streetAt(in, p, i, 3);
            boolean v = streetAt(in, p, i, 0) || streetAt(in, p, i, 2);
            if (h) fillRect(img, tile * px, x0, y0 + s[1], x0 + px, y0 + s[1] + s[0], c);
            if (v) fillRect(img, tile * px, x0 + s[1], y0, x0 + s[1] + s[0], y0 + px, c);
        }
        // Each arm, drawn by the plot it is on (so the two tiles draw their halves of a junction across an edge).
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isStreet(p, i) || p.beneath[i] || p.road[i] == TilePainter.TRACK) continue;
            int x = i % tile, y = i / tile;
            for (int dir = 0; dir < 4; dir++) {
                if (!signalAt(in, p, x + TilePainter.DX[dir], y + TilePainter.DY[dir])) continue;
                drawSignalArm(in, p, i, px, img, dir);
            }
        }
    }

    /** A zebra crossing and its stop line on one arm of a signal, at the edge facing the junction. */
    private static void drawSignalArm(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int dir) {
        int tile = TilePainter.TILE, w = tile * px;
        int[] s = surfaceAt(p, i, px);
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        int depth = Math.max(1, (int) Math.round(ZEBRA_DEPTH * px));
        int zebra = in.owned[i] ? ZEBRA : blend(ZEBRA, VOID, UNOWNED_DIM);
        int half = s[0] / 2;
        if (dir == 0 || dir == 2) { // A north-south arm: stripes run along the arm, across its width.
            int y = dir == 0 ? 0 : px - depth;
            for (int dx = s[1]; dx < s[1] + s[0]; dx++) {
                if ((dx - s[1]) % 2 == 0) fillRect(img, w, x0 + dx, y0 + y, x0 + dx + 1, y0 + y + depth, zebra);
            }
            int stopY = dir == 0 ? depth : px - depth - 1;
            int startX = dir == 0 ? s[1] + s[0] - half : s[1];
            fillRect(img, w, x0 + startX, y0 + stopY, x0 + startX + half, y0 + stopY + 1, zebra);
        } else { // An east-west arm.
            int x = dir == 1 ? px - depth : 0;
            for (int dy = s[1]; dy < s[1] + s[0]; dy++) {
                if ((dy - s[1]) % 2 == 0) fillRect(img, w, x0 + x, y0 + dy, x0 + x + depth, y0 + dy + 1, zebra);
            }
            int stopX = dir == 1 ? px - depth - 1 : depth;
            int startY = dir == 1 ? s[1] + s[0] - half : s[1];
            fillRect(img, w, x0 + stopX, y0 + startY, x0 + stopX + 1, y0 + startY + half, zebra);
        }
    }

    /* ----- 2. roundabouts: boulevard x boulevard ----- */

    private static void roundabouts(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!roundaboutTarget(in, p, i)) continue;
            int x = i % tile, y = i / tile;
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (roundaboutAt(in, p, x + dx, y + dy)) drawRoundaboutOn(in, p, i, px, img, x + dx, y + dy);
                }
            }
        }
    }

    /** Whether a roundabout's ring or island may be drawn on plot i: a street or empty ground, never a building, the track, a highway or water. */
    private static boolean roundaboutTarget(TilePainter.Input in, TilePainter.Painted p, int i) {
        if (p.use[i] == TilePainter.BUILDING || p.use[i] == TilePainter.RAIL || isHighway(p, i)) return false;
        if (in.terrain[i] == World.SALT || in.terrain[i] == World.FRESH) return false;
        return p.use[i] == TilePainter.EMPTY || p.use[i] == TilePainter.FIELD || isStreet(p, i);
    }

    /** The ring's and island's pixels of a roundabout centred on plot (cx, cy) that fall on plot i. */
    private static void drawRoundaboutOn(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int cx, int cy) {
        int tile = TilePainter.TILE, w = tile * px;
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        double centreX = (cx + 0.5) * px, centreY = (cy + 0.5) * px;
        double outer = ROUNDABOUT_RADIUS * px, inner = ROUNDABOUT_ISLAND * px;
        int paved = in.owned[i] ? ROAD[BuildingVisual.PAVED] : blend(ROAD[BuildingVisual.PAVED], VOID, UNOWNED_DIM);
        int island = in.owned[i] ? ISLAND : blend(ISLAND, VOID, UNOWNED_DIM);
        for (int v = 0; v < px; v++) {
            for (int u = 0; u < px; u++) {
                double qx = x0 + u + 0.5 - centreX, qy = y0 + v + 0.5 - centreY;
                double d = Math.sqrt(qx * qx + qy * qy);
                if (d <= inner) img[(y0 + v) * w + x0 + u] = island;
                else if (d <= outer) img[(y0 + v) * w + x0 + u] = paved;
            }
        }
    }

    /* ----- 3. diamonds: a street passes beneath a highway ----- */

    private static void diamonds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE;
        // The ramps in the tile, and those just across its edges: an arterial lies on a cell's ring, the tile's first row or
        // column, so a diamond's slips on one side of its street are the next tile's to draw.
        for (int y = -1; y <= tile; y++) {
            for (int x = -1; x <= tile; x++) {
                boolean inside = x >= 0 && y >= 0 && x < tile && y < tile;
                if (!inside && (x < 0 || x >= tile) && (y < 0 || y >= tile)) continue;
                if (inside) {
                    int i = y * tile + x;
                    if (!isHighway(p, i) || (in.marks[i] & CityRuns.M_RAMP) == 0 || !p.beneath[i]) continue;
                } else {
                    int dir = y < 0 ? 0 : y >= tile ? 2 : x < 0 ? 3 : 1, along = dir == 0 || dir == 2 ? x : y;
                    if (in.fixedBeyond[dir * tile + along] != BuildingVisual.HIGHWAY || (in.marksBeyond[dir * tile + along] & CityRuns.M_RAMP) == 0
                            || !isStreetAt(in, p, x, y)) continue;
                }
                boolean rowHw = isRunHighway(in, p, x - 1, y) || isRunHighway(in, p, x + 1, y);
                boolean colHw = isRunHighway(in, p, x, y - 1) || isRunHighway(in, p, x, y + 1);
                if (rowHw == colHw) continue;
                boolean streetAcross = rowHw
                        ? isStreetAt(in, p, x, y - 1) || isStreetAt(in, p, x, y + 1)
                        : isStreetAt(in, p, x - 1, y) || isStreetAt(in, p, x + 1, y);
                if (!streetAcross) continue;
                for (int side = -1; side <= 1; side += 2) {
                    for (int dir = -1; dir <= 1; dir += 2) drawSlip(in, p, px, img, x, y, rowHw, side, dir);
                }
            }
        }
    }


    /** One diamond slip along the verge beside a highway, from the street out for SLIP_LENGTH plots. */
    private static void drawSlip(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int hx, int hy, boolean rowHw, int side, int dir) {
        int tile = TilePainter.TILE, w = tile * px;
        double half = Math.max(0.6, RAMP_WIDTH * px / 2.0);
        for (int k = 1; k <= (int) SLIP_LENGTH; k++) {
            int tx = rowHw ? hx + dir * k : hx + side;
            int ty = rowHw ? hy + side : hy + dir * k;
            if (tx < 0 || ty < 0 || tx >= tile || ty >= tile) return;
            int i = ty * tile + tx;
            if (p.use[i] != TilePainter.EMPTY && p.use[i] != TilePainter.FIELD) return;
            int x0 = tx * px, y0 = ty * px;
            int c = in.owned[i] ? ROAD[BuildingVisual.PAVED] : blend(ROAD[BuildingVisual.PAVED], VOID, UNOWNED_DIM);
            if (rowHw) {
                segment(img, w, x0, y0, px, 0, px / 2.0, px, px / 2.0, half, c);
                if (k == SLIP_LENGTH) segment(img, w, x0, y0, px, px / 2.0, px / 2.0, px / 2.0, side < 0 ? px : 0, half, c);
            } else {
                segment(img, w, x0, y0, px, px / 2.0, 0, px / 2.0, px, half, c);
                if (k == SLIP_LENGTH) segment(img, w, x0, y0, px, px / 2.0, px / 2.0, side < 0 ? px : 0, px / 2.0, half, c);
            }
        }
    }

    /* ----- 4. a highway arm's end ----- */

    /**
     * A highway arm's end (spec 2.7's ramp at an arm's end): a highway plot the runs mark as a ramp with no street beneath,
     * where the highway runs on exactly one of its four sides - the band narrows to a paved road's and a paved apron is
     * laid on the next plot. A ramp where the district plan laid no street beneath, in the middle of a straight highway
     * (the highway on two sides along it) or at a crossing, is not the highway's end and stays the plain highway (FX2,
     * Jerus's eye check of 0.8.2).
     */
    private static void highwayEnds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, stride = tile * px;
        int[] fullCs = crossSection(BuildingVisual.HIGHWAY, px);
        int full = fullCs[0], cas = fullCs[1];
        int paved = crossSection(BuildingVisual.PAVED, px)[0];
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isHighway(p, i) || (in.marks[i] & CityRuns.M_RAMP) == 0 || p.beneath[i]) continue;
            boolean n = runHighwayAt(in, p, i, 0), s = runHighwayAt(in, p, i, 2);
            boolean e = runHighwayAt(in, p, i, 1), west = runHighwayAt(in, p, i, 3);
            boolean ns = n || s;
            // An end has the highway on exactly one side: a lone plot (none), a bend or crossing, and the middle of a straight
            // highway (two sides, a ramp the plan laid no street beneath) are not ends and stay the plain highway.
            if ((n ? 1 : 0) + (s ? 1 : 0) + (e ? 1 : 0) + (west ? 1 : 0) != 1) continue;
            int x0 = (i % tile) * px, y0 = (i / tile) * px;
            int ground = groundColour(in, p, i);
            fillRect(img, stride, x0, y0, x0 + px, y0 + px, ground);
            int fill = in.owned[i] ? ROAD[BuildingVisual.HIGHWAY] : blend(ROAD[BuildingVisual.HIGHWAY], VOID, UNOWNED_DIM);
            int caseC = in.owned[i] ? HIGHWAY_CASE : blend(HIGHWAY_CASE, VOID, UNOWNED_DIM);
            int apron = in.owned[i] ? ROAD[BuildingVisual.PAVED] : blend(ROAD[BuildingVisual.PAVED], VOID, UNOWNED_DIM);
            boolean fromTop = n, fromLeft = west;
            if (ns) {
                for (int y = 0; y < px; y++) {
                    double f = (fromTop ? y : px - 1 - y) / (double) (px - 1);
                    int band = Math.max(paved, Math.min(px, (int) Math.round(full + (paved - full) * f)));
                    int left = (px - band) / 2;
                    fillRect(img, stride, x0 + left, y0 + y, x0 + left + band, y0 + y + 1, fill);
                    if (cas > 0 && band > 0) {
                        fillRect(img, stride, x0 + left, y0 + y, x0 + left + 1, y0 + y + 1, caseC);
                        fillRect(img, stride, x0 + left + band - 1, y0 + y, x0 + left + band, y0 + y + 1, caseC);
                    }
                }
                // Where the highway is no wider than a paved road (4 px a plot), the half toward the open end is the paved road's.
                if (full <= paved) {
                    int left = (px - paved) / 2;
                    fillRect(img, stride, x0 + left, fromTop ? y0 + px / 2 : y0, x0 + left + paved, fromTop ? y0 + px : y0 + px / 2, apron);
                }
                // The paved apron on the next plot, from the open end to its far edge.
                int ay = fromTop ? 1 : -1;
                drawHighwayApron(in, p, px, img, i % tile, i / tile + ay, true, paved, apron);
            } else {
                for (int x = 0; x < px; x++) {
                    double f = (fromLeft ? x : px - 1 - x) / (double) (px - 1);
                    int band = Math.max(paved, Math.min(px, (int) Math.round(full + (paved - full) * f)));
                    int top = (px - band) / 2;
                    fillRect(img, stride, x0 + x, y0 + top, x0 + x + 1, y0 + top + band, fill);
                    if (cas > 0 && band > 0) {
                        fillRect(img, stride, x0 + x, y0 + top, x0 + x + 1, y0 + top + 1, caseC);
                        fillRect(img, stride, x0 + x, y0 + top + band - 1, x0 + x + 1, y0 + top + band, caseC);
                    }
                }
                if (full <= paved) {
                    int top = (px - paved) / 2;
                    fillRect(img, stride, fromLeft ? x0 + px / 2 : x0, y0 + top, fromLeft ? x0 + px : x0 + px / 2, y0 + top + paved, apron);
                }
                int ax = fromLeft ? 1 : -1;
                drawHighwayApron(in, p, px, img, i % tile + ax, i / tile, true, paved, apron);
            }
        }
    }

    /** A paved apron the paved road's width across the plot just past a highway's open end, when that plot is empty ground. */
    private static void drawHighwayApron(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int tx, int ty, boolean vertical, int paved, int apron) {
        int tile = TilePainter.TILE, w = tile * px;
        if (tx < 0 || ty < 0 || tx >= tile || ty >= tile) return;
        int i = ty * tile + tx;
        if (p.use[i] != TilePainter.EMPTY && p.use[i] != TilePainter.FIELD) return;
        int x0 = tx * px, y0 = ty * px;
        double half = paved / 2.0;
        if (vertical) segment(img, w, x0, y0, px, px / 2.0, 0, px / 2.0, px, half, apron);
        else segment(img, w, x0, y0, px, 0, px / 2.0, px, px / 2.0, half, apron);
    }

    /* ----- 5. a stack: highways cross ----- */

    /**
     * A stack where two straight highways cross: a highway plot with another
     * beside it on both axes. A 45-degree stretch's plot is never a stack,
     * nor does it count as the highway beside a straight one: its steps and
     * corners have a neighbour on both axes by the staircase's shape, and were
     * taken for crossings - Jerus's "staircase of separate dark blocks" (FX3,
     * his eye check of 0.8.3).
     */
    private static void stack(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px;
        int[] cs = crossSection(BuildingVisual.HIGHWAY, px);
        int width = cs[0], cas = cs[1], o = (px - width) / 2;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isHighway(p, i) || diagonal(in, i)) continue;
            boolean ns = straightHighwayAt(in, p, i, 0) || straightHighwayAt(in, p, i, 2);
            boolean ew = straightHighwayAt(in, p, i, 1) || straightHighwayAt(in, p, i, 3);
            if (!ns || !ew) continue;
            int hx = i % tile, hy = i / tile;
            int x0 = hx * px, y0 = hy * px;
            int fill = in.owned[i] ? ROAD[BuildingVisual.HIGHWAY] : blend(ROAD[BuildingVisual.HIGHWAY], VOID, UNOWNED_DIM);
            int caseC = in.owned[i] ? HIGHWAY_CASE : blend(HIGHWAY_CASE, VOID, UNOWNED_DIM);
            // The row's deck over the column's, its casing a pixel either side across the lower.
            fillRect(img, w, x0 + o, y0 + o, x0 + o + width, y0 + o + width, fill);
            if (cas > 0) {
                fillRect(img, w, x0 + o, y0 + o, x0 + o + width, y0 + o + cas, caseC);
                fillRect(img, w, x0 + o, y0 + o + width - cas, x0 + o + width, y0 + o + width, caseC);
            }
            // The four corner slips, when the diagonal plot is open ground - and no reserve's loops replace them (batch FR7).
            if (reserveJunction(in, hx, hy)) continue;
            for (int dy = -1; dy <= 1; dy += 2) {
                for (int dx = -1; dx <= 1; dx += 2) drawStackSlip(in, p, px, img, hx + dx, hy + dy, dx, dy);
            }
        }
    }

    /** One corner slip of a stack, across the plot diagonal to the crossing. */
    private static void drawStackSlip(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int cx, int cy, int dx, int dy) {
        int tile = TilePainter.TILE, w = tile * px;
        if (cx < 0 || cy < 0 || cx >= tile || cy >= tile) return;
        int i = cy * tile + cx;
        if (p.use[i] != TilePainter.EMPTY && p.use[i] != TilePainter.FIELD) return;
        int x0 = cx * px, y0 = cy * px;
        double half = Math.max(0.6, RAMP_WIDTH * px / 2.0);
        int c = in.owned[i] ? ROAD[BuildingVisual.HIGHWAY] : blend(ROAD[BuildingVisual.HIGHWAY], VOID, UNOWNED_DIM);
        double ax = dx < 0 ? px : 0, ay = px / 2.0, bx = px / 2.0, by = dy < 0 ? px : 0;
        segment(img, w, x0, y0, px, ax, ay, bx, by, half, c);
    }

    /* ----- 6. interchanges' loops: a cloverleaf's and a trumpet's reserve (batch FR7; spec-freight 4.6, D19) ----- */

    /** A loop's radius, in plots, and how far from the crossing's middle along each axis a cloverleaf's loop is centred (batch FR7): 2.5 and 5.0, so a loop fills its quadrant of the 19-plot reserve. */
    public static final double LOOP_RADIUS = 2.5, LOOP_AT = 5.0;

    /** A trumpet's loop: its radius and how far out along its stub and across it its centre lies, in plots - 2.0, and 5.5 and 3.0, inside the 11-plot reserve. */
    public static final double TRUMPET_RADIUS = 2.0, TRUMPET_OUT = 5.5, TRUMPET_ACROSS = 3.0;

    /** A loop's and a slip's road across, as a share of a plot: 0.34, a pixel or two wider than a gravel street's, so it reads as a ramp at 8 px a plot and not as a street. */
    public static final double LOOP_WIDTH = 0.34;

    /** Where a slip leaves its highway, in plots from the junction's middle: just off the highway's band (0.45) and (along it) 9.0 out for a cloverleaf, 10.0 for a trumpet's stub. */
    public static final double SLIP_OFF = 0.45, SLIP_OUT = 9.0, TRUMPET_SLIP_OUT = 10.0;

    /**
     * The loops of every reserve that reaches the tile, from JUNCTIONS_FROM px
     * a plot: a cloverleaf's four loops, each with a slip to either highway; a
     * trumpet's one loop with a slip to its stub and to the highway it ends
     * at - all in the highway's colour and casing, only on the reserve's own
     * open plots (never over a highway, a street, a building or a field's
     * plot of the tile that is not the reserve's).
     */
    private static void loops(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE, w = tile * px, ri = TilePainter.RESERVE_INTS;
        for (int r = 0; r < in.reserves; r++) {
            int o = r * ri, kind = in.reserve[o], stub = in.reserve[o + 1], jx = in.reserve[o + 2], jy = in.reserve[o + 3];
            int bx0 = in.reserve[o + 4], by0 = in.reserve[o + 5], bw = in.reserve[o + 6], bh = in.reserve[o + 7];
            double[][] circles = new double[kind == InterchangeGround.CLOVERLEAF ? 4 : 1][];
            double[][] slips = new double[kind == InterchangeGround.CLOVERLEAF ? 8 : 2][];
            if (kind == InterchangeGround.CLOVERLEAF) {
                int k = 0;
                for (int sy = -1; sy <= 1; sy += 2) {
                    for (int sx = -1; sx <= 1; sx += 2) {
                        circles[k] = new double[] { sx * LOOP_AT, sy * LOOP_AT, LOOP_RADIUS };
                        slips[2 * k] = spoke(sx * SLIP_OUT, sy * SLIP_OFF, sx * LOOP_AT, sy * LOOP_AT, LOOP_RADIUS);
                        slips[2 * k + 1] = spoke(sx * SLIP_OFF, sy * SLIP_OUT, sx * LOOP_AT, sy * LOOP_AT, LOOP_RADIUS);
                        k++;
                    }
                }
            } else {
                // The stub's way out is the axis a; across it, to its right, b.
                double ax = TilePainter.DX[stub], ay = TilePainter.DY[stub], bx = -ay, by = ax;
                double cx = TRUMPET_OUT * ax + TRUMPET_ACROSS * bx, cy = TRUMPET_OUT * ay + TRUMPET_ACROSS * by;
                circles[0] = new double[] { cx, cy, TRUMPET_RADIUS };
                slips[0] = spoke(TRUMPET_SLIP_OUT * ax + SLIP_OFF * bx, TRUMPET_SLIP_OUT * ay + SLIP_OFF * by, cx, cy, TRUMPET_RADIUS);
                slips[1] = spoke(SLIP_OFF * ax + (TRUMPET_ACROSS + TRUMPET_RADIUS) * bx, SLIP_OFF * ay + (TRUMPET_ACROSS + TRUMPET_RADIUS) * by, cx, cy, TRUMPET_RADIUS);
            }
            double half = LOOP_WIDTH / 2;
            int cas = px >= 6 ? 1 : 0;
            for (int y = Math.max(0, by0); y < Math.min(tile, by0 + bh); y++) {
                for (int x = Math.max(0, bx0); x < Math.min(tile, bx0 + bw); x++) {
                    int i = y * tile + x;
                    if (p.run[i] != 0 || (p.use[i] != TilePainter.EMPTY && p.use[i] != TilePainter.FIELD)) continue;
                    int fill = in.owned[i] ? ROAD[BuildingVisual.HIGHWAY] : blend(ROAD[BuildingVisual.HIGHWAY], VOID, UNOWNED_DIM);
                    int caseC = in.owned[i] ? HIGHWAY_CASE : blend(HIGHWAY_CASE, VOID, UNOWNED_DIM);
                    for (int dy = 0; dy < px; dy++) {
                        for (int dx = 0; dx < px; dx++) {
                            // The pixel's middle, in plots from the junction plot's middle.
                            double u = x + (dx + 0.5) / px - (jx + 0.5), v = y + (dy + 0.5) / px - (jy + 0.5);
                            double d = Double.MAX_VALUE;
                            for (double[] c : circles) d = Math.min(d, Math.abs(Math.hypot(u - c[0], v - c[1]) - c[2]));
                            for (double[] s : slips) d = Math.min(d, segmentDistance(u, v, s[0], s[1], s[2], s[3]));
                            if (d <= half) img[(y * px + dy) * w + x * px + dx] = fill;
                            else if (cas > 0 && d <= half + (double) cas / px) img[(y * px + dy) * w + x * px + dx] = caseC;
                        }
                    }
                }
            }
        }
    }

    /** A slip from (ax, ay) to the ring about (cx, cy) of radius r: the straight line toward the ring's middle, ending on it. */
    private static double[] spoke(double ax, double ay, double cx, double cy, double r) {
        double dx = cx - ax, dy = cy - ay, d = Math.hypot(dx, dy);
        return new double[] { ax, ay, cx - dx / d * r, cy - dy / d * r };
    }

    /** The distance from (x, y) to the segment from (ax, ay) to (bx, by). */
    private static double segmentDistance(double x, double y, double ax, double ay, double bx, double by) {
        double dx = bx - ax, dy = by - ay, l2 = dx * dx + dy * dy;
        double t = l2 == 0 ? 0 : Math.max(0, Math.min(1, ((x - ax) * dx + (y - ay) * dy) / l2));
        return Math.hypot(x - (ax + t * dx), y - (ay + t * dy));
    }

    /** Whether plot (x, y) of the tile is the junction of a reserve that reaches it: where loops replace a stack's corner slips. */
    private static boolean reserveJunction(TilePainter.Input in, int x, int y) {
        for (int r = 0; r < in.reserves; r++) {
            int o = r * TilePainter.RESERVE_INTS;
            if (in.reserve[o + 2] == x && in.reserve[o + 3] == y) return true;
        }
        return false;
    }

    /* ----- 7. kerb corners and estate T's (batch FR7; spec-freight 4.6's table) ----- */

    /** A kerb corner's radius, in plots: 0.25 (7.5 m), the corner of a + of two streets in a homes cell - and of a street's T on an arterial there. */
    public static final double KERB_RADIUS = 0.25;

    /** An estate T's corner radius, in plots: 0.5 (15 m) - wide, where trucks turn (spec-freight 4.6: "a T with wide radius corners"). */
    public static final double ESTATE_T_RADIUS = 0.5;

    /**
     * Kerb corners and estate T's, from JUNCTIONS_FROM px a plot, over the
     * streets' surfaces and their verges: at a + of two streets (a cell's
     * streets, role STREET, on all four sides) each of its four inner corners
     * is rounded by KERB_RADIUS; at a T where a street ends on an arterial,
     * boulevard or access road that runs on both sides of it, the two corners
     * beside the street are rounded by KERB_RADIUS in a homes cell and
     * ESTATE_T_RADIUS in an estate, which the painter reads from what stands
     * beside the T - a building of an estate's classes within two plots
     * (estateAt()). A rounding is the road's surface over the verge in the corner
     * between the two bars, outside the circle of the radius tangent to both
     * (fillet()), on street plots alone.
     */
    private static void kerbs(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        int tile = TilePainter.TILE;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (!isStreet(p, i) || p.beneath[i] || p.bridge[i] || p.use[i] != TilePainter.ROAD) continue;
            int kind = p.road[i];
            if (kind != BuildingVisual.GRAVEL && kind != BuildingVisual.PAVED) continue;
            int x = i % tile, y = i / tile;
            byte role = p.role[i];
            int t = Math.max(1, (int) Math.round(SURFACE[Math.max(1, Math.min(2, (int) p.width[i]))] * px)), o = (px - t) / 2;
            if (role == TilePainter.STREET) {
                boolean plus = true;
                for (int d = 0; d < 4; d++) plus &= streetRoleAt(in, p, x + TilePainter.DX[d], y + TilePainter.DY[d]) == TilePainter.STREET;
                if (!plus) continue;
                int r = Math.max(1, (int) Math.round(KERB_RADIUS * px));
                for (int sy = -1; sy <= 1; sy += 2) {
                    for (int sx = -1; sx <= 1; sx += 2) fillet(in, p, px, img, x, y, sx < 0 ? o : o + t, sy < 0 ? o : o + t, sx, sy, r);
                }
            } else if (junctionRole(role)) {
                int stem = -1, stems = 0;
                for (int d = 0; d < 4; d++) {
                    if (streetRoleAt(in, p, x + TilePainter.DX[d], y + TilePainter.DY[d]) != TilePainter.STREET) continue;
                    stem = d;
                    stems++;
                }
                if (stems != 1) continue;
                int back = (stem + 2) % 4, left = (stem + 3) % 4, right = (stem + 1) % 4;
                if (streetRoleAt(in, p, x + TilePainter.DX[back], y + TilePainter.DY[back]) != 0) continue;
                if (!junctionRole(streetRoleAt(in, p, x + TilePainter.DX[left], y + TilePainter.DY[left]))
                        || !junctionRole(streetRoleAt(in, p, x + TilePainter.DX[right], y + TilePainter.DY[right]))) continue;
                // The stem's own bar, from the plot it stands on beside this one: its edges across, in this plot's pixels.
                int sx0 = x + TilePainter.DX[stem], sy0 = y + TilePainter.DY[stem];
                int stemT = t, stemO = o;
                if (sx0 >= 0 && sy0 >= 0 && sx0 < tile && sy0 < tile && isStreet(p, sy0 * tile + sx0)) {
                    stemT = Math.max(1, (int) Math.round(SURFACE[Math.max(1, Math.min(2, (int) p.width[sy0 * tile + sx0]))] * px));
                    stemO = (px - stemT) / 2;
                }
                int r = Math.max(1, (int) Math.round((estateAt(in, p, x, y) ? ESTATE_T_RADIUS : KERB_RADIUS) * px));
                boolean ns = stem == 0 || stem == 2;
                // The arterial's near edge: the side of its bar the stem is on.
                int near = stem == 0 || stem == 3 ? o : o + t;
                for (int side = -1; side <= 1; side += 2) {
                    int across = side < 0 ? stemO : stemO + stemT;
                    if (ns) fillet(in, p, px, img, x, y, across, near, side, stem == 0 ? -1 : 1, r);
                    else fillet(in, p, px, img, x, y, near, across, stem == 3 ? -1 : 1, side, r);
                }
            }
        }
    }

    /** Whether a building of an estate's classes - industry, farms, utilities, mines and wells, transport; a homes cell holds the rest - stands within two plots of plot (x, y): the painter's reading of an estate. */
    private static boolean estateAt(TilePainter.Input in, TilePainter.Painted p, int x, int y) {
        int tile = TilePainter.TILE;
        for (int yy = Math.max(0, y - 2); yy <= Math.min(tile - 1, y + 2); yy++) {
            for (int xx = Math.max(0, x - 2); xx <= Math.min(tile - 1, x + 2); xx++) {
                int i = yy * tile + xx;
                if (p.use[i] != TilePainter.BUILDING) continue;
                int b = p.bld[i] - 1, t = b >= 0 && b < p.buildings ? p.btype[b] : -1;
                if (t < 0 || t >= in.types.length || in.types[t] == null) continue;
                int cls = in.types[t].cls();
                if (cls == BuildingVisual.INDUSTRY || cls == BuildingVisual.FARM || cls == BuildingVisual.UTILITY || cls == BuildingVisual.MINE || cls == BuildingVisual.TRANSPORT) return true;
            }
        }
        return false;
    }

    /**
     * One rounded corner: the corner point (cx, cy), in pixels from plot
     * (jx, jy)'s corner, where two bars meet; (sx, sy) the way out of it,
     * toward the verge. The pixels of the square of `r` a side out from it,
     * outside the circle of radius r centred r out along both axes, are set to
     * their plot's road colour - on a street plot of the tile alone, and only
     * where the verge is (nothing already drawn over it).
     */
    private static void fillet(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int jx, int jy, int cx, int cy, int sx, int sy, int r) {
        int tile = TilePainter.TILE, w = tile * px;
        double dcx = cx + sx * (double) r, dcy = cy + sy * (double) r;
        for (int dv = 0; dv < r; dv++) {
            for (int du = 0; du < r; du++) {
                double u = cx + sx * (du + 0.5), v = cy + sy * (dv + 0.5);
                if (Math.hypot(u - dcx, v - dcy) <= r) continue;
                int X = jx * px + (int) Math.floor(u), Y = jy * px + (int) Math.floor(v);
                if (X < 0 || Y < 0 || X >= w || Y >= w) continue;
                int i = (Y / px) * tile + X / px;
                if (!isStreet(p, i) || p.beneath[i] || p.use[i] != TilePainter.ROAD) continue;
                int kind = p.road[i];
                if (kind != BuildingVisual.GRAVEL && kind != BuildingVisual.PAVED) continue;
                int verge = in.owned[i] ? VERGE : blend(VERGE, VOID, UNOWNED_DIM);
                if (img[Y * w + X] != verge) continue;
                img[Y * w + X] = in.owned[i] ? ROAD[kind] : blend(ROAD[kind], VOID, UNOWNED_DIM);
            }
        }
    }

    /* ----- the track as rail, and its crossings (0.8.1, batch FR0; spec-freight 4.3) ----- */

    /**
     * The track (0.7.72; redrawn as rail, 0.8.1, batch FR0, spec-freight 4.3):
     * each plot's middle and its arms to the track beside it - past the tile's
     * edge too, where the line runs on - as a ballast bed, and from 4 px a
     * plot its rails as a centre line and a sleeper tick a plot, from 6 px two
     * rails on sleepers every quarter plot; double track where the main line
     * is marked. A road crossing it is drawn over the bed; from RAILS_FROM px
     * a plot the rails are then drawn through the street with its crossing
     * marks (railCrossings()).
     */
    private static void track(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.use[i] != TilePainter.RAIL) continue;
            drawTrackLook(in, p, i, px, img);
        }
    }

    /** The ballast bed's width, whole pixels: BED, or BED_DOUBLE where the plot is marked double track. */
    private static int bedWidth(int px, boolean doubled) {
        return Math.min(px, Math.max(1, (int) Math.round((doubled ? BED_DOUBLE : BED) * px)));
    }

    /** The track's look on one plot that carries rail: its ballast bed, sleepers and rails (a 45-degree stretch's just a bed and one rail). */
    private static void drawTrackLook(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img) {
        // The track beside it, north, east, south and west, read once: bit d for direction d.
        int arms = (railAt(in, p, i, 0) ? 1 : 0) | (railAt(in, p, i, 1) ? 2 : 0) | (railAt(in, p, i, 2) ? 4 : 0) | (railAt(in, p, i, 3) ? 8 : 0);
        drawTrackLook(in, p, i, px, img, arms);
    }

    /** ...with the track beside it given, bit d for direction d (a yard's head reads it from the runs: yardTrack()). */
    private static void drawTrackLook(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int arms) {
        boolean doubled = (in.marks[i] & TilePainter.M_DOUBLE) != 0;
        int bed = bedWidth(px, doubled);
        if (diagonal(in, i)) {
            int ballast = in.owned[i] ? BALLAST : blend(BALLAST, VOID, UNOWNED_DIM);
            diagBand(img, TilePainter.TILE * px, i, px, in.marks[i], bed / 2.0, ballast);
            int steel = in.owned[i] ? STEEL : blend(STEEL, VOID, UNOWNED_DIM);
            diagBand(img, TilePainter.TILE * px, i, px, in.marks[i], 0.5, steel);
            return;
        }
        boolean ns = (arms & 5) != 0;
        boolean ew = (arms & 10) != 0;
        if (!ns && !ew) ew = true;
        int a = (px - bed) / 2, z = a + bed;
        drawBed(in, p, i, px, img, a, z, arms);
        if (px >= RAILS_FROM) {
            int step = Math.max(2, (int) Math.round(SLEEPERS_EVERY * px));
            drawSleepers(in, p, i, px, img, a, z, ns, ew, step, arms);
        } else {
            drawSleeperTicks(in, p, i, px, img, a, z, ns, ew, arms);
        }
        drawRails(in, p, i, px, img, a, z, ns, ew, doubled, arms);
    }

    /** The track's ballast bed: the plot's middle and its arms to the track beside it, as the bed's whole width. */
    private static void drawBed(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int arms) {
        int tile = TilePainter.TILE, w = tile * px;
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        int c = in.owned[i] ? BALLAST : blend(BALLAST, VOID, UNOWNED_DIM);
        fillRect(img, w, x0 + a, y0 + a, x0 + z, y0 + z, c);
        if (((arms >> 0) & 1) != 0) fillRect(img, w, x0 + a, y0, x0 + z, y0 + a, c);
        if (((arms >> 2) & 1) != 0) fillRect(img, w, x0 + a, y0 + z, x0 + z, y0 + px, c);
        if (((arms >> 3) & 1) != 0) fillRect(img, w, x0, y0 + a, x0 + a, y0 + z, c);
        if (((arms >> 1) & 1) != 0) fillRect(img, w, x0 + z, y0 + a, x0 + px, y0 + z, c);
    }

    /** The sleepers, a pixel thick across the bed, every `step` pixels along the way the track runs. */
    private static void drawSleepers(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z,
                                     boolean ns, boolean ew, int step, int arms) {
        int tile = TilePainter.TILE, w = tile * px;
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        int c = in.owned[i] ? SLEEPER : blend(SLEEPER, VOID, UNOWNED_DIM);
        if (ew) {
            for (int sx = x0; sx < x0 + px; sx += step) {
                if (!(sx >= x0 + a && sx < x0 + z)
                        && !(((arms >> 3) & 1) != 0 && sx >= x0 && sx < x0 + a)
                        && !(((arms >> 1) & 1) != 0 && sx >= x0 + z && sx < x0 + px)) continue;
                fillRect(img, w, sx, y0 + a, sx + 1, y0 + z, c);
            }
        }
        if (ns) {
            for (int sy = y0; sy < y0 + px; sy += step) {
                if (!(sy >= y0 + a && sy < y0 + z)
                        && !(((arms >> 0) & 1) != 0 && sy >= y0 && sy < y0 + a)
                        && !(((arms >> 2) & 1) != 0 && sy >= y0 + z && sy < y0 + px)) continue;
                fillRect(img, w, x0 + a, sy, x0 + z, sy + 1, c);
            }
        }
    }

    /** Below RAILS_FROM px a plot, one sleeper tick across the bed at the start of each plot along the track. */
    private static void drawSleeperTicks(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z,
                                         boolean ns, boolean ew, int arms) {
        int tile = TilePainter.TILE, w = tile * px;
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        int c = in.owned[i] ? SLEEPER : blend(SLEEPER, VOID, UNOWNED_DIM);
        if (ew) fillRect(img, w, x0, y0 + a, x0 + 1, y0 + z, c);
        if (ns) fillRect(img, w, x0 + a, y0, x0 + z, y0 + 1, c);
    }

    /** The rails, a pixel each, along the way the track runs. */
    private static void drawRails(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z,
                                  boolean ns, boolean ew, boolean doubled, int arms) {
        if (px >= RAILS_FROM) drawRailsFull(in, p, i, px, img, a, z, ns, ew, doubled, arms);
        else drawRailsLow(in, p, i, px, img, a, z, ns, ew, doubled, arms);
    }

    /** From RAILS_FROM px a plot: two rails a track, double track's two tracks at the plot's middle +/- TRACKS_APART. */
    private static void drawRailsFull(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z,
                                      boolean ns, boolean ew, boolean doubled, int arms) {
        int mid = px / 2;
        int off = Math.max(1, (int) Math.round(GAUGE / 2.0 * px));
        int sep = doubled ? Math.max(1, (int) Math.round(TRACKS_APART / 2.0 * px)) : 0;
        int c = in.owned[i] ? STEEL : blend(STEEL, VOID, UNOWNED_DIM);
        if (ew) {
            if (doubled) {
                railH(in, p, i, px, img, a, z, mid - sep - off, c, arms);
                railH(in, p, i, px, img, a, z, mid - sep + off, c, arms);
                railH(in, p, i, px, img, a, z, mid + sep - off, c, arms);
                railH(in, p, i, px, img, a, z, mid + sep + off, c, arms);
            } else {
                railH(in, p, i, px, img, a, z, mid - off, c, arms);
                railH(in, p, i, px, img, a, z, mid + off, c, arms);
            }
        }
        if (ns) {
            if (doubled) {
                railV(in, p, i, px, img, a, z, mid - sep - off, c, arms);
                railV(in, p, i, px, img, a, z, mid - sep + off, c, arms);
                railV(in, p, i, px, img, a, z, mid + sep - off, c, arms);
                railV(in, p, i, px, img, a, z, mid + sep + off, c, arms);
            } else {
                railV(in, p, i, px, img, a, z, mid - off, c, arms);
                railV(in, p, i, px, img, a, z, mid + off, c, arms);
            }
        }
    }

    /** Below RAILS_FROM px a plot: one STEEL line down the track's middle, two at double track's tracks. */
    private static void drawRailsLow(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z,
                                     boolean ns, boolean ew, boolean doubled, int arms) {
        int mid = px / 2;
        int sep = doubled ? Math.max(1, (int) Math.round(TRACKS_APART / 2.0 * px)) : 0;
        int c = in.owned[i] ? STEEL : blend(STEEL, VOID, UNOWNED_DIM);
        if (ew) {
            if (doubled) {
                railH(in, p, i, px, img, a, z, mid - sep, c, arms);
                railH(in, p, i, px, img, a, z, mid + sep, c, arms);
            } else {
                railH(in, p, i, px, img, a, z, mid, c, arms);
            }
        }
        if (ns) {
            if (doubled) {
                railV(in, p, i, px, img, a, z, mid - sep, c, arms);
                railV(in, p, i, px, img, a, z, mid + sep, c, arms);
            } else {
                railV(in, p, i, px, img, a, z, mid, c, arms);
            }
        }
    }

    /** A rail a pixel wide along an east-west track at row `row`, its middle and its arms. */
    private static void railH(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int row, int c, int arms) {
        int tile = TilePainter.TILE, w = tile * px;
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        fillRect(img, w, x0 + a, y0 + row, x0 + z, y0 + row + 1, c);
        if (((arms >> 3) & 1) != 0) fillRect(img, w, x0, y0 + row, x0 + a, y0 + row + 1, c);
        if (((arms >> 1) & 1) != 0) fillRect(img, w, x0 + z, y0 + row, x0 + px, y0 + row + 1, c);
    }

    /** ...and a north-south track's, at column `col`. */
    private static void railV(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int col, int c, int arms) {
        int tile = TilePainter.TILE, w = tile * px;
        int x0 = (i % tile) * px, y0 = (i / tile) * px;
        fillRect(img, w, x0 + col, y0 + a, x0 + col + 1, y0 + z, c);
        if (((arms >> 0) & 1) != 0) fillRect(img, w, x0 + col, y0, x0 + col + 1, y0 + a, c);
        if (((arms >> 2) & 1) != 0) fillRect(img, w, x0 + col, y0 + z, x0 + col + 1, y0 + px, c);
    }

    /**
     * The railway's crossings (0.8.1, spec-freight 4.3, D18): the street's
     * surface already lies over the bed; from RAILS_FROM px a plot, draw the
     * rails through a level crossing with its crossbucks and half barriers,
     * and an arterial or boulevard over the main line as a road bridge's deck
     * edges, with no rails or crossing marks on its surface.
     */
    private static void railCrossings(TilePainter.Input in, TilePainter.Painted p, int px, int[] img) {
        if (px < RAILS_FROM) return;
        int tile = TilePainter.TILE, w = tile * px;
        for (int i = 0; i < TilePainter.PLOTS; i++) {
            if (p.use[i] != TilePainter.RAIL) continue;
            int kind = p.road[i];
            if (kind != BuildingVisual.GRAVEL && kind != BuildingVisual.PAVED) continue;
            int x = i % tile, y = i / tile, x0 = x * px, y0 = y * px;
            boolean h = streetAt(in, p, i, 1) || streetAt(in, p, i, 3);
            boolean v = streetAt(in, p, i, 0) || streetAt(in, p, i, 2);
            if (!h && !v) h = true;
            int t = Math.max(1, (int) Math.round(SURFACE[Math.max(1, Math.min(2, (int) p.width[i]))] * px));
            int o = (px - t) / 2;
            boolean main = (in.marks[i] & TilePainter.M_MAIN) != 0;
            boolean arterial = p.role[i] == TilePainter.ARTERIAL || p.role[i] == TilePainter.BOULEVARD || p.role[i] == TilePainter.ACCESS;
            if (main && arterial) {
                // An arterial or boulevard over the main line: a road bridge, never a level crossing (D18).
                int deck = in.owned[i] ? DECK : blend(DECK, VOID, UNOWNED_DIM);
                if (h) {
                    if (o > 0) fillRect(img, w, x0, y0 + o - 1, x0 + px, y0 + o, deck);
                    if (o + t < px) fillRect(img, w, x0, y0 + o + t, x0 + px, y0 + o + t + 1, deck);
                }
                if (v) {
                    if (o > 0) fillRect(img, w, x0 + o - 1, y0, x0 + o, y0 + px, deck);
                    if (o + t < px) fillRect(img, w, x0 + o + t, y0, x0 + o + t + 1, y0 + px, deck);
                }
                continue;
            }
            boolean doubled = (in.marks[i] & TilePainter.M_DOUBLE) != 0;
            if (diagonal(in, i)) {
                int steel = in.owned[i] ? STEEL : blend(STEEL, VOID, UNOWNED_DIM);
                diagBand(img, w, i, px, in.marks[i], 0.5, steel);
            } else {
                int bed = bedWidth(px, doubled);
                int a = (px - bed) / 2, z = a + bed;
                int arms = (railAt(in, p, i, 0) ? 1 : 0) | (railAt(in, p, i, 1) ? 2 : 0) | (railAt(in, p, i, 2) ? 4 : 0) | (railAt(in, p, i, 3) ? 8 : 0);
                boolean ns = (arms & 5) != 0;
                boolean ew = (arms & 10) != 0;
                if (!ns && !ew) ew = true;
                drawRails(in, p, i, px, img, a, z, ns, ew, doubled, arms);
            }
            drawCrossbucks(px, img, w, x0, y0);
            drawBarriers(in, p, i, px, img, w, x0, y0, t, o, h, v);
        }
    }

    /** A level crossing's two crossbucks: a white X in the plot's north-west and south-east corners. */
    private static void drawCrossbucks(int px, int[] img, int w, int x0, int y0) {
        int s = Math.max(3, (int) Math.round(3 * px / 8.0));
        drawX(img, w, x0, y0, s);
        drawX(img, w, x0 + px - s, y0 + px - s, s);
    }

    /** An X of side `s` in CROSSBUCK, its corner at (x0, y0). */
    private static void drawX(int[] img, int w, int x0, int y0, int s) {
        for (int k = 0; k < s; k++) {
            img[(y0 + k) * w + x0 + k] = CROSSBUCK;
            img[(y0 + k) * w + x0 + s - 1 - k] = CROSSBUCK;
        }
    }

    /** A level crossing's half barriers: a bar across the lane coming in at each edge where the street enters the plot. */
    private static void drawBarriers(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int w,
                                     int x0, int y0, int t, int o, boolean h, boolean v) {
        int half = t / 2;
        if (half <= 0) return;
        if (h) {
            if (streetAt(in, p, i, 3)) {
                int y = y0 + o + (t - half);
                for (int k = 0; k < half; k++) img[(y + k) * w + x0] = (k & 1) == 0 ? BARRIER : CROSSBUCK;
            }
            if (streetAt(in, p, i, 1)) {
                int y = y0 + o;
                for (int k = 0; k < half; k++) img[(y + k) * w + x0 + px - 1] = (k & 1) == 0 ? BARRIER : CROSSBUCK;
            }
        }
        if (v) {
            if (streetAt(in, p, i, 0)) {
                int x = x0 + o;
                for (int k = 0; k < half; k++) img[y0 * w + x + k] = (k & 1) == 0 ? BARRIER : CROSSBUCK;
            }
            if (streetAt(in, p, i, 2)) {
                int x = x0 + o + (t - half);
                for (int k = 0; k < half; k++) img[(y0 + px - 1) * w + x + k] = (k & 1) == 0 ? BARRIER : CROSSBUCK;
            }
        }
    }

    /* =====================================================================
       A RECTANGLE OF PIXELS, AND THE PLOTS' COLOURS
       ===================================================================== */

    /** Speckles GRAVEL_GRAIN_SHARE of the box's pixels with c, each by a hash of its pixel in the tile and the tile's place, so the same street speckles the same at every paint. */
    private static void grain(TilePainter.Input in, int[] img, int w, int x0, int y0, int x1, int y1, int c) {
        for (int y = y0; y < y1; y++) {
            int o = y * w;
            for (int x = x0; x < x1; x++) {
                double u = World.unit(World.mix((in.tx * 8191L + x) * 0x9E3779B97F4A7C15L
                        ^ (in.ty * 8191L + y) * 0xC2B2AE3D27D4EB4FL ^ in.seed ^ 0x47524156L));
                if (u < GRAVEL_GRAIN_SHARE) img[o + x] = c;
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
    public static final int[] ROAD_SMALL = { 0, 0xffbdb8ae, 0xff6a7077, 0xff3b4048 };

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
