# CityRuns.java - 1,442 lines · 68 methods · 27 constants · model

`ham/citybuildersim/CityRuns.java` - generated 2026-10-18 by CodeMap; line numbers are as of that run.

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
>             (RAMP_EVERY) and at each arm's end; the railway crosses streets
>             level - an arterial or boulevard crosses the main line on a
>             bridge (D18) - and never runs along a highway;
>   YARDS     each Rail Terminal on the track, in the cell along it nearest a
>             mine, its box across the line (spec 2.8);
>   GATEWAY   (0.8.1, batch FR0; spec-freight 4.1, D13) the bearing toward
>             the world (Gateways), found once by the map and kept here: each
>             net's spine is the row or column nearer it, its arm toward the
>             gateway grown first, until it waits at the city's edge, and
>             taken back last; with none, hw.py's order;
>   MAIN LINE (spec-freight 4.2, D14) the railway from the gateway's arm to
> ... (9 more lines in the source)

**Uses:** [World](World.md) (28), [CityMap](CityMap.md) (12), [TilePainter](TilePainter.md) (5), [DistrictPlan](DistrictPlan.md) (4), [BuildingVisual](BuildingVisual.md) (2)

**Used by (13):** [CityMap](CityMap.md), [DistrictPlan](DistrictPlan.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GatewayCheck](GatewayCheck.md), [JunctionCheck](JunctionCheck.md), [LandMap](LandMap.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [RailDrawCheck](RailDrawCheck.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md)

## Sections

| line | section |
|---:|---|
| 86 | THE DIALS |
| 159 | THE GROUND THE RUNS READ |
| 178 | A NET: ITS HUB, CORRIDORS AND ARMS |
| 330 | WHERE A NET'S PLOTS ARE |
| 395 | GROWING A NET |
| 754 | TAKING FROM THE NEWEST END |
| 840 | THE RAILWAY'S YARDS |
| 936 | THE MAIN LINE (0.8.1, batch FR0; spec-freight 4.2, D14) |
| 1120 | THE MONTH: TO THE MODEL'S COUNTS |
| 1162 | READING THEM: A BOX OF PLOTS |
| 1327 | THE SIDECAR |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 91 | `CityRuns.CELL` | `World.TILE` | A cell's side: a tile, 32 plots (DistrictPlan.CELL). |
| 94 | `CityRuns.HIGHWAY_AT` | `DistrictPlan.MIDDLE + 1` | Where a highway rides in its cell: 16 plots in, the street line through its middle (spec 2.7; DistrictPlan's MIDDLE line, 15 into the interior), so it never takes an arterial's row. |
| 97 | `CityRuns.RAIL_AT` | `DistrictPlan.LATTICE` | ...and the railway: 8 plots in, the street line a quarter in (spec 2.8; the lattice's first line, DistrictPlan.LATTICE) - never a highway's row. |
| 100 | `CityRuns.GATEWAY_UNKNOWN` | `- 2` | A city whose gateway has not been looked for yet (a map's runs read from a sidecar older than FORMAT 8). |
| 103 | `CityRuns.NO_GATEWAY` | `- 1` | A city looked for and found to have no gateway: an island founding with no land route to the world (spec 4.1). |
| 106 | `CityRuns.APART` | `CityMap.DISTRICT` | Parallel corridors lie one district apart: 256 plots, 7.68 km (spec 2.7; hw.py's APART). |
| 109 | `CityRuns.LOOK` | `6` | How far ahead an arm must see its way clear to step: 6 plots (hw.py's _ahead(..., 6)) - so it stops short of the sea and the city's edge. |
| 112 | `CityRuns.SIDE_LOOK` | `159` | How far an arm at the sea looks along each 45-degree way, to turn toward the one with more ground: 159 plots (hw.py's score, range(1, 160)). |
| 115 | `CityRuns.STRAIGHT_COST` | `1` | What a step straight on costs, in plots of straight: 1 (spec 2.7, H2). |
| 118 | `CityRuns.TURN_COST` | `40` | ...a 45-degree turn: about 40 (spec 2.7, H2: "est., dials to tune"). |
| 121 | `CityRuns.JUNCTION_COST` | `200` | ...a junction: about 200 (spec 2.7, H2). |
| 124 | `CityRuns.TWIN_NEAR` | `40` | A run off its heading stops short of another arm's plot this near beside it, either side: 40 plots (hw.py's no parallel twin, range(2, 40)). |
| 127 | `CityRuns.RAMP_EVERY` | `2` | Ramps where a highway crosses every RAMP_EVERY-th arterial: every other one, 1.92 km (spec 2.7). |
| 130 | `CityRuns.HUB_REACH` | `CityMap.TILES_A_SIDE` | A hub is sought among its lines' crossings within this many cells of where its net starts, each way, the nearest first: 8 - a district - where its own crossing is not the city's dry ground. |
| 133 | `CityRuns.READ_AHEAD` | `CELL` | An arm reads its way ahead this far at a time and steps on what it read: a cell, 32 plots - so a step reads a plot, not LOOK. |
| 136 | `CityRuns.DX` | `{ 1, 1, 0, - 1, - 1, - 1, 0, 1 }, DY = { 0, 1, 1, 1, 0, - 1, - 1, - 1 }` | The eight headings, hw.py's DIRS: east, south-east, south, south-west, west, north-west, north, north-east. |
| 139 | `CityRuns.HIGHWAYS` | `0, RAILWAY = 1` | A net's kinds. |
| 142 | `CityRuns.F_HIGHWAY` | `BuildingVisual.HIGHWAY` | What a plot carries (fill()): a highway's plot (BuildingVisual.HIGHWAY, DistrictPlan.FIXED_HIGHWAY)... |
| 144 | `CityRuns.F_RAIL` | `TilePainter.RAIL` | ...the railway's track (TilePainter.RAIL, DistrictPlan.FIXED_RAIL)... |
| 146 | `CityRuns.F_RAIL_OVER` | `6` | ...the railway on a bridge over a highway (spec 2.8)... |
| 148 | `CityRuns.F_YARD` | `7` | ...a yard's ground, its track among it (DistrictPlan.FIXED_YARD): no street crosses it, no other building stands on it. |
| 151 | `CityRuns.M_RAMP` | `1` | A plot's marks (fill()'s second array): a highway's ramp (spec 2.7)... |
| 153 | `CityRuns.M_DIAG` | `2` | ...a plot of a 45-degree stretch, drawn smooth, its heading's two bits from M_DIAG_SHIFT (diagCode())... |
| 155 | `CityRuns.M_DIAG_SHIFT` | `2` | ...the bit that heading's two bits start at: bits 2 and 3, between M_DIAG and M_CORNER... |
| 157 | `CityRuns.M_CORNER` | `16` | ...and of those, a staircase's corner: the plot beside the stretch's line, (x + dx, y) of a step from (x, y). |
| 400 | `CityRuns.OPEN` | `0, EDGE = 1, SEA = 2` | What stops a way: nothing, the city's edge, or the sea (and fresh water wider than the net bridges). |
| 527 | `CityRuns.STEPPED` | `0, WAITS = 1, JOINED = 2, HELD = 3, YIELDS = 4` | What a step did: stepped on; waits on the ground (tried again when the ground moves); stopped for good (joined); held for want of a second plot; or waits on the other net (tried again next time). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 184 | `final int corridor, home` |  |
| 185 | `final long sx, sy` |  |
| 186 | `int head` |  |
| 187 | `long x, y` |  |
| 188 | `int steps` |  |
| 189 | `boolean joined` |  |
| 191 | `int segs` | Its stretches: each one's heading, steps, and start (the plot before its first step). |
| 192 | `int[] segHead` |  |
| 193 | `long[] segX` |  |
| 195 | `final List<long[]> crossings` | Where it crossed the net (interchanges), each {x, y, the step that made it}, in the order made. |
| 197 | `int known` | Steps known clear ahead along `head`, read at the ground's version knownAt; and the version it waits at (transient). |
| 198 | `long knownAt` |  |
| 227 | `final int kind, at, bridge` |  |
| 229 | `boolean started, hubLaid` | Its hub chosen, and whether the hub's plot is laid (a railway may stand for its yards with no track). |
| 230 | `long hubX, hubY` |  |
| 232 | `final List<long[]> corridors` | Its corridors, {0 a row \| 1 a column, the line's coordinate}, in the order opened. |
| 233 | `final List<Arm> arms` |  |
| 234 | `final List<Interchange> interchanges` |  |
| 236 | `long plots` | Plots laid: the union of its arms' plots and its hub's. |
| 238 | `long stuckAt` | The ground's version at which it last could lay no more (transient): it waits until the ground moves. |
| 240 | `final Map<Long, long[]> buckets` | Its stretches by district: district key -> {arm << 24 \| stretch} entries, and how many. |
| 241 | `final Map<Long, Integer> bucketN` |  |
| 284 | `final Net highways` |  |
| 285 | `final Net rail` |  |
| 288 | `int gateway` | The gateway's bearing, city-wide (the highway's and the railway's share it): GATEWAY_UNKNOWN until the map looks, NO_GATEWAY where it finds none. |
| 291 | `final List<long[]> yards` | The railway's yards: each {x0, y0, w, h} in world plots, in the order laid. |
| 294 | `long highwayShort, railShort` | What the model has that the runs could not lay: highway plots, track plots, yards (counted: the legend says so; a yard with no place is drawn by its district's plan). |
| 295 | `int yardsShort` |  |
| 298 | `long version` | A stamp of everything laid: what the map's plans and views are kept against. |
| 301 | `private boolean doubleMain` | Whether the main line is double track: the city has a Freight Line (spec 4.3). |
| 304 | `private List<int[]> mainLineCache` | mainLine()'s last answer, and the version it was computed at. |
| 305 | `private long mainLineCacheAt` |  |
| 957 | `private long[] station` | freightStation()'s last answer, the version and the gateway it was found at. |
| 958 | `private long stationAt` |  |
| 959 | `private int stationAtGateway` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 84 | 1359 | **type** `public final class CityRuns` | The city's highways and railway as runs on corridors: each laid month by month from its hub's lines out, straight by preference, never moved, with the railway's yards on its track nearest the mines - what the city map... |

### THE DIALS (lines 86-158)

### THE GROUND THE RUNS READ (lines 159-177)

| line | len | member | says |
|---:|---:|---|---|
| 164 | 8 | **type** `public interface Ground` | The ground runs are laid on: the city map's (CityMap.RunGround). |
| 166 | 1 | `int at(long x, long y, int dir)` _(in CityRuns.Ground)_ | The ground at world plot (x, y), read along heading `dir` (0 to 7, or -1): a World class, or -1 where the city does not own it. |
| 168 | 1 | `long version()` _(in CityRuns.Ground)_ | A number that moves whenever the city's ground does (a purchase): what a waiting arm and a stuck net wait on. |
| 170 | 1 | `long[] box()` _(in CityRuns.Ground)_ | The city's owned box, {x0, y0, x1, y1} in plots, inclusive: how far out a corridor is sought. |
| 174 | 3 | `static boolean dry(int g)` | Whether a ground class is the city's dry ground: owned (a class at all, not -1), and dry by World.dry() - not water, and since 0.7.127 not ice. |

### A NET: ITS HUB, CORRIDORS AND ARMS (lines 178-329)

| line | len | member | says |
|---:|---:|---|---|
| 183 | 38 | **type** `static final class Arm` | One arm: from its start along its corridor, its straight stretches in the order laid. |
| 200 | 4 | `Arm(int corridor, int home, long sx, long sy)` _(in CityRuns.Arm)_ |  |
| 206 | 4 | `void push(int h)` _(in CityRuns.Arm)_ | A step along h from where it stands: its stretch lengthened, or a new one begun. |
| 211 | 9 | `void append(int h, int steps, long fromX, long fromY)` _(in CityRuns.Arm)_ |  |
| 223 | 1 | **type** `record Interchange(long x, long y, int corridor)` | A corridor's start: where it began on the net (an interchange; the spine's at the hub). |
| 226 | 57 | **type** `public static final class Net` | One net: the highways or the railway. |
| 243 | 1 | `Net(int kind, int at, int bridge)` _(in CityRuns.Net)_ |  |
| 246 | 1 | `public long plots()` _(in CityRuns.Net)_ | Plots laid. |
| 248 | 1 | `public int corridors()` _(in CityRuns.Net)_ | Corridors opened. |
| 250 | 1 | `public int interchanges()` _(in CityRuns.Net)_ | Interchanges: corridors' starts (the hub's among them) and arms' crossings. |
| 252 | 6 | `public List<long[]> interchangePlots()` _(in CityRuns.Net)_ | ...as {x, y}: a harness's. |
| 259 | 1 | `public int arms()` _(in CityRuns.Net)_ | Arms. |
| 261 | 1 | `public long[] hub()` _(in CityRuns.Net)_ | Its hub, {x, y}, or null before it starts. |
| 263 | 5 | `public int bends()` _(in CityRuns.Net)_ | Stretches off their arms' headings: 45-degree runs along a shore or the city's edge. |
| 269 | 1 | `public int joined()` _(in CityRuns.Net)_ | Arms ended where they met the net (joined). |
| 272 | 5 | `public List<long[]> stretches()` _(in CityRuns.Net)_ | Each arm's stretches as {x0, y0, heading, steps} (x0, y0 the plot before its first step), arm by arm: a harness's. |
| 278 | 4 | `void reset()` _(in CityRuns.Net)_ |  |
| 307 | 1 | `public Net highways()` |  |
| 308 | 1 | `public Net rail()` |  |
| 309 | 1 | `public List<long[]> yards()` |  |
| 310 | 1 | `public long highwayShort()` |  |
| 311 | 1 | `public long railShort()` |  |
| 312 | 1 | `public int yardsShort()` |  |
| 313 | 1 | `public long version()` |  |
| 316 | 1 | `public boolean doubleMain()` | Whether the main line is double track: the city has a Freight Line (spec 4.3). |
| 319 | 1 | `public void setDoubleMain(boolean doubleMain)` | Sets whether the main line is double track (the map's, from its Freight Line count). |
| 322 | 1 | `public int gateway()` | The gateway's bearing: GATEWAY_UNKNOWN until the map looks, NO_GATEWAY where it finds none. |
| 325 | 4 | `void setGateway(int bearing)` | Sets the gateway's bearing (the map's, once it has looked). |

### WHERE A NET'S PLOTS ARE (lines 330-394)

| line | len | member | says |
|---:|---:|---|---|
| 334 | 3 | `static long districtKey(long x, long y)` |  |
| 339 | 12 | `static void file(Net n, int a, int s, long x, long y)` | Files stretch s of arm a under the district of plot (x, y). |
| 353 | 20 | `static boolean onStretch(Arm a, int s, long px, long py)` | Whether stretch s of arm a covers world plot (px, py): one of its steps' plots, or a 45-degree step's corner. |
| 375 | 14 | `static int coverer(Net n, long x, long y, int not)` | The arm of net n covering plot (x, y) other than `not` (-1 for any): its index, -2 for the hub, -1 for none. |
| 391 | 3 | `public static boolean covered(Net n, long x, long y)` | Whether net n has a plot at (x, y). |

### GROWING A NET (lines 395-753)

| line | len | member | says |
|---:|---:|---|---|
| 409 | 36 | `static int clear(Net n, Ground g, long x, long y, int d, int k, int[] why)` | The steps along heading d from (x, y) the net may take, up to k (or a bridge's past it), and into why[0] what stops it: ground the city does not own, the sea, water wider than its bridge, a staircase's corner off the ... |
| 447 | 5 | `static void lay(Net n, int a, int s, long x, long y)` | Lays net n's plot (x, y) on arm a's stretch s: counted where nothing else of the net covers it yet (an arm never covers its own new plot: it never turns back). |
| 461 | 48 | `static boolean start(Net n, Ground g, long ax, long ay, int gateway)` | Chooses net n's hub: the line crossing nearest (ax, ay) that is the city's dry ground, within HUB_REACH cells; false where none is. |
| 511 | 14 | `static int gateArm(Net n, int gateway)` | The spine's arm toward the gateway: -1 when there is none or the net has not started; else the index of the arm of corridor 0 or 1 whose home heading's angle (heading h is h * 45 degrees, x = cos, y = sin, as DX/DY) i... |
| 536 | 41 | `static boolean grow(Net n, Net other, long target, Ground g, long ax, long ay, int gateway)` | Grows net n toward `target` plots (hw.py's grow()): its hub laid first, then each plot to the shortest arm of the earliest corridor that can still grow - the gateway's arm (gateArm()) before the rest of its corridor -... |
| 585 | 66 | `static int step(Net n, Net other, int i, Arm a, Ground g, long target, int[] why)` | One step of arm i (hw.py's _step, with the cost rule): STEPPED; WAITS (the city's edge, the sea with no way along it, a T it does not cross, the other net's run along its way); JOINED (it met the net running its own w... |
| 659 | 36 | `static boolean newCorridor(Net n, Ground g)` | The next corridor (hw.py's _new_corridor()): the parallel line k districts out, k from 1, that crosses the net already laid on the city's ground - at its plot there nearest the hub, an interchange - the nearest of tho... |
| 697 | 7 | `static boolean before(double d, int kind, long v, long[] p, double d2, int kind2, long v2, long[] p2)` | hw.py's order of candidates (distance, kind - 'col' before 'row' -, line, plot): whether the first comes before the second. |
| 706 | 47 | `static long[] nearestOnLine(Net n, Ground g, int kind, long v)` | The plot of net n on the row (kind 0) or column (1) at v, on the city's ground, nearest its hub; null for none. |

### TAKING FROM THE NEWEST END (lines 754-839)

| line | len | member | says |
|---:|---:|---|---|
| 759 | 59 | `static boolean trim(Net n, long target, int gateway)` | Takes net n back to `target` plots, each from the newest end - the latest corridor's longest arm, the gateway's arm after the rest of its corridor, its last step (the reverse of grow()'s order); the hub last. |
| 820 | 19 | `static void rebuildBuckets(Net n)` | Files every stretch of net n again (after a corridor is closed, or the runs read back). |

### THE RAILWAY'S YARDS (lines 840-935)

| line | len | member | says |
|---:|---:|---|---|
| 853 | 16 | `void layYards(int target, int w, int h, Ground g, List<long[]> mines)` | The yards toward `target` (spec 2.8): each on the track, in the cell along it nearest a mine (`mines`, the city's mined sites, each {x0, y0, x1, y1} inclusive), its w x h box across the track's line inside the cell - ... |
| 871 | 36 | `List<long[]> yardSpots(int w, int h, List<long[]> mines)` | Every box a yard could take, the nearest a mine first: two a cell the track runs straight through on its line, one at either end of it. |
| 909 | 12 | `static void spotsIn(List<long[]> out, long cx, long cy, boolean row, int w, int h)` | A cell's yard boxes across its quarter line (a row's, or a column's): inside the cell's interior, covering the line, at either end of the cell. |
| 923 | 12 | `boolean yardFits(long[] b, Ground g, List<long[]> mines, List<long[]> laid)` | Whether a yard's box is free, the yards `laid` before it: owned dry ground, off the highways and their verges, no other yard's, no mine's site. |

### THE MAIN LINE (0.8.1, batch FR0; spec-freight 4.2, D14) (lines 936-1119)

| line | len | member | says |
|---:|---:|---|---|
| 947 | 8 | `public long[] freightStation()` | The freight station: the yard nearest the rail net's gate arm's end (its x, y), by the yard box's middle, the first in yards' order on a tie. |
| 962 | 14 | `private long[] findStation()` | The freight station found afresh (freightStation()'s rule). |
| 978 | 13 | `private long[] anchorInYard(long[] y)` | The rail net's plot inside a freight station's box nearest its middle; null when the box has none. |
| 997 | 29 | `static int[] locate(Net n, long x, long y)` | The arm and step of the lowest-index arm of n covering plot (x, y); null if none. |
| 1035 | 34 | `public List<int[]> mainLine()` | The main line as ranges of steps, each {arm, from, to} inclusive (step 0 an arm's start plot). |
| 1075 | 44 | `private static long mainInto(Arm a, int from, int to, long x0, long y0, int w, int h, byte[] marks, boolean doubleMain)` | Marks one main-line range's plots inside the box with M_MAIN, and with M_DOUBLE where doubleMain; returns a hash of the range when any of it lies in the box, else 0. |

### THE MONTH: TO THE MODEL'S COUNTS (lines 1120-1161)

| line | len | member | says |
|---:|---:|---|---|
| 1131 | 17 | `public boolean layTo(long highwayPlots, long trackPlots, int yardCount, int yardW, int yardH, Ground g, long fx, long fy, long ...` | Lays the runs to the model's plots of highway and of track, and its yards (w x h each), over the ground: each net grown, or taken back from its newest end - the highways from the founding site (fx, fy), the railway fr... |
| 1150 | 11 | `long signature()` | A hash of everything laid. |

### READING THEM: A BOX OF PLOTS (lines 1162-1326)

| line | len | member | says |
|---:|---:|---|---|
| 1174 | 52 | `public long fill(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean yardsOver)` | What the runs lay on the w x h plots from world plot (x0, y0), row by row: into fixed, F_HIGHWAY, F_RAIL, F_RAIL_OVER (the railway over a highway) and, `yardsOver`, F_YARD over every plot of a yard (a plan's ground: n... |
| 1233 | 29 | `public long frameHash(long x0, long y0, int w, int h)` | A hash of what the runs lay on the w x h plots from (x0, y0) - each stretch's steps there, the hub, the arms' ends (their ramps), the yards - without drawing them: what a district's plan is kept against (CityMap.ownSt... |
| 1264 | 14 | `static long[] range(Arm a, int s, long x0, long y0, int w, int h)` | The steps of stretch s of arm a whose plots (or corners) may fall in the box: {first, last}; first > last for none. |
| 1279 | 6 | `private static void put(byte[] fixed, int w, long x, long y, byte code)` |  |
| 1287 | 27 | `private static long stretchInto(Net n, Arm a, int s, long x0, long y0, int w, int h, byte[] fixed, byte[] marks, byte code)` | One stretch's plots inside the box, into fixed and marks; a hash of its part there. |
| 1316 | 3 | `static int diagCode(int h)` | A 45-degree heading's two bits: south-east 0, south-west 1, north-west 2, north-east 3. |
| 1321 | 5 | `public byte at(long x, long y)` | What the runs carry at world plot (x, y): F_YARD, F_RAIL_OVER, F_HIGHWAY, F_RAIL, or 0. |

### THE SIDECAR (lines 1327-1442)

| line | len | member | says |
|---:|---:|---|---|
| 1332 | 3 | `public int runs()` | The sidecar's count of runs: the arms of both nets, and one for the yards (0: none laid). |
| 1337 | 31 | `void write(DataOutputStream out) throws IOException` | Writes the runs after their count (CityMap's FORMAT 5): each net's hub, corridors, arms stretch by stretch and interchanges; the yards; what was short. |
| 1370 | 50 | `static CityRuns read(DataInputStream in) throws IOException` | The runs write() wrote, read back; null when they do not add up. |
| 1422 | 20 | `public boolean same(CityRuns o)` | Whether two cities' runs are the same: the gateway, each net's hub, plots, corridors and arms stretch for stretch, and the yards. |

