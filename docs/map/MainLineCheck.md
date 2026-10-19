# MainLineCheck.java - 1,201 lines · 36 methods · 1 constants · harnesses

`ham/citybuildersim/MainLineCheck.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

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
> ... (22 more lines in the source)

**Uses:** [CityRuns](CityRuns.md) (160), [TrainSchedule](TrainSchedule.md) (26), [TilePainter](TilePainter.md) (19), [CityMap](CityMap.md) (14), [MapCheck](MapCheck.md) (6), [Ports](Ports.md) (5), [World](World.md) (3), [TileRaster](TileRaster.md) (2), [MapTiles](MapTiles.md) (2), [LandMap](LandMap.md) (1), [BuildingVisual](BuildingVisual.md) (1)

**Used by (1):** [JunctionCheck](JunctionCheck.md)

## Sections

| line | section |
|---:|---|
| 172 | · 1. no yard |
| 205 | · 2. a yard on the gate arm |
| 237 | · 3. a yard on the opposite arm |
| 279 | · 4. a yard on a later corridor |
| 321 | · 5. two yards |
| 337 | · 6. no gateway |
| 351 | · 7. double track |
| 378 | · 8. the hashes |
| 401 | · 9. on a real map |
| 416 | · 10. a gate arm that bends |
| 537 | 11. the main arm (FX2) |
| 662 | 12. two yards stacked on a corridor (FX3) |
| 800 | 13. the railway's ways (FR7 slice 3) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 680 | `MainLineCheck.YARD_SIDE` | `24` | Plots: a yard's box a side in the fixture, his Rail Terminals' (CityRuns.spotsIn() lays one at either end of its cell: at 1, or CELL - side). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 95 | `static int fails` |  |
| 104 | `final long x0, y0, x1, y1` |  |
| 461 | `final long seaX` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 93 | 1109 | **type** `public class MainLineCheck` | The main line is the railway's run from its gateway to the freight station, the yard nearest the gate arm's end, and it is drawn double where the city has a Freight Line. |
| 97 | 4 | `static void check(String label, boolean ok)` |  |
| 103 | 15 | **type** `static class BoxGround implements CityRuns.Ground` | A square of owned dry ground the runs may lay on, -1 outside: the GatewayCheck fixture's shape. |
| 106 | 3 | `BoxGround(long x0, long y0, long x1, long y1)` _(in MainLineCheck.BoxGround)_ |  |
| 110 | 3 | `public int at(long x, long y, int dir)` _(in MainLineCheck.BoxGround)_ |  |
| 114 | 1 | `public long version()` _(in MainLineCheck.BoxGround)_ |  |
| 116 | 1 | `public long[] box()` _(in MainLineCheck.BoxGround)_ |  |
| 119 | 1 | `static BoxGround ground(long x0, long y0, long x1, long y1)` |  |
| 121 | 3 | `static void layRail(CityRuns runs, BoxGround g, long track, long siteX, long siteY)` |  |
| 133 | 4 | `static void layOldArms(CityRuns runs, BoxGround g, long track, long siteX, long siteY)` | The nets the railway was laid as until FR7 slice 3: every arm grown from the hub (CityRuns.grow(), still the highways'), the gate arm first, then the arm opposite it, the perpendicular pair and new corridors. |
| 139 | 13 | `static long[] plotOnArm(CityRuns.Arm a, int step)` | The main plot of step k along an arm: step 0 its start plot. |
| 153 | 4 | `static void addYardAtStep(CityRuns runs, int arm, int step)` |  |
| 158 | 5 | `static int markAt(long x0, long y0, int w, byte[] marks, long x, long y)` |  |
| 164 | 4 | `static long fillBox(CityRuns runs, BoxGround g, byte[] fixed, byte[] marks)` |  |
| 169 | 289 | `public static void main(String[] args)` |  |
| 460 | 13 | **type** `static class SeaGround extends BoxGround` | BoxGround with the city's own sea in it: the plots from seaX east to the box's east edge are owned salt water, the rest owned grass (FX2: Jerus's Halifax, where the gate arm waits at the sea the city owns beyond it). |
| 463 | 4 | `SeaGround(long x0, long y0, long x1, long y1, long seaX)` _(in MainLineCheck.SeaGround)_ |  |
| 468 | 4 | `public int at(long x, long y, int dir)` _(in MainLineCheck.SeaGround)_ |  |
| 475 | 6 | `static CityRuns seaCity(SeaGround g, long track, long siteX, long siteY)` | A railway of `track` plots laid on g with its gateway east, from the site (siteX, siteY). |
| 483 | 4 | `static int westArm(CityRuns.Net rail)` | The index of the arm opposite the gate arm of a gateway east: corridor 0, home heading west (4); -1 for none. |
| 489 | 12 | `static int[] mainByArm(CityRuns runs, BoxGround g)` | The plots fill() marks M_MAIN over the box, counted by the arm locate() puts them on; the last entry counts those on no arm (the hub's plot among them). |
| 503 | 5 | `static int others(int[] by, int keep)` | What the counts hold apart from entry keep: the plots on every other arm and on none. |
| 515 | 21 | `static int[] walkWay(CityRuns runs, double[][] way)` | A way (wayTo()'s points, each a plot's middle) walked every quarter plot: {positions walked, positions that enter a plot the walk had already left (a way doubling back), positions on a plot the railway does not cover}. |

### 11. the main arm (FX2) (lines 537-661)

| line | len | member | says |
|---:|---:|---|---|
| 546 | 115 | `static void theMainArm(long siteX, long siteY)` | Section 11. |

### 12. two yards stacked on a corridor (FX3) (lines 662-799)

| line | len | member | says |
|---:|---:|---|---|
| 665 | 3 | `static double turn(double[] a, double[] b, double[] c)` | The cross product of (b - a) and (c - a): its sign is the side of a-b that c lies on. |
| 670 | 3 | `static boolean crossesInside(double[] a, double[] b, double[] c, double[] d)` | Whether segments a-b and c-d cross at a point strictly inside both: parallel, collinear and touching segments do not. |
| 675 | 3 | `static double[] pointOf(TrainSchedule.Route r, int k)` | Route r's point k as {x, y}. |
| 692 | 107 | `static void stackedOnACorridor(long siteX, long siteY)` | Section 12. |

### 13. the railway's ways (FR7 slice 3) (lines 800-1201)

| line | len | member | says |
|---:|---:|---|---|
| 803 | 3 | `static long[] box(long x0, long y0, int w, int h)` | A works' box {x0, y0, x1, y1} (inclusive) from its north-west plot and its sides. |
| 807 | 3 | `static long key(long x, long y)` |  |
| 811 | 3 | `static boolean insideBox(long[] b, long x, long y)` |  |
| 816 | 3 | `static boolean beside(long[] b, long x, long y)` | Whether plot (x, y) is outside box b and next to it on a side (a siding's last plot, beside its works). |
| 820 | 3 | `static boolean inYard(long[] yard, long x, long y)` |  |
| 825 | 13 | `static Set<Long> walkedPlots(double[][] way)` | The plots a way (points, each a plot's middle) walks, every quarter plot. |
| 840 | 10 | `static int plotsNearHighways(CityRuns runs, CityRuns.Arm a)` | The plots of every arm of net n with steps laid (the arms' plots, not the hub's) that a highway's plot is next to or on: those within one plot of a highway. |
| 860 | 220 | `static void railWays(long siteX, long siteY, CityMap real, MapCheck.Squares sq)` | Section 13. |
| 1082 | 80 | `static void trainsToTheSidings(long siteX, long siteY, CityRuns runs, CityMap real, MapCheck.Squares sq)` | The trains: a route to every siding, a bulk unit train's alone, on the fixture's net and on a real map. |
| 1164 | 22 | `static void sidingRoutes(CityMap map, String what, boolean maybeNone)` | Section 13's trains on a map: one bulk route to every siding the trains' way reaches, each ending on its siding's last plot; every other route a yard's. |
| 1187 | 14 | `static boolean marksCarry(CityMap m, int bits)` |  |

