# RailDrawCheck.java - 816 lines · 47 methods · 2 constants · harnesses

`ham/citybuildersim/RailDrawCheck.java` - generated 2026-10-18 by CodeMap; line numbers are as of that run.

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
>      and the gateway, and no RAIL_LINE entry.
> 
> Numbering is the running order.

**Uses:** [TileRaster](TileRaster.md) (84), [TilePainter](TilePainter.md) (77), [BuildingVisual](BuildingVisual.md) (40), [CityMap](CityMap.md) (12), [LandMap](LandMap.md) (10), [MapTiles](MapTiles.md) (10), [MapFrame](MapFrame.md) (6), [World](World.md) (4), [Founding](Founding.md) (4), [BuildingCatalog](BuildingCatalog.md) (3), [DistrictPlan](DistrictPlan.md) (3), [CityRuns](CityRuns.md) (3), [BuildingType](BuildingType.md) (3), [MapCheck](MapCheck.md) (1), [Resource](Resource.md) (1), [CityShore](CityShore.md) (1)

## Sections

| line | section |
|---:|---|
| 297 | 1. a straight east-west single track at 8 px |
| 319 | 2. M_MAIN \| M_DOUBLE, and M_MAIN alone |
| 347 | 3. a north-south track at 8 px |
| 368 | 4. at 4 px |
| 385 | 5. L1 blocks |
| 418 | 6. L2 blocks |
| 437 | 7. a level crossing |
| 455 | 8. an arterial over the main line |
| 491 | 9. unowned track |
| 505 | 10. a diagonal plot |
| 529 | 11. the eleventh class |
| 546 | 12. a bulk yard at 8 px over an east-west track |
| 566 | 13. the freight station |
| 602 | 14. wagons standing |
| 639 | 15. a north-south line through the yard |
| 651 | 16. at 4 px, and below LINES_FROM |
| 676 | 17. the stamp folds the yard |
| 698 | 18. the rail picture's repaint |
| 719 | 19. the far skeleton |
| 792 | 20. the legend |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 76 | `RailDrawCheck.T` | `TilePainter.TILE` |  |
| 79 | `RailDrawCheck.TYPES` | `BuildingVisual.table(new BuildingCatalog().load())` | The game's building templates, as the map draws them. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 750 | **type** `public class RailDrawCheck` | Proves that the railway is drawn as rail, not as a cased road band (0.8.1, batch FR0; docs/studies/spec-freight-and-infrastructure.md 4.3, D15, D18). |
| 71 | 4 | `static void check(String label, boolean ok)` |  |
| 82 | 4 | `static BuildingVisual.Type terminal()` | The Rail Terminal type. |
| 88 | 6 | `static int[] raster(TilePainter.Input in, TilePainter.Painted p, int px)` | The image a painted tile rasters to at px pixels a plot. |
| 96 | 6 | `static int[] blocks(TilePainter.Input in, TilePainter.Painted p, int px, int block)` | The image a painted tile's blocks raster to at px pixels a plot. |
| 104 | 5 | `static TilePainter.Input owned()` | A tile whose plots are all owned, with no buildings and no sites. |
| 111 | 5 | `static TilePainter.Painted paint(TilePainter.Input in)` | Paints `in` into a fresh Painted. |
| 118 | 6 | `static void railRow(TilePainter.Input in, int y, int marks)` | Lays a row of track, each plot with `marks` (0 for none). |
| 126 | 6 | `static void railColumn(TilePainter.Input in, int x, int marks)` | Lays a column of track, each plot with `marks` (0 for none). |
| 134 | 3 | `static void streetColumn(TilePainter.Input in, int x, int code)` | Sets a column of street plots to `code`. |
| 139 | 3 | `static int pixel(int[] img, int w, int plotX, int plotY, int dx, int dy, int px)` | One pixel of plot (plotX, plotY), its local (dx, dy) at px pixels a plot. |
| 144 | 3 | `static int bedWidth(int px, boolean doubled)` | The bed width the dials give at px pixels a plot. |
| 149 | 9 | `static boolean[] rowsAll(int[] img, int w, int plotX, int plotY, int px, int colour)` | The rows of a plot that are wholly `colour`, in dy order. |
| 160 | 9 | `static boolean[] colsAll(int[] img, int w, int plotX, int plotY, int px, int colour)` | The columns of a plot that are wholly `colour`, in dx order. |
| 170 | 5 | `static int count(boolean[] a)` |  |
| 177 | 8 | `static TilePainter.Input yardAt(BuildingVisual.Type terminal, int boxX, int boxY, int boxW, int boxH)` | A hand-built yard tile: the Rail Terminal's box, on owned ground. |
| 187 | 6 | `static boolean hasInBox(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | Whether any pixel in the box is `colour`. |
| 195 | 8 | `static boolean hasOnSide(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour, int line, boolean above)` | Whether any pixel in the box on the chosen side of `line` is `colour`. |
| 205 | 9 | `static int distinctRows(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | How many distinct pixel rows in the box have a pixel of `colour`. |
| 216 | 9 | `static int distinctCols(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | How many distinct pixel columns in the box have a pixel of `colour`. |
| 227 | 11 | `static boolean[] rowsWith(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour)` | The box's pixel rows, relative to its top, that have a pixel of `colour`. |
| 240 | 20 | `static int[] bedBands(boolean[] rows)` | A fan's beds from its BALLAST rows: the two ballast rows of each track are one rail apart, the tracks a gap apart. |
| 262 | 9 | `static boolean bandHas(int[] img, int w, int boxX, int boxY, int boxW, int boxH, int px, int colour, int start, int end)` | Whether any pixel in rows start..end (relative to the box's top) is `colour`. |
| 272 | 24 | `public static void main(String[] args)` |  |

### 1. a straight east-west single track at 8 px (lines 297-318)

| line | len | member | says |
|---:|---:|---|---|
| 298 | 20 | `static void straightAtEight()` |  |

### 2. M_MAIN | M_DOUBLE, and M_MAIN alone (lines 319-346)

| line | len | member | says |
|---:|---:|---|---|
| 320 | 26 | `static void doubleMainAtEight()` |  |

### 3. a north-south track at 8 px (lines 347-367)

| line | len | member | says |
|---:|---:|---|---|
| 348 | 19 | `static void northSouthAtEight()` |  |

### 4. at 4 px (lines 368-384)

| line | len | member | says |
|---:|---:|---|---|
| 369 | 15 | `static void atFour()` |  |

### 5. L1 blocks (lines 385-417)

| line | len | member | says |
|---:|---:|---|---|
| 386 | 31 | `static void blocksMiddle()` |  |

### 6. L2 blocks (lines 418-436)

| line | len | member | says |
|---:|---:|---|---|
| 419 | 17 | `static void blocksFar()` |  |

### 7. a level crossing (lines 437-454)

| line | len | member | says |
|---:|---:|---|---|
| 438 | 16 | `static void levelCrossing()` |  |

### 8. an arterial over the main line (lines 455-490)

| line | len | member | says |
|---:|---:|---|---|
| 456 | 34 | `static void arterialBridge()` |  |

### 9. unowned track (lines 491-504)

| line | len | member | says |
|---:|---:|---|---|
| 492 | 12 | `static void unownedTrack()` |  |

### 10. a diagonal plot (lines 505-528)

| line | len | member | says |
|---:|---:|---|---|
| 506 | 22 | `static void diagonalTrack()` |  |

### 11. the eleventh class (lines 529-545)

| line | len | member | says |
|---:|---:|---|---|
| 530 | 15 | `static void eleventhClass()` |  |

### 12. a bulk yard at 8 px over an east-west track (lines 546-565)

| line | len | member | says |
|---:|---:|---|---|
| 547 | 18 | `static void bulkYardAtEight()` |  |

### 13. the freight station (lines 566-601)

| line | len | member | says |
|---:|---:|---|---|
| 567 | 34 | `static void freightStation()` |  |

### 14. wagons standing (lines 602-638)

| line | len | member | says |
|---:|---:|---|---|
| 603 | 35 | `static void wagonsStanding()` |  |

### 15. a north-south line through the yard (lines 639-650)

| line | len | member | says |
|---:|---:|---|---|
| 640 | 10 | `static void northSouthYard()` |  |

### 16. at 4 px, and below LINES_FROM (lines 651-675)

| line | len | member | says |
|---:|---:|---|---|
| 652 | 23 | `static void atFourAndBelow()` |  |

### 17. the stamp folds the yard (lines 676-697)

| line | len | member | says |
|---:|---:|---|---|
| 677 | 20 | `static void stampFoldsTheYard()` |  |

### 18. the rail picture's repaint (lines 698-718)

| line | len | member | says |
|---:|---:|---|---|
| 699 | 6 | `static CityMap railCity()` |  |
| 706 | 12 | `static void railPictureRepaint()` |  |

### 19. the far skeleton (lines 719-791)

| line | len | member | says |
|---:|---:|---|---|
| 722 | 21 | `static CityMap skeletonCity()` | A square city on a dry place whose square holds iron, with highways, a freight line and yards: the far skeleton's fixture. |
| 745 | 4 | `static boolean hasColour(int[] img, int colour)` | Whether any pixel is exactly `colour`. |
| 750 | 41 | `static void farSkeleton()` |  |

### 20. the legend (lines 792-816)

| line | len | member | says |
|---:|---:|---|---|
| 795 | 4 | `static boolean legendHas(List<LandMap.Entry> entries, String words)` | Whether the legend has an entry with these words. |
| 800 | 16 | `static void legend()` |  |

