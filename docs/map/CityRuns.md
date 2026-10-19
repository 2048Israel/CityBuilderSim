# CityRuns.java - 1,683 lines · 78 methods · 27 constants · model

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
> ... (17 more lines in the source)

**Uses:** [World](World.md) (28), [CityMap](CityMap.md) (12), [TilePainter](TilePainter.md) (5), [DistrictPlan](DistrictPlan.md) (4), [BuildingVisual](BuildingVisual.md) (2)

**Used by (13):** [CityMap](CityMap.md), [DistrictPlan](DistrictPlan.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GatewayCheck](GatewayCheck.md), [JunctionCheck](JunctionCheck.md), [LandMap](LandMap.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [RailDrawCheck](RailDrawCheck.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md)

## Sections

| line | section |
|---:|---|
| 94 | THE DIALS |
| 167 | THE GROUND THE RUNS READ |
| 186 | A NET: ITS HUB, CORRIDORS AND ARMS |
| 364 | WHERE A NET'S PLOTS ARE |
| 494 | GROWING A NET |
| 853 | TAKING FROM THE NEWEST END |
| 939 | THE RAILWAY'S YARDS |
| 1035 | THE MAIN LINE (0.8.1, batch FR0; spec-freight 4.2, D14) |
| 1263 | · the trains' way (0.8.2, batch FR1; spec-freight 4.4) |
| 1349 | THE MONTH: TO THE MODEL'S COUNTS |
| 1391 | READING THEM: A BOX OF PLOTS |
| 1568 | THE SIDECAR |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 99 | `CityRuns.CELL` | `World.TILE` | A cell's side: a tile, 32 plots (DistrictPlan.CELL). |
| 102 | `CityRuns.HIGHWAY_AT` | `DistrictPlan.MIDDLE + 1` | Where a highway rides in its cell: 16 plots in, the street line through its middle (spec 2.7; DistrictPlan's MIDDLE line, 15 into the interior), so it never takes an arterial's row. |
| 105 | `CityRuns.RAIL_AT` | `DistrictPlan.LATTICE` | ...and the railway: 8 plots in, the street line a quarter in (spec 2.8; the lattice's first line, DistrictPlan.LATTICE) - never a highway's row. |
| 108 | `CityRuns.GATEWAY_UNKNOWN` | `- 2` | A city whose gateway has not been looked for yet (a map's runs read from a sidecar older than FORMAT 8). |
| 111 | `CityRuns.NO_GATEWAY` | `- 1` | A city looked for and found to have no gateway: an island founding with no land route to the world (spec 4.1). |
| 114 | `CityRuns.APART` | `CityMap.DISTRICT` | Parallel corridors lie one district apart: 256 plots, 7.68 km (spec 2.7; hw.py's APART). |
| 117 | `CityRuns.LOOK` | `6` | How far ahead an arm must see its way clear to step: 6 plots (hw.py's _ahead(..., 6)) - so it stops short of the sea and the city's edge. |
| 120 | `CityRuns.SIDE_LOOK` | `159` | How far an arm at the sea looks along each 45-degree way, to turn toward the one with more ground: 159 plots (hw.py's score, range(1, 160)). |
| 123 | `CityRuns.STRAIGHT_COST` | `1` | What a step straight on costs, in plots of straight: 1 (spec 2.7, H2). |
| 126 | `CityRuns.TURN_COST` | `40` | ...a 45-degree turn: about 40 (spec 2.7, H2: "est., dials to tune"). |
| 129 | `CityRuns.JUNCTION_COST` | `200` | ...a junction: about 200 (spec 2.7, H2). |
| 132 | `CityRuns.TWIN_NEAR` | `40` | A run off its heading stops short of another arm's plot this near beside it, either side: 40 plots (hw.py's no parallel twin, range(2, 40)). |
| 135 | `CityRuns.RAMP_EVERY` | `2` | Ramps where a highway crosses every RAMP_EVERY-th arterial: every other one, 1.92 km (spec 2.7); and (FX2) where each arm of the spine first crosses one. |
| 138 | `CityRuns.HUB_REACH` | `CityMap.TILES_A_SIDE` | A hub is sought among its lines' crossings within this many cells of where its net starts, each way, the nearest first: 8 - a district - where its own crossing is not the city's dry ground. |
| 141 | `CityRuns.READ_AHEAD` | `CELL` | An arm reads its way ahead this far at a time and steps on what it read: a cell, 32 plots - so a step reads a plot, not LOOK. |
| 144 | `CityRuns.DX` | `{ 1, 1, 0, - 1, - 1, - 1, 0, 1 }, DY = { 0, 1, 1, 1, 0, - 1, - 1, - 1 }` | The eight headings, hw.py's DIRS: east, south-east, south, south-west, west, north-west, north, north-east. |
| 147 | `CityRuns.HIGHWAYS` | `0, RAILWAY = 1` | A net's kinds. |
| 150 | `CityRuns.F_HIGHWAY` | `BuildingVisual.HIGHWAY` | What a plot carries (fill()): a highway's plot (BuildingVisual.HIGHWAY, DistrictPlan.FIXED_HIGHWAY)... |
| 152 | `CityRuns.F_RAIL` | `TilePainter.RAIL` | ...the railway's track (TilePainter.RAIL, DistrictPlan.FIXED_RAIL)... |
| 154 | `CityRuns.F_RAIL_OVER` | `6` | ...the railway on a bridge over a highway (spec 2.8)... |
| 156 | `CityRuns.F_YARD` | `7` | ...a yard's ground, its track among it (DistrictPlan.FIXED_YARD): no street crosses it, no other building stands on it. |
| 159 | `CityRuns.M_RAMP` | `1` | A plot's marks (fill()'s second array): a highway's ramp (spec 2.7)... |
| 161 | `CityRuns.M_DIAG` | `2` | ...a plot of a 45-degree stretch, drawn smooth, its heading's two bits from M_DIAG_SHIFT (diagCode())... |
| 163 | `CityRuns.M_DIAG_SHIFT` | `2` | ...the bit that heading's two bits start at: bits 2 and 3, between M_DIAG and M_CORNER... |
| 165 | `CityRuns.M_CORNER` | `16` | ...and of those, a staircase's corner: the plot beside the stretch's line, (x + dx, y) of a step from (x, y). |
| 499 | `CityRuns.OPEN` | `0, EDGE = 1, SEA = 2` | What stops a way: nothing, the city's edge, or the sea (and fresh water wider than the net bridges). |
| 626 | `CityRuns.STEPPED` | `0, WAITS = 1, JOINED = 2, HELD = 3, YIELDS = 4` | What a step did: stepped on; waits on the ground (tried again when the ground moves); stopped for good (joined); held for want of a second plot; or waits on the other net (tried again next time). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 192 | `final int corridor, home` |  |
| 193 | `final long sx, sy` |  |
| 194 | `int head` |  |
| 195 | `long x, y` |  |
| 196 | `int steps` |  |
| 197 | `boolean joined` |  |
| 199 | `int segs` | Its stretches: each one's heading, steps, and start (the plot before its first step). |
| 200 | `int[] segHead` |  |
| 201 | `long[] segX` |  |
| 203 | `final List<long[]> crossings` | Where it crossed the net (interchanges), each {x, y, the step that made it}, in the order made. |
| 205 | `int known` | Steps known clear ahead along `head`, read at the ground's version knownAt; and the version it waits at (transient). |
| 206 | `long knownAt` |  |
| 235 | `final int kind, at, bridge` |  |
| 237 | `boolean started, hubLaid` | Its hub chosen, and whether the hub's plot is laid (a railway may stand for its yards with no track). |
| 238 | `long hubX, hubY` |  |
| 240 | `final List<long[]> corridors` | Its corridors, {0 a row \| 1 a column, the line's coordinate}, in the order opened. |
| 241 | `final List<Arm> arms` |  |
| 242 | `final List<Interchange> interchanges` |  |
| 244 | `long plots` | Plots laid: the union of its arms' plots and its hub's. |
| 246 | `long stuckAt` | The ground's version at which it last could lay no more (transient): it waits until the ground moves. |
| 248 | `final Map<Long, long[]> buckets` | Its stretches by district: district key -> {arm << 24 \| stretch} entries, and how many. |
| 249 | `final Map<Long, Integer> bucketN` |  |
| 292 | `final Net highways` |  |
| 293 | `final Net rail` |  |
| 296 | `int gateway` | The gateway's bearing, city-wide (the highway's and the railway's share it): GATEWAY_UNKNOWN until the map looks, NO_GATEWAY where it finds none. |
| 299 | `final List<long[]> yards` | The railway's yards: each {x0, y0, w, h} in world plots, in the order laid. |
| 302 | `long highwayShort, railShort` | What the model has that the runs could not lay: highway plots, track plots, yards (counted: the legend says so; a yard with no place is drawn by its district's plan). |
| 303 | `int yardsShort` |  |
| 306 | `long version` | A stamp of everything laid: what the map's plans and views are kept against. |
| 309 | `private boolean doubleMain` | Whether the main line is double track: the city has a Freight Line (spec 4.3). |
| 312 | `private List<int[]> mainLineCache` | mainLine()'s last answer, and the version and the main arm it was computed at. |
| 313 | `private long mainLineCacheAt` |  |
| 314 | `private int mainLineCacheArm` |  |
| 317 | `private boolean highwaysReachCache` | highwaysReach()'s last answer, and the version it was computed at. |
| 318 | `private long highwaysReachCacheAt` |  |
| 321 | `private int mainArm` | The rail net's arm the main line runs out along and the trains come in on: the arm the railway's world line leaves the city's ground from (CityMap.worldLines() sets it, FX2), or -1 for the gate arm. |
| 1061 | `private long[] station` | freightStation()'s last answer, the version, the gateway and the main arm it was found at. |
| 1062 | `private long stationAt` |  |
| 1063 | `private int stationAtGateway` |  |
| 1064 | `private int stationAtArm` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 92 | 1592 | **type** `public final class CityRuns` | The city's highways and railway as runs on corridors: each laid month by month from its hub's lines out, straight by preference, never moved, with the railway's yards on its track nearest the mines - what the city map... |

### THE DIALS (lines 94-166)

### THE GROUND THE RUNS READ (lines 167-185)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 8 | **type** `public interface Ground` | The ground runs are laid on: the city map's (CityMap.RunGround). |
| 174 | 1 | `int at(long x, long y, int dir)` _(in CityRuns.Ground)_ | The ground at world plot (x, y), read along heading `dir` (0 to 7, or -1): a World class, or -1 where the city does not own it. |
| 176 | 1 | `long version()` _(in CityRuns.Ground)_ | A number that moves whenever the city's ground does (a purchase): what a waiting arm and a stuck net wait on. |
| 178 | 1 | `long[] box()` _(in CityRuns.Ground)_ | The city's owned box, {x0, y0, x1, y1} in plots, inclusive: how far out a corridor is sought. |
| 182 | 3 | `static boolean dry(int g)` | Whether a ground class is the city's dry ground: owned (a class at all, not -1), and dry by World.dry() - not water, and since 0.7.127 not ice. |

### A NET: ITS HUB, CORRIDORS AND ARMS (lines 186-363)

| line | len | member | says |
|---:|---:|---|---|
| 191 | 38 | **type** `static final class Arm` | One arm: from its start along its corridor, its straight stretches in the order laid. |
| 208 | 4 | `Arm(int corridor, int home, long sx, long sy)` _(in CityRuns.Arm)_ |  |
| 214 | 4 | `void push(int h)` _(in CityRuns.Arm)_ | A step along h from where it stands: its stretch lengthened, or a new one begun. |
| 219 | 9 | `void append(int h, int steps, long fromX, long fromY)` _(in CityRuns.Arm)_ |  |
| 231 | 1 | **type** `record Interchange(long x, long y, int corridor)` | A corridor's start: where it began on the net (an interchange; the spine's at the hub). |
| 234 | 57 | **type** `public static final class Net` | One net: the highways or the railway. |
| 251 | 1 | `Net(int kind, int at, int bridge)` _(in CityRuns.Net)_ |  |
| 254 | 1 | `public long plots()` _(in CityRuns.Net)_ | Plots laid. |
| 256 | 1 | `public int corridors()` _(in CityRuns.Net)_ | Corridors opened. |
| 258 | 1 | `public int interchanges()` _(in CityRuns.Net)_ | Interchanges: corridors' starts (the hub's among them) and arms' crossings. |
| 260 | 6 | `public List<long[]> interchangePlots()` _(in CityRuns.Net)_ | ...as {x, y}: a harness's. |
| 267 | 1 | `public int arms()` _(in CityRuns.Net)_ | Arms. |
| 269 | 1 | `public long[] hub()` _(in CityRuns.Net)_ | Its hub, {x, y}, or null before it starts. |
| 271 | 5 | `public int bends()` _(in CityRuns.Net)_ | Stretches off their arms' headings: 45-degree runs along a shore or the city's edge. |
| 277 | 1 | `public int joined()` _(in CityRuns.Net)_ | Arms ended where they met the net (joined). |
| 280 | 5 | `public List<long[]> stretches()` _(in CityRuns.Net)_ | Each arm's stretches as {x0, y0, heading, steps} (x0, y0 the plot before its first step), arm by arm: a harness's. |
| 286 | 4 | `void reset()` _(in CityRuns.Net)_ |  |
| 323 | 1 | `public Net highways()` |  |
| 324 | 1 | `public Net rail()` |  |
| 325 | 1 | `public List<long[]> yards()` |  |
| 326 | 1 | `public long highwayShort()` |  |
| 329 | 1 | `public long highwayUndrawn()` | The highway plots the model has that the map draws nowhere: those the runs could not lay (highwayShort()), and (FX2) all the net's while it reaches no street (highwaysReach()), laid but not drawn - the legend's. |
| 330 | 1 | `public long railShort()` |  |
| 331 | 1 | `public int yardsShort()` |  |
| 332 | 1 | `public long version()` |  |
| 335 | 1 | `public boolean doubleMain()` | Whether the main line is double track: the city has a Freight Line (spec 4.3). |
| 338 | 1 | `public void setDoubleMain(boolean doubleMain)` | Sets whether the main line is double track (the map's, from its Freight Line count). |
| 341 | 1 | `public int gateway()` | The gateway's bearing: GATEWAY_UNKNOWN until the map looks, NO_GATEWAY where it finds none. |
| 344 | 4 | `void setGateway(int bearing)` | Sets the gateway's bearing (the map's, once it has looked). |
| 350 | 1 | `public void setMainArm(int arm)` | Sets the arm the main line runs out along (the map's, from where the railway's world line leaves the city's ground): -1 for the gate arm. |
| 357 | 6 | `public int mainArm()` | The rail net's main arm: -1 with no gateway or before the railway starts; else the arm the map set (setMainArm()) while it is an arm of the net with steps laid, else the gate arm. |

### WHERE A NET'S PLOTS ARE (lines 364-493)

| line | len | member | says |
|---:|---:|---|---|
| 368 | 3 | `static long districtKey(long x, long y)` |  |
| 373 | 12 | `static void file(Net n, int a, int s, long x, long y)` | Files stretch s of arm a under the district of plot (x, y). |
| 387 | 20 | `static boolean onStretch(Arm a, int s, long px, long py)` | Whether stretch s of arm a covers world plot (px, py): one of its steps' plots, or a 45-degree step's corner. |
| 409 | 14 | `static int coverer(Net n, long x, long y, int not)` | The arm of net n covering plot (x, y) other than `not` (-1 for any): its index, -2 for the hub, -1 for none. |
| 425 | 3 | `public static boolean covered(Net n, long x, long y)` | Whether net n has a plot at (x, y). |
| 441 | 14 | `public boolean highwaysReach()` | Whether the highways net is drawn: it has started and it has a ramp onto an arterial where stretchInto() marks M_RAMP - a straight stretch's step on a multiple of CELL that is every RAMP_EVERY-th cell line, or an arm ... |
| 457 | 12 | `static boolean onRampLine(Arm a, int s)` | Whether straight stretch s of arm a has a step (1 to its steps) on a ramp line: its coordinate along the stretch (x going east or west, y going north or south) a multiple of CELL whose cell line is every RAMP_EVERY-th... |
| 477 | 16 | `static long firstLineStep(Arm a)` | The step (1 to its steps) at which arm a's straight stretches first put a plot on a cell line along their heading - x a multiple of CELL going east or west, y going north or south - or -1 for none: where an arm of the... |

### GROWING A NET (lines 494-852)

| line | len | member | says |
|---:|---:|---|---|
| 508 | 36 | `static int clear(Net n, Ground g, long x, long y, int d, int k, int[] why)` | The steps along heading d from (x, y) the net may take, up to k (or a bridge's past it), and into why[0] what stops it: ground the city does not own, the sea, water wider than its bridge, a staircase's corner off the ... |
| 546 | 5 | `static void lay(Net n, int a, int s, long x, long y)` | Lays net n's plot (x, y) on arm a's stretch s: counted where nothing else of the net covers it yet (an arm never covers its own new plot: it never turns back). |
| 560 | 48 | `static boolean start(Net n, Ground g, long ax, long ay, int gateway)` | Chooses net n's hub: the line crossing nearest (ax, ay) that is the city's dry ground, within HUB_REACH cells; false where none is. |
| 610 | 14 | `static int gateArm(Net n, int gateway)` | The spine's arm toward the gateway: -1 when there is none or the net has not started; else the index of the arm of corridor 0 or 1 whose home heading's angle (heading h is h * 45 degrees, x = cos, y = sin, as DX/DY) i... |
| 635 | 41 | `static boolean grow(Net n, Net other, long target, Ground g, long ax, long ay, int gateway)` | Grows net n toward `target` plots (hw.py's grow()): its hub laid first, then each plot to the shortest arm of the earliest corridor that can still grow - the gateway's arm (gateArm()) before the rest of its corridor -... |
| 684 | 66 | `static int step(Net n, Net other, int i, Arm a, Ground g, long target, int[] why)` | One step of arm i (hw.py's _step, with the cost rule): STEPPED; WAITS (the city's edge, the sea with no way along it, a T it does not cross, the other net's run along its way); JOINED (it met the net running its own w... |
| 758 | 36 | `static boolean newCorridor(Net n, Ground g)` | The next corridor (hw.py's _new_corridor()): the parallel line k districts out, k from 1, that crosses the net already laid on the city's ground - at its plot there nearest the hub, an interchange - the nearest of tho... |
| 796 | 7 | `static boolean before(double d, int kind, long v, long[] p, double d2, int kind2, long v2, long[] p2)` | hw.py's order of candidates (distance, kind - 'col' before 'row' -, line, plot): whether the first comes before the second. |
| 805 | 47 | `static long[] nearestOnLine(Net n, Ground g, int kind, long v)` | The plot of net n on the row (kind 0) or column (1) at v, on the city's ground, nearest its hub; null for none. |

### TAKING FROM THE NEWEST END (lines 853-938)

| line | len | member | says |
|---:|---:|---|---|
| 858 | 59 | `static boolean trim(Net n, long target, int gateway)` | Takes net n back to `target` plots, each from the newest end - the latest corridor's longest arm, the gateway's arm after the rest of its corridor, its last step (the reverse of grow()'s order); the hub last. |
| 919 | 19 | `static void rebuildBuckets(Net n)` | Files every stretch of net n again (after a corridor is closed, or the runs read back). |

### THE RAILWAY'S YARDS (lines 939-1034)

| line | len | member | says |
|---:|---:|---|---|
| 952 | 16 | `void layYards(int target, int w, int h, Ground g, List<long[]> mines)` | The yards toward `target` (spec 2.8): each on the track, in the cell along it nearest a mine (`mines`, the city's mined sites, each {x0, y0, x1, y1} inclusive), its w x h box across the track's line inside the cell - ... |
| 970 | 36 | `List<long[]> yardSpots(int w, int h, List<long[]> mines)` | Every box a yard could take, the nearest a mine first: two a cell the track runs straight through on its line, one at either end of it. |
| 1008 | 12 | `static void spotsIn(List<long[]> out, long cx, long cy, boolean row, int w, int h)` | A cell's yard boxes across its quarter line (a row's, or a column's): inside the cell's interior, covering the line, at either end of the cell. |
| 1022 | 12 | `boolean yardFits(long[] b, Ground g, List<long[]> mines, List<long[]> laid)` | Whether a yard's box is free, the yards `laid` before it: owned dry ground, off the highways and their verges, no other yard's, no mine's site. |

### THE MAIN LINE (0.8.1, batch FR0; spec-freight 4.2, D14) (lines 1035-1262)

| line | len | member | says |
|---:|---:|---|---|
| 1047 | 12 | `public long[] freightStation()` | The freight station: the yard nearest the rail net's main arm's end (mainArm(): the arm the railway's world line leaves from, else the gate arm; its x, y), by the yard box's middle, the first in yards' order on a tie. |
| 1067 | 14 | `private long[] findStation()` | The freight station found afresh (freightStation()'s rule). |
| 1083 | 13 | `private long[] anchorInYard(long[] y)` | The rail net's plot inside a freight station's box nearest its middle; null when the box has none. |
| 1102 | 29 | `static int[] locate(Net n, long x, long y)` | The arm and step of the lowest-index arm of n covering plot (x, y); null if none. |
| 1141 | 13 | `static List<int[]> chain(Net n, int a, int k)` | The arms from step k of arm a back toward the hub, as {arm, step}: a's first, then each next the arm and step its predecessor starts on (locate() of its start plot), ending on an arm of corridor 0 or 1. |
| 1167 | 45 | `public List<int[]> mainLine()` | The main line as ranges of steps, each {arm, from, to} inclusive (step 0 an arm's start plot). |
| 1218 | 44 | `private static long mainInto(Arm a, int from, int to, long x0, long y0, int w, int h, byte[] marks, boolean doubleMain)` | Marks one main-line range's plots inside the box with M_MAIN, and with M_DOUBLE where doubleMain; returns a hash of the range when any of it lies in the box, else 0. |

### the trains' way (0.8.2, batch FR1; spec-freight 4.4) (lines 1263-1348)

| line | len | member | says |
|---:|---:|---|---|
| 1280 | 41 | `public double[][] wayTo(long[] yard)` | The railway's way from its main arm's end (mainArm(): the arm the railway's world line leaves from, else the gate arm) to a yard, as points in plots, each a plot's middle, at the ends and every turn: in along the main... |
| 1323 | 10 | `static void armPoints(Arm a, int from, int to, List<long[]> out)` | Adds to out arm a's plots at steps from and to (from <= to) and at every turn between, in the order of its steps. |
| 1335 | 13 | `static long[] plotAt(Arm a, int k)` | The plot at step k of arm a: its start at 0, its end at a.steps and past. |

### THE MONTH: TO THE MODEL'S COUNTS (lines 1349-1390)

| line | len | member | says |
|---:|---:|---|---|
| 1360 | 17 | `public boolean layTo(long highwayPlots, long trackPlots, int yardCount, int yardW, int yardH, Ground g, long fx, long fy, long ...` | Lays the runs to the model's plots of highway and of track, and its yards (w x h each), over the ground: each net grown, or taken back from its newest end - the highways from the founding site (fx, fy), the railway fr... |
| 1379 | 11 | `long signature()` | A hash of everything laid. |

### READING THEM: A BOX OF PLOTS (lines 1391-1567)

| line | len | member | says |
|---:|---:|---|---|
| 1405 | 53 | `public long fill(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean yardsOver)` | What the runs lay on the w x h plots from world plot (x0, y0), row by row: into fixed, F_HIGHWAY, F_RAIL, F_RAIL_OVER (the railway over a highway) and, `yardsOver`, F_YARD over every plot of a yard (a plan's ground: n... |
| 1466 | 30 | `public long frameHash(long x0, long y0, int w, int h)` | A hash of what the runs lay on the w x h plots from (x0, y0) - each stretch's steps there, the hub, the arms' ends (their ramps), the yards - without drawing them: what a district's plan is kept against (CityMap.ownSt... |
| 1498 | 14 | `static long[] range(Arm a, int s, long x0, long y0, int w, int h)` | The steps of stretch s of arm a whose plots (or corners) may fall in the box: {first, last}; first > last for none. |
| 1513 | 6 | `private static void put(byte[] fixed, int w, long x, long y, byte code)` |  |
| 1521 | 34 | `private static long stretchInto(Net n, Arm a, int s, long x0, long y0, int w, int h, byte[] fixed, byte[] marks, byte code)` | One stretch's plots inside the box, into fixed and marks; a hash of its part there. |
| 1557 | 3 | `static int diagCode(int h)` | A 45-degree heading's two bits: south-east 0, south-west 1, north-west 2, north-east 3. |
| 1562 | 5 | `public byte at(long x, long y)` | What the runs carry at world plot (x, y): F_YARD, F_RAIL_OVER, F_HIGHWAY, F_RAIL, or 0 - no highway while the net reaches no street (highwaysReach(), FX2). |

### THE SIDECAR (lines 1568-1683)

| line | len | member | says |
|---:|---:|---|---|
| 1573 | 3 | `public int runs()` | The sidecar's count of runs: the arms of both nets, and one for the yards (0: none laid). |
| 1578 | 31 | `void write(DataOutputStream out) throws IOException` | Writes the runs after their count (CityMap's FORMAT 5): each net's hub, corridors, arms stretch by stretch and interchanges; the yards; what was short. |
| 1611 | 50 | `static CityRuns read(DataInputStream in) throws IOException` | The runs write() wrote, read back; null when they do not add up. |
| 1663 | 20 | `public boolean same(CityRuns o)` | Whether two cities' runs are the same: the gateway, each net's hub, plots, corridors and arms stretch for stretch, and the yards. |

