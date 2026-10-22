# FreightView.java - 470 lines · 23 methods · 1 constants · model

`ham/citybuildersim/FreightView.java` - generated 2026-10-22 by CodeMap; line numbers are as of that run.

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
> WHAT IS LEFT OUT, because the model keeps no such figure: the vans' tonnes
> (the "distribution" leg: a van's load is a nameplate fleet size,
> Sector.vansNeeded(), not a month's flow), Haulage's rule floor (its running
> cost is a local of the reprice), a history of the trucks against the need,
> and the month's calls by ship class (BoatSchedule keeps them by kind).
> 
> THE WORKS' STRIP (tonnesOf(), plantOf(); board 3, "How its tonnes move").
> The legs above are the city's. A works' page draws its own: each good it
> moved, by way (landed, shipped, bought in the city) and by mode on the long
> haul and the city leg, the month's bill by division and what a siding saves.
> The tonnes are Haulage's tally (Haulage.movesOf(), struck in Freight.haul()
> at the shares in force while they moved and saved with the extras): the
> sectors' own units are cleared at the strike and hold the NEXT month's trade
> when a screen asks, so working them out here would draw one month's tonnes
> beside another month's bill. The bill is the statement's own lines. The
> siding's saving is a counterfactual and says so: the share of a tonne's city
> leg a siding carries (InfrastructureManager.RAIL_ROAD_RELIEF) of the tonnes
> the trucks carried, at Haulage's quote in force.
> 
> Pure: it reads the city and changes nothing.

**Uses:** [Ports](Ports.md) (11), [Haulage](Haulage.md) (10), [Freight](Freight.md) (9), [PortDivision](PortDivision.md) (6), [Game](Game.md) (5), [BuildingManager](BuildingManager.md) (3), [InfrastructureManager](InfrastructureManager.md) (3), [Good](Good.md) (3), [Sector](Sector.md) (2), [Traffic](Traffic.md) (2), [BoatSchedule](BoatSchedule.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (5):** [FreightViewCheck](FreightViewCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [SectorScreen](SectorScreen.md), [TradeBandCheck](TradeBandCheck.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 63 | THE VOCABULARY |
| 119 | THE RECORDS |
| 230 | THE VIEW |
| 348 | THE WORKS' STRIP: HOW ITS TONNES MOVE (board 3) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 69 | `FreightView.Division.ALL` |  |
| 69 | `FreightView.Division.HAULAGE` |  |
| 69 | `FreightView.Division.RAIL` |  |
| 69 | `FreightView.Division.PORTS` |  |
| 80 | `FreightView.Leg.LONG_HAUL` |  |
| 80 | `FreightView.Leg.CITY_LEG` |  |
| 91 | `FreightView.Mode.WORLD_LORRY` |  |
| 92 | `FreightView.Mode.RAIL` |  |
| 93 | `FreightView.Mode.SEA` |  |
| 94 | `FreightView.Mode.TRUCK` |  |
| 95 | `FreightView.Mode.SIDING` |  |
| 117 | `FreightView.Unit.PER_TONNE` |  |
| 117 | `FreightView.Unit.SHARE_OF_LORRY` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 228 | `FreightView.RAIL_BILLED_AS` | `"Rail freight"` | The line the railway's bill is raised under on a shipper's input rows (Freight.haul()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 71 | `private final String label` |  |
| 82 | `private final String label` |  |
| 97 | `private final String label` |  |
| 98 | `private final boolean abroad` |  |
| 99 | `private final Division door` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 412 | **type** `public final class FreightView` | The Freight sector's divisions as the screens draw them (batch FR4; docs/studies/spec-freight-and-infrastructure.md 6): the city's freight this month in legs, and the Haulage, Rail and Ports pages with each division's... |
| 61 | 1 | `private FreightView()` |  |

### THE VOCABULARY (lines 63-118)

| line | len | member | says |
|---:|---:|---|---|
| 68 | 9 | **type** `public enum Division` | The division strip under the page chips: All, then the three divisions. |
| 73 | 1 | `Division(String label)` _(in FreightView.Division)_ |  |
| 75 | 1 | `public String label()` _(in FreightView.Division)_ |  |
| 79 | 9 | **type** `public enum Leg` | The two legs of the city's freight the model counts: what crossed the boundary, and the trucks' and sidings' leg inside the city. |
| 84 | 1 | `Leg(String label)` _(in FreightView.Leg)_ |  |
| 86 | 1 | `public String label()` _(in FreightView.Leg)_ |  |
| 90 | 25 | **type** `public enum Mode` | How a leg's tonnes moved: a long haul by the world's lorries, the railway or the ships; a city leg by Haulage's trucks or the railway's sidings. |
| 101 | 5 | `Mode(String label, boolean abroad, Division door)` _(in FreightView.Mode)_ |  |
| 107 | 1 | `public String label()` _(in FreightView.Mode)_ |  |
| 110 | 1 | `public boolean abroad()` _(in FreightView.Mode)_ | Whether the tonnes are paid abroad (the study's hatching): the world's lorries and the ships. |
| 113 | 1 | `public Division door()` _(in FreightView.Mode)_ | The division a bar of this mode opens; ALL for a bar with no door (the world's lorries are nobody's here). |
| 117 | 1 | **type** `public enum Unit` | How a quote is stated: a price a tonne in the city's money, or a share of what a lorry charges. |

### THE RECORDS (lines 119-229)

| line | len | member | says |
|---:|---:|---|---|
| 124 | 1 | **type** `public record Row(Leg leg, Mode mode, double tonnes, boolean abroad, Division door)` | One bar of the legs chart: a mode's tonnes on a leg, paid abroad or not, and the division it opens. |
| 127 | 8 | **type** `public record Legs(double longHaul, double cityLeg, List<Row> rows)` | The city's freight this month in legs: the long haul's tonnes (everything that crossed), the city leg's, and the five bars in leg order (world lorry, rail, sea; truck, siding). |
| 129 | 5 | `public List<Row> of(Leg leg)` _(in FreightView.Legs)_ | The bars of one leg, in order. |
| 137 | 1 | **type** `public record Quote(Unit unit, double quote, boolean known, OptionalDouble floor, OptionalDouble target)` | A division's price: in force now, whether a month has struck it, and the rule's floor and target where the model keeps them (empty where it does not). |
| 140 | 20 | **type** `public record HaulagePage(double trucks, double needed, double asked, double shortBy, int depots, double de...` | Haulage's page: the trucks against the city leg's need, the depots, the month's carriage, the quote and its bill. |
| 162 | 27 | **type** `public record RailPage(double sets, double setsNeeded, double track, double stations, double sidings, doubl...` | Rail's page: the wagon sets and the track against the freight offered, FR6's own-bulk tonnes and bill, the quote and its bill. |
| 191 | 15 | **type** `public record Berth(Ports.Cargo kind, int berths, double capacity, double onSite, double tonnes, double use...` | One kind of berth on the Ports page: the terminals standing, the tonnes against their room, the handling quote, the dues and the calls. |
| 208 | 12 | **type** `public record PortsPage(List<Berth> kinds, int berths, double capacity, double tonnes, double handlingBille...` | The Ports page: berths by kind, the month's handling billed and dues paid, the dial and the calls. |
| 218 | 1 | `public Berth of(Ports.Cargo kind)` _(in FreightView.PortsPage)_ | The row of one kind. |
| 222 | 1 | **type** `public record Shipper(String sector, double haulage, double rail, double handling, double total)` | A shipper billed in the month: what each division charged it (Haulage's, the railway's, the quay's handling) and the sum. |
| 225 | 1 | **type** `public record View(Legs legs, HaulagePage haulage, RailPage rail, PortsPage ports, List<Shipper> shippers, ...` | The whole of it: the legs, the three pages, the shippers billed (largest bill first) and the three divisions' bills together. |

### THE VIEW (lines 230-347)

| line | len | member | says |
|---:|---:|---|---|
| 235 | 10 | `public static View of(Game game)` | The month billed, from the game's Freight sector, its ports and its buildings. |
| 247 | 15 | `static Legs legs(Freight fr, Ports ports)` | The legs: the boundary's tonnes by mode (the lorries' are what the railway and the ships did not take), the city leg's by the trucks and the sidings. |
| 263 | 3 | `private static Row row(Leg leg, Mode mode, double tonnes)` |  |
| 267 | 13 | `static HaulagePage haulagePage(Freight fr, BuildingManager buildings)` |  |
| 281 | 12 | `static RailPage railPage(Freight fr, Ports ports)` |  |
| 294 | 36 | `static PortsPage portsPage(Game game, Freight fr, Ports ports, BuildingManager buildings)` |  |
| 332 | 15 | `static List<Shipper> shippers(Game game, Freight fr)` | The shippers billed, largest bill first (the sector order breaking ties): each sector's three lines of the month, those with nothing billed left out. |

### THE WORKS' STRIP: HOW ITS TONNES MOVE (board 3) (lines 348-470)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 26 | **type** `public record Tonnes(String sector, boolean moved, List<Haulage.Move> lines, double crossed, double rail, d...` | One sector's tonnes in the month billed, as its Operations page draws them under the flow card. |
| 387 | 4 | `private static Tonnes none(String sector, double quote, double homeShare, double standing)` | The all-zero strip: no sector, Freight itself (it bills, it is not billed), or a sector that moved nothing. |
| 396 | 30 | `public static Tonnes tonnesOf(Game game, Sector sector)` | A sector's tonnes of the month billed: the tally Haulage struck at the haul, the statement's freight lines and the siding's saving. |
| 428 | 1 | **type** `public record PlantLine(Good good, boolean made, double tonnes)` | One BULK good a building makes or uses, tonnes a month at nameplate. |
| 431 | 1 | **type** `public record Plan(double truckTonnes, double sidingTonnes, double trucks, double drivers, double haulage)` | One way of moving a building's tonnes: those the trucks carry and the siding does, the trucks and drivers that takes (Haulage.TONNES_A_TRUCK_MONTH, DRIVERS_A_TRUCK), and a month of haulage at the quote. |
| 439 | 2 | **type** `public record Plant(String building, List<PlantLine> lines, double tonnes, double quote, double relief, Pla...` | A building's city legs at nameplate, before and after a siding (spec 3.6, the Pelletising Plant): every tonne of the BULK goods it makes and uses (Freight.bulkTonnesOf()) a city leg, carried all by truck, or with a si... |
| 443 | 17 | `public static Plant plantOf(Game game, BuildingsTemplate t)` | A building's legs at nameplate, at Haulage's quote in force (the estimate before a month struck one): all by truck, and with a siding. |
| 461 | 4 | `private static Plan plan(double truckTonnes, double sidingTonnes, double quote)` |  |
| 467 | 3 | `private static double fin(double v)` | A finite figure: NaN and infinity read 0, so no page ever draws or divides by one. |

