# CityRuns.java - 2,770 lines · 133 methods · 37 constants · model

`ham/citybuildersim/CityRuns.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

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
>             mine, its box across the line (spec 2.8);
> ... (31 more lines in the source)

**Uses:** [World](World.md) (41), [CityMap](CityMap.md) (16), [InterchangeGround](InterchangeGround.md) (12), [TilePainter](TilePainter.md) (6), [DistrictPlan](DistrictPlan.md) (4), [BuildingVisual](BuildingVisual.md) (2)

**Used by (14):** [CityMap](CityMap.md), [DistrictPlan](DistrictPlan.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GatewayCheck](GatewayCheck.md), [InterchangeGround](InterchangeGround.md), [JunctionCheck](JunctionCheck.md), [LandMap](LandMap.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [RailDrawCheck](RailDrawCheck.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md)

## Sections

| line | section |
|---:|---|
| 108 | THE DIALS |
| 183 | THE GROUND THE RUNS READ |
| 202 | A NET: ITS HUB, CORRIDORS AND ARMS |
| 465 | WHERE A NET'S PLOTS ARE |
| 595 | GROWING A NET |
| 1018 | TAKING FROM THE NEWEST END |
| 1104 | THE RAILWAY'S YARDS |
| 1229 | THE RAILWAY'S WAYS: THE MAIN LINE, ITS SIDINGS AND ITS BRANCHES |
| 1517 | · boxes: a grid of them, the works merged where they touch, the works in groups |
| 1700 | · the sidings |
| 1898 | · a further yard, and its branch |
| 2079 | THE MAIN LINE (0.8.1, batch FR0; spec-freight 4.2, D14) |
| 2307 | · the trains' way (0.8.2, batch FR1; spec-freight 4.4) |
| 2403 | THE MONTH: TO THE MODEL'S COUNTS |
| 2456 | READING THEM: A BOX OF PLOTS |
| 2655 | THE SIDECAR |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 113 | `CityRuns.CELL` | `World.TILE` | A cell's side: a tile, 32 plots (DistrictPlan.CELL). |
| 116 | `CityRuns.HIGHWAY_AT` | `DistrictPlan.MIDDLE + 1` | Where a highway rides in its cell: 16 plots in, the street line through its middle (spec 2.7; DistrictPlan's MIDDLE line, 15 into the interior), so it never takes an arterial's row. |
| 119 | `CityRuns.RAIL_AT` | `DistrictPlan.LATTICE` | ...and the railway: 8 plots in, the street line a quarter in (spec 2.8; the lattice's first line, DistrictPlan.LATTICE) - never a highway's row. |
| 122 | `CityRuns.GATEWAY_UNKNOWN` | `- 2` | A city whose gateway has not been looked for yet (a map's runs read from a sidecar older than FORMAT 8). |
| 125 | `CityRuns.NO_GATEWAY` | `- 1` | A city looked for and found to have no gateway: an island founding with no land route to the world (spec 4.1). |
| 128 | `CityRuns.APART` | `CityMap.DISTRICT` | Parallel corridors lie one district apart: 256 plots, 7.68 km (spec 2.7; hw.py's APART). |
| 131 | `CityRuns.LOOK` | `6` | How far ahead an arm must see its way clear to step: 6 plots (hw.py's _ahead(..., 6)) - so it stops short of the sea and the city's edge. |
| 134 | `CityRuns.SIDE_LOOK` | `159` | How far an arm at the sea looks along each 45-degree way, to turn toward the one with more ground: 159 plots (hw.py's score, range(1, 160)). |
| 137 | `CityRuns.STRAIGHT_COST` | `1` | What a step straight on costs, in plots of straight: 1 (spec 2.7, H2). |
| 140 | `CityRuns.TURN_COST` | `40` | ...a 45-degree turn: about 40 (spec 2.7, H2: "est., dials to tune"). |
| 143 | `CityRuns.JUNCTION_COST` | `200` | ...a junction: about 200 (spec 2.7, H2). |
| 146 | `CityRuns.TWIN_NEAR` | `40` | A run off its heading stops short of another arm's plot this near beside it, either side: 40 plots (hw.py's no parallel twin, range(2, 40)). |
| 149 | `CityRuns.RAMP_EVERY` | `2` | Ramps where a highway crosses every RAMP_EVERY-th arterial: every other one, 1.92 km (spec 2.7); and (FX2) where each arm of the spine first crosses one. |
| 152 | `CityRuns.HUB_REACH` | `CityMap.TILES_A_SIDE` | A hub is sought among its lines' crossings within this many cells of where its net starts, each way, the nearest first: 8 - a district - where its own crossing is not the city's dry ground. |
| 155 | `CityRuns.READ_AHEAD` | `CELL` | An arm reads its way ahead this far at a time and steps on what it read: a cell, 32 plots - so a step reads a plot, not LOOK. |
| 158 | `CityRuns.DX` | `{ 1, 1, 0, - 1, - 1, - 1, 0, 1 }, DY = { 0, 1, 1, 1, 0, - 1, - 1, - 1 }` | The eight headings, hw.py's DIRS: east, south-east, south, south-west, west, north-west, north, north-east. |
| 161 | `CityRuns.HIGHWAYS` | `0, RAILWAY = 1` | A net's kinds. |
| 164 | `CityRuns.F_HIGHWAY` | `BuildingVisual.HIGHWAY` | What a plot carries (fill()): a highway's plot (BuildingVisual.HIGHWAY, DistrictPlan.FIXED_HIGHWAY)... |
| 166 | `CityRuns.F_RAIL` | `TilePainter.RAIL` | ...the railway's track (TilePainter.RAIL, DistrictPlan.FIXED_RAIL)... |
| 168 | `CityRuns.F_RAIL_OVER` | `6` | ...the railway on a bridge over a highway (spec 2.8)... |
| 170 | `CityRuns.F_YARD` | `7` | ...a yard's ground, its track among it (DistrictPlan.FIXED_YARD): no street crosses it, no other building stands on it. |
| 172 | `CityRuns.F_RESERVE` | `8` | ...and (batch FR7, D19) an interchange's reserve, a plan's ground only (fill() with yardsOver; InterchangeGround): street ground the plan keeps clear of lots, buildings and streets (DistrictPlan.FIXED_RESERVE) - never... |
| 175 | `CityRuns.M_RAMP` | `1` | A plot's marks (fill()'s second array): a highway's ramp (spec 2.7)... |
| 177 | `CityRuns.M_DIAG` | `2` | ...a plot of a 45-degree stretch, drawn smooth, its heading's two bits from M_DIAG_SHIFT (diagCode())... |
| 179 | `CityRuns.M_DIAG_SHIFT` | `2` | ...the bit that heading's two bits start at: bits 2 and 3, between M_DIAG and M_CORNER... |
| 181 | `CityRuns.M_CORNER` | `16` | ...and of those, a staircase's corner: the plot beside the stretch's line, (x + dx, y) of a step from (x, y). |
| 341 | `CityRuns.RESERVES_KEPT` | `8192` | The most regions' sites kept at once: 8,192 - a screen's tiles and the plans' frames, many times over. |
| 600 | `CityRuns.OPEN` | `0, EDGE = 1, SEA = 2` | What stops a way: nothing, the city's edge, or the sea (and fresh water wider than the net bridges). |
| 727 | `CityRuns.STEPPED` | `0, WAITS = 1, JOINED = 2, HELD = 3, YIELDS = 4` | What a step did: stepped on; waits on the ground (tried again when the ground moves); stopped for good (joined); held for want of a second plot; or waits on the other net (tried again next time). |
| 1283 | `CityRuns.BRANCH` | `2, SIDING = 3` | The corridors of the arms laid for a train's way: a branch to a further yard (2) and a siding into a works (3). |
| 1286 | `CityRuns.SIDING_REACH` | `CityMap.DISTRICT` | A siding serves a works within this of the main line, in plots: one district (CityMap.DISTRICT, 256; spec-freight 4.3, "the largest BULK works within one district of the line"). |
| 1289 | `CityRuns.SERVED_WITHIN` | `1` | A works the main line touches or runs beside, within this many plots, has the railway at its door and needs no siding: 1. |
| 1292 | `CityRuns.GROUP_LINK` | `CityMap.DISTRICT / 2` | A group of rail-served works is the works whose box middles lie within this of each other, link by link: half a district, 128 plots. |
| 1295 | `CityRuns.YARD_APART` | `CityMap.DISTRICT` | A further yard goes beside a group of works that lies more than this from every yard laid: a district, 256 plots (FR7: "more than one district from every yard laid"). |
| 1298 | `CityRuns.BRANCH_FROM` | `CityMap.DISTRICT` | A group of works farther than this from the track is reached by a branch off the main line, else the yard sits on the track nearest it: a district, 256 plots. |
| 1520 | `CityRuns.BOX_GRID` | `64` | The boxes' grid: 64 plots a side. |
| 1753 | `CityRuns.SIDING_TRIES` | `200` | The most works laySidings() tries in one run: 200. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 208 | `final int corridor, home` |  |
| 209 | `final long sx, sy` |  |
| 210 | `int head` |  |
| 211 | `long x, y` |  |
| 212 | `int steps` |  |
| 213 | `boolean joined` |  |
| 215 | `int segs` | Its stretches: each one's heading, steps, and start (the plot before its first step). |
| 216 | `int[] segHead` |  |
| 217 | `long[] segX` |  |
| 219 | `final List<long[]> crossings` | Where it crossed the net (interchanges), each {x, y, the step that made it}, in the order made. |
| 221 | `int known` | Steps known clear ahead along `head`, read at the ground's version knownAt; and the version it waits at (transient). |
| 222 | `long knownAt` |  |
| 224 | `int stoppedBy` | What stopped it the last time it waited with no way along (transient): OPEN (nothing, or another reason), EDGE or SEA. |
| 253 | `final int kind, at, bridge` |  |
| 255 | `boolean started, hubLaid` | Its hub chosen, and whether the hub's plot is laid (a railway may stand for its yards with no track). |
| 256 | `long hubX, hubY` |  |
| 258 | `final List<long[]> corridors` | Its corridors, {0 a row \| 1 a column, the line's coordinate}, in the order opened. |
| 259 | `final List<Arm> arms` |  |
| 260 | `final List<Interchange> interchanges` |  |
| 262 | `long plots` | Plots laid: the union of its arms' plots and its hub's. |
| 264 | `long stuckAt` | The ground's version at which it last could lay no more (transient): it waits until the ground moves. |
| 266 | `final Map<Long, long[]> buckets` | Its stretches by district: district key -> {arm << 24 \| stretch} entries, and how many. |
| 267 | `final Map<Long, Integer> bucketN` |  |
| 310 | `final Net highways` |  |
| 311 | `final Net rail` |  |
| 314 | `int gateway` | The gateway's bearing, city-wide (the highway's and the railway's share it): GATEWAY_UNKNOWN until the map looks, NO_GATEWAY where it finds none. |
| 317 | `final List<long[]> yards` | The railway's yards: each {x0, y0, w, h} in world plots, in the order laid. |
| 329 | `long highwayShort, trackSurplus` | What the model has that the runs could not lay: highway plots, yards (counted: the legend says so; a yard with no place is drawn by its district's plan) - and (FR7 2026-10-18, batch FR7 slice 3) the track plots the mo... |
| 330 | `int yardsShort` |  |
| 333 | `private Ground reserveGround` | The ground the interchanges' reserves are found over, and the works they keep clear of (batch FR7, InterchangeGround): bound by layTo() and the map, never saved. |
| 334 | `private InterchangeGround.Guard reserveGuard` |  |
| 337 | `private final Map<List<Long>, List<InterchangeGround.Site>> reserveKept` | The sites found, by the region asked about, against the stamp they were found at: the runs, the ground's and the works' versions - none kept past a change of any. |
| 338 | `private long reserveStamp` |  |
| 405 | `long version` | A stamp of everything laid: what the map's plans and views are kept against. |
| 408 | `private boolean doubleMain` | Whether the main line is double track: the city has a Freight Line (spec 4.3). |
| 411 | `private List<int[]> mainLineCache` | mainLine()'s last answer, and the version and the main arm it was computed at. |
| 412 | `private long mainLineCacheAt` |  |
| 413 | `private int mainLineCacheArm` |  |
| 416 | `private boolean highwaysReachCache` | highwaysReach()'s last answer, and the version it was computed at. |
| 417 | `private long highwaysReachCacheAt` |  |
| 420 | `private int mainArm` | The rail net's arm the main line runs out along and the trains come in on: the arm the railway's world line leaves the city's ground from (CityMap.worldLines() sets it, FX2), or -1 for the gate arm. |
| 1307 | `private long railStamp` | layRail()'s last stamp, and whether it has one: everything it reads, so it lays nothing twice for the same inputs. |
| 1308 | `private boolean railStampSet` |  |
| 1317 | `final List<long[]> mines, works` |  |
| 1318 | `private List<long[]> bulk, line` |  |
| 1319 | `private BoxGrid blocked` |  |
| 1320 | `private Groups groups` |  |
| 1321 | `private final Map<Long, List<long[]>> spots` |  |
| 1322 | `private final Map<Long, Integer> spotAt` |  |
| 1524 | `private final Map<Long, List<long[]>> cells` |  |
| 1628 | `final double[][] g` |  |
| 1629 | `final boolean[] served` |  |
| 1630 | `private final Map<Long, List<Integer>> cells` |  |
| 1631 | `private int next` |  |
| 2105 | `private long[] station` | freightStation()'s last answer, the version, the gateway and the main arm it was found at. |
| 2106 | `private long stationAt` |  |
| 2107 | `private int stationAtGateway` |  |
| 2108 | `private int stationAtArm` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 106 | 2665 | **type** `public final class CityRuns` | The city's highways and railway as runs on corridors: each laid month by month from its hub's lines out, straight by preference, never moved, with the railway's yards on its track nearest the mines - what the city map... |

### THE DIALS (lines 108-182)

### THE GROUND THE RUNS READ (lines 183-201)

| line | len | member | says |
|---:|---:|---|---|
| 188 | 8 | **type** `public interface Ground` | The ground runs are laid on: the city map's (CityMap.RunGround). |
| 190 | 1 | `int at(long x, long y, int dir)` _(in CityRuns.Ground)_ | The ground at world plot (x, y), read along heading `dir` (0 to 7, or -1): a World class, or -1 where the city does not own it. |
| 192 | 1 | `long version()` _(in CityRuns.Ground)_ | A number that moves whenever the city's ground does (a purchase): what a waiting arm and a stuck net wait on. |
| 194 | 1 | `long[] box()` _(in CityRuns.Ground)_ | The city's owned box, {x0, y0, x1, y1} in plots, inclusive: how far out a corridor is sought. |
| 198 | 3 | `static boolean dry(int g)` | Whether a ground class is the city's dry ground: owned (a class at all, not -1), and dry by World.dry() - not water, and since 0.7.127 not ice. |

### A NET: ITS HUB, CORRIDORS AND ARMS (lines 202-464)

| line | len | member | says |
|---:|---:|---|---|
| 207 | 40 | **type** `static final class Arm` | One arm: from its start along its corridor, its straight stretches in the order laid. |
| 226 | 4 | `Arm(int corridor, int home, long sx, long sy)` _(in CityRuns.Arm)_ |  |
| 232 | 4 | `void push(int h)` _(in CityRuns.Arm)_ | A step along h from where it stands: its stretch lengthened, or a new one begun. |
| 237 | 9 | `void append(int h, int steps, long fromX, long fromY)` _(in CityRuns.Arm)_ |  |
| 249 | 1 | **type** `record Interchange(long x, long y, int corridor)` | A corridor's start: where it began on the net (an interchange; the spine's at the hub). |
| 252 | 57 | **type** `public static final class Net` | One net: the highways or the railway. |
| 269 | 1 | `Net(int kind, int at, int bridge)` _(in CityRuns.Net)_ |  |
| 272 | 1 | `public long plots()` _(in CityRuns.Net)_ | Plots laid. |
| 274 | 1 | `public int corridors()` _(in CityRuns.Net)_ | Corridors opened. |
| 276 | 1 | `public int interchanges()` _(in CityRuns.Net)_ | Interchanges: corridors' starts (the hub's among them) and arms' crossings. |
| 278 | 6 | `public List<long[]> interchangePlots()` _(in CityRuns.Net)_ | ...as {x, y}: a harness's. |
| 285 | 1 | `public int arms()` _(in CityRuns.Net)_ | Arms. |
| 287 | 1 | `public long[] hub()` _(in CityRuns.Net)_ | Its hub, {x, y}, or null before it starts. |
| 289 | 5 | `public int bends()` _(in CityRuns.Net)_ | Stretches off their arms' headings: 45-degree runs along a shore or the city's edge. |
| 295 | 1 | `public int joined()` _(in CityRuns.Net)_ | Arms ended where they met the net (joined). |
| 298 | 5 | `public List<long[]> stretches()` _(in CityRuns.Net)_ | Each arm's stretches as {x0, y0, heading, steps} (x0, y0 the plot before its first step), arm by arm: a harness's. |
| 304 | 4 | `void reset()` _(in CityRuns.Net)_ |  |
| 344 | 4 | `public void setReserveGuard(InterchangeGround.Guard guard)` | Sets the works a reserve may not take (the map's mines and shore works). |
| 354 | 4 | `public void layReserves(Ground g)` | Binds the ground the interchanges' reserves are found over and forgets the sites kept: called by layTo() every month, and by the map when it has read its runs back. |
| 366 | 15 | `public List<InterchangeGround.Site> reservesIn(long x0, long y0, int w, int h)` | The interchange sites whose box meets the w x h plots from (x0, y0), each with whether it got its reserve (InterchangeGround): found when asked, over the region and a margin, and kept against the runs, the ground and ... |
| 383 | 13 | `public List<InterchangeGround.Site> reserves()` | Every interchange site of the highways, over the box their plots lie in: a harness's, for a city a screen's size - a city of billions asks reservesIn() of a plan or a tile. |
| 398 | 5 | `public List<InterchangeGround.Site> reservesLaid()` | The reserves that were laid (Site.got()), of reserves(). |
| 422 | 1 | `public Net highways()` |  |
| 423 | 1 | `public Net rail()` |  |
| 424 | 1 | `public List<long[]> yards()` |  |
| 425 | 1 | `public long highwayShort()` |  |
| 428 | 1 | `public long highwayUndrawn()` | The highway plots the model has that the map draws nowhere: those the runs could not lay (highwayShort()), and (FX2) all the net's while it reaches no street (highwaysReach()), laid but not drawn - the legend's. |
| 430 | 1 | `public long trackSurplus()` | The track plots the model has that the railway's ways do not use and the map does not draw (FR7): the model's less the main line's, the sidings' and the branches' - the legend's "plots of track more than the line uses". |
| 431 | 1 | `public int yardsShort()` |  |
| 432 | 1 | `public long version()` |  |
| 435 | 1 | `public boolean doubleMain()` | Whether the main line is double track: the city has a Freight Line (spec 4.3). |
| 438 | 1 | `public void setDoubleMain(boolean doubleMain)` | Sets whether the main line is double track (the map's, from its Freight Line count). |
| 441 | 1 | `public int gateway()` | The gateway's bearing: GATEWAY_UNKNOWN until the map looks, NO_GATEWAY where it finds none. |
| 444 | 5 | `void setGateway(int bearing)` | Sets the gateway's bearing (the map's, once it has looked). |
| 451 | 1 | `public void setMainArm(int arm)` | Sets the arm the main line runs out along (the map's, from where the railway's world line leaves the city's ground): -1 for the gate arm. |
| 458 | 6 | `public int mainArm()` | The rail net's main arm: -1 with no gateway or before the railway starts; else the arm the map set (setMainArm()) while it is an arm of the net with steps laid, else the gate arm. |

### WHERE A NET'S PLOTS ARE (lines 465-594)

| line | len | member | says |
|---:|---:|---|---|
| 469 | 3 | `static long districtKey(long x, long y)` |  |
| 474 | 12 | `static void file(Net n, int a, int s, long x, long y)` | Files stretch s of arm a under the district of plot (x, y). |
| 488 | 20 | `static boolean onStretch(Arm a, int s, long px, long py)` | Whether stretch s of arm a covers world plot (px, py): one of its steps' plots, or a 45-degree step's corner. |
| 510 | 14 | `static int coverer(Net n, long x, long y, int not)` | The arm of net n covering plot (x, y) other than `not` (-1 for any): its index, -2 for the hub, -1 for none. |
| 526 | 3 | `public static boolean covered(Net n, long x, long y)` | Whether net n has a plot at (x, y). |
| 542 | 14 | `public boolean highwaysReach()` | Whether the highways net is drawn: it has started and it has a ramp onto an arterial where stretchInto() marks M_RAMP - a straight stretch's step on a multiple of CELL that is every RAMP_EVERY-th cell line, or an arm ... |
| 558 | 12 | `static boolean onRampLine(Arm a, int s)` | Whether straight stretch s of arm a has a step (1 to its steps) on a ramp line: its coordinate along the stretch (x going east or west, y going north or south) a multiple of CELL whose cell line is every RAMP_EVERY-th... |
| 578 | 16 | `static long firstLineStep(Arm a)` | The step (1 to its steps) at which arm a's straight stretches first put a plot on a cell line along their heading - x a multiple of CELL going east or west, y going north or south - or -1 for none: where an arm of the... |

### GROWING A NET (lines 595-1017)

| line | len | member | says |
|---:|---:|---|---|
| 609 | 36 | `static int clear(Net n, Ground g, long x, long y, int d, int k, int[] why)` | The steps along heading d from (x, y) the net may take, up to k (or a bridge's past it), and into why[0] what stops it: ground the city does not own, the sea, water wider than its bridge, a staircase's corner off the ... |
| 647 | 5 | `static void lay(Net n, int a, int s, long x, long y)` | Lays net n's plot (x, y) on arm a's stretch s: counted where nothing else of the net covers it yet (an arm never covers its own new plot: it never turns back). |
| 661 | 48 | `static boolean start(Net n, Ground g, long ax, long ay, int gateway)` | Chooses net n's hub: the line crossing nearest (ax, ay) that is the city's dry ground, within HUB_REACH cells; false where none is. |
| 711 | 14 | `static int gateArm(Net n, int gateway)` | The spine's arm toward the gateway: -1 when there is none or the net has not started; else the index of the arm of corridor 0 or 1 whose home heading's angle (heading h is h * 45 degrees, x = cos, y = sin, as DX/DY) i... |
| 736 | 41 | `static boolean grow(Net n, Net other, long target, Ground g, long ax, long ay, int gateway)` | Grows net n toward `target` plots (hw.py's grow()): its hub laid first, then each plot to the shortest arm of the earliest corridor that can still grow - the gateway's arm (gateArm()) before the rest of its corridor -... |
| 784 | 13 | `static List<Integer> spineOrder(Net n, int gate)` | The spine's arms in the order the railway's main line is grown (FR7, batch FR7 slice 3): the gate arm, then the other arm of its corridor, then the other corridor's by index. |
| 809 | 28 | `static boolean growMainLine(Net n, Net other, long target, Ground g, long ax, long ay, int gateway)` | Grows the railway's main line toward `target` plots (FR7, batch FR7 slice 3): its hub laid first, then the gate arm alone, until it waits - at the city's edge, where the world line takes it on. |
| 845 | 70 | `static int step(Net n, Net other, int i, Arm a, Ground g, long target, int[] why)` | One step of arm i (hw.py's _step, with the cost rule): STEPPED; WAITS (the city's edge, the sea with no way along it, a T it does not cross, the other net's run along its way); JOINED (it met the net running its own w... |
| 923 | 36 | `static boolean newCorridor(Net n, Ground g)` | The next corridor (hw.py's _new_corridor()): the parallel line k districts out, k from 1, that crosses the net already laid on the city's ground - at its plot there nearest the hub, an interchange - the nearest of tho... |
| 961 | 7 | `static boolean before(double d, int kind, long v, long[] p, double d2, int kind2, long v2, long[] p2)` | hw.py's order of candidates (distance, kind - 'col' before 'row' -, line, plot): whether the first comes before the second. |
| 970 | 47 | `static long[] nearestOnLine(Net n, Ground g, int kind, long v)` | The plot of net n on the row (kind 0) or column (1) at v, on the city's ground, nearest its hub; null for none. |

### TAKING FROM THE NEWEST END (lines 1018-1103)

| line | len | member | says |
|---:|---:|---|---|
| 1023 | 59 | `static boolean trim(Net n, long target, int gateway)` | Takes net n back to `target` plots, each from the newest end - the latest corridor's longest arm, the gateway's arm after the rest of its corridor, its last step (the reverse of grow()'s order); the hub last. |
| 1084 | 19 | `static void rebuildBuckets(Net n)` | Files every stretch of net n again (after a corridor is closed, or the runs read back). |

### THE RAILWAY'S YARDS (lines 1104-1228)

| line | len | member | says |
|---:|---:|---|---|
| 1119 | 6 | `private void keepYards(List<int[]> sizes)` | Takes the yards back to the longest run, from the first, whose boxes are the sizes the model now has (FR7 slice 3, 2026-10-18): a Rail Terminal's box and a Rail Depot's are not one size, and until now every yard was l... |
| 1127 | 12 | `List<long[]> yardSpots(int w, int h, List<long[]> mines)` | Every box a yard could take, the nearest a mine first: two a cell the track runs straight through on its line, one at either end of it. |
| 1141 | 9 | `List<long[]> yardSpotsNear(int w, int h, double gx, double gy)` | ...the nearest the point (gx, gy) first (FR7 slice 3: a further yard goes beside a group of works, not beside the mine nearest the hub). |
| 1152 | 9 | `private static List<long[]> sortedBy(List<long[]> out, double[] key)` | The boxes by key, the least first; on a tie the northern, then the western. |
| 1163 | 33 | `private List<long[]> spotsOnTrack(int w, int h)` | Every box a yard of w x h could take on the track, unsorted: two a cell the track runs straight through on its line, one at either end of it; a siding is no line for a yard. |
| 1198 | 12 | `static void spotsIn(List<long[]> out, long cx, long cy, boolean row, int w, int h)` | A cell's yard boxes across its quarter line (a row's, or a column's): inside the cell's interior, covering the line, at either end of the cell. |
| 1212 | 16 | `boolean yardFits(long[] b, Ground g, List<long[]> mines, List<long[]> laid)` | Whether a yard's box is free, the yards `laid` before it: owned dry ground, off the highways and their verges, no other yard's, no mine's site, and (FR7) no siding laid across it. |

### THE RAILWAY'S WAYS: THE MAIN LINE, ITS SIDINGS AND ITS BRANCHES (lines 1229-1516)

| line | len | member | says |
|---:|---:|---|---|
| 1301 | 1 | **type** `private record Way(long sx, long sy, int[][] legs, int plots)` | A way laid for a train: its start plot on the net, its legs {heading, steps}, and the plots it lays. |
| 1304 | 1 | **type** `private record Branch(long px, long py, int heading, int steps, long[] box)` | A branch to a yard: where it leaves the main line, its heading, its steps, and the yard's box. |
| 1316 | 59 | **type** `private final class Ctx` | What one layRail() run builds on demand and its steps share: the bulk works merged where they touch, a grid of every box a way must keep clear of (a city of billions has some two hundred thousand mined sites), the mai... |
| 1324 | 4 | `Ctx(List<long[]> mines, List<long[]> works)` _(in CityRuns.Ctx)_ |  |
| 1330 | 4 | `List<long[]> bulk()` _(in CityRuns.Ctx)_ | The bulk works' boxes, merged where they touch (mergeTouching()). |
| 1336 | 4 | `BoxGrid blocked()` _(in CityRuns.Ctx)_ | Every mine's and works' box, in a grid. |
| 1342 | 4 | `List<long[]> line()` _(in CityRuns.Ctx)_ | The main line's plots a siding or a branch may leave from (linePlots()), as the yards stand now. |
| 1348 | 7 | `Groups groups()` _(in CityRuns.Ctx)_ | The works' groups, served as the yards laid are. |
| 1357 | 4 | `void yardLaid(long[] y)` _(in CityRuns.Ctx)_ | Another yard laid: the line has a box on it, and the groups beside it are served. |
| 1363 | 11 | `long[] nearestMineSpot(int w, int h, Ground g)` _(in CityRuns.Ctx)_ | The next yard box of w x h on the track nearest a mine that fits (yardSpots(), kept for the run: the old rule, each size's list gone through once); null for none. |
| 1383 | 29 | `private void layRail(long trackPlots, List<int[]> sizes, int spurs, Ground g, long rx, long ry, List<long[]> mines, List<long[]...` | Lays the railway to the model's track plots, yards and spurs (layTo()'s rail half): the derived arms off, the main line grown or taken back, the first yard, the sidings, then the branches and the further yards, each o... |
| 1414 | 12 | `private long railStampOf(long trackPlots, List<int[]> sizes, int spurs, Ground g, long rx, long ry, List<long[]> mines, List<lo...` | A hash of everything layRail() reads: the track plots, the yards' sizes, the spurs, the ground's version, the gateway, the hub's seed, the mines and works, and the highways laid. |
| 1434 | 13 | `private void dropWays()` | Takes off what an earlier run laid beyond the main line: the arms of the BRANCH and SIDING corridors and any corridor past the spine's (a net grown before FR7 has them as dead ends), and then the spine's arms past the... |
| 1457 | 17 | `private boolean cleanSpine()` | Takes the spine's arms past the main line's end back to nothing: the main line is the gate arm and, where the gate arm waits at the sea, the arms after it as far as the first that waits anywhere else (spineOrder(), gr... |
| 1476 | 15 | `static long countPlots(Net n)` | The plots of net n: its hub's, if laid, and the union of its arms' (each 45-degree step's corner too). |
| 1493 | 3 | `private void wayCorridors()` | The corridors a branch and a siding belong to, there once a way is laid: placeholders after the spine's two (they run along no line of their own). |
| 1498 | 18 | `private int layWay(int corridor, long sx, long sy, int[][] legs)` | Lays a way from the net's plot (sx, sy) as an arm of `corridor`: each leg's plots, one by one, counted where nothing else of the net has them; the arm's index. |

### boxes: a grid of them, the works merged where they touch, the works in groups (lines 1517-1699)

| line | len | member | says |
|---:|---:|---|---|
| 1523 | 39 | **type** `private static final class BoxGrid` | A grid of boxes {x0, y0, x1, y1} (inclusive) BOX_GRID plots a side: whether one covers a plot, or meets another box. |
| 1527 | 3 | `BoxGrid(List<long[]>...lists)` _(in CityRuns.BoxGrid)_ |  |
| 1531 | 3 | `private static long key(long gx, long gy)` _(in CityRuns.BoxGrid)_ |  |
| 1535 | 7 | `void add(long[] b)` _(in CityRuns.BoxGrid)_ |  |
| 1543 | 6 | `boolean covers(long x, long y)` _(in CityRuns.BoxGrid)_ |  |
| 1551 | 10 | `boolean meets(long x0, long y0, long x1, long y1)` _(in CityRuns.BoxGrid)_ | Whether any box meets the plots x0..x1, y0..y1. |
| 1570 | 44 | `static List<long[]> mergeTouching(List<long[]> boxes)` | The works boxes {x0, y0, x1, y1} with those that touch (a gap of at most one plot between them) merged, transitively, into their bounding box - a large pit on twenty sites is one works. |
| 1616 | 3 | `private static long areaOf(long[] w)` | A works box's plots. |
| 1627 | 72 | **type** `private static final class Groups` | The works' groups (FR7 slice 3): works are one group while their box middles lie within GROUP_LINK of each other, link by link; a group is as large as its boxes' plots, its middle their plots' mean. |
| 1633 | 47 | `Groups(List<long[]> works)` _(in CityRuns.Groups)_ |  |
| 1682 | 10 | `void serve(double x, double y)` _(in CityRuns.Groups)_ | Marks every group within YARD_APART of (x, y) served. |
| 1694 | 4 | `double[] largest()` _(in CityRuns.Groups)_ | The largest group no yard serves, its middle {x, y}; null for none. |

### the sidings (lines 1700-1897)

| line | len | member | says |
|---:|---:|---|---|
| 1715 | 36 | `private void laySidings(int wanted, Ctx ctx, Ground g, long[] budget)` | Lays a siding for each of `wanted` Rail Spurs (FR7, D14): the largest bulk works (by its box's plots) within SIDING_REACH of the main line that has none and that the line does not already touch, a track from the neare... |
| 1756 | 15 | `private List<long[]> linePlots()` | The plots of the main line a siding or a branch may leave from, {x, y, axis (0 east-west, 1 north-south)}: the spine's arms past their start plots, on a stretch running straight east-west or north-south, and not insid... |
| 1772 | 4 | `private boolean inAYard(long x, long y)` |  |
| 1778 | 4 | `private static double distanceTo(long[] w, long x, long y)` | The distance from plot (x, y) to a works box {x0, y0, x1, y1}: 0 inside it, else along the axes or the corner. |
| 1789 | 29 | `private Way sidingTo(long[] w, List<long[]> line, Ground g, Ctx ctx)` | The shortest siding from the line to works w, or null: every plot of the line, its nearest plot beside the box, and the two ways of one turn, in order of length (then straight before turned, then the line's order), th... |
| 1820 | 8 | `private static int[][] legsOf(long px, long py, long ex, long ey, boolean northSouthFirst)` | The legs from (px, py) to (ex, ey): east-west then north-south, or (`northSouthFirst`) the other way; a leg of no length left out. |
| 1834 | 12 | `private boolean wayClear(long px, long py, int[][] legs, Ground g, BoxGrid blocked)` | Whether a way's plots, after its start, are all free: owned dry ground, in no works' or mine's box, no yard, off every highway's plot and its verge (the plots beside it), and on no track of the net. |
| 1848 | 8 | `public List<long[]> sidings()` | The sidings laid, each {arm index, start x, start y, end x, end y, plots}, in the order laid: the arms of the SIDING corridor with steps. |
| 1858 | 5 | `public int sidingsLaid()` | How many sidings the runs have laid, into mined sites: the Rail Spurs they took (batch FR7 slice 5; the map deals the rest of the spurs to the plans' works, CityMap.sidingsOf()). |
| 1872 | 15 | `public java.util.Set<Long> mainLineCells(int side)` | The cells of a grid of `side` plots a side, each as (cx << 32) ^ (cy & 0xffffffff), whose own plots (not their first row or column, which the districts share) hold a plot of the main line - the spine arms of the corri... |
| 1889 | 8 | `public List<long[]> branches()` | The branches laid, each {arm index, start x, start y, end x, end y, plots}, in the order laid: the arms of the BRANCH corridor with steps. |

### a further yard, and its branch (lines 1898-2078)

| line | len | member | says |
|---:|---:|---|---|
| 1901 | 13 | `private double distanceToNet(double x, double y)` | The distance from the point (x, y) to the railway laid (its hub and its spine's and branches' stretches), the segment each stretch is. |
| 1925 | 26 | `private boolean placeFurther(int[] size, Ground g, Ctx ctx, long[] budget)` | A further yard (FR7): beside the largest group of works a district from every yard laid (Groups.largest()), not beside the mine nearest the hub. |
| 1953 | 16 | `private boolean yardFree(long[] b, Ground g, Ctx ctx)` | yardFits() with the mines' boxes in the run's grid, for the many boxes a further yard weighs. |
| 1976 | 8 | `private void reachYard(long[] yard, Ground g, Ctx ctx, long[] budget)` | The branch for a yard laid already that the track does not reach (the branches are laid afresh with the rest): the shortest straight arm off the main line to its box, paid for out of `budget`; none if the yard has tra... |
| 1992 | 25 | `private Branch bestBranch(int w, int h, double gx, double gy, Ground g, Ctx ctx, long budget)` | The best branch to a new yard of w x h beside the point (gx, gy): from each plot of the main line whose cross line is a railway line (RAIL_AT), along that line toward the point, to a yard box in the cells beside the p... |
| 2019 | 21 | `private Branch branchTo(long[] box, Ground g, Ctx ctx)` | The shortest branch to a yard's box from the main line, or null: from a plot of the line on a railway line through the box, along it, as branchSteps() clears. |
| 2048 | 24 | `private int branchSteps(long px, long py, int heading, long[] box, Ground g, BoxGrid blocked)` | The steps of a branch from (px, py) along `heading` to the far edge of a yard's box, or -1: the box wholly beyond the plot, its line through it, and every plot owned dry ground, in no mine's or works' box nor another ... |
| 2074 | 4 | `private boolean trackIn(long[] box)` | Whether any plot of the box holds track of the net. |

### THE MAIN LINE (0.8.1, batch FR0; spec-freight 4.2, D14) (lines 2079-2306)

| line | len | member | says |
|---:|---:|---|---|
| 2091 | 12 | `public long[] freightStation()` | The freight station: the yard nearest the rail net's main arm's end (mainArm(): the arm the railway's world line leaves from, else the gate arm; its x, y), by the yard box's middle, the first in yards' order on a tie. |
| 2111 | 14 | `private long[] findStation()` | The freight station found afresh (freightStation()'s rule). |
| 2127 | 13 | `private long[] anchorInYard(long[] y)` | The rail net's plot inside a freight station's box nearest its middle; null when the box has none. |
| 2146 | 29 | `static int[] locate(Net n, long x, long y)` | The arm and step of the lowest-index arm of n covering plot (x, y); null if none. |
| 2185 | 13 | `static List<int[]> chain(Net n, int a, int k)` | The arms from step k of arm a back toward the hub, as {arm, step}: a's first, then each next the arm and step its predecessor starts on (locate() of its start plot), ending on an arm of corridor 0 or 1. |
| 2211 | 45 | `public List<int[]> mainLine()` | The main line as ranges of steps, each {arm, from, to} inclusive (step 0 an arm's start plot). |
| 2262 | 44 | `private static long mainInto(Arm a, int from, int to, long x0, long y0, int w, int h, byte[] marks, boolean doubleMain)` | Marks one main-line range's plots inside the box with M_MAIN, and with M_DOUBLE where doubleMain; returns a hash of the range when any of it lies in the box, else 0. |

### the trains' way (0.8.2, batch FR1; spec-freight 4.4) (lines 2307-2402)

| line | len | member | says |
|---:|---:|---|---|
| 2324 | 5 | `public double[][] wayTo(long[] yard)` | The railway's way from its main arm's end (mainArm(): the arm the railway's world line leaves from, else the gate arm) to a yard, as points in plots, each a plot's middle, at the ends and every turn: in along the main... |
| 2336 | 39 | `public double[][] wayToPlot(long px, long py)` | ...to a plot of the net (FR7 slice 3): the way wayTo() finds to a yard's anchor, to any plot - the end of a siding, where a bulk unit train runs straight in from the world line without a yard. |
| 2377 | 10 | `static void armPoints(Arm a, int from, int to, List<long[]> out)` | Adds to out arm a's plots at steps from and to (from <= to) and at every turn between, in the order of its steps. |
| 2389 | 13 | `static long[] plotAt(Arm a, int k)` | The plot at step k of arm a: its start at 0, its end at a.steps and past. |

### THE MONTH: TO THE MODEL'S COUNTS (lines 2403-2455)

| line | len | member | says |
|---:|---:|---|---|
| 2416 | 5 | `public boolean layTo(long highwayPlots, long trackPlots, int yardCount, int yardW, int yardH, Ground g, long fx, long fy, long ...` | Lays the runs to the model's plots of highway and of track, and its yards (w x h each), over the ground: the highways grown, or taken back from their newest end, from the founding site (fx, fy); the railway laid from ... |
| 2428 | 14 | `public boolean layTo(long highwayPlots, long trackPlots, List<int[]> yardSizes, int spurs, Ground g, long fx, long fy, long rx,...` | ...with a yard of its own size each (`yardSizes`, {w, h} in the order the map lays them: Rail Terminals, then Rail Depots), `spurs` Rail Spurs, each a siding into a bulk works, and the bulk works' boxes `works`, each ... |
| 2444 | 11 | `long signature()` | A hash of everything laid. |

### READING THEM: A BOX OF PLOTS (lines 2456-2654)

| line | len | member | says |
|---:|---:|---|---|
| 2470 | 73 | `public long fill(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean yardsOver)` | What the runs lay on the w x h plots from world plot (x0, y0), row by row: into fixed, F_HIGHWAY, F_RAIL, F_RAIL_OVER (the railway over a highway) and, `yardsOver`, F_YARD over every plot of a yard (a plan's ground: n... |
| 2551 | 32 | `public long frameHash(long x0, long y0, int w, int h)` | A hash of what the runs lay on the w x h plots from (x0, y0) - each stretch's steps there, the hub, the arms' ends (their ramps), the yards - without drawing them: what a district's plan is kept against (CityMap.ownSt... |
| 2585 | 14 | `static long[] range(Arm a, int s, long x0, long y0, int w, int h)` | The steps of stretch s of arm a whose plots (or corners) may fall in the box: {first, last}; first > last for none. |
| 2600 | 6 | `private static void put(byte[] fixed, int w, long x, long y, byte code)` |  |
| 2608 | 34 | `private static long stretchInto(Net n, Arm a, int s, long x0, long y0, int w, int h, byte[] fixed, byte[] marks, byte code)` | One stretch's plots inside the box, into fixed and marks; a hash of its part there. |
| 2644 | 3 | `static int diagCode(int h)` | A 45-degree heading's two bits: south-east 0, south-west 1, north-west 2, north-east 3. |
| 2649 | 5 | `public byte at(long x, long y)` | What the runs carry at world plot (x, y): F_YARD, F_RAIL_OVER, F_HIGHWAY, F_RAIL, or 0 - no highway while the net reaches no street (highwaysReach(), FX2). |

### THE SIDECAR (lines 2655-2770)

| line | len | member | says |
|---:|---:|---|---|
| 2660 | 3 | `public int runs()` | The sidecar's count of runs: the arms of both nets, and one for the yards (0: none laid). |
| 2665 | 31 | `void write(DataOutputStream out) throws IOException` | Writes the runs after their count (CityMap's FORMAT 5): each net's hub, corridors, arms stretch by stretch and interchanges; the yards; what was short. |
| 2698 | 50 | `static CityRuns read(DataInputStream in) throws IOException` | The runs write() wrote, read back; null when they do not add up. |
| 2750 | 20 | `public boolean same(CityRuns o)` | Whether two cities' runs are the same: the gateway, each net's hub, plots, corridors and arms stretch for stretch, and the yards. |

