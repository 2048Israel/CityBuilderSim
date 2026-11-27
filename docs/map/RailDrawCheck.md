# RailDrawCheck.java - 1,983 lines · 82 methods · 4 constants · harnesses

`ham/citybuildersim/RailDrawCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> Proves that the railway is drawn as rail, not as a cased road band (0.8.1,
> batch FR0; docs/studies/spec-freight-and-infrastructure.md 4.3, D15, D18).
> Not part of the game.
> 
> WHY THIS EXISTS. Until 0.8.1 TileRaster.track() drew the railway as a paved
> road's band, cased grey with white dashes, so it read as a road. This draws
> it as ballast, sleepers and steel rails at the close views, a dark line
> with cross-ticks in the blocks, and the crossings as level crossings or a
> road bridge over the main line.
> 
> What it has to prove, a section each, every one a fixture that sets the
> condition by hand:
> 
>   1. a straight east-west single track at 8 px has a BALLAST bed, two STEEL
>      rows at the offsets the dials give, and SLEEPER columns every
>      max(2, round(SLEEPERS_EVERY * px));
>   2. the same plots marked M_MAIN | M_DOUBLE have four STEEL rows and a bed
>      as wide as BED_DOUBLE; M_MAIN alone stays single track;
>   3. a north-south track is the same, by columns;
>   4. at 4 px the track is a BALLAST bed, one STEEL row down its middle and
>      one SLEEPER tick a plot;
>   5. L1 blocks draw the track in RAIL_DARK, never RAIL_LINE, with tick
>      pixels beside the line at every block and none between;
>   6. L2 blocks draw RAIL_DARK and no tick pixels beside the line;
>   7. a paved street crossing a single track at level has CROSSBUCK pixels
>      in the corners, BARRIER pixels on the street's surface at its edges,
>      and STEEL pixels on the surface;
>   8. an arterial crossing a main-line plot is a bridge: DECK along the
>      street's edges, no CROSSBUCK, BARRIER or STEEL on the surface, and
>      LandMap.plotWords says "bridging the railway" there and "crossing the
>      railway" at a level crossing;
>   9. an unowned track's bed pixel is blend(BALLAST, VOID, UNOWNED_DIM);
>  10. a diagonal plot has BALLAST and STEEL pixels, and no SLEEPER;
>  11. the eleventh class: RAIL, PORTS and INFRASTRUCTURE are TRANSPORT,
>      CLASSES and the class tables all have eleven entries, and a Rail
>      Terminal has TRANSPORT's fill;
>  12. a bulk yard at 8 px over an east-west track has YARD_GROUND, STEEL on
>      at least YARD_TRACKS rows, its shed on one side of the line and its
>      stockpiles on the other, and no containers;
>  13. the freight station with goods is a container yard; the same yard not
>      the station, and the station without goods, stay bulk;
>  14. wagonTracks 0 draws no WAGON, YARD_TRACKS draws a wagon on every
>      track's bed but one (FX5: a yard keeps a siding clear for a train),
>      and half of YARD_TRACKS on that many tracks only;
>  15. a north-south line through the yard gives a north-south fan;
>  16. at 4 px the yard still draws as a yard; below LINES_FROM it is a plain
>      TRANSPORT box;
>  17. MapTiles.stamp changes when stationX, stationY, goodsByRail or
>      wagonTracks changes, and not otherwise;
>  18. CityMap.railPicture repaints when goodsByRail or the whole wagon
>      tracks change, and not when the share moves less than a track's worth.
>  19. the far skeleton: MapTiles.nodePixels over a node covering a city with
>      highways, track and yards draws the lightened highway colour and
>      RAIL_DARK, never RAIL_LINE, and a 2 x 2 TRANSPORT dot at each yard;
>  20. the legend: planeLegend() has the transport class, the railway's four
>      and the gateway, and (0.8.2) the train's entry, LandMap.TRAIN_WORDS,
>      and no RAIL_LINE entry, and (FX5) the street with no road bought named
>      "Unmade street", not a second "Track" beside "Railway";
>  21. (batch FX3, 0.8.4; red on 0.8.3) a second line crossing the yard's box:
>      the fan closes on the line the yard stands on, not on the middle of
> ... (52 more lines in the source)

**Uses:** [TileRaster](TileRaster.md) (175), [TilePainter](TilePainter.md) (137), [BuildingVisual](BuildingVisual.md) (69), [CityMap](CityMap.md) (43), [MapTiles](MapTiles.md) (39), [TrackCurves](TrackCurves.md) (35), [World](World.md) (34), [LandMap](LandMap.md) (17), [TrainSchedule](TrainSchedule.md) (14), [MapCheck](MapCheck.md) (13), [CityRuns](CityRuns.md) (10), [MapFrame](MapFrame.md) (8), [DistrictPlan](DistrictPlan.md) (6), [Founding](Founding.md) (5), [BuildingCatalog](BuildingCatalog.md) (3), [BuildingType](BuildingType.md) (3), [JunctionCheck](JunctionCheck.md) (2), [Resource](Resource.md) (1), [CityShore](CityShore.md) (1), [Mining](Mining.md) (1)

## Sections

| line | section |
|---:|---|
| 363 | 1. a straight east-west single track at 8 px |
| 385 | 2. M_MAIN \| M_DOUBLE, and M_MAIN alone |
| 413 | 3. a north-south track at 8 px |
| 434 | 4. at 4 px |
| 451 | 5. L1 blocks |
| 484 | 6. L2 blocks |
| 503 | 7. a level crossing |
| 521 | 8. an arterial over the main line |
| 557 | 9. unowned track |
| 571 | 10. a diagonal plot |
| 595 | 11. the eleventh class |
| 612 | 12. a bulk yard at 8 px over an east-west track |
| 632 | 13. the freight station |
| 668 | 14. wagons standing |
| 711 | 15. a north-south line through the yard |
| 723 | 16. at 4 px, and below LINES_FROM |
| 748 | 17. the stamp folds the yard |
| 770 | 18. the rail picture's repaint |
| 791 | 19. the far skeleton |
| 864 | 20. the legend |
| 892 | 22. gravel drawn grey (FR7 2.3) |
| 934 | 23. the Rail Depot: a small yard (FR7 slice 3) |
| 983 | 24. a siding's loading shed (FR7 slice 3) |
| 1007 | 21. a line crossing the yard's box |
| 1061 | 25. (FX5) a corner is drawn as a curve |
| 1321 | 26. (FX5) a yard's ladders are S curves |
| 1365 | 27. a yard longer than a tile, drawn whole (FX5 Y2) |
| 1456 | 28. a depot drawn whole at its own footprint, on its line with no track yet (CL4; threw on 0.8.18) |
| 1640 | 29. (MF1) a siding that turns a corner is drawn as an arc, and the trains ride it |
| 1856 | 31. (RR1) a plan's siding keeps its train and its arcs past a month |
| 1929 | 30. (MF1) a stub is not drawn: a siding no train fits, a siding whose works is gone |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 131 | `RailDrawCheck.T` | `TilePainter.TILE` |  |
| 134 | `RailDrawCheck.TYPES` | `BuildingVisual.table(new BuildingCatalog().load())` | The game's building templates, as the map draws them. |
| 1643 | `RailDrawCheck.SPURS` | `30, WORKS = 300` | The Rail Spurs and bulk works added to the sidings fixture: 30 and 300, more than MapCheck 8c's 12 and 90, so that several sidings are laid that a unit train fits on and some turn corners. |
| 1859 | `RailDrawCheck.MONTHS_31` | `6` | How many months section 31 plays its siding city on at most (6): one for each type the chain's first district alone holds. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 124 | `static int fails` |  |
| 1647 | `final CityMap map` |  |
| 1648 | `final long[] counts` |  |
| 1692 | `final TilePainter.Input in` |  |
| 1693 | `final int[] img` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 122 | 1862 | **type** `public class RailDrawCheck` | Proves that the railway is drawn as rail, not as a cased road band (0.8.1, batch FR0; docs/studies/spec-freight-and-infrastructure.md 4.3, D15, D18). |
| 126 | 4 | `static void check(String label, boolean ok)` |  |
| 137 | 4 | `static BuildingVisual.Type terminal()` | The Rail Terminal type. |
| 143 | 6 | `static int[] raster(TilePainter.Input in, TilePainter.Painted p, int px)` | The image a painted tile rasters to at px pixels a plot. |
| 151 | 6 | `static int[] blocks(TilePainter.Input in, TilePainter.Painted p, int px, int block)` | The image a painted tile's blocks raster to at px pixels a plot. |
| 159 | 5 | `static TilePainter.Input owned()` | A tile whose plots are all owned, with no buildings and no sites. |
| 166 | 5 | `static TilePainter.Painted paint(TilePainter.Input in)` | Paints `in` into a fresh Painted. |
| 173 | 6 | `static void railRow(TilePainter.Input in, int y, int marks)` | Lays a row of track, each plot with `marks` (0 for none). |
| 181 | 6 | `static void railColumn(TilePainter.Input in, int x, int marks)` | Lays a column of track, each plot with `marks` (0 for none). |
| 189 | 3 | `static void streetColumn(TilePainter.Input in, int x, int code)` | Sets a column of street plots to `code`. |
| 194 | 3 | `static int pixel(int[] img, int w, int plotX, int plotY, int dx, int dy, int px)` | One pixel of plot (plotX, plotY), its local (dx, dy) at px pixels a plot. |
| 199 | 3 | `static int bedWidth(int px, boolean doubled)` | The bed width the dials give at px pixels a plot. |
| 204 | 9 | `static boolean[] rowsAll(int[] img, int w, int plotX, int plotY, int px, int colour)` | The rows of a plot that are wholly `colour`, in dy order. |
| 215 | 9 | `static boolean[] colsAll(int[] img, int w, int plotX, int plotY, int px, int colour)` | The columns of a plot that are wholly `colour`, in dx order. |
| 225 | 5 | `static int count(boolean[] a)` |  |
| 232 | 8 | `static TilePainter.Input yardAt(BuildingVisual.Type terminal, int boxX, int boxY, int boxW, int boxH)` | A hand-built yard tile: the Rail Terminal's box, on owned ground. |
| 242 | 6 | `static boolean hasInBox(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | Whether any pixel in the box is `colour`. |
| 250 | 8 | `static boolean hasOnSide(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour, int line, boolean above)` | Whether any pixel in the box on the chosen side of `line` is `colour`. |
| 260 | 9 | `static int distinctRows(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | How many distinct pixel rows in the box have a pixel of `colour`. |
| 271 | 9 | `static int distinctCols(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | How many distinct pixel columns in the box have a pixel of `colour`. |
| 282 | 11 | `static boolean[] rowsWith(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | The box's pixel rows, relative to its top, that have a pixel of `colour`. |
| 295 | 20 | `static int[] bedBands(boolean[] rows)` | A fan's beds from its BALLAST rows: the two ballast rows of each track are one rail apart, the tracks a gap apart. |
| 317 | 9 | `static boolean bandHas(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour, int start, int end)` | Whether any pixel in rows start..end (relative to the box's top) is `colour`. |
| 327 | 35 | `public static void main(String[] args)` |  |

### 1. a straight east-west single track at 8 px (lines 363-384)

| line | len | member | says |
|---:|---:|---|---|
| 364 | 20 | `static void straightAtEight()` |  |

### 2. M_MAIN | M_DOUBLE, and M_MAIN alone (lines 385-412)

| line | len | member | says |
|---:|---:|---|---|
| 386 | 26 | `static void doubleMainAtEight()` |  |

### 3. a north-south track at 8 px (lines 413-433)

| line | len | member | says |
|---:|---:|---|---|
| 414 | 19 | `static void northSouthAtEight()` |  |

### 4. at 4 px (lines 434-450)

| line | len | member | says |
|---:|---:|---|---|
| 435 | 15 | `static void atFour()` |  |

### 5. L1 blocks (lines 451-483)

| line | len | member | says |
|---:|---:|---|---|
| 452 | 31 | `static void blocksMiddle()` |  |

### 6. L2 blocks (lines 484-502)

| line | len | member | says |
|---:|---:|---|---|
| 485 | 17 | `static void blocksFar()` |  |

### 7. a level crossing (lines 503-520)

| line | len | member | says |
|---:|---:|---|---|
| 504 | 16 | `static void levelCrossing()` |  |

### 8. an arterial over the main line (lines 521-556)

| line | len | member | says |
|---:|---:|---|---|
| 522 | 34 | `static void arterialBridge()` |  |

### 9. unowned track (lines 557-570)

| line | len | member | says |
|---:|---:|---|---|
| 558 | 12 | `static void unownedTrack()` |  |

### 10. a diagonal plot (lines 571-594)

| line | len | member | says |
|---:|---:|---|---|
| 572 | 22 | `static void diagonalTrack()` |  |

### 11. the eleventh class (lines 595-611)

| line | len | member | says |
|---:|---:|---|---|
| 596 | 15 | `static void eleventhClass()` |  |

### 12. a bulk yard at 8 px over an east-west track (lines 612-631)

| line | len | member | says |
|---:|---:|---|---|
| 613 | 18 | `static void bulkYardAtEight()` |  |

### 13. the freight station (lines 632-667)

| line | len | member | says |
|---:|---:|---|---|
| 633 | 34 | `static void freightStation()` |  |

### 14. wagons standing (lines 668-710)

| line | len | member | says |
|---:|---:|---|---|
| 669 | 41 | `static void wagonsStanding()` |  |

### 15. a north-south line through the yard (lines 711-722)

| line | len | member | says |
|---:|---:|---|---|
| 712 | 10 | `static void northSouthYard()` |  |

### 16. at 4 px, and below LINES_FROM (lines 723-747)

| line | len | member | says |
|---:|---:|---|---|
| 724 | 23 | `static void atFourAndBelow()` |  |

### 17. the stamp folds the yard (lines 748-769)

| line | len | member | says |
|---:|---:|---|---|
| 749 | 20 | `static void stampFoldsTheYard()` |  |

### 18. the rail picture's repaint (lines 770-790)

| line | len | member | says |
|---:|---:|---|---|
| 771 | 6 | `static CityMap railCity()` |  |
| 778 | 12 | `static void railPictureRepaint()` |  |

### 19. the far skeleton (lines 791-863)

| line | len | member | says |
|---:|---:|---|---|
| 794 | 21 | `static CityMap skeletonCity()` | A square city on a dry place whose square holds iron, with highways, a freight line and yards: the far skeleton's fixture. |
| 817 | 4 | `static boolean hasColour(int[] img, int colour)` | Whether any pixel is exactly `colour`. |
| 822 | 41 | `static void farSkeleton()` |  |

### 20. the legend (lines 864-891)

| line | len | member | says |
|---:|---:|---|---|
| 867 | 4 | `static boolean legendHas(List<LandMap.Entry> entries, String words)` | Whether the legend has an entry with these words. |
| 872 | 19 | `static void legend()` |  |

### 22. gravel drawn grey (FR7 2.3) (lines 892-933)

| line | len | member | says |
|---:|---:|---|---|
| 893 | 23 | `static void gravelGrey()` |  |
| 918 | 3 | `static double luma(int c)` | A colour's luma, 0 to 255 (Rec. |
| 923 | 4 | `static double distance(int a, int c)` | Two colours' distance in RGB. |
| 929 | 4 | `static boolean nearInRow(int[] img, int w, int y, int x, int reach, int colour)` | Whether pixel row y has a pixel of `colour` within `reach` pixels of column x. |

### 23. the Rail Depot: a small yard (FR7 slice 3) (lines 934-982)

| line | len | member | says |
|---:|---:|---|---|
| 937 | 4 | `static BuildingVisual.Type depot()` | The Rail Depot type: the yard FR7 added as the railway's small first rung. |
| 942 | 40 | `static void railDepotYard()` |  |

### 24. a siding's loading shed (FR7 slice 3) (lines 983-1006)

| line | len | member | says |
|---:|---:|---|---|
| 985 | 21 | `static void sidingShed()` |  |

### 21. a line crossing the yard's box (lines 1007-1060)

| line | len | member | says |
|---:|---:|---|---|
| 1008 | 52 | `static void lineCrossingTheYard()` |  |

### 25. (FX5) a corner is drawn as a curve (lines 1061-1320)

| line | len | member | says |
|---:|---:|---|---|
| 1064 | 3 | `static boolean isTrack(int c)` | Whether a pixel is the railway's: ballast, a sleeper or steel. |
| 1069 | 7 | `static TilePainter.Input lOfTrack(int x0, int rowY, int colX, int y1, int marks)` | An L of single track on owned ground, as the runs lay it: east along row `rowY` from column `x0` to column `colX`, then south down that column to row `y1`, each plot with `marks`. |
| 1078 | 6 | `static List<TrackCurves.Bend> bendInto(TilePainter.Input in, double[] xs, double[] ys, boolean doubled)` | The bends of the polyline through the points, put into the input as CityMap puts a route's in, and the plots they replace marked. |
| 1086 | 5 | `static double pathDistance(double[][] path, int px, int x, int y)` | The distance, in pixels, from the middle of pixel (x, y) to the nearest of the path's points (in plots, scaled by px). |
| 1093 | 12 | `static double[][] bentPath(TrackCurves.Bend b, double xa, double ya, double xb, double yb)` | An L's centreline bent by `b`, sampled every 1/50 plot: from plot edge (xa, ya) to the arc's start, the arc, and from its end on to plot edge (xb, yb). |
| 1106 | 168 | `static void curvedCorner()` |  |
| 1276 | 38 | `static void trackCurvesRules()` | TrackCurves' own rules, the geometry the drawing and the routes share: the radius, the shrinking, the arcs' tangency and the ladder's S. |
| 1316 | 4 | `static boolean anyCurved(TilePainter.Input in)` | Whether any plot of the input is marked curved. |

### 26. (FX5) a yard's ladders are S curves (lines 1321-1364)

| line | len | member | says |
|---:|---:|---|---|
| 1323 | 41 | `static void yardLadders()` |  |

### 27. a yard longer than a tile, drawn whole (FX5 Y2) (lines 1365-1455)

| line | len | member | says |
|---:|---:|---|---|
| 1368 | 5 | `static boolean[] colRows(int[] img, int width, int height, int col, int colour)` | Which rows of pixel column `col` hold `colour`, in an image `width` across and `height` down. |
| 1384 | 22 | `static TilePainter.Input[] yardTiles(BuildingVisual.Type terminal, int boxX, int boxY, int along, int across, int lineRow, bool...` | The tiles an east-west yard reaches, as CityMap.fillInput() builds them (FX5 Y2): the yard's box starts at plot (boxX, boxY) of tile 0 and is `along` plots along and `across` across, its line the plot row `lineRow`. |
| 1408 | 9 | `static int[] sideBySide(TilePainter.Input[] tiles, int px)` | The tiles rastered alone at px and laid side by side: tiles.length * T * px across, T * px down. |
| 1419 | 22 | `static int pieces(int[] img, int width, int a, int b)` | How many 4-connected pieces the pixels of colours a and b make in an image `width` across. |
| 1443 | 5 | `static boolean[] bedRows(int[] img, int width, int height, int col)` | A track's bed in a pixel column: its ballast rows and the steel rows among them. |
| 1450 | 5 | `static int runs(boolean[] rows)` | How many runs of true the column of rows has. |

### 28. a depot drawn whole at its own footprint, on its line with no track yet (CL4; threw on 0.8.18) (lines 1456-1639)

| line | len | member | says |
|---:|---:|---|---|
| 1458 | 12 | `static void depotWithNoLine()` |  |
| 1472 | 28 | `static void depotAt(BuildingVisual.Type depot, int[] fd, int boxX, int boxY, int[] layout, int px)` | Section 28's depot drawn at px, on a thread that drew no yard before. |
| 1501 | 139 | `static void yardAcrossTiles()` |  |

### 29. (MF1) a siding that turns a corner is drawn as an arc, and the trains ride it (lines 1640-1855)

| line | len | member | says |
|---:|---:|---|---|
| 1646 | 5 | **type** `static final class SidingCity` | MapCheck 8c's iron city (his city x 1 where its square holds iron; highways, yards, a freight line, depots) with spurs and bulk works added, and the counts it was last given. |
| 1649 | 1 | `SidingCity(CityMap map, long[] counts)` _(in RailDrawCheck.SidingCity)_ |  |
| 1653 | 36 | `static SidingCity sidingCity(int spurs, int bulk)` | The sidings fixture as MapCheck.sidingsDealt() builds it (accessCity(), with the Squares' big copies left out: seconds, not minutes). |
| 1691 | 8 | **type** `static final class Shot` | A tile's input and its picture at px pixels a plot, as the map builds and paints them. |
| 1694 | 4 | `Shot(MapTiles tiles, long tx, long ty, int px)` _(in RailDrawCheck.Shot)_ |  |
| 1701 | 15 | `static List<List<long[]>> drawnSidingWays(CityMap m)` | Every plan siding the map draws, as the world plots of its way: the railway plot it meets, its corners, its shed - in the districts' order. |
| 1718 | 16 | `static int sidingPlotsOnTiles(CityMap m)` | How many plots of plan sidings the tile inputs of every district carry. |
| 1735 | 120 | `static void sidingsTurnCorners()` |  |

### 31. (RR1) a plan's siding keeps its train and its arcs past a month (lines 1856-1928)

| line | len | member | says |
|---:|---:|---|---|
| 1862 | 5 | `static int planSidingRoutes(CityMap m)` | The trains' routes into the plans' sidings: the works' routes past the runs' own sidings'. |
| 1878 | 50 | `static void sidingsAfterAMonth()` | Jerus, 2026-11-20 (his shot 5, month 1,208): "rail still has dead ends and random 90 degree turns". |

### 30. (MF1) a stub is not drawn: a siding no train fits, a siding whose works is gone (lines 1929-1983)

| line | len | member | says |
|---:|---:|---|---|
| 1931 | 51 | `static void sidingStubs()` |  |

