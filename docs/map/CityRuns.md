# CityRuns.java - 3,063 lines · 147 methods · 41 constants · model

`ham/citybuildersim/CityRuns.java` - generated 2026-10-22 by CodeMap; line numbers are as of that run.

> The city's highways and railway as runs on corridors: each laid month by month from its hub's lines out, straight by preference, never moved, with the railway's yards on its track nearest the mines - what the city map draws them from and keeps in its sidecar.
> 
> WHY THIS EXISTS (0.7.89, batch RD3; the project's spec-roads-and-ports.md
> 2.7 and 2.8, a port of its prototype roads-prototype/hw.py). From 0.7.72 to
> 0.7.88 each district laid its own highway and track from its first road
> tile's hub, both ways and across itself once (CityMap's THE NETWORK): a
> city's six highways were six stubs, each crossing itself - the dark + with
> buildings inside its arms in Jerus's screenshot, and at 0.7.88 a short
> highway floating inside a block. Jerus: "highways that prefer going
> straight and must be connected". Here a NET (the highways, or the railway)
> is laid city-wide:
> 
>   LINES     a highway rides the street line through the middle of a cell,
>             HIGHWAY_AT plots in, so it never takes an arterial's row; the
>             railway the line a quarter in, RAIL_AT, so it never shares a row
>             with a highway;
>   SPINE     from its HUB - the highways' line crossing at the founding site,
>             the railway's at the city's mine nearest it (spec 2.8: "laid as
>             runs from the rail terminals, which sit in estate cells nearest
>             the mines") - four ARMS along its row and its column; then
>             parallel CORRIDORS one district (APART) apart, nearest the hub
>             first, each starting where it crosses the net already laid on
>             the city's ground (an interchange);
>   GROWTH    each plot the model adds goes to the shortest arm of the
>             earliest corridor that can still grow, the gateway's arm before
>             the rest (GATEWAY); what it takes away comes off the newest end -
>             the latest corridor's longest arm, the gateway's last - so no
>             plot laid ever moves (H4);
>   THE COST  (H2: "heavily favours being straight and few junctions"): a step
>             costs STRAIGHT_COST, a 45-degree turn TURN_COST plots of
>             straight, a junction JUNCTION_COST. An arm goes straight while it
>             can; at the sea, or fresh water wider than it bridges, it turns
>             45 degrees to the side with more ground and runs along the shore
>             (the alternative is a dead end); it turns back onto its heading
>             where the way home runs TURN_COST plots (each step home lays one
>             plot where the staircase lays two, so the turn pays); at the
>             city's edge it waits for the ground ahead, unless the ground
>             beside it runs JUNCTION_COST plots at 45 degrees (a coast or a
>             valley: what another corridor's junction would serve); meeting
>             the net running across it, it crosses (an interchange) only where
>             the way beyond runs JUNCTION_COST plots, else waits there in a T;
>             meeting it running its own way, it joins it and stops; a new
>             corridor opens only when no arm can grow. The prototype's rule -
>             45 degrees at the sea alone, waiting at every edge - is this
>             one's stiffest case;
>   DRAWN     a diagonal is laid as a staircase so the run stays joined, and
>             drawn smooth; a highway is elevated - streets pass beneath, the
>             railway bridges over it, it passes over a mine standing on its
>             site - with RAMPS where it crosses every other arterial
>             (RAMP_EVERY), where each arm of the spine first crosses one
>             (FX2: the every-other rule counts across the map, and could
>             leave a spine arm's first 1.4 km with none), and at each arm's
>             end; the railway crosses streets
>             level - an arterial or boulevard crosses the main line on a
>             bridge (D18) - and never runs along a highway; (FX2) a highway
>             net is drawn once a straight stretch of it crosses an arterial
>             at a ramp; before that its plots are counted undrawn, the
>             legend's;
>   YARDS     each Rail Terminal on the track, in the cell along it nearest a
>             mine, its box across the line (spec 2.8); (FX5 Y1) laid YARD_LONG
> ... (36 more lines in the source)

**Uses:** [World](World.md) (41), [CityMap](CityMap.md) (18), [InterchangeGround](InterchangeGround.md) (12), [TilePainter](TilePainter.md) (6), [TrainSchedule](TrainSchedule.md) (6), [DistrictPlan](DistrictPlan.md) (5), [BuildingVisual](BuildingVisual.md) (2), [TileRaster](TileRaster.md) (2)

**Used by (16):** [CityMap](CityMap.md), [DistrictPlan](DistrictPlan.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GatewayCheck](GatewayCheck.md), [InterchangeGround](InterchangeGround.md), [JunctionCheck](JunctionCheck.md), [LandMap](LandMap.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [PlanCheck](PlanCheck.md), [RailDrawCheck](RailDrawCheck.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 113 | THE DIALS |
| 188 | THE GROUND THE RUNS READ |
| 207 | A NET: ITS HUB, CORRIDORS AND ARMS |
| 477 | WHERE A NET'S PLOTS ARE |
| 607 | GROWING A NET |
| 1034 | TAKING FROM THE NEWEST END |
| 1120 | THE RAILWAY'S YARDS |
| 1397 | THE RAILWAY'S WAYS: THE MAIN LINE, ITS SIDINGS AND ITS BRANCHES |
| 1709 | · boxes: a grid of them, the works merged where they touch, the works in groups |
| 1892 | · the sidings |
| 2090 | · a further yard, and its branch |
| 2283 | THE MAIN LINE (0.8.1, batch FR0; spec-freight 4.2, D14) |
| 2511 | · the trains' way (0.8.2, batch FR1; spec-freight 4.4) |
| 2669 | THE MONTH: TO THE MODEL'S COUNTS |
| 2722 | READING THEM: A BOX OF PLOTS |
| 2921 | THE SIDECAR |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 118 | `CityRuns.CELL` | `World.TILE` | A cell's side: a tile, 32 plots (DistrictPlan.CELL). |
| 121 | `CityRuns.HIGHWAY_AT` | `DistrictPlan.MIDDLE + 1` | Where a highway rides in its cell: 16 plots in, the street line through its middle (spec 2.7; DistrictPlan's MIDDLE line, 15 into the interior), so it never takes an arterial's row. |
| 124 | `CityRuns.RAIL_AT` | `DistrictPlan.LATTICE` | ...and the railway: 8 plots in, the street line a quarter in (spec 2.8; the lattice's first line, DistrictPlan.LATTICE) - never a highway's row. |
| 127 | `CityRuns.GATEWAY_UNKNOWN` | `- 2` | A city whose gateway has not been looked for yet (a map's runs read from a sidecar older than FORMAT 8). |
| 130 | `CityRuns.NO_GATEWAY` | `- 1` | A city looked for and found to have no gateway: an island founding with no land route to the world (spec 4.1). |
| 133 | `CityRuns.APART` | `CityMap.DISTRICT` | Parallel corridors lie one district apart: 256 plots, 7.68 km (spec 2.7; hw.py's APART). |
| 136 | `CityRuns.LOOK` | `6` | How far ahead an arm must see its way clear to step: 6 plots (hw.py's _ahead(..., 6)) - so it stops short of the sea and the city's edge. |
| 139 | `CityRuns.SIDE_LOOK` | `159` | How far an arm at the sea looks along each 45-degree way, to turn toward the one with more ground: 159 plots (hw.py's score, range(1, 160)). |
| 142 | `CityRuns.STRAIGHT_COST` | `1` | What a step straight on costs, in plots of straight: 1 (spec 2.7, H2). |
| 145 | `CityRuns.TURN_COST` | `40` | ...a 45-degree turn: about 40 (spec 2.7, H2: "est., dials to tune"). |
| 148 | `CityRuns.JUNCTION_COST` | `200` | ...a junction: about 200 (spec 2.7, H2). |
| 151 | `CityRuns.TWIN_NEAR` | `40` | A run off its heading stops short of another arm's plot this near beside it, either side: 40 plots (hw.py's no parallel twin, range(2, 40)). |
| 154 | `CityRuns.RAMP_EVERY` | `2` | Ramps where a highway crosses every RAMP_EVERY-th arterial: every other one, 1.92 km (spec 2.7); and (FX2) where each arm of the spine first crosses one. |
| 157 | `CityRuns.HUB_REACH` | `CityMap.TILES_A_SIDE` | A hub is sought among its lines' crossings within this many cells of where its net starts, each way, the nearest first: 8 - a district - where its own crossing is not the city's dry ground. |
| 160 | `CityRuns.READ_AHEAD` | `CELL` | An arm reads its way ahead this far at a time and steps on what it read: a cell, 32 plots - so a step reads a plot, not LOOK. |
| 163 | `CityRuns.DX` | `{ 1, 1, 0, - 1, - 1, - 1, 0, 1 }, DY = { 0, 1, 1, 1, 0, - 1, - 1, - 1 }` | The eight headings, hw.py's DIRS: east, south-east, south, south-west, west, north-west, north, north-east. |
| 166 | `CityRuns.HIGHWAYS` | `0, RAILWAY = 1` | A net's kinds. |
| 169 | `CityRuns.F_HIGHWAY` | `BuildingVisual.HIGHWAY` | What a plot carries (fill()): a highway's plot (BuildingVisual.HIGHWAY, DistrictPlan.FIXED_HIGHWAY)... |
| 171 | `CityRuns.F_RAIL` | `TilePainter.RAIL` | ...the railway's track (TilePainter.RAIL, DistrictPlan.FIXED_RAIL)... |
| 173 | `CityRuns.F_RAIL_OVER` | `6` | ...the railway on a bridge over a highway (spec 2.8)... |
| 175 | `CityRuns.F_YARD` | `7` | ...a yard's ground, its track among it (DistrictPlan.FIXED_YARD): no street crosses it, no other building stands on it. |
| 177 | `CityRuns.F_RESERVE` | `8` | ...and (batch FR7, D19) an interchange's reserve, a plan's ground only (fill() with yardsOver; InterchangeGround): street ground the plan keeps clear of lots, buildings and streets (DistrictPlan.FIXED_RESERVE) - never... |
| 180 | `CityRuns.M_RAMP` | `1` | A plot's marks (fill()'s second array): a highway's ramp (spec 2.7)... |
| 182 | `CityRuns.M_DIAG` | `2` | ...a plot of a 45-degree stretch, drawn smooth, its heading's two bits from M_DIAG_SHIFT (diagCode())... |
| 184 | `CityRuns.M_DIAG_SHIFT` | `2` | ...the bit that heading's two bits start at: bits 2 and 3, between M_DIAG and M_CORNER... |
| 186 | `CityRuns.M_CORNER` | `16` | ...and of those, a staircase's corner: the plot beside the stretch's line, (x + dx, y) of a step from (x, y). |
| 353 | `CityRuns.RESERVES_KEPT` | `8192` | The most regions' sites kept at once: 8,192 - a screen's tiles and the plans' frames, many times over. |
| 612 | `CityRuns.OPEN` | `0, EDGE = 1, SEA = 2` | What stops a way: nothing, the city's edge, or the sea (and fresh water wider than the net bridges). |
| 739 | `CityRuns.STEPPED` | `0, WAITS = 1, JOINED = 2, HELD = 3, YIELDS = 4` | What a step did: stepped on; waits on the ground (tried again when the ground moves); stopped for good (joined); held for want of a second plot; or waits on the other net (tried again next time). |
| 1148 | `CityRuns.YARD_LONG` | `(int) Math.ceil(2 * TrainSchedule.LOCO_PLOTS + Math.ceil(TrainSchedule.UNIT_T...` | How long a yard is laid along its line (FX5 Y1; Jerus, 2026-10-20: "a long train will be out of the depot", "the rail depots would have to be redesigned to accomdoate such large trains"): a unit train's length rounded... |
| 1158 | `CityRuns.TIER_LENGTHS` | `tierLengths()` | The lengths, longest first, a yard of footprint {w, h} is laid at along a row (w along) or a column (h along): each kind of train's length rounded up and its two ladders - a unit train's YARD_LONG (48), an intermodal ... |
| 1161 | `CityRuns.TIERS` | `TIER_LENGTHS.length + 1` | ...how many: one for each length and one for the footprint's own. |
| 1457 | `CityRuns.BRANCH` | `2, SIDING = 3` | The corridors of the arms laid for a train's way: a branch to a further yard (2) and a siding into a works (3). |
| 1460 | `CityRuns.SIDING_REACH` | `CityMap.DISTRICT` | A siding serves a works within this of the main line, in plots: one district (CityMap.DISTRICT, 256; spec-freight 4.3, "the largest BULK works within one district of the line"). |
| 1463 | `CityRuns.MAIN_LINE_REACH` | `1L<<20` | How far past the plots it has the main line of a city with a station is grown, at most, in plots: 1,048,576 (2^20) - some four thousand districts, past any city's ground, so the line waits at the city's edge and not a... |
| 1466 | `CityRuns.SERVED_WITHIN` | `1` | A works the main line touches or runs beside, within this many plots, has the railway at its door and needs no siding: 1. |
| 1469 | `CityRuns.GROUP_LINK` | `CityMap.DISTRICT / 2` | A group of rail-served works is the works whose box middles lie within this of each other, link by link: half a district, 128 plots. |
| 1472 | `CityRuns.YARD_APART` | `CityMap.DISTRICT` | A further yard goes beside a group of works that lies more than this from every yard laid: a district, 256 plots (FR7: "more than one district from every yard laid"). |
| 1475 | `CityRuns.BRANCH_FROM` | `CityMap.DISTRICT` | A group of works farther than this from the track is reached by a branch off the main line, else the yard sits on the track nearest it: a district, 256 plots. |
| 1712 | `CityRuns.BOX_GRID` | `64` | The boxes' grid: 64 plots a side. |
| 1945 | `CityRuns.SIDING_TRIES` | `200` | The most works laySidings() tries in one run: 200. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 213 | `final int corridor, home` |  |
| 214 | `final long sx, sy` |  |
| 215 | `int head` |  |
| 216 | `long x, y` |  |
| 217 | `int steps` |  |
| 218 | `boolean joined` |  |
| 220 | `int segs` | Its stretches: each one's heading, steps, and start (the plot before its first step). |
| 221 | `int[] segHead` |  |
| 222 | `long[] segX` |  |
| 224 | `final List<long[]> crossings` | Where it crossed the net (interchanges), each {x, y, the step that made it}, in the order made. |
| 226 | `int known` | Steps known clear ahead along `head`, read at the ground's version knownAt; and the version it waits at (transient). |
| 227 | `long knownAt` |  |
| 229 | `int stoppedBy` | What stopped it the last time it waited with no way along (transient): OPEN (nothing, or another reason), EDGE or SEA. |
| 258 | `final int kind, at, bridge` |  |
| 260 | `boolean started, hubLaid` | Its hub chosen, and whether the hub's plot is laid (a railway may stand for its yards with no track). |
| 261 | `long hubX, hubY` |  |
| 263 | `final List<long[]> corridors` | Its corridors, {0 a row \| 1 a column, the line's coordinate}, in the order opened. |
| 264 | `final List<Arm> arms` |  |
| 265 | `final List<Interchange> interchanges` |  |
| 267 | `long plots` | Plots laid: the union of its arms' plots and its hub's. |
| 269 | `long stuckAt` | The ground's version at which it last could lay no more (transient): it waits until the ground moves. |
| 271 | `final Map<Long, long[]> buckets` | Its stretches by district: district key -> {arm << 24 \| stretch} entries, and how many. |
| 272 | `final Map<Long, Integer> bucketN` |  |
| 322 | `final Net highways` |  |
| 323 | `final Net rail` |  |
| 326 | `int gateway` | The gateway's bearing, city-wide (the highway's and the railway's share it): GATEWAY_UNKNOWN until the map looks, NO_GATEWAY where it finds none. |
| 329 | `final List<long[]> yards` | The railway's yards: each {x0, y0, w, h} in world plots, in the order laid (FX5 Y1: up to YARD_LONG plots along its line, so its box is not always its type's footprint: yardAsked()). |
| 341 | `long highwayShort, trackSurplus` | What the model has that the runs could not lay: highway plots, yards (counted: the legend says so; a yard with no place is drawn by its district's plan) - and (FR7 2026-10-18, batch FR7 slice 3) the track plots the mo... |
| 342 | `int yardsShort` |  |
| 345 | `private Ground reserveGround` | The ground the interchanges' reserves are found over, and the works they keep clear of (batch FR7, InterchangeGround): bound by layTo() and the map, never saved. |
| 346 | `private InterchangeGround.Guard reserveGuard` |  |
| 349 | `private final Map<List<Long>, List<InterchangeGround.Site>> reserveKept` | The sites found, by the region asked about, against the stamp they were found at: the runs, the ground's and the works' versions - none kept past a change of any. |
| 350 | `private long reserveStamp` |  |
| 417 | `long version` | A stamp of everything laid: what the map's plans and views are kept against. |
| 420 | `private boolean doubleMain` | Whether the main line is double track: the city has a Freight Line (spec 4.3). |
| 423 | `private List<int[]> mainLineCache` | mainLine()'s last answer, and the version and the main arm it was computed at. |
| 424 | `private long mainLineCacheAt` |  |
| 425 | `private int mainLineCacheArm` |  |
| 428 | `private boolean highwaysReachCache` | highwaysReach()'s last answer, and the version it was computed at. |
| 429 | `private long highwaysReachCacheAt` |  |
| 432 | `private int mainArm` | The rail net's arm the main line runs out along and the trains come in on: the arm the railway's world line leaves the city's ground from (CityMap.worldLines() sets it, FX2), or -1 for the gate arm. |
| 1203 | `private final List<int[]> yardAsked` | The footprints the yards were laid for, in the order of `yards` (the first yardAsked.size() of them; the rest were added by hand and were asked for their boxes' own). |
| 1484 | `private long railStamp` | layRail()'s last stamp, and whether it has one: everything it reads, so it lays nothing twice for the same inputs. |
| 1485 | `private boolean railStampSet` |  |
| 1494 | `final List<long[]> mines, works` |  |
| 1495 | `private List<long[]> bulk, line` |  |
| 1496 | `private BoxGrid blocked` |  |
| 1497 | `private Groups groups` |  |
| 1498 | `private final Map<Long, List<long[]>> spots` |  |
| 1499 | `private final Map<Long, Integer> spotAt` |  |
| 1501 | `private final Map<Long, List<List<long[]>>> spotTiers` | Each size's boxes by tier, unsorted (spotsOnTrack()), and how many tiers are sorted into `spots` so far: a tier is sorted - each box weighed against every mine - only once every box before it failed to fit (FX5: five ... |
| 1502 | `private final Map<Long, Integer> tiersSorted` |  |
| 1716 | `private final Map<Long, List<long[]>> cells` |  |
| 1820 | `final double[][] g` |  |
| 1821 | `final boolean[] served` |  |
| 1822 | `private final Map<Long, List<Integer>> cells` |  |
| 1823 | `private int next` |  |
| 2309 | `private long[] station` | freightStation()'s last answer, the version, the gateway and the main arm it was found at. |
| 2310 | `private long stationAt` |  |
| 2311 | `private int stationAtGateway` |  |
| 2312 | `private int stationAtArm` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 111 | 2953 | **type** `public final class CityRuns` | The city's highways and railway as runs on corridors: each laid month by month from its hub's lines out, straight by preference, never moved, with the railway's yards on its track nearest the mines - what the city map... |

### THE DIALS (lines 113-187)

### THE GROUND THE RUNS READ (lines 188-206)

| line | len | member | says |
|---:|---:|---|---|
| 193 | 8 | **type** `public interface Ground` | The ground runs are laid on: the city map's (CityMap.RunGround). |
| 195 | 1 | `int at(long x, long y, int dir)` _(in CityRuns.Ground)_ | The ground at world plot (x, y), read along heading `dir` (0 to 7, or -1): a World class, or -1 where the city does not own it. |
| 197 | 1 | `long version()` _(in CityRuns.Ground)_ | A number that moves whenever the city's ground does (a purchase): what a waiting arm and a stuck net wait on. |
| 199 | 1 | `long[] box()` _(in CityRuns.Ground)_ | The city's owned box, {x0, y0, x1, y1} in plots, inclusive: how far out a corridor is sought. |
| 203 | 3 | `static boolean dry(int g)` | Whether a ground class is the city's dry ground: owned (a class at all, not -1), and dry by World.dry() - not water, and since 0.7.127 not ice. |

### A NET: ITS HUB, CORRIDORS AND ARMS (lines 207-476)

| line | len | member | says |
|---:|---:|---|---|
| 212 | 40 | **type** `static final class Arm` | One arm: from its start along its corridor, its straight stretches in the order laid. |
| 231 | 4 | `Arm(int corridor, int home, long sx, long sy)` _(in CityRuns.Arm)_ |  |
| 237 | 4 | `void push(int h)` _(in CityRuns.Arm)_ | A step along h from where it stands: its stretch lengthened, or a new one begun. |
| 242 | 9 | `void append(int h, int steps, long fromX, long fromY)` _(in CityRuns.Arm)_ |  |
| 254 | 1 | **type** `record Interchange(long x, long y, int corridor)` | A corridor's start: where it began on the net (an interchange; the spine's at the hub). |
| 257 | 64 | **type** `public static final class Net` | One net: the highways or the railway. |
| 274 | 1 | `Net(int kind, int at, int bridge)` _(in CityRuns.Net)_ |  |
| 277 | 1 | `public long plots()` _(in CityRuns.Net)_ | Plots laid. |
| 279 | 1 | `public int corridors()` _(in CityRuns.Net)_ | Corridors opened. |
| 281 | 1 | `public int interchanges()` _(in CityRuns.Net)_ | Interchanges: corridors' starts (the hub's among them) and arms' crossings. |
| 283 | 6 | `public List<long[]> interchangePlots()` _(in CityRuns.Net)_ | ...as {x, y}: a harness's. |
| 290 | 1 | `public int arms()` _(in CityRuns.Net)_ | Arms. |
| 292 | 1 | `public long[] hub()` _(in CityRuns.Net)_ | Its hub, {x, y}, or null before it starts. |
| 294 | 5 | `public int bends()` _(in CityRuns.Net)_ | Stretches off their arms' headings: 45-degree runs along a shore or the city's edge. |
| 300 | 1 | `public int joined()` _(in CityRuns.Net)_ | Arms ended where they met the net (joined). |
| 303 | 5 | `public List<long[]> stretches()` _(in CityRuns.Net)_ | Each arm's stretches as {x0, y0, heading, steps} (x0, y0 the plot before its first step), arm by arm: a harness's. |
| 315 | 5 | `void reset()` _(in CityRuns.Net)_ | Back to a net never started, the hub with it (FR6's playtest): a reset that kept its hub read back from a save as a fresh net's 0, 0, and the map was not the same map across the save (the default run, m1186, after its... |
| 356 | 4 | `public void setReserveGuard(InterchangeGround.Guard guard)` | Sets the works a reserve may not take (the map's mines and shore works). |
| 366 | 4 | `public void layReserves(Ground g)` | Binds the ground the interchanges' reserves are found over and forgets the sites kept: called by layTo() every month, and by the map when it has read its runs back. |
| 378 | 15 | `public List<InterchangeGround.Site> reservesIn(long x0, long y0, int w, int h)` | The interchange sites whose box meets the w x h plots from (x0, y0), each with whether it got its reserve (InterchangeGround): found when asked, over the region and a margin, and kept against the runs, the ground and ... |
| 395 | 13 | `public List<InterchangeGround.Site> reserves()` | Every interchange site of the highways, over the box their plots lie in: a harness's, for a city a screen's size - a city of billions asks reservesIn() of a plan or a tile. |
| 410 | 5 | `public List<InterchangeGround.Site> reservesLaid()` | The reserves that were laid (Site.got()), of reserves(). |
| 434 | 1 | `public Net highways()` |  |
| 435 | 1 | `public Net rail()` |  |
| 436 | 1 | `public List<long[]> yards()` |  |
| 437 | 1 | `public long highwayShort()` |  |
| 440 | 1 | `public long highwayUndrawn()` | The highway plots the model has that the map draws nowhere: those the runs could not lay (highwayShort()), and (FX2) all the net's while it reaches no street (highwaysReach()), laid but not drawn - the legend's. |
| 442 | 1 | `public long trackSurplus()` | The track plots the model has that the railway's ways do not use and the map does not draw (FR7): the model's less the main line's, the sidings' and the branches' - the legend's "plots of track more than the line uses". |
| 443 | 1 | `public int yardsShort()` |  |
| 444 | 1 | `public long version()` |  |
| 447 | 1 | `public boolean doubleMain()` | Whether the main line is double track: the city has a Freight Line (spec 4.3). |
| 450 | 1 | `public void setDoubleMain(boolean doubleMain)` | Sets whether the main line is double track (the map's, from its Freight Line count). |
| 453 | 1 | `public int gateway()` | The gateway's bearing: GATEWAY_UNKNOWN until the map looks, NO_GATEWAY where it finds none. |
| 456 | 5 | `void setGateway(int bearing)` | Sets the gateway's bearing (the map's, once it has looked). |
| 463 | 1 | `public void setMainArm(int arm)` | Sets the arm the main line runs out along (the map's, from where the railway's world line leaves the city's ground): -1 for the gate arm. |
| 470 | 6 | `public int mainArm()` | The rail net's main arm: -1 with no gateway or before the railway starts; else the arm the map set (setMainArm()) while it is an arm of the net with steps laid, else the gate arm. |

### WHERE A NET'S PLOTS ARE (lines 477-606)

| line | len | member | says |
|---:|---:|---|---|
| 481 | 3 | `static long districtKey(long x, long y)` |  |
| 486 | 12 | `static void file(Net n, int a, int s, long x, long y)` | Files stretch s of arm a under the district of plot (x, y). |
| 500 | 20 | `static boolean onStretch(Arm a, int s, long px, long py)` | Whether stretch s of arm a covers world plot (px, py): one of its steps' plots, or a 45-degree step's corner. |
| 522 | 14 | `static int coverer(Net n, long x, long y, int not)` | The arm of net n covering plot (x, y) other than `not` (-1 for any): its index, -2 for the hub, -1 for none. |
| 538 | 3 | `public static boolean covered(Net n, long x, long y)` | Whether net n has a plot at (x, y). |
| 554 | 14 | `public boolean highwaysReach()` | Whether the highways net is drawn: it has started and it has a ramp onto an arterial where stretchInto() marks M_RAMP - a straight stretch's step on a multiple of CELL that is every RAMP_EVERY-th cell line, or an arm ... |
| 570 | 12 | `static boolean onRampLine(Arm a, int s)` | Whether straight stretch s of arm a has a step (1 to its steps) on a ramp line: its coordinate along the stretch (x going east or west, y going north or south) a multiple of CELL whose cell line is every RAMP_EVERY-th... |
| 590 | 16 | `static long firstLineStep(Arm a)` | The step (1 to its steps) at which arm a's straight stretches first put a plot on a cell line along their heading - x a multiple of CELL going east or west, y going north or south - or -1 for none: where an arm of the... |

### GROWING A NET (lines 607-1033)

| line | len | member | says |
|---:|---:|---|---|
| 621 | 36 | `static int clear(Net n, Ground g, long x, long y, int d, int k, int[] why)` | The steps along heading d from (x, y) the net may take, up to k (or a bridge's past it), and into why[0] what stops it: ground the city does not own, the sea, water wider than its bridge, a staircase's corner off the ... |
| 659 | 5 | `static void lay(Net n, int a, int s, long x, long y)` | Lays net n's plot (x, y) on arm a's stretch s: counted where nothing else of the net covers it yet (an arm never covers its own new plot: it never turns back). |
| 673 | 48 | `static boolean start(Net n, Ground g, long ax, long ay, int gateway)` | Chooses net n's hub: the line crossing nearest (ax, ay) that is the city's dry ground, within HUB_REACH cells; false where none is. |
| 723 | 14 | `static int gateArm(Net n, int gateway)` | The spine's arm toward the gateway: -1 when there is none or the net has not started; else the index of the arm of corridor 0 or 1 whose home heading's angle (heading h is h * 45 degrees, x = cos, y = sin, as DX/DY) i... |
| 748 | 41 | `static boolean grow(Net n, Net other, long target, Ground g, long ax, long ay, int gateway)` | Grows net n toward `target` plots (hw.py's grow()): its hub laid first, then each plot to the shortest arm of the earliest corridor that can still grow - the gateway's arm (gateArm()) before the rest of its corridor -... |
| 796 | 13 | `static List<Integer> spineOrder(Net n, int gate)` | The spine's arms in the order the railway's main line is grown (FR7, batch FR7 slice 3): the gate arm, then the other arm of its corridor, then the other corridor's by index. |
| 825 | 28 | `static boolean growMainLine(Net n, Net other, long target, Ground g, long ax, long ay, int gateway)` | Grows the railway's main line toward `target` plots (FR7, batch FR7 slice 3): its hub laid first, then the gate arm alone, until it waits - at the city's edge, where the world line takes it on. |
| 861 | 70 | `static int step(Net n, Net other, int i, Arm a, Ground g, long target, int[] why)` | One step of arm i (hw.py's _step, with the cost rule): STEPPED; WAITS (the city's edge, the sea with no way along it, a T it does not cross, the other net's run along its way); JOINED (it met the net running its own w... |
| 939 | 36 | `static boolean newCorridor(Net n, Ground g)` | The next corridor (hw.py's _new_corridor()): the parallel line k districts out, k from 1, that crosses the net already laid on the city's ground - at its plot there nearest the hub, an interchange - the nearest of tho... |
| 977 | 7 | `static boolean before(double d, int kind, long v, long[] p, double d2, int kind2, long v2, long[] p2)` | hw.py's order of candidates (distance, kind - 'col' before 'row' -, line, plot): whether the first comes before the second. |
| 986 | 47 | `static long[] nearestOnLine(Net n, Ground g, int kind, long v)` | The plot of net n on the row (kind 0) or column (1) at v, on the city's ground, nearest its hub; null for none. |

### TAKING FROM THE NEWEST END (lines 1034-1119)

| line | len | member | says |
|---:|---:|---|---|
| 1039 | 59 | `static boolean trim(Net n, long target, int gateway)` | Takes net n back to `target` plots, each from the newest end - the latest corridor's longest arm, the gateway's arm after the rest of its corridor, its last step (the reverse of grow()'s order); the hub last. |
| 1100 | 19 | `static void rebuildBuckets(Net n)` | Files every stretch of net n again (after a corridor is closed, or the runs read back). |

### THE RAILWAY'S YARDS (lines 1120-1396)

| line | len | member | says |
|---:|---:|---|---|
| 1163 | 10 | `private static int[] tierLengths()` |  |
| 1175 | 3 | `static int tierAlong(int own, int t)` | The footprint's own length along the line, `own`, at tier t: TIER_LENGTHS[t] (never less than the footprint's), the footprint's own at the last. |
| 1180 | 3 | `static boolean sameTier(int own, int t)` | Whether tier t is a length the tier before it already is (so it adds no boxes): a yard whose footprint is already as long as the interior has two tiers. |
| 1189 | 5 | `static int[] boxSize(boolean row, int w, int h, int along)` | A yard's box on a row (its line along x) or a column, `along` plots long, as {w, h}: across the footprint's own across where the length is the footprint's own (a Rail Depot is 8 x 7 whichever way its line runs, as it ... |
| 1196 | 5 | `public int[] yardAsked(int i)` | The footprint yard i was laid for, {w, h}: the size it was asked for, which it keeps whatever length the ground gave it (a yard added to `yards` by hand, as a harness does, was asked for its box's own). |
| 1206 | 5 | `private void addYard(long[] box, int[] asked)` | Lays a yard: its box, and the footprint it was asked for. |
| 1226 | 11 | `private void keepYards(List<int[]> sizes)` | Takes the yards back to the longest run, from the first, that were laid for the sizes the model now asks (FR7 slice 3, 2026-10-18): a Rail Terminal's box and a Rail Depot's are not one size, and until now every yard w... |
| 1239 | 5 | `List<long[]> yardSpots(int w, int h, List<long[]> mines)` | Every box a yard could take, the nearest a mine first: the YARD_LONG boxes, then (where none fits, in the order of the tiers) the cell's interior's, then the footprint's own; two a cell the track runs straight through... |
| 1246 | 11 | `private List<long[]> nearestMineFirst(List<long[]> tier, List<long[]> mines)` | One tier's boxes, the nearest a mine first (the hub with no mine): yardSpots()'s order within a tier. |
| 1259 | 12 | `List<long[]> yardSpotsNear(int w, int h, double gx, double gy)` | ...the nearest the point (gx, gy) first (FR7 slice 3: a further yard goes beside a group of works, not beside the mine nearest the hub), the tiers in order as above. |
| 1273 | 9 | `private static List<long[]> sortedBy(List<long[]> out, double[] key)` | The boxes by key, the least first; on a tie the northern, then the western. |
| 1288 | 71 | `private List<List<long[]>> spotsOnTrack(int w, int h)` | Every box a yard of footprint w x h could take on the track, unsorted, by tier (TIERS lists, the longest first): two a cell the track runs straight through on its line, one at either end of it; a siding is no line for... |
| 1365 | 13 | `static void spotsIn(List<long[]> out, long cx, long cy, boolean row, int w, int h, int along)` | A cell's yard boxes across its quarter line (a row's, or a column's), `along` plots long (boxSize()): covering the line across, inside the interior across, at either end of the cell along - from the interior's first p... |
| 1380 | 16 | `boolean yardFits(long[] b, Ground g, List<long[]> mines, List<long[]> laid)` | Whether a yard's box is free, the yards `laid` before it: owned dry ground, off the highways and their verges, no other yard's, no mine's site, and (FR7) no siding laid across it. |

### THE RAILWAY'S WAYS: THE MAIN LINE, ITS SIDINGS AND ITS BRANCHES (lines 1397-1708)

| line | len | member | says |
|---:|---:|---|---|
| 1478 | 1 | **type** `private record Way(long sx, long sy, int[][] legs, int plots)` | A way laid for a train: its start plot on the net, its legs {heading, steps}, and the plots it lays. |
| 1481 | 1 | **type** `private record Branch(long px, long py, int heading, int steps, long[] box)` | A branch to a yard: where it leaves the main line, its heading, its steps, and the yard's box. |
| 1493 | 69 | **type** `private final class Ctx` | What one layRail() run builds on demand and its steps share: the bulk works merged where they touch, a grid of every box a way must keep clear of (a city of billions has some two hundred thousand mined sites), the mai... |
| 1504 | 4 | `Ctx(List<long[]> mines, List<long[]> works)` _(in CityRuns.Ctx)_ |  |
| 1510 | 4 | `List<long[]> bulk()` _(in CityRuns.Ctx)_ | The bulk works' boxes, merged where they touch (mergeTouching()). |
| 1516 | 4 | `BoxGrid blocked()` _(in CityRuns.Ctx)_ | Every mine's and works' box, in a grid. |
| 1522 | 4 | `List<long[]> line()` _(in CityRuns.Ctx)_ | The main line's plots a siding or a branch may leave from (linePlots()), as the yards stand now. |
| 1528 | 7 | `Groups groups()` _(in CityRuns.Ctx)_ | The works' groups, served as the yards laid are. |
| 1537 | 4 | `void yardLaid(long[] y)` _(in CityRuns.Ctx)_ | Another yard laid: the line has a box on it, and the groups beside it are served. |
| 1543 | 18 | `long[] nearestMineSpot(int w, int h, Ground g)` _(in CityRuns.Ctx)_ | The next yard box of w x h on the track nearest a mine that fits (yardSpots(), kept for the run: the old rule, each size's list gone through once); null for none. |
| 1573 | 31 | `private void layRail(long trackPlots, List<int[]> sizes, int spurs, Ground g, long rx, long ry, List<long[]> mines, List<long[]...` | Lays the railway to the model's track plots, yards and spurs (layTo()'s rail half; each yard YARD_LONG along its line, or as short as the ground allows, THE RAILWAY'S YARDS): the derived arms off, the main line grown ... |
| 1606 | 12 | `private long railStampOf(long trackPlots, List<int[]> sizes, int spurs, Ground g, long rx, long ry, List<long[]> mines, List<lo...` | A hash of everything layRail() reads: the track plots, the yards' sizes, the spurs, the ground's version, the gateway, the hub's seed, the mines and works, and the highways laid. |
| 1626 | 13 | `private void dropWays()` | Takes off what an earlier run laid beyond the main line: the arms of the BRANCH and SIDING corridors and any corridor past the spine's (a net grown before FR7 has them as dead ends), and then the spine's arms past the... |
| 1649 | 17 | `private boolean cleanSpine()` | Takes the spine's arms past the main line's end back to nothing: the main line is the gate arm and, where the gate arm waits at the sea, the arms after it as far as the first that waits anywhere else (spineOrder(), gr... |
| 1668 | 15 | `static long countPlots(Net n)` | The plots of net n: its hub's, if laid, and the union of its arms' (each 45-degree step's corner too). |
| 1685 | 3 | `private void wayCorridors()` | The corridors a branch and a siding belong to, there once a way is laid: placeholders after the spine's two (they run along no line of their own). |
| 1690 | 18 | `private int layWay(int corridor, long sx, long sy, int[][] legs)` | Lays a way from the net's plot (sx, sy) as an arm of `corridor`: each leg's plots, one by one, counted where nothing else of the net has them; the arm's index. |

### boxes: a grid of them, the works merged where they touch, the works in groups (lines 1709-1891)

| line | len | member | says |
|---:|---:|---|---|
| 1715 | 39 | **type** `private static final class BoxGrid` | A grid of boxes {x0, y0, x1, y1} (inclusive) BOX_GRID plots a side: whether one covers a plot, or meets another box. |
| 1719 | 3 | `BoxGrid(List<long[]>...lists)` _(in CityRuns.BoxGrid)_ |  |
| 1723 | 3 | `private static long key(long gx, long gy)` _(in CityRuns.BoxGrid)_ |  |
| 1727 | 7 | `void add(long[] b)` _(in CityRuns.BoxGrid)_ |  |
| 1735 | 6 | `boolean covers(long x, long y)` _(in CityRuns.BoxGrid)_ |  |
| 1743 | 10 | `boolean meets(long x0, long y0, long x1, long y1)` _(in CityRuns.BoxGrid)_ | Whether any box meets the plots x0..x1, y0..y1. |
| 1762 | 44 | `static List<long[]> mergeTouching(List<long[]> boxes)` | The works boxes {x0, y0, x1, y1} with those that touch (a gap of at most one plot between them) merged, transitively, into their bounding box - a large pit on twenty sites is one works. |
| 1808 | 3 | `private static long areaOf(long[] w)` | A works box's plots. |
| 1819 | 72 | **type** `private static final class Groups` | The works' groups (FR7 slice 3): works are one group while their box middles lie within GROUP_LINK of each other, link by link; a group is as large as its boxes' plots, its middle their plots' mean. |
| 1825 | 47 | `Groups(List<long[]> works)` _(in CityRuns.Groups)_ |  |
| 1874 | 10 | `void serve(double x, double y)` _(in CityRuns.Groups)_ | Marks every group within YARD_APART of (x, y) served. |
| 1886 | 4 | `double[] largest()` _(in CityRuns.Groups)_ | The largest group no yard serves, its middle {x, y}; null for none. |

### the sidings (lines 1892-2089)

| line | len | member | says |
|---:|---:|---|---|
| 1907 | 36 | `private void laySidings(int wanted, Ctx ctx, Ground g, long[] budget)` | Lays a siding for each of `wanted` Rail Spurs (FR7, D14): the largest bulk works (by its box's plots) within SIDING_REACH of the main line that has none and that the line does not already touch, a track from the neare... |
| 1948 | 15 | `private List<long[]> linePlots()` | The plots of the main line a siding or a branch may leave from, {x, y, axis (0 east-west, 1 north-south)}: the spine's arms past their start plots, on a stretch running straight east-west or north-south, and not insid... |
| 1964 | 4 | `private boolean inAYard(long x, long y)` |  |
| 1970 | 4 | `private static double distanceTo(long[] w, long x, long y)` | The distance from plot (x, y) to a works box {x0, y0, x1, y1}: 0 inside it, else along the axes or the corner. |
| 1981 | 29 | `private Way sidingTo(long[] w, List<long[]> line, Ground g, Ctx ctx)` | The shortest siding from the line to works w, or null: every plot of the line, its nearest plot beside the box, and the two ways of one turn, in order of length (then straight before turned, then the line's order), th... |
| 2012 | 8 | `private static int[][] legsOf(long px, long py, long ex, long ey, boolean northSouthFirst)` | The legs from (px, py) to (ex, ey): east-west then north-south, or (`northSouthFirst`) the other way; a leg of no length left out. |
| 2026 | 12 | `private boolean wayClear(long px, long py, int[][] legs, Ground g, BoxGrid blocked)` | Whether a way's plots, after its start, are all free: owned dry ground, in no works' or mine's box, no yard, off every highway's plot and its verge (the plots beside it), and on no track of the net. |
| 2040 | 8 | `public List<long[]> sidings()` | The sidings laid, each {arm index, start x, start y, end x, end y, plots}, in the order laid: the arms of the SIDING corridor with steps. |
| 2050 | 5 | `public int sidingsLaid()` | How many sidings the runs have laid, into mined sites: the Rail Spurs they took (batch FR7 slice 5; the map deals the rest of the spurs to the plans' works, CityMap.sidingsOf()). |
| 2064 | 15 | `public java.util.Set<Long> mainLineCells(int side)` | The cells of a grid of `side` plots a side, each as (cx << 32) ^ (cy & 0xffffffff), whose own plots (not their first row or column, which the districts share) hold a plot of the main line - the spine arms of the corri... |
| 2081 | 8 | `public List<long[]> branches()` | The branches laid, each {arm index, start x, start y, end x, end y, plots}, in the order laid: the arms of the BRANCH corridor with steps. |

### a further yard, and its branch (lines 2090-2282)

| line | len | member | says |
|---:|---:|---|---|
| 2093 | 13 | `private double distanceToNet(double x, double y)` | The distance from the point (x, y) to the railway laid (its hub and its spine's and branches' stretches), the segment each stretch is. |
| 2117 | 26 | `private boolean placeFurther(int[] size, Ground g, Ctx ctx, long[] budget)` | A further yard (FR7): beside the largest group of works a district from every yard laid (Groups.largest()), not beside the mine nearest the hub. |
| 2145 | 16 | `private boolean yardFree(long[] b, Ground g, Ctx ctx)` | yardFits() with the mines' boxes in the run's grid, for the many boxes a further yard weighs. |
| 2168 | 8 | `private void reachYard(long[] yard, Ground g, Ctx ctx, long[] budget)` | The branch for a yard laid already that the track does not reach (the branches are laid afresh with the rest): the shortest straight arm off the main line to its box, paid for out of `budget`; none if the yard has tra... |
| 2184 | 8 | `private Branch bestBranch(int w, int h, double gx, double gy, Ground g, Ctx ctx, long budget)` | The best branch to a new yard of w x h beside the point (gx, gy): from each plot of the main line whose cross line is a railway line (RAIL_AT), along that line toward the point, to a yard box in the cells beside the p... |
| 2194 | 27 | `private Branch bestBranchAt(int w, int h, int tier, double gx, double gy, Ground g, Ctx ctx, long budget)` | ...at one tier (tierAlong()): the yard boxes of that length only; null where none is free, within `budget` and reached by a branch. |
| 2223 | 21 | `private Branch branchTo(long[] box, Ground g, Ctx ctx)` | The shortest branch to a yard's box from the main line, or null: from a plot of the line on a railway line through the box, along it, as branchSteps() clears. |
| 2252 | 24 | `private int branchSteps(long px, long py, int heading, long[] box, Ground g, BoxGrid blocked)` | The steps of a branch from (px, py) along `heading` to the far edge of a yard's box, or -1: the box wholly beyond the plot, its line through it, and every plot owned dry ground, in no mine's or works' box nor another ... |
| 2278 | 4 | `private boolean trackIn(long[] box)` | Whether any plot of the box holds track of the net. |

### THE MAIN LINE (0.8.1, batch FR0; spec-freight 4.2, D14) (lines 2283-2510)

| line | len | member | says |
|---:|---:|---|---|
| 2295 | 12 | `public long[] freightStation()` | The freight station: the yard nearest the rail net's main arm's end (mainArm(): the arm the railway's world line leaves from, else the gate arm; its x, y), by the yard box's middle, the first in yards' order on a tie. |
| 2315 | 14 | `private long[] findStation()` | The freight station found afresh (freightStation()'s rule). |
| 2331 | 13 | `private long[] anchorInYard(long[] y)` | The rail net's plot inside a freight station's box nearest its middle; null when the box has none. |
| 2350 | 29 | `static int[] locate(Net n, long x, long y)` | The arm and step of the lowest-index arm of n covering plot (x, y); null if none. |
| 2389 | 13 | `static List<int[]> chain(Net n, int a, int k)` | The arms from step k of arm a back toward the hub, as {arm, step}: a's first, then each next the arm and step its predecessor starts on (locate() of its start plot), ending on an arm of corridor 0 or 1. |
| 2415 | 45 | `public List<int[]> mainLine()` | The main line as ranges of steps, each {arm, from, to} inclusive (step 0 an arm's start plot). |
| 2466 | 44 | `private static long mainInto(Arm a, int from, int to, long x0, long y0, int w, int h, byte[] marks, boolean doubleMain)` | Marks one main-line range's plots inside the box with M_MAIN, and with M_DOUBLE where doubleMain; returns a hash of the range when any of it lies in the box, else 0. |

### the trains' way (0.8.2, batch FR1; spec-freight 4.4) (lines 2511-2668)

| line | len | member | says |
|---:|---:|---|---|
| 2528 | 5 | `public double[][] wayTo(long[] yard)` | The railway's way from its main arm's end (mainArm(): the arm the railway's world line leaves from, else the gate arm) to a yard, as points in plots, each a plot's middle, at the ends and every turn: in along the main... |
| 2540 | 3 | `public double[][] wayToPlot(long px, long py)` | ...to a plot of the net (FR7 slice 3): the way wayTo() finds to a yard's anchor, to any plot - the end of a siding, where a bulk unit train runs straight in from the world line without a yard. |
| 2551 | 38 | `public double[][] wayOn(Net n, int fromArm, long px, long py)` | ...on any net, from the end of arm fromArm (FR5, slice B: the trucks' highway way is this walk on the highways net, from the arm its world line leaves from): wayToPlot() is wayOn(rail, mainArm(), px, py) exactly. |
| 2597 | 3 | `public double[][] highwayWayTo(int fromArm, long px, long py)` | The highways' way from the end of arm fromArm (the arm their world line leaves the city's ground from) to a plot of the highways net, as wayToPlot() finds the railway's (FR5, slice B: the trucks' routes): wayOn(highwa... |
| 2609 | 16 | `public List<double[][]> highwayPolylines()` | The highways' arms as polylines (FX5 slice D), each {xs, ys} in plots: the points of the arm at its ends and at every turn (armPoints()) at the middles of their plots (x + 0.5, y + 0.5), the corners the map rounds wit... |
| 2632 | 9 | `public List<long[]> highwayRamps()` | The highways' ramps at their arms' ends (fill() marks one at each arm that has steps): {x, y, the arm's index} in plots, in the arms' order; none while the highways reach no street (highwaysReach()). |
| 2643 | 10 | `static void armPoints(Arm a, int from, int to, List<long[]> out)` | Adds to out arm a's plots at steps from and to (from <= to) and at every turn between, in the order of its steps. |
| 2655 | 13 | `static long[] plotAt(Arm a, int k)` | The plot at step k of arm a: its start at 0, its end at a.steps and past. |

### THE MONTH: TO THE MODEL'S COUNTS (lines 2669-2721)

| line | len | member | says |
|---:|---:|---|---|
| 2682 | 5 | `public boolean layTo(long highwayPlots, long trackPlots, int yardCount, int yardW, int yardH, Ground g, long fx, long fy, long ...` | Lays the runs to the model's plots of highway and of track, and its yards (w x h each), over the ground: the highways grown, or taken back from their newest end, from the founding site (fx, fy); the railway laid from ... |
| 2694 | 14 | `public boolean layTo(long highwayPlots, long trackPlots, List<int[]> yardSizes, int spurs, Ground g, long fx, long fy, long rx,...` | ...with a yard of its own size each (`yardSizes`, {w, h} in the order the map lays them: Rail Terminals, then Rail Depots), `spurs` Rail Spurs, each a siding into a bulk works, and the bulk works' boxes `works`, each ... |
| 2710 | 11 | `long signature()` | A hash of everything laid. |

### READING THEM: A BOX OF PLOTS (lines 2722-2920)

| line | len | member | says |
|---:|---:|---|---|
| 2736 | 73 | `public long fill(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean yardsOver)` | What the runs lay on the w x h plots from world plot (x0, y0), row by row: into fixed, F_HIGHWAY, F_RAIL, F_RAIL_OVER (the railway over a highway) and, `yardsOver`, F_YARD over every plot of a yard (a plan's ground: n... |
| 2817 | 32 | `public long frameHash(long x0, long y0, int w, int h)` | A hash of what the runs lay on the w x h plots from (x0, y0) - each stretch's steps there, the hub, the arms' ends (their ramps), the yards - without drawing them: what a district's plan is kept against (CityMap.ownSt... |
| 2851 | 14 | `static long[] range(Arm a, int s, long x0, long y0, int w, int h)` | The steps of stretch s of arm a whose plots (or corners) may fall in the box: {first, last}; first > last for none. |
| 2866 | 6 | `private static void put(byte[] fixed, int w, long x, long y, byte code)` |  |
| 2874 | 34 | `private static long stretchInto(Net n, Arm a, int s, long x0, long y0, int w, int h, byte[] fixed, byte[] marks, byte code)` | One stretch's plots inside the box, into fixed and marks; a hash of its part there. |
| 2910 | 3 | `static int diagCode(int h)` | A 45-degree heading's two bits: south-east 0, south-west 1, north-west 2, north-east 3. |
| 2915 | 5 | `public byte at(long x, long y)` | What the runs carry at world plot (x, y): F_YARD, F_RAIL_OVER, F_HIGHWAY, F_RAIL, or 0 - no highway while the net reaches no street (highwaysReach(), FX2). |

### THE SIDECAR (lines 2921-3063)

| line | len | member | says |
|---:|---:|---|---|
| 2926 | 3 | `public int runs()` | The sidecar's count of runs: the arms of both nets, and one for the yards (0: none laid). |
| 2931 | 3 | `void write(DataOutputStream out) throws IOException` | Writes the runs after their count (CityMap's FORMAT 5): each net's hub, corridors, arms stretch by stretch and interchanges; the yards (and since FORMAT 10 the footprints they were laid for); what was short. |
| 2939 | 38 | `void write(DataOutputStream out, int format) throws IOException` | ...in the sidecar format given: from FORMAT 10 (FX5 Y1) each yard's box is followed, after all the boxes, by the footprint it was laid for, two ints (w, h) each - the size it was asked for, which a box laid long or sh... |
| 2979 | 3 | `static CityRuns read(DataInputStream in) throws IOException` | The runs write() wrote, read back; null when they do not add up. |
| 2984 | 57 | `static CityRuns read(DataInputStream in, int format) throws IOException` | ...from a sidecar of the format given: before FORMAT 10 a yard's footprint is its box's own width and height, as it was laid then. |
| 3043 | 20 | `public boolean same(CityRuns o)` | Whether two cities' runs are the same: the gateway, each net's hub, plots, corridors and arms stretch for stretch, and the yards. |

