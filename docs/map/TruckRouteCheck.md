# TruckRouteCheck.java - 1,355 lines · 50 methods · 3 constants · harnesses

`ham/citybuildersim/TruckRouteCheck.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> Proves the trucks' routes (0.8.8, batch FR5, slice B;
> docs/studies/spec-freight-and-infrastructure.md 4.7, D21): the ways the
> map lets a truck drive, CityMap.truckRoutes() along the highways from their
> line to the world and CityMap.accessRoutesIn() along each drawn district's
> access roads - and, since FR9, the TRIPS they are joined into
> (CityMap.tripsDrawn()): a truck's whole way from the world line to a
> works' gate, and from one works' gate to another's. Not part of the game.
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
>   7. a trip is whole (FR9), on the access city and on a trips city built to
>      cause a road with a street leg as well as roads that end at a ramp:
>      the city has trips, world and home, and a road of each kind; every
>      world trip starts WORLD_PLOTS out along the highways' line, passes
>      the line's point, and from the arm's end on every quarter plot lies on
>      a highway plot or a street the plans draw, the highway part ends at a
>      ramp, the rest keeps to streets, and its last point is a gate with its
>      last leg on the access road; every home trip runs gate to gate on
>      streets alone, never on a highway plot that carries none, never back
>      on itself, from the gate of the nearest other road that comes to the
>      same ramp; no sample of any trip lies where no road is drawn;
>   8. only what is drawn: asking for trips plans nothing (the plans kept,
>      the links and changes() as they were); before a district is drawn it
>      has no trips and after it is drawn it has; a city whose highways have
>      no world line has no whole trip, each drawn road FR5's access route
>      alone; the list is kept while nothing changes (the
>      same object) and made again once a plan or the runs change;
> ... (28 more lines in the source)

**Uses:** [CityMap](CityMap.md) (174), [TruckSchedule](TruckSchedule.md) (145), [CityRuns](CityRuns.md) (18), [MapCheck](MapCheck.md) (16), [BuildingVisual](BuildingVisual.md) (13), [GatewayCheck](GatewayCheck.md) (13), [TilePainter](TilePainter.md) (10), [DistrictPlan](DistrictPlan.md) (9), [TrainSchedule](TrainSchedule.md) (7), [World](World.md) (5), [BuildingType](BuildingType.md) (3), [Good](Good.md) (3), [TruckCheck](TruckCheck.md) (3), [JunctionCheck](JunctionCheck.md) (2), [HaulageCheck](HaulageCheck.md) (2), [Sector](Sector.md) (2), [Founding](Founding.md) (1), [Mining](Mining.md) (1), [CityShore](CityShore.md) (1), [Game](Game.md) (1), [GameFiles](GameFiles.md) (1), [Freight](Freight.md) (1), [Haulage](Haulage.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 107 | `TruckRouteCheck.STEP` | `CityMap.WORLD_STEP` | How finely a leg is read, in plots: a quarter, as the world lines are (CityMap.WORLD_STEP). |
| 110 | `TruckRouteCheck.EPS` | `1e-9` | The tolerance of a position in plots, well below anything the map measures. |
| 117 | `TruckRouteCheck.TRIPS_MORE_DEPOTS` | `12, TRIPS_HIGHWAYS = 60` | Section 7's trips city: MapCheck's access city with 12 Truck Depots more and 60 Elevated Highways in place of its 200. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 104 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 102 | 1254 | **type** `public class TruckRouteCheck` | Proves the trucks' routes (0.8.8, batch FR5, slice B; docs/studies/spec-freight-and-infrastructure.md 4.7, D21): the ways the map lets a truck drive, CityMap.truckRoutes() along the highways from their line to the wor... |
| 119 | 4 | `static void check(String label, boolean ok)` |  |
| 125 | 4 | `static boolean same(TruckSchedule.Route a, TruckSchedule.Route b)` | Whether two routes are the same, point for point (a record holds arrays by reference, so equals() would not say). |
| 130 | 5 | `static boolean same(List<TruckSchedule.Route> a, List<TruckSchedule.Route> b)` |  |
| 137 | 4 | `static boolean hasHighwayLine(CityMap m)` | Whether a map has a world line for the highways. |
| 142 | 4 | `static CityMap.WorldLine highwayLine(CityMap m)` |  |
| 148 | 13 | `static int offHighway(CityRuns runs, TruckSchedule.Route r, int from)` | The fewest plots a world route's points are read at, every STEP along each leg from `from` (the leg that starts at that point index): how many of them lie on no plot of the highways. |
| 165 | 3 | `static BuildingVisual.Type[] types()` | The game's building templates, as the map draws them. |
| 173 | 3 | `static CityMap accessCity(BuildingVisual.Type[] types)` | MapCheck's access city on its own (his city x 1 where its square holds iron, with Rail Terminals, a Freight Line, 200 more highways and 3 Truck Depots added: MapCheck.accessCity() without the copies it takes a Squares... |
| 178 | 3 | `static CityMap accessCity(BuildingVisual.Type[] types, int moreDepots)` | ...with `moreDepots` Truck Depots besides MapCheck's (section 7's trips city: a depot in a frame with no ramp ends its road on an arterial, and its trucks drive a street leg to a ramp). |
| 183 | 36 | `static CityMap accessCity(BuildingVisual.Type[] types, int moreDepots, int highways)` | ...and `highways` Elevated Highways in place of MapCheck's. |
| 221 | 3 | `static long[] origin(CityMap m, CityMap.District d)` | The frame origin of district d, in world plots. |
| 227 | 21 | `static void noLine()` | ------------------------------------------------------------------ 1 |
| 251 | 57 | `static void highwayRoutes()` | ------------------------------------------------------------------ 2 |
| 312 | 12 | `static long[] anchorOf(CityRuns runs, long[] y)` | The rail plot in a yard's box nearest its middle: CityRuns.anchorInYard(), as the harness reads it. |
| 325 | 3 | `static boolean sameWay(double[][] a, double[][] b)` |  |
| 329 | 31 | `static void railwayUnchanged(CityMap m)` |  |
| 367 | 13 | `static int offAccess(TruckSchedule.Route r, CityMap.Drawn dr, long ox, long oy)` | How many plots, reading every STEP along each leg of route r, are not a street the plan draws as an access road (or a boulevard's, which keeps its role) in the Drawn dr whose frame starts at (ox, oy): the truth the pa... |
| 382 | 5 | `static boolean notAccess(CityMap.Drawn dr, long px, long py)` | Whether frame plot (px, py) of the Drawn is not a street drawn as an access road's (or a boulevard's, which keeps its role). |
| 389 | 13 | `static List<int[]> worksOf(CityMap m, CityMap.District d, CityMap.Drawn dr)` | The works of district d that stand where a road comes to: the yards and sea terminals the map gave its plan, and the Truck Depots among its buildings as the Drawn packs them, each {x, y, w, h} in the frame's plots. |
| 403 | 89 | `static void accessRoutes(CityMap m)` |  |
| 495 | 20 | `static void kept()` | ------------------------------------------------------------------ 5 |
| 518 | 23 | `static void count(CityMap access)` | ------------------------------------------------------------------ 6 |
| 545 | 1 | **type** `interface PlotTest` | A test of one plot. |
| 545 | 1 | `boolean at(long px, long py)` _(in TruckRouteCheck.PlotTest)_ |  |
| 548 | 13 | `static int samplesWhere(TruckSchedule.Route r, int from, PlotTest test)` | How many plots, reading every STEP along each leg of r from the leg that starts at point `from`, the test says yes to. |
| 563 | 3 | `static boolean isStreet(CityMap m, long px, long py)` | Whether the plans draw a street (a code of any kind but none) on world plot (px, py): the truth the painter reads. |
| 568 | 5 | `static boolean isRamp(CityMap m, double x, double y)` | Whether the runs mark a ramp on the plot under (x, y). |
| 575 | 1 | `static long pk(double x, double y)` | A plot's middle packed with its plot, for a map key. |
| 578 | 8 | `static List<int[]> worldWorks(CityMap m)` | Every work of every district of the map, each {x, y, w, h} in world plots. |
| 588 | 5 | `static boolean atGate(List<int[]> works, double x, double y)` | Whether (x, y) is a plot's middle beside the box of one of the works. |
| 595 | 13 | `static int rampIndex(CityMap m, TruckSchedule.Route r)` | The index of the point a world trip leaves the highways at: the last ramp among the points the trip keeps on highway plots from the arm's end (point 2) on; -1 for none. |
| 610 | 7 | `static boolean turnsBack(TruckSchedule.Route r)` | Whether a trip turns back on itself at a point: the leg out of it runs the way the leg into it came from. |
| 619 | 5 | `static boolean sameTrips(List<CityMap.DistrictTrips> a, List<CityMap.DistrictTrips> b)` | Whether every trip of two lists is the same, district by district and point by point. |
| 626 | 1 | **type** `record RoadAt(long endX, long endY, long ramp)` | One road as the trips are checked against it: where it ends and the plot of the ramp its trip left the highways at. |
| 629 | 5 | `static int plotCost(CityMap m, long px, long py)` | What it costs a truck to enter world plot (px, py): 1 on an arterial, boulevard or access street, TRIP_STREET_COST on any other street, -1 where no street is drawn. |
| 640 | 35 | `static int cheapestLeg(CityMap m, CityMap.WorldLine line, long[] o, long ex, long ey)` | The cheapest way to world plot (ex, ey) over the streets of the box about a district (its frame, with origin o, grown by CityMap.TRIP_REACH), from any ramp in the box that carries a street and has a way along the high... |
| 681 | 138 | `static void tripsWhole(String name, CityMap m, boolean mixed, String coldNote)` | Section 7 on one drawn city: every trip is whole. |
| 821 | 17 | `static void warmCost(String name, CityMap m)` | Prints what making a city's trips again costs, warm: five remakes of everything (the legs and the list forgotten), the best and the median of a whole tripsDrawn() and of one district's search. |
| 842 | 4 | `static boolean hasTrips(List<CityMap.DistrictTrips> trips, CityMap.District d)` | Whether district d has trips in the list. |
| 848 | 34 | `static String tripsOnlyDrawn(CityMap m)` | Section 8's first half, on a city nothing of which is drawn: trips plan nothing, and a district drawn later gets its trips then. |
| 884 | 55 | `static void tripsKept(CityMap m, BuildingVisual.Type[] types)` | Section 8's second half, on a city drawn whole: the list is kept while nothing changes and made again once a plan or the runs change; no world line, FR5's access routes alone. |
| 942 | 3 | `static boolean near(double a, double b)` | ------------------------------------------------------------------ 9 |
| 946 | 132 | `static void shares(BuildingVisual.Type[] types, CityMap trips)` |  |
| 1082 | 5 | `static CityMap.District hand(BuildingVisual.Type[] types, int dx, int dy, int...typeAndCount)` | A district by hand at (dx, dy) holding `count` buildings of each type given as type, count pairs. |
| 1089 | 3 | `static CityMap.DistrictTrips tripsOf(CityMap.District d)` | A district's trips, by hand: the trips themselves are nothing to the catchment. |
| 1094 | 5 | `static java.util.function.BiFunction<Integer, Integer, CityMap.District> gridOf(java.util.Collection<CityMap.District> ds)` | A lookup of districts by (dx, dy), as CityMap.district() gives. |
| 1100 | 3 | `static boolean same3(double[] x, double[] y)` |  |
| 1105 | 13 | `static CityMap.DistrictTrips winnerOf(List<CityMap.DistrictTrips> trips, CityMap.District t)` | The district with trips that district t rides, by the rule written out again: the nearest within CATCHMENT_DISTRICTS, Chebyshev, then squared Euclidean, then key; null for none. |
| 1120 | 10 | `static double[] ridersOf(List<CityMap.DistrictTrips> trips, java.util.Collection<CityMap.District> all, CityMap.DistrictTrips e...` | The shares of the districts that ride `e`'s trips, summed. |
| 1132 | 136 | `static void catchment(BuildingVisual.Type[] types, CityMap trips, double[][] per, double[] city, int a, int b)` | Section 9's catchment: a district's trips carry the tonnes of the works about it, not its own alone. |
| 1271 | 61 | `static void pace()` | ------------------------------------------------------------------ 10 |
| 1333 | 22 | `public static void main(String[] args)` |  |

