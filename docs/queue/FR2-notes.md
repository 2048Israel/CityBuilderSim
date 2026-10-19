# FR2 - the Freight sector: notes

*0.8.5, 2026-10-18/19. The freight batch that changes the model (`docs/studies/spec-freight-and-infrastructure.md` §7's FR2 row; §2's rule, §3's design; decisions D1-D6, Jerus 2026-10-12: "all the freighting just do rec"). The brief, with every number and its source, is [FR2.md](FR2.md). The slices were Sonnet 5.5 subagents' (DeepSeek's key not being set on the PC), each reviewed by Claude; the review changed the design in seven places (§2), the suite's reds found two latent bugs outside FR2 (§3), and every trace moves (§4). The model measured is commit **1f573f6** (the gate's round, §2's ★11); what came after it touched no model code. Save format 40, the floor with it.*

## 1. What shipped

- **Freight in Rail's slot** (`sectors/Freight.java`, `Sectors.FREIGHT`, `freight()`): key "Freight", `Sectors.KEYS` position 12 (index 11) in place. Its railway is the Rail division, as built; its bill on the shipper is "Rail freight", since "Haulage" is the trucks'. The railway's price rule reads the Rail division's share of each cost line and of the rate base: all of it while no depot stands.
- **Haulage** (`sectors/Haulage.java`), Freight's second division (Ports is FR3's):
  - **the city leg, each paid once** (`legTonnes()`): a BULK tonne bought at home is a truck tonne, paid by its buyer; an import or export is one less the siding's share of its railed part (`RAIL_ROAD_RELIEF` .75); sea relieves nothing. Billed at the top of the month in `Freight.haul()`, beside the railway's bill, at the share in force and the quote, never past what the trucks could carry.
  - **the need** (`nameplateNeed()`): the plant standing, at nameplate, each tonne weighted as the leg price weights it; **the order** toward it and the plant on site (`trucksAsked()`), an eighth a month at most. **The share** (`getShare()`): struck once at each haul, over the fleet held before the month's wear. **The freight factor** (`Sector.getFreightRatio()`): the lesser of the vans' ratio (GOODS) and .6 + .4 x the share, for a sector with any BULK input or output.
  - **the fleet**: TRUCKS in Freight's pantry, seeded once at the need, worn over 120 months and a tenth faster past the depots' room; diesel drawn as the railway's, 33 L a 100 km.
  - **the price**: the railway's rule on Haulage's books - the depots' share of Freight's payroll, power, water, repairs and tax, the diesel, the trucks worn and **the fleet account** (what the trucks bought cost past their wear, recovered over 24 months), plus 1.2% a month on the trucks, the depots and their ground - over what the trucks **could** carry, times the scarcity, a quarter of the way a month. A planner weighs a works at the **steady quote**: the same rule on the trucks worn, no scarcity (`getSteadyQuote()`).
  - **the Truck Depot** (id 139, `BuildingType.HAULAGE`): 100 trucks, D$12M, 4 ha, 100 drivers and 25 mechanics; ordered when the street holds half a depot's trucks; never retired by the railway's measure. **Drivers follow the trucks** (`strikeDrivers()`, BuildingManager's offered share by job type).
- **TRUCKS** (last in `Good`): D$215k, 15 t, imports only, GENERAL cargo; **the Heavy Truck Plant** (id 140): six a month.
- **Vans carry goods only** (`Sector.tonnesMoved()`); **every maker is weighed with its freight** (`ProcessingSector.freightRunning()`, `cityLegs()`, `Haulage.legPrice()`; `ExtractionSector.freightRunning()`; a farm, a food works or a vehicle plant through `Sector.estimatedMonthlyProfit()`), no truck in its capital.
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
10. **★10 (review) The fleet account.** Freight's books expense a truck when it is bought; priced at its wear alone, a growing fleet was a loss every month, borrowed for with no plant to borrow on before a depot: on one path the playtest's Freight was an empty company in debt from month 936, its fleet orders refused ($3.87T), and the city stalled at 30,000 people for 3,000 months. Priced at the month's purchases instead (b4c73d2), the bill was lumpy - a shop paid a month of a future works' trucks, and HealthCheck's dear twin emptied its shelves. So Haulage keeps what the trucks bought cost past their wear and recovers it over `FLEET_RECOVERY_MONTHS` (24): over a city's life the shippers pay what the trucks cost, as each once paid for its own vans. Planners weigh at the steady quote, the rule on wear, so a works is not charged the whole fleet's growth in its estimate.
11. **★11 (the gate's round) A truck a month at least, and every maker weighed with its legs.** The order took an eighth of the trucks asked a month: a young town's eighth of two trucks is a quarter of one, so its every new works ran short for a year (`Haulage.MIN_TRUCKS_A_MONTH` 1 now). And only the processing works and the extractors had their legs in their estimates: Agriculture, its vans gone, weighed a farm with no freight at all.

## 3. The slices, and what the review changed

| slice | by | what the review changed |
|---|---|---|
| 1, the rename (39 files) | Sonnet | nothing |
| 2, TRUCKS, the depot, the plant (22 files) | Sonnet | the TRUCKS banner's "nothing buys it yet"; buildings.json's final newline. Its additions kept: `Automotive.LUBRICANT_LITRES_A_TRUCK`, the "Truck depots" card group |
| 3, Haulage (421 lines, Freight, Sector) | Sonnet | ★2, ★4, ★7-★10 and the steady quote; RailCheck on the railway's own figures (`Freight.getRunningCost()`) |
| 4, the weighing | Sonnet | nothing; WoodProductsCheck's HELD tail fixed by Claude |
| VanCheck | Sonnet | nothing (department stores and meat works in its fixture) |
| HaulageCheck | Sonnet | nothing; ★4 and ★9 landed under it, and its bid section moved to the order's target |
| the suite's reds | Claude, Sonnet diagnosing | named service lines in SectorBooksCheck and BondCheck; the trucks' diesel in OilCheck, RefineryView, RefineryViewCheck; the floor in SaveFileCheck, EarthTerrainCheck, MaterialsCheck; SectorFlowCheck's sixth throttle; ScaleCheck's scaler (Haulage's prefixed keys; the unemployment array); fixtures FR2 moved, each still caused: PolicyPreviewCheck (700 houses), HealthCheck (the twins' trucks kept up), CreditCheck (the ban re-armed), BankCheck (Freight's cash), CentralBankCheck (a world price fall causes the loose twin's months under target) |
| the gate's three points (40b9468) | Claude | ★11: the village's farms weighed with their legs (`Sector.estimatedMonthlyProfit()`), the order at least a truck a month (`Haulage.bid()`); AutoBuildCheck's fixture removed (main's passes), CopperRefiningCheck's Fabrication Shop line on the new rule; the probe of the share in CentralBankCheck's twins reverted |
| latent bugs FR2's paths reached | Claude, Sonnet diagnosing | **`Mortgage.Slice.of`** tested the down payment with no tolerance where `canFundMortgage()` allows 1e-9: a landlord raises equity to the payment exactly, and a ten-trillionth decided whether a city built and its reformed twin refused (DenominationCheck; OrderSearchCheck's count with it). **`FundView.search`**'s order was no order (one issuer's bonds by maturity, every other pair by size): TimSort threw on ReadPathCheck's city. **IndustryPicture**'s "OUT OF THE WAREHOUSE" ran 137 px in a 98 px room: "FROM STOCK" |

## 4. What moved in the trace

Against main at 1213dbe (0.8.4), the model at 1f573f6. Both runs: "Nothing. Every month passed the audit and every reload matched." Every file moves from month 4 (month, bank, bonds), 5 (build, house) and 29-31 (pop, labour): the first hauls with a city leg; borrow from month 963 (default) and 2,173 (Earth).

| | main | FR2 (1f573f6) | a651649, before the gate's round |
|---|---|---|---|
| default, end | m4001: 544,052 people, GDP $6.0T a month at a price index of 1,655 | m4000: 721,948, $15.4T at 3,506 (real GDP +21%) | 795,408, $13.7T at 3,387 (+11%) |
| default, m1071 / m2022 | 10,405 / 71,832 | 7,987 / 86,720 | 15,386 / 133,536 |
| Earth (Rotterdam), end | m4005: 812,101, $18.4T at 3,414 | m4005: 675,857, $4.5T at 983 (real GDP -15%) | 762,356, $8.6T at 1,655 (-4%) |
| Earth, m1071 / m2022 | 22,371 / 152,373 | 18,259 / 122,291 | 20,607 / 164,404 |

**Haulage over the run** (LongPlaytest's line): default - short of the city's legs in 538 months, **33 under 90%**, at worst 36% carried (m40); 989 trucks and 1,000 depot places at most; $12.74T billed for 2.10 Gt; the last quote $12,981 a tonne against $5,039 estimated (price index 3,506: about D$3.70 a tonne at founding prices). Earth - 562 months short, **29 under 90%**, worst 40% (m40); 947 trucks, 900 depot places; $2.87T for 2.15 Gt. Freight counts as "a shell" (no plant, owing something) from month 5 until its first depot - 980 months on the default run, 497 on the Earth: it carries the bulk all the while, and the playtest's count reads a trucks-only Freight as an empty company.

**Why.** The bulk works lost their vans' capital and diesel (a Pelletising Plant's 64%) and pay a city leg at a few dollars a tonne instead; the van fleet falls to the shops' and kitchens' few; drivers and mechanics are posts. The short months are mostly the struck share reading a growing plant a month late (under 1%); the deep ones are a giant works landing, every bulk sector sharing the shortfall (★5) until its trucks arrive. A single run swings a long way with the city's path - the model's earlier drafts ended this default run at 787,393, 526,648, 30,614 (stalled, ★10), 673,357 and 795,408 - so the ensemble (§5) is the measure.

## 5. The ensemble

The 16-seed x 5-policy ensemble (`workbench/workbench-delta-20261010.zip`'s runner, adapted for the PC as `runpc.sh` and run five at a time, outside the tree), scored by its own `score.py`, on main (7255f49: the same model as 1213dbe) and on FR2 at 1f573f6; 80 runs each, none missing or short, no LongPlaytest finding, no money-audit month, 64/64 runs their AUTO2 twin's before the switch. Three earlier drafts beside it: a651649 (before the gate's round), 2552755 (before ★10) and 361118e (before the need on site and the fleet account).

| criterion | main | FR2 1f573f6 | a651649 | 2552755 | 361118e |
|---|---|---|---|---|---|
| A1 inflation m240-end | 2.23%, pass | 2.22%, pass | 2.28%, pass | 2.27%, pass | 2.28%, pass |
| A2 longest yoy > 10% spell | 16 [6-29], FAIL | 15 [6-35], FAIL | 15 [5-22], FAIL | 11 [5-20], FAIL | 12 [8-22], FAIL |
| A3 deflation; final/peak population | pass | pass (99.81% the least) | **FAIL: one seed 30.9%** | pass | pass |
| S1 0% from m1800 minus twin | 1.77%, spell 23, FAIL | 1.92%, spell 34, FAIL | 1.80%, spell 24, FAIL | 1.94%, spell 21, FAIL | 2.00%, spell 36, FAIL |
| H1 a young city at 0% | FAIL | FAIL | FAIL | FAIL | FAIL |
| K2 SHOCK m720-840 | 2.77%, pass | 2.81%, pass | 2.68%, pass | 2.96%, pass | 3.00%, FAIL |
| R3 HIGH minus twin | 4 above +1, FAIL | 5 above +1, FAIL | 5 above +1, FAIL | 4 above +1, FAIL | 4 above +1, FAIL |
| T1 AUTO2 seeds to 40,000 | 16/16 at m1,324 | 16/16 at m1,436 | **15/16** at m1,437 | 16/16 at m1,380 | 16/16 at m1,434 |
| T2 AUTO2 final population | 235,649 | 237,796 [161,672-282,179] | 221,860 [7,889-285,501] | 240,150 | 206,916 |
| criteria | 10/14 (no baseline) | 21/25 | 21/25 | 21/25 | 20/25 |

**Read:** main's four standing reds stand in every draft (A2, S1, H1, R3), and nothing else is red on the final model. The seed that collapsed on a651649 (seed 9: 25,521 people at m1184, 7,889 at the end, its Business Services' and Manufacturing's works closing) ends at 242,552 on 1f573f6 and grew past 178,000 on main and on both earlier drafts; its cause was never pinned to one mechanism, and the gate's round (★11) moved its path with every other's. AUTO2's cities reach 40,000 people about 110 months later than main's at the median and end the 2,400 months at main's size.

## 6. The suite

`Batch check.bat --maps` on 1f573f6: **115 harnesses run, 113 passed, 2 failed** - ScaleCheck (four lines, below) and CopperRefiningCheck, whose Fabrication Shop line asserted the rule ★11 changed (a maker's estimate without its legs): moved onto the new rule, computed from `cityLegs()` and `legPrice()`, green (its legs D$8.3k a month). Stale: no firm finding; the maps regenerated. Main's baseline (7255f49): 113 run, 113 passed. The gate's three points on 40b9468, and what came of them:

- **AutoBuildCheck's village** (1,571 people at m600 against main's 13,608) was a fault: Sector's default estimate weighed a maker without its freight, so Agriculture, its vans gone, saw a farm with no cost of moving its grain and put a third Mixed Farm on the village's last ground. Weighed with its legs (★11) the village ends at 12,629, and at 10,307 with the order's floor beside it; main's AutoBuildCheck passes unchanged on the final model (4 passes borrowed), so the fixture FR2 had given it (the copy handed its people again) is gone.
- **CentralBankCheck**, "very strict holds prices lower than Standard over the horizon": **green on 1f573f6** - very strict 2.168% a year over the 240 months against Standard's 2.577% (the dial at most 12.76% and 4.26% on average, against 8.60% and 3.81%), the cities 5,488 and 5,902 people. Main: 2.386% against 2.710%, the cities alike. On a651649 it was 2.357% against 2.281%, the strict twin's dial at 19.98% after a year at 9.99% (m739), its city 27% smaller (4,662 against 6,398). The haulage share read 1.000 in every twin's 240 months, so nothing in the twins throttled; what moved is the city they branch from, grown through its first years with its works short of trucks while the order was an eighth of two trucks a month (★11).
- **ScaleCheck**, "...and it builds": **green on 1f573f6** - the city built 137 homes in its six free months, the five- and ten-billion copies 81.68 and 19.06 per K (main: 63, 110.23 and 39.25; a651649: 0, 41.42 and 0). **Red instead, and left for Jerus**: its fixture "its own rents price no migrant out in the months its copies are read against", and with it both copies' "its rents pull migrants as the city's do" (worst 0.034 and 0.039 against 0.01); and the ten-billion copy's fixture "the free copy ordered - an order sized from more than a million needed" (its largest needed 508,825), the same city's path. The fixture's premise does not hold on FR2's path, and nothing in it is short of trucks: the share is 1.000 in all six months (4.2 trucks for 12,542 t of need). The city houses 3,512 people in 936 homes (3.75 a home) where main's houses 3,877 in 1,140 (3.40), and its rent burden is 0.3006-0.3153 in five of the six months against `TARGET_RENT_BURDEN` 0.30 (main's at or under it in all six). The line went red and green with the path all through the batch - the fixture city's burden sits on the target, 0.287-0.315 across the drafts - and the copies' own miss is the founding endowments they keep (§7). The harness is not moved; whether a town built under FR2's freight should house its people as roomily as main's is Jerus's call (FR2-C), and **FR2 waits for his review on it**.
- The fixtures FR2 gave because of the trucks, tried again on the final model: AutoBuildCheck's removed (above); HealthCheck's twins pass without their fleet held (4.8 points against 5: 3.7 priced out, 1.1 on the shelf) and keep it, since a point of that is still when each twin's trucks arrive and the section tests the fee; PolicyPreviewCheck's 700 houses stay (with main's 580 the bank makes no profit to tax).

## 7. Not built, and what is left

- **Not this batch's** (the spec's later rows): the Ports division and the quay's handling (FR3); the railway carrying the city's own bulk (D4) and the port yard (D7) (FR6); every screen of spec §6 (FR4); trucks drawn (FR5); a truck's `since` year.
- **Not built, noticed**: the road still relieves rail and sea shares at `RAIL_ROAD_RELIEF` and does not read the trucks' tonnes (spec §9.10, §9.14); GOODS go by vans, never by truck; the government's own purchases pay no city leg; Construction is billed for its materials' legs but never throttled (its materials are not declared uses).
- **For Jerus (Pending, FR2-C)**:
  1. **One share for the city (★5)**: a giant works landing slows every bulk sector for the months its trucks take. Per-sector dealing (the newest demand last) is the alternative.
  2. **The review's reversals of the brief**: ★4 (the need at nameplate, the order toward the works on site), ★7 (a depot at half a depot of trucks), ★10 (the price on the trucks bought), ★11 (a truck a month at least).
  3. **A village with little ground** - fixed in the gate's round (§6): every maker weighed with its legs; the village ends at 10,307 people against main's 13,608, its fixture main's again.
  4. **ScaleCheck's copy keeps its city's founding endowments**: the founding care and police endowments are code constants for 1,200 people, not saved (the harness's own "WHAT IT STILL LOSES"); FR2's fixture city is small enough at month 602 (2,188 people on one draft, 5,567 before) for the endowment to be half its care, and its rents-pull pair went red (worst 0.012 against 0.01) until the city's path moved again. Scaling both by K in a scratch tree gave a pull of 1.0000 every month. Closing it needs a saved `foundingScale` - a model and save change, its own small batch. On the final tree its rents-pull pair is red again, its fixture city's own rents now over the target (§6).
  5. **The ensemble's runner** lives in `workbench/workbench-delta-20261010.zip`, written for the cloud; FR2 ran it through an adapted `runpc.sh` (';' classpaths, `target/classes`, gson 2.10.1, `python -I` with pandas installed into a scratch folder). Whether it belongs in the tree is the orchestrator's call.
  6. **ScaleCheck's rents-pull premise** (§6): FR2's fixture city is more crowded than main's (3.75 people a home against 3.40) and its rents price migrants out in five of the six months the copies are read against. The model's answer or the harness's is Jerus's call. CentralBankCheck's strict twin, red on 40b9468, is green since ★11.
