package ham.citybuildersim;

import java.util.Arrays;
import java.util.List;

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
 *      track's bed but one (FX5: a yard keeps a siding clear for a train),
 *      and half of YARD_TRACKS on that many tracks only;
 *  15. a north-south line through the yard gives a north-south fan;
 *  16. at 4 px the yard still draws as a yard; below LINES_FROM it is a plain
 *      TRANSPORT box;
 *  17. MapTiles.stamp changes when stationX, stationY, goodsByRail or
 *      wagonTracks changes, and not otherwise;
 *  18. CityMap.railPicture repaints when goodsByRail or the whole wagon
 *      tracks change, and not when the share moves less than a track's worth.
 *  19. the far skeleton: MapTiles.nodePixels over a node covering a city with
 *      highways, track and yards draws the lightened highway colour and
 *      RAIL_DARK, never RAIL_LINE, and a 2 x 2 TRANSPORT dot at each yard;
 *  20. the legend: planeLegend() has the transport class, the railway's four
 *      and the gateway, and (0.8.2) the train's entry, LandMap.TRAIN_WORDS,
 *      and no RAIL_LINE entry, and (FX5) the street with no road bought named
 *      "Unmade street", not a second "Track" beside "Railway";
 *  21. (batch FX3, 0.8.4; red on 0.8.3) a second line crossing the yard's box:
 *      the fan closes on the line the yard stands on, not on the middle of
 *      all the track in the box; the crossing track is drawn from the box's
 *      edge to that line; no siding runs across it.
 *  22. (batch FR7 2.3) gravel is drawn grey, not tan: a near-neutral light
 *      grey, lighter than paved, told apart from the verge, the ballast, the
 *      track's brown dashes, the desert and the Farms' and the mines' fills,
 *      speckled at 8 px a plot, the legend's swatch the same grey.
 *  23. (batch FR7 slice 3) the Rail Depot is a small yard: its own footprint,
 *      smaller than a Rail Terminal's, in TRANSPORT's colours; drawn with
 *      DEPOT_TRACKS sidings (2 or 3) and a shed, a Terminal's with YARD_TRACKS
 *      as before; the trains' routes to it are DEPOT_TRACKS, one a siding.
 *  24. (batch FR7 slice 3) a siding's loading shed: a plot marked M_SHED beside
 *      a works is drawn with a block in TRANSPORT's fill over the track's end;
 *      an unmarked track plot is not.
 *  25. (batch FX5, 0.8.18; red before the curves) track turning a corner is
 *      drawn as a curve: an L of single track, its bend built by
 *      TrackCurves.bends() as the map builds it, has BALLAST, a sleeper or
 *      STEEL on the arc's middle, ground where the old L drew its corner, no
 *      ballast farther than the bed's half-width from the arc and the
 *      straights, the straights beside it as before and no step in the rails
 *      where the arc meets them; the polyline the trains run on (TrackCurves
 *      round() of the L's points) is on the drawn rail all the way, and
 *      TrackCurves' own rules hold (the radius, the cut to half a leg, the
 *      arcs, the ladder's S); double track draws the wider bed; a 45-degree
 *      turn draws its arc too and leaves no gap in it; the blocks' views draw
 *      the arc in RAIL_DARK and not the corner's cell; the tile's stamp moves
 *      with its bends.
 *  26. (batch FX5) a yard's ladders are S curves: tangent to the line at the
 *      apex and to the siding at its start, two rails from RAILS_FROM px.
 *  27. (batch FX5 Y2; red before the yard is drawn whole) a yard longer than
 *      a tile is drawn as one: a Rail Terminal's box CityRuns.YARD_LONG along
 *      its line and 24 across, east-west, starting a plot into a tile and so
 *      running on into the next across the ring (and, from plot 24, into a
 *      third, each seam more than a ladder from the box's ends), its tiles' inputs built as CityMap.fillInput() builds them
 *      (each tile given the yard whole in Input.yards and its clipped part as
 *      a building), each tile rastered alone at 8 px and laid side by side:
 *      each siding's ballast band lies at the same rows on both sides of a
 *      seam and runs across it, no ladder (no steel off the sidings' rails)
 *      within YARD_LADDER plots of a seam but one at each end of the box,
 *      one shed and three stockpiles, not one set a tile; the second tile's
 *      part is yard ground; a 24 x 24 yard inside one tile draws to the pixel
 *      as it does with no entry (section 12's); the stamp folds the yards; and
 *      on the skeleton city the map gives every tile a yard reaches the yard
 *      whole, in the layout the trains' routes lay, counted once, and its
 *      part on every tile is the yard's class in the blocks' views.
 *  28. (batch CL4; threw on 0.8.18) a Rail Depot drawn whole at its own
 *      8 x 7 footprint, laid east-west on its line at its last row with no
 *      track yet, as a world with no gateway keeps it: drawn at LINES_FROM,
 *      JUNCTIONS_FROM and 8 px without throwing, as a yard - no heap drawn
 *      where its share past the fan is under TileRaster.HEAP_LEAST; on 0.8.18
 *      the heaps were drawn past the yard's picture and the near view threw.
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
        farSkeleton();
        legend();
        lineCrossingTheYard();
        gravelGrey();
        railDepotYard();
        sidingShed();
        curvedCorner();
        yardLadders();
        yardAcrossTiles();
        depotWithNoLine();
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
        System.out.println("--- 14. wagonTracks: none, every track but one, then half the tracks ---");
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
        // FX5, Jerus 2026-10-20 ("Queue the fix" for a train standing over the yard's own wagons): a yard keeps one siding clear of standing
        // wagons, so a train always has one (TileRaster.wagonTracksOn(); CityMap.trainRoutes() lays no route to the others): YARD_TRACKS
        // wagon tracks stand on every track's bed but one, the last from the shed's side. It was "every track's bed".
        boolean allWagons = true, lastClear = true;
        int tracksDrawn = bands.length / 2;
        for (int b = 0; b < tracksDrawn; b++) {
            boolean has = bandHas(imgf, w, boxX, boxY, boxW, boxH, px, TileRaster.WAGON, bands[2 * b], bands[2 * b + 1]);
            if (b < tracksDrawn - 1) allWagons &= has;
            else lastClear &= !has;
        }
        check("YARD_TRACKS wagons stand on every track's bed but one, which stays clear for a train", allWagons && lastClear);

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

    /* ================= 19. the far skeleton ================= */

    /** A square city on a dry place whose square holds iron, with highways, a freight line and yards: the far skeleton's fixture. */
    static CityMap skeletonCity() {
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        World world = World.of(Founding.DEFAULT_WORLD_SEED);
        int side = 4;
        long[] place = MapCheck.dryPlace(world, side);
        int highway = -1, freight = -1, terminal = -1, mine = -1;
        for (int t = 0; t < types.length; t++) {
            if (types[t] == null) continue;
            if (highway < 0 && types[t].road() == BuildingVisual.HIGHWAY) highway = t;
            else if (freight < 0 && types[t].track() && types[t].plots() > 100) freight = t;
            else if (terminal < 0 && types[t].terminal()) terminal = t;
            if (mine < 0 && types[t].site() == Resource.IRON) mine = t;
        }
        long[] counts = new long[types.length];
        counts[highway] = 10;
        counts[freight] = 2;
        counts[terminal] = 2;
        counts[mine] = 2;
        return CityMap.square(Founding.DEFAULT_WORLD_SEED, place[0], place[1], types, side,
                CityMap.DISTRICT * CityMap.DISTRICT, counts);
    }

    /** Whether any pixel is exactly `colour`. */
    static boolean hasColour(int[] img, int colour) {
        for (int c : img) if (c == colour) return true;
        return false;
    }

    static void farSkeleton() {
        System.out.println("--- 19. the far skeleton: lightened highways, dark track, and TRANSPORT yard dots on the node ---");
        CityMap m = skeletonCity();
        int highwayColour = TileRaster.blend(TileRaster.ROAD[BuildingVisual.HIGHWAY], 0xffffffff,
                MapTiles.FAR_HIGHWAY_LIGHTEN);
        MapTiles tiles = new MapTiles(m);
        long plots = 16, span = MapFrame.NODE_PX * plots;
        long nx = Math.floorDiv(m.land().siteX(), span) * span, ny = Math.floorDiv(m.land().siteY(), span) * span;
        byte[] ground = MapTiles.nodeTerrain(m.seed(), nx, ny, plots);
        int[] img = new int[MapFrame.NODE_PX * MapFrame.NODE_PX];
        tiles.nodePixels(nx, ny, plots, ground, img);
        check("the node has pixels in the lightened highway colour", hasColour(img, highwayColour));
        check("...and pixels in RAIL_DARK", hasColour(img, TileRaster.RAIL_DARK));
        check("...and no pixel in the old RAIL_LINE", !hasColour(img, TileRaster.RAIL_LINE));
        int transport = BuildingVisual.FILL[BuildingVisual.TRANSPORT];
        int n = MapFrame.NODE_PX;
        boolean allYards = true;
        for (long[] yard : m.runs().yards()) {
            long px = Math.floorDiv((long) Math.floor(yard[0] + yard[2] / 2.0 - nx), plots);
            long py = Math.floorDiv((long) Math.floor(yard[1] + yard[3] / 2.0 - ny), plots);
            boolean dot = px >= 0 && py >= 0 && px + 1 < n && py + 1 < n
                    && img[(int) (py * n + px)] == transport && img[(int) (py * n + px + 1)] == transport
                    && img[(int) ((py + 1) * n + px)] == transport && img[(int) ((py + 1) * n + px + 1)] == transport;
            allYards &= dot;
        }
        check("a 2 x 2 TRANSPORT dot sits at each yard's middle", !m.runs().yards().isEmpty() && allYards);
        if (m.shore().works().isEmpty()) {
            System.out.println("  the skeleton city has no sea terminals; the yard dots prove the far dot");
        } else {
            boolean allTerminals = true;
            for (CityShore.Work w : m.shore().works()) {
                if (w.quay() <= 0) continue;
                long px = Math.floorDiv((long) Math.floor(w.x0() + w.w() / 2.0 - nx), plots);
                long py = Math.floorDiv((long) Math.floor(w.y0() + w.h() / 2.0 - ny), plots);
                allTerminals &= px >= 0 && py >= 0 && px + 1 < n && py + 1 < n
                        && img[(int) (py * n + px)] == transport && img[(int) (py * n + px + 1)] == transport
                        && img[(int) ((py + 1) * n + px)] == transport && img[(int) ((py + 1) * n + px + 1)] == transport;
            }
            check("...and a 2 x 2 TRANSPORT dot sits at each sea terminal's middle", allTerminals);
        }
    }

    /* ================= 20. the legend ================= */

    /** Whether the legend has an entry with these words. */
    static boolean legendHas(List<LandMap.Entry> entries, String words) {
        for (LandMap.Entry e : entries) if (e.words().equals(words)) return true;
        return false;
    }

    static void legend() {
        System.out.println("--- 20. the legend: the transport class, the railway's four and the gateway, and (0.8.2) the train ---");
        List<LandMap.Entry> plane = LandMap.planeLegend();
        check("planeLegend() has the TRANSPORT class's entry",
                legendHas(plane, BuildingVisual.CLASS_NAMES[BuildingVisual.TRANSPORT]));
        check("...has \"Railway\"", legendHas(plane, "Railway"));
        check("...has \"Freight yard\"", legendHas(plane, "Freight yard"));
        check("...has \"Level crossing\"", legendHas(plane, "Level crossing"));
        check("...has \"Main line, double with a Freight Line\"", legendHas(plane, "Main line, double with a Freight Line"));
        check("...has \"Gateway: to the world\"", legendHas(plane, "Gateway: to the world"));
        check("...has the train's entry (0.8.2): \"" + LandMap.TRAIN_WORDS + "\"", legendHas(plane, LandMap.TRAIN_WORDS));
        boolean railLine = false;
        for (LandMap.Entry e : plane) railLine |= e.colour() == TileRaster.RAIL_LINE;
        check("...and no entry wears the old RAIL_LINE", !railLine);
        check("...a street with no road bought has \"" + LandMap.TRACK_ENTRY + "\" and the legend has no second \"Track\" beside \"Railway\" (FX5)",
                legendHas(plane, LandMap.TRACK_ENTRY) && !legendHas(plane, "Track") && "Unmade street".equals(LandMap.TRACK_ENTRY));
        check("...and its hover words begin with the entry's", LandMap.TRACK_WORDS.startsWith(LandMap.TRACK_ENTRY));
        System.out.println("  ui.MapView.LEGEND_ROWS is package-private, so this harness cannot reach it; the layout is checked by eye");
    }

    /* ================= 22. gravel drawn grey (FR7 2.3) ================= */
    static void gravelGrey() {
        System.out.println("--- 22. gravel drawn grey, not tan, and told from its neighbours (FR7 2.3) ---");
        int g = TileRaster.ROAD[BuildingVisual.GRAVEL];
        int r = (g >> 16) & 255, gr = (g >> 8) & 255, b = g & 255;
        int spread = Math.max(r, Math.max(gr, b)) - Math.min(r, Math.min(gr, b));
        check("gravel is a near-neutral grey: its channels within 0x14 of each other (the tan's were 0x51 apart), " + spread, spread <= 0x14);
        check("...the small view's gravel the same grey", TileRaster.ROAD_SMALL[BuildingVisual.GRAVEL] == g);
        check("...lighter than paved: gravel reads apart from paved by lightness", luma(g) > luma(TileRaster.ROAD[BuildingVisual.PAVED]) + 8);
        int[][] neighbours = {
            { TileRaster.VERGE, 0 }, { TileRaster.ROAD[BuildingVisual.PAVED], 1 }, { TileRaster.BALLAST, 2 }, { TileRaster.TRACK_DASH, 3 },
            { TileRaster.GROUND[World.DESERT], 4 }, { BuildingVisual.FILL[BuildingVisual.FARM], 5 },
            { BuildingVisual.FILL[BuildingVisual.MINE], 6 } };
        String[] names = { "the verge", "paved", "the ballast", "the track's dashes", "the desert", "the Farms' fill", "the mines' fill" };
        for (int[] n : neighbours) {
            double d = distance(g, n[0]);
            check("...told from " + names[n[1]] + " (" + String.format("#%06x", n[0] & 0xffffff) + "): " + String.format("%.0f", d) + " apart, at least 24", d >= 24);
        }
        check("the speckle is darker than the stone it lies on, and lies on a fifth of it or less",
                luma(TileRaster.GRAVEL_GRAIN) < luma(g) && TileRaster.GRAVEL_GRAIN_SHARE > 0 && TileRaster.GRAVEL_GRAIN_SHARE <= .2);
        boolean swatch = false;
        for (LandMap.Entry e : LandMap.planeLegend()) swatch |= e.colour() == g && e.words().startsWith("Gravel");
        check("the legend's gravel swatch is the new grey", swatch);
    }

    /** A colour's luma, 0 to 255 (Rec. 601). */
    static double luma(int c) {
        return .299 * ((c >> 16) & 255) + .587 * ((c >> 8) & 255) + .114 * (c & 255);
    }

    /** Two colours' distance in RGB. */
    static double distance(int a, int c) {
        double dr = ((a >> 16) & 255) - ((c >> 16) & 255), dg = ((a >> 8) & 255) - ((c >> 8) & 255), db = (a & 255) - (c & 255);
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    /** Whether pixel row y has a pixel of `colour` within `reach` pixels of column x. */
    static boolean nearInRow(int[] img, int w, int y, int x, int reach, int colour) {
        for (int xx = Math.max(0, x - reach); xx <= Math.min(w - 1, x + reach); xx++) if (img[y * w + xx] == colour) return true;
        return false;
    }

    /* ================= 23. the Rail Depot: a small yard (FR7 slice 3) ================= */

    /** The Rail Depot type: the yard FR7 added as the railway's small first rung. */
    static BuildingVisual.Type depot() {
        for (BuildingVisual.Type t : TYPES) if (BuildingVisual.depot(t)) return t;
        throw new IllegalStateException("no Rail Depot type");
    }

    static void railDepotYard() {
        System.out.println("--- 23. the Rail Depot is a small yard: its own footprint, a few sidings and a shed; trains route to each ---");
        BuildingVisual.Type depot = depot(), terminal = terminal();
        int[] fd = BuildingVisual.footprint(depot), ft = BuildingVisual.footprint(terminal);
        check("its footprint is smaller than a Rail Terminal's, in whole plots: " + fd[0] + " x " + fd[1] + " against " + ft[0] + " x " + ft[1],
                fd[0] * fd[1] < ft[0] * ft[1] && fd[0] <= ft[0] && fd[1] <= ft[1]);
        check("...it is a yard of TRANSPORT's colours, as the Terminal is",
                depot.terminal() && depot.fill() == BuildingVisual.FILL[BuildingVisual.TRANSPORT] && depot.fill() == terminal.fill());
        check("DEPOT_TRACKS is 2 or 3, fewer than YARD_TRACKS; tracksOf() gives the depot its own and a Terminal YARD_TRACKS",
                TileRaster.DEPOT_TRACKS >= 2 && TileRaster.DEPOT_TRACKS <= 3 && TileRaster.DEPOT_TRACKS < TileRaster.YARD_TRACKS
                        && TileRaster.tracksOf(depot) == TileRaster.DEPOT_TRACKS && TileRaster.tracksOf(terminal) == TileRaster.YARD_TRACKS);
        // Drawn at 8 px, its box across an east-west line as CityRuns.spotsIn() lays it: ending on the line, its last row.
        int px = 8, boxX = 6, boxY = 10, w = T * px, boxW = fd[0], boxH = fd[1], railY = boxY + boxH - 1;
        TilePainter.Input in = yardAt(depot, boxX, boxY, boxW, boxH);
        railRow(in, railY, 0);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);
        check("the depot is drawn as a yard: YARD_GROUND inside its box", hasInBox(img, w, boxX, boxY, boxW, boxH, px, TileRaster.YARD_GROUND));
        int[] bands = bedBands(rowsWith(img, w, boxX, boxY, boxW, boxH, px, TileRaster.BALLAST));
        check("...with DEPOT_TRACKS ballast bands (sidings), not a Terminal's YARD_TRACKS: " + bands.length / 2, bands.length / 2 == TileRaster.DEPOT_TRACKS);
        check("...and a shed in TRANSPORT's fill", hasInBox(img, w, boxX, boxY, boxW, boxH, px, BuildingVisual.FILL[BuildingVisual.TRANSPORT]));
        // The control: a Terminal's box over a line keeps its fan of eight.
        TilePainter.Input tin = yardAt(terminal, 4, 4, ft[0], ft[1]);
        railRow(tin, 16, 0);
        TilePainter.Painted tp = paint(tin);
        int[] tbands = bedBands(rowsWith(raster(tin, tp, px), w, 4, 4, ft[0], ft[1], px, TileRaster.BALLAST));
        check("control: a Rail Terminal's yard still has YARD_TRACKS ballast bands", tbands.length / 2 == TileRaster.YARD_TRACKS);
        // The trains' routes to the depot: one to each of its sidings.
        long[] yard = { 100, 100, boxW, boxH };
        boolean[] rail = new boolean[boxW * boxH];
        for (int x = 0; x < boxW; x++) rail[(boxH - 1) * boxW + x] = true;
        double ly = 100 + boxH - 0.5;
        double[][] way = { { 80.5, 100 + boxW / 2.0 }, { ly, ly } };
        java.util.List<TrainSchedule.Route> routes = new java.util.ArrayList<>();
        CityMap.trainRoutesInto(routes, 0, false, yard, way, 16.5, ly, 80.5, ly, rail, TileRaster.tracksOf(depot));
        java.util.Set<Integer> sidings = new java.util.HashSet<>();
        for (TrainSchedule.Route r : routes) sidings.add(r.siding());
        check("the trains' routes to it are DEPOT_TRACKS, one a siding: " + routes.size() + " routes",
                routes.size() == TileRaster.DEPOT_TRACKS && sidings.size() == TileRaster.DEPOT_TRACKS && sidings.contains(0) && sidings.contains(TileRaster.DEPOT_TRACKS - 1));
    }

    /* ================= 24. a siding's loading shed (FR7 slice 3) ================= */

    static void sidingShed() {
        System.out.println("--- 24. a siding's loading shed: a block in TRANSPORT's fill on the plot marked M_SHED, none on the track beside it ---");
        int px = 8, plotX = 10, plotY = 12, w = T * px, fill = BuildingVisual.FILL[BuildingVisual.TRANSPORT];
        TilePainter.Input in = owned();
        railRow(in, plotY, 0);
        in.marks[plotY * T + plotX] = (byte) TilePainter.M_SHED;
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);
        check("the marked plot's middle pixel is TRANSPORT's fill", pixel(img, w, plotX, plotY, px / 2, px / 2, px) == fill);
        boolean beside = false;
        for (int dy = 0; dy < px; dy++) for (int dx = 0; dx < px; dx++) beside |= pixel(img, w, plotX - 1, plotY, dx, dy, px) == fill || pixel(img, w, plotX + 1, plotY, dx, dy, px) == fill;
        check("...and the track plots beside it have none", !beside);
        int pad = (int) Math.round(px * (1 - TileRaster.SIDING_SHED) / 2.0);
        check("...the shed is a small block: a corner of the plot is still track, not shed", pixel(img, w, plotX, plotY, 0, 0, px) != fill && pad > 0);
        TilePainter.Input plain = owned();
        railRow(plain, plotY, 0);
        int[] pimg = raster(plain, paint(plain), px);
        boolean any = false;
        for (int dy = 0; dy < px; dy++) for (int dx = 0; dx < px; dx++) any |= pixel(pimg, w, plotX, plotY, dx, dy, px) == fill;
        check("control: the same track with no mark has no TRANSPORT fill on it", !any);
    }

    /* ================= 21. a line crossing the yard's box ================= */
    static void lineCrossingTheYard() {
        System.out.println("--- 21. a line crossing a yard's box: the fan closes on the yard's own line, the cross track is drawn, no siding runs across it ---");
        int px = 8, boxX = 4, boxY = 4, boxW = 24, boxH = 24, lineX = 11, crossY = 8, w = T * px;
        // Jerus's second yard: its own north-south line (plot column 11, the box's column 7), and an east-west main line across the box on plot row 8 (its row 4).
        TilePainter.Input in = yardAt(terminal(), boxX, boxY, boxW, boxH);
        railColumn(in, lineX, 0);
        railRow(in, crossY, 0);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        // On 0.8.3 drawYard() took the middle of ALL the track in the box (box column 12, pixel 128) for the line the fan closes to;
        // since FX3 (0.8.4) the fan takes TileRaster.yardLayout()'s stretch of the yard's own line.
        int apexY = (boxY + boxH) * px - 2, lineMid = lineX * px + px / 2, boxMid = (boxX + boxW / 2) * px;
        check("the fan's south apex is on the line: STEEL within 1 px of the line's middle column in the box's last row",
                nearInRow(img, w, apexY, lineMid, 1, TileRaster.STEEL));
        check("...and no STEEL within 2 px of the box's middle column in that row",
                !nearInRow(img, w, apexY, boxMid, 2, TileRaster.STEEL));

        int ballasted = 0, firstBare = -1;
        int midRow = crossY * px + px / 2;
        for (int plotX = lineX + 1; plotX < boxX + boxW; plotX++) {
            boolean has = false;
            for (int x = plotX * px; x < (plotX + 1) * px; x++) has |= img[midRow * w + x] == TileRaster.BALLAST;
            if (has) ballasted++;
            else if (firstBare < 0) firstBare = plotX;
        }
        int wanted = boxX + boxW - 1 - lineX;
        check("the east-west track is drawn from the box's east edge to the line: BALLAST in every plot's middle row, columns "
                + (lineX + 1) + ".." + (boxX + boxW - 1), ballasted == wanted);
        System.out.println("  plots with BALLAST in row " + crossY + "'s middle pixel row: " + ballasted + " of " + wanted
                + (firstBare < 0 ? "" : ", the first bare one is plot column " + firstBare));

        int bandY0 = crossY * px, worstRows = 0, worstX = -1;
        for (int x = 0; x < w; x++) {
            if (x >= lineX * px && x < (lineX + 1) * px) continue;
            int rows = 0;
            for (int y = bandY0; y < bandY0 + px; y++) if (img[y * w + x] == TileRaster.STEEL) rows++;
            if (rows > worstRows) { worstRows = rows; worstX = x; }
        }
        check("no siding runs across the crossing: no pixel column outside the line's own plot has STEEL on more than 2 of row "
                + crossY + "'s " + px + " pixel rows", worstRows <= 2);
        System.out.println("  the most STEEL rows in one pixel column of row " + crossY + "'s band outside the line's plot: " + worstRows
                + (worstRows > 2 ? " at x = " + worstX : ""));

        // The control: the same yard with only the north-south column (section 15's case moved to column 11) must stay as it is.
        TilePainter.Input only = yardAt(terminal(), boxX, boxY, boxW, boxH);
        railColumn(only, lineX, 0);
        TilePainter.Painted po = paint(only);
        int[] imgo = raster(only, po, px);
        check("control: with only the north-south column the south apex STEEL is within 1 px of x = " + lineMid,
                nearInRow(imgo, w, apexY, lineMid, 1, TileRaster.STEEL));
    }

    /* ================= 25. (FX5) a corner is drawn as a curve ================= */

    /** Whether a pixel is the railway's: ballast, a sleeper or steel. */
    static boolean isTrack(int c) {
        return c == TileRaster.BALLAST || c == TileRaster.SLEEPER || c == TileRaster.STEEL;
    }

    /** An L of single track on owned ground, as the runs lay it: east along row `rowY` from column `x0` to column `colX`, then south down that column to row `y1`, each plot with `marks`. */
    static TilePainter.Input lOfTrack(int x0, int rowY, int colX, int y1, int marks) {
        TilePainter.Input in = owned();
        for (int x = x0; x <= colX; x++) in.fixed[rowY * T + x] = TilePainter.RAIL;
        for (int y = rowY; y <= y1; y++) in.fixed[y * T + colX] = TilePainter.RAIL;
        if (marks != 0) for (int i = 0; i < TilePainter.PLOTS; i++) if (in.fixed[i] == TilePainter.RAIL) in.marks[i] = (byte) marks;
        return in;
    }

    /** The bends of the polyline through the points, put into the input as CityMap puts a route's in, and the plots they replace marked. */
    static List<TrackCurves.Bend> bendInto(TilePainter.Input in, double[] xs, double[] ys, boolean doubled) {
        List<TrackCurves.Bend> bends = TrackCurves.bends(xs, ys, TrackCurves.RADIUS);
        for (TrackCurves.Bend b : bends) in.addBend(b.net(), b.x(), b.y(), b.ux(), b.uy(), b.vx(), b.vy(), b.r(), b.t(), doubled);
        in.markCurved();
        return bends;
    }

    /** The distance, in pixels, from the middle of pixel (x, y) to the nearest of the path's points (in plots, scaled by px). */
    static double pathDistance(double[][] path, int px, int x, int y) {
        double best = Double.MAX_VALUE;
        for (double[] q : path) best = Math.min(best, Math.hypot(q[0] * px - (x + 0.5), q[1] * px - (y + 0.5)));
        return best;
    }

    /** An L's centreline bent by `b`, sampled every 1/50 plot: from plot edge (xa, ya) to the arc's start, the arc, and from its end on to plot edge (xb, yb). */
    static double[][] bentPath(TrackCurves.Bend b, double xa, double ya, double xb, double yb) {
        List<double[]> out = new java.util.ArrayList<>();
        double d = Math.hypot(b.startX() - xa, b.startY() - ya);
        for (int k = 0; k <= d * 50; k++) out.add(new double[] { xa + (b.startX() - xa) * k / (d * 50), ya + (b.startY() - ya) * k / (d * 50) });
        for (int k = 0; k <= b.length() * 50; k++) {
            double[] q = b.at(k / 50.0);
            out.add(new double[] { q[0], q[1] });
        }
        double e = Math.hypot(xb - b.endX(), yb - b.endY());
        for (int k = 0; k <= e * 50; k++) out.add(new double[] { b.endX() + (xb - b.endX()) * k / (e * 50), b.endY() + (yb - b.endY()) * k / (e * 50) });
        return out.toArray(new double[0][]);
    }

    static void curvedCorner() {
        System.out.println("--- 25. (FX5) track turning a corner is drawn as a curve: an arc of bed, rails and sleepers, ground where the L's corner was ---");
        trackCurvesRules();
        int px = 8, w = T * px, rowY = 10, colX = 14, x0 = 3, y1 = 22;
        TilePainter.Input in = lOfTrack(x0, rowY, colX, y1, 0);
        double[] xs = { x0 + .5, colX + .5, colX + .5 }, ys = { rowY + .5, rowY + .5, y1 + .5 };
        List<TrackCurves.Bend> bends = bendInto(in, xs, ys, false);
        check("fixture: the L's one bend has the radius TrackCurves.RADIUS and replaces the corner's plot and the next two on each leg, no more",
                bends.size() == 1 && Math.abs(bends.get(0).r() - TrackCurves.RADIUS) < 1e-9 && in.curved[rowY * T + colX]
                        && in.curved[rowY * T + colX - 1] && in.curved[rowY * T + colX - 2] && !in.curved[rowY * T + colX - 3]
                        && in.curved[(rowY + 1) * T + colX] && in.curved[(rowY + 2) * T + colX] && !in.curved[(rowY + 3) * T + colX]);
        TrackCurves.Bend b = bends.get(0);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);

        double[] mid = b.at(b.length() / 2);
        int mx = (int) Math.floor(mid[0] * px), my = (int) Math.floor(mid[1] * px);
        check("the arc's middle (the centre plus the radius along the bisector) is BALLAST, a sleeper or STEEL", isTrack(img[my * w + mx]));
        check("the L's corner, the middle of the corner plot, is ground and not the bed the L drew there",
                !isTrack(img[(rowY * px + px / 2) * w + colX * px + px / 2]));
        check("...and so is the pixel at the foot of the corner plot, where the L drew its arm to the south",
                !isTrack(img[(rowY * px + px / 2 + 3) * w + colX * px + px / 2]));

        double[][] path = bentPath(b, x0, rowY + .5, colX + .5, y1 + 1);
        int bed = bedWidth(px, false), far = 0, steel = 0, sleepers = 0;
        double worst = 0;
        for (int y = 0; y < w; y++) {
            for (int x = 0; x < w; x++) {
                int c = img[y * w + x];
                if (c == TileRaster.STEEL) steel++;
                if (c == TileRaster.SLEEPER) sleepers++;
                if (c != TileRaster.BALLAST) continue;
                double d = pathDistance(path, px, x, y);
                worst = Math.max(worst, d);
                if (d > bed / 2.0 + 0.5) far++;
            }
        }
        check("no BALLAST pixel lies farther than the bed's half-width (and half a pixel) from the arc and the straights: the worst is "
                + String.format("%.2f", worst) + " px against " + (bed / 2.0 + 0.5), far == 0);
        check("...and the arc has its rails and sleepers: STEEL and SLEEPER pixels over the tile", steel > 40 && sleepers > 10);

        // The rails are there all the way round: along the arc, at the gauge to either side, a STEEL pixel within a pixel.
        int off = Math.max(1, (int) Math.round(TileRaster.GAUGE / 2.0 * px)), gaps = 0;
        for (int k = 0; k <= 20; k++) {
            double[] q = b.at(b.length() * k / 20.0);
            double nx = -Math.sin(q[2]), ny = Math.cos(q[2]);
            for (int side = -1; side <= 1; side += 2) {
                double cx = q[0] * px + nx * side * off + 0.5, cy = q[1] * px + ny * side * off + 0.5;
                boolean found = false;
                for (int yy = (int) Math.floor(cy) - 1; yy <= (int) Math.floor(cy) + 1; yy++)
                    for (int xx = (int) Math.floor(cx) - 1; xx <= (int) Math.floor(cx) + 1; xx++) found |= img[yy * w + xx] == TileRaster.STEEL;
                if (!found) gaps++;
            }
        }
        check("...two rails the gauge apart all the way round it, no gap in either (21 places on the arc): " + gaps + " missing", gaps == 0);

        // The trains' way round it is the same geometry: every point of the rounded polyline (TrackCurves.round(), as the routes are rounded)
        // lies on the rail the tile draws - the pixel under it or one of its eight neighbours is the railway's.
        double[][] way = TrackCurves.round(xs, ys, TrackCurves.RADIUS);
        int offRail = 0, sampled = 0;
        for (int k = 0; k + 1 < way[0].length; k++) {
            double len = Math.hypot(way[0][k + 1] - way[0][k], way[1][k + 1] - way[1][k]);
            for (double f = 0; f < len; f += 0.1) {
                double wx = way[0][k] + (way[0][k + 1] - way[0][k]) * f / len, wy = way[1][k] + (way[1][k + 1] - way[1][k]) * f / len;
                int cx = (int) Math.floor(wx * px), cy = (int) Math.floor(wy * px);
                boolean on = false;
                for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) on |= isTrack(img[(cy + dy) * w + cx + dx]);
                sampled++;
                if (!on) offRail++;
            }
        }
        check("the rounded polyline the trains run (TrackCurves.round() of the L's points) is on the drawn rail all the way: " + offRail + " of " + sampled
                + " points off it", sampled > 100 && offRail == 0);

        // The straights beside the bend are as they were.
        TilePainter.Input plain = owned();
        railRow(plain, rowY, 0);
        int[] pimg = raster(plain, paint(plain), px);
        boolean same = true;
        for (int plotX = x0 + 1; plotX < colX - 4; plotX++) {
            for (int dy = 0; dy < px; dy++) for (int dx = 0; dx < px; dx++) same &= pixel(img, w, plotX, rowY, dx, dy, px) == pixel(pimg, w, plotX, rowY, dx, dy, px);
        }
        check("control: the straight plots away from the bend are drawn exactly as a straight track's (section 1's look)", same);
        TilePainter.Input none = owned();
        railRow(none, rowY, 0);
        none.markCurved();
        check("...and a track with no bend at all is drawn as before: markCurved() marks no plot and the image is the same",
                !anyCurved(none) && java.util.Arrays.equals(raster(none, paint(none), px), pimg));

        // No step where the arc meets the straight: each rail pixel of the last straight plot's last column has a rail within a pixel in the next column.
        int seamX = (colX - 2) * px, steps = 0, rails = 0;
        for (int y = (rowY - 1) * px; y < (rowY + 2) * px; y++) {
            if (img[y * w + seamX - 1] != TileRaster.STEEL) continue;
            rails++;
            boolean next = false;
            for (int dy = -1; dy <= 1; dy++) next |= img[(y + dy) * w + seamX] == TileRaster.STEEL;
            if (!next) steps++;
        }
        check("the rails run on across the seam from the last straight plot into the arc, within a pixel: " + rails + " rails meet it, " + steps + " with a step",
                rails == 2 && steps == 0);

        // Double track: the L's plots marked M_MAIN | M_DOUBLE, its bend on double track.
        TilePainter.Input two = lOfTrack(x0, rowY, colX, y1, TilePainter.M_MAIN | TilePainter.M_DOUBLE);
        TrackCurves.Bend bd = bendInto(two, xs, ys, true).get(0);
        int[] dimg = raster(two, paint(two), px);
        double[][] dpath = bentPath(bd, x0, rowY + .5, colX + .5, y1 + 1);
        int dbed = bedWidth(px, true), dfar = 0, wide = 0;
        for (int y = 0; y < w; y++) {
            for (int x = 0; x < w; x++) {
                if (dimg[y * w + x] != TileRaster.BALLAST) continue;
                double d = pathDistance(dpath, px, x, y);
                if (d > dbed / 2.0 + 0.5) dfar++;
                if (d > bed / 2.0 + 0.5) wide++;
            }
        }
        double[] dmid = bd.at(bd.length() / 2);
        check("double track: the arc has BED_DOUBLE's bed (some BALLAST beyond the single bed's width, none beyond the double's) and is track at its middle",
                dfar == 0 && wide > 0 && isTrack(dimg[(int) Math.floor(dmid[1] * px) * w + (int) Math.floor(dmid[0] * px)]));

        // A 45-degree turn: straight east, then a stretch south-east; the arc joins them and leaves no gap on its centreline.
        TilePainter.Input diag = owned();
        for (int x = 2; x <= 13; x++) diag.fixed[10 * T + x] = TilePainter.RAIL;
        int code = CityRuns.diagCode(1);
        for (int k = 1; k <= 5; k++) {
            diag.fixed[(10 + k) * T + 12 + k] = TilePainter.RAIL;
            diag.marks[(10 + k) * T + 12 + k] = (byte) (CityRuns.M_DIAG | (code << CityRuns.M_DIAG_SHIFT));
            diag.fixed[(10 + k - 1) * T + 12 + k] = TilePainter.RAIL;
            diag.marks[(10 + k - 1) * T + 12 + k] = (byte) (CityRuns.M_DIAG | (code << CityRuns.M_DIAG_SHIFT) | CityRuns.M_CORNER);
        }
        List<TrackCurves.Bend> turn = bendInto(diag, new double[] { 2.5, 12.5, 17.5 }, new double[] { 10.5, 10.5, 15.5 }, false);
        int[] gimg = raster(diag, paint(diag), px);
        TrackCurves.Bend t45 = turn.get(0);
        int holes = 0;
        for (int k = 0; k <= 20; k++) {
            double[] q = t45.at(t45.length() * k / 20.0);
            if (!isTrack(gimg[(int) Math.floor(q[1] * px) * w + (int) Math.floor(q[0] * px)])) holes++;
        }
        check("a 45-degree turn (straight into an M_DIAG stretch) is one bend of 45 degrees, its arc track all along its centreline: " + holes + " holes in 21",
                turn.size() == 1 && Math.abs(Math.abs(t45.sweep()) - Math.PI / 4) < 1e-9 && holes == 0);
        // ...and none of the old joint's ground inside its bed: every pixel within a pixel of the arc's centreline is track.
        int ground = 0;
        for (int k = 0; k <= 20; k++) {
            double[] q = t45.at(t45.length() * k / 20.0);
            for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
                if (!isTrack(gimg[((int) Math.floor(q[1] * px) + dy) * w + (int) Math.floor(q[0] * px) + dx])) ground++;
            }
        }
        check("...no ground pixel inside the bed at the joint: the 3 x 3 pixels about each of 21 places on the arc are all track: " + ground + " are ground", ground == 0);

        // The blocks' views: L1 draws the arc in RAIL_DARK at the width it draws track, and not the corner's cell.
        int bpx = MapFrame.MIDDLE_TILE_PX, bw = T * bpx;
        int[] bl = blocks(in, p, bpx, TileRaster.BLOCK_MIDDLE);
        check("L1 blocks: the arc's middle is RAIL_DARK", bl[(int) Math.floor(mid[1] * bpx) * bw + (int) Math.floor(mid[0] * bpx)] == TileRaster.RAIL_DARK);
        check("...the corner's own cell is not (the corner is cut), and a straight plot of the leg still is",
                bl[(rowY * bpx) * bw + colX * bpx] != TileRaster.RAIL_DARK && bl[(rowY * bpx) * bw + (x0 + 2) * bpx] == TileRaster.RAIL_DARK);
        int fpx = MapFrame.FAR_TILE_PX, fw = T * fpx;
        int[] far1 = blocks(in, p, fpx, TileRaster.BLOCK_FAR);
        check("L2 blocks: the arc's middle is RAIL_DARK, the corner's cell is not",
                far1[(int) Math.floor(mid[1] * fpx) * fw + (int) Math.floor(mid[0] * fpx)] == TileRaster.RAIL_DARK
                        && far1[rowY * fpx * fw + colX * fpx] != TileRaster.RAIL_DARK);

        // The stamp: a tile repaints when its bends change.
        TilePainter.Input again = lOfTrack(x0, rowY, colX, y1, 0);
        bendInto(again, xs, ys, false);
        TilePainter.Input bare = lOfTrack(x0, rowY, colX, y1, 0);
        check("the stamp is the same for the same bends, and changes with a bend's doubling or with none at all",
                MapTiles.stamp(again) == MapTiles.stamp(in) && MapTiles.stamp(two) != MapTiles.stamp(in) && MapTiles.stamp(bare) != MapTiles.stamp(in));
    }

    /** TrackCurves' own rules, the geometry the drawing and the routes share: the radius, the shrinking, the arcs' tangency and the ladder's S. */
    static void trackCurvesRules() {
        // A turn of 90 degrees between two long legs takes the full radius; between short ones it is cut to half of each leg, the radius shrunk with it.
        List<TrackCurves.Bend> longLegs = TrackCurves.bends(new double[] { 0, 20, 20 }, new double[] { 0, 0, 20 }, TrackCurves.RADIUS);
        List<TrackCurves.Bend> shortLegs = TrackCurves.bends(new double[] { 0, 2, 2 }, new double[] { 0, 0, 3 }, TrackCurves.RADIUS);
        check("a 90-degree turn between long legs has TrackCurves.RADIUS and a tangent length of as much; between legs of 2 and 3 plots it is cut to half the shorter",
                longLegs.size() == 1 && Math.abs(longLegs.get(0).r() - TrackCurves.RADIUS) < 1e-9 && Math.abs(longLegs.get(0).t() - TrackCurves.RADIUS) < 1e-9
                        && shortLegs.size() == 1 && Math.abs(shortLegs.get(0).t() - 1) < 1e-9 && Math.abs(shortLegs.get(0).r() - 1) < 1e-9);
        // Collinear points and a doubling back are no bends; a leg of no length is passed over.
        check("collinear points, a leg of no length and a doubling back make no bend",
                TrackCurves.bends(new double[] { 0, 5, 9 }, new double[] { 0, 0, 0 }, 3).isEmpty()
                        && TrackCurves.bends(new double[] { 0, 5, 5, 9 }, new double[] { 0, 0, 0, 0 }, 3).isEmpty()
                        && TrackCurves.bends(new double[] { 0, 5, 0 }, new double[] { 0, 0, 0 }, 3).isEmpty());
        // Two bends on one leg never meet: the legs' halves.
        double[] sx = { 0, 4, 4, 0 }, sy = { 0, 0, 3, 3 };
        List<TrackCurves.Bend> two = TrackCurves.bends(sx, sy, 3);
        check("two turns on one leg (a jog of 3 plots) take at most half of it each: their tangent lengths on the middle leg sum to no more than its length",
                two.size() == 2 && two.get(0).t() + two.get(1).t() <= 3 + 1e-9);
        // round(): the ends kept, every point of an arc at the radius from its centre, the legs' tangent points on the legs.
        double[][] r = TrackCurves.round(new double[] { 0, 20, 20 }, new double[] { 0, 0, 20 }, TrackCurves.RADIUS);
        TrackCurves.Bend b = longLegs.get(0);
        boolean onArc = true;
        for (int i = 0; i < r[0].length; i++) {
            if (r[0][i] > b.startX() + 1e-9 && r[1][i] < b.endY() - 1e-9) onArc &= Math.abs(Math.hypot(r[0][i] - b.cx(), r[1][i] - b.cy()) - b.r()) < 1e-9;
        }
        check("round() keeps the polyline's ends, and the points between the tangent points lie on the arc, a radius from its centre",
                r[0][0] == 0 && r[1][0] == 0 && r[0][r[0].length - 1] == 20 && r[1][r[1].length - 1] == 20 && onArc
                        && r[0].length > 8 && Math.abs(b.startX() - 17) < 1e-9 && Math.abs(b.endY() - 3) < 1e-9);
        // The ladder: an S from the apex, level at both ends, over `along` and `off`.
        double[][] lad = TrackCurves.ladder(10, 20, 1, 0, 0, 1, 3, 2.5);
        int n = lad[0].length;
        boolean monotone = true;
        for (int i = 1; i < n; i++) monotone &= lad[0][i] > lad[0][i - 1] && lad[1][i] >= lad[1][i - 1] - 1e-12;
        double slope0 = (lad[1][1] - lad[1][0]) / (lad[0][1] - lad[0][0]), slopeN = (lad[1][n - 1] - lad[1][n - 2]) / (lad[0][n - 1] - lad[0][n - 2]);
        check("a ladder runs from the apex to the siding's start (along 3, off 2.5) as an S that is level at both ends: first slope " + String.format("%.2f", slope0)
                        + ", last " + String.format("%.2f", slopeN) + ", halfway " + String.format("%.2f", TrackCurves.smoothstep(0.5)),
                lad[0][0] == 10 && lad[1][0] == 20 && Math.abs(lad[0][n - 1] - 13) < 1e-9 && Math.abs(lad[1][n - 1] - 22.5) < 1e-9
                        && monotone && slope0 < 0.5 && slopeN < 0.5 && Math.abs(TrackCurves.smoothstep(0.5) - 0.5) < 1e-12);
    }

    /** Whether any plot of the input is marked curved. */
    static boolean anyCurved(TilePainter.Input in) {
        for (boolean c : in.curved) if (c) return true;
        return false;
    }

    /* ================= 26. (FX5) a yard's ladders are S curves ================= */

    static void yardLadders() {
        System.out.println("--- 26. (FX5) a yard's ladders are S curves: tangent to the line at the apex, to the siding at its start, two rails from RAILS_FROM ---");
        int px = 8, boxX = 4, boxY = 4, boxW = 24, boxH = 24, railY = 16, w = T * px;
        TilePainter.Input in = yardAt(terminal(), boxX, boxY, boxW, boxH);
        railRow(in, railY, 0);
        TilePainter.Painted p = paint(in);
        int[] img = raster(in, p, px);
        // The fan: eight sidings YARD_TRACK_APART apart about the line; the outermost two lie 5.25 plots off it, over a ladder of YARD_LADDER plots.
        int line = (int) Math.round((railY + 0.5) * px), start = boxX * px + 1, ladder = (int) Math.round(TileRaster.YARD_LADDER * px);
        int fanA = start + ladder, outer = (int) Math.round(line + (TileRaster.YARD_TRACKS - 1) / 2.0 * TileRaster.YARD_TRACK_APART * px);
        // Two pixels along from the apex the straight ladder was 3.5 px off the line (4 rows with the pixel line's rounding); an S that leaves it level is 2 or under.
        int apexCol = start + 2, south = -1, north = Integer.MAX_VALUE;
        for (int y = 0; y < w; y++) {
            if (img[y * w + apexCol] != TileRaster.STEEL) continue;
            south = Math.max(south, y);
            north = Math.min(north, y);
        }
        check("two pixels along from the apex the outermost ladders are still at the line (an S leaves it tangent): STEEL rows " + north + ".." + south
                + " about " + line + " are within 2 px of it", south - line <= 2 && line - north <= 2);
        // ...and the other end, two pixels short of the siding's start: the ladder is at the siding's row.
        int fanCol = fanA - 2;
        boolean reaches = false;
        for (int y = 0; y < w; y++) reaches |= Math.abs(y - outer) <= 1 && img[y * w + fanCol] == TileRaster.STEEL;
        check("...and meets the outermost siding tangent at the other end: STEEL within 1 px of its row (" + outer + ") two pixels before the fan", reaches);
        // From RAILS_FROM px a plot a ladder is two rails: halfway along, a pair of STEEL pixels a gauge apart, not a one-pixel line.
        int half = start + 12, pairs = 0;
        for (int y = 1; y < w - 2; y++) {
            if (img[y * w + half] == TileRaster.STEEL && img[(y + 1) * w + half] != TileRaster.STEEL && img[(y + 2) * w + half] == TileRaster.STEEL) pairs++;
        }
        check("at 8 px a ladder is two rails: a pair of STEEL pixels a gauge apart in its column halfway along, not a one-pixel line: " + pairs + " pairs", pairs >= 1);

        // The middle siding of an odd fan lies on the line itself: its ladders are straight, two rails all the way from the apex to the siding.
        TilePainter.Input odd = yardAt(depot(), 4, 4, 12, 12);
        railRow(odd, 10, 0);
        int[] oimg = raster(odd, paint(odd), px);
        int oline = (int) Math.round((10 + 0.5) * px), ostart = 4 * px + 1, ofanA = ostart + (int) Math.round(TileRaster.YARD_LADDER * px), bare = 0;
        for (int x = ostart + 4; x < ofanA - 4; x++) {
            if (oimg[(oline - 1) * w + x] != TileRaster.STEEL || oimg[(oline + 1) * w + x] != TileRaster.STEEL) bare++;
        }
        check("the middle siding of a depot's three, on the line, has its two rails the whole length of its ladder: " + bare + " columns without", bare == 0);
    }

    /* ================= 27. a yard longer than a tile, drawn whole (FX5 Y2) ================= */

    /** Which rows of pixel column `col` hold `colour`, in an image `width` across and `height` down. */
    static boolean[] colRows(int[] img, int width, int height, int col, int colour) {
        boolean[] rows = new boolean[height];
        for (int y = 0; y < height; y++) rows[y] = img[y * width + col] == colour;
        return rows;
    }

    /**
     * The tiles an east-west yard reaches, as CityMap.fillInput() builds them
     * (FX5 Y2): the yard's box starts at plot (boxX, boxY) of tile 0 and is
     * `along` plots along and `across` across, its line the plot row
     * `lineRow`. Every tile it reaches holds the yard whole (Input.yards: its
     * layout from yardLayout() of the box's track, as the routes lay it) and
     * its clipped part as a building, counted on the first tile only, with
     * the line's track on its row. `entries` false leaves the yards out: the
     * clipped parts alone, as a hand-built yard has always been.
     */
    static TilePainter.Input[] yardTiles(BuildingVisual.Type terminal, int boxX, int boxY, int along, int across, int lineRow, boolean entries) {
        boolean[] rail = new boolean[along * across];
        for (int x = 0; x < along; x++) rail[(lineRow - boxY) * along + x] = true;
        int[] layout = TileRaster.yardLayout(rail, along, across);
        int n = (boxX + along + T - 1) / T;
        TilePainter.Input[] tiles = new TilePainter.Input[n];
        for (int i = 0; i < n; i++) {
            TilePainter.Input in = owned();
            in.tx = i;
            in.types = new BuildingVisual.Type[terminal.id() + 1];
            in.types[terminal.id()] = terminal;
            in.counts = new int[terminal.id() + 1];
            int x0 = boxX - i * T, cx0 = Math.max(0, x0), cx1 = Math.min(T, x0 + along);
            int box = CityMap.box(cx0, boxY, cx1 - cx0, across, false);
            if (i == 0) in.addBuilding(box, terminal.id());
            else in.addBuildingPart(box, terminal.id());
            if (entries) in.addYard(x0, boxY, along, across, terminal.id(), layout, false);
            railRow(in, lineRow, 0);
            tiles[i] = in;
        }
        return tiles;
    }

    /** The tiles rastered alone at px and laid side by side: tiles.length * T * px across, T * px down. */
    static int[] sideBySide(TilePainter.Input[] tiles, int px) {
        int w = T * px, n = tiles.length;
        int[] all = new int[n * w * w];
        for (int i = 0; i < n; i++) {
            int[] img = raster(tiles[i], paint(tiles[i]), px);
            for (int y = 0; y < w; y++) System.arraycopy(img, y * w, all, y * n * w + i * w, w);
        }
        return all;
    }

    /** How many 4-connected pieces the pixels of colours a and b make in an image `width` across. */
    static int pieces(int[] img, int width, int a, int b) {
        int height = img.length / width, n = 0;
        boolean[] seen = new boolean[img.length];
        int[] stack = new int[img.length];
        for (int s = 0; s < img.length; s++) {
            if (seen[s] || img[s] != a && img[s] != b) continue;
            n++;
            int top = 0;
            stack[top++] = s;
            seen[s] = true;
            while (top > 0) {
                int at = stack[--top], x = at % width, y = at / width;
                int[] next = { x > 0 ? at - 1 : -1, x < width - 1 ? at + 1 : -1, y > 0 ? at - width : -1, y < height - 1 ? at + width : -1 };
                for (int q : next) {
                    if (q < 0 || seen[q] || img[q] != a && img[q] != b) continue;
                    seen[q] = true;
                    stack[top++] = q;
                }
            }
        }
        return n;
    }

    /** A track's bed in a pixel column: its ballast rows and the steel rows among them. */
    static boolean[] bedRows(int[] img, int width, int height, int col) {
        boolean[] ballast = colRows(img, width, height, col, TileRaster.BALLAST), steel = colRows(img, width, height, col, TileRaster.STEEL);
        for (int y = 0; y < height; y++) ballast[y] |= steel[y];
        return ballast;
    }

    /** How many runs of true the column of rows has. */
    static int runs(boolean[] rows) {
        int n = 0;
        for (int y = 0; y < rows.length; y++) if (rows[y] && (y == 0 || !rows[y - 1])) n++;
        return n;
    }

    /* ================= 28. a depot drawn whole at its own footprint, on its line with no track yet (CL4; threw on 0.8.18) ================= */

    static void depotWithNoLine() {
        System.out.println("--- 28. (CL4; threw on 0.8.18) a Rail Depot drawn whole at its own footprint on its line, no track yet: its heaps stay in its picture ---");
        BuildingVisual.Type depot = depot();
        int[] fd = BuildingVisual.footprint(depot);
        int boxX = 6, boxY = 10;
        // As CityMap.fillInput() handed the default playtest's depot (m4003) to its tile: the box whole in Input.yards, laid on its line
        // at its last row (as CityRuns lays a depot, section 23) - the layout yardLayout() gives that line - and no track drawn yet.
        boolean[] line = new boolean[fd[0] * fd[1]];
        for (int x = 0; x < fd[0]; x++) line[(fd[1] - 1) * fd[0] + x] = true;
        int[] layout = TileRaster.yardLayout(line, fd[0], fd[1]);
        for (int px : new int[] { TileRaster.LINES_FROM, TileRaster.JUNCTIONS_FROM, 8 }) depotAt(depot, fd, boxX, boxY, layout, px);
    }

    /** Section 28's depot drawn at px, on a thread that drew no yard before. */
    static void depotAt(BuildingVisual.Type depot, int[] fd, int boxX, int boxY, int[] layout, int px) {
        int w = T * px;
        TilePainter.Input in = yardAt(depot, boxX, boxY, fd[0], fd[1]);
        in.addYard(boxX, boxY, fd[0], fd[1], depot.id(), layout, false);
        // On a thread of its own: a thread's yard canvas (TileRaster.yardCanvas()) is only as large as the largest yard it drew, so
        // this one's is exactly the depot's picture, as on a worker that draws it first - where 0.8.18 threw.
        int[][] got = new int[1][];
        String[] threwOn = new String[1];
        Thread t = new Thread(() -> {
            try {
                got[0] = raster(in, paint(in), px);
            } catch (RuntimeException e) {
                threwOn[0] = e.toString();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        int[] img = got[0];
        String threw = threwOn[0];
        check("the fixture at " + px + " px: the depot's own footprint " + fd[0] + " x " + fd[1] + ", east-west on its line at its last row (layout "
                + Arrays.toString(layout) + "), drawn whole on a thread that drew no yard before", layout[0] == 1 && layout[1] == fd[1] - 1 && in.yards == 1);
        check("...it is drawn without throwing" + (threw == null ? "" : " (" + threw + ")"), threw == null);
        check("...as a yard: YARD_GROUND inside its box", img != null && hasInBox(img, w, boxX, boxY, fd[0], fd[1], px, TileRaster.YARD_GROUND));
    }

    static void yardAcrossTiles() {
        System.out.println("--- 27. (FX5 Y2) a yard longer than a tile is drawn as one: one fan, ladders at its two ends, one shed ---");
        BuildingVisual.Type terminal = terminal();
        int px = 8, w = T * px, along = CityRuns.YARD_LONG, across = 24, boxY = 4, lineRow = 16, ladder = TileRaster.YARD_LADDER * px;
        check("the fixture's yard is longer than a tile: YARD_LONG " + along + " plots against a tile's " + T, along > T);
        for (int boxX : new int[] { 1, 24 }) {
            TilePainter.Input[] tiles = yardTiles(terminal, boxX, boxY, along, across, lineRow, true);
            int n = tiles.length, width = n * w;
            int[] all = sideBySide(tiles, px);
            String where = "a yard from plot " + boxX;
            check(where + " reaches " + (boxX == 1 ? 2 : 3) + " tiles, each holding its part as a building: " + n, n == (boxX == 1 ? 2 : 3));
            // The fan's straight middle is the reference: YARD_TRACKS ballast bands, and the rows of steel the sidings' rails make.
            int midCol = boxX * px + along * px / 2;
            boolean[] ballast = colRows(all, width, w, midCol, TileRaster.BALLAST), steel = colRows(all, width, w, midCol, TileRaster.STEEL);
            int bands = runs(bedRows(all, width, w, midCol));
            check(where + ": the fan's middle has YARD_TRACKS ballast bands: " + bands, bands == TileRaster.YARD_TRACKS);
            for (int s = 1; s < n; s++) {
                int seam = s * w, bad = 0, badSteel = 0, cut = 0;
                for (int col = seam - ladder; col < seam + ladder; col++) {
                    if (!Arrays.equals(colRows(all, width, w, col, TileRaster.BALLAST), ballast)) bad++;
                    if (!Arrays.equals(colRows(all, width, w, col, TileRaster.STEEL), steel)) badSteel++;
                }
                for (int y = 0; y < w; y++) {
                    if (ballast[y] != (all[y * width + seam - 1] == TileRaster.BALLAST) || ballast[y] != (all[y * width + seam] == TileRaster.BALLAST)) cut++;
                }
                check(where + ", seam " + s + ": every siding's ballast band lies at the same rows on both sides and runs across it: "
                        + bad + " of " + 2 * ladder + " columns off, " + cut + " rows cut", bad == 0 && cut == 0);
                check("...and no steel but the sidings' rails within YARD_LADDER plots of it, no ladder at a seam: "
                        + badSteel + " of " + 2 * ladder + " columns off", badSteel == 0);
            }
            // The two ends: a ladder at the box's west end and at its east end, one ballast band by the apex, not the fan's eight.
            int west = boxX * px + 1 + 2, east = (boxX + along) * px - 1 - 2;
            check(where + ": a ladder at the box's west end (fewer ballast bands by the apex than the fan's)",
                    runs(bedRows(all, width, w, west)) < TileRaster.YARD_TRACKS);
            check("...and at its east end", runs(bedRows(all, width, w, east)) < TileRaster.YARD_TRACKS);
            // One shed and one set of stockpiles, not a set a tile.
            int shed = pieces(all, width, terminal.fill(), terminal.edge());
            int heaps = pieces(all, width, TileRaster.ORE_HEAP, TileRaster.blend(TileRaster.ORE_HEAP, 0xff000000, 0.25));
            check(where + ": one shed, not one a tile: " + shed, shed == 1);
            check("...and three stockpiles, not three a tile: " + heaps, heaps == 3);
            // The part on a later tile is yard ground, not the plain track the tile alone would show.
            boolean groundOnLater = true;
            for (int i = 1; i < n; i++) {
                int[] tileImg = raster(tiles[i], paint(tiles[i]), px);
                int x0 = Math.max(0, boxX - i * T), x1 = Math.min(T, boxX + along - i * T);
                groundOnLater &= hasInBox(tileImg, w, x0, boxY, x1 - x0, across, px, TileRaster.YARD_GROUND);
            }
            check(where + ": the part on each later tile is yard ground", groundOnLater);
        }

        // The control: a 24 x 24 yard inside one tile draws to the pixel as it does with no entry, and as section 12 asserts.
        int boxX = 4, railY = 16;
        TilePainter.Input[] one = yardTiles(terminal, boxX, boxY, 24, 24, railY, true), bare = yardTiles(terminal, boxX, boxY, 24, 24, railY, false);
        int[] with = sideBySide(one, px), without = sideBySide(bare, px);
        check("control: a 24 x 24 yard inside one tile is drawn to the pixel as with no entry", one.length == 1 && Arrays.equals(with, without));
        check("...with YARD_GROUND, STEEL on YARD_TRACKS rows and the shed, as section 12 asserts",
                hasInBox(with, w, boxX, boxY, 24, 24, px, TileRaster.YARD_GROUND)
                        && distinctRows(with, w, boxX, boxY, 24, 24, px, TileRaster.STEEL) >= TileRaster.YARD_TRACKS
                        && hasInBox(with, w, boxX, boxY, 24, 24, px, terminal.fill()));

        // The line that runs into the yard is drawn to its box, not left a dead end short of it: the plot before the box (the box from plot 1, the line along its row)
        // has its bed and rails to the edge it shares with the box, where the yard's ground begins (a gap there is where the trains' vehicles would run off the rail).
        TilePainter.Input[] into = yardTiles(terminal, 1, boxY, along, across, lineRow, true);
        int[] intoImg = raster(into[0], paint(into[0]), px);
        boolean reachesBox = false;
        for (int dy = 0; dy < px; dy++) {
            int c = pixel(intoImg, w, 0, lineRow, px - 1, dy, px);
            reachesBox |= c == TileRaster.BALLAST || c == TileRaster.STEEL;
        }
        check("the line meets the yard's box: the plot before the box has its bed to the edge it shares with it", reachesBox);

        // The stamp folds the yards: a tile repaints when its yard's layout moves.
        TilePainter.Input a = yardTiles(terminal, 1, boxY, along, across, lineRow, true)[1], b = yardTiles(terminal, 1, boxY, along, across, lineRow, true)[1];
        check("the stamp: the same yards, the same stamp", MapTiles.stamp(a) == MapTiles.stamp(b));
        b.yard[6] += 1;
        check("...a yard's line moved, another stamp", MapTiles.stamp(a) != MapTiles.stamp(b));
        b.yard[6] -= 1;
        b.yard[2] += 1;
        check("...a yard's box longer, another stamp", MapTiles.stamp(a) != MapTiles.stamp(b));
        b.yard[2] -= 1;
        b.yard[9] = 1;
        check("...the yard the freight station, another stamp", MapTiles.stamp(a) != MapTiles.stamp(b));
        b.yard[9] = 0;
        TilePainter.Input copy = new TilePainter.Input();
        MapTiles.copy(a, copy);
        check("...and a kept copy of the tile has its yards and buildings as the tile had them, counted as it counted them",
                MapTiles.stamp(copy) == MapTiles.stamp(a) && copy.yards == a.yards && copy.buildings == a.buildings && Arrays.equals(copy.counts, a.counts));

        // The map: every tile a yard of the skeleton city reaches is given the yard whole, in the layout the routes lay, counted on the tile holding its first plot only.
        CityMap m = skeletonCity();
        List<long[]> yards = m.runs().yards();
        int longer = 0, wholeOn = 0, reached = 0, layouts = 0, counted = 0, parts = 0, blockPlots = 0, blockDrawn = 0;
        for (int yi = 0; yi < yards.size(); yi++) {
            long[] yd = yards.get(yi);
            int type = m.yardTypeOf(yi);
            if (type < 0) continue;
            int[] layout = TileRaster.yardLayout(CityMap.yardRail(m.runs(), yd), (int) yd[2], (int) yd[3]);
            long tx0 = Math.floorDiv(yd[0], T), tx1 = Math.floorDiv(yd[0] + yd[2] - 1, T), ty0 = Math.floorDiv(yd[1], T), ty1 = Math.floorDiv(yd[1] + yd[3] - 1, T);
            if (tx1 > tx0 || ty1 > ty0) longer++;
            for (long ty = ty0; ty <= ty1; ty++) {
                for (long tx = tx0; tx <= tx1; tx++) {
                    TilePainter.Input in = new TilePainter.Input();
                    m.tileInput(tx, ty, in);
                    reached++;
                    boolean found = false;
                    for (int k = 0; k < in.yards; k++) {
                        int o = k * TilePainter.Input.YARD_INTS;
                        if (in.yard[o] != yd[0] - tx * T || in.yard[o + 1] != yd[1] - ty * T || in.yard[o + 2] != yd[2] || in.yard[o + 3] != yd[3] || in.yard[o + 4] != type) continue;
                        found = true;
                        if (in.yard[o + 5] == layout[0] && in.yard[o + 6] == layout[1] && in.yard[o + 7] == layout[2] && in.yard[o + 8] == layout[3]) layouts++;
                    }
                    if (found) wholeOn++;
                    TilePainter.Painted p = paint(in);
                    int cx0 = (int) Math.max(0, yd[0] - tx * T), cy0 = (int) Math.max(0, yd[1] - ty * T);
                    int cx1 = (int) Math.min(T, yd[0] + yd[2] - tx * T), cy1 = (int) Math.min(T, yd[1] + yd[3] - ty * T);
                    boolean every = true;
                    for (int y = cy0; y < cy1; y++) {
                        for (int x = cx0; x < cx1; x++) every &= p.use[y * T + x] == TilePainter.BUILDING && p.bld[y * T + x] > 0 && p.btype[p.bld[y * T + x] - 1] == type;
                    }
                    if (every) parts++;
                    // ...and the blocks' views draw it there as the yard's class: a tile's part not under ground colour alone (L1, 2 px a plot, 4-plot blocks).
                    int[] blk = blocks(in, p, 2, 4);
                    for (int y = cy0; y < cy1; y++) {
                        for (int x = cx0; x < cx1; x++) {
                            blockPlots++;
                            if (blk[(y * 2) * T * 2 + x * 2] != TileRaster.groundColour(in, p, y * T + x)) blockDrawn++;
                        }
                    }
                    if (yd[0] >= tx * T && yd[1] >= ty * T && yd[0] < (tx + 1) * T && yd[1] < (ty + 1) * T && in.counts[type] > 0) counted++;
                }
            }
        }
        check("the skeleton city has a yard that reaches more than one tile, so the fixture causes the condition: " + longer + " of " + yards.size(), longer > 0);
        check("the map gives every tile a yard reaches the yard whole, its box in the tile's plots: " + wholeOn + " of " + reached, reached > 0 && wholeOn == reached);
        check("...in the layout the trains' routes lay (yardLayout() of yardRail()): " + layouts + " of " + reached, reached > 0 && layouts == reached);
        check("...with its part there a building on every plot: " + parts + " of " + reached, reached > 0 && parts == reached);
        check("...drawn in the blocks' views as the yard's class on every plot of every part: " + blockDrawn + " of " + blockPlots + " plots", blockPlots > 0 && blockDrawn == blockPlots);
        check("...and counted on the tile holding its first plot: " + counted + " of " + yards.size(), counted == yards.size());
    }
}
