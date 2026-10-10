# TileRaster.java - 822 lines · 28 methods · 40 constants · model

`ham/citybuildersim/TileRaster.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> A painted tile as pixels: an int[] of 0xAARRGGBB, a row at a time, at a whole number of pixels a plot - what the map view hands PixelWriter.setPixels() for a tile's image.
> 
> WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.6). The
> tile painter says what each plot is; the map draws a tile as one image,
> kept in a cache, at 4 px a plot for the near view (8 px above 6 px a plot)
> and, as blocks, at 2 px for the middle view and 1 px for the far (MapFrame,
> 0.7.61). This turns a painted tile into that image, in the mockup's
> colours: its ground (the legend's grass, forest, fresh water, sea and
> beach), ground the city does not own dimmed toward the
> map's background, a resource's sites tinted with its colour - half grey
> where its holding is being worked, grey where it is worked out (the
> mockup's star 7) - the roads by kind, and every building in its class's
> fill, edged from 6 px a plot (the mockup's), the flats darker; a mine or
> well grey on its site with a mark of its resource's colour at its centre.
> Pure: the same tile gives the same pixels. The design measured 24 to 31 us
> a tile at 4 px, 81 to 134 at 8, 8 at 1.
> 
> THE MOCKUP'S WIDTHS AND SIZES (J3b). J3 filled a road's whole plot and drew
> every building a pixel in from its plot, so at 4 px a plot a home was a
> 2 x 2 speck on the grass. From 4 px a road was the mockup's line - gravel
> 0.3 of its plot, paved 0.52 with a dark casing, a highway 0.86 with its
> own and a dashed centre line from 6 px - joined to each road beside it at
> the narrower one's width, with its bridge's deck under it (a highway's
> still is; a street's since 0.7.88 is its verge and surface, below); a one-plot
> building is drawn at its own land's side (0.7.64: a House 0.91 of its
> plot; the mockup's 0.6 to 0.92 by a hash before), set against the road it
> faces, so a street's homes line it; and
> below 4 px a paved road is drawn in its casing's grey, which a pixel of
> stands out from the grass where its own does not. BLOCKS (blocks()) are
> the mockup's middle and far views: each 4 or 8 plots square in its
> dominant kind's colour at an opacity by how much of it is built, the roads
> over them - what the whole city looks like at the land office's zoom.
> 
> TRACK (0.7.72, batch N3). The railway's lines are drawn plot by plot as
> track (TilePainter.RAIL): from 4 px a plot a line of a paved road's
> width, cased in RAIL_LINE and dashed white plot by plot, joined to the
> track beside it, on a deck over fresh water; a road crossing it drawn over
> it; below 4 px and in the blocks, its plots in RAIL_LINE. The mockup drew
> no railway, so the colours are a map's convention (star N3-9).
> 
> STREETS AS VERGE AND SURFACE (0.7.88, batch RD2; the project's
> spec-roads-and-ports.md 2.1 and 5, its prototype's render()). A street's
> plot is its right of way: drawn as VERGE, with its surface down its middle
> - gravel tan, paved grey, a band half the plot wide for a street of half
> width (15 m) and the whole plot for one of full width (30 m) - joined to
> the street beside it each way it runs; a TRACK, a street the city has
> bought no road for, a dashed brown line on the verge; an arterial and a
> boulevard's two rows drawn as the streets they are; a bridge's deck under
> its surface. A highway is drawn over a street that passes beneath it, dark
> with its dashed centre line. Ground under a building is drawn cleared
> (forest as grass; spec 2.6, the zoning study's Z7), and a building packed
> at the city's edge without a street (R7) a shade darker.

**Uses:** [TilePainter](TilePainter.md) (127), [BuildingVisual](BuildingVisual.md) (42), [World](World.md) (6), [DistrictPlan](DistrictPlan.md) (4), [CityRuns](CityRuns.md) (4), [Resource](Resource.md) (3)

**Used by (6):** [LandScreen](LandScreen.md), [MapCheck](MapCheck.md), [MapFrame](MapFrame.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [ReadPathCheck](ReadPathCheck.md)

## Sections

| line | section |
|---:|---|
| 218 | AT SEA (0.7.97, batch O13; TilePainter's AT SEA; mockup 3's colours) |
| 352 | THE MIDDLE AND FAR VIEWS AS BLOCKS (J3b: the mockup's levels 1 and 2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 64 | `TileRaster.GROUND` | `{ 0xffa8c47e, 0xff527c45, 0xff78b4dc, 0xff2f5d88, 0xffe6daaa }` | Each ground class's colour, by World's class (GRASS, FOREST, FRESH, SALT, SAND): the mockup's legend. |
| 67 | `TileRaster.ROAD` | `{ 0, 0xffcdb07c, 0xffa4aab0, 0xff3b4048 }` | Each road kind's colour (gravel, paved, highway): the mockup's MAP. |
| 70 | `TileRaster.RAIL_LINE` | `0xff707070` | The railway's line, and its casing from 4 px a plot (0.7.72): openstreetmap-carto's rail grey, #707070 (the mockup drew no railway; star N3-9)... |
| 73 | `TileRaster.RAIL_DASH` | `0xffffffff` | ...and its dashes, white on every other plot along it: carto's rail dash. |
| 76 | `TileRaster.DECK` | `0xff5d4c3c` | A bridge's deck, edging a road over fresh water from 4 px a plot: the mockup's. |
| 79 | `TileRaster.UNOWNED_DIM` | `0.4` | Ground no one in the city owns is drawn this much of the way to the map's background: 40%. |
| 82 | `TileRaster.VOID` | `0xff16222c` | The map's background, which unowned ground is dimmed toward: the mockup's --map-void. |
| 85 | `TileRaster.SITE_TINT` | `0.36` | A site's tint over its ground, its resource's colour this much of the way: 36% (the mockup's most for a deposit's body). |
| 88 | `TileRaster.SITE_SPECKLE` | `0.75` | ...and on the plots its own hash speckles, 75% (the mockup's ore showing through). |
| 91 | `TileRaster.SPECKLE_SHARE` | `0.15` | The share of a site's plots speckled: 15%. |
| 94 | `TileRaster.WORKED_GREY` | `0xff929496` | A worked-out site's grey: the mockup's mined grey. |
| 97 | `TileRaster.RING` | `0xff2b2d31` | A mine's ring and centre mark: the mockup's ring. |
| 100 | `TileRaster.INSET_FROM` | `3` | Pixels a plot from which a building of more than one plot is inset a pixel: 3 (a one-plot building takes its own land's side: SMALL_MOST). |
| 103 | `TileRaster.EDGE_FROM` | `6` | ...and from which it is edged: 6 (the mockup's outlines). |
| 106 | `TileRaster.ROAD_WIDTH` | `{ 0, 0.30, 0.52, 0.86 }` | A road's width as a share of its plot, by kind (gravel, paved, highway): the mockup's 0.3, 0.52 and 0.86 - since 0.7.88 the highway's and (as a paved road's) the track's; a street's surface is its width's (SURFACE). |
| 109 | `TileRaster.ROAD_LEAST_PX` | `{ 0, 1, 2, 3 }` | ...and its least, in pixels, at the near view: 1, 2 and 3 (the mockup's 1, 1.5 and 2.5, whole). |
| 112 | `TileRaster.HIGHWAY_CASE` | `0xff1d2126` | A highway's casing, a pixel either side from 6 px (the mockup's highwayCase)... |
| 115 | `TileRaster.CENTRE_LINE` | `0xfff0cf5a` | ...and its dashed centre line from 6 px a plot, a pixel wide, on the first 55% of each plot along it (the mockup's centre, its dash 0.7 on and 0.55 off). |
| 118 | `TileRaster.LINES_FROM` | `4` | Pixels a plot from which a road is drawn as a line of its own width on its ground - a street as verge and surface (0.7.88) - and below, the whole plot in its colour: 4, the near view's image. |
| 121 | `TileRaster.SMALL_MOST` | `0.92` | A one-plot building's side as a share of its plot: its own land's (BuildingVisual.plotSide(), 0.7.64 - a House 0.91 of the plot, a Convenience Store 0.72; the mockup's 0.6 to 0.92 by a hash before), never more than 0.... |
| 124 | `TileRaster.SET_BACK` | `0.07` | ...and set this far from the road it faces: 0.07 of a plot, so it hugs its street (the mockup's). |
| 128 | `TileRaster.VERGE` | `0xffbfd39a` | A street's verge, its right of way either side of its surface (0.7.88): the prototype's VERGE (spec-roads-and-ports.md 5, roads_proto.py). |
| 131 | `TileRaster.TRACK_DASH` | `0xff8a6a3c` | A track's dashes, a street the city has bought no road for (0.7.88): the prototype's brown (spec 2.5, 5: "a dashed brown line on the verge"). |
| 134 | `TileRaster.SURFACE` | `{ 0, DistrictPlan.HALF, DistrictPlan.FULL }` | A street's surface across its plot, by its width (Painted.width: 0, half, full): none, half the plot (15 m), the whole plot (30 m) - the right of way is 30 m (spec 2.1; the prototype's W_MIN and W_FULL). |
| 137 | `TileRaster.PACKED_DIM` | `0.35` | A building packed at the city's edge without a street (R7) is drawn this much of the way to the map's background: 35% (star RD2-2) - a shade darker, so it reads as standing apart from the streets, at its own size and ... |
| 223 | `TileRaster.QUAY_DECK` | `0xff3a4655` | A quay's deck: mockup 3's jetty, #3a4655... |
| 226 | `TileRaster.QUAY_EDGE` | `0xff5a6676` | ...and its edge from EDGE_FROM px a plot: the mockup's #5a6676. |
| 229 | `TileRaster.JACKET_FILL` | `0xff2a3540` | A platform's jacket: mockup 3's #2a3540... |
| 232 | `TileRaster.OIL_TAN` | `0xffc9b68f` | ...edged in the mockup's tan, #c9b68f - its ring's and the pipe's colour too (the ore's). |
| 235 | `TileRaster.RING_ALPHA` | `0.25` | A platform's 500 m ring over the water: the tan at the mockup's 25%... |
| 238 | `TileRaster.RING_DASH_PX` | `3` | ...dashed 3 px on, 3 off along its arc (the mockup's "3 3"). |
| 241 | `TileRaster.PIPE_ALPHA` | `0.5` | A crude pipeline: the tan at the mockup's 50%, dashed 2 px on and 4 off (its "2 4"), 1.5 px wide (drawn 1 below 4 px a plot, 2 from it). |
| 242 | `TileRaster.PIPE_ON_PX` | `2, PIPE_OFF_PX = 4` |  |
| 357 | `TileRaster.BLOCK_ALPHA` | `{ 0.4, 0.6, 0.8, 0.96 }` | A block's opacity over its ground by its share built: 40%, 60%, 80% and 96% (the mockup's ALPHA)... |
| 360 | `TileRaster.BLOCK_FILLS` | `{ 0.12, 0.28, 0.5 }` | ...at a share built under 12%, under 28%, under 50%, and above (the mockup's). |
| 363 | `TileRaster.BLOCK_MIDDLE` | `4` | Blocks the mockup drew at its middle view, in plots a side: 4 (120 m), from 1.4 to 3.2 px a plot... |
| 366 | `TileRaster.BLOCK_FAR` | `8` | ...and at its far view: 8 (240 m), below 1.4 px a plot, with gravel hidden. |
| 449 | `TileRaster.HIGHWAY_LEAST_PX` | `2` | The least a highway is drawn across, in pixels, in the blocks' views: 2 (the mockup's 1.8 at its far view, 2 at its middle). |
| 663 | `TileRaster.RAMP_WIDTH` | `ROAD_WIDTH [ BuildingVisual.GRAVEL ]` | A ramp's slip road's width, as a share of a plot: a gravel street's, 0.3 (ROAD_WIDTH). |
| 804 | `TileRaster.ROAD_SMALL` | `{ 0, 0xffcdb07c, 0xff6a7077, 0xff3b4048 }` | A road plot's colour below LINES_FROM px a plot, where it fills its plot, by kind: gravel's and the highway's own, a paved road's casing - its own grey is the grass's brightness, and a pixel of it vanishes among the h... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 764 | **type** `public final class TileRaster` | A painted tile as pixels: an int[] of 0xAARRGGBB, a row at a time, at a whole number of pixels a plot - what the map view hands PixelWriter.setPixels() for a tile's image. |
| 61 | 1 | `private TileRaster()` |  |
| 148 | 69 | `public static void raster(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | Fills img with the tile at px pixels a plot: (32 px) squared pixels, row by row. |

### AT SEA (0.7.97, batch O13; TilePainter's AT SEA; mockup 3's colours) (lines 218-351)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 63 | `static void atSea(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The works at sea over the tile's picture: each quay plot the deck, edged from EDGE_FROM; a jacket the mockup's platform, edged; a well its tan dot; then every pixel of a plot a ring or a pipe crosses that lies on its ... |
| 317 | 3 | `static boolean runHighway(TilePainter.Painted p, int i)` | Whether plot i of the runs is a highway's (its own, or the track bridging it), under a building or not (0.7.89). |
| 326 | 16 | `private static void overBuildings(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A highway passing over a building (0.7.89): a mine standing on its site where the elevated highway crosses it - drawn over the building, its arms to the highway beside it (spec 2.7: elevated). |
| 344 | 7 | `private static boolean runHighwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | Whether the plot a step from plot i, across the tile's edge too, is a highway's of the runs (under a building or not). |

### THE MIDDLE AND FAR VIEWS AS BLOCKS (J3b: the mockup's levels 1 and 2) (lines 352-822)

| line | len | member | says |
|---:|---:|---|---|
| 377 | 70 | `public static void blocks(TilePainter.Input in, TilePainter.Painted p, int px, int block, boolean gravel, int[] img)` | A tile at px pixels a plot as the mockup drew its middle and far views: its ground, then blocks of `block` plots a side, each in its dominant kind's colour - by its buildings' plots, the homes' houses and flats togeth... |
| 452 | 3 | `static boolean isHighway(TilePainter.Painted p, int i)` | Whether plot i is a highway's own (a run's, not a street of highway plots). |
| 457 | 3 | `static boolean isStreet(TilePainter.Painted p, int i)` | Whether plot i is a street (a track among them), or a highway's plot with a street beneath. |
| 462 | 4 | `static int roadKind(TilePainter.Painted p, int i)` | The kind of road on plot i - GRAVEL, PAVED, HIGHWAY or (0.7.88) TilePainter.TRACK - a road's plot, or where a street crosses the track - or 0. |
| 468 | 8 | `static int faceOf(TilePainter.Painted p, int i)` | The side of plot i a street lies on, north, east, south, west, or -1 for none. |
| 478 | 10 | `static int[] crossSection(int kind, int px)` | A road's line across its plot, by kind at px pixels a plot: {its whole width, its casing either side} - the highway's and (as a paved road's) the track's. |
| 490 | 6 | `private static boolean streetAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | Whether the plot a step from plot i - across the tile's edge too, as the plans have it (Input.beyond) - is a way of the street at i: a street, or a highway's plot a street passes beneath. |
| 498 | 7 | `private static boolean highwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | ...and a highway's own plot, a step from plot i. |
| 507 | 7 | `private static boolean railAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | ...and the railway's track (since 0.7.89 where it bridges a highway too: the runs' RAIL_OVER). |
| 516 | 3 | `static boolean diagonal(TilePainter.Input in, int i)` | Whether plot i is a 45-degree stretch's (the runs' M_DIAG): drawn as a band along its line. |
| 528 | 12 | `static void diagBand(int[] img, int w, int i, int px, byte mark, double half, int c)` | A 45-degree stretch's band across plot i (spec 2.7: "a curve is drawn smooth; in plots it is a staircase"): the pixels within `half` pixels of the stretch's line - through the plot's middle, or for a staircase's corne... |
| 549 | 112 | `private static void ways(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The ways as lines (from LINES_FROM px a plot; spec 5): each street's verge, its whole plot on dry ground; the decks of bridges; the track; each street's surface down its middle along each way it runs - half the plot f... |
| 673 | 36 | `private static void ramps(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The ramps (0.7.89; spec 2.7: "ramps where an arterial crosses, every other one, and at an arm's end"): at a highway's plot the runs mark a ramp, where a street passes beneath it, a slip road on each verge plot at its ... |
| 711 | 8 | `private static boolean isRunHighway(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether plot (x, y) - in the tile, or just across one of its edges - is a highway's of the runs. |
| 721 | 7 | `private static boolean isStreetAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether plot (x, y) - in the tile, or just across one of its edges - is a street's. |
| 730 | 11 | `private static void segment(int[] img, int w, int x0, int y0, int px, double ax, double ay, double bx, double by, double half, ...` | The pixels of a plot (its corner at x0, y0, px a side) within `half` of the segment from (ax, ay) to (bx, by), in the plot's pixels: colour c. |
| 748 | 24 | `private static void track(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The track (0.7.72): each plot's middle and its arms to the track beside it - past the tile's edge too, where the line runs on - at a paved road's cross-section, cased in RAIL_LINE and filled RAIL_DASH on every other p... |
| 773 | 6 | `private static void fillRect(int[] img, int w, int x0, int y0, int x1, int y1, int c)` |  |
| 781 | 10 | `static int groundColour(TilePainter.Input in, TilePainter.Painted p, int i)` | A plot's ground: its terrain - forest under a building cleared (0.7.88; spec 2.6) - its site's tint or grey, dimmed when unowned: what a road is drawn on from LINES_FROM px. |
| 793 | 9 | `static int siteTint(TilePainter.Input in, int i, int s, int c)` | A site's plot over its ground: its resource's colour, speckled, half grey while its holding is worked, grey worked out. |
| 807 | 7 | `static int plotColour(TilePainter.Input in, TilePainter.Painted p, int i)` | A plot's colour before its building: its ground, its site's tint or grey, its road's colour - a track's brown (0.7.88) - or (0.7.72) its track's, where no street crosses it - and dimmed when unowned. |
| 816 | 6 | `static int blend(int a, int b, double f)` | a toward b by share f, channel by channel, opaque. |

