# Ports.java - 762 lines · 56 methods · 11 constants · model

`ham/citybuildersim/Ports.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> The city's ports: the berths its sea terminals stand, the share of each
> kind of cargo they take off the lorries and the railway, and the tonnes
> that went by sea in the month (0.7.86, batch O9; docs/studies/spec-oil.md
> 2.9, the research's 4.1-4.3 and Q9-Q10, runs/research-freight.md 5).
> 
> WHY. A coastal city traded by lorry and, once the investors laid track,
> by rail, and nothing it could build moved its freight more cheaply than
> the railway's floor. A ship does: the research's freight rule puts a tonne
> by sea at a tenth to a fifth of what a lorry charges over the same
> distance, door to door. The research's Q9 makes the terminals the city's
> (no investor would build one on its fees) and Q10 lets each good take the
> cheaper of sea and rail, so a port is what it is in reality: the city's
> way to narrow the freight wedge on its seaborne trade.
> 
> THE BERTHS. A terminal is one of one kind of cargo (Cargo), handling its
> tonnes a year (BuildingsTemplate.berthTonnesAYear()); a kind's berths are
> the terminals of that kind standing, a twelfth of their year a month.
> They are SHARED AMONG THE KIND'S GOODS BY THEIR BOUNDARY TONNES, so every
> good of a kind goes by sea in the same share, min(1, berths / the kind's
> tonnes) - last month's tonnes, as the railway reads them.
> 
> SL1 (2026-10-28; Jerus: "one port can sustain alot"): a terminal is now ONE
> LARGE MODERN REAL TERMINAL OF SEVERAL BERTHS, not one berth, because the
> default playtest stood 35 General Cargo Terminals at 544,000 people, all
> full, on a 450,000 t break-bulk berth against a trade of about 2.5 t a
> person a month. The Tanker Terminal handles 30 Mt a year (Westridge Marine
> Terminal, Trans Mountain, 3 Aframax berths, 630,000 bbl a day = 31 Mt,
> Kinder Morgan Canada 10-K FY2017; Maasvlakte Olie Terminal, Rotterdam,
> ~30 Mt; BTC Ceyhan, 2 VLCC berths, 50 Mt design, ~30 Mt in 2022), the Bulk
> Terminal 50 Mt (Abbot Point / NQXT, 2 Capesize berths, 50 Mt nameplate;
> NCIG Newcastle 66 Mt on 3 berths; Richards Bay Coal Terminal 91 Mt on 6),
> the Container Terminal 2.7M TEU, 24.3 Mt at TONNES_A_TEU (APM Terminals
> Maasvlakte II, opened 2015: 86 ha, a 1,000 m deep-sea quay, 8 STS cranes,
> 2.7 Mteu) and the General Cargo Terminal 2 Mt (PSA Breakbulk Antwerp,
> 1,319 m of quay, 25.9 ha, ~2 Mt a year of steel and metals; BLG
> Neustaedter Hafen, Bremen, ~1.4 Mt on 2.1 km). Every cost line moved by the
> same ratio, so the price and the port dues a tonne are as they were and
> only the step grows; the Bulk Berth (2.5 Mt) stays dry bulk's small
> terminal for the young city: the terminal ordered is the largest the kind's
> whole sea trade pays back (payingTerminal()), once the uncovered tonnes pay
> back some terminal at all. And a fast-growing city is ordered the COUNT its
> uncovered tonnes pay back, not one at a time (payingCount(), orderCount()).
> 
> SEA BEFORE RAIL, OR AFTER. A kind whose sea freight (SEA_FREIGHT_SHARE, of
> a lorry's) is under the railway's quote takes its share first and the
> railway hauls what it leaves; one whose sea freight is not takes only what
> the railway leaves. The railway's quote never falls under its floor of .30
> (sectors.Freight.RAIL_FLOOR) and no kind's sea freight reaches .30, so today
> the berths always go first - the other order is written and checked
> (PortCheck) for the day a constant moves.
> 
> THE BAND (sectors.Freight.haul() step 5). A good's band carries
> 
>     1 - rail - sea + sea x SEA_FREIGHT_SHARE        (factor())
> 
> of its lorry freight: the lorries' part and the ships', both paid abroad
> inside the band, so no money path is new; the railway still bills its own
> share at home. With no terminal the sea share is nothing and the factor is
> the railway's own 1 - rail, to the bit (PortCheck, InfrastructureCheck).
> 
> ... (16 more lines in the source)

**Uses:** [Good](Good.md) (17), [BuildingsTemplate](BuildingsTemplate.md) (15), [Traffic](Traffic.md) (4), [BuildingManager](BuildingManager.md) (4), [Refining](Refining.md) (2), [Freight](Freight.md) (1), [BuildingsStacks](BuildingsStacks.md) (1)

**Used by (48):** [AutoBuilder](AutoBuilder.md), [BoatSchedule](BoatSchedule.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [CityShore](CityShore.md), [CopperMiningCheck](CopperMiningCheck.md), [DataSave](DataSave.md), [ForestryCheck](ForestryCheck.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [Game](Game.md), [Good](Good.md), [HaulageCheck](HaulageCheck.md), [HomeRailCheck](HomeRailCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LandmassCheck](LandmassCheck.md), [LandmassView](LandmassView.md), [Landmasses](Landmasses.md), [LongPlaytest](LongPlaytest.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [Refining](Refining.md), [SaveFileCheck](SaveFileCheck.md), [ShipShapes](ShipShapes.md), [TradeBand](TradeBand.md), [TradeBandCheck](TradeBandCheck.md), [TrainSchedule](TrainSchedule.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 83 | THE FREIGHT RULE (runs/research-freight.md 5) |
| 196 | THE BERTHS, AND THE ROOM |
| 454 | IN FORCE: THE MONTH RUNNING |
| 533 | THE MONTH BILLED: WHAT CROSSED THE BOUNDARY, AND WHAT OF IT BY SEA |
| 605 | THE NEXT MONTH'S SHARES (sectors.Freight.haul() step 3) |
| 707 | THE SAVE (DataSave.portMonth) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 114 | `Ports.Cargo.LIQUID` |  |
| 115 | `Ports.Cargo.DRY_BULK` |  |
| 116 | `Ports.Cargo.CONTAINER` |  |
| 117 | `Ports.Cargo.GENERAL` |  |
| 151 | `Ports.Ship.MR` |  |
| 152 | `Ports.Ship.LR1` |  |
| 153 | `Ports.Ship.AFRAMAX` |  |
| 154 | `Ports.Ship.LR2` |  |
| 155 | `Ports.Ship.SUEZMAX` |  |
| 156 | `Ports.Ship.VLCC` |  |
| 157 | `Ports.Ship.HANDYSIZE` |  |
| 158 | `Ports.Ship.SUPRAMAX` |  |
| 159 | `Ports.Ship.PANAMAX` |  |
| 160 | `Ports.Ship.CAPESIZE` |  |
| 161 | `Ports.Ship.FEEDER` |  |
| 162 | `Ports.Ship.GENERAL_CARGO` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 97 | `Ports.SEA_FREIGHT_SHARE_LIQUID` | `.08` | Liquid bulk by sea against a lorry, at 5,000 km: .08, the mean of the VLCC, Suezmax, Aframax-LR and MR rows of the rule. |
| 100 | `Ports.SEA_FREIGHT_SHARE_DRY_BULK` | `.07` | Dry bulk by sea against a lorry, at 5,000 km: .07, the mean of the Capesize and Panamax rows. |
| 103 | `Ports.SEA_FREIGHT_SHARE_CONTAINER` | `.16` | Containers by sea against a lorry, at 5,000 km: .16, the deep-sea box row. |
| 106 | `Ports.SEA_FREIGHT_SHARE_GENERAL` | `.20` | General cargo by sea against a lorry, at 5,000 km: .20, the 5,500 t ship's row. |
| 134 | `Ports.TONNES_A_TEU` | `9` | A loaded TEU's cargo, in tonnes: 9 (the research's 8-10 [P43][P44][P45]; spec-oil 2.9). |
| 137 | `Ports.FEEDER_TEU` | `3_000` | A feeder's boxes: 3,000 TEU, the top of [P43]'s feeder class ("under 3,000"), est. |
| 189 | `Ports.WORTH_A_BERTH` | `ham.citybuildersim.sectors.Freight.MIN_LINE_UTILISATION` | The share of a berth's month the uncovered tonnes of its kind must reach before the test player orders one (spec-oil 5's O9 row): the railway's own line, Freight.MIN_LINE_UTILISATION. |
| 192 | `Ports.KINDS` | `Cargo.values().length` | The kinds of cargo a terminal takes, the length of every by-kind array here. |
| 194 | `Ports.STREAMS` | `Traffic.values().length` | The streams of traffic (Traffic), the length of every by-stream array here. |
| 381 | `Ports.ROOM_TOLERANCE` | `1e-9` | How near a cargo may come to the free room and still fit: a billionth, a store's rounding (the refiners keep exactly an MR's cargo free since 0.7.105, sectors.Refining.CARGO_KEPT_FREE). |
| 389 | `Ports.BULK_TERMINAL_SHIPS` | `java.util.List.of(Ship.PANAMAX, Ship.CAPESIZE)` | The classes the Bulk Terminal takes, and the dry bulk fleet while no dry bulk berth stands: Panamax and Capesize (FR3 call 6, docs/queue/FR3.md 2; the rule before FR3). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 119 | `private final String label` |  |
| 120 | `private final double seaFreightShare` |  |
| 164 | `private final Cargo cargo` |  |
| 165 | `private final String label` |  |
| 166 | `private final double tonnes` |  |
| 463 | `private final double[] share` | Each kind's share of its goods' tonnes at sea (or, for a kind after the railway, of what the railway leaves). |
| 466 | `private final boolean[] first` | Whether each kind goes by sea before the railway. |
| 469 | `private Ship crudeShip` | The class crude goes in; null while no tanker berth stands or the refiners' tanks have no room for one. |
| 472 | `private final double[] firstOfStream` | The share of each stream's tonnes the berths take before the railway, and all the berths take. |
| 473 | `private final double[] seaOfStream` |  |
| 476 | `private double heldBack` | Crude tonnes a month a tanker berth would carry if the refiners had room for a cargo: held back for room. |
| 542 | `private final double[] seaIn` |  |
| 543 | `private final double[] tradeIn` |  |
| 544 | `private double crudeSeaIn, crudeSeaOut, crudeTrade` |  |
| 547 | `private Ship billedCrudeShip` | The class the month billed's crude went in. |
| 616 | `private final double[] nextShare` |  |
| 617 | `private final boolean[] nextFirst` |  |
| 618 | `private Ship nextCrudeShip` |  |
| 619 | `private double nextHeld` |  |
| 713 | `public double[] share, firstOfStream, seaOfStream, seaIn, seaOut, tradeIn, tradeOut` |  |
| 714 | `public boolean[] first` |  |
| 715 | `public String crudeShip, billedCrudeShip` |  |
| 716 | `public double heldBack, crudeSeaIn, crudeSeaOut, crudeTrade` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 81 | 682 | **type** `public final class Ports` | The city's ports: the berths its sea terminals stand, the share of each kind of cargo they take off the lorries and the railway, and the tonnes that went by sea in the month (0.7.86, batch O9; docs/studies/spec-oil.md... |

### THE FREIGHT RULE (runs/research-freight.md 5) (lines 83-195)

| line | len | member | says |
|---:|---:|---|---|
| 113 | 19 | **type** `public enum Cargo` | The kind of cargo a ship carries and a berth handles (spec-oil 2.1, Good.cargo()): LIQUID in tankers, DRY_BULK in bulk carriers, CONTAINER in box ships, GENERAL in general cargo ships. |
| 122 | 4 | `Cargo(String label, double seaFreightShare)` _(in Ports.Cargo)_ |  |
| 127 | 1 | `public String label()` _(in Ports.Cargo)_ |  |
| 130 | 1 | `public double seaFreightShare()` _(in Ports.Cargo)_ | What a tonne of it costs by sea, as a share of what a lorry charges (SEA_FREIGHT_SHARE_*). |
| 150 | 37 | **type** `public enum Ship` | The ships, by the cargo one carries [P30][P40][P42][P46] (spec-oil 2.9): six tankers, four bulk carriers, a feeder and a general cargo ship, each kind's smallest first. |
| 168 | 5 | `Ship(Cargo cargo, String label, double tonnes)` _(in Ports.Ship)_ |  |
| 174 | 1 | `public Cargo cargo()` _(in Ports.Ship)_ |  |
| 175 | 1 | `public String label()` _(in Ports.Ship)_ |  |
| 178 | 1 | `public double tonnes()` _(in Ports.Ship)_ | Its cargo a voyage, in tonnes. |
| 181 | 5 | `public static Ship byName(String name)` _(in Ports.Ship)_ | The one of this name, or null - a save written by a build without it loses the class, not the load. |

### THE BERTHS, AND THE ROOM (lines 196-453)

| line | len | member | says |
|---:|---:|---|---|
| 201 | 1 | `public static boolean isPort(BuildingsTemplate t)` | Whether a building is a sea terminal: a PORTS building with a berth (BuildingsTemplate.isPort()). |
| 204 | 1 | `public static double berthMonth(BuildingsTemplate t)` | Tonnes a month one terminal's berth handles: its year over twelve. |
| 207 | 10 | `public static double[] berths(BuildingManager buildings)` | Tonnes a month the terminals standing handle, by kind (Cargo order). |
| 219 | 10 | `public static int[] berthCounts(BuildingManager buildings)` | How many terminals stand, by kind (Cargo order): the count behind berths()' tonnes, for the Freight screens (FR4, FreightView). |
| 231 | 9 | `public static double[] berthsOnSite(BuildingManager buildings)` | ...and the terminals on site, by kind, for anyone's order. |
| 261 | 3 | `public static boolean berthPaysBack(double uncovered, double berthMonth, double dial)` | Whether a berth PAYS BACK (FR3, docs/queue/FR3.md 1 and 3; the finding of spec-freight-and-infrastructure 9.5, "ports earn nothing and are never advised"): the kind's uncovered tonnes a month - what could go by sea an... |
| 286 | 10 | `public static int payingCount(double uncovered, double berthMonth, double dial)` | How many of a terminal the uncovered tonnes pay back (SL1, 2026-10-28): each counted after the ones before it, so the largest n with |
| 303 | 5 | `public double uncovered(Cargo k, double[] standing, double[] onSite)` | A kind's tonnes a month that could go by sea and no berth takes (FR3): carriable() less the kind's berths standing and on site (berths() and berthsOnSite(), by Cargo order), never below nothing - what berthPaysBack() ... |
| 330 | 18 | `public static BuildingsTemplate payingTerminal(Cargo k, Iterable<BuildingsTemplate> catalogue, double uncovered, double carriab...` | The terminal of a kind to order (FR3, docs/queue/FR3.md 5.4; SL1, 2026-10-28, the size by the kind's whole trade): null with no NEED - no terminal of the kind but those in `skip` that berthPaysBack() on the `uncovered... |
| 355 | 3 | `public static int orderCount(BuildingsTemplate terminal, double uncovered, double dial)` | How many of the terminal payingTerminal() chose to order (SL1): the count the uncovered tonnes pay back (payingCount()), and at least one - the one big terminal chosen on the kind's whole trade where its own break-eve... |
| 364 | 6 | `public static double freeCrudeRoom(ham.citybuildersim.sectors.Refining refiners)` | Tonnes of crude a tanker could land in the refiners' tanks: their Tank Farms' room (sectors.Refining.tankFarmRoom(), litres at a tonne of crude's) less the crude on hand, never below nothing. |
| 372 | 7 | `public static Ship crudeShipFor(double room)` | The largest tanker whose cargo fits this room (spec-oil 2.9), or null under an MR's: no seaborne crude. |
| 392 | 7 | `public static java.util.List<Ship> shipsOf(BuildingsTemplate t)` | The classes a terminal takes (FR3): its own list, else every class of its cargo's kind; none for a building that is not a berth. |
| 408 | 10 | `public static java.util.List<Ship> bulkShipsStanding(BuildingManager buildings)` | The classes the month's dry bulk calls are made in (FR3 call 6): those of the LARGEST dry bulk berth standing, by its tonnes a year, so a Bulk Terminal beside Bulk Berths draws Panamax and Capesize; with none standing... |
| 420 | 3 | `public static Ship bulkShipFor(double tonnes)` | The bulk carrier a month's tonnes one way go in, with the Bulk Terminal's classes: a Capesize when they fill one, else a Panamax (star). |
| 430 | 9 | `public static Ship bulkShipFor(double tonnes, java.util.List<Ship> allowed)` | ...in these classes (FR3 call 6): the largest whose cargo is at most the tonnes, else the smallest of them. |
| 445 | 3 | `public static double factor(double rail, double sea, double seaFreightShare)` | The share of a good's lorry freight its band still carries, with these shares by rail and by sea: 1 - rail - sea + sea x seaFreightShare. |
| 450 | 3 | `public static double seaFactor(double sea, double seaFreightShare)` | ...the ships' part of that factor (FR4): what Trade's band in legs draws as the sea leg (TradeBand). |

### IN FORCE: THE MONTH RUNNING (lines 454-532)

| line | len | member | says |
|---:|---:|---|---|
| 479 | 1 | `public double share(Cargo k)` | Each kind's share in force. |
| 482 | 1 | `public boolean seaFirst(Cargo k)` | Whether a kind goes by sea before the railway, in force. |
| 485 | 1 | `public Ship crudeShip()` | The class crude goes in, in force; null when none goes by sea. |
| 488 | 1 | `public double getHeldBack()` | Crude tonnes a month held back for room (spec-oil 2.9): what a tanker berth standing would carry if the refiners' tanks had room for a cargo. |
| 491 | 4 | `public boolean anyAtSea()` | True while anything goes by sea. |
| 497 | 4 | `public static double seaFreightShareOf(Good g)` | What a tonne of this good costs by sea, of a lorry's; 0 for a good no ship carries. |
| 503 | 7 | `public double seaShareOf(Good g, double carried)` | The share of a good's tonnes at sea, with the railway carrying `carried` of its stream's remainder. |
| 516 | 6 | `public double railShareOf(Good g, double carried)` | The share of a good's tonnes on the railway: `carried` - its share of its stream's tonnes the berths before it leave - of what the berths do not take first. |
| 524 | 5 | `public double[] railOfStream(double[] carried)` | The railway's share of each stream's tonnes, for the road: its share of the remainder (Freight.getCarried()) of what the berths do not take first. |
| 531 | 1 | `public double[] seaOfStream()` | ...and the berths' share of each stream's tonnes. |

### THE MONTH BILLED: WHAT CROSSED THE BOUNDARY, AND WHAT OF IT BY SEA (lines 533-604)

| line | len | member | says |
|---:|---:|---|---|
| 550 | 8 | `public void beginMonth()` | Starts the month's tally, at the class in force while it moved. |
| 560 | 14 | `public void tally(Good g, double tonnesIn, double tonnesOut, double sea)` | One owner's tonnes of a good each way across the boundary, `sea` of them by ship. |
| 576 | 1 | `public double seaIn(Cargo k)` | Tonnes of a kind landed by sea, the month billed. |
| 578 | 1 | `public double seaOut(Cargo k)` | ...and shipped by sea. |
| 580 | 1 | `public double tradeIn(Cargo k)` | Tonnes of a kind that crossed the boundary inbound, by any mode, the month billed. |
| 582 | 1 | `public double tradeOut(Cargo k)` | ...and outbound. |
| 584 | 1 | `public double crudeSeaIn()` | Crude's own part of liquid bulk's sea tonnes: landed, and shipped. |
| 585 | 1 | `public double crudeSeaOut()` |  |
| 587 | 1 | `public double crudeTrade()` | Crude's tonnes across the boundary, both ways, by any mode. |
| 589 | 1 | `public Ship billedCrudeShip()` | The class the month billed's crude went in, or null. |
| 592 | 5 | `public double carriable(Cargo k)` | Tonnes of a kind that could go by sea, both ways, the month billed: crude left out while held back for room. |
| 599 | 5 | `public double seaTonnes()` | All sea tonnes, both ways, the month billed. |

### THE NEXT MONTH'S SHARES (sectors.Freight.haul() step 3) (lines 605-706)

| line | len | member | says |
|---:|---:|---|---|
| 626 | 28 | `public double[][] planFirst(double[] tonnes, double[] lorry, double quote, double[] berths, double freeCrudeRoom)` | The berths' shares for next month of the kinds that go before the railway, at the railway's quote in force. |
| 656 | 8 | `public void planAfter(double[] tonnes, double[] next)` | The berths' shares for next month of the kinds that go after the railway: of what it leaves, its share of each stream's remainder `next`. |
| 666 | 22 | `public void commit(double[] tonnes, double[] streamTonnes, double[] next)` | Puts next month's shares in force, and the road's two shares of each stream with them (`streamTonnes`, the railway's by stream). |
| 690 | 16 | `private double[] kindTonnes(double[] tonnes, double[] next)` | A month's tonnes by kind; with `next`, only what the railway leaves of each good's stream, and crude out while next month holds it back. |

### THE SAVE (DataSave.portMonth) (lines 707-762)

| line | len | member | says |
|---:|---:|---|---|
| 712 | 6 | **type** `public static final class State` | The shares in force and the month billed, as the save holds them. |
| 719 | 18 | `public State toState()` |  |
| 739 | 17 | `public void restore(State s)` | A save's ports; null - a save from before 0.7.86 - none: nothing at sea, which is what that city had. |
| 757 | 3 | `private static void copy(double[] from, double[] to)` |  |
| 761 | 1 | `private static double finite(double v)` |  |

