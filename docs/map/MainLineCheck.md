# MainLineCheck.java - 1,743 lines · 56 methods · 4 constants · harnesses

`ham/citybuildersim/MainLineCheck.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> The main line is the railway's run from its gateway to the freight station,
> the yard nearest the gate arm's end, and it is drawn double where the city
> has a Freight Line.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. NO YARD: with a gateway and track but no yard, fill() marks M_MAIN on
>      the whole gate arm (the hub and the hub's row toward the gateway) and
>      on no other plot of the track, and freightStation() is null.
> 
>   2. A YARD ON THE GATE ARM: M_MAIN runs from the yard's anchor out to the
>      arm's end, not between the hub and the yard.
> 
>   3. A YARD ON THE OPPOSITE ARM: the whole gate arm, the hub and the
>      opposite arm from the hub to the yard are M_MAIN; the opposite arm
>      beyond the yard is not.
> 
>   4. A YARD ON A LATER CORRIDOR: the chain through its interchange is
>      M_MAIN - the later arm from its start to the yard and not beyond it,
>      and the earlier arm's plot at the interchange.
> 
>   5. TWO YARDS: the one nearer the gate arm's end is freightStation().
> 
>   6. NO GATEWAY: nothing is marked M_MAIN, and freightStation() is null.
> 
>   7. DOUBLE TRACK: setDoubleMain(true) marks M_DOUBLE on exactly the
>      M_MAIN plots; false marks none; signature() differs between the two.
> 
>   8. THE HASHES: fill()'s hash with marks changes when a nearer yard moves
>      the station over a box on the old path; with marks == null it does
>      not, and frameHash() does not.
> 
>   9. ON A REAL MAP: MapCheck.yardCity() has a Freight Line, so
>      runs().doubleMain() is true, its freightStation() is not null and some
>      tile's marks carry M_MAIN | M_DOUBLE; with NO_GATEWAY on that seed,
>      nothing is marked instead.
> 
>  10. A GATE ARM THAT BENDS: an arm of three stretches - east, south-east,
>      east - is marked step for step, each 45-degree step's corner with it:
>      a stretch's steps are counted from the arm's start, not the stretch's.
> 
>  11. THE MAIN ARM (0.8.3, batch FX2): where the gate arm waits at the sea
>      the city owns ahead of it and the opposite arm runs to the city's
>      edge, the main line, the freight station and the trains' way run from
>      the arm the map sets (CityRuns.setMainArm(), mainArm()). With none set
>      mainArm() is the gate arm and mainLine() the gate arm's; with the west
>      arm set, the freight station is the yard nearest its end, the M_MAIN
>      marks lie on the west arm from that yard's anchor and on no plot of
>      the gate arm, and wayTo() starts at the west arm's end plot's middle;
>      -1 and an arm with no steps fall back to the gate arm; and a yard on a
>      corridor that starts on the main arm is reached along that arm, no
>      plot of the way twice and every quarter plot of it on track.
> 
>  12. TWO YARDS STACKED ON A CORRIDOR (batch FX3, red until the fix): yard B
>      stands over a north-south corridor's interchange with the spine, yard
>      A a plot above it on the corridor's north arm, so A's way runs north
>      through B's box. Each yard's routes (CityMap.trainRoutesInto()) end on
>      north-south sidings, enter the fan at an apex on the corridor's
>      column, and no leg of any route crosses a siding.
> 
> ... (56 more lines in the source)

**Uses:** [CityRuns](CityRuns.md) (241), [TrainSchedule](TrainSchedule.md) (30), [CityMap](CityMap.md) (23), [TilePainter](TilePainter.md) (19), [DistrictPlan](DistrictPlan.md) (19), [BuildingVisual](BuildingVisual.md) (10), [JunctionCheck](JunctionCheck.md) (9), [World](World.md) (8), [MapCheck](MapCheck.md) (6), [Ports](Ports.md) (5), [TileRaster](TileRaster.md) (4), [BuildingCatalog](BuildingCatalog.md) (2), [MapTiles](MapTiles.md) (2), [LandMap](LandMap.md) (1)

**Used by (2):** [JunctionCheck](JunctionCheck.md), [MapCheck](MapCheck.md)

## Sections

| line | section |
|---:|---|
| 206 | · 1. no yard |
| 239 | · 2. a yard on the gate arm |
| 271 | · 3. a yard on the opposite arm |
| 313 | · 4. a yard on a later corridor |
| 355 | · 5. two yards |
| 371 | · 6. no gateway |
| 385 | · 7. double track |
| 412 | · 8. the hashes |
| 435 | · 9. on a real map |
| 450 | · 10. a gate arm that bends |
| 574 | 11. the main arm (FX2) |
| 699 | 12. two yards stacked on a corridor (FX3) |
| 842 | 13. the railway's ways (FR7 slice 3) |
| 1561 | 16. the railway's corner and its dead end (MF2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 717 | `MainLineCheck.YARD_SIDE` | `24` | Plots: a yard's box a side in the fixture, his Rail Terminals' (CityRuns.spotsIn() lays one at either end of its cell: at 1, or CELL - side). |
| 1571 | `MainLineCheck.COAST_AT` | `60, COAST_BAY = 125, COAST_SUM = 214, COAST_TIP = 40` | MF2, 2026-11-08. |
| 1628 | `MainLineCheck.HIGHWAYS_GOLDEN` | `900` | What the highways' 900 plots on the coast above laid on main at 0.9.49 (MF2, 2026-11-08), before the railway's rules changed: their count and the hash of their stretches. |
| 1629 | `MainLineCheck.HIGHWAYS_SHAPE` | `- 1584424669` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 129 | `static int fails` |  |
| 138 | `final long x0, y0, x1, y1` |  |
| 498 | `final long seaX` |  |
| 1574 | `final long hx, hy` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 127 | 1617 | **type** `public class MainLineCheck` | The main line is the railway's run from its gateway to the freight station, the yard nearest the gate arm's end, and it is drawn double where the city has a Freight Line. |
| 131 | 4 | `static void check(String label, boolean ok)` |  |
| 137 | 15 | **type** `static class BoxGround implements CityRuns.Ground` | A square of owned dry ground the runs may lay on, -1 outside: the GatewayCheck fixture's shape. |
| 140 | 3 | `BoxGround(long x0, long y0, long x1, long y1)` _(in MainLineCheck.BoxGround)_ |  |
| 144 | 3 | `public int at(long x, long y, int dir)` _(in MainLineCheck.BoxGround)_ |  |
| 148 | 1 | `public long version()` _(in MainLineCheck.BoxGround)_ |  |
| 150 | 1 | `public long[] box()` _(in MainLineCheck.BoxGround)_ |  |
| 153 | 1 | `static BoxGround ground(long x0, long y0, long x1, long y1)` |  |
| 155 | 3 | `static void layRail(CityRuns runs, BoxGround g, long track, long siteX, long siteY)` |  |
| 167 | 4 | `static void layOldArms(CityRuns runs, BoxGround g, long track, long siteX, long siteY)` | The nets the railway was laid as until FR7 slice 3: every arm grown from the hub (CityRuns.grow(), still the highways'), the gate arm first, then the arm opposite it, the perpendicular pair and new corridors. |
| 173 | 13 | `static long[] plotOnArm(CityRuns.Arm a, int step)` | The main plot of step k along an arm: step 0 its start plot. |
| 187 | 4 | `static void addYardAtStep(CityRuns runs, int arm, int step)` |  |
| 192 | 5 | `static int markAt(long x0, long y0, int w, byte[] marks, long x, long y)` |  |
| 198 | 4 | `static long fillBox(CityRuns runs, BoxGround g, byte[] fixed, byte[] marks)` |  |
| 203 | 292 | `public static void main(String[] args)` |  |
| 497 | 13 | **type** `static class SeaGround extends BoxGround` | BoxGround with the city's own sea in it: the plots from seaX east to the box's east edge are owned salt water, the rest owned grass (FX2: Jerus's Halifax, where the gate arm waits at the sea the city owns beyond it). |
| 500 | 4 | `SeaGround(long x0, long y0, long x1, long y1, long seaX)` _(in MainLineCheck.SeaGround)_ |  |
| 505 | 4 | `public int at(long x, long y, int dir)` _(in MainLineCheck.SeaGround)_ |  |
| 512 | 6 | `static CityRuns seaCity(SeaGround g, long track, long siteX, long siteY)` | A railway of `track` plots laid on g with its gateway east, from the site (siteX, siteY). |
| 520 | 4 | `static int westArm(CityRuns.Net rail)` | The index of the arm opposite the gate arm of a gateway east: corridor 0, home heading west (4); -1 for none. |
| 526 | 12 | `static int[] mainByArm(CityRuns runs, BoxGround g)` | The plots fill() marks M_MAIN over the box, counted by the arm locate() puts them on; the last entry counts those on no arm (the hub's plot among them). |
| 540 | 5 | `static int others(int[] by, int keep)` | What the counts hold apart from entry keep: the plots on every other arm and on none. |
| 552 | 21 | `static int[] walkWay(CityRuns runs, double[][] way)` | A way (wayTo()'s points, each a plot's middle) walked every quarter plot: {positions walked, positions that enter a plot the walk had already left (a way doubling back), positions on a plot the railway does not cover}. |

### 11. the main arm (FX2) (lines 574-698)

| line | len | member | says |
|---:|---:|---|---|
| 583 | 115 | `static void theMainArm(long siteX, long siteY)` | Section 11. |

### 12. two yards stacked on a corridor (FX3) (lines 699-841)

| line | len | member | says |
|---:|---:|---|---|
| 702 | 3 | `static double turn(double[] a, double[] b, double[] c)` | The cross product of (b - a) and (c - a): its sign is the side of a-b that c lies on. |
| 707 | 3 | `static boolean crossesInside(double[] a, double[] b, double[] c, double[] d)` | Whether segments a-b and c-d cross at a point strictly inside both: parallel, collinear and touching segments do not. |
| 712 | 3 | `static double[] pointOf(TrainSchedule.Route r, int k)` | Route r's point k as {x, y}. |
| 729 | 112 | `static void stackedOnACorridor(long siteX, long siteY)` | Section 12. |

### 13. the railway's ways (FR7 slice 3) (lines 842-1560)

| line | len | member | says |
|---:|---:|---|---|
| 845 | 3 | `static long[] box(long x0, long y0, int w, int h)` | A works' box {x0, y0, x1, y1} (inclusive) from its north-west plot and its sides. |
| 849 | 3 | `static long key(long x, long y)` |  |
| 853 | 3 | `static boolean insideBox(long[] b, long x, long y)` |  |
| 858 | 3 | `static boolean beside(long[] b, long x, long y)` | Whether plot (x, y) is outside box b and next to it on a side (a siding's last plot, beside its works). |
| 862 | 3 | `static boolean inYard(long[] yard, long x, long y)` |  |
| 867 | 13 | `static Set<Long> walkedPlots(double[][] way)` | The plots a way (points, each a plot's middle) walks, every quarter plot. |
| 882 | 10 | `static int plotsNearHighways(CityRuns runs, CityRuns.Arm a)` | The plots of every arm of net n with steps laid (the arms' plots, not the hub's) that a highway's plot is next to or on: those within one plot of a highway. |
| 902 | 237 | `static void railWays(long siteX, long siteY, CityMap real, MapCheck.Squares sq)` | Section 13. |
| 1141 | 80 | `static void trainsToTheSidings(long siteX, long siteY, CityRuns runs, CityMap real, MapCheck.Squares sq)` | The trains: a route to every siding, a bulk unit train's alone, on the fixture's net and on a real map. |
| 1223 | 37 | `static void sidingRoutes(CityMap map, String what, boolean maybeNone)` | Section 13's trains on a map: one bulk route to every siding the trains' way reaches, each ending on its siding's last plot; every other route a yard's. |
| 1262 | 4 | `static int[] depotYard()` | The Rail Depot's yard size, {across, down}: BuildingVisual.footprint() of the catalogue's depot, the size CityMap.layRuns() gives the runs. |
| 1268 | 8 | `static int armInside(CityRuns.Arm gate, long[] yard)` | How many plots of the net's arm `gate` (its start and every step) lie inside the yard's box {x, y, w, h}. |
| 1278 | 52 | `static void stationAlone(long siteX, long siteY)` | Section 14: a station alone lays its main line (batch FX5). |
| 1332 | 4 | `static int[] terminalYard()` | The Rail Terminal's yard size, {across, down}: BuildingVisual.footprint() of the catalogue's terminal that is no depot. |
| 1338 | 6 | `static CityRuns yardsRun(BoxGround g, List<int[]> sizes, long track)` | A yard laid on the long fixture's ground: the gateway east, the founding site JunctionCheck.SITE, no mines, `track` plots of track, the sizes asked in order. |
| 1346 | 4 | `static boolean layYards(CityRuns r, BoxGround g, List<int[]> sizes, long track)` | ...laid (again) to the sizes and track given. |
| 1352 | 4 | `static JunctionCheck.PatchGround longGround()` | The ground of the long fixture: owned grass round the founding site, as JunctionCheck lays its cities (the frame at the world's origin lies inside it). |
| 1358 | 14 | `static int[] lineOf(CityRuns runs, long[] y)` | A yard's line: {along, across, 1 for a row}, the RAIL_AT line of its cell through its box being track the box's whole length; null where it is not. |
| 1374 | 5 | `static boolean coversRing(long[] y, int[] line)` | Whether the yard's line holds a ring plot (a multiple of CELL along it): the box runs on across the street between two cells. |
| 1381 | 9 | `static int streetsOnYards(DistrictPlan p, List<long[]> yards)` | How many plots of the yards' boxes (in the frame at the world's origin) the plan lays a street on. |
| 1392 | 168 | `static void longYards(CityMap real)` | Section 15: a yard is as long as a unit train along its line (batch FX5 Y1). |

### 16. the railway's corner and its dead end (MF2) (lines 1561-1743)

| line | len | member | says |
|---:|---:|---|---|
| 1573 | 16 | **type** `static class CoastGround extends BoxGround` |  |
| 1576 | 5 | `CoastGround(long x0, long y0, long x1, long y1, long hx, long hy)` _(in MainLineCheck.CoastGround)_ |  |
| 1582 | 6 | `public int at(long x, long y, int dir)` _(in MainLineCheck.CoastGround)_ |  |
| 1591 | 4 | `static int turn(int h1, int h2)` | The turn between two headings, in 45-degree units: 0 straight on to 4 about face. |
| 1597 | 5 | `static int sharpestCorner(CityRuns.Net n)` | The sharpest corner between one stretch of an arm and the next over the net's arms, in 45-degree units. |
| 1604 | 10 | `static double sharpestWayCorner(double[][] way)` | The sharpest corner of a way's points (each a plot's middle), in degrees: 0 straight on. |
| 1616 | 10 | `static String shape(CityRuns.Net n)` | Each arm's stretches as text, heading x steps in order: what the log shows of a net's shape. |
| 1632 | 6 | `static long[] flat(CityRuns.Net n)` | A net's stretches as one array, arm by arm: {x0, y0, heading, steps, home} each. |
| 1640 | 10 | `static boolean walkWayOk(CityRuns runs)` | Whether each yard's way from the main arm's end walks only plots the railway covers, and no plot twice. |
| 1651 | 77 | `static void railCorner()` |  |
| 1729 | 14 | `static boolean marksCarry(CityMap m, int bits)` |  |

