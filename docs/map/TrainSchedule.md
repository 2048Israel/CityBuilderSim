# TrainSchedule.java - 668 lines · 49 methods · 19 constants · model

`ham/citybuildersim/TrainSchedule.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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
> Since FX5 (slice T) a train is drawn at its real length - its payload over
> WAGON_TONNES, no cap: a unit train is 80 wagons and 41.4 plots, 1.24 km -
> so a big city shows fewer, longer trains, never more short ones.
> 
> TIME. The boats' month, MONTH_SECONDS. A train runs its whole route, from
> the world line's far end to its siding's, over a leg of its length over
> TRAIN_PLOTS_A_SECOND seconds (between LEG_SECONDS and LEG_MOST), easing
> in to a stop at the siding's end, stands there DWELL_SECONDS and so much a
> wagon (DWELL_A_WAGON) and runs back out, easing away from the stand; its
> arrival is spread through the month by its hash, its whole visit inside it,
> so a train never appears or vanishes at the month's turn. An import arrives
> full and leaves empty, an export arrives empty and leaves full, and boxes
> are loaded both ways.
> 
> SIDINGS (FX5, slice T; Jerus, 2026-10-20: "real trains are really long",
> "the rail depots would have to be redesigned to accomdoate such large
> trains, as well as teh animation for them", "Queue the fix" for a train
> standing over the yard's own wagons). A route says what its siding holds
> (Route.holds()), and a train goes only on a route whose siding holds its
> whole length; a kind none holds is not drawn (unfit()). Trains are seated
> in the order they arrive, one on a siding at a time: from its front
> reaching the siding's ladder in to its rear leaving it out, no other train
> is on it. A train that finds its siding busy takes the next free route of
> its kind to the same yard, else waits for the siding of that yard (its own
> first) that is free soonest, if its whole visit still fits inside the
> month, else it is not drawn. All of it pure in the month, the tonnes and
> the routes, so panning away and back shows the same trains in the same
> places.
> 
> ROUTES (CityMap.trainRoutes(); the map's, never the model's). From
> WORLD_PLOTS out along the railway's world line, in to its gate arm's end,
> along the main line and the corridors to a yard, through its ladder and
> onto a siding: one route a yard and clear siding (one the yard's standing
> wagons are not on: the routes skip those, and a yard keeps one clear).
> Boxes and general cargo go to
> the freight station; bulk to the other yards when the station is a
> ... (13 more lines in the source)

**Uses:** [Ports](Ports.md) (14), [World](World.md) (4), [BoatSchedule](BoatSchedule.md) (3), [Freight](Freight.md) (2), [TileRaster](TileRaster.md) (1)

**Used by (13):** [CityMap](CityMap.md), [CityRuns](CityRuns.md), [Game](Game.md), [GatewayCheck](GatewayCheck.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [RailCheck](RailCheck.md), [RailDrawCheck](RailDrawCheck.md), [ReadPathCheck](ReadPathCheck.md), [TrainShapes](TrainShapes.md), [TruckRouteCheck](TruckRouteCheck.md), [TruckSchedule](TruckSchedule.md)

## Sections

| line | section |
|---:|---|
| 292 | THE MONTH'S TRAINS |
| 514 | WHERE A TRAIN IS |
| 629 | · reads |

## Enum constants

| line | constant | says |
|---:|---|---|
| 145 | `TrainSchedule.Kind.TANK` |  |
| 146 | `TrainSchedule.Kind.HOPPER` |  |
| 147 | `TrainSchedule.Kind.INTERMODAL` |  |
| 149 | `TrainSchedule.Kind.MIXED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 84 | `TrainSchedule.MONTH_SECONDS` | `BoatSchedule.MONTH_SECONDS` | Seconds of trains a game month: the boats' (spec-freight 4.4, "the clock is the boats'"). |
| 87 | `TrainSchedule.TRAIN_PLOTS_A_SECOND` | `50` | A train's mean pace along its route, in plots a second: 50 (a visual pace; its easing doubles it at the world line's end, a plot and a half a frame at 8 px and 60 frames a second). |
| 90 | `TrainSchedule.LEG_SECONDS` | `6` | The shortest leg, in or out, in seconds: 6 (spec-freight 4.4, a visual pace), for a route of 300 plots or less. |
| 93 | `TrainSchedule.LEG_MOST` | `10` | The longest leg, in seconds: 10, for a route of 500 plots or more, so a long way stays inside the 60 s month (a visit is two legs and a stand). |
| 96 | `TrainSchedule.DWELL_SECONDS` | `3` | Seconds a train stands on its siding before its wagons are counted: 3 (spec-freight 4.4). |
| 99 | `TrainSchedule.DWELL_A_WAGON` | `0.025` | ...and the seconds a wagon adds to its stand: 0.025, so a unit train of 80 stands 5 s and a mixed train of 15 3.375 s - the longer the train, the longer it takes to load or empty. |
| 102 | `TrainSchedule.WORLD_PLOTS` | `64` | A route's part along the world line, past the city's ground, in plots: 64, about 1.9 km, over which a train fades in. |
| 105 | `TrainSchedule.UNIT_TONNES` | `8_000` | A unit train's payload, hoppers or tank wagons: 8,000 t (est., between Europe's 4,000 t block trains [R47] and a 12,100 t North American coal train [R39]). |
| 108 | `TrainSchedule.MIXED_TONNES` | `1_500` | A mixed (manifest) train's payload: 1,500 t (Europe's route limits run 1,300 to 2,400 t [R45]). |
| 111 | `TrainSchedule.INTERMODAL_BOXES` | `100` | An intermodal train's boxes: 100 (a European 750 m train is 50 wagons [R46]; Union Pacific's averaged 173 [R40]). |
| 114 | `TrainSchedule.WAGON_TONNES` | `100` | Tonnes a wagon carries, for the count of wagons drawn: 100 (spec-freight 4.4, "the payload / 100 t"). |
| 117 | `TrainSchedule.BOXES_A_WAGON` | `2` | Boxes a flat wagon carries: 2, so an intermodal train is 50 wagons, as [R46]'s. |
| 120 | `TrainSchedule.CAR_PLOTS` | `TileRaster.WAGON_LENGTH` | A wagon's length in plots: TileRaster.WAGON_LENGTH, about 15 m. |
| 123 | `TrainSchedule.LOCO_PLOTS` | `0.7` | A locomotive's length in plots: 0.7, about 21 m. |
| 126 | `TrainSchedule.MOST_A_MONTH` | `30` | The most trains a month drawn one for one: 30, a train about every 2 s (spec-freight 4.4's worked example, 25, "reads as a busy line, not a blur"). |
| 129 | `TrainSchedule.SALT` | `0x7A41_45EDL` | The salt the trains' hashes start from. |
| 132 | `TrainSchedule.GROUP_BITS` | `7, ROUTE_BITS = 20, ARRIVAL_BITS = 36, ARRIVAL_SHIFT = GROUP_BITS + ROUTE_BITS` | A train's key: its arrival in ARRIVAL_BITS ticks of a month from bit ARRIVAL_SHIFT up, its route + 1 (0 for none) in ROUTE_BITS, its group in the low GROUP_BITS. |
| 134 | `TrainSchedule.GROUP_MASK` | `(1L<<GROUP_BITS) - 1, ROUTE_MASK =(1L<<ROUTE_BITS) - 1, ARRIVAL_MASK =(1L<<AR...` | The masks that take a key's group, route and arrival back out, once shifted down. |
| 136 | `TrainSchedule.TICKS` | `(double)(1L<<ARRIVAL_BITS)` | A month's arrival ticks, 2^36: an arrival in ticks over this is its share of the month. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 151 | `private final Ports.Cargo cargo` |  |
| 152 | `private final String label` |  |
| 153 | `private final double tonnes` |  |
| 154 | `private final int wagons` |  |
| 270 | `private final int month` |  |
| 271 | `private final List<Route> routes` |  |
| 272 | `private final List<Group> groups` |  |
| 273 | `private final int count, stands` |  |
| 274 | `private final boolean boxes` |  |
| 277 | `private long[] keys` | Built on the first ask: the trains' keys, sorted by arrival; each route's box, {x0, y0, x1, y1} in plots; and the trains given no route. |
| 278 | `private double[][] boxesOf` |  |
| 279 | `private int touched, unfit` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 81 | 588 | **type** `public final class TrainSchedule` | The month's trains, as a pure function of time: which trains the city's railed tonnes make, when each reaches its yard, and where each one is at any moment - BoatSchedule's shape, almost line for line (0.8.2, batch FR... |
| 144 | 39 | **type** `public enum Kind` | The kinds of train, one a kind of cargo (Ports.Cargo): its words, its payload in tonnes and the wagons it is drawn with - its real count since FX5 (slice T), its payload over WAGON_TONNES with no cap: a unit train of ... |
| 156 | 3 | `Kind(Ports.Cargo cargo, String label, double tonnes, int wagons)` _(in TrainSchedule.Kind)_ |  |
| 160 | 1 | `public Ports.Cargo cargo()` _(in TrainSchedule.Kind)_ |  |
| 161 | 1 | `public String label()` _(in TrainSchedule.Kind)_ |  |
| 163 | 1 | `public double tonnes()` _(in TrainSchedule.Kind)_ | A train's payload, in tonnes. |
| 165 | 1 | `public int wagons()` _(in TrainSchedule.Kind)_ | Its wagons, between its two locomotives. |
| 167 | 1 | `public double length()` _(in TrainSchedule.Kind)_ | Its length, both locomotives and its wagons, in plots. |
| 169 | 1 | `public double standSeconds()` _(in TrainSchedule.Kind)_ | Seconds it stands on its siding: DWELL_SECONDS and DWELL_A_WAGON a wagon. |
| 171 | 1 | `public boolean toStation()` _(in TrainSchedule.Kind)_ | Whether it goes to the freight station: boxes and general cargo. |
| 174 | 4 | `public static Kind of(Ports.Cargo c)` _(in TrainSchedule.Kind)_ | The kind that carries a kind of cargo. |
| 179 | 3 | `private static int wagonsOf(double tonnes)` _(in TrainSchedule.Kind)_ |  |
| 185 | 5 | `private static double longestStand()` | The longest stand of any kind, in seconds (a unit train's): arrivals are spread so that this and the longest leg still end inside the month. |
| 210 | 47 | **type** `public record Route(int yard, boolean station, int siding, boolean works, double[] xs, double[] ys, double[...` | A train's way: its yard (an index in CityRuns.yards()), whether that yard is the freight station, its siding (0 to the yard's TileRaster.tracksOf() - 1, -1 with none), and the way as points in plots - the world line's... |
| 214 | 3 | `public static Route through(int yard, boolean station, int siding, double[] xs, double[] ys, double fadeTo)` _(in TrainSchedule.Route)_ | A hand-built route through points: their distances along it worked out; it holds any train (a siding's, 0 with none). |
| 219 | 3 | `public static Route through(int yard, boolean station, int siding, double[] xs, double[] ys, double fadeTo, double holds, doubl...` _(in TrainSchedule.Route)_ | ...saying what its siding holds and where it begins (FX5, slice T): `holds` plots of train, from `enters` plots along the route. |
| 230 | 3 | `public static Route toWorks(double[] xs, double[] ys, double fadeTo)` _(in TrainSchedule.Route)_ | A route to a works' siding (FR7 slice 3, 2026-10-18): no yard (-1), not the station, no siding of a yard's fan (-1), `works` true - a bulk unit train's alone, from the world line along the main line straight in to the... |
| 235 | 5 | `public static Route toWorks(double[] xs, double[] ys, double fadeTo, double holds)` _(in TrainSchedule.Route)_ | ...whose siding holds `holds` plots of train, from where it leaves the main line to its end (FX5, slice T). |
| 241 | 9 | `private static Route through(int yard, boolean station, int siding, boolean works, double[] xs, double[] ys, double fadeTo, dou...` _(in TrainSchedule.Route)_ |  |
| 252 | 1 | `public double length()` _(in TrainSchedule.Route)_ | Its length, the world line's far end to the siding's, in plots. |
| 255 | 1 | `public double legSeconds()` _(in TrainSchedule.Route)_ | Seconds a train takes to run it, in or out: its length over TRAIN_PLOTS_A_SECOND, no less than LEG_SECONDS and no more than LEG_MOST. |
| 259 | 1 | **type** `public record Call(Kind kind, boolean inbound, int route, double arrives)` | One train: its kind, whether it lands cargo (an import, or boxes, which go both ways) or comes to load it, its route among the schedule's (-1 with none), and when it reaches its siding's end, in month fractions. |
| 262 | 1 | **type** `public record Train(Call call, double front, boolean loaded, boolean standing)` | A train at a moment: its call, how far along its route its front is, in plots, whether it is loaded, and whether it stands on its siding. |
| 265 | 1 | **type** `public record Car(double x, double y, double heading, boolean loco, double alpha)` | One vehicle of a train at a moment: its middle in plots, its heading along the route toward the yard (radians, east 0, south a quarter turn), whether it is a locomotive, and how much of it shows (0 at the world line's... |
| 268 | 1 | **type** `private record Group(Kind kind, boolean inbound, long seed, int calls)` | One (kind, direction) of the month: its seed and its trains. |
| 281 | 10 | `private TrainSchedule(int month, List<Route> routes, List<Group> groups, int stands, boolean boxes)` |  |

### THE MONTH'S TRAINS (lines 292-513)

| line | len | member | says |
|---:|---:|---|---|
| 297 | 3 | `public static boolean boxesByRail(ham.citybuildersim.sectors.Freight rail)` | Whether the freight station is a container yard: the railway landed or shipped any boxes the month billed (the map's yard and these trains read the same). |
| 302 | 11 | `public static TrainSchedule of(int month, ham.citybuildersim.sectors.Freight rail, List<Route> routes)` | The month's schedule from the railway's month billed (sectors.Freight.railedIn() and railedOut()), on the map's routes; none with no railway. |
| 320 | 21 | `public static TrainSchedule of(int month, double[] in, double[] out, List<Route> routes)` | ...from tonnes by kind of cargo (Ports.Cargo's order), landed and shipped: each kind's trains counted at its payload - times stands(), past MOST_A_MONTH trains in all - each to go on a route its kind goes to, by a has... |
| 342 | 4 | `private static double tonnes(double[] t, Kind k)` |  |
| 347 | 5 | `private static void add(List<Group> groups, int month, Kind k, boolean inbound, double tonnes, int stands)` |  |
| 354 | 3 | `static long seed(int month, Kind k, boolean inbound)` | The seed of one (kind, direction) in one month. |
| 363 | 5 | `static long arrivalTicks(long seed, int k)` | When a group's k-th train reaches its siding's end by its hash, in ticks of a month: spread through the month with the longest leg (LEG_MOST) and the longest stand either side, so any train's whole visit is inside it ... |
| 379 | 12 | `private int[] routesFor(Kind k)` | The routes a kind goes on: boxes and general cargo the freight station's; bulk the other yards' when the station is a container yard, every yard's when it is not - and (FR7 slice 3) a dry bulk unit train the works' si... |
| 397 | 7 | `static double[] zone(Kind kind, Route r, double a)` | The span of the month, in fractions, a train of a kind that reaches its siding's end at `a` is on the siding: from its front reaching the route's `enters` in (the front is at len x (1 - (1 - u)^2) in the leg, u from 0... |
| 406 | 4 | `private static boolean fits(Kind kind, Route r, long ticks)` | Whether a train of a kind arriving at `ticks` on a route has its whole visit, its leg in, its stand and its leg out, inside the month. |
| 412 | 7 | `private static double busyUntil(Kind kind, Route r, long ticks, List<double[]> taken)` | When the earliest-ending of the visits already `taken` on a siding that a train arriving at `ticks` would share it with ends, in month fractions; -1 when the siding is free for it then. |
| 426 | 26 | `private long[] seat(Kind kind, int[] rs, int p, long arrival, java.util.Map<Integer, List<double[]>> taken)` | Seats one train: route index and arrival ticks, or null when it cannot be (FX5, slice T). |
| 454 | 11 | `private static long firstFree(Kind kind, Route r, long arrival, List<double[]> taken)` | The first moment, in ticks from `arrival` on, a train of a kind can arrive at a route with every visit `taken` clear of it and its whole visit inside the month; -1 when there is none. |
| 471 | 34 | `private void build()` | The trains' keys, sorted by arrival, and the routes' boxes; built on the first ask. |
| 507 | 6 | `private Call call(long key)` | A call out of its key. |

### WHERE A TRAIN IS (lines 514-628)

| line | len | member | says |
|---:|---:|---|---|
| 531 | 15 | `public static Train position(Call call, Route route, double t)` | Where a call's train is at t (month fractions), or null when it is not on the line: running in over its leg, standing on its siding, or running back out. |
| 552 | 24 | `public static List<Car> cars(Train train, Route route)` | A train's vehicles, front first: a locomotive, its wagons, a locomotive, each a whole length behind the one before along the route - those still past the world line's far end left out. |
| 583 | 28 | `public List<Train> frame(long x0, long y0, long x1, long y1, double t)` | The trains on the line at t with a vehicle in the rectangle [x0, x1) x [y0, y1) in plots, widened by a vehicle's length: the month's arrivals searched for those whose visit spans t, and only those looked at (touched()). |
| 613 | 5 | `public boolean crosses(long x0, long y0, long x1, long y1)` | Whether any route meets the rectangle [x0, x1) x [y0, y1) in plots: the view keeps its frame going while one does. |
| 620 | 8 | `static double[] box(Route r)` | A route's box, {x0, y0, x1, y1} in plots, inclusive of its points. |

### reads (lines 629-668)

| line | len | member | says |
|---:|---:|---|---|
| 631 | 1 | `public int month()` |  |
| 632 | 1 | `public List<Route> routes()` |  |
| 635 | 6 | `public List<Call> calls()` | Every train of the month, by arrival. |
| 643 | 1 | `public int callCount()` | The month's trains drawn, all kinds: counted when the schedule is made, nothing built. |
| 646 | 1 | `public int stands()` | How many trains each one drawn stands for: 1 up to MOST_A_MONTH a month. |
| 649 | 1 | `public boolean boxes()` | Whether the freight station is a container yard this month (boxesByRail()'s rule, from the tonnes given). |
| 652 | 1 | `public int touched()` | The trains the last frame() looked at. |
| 655 | 4 | `public int unfit()` | The month's trains given no route (FX5, slice T): no route's siding held them, or none was free at a moment their visit fitted inside the month - counted, not drawn. |
| 661 | 5 | `public int callsOf(Kind k)` | The trains drawn of one kind. |
| 667 | 1 | `public String toString()` |  |

