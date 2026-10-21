# MapCheck.java - 6,122 lines · 129 methods · 62 constants · harnesses

`ham/citybuildersim/MapCheck.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

> The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter draws each district's street plan and paints the same pixels from the same inputs, the drawn raster itself is one street network with + junctions apart and every building within reach, the sidecar comes back byte for byte, and a screen costs what the screen holds, never what the city does - at Jerus's size and at five and ten billion people.
> 
> WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.5, 2.6 and
> 3). The map is the one part of the city nothing in the model reads, so
> nothing else would notice it drifting: districts that stopped adding up to
> the buildings, a building that jumped across the city when another was
> built, a tile that painted differently twice, a sidecar from another save
> read as this one's, or a screen that slowed as the city grew - the failure
> the design was drawn to avoid; and (J3b) a city of half a million drawn as
> scattered dots on a field, one dot for each apartment block, which is what
> J3's first render of Jerus's city showed. Headless: no JavaFX (the view is
> J4's).
> 
> What it has to prove:
>   1. a city's map adds up: every month of a 120-month city played as the
>      playtest plays it (land bought, built, demolished), its districts sum
>      to the model's count of every type and its pyramid's top to its
>      districts; a district recounted after a purchase holds what a map
>      drawn afresh measures - a whole iron field's sites among it, wherever
>      they lie (0.7.64); its mines stand on owned iron sites; a demolition
>      comes off the outermost district holding the type; and at every
>      DRAWN_EVERY-th month, every tile painted, the map draws exactly what
>      the city has - each type's buildings as many as the model's (since
>      0.7.88 those its plans could not hold packed at the city's edge), a
>      mine once on its site, its streets' surface kind by kind with what the
>      plans have no street for the model's road plots, every plot of its
>      track; and (0.8.3, batch FX2) in every one of those months no line to
>      the world starts on a plot the city owns or on water, none runs out
>      over ground that is not dry (at least TrainSchedule.WORLD_PLOTS, at
>      most CityMap.WORLD_REACH, ending where the land does), and no train
>      position of any route is on water off the track - the months that had
>      a line, and trains, are printed;
>   2. nothing placed moves: one more building changes one district's count
>      by one, inner types in the first district with room, outer types in
>      the last; and (0.7.88, spec-roads-and-ports.md 8.4; one tile's count
>      by one while the deal stood) in that district's plan it moves only the
>      buildings placed after it - where the plan's ladder takes the same
>      step and its first band is dealt round as many cells - and one fewer
>      likewise;
>   3. the painter draws the plan (0.7.88; the mockup's rules on the deal's
>      tiles before): on the dense screen every plot a district's plan
>      surfaces is painted as that street - kind and width - its seams once;
>      no bridge crosses more fresh water than its line may (a street
>      STREET_BRIDGE, an arterial ARTERIAL_BRIDGE) and no road is on the sea;
>      every mine on its own site; the same inputs paint the same pixels,
>      and the far view's blocks, from a twin map drawn the same way;
>   4. the sidecar round-trips byte for byte through a save and a load, the
>      next month plays to the same map, a stale or missing sidecar draws the
>      map again canonically, a city with no map saves none, and (0.7.88) a
>      FORMAT 4 sidecar still loads into the same map - and (0.7.89) it and
>      0.7.88's FORMAT 5, which wrote no runs, with their runs laid as a map
>      drawn afresh lays them; and at five and ten billion it is written and
>      read back the same;
>   5. the cost follows the screen: a 1,389 x 868 L0 screen (SCREEN_TILES)
>      paints and rasters from its districts' plans in no more than SCREEN_MS
>      at Jerus's city x 1, x 9,814 (5B), x 10,000 and x 19,629 (10B), each
>      within SCREEN_RATIO of the 5B copy's, the first whose screen is all
>      city; the plans themselves are made away from the screen (PlanCheck
>      holds their PLAN_MS); and a month's change at 5B and 10B in no more
>      than RECONCILE_MS (the design's 5 ms; measured 0.76) of the main
> ... (214 more lines in the source)

**Uses:** [CityMap](CityMap.md) (336), [World](World.md) (251), [MapFrame](MapFrame.md) (139), [BuildingVisual](BuildingVisual.md) (135), [TilePainter](TilePainter.md) (103), [LandMap](LandMap.md) (99), [GlobeFrame](GlobeFrame.md) (94), [MapTiles](MapTiles.md) (81), [TrainSchedule](TrainSchedule.md) (78), [MiniMapFrame](MiniMapFrame.md) (66), [Game](Game.md) (56), [DistrictPicture](DistrictPicture.md) (56), [DistrictPlan](DistrictPlan.md) (47), [CityRuns](CityRuns.md) (46), [TileRaster](TileRaster.md) (42), [LongPlaytest](LongPlaytest.md) (37), [LandParcel](LandParcel.md) (34), [Ports](Ports.md) (29), [EarthTerrain](EarthTerrain.md) (29), [BoatSchedule](BoatSchedule.md) (25), [Resource](Resource.md) (22), [CityLand](CityLand.md) (22), [EarthData](EarthData.md) (22), [CityShore](CityShore.md) (20), [EarthTerrainCheck](EarthTerrainCheck.md) (20), [SeaRoutes](SeaRoutes.md) (17), [Timing](Timing.md) (16), [GlobePicture](GlobePicture.md) (14), [GameFiles](GameFiles.md) (13), [Sectors](Sectors.md) (13)... and 22 more

**Used by (7):** [EarthTerrainCheck](EarthTerrainCheck.md), [GatewayCheck](GatewayCheck.md), [GlobeCheck](GlobeCheck.md), [MainLineCheck](MainLineCheck.md), [PlanCheck](PlanCheck.md), [RailDrawCheck](RailDrawCheck.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 415 | 1. A CITY'S MAP ADDS UP |
| 597 | · WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64) |
| 731 | 2. NOTHING PLACED MOVES |
| 810 | THE COPIES: JERUS'S CITY ON A SQUARE OF DISTRICTS, AT HIS DENSITY |
| 929 | 3. THE PAINTER DRAWS THE PLAN (0.7.88, batch RD2; the mockup's rules on |
| 993 | 6. THE CITY DRAWN AS WHAT IT HAS (0.7.64, batch L2) |
| 1095 | 8. THE NETWORK ON THE DRAWN RASTER (0.7.88, batch RD2; N3's on its |
| 1508 | 8b. THE INTERCHANGES' GROUND AND THE ACCESS ROADS (batch FR7, slice 4; |
| 1676 | 8c. THE SPURS DEALT TO THE PLANS' SIDINGS (batch FR7, slice 5; spec- |
| 2214 | 4. THE SIDECAR |
| 2304 | 5. THE COST FOLLOWS THE SCREEN |
| 2471 | 7. THE VIEW'S PURE HALF (0.7.61, batch J4) |
| 3161 | 9. AT SEA AND ON THE SHORE (0.7.97, batch O13) |
| 3865 | 10. THE MAP ON EARTH (0.7.119, batch E5) |
| 4280 | 11. THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) |
| 4736 | 12. THE MINIMAP (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3) |
| 4981 | 13. THE GLOBE ON SCREEN (0.7.135, batch E9) |
| 5356 | 14. BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) |
| 5756 | 15. THE MATERIALS DRAWN AS THEMSELVES (0.8.0, batch I14; |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 302 | `MapCheck.MONTHS` | `120` | Months the city of section 1 is played: 120 (the design's). |
| 305 | `MapCheck.IRON_AT` | `MONTHS / 3` | The month the played city is handed a whole iron field and orders a mine on it, if it has none (0.7.64): a third of the way. |
| 317 | `MapCheck.JERUS_COUNTS` | `{ 1913, 200, 1212, 0, 65, 4, 1701, 4, 0, 2, 5, 48, 11, 81, 21, 0, 0, 239, 114...` | Jerus's city at month 1,851 (his autosave, 509,455 people on 89.63 km2 of dry ground): every building type's count, by id - 14,214 buildings, the design's own fixture for the map's sizes; 8,204 since 0.7.71, its child... |
| 322 | `MapCheck.JERUS_PEOPLE` | `509_455` | ...his people. |
| 325 | `MapCheck.JERUS_KM2` | `89.63` | ...and his dry ground, in km2. |
| 328 | `MapCheck.JERUS_FILL` | `0.92` | His buildings' footprint over his dry ground: 91.8% (the design's measure), so the design's square city holds this share of a district - his density. |
| 331 | `MapCheck.JERUS_FOOTPRINT_KM2` | `82.273` | ...the land those buildings stood on, by the catalogue his save was measured with (0.7.70's): 82.273 km2, 91.8% of his dry ground (runs/fixN2-notes.md). |
| 349 | `MapCheck.TIMES` | `{ 1, 9_814, 10_000, 19_629 }` | The copies measured: his city x 1, x 9,814 (5 billion people), x 10,000 (the design's) and x 19,629 (10 billion). |
| 352 | `MapCheck.DENSE` | `2` | The copy whose screen is all city, at his density, that section 3 paints: x 10,000. |
| 355 | `MapCheck.SCREEN_ACROSS` | `18, SCREEN_DOWN = 11, SCREEN_TILES = SCREEN_ACROSS * SCREEN_DOWN` | The screen: 18 x 11 tiles, 198 - a 1,389 x 868 view at L0's least 3.2 px a plot is 13.6 x 8.5 tiles, the design's "about 200 with a margin". |
| 358 | `MapCheck.SCREEN_MS` | `80` | The design's bound on that screen's paint and raster, in ms (derived: 38 at its measured 0.19 ms a tile). |
| 361 | `MapCheck.SCREEN_RATIO` | `1.5` | The most any copy's screen may take against the 5B copy's, the first whose screen is all city (the design's; against the city x 1's until 0.7.64 - section 5's note). |
| 364 | `MapCheck.RECONCILE_MS` | `5` | The design's bound on a month's change at 5B and at 10B, in ms of the main thread's CPU since 0.7.100 (measured 0.76; section 5's note). |
| 367 | `MapCheck.PX` | `4` | Pixels a plot the screen is rastered at: 4, L0's image (spec-land 2.6). |
| 370 | `MapCheck.WARM_ROUNDS` | `3, TIMED_ROUNDS = 5` | Rounds of the screen run over every copy before any is timed, and rounds timed, each copy in turn: the least of each copy's timed rounds is its time. |
| 373 | `MapCheck.DRY_PLACE` | `0.97` | How dry the place the copies stand on must be, at a sample a tile over 3 x 3 districts: 97% - every screen tile can be built on, the painter's worst case. |
| 376 | `MapCheck.DRAWN_EVERY` | `30` | The months of section 1's city at which every tile is painted and what is drawn counted against the model: every 30th, four of its 120 (0.7.64). |
| 410 | `MapCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } ...` |  |
| 998 | `MapCheck.NEW_CITY_MONTHS` | `{ 0, 1, 12 }` | The months a new default city is drawn at: as founded, a month on and a year on (Jerus: "a brand new city shows that it has a few houses and a shop when it doesnt"). |
| 1515 | `MapCheck.ACCESS_HIGHWAYS_ADDED` | `200, ACCESS_DEPOTS_ADDED = 3` | The access fixture's added highways and Truck Depots: enough highways to cross, three depots to place. |
| 1683 | `MapCheck.SPURS_ADDED` | `12, BULK_WORKS_ADDED = 90` | The sidings fixture's added Rail Spurs and bulk works (JunctionCheck's two bulk types, two in three the larger). |
| 1982 | `MapCheck.CORRIDOR_STAGES` | `{ { 1, 6 }, { 3, 92 }, { 6, 400 }, { 9, 1385 } }` | Spec 2.7's table on the game's own ground: the design's dry place, the city's ground a square 1, 3, 6 and 9 districts a side (4 to 35 km from its middle, the prototype's 4 to 34), and its Elevated Highways the prototy... |
| 2137 | `MapCheck.YARDS_ADDED` | `2, FREIGHT_ADDED = 1` | The fixture's added rail: Rail Terminals (yards) and Freight Lines, to hold the yards' rule on (his city has spurs, no yard; 0.7.72's fixture). |
| 2329 | `MapCheck.THREADS` | `java.lang.management.ManagementFactory.getThreadMXBean()` | The JVM's threads, read for the main thread's CPU time. |
| 2332 | `MapCheck.CPU_STEP_MS` | `0.1` | The coarsest step of the thread's CPU clock a bound is read on, in ms: 0.1, a twentieth of the quickest stamp timed; coarser (Windows: 15.6) and the bounds are read on the wall clock. |
| 2335 | `MapCheck.CPU_TIMED` | `cpuTimed()` | Whether a month is timed on the main thread's CPU: this JVM measures it, it is on (turned on here if it is off), and its clock steps no coarser than CPU_STEP_MS. |
| 2476 | `MapCheck.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 2479 | `MapCheck.SCREEN_POINTS` | `{ { 0, 0 }, { 300, 200 }, { 1344, 805 }, { 17.25, 640.5 }, { 1000, 3 } }` | Points across the screen the transforms are tried at. |
| 2929 | `MapCheck.BIG_OFFER_LEVEL` | `9` | The level of the offer the overlay's clip is tried on, zoomed in as far as the view goes: 9, blocks of 15.36 km - an offer of 2 x 4 of them is over three views across even at the expanded size, which 0.7.61's corners'... |
| 3036 | `MapCheck.DRAFT_MONTHS` | `24` | Months the draft's town is played before its map is drawn: 24. |
| 3166 | `MapCheck.SHORE_SIDE` | `3` | The section's city: three districts a side, all of it owned and drawn canonically on its measured ground - the playtest's coast (its bay, its south shore, its lagoon) - about a site one district west of the default wo... |
| 3183 | `MapCheck.BOAT_FRAME_MS` | `0.5` | The design's bound on a frame's boats at 10 billion, in ms (spec-oil 5's O13 row: "a frame's boats <= 0.5 ms at 10B, measured 0.38"). |
| 3186 | `MapCheck.BOAT_FRAMES` | `200` | Frames timed for that bound, a round: 200 at times through the month; the least of TIMED_ROUNDS rounds' means after WARM_ROUNDS. |
| 3458 | `MapCheck.TRAIN_FRAME_MS` | `BOAT_FRAME_MS` | The design's bound on a frame's trains at 10 billion, in ms: the boats' (spec-freight 4.4, "the same structure is well inside budget"). |
| 3461 | `MapCheck.TRAIN_YARDS_A_SIDE` | `40` | Yards a side of the synthetic city the frame is timed on at 10 billion: 40, 1,600 yards, 12,800 routes on one main line. |
| 3702 | `MapCheck.SCREEN_W_PX` | `1389, SCREEN_H_PX = 868` | The view's size in pixels the frames are timed at: section 5's 1,389 x 868 screen. |
| 3705 | `MapCheck.PROTO_TONNES` | `896_308, PROTO_PEOPLE = 469_092` | BoatProto's trade (the oil spec's prototype, its out/boat.txt): the playtest's tonnes across the boundary a month at m4000 (pt0770's save) and its people then, scaled from by people. |
| 3708 | `MapCheck.PROTO_LANES` | `20_000` | BoatProto's lanes at 10B: its cap of 20,000, each from its berth 200 plots east and 1,500 north ("a lane 45 km out to sea, north of the coast"). |
| 3710 | `MapCheck.PROTO_LANE_DX` | `200, PROTO_LANE_DY = - 1_500` | ...each lane's far end, in plots from its berth: 200 east and 1,500 north (north is less y). |
| 3870 | `MapCheck.WINDOW_STRIDE` | `EarthTerrainCheck.WINDOW / MapFrame.NODE_PX` | The stride the study's windows are drawn at: EarthTerrainCheck.WINDOW / NODE_PX, 16 plots a pixel - one node is the window, a world cell, and from it the far node strokes its own water and dithers its forest (EarthTer... |
| 3873 | `MapCheck.CLOSE_STRIDES` | `{ 2, 4, 8 }` | The strides under WINDOW_STRIDE the windows are drawn at too, where the far node is E2's region but for its rivers: 2, 4 and 8 plots a pixel. |
| 3876 | `MapCheck.SEAM_SHIFT_PX` | `MapFrame.NODE_PX / 2` | How far a node is moved to hold the 180th meridian with its neighbour, in pixels: half a node, 64 - whole blocks of EarthTerrain.FAR_BLEND_PX, so two nodes read every pixel they share alike. |
| 3879 | `MapCheck.CHUKOTKA_LAT` | `66, CHUKOTKA_LON = 180` | The full file's seam: the 180th meridian in Chukotka, 66 N, rivers and lakes either side of it. |
| 3882 | `MapCheck.CHUKOTKA_STRIDE` | `EarthTerrain.FAR_TILE_BLEND_STRIDE` | ...drawn at 64 plots a pixel (1.9 km; a node 246 km across), EarthTerrain.FAR_TILE_BLEND_STRIDE: every river stroked (FAR_RIVER_SHARE's 23 m), each tile's points blended. |
| 3885 | `MapCheck.LAKES_LAT` | `62, LAKES_LON = 26` | The full file's small lakes: Finland's lake district, 62 N 26 E. |
| 3888 | `MapCheck.LAKES_NODES` | `3` | ...over this many nodes a side about the spot at WINDOW_STRIDE: 3, 184 km, a few thousand lakes. |
| 4285 | `MapCheck.PICTURE_MONTHS` | `MONTHS` | Months section 11 plays the played city on, every district's picture asked after each as a far view asks for it: MONTHS more. |
| 4288 | `MapCheck.PICTURE_SIGMAS` | `4` | How far a picture's share of a kind may stand from its plots' share: this many of its binomial spreads (DistrictPicture.spread()), and a pixel - four, about one district in 16,000 past it by chance. |
| 4291 | `MapCheck.NODE_ROUNDS` | `TIMED_ROUNDS` | Rounds a far node is timed over after WARM_ROUNDS, the least its time: TIMED_ROUNDS. |
| 4741 | `MapCheck.FAR_DISTRICTS` | `4` | How far out the played city's far block is bought: 4 districts past its built-up ground's east edge (31 km) - districts of its own, nothing built in them. |
| 4744 | `MapCheck.STAMP_WARM` | `20, STAMP_TIMED = 10` | Rounds the stamp is drawn before it is timed, and rounds timed, the least its time: 20 and 10 - it is drawn every month of play, so its steady state is what is timed, the JIT's compiling done (about 5 ms a round; sect... |
| 4986 | `MapCheck.KEY_FRAMES` | `60` | Frames of the keys' second: 60, the pump's (a sixtieth each). |
| 4989 | `MapCheck.KEY_SLOP_PX` | `1e-3` | How far a second of the keys may miss KEY_PX_A_SECOND, in pixels: a thousandth (★E9) - on the plane it is exact; on the globe each frame's drag keeps the point grabbed under the pointer to GlobeCheck's POINTER_PX. |
| 4992 | `MapCheck.MATRIX_PLOTS` | `1e-6` | The composed matrix's worst against the camera's and the plane's own projections, in plots: a millionth (★E9; measured 4e-10). |
| 5361 | `MapCheck.BOXES` | `200` | Boxes drawn at random on the played city's opening view, each tried against every plot under it: 200. |
| 5364 | `MapCheck.GLOBE_BOXES` | `60` | ...and through the globe's camera, each against a point at every free plot's middle: 60. |
| 5367 | `MapCheck.FIELD_BUYS_MOST` | `200` | The most offers the city buys toward an iron field for one to be on offer: 200, MiningCheck.makeRoom()'s own bound. |
| 5370 | `MapCheck.BOX_PLOTS_MOST` | `200_000` | The most plots a box is tried on, one by one: 200,000 - a box larger is drawn again smaller. |
| 5373 | `MapCheck.GLOBE_LAT` | `51.90625, GLOBE_LON = 4.48125` | Where the globe's check looks from: EarthCheck's Rotterdam window's centre, the played city's site put there. |
| 5762 | `MapCheck.MATERIALS_SIDE` | `10` | The materials fixture's square, in districts a side: 10 (77 km), wide enough to hold fields of every typed slot on the default world. |
| 5765 | `MapCheck.MATERIALS_SECTORS` | `{ Sectors.MINING, Sectors.HEAVY_INDUSTRY, Sectors.COAL_MINING, Sectors.QUARRY...` | The nine materials industries, Iron Mining to Wood Products (spec-materials 2.1): every building of theirs drawn with a look of its own. |
| 6102 | `MapCheck.MATERIALS_RINGS` | `24` | How far out materialsPlace() looks, in cells: 24. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 293 | `static int fails` |  |
| 294 | `static PrintStream out` |  |
| 604 | `long[] buildings` | Buildings by type id: a mine or well on a site once, however many tiles its site spans - since 0.8.0 a works once for all the sites it takes; packed ones among them. |
| 606 | `long[] siteCount` | The sites under each type's works, by type id (0.8.0), each once; and the types whose sites are not a whole number of its works. |
| 607 | `long partWorks` |  |
| 609 | `final double[] surface` | The streets' surface in plots by kind [0, gravel, paved, highway] - a highway's own plots among the highway's - and what the plans have no street for (CityMap.leftoverByKind()). |
| 611 | `long rail, trackSurplus, packed, tiles, districts` | The railway's track laid, in plots (0.7.72), and (0.7.89) what the runs could not lay - since FR7 slice 3 the plots no train's way uses (CityRuns.trackSurplus()); buildings packed at the city's edge (R7); tiles painte... |
| 708 | `static GameFiles cityFiles` | The played city's save folder. |
| 816 | `final BuildingVisual.Type[] types` |  |
| 817 | `final long seed` |  |
| 818 | `final long x, y` |  |
| 819 | `final CityMap[] maps` |  |
| 820 | `final long[][] counts` |  |
| 821 | `final double[] buildMs` |  |
| 823 | `final long ironX, ironY` | His city x 1 again where its square holds iron (0.7.99, batch W1; dryPlace(w, side)): the fixtures that need his mines - the runs' marks, the rail yards - stand on it; the dry place's own holds none since the deposits... |
| 824 | `final CityMap ironX1` |  |
| 825 | `final long[] ironCounts` |  |
| 1113 | `final CityMap map` |  |
| 1114 | `final long px0, py0` |  |
| 1115 | `final int w, h` |  |
| 1116 | `final byte[] use, road, width, role, run` |  |
| 1117 | `final boolean[] owned, sea, wet, bridge, beneath, mined` |  |
| 1119 | `final List<int[]> boxes` | Each building: {x0, y0, w, h, type, site (or -1), packed (1 or 0)} in the box's plots. |
| 1120 | `long rail, crossings, packed, siding` |  |
| 1121 | `final double[] surface` |  |
| 1122 | `final long[] buildings` |  |
| 1123 | `final java.util.Set<Long> sites` |  |
| 2045 | `final long x0, y0` |  |
| 2046 | `final int w, h` |  |
| 2047 | `final byte[] fixed` |  |
| 4086 | `final List<double[]> segs` |  |
| 4087 | `final List<double[]> lakes` |  |
| 4088 | `final long x0, y0` |  |
| 4089 | `final int stride` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 291 | 5832 | **type** `public class MapCheck` | The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter draws each district's street plan and paints the same pixels from the same inputs, the draw... |
| 296 | 4 | `static void check(String label, boolean ok)` |  |
| 340 | 7 | `static double groundKm2(BuildingVisual.Type[] types)` | The dry ground the copies' x 1 stands on (0.7.71): his, scaled by his buildings' land on this catalogue over theirs then, so the copies keep his density (JERUS_FILL) whatever the catalogue: with his childcare restated... |
| 378 | 31 | `public static void main(String[] args) throws Exception` |  |

### 1. A CITY'S MAP ADDS UP (lines 415-596)

| line | len | member | says |
|---:|---:|---|---|
| 419 | 127 | `static Game playedCity(Path root) throws Exception` |  |
| 560 | 36 | `static boolean worldWaysOk(CityMap m, int[] seen)` | Whether a month's lines to the world and trains lie on the ground they should (FX2; GatewayCheck holds the whole list on its fixtures, this is the cheap half, the points and the routes and never the box): each world l... |

### WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64) (lines 597-730)

| line | len | member | says |
|---:|---:|---|---|
| 602 | 32 | **type** `static final class Drawn` | What the painted map draws over every tile of a map's districts and a tile round them (0.7.88: a district's plan lays the next one's first column and row). |
| 613 | 1 | `long total()` _(in MapCheck.Drawn)_ |  |
| 614 | 1 | `int kinds()` _(in MapCheck.Drawn)_ |  |
| 615 | 1 | `double surfaced()` _(in MapCheck.Drawn)_ |  |
| 616 | 1 | `double left()` _(in MapCheck.Drawn)_ |  |
| 619 | 14 | `boolean sameAs(long[] model, BuildingVisual.Type[] types)` _(in MapCheck.Drawn)_ | Whether it is exactly the model's: every drawn type as many as the model has (a kind it has none of not drawn); each road kind's plots its streets' surface and what they do not carry, to within half a plot a district ... |
| 636 | 9 | `static long[] tilesOf(CityMap m)` | The tiles a map's districts cover and one round them: {tx0, ty0, across, down}. |
| 647 | 37 | `static Drawn drawn(CityMap m)` | Paints every tile of every district on a map, and a tile round them, and counts what is drawn. |
| 686 | 5 | `static long modelBuildings(long[] model, BuildingVisual.Type[] types)` | The model's buildings the map draws: every type but the roads. |
| 693 | 7 | `static long roadPlotsOf(long[] model, BuildingVisual.Type[] types, int kind)` | The model's road plots of a kind: each road type's count x its own plots (BuildingVisual.cells()). |
| 702 | 4 | `static long modelRoadPlots(long[] model, BuildingVisual.Type[] types)` | ...of every kind. |
| 711 | 8 | `static boolean roomAt(CityMap.District d, BuildingVisual.Type type, double share)` | Whether district d has room for one more of a type at a road share: CityMap.hasRoomFor()'s rule (buildingRoom()), at the share given rather than the map's own. |
| 721 | 4 | `static long mineSites(CityMap m, int mine)` | The sites a mine type may stand on (0.8.0, batch I14): its resource's of its type of field - the game's own rule since I5 (Game.committedOn(r, type), LandManager.fits()), an Iron Mine on hematite. |
| 726 | 4 | `static int mineId(Game g)` |  |

### 2. NOTHING PLACED MOVES (lines 731-809)

| line | len | member | says |
|---:|---:|---|---|
| 735 | 74 | `static void nothingMoves(Game g)` |  |

### THE COPIES: JERUS'S CITY ON A SQUARE OF DISTRICTS, AT HIS DENSITY (lines 810-928)

| line | len | member | says |
|---:|---:|---|---|
| 815 | 62 | **type** `static final class Squares` | His city x 1 and its copies, each on the design's square city at the dry place, its mines no more than its sites. |
| 827 | 49 | `Squares()` _(in MapCheck.Squares)_ |  |
| 879 | 3 | `static int x1Side(BuildingVisual.Type[] types)` | The side of Jerus's city x 1 on the design's square city, in districts: its ground over a district's (Squares). |
| 884 | 3 | `static long[] dryPlace(World w)` | The world cell nearest the founding site's whose middle 3 x 3 districts are DRY_PLACE dry at a sample a tile: {x, y, rings out}. |
| 896 | 18 | `static long[] dryPlace(World w, int side)` | ...and (0.7.99, batch W1), with `side` over 0, whose square for Jerus's city x 1 - `side` districts round its middle, as CityMap.square() lays it - holds an iron field's centre, so his mines have sites and his railway... |
| 916 | 12 | `static boolean holdsIron(World w, long x, long y, int side)` | Whether the square of `side` districts CityMap.square() lays round plot (x, y) holds an iron field's centre. |

### 3. THE PAINTER DRAWS THE PLAN (0.7.88, batch RD2; the mockup's rules on (lines 929-992)

| line | len | member | says |
|---:|---:|---|---|
| 934 | 58 | `static void painterRules(CityMap m)` |  |

### 6. THE CITY DRAWN AS WHAT IT HAS (0.7.64, batch L2) (lines 993-1094)

| line | len | member | says |
|---:|---:|---|---|
| 1010 | 84 | `static void drawnAsItIs(CityMap dense)` | Jerus, 2026-10-07: "the generation should only put what the city has, not more not less". |

### 8. THE NETWORK ON THE DRAWN RASTER (0.7.88, batch RD2; N3's on its (lines 1095-1507)

| line | len | member | says |
|---:|---:|---|---|
| 1112 | 309 | **type** `static final class Raster` | A map painted whole over a box of tiles and put together: what each plot is, every building's box - what holds the network's rules. |
| 1125 | 54 | `Raster(CityMap map, long tx0, long ty0, int tw, int th)` _(in MapCheck.Raster)_ |  |
| 1181 | 4 | `static Raster of(CityMap map)` _(in MapCheck.Raster)_ | Every tile of every district of a map, and a tile round them. |
| 1187 | 1 | `boolean isRoad(int g)` _(in MapCheck.Raster)_ | A road plot: a street (a track among them, the track's level crossings among them), or a highway's. |
| 1190 | 1 | `boolean isStreet(int g)` _(in MapCheck.Raster)_ | A street plot: a road plot with a street's role - not a highway's own. |
| 1193 | 1 | `boolean isHighway(int g)` _(in MapCheck.Raster)_ | A highway's own plot (its runs'). |
| 1195 | 1 | `boolean isRail(int g)` _(in MapCheck.Raster)_ |  |
| 1198 | 1 | `boolean isRunHighway(int g)` _(in MapCheck.Raster)_ | A plot of the city's highways as its runs lay it (0.7.89), under a building or not - the railway bridging it among them. |
| 1201 | 1 | `boolean isRunRail(int g)` _(in MapCheck.Raster)_ | ...of its railway's track, under a yard or a mine too, or bridging a highway. |
| 1203 | 5 | `long streetPlots()` _(in MapCheck.Raster)_ |  |
| 1209 | 8 | `int beside(int g, java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ |  |
| 1219 | 19 | `int[] pieces(java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ | The pieces plots of a kind make, four ways joined: each plot's piece number (0 for none), [0] = how many pieces. |
| 1249 | 37 | `int[] network()` _(in MapCheck.Raster)_ | The road's pieces and those the city's own ground joins to the largest: {pieces, plots of the largest, pieces the ground joins to it} - from the largest piece, four ways over owned ground that is not the sea or a mine... |
| 1287 | 5 | `long count(java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ |  |
| 1294 | 5 | `long alone()` _(in MapCheck.Raster)_ | Road plots with no road beside them, four ways: a stray. |
| 1301 | 9 | `boolean crossing(int g)` _(in MapCheck.Raster)_ | A + junction of streets: a street plot with street on its four sides and none on its four corners (a square's middle, street on its corners too, is not one; nor a street beneath a highway, which passes under it). |
| 1312 | 16 | `long[] crossings(int apart)` _(in MapCheck.Raster)_ | {crossings, pairs of them nearer than `apart` either way}. |
| 1330 | 18 | `int[] reach(int most)` _(in MapCheck.Raster)_ | Each plot's distance from a street, across corners (a street 0), to `most` + 1. |
| 1350 | 5 | `static int boxReach(int[] dist, int w, int[] b)` _(in MapCheck.Raster)_ | A box's nearest plot's distance. |
| 1357 | 3 | `CityMap.District districtAt(int g)` _(in MapCheck.Raster)_ | The district a plot of the box is in. |
| 1367 | 24 | `int[] asPlanned()` _(in MapCheck.Raster)_ | Every plot the plans of the districts in the box surface (Drawn's codes, its frame's east column and south row among them) against the painted plot: {plots, those not painted as that street - its kind and its width}. |
| 1393 | 27 | `int[] bridges()` _(in MapCheck.Raster)_ | The bridges: {runs longer than their line may cross - an arterial's or a boulevard's ARTERIAL_BRIDGE, a street's STREET_BRIDGE, a highway's MAX_BRIDGE - roads on the sea, bridge plots}. |
| 1428 | 46 | `static void networkOn(CityMap m, long[] model, String what)` | The network's rules on a map's drawn raster (0.7.88): one network but where the ground parts it, no road alone, + junctions apart, every building within reach, the streets' surface the model's road kind by kind, every... |
| 1475 | 32 | `static void network(Squares sq, Game city, String[] saves) throws Exception` |  |

### 8b. THE INTERCHANGES' GROUND AND THE ACCESS ROADS (batch FR7, slice 4; (lines 1508-1675)

| line | len | member | says |
|---:|---:|---|---|
| 1518 | 5 | `static int depotType(BuildingVisual.Type[] types)` | The type id of the Truck Depot, the one building of the category HAULAGE. |
| 1528 | 3 | `static CityMap accessCity(Squares sq, boolean highways)` | The yard city (his city x 1 where its square holds iron, with two Rail Terminals and a freight line) with highways added to cross and Truck Depots to place; with `highways` false his own highways taken out too: none, ... |
| 1533 | 33 | `static CityMap accessCity(Squares sq, boolean highways, long[] extra)` | ...with `extra` counts added (batch FR7 slice 5: spurs and bulk works for the sidings' deal): a track type's to the railway, any other's to the buildings. |
| 1571 | 20 | `static int[] accessFigures(CityMap m)` | What a map's plans gave its works' access roads: {roads, on a ramp, on an arterial, already on one, gates not beside their box, ends neither a ramp's street nor an arterial, the map's works (yards and sea terminals) g... |
| 1592 | 83 | `static void interchangesAndAccess(Squares sq)` |  |

### 8c. THE SPURS DEALT TO THE PLANS' SIDINGS (batch FR7, slice 5; spec- (lines 1676-2213)

| line | len | member | says |
|---:|---:|---|---|
| 1686 | 74 | `static void sidingsDealt(Squares sq)` | The spurs the runs' sidings left, as the map deals them: each dealt district is one the main line crosses with bulk works of its own, no more than it holds, and the plans lay at most their share, drawn on the raster; ... |
| 1762 | 23 | `static Game loadCopy(Path src) throws Exception` | A save's autosave copied into a temporary folder and loaded (its map drawn from its sidecar or canonically): saves named on the command line, never written. |
| 1794 | 30 | `static CityMap riverCity(Squares sq, int kind)` | The river fixture (0.7.77, batch N5): Jerus's city x 1 on the design's square city sited at the default world's founding river - its point nearest the founding site - with its streets all of one kind: his Paved Roads ... |
| 1834 | 44 | `static void riverCrossed(Squares sq)` | A river parts gravel streets no more than paved ones (0.7.77; Jerus, 2026-10-08: "gravel road bridge rivers sure"): the river fixture all gravel against its paved twin - the road pieces with a plot within a tile of th... |
| 1887 | 32 | `static void highways(Raster net, String what)` | The city's highways on the drawn raster (0.7.89, batch RD3; spec 2.7): drawn plot for plot as its runs lay them, and with what they could not lay the model's Elevated Highways; one network; no building plot touching a... |
| 1933 | 47 | `static int marksReachTheView(CityMap m, long tx0, long ty0, int tw, int th, String what, boolean bands)` | The runs' marks reach the picture a screen draws (0.7.98, batch O14): a screen rasters a tile from the view's kept copy of its inputs (MapTiles.painted(), paintedInput()), and until then that copy held none of the run... |
| 1995 | 47 | `static void corridors(Squares sq)` | The highways on corridors on the game's own ground (0.7.89; spec 2.7): each stage of spec 2.7's table on a square city of highways alone at the dry place, laid at once - its plots each laid or counted, one network, st... |
| 2044 | 55 | **type** `static final class Box` | The runs' plots over a map's districts and a plot round them, put together: one network, its junctions. |
| 2049 | 5 | `Box(long x0, long y0, int w, int h, CityRuns r)` _(in MapCheck.Box)_ |  |
| 2055 | 1 | `static Box of(CityMap m)` _(in MapCheck.Box)_ |  |
| 2057 | 4 | `static Box of(CityMap m, CityRuns r)` _(in MapCheck.Box)_ |  |
| 2062 | 1 | `boolean hw(int g)` _(in MapCheck.Box)_ |  |
| 2065 | 18 | `int pieces()` _(in MapCheck.Box)_ | The highways' pieces, four ways joined. |
| 2085 | 13 | `long junctions()` _(in MapCheck.Box)_ | Highway plots with highways on three or four sides: where they meet. |
| 2101 | 17 | `static int bendsStopped(CityMap m, CityRuns.Net n)` | The bends of a net that are not where its way was stopped: a stretch off its arm's heading begun where the way on along the heading before ran LOOK plots clear, or one back onto it where the way home did not run TURN_... |
| 2120 | 15 | `static void rail(Raster net, String what)` | The railway on the drawn raster (0.7.89; spec 2.8): one network, every plot of the model's track drawn - under its yards and mines too - or counted as what the runs could not lay (since FR7 slice 3 the surplus no trai... |
| 2140 | 25 | `static CityMap yardCity(Squares sq)` | The x 1 copy with rail yards and a freight line added (0.7.72's fixture, restored in 0.7.89): its buildings, then its mines on their sites, then its railway - so the railway starts at its mine nearest the founding sit... |
| 2173 | 40 | `static void yardsNearMines(CityMap m, Raster net)` | Every rail yard on its track, in the cell along it nearest a mine that had room (spec 2.8: "laid as runs from the rail terminals, which sit in estate cells nearest the mines"; 0.7.72's rule, restored): the track runs ... |

### 4. THE SIDECAR (lines 2214-2303)

| line | len | member | says |
|---:|---:|---|---|
| 2218 | 85 | `static void sidecar(Game g, Path root, Squares sq) throws Exception` |  |

### 5. THE COST FOLLOWS THE SCREEN (lines 2304-2470)

| line | len | member | says |
|---:|---:|---|---|
| 2337 | 9 | `static boolean cpuTimed()` |  |
| 2348 | 10 | `static double cpuStepMs()` | The least step the main thread's CPU clock takes, in ms: five times, spinning until it moves (200 ms of the wall clock at most each); infinite if it never does. |
| 2360 | 3 | `static long monthClock()` | The clock a month's bound reads, in ns: the main thread's CPU, or the wall clock where CPU_TIMED is false. |
| 2364 | 106 | `static void cost(Squares sq)` |  |

### 7. THE VIEW'S PURE HALF (0.7.61, batch J4) (lines 2471-3160)

| line | len | member | says |
|---:|---:|---|---|
| 2481 | 8 | `static void viewHalf(Game city, Squares sq, Path root) throws Exception` |  |
| 2491 | 72 | `static void frames()` | MapFrame: the transforms, a notch of the wheel at the pointer, the clamps, the levels and what a screen asks for. |
| 2565 | 121 | `static void picks(Game g)` | LandMap: what a click and a hover pick on the played city agrees with the model's own grid (CityLand.holdingOf()) and GridOffers' sides and places, and the outlines with the picks (0.7.67; the lanes' owns() and sideLa... |
| 2699 | 228 | `static void overlay(Game g)` | The overlay on the block grid (0.7.69, batch M5; spec-grid 2.4), what MapView draws by: the city's edge as maximal runs with its ground on their right, drawn crisp - whole pixels, a pixel thick, just inside the ground... |
| 2932 | 3 | `static char dir(double x0, double y0, double x1, double y1)` | A run's way: E, S, W or N. |
| 2937 | 3 | `static boolean touch(double[] a, double[] b)` | Whether two boxes of pixels {x, y, w, h} share a pixel or touch at a corner or an edge. |
| 2942 | 13 | `static int inQuad(double[] q, double x, double y)` | Whether a point is inside a convex quadrilateral (corners in order): 1 inside, 0 within a hair of an edge, -1 outside. |
| 2957 | 77 | `static void draft(Path root) throws Exception` | Game.mapDraft()/adoptMap(): the map drawn on another thread is the map drawn here, a month between is caught up, and a draft for other land is not kept. |
| 3039 | 120 | `static void tiles(Squares sq)` | MapTiles: a tile's stamp holds while nothing moves, one more building restamps only the tiles it changes, the view's per-tile path follows the screen, and every cache fits the budget. |

### 9. AT SEA AND ON THE SHORE (0.7.97, batch O13) (lines 3161-3864)

| line | len | member | says |
|---:|---:|---|---|
| 3169 | 12 | `static CityMap coastal(long seed, long sx, long sy, BuildingVisual.Type[] types, int side, long[] counts)` | A city owning `side` districts a side about (sx, sy), every plot of them, its map drawn canonically on the ground measured. |
| 3189 | 12 | `static long[] shoreCounts(BuildingVisual.Type[] types)` | The section's town: Jerus's mix at a tenth, one of each of the refinery's units, two of each terminal and one of each tank farm. |
| 3202 | 254 | `static void atSea(BuildingVisual.Type[] types, long seed)` |  |
| 3473 | 202 | `static void trains(Squares sq, Game city)` | Section 9's trains (0.8.2, batch FR1; spec-freight 4.4, D16): on the yard city (yardCity()), the routes run from the world line along the track into each yard and onto its sidings; the trains are its railed tonnes ove... |
| 3683 | 17 | `static List<TrainSchedule.Route> syntheticYards(long cx, long cy, int side)` | A city of yards about (cx, cy) for the frame at 10 billion: side x side yards on a grid four cells apart, each with its YARD_TRACKS routes in from one gateway WORLD_PLOTS west - along the row to its column, down the c... |
| 3717 | 12 | `static List<BoatSchedule.Route> protoLanes(long cx, long cy)` | BoatProto's 20,000 lanes about (cx, cy): berths at hashed places along its coast's span at 10B - sqrt(10B / 5,000) x 33 plots, its "city's span in plots (rough)" - every other one liquid bulk's, the rest dry bulk's. |
| 3731 | 5 | `static int tileGround(World w, long x, long y)` | The world's ground at a plot as the painter reads it: its tile's (World.tileTerrain()), what the shore's search and the routes read. |
| 3738 | 19 | `static boolean connected(java.util.Set<Integer> cells)` | Whether a set of cells (ci + cj x CELLS_A_SIDE) is one piece, eight ways: a corner touching counts, as DistrictPlan.campusNext() opens them. |
| 3759 | 5 | `static String worksWords(CityShore shore, BuildingVisual.Type[] types)` | The shore's works by type, in words. |
| 3766 | 17 | `static Ports tradeAtSea(double liquid, double dryBulk, double general, double boxes)` | A month billed with these tonnes of each kind shipped out, all of them by sea: each on the first good of its kind that is not crude. |
| 3791 | 73 | `static void oilAtSea()` | The game's own platform, its wells and its pipe on the map (WellCheck's sea town): Game.mapAtSea() puts the jacket in the middle of its slotted sites and its wells on them, the pipe from the jacket to the founding sit... |

### 10. THE MAP ON EARTH (0.7.119, batch E5) (lines 3865-4279)

| line | len | member | says |
|---:|---:|---|---|
| 3891 | 106 | `static void onEarth(BuildingVisual.Type[] types) throws Exception` | Section 10. |
| 4005 | 73 | `static void farWindow(String name, World w, EarthData data)` | One window drawn as a far node at WINDOW_STRIDE and at CLOSE_STRIDES: closer, the region but for its rivers stroked wider; at the window's stride every river stroked at least FAR_RIVER_HALF_PX a side, every small lake... |
| 4085 | 60 | **type** `static final class Water` | The rivers and small lakes under a far node, as the check reads them from the data itself: each run the node would stroke (its median width at least FAR_RIVER_SHARE of a pixel), its segments projected exactly, and eac... |
| 4091 | 29 | `Water(EarthTerrain e, EarthData data, long x0, long y0, int stride)` _(in MapCheck.Water)_ |  |
| 4122 | 12 | `boolean inStroke(int i, int j, boolean least)` _(in MapCheck.Water)_ | Whether pixel (i, j)'s centre lies within a river's stroke: its least half-width alone (`least`), or its own. |
| 4136 | 8 | `boolean inCore(int i, int j)` _(in MapCheck.Water)_ | Whether pixel (i, j)'s centre lies in a small lake's sure part: within its radius x the root of 1 - BLOB_FRAY / 2, which its blob holds whatever its noise. |
| 4153 | 36 | `static void seam(String where, World w, int stride, EarthData data, boolean water)` | A node holding the 180th meridian (its middle on the spot, which is on it) and its neighbours moved SEAM_SHIFT_PX east and west: on the half each shares with it, pixel for pixel the same; each side of the line's land ... |
| 4191 | 88 | `static void fullFile(int widest, EarthTerrainCheck.Spot rot) throws Exception` | Section 10 on the full file, when it is named (-Dcbs.earth): the longest segment, every stride's cost, the whole world, the small lakes' area and Chukotka's seam. |

### 11. THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) (lines 4280-4735)

| line | len | member | says |
|---:|---:|---|---|
| 4293 | 357 | `static void pictures(Game city, Squares sq)` |  |
| 4652 | 5 | `static Map<CityMap.District, DistrictPicture> picturesOf(CityMap m)` | A district's pictures, every one asked for: each district of the map to its picture (null where it has none). |
| 4659 | 5 | `static long[] countsOf(CityMap.District d, int types)` | A district's counts by type id. |
| 4666 | 5 | `static String codeName(int c)` | A picture's code's name, for the prints. |
| 4680 | 26 | `static byte[] pictureSource(CityMap m, CityMap.District d, int[] boxPlots)` | The plots a district's picture is drawn from, read here from its plan as the painter reads it (CityMap.Drawn: its streets, its boxes by tile) and its own plan's inputs (the runs over its frame, the mines on their site... |
| 4707 | 5 | `private static void fill(byte[] src, int x0, int y0, int w, int h, byte code, int[] counted)` |  |
| 4714 | 15 | `static boolean[] runPixelsOf(CityMap m, long x0, long y0, long plots)` | Which pixels of a far node at (x0, y0), `plots` a pixel, hold a plot of the city's highways or track: every plot of every stretch walked, each 45-degree step's corner with it (CityRuns.onStretch()). |
| 4730 | 5 | `private static void markPixel(boolean[] out, long x, long y, long x0, long y0, long plots)` |  |

### 12. THE MINIMAP (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3) (lines 4736-4980)

| line | len | member | says |
|---:|---:|---|---|
| 4746 | 196 | `static void miniMap(Game city, Squares sq) throws Exception` |  |
| 4948 | 32 | `static MiniMapFrame.Box builtUpByLeaves(CityMap m)` | The built-up ground found another way, for the check: every leaf of the grid cut to each district with buildings it lies in, their bounds; a built district with none owned its whole square. |

### 13. THE GLOBE ON SCREEN (0.7.135, batch E9) (lines 4981-5355)

| line | len | member | says |
|---:|---:|---|---|
| 4994 | 321 | `static void globeOnScreen(Squares sq) throws Exception` |  |
| 5317 | 4 | `static byte groundClass(String words)` | The class a legend's ground words name. |
| 5323 | 15 | `static double foundingParting(double lat, double lon, double km)` | How far the founding globe's two drawings part at a scale, in pixels at worst over its canvas's corners and edges: the globe through GlobeFrame against the ground drawn round the view's centre (Lambert's plane there, ... |
| 5340 | 15 | `static CityMap earthX1(Squares sq) throws Exception` | Jerus's city x 1 on the real Earth at Rotterdam, EarthCheck's fixture (section 10's window): null when the fixture is not beside the classes. |

### 14. BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) (lines 5356-5755)

| line | len | member | says |
|---:|---:|---|---|
| 5375 | 351 | `static void buyingFromTheMap(Game g) throws Exception` |  |
| 5728 | 10 | `static java.util.Set<Integer> boxByPlots(CityLand land, List<LandParcel> offers, double x0, double y0, double x1, double y1)` | The offers a box of plots meets, plot by plot: every plot under (x0, y0)-(x1, y1) in some part, not the city's, inside an offer's rectangle. |
| 5740 | 10 | `static boolean inOrder(List<Integer> ids, List<LandParcel> listing)` | Whether these ids stand in the listing's order. |
| 5752 | 3 | `static boolean sameFigure(double a, double b)` | Two money or area figures the same to a billionth of the larger. |

### 15. THE MATERIALS DRAWN AS THEMSELVES (0.8.0, batch I14; (lines 5756-6122)

| line | len | member | says |
|---:|---:|---|---|
| 5769 | 3 | `static boolean materialsOf(BuildingsTemplate t)` | Whether a template is one of the nine industries'. |
| 5774 | 3 | `static long materialsCount(BuildingVisual.Type t)` | The fixture's count of a materials type: one of a works on more than one site, two of anything else - so each slot holds two kinds and the larger is laid first. |
| 5778 | 264 | `static void materials(long seed)` |  |
| 6044 | 3 | `static long countZone(List<CityMap.Tinted> tint, int z)` | How many districts a zone's tint takes. |
| 6049 | 7 | `static java.util.Map<Long, Integer> sitesUnder(CityMap m)` | Each site under a works on a map, by its key, and the works' type. |
| 6058 | 12 | `static int lookSign(BuildingVisual.Type t)` | The colour a look shows on a box of it rastered (TileRaster's THE MATERIALS AS THEMSELVES): a pit's benches, a shaft's headframe, a quarry's stone, the leach plant's pond, the wash plant's conveyor, a camp's logs, pla... |
| 6078 | 22 | `static long[] materialsPlace(World w, int side, int[] need)` | The world cell nearest the founding site's whose square of `side` districts (as CityMap.square() lays it round the cell's middle) is DRY_PLACE dry in its middle 3 x 3 districts and holds fields centred in it with at l... |
| 6105 | 17 | `static int[] slotSites(World w, long x, long y, int side)` | The sites of each slot of the fields centred in the square of `side` districts CityMap.square() lays round plot (x, y). |

