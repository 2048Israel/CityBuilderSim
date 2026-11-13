# CityShore.java - 655 lines · 42 methods · 17 constants · model

`ham/citybuildersim/CityShore.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> The city's works on its shore as the map draws them: each sea terminal's box on owned dry ground at the water with its quay run straight out over owned sea, and each tank farm's box at the water, laid city-wide in the order the city builds them - never moved, the newest of a type taken first - what CityMap fixes in its districts' plans, the tile painter draws and the boats come alongside; kept in the map's sidecar.
> 
> WHY THIS EXISTS (0.7.97, batch O13; the project's spec-roads-and-ports.md
> 2.8: "Ports and tank farms: estate cells on the shore, the berths over owned
> sea"; docs/studies/spec-oil.md 2.12: "terminals stand on owned coast with a
> quay"). Until 0.7.96 a sea terminal was one more industry building in its
> district's plan, wherever that district's estate cells had room: the
> playtest's 23 General Cargo Terminals stood in five districts, inland as
> often as not, and the boats ran from points with no water under them. A
> district's plan does not know the city's shore, and a boat's route starts
> at its quay, which a frame needs whether that district is planned or not.
> So the works are laid as the railway's yards are (CityRuns, YARDS):
> city-wide, before any plan, kept, and every plan drawn round them.
> 
> THE RULE (star O13-3). A work goes on the city's shore nearest the founding
> site: district by district in the map's order (CityMap.DISTRICT_ORDER),
> each district's tiles nearest the site first, each tile's plots in rows.
> A plot of owned dry ground with owned salt water beside it is a shore plot;
> the work's box stands inland of it with the plot at the middle of its sea
> side, either way round, inside one cell's interior (as every box of a plan
> and every yard is: off the arterials, on one tile). Its box is owned dry
> ground on no run of the city's (highway, track or yard), on no mined site,
> a plot clear of every other work, and no highway touches it, corners
> included (H5). A terminal's quay runs from that plot's side QUAY_PLOTS of
> its cargo straight out, every plot owned salt water and no other work's,
> and the water just past its end opening to the sea (SeaRoutes.opensToSea()).
> Of the spots a district holds, one within TilePainter.REACH of its cell's
> ring - where the cell's arterials run when the plan opens it - comes first
> (star O13-4); the first district with a spot has the work. A city with no
> spot, or past SEARCH_TILES_MOST tiles looked at, counts the work short;
> its district's plan draws it among its industry, as a yard with no place
> is drawn, and the search waits for the ground to change.
> 
> NEVER MOVED (H4, as the runs). The works are laid in the order the model's
> counts grow, kept in the sidecar (CityMap FORMAT 6); one fewer of a type
> takes its newest. A FORMAT 5 sidecar, which has none, has them laid from its
> counts as it is read.
> 
> THE HARBOUR (MF1, 2026-10-28; item 8 of docs/queue/MF1.md; Jerus: "i dont think
> real life is like that", the berths "gathered into one or two harbours at the best
> shore ... the rest of the coast stays beach"). The rule above gave every terminal
> the shore nearest the founding site, so a city of many stood on its whole coast,
> each on a pier of its own with a lane of its own. Now the first work on a coast
> opens a HARBOUR at the best shore its search weighs (CityMap.openHarbour(): near the
> founding site, sheltered, deep), and every later one stands within HARBOUR_REACH
> of that first work's shore plot, nearest it first, on the harbour's common quay: a
> quay HARBOUR_QUAY_PLOTS out, berths side by side. A second harbour opens only when
> the first has no owned shore left in its reach, HARBOUR_APART from the first, and
> there are at most HARBOURS_MOST on a coast (a landmass); a work with no place is
> counted short, as before. A harbour is derived, never saved: the first work laid
> within reach of nothing is a harbour's first (harbours()); works laid before MF1,
> on piers of their own, stay where they are.
> 
> THE INDEX (SL1, 2026-10-28). blocks() is asked of every plot a search, a lay
> or a plan reads (CityMap's RunGround.at()): a shoreSpot() reads a 96 x 96
> window for each tile it looks at, and each plot asked every work on the
> shore - 300 terminals took a canonical draw 183 s (14.6 s at 100, 1.2 s at
> 10), a late city's 2,200 would take hours. So the works are kept by cell too:
> a square of 8 plots a side (CELL_SHIFT) holds each work whose box with its
> CLEAR verge, or whose quay, touches the cell, in the order laid, and blocks()
> ... (7 more lines in the source)

**Uses:** [World](World.md) (4), [BoatSchedule](BoatSchedule.md) (3), [BuildingVisual](BuildingVisual.md) (1), [Ports](Ports.md) (1)

**Used by (18):** [BoatSchedule](BoatSchedule.md), [CityMap](CityMap.md), [HarbourRenders](HarbourRenders.md), [HarbourYard](HarbourYard.md), [InfrastructureScreen](InfrastructureScreen.md), [IslandRailCheck](IslandRailCheck.md), [LandmassCheck](LandmassCheck.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [PortCheck](PortCheck.md), [QuayCrane](QuayCrane.md), [QuayCraneCheck](QuayCraneCheck.md), [QuayRail](QuayRail.md), [QuayRailCheck](QuayRailCheck.md), [RailDrawCheck](RailDrawCheck.md), [SeaRoutes](SeaRoutes.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 98 | · the harbour (MF1, 2026-10-28) |
| 311 | · the index (SL1) |
| 459 | · the harbour's quay and its boats alongside (HB1, 2026-11-08) |
| 617 | · the sidecar |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 90 | `CityShore.QUAY_PLOTS` | `{ 10, 10, 14, 6 }` | A terminal's quay out over the sea, in plots, by Ports.Cargo's ordinal: the research's quay a berth (4.1) at its middle, in whole plots of 30 m - a tanker berth's 270 to 345 m [P1] 10, a bulk berth's about 300 m 10, a... |
| 93 | `CityShore.CLEAR` | `1` | Plots kept clear between two works, and between a work and a highway (H5's verge): one. |
| 96 | `CityShore.SEARCH_TILES_MOST` | `4096` | The most tiles one search looks at before it counts the work short: 4,096 - 64 districts' worth, about 0.3 s, so a city of billions with no free shore never pays more. |
| 101 | `CityShore.HARBOUR_REACH` | `100` | How far from a harbour's first work, in plots, its other works may stand: 100, 3 km each way: the berths of one harbour lie side by side along a few kilometres of one shore, nearest its first work first. |
| 104 | `CityShore.HARBOURS_MOST` | `2` | The most harbours a coast has: 2 (Jerus, MF1 item 8: "one or two harbours"); a coast is a landmass (ET5), so a second landmass has its own. |
| 107 | `CityShore.HARBOUR_APART` | `3 * HARBOUR_REACH` | How far a second harbour's first work lies from the first's, in plots: three reaches, 300, so that a stretch of beach (a reach wide) lies between the two. |
| 110 | `CityShore.HARBOUR_QUAY_PLOTS` | `2` | A harbour terminal's quay, in plots: 2, a wall along the water with the berth just past it, the same for every terminal so that the berths lie on one line side by side, not on piers of their own (QUAY_PLOTS' 6 to 14 a... |
| 113 | `CityShore.HARBOUR_DEPTH_M` | `14` | Deep enough for a harbour, in metres: 14 (a loaded Panamax draws about 12 to 14 m; the berth's own water is dredged, the channel is not). |
| 116 | `CityShore.HARBOUR_DEPTH_AT` | `10` | Where that depth is read: the water this many plots out from a candidate's shore plot (10, 300 m: where a pier ran before MF1). |
| 119 | `CityShore.HARBOUR_SHELTER_PLOTS` | `60` | How far round a candidate's shore plot its shelter is read, in plots: 60, 1.8 km: the share of that disc that is salt water (a cape's is above a half, a bay's below). |
| 122 | `CityShore.HARBOUR_EXPOSED_PLOTS` | `150` | What a wholly open shore costs against a wholly closed one, in plots of distance from the founding site: 150, 4.5 km: a bay that much farther is as good as a cape. |
| 125 | `CityShore.HARBOUR_SHALLOW_PLOTS` | `150` | What a shore whose water is not HARBOUR_DEPTH_M deep costs, in plots of distance: 150. |
| 128 | `CityShore.HARBOUR_CANDIDATES` | `8` | The shores a harbour's first work weighs: 8, each at least a reach from the one before, the nearest first, within three reaches of the nearest's distance. |
| 159 | `CityShore.DX` | `{ 0, 1, 0, - 1 }, DY = { - 1, 0, 1, 0 }` | North, east, south, west: the sea's side of a work, its quay's heading. |
| 215 | `CityShore.CELL_SHIFT` | `3` | Plots on the index's cell, as a shift: 8 - a General Cargo Terminal's box with its verge (5 x 6) touches four cells at most, the largest work's (a container terminal's 16 x 15) a dozen. |
| 218 | `CityShore.CELLS_MOST` | `64` | The most cells one work may be entered in; a work that spans more (a box or a quay no layout makes: a corrupt sidecar's) is kept apart and asked of every plot, as the loop asked it. |
| 462 | `CityShore.WALL_JOIN_GAP` | `2 * CLEAR + 1` | How wide a gap along the shore a harbour's quay still joins across, in plots: 2 x CLEAR + 1, 3 - the verge each of two neighbours keeps and a plot more, so works that stand side by side read as one wall, and works far... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 206 | `final List<Work> works` | The works, in the order laid. |
| 209 | `private long version` | Moves with every work laid or taken: what the plans and the routes are kept against. |
| 212 | `private long removals` | How many works have been taken since this shore was made: a search that remembers where it found room (CityMap.shoreSpot()) starts again from the nearest when it moves, for a work taken may open room anywhere. |
| 221 | `private Index index` | The works by cell (SL1), or null when it is to be built again: a reader takes the index it finds and asks that one, so a rebuild beside it costs it a stale answer, never a failed one. |
| 315 | `final long key` |  |
| 316 | `Work[] works` |  |
| 326 | `Cell[] cells` |  |
| 327 | `int count` |  |
| 329 | `int indexed` | The works' count it was built to. |
| 331 | `Work[] wide` | The works that span more than CELLS_MOST cells, in the order laid: asked of every plot. |
| 485 | `private volatile Walls walls` | The walls and lines, or null when they are to be made again: a reader takes the set it finds, so a rebuild beside it costs it a stale answer, never a failed one. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 577 | **type** `public final class CityShore` | The city's works on its shore as the map draws them: each sea terminal's box on owned dry ground at the water with its quay run straight out over owned sea, and each tank farm's box at the water, laid city-wide in the... |

### the harbour (MF1, 2026-10-28) (lines 98-310)

| line | len | member | says |
|---:|---:|---|---|
| 131 | 1 | **type** `public record Harbour(long x, long y, int landmass, int works)` | A harbour: its first work's shore plot (x, y) in world plots, the landmass it is on (ANY when the city holds no picture of its landmasses) and how many works stand in its reach. |
| 138 | 19 | `static List<Harbour> harbours(List<Work> works, int reach, java.util.function.ToIntFunction<Work> landmassOf)` | The harbours of `works`, in the order opened: each work, in the order laid, belongs to the first harbour on its landmass whose first work is within `reach` plots of its shore plot, or opens one (a shore laid before th... |
| 166 | 38 | **type** `public record Work(long x0, long y0, int w, int h, int type, int side, int quay)` | One work: its box {x0, y0, w, h} in world plots, its type id, the side the sea is on (0 north to 3 west), and its quay's length in plots (0 for a tank farm, which has none). |
| 168 | 1 | `public boolean berthed()` _(in CityShore.Work)_ | Whether a boat comes alongside it: a terminal's quay. |
| 171 | 8 | `public long[] shorePlot()` _(in CityShore.Work)_ | The plot of the box at the middle of its sea side, {x, y}: where its quay starts from. |
| 181 | 4 | `public long[] quayPlot(int k)` _(in CityShore.Work)_ | The quay's k-th plot out (k from 1 to quay), {x, y}. |
| 187 | 4 | `public double[] berth()` _(in CityShore.Work)_ | Where a boat lies at it: the middle of the plot just past its quay's end, {x, y} in plots - its route's first point. |
| 193 | 1 | `public boolean holds(long x, long y)` _(in CityShore.Work)_ | Whether world plot (x, y) is its box's. |
| 196 | 7 | `public boolean quayAt(long x, long y)` _(in CityShore.Work)_ | Whether world plot (x, y) is one of its quay's plots. |
| 224 | 1 | `public List<Work> works()` | The works, in the order laid. |
| 227 | 1 | `public boolean isEmpty()` | Whether there are none. |
| 230 | 1 | `public long version()` | A number that moves with every work laid or taken. |
| 233 | 1 | `public long removals()` | How many works have been taken, ever: moves with a removal only. |
| 236 | 5 | `public int count(int t)` | How many works of type t are laid. |
| 243 | 10 | `void add(Work w)` | A work laid (CityMap's THE SHORE finds its place). |
| 255 | 11 | `boolean removeNewest(int t)` | The newest work of type t taken away; false when there is none. |
| 272 | 11 | `boolean removeNewest(int t, java.util.function.Predicate<Work> which)` | The newest work of type t that `which` accepts taken away; false when there is none (ET5, 2026-10-25: a city that stands on more than one landmass takes a landmass's terminal from that landmass, CityMap's layShore()). |
| 285 | 10 | `public Work at(long x, long y)` | The work whose box holds world plot (x, y), or null: the first laid, from the plot's cell (SL1; the loop over every work, when some work is wide). |
| 297 | 7 | `public boolean blocks(long x, long y)` | Whether world plot (x, y) is a work's box or within CLEAR of one, or a quay's: where the runs may not go and no other work may stand. |
| 306 | 4 | `private static boolean blocksAt(Work w, long x, long y)` | Whether one work blocks world plot (x, y): its box with its verge, or its quay. |

### the index (SL1) (lines 311-458)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 9 | **type** `private static final class Cell` | One cell of the index: its key and the works that touch it, in the order laid (replaced, never edited, so a reader holds a whole list). |
| 318 | 4 | `Cell(long key, Work first)` _(in CityShore.Cell)_ |  |
| 325 | 83 | **type** `private static final class Index` | The cells that hold any work, open addressing by the cell's key (a null slot is empty), and the works too wide for cells. |
| 333 | 5 | `Index(int works)` _(in CityShore.Index)_ |  |
| 339 | 3 | `private static int slot(long key)` _(in CityShore.Index)_ |  |
| 344 | 7 | `Cell cellAt(long cx, long cy)` _(in CityShore.Index)_ | The cell with key (cx, cy), or null when no work touches it. |
| 353 | 25 | `void enter(Work w)` _(in CityShore.Index)_ | Enters work w in every cell its box with its verge, or its quay, touches (after the works laid before it, so a cell's list is in the order laid). |
| 380 | 27 | `private void put(long cx, long cy, Work w)` _(in CityShore.Index)_ | Appends w to the cell (cx, cy), the cell made if it is the first work in it. |
| 410 | 9 | `private Index index()` | The index as it stands: built to the works as they are when it is not current. |
| 426 | 11 | `public long fill(long x0, long y0, int w, int h, byte[] fixed, byte code)` | The works' boxes over the w x h plots from world plot (x0, y0), row by row: `code` over every plot of a box (a plan's ground: DistrictPlan's FIXED_YARD, no street crosses it, no building stands on it). |
| 439 | 8 | `public long frameHash(long x0, long y0, int w, int h)` | ...the same hash without drawing them: what a district's plan is kept against. |
| 449 | 9 | `public List<BoatSchedule.Berth> berths(BuildingVisual.Type[] types)` | The works that are berths: each terminal's quay end as BoatSchedule's berth, in the order laid. |

### the harbour's quay and its boats alongside (HB1, 2026-11-08) (lines 459-616)

| line | len | member | says |
|---:|---:|---|---|
| 468 | 12 | **type** `public record Wall(long x0, long y0, int w, int h, int side)` | One run of a harbour's quay, drawn as a wall from DETAIL_FROM in: world plots (x0, y0) to (x0 + w, y0 + h) not included, on the sea side `side` of the works it stands before (0 north to 3 west). |
| 471 | 3 | `public boolean meets(long wx0, long wy0, long wx1, long wy1)` _(in CityShore.Wall)_ | Whether it meets the plots from (x0, y0) up to, not including, (x1, y1). |
| 476 | 3 | `public boolean holds(long x, long y)` _(in CityShore.Wall)_ | Whether world plot (x, y) is in it. |
| 482 | 1 | **type** `private record Walls(long version, int works, Wall[] walls, List<QuayLine> lines)` | The walls and the quay lines as they were made, and what they were made against: the version and the count of works (a list edited by hand moves the count, as the index checks). |
| 494 | 3 | `public List<Wall> walls()` | The harbour's quays as walls, cached on version(): for each berthed work with quay() == HARBOUR_QUAY_PLOTS the strip of plots along its box's whole sea side, quay() plots deep; and between two such works facing the sa... |
| 499 | 5 | `public List<Wall> wallsIn(long x0, long y0, long x1, long y1)` | The walls that meet the plots from (x0, y0) up to, not including, (x1, y1): a frame's, from the cached set, in the order of walls(). |
| 506 | 1 | **type** `public record QuayLine(int side, long line, long from, long to, List<Work> works)` | One quay line: the works whose strips join into one wall, in order along the shore. |
| 509 | 3 | `public List<QuayLine> quayLines()` | The quay lines, cached with the walls and made in the same pass: one for each wall before it is cut round the boxes, in the order of side, line and place along the shore. |
| 513 | 11 | `private Walls wallsNow()` |  |
| 526 | 9 | `private static long[] strip(Work k)` | One work's strip before its sea side, as {side, line, from, to, depth}: `line` the strip's outer edge, `from` to `to` (not included) along the shore. |
| 537 | 1 | **type** `private record Strip(long[] s, Work work)` | One berthed harbour work and its strip(). |
| 540 | 35 | `private Wall[] makeWalls(List<QuayLine> lines)` | The walls of walls(), and into `lines` the quay lines: the strips sorted by side, line and place, joined across WALL_JOIN_GAP (a group is a line), each group's wall cut round the boxes it meets. |
| 577 | 3 | `private static boolean meetsBox(Wall wall, Work k)` | Whether the wall meets work k's box. |
| 582 | 34 | `private void cutRound(List<Wall> out, Wall wall)` | `wall` added to `out` less every plot of every work's box: the works that stand in its plots, and the pieces of it round their boxes. |

### the sidecar (lines 617-655)

| line | len | member | says |
|---:|---:|---|---|
| 620 | 12 | `void write(DataOutputStream out) throws IOException` | Writes the works (CityMap FORMAT 6): their count, then each one's box, type, side and quay. |
| 634 | 12 | `static CityShore read(DataInputStream in) throws IOException` | The works read back, or null when they do not add up. |
| 648 | 3 | `public boolean same(CityShore o)` | Whether two cities' works are the same, in the same order. |
| 652 | 3 | `public String toString()` |  |

