# TileRaster.java - 2,381 lines · 83 methods · 90 constants · model

`ham/citybuildersim/TileRaster.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

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
> TRACK (0.7.72, batch N3; redrawn as rail, 0.8.1, batch FR0). The railway's
> lines are drawn plot by plot as track (TilePainter.RAIL): from 4 px a plot
> a ballast bed with one steel centre line and a sleeper tick a plot, and
> from 6 px two steel rails on sleepers (double track where the main line is
> marked), joined to the track beside it, on a deck over fresh water; a road
> crossing it drawn over the bed with the rails showing through and level
> crossing marks, or bridging it where an arterial or boulevard crosses the
> main line; below 4 px and in the blocks, its plots in RAIL_DARK with
> cross-ticks every block from 2 px. The mockup drew no railway, so the
> colours are a map's convention (star N3-9, spec-freight 4.3).
> 
> YARDS AND JUNCTIONS (0.8.1, batch FR0; spec-freight 4.2, 4.3 and 4.6,
> D15 and D19). From 4 px a plot a Rail Terminal is drawn as a yard
> (drawYard()): a fan of sidings closing to the line at each end, a freight
> shed, the freight it handles - ore stockpiles, or at the freight station
> of a railway carrying goods rows of boxes and two gantries - and wagons
> standing on as many sidings as the fleet's share of its wagon sets gives.
> From JUNCTIONS_FROM the junctions are drawn by rule, none of them a
> building: signals where arterials cross, roundabouts where boulevards
> cross, diamonds where a street passes beneath a ramp, stacks where
> highways cross; and from 4 px a highway's open end comes down to the
> ground. (Batch FR7, slice 4) Where a crossing has kept its ground
> (InterchangeGround: a 19-plot reserve for a cloverleaf, an 11-plot one for a
> trumpet) the stack gives way to the loops drawn on it (loops()); and a +
> of streets, a street's T on an arterial, are drawn with kerb corners, wide
> where an estate's trucks turn (kerbs()).
> 
> ... (12 more lines in the source)

**Uses:** [TilePainter](TilePainter.md) (309), [BuildingVisual](BuildingVisual.md) (92), [CityRuns](CityRuns.md) (14), [World](World.md) (10), [DistrictPlan](DistrictPlan.md) (6), [Resource](Resource.md) (3), [InterchangeGround](InterchangeGround.md) (3)

**Used by (20):** [BuildingVisual](BuildingVisual.md), [CityMap](CityMap.md), [DistrictPicture](DistrictPicture.md), [EarthTerrainCheck](EarthTerrainCheck.md), [FoundingScreen](FoundingScreen.md), [GlobeCheck](GlobeCheck.md), [GlobePicture](GlobePicture.md), [JunctionCheck](JunctionCheck.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapFrame](MapFrame.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [RailDrawCheck](RailDrawCheck.md), [ReadPathCheck](ReadPathCheck.md), [TrainSchedule](TrainSchedule.md), [TrainShapes](TrainShapes.md)

## Sections

| line | section |
|---:|---|
| 114 | RAIL DRAWN AS RAIL (0.8.1, batch FR0; spec-freight 4.3, D15, D18) |
| 142 | JUNCTIONS DRAWN BY RULE (0.8.1, batch FR0; spec-freight 4.6, D19) |
| 211 | YARDS DRAWN AS YARDS (0.8.1, batch FR0; spec-freight 4.3, D15) |
| 260 | THE NEAR VIEW (J3: a tile at 4 or 8 px a plot, MapFrame.L0) |
| 681 | THE MATERIALS AS THEMSELVES (0.8.0, batch I14; spec-materials 5's I14 |
| 793 | AT SEA (0.7.97, batch O13; TilePainter's AT SEA; mockup 3's colours) |
| 928 | THE MIDDLE AND FAR VIEWS AS BLOCKS (J3b: the mockup's levels 1 and 2) |
| 1215 | THE WAYS AS LINES (0.7.88, batch RD2; from LINES_FROM px a plot) |
| 1460 | · junctions' helpers |
| 1524 | · 1. signals: arterial x arterial |
| 1577 | · 2. roundabouts: boulevard x boulevard |
| 1617 | · 3. diamonds: a street passes beneath a highway |
| 1672 | · 4. a highway arm's end |
| 1753 | · 5. a stack: highways cross |
| 1803 | · 6. interchanges' loops: a cloverleaf's and a trumpet's reserve (batch FR7; spec-freight 4.6, D19) |
| 1896 | · 7. kerb corners and estate T's (batch FR7; spec-freight 4.6's table) |
| 2010 | · the track as rail, and its crossings (0.8.1, batch FR0; spec-freight 4.3) |
| 2316 | A RECTANGLE OF PIXELS, AND THE PLOTS' COLOURS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `TileRaster.GROUND` | `{ 0xffa8c47e, 0xff527c45, 0xff78b4dc, 0xff2f5d88, 0xffe6daaa, 0xffdfe7ee, 0xf...` | Each ground class's colour, by World's class (GRASS, FOREST, FRESH, SALT, SAND): the mockup's legend; and since 0.7.127 (batch E7; spec-globe 3.9) ICE, the founding globe's ice, 0xdfe7ee, and DESERT, CSS's tan (#d2b48... |
| 100 | `TileRaster.ROAD` | `{ 0, 0xffbdb8ae, 0xffa4aab0, 0xff3b4048 }` | Each road kind's colour (gravel, paved, highway): the mockup's MAP, but gravel's. |
| 103 | `TileRaster.GRAVEL_GRAIN` | `0xff9a958b` | The loose stone's speckle on a gravel street from 4 px a plot (the near view, L0): a shade darker than its grey. |
| 106 | `TileRaster.GRAVEL_GRAIN_SHARE` | `.18` | The share of a gravel street's pixels speckled: 18%. |
| 109 | `TileRaster.RAIL_LINE` | `0xff707070` | The railway's line, and its casing from 4 px a plot (0.7.72) until 0.8.1 drew the track as rail; since, a track plot's colour in raster() below LINES_FROM px a plot alone (plotColour()): openstreetmap-carto's rail gre... |
| 112 | `TileRaster.RAIL_DASH` | `0xffffffff` | ...and its dashes, white on every other plot along it until 0.8.1: carto's rail dash. |
| 117 | `TileRaster.BALLAST` | `0xff6f675c` | The track's ballast bed (spec-freight 4.3's grey-brown #6f675c). |
| 119 | `TileRaster.SLEEPER` | `0xff4a3f35` | A sleeper across the bed (spec-freight 4.3's #4a3f35). |
| 121 | `TileRaster.STEEL` | `0xffc9ced3` | The rails' steel (spec-freight 4.3's #c9ced3). |
| 123 | `TileRaster.RAIL_DARK` | `0xff3a3632` | The line the track is at the middle and far views (spec-freight 4.3's dark #3a3632). |
| 126 | `TileRaster.RAILS_FROM` | `6` | Pixels a plot from which the track is two rails on sleepers (the 8-px raster). |
| 128 | `TileRaster.BED` | `0.52` | A single track's bed, a share of the plot: the paved road's width, as the track has today. |
| 130 | `TileRaster.BED_DOUBLE` | `0.86` | Double track's bed, a share of the plot: the highway's. |
| 132 | `TileRaster.GAUGE` | `0.24` | Between a track's two rails, a share of the plot (at 8 px: the rails a pixel either side of the track's middle). |
| 134 | `TileRaster.TRACKS_APART` | `0.44` | Between double track's two tracks, middle to middle. |
| 136 | `TileRaster.SLEEPERS_EVERY` | `0.25` | A sleeper every quarter plot (spec-freight 4.3: "sleepers across it every 0.25 plot"). |
| 138 | `TileRaster.CROSSBUCK` | `0xffffffff` | A level crossing's white crossbuck (spec-freight 4.3 "Crossings"). |
| 140 | `TileRaster.BARRIER` | `0xffd04a3c` | ...and its barrier's red (spec-freight 4.3 "Crossings"). |
| 145 | `TileRaster.JUNCTIONS_FROM` | `6` | Pixels a plot from which junctions are drawn (the 8-px raster): signals, roundabouts, diamonds and stacks; a highway's end comes down to the ground wherever the ways are drawn, from LINES_FROM. |
| 147 | `TileRaster.JUNCTION_BOX` | `0xff8e949a` | A signalled junction's box, a shade darker than paved. |
| 149 | `TileRaster.ZEBRA` | `0xfff2f2f2` | A crossing's stripes and stop lines. |
| 151 | `TileRaster.ZEBRA_DEPTH` | `0.3` | Plots: a zebra crossing's depth along its arm. |
| 153 | `TileRaster.ROUNDABOUT_RADIUS` | `0.75` | Plots: a roundabout's outer radius, spec: "a circle of 1.5 plots". |
| 155 | `TileRaster.ROUNDABOUT_ISLAND` | `0.35` | Plots: its planted middle's radius. |
| 157 | `TileRaster.ISLAND` | `0xff6f9a4f` | The planted middle. |
| 159 | `TileRaster.SLIP_LENGTH` | `2` | Plots: a diamond's slip road along the verge each side of its street. |
| 161 | `TileRaster.JUNCTION_RUN` | `4` | Plots: the least run of junction streets through a plot along an axis for a road to cross there (FX3): a road up to three plots wide - a cell's boulevard, two deep, beside the next cell's road, as a tile sees it one p... |
| 164 | `TileRaster.TICKS_FROM` | `2` | Pixels a plot from which the blocks' view draws the track's cross-ticks (the L1 raster). |
| 167 | `TileRaster.DECK` | `0xff5d4c3c` | A bridge's deck, edging a road over fresh water from 4 px a plot: the mockup's. |
| 170 | `TileRaster.UNOWNED_DIM` | `0.4` | Ground no one in the city owns is drawn this much of the way to the map's background: 40%. |
| 173 | `TileRaster.VOID` | `0xff16222c` | The map's background, which unowned ground is dimmed toward: the mockup's --map-void. |
| 176 | `TileRaster.SITE_TINT` | `0.36` | A site's tint over its ground, its resource's colour this much of the way: 36% (the mockup's most for a deposit's body). |
| 179 | `TileRaster.SITE_SPECKLE` | `0.75` | ...and on the plots its own hash speckles, 75% (the mockup's ore showing through). |
| 182 | `TileRaster.SPECKLE_SHARE` | `0.15` | The share of a site's plots speckled: 15%. |
| 185 | `TileRaster.WORKED_GREY` | `0xff929496` | A worked-out site's grey: the mockup's mined grey. |
| 188 | `TileRaster.RING` | `0xff2b2d31` | A mine's ring and centre mark: the mockup's ring. |
| 191 | `TileRaster.INSET_FROM` | `3` | Pixels a plot from which a building of more than one plot is inset a pixel: 3 (a one-plot building takes its own land's side: SMALL_MOST). |
| 194 | `TileRaster.EDGE_FROM` | `6` | ...and from which it is edged: 6 (the mockup's outlines). |
| 197 | `TileRaster.ROAD_WIDTH` | `{ 0, 0.30, 0.52, 0.86 }` | A road's width as a share of its plot, by kind (gravel, paved, highway): the mockup's 0.3, 0.52 and 0.86 - since 0.7.88 the highway's and (as a paved road's) the track's; a street's surface is its width's (SURFACE). |
| 200 | `TileRaster.ROAD_LEAST_PX` | `{ 0, 1, 2, 3 }` | ...and its least, in pixels, at the near view: 1, 2 and 3 (the mockup's 1, 1.5 and 2.5, whole). |
| 203 | `TileRaster.HIGHWAY_CASE` | `0xff1d2126` | A highway's casing, a pixel either side from 6 px (the mockup's highwayCase)... |
| 206 | `TileRaster.CENTRE_LINE` | `0xfff0cf5a` | ...and its dashed centre line from 6 px a plot, a pixel wide, on the first 55% of each plot along it (the mockup's centre, its dash 0.7 on and 0.55 off). |
| 209 | `TileRaster.LINES_FROM` | `4` | Pixels a plot from which a road is drawn as a line of its own width on its ground - a street as verge and surface (0.7.88) - and below, the whole plot in its colour: 4, the near view's image. |
| 214 | `TileRaster.YARD_GROUND` | `0xff8c8478` | A yard's ground, light ballast (spec-freight 4.3's yard). |
| 216 | `TileRaster.YARD_TRACKS` | `8` | The fan's sidings (spec-freight 4.3: 6 to 10). |
| 218 | `TileRaster.DEPOT_TRACKS` | `3` | A Rail Depot's sidings (FR7 slice 3, "2 to 3 sidings and a shed"): 3 - its box is 8 plots by 7, a Rail Terminal's 24 by 24. |
| 225 | `TileRaster.YARD_TRACK_APART` | `1.5` | Plots between the fan's tracks, middle to middle. |
| 227 | `TileRaster.YARD_LADDER` | `3` | Plots at each end over which the fan closes to the line. |
| 229 | `TileRaster.SHED_DEPTH` | `2.5, SHED_LENGTH = 0.6` | The freight shed: plots deep, and its share of the fan's length. |
| 231 | `TileRaster.ORE_HEAP` | `0xffc9b68f` | A bulk yard's stockpiles, the spec's sand colour. |
| 233 | `TileRaster.CONTAINER` | `0xffd6dde4` | A container yard's boxes, the boats' white. |
| 235 | `TileRaster.GANTRY` | `0xff2b2f36` | Its two cranes. |
| 237 | `TileRaster.WAGON` | `0xff7b4b3a` | A wagon standing. |
| 239 | `TileRaster.WAGON_LENGTH` | `0.5` | Plots: a wagon about 15 m. |
| 242 | `TileRaster.SMALL_MOST` | `0.92` | A one-plot building's side as a share of its plot: its own land's (BuildingVisual.plotSide(), 0.7.64 - a House 0.91 of the plot, a Convenience Store 0.72; the mockup's 0.6 to 0.92 by a hash before), never more than 0.... |
| 245 | `TileRaster.SET_BACK` | `0.07` | ...and set this far from the road it faces: 0.07 of a plot, so it hugs its street (the mockup's). |
| 249 | `TileRaster.VERGE` | `0xffbfd39a` | A street's verge, its right of way either side of its surface (0.7.88): the prototype's VERGE (spec-roads-and-ports.md 5, roads_proto.py). |
| 252 | `TileRaster.TRACK_DASH` | `0xff8a6a3c` | A track's dashes, a street the city has bought no road for (0.7.88): the prototype's brown (spec 2.5, 5: "a dashed brown line on the verge"). |
| 255 | `TileRaster.SURFACE` | `{ 0, DistrictPlan.HALF, DistrictPlan.FULL }` | A street's surface across its plot, by its width (Painted.width: 0, half, full): none, half the plot (15 m), the whole plot (30 m) - the right of way is 30 m (spec 2.1; the prototype's W_MIN and W_FULL). |
| 258 | `TileRaster.PACKED_DIM` | `0.35` | A building packed at the city's edge without a street (R7) is drawn this much of the way to the map's background: 35% (star RD2-2) - a shade darker, so it reads as standing apart from the streets, at its own size and ... |
| 694 | `TileRaster.LOOK_FROM` | `3` | Pixels a plot from which a materials building is drawn as its look rather than a box of its colour: 3 (TileRaster's INSET_FROM; the L0 image is 4). |
| 697 | `TileRaster.BENCHES` | `6` | The benches a pit or a quarry steps down by from its rim to its middle: 6. |
| 700 | `TileRaster.POND` | `0xff4f8fbf` | A leach plant's pond: #4f8fbf. |
| 703 | `TileRaster.HEADFRAME` | `0xff2b2622, WINDING = 0xff8a7f72` | A shaft's headframe: #2b2622, and the winding house in it #8a7f72. |
| 706 | `TileRaster.CONVEYOR` | `0xff9a9aa2` | The wash plant's conveyor: #9a9aa2. |
| 709 | `TileRaster.LOGS` | `0xff5a3e22` | A camp's log stacks: #5a3e22. |
| 712 | `TileRaster.CROWN` | `0xff6a9a55` | Planted forest's crowns, in rows over its darker floor: #6a9a55. |
| 715 | `TileRaster.RIDGE_DARK` | `0.18` | How much darker a works' roof ridges are than its fill: 18%. |
| 798 | `TileRaster.QUAY_DECK` | `0xff3a4655` | A quay's deck: mockup 3's jetty, #3a4655... |
| 801 | `TileRaster.QUAY_EDGE` | `0xff5a6676` | ...and its edge from EDGE_FROM px a plot: the mockup's #5a6676. |
| 804 | `TileRaster.JACKET_FILL` | `0xff2a3540` | A platform's jacket: mockup 3's #2a3540... |
| 807 | `TileRaster.OIL_TAN` | `0xffc9b68f` | ...edged in the mockup's tan, #c9b68f - its ring's and the pipe's colour too (the ore's). |
| 810 | `TileRaster.RING_ALPHA` | `0.25` | A platform's 500 m ring over the water: the tan at the mockup's 25%... |
| 813 | `TileRaster.RING_DASH_PX` | `3` | ...dashed 3 px on, 3 off along its arc (the mockup's "3 3"). |
| 816 | `TileRaster.PIPE_ALPHA` | `0.5` | A crude pipeline: the tan at the mockup's 50%, dashed 2 px on and 4 off (its "2 4"), 1.5 px wide (drawn 1 below 4 px a plot, 2 from it). |
| 818 | `TileRaster.PIPE_ON_PX` | `2, PIPE_OFF_PX = 4` | ...its dash, 2 px on and 4 off along the pipe (the mockup's "2 4"). |
| 933 | `TileRaster.BLOCK_ALPHA` | `{ 0.4, 0.6, 0.8, 0.96 }` | A block's opacity over its ground by its share built: 40%, 60%, 80% and 96% (the mockup's ALPHA)... |
| 936 | `TileRaster.BLOCK_FILLS` | `{ 0.12, 0.28, 0.5 }` | ...at a share built under 12%, under 28%, under 50%, and above (the mockup's). |
| 939 | `TileRaster.BLOCK_MIDDLE` | `4` | Blocks the mockup drew at its middle view, in plots a side: 4 (120 m), from 1.4 to 3.2 px a plot... |
| 942 | `TileRaster.BLOCK_FAR` | `8` | ...and at its far view: 8 (240 m), below 1.4 px a plot, with gravel hidden. |
| 1048 | `TileRaster.HIGHWAY_LEAST_PX` | `2` | The least a highway is drawn across, in pixels, in the blocks' views: 2 (the mockup's 1.8 at its far view, 2 at its middle). |
| 1378 | `TileRaster.RAMP_WIDTH` | `ROAD_WIDTH [ BuildingVisual.GRAVEL ]` | A ramp's slip road's width, as a share of a plot: a gravel street's, 0.3 (ROAD_WIDTH). |
| 1806 | `TileRaster.LOOP_RADIUS` | `2.5, LOOP_AT = 5.0` | A loop's radius, in plots, and how far from the crossing's middle along each axis a cloverleaf's loop is centred (batch FR7): 2.5 and 5.0, so a loop fills its quadrant of the 19-plot reserve. |
| 1809 | `TileRaster.TRUMPET_RADIUS` | `2.0, TRUMPET_OUT = 5.5, TRUMPET_ACROSS = 3.0` | A trumpet's loop: its radius and how far out along its stub and across it its centre lies, in plots - 2.0, and 5.5 and 3.0, inside the 11-plot reserve. |
| 1812 | `TileRaster.LOOP_WIDTH` | `0.34` | A loop's and a slip's road across, as a share of a plot: 0.34, a pixel or two wider than a gravel street's, so it reads as a ramp at 8 px a plot and not as a street. |
| 1815 | `TileRaster.SLIP_OFF` | `0.45, SLIP_OUT = 9.0, TRUMPET_SLIP_OUT = 10.0` | Where a slip leaves its highway, in plots from the junction's middle: just off the highway's band (0.45) and (along it) 9.0 out for a cloverleaf, 10.0 for a trumpet's stub. |
| 1899 | `TileRaster.KERB_RADIUS` | `0.25` | A kerb corner's radius, in plots: 0.25 (7.5 m), the corner of a + of two streets in a homes cell - and of a street's T on an arterial there. |
| 1902 | `TileRaster.ESTATE_T_RADIUS` | `0.5` | An estate T's corner radius, in plots: 0.5 (15 m) - wide, where trucks turn (spec-freight 4.6: "a T with wide radius corners"). |
| 2030 | `TileRaster.SIDING_SHED` | `0.7` | A siding's loading shed, the share of a plot it fills (FR7 slice 3): 0.7, a small block at the siding's end beside the works. |
| 2363 | `TileRaster.ROAD_SMALL` | `{ 0, 0xffbdb8ae, 0xff6a7077, 0xff3b4048 }` | A road plot's colour below LINES_FROM px a plot, where it fills its plot, by kind: gravel's and the highway's own, a paved road's casing - its own grey is the grass's brightness, and a pixel of it vanishes among the h... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 2303 | **type** `public final class TileRaster` | A painted tile as pixels: an int[] of 0xAARRGGBB, a row at a time, at a whole number of pixels a plot - what the map view hands PixelWriter.setPixels() for a tile's image. |
| 81 | 1 | `private TileRaster()` |  |

### RAIL DRAWN AS RAIL (0.8.1, batch FR0; spec-freight 4.3, D15, D18) (lines 114-141)

### JUNCTIONS DRAWN BY RULE (0.8.1, batch FR0; spec-freight 4.6, D19) (lines 142-210)

### YARDS DRAWN AS YARDS (0.8.1, batch FR0; spec-freight 4.3, D15) (lines 211-259)

| line | len | member | says |
|---:|---:|---|---|
| 221 | 3 | `public static int tracksOf(BuildingVisual.Type t)` | The sidings of a yard of type t: a Rail Depot's DEPOT_TRACKS, else YARD_TRACKS (FR7 slice 3). |

### THE NEAR VIEW (J3: a tile at 4 or 8 px a plot, MapFrame.L0) (lines 260-680)

| line | len | member | says |
|---:|---:|---|---|
| 274 | 84 | `public static void raster(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | Fills img with the tile at px pixels a plot: (32 px) squared pixels, row by row. |
| 383 | 47 | `static int[] yardLayout(boolean[] rail, int w, int h)` | Where a yard's fan lies in its box, from the box's track (FX3, 0.8.4): rail[y * w + x] is whether the box's plot (x, y) holds track, the box w plots across and h down. |
| 445 | 135 | `private static void drawYard(TilePainter.Input in, TilePainter.Painted p, int b, BuildingVisual.Type t, int px, int[] img, int ...` | A Rail Terminal's yard as a yard, not its class's plain box (0.8.1, batch FR0; spec-freight 4.2 and 4.3, D15): ballast ground, a fan of YARD_TRACKS sidings closing to the line at each end over YARD_LADDER plots, a fre... |
| 587 | 10 | `private static void yardTrack(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img)` | One plot of the track that crosses a yard's head, drawn over the yard's ground (FX3, 0.8.4): drawTrackLook()'s look, with the track beside it read from the runs (p.run), because a building stands on every plot of the ... |
| 599 | 27 | `private static void containers(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int w, int x0, int y0, int x1, i...` | A container yard's boxes, in rows, and its two gantry cranes across the fan. |
| 628 | 19 | `private static void stockpiles(int px, int[] img, int w, int x0, int y0, int x1, int y1, boolean ew, int fanA, int fanB, int fa...` | A bulk yard's three stockpiles, side by side along the fan. |
| 649 | 11 | `private static void fillEllipse(int[] img, int w, int cx, int cy, int rx, int ry, int fill, int edge)` | A filled ellipse, its edge a pixel darker. |
| 662 | 4 | `private static boolean outside(int x, int y, int cx, int cy, int rx, int ry)` | Whether (x, y) lies outside the ellipse centred at (cx, cy). |
| 668 | 12 | `private static void line(int[] img, int w, int ax, int ay, int bx, int by, int c)` | A one-pixel line from (ax, ay) to (bx, by), Bresenham. |

### THE MATERIALS AS THEMSELVES (0.8.0, batch I14; spec-materials 5's I14 (lines 681-792)

| line | len | member | says |
|---:|---:|---|---|
| 718 | 74 | `static void drawLook(TilePainter.Input in, BuildingVisual.Type t, int site, int px, boolean edged, int[] img, int w, int x0, in...` | One materials building's look over its box [x0, x1) x [y0, y1) in pixels: a site's measured from the site's whole square. |

### AT SEA (0.7.97, batch O13; TilePainter's AT SEA; mockup 3's colours) (lines 793-927)

| line | len | member | says |
|---:|---:|---|---|
| 828 | 63 | `static void atSea(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The works at sea over the tile's picture: each quay plot the deck, edged from EDGE_FROM; a jacket the mockup's platform, edged; a well its tan dot; then every pixel of a plot a ring or a pipe crosses that lies on its ... |
| 893 | 3 | `static boolean runHighway(TilePainter.Painted p, int i)` | Whether plot i of the runs is a highway's (its own, or the track bridging it), under a building or not (0.7.89). |
| 902 | 16 | `private static void overBuildings(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A highway passing over a building (0.7.89): a mine standing on its site where the elevated highway crosses it - drawn over the building, its arms to the highway beside it (spec 2.7: elevated). |
| 920 | 7 | `private static boolean runHighwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | Whether the plot a step from plot i, across the tile's edge too, is a highway's of the runs (under a building or not). |

### THE MIDDLE AND FAR VIEWS AS BLOCKS (J3b: the mockup's levels 1 and 2) (lines 928-1214)

| line | len | member | says |
|---:|---:|---|---|
| 953 | 93 | `public static void blocks(TilePainter.Input in, TilePainter.Painted p, int px, int block, boolean gravel, int[] img)` | A tile at px pixels a plot as the mockup drew its middle and far views: its ground, then blocks of `block` plots a side, each in its dominant kind's colour - by its buildings' plots, the homes' houses and flats togeth... |
| 1051 | 3 | `static boolean isHighway(TilePainter.Painted p, int i)` | Whether plot i is a highway's own (a run's, not a street of highway plots). |
| 1056 | 3 | `static boolean isStreet(TilePainter.Painted p, int i)` | Whether plot i is a street (a track among them), or a highway's plot with a street beneath. |
| 1061 | 4 | `static int roadKind(TilePainter.Painted p, int i)` | The kind of road on plot i - GRAVEL, PAVED, HIGHWAY or (0.7.88) TilePainter.TRACK - a road's plot, or where a street crosses the track - or 0. |
| 1067 | 8 | `static int faceOf(TilePainter.Painted p, int i)` | The side of plot i a street lies on, north, east, south, west, or -1 for none. |
| 1077 | 10 | `static int[] crossSection(int kind, int px)` | A road's line across its plot, by kind at px pixels a plot: {its whole width, its casing either side} - the highway's and (as a paved road's) the track's. |
| 1089 | 6 | `private static boolean streetAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | Whether the plot a step from plot i - across the tile's edge too, as the plans have it (Input.beyond) - is a way of the street at i: a street, or a highway's plot a street passes beneath. |
| 1097 | 7 | `private static boolean highwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | ...and a highway's own plot, a step from plot i. |
| 1106 | 12 | `private static boolean straightHighwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | ...and a straight highway's, a step from plot i: not a 45-degree stretch's plot, whose marks (across the tile's edge too: marksBeyond) say it is no highway beside a straight one for a stack's test. |
| 1120 | 7 | `private static boolean railAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | ...and the railway's track (since 0.7.89 where it bridges a highway too: the runs' RAIL_OVER). |
| 1129 | 3 | `static boolean diagonal(TilePainter.Input in, int i)` | Whether plot i is a 45-degree stretch's (the runs' M_DIAG): drawn as a band along its line. |
| 1140 | 6 | `static void diagBand(int[] img, int w, int i, int px, byte mark, double half, int c)` | A 45-degree stretch's band across plot i (spec 2.7: "a curve is drawn smooth; in plots it is a staircase"): the pixels within `half` pixels of the stretch's line - through the plot's middle, or for a staircase's corne... |
| 1154 | 12 | `private static void diagLine(int[] img, int w, int i, int px, int code, double pxl, double pyl, double half, double along, int c)` | The pixels of plot i within `half` pixels of a line at a 45-degree heading's code through the point (pxl, pyl) of the plot's pixels, and no farther along the line than `along` pixels either side of that point (FX3: di... |
| 1168 | 15 | `private static int stepMarkAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | The marks of the 45-degree stretch's step plot at (x, y) - in the tile, or just across one of its edges (marksBeyond) - or -1 where that plot is no step: a corner, a straight highway, other ground. |
| 1194 | 20 | `private static void diagOpen(TilePainter.Input in, TilePainter.Painted p, int i, int px, double half, int c, int[] img)` | Where a 45-degree stretch passes between two of its steps the line clips the corner of the plot that is not the run's - the staircase's corners are on the other side of it - and the band would leave a notch there (FX3... |

### THE WAYS AS LINES (0.7.88, batch RD2; from LINES_FROM px a plot) (lines 1215-1459)

| line | len | member | says |
|---:|---:|---|---|
| 1234 | 142 | `private static void ways(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The ways as lines (from LINES_FROM px a plot; spec 5): each street's verge, its whole plot on dry ground; the decks of bridges; the track (as rail since 0.8.1); each street's surface down its middle along each way it ... |
| 1390 | 37 | `private static void ramps(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The ramps (0.7.89; spec 2.7: "ramps where an arterial crosses, every other one, and at an arm's end"): at a highway's plot the runs mark a ramp, where a street passes beneath it, a slip road on each verge plot at its ... |
| 1429 | 8 | `private static boolean isRunHighway(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether plot (x, y) - in the tile, or just across one of its edges - is a highway's of the runs. |
| 1439 | 7 | `private static boolean isStreetAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether plot (x, y) - in the tile, or just across one of its edges - is a street's. |
| 1448 | 11 | `private static void segment(int[] img, int w, int x0, int y0, int px, double ax, double ay, double bx, double by, double half, ...` | The pixels of a plot (its corner at x0, y0, px a side) within `half` of the segment from (ax, ay) to (bx, by), in the plot's pixels: colour c. |

### junctions' helpers (lines 1460-1523)

| line | len | member | says |
|---:|---:|---|---|
| 1463 | 14 | `private static byte streetRoleAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | The role of the street at plot (x, y) - in the tile, or just across one of its edges - ARTERIAL or BOULEVARD; 0 for none, a track, a street beneath a highway, or a corner beyond the tile. |
| 1479 | 3 | `private static boolean junctionRole(byte role)` | Whether a street role is one a junction is drawn for. |
| 1490 | 14 | `private static int junctionRun(TilePainter.Input in, TilePainter.Painted p, int x, int y, int dx, int dy, boolean boulevards)` | The plots in a run of streets through plot (x, y) along (dx, dy), itself counted: its neighbours each way while their role is a junction's (boulevards alone with `boulevards`; FX3, Jerus's eye check of 0.8.3). |
| 1506 | 5 | `private static boolean signalAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether the street at plot (x, y) is a signalled junction: itself an arterial or boulevard, with a run of JUNCTION_RUN or more of them through it on each axis (a road two plots wide is one road), and not a roundabout. |
| 1513 | 4 | `private static boolean roundaboutAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether the street at plot (x, y) is a roundabout: itself a boulevard, with a run of JUNCTION_RUN or more boulevards through it on each axis. |
| 1519 | 4 | `private static int[] surfaceAt(TilePainter.Painted p, int i, int px)` | A street's surface across its plot at px pixels a plot: {its width, its offset from the plot's edge}. |

### 1. signals: arterial x arterial (lines 1524-1576)

| line | len | member | says |
|---:|---:|---|---|
| 1526 | 23 | `private static void signals(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` |  |
| 1551 | 25 | `private static void drawSignalArm(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int dir)` | A zebra crossing and its stop line on one arm of a signal, at the edge facing the junction. |

### 2. roundabouts: boulevard x boulevard (lines 1577-1616)

| line | len | member | says |
|---:|---:|---|---|
| 1579 | 12 | `private static void roundabouts(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` |  |
| 1593 | 5 | `private static boolean roundaboutTarget(TilePainter.Input in, TilePainter.Painted p, int i)` | Whether a roundabout's ring or island may be drawn on plot i: a street or empty ground, never a building, the track, a highway or water. |
| 1600 | 16 | `private static void drawRoundaboutOn(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int cx, int cy)` | The ring's and island's pixels of a roundabout centred on plot (cx, cy) that fall on plot i. |

### 3. diamonds: a street passes beneath a highway (lines 1617-1671)

| line | len | member | says |
|---:|---:|---|---|
| 1619 | 29 | `private static void diamonds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` |  |
| 1651 | 20 | `private static void drawSlip(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int hx, int hy, boolean rowHw, int...` | One diamond slip along the verge beside a highway, from the street out for SLIP_LENGTH plots. |

### 4. a highway arm's end (lines 1672-1752)

| line | len | member | says |
|---:|---:|---|---|
| 1681 | 59 | `private static void highwayEnds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A highway arm's end (spec 2.7's ramp at an arm's end): a highway plot the runs mark as a ramp with no street beneath, where the highway runs on exactly one of its four sides - the band narrows to a paved road's and a ... |
| 1742 | 10 | `private static void drawHighwayApron(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int tx, int ty, boolean ve...` | A paved apron the paved road's width across the plot just past a highway's open end, when that plot is empty ground. |

### 5. a stack: highways cross (lines 1753-1802)

| line | len | member | says |
|---:|---:|---|---|
| 1763 | 26 | `private static void stack(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A stack where two straight highways cross: a highway plot with another beside it on both axes. |
| 1791 | 11 | `private static void drawStackSlip(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int cx, int cy, int dx, int dy)` | One corner slip of a stack, across the plot diagonal to the crossing. |

### 6. interchanges' loops: a cloverleaf's and a trumpet's reserve (batch FR7; spec-freight 4.6, D19) (lines 1803-1895)

| line | len | member | says |
|---:|---:|---|---|
| 1825 | 48 | `private static void loops(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The loops of every reserve that reaches the tile, from JUNCTIONS_FROM px a plot: a cloverleaf's four loops, each with a slip to either highway; a trumpet's one loop with a slip to its stub and to the highway it ends a... |
| 1875 | 4 | `private static double[] spoke(double ax, double ay, double cx, double cy, double r)` | A slip from (ax, ay) to the ring about (cx, cy) of radius r: the straight line toward the ring's middle, ending on it. |
| 1881 | 5 | `private static double segmentDistance(double x, double y, double ax, double ay, double bx, double by)` | The distance from (x, y) to the segment from (ax, ay) to (bx, by). |
| 1888 | 7 | `private static boolean reserveJunction(TilePainter.Input in, int x, int y)` | Whether plot (x, y) of the tile is the junction of a reserve that reaches it: where loops replace a stack's corner slips. |

### 7. kerb corners and estate T's (batch FR7; spec-freight 4.6's table) (lines 1896-2009)

| line | len | member | says |
|---:|---:|---|---|
| 1917 | 48 | `private static void kerbs(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | Kerb corners and estate T's, from JUNCTIONS_FROM px a plot, over the streets' surfaces and their verges: at a + of two streets (a cell's streets, role STREET, on all four sides) each of its four inner corners is round... |
| 1967 | 14 | `private static boolean estateAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether a building of an estate's classes - industry, farms, utilities, mines and wells, transport; a homes cell holds the rest - stands within two plots of plot (x, y): the painter's reading of an estate. |
| 1990 | 19 | `private static void fillet(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int jx, int jy, int cx, int cy, int ...` | One rounded corner: the corner point (cx, cy), in pixels from plot (jx, jy)'s corner, where two bars meet; (sx, sy) the way out of it, toward the verge. |

### the track as rail, and its crossings (0.8.1, batch FR0; spec-freight 4.3) (lines 2010-2315)

| line | len | member | says |
|---:|---:|---|---|
| 2022 | 6 | `private static void track(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The track (0.7.72; redrawn as rail, 0.8.1, batch FR0, spec-freight 4.3): each plot's middle and its arms to the track beside it - past the tile's edge too, where the line runs on - as a ballast bed, and from 4 px a pl... |
| 2037 | 20 | `private static void sidingSheds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A siding's loading shed (FR7 slice 3): on the plot marked M_SHED, the last of a siding beside its works' box, a small block in TRANSPORT's colours over the track's end, edged from EDGE_FROM. |
| 2059 | 3 | `private static int bedWidth(int px, boolean doubled)` | The ballast bed's width, whole pixels: BED, or BED_DOUBLE where the plot is marked double track. |
| 2064 | 5 | `private static void drawTrackLook(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img)` | The track's look on one plot that carries rail: its ballast bed, sleepers and rails (a 45-degree stretch's just a bed and one rail). |
| 2071 | 23 | `private static void drawTrackLook(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int arms)` | ...with the track beside it given, bit d for direction d (a yard's head reads it from the runs: yardTrack()). |
| 2096 | 10 | `private static void drawBed(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int arms)` | The track's ballast bed: the plot's middle and its arms to the track beside it, as the bed's whole width. |
| 2108 | 22 | `private static void drawSleepers(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boolean n...` | The sleepers, a pixel thick across the bed, every `step` pixels along the way the track runs. |
| 2132 | 8 | `private static void drawSleeperTicks(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boole...` | Below RAILS_FROM px a plot, one sleeper tick across the bed at the start of each plot along the track. |
| 2142 | 5 | `private static void drawRails(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boolean ns, ...` | The rails, a pixel each, along the way the track runs. |
| 2149 | 29 | `private static void drawRailsFull(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boolean ...` | From RAILS_FROM px a plot: two rails a track, double track's two tracks at the plot's middle +/- TRACKS_APART. |
| 2180 | 22 | `private static void drawRailsLow(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boolean n...` | Below RAILS_FROM px a plot: one STEEL line down the track's middle, two at double track's tracks. |
| 2204 | 7 | `private static void railH(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int row, int c, ...` | A rail a pixel wide along an east-west track at row `row`, its middle and its arms. |
| 2213 | 7 | `private static void railV(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int col, int c, ...` | ...and a north-south track's, at column `col`. |
| 2228 | 45 | `private static void railCrossings(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The railway's crossings (0.8.1, spec-freight 4.3, D18): the street's surface already lies over the bed; from RAILS_FROM px a plot, draw the rails through a level crossing with its crossbucks and half barriers, and an ... |
| 2275 | 5 | `private static void drawCrossbucks(int px, int[] img, int w, int x0, int y0)` | A level crossing's two crossbucks: a white X in the plot's north-west and south-east corners. |
| 2282 | 6 | `private static void drawX(int[] img, int w, int x0, int y0, int s)` | An X of side `s` in CROSSBUCK, its corner at (x0, y0). |
| 2290 | 25 | `private static void drawBarriers(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int w, int x0, int y0, ...` | A level crossing's half barriers: a bar across the lane coming in at each edge where the street enters the plot. |

### A RECTANGLE OF PIXELS, AND THE PLOTS' COLOURS (lines 2316-2381)

| line | len | member | says |
|---:|---:|---|---|
| 2321 | 10 | `private static void grain(TilePainter.Input in, int[] img, int w, int x0, int y0, int x1, int y1, int c)` | Speckles GRAVEL_GRAIN_SHARE of the box's pixels with c, each by a hash of its pixel in the tile and the tile's place, so the same street speckles the same at every paint. |
| 2332 | 6 | `private static void fillRect(int[] img, int w, int x0, int y0, int x1, int y1, int c)` |  |
| 2340 | 10 | `static int groundColour(TilePainter.Input in, TilePainter.Painted p, int i)` | A plot's ground: its terrain - forest under a building cleared (0.7.88; spec 2.6) - its site's tint or grey, dimmed when unowned: what a road is drawn on from LINES_FROM px. |
| 2352 | 9 | `static int siteTint(TilePainter.Input in, int i, int s, int c)` | A site's plot over its ground: its resource's colour, speckled, half grey while its holding is worked, grey worked out. |
| 2366 | 7 | `static int plotColour(TilePainter.Input in, TilePainter.Painted p, int i)` | A plot's colour before its building: its ground, its site's tint or grey, its road's colour - a track's brown (0.7.88) - or (0.7.72) its track's, where no street crosses it - and dimmed when unowned. |
| 2375 | 6 | `static int blend(int a, int b, double f)` | a toward b by share f, channel by channel, opaque. |

