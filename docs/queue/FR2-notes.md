# FR2 - the Freight sector: notes

*0.8.5, 2026-10-18. The freight batch that changes the model (`docs/studies/spec-freight-and-infrastructure.md` §7's FR2 row; §2's rule, §3's design; decisions D1-D6, Jerus 2026-10-12: "all the freighting just do rec"). The brief, with every number and its source, is [FR2.md](FR2.md). The slices were Sonnet 5.5 subagents' (DeepSeek's key not being set on the PC), each reviewed by Claude; the review changed the design in four places (§3), and every trace moves (§4). Save format 40, the floor with it.*

## 1. What shipped

- **Freight in Rail's slot** (`sectors/Freight.java`, `Sectors.FREIGHT`, `freight()`): key "Freight", `Sectors.KEYS` position 12 (index 11) in place. Its railway is the Rail division, as built; its bill on the shipper is "Rail freight", since "Haulage" is the trucks'. The railway's price rule reads the Rail division's share of each cost line and of the rate base: all of it while no depot stands, so a railway-only Freight prices as Rail did.
- **Haulage** (`sectors/Haulage.java`), Freight's second division (Ports is FR3's):
  - **the city leg, each paid once** (`legTonnes()`): a BULK tonne bought at home is a truck tonne, paid by its buyer; an import or export is one less the siding's share of its railed part (`RAIL_ROAD_RELIEF` .75); sea relieves nothing. Billed at the top of the month in `Freight.haul()`, beside the railway's bill, at the share in force and the quote, never past what the trucks could carry.
  - **the need**: the plant standing, at nameplate (`nameplateNeed()`), each tonne weighted as the leg price weights it. **The share** (`getShare()`): struck once at each haul, over the fleet held before the month's wear. **The freight factor** (`Sector.getFreightRatio()`): the lesser of the vans' ratio (GOODS) and the haulage ratio, .6 + .4 x the share, for a sector with any BULK input or output.
  - **the fleet**: TRUCKS in Freight's pantry, bid as vans are (`min(need - have, need / 8)`), seeded once at the need, worn over 120 months and a tenth faster past the depots' room; their diesel drawn as the railway's, 33 L a 100 km (.66 L a tonne).
  - **the price**: the railway's rule on Haulage's books - the depots' share of Freight's payroll, power, water, repairs and tax, the diesel, the trucks worn, plus 1.2% a month on the trucks, the depots and their ground - over what the trucks **could** carry, times the scarcity, a quarter of the way a month.
  - **the Truck Depot** (id 139, `BuildingType.HAULAGE`): 100 trucks, D$12M, 4 ha, 100 drivers and 25 mechanics; ordered by Freight's plan when the street holds half a depot's trucks; never retired by the railway's measure. **Drivers follow the trucks**: a depot offers a driver's post for each truck it keeps (`strikeDrivers()`, BuildingManager's offered share by job type), the city's count and Freight's wage bill the same whole number.
- **TRUCKS** (last in `Good`): D$215k, 15 t, imports only, GENERAL cargo; **the Heavy Truck Plant** (id 140, Automotive): six a month, the van plant's figures scaled by its output's value.
- **Vans carry goods only** (`Sector.tonnesMoved()`): a works whose tonnes are bulk keeps none.
- **A works is weighed with its freight** (`ProcessingSector.cityLegs()`, `freightRunning()`; `ExtractionSector.freightRunning()`): its goods' vans and their capital, and its bulk legs at the quote through `Haulage.legPrice()`; no truck in its capital.
- **The save**: Haulage's state in Freight's extras (`haul.*`), each sector's units bought at home carried across a save (`SectorState.boughtLocal`). `SAVE_FORMAT` 40 and `OLDEST_LOADABLE_FORMAT` 40: a format-39 save's railway is keyed "Rail" and would load into nobody.
- **The screens, as few as FR4 leaves**: Freight's page shows its trucks ("Haulage, the trucks"); the fifth throttle reads "freight"; the depot on Build's Rail tab with its own card; the refinery's picture counts the trucks' diesel among the lorries'. LongPlaytest counts Haulage over the run.
- **Harnesses**: HaulageCheck (new, ten sections and a reload, written to the brief by a second subagent as a stranger to the code); RailCheck, VanCheck, the weighing harnesses, SectorBooksCheck, BondCheck, OilCheck, RefineryViewCheck, SaveFileCheck, EarthTerrainCheck, SectorFlowCheck, WoodProductsCheck and others moved onto the new rules (§3).

## 2. ★ calls

The brief's, as Jerus's "rec" left them, and the review's:

1. **★1 Haulage carries the BULK stream; vans the GOODS stream** (spec 3.5's formula).
2. **★2 A driver is a post on the depot that keeps the truck** (posts exist only on buildings); trucks past the room run unposted at +10% wear (D6). **Reviewed:** a depot offers drivers only for the trucks it keeps - the brief's depot paid a hundred drivers for eleven trucks and tripled the city leg's price.
3. **★3 D20, the Elevated Highway's price, checked and not raised**: it comes from the three roads' crossovers (InfrastructureCheck), not a cost a km.
4. **★4 REVERSED by the review: the need is the plant standing, at nameplate**, as the vans' is. The brief took last month's legs ("carried and refused"); a town whose works stand before its first month then seeded its fleet on that month's few hundred tonnes and ran its wells, kilns and refineries at the floor for months - fourteen harnesses at once.
5. **★5 One carried share for the city**, as the railway's per stream: every bulk sector runs at the same haulage ratio. Its cost is in §4: a giant works landing shares its shortfall with every bulk sector for the months the trucks take to arrive.
6. **★6 The Heavy Truck Plant at six a month**: 55-70% of a mature city's replacement trucks.
7. **★7 (the review) A depot is ordered when the street holds half a depot's trucks** (`DEPOT_ORDER_MARGIN` .5, not the brief's .1): at a tenth, the first depot opened at eleven trucks and priced the leg at six times the trucks' own cost for two years.
8. **★8 (the review) The price is struck over what the trucks could carry**, not what they carried: over the carried tonnes, the railway's divisor, a seeded fleet priced a month's dozen tonnes at D$5,000 a tonne.
9. **★9 (the review) The share and the trade's shares are struck at the haul and saved**: read live, the share moved inside a month (the sectors before Freight's purchases at one rate, after at another - a refinery bought its crude at one and made its products at the other); read off the markets, a reload struck another need.

## 3. The slices, and what the review changed

| slice | by | what the review changed |
|---|---|---|
| 1, the rename (39 files) | Sonnet | nothing |
| 2, TRUCKS, the depot, the plant (22 files) | Sonnet | the TRUCKS banner's "nothing buys it yet"; buildings.json's final newline. Its additions kept: `Automotive.LUBRICANT_LITRES_A_TRUCK`, a "Truck depots" card group |
| 3, Haulage (Haulage.java 421 lines, Freight, Sector) | Sonnet | the live share (then struck, ★9), the price over capacity (★8), the drivers (★2), the depot margin (★7), RailCheck on the railway's own figures (`Freight.getRunningCost()`), ScaleCheck's scaler taught `haul.quote` and `haul.tightness` |
| 4, the weighing | Sonnet | nothing; WoodProductsCheck's HELD tail fixed by Claude |
| VanCheck | Sonnet | nothing (its fixture gains department stores and meat works) |
| HaulageCheck | Sonnet | nothing; two model changes landed under it (★4, ★9) and it followed them |
| the suite's 17 reds | Claude | ★4's reversal, ★9; the named service lines in SectorBooksCheck and BondCheck; the trucks' diesel in OilCheck, RefineryView and RefineryViewCheck; the floor in SaveFileCheck, EarthTerrainCheck and MaterialsCheck; SectorFlowCheck's sixth throttle; IndustryPicture's "OUT OF THE WAREHOUSE" (137 px in a 98 px room, a pre-existing overflow FR2's fixture first reached) now "FROM STOCK" |

## 4. What moved in the trace

Against main at 1213dbe (0.8.4). Both runs: "Nothing. Every month passed the audit and every reload matched." Every file moves from month 3, the first haul with a city leg.

| | main | FR2 |
|---|---|---|
| default, end | m4001: 544,052 people, GDP $6.0T a month at a price index of 1,655 | m4005: 787,393, $5.6T at 1,078 (real GDP +43%) |
| default, m1071 / m2022 | 10,581 / 72,604 people | 15,911 / 189,780 |
| Earth (Rotterdam), end | m4005: 812,101, $18.4T at 3,414 | m4000: 684,672, $7.1T at 1,906 (real GDP -30%) |
| Earth, m1070 / m2020 | 22,371 / 152,373 | 17,170 / 112,733 |

**Haulage over the run** (LongPlaytest's new line): default - short of the city's legs in 1,761 months, **98 under 90%**, at worst 19% carried (m1254); 948 trucks and 900 depot places at most; D$3.90T billed for 2.68 Gt; the last quote $3,584 a tonne against $2,167 estimated (price index 1,078: about D$3.30 a tonne at founding prices). Earth - 1,870 months short, 104 under 90%, worst 21% (m35); 927 trucks; $5.21T for 2.18 Gt.

**Why.** The bulk works lost their vans' capital and diesel (a Pelletising Plant's 64%) and pay a city leg at a few dollars a tonne instead; the van fleet falls to the shops' and kitchens' few; drivers and mechanics are posts. The short months are mostly the struck share reading a growing plant a month late (under 1%); the deep ones are a giant works landing, every bulk sector sharing the shortfall (★5) for the ~6 months the delivery cap (an eighth of the need a month) takes. The two runs diverge in opposite directions from month 3, as runs do: neither end is the batch's alone.

## 5. The ensemble

The 16-seed x 5-policy ensemble (`workbench/workbench-delta-20261010.zip`'s runner, adapted for the PC as `runpc.sh`, outside the tree), run on main (7255f49: the same model as 1213dbe, TM1 and FX3 moving no model code) and on FR2 (361118e + 0.8.5), scored by its own `score.py`; 80 runs each, none missing or short, no LongPlaytest finding, no money-audit month, 64/64 runs their AUTO2 twin's before the switch.

| criterion | main | FR2 |
|---|---|---|
| A1 inflation m240-end | 2.23% [2.06-2.47], pass | 2.28% [2.05-2.43], pass |
| A2 longest spell of yoy > 10% | 16 [6-29], FAIL | 12 [8-22], FAIL |
| S1 0% from m1800 minus twin | 1.77%, spell 23, FAIL | 2.00%, spell 36, FAIL |
| H1 a young city at 0% | FAIL | FAIL |
| K2 SHOCK m720-840; minus twin | 2.77%; 0.47%, pass | 3.00%; 0.41%, **FAIL** (at the 3.00% line) |
| R3 HIGH m660-720 minus twin | -1.47%, 4 seeds above +1, FAIL | -1.95%, 4 above +1, FAIL |
| T1 AUTO2 seeds to 40,000 | 16/16 at m1,324 [1,192-1,546] | 16/16 at m1,434 [1,233-1,676], pass |
| T2 AUTO2 final population | 235,649 [189,382-279,902] | 206,916 [154,994-286,962] (-12%), pass |

**Read:** main's four standing reds stand (A2 better, S1 worse); K2 turns red by a hair - the shock's inflation at 3.00% against a 3.00% line, 2.77% on main; cities grow a little slower (40,000 people 110 months later at the median, 12% fewer at m2400). The bulk works lose their vans' capital and pay a few dollars a tonne instead; the cost of the change is the trucks' shortfall while a city's works grow (§4). Main scored 10/14 with no baseline of its own; FR2 20/25 with main's T1 and T2 as its baseline: 9/14 of main's fourteen.

## 6. The suite

SUITE_PLACEHOLDER

## 7. Not built, and what is left

- **Not this batch's** (the spec's later rows): the Ports division and the quay's handling (FR3); the railway carrying the city's own bulk (D4) and the port yard (D7) (FR6); every screen of spec §6 (FR4); trucks drawn on the map (FR5); a truck's `since` year (the research batch).
- **Not built, noticed**: the road still relieves rail and sea shares at `RAIL_ROAD_RELIEF` and does not read the trucks' tonnes (spec §9.10, §9.14); GOODS go by vans, never by truck (★1); the city's own purchases (the government is not a sector) pay no city leg; the builders' site materials are billed (they are bought) but Construction is never throttled, its materials not being declared uses (`hasBulkLegs()` false).
- **Findings for Jerus (Pending)**:
  1. **One share for the city (★5)**: when a giant works lands, every bulk sector runs short with it for about six months. Per-sector dealing (the newest demand last) is the alternative.
  2. **Freight finances its trucks out of revenue and credit**, as sectors financed their vans: a founded Freight earns almost nothing, so HaulageCheck's fixture gives it D$1B. In the playtest buying trucks outside the budget changed little (1,629 short months against 1,761): cash is not what binds a played city.
  3. **ScaleCheck's scaled copy runs its rent burden about 4% above its city's**, before FR2 as after (slice 2's tree: 0.279 against 0.268); FR2's fixture city sat at 0.287-0.298 under the 0.30 target for a while and the copy crossed it (red in the middle of the batch, green at its end). A pre-existing imprecision of the scaler, worth its own look.
  4. **The ensemble's runner** is in `workbench/workbench-delta-20261010.zip`, written for the cloud; FR2 ran it on the PC through an adapted `runpc.sh` (';' classpaths, `target/classes`, gson 2.10.1, Python's `-I`, pandas installed into a scratch folder). Whether it belongs in the tree is the orchestrator's call.
