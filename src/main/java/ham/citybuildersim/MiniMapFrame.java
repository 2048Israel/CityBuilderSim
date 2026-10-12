package ham.citybuildersim;

import java.util.Set;

/**
 * The minimap's arithmetic without the toolkit: what the stamp at the rail's foot and its hover preview frame - the city's built-up ground, the ground it owns in its districts with buildings, and a margin round it - at how many whole plots a pixel, the far nodes that draw them from the district pictures, and what a click opens the expanded map on: what ui/MiniMap draws by and MapCheck holds.
 *
 * WHY THIS EXISTS (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3).
 * Jerus, 2026-10-11: "i think a small map always visible on the ui would be
 * nice or no?". The city's map was on screen only in the land office; the
 * study put a stamp of it at the foot of the rail, on screen from every tab,
 * a preview on hover and the expanded map on a click. What it shows is the
 * question this class answers. Framed on everything the city owns, as the
 * land office opens (LandMap.openingBox()), a city that bought a stretch of
 * sea for its wells or a field of iron two districts out would shrink to a
 * dot in a 66-pixel stamp; so the stamp frames its BUILT-UP ground: each
 * district with buildings (CityMap.District.usedCells()), shrunk to the
 * ground the city owns in it - a new town's few hundred plots, not the
 * 256-plot square of the district it stands in - and MARGIN round that.
 * Owned ground in a district with nothing built on it is left out.
 *
 * HOW IT IS DRAWN. A view (View) is w x h pixels at a whole number of plots
 * a pixel, the least that holds the frame, centred on it; its pixels are
 * MapTiles.nodePixels()'s far nodes at that stride (MM1: each district from
 * its picture where it is MapTiles.PICTURE_FROM_PX pixels across or more,
 * dithered by its shares where smaller, the highways, the railway and the
 * city's edge at least a pixel wide), laid from the view's corner and cut
 * to it (pixels()). Their ground is the world's (ground(), any thread); the
 * pixels are the screen's thread's, and draw no plan: a district whose
 * picture is not drawn is dithered meanwhile and named in what pixels()
 * wants, which the screen asks of CityMap.pictureJobs() on the map's worker,
 * as the expanded map does. No boats (the study's Q12): nodePixels() draws
 * none. On the real Earth it is the city's plane, never the globe (8.3).
 *
 * Pure: the same map and land give the same frame and the same pixels.
 */
public final class MiniMapFrame {

    private MiniMapFrame() { }

    /** The stamp at the rail's foot, in pixels: 66 x 44 (the study's 8.3, est.: as wide as the rail's 76 allows inside its edge and a pixel of border either side, 3 by 2 as the preview is). */
    public static final int STAMP_W = 66, STAMP_H = 44;

    /** The preview beside it on hover, in pixels: 288 x 192 (the study's 8.3, est.; the mockup's panel). */
    public static final int PREVIEW_W = 288, PREVIEW_H = 192;

    /** The margin round the built-up ground, each way, as a share of its longer side (star MM2-2): 0.1 - the city 83% of the frame, a tenth of its size round it, so its white edge (MapTiles.EDGE) stands clear of the stamp's border with the ground beyond in view, as the mockup's stamp draws it (the city's edge 5 to 11% of the frame in from its sides there). */
    public static final double MARGIN = 0.1;

    /** The most the stamp may cost the screen's thread when the map's version moves - its frame found and its far node drawn - in ms of the thread's CPU (MapCheck 12): 8, a frame's painting (spec-land 2.6; ui/MapView's FRAME_MS), so it is drawn in the frame the month lands in (the study's "a few milliseconds", est.). */
    public static final double STAMP_MS = 8;

    /** A box of plots, [x0, x1) x [y0, y1). */
    public record Box(long x0, long y0, long x1, long y1) {
        public long width()      { return x1 - x0; }
        public long height()     { return y1 - y0; }
        public double centreX()  { return (x0 + x1) / 2.0; }
        public double centreY()  { return (y0 + y1) / 2.0; }
        /** Whether it holds another whole. */
        public boolean holds(Box o) { return o != null && x0 <= o.x0 && y0 <= o.y0 && x1 >= o.x1 && y1 >= o.y1; }
    }

    /* =====================================================================
       THE FRAME: THE BUILT-UP GROUND AND ITS MARGIN
       ===================================================================== */

    /** District d's square of plots on map m: [x, x + World.DISTRICT) each way. */
    public static Box districtBox(CityMap m, CityMap.District d) {
        long x = (m.baseDX() + d.dx) * World.DISTRICT, y = (m.baseDY() + d.dy) * World.DISTRICT;
        return new Box(x, y, x + World.DISTRICT, y + World.DISTRICT);
    }

    /**
     * The city's built-up ground: the bounds of the ground it owns in each of
     * its districts with buildings (usedCells() over nothing) - a built
     * district with none of it owned (a harness's square city, whose
     * capacities were given) its whole square. Null when nothing is built.
     *
     * Only the outermost built columns and rows are read: ground owned in a
     * district lies in its square, so the westmost of it all lies in the
     * westmost column of built districts, and so on - each of those
     * districts' owned ground's edge found on the grid (ownedEdge()), the
     * rest never asked. A few hundred of the grid's sums at ten billion
     * people, not one for each of its thirty thousand districts.
     */
    public static Box builtUp(CityMap m) {
        int dx0 = Integer.MAX_VALUE, dy0 = Integer.MAX_VALUE, dx1 = Integer.MIN_VALUE, dy1 = Integer.MIN_VALUE;
        for (CityMap.District d : m.districts()) {
            if (d.usedCells() <= 0) continue;
            dx0 = Math.min(dx0, d.dx);
            dy0 = Math.min(dy0, d.dy);
            dx1 = Math.max(dx1, d.dx);
            dy1 = Math.max(dy1, d.dy);
        }
        if (dx0 > dx1) return null;
        CityLand land = m.land();
        LandGrid grid = land == null ? null : land.grid();
        long x0 = Long.MAX_VALUE, y0 = Long.MAX_VALUE, x1 = Long.MIN_VALUE, y1 = Long.MIN_VALUE;
        for (CityMap.District d : m.districts()) {
            if (d.usedCells() <= 0 || (d.dx != dx0 && d.dx != dx1 && d.dy != dy0 && d.dy != dy1)) continue;
            Box b = districtBox(m, d);
            boolean owns = grid != null && grid.owned(b.x0(), b.y0(), b.x1(), b.y1()) > 0;
            if (d.dx == dx0) x0 = Math.min(x0, owns ? ownedEdge(grid, b, WEST) : b.x0());
            if (d.dx == dx1) x1 = Math.max(x1, owns ? ownedEdge(grid, b, EAST) : b.x1());
            if (d.dy == dy0) y0 = Math.min(y0, owns ? ownedEdge(grid, b, NORTH) : b.y0());
            if (d.dy == dy1) y1 = Math.max(y1, owns ? ownedEdge(grid, b, SOUTH) : b.y1());
        }
        return new Box(x0, y0, x1, y1);
    }

    /** The sides ownedEdge() finds. */
    static final int WEST = 0, NORTH = 1, EAST = 2, SOUTH = 3;

    /**
     * The edge of the ground the grid owns within a box holding some of it,
     * on one side: the west and north the first plot's column or row, the
     * east and south one past the last - found by halving, the grid's sum of
     * a part of the box each step (LandGrid.owned()), the whole box first.
     */
    static long ownedEdge(LandGrid g, Box b, int side) {
        boolean across = side == WEST || side == EAST;
        long lo = across ? b.x0() : b.y0(), hi = across ? b.x1() : b.y1();
        if (side == WEST || side == NORTH) {
            // The least c with ground owned in [lo, c + 1).
            while (lo < hi - 1) {
                long mid = (lo + hi) >>> 1;
                long got = across ? g.owned(b.x0(), b.y0(), mid, b.y1()) : g.owned(b.x0(), b.y0(), b.x1(), mid);
                if (got > 0) hi = mid; else lo = mid;
            }
            long first = across ? g.owned(b.x0(), b.y0(), lo + 1, b.y1()) : g.owned(b.x0(), b.y0(), b.x1(), lo + 1);
            return first > 0 ? lo : hi;
        }
        // One past the greatest c with ground owned in [c, hi).
        long a = lo, z = hi;
        while (a < z - 1) {
            long mid = (a + z) >>> 1;
            long got = across ? g.owned(mid, b.y0(), b.x1(), b.y1()) : g.owned(b.x0(), mid, b.x1(), b.y1());
            if (got > 0) a = mid; else z = mid;
        }
        return a + 1;
    }

    /** A box with MARGIN of its longer side round it each way, in whole plots (rounded up). */
    public static Box withMargin(Box b) {
        long m = (long) Math.ceil(MARGIN * Math.max(b.width(), b.height()));
        return new Box(b.x0() - m, b.y0() - m, b.x1() + m, b.y1() + m);
    }

    /**
     * What the stamp and the preview frame: the built-up ground with its
     * margin (builtUp(), withMargin()); before anything is built, the ground
     * the city owns with it (star MM2-3: a city founded and not yet built on
     * has only its centre); null for a map with no land.
     */
    public static Box frame(CityMap m) {
        Box b = builtUp(m);
        if (b == null) {
            CityLand land = m.land();
            if (land == null) return null;
            LandGrid g = land.grid();
            b = g.ownedPlots() > 0 ? new Box(g.minX(), g.minY(), g.maxX(), g.maxY())
                    : new Box(land.siteX(), land.siteY(), land.siteX() + 1, land.siteY() + 1);
        }
        return withMargin(b);
    }

    /* =====================================================================
       A VIEW: W x H PIXELS AT A WHOLE NUMBER OF PLOTS A PIXEL
       ===================================================================== */

    /** A view: w x h pixels, `stride` whole plots a pixel each way, its north-west corner at plot (x0, y0). */
    public record View(long x0, long y0, long stride, int w, int h) {
        /** The plots it shows: [x0, x0 + w stride) x [y0, y0 + h stride). */
        public Box shown() { return new Box(x0, y0, x0 + w * stride, y0 + h * stride); }
        /** Far nodes across and down: MapFrame.NODE_PX pixels each, laid from its corner. */
        public int nodesAcross() { return (w + MapFrame.NODE_PX - 1) / MapFrame.NODE_PX; }
        public int nodesDown()   { return (h + MapFrame.NODE_PX - 1) / MapFrame.NODE_PX; }
        /** Node (i, j)'s north-west plot. */
        public long nodeX(int i) { return x0 + (long) i * MapFrame.NODE_PX * stride; }
        public long nodeY(int j) { return y0 + (long) j * MapFrame.NODE_PX * stride; }
        /** The plot at the middle of pixel (px, py)'s... */
        public double plotX(double px) { return x0 + px * stride; }
        public double plotY(double py) { return y0 + py * stride; }
        /** ...and the view as the map view's frame: its centre, at 1 / stride px a plot - what MapTiles.picturesFit() is asked with. */
        public MapFrame asMapFrame() {
            MapFrame f = new MapFrame(w, h);
            f.set(x0 + w * stride / 2.0, y0 + h * stride / 2.0, 1.0 / stride);
            return f;
        }
    }

    /** The least whole number of plots a pixel at which a w x h view holds a box: at least one. */
    public static long strideFor(Box b, int w, int h) {
        long sx = (b.width() + w - 1) / w, sy = (b.height() + h - 1) / h;
        return Math.max(1, Math.max(sx, sy));
    }

    /** A w x h view of a box: at strideFor(), centred on it, its corner on whole plots - so it holds the box whole. */
    public static View view(Box b, int w, int h) {
        long s = strideFor(b, w, h);
        long x0 = (long) Math.floor(b.centreX() - w * s / 2.0), y0 = (long) Math.floor(b.centreY() - h * s / 2.0);
        return new View(x0, y0, s, w, h);
    }

    /** The stamp's view of a frame (STAMP_W x STAMP_H)... */
    public static View stamp(Box frame)   { return view(frame, STAMP_W, STAMP_H); }

    /** ...and the preview's (PREVIEW_W x PREVIEW_H), at its own stride, the same frame. */
    public static View preview(Box frame) { return view(frame, PREVIEW_W, PREVIEW_H); }

    /* =====================================================================
       ITS PIXELS: THE FAR NODES, FROM THE DISTRICT PICTURES
       ===================================================================== */

    /** The ground of each of a view's far nodes, row by row (MapTiles.nodeTerrain() at its stride, from its corner): the world's, so any thread - the map's worker's. */
    public static byte[][] ground(World.Key world, View v) {
        int a = v.nodesAcross(), d = v.nodesDown();
        byte[][] out = new byte[a * d][];
        for (int j = 0; j < d; j++) for (int i = 0; i < a; i++) out[j * a + i] = MapTiles.nodeTerrain(world, v.nodeX(i), v.nodeY(j), v.stride());
        return out;
    }

    /**
     * A view's pixels into `out` (w x h, row by row, 0xAARRGGBB): each far
     * node's (MapTiles.nodePixels() at the view's stride, from its ground)
     * cut to the view. Into `wanted`, the built districts drawn without the
     * picture they would sample (MapTiles.wanted()), to be asked of
     * CityMap.pictureJobs() away from this thread; returns how many. The
     * screen's thread, as the tiles are: it reads the pictures kept
     * (CityMap.pictureIfCurrent()) and draws no plan.
     */
    public static int pixels(MapTiles tiles, View v, byte[][] ground, int[] out, Set<CityMap.District> wanted) {
        int[] buf = new int[MapFrame.NODE_PX * MapFrame.NODE_PX];
        for (int k = 0; k < v.nodesAcross() * v.nodesDown(); k++) node(tiles, v, k, ground[k], buf, out, wanted);
        return wanted == null ? 0 : wanted.size();
    }

    /** ...one node of it, the k-th row by row (MapTiles.nodePixels() into `buf`, NODE_PX squared, cut into `out`): what the screen paints a frame at a time. */
    public static void node(MapTiles tiles, View v, int k, byte[] ground, int[] buf, int[] out, Set<CityMap.District> wanted) {
        int n = MapFrame.NODE_PX, i = k % v.nodesAcross(), j = k / v.nodesAcross();
        tiles.nodePixels(v.nodeX(i), v.nodeY(j), v.stride(), ground, buf);
        if (wanted != null) wanted.addAll(tiles.wanted());
        int cw = Math.min(n, v.w() - i * n), ch = Math.min(n, v.h() - j * n);
        for (int y = 0; y < ch; y++) System.arraycopy(buf, y * n, out, (j * n + y) * v.w() + i * n, cw);
    }

    /* =====================================================================
       A CLICK
       ===================================================================== */

    /** What a click on the stamp opens: the expanded map fitted on the stamp's frame (MapFrame.fit(), FIT_MARGIN of it) - the built-up ground it shows, bigger (star MM2-4). */
    public static void open(MapFrame big, Box frame) {
        if (frame != null) big.fit(frame.x0(), frame.y0(), frame.x1(), frame.y1());
    }
}
