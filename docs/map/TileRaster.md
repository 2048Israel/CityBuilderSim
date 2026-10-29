# TileRaster.java - 4,262 lines · 157 methods · 104 constants · model

`ham/citybuildersim/TileRaster.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

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
> (0.8.18; Jerus, 2026-10-20: "do trains make 90 degree turns?") a corner of
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
> ... (64 more lines in the source)

**Uses:** [TilePainter](TilePainter.md) (586), [BuildingVisual](BuildingVisual.md) (125), [World](World.md) (36), [CityRuns](CityRuns.md) (28), [TrackCurves](TrackCurves.md) (26), [DistrictPlan](DistrictPlan.md) (8), [Resource](Resource.md) (3), [StreetCurves](StreetCurves.md) (1), [InterchangeGround](InterchangeGround.md) (1)

**Used by (27):** [BridgeMapCheck](BridgeMapCheck.md), [BuildingVisual](BuildingVisual.md), [CityMap](CityMap.md), [CityRuns](CityRuns.md), [CrossingCheck](CrossingCheck.md), [DistrictPicture](DistrictPicture.md), [EarthTerrainCheck](EarthTerrainCheck.md), [FoundingScreen](FoundingScreen.md), [GlobeCheck](GlobeCheck.md), [GlobePicture](GlobePicture.md), [JunctionCheck](JunctionCheck.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapFrame](MapFrame.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [RailDrawCheck](RailDrawCheck.md), [ReadPathCheck](ReadPathCheck.md), [ReliefRenders](ReliefRenders.md), [RunRenders](RunRenders.md), [RunWorksCheck](RunWorksCheck.md), [SlopeRenders](SlopeRenders.md), [TrainSchedule](TrainSchedule.md), [TrainShapes](TrainShapes.md)

## Sections

| line | section |
|---:|---|
| 216 | RAIL DRAWN AS RAIL (0.8.1, batch FR0; spec-freight 4.3, D15, D18) |
| 244 | JUNCTIONS DRAWN BY RULE (0.8.1, batch FR0; spec-freight 4.6, D19) |
| 273 | TUNNELS AND VIADUCTS (ET4, 2026-10-25; spec-earth2-terrain 4.3, D10) |
| 332 | YARDS DRAWN AS YARDS (0.8.1, batch FR0; spec-freight 4.3, D15) |
| 403 | THE NEAR VIEW (J3: a tile at 4 or 8 px a plot, MapFrame.L0) |
| 928 | THE MATERIALS AS THEMSELVES (0.8.0, batch I14; spec-materials 5's I14 |
| 1040 | AT SEA (0.7.97, batch O13; TilePainter's AT SEA; mockup 3's colours) |
| 1175 | THE MIDDLE AND FAR VIEWS AS BLOCKS (J3b: the mockup's levels 1 and 2) |
| 1559 | THE AVENUES (batch CL4; the study spec-city-layout 5.2) |
| 1940 | THE CURVES (batch CL4, slice D; the study spec-city-layout 5.2) |
| 2215 | THE WAYS AS LINES (0.7.88, batch RD2; from LINES_FROM px a plot) |
| 2563 | · a wide road is one carriageway (MF1, slice C) |
| 2670 | · junctions' helpers |
| 2997 | · 1. signals: where arterials, boulevards and access roads cross (FX5: one box a crossing) |
| 3072 | · 2. roundabouts: where ROUNDABOUT_ROADS or more roads meet (FX5; before, wherever boulevards crossed) |
| 3116 | · 3. diamonds: a street passes beneath a highway |
| 3171 | · 4. a highway arm's end |
| 3253 | · 5. a stack: highways cross |
| 3303 | · 6. interchanges' loops: a cloverleaf's and a trumpet's reserve (batch FR7; spec-freight 4.6, D19; leaves since MF1) |
| 3418 | · 7. kerb corners and estate T's (batch FR7; spec-freight 4.6's table) |
| 3540 | · the track as rail, and its crossings (0.8.1, batch FR0; spec-freight 4.3) |
| 3828 | · the curves (FX5) |
| 4195 | A RECTANGLE OF PIXELS, AND THE PLOTS' COLOURS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 144 | `TileRaster.GROUND` | `{ 0xffa8c47e, 0xff527c45, 0xff78b4dc, 0xff2f5d88, 0xffe6daaa, 0xffdfe7ee, 0xf...` | Each ground class's colour, by World's class (GRASS, FOREST, FRESH, SALT, SAND): the mockup's legend; and since 0.7.127 (batch E7; spec-globe 3.9) ICE, the founding globe's ice, 0xdfe7ee, and DESERT, CSS's tan (#d2b48... |
| 153 | `TileRaster.RELIEF_GREY` | `{ 0, 0xffc8c3b8, 0xff9b968c, 0xff6c6862 }` | The grey each relief paints on open dry ground (ET1 slice D; spec-earth2-terrain 9.3; Jerus 2026-10-12, D4), by World's relief (FLAT, FOOTHILL, STEEP, VERY_STEEP): FLAT none - flat ground keeps its cover's colour - an... |
| 161 | `TileRaster.FOREST_MIX` | `{ 0, 0.50, 0.50, 0.60 }` | How far forest on a slope is shaded toward its slope's grey (D5, the study's way B; the mockup's mixes, estimated by eye on Medellin): FLAT none, foothill and steep half the way, very steep 0.60 - forest stays forest ... |
| 164 | `TileRaster.COVER_RELIEF` | `new int [ World.RELIEFS * World.CLASSES ]` | Every cover's colour at every relief, by relief x CLASSES + cover: the table colour() reads - GROUND on the FLAT row, built once. |
| 202 | `TileRaster.ROAD` | `{ 0, 0xffbdb8ae, 0xffa4aab0, 0xff3b4048 }` | Each road kind's colour (gravel, paved, highway): the mockup's MAP, but gravel's. |
| 205 | `TileRaster.GRAVEL_GRAIN` | `0xff9a958b` | The loose stone's speckle on a gravel street from 4 px a plot (the near view, L0): a shade darker than its grey. |
| 208 | `TileRaster.GRAVEL_GRAIN_SHARE` | `.18` | The share of a gravel street's pixels speckled: 18%. |
| 211 | `TileRaster.RAIL_LINE` | `0xff707070` | The railway's line, and its casing from 4 px a plot (0.7.72) until 0.8.1 drew the track as rail; since, a track plot's colour in raster() below LINES_FROM px a plot alone (plotColour()): openstreetmap-carto's rail gre... |
| 214 | `TileRaster.RAIL_DASH` | `0xffffffff` | ...and its dashes, white on every other plot along it until 0.8.1: carto's rail dash. |
| 219 | `TileRaster.BALLAST` | `0xff6f675c` | The track's ballast bed (spec-freight 4.3's grey-brown #6f675c). |
| 221 | `TileRaster.SLEEPER` | `0xff4a3f35` | A sleeper across the bed (spec-freight 4.3's #4a3f35). |
| 223 | `TileRaster.STEEL` | `0xffc9ced3` | The rails' steel (spec-freight 4.3's #c9ced3). |
| 225 | `TileRaster.RAIL_DARK` | `0xff3a3632` | The line the track is at the middle and far views (spec-freight 4.3's dark #3a3632). |
| 228 | `TileRaster.RAILS_FROM` | `6` | Pixels a plot from which the track is two rails on sleepers (the 8-px raster). |
| 230 | `TileRaster.BED` | `0.52` | A single track's bed, a share of the plot: the paved road's width, as the track has today. |
| 232 | `TileRaster.BED_DOUBLE` | `0.86` | Double track's bed, a share of the plot: the highway's. |
| 234 | `TileRaster.GAUGE` | `0.24` | Between a track's two rails, a share of the plot (at 8 px: the rails a pixel either side of the track's middle). |
| 236 | `TileRaster.TRACKS_APART` | `0.44` | Between double track's two tracks, middle to middle. |
| 238 | `TileRaster.SLEEPERS_EVERY` | `0.25` | A sleeper every quarter plot (spec-freight 4.3: "sleepers across it every 0.25 plot"). |
| 240 | `TileRaster.CROSSBUCK` | `0xffffffff` | A level crossing's white crossbuck (spec-freight 4.3 "Crossings"). |
| 242 | `TileRaster.BARRIER` | `0xffd04a3c` | ...and its barrier's red (spec-freight 4.3 "Crossings"). |
| 247 | `TileRaster.JUNCTIONS_FROM` | `6` | Pixels a plot from which junctions are drawn (the 8-px raster): signals, roundabouts, diamonds and stacks, and since FR7 kerb corners and interchanges' loops; a highway's end comes down to the ground wherever the ways... |
| 249 | `TileRaster.JUNCTION_BOX` | `0xff8e949a` | A signalled junction's box, a shade darker than paved. |
| 251 | `TileRaster.ZEBRA` | `0xfff2f2f2` | A crossing's stripes and stop lines. |
| 253 | `TileRaster.ZEBRA_DEPTH` | `0.3` | Plots: a zebra crossing's depth along its arm. |
| 255 | `TileRaster.ROUNDABOUT_RADIUS` | `0.75` | Plots: a roundabout's outer radius, spec: "a circle of 1.5 plots" - about a crossing one plot wide; since FX5 a wider crossing's ring is that and half of the rectangle's longer side less a plot more. |
| 257 | `TileRaster.ROUNDABOUT_ISLAND` | `0.35` | Plots: its planted middle's radius, widened as the ring is. |
| 259 | `TileRaster.ISLAND` | `0xff6f9a4f` | The planted middle. |
| 261 | `TileRaster.SLIP_LENGTH` | `2` | Plots: a diamond's slip road along the verge each side of its street. |
| 263 | `TileRaster.JUNCTION_RUN` | `4` | Plots: the least run of junction streets through a plot along an axis for a road to cross there (FX3): a road up to three plots wide - a cell's boulevard, two deep, beside the next cell's road, as a tile sees it past ... |
| 265 | `TileRaster.ROUNDABOUT_ROADS` | `5` | Roads: the fewest that leave a crossing (counted by Crossings.roadsLeaving(): each run of streets along a side of its rectangle, each slanted street off a corner) for it to be a roundabout, not a signal (FX5; Jerus, 2... |
| 268 | `TileRaster.TICKS_FROM` | `2` | Pixels a plot from which the blocks' view draws the track's cross-ticks (the L1 raster). |
| 271 | `TileRaster.DECK` | `0xff5d4c3c` | A bridge's deck, edging a road over fresh water from 4 px a plot: the mockup's. |
| 276 | `TileRaster.TUNNEL_PORTAL` | `0xff090b0d` | A tunnel's portal (ET4): the near black band across a highway or a railway where it goes into the hill or comes out, on the last plot of the tunnel at its edge, a little wider than the run (its cross-section and a pix... |
| 279 | `TileRaster.TUNNEL_DASH` | `0.6` | The share of a plot a tunnel's dashed line fills along the run, from the plot's start: 0.6 (the highway's centre line is .55), in the run's own colour - the highway's surface, the track's steel - a pixel wide at the n... |
| 282 | `TileRaster.TUNNEL_PORTAL_DEPTH` | `0.25` | A portal's depth along the run, a share of a plot: 0.25, a pixel at least - 1 px at 4 px a plot, 2 at 8. |
| 291 | `TileRaster.UNOWNED_DIM` | `0.4` | Ground no one in the city owns is drawn this much of the way to the map's background: 40%. |
| 294 | `TileRaster.VOID` | `0xff16222c` | The map's background, which unowned ground is dimmed toward: the mockup's --map-void. |
| 297 | `TileRaster.SITE_TINT` | `0.36` | A site's tint over its ground, its resource's colour this much of the way: 36% (the mockup's most for a deposit's body). |
| 300 | `TileRaster.SITE_SPECKLE` | `0.75` | ...and on the plots its own hash speckles, 75% (the mockup's ore showing through). |
| 303 | `TileRaster.SPECKLE_SHARE` | `0.15` | The share of a site's plots speckled: 15%. |
| 306 | `TileRaster.WORKED_GREY` | `0xff929496` | A worked-out site's grey: the mockup's mined grey. |
| 309 | `TileRaster.RING` | `0xff2b2d31` | A mine's ring and centre mark: the mockup's ring. |
| 312 | `TileRaster.INSET_FROM` | `3` | Pixels a plot from which a building of more than one plot is inset a pixel: 3 (a one-plot building takes its own land's side: SMALL_MOST). |
| 315 | `TileRaster.EDGE_FROM` | `6` | ...and from which it is edged: 6 (the mockup's outlines). |
| 318 | `TileRaster.ROAD_WIDTH` | `{ 0, 0.30, 0.52, 0.86 }` | A road's width as a share of its plot, by kind (gravel, paved, highway): the mockup's 0.3, 0.52 and 0.86 - since 0.7.88 the highway's and (as a paved road's) the track's; a street's surface is its width's (SURFACE). |
| 321 | `TileRaster.ROAD_LEAST_PX` | `{ 0, 1, 2, 3 }` | ...and its least, in pixels, at the near view: 1, 2 and 3 (the mockup's 1, 1.5 and 2.5, whole). |
| 324 | `TileRaster.HIGHWAY_CASE` | `0xff1d2126` | A highway's casing, a pixel either side from 6 px (the mockup's highwayCase)... |
| 327 | `TileRaster.CENTRE_LINE` | `0xfff0cf5a` | ...and its dashed centre line from 6 px a plot, a pixel wide, on the first 55% of each plot along it (the mockup's centre, its dash 0.7 on and 0.55 off). |
| 330 | `TileRaster.LINES_FROM` | `4` | Pixels a plot from which a road is drawn as a line of its own width on its ground - a street as verge and surface (0.7.88) - and below, the whole plot in its colour: 4, the near view's image. |
| 346 | `TileRaster.YARD_GROUND` | `0xff8c8478` | A yard's ground, light ballast (spec-freight 4.3's yard). |
| 348 | `TileRaster.YARD_TRACKS` | `8` | The fan's sidings (spec-freight 4.3: 6 to 10). |
| 350 | `TileRaster.DEPOT_TRACKS` | `3` | A Rail Depot's sidings (FR7 slice 3, "2 to 3 sidings and a shed"): 3 - its box is 8 plots by 7, a Rail Terminal's 24 by 24. |
| 368 | `TileRaster.YARD_TRACK_APART` | `1.5` | Plots between the fan's tracks, middle to middle. |
| 370 | `TileRaster.YARD_LADDER` | `3` | Plots at each end over which the fan closes to the line. |
| 372 | `TileRaster.SHED_DEPTH` | `2.5, SHED_LENGTH = 0.6` | The freight shed: plots deep, and its share of the fan's length. |
| 374 | `TileRaster.ORE_HEAP` | `0xffc9b68f` | A bulk yard's stockpiles, the spec's sand colour. |
| 376 | `TileRaster.CONTAINER` | `0xffd6dde4` | A container yard's boxes, the boats' white. |
| 378 | `TileRaster.GANTRY` | `0xff2b2f36` | Its two cranes. |
| 380 | `TileRaster.WAGON` | `0xff7b4b3a` | A wagon standing. |
| 382 | `TileRaster.WAGON_LENGTH` | `0.5` | Plots: a wagon about 15 m. |
| 385 | `TileRaster.SMALL_MOST` | `0.92` | A one-plot building's side as a share of its plot: its own land's (BuildingVisual.plotSide(), 0.7.64 - a House 0.91 of the plot, a Convenience Store 0.72; the mockup's 0.6 to 0.92 by a hash before), never more than 0.... |
| 388 | `TileRaster.SET_BACK` | `0.07` | ...and set this far from the road it faces: 0.07 of a plot, so it hugs its street (the mockup's). |
| 392 | `TileRaster.VERGE` | `0xffbfd39a` | A street's verge, its right of way either side of its surface (0.7.88): the prototype's VERGE (spec-roads-and-ports.md 5, roads_proto.py). |
| 395 | `TileRaster.TRACK_DASH` | `0xff8a6a3c` | A track's dashes, a street the city has bought no road for (0.7.88): the prototype's brown (spec 2.5, 5: "a dashed brown line on the verge"). |
| 398 | `TileRaster.SURFACE` | `{ 0, DistrictPlan.HALF, DistrictPlan.FULL }` | A street's surface across its plot, by its width (Painted.width: 0, half, full): none, half the plot (15 m), the whole plot (30 m) - the right of way is 30 m (spec 2.1; the prototype's W_MIN and W_FULL). |
| 401 | `TileRaster.PACKED_DIM` | `0.35` | A building packed at the city's edge (R7) is drawn this much of the way to the map's background: 35% (star RD2-2) - a shade darker, so it reads as standing apart from the streets, at its own size and place. |
| 663 | `TileRaster.YARD_CANVAS` | `ThreadLocal.withInitial(() -> new int [ 0 ])` |  |
| 907 | `TileRaster.HEAP_LEAST` | `2` | The least share of the ground past a bulk yard's fan, in pixels either way, a heap is drawn on: 2. |
| 941 | `TileRaster.LOOK_FROM` | `3` | Pixels a plot from which a materials building is drawn as its look rather than a box of its colour: 3 (TileRaster's INSET_FROM; the L0 image is 4). |
| 944 | `TileRaster.BENCHES` | `6` | The benches a pit or a quarry steps down by from its rim to its middle: 6. |
| 947 | `TileRaster.POND` | `0xff4f8fbf` | A leach plant's pond: #4f8fbf. |
| 950 | `TileRaster.HEADFRAME` | `0xff2b2622, WINDING = 0xff8a7f72` | A shaft's headframe: #2b2622, and the winding house in it #8a7f72. |
| 953 | `TileRaster.CONVEYOR` | `0xff9a9aa2` | The wash plant's conveyor: #9a9aa2. |
| 956 | `TileRaster.LOGS` | `0xff5a3e22` | A camp's log stacks: #5a3e22. |
| 959 | `TileRaster.CROWN` | `0xff6a9a55` | Planted forest's crowns, in rows over its darker floor: #6a9a55. |
| 962 | `TileRaster.RIDGE_DARK` | `0.18` | How much darker a works' roof ridges are than its fill: 18%. |
| 1045 | `TileRaster.QUAY_DECK` | `0xff3a4655` | A quay's deck: mockup 3's jetty, #3a4655... |
| 1048 | `TileRaster.QUAY_EDGE` | `0xff5a6676` | ...and its edge from EDGE_FROM px a plot: the mockup's #5a6676. |
| 1051 | `TileRaster.JACKET_FILL` | `0xff2a3540` | A platform's jacket: mockup 3's #2a3540... |
| 1054 | `TileRaster.OIL_TAN` | `0xffc9b68f` | ...edged in the mockup's tan, #c9b68f - its ring's and the pipe's colour too (the ore's). |
| 1057 | `TileRaster.RING_ALPHA` | `0.25` | A platform's 500 m ring over the water: the tan at the mockup's 25%... |
| 1060 | `TileRaster.RING_DASH_PX` | `3` | ...dashed 3 px on, 3 off along its arc (the mockup's "3 3"). |
| 1063 | `TileRaster.PIPE_ALPHA` | `0.5` | A crude pipeline: the tan at the mockup's 50%, dashed 2 px on and 4 off (its "2 4"), 1.5 px wide (drawn 1 below 4 px a plot, 2 from it). |
| 1065 | `TileRaster.PIPE_ON_PX` | `2, PIPE_OFF_PX = 4` | ...its dash, 2 px on and 4 off along the pipe (the mockup's "2 4"). |
| 1180 | `TileRaster.BLOCK_ALPHA` | `{ 0.4, 0.6, 0.8, 0.96 }` | A block's opacity over its ground by its share built: 40%, 60%, 80% and 96% (the mockup's ALPHA)... |
| 1183 | `TileRaster.BLOCK_FILLS` | `{ 0.12, 0.28, 0.5 }` | ...at a share built under 12%, under 28%, under 50%, and above (the mockup's). |
| 1186 | `TileRaster.BLOCK_MIDDLE` | `4` | Blocks the mockup drew at its middle view, in plots a side: 4 (120 m), from 1.4 to 3.2 px a plot... |
| 1189 | `TileRaster.BLOCK_FAR` | `8` | ...and at its far view: 8 (240 m), below 1.4 px a plot, with gravel hidden. |
| 1300 | `TileRaster.HIGHWAY_LEAST_PX` | `2` | The least a highway is drawn across, in pixels, in the blocks' views: 2 (the mockup's 1.8 at its far view, 2 at its middle). |
| 1626 | `TileRaster.NOTCH` | `1<<24` | What marks an outer plot in avenueNotches(): bit 24, over the arm's code (low two bits) and the codes of the two steps either side of it (bits 8 to 15 and 16 to 23). |
| 2481 | `TileRaster.RAMP_WIDTH` | `ROAD_WIDTH [ BuildingVisual.GRAVEL ]` | A ramp's slip road's width, as a share of a plot: a gravel street's, 0.3 (ROAD_WIDTH). |
| 2737 | `TileRaster.Crossings.N` | `TilePainter.CROSS_RING, R = TilePainter.CROSS_REACH` | The side of Input.ring and how far it reaches past the tile on every side, in plots: TilePainter.CROSS_RING and TilePainter.CROSS_REACH. |
| 2739 | `TileRaster.Crossings.NONE` | `new Crossings()` | A tile with no junction street about it: no crossings. |
| 3309 | `TileRaster.LOOP_WIDTH` | `0.34` | A loop's road across, as a share of a plot: 0.34, a pixel or two wider than a gravel street's, so it reads as a ramp at 8 px a plot and not as a street. |
| 3312 | `TileRaster.SLIP_OFF` | `0.45` | How far off a highway's middle a ramp runs where it meets the highway, in plots: just off the highway's band (0.45). |
| 3321 | `TileRaster.LEAF_RADIUS` | `2.4` | A leaf's radius, in plots (MF1, 2026-10-28; Jerus, shot 5: "Draw a cleaner interchange (a cloverleaf's or a diamond's shape)"): a cloverleaf's loop ramp is one leaf in its quadrant, about 270 degrees of a circle of th... |
| 3324 | `TileRaster.LEAF_AT` | `LEAF_RADIUS + SLIP_OFF` | Where a leaf's middle lies, in plots from the junction's middle along each of its two highways: its radius and the highway's edge, so it touches both. |
| 3331 | `TileRaster.CONNECTOR_RADIUS` | `5.2` | The outer connector's radius about the leaf's middle, in plots (MF1): in each quadrant of a cloverleaf a second, wider arc about the leaf, from one highway's edge round the outside of the leaf to the other's, the righ... |
| 3421 | `TileRaster.KERB_RADIUS` | `0.25` | A kerb corner's radius, in plots: 0.25 (7.5 m), the corner of a + of two streets in a homes cell - and of a street's T on an arterial there. |
| 3424 | `TileRaster.ESTATE_T_RADIUS` | `0.5` | An estate T's corner radius, in plots: 0.5 (15 m) - wide, where trucks turn (spec-freight 4.6: "a T with wide radius corners"). |
| 3638 | `TileRaster.SIDING_SHED` | `0.7` | A siding's loading shed, the share of a plot it fills (FR7 slice 3): 0.7, a small block at the siding's end beside the works. |
| 3990 | `TileRaster.HIGHWAY_LEG` | `2` | How far the straight legs of a highway bend are drawn past its tangent points, in plots: two - past the last plot the bend replaces (a plot and a half out), where the plots' own bands take over. |
| 4242 | `TileRaster.ROAD_SMALL` | `{ 0, 0xffbdb8ae, 0xff6a7077, 0xff3b4048 }` | A road plot's colour below LINES_FROM px a plot, where it fills its plot, by kind: gravel's and the highway's own, a paved road's casing - its own grey is the grass's brightness, and a pixel of it vanishes among the h... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 2742 | `final byte[] role` | The role of the street on each plot of the ring, by TilePainter.ringIndex(); null with no junction street about the tile. |
| 2744 | `final int[] group` | The crossing each plot belongs to, or -1; null with none. |
| 2746 | `byte[] av` | The avenue's mark on each plot of the ring, by ringIndex() (CL4); null with no avenue about the tile. |
| 2748 | `int groups` | The crossings: each one's rectangle (first and last column and row, in the tile's plots, which may lie off the tile), the roads that leave it, and whether it is a roundabout. |
| 2749 | `int[] x0` |  |
| 2750 | `boolean[] round` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 131 | 4132 | **type** `public final class TileRaster` | A painted tile as pixels: an int[] of 0xAARRGGBB, a row at a time, at a whole number of pixels a plot - what the map view hands PixelWriter.setPixels() for a tile's image. |
| 133 | 1 | `private TileRaster()` |  |
| 165 | 9 | `static { ... }` |  |
| 181 | 4 | `public static int colour(int cover, int relief)` | A plot's ground colour from its cover (a World class) and its relief (World.FLAT to VERY_STEEP): GROUND[cover] on flat ground, on water, on ice and for a generated world (which has no relief); on dry open ground (gras... |
| 191 | 1 | `public static int[] codeColours()` | Every cover-and-relief code's colour, a copy of the table colour() reads: code = relief x World.CLASSES + cover (World.RELIEFS x World.CLASSES = 32 entries, the FLAT row GROUND). |

### RAIL DRAWN AS RAIL (0.8.1, batch FR0; spec-freight 4.3, D15, D18) (lines 216-243)

### JUNCTIONS DRAWN BY RULE (0.8.1, batch FR0; spec-freight 4.6, D19) (lines 244-272)

### TUNNELS AND VIADUCTS (ET4, 2026-10-25; spec-earth2-terrain 4.3, D10) (lines 273-331)

| line | len | member | says |
|---:|---:|---|---|
| 285 | 4 | `static boolean tunnelDash(int i)` | Whether tunnel plot i is a dash where the view draws no lines (the blocks' views, below LINES_FROM): every other plot along the run, the plots whose x + y is even, the rest its bare ground. |

### YARDS DRAWN AS YARDS (0.8.1, batch FR0; spec-freight 4.3, D15) (lines 332-402)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 3 | `public static int tracksOf(BuildingVisual.Type t)` | The sidings of a yard of type t: a Rail Depot's DEPOT_TRACKS, else YARD_TRACKS (FR7 slice 3). |
| 363 | 4 | `public static int wagonTracksOn(int wagonTracks, int tracks)` | The sidings of a yard of `tracks` that wagons stand on, from the shed's side, when the railway's picture gives `wagonTracks` of a Terminal's YARD_TRACKS (CityMap.wagonTracksOf()): scaled to the yard's own sidings, and... |

### THE NEAR VIEW (J3: a tile at 4 or 8 px a plot, MapFrame.L0) (lines 403-927)

| line | len | member | says |
|---:|---:|---|---|
| 418 | 97 | `public static void raster(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | Fills img with the tile at px pixels a plot: (32 px) squared pixels, row by row. |
| 540 | 47 | `static int[] yardLayout(boolean[] rail, int w, int h)` | Where a yard's fan lies in its box, from the box's track (FX3, 0.8.4): rail[y * w + x] is whether the box's plot (x, y) holds track, the box w plots across and h down. |
| 615 | 14 | `private static void drawYard(TilePainter.Input in, TilePainter.Painted p, int b, BuildingVisual.Type t, int px, int[] img, int ...` | A Rail Terminal's yard as a yard, not its class's plain box (0.8.1, batch FR0; spec-freight 4.2 and 4.3, D15), and since FR7 (slice 3) a Rail Depot's: ballast ground, a fan of tracksOf() sidings (a Terminal's YARD_TRA... |
| 640 | 12 | `private static void drawWholeYard(TilePainter.Input in, TilePainter.Painted p, int e, BuildingVisual.Type t, int px, int[] img,...` | A yard the tile is given whole (FX5 Y2), drawn clipped to the tile: its picture - ground, fan, ladders, shed, heaps or boxes and cranes, wagons - is laid from the entry's whole box and layout (Input.yards, the very la... |
| 654 | 8 | `private static int[] yardCanvas(int size)` | The canvas a whole yard is drawn on: this thread's, at least `size` pixels (the raster runs on the map's workers and the screen's thread alike). |
| 671 | 10 | `static int yardOf(TilePainter.Input in, TilePainter.Painted p, int b)` | The entry of Input.yards that is the yard of building b (FX5 Y2): the building's part on the tile is the entry's whole box clipped to it, of the entry's type; -1 for none (a yard with no entry is a hand-built one, dra... |
| 690 | 116 | `private static void yardPicture(TilePainter.Input in, BuildingVisual.Type t, int px, int[] img, int w, int x0, int y0, int x1, ...` | A yard's picture into img (w pixels across) inside [x0, x1) x [y0, y1): the box at plots (bx, by), bw across and bh down, in img's plots, its layout (yardLayout()) laid for a box of boxW x boxH plots - the part of the... |
| 815 | 15 | `private static void headTrack(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int boxX, int boxY, int boxW, int...` | The track the fan's span leaves out, over the yard's ground (FX3): a line crossing the box runs through the head and meets the fan at its throat. |
| 837 | 10 | `private static void yardTrack(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img)` | One plot of the track that crosses a yard's head, drawn over the yard's ground (FX3, 0.8.4): drawTrackLook()'s look, with the track beside it read from the runs (p.run), because a building stands on every plot of the ... |
| 849 | 27 | `private static void containers(int px, int[] img, int w, int x0, int y0, int x1, int y1, boolean ew, int fanA, int fanB, int fa...` | A container yard's boxes, in rows, and its two gantry cranes across the fan. |
| 883 | 22 | `private static void stockpiles(int px, int[] img, int w, int x0, int y0, int x1, int y1, boolean ew, int fanA, int fanB, int fa...` | A bulk yard's three stockpiles, side by side along the fan. |
| 910 | 11 | `private static void fillEllipse(int[] img, int w, int cx, int cy, int rx, int ry, int fill, int edge, int clipX0, int clipY0, i...` | A filled ellipse, its edge a pixel darker, kept to [clipX0, clipX1) x [clipY0, clipY1). |
| 923 | 4 | `private static boolean outside(int x, int y, int cx, int cy, int rx, int ry)` | Whether (x, y) lies outside the ellipse centred at (cx, cy). |

### THE MATERIALS AS THEMSELVES (0.8.0, batch I14; spec-materials 5's I14 (lines 928-1039)

| line | len | member | says |
|---:|---:|---|---|
| 965 | 74 | `static void drawLook(TilePainter.Input in, BuildingVisual.Type t, int site, int px, boolean edged, int[] img, int w, int x0, in...` | One materials building's look over its box [x0, x1) x [y0, y1) in pixels: a site's measured from the site's whole square. |

### AT SEA (0.7.97, batch O13; TilePainter's AT SEA; mockup 3's colours) (lines 1040-1174)

| line | len | member | says |
|---:|---:|---|---|
| 1075 | 63 | `static void atSea(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The works at sea over the tile's picture: each quay plot the deck, edged from EDGE_FROM; a jacket the mockup's platform, edged; a well its tan dot; then every pixel of a plot a ring or a pipe crosses that lies on its ... |
| 1140 | 3 | `static boolean runHighway(TilePainter.Painted p, int i)` | Whether plot i of the runs is a highway's (its own, or the track bridging it), under a building or not (0.7.89). |
| 1149 | 16 | `private static void overBuildings(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A highway passing over a building (0.7.89): a mine standing on its site where the elevated highway crosses it - drawn over the building, its arms to the highway beside it (spec 2.7: elevated). |
| 1167 | 7 | `private static boolean runHighwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | Whether the plot a step from plot i, across the tile's edge too, is a highway's of the runs (under a building or not). |

### THE MIDDLE AND FAR VIEWS AS BLOCKS (J3b: the mockup's levels 1 and 2) (lines 1175-1558)

| line | len | member | says |
|---:|---:|---|---|
| 1200 | 98 | `public static void blocks(TilePainter.Input in, TilePainter.Painted p, int px, int block, boolean gravel, int[] img)` | A tile at px pixels a plot as the mockup drew its middle and far views: its ground, then blocks of `block` plots a side, each in its dominant kind's colour - by its buildings' plots, the homes' houses and flats togeth... |
| 1303 | 3 | `static boolean isHighway(TilePainter.Painted p, int i)` | Whether plot i is a highway's own (a run's, not a street of highway plots). |
| 1308 | 3 | `static boolean isStreet(TilePainter.Painted p, int i)` | Whether plot i is a street (a track among them), or a highway's plot with a street beneath. |
| 1313 | 4 | `static int roadKind(TilePainter.Painted p, int i)` | The kind of road on plot i - GRAVEL, PAVED, HIGHWAY or (0.7.88) TilePainter.TRACK - a road's plot, or where a street crosses the track - or 0. |
| 1319 | 8 | `static int faceOf(TilePainter.Painted p, int i)` | The side of plot i a street lies on, north, east, south, west, or -1 for none. |
| 1329 | 10 | `static int[] crossSection(int kind, int px)` | A road's line across its plot, by kind at px pixels a plot: {its whole width, its casing either side} - the highway's and (as a paved road's) the track's. |
| 1341 | 19 | `private static boolean streetAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | Whether the plot a step from plot i - across the tile's edge too, as the plans have it (Input.beyond) - is a way of the street at i: a street, or a highway's plot a street passes beneath. |
| 1362 | 7 | `private static boolean highwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | ...and a highway's own plot, a step from plot i. |
| 1371 | 12 | `private static boolean straightHighwayAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | ...and a straight highway's, a step from plot i: not a 45-degree stretch's plot, whose marks (across the tile's edge too: marksBeyond) say it is no highway beside a straight one for a stack's test. |
| 1385 | 7 | `private static boolean railAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | ...and the railway's track (since 0.7.89 where it bridges a highway too: the runs' RAIL_OVER). |
| 1400 | 8 | `private static boolean yardLineAt(TilePainter.Input in, TilePainter.Painted p, int i, int dir)` | Whether the plot a step from plot i, on this tile, is a plot of a yard's box that carries the runs' track (FX5 Y2): the line the yard stands on, which runs on under its ground into the fan. |
| 1410 | 13 | `private static int armsOf(TilePainter.Input in, TilePainter.Painted p, int i)` | The track beside plot i, north, east, south and west, one bit each (bit d for direction d): its neighbours' track, and a yard's line running on under the yard's ground. |
| 1425 | 3 | `static boolean diagonal(TilePainter.Input in, int i)` | Whether plot i is a 45-degree stretch's (the runs' M_DIAG): drawn as a band along its line. |
| 1436 | 6 | `static void diagBand(int[] img, int w, int i, int px, byte mark, double half, int c)` | A 45-degree stretch's band across plot i (spec 2.7: "a curve is drawn smooth; in plots it is a staircase"): the pixels within `half` pixels of the stretch's line - through the plot's middle, or for a staircase's corne... |
| 1450 | 12 | `private static void diagLine(int[] img, int w, int i, int px, int code, double pxl, double pyl, double half, double along, int c)` | The pixels of plot i within `half` pixels of a line at a 45-degree heading's code through the point (pxl, pyl) of the plot's pixels, and no farther along the line than `along` pixels either side of that point (FX3: di... |
| 1464 | 15 | `private static int stepMarkAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | The marks of the 45-degree stretch's step plot at (x, y) - in the tile, or just across one of its edges (marksBeyond) - or -1 where that plot is no step: a corner, a straight highway, other ground. |
| 1490 | 20 | `private static void diagOpen(TilePainter.Input in, TilePainter.Painted p, int i, int px, double half, int c, int[] img)` | Where a 45-degree stretch passes between two of its steps the line clips the corner of the plot that is not the run's - the staircase's corners are on the other side of it - and the band would leave a notch there (FX3... |
| 1517 | 3 | `private static boolean bendReplaces(TilePainter.Input in, TilePainter.Painted p, int i)` | Whether a highway bend draws plot i instead of its cell (FX5 slice D): the plot is one Input.curved names and no bridge's - a bridge's deck is straight and the arc is never drawn over water, so a curved plot over fres... |
| 1522 | 8 | `private static boolean highwayPlot(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether plot (x, y), in the tile or just across one of its edges on an axis, is a highway's own. |
| 1543 | 15 | `private static void diagEndOpen(TilePainter.Input in, TilePainter.Painted p, int i, int px, double half, int c, int[] img)` | An arm that ends on its diagonal (FX5 slice D) ends squared across its heading at its last step's far edge. |

### THE AVENUES (batch CL4; the study spec-city-layout 5.2) (lines 1559-1939)

| line | len | member | says |
|---:|---:|---|---|
| 1579 | 1 | `private static int armDx(int code)` | The heading of an avenue's arm, by CityRuns.diagCode(): east-south, west-south, west-north, east-north (as DistrictPlan.AVENUE_DX and AVENUE_DY): +1 or -1 along x... |
| 1582 | 1 | `private static int armDy(int code)` | ...and along y. |
| 1585 | 6 | `private static int codeAt(TilePainter.Input in, int x, int y)` | The street code on plot (x, y) of the tile or within CROSS_REACH of it (Input.street, Input.ring): 0 for none or a plot farther out. |
| 1593 | 5 | `private static boolean ringStreet(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether plot (x, y), in the tile or within REACH of it, is a street (as streetAt() reads it: a track, and a highway's plot a street passes beneath, among them). |
| 1605 | 6 | `private static boolean axisArm(TilePainter.Input in, TilePainter.Painted p, int x, int y, int d)` | Whether avenue plot (x, y) of the tile has an AXIS ARM in direction d (north, east, south, west): a street running into it that is none of the avenue's - the plot beside it a street plot and no avenue's, and the plot ... |
| 1613 | 4 | `private static int stepAt(TilePainter.Input in, int x, int y)` | The mark of the avenue's step (a plot of the line, no corner) at (x, y) of the tile or its ring, or -1. |
| 1619 | 5 | `private static boolean bridgeAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether plot (x, y) of the tile or its ring bridges fresh water: its street's bridge, or the deck the painter laid. |
| 1634 | 24 | `private static int[] avenueNotches(TilePainter.Input in, TilePainter.Painted p)` | The OUTER PLOTS of the tile's avenues (O_k, the fourth plot of the 2 x 2 block S_(k-1), C_k, S_k, O_k): by plot, 0 for none, else NOTCH, the arm's code, and the street codes of the two steps either side of it, S_(k-1)... |
| 1660 | 3 | `private static boolean notchVerge(TilePainter.Input in, TilePainter.Painted p, int i)` | Whether outer plot i (avenueNotches() says it is) is open ground that takes a verge: nothing on it, dry. |
| 1672 | 27 | `private static void avLine(TilePainter.Input in, int[] img, int w, int i, int px, int code, double pxl, double pyl, double half...` | The pixels of plot i within `half` pixels of the line at the heading `code` through the point (pxl, pyl) of the plot's pixels, no farther along the heading than sMax pixels from that point and no farther behind it tha... |
| 1701 | 3 | `private static int surfaceOf(int width, int px)` | A street's surface across its plot, in pixels at px a plot, as a straight street's bar is (SURFACE by its width code, 1 or 2). |
| 1713 | 33 | `private static void avenuePlot(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, boolean box)` | Avenue plot i drawn (from LINES_FROM px a plot): the bar across it for each street that crosses the avenue through it (axisArm()), in that street's own kind and width - so the street reads as one line across the band ... |
| 1748 | 4 | `private static int avenueArm(TilePainter.Input in, TilePainter.Painted p, int x, int y, int d0, int d1)` | The street code of the first of the two arms (directions d0 and d1, the axis's two ways) of avenue plot (x, y) that is an axis arm: the code of the plot beside it; 0 for none. |
| 1758 | 23 | `private static void axisBar(TilePainter.Input in, int i, int px, int[] img, boolean horizontal, int arm, boolean box)` | The bar of a street that crosses an avenue, across avenue plot i along one axis (horizontal or not), edge to edge, in the kind and width of its code `arm` - gravel's grain, a track's dashes, a highway's none - or, box... |
| 1783 | 6 | `private static int notchKind(int codeA, int codeB)` | The kind, in BuildingVisual's numbering or TilePainter.TRACK, an outer plot between two steps is drawn as: the lesser of theirs, a track if either is one; 0 where a step has none (beneath a highway). |
| 1796 | 32 | `private static void avenueNotch(TilePainter.Input in, TilePainter.Painted p, int i, int info, int px, int[] img, boolean deck, ...` | Outer plot i (avenueNotches() entry `info`) drawn: the band of its arm's line through the middle of the step beside it across its pixels - the notch the staircase's plots would leave in the band - in the kind and widt... |
| 1837 | 33 | `private static void avenueEnds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A piece of avenue's end: where a step's successor corner is not laid (its head: the avenue ends there, at a yard, at water too wide, at the edge of the city's ground) or its predecessor's is not (its tail), the band e... |
| 1879 | 32 | `private static void avenueOrigin(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, Crossings cr)` | The origin's neighbourhood (the 3 x 3 plots about Input.avenueOx/avenueOy, the lattice node the four arms run out from): a plot two arms share carries the first arm's mark only (DistrictPlan.avenue), so the marks draw... |
| 1913 | 11 | `private static void avenueDeck(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img)` | Avenue plot i over fresh water (the decks' pass of ways()): its deck - the band a pixel wider either side - and a bar of deck for each street that crosses it; the surface comes with avenuePlot(). |
| 1926 | 13 | `private static void avenueBoxes(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, Crossings cr)` | The avenues' part of every signalled crossing's one box (CL4): the avenue plots, the outer plots and the origin's rays that lie in a junction of a signal, redrawn in JUNCTION_BOX. |

### THE CURVES (batch CL4, slice D; the study spec-city-layout 5.2) (lines 1940-2214)

| line | len | member | says |
|---:|---:|---|---|
| 1967 | 11 | `private static boolean curveDrawn(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether plot (x, y), of the tile or within REACH of it, is a street plot a curve draws in place of its bars (see THE CURVES). |
| 1984 | 6 | `private static boolean curveArm(TilePainter.Input in, TilePainter.Painted p, int x, int y, int d)` | Whether curve-drawn plot (x, y) has an ARM in direction d (north, east, south, west): a street running into it that is none of the curve's - the plot beside it a street plot and no curve's own, and the plot beyond tha... |
| 1997 | 9 | `private static boolean curveLink(TilePainter.Input in, TilePainter.Painted p, int ax, int ay, int bx, int by, int d)` | Whether plot A (ax, ay) of the tile, beside the street plot B (bx, by) in direction d, has a bar toward it, where a curve is about (streetAt()): two plots the curve draws have none between them; a plot the curve draws... |
| 2008 | 6 | `private static void curveArms(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img)` | The bars of curve-drawn plot i: the street that crosses it along each axis, across the plot in that street's kind and width (axisBar()). |
| 2016 | 4 | `private static int curveArmCode(TilePainter.Input in, TilePainter.Painted p, int x, int y, int d0, int d1)` | The street code of the first of the two arms (directions d0 and d1) of curve-drawn plot (x, y); 0 for none. |
| 2028 | 30 | `private static boolean curveEndBars(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int dashT, int dashS...` | The bars of an end of a curve (CV_END), where the street runs into it from one side alone on an axis - the dead end the street along a cut leaves from, whose street stops there - so the bar is drawn from that side's e... |
| 2060 | 5 | `private static boolean bandable(TilePainter.Painted p, int i)` | Whether the band may be drawn on plot i of the tile: a street's plot (no bridge's, none beneath a highway, no highway's own) or open ground, the water the line cuts across a corner of among it - never a building, the ... |
| 2067 | 11 | `private static int curveLookOf(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | What the band is drawn as on own plot (x, y) of the tile or its ring: (kind << 4 \| width) + 1 of its street, 0 where it is none the band is drawn as (a highway's, one beneath a highway, a bridge's). |
| 2080 | 23 | `private static int[] curveLooks(TilePainter.Input in, TilePainter.Painted p)` | What the band is drawn as on the plots of the tile: per plot, 0 for none or (kind << 4 \| width) + 1 - an own street plot's own kind and width, a band plot's those of the nearest own street plot beside it (the four sid... |
| 2109 | 19 | `static double[][] curveLine(int kind, double[] g, int at, int n)` | A curve's line, in the plots of the tile, from the numbers Input.curveGeom holds (n of them from `at`): {xs, ys}. |
| 2130 | 11 | `private static void curves(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | Every curve of the tile drawn (THE CURVES): the band along each line over the plots it is drawn on. |
| 2147 | 67 | `private static void drawCurve(TilePainter.Input in, int px, int[] img, int[] look, double[] xs, double[] ys)` | One curve's band: for each plot the curve is drawn on (look), the pixels of it whose centres lie within half the street's width of the line (xs, ys, in plots) - a track's width, the dashes counted along the line from ... |

### THE WAYS AS LINES (0.7.88, batch RD2; from LINES_FROM px a plot) (lines 2215-2562)

| line | len | member | says |
|---:|---:|---|---|
| 2266 | 213 | `private static void ways(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The ways as lines (from LINES_FROM px a plot; spec 5): each street's verge, its whole plot on dry ground; the decks of bridges; the track (as rail since 0.8.1); each street's surface down its middle along each way it ... |
| 2493 | 37 | `private static void ramps(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The ramps (0.7.89; spec 2.7: "ramps where an arterial crosses, every other one, and at an arm's end"): at a highway's plot the runs mark a ramp, where a street passes beneath it, a slip road on each verge plot at its ... |
| 2532 | 8 | `private static boolean isRunHighway(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether plot (x, y) - in the tile, or just across one of its edges - is a highway's of the runs. |
| 2542 | 7 | `private static boolean isStreetAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether plot (x, y) - in the tile, or just across one of its edges - is a street's. |
| 2551 | 11 | `private static void segment(int[] img, int w, int x0, int y0, int px, double ax, double ay, double bx, double by, double half, ...` | The pixels of a plot (its corner at x0, y0, px a side) within `half` of the segment from (ax, ay) to (bx, by), in the plot's pixels: colour c. |

### a wide road is one carriageway (MF1, slice C) (lines 2563-2669)

| line | len | member | says |
|---:|---:|---|---|
| 2578 | 48 | `private static void bands(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The surface of every 2 x 2 block of street plots between the bars through their middles, so a road several plots across is one carriageway (MF1, 2026-10-28; Jerus, on his city: "continous intersection, like its a 3x14... |
| 2628 | 4 | `private static boolean block(TilePainter.Input in, TilePainter.Painted p, int x, int y, int[] at)` | Whether the four plots from (x, y), across and down, are street of one kind as bandAt() counts it - which it leaves in `at`, one a plot. |
| 2634 | 4 | `private static boolean sameKind(int[] at)` | Whether the four bandAt() values in `at` are all street, and of one kind. |
| 2640 | 10 | `private static int[] mesh(int[] at, int px, int x, int y)` | The pixels {x0, y0, x1, y1} between the bars of the four street plots from plot (x, y), from their bandAt() values: bar edge to bar edge. |
| 2652 | 17 | `private static int bandAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | What plot (x, y) of the tile or its ring is to bands(): 0 for no street of its kind, else its kind (GRAVEL or PAVED) and, in bits 4 and 5, its width (1 half, 2 full). |

### junctions' helpers (lines 2670-2996)

| line | len | member | says |
|---:|---:|---|---|
| 2680 | 13 | `private static byte streetRoleAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | The role of the street at plot (x, y) - ARTERIAL, BOULEVARD, ACCESS or STREET: in the tile, from its painted plots; the plot just across one of its edges, from Input.beyond; and (FX5) any other plot within TilePainter... |
| 2700 | 5 | `private static byte crossingRoleAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | The role of the street at plot (x, y) as a crossing reads it (MF1, 2026-10-28): from the plan's code on every plot, the tile's own among them, not from the painted plot, which a building or a highway laid over a stree... |
| 2707 | 5 | `private static byte roleOfCode(int code)` | The role of a street code (the S_ codes) as streetRoleAt() answers it: 0 for none or a street beneath a highway. |
| 2714 | 3 | `private static boolean junctionRole(byte role)` | Whether a street role is one a junction is drawn for. |
| 2735 | 243 | **type** `private static final class Crossings` | The crossings of a tile's streets (FX5; Jerus, 2026-10-20, "Mostly signals"), read once for the tile over the plots about it - the tile and TilePainter.CROSS_REACH more on every side, which Input.ring gives - so a cro... |
| 2752 | 4 | `private Crossings()` _(in TileRaster.Crossings)_ |  |
| 2757 | 5 | `private Crossings(byte[] role)` _(in TileRaster.Crossings)_ |  |
| 2764 | 66 | `static Crossings of(TilePainter.Input in, TilePainter.Painted p)` _(in TileRaster.Crossings)_ | The crossings of the tile in/p draw. |
| 2837 | 21 | `private static void avenueJunctions(TilePainter.Input in, byte[] role, byte[] avm, boolean[] jp)` _(in TileRaster.Crossings)_ | The junction plots the avenues make (CL4), into jp: an avenue's plot of a junction role (they are arterials) that a street of a junction role crosses - one of its sides a plot of such a street that is no avenue's with... |
| 2860 | 4 | `int avAt(int x, int y)` _(in TileRaster.Crossings)_ | The avenue's mark on plot (x, y) of the ring; 0 for none or a plot off it. |
| 2871 | 6 | `private boolean streetOut(int x, int y, int d)` _(in TileRaster.Crossings)_ | Whether plot (x, y), just outside a crossing's rectangle, is a road leaving it (CL4): a street plot that is no avenue's, or an avenue's plot that is also a street the avenue crosses - one that goes on out, in directio... |
| 2884 | 15 | `private int branches(int g)` _(in TileRaster.Crossings)_ | The avenues' branches that leave crossing g (CL4): of the avenue's plots in its rectangle each plot the staircase runs on to (a step's are the corner before it, at (x, y - dy), and after it, at (x + dx, y); a corner's... |
| 2901 | 3 | `private static boolean runRole(byte[] role, byte[] avm, int k)` _(in TileRaster.Crossings)_ | Whether plot k of the ring is a plot of a run of junction streets: a junction role, and no avenue's plot (avm null with no avenue about the tile). |
| 2906 | 10 | `private int open(int x, int y)` _(in TileRaster.Crossings)_ | Starts a crossing at plot (x, y); its number. |
| 2918 | 4 | `private void grow(int g, int x, int y)` _(in TileRaster.Crossings)_ | Adds plot (x, y) to crossing g's rectangle. |
| 2924 | 4 | `byte roleAt(int x, int y)` _(in TileRaster.Crossings)_ | The role of the street at plot (x, y) of the ring; 0 for none or for a plot off it. |
| 2930 | 4 | `int at(int x, int y)` _(in TileRaster.Crossings)_ | The crossing plot (x, y) is a junction plot of, or -1. |
| 2936 | 4 | `boolean signalAt(int x, int y)` _(in TileRaster.Crossings)_ | Whether plot (x, y) is a junction plot of a signal. |
| 2942 | 3 | `boolean inRect(int g, int x, int y)` _(in TileRaster.Crossings)_ | Whether plot (x, y) lies in crossing g's rectangle. |
| 2954 | 23 | `private int roadsLeaving(int g)` _(in TileRaster.Crossings)_ | The roads that leave crossing g: on each of the rectangle's four sides each maximal run of street plots just outside it is one road, and a street plot diagonally off a corner is one more where the street goes on out a... |
| 2984 | 6 | `static int[][] crossings(TilePainter.Input in, TilePainter.Painted p)` | The crossings the painter reads for a tile, each {first column, first row, last column, last row, roads that leave it, 1 for a roundabout and 0 for a signal}, in the tile's plots (a rectangle may lie off the tile): wh... |
| 2992 | 4 | `private static int[] surfaceAt(TilePainter.Painted p, int i, int px)` | A street's surface across its plot at px pixels a plot: {its width, its offset from the plot's edge}. |

### 1. signals: where arterials, boulevards and access roads cross (FX5: one box a crossing) (lines 2997-3071)

| line | len | member | says |
|---:|---:|---|---|
| 2999 | 45 | `private static void signals(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, Crossings cr)` |  |
| 3046 | 25 | `private static void drawSignalArm(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int dir)` | A zebra crossing and its stop line on one arm of a signal, at the edge facing the junction. |

### 2. roundabouts: where ROUNDABOUT_ROADS or more roads meet (FX5; before, wherever boulevards crossed) (lines 3072-3115)

| line | len | member | says |
|---:|---:|---|---|
| 3074 | 18 | `private static void roundabouts(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, Crossings cr)` |  |
| 3094 | 5 | `private static boolean roundaboutTarget(TilePainter.Input in, TilePainter.Painted p, int i)` | Whether a roundabout's ring or island may be drawn on plot i: a street or empty ground, never a building, the track, a highway or water. |
| 3101 | 14 | `private static void drawRoundaboutOn(TilePainter.Input in, int i, int px, int[] img, double centreX, double centreY, double out...` | The ring's and island's pixels of a roundabout centred on (centreX, centreY), in the tile's pixels, with an outer and an island radius in pixels, that fall on plot i. |

### 3. diamonds: a street passes beneath a highway (lines 3116-3170)

| line | len | member | says |
|---:|---:|---|---|
| 3118 | 29 | `private static void diamonds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` |  |
| 3150 | 20 | `private static void drawSlip(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int hx, int hy, boolean rowHw, int...` | One diamond slip along the verge beside a highway, from the street out for SLIP_LENGTH plots. |

### 4. a highway arm's end (lines 3171-3252)

| line | len | member | says |
|---:|---:|---|---|
| 3181 | 59 | `private static void highwayEnds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A highway arm's end (spec 2.7's ramp at an arm's end): a highway plot the runs mark as a ramp with no street beneath, where the highway runs on exactly one of its four sides - the band narrows to a paved road's and a ... |
| 3242 | 10 | `private static void drawHighwayApron(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int tx, int ty, boolean ve...` | A paved apron the paved road's width across the plot just past a highway's open end, when that plot is empty ground. |

### 5. a stack: highways cross (lines 3253-3302)

| line | len | member | says |
|---:|---:|---|---|
| 3263 | 26 | `private static void stack(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A stack where two straight highways cross: a highway plot with another beside it on both axes. |
| 3291 | 11 | `private static void drawStackSlip(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int cx, int cy, int dx, int dy)` | One corner slip of a stack, across the plot diagonal to the crossing. |

### 6. interchanges' loops: a cloverleaf's and a trumpet's reserve (batch FR7; spec-freight 4.6, D19; leaves since MF1) (lines 3303-3417)

| line | len | member | says |
|---:|---:|---|---|
| 3339 | 41 | `private static void loops(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The loops of every reserve that reaches the tile, from JUNCTIONS_FROM px a plot: a cloverleaf's four leaves, each with its connector round the outside; a trumpet's one leaf, in the quadrant between its stub and the hi... |
| 3386 | 22 | `private static double rampDistance(double qa, double qb, boolean connector)` | The distance from a point of a quadrant to the quadrant's ramps, the point given from the leaf's middle along the quadrant's two axes (qa, qb): to the leaf, the circle of LEAF_RADIUS but for the quarter facing the cro... |
| 3410 | 7 | `private static boolean reserveJunction(TilePainter.Input in, int x, int y)` | Whether plot (x, y) of the tile is the junction of a reserve that reaches it: where loops replace a stack's corner slips. |

### 7. kerb corners and estate T's (batch FR7; spec-freight 4.6's table) (lines 3418-3539)

| line | len | member | says |
|---:|---:|---|---|
| 3439 | 56 | `private static void kerbs(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | Kerb corners and estate T's, from JUNCTIONS_FROM px a plot, over the streets' surfaces and their verges: at a + of two streets (a cell's streets, role STREET, on all four sides) each of its four inner corners is round... |
| 3497 | 14 | `private static boolean estateAt(TilePainter.Input in, TilePainter.Painted p, int x, int y)` | Whether a building of an estate's classes - industry, farms, utilities, mines and wells, transport; a homes cell holds the rest - stands within two plots of plot (x, y): the painter's reading of an estate. |
| 3520 | 19 | `private static void fillet(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int jx, int jy, int cx, int cy, int ...` | One rounded corner: the corner point (cx, cy), in pixels from plot (jx, jy)'s corner, where two bars meet; (sx, sy) the way out of it, toward the verge. |

### the track as rail, and its crossings (0.8.1, batch FR0; spec-freight 4.3) (lines 3540-3827)

| line | len | member | says |
|---:|---:|---|---|
| 3553 | 6 | `private static void track(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The track (0.7.72; redrawn as rail, 0.8.1, batch FR0, spec-freight 4.3): each plot's middle and its arms to the track beside it - past the tile's edge too, where the line runs on - as a ballast bed, and from 4 px a pl... |
| 3571 | 19 | `private static void tunnels(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The tunnels (ET4, 2026-10-25; spec-earth2-terrain 4.3: "a run on very steep ground is drawn as a tunnel (portals, a dashed line)"): each plot of a run that lies on very steep dry ground (Painted.tunnel) is its ground,... |
| 3592 | 44 | `private static void tunnelWay(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int arms, int colour, int ...` | One run's way through tunnel plot i, `arms` its neighbours (bit d for direction d) and `cross` its cross-section in pixels: the dashed line and the portals of tunnels(). |
| 3645 | 20 | `private static void sidingSheds(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | A siding's loading shed (FR7 slice 3): on the plot marked M_SHED, the last of a siding beside its works' box, a small block in TRANSPORT's colours over the track's end, edged from EDGE_FROM. |
| 3667 | 3 | `private static int bedWidth(int px, boolean doubled)` | The ballast bed's width, whole pixels: BED, or BED_DOUBLE where the plot is marked double track. |
| 3672 | 4 | `private static void drawTrackLook(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img)` | The track's look on one plot that carries rail: its ballast bed, sleepers and rails (a 45-degree stretch's just a bed and one rail). |
| 3678 | 23 | `private static void drawTrackLook(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int arms)` | ...with the track beside it given, bit d for direction d (a yard's head reads it from the runs: yardTrack()). |
| 3703 | 10 | `private static void drawBed(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int arms)` | The track's ballast bed: the plot's middle and its arms to the track beside it, as the bed's whole width. |
| 3715 | 22 | `private static void drawSleepers(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boolean n...` | The sleepers, a pixel thick across the bed, every `step` pixels along the way the track runs. |
| 3739 | 8 | `private static void drawSleeperTicks(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boole...` | Below RAILS_FROM px a plot, one sleeper tick across the bed at the start of each plot along the track. |
| 3749 | 5 | `private static void drawRails(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boolean ns, ...` | The rails, a pixel each, along the way the track runs. |
| 3756 | 29 | `private static void drawRailsFull(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boolean ...` | From RAILS_FROM px a plot: two rails a track, double track's two tracks at the plot's middle +/- TRACKS_APART. |
| 3787 | 22 | `private static void drawRailsLow(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, boolean n...` | Below RAILS_FROM px a plot: one STEEL line down the track's middle, two at double track's tracks. |
| 3811 | 7 | `private static void railH(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int row, int c, ...` | A rail a pixel wide along an east-west track at row `row`, its middle and its arms. |
| 3820 | 7 | `private static void railV(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int a, int z, int col, int c, ...` | ...and a north-south track's, at column `col`. |

### the curves (FX5) (lines 3828-4194)

| line | len | member | says |
|---:|---:|---|---|
| 3848 | 8 | `private static void bends(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int cx0, int cy0, int cx1, int cy1, b...` | The railway's bends (Input.bends, TrackCurves), drawn on the pixels of the tile within the box [cx0, cx1) x [cy0, cy1): for each, its arc between its tangent points and its two legs' straights from each tangent point ... |
| 3858 | 3 | `private static TrackCurves.Bend inPixels(TrackCurves.Bend b, int px)` | The bend scaled to pixels a plot: the same arc, its corner, radius and tangent length times px. |
| 3870 | 18 | `private static double pathLateral(TrackCurves.Bend s, double back1, double back2, double x, double y, double[] out)` | How far point (x, y) lies from the path of bend s (in pixels): its arc, the straight of `back1` before its start along the incoming leg, and the straight of `back2` after its end along the outgoing one - the least of ... |
| 3889 | 69 | `private static void drawBend(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, TrackCurves.Bend b, boolean double...` |  |
| 3964 | 24 | `private static void blockBends(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The bends in the blocks' views (FX5): each bend's arc and straights in RAIL_DARK at the width the blocks draw track (a plot, px pixels), in place of the cells of the plots it replaces. |
| 4006 | 24 | `private static void highwayBends(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, double half, int c)` | The highways' bends (Input.bends of net HIGHWAY, FX5 slice D), drawn as the carriageway along the arc: the pixels within `half` pixels of each bend's path in colour c, one pass of the highways' casing or surface (ways... |
| 4039 | 26 | `private static void highwayBendDashes(TilePainter.Input in, TilePainter.Painted p, int px, int[] img, int dash)` | The highways' bends' centre line (FX5 slice D): a dash over the first 55% of each plot's length of the path from the arc's start (`dash` pixels of every px), a pixel wide on the row or column the straight's own dash t... |
| 4075 | 18 | `private static void ladderRails(int[] img, int w, int px, boolean ew, double apexAlong, double lineAcross, double sidingAlong, ...` | One of a yard's ladders (FX5), from the fan's apex on the line to its siding's start, as TrackCurves.ladder() lays it - an S tangent to the line and to the siding, the points the trains' routes run along - drawn as th... |
| 4101 | 51 | `private static void railCrossings(TilePainter.Input in, TilePainter.Painted p, int px, int[] img)` | The railway's crossings (0.8.1, spec-freight 4.3, D18): the street's surface already lies over the bed; from RAILS_FROM px a plot, draw the rails through a level crossing with its crossbucks and half barriers, and an ... |
| 4154 | 5 | `private static void drawCrossbucks(int px, int[] img, int w, int x0, int y0)` | A level crossing's two crossbucks: a white X in the plot's north-west and south-east corners. |
| 4161 | 6 | `private static void drawX(int[] img, int w, int x0, int y0, int s)` | An X of side `s` in CROSSBUCK, its corner at (x0, y0). |
| 4169 | 25 | `private static void drawBarriers(TilePainter.Input in, TilePainter.Painted p, int i, int px, int[] img, int w, int x0, int y0, ...` | A level crossing's half barriers: a bar across the lane coming in at each edge where the street enters the plot. |

### A RECTANGLE OF PIXELS, AND THE PLOTS' COLOURS (lines 4195-4262)

| line | len | member | says |
|---:|---:|---|---|
| 4200 | 10 | `private static void grain(TilePainter.Input in, int[] img, int w, int x0, int y0, int x1, int y1, int c)` | Speckles GRAVEL_GRAIN_SHARE of the box's pixels with c, each by a hash of its pixel in the tile and the tile's place, so the same street speckles the same at every paint. |
| 4211 | 6 | `private static void fillRect(int[] img, int w, int x0, int y0, int x1, int y1, int c)` |  |
| 4219 | 10 | `static int groundColour(TilePainter.Input in, TilePainter.Painted p, int i)` | A plot's ground: its terrain - forest under a building cleared (0.7.88; spec 2.6) - its site's tint or grey, dimmed when unowned: what a road is drawn on from LINES_FROM px. |
| 4231 | 9 | `static int siteTint(TilePainter.Input in, int i, int s, int c)` | A site's plot over its ground: its resource's colour, speckled, half grey while its holding is worked, grey worked out. |
| 4245 | 9 | `static int plotColour(TilePainter.Input in, TilePainter.Painted p, int i)` | A plot's colour before its building: its ground, its site's tint or grey, its road's colour - a track's brown (0.7.88) - or (0.7.72) its track's, where no street crosses it - and dimmed when unowned. |
| 4256 | 6 | `static int blend(int a, int b, double f)` | a toward b by share f, channel by channel, opaque. |

