# FR2 - the Freight sector: notes

*0.8.5, 2026-10-18/19. The freight batch that changes the model (`docs/studies/spec-freight-and-infrastructure.md` §7's FR2 row; §2's rule, §3's design; decisions D1-D6, Jerus 2026-10-12: "all the freighting just do rec"). The brief, with every number and its source, is [FR2.md](FR2.md). The slices were Sonnet 5.5 subagents' (DeepSeek's key not being set on the PC), each reviewed by Claude; the review changed the design in six places (§2), the suite's reds found two latent bugs outside FR2 (§3), and every trace moves (§4). The model measured is commit **b4c73d2**; what came after it touched harnesses only. Save format 40, the floor with it.*

## 1. What shipped

- **Freight in Rail's slot** (`sectors/Freight.java`, `Sectors.FREIGHT`, `freight()`): key "Freight", `Sectors.KEYS` position 12 (index 11) in place. Its railway is the Rail division, as built; its bill on the shipper is "Rail freight", since "Haulage" is the trucks'. The railway's price rule reads the Rail division's share of each cost line and of the rate base: all of it while no depot stands.
- **Haulage** (`sectors/Haulage.java`), Freight's second division (Ports is FR3's):
  - **the city leg, each paid once** (`legTonnes()`): a BULK tonne bought at home is a truck tonne, paid by its buyer; an import or export is one less the siding's share of its railed part (`RAIL_ROAD_RELIEF` .75); sea relieves nothing. Billed at the top of the month in `Freight.haul()`, beside the railway's bill, at the share in force and the quote, never past what the trucks could carry.
  - **the need** (`nameplateNeed()`): the plant standing, at nameplate, each tonne weighted as the leg price weights it; **the order** toward it and the plant on site (`trucksAsked()`), an eighth a month at most. **The share** (`getShare()`): struck once at each haul, over the fleet held before the month's wear. **The freight factor** (`Sector.getFreightRatio()`): the lesser of the vans' ratio (GOODS) and .6 + .4 x the share, for a sector with any BULK input or output.
  - **the fleet**: TRUCKS in Freight's pantry, seeded once at the need, worn over 120 months and a tenth faster past the depots' room; diesel drawn as the railway's, 33 L a 100 km.
  - **the price**: the railway's rule on Haulage's books - the depots' share of Freight's payroll, power, water, repairs and tax, the diesel, **the trucks bought**, plus 1.2% a month on the trucks, the depots and their ground - over what the trucks **could** carry, times the scarcity, a quarter of the way a month.
  - **the Truck Depot** (id 139, `BuildingType.HAULAGE`): 100 trucks, D$12M, 4 ha, 100 drivers and 25 mechanics; ordered when the street holds half a depot's trucks; never retired by the railway's measure. **Drivers follow the trucks** (`strikeDrivers()`, BuildingManager's offered share by job type).
- **TRUCKS** (last in `Good`): D$215k, 15 t, imports only, GENERAL cargo; **the Heavy Truck Plant** (id 140): six a month.
- **Vans carry goods only** (`Sector.tonnesMoved()`); **a works is weighed with its freight** (`ProcessingSector.freightRunning()`, `cityLegs()`, `Haulage.legPrice()`; `ExtractionSector.freightRunning()`), no truck in its capital.
- **The save**: Haulage's state in Freight's extras (`haul.*`: the quote, the share, the need and the need on site, the trade's shares, the drivers' share), units bought at home carried across a save (`SectorState.boughtLocal`). `SAVE_FORMAT` 40 and `OLDEST_LOADABLE_FORMAT` 40.
- **The screens, as few as FR4 leaves**: Freight's page shows its trucks; the fifth throttle reads "freight"; the depot on Build's Rail tab with its own card group; the refinery's picture counts the trucks' diesel. LongPlaytest counts Haulage over the run.
- **Harnesses**: HaulageCheck (new: ten sections and a reload, written to the brief by a second subagent as a stranger to the code); twenty-odd harnesses moved onto the new rules or their fixtures restored (§3).

## 2. ★ calls

The brief's, as Jerus's "rec" left them, and the review's:

1. **★1 Haulage carries the BULK stream; vans the GOODS stream** (spec 3.5's formula).
2. **★2 A driver is a post on the depot that keeps the truck** (posts exist only on buildings); trucks past the room run unposted at +10% wear (D6). **Review:** a depot offers drivers only for the trucks it keeps - the brief's depot paid a hundred drivers for eleven trucks.
3. **★3 D20, the Elevated Highway's price, checked and not raised**: it comes from the three roads' crossovers, not a cost a km.
4. **★4 REVERSED: the need is the plant standing at nameplate**, as the vans' is, and the order also toward the plant on site. The brief's last-month legs seeded a town built at once on its first month's few hundred tonnes; bought only once a works stood, a growing town's bulk ran short four months after each opening (HealthCheck's twins: 19 of 96 months, to .69).
5. **★5 One carried share for the city**, as the railway's per stream (Pending: it makes a giant works' arrival everybody's shortfall).
6. **★6 The Heavy Truck Plant at six a month.**
7. **★7 (review) A depot when the street holds half a depot's trucks** (`DEPOT_ORDER_MARGIN` .5, not .1): at a tenth the first depot opened at eleven trucks and priced the leg at six times the trucks' cost for two years.
8. **★8 (review) The price over what the trucks could carry**, not what they carried: a seeded fleet priced a month's dozen tonnes at D$5,000 a tonne.
9. **★9 (review) The share and the trade's shares struck at the haul and saved**: read live, the share moved inside a month (a refinery bought crude at one rate and made products at another); read off the markets, a reload struck another need.
10. **★10 (review) The price counts the trucks bought, not the trucks worn.** Freight's books expense a truck when it is bought; priced at its wear, a growing fleet was a loss every month, borrowed for with no plant to borrow on before a depot: on one path the playtest's Freight was an empty company in debt from month 936, its fleet orders refused ($3.87T), and the city stalled at 30,000 people for 3,000 months. The shippers now pay for the fleet as it grows, as each paid for its own vans.

## 3. The slices, and what the review changed

| slice | by | what the review changed |
|---|---|---|
| 1, the rename (39 files) | Sonnet | nothing |
| 2, TRUCKS, the depot, the plant (22 files) | Sonnet | the TRUCKS banner's "nothing buys it yet"; buildings.json's final newline. Its additions kept: `Automotive.LUBRICANT_LITRES_A_TRUCK`, the "Truck depots" card group |
| 3, Haulage (421 lines, Freight, Sector) | Sonnet | ★2, ★4, ★7-★10; RailCheck on the railway's own figures (`Freight.getRunningCost()`) |
| 4, the weighing | Sonnet | nothing; WoodProductsCheck's HELD tail fixed by Claude |
| VanCheck | Sonnet | nothing (department stores and meat works in its fixture) |
| HaulageCheck | Sonnet | nothing; ★4 and ★9 landed under it, and its bid section moved to the order's target |
| the suite's reds | Claude, Sonnet diagnosing | named service lines in SectorBooksCheck and BondCheck; the trucks' diesel in OilCheck, RefineryView, RefineryViewCheck; the floor in SaveFileCheck, EarthTerrainCheck, MaterialsCheck; SectorFlowCheck's sixth throttle; ScaleCheck's scaler (Haulage's prefixed keys; the unemployment array); fixtures FR2 moved, each still caused: PolicyPreviewCheck (700 houses), AutoBuildCheck (the copy given people), HealthCheck (the twins' trucks kept up), CreditCheck (the ban re-armed), BankCheck (Freight's cash), CentralBankCheck (a world price fall causes the loose twin's months under target) |
| latent bugs FR2's paths reached | Claude, Sonnet diagnosing | **`Mortgage.Slice.of`** tested the down payment with no tolerance where `canFundMortgage()` allows 1e-9: a landlord raises equity to the payment exactly, and a ten-trillionth decided whether a city built and its reformed twin refused (DenominationCheck; OrderSearchCheck's count with it). **`FundView.search`**'s order was no order (one issuer's bonds by maturity, every other pair by size): TimSort threw on ReadPathCheck's city. **IndustryPicture**'s "OUT OF THE WAREHOUSE" ran 137 px in a 98 px room: "FROM STOCK" |

## 4. What moved in the trace

Against main at 1213dbe (0.8.4), the model at b4c73d2. Both runs: "Nothing. Every month passed the audit and every reload matched." Every file moves from month 4 (month, bank, bonds), 5 (build, house) and 29 (pop, labour): the first hauls with a city leg.

| | main | FR2 |
|---|---|---|
| default, end | m4001: 544,052 people, GDP $6.0T a month at a price index of 1,655 | m4001: 673,357, $4.4T at 1,157 (real GDP +5%) |
| default, m1074 / m2025 | 10,581 / 72,604 | 10,740 / 108,029 |
| Earth (Rotterdam), end | m4005: 812,101, $18.4T at 3,414 | EARTH_END |
| Earth, m1070 / m2020 | 22,371 / 152,373 | EARTH_MID |

**Haulage over the run** (LongPlaytest's line): default - short of the city's legs in 619 months, **14 under 90%**, at worst 53% carried (m1156); 753 trucks and 700 depot places at most; $4.02T billed for 2.04 Gt; the last quote $4,250 a tonne against $1,909 estimated (price index 1,157: about D$3.70 a tonne at founding prices). EARTH_HAUL

**Why.** The bulk works lost their vans' capital and diesel (a Pelletising Plant's 64%) and pay a city leg at a few dollars a tonne instead; the van fleet falls to the shops' and kitchens' few; drivers and mechanics are posts. The short months are mostly the struck share reading a growing plant a month late (under 1%); the deep ones are a giant works landing, every bulk sector sharing the shortfall (★5) until its trucks arrive. A single run swings a long way with the city's path - the model's earlier drafts ended this default run at 787,393, 526,648 and (stalled, ★10) 30,614 - so the ensemble (§5) is the measure.

## 5. The ensemble

ENSEMBLE_PLACEHOLDER

## 6. The suite

SUITE_PLACEHOLDER

## 7. Not built, and what is left

- **Not this batch's** (the spec's later rows): the Ports division and the quay's handling (FR3); the railway carrying the city's own bulk (D4) and the port yard (D7) (FR6); every screen of spec §6 (FR4); trucks drawn (FR5); a truck's `since` year.
- **Not built, noticed**: the road still relieves rail and sea shares at `RAIL_ROAD_RELIEF` and does not read the trucks' tonnes (spec §9.10, §9.14); GOODS go by vans, never by truck; the government's own purchases pay no city leg; Construction is billed for its materials' legs but never throttled (its materials are not declared uses).
- **For Jerus (Pending, FR2-C)**:
  1. **One share for the city (★5)**: a giant works landing slows every bulk sector for the months its trucks take. Per-sector dealing (the newest demand last) is the alternative.
  2. **The review's reversals of the brief**: ★4 (the need at nameplate, the order toward the works on site), ★7 (a depot at half a depot of trucks), ★10 (the price on the trucks bought).
  3. **A village with little ground**: AutoBuildCheck's 600-month village ends at 1,571 people against main's 13,608 - Agriculture, its vans gone, built a third Mixed Farm onto its last ground (its fixture now gives the copy its people back).
  4. **ScaleCheck stays red** (worst pull 0.012 against 0.01): its copy does not scale the founding care and police endowments (code constants for 1,200 people, not saved; the harness's own "WHAT IT STILL LOSES"), and FR2's fixture city is small enough at month 602 (2,188 people, 5,567 before) for the endowment to be half its care. Scaling both by K in a scratch tree gave a pull of 1.0000 every month. Closing it needs a saved `foundingScale` - a model and save change, its own small batch.
  5. **The ensemble's runner** lives in `workbench/workbench-delta-20261010.zip`, written for the cloud; FR2 ran it through an adapted `runpc.sh` (';' classpaths, `target/classes`, gson 2.10.1, `python -I` with pandas installed into a scratch folder). Whether it belongs in the tree is the orchestrator's call.
