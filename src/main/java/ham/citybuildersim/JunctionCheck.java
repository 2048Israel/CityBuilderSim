package ham.citybuildersim;

import java.util.Arrays;

/**
 * Proves that the junctions and interchanges of spec-freight 4.6 are drawn
 * by rule from what crosses what, not as buildings (0.8.1, batch FR0; D19).
 * Not part of the game.
 *
 * WHY THIS EXISTS. Until 0.8.1 a + was two street bars overlapping, a ramp
 * was one diagonal stroke across a verge plot's corner, and a highway
 * crossing a highway was a plain overlap. This draws the table's first five
 * rows at the 8-px raster: signals, roundabouts, diamonds, an arm's end and
 * a stack, each from hand-built painted tiles.
 *
 * What it has to prove, a section each, every one a fixture that sets the
 * condition by hand:
 *
 *   1. an arterial crossing an arterial has JUNCTION_BOX pixels on the
 *      junction plot and ZEBRA stripes and stop lines on its four arms; a
 *      plain street crossing keeps the paved colour;
 *   2. a boulevard crossing a boulevard has ISLAND pixels at its middle and
 *      the paved ring's pixels on its four neighbouring street plots, and
 *      none on a building placed diagonally beside it;
 *   3. a highway with M_RAMP where an arterial passes beneath has paved
 *      slip pixels on the verge rows above and below, both sides of the
 *      street, SLIP_LENGTH plots out, and none where a building stands on
 *      the verge row;
 *   4. an arm's last plot with M_RAMP and no street beneath rasters
 *      differently from the same tile without the mark, and at its open end
 *      the band is no wider than the paved road's width;
 *   5. two highways crossing have HIGHWAY_CASE pixels across the column's
 *      highway and slip pixels in the highway's colour in the four corner
 *      plots, but none in a corner plot holding a building;
 *   6. at 4 px, below JUNCTIONS_FROM, none of these fixtures has a
 *      JUNCTION_BOX, ZEBRA or ISLAND pixel;
 *   7. a roundabout whose centre is a tile's last column is read through
 *      Input.beyond by the next tile, which draws the ring's pixels on its
 *      first column.
 *   8. a diamond whose street is a tile's first row (an arterial lies on a
 *      cell's ring, so most do): the tile holding the ramp draws its side's
 *      slips, and the tile across the edge draws the other side's from the
 *      ramp it reads beyond its edge.
 *
 * Numbering is the running order.
 *
 * @author Jerus
 */
public class JunctionCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-70s %s%n", label, ok ? "OK" : "FAIL");
    }

    static final int T = TilePainter.TILE;

    /** The image a painted tile rasters to at px pixels a plot. */
    static int[] raster(TilePainter.Input in, TilePainter.Painted p, int px) {
        int w = T * px;
        int[] img = new int[w * w];
        TileRaster.raster(in, p, px, img);
        return img;
    }

    /** A tile whose plots are all owned, with no buildings and no sites. */
    static TilePainter.Input owned() {
        TilePainter.Input in = new TilePainter.Input();
        Arrays.fill(in.owned, true);
        return in;
    }

    /** Paints `in` into a fresh Painted. */
    static TilePainter.Painted paint(TilePainter.Input in) {
        TilePainter.Painted p = new TilePainter.Painted();
        TilePainter.paint(in, p);
        return p;
    }

    /** A street code: kind in the low bits, then width and the painter's role. */
    static int streetCode(int kind, int width, int role) {
        return kind | (width << TilePainter.S_WIDTH_SHIFT) | (role << TilePainter.S_ROLE_SHIFT);
    }

    static final int ARTERIAL = streetCode(DistrictPlan.PAVED, 1, TilePainter.S_ARTERIAL);
    static final int BOULEVARD = streetCode(DistrictPlan.PAVED, 1, TilePainter.S_BOULEVARD);
    static final int STREET = streetCode(DistrictPlan.PAVED, 1, 0);

    /** Sets a column of street plots to `code`. */
    static void streetColumn(TilePainter.Input in, int x, int code) {
        for (int y = 0; y < T; y++) in.street[y * T + x] = (byte) code;
    }

    /** Sets a row of street plots to `code`. */
    static void streetRow(TilePainter.Input in, int y, int code) {
        for (int x = 0; x < T; x++) in.street[y * T + x] = (byte) code;
    }

    /** Sets a row of highway plots. */
    static void highwayRow(TilePainter.Input in, int y) {
        for (int x = 0; x < T; x++) in.fixed[y * T + x] = BuildingVisual.HIGHWAY;
    }

    /** Sets a row of highway plots ending at `last` (exclusive). */
    static void highwayRowTo(TilePainter.Input in, int y, int last) {
        for (int x = 0; x < last; x++) in.fixed[y * T + x] = BuildingVisual.HIGHWAY;
    }

    /** Sets a column of highway plots. */
    static void highwayColumn(TilePainter.Input in, int x) {
        for (int y = 0; y < T; y++) in.fixed[y * T + x] = BuildingVisual.HIGHWAY;
    }

    /** Adds a one-plot building (an industry's fill) at (x, y). */
    static void building(TilePainter.Input in, int x, int y) {
        int b = in.buildings;
        in.boxes[2 * b] = x | (y << 5);
        in.boxes[2 * b + 1] = 0;
        in.buildings = b + 1;
    }

    /** One pixel of plot (plotX, plotY), its local (dx, dy) at px pixels a plot. */
    static int pixel(int[] img, int w, int plotX, int plotY, int dx, int dy, int px) {
        return img[(plotY * px + dy) * w + plotX * px + dx];
    }

    /** Whether plot (plotX, plotY) has any pixel of `colour` at px pixels a plot. */
    static boolean hasColour(int[] img, int w, int plotX, int plotY, int px, int colour) {
        for (int dy = 0; dy < px; dy++)
            for (int dx = 0; dx < px; dx++)
                if (pixel(img, w, plotX, plotY, dx, dy, px) == colour) return true;
        return false;
    }

    /** Whether the whole image has none of the given colours. */
    static boolean noneOf(int[] img, int... colours) {
        for (int c : img)
            for (int k : colours)
                if (c == k) return false;
        return true;
    }

    public static void main(String[] args) {
        signals();
        roundabout();
        diamond();
        highwayEnd();
        stack();
        belowJunctionsFrom();
        roundaboutAcrossEdge();
        diamondAcrossEdge();
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails);
    }

    /* ================= 1. signals ================= */
    static void signals() {
        System.out.println("--- 1. an arterial crossing an arterial: a dark box and four zebra arms; a plain + keeps the paved colour ---");
        int px = 8, cx = 10, cy = 10, w = T * px;
        TilePainter.Input in = owned();
        streetColumn(in, cx, ARTERIAL);
        streetRow(in, cy, ARTERIAL);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        int t = Math.max(1, (int) Math.round(TileRaster.SURFACE[1] * px)), o = (px - t) / 2;
        check("the junction plot's surface has JUNCTION_BOX pixels",
                pixel(img, w, cx, cy, o, o, px) == TileRaster.JUNCTION_BOX
                        && pixel(img, w, cx, cy, o + 1, o + 1, px) == TileRaster.JUNCTION_BOX);

        int depth = Math.max(1, (int) Math.round(TileRaster.ZEBRA_DEPTH * px));
        boolean arms = true;
        // North arm: junction south of it, so the zebra is on its south edge.
        arms &= pixel(img, w, cx, cy - 1, o, px - 1, px) == TileRaster.ZEBRA
                && pixel(img, w, cx, cy - 1, o + 1, px - 2, px) != TileRaster.ZEBRA;
        // South arm: junction north of it, so the zebra is on its north edge.
        arms &= pixel(img, w, cx, cy + 1, o, 0, px) == TileRaster.ZEBRA
                && pixel(img, w, cx, cy + 1, o + 1, 1, px) != TileRaster.ZEBRA;
        // West arm: junction east of it, so the zebra is on its east edge.
        arms &= pixel(img, w, cx - 1, cy, px - 1, o, px) == TileRaster.ZEBRA
                && pixel(img, w, cx - 1, cy, px - 2, o + 1, px) != TileRaster.ZEBRA;
        // East arm: junction west of it, so the zebra is on its west edge.
        arms &= pixel(img, w, cx + 1, cy, 0, o, px) == TileRaster.ZEBRA
                && pixel(img, w, cx + 1, cy, 1, o + 1, px) != TileRaster.ZEBRA;
        check("each of the four arms has a ZEBRA stripe at the edge facing the junction, alternating with the surface", arms);

        boolean stops = pixel(img, w, cx, cy - 1, o, px - depth - 1, px) == TileRaster.ZEBRA
                && pixel(img, w, cx, cy + 1, o + t - t / 2, depth, px) == TileRaster.ZEBRA
                && pixel(img, w, cx - 1, cy, px - depth - 1, o + t - t / 2, px) == TileRaster.ZEBRA
                && pixel(img, w, cx + 1, cy, depth, o, px) == TileRaster.ZEBRA;
        check("each arm has a stop line across the right-hand half, just beyond the zebra", stops);

        TilePainter.Input plain = owned();
        streetColumn(plain, cx, STREET);
        streetRow(plain, cy, STREET);
        TilePainter.Painted pp = paint(plain);
        int[] plainImg = raster(plain, pp, px);
        check("a plain street crossing keeps the paved colour, not JUNCTION_BOX",
                pixel(plainImg, w, cx, cy, o, o, px) == TileRaster.ROAD[BuildingVisual.PAVED]);
    }

    /* ================= 2. a roundabout ================= */
    static void roundabout() {
        System.out.println("--- 2. a boulevard crossing a boulevard: a planted middle, a ring on the four arms, none on a diagonal building ---");
        int px = 8, cx = 10, cy = 10, w = T * px;
        TilePainter.Input in = owned();
        streetColumn(in, cx, BOULEVARD);
        streetRow(in, cy, BOULEVARD);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        check("the roundabout's middle has ISLAND pixels",
                pixel(img, w, cx, cy, px / 2 - 1, px / 2 - 1, px) == TileRaster.ISLAND
                        && pixel(img, w, cx, cy, px / 2, px / 2, px) == TileRaster.ISLAND);

        int paved = TileRaster.ROAD[BuildingVisual.PAVED];
        check("the ring has paved pixels on the four neighbouring street plots",
                pixel(img, w, cx, cy - 1, px - 1, px - 1, px) == paved
                        && pixel(img, w, cx, cy + 1, 0, 0, px) == paved
                        && pixel(img, w, cx - 1, cy, px - 1, px - 1, px) == paved
                        && pixel(img, w, cx + 1, cy, 0, 0, px) == paved);

        TilePainter.Input built = owned();
        streetColumn(built, cx, BOULEVARD);
        streetRow(built, cy, BOULEVARD);
        building(built, cx - 1, cy - 1);
        TilePainter.Painted pb = paint(built);
        int[] builtImg = raster(built, pb, px);
        boolean noneOnBuilding = true;
        for (int dy = 0; dy < px; dy++)
            for (int dx = 0; dx < px; dx++)
                noneOnBuilding &= pixel(builtImg, w, cx - 1, cy - 1, dx, dy, px) != paved;
        check("no ring pixel is drawn on a building placed diagonally beside it", noneOnBuilding);
    }

    /* ================= 3. a diamond ================= */
    static void diamond() {
        System.out.println("--- 3. a diamond: paved slips on the verge rows both sides of the street, SLIP_LENGTH out; a building stops one ---");
        int px = 8, hx = 10, hy = 10, w = T * px;
        TilePainter.Input in = owned();
        highwayRow(in, hy);
        in.marks[hy * T + hx] = CityRuns.M_RAMP;
        streetColumn(in, hx, ARTERIAL); // The highway plot itself is beneath the street.
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        int paved = TileRaster.ROAD[BuildingVisual.PAVED];
        int mid = px / 2;
        check("the north verge row has paved slip pixels on both sides of the street, SLIP_LENGTH plots out",
                pixel(img, w, hx - 1, hy - 1, mid, mid - 1, px) == paved
                        && pixel(img, w, hx - 2, hy - 1, mid, mid - 1, px) == paved
                        && pixel(img, w, hx + 1, hy - 1, mid, mid - 1, px) == paved
                        && pixel(img, w, hx + 2, hy - 1, mid, mid - 1, px) == paved);
        check("the south verge row has paved slip pixels on both sides of the street, SLIP_LENGTH plots out",
                pixel(img, w, hx - 1, hy + 1, mid, mid - 1, px) == paved
                        && pixel(img, w, hx - 2, hy + 1, mid, mid - 1, px) == paved
                        && pixel(img, w, hx + 1, hy + 1, mid, mid - 1, px) == paved
                        && pixel(img, w, hx + 2, hy + 1, mid, mid - 1, px) == paved);

        TilePainter.Input stopped = owned();
        highwayRow(stopped, hy);
        stopped.marks[hy * T + hx] = CityRuns.M_RAMP;
        streetColumn(stopped, hx, ARTERIAL);
        building(stopped, hx - 1, hy + 1);
        TilePainter.Painted ps = paint(stopped);
        int[] stoppedImg = raster(stopped, ps, px);
        check("no slip pixel is drawn past a building standing on the verge row",
                !hasColour(stoppedImg, w, hx - 2, hy + 1, px, paved)
                        && !hasColour(stoppedImg, w, hx - 1, hy + 1, px, paved));
        check("the building on the verge row does not stop the other three slips",
                pixel(stoppedImg, w, hx - 1, hy - 1, mid, mid - 1, px) == paved
                        && pixel(stoppedImg, w, hx + 1, hy - 1, mid, mid - 1, px) == paved
                        && pixel(stoppedImg, w, hx + 1, hy + 1, mid, mid - 1, px) == paved);
    }

    /* ================= 4. a highway arm's end ================= */
    static void highwayEnd() {
        System.out.println("--- 4. an arm's last plot with M_RAMP and no street beneath: its raster changes, and the open end narrows ---");
        int px = 8, endX = 10, y = 10, w = T * px;
        TilePainter.Input in = owned();
        highwayRowTo(in, y, endX + 1);
        in.marks[y * T + endX] = CityRuns.M_RAMP;
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        TilePainter.Input plain = owned();
        highwayRowTo(plain, y, endX + 1);
        TilePainter.Painted pp = paint(plain);
        int[] plainImg = raster(plain, pp, px);
        check("the marked plot rasters differently from the same tile without the mark",
                !Arrays.equals(img, plainImg));

        boolean openEndNarrow = true;
        int paved = crossSectionPaved(px);
        int left = (px - paved) / 2, right = left + paved;
        for (int dy = 0; dy < px; dy++) {
            if (dy >= left && dy < right) continue;
            int c = pixel(img, w, endX, y, px - 1, dy, px);
            openEndNarrow &= c != TileRaster.ROAD[BuildingVisual.HIGHWAY] && c != TileRaster.HIGHWAY_CASE;
        }
        check("at the open end the band is no wider than the paved road's width", openEndNarrow);
    }

    static int crossSectionPaved(int px) {
        return TileRaster.crossSection(BuildingVisual.PAVED, px)[0];
    }

    /* ================= 5. a stack ================= */
    static void stack() {
        System.out.println("--- 5. a stack: the row's deck over the column's, and corner slips where the corners are open ---");
        int px = 8, hx = 10, hy = 10, w = T * px;
        TilePainter.Input in = owned();
        highwayRow(in, hy);
        highwayColumn(in, hx);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        boolean casingAcross = false;
        for (int dx = 0; dx < px; dx++) casingAcross |= pixel(img, w, hx, hy, dx, 0, px) == TileRaster.HIGHWAY_CASE;
        check("HIGHWAY_CASE pixels lie across the column's highway on the row's deck", casingAcross);

        int highway = TileRaster.ROAD[BuildingVisual.HIGHWAY];
        check("the four corner plots have slip pixels in the highway's colour",
                hasColour(img, w, hx - 1, hy - 1, px, highway)
                        && hasColour(img, w, hx + 1, hy - 1, px, highway)
                        && hasColour(img, w, hx - 1, hy + 1, px, highway)
                        && hasColour(img, w, hx + 1, hy + 1, px, highway));

        TilePainter.Input built = owned();
        highwayRow(built, hy);
        highwayColumn(built, hx);
        building(built, hx - 1, hy - 1);
        TilePainter.Painted pb = paint(built);
        int[] builtImg = raster(built, pb, px);
        check("no corner slip is drawn in a corner plot holding a building",
                !hasColour(builtImg, w, hx - 1, hy - 1, px, highway));
    }

    /* ================= 6. below JUNCTIONS_FROM ================= */
    static void belowJunctionsFrom() {
        System.out.println("--- 6. at 4 px, below JUNCTIONS_FROM, no junction colour appears ---");
        int px = 4;
        TilePainter.Input in = owned();
        streetColumn(in, 5, ARTERIAL);
        streetRow(in, 5, ARTERIAL);
        streetColumn(in, 15, BOULEVARD);
        streetRow(in, 15, BOULEVARD);
        highwayRow(in, 20);
        in.marks[20 * T + 10] = CityRuns.M_RAMP;
        streetColumn(in, 10, ARTERIAL);
        highwayRowTo(in, 25, 11);
        in.marks[25 * T + 10] = CityRuns.M_RAMP;
        highwayRow(in, 27);
        highwayColumn(in, 27);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);
        check("no JUNCTION_BOX, ZEBRA or ISLAND pixel is drawn at 4 px",
                noneOf(img, TileRaster.JUNCTION_BOX, TileRaster.ZEBRA, TileRaster.ISLAND));
    }

    /* ================= 7. a roundabout across a tile's edge ================= */
    static void roundaboutAcrossEdge() {
        System.out.println("--- 7. the next tile draws its half of a roundabout whose centre is the tile's last column ---");
        int px = 8, centreX = T - 1, centreY = 10, w = T * px;
        TilePainter.Input next = owned();
        streetColumn(next, 0, BOULEVARD);
        // The tile west of this one holds the roundabout's centre on its last column:
        // its boulevard column and row reach this tile's beyond.
        for (int y = 0; y < T; y++) next.beyond[3 * T + y] = (byte) BOULEVARD; // west edge
        next.beyond[3 * T + centreY] = (byte) BOULEVARD;
        TilePainter.Painted p = paint(next);
        int[] img = raster(next, p, px);

        int paved = TileRaster.ROAD[BuildingVisual.PAVED];
        check("the ring's pixels are drawn on this tile's first column",
                pixel(img, w, 0, centreY, 0, px - 1, px) == paved);
    }

    /* ================= 8. a diamond across the tile's edge ================= */
    static void diamondAcrossEdge() {
        System.out.println("--- 8. a diamond whose street is the tile's first row: each tile draws its own side's slips ---");
        int px = 8, hx = 10, w = T * px;
        int paved = TileRaster.ROAD[BuildingVisual.PAVED];
        // The tile south of the street: the highway's column, the arterial on its first row beneath it, the ramp there.
        TilePainter.Input south = owned();
        highwayColumn(south, hx);
        streetRow(south, 0, ARTERIAL);
        south.marks[hx] = CityRuns.M_RAMP;
        int[] southImg = raster(south, paint(south), px);
        check("the tile holding the ramp draws its side's slips, on the verge columns, SLIP_LENGTH plots down",
                hasColour(southImg, w, hx - 1, 1, px, paved) && hasColour(southImg, w, hx + 1, 1, px, paved)
                        && hasColour(southImg, w, hx - 1, (int) TileRaster.SLIP_LENGTH, px, paved)
                        && hasColour(southImg, w, hx + 1, (int) TileRaster.SLIP_LENGTH, px, paved));
        // The tile north of it: the highway's column, and just beyond its south edge the arterial, the highway and the ramp.
        TilePainter.Input north = owned();
        highwayColumn(north, hx);
        for (int x = 0; x < T; x++) north.beyond[2 * T + x] = (byte) ARTERIAL;
        north.fixedBeyond[2 * T + hx] = BuildingVisual.HIGHWAY;
        north.marksBeyond[2 * T + hx] = CityRuns.M_RAMP;
        int[] northImg = raster(north, paint(north), px);
        check("the tile across the edge draws the other side's slips from the ramp beyond its edge",
                hasColour(northImg, w, hx - 1, T - 1, px, paved) && hasColour(northImg, w, hx + 1, T - 1, px, paved)
                        && hasColour(northImg, w, hx - 1, T - (int) TileRaster.SLIP_LENGTH, px, paved)
                        && hasColour(northImg, w, hx + 1, T - (int) TileRaster.SLIP_LENGTH, px, paved));
        TilePainter.Input bare = owned();
        highwayColumn(bare, hx);
        int[] bareImg = raster(bare, paint(bare), px);
        check("...and without the ramp beyond it, none", !hasColour(bareImg, w, hx - 1, T - 1, px, paved) && !hasColour(bareImg, w, hx + 1, T - 1, px, paved));
    }

}
