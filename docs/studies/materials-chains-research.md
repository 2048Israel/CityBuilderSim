# Iron redone, and coal, copper, wood and stone: design study and mockups

*2026-10-08. A read-only design study for the industries after oil. Jerus asked for "how many buildings, and how the industries will be designed for wood, stone, copper, and redoing the iron industry", with at most three industries per resource. He then added coal, a pictogram for each industry, and that each resource should be one mining sector plus one processing sector that "decides which buildings to build to make the most profit". He also said construction gets its own upgrade afterwards, since buildings will then be made of these materials.*

*No game code was changed. The 0.7.69 tree on Jerus's PC was read on the morning of 2026-10-08, while another agent was working on it. Files read: `buildings.json`, `Resource.java`, `Deposit.java`, `Good.java`, `World.java`, `PayTier.java`, `LabourMarket.java`, `UtilitiesHandler.java`, `MiningCheck.java`, and in `sectors/`: Mining, HeavyIndustry, Materials, Oil, Refining, Manufacturing and Construction.*

*Revised the same day with Jerus's answers to all 22 questions (§10). The answers that changed the design: steel moves to the world's price (Q6); the processing sectors build whatever earns most, with no city's-own-reason gate, held instead by freight in the price bands (Q7); the steelworks and the pulp mill make their own power now (Q20); Forestry has no cutting limit and owns the plantations, which it plants as a cost of cutting (Q14, Q15); and a paper mill joins Wood Products (Q16). The model, the mockups and every figure below were reworked to match.*

*Amended later the same day by the build plan's questions A–E (§10.3). Every order is tested on its whole cost, whoever pays (A). Plantations go only on ground the city has zoned for Forestry, at the zone's price, so zoning comes first (B, `spec-zoning.md`). Car, van and train prices move with the steel in them (C). Iron in the ground stays at US$ 0.40 a tonne (D). Forestry adds a crew only while the forest it may cut lasts 120 months at that cut (E; the plan said 36, Jerus set 120). Wood on forest ground a building takes is sold by Forestry as a salvage cut (`spec-zoning.md` Z7). The mockups were not redrawn for A–E.*

*Sources are in §12 and cited by chain: [I] iron and steel, [C] coal, [K] copper, [W] wood, [S] stone, each keeping its research pass's own numbering. "(est.)" marks a figure estimated or derived here. Research money is US$. Proposals and mockups use D$ at parity, as the oil study did.*

---

## 0. In one page

### The shape
Each resource is one sector that digs or cuts and one sector that processes. Iron and copper also have Manufacturing as a third. That keeps every chain within Jerus's limit of three industries, and he kept it that way (Q1).

| Resource | 1 · digs or cuts | 2 · processes (whatever earns most) | 3 · existing |
|---|---|---|---|
| Iron | **Iron Mining** (today's Mining, renamed) | **Iron & Steel** (today's Heavy Industry, renamed) | Manufacturing |
| Coal | **Coal Mining** | none: coal becomes coke, heat and power in the sectors that burn it | — |
| Copper | **Copper Mining** | **Copper Refining** | Manufacturing (Wire & Cable Works) |
| Wood | **Forestry** | **Wood Products** | — |
| Stone | **Quarrying** | **Cement & Concrete** | — |

- **Counts:**
  - 7 new sectors (17 → 24), and 2 renamed with their saved keys unchanged;
  - 39 new buildings, and 3 existing ones changed (Iron Mine id 14, Steel Foundry id 11 renamed Electric Arc Furnace, Steel Mini-Mill id 12);
  - 29 new goods, and `IRON` becomes iron ore only.
- **One rule decides what gets built** (§1.2): whatever earns the most on its cost at the city's own prices, if it passes the existing interest test. There is no other gate (Q7). What stops a city importing ore to export steel forever is freight: every price band was checked so that a building which imports all its feed and exports all its output earns at most 0.4% a month, under the investors' hurdle.
- **Each field has a kind, drawn by the world's real shares** (§1.4). The kind is a mechanism, not a modifier:
  - hematite or magnetite iron;
  - thermal or coking coal;
  - sulphide or oxide copper;
  - limestone or hard-rock stone.

  The kind decides which mine stands on the field and what it makes.

### Iron, redone
- **What is wrong today.** `IRON` stands for both ore and scrap. A mill turns 1.1 t of it into 1 t of steel. The Iron Mine lifts 500 t a worker a year; real mines lift 4,800–72,000. Ore exports at D$ 140, against a real US$ 85–90.
- **What changes.**
  - Ore goes to its real price (D$ 85 export, D$ 115 import), and the Iron Mine lifts 8× more with the same 60 posts.
  - A blast-furnace route arrives: coke ovens, blast furnace, oxygen steel shop. They burn their own gases for power (§1.6, Q20).
  - Scrap becomes its own good, which the city makes from old cars and demolitions.
  - **Steel moves to the world's price:** D$ 650 landed and D$ 470 exported, from D$ 1,284 / 847 (Q6). Fabricated steel and machinery move with it, so Manufacturing's returns do not change (§2.6). So do cars, vans and trains (C, §10.3).

### Two places the market does the work
- **Freight** keeps stone and cement local, makes a smelter next to the city's own copper mine worth more than one abroad, and now does the job the city's-own-reason gate would have done. Nothing is scripted.
- **Construction** will draw these products in the next run. §9 lists what it will need per m² and per km of road.

### What comes with this report
- **Seven mockups:**
  - an overview of every building;
  - a pictogram page for each chain (two windows each: the extraction sector and the processing sector);
  - six build cards.
- **Twenty-two decisions** (§10), each with what it changed. The four that change the most:
  - **Q6:** steel at the world's price;
  - **Q7:** whatever earns most, held by freight in the bands;
  - **Q14–Q15:** no limit on the cut, and plantations that Forestry plants as a cost of cutting; amended by **E** (crews held by the forest they may cut) and **B** (plantations only on ground zoned for Forestry), §10.3;
  - **Q20:** the works' own power, now rather than in the electricity run.
- **For the implementer** (§10.1): the order of work, and what each harness needs. **Still open** (§10.2): four items, none blocking. **The build plan's A–E** (§10.3).

---

## 1. The shape, for all five

### 1.1 Two sectors a resource, at most three industries
**Industry 1** digs or cuts. It stands on ground the city owns: a field's sites, or forest. Its product must sell on the world market, so a mine is worth building before anything downstream exists. That is the rule `steel-and-heavy-industry.md` set for iron ore; it is kept and applied to all five. It is also why copper's mine includes its concentrator: ore at 0.5% copper has no market [K12].

**Industry 2** processes. It owns every building that turns the resource into something else, and builds whichever earns the most on its cost (§1.2). This is Jerus's "only one sector and basically that sector just decides which buildings to build to make the most profit, aka following the demand".

**Industry 3** exists only where a downstream sector already exists: Manufacturing, for iron (fabrication, machinery) and copper (one new building, the Wire & Cable Works).

A sector that buys from another chain is not counted in that chain. Iron & Steel buys coal, but it is iron's industry, not coal's.

### 1.2 The processing rule (Q7)
This is the oil study's §2.3, with nothing added. Jerus: "whatever earns most, i think that currency exchange rate handles the pure profit forever". He chose pure profit, with real freight in the price bands.

1. **Price every good at the city's own price this month:**
   - the import price if the city imported any;
   - the export price if it exported any;
   - the cleared local price otherwise.

   A new building's own output is valued at the import price for as much as the city imports, and at the export price beyond. A third coke oven in mockup 1 would replace 11,750 t of imported coke at D$ 420 and sell its other 28,250 t abroad at D$ 320.
2. **What it earns** = what it makes − what it uses, at those prices, − its running cost (wages, power, water, fuel, consumables), + any power it makes beyond its own draw, sold to the grid at the tariff (§1.6).
3. **Gates, as today:**
   1. **Land.**
   2. **Staff:** the existing `Staffing`.
   3. **Money:** at least 1.25× its interest, the existing `PROFIT_OVER_INTEREST`. That is about 0.6% of the cost a month on a 5.7% loan, and 0.74% on the 7.1% the investors were quoted in 0.7.7's runs (`the-bank-prices-like-a-business.md`). **Every order is tested on its whole cost, whoever pays** (A, §10.3): an order paid from the sector's own cash is asked the same as one on a loan.
4. **Build** the one that earns the most on its cost, one order at a time per sector, as today.
5. **Idle, then shed,** by the existing distress rule, when it earns below zero for 6 months.

**What holds it, without a gate.** Two things, and Jerus named the first.
- **The currency.** A city that exports earns foreign money, its currency rises, and its exports earn less in D$. That pushes back, but the game has measured it not pushing back hard enough on its own: 179 Fabrication Works on a fixed export floor (`Good.java`'s header), and 120 oil refineries before 0.7.62's gate.
- **Freight, in the price bands.** The gap between what the world pays and what it asks is mostly freight (`Good.java`: three quarters of the half-wedge). A building that imports its feed pays freight in; one that exports its output pays it out. So every band was checked: in case A (all feed imported, all output exported) no processing building earns more than 0.4% a month, under the hurdle. With steel at the world's price the steel shops needed nothing more (§2.5). Eight bands, for six buildings, were widened toward their real freight:

| Good | Was, D$ | Now, D$ | Building it holds | Why (est.) |
|---|---|---|---|---|
| PULPWOOD, import | 70 | 90 | Panel Mill, Pulp Mill | roundwood is heavy and wet, and ships badly |
| RESIDUES, import | 35 | 45 | Pulp Mill, Wood Pellet Mill | loose chips take about 2.5× the room of solid wood |
| PANELS, export | 300 | 260 | Panel Mill | OSB is cheap for its bulk |
| PULP, export | 640 | 600 | Pulp Mill | netback after ocean freight to Asia |
| WOOD_PELLETS, export | 160 | 150 | Wood Pellet Mill | |
| LIME, export | 150 | 110 | Lime Kiln | quicklime slakes in transit and rarely travels far |
| COPPER_SCRAP, import | 8,200 | 8,700 | Scrap Copper Refinery | sorted scrap landed, about 87% of cathode |
| WIRE_CABLE, export | 14,500 | 13,000 | Wire & Cable Works | cable drums ship by volume, not weight |

**Case A now** (all in §2.5, §4.4, §5.5, §6.5): pulp mill and pellet mill 0.4%, blast furnace and lime kiln 0.2%, pelletising and the large sawmill 0.1%, panels 0.0%; every other processing building below zero.

**One thing to watch.** World prices are in world money and wages are in D$. When the D$ is weak, every traded margin grows against the wage bill, and so does case A. The ensemble should check case A at the weakest rate the currency reaches (§10.1).

### 1.3 The extraction rule
This is today's `Mining.plan()`, with one addition: the sector chooses the mine type that fits.

- A mine stands on free sites of a field the city owns.
- The field's kind picks the family:
  - a coking field takes an underground or surface mine whose coal goes to a preparation plant;
  - an oxide copper field takes a leach plant.
- The free sites pick the size. A Large Open-Pit Mine needs 20 sites in one field; West 9's 4 sites get Iron Mines (mockup 1).
- Among the mines that fit, the best return wins, as now.

**Forest has no sites, and no fixed limit on the cut (Q14).** Jerus: "the map will include forest". Forestry builds crews while cutting pays, like any mine, **but only while the forest it may cut would last 120 months at the cut with the new crew** (E, §10.3). Forest the city's forest law protects is not counted (`spec-zoning.md` §2). The game's regrowth rule sets where the forest settles at that cut, and the Forestry page shows it (§5.2, Q13). What answers an overcut is planting: Forestry plants a Tree Plantation whenever its cut exceeds regrowth, **on ground the city has zoned for Forestry** (§5.7, Q15, B). Mockup 6 shows a seventh crew on site, and what it costs the forest; it was drawn before E.

### 1.4 Field kinds, by real shares

| Resource | Kind A | Kind B | Basis |
|---|---|---|---|
| Iron | hematite, 58–64% Fe, shipped as dug: **75%** | magnetite, ~30% Fe, concentrated about 3:1: **25%** | world output by country type [I1]; Rana Gruber 3.05 t of crude ore a tonne [I16] |
| Coal | thermal (sub-bituminous, 20.1 GJ/t), sold unwashed: **85%** | coking (bituminous, 29.0 GJ/t), washed: **15%** | metallurgical is 13% of world demand [C13]; heat content [C11] |
| Copper | sulphide, floated to concentrate: **80%** | oxide, leached to cathode at the mine: **20%** | SX-EW was 21% of mine output in 2024 [K2] |
| Stone | limestone (kiln feed, and aggregate): **70%** | hard rock, granite and the like (aggregate, dimension stone): **30%** | US crushed stone by rock type [S1] |
| Forest | natural | plantation (planted by Forestry on its zone, §5.7) | — |

Each field draws its kind once, from its own hash, like everything else about a field. `Resource.java`'s order and densities are unchanged; only `World` needs a kind per field (Q3: yes).

### 1.5 Sizes and costs
- **Two sizes where real plants are big** (Q19: yes). The small size is the smallest real operation that exists; the large one is a typical modern plant. Without the large size a big city needs hundreds of small ones: the 5,000-daycares problem, and the oil study's Q1.
- **Costs follow the game's own convention,** not raw real capital. Today's buildings cost 1–2.5 years of what they sell abroad:
  - Fabrication Shop: 1.0×;
  - Oil Refinery: 1.1×;
  - Iron Mine: 2.3×;
  - Steel Foundry: 2.5×.

  New buildings sit in that band. They are usually 1.2–2× the real capital, which `manufacturing.md` calls deliberate and load-bearing.
- **Wages and utilities are the game's own:**
  - wages in thousands a month: no diploma 3.46, diploma 4.50, college 5.50, science 8.20 (`PayTier`);
  - power D$ 10 a kW a month; water D$ 50 a unit (`UtilitiesHandler`);
  - diesel D$ 0.60 a litre, the oil mockups' price here.

### 1.6 Power the plants make themselves (Q20)
Jerus chose to do this now, rather than in the electricity run.
- **Who makes power.** Real integrated steelworks make most of their own electricity from blast-furnace, coke-oven and converter gas [I24][I36]. A kraft pulp mill burns its black liquor and bark, and sells about 0.9 MWh a tonne of pulp [W42].
- **The rule.** A building has a `gen` figure in kW. It buys from the grid only what it draws beyond that. What it makes beyond its own draw it sells to the grid at the tariff (D$ 10 a kW a month), and that counts in what it earns (§1.2).
- **The figures (est.):**

| Building | Draws, kW | Makes, kW | So it |
|---|---|---|---|
| Coke Ovens | 2,200 | 8,000 | sells 5,800 |
| Blast Furnace (with its top-gas turbine) | 51,400 | 60,000 | sells 8,600 |
| Oxygen Steel Shop | 51,600 | 25,000 | buys 26,600 |
| Pulp Mill | its own needs, met by its boiler | 13,500 | sells 13,500 |

- **Where the figures come from.** A battery burns about half its gas heating its own ovens (est.); the 7.65 GJ of gas a tonne of coke [I36] leaves enough for about 20 MW a battery, and 8 MW is a cautious share. The pulp mill's is 11,000 t × 0.9 MWh [W42].
- **In mockup 1** the works makes 101 MW of the 131 MW it draws.
- **What waits for the electricity run** (Q10): coal power plants that buy coal, and the tariff itself. At D$ 0.014 a kWh the works sells its power cheap; when the tariff moves, these figures move with it.

---

## 2. Iron, redone

### 2.1 What is wrong today (read in the code)

| Today | Real | Where |
|---|---|---|
| `IRON` is "Iron ore, and the scrap that stands in for it": import D$ 0.41k (scrap), export D$ 0.14k (ore) | ore 62% Fe CFR China averaged US$ 99 in Jan–Sep 2025 and US$ 91 on 7 Oct 2026 [I1][I53]; scrap CFR Turkey about US$ 394 [I58] | `Good.IRON` |
| a mill turns 1.1 t of `IRON` into 1 t of steel | a blast furnace takes about 1.55–1.65 t of ore a tonne of iron (est., from Fe content); an arc furnace about 1.10 t of scrap a tonne of steel [I27][I52] | Steel Foundry, Steel Mini-Mill |
| Iron Mine: 2,500 t a month, 60 posts, 500 t a post a year | 4,800 (Rana Gruber, small) to 72,000 (Gudai-Darri) t an employee a year [I10][I16] | id 14 |
| a site holds 15 Mt; at 2,500 t a month one mine takes 500 years | real mines last 15–40 years | `Resource.IRON` |

### 2.2 Real figures that size the buildings

| Item | Figure | Source |
|---|---|---|
| Mines: output per employee a year | Gudai-Darri ~72,000 t; Onslow ~29,000; Hibbing Taconite ~10,700; US taconite ~8,800; Rana Gruber ~4,800 | [I1][I10][I11][I15][I16] |
| Mines: capital per tonne a year | Onslow US$ 65; Gudai-Darri 72; Simandou (mine only) 85; Iron Bridge (magnetite) 150–160 | [I5][I8][I10][I11] |
| Mines: cash cost | Pilbara US$ 18.7–23.5 a tonne FOB; magnetite (Champion, Iron Bridge target) 60–70 | [I4][I6][I17] |
| Diesel to dig and haul | 2–3 L a tonne of raw ore | [I21] |
| Pellets | premium over fines about US$ 42 (IOC, Vale); plants of 1.8–12.5 Mt a year; 0.6–0.8 GJ a tonne | [I4][I18][I24][I25] |
| Coke ovens | 1.27 t of coking coal a tonne of coke (reference plant), 1.41 (US plants, 2024); coke-oven gas 7.65 GJ a tonne | [I36][C1] |
| Blast furnace | 1.2–5.65 Mt a year a furnace; PCI 125–230 kg a tonne of iron; flux about 300 kg a tonne of steel on the route | [I29][I31][I24][I39][I52] |
| Basic oxygen furnace | 0.84–1.00 t of hot metal a tonne of steel, the rest scrap; oxygen 55–60 Nm³; lime about 75 kg | [I38][I41][S38] |
| Electric arc furnace | 409 + 65 kWh a tonne on scrap; Steel Dynamics Sinton US$ 1.9bn for 3 Mt, ~600 jobs; Nucor West Virginia US$ 3.1bn for 3 Mt | [I24][I45][I46][I47] |
| Integrated works | 370–3,600 t an employee a year (US average ~940); greenfield ~US$ 655 a tonne a year; ~51 ha a Mt | [I2][I29][I30][I31][I35] |
| Prices, 2025–26 | pellets IOC US$ 125.7; pig iron Brazil FOB 408–440; HRC world export 510, China 424, W. Europe 842, US 1,293 (28 Sep 2026) | [I4][I57][I59] |

**The game's own electricity figures are already right.** The Steel Mini-Mill's 3,700 kW for 6,000 t a month is 450 kWh a tonne; the Foundry's 750 kW for 1,200 t is 456. Both stand, unchanged.

### 2.3 Goods

| Good | Unit | Import / export, D$ | Notes |
|---|---|---|---|
| `IRON`, renamed **Iron ore** | t | 115 / 85 | fines, lump and concentrate at 62–66% Fe; flow, as now |
| **SCRAP** | t | 410 / 300 | the old `IRON` import price moves here; flow |
| **PELLETS** | t | 160 / 125 | export only, until a DRI plant exists (§2.8) |
| **PIG_IRON** | t | 460 / 400 | merchant pig iron; flow |
| **COKE** | t | 420 / 320 | stockable |
| `STEEL` | t | **650 / 470** (was 1,284 / 847) | the world's price (Q6): hot-rolled coil at about US$ 510 FOB on the world market [I57]; §2.6 |
| `FABRICATED_STEEL` | t | export **1,545** (was 2,210) | moved with steel (Q6b) |
| `MACHINERY` | t | **13,400 / 8,100** (was 14,300 / 9,000) | moved with steel (Q6b) |

The city's **old steel** is not a good. It is a pool that arises each month and a Scrap Yard buys (§2.7).

### 2.4 Buildings
Figures are a month at nameplate. Posts are no diploma / diploma / college / university. Costs are all-in game costs. Runs count only the power bought from the grid (§1.6).

**Iron Mining (today's Mining).**

| Building | Makes | Posts | Power kW | Water | Ground | Cost | Runs, D$/mo |
|---|---|---|---|---|---|---|---|
| **Iron Mine** (id 14, kept) | 20,000 t ore (was 2,500) | 60 (45/12/3/0), unchanged | 600 (was 2,000) | 20 | 1 site | D$ 45M (was 9.7M) | 469k, with 60,000 L diesel |
| Large Open-Pit Mine | 500,000 t ore | 400 (240/110/40/10) | 3,000 | 300 | 20 sites, 74 ha | D$ 1.00B | 5.42M |
| Magnetite Mine & Concentrator | 20,000 t concentrate, from 60,000 t of rock | 120 (60/40/15/5) | 3,000 | 400 | 1 site | D$ 60M | 951k |
| Large Magnetite Complex | 400,000 t concentrate, from 1.2M t | 500 (280/150/55/15) | 52,000 | 7,900 | 20 sites | D$ 1.10B | 9.58M |

**Iron & Steel (today's Heavy Industry).**

| Building | Makes | Uses | Posts | Power kW | Ground | Cost | Runs |
|---|---|---|---|---|---|---|---|
| Scrap Yard | 10,000 t scrap | 10,500 t old steel | 40 | 550 | 1.9 ha | D$ 35M | 255k |
| Pelletising Plant | 150,000 t pellets | 150,000 t ore, 4,500 t thermal coal | 150 | 5,750 | 15 ha | D$ 270M | 1.00M |
| Coke Ovens | 40,000 t coke | 54,000 t coking coal | 200 | 2,200; makes 8,000 | 9 ha | D$ 264M | 1.25M |
| Mini Blast Furnace | 15,000 t pig iron | 24,000 t ore, 6,750 t coke, 3,750 t limestone | 150 | 2,050 | 3.7 ha | D$ 54M | 868k |
| Blast Furnace (with its sinter plant) | 250,000 t pig iron | 387,500 t ore, 85,000 t coke, 37,500 t thermal coal, 62,500 t limestone | 1,200 | 51,400; makes 60,000 | 60 ha | D$ 1.35B | 11.7M |
| Oxygen Steel Shop (with caster and hot strip mill) | 290,000 t steel | 246,500 t pig iron, 72,500 t scrap, 17,400 t lime | 1,800 | 51,600; makes 25,000 | 84 ha | D$ 1.80B (was 4.00B, §2.6) | 25.6M |
| **Electric Arc Furnace** (id 11, was Steel Foundry) | 1,200 t steel | 1,320 t **scrap** | 38, unchanged | 750 | as now | as now | 187k |
| **Steel Mini-Mill** (id 12) | 6,000 t steel | 6,600 t **scrap** | 130, unchanged | 3,700 | as now | as now | 708k |
| Large Mini-Mill | 125,000 t steel | 137,500 t scrap | 400 | 77,000 | 46 ha | D$ 1.20B (was 2.40B, §2.6) | 5.10M |

**Consumables** go into running costs as an `upkeep` line:
- refractories, rolls and alloys: D$ 25–60 a tonne;
- electrodes for the arc furnaces: D$ 20 a tonne (est.);
- explosives and wear parts at the mines: D$ 6–15 a tonne (est.).

### 2.5 Balance
Return is the monthly margin ÷ cost, in four cases:
- **A:** feed imported, output exported: what freight has to hold under the hurdle (§1.2);
- **B:** feed local (valued at its export price), output exported;
- **C:** feed local, output replacing imports;
- **D:** both at import prices.

Power sold to the grid is counted (§1.6). The last column is the cost over a year of what it would sell abroad.

| Building | A | B | C | D | Cost ÷ a year abroad |
|---|---|---|---|---|---|
| Iron Mine | 2.7% | 2.7% | 4.1% | 4.1% | 2.2 |
| Large Open-Pit Mine | 3.7% | 3.7% | 5.2% | 5.2% | 2.0 |
| Magnetite Mine & Concentrator | 1.2% | 1.2% | 2.2% | 2.2% | 2.9 |
| Large Magnetite Complex | 2.2% | 2.2% | 3.3% | 3.3% | 2.7 |
| Scrap Yard | 1.5% | 1.5% | 4.7% | 4.7% | 1.0 |
| Pelletising Plant | 0.1% | 1.8% | 3.7% | 2.0% | 1.2 |
| Coke Ovens | −0.2% | 0.7% | 2.2% | 1.3% | 1.7 |
| Mini Blast Furnace | −1.1% | 1.7% | 3.3% | 0.5% | 0.8 |
| Blast Furnace | 0.2% | 1.9% | 3.0% | 1.3% | 1.1 |
| Oxygen Steel Shop | −2.1% | −0.6% | 2.3% | 0.8% | 1.1 |
| Electric Arc Furnace | −0.5% | −0.1% | 0.7% | 0.2% | 4.4 |
| Steel Mini-Mill | −0.5% | 0.1% | 1.0% | 0.4% | 3.5 |
| Large Mini-Mill | −0.2% | 1.0% | 2.9% | 1.6% | 1.7 |

**What it says:**
- **A mine is always worth building.**
  - The Iron Mine costs D$ 23 a tonne to run (469k ÷ 20,000), against D$ 85 abroad.
  - The large mines run at D$ 11–24 and the small magnetite mine at D$ 48, in the real range [I4][I6][I17].
  - Iron ore is the richest business in the chain, as it really is. The field's price, US$ 180M for the founding field, is the gate; it stays there because iron in the ground stays at US$ 0.40 a tonne (D, §10.3).
- **Ironmaking pays on the city's own feed and not on imported feed** (the blast furnace 0.2% in case A, the mini furnace −1.1%).
- **Steelmaking now pays at home, not abroad.** With steel at D$ 470 abroad, the Oxygen Steel Shop loses 0.6% a month exporting and earns 2.3% replacing imports; the Large Mini-Mill 1.0% and 2.9%. So the rule builds steel shops where the city's fabricators buy steel, which is what Manufacturing was built for. Mockup 1's city uses all its steel and imports 18,900 t more.
- **The existing arc furnaces become thin.** Replacing imported steel, the Electric Arc Furnace (id 11) earns 0.7% a month on the city's scrap and 0.2% on imported scrap; the Mini-Mill (id 12) 1.0% and 0.4%. Their costs were set against the old steel price: 4.4 and 3.5 years of what they would now sell abroad. They are left as they are (§10.2).
- **MiningCheck's bands move.** Today it expects the Foundry's margin at 20–36% of its sales on imported scrap, and 48–62% on local ore. At the new steel price, selling at home, the Foundry makes 7% and the Mini-Mill 13% on imported scrap, and 25% and 31% on the city's own. So the bands become about 5–15% on imported scrap and 20–35% on local scrap, and "local ore" becomes "local scrap" (§10.1).
- **The rule's order in mockup 1.** A second Pelletising Plant (1.8%) ranks above a third Coke Oven (1.2%, replacing 11,750 t of imported coke and exporting the rest) and a second Blast Furnace (1.1%, because its coke would be imported). An arc furnace for the 18,900 t of steel the city imports earns 0.2% on imported scrap.

### 2.6 Steel at the world's price (Q6)
- **The move.** `STEEL` goes from D$ 1,284 / 847 to D$ 650 / 470. The old band was the US price, which sits behind tariffs (US$ 1,293 a tonne in September 2026); the world trades hot-rolled coil at about US$ 510 FOB, China at 424 [I57].
- **What moves with it** (Q6b, "move them with steel"). Manufacturing was calibrated on steel at D$ 1,284, so its two steel goods move by the steel in them:
  - `FABRICATED_STEEL` exports at D$ 1,545 (was 2,210). A Fabrication Shop or Works still adds D$ 862 a tonne over the 1.05 t of steel it uses.
  - `MACHINERY` at D$ 13,400 / 8,100 (was 14,300 / 9,000): 1.45 t of steel a tonne, each D$ 634 cheaper.
  - Each building's margin in D$ is unchanged, so its return on cost is unchanged. Manufacturing's harness premises keep their logic; the prices they quote need re-reading.
  - **Cars, vans and trains move the same way** (C, §10.3): Automotive's import and export prices fall by the steel in each, at D$ 634 a tonne of steel, so the car plants' inputs getting cheaper does not hand them a margin they did not have.
- **Costs rescaled.** The Oxygen Steel Shop (D$ 4.0B → 1.8B) and the Large Mini-Mill (2.4B → 1.2B) keep about 1.1–1.7 years of what they would sell abroad, the game's convention (§1.5). For scale, Steel Dynamics' Sinton mill cost US$ 1.9bn for 3 Mt a year, about US$ 630 a tonne a year [I46]; the Large Mini-Mill is now about US$ 800.
- **Why the gate could go** (Q7). At the old D$ 847 export price, an integrated works on imported ore and coke cleared 1.8% a month exporting, and only a gate could stop it. At D$ 470 it loses money doing that.

### 2.7 Scrap that the city makes
Old steel arises each month from four sources (est.):
- **Scrapped cars and vans:** `Motoring`'s fleet. About 0.9 t of steel a car [I63], and a fleet scrapping about 4.6% a year [I64].
- **Demolished buildings:** about 50–110 kg of steel a m² of steel-framed floor, and 25–50 kg of rebar a m² of concrete housing [I65]. The demolition log already exists.
- **Fabrication offcuts:** today's Fabrication Shop already turns 1,260 t into 1,200, so 60 t of prompt scrap a month is waiting to be counted.
- **Rolling stock** retired by the railway.

How it is handled:
- **Real recovery** is 85% for construction and cars [I52].
- **A Scrap Yard** pays the sellers 70% of scrap's export price, D$ 210 a tonne (Q8: "Pay sellers 70%"): households for their cars, and the owner for a demolition.
- **Without a yard,** old steel goes to landfill.

### 2.8 Left out, on purpose
- **DRI.** Direct reduction needs 10 GJ of natural gas a tonne [I49], and the world has no gas. It can come back with gas, or with hydrogen from electricity.
- **Sinter.** The sinter plant is folded into the Blast Furnace, as are the caster and hot strip mill into the Oxygen Steel Shop. Each is its own unit in reality, but none is ever built alone.
- **Two steels.** Flat steel and long steel (rebar, sections) should split when construction starts buying rebar. Until then one `STEEL` is enough.

---

## 3. Coal

### 3.1 Real figures

| Item | Figure | Source |
|---|---|---|
| Productivity, US 2024 | underground 3.24 short tons an employee-hour, surface 8.94 (open pit 15.2); about 7,460 and 18,760 st an employee-year, Wyoming 43,600 | [C1] |
| Mine sizes | US average 1.16 Mst underground, 0.90 surface; the largest surface mine 59.8 Mst, the largest longwall 10.8 Mst | [C1] |
| Capital | Blue Creek (longwall, metallurgical) about US$ 238 a tonne a year; Winchester South and Olive Downs (open-cut) about US$ 45–60 (est.) | [C22][C27][C30] |
| Recovery of the seam | surface up to 95%, longwall about 80%, room-and-pillar 50–60% | [C35] |
| Washing | coking yield about 75% (Olive Downs); Winchester South about 58%, sold as about 75% coking and 25% thermal by value; Powder River coal is sold unwashed | [C29][C31][C25] |
| Prices | Newcastle 6,000 kcal US$ 104 (2025), about 136–150 (2026); Indonesia 4,200 kcal 45–66; premium hard coking coal 187–245; Powder River at the mine US$ 15.23 a short ton | [C1][C9][C16][C17] |
| Freight | Australia → Japan US$ 12.7 a tonne (2025) | [C16] |
| Users | coal fleet 10,777 Btu/kWh, so 0.39 t a MWh of bituminous, 0.57 of sub-bituminous (est.); capacity factor 42.6%; coke 1.41 t of coal a tonne; cement kiln 3.6 GJ a tonne of clinker; lime 3.2–9.2 GJ a tonne | [C5][C6][C1][C25][C26] |

**The game's coal site is right.** At 1 km², 5 m and 1.3 t a m³ it holds 6.5 Mt, and takes about 15 ha a Mt. Winchester South disturbs 18–20.

### 3.2 Goods

| Good | Unit | Import / export, D$ | Notes |
|---|---|---|---|
| **THERMAL_COAL** | t | 75 / 50 | 20 GJ a tonne; stockable |
| **COKING_COAL** | t | 225 / 180 | stockable |
| **ROM_COAL** | t | not traded | a coking field's coal before washing. Without a preparation plant it sells as thermal coal, tonne for tonne |

### 3.3 Buildings: Coal Mining

| Building | Makes | Posts | Power kW | Ground | Cost | Runs |
|---|---|---|---|---|---|---|
| Surface Coal Mine | 100,000 t | 80 (50/22/6/2) | 800 | 1 site | D$ 120M | 1.07M, with 230,000 L diesel |
| Large Surface Mine (dragline) | 800,000 t | 350 | 6,000 | 4 sites | D$ 950M | 5.57M |
| Underground Coal Mine (longwall) | 250,000 t raw, 80% of the seam | 400 | 29,000 | 1 site, coking field | D$ 800M | 5.77M |
| Coal Preparation Plant | 275,000 t coking + 100,000 t thermal, from 500,000 t raw; 125,000 t to spoil | 50 | 4,000 | 19 ha | D$ 150M | 774k |

### 3.4 Balance
- **Surface mines** return 3.3–3.6% a month exporting. They cost D$ 7–11 a tonne against D$ 50.
- **An underground mine alone** returns 0.8%, because its raw coal sells as thermal coal. With the preparation plant the pair returns about 2.4%. The plant on its own shows 19%, because washing is where coking coal is made. It is a separate building, as Jerus chose (Q9), and the sector builds one beside every coking mine.

### 3.5 Coal and power (Q10)
- **The obvious buyer is the power plant.** The game's Coal Power Plant (280 MW, 68 posts) burns nothing today.
- **At sub-bituminous rates, its coal costs D$ 0.029 a kWh.** At D$ 50 a tonne and 0.57 t a MWh.
- **The tariff is about D$ 0.014.** D$ 10 a kW a month is about D$ 0.014 a kWh.
- **So a coal plant that bought coal would lose money on fuel alone,** before wages.

**Decided: the electricity run** (Q10, "wait for the electricity run"). The mockups draw the power plants as a "later" buyer of 419,000 t a month in the big city. The plants that make power from their own gas and liquor are this run's (§1.6, Q20).

---

## 4. Copper

### 4.1 Real figures

| Item | Figure | Source |
|---|---|---|
| Grades | porphyry 0.40–1.00% Cu; Chile's average falling below 0.5% | [K12][K13] |
| Mid-size mine (Copper World, Arizona, PFS) | 60,000 t of ore a day; 85,300 t of copper a year; US$ 1.32bn; about 400 jobs; 79% recovery; concentrate 23.2% Cu; freight US$ 173 a wet tonne; 96.5% payable | [K5][K6] |
| Large mine (Quellaveco) | 127,500 t a day; about 300 kt a year; US$ 5.0–5.5bn; about 2,500 jobs | [K3][K4] |
| Capital per tonne a year of copper | US$ 11.7k (Marimaca) to about 30k (QB2) | [K10][K5][K3] |
| Concentrator | about 23 kWh a tonne of ore; water 0.53–0.69 m³ a tonne | [K13][K15] |
| Leaching (SX-EW) | 21% of world mine output; Marimaca 50 kt a year for US$ 587M, 72% recovery; 2–5 t of acid a tonne of copper | [K2][K10][K11][K27] |
| Smelters | Kamoa-Kakula US$ 1.1bn for 500 kt (about US$ 2,200 a tonne a year); Manyar US$ 3.17bn for 600 kt (5,300); about 0.9 t of acid a tonne of concentrate | [K16][K17][K18][K27] |
| Refining | electrorefining about 400 kWh a tonne | [K21] |
| Treatment and refining charges | 2024 benchmark US$ 80 a tonne and 8.0¢ a lb; 2025 US$ 21.25; 2026 zero; spot −US$ 148 (July 2026) | [K23][K24][K29] |
| Scrap | 17% of refined copper, about a third of all copper used; No.2 scrap at 71–78% of COMEX; Aurubis Richmond 180 kt of input, US$ 800M, about 220 jobs | [K2][K32][K34] |
| Rod | Contirod lines 320 kt a year; wire rod is 59–62% of world semis capacity | [K2][K35] |
| Prices | LME US$ 9,143 (2024), about 9,940 (2025), 14,483 (Sep 2026, a record) | [K1][K39][K40] |
| Copper in use | a house 199 kg (about 1.0 kg a m²); a petrol car 23 kg, a battery car 83 kg; wind 2.9–8 t a MW | [K44][K45][K46] |

### 4.2 Goods
All are flow goods except cathode, which is stockable. Prices are on an LME basis of D$ 10,000, the 2024–25 range, at the 2024 treatment charges (Q11).

| Good | Unit | Import / export, D$ |
|---|---|---|
| **COPPER_CONC** (30% Cu, 96.5% payable, at the 2024 charges) | t | 2,800 / 2,575; freight about D$ 190 a tonne each way, D$ 630 a tonne of copper |
| **ANODE** | t | 10,150 / 9,650 |
| **COPPER** (cathode) | t | 10,500 / 10,000 |
| **COPPER_SCRAP** | t | **8,700** (was 8,200) / 7,300; widened in §1.2 |
| **COPPER_ROD** | t | 10,750 / 10,250 |
| **ACID** (sulphuric acid) | t | 160 / 70; traded (Q12) |
| **WIRE_CABLE** (a tonne of copper contained) | t | 17,000 / **13,000** (was 14,500), wire worth 1.3–1.7× its copper (est.); widened in §1.2 |

### 4.3 Buildings

| Building | Sector | Makes | Uses | Posts | Power kW | Cost |
|---|---|---|---|---|---|---|
| Copper Mine & Mill | Copper Mining | 2,200 t concentrate (660 t Cu) | 150,000 t ore, 0.5% | 120 | 5,500 | D$ 160M |
| Large Copper Mine | Copper Mining | 26,400 t concentrate (7,920 t Cu) | 1.8M t ore; 8 sites | 450 | 60,000 | D$ 2.00B |
| Leach & SX-EW Plant | Copper Mining | 1,000 t cathode | 280,000 t ore, 3,500 t acid; oxide field | 150 | 6,000 | D$ 250M |
| Copper Smelter | Copper Refining | 9,750 t anode, 29,700 t acid | 33,000 t concentrate | 700 | 13,400 | D$ 500M |
| Electrolytic Refinery | Copper Refining | 9,700 t cathode | 9,750 t anode | 200 | 6,000 | D$ 180M |
| Scrap Copper Refinery | Copper Refining | 4,000 t cathode | 4,400 t scrap | 200 | 5,500 | D$ 280M |
| Rod Mill | Copper Refining | 8,000 t rod | 8,016 t cathode | 80 | 1,100 | D$ 45M |
| Wire & Cable Works | Manufacturing | 2,000 t wire and cable | 2,000 t rod; insulation as upkeep | 400 | 2,500 | D$ 110M |

### 4.4 Balance

| Building | A | B | C | D |
|---|---|---|---|---|
| Copper Mine & Mill | 2.6% | 2.6% | 2.9% | 2.9% |
| Large Copper Mine | 2.8% | 2.8% | 3.1% | 3.1% |
| Leach & SX-EW Plant | 3.1% | 3.2% | 3.4% | 3.3% |
| Copper Smelter | −0.3% | 1.2% | 2.7% | 1.2% |
| Electrolytic Refinery | −1.9% | 0.8% | 3.5% | 0.8% |
| Scrap Copper Refinery | 0.0% | 2.2% | 2.9% | 0.7% |
| Rod Mill | −6.7% | 2.2% | 11.1% | 2.2% |
| Wire & Cable Works | −0.3% | 0.6% | 7.9% | 7.0% |

**What it says:**
- **Smelting is thin, as it really is.** At the 2024 charges a smelter earns about US$ 445 a tonne of copper in fees (derived from [K23]). The model's smelter adds about D$ 440 a tonne on the city's own concentrate, and loses 0.3% a month on imported concentrate, so it is never built without a mine.
- **Freight is the edge.** Concentrate pays freight both ways, about D$ 630 a tonne of copper, so a smelter beside the city's own mine keeps what one abroad would pay. Mockup 3's whole refining sector adds only D$ 4.4M a month on D$ 138M of sales.
- **Imported scrap just breaks even** (case A, −0.02%). The refinery is worth building on the city's own scrap and not on the world's, which is what the wider band was for. Mockup 3's refinery runs on 1,750 t of the city's scrap and 2,650 t imported at D$ 8,700.
- **The Wire & Cable Works is built to replace imports** (7.9%), not to export (0.6% on the city's own rod).
- **The 2026 collapse of the charges** is a world event the game's fixed world prices cannot show. Jerus chose the 2024 benchmark (Q11).
- **The rod mill's 11% in case C** is a thin premium on a big tonnage: the gap between imported rod (D$ 10,750) and the city's cathode (D$ 10,000).

---

## 5. Wood

### 5.1 Real figures

| Item | Figure | Source |
|---|---|---|
| Standing timber | world 152 m³ a ha; Canada 137, exactly the game's 13,700 m³ a km² | [W1][W2] |
| Harvest | Canada about 197 m³ a ha cut (2022); Swedish clear-fell 265 standing | [W2][W3] |
| Growth | natural boreal 1–3 m³ a ha a year, managed Nordic 4–6 (est.); plantations 15–40, rotations 5–25 years; establishment about US$ 850 a ha | [W4][W5] |
| Crews | a harvester US$ 800k and a forwarder 749k; about 29,000 m³ a year a set on one shift; US$ 15.91 a m³ stump to truck; 10,000–15,000 m³ a worker a year mechanised (est.) | [W6] |
| Logs | sawlogs about US$ 93–96 a m³ delivered (Finland, BC); pulpwood 46–58; harvest about 50:50 sawlog to pulpwood | [W14][W15] |
| Sawmills | Canfor Axis US$ 210M for 250 MMBF, 130 jobs; Biewer US$ 130M, 150 jobs; a log is 52% lumber, 29% chips, 12% sawdust and shavings, 7% bark; 99–109 kWh a m³ | [W18][W20][W24][W26][W27] |
| Panels (OSB) | Allendale 760 MMSF (about 673,000 m³), 135 staff; Huber US$ 440M, 158 jobs; 207 kWh a m³; resin 3.7% | [W31][W33][W34][W35] |
| Pulp | Arauco MAPA US$ 2.35bn for 1.56 Mt; Metsä Kemi 1.5 Mt with 250 direct jobs, 5.1 m³ of wood a tonne; 4.5 m³ a tonne (UK factor); 30–50 m³ of water a tonne; surplus power about 0.9 MWh a tonne | [W30][W37][W40][W42] |
| Pellets | Enviva Epes 1.1 Mt for about US$ 375M, about 100 jobs | [W46] |
| Prices, 2025–26 | framing lumber US$ 445 a thousand board feet (about 275 a m³ actual); structural panels 465 a thousand sq ft; NBSK net 680–750 a tonne; pellets CIF NW Europe 202; mill residues 22.50–24.50 a green ton | [W49][W52][W53][W54][W55] |
| Wood in a house | about 15,000 board feet of framing lumber + 9,000 sq ft of panels: about 0.11–0.16 m³ of lumber and 0.045 of panels a m² | [W50] |

### 5.2 Regrowth: the game's rule is the right rate
- **The rule.** `Resource.FOREST` regrows by 1/240 of its depletion a month. A clear-cut hectare (137 m³) therefore regrows about 0.57 m³ a month, or 6.9 m³ a year at first: a managed Nordic forest's rate [W4].
- **Only cut-over ground grows back,** which is right too: old forest is at steady state.
- **Sustainable cut and equilibrium.** The rule makes a forest's sustainable cut = what has been cut ÷ 240. A cut H a month settles where the deficit is 240 × H.

  In mockup 4 the natural woods (25.4M m³ full) are cut by 75,000 m³ a month, so they settle at 18.0M cut, 29% standing. That takes about 45 years.

  The Forestry page shows this as "where it settles". Jerus kept the rule and asked for it to be shown (Q13).
- **What the rule does to the crews** (found after A–E, `spec-zoning.md` §3.3). Because regrowth is largest when everything is cut (105.6k m³ a month for these woods at 0% standing, against 14.4k at 86%), crews that are not held strip the forest and keep it stripped. E's crew rule slows that and does not stop it; only forest the law protects stays standing.

### 5.3 Goods

| Good | Unit | Import / export, D$ | Notes |
|---|---|---|---|
| **SAWLOGS** | m³ | 110 / 65 | logs travel badly; flow |
| **PULPWOOD** | m³ | **90** (was 70) / 35 | flow; widened in §1.2 |
| **LUMBER** | m³ | 340 / 250 | stockable |
| **PANELS** (OSB) | m³ | 420 / **260** (was 300) | stockable; widened in §1.2 |
| **RESIDUES** (chips and sawdust) | m³ | **45** (was 35) / 15 | flow; widened in §1.2 |
| **PULP** | t | 760 / **600** (was 640) | stockable; widened in §1.2 |
| **PAPER** | t | 1,050 / 880 | new (Q16, §5.6); stockable |
| **WOOD_PELLETS** | t | 220 / **150** (was 160) | stockable; widened in §1.2 |

### 5.4 Buildings

| Building | Sector | Makes | Uses | Posts | Cost | Runs |
|---|---|---|---|---|---|---|
| Logging Camp | Forestry | 1,000 m³ sawlogs + 1,000 pulpwood | owned forest | 11 | D$ 3M | 61k |
| Harvesting Operation | Forestry | 6,250 + 6,250 m³ | owned forest, about 90 ha a month | 18 | D$ 15M | 217k |
| Tree Plantation | Forestry (Q15) | forest growing 20 m³ a ha a year on 1,000 ha | ground zoned for Forestry (B); planted as a cost of cutting (§5.7) | 5 | D$ 2M | 24k |
| Sawmill | Wood Products | 2,080 m³ lumber, 1,640 m³ residues | 4,000 m³ sawlogs | 35 | D$ 15M | 177k |
| Large Sawmill | Wood Products | 20,020 m³ lumber, 15,785 m³ residues | 38,500 m³ sawlogs | 130 | D$ 150M | 844k |
| Panel Mill | Wood Products | 15,000 m³ OSB | 27,750 m³ pulpwood; resin as upkeep | 100 | D$ 140M | 1.36M |
| Pulp Mill | Wood Products | 11,000 t pulp, and 13,500 kW sold to the grid (§1.6) | 28,000 m³ pulpwood, 21,500 m³ residues; 11,600 water units | 200 | D$ 260M | 2.17M |
| **Paper Mill** | Wood Products | 5,500 t paper | 5,775 t pulp; 1,450 water units; 850,000 L of fuel for its drying steam | 120 (50/45/20/5) | D$ 150M | 1.38M |
| Wood Pellet Mill | Wood Products | 6,000 t pellets | 13,200 m³ residues | 30 | D$ 30M | 183k |

### 5.5 Balance

| Building | A | B | C | D |
|---|---|---|---|---|
| Logging Camp | 1.3% | 1.3% | 4.6% | 4.6% |
| Harvesting Operation | 2.7% | 2.7% | 6.9% | 6.9% |
| Tree Plantation | −1.2% | −1.2% | −1.2% | −1.2% |
| Sawmill | −0.5% | 0.7% | 2.3% | 1.1% |
| Large Sawmill | 0.1% | 1.3% | 2.8% | 1.6% |
| Panel Mill | 0.0% | 1.1% | 2.8% | 1.7% |
| Pulp Mill | 0.4% | 1.3% | 1.9% | 1.1% |
| Paper Mill | −0.6% | 0.0% | 0.6% | 0.0% |
| Wood Pellet Mill | 0.4% | 1.7% | 3.1% | 1.8% |

**What it says:**
- **Residues have two buyers.** A cubic metre of residues is worth D$ 92 to a pulp mill (power sales included) and D$ 54 to a pellet mill. So the rule builds pellet mills only where there is no pulp mill to feed.
- **A paper mill only pays where the city buys paper** (§5.6): 0.0% on the city's own pulp exporting, 0.6% replacing imports.
- **A plantation never pays as an investment** (−1.2% a month, nothing to sell for 25 years), which is why it is planted as a cost of cutting (§5.7). That return leaves out its ground, which is the player's to price (B).

### 5.6 Paper (Q16)
Jerus: "Add a paper mill now".
- **The good.** `PAPER`, a tonne of printing and writing paper: D$ 1,050 / 880, stockable. UPM's paper business sold 731,000 t for €664M in Q1 2025, about €908 a tonne [W56], or about US$ 980.
- **The building.** The Paper Mill, Wood Products': 5,500 t a month from 5,775 t of pulp. 120 posts, 4,900 kW, 1,450 water units, D$ 150M, 19 ha. Its drying steam is about 5.5 GJ a tonne of paper (est.), bought as 850,000 L of fuel.
- **What it earns.** Thin, as paper made from bought pulp is: woodfree paper's price follows the pulp it is made from [W57]. On the city's own pulp, exporting, it earns 0.0% a month; replacing imported paper, 0.6%; on imported pulp, −0.6%.
- **Its buyer.** Nothing in the city buys paper yet. The natural first buyers are Retail, for packaging, and Business Services, for its offices. Adding paper to what they use is a demand-side change for whoever designs the next basket (§10.2). Until then the paper mill is on Wood Products' list and is not built, and mockup 4 says why.

### 5.7 Plantations (Q15, amended by B)
Jerus: "Forestry's". Then, for B: "in policy zoning will now exist".
- **Why it is not an investment.** A plantation costs D$ 2M and D$ 24k a month, and has nothing to sell for 25 years. No interest test passes it.
- **Why its ground needed a decision (B).** At business land prices its 1,000 ha cost D$ 0.5–7.7B. No cut carries that for wood it sells in 25 years.
- **The rule (est.).** Planting is a cost of cutting:
  - each month, if Forestry's cut exceeded regrowth (the natural forest's plus its plantations'), it plants one Tree Plantation;
  - **on 1,000 ha of ground the city has zoned for Forestry, at the zone's price** (Policy › Zoning, `spec-zoning.md`); with no Forestry zone it plants none;
  - paid from Forestry's cash and booked as a cost of cutting, outside the interest test;
  - it stops when regrowth covers the cut.
- **What one plantation does.** It grows 20 m³ a ha a year, 1,667 m³ a month on its 1,000 ha [W4].
- **In mockup 4** the cut (87,000 m³ a month) is 52,400 m³ above regrowth (34,600), so about 32 more plantations would close the gap. The mockup was drawn before B: its line points at the land office, and now it points at Policy › Zoning, where the player decides how much ground Forestry gets and at what price.

---

## 6. Stone

### 6.1 Limestone sells as aggregate
- **A quarry on a limestone field makes LIMESTONE.** Kilns and blast furnaces must have it.
- **What they leave is sold as aggregate,** at the aggregate price. 70% of real crushed stone is limestone [S1].
- **A hard-rock field makes AGGREGATE only.**

This one-way substitution is the only market rule here that is new to the game. Jerus agreed (Q17).

### 6.2 Real figures

| Item | Figure | Source |
|---|---|---|
| Quarries, US | 1,500 Mt from about 3,500 quarries (about 0.43 Mt each); about 21,000 t an employee a year (est.); US$ 18.50 a tonne (2025); 72% aggregate, 17% cement, 6% lime | [S1] |
| Costs | Vulcan's cash cost US$ 11.7 a tonne, price 24.2; a new crushing plant about US$ 20–30 a tonne a year (est.); 230 ha for a 3 Mst-a-year quarry; crushing 1.6–1.8 kWh a tonne | [S7][S15][S16][S18] |
| Freight | trucking US$ 0.16–0.33 a tonne-km (est.); price doubles in about 50 km (rural); freight 50–70% of the delivered price | [S10][S12][S13] |
| Cement | US 97 plants, 100 Mt of clinker, 13,000 jobs; capital US$ 107–170 a tonne a year (Africa) to about 300–400 (US, est.); 1.52–1.55 t of raw meal a tonne of clinker; clinker factor 0.71 (world) to 0.88 (US); 3.6 GJ a tonne of clinker; 91 kWh a tonne; US mill price US$ 160, Vietnamese exports about 37 | [S4][S9][S24][S25][S27][S31][S34] |
| Lime | US 15 Mt from 70 plants; 1.785 t of limestone a tonne in theory; 3.2–9.2 GJ a tonne; quicklime US$ 190–260; steel uses 75 kg a tonne (BOF) and 25–60 (EAF) | [S5][S6][S27][S38] |
| Ready-mix | 31,000–45,000 m³ a plant a year; US$ 200–247 a m³; 291 kg of cement and 1,954 kg of aggregate a m³; about 90 minutes from mixing to placing | [S7][S9][S41][S43] |
| Asphalt | 5% binder, 95% aggregate; 170,000–250,000 t a plant a year; US$ 90 a tonne | [S7][S15][S44] |

### 6.3 Goods

| Good | Unit | Import / export, D$ | Notes |
|---|---|---|---|
| **LIMESTONE** | t | 40 / 10 | sells as aggregate too (Q17) |
| **AGGREGATE** | t | 40 / 10 | the gap is freight |
| **DIMENSION_STONE** | t | 260 / 170 | |
| **CEMENT** | t | 110 / 45 | stockable |
| **LIME** | t | 280 / **110** (was 150) | widened in §1.2 |
| **CONCRETE** | m³ | not traded, priced by the seller at cost plus 15% (about D$ 149) | the 90-minute rule; with the construction upgrade (Q18) |
| **ASPHALT** | t | not traded, priced by the seller (about D$ 83) | with the construction upgrade (Q18) |

### 6.4 Buildings

| Building | Sector | Makes | Uses | Posts | Cost | Runs |
|---|---|---|---|---|---|---|
| Quarry | Quarrying | 40,000 t | 1 site | 22 | D$ 15M | 163k |
| Large Quarry | Quarrying | 250,000 t | 3 sites | 125 | D$ 90M | 981k |
| Dimension Stone Quarry | Quarrying | 1,000 t of blocks | 1 hard-rock site | 17 | D$ 6M | 104k |
| Cement Plant | Cement & Concrete | 25,000 t cement | 30,000 t limestone, 3,750 t aggregate, 3,350 t coal | 60 | D$ 120M | 501k |
| Large Cement Plant | Cement & Concrete | 125,000 t cement | 150,000 t limestone, 18,750 t aggregate, 16,750 t coal | 150 | D$ 500M | 1.73M |
| Lime Kiln | Cement & Concrete | 18,000 t lime | 32,400 t limestone, 3,600 t coal | 45 | D$ 75M | 298k |
| Ready-Mix Plant (later) | Cement & Concrete | 3,300 m³ concrete | 990 t cement, 6,435 t aggregate; 15 mixer trucks | 21 | D$ 9M | 125k |
| Asphalt Plant (later) | Cement & Concrete | 21,000 t asphalt | 19,950 t aggregate, 1,050 t bitumen | 12 | D$ 12M | 217k |

### 6.5 Balance
- **Quarries** cost D$ 4 a tonne to run. They return 1.6–1.7% a month exporting at D$ 10, and about 10% when they replace imports at D$ 40. Mockup 6's Large Quarry pays for itself in 10 months for that reason.
- **Cement plants** return 0.1–0.3% exporting and 1.5–1.9% replacing imports. So the rule builds one only when the city buys cement, as cement really is regional [S9].
- **The lime kiln** returns 1.6% on limestone at its export price, and 5.7% replacing imported lime. In mockup 5 limestone costs D$ 40, because aggregate is imported, so a second kiln that sells most of its lime abroad earns 0.6%: at the hurdle, not yet over it.

---

## 7. Where the chains meet

| From | Good | To | Rate |
|---|---|---|---|
| Coal Mining | coking coal | Iron & Steel, coke ovens | 1.35 t a tonne of coke [I36][C1] |
| Coal Mining | thermal coal | Iron & Steel, blast furnace and pellets | 0.15 t a tonne of iron; 0.03 a tonne of pellets |
| Coal Mining | thermal coal | Cement & Concrete, kilns | 0.13 t a tonne of cement; 0.20 a tonne of lime |
| Coal Mining | thermal coal | Coal Power Plants (the electricity run) | 0.57 t a MWh |
| Quarrying | limestone | Iron & Steel | 0.25 t a tonne of iron |
| Cement & Concrete | lime | Iron & Steel, the oxygen shop | 60 kg a tonne of steel |
| Cement & Concrete | lime | Copper Mining | about 1 kg a tonne of ore (est.) |
| Copper Refining | sulphuric acid | Copper Mining, the leach plant | 3.5 t a tonne of copper [K27] |
| Refining (oil) | bitumen | Cement & Concrete, asphalt | 5% of the mix [S44] |
| Wood Products | residues | Wood Products, the pulp mill | inside the sector |
| Wood Products | pulp | Wood Products, the paper mill | inside the sector, 1.05 t a tonne of paper |
| the city | old steel, old copper | Iron & Steel, Copper Refining | §2.7 |
| Iron & Steel | power from furnace gas | the grid | what the works does not use itself (§1.6) |
| Wood Products | power from the pulp mill | the grid | about 13.5 MW a mill (§1.6) |

---

## 8. Every new good

| Chain | Goods | Count |
|---|---|---|
| Iron | SCRAP, PELLETS, PIG_IRON, COKE (and IRON renamed Iron ore) | 4 |
| Coal | THERMAL_COAL, COKING_COAL, ROM_COAL | 3 |
| Copper | COPPER_CONC, ANODE, COPPER, COPPER_SCRAP, COPPER_ROD, ACID, WIRE_CABLE | 7 |
| Wood | SAWLOGS, PULPWOOD, LUMBER, PANELS, RESIDUES, PULP, PAPER, WOOD_PELLETS | 8 |
| Stone | LIMESTONE, AGGREGATE, DIMENSION_STONE, CEMENT, LIME, CONCRETE, ASPHALT | 7 |
| **Total** | | **29** |

Three existing goods change price: `STEEL`, `FABRICATED_STEEL` and `MACHINERY` (§2.6), and Automotive's cars, vans and trains move with the steel in them (C).

**Prices.** The import and export prices are in §2.3, §3.2, §4.2, §5.3 and §6.3. Each band sits around a 2025–26 world price, with freight set by how far the good really travels:
- **Wide bands** for aggregate, cement, logs, lime and acid, which travel badly.
- **Narrow bands** for cathode and pulp, which ship anywhere.

That follows `Good.java`'s rule of three quarters freight and one quarter the world's margin. Every band was then checked against the pure-profit rule (§1.2): in case A no processing building earns more than 0.4% a month.

**`Resource.java` needs a good for each resource:**
- STONE → LIMESTONE or AGGREGATE, by the field's kind;
- COAL → THERMAL_COAL or ROM_COAL;
- COPPER → COPPER_CONC or COPPER;
- FOREST → SAWLOGS and PULPWOOD.

**The in-ground share** sets the land office's price, as for iron and oil (5%). Recommended (est.):
- **iron: US$ 0.40 a tonne, kept** (D). It was 1/350 of the old US$ 140 floor; against the new D$ 85 export price the share is re-struck to about 1/212, so the founding field stays about US$ 180M instead of falling to US$ 109M.
- **coal 1/200;**
- **copper 1/100.** Copper is a rich, scarce field (0.0005 a km²).
- **stone 1/400.**
- **forest 2%.** Its standing timber at the pulpwood price.

---

## 9. For the construction upgrade (the next run)
Jerus: "construction will be due to an upgrade afterwards, cause well now buildings will be using different materials". This run only makes the materials. Here is what the builders will draw, for whoever designs that upgrade.

### 9.1 Per m² of floor

| Material | Single-family | Multi-family | Non-residential | Sources |
|---|---|---|---|---|
| Concrete, kg (median) | 449 | 819 | 831 | Heeren & Fishman, 910 buildings [S48] |
| Concrete, m³ | about 0.19 | 0.34 | 0.35 | from the above (est.) |
| Of which cement and aggregate | about 12% and 82% of that mass | | | [S41] |
| Steel, kg | 5–15 (timber house), 25–50 (concrete) | 25–50 | 50–110 (steel frame) | [I65] |
| Lumber, m³ | 0.11–0.16 | | | [W50] |
| Panels, m³ | about 0.045 | | | [W50] |
| Copper, kg | about 1.0 | about 1.4 | 1–2 (est.) | [K44] |

### 9.2 Per km of road (for the gravel to paved upgrade)

| Road | Material | Source |
|---|---|---|
| Gravel | about 2,300 t compacted, about 3,000 t delivered | [S49] |
| Two-lane paved | about 8,000–12,000 t of aggregate (est.), plus asphalt at 5% binder (the oil study's bitumen) | |
| Four-lane interstate | about 21,400 t | [S14] |

### 9.3 Against today's materials unit
Today a House is ten units of D$ 18,000 of `MATERIALS`, with the Construction Materials Plant making them from nothing but labour. A House of about 150 m² would draw (est., from §9.1):
- about 30 m³ of concrete;
- 20 m³ of lumber;
- 7 m³ of panels;
- 3 t of steel;
- 150 kg of copper.

That is about D$ 14,000–20,000 of commodities, at export and import prices and steel at its new world price, against D$ 180,000 of materials; the rest of a building's materials are manufactured goods (windows, fixtures, plumbing) and their fitting. The upgrade will have to decide whether the unit keeps a "fit-out" share, imported or made by Manufacturing. It is listed here and not decided.

---

## 10. Decisions (Jerus, 2026-10-08)
All 22 questions were answered the same day. Each line gives the question, his answer, and what it changed here.

1. **The shape.** One sector that digs and one that processes per resource, plus Manufacturing for iron and copper. Jerus: "what you did is perfect". He asked whether each mining operation should be its own sector, and chose **one sector per resource**. Unchanged.
2. **Names.** **Rename** Mining → Iron Mining and Heavy Industry → Iron & Steel, keeping the saved keys.
3. **Field kinds.** **Yes, by real shares** (§1.4), from each field's own hash.
4. **Scrap splits from ore.** **Split.** `IRON` becomes iron ore only; SCRAP feeds the arc furnaces. MiningCheck's arc-furnace bands are re-set (§2.5).
5. **The Iron Mine.** **20,000 t** a month with the same 60 posts, at D$ 45M.
6. **Steel's price.** **Move to world prices:** D$ 650 / 470 (§2.6).
   - 6b. **Fabricated steel and machinery move with steel**, so Manufacturing's returns hold. Cars, vans and trains too (C).
7. **The gate.** **Whatever earns most**, with **real freight** in the bands instead of a gate (§1.2). Jerus asked whether the exchange rate handles pure profit forever. It pushes back, but the game has measured it failing on its own; freight is what really stops a city importing ore to export steel, so freight does it here. Eight bands were widened.
8. **Scrap yards** **pay the sellers 70%** of scrap's export price (§2.7).
9. **Coal washing.** **A separate building**, the Coal Preparation Plant (§3.3).
10. **Coal power.** **Wait for the electricity run** (§3.5).
11. **Copper charges.** **The 2024 benchmark** (§4.2).
12. **Sulphuric acid.** **A traded good** (§4.2).
13. **Forest regrowth.** **Keep the 1/240 rule, and show it** on the Forestry page (§5.2).
14. **Forestry's gate.** "the map will include forest": **no limit** on the cut. The page shows where the forest settles (§1.3). **Amended by E** (§10.3): a crew is added only while the forest Forestry may cut would last 120 months at the cut with it.
15. **Tree Plantation.** **Forestry's**, planted as a cost of cutting outside the interest test (§5.7). **Amended by B** (§10.3): only on ground zoned for Forestry, at the zone's price.
16. **Paper.** **Add a paper mill now** (§5.6). It is on the list, and is built where the city buys paper.
17. **Limestone sells as aggregate.** **Yes** (§6.1).
18. **Concrete and asphalt.** **Yes, with the construction upgrade:** not traded, priced by the seller.
19. **Two sizes** where real plants are big. **Yes.**
20. **The works and the grid.** **Now:** the coke ovens, blast furnace, oxygen shop and pulp mill make their own power, and sell what they do not use (§1.6).
21. **Seven new sectors.** In the order **iron, coal, stone, copper, wood**, one batch a chain.
22. **New building ids.** **After the oil run's**, from `buildings.json`'s `nextId`.

### 10.1 For the implementer
In the order of Q21, one batch a chain:
1. **Iron.**
   - Goods: `IRON` renamed iron ore (115 / 85); SCRAP, PELLETS, PIG_IRON, COKE; `STEEL`, `FABRICATED_STEEL` and `MACHINERY` at their new prices (§2.3); Automotive's cars, vans and trains moved by the steel in them (C).
   - Buildings: four in Iron Mining, nine in Iron & Steel (§2.4), the old-steel pool and the Scrap Yard's 70% (§2.7).
   - Own power: a `gen` figure on buildings, and the sale of the surplus at the tariff (§1.6).
   - The rule: `HeavyIndustry.plan()` drops its spare-ore gate for pure profit, with a new building's output valued at the import price up to the shortfall (§1.2). Every order is tested on its whole cost, whoever pays (A).
   - The land office: iron in the ground stays at US$ 0.40 a tonne, the in-ground share re-struck against the new ore price (D, §8).
   - Harnesses: MiningCheck's arc-furnace bands are re-set at the new steel price, and "local ore" becomes "local scrap" (§2.5). Manufacturing's premises keep their logic; the prices they quote need re-reading (§2.6).
2. **Coal.** THERMAL_COAL, COKING_COAL, ROM_COAL; Coal Mining's four buildings; field kinds for coal.
3. **Stone.** LIMESTONE, AGGREGATE, DIMENSION_STONE, CEMENT, LIME; Quarrying's three buildings; Cement & Concrete's three kilns and plants. The ready-mix and asphalt plants, CONCRETE and ASPHALT wait for the construction upgrade (Q18).
4. **Copper.** Seven goods; Copper Mining's three buildings, Copper Refining's four, and the Wire & Cable Works in Manufacturing.
5. **Wood.** Eight goods, PAPER among them; Forestry's three buildings, with E's crew rule and planting only on Forestry's zone (§5.7, §10.3); Wood Products' six. **Zoning and the forest law come first** (`spec-zoning.md` §6): the planting rule needs the Forestry zone, and E counts only the forest the law lets Forestry cut.

After each batch: the ensemble, and case A checked at the weakest rate the currency reaches (§1.2).

### 10.2 Still open
None of these blocks the work.
- **Who buys paper.** Retail's packaging and Business Services' offices are the natural buyers; that is a basket change (§5.6).
- **Oil's refinery gate.** Q7 was asked for the five chains here. The oil run's Refining has the same kind of gate since 0.7.62; whether it goes too is the oil agent's call.
- **The existing arc furnaces' costs** at the new steel price (§2.5).
- **The tariff.** Own power sells at D$ 10 a kW a month; the electricity run will move it (§1.6).
- **Zoning's own decisions** are all made (Z1–Z13, `spec-zoning.md` §5): E at 120 months, the zone's tax rows the sector's own dials, cleared forest sold by Forestry as a salvage cut, and no age rule for regrown wood for now.

### 10.3 The build plan's questions A–E (2026-10-08)
Put to Jerus by the build plan for 0.8.0 (iron redone, coal, copper, wood and stone, batches I1–I14). A, C, D and E are decided as the plan recommended; B is Jerus's own answer, zoning.

- **A. When a works may be built.** Today a works paid from the sector's own cash is not asked to earn anything, so loss-making plants get built. **Decided: test every order on its whole cost, whoever pays** (§1.2). With zoning, the ground in that cost is at its zone's price.
- **B. Plantation land.** 1,000 ha at business land prices costs D$ 0.5–7.7B. The plan recommended the city keep the ground and license it to Forestry, like Crown forest. **Decided instead: zoning** (`spec-zoning.md`). Policy gets a fifth area, Zoning: the default is no zones, anything anywhere; a zone holds km² or a share of the dry ground for one industry, at a share of the going price down to free, assessed for property tax at that price, with that industry's profit, sales and property dials shown in place (the same dials as Policy › Taxes). Forestry plants only on ground zoned for it, at the zone's price, and with no zone plants none (§5.7). A forest law splits the city's forest into protected (never cut, never built on), working (cut, never built on) and open (cut or built on); the default is all open.
- **C. Car plant prices.** With steel at the world price, car makers' inputs get cheaper. **Decided: car, van and train prices move with the steel in them** (§2.6), as fabricated steel and machinery did.
- **D. Iron ground price.** The new ore price would drop the starting field from US$ 180M to US$ 109M. **Decided: keep US$ 0.40 a tonne** in the ground (§8).
- **E. Forestry crews.** Nothing caps them, and one crew strips 10 km² in 11 months. **Decided: add a crew only while the forest would last 120 months at that cutting rate** (§1.3), counting only the forest Forestry may cut: working and open forest and its plantations, not protected forest. The plan recommended 36 months; **Jerus set 120** ("yes 120"), a crew's working life rather than its payback, so a big forest does not build 21 crews to keep 10. A 10 km² forest then gets no crew at all (even a Logging Camp needs 240,000 m³). On a big forest E slows the strip and does not stop it (0% standing in about 17 years instead of 5, `spec-zoning.md` §3.2), because the regrowth rule pays most at 0%; the forest law's protected share is what stays standing.

---

## 11. The mockups
Static HTML, one file each, in `claude/mockups-materials/`. Open them in a browser; nothing is fetched except the IBM Plex fonts. Each chain page has two windows, in the same city and month as the oil mockups (June 2183, 2.61M people). The figures across pages agree: the coal the steelworks buys is the coal the mines sell, and the lime the steel shop uses is the kiln's. All seven were redrawn with the decisions of §10, but not with A–E (§10.3): mockup 4's planting line and mockup 6's seventh crew were drawn before B and E. The zoning screens are the artifact "Zoning Study".

| File | Shows |
|---|---|
| `0-the-five-chains.html` | All 39 new buildings and the 3 changed ones, by chain and industry, with posts and cost; the two rules as decided; where the chains meet, power included. |
| `1-iron.html` | **Iron Mining:** four fields drawn as vessels filled to what is left, eleven mines, ore by grade to its buyers. **Iron & Steel:** ore, coal, limestone, lime and scrap through coke ovens, blast furnaces and steel shops into a furnace-shaped products pictogram. Steel at the world's price, all used at home with 18,900 t imported; 101 MW of the works' own power. |
| `2-coal.html` | Coal Mining in a small town (one surface mine, everything abroad) and the big city (longwall mines through a wash plant; the power plants drawn as a later buyer, Q10). |
| `3-copper.html` | **Copper Mining:** sulphide and oxide fields, concentrate and leach cathode. **Copper Refining:** smelter, refinery, scrap refinery and rod mill, in tonnes of copper contained, with the acid noted; scrap at D$ 8,700. |
| `4-wood.html` | **Forestry:** two woods and the plantations, where the forest settles at this cut, and the planting rule waiting on land. **Wood Products:** sawmills feeding the pulp mill their chips; builders as a later buyer; why the paper mill is not built yet. |
| `5-stone.html` | **Quarrying:** limestone and granite fields, limestone sold as aggregate, imported aggregate hatched. **Cement & Concrete:** kilns burning the city's coal, ready-mix and asphalt as later; why the second lime kiln waits. |
| `6-build-cards.html` | Six cards: Blast Furnace (ranked second), Copper Smelter (it would lose money on imported concentrate), Large Sawmill (the player's order against the investors), and three investors' orders on site: Underground Coal Mine, Large Quarry, and Harvesting Operation, whose card shows what a seventh crew costs the forest. |

**House style:** the oil mockups' frame at 1,389 × 868, palette and type. The pictogram is the oil refinery pictogram made general. Ribbons are to scale, in the chain's own unit:
- tonnes;
- tonnes of copper contained;
- m³ of wood.

---

## 12. Sources
*Each chain keeps its research pass's own numbering, so some numbers are unused. Five passes ran on 2026-10-08. All five hit the shared web-search limit near the end. Figures they could not source are marked (est.) above.*

**Iron and steel [I]**
1. USGS MCS 2026, iron ore: https://pubs.usgs.gov/periodicals/mcs2026/mcs2026-iron-ore.pdf
2. USGS MCS 2026, iron and steel: https://pubs.usgs.gov/periodicals/mcs2026/mcs2026-iron-steel.pdf
3. USGS MCS 2026, iron and steel scrap: https://pubs.usgs.gov/periodicals/mcs2026/mcs2026-iron-steel-scrap.pdf
4. Rio Tinto FY2025 results: https://www.sec.gov/Archives/edgar/data/863064/000162828026009503/ye25-pressreleasex19feb.htm
5. Rio Tinto, Simandou presentation: https://www.sec.gov/Archives/edgar/data/863064/000162828024000045/ex08d06presentation.htm
6. Fortescue, June 2026 quarterly: https://announcements.asx.com.au/asxpdf/20260731/pdf/07282pwdb2jzw4.pdf
8. Mining News, Iron Bridge costs: https://www.miningnews.net/exploration-development/news/1411036/costs-blow-fmgs-iron-bridge
10. Mining Technology, Gudai-Darri: https://www.mining-technology.com/projects/gudai-darri-iron-ore-project-pilbara-australia/
11. NS Energy, Onslow Iron: https://www.nsenergybusiness.com/projects/onslow-iron-ore-project
15. ArcelorMittal, Hibbing feasibility (2019): https://minedocs.com/20/Arcelormittal-Hibbing-FS-2019.pdf
16. Rana Gruber, annual report 2025: https://live.euronext.com/sites/default/files/company_press_releases/attachments_oslo/2026/03/25/669236_Rana%20Gruber%20annual%202025%20print.pdf
17. Champion Iron, Q1 FY2027: https://newsroom.championiron.com/2026-07-29-CHAMPION-IRON-REPORTS-ITS-FY2027-FIRST-QUARTER-RESULTS
18. Vale, 2Q26: https://api.mziq.com/mzfilemanager/v2/d/53207d1c-63b4-48f1-96b7-19869fae19fe/69d9def3-395b-67c8-7af2-78cff5924cf2?origin=2
21. SMM, iron ore and energy prices (2026): https://news.metal.com/en/newscontent/103869831-smm-analysis-will-there-be-production-cuts-in-iron-ore-mine-while-global-energy-prices-surging
24. LBNL, world best practice energy intensity: https://eta-publications.lbl.gov/sites/default/files/industrial_best_practice_en.pdf
25. US DOE, iron and steel bandwidth (2015): https://www.energy.gov/sites/prod/files/2015/08/f26/iron_and_steel_bandwidth_report_0.pdf
27. IEA, Iron and Steel Technology Roadmap: https://iea.blob.core.windows.net/assets/eb0c8ec1-3665-4959-97d0-187ceca189a8/Iron_and_Steel_Technology_Roadmap.pdf
29. GEM wiki, Hoa Phat Dung Quat: https://www.gem.wiki/Hoa_Phat_Dung_Quat_steel_plant
30. VietnamPlus, Hoa Phat Dung Quat 2: https://en.vietnamplus.vn/hoa-phat-earmarks-367-bln-usd-for-hoa-phat-dung-quat-2-iron-steel-project-post200516.vnp
31. GEM wiki, Tata Steel IJmuiden: https://www.gem.wiki/Tata_Steel_IJmuiden_steel_plant
35. Wikipedia, Gary Works: https://en.wikipedia.org/wiki/Gary_Works
36. IEAGHG / Horvath, coke making: https://publications.ieaghg.org/docs/General_Docs/Iron%20and%20Steel%202%20Secured%20presentations/1620%20Ladislav%20Horvath.pdf
38. RITE, BF-BOF energy intensity (2019): https://www.rite.or.jp/system/en/global-warming-ouyou/download-data/2019_BF-BOF_energy%20intensity%20by%20RITE.pdf
39. RITE, energy efficiency comparison (2010): https://www.rite.or.jp/system/en/global-warming-ouyou/download-data/E-Comparison_EnergyEfficiency2010steel.pdf
41. Ispat Guru, BOF: https://www.ispatguru.com/?p=2511
45. Nucor, West Virginia sheet mill: https://nucor.com/news-release/nucor-provides-one-year-update-on-new-west-virginia-sheet-mill-122563
46. KRIS TV, Sinton: https://www.kristv.com/news/local-news/1-9-million-dollar-steel-mill-to-open-in-sinton
47. Steel Dynamics 10-K 2022: https://www.sec.gov/Archives/edgar/data/1022671/000155837023002303/stld-20221231x10k.htm
49. Ispat Guru, Midrex: https://www.ispatguru.com/midrex-process-for-direct-reduction-of-iron-ore/
52. worldsteel, raw materials (2018): https://steel.org.au/getattachment/458fa31b-2586-47bb-a645-9411140863dd/WSA_fact_raw_materials_2018.pdf
53. Trading Economics, iron ore: https://tradingeconomics.com/commodity/iron-ore
57. SteelBenchmarker history: http://steelbenchmarker.com/history.pdf
58. LME, steel scrap CFR Turkey: https://www.lme.com/en/metals/ferrous/lme-steel-scrap-cfr-turkey-platts
59. Steelonthenet, pig iron: https://www.steelonthenet.com/prices/pig-iron.html
63. Automotive Manufacturing Solutions, steel in vehicles: https://www.automotivemanufacturingsolutions.com/materials/steel-makes-the-case-as-the-mainstay-material-for-vehicles/40294.article
64. Mobility Global, US vehicle age 2024: https://www.mobilityglobal.com/en-us/automotive-insights/blog/average-age-vehicles-united-states-2024
65. Steel Construction Info, cost of structural steelwork: https://www.steelconstruction.info/Cost_of_structural_steelwork

**Coal [C]**
1. EIA, Annual Coal Report 2024: https://www.eia.gov/coal/annual/
5. EIA, Electric Power Annual, table 8.1: https://www.eia.gov/electricity/annual/html/epa_08_01.html
6. EIA, Electric Power Annual, tables 4.8.A and 7.5: https://www.eia.gov/electricity/annual/html/epa_04_08_a.html
9. SteelOrbis, Australian coking coal forecast (Dec 2025): https://www.steelorbis.com/steel-news/latest-news/australia-forecasts-phcc-prices-to-stay-near-190mt-in-2026-1426272.htm
10. EIA, Quarterly Coal Report: https://www.eia.gov/coal/production/quarterly/
11. EPA, default heat content (40 CFR 98): https://www.epa.gov/system/files/documents/2022-05/Appendix%20C_Default%20Heat%20Content%20Ratios%20in%20CAERS%20V2.pdf
13. IEA, Coal Mid-Year Update 2026, demand: https://www.iea.org/reports/coal-mid-year-update-2026/demand
16. IEA, Coal 2025, prices and costs: https://www.iea.org/reports/coal-2025/prices-and-costs
17. IEA, Coal Mid-Year Update 2026, prices: https://www.iea.org/reports/coal-mid-year-update-2026/prices
22. Warrior Met Coal, Blue Creek: https://investors.warriormetcoal.com/news-releases/2022/05-03-2022-130148418
25. IEA, Cement: https://www.iea.org/reports/cement-3 ; Peabody 10-K FY2024: https://www.sec.gov/Archives/edgar/data/1064728/000106472825000018/btu-20241231.htm
26. EU BAT conclusions, cement, lime and magnesium oxide (2013/163/EU): https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32013D0163
27. Queensland Coordinator-General, Winchester South: https://www.statedevelopment.qld.gov.au/coordinator-general/assessments-and-approvals/coordinated-projects/current-projects/winchester-south-project
29. Winchester South economic assessment (2022): https://eisdocs.dsdip.qld.gov.au/Winchester%20South/Revised%20draft%20EIS/attachment-16-economics.pdf
30. Olive Downs EIS, executive summary: https://eisdocs.dsdip.qld.gov.au/Olive%20Downs/Draft%20EIS/executive-summary.pdf
31. Olive Downs EIS, project description: https://eisdocs.dsdip.qld.gov.au/Olive%20Downs/Draft%20EIS/section-2-project-description.pdf
35. US DOE, energy profile of coal mining (2002): https://www.energy.gov/sites/prod/files/2013/11/f4/coal.pdf

**Copper [K]**
1. USGS MCS 2026, copper: https://pubs.usgs.gov/periodicals/mcs2026/mcs2026-copper.pdf
2. ICSG, World Copper Factbook 2025: https://icsg.org/copper-factbook/
3. Mining Weekly, Quellaveco (2022): https://www.miningweekly.com/article/quellaveco-copper-project-peru-update-2022-08-04
4. Wikipedia, Quellaveco: https://en.wikipedia.org/wiki/Quellaveco_mine
5. Hudbay, Copper World PFS (2023): https://www.globenewswire.com/news-release/2023/09/08/2739939/0/en/hudbay-de-risks-copper-world-phase-i-with-enhanced-pre-feasibility-study.html
6. Hudbay, Copper World PFS presentation: https://s23.q4cdn.com/405985100/files/doc_presentations/2023/09/08/Copper-World-PFS-Presentation_Sept2023_FINAL.pdf
10. Northern Miner, Marimaca DFS: https://www.northernminer.com/news/marimaca-copper-targets-2026-build-start-on-new-dfs/1003881916/
11. Marimaca, project overview: https://marimaca.com/marimaca-copper-project-overview/
12. CRU, Chilean concentrators (2025): https://www.crugroup.com/en/communities/thought-leadership/2025/chilean-copper-competitiveness-an-overview-of-energy-cost-trends-for-concentrators/
13. AT Minerals, energy intensity in copper mining: https://www.at-minerals.com/en/artikel/at_-3001684.html
15. Cochilco, water use (2016): https://www.cochilco.cl/Comunicados%20Ingles/2016%2004%2026%20Water%20use%20in%20copper%20mining%20-%20Expomin%202016%20(Camila%20Montes).pdf
16. Ivanhoe, first anode at Kamoa-Kakula: https://www.newsfilecorp.com/release/279364/Ivanhoe-Mines-Announces-First-Anode-Production-from-KamoaKakula-Copper-Smelter
17. Ivanhoe, 2 Jan 2026: https://www.ivanhoemines.com/wp-content/uploads/2026-01-02_Kamoa_Kakula.pdf
18. Indonesia Business Post, Manyar smelter: https://indonesiabusinesspost.com/2210/investment-and-risk/freeports-317-billion-smelter-to-commence-production-in-august-2024
21. Total Materia, copper production processes: https://www.totalmateria.com/es/articles/copper-production-processes/
23. Mining.com, 2024 TC/RC: https://www.mining.com/web/antofagasta-agrees-lower-2024-copper-charges-with-chinese-smelter-at-80-per-ton-sources/
24. Kitco, zero TC/RC for 2026: https://www.kitco.com/news/off-the-wire/2025-12-19/antofagasta-agrees-zero-copper-processing-charges-2026-chinese-smelter
27. SMM, sulphuric acid (May 2026): https://news.metal.com/es/newscontent/103892148-an%C3%A1lisis-smm-%C3%A1cido-sulf%C3%BArico-una-variable-clave-que-redefine-la-l%C3%B3gica-de-fijaci%C3%B3n-de-precios-del-cobre
29. S&P Global, 21 Jul 2026: https://www.spglobal.com/energy/en/news-research/latest-news/metals/072126-china-refined-copper-output-hits-record-high-despite-concentrate-crunch
32. Aurubis Richmond: https://www.aurubis.com/richmond/media/Press-Releases/aurubis-celebrates-ribbon-cutting-at-the-first-multimetal-recycling-plant-in-the-united-states
34. Metalcharts, scrap copper prices: https://metalcharts.org/scrap-copper-prices
35. SMS group, Contirod line: https://www.sms-group.com/press-and-media/press-releases/press-release-detail/conticon-boosts-copper-rod-production-for-the-wire-and-cable-industry-with-a-new-state-of-the-art-sms-group-contirodr-line-in-north-america
39. Westmetall, LME averages: https://www.westmetall.com/en/markdaten.php?action=averages&field=LME_Cu_cash
40. XTB, copper record (Sep 2026): https://www.xtb.com/en/market-analysis/copper-is-breaking-ath-what-does-kghm-have-to-say-about-this
44. Copper Development Association, copper in the home: https://www.copper.org/education/c-facts/home/print-category.html
45. International Copper Association, e-mobility (2017): https://internationalcopper.org/wp-content/uploads/2017/06/2017.06-E-Mobility-Factsheet-1.pdf
46. Visual Capitalist / IEA, metals in clean energy: https://www.visualcapitalist.com/sp/visualized-how-much-metal-is-used-in-clean-energy-technology/

**Wood [W]**
1. FAO, FRA 2025, growing stock: https://openknowledge.fao.org/server/api/core/bitstreams/2dee6e93-1988-4659-aa89-30dd20b43b15/content/FRA-2025/growing-stock-biomass-carbon.html
2. NRCan, State of Canada's Forests 2024: https://prod-natural-resources.azure.cloud.nrcan-rncan.gc.ca/sites/admin/files/documents/2025-08/StateofForestReport-2024-EN.pdf
3. Skogsstyrelsen, fellings: https://skogsstyrelsen.se/en/statistics/felling/fellings
4. NZ Journal of Forestry Science, plantation growth: https://nzjforestryscience.springeropen.com/articles/10.1186/1179-5395-44-S1-S10/tables/4
5. Alabama Extension, southern forestry practice costs 2024: https://www.aces.edu/blog/topics/forestry/costs-trends-of-southern-forestry-practices-2024/
6. Silva Fennica, harvester and forwarder systems: https://silvafennica.fi/raw/26004
14. Luke (Finland), roundwood trade 2025: https://www.luke.fi/en/statistics/volumes-and-prices-in-industrial-roundwood-trade/volumes-and-prices-in-roundwood-trade-2025
15. BC Interior log market report (2025): https://www2.gov.bc.ca/assets/gov/farming-natural-resources-and-industry/forestry/timber-pricing/interior-timber-pricing/interior-log-market-reports/3mi_july_25.pdf
18. Made in Alabama, Canfor Axis: https://www.madeinalabama.com/2022/08/canfor-to-invest-210-million-in-new-alabama-sawmill-complex/
20. Woodworking Network, Biewer Newton: https://woodworkingnetwork.com/news/woodworking-industry-news/biewer-lumber-invests-130-million-new-mississippi-plant-will-add-150
24. Forisk, Oregon sawmill residues: https://forisk.com/oregon-sawmill-species-and-residual-production/
26. Forest Products Journal, California lumber: https://fpj.kglmeridian.com/view/journals/fpro/75/2/article-p95.xml
27. USDA Forest Service, sawmill energy: https://research.fs.usda.gov/treesearch/37321
30. Forest Research (UK), conversion factors (2019): https://forestresearch.gov.uk/tools-and-resources/statistics/forestry-statistics/forestry-statistics-2019/sources-4/timber/conversion-factors-4
31. IWF, West Fraser Allendale: https://iwfatlanta.com/industry-trends/west-fraser-pays-280m-for-idle-osb-mill/
33. Twin Cities Business, Huber Cohasset: https://tcbmag.com/huber-engineered-woods-to-build-440-million-mill-on-iron-range/
34. Wood and Fiber Science, OSB life cycle: https://wfs.swst.org/index.php/wfs/article/view/1749
35. OSB environmental product declaration: https://www.astm.org/v3/assets/blt5eb0a2cb04534832/blt2c67803e9b766a5c/682765f9be86181a925424ab/OSB_EPD_Final.pdf
37. Bioenergy International, Arauco MAPA: https://bioenergyinternational.com/biochemicals-materials/arauco-approves-us2-35-billion-construction-of-mapa
40. Paperage, Metsä Kemi: https://paperage.com/2019news/04_29_2019metsa_fibre_kemi_pulp_mill.html
42. Pulp & Paper Canada, EU BAT levels: https://www.pulpandpapercanada.com/a-closer-look-at-european-bat-levels-1000148944/
46. Bioenergy International, Enviva Epes: https://bioenergyinternational.com/groundbreaking-ceremony-for-worlds-largest-pellet-plant/
49. NRCan, current lumber, pulp and panel prices: https://prod-natural-resources.azure.cloud.nrcan-rncan.gc.ca/domestic-international-markets/current-lumber-pulp-panel-prices
50. NAHB, framing lumber prices: https://nahb.org/News-and-Economics/Housing-Economics/National-Statistics/Framing-Lumber-Prices
52. Sinpacel, price indicators (2026): https://www.sinpacel.org.br/wp-content/uploads/2026/03/indicadores-de-precos.pdf
53. Fastmarkets, pulp prices: https://www.fastmarkets.com/insights/pulp-market-prices-oversupply-us-bleached-kraft-pulp-downtime-producer-inventories-rise/
54. Argus Biomass Markets sample (Oct 2025): https://view.argusmedia.com/rs/584-BUW-606/images/NET-Sample-Report-Argus%20Biomass%20Markets%20%282025-10-22%29.pdf
55. Forisk, biomass prices: https://forisk.com/biomass-prices-continue-to-decline-in-the-u-s-south/
56. UPM, Interim Report Q1 2025 (Communication Papers: sales €664M, deliveries 731 kt): https://www.upm.com/siteassets/asset/investors/2025/upm-interim-report-q1-2025-en.pdf
57. Fastmarkets, woodfree paper prices and pulp costs (July 2024): https://www.fastmarkets.com/insights/woodfree-paper-prices-increase-slightly-in-q2/

**Stone [S]**
1. USGS MCS 2026, crushed stone: https://pubs.usgs.gov/periodicals/mcs2026/mcs2026-stone-crushed.pdf
4. USGS MCS 2026, cement: https://pubs.usgs.gov/periodicals/mcs2026/mcs2026-cement.pdf
5. USGS MCS 2026, lime: https://pubs.usgs.gov/periodicals/mcs2026/mcs2026-lime.pdf
6. USGS MCS 2025, lime: https://pubs.usgs.gov/periodicals/mcs2025/mcs2025-lime.pdf
7. Vulcan Materials 10-K FY2025: https://www.sec.gov/Archives/edgar/data/1396009/000162828026009546/vmc-20251231.htm
9. Eagle Materials 10-K FY2026: https://www.sec.gov/Archives/edgar/data/918646/000119312526230979/exp-20260331.htm
10. French, transportation of mineral aggregates in Indiana (about 1968): https://journals.indianapolis.iu.edu/index.php/ias/article/download/7013/7024/13139
12. Rock Products, regional pricing (2025): https://rockproducts.com/2025/01/03/the-2024-regional-pricing-puzzle/
13. Aggregate Markets, delivery cost per mile: https://aggregatemarkets.com/en/blog/delivery-ordering/aggregate-delivery-cost-per-mile-ton-guide
14. Wyoming State Geological Survey, aggregate: https://main.wsgs.wyo.gov/mineral-resources/industrial-minerals/aggregate
15. Pit & Quarry, Ozark quarry (2025): https://www.pitandquarry.com/new-plants-new-pace-at-capital-aggregates-ozark-quarry/
16. Industrial Info, Texas Hill Country quarry (2003): https://www.industrialinfo.com/news/article/capital-aggregates-developing-new-limestone-quarry-in-the-texas-hill-country--17852
18. Mitchell, BGS, aggregate carbon demand (2010): https://nora.nerc.ac.uk/id/eprint/11458/1/EIG_2010_Aggregate_Carbon_Demand_Clive_Mitchell_BGS.pdf
24. IEA, Cement: https://www.iea.org/reports/cement-3
25. IEA/CSI, Cement Technology Roadmap (2018): https://iea.blob.core.windows.net/assets/cbaa3da1-fd61-4c2a-8719-31538f59b54f/TechnologyRoadmapLowCarbonTransitionintheCementIndustry.pdf
27. EU BREF, cement, lime and magnesium oxide (2010): https://www.umweltbundesamt.de/system/files/medien/419/dokumente/clm_bref_0510.pdf
31. Global Cement, South Africa: https://www.globalcement.com/news/18963-is-capacity-expansion-coming-to-south-africa/
34. Global Cement, Vietnamese exports (Aug 2026): https://www.globalcement.com/news/21233-vietnamese-cement-exports-rise-in-august-2026
38. National Lime Association, metallurgical uses: https://www.lime.org/resource/metallurgical-uses-of-lime/
41. EPA AP-42 §11.12 (2006): https://www.epa.gov/sites/default/files/2020-10/documents/c11s12.pdf
43. ConcreteNetwork, concrete prices (2025): https://www.concretenetwork.com/concrete-prices.html
44. NAPA, asphalt facts: https://www.asphaltpavement.org/all-about-asphalt/asphalt-facts/
48. Heeren & Fishman, material intensity database v1.2: https://github.com/nheeren/material_intensity_db (paper doi:10.1038/s41597-019-0021-x)
49. FHWA, Gravel Roads guide (2015): https://www.fhwa.dot.gov/construction/pubs/ots15002.pdf
