# RailDrawCheck.java - 1,005 lines · 55 methods · 2 constants · harnesses

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
>      track's bed, and half of YARD_TRACKS on that many tracks only;
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
>      and no RAIL_LINE entry;
>  21. (batch FX3, 0.8.4; red on 0.8.3) a second line crossing the yard's box:
>      the fan closes on the line the yard stands on, not on the middle of
>      all the track in the box; the crossing track is drawn from the box's
>      edge to that line; no siding runs across it.
> ... (13 more lines in the source)

**Uses:** [TileRaster](TileRaster.md) (119), [TilePainter](TilePainter.md) (89), [BuildingVisual](BuildingVisual.md) (60), [CityMap](CityMap.md) (13), [LandMap](LandMap.md) (12), [MapTiles](MapTiles.md) (10), [MapFrame](MapFrame.md) (6), [World](World.md) (4), [Founding](Founding.md) (4), [BuildingCatalog](BuildingCatalog.md) (3), [DistrictPlan](DistrictPlan.md) (3), [CityRuns](CityRuns.md) (3), [BuildingType](BuildingType.md) (3), [TrainSchedule](TrainSchedule.md) (2), [MapCheck](MapCheck.md) (1), [Resource](Resource.md) (1), [CityShore](CityShore.md) (1)

## Sections

| line | section |
|---:|---|
| 317 | 1. a straight east-west single track at 8 px |
| 339 | 2. M_MAIN \| M_DOUBLE, and M_MAIN alone |
| 367 | 3. a north-south track at 8 px |
| 388 | 4. at 4 px |
| 405 | 5. L1 blocks |
| 438 | 6. L2 blocks |
| 457 | 7. a level crossing |
| 475 | 8. an arterial over the main line |
| 511 | 9. unowned track |
| 525 | 10. a diagonal plot |
| 549 | 11. the eleventh class |
| 566 | 12. a bulk yard at 8 px over an east-west track |
| 586 | 13. the freight station |
| 622 | 14. wagons standing |
| 659 | 15. a north-south line through the yard |
| 671 | 16. at 4 px, and below LINES_FROM |
| 696 | 17. the stamp folds the yard |
| 718 | 18. the rail picture's repaint |
| 739 | 19. the far skeleton |
| 812 | 20. the legend |
| 837 | 22. gravel drawn grey (FR7 2.3) |
| 879 | 23. the Rail Depot: a small yard (FR7 slice 3) |
| 928 | 24. a siding's loading shed (FR7 slice 3) |
| 952 | 21. a line crossing the yard's box |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 92 | `RailDrawCheck.T` | `TilePainter.TILE` |  |
| 95 | `RailDrawCheck.TYPES` | `BuildingVisual.table(new BuildingCatalog().load())` | The game's building templates, as the map draws them. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 85 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 83 | 923 | **type** `public class RailDrawCheck` | Proves that the railway is drawn as rail, not as a cased road band (0.8.1, batch FR0; docs/studies/spec-freight-and-infrastructure.md 4.3, D15, D18). |
| 87 | 4 | `static void check(String label, boolean ok)` |  |
| 98 | 4 | `static BuildingVisual.Type terminal()` | The Rail Terminal type. |
| 104 | 6 | `static int[] raster(TilePainter.Input in, TilePainter.Painted p, int px)` | The image a painted tile rasters to at px pixels a plot. |
| 112 | 6 | `static int[] blocks(TilePainter.Input in, TilePainter.Painted p, int px, int block)` | The image a painted tile's blocks raster to at px pixels a plot. |
| 120 | 5 | `static TilePainter.Input owned()` | A tile whose plots are all owned, with no buildings and no sites. |
| 127 | 5 | `static TilePainter.Painted paint(TilePainter.Input in)` | Paints `in` into a fresh Painted. |
| 134 | 6 | `static void railRow(TilePainter.Input in, int y, int marks)` | Lays a row of track, each plot with `marks` (0 for none). |
| 142 | 6 | `static void railColumn(TilePainter.Input in, int x, int marks)` | Lays a column of track, each plot with `marks` (0 for none). |
| 150 | 3 | `static void streetColumn(TilePainter.Input in, int x, int code)` | Sets a column of street plots to `code`. |
| 155 | 3 | `static int pixel(int[] img, int w, int plotX, int plotY, int dx, int dy, int px)` | One pixel of plot (plotX, plotY), its local (dx, dy) at px pixels a plot. |
| 160 | 3 | `static int bedWidth(int px, boolean doubled)` | The bed width the dials give at px pixels a plot. |
| 165 | 9 | `static boolean[] rowsAll(int[] img, int w, int plotX, int plotY, int px, int colour)` | The rows of a plot that are wholly `colour`, in dy order. |
| 176 | 9 | `static boolean[] colsAll(int[] img, int w, int plotX, int plotY, int px, int colour)` | The columns of a plot that are wholly `colour`, in dx order. |
| 186 | 5 | `static int count(boolean[] a)` |  |
| 193 | 8 | `static TilePainter.Input yardAt(BuildingVisual.Type terminal, int boxX, int boxY, int boxW, int boxH)` | A hand-built yard tile: the Rail Terminal's box, on owned ground. |
| 203 | 6 | `static boolean hasInBox(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | Whether any pixel in the box is `colour`. |
| 211 | 8 | `static boolean hasOnSide(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour, int line, boolean above)` | Whether any pixel in the box on the chosen side of `line` is `colour`. |
| 221 | 9 | `static int distinctRows(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | How many distinct pixel rows in the box have a pixel of `colour`. |
| 232 | 9 | `static int distinctCols(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | How many distinct pixel columns in the box have a pixel of `colour`. |
| 243 | 11 | `static boolean[] rowsWith(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | The box's pixel rows, relative to its top, that have a pixel of `colour`. |
| 256 | 20 | `static int[] bedBands(boolean[] rows)` | A fan's beds from its BALLAST rows: the two ballast rows of each track are one rail apart, the tracks a gap apart. |
| 278 | 9 | `static boolean bandHas(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour, int start, int end)` | Whether any pixel in rows start..end (relative to the box's top) is `colour`. |
| 288 | 28 | `public static void main(String[] args)` |  |

### 1. a straight east-west single track at 8 px (lines 317-338)

| line | len | member | says |
|---:|---:|---|---|
| 318 | 20 | `static void straightAtEight()` |  |

### 2. M_MAIN | M_DOUBLE, and M_MAIN alone (lines 339-366)

| line | len | member | says |
|---:|---:|---|---|
| 340 | 26 | `static void doubleMainAtEight()` |  |

### 3. a north-south track at 8 px (lines 367-387)

| line | len | member | says |
|---:|---:|---|---|
| 368 | 19 | `static void northSouthAtEight()` |  |

### 4. at 4 px (lines 388-404)

| line | len | member | says |
|---:|---:|---|---|
| 389 | 15 | `static void atFour()` |  |

### 5. L1 blocks (lines 405-437)

| line | len | member | says |
|---:|---:|---|---|
| 406 | 31 | `static void blocksMiddle()` |  |

### 6. L2 blocks (lines 438-456)

| line | len | member | says |
|---:|---:|---|---|
| 439 | 17 | `static void blocksFar()` |  |

### 7. a level crossing (lines 457-474)

| line | len | member | says |
|---:|---:|---|---|
| 458 | 16 | `static void levelCrossing()` |  |

### 8. an arterial over the main line (lines 475-510)

| line | len | member | says |
|---:|---:|---|---|
| 476 | 34 | `static void arterialBridge()` |  |

### 9. unowned track (lines 511-524)

| line | len | member | says |
|---:|---:|---|---|
| 512 | 12 | `static void unownedTrack()` |  |

### 10. a diagonal plot (lines 525-548)

| line | len | member | says |
|---:|---:|---|---|
| 526 | 22 | `static void diagonalTrack()` |  |

### 11. the eleventh class (lines 549-565)

| line | len | member | says |
|---:|---:|---|---|
| 550 | 15 | `static void eleventhClass()` |  |

### 12. a bulk yard at 8 px over an east-west track (lines 566-585)

| line | len | member | says |
|---:|---:|---|---|
| 567 | 18 | `static void bulkYardAtEight()` |  |

### 13. the freight station (lines 586-621)

| line | len | member | says |
|---:|---:|---|---|
| 587 | 34 | `static void freightStation()` |  |

### 14. wagons standing (lines 622-658)

| line | len | member | says |
|---:|---:|---|---|
| 623 | 35 | `static void wagonsStanding()` |  |

### 15. a north-south line through the yard (lines 659-670)

| line | len | member | says |
|---:|---:|---|---|
| 660 | 10 | `static void northSouthYard()` |  |

### 16. at 4 px, and below LINES_FROM (lines 671-695)

| line | len | member | says |
|---:|---:|---|---|
| 672 | 23 | `static void atFourAndBelow()` |  |

### 17. the stamp folds the yard (lines 696-717)

| line | len | member | says |
|---:|---:|---|---|
| 697 | 20 | `static void stampFoldsTheYard()` |  |

### 18. the rail picture's repaint (lines 718-738)

| line | len | member | says |
|---:|---:|---|---|
| 719 | 6 | `static CityMap railCity()` |  |
| 726 | 12 | `static void railPictureRepaint()` |  |

### 19. the far skeleton (lines 739-811)

| line | len | member | says |
|---:|---:|---|---|
| 742 | 21 | `static CityMap skeletonCity()` | A square city on a dry place whose square holds iron, with highways, a freight line and yards: the far skeleton's fixture. |
| 765 | 4 | `static boolean hasColour(int[] img, int colour)` | Whether any pixel is exactly `colour`. |
| 770 | 41 | `static void farSkeleton()` |  |

### 20. the legend (lines 812-836)

| line | len | member | says |
|---:|---:|---|---|
| 815 | 4 | `static boolean legendHas(List<LandMap.Entry> entries, String words)` | Whether the legend has an entry with these words. |
| 820 | 16 | `static void legend()` |  |

### 22. gravel drawn grey (FR7 2.3) (lines 837-878)

| line | len | member | says |
|---:|---:|---|---|
| 838 | 23 | `static void gravelGrey()` |  |
| 863 | 3 | `static double luma(int c)` | A colour's luma, 0 to 255 (Rec. |
| 868 | 4 | `static double distance(int a, int c)` | Two colours' distance in RGB. |
| 874 | 4 | `static boolean nearInRow(int[] img, int w, int y, int x, int reach, int colour)` | Whether pixel row y has a pixel of `colour` within `reach` pixels of column x. |

### 23. the Rail Depot: a small yard (FR7 slice 3) (lines 879-927)

| line | len | member | says |
|---:|---:|---|---|
| 882 | 4 | `static BuildingVisual.Type depot()` | The Rail Depot type: the yard FR7 added as the railway's small first rung. |
| 887 | 40 | `static void railDepotYard()` |  |

### 24. a siding's loading shed (FR7 slice 3) (lines 928-951)

| line | len | member | says |
|---:|---:|---|---|
| 930 | 21 | `static void sidingShed()` |  |

### 21. a line crossing the yard's box (lines 952-1005)

| line | len | member | says |
|---:|---:|---|---|
| 953 | 52 | `static void lineCrossingTheYard()` |  |

