# TruckSchedule.java - 592 lines · 38 methods · 12 constants · model

`ham/citybuildersim/TruckSchedule.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> The month's trucks, as a pure function of time: which loads Haulage's
> trucks carry, when each reaches its gate, and where each one is at any
> moment - TrainSchedule's shape (0.8.8, batch FR5;
> docs/studies/spec-freight-and-infrastructure.md 4.7, D21), on whole trips
> since FR9. Model only: ui/MapView draws it on the boats' clock, on
> CityMap.tripsDrawn(), at the near view alone (drawnAt()) and only while the
> player's setting is on (GamePrefs.isTrucksOnMap(), on by default).
> 
> WHY. Haulage (FR2) carries every bulk tonne's city leg (since FR6 less
> the railway's share of the city's own and the quay's bulk) and the highways
> and access roads (FR7) are drawn, but the roads stood empty while the sea
> had its boats and the railway its trains. So A TRUCK IS A SCHEDULE ENTRY,
> NOT AN OBJECT, as a boat and a train are: a load, a route and an arrival
> hashed from (load, month, truck number), and a truck's place is
> route(call, t). Nothing is simulated, stored or saved; panning away and
> back shows the same truck in the same place, and a city that hauls
> nothing has no trucks. Nothing in the model reads it, and what it counts
> from is read-only (Freight.truckedIn() and the others), so the map
> changes no figure of the game. FR5 drew two kinds of truck on two
> schedules - the highways' to each arm's end, a ramp inside the city, where
> they stood and turned back, and the access roads', which came out of the
> street grid from nowhere - so a truck vanished in the middle of the city
> (Jerus, 2026-10-28: "disappear into the void... but still inside the
> city"). FR9 gives each truck ONE WHOLE TRIP, from the world to a works'
> gate and back, and a district draws its own works' share of the loads.
> 
> CALLS. The month billed's city-leg tonnes by load (sectors.Freight.truckedIn(),
> truckedOut() and truckedHome(): what Haulage's trucks were asked, which
> come to Haulage.getTonnes()), at the share Haulage carried of them
> (getCarried() over getTonnes(), a fleet too small leaves tonnes on the
> ground) and a share of each load (`shares`, by Load's order: the works'
> truck tonnes of it over the city's - sharesOf(), perBuilding() and
> truckedBy(), the model's own Move.truck() read - of a district's
> CATCHMENT, sharesByTrips(): a works district rides the trips of the nearest
> district that has any, so a depot district draws its neighbours' trucks and
> a works district with no access road is not left without), over a truck's load,
> Haulage.TONNES_A_LOAD. A fraction of a load is a whole truck in the share
> of months its fraction says (BoatSchedule.callsFor()). Past MOST_A_MONTH
> loads a month (the city's, every load before `shares`, so every district's
> schedule stands for one N) each truck drawn stands for stands() of them,
> every load alike, N a round 1, 2, 5, 10, 20, 50... so the legend reads
> "trucks: one per N loads" and a big city's roads read as busy and not as a
> blur.
> 
> TIME. The boats' month, MONTH_SECONDS. A truck runs its route, from where
> it comes in to its gate, over its route's legSeconds() - the route's length
> over TrainSchedule.TRAIN_PLOTS_A_SECOND, the trains' pace, no less than
> LEG_SECONDS and no more than LEG_MOST - stands DWELL_SECONDS at the gate
> and runs back out; its arrival is spread through the month by its hash,
> between LEG_MOST and the month's end less LEG_MOST and a stand, so its
> whole visit is inside the month whatever its route and a truck never
> appears or vanishes at the month's turn. An import arrives loaded and
> leaves empty, an export arrives empty and leaves loaded, and a tonne
> bought at home is carried as an import is. A truck fades in over the first
> fadeTo plots of its route, the world line's.
> 
> ROUTES (CityMap.tripsDrawn(); the map's, never the model's). A trip is a
> route a truck drives whole, from where its load enters to the gate of the
> work it goes to: a WORLD trip (Route.through(true, ...)) from WORLD_PLOTS
> out along the highway's world line, in along the highways to a ramp, along
> ... (11 more lines in the source)

**Uses:** [CityMap](CityMap.md) (7), [World](World.md) (5), [Haulage](Haulage.md) (4), [BoatSchedule](BoatSchedule.md) (3), [TrainSchedule](TrainSchedule.md) (3), [Freight](Freight.md) (3), [MapFrame](MapFrame.md) (1), [BuildingVisual](BuildingVisual.md) (1), [DistrictPlan](DistrictPlan.md) (1)

**Used by (6):** [CityMap](CityMap.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [TruckCheck](TruckCheck.md), [TruckRouteCheck](TruckRouteCheck.md), [TruckShapes](TruckShapes.md)

## Sections

| line | section |
|---:|---|
| 192 | THE MONTH'S TRUCKS |
| 346 | WHERE A TRUCK IS |
| 430 | A DISTRICT'S PART OF THE CITY'S LOADS (batch FR9) |
| 559 | · reads |

## Enum constants

| line | constant | says |
|---:|---|---|
| 121 | `TruckSchedule.Load.IMPORT` |  |
| 121 | `TruckSchedule.Load.EXPORT` |  |
| 121 | `TruckSchedule.Load.HOME` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 84 | `TruckSchedule.MONTH_SECONDS` | `BoatSchedule.MONTH_SECONDS` | Seconds of trucks a game month: the boats' (spec-freight 4.7, as the trains). |
| 87 | `TruckSchedule.LEG_SECONDS` | `6` | The shortest leg a truck takes to run its route, in or out, in seconds: 6 (spec-freight 4.7, a visual pace, the trains'), for a route of 300 plots or less. |
| 90 | `TruckSchedule.LEG_MOST` | `TrainSchedule.LEG_MOST` | The longest leg, in seconds: 10, the trains' (TrainSchedule.LEG_MOST), for a route of 500 plots or more, so a trip of the whole city's length stays inside the month (a visit is two legs and a stand). |
| 93 | `TruckSchedule.DWELL_SECONDS` | `2` | Seconds a truck stands at its gate, loading or unloading: 2 (spec-freight 4.7). |
| 96 | `TruckSchedule.WORLD_PLOTS` | `TrainSchedule.WORLD_PLOTS` | A route's part along the world line, past the city's ground, in plots: 64, as the trains' (TrainSchedule.WORLD_PLOTS), over which a truck fades in. |
| 99 | `TruckSchedule.TRUCK_PLOTS` | `0.55` | An articulated truck's length in plots: 0.55, 16.5 m of a plot's 30 m (TrainSchedule.CAR_PLOTS, a wagon, is TileRaster.WAGON_LENGTH for scale). |
| 102 | `TruckSchedule.MOST_A_MONTH` | `1000` | The most trucks a month drawn one for one: 1,000, a truck about every 0.06 s of a 60 s month (spec-freight 4.7; the roads take many more marks than the rails). |
| 105 | `TruckSchedule.SALT` | `0x7C4B_E5E7L` | The salt the trucks' hashes start from. |
| 108 | `TruckSchedule.GROUP_BITS` | `7, ROUTE_BITS = 20, ARRIVAL_BITS = 36, ARRIVAL_SHIFT = GROUP_BITS + ROUTE_BITS` | A truck's key: its arrival in ARRIVAL_BITS ticks of a month from bit ARRIVAL_SHIFT up, its route + 1 (0 for none) in ROUTE_BITS, its group in the low GROUP_BITS. |
| 110 | `TruckSchedule.GROUP_MASK` | `(1L<<GROUP_BITS) - 1, ROUTE_MASK =(1L<<ROUTE_BITS) - 1, ARRIVAL_MASK =(1L<<AR...` | The masks that take a key's group, route and arrival back out, once shifted down. |
| 112 | `TruckSchedule.TICKS` | `(double)(1L<<ARRIVAL_BITS)` | A month's arrival ticks, 2^36: an arrival in ticks over this is its share of the month. |
| 512 | `TruckSchedule.CATCHMENT_DISTRICTS` | `DistrictPlan.ACCESS_RAMP_DISTRICTS` | How far, in districts (Chebyshev, by dx and dy), a district's works' tonnes look for a district with trips to ride: 2, how far a work looks for a ramp (DistrictPlan.ACCESS_RAMP_DISTRICTS). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 165 | `private final int month` |  |
| 166 | `private final List<Route> routes` |  |
| 167 | `private final List<Group> groups` |  |
| 168 | `private final double[] loads` |  |
| 169 | `private final int count, stands` |  |
| 172 | `private long[] keys` | Built on the first ask: the trucks' keys, sorted by arrival; and each route's box, {x0, y0, x1, y1} in plots. |
| 173 | `private double[][] boxesOf` |  |
| 174 | `private int touched` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 81 | 512 | **type** `public final class TruckSchedule` | The month's trucks, as a pure function of time: which loads Haulage's trucks carry, when each reaches its gate, and where each one is at any moment - TrainSchedule's shape (0.8.8, batch FR5; docs/studies/spec-freight-... |
| 121 | 1 | **type** `public enum Load` | A load's way through the city: what it carries each way and its colour's sense (spec-freight 4.7: imports blue, exports orange, empty grey). |
| 132 | 23 | **type** `public record Route(boolean world, boolean home, double[] xs, double[] ys, double[] along, double fadeTo)` | A truck's way: points in plots, from where it comes in (the highway's world line's far end, another work's gate, or an access road's end on the street grid) to its gate, with each point's distance along it; the first ... |
| 134 | 3 | `public static Route through(boolean world, double[] xs, double[] ys, double fadeTo)` _(in TruckSchedule.Route)_ | A route through points: their distances along it worked out. |
| 139 | 3 | `public static Route home(double[] xs, double[] ys, double fadeTo)` _(in TruckSchedule.Route)_ | ...a home trip through points (FR9): from a work's gate to another's, taking the tonnes bought at home alone. |
| 143 | 5 | `private static Route make(boolean world, boolean home, double[] xs, double[] ys, double fadeTo)` _(in TruckSchedule.Route)_ |  |
| 150 | 1 | `public double length()` _(in TruckSchedule.Route)_ | Its length, in plots. |
| 153 | 1 | `public double legSeconds()` _(in TruckSchedule.Route)_ | Seconds a truck takes to run it, in or out: its length over TrainSchedule.TRAIN_PLOTS_A_SECOND, no less than LEG_SECONDS and no more than LEG_MOST (the trains' pace). |
| 157 | 1 | **type** `public record Call(Load load, int route, double arrives)` | One truck: its load, its route among the schedule's, and when it reaches its gate, in month fractions. |
| 160 | 1 | **type** `public record Truck(Call call, double x, double y, double heading, boolean loaded, double alpha)` | A truck at a moment: its call, its middle in plots, its heading (radians, east 0, south a quarter turn), whether it is loaded, and how much of it shows (0 to 1). |
| 163 | 1 | **type** `private record Group(Load load, long seed, int calls)` | One load of the month: its seed and its trucks. |
| 176 | 10 | `private TruckSchedule(int month, List<Route> routes, List<Group> groups, double[] loads, int stands)` |  |
| 188 | 3 | `public static boolean drawnAt(int level, boolean settingOn)` | Whether trucks are drawn at a map level with the player's setting: at the near view (MapFrame.L0) alone, and only with the setting on. |

### THE MONTH'S TRUCKS (lines 192-345)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 3 | `public static TruckSchedule of(int month, Freight freight, List<Route> routes, double share, long salt)` | The month's schedule from Haulage's month billed: Freight's city-leg truck tonnes by load (Freight.truckedIn(), truckedOut(), truckedHome()), at the share Haulage carried (Haulage.getCarried() over getTonnes()), times... |
| 217 | 12 | `public static TruckSchedule of(int month, Freight freight, List<Route> routes, double[] shares, long salt)` | ...with a share for each load (by Load's order: a district's works' truck tonnes of it over the city's, sharesOf()), since a district that holds a sector's buildings holds its imports and its exports in other measure ... |
| 236 | 3 | `public static TruckSchedule of(int month, double[] loadsIn, List<Route> routes, double share, long salt)` | ...from the month's loads counted already, by Load's order, at the share Haulage carried and before `share` (a fraction of a load is a whole truck in the share of months it says): the headless renders' and the harness... |
| 241 | 22 | `public static TruckSchedule of(int month, double[] loadsIn, List<Route> routes, double[] shares, long salt)` | ...with a share for each load, by Load's order: the way in the others come to. |
| 264 | 1 | `private static double tonnesOf(double t)` |  |
| 267 | 5 | `private static double[] sameShare(double share)` | One share for every load. |
| 274 | 10 | `public static int standsFor(double loads)` | Loads each truck drawn stands for when a month has `loads`: 1 up to MOST_A_MONTH, then the smallest of 1, 2, 5, 10, 20, 50, 100... |
| 286 | 1 | `private static boolean takes(Route r, Load l)` | Whether a load goes on a route: an import or an export on any but a home trip, a tonne bought at home on an access road or a home trip alone. |
| 289 | 5 | `private static boolean takenBy(List<Route> routes, Load l)` | Whether any of the routes a schedule keeps takes a load. |
| 296 | 3 | `static long seed(int month, Load l, long salt)` | The seed of one load in one month; `salt` is mixed into it, so it is mixed into every hash drawn from it. |
| 301 | 5 | `static long arrivalTicks(long seed, int k)` | When a group's k-th truck reaches its gate, in ticks of a month: hashed through the month between the longest leg (LEG_MOST) and the month's end less that and a stand, so any truck's whole visit is inside it whatever ... |
| 308 | 6 | `private int[] routesFor(Load l)` | The routes a load goes on (their indices): every one that takes it. |
| 316 | 21 | `private void build()` | The trucks' keys, sorted by arrival, and the routes' boxes; built on the first ask. |
| 339 | 6 | `private Call call(long key)` | A call out of its key. |

### WHERE A TRUCK IS (lines 346-429)

| line | len | member | says |
|---:|---:|---|---|
| 356 | 30 | `public static Truck position(Call call, Route route, double t)` | Where a call's truck is at t (month fractions), or null when it is not on the road: running in over its leg, standing at its gate (loaded as it came for the first half of its stand, as it will leave for the second), o... |
| 393 | 19 | `public List<Truck> frame(long x0, long y0, long x1, long y1, double t)` | The trucks on the roads at t whose middle is in the rectangle [x0, x1) x [y0, y1) in plots, widened by a truck's length, in the order of their arrival: the month's arrivals searched for those whose visit spans t, and ... |
| 414 | 5 | `public boolean crosses(long x0, long y0, long x1, long y1)` | Whether any route meets the rectangle [x0, x1) x [y0, y1) in plots: the view keeps its frame going while one does. |
| 421 | 8 | `static double[] box(Route r)` | A route's box, {x0, y0, x1, y1} in plots, inclusive of its points. |

### A DISTRICT'S PART OF THE CITY'S LOADS (batch FR9) (lines 430-558)

| line | len | member | says |
|---:|---:|---|---|
| 445 | 20 | `public static double[][] perBuilding(java.util.function.Function<String, double[]> truckedBy, BuildingVisual.Type[] types, long...` | What ONE standing building of each type carries in the month, in truck tonnes by load (Load's order: imports, exports, tonnes bought at home), by the type's index in `types`: its sector's truck tonnes by load (`trucke... |
| 472 | 14 | `public static double[] truckedBy(Freight freight, String sector)` | The model's own figure for a sector, read: the truck tonnes (Move.truck()) of its moves in the month billed (Haulage.movesOf()), by load - landed imports, shipped exports, bought at home. |
| 495 | 15 | `public static double[] sharesOf(double[][] perBuilding, CityMap.District d, double[] cityTonnes)` | A district's part of the city's truck tonnes, by load: the sum over the types of its count times perBuilding() (what one building of the type carries) over `cityTonnes`, the city's by load (Freight.truckedIn(), trucke... |
| 525 | 33 | `public static java.util.Map<Long, double[]> sharesByTrips(List<CityMap.DistrictTrips> trips, java.util.function.BiFunction<Inte...` | Each district with trips and the shares of the city's tonnes, by load, its trips carry - its CATCHMENT (FR9, slice 2): the works' tonnes (sharesOf()) of every district that rides its trips, itself among them. |

### reads (lines 559-592)

| line | len | member | says |
|---:|---:|---|---|
| 561 | 1 | `public int month()` |  |
| 562 | 1 | `public List<Route> routes()` |  |
| 565 | 6 | `public List<Call> calls()` | Every truck of the month, by arrival. |
| 573 | 1 | `public int callCount()` | The trucks drawn this month: counted when the schedule is made, nothing built. |
| 576 | 1 | `public int stands()` | Loads each drawn truck stands for: N in the legend's "one per N loads", 1 for a quiet month. |
| 579 | 5 | `public int callsOf(Load l)` | The trucks drawn of one load. |
| 586 | 1 | `public double loads(Load l)` | The month's loads of one kind at the share given, before stands() and whether a route takes them (a fraction is a load not yet whole). |
| 589 | 1 | `public int touched()` | The trucks the last frame() looked at. |
| 591 | 1 | `public String toString()` |  |

