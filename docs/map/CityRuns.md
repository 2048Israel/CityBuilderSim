# CityRuns.java - 1,156 lines · 57 methods · 25 constants · model

`ham/citybuildersim/CityRuns.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
>             earliest corridor that can still grow; what it takes away comes
>             off the newest end - the latest corridor's longest arm - so no
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
>             level and never runs along a highway;
>   YARDS     each Rail Terminal on the track, in the cell along it nearest a
>             mine, its box across the line (spec 2.8).
> 
> A net is kept as its arms' straight STRETCHES, not its plots: a city of ten
> billion has some fifteen million plots of track on a few hundred arms.
> Pure: the same ground, counts and history give the same runs on any
> machine; nothing in the model reads them (CityMap's banner). MapCheck holds
> them on the drawn raster.

**Uses:** [World](World.md) (25), [CityMap](CityMap.md) (12), [DistrictPlan](DistrictPlan.md) (4), [TilePainter](TilePainter.md) (3), [BuildingVisual](BuildingVisual.md) (2)

**Used by (6):** [CityMap](CityMap.md), [DistrictPlan](DistrictPlan.md), [LandMap](LandMap.md), [MapCheck](MapCheck.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md)

## Sections

| line | section |
|---:|---|
| 75 | THE DIALS |
| 141 | THE GROUND THE RUNS READ |
| 160 | A NET: ITS HUB, CORRIDORS AND ARMS |
| 287 | WHERE A NET'S PLOTS ARE |
| 352 | GROWING A NET |
| 663 | TAKING FROM THE NEWEST END |
| 746 | THE RAILWAY'S YARDS |
| 842 | THE MONTH: TO THE MODEL'S COUNTS |
| 883 | READING THEM: A BOX OF PLOTS |
| 1042 | THE SIDECAR |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 80 | `CityRuns.CELL` | `World.TILE` | A cell's side: a tile, 32 plots (DistrictPlan.CELL). |
| 83 | `CityRuns.HIGHWAY_AT` | `DistrictPlan.MIDDLE + 1` | Where a highway rides in its cell: 16 plots in, the street line through its middle (spec 2.7; DistrictPlan's MIDDLE line, 15 into the interior), so it never takes an arterial's row. |
| 86 | `CityRuns.RAIL_AT` | `DistrictPlan.LATTICE` | ...and the railway: 8 plots in, the street line a quarter in (spec 2.8; the lattice's first line, DistrictPlan.LATTICE) - never a highway's row. |
| 89 | `CityRuns.APART` | `CityMap.DISTRICT` | Parallel corridors lie one district apart: 256 plots, 7.68 km (spec 2.7; hw.py's APART). |
| 92 | `CityRuns.LOOK` | `6` | How far ahead an arm must see its way clear to step: 6 plots (hw.py's _ahead(..., 6)) - so it stops short of the sea and the city's edge. |
| 95 | `CityRuns.SIDE_LOOK` | `159` | How far an arm at the sea looks along each 45-degree way, to turn toward the one with more ground: 159 plots (hw.py's score, range(1, 160)). |
| 98 | `CityRuns.STRAIGHT_COST` | `1` | What a step straight on costs, in plots of straight: 1 (spec 2.7, H2). |
| 101 | `CityRuns.TURN_COST` | `40` | ...a 45-degree turn: about 40 (spec 2.7, H2: "est., dials to tune"). |
| 104 | `CityRuns.JUNCTION_COST` | `200` | ...a junction: about 200 (spec 2.7, H2). |
| 107 | `CityRuns.TWIN_NEAR` | `40` | A run off its heading stops short of another arm's plot this near beside it, either side: 40 plots (hw.py's no parallel twin, range(2, 40)). |
| 110 | `CityRuns.RAMP_EVERY` | `2` | Ramps where a highway crosses every RAMP_EVERY-th arterial: every other one, 1.92 km (spec 2.7). |
| 113 | `CityRuns.HUB_REACH` | `CityMap.TILES_A_SIDE` | A hub is sought among its lines' crossings within this many cells of where its net starts, each way, the nearest first: 8 - a district - where its own crossing is not the city's dry ground. |
| 116 | `CityRuns.READ_AHEAD` | `CELL` | An arm reads its way ahead this far at a time and steps on what it read: a cell, 32 plots - so a step reads a plot, not LOOK. |
| 119 | `CityRuns.DX` | `{ 1, 1, 0, - 1, - 1, - 1, 0, 1 }, DY = { 0, 1, 1, 1, 0, - 1, - 1, - 1 }` | The eight headings, hw.py's DIRS: east, south-east, south, south-west, west, north-west, north, north-east. |
| 122 | `CityRuns.HIGHWAYS` | `0, RAILWAY = 1` | A net's kinds. |
| 125 | `CityRuns.F_HIGHWAY` | `BuildingVisual.HIGHWAY` | What a plot carries (fill()): a highway's plot (BuildingVisual.HIGHWAY, DistrictPlan.FIXED_HIGHWAY)... |
| 127 | `CityRuns.F_RAIL` | `TilePainter.RAIL` | ...the railway's track (TilePainter.RAIL, DistrictPlan.FIXED_RAIL)... |
| 129 | `CityRuns.F_RAIL_OVER` | `6` | ...the railway on a bridge over a highway (spec 2.8)... |
| 131 | `CityRuns.F_YARD` | `7` | ...a yard's ground, its track among it (DistrictPlan.FIXED_YARD): no street crosses it, no other building stands on it. |
| 134 | `CityRuns.M_RAMP` | `1` | A plot's marks (fill()'s second array): a highway's ramp (spec 2.7)... |
| 136 | `CityRuns.M_DIAG` | `2` | ...a plot of a 45-degree stretch, drawn smooth, its heading's two bits from M_DIAG_SHIFT (diagCode())... |
| 137 | `CityRuns.M_DIAG_SHIFT` | `2` |  |
| 139 | `CityRuns.M_CORNER` | `16` | ...and of those, a staircase's corner: the plot beside the stretch's line, (x + dx, y) of a step from (x, y). |
| 357 | `CityRuns.OPEN` | `0, EDGE = 1, SEA = 2` | What stops a way: nothing, the city's edge, or the sea (and fresh water wider than the net bridges). |
| 440 | `CityRuns.STEPPED` | `0, WAITS = 1, JOINED = 2, HELD = 3, YIELDS = 4` | What a step did: stepped on; waits on the ground (tried again when the ground moves); stopped for good (joined); held for want of a second plot; or waits on the other net (tried again next time). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 166 | `final int corridor, home` |  |
| 167 | `final long sx, sy` |  |
| 168 | `int head` |  |
| 169 | `long x, y` |  |
| 170 | `int steps` |  |
| 171 | `boolean joined` |  |
| 173 | `int segs` | Its stretches: each one's heading, steps, and start (the plot before its first step). |
| 174 | `int[] segHead` |  |
| 175 | `long[] segX` |  |
| 177 | `final List<long[]> crossings` | Where it crossed the net (interchanges), each {x, y, the step that made it}, in the order made. |
| 179 | `int known` | Steps known clear ahead along `head`, read at the ground's version knownAt; and the version it waits at (transient). |
| 180 | `long knownAt` |  |
| 209 | `final int kind, at, bridge` |  |
| 211 | `boolean started, hubLaid` | Its hub chosen, and whether the hub's plot is laid (a railway may stand for its yards with no track). |
| 212 | `long hubX, hubY` |  |
| 214 | `final List<long[]> corridors` | Its corridors, {0 a row \| 1 a column, the line's coordinate}, in the order opened. |
| 215 | `final List<Arm> arms` |  |
| 216 | `final List<Interchange> interchanges` |  |
| 218 | `long plots` | Plots laid: the union of its arms' plots and its hub's. |
| 220 | `long stuckAt` | The ground's version at which it last could lay no more (transient): it waits until the ground moves. |
| 222 | `final Map<Long, long[]> buckets` | Its stretches by district: district key -> {arm << 24 \| stretch} entries, and how many. |
| 223 | `final Map<Long, Integer> bucketN` |  |
| 266 | `final Net highways` |  |
| 267 | `final Net rail` |  |
| 270 | `final List<long[]> yards` | The railway's yards: each {x0, y0, w, h} in world plots, in the order laid. |
| 273 | `long highwayShort, railShort` | What the model has that the runs could not lay: highway plots, track plots, yards (counted: the legend says so; a yard with no place is drawn by its district's plan). |
| 274 | `int yardsShort` |  |
| 277 | `long version` | A stamp of everything laid: what the map's plans and views are kept against. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 73 | 1084 | **type** `public final class CityRuns` | The city's highways and railway as runs on corridors: each laid month by month from its hub's lines out, straight by preference, never moved, with the railway's yards on its track nearest the mines - what the city map... |

### THE DIALS (lines 75-140)

### THE GROUND THE RUNS READ (lines 141-159)

| line | len | member | says |
|---:|---:|---|---|
| 146 | 8 | **type** `public interface Ground` | The ground runs are laid on: the city map's (CityMap.RunGround). |
| 148 | 1 | `int at(long x, long y, int dir)` _(in CityRuns.Ground)_ | The ground at world plot (x, y), read along heading `dir` (0 to 7, or -1): a World class, or -1 where the city does not own it. |
| 150 | 1 | `long version()` _(in CityRuns.Ground)_ | A number that moves whenever the city's ground does (a purchase): what a waiting arm and a stuck net wait on. |
| 152 | 1 | `long[] box()` _(in CityRuns.Ground)_ | The city's owned box, {x0, y0, x1, y1} in plots, inclusive: how far out a corridor is sought. |
| 156 | 3 | `static boolean dry(int g)` | Whether a ground class is the city's dry ground: owned, not water. |

### A NET: ITS HUB, CORRIDORS AND ARMS (lines 160-286)

| line | len | member | says |
|---:|---:|---|---|
| 165 | 38 | **type** `static final class Arm` | One arm: from its start along its corridor, its straight stretches in the order laid. |
| 182 | 4 | `Arm(int corridor, int home, long sx, long sy)` _(in CityRuns.Arm)_ |  |
| 188 | 4 | `void push(int h)` _(in CityRuns.Arm)_ | A step along h from where it stands: its stretch lengthened, or a new one begun. |
| 193 | 9 | `void append(int h, int steps, long fromX, long fromY)` _(in CityRuns.Arm)_ |  |
| 205 | 1 | **type** `record Interchange(long x, long y, int corridor)` | A corridor's start: where it began on the net (an interchange; the spine's at the hub). |
| 208 | 57 | **type** `public static final class Net` | One net: the highways or the railway. |
| 225 | 1 | `Net(int kind, int at, int bridge)` _(in CityRuns.Net)_ |  |
| 228 | 1 | `public long plots()` _(in CityRuns.Net)_ | Plots laid. |
| 230 | 1 | `public int corridors()` _(in CityRuns.Net)_ | Corridors opened. |
| 232 | 1 | `public int interchanges()` _(in CityRuns.Net)_ | Interchanges: corridors' starts (the hub's among them) and arms' crossings. |
| 234 | 6 | `public List<long[]> interchangePlots()` _(in CityRuns.Net)_ | ...as {x, y}: a harness's. |
| 241 | 1 | `public int arms()` _(in CityRuns.Net)_ | Arms. |
| 243 | 1 | `public long[] hub()` _(in CityRuns.Net)_ | Its hub, {x, y}, or null before it starts. |
| 245 | 5 | `public int bends()` _(in CityRuns.Net)_ | Stretches off their arms' headings: 45-degree runs along a shore or the city's edge. |
| 251 | 1 | `public int joined()` _(in CityRuns.Net)_ | Arms ended where they met the net (joined). |
| 254 | 5 | `public List<long[]> stretches()` _(in CityRuns.Net)_ | Each arm's stretches as {x0, y0, heading, steps} (x0, y0 the plot before its first step), arm by arm: a harness's. |
| 260 | 4 | `void reset()` _(in CityRuns.Net)_ |  |
| 279 | 1 | `public Net highways()` |  |
| 280 | 1 | `public Net rail()` |  |
| 281 | 1 | `public List<long[]> yards()` |  |
| 282 | 1 | `public long highwayShort()` |  |
| 283 | 1 | `public long railShort()` |  |
| 284 | 1 | `public int yardsShort()` |  |
| 285 | 1 | `public long version()` |  |

### WHERE A NET'S PLOTS ARE (lines 287-351)

| line | len | member | says |
|---:|---:|---|---|
| 291 | 3 | `static long districtKey(long x, long y)` |  |
| 296 | 12 | `static void file(Net n, int a, int s, long x, long y)` | Files stretch s of arm a under the district of plot (x, y). |
| 310 | 20 | `static boolean onStretch(Arm a, int s, long px, long py)` | Whether stretch s of arm a covers world plot (px, py): one of its steps' plots, or a 45-degree step's corner. |
| 332 | 14 | `static int coverer(Net n, long x, long y, int not)` | The arm of net n covering plot (x, y) other than `not` (-1 for any): its index, -2 for the hub, -1 for none. |
| 348 | 3 | `public static boolean covered(Net n, long x, long y)` | Whether net n has a plot at (x, y). |

### GROWING A NET (lines 352-662)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 36 | `static int clear(Net n, Ground g, long x, long y, int d, int k, int[] why)` | The steps along heading d from (x, y) the net may take, up to k (or a bridge's past it), and into why[0] what stops it: ground the city does not own, the sea, water wider than its bridge, a staircase's corner off the ... |
| 404 | 5 | `static void lay(Net n, int a, int s, long x, long y)` | Lays net n's plot (x, y) on arm a's stretch s: counted where nothing else of the net covers it yet (an arm never covers its own new plot: it never turns back). |
| 411 | 27 | `static boolean start(Net n, Ground g, long ax, long ay)` | Chooses net n's hub: the line crossing nearest (ax, ay) that is the city's dry ground, within HUB_REACH cells; false where none is. |
| 448 | 38 | `static boolean grow(Net n, Net other, long target, Ground g, long ax, long ay)` | Grows net n toward `target` plots (hw.py's grow()): its hub laid first, then each plot to the shortest arm of the earliest corridor that can still grow, a new corridor when none can. |
| 494 | 66 | `static int step(Net n, Net other, int i, Arm a, Ground g, long target, int[] why)` | One step of arm i (hw.py's _step, with the cost rule): STEPPED; WAITS (the city's edge, the sea with no way along it, a T it does not cross, the other net's run along its way); JOINED (it met the net running its own w... |
| 568 | 36 | `static boolean newCorridor(Net n, Ground g)` | The next corridor (hw.py's _new_corridor()): the parallel line k districts out, k from 1, that crosses the net already laid on the city's ground - at its plot there nearest the hub, an interchange - the nearest of tho... |
| 606 | 7 | `static boolean before(double d, int kind, long v, long[] p, double d2, int kind2, long v2, long[] p2)` | hw.py's order of candidates (distance, kind - 'col' before 'row' -, line, plot): whether the first comes before the second. |
| 615 | 47 | `static long[] nearestOnLine(Net n, Ground g, int kind, long v)` | The plot of net n on the row (kind 0) or column (1) at v, on the city's ground, nearest its hub; null for none. |

### TAKING FROM THE NEWEST END (lines 663-745)

| line | len | member | says |
|---:|---:|---|---|
| 668 | 56 | `static boolean trim(Net n, long target)` | Takes net n back to `target` plots, each from the newest end - the latest corridor's longest arm, its last step (the reverse of grow()'s order); the hub last. |
| 726 | 19 | `static void rebuildBuckets(Net n)` | Files every stretch of net n again (after a corridor is closed, or the runs read back). |

### THE RAILWAY'S YARDS (lines 746-841)

| line | len | member | says |
|---:|---:|---|---|
| 759 | 16 | `void layYards(int target, int w, int h, Ground g, List<long[]> mines)` | The yards toward `target` (spec 2.8): each on the track, in the cell along it nearest a mine (`mines`, the city's mined sites, each {x0, y0, x1, y1} inclusive), its w x h box across the track's line inside the cell - ... |
| 777 | 36 | `List<long[]> yardSpots(int w, int h, List<long[]> mines)` | Every box a yard could take, the nearest a mine first: two a cell the track runs straight through on its line, one at either end of it. |
| 815 | 12 | `static void spotsIn(List<long[]> out, long cx, long cy, boolean row, int w, int h)` | A cell's yard boxes across its quarter line (a row's, or a column's): inside the cell's interior, covering the line, at either end of the cell. |
| 829 | 12 | `boolean yardFits(long[] b, Ground g, List<long[]> mines, List<long[]> laid)` | Whether a yard's box is free, the yards `laid` before it: owned dry ground, off the highways and their verges, no other yard's, no mine's site. |

### THE MONTH: TO THE MODEL'S COUNTS (lines 842-882)

| line | len | member | says |
|---:|---:|---|---|
| 853 | 17 | `public boolean layTo(long highwayPlots, long trackPlots, int yardCount, int yardW, int yardH, Ground g, long fx, long fy, long ...` | Lays the runs to the model's plots of highway and of track, and its yards (w x h each), over the ground: each net grown, or taken back from its newest end - the highways from the founding site (fx, fy), the railway fr... |
| 872 | 10 | `long signature()` | A hash of everything laid. |

### READING THEM: A BOX OF PLOTS (lines 883-1041)

| line | len | member | says |
|---:|---:|---|---|
| 895 | 46 | `public long fill(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean yardsOver)` | What the runs lay on the w x h plots from world plot (x0, y0), row by row: into fixed, F_HIGHWAY, F_RAIL, F_RAIL_OVER (the railway over a highway) and, `yardsOver`, F_YARD over every plot of a yard (a plan's ground: n... |
| 948 | 29 | `public long frameHash(long x0, long y0, int w, int h)` | A hash of what the runs lay on the w x h plots from (x0, y0) - each stretch's steps there, the hub, the arms' ends (their ramps), the yards - without drawing them: what a district's plan is kept against (CityMap.ownSt... |
| 979 | 14 | `static long[] range(Arm a, int s, long x0, long y0, int w, int h)` | The steps of stretch s of arm a whose plots (or corners) may fall in the box: {first, last}; first > last for none. |
| 994 | 6 | `private static void put(byte[] fixed, int w, long x, long y, byte code)` |  |
| 1002 | 27 | `private static long stretchInto(Net n, Arm a, int s, long x0, long y0, int w, int h, byte[] fixed, byte[] marks, byte code)` | One stretch's plots inside the box, into fixed and marks; a hash of its part there. |
| 1031 | 3 | `static int diagCode(int h)` | A 45-degree heading's two bits: south-east 0, south-west 1, north-west 2, north-east 3. |
| 1036 | 5 | `public byte at(long x, long y)` | What the runs carry at world plot (x, y): F_YARD, F_RAIL_OVER, F_HIGHWAY, F_RAIL, or 0. |

### THE SIDECAR (lines 1042-1156)

| line | len | member | says |
|---:|---:|---|---|
| 1047 | 3 | `public int runs()` | The sidecar's count of runs: the arms of both nets, and one for the yards (0: none laid). |
| 1052 | 31 | `void write(DataOutputStream out) throws IOException` | Writes the runs after their count (CityMap's FORMAT 5): each net's hub, corridors, arms stretch by stretch and interchanges; the yards; what was short. |
| 1085 | 50 | `static CityRuns read(DataInputStream in) throws IOException` | The runs write() wrote, read back; null when they do not add up. |
| 1137 | 19 | `public boolean same(CityRuns o)` | Whether two cities' runs are the same: each net's hub, plots, corridors and arms stretch for stretch, and the yards. |

