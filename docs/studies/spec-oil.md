# Oil and ports: the build spec, 2026-10-08

Read-only study of 0.7.70 (`/home/claude/cbs-0770`), from the project doc `claude/oil-and-ports-research.md` (cited by its letters [R], [W], [P], [S]). Prototypes in `/home/claude/scratch-oil/`: `proto/spread.py` (the refinery as units, the spread rule, the planner: `out/spread-3.txt`, `out/spread-package.txt`, `out/topping-gate.txt`); `proto/src/.../ShelfProto.java` (depth and offshore oil: `out/shelf.txt`); `proto/src/.../BoatProto.java` (the boats' schedule and a frame's query: `out/boat.txt`); `out/units-table.txt` (every derived figure below).

Timings are medians of 7 rounds on 2 shared cores, each repeated 3 times. Each figure is one of four kinds: from the research, by letter and number; derived, with the sum shown; measured; or ★est., a named constant with its reason, used where the research gives no figure (listed in §6).

## 1. Summary for Jerus
1. **The refinery becomes a campus of units.** A crude unit splits crude into seven cuts; your Oil Refinery becomes the small one (2,000 b/d), and a large one does 100,000. Seven conversion units turn cheap cuts into dear ones, each in two sizes: reformer, cracking unit, hydrocracker, alkylation, coker, lube plant and asphalt unit.
2. **Fuel becomes eight products plus coke.** Drivers buy petrol. The railway and lorries buy diesel, roads take bitumen, and factories take lubricants. Gas, naphtha, jet and fuel oil are exported until they have buyers. Old saves convert once, and no money moves.
3. **One rule builds it.** A unit's spread is what it makes less its feed, at the city's own prices. Each month the widest spread per dollar that passes the feed, land, staff and money gates is built. Nothing is scripted.
4. **★ Your one big call: petrol and diesel at wholesale (the research's ladder), or at today's pump price?** Wholesale cuts the drivers' fuel by 63% and the railway's by 59%, and a refinery on imported crude then pays only in a city of millions, as in reality. The pump price keeps every bill, but the playtest city then builds 6 small refineries and 27 small units. (Both measured.) I recommend wholesale; it is its own batch (O2), so it can wait for you.
5. **Each field is light, medium or heavy.** Heavy crude leaves residue that only a coker or an asphalt unit turns into money. Residue sells as fuel oil only when cut with diesel.
6. **Wells decline 10% a year** and retire at 10 barrels a day, after 22 years. A well needs 1 post, not 3.
7. **Platforms stand on shallow owned sea, under 150 m;** the generated world gets a depth. Measured: the research's 18,000 b/d platform would drain the largest shallow field within 100 km of three test sites (16 sites, 1.42 Mt) in 19 months. So a platform is a jacket with up to 12 wells, each lifting what a land well does (from the research's per-well figures). ★ To confirm.
8. **Ports are city buildings:** tanker, bulk, container and general cargo terminals. Goods go by sea where it is cheaper than rail or lorry, which narrows the trade band. One number is missing from the research (sea freight against a lorry's) and needs a source before O9.
9. **A Tank Farm stores crude and products.** A city-owned one is a strategic reserve that you fill and release.
10. **Pipelines are built only where the freight they save over the field's life repays them 1.25 times.** In practice, that means offshore.
11. **Boats are drawn only where you look,** from the month's trade: about 15 calls a month if all the playtest city's trade went by sea. The schedule takes 9 ms a month, and a frame's query stays under 0.4 ms even at 10B.
12. **Thirteen batches:** ten for the model (O1 bumps the save format) and three for the interface. The traces move at O1, O2, O3, O5, O6, O7 and O9.

**The research's answers, adopted, to confirm** (★ = adopted with a change):
- **Q1** two sizes; id 75 is the small crude unit. **Q2** eight products plus coke; FUEL → PETROL + DIESEL; save format 33. **Q3** a grade per field; imports medium. **Q4** phased buyers. **Q5** residue cut 3:1 with diesel.
- **Q6** declines: 10% a year on land; 3 years flat, then 8.5% a year at sea. **Q7 ★** shallow platforms (≤ 150 m) on a generated depth; the platform's form changes (item 7). **Q8** 60 s of boats a month. **Q9** ports are city buildings that narrow the wedge.
- **Q10** goods go by sea where it is cheaper. **Q11** the refinery is drawn as a campus. **Q12** no hydrotreating. **Q13** 1 post a land well. **Q14** a Tank Farm in this run. **Q15** pipelines by the payback rule. **Q16** shipyard, and **Q17** coastal shipping, later.

## 2. The design, on the code
### 2.1 The goods (`Good`)
`FUEL` is retired with a note, as FOOD was, and nine rows are appended.
- **World price:** the research's ladder (§2.1, §1.5) times crude's world mid: (.60 + .50) ÷ 2 = .55 a tonne over 1,165 L [P35], or .00047210 a litre.
- **Import and export:** world × 1.08 and world × 0.92 [§2.1].
- **Freight:** `Good`'s rule, three quarters of the half-wedge, which is .06 × world.
- **Every row** is stockable and `BAND`, and none is tax-exempt.

| good | unit, L a tonne | import | export | freight | traffic |
|---|---|---|---|---|---|
| LPG | litre, 1,850 [P35] | .0002345 | .0001998 | .00001303 | BULK |
| NAPHTHA | litre, 1,351 ★est. (JODI) | .0005099 | .0004343 | .00002833 | BULK |
| PETROL | litre, 1,320 [P35] | O1 .0016667 (FUEL's) → O2 .0006118 | O1 .0007 → O2 .0005212 | O1 .0003625 → O2 .00003399 | GOODS |
| JET | litre, 1,260 [P35] | .0006526 | .0005559 | .00003626 | BULK |
| DIESEL | litre, 1,180 [P35] | O1 .0016667 → O2 .0006883 | O1 .0007 → O2 .0005864 | O1 .0003625 → O2 .00003824 | GOODS |
| LUBRICANTS | litre, 1,127 ★est. (JODI) | .0009637 | .0008209 | .00005354 | BULK |
| FUEL_OIL | litre, 1,010 [P35] | .0004997 | .0004256 | .00002776 | BULK |
| BITUMEN | tonne | .6415 | .5465 | .03564 | BULK |
| COKE | tonne | .09207 | .07843 | .005115 | BULK |

- **Weight:** a litre good's `tonnesPerUnit()` becomes 1 ÷ its litres a tonne, a new field. K's rule had been a kilogram a litre.
- **`Good.cargo()` (O9):** LIQUID is crude, the litre goods and bitumen; DRY_BULK is iron, crops, grains, materials and coke; GENERAL is steel, fabricated steel, machinery, rolling stock, cars and vans; CONTAINER is the rest of GOODS. `ui/Icons` gets an icon for each new good.

### 2.2 Crude grades (`World`, `Deposit`, `LandManager`)
- **The grade:** `Deposit.grade()` returns LIGHT, MEDIUM or HEAVY, a pure function of `World.mix(cell, index, kind, salt)` at `GRADE_SHARES` of ⅓ each (★est.). Nothing is stored and no tonne moves, so W = unowned + remaining + extracted holds exactly.
- **The cuts:** `Refining.CUTS` holds research §1.1's cuts (gas, LN, HN, kerosene, diesel, VGO, residue), each column normalised to 100. Light is Brent [R1], medium is the research's blend, and heavy is Maya [R2] with its naphtha split 1:2 like the blend's.
- **The month's mix:** `LandManager.extractOil()` walks the owned fields in acquisition order from E to E + lifted and keeps `liftedByGrade[3]`. Refining's mix is its local fill at that mix, plus imports at MEDIUM. It is saved as extras `crudeMix.*`, and next month's slate is struck on it.

### 2.3 The refinery as buildings, and the flow (`sectors/Refining`, new `RefineryFlow`)
Conversion units get `"refinery": {"unit": KIND, "feed": litres a month}` (`BuildingsTemplate.refineryUnit()`, `feedPerMonth()`). Crude units keep `uses CRUDE` and lose `makes`, because the slate comes from the flow.

The figures are derived as follows:
- **Throughput:** a barrel a day is 4,839.6 L a month.
- **Small size:** one 2,000 b/d plant's stream of the unit's feed, on medium crude (for alkylation, a small cracking unit's cracked gas).
- **Capital:** small capital is large × (small/large)^0.485. The exponent is measured between the game's own D$60M for 2,000 b/d and the research's D$400M for 100,000.
- **cashCost, materials and points:** cashCost is the capital; materials are cashCost ÷ 60 and points cashCost ÷ 2, id 75's ratios.
- **Posts:** small units take 10 (the low end of [R5]'s 10–15 operators); large units as research §2.4. The skill mix is id 75's 50/30/15/5%.
- **Power and water:** power is the large unit's × the size ratio [R6], and water 0.25 a kW.
- **Land:** the large unit's × the capital ratio.
- **Road and stock:** none for conversion units.

| id | building | feed | b/d small / large | D$M | posts | kW | yields, vol% of feed (research §1.2) |
|---|---|---|---|---|---|---|---|
| 75 | Oil Refinery (the small crude unit, as today) | crude 8,300 t | 2,000 | 60 (+18 materials) | 100 | 1,000 | the cuts |
| 76 | Crude Unit | crude 415,000 t | 100,000 | 400 | 160 | 4,400 | the cuts; 40 ha; road load 32,950 (50× id 75's); stock 1.25B L |
| 77/78 | Small Reformer / Reformer | HN | 240 / 12,000 | 16.2 / 108 | 10 / 30 | 29 / 1,450 | petrol 82, gas 8; H₂ 1,400 scf a bbl [R6] |
| 79/80 | Small Cracking Unit / Cracking Unit | VGO | 480 / 15,000 | 19.8 / 105 | 10 / 40 | 74 / 2,300 | cracked gas 25, petrol 55, diesel 18, fuel oil 7 |
| 81/82 | Small Hydrocracker / Hydrocracker | VGO + H₂, 1,850 scf a bbl [R7] | 480 / 10,000 | 19.0 / 83 | 10 / 35 | 228 / 4,750 | diesel 50, jet 40, naphtha 15 (to HN), gas 5 |
| 83/84 | Small Alkylation Unit / Alkylation Unit | cracked gas only (olefins [R6]) | 120 / 5,000 | 18.8 / 115 | 10 / 30 | 60 / 2,500 | petrol 80 |
| 85/86 | Small Coker / Coker | residue | 450 / 10,000 | 18.7 / 84 | 10 / 40 | 68 / 1,500 | cracked gas 8, naphtha 15 (to HN), diesel 35, fuel oil 12, coke 30% of the feed's weight |
| 87/88 | Small Lube Plant / Lube Plant | VGO | 480 / 2,000 | 18.0 / 36 | 10 / 30 | 360 / 1,500 | lubricants 40, fuel oil 60 |
| 89/90 | Small Asphalt Unit / Asphalt Unit | heavy-crude residue only [R17][R18] | 450 / 3,000 | 2.15 / 5.4 | 10 / 10 | 9 / 60 | bitumen 95 (t, at 1,010 L) |

**The flow:** `RefineryFlow.solve(counts, crude, mix, values)`, pure and cached on the month.
1. **Crude** runs at nameplate × the rate and is split into cuts by the mix.
2. **Streams** are solved in flow order: VGO, then residue (the heavy part kept apart), then the HN pool, then cracked gas. Within a stream, units take feed in order of spread; one at a spread of 0 or less takes none, so it idles.
3. **Hydrogen** caps the hydrocrackers at the reformers' make on straight-run HN × 1,400 ÷ 1,850 (Q12: no other hydrogen).
4. **Leftovers:** gas goes to LPG, LN to PETROL, HN to NAPHTHA, kerosene to JET, diesel to DIESEL and VGO to FUEL_OIL.
5. **Residue** becomes FUEL_OIL only when cut 3:1 with the pool's diesel (Q5). Whatever the diesel cannot cut is burned in the furnaces at no value, which is why heavy crude wants a coker.
6. **Reads:** `getCapacity(g)` is the flow's product and `getInputAtCapacity(CRUDE)` the crude units' run. `getStockCapacity(g)` is the tankage × g's share of the slate. `costShareOf()` already splits the joint bill.

### 2.4 The spread rule as the planner (`Refining.plan()`)
- **Value:** `cityValue(g)` is `netImportPrice()` if the market imported any last month, else `netExportPrice()` if it exported any, else `getLocalPrice()`.
- **Feed:** `feedValue(f)` is what the feed becomes with no unit: VGO is worth fuel oil; residue (4 × fuel oil − diesel) ÷ 3; HN naphtha; gas LPG.
- **Spread:** `spread(kind)` = Σ yield × cityValue − feedValue.
- **Conversion units:** the gates, in order, are feed (the flow's spare feed and hydrogen ≥ 60% of one unit, `Rail.MIN_LINE_UTILISATION`), land (`plotsAvailableFor`), staff (`staffing(t).passes()`) and money (`servicesItsOwnDebt` on spread × min(spare, feed) × rate − running − standing). The best spread × min(spare, feed) ÷ cost gets the order, with one order in flight, as now.
- **Crude units (rule 6):** a crude unit is a candidate when the city imported on the trend at least 60% of what it would make of the goods that have buyers (O1: petrol and diesel), or when the wells' spare covers 60% of its crude (K's rule).
- **★ The package.** Measured: a crude unit alone fails the money gate on imported crude at the research's prices (`out/topping-gate.txt`), even a large one for 10× the playtest's fuel, and then its units never come. So it is judged with the conversion units of its own size whose spread and feed gates its cuts would pass, added one at a time. The margin is all of their margins less its crude at K's prices (the wells' spare at the local price, the rest at the net import price). The cost is all of their costs. Only the crude unit is ordered, and its units follow on their own spreads.
- **Measured at the research's prices over 20 years** (`out/spread-package.txt`):
  - a city with 10× the playtest's fuel, on imported crude: 2 Crude Units, 2 Cracking, 2 Alkylation and 10 Lube Plants;
  - the playtest city (m4000), on imported crude: nothing;
  - on 40 wells of its own light crude: 2 Oil Refineries and 3 Small Lube Plants (the first refinery clears the gate at +541 against 488, D$k a month);
  - on its own heavy crude: nothing.
- **Idle and shed:** a kind idle 6 months running (extras `idleMonths.KIND`) is the only kind `mayRetire()` passes. `retirementDemandAndCapacity()` returns {0, feed}, so `planRetirement()` sheds it while Refining loses money.

### 2.5 The buyers in phase 1 (Q4), and the export of the rest
- **Drivers** draw PETROL through `Motoring.drawFuel()`.
- **The railway** draws DIESEL through `Rail.haul()`. At O2, `FUEL_LITRES_PER_TONNE` is pinned at the literal 18, so the litres stay when the price moves.
- **Vans and trucks** draw DIESEL at ★est. 240 L a van-month: `TONNES_PER_VAN`'s 40 loads, at 50 km and 12 L/100 km each. `Sector.runFleet()` makes the draw with `Markets.draw(DIESEL, s, …)` at the operating rate.
- **Paving** draws BITUMEN with the order's material, through `Game.drawBitumen()`. The amount is the material over a Gravel Road's, (450 − 193) × 5 t, at 5% binder [R18]: 64.25 t for a Paved Road and for N1's paving, and (800 − 193) × 5 × .05 = 151.75 t for an Elevated Highway.
- **Lubricants:** ★est. 8 L a car built, 10 L a tonne of machinery and 2 L a tonne of fabricated steel, as `uses(LUBRICANTS)` on the Automotive and Manufacturing templates.
- **Everything else,** and any surplus, leaves by `produceStock`'s export-bound line. No audit line is new.

### 2.6 Well declines (`sectors/Oil`)
- **Vintages:** `Oil.vintages` records [month opened, count, LAND or PLATFORM] in extras. A save without them opens every well at the load month.
- **Output:** `getCapacity(CRUDE)` = Σ count × 415 t × profile(age), cached. The land profile is 0.9^(age/12) [W6, est.]. A platform well holds 1 for 36 months, then 0.915^((age − 36)/12) [W6][W16].
- **Retirement:** `Oil.wornOut()` finds wells under 10 b/d (41.5 t): land wells at 263 months, platform wells at 348. `Game.runRetirement()` retires them oldest first, and the one-a-month rule refills the sites.
- **Sites:** Q13 cuts id 74's jobs to 1 (DIPLOMA). Land wells take only dry sites (`LandManager.getSites(OIL, dry)`: a field's centre on land). Wells already on sea sites are kept.

### 2.7 Depth and the offshore platform
- **`World.depthAt(x, y)`** is in metres below the sea's level, 0 on land. `shelfTheta()` comes from the sea pass's own sorted samples: the level above which lies the shallowest `SHELF_SHARE` of the sea, 8.86% (★est., Harris et al. 2014). Depth = `SHELF_BREAK_M` 140 (★est.) × (θ_sea − e) ÷ (θ_sea − θ_shelf), and linear below.
- **Pure,** so a world without a depth gets one. spec-earth's GEBCO data can replace it.
- **Measured:** θ_shelf runs 0.528–0.557 on three seeds, at 509 ns a point (`out/shelf.txt`).

| seed | oil fields within 50 km | in the sea | ≤ 150 m | their sites | shallow, 12+ sites, within 100 km |
|---|---|---|---|---|---|
| 4127 | 31 | 25 | 8 | 13 | 0 |
| 77 | 141 | 19 | 16 | 35 | 1 (16 sites, 47 m, 84 km out) |
| 709115276 | 18 | 17 | 3 | 10 | 0 |

The world was sized at 150,000 t a site, for land wells. At that size, the research's 18,000 b/d platform (74,750 t a month) would empty the largest of these fields (1.42 Mt) in 19 months. **★ So the platform is built from the research's per-well figures:**

| building | id | cash | posts, power and water | lift |
|---|---|---|---|---|
| Offshore Platform (Oil) | 97 | 18,000 ([W13]'s jacket) | 12 (6 aboard × a 2-on/2-off rota, ★est. [W14][W27]); power and water 0 (its own) | 12 slots, on one sea site of a field ≤ `PLATFORM_MAX_DEPTH_M` 150 [§3.2] |
| Platform Well (Oil) | 98 | 14,000 ([W13]: $13–15M) | 1 | 415 t a month (Gulf shelf ~100 b/d [W15]) on the platform profile; one a site, in a slot |

- **The record:** `Oil.platforms` keeps [cell, index, wells, month].
- **The planner** builds a land well while a dry site is free and a platform well while a slot and a sea site are free. It builds a jacket for a shallow field with a free sea site, judged with min(12, sites) wells by the interest test.
- **Shuttle freight:** platform crude sold at home pays CRUDE's band freight a tonne (`baseFreight × freightFactor × rate`; ★ ashore costs what the boundary costs). `Oil.payShuttle()` books it as an imported service (`otherInputs["Shuttle tankers"]`, `imports`). Exported platform crude pays none.

### 2.8 The Tank Farm and the strategic reserve (Q14)
- **Tank Farm** (id 91, Refining): 500,000 m³ [§4.5]; 10 ha [P13]; cash 190,000 (★ the research's est., US$60 a barrel); 10 posts (★est.).
  - Refining keeps `pantry(CRUDE, 1)`, a month's run (★), and the products share the rest of the room. The rate is capped by crude on hand plus the month's fill.
  - It is built only when a port holds crude back for room (§2.9).
- **Strategic Reserve** (id 92): the city's, with the same figures.
  - `Game.fillReserve(t)` draws CRUDE as `Trade.CITY` from the treasury.
  - `Game.releaseReserve(t a month)` offers that much into the CRUDE clear as a city seller, a new branch in `Markets.clear()`.
  - Two audit lines: "− city ReserveFill" (TRADE scope for the part imported, DOMESTIC for the part from the wells) and "+ city ReserveSales" (DOMESTIC).
  - `DataSave.reserve` saves {tonnes, cost, release}, and `DecisionLog` records each fill and release.

### 2.9 Ports and the freight wedge (Q9, Q10)
The terminals are city buildings in a new `BuildingType.PORTS`, appended. An order without owned coast is refused `NO_COAST` (`hasCoastFor`).

| terminal | id | a berth a year | capital | posts | land |
|---|---|---|---|---|---|
| Tanker Terminal | 93 | 3.25 Mt [P1][P2] | D$96M [P11] | 7 [P13][P28] | 4 ha ★est. |
| Bulk Terminal | 94 | 27.5 Mt [P3][P5] | D$2,310M (US$84 a t-year [P3]) | 234 [P22] | 22 ha [P14] |
| Container Terminal | 95 | 0.75M TEU × 9 t [P6][P7][P43] | D$461M [P7] | 585 [P25][P26] | 20 ha [P15] |
| General Cargo Terminal | 96 | 0.45 Mt [P6][P8] | D$70M [P9] | 11 [P27] | 1 ha [P12] |

- **The wedge** (`Ports`, new, at `Rail.haul()` step 5):
  - A cargo kind's berth tonnes are shared among its goods by their boundary tonnes.
  - A good goes by sea first when ★`SEA_FREIGHT_SHARE` (sea's freight over a lorry's; unsourced) is below rail's quote, and after rail otherwise.
  - `setFreightFactor(1 − s_rail − s_sea + s_sea × SEA_FREIGHT_SHARE)`. Sea freight is paid abroad, inside the band, so no money path is new. With no terminal the factor is exactly 1.
  - Ports relieve the road as rail does, at 70%. `DataSave.portMonth` keeps the month's sea tonnes by kind and direction.
- **Classes:**
  - A tanker is the largest whose cargo fits the free tank room [§4.3]: VLCC 265,000 t, Suezmax 137,500, LR2 82,500, Aframax 75,000, LR1 60,000, MR 37,500 [P30]. Seaborne crude needs room for at least an MR. Without it, crude stays on rail or the lorry, and Refining reads "held back for room".
  - Bulk carriers are Capesize 165,000 t or Panamax 66,000 [P40][P42]. Feeders carry 9 t a TEU, and general cargo ships 5,500 t [P46].
  - Depth does not set the class: the research has no drafts.

### 2.10 Boats, a pure function of time (`BoatSchedule`: model, pure, never saved)
- **Calls:** tonnes ÷ the class's cargo, by kind and direction. A call arrives at `unit(mix(good, month, k))` × the month.
- **Movement:** each boat runs from its quay straight out to sea, away from the founding site, for `LANE_PLOTS` 1,536. Its position is route(call, t), where t is the game clock's month fraction (Q8: about 60 s a month). A leg takes `LEG_SECONDS` 4 and a berth `BERTH_SECONDS` 2 (24 h [P33]).

| city | calls a month | schedule | a frame's query (`out/boat.txt`) |
|---|---|---|---|
| m4000 (all 896,308 t by sea) | 15 | 0.01 ms | 2 µs |
| 5B | 159,227 | 7 ms | 52 µs for 1 district; 338 µs for 8 × 8 |
| 10B | 318,455 | 9 ms | 62 µs for 1 district (155 boats); 380 µs for 8 × 8 (1,429) |

- **Drawn** at L0 and L1, with lanes only at L2. Imports are blue, exports orange, an empty leg grey and boxes white [§4.4]. Boats are drawn at 1.6× at district zoom.

### 2.11 Pipelines (Q15)
- **Crude Pipeline** (id 99, Oil) is counted in km.
  - Cash: 4,500 a km offshore and 3,000 onshore (★ the research's est. [S2]).
  - No posts or power: its running cost is the house's 1% a year in repairs, plus tax.
  - `Oil.pipelines` keeps [cell, index, km, month]; the length is a straight line from the field to the founding site.
- **The rule:** `Oil.pipelinePays(field)` builds when (shuttle freight − the pipe's repairs and tax, a tonne) × tonnes a month × min(remaining ÷ lift, 480 months) ≥ cost × 1.25, provided Refining is not losing money and the wells are on their plateau.
- **Onshore** pipes never pass, because the model has no domestic freight. The rule is written for both.

### 2.12 The pictogram, 2.13 the oil screen, and the map
- **`RefineryView.of(Game)`** is pure and checked like `SectorFlow`. It holds crude by grade, the cuts, each unit's run, spread and gate, the products (made, imported, exported, by buyer) and the residue burned. It becomes `ui/SectorScreen`'s OPERATIONS · THE REFINERY on Refining's page: ribbons to scale and a strip, mockup 1's two states at 1,389 × 868.
- **`OilView.of(Game)`** is pure. It holds the wells by type with a 10-year forecast from `vintages`; the units' spreads, gates and the planner's words; the products table; and the reserve's Fill and Release (`Levers`). It becomes the panels of Oil's Operations page (mockup 2).
- **The map (Q11):** `CityMap` deals Refining's buildings to the district already holding Refining land, and `TilePainter` packs them as a campus. Platforms sit on their field inside a 500 m ring [W32], terminals stand on owned coast with a quay, and pipes are dashed.

## 3. The save: SAVE_FORMAT 33 (O1)
`FuelSplit.convert(DataSave)` runs at `saveFormat` ≤ 32, on names, before `Sector.restore()` and `Markets.restore()`.
- **The split:** p = drivers' litres ÷ (drivers' + railway's). The drivers' are `householdFuel[2]`, the railway's its `hauled` × 18; with neither, p = 1.
- **Markets:** FUEL's state becomes PETROL (price kept; flow, stock, demand and `taken[]` × p) and DIESEL (× (1 − p)).
- **Sector maps:** every map keyed "FUEL" (stock, pantry, the ledger's `unitsSold`, `unitsBought`, `sold` and `bought`, and the carried `exported` and `imported`) splits as PETROL = x × p and DIESEL = x − PETROL, so each pair sums to the original to the bit.
- **HELD** becomes {CROPS, VANS, ROLLING_STOCK, PETROL, DIESEL, LPG, NAPHTHA, JET, LUBRICANTS, FUEL_OIL, BITUMEN, COKE}; slot 3 splits, and the rest start at a known zero.
- **Kept:** `householdFuel` (now petrol) and Rail's `fuel` and `fuelImported` (now diesel). Id 75 keeps its id, stack and sites; its FUEL becomes the cuts, and its 25M L of tanks are shared out.
- **No money moves:** at O1, PETROL and DIESEL carry FUEL's band, so inventories are unchanged to the bit, and the audit closes the month after a load.
- **Compatibility:** an older build refuses a format-33 save, and a converted save reloads identically. Later fields (`crudeMix`, `idleMonths`, `vintages`, `platforms`, `pipelines`, `reserve`, `portMonth`) need no bump, since Gson leaves a missing key alone. Each is added to `ReadPathCheck` and `SaveFileCheck`.

## 4. Premises that change, and the new assertions
| check | before | after |
|---|---|---|
| OilCheck 1, 4 | "FUEL, 1,000 L a tonne" | the slate of the cuts (O1) |
| OilCheck 5 | fuel | PETROL and DIESEL |
| OilCheck 7 | "the bill is 0.7.49's × the level" | the same at O1; the ladder from O2 |
| OilCheck 8 | "a whole plant's worth" | the 60% gate and the package (O5) |
| OilCheck 2 | "3 jobs" | 1 (O7) |
| BuildingDataCheck / BuildMenuCheck | 76 / 76 of 76 | 90, 92, 96, 99 / N of N |
| BuildCardCheck | nine groups | plus Ports (O9) |
| ForeignCheck, RailCheck | FUEL | PETROL and DIESEL |
| TradeCostCheck | its goods | plus the nine |
| CarCheck | the fuel price | the ladder (O2) |
| ManufacturingCheck, VanCheck | – | lubricants and van diesel (O6) |
| RoadCheck 4 | the paving price | plus bitumen (O6) |
| InfrastructureCheck | the band | the band with a port (O9) |

- **RefineryCheck** (O4/O5): units balance to the litre; hydrogen caps; the 3:1 cut and the burned rest; crude units alone equal O3's slate to the bit; spreads to the bit; each gate refuses on a fixture that causes it; the package reproduces the four rows of §2.4; only a kind idle for 6 months is shed.
- **PortCheck** (O9): the coast gate; the factor equals its formula exactly; no port gives a bit-identical band; sea is used only when cheaper; calls = tonnes ÷ cargo; the class fits the free room; a boat is pure in (call, t); a frame touches only the routes on screen.
- **WellCheck** (O7/O10): the profiles to the bit; retirement at 263 and 348 months; refills; dry and sea sites kept apart; slots ≤ 12 and ≤ the field's sites; depth ≤ 150 m; W conserved; shuttle freight to the cent; the pipeline passes on a big field and fails on a small one.
- **OilCheck** gains: §3 on copies of m4000, jerus-1007a, city600 and city2400 (the litres sum, money is exact, and the audit closes for 12 months); the reserve's cash, exact; O6's buyers foot to the balance of payments.
- **WorldCheck** gains: the shelf is `SHELF_SHARE` ± 0.5 pt; grades are ⅓ ± 2 pt and pure; totals are unchanged.

## 5. Build order
**Every batch:** one fresh Opus implementer, about 2 hours, in `/home/claude/cbs`, with a version bump and a `GameVersion` entry, porting the prototypes. The gate: both builds silent; the suite green except HealthCheck; BuildMenuCheck; `tools.Stale` firm at 0; `endings-check.sh`; the playtest re-baselined only where stated (each move explained) and then rerun byte-identical; then the docs pass. ★ marks a batch that moves the playtest: it needs the 16-seed ensemble against the last label.

| batch | version | files and methods | saved · checks · playtest | the trace |
|---|---|---|---|---|
| **O1 ★ Goods and the split** | 0.7.71, SAVE_FORMAT 33 | `Good` (O1 prices, `litresPerTonne`, FUEL retired); `Refining` (makes the nine from the medium cuts by §2.3's leftovers and residue cut, so O4's flow can equal it; tanks shared; K's gate in petrol + diesel); id 75; `Motoring`; `Rail` ("Diesel"); `NationalAccounts.HELD`; `tradeByGood`; `MoneyAudit` names; `EconomyManager`; `FuelSplit`; `Game`'s load; `ui/Icons`; `TradeScreen` and `BuildCard` words; `LongPlaytest.fuelBoughtAbroad()` | SAVE_FORMAT 33, §3 · OilCheck's §3 and slate; §4's FUEL premises · `fuelBoughtAbroad()` reads petrol + diesel | moves from the first refinery (pt0762: m2,526). A tonne now makes 70 L of petrol and 169 L of diesel (256 L less the residue's cut), not 1,000 L of fuel, so the gate passes about 4× as often |
| **O2 ★ Petrol and diesel on the ladder** (waits for Jerus, §6 A) | 0.7.72 | `Good` O2 prices; `FUEL_LITRES_PER_TONNE` = 18, a literal with its history; OilCheck 7; CarCheck | none · OilCheck 7, CarCheck · none | moves from the first car's fuel (pt0762: m26): drivers' fuel −63%, the railway's −59%; cars gain on transit |
| **O3 ★ Grades and depth** | 0.7.73 | `Deposit.grade()`; `World` (`shelfTheta`, `depthAt` and its constants); `LandManager` (`liftedByGrade`, dry and sea sites); `Refining` (`CUTS`, the mix) | Refining `crudeMix.*` · WorldCheck; OilCheck's grade slate · none | byte-identical until the first well (pt0762: m812) |
| **O4 Units and the flow** | 0.7.74 | `RefineryFlow` (from `spread.py`'s `solve`); `refineryUnit()` and `feedPerMonth()`; ids 76–90; Refining's reads; cards. The player may order units; the investors don't yet | none · RefineryCheck (flow); BuildingDataCheck 90; BuildMenuCheck · none | byte-identical (asserted) |
| **O5 ★ The spread planner** | 0.7.75 | `Refining.plan()`, `cityValue`, `feedValue`, `spread`, `packageEstimate`, `mayRetire`, `idleMonths` | Refining `idleMonths.*` · RefineryCheck (planner); OilCheck 8 · none (the investors) | moves; expect no refinery on imported crude at the playtest's size |
| **O6 ★ Phase-1 buyers** | 0.7.76 | `Sector.runFleet()`; `Game.drawBitumen()`; `ConstructionControl`'s paving; `BuildAdvice`'s road price; the Automotive and Manufacturing templates | none · RoadCheck 4, ManufacturingCheck, VanCheck; OilCheck's BoP · none (paving never fires in the default run, N1) | moves from the first van |
| **O7 ★ Declines and one post** | 0.7.77 | `Oil` (`vintages`, `getCapacity`, `wornOut`); `Game.runRetirement()`; id 74; dry sites | Oil `vintages` · WellCheck (land); OilCheck 2 · none | moves from the first well |
| **O8 Tank Farm and reserve** | 0.7.78 | ids 91–92; the pantry; the city seller in `Markets.clear()`; `fillReserve` and `releaseReserve`; two audit lines; `DataSave.reserve` | `DataSave.reserve` · OilCheck's reserve · none | byte-identical |
| **O9 ★ Ports** (waits for `SEA_FREIGHT_SHARE`, §6 C) | 0.7.79 | `BuildingType.PORTS`; ids 93–96; `Ports`; `Good.cargo()`; `Rail.haul()` step 5; `InfrastructureManager`; `BuildAdvice` PORTS; `portMonth`; `BoatSchedule` (from `BoatProto`) | `DataSave.portMonth` · PortCheck; InfrastructureCheck; BuildCardCheck · build a terminal when a kind's boundary tonnes pass 60% of a berth's month and the city owns coast | moves |
| **O10 Offshore and pipelines** (waits for Jerus, §6 B) | 0.7.80 | ids 97–99; `Oil` (`platforms`, `pipelines`, `payShuttle`, `pipelinePays`, the three-kind planner) | Oil `platforms`, `pipelines` · WellCheck (sea) · none | moves only from the first platform |
| **O11 The refinery pictogram** (UI) | 0.7.81 | `RefineryView`, its check, the Refining section; a words probe | none · RefineryViewCheck, words · none | byte-identical; Jerus's PC check |
| **O12 The oil industry screen** (UI) | 0.7.82 | `OilView`, its check, Oil's panels and the reserve's levers; a words probe | none · OilViewCheck, words · none | byte-identical |
| **O13 The map: campus, sea and boats** (UI) | 0.7.83 | `CityMap`'s campus deal; `TilePainter` (platform, ring, quay, pipe); `MapView` draws `BoatSchedule` on the game clock. MapCheck: each thing drawn once, boats pure in t, a frame's boats ≤ 0.5 ms at 10B (measured 0.38) | none · MapCheck · none | byte-identical; a 60 fps pan on the PC |

## 6. Still to decide, risks, and what to measure first
**Needs Jerus before building:**
- **A, before O2:** price petrol and diesel at wholesale? Yes means the research's ladder: bills fall 63% and 59%, and the refinery economics are real. No keeps the pump price, and the teapots come with it (`out/spread-3.txt`'s P runs: 6 Oil Refineries and 27 small units in the playtest city). A third way, later: wholesale plus a fuel duty paid to the treasury.
- **B, before O10:** the per-well platform (§2.7), in place of an 18,000 b/d platform that no generated shallow field can feed.

**Needs a source, not Jerus. C, before O9:** `SEA_FREIGHT_SHARE`. The research gives berths and ships, but no sea freight a tonne; a short research pass will do.

**★est. to confirm** (none blocks a batch):
- the grades' ⅓ shares;
- naphtha at 1,351 L a tonne and lubricants at 1,127;
- `SHELF_SHARE` 8.86% and `SHELF_BREAK_M` 140;
- a van's 240 L a month;
- lubricants at 8 L a car, 10 L a tonne of machinery and 2 L a tonne of fabricated steel;
- the Tank Farm's D$190M and 10 posts;
- the jacket's 12 posts;
- the tanker terminal's 4 ha.

**Risks:**
- **Unbounded exports.** Gas oil's best use is lubricants (1.89× crude). The 10× city built 10 Lube Plants, 4% of its crude against a real 1% [R37]. The crude units bound them; measure at O5, and past 2% give lubricants a bounded world demand (the research's rule for ships).
- **The playtest may lose its two refineries** at O2 or O5, since both run on imported crude. Watch the fuel imports' share of GDP and `OIL_FUEL_IMPORTS_SHARE`.
- **The gates are close.** An Oil Refinery on its own light crude clears by 11%; on medium crude it fails by 22%. The real hurdle and the standing costs decide.
- **Joint products.** A product with no buyer here fills 80% of its tank share before it ships. Check at O1 that nothing is `writtenOff`.
- **Scale.** The flow is O(kinds) and `vintages` O(months), both cached. Boats cost ≤ 0.4 ms a frame and 9 ms a month at 10B. JavaFX's cost to draw 1,429 boats is unmeasured: check it on the PC at O13, and fall back to dots at L1.

**Measure first:**
1. O1's refinery count and fuel imports.
2. O2's car and transit shares.
3. O5's money gate at the real hurdle.
4. O6's van diesel against the payrolls.
5. The lubricants' share of crude.
