# MapCheck.java - 4,695 lines · 106 methods · 49 constants · harnesses

`ham/citybuildersim/MapCheck.java` - generated 2026-10-12 by CodeMap; line numbers are as of that run.

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
>      track;
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
>      thread's CPU - since 0.7.100 (A28) the thread's CPU, not the wall
>      clock: on the two-core cloud machine the JIT's compiler threads took
>      the main thread's core and a month's wall time jumped 3 to 4.5 ms over
>      its CPU, failing the bound about one run in ten with no change in the
>      work (runs/fixRD7-notes.md 4 and its star 4, and 7; runs/fixO11-notes.md
> ... (155 more lines in the source)

**Uses:** [CityMap](CityMap.md) (252), [World](World.md) (217), [MapFrame](MapFrame.md) (131), [TilePainter](TilePainter.md) (91), [GlobeFrame](GlobeFrame.md) (89), [MapTiles](MapTiles.md) (77), [BuildingVisual](BuildingVisual.md) (71), [LandMap](LandMap.md) (66), [MiniMapFrame](MiniMapFrame.md) (66), [DistrictPicture](DistrictPicture.md) (56), [Game](Game.md) (41), [LongPlaytest](LongPlaytest.md) (36), [CityRuns](CityRuns.md) (30), [EarthTerrain](EarthTerrain.md) (29), [TileRaster](TileRaster.md) (28), [DistrictPlan](DistrictPlan.md) (27), [BoatSchedule](BoatSchedule.md) (25), [EarthData](EarthData.md) (22), [EarthTerrainCheck](EarthTerrainCheck.md) (20), [Resource](Resource.md) (18), [CityShore](CityShore.md) (18), [LandParcel](LandParcel.md) (17), [SeaRoutes](SeaRoutes.md) (17), [CityLand](CityLand.md) (16), [GlobePicture](GlobePicture.md) (14), [GameFiles](GameFiles.md) (13), [Ports](Ports.md) (13), [EarthCheck](EarthCheck.md) (9), [EarthProj](EarthProj.md) (9), [WellCheck](WellCheck.md) (8)... and 15 more

**Used by (3):** [EarthTerrainCheck](EarthTerrainCheck.md), [GlobeCheck](GlobeCheck.md), [PlanCheck](PlanCheck.md)

## Sections

| line | section |
|---:|---|
| 353 | 1. A CITY'S MAP ADDS UP |
| 477 | · WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64) |
| 594 | 2. NOTHING PLACED MOVES |
| 673 | THE COPIES: JERUS'S CITY ON A SQUARE OF DISTRICTS, AT HIS DENSITY |
| 792 | 3. THE PAINTER DRAWS THE PLAN (0.7.88, batch RD2; the mockup's rules on |
| 856 | 6. THE CITY DRAWN AS WHAT IT HAS (0.7.64, batch L2) |
| 958 | 8. THE NETWORK ON THE DRAWN RASTER (0.7.88, batch RD2; N3's on its |
| 1821 | 4. THE SIDECAR |
| 1911 | 5. THE COST FOLLOWS THE SCREEN |
| 2058 | 7. THE VIEW'S PURE HALF (0.7.61, batch J4) |
| 2748 | 9. AT SEA AND ON THE SHORE (0.7.97, batch O13) |
| 3208 | 10. THE MAP ON EARTH (0.7.119, batch E5) |
| 3623 | 11. THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) |
| 4079 | 12. THE MINIMAP (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3) |
| 4324 | 13. THE GLOBE ON SCREEN (0.7.135, batch E9) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 243 | `MapCheck.MONTHS` | `120` | Months the city of section 1 is played: 120 (the design's). |
| 246 | `MapCheck.IRON_AT` | `MONTHS / 3` | The month the played city is handed a whole iron field and orders a mine on it, if it has none (0.7.64): a third of the way. |
| 258 | `MapCheck.JERUS_COUNTS` | `{ 1913, 200, 1212, 0, 65, 4, 1701, 4, 0, 2, 5, 48, 11, 81, 21, 0, 0, 239, 114...` | Jerus's city at month 1,851 (his autosave, 509,455 people on 89.63 km2 of dry ground): every building type's count, by id - 14,214 buildings, the design's own fixture for the map's sizes; 8,204 since 0.7.71, its child... |
| 263 | `MapCheck.JERUS_PEOPLE` | `509_455` | ...his people. |
| 266 | `MapCheck.JERUS_KM2` | `89.63` | ...and his dry ground, in km2. |
| 269 | `MapCheck.JERUS_FILL` | `0.92` | His buildings' footprint over his dry ground: 91.8% (the design's measure), so the design's square city holds this share of a district - his density. |
| 272 | `MapCheck.JERUS_FOOTPRINT_KM2` | `82.273` | ...the land those buildings stood on, by the catalogue his save was measured with (0.7.70's): 82.273 km2, 91.8% of his dry ground (runs/fixN2-notes.md). |
| 290 | `MapCheck.TIMES` | `{ 1, 9_814, 10_000, 19_629 }` | The copies measured: his city x 1, x 9,814 (5 billion people), x 10,000 (the design's) and x 19,629 (10 billion). |
| 293 | `MapCheck.DENSE` | `2` | The copy whose screen is all city, at his density, that section 3 paints: x 10,000. |
| 296 | `MapCheck.SCREEN_ACROSS` | `18, SCREEN_DOWN = 11, SCREEN_TILES = SCREEN_ACROSS * SCREEN_DOWN` | The screen: 18 x 11 tiles, 198 - a 1,389 x 868 view at L0's least 3.2 px a plot is 13.6 x 8.5 tiles, the design's "about 200 with a margin". |
| 299 | `MapCheck.SCREEN_MS` | `80` | The design's bound on that screen's paint and raster, in ms (derived: 38 at its measured 0.19 ms a tile). |
| 302 | `MapCheck.SCREEN_RATIO` | `1.5` | The most any copy's screen may take against the 5B copy's, the first whose screen is all city (the design's; against the city x 1's until 0.7.64 - section 5's note). |
| 305 | `MapCheck.RECONCILE_MS` | `5` | The design's bound on a month's change at 5B and at 10B, in ms of the main thread's CPU since 0.7.100 (measured 0.76; section 5's note). |
| 308 | `MapCheck.PX` | `4` | Pixels a plot the screen is rastered at: 4, L0's image (spec-land 2.6). |
| 311 | `MapCheck.WARM_ROUNDS` | `3, TIMED_ROUNDS = 5` | Rounds of the screen run over every copy before any is timed, and rounds timed, each copy in turn: the least of each copy's timed rounds is its time. |
| 314 | `MapCheck.DRY_PLACE` | `0.97` | How dry the place the copies stand on must be, at a sample a tile over 3 x 3 districts: 97% - every screen tile can be built on, the painter's worst case. |
| 317 | `MapCheck.DRAWN_EVERY` | `30` | The months of section 1's city at which every tile is painted and what is drawn counted against the model: every 30th, four of its 120 (0.7.64). |
| 348 | `MapCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } ...` |  |
| 861 | `MapCheck.NEW_CITY_MONTHS` | `{ 0, 1, 12 }` | The months a new default city is drawn at: as founded, a month on and a year on (Jerus: "a brand new city shows that it has a few houses and a shop when it doesnt"). |
| 1589 | `MapCheck.CORRIDOR_STAGES` | `{ { 1, 6 }, { 3, 92 }, { 6, 400 }, { 9, 1385 } }` | Spec 2.7's table on the game's own ground: the design's dry place, the city's ground a square 1, 3, 6 and 9 districts a side (4 to 35 km from its middle, the prototype's 4 to 34), and its Elevated Highways the prototy... |
| 1744 | `MapCheck.YARDS_ADDED` | `2, FREIGHT_ADDED = 1` | The fixture's added rail: Rail Terminals (yards) and Freight Lines, to hold the yards' rule on (his city has spurs, no yard; 0.7.72's fixture). |
| 1931 | `MapCheck.THREADS` | `java.lang.management.ManagementFactory.getThreadMXBean()` | The JVM's threads, read for the main thread's CPU time. |
| 1934 | `MapCheck.CPU_TIMED` | `cpuTimed()` | Whether a month is timed on the main thread's CPU: this JVM measures it and it is on (turned on here if it is off). |
| 2063 | `MapCheck.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 2066 | `MapCheck.SCREEN_POINTS` | `{ { 0, 0 }, { 300, 200 }, { 1344, 805 }, { 17.25, 640.5 }, { 1000, 3 } }` | Points across the screen the transforms are tried at. |
| 2516 | `MapCheck.BIG_OFFER_LEVEL` | `9` | The level of the offer the overlay's clip is tried on, zoomed in as far as the view goes: 9, blocks of 15.36 km - an offer of 2 x 4 of them is over three views across even at the expanded size, which 0.7.61's corners'... |
| 2623 | `MapCheck.DRAFT_MONTHS` | `24` | Months the draft's town is played before its map is drawn: 24. |
| 2753 | `MapCheck.SHORE_SIDE` | `3` | The section's city: three districts a side, all of it owned and drawn canonically on its measured ground - the playtest's coast (its bay, its south shore, its lagoon) - about a site one district west of the default wo... |
| 2770 | `MapCheck.BOAT_FRAME_MS` | `0.5` | The design's bound on a frame's boats at 10 billion, in ms (spec-oil 5's O13 row: "a frame's boats <= 0.5 ms at 10B, measured 0.38"). |
| 2773 | `MapCheck.BOAT_FRAMES` | `200` | Frames timed for that bound, a round: 200 at times through the month; the least of TIMED_ROUNDS rounds' means after WARM_ROUNDS. |
| 3045 | `MapCheck.SCREEN_W_PX` | `1389, SCREEN_H_PX = 868` | The view's size in pixels the frames are timed at: section 5's 1,389 x 868 screen. |
| 3048 | `MapCheck.PROTO_TONNES` | `896_308, PROTO_PEOPLE = 469_092` | BoatProto's trade (the oil spec's prototype, its out/boat.txt): the playtest's tonnes across the boundary a month at m4000 (pt0770's save) and its people then, scaled from by people. |
| 3051 | `MapCheck.PROTO_LANES` | `20_000` | BoatProto's lanes at 10B: its cap of 20,000, each from its berth 200 plots east and 1,500 north ("a lane 45 km out to sea, north of the coast"). |
| 3053 | `MapCheck.PROTO_LANE_DX` | `200, PROTO_LANE_DY = - 1_500` | ...each lane's far end, in plots from its berth: 200 east and 1,500 north (north is less y). |
| 3213 | `MapCheck.WINDOW_STRIDE` | `EarthTerrainCheck.WINDOW / MapFrame.NODE_PX` | The stride the study's windows are drawn at: EarthTerrainCheck.WINDOW / NODE_PX, 16 plots a pixel - one node is the window, a world cell, and from it the far node strokes its own water and dithers its forest (EarthTer... |
| 3216 | `MapCheck.CLOSE_STRIDES` | `{ 2, 4, 8 }` | The strides under WINDOW_STRIDE the windows are drawn at too, where the far node is E2's region but for its rivers: 2, 4 and 8 plots a pixel. |
| 3219 | `MapCheck.SEAM_SHIFT_PX` | `MapFrame.NODE_PX / 2` | How far a node is moved to hold the 180th meridian with its neighbour, in pixels: half a node, 64 - whole blocks of EarthTerrain.FAR_BLEND_PX, so two nodes read every pixel they share alike. |
| 3222 | `MapCheck.CHUKOTKA_LAT` | `66, CHUKOTKA_LON = 180` | The full file's seam: the 180th meridian in Chukotka, 66 N, rivers and lakes either side of it. |
| 3225 | `MapCheck.CHUKOTKA_STRIDE` | `EarthTerrain.FAR_TILE_BLEND_STRIDE` | ...drawn at 64 plots a pixel (1.9 km; a node 246 km across), EarthTerrain.FAR_TILE_BLEND_STRIDE: every river stroked (FAR_RIVER_SHARE's 23 m), each tile's points blended. |
| 3228 | `MapCheck.LAKES_LAT` | `62, LAKES_LON = 26` | The full file's small lakes: Finland's lake district, 62 N 26 E. |
| 3231 | `MapCheck.LAKES_NODES` | `3` | ...over this many nodes a side about the spot at WINDOW_STRIDE: 3, 184 km, a few thousand lakes. |
| 3628 | `MapCheck.PICTURE_MONTHS` | `MONTHS` | Months section 11 plays the played city on, every district's picture asked after each as a far view asks for it: MONTHS more. |
| 3631 | `MapCheck.PICTURE_SIGMAS` | `4` | How far a picture's share of a kind may stand from its plots' share: this many of its binomial spreads (DistrictPicture.spread()), and a pixel - four, about one district in 16,000 past it by chance. |
| 3634 | `MapCheck.NODE_ROUNDS` | `TIMED_ROUNDS` | Rounds a far node is timed over after WARM_ROUNDS, the least its time: TIMED_ROUNDS. |
| 4084 | `MapCheck.FAR_DISTRICTS` | `4` | How far out the played city's far block is bought: 4 districts past its built-up ground's east edge (31 km) - districts of its own, nothing built in them. |
| 4087 | `MapCheck.STAMP_WARM` | `20, STAMP_TIMED = 10` | Rounds the stamp is drawn before it is timed, and rounds timed, the least its time: 20 and 10 - it is drawn every month of play, so its steady state is what is timed, the JIT's compiling done (about 5 ms a round; sect... |
| 4329 | `MapCheck.KEY_FRAMES` | `60` | Frames of the keys' second: 60, the pump's (a sixtieth each). |
| 4332 | `MapCheck.KEY_SLOP_PX` | `1e-3` | How far a second of the keys may miss KEY_PX_A_SECOND, in pixels: a thousandth (★E9) - on the plane it is exact; on the globe each frame's drag keeps the point grabbed under the pointer to GlobeCheck's POINTER_PX. |
| 4335 | `MapCheck.MATRIX_PLOTS` | `1e-6` | The composed matrix's worst against the camera's and the plane's own projections, in plots: a millionth (★E9; measured 4e-10). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 234 | `static int fails` |  |
| 235 | `static PrintStream out` |  |
| 484 | `long[] buildings` | Buildings by type id: a mine or well on a site once, however many tiles its site spans; packed ones among them. |
| 486 | `final double[] surface` | The streets' surface in plots by kind [0, gravel, paved, highway] - a highway's own plots among the highway's - and what the plans have no street for (CityMap.leftoverByKind()). |
| 488 | `long rail, railShort, packed, tiles, districts` | The railway's track laid, in plots (0.7.72), and (0.7.89) what the runs could not lay; buildings packed at the city's edge (R7); tiles painted; districts. |
| 577 | `static GameFiles cityFiles` | The played city's save folder. |
| 679 | `final BuildingVisual.Type[] types` |  |
| 680 | `final long seed` |  |
| 681 | `final long x, y` |  |
| 682 | `final CityMap[] maps` |  |
| 683 | `final long[][] counts` |  |
| 684 | `final double[] buildMs` |  |
| 686 | `final long ironX, ironY` | His city x 1 again where its square holds iron (0.7.99, batch W1; dryPlace(w, side)): the fixtures that need his mines - the runs' marks, the rail yards - stand on it; the dry place's own holds none since the deposits... |
| 687 | `final CityMap ironX1` |  |
| 688 | `final long[] ironCounts` |  |
| 976 | `final CityMap map` |  |
| 977 | `final long px0, py0` |  |
| 978 | `final int w, h` |  |
| 979 | `final byte[] use, road, width, role, run` |  |
| 980 | `final boolean[] owned, sea, wet, bridge, beneath, mined` |  |
| 982 | `final List<int[]> boxes` | Each building: {x0, y0, w, h, type, site (or -1), packed (1 or 0)} in the box's plots. |
| 983 | `long rail, crossings, packed` |  |
| 984 | `final double[] surface` |  |
| 985 | `final long[] buildings` |  |
| 986 | `final java.util.Set<Long> sites` |  |
| 1652 | `final long x0, y0` |  |
| 1653 | `final int w, h` |  |
| 1654 | `final byte[] fixed` |  |
| 3429 | `final List<double[]> segs` |  |
| 3430 | `final List<double[]> lakes` |  |
| 3431 | `final long x0, y0` |  |
| 3432 | `final int stride` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 232 | 4464 | **type** `public class MapCheck` | The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter draws each district's street plan and paints the same pixels from the same inputs, the draw... |
| 237 | 4 | `static void check(String label, boolean ok)` |  |
| 281 | 7 | `static double groundKm2(BuildingVisual.Type[] types)` | The dry ground the copies' x 1 stands on (0.7.71): his, scaled by his buildings' land on this catalogue over theirs then, so the copies keep his density (JERUS_FILL) whatever the catalogue: with his childcare restated... |
| 319 | 28 | `public static void main(String[] args) throws Exception` |  |

### 1. A CITY'S MAP ADDS UP (lines 353-476)

| line | len | member | says |
|---:|---:|---|---|
| 357 | 119 | `static Game playedCity(Path root) throws Exception` |  |

### WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64) (lines 477-593)

| line | len | member | says |
|---:|---:|---|---|
| 482 | 29 | **type** `static final class Drawn` | What the painted map draws over every tile of a map's districts and a tile round them (0.7.88: a district's plan lays the next one's first column and row). |
| 490 | 1 | `long total()` _(in MapCheck.Drawn)_ |  |
| 491 | 1 | `int kinds()` _(in MapCheck.Drawn)_ |  |
| 492 | 1 | `double surfaced()` _(in MapCheck.Drawn)_ |  |
| 493 | 1 | `double left()` _(in MapCheck.Drawn)_ |  |
| 496 | 14 | `boolean sameAs(long[] model, BuildingVisual.Type[] types)` _(in MapCheck.Drawn)_ | Whether it is exactly the model's: every drawn type as many as the model has (a kind it has none of not drawn); each road kind's plots its streets' surface and what they do not carry, to within half a plot a district ... |
| 513 | 9 | `static long[] tilesOf(CityMap m)` | The tiles a map's districts cover and one round them: {tx0, ty0, across, down}. |
| 524 | 29 | `static Drawn drawn(CityMap m)` | Paints every tile of every district on a map, and a tile round them, and counts what is drawn. |
| 555 | 5 | `static long modelBuildings(long[] model, BuildingVisual.Type[] types)` | The model's buildings the map draws: every type but the roads. |
| 562 | 7 | `static long roadPlotsOf(long[] model, BuildingVisual.Type[] types, int kind)` | The model's road plots of a kind: each road type's count x its own plots (BuildingVisual.cells()). |
| 571 | 4 | `static long modelRoadPlots(long[] model, BuildingVisual.Type[] types)` | ...of every kind. |
| 580 | 8 | `static boolean roomAt(CityMap.District d, BuildingVisual.Type type, double share)` | Whether district d has room for one more of a type at a road share: CityMap.hasRoomFor()'s rule (buildingRoom()), at the share given rather than the map's own. |
| 589 | 4 | `static int mineId(Game g)` |  |

### 2. NOTHING PLACED MOVES (lines 594-672)

| line | len | member | says |
|---:|---:|---|---|
| 598 | 74 | `static void nothingMoves(Game g)` |  |

### THE COPIES: JERUS'S CITY ON A SQUARE OF DISTRICTS, AT HIS DENSITY (lines 673-791)

| line | len | member | says |
|---:|---:|---|---|
| 678 | 62 | **type** `static final class Squares` | His city x 1 and its copies, each on the design's square city at the dry place, its mines no more than its sites. |
| 690 | 49 | `Squares()` _(in MapCheck.Squares)_ |  |
| 742 | 3 | `static int x1Side(BuildingVisual.Type[] types)` | The side of Jerus's city x 1 on the design's square city, in districts: its ground over a district's (Squares). |
| 747 | 3 | `static long[] dryPlace(World w)` | The world cell nearest the founding site's whose middle 3 x 3 districts are DRY_PLACE dry at a sample a tile: {x, y, rings out}. |
| 759 | 18 | `static long[] dryPlace(World w, int side)` | ...and (0.7.99, batch W1), with `side` over 0, whose square for Jerus's city x 1 - `side` districts round its middle, as CityMap.square() lays it - holds an iron field's centre, so his mines have sites and his railway... |
| 779 | 12 | `static boolean holdsIron(World w, long x, long y, int side)` | Whether the square of `side` districts CityMap.square() lays round plot (x, y) holds an iron field's centre. |

### 3. THE PAINTER DRAWS THE PLAN (0.7.88, batch RD2; the mockup's rules on (lines 792-855)

| line | len | member | says |
|---:|---:|---|---|
| 797 | 58 | `static void painterRules(CityMap m)` |  |

### 6. THE CITY DRAWN AS WHAT IT HAS (0.7.64, batch L2) (lines 856-957)

| line | len | member | says |
|---:|---:|---|---|
| 873 | 84 | `static void drawnAsItIs(CityMap dense)` | Jerus, 2026-10-07: "the generation should only put what the city has, not more not less". |

### 8. THE NETWORK ON THE DRAWN RASTER (0.7.88, batch RD2; N3's on its (lines 958-1820)

| line | len | member | says |
|---:|---:|---|---|
| 975 | 308 | **type** `static final class Raster` | A map painted whole over a box of tiles and put together: what each plot is, every building's box - what holds the network's rules. |
| 988 | 53 | `Raster(CityMap map, long tx0, long ty0, int tw, int th)` _(in MapCheck.Raster)_ |  |
| 1043 | 4 | `static Raster of(CityMap map)` _(in MapCheck.Raster)_ | Every tile of every district of a map, and a tile round them. |
| 1049 | 1 | `boolean isRoad(int g)` _(in MapCheck.Raster)_ | A road plot: a street (a track among them, the track's level crossings among them), or a highway's. |
| 1052 | 1 | `boolean isStreet(int g)` _(in MapCheck.Raster)_ | A street plot: a road plot with a street's role - not a highway's own. |
| 1055 | 1 | `boolean isHighway(int g)` _(in MapCheck.Raster)_ | A highway's own plot (its runs'). |
| 1057 | 1 | `boolean isRail(int g)` _(in MapCheck.Raster)_ |  |
| 1060 | 1 | `boolean isRunHighway(int g)` _(in MapCheck.Raster)_ | A plot of the city's highways as its runs lay it (0.7.89), under a building or not - the railway bridging it among them. |
| 1063 | 1 | `boolean isRunRail(int g)` _(in MapCheck.Raster)_ | ...of its railway's track, under a yard or a mine too, or bridging a highway. |
| 1065 | 5 | `long streetPlots()` _(in MapCheck.Raster)_ |  |
| 1071 | 8 | `int beside(int g, java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ |  |
| 1081 | 19 | `int[] pieces(java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ | The pieces plots of a kind make, four ways joined: each plot's piece number (0 for none), [0] = how many pieces. |
| 1111 | 37 | `int[] network()` _(in MapCheck.Raster)_ | The road's pieces and those the city's own ground joins to the largest: {pieces, plots of the largest, pieces the ground joins to it} - from the largest piece, four ways over owned ground that is not the sea or a mine... |
| 1149 | 5 | `long count(java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ |  |
| 1156 | 5 | `long alone()` _(in MapCheck.Raster)_ | Road plots with no road beside them, four ways: a stray. |
| 1163 | 9 | `boolean crossing(int g)` _(in MapCheck.Raster)_ | A + junction of streets: a street plot with street on its four sides and none on its four corners (a square's middle, street on its corners too, is not one; nor a street beneath a highway, which passes under it). |
| 1174 | 16 | `long[] crossings(int apart)` _(in MapCheck.Raster)_ | {crossings, pairs of them nearer than `apart` either way}. |
| 1192 | 18 | `int[] reach(int most)` _(in MapCheck.Raster)_ | Each plot's distance from a street, across corners (a street 0), to `most` + 1. |
| 1212 | 5 | `static int boxReach(int[] dist, int w, int[] b)` _(in MapCheck.Raster)_ | A box's nearest plot's distance. |
| 1219 | 3 | `CityMap.District districtAt(int g)` _(in MapCheck.Raster)_ | The district a plot of the box is in. |
| 1229 | 24 | `int[] asPlanned()` _(in MapCheck.Raster)_ | Every plot the plans of the districts in the box surface (Drawn's codes, its frame's east column and south row among them) against the painted plot: {plots, those not painted as that street - its kind and its width}. |
| 1255 | 27 | `int[] bridges()` _(in MapCheck.Raster)_ | The bridges: {runs longer than their line may cross - an arterial's or a boulevard's ARTERIAL_BRIDGE, a street's STREET_BRIDGE, a highway's MAX_BRIDGE - roads on the sea, bridge plots}. |
| 1290 | 46 | `static void networkOn(CityMap m, long[] model, String what)` | The network's rules on a map's drawn raster (0.7.88): one network but where the ground parts it, no road alone, + junctions apart, every building within reach, the streets' surface the model's road kind by kind, every... |
| 1337 | 30 | `static void network(Squares sq, Game city, String[] saves) throws Exception` |  |
| 1369 | 23 | `static Game loadCopy(Path src) throws Exception` | A save's autosave copied into a temporary folder and loaded (its map drawn from its sidecar or canonically): saves named on the command line, never written. |
| 1401 | 30 | `static CityMap riverCity(Squares sq, int kind)` | The river fixture (0.7.77, batch N5): Jerus's city x 1 on the design's square city sited at the default world's founding river - its point nearest the founding site - with its streets all of one kind: his Paved Roads ... |
| 1441 | 44 | `static void riverCrossed(Squares sq)` | A river parts gravel streets no more than paved ones (0.7.77; Jerus, 2026-10-08: "gravel road bridge rivers sure"): the river fixture all gravel against its paved twin - the road pieces with a plot within a tile of th... |
| 1494 | 32 | `static void highways(Raster net, String what)` | The city's highways on the drawn raster (0.7.89, batch RD3; spec 2.7): drawn plot for plot as its runs lay them, and with what they could not lay the model's Elevated Highways; one network; no building plot touching a... |
| 1540 | 47 | `static int marksReachTheView(CityMap m, long tx0, long ty0, int tw, int th, String what, boolean bands)` | The runs' marks reach the picture a screen draws (0.7.98, batch O14): a screen rasters a tile from the view's kept copy of its inputs (MapTiles.painted(), paintedInput()), and until then that copy held none of the run... |
| 1602 | 47 | `static void corridors(Squares sq)` | The highways on corridors on the game's own ground (0.7.89; spec 2.7): each stage of spec 2.7's table on a square city of highways alone at the dry place, laid at once - its plots each laid or counted, one network, st... |
| 1651 | 55 | **type** `static final class Box` | The runs' plots over a map's districts and a plot round them, put together: one network, its junctions. |
| 1656 | 5 | `Box(long x0, long y0, int w, int h, CityRuns r)` _(in MapCheck.Box)_ |  |
| 1662 | 1 | `static Box of(CityMap m)` _(in MapCheck.Box)_ |  |
| 1664 | 4 | `static Box of(CityMap m, CityRuns r)` _(in MapCheck.Box)_ |  |
| 1669 | 1 | `boolean hw(int g)` _(in MapCheck.Box)_ |  |
| 1672 | 18 | `int pieces()` _(in MapCheck.Box)_ | The highways' pieces, four ways joined. |
| 1692 | 13 | `long junctions()` _(in MapCheck.Box)_ | Highway plots with highways on three or four sides: where they meet. |
| 1708 | 17 | `static int bendsStopped(CityMap m, CityRuns.Net n)` | The bends of a net that are not where its way was stopped: a stretch off its arm's heading begun where the way on along the heading before ran LOOK plots clear, or one back onto it where the way home did not run TURN_... |
| 1727 | 15 | `static void rail(Raster net, String what)` | The railway on the drawn raster (0.7.89; spec 2.8): one network, every plot of the model's track drawn - under its yards and mines too - or counted as what the runs could not lay, its streets crossing it level. |
| 1747 | 25 | `static CityMap yardCity(Squares sq)` | The x 1 copy with rail yards and a freight line added (0.7.72's fixture, restored in 0.7.89): its buildings, then its mines on their sites, then its railway - so the railway starts at its mine nearest the founding sit... |
| 1780 | 40 | `static void yardsNearMines(CityMap m, Raster net)` | Every rail yard on its track, in the cell along it nearest a mine that had room (spec 2.8: "laid as runs from the rail terminals, which sit in estate cells nearest the mines"; 0.7.72's rule, restored): the track runs ... |

### 4. THE SIDECAR (lines 1821-1910)

| line | len | member | says |
|---:|---:|---|---|
| 1825 | 85 | `static void sidecar(Game g, Path root, Squares sq) throws Exception` |  |

### 5. THE COST FOLLOWS THE SCREEN (lines 1911-2057)

| line | len | member | says |
|---:|---:|---|---|
| 1936 | 9 | `static boolean cpuTimed()` |  |
| 1947 | 3 | `static long monthClock()` | The clock a month's bound reads, in ns: the main thread's CPU, or the wall clock where CPU_TIMED is false. |
| 1951 | 106 | `static void cost(Squares sq)` |  |

### 7. THE VIEW'S PURE HALF (0.7.61, batch J4) (lines 2058-2747)

| line | len | member | says |
|---:|---:|---|---|
| 2068 | 8 | `static void viewHalf(Game city, Squares sq, Path root) throws Exception` |  |
| 2078 | 72 | `static void frames()` | MapFrame: the transforms, a notch of the wheel at the pointer, the clamps, the levels and what a screen asks for. |
| 2152 | 121 | `static void picks(Game g)` | LandMap: what a click and a hover pick on the played city agrees with the model's own grid (CityLand.holdingOf()) and GridOffers' sides and places, and the outlines with the picks (0.7.67; the lanes' owns() and sideLa... |
| 2286 | 228 | `static void overlay(Game g)` | The overlay on the block grid (0.7.69, batch M5; spec-grid 2.4), what MapView draws by: the city's edge as maximal runs with its ground on their right, drawn crisp - whole pixels, a pixel thick, just inside the ground... |
| 2519 | 3 | `static char dir(double x0, double y0, double x1, double y1)` | A run's way: E, S, W or N. |
| 2524 | 3 | `static boolean touch(double[] a, double[] b)` | Whether two boxes of pixels {x, y, w, h} share a pixel or touch at a corner or an edge. |
| 2529 | 13 | `static int inQuad(double[] q, double x, double y)` | Whether a point is inside a convex quadrilateral (corners in order): 1 inside, 0 within a hair of an edge, -1 outside. |
| 2544 | 77 | `static void draft(Path root) throws Exception` | Game.mapDraft()/adoptMap(): the map drawn on another thread is the map drawn here, a month between is caught up, and a draft for other land is not kept. |
| 2626 | 120 | `static void tiles(Squares sq)` | MapTiles: a tile's stamp holds while nothing moves, one more building restamps only the tiles it changes, the view's per-tile path follows the screen, and every cache fits the budget. |

### 9. AT SEA AND ON THE SHORE (0.7.97, batch O13) (lines 2748-3207)

| line | len | member | says |
|---:|---:|---|---|
| 2756 | 12 | `static CityMap coastal(long seed, long sx, long sy, BuildingVisual.Type[] types, int side, long[] counts)` | A city owning `side` districts a side about (sx, sy), every plot of them, its map drawn canonically on the ground measured. |
| 2776 | 12 | `static long[] shoreCounts(BuildingVisual.Type[] types)` | The section's town: Jerus's mix at a tenth, one of each of the refinery's units, two of each terminal and one of each tank farm. |
| 2789 | 254 | `static void atSea(BuildingVisual.Type[] types, long seed)` |  |
| 3060 | 12 | `static List<BoatSchedule.Route> protoLanes(long cx, long cy)` | BoatProto's 20,000 lanes about (cx, cy): berths at hashed places along its coast's span at 10B - sqrt(10B / 5,000) x 33 plots, its "city's span in plots (rough)" - every other one liquid bulk's, the rest dry bulk's. |
| 3074 | 5 | `static int tileGround(World w, long x, long y)` | The world's ground at a plot as the painter reads it: its tile's (World.tileTerrain()), what the shore's search and the routes read. |
| 3081 | 19 | `static boolean connected(java.util.Set<Integer> cells)` | Whether a set of cells (ci + cj x CELLS_A_SIDE) is one piece, eight ways: a corner touching counts, as DistrictPlan.campusNext() opens them. |
| 3102 | 5 | `static String worksWords(CityShore shore, BuildingVisual.Type[] types)` | The shore's works by type, in words. |
| 3109 | 17 | `static Ports tradeAtSea(double liquid, double dryBulk, double general, double boxes)` | A month billed with these tonnes of each kind shipped out, all of them by sea: each on the first good of its kind that is not crude. |
| 3134 | 73 | `static void oilAtSea()` | The game's own platform, its wells and its pipe on the map (WellCheck's sea town): Game.mapAtSea() puts the jacket in the middle of its slotted sites and its wells on them, the pipe from the jacket to the founding sit... |

### 10. THE MAP ON EARTH (0.7.119, batch E5) (lines 3208-3622)

| line | len | member | says |
|---:|---:|---|---|
| 3234 | 106 | `static void onEarth(BuildingVisual.Type[] types) throws Exception` | Section 10. |
| 3348 | 73 | `static void farWindow(String name, World w, EarthData data)` | One window drawn as a far node at WINDOW_STRIDE and at CLOSE_STRIDES: closer, the region but for its rivers stroked wider; at the window's stride every river stroked at least FAR_RIVER_HALF_PX a side, every small lake... |
| 3428 | 60 | **type** `static final class Water` | The rivers and small lakes under a far node, as the check reads them from the data itself: each run the node would stroke (its median width at least FAR_RIVER_SHARE of a pixel), its segments projected exactly, and eac... |
| 3434 | 29 | `Water(EarthTerrain e, EarthData data, long x0, long y0, int stride)` _(in MapCheck.Water)_ |  |
| 3465 | 12 | `boolean inStroke(int i, int j, boolean least)` _(in MapCheck.Water)_ | Whether pixel (i, j)'s centre lies within a river's stroke: its least half-width alone (`least`), or its own. |
| 3479 | 8 | `boolean inCore(int i, int j)` _(in MapCheck.Water)_ | Whether pixel (i, j)'s centre lies in a small lake's sure part: within its radius x the root of 1 - BLOB_FRAY / 2, which its blob holds whatever its noise. |
| 3496 | 36 | `static void seam(String where, World w, int stride, EarthData data, boolean water)` | A node holding the 180th meridian (its middle on the spot, which is on it) and its neighbours moved SEAM_SHIFT_PX east and west: on the half each shares with it, pixel for pixel the same; each side of the line's land ... |
| 3534 | 88 | `static void fullFile(int widest, EarthTerrainCheck.Spot rot) throws Exception` | Section 10 on the full file, when it is named (-Dcbs.earth): the longest segment, every stride's cost, the whole world, the small lakes' area and Chukotka's seam. |

### 11. THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) (lines 3623-4078)

| line | len | member | says |
|---:|---:|---|---|
| 3636 | 357 | `static void pictures(Game city, Squares sq)` |  |
| 3995 | 5 | `static Map<CityMap.District, DistrictPicture> picturesOf(CityMap m)` | A district's pictures, every one asked for: each district of the map to its picture (null where it has none). |
| 4002 | 5 | `static long[] countsOf(CityMap.District d, int types)` | A district's counts by type id. |
| 4009 | 5 | `static String codeName(int c)` | A picture's code's name, for the prints. |
| 4023 | 26 | `static byte[] pictureSource(CityMap m, CityMap.District d, int[] boxPlots)` | The plots a district's picture is drawn from, read here from its plan as the painter reads it (CityMap.Drawn: its streets, its boxes by tile) and its own plan's inputs (the runs over its frame, the mines on their site... |
| 4050 | 5 | `private static void fill(byte[] src, int x0, int y0, int w, int h, byte code, int[] counted)` |  |
| 4057 | 15 | `static boolean[] runPixelsOf(CityMap m, long x0, long y0, long plots)` | Which pixels of a far node at (x0, y0), `plots` a pixel, hold a plot of the city's highways or track: every plot of every stretch walked, each 45-degree step's corner with it (CityRuns.onStretch()). |
| 4073 | 5 | `private static void markPixel(boolean[] out, long x, long y, long x0, long y0, long plots)` |  |

### 12. THE MINIMAP (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3) (lines 4079-4323)

| line | len | member | says |
|---:|---:|---|---|
| 4089 | 196 | `static void miniMap(Game city, Squares sq) throws Exception` |  |
| 4291 | 32 | `static MiniMapFrame.Box builtUpByLeaves(CityMap m)` | The built-up ground found another way, for the check: every leaf of the grid cut to each district with buildings it lies in, their bounds; a built district with none owned its whole square. |

### 13. THE GLOBE ON SCREEN (0.7.135, batch E9) (lines 4324-4695)

| line | len | member | says |
|---:|---:|---|---|
| 4337 | 318 | `static void globeOnScreen(Squares sq) throws Exception` |  |
| 4657 | 4 | `static byte groundClass(String words)` | The class a legend's ground words name. |
| 4663 | 15 | `static double foundingParting(double lat, double lon, double km)` | How far the founding globe's two drawings part at a scale, in pixels at worst over its canvas's corners and edges: the globe through GlobeFrame against the ground drawn round the view's centre (Lambert's plane there, ... |
| 4680 | 15 | `static CityMap earthX1(Squares sq) throws Exception` | Jerus's city x 1 on the real Earth at Rotterdam, EarthCheck's fixture (section 10's window): null when the fixture is not beside the classes. |

