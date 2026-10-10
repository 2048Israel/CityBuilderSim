# Ports.java - 539 lines · 46 methods · 9 constants · model

`ham/citybuildersim/Ports.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The city's ports: the berths its sea terminals stand, the share of each
> kind of cargo they take off the lorries and the railway, and the tonnes
> that went by sea in the month (0.7.86, batch O9; runs/spec-oil.md 2.9, the
> research's 4.1-4.3 and Q9-Q10, runs/research-freight.md 5).
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
> (sectors.Rail.RAIL_FLOOR) and no kind's sea freight reaches .30, so today
> the berths always go first - the other order is written and checked
> (PortCheck) for the day a constant moves.
> 
> THE BAND (sectors.Rail.haul() step 5). A good's band carries
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
> lorries, HELD BACK FOR ROOM, and Refining's page says how much. The other
> liquids go in MR product tankers, a depot's parcel.
> 
> THE ROAD. What goes by sea is off the street as what goes by rail is, less
> the lorry to the quay (InfrastructureManager.RAIL_ROAD_RELIEF, the
> railway's relief, applied to both).
> 
> THE BOATS are BoatSchedule's, from the month's sea tonnes here, and are
> never saved. This holds the shares in force and the month's tonnes, saved
> under one key (DataSave.portMonth); it moves no money.

**Uses:** [Good](Good.md) (17), [Traffic](Traffic.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [BuildingManager](BuildingManager.md) (2), [Refining](Refining.md) (2), [Rail](Rail.md) (1), [BuildingsStacks](BuildingsStacks.md) (1)

**Used by (21):** [BoatSchedule](BoatSchedule.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [CityShore](CityShore.md), [DataSave](DataSave.md), [Game](Game.md), [Good](Good.md), [InfrastructureCheck](InfrastructureCheck.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [PortCheck](PortCheck.md), [Rail](Rail.md), [ReadPathCheck](ReadPathCheck.md), [Refining](Refining.md), [SaveFileCheck](SaveFileCheck.md), [ShipShapes](ShipShapes.md)

## Sections

| line | section |
|---:|---|
| 59 | THE FREIGHT RULE (runs/research-freight.md 5) |
| 163 | THE BERTHS, AND THE ROOM |
| 231 | IN FORCE: THE MONTH RUNNING |
| 310 | THE MONTH BILLED: WHAT CROSSED THE BOUNDARY, AND WHAT OF IT BY SEA |
| 382 | THE NEXT MONTH'S SHARES (sectors.Rail.haul() step 3) |
| 484 | THE SAVE (DataSave.portMonth) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 90 | `Ports.Cargo.LIQUID` |  |
| 91 | `Ports.Cargo.DRY_BULK` |  |
| 92 | `Ports.Cargo.CONTAINER` |  |
| 93 | `Ports.Cargo.GENERAL` |  |
| 122 | `Ports.Ship.MR` |  |
| 123 | `Ports.Ship.LR1` |  |
| 124 | `Ports.Ship.AFRAMAX` |  |
| 125 | `Ports.Ship.LR2` |  |
| 126 | `Ports.Ship.SUEZMAX` |  |
| 127 | `Ports.Ship.VLCC` |  |
| 128 | `Ports.Ship.PANAMAX` |  |
| 129 | `Ports.Ship.CAPESIZE` |  |
| 130 | `Ports.Ship.FEEDER` |  |
| 131 | `Ports.Ship.GENERAL_CARGO` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 73 | `Ports.SEA_FREIGHT_SHARE_LIQUID` | `.08` | Liquid bulk by sea against a lorry, at 5,000 km: .08, the mean of the VLCC, Suezmax, Aframax-LR and MR rows of the rule. |
| 76 | `Ports.SEA_FREIGHT_SHARE_DRY_BULK` | `.07` | Dry bulk by sea against a lorry, at 5,000 km: .07, the mean of the Capesize and Panamax rows. |
| 79 | `Ports.SEA_FREIGHT_SHARE_CONTAINER` | `.16` | Containers by sea against a lorry, at 5,000 km: .16, the deep-sea box row. |
| 82 | `Ports.SEA_FREIGHT_SHARE_GENERAL` | `.20` | General cargo by sea against a lorry, at 5,000 km: .20, the 5,500 t ship's row. |
| 110 | `Ports.TONNES_A_TEU` | `9` | A loaded TEU's cargo, in tonnes: 9 (the research's 8-10 [P43][P44][P45]; spec-oil 2.9). |
| 113 | `Ports.FEEDER_TEU` | `3_000` | A feeder's boxes: 3,000 TEU, the top of [P43]'s feeder class ("under 3,000"), est. |
| 158 | `Ports.WORTH_A_BERTH` | `ham.citybuildersim.sectors.Rail.MIN_LINE_UTILISATION` | The share of a berth's month the uncovered tonnes of its kind must reach before the test player orders one (spec-oil 5's O9 row): the railway's own line, Rail.MIN_LINE_UTILISATION. |
| 160 | `Ports.KINDS` | `Cargo.values().length` |  |
| 161 | `Ports.STREAMS` | `Traffic.values().length` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 95 | `private final String label` |  |
| 96 | `private final double seaFreightShare` |  |
| 133 | `private final Cargo cargo` |  |
| 134 | `private final String label` |  |
| 135 | `private final double tonnes` |  |
| 240 | `private final double[] share` | Each kind's share of its goods' tonnes at sea (or, for a kind after the railway, of what the railway leaves). |
| 243 | `private final boolean[] first` | Whether each kind goes by sea before the railway. |
| 246 | `private Ship crudeShip` | The class crude goes in; null while no tanker berth stands or the refiners' tanks have no room for one. |
| 249 | `private final double[] firstOfStream` | The share of each stream's tonnes the berths take before the railway, and all the berths take. |
| 250 | `private final double[] seaOfStream` |  |
| 253 | `private double heldBack` | Crude tonnes a month a tanker berth would carry if the refiners had room for a cargo: held back for room. |
| 319 | `private final double[] seaIn` |  |
| 320 | `private final double[] tradeIn` |  |
| 321 | `private double crudeSeaIn, crudeSeaOut, crudeTrade` |  |
| 324 | `private Ship billedCrudeShip` | The class the month billed's crude went in. |
| 393 | `private final double[] nextShare` |  |
| 394 | `private final boolean[] nextFirst` |  |
| 395 | `private Ship nextCrudeShip` |  |
| 396 | `private double nextHeld` |  |
| 490 | `public double[] share, firstOfStream, seaOfStream, seaIn, seaOut, tradeIn, tradeOut` |  |
| 491 | `public boolean[] first` |  |
| 492 | `public String crudeShip, billedCrudeShip` |  |
| 493 | `public double heldBack, crudeSeaIn, crudeSeaOut, crudeTrade` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 483 | **type** `public final class Ports` | The city's ports: the berths its sea terminals stand, the share of each kind of cargo they take off the lorries and the railway, and the tonnes that went by sea in the month (0.7.86, batch O9; runs/spec-oil.md 2.9, th... |

### THE FREIGHT RULE (runs/research-freight.md 5) (lines 59-162)

| line | len | member | says |
|---:|---:|---|---|
| 89 | 19 | **type** `public enum Cargo` | The kind of cargo a ship carries and a berth handles (spec-oil 2.1, Good.cargo()): LIQUID in tankers, DRY_BULK in bulk carriers, CONTAINER in box ships, GENERAL in general cargo ships. |
| 98 | 4 | `Cargo(String label, double seaFreightShare)` _(in Ports.Cargo)_ |  |
| 103 | 1 | `public String label()` _(in Ports.Cargo)_ |  |
| 106 | 1 | `public double seaFreightShare()` _(in Ports.Cargo)_ | What a tonne of it costs by sea, as a share of what a lorry charges (SEA_FREIGHT_SHARE_*). |
| 121 | 35 | **type** `public enum Ship` | The ships, by the cargo one carries [P30][P40][P42][P46] (spec-oil 2.9): six tankers, two bulk carriers, a feeder and a general cargo ship, each kind's smallest first. |
| 137 | 5 | `Ship(Cargo cargo, String label, double tonnes)` _(in Ports.Ship)_ |  |
| 143 | 1 | `public Cargo cargo()` _(in Ports.Ship)_ |  |
| 144 | 1 | `public String label()` _(in Ports.Ship)_ |  |
| 147 | 1 | `public double tonnes()` _(in Ports.Ship)_ | Its cargo a voyage, in tonnes. |
| 150 | 5 | `public static Ship byName(String name)` _(in Ports.Ship)_ | The one of this name, or null - a save written by a build without it loses the class, not the load. |

### THE BERTHS, AND THE ROOM (lines 163-230)

| line | len | member | says |
|---:|---:|---|---|
| 168 | 1 | `public static boolean isPort(BuildingsTemplate t)` | Whether a building is a sea terminal: a PORTS building with a berth (BuildingsTemplate.isPort()). |
| 171 | 1 | `public static double berthMonth(BuildingsTemplate t)` | Tonnes a month one terminal's berth handles: its year over twelve. |
| 174 | 10 | `public static double[] berths(BuildingManager buildings)` | Tonnes a month the terminals standing handle, by kind (Cargo order). |
| 186 | 9 | `public static double[] berthsOnSite(BuildingManager buildings)` | ...and the terminals on site, by kind, for anyone's order. |
| 201 | 6 | `public static double freeCrudeRoom(ham.citybuildersim.sectors.Refining refiners)` | Tonnes of crude a tanker could land in the refiners' tanks: their Tank Farms' room (sectors.Refining.tankFarmRoom(), litres at a tonne of crude's) less the crude on hand, never below nothing. |
| 209 | 7 | `public static Ship crudeShipFor(double room)` | The largest tanker whose cargo fits this room (spec-oil 2.9), or null under an MR's: no seaborne crude. |
| 218 | 3 | `public static Ship bulkShipFor(double tonnes)` | The bulk carrier a month's tonnes one way go in: a Capesize when they fill one, else a Panamax (star). |
| 227 | 3 | `public static double factor(double rail, double sea, double seaFreightShare)` | The share of a good's lorry freight its band still carries, with these shares by rail and by sea: 1 - rail - sea + sea x seaFreightShare. |

### IN FORCE: THE MONTH RUNNING (lines 231-309)

| line | len | member | says |
|---:|---:|---|---|
| 256 | 1 | `public double share(Cargo k)` | Each kind's share in force. |
| 259 | 1 | `public boolean seaFirst(Cargo k)` | Whether a kind goes by sea before the railway, in force. |
| 262 | 1 | `public Ship crudeShip()` | The class crude goes in, in force; null when none goes by sea. |
| 265 | 1 | `public double getHeldBack()` | Crude tonnes a month held back for room (spec-oil 2.9): what a tanker berth standing would carry if the refiners' tanks had room for a cargo. |
| 268 | 4 | `public boolean anyAtSea()` | True while anything goes by sea. |
| 274 | 4 | `public static double seaFreightShareOf(Good g)` | What a tonne of this good costs by sea, of a lorry's; 0 for a good no ship carries. |
| 280 | 7 | `public double seaShareOf(Good g, double carried)` | The share of a good's tonnes at sea, with the railway carrying `carried` of its stream's remainder. |
| 293 | 6 | `public double railShareOf(Good g, double carried)` | The share of a good's tonnes on the railway: `carried` - its share of its stream's tonnes the berths before it leave - of what the berths do not take first. |
| 301 | 5 | `public double[] railOfStream(double[] carried)` | The railway's share of each stream's tonnes, for the road: its share of the remainder (Rail.getCarried()) of what the berths do not take first. |
| 308 | 1 | `public double[] seaOfStream()` | ...and the berths' share of each stream's tonnes. |

### THE MONTH BILLED: WHAT CROSSED THE BOUNDARY, AND WHAT OF IT BY SEA (lines 310-381)

| line | len | member | says |
|---:|---:|---|---|
| 327 | 8 | `public void beginMonth()` | Starts the month's tally, at the class in force while it moved. |
| 337 | 14 | `public void tally(Good g, double tonnesIn, double tonnesOut, double sea)` | One owner's tonnes of a good each way across the boundary, `sea` of them by ship. |
| 353 | 1 | `public double seaIn(Cargo k)` | Tonnes of a kind landed by sea, the month billed. |
| 355 | 1 | `public double seaOut(Cargo k)` | ...and shipped by sea. |
| 357 | 1 | `public double tradeIn(Cargo k)` | Tonnes of a kind that crossed the boundary inbound, by any mode, the month billed. |
| 359 | 1 | `public double tradeOut(Cargo k)` | ...and outbound. |
| 361 | 1 | `public double crudeSeaIn()` | Crude's own part of liquid bulk's sea tonnes: landed, and shipped. |
| 362 | 1 | `public double crudeSeaOut()` |  |
| 364 | 1 | `public double crudeTrade()` | Crude's tonnes across the boundary, both ways, by any mode. |
| 366 | 1 | `public Ship billedCrudeShip()` | The class the month billed's crude went in, or null. |
| 369 | 5 | `public double carriable(Cargo k)` | Tonnes of a kind that could go by sea, both ways, the month billed: crude left out while held back for room. |
| 376 | 5 | `public double seaTonnes()` | All sea tonnes, both ways, the month billed. |

### THE NEXT MONTH'S SHARES (sectors.Rail.haul() step 3) (lines 382-483)

| line | len | member | says |
|---:|---:|---|---|
| 403 | 28 | `public double[][] planFirst(double[] tonnes, double[] lorry, double quote, double[] berths, double freeCrudeRoom)` | The berths' shares for next month of the kinds that go before the railway, at the railway's quote in force. |
| 433 | 8 | `public void planAfter(double[] tonnes, double[] next)` | The berths' shares for next month of the kinds that go after the railway: of what it leaves, its share of each stream's remainder `next`. |
| 443 | 22 | `public void commit(double[] tonnes, double[] streamTonnes, double[] next)` | Puts next month's shares in force, and the road's two shares of each stream with them (`streamTonnes`, the railway's by stream). |
| 467 | 16 | `private double[] kindTonnes(double[] tonnes, double[] next)` | A month's tonnes by kind; with `next`, only what the railway leaves of each good's stream, and crude out while next month holds it back. |

### THE SAVE (DataSave.portMonth) (lines 484-539)

| line | len | member | says |
|---:|---:|---|---|
| 489 | 6 | **type** `public static final class State` | The shares in force and the month billed, as the save holds them. |
| 496 | 18 | `public State toState()` |  |
| 516 | 17 | `public void restore(State s)` | A save's ports; null - a save from before 0.7.86 - none: nothing at sea, which is what that city had. |
| 534 | 3 | `private static void copy(double[] from, double[] to)` |  |
| 538 | 1 | `private static double finite(double v)` |  |

