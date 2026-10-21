# RailDrawCheck.java - 1,585 lines · 71 methods · 2 constants · harnesses

`ham/citybuildersim/RailDrawCheck.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

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
> ... (46 more lines in the source)

**Uses:** [TileRaster](TileRaster.md) (172), [TilePainter](TilePainter.md) (129), [BuildingVisual](BuildingVisual.md) (63), [TrackCurves](TrackCurves.md) (33), [MapTiles](MapTiles.md) (27), [LandMap](LandMap.md) (17), [CityMap](CityMap.md) (16), [CityRuns](CityRuns.md) (10), [MapFrame](MapFrame.md) (8), [World](World.md) (4), [Founding](Founding.md) (4), [BuildingCatalog](BuildingCatalog.md) (3), [DistrictPlan](DistrictPlan.md) (3), [BuildingType](BuildingType.md) (3), [TrainSchedule](TrainSchedule.md) (2), [MapCheck](MapCheck.md) (1), [Resource](Resource.md) (1), [CityShore](CityShore.md) (1)

## Sections

| line | section |
|---:|---|
| 353 | 1. a straight east-west single track at 8 px |
| 375 | 2. M_MAIN \| M_DOUBLE, and M_MAIN alone |
| 403 | 3. a north-south track at 8 px |
| 424 | 4. at 4 px |
| 441 | 5. L1 blocks |
| 474 | 6. L2 blocks |
| 493 | 7. a level crossing |
| 511 | 8. an arterial over the main line |
| 547 | 9. unowned track |
| 561 | 10. a diagonal plot |
| 585 | 11. the eleventh class |
| 602 | 12. a bulk yard at 8 px over an east-west track |
| 622 | 13. the freight station |
| 658 | 14. wagons standing |
| 701 | 15. a north-south line through the yard |
| 713 | 16. at 4 px, and below LINES_FROM |
| 738 | 17. the stamp folds the yard |
| 760 | 18. the rail picture's repaint |
| 781 | 19. the far skeleton |
| 854 | 20. the legend |
| 882 | 22. gravel drawn grey (FR7 2.3) |
| 924 | 23. the Rail Depot: a small yard (FR7 slice 3) |
| 973 | 24. a siding's loading shed (FR7 slice 3) |
| 997 | 21. a line crossing the yard's box |
| 1051 | 25. (FX5) a corner is drawn as a curve |
| 1311 | 26. (FX5) a yard's ladders are S curves |
| 1355 | 27. a yard longer than a tile, drawn whole (FX5 Y2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 125 | `RailDrawCheck.T` | `TilePainter.TILE` |  |
| 128 | `RailDrawCheck.TYPES` | `BuildingVisual.table(new BuildingCatalog().load())` | The game's building templates, as the map draws them. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 118 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 116 | 1470 | **type** `public class RailDrawCheck` | Proves that the railway is drawn as rail, not as a cased road band (0.8.1, batch FR0; docs/studies/spec-freight-and-infrastructure.md 4.3, D15, D18). |
| 120 | 4 | `static void check(String label, boolean ok)` |  |
| 131 | 4 | `static BuildingVisual.Type terminal()` | The Rail Terminal type. |
| 137 | 6 | `static int[] raster(TilePainter.Input in, TilePainter.Painted p, int px)` | The image a painted tile rasters to at px pixels a plot. |
| 145 | 6 | `static int[] blocks(TilePainter.Input in, TilePainter.Painted p, int px, int block)` | The image a painted tile's blocks raster to at px pixels a plot. |
| 153 | 5 | `static TilePainter.Input owned()` | A tile whose plots are all owned, with no buildings and no sites. |
| 160 | 5 | `static TilePainter.Painted paint(TilePainter.Input in)` | Paints `in` into a fresh Painted. |
| 167 | 6 | `static void railRow(TilePainter.Input in, int y, int marks)` | Lays a row of track, each plot with `marks` (0 for none). |
| 175 | 6 | `static void railColumn(TilePainter.Input in, int x, int marks)` | Lays a column of track, each plot with `marks` (0 for none). |
| 183 | 3 | `static void streetColumn(TilePainter.Input in, int x, int code)` | Sets a column of street plots to `code`. |
| 188 | 3 | `static int pixel(int[] img, int w, int plotX, int plotY, int dx, int dy, int px)` | One pixel of plot (plotX, plotY), its local (dx, dy) at px pixels a plot. |
| 193 | 3 | `static int bedWidth(int px, boolean doubled)` | The bed width the dials give at px pixels a plot. |
| 198 | 9 | `static boolean[] rowsAll(int[] img, int w, int plotX, int plotY, int px, int colour)` | The rows of a plot that are wholly `colour`, in dy order. |
| 209 | 9 | `static boolean[] colsAll(int[] img, int w, int plotX, int plotY, int px, int colour)` | The columns of a plot that are wholly `colour`, in dx order. |
| 219 | 5 | `static int count(boolean[] a)` |  |
| 226 | 8 | `static TilePainter.Input yardAt(BuildingVisual.Type terminal, int boxX, int boxY, int boxW, int boxH)` | A hand-built yard tile: the Rail Terminal's box, on owned ground. |
| 236 | 6 | `static boolean hasInBox(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | Whether any pixel in the box is `colour`. |
| 244 | 8 | `static boolean hasOnSide(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour, int line, boolean above)` | Whether any pixel in the box on the chosen side of `line` is `colour`. |
| 254 | 9 | `static int distinctRows(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | How many distinct pixel rows in the box have a pixel of `colour`. |
| 265 | 9 | `static int distinctCols(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | How many distinct pixel columns in the box have a pixel of `colour`. |
| 276 | 11 | `static boolean[] rowsWith(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | The box's pixel rows, relative to its top, that have a pixel of `colour`. |
| 289 | 20 | `static int[] bedBands(boolean[] rows)` | A fan's beds from its BALLAST rows: the two ballast rows of each track are one rail apart, the tracks a gap apart. |
| 311 | 9 | `static boolean bandHas(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour, int start, int end)` | Whether any pixel in rows start..end (relative to the box's top) is `colour`. |
| 321 | 31 | `public static void main(String[] args)` |  |

### 1. a straight east-west single track at 8 px (lines 353-374)

| line | len | member | says |
|---:|---:|---|---|
| 354 | 20 | `static void straightAtEight()` |  |

### 2. M_MAIN | M_DOUBLE, and M_MAIN alone (lines 375-402)

| line | len | member | says |
|---:|---:|---|---|
| 376 | 26 | `static void doubleMainAtEight()` |  |

### 3. a north-south track at 8 px (lines 403-423)

| line | len | member | says |
|---:|---:|---|---|
| 404 | 19 | `static void northSouthAtEight()` |  |

### 4. at 4 px (lines 424-440)

| line | len | member | says |
|---:|---:|---|---|
| 425 | 15 | `static void atFour()` |  |

### 5. L1 blocks (lines 441-473)

| line | len | member | says |
|---:|---:|---|---|
| 442 | 31 | `static void blocksMiddle()` |  |

### 6. L2 blocks (lines 474-492)

| line | len | member | says |
|---:|---:|---|---|
| 475 | 17 | `static void blocksFar()` |  |

### 7. a level crossing (lines 493-510)

| line | len | member | says |
|---:|---:|---|---|
| 494 | 16 | `static void levelCrossing()` |  |

### 8. an arterial over the main line (lines 511-546)

| line | len | member | says |
|---:|---:|---|---|
| 512 | 34 | `static void arterialBridge()` |  |

### 9. unowned track (lines 547-560)

| line | len | member | says |
|---:|---:|---|---|
| 548 | 12 | `static void unownedTrack()` |  |

### 10. a diagonal plot (lines 561-584)

| line | len | member | says |
|---:|---:|---|---|
| 562 | 22 | `static void diagonalTrack()` |  |

### 11. the eleventh class (lines 585-601)

| line | len | member | says |
|---:|---:|---|---|
| 586 | 15 | `static void eleventhClass()` |  |

### 12. a bulk yard at 8 px over an east-west track (lines 602-621)

| line | len | member | says |
|---:|---:|---|---|
| 603 | 18 | `static void bulkYardAtEight()` |  |

### 13. the freight station (lines 622-657)

| line | len | member | says |
|---:|---:|---|---|
| 623 | 34 | `static void freightStation()` |  |

### 14. wagons standing (lines 658-700)

| line | len | member | says |
|---:|---:|---|---|
| 659 | 41 | `static void wagonsStanding()` |  |

### 15. a north-south line through the yard (lines 701-712)

| line | len | member | says |
|---:|---:|---|---|
| 702 | 10 | `static void northSouthYard()` |  |

### 16. at 4 px, and below LINES_FROM (lines 713-737)

| line | len | member | says |
|---:|---:|---|---|
| 714 | 23 | `static void atFourAndBelow()` |  |

### 17. the stamp folds the yard (lines 738-759)

| line | len | member | says |
|---:|---:|---|---|
| 739 | 20 | `static void stampFoldsTheYard()` |  |

### 18. the rail picture's repaint (lines 760-780)

| line | len | member | says |
|---:|---:|---|---|
| 761 | 6 | `static CityMap railCity()` |  |
| 768 | 12 | `static void railPictureRepaint()` |  |

### 19. the far skeleton (lines 781-853)

| line | len | member | says |
|---:|---:|---|---|
| 784 | 21 | `static CityMap skeletonCity()` | A square city on a dry place whose square holds iron, with highways, a freight line and yards: the far skeleton's fixture. |
| 807 | 4 | `static boolean hasColour(int[] img, int colour)` | Whether any pixel is exactly `colour`. |
| 812 | 41 | `static void farSkeleton()` |  |

### 20. the legend (lines 854-881)

| line | len | member | says |
|---:|---:|---|---|
| 857 | 4 | `static boolean legendHas(List<LandMap.Entry> entries, String words)` | Whether the legend has an entry with these words. |
| 862 | 19 | `static void legend()` |  |

### 22. gravel drawn grey (FR7 2.3) (lines 882-923)

| line | len | member | says |
|---:|---:|---|---|
| 883 | 23 | `static void gravelGrey()` |  |
| 908 | 3 | `static double luma(int c)` | A colour's luma, 0 to 255 (Rec. |
| 913 | 4 | `static double distance(int a, int c)` | Two colours' distance in RGB. |
| 919 | 4 | `static boolean nearInRow(int[] img, int w, int y, int x, int reach, int colour)` | Whether pixel row y has a pixel of `colour` within `reach` pixels of column x. |

### 23. the Rail Depot: a small yard (FR7 slice 3) (lines 924-972)

| line | len | member | says |
|---:|---:|---|---|
| 927 | 4 | `static BuildingVisual.Type depot()` | The Rail Depot type: the yard FR7 added as the railway's small first rung. |
| 932 | 40 | `static void railDepotYard()` |  |

### 24. a siding's loading shed (FR7 slice 3) (lines 973-996)

| line | len | member | says |
|---:|---:|---|---|
| 975 | 21 | `static void sidingShed()` |  |

### 21. a line crossing the yard's box (lines 997-1050)

| line | len | member | says |
|---:|---:|---|---|
| 998 | 52 | `static void lineCrossingTheYard()` |  |

### 25. (FX5) a corner is drawn as a curve (lines 1051-1310)

| line | len | member | says |
|---:|---:|---|---|
| 1054 | 3 | `static boolean isTrack(int c)` | Whether a pixel is the railway's: ballast, a sleeper or steel. |
| 1059 | 7 | `static TilePainter.Input lOfTrack(int x0, int rowY, int colX, int y1, int marks)` | An L of single track on owned ground, as the runs lay it: east along row `rowY` from column `x0` to column `colX`, then south down that column to row `y1`, each plot with `marks`. |
| 1068 | 6 | `static List<TrackCurves.Bend> bendInto(TilePainter.Input in, double[] xs, double[] ys, boolean doubled)` | The bends of the polyline through the points, put into the input as CityMap puts a route's in, and the plots they replace marked. |
| 1076 | 5 | `static double pathDistance(double[][] path, int px, int x, int y)` | The distance, in pixels, from the middle of pixel (x, y) to the nearest of the path's points (in plots, scaled by px). |
| 1083 | 12 | `static double[][] bentPath(TrackCurves.Bend b, double xa, double ya, double xb, double yb)` | An L's centreline bent by `b`, sampled every 1/50 plot: from plot edge (xa, ya) to the arc's start, the arc, and from its end on to plot edge (xb, yb). |
| 1096 | 168 | `static void curvedCorner()` |  |
| 1266 | 38 | `static void trackCurvesRules()` | TrackCurves' own rules, the geometry the drawing and the routes share: the radius, the shrinking, the arcs' tangency and the ladder's S. |
| 1306 | 4 | `static boolean anyCurved(TilePainter.Input in)` | Whether any plot of the input is marked curved. |

### 26. (FX5) a yard's ladders are S curves (lines 1311-1354)

| line | len | member | says |
|---:|---:|---|---|
| 1313 | 41 | `static void yardLadders()` |  |

### 27. a yard longer than a tile, drawn whole (FX5 Y2) (lines 1355-1585)

| line | len | member | says |
|---:|---:|---|---|
| 1358 | 5 | `static boolean[] colRows(int[] img, int width, int height, int col, int colour)` | Which rows of pixel column `col` hold `colour`, in an image `width` across and `height` down. |
| 1374 | 22 | `static TilePainter.Input[] yardTiles(BuildingVisual.Type terminal, int boxX, int boxY, int along, int across, int lineRow, bool...` | The tiles an east-west yard reaches, as CityMap.fillInput() builds them (FX5 Y2): the yard's box starts at plot (boxX, boxY) of tile 0 and is `along` plots along and `across` across, its line the plot row `lineRow`. |
| 1398 | 9 | `static int[] sideBySide(TilePainter.Input[] tiles, int px)` | The tiles rastered alone at px and laid side by side: tiles.length * T * px across, T * px down. |
| 1409 | 22 | `static int pieces(int[] img, int width, int a, int b)` | How many 4-connected pieces the pixels of colours a and b make in an image `width` across. |
| 1433 | 5 | `static boolean[] bedRows(int[] img, int width, int height, int col)` | A track's bed in a pixel column: its ballast rows and the steel rows among them. |
| 1440 | 5 | `static int runs(boolean[] rows)` | How many runs of true the column of rows has. |
| 1446 | 139 | `static void yardAcrossTiles()` |  |

