# TruckRouteCheck.java - 489 lines · 21 methods · 2 constants · harnesses

`ham/citybuildersim/TruckRouteCheck.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

> Proves the trucks' routes (0.8.8, batch FR5, slice B;
> docs/studies/spec-freight-and-infrastructure.md 4.7, D21): the ways the
> map lets a truck drive, CityMap.truckRoutes() along the highways from their
> line to the world and CityMap.accessRoutesIn() along each drawn district's
> access roads. Not part of the game.
> 
> WHY THIS EXISTS. Haulage's trucks (FR2) carry every bulk tonne's city leg,
> and the highways and access roads (FR7) are drawn, but nothing said where a
> truck's mark runs. The routes are the map's alone - nothing in the model
> reads them - and are found as the trains' are (FR1): the highways' by the
> same walk CityRuns.wayToPlot() makes on the railway, which was factored into
> CityRuns.wayOn() for it and must give the railway what it gave; the access
> roads' when a plan is packed (CityMap.Drawn.access), and never by drawing a
> plan.
> 
> What it has to prove, a section each, every one a fixture that causes the
> condition (GatewayCheck's and MapCheck's cities; never new Game()):
> 
>   1. with no highway world line there is no world route: highways that
>      reach no street (not drawn), highways that reach one but find no way
>      out, and a railway alone;
>   2. in a city with a highway world line each world route starts
>      TruckSchedule.WORLD_PLOTS out along the line's way, passes the line's point,
>      runs on highway plots only (the points and the middles of every leg
>      from the arm's end), ends at a ramp, and is faded in over its world
>      part; no two end at one ramp, none is shorter than TRUCK_ROUTE_LEAST
>      along the highways, the number is within TRUCK_ROUTES_MOST;
>   3. the railway's way is unchanged: wayToPlot() is wayOn() of the rail net
>      from the main arm for every yard's anchor and every siding's end, and
>      wayTo() of a yard is wayToPlot() of its anchor; asking for trucks
>      leaves the trains' routes as they were;
>   4. the access roads: no plan is drawn by asking; once a district's plan
>      is drawn each road the plan laid has one non-world route from the
>      road's end to its gate, every plot along it an access plot, a straight
>      run kept to its two ends, faded in over TRUCK_ACCESS_FADE; a box
>      meets only the districts whose frame it meets, under their own keys;
>   5. the routes are kept against the map's changes (the same list asked
>      again) and made again once the runs move them;
>   6. accessRoadCount() is the works that need an access road: the yards
>      the runs draw, the shore's sea terminals, the Truck Depots - at
>      least 1.
> 
> They run in the order 1, 2, 5, 3, 4, 6: section 5, like 1 and 2, plays on
> GatewayCheck's cities, and 3, 4 and 6 share one access city built after
> them.

**Uses:** [CityMap](CityMap.md) (60), [TruckSchedule](TruckSchedule.md) (16), [MapCheck](MapCheck.md) (14), [CityRuns](CityRuns.md) (11), [GatewayCheck](GatewayCheck.md) (11), [BuildingVisual](BuildingVisual.md) (7), [World](World.md) (5), [DistrictPlan](DistrictPlan.md) (5), [TilePainter](TilePainter.md) (4), [TrainSchedule](TrainSchedule.md) (2), [BuildingType](BuildingType.md) (2), [Founding](Founding.md) (1), [Mining](Mining.md) (1), [JunctionCheck](JunctionCheck.md) (1), [CityShore](CityShore.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `TruckRouteCheck.STEP` | `CityMap.WORLD_STEP` | How finely a leg is read, in plots: a quarter, as the world lines are (CityMap.WORLD_STEP). |
| 62 | `TruckRouteCheck.EPS` | `1e-9` | The tolerance of a position in plots, well below anything the map measures. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 54 | 436 | **type** `public class TruckRouteCheck` | Proves the trucks' routes (0.8.8, batch FR5, slice B; docs/studies/spec-freight-and-infrastructure.md 4.7, D21): the ways the map lets a truck drive, CityMap.truckRoutes() along the highways from their line to the wor... |
| 64 | 4 | `static void check(String label, boolean ok)` |  |
| 70 | 4 | `static boolean same(TruckSchedule.Route a, TruckSchedule.Route b)` | Whether two routes are the same, point for point (a record holds arrays by reference, so equals() would not say). |
| 75 | 5 | `static boolean same(List<TruckSchedule.Route> a, List<TruckSchedule.Route> b)` |  |
| 82 | 4 | `static boolean hasHighwayLine(CityMap m)` | Whether a map has a world line for the highways. |
| 87 | 4 | `static CityMap.WorldLine highwayLine(CityMap m)` |  |
| 93 | 13 | `static int offHighway(CityRuns runs, TruckSchedule.Route r, int from)` | The fewest plots a world route's points are read at, every STEP along each leg from `from` (the leg that starts at that point index): how many of them lie on no plot of the highways. |
| 110 | 3 | `static BuildingVisual.Type[] types()` | The game's building templates, as the map draws them. |
| 118 | 36 | `static CityMap accessCity(BuildingVisual.Type[] types)` | MapCheck's access city on its own (his city x 1 where its square holds iron, with Rail Terminals, a Freight Line, 200 more highways and 3 Truck Depots added: MapCheck.accessCity() without the copies it takes a Squares... |
| 156 | 3 | `static long[] origin(CityMap m, CityMap.District d)` | The frame origin of district d, in world plots. |
| 162 | 21 | `static void noLine()` | ------------------------------------------------------------------ 1 |
| 186 | 57 | `static void highwayRoutes()` | ------------------------------------------------------------------ 2 |
| 247 | 12 | `static long[] anchorOf(CityRuns runs, long[] y)` | The rail plot in a yard's box nearest its middle: CityRuns.anchorInYard(), as the harness reads it. |
| 260 | 3 | `static boolean sameWay(double[][] a, double[][] b)` |  |
| 264 | 31 | `static void railwayUnchanged(CityMap m)` |  |
| 302 | 13 | `static int offAccess(TruckSchedule.Route r, CityMap.Drawn dr, long ox, long oy)` | How many plots, reading every STEP along each leg of route r, are not a street the plan draws as an access road (or a boulevard's, which keeps its role) in the Drawn dr whose frame starts at (ox, oy): the truth the pa... |
| 317 | 5 | `static boolean notAccess(CityMap.Drawn dr, long px, long py)` | Whether frame plot (px, py) of the Drawn is not a street drawn as an access road's (or a boulevard's, which keeps its role). |
| 324 | 13 | `static List<int[]> worksOf(CityMap m, CityMap.District d, CityMap.Drawn dr)` | The works of district d that stand where a road comes to: the yards and sea terminals the map gave its plan, and the Truck Depots among its buildings as the Drawn packs them, each {x, y, w, h} in the frame's plots. |
| 338 | 89 | `static void accessRoutes(CityMap m)` |  |
| 430 | 20 | `static void kept()` | ------------------------------------------------------------------ 5 |
| 453 | 23 | `static void count(CityMap access)` | ------------------------------------------------------------------ 6 |
| 477 | 12 | `public static void main(String[] args)` |  |

