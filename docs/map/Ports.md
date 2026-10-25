# Ports.java - 678 lines · 54 methods · 11 constants · model

`ham/citybuildersim/Ports.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

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
> THE BERTHS. A terminal is one berth of one kind of cargo (Cargo), handling
> its tonnes a year (BuildingsTemplate.berthTonnesAYear()); a kind's berths
> are the terminals of that kind standing, a twelfth of their year a month.
> They are SHARED AMONG THE KIND'S GOODS BY THEIR BOUNDARY TONNES, so every
> good of a kind goes by sea in the same share, min(1, berths / the kind's
> tonnes) - last month's tonnes, as the railway reads them.
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
> CRUDE NEEDS ROOM. A tanker lands its whole cargo at once and the refiners
> run it a month at a time, so crude goes by sea only while the refiners'
> Tank Farms have room free for at least an MR's cargo, in the largest class
> that fits (crudeShip()); without it crude stays on the railway and the
> lorries, HELD BACK FOR ROOM, and Refining's page says how much. Since
> 0.7.105 the refiners keep that room free (sectors.Refining.
> CARGO_KEPT_FREE): a farm no longer fills with their month. The other
> liquids go in MR product tankers, a depot's parcel.
> 
> THE ROAD. What goes by sea is off the street as what goes by rail is, less
> the lorry to the quay (InfrastructureManager.RAIL_ROAD_RELIEF, the
> railway's relief, applied to both).
> 
> THE BOATS are BoatSchedule's, from the month's sea tonnes here, and are
> never saved. This holds the shares in force and the month's tonnes, saved
> under one key (DataSave.portMonth); it moves no money.

**Uses:** [Good](Good.md) (17), [BuildingsTemplate](BuildingsTemplate.md) (13), [Traffic](Traffic.md) (4), [BuildingManager](BuildingManager.md) (4), [Refining](Refining.md) (2), [Freight](Freight.md) (1), [BuildingsStacks](BuildingsStacks.md) (1)

**Used by (45):** [AutoBuilder](AutoBuilder.md), [BoatSchedule](BoatSchedule.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [CityShore](CityShore.md), [CopperMiningCheck](CopperMiningCheck.md), [DataSave](DataSave.md), [ForestryCheck](ForestryCheck.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [Game](Game.md), [Good](Good.md), [HaulageCheck](HaulageCheck.md), [HomeRailCheck](HomeRailCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LongPlaytest](LongPlaytest.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [Refining](Refining.md), [SaveFileCheck](SaveFileCheck.md), [ShipShapes](ShipShapes.md), [TradeBand](TradeBand.md), [TradeBandCheck](TradeBandCheck.md), [TrainSchedule](TrainSchedule.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 61 | THE FREIGHT RULE (runs/research-freight.md 5) |
| 174 | THE BERTHS, AND THE ROOM |
| 370 | IN FORCE: THE MONTH RUNNING |
| 449 | THE MONTH BILLED: WHAT CROSSED THE BOUNDARY, AND WHAT OF IT BY SEA |
| 521 | THE NEXT MONTH'S SHARES (sectors.Freight.haul() step 3) |
| 623 | THE SAVE (DataSave.portMonth) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 92 | `Ports.Cargo.LIQUID` |  |
| 93 | `Ports.Cargo.DRY_BULK` |  |
| 94 | `Ports.Cargo.CONTAINER` |  |
| 95 | `Ports.Cargo.GENERAL` |  |
| 129 | `Ports.Ship.MR` |  |
| 130 | `Ports.Ship.LR1` |  |
| 131 | `Ports.Ship.AFRAMAX` |  |
| 132 | `Ports.Ship.LR2` |  |
| 133 | `Ports.Ship.SUEZMAX` |  |
| 134 | `Ports.Ship.VLCC` |  |
| 135 | `Ports.Ship.HANDYSIZE` |  |
| 136 | `Ports.Ship.SUPRAMAX` |  |
| 137 | `Ports.Ship.PANAMAX` |  |
| 138 | `Ports.Ship.CAPESIZE` |  |
| 139 | `Ports.Ship.FEEDER` |  |
| 140 | `Ports.Ship.GENERAL_CARGO` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 75 | `Ports.SEA_FREIGHT_SHARE_LIQUID` | `.08` | Liquid bulk by sea against a lorry, at 5,000 km: .08, the mean of the VLCC, Suezmax, Aframax-LR and MR rows of the rule. |
| 78 | `Ports.SEA_FREIGHT_SHARE_DRY_BULK` | `.07` | Dry bulk by sea against a lorry, at 5,000 km: .07, the mean of the Capesize and Panamax rows. |
| 81 | `Ports.SEA_FREIGHT_SHARE_CONTAINER` | `.16` | Containers by sea against a lorry, at 5,000 km: .16, the deep-sea box row. |
| 84 | `Ports.SEA_FREIGHT_SHARE_GENERAL` | `.20` | General cargo by sea against a lorry, at 5,000 km: .20, the 5,500 t ship's row. |
| 112 | `Ports.TONNES_A_TEU` | `9` | A loaded TEU's cargo, in tonnes: 9 (the research's 8-10 [P43][P44][P45]; spec-oil 2.9). |
| 115 | `Ports.FEEDER_TEU` | `3_000` | A feeder's boxes: 3,000 TEU, the top of [P43]'s feeder class ("under 3,000"), est. |
| 167 | `Ports.WORTH_A_BERTH` | `ham.citybuildersim.sectors.Freight.MIN_LINE_UTILISATION` | The share of a berth's month the uncovered tonnes of its kind must reach before the test player orders one (spec-oil 5's O9 row): the railway's own line, Freight.MIN_LINE_UTILISATION. |
| 170 | `Ports.KINDS` | `Cargo.values().length` | The kinds of cargo a terminal takes, the length of every by-kind array here. |
| 172 | `Ports.STREAMS` | `Traffic.values().length` | The streams of traffic (Traffic), the length of every by-stream array here. |
| 297 | `Ports.ROOM_TOLERANCE` | `1e-9` | How near a cargo may come to the free room and still fit: a billionth, a store's rounding (the refiners keep exactly an MR's cargo free since 0.7.105, sectors.Refining.CARGO_KEPT_FREE). |
| 305 | `Ports.BULK_TERMINAL_SHIPS` | `java.util.List.of(Ship.PANAMAX, Ship.CAPESIZE)` | The classes the Bulk Terminal takes, and the dry bulk fleet while no dry bulk berth stands: Panamax and Capesize (FR3 call 6, docs/queue/FR3.md 2; the rule before FR3). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 97 | `private final String label` |  |
| 98 | `private final double seaFreightShare` |  |
| 142 | `private final Cargo cargo` |  |
| 143 | `private final String label` |  |
| 144 | `private final double tonnes` |  |
| 379 | `private final double[] share` | Each kind's share of its goods' tonnes at sea (or, for a kind after the railway, of what the railway leaves). |
| 382 | `private final boolean[] first` | Whether each kind goes by sea before the railway. |
| 385 | `private Ship crudeShip` | The class crude goes in; null while no tanker berth stands or the refiners' tanks have no room for one. |
| 388 | `private final double[] firstOfStream` | The share of each stream's tonnes the berths take before the railway, and all the berths take. |
| 389 | `private final double[] seaOfStream` |  |
| 392 | `private double heldBack` | Crude tonnes a month a tanker berth would carry if the refiners had room for a cargo: held back for room. |
| 458 | `private final double[] seaIn` |  |
| 459 | `private final double[] tradeIn` |  |
| 460 | `private double crudeSeaIn, crudeSeaOut, crudeTrade` |  |
| 463 | `private Ship billedCrudeShip` | The class the month billed's crude went in. |
| 532 | `private final double[] nextShare` |  |
| 533 | `private final boolean[] nextFirst` |  |
| 534 | `private Ship nextCrudeShip` |  |
| 535 | `private double nextHeld` |  |
| 629 | `public double[] share, firstOfStream, seaOfStream, seaIn, seaOut, tradeIn, tradeOut` |  |
| 630 | `public boolean[] first` |  |
| 631 | `public String crudeShip, billedCrudeShip` |  |
| 632 | `public double heldBack, crudeSeaIn, crudeSeaOut, crudeTrade` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 620 | **type** `public final class Ports` | The city's ports: the berths its sea terminals stand, the share of each kind of cargo they take off the lorries and the railway, and the tonnes that went by sea in the month (0.7.86, batch O9; docs/studies/spec-oil.md... |

### THE FREIGHT RULE (runs/research-freight.md 5) (lines 61-173)

| line | len | member | says |
|---:|---:|---|---|
| 91 | 19 | **type** `public enum Cargo` | The kind of cargo a ship carries and a berth handles (spec-oil 2.1, Good.cargo()): LIQUID in tankers, DRY_BULK in bulk carriers, CONTAINER in box ships, GENERAL in general cargo ships. |
| 100 | 4 | `Cargo(String label, double seaFreightShare)` _(in Ports.Cargo)_ |  |
| 105 | 1 | `public String label()` _(in Ports.Cargo)_ |  |
| 108 | 1 | `public double seaFreightShare()` _(in Ports.Cargo)_ | What a tonne of it costs by sea, as a share of what a lorry charges (SEA_FREIGHT_SHARE_*). |
| 128 | 37 | **type** `public enum Ship` | The ships, by the cargo one carries [P30][P40][P42][P46] (spec-oil 2.9): six tankers, four bulk carriers, a feeder and a general cargo ship, each kind's smallest first. |
| 146 | 5 | `Ship(Cargo cargo, String label, double tonnes)` _(in Ports.Ship)_ |  |
| 152 | 1 | `public Cargo cargo()` _(in Ports.Ship)_ |  |
| 153 | 1 | `public String label()` _(in Ports.Ship)_ |  |
| 156 | 1 | `public double tonnes()` _(in Ports.Ship)_ | Its cargo a voyage, in tonnes. |
| 159 | 5 | `public static Ship byName(String name)` _(in Ports.Ship)_ | The one of this name, or null - a save written by a build without it loses the class, not the load. |

### THE BERTHS, AND THE ROOM (lines 174-369)

| line | len | member | says |
|---:|---:|---|---|
| 179 | 1 | `public static boolean isPort(BuildingsTemplate t)` | Whether a building is a sea terminal: a PORTS building with a berth (BuildingsTemplate.isPort()). |
| 182 | 1 | `public static double berthMonth(BuildingsTemplate t)` | Tonnes a month one terminal's berth handles: its year over twelve. |
| 185 | 10 | `public static double[] berths(BuildingManager buildings)` | Tonnes a month the terminals standing handle, by kind (Cargo order). |
| 197 | 10 | `public static int[] berthCounts(BuildingManager buildings)` | How many terminals stand, by kind (Cargo order): the count behind berths()' tonnes, for the Freight screens (FR4, FreightView). |
| 209 | 9 | `public static double[] berthsOnSite(BuildingManager buildings)` | ...and the terminals on site, by kind, for anyone's order. |
| 239 | 4 | `public static boolean berthPaysBack(double uncovered, double berthMonth, double dial)` | Whether a berth PAYS BACK (FR3, docs/queue/FR3.md 1 and 3; the finding of spec-freight-and-infrastructure 9.5, "ports earn nothing and are never advised"): the kind's uncovered tonnes a month - what could go by sea an... |
| 250 | 5 | `public double uncovered(Cargo k, double[] standing, double[] onSite)` | A kind's tonnes a month that could go by sea and no berth takes (FR3): carriable() less the kind's berths standing and on site (berths() and berthsOnSite(), by Cargo order), never below nothing - what berthPaysBack() ... |
| 264 | 10 | `public static BuildingsTemplate payingTerminal(Cargo k, Iterable<BuildingsTemplate> catalogue, double uncovered, double dial, j...` | The terminal of a kind to order (FR3, docs/queue/FR3.md 5.4): among the catalogue's terminals of the kind but those in `skip`, the LARGEST (by tonnes a year) that pays back (berthPaysBack()) - the Bulk Terminal when t... |
| 280 | 6 | `public static double freeCrudeRoom(ham.citybuildersim.sectors.Refining refiners)` | Tonnes of crude a tanker could land in the refiners' tanks: their Tank Farms' room (sectors.Refining.tankFarmRoom(), litres at a tonne of crude's) less the crude on hand, never below nothing. |
| 288 | 7 | `public static Ship crudeShipFor(double room)` | The largest tanker whose cargo fits this room (spec-oil 2.9), or null under an MR's: no seaborne crude. |
| 308 | 7 | `public static java.util.List<Ship> shipsOf(BuildingsTemplate t)` | The classes a terminal takes (FR3): its own list, else every class of its cargo's kind; none for a building that is not a berth. |
| 324 | 10 | `public static java.util.List<Ship> bulkShipsStanding(BuildingManager buildings)` | The classes the month's dry bulk calls are made in (FR3 call 6): those of the LARGEST dry bulk berth standing, by its tonnes a year, so a Bulk Terminal beside Bulk Berths draws Panamax and Capesize; with none standing... |
| 336 | 3 | `public static Ship bulkShipFor(double tonnes)` | The bulk carrier a month's tonnes one way go in, with the Bulk Terminal's classes: a Capesize when they fill one, else a Panamax (star). |
| 346 | 9 | `public static Ship bulkShipFor(double tonnes, java.util.List<Ship> allowed)` | ...in these classes (FR3 call 6): the largest whose cargo is at most the tonnes, else the smallest of them. |
| 361 | 3 | `public static double factor(double rail, double sea, double seaFreightShare)` | The share of a good's lorry freight its band still carries, with these shares by rail and by sea: 1 - rail - sea + sea x seaFreightShare. |
| 366 | 3 | `public static double seaFactor(double sea, double seaFreightShare)` | ...the ships' part of that factor (FR4): what Trade's band in legs draws as the sea leg (TradeBand). |

### IN FORCE: THE MONTH RUNNING (lines 370-448)

| line | len | member | says |
|---:|---:|---|---|
| 395 | 1 | `public double share(Cargo k)` | Each kind's share in force. |
| 398 | 1 | `public boolean seaFirst(Cargo k)` | Whether a kind goes by sea before the railway, in force. |
| 401 | 1 | `public Ship crudeShip()` | The class crude goes in, in force; null when none goes by sea. |
| 404 | 1 | `public double getHeldBack()` | Crude tonnes a month held back for room (spec-oil 2.9): what a tanker berth standing would carry if the refiners' tanks had room for a cargo. |
| 407 | 4 | `public boolean anyAtSea()` | True while anything goes by sea. |
| 413 | 4 | `public static double seaFreightShareOf(Good g)` | What a tonne of this good costs by sea, of a lorry's; 0 for a good no ship carries. |
| 419 | 7 | `public double seaShareOf(Good g, double carried)` | The share of a good's tonnes at sea, with the railway carrying `carried` of its stream's remainder. |
| 432 | 6 | `public double railShareOf(Good g, double carried)` | The share of a good's tonnes on the railway: `carried` - its share of its stream's tonnes the berths before it leave - of what the berths do not take first. |
| 440 | 5 | `public double[] railOfStream(double[] carried)` | The railway's share of each stream's tonnes, for the road: its share of the remainder (Freight.getCarried()) of what the berths do not take first. |
| 447 | 1 | `public double[] seaOfStream()` | ...and the berths' share of each stream's tonnes. |

### THE MONTH BILLED: WHAT CROSSED THE BOUNDARY, AND WHAT OF IT BY SEA (lines 449-520)

| line | len | member | says |
|---:|---:|---|---|
| 466 | 8 | `public void beginMonth()` | Starts the month's tally, at the class in force while it moved. |
| 476 | 14 | `public void tally(Good g, double tonnesIn, double tonnesOut, double sea)` | One owner's tonnes of a good each way across the boundary, `sea` of them by ship. |
| 492 | 1 | `public double seaIn(Cargo k)` | Tonnes of a kind landed by sea, the month billed. |
| 494 | 1 | `public double seaOut(Cargo k)` | ...and shipped by sea. |
| 496 | 1 | `public double tradeIn(Cargo k)` | Tonnes of a kind that crossed the boundary inbound, by any mode, the month billed. |
| 498 | 1 | `public double tradeOut(Cargo k)` | ...and outbound. |
| 500 | 1 | `public double crudeSeaIn()` | Crude's own part of liquid bulk's sea tonnes: landed, and shipped. |
| 501 | 1 | `public double crudeSeaOut()` |  |
| 503 | 1 | `public double crudeTrade()` | Crude's tonnes across the boundary, both ways, by any mode. |
| 505 | 1 | `public Ship billedCrudeShip()` | The class the month billed's crude went in, or null. |
| 508 | 5 | `public double carriable(Cargo k)` | Tonnes of a kind that could go by sea, both ways, the month billed: crude left out while held back for room. |
| 515 | 5 | `public double seaTonnes()` | All sea tonnes, both ways, the month billed. |

### THE NEXT MONTH'S SHARES (sectors.Freight.haul() step 3) (lines 521-622)

| line | len | member | says |
|---:|---:|---|---|
| 542 | 28 | `public double[][] planFirst(double[] tonnes, double[] lorry, double quote, double[] berths, double freeCrudeRoom)` | The berths' shares for next month of the kinds that go before the railway, at the railway's quote in force. |
| 572 | 8 | `public void planAfter(double[] tonnes, double[] next)` | The berths' shares for next month of the kinds that go after the railway: of what it leaves, its share of each stream's remainder `next`. |
| 582 | 22 | `public void commit(double[] tonnes, double[] streamTonnes, double[] next)` | Puts next month's shares in force, and the road's two shares of each stream with them (`streamTonnes`, the railway's by stream). |
| 606 | 16 | `private double[] kindTonnes(double[] tonnes, double[] next)` | A month's tonnes by kind; with `next`, only what the railway leaves of each good's stream, and crude out while next month holds it back. |

### THE SAVE (DataSave.portMonth) (lines 623-678)

| line | len | member | says |
|---:|---:|---|---|
| 628 | 6 | **type** `public static final class State` | The shares in force and the month billed, as the save holds them. |
| 635 | 18 | `public State toState()` |  |
| 655 | 17 | `public void restore(State s)` | A save's ports; null - a save from before 0.7.86 - none: nothing at sea, which is what that city had. |
| 673 | 3 | `private static void copy(double[] from, double[] to)` |  |
| 677 | 1 | `private static double finite(double v)` |  |

