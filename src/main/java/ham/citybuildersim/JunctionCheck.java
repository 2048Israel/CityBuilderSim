package ham.citybuildersim;

import java.util.ArrayList;
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
 *      ramp it reads beyond its edge;
 *   9. a ramp in the middle of a highway with no street beneath (the plan
 *      laid none on the arterial there) is not an arm's end: at 8 px and at
 *      4 px the tile rasters exactly as the same tile without the mark,
 *      along a row and down a column; and an arm's last plot still narrows
 *      (section 4) - Jerus's eye check of 0.8.2 (FX2).
 *  10. (batch FX3, red until the fix) a 45-degree highway stretch laid as a
 *      staircase rasters as one carriageway along its line: no deck on a step
 *      and no slip beside it, nothing highway-coloured off the carriageway,
 *      every pixel inside it road or centre line, and a centre line on the
 *      steps;
 *  11. (batch FX3, red until the fix) a road two plots wide is one road: two
 *      arterials side by side have a zebra crossing and a junction box only
 *      where a street crosses them, two boulevards side by side a roundabout
 *      only where one crosses them; and three boulevards side by side, as his
 *      city's are across a cell line.
 *  12. (batch FR7, slice 4: FR0-L's D19) THE INTERCHANGES' GROUND: in a city
 *      of highways laid over owned dry ground the spine's hub, where four arms
 *      cross, is a cloverleaf site with a CLOVERLEAF_SIDE reserve centred on
 *      it, and an arm laid to end beside the spine a trumpet site with a
 *      TRUMPET_SIDE one beside the junction on its stub's side; the plan's
 *      ground (fill with yardsOver) holds F_RESERVE on every open plot of each
 *      box and nowhere else, a tile's fill none; each way the rule names - a
 *      plot of salt water, ground not the city's, a work the map has placed, a
 *      yard - leaves the site with no reserve (a stack); a district planned
 *      round the reserves places no building and lays no street on them, and
 *      the same plan without them stands buildings there;
 *  13. a reserve's loops are drawn from 6 px a plot: a cloverleaf's four, each
 *      with its slips, a trumpet's one on its stub's side, in the highway's
 *      colours on the reserve's own open plots only; the stack's corner slips
 *      give way to them; nothing at 4 px;
 *  14. ACCESS ROADS (D17): in a plan with a yard, a sea terminal's box and a
 *      Truck Depot, each is given a road from a gate beside its box to a
 *      highway ramp's street in reach, or - with no highway - to an arterial
 *      that was there; the road is one unbroken street of the access role on
 *      no building, yard, reserve, mine or highway (but beneath it); a frame
 *      with no opened cell gives none; a city with road enough paves it before
 *      any other street, and one short of road makes it gravel and the rest
 *      tracks;
 *  15. KERB CORNERS: a + of two streets has its four corners rounded from 6 px
 *      a plot, a street's T on an arterial likewise, wider in an estate.
 *
 * The ground and the access roads are held on the map's own cities by MapCheck
 * section 8.
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
        rampMidHighway();
        diagonalStretch();
        twoPlotsWide();
        interchangeGround();
        loopsDrawn();
        accessRoads();
        kerbCorners();
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
        streetColumn(in, hx, ARTERIAL); // The street runs through the highway's plot too, beneath it.
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
        // Its boulevard row runs on into this tile (FX3: until 0.8.4 the fixture laid a boulevard down this tile's first
        // column instead, beside the west tile's: a boulevard two plots wide on the seam with no road crossing it, which
        // 0.8.3 drew as a roundabout on every plot - the fault JunctionCheck 11 holds - and so passed here).
        streetRow(next, centreY, BOULEVARD);
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

    /* ================= 9. a ramp mid-highway with no street beneath ================= */
    static void rampMidHighway() {
        System.out.println("--- 9. a ramp mid-highway with no street beneath: drawn as the plain highway, at 8 px and 4 px, along a row and a column ---");
        int mid = 16, line = 10;
        // Along a row: the highway across the whole tile, the ramp on its middle plot, no street beneath it.
        TilePainter.Input rowMarked = owned();
        highwayRow(rowMarked, line);
        rowMarked.marks[line * T + mid] = CityRuns.M_RAMP;
        TilePainter.Input rowPlain = owned();
        highwayRow(rowPlain, line);
        TilePainter.Painted rowMarkedP = paint(rowMarked), rowPlainP = paint(rowPlain);
        check("a mid-highway ramp along a row with no street beneath rasters as the plain highway at 8 px",
                Arrays.equals(raster(rowMarked, rowMarkedP, 8), raster(rowPlain, rowPlainP, 8)));
        check("a mid-highway ramp along a row with no street beneath rasters as the plain highway at 4 px",
                Arrays.equals(raster(rowMarked, rowMarkedP, 4), raster(rowPlain, rowPlainP, 4)));
        // Down a column: the same, the ramp at y = 16.
        TilePainter.Input colMarked = owned();
        highwayColumn(colMarked, line);
        colMarked.marks[mid * T + line] = CityRuns.M_RAMP;
        TilePainter.Input colPlain = owned();
        highwayColumn(colPlain, line);
        TilePainter.Painted colMarkedP = paint(colMarked), colPlainP = paint(colPlain);
        check("a mid-highway ramp down a column with no street beneath rasters as the plain highway at 8 px",
                Arrays.equals(raster(colMarked, colMarkedP, 8), raster(colPlain, colPlainP, 8)));
        check("a mid-highway ramp down a column with no street beneath rasters as the plain highway at 4 px",
                Arrays.equals(raster(colMarked, colMarkedP, 4), raster(colPlain, colPlainP, 4)));
    }

    /* ================= 10. a 45-degree highway stretch ================= */
    static void diagonalStretch() {
        System.out.println("--- 10. a 45-degree highway stretch rasters as one carriageway along its line (FX3) ---");
        // The stretch starts where x + y is odd, as his does: its steps all share that parity (FX3: the centre line went on even plots alone).
        int px = 8, x0 = 8, y0 = 9, steps = 12, w = T * px;
        TilePainter.Input in = owned();
        // Straight highway before the stretch (plot (x0, y0) is its start, step 0, unmarked) and after it (step 12 is (x0 + 12, y0 + 12)).
        for (int x = 4; x <= x0; x++) in.fixed[y0 * T + x] = BuildingVisual.HIGHWAY;
        for (int x = x0 + steps + 1; x <= 27; x++) in.fixed[(y0 + steps) * T + x] = BuildingVisual.HIGHWAY;
        // The stretch south-east, as CityRuns.stretchInto() lays it: step k's plot and its corner, the plot before it up.
        int diag = CityRuns.M_DIAG | (0 << CityRuns.M_DIAG_SHIFT);
        for (int k = 1; k <= steps; k++) {
            int step = (y0 + k) * T + x0 + k, corner = (y0 + k - 1) * T + x0 + k;
            in.fixed[step] = BuildingVisual.HIGHWAY;
            in.marks[step] = (byte) diag;
            in.fixed[corner] = BuildingVisual.HIGHWAY;
            in.marks[corner] = (byte) (diag | CityRuns.M_CORNER);
        }
        int[] img = raster(in, paint(in), px);

        // The line runs through the steps' middles, (x0 + k + 0.5, y0 + k + 0.5) plots; a pixel's distance from it is |(u - cx) - (v - cy)| / sqrt 2.
        double half = TileRaster.crossSection(BuildingVisual.HIGHWAY, px)[0] / 2.0;
        int road = TileRaster.ROAD[BuildingVisual.HIGHWAY];
        double cx2 = (x0 + 2 + 0.5) * px, cy2 = (y0 + 2 + 0.5) * px, span = (11 - 2) * px;
        int off = 0, offFirstX = -1, offFirstY = -1, inside = 0, broken = 0, brokenFirstX = -1, brokenFirstY = -1;
        // The plots of steps 2..11 and their 8 neighbours: plots x0 + 1 .. x0 + 12 on both axes.
        for (int plotY = y0 + 1; plotY <= y0 + steps; plotY++) {
            for (int plotX = x0 + 1; plotX <= x0 + steps; plotX++) {
                for (int dy = 0; dy < px; dy++) {
                    for (int dx = 0; dx < px; dx++) {
                        int X = plotX * px + dx, Y = plotY * px + dy, c = img[Y * w + X];
                        double u = X + 0.5, v = Y + 0.5, d = Math.abs((u - cx2) - (v - cy2)) / Math.sqrt(2), along = ((u - cx2) + (v - cy2)) / 2;
                        if (d > half + 1.5 && (c == TileRaster.HIGHWAY_CASE || c == road)) {
                            off++;
                            if (offFirstX < 0) { offFirstX = X; offFirstY = Y; }
                        }
                        if (d <= half - 1.5 && along >= 0 && along <= span) {
                            inside++;
                            if (c != road && c != TileRaster.CENTRE_LINE) {
                                broken++;
                                if (brokenFirstX < 0) { brokenFirstX = X; brokenFirstY = Y; }
                            }
                        }
                    }
                }
            }
        }
        check("no highway pixel lies off the carriageway: none farther than half + 1.5 px from the line is HIGHWAY_CASE or road", off == 0);
        System.out.printf("  the carriageway is %d px wide (half %.1f); %d pixels farther than %.1f px from the line are highway-coloured%s%n",
                TileRaster.crossSection(BuildingVisual.HIGHWAY, px)[0], half, off, half + 1.5,
                off == 0 ? "" : ", the first at pixel (" + offFirstX + ", " + offFirstY + ")");
        check("one carriageway: every pixel within half - 1.5 px of the line, between steps 2 and 11, is road or centre line", broken == 0 && inside > 0);
        System.out.printf("  %d pixels within %.1f px of the line between steps 2 and 11, %d of them neither road nor centre line%s%n",
                inside, half - 1.5, broken, broken == 0 ? "" : ", the first at pixel (" + brokenFirstX + ", " + brokenFirstY + ")");
        int centred = 0;
        for (int k = 2; k <= 11; k++) if (hasColour(img, w, x0 + k, y0 + k, px, TileRaster.CENTRE_LINE)) centred++;
        check("its centre line: CENTRE_LINE pixels lie on at least 4 of steps 2..11's plots", centred >= 4);
        System.out.println("  CENTRE_LINE on " + centred + " of the 10 step plots");
    }

    /** Whether any plot (x, y) with x in x0..x1 and y in y0..y1 has a pixel of one of the colours. */
    static boolean anyPlotHas(int[] img, int w, int x0, int x1, int y0, int y1, int px, int... colours) {
        for (int y = y0; y <= y1; y++)
            for (int x = x0; x <= x1; x++)
                for (int c : colours) if (hasColour(img, w, x, y, px, c)) return true;
        return false;
    }

    /* ================= 11. a road two plots wide ================= */
    static void twoPlotsWide() {
        System.out.println("--- 11. a road two plots wide is one road: a crossing street gives the junction, nothing else does (FX3) ---");
        int px = 8, w = T * px;
        // Two plans' arterials side by side (a district's edge): columns 10 and 11, crossed by a row.
        TilePainter.Input arterials = owned();
        streetColumn(arterials, 10, ARTERIAL);
        streetColumn(arterials, 11, ARTERIAL);
        streetRow(arterials, 16, ARTERIAL);
        int[] imgA = raster(arterials, paint(arterials), px);
        check("an arterial two plots wide has no ZEBRA or JUNCTION_BOX on its plots away from the crossing street",
                !anyPlotHas(imgA, w, 10, 11, 2, 12, px, TileRaster.ZEBRA, TileRaster.JUNCTION_BOX)
                        && !anyPlotHas(imgA, w, 10, 11, 20, 29, px, TileRaster.ZEBRA, TileRaster.JUNCTION_BOX));
        int boxed = 0, zebras = 0;
        for (int y = 2; y <= 29; y++) {
            if (y > 12 && y < 20) continue;
            for (int x = 10; x <= 11; x++) {
                if (hasColour(imgA, w, x, y, px, TileRaster.JUNCTION_BOX)) boxed++;
                if (hasColour(imgA, w, x, y, px, TileRaster.ZEBRA)) zebras++;
            }
        }
        System.out.println("  of the 42 plots of columns 10 and 11 in rows 2..12 and 20..29, " + boxed + " hold JUNCTION_BOX pixels and " + zebras + " hold ZEBRA pixels");
        check("...the crossing keeps its junction: JUNCTION_BOX pixels on plots (10, 16) and (11, 16)",
                hasColour(imgA, w, 10, 16, px, TileRaster.JUNCTION_BOX) && hasColour(imgA, w, 11, 16, px, TileRaster.JUNCTION_BOX));

        // The same along a boulevard two plots wide: rows 10 and 11, crossed by a column.
        TilePainter.Input boulevards = owned();
        streetRow(boulevards, 10, BOULEVARD);
        streetRow(boulevards, 11, BOULEVARD);
        streetColumn(boulevards, 16, BOULEVARD);
        int[] imgB = raster(boulevards, paint(boulevards), px);
        check("a boulevard two plots wide has no ISLAND on its plots away from the crossing street",
                !anyPlotHas(imgB, w, 2, 12, 10, 11, px, TileRaster.ISLAND)
                        && !anyPlotHas(imgB, w, 20, 29, 10, 11, px, TileRaster.ISLAND));
        int islands = 0;
        for (int y = 10; y <= 11; y++)
            for (int x = 2; x <= 29; x++) {
                if (x > 12 && x < 20) continue;
                if (hasColour(imgB, w, x, y, px, TileRaster.ISLAND)) islands++;
            }
        System.out.println("  of the 42 plots of rows 10 and 11 in columns 2..12 and 20..29, " + islands + " hold ISLAND pixels");
        check("...the crossing keeps its roundabout: ISLAND pixels within the plots (15..17, 9..12)",
                anyPlotHas(imgB, w, 15, 17, 9, 12, px, TileRaster.ISLAND));

        // Three wide, as his city's boulevards are across a cell line: a cell's boulevard two deep beside the next cell's road row.
        TilePainter.Input three = owned();
        streetRow(three, 10, BOULEVARD);
        streetRow(three, 11, BOULEVARD);
        streetRow(three, 12, BOULEVARD);
        streetColumn(three, 16, BOULEVARD);
        int[] imgC = raster(three, paint(three), px);
        check("a boulevard three plots wide has no ISLAND on its plots away from the crossing street",
                !anyPlotHas(imgC, w, 2, 12, 10, 12, px, TileRaster.ISLAND)
                        && !anyPlotHas(imgC, w, 20, 29, 10, 12, px, TileRaster.ISLAND));
        check("...the crossing keeps its roundabout: ISLAND pixels within the plots (15..17, 9..13)",
                anyPlotHas(imgC, w, 15, 17, 9, 13, px, TileRaster.ISLAND));
    }

    /* ================= 12. the interchanges' ground (batch FR7, slice 4) ================= */

    static BuildingVisual.Type[] types;

    /** The game's building types, by id (read once). */
    static BuildingVisual.Type[] types() {
        if (types == null) types = BuildingVisual.table(new BuildingCatalog().load());
        return types;
    }

    /** The founding site the fixture cities lie round: a line crossing of the highways' (16 plots into a cell), so the hub is here. */
    static final long SITE = 112;

    /** Dry owned ground with some plots of another class or none, to make the ground short: -1 (unowned) or a World class at the named plots. */
    static class PatchGround extends MainLineCheck.BoxGround {
        final java.util.Map<Long, Integer> patch = new java.util.HashMap<>();

        PatchGround(long x0, long y0, long x1, long y1) { super(x0, y0, x1, y1); }

        PatchGround with(long x, long y, int cls) {
            patch.put((x << 32) ^ (y & 0xffffffffL), cls);
            return this;
        }

        @Override public int at(long x, long y, int dir) {
            Integer c = patch.get((x << 32) ^ (y & 0xffffffffL));
            return c != null ? c : super.at(x, y, dir);
        }
    }

    /** A fixture city: highways laid to `plots` over the ground g from the founding site, no railway; its gateway none. */
    static CityRuns highwayCity(CityRuns.Ground g, long plots) {
        CityRuns runs = new CityRuns();
        runs.setGateway(CityRuns.NO_GATEWAY);
        runs.layTo(plots, 0, 0, 0, 0, g, SITE, SITE, SITE, SITE, new ArrayList<>());
        return runs;
    }

    /** The interchange site at (x, y) among the runs' sites, or null. */
    static InterchangeGround.Site siteAt(CityRuns runs, long x, long y) {
        for (InterchangeGround.Site s : runs.reserves()) if (s.x() == x && s.y() == y) return s;
        return null;
    }

    /** A highway arm laid by hand: from (fromX, fromY) `steps` plots along heading h (0 east, 2 south, 4 west, 6 north), as step() lays one. */
    static void addArm(CityRuns runs, long fromX, long fromY, int h, int steps) {
        CityRuns.Net n = runs.highways;
        int ci = n.corridors.size();
        n.corridors.add(new long[] { h % 4 == 0 ? 0 : 1, h % 4 == 0 ? fromY : fromX });
        CityRuns.Arm a = new CityRuns.Arm(ci, h, fromX, fromY);
        n.arms.add(a);
        int idx = n.arms.size() - 1;
        for (int k = 0; k < steps; k++) {
            a.push(h);
            a.steps++;
            long nx = a.x + CityRuns.DX[h], ny = a.y + CityRuns.DY[h];
            CityRuns.lay(n, idx, a.segs - 1, nx, ny);
            a.x = nx;
            a.y = ny;
        }
    }

    /** The plots of the runs' fill over the w x h box from (x0, y0), as a plan is given them (yardsOver). */
    static byte[] planGround(CityRuns runs, long x0, long y0, int w, int h) {
        byte[] fixed = new byte[w * h];
        runs.fill(x0, y0, w, h, fixed, null, true);
        return fixed;
    }

    static void interchangeGround() {
        System.out.println("--- 12. (batch FR7, D19) a highway crossing keeps its ground: a cloverleaf's 19 x 19, a trumpet's 11 x 11; short ground leaves a stack ---");
        PatchGround g = new PatchGround(SITE - 150, SITE - 150, SITE + 150, SITE + 150);
        CityRuns runs = highwayCity(g, 500);
        long[] hub = runs.highways().hub();
        int cl = InterchangeGround.CLOVERLEAF_SIDE;
        InterchangeGround.Site hubSite = siteAt(runs, hub[0], hub[1]);
        check("the spine's hub, where four arms cross, is a cloverleaf site", hubSite != null && hubSite.kind() == InterchangeGround.CLOVERLEAF);
        check("...which got its reserve on this ground", hubSite != null && hubSite.got());
        check("...CLOVERLEAF_SIDE plots square, centred on the crossing",
                hubSite != null && hubSite.w() == cl && hubSite.h() == cl && hubSite.x0() == hub[0] - cl / 2 && hubSite.y0() == hub[1] - cl / 2);
        System.out.println("  the hub at (" + hub[0] + ", " + hub[1] + "): " + runs.reserves().size() + " site(s), " + runs.reservesLaid().size() + " laid");

        int fw = 60, fh = 60;
        long fx = hub[0] - fw / 2, fy = hub[1] - fh / 2;
        byte[] fixed = planGround(runs, fx, fy, fw, fh);
        int inBox = 0, wrong = 0, outside = 0;
        for (int j = 0; j < fh; j++) {
            for (int i = 0; i < fw; i++) {
                long px = fx + i, py = fy + j;
                boolean box = hubSite != null && hubSite.holds(px, py);
                byte f = fixed[j * fw + i];
                if (box) {
                    inBox++;
                    boolean highway = CityRuns.covered(runs.highways(), px, py);
                    if (highway ? f != CityRuns.F_HIGHWAY && f != CityRuns.F_RAIL_OVER : f != CityRuns.F_RESERVE) wrong++;
                } else if (f == CityRuns.F_RESERVE) {
                    outside++;
                }
            }
        }
        check("every plot of the box that is not a highway's is F_RESERVE in the plan's ground, and every highway plot stays the highway's", inBox == cl * cl && wrong == 0);
        check("...and no plot outside the box is", outside == 0);
        byte[] seen = new byte[fw * fh];
        runs.fill(fx, fy, fw, fh, seen, null, false);
        boolean any = false;
        for (byte b : seen) any |= b == CityRuns.F_RESERVE;
        check("a tile's fill (yardsOver false) has none: the painter takes the reserve from reserves(), not from the highways' codes", !any);

        // A trumpet: an arm laid by hand to end beside the spine's row, 64 plots east of the hub.
        long jx = hub[0] + 64, jy = hub[1];
        addArm(runs, jx, jy - 40, 2, 39);
        runs.layReserves(g);
        InterchangeGround.Site t = siteAt(runs, jx, jy);
        int tr = InterchangeGround.TRUMPET_SIDE;
        check("an arm that ends beside another highway makes a trumpet site at the junction, its stub to the north",
                t != null && t.kind() == InterchangeGround.TRUMPET && t.stub() == 0);
        check("...which got its reserve, TRUMPET_SIDE plots square, beside the junction on the stub's side with the stub along its middle",
                t != null && t.got() && t.w() == tr && t.h() == tr && t.y0() + t.h() == jy && t.x0() == jx - tr / 2);
        byte[] fixedT = planGround(runs, jx - 20, jy - 30, 41, 40);
        int trBad = 0, trIn = 0;
        for (int j = 0; j < 40; j++) {
            for (int i = 0; i < 41; i++) {
                long px = jx - 20 + i, py = jy - 30 + j;
                byte f = fixedT[j * 41 + i];
                if (t != null && t.holds(px, py)) {
                    trIn++;
                    if (CityRuns.covered(runs.highways(), px, py) ? f != CityRuns.F_HIGHWAY : f != CityRuns.F_RESERVE) trBad++;
                }
            }
        }
        check("...every open plot of its box is F_RESERVE and the stub's own plots stay the highway's", trIn == tr * tr && trBad == 0);

        // Ground short: each way the rule names leaves the crossing a stack, with its site kept and marked.
        PatchGround wet = new PatchGround(SITE - 150, SITE - 150, SITE + 150, SITE + 150).with(SITE + 4, SITE + 4, World.SALT);
        CityRuns wetRuns = highwayCity(wet, 500);
        InterchangeGround.Site wetHub = siteAt(wetRuns, SITE, SITE);
        check("a plot of salt water in the box: the crossing is still a site, but got no reserve", wetHub != null && !wetHub.got());
        check("...and the plan's ground has no F_RESERVE anywhere", noReserve(planGround(wetRuns, SITE - 30, SITE - 30, 60, 60)));
        PatchGround edge = new PatchGround(SITE - 150, SITE - 150, SITE + 150, SITE + 150);
        for (int dy = 5; dy <= 9; dy++) for (int dx = 5; dx <= 9; dx++) edge.with(SITE + dx, SITE + dy, -1);
        InterchangeGround.Site edgeHub = siteAt(highwayCity(edge, 500), SITE, SITE);
        check("a corner of the box not the city's (unowned): a stack", edgeHub != null && !edgeHub.got());
        CityRuns guarded = highwayCity(g, 500);
        guarded.setReserveGuard(guard((x0, y0, x1, y1) -> x0 <= SITE + 3 && x1 >= SITE + 3 && y0 <= SITE - 3 && y1 >= SITE - 3));
        guarded.layReserves(g);
        InterchangeGround.Site guardedHub = siteAt(guarded, SITE, SITE);
        check("a work the map has already placed in the box (a mine on its site): a stack", guardedHub != null && !guardedHub.got());
        guarded.setReserveGuard(guard((x0, y0, x1, y1) -> x0 <= SITE + 100 && x1 >= SITE + 100));
        guarded.layReserves(g);
        guardedHub = siteAt(guarded, SITE, SITE);
        check("...and a work elsewhere does not take it", guardedHub != null && guardedHub.got());
        CityRuns yarded = highwayCity(g, 500);
        yarded.yards.add(new long[] { SITE + 3, SITE + 3, 4, 4 });
        yarded.layReserves(g);
        InterchangeGround.Site yardedHub = siteAt(yarded, SITE, SITE);
        check("a yard in the box: a stack", yardedHub != null && !yardedHub.got());

        // The plan: no lot, no building and no street in the reserve - and without it, buildings stand there.
        planRound(runs);
    }

    /** A box test as the works the map has placed (version 1: the harness changes it by setting another). */
    interface Blocked { boolean in(long x0, long y0, long x1, long y1); }

    static InterchangeGround.Guard guard(Blocked b) {
        return new InterchangeGround.Guard() {
            @Override public boolean blocked(long x0, long y0, long x1, long y1) { return b.in(x0, y0, x1, y1); }

            @Override public long version() { return 1; }
        };
    }

    static boolean noReserve(byte[] fixed) {
        for (byte b : fixed) if (b == CityRuns.F_RESERVE) return false;
        return true;
    }

    /** Plans a district round the fixture city's reserve, and again without it. */
    static void planRound(CityRuns runs) {
        int f = DistrictPlan.FRAME;
        DistrictPlan.Input with = planInput(runs);
        DistrictPlan p = DistrictPlan.make(with);
        int onReserve = 0, streetsOn = 0;
        for (int b = 0; b < p.buildings; b++) {
            for (int y = p.by[b]; y < p.by[b] + p.bh[b]; y++)
                for (int x = p.bx[b]; x < p.bx[b] + p.bw[b]; x++) if (with.fixed[y * f + x] == DistrictPlan.FIXED_RESERVE) onReserve++;
        }
        int reservePlots = 0;
        for (int q = 0; q < f * f; q++) {
            if (with.fixed[q] != DistrictPlan.FIXED_RESERVE) continue;
            reservePlots++;
            if (p.isStreet(q)) streetsOn++;
        }
        System.out.println("  the plan: " + p.buildings + " buildings, " + reservePlots + " reserve plots, " + onReserve + " building plots and " + streetsOn + " street plots on them");
        check("the plan is given every open plot of every reserve laid", reservePlots == reserveCapacity(runs) && reservePlots > 0);
        check("...places no building on a plot of the reserve, and lays no street across it", onReserve == 0 && streetsOn == 0);
        DistrictPlan.Input without = planInput(runs);
        for (int q = 0; q < f * f; q++) if (without.fixed[q] == DistrictPlan.FIXED_RESERVE) without.fixed[q] = 0;
        DistrictPlan bare = DistrictPlan.make(without);
        int would = 0;
        for (int b = 0; b < bare.buildings; b++) {
            for (int y = bare.by[b]; y < bare.by[b] + bare.bh[b]; y++)
                for (int x = bare.bx[b]; x < bare.bx[b] + bare.bw[b]; x++) if (with.fixed[y * f + x] == DistrictPlan.FIXED_RESERVE) would++;
        }
        check("...which the fixture causes: without the reserve the same plan stands buildings on those plots", would > 0);
        System.out.println("  without the reserve, " + would + " building plots would stand on them");
    }

    /** The plots every laid reserve's box holds that are not a highway's: what a plan is given. */
    static int reserveCapacity(CityRuns runs) {
        int n = 0;
        for (InterchangeGround.Site s : runs.reservesLaid()) {
            for (long y = s.y0(); y < s.y0() + s.h(); y++) {
                for (long x = s.x0(); x < s.x0() + s.w(); x++) if (!CityRuns.covered(runs.highways(), x, y)) n++;
            }
        }
        return n;
    }

    /** A plan's input over the district whose frame starts at the world's origin: owned grass, the PlanCheck test district's buildings and road, the hub the founding site, the runs' plots and reserves as a plan is given them. */
    static DistrictPlan.Input planInput(CityRuns runs) {
        DistrictPlan.Input in = new DistrictPlan.Input();
        int f = DistrictPlan.FRAME;
        Arrays.fill(in.terrain, World.GRASS);
        Arrays.fill(in.owned, true);
        in.types = types();
        in.counts = new int[in.types.length];
        for (int i = 0; i < PlanCheck.TEST_TYPES.length; i++) in.counts[PlanCheck.TEST_TYPES[i]] = PlanCheck.TEST_COUNTS[i];
        in.seed = Founding.DEFAULT_WORLD_SEED;
        in.hubX = SITE;
        in.hubY = SITE;
        in.gravel = PlanCheck.TEST_GRAVEL;
        in.paved = PlanCheck.TEST_PAVED;
        runs.fill(0, 0, f, f, in.fixed, null, true);
        for (int q = 0; q < f * f; q++) if (in.fixed[q] == CityRuns.F_RAIL_OVER) in.fixed[q] = DistrictPlan.FIXED_HIGHWAY;
        return in;
    }

    /* ================= 14. access roads (batch FR7, slice 4) ================= */

    /** The yard, sea terminal and depot the access fixtures use: a Rail Terminal's box east of the hub's cell row, a terminal's west of it. */
    static final int[] YARD = { 163, 131, 20, 20 }, PORT = { 40, 100, 12, 10 };

    /** Marks a work's ground in the plan's input, as the map does (FIXED_YARD) and lists it as a work needing a road. */
    static void work(DistrictPlan.Input in, int[] box) {
        for (int j = box[1]; j < box[1] + box[3]; j++) for (int i = box[0]; i < box[0] + box[2]; i++) in.fixed[j * DistrictPlan.FRAME + i] = DistrictPlan.FIXED_YARD;
        in.works.add(box);
    }

    /** The type id of the Truck Depot (the one building of the category HAULAGE). */
    static int depotType() {
        int id = -1;
        for (BuildingVisual.Type t : types()) if (t != null && t.category() == BuildingType.HAULAGE) id = t.id();
        return id;
    }

    /** A plan's input with a yard, a sea terminal and a Truck Depot to place, and (with highways) their ramps given as the map gives them. */
    static DistrictPlan.Input accessInput(CityRuns runs) {
        DistrictPlan.Input in = planInput(runs);
        int f = DistrictPlan.FRAME;
        if (runs.highways().started) {
            in.ramp = new boolean[f * f];
            byte[] fixed = new byte[f * f], marks = new byte[f * f];
            runs.fill(0, 0, f, f, fixed, marks, true);
            for (int q = 0; q < f * f; q++) in.ramp[q] = (marks[q] & CityRuns.M_RAMP) != 0 && fixed[q] == CityRuns.F_HIGHWAY;
        }
        work(in, YARD);
        work(in, PORT);
        in.counts[depotType()] = 1;
        return in;
    }

    /** Whether plot (x, y) is beside the box {bx, by, w, h}, across one of its sides. */
    static boolean beside(int[] box, int x, int y) {
        boolean ew = (x == box[0] - 1 || x == box[0] + box[2]) && y >= box[1] && y < box[1] + box[3];
        boolean ns = (y == box[1] - 1 || y == box[1] + box[3]) && x >= box[0] && x < box[0] + box[2];
        return ew || ns;
    }

    /** Whether plot q of the plan is one an access road runs over. */
    static boolean access(DistrictPlan p, int q) { return p.accessAt != null && p.accessAt[q]; }

    /** The plots the access roads run over that are streets with a surface to pay (not the ramp's, beneath a highway). */
    static int accessPlots(DistrictPlan p) {
        int n = 0;
        for (int q = 0; q < DistrictPlan.AREA; q++) if (access(p, q) && p.kindAt(q) != DistrictPlan.UNDER) n++;
        return n;
    }

    /** Whether the access road runs unbroken from (gx, gy) to (ex, ey): a chain of plots of the access role, side to side. */
    static boolean chained(DistrictPlan p, int gx, int gy, int ex, int ey) {
        int f = DistrictPlan.FRAME;
        boolean[] seen = new boolean[f * f];
        java.util.ArrayDeque<Integer> q = new java.util.ArrayDeque<>();
        if (!access(p, gy * f + gx)) return false;
        q.add(gy * f + gx);
        seen[gy * f + gx] = true;
        while (!q.isEmpty()) {
            int a = q.poll(), x = a % f, y = a / f;
            if (x == ex && y == ey) return true;
            int[] nb = { x > 0 ? a - 1 : -1, x + 1 < f ? a + 1 : -1, y > 0 ? a - f : -1, y + 1 < f ? a + f : -1 };
            for (int b : nb) if (b >= 0 && !seen[b] && access(p, b)) { seen[b] = true; q.add(b); }
        }
        return false;
    }

    static void accessRoads() {
        System.out.println("--- 14. (batch FR7, D17) a sea terminal, a yard and a Truck Depot each get an access road, to a highway ramp in reach, else to the arterial grid; paved first ---");
        int f = DistrictPlan.FRAME, side = DistrictPlan.SIDE;
        PatchGround g = new PatchGround(SITE - 150, SITE - 150, SITE + 150, SITE + 150);
        CityRuns runs = highwayCity(g, 500);
        DistrictPlan.Input in = accessInput(runs);
        DistrictPlan p = DistrictPlan.make(in);
        int depot = depotType();
        int[] depotBox = null;
        for (int b = 0; b < p.buildings; b++) if (p.btype[b] == depot) depotBox = new int[] { p.bx[b], p.by[b], p.bw[b], p.bh[b] };
        check("the fixture places its Truck Depot", depotBox != null);
        check("each of the three works - the yard, the terminal and the depot - is given an access road", p.accessRoads.size() == 3 && depotBox != null);
        boolean gates = true, ramps = true, chain = true, reach = true;
        for (int[] r : p.accessRoads) {
            int[] box = { r[0], r[1], r[2], r[3] };
            gates &= beside(box, r[4], r[5]);
            ramps &= r[8] == DistrictPlan.ACCESS_RAMP && in.ramp[r[7] * f + r[6]] && p.kindAt(r[7] * f + r[6]) == DistrictPlan.UNDER;
            reach &= Math.max(Math.abs(r[6] - (r[0] + r[2] / 2)), Math.abs(r[7] - (r[1] + r[3] / 2))) <= DistrictPlan.ACCESS_RAMP_DISTRICTS * side;
            chain &= chained(p, r[4], r[5], r[6], r[7]);
            System.out.println("  work " + Arrays.toString(box) + ": gate (" + r[4] + ", " + r[5] + "), ends " + (r[8] == DistrictPlan.ACCESS_RAMP ? "on a ramp" : "on an arterial") + " at (" + r[6] + ", " + r[7] + ")");
        }
        check("every road starts at its work's gate: its first plot is beside the work's box", gates && p.accessRoads.size() == 3);
        check("with a highway ramp in reach every road ends on it: a street beneath a highway plot that carries a ramp", ramps);
        check("...within ACCESS_RAMP_DISTRICTS districts of its work", reach);
        check("...and is one unbroken street of the access role from gate to ramp", chain);
        boolean clear = true;
        for (int q = 0; q < f * f; q++) {
            if (!access(p, q)) continue;
            int x = q % f, y = q / f;
            clear &= x >= 1 && y >= 1 && x < side && y < side && !DistrictPlan.works(in.fixed[q]) && in.site[q] != DistrictPlan.SITE_MINED
                    && (in.fixed[q] != DistrictPlan.FIXED_HIGHWAY || p.kindAt(q) == DistrictPlan.UNDER);
        }
        for (int b = 0; b < p.buildings; b++) {
            for (int y = p.by[b]; y < p.by[b] + p.bh[b]; y++) for (int x = p.bx[b]; x < p.bx[b] + p.bw[b]; x++) clear &= !access(p, y * f + x);
        }
        check("no road takes a plot of a building, a yard, a reserve or a mine, or the frame's edge, or crosses a highway but beneath it", clear);

        // The same city with no highways: every road ends on an arterial that was there.
        DistrictPlan.Input inA = accessInput(new CityRuns());
        DistrictPlan pa = DistrictPlan.make(inA);
        boolean arterial = pa.accessRoads.size() == 3, chainA = true;
        for (int[] r : pa.accessRoads) {
            arterial &= r[8] == DistrictPlan.ACCESS_ARTERIAL && (r[9] == DistrictPlan.ROLE_ARTERIAL || r[9] == DistrictPlan.ROLE_BOULEVARD);
            chainA &= chained(pa, r[4], r[5], r[6], r[7]);
        }
        check("with no highway, every road ends on an arterial of the grid (a cell's ring) that was there", arterial);
        check("...and is one unbroken street of the access role from gate to arterial", chainA);

        // No grid: nothing opened round the yard, so there is no street to go to.
        DistrictPlan.Input empty = accessInput(new CityRuns());
        Arrays.fill(empty.counts, 0);
        DistrictPlan pe = DistrictPlan.make(empty);
        check("a frame whose cells have not opened has no arterial to go to: the works get no road", pe.cellsOpen == 0 && pe.accessRoads.isEmpty() && pe.accessAt == null);

        // Surface: paved first.
        DistrictPlan.Input tight = accessInput(runs);
        tight.paved = 5;
        tight.gravel = 0;
        int a = accessPlots(DistrictPlan.make(tight));
        DistrictPlan.Input paved = accessInput(runs);
        paved.paved = 0.5 * (a + 30);
        paved.gravel = 0;
        DistrictPlan pp = DistrictPlan.make(paved);
        boolean allPaved = true, otherTrack = false;
        for (int q = 0; q < f * f; q++) {
            boolean acc = access(pp, q);
            if (acc && pp.kindAt(q) != DistrictPlan.PAVED && pp.kindAt(q) != DistrictPlan.UNDER) allPaved = false;
            if (!acc && pp.kindAt(q) == DistrictPlan.TRACK) otherTrack = true;
        }
        System.out.println("  " + a + " plots of access road; a budget of " + paved.paved + " paved");
        check("a city with road for the access roads and a little over: every plot of every road is paved, before any other street", a > 0 && accessPlots(pp) == a && allPaved);
        check("...and the road is short, so some other street is a track", otherTrack);
        DistrictPlan.Input gravel = accessInput(runs);
        gravel.paved = 0;
        gravel.gravel = 0.5 * a;
        DistrictPlan pg = DistrictPlan.make(gravel);
        boolean allGravel = true, restTrack = true;
        for (int q = 0; q < f * f; q++) {
            if (pg.kindAt(q) == DistrictPlan.NONE || pg.kindAt(q) == DistrictPlan.UNDER || pg.kindAt(q) == DistrictPlan.SEAM) continue;
            if (access(pg, q)) allGravel &= pg.kindAt(q) == DistrictPlan.GRAVEL;
            else restTrack &= pg.kindAt(q) == DistrictPlan.TRACK;
        }
        check("a city short of road gets a gravel access road: with gravel for exactly the roads, each is gravel and every other street is a track", accessPlots(pg) == a && allGravel && restTrack);
    }

    /* ================= 15. kerb corners and estate T's (batch FR7, slice 4) ================= */

    /** A one-plot building of the type at (x, y). */
    static void buildingOf(TilePainter.Input in, int x, int y, int type) {
        building(in, x, y);
        in.boxes[2 * (in.buildings - 1) + 1] = type;
    }

    /** The id of a type of the given class. */
    static int typeOfClass(int cls) {
        for (BuildingVisual.Type t : types()) if (t != null && t.cls() == cls && t.drawn()) return t.id();
        return -1;
    }

    static void kerbCorners() {
        System.out.println("--- 15. (batch FR7, spec 4.6) a + of streets has kerb corners; a street's T on an arterial has them, wide in an estate; from 6 px a plot ---");
        int px = 8, jx = 10, jy = 10, w = T * px;
        int paved = TileRaster.ROAD[BuildingVisual.PAVED], verge = TileRaster.VERGE;
        // A + of two streets.
        TilePainter.Input plus = owned();
        streetColumn(plus, jx, STREET);
        streetRow(plus, jy, STREET);
        int[] img = raster(plus, paint(plus), px);
        // The inner corners are (o, o), (o + t, o), (o, o + t) and (o + t, o + t) of the junction plot; the surface's half width is t = 4, o = 2 px.
        int t = Math.max(1, (int) Math.round(TileRaster.SURFACE[1] * px)), o = (px - t) / 2;
        int r = Math.max(1, (int) Math.round(TileRaster.KERB_RADIUS * px));
        boolean rounded = true;
        for (int sy = -1; sy <= 1; sy += 2) {
            for (int sx = -1; sx <= 1; sx += 2) {
                // The pixel of the corner's square farthest from the circle's middle: diagonal to the corner point, one pixel out.
                int cx = sx < 0 ? o : o + t, cy = sy < 0 ? o : o + t;
                int fx = sx < 0 ? cx - 1 : cx, fy = sy < 0 ? cy - 1 : cy;
                rounded &= pixel(img, w, jx, jy, fx, fy, px) == paved;
            }
        }
        check("a + of two streets: each of its four inner corners is rounded - the surface's colour on the pixel the circle leaves out", rounded && r >= 1);
        TilePainter.Input straight = owned();
        streetRow(straight, jy, STREET);
        int[] straightImg = raster(straight, paint(straight), px);
        check("...and a street with no street across it is not: the same pixel is the verge", pixel(straightImg, w, jx, jy, o - 1, o - 1, px) == verge);
        int px4 = 4;
        int[] img4 = raster(plus, paint(plus), px4);
        check("at 4 px a plot a + has no kerb corner: the corner pixel is the verge", pixel(img4, T * px4, jx, jy, 0, 0, px4) == verge);

        // A street's T on an arterial: in a homes cell the kerb radius, in an estate the wide one.
        TilePainter.Input homes = owned();
        homes.types = types();
        streetRow(homes, jy, ARTERIAL);
        for (int y = 0; y < jy; y++) homes.street[y * T + jx] = (byte) STREET;
        int[] homesImg = raster(homes, paint(homes), px);
        TilePainter.Input estate = owned();
        estate.types = types();
        streetRow(estate, jy, ARTERIAL);
        for (int y = 0; y < jy; y++) estate.street[y * T + jx] = (byte) STREET;
        buildingOf(estate, jx + 2, jy - 2, typeOfClass(BuildingVisual.INDUSTRY));
        int[] estateImg = raster(estate, paint(estate), px);
        // The stem runs north; its bar's west edge is at o and the arterial's north edge at o: the corner point (o, o), out to the north-west.
        check("a street's T on an arterial in a homes cell is rounded by the kerb radius: the corner pixel is the surface, the pixel beside it the verge",
                pixel(homesImg, w, jx, jy, o - 1, o - 1, px) == paved && pixel(homesImg, w, jx, jy, o - 2, o - 1, px) == verge);
        check("...in an estate (an industry's building beside it) the radius is wide: the pixels the wider circle leaves out are the surface too",
                pixel(estateImg, w, jx, jy, o - 1, o - 1, px) == paved && pixel(estateImg, w, jx, jy, o - 2, o - 1, px) == paved
                        && pixel(estateImg, w, jx, jy, o - 1, o - 2, px) == paved);
        int homesPaved = 0, estatePaved = 0;
        for (int y = jy - 1; y <= jy + 1; y++) {
            for (int x = jx - 1; x <= jx + 1; x++) {
                for (int dy = 0; dy < px; dy++) {
                    for (int dx = 0; dx < px; dx++) {
                        if (pixel(homesImg, w, x, y, dx, dy, px) == paved) homesPaved++;
                        if (pixel(estateImg, w, x, y, dx, dy, px) == paved) estatePaved++;
                    }
                }
            }
        }
        System.out.println("  surface-coloured pixels in the 3 x 3 plots about the T: " + homesPaved + " in a homes cell, " + estatePaved + " in an estate");
        check("...the wide corners put more surface on the verge than the kerb ones do", TileRaster.ESTATE_T_RADIUS > TileRaster.KERB_RADIUS && estatePaved > homesPaved);
        // The east corner of the T is rounded the same: the corner point (o + t, o), out to the north-east.
        check("...and the T's other corner is rounded as its first: the pixel diagonal to the corner point, to the north-east",
                pixel(homesImg, w, jx, jy, o + t, o - 1, px) == paved && pixel(estateImg, w, jx, jy, o + t, o - 1, px) == paved);
        // A street ending on a street is no T of this kind.
        TilePainter.Input onStreet = owned();
        streetRow(onStreet, jy, STREET);
        for (int y = 0; y < jy; y++) onStreet.street[y * T + jx] = (byte) STREET;
        int[] onStreetImg = raster(onStreet, paint(onStreet), px);
        check("a T of streets with no arterial has no rounded corner here: the pixel is the verge", pixel(onStreetImg, w, jx, jy, o - 1, o - 1, px) == verge);
    }

    /* ================= 13. the loops (batch FR7, slice 4) ================= */

    /** The pixel of the tile at plot (junction x + u, junction y + v) in plots from the junction plot's middle, at px pixels a plot. */
    static int atOffset(int[] img, int w, int jx, int jy, double u, double v, int px) {
        int X = (int) Math.floor((jx + 0.5 + u) * px), Y = (int) Math.floor((jy + 0.5 + v) * px);
        return img[Y * w + X];
    }

    static void loopsDrawn() {
        System.out.println("--- 13. (batch FR7, D19) a reserve's loops: four on a cloverleaf, one on a trumpet, in the highway's colours, on the reserve's own plots, from 6 px a plot ---");
        int px = 8, jx = 16, jy = 16, w = T * px, side = InterchangeGround.CLOVERLEAF_SIDE;
        int road = TileRaster.ROAD[BuildingVisual.HIGHWAY];
        TilePainter.Input in = owned();
        highwayRow(in, jy);
        highwayColumn(in, jx);
        in.addReserve(InterchangeGround.CLOVERLEAF, -1, jx, jy, jx - side / 2, jy - side / 2, side, side);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);
        TilePainter.Input bare = owned();
        highwayRow(bare, jy);
        highwayColumn(bare, jx);
        int[] bareImg = raster(bare, paint(bare), px);

        boolean four = true;
        for (int sy = -1; sy <= 1; sy += 2) {
            for (int sx = -1; sx <= 1; sx += 2) {
                // The ring's farthest point from the crossing along each axis, and its nearest: on the road.
                double cx = sx * TileRaster.LOOP_AT, cy = sy * TileRaster.LOOP_AT, r = TileRaster.LOOP_RADIUS;
                four &= atOffset(img, w, jx, jy, cx + sx * r, cy, px) == road && atOffset(img, w, jx, jy, cx, cy + sy * r, px) == road
                        && atOffset(img, w, jx, jy, cx - sx * r, cy, px) == road;
                // ...and its middle is the ground between, not the road.
                four &= atOffset(img, w, jx, jy, cx, cy, px) != road;
            }
        }
        check("each of the four quadrants has a loop: highway-coloured pixels on its ring, none at its middle", four);
        check("...and the same tile without the reserve has none of them", atOffset(bareImg, w, jx, jy, 5 + TileRaster.LOOP_RADIUS, 5, px) != road);
        // A slip joins each loop to a highway: pixels between the highway's edge and the ring.
        double sx0 = TileRaster.SLIP_OUT - 1.0;
        check("a slip leaves the east highway toward the south-east loop: highway-coloured pixels on the open plot between them",
                hasColour(img, w, (int) Math.floor(jx + 0.5 + sx0), jy + 1, px, road));
        // Outside the box nothing is drawn.
        boolean outside = true;
        for (int y = 0; y < T; y++) {
            for (int x = 0; x < T; x++) {
                if (x >= jx - side / 2 && x < jx - side / 2 + side && y >= jy - side / 2 && y < jy - side / 2 + side) continue;
                for (int dy = 0; dy < px; dy++) for (int dx = 0; dx < px; dx++)
                    outside &= pixel(img, w, x, y, dx, dy, px) == pixel(bareImg, w, x, y, dx, dy, px);
            }
        }
        check("no pixel outside the reserve's box differs from the tile without it", outside);
        // The stack's corner slips give way to the loops: the plots diagonal to the crossing hold no highway colour.
        check("the stack's four corner slips are not drawn where a cloverleaf's loops are",
                !hasColour(img, w, jx - 1, jy - 1, px, road) && !hasColour(img, w, jx + 1, jy + 1, px, road)
                        && hasColour(bareImg, w, jx - 1, jy - 1, px, road));
        // A building on a plot of the reserve: no loop pixel there.
        TilePainter.Input built = owned();
        highwayRow(built, jy);
        highwayColumn(built, jx);
        built.addReserve(InterchangeGround.CLOVERLEAF, -1, jx, jy, jx - side / 2, jy - side / 2, side, side);
        int bx = jx + (int) Math.floor(TileRaster.LOOP_AT + TileRaster.LOOP_RADIUS), by = jy + (int) Math.floor(TileRaster.LOOP_AT);
        building(built, bx, by);
        int[] builtImg = raster(built, paint(built), px);
        check("a building on a plot of the ring: no loop pixel on its plot", !hasColour(builtImg, w, bx, by, px, road));
        // Below JUNCTIONS_FROM nothing of it is drawn.
        int[] img4 = raster(in, p, 4);
        int[] bare4 = raster(bare, paint(bare), 4);
        check("at 4 px a plot the reserve changes nothing", Arrays.equals(img4, bare4));

        // A trumpet: one loop, on the stub's side.
        TilePainter.Input t = owned();
        highwayRow(t, jy);
        for (int y = 0; y < jy; y++) t.fixed[y * T + jx] = BuildingVisual.HIGHWAY;
        int tr = InterchangeGround.TRUMPET_SIDE;
        t.addReserve(InterchangeGround.TRUMPET, 0, jx, jy, jx - tr / 2, jy - tr, tr, tr);
        int[] tImg = raster(t, paint(t), px);
        double ac = TileRaster.TRUMPET_OUT, bc = TileRaster.TRUMPET_ACROSS, rr = TileRaster.TRUMPET_RADIUS;
        // The stub runs north, so a is -v, and b (to its right) is +u.
        check("a trumpet has its loop on the stub's side: highway-coloured pixels on the ring, none at its middle",
                atOffset(tImg, w, jx, jy, bc + rr, -ac, px) == road && atOffset(tImg, w, jx, jy, bc - rr, -ac, px) == road && atOffset(tImg, w, jx, jy, bc, -ac, px) != road);
        check("...and none on the other side of the stub", atOffset(tImg, w, jx, jy, -(bc + rr), -ac, px) != road);
    }

}
