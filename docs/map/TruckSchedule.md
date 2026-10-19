# TruckSchedule.java - 401 lines · 28 methods · 10 constants · model

`ham/citybuildersim/TruckSchedule.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> The month's trucks, as a pure function of time: which loads Haulage's
> trucks carry, when each reaches its gate, and where each one is at any
> moment - TrainSchedule's shape (0.8.8, batch FR5;
> docs/studies/spec-freight-and-infrastructure.md 4.7, D21). Model only:
> ui/MapView draws it on the boats' clock, on CityMap.truckRoutes(), at the
> near view alone (drawnAt()) and only while the player's setting is on
> (GamePrefs.isTrucksOnMap(), on by default).
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
> changes no figure of the game.
> 
> CALLS. The month billed's city-leg tonnes by load (sectors.Freight.truckedIn(),
> truckedOut() and truckedHome(): what Haulage's trucks were asked, which
> come to Haulage.getTonnes()), at the share Haulage carried of them
> (getCarried() over getTonnes(), a fleet too small leaves tonnes on the
> ground) and `share` (1 for the highways' routes, an access road's part of
> the city's for a district's), over a truck's load, Haulage.TONNES_A_LOAD.
> A fraction of a load is a whole truck in the share of months its fraction
> says (BoatSchedule.callsFor()). Past MOST_A_MONTH loads a month (the
> city's, every load before `share`, so the highways' trucks and each
> district's stand for one N) each truck
> drawn stands for stands() of them, every load alike, N a round 1, 2, 5, 10,
> 20, 50... so the legend reads "trucks: one per N loads" and a big city's
> roads read as busy and not as a blur.
> 
> TIME. The boats' month, MONTH_SECONDS. A truck runs its route, from where
> it comes in to its gate, over LEG_SECONDS, stands DWELL_SECONDS at the gate
> and runs back out; its arrival is spread through the month by its hash, its
> whole visit inside it, so a truck never appears or vanishes at the month's
> turn. An import arrives loaded and leaves empty, an export arrives empty
> and leaves loaded, and a tonne bought at home is carried as an import is.
> A truck fades in over the first fadeTo plots of its route, the world line's.
> 
> ROUTES (CityMap.truckRoutes(); the map's, never the model's). From
> WORLD_PLOTS out along the highway's world line, in along the highways to
> the ramps; and each access road, from its end on the street grid to its
> work's gate. A world route takes imports and exports alone (what crosses
> the city's edge comes in by it and leaves by it); an access road takes
> every load, a tonne bought at home included, which crosses no edge. A load
> goes to one of the routes that take it, by a hash. A load no route takes
> has no truck, and a city with no routes has none at all.

**Uses:** [World](World.md) (5), [BoatSchedule](BoatSchedule.md) (3), [Haulage](Haulage.md) (2), [TrainSchedule](TrainSchedule.md) (1), [MapFrame](MapFrame.md) (1), [Freight](Freight.md) (1)

**Used by (5):** [CityMap](CityMap.md), [MapView](MapView.md), [TruckCheck](TruckCheck.md), [TruckRouteCheck](TruckRouteCheck.md), [TruckShapes](TruckShapes.md)

## Sections

| line | section |
|---:|---|
| 154 | THE MONTH'S TRUCKS |
| 284 | WHERE A TRUCK IS |
| 368 | · reads |

## Enum constants

| line | constant | says |
|---:|---|---|
| 97 | `TruckSchedule.Load.IMPORT` |  |
| 97 | `TruckSchedule.Load.EXPORT` |  |
| 97 | `TruckSchedule.Load.HOME` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 63 | `TruckSchedule.MONTH_SECONDS` | `BoatSchedule.MONTH_SECONDS` | Seconds of trucks a game month: the boats' (spec-freight 4.7, as the trains). |
| 66 | `TruckSchedule.LEG_SECONDS` | `6` | Seconds a truck takes to run its route, in or out: 6 (spec-freight 4.7, a visual pace, the trains'). |
| 69 | `TruckSchedule.DWELL_SECONDS` | `2` | Seconds a truck stands at its gate, loading or unloading: 2 (spec-freight 4.7). |
| 72 | `TruckSchedule.WORLD_PLOTS` | `TrainSchedule.WORLD_PLOTS` | A route's part along the world line, past the city's ground, in plots: 64, as the trains' (TrainSchedule.WORLD_PLOTS), over which a truck fades in. |
| 75 | `TruckSchedule.TRUCK_PLOTS` | `0.55` | An articulated truck's length in plots: 0.55, 16.5 m of a plot's 30 m (TrainSchedule.CAR_PLOTS, a wagon, is TileRaster.WAGON_LENGTH for scale). |
| 78 | `TruckSchedule.MOST_A_MONTH` | `1000` | The most trucks a month drawn one for one: 1,000, a truck about every 0.06 s of a 60 s month (spec-freight 4.7; the roads take many more marks than the rails). |
| 81 | `TruckSchedule.SALT` | `0x7C4B_E5E7L` | The salt the trucks' hashes start from. |
| 84 | `TruckSchedule.GROUP_BITS` | `7, ROUTE_BITS = 20, ARRIVAL_BITS = 36, ARRIVAL_SHIFT = GROUP_BITS + ROUTE_BITS` | A truck's key: its arrival in ARRIVAL_BITS ticks of a month from bit ARRIVAL_SHIFT up, its route + 1 (0 for none) in ROUTE_BITS, its group in the low GROUP_BITS. |
| 86 | `TruckSchedule.GROUP_MASK` | `(1L<<GROUP_BITS) - 1, ROUTE_MASK =(1L<<ROUTE_BITS) - 1, ARRIVAL_MASK =(1L<<AR...` | The masks that take a key's group, route and arrival back out, once shifted down. |
| 88 | `TruckSchedule.TICKS` | `(double)(1L<<ARRIVAL_BITS)` | A month's arrival ticks, 2^36: an arrival in ticks over this is its share of the month. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 127 | `private final int month` |  |
| 128 | `private final List<Route> routes` |  |
| 129 | `private final List<Group> groups` |  |
| 130 | `private final double[] loads` |  |
| 131 | `private final int count, stands` |  |
| 134 | `private long[] keys` | Built on the first ask: the trucks' keys, sorted by arrival; and each route's box, {x0, y0, x1, y1} in plots. |
| 135 | `private double[][] boxesOf` |  |
| 136 | `private int touched` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 342 | **type** `public final class TruckSchedule` | The month's trucks, as a pure function of time: which loads Haulage's trucks carry, when each reaches its gate, and where each one is at any moment - TrainSchedule's shape (0.8.8, batch FR5; docs/studies/spec-freight-... |
| 97 | 1 | **type** `public enum Load` | A load's way through the city: what it carries each way and its colour's sense (spec-freight 4.7: imports blue, exports orange, empty grey). |
| 106 | 11 | **type** `public record Route(boolean world, double[] xs, double[] ys, double[] along, double fadeTo)` | A truck's way: points in plots, from where it comes in (the highway's world line's far end, or an access road's end on the street grid) to its gate, with each point's distance along it; the first fadeTo plots are fade... |
| 108 | 5 | `public static Route through(boolean world, double[] xs, double[] ys, double fadeTo)` _(in TruckSchedule.Route)_ | A route through points: their distances along it worked out. |
| 115 | 1 | `public double length()` _(in TruckSchedule.Route)_ | Its length, in plots. |
| 119 | 1 | **type** `public record Call(Load load, int route, double arrives)` | One truck: its load, its route among the schedule's, and when it reaches its gate, in month fractions. |
| 122 | 1 | **type** `public record Truck(Call call, double x, double y, double heading, boolean loaded, double alpha)` | A truck at a moment: its call, its middle in plots, its heading (radians, east 0, south a quarter turn), whether it is loaded, and how much of it shows (0 to 1). |
| 125 | 1 | **type** `private record Group(Load load, long seed, int calls)` | One load of the month: its seed and its trucks. |
| 138 | 10 | `private TruckSchedule(int month, List<Route> routes, List<Group> groups, double[] loads, int stands)` |  |
| 150 | 3 | `public static boolean drawnAt(int level, boolean settingOn)` | Whether trucks are drawn at a map level with the player's setting: at the near view (MapFrame.L0) alone, and only with the setting on. |

### THE MONTH'S TRUCKS (lines 154-283)

| line | len | member | says |
|---:|---:|---|---|
| 167 | 12 | `public static TruckSchedule of(int month, Freight freight, List<Route> routes, double share, long salt)` | The month's schedule from Haulage's month billed: Freight's city-leg truck tonnes by load (Freight.truckedIn(), truckedOut(), truckedHome()), at the share Haulage carried (Haulage.getCarried() over getTonnes()), times... |
| 186 | 22 | `public static TruckSchedule of(int month, double[] loadsIn, List<Route> routes, double share, long salt)` | ...from the month's loads counted already, by Load's order, at the share Haulage carried and before `share` (a fraction of a load is a whole truck in the share of months it says): the headless renders' and the harness... |
| 209 | 1 | `private static double tonnesOf(double t)` |  |
| 212 | 10 | `public static int standsFor(double loads)` | Loads each truck drawn stands for when a month has `loads`: 1 up to MOST_A_MONTH, then the smallest of 1, 2, 5, 10, 20, 50, 100... |
| 224 | 1 | `private static boolean takes(Route r, Load l)` | Whether a load goes on a route: an import or an export on any, a tonne bought at home on an access road's alone. |
| 227 | 5 | `private static boolean takenBy(List<Route> routes, Load l)` | Whether any of the routes a schedule keeps takes a load. |
| 234 | 3 | `static long seed(int month, Load l, long salt)` | The seed of one load in one month; `salt` is mixed into it, so it is mixed into every hash drawn from it. |
| 239 | 5 | `static long arrivalTicks(long seed, int k)` | When a group's k-th truck reaches its gate, in ticks of a month: hashed through the month, its whole visit inside it. |
| 246 | 6 | `private int[] routesFor(Load l)` | The routes a load goes on (their indices): every one that takes it. |
| 254 | 21 | `private void build()` | The trucks' keys, sorted by arrival, and the routes' boxes; built on the first ask. |
| 277 | 6 | `private Call call(long key)` | A call out of its key. |

### WHERE A TRUCK IS (lines 284-367)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 30 | `public static Truck position(Call call, Route route, double t)` | Where a call's truck is at t (month fractions), or null when it is not on the road: running in over its leg, standing at its gate (loaded as it came for the first half of its stand, as it will leave for the second), o... |
| 331 | 19 | `public List<Truck> frame(long x0, long y0, long x1, long y1, double t)` | The trucks on the roads at t whose middle is in the rectangle [x0, x1) x [y0, y1) in plots, widened by a truck's length, in the order of their arrival: the month's arrivals searched for those whose visit spans t, and ... |
| 352 | 5 | `public boolean crosses(long x0, long y0, long x1, long y1)` | Whether any route meets the rectangle [x0, x1) x [y0, y1) in plots: the view keeps its frame going while one does. |
| 359 | 8 | `static double[] box(Route r)` | A route's box, {x0, y0, x1, y1} in plots, inclusive of its points. |

### reads (lines 368-401)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 1 | `public int month()` |  |
| 371 | 1 | `public List<Route> routes()` |  |
| 374 | 6 | `public List<Call> calls()` | Every truck of the month, by arrival. |
| 382 | 1 | `public int callCount()` | The trucks drawn this month: counted when the schedule is made, nothing built. |
| 385 | 1 | `public int stands()` | Loads each drawn truck stands for: N in the legend's "one per N loads", 1 for a quiet month. |
| 388 | 5 | `public int callsOf(Load l)` | The trucks drawn of one load. |
| 395 | 1 | `public double loads(Load l)` | The month's loads of one kind at the share given, before stands() and whether a route takes them (a fraction is a load not yet whole). |
| 398 | 1 | `public int touched()` | The trucks the last frame() looked at. |
| 400 | 1 | `public String toString()` |  |

