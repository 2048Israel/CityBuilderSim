# TrainSchedule.java - 470 lines · 38 methods · 17 constants · model

`ham/citybuildersim/TrainSchedule.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> The month's trains, as a pure function of time: which trains the city's
> railed tonnes make, when each reaches its yard, and where each one is at
> any moment - BoatSchedule's shape, almost line for line (0.8.2, batch FR1;
> docs/studies/spec-freight-and-infrastructure.md 4.4, D16). Model only:
> ui/MapView draws it on the boats' clock, on CityMap.trainRoutes().
> 
> WHY. The railway was a share of the freight band and a line on the map
> with nothing running on it, while the ships already showed the sea trade
> as boats that follow it exactly. So A TRAIN IS A SCHEDULE ENTRY, NOT AN
> OBJECT: a kind, a direction, a route and an arrival time hashed from
> (kind, direction, month, train number), and a train's place is
> route(call, t). Nothing is simulated, stored or saved; panning away and
> back shows the same train in the same place, and a city that rails
> nothing has no trains.
> 
> CALLS. The month billed's railed tonnes by kind (sectors.Freight.railedIn()
> and railedOut()) over a train's payload: dry bulk in unit trains of
> hoppers and liquids in unit trains of tank wagons, UNIT_TONNES each;
> general cargo in mixed trains of MIXED_TONNES; boxes in intermodal trains
> of INTERMODAL_BOXES, one train carrying both ways, so the larger direction
> counts (the boats' rule 3). A fraction of a train is a whole one in the
> share of months its fraction says (BoatSchedule.callsFor()). Past
> MOST_A_MONTH trains a month each train drawn stands for stands() of them,
> every kind alike, so a big city's line reads as busy and not as a blur.
> 
> TIME. The boats' month, MONTH_SECONDS. A train runs its whole route, from
> the world line's far end to its siding's, over LEG_SECONDS, stands there
> DWELL_SECONDS and runs back out; its arrival is spread through the month
> by its hash, its whole visit inside it, so a train never appears or
> vanishes at the month's turn. An import arrives full and leaves empty, an
> export arrives empty and leaves full, and boxes are loaded both ways.
> 
> ROUTES (CityMap.trainRoutes(); the map's, never the model's). From
> WORLD_PLOTS out along the railway's world line, in to its gate arm's end,
> along the main line and the corridors to a yard, through its ladder and
> onto a siding: one route a yard and siding. Boxes and general cargo go to
> the freight station; bulk to the other yards when the station is a
> container yard (boxesByRail()), to every yard when it is not. A train
> fades in over the world line's part. Since FR7 (slice 3, 2026-10-18) a
> route may also run straight along the main line into a works' siding, no
> yard (Route.toWorks()): real unit trains skip the yard, so only a dry bulk
> hopper train goes there, one with the yards' routes; mixed and container
> trains still run to the yards.
> 
> A TRAIN. A locomotive at each end - distributed power, as a unit train
> has - so it leaves the way it came with no run-round, and its wagons
> between: each vehicle's place is its distance along the route behind the
> train's front (cars()). A frame looks only at the trains on the line now,
> found by a binary search of the month's arrivals.

**Uses:** [Ports](Ports.md) (14), [World](World.md) (4), [BoatSchedule](BoatSchedule.md) (3), [Freight](Freight.md) (2), [TileRaster](TileRaster.md) (1)

**Used by (12):** [CityMap](CityMap.md), [Game](Game.md), [GatewayCheck](GatewayCheck.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [RailCheck](RailCheck.md), [RailDrawCheck](RailDrawCheck.md), [ReadPathCheck](ReadPathCheck.md), [TrainShapes](TrainShapes.md), [TruckRouteCheck](TruckRouteCheck.md), [TruckSchedule](TruckSchedule.md)

## Sections

| line | section |
|---:|---|
| 217 | THE MONTH'S TRAINS |
| 339 | WHERE A TRAIN IS |
| 437 | · reads |

## Enum constants

| line | constant | says |
|---:|---|---|
| 110 | `TrainSchedule.Kind.TANK` |  |
| 111 | `TrainSchedule.Kind.HOPPER` |  |
| 112 | `TrainSchedule.Kind.INTERMODAL` |  |
| 114 | `TrainSchedule.Kind.MIXED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 60 | `TrainSchedule.MONTH_SECONDS` | `BoatSchedule.MONTH_SECONDS` | Seconds of trains a game month: the boats' (spec-freight 4.4, "the clock is the boats'"). |
| 63 | `TrainSchedule.LEG_SECONDS` | `6` | Seconds a train takes to run its route, in or out: 6 (spec-freight 4.4, a visual pace). |
| 66 | `TrainSchedule.DWELL_SECONDS` | `3` | Seconds a train stands on its siding: 3 (spec-freight 4.4). |
| 69 | `TrainSchedule.WORLD_PLOTS` | `64` | A route's part along the world line, past the city's ground, in plots: 64, about 1.9 km, over which a train fades in. |
| 72 | `TrainSchedule.UNIT_TONNES` | `8_000` | A unit train's payload, hoppers or tank wagons: 8,000 t (est., between Europe's 4,000 t block trains [R47] and a 12,100 t North American coal train [R39]). |
| 75 | `TrainSchedule.MIXED_TONNES` | `1_500` | A mixed (manifest) train's payload: 1,500 t (Europe's route limits run 1,300 to 2,400 t [R45]). |
| 78 | `TrainSchedule.INTERMODAL_BOXES` | `100` | An intermodal train's boxes: 100 (a European 750 m train is 50 wagons [R46]; Union Pacific's averaged 173 [R40]). |
| 81 | `TrainSchedule.WAGON_TONNES` | `100` | Tonnes a wagon carries, for the count of wagons drawn: 100 (spec-freight 4.4, "the payload / 100 t"). |
| 84 | `TrainSchedule.BOXES_A_WAGON` | `2` | Boxes a flat wagon carries: 2, so an intermodal train is 50 wagons, as [R46]'s. |
| 87 | `TrainSchedule.MOST_WAGONS` | `40` | The most wagons a train is drawn with: 40 (spec-freight 4.4). |
| 90 | `TrainSchedule.CAR_PLOTS` | `TileRaster.WAGON_LENGTH` | A wagon's length in plots: TileRaster.WAGON_LENGTH, about 15 m. |
| 93 | `TrainSchedule.LOCO_PLOTS` | `0.7` | A locomotive's length in plots: 0.7, about 21 m. |
| 96 | `TrainSchedule.MOST_A_MONTH` | `30` | The most trains a month drawn one for one: 30, a train about every 2 s (spec-freight 4.4's worked example, 25, "reads as a busy line, not a blur"). |
| 99 | `TrainSchedule.SALT` | `0x7A41_45EDL` | The salt the trains' hashes start from. |
| 102 | `TrainSchedule.GROUP_BITS` | `7, ROUTE_BITS = 20, ARRIVAL_BITS = 36, ARRIVAL_SHIFT = GROUP_BITS + ROUTE_BITS` | A train's key: its arrival in ARRIVAL_BITS ticks of a month from bit ARRIVAL_SHIFT up, its route + 1 (0 for none) in ROUTE_BITS, its group in the low GROUP_BITS. |
| 104 | `TrainSchedule.GROUP_MASK` | `(1L<<GROUP_BITS) - 1, ROUTE_MASK =(1L<<ROUTE_BITS) - 1, ARRIVAL_MASK =(1L<<AR...` | The masks that take a key's group, route and arrival back out, once shifted down. |
| 106 | `TrainSchedule.TICKS` | `(double)(1L<<ARRIVAL_BITS)` | A month's arrival ticks, 2^36: an arrival in ticks over this is its share of the month. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 116 | `private final Ports.Cargo cargo` |  |
| 117 | `private final String label` |  |
| 118 | `private final double tonnes` |  |
| 119 | `private final int wagons` |  |
| 195 | `private final int month` |  |
| 196 | `private final List<Route> routes` |  |
| 197 | `private final List<Group> groups` |  |
| 198 | `private final int count, stands` |  |
| 199 | `private final boolean boxes` |  |
| 202 | `private long[] keys` | Built on the first ask: the trains' keys, sorted by arrival; and each route's box, {x0, y0, x1, y1} in plots. |
| 203 | `private double[][] boxesOf` |  |
| 204 | `private int touched` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 414 | **type** `public final class TrainSchedule` | The month's trains, as a pure function of time: which trains the city's railed tonnes make, when each reaches its yard, and where each one is at any moment - BoatSchedule's shape, almost line for line (0.8.2, batch FR... |
| 109 | 37 | **type** `public enum Kind` | The kinds of train, one a kind of cargo (Ports.Cargo): its words, its payload in tonnes and the wagons it is drawn with. |
| 121 | 3 | `Kind(Ports.Cargo cargo, String label, double tonnes, int wagons)` _(in TrainSchedule.Kind)_ |  |
| 125 | 1 | `public Ports.Cargo cargo()` _(in TrainSchedule.Kind)_ |  |
| 126 | 1 | `public String label()` _(in TrainSchedule.Kind)_ |  |
| 128 | 1 | `public double tonnes()` _(in TrainSchedule.Kind)_ | A train's payload, in tonnes. |
| 130 | 1 | `public int wagons()` _(in TrainSchedule.Kind)_ | Its wagons, between its two locomotives. |
| 132 | 1 | `public double length()` _(in TrainSchedule.Kind)_ | Its length, both locomotives and its wagons, in plots. |
| 134 | 1 | `public boolean toStation()` _(in TrainSchedule.Kind)_ | Whether it goes to the freight station: boxes and general cargo. |
| 137 | 4 | `public static Kind of(Ports.Cargo c)` _(in TrainSchedule.Kind)_ | The kind that carries a kind of cargo. |
| 142 | 3 | `private static int wagonsOf(double tonnes)` _(in TrainSchedule.Kind)_ |  |
| 156 | 26 | **type** `public record Route(int yard, boolean station, int siding, boolean works, double[] xs, double[] ys, double[...` | A train's way: its yard (an index in CityRuns.yards()), whether that yard is the freight station, its siding (0 to TileRaster.YARD_TRACKS - 1, -1 with none), and the way as points in plots - the world line's far end f... |
| 159 | 3 | `public static Route through(int yard, boolean station, int siding, double[] xs, double[] ys, double fadeTo)` _(in TrainSchedule.Route)_ | A route through points: their distances along it worked out. |
| 169 | 3 | `public static Route toWorks(double[] xs, double[] ys, double fadeTo)` _(in TrainSchedule.Route)_ | A route to a works' siding (FR7 slice 3, 2026-10-18): no yard (-1), not the station, no siding of a yard's fan (-1), `works` true - a bulk unit train's alone, from the world line along the main line straight in to the... |
| 173 | 5 | `private static Route through(int yard, boolean station, int siding, boolean works, double[] xs, double[] ys, double fadeTo)` _(in TrainSchedule.Route)_ |  |
| 180 | 1 | `public double length()` _(in TrainSchedule.Route)_ | Its length, the world line's far end to the siding's, in plots. |
| 184 | 1 | **type** `public record Call(Kind kind, boolean inbound, int route, double arrives)` | One train: its kind, whether it lands cargo (an import, or boxes, which go both ways) or comes to load it, its route among the schedule's (-1 with none), and when it reaches its siding's end, in month fractions. |
| 187 | 1 | **type** `public record Train(Call call, double front, boolean loaded, boolean standing)` | A train at a moment: its call, how far along its route its front is, in plots, whether it is loaded, and whether it stands on its siding. |
| 190 | 1 | **type** `public record Car(double x, double y, double heading, boolean loco, double alpha)` | One vehicle of a train at a moment: its middle in plots, its heading along the route toward the yard (radians, east 0, south a quarter turn), whether it is a locomotive, and how much of it shows (0 at the world line's... |
| 193 | 1 | **type** `private record Group(Kind kind, boolean inbound, long seed, int calls)` | One (kind, direction) of the month: its seed and its trains. |
| 206 | 10 | `private TrainSchedule(int month, List<Route> routes, List<Group> groups, int stands, boolean boxes)` |  |

### THE MONTH'S TRAINS (lines 217-338)

| line | len | member | says |
|---:|---:|---|---|
| 222 | 3 | `public static boolean boxesByRail(ham.citybuildersim.sectors.Freight rail)` | Whether the freight station is a container yard: the railway landed or shipped any boxes the month billed (the map's yard and these trains read the same). |
| 227 | 11 | `public static TrainSchedule of(int month, ham.citybuildersim.sectors.Freight rail, List<Route> routes)` | The month's schedule from the railway's month billed (sectors.Freight.railedIn() and railedOut()), on the map's routes; none with no railway. |
| 245 | 21 | `public static TrainSchedule of(int month, double[] in, double[] out, List<Route> routes)` | ...from tonnes by kind of cargo (Ports.Cargo's order), landed and shipped: each kind's trains counted at its payload - times stands(), past MOST_A_MONTH trains in all - each to go on a route its kind goes to, by a has... |
| 267 | 4 | `private static double tonnes(double[] t, Kind k)` |  |
| 272 | 5 | `private static void add(List<Group> groups, int month, Kind k, boolean inbound, double tonnes, int stands)` |  |
| 279 | 3 | `static long seed(int month, Kind k, boolean inbound)` | The seed of one (kind, direction) in one month. |
| 284 | 5 | `static long arrivalTicks(long seed, int k)` | When a group's k-th train reaches its siding's end, in ticks of a month: hashed through the month, its whole visit inside it. |
| 298 | 11 | `private int[] routesFor(Kind k)` | The routes a kind goes on: boxes and general cargo the freight station's; bulk the other yards' when the station is a container yard, every yard's when it is not - and (FR7 slice 3) a dry bulk unit train the works' si... |
| 311 | 19 | `private void build()` | The trains' keys, sorted by arrival, and the routes' boxes; built on the first ask. |
| 332 | 6 | `private Call call(long key)` | A call out of its key. |

### WHERE A TRAIN IS (lines 339-436)

| line | len | member | says |
|---:|---:|---|---|
| 348 | 9 | `public static Train position(Call call, Route route, double t)` | Where a call's train is at t (month fractions), or null when it is not on the line: running in over its leg, standing on its siding, or running back out. |
| 363 | 24 | `public static List<Car> cars(Train train, Route route)` | A train's vehicles, front first: a locomotive, its wagons, a locomotive, each a whole length behind the one before along the route - those still past the world line's far end left out. |
| 394 | 25 | `public List<Train> frame(long x0, long y0, long x1, long y1, double t)` | The trains on the line at t with a vehicle in the rectangle [x0, x1) x [y0, y1) in plots, widened by a vehicle's length: the month's arrivals searched for those whose visit spans t, and only those looked at (touched()). |
| 421 | 5 | `public boolean crosses(long x0, long y0, long x1, long y1)` | Whether any route meets the rectangle [x0, x1) x [y0, y1) in plots: the view keeps its frame going while one does. |
| 428 | 8 | `static double[] box(Route r)` | A route's box, {x0, y0, x1, y1} in plots, inclusive of its points. |

### reads (lines 437-470)

| line | len | member | says |
|---:|---:|---|---|
| 439 | 1 | `public int month()` |  |
| 440 | 1 | `public List<Route> routes()` |  |
| 443 | 6 | `public List<Call> calls()` | Every train of the month, by arrival. |
| 451 | 1 | `public int callCount()` | The month's trains drawn, all kinds: counted when the schedule is made, nothing built. |
| 454 | 1 | `public int stands()` | How many trains each one drawn stands for: 1 up to MOST_A_MONTH a month. |
| 457 | 1 | `public boolean boxes()` | Whether the freight station is a container yard this month (boxesByRail()'s rule, from the tonnes given). |
| 460 | 1 | `public int touched()` | The trains the last frame() looked at. |
| 463 | 5 | `public int callsOf(Kind k)` | The trains drawn of one kind. |
| 469 | 1 | `public String toString()` |  |

