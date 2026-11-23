# MainLineCheck.java - 2,032 lines · 67 methods · 7 constants · harnesses

`ham/citybuildersim/MainLineCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

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
> ... (70 more lines in the source)

**Uses:** [CityRuns](CityRuns.md) (290), [TrainSchedule](TrainSchedule.md) (56), [CityMap](CityMap.md) (25), [DistrictPlan](DistrictPlan.md) (20), [TilePainter](TilePainter.md) (19), [World](World.md) (16), [JunctionCheck](JunctionCheck.md) (12), [BuildingVisual](BuildingVisual.md) (10), [Ports](Ports.md) (7), [MapCheck](MapCheck.md) (6), [TileRaster](TileRaster.md) (5), [BuildingCatalog](BuildingCatalog.md) (2), [MapTiles](MapTiles.md) (2), [LandMap](LandMap.md) (1), [GatewayCheck](GatewayCheck.md) (1)

**Used by (2):** [JunctionCheck](JunctionCheck.md), [MapCheck](MapCheck.md)

## Sections

| line | section |
|---:|---|
| 220 | · 1. no yard |
| 253 | · 2. a yard on the gate arm |
| 285 | · 3. a yard on the opposite arm |
| 327 | · 4. a yard on a later corridor |
| 369 | · 5. two yards |
| 385 | · 6. no gateway |
| 399 | · 7. double track |
| 426 | · 8. the hashes |
| 449 | · 9. on a real map |
| 464 | · 10. a gate arm that bends |
| 590 | 11. the main arm (FX2) |
| 715 | 12. two yards stacked on a corridor (FX3) |
| 858 | 13. the railway's ways (FR7 slice 3) |
| 1577 | 16. the railway's corner and its dead end (MF2) |
| 1945 | (RR1) a main line that reaches no edge runs on past no train's way |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 733 | `MainLineCheck.YARD_SIDE` | `24` | Plots: a yard's box a side in the fixture, his Rail Terminals' (CityRuns.spotsIn() lays one at either end of its cell: at 1, or CELL - side). |
| 1587 | `MainLineCheck.COAST_AT` | `60, COAST_BAY = 125, COAST_SUM = 214, COAST_TIP = 40` | MF2, 2026-11-08. |
| 1644 | `MainLineCheck.HIGHWAYS_GOLDEN` | `900` | What the highways' 900 plots on the coast above laid on main at 0.9.49 (MF2, 2026-11-08), before the railway's rules changed: their count and the hash of their stretches. |
| 1645 | `MainLineCheck.HIGHWAYS_SHAPE` | `- 1584424669` |  |
| 1892 | `MainLineCheck.STY1_CANAL` | `24` | Section 17 (c)'s canal: salt water on the quarter line's row and the row south of it from this many plots east of the hub, so its track on the quarter line holds a mixed train's box and no longer (STY1). |
| 1895 | `MainLineCheck.STY1_DRY_IN` | `GatewayCheck.TN1_DRY_IN` | Section 17 (c)'s month of dry bulk in, in tonnes: unit trains of hoppers, as GatewayCheck's TN1 coast is given (TN1_DRY_IN). |
| 1948 | `MainLineCheck.HALF_RR1` | `60, SEA_RR1 = 30` | The island noEdge() lays its railway on: dry ground HALF_RR1 plots every way from the site, salt water the city owns SEA_RR1 plots beyond it to the box's edge. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 143 | `static int fails` |  |
| 152 | `final long x0, y0, x1, y1` |  |
| 514 | `final long seaX` |  |
| 1590 | `final long hx, hy` |  |
| 1952 | `final long cx, cy, half` |  |
| 1953 | `final boolean openWest` |  |
| 1954 | `long version` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 141 | 1892 | **type** `public class MainLineCheck` | The main line is the railway's run from its gateway to the freight station, the yard nearest the gate arm's end, and it is drawn double where the city has a Freight Line. |
| 145 | 4 | `static void check(String label, boolean ok)` |  |
| 151 | 15 | **type** `static class BoxGround implements CityRuns.Ground` | A square of owned dry ground the runs may lay on, -1 outside: the GatewayCheck fixture's shape. |
| 154 | 3 | `BoxGround(long x0, long y0, long x1, long y1)` _(in MainLineCheck.BoxGround)_ |  |
| 158 | 3 | `public int at(long x, long y, int dir)` _(in MainLineCheck.BoxGround)_ |  |
| 162 | 1 | `public long version()` _(in MainLineCheck.BoxGround)_ |  |
| 164 | 1 | `public long[] box()` _(in MainLineCheck.BoxGround)_ |  |
| 167 | 1 | `static BoxGround ground(long x0, long y0, long x1, long y1)` |  |
| 169 | 3 | `static void layRail(CityRuns runs, BoxGround g, long track, long siteX, long siteY)` |  |
| 181 | 4 | `static void layOldArms(CityRuns runs, BoxGround g, long track, long siteX, long siteY)` | The nets the railway was laid as until FR7 slice 3: every arm grown from the hub (CityRuns.grow(), still the highways'), the gate arm first, then the arm opposite it, the perpendicular pair and new corridors. |
| 187 | 13 | `static long[] plotOnArm(CityRuns.Arm a, int step)` | The main plot of step k along an arm: step 0 its start plot. |
| 201 | 4 | `static void addYardAtStep(CityRuns runs, int arm, int step)` |  |
| 206 | 5 | `static int markAt(long x0, long y0, int w, byte[] marks, long x, long y)` |  |
| 212 | 4 | `static long fillBox(CityRuns runs, BoxGround g, byte[] fixed, byte[] marks)` |  |
| 217 | 294 | `public static void main(String[] args)` |  |
| 513 | 13 | **type** `static class SeaGround extends BoxGround` | BoxGround with the city's own sea in it: the plots from seaX east to the box's east edge are owned salt water, the rest owned grass (FX2: Jerus's Halifax, where the gate arm waits at the sea the city owns beyond it). |
| 516 | 4 | `SeaGround(long x0, long y0, long x1, long y1, long seaX)` _(in MainLineCheck.SeaGround)_ |  |
| 521 | 4 | `public int at(long x, long y, int dir)` _(in MainLineCheck.SeaGround)_ |  |
| 528 | 6 | `static CityRuns seaCity(SeaGround g, long track, long siteX, long siteY)` | A railway of `track` plots laid on g with its gateway east, from the site (siteX, siteY). |
| 536 | 4 | `static int westArm(CityRuns.Net rail)` | The index of the arm opposite the gate arm of a gateway east: corridor 0, home heading west (4); -1 for none. |
| 542 | 12 | `static int[] mainByArm(CityRuns runs, BoxGround g)` | The plots fill() marks M_MAIN over the box, counted by the arm locate() puts them on; the last entry counts those on no arm (the hub's plot among them). |
| 556 | 5 | `static int others(int[] by, int keep)` | What the counts hold apart from entry keep: the plots on every other arm and on none. |
| 568 | 21 | `static int[] walkWay(CityRuns runs, double[][] way)` | A way (wayTo()'s points, each a plot's middle) walked every quarter plot: {positions walked, positions that enter a plot the walk had already left (a way doubling back), positions on a plot the railway does not cover}. |

### 11. the main arm (FX2) (lines 590-714)

| line | len | member | says |
|---:|---:|---|---|
| 599 | 115 | `static void theMainArm(long siteX, long siteY)` | Section 11. |

### 12. two yards stacked on a corridor (FX3) (lines 715-857)

| line | len | member | says |
|---:|---:|---|---|
| 718 | 3 | `static double turn(double[] a, double[] b, double[] c)` | The cross product of (b - a) and (c - a): its sign is the side of a-b that c lies on. |
| 723 | 3 | `static boolean crossesInside(double[] a, double[] b, double[] c, double[] d)` | Whether segments a-b and c-d cross at a point strictly inside both: parallel, collinear and touching segments do not. |
| 728 | 3 | `static double[] pointOf(TrainSchedule.Route r, int k)` | Route r's point k as {x, y}. |
| 745 | 112 | `static void stackedOnACorridor(long siteX, long siteY)` | Section 12. |

### 13. the railway's ways (FR7 slice 3) (lines 858-1576)

| line | len | member | says |
|---:|---:|---|---|
| 861 | 3 | `static long[] box(long x0, long y0, int w, int h)` | A works' box {x0, y0, x1, y1} (inclusive) from its north-west plot and its sides. |
| 865 | 3 | `static long key(long x, long y)` |  |
| 869 | 3 | `static boolean insideBox(long[] b, long x, long y)` |  |
| 874 | 3 | `static boolean beside(long[] b, long x, long y)` | Whether plot (x, y) is outside box b and next to it on a side (a siding's last plot, beside its works). |
| 878 | 3 | `static boolean inYard(long[] yard, long x, long y)` |  |
| 883 | 13 | `static Set<Long> walkedPlots(double[][] way)` | The plots a way (points, each a plot's middle) walks, every quarter plot. |
| 898 | 10 | `static int plotsNearHighways(CityRuns runs, CityRuns.Arm a)` | The plots of every arm of net n with steps laid (the arms' plots, not the hub's) that a highway's plot is next to or on: those within one plot of a highway. |
| 918 | 237 | `static void railWays(long siteX, long siteY, CityMap real, MapCheck.Squares sq)` | Section 13. |
| 1157 | 80 | `static void trainsToTheSidings(long siteX, long siteY, CityRuns runs, CityMap real, MapCheck.Squares sq)` | The trains: a route to every siding, a bulk unit train's alone, on the fixture's net and on a real map. |
| 1239 | 37 | `static void sidingRoutes(CityMap map, String what, boolean maybeNone)` | Section 13's trains on a map: one bulk route to every siding the trains' way reaches, each ending on its siding's last plot; every other route a yard's. |
| 1278 | 4 | `static int[] depotYard()` | The Rail Depot's yard size, {across, down}: BuildingVisual.footprint() of the catalogue's depot, the size CityMap.layRuns() gives the runs. |
| 1284 | 8 | `static int armInside(CityRuns.Arm gate, long[] yard)` | How many plots of the net's arm `gate` (its start and every step) lie inside the yard's box {x, y, w, h}. |
| 1294 | 52 | `static void stationAlone(long siteX, long siteY)` | Section 14: a station alone lays its main line (batch FX5). |
| 1348 | 4 | `static int[] terminalYard()` | The Rail Terminal's yard size, {across, down}: BuildingVisual.footprint() of the catalogue's terminal that is no depot. |
| 1354 | 6 | `static CityRuns yardsRun(BoxGround g, List<int[]> sizes, long track)` | A yard laid on the long fixture's ground: the gateway east, the founding site JunctionCheck.SITE, no mines, `track` plots of track, the sizes asked in order. |
| 1362 | 4 | `static boolean layYards(CityRuns r, BoxGround g, List<int[]> sizes, long track)` | ...laid (again) to the sizes and track given. |
| 1368 | 4 | `static JunctionCheck.PatchGround longGround()` | The ground of the long fixture: owned grass round the founding site, as JunctionCheck lays its cities (the frame at the world's origin lies inside it). |
| 1374 | 14 | `static int[] lineOf(CityRuns runs, long[] y)` | A yard's line: {along, across, 1 for a row}, the RAIL_AT line of its cell through its box being track the box's whole length; null where it is not. |
| 1390 | 5 | `static boolean coversRing(long[] y, int[] line)` | Whether the yard's line holds a ring plot (a multiple of CELL along it): the box runs on across the street between two cells. |
| 1397 | 9 | `static int streetsOnYards(DistrictPlan p, List<long[]> yards)` | How many plots of the yards' boxes (in the frame at the world's origin) the plan lays a street on. |
| 1408 | 168 | `static void longYards(CityMap real)` | Section 15: a yard is as long as a unit train along its line (batch FX5 Y1). |

### 16. the railway's corner and its dead end (MF2) (lines 1577-1944)

| line | len | member | says |
|---:|---:|---|---|
| 1589 | 16 | **type** `static class CoastGround extends BoxGround` |  |
| 1592 | 5 | `CoastGround(long x0, long y0, long x1, long y1, long hx, long hy)` _(in MainLineCheck.CoastGround)_ |  |
| 1598 | 6 | `public int at(long x, long y, int dir)` _(in MainLineCheck.CoastGround)_ |  |
| 1607 | 4 | `static int turn(int h1, int h2)` | The turn between two headings, in 45-degree units: 0 straight on to 4 about face. |
| 1613 | 5 | `static int sharpestCorner(CityRuns.Net n)` | The sharpest corner between one stretch of an arm and the next over the net's arms, in 45-degree units. |
| 1620 | 10 | `static double sharpestWayCorner(double[][] way)` | The sharpest corner of a way's points (each a plot's middle), in degrees: 0 straight on. |
| 1632 | 10 | `static String shape(CityRuns.Net n)` | Each arm's stretches as text, heading x steps in order: what the log shows of a net's shape. |
| 1648 | 6 | `static long[] flat(CityRuns.Net n)` | A net's stretches as one array, arm by arm: {x0, y0, heading, steps, home} each. |
| 1656 | 10 | `static boolean walkWayOk(CityRuns runs)` | Whether each yard's way from the main arm's end walks only plots the railway covers, and no plot twice. |
| 1667 | 77 | `static void railCorner()` |  |
| 1746 | 1 | `static long alongOf(long[] y)` | A yard's length along its line: the longer side of its box (a box laid at a tier is longer along than across; at its footprint's own, either). |
| 1749 | 141 | `static void stationLengthened()` | Section 17: the station's yard laid short is laid again longer, once a year, where the ground now allows it (batch TN1). |
| 1901 | 7 | `static List<TrainSchedule.Route> stationRoutes(CityRuns r, long[] yard)` | A station's routes as CityMap.trainRoutes() lays them (CityMap.trainRoutesInto(), the box's track as CityMap.yardRail() reads it), from the first point of the trains' way (wayTo()), the main line's end, as from TN1's ... |
| 1910 | 5 | `static int running(TrainSchedule s, TrainSchedule.Kind k)` | How many of the month's trains of kind k run: drawn with a route (TrainSchedule.calls(); an unfit one has none). |
| 1917 | 5 | `static double mostHeld(List<TrainSchedule.Route> routes)` | The most any of the routes' sidings holds, in plots (0 for none). |
| 1924 | 5 | `static TrainSchedule unitSchedule(List<TrainSchedule.Route> routes)` | The month's schedule on the routes with STY1_DRY_IN tonnes of dry bulk in and nothing else: unit trains of hoppers. |
| 1930 | 14 | `static boolean marksCarry(CityMap m, int bits)` |  |

### (RR1) a main line that reaches no edge runs on past no train's way (lines 1945-2032)

| line | len | member | says |
|---:|---:|---|---|
| 1951 | 18 | **type** `static class IslandGround extends BoxGround` | Salt water the city owns but for a dry square `half` plots every way from (cx, cy) - and, open west, dry on to the box's west edge, 200 plots further. |
| 1956 | 4 | `IslandGround(long cx, long cy, long half, long sea, boolean openWest)` _(in MainLineCheck.IslandGround)_ |  |
| 1961 | 5 | `public int at(long x, long y, int dir)` _(in MainLineCheck.IslandGround)_ |  |
| 1967 | 1 | `public long version()` _(in MainLineCheck.IslandGround)_ |  |
| 1971 | 13 | `static List<String> pastTheWay(CityRuns runs)` | The arms after the gate arm in spineOrder() that have steps and do not end on a yard, each "arm i: n steps to (x, y)"; empty when none. |
| 1992 | 40 | `static void noEdge(long siteX, long siteY)` | Jerus, 2026-11-20, his fjord city (Bergen, month 1,208): "rail still has dead ends". |

