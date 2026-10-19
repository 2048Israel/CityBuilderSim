# FR2 - the Freight sector: notes

*0.8.5, 2026-10-18/19. The freight batch that changes the model (`docs/studies/spec-freight-and-infrastructure.md` §7's FR2 row; §2's rule, §3's design; decisions D1-D6, Jerus 2026-10-12: "all the freighting just do rec"). The brief, with every number and its source, is [FR2.md](FR2.md). The slices were Sonnet 5.5 subagents' (DeepSeek's key not being set on the PC), each reviewed by Claude; the review changed the design in six places (§2), the suite's reds found two latent bugs outside FR2 (§3), and every trace moves (§4). The model measured is commit **a651649**; what came after it touched no model code. Save format 40, the floor with it.*

## 1. What shipped

- **Freight in Rail's slot** (`sectors/Freight.java`, `Sectors.FREIGHT`, `freight()`): key "Freight", `Sectors.KEYS` position 12 (index 11) in place. Its railway is the Rail division, as built; its bill on the shipper is "Rail freight", since "Haulage" is the trucks'. The railway's price rule reads the Rail division's share of each cost line and of the rate base: all of it while no depot stands.
- **Haulage** (`sectors/Haulage.java`), Freight's second division (Ports is FR3's):
  - **the city leg, each paid once** (`legTonnes()`): a BULK tonne bought at home is a truck tonne, paid by its buyer; an import or export is one less the siding's share of its railed part (`RAIL_ROAD_RELIEF` .75); sea relieves nothing. Billed at the top of the month in `Freight.haul()`, beside the railway's bill, at the share in force and the quote, never past what the trucks could carry.
  - **the need** (`nameplateNeed()`): the plant standing, at nameplate, each tonne weighted as the leg price weights it; **the order** toward it and the plant on site (`trucksAsked()`), an eighth a month at most. **The share** (`getShare()`): struck once at each haul, over the fleet held before the month's wear. **The freight factor** (`Sector.getFreightRatio()`): the lesser of the vans' ratio (GOODS) and .6 + .4 x the share, for a sector with any BULK input or output.
  - **the fleet**: TRUCKS in Freight's pantry, seeded once at the need, worn over 120 months and a tenth faster past the depots' room; diesel drawn as the railway's, 33 L a 100 km.
  - **the price**: the railway's rule on Haulage's books - the depots' share of Freight's payroll, power, water, repairs and tax, the diesel, the trucks worn and **the fleet account** (what the trucks bought cost past their wear, recovered over 24 months), plus 1.2% a month on the trucks, the depots and their ground - over what the trucks **could** carry, times the scarcity, a quarter of the way a month. A planner weighs a works at the **steady quote**: the same rule on the trucks worn, no scarcity (`getSteadyQuote()`).
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
10. **★10 (review) The fleet account.** Freight's books expense a truck when it is bought; priced at its wear alone, a growing fleet was a loss every month, borrowed for with no plant to borrow on before a depot: on one path the playtest's Freight was an empty company in debt from month 936, its fleet orders refused ($3.87T), and the city stalled at 30,000 people for 3,000 months. Priced at the month's purchases instead (b4c73d2), the bill was lumpy - a shop paid a month of a future works' trucks, and HealthCheck's dear twin emptied its shelves. So Haulage keeps what the trucks bought cost past their wear and recovers it over `FLEET_RECOVERY_MONTHS` (24): over a city's life the shippers pay what the trucks cost, as each once paid for its own vans. Planners weigh at the steady quote, the rule on wear, so a works is not charged the whole fleet's growth in its estimate.

## 3. The slices, and what the review changed

| slice | by | what the review changed |
|---|---|---|
| 1, the rename (39 files) | Sonnet | nothing |
| 2, TRUCKS, the depot, the plant (22 files) | Sonnet | the TRUCKS banner's "nothing buys it yet"; buildings.json's final newline. Its additions kept: `Automotive.LUBRICANT_LITRES_A_TRUCK`, the "Truck depots" card group |
| 3, Haulage (421 lines, Freight, Sector) | Sonnet | ★2, ★4, ★7-★10 and the steady quote; RailCheck on the railway's own figures (`Freight.getRunningCost()`) |
| 4, the weighing | Sonnet | nothing; WoodProductsCheck's HELD tail fixed by Claude |
| VanCheck | Sonnet | nothing (department stores and meat works in its fixture) |
| HaulageCheck | Sonnet | nothing; ★4 and ★9 landed under it, and its bid section moved to the order's target |
| the suite's reds | Claude, Sonnet diagnosing | named service lines in SectorBooksCheck and BondCheck; the trucks' diesel in OilCheck, RefineryView, RefineryViewCheck; the floor in SaveFileCheck, EarthTerrainCheck, MaterialsCheck; SectorFlowCheck's sixth throttle; ScaleCheck's scaler (Haulage's prefixed keys; the unemployment array); fixtures FR2 moved, each still caused: PolicyPreviewCheck (700 houses), AutoBuildCheck (the copy given people), HealthCheck (the twins' trucks kept up), CreditCheck (the ban re-armed), BankCheck (Freight's cash), CentralBankCheck (a world price fall causes the loose twin's months under target) |
| latent bugs FR2's paths reached | Claude, Sonnet diagnosing | **`Mortgage.Slice.of`** tested the down payment with no tolerance where `canFundMortgage()` allows 1e-9: a landlord raises equity to the payment exactly, and a ten-trillionth decided whether a city built and its reformed twin refused (DenominationCheck; OrderSearchCheck's count with it). **`FundView.search`**'s order was no order (one issuer's bonds by maturity, every other pair by size): TimSort threw on ReadPathCheck's city. **IndustryPicture**'s "OUT OF THE WAREHOUSE" ran 137 px in a 98 px room: "FROM STOCK" |

## 4. What moved in the trace

Against main at 1213dbe (0.8.4), the model at a651649. Both runs: "Nothing. Every month passed the audit and every reload matched." Every file moves from month 4 (month, bank, bonds), 5 (build, house) and 29 (pop, labour): the first hauls with a city leg.

| | main | FR2 |
|---|---|---|
| default, end | m4001: 544,052 people, GDP $6.0T a month at a price index of 1,655 | m4001: 795,408, $13.7T at 3,387 (real GDP +11%) |
| default, m1074 / m2025 | 10,581 / 72,604 | 15,582 / 133,702 |
| Earth (Rotterdam), end | m4005: 812,101, $18.4T at 3,414 | m4005: 762,356, $8.6T at 1,655 (real GDP -4%) |
| Earth, m1071 / m2022 | 22,371 / 152,373 | 20,607 / 164,404 |

**Haulage over the run** (LongPlaytest's line): default - short of the city's legs in 515 months, **19 under 90%**, at worst 36% carried (m40); 1,038 trucks and 1,000 depot places at most; $10.67T billed for 2.33 Gt; the last quote $12,737 a tonne against $8,245 estimated (price index 3,387: about D$3.76 a tonne at founding prices). Earth - 681 months short, **23 under 90%**, worst 40% (m40); 882 trucks, 900 depot places; $6.34T for 2.45 Gt. On the Earth Freight counts as "a shell" (no plant, owing something) for 497 months from month 5, until its first depot: it carries the bulk all the while - the playtest's count reads a trucks-only Freight as an empty company.

**Why.** The bulk works lost their vans' capital and diesel (a Pelletising Plant's 64%) and pay a city leg at a few dollars a tonne instead; the van fleet falls to the shops' and kitchens' few; drivers and mechanics are posts. The short months are mostly the struck share reading a growing plant a month late (under 1%); the deep ones are a giant works landing, every bulk sector sharing the shortfall (★5) until its trucks arrive. A single run swings a long way with the city's path - the model's earlier drafts ended this default run at 787,393, 526,648, 30,614 (stalled, ★10) and 673,357 - so the ensemble (§5) is the measure.

## 5. The ensemble

The 16-seed x 5-policy ensemble (`workbench/workbench-delta-20261010.zip`'s runner, adapted for the PC as `runpc.sh`, outside the tree), scored by its own `score.py`, on main (7255f49: the same model as 1213dbe) and on FR2 at a651649; 80 runs each, none missing or short, no LongPlaytest finding, no money-audit month, 64/64 runs their AUTO2 twin's before the switch. Two earlier drafts beside it: 361118e (before the need on site and the fleet account) and 2552755 (before ★10).

| criterion | main | FR2 a651649 | 2552755 | 361118e |
|---|---|---|---|---|
| A1 inflation m240-end | 2.23%, pass | 2.28%, pass | 2.27%, pass | 2.28%, pass |
| A2 longest yoy > 10% spell | 16 [6-29], FAIL | 15 [5-22], FAIL | 11 [5-20], FAIL | 12 [8-22], FAIL |
| A3 deflation; final/peak population | pass | **FAIL: one seed 30.9%** | pass | pass |
| S1 0% from m1800 minus twin | 1.77%, spell 23, FAIL | 1.80%, spell 24, FAIL | 1.94%, spell 21, FAIL | 2.00%, spell 36, FAIL |
| H1 a young city at 0% | FAIL | FAIL | FAIL | FAIL |
| K2 SHOCK m720-840 | 2.77%, pass | 2.68%, pass | 2.96%, pass | 3.00%, FAIL |
| R3 HIGH minus twin | 4 above +1, FAIL | 5 above +1, FAIL | 4 above +1, FAIL | 4 above +1, FAIL |
| T1 AUTO2 seeds to 40,000 | 16/16 at m1,324 | **15/16** at m1,437 | 16/16 at m1,380 | 16/16 at m1,434 |
| T2 AUTO2 final population | 235,649 | 221,860 [7,889-285,501] | 240,150 | 206,916 |
| criteria | 10/14 (no baseline) | 21/25 | 21/25 | 20/25 |

**Read:** main's four standing reds stand in every draft (A2, S1, H1, R3). On the final model **one AUTO2 seed collapses**: seed 9 peaks at 25,521 people at m1184 and ends at 7,889 (its ZERO_SLACK twin with it), which fails A3 and costs T1 a seed. Its run, replayed with Freight's figures beside it: Haulage's trucks carry every leg throughout (share 1.000 but one month at .987); Freight opens one rail building at m1120 on a loan of about D$1B and has neither by m1200; Business Services' offices close (15 to 0, m1180-m1220: the sector built to boom and bust with the currency) and then Manufacturing's (10 to 0 by m1300), unemployment rising to 56%. The same seed grows past 178,000 on main and on both earlier drafts. **The cause is not established** - whether FR2's prices tipped this one path into a collapse the model already had, or something of Freight's (the rail order and its loss) did it - and it is the open question this batch hands the gate (FR2-C). Without that seed the final model's AUTO2 cities end at main's size or above (median 221,860 against 235,649 with it; 15 of 16 to 40,000 people 110 months later at the median).

## 6. The suite

`Batch check.bat --maps` on a651649 (and `--timing`, alone and hard: 8 harnesses run in 137 s, 7 passed - every timing assertion OK; the one red is ScaleCheck's "it builds" below): **115 harnesses run, 113 passed, 2 failed**; Stale: no firm finding; the maps regenerated. Main's baseline (7255f49): 113 run, 113 passed. The two reds, both comparisons over a long run that each of the model's last rounds turned one way or the other:
- **CentralBankCheck**, "very strict holds prices lower than Standard over the horizon": the very strict twin's 240 months at 2.357% a year against Standard's 2.281% (its dial 5.12% on average against 3.67%, its city a quarter smaller: 4,662 people against 6,398). It passed on b4c73d2 and on main. A finding about monetary policy in one probe city, not a fixture; left for Jerus's look (FR2-C).
- **ScaleCheck**, "...and it builds: more homes at the end than at the start": its ten-billion copy built no home in its six free months (the city it copies built none either; the five-billion copy built 41 per K). Its rents-pull pair, red in the middle of the batch, passes on this tree; the copy's founding endowments are still the city's (§7).

## 7. Not built, and what is left

- **Not this batch's** (the spec's later rows): the Ports division and the quay's handling (FR3); the railway carrying the city's own bulk (D4) and the port yard (D7) (FR6); every screen of spec §6 (FR4); trucks drawn (FR5); a truck's `since` year.
- **Not built, noticed**: the road still relieves rail and sea shares at `RAIL_ROAD_RELIEF` and does not read the trucks' tonnes (spec §9.10, §9.14); GOODS go by vans, never by truck; the government's own purchases pay no city leg; Construction is billed for its materials' legs but never throttled (its materials are not declared uses).
- **For Jerus (Pending, FR2-C)**:
  1. **One share for the city (★5)**: a giant works landing slows every bulk sector for the months its trucks take. Per-sector dealing (the newest demand last) is the alternative.
  2. **The review's reversals of the brief**: ★4 (the need at nameplate, the order toward the works on site), ★7 (a depot at half a depot of trucks), ★10 (the price on the trucks bought).
  3. **A village with little ground**: AutoBuildCheck's 600-month village ends at 1,571 people against main's 13,608 - Agriculture, its vans gone, built a third Mixed Farm onto its last ground (its fixture now gives the copy its people back).
  4. **ScaleCheck's copy keeps its city's founding endowments**: the founding care and police endowments are code constants for 1,200 people, not saved (the harness's own "WHAT IT STILL LOSES"); FR2's fixture city is small enough at month 602 (2,188 people on one draft, 5,567 before) for the endowment to be half its care, and its rents-pull pair went red (worst 0.012 against 0.01) until the city's path moved again. Scaling both by K in a scratch tree gave a pull of 1.0000 every month. Closing it needs a saved `foundingScale` - a model and save change, its own small batch. On the final tree its "it builds" line is red instead (§6).
  6. **CentralBankCheck's very strict twin** ends the 240 months a hair dearer than Standard (§6).
  5. **The ensemble's runner** lives in `workbench/workbench-delta-20261010.zip`, written for the cloud; FR2 ran it through an adapted `runpc.sh` (';' classpaths, `target/classes`, gson 2.10.1, `python -I` with pandas installed into a scratch folder). Whether it belongs in the tree is the orchestrator's call.
