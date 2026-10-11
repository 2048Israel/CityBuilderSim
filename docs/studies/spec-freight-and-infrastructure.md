# Freight, and the ground it runs on: a design study

*2026-10-11. A read-only design study: no code was changed. The study starts from I4's note on "lorries in the cost". Samuel, reading it: "pellets are transported via trucks (i think they need to be added, they use highways, and also that means a subdivision of the freighting sector) ... or by train if the city has it, but still requiring some trucks that use highways, to transport to and fro the railways, railways cuts transports costs alot but you still need trucks, also shipping is possible as well". Then: "that also means adding proper cargo ships ... a cargo port not just a tanker port, and also moving soem buildings that are in industry that you actulally have to build to infrastructure". Then: "freighting or something should be one sector, and it has subdivisions, and i think its a perfect time to redo roads, highways, and infrastructure ... it would be nice that we also see trains comming in by rail, as we see ships now, and the rail image should be different like in the map, and also the rails should connect to the city edge, all the way to the raily station or whatever, and highways should connect with rail stations and ports ... perhaps a new infrastrucutre buildings such as intersections, so that the map has a proper image for it ... first do the design study and ui mockup and think what is best".*

*What was read:*
- *The code at 0.7.123 (commit 9605b25, I4 merged), read and not changed: `Good`, `GoodsMarket`, `Sector` (the fleet), `sectors/Rail`, `ProcessingSector`, `Ports`, `BoatSchedule`, `InfrastructureManager`, `BuildingsTemplate`, `BuildAdvice`, `BuildScreen`, `CityMap`, `DistrictPlan`, `CityRuns`, `CityShore`, `TileRaster`, `BuildingVisual`, `ui/MapView`, `MapCheck` and `buildings.json`.*
- *The notes: `spec-roads-and-ports.md`, `the-railway.md`, `the-sixth-link.md`, `the-freight-band-and-the-three-loads.md`, `oil-and-ports-research.md` and the todo's port findings.*

*The "today" pictures were drawn by the game's own `TileRaster`, at 8 px a plot, from MapCheck's fixtures, in a throwaway harness outside the tree; they and the harness are in `freight/`. The mockups are the boards in `freight/` (`freight-study.html` holds all four) and the artifact "Freight & Infrastructure Study". Figures marked (est.) are estimates; [R#] are the sources at the end.*

---

## 0. In one page

**What I4 found, read plainly.**
- A Pelletising Plant keeps 2,538 vans, 64% of its cost, because the game moves ore in vans: 120 t a month each, "forty three-tonne loads". That is a delivery van's month, used for bulk.
- The ore it imports has already paid its haul inside its price, so the plant pays twice.
- A siding saves it nothing, because a works' vans never ask whether the railway carried anything.
- I4 counted the cost correctly. **What it counted is a missing vehicle and a missing business** (§1, §2).

**The proposal in one sentence.** One **Freight** sector, with three divisions, carries the city's leg of every tonne, and the map draws the network those tonnes run on.

**Freight (§2, §3):**
- **One rule: a tonne moves in legs, and each leg is paid once.** The long haul from the world is as today: the world's lorries and ships paid abroad, the city's railway at home. **The city leg**, from the gateway, berth or yard to the door (or door to door in the city), is a service Freight bills per tonne. The buyer pays for what it receives, the seller for what it exports. Vans keep only the goods going to the shops.
- **The sector takes Rail's slot in `Sectors.KEYS`.** One balance sheet, three divisions, each with its own price rule:
  - **Haulage:** trucks. A new good TRUCKS from a new Heavy Truck Plant; 25 t a load, about 3,000 t a month, D$215k, one driver; kept at a new Truck Depot.
  - **Rail:** as built. A siding now also takes the trucks off a works, and later the railway may carry the city's own bulk.
  - **Ports:** a landlord port. The city builds and owns the berths, Freight works them and charges handling, and Freight pays the city port dues, so a berth finally pays back.
- **A new Bulk Berth** (about 2.5 Mt a year, D$60M) lets a mid-sized city ship bulk by sea. The bulk carriers already exist in the game, but no city ever builds the 27.5 Mt Bulk Terminal they need. That is why only tankers are seen.
- **The pellet plant afterwards:**
  - with all its city legs by truck, it uses about 102 of Haulage's trucks and none of its own;
  - by rail with a siding, it uses about 27 trucks;
  - its D$173M of vans leaves its cost, and up to about 100 drivers' posts appear in the city (§3.6).

**The map (§4):**
- **Gateways** on the city's edge where the world's highway and main line come in, drawn on beyond the city's ground as "to the world ›".
- **A main line** from the rail gateway to the freight station.
- **Sidings** into the bulk works.
- **Yards drawn as yards:** fans of track, a shed, stockpiles or boxes, wagons standing.
- **Rail drawn as rail:** sleepers and two rails close in, double track on the main line.
- **Trains arriving as the ships do:** calls from the railway's tonnes, a pure function of time, in the boats' colours.
- **Access roads** from every port, yard and depot to the nearest highway ramp.
- **Junctions drawn by rule:** signals, roundabouts, diamonds, a trumpet, a cloverleaf where highways cross. **Drawn, not built:** the player never chooses where one goes, so a building would be a cost with no decision (★ D19).
- Optional: **trucks on the highways**, and the network's skeleton at the far view.

**The build menu (§5):**
- "Roads & transit" becomes **Infrastructure** again, holding roads, highways, transit, the ports and the reserve, all the city's.
- "Rail" becomes **Freight**, holding the rail rungs and the Truck Depot.
- Industry keeps only what investors build, which also clears its wrong city dot.

**Order (§7):**
- **FR0 and FR1**, the map's network and the trains, change no trace and can go now.
- **FR2** is the Freight sector and moves every trace.
- **FR3** is the ports, **FR4** the screens, and **FR5 and FR6** the options.

**22 decisions (§8)**, all but one with a recommendation. The one already made is Samuel's own: one sector with subdivisions.

---

## 1. What the game does today

### 1.1 Who carries a tonne, and who is paid

**The freight band** (`Good.baseFreight`, `GoodsMarket`):
- Every traded good carries the cost of moving one unit between the world and the city.
- Three quarters of the band between its import and export price is freight; one quarter is the world's margin.
- Iron ore, for example, lands at D$115 a tonne and leaves at D$85, and D$11.25 of each is haulage.

**The three ways a tonne crosses the boundary today:**
- **By road (the default):** the world's lorries, paid abroad. The money "simply left with the cargo" (`Rail.java:27`).
- **By rail:** the city's railway (`sectors/Rail`, the twelfth sector) takes a share of the boundary's bulk and goods. It bills the shipper at home at its quote, 30% to 100% of the lorry rate. It is priced like a regulated network: running cost plus 1.2% a month on its rate base.
- **By sea:** the city's four kinds of terminal take their share first, because every sea share (7% to 20% of a lorry's rate) is below rail's floor (30%). The ships are the world's, paid abroad.

**Inside the city nothing is charged.** A tonne of ore from the city's mine to the city's mill pays no freight. What moves it is the vans.

### 1.2 Vans, and the pellet plant

**What a van fleet is** (5d, 0.6.4):
- Every sector keeps a fleet of VANS ("Vans and trucks", 3 t a load).
- The fleet is sized as `tonnesMoved() / TONNES_PER_VAN`. Tonnes moved are everything the sector makes and everything it uses, at nameplate, both sides of the door.
- `TONNES_PER_VAN` is 120: "forty three-tonne loads a month".
- A sector short of vans runs at `0.6 + 0.4 × have/need`.
- Vans wear out over 120 months and burn 240 L of diesel a month.
- They have no drivers: `Sector.runFleet()` creates no post.

**What I4 (0.7.123) did.** It weighs a works with its vans. The vans' capital goes into what it costs, and their wear and diesel into what it earns (`ProcessingSector.weigh()`).

**The finding in I4's own words:** the probe's first Pelletising Plant, on imported ore, moves 304,500 t a month (150,000 t of ore in, 150,000 t of pellets out, 4,500 t of coal in). That keeps **2,538 vans**, D$173M at parity, **64% of the plant**. They cost D$1.4M a month of wear and D$0.4M of diesel. Without them in the cost "Iron & Steel went under whole seventeen times in the run".

**Read plainly, the number is the bug.**
- 120 t a month is a delivery van's month, and it is being used for ore.
- A tipper or a semitrailer carries about 25 t a load and does several loads a day.
- The game's own road-load fit already treats a truckload as 20 t (`BuildingsTemplate.TONNES_PER_WORKER`).
- The imported ore already paid its haul inside its price, so the plant pays twice (§2).
- A siding does not help: `tonnesMoved()` never asks whether the railway carried anything.

I4 counted the cost correctly. What it counted is wrong.

### 1.3 Ports

**There are four terminals today** (O9, 0.7.86, ids 94 to 97, all the city's, built from Build › Industry › Ports):

| Terminal | Carries | A year |
|---|---|---|
| Tanker Terminal | liquids | 3.25 Mt |
| Bulk Terminal | dry bulk, Capesize | 27.5 Mt, D$2,310M |
| Container Terminal | boxes | 6.75 Mt |
| General Cargo Terminal | general cargo | 0.45 Mt |

**Ship classes from MR tanker to VLCC, Panamax and Capesize, feeders and 5,500 t general cargo ships** are all modelled and drawn (`Ports.Ship`, `ShipShapes`).

**What they lack:**
- **No fees.** The crews and repairs are paid out of the city's transit bill. A berth is a pure cost, so the advice never offers one and automatic building never orders one.
- **No bulk berth a city can fill.** The playtest's dry bulk (about 57k t a month) never comes within 60% of the Bulk Terminal's 1.375 Mt a month. So only general cargo, and the oil chain's tankers, ever reach a berth.
- **No link from the quay to anything.** A ship's cargo crosses the city to its buyer by nobody in particular.

### 1.4 The map

Rendered by the game's own `TileRaster` at 8 px a plot (MapCheck's `yardCity` at ×30 and section 9's coastal town). These are board 1's left half.

**Rail:**
- **Drawn as a road.** One band the width of a paved street, cased grey, with white dashes. There are no sleepers, no double track and no trains.
- **A Rail Terminal yard** is a plain 24 × 24 box in Industry's purple, laid across the track so that it hides its own line.
- **Where the railway crosses a highway** there is only a small gap in the highway's dark band.
- **The railway's hub is the mine nearest the founding site,** and its arms stop where the city's owned ground stops. So the line that carries the city's trade to the world never reaches the world. On Jerus's own save, 1,863 of 3,336 track plots have no ground to go on.

**Highways:**
- They are laid straight on corridors from the founding site (RD3), good as far as they go.
- A ramp is a 1 to 2 px grey wedge in a verge's corner, almost invisible.
- A highway crossing another is a plain overlap: `CityRuns` records an `Interchange` there, and nothing draws it.
- **No highway may touch a terminal or a yard** (H5, rightly), and nothing else links them either.

**Streets:** a junction is two street bars overlapping. There is no junction object of any kind on the map.

**Ports:** near-black boxes on the shore with a 2 to 3 px quay out to sea. Boats are the only thing that moves on the map, and nothing moves on land.

**At the far view** nothing of the network is drawn.

## 2. The rule: a tonne moves in legs, and each leg is paid once

A tonne of ore that comes from abroad to a works moves in two legs, and a tonne that goes from the city's mine to its own mill moves in one:

| Leg | From → to | Carried by | Paid to | Today |
|---|---|---|---|---|
| **The long haul** | the world → the city's gateway, berth or yard | road: the world's lorries · rail: the city's railway · sea: the world's ships | road and sea abroad; rail at home | as today, the freight band (`Good.baseFreight`, `Rail.haul`, `Ports.factor`) |
| **The city leg** | gateway, berth or yard → the door; or one door → another door in the city | **trucks** (Freight's Haulage), or a **siding** where the works has one (Freight's Rail) | at home | not charged at all; the receiving sector's own **vans** do it, at nameplate, both sides of the door |
| **Distribution** | a works or warehouse → the shops | the sector's own **vans** | its own fleet | vans, as today |

Today the city leg is paid **twice for some tonnes and never for others**:
- **Paid twice.** A tonne that crosses the boundary pays its whole haul inside its price (the band), and the works *also* keeps a van per 120 t of it. Every one of the Pelletising Plant's tonnes crosses: the ore pays its haul inside its import price and the pellets inside their export price. The plant keeps 2,538 vans for them as well.
- **Never paid to anyone.** A tonne moving between two works in the city pays no freight at all. It needs vans, but the vans have no drivers (`Sector.runFleet()` wears them and burns diesel, and employs nobody), so nobody in the city earns from moving it.
- **Rail and ships change neither.** `Sector.tonnesMoved()` reads only a works' nameplate. A works on a siding keeps the same vans as one that is not. The road's own note says so: "the honest fix needs an in-city freight matrix, which is a later argument" (`InfrastructureManager.java:215-223`). This is that argument.

**The rule proposed:**
- **The long haul stays as it is.** The world's lorries and ships are paid abroad, the city's railway at home.
- **The city leg is a service** bought from the Freight sector, billed per tonne through `Sector.billForService`, like the railway's haulage today.
- **Who pays which leg.** The **buyer** pays the city leg of what it receives, whether from abroad or from a works in the city. The **seller** pays the city leg of what it exports. So each tonne pays each leg exactly once.
- **Vans keep distribution only:** GOODS moving inside the city, to the shops' back doors. Jerus's 5d mechanic, a sector short of its own vans running slower, stays exactly as it is for them.
- **Money is conserved:** every haulage dollar is a dollar on a shipper's input line, as rail's is.

Jerus said it in 2026-09: "stuff still needs to freight to and fro from the rail. and some stuff wont even take the rail" (quoted at `InfrastructureManager.java:209-211`). Today that truck exists only as a road load. This makes it a truck someone owns, drives and is paid for.

---

## 3. Freight: one sector, three divisions

### 3.1 The sector

- **Rail's slot becomes Freight.** It is `Sectors.KEYS` position 12. That order is load-bearing (the equity index, the household share arrays, the audit pools), so Freight takes the slot rather than joining at the end. The railway's company becomes the Freight company. Old saves need not load (Jerus, 2026-10-10), so `SAVE_FORMAT` rises and the floor with it.
- **One business, three divisions:** **Haulage** (trucks), **Rail** (track and trains), **Ports** (cargo handling at the city's berths). One balance sheet, one share register, one borrower at the bank.
- **Each division keeps its own figures and its own price rule.** That means its own fleet, capacity, quote, planner and lines in the statement. A rule that prices a railway cannot price a truck (the railway's own lesson: "a shop's pricing rule cannot price a utility").
- **This is how real freight groups are built.** Large railways run their own trucking and intermodal arms, and port groups run inland logistics. It also means one bill for a tonne that goes by rail and then by truck.
- **The risk.** One borrower that is a third of the city's freight is a third of the bank's freight lending. The bank already has no concentration limit (found in 5d, still open). This sector would be the first place it bites.

### 3.2 Haulage: the trucks

**The vehicle**
- **TRUCKS**, a new good: a heavy tractor and semitrailer, 25 t a load (an EU 40 t articulated truck [R9]; a North American 80,000 lb truck carries about 20 t [R11], a tipper 20 t [R15]).
- **D$215k at parity**: a tractor at US$158k to US$195k [R12][R19] and a trailer at about US$45k to US$60k [R21].
- **Ten years** (`VAN_LIFE_MONTHS`, 120): a first owner keeps a tractor 3 to 5 years and its whole life is about 15 [R22][R12], so ten is a fleet's average.
- It **imports and does not export**, as vans and wagon sets do, for the same reason: an unbounded export market at a fixed floor builds the plant for ever (`Good.java:579-594`).
- It is made by a new **Heavy Truck Plant** (Automotive) on fabricated steel and machinery, beside the Commercial Vehicle Plant, which keeps making vans.
- It carries a `since` year for the 1900 start (research-design.md 2.1). Before it, the city leg goes by horse dray at a fraction of the tonnes. That is where the word drayage comes from. This is the research batch's to wire, and nothing reads it now.

**What one truck does in a month:**
- **A city leg** is the game's own `KM_A_LOAD`: 50 km there and back.
- **A cycle takes about 1.5 hours** at that length (est.: port trucks make three or four moves a day with a 51-minute terminal visit [R17][R18], and quarry tippers on a short haul make 16 to 18 loads a shift [R16]).
- **So 6 to 7 loads in a ten-hour shift.** At 25 t a load and 22 working days that is **about 3,000 t a month on one shift** (est.). Dutch lorries of 20 t and more average only 3,500 km a month [R4], which would give about 1,500 t, the low end.
- **That is 25 vans' worth.** A van moves 3 t a load, and a real one less: a large panel van carries 1.2 to 1.65 t [R25].
- **Diesel: 33 L a 100 km** [R3][R23], against the van's `DIESEL_LITRES_PER_100_KM` of 12.
- **One driver a truck on one shift.** US fleets run 0.93 drivers a truck [R2]. This is the first freight vehicle in the game with a crew.

**The Truck Depot**, a new building (Freight's, sector-built; the player may build one too):
- The yard where the trucks are kept, fuelled and repaired. It holds 100 trucks for about D$12M, on about 4 ha, with about 25 mechanics (all est.).
- It needs ground and mechanics.
- It is the image of haulage on the map (§4).
- Trucks beyond the depots' room are not stopped. They park on the street, cost more to keep and the hover says so (est. +10% upkeep) (★ D6).

**Capacity and the throttle**
- Haulage's capacity is trucks × tonnes a truck-month.
- Each month the city's city-leg tonnes are dealt against it, **bulk before goods**, as the railway deals.
- A sector whose tonnes are not all carried runs slower by the van rule, moved across: `0.6 + 0.4 × carried share`. A firm short of haulage collects its own, slower, and is not stopped.
- `operatingRate` keeps its six factors. The vans factor becomes **the freight factor**: vans for a sector's goods, haulage for its bulk, whichever binds.

**The price**
- **Rail's rule, on Haulage's books:** the month's running cost (drivers, diesel, depots, upkeep, wear) plus `TARGET_RETURN` (1.2% a month) on its rate base (trucks, depots, ground). The result is per tonne, with the scarcity multiple when tight (`MAX_SCARCITY_MULTIPLE` 1.6) and moved a quarter of the way a month (`REPRICE_SPEED`).
- **No ceiling but the rule,** as a regulated carrier. The cap rail has (the lorry rate) is a long-haul figure and does not apply to a city leg.

**What a truck-month costs (est.):**
- D$1.8k of wear (D$215k over 120 months);
- D$1.4k of diesel (6,000 km at 33 L a 100 km, at DIESEL's D$0.69 a litre);
- D$2.6k of return (1.2% of D$215k);
- a driver at the city's wage;
- a share of a depot.

That is about **D$4 a tonne** at a driver's month of D$5k (est.).

**How that compares:**
- **Against the world's haul,** it is about a third of a tonne of ore's whole haul from the world (IRON's `baseFreight` .01125, D$11.25 a tonne).
- **That is the real proportion.** Drayage is 5% to 10% of an intermodal move's distance but more than 30% to 40% of its cost [R57][R58]. That is why mills sit on rail and at ports, and why a siding matters.
- **Against the long haul per tonne-km,** the railway carries at about 3 US¢ a tonne-kilometre against a truck's 8 to 10 [R50][R51].

**The planner**
- Buy trucks toward the month's carried and refused tonnes over tonnes a truck-month. At most an eighth of the gap a month (`FLEET_DELIVERY_MONTHS` 8, the vans' rule).
- A depot when the fleet passes the depots' room.
- Retire on the van's pace (`VAN_LIFE_MONTHS` 120: "a lorry is worked").

### 3.3 Rail

The railway is kept as built. Three things change:
- **A siding takes the truck off.** Of the tonnes the railway hauls for a works, `RAIL_ROAD_RELIEF` (.75) go door to door by siding and the rest is drayage by truck, billed by Haulage. Today the .75 only relieves the road; now it also relieves the trucks the works pays for. A works on rail buys a quarter of the city legs it would otherwise.
- **The railway may carry the city's own bulk** (★ D4). Today `Rail.haul` counts only cross-boundary tonnes, so a yard "near the mines" never carries the mine's ore to the city's mill. Proposed: domestic BULK tonnes join the railway's offer, priced at its quote on the city leg's own rate (Haulage's price, not the long-haul lorry's). Dealt after the boundary's.
- **The railway reports its tonnes by cargo kind,** so the map's trains (§4.4) can be counted, as the ports report theirs for the boats.

### 3.4 Ports

**Today:**
- The four terminals are the city's (`BuildingType.PORTS`: "no investor would build one").
- Their crews and repairs are paid out of the city's transit bill (`Game.portsBill`).
- They charge nothing: a `grep` for fee, dues and wharfage finds only prose.
- So a berth is a pure cost to the city, and that is why the advice never offers one.

**Proposed: the landlord port**, the most common way ports are run: the authority keeps the land and the quays, and private operators lease the terminals and bring the equipment [R91][R92]:
- **The city builds and owns the berths.** They are infrastructure, on Build › Infrastructure (§5). The city keeps paying their repairs.
- **Freight's Ports division works them.** The crews move from the city's transit bill to Freight's payroll. It charges shippers a **handling charge** per tonne by cargo kind. This study did not research these (est.: bulk a few dollars a tonne, a box a few hundred dollars, general cargo the dearest per tonne). FR3's brief fixes them from sources.
- **Freight pays the city port dues** per tonne crossing a berth. That is a new city revenue line, "Port dues", and a Policy dial (the city's own price, like the fare). Its default recovers a berth's cost over its life at the city's borrowing rate.
- **The price rule for handling** is the division's running cost plus the return, as Haulage's, with the dues passed through.

Ports keep going first (`seaFreightShare` below `RAIL_FLOOR` for every kind).

The quay-to-door leg is a city leg by truck, unless the port has a yard (★ D7: a port yard, rail on the quay). Trucks carry 59% of Rotterdam's containers inland and about three quarters of Long Beach's; rail takes about 45% at Montreal [R67][R69][R70].

**A berth a city can fill: the Bulk Berth (new).**
- The only dry bulk building is the Bulk Terminal, a Capesize export terminal: 27.5 Mt a year, D$2,310M (id 95).
- The playtest's dry bulk (57k t a month) never comes within 60% of its 1.375 Mt a month. So no city builds one, and its ore comes by lorry.
- A **Bulk Berth** for Handysize and Supramax ships gives a mid-sized city a bulk port: **about 2.5 Mt a year for about D$60M** (est.).
  - Two harbour cranes at an effective 500 t an hour, 20 hours a day and half the time occupied, make 1.5 to 3.5 Mt a year for US$40M to US$80M.
  - A real mechanised 250 m berth at Paradip handles 8 Mt a year for about US$72M [R74]. Small import terminals run 0.15 to 1 Mt a year [R73].
- The 30,000 to 60,000 t bulk carriers join `Ports.Ship` beside Panamax and Capesize.

**Why Jerus sees only tankers.** All four kinds of ship are in the game and drawn (`ShipShapes` has a tanker's, a bulk carrier's, a box ship's and a general cargo ship's deck). But in most cities only the tanker terminal (from the oil chain) and the General Cargo Terminal ever get built. The Bulk Berth and the ports' own payback (above) are what bring the bulk carriers in.

### 3.5 Vans after Freight

- **A sector's vans move its GOODS tonnes only,** both sides of the door at nameplate, as 5d set: `vansNeeded = goods tonnes / 120`.
- Bulk sectors (Iron & Steel, Iron Mining, Materials, Agriculture) keep almost none. Retail and Food Processing keep theirs.
- I4's **"a works is weighed with its lorries"** becomes "a works is weighed with its **freight**". The planner adds the month's city-leg bill at Haulage's quote, less what its siding saves, to the running cost it estimates. No fleet appears in its capital.
- **The same rule reaches Iron Mining.** `ExtractionSector` has no lorry weighing today (I4 put it in `ProcessingSector` only), and a mine ships every tonne it digs.

### 3.6 The pellet plant again (est.)

The I4 probe's first order was a Pelletising Plant on imported ore: 150,000 t of ore in, 150,000 t of pellets out and 4,500 t of coal in, so 304,500 t a month at nameplate, every tonne a city leg.

| | Today (I4) | Freight, all by truck | Ore in by rail to a siding, pellets out by sea | Ore in and pellets out by rail, a siding |
|---|---|---|---|---|
| Tonnes trucked | (2,538 vans move all 304,500) | 304,500 | 192,000 (ore's drayage 37,500, pellets quay to door 150,000, coal 4,500) | 79,500 (drayage 75,000, coal 4,500) |
| Vehicles | 2,538 vans, the plant's own | about 102 trucks, Haulage's | about 64 trucks | about 27 trucks |
| Fleet in the plant's cost | D$173M, 64% of the plant | none | none | none |
| A month for its city legs | D$1.44M of wear + D$0.42M of diesel | about D$1.2M of haulage | about D$0.77M | about D$0.32M |
| Jobs in the city | none | about 102 drivers + depot mechanics | about 64 | about 27 |

The siding is worth about D$0.45M a month to this plant on its ore alone: 112,500 t door to door.

The plant's monthly freight comes out at or below I4's van running cost, and **the D$173M of vans leaves its capital**, which is what killed it. The money goes to a business in the city that employs drivers, rather than to wear and diesel. Whether Iron & Steel then lives is the FR2 batch's ensemble to measure, not this study's to promise.

### 3.7 What it costs a city (est.)

The 5d measurement (a 106,612-person city moving 1,169,640 t a month at nameplate) gives the scale:

| | Today | Freight (est.) |
|---|---|---|
| Fleet | about 9,750 vans, all sectors | about 200 vans (goods only) + about 250 to 300 trucks (the bulk city legs, each paid once, fewer with rail's sidings) |
| Fleet capital | about D$664M of vans | about D$14M of vans (sectors') + about D$55M to D$65M of trucks (Freight's) |
| Drivers | none | about 250 to 300 posts, and the depots' mechanics |
| Who is paid | wear and diesel (and the import of vans) | the Freight sector, at home |

That city's tonnes were almost all bulk: Manufacturing alone moved 972,141 t of fabricated steel and machinery, and Heavy Industry 148,680 t. Its goods (Retail's 5,312 t and Food Processing's 2,011 t, with some of Automotive's) are a few percent. So almost the whole van fleet goes, and one truck replaces about 25 vans. Counting each leg once, rather than both sides of every door, takes off about a fifth more.

## 4. The ground it runs on: the map

Everything in this section is the map's alone. `CityMap`'s rule holds: the map reads the model, nothing in the model reads the map. Section 4 can therefore ship before the Freight sector does, with every trace byte-identical (batches FR0 and FR1, §7).

### 4.1 Gateways: where the city meets the world

**Today** a run stops where the city's owned ground stops (`RunGround.at()` returns -1, the arm waits, `CityRuns.java:375`). The railway's hub is the mine nearest the founding site and its arms grow out from there. Nothing says which way the world is, so the line that carries the city's trade to the world does not reach the world.

**Proposed:** two **gateways** on the city's edge, one for the highway and one for the railway. A gateway is a point where the world's own road and line meet the city's.

- **Found once, like the offing.** The sea already has one: the bearing with the longest run of open sea from the port (`SeaRoutes`). The land gets the mirror of it:
  - on a generated world, the bearing (every 5°) with the longest run of **land** from the founding site;
  - on the Earth, the bearing toward the nearest larger place in the pack's place list (`EarthData`), along land.
- **The rail gateway shares the highway's bearing.** Real main lines and motorways follow the same valleys. The two never share a row anyway (`HIGHWAY_AT` 16 against `RAIL_AT` 8 plots into a cell).
- **The first arm laid is the arm toward the gateway.** Today the highway's spine is the founding site's row, both ways (H1). It becomes the row or column nearer the gateway's bearing, and its gateway arm grows first. The railway's first arm runs from its hub toward the same gateway, so the first track the city lays is the main line out.
- **Beyond the city's ground, the world's line is drawn but not counted.** From the last owned plot, the highway and the main line run on along the bearing over unowned ground, faded, to the edge of the view, labelled "to the world ›". This is the same idea as the abyss past the offing: it says where the trade goes without pretending the city owns it.
- **A city with no land route has no gateway.** An island founding gets no road or rail to the world (see §9, item 6).
- **Saved** in the map's sidecar with the runs (FORMAT 7): two bearings and two points.

### 4.2 The main line and the yards

**Today:**
- Track plots are 72 for each Rail Spur and 248 for each Freight Line, laid as one network of arms.
- Each Rail Terminal is a **yard**: a 24 × 24 box across the track, in a cell the track runs straight through, nearest a mine (`yardSpots`, `CityRuns.java:778`).
- The yard is drawn in Industry's purple (`0xff9076ba`), because `classOf(RAIL)` falls through to INDUSTRY. Nothing marks it as a station.

**Proposed:**
- **The main line** is the arm from the rail gateway to the yard nearest it. If the city has no Rail Terminal yet, it runs to the hub.
- **Sidings** branch off the main line into the estate cells of the works the railway serves (§4.3). They take track plots from the same count: a Rail Spur is already named for one, and on the map it becomes one.
- **The yard nearest the gateway is the freight station.** Further yards stay where they are, near the mines.
- **The yard gets its own look** (§4.3) and its own class, TRANSPORT, with its own colour. It stops reading as industry.
- **Ports and yards get an access road** (§4.5).

### 4.3 Rail drawn as rail

| Zoom | Today (`TileRaster.track()`, line 749) | Proposed |
|---|---|---|
| L0, close (≥ 6 px a plot) | a band 0.52 of a plot wide, cased `0xff707070`, white dashes every other plot | **Two rails on sleepers.** Ballast as a grey-brown bed (`#6f675c`), sleepers across it every 0.25 plot (`#4a3f35`), two steel rails (`#c9ced3`). **Double track** (two pairs, 0.22 plot apart) on the main line once the city has a Freight Line. |
| L0, 3.2–6 px a plot | same band | Ballast bed with the rails as one light centre line and ticks for sleepers every plot. |
| L1 (blocks of 4 plots) | track plots filled solid grey | A dark line (`#3a3632`) with cross-ticks every block, so it cannot be mistaken for a road. |
| L2 (blocks of 8) | solid grey | The same dark line, one pixel, no ticks. |
| FAR | nothing | The main line as a 1 px dark line on the node image, with the highways (§4.8). |

Double track is a picture of scale, not of capacity: single track with central traffic control carries about 30 trains a day, double 75 [R49], and no city's own trains come near either.

**Yards.** A Rail Terminal's 24 × 24 box is drawn as what it is:
- a **fan of parallel tracks** (6 to 10 sidings) with a ladder at each end joining them to the main line;
- a **freight shed** along one side, a low long building in the TRANSPORT colour;
- by what the yard mostly handles, read from Rail's tonnes by cargo kind: a **bulk yard** shows stockpiles in the sand colour of the ore (`#c9b68f`); a **container yard** (an intermodal terminal) shows rows of boxes in the boats' white (`#d6dde4`) and two gantry cranes across the tracks;
- **wagons standing on the sidings** in proportion to the wagon sets the railway owns over the sets its track needs. A railway short of rolling stock shows empty sidings.

**Sidings into works.** A siding ends inside the estate cell of a bulk works (the Iron & Steel works, mines, the refinery campus) on a short spur beside the building's box, with a loading shed. Which works get sidings: the largest BULK works within one district of the line, in order, while the railway's spur track lasts.

**Crossings.**
- Streets cross track **at level** today (the `CROSSING` flag) and the street's surface is simply drawn over the rails. Proposed: draw **level crossing** marks, white crossbucks and barrier stripes at L0, so the eye sees a crossing.
- Rail over a highway is a **bridge**, as today (`F_RAIL_OVER`).
- An **arterial or boulevard** over the main line becomes a road bridge (drawn as the road deck over the track), never a level crossing. A busy line has no level crossings on its arterials (★ D18).

### 4.4 Trains, as the ships are

**The model of a train** is `BoatSchedule`'s, almost line for line:
- **A call** is a train: a schedule entry with a route (gateway to yard), a kind, a direction, and an arrival time hashed into the month.
- **Calls a month** = the month's railed tonnes of that kind ÷ one train's payload. The tonnes come from Rail's own tally by cargo kind (`Rail.haul`).
- **Payloads:**
  - a bulk **unit train** about 8,000 t (est., between Europe's ore and coal block trains of about 4,000 t net [R47] and a North American coal train of about 12,100 t, 115 cars [R39]);
  - a **mixed** (manifest) train about 1,500 t (Europe's route limits run 1,300 to 2,400 t [R45]);
  - an **intermodal** train about 100 boxes (Union Pacific averaged 173 [R40]; a European 750 m container train is 50 wagons [R46]).
- **The route:** from the faded world line beyond the gateway, along the main line, into the yard, a dwell on a siding, and back out. A train carrying an import arrives full and leaves empty; an export arrives empty and leaves full.
- **A pure function of time**, never saved. The map asks which routes cross the view and draws those trains. A city that rails nothing has no trains.

**Drawing a train:**
- A locomotive (dark, `#2b2f36`) followed by wagons. The number of wagons is the payload ÷ 100 t, drawn up to 40, and the length is to scale at L0 (a wagon is about 15 m, half a plot).
- **Colours as the boats':** full imports blue `0xff5aa9ff`, full exports orange `0xfff2a65a`, empty grey `0xff8496ab`, intermodal boxes white `0xffd6dde4`.
- **Wagon shapes by kind:** open hoppers with a heap of the cargo's colour (bulk), tank wagons (liquids), flat wagons with boxes (intermodal), covered vans (general).
- Drawn from L1 in, like the boats. At L1 a train is a coloured dash on the line.

**The clock is the boats'** (`MONTH_SECONDS` 60 for the schedule). A train runs from the gateway to the yard in about 6 s at any view, and dwells about 3 s. These are visual paces, not model ones.

**Worked example (est.):** a city railing 200,000 t a month of bulk sees about 25 unit trains a month, so a train arrives about every 2.4 s of the map's clock. That reads as a busy line, not a blur.

**Cost.** The boats' query costs 0.037 to 0.378 ms a frame at 10 billion people (`BOAT_FRAME_MS` 0.5, MapCheck 9). Trains have a few routes per city against the sea's thousands of calls, so the same structure is well inside budget. MapCheck 9 gets a train section asserting pure-in-(call, t) and the frame time.

### 4.5 Highways to where the freight is

**Today** a highway may not touch a sea terminal (`CityShore` `CLEAR = 1`), and a yard may not sit on a highway or its verge (`yardFits`). Both are right: H5 says no building beside a highway. But nothing links them at all. A port's only road is whichever arterial its cell happens to have (`nearRing`).

**Proposed, an access road:**
- **What it is.** Every sea terminal, every yard and every Truck Depot (§3.2) gets an access road. It is a street of the plan's ARTERIAL role (new role `ACCESS`), full width, paved first, from the work's gate to the **nearest highway ramp** if one lies within two districts. Otherwise it runs to the arterial grid.
- **It keeps H5.** It is a street, not a highway: it meets the highway at a ramp, where streets already meet it.
- **Its surface is paid like every street's** (§2.5 of the roads study): from the model's road plots, arterials first. A port in a city short of road gets a gravel access road, or a track, and the hover says so.
- **The hover:** "Port access, paved, 30 m · to the highway at the Harbour interchange, 2.1 km".
- **Ramps where access roads meet the highway** are added to the every-other-arterial ramps (`RAMP_EVERY` 2), so a port always has its own on-ramp. A single-lane ramp carries about 2,000 vehicles an hour [R86]; one ramp per access road is ample for any city's port (est.: a 350 m container berth keeps about 420 truck moves a day [R71]).

### 4.6 Junctions and interchanges

**Today:**
- There is no junction object anywhere on the map (a `grep` for junction, roundabout and interchange finds none in the map files).
- A + is two street bars overlapping.
- A ramp is a single diagonal stroke across a verge plot's corner.
- `CityRuns` records an `Interchange` where corridors cross, but nothing draws one.

**Proposed: junctions drawn by rule, from what crosses what.** None of these is a building.

| Where | Drawn as (L0, ≥ 8 px a plot) | Ground |
|---|---|---|
| Street × street (homes cells) | a plain + with kerb corners | the streets' |
| Street × arterial, estate T | a T with wide radius corners (trucks turn here) | the streets' |
| Arterial × arterial | **signals**: zebra crossings on each arm, stop lines, a dark box | the streets' |
| Boulevard × boulevard, or arterial where five or more cells meet | a **roundabout**: a circle of 1.5 plots with a planted middle | one plot of each corner cell |
| Arterial under a highway (every other one, and every access road) | a **diamond**: four slip roads parallel to the highway in the verge, joining the arterial either side | the verge (H5's plot each side), as now |
| Highway arm ending at another highway | a **trumpet**: one loop | an 11 × 11 plot reserve in the cell beside the junction (about 11 ha, est.) |
| Highway × highway (`CityRuns.Interchange`) | a **cloverleaf**: four loops, four slips | a 19 × 19 plot reserve centred on the crossing (570 m square, about 32 ha: about 20 acres a quadrant [R82]) |
| Highway × highway, ground short (water, the edge, buildings already placed) | a **stack**: flyovers stacked on the crossing, no loops | the corridors' own plots plus their verges |

**The ground for interchanges.** A cloverleaf's reserve is laid with the corridor, before cells open round it, the way H5's verges are. It is street ground on the map, not lots, exactly as the verges are (★M was dropped, 2026-10-09).

**Why these are drawn by rule and not built.** Jerus asked about intersections as buildings "so that the map has a proper image for it". This study recommends against buildings, for three reasons:
- **Where an interchange goes is never the player's choice.** The map lays the corridors and the crossings fall where they fall. A building would be a cost with no decision.
- **The game adds a building when it brings a mechanic** (his rule). An interchange's only mechanic would be "the highway works", which the highway already does.
- **The highway's price can carry it.** A diamond costs about US$25.6M before land, a parclo US$29.1M [R78]. Directional stacks run from US$105M (four levels [R84]) to US$615M (five [R85]).
  - The map puts a ramp every 1.92 km (`RAMP_EVERY` 2), which sits inside the urban minimum spacing of 1.6 km [R87].
  - The Elevated Highway costs D$9.6M for its 7 plots (210 m), so D$45.7M a km. A diamond every 1.92 km adds about US$13M a km, **about 30%**.
  - `buildings.json` does not say whether the D$9.6M counts its ramps (★ D20).

The alternative is set out under ★ D19.

### 4.7 Trucks on the map (optional)

- Haulage's loads make the roads move the way the boats make the sea move.
- **One mark per N loads**, N chosen so a busy highway shows a truck every few plots at L0, and the legend says "trucks: one per N loads".
- Marks run between the depots, yards, ports and works along the highway and the access roads.
- Imports blue, exports orange, empty grey, as the boats and trains.
- From L0 only. A pure function of time, never saved.
- **This is the costliest drawing in the study** (every highway stretch on screen, every frame), so it is its own batch (FR5), after the trains have measured the frame budget.

### 4.8 The far view

Today nothing of the network is drawn at FAR: the city is tinted districts.

**Proposed:** draw the **skeleton** on the node images, so a city's shape reads from far out:
- highways as 1 px lines in the highway colour `0xff3b4048`, lightened 40% against the dark ground;
- the main line as a 1 px dark line;
- ports and yards as 2 px dots in the TRANSPORT colour.

These are city-wide state already (`CityRuns`, `CityShore`), so the nodes can be drawn without planning a district.

### 4.9 The new legend

- The **Railway** entry splits into **Main line**, **Siding** and **Freight yard**.
- New entries: **Access road**, **Interchange**, **Level crossing**, **Truck depot**, **Train** (with the four boat colours, reused), and **Gateway: to the world**.
- The **TRANSPORT** class joins the ten building classes. Yards, terminals and depots move into it.

---

## 5. Build › Infrastructure

**Today** (`BuildAdvice.categories()`, `BuildAdvice.java:92-111`):
- **"Roads & transit"** holds the three roads and the three transit lines. It was called "Infrastructure" until 0.7.24 (`was`, `:95`).
- **Industry** holds the four sea terminals and the Strategic Reserve. These are the city's: only the player can build them, and they have no investors' line.
- **Rail** is its own market category.

Two side effects of where things sit:
- Because PORTS is in Industry's type set but not in `investorTypes()`, the Industry tab probably carries the city's blue dot and the line "Only the city builds these" (a reading of `BuildScreen.java:111-113, 301, 456`, not checked on screen).
- Ports and the reserve have no measure, so neither the advice nor automatic building ever orders one.

**Proposed: the city's things on one strip, the businesses' on theirs.**

| Strip tab | Groups | Who builds |
|---|---|---|
| **Infrastructure** (renamed back from "Roads & transit") | **Roads:** Gravel Road, Paved Road. **Highways:** Elevated Highway (and the Highway Bridge when it comes: the roads study's batch B1). **Transit:** Bus Network, Light Rail Line, Metro Line. **Ports:** Tanker Terminal, Bulk Berth (new, §3.4), Bulk Terminal, Container Terminal, General Cargo Terminal. **Reserve:** Strategic Reserve. | The city |
| **Freight** (renamed from "Rail") | **Rail:** Rail Spur, Freight Line, Rail Terminal. **Haulage:** Truck Depot (new). | The Freight sector's investors; the player may build them too, as today for rail |
| **Industry** | Unchanged, less the ports and the reserve. The Tank Farm stays: it is Refining's. | The investors only, so its dot is honest again |
| **Vehicles** | Adds the Heavy Truck Plant (new, §3.2) | Automotive |

- **Ports get a measure.** With port dues (§3.4) a berth has a payback: dues and the trade it carries against its running cost. The advice can rank it, and automatic building can order one by Jerus's rule for everything that is not an essential service ("judged by payback", 2026-10-10).
- **The rail tab "Infrastructure"** (the twelfth tab, `InfrastructureScreen`) keeps its pages: Roads, Transit, The railway, Freight. "The railway" and "Freight" become one page, **Freight**, with a division chip strip (§6). It gains a **Ports** page: berths by kind, what each carried, dues, and the ships.

## 6. The screens

The mockups are the four boards in `freight/` beside this note, and the artifact "Freight & Infrastructure Study", which holds all four.

- **The map (board 1)** shows the same ground drawn two ways: today, then proposed. The proposed half has the gateways and the world line beyond them, the main line and its sidings, a freight yard, a train arriving, and the highway with its diamonds and a cloverleaf. It also has the port's access road, a Truck Depot, level crossings, and trucks on the highway. The hover card and the legend show the new words.

- **Sectors › Freight (board 2).** The sector page, as every sector's (the head, five figures, the investors' line, page chips), with a **division strip** under the chips: All · Haulage · Rail · Ports.
  - The **Operations** page for All is the **city's freight this month in legs**: tonnes by leg (long haul, city leg, distribution) and by mode (world lorry, rail, sea, truck, siding, van), each bar a door to its division.
  - Each division's page has its fleet against what it needs (trucks 412 of 455; wagon sets; berths by kind and how full), its quote with the rule's floor and target, and its own lines (the railway's are today's).
  - The **Income** page's statement gains a division column. The rows are the shippers billed, and each figure is a door to that sector's input line.

- **A works' page: "How its tonnes move" (board 3).** On every sector's Operations page, under the flow card, one strip shows the sector's tonnes by leg and mode and the month's freight bill by division.
  - It also shows what a siding would save. For example: "a siding would carry 75% of 150,000 t: about D$0.4M a month less haulage".
  - The Iron & Steel version shows the pellet plant from §3.6, before and after.

- **Build › Infrastructure and Build › Freight (board 4).** The strip with Infrastructure first among the city's tabs, its five groups, and the new **Bulk Berth** card with a payback line, which ports never had. Freight's tab shows the rail rungs and the **Truck Depot** card, with its investors' line ("Freight would build one: 18 trucks over its depots' room").

- **Infrastructure › Freight and › Ports.** The twelfth tab's railway and freight pages become one **Freight** page with the division strip. The new **Ports** page lists berths by kind, tonnes against capacity, the dues collected, and the month's calls by ship class. Its door "The ships on the map" opens the map at the port.

- **Trade.** The freight band's bar splits into its legs: the world's margin, the long haul by mode (world lorry, rail, sea), and the city leg.

---

## 7. Batches

| Batch | What | Traces |
|---|---|---|
| **FR0** | The map's network (§4.1–4.3, 4.5, 4.6, 4.8, 4.9): gateways and the world line, the main line, sidings, yards as yards, rail drawn as rail, access roads, junctions and interchanges by rule, the far skeleton, the TRANSPORT class, the legend; sidecar FORMAT 7; MapCheck 8 extended (gateway reached, access roads reach a ramp or arterial, interchange reserves clear of buildings) | identical |
| **FR1** | Trains (§4.4): `TrainSchedule` from Rail's tonnes by kind, drawing, MapCheck 9's train section and frame time. Rail reports tonnes by kind (read-only) | identical |
| **FR2 ★** | The Freight sector: Rail's slot renamed, the divisions, Haulage (TRUCKS, the Heavy Truck Plant, the Truck Depot, the city leg billed by buyer and exporter, the freight factor), vans for goods only, the siding relief on trucks, I4's weighing moved onto freight and onto `ExtractionSector`; `SAVE_FORMAT` and the floor raised | every trace moves; re-baseline, the ensemble |
| **FR3 ★** | Ports as a landlord port: Freight's Ports division, handling charges, port dues and its Policy dial, crews off the transit bill, the Bulk Berth and the Handysize/Supramax classes, ports' payback in the advice | moves |
| **FR4** | The screens (§6): Sectors › Freight with the division strip, "How its tonnes move", Build › Infrastructure and Build › Freight, Infrastructure › Freight and › Ports, Trade's band in legs; the manual | identical to FR3 |
| **FR5** | Trucks on the map (§4.7), if wanted | identical |
| **FR6** | Rail carries the city's own bulk (★ D4), if wanted | moves |

FR0, FR1 and FR5 are the map's alone and can go now, in any order after FR0. FR2 is the heart and needs the research numbers fixed in its brief. FR3 can follow it or ride with it.

---

## 8. ★ Decisions

**The sector**
1. **D1. One Freight sector with three divisions** (Haulage, Rail, Ports), in Rail's slot of `Sectors.KEYS`, renamed. **Jerus's call, 2026-10-11** ("freighting or something should be one sector, and it has subdivisions"). Recommend the name **Freight**.
2. **D2. The city leg is a service** billed per tonne by Freight. The buyer pays for what it receives (from abroad or at home), the seller for what it exports. Each leg is paid once. **Recommend yes.** It ends the double charge on imported tonnes (§2).
3. **D3. Trucks belong to Haulage, not to each sector.** Sectors keep vans for their goods only. **Recommend yes.**
   - **Real trucking is mostly for hire:** about 90% of the EU's road tonne-kilometres [R28].
   - **Own-account trucking is real but narrow:** it is a third of US truck tonnes, mostly short-haul quarry, waste and petroleum [R29][R30], trades the game does not have yet.
   - **The alternative**, each sector buying trucks as it buys vans, keeps the fleet in every works' cost (the pellet plant's 64%) and gives Freight nothing to haul.
4. **D4. The railway may carry the city's own bulk** (mine to mill) at the city leg's price. **Recommend yes, as its own batch (FR6)** after FR2 has been measured.
5. **D5. The Heavy Truck Plant** (Automotive) makes TRUCKS; trucks import but do not export. **Recommend yes.**
6. **D6. The Truck Depot** is a building (Freight's), about 100 trucks a depot. Trucks past the room park on the street at +10% upkeep. **Recommend yes.** It is haulage's image on the map and a real need for ground.
7. **D7. A port yard** (rail on the quay) when a port and a Rail Terminal are in the same district: the quay-to-door leg by rail. **Recommend later**, with FR6.

**The ports**
8. **D8. The landlord port:** the city builds and owns the berths; Freight's Ports division works them and charges handling; Freight pays the city port dues per tonne; the crews move off the city's transit bill. **Recommend yes.**
9. **D9. Port dues are a Policy dial,** default the berth's cost recovered over its life at the city's rate. **Recommend yes.**
10. **D10. The Bulk Berth** (Handysize and Supramax, about 2.5 Mt a year for about D$60M) so a mid-sized city can ship bulk by sea. **Recommend yes.**

**The build menu**
11. **D11. "Roads & transit" becomes "Infrastructure"** again, with Roads, Highways, Transit, Ports and the Strategic Reserve. The ports and the reserve leave Industry. **Recommend yes.** It also fixes Industry's city dot.
12. **D12. "Rail" becomes "Freight"** on Build, with the rail rungs and the Truck Depot. **Recommend yes.**

**The map**
13. **D13. Gateways:** a highway gateway and a rail gateway on the city's edge, on the longest land run (generated) or toward the nearest larger place (the Earth). The first arm is laid toward them, and the world's line is drawn faded beyond the city's ground. **Recommend yes.**
14. **D14. The main line** from the rail gateway to the nearest yard; **sidings** into the estate cells of the bulk works. **Recommend yes.**
15. **D15. Rail drawn as rail:** sleepers and two rails close in, double track on the main line once the city has a Freight Line, a ticked dark line further out, and yards as fans of track with a shed and their cargo. **Recommend yes.**
16. **D16. Trains as the ships are:** calls from Rail's tonnes by kind ÷ a train's payload, from the gateway to a yard and back, a pure function of time, the boats' colours. **Recommend yes.**
17. **D17. Access roads** from every terminal, yard and depot to the nearest highway ramp (within two districts) or the arterial grid, paid from the road surface like any arterial. **Recommend yes.**
18. **D18. No level crossings on arterials:** an arterial or boulevard crosses the main line on a bridge; streets cross at level. **Recommend yes.**

**Junctions**
19. **D19. Junctions and interchanges drawn by rule, not built:** signals at arterials, roundabouts at boulevards, diamonds at ramps, a trumpet at a T, a cloverleaf where corridors cross (a stack where there is no room). **Recommend yes.** **The alternative**, an Interchange building on Build › Infrastructure, would cost money and ground but give the player no decision, because the map, not the player, places it (§4.6).
20. **D20. If the Elevated Highway's price leaves out its ramps, raise it about 30%** (a diamond every 1.92 km at about US$25.6M, against D$45.7M a km of highway, §4.6). **Recommend: check the price's source in FR2's brief first**, then raise it if needed. It moves traces.

**Optional**
21. **D21. Trucks drawn on the map** (FR5), one mark per N loads, near zoom only. **Recommend yes, last.**
22. **D22. The far view's skeleton:** highways, the main line, ports and yards drawn on far nodes. **Recommend yes,** in FR0.

---

## 9. Found on the way

1. **Imported tonnes pay their city leg twice:** once inside the band, once in the buyer's vans (§2).
2. **Vans have no drivers.** `Sector.runFleet()` wears and fuels them, and no post is created. That is about 9,750 vehicles with nobody in them in the 5d city.
3. **Rail and ships do not reduce a sector's vans.** `tonnesMoved()` reads nameplate only. The 5d write-up listed it as second order; for the pellet plant it was the whole story.
4. **I4's weighing is in `ProcessingSector` only.** Iron Mining (`ExtractionSector`) ships every tonne it digs and is not weighed with its lorries.
5. **Ports earn nothing and are never advised.** There are no fees, the crews are on the transit bill, and there is no measure, so the advice and automatic building never order a berth. Only the test player and the player do.
6. **An island city's trade still pays a lorry's long haul.** The band assumes a road to the world. With gateways (§4.1), a city with no land route would have to ship everything by sea. That belongs with the islands batch (I1), not here, but it should not be forgotten.
7. **The Industry tab probably shows the city's dot** because PORTS sits in it and not in `investorTypes()`, and BuildAdviceCheck strips PORTS before its own test, so it cannot catch it (`BuildAdviceCheck.java:299-307`). D11 removes the cause.
8. **Two stale comments.** `Rail.java:115` says 70%; the constant is .75. `Good.java:69` says `baseFreight` "is read by nothing"; `Rail.haul`, `GoodsMarket`, `Oil` and `Refining` read it.
9. **Rail's road relief is counted against boundary tonnes only** and applied to the whole bulk stream, which over-credits it where the city moves its own bulk (`InfrastructureManager.java:215-223`, which says so). FR2's city legs are the in-city freight matrix that comment asks for.
10. **Ships relieve the road like rail** (`railed = 1 - .75 × (rail + sea)`), but a ship's cargo still crosses the city from the quay by truck. Under FR2 the quay-to-door leg is a truck with a bill, so the relief should come from what the trucks actually carry.
11. **Yards are drawn as industry** because `classOf(RAIL)` falls through to INDUSTRY's purple. Transit networks are drawn as utility boxes. D15's TRANSPORT class takes both.
12. **Nothing of the network is drawn at FAR** (D22).
13. **The manual's catalogue says 101 buildings;** there are 108 since I4.
14. **The road does not know a truck from a car.**
   - On a freeway a heavy truck counts as two cars [R32], and it wears the road thousands of times more: a loaded 80,000 lb articulated truck is about 2.4 equivalent standard axles on asphalt, a car 0.0004 [R36][R37].
   - With Haulage's trucks counted, the road's repairs could follow the truck-kilometres, a mechanism rather than the flat `repairs` share each road carries now.
   - That is a later batch's question; nothing here depends on it.

---

## Sources

Figures from the research for this study (2026-10-11); a figure marked (est.) is derived from them or estimated. The game's own figures are from the code at 0.7.123 and the project's notes, named where used.

- [R2] ATRI, operational costs of trucking, 2025 (drivers per truck): https://truckingresearch.org/2025/07/new-atri-report-shows-trucking-profitability-severly-squeezed-by-high-costs-low-rates/
- [R3] FHWA, Highway Statistics 2024, table VM-1: https://www.fhwa.dot.gov/policyinformation/statistics/2024/vm1.cfm
- [R4] CBS (Statistics Netherlands), kilometres by vehicle type, 2024: https://www.cbs.nl/en-gb/figures/detail/85416ENG
- [R9] Tata Steel UK, axle weights and load distribution: https://tatasteeluk.com/sites/default/files/tis-0012-axle-weights-and-load-distribution.pdf
- [R11] FreightWaves, how much weight can a big rig carry: https://www.freightwaves.com/news/how-much-weight-can-a-big-rig-carry
- [R12] ICCT, total cost of ownership of long-haul trucks in the US, 2023: https://theicct.org/wp-content/uploads/2023/04/tco-alt-powertrain-long-haul-trucks-us-apr23.pdf
- [R15] Transport Engineer, 20 tonnes payload on an 8-wheel tipper: https://www.transportengineer.org.uk/content/features/sanders-woods-reports-20-tonnes-payload-on-new-daf-tipper
- [R16] Pit & Quarry, haul truck cycles: https://www.pitandquarry.com/?p=43142
- [R17] PierPass, drayage trucks at Los Angeles and Long Beach, 2010: https://www.pierpass.org/?p=463
- [R18] FleetOwner, truck turn times at Los Angeles and Long Beach, 2011: https://www.fleetowner.com/operations/article/21684494/ports-of-los-angeles-and-long-beach-aim-to-speed-up-truck-cargo-turns
- [R19] CCJ, class 8 truck prices, 2024: https://www.ccjdigital.com/regulations/article/15678767/how-high-will-class-8-truck-prices-go-with-epas-2027-regs
- [R21] TruckPaper, a 53 ft dry van trailer listing, 2026: https://www.truckpaper.com/listing/for-sale/257335871/2027-wabash-53-ft-x-102-in
- [R22] ACT Research, truck and trailer replacement cycle: https://www.actresearch.net/resources/knowledge-center/what-is-the-truck-trailer-replacement-cycle
- [R23] ICCT, fuel consumption of tractor-trailers in the EU and the US: https://theicct.org/publication/fuel-consumption-testing-of-tractor-trailers-in-the-european-union-and-the-united-states
- [R25] Vans Direct, large van payloads, 2025: https://www.vansdirect.co.uk/5-best-large-vans-payload/
- [R28] IRU, EU road freight volumes, 2026: https://www.iru.org/sites/default/files/2026-06/IRU%20Briefing%20preview%20-%20A%20breakdown%20of%20the%20latest%20EU%20road%20freight%20volumes%20and%20forecast%20to%202030.pdf
- [R29] BTS, Commodity Flow Survey 2012, table 1c: https://www.bts.gov/archive/publications/commodity_flow_survey/2012/united_states/table1c
- [R30] CSO Ireland, Road Freight Transport Survey 2017: https://www.cso.ie/en/releasesandpublications/ep/p-rfts/roadfreighttransportsurvey2017
- [R32] Highway Capacity Manual 7th ed., basic freeway segments (study guide): https://open-exam-prep.com/study-guides/ptoe/capacity-analysis-highways/basic-freeway-segments
- [R36] FHWA, Comprehensive Truck Size and Weight Study, working paper 3: https://www.fhwa.dot.gov/reports/tswstudy/TSWwp3.pdf
- [R37] OOIDA, truck and car pavement damage, 2021: https://www.ooida.com/wp-content/uploads/2021/11/Myth-One_Truck_Does_More_Damage_than_9600_AutomobilesOne_Pager.pdf
- [R39] EIA, coal unit trains, 2013: https://www.eia.gov/todayinenergy/detail.php?id=16651
- [R40] Progressive Railroading, Class I train lengths, 2017: https://www.progressiverailroading.com/bnsf_railway/article/Class-I-railroads-continue-the-longer-train-trend--55035
- [R45] UIRR, optimal length of freight trains: https://www.uirr.com/web-news/optimal-length-freight-trains
- [R46] Double-stack rail transport (Wikipedia): https://en.wikipedia.org/wiki/Double-stack_rail_transport
- [R47] Port of Hamburg magazine, ore and coal block trains, 2020: https://www.hafen-hamburg.de/assets/files/magazin/poh42020_en/files/basic-html/page9.html
- [R49] Cambridge Systematics for the AAR, rail line capacity, briefing to the STB: https://www.stb.gov/wp-content/uploads/files/docs/railEnergyTransportationAdvisoryCommittee/Cambridge%20Systematics%20briefing.pdf
- [R50] BTS, National Transportation Statistics table 3-21 (revenue per ton-mile): https://www.bts.gov/archive/publications/national_transportation_statistics/table_03_21
- [R51] DAT, truckload rates, 2025: https://www.dat.com/company/news-events/news-releases/dat-typical-may-truckload-rates-volumes-masked-tariff-turmoil
- [R57] Research in Transportation Economics vol. 90 (2021), drayage in intermodal transport: https://ideas.repec.org/a/eee/retrec/v90y2021ics0739885920300822.html
- [R58] Universidad de Sevilla, drayage cost share, 2013: https://idus.us.es/handle/11441/53605
- [R67] UIRR, North Sea ports' modal split: https://www.uirr.com/web-news/german-rail-development-indicates-shifts-north-sea-ports-modal-split
- [R69] FreightWaves, Long Beach on-dock rail: https://www.freightwaves.com/news/rail-long-beach-on-dock-rail-project
- [R70] Port of Montreal railway (Wikipedia): https://en.wikipedia.org/wiki/Port_of_Montreal_railway
- [R71] Fundación Valenciaport, port capacity: https://www.fundacion.valenciaport.com/wp-content/uploads/2020/06/Capacity_EN-1.pdf
- [R73] Learn Bulk, import bulk terminal operations: https://learnbulk.substack.com/p/bulk-terminal-operations-part-1-import
- [R74] Maritime Gateway, Paradip mechanised multipurpose berth, 2025: https://www.maritimegateway.com/paradip-port-trust-floats-tender-for-mechanisation-of-multipurpose-berth/
- [R78] Chatham County–Savannah MPO, Little Neck interchange alternatives, 2019: https://www.thempc.org/eagenda/x/mpo/2019/october-30-2019-meeting-of-the-core-mpo-board/vhb_littleneck_finalalternatives_report-mpo-comments_1.pdf
- [R82] SCDOT Roadway Design Manual, chapter 10, interchanges: https://scdot.org/business/pdf/roadway/revisedChapters/Chapter%2010%20-%20Interchanges.pdf
- [R84] Equipment World, Oklahoma's four-level I-235/I-44 interchange, 2022: https://www.equipmentworld.com/better-roads/article/15289388/oklahoma-opens-fourlevel-i235i44-interchange
- [R85] Interstate 10–Loop 1604 interchange (Wikipedia): https://en.wikipedia.org/wiki/Interstate_10%E2%80%93Loop_1604_interchange
- [R86] MVRPC, ramp analysis methodology (HCM 2000 ramp capacities): https://www.mvrpc.org/sites/default/files/4.4_Ramp_Analysis_Methodology.pdf
- [R87] WSDOT Design Manual M 22-01, chapter 1360, interchanges: https://wsdot.wa.gov/publications/manuals/fulltext/M22-01/1360.pdf
- [R91] J.-P. Rodrigue, The Geography of Transport Systems, public and private roles in port management: https://transportgeography.org/contents/chapter6/port-terminals/public-private-roles-ports/
- [R92] World Bank / PPIAF, Port Reform Toolkit, module 3: https://ppiaf.org/sites/ppiaf.org/files/documents/toolkits/Portoolkit/Toolkit/module3/index.html
