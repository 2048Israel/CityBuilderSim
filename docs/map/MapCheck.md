# MapCheck.java - 1,525 lines · 35 methods · 22 constants · harnesses

`ham/citybuildersim/MapCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter keeps the mockup's rules and paints the same pixels from the same inputs, the sidecar comes back byte for byte, and a screen costs what the screen holds, never what the city does - at Jerus's size and at five and ten billion people.
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
>      comes off the outermost district holding the type; and (0.7.64) at
>      every DRAWN_EVERY-th month, every tile painted, the map draws exactly
>      what the city has - each type's buildings as many as the model's, a
>      mine once on its site, every road plot of every kind - none without
>      a plot;
>   2. nothing placed moves: one more building changes one district's count
>      by one, inner types in the first district with room, outer types in
>      the last - and one tile of the map by one; one fewer takes its own
>      kind off one tile and moves nothing else but a smaller kind into the
>      room it frees (since 0.7.64 the deal keeps a district on its own
>      tiles; J3b spread it over the tiles about it);
>   3. the painter keeps the mockup's rules on Jerus's city: shared ports
>      agree and no road meets a border away from a port, every tile lays
>      exactly the road plots of each kind dealt to it and no bridge longer
>      than its kind allows nor over the sea, the same inputs paint the same
>      pixels (and the far view's blocks), and one more model building in a
>      tile with room for it moves none of those placed before it (0.7.64; a
>      median of none of the others before, at J3b's 30% built) and never
>      draws fewer of anything;
>   4. the sidecar round-trips byte for byte through a save and a load, the
>      next month plays to the same map, a stale or missing sidecar draws the
>      map again canonically, a city with no map saves none; and at five and
>      ten billion it is written and read back the same;
>   5. the cost follows the screen: a 1,389 x 868 L0 screen (SCREEN_TILES)
>      paints and rasters in no more than SCREEN_MS (the design's 80 ms,
>      derived from 38 at its measured 0.19 ms a tile) at Jerus's city x 1,
>      x 9,814 (5B), x 10,000 and x 19,629 (10B), each within SCREEN_RATIO of
>      the 5B copy's, the first whose screen is all city (until 0.7.64, of
>      x 1's: section 5's note); and a month's change at 5B and 10B in no
>      more than RECONCILE_MS (the design's 5 ms; measured 0.76);
>   6. the city is drawn as what it has (0.7.64, batch L2; J3b's homes from
>      people and workplaces from jobs gone): a new default city as founded,
>      a month on and a year on draws its own buildings and nothing else -
>      at its founding the bank and bare ground, no road, no highway; on the
>      dense screen every building dealt is drawn, on its own type's land
>      but for the few no free box of the tile could hold (SHRUNK_MOST),
>      and the screen is built as full as its districts are used;
>   7. the view's pure half (J4: MapFrame, LandMap, MapTiles, Game's draft):
>      a screen point and its plot go back and forth, a notch of the wheel
>      zooms by ZOOM_STEP with the plot under the pointer kept, the zoom
> ... (9 more lines in the source)

**Uses:** [TilePainter](TilePainter.md) (68), [CityMap](CityMap.md) (63), [World](World.md) (53), [MapFrame](MapFrame.md) (42), [BuildingVisual](BuildingVisual.md) (36), [Game](Game.md) (30), [LongPlaytest](LongPlaytest.md) (29), [LandMap](LandMap.md) (23), [MapTiles](MapTiles.md) (22), [TileRaster](TileRaster.md) (14), [Resource](Resource.md) (13), [CityLand](CityLand.md) (12), [GameFiles](GameFiles.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (4), [LandParcel](LandParcel.md) (4), [TreasuryFund](TreasuryFund.md) (2), [Founding](Founding.md) (2), [MiningCheck](MiningCheck.md) (1), [BuildingCatalog](BuildingCatalog.md) (1), [DataSave](DataSave.md) (1), [LandMarket](LandMarket.md) (1), [Deposit](Deposit.md) (1), [BuildingType](BuildingType.md) (1)

## Sections

| line | section |
|---:|---|
| 186 | 1. A CITY'S MAP ADDS UP |
| 304 | · WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64) |
| 392 | 2. NOTHING PLACED MOVES |
| 468 | THE COPIES: JERUS'S CITY ON A SQUARE OF DISTRICTS, AT HIS DENSITY |
| 538 | 3. THE PAINTER'S RULES |
| 838 | 6. THE CITY DRAWN AS WHAT IT HAS (0.7.64, batch L2) |
| 942 | 4. THE SIDECAR |
| 1015 | 5. THE COST FOLLOWS THE SCREEN |
| 1108 | 7. THE VIEW'S PURE HALF (0.7.61, batch J4) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 95 | `MapCheck.MONTHS` | `120` | Months the city of section 1 is played: 120 (the design's). |
| 98 | `MapCheck.IRON_AT` | `MONTHS / 3` | The month the played city is handed a whole iron field and orders a mine on it, if it has none (0.7.64): a third of the way. |
| 105 | `MapCheck.JERUS_COUNTS` | `{ 1913, 200, 1212, 0, 65, 4, 1701, 4, 0, 2, 5, 48, 11, 81, 21, 6079, 0, 170, ...` | Jerus's city at month 1,851 (his autosave, 509,455 people on 89.63 km2 of dry ground): every building type's count, by id - 14,214 buildings, the design's own fixture for the map's sizes. |
| 110 | `MapCheck.JERUS_PEOPLE` | `509_455` | ...his people. |
| 113 | `MapCheck.JERUS_KM2` | `89.63` | ...and his dry ground, in km2. |
| 116 | `MapCheck.JERUS_FILL` | `0.92` | His buildings' footprint over his dry ground: 91.8% (the design's measure), so the design's square city holds this share of a district - his density. |
| 119 | `MapCheck.TIMES` | `{ 1, 9_814, 10_000, 19_629 }` | The copies measured: his city x 1, x 9,814 (5 billion people), x 10,000 (the design's) and x 19,629 (10 billion). |
| 122 | `MapCheck.DENSE` | `2` | The copy whose screen is all city, at his density, that section 3 paints: x 10,000. |
| 125 | `MapCheck.SCREEN_ACROSS` | `18, SCREEN_DOWN = 11, SCREEN_TILES = SCREEN_ACROSS * SCREEN_DOWN` | The screen: 18 x 11 tiles, 198 - a 1,389 x 868 view at L0's least 3.2 px a plot is 13.6 x 8.5 tiles, the design's "about 200 with a margin". |
| 128 | `MapCheck.SCREEN_MS` | `80` | The design's bound on that screen's paint and raster, in ms (derived: 38 at its measured 0.19 ms a tile). |
| 131 | `MapCheck.SCREEN_RATIO` | `1.5` | The most any copy's screen may take against the 5B copy's, the first whose screen is all city (the design's; against the city x 1's until 0.7.64 - section 5's note). |
| 134 | `MapCheck.RECONCILE_MS` | `5` | The design's bound on a month's change at 10B, in ms (measured 0.76). |
| 137 | `MapCheck.PX` | `4` | Pixels a plot the screen is rastered at: 4, L0's image (spec-land 2.6). |
| 140 | `MapCheck.WARM_ROUNDS` | `3, TIMED_ROUNDS = 5` | Rounds of the screen run over every copy before any is timed, and rounds timed, each copy in turn: the least of each copy's timed rounds is its time. |
| 143 | `MapCheck.DRY_PLACE` | `0.97` | How dry the place the copies stand on must be, at a sample a tile over 3 x 3 districts: 97% - every screen tile can be built on, the painter's worst case. |
| 146 | `MapCheck.DRAWN_EVERY` | `30` | The months of section 1's city at which every tile is painted and what is drawn counted against the model: every 30th, four of its 120 (0.7.64). |
| 157 | `MapCheck.SHRUNK_MOST` | `0.10` | The most of the dense screen's buildings drawn smaller than their own land, because no free box of their tile held it: a tenth (star, 0.7.64). |
| 181 | `MapCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } ...` |  |
| 843 | `MapCheck.NEW_CITY_MONTHS` | `{ 0, 1, 12 }` | The months a new default city is drawn at: as founded, a month on and a year on (Jerus: "a brand new city shows that it has a few houses and a shop when it doesnt"). |
| 1113 | `MapCheck.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 1116 | `MapCheck.SCREEN_POINTS` | `{ { 0, 0 }, { 300, 200 }, { 1344, 805 }, { 17.25, 640.5 }, { 1000, 3 } }` | Points across the screen the transforms are tried at. |
| 1400 | `MapCheck.DRAFT_MONTHS` | `24` | Months the draft's town is played before its map is drawn: 24. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 86 | `static int fails` |  |
| 87 | `static PrintStream out` |  |
| 311 | `long[] buildings` | Buildings by type id: a mine or well on a site once, however many tiles its site spans. |
| 313 | `final long[] roads` | Road plots laid by kind [0, gravel, paved, highway]. |
| 315 | `long dropped, shrunk, roadShort, tiles` | Buildings dealt and drawn on no plot; drawn smaller than their own land; road plots dealt and not laid; tiles painted. |
| 385 | `static GameFiles cityFiles` | The played city's save folder. |
| 474 | `final BuildingVisual.Type[] types` |  |
| 475 | `final long seed` |  |
| 476 | `final long x, y` |  |
| 477 | `final CityMap[] maps` |  |
| 478 | `final long[][] counts` |  |
| 479 | `final double[] buildMs` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 84 | 1442 | **type** `public class MapCheck` | The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter keeps the mockup's rules and paints the same pixels from the same inputs, the sidecar comes... |
| 89 | 4 | `static void check(String label, boolean ok)` |  |
| 159 | 21 | `public static void main(String[] args) throws Exception` |  |

### 1. A CITY'S MAP ADDS UP (lines 186-303)

| line | len | member | says |
|---:|---:|---|---|
| 190 | 113 | `static Game playedCity(Path root) throws Exception` |  |

### WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64) (lines 304-391)

| line | len | member | says |
|---:|---:|---|---|
| 309 | 24 | **type** `static final class Drawn` | What the painted map draws over every tile of a map's districts. |
| 317 | 1 | `long total()` _(in MapCheck.Drawn)_ |  |
| 318 | 1 | `int kinds()` _(in MapCheck.Drawn)_ |  |
| 319 | 1 | `long roadPlots()` _(in MapCheck.Drawn)_ |  |
| 322 | 10 | `boolean sameAs(long[] model, BuildingVisual.Type[] types)` _(in MapCheck.Drawn)_ | Whether it is exactly the model's: every drawn type as many as the model has (a kind it has none of not drawn), every road kind's plots, none without a plot. |
| 335 | 26 | `static Drawn drawn(CityMap m)` | Paints every tile of every district on a map and counts what is drawn. |
| 363 | 5 | `static long modelBuildings(long[] model, BuildingVisual.Type[] types)` | The model's buildings the map draws: every type but the roads. |
| 370 | 7 | `static long roadPlotsOf(long[] model, BuildingVisual.Type[] types, int kind)` | The model's road plots of a kind: each road type's count x its own plots (BuildingVisual.cells()). |
| 379 | 4 | `static long modelRoadPlots(long[] model, BuildingVisual.Type[] types)` | ...of every kind. |
| 387 | 4 | `static int mineId(Game g)` |  |

### 2. NOTHING PLACED MOVES (lines 392-467)

| line | len | member | says |
|---:|---:|---|---|
| 396 | 71 | `static void nothingMoves(Game g)` |  |

### THE COPIES: JERUS'S CITY ON A SQUARE OF DISTRICTS, AT HIS DENSITY (lines 468-537)

| line | len | member | says |
|---:|---:|---|---|
| 473 | 44 | **type** `static final class Squares` | His city x 1 and its copies, each on the design's square city at the dry place, its mines no more than its sites. |
| 481 | 35 | `Squares()` _(in MapCheck.Squares)_ |  |
| 519 | 18 | `static long[] dryPlace(World w)` | The world cell nearest the founding site's whose middle 3 x 3 districts are DRY_PLACE dry at a sample a tile: {x, y, rings out}. |

### 3. THE PAINTER'S RULES (lines 538-837)

| line | len | member | says |
|---:|---:|---|---|
| 542 | 107 | `static void painterRules(CityMap m)` |  |
| 657 | 62 | `static void bridges(CityMap m, long tx0, long ty0)` | Bridges, where there is fresh water to cross: the default world's founding river, every tile it runs through painted owned with the dense screen's middle tile's buildings and its whole road budget paved, no more than ... |
| 721 | 1 | `static boolean road(TilePainter.Painted p, int i)` | A road plot. |
| 723 | 5 | `static int[] portsOf(long seed, long ax, long ay, boolean vertical)` |  |
| 729 | 4 | `static boolean has(int[] a, int v)` |  |
| 742 | 65 | `static void moves(CityMap m, long tx0, long ty0)` | The city grown by one building in a tile (0.7.64: one more of each model type the tile holds, painted again, where the tile has room for its whole plots as the deal reckons room - its free plots less what was dealt it... |
| 809 | 5 | `static long[] drawnByType(TilePainter.Painted p, int types)` | A painted tile's buildings by type id. |
| 816 | 5 | `static int dryOwned(TilePainter.Input in)` | A tile's owned dry plots. |
| 823 | 8 | `static java.util.Map<Long, Long> positions(TilePainter.Painted p)` | Each building's place, keyed by its type and number. |
| 832 | 5 | `static int movedOf(java.util.Map<Long, Long> before, java.util.Map<Long, Long> after)` |  |

### 6. THE CITY DRAWN AS WHAT IT HAS (0.7.64, batch L2) (lines 838-941)

| line | len | member | says |
|---:|---:|---|---|
| 856 | 85 | `static void drawnAsItIs(CityMap dense)` | Jerus, 2026-10-07: "the generation should only put what the city has, not more not less". |

### 4. THE SIDECAR (lines 942-1014)

| line | len | member | says |
|---:|---:|---|---|
| 946 | 68 | `static void sidecar(Game g, Path root, Squares sq) throws Exception` |  |

### 5. THE COST FOLLOWS THE SCREEN (lines 1015-1107)

| line | len | member | says |
|---:|---:|---|---|
| 1019 | 88 | `static void cost(Squares sq)` |  |

### 7. THE VIEW'S PURE HALF (0.7.61, batch J4) (lines 1108-1525)

| line | len | member | says |
|---:|---:|---|---|
| 1118 | 7 | `static void viewHalf(Game city, Squares sq, Path root) throws Exception` |  |
| 1127 | 72 | `static void frames()` | MapFrame: the transforms, a notch of the wheel at the pointer, the clamps, the levels and what a screen asks for. |
| 1201 | 103 | `static void picks(Game g)` | LandMap: what a click and a hover pick on the played city agrees with the model's own owns() and sideLane(), and the outlines with the picks. |
| 1306 | 13 | `static int inQuad(double[] q, double x, double y)` | Whether a point is inside a convex quadrilateral (corners in order): 1 inside, 0 within a hair of an edge, -1 outside. |
| 1321 | 77 | `static void draft(Path root) throws Exception` | Game.mapDraft()/adoptMap(): the map drawn on another thread is the map drawn here, a month between is caught up, and a draft for other land is not kept. |
| 1403 | 115 | `static void tiles(Squares sq)` | MapTiles: a tile's stamp holds while nothing moves, one more building restamps only the tiles it changes, the view's per-tile path follows the screen, and every cache fits the budget. |
| 1520 | 5 | `static int[] tilesModel(MapTiles tiles, long tx, long ty)` | A tile's model counts, as the map deals them now. |

