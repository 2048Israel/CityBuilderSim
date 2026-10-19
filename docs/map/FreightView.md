# FreightView.java - 342 lines · 19 methods · 1 constants · model

`ham/citybuildersim/FreightView.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> The Freight sector's divisions as the screens draw them (batch FR4;
> docs/studies/spec-freight-and-infrastructure.md 6): the city's freight this
> month in legs, and the Haulage, Rail and Ports pages with each division's
> fleet against what it needs, its quote with the rule's floor and target
> where the model keeps them, and its bill.
> 
> WHY. Since FR2, FR3, FR6 and FR7 the Freight sector is three businesses in
> one set of books - the trucks, the railway and the quay - and its page (a
> sector's, SectorScreen) showed them as one list of lines under the
> railway's, "the screens' division strip is FR4's" (Freight.ownLines()). The
> study's board 2 draws them apart: a strip under the page chips, All ·
> Haulage · Rail · Ports, where All is the month's tonnes by leg and by mode
> and each bar is a door to its division. These are the figures and the
> doors, held by a harness (FreightViewCheck) to the model's own getters
> rather than worked out in the screen, which only paints them. Infrastructure
> › Freight and › Ports read the same View.
> 
> WHICH MONTH. The month billed, as the getters keep it (Freight.haul(),
> Haulage.billMonth(), PortDivision.billMonth()): the tonnes and bills of the
> last haul, the fleets and quotes in force now. Before the first haul every
> flow is zero and the quotes are the estimates the model gives. Every figure
> is finite whatever the city: a ratio with nothing to divide by is 0, and a
> figure the model has not struck (Freight.getAllowedRevenue() is NaN until a
> haul) reads 0 beside a "known" flag.
> 
> WHAT IS LEFT OUT, because the model keeps no such figure and no tally was
> added for the screen: the vans' tonnes (the "distribution" leg: a van's load
> is a nameplate fleet size, Sector.vansNeeded(), not a month's flow), the
> siding's relief on the tonnes that crossed the boundary (only FR6's railed
> city tonnes are kept), a per-shipper count of tonnes (the bills are kept,
> not the tonnes billed), Haulage's rule floor (its running cost is a local of
> the reprice), a history of the trucks against the need, and the month's
> calls by ship class (BoatSchedule keeps them by kind).
> 
> Pure: it reads the city and changes nothing.

**Uses:** [Ports](Ports.md) (11), [Freight](Freight.md) (7), [PortDivision](PortDivision.md) (5), [Game](Game.md) (3), [BuildingManager](BuildingManager.md) (3), [Haulage](Haulage.md) (3), [BoatSchedule](BoatSchedule.md) (1), [Sector](Sector.md) (1)

**Used by (3):** [FreightViewCheck](FreightViewCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 53 | THE VOCABULARY |
| 109 | THE RECORDS |
| 220 | THE VIEW |

## Enum constants

| line | constant | says |
|---:|---|---|
| 59 | `FreightView.Division.ALL` |  |
| 59 | `FreightView.Division.HAULAGE` |  |
| 59 | `FreightView.Division.RAIL` |  |
| 59 | `FreightView.Division.PORTS` |  |
| 70 | `FreightView.Leg.LONG_HAUL` |  |
| 70 | `FreightView.Leg.CITY_LEG` |  |
| 81 | `FreightView.Mode.WORLD_LORRY` |  |
| 82 | `FreightView.Mode.RAIL` |  |
| 83 | `FreightView.Mode.SEA` |  |
| 84 | `FreightView.Mode.TRUCK` |  |
| 85 | `FreightView.Mode.SIDING` |  |
| 107 | `FreightView.Unit.PER_TONNE` |  |
| 107 | `FreightView.Unit.SHARE_OF_LORRY` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 218 | `FreightView.RAIL_BILLED_AS` | `"Rail freight"` | The line the railway's bill is raised under on a shipper's input rows (Freight.haul()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `private final String label` |  |
| 72 | `private final String label` |  |
| 87 | `private final String label` |  |
| 88 | `private final boolean abroad` |  |
| 89 | `private final Division door` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 294 | **type** `public final class FreightView` | The Freight sector's divisions as the screens draw them (batch FR4; docs/studies/spec-freight-and-infrastructure.md 6): the city's freight this month in legs, and the Haulage, Rail and Ports pages with each division's... |
| 51 | 1 | `private FreightView()` |  |

### THE VOCABULARY (lines 53-108)

| line | len | member | says |
|---:|---:|---|---|
| 58 | 9 | **type** `public enum Division` | The division strip under the page chips: All, then the three divisions. |
| 63 | 1 | `Division(String label)` _(in FreightView.Division)_ |  |
| 65 | 1 | `public String label()` _(in FreightView.Division)_ |  |
| 69 | 9 | **type** `public enum Leg` | The two legs of the city's freight the model counts: what crossed the boundary, and the trucks' and sidings' leg inside the city. |
| 74 | 1 | `Leg(String label)` _(in FreightView.Leg)_ |  |
| 76 | 1 | `public String label()` _(in FreightView.Leg)_ |  |
| 80 | 25 | **type** `public enum Mode` | How a leg's tonnes moved: a long haul by the world's lorries, the railway or the ships; a city leg by Haulage's trucks or the railway's sidings. |
| 91 | 5 | `Mode(String label, boolean abroad, Division door)` _(in FreightView.Mode)_ |  |
| 97 | 1 | `public String label()` _(in FreightView.Mode)_ |  |
| 100 | 1 | `public boolean abroad()` _(in FreightView.Mode)_ | Whether the tonnes are paid abroad (the study's hatching): the world's lorries and the ships. |
| 103 | 1 | `public Division door()` _(in FreightView.Mode)_ | The division a bar of this mode opens; ALL for a bar with no door (the world's lorries are nobody's here). |
| 107 | 1 | **type** `public enum Unit` | How a quote is stated: a price a tonne in the city's money, or a share of what a lorry charges. |

### THE RECORDS (lines 109-219)

| line | len | member | says |
|---:|---:|---|---|
| 114 | 1 | **type** `public record Row(Leg leg, Mode mode, double tonnes, boolean abroad, Division door)` | One bar of the legs chart: a mode's tonnes on a leg, paid abroad or not, and the division it opens. |
| 117 | 8 | **type** `public record Legs(double longHaul, double cityLeg, List<Row> rows)` | The city's freight this month in legs: the long haul's tonnes (everything that crossed), the city leg's, and the five bars in leg order (world lorry, rail, sea; truck, siding). |
| 119 | 5 | `public List<Row> of(Leg leg)` _(in FreightView.Legs)_ | The bars of one leg, in order. |
| 127 | 1 | **type** `public record Quote(Unit unit, double quote, boolean known, OptionalDouble floor, OptionalDouble target)` | A division's price: in force now, whether a month has struck it, and the rule's floor and target where the model keeps them (empty where it does not). |
| 130 | 20 | **type** `public record HaulagePage(double trucks, double needed, double asked, double shortBy, int depots, double de...` | Haulage's page: the trucks against the city leg's need, the depots, the month's carriage, the quote and its bill. |
| 152 | 27 | **type** `public record RailPage(double sets, double setsNeeded, double track, double stations, double sidings, doubl...` | Rail's page: the wagon sets and the track against the freight offered, FR6's own-bulk tonnes and bill, the quote and its bill. |
| 181 | 15 | **type** `public record Berth(Ports.Cargo kind, int berths, double capacity, double onSite, double tonnes, double use...` | One kind of berth on the Ports page: the terminals standing, the tonnes against their room, the handling quote, the dues and the calls. |
| 198 | 12 | **type** `public record PortsPage(List<Berth> kinds, int berths, double capacity, double tonnes, double handlingBille...` | The Ports page: berths by kind, the month's handling billed and dues paid, the dial and the calls. |
| 208 | 1 | `public Berth of(Ports.Cargo kind)` _(in FreightView.PortsPage)_ | The row of one kind. |
| 212 | 1 | **type** `public record Shipper(String sector, double haulage, double rail, double handling, double total)` | A shipper billed in the month: what each division charged it (Haulage's, the railway's, the quay's handling) and the sum. |
| 215 | 1 | **type** `public record View(Legs legs, HaulagePage haulage, RailPage rail, PortsPage ports, List<Shipper> shippers, ...` | The whole of it: the legs, the three pages, the shippers billed (largest bill first) and the three divisions' bills together. |

### THE VIEW (lines 220-342)

| line | len | member | says |
|---:|---:|---|---|
| 225 | 10 | `public static View of(Game game)` | The month billed, from the game's Freight sector, its ports and its buildings. |
| 237 | 15 | `static Legs legs(Freight fr, Ports ports)` | The legs: the boundary's tonnes by mode (the lorries' are what the railway and the ships did not take), the city leg's by the trucks and the sidings. |
| 253 | 3 | `private static Row row(Leg leg, Mode mode, double tonnes)` |  |
| 257 | 13 | `static HaulagePage haulagePage(Freight fr, BuildingManager buildings)` |  |
| 271 | 12 | `static RailPage railPage(Freight fr, Ports ports)` |  |
| 284 | 36 | `static PortsPage portsPage(Game game, Freight fr, Ports ports, BuildingManager buildings)` |  |
| 322 | 15 | `static List<Shipper> shippers(Game game, Freight fr)` | The shippers billed, largest bill first (the sector order breaking ties): each sector's three lines of the month, those with nothing billed left out. |
| 339 | 3 | `private static double fin(double v)` | A finite figure: NaN and infinity read 0, so no page ever draws or divides by one. |

