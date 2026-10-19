# TruckSchedule.java - 400 lines · 28 methods · 10 constants · model

`ham/citybuildersim/TruckSchedule.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> The month's trucks, as a pure function of time: which loads Haulage's
> trucks carry, when each reaches its gate, and where each one is at any
> moment - TrainSchedule's shape (0.8.8, batch FR5;
> docs/studies/spec-freight-and-infrastructure.md 4.7, D21). Model only:
> ui/MapView draws it on the boats' clock, on CityMap.truckRoutes(), at the
> near view alone (drawnAt()) and only while the player's setting is on
> (GamePrefs.isTrucksOnMap(), on by default).
> 
> WHY. Haulage (FR2) carries every bulk tonne's city leg and the highways
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
| 153 | THE MONTH'S TRUCKS |
| 283 | WHERE A TRUCK IS |
| 367 | · reads |

## Enum constants

| line | constant | says |
|---:|---|---|
| 96 | `TruckSchedule.Load.IMPORT` |  |
| 96 | `TruckSchedule.Load.EXPORT` |  |
| 96 | `TruckSchedule.Load.HOME` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 62 | `TruckSchedule.MONTH_SECONDS` | `BoatSchedule.MONTH_SECONDS` | Seconds of trucks a game month: the boats' (spec-freight 4.7, as the trains). |
| 65 | `TruckSchedule.LEG_SECONDS` | `6` | Seconds a truck takes to run its route, in or out: 6 (spec-freight 4.7, a visual pace, the trains'). |
| 68 | `TruckSchedule.DWELL_SECONDS` | `2` | Seconds a truck stands at its gate, loading or unloading: 2 (spec-freight 4.7). |
| 71 | `TruckSchedule.WORLD_PLOTS` | `TrainSchedule.WORLD_PLOTS` | A route's part along the world line, past the city's ground, in plots: 64, as the trains' (TrainSchedule.WORLD_PLOTS), over which a truck fades in. |
| 74 | `TruckSchedule.TRUCK_PLOTS` | `0.55` | An articulated truck's length in plots: 0.55, 16.5 m of a plot's 30 m (TrainSchedule.CAR_PLOTS, a wagon, is TileRaster.WAGON_LENGTH for scale). |
| 77 | `TruckSchedule.MOST_A_MONTH` | `1000` | The most trucks a month drawn one for one: 1,000, a truck about every 0.06 s of a 60 s month (spec-freight 4.7; the roads take many more marks than the rails). |
| 80 | `TruckSchedule.SALT` | `0x7C4B_E5E7L` | The salt the trucks' hashes start from. |
| 83 | `TruckSchedule.GROUP_BITS` | `7, ROUTE_BITS = 20, ARRIVAL_BITS = 36, ARRIVAL_SHIFT = GROUP_BITS + ROUTE_BITS` | A truck's key: its arrival in ARRIVAL_BITS ticks of a month from bit ARRIVAL_SHIFT up, its route + 1 (0 for none) in ROUTE_BITS, its group in the low GROUP_BITS. |
| 85 | `TruckSchedule.GROUP_MASK` | `(1L<<GROUP_BITS) - 1, ROUTE_MASK =(1L<<ROUTE_BITS) - 1, ARRIVAL_MASK =(1L<<AR...` | The masks that take a key's group, route and arrival back out, once shifted down. |
| 87 | `TruckSchedule.TICKS` | `(double)(1L<<ARRIVAL_BITS)` | A month's arrival ticks, 2^36: an arrival in ticks over this is its share of the month. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 126 | `private final int month` |  |
| 127 | `private final List<Route> routes` |  |
| 128 | `private final List<Group> groups` |  |
| 129 | `private final double[] loads` |  |
| 130 | `private final int count, stands` |  |
| 133 | `private long[] keys` | Built on the first ask: the trucks' keys, sorted by arrival; and each route's box, {x0, y0, x1, y1} in plots. |
| 134 | `private double[][] boxesOf` |  |
| 135 | `private int touched` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 342 | **type** `public final class TruckSchedule` | The month's trucks, as a pure function of time: which loads Haulage's trucks carry, when each reaches its gate, and where each one is at any moment - TrainSchedule's shape (0.8.8, batch FR5; docs/studies/spec-freight-... |
| 96 | 1 | **type** `public enum Load` | A load's way through the city: what it carries each way and its colour's sense (spec-freight 4.7: imports blue, exports orange, empty grey). |
| 105 | 11 | **type** `public record Route(boolean world, double[] xs, double[] ys, double[] along, double fadeTo)` | A truck's way: points in plots, from where it comes in (the highway's world line's far end, or an access road's end on the street grid) to its gate, with each point's distance along it; the first fadeTo plots are fade... |
| 107 | 5 | `public static Route through(boolean world, double[] xs, double[] ys, double fadeTo)` _(in TruckSchedule.Route)_ | A route through points: their distances along it worked out. |
| 114 | 1 | `public double length()` _(in TruckSchedule.Route)_ | Its length, in plots. |
| 118 | 1 | **type** `public record Call(Load load, int route, double arrives)` | One truck: its load, its route among the schedule's, and when it reaches its gate, in month fractions. |
| 121 | 1 | **type** `public record Truck(Call call, double x, double y, double heading, boolean loaded, double alpha)` | A truck at a moment: its call, its middle in plots, its heading (radians, east 0, south a quarter turn), whether it is loaded, and how much of it shows (0 to 1). |
| 124 | 1 | **type** `private record Group(Load load, long seed, int calls)` | One load of the month: its seed and its trucks. |
| 137 | 10 | `private TruckSchedule(int month, List<Route> routes, List<Group> groups, double[] loads, int stands)` |  |
| 149 | 3 | `public static boolean drawnAt(int level, boolean settingOn)` | Whether trucks are drawn at a map level with the player's setting: at the near view (MapFrame.L0) alone, and only with the setting on. |

### THE MONTH'S TRUCKS (lines 153-282)

| line | len | member | says |
|---:|---:|---|---|
| 166 | 12 | `public static TruckSchedule of(int month, Freight freight, List<Route> routes, double share, long salt)` | The month's schedule from Haulage's month billed: Freight's city-leg truck tonnes by load (Freight.truckedIn(), truckedOut(), truckedHome()), at the share Haulage carried (Haulage.getCarried() over getTonnes()), times... |
| 185 | 22 | `public static TruckSchedule of(int month, double[] loadsIn, List<Route> routes, double share, long salt)` | ...from the month's loads counted already, by Load's order, at the share Haulage carried and before `share` (a fraction of a load is a whole truck in the share of months it says): the headless renders' and the harness... |
| 208 | 1 | `private static double tonnesOf(double t)` |  |
| 211 | 10 | `public static int standsFor(double loads)` | Loads each truck drawn stands for when a month has `loads`: 1 up to MOST_A_MONTH, then the smallest of 1, 2, 5, 10, 20, 50, 100... |
| 223 | 1 | `private static boolean takes(Route r, Load l)` | Whether a load goes on a route: an import or an export on any, a tonne bought at home on an access road's alone. |
| 226 | 5 | `private static boolean takenBy(List<Route> routes, Load l)` | Whether any of the routes a schedule keeps takes a load. |
| 233 | 3 | `static long seed(int month, Load l, long salt)` | The seed of one load in one month; `salt` is mixed into it, so it is mixed into every hash drawn from it. |
| 238 | 5 | `static long arrivalTicks(long seed, int k)` | When a group's k-th truck reaches its gate, in ticks of a month: hashed through the month, its whole visit inside it. |
| 245 | 6 | `private int[] routesFor(Load l)` | The routes a load goes on (their indices): every one that takes it. |
| 253 | 21 | `private void build()` | The trucks' keys, sorted by arrival, and the routes' boxes; built on the first ask. |
| 276 | 6 | `private Call call(long key)` | A call out of its key. |

### WHERE A TRUCK IS (lines 283-366)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 30 | `public static Truck position(Call call, Route route, double t)` | Where a call's truck is at t (month fractions), or null when it is not on the road: running in over its leg, standing at its gate (loaded as it came for the first half of its stand, as it will leave for the second), o... |
| 330 | 19 | `public List<Truck> frame(long x0, long y0, long x1, long y1, double t)` | The trucks on the roads at t whose middle is in the rectangle [x0, x1) x [y0, y1) in plots, widened by a truck's length, in the order of their arrival: the month's arrivals searched for those whose visit spans t, and ... |
| 351 | 5 | `public boolean crosses(long x0, long y0, long x1, long y1)` | Whether any route meets the rectangle [x0, x1) x [y0, y1) in plots: the view keeps its frame going while one does. |
| 358 | 8 | `static double[] box(Route r)` | A route's box, {x0, y0, x1, y1} in plots, inclusive of its points. |

### reads (lines 367-400)

| line | len | member | says |
|---:|---:|---|---|
| 369 | 1 | `public int month()` |  |
| 370 | 1 | `public List<Route> routes()` |  |
| 373 | 6 | `public List<Call> calls()` | Every truck of the month, by arrival. |
| 381 | 1 | `public int callCount()` | The trucks drawn this month: counted when the schedule is made, nothing built. |
| 384 | 1 | `public int stands()` | Loads each drawn truck stands for: N in the legend's "one per N loads", 1 for a quiet month. |
| 387 | 5 | `public int callsOf(Load l)` | The trucks drawn of one load. |
| 394 | 1 | `public double loads(Load l)` | The month's loads of one kind at the share given, before stands() and whether a route takes them (a fraction is a load not yet whole). |
| 397 | 1 | `public int touched()` | The trucks the last frame() looked at. |
| 399 | 1 | `public String toString()` |  |

