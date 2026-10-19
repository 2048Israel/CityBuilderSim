# FR2 - the Freight sector: Haulage's trucks carry the city leg, vans carry goods only

The brief for batch FR2 (2026-10-18). `QUEUE.md` links the spec; this file is
what the batch builds, with every number it uses and where each comes from.
The spec is `docs/studies/spec-freight-and-infrastructure.md`: §7's FR2 row is
the batch, §2 the rule, §3 the design, §8 the decisions, §9 the findings; the
study's pages are `docs/studies/freight/2-freight-sector.html` and
`3-how-its-tonnes-move.html`. Jerus answered every decision on 2026-10-12:
"all the freighting just do rec". Every recommendation stands; where the spec
leaves a choice open, the call is marked ★ below and argued in a line.

FR3 (ports as a landlord port) and FR4 (the screens) follow. FR2 builds the
model and the save, and only the screen lines the model needs to be read.

## 1. The rule, as built (§2)

- **A tonne moves in legs, and each leg is paid once.** The long haul is as
  today: the world's lorries and ships inside the band, the city's railway on
  its invoice. **The city leg** (gateway, yard or quay to the door, or door to
  door in the city) is a service Freight's Haulage division bills per tonne
  through `Sector.billForService`, as the railway bills its haulage.
- **Who pays (D2).** The buyer pays the city leg of every BULK tonne it
  receives, from the city or from abroad. The exporter pays the city leg of
  every BULK tonne it ships abroad. A tonne sold at home is paid by its buyer,
  never by its seller. A works' captive goods (Iron & Steel's coke and pig
  iron) move on its own rails and are not a leg.
- **★1 Haulage carries the BULK stream; vans carry the GOODS stream.** §3.5
  gives the van rule as `vansNeeded = goods tonnes / 120` and §3.2 the freight
  factor as "vans for a sector's goods, haulage for its bulk"; the stream is
  `Good.traffic()`. A GOODS tonne keeps today's 5d rule exactly, both sides of
  the door at nameplate, in the sector's own vans. (§3.2's "bulk before goods"
  dealing has only bulk to deal; goods by truck is a later call if Jerus
  wants it.)
- **The siding (§3.3).** Of the tonnes the railway carries across the
  boundary, `InfrastructureManager.RAIL_ROAD_RELIEF` (.75) go door to door by
  siding and the rest by truck: a railed tonne's city leg is a quarter of a
  truck tonne. A tonne by sea has no relief: quay to door is a truck (a port
  yard is D7, with FR6).
- **Money is conserved.** Every Haulage dollar is a dollar on a shipper's
  input line ("Haulage") and on Freight's other revenue; the trucks' diesel
  is drawn as the railway draws its own (`Markets.draw()`).

## 2. The numbers

Sources are the spec's (its Sources list; [R#] as there). (est.) marks a
figure derived or estimated, and the line says from what.

### TRUCKS, the good

| figure | value | source, and the choice |
|---|---|---|
| unit | "truck" | a tractor and semitrailer (§3.2) |
| payload a load | 25 t | an EU 40 t articulated truck [R9]. A North American 80,000 lb truck carries about 20 t [R11], a tipper 20 t [R15]: the spec takes the EU figure, and so does this. |
| weight (`tonnesPerUnit`) | 15 t | its tare: 40 t gross less 25 t payload [R9] (est.) |
| import price, world money | 215.0 (D$215k at parity) | a tractor US$158k-195k [R12][R19] and a trailer US$45k-60k [R21]: US$203k-255k; the spec's D$215k |
| export price | none (NaN) | D5: trucks import and do not export, as vans and wagon sets (`Good.java`, VANS' comment: an unbounded export market at a fixed floor builds the plant for ever) |
| freight (`baseFreight`) | 13.4375 | 6.25% of its price, the share VANS (4.5 of 72) and ROLLING_STOCK (150 of 2,400) carry (est.) |
| stream, cargo | GOODS, GENERAL | as VANS: it drives in on its own wheels |
| life | 120 months | `Sector.VAN_LIFE_MONTHS`: a first owner keeps a tractor 3-5 years and its life is about 15 [R22][R12], so ten is a fleet's average (§3.2) |
| `since` year | not built | §3.2 says the research batch wires it; `Good` has no `since` field yet |

### Haulage, the division (`sectors/Haulage.java`)

| dial | value | source, and the choice |
|---|---|---|
| `TONNES_A_LOAD` | 25 | above |
| `TONNES_A_TRUCK_MONTH` | 3,000 | a city leg is `Sector.KM_A_LOAD` (50 km there and back); a cycle about 1.5 h (est.: port trucks make 3-4 moves a day with a 51-minute terminal visit [R17][R18]; quarry tippers 16-18 loads a shift on a short haul [R16]); 6-7 loads in a ten-hour shift at 25 t and 22 days is 3,300-3,850 t, and the spec rounds down to 3,000 on one shift. Dutch lorries of 20 t and more average 3,500 km a month [R4], about 1,500 t, the low end: the spec's 3,000 is taken. So 120 loads and 6,000 km a truck-month. |
| `DIESEL_LITRES_PER_100_KM` | 33 | a laden tractor-trailer [R3][R23], against the van's 12 (`Sector.DIESEL_LITRES_PER_100_KM`) |
| diesel a tonne carried | .66 L | 50 km / 25 t x 33 L / 100 km, drawn on the tonnes it carried |
| drivers | one a truck kept at a depot | US fleets run 0.93 drivers a truck [R2]; one a truck on one shift (§3.2). ★2 below: a driver is a post on the depot that keeps the truck. |
| `STREET_WEAR` | 1.10 | D6: trucks past the depots' room park on the street at +10% upkeep (est.); built as wear, a tenth faster |
| price rule | `Freight.TARGET_RETURN` .012 a month, `MAX_SCARCITY_MULTIPLE` 1.6, `REPRICE_SPEED` .25 | Rail's rule on Haulage's books (§3.2): running cost (the depots' payroll, power, water, repairs and tax, the diesel, the trucks worn) plus 1.2% a month on its rate base (trucks, depots, their ground), over the tonnes carried, times the scarcity, a quarter of the way a month. No ceiling but the rule (§3.2: the lorry rate is a long-haul cap). |
| the throttle | `Sector.MIN_VAN_RATE` .6 | the van rule moved across (§3.2): a sector whose bulk is not all carried runs at .6 + .4 x the carried share |
| buying | `Sector.FLEET_DELIVERY_MONTHS` 8 | the van rule: at most an eighth of the need a month (§3.2) |
| the siding | `InfrastructureManager.RAIL_ROAD_RELIEF` .75 | §3.3; one number, where it is |

What a truck-month comes to at parity (§3.2, est.): D$1.8k of wear, D$1.4k of
diesel (1,980 L at DIESEL's D$0.69), D$2.6k of return, a driver (D$5k), a
share of a depot (D$1.4k of return and D$1.3k of mechanics): about D$13.5k,
D$4.50 a tonne. The spec's D$4 left the depot out.

### The Truck Depot (id 139, Freight's, new `BuildingType.HAULAGE`)

| field | value | source, and the choice |
|---|---|---|
| trucks kept (`truckRoom`, new field) | 100 | §3.2, D6 (est.) |
| `cashCost` | 12,000 (D$12M) | §3.2 (est.) |
| `land` | 430,000 sq ft | about 4 ha (§3.2, est.) |
| jobs | DIPLOMA 122, COLLEGE_ENGINEERING 3 | 100 drivers (★2) and about 25 mechanics (§3.2, est.), three of them the fleet's engineers; drivers on the DIPLOMA tier: a licence and a school, not a degree (est.) |
| `electricity`, `water` | 150, 10 | a workshop and a wash bay (est.) |
| `constructionPoints`, `constructionMaterials` | 1,320, 290 | the Commercial Vehicle Plant's .11 and .024 of its cash (the game's own ratios) |
| `roadLoad` | 110 | its posts at the vehicle plants' ratio, about .9 a post (290 for 330 posts); the trucks' own load is the bulk stream's, as now |

### The Heavy Truck Plant (id 140, Automotive)

The spec gives no figures. They are the Commercial Vehicle Plant's (id 67)
scaled by its output's value (est.): 6 trucks at D$215k is D$1.29M a month,
against 150 vans at D$68.1k, D$10.2M, so 0.126 of the van plant.

| field | value |
|---|---|
| makes | TRUCKS 6 a month |
| uses | FABRICATED_STEEL 60 (10 t a truck), MACHINERY 12 (2 t: engine, gearbox, axles), LUBRICANTS 240 (40 L) (est.: 15 t of tare) |
| `cashCost` 16,600; `constructionPoints` 1,830; `constructionMaterials` 400 | 0.126 of 132,000; 14,500; 3,170 |
| jobs | NO_DIPLOMA 25, DIPLOMA 13, COLLEGE_ENGINEERING 3, UNIV_SCIENCE 1 (42; 0.126 of 330) |
| `electricity` 150, `water` 11, `land` 120,000, `roadLoad` 37, `stock` 18 (three months) | 0.126 of the van plant's |

**Why six a month.** A mature playtest city keeps about 400-500 trucks (§3.7
scaled to its 1.2 Mt a month), so it wears out 3.3-4.2 a month: the plant
runs at 55-70%, as the Locomotive Works runs at about half (`Rail`'s
`TONNES_PER_SET` comment). A larger plant would never be built; the world
sells trucks to a city without one.

### D20, the Elevated Highway's price: checked, not raised (★3)

§8 asks FR2's brief to check where the Elevated Highway's D$9.6M (id 30)
comes from before any raise. It is not a cost a km with or without ramps: it
is set by the three roads' crossovers, each road the cheapest a trip across a
band of land prices (BuildingManager, THREE ROADS: $16.50 and $28.50 a square
foot, asserted by InfrastructureCheck). Nothing in it can leave the ramps out.
A 30% raise would move the upper crossover to about $37 and unbalance the
three roads. FR2 leaves it; a roads batch may revisit it with the crossovers.

## 3. The design in the code

- **The slot (§3.1, D1).** `sectors/Rail.java` becomes `sectors/Freight.java`
  (git mv): the class `Freight`, key and label "Freight", in `Sectors.KEYS`
  position 12 in place (`Sectors.FREIGHT`; `rail()` becomes `freight()`).
  Its railway code stays as it is, under a banner THE RAIL DIVISION; the
  railway's bill line on the shipper is renamed "Rail freight", since
  "Haulage" is now the trucks'. The railway's posts are its track's; its
  price rule now reads the rail division's share of each cost line and of the
  rate base (all of it while no depot stands).
- **Haulage** is a division class owned by Freight, not a Sector: its own
  figures, quote, fleet, need and price rule, booking through Freight (the
  fleet is Freight's TRUCKS pantry). Ports, the third division, is FR3's.
- **The month (`Freight.haul()`, from `Game.chargeFreight()`, unchanged in
  place).** In the loop that bills the railway, at the shares in force while
  the cargo moved: each sector's city-leg tonnes of last month (its BULK
  units bought at home, imported and exported, the railed part of the last
  two a quarter of a truck tonne), billed at Haulage's quote for the tonnes
  Haulage carried - the carried share in force, and never more than the
  trucks it had could carry. Then the trucks' diesel on what they carried,
  their wear (a tenth faster past the depots' room), and the coming month:
  the need is last month's city-leg tonnes, carried and collected
  ("carried and refused", §3.2); the carried share `min(1, trucks x 3,000 /
  need)`, exactly 1 when the trucks suffice; the reprice; and the share
  pushed to every sector, which reads it in `getOperatingRate()`.
- **The freight factor (§3.2).** `getOperatingRate()`'s fifth factor is
  `getFreightRatio()`: the lesser of the van ratio (vans for the sector's
  GOODS tonnes) and the haulage ratio (.6 + .4 x the carried share, for a
  sector with any BULK input or output at nameplate; 1 for one with none,
  and for Freight). Both are a literal 1 when nothing binds.
- **The fleet (§3.2).** Freight bids for TRUCKS as a sector bids for vans:
  `min(need - have, need / 8)` trucks, the need being the need tonnes over
  `TONNES_A_TRUCK_MONTH`. Seeded once, the first month the city has a city leg
  to carry, at the need, free, as vans and wagon sets were seeded
  (`trucksKnown`, saved).
- **The depot (D6, §3.2's planner).** Freight's plan orders a Truck Depot,
  before any rail line, when its fleet passes its depots' room by a tenth of a
  depot or more, and none is on site; staffing and ground gate it as any
  order. ★2: the trucks a depot keeps are crewed by its posts; trucks past
  the room still run, unposted, at `STREET_WEAR`.
- **Vans after Freight (§3.5).** `Sector.tonnesMoved()` counts the GOODS
  stream only (and leaves TRUCKS out with VANS): the vans move goods.
- **I4's weighing on freight (§3.5, §9.4).** `ProcessingSector` and
  `ExtractionSector` weigh one more works with its freight: the vans for its
  GOODS tonnes (their wear and diesel; their capital in its cost), and its
  BULK city legs at the rate - every tonne in, and the share of every tonne
  out the city exported of that good at the last clearing - at Haulage's
  quote, the railed part relieved by the siding. No truck appears in its
  capital. With no quote yet, the estimate of a truck-tonne at today's prices.
- **The save.** Haulage's figures are Freight's extras; a sector's units
  bought at home carry across a save as the A1 trade does (`SectorState`), so
  the first bill after a load is the month's. `SAVE_FORMAT` 39 -> 40 and
  `OLDEST_LOADABLE_FORMAT` to 40 (the sector's key changes and a save's
  sectors would load wrongly); Jerus: "my saves never matter".
- **The screens (FR4 has them).** Only: Freight's own lines gain Haulage's
  (trucks against the need, depots' room, the quote, tonnes billed, the bill);
  the fifth ratio's label reads "Freight" where a sector's ratios are listed;
  the Build tab "Rail" lists the Truck Depot; the map draws the depot in the
  TRANSPORT class. `BuildingVisual` is the only map file touched beyond the
  rename (FX3 owns the rest).
- **Version.** The next patch after main's (0.8.4, or 0.8.5 if FX3 ships
  first): GameVersion's minor numbers mark the end of a whole area (0.8.0
  finished industry), and freight runs to FR6.

## 4. ★ calls

1. **Haulage carries BULK, vans GOODS** (§1 above).
2. **A driver is a post on the depot that keeps the truck.** The labour
   market counts posts on buildings only (`BuildingManager.getJobArrayBySector`);
   a post without a building would pay households for work no building
   offers. So a depot is 100 trucks' drivers and 25 mechanics. D6 stands:
   trucks past the room are not stopped, and run at +10% wear; the planner
   builds a depot as soon as they pass it, so in a steady city every truck
   has its driver.
3. **D20 not raised** (§2 above).
4. **The need is last month's legs, not nameplate.** §3.2 says "carried and
   refused"; a nameplate need would have to guess each output's export share
   and each input's railed share before the month runs. The van rule's
   nameplate stays for vans.
5. **One carried share for the city**, as the railway's per stream: every
   sector with bulk runs at the same haulage ratio; the bill is per sector.
6. **The plant at six a month** (§2 above).

## 5. The slices

Each to a Sonnet 5.5 subagent at xhigh (DeepSeek's key not being set on the
PC; Jerus, 2026-10-18), the brief in `target/deepseek/brief-FR2-<n>.md`,
reviewed by Claude as a stranger's diff and committed by Claude. The design,
the month's order, `SAVE_FORMAT`, GameVersion, the trace and the notes are
Claude's.

1. **The rename.** Rail -> Freight everywhere the type, the key or the
   accessor is named (the model, the screens, the harnesses, `buildings.json`
   and the built-ins); the railway's bill line "Rail freight". Nothing moves
   but names: RailCheck and SaveFileCheck green.
2. **The good and the buildings.** TRUCKS (last in `Good`), `BuildingType.HAULAGE`
   (last), `truckRoom` on `BuildingsTemplate` and in `BuildingCatalog`, ids 139
   and 140 in `buildings.json` and the built-ins, the depot's TRANSPORT class
   in `BuildingVisual`, the Build tab's category. BuildingDataCheck green.
3. **Haulage.** `sectors/Haulage.java`, Freight's wiring in `haul()`, the
   bid, the seeding, the depot in `plan()`, the cost split, the extras; the
   Sector side (`tonnesMoved()` on goods, the haulage and freight ratios,
   units bought at home and their carry). HaulageCheck.
4. **The weighing.** `ProcessingSector`, `ExtractionSector`, their harnesses'
   lorry sections moved onto freight. HaulageCheck's weighing section.
5. **The read path and the screens' lines.** The load path's reapply,
   `SectorState`, SaveFileCheck and ReadPathCheck; Freight's own lines,
   SectorFlow's fifth ratio. VanCheck to goods only.

## 6. HaulageCheck - what it has to prove

Fixtures in `GameFiles.scratch()` cities, each causing its condition; every
assertion against the model's constants.

1. **The slot.** `Sectors.KEYS[12]` is "Freight", the length unchanged;
   Freight is the railway (a fixture railway's quote and bill as RailCheck's).
2. **TRUCKS.** It imports at its price and never exports; it weighs
   `tonnesPerUnit()` 15; the Heavy Truck Plant alone makes it.
3. **Each leg once.** A city with a mine selling ore at home to a mill, the
   mill importing coal and exporting steel: the mill pays the ore it bought
   and the coal it landed and the steel it shipped; the mine pays nothing for
   the ore it sold at home; the bill is the legs' tonnes x the quote; Freight's
   "Haulage" revenue is the shippers' "Haulage" lines to the cent.
4. **The siding.** The same city with the railway carrying a share r of the
   imports: their truck tonnes fall by `RAIL_ROAD_RELIEF` x r; a sea share
   relieves nothing.
5. **Vans for goods.** A bulk works keeps no vans; a goods sector keeps its
   goods tonnes over `TONNES_PER_VAN`.
6. **The freight factor.** Trucks short: a bulk sector runs at `MIN_VAN_RATE`
   + (1 - `MIN_VAN_RATE`) x the share; trucks enough: a literal 1; a goods-only
   sector untouched; the lesser of the van and haulage ratios.
7. **The fleet.** The bid, the wear (and `STREET_WEAR` past the room), the
   seeding once and never again, the bill never past what the trucks carried.
8. **The price.** The quote walks `REPRICE_SPEED` toward the rule's target,
   the scarcity at `MAX_SCARCITY_MULTIPLE` when the need doubles the fleet.
9. **The depot.** Ordered when the fleet passes the room, not before; its
   posts are its trucks' drivers and its mechanics.
10. **The weighing.** A Pelletising Plant on imported ore keeps no vans in its
    cost, and its freight is its legs at the quote; a railed share lowers it
    by the siding; an Iron Mine is weighed the same way.
11. **A real run.** `LongPlaytest`'s fixture city a few hundred months: how
    often the haulage ratio bites, the depots built, the trucks against the
    need; printed, and the mechanic must fire.

## 7. What moves, and what is not built

- **Every trace moves** (§7): the van fleet falls to the goods' few hundred,
  bulk works lose their vans' capital and pay Haulage, the Commercial Vehicle
  Plant loses most of its market, drivers' posts appear. Re-baselined; the
  ensemble run and read against the reference (FR2-notes).
- **Not built here:** the Ports division and the quay's handling (FR3); the
  railway carrying the city's own bulk (D4, FR6) and the port yard (D7); the
  road's relief from what the trucks carry (§9.10) and trucks on the road's
  load (§9.14); GOODS by truck; the city's own purchases' city legs (the
  government is not a sector); `since`; trucks drawn (FR5); every screen of
  §6 (FR4).
