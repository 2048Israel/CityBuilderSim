package ham.citybuildersim;

import java.util.Arrays;

/**
 * Proves that the railway is drawn as rail, not as a cased road band (0.8.1,
 * batch FR0; docs/studies/spec-freight-and-infrastructure.md 4.3, D15, D18).
 * Not part of the game.
 *
 * WHY THIS EXISTS. Until 0.8.1 TileRaster.track() drew the railway as a paved
 * road's band, cased grey with white dashes, so it read as a road. This draws
 * it as ballast, sleepers and steel rails at the close views, a dark line
 * with cross-ticks in the blocks, and the crossings as level crossings or a
 * road bridge over the main line.
 *
 * What it has to prove, a section each, every one a fixture that sets the
 * condition by hand:
 *
 *   1. a straight east-west single track at 8 px has a BALLAST bed, two STEEL
 *      rows at the offsets the dials give, and SLEEPER columns every
 *      max(2, round(SLEEPERS_EVERY * px));
 *   2. the same plots marked M_MAIN | M_DOUBLE have four STEEL rows and a bed
 *      as wide as BED_DOUBLE; M_MAIN alone stays single track;
 *   3. a north-south track is the same, by columns;
 *   4. at 4 px the track is a BALLAST bed, one STEEL row down its middle and
 *      one SLEEPER tick a plot;
 *   5. L1 blocks draw the track in RAIL_DARK, never RAIL_LINE, with tick
 *      pixels beside the line at every block and none between;
 *   6. L2 blocks draw RAIL_DARK and no tick pixels beside the line;
 *   7. a paved street crossing a single track at level has CROSSBUCK pixels
 *      in the corners, BARRIER pixels on the street's surface at its edges,
 *      and STEEL pixels on the surface;
 *   8. an arterial crossing a main-line plot is a bridge: DECK along the
 *      street's edges, no CROSSBUCK, BARRIER or STEEL on the surface, and
 *      LandMap.plotWords says "bridging the railway" there and "crossing the
 *      railway" at a level crossing;
 *   9. an unowned track's bed pixel is blend(BALLAST, VOID, UNOWNED_DIM);
 *  10. a diagonal plot has BALLAST and STEEL pixels, and no SLEEPER;
 *  11. the eleventh class: RAIL, PORTS and INFRASTRUCTURE are TRANSPORT,
 *      CLASSES and the class tables all have eleven entries, and a Rail
 *      Terminal has TRANSPORT's fill;
 *  12. a bulk yard at 8 px over an east-west track has YARD_GROUND, STEEL on
 *      at least YARD_TRACKS rows, its shed on one side of the line and its
 *      stockpiles on the other, and no containers;
 *  13. the freight station with goods is a container yard; the same yard not
 *      the station, and the station without goods, stay bulk;
 *  14. wagonTracks 0 draws no WAGON, YARD_TRACKS draws a wagon on every
 *      track's bed, and half of YARD_TRACKS on that many tracks only;
 *  15. a north-south line through the yard gives a north-south fan;
 *  16. at 4 px the yard still draws as a yard; below LINES_FROM it is a plain
 *      TRANSPORT box;
 *  17. MapTiles.stamp changes when stationX, stationY, goodsByRail or
 *      wagonTracks changes, and not otherwise;
 *  18. CityMap.railPicture repaints when goodsByRail or the whole wagon
 *      tracks change, and not when the share moves less than a track's worth.
 *
 * Numbering is the running order.
 *
 * @author Jerus
 */
public class RailDrawCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-70s %s%n", label, ok ? "OK" : "FAIL");
    }

    static final int T = TilePainter.TILE;

    /** The game's building templates, as the map draws them. */
    static final BuildingVisual.Type[] TYPES = BuildingVisual.table(new BuildingCatalog().load());

    /** The Rail Terminal type. */
    static BuildingVisual.Type terminal() {
        for (BuildingVisual.Type t : TYPES) if (t != null && t.terminal()) return t;
        throw new IllegalStateException("no Rail Terminal type");
    }

    /** The image a painted tile rasters to at px pixels a plot. */
    static int[] raster(TilePainter.Input in, TilePainter.Painted p, int px) {
        int w = T * px;
        int[] img = new int[w * w];
        TileRaster.raster(in, p, px, img);
        return img;
    }

    /** The image a painted tile's blocks raster to at px pixels a plot. */
    static int[] blocks(TilePainter.Input in, TilePainter.Painted p, int px, int block) {
        int w = T * px;
        int[] img = new int[w * w];
        TileRaster.blocks(in, p, px, block, false, img);
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

    /** Lays a row of track, each plot with `marks` (0 for none). */
    static void railRow(TilePainter.Input in, int y, int marks) {
        for (int x = 0; x < T; x++) {
            in.fixed[y * T + x] = TilePainter.RAIL;
            if (marks != 0) in.marks[y * T + x] = (byte) marks;
        }
    }

    /** Lays a column of track, each plot with `marks` (0 for none). */
    static void railColumn(TilePainter.Input in, int x, int marks) {
        for (int y = 0; y < T; y++) {
            in.fixed[y * T + x] = TilePainter.RAIL;
            if (marks != 0) in.marks[y * T + x] = (byte) marks;
        }
    }

    /** Sets a column of street plots to `code`. */
    static void streetColumn(TilePainter.Input in, int x, int code) {
        for (int y = 0; y < T; y++) in.street[y * T + x] = (byte) code;
    }

    /** One pixel of plot (plotX, plotY), its local (dx, dy) at px pixels a plot. */
    static int pixel(int[] img, int w, int plotX, int plotY, int dx, int dy, int px) {
        return img[(plotY * px + dy) * w + plotX * px + dx];
    }

    /** The bed width the dials give at px pixels a plot. */
    static int bedWidth(int px, boolean doubled) {
        return Math.min(px, Math.max(1, (int) Math.round((doubled ? TileRaster.BED_DOUBLE : TileRaster.BED) * px)));
    }

    /** The rows of a plot that are wholly `colour`, in dy order. */
    static boolean[] rowsAll(int[] img, int w, int plotX, int plotY, int px, int colour) {
        boolean[] all = new boolean[px];
        for (int dy = 0; dy < px; dy++) {
            boolean row = true;
            for (int dx = 0; dx < px; dx++) row &= pixel(img, w, plotX, plotY, dx, dy, px) == colour;
            all[dy] = row;
        }
        return all;
    }

    /** The columns of a plot that are wholly `colour`, in dx order. */
    static boolean[] colsAll(int[] img, int w, int plotX, int plotY, int px, int colour) {
        boolean[] all = new boolean[px];
        for (int dx = 0; dx < px; dx++) {
            boolean col = true;
            for (int dy = 0; dy < px; dy++) col &= pixel(img, w, plotX, plotY, dx, dy, px) == colour;
            all[dx] = col;
        }
        return all;
    }

    static int count(boolean[] a) {
        int n = 0;
        for (boolean b : a) if (b) n++;
        return n;
    }

    /** A hand-built yard tile: the Rail Terminal's box, on owned ground. */
    static TilePainter.Input yardAt(BuildingVisual.Type terminal, int boxX, int boxY, int boxW, int boxH) {
        TilePainter.Input in = owned();
        in.types = new BuildingVisual.Type[terminal.id() + 1];
        in.types[terminal.id()] = terminal;
        in.counts = new int[terminal.id() + 1];
        in.addBuilding(CityMap.box(boxX, boxY, boxW, boxH, false), terminal.id());
        return in;
    }

    /** Whether any pixel in the box is `colour`. */
    static boolean hasInBox(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour) {
        for (int y = boxY * px; y < (boxY + boxH) * px; y++) {
            for (int x = boxX * px; x < (boxX + boxW) * px; x++) if (img[y * w + x] == colour) return true;
        }
        return false;
    }

    /** Whether any pixel in the box on the chosen side of `line` is `colour`. */
    static boolean hasOnSide(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour, int line,
                             boolean above) {
        for (int y = boxY * px; y < (boxY + boxH) * px; y++) {
            if (above ? y >= line : y < line) continue;
            for (int x = boxX * px; x < (boxX + boxW) * px; x++) if (img[y * w + x] == colour) return true;
        }
        return false;
    }

    /** How many distinct pixel rows in the box have a pixel of `colour`. */
    static int distinctRows(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour) {
        int n = 0;
        for (int y = boxY * px; y < (boxY + boxH) * px; y++) {
            for (int x = boxX * px; x < (boxX + boxW) * px; x++) {
                if (img[y * w + x] == colour) { n++; break; }
            }
        }
        return n;
    }

    /** How many distinct pixel columns in the box have a pixel of `colour`. */
    static int distinctCols(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour) {
        int n = 0;
        for (int x = boxX * px; x < (boxX + boxW) * px; x++) {
            for (int y = boxY * px; y < (boxY + boxH) * px; y++) {
                if (img[y * w + x] == colour) { n++; break; }
            }
        }
        return n;
    }

    /** The box's pixel rows, relative to its top, that have a pixel of `colour`. */
    static boolean[] rowsWith(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour) {
        int inset = px >= TileRaster.INSET_FROM ? 1 : 0;
        boolean[] rows = new boolean[boxH * px];
        for (int dy = inset; dy < rows.length - inset; dy++) {
            int y = boxY * px + dy;
            for (int x = boxX * px + inset; x < (boxX + boxW) * px - inset; x++) {
                if (img[y * w + x] == colour) { rows[dy] = true; break; }
            }
        }
        return rows;
    }

    /** A fan's beds from its BALLAST rows: the two ballast rows of each track are one rail apart, the tracks a gap apart. */
    static int[] bedBands(boolean[] rows) {
        int n = 0, i = 0;
        while (i < rows.length) {
            if (!rows[i]) { i++; continue; }
            n++;
            while (i + 2 < rows.length && !rows[i + 1] && rows[i + 2]) i += 2;
            i++;
        }
        int[] out = new int[2 * n];
        int k = 0, j = 0;
        while (j < rows.length) {
            if (!rows[j]) { j++; continue; }
            out[2 * k] = j;
            while (j + 2 < rows.length && !rows[j + 1] && rows[j + 2]) j += 2;
            out[2 * k + 1] = j;
            k++;
            j++;
        }
        return out;
    }

    /** Whether any pixel in rows start..end (relative to the box's top) is `colour`. */
    static boolean bandHas(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour, int start,
                           int end) {
        int inset = px >= TileRaster.INSET_FROM ? 1 : 0;
        for (int dy = Math.max(start, inset); dy <= Math.min(end, boxH * px - 1 - inset); dy++) {
            int y = boxY * px + dy;
            for (int x = boxX * px + inset; x < (boxX + boxW) * px - inset; x++) if (img[y * w + x] == colour) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        straightAtEight();
        doubleMainAtEight();
        northSouthAtEight();
        atFour();
        blocksMiddle();
        blocksFar();
        levelCrossing();
        arterialBridge();
        unownedTrack();
        diagonalTrack();
        eleventhClass();
        bulkYardAtEight();
        freightStation();
        wagonsStanding();
        northSouthYard();
        atFourAndBelow();
        stampFoldsTheYard();
        railPictureRepaint();
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails);
    }

    /* ================= 1. a straight east-west single track at 8 px ================= */
    static void straightAtEight() {
        System.out.println("--- 1. a straight east-west single track at 8 px: ballast, two rails, sleepers every quarter plot ---");
        int px = 8, plotX = 5, plotY = 10, w = T * px;
        TilePainter.Input in = owned();
        railRow(in, plotY, 0);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        int bed = bedWidth(px, false), a = (px - bed) / 2;
        int mid = px / 2, off = Math.max(1, (int) Math.round(TileRaster.GAUGE / 2.0 * px));
        boolean[] steel = rowsAll(img, w, plotX, plotY, px, TileRaster.STEEL);
        check("exactly two pixel rows are STEEL, at middle +/- round(GAUGE/2*px)", count(steel) == 2 && steel[mid - off] && steel[mid + off]);
        check("a bed pixel is BALLAST", pixel(img, w, plotX, plotY, 1, a, px) == TileRaster.BALLAST);

        int step = Math.max(2, (int) Math.round(TileRaster.SLEEPERS_EVERY * px));
        boolean sleepers = true;
        for (int sx = 0; sx < px; sx += step) sleepers &= pixel(img, w, plotX, plotY, sx, a, px) == TileRaster.SLEEPER;
        check("SLEEPER columns repeat every max(2, round(SLEEPERS_EVERY*px))", sleepers);
        check("a column between sleepers is not a sleeper", pixel(img, w, plotX, plotY, 1, a, px) != TileRaster.SLEEPER);
    }

    /* ================= 2. M_MAIN | M_DOUBLE, and M_MAIN alone ================= */
    static void doubleMainAtEight() {
        System.out.println("--- 2. M_MAIN | M_DOUBLE at 8 px: four rails and a double bed; M_MAIN alone is single ---");
        int px = 8, plotX = 5, plotY = 10, w = T * px;
        TilePainter.Input in = owned();
        railRow(in, plotY, TilePainter.M_MAIN | TilePainter.M_DOUBLE);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        int bed = bedWidth(px, true), a = (px - bed) / 2;
        int mid = px / 2, off = Math.max(1, (int) Math.round(TileRaster.GAUGE / 2.0 * px));
        int sep = Math.max(1, (int) Math.round(TileRaster.TRACKS_APART / 2.0 * px));
        boolean[] steel = rowsAll(img, w, plotX, plotY, px, TileRaster.STEEL);
        check("four pixel rows are STEEL, at the two tracks' rails", count(steel) == 4
                && steel[mid - sep - off] && steel[mid - sep + off] && steel[mid + sep - off] && steel[mid + sep + off]);
        check("the bed's first and last pixel rows are BALLAST, as wide as BED_DOUBLE",
                pixel(img, w, plotX, plotY, 1, a, px) == TileRaster.BALLAST
                        && pixel(img, w, plotX, plotY, 1, a + bed - 1, px) == TileRaster.BALLAST);

        TilePainter.Input single = owned();
        railRow(single, plotY, TilePainter.M_MAIN);
        TilePainter.Painted ps = paint(single);
        int[] imgs = raster(single, ps, px);
        boolean[] steelSingle = rowsAll(imgs, w, plotX, plotY, px, TileRaster.STEEL);
        check("M_MAIN alone has two STEEL rows, as single track", count(steelSingle) == 2
                && steelSingle[mid - off] && steelSingle[mid + off]);
    }

    /* ================= 3. a north-south track at 8 px ================= */
    static void northSouthAtEight() {
        System.out.println("--- 3. a north-south track at 8 px: the same, by columns ---");
        int px = 8, plotX = 10, plotY = 5, w = T * px;
        TilePainter.Input in = owned();
        railColumn(in, plotX, 0);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        int bed = bedWidth(px, false), a = (px - bed) / 2;
        int mid = px / 2, off = Math.max(1, (int) Math.round(TileRaster.GAUGE / 2.0 * px));
        boolean[] steel = colsAll(img, w, plotX, plotY, px, TileRaster.STEEL);
        check("exactly two pixel columns are STEEL, at middle +/- round(GAUGE/2*px)", count(steel) == 2 && steel[mid - off] && steel[mid + off]);
        check("a bed pixel is BALLAST", pixel(img, w, plotX, plotY, a, 1, px) == TileRaster.BALLAST);

        int step = Math.max(2, (int) Math.round(TileRaster.SLEEPERS_EVERY * px));
        boolean sleepers = true;
        for (int sy = 0; sy < px; sy += step) sleepers &= pixel(img, w, plotX, plotY, a, sy, px) == TileRaster.SLEEPER;
        check("SLEEPER rows repeat every max(2, round(SLEEPERS_EVERY*px))", sleepers);
    }

    /* ================= 4. at 4 px ================= */
    static void atFour() {
        System.out.println("--- 4. at 4 px: a ballast bed, one steel centre line, one sleeper tick a plot ---");
        int px = 4, plotX = 5, plotY = 10, w = T * px;
        TilePainter.Input in = owned();
        railRow(in, plotY, 0);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        int bed = bedWidth(px, false), a = (px - bed) / 2;
        int mid = px / 2;
        boolean[] steel = rowsAll(img, w, plotX, plotY, px, TileRaster.STEEL);
        check("one pixel row is STEEL down the bed's middle", count(steel) == 1 && steel[mid]);
        check("a bed pixel is BALLAST", pixel(img, w, plotX, plotY, 1, a, px) == TileRaster.BALLAST);
        check("one SLEEPER tick at the start of the plot", pixel(img, w, plotX, plotY, 0, a, px) == TileRaster.SLEEPER);
    }

    /* ================= 5. L1 blocks ================= */
    static void blocksMiddle() {
        System.out.println("--- 5. L1 blocks at 2 px a plot: RAIL_DARK, with a tick beside the line every block ---");
        int px = MapFrame.MIDDLE_TILE_PX, block = TileRaster.BLOCK_MIDDLE, plotY = 10, w = T * px;
        TilePainter.Input in = owned();
        railRow(in, plotY, 0);
        TilePainter.Painted p = paint(in);
        int[] img = blocks(in, p, px, block);

        boolean dark = true, noLine = true;
        for (int x = 0; x < T; x++) {
            for (int dy = 0; dy < px; dy++) {
                for (int dx = 0; dx < px; dx++) {
                    dark &= pixel(img, w, x, plotY, dx, dy, px) == TileRaster.RAIL_DARK;
                    noLine &= pixel(img, w, x, plotY, dx, dy, px) != TileRaster.RAIL_LINE;
                }
            }
        }
        check("every track pixel is RAIL_DARK", dark);
        check("no track pixel is RAIL_LINE", noLine);

        boolean ticks = true, noneBetween = true;
        for (int x = 0; x < T; x++) {
            boolean expect = x % block == 0;
            int north = pixel(img, w, x, plotY - 1, 0, px - 1, px);
            int south = pixel(img, w, x, plotY + 1, 0, 0, px);
            ticks &= (north == TileRaster.RAIL_DARK) == expect;
            noneBetween &= (south == TileRaster.RAIL_DARK) == expect;
        }
        check("a tick pixel lies beside the line at every block boundary", ticks);
        check("no tick pixel lies beside the line between block boundaries", noneBetween);
    }

    /* ================= 6. L2 blocks ================= */
    static void blocksFar() {
        System.out.println("--- 6. L2 blocks at 1 px a plot: RAIL_DARK, no ticks ---");
        int px = MapFrame.FAR_TILE_PX, block = TileRaster.BLOCK_FAR, plotY = 10, w = T * px;
        TilePainter.Input in = owned();
        railRow(in, plotY, 0);
        TilePainter.Painted p = paint(in);
        int[] img = blocks(in, p, px, block);

        boolean dark = true, noTicks = true;
        for (int x = 0; x < T; x++) {
            dark &= pixel(img, w, x, plotY, 0, 0, px) == TileRaster.RAIL_DARK;
            noTicks &= pixel(img, w, x, plotY - 1, 0, 0, px) != TileRaster.RAIL_DARK
                    && pixel(img, w, x, plotY + 1, 0, 0, px) != TileRaster.RAIL_DARK;
        }
        check("every track pixel is RAIL_DARK", dark);
        check("no pixel beside the line is a tick", noTicks);
    }

    /* ================= 7. a level crossing ================= */
    static void levelCrossing() {
        System.out.println("--- 7. a paved street crossing a single track at level: crossbucks, barriers, rails showing ---");
        int px = 8, plotX = 5, plotY = 10, w = T * px;
        TilePainter.Input in = owned();
        railRow(in, plotY, 0);
        streetColumn(in, plotX, DistrictPlan.PAVED | (1 << TilePainter.S_WIDTH_SHIFT));
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        int t = Math.max(1, (int) Math.round(TileRaster.SURFACE[1] * px)), o = (px - t) / 2;
        check("a CROSSBUCK pixel in the north-west corner", pixel(img, w, plotX, plotY, 0, 0, px) == TileRaster.CROSSBUCK);
        check("a CROSSBUCK pixel in the south-east corner", pixel(img, w, plotX, plotY, px - 1, px - 1, px) == TileRaster.CROSSBUCK);
        check("a BARRIER pixel at the street's north edge", pixel(img, w, plotX, plotY, o, 0, px) == TileRaster.BARRIER);
        check("a BARRIER pixel at the street's south edge", pixel(img, w, plotX, plotY, o + (t - t / 2), px - 1, px) == TileRaster.BARRIER);
        check("a STEEL pixel on the street's surface", pixel(img, w, plotX, plotY, o, px / 2 + 1, px) == TileRaster.STEEL);
    }

    /* ================= 8. an arterial over the main line ================= */
    static void arterialBridge() {
        System.out.println("--- 8. an arterial over the main line is a bridge; a level crossing is named as one ---");
        int px = 8, plotX = 5, plotY = 10, w = T * px;
        int code = DistrictPlan.PAVED | (1 << TilePainter.S_WIDTH_SHIFT) | (TilePainter.S_ARTERIAL << TilePainter.S_ROLE_SHIFT);
        TilePainter.Input in = owned();
        railRow(in, plotY, TilePainter.M_MAIN);
        streetColumn(in, plotX, code);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        int t = Math.max(1, (int) Math.round(TileRaster.SURFACE[1] * px)), o = (px - t) / 2;
        check("a DECK edge along each side of the street's surface",
                pixel(img, w, plotX, plotY, o - 1, 0, px) == TileRaster.DECK
                        && pixel(img, w, plotX, plotY, o + t, 0, px) == TileRaster.DECK);
        boolean noMarks = true, noSteel = true;
        for (int dy = 0; dy < px; dy++) {
            for (int dx = 0; dx < px; dx++) {
                int c = pixel(img, w, plotX, plotY, dx, dy, px);
                noMarks &= c != TileRaster.CROSSBUCK && c != TileRaster.BARRIER;
                if (dx >= o && dx < o + t) noSteel &= c != TileRaster.STEEL;
            }
        }
        check("no CROSSBUCK and no BARRIER on the plot", noMarks);
        check("no STEEL on the street's surface", noSteel);
        String words = LandMap.plotWords(in, p, plotY * T + plotX, id -> null);
        check("LandMap.plotWords says \"bridging the railway\" there", words != null && words.contains("bridging the railway"));

        TilePainter.Input level = owned();
        railRow(level, plotY, 0);
        streetColumn(level, plotX, DistrictPlan.PAVED | (1 << TilePainter.S_WIDTH_SHIFT));
        TilePainter.Painted pl = paint(level);
        String levelWords = LandMap.plotWords(level, pl, plotY * T + plotX, id -> null);
        check("LandMap.plotWords says \"crossing the railway\" at a level crossing", levelWords != null && levelWords.contains("crossing the railway"));
    }

    /* ================= 9. unowned track ================= */
    static void unownedTrack() {
        System.out.println("--- 9. an unowned track's bed pixel is dimmed toward VOID ---");
        int px = 8, plotX = 5, plotY = 10, w = T * px;
        TilePainter.Input in = new TilePainter.Input();
        in.fixed[plotY * T + plotX] = TilePainter.RAIL;
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        int bed = bedWidth(px, false), a = (px - bed) / 2;
        int expected = TileRaster.blend(TileRaster.BALLAST, TileRaster.VOID, TileRaster.UNOWNED_DIM);
        check("a bed pixel is blend(BALLAST, VOID, UNOWNED_DIM)", pixel(img, w, plotX, plotY, 3, a, px) == expected);
    }

    /* ================= 10. a diagonal plot ================= */
    static void diagonalTrack() {
        System.out.println("--- 10. a diagonal track at 8 px: ballast and steel, no sleepers ---");
        int px = 8, plotX = 5, plotY = 10, w = T * px;
        TilePainter.Input in = owned();
        in.fixed[plotY * T + plotX] = TilePainter.RAIL;
        in.marks[plotY * T + plotX] = (byte) (CityRuns.M_DIAG | (CityRuns.diagCode(1) << CityRuns.M_DIAG_SHIFT));
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        boolean ballast = false, steel = false, sleeper = false;
        for (int dy = 0; dy < px; dy++) {
            for (int dx = 0; dx < px; dx++) {
                int c = pixel(img, w, plotX, plotY, dx, dy, px);
                ballast |= c == TileRaster.BALLAST;
                steel |= c == TileRaster.STEEL;
                sleeper |= c == TileRaster.SLEEPER;
            }
        }
        check("the plot has BALLAST pixels", ballast);
        check("the plot has STEEL pixels", steel);
        check("the plot has no SLEEPER pixels", !sleeper);
    }

    /* ================= 11. the eleventh class ================= */
    static void eleventhClass() {
        System.out.println("--- 11. the eleventh class: TRANSPORT, eleven tables, a Rail Terminal's fill ---");
        BuildingVisual.Type terminal = terminal();
        check("classOf(RAIL) is TRANSPORT", BuildingVisual.classOf(BuildingType.RAIL) == BuildingVisual.TRANSPORT);
        check("classOf(PORTS) is TRANSPORT", BuildingVisual.classOf(BuildingType.PORTS) == BuildingVisual.TRANSPORT);
        check("classOf(INFRASTRUCTURE) is TRANSPORT",
                BuildingVisual.classOf(BuildingType.INFRASTRUCTURE) == BuildingVisual.TRANSPORT);
        check("CLASSES, CLASS_NAMES, FILL, EDGE and LandMap.CLASS_ONE all have one length",
                BuildingVisual.CLASSES == BuildingVisual.CLASS_NAMES.length
                        && BuildingVisual.CLASSES == BuildingVisual.FILL.length
                        && BuildingVisual.CLASSES == BuildingVisual.EDGE.length
                        && BuildingVisual.CLASSES == LandMap.CLASS_ONE.length);
        check("that length is eleven", BuildingVisual.CLASSES == 11);
        check("a Rail Terminal has TRANSPORT's fill", terminal.fill() == BuildingVisual.FILL[BuildingVisual.TRANSPORT]);
    }

    /* ================= 12. a bulk yard at 8 px over an east-west track ================= */
    static void bulkYardAtEight() {
        System.out.println("--- 12. a bulk yard at 8 px over an east-west track: ground, fan, shed and stockpiles ---");
        int px = 8, boxX = 4, boxY = 4, boxW = 24, boxH = 24, railY = 16, w = T * px;
        TilePainter.Input in = yardAt(terminal(), boxX, boxY, boxW, boxH);
        railRow(in, railY, 0);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        check("YARD_GROUND pixels inside the box", hasInBox(img, w, boxX, boxY, boxW, boxH, px, TileRaster.YARD_GROUND));
        check("STEEL on at least YARD_TRACKS distinct pixel rows inside the box",
                distinctRows(img, w, boxX, boxY, boxW, boxH, px, TileRaster.STEEL) >= TileRaster.YARD_TRACKS);
        int lineY = railY * px + px / 2;
        check("TRANSPORT's fill (the shed) on the north side of the line",
                hasOnSide(img, w, boxX, boxY, boxW, boxH, px, BuildingVisual.FILL[BuildingVisual.TRANSPORT], lineY, true));
        check("ORE_HEAP stockpiles on the south side",
                hasOnSide(img, w, boxX, boxY, boxW, boxH, px, TileRaster.ORE_HEAP, lineY, false));
        check("no CONTAINER pixels", !hasInBox(img, w, boxX, boxY, boxW, boxH, px, TileRaster.CONTAINER));
    }

    /* ================= 13. the freight station ================= */
    static void freightStation() {
        System.out.println("--- 13. the freight station with goods is a container yard; other yards stay bulk ---");
        int px = 8, boxX = 4, boxY = 4, boxW = 24, boxH = 24, railY = 16, w = T * px;
        TilePainter.Input station = yardAt(terminal(), boxX, boxY, boxW, boxH);
        railRow(station, railY, 0);
        station.stationX = boxX;
        station.stationY = boxY;
        station.goodsByRail = true;
        TilePainter.Painted ps = paint(station);
        int[] imgs = raster(station, ps, px);
        check("the freight station with goods has CONTAINER pixels",
                hasInBox(imgs, w, boxX, boxY, boxW, boxH, px, TileRaster.CONTAINER));
        check("...and GANTRY pixels", hasInBox(imgs, w, boxX, boxY, boxW, boxH, px, TileRaster.GANTRY));
        check("...and no ORE_HEAP", !hasInBox(imgs, w, boxX, boxY, boxW, boxH, px, TileRaster.ORE_HEAP));

        TilePainter.Input other = yardAt(terminal(), boxX, boxY, boxW, boxH);
        railRow(other, railY, 0);
        other.goodsByRail = true;
        TilePainter.Painted po = paint(other);
        int[] imgo = raster(other, po, px);
        check("the same yard not the station stays bulk",
                hasInBox(imgo, w, boxX, boxY, boxW, boxH, px, TileRaster.ORE_HEAP)
                        && !hasInBox(imgo, w, boxX, boxY, boxW, boxH, px, TileRaster.CONTAINER));

        TilePainter.Input noGoods = yardAt(terminal(), boxX, boxY, boxW, boxH);
        railRow(noGoods, railY, 0);
        noGoods.stationX = boxX;
        noGoods.stationY = boxY;
        TilePainter.Painted pn = paint(noGoods);
        int[] imgn = raster(noGoods, pn, px);
        check("the station without goods stays bulk",
                hasInBox(imgn, w, boxX, boxY, boxW, boxH, px, TileRaster.ORE_HEAP)
                        && !hasInBox(imgn, w, boxX, boxY, boxW, boxH, px, TileRaster.CONTAINER));
    }

    /* ================= 14. wagons standing ================= */
    static void wagonsStanding() {
        System.out.println("--- 14. wagonTracks: none, every track, then half the tracks ---");
        int px = 8, boxX = 4, boxY = 4, boxW = 24, boxH = 24, railY = 16, w = T * px;
        TilePainter.Input none = yardAt(terminal(), boxX, boxY, boxW, boxH);
        railRow(none, railY, 0);
        TilePainter.Painted p0 = paint(none);
        int[] img0 = raster(none, p0, px);
        int[] bands = bedBands(rowsWith(img0, w, boxX, boxY, boxW, boxH, px, TileRaster.BALLAST));
        check("the yard's fan has YARD_TRACKS ballast bands", bands.length / 2 == TileRaster.YARD_TRACKS);
        check("wagonTracks 0 has no WAGON pixels", !hasInBox(img0, w, boxX, boxY, boxW, boxH, px, TileRaster.WAGON));

        TilePainter.Input full = yardAt(terminal(), boxX, boxY, boxW, boxH);
        railRow(full, railY, 0);
        full.wagonTracks = TileRaster.YARD_TRACKS;
        TilePainter.Painted pf = paint(full);
        int[] imgf = raster(full, pf, px);
        boolean allWagons = true;
        for (int b = 0; b < bands.length / 2; b++) {
            allWagons &= bandHas(imgf, w, boxX, boxY, boxW, boxH, px, TileRaster.WAGON, bands[2 * b], bands[2 * b + 1]);
        }
        check("YARD_TRACKS wagons stand on every track's bed", allWagons);

        TilePainter.Input half = yardAt(terminal(), boxX, boxY, boxW, boxH);
        railRow(half, railY, 0);
        half.wagonTracks = TileRaster.YARD_TRACKS / 2;
        TilePainter.Painted ph = paint(half);
        int[] imgh = raster(half, ph, px);
        boolean firstHalf = true, restNone = true;
        for (int b = 0; b < bands.length / 2; b++) {
            boolean has = bandHas(imgh, w, boxX, boxY, boxW, boxH, px, TileRaster.WAGON, bands[2 * b], bands[2 * b + 1]);
            if (b < TileRaster.YARD_TRACKS / 2) firstHalf &= has;
            else restNone &= !has;
        }
        check("half of YARD_TRACKS wagons stand on that many tracks and none on the rest", firstHalf && restNone);
    }

    /* ================= 15. a north-south line through the yard ================= */
    static void northSouthYard() {
        System.out.println("--- 15. a north-south line through the yard: the fan runs north-south ---");
        int px = 8, boxX = 4, boxY = 4, boxW = 24, boxH = 24, railX = 16, w = T * px;
        TilePainter.Input in = yardAt(terminal(), boxX, boxY, boxW, boxH);
        railColumn(in, railX, 0);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);
        check("STEEL on at least YARD_TRACKS distinct pixel columns inside the box",
                distinctCols(img, w, boxX, boxY, boxW, boxH, px, TileRaster.STEEL) >= TileRaster.YARD_TRACKS);
    }

    /* ================= 16. at 4 px, and below LINES_FROM ================= */
    static void atFourAndBelow() {
        System.out.println("--- 16. at 4 px the yard still draws as a yard; below LINES_FROM it is a plain TRANSPORT box ---");
        int boxX = 4, boxY = 4, boxW = 24, boxH = 24, railY = 16;
        int px = 4, w = T * px;
        TilePainter.Input in = yardAt(terminal(), boxX, boxY, boxW, boxH);
        railRow(in, railY, 0);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);
        check("at 4 px the yard has YARD_GROUND pixels",
                hasInBox(img, w, boxX, boxY, boxW, boxH, px, TileRaster.YARD_GROUND));
        check("...STEEL pixels", hasInBox(img, w, boxX, boxY, boxW, boxH, px, TileRaster.STEEL));
        check("...and TRANSPORT's fill (the shed)",
                hasInBox(img, w, boxX, boxY, boxW, boxH, px, BuildingVisual.FILL[BuildingVisual.TRANSPORT]));

        int low = 3, wl = T * low;
        TilePainter.Input below = yardAt(terminal(), boxX, boxY, boxW, boxH);
        railRow(below, railY, 0);
        TilePainter.Painted pb = paint(below);
        int[] imgb = raster(below, pb, low);
        check("below LINES_FROM the yard is a plain box of TRANSPORT's fill",
                hasInBox(imgb, wl, boxX, boxY, boxW, boxH, low, BuildingVisual.FILL[BuildingVisual.TRANSPORT])
                        && !hasInBox(imgb, wl, boxX, boxY, boxW, boxH, low, TileRaster.YARD_GROUND));
    }

    /* ================= 17. the stamp folds the yard ================= */
    static void stampFoldsTheYard() {
        System.out.println("--- 17. MapTiles.stamp changes when the yard's inputs change, and not otherwise ---");
        int boxX = 3, boxY = 3, boxW = 12, boxH = 12;
        TilePainter.Input in = yardAt(terminal(), boxX, boxY, boxW, boxH);
        long base = MapTiles.stamp(in);
        in.goodsByRail = true;
        check("the stamp changes when goodsByRail changes", MapTiles.stamp(in) != base);
        in.goodsByRail = false;
        in.wagonTracks = 2;
        check("the stamp changes when wagonTracks changes", MapTiles.stamp(in) != base);
        in.wagonTracks = 0;
        in.stationX = boxX;
        check("the stamp changes when stationX changes", MapTiles.stamp(in) != base);
        in.stationX = Long.MIN_VALUE;
        in.stationY = boxY;
        check("the stamp changes when stationY changes", MapTiles.stamp(in) != base);
        in.stationY = Long.MIN_VALUE;
        in.types = new BuildingVisual.Type[0];
        check("the stamp does not change otherwise", MapTiles.stamp(in) == base);
    }

    /* ================= 18. the rail picture's repaint ================= */
    static CityMap railCity() {
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        World world = World.of(Founding.DEFAULT_WORLD_SEED);
        return CityMap.square(Founding.DEFAULT_WORLD_SEED, world.foundingX(), world.foundingY(), types, 2, 100,
                new long[types.length]);
    }

    static void railPictureRepaint() {
        System.out.println("--- 18. CityMap.railPicture repaints on goods or a whole track, not a share less than one ---");
        CityMap m = railCity();
        long c0 = m.changes();
        m.railPicture(new CityMap.RailPicture(true, 0));
        check("goodsByRail true repaints", m.changes() > c0);
        long c1 = m.changes();
        m.railPicture(new CityMap.RailPicture(true, 0.05));
        check("a share change under a track's worth does not repaint", m.changes() == c1);
        m.railPicture(new CityMap.RailPicture(true, 0.5));
        check("a share change of a whole track repaints", m.changes() > c1);
    }
}
