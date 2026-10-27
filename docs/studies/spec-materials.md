# The materials chains and zoning: iron redone, coal, copper, wood and stone, the build spec, 2026-10-08 (revised the same day)

Read-only study of 0.7.80 (`/home/claude/cbs-0780`), revised on 0.7.83 (`/home/claude/cbs-0783`) with Jerus's answers to A–E and the project doc `claude/spec-zoning.md` (its Z1–Z13, §6 and §7), from `claude/materials-chains-research.md` (the study: its §s, its Q1–Q22 answers and its sources [I] [C] [K] [W] [S]), after `runs/spec-oil.md` and the fixO1–O6 notes. Prototypes in `/home/claude/scratch-materials/`: `proto/balance.py` (the study's four cases rebuilt, then struck as the game's investors strike them in three real cities: `out/balance.txt`, with each order's ground `out/balance-land.txt`); `proto/gen_buildings.py` (every building and good row: `out/buildings-materials.json`, `out/goods-materials.txt`, `out/gen.txt`); `proto/src/.../MatProbe.java` (prices, wages, the hurdle, land and cars in copies of city600 m612, city2400 m2412 and Jerus's 1007a autosave m416: `out/matprobe.txt`); `FieldProbe.java` (field types on four worlds and three cities: `out/fields.txt`); `TypeTiming.java`; `proto/forest.py`; `proto/earth_types.py` (each `out/<name>.txt`); C's and D's arithmetic `out/c-and-d.txt`. Timings are medians of 7 rounds, 3 repeats, on 2 shared cores. Each figure is the study's, measured, derived (sum shown), or ★est. with its reason.

## 1. Summary for Jerus
1. **Zoning comes first** (Z1, Z2): a fifth Policy area where you hold ground for an industry, price it from full to free, and set the forest law (protected, working, open). With nothing set, the city plays as today.
2. **Seven new industries, two renamed:** Mining becomes Iron Mining, Heavy Industry becomes Iron & Steel (saves keep the old names inside); Coal Mining, Quarrying, Cement & Concrete, Copper Mining, Copper Refining, Forestry and Wood Products are new. 24 industries in all.
3. **37 new buildings now, 2 with the construction upgrade.** The Iron Mine lifts 8× more; the Electric Arc Furnace (the old Steel Foundry) and the Mini-Mill run on scrap. **26 new goods now, 2 later** (raw coal is a stream, not a good); steel, and with it cars, vans and trains, move to the world's price.
4. **Each field has a type** at the real shares, within 0.6 points on four worlds; world totals stay exact to the tonne.
5. **One rule builds every works:** whatever earns most on its whole cost at the city's own prices, whoever pays, with zoned ground at the zone's price (your A). It is the oil refinery's planner, shared.
6. **Measured in three real cities:** almost nothing pays on imported feed for export; replacing imports does, which is the intent.
7. **The steelworks and the pulp mill make their own power;** scrap comes from old cars and demolitions (a Scrap Yard pays 70%).
8. **Forestry:** crews only while the forest it may cut lasts 120 months; plantations only on ground zoned for it; wood under a new building sold as a salvage cut. The forest the city keeps is the share it protects.
9. **Sixteen batches after the oil chain** (Z1–Z2, I1–I14), 0.7.91 to 0.8.0: eleven for the model, five for the screens. Eight move the playtest and need the ensemble.

**Decided by Jerus (2026-10-08; spec-zoning 5 and 7), and where each lands:** A, the whole-cost test for every SpreadPlanner user (I2); B, plantations only on ground zoned for Forestry at the zone's price, no licence (batches Z1, I9); C, cars, vans and trains move with the steel in them (I3); D, US$0.40 a tonne in the ground kept (I3); E, a crew only while the forest Forestry may cut lasts 120 months (I9); Z1–Z13 (batches Z1, Z2, I9).

**Still open (none blocks a batch before I2):**
- **A outside the works.** Jerus said "every order". **Decided (Jerus, 2026-10-18): yes, as batch I2b, queued.** SpreadPlanner's users get it at I2, byte-identical: O5's counterfactual (ptW) found refiners never hold cash when they weigh. Every other sector's orders go through `Game.Slices`, which still tests only what is borrowed. O6 found filling stations pass on their own cash. Moving those orders to the whole cost would move the playtest; it is the coordinator's call whether it is in A's scope (one ★ row, I2b, if yes). **Already done (found 2026-10-27, when the row came up):** 0.7.113 (batch I2b, 2026-10-11, decisions A and A13) put every `consider()` order and the sea's on the whole cost (`Game.orderGate()`, SpreadPlanner's own `ON_ITS_WHOLE_COST`; InvestCheck's "an order is tested on its whole cost, whoever pays"); this line, written before it, is what the 2026-10-18 row read. What still differs from SpreadPlanner is the rate alone: `Game.Slices` strikes the whole cost's interest at the rate the order's financing carries (`projectRate` of what it borrows, or a cheaper bond's blend), SpreadPlanner at `projectRate` of the whole cost.
- **The plantation's size** (spec-zoning 8.4, not decided): 1,000 ha needs 10 km² of zone; a 100-ha woodlot (167 m³ a month) would let a small city's zone fill in steps. The spec keeps 1,000 ha. **Settled (Jerus, 2026-10-18):** the 100-ha Woodlot stands beside the 1,000-ha Tree Plantation, so a small city's zone fills in steps.

**What the code makes impossible, decided here (★):**
- `Good.COKE` is petroleum coke, and names are saved, so the study's coke is **MET_COKE** ("Coke").
- ROM_COAL cannot be a market good that "sells as thermal coal tonne for tonne": it is a stream in Coal Mining's flow (§2.10), as the refinery's cuts are.
- No mine is recorded on a field, so "20 sites in one field" is a pure packing of the counts (§2.4).
- `LandMarket.bareGround()` would call every forested offer a deposit once forest is priced (I9), so it leaves forest out (§2.4).
- Forest is dry ground today and a building on it clears nothing (`LandManager.getForestKm2()` is "part of its dry ground"; `allocate()` takes dry square feet): Z1's clearing rule (Z7) makes it explicit.

## 2. The design, on the code
### 2.1 Sectors (`Sectors.KEYS` appended in Q21's order) and two bases, so a sector class stays a declaration
- **`sectors/ExtractionSector`** (abstract): §2.6b's plan, `groundLimit()` by type, retirement when its type is worked out. Mining moves onto it at I2, byte-identical.
- **`sectors/ProcessingSector`** (abstract): `plan()` = SpreadPlanner (§2.6), captive use (§2.7), idle-then-shed (extras `idleMonths.<id>`, `mayRetire()`, O5's rule).

| key (saved) | label | class, base | type | makes / uses |
|---|---|---|---|---|
| "Mining" | **Iron Mining** | Mining, Extraction | MINING | IRON (ore) / DIESEL |
| "Heavy Industry" | **Iron & Steel** | HeavyIndustry, Processing | HEAVY_INDUSTRY | SCRAP, PELLETS, PIG_IRON, MET_COKE, STEEL / IRON, SCRAP, MET_COKE, PIG_IRON, COKING_COAL, THERMAL_COAL, LIMESTONE, LIME |
| "Coal Mining" | Coal Mining | CoalMining, Extraction | MINING | THERMAL_COAL, COKING_COAL / DIESEL |
| "Quarrying" | Quarrying | Quarrying, Extraction | MINING | LIMESTONE, AGGREGATE, DIMENSION_STONE |
| "Cement & Concrete" | Cement & Concrete | CementConcrete, Processing | HEAVY_INDUSTRY | CEMENT, LIME / LIMESTONE, AGGREGATE, THERMAL_COAL |
| "Copper Mining" | Copper Mining | CopperMining, Extraction | MINING | COPPER_CONC, COPPER (leach) / ACID |
| "Copper Refining" | Copper Refining | CopperRefining, Processing | HEAVY_INDUSTRY | ANODE, ACID, COPPER, COPPER_ROD / COPPER_CONC, ANODE, COPPER, COPPER_SCRAP |
| "Forestry" | Forestry | Forestry, Extraction | MINING | SAWLOGS, PULPWOOD |
| "Wood Products" | Wood Products | WoodProducts, Processing | HEAVY_INDUSTRY | LUMBER, RESIDUES, PANELS, PULP, PAPER, WOOD_PELLETS / SAWLOGS, PULPWOOD, RESIDUES, PULP, FUEL_OIL |

Manufacturing (its own plan, unchanged) gains the Wire & Cable Works (COPPER_ROD → WIRE_CABLE). A rename is the label alone; `Equity.COMPANIES` and the households' share arrays grow with KEYS, and an older save restores into the new sectors empty (Sectors' header).

### 2.2 Buildings (`out/buildings-materials.json`: port it; `BuildingManager`'s built-in twins generated from it as O4's `gen.py` did)
Ids come from `nextId` (101 if O8–O10 take 92–100 after the Filling Station's 91), batch by batch in Q21's order (iron, coal, stone, copper, wood). The three changed rows keep theirs:

| id | was → is | makes / uses | posts, kW, cost |
|---|---|---|---|
| 11 | Steel Foundry → **Electric Arc Furnace** | 1,200 t STEEL / 1,320 t **SCRAP** | as now (38; 750 kW; D$30.0M) |
| 12 | Steel Mini-Mill | 6,000 / 6,600 **SCRAP** | as now |
| 14 | Iron Mine | **20,000 t** IRON; 60,000 L DIESEL | 60 (45/12/3/0); **600 kW**, water 20; cash, points, materials × 4.632 (D$45M); roadLoad 3,360 |

- **The 37 new, in id order:** Scrap Yard, Pelletising Plant, Coke Ovens, Mini Blast Furnace, Blast Furnace, Oxygen Steel Shop, Large Mini-Mill (Iron & Steel, I4) · Large Open-Pit Mine, Magnetite Mine & Concentrator, Large Magnetite Complex (Iron Mining, I5) · Surface Coal Mine, Large Surface Mine, Underground Coal Mine, Coal Preparation Plant (I6) · Quarry, Large Quarry, Dimension Stone Quarry, Cement Plant, Large Cement Plant, Lime Kiln (I7) · Copper Mine & Mill, Large Copper Mine, Leach & SX-EW Plant, Copper Smelter, Electrolytic Refinery, Scrap Copper Refinery, Rod Mill, Wire & Cable Works (Manufacturing's) (I8) · Logging Camp, Harvesting Operation, Tree Plantation (I9) · Sawmill, Large Sawmill, Panel Mill, Pulp Mill, Paper Mill, Wood Pellet Mill (I10).
- **Figures** are the study's §2.4, §3.3, §4.3, §5.4 and §6.4: makes, uses, posts, kW, water, hectares, cost.
- **Conventions (★, the generator's header):**
  - **Money:** cash = the study's all-in cost ÷ 1.3, so materials = cash ÷ 60 and points = cash ÷ 2 (O4's ratios).
  - **Posts:** the study's tier split where given, else its family's (mines 75/20/5/0, large mines 60/27.5/10/2.5, heavy plants 54/35/9/2, light 50/46/4/0, pulp and paper 42/37/17/4), by largest remainder, ties to the less skilled (O4-4).
  - **Power:** the study's kW; where none, the research's kWh a unit ([W27] [W34] [S18] [S31]; pellets and lime est.).
  - **Water:** the study's units, else the Steel Mini-Mill's 320 for 3,700 kW (O4's 0.25 a kW would give a Blast Furnace more than a pulp mill).
  - **Land:** the study's hectares; a mine's, sites × its resource's site; else the Steel Foundry's (heavy) or the Fabrication Shop's (light) ground per D$ of cost.
  - **roadLoad:** tonnes in and out at the Iron Mine's 420 for 2,500 t (mines) or the Steel Foundry's 110 for 2,520 t (plants).
  - **stock:** 3 months of a stockable output (the Vehicle Works' ratio).
  - **Fuel:** DIESEL where the study names it, scaled by tonnes to the large sibling; the Paper Mill's 850,000 L as FUEL_OIL, the refinery's first fuel-oil buyer.
  - **consumables:** the rest of the study's runs at parity, so its four cases hold. `out/gen.txt` lists each; none is below nothing.

### 2.3 Goods (`Good`, appended; the rows in `out/goods-materials.txt`)
- **26 rows** at the study's import and export prices, its eight widened bands (§1.2) included, freight .375 × the wedge (Good's rule):
  - iron: SCRAP, PELLETS, PIG_IRON, MET_COKE;
  - coal: THERMAL_COAL, COKING_COAL;
  - stone: LIMESTONE, AGGREGATE, DIMENSION_STONE, CEMENT, LIME;
  - copper: COPPER_CONC, ANODE, COPPER, COPPER_SCRAP, COPPER_ROD, ACID, WIRE_CABLE;
  - wood: SAWLOGS, PULPWOOD, LUMBER, PANELS, RESIDUES, PULP, PAPER, WOOD_PELLETS.
- **Stockable:** MET_COKE, both coals, CEMENT, COPPER, LUMBER, PANELS, PULP, PAPER, WOOD_PELLETS; each joins `NationalAccounts.HELD` on the end. The rest are flow goods, as ore is: a mine or mill ships what it makes.
- **Traffic:** GOODS for LUMBER, PANELS, PAPER, WIRE_CABLE, COPPER_ROD and DIMENSION_STONE; BULK for the rest.
- **m³:** a constructor argument `tonnesPerCubicMetre` (NaN otherwise) and the case "cubic metre" in `tonnesPerUnit()`: green logs, pulpwood and residues 0.9, lumber 0.5, OSB 0.62 [W35] (★est.).
- **Changed:** IRON relabelled "Iron ore", .41/.14 → **.115/.085** (freight .01125); STEEL 1.284/.847 → **.650/.470** (.0675); FABRICATED_STEEL –/2.21 → **–/1.545** (freight .3398, its ratio kept); MACHINERY 14.3/9.0 → **13.4/8.1** (freight 1.98, the same wedge); and by the steel in them (C, Q6b's rule: fabricated steel −.665 and machinery −.9 a tonne; `out/c-and-d.txt`): CARS 44/36 → **41.3125/33.3125** (3.5 t + 0.4 t a car; freight 3.0, the same wedge), VANS 72 → **68.135** (5 t + 0.6 t), ROLLING_STOCK 2,400 → **2,302.2** (120 t + 20 t a set).
- **Not rows:** ROM_COAL (§2.10); CONCRETE and ASPHALT with the construction upgrade (Q18).

### 2.4 Deposits: types, sites and the ground by type (`Deposit`, `Resource`, `World`, `LandManager`, `CityLand`, `Game`)
- **`Deposit.FieldType`:** HEMATITE, MAGNETITE, THERMAL, COKING, SULPHIDE, OXIDE, LIMESTONE, HARD_ROCK, NONE.
  - `Deposit.type()` draws as `grade()` does, `World.unit(World.mix((cell << 24) ^ (index << 4) ^ kind.ordinal() ^ TYPE_SALT))`, against `TYPE_SHARES` (study 1.4: .75, .85, .80, .70 for the first type). Oil and forest are NONE.
  - Pure: nothing is stored, no tonne moves.
  - Measured (`out/fields.txt`, about 7,000 land cells on each of four worlds): every share within 0.53 points; 0 cells whose types fail to sum to the cell's total exactly; 15.8 ns a draw.
- **On Earth (E3)** a type leans on its cell's geology by odds ratios (★est.): coking coal ×3 in fold belts, ×0.5 on shields; limestone ×2 in basins and on shelves, ×0.5 in fold belts, ×0.25 on shields; iron and copper flat. One constant per resource keeps the Earth-wide share real (`out/earth-types.txt`: limestone .886 in basins, .493 on shields, 70.00% in all). Generated worlds draw flat.
- **`Resource.typesOf()` and `goodOf(type)`:** IRON for both iron types; THERMAL_COAL for both coals (washing is Coal Mining's flow); COPPER_CONC or COPPER; LIMESTONE or AGGREGATE. `good()` stays one pricing good per resource: IRON, THERMAL_COAL, COPPER, AGGREGATE, PULPWOOD.
- **`inGroundShare`** (study §8): coal 1/200, copper 1/100 (a tonne of metal at cathode's price), stone 1/400, forest .02 (I9). Iron **1/212.5** (D: US$0.40 a tonne of ore at .085, the founding field's US$180M kept; I3).
- **`LandMarket.bareGround()` ignores FOREST** (I9, when forest is priced), or every forested offer becomes a deposit to `Game.bestOffer()` and the auto-builder (N5).
- **The template's `site`** = {resource, type or ANY, sites, dig}, dig being the tonnes of field a unit made takes: magnetite rock 3; copper metal .341 a tonne of concentrate and 1.4 a tonne of leach cathode; coal seam 1/.95 at the surface, 1/.80 underground; dimension stone 3. `Game.siteOf()` reads it (a template without one is not a mine); `committedOn(r, type)` counts sites, not buildings.
- **"20 sites in one field":** `LandManager.fits(r, type, standing, extra)` packs the standing counts onto the held fields of the type, first fit decreasing, in acquisition order, typed buildings before ANY and ANY quarries onto limestone first. A new building fits if the packing with it succeeds. Pure, nothing saved; a standing building it cannot place (an older save's mine) keeps standing and counts against free sites first.
- **The ground by type:**
  - `LandManager.extracted` gains `extractedByType` (resource → its two types). `getSites(r, type)` and `getRemaining(r, type)` are counted per holding as O3's sea sites are, capped at what the holding listed; ground listed by fiat is the first type (O3-3).
  - `extract(r, type, t)` raises the type's E and the resource's, so W = unowned + Σ remaining + Σ E stays exact.
  - Old saves: `typeRuns(r)` (O3's `oilRuns()` made general) splits each E by walking the held fields in acquisition order, to the bit, then saves it (`DataSave.depletionByType`).
- **Measured holdings:** Jerus's 1007a has 13 iron sites, 10 hematite and 3 magnetite (one 3-site field), and his three Iron Mines fit hematite; city2400 18 and 4; city600 10 by fiat, hematite.
- **Near a founding site (four worlds):** coal 0–2 fields within 50 km, none coking; copper 0–2 (seed 4127's two hold 47 sites); stone 6–15 fields; oil 18–31.

### 2.5 The template's new fields (`BuildingsTemplate`, `BuildingCatalog`; `BuildingDataCheck` compares each, json against the twin)
- `gen` (kW made, §2.8);
- `consumables` (world k$ a month, paid abroad at the exchange factor through the imported-service line O10 adds for shuttle freight: `otherInputs["Consumables"]`, imports, TRADE scope);
- `site` (§2.4); `wash` (t of raw coal a month, §2.10); `oldMetal` ({STEEL or COPPER: t a month}, §2.9); `plantation` ({ha, m³ a ha a year}, §2.11).
- DIESEL and FUEL_OIL are ordinary `uses` in litres, bought from Refining or the world: the oil chain's second wave of buyers.

### 2.6 The planner: O5's `sectors/SpreadPlanner`, shared (Refining its first user since 0.7.82)
O5 built `cityValue()`, `Candidate`, `City` (cost, running, standing, rate, land, staff, money), `judge()` over the gates `Gate.FEED/LAND/STAFF/MONEY`, `best()`, the idle book (`noteMonth`, `mayShed`, extras `idleMonths.<KIND>`) and **`MoneyGate`**, one replaceable piece. I2 adds beside them:
- **A, the whole-cost rule for every user:** the default `moneyGate` becomes `ON_ITS_WHOLE_COST` (the interest at `projectRate(cost)` × `PROFIT_OVER_INTEREST` on the whole cost, whatever the sector's cash); `ON_ITS_BORROWING` stays as a named history. Refining: byte-identical (O5's ptW and its ensemble columns: refiners never hold cash when they weigh). The cost is `plans.getCostOf(t, 1)`, its ground at the zone's price (batch Z1, §2.15).
- **`outValue(g, u)` and `inValue(g, u)`**, the marginal split of `cityValue`: the net import price for min(u, last month's imports of g), the net export price for the rest (with neither, the local price up to the forecast's room); `inValue` is the mirror (export price for what the city exported, import price for the rest). The month's trade by good is Σ `unitsImported/Exported` over the sectors (saved since A1).

**a. Processing (`ProcessingSector.plan()`):** a candidate earns Σ makes × `outValue` − Σ uses × `inValue` − `runningCostOf` − consumables × factor − `standingCostOf` (the zone's assessment, Z4) + gen surplus × tariff; `judge()` asks LAND (`freeFor(sector)`, batch Z1), STAFF and MONEY; the best earns ÷ cost is ordered, one in flight; a kind whose earns is below nothing for `IDLE_MONTHS` is shed.
- **The hurdle measured:** O5, a month after load, 1.25 × `realTestRate(projectRate)` ÷ 12 of the cost: city600 0.138%, city2400 0.194%, Jerus 0.172%, the playtest 0.13–1.38% (median 0.44%) — not the 0.06–0.10% this spec first read at load.
- **The works measured** (`out/balance-land.txt`, city600 / city2400 / Jerus, after repairs, tax and ground at the business price):
  - on imported feed for export, every processing building earns below nothing but the Pulp Mill (−.03 / −.28 / **+.18%**, a hair over Jerus's 0.172%) and the Pellet Mill (+.01 / −.30 / +.01, under every hurdle);
  - replacing imports on imported feed clears for most: Large Mini-Mill .66–1.14%, Blast Furnace .33–.80%, Lime Kiln 2.35–3.32%, Large Cement Plant .22–.44%;
  - the arc furnaces and the Paper Mill clear only on the city's own feed replacing imports, and not in city2400 (study 2.5, 5.6).

**b. Extraction (`ExtractionSector.plan()`):** Mining's rule, by type and size. Each candidate whose `site` fits (§2.4) and whose ground `freeFor(sector)` holds is scored by `estimatedMonthlyProfit ÷ cost`, the best winning (Q19's sizes), one order at a time, through the same MONEY gate from I3 (I2 moves Mining onto the base with today's test, so I2 stays byte-identical). Forestry: §2.11.

### 2.7 Captive use (`Sector.captive(g)`, new hook; no sector has a captive good today, so byte-identical)
- A good a sector both makes and uses (coke, pig iron and scrap in Iron & Steel; anode and cathode; residues and pulp) goes first to its own need at the rate, min(made, needed).
- The market sees the net: `bid()` drops it; `produceFlow`/`produceStock` offer the rest. No sale or purchase is booked, so revenue and inputs are not inflated. `Output.captive` records it for SectorFlow and the pictograms.

### 2.8 The works' own power (`BuildingsTemplate.gen`, `Sector`, `ServicesManager`, `UtilitiesHandler`, `MoneyAudit`)
- Per template, bought = max(0, draw − gen) and sold = max(0, gen − draw). The sector is billed Σ bought × energyRatio × tariff; `Statement.powerSold` = Σ sold × tariff.
- `ServicesManager` sets the grid's consumption from the net draws; `UtilitiesHandler.setWorksPower(kW)` adds the surplus to production, unscaled by the utility's staffing; the city's utility pays it.
- Two audit lines, DOMESTIC: "+ sectors PowerSold", "− utilities PowerBought".
- Coke Ovens sell 5,800 kW, the Blast Furnace 8,600, the Pulp Mill 13,500; the Oxygen Steel Shop buys 26,600.

### 2.9 Old metal (`OldMetal`, new; `Motoring`, `Game`'s demolitions, `Manufacturing`, `Rail`)
- **Steel arising a month, at 85% recovery [I52]:**
  - cars scrapped (`HouseholdBalance.wearOutCars()`'s return, today dropped at `Motoring` line 249) × 0.9 t [I63];
  - demolitions: `DemolitionLog`'s month × the template's materials units × 0.3 t a unit (★est.: study 9.3's 3 t in a 10-unit House);
  - Manufacturing's offcuts: 0.05 t a tonne of FABRICATED_STEEL made (1,260 → 1,200);
  - rolling stock retired: 300 t a set.
- **Copper:** 23 kg a car [K45]; 0.1 t a materials unit (★est.: 150 kg in a House).
- **Buyers:** Iron & Steel's Scrap Yards take steel up to their `oldMetal` at the rate, at 70% of SCRAP's export price (Q8), paying the households (cars), the demolished building's owner and Manufacturing (offcuts) pro rata. The Scrap Copper Refinery takes copper the same way at 70% of COPPER_SCRAP's (★), its bid being the rest. Whatever is left goes to landfill.
- **Saved:** `DataSave.oldMetal` (steel and copper: arisen, bought). **Audit:** "+ households OldMetalSold", "+ sectors OldMetalSold", "− sectors OldMetalBought".
- **Measured:** city2400's 31,407 cars yield 134 t of steel a month, 1.3% of one yard.

### 2.10 Coal's wash, and limestone sold as aggregate
- **`CoalMining.flow()`** (pure): raw = the makes of templates on COKING sites (the Underground Coal Mine's 250,000 t); washed = min(raw, Σ `wash`). COKING_COAL = .55 × washed; THERMAL_COAL = the surface mines' + .20 × washed + (raw − washed); spoil .25 (study 3.3). `getCapacity()` reads it, so an underground mine alone sells its raw coal as thermal, tonne for tonne.
- **Quarrying (Q17):** quarries packed on limestone make LIMESTONE, those on hard rock AGGREGATE; the Dimension Stone Quarry makes DIMENSION_STONE. `shipUnsoldFlow(LIMESTONE)` books nothing and carries its tonnes into the month's AGGREGATE output (LIMESTONE clears first, by enum order); the ground drawn stays limestone.

### 2.11 Forestry (`Forestry`, `LandManager.endMonth()`; on Z1's zones and law)
- **The forest:** O = owned forest km² × 13,700 m³; Z1 splits it protected / working / open (§2.15). Crews `extract(FOREST)` from the **cuttable** forest only: remaining less the protected wood (Z8: working, open, and what its plantations regrow). No sites, no cutting limit (Q14).
- **Crews (E, decided):** a candidate only while the cuttable forest ≥ `FOREST_CREW_MONTHS` **120** × (the cut of every crew with it): a crew's working life, not its payback (Z13). A Logging Camp then needs 17.5 km² of cuttable forest, a Harvesting Operation 109 km²; a 10 km² forest gets none (spec-zoning 3.2).
- **Regrowth:** E ← max(0, E × (1 − 1/240) − `PLANTED` × plantations), with `PLANTED` = 20 m³ a ha a year × 1,000 ha ÷ 12 = 1,667 m³ a month [W4], as a managed estate of every age (★). A plantation is regrowth, never timber past the forest's full stock, so W stays exact. Cleared ground (Z1's C) never regrows.
- **Planting (B, decided; Q15, Z3):** in `Forestry.endOfMonth()`, one a month while the month's cut (salvage not counted, ★ as spec-zoning 2.2 recommends) exceeds regrowth and Forestry's zone holds 1,000 ha free, at the zone's price, paid from Forestry's cash outside the interest test. **No Forestry zone, no plantation.** Its ground is working forest from then on (spec-zoning 2.3).
- **The salvage cut (Z7):** when Z1's clearing takes standing forest, `Forestry.salvage(m³)` adds it to the month's SAWLOGS and PULPWOOD (half each), sold like any cut, billed to Forestry by the builders who cleared it (`billForService("Construction", "Salvage cut", …)`, ★) at its crews' running cost a m³ (with none standing, a Harvesting Operation's at today's wages). No crew, and outside E's test. Before I9 the wood is lost (batch Z1).
- **The page** shows where the forest settles (Q13): with the law, at its protected share (spec-zoning 3.3); with none, depletion 240 × cut.
- **Measured** (`out/forest.txt`; spec-zoning 3.2's model): mockup 4's woods settle at 29.1% standing; E at 120 months alone strips a big forest in 17 years instead of 5; with 40% protected it settles at 40% with 7 crews.

### 2.12 The buyers
- **Now:**
  - IRON: pellet plants and blast furnaces; SCRAP: the arc furnaces and the oxygen shop; MET_COKE and PIG_IRON: the route itself (captive); STEEL: Manufacturing;
  - coal: the pellet plants, blast furnaces and kilns (thermal), the coke ovens (coking); LIMESTONE: blast furnaces and kilns; LIME: the oxygen shop;
  - copper goods down the chain; ACID: the leach plants; COPPER_SCRAP: the scrap refinery;
  - logs, residues and pulp: Wood Products; DIESEL: the mines; FUEL_OIL: the paper mill.
- **Exported until a buyer comes:**
  - WIRE_CABLE, LUMBER, PANELS, CEMENT, AGGREGATE and DIMENSION_STONE: the construction upgrade;
  - PAPER: Retail's and Business Services' basket (study 10.2), so the Paper Mill waits;
  - WOOD_PELLETS and THERMAL_COAL to power: the electricity run (Q10).

### 2.13 The pictograms and screens (oil's pattern: O11's `RefineryView` and drawing)
- **`IndustryView.of(Game, Sector)`,** pure and checked like SectorFlow, one view for each of the 9 industries:
  - sources: fields by type drawn as vessels filled to what remains, or feed by origin (own fields, other sectors, old metal, imports hatched);
  - each kind of unit: its run, its earns ÷ cost, and the gate it fails, in SpreadPlanner's words;
  - products: made, captive, home by buyer, exported;
  - power made, used and sold;
  - Forestry's settling point and planting line.
- **`ui/SectorScreen`'s OPERATIONS** draws it with O11's ribbons, strip and tank pictogram, a roof per industry (furnace, mill, kiln, pit), at 1,389 × 868 (study 11).
- **`BuildCard`/`BuildScreen`:** a card reads its sites and type ("stands on 20 sites of one hematite field"), the planner's earns and gate, and the new group words.

### 2.14 Hooks for the construction upgrade (not designed here)
- The builders will draw the new goods as paving draws bitumen (`Markets.draw()`, O6's `Game.drawBitumen()`); `outValue` then brings the plants after the imports by itself.
- MATERIALS and the Construction Materials Plant stay. Study §9's per-m² and per-km table is the upgrade's input. CONCRETE, ASPHALT and their two plants take the next ids then.

### 2.15 Zoning and the forest law (batches Z1, the model, and Z2, the screens; spec-zoning)
In parentheses after a rule, Z1–Z13 are Jerus's decisions (spec-zoning 5); elsewhere Z1 and Z2 are the batches.
- **`Zoning`** (new, model, saved): zones, one a sector at most, each {sector key, size as km² or a share of dry ground (Z2, the share the default, re-read each month), price share 0–1 of the going business price (Z3)}; and the law {protected share p, kept-from-building share k ≥ p} (Z6), default none and 0/0. Read by `LandManager`, `BusinessInvestment`, `EconomyManager`, `PolicyPreview`.
- **The ground (`LandManager`):** dry ground D = bare B + forest F (forest is dry ground today); F splits protected p·F, working (k − p)·F, open (1 − k)·F. Buildable = B + open; free = buildable − allocated.
  - `heldBy(sector)`: free ground a zone holds, bare first, then open forest (Z9), up to its size; short of it, it holds what is free, fills as land is bought, and says so (Z10).
  - `freeForAnyone()` = free − Σ held; **`freeFor(sector)`** = that + its zone's held ground. `BusinessInvestment.plotsAvailableFor()` and `landReason()` read `freeFor` ("no land: 55.9 km² is zoned for others").
  - `allocate(sqFt, sector)` takes the zone's held ground first and records it as the zone's `used`; `release(sqFt, sector)` gives it back to the zone. Un-zoning returns held ground to everyone; used ground stays with its owner.
- **The price (Z3):** `groundPriceFor(sector)` = the price share × `getPricePerSqFt()` for ground in its zone, the going price past it. The city is paid that (`recordSale(sqFt, price)`); `Game.refreshLand()` hands `BusinessInvestment` a price by sector (`landPriceFor(key)`), which `getCostOf()` reads. Ground sold is not re-priced when the share changes.
- **The assessment (Z4):** `EconomyManager.landValueOf(s)` values a sector's zone-used ground at its zone's price and the rest at the going price (× `assessedLandShare` as today); `standingCostOf()` follows. The zone's tax rows are the sector's own offsets (Z5: one dial, two doors), so `TaxPolicy` is unchanged.
- **Farms (Z11):** zoned Agriculture ground does not yield to homes; unzoned fields still do.
- **The forest law (Z6, Z8):** protected wood = min(p × O, remaining), the fullest forest first; `cuttableForest()` = remaining − protected wood. Protected ground is never cut or built on, working ground never built on.
- **Clearing (Z7):**
  - Built ground lies on bare ground first, less the bare ground zones hold for others, and past that on open forest: each month the cleared km² rises to max(cleared, min(open, allocated + bare held by zones − B)); a demolition leaves it cleared (★).
  - A rise of Δ km² takes cut-over ground first: m = min(E, 13,700 Δ) moves from E to C (cleared, never regrowing). Then standing wood: w = 13,700 Δ − m goes into C, and to Forestry's salvage cut (§2.11), lost before I9.
  - Remaining = O − C − E, so W = unowned + remaining + C + E stays exact.
- **`PolicyPreview`:** zoning's before → after, as for every dial: the zone's ground at the going price, the price given up, the tax relief; the law's long-run standing and the crews it carries (spec-zoning 4, boards 2–3).
- **The screens (batch Z2), drawn to the "Zoning Study" boards:**
  - `ui/PolicyScreen`'s fifth area, ZONING (`AREAS`, `AREA_ICONS`): the hub, a page a zone, the forest law, with 0.7.36's dial cards and tray;
  - the land office's "N km² zoned";
  - Build's LAND FREE ("free for anyone · zoned for Forestry ›");
  - NEEDS YOU's GROUND row, naming what zones hold when homes are short.

## 3. The saves
**Zoning (batch Z1), no bump:** `DataSave.zoning` = the zones {sector, kind, size, price share, used sq ft}, the law {p, k} and the forest cleared {km², m³}. An older save reads none, 0/0 and nothing cleared, and loads as today (Gson leaves a missing key alone).

**IronSplit (I3; the next SAVE_FORMAT, 35 if nothing takes it first):** `IronSplit.convert(DataSave)` runs at format ≤ `LAST_IRON_FORMAT`, on names, after FuelSplit and before `Sector.restore()`, in FuelSplit's shape.
- **The market:** IRON's state keeps its price and supply (flow). Its demand and `taken[]` (the mills' buying) move to a new SCRAP state at the same price. The first clearing strikes each in its band (O2-5).
- **Heavy Industry's maps keyed IRON:** the imported part becomes SCRAP (`imported`, the ledger's and the statement's `unitsBought` and `bought.abroad`); the home part stays IRON, so ore sold at home equals ore bought at home, good by good. IRON is a flow good: no stock or pantry. Mining's IRON is unchanged.
- **Buildings:** ids 11, 12 and 14 keep their ids, stacks and books; the logs keep the old names as history. E_iron is split by type with `typeRuns()` (§2.4).
- **No money moves:** IRON and STEEL are flow goods, so nothing held is revalued; C's vehicle prices move no money at the load either: each market keeps its restored price until its first clearing (O2-5). The audit closes the month after a load, and a converted save reloads identically.
- **No bump for the rest** (`depletionByType`, `oldMetal`, `idleMonths.*`, `Statement.powerSold`); each goes into ReadPathCheck and SaveFileCheck.

## 4. Premises that change, and the new checks
| check | before | after (batch) |
|---|---|---|
| LandCheck, BuildAdviceCheck, InvestCheck | free ground = owned − allocated, one price | the same with no zones; `freeFor`, the zone's price and assessment (Z1) |
| PolicyPreviewCheck | four areas | + zoning's before → after (Z1) |
| MiningCheck | arc furnace margin 20–36% of sales on imported scrap, 48–62% on local ore | 5–15% on imported scrap, 20–35% on local scrap; ore .115/.085; the Iron Mine 20,000 t (I3) |
| ManufacturingCheck | steel 1.284/.847; fabricated 2.21; machinery 14.3/9.0 | the new prices; margins in D$ unchanged to the cent (I3) |
| CarCheck, VanCheck, RailCheck | CARS 44/36, VANS 72, ROLLING_STOCK 2,400 | 41.3125/33.3125, 68.135, 2,302.2; Automotive's margins in D$ unchanged to the cent (C, I3) |
| TradeCostCheck, ForeignCheck | their goods | + the 26 rows; IRON, STEEL, the vehicles |
| BuildingDataCheck, BuildMenuCheck | 92 (or the oil chain's count) | + each batch's rows; json = twin on the six new fields (I2–I10) |
| BuildCardCheck | groups | + Coal, Copper, Stone, Wood, Cement; "Iron" and "Steel" → the renamed sectors' |
| GdpCheck | HELD | + the 10 stockable goods |
| LandCheck, WorldCheck | "a resource with no good is free"; totals | iron 1/212.5 (I3); the four new in-ground shares, bareGround ignores forest (I6–I9); W exact by type (I1) and with C (Z1) |
| RefineryCheck 11b | "cash in the till asks only earns > 0" | the whole cost is tested whoever pays (A, I2) |
| MoneyCheck | its lines | + PowerSold/PowerBought and Consumables (I2), OldMetal (I4) |
| 21 harnesses and LongPlaytest naming "Steel Foundry" or "Iron Mine" | by name | by id 11, 12 or 14 (I3) |

**New checks:**
- **`ZoningCheck`** (batch Z1, spec-zoning 6):
  - no zones and the law at 0/0 give today's month;
  - a zone's ground is built on only by its sector, and held ground leaves others' free ground;
  - the city is paid the zone's price, and zoned ground is assessed at it;
  - a zone's tax row moves the sector's offset, the same as Policy › Taxes;
  - protected forest is never cut or built on;
  - building on open forest clears cut-over ground first, and its standing wood is extracted (salvage from I9); W holds;
  - a zone bigger than the free ground holds what is free and fills;
  - a saved and reloaded city is identical.

  I9 adds: E's 120 months on the cuttable forest; no Forestry zone, no plantation.
- **`MaterialsCheck`** (I2; each batch adds a section):
  - the four cases at parity equal the study's to its rounding (`balance.py` part 1);
  - `outValue` and `inValue` split at the month's imports and exports, and the whole-cost gate refuses a cash-rich sector's case A on a fixture;
  - captive books no sale; gen is billed net and the city pays the surplus; consumables go to TRADE;
  - IronSplit on copies of m4000, jerus-1007a, city600, city2400 and jerus-old: maps to the bit, cash to the cent, a 12-month audit, saved and reloaded the same;
  - the old-steel pool and the 70% rule; the wash flow (500,000 → 275,000 + 100,000 + 125,000); limestone carried to aggregate;
  - Forestry's settling point, planting on its zone, the salvage cut's books, and W exact.
- **WorldCheck 8** (I1): shares within 2 points; types partition every cell's total exactly; pure; `fits()` places a 20-site mine only on a ≥ 20-site field of its type.
- **`IndustryViewCheck`** (I11): every unit balances in tonnes; every figure equals the model's getter; pure.

## 5. Build order
- **Every batch:** one fresh Opus implementer, about 2 hours, in `/home/claude/cbs`, with brief-batchO's gate (both builds silent; the suite green but HealthCheck; BuildMenuCheck; Stale 0 firm; `endings-check.sh`; the playtest as the row says, then rerun byte-identical; the docs pass). ★ = moves the playtest: re-baseline, explain each move, and run the 16-seed ensemble against the last label.
- **Versions** follow O13 (0.7.90). Zoning goes first: it can ship before the chain, and I9 needs it. 0.7.100–0.7.105 are safe: the one numeric read of a version, `GridConversion.buildNumber()` (a × 1,000,000 + b × 1,000 + c, format-31 saves only), orders 0.7.100 after 0.7.99. **0.8.0 is I14**, the last batch: oil and the industry chain together (Jerus).
- **Needs first:** O10's imported-service line before I2; O11 (RefineryView's drawing) and O13 (the map's campus) before I11–I14; Z1 before I9.
- **Ids** from `nextId`, 101 if the oil chain takes 92–100 after the Filling Station's 91.

| batch | version | files and methods | saved · checks · playtest | the trace |
|---|---|---|---|---|
| **Z1 Zoning, the model** | 0.7.91 | `Zoning`; `LandManager` (`freeFor`, `freeForAnyone`, `heldBy`, `usedBy`, `allocate/release(sqFt, sector)`, `groundPriceFor`, the law, `cuttableForest`, the clearing and C); `BusinessInvestment` (`landPriceFor`, `plotsAvailableFor`/`landReason` by `freeFor`); `EconomyManager.landValueOf` (Z4); the farms' rule (Z11); `PolicyPreview`; `Game.refreshLand` and the sale path | `DataSave.zoning` · ZoningCheck, LandCheck, PolicyPreviewCheck · none (the test player sets no zone) | byte-identical: with nothing set only FOREST's E and C move, which nothing priced and no trace column reads (pt0783's eight checked) |
| **Z2 Zoning, the screens** (UI) | 0.7.92 | `ui/PolicyScreen` ZONING (hub, a page a zone, the forest law; dial cards and tray); Land office, Build's LAND FREE, NEEDS YOU's GROUND, the investors' word; a words probe at 1,389 × 868 | none · words · none | byte-identical; Jerus's PC check |
| **I1 Fields by type** | 0.7.93 | `Deposit.FieldType`, `type()`, `TYPE_SHARES`, `TYPE_SALT`; `Resource.typesOf()`, `goodOf()`; the template's `site` (catalog, twins), `Game.siteOf/committedOn(r, type)` (id 14 on ANY until I5); `LandManager` (`getSites/getRemaining/extract(r, type)`, `extractedByType`, `typeRuns`, `fits`); `CityLand.sitesByType` | `DataSave.depletionByType` · WorldCheck 8, LandCheck · none | byte-identical |
| **I2 The shared machinery** | 0.7.94 | SpreadPlanner (A: `ON_ITS_WHOLE_COST` the default; `outValue`, `inValue`); `ProcessingSector`, `ExtractionSector` (Mining onto it, today's test); `Sector.captive`, `Output.captive`; `gen` (the bill, `Statement.powerSold`, `ServicesManager`, `UtilitiesHandler.setWorksPower`); `consumables`; MoneyAudit lines | `Statement.powerSold` · MaterialsCheck 1–5; RefineryCheck 11b · none | byte-identical (O5's ptW: Refining unchanged; no template has gen, consumables or a captive good) |
| **I3 ★ Iron's prices and the split** | 0.7.95 | `Good` (IRON, STEEL, FABRICATED_STEEL, MACHINERY, SCRAP; CARS, VANS, ROLLING_STOCK, C); ids 11 (name), 12, 14 (20,000 t, D$45M, DIESEL); the labels Iron Mining and Iron & Steel; HeavyIndustry onto ProcessingSector; extraction on the whole-cost gate; `IronSplit`; `Resource.IRON` 1/212.5 (D); name lookups by id | SAVE_FORMAT + 1 · MiningCheck, ManufacturingCheck, CarCheck, MaterialsCheck 6 · the test player's foundry rule by id | moves from the first mine's ore (8× and cheaper) and the first car (6% cheaper), then the mills: expect the Electric Arc Furnaces shed by distress (−.12 to −.29% on imported scrap) and steel imported at 650 |
| **I4 ★ The integrated route and old steel** | 0.7.96 | the seven Iron & Steel rows; PELLETS, PIG_IRON, MET_COKE, THERMAL_COAL, COKING_COAL, LIMESTONE, LIME (imported until their sectors come); gen on three; `OldMetal` (steel) and the Scrap Yard; `Motoring` keeps the cars scrapped | `DataSave.oldMetal` · MaterialsCheck 7–8; GdpCheck HELD · none | moves (new candidates): watch pelletising on the city's own ore (.59–1.15%) |
| **I5 ★ Iron Mining by type and size** | 0.7.97 | id 14 on HEMATITE; the three new mines; `fits` gates the plan; `groundLimit(IRON)` split by each type's capacity; retirement by type | none · MaterialsCheck 9 · none | moves from the first mine order |
| **I6 ★ Coal** | 0.7.98 | KEYS + Coal Mining; four rows; `CoalMining.flow()` and `wash`; `Resource.COAL` (good, 1/200) | none · MaterialsCheck 10 · none | only where an offer holds coal (none within 50 km on seed 4127): expect the playtest byte-identical, the ensemble moved |
| **I7 ★ Stone** | 0.7.99 | KEYS + Quarrying, Cement & Concrete; AGGREGATE, DIMENSION_STONE, CEMENT; six rows; the limestone carry; `Resource.STONE` (1/400) | none · MaterialsCheck 11 · none | moves from the first offer holding stone (9 limestone fields within 50 km on 4127) |
| **I8 ★ Copper** | 0.7.100 | KEYS + Copper Mining, Copper Refining; seven goods; seven rows and the Wire & Cable Works (Manufacturing); `OldMetal` copper; `Resource.COPPER` (1/100) | none · MaterialsCheck 12 · none | moves from the first offer holding copper |
| **I9 ★ Forestry** (after Z1) | 0.7.101 | KEYS + Forestry; SAWLOGS, PULPWOOD; Logging Camp, Harvesting Operation, Tree Plantation; E at 120 months on `cuttableForest()`; planting on its zone (B); `Forestry.salvage()` on Z1's clearing; regrowth with plantations; `Resource.FOREST` (good, .02); `LandMarket.bareGround` | none · MaterialsCheck 13, ZoningCheck's Forestry sections, LandCheck · none | moves wherever the city owns 17.5 km² of cuttable forest, and from the first cleared forest (salvage) |
| **I10 ★ Wood Products** | 0.7.102 | KEYS + Wood Products; LUMBER, PANELS, RESIDUES, PULP, PAPER, WOOD_PELLETS; six rows; the pulp mill's gen; FUEL_OIL | none · MaterialsCheck 14 · none | moves once Forestry ships logs |
| **I11 The pictograms' model** (UI) | 0.7.103 | `IndustryView`, `IndustryViewCheck`; Forestry's settling line under the law | none · IndustryViewCheck · none | byte-identical |
| **I12 The pictograms drawn** (UI) | 0.7.104 | `ui/SectorScreen` OPERATIONS for the 9, on O11's drawing; a words probe at 1,389 × 868 | none · words · none | byte-identical; Jerus's PC check |
| **I13 Cards and pages** (UI) | 0.7.105 | `BuildCard`/`BuildScreen` for the 37 (sites, type, earns, gate); each sector's `ownLines`; the land office's hover (field types); the Forestry zone's E line (spec-zoning board 3) | none · BuildCardCheck, words · none | byte-identical |
| **I14 The map, and 0.8.0** (UI) | **0.8.0** | `BuildingVisual`/`TilePainter`: pits by sites, quarries, camps, plantations as forest on their ground, the works as O13's campus; zones tinted by district (Z1's "the map picks where") | none · MapCheck (each thing once; ≤ 80 ms) · none | byte-identical; a pan on the PC; the release |

## 6. Risks, and what to measure first
**Risks:**
- **Exporters on imports** (study 1.2). With A, only an order earning over the hurdle on its whole cost is built: at the measured rates the Pulp Mill in Jerus's city, by a hair. A weaker currency opens more (city600's conditions: at ×1.5 the Blast Furnace +.06, Pulp Mill +.29, Pellet Mill +.28; at ×2 also the Lime Kiln +.14, Large Sawmill +.12, Wire & Cable Works +.13). If the ensemble's count grows, give pulp and pellets a bounded world demand (O5's advice for lubricants) rather than a gate.
- **Ground at city prices.** A coal site is 1 km²: at city2400's going price a Surface Coal Mine's ground is D$501M against the mine's D$130M (1.63% → 0.13% a month). Zoning is now the player's lever: a Coal Mining zone at a low price makes it pay, and shows the price given up.
- **The forest.** E slows the strip; only the law keeps forest (spec-zoning 3.3: regrowth pays most at 0% standing). With the default 0/0, expect big forests stripped in about 17 years once Forestry runs.
- **The playtest** loses its foundries at I3; steel is imported at half today's price, and Manufacturing's and Automotive's margins hold. Watch jobs and the trade balance.
- **Water and roads.** A Pulp Mill draws 11,600 units and a Large Magnetite Complex 7,900; the fresh-water cap (J2) will hold them. A Large Open-Pit Mine's road load is 84,000, 2.5 Crude Units'.
- **Scale.** The new code is O(fields held), O(zones) or O(kinds), and pure.

**Measure first:**
1. Z1: how much forest the playtest's buildings clear by era (the default law's cost).
2. I3's playtest: Heavy Industry's distress, steel imports, Manufacturing's and Automotive's counts.
3. Case A at the ensemble's weakest exchange factor (study 10.1).
4. I4: pelletising on the city's own ore, and the scrap pool against one yard.
5. The share of seeds whose founding region holds coal, copper and stone (I6–I8's ensembles).
6. I9: crews against the cuttable forest, and the salvage cut's share of Forestry's wood.
