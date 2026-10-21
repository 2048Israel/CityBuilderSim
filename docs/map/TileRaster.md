# TileRaster.java - 3,034 lines · 109 methods · 95 constants · model

`ham/citybuildersim/TileRaster.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

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
> colours are a map's convention (star N3-9, spec-freight 4.3). Since FX5
> (0.8.15; Jerus, 2026-10-20: "do trains make 90 degree turns?") a corner of
> the railway is an arc, not an L: the turns of the trains' routes
> (CityMap.trainRoutes()) are TrackCurves' bends, drawn by the arcs of
> bends() over the plots they replace (Input.curved), and a yard's ladders
> are the S curves the routes run along (ladderRails()), so the drawing and
> the trains share one geometry. The highways' joints are drawn so too (FX5
> slice D; Jerus: "once it finishes its still a tad wierd"): where a
> 45-degree stretch meets straight highway the carriageway turns along an arc
> (TrackCurves.HIGHWAY_RADIUS, highwayBends()) in place of the plots it
> replaces, and a stretch that ends on its diagonal ends squared across its
> heading (diagEndOpen()).
> 
> YARDS AND JUNCTIONS (0.8.1, batch FR0; spec-freight 4.2, 4.3 and 4.6,
> D15 and D19). From 4 px a plot a Rail Terminal (and since FR7 a Rail
> Depot, with fewer sidings) is drawn as a yard
> (drawYard()): a fan of sidings closing to the line at each end, a freight
> shed, the freight it handles - ore stockpiles, or at the freight station
> ... (29 more lines in the source)

**Uses:** [TilePainter](TilePainter.md) (366), [BuildingVisual](BuildingVisual.md) (98), [TrackCurves](TrackCurves.md) (22), [CityRuns](CityRuns.md) (16), [World](World.md) (10), [DistrictPlan](DistrictPlan.md) (6), [Resource](Resource.md) (3), [InterchangeGround](InterchangeGround.md) (3)

**Used by (21):** [BuildingVisual](BuildingVisual.md), [CityMap](CityMap.md), [CityRuns](CityRuns.md), [DistrictPicture](DistrictPicture.md), [EarthTerrainCheck](EarthTerrainCheck.md), [FoundingScreen](FoundingScreen.md), [GlobeCheck](GlobeCheck.md), [GlobePicture](GlobePicture.md), [JunctionCheck](JunctionCheck.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapFrame](MapFrame.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [RailDrawCheck](RailDrawCheck.md), [ReadPathCheck](ReadPathCheck.md), [TrainSchedule](TrainSchedule.md), [TrainShapes](TrainShapes.md)

## Sections

| line | section |
|---:|---|
| 131 | RAIL DRAWN AS RAIL (0.8.1, batch FR0; spec-freight 4.3, D15, D18) |
| 159 | JUNCTIONS DRAWN BY RULE (0.8.1, batch FR0; spec-freight 4.6, D19) |
| 230 | YARDS DRAWN AS YARDS (0.8.1, batch FR0; spec-freight 4.3, D15) |
| 301 | THE NEAR VIEW (J3: a tile at 4 or 8 px a plot, MapFrame.L0) |
| 814 | THE MATERIALS AS THEMSELVES (0.8.0, batch I14; spec-materials 5's I14 |
| 926 | AT SEA (0.7.97, batch O13; TilePainter's AT SEA; mockup 3's colours) |
| 1061 | THE MIDDLE AND FAR VIEWS AS BLOCKS (J3b: the mockup's levels 1 and 2) |
| 1422 | THE WAYS AS LINES (0.7.88, batch RD2; from LINES_FROM px a plot) |
| 1691 | · junctions' helpers |
| 1904 | · 1. signals: where arterials, boulevards and access roads cross (FX5: one box a crossing) |
| 1960 | · 2. roundabouts: where ROUNDABOUT_ROADS or more roads meet (FX5; before, wherever boulevards crossed) |
| 2004 | · 3. diamonds: a street passes beneath a highway |
| 2059 | · 4. a highway arm's end |
| 2141 | · 5. a stack: highways cross |
| 2191 | · 6. interchanges' loops: a cloverleaf's and a trumpet's reserve (batch FR7; spec-freight 4.6, D19) |
| 2284 | · 7. kerb corners and estate T's (batch FR7; spec-freight 4.6's table) |
| 2398 | · the track as rail, and its crossings (0.8.1, batch FR0; spec-freight 4.3) |
| 2609 | · the curves (FX5) |
| 2969 | A RECTANGLE OF PIXELS, AND THE PLOTS' COLOURS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 106 | `TileRaster.GROUND` | `{ 0xffa8c47e, 0xff527c45, 0xff78b4dc, 0xff2f5d88, 0xffe6daaa, 0xffdfe7ee, 0xf...` | Each ground class's colour, by World's class (GRASS, FOREST, FRESH, SALT, SAND): the mockup's legend; and since 0.7.127 (batch E7; spec-globe 3.9) ICE, the founding globe's ice, 0xdfe7ee, and DESERT, CSS's tan (#d2b48... |
| 117 | `TileRaster.ROAD` | `{ 0, 0xffbdb8ae, 0xffa4aab0, 0xff3b4048 }` | Each road kind's colour (gravel, paved, highway): the mockup's MAP, but gravel's. |
| 120 | `TileRaster.GRAVEL_GRAIN` | `0xff9a958b` | The loose stone's speckle on a gravel street from 4 px a plot (the near view, L0): a shade darker than its grey. |
| 123 | `TileRaster.GRAVEL_GRAIN_SHARE` | `.18` | The share of a gravel street's pixels speckled: 18%. |
| 126 | `TileRaster.RAIL_LINE` | `0xff707070` | The railway's line, and its casing from 4 px a plot (0.7.72) until 0.8.1 drew the track as rail; since, a track plot's colour in raster() below LINES_FROM px a plot alone (plotColour()): openstreetmap-carto's rail gre... |
| 129 | `TileRaster.RAIL_DASH` | `0xffffffff` | ...and its dashes, white on every other plot along it until 0.8.1: carto's rail dash. |
| 134 | `TileRaster.BALLAST` | `0xff6f675c` | The track's ballast bed (spec-freight 4.3's grey-brown #6f675c). |
| 136 | `TileRaster.SLEEPER` | `0xff4a3f35` | A sleeper across the bed (spec-freight 4.3's #4a3f35). |
| 138 | `TileRaster.STEEL` | `0xffc9ced3` | The rails' steel (spec-freight 4.3's #c9ced3). |
| 140 | `TileRaster.RAIL_DARK` | `0xff3a3632` | The line the track is at the middle and far views (spec-freight 4.3's dark #3a3632). |
| 143 | `TileRaster.RAILS_FROM` | `6` | Pixels a plot from which the track is two rails on sleepers (the 8-px raster). |
| 145 | `TileRaster.BED` | `0.52` | A single track's bed, a share of the plot: the paved road's width, as the track has today. |
| 147 | `TileRaster.BED_DOUBLE` | `0.86` | Double track's bed, a share of the plot: the highway's. |
| 149 | `TileRaster.GAUGE` | `0.24` | Between a track's two rails, a share of the plot (at 8 px: the rails a pixel either side of the track's middle). |
| 151 | `TileRaster.TRACKS_APART` | `0.44` | Between double track's two tracks, middle to middle. |
| 153 | `TileRaster.SLEEPERS_EVERY` | `0.25` | A sleeper every quarter plot (spec-freight 4.3: "sleepers across it every 0.25 plot"). |
| 155 | `TileRaster.CROSSBUCK` | `0xffffffff` | A level crossing's white crossbuck (spec-freight 4.3 "Crossings"). |
| 157 | `TileRaster.BARRIER` | `0xffd04a3c` | ...and its barrier's red (spec-freight 4.3 "Crossings"). |
| 162 | `TileRaster.JUNCTIONS_FROM` | `6` | Pixels a plot from which junctions are drawn (the 8-px raster): signals, roundabouts, diamonds and stacks, and since FR7 kerb corners and interchanges' loops; a highway's end comes down to the ground wherever the ways... |
| 164 | `TileRaster.JUNCTION_BOX` | `0xff8e949a` | A signalled junction's box, a shade darker than paved. |
| 166 | `TileRaster.ZEBRA` | `0xfff2f2f2` | A crossing's stripes and stop lines. |
| 168 | `TileRaster.ZEBRA_DEPTH` | `0.3` | Plots: a zebra crossing's depth along its arm. |
| 170 | `TileRaster.ROUNDABOUT_RADIUS` | `0.75` | Plots: a roundabout's outer radius, spec: "a circle of 1.5 plots" - about a crossing one plot wide; since FX5 a wider crossing's ring is that and half of the rectangle's longer side less a plot more. |
| 172 | `TileRaster.ROUNDABOUT_ISLAND` | `0.35` | Plots: its planted middle's radius, widened as the ring is. |
| 174 | `TileRaster.ISLAND` | `0xff6f9a4f` | The planted middle. |
| 176 | `TileRaster.SLIP_LENGTH` | `2` | Plots: a diamond's slip road along the verge each side of its street. |
| 178 | `TileRaster.JUNCTION_RUN` | `4` | Plots: the least run of junction streets through a plot along an axis for a road to cross there (FX3): a road up to three plots wide - a cell's boulevard, two deep, beside the next cell's road, as a tile sees it past ... |
| 180 | `TileRaster.ROUNDABOUT_ROADS` | `5` | Roads: the fewest that leave a crossing (counted by Crossings.roadsLeaving(): each run of streets along a side of its rectangle, each slanted street off a corner) for it to be a roundabout, not a signal (FX5; Jerus, 2... |
| 183 | `TileRaster.TICKS_FROM` | `2` | Pixels a plot from which the blocks' view draws the track's cross-ticks (the L1 raster). |
| 186 | `TileRaster.DECK` | `0xff5d4c3c` | A bridge's deck, edging a road over fresh water from 4 px a plot: the mockup's. |
| 189 | `TileRaster.UNOWNED_DIM` | `0.4` | Ground no one in the city owns is drawn this much of the way to the map's background: 40%. |
| 192 | `TileRaster.VOID` | `0xff16222c` | The map's background, which unowned ground is dimmed toward: the mockup's --map-void. |
| 195 | `TileRaster.SITE_TINT` | `0.36` | A site's tint over its ground, its resource's colour this much of the way: 36% (the mockup's most for a deposit's body). |
| 198 | `TileRaster.SITE_SPECKLE` | `0.75` | ...and on the plots its own hash speckles, 75% (the mockup's ore showing through). |
| 201 | `TileRaster.SPECKLE_SHARE` | `0.15` | The share of a site's plots speckled: 15%. |
| 204 | `TileRaster.WORKED_GREY` | `0xff929496` | A worked-out site's grey: the mockup's mined grey. |
| 207 | `TileRaster.RING` | `0xff2b2d31` | A mine's ring and centre mark: the mockup's ring. |
| 210 | `TileRaster.INSET_FROM` | `3` | Pixels a plot from which a building of more than one plot is inset a pixel: 3 (a one-plot building takes its own land's side: SMALL_MOST). |
| 213 | `TileRaster.EDGE_FROM` | `6` | ...and from which it is edged: 6 (the mockup's outlines). |
| 216 | `TileRaster.ROAD_WIDTH` | `{ 0, 0.30, 0.52, 0.86 }` | A road's width as a share of its plot, by kind (gravel, paved, highway): the mockup's 0.3, 0.52 and 0.86 - since 0.7.88 the highway's and (as a paved road's) the track's; a street's surface is its width's (SURFACE). |
| 219 | `TileRaster.ROAD_LEAST_PX` | `{ 0, 1, 2, 3 }` | ...and its least, in pixels, at the near view: 1, 2 and 3 (the mockup's 1, 1.5 and 2.5, whole). |
| 222 | `TileRaster.HIGHWAY_CASE` | `0xff1d2126` | A highway's casing, a pixel either side from 6 px (the mockup's highwayCase)... |
| 225 | `TileRaster.CENTRE_LINE` | `0xfff0cf5a` | ...and its dashed centre line from 6 px a plot, a pixel wide, on the first 55% of each plot along it (the mockup's centre, its dash 0.7 on and 0.55 off). |
| 228 | `TileRaster.LINES_FROM` | `4` | Pixels a plot from which a road is drawn as a line of its own width on its ground - a street as verge and surface (0.7.88) - and below, the whole plot in its colour: 4, the near view's image. |
| 244 | `TileRaster.YARD_GROUND` | `0xff8c8478` | A yard's ground, light ballast (spec-freight 4.3's yard). |
| 246 | `TileRaster.YARD_TRACKS` | `8` | The fan's sidings (spec-freight 4.3: 6 to 10). |
| 248 | `TileRaster.DEPOT_TRACKS` | `3` | A Rail Depot's sidings (FR7 slice 3, "2 to 3 sidings and a shed"): 3 - its box is 8 plots by 7, a Rail Terminal's 24 by 24. |
| 266 | `TileRaster.YARD_TRACK_APART` | `1.5` | Plots between the fan's tracks, middle to middle. |
| 268 | `TileRaster.YARD_LADDER` | `3` | Plots at each end over which the fan closes to the line. |
| 270 | `TileRaster.SHED_DEPTH` | `2.5, SHED_LENGTH = 0.6` | The freight shed: plots deep, and its share of the fan's length. |
| 272 | `TileRaster.ORE_HEAP` | `0xffc9b68f` | A bulk yard's stockpiles, the spec's sand colour. |
| 274 | `TileRaster.CONTAINER` | `0xffd6dde4` | A container yard's boxes, the boats' white. |
| 276 | `TileRaster.GANTRY` | `0xff2b2f36` | Its two cranes. |
| 278 | `TileRaster.WAGON` | `0xff7b4b3a` | A wagon standing. |
| 280 | `TileRaster.WAGON_LENGTH` | `0.5` | Plots: a wagon about 15 m. |
| 283 | `TileRaster.SMALL_MOST` | `0.92` | A one-plot building's side as a share of its plot: its own land's (BuildingVisual.plotSide(), 0.7.64 - a House 0.91 of the plot, a Convenience Store 0.72; the mockup's 0.6 to 0.92 by a hash before), never more than 0.... |
| 286 | `TileRaster.SET_BACK` | `0.07` | ...and set this far from the road it faces: 0.07 of a plot, so it hugs its street (the mockup's). |
| 290 | `TileRaster.VERGE` | `0xffbfd39a` | A street's verge, its right of way either side of its surface (0.7.88): the prototype's VERGE (spec-roads-and-ports.md 5, roads_proto.py). |
| 293 | `TileRaster.TRACK_DASH` | `0xff8a6a3c` | A track's dashes, a street the city has bought no road for (0.7.88): the prototype's brown (spec 2.5, 5: "a dashed brown line on the verge"). |
| 296 | `TileRaster.SURFACE` | `{ 0, DistrictPlan.HALF, DistrictPlan.FULL }` | A street's surface across its plot, by its width (Painted.width: 0, half, full): none, half the plot (15 m), the whole plot (30 m) - the right of way is 30 m (spec 2.1; the prototype's W_MIN and W_FULL). |
| 299 | `TileRaster.PACKED_DIM` | `0.35` | A building packed at the city's edge without a street (R7) is drawn this much of the way to the map's background: 35% (star RD2-2) - a shade darker, so it reads as standing apart from the streets, at its own size and ... |
| 560 | `TileRaster.YARD_CANVAS` | `ThreadLocal.withInitial(() -> new int [ 0 ])` |  |
| 827 | `TileRaster.LOOK_FROM` | `3` | Pixels a plot from which a materials building is drawn as its look rather than a box of its colour: 3 (TileRaster's INSET_FROM; the L0 image is 4). |
| 830 | `TileRaster.BENCHES` | `6` | The benches a pit or a quarry steps down by from its rim to its middle: 6. |
| 833 | `TileRaster.POND` | `0xff4f8fbf` | A leach plant's pond: #4f8fbf. |
| 836 | `TileRaster.HEADFRAME` | `0xff2b2622, WINDING = 0xff8a7f72` | A shaft's headframe: #2b2622, and the winding house in it #8a7f72. |
| 839 | `TileRaster.CONVEYOR` | `0xff9a9aa2` | The wash plant's conveyor: #9a9aa2. |
| 842 | `TileRaster.LOGS` | `0xff5a3e22` | A camp's log stacks: #5a3e22. |
| 845 | `TileRaster.CROWN` | `0xff6a9a55` | Planted forest's crowns, in rows over its darker floor: #6a9a55. |
| 848 | `TileRaster.RIDGE_DARK` | `0.18` | How much darker a works' roof ridges are than its fill: 18%. |
| 931 | `TileRaster.QUAY_DECK` | `0xff3a4655` | A quay's deck: mockup 3's jetty, #3a4655... |
| 934 | `TileRaster.QUAY_EDGE` | `0xff5a6676` | ...and its edge from EDGE_FROM px a plot: the mockup's #5a6676. |
| 937 | `TileRaster.JACKET_FILL` | `0xff2a3540` | A platform's jacket: mockup 3's #2a3540... |
| 940 | `TileRaster.OIL_TAN` | `0xffc9b68f` | ...edged in the mockup's tan, #c9b68f - its ring's and the pipe's colour too (the ore's). |
| 943 | `TileRaster.RING_ALPHA` | `0.25` | A platform's 500 m ring over the water: the tan at the mockup's 25%... |
| 946 | `TileRaster.RING_DASH_PX` | `3` | ...dashed 3 px on, 3 off along its arc (the mockup's "3 3"). |
| 949 | `TileRaster.PIPE_ALPHA` | `0.5` | A crude pipeline: the tan at the mockup's 50%, dashed 2 px on and 4 off (its "2 4"), 1.5 px wide (drawn 1 below 4 px a plot, 2 from it). |
| 951 | `TileRaster.PIPE_ON_PX` | `2, PIPE_OFF_PX = 4` | ...its dash, 2 px on and 4 off along the pipe (the mockup's "2 4"). |
| 1066 | `TileRaster.BLOCK_ALPHA` | `{ 0.4, 0.6, 0.8, 0.96 }` | A block's opacity over its ground by its share built: 40%, 60%, 80% and 96% (the mockup's ALPHA)... |
| 1069 | `TileRaster.BLOCK_FILLS` | `{ 0.12, 0.28, 0.5 }` | ...at a share built under 12%, under 28%, under 50%, and above (the mockup's). |
| 1072 | `TileRaster.BLOCK_MIDDLE` | `4` | Blocks the mockup drew at its middle view, in plots a side: 4 (120 m), from 1.4 to 3.2 px a plot... |
| 1075 | `TileRaster.BLOCK_FAR` | `8` | ...and at its far view: 8 (240 m), below 1.4 px a plot, with gravel hidden. |
| 1184 | `TileRaster.HIGHWAY_LEAST_PX` | `2` | The least a highway is drawn across, in pixels, in the blocks' views: 2 (the mockup's 1.8 at its far view, 2 at its middle). |
| 1609 | `TileRaster.RAMP_WIDTH` | `ROAD_WIDTH [ BuildingVisual.GRAVEL ]` | A ramp's slip road's width, as a share of a plot: a gravel street's, 0.3 (ROAD_WIDTH). |
| 1745 | `TileRaster.Crossings.N` | `TilePainter.RING, R = TilePainter.REACH` |  |
| 1746 | `TileRaster.Crossings.NONE` | `new Crossings()` |  |
| 2194 | `TileRaster.LOOP_RADIUS` | `2.5, LOOP_AT = 5.0` | A loop's radius, in plots, and how far from the crossing's middle along each axis a cloverleaf's loop is centred (batch FR7): 2.5 and 5.0, so a loop fills its quadrant of the 19-plot reserve. |
| 2197 | `TileRaster.TRUMPET_RADIUS` | `2.0, TRUMPET_OUT = 5.5, TRUMPET_ACROSS = 3.0` | A trumpet's loop: its radius and how far out along its stub and across it its centre lies, in plots - 2.0, and 5.5 and 3.0, inside the 11-plot reserve. |
| 2200 | `TileRaster.LOOP_WIDTH` | `0.34` | A loop's and a slip's road across, as a share of a plot: 0.34, a pixel or two wider than a gravel street's, so it reads as a ramp at 8 px a plot and not as a street. |
| 2203 | `TileRaster.SLIP_OFF` | `0.45, SLIP_OUT = 9.0, TRUMPET_SLIP_OUT = 10.0` | Where a slip leaves its highway, in plots from the junction's middle: just off the highway's band (0.45) and (along it) 9.0 out for a cloverleaf, 10.0 for a trumpet's stub. |
| 2287 | `TileRaster.KERB_RADIUS` | `0.25` | A kerb corner's radius, in plots: 0.25 (7.5 m), the corner of a + of two streets in a homes cell - and of a street's T on an arterial there. |
| 2290 | `TileRaster.ESTATE_T_RADIUS` | `0.5` | An estate T's corner radius, in plots: 0.5 (15 m) - wide, where trucks turn (spec-freight 4.6: "a T with wide radius corners"). |
| 2419 | `TileRaster.SIDING_SHED` | `0.7` | A siding's loading shed, the share of a plot it fills (FR7 slice 3): 0.7, a small block at the siding's end beside the works. |
| 2766 | `TileRaster.HIGHWAY_LEG` | `2` | How far the straight legs of a highway bend are drawn past its tangent points, in plots: two - past the last plot the bend replaces (a plot and a half out), where the plots' own bands take over. |
| 3016 | `TileRaster.ROAD_SMALL` | `{ 0, 0xffbdb8ae, 0xff6a7077, 0xff3b4048 }` | A road plot's colour below LINES_FROM px a plot, where it fills its plot, by kind: gravel's and the highway's own, a paved road's casing - its own grey is the grass's brightness, and a pixel of it vanishes among the h... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 1749 | `final byte[] role` | The role of the street on each plot of the ring, by TilePainter.ringIndex(); null with no junction street about the tile. |
| 1751 | `final int[] group` | The crossing each plot belongs to, or -1; null with none. |
| 1753 | `int groups` | The crossings: each one's rectangle (first and last column and row, in the tile's plots, which may lie off the tile), the roads that leave it, and whether it is a roundabout. |
| 1754 | `int[] x0` |  |
| 1755 | `boolean[] round` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 96 | 2939 | **type** `public final class TileRaster` | A painted tile as pixels: an int[] of 0xAARRGGBB, a row at a time, at a whole number of pixels a plot - what the map view hands PixelWriter.setPixels() for a tile's image. |
| 98 | 1 | `private TileRaster()` |  |

### RAIL DRAWN AS RAIL (0.8.1, batch FR0; spec-freight 4.3, D15, D18) (lines 131-158)

### JUNCTIONS DRAWN BY RULE (0.8.1, batch FR0; spec-freight 4.6, D19) (lines 159-229)

### YARDS DRAWN AS YARDS (0.8.1, batch FR0; spec-freight 4.3, D15) (lines 230-300)

| line | len | member | says |
|---:|---:|---|---|
| 251 | 3 | `public static int tracksOf(BuildingVisual.Type t)` | The sidings of a yard of type t: a Rail Depot's DEPOT_TRACKS, else YARD_TRACKS (FR7 slice 3). |
| 261 | 4 | `public static int wagonTracksOn(int wagonTracks, int tracks)` | The sidings of a yard of `tracks` that wagons stand on, from the shed's side, when the railway's picture gives `wagonTracks` of a Terminal's YARD_TRACKS (CityMap.wagonTracksOf()): scaled to the yard's own sidings, and... |

### THE NEAR VIEW (J3: a tile at 4 or 8 px a plot, MapFrame.L0) (lines 301-813)

| line | len | member | says |
|---:|---:|---|---|
| 316 | 97 | `public static void raster(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | Fills img with the tile at px pixels a plot: (32 px) squared pixels, row by row. |
| 438 | 47 | `static int[] yardLayout(boolean[] rail, int w, int h)` | Where a yard's fan lies in its box, from the box's track (FX3, 0.8.4): rail[y * w + x] is whether the box's plot (x, y) holds track, the box w plots across and h down. |
| 513 | 14 | `private static void drawYard(TilePainter.Input in, TilePainter.Painted p, int b, BuildingVisual.Type t, int px, int[] img, int ...` | A Rail Terminal's yard as a yard, not its class's plain box (0.8.1, batch FR0; spec-freight 4.2 and 4.3, D15), and since FR7 (slice 3) a Rail Depot's: ballast ground, a fan of tracksOf() sidings (a Terminal's YARD_TRA... |
| 538 | 11 | `private static void drawWholeYard(TilePainter.Input in, TilePainter.Painted p, int e, BuildingVisual.Type t, int px, int[] img,...` | A yard the tile is given whole (FX5 Y2), drawn clipped to the tile: its picture - ground, fan, ladders, shed, heaps or boxes and cranes, wagons - is laid from the entry's whole box and layout (Input.yards, the very la... |
| 551 | 8 | `private static int[] yardCanvas(int size)` | The canvas a whole yard is drawn on: this thread's, at least `size` pixels (the raster runs on the map's workers and the screen's thread alike). |
| 568 | 10 | `static int yardOf(TilePainter.Input in, TilePainter.Painted p, int b)` | The entry of Input.yards that is the yard of building b (FX5 Y2): the building's part on the tile is the entry's whole box clipped to it, of the entry's type; -1 for none (a yard with no entry is a hand-built one, dra... |
| 587 | 116 | `private static void yardPicture(TilePainter.Input in, BuildingVisual.Type t, int px, int[] img, int w, int x0, int y0, int x1, ...` | A yard's picture into img (w pixels across) inside [x0, x1) x [y0, y1): the box at plots (bx, by), bw across and bh down, in img's plots, its layout (yardLayout()) laid for a box of boxW x boxH plots - the part of the... |
| 712 | 15 | `private static void headTrack(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int boxX, int boxY, int boxW, int...` | The track the fan's span leaves out, over the yard's ground (FX3): a line crossing the box runs through the head and meets the fan at its throat. |
| 734 | 10 | `private static void yardTrack(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img)` | One plot of the track that crosses a yard's head, drawn over the yard's ground (FX3, 0.8.4): drawTrackLook()'s look, with the track beside it read from the runs (p.run), because a building stands on every plot of the ... |
| 746 | 27 | `private static void containers(int px, int[] img, int w, int x0, int y0, int x1, int y1, boolean ew, int fanA, int fanB, int fa...` | A container yard's boxes, in rows, and its two gantry cranes across the fan. |
| 775 | 19 | `private static void stockpiles(int px, int[] img, int w, int x0, int y0, int x1, int y1, boolean ew, int fanA, int fanB, int fa...` | A bulk yard's three stockpiles, side by side along the fan. |
| 796 | 11 | `private static void fillEllipse(int[] img, int w, int cx, int cy, int rx, int ry, int fill, int edge)` | A filled ellipse, its edge a pixel darker. |
| 809 | 4 | `private static boolean outside(int x, int y, int cx, int cy, int rx, int ry)` | Whether (x, y) lies outside the ellipse centred at (cx, cy). |

### THE MATERIALS AS THEMSELVES (0.8.0, batch I14; spec-materials 5's I14 (lines 814-925)

| line | len | member | says |
|---:|---:|---|---|
| 851 | 74 | `static void drawLook(TilePainter.Input in, BuildingVisual.Type t, int site, int px, boolean edged, int[] img, int w, int x0, in...` | One materials building's look over its box [x0, x1) x [y0, y1) in pixels: a site's measured from the site's whole square. |

### AT SEA (0.7.97, batch O13; TilePainter's AT SEA; mockup 3's colours) (lines 926-1060)

| line | len | member | says |
|---:|---:|---|---|
| 961 | 63 | `static void atSea(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The works at sea over the tile's picture: each quay plot the deck, edged from EDGE_FROM; a jacket the mockup's platform, edged; a well its tan dot; then every pixel of a plot a ring or a pipe crosses that lies on its ... |
| 1026 | 3 | `static boolean runHighway(TilePainter.Painted p, int i)` | Whether plot i of the runs is a highway's (its own, or the track bridging it), under a building or not (0.7.89). |
| 1035 | 16 | `private static void overBuildings(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A highway passing over a building (0.7.89): a mine standing on its site where the elevated highway crosses it - drawn over the building, its arms to the highway beside it (spec 2.7: elevated). |
| 1053 | 7 | `private static boolean runHighwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | Whether the plot a step from plot i, across the tile's edge too, is a highway's of the runs (under a building or not). |

### THE MIDDLE AND FAR VIEWS AS BLOCKS (J3b: the mockup's levels 1 and 2) (lines 1061-1421)

| line | len | member | says |
|---:|---:|---|---|
| 1086 | 96 | `public static void blocks(TilePainter.Input in, TilePainter.Painted p, int px, int block, boolean gravel, int[] img)` | A tile at px pixels a plot as the mockup drew its middle and far views: its ground, then blocks of `block` plots a side, each in its dominant kind's colour - by its buildings' plots, the homes' houses and flats togeth... |
| 1187 | 3 | `static boolean isHighway(TilePainter.Painted p, int i)` | Whether plot i is a highway's own (a run's, not a street of highway plots). |
| 1192 | 3 | `static boolean isStreet(TilePainter.Painted p, int i)` | Whether plot i is a street (a track among them), or a highway's plot with a street beneath. |
| 1197 | 4 | `static int roadKind(TilePainter.Painted p, int i)` | The kind of road on plot i - GRAVEL, PAVED, HIGHWAY or (0.7.88) TilePainter.TRACK - a road's plot, or where a street crosses the track - or 0. |
| 1203 | 8 | `static int faceOf(TilePainter.Painted p, int i)` | The side of plot i a street lies on, north, east, south, west, or -1 for none. |
| 1213 | 10 | `static int[] crossSection(int kind, int px)` | A road's line across its plot, by kind at px pixels a plot: {its whole width, its casing either side} - the highway's and (as a paved road's) the track's. |
| 1225 | 6 | `private static boolean streetAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | Whether the plot a step from plot i - across the tile's edge too, as the plans have it (Input.beyond) - is a way of the street at i: a street, or a highway's plot a street passes beneath. |
| 1233 | 7 | `private static boolean highwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | ...and a highway's own plot, a step from plot i. |
| 1242 | 12 | `private static boolean straightHighwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | ...and a straight highway's, a step from plot i: not a 45-degree stretch's plot, whose marks (across the tile's edge too: marksBeyond) say it is no highway beside a straight one for a stack's test. |
| 1256 | 7 | `private static boolean railAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | ...and the railway's track (since 0.7.89 where it bridges a highway too: the runs' RAIL_OVER). |
| 1271 | 8 | `private static boolean yardLineAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | Whether the plot a step from plot i, on this tile, is a plot of a yard's box that carries the runs' track (FX5 Y2): the line the yard stands on, which runs on under its ground into the fan. |
| 1281 | 5 | `private static int armsOf(TilePainter.Input in, TilePainter.Painted p, int i)` | The track beside plot i, north, east, south and west, one bit each (bit d for direction d): its neighbours' track, and a yard's line running on under the yard's ground. |
| 1288 | 3 | `static boolean diagonal(TilePainter.Input in, int i)` | Whether plot i is a 45-degree stretch's (the runs' M_DIAG): drawn as a band along its line. |
| 1299 | 6 | `static void diagBand(int[] img, int w, int i, int px, byte mark, double half, int c)` | A 45-degree stretch's band across plot i (spec 2.7: "a curve is drawn smooth; in plots it is a staircase"): the pixels within `half` pixels of the stretch's line - through the plot's middle, or for a staircase's corne... |
| 1313 | 12 | `private static void diagLine(int[] img, int w, int i, int px, int code, double pxl, double pyl, double half, double along, int c)` | The pixels of plot i within `half` pixels of a line at a 45-degree heading's code through the point (pxl, pyl) of the plot's pixels, and no farther along the line than `along` pixels either side of that point (FX3: di... |
| 1327 | 15 | `private static int stepMarkAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | The marks of the 45-degree stretch's step plot at (x, y) - in the tile, or just across one of its edges (marksBeyond) - or -1 where that plot is no step: a corner, a straight highway, other ground. |
| 1353 | 20 | `private static void diagOpen(TilePainter.Input in, TilePainter.Painted p, int i, int px, double half, int c, int[] img)` | Where a 45-degree stretch passes between two of its steps the line clips the corner of the plot that is not the run's - the staircase's corners are on the other side of it - and the band would leave a notch there (FX3... |
| 1380 | 3 | `private static boolean bendReplaces(TilePainter.Input in, TilePainter.Painted p, int i)` | Whether a highway bend draws plot i instead of its cell (FX5 slice D): the plot is one Input.curved names and no bridge's - a bridge's deck is straight and the arc is never drawn over water, so a curved plot over fres... |
| 1385 | 8 | `private static boolean highwayPlot(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether plot (x, y), in the tile or just across one of its edges on an axis, is a highway's own. |
| 1406 | 15 | `private static void diagEndOpen(TilePainter.Input in, TilePainter.Painted p, int i, int px, double half, int c, int[] img)` | An arm that ends on its diagonal (FX5 slice D) ends squared across its heading at its last step's far edge. |

### THE WAYS AS LINES (0.7.88, batch RD2; from LINES_FROM px a plot) (lines 1422-1690)

| line | len | member | says |
|---:|---:|---|---|
| 1455 | 152 | `private static void ways(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The ways as lines (from LINES_FROM px a plot; spec 5): each street's verge, its whole plot on dry ground; the decks of bridges; the track (as rail since 0.8.1); each street's surface down its middle along each way it ... |
| 1621 | 37 | `private static void ramps(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The ramps (0.7.89; spec 2.7: "ramps where an arterial crosses, every other one, and at an arm's end"): at a highway's plot the runs mark a ramp, where a street passes beneath it, a slip road on each verge plot at its ... |
| 1660 | 8 | `private static boolean isRunHighway(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether plot (x, y) - in the tile, or just across one of its edges - is a highway's of the runs. |
| 1670 | 7 | `private static boolean isStreetAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether plot (x, y) - in the tile, or just across one of its edges - is a street's. |
| 1679 | 11 | `private static void segment(int[] img, int w, int x0, int y0, int px, double ax, double ay, double bx, double by, double half, ...` | The pixels of a plot (its corner at x0, y0, px a side) within `half` of the segment from (ax, ay) to (bx, by), in the plot's pixels: colour c. |

### junctions' helpers (lines 1691-1903)

| line | len | member | says |
|---:|---:|---|---|
| 1701 | 13 | `private static byte streetRoleAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | The role of the street at plot (x, y) - ARTERIAL, BOULEVARD, ACCESS or STREET: in the tile, from its painted plots; the plot just across one of its edges, from Input.beyond; and (FX5) any other plot within TilePainter... |
| 1716 | 5 | `private static byte roleOfCode(int code)` | The role of a street code (the S_ codes) as streetRoleAt() answers it: 0 for none or a street beneath a highway. |
| 1723 | 3 | `private static boolean junctionRole(byte role)` | Whether a street role is one a junction is drawn for. |
| 1744 | 153 | **type** `private static final class Crossings` | The crossings of a tile's streets (FX5; Jerus, 2026-10-20, "Mostly signals"), read once for the tile over the plots about it - the tile and TilePainter.REACH more on every side, which Input.ring gives - so a crossing ... |
| 1757 | 4 | `private Crossings()` _(in TileRaster.Crossings)_ |  |
| 1762 | 5 | `private Crossings(byte[] role)` _(in TileRaster.Crossings)_ |  |
| 1769 | 56 | `static Crossings of(TilePainter.Input in, TilePainter.Painted p)` _(in TileRaster.Crossings)_ | The crossings of the tile in/p draw. |
| 1827 | 10 | `private int open(int x, int y)` _(in TileRaster.Crossings)_ | Starts a crossing at plot (x, y); its number. |
| 1839 | 4 | `private void grow(int g, int x, int y)` _(in TileRaster.Crossings)_ | Adds plot (x, y) to crossing g's rectangle. |
| 1845 | 4 | `byte roleAt(int x, int y)` _(in TileRaster.Crossings)_ | The role of the street at plot (x, y) of the ring; 0 for none or for a plot off it. |
| 1851 | 4 | `int at(int x, int y)` _(in TileRaster.Crossings)_ | The crossing plot (x, y) is a junction plot of, or -1. |
| 1857 | 4 | `boolean signalAt(int x, int y)` _(in TileRaster.Crossings)_ | Whether plot (x, y) is a junction plot of a signal. |
| 1863 | 3 | `boolean inRect(int g, int x, int y)` _(in TileRaster.Crossings)_ | Whether plot (x, y) lies in crossing g's rectangle. |
| 1875 | 21 | `private int roadsLeaving(int g)` _(in TileRaster.Crossings)_ | The roads that leave crossing g: on each of the rectangle's four sides each maximal run of street plots just outside it is one road, and a street plot diagonally off a corner is one more where the street goes on out a... |
| 1899 | 4 | `private static int[] surfaceAt(TilePainter.Painted p, int i, int px)` | A street's surface across its plot at px pixels a plot: {its width, its offset from the plot's edge}. |

### 1. signals: where arterials, boulevards and access roads cross (FX5: one box a crossing) (lines 1904-1959)

| line | len | member | says |
|---:|---:|---|---|
| 1906 | 26 | `private static void signals(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, Crossings cr)` |  |
| 1934 | 25 | `private static void drawSignalArm(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int dir)` | A zebra crossing and its stop line on one arm of a signal, at the edge facing the junction. |

### 2. roundabouts: where ROUNDABOUT_ROADS or more roads meet (FX5; before, wherever boulevards crossed) (lines 1960-2003)

| line | len | member | says |
|---:|---:|---|---|
| 1962 | 18 | `private static void roundabouts(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, Crossings cr)` |  |
| 1982 | 5 | `private static boolean roundaboutTarget(TilePainter.Input in, TilePainter.Painted p, int i)` | Whether a roundabout's ring or island may be drawn on plot i: a street or empty ground, never a building, the track, a highway or water. |
| 1989 | 14 | `private static void drawRoundaboutOn(TilePainter.Input in, int i, int px, int[] img, double centreX, double centreY, double out...` | The ring's and island's pixels of a roundabout centred on (centreX, centreY), in the tile's pixels, with an outer and an island radius in pixels, that fall on plot i. |

### 3. diamonds: a street passes beneath a highway (lines 2004-2058)

| line | len | member | says |
|---:|---:|---|---|
| 2006 | 29 | `private static void diamonds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` |  |
| 2038 | 20 | `private static void drawSlip(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int hx, int hy, boolean rowHw, int...` | One diamond slip along the verge beside a highway, from the street out for SLIP_LENGTH plots. |

### 4. a highway arm's end (lines 2059-2140)

| line | len | member | says |
|---:|---:|---|---|
| 2069 | 59 | `private static void highwayEnds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A highway arm's end (spec 2.7's ramp at an arm's end): a highway plot the runs mark as a ramp with no street beneath, where the highway runs on exactly one of its four sides - the band narrows to a paved road's and a ... |
| 2130 | 10 | `private static void drawHighwayApron(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int tx, int ty, boolean ve...` | A paved apron the paved road's width across the plot just past a highway's open end, when that plot is empty ground. |

### 5. a stack: highways cross (lines 2141-2190)

| line | len | member | says |
|---:|---:|---|---|
| 2151 | 26 | `private static void stack(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A stack where two straight highways cross: a highway plot with another beside it on both axes. |
| 2179 | 11 | `private static void drawStackSlip(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int cx, int cy, int dx, int dy)` | One corner slip of a stack, across the plot diagonal to the crossing. |

### 6. interchanges' loops: a cloverleaf's and a trumpet's reserve (batch FR7; spec-freight 4.6, D19) (lines 2191-2283)

| line | len | member | says |
|---:|---:|---|---|
| 2213 | 48 | `private static void loops(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The loops of every reserve that reaches the tile, from JUNCTIONS_FROM px a plot: a cloverleaf's four loops, each with a slip to either highway; a trumpet's one loop with a slip to its stub and to the highway it ends a... |
| 2263 | 4 | `private static double[] spoke(double ax, double ay, double cx, double cy, double r)` | A slip from (ax, ay) to the ring about (cx, cy) of radius r: the straight line toward the ring's middle, ending on it. |
| 2269 | 5 | `private static double segmentDistance(double x, double y, double ax, double ay, double bx, double by)` | The distance from (x, y) to the segment from (ax, ay) to (bx, by). |
| 2276 | 7 | `private static boolean reserveJunction(TilePainter.Input in, int x, int y)` | Whether plot (x, y) of the tile is the junction of a reserve that reaches it: where loops replace a stack's corner slips. |

### 7. kerb corners and estate T's (batch FR7; spec-freight 4.6's table) (lines 2284-2397)

| line | len | member | says |
|---:|---:|---|---|
| 2305 | 48 | `private static void kerbs(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | Kerb corners and estate T's, from JUNCTIONS_FROM px a plot, over the streets' surfaces and their verges: at a + of two streets (a cell's streets, role STREET, on all four sides) each of its four inner corners is round... |
| 2355 | 14 | `private static boolean estateAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether a building of an estate's classes - industry, farms, utilities, mines and wells, transport; a homes cell holds the rest - stands within two plots of plot (x, y): the painter's reading of an estate. |
| 2378 | 19 | `private static void fillet(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int jx, int jy, int cx, int cy, int ...` | One rounded corner: the corner point (cx, cy), in pixels from plot (jx, jy)'s corner, where two bars meet; (sx, sy) the way out of it, toward the verge. |

### the track as rail, and its crossings (0.8.1, batch FR0; spec-freight 4.3) (lines 2398-2608)

| line | len | member | says |
|---:|---:|---|---|
| 2411 | 6 | `private static void track(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The track (0.7.72; redrawn as rail, 0.8.1, batch FR0, spec-freight 4.3): each plot's middle and its arms to the track beside it - past the tile's edge too, where the line runs on - as a ballast bed, and from 4 px a pl... |
| 2426 | 20 | `private static void sidingSheds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A siding's loading shed (FR7 slice 3): on the plot marked M_SHED, the last of a siding beside its works' box, a small block in TRANSPORT's colours over the track's end, edged from EDGE_FROM. |
| 2448 | 3 | `private static int bedWidth(int px, boolean doubled)` | The ballast bed's width, whole pixels: BED, or BED_DOUBLE where the plot is marked double track. |
| 2453 | 4 | `private static void drawTrackLook(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img)` | The track's look on one plot that carries rail: its ballast bed, sleepers and rails (a 45-degree stretch's just a bed and one rail). |
| 2459 | 23 | `private static void drawTrackLook(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int arms)` | ...with the track beside it given, bit d for direction d (a yard's head reads it from the runs: yardTrack()). |
| 2484 | 10 | `private static void drawBed(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int arms)` | The track's ballast bed: the plot's middle and its arms to the track beside it, as the bed's whole width. |
| 2496 | 22 | `private static void drawSleepers(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boolean n...` | The sleepers, a pixel thick across the bed, every `step` pixels along the way the track runs. |
| 2520 | 8 | `private static void drawSleeperTicks(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boole...` | Below RAILS_FROM px a plot, one sleeper tick across the bed at the start of each plot along the track. |
| 2530 | 5 | `private static void drawRails(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boolean ns, ...` | The rails, a pixel each, along the way the track runs. |
| 2537 | 29 | `private static void drawRailsFull(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boolean ...` | From RAILS_FROM px a plot: two rails a track, double track's two tracks at the plot's middle +/- TRACKS_APART. |
| 2568 | 22 | `private static void drawRailsLow(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boolean n...` | Below RAILS_FROM px a plot: one STEEL line down the track's middle, two at double track's tracks. |
| 2592 | 7 | `private static void railH(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int row, int c, ...` | A rail a pixel wide along an east-west track at row `row`, its middle and its arms. |
| 2601 | 7 | `private static void railV(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int col, int c, ...` | ...and a north-south track's, at column `col`. |

### the curves (FX5) (lines 2609-2968)

| line | len | member | says |
|---:|---:|---|---|
| 2629 | 8 | `private static void bends(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int cx0, int cy0, int cx1, int cy1, b...` | The railway's bends (Input.bends, TrackCurves), drawn on the pixels of the tile within the box [cx0, cx1) x [cy0, cy1): for each, its arc between its tangent points and its two legs' straights from each tangent point ... |
| 2639 | 3 | `private static TrackCurves.Bend inPixels(TrackCurves.Bend b, int px)` | The bend scaled to pixels a plot: the same arc, its corner, radius and tangent length times px. |
| 2651 | 18 | `private static double pathLateral(TrackCurves.Bend s, double back1, double back2, double x, double y, double[] out)` | How far point (x, y) lies from the path of bend s (in pixels): its arc, the straight of `back1` before its start along the incoming leg, and the straight of `back2` after its end along the outgoing one - the least of ... |
| 2670 | 65 | `private static void drawBend(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, TrackCurves.Bend b, boolean double...` |  |
| 2741 | 23 | `private static void blockBends(TilePainter.Input in, int px, int[] img)` | The bends in the blocks' views (FX5): each bend's arc and straights in RAIL_DARK at the width the blocks draw track (a plot, px pixels), in place of the cells of the plots it replaces. |
| 2782 | 24 | `private static void highwayBends(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, double half, int c)` | The highways' bends (Input.bends of net HIGHWAY, FX5 slice D), drawn as the carriageway along the arc: the pixels within `half` pixels of each bend's path in colour c, one pass of the highways' casing or surface (ways... |
| 2815 | 26 | `private static void highwayBendDashes(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int dash)` | The highways' bends' centre line (FX5 slice D): a dash over the first 55% of each plot's length of the path from the arc's start (`dash` pixels of every px), a pixel wide on the row or column the straight's own dash t... |
| 2851 | 18 | `private static void ladderRails(int[] img, int w, int px, boolean ew, double apexAlong, double lineAcross, double sidingAlong, ...` | One of a yard's ladders (FX5), from the fan's apex on the line to its siding's start, as TrackCurves.ladder() lays it - an S tangent to the line and to the siding, the points the trains' routes run along - drawn as th... |
| 2877 | 49 | `private static void railCrossings(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The railway's crossings (0.8.1, spec-freight 4.3, D18): the street's surface already lies over the bed; from RAILS_FROM px a plot, draw the rails through a level crossing with its crossbucks and half barriers, and an ... |
| 2928 | 5 | `private static void drawCrossbucks(int px, int[] img, int w, int x0, int y0)` | A level crossing's two crossbucks: a white X in the plot's north-west and south-east corners. |
| 2935 | 6 | `private static void drawX(int[] img, int w, int x0, int y0, int s)` | An X of side `s` in CROSSBUCK, its corner at (x0, y0). |
| 2943 | 25 | `private static void drawBarriers(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int w, int x0, int y0, ...` | A level crossing's half barriers: a bar across the lane coming in at each edge where the street enters the plot. |

### A RECTANGLE OF PIXELS, AND THE PLOTS' COLOURS (lines 2969-3034)

| line | len | member | says |
|---:|---:|---|---|
| 2974 | 10 | `private static void grain(TilePainter.Input in, int[] img, int w, int x0, int y0, int x1, int y1, int c)` | Speckles GRAVEL_GRAIN_SHARE of the box's pixels with c, each by a hash of its pixel in the tile and the tile's place, so the same street speckles the same at every paint. |
| 2985 | 6 | `private static void fillRect(int[] img, int w, int x0, int y0, int x1, int y1, int c)` |  |
| 2993 | 10 | `static int groundColour(TilePainter.Input in, TilePainter.Painted p, int i)` | A plot's ground: its terrain - forest under a building cleared (0.7.88; spec 2.6) - its site's tint or grey, dimmed when unowned: what a road is drawn on from LINES_FROM px. |
| 3005 | 9 | `static int siteTint(TilePainter.Input in, int i, int s, int c)` | A site's plot over its ground: its resource's colour, speckled, half grey while its holding is worked, grey worked out. |
| 3019 | 7 | `static int plotColour(TilePainter.Input in, TilePainter.Painted p, int i)` | A plot's colour before its building: its ground, its site's tint or grey, its road's colour - a track's brown (0.7.88) - or (0.7.72) its track's, where no street crosses it - and dimmed when unowned. |
| 3028 | 6 | `static int blend(int a, int b, double f)` | a toward b by share f, channel by channel, opaque. |

