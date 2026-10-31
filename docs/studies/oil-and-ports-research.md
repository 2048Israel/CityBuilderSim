# Oil and ports: design research and mockups

*2026-10-08. Read-only design study for the run after the block grid, answering `brief-oil-and-ports-design.md`. No game code was read or changed. Every figure has a source in §7, cited by letter and number: [R] refining, [W] wells, [P] ports. "(est.)" marks anything estimated or derived here rather than taken from a source. Money is US$ in the research tables, and D$ (at parity, for illustration) in the game proposals and mockups.*

## 0. In one page

- **The refinery is many buildings.** The crude unit splits crude into seven cuts. Conversion units turn the cuts that are cheap into products that are dear: a reformer, a cracking unit (the FCC), a hydrocracker, an alkylation unit, a coker, a lube plant and an asphalt unit.
- **Eight products**, plus coke as a by-product: gas (LPG), naphtha, petrol, jet fuel, diesel, lubricants, fuel oil and bitumen (§2.1). Today's single `FUEL` splits into these.
- **One rule decides what gets built** (§2.3). A unit's **spread** is what it makes minus its feed, valued at the city's own prices: the import price for a product the city is short of, the export price for one it has spare. Investors build the widest spread that passes the gates: feed, land, staff and money. Demand pulls the refinery with no scripting.
- **Wells come in two types.** The **Land Well** stays (100 b/d) and gains a decline. The new **Offshore Platform** is a fixed platform on owned shallow sea: 12 wells, 18,000 b/d for 3 years, then down 8.5% a year (§3).
- **Ports are four terminal kinds**: tanker, bulk, container and general cargo (§4).
  - **Boats come from trade:** calls a month = tonnes ÷ one ship's cargo.
  - **A call is a schedule entry, not an object.** The map draws only the boats whose route is on screen.
- **Storage, pipes and ships** (§4.5):
  - a **Tank Farm** makes crude and products storable; a tanker's whole cargo needs somewhere to go, and a city-owned tank farm is a strategic reserve;
  - a **Pipeline** costs a lot once and little after, so it is built only where the flow is big and long-lived; trucks, rail and shuttle tankers stay the default;
  - a **Shipyard** builds ships for a city-owned fleet that keeps freight money at home and carries goods between the city's own ports.
- **Four mockups**, in house style at 1,389 × 868 (§6):
  - the refinery pictogram, in a small town and a big city;
  - the oil industry screen;
  - ports and boats at two zooms;
  - three build cards.
- **Seventeen questions for Jerus** (§5), each with a recommendation. The three that change the most:
  - **Q1:** two sizes per refinery building;
  - **Q5:** residue must be cut with diesel to sell as fuel oil, or cokers never pay;
  - **Q9:** ports as a city building.

---

## 1. How a real refinery works, at game resolution

### 1.1 The column: straight-run yields

Distillation splits crude by boiling point. Light crude gives more naphtha and diesel. Heavy crude gives more residue, which is worth little without conversion units.

| Cut | Brent, light, 38° API [R1] (vol%) | Maya, heavy, 21.5° API [R2] (vol%) | Medium, SG 0.84 [R38] (vol%) | Heavy, SG 0.92 [R38] (vol%) |
|---|---|---|---|---|
| Gas / LPG | 4.1 | 0.3 | 2 | 0 |
| Light naphtha | 8.4 | 15.3 (all naphtha) | 27 (all naphtha) | 10 (all naphtha) |
| Heavy naphtha | 15.9 | ↑ | ↑ | ↑ |
| Kerosene | 13.9 | 13.8 | 12 | 7 |
| Diesel / gas oil | 25.6 | 9.4 | 27 | 26 |
| Vacuum gas oil | 21.3 | 24.3 | 20 | 27 |
| Vacuum residue | 11.4 | 36.9 | 12 | 30 |

A typical crude unit is 100,000–175,000 b/d. It costs US$1,050–1,240 per b/d, and the vacuum unit US$1,680–2,030 (2017). Process units are about half a refinery's installed cost; utilities and offsites are the rest [R5].

**For the game (est.):** give each oil field one of three grades. Light uses the Brent column above. Heavy uses the Maya column. Medium is the world blend imports use: gas 1.5, light naphtha 6, heavy naphtha 12, kerosene 12, diesel 22, gas oil 24, residue 22.5.

### 1.2 Conversion units: what each turns into what

| Unit | Feed | Real yields | Game set (est.), vol% of feed |
|---|---|---|---|
| Catalytic reformer | heavy naphtha | reformate 82–86 wt%, plus hydrogen and gas [R6][R11] | petrol 82, gas 8; 10 burned as refinery fuel; makes the hydrogen the hydro-units use |
| Fluid catalytic cracking (FCC); game name **Cracking Unit** | vacuum gas oil | LPG 25, gasoline 45, light cycle oil 16, slurry 9, coke 4 (wt%); liquid volume above 100% [R8][R10] | gas 25, petrol 55, diesel 18, fuel oil 7 (105% by volume) |
| Hydrocracker | vacuum gas oil + H₂ | distillate mode: 85–90 vol% jet and diesel; total liquid 110–115 vol% (est.) [R7][R9] | diesel 50, jet 40, naphtha 15, gas 5 (110%) |
| Alkylation | C3/C4 olefins + isobutane | alkylate, 90–94 RON; about 1.75 bbl per bbl of olefin [R6][R7] | gas 100 → petrol 80 |
| Delayed coker | vacuum residue | coke 25–30 wt%; liquids about 55 wt%; the rest gas [R7][R13][R14] | gas 8, naphtha 15, diesel 35, fuel oil 12, plus coke at 30% of the feed's weight |
| Hydrotreater | any distillate + H₂ | the same stream with its sulphur removed, about 1% loss [R7] | no change of product (see Q12) |
| Lube oil plant | vacuum gas oil | base oil 29–50% by route [R15] | lubricants 40, fuel oil 60 |
| Asphalt unit | vacuum residue of suitable crude | bitumen 20–50 wt% of crude; only 10–15% of crudes suit [R17][R18] | bitumen 95 |

### 1.3 Size, cost, energy and staff

| Unit | Typical size (b/d) | Capital per b/d, US$ (year) | Fuel, kBtu/bbl | Power, kWh/bbl | Hydrogen, scf/bbl |
|---|---|---|---|---|---|
| Crude unit | 100k–175k | 1,050–1,240 (2017) [R5] | 106.8 | 0.7 | — |
| Vacuum unit | 35k–65k | 1,680–2,030 (2017) [R5] | 87.9 | 0.35 | — |
| FCC | 50k–60k | 6,750–7,000 (2017) [R5] | 62 + burns its own coke | 3.7 | — |
| Hydrocracker | 50k–60k | 8,270 (2017) [R5] | 120 | 11.4 | uses 1,200–2,500 [R7] |
| Reformer | 15k–30k | 8,370–10,270 (2017) [R5] | 254 | 2.9 | **makes** 1,100–1,700 [R6] |
| Alkylation | 13k–18k | about 23,000, whole project (2019) [R12] | 209–240 | 1.4–12.0 | — |
| Delayed coker | 35k–55k | 7,900–8,800 (2017) [R5] | 128 | 3.6 | — |
| Hydrotreater | 14k–50k | 2,000 (naphtha) to 6,700 (gas oil) (2017) [R5] | 62 | 5.5 | 25–2,000 by feed [R7] |
| Lube plant | about 8k of base oil (est.) | about 45,000 per b/d of base oil (est., 2021) [R16] | 1,318 | 17.9 | — |

Energy figures are from DOE's 2005 profile [R6].

**Staff.** Unit-level staffing is rarely published. A simple 100 kb/d refinery runs on about 368 staff: 200 in operations, 80 in maintenance and 88 in support. Complex ones run on 537–679 [R5]. Real plants run 1.5–4.6 staff per thousand b/d [R25][R26][R27]. **Per unit (est.):** 10–15 operators across all shifts [R5].

### 1.4 Whole refineries

| | Capacity, b/d | Cost (year) | $ per b/d |
|---|---|---|---|
| Simple (hydroskimming) [R5] | 100,000 | $2.26bn total capital (2017) | 22,600 |
| Conversion with a coker [R5] | 220,000 | $6.60bn (2017) | 30,000 |
| Dangote, Nigeria [R23] | 650,000 | $20bn (2024) | 30,800 |
| Dos Bocas, Mexico [R24] | 340,000 | $21bn (2025) | 61,600 |

| Complexity | Nelson index | Petrol / jet + diesel, vol% of crude [R7] |
|---|---|---|
| Topping | under 2 | 31 / 30 |
| Hydroskimming | 2–6 | 28 / 30 |
| Conversion | 6–12 | 44 / 32 |
| Deep conversion (coking) | over 12 | 47 / 42 |

The US average Nelson index is 9.5; the world's was 5.9 in 1996 [R21]. US conversion capacity as a share of crude capacity, January 2026: FCC 27%, reformer 18%, coker 14%, hydrocracker 12% (est., from [R19][R20]). Those ratios size the big city's units in the mockups.

### 1.5 Product prices against crude

| Product | Ratio to crude | Basis |
|---|---|---|
| Propane / LPG | 0.46 per barrel | US Gulf, 2025 [R28] |
| Naphtha | about 1.0 per barrel; 1.19 per tonne | Japan, Feb 2025 [R31] |
| Gasoline / petrol | 1.13–1.23 per barrel | Singapore and US Gulf, 2025 [R28][R31] |
| Jet fuel | 1.28 per barrel | US Gulf, 2025 [R28] |
| Diesel | 1.23–1.35 per barrel | Singapore and US Gulf, 2025 [R28][R31] |
| Heavy fuel oil | 0.78–0.97 per tonne; about 1.0 per barrel (est.) | Singapore, 2025–26 [R31][R32][R33] |
| Lubricant base oil (Group II) | 1.88 per barrel | US, 2025 [R35] |
| Bitumen | 1.08 per tonne, about 1.2–1.3 per litre (est.) | US index, 2025 [R36] |
| Petroleum coke | 0.12–0.19 per tonne | US Gulf, China, 2025 [R34] |

What the US barrel became in 2025 [R37]:

| Product | % of crude in |
|---|---|
| Petrol | 45.9 |
| Diesel | 30.0 |
| Jet fuel | 11.0 |
| Coke | 4.6 |
| Refinery gas | 3.9 |
| LPG | 3.6 |
| Asphalt | 2.0 |
| Fuel oil | 1.9 |
| Petrochemical feed | 1.4 |
| Lubricants | 1.0 |
| Other | 0.9 |
| **Total, with processing gain** | **about 106** |

The ladder is what pays for conversion. Light products sell at 1.2–1.35× crude. LPG and naphtha sell at 0.5–1.0×. Fuel oil, bitumen and coke sell at or below crude by the tonne.

---

## 2. The game's products, and how the refinery decides

### 2.1 Eight products and a by-product

| Product | Unit | Who buys it in a city | World price, × crude (per litre, est.) | Buyer exists today? |
|---|---|---|---|---|
| Gas (LPG) | litres | industry heat; homes for heating and cooking; petrochemicals | 0.46 | no: exported |
| Naphtha | litres | petrochemical plants; the reformer's feed | 1.0 | no: exported |
| Petrol | litres | cars (households, `Motoring`) | 1.20 | **yes** |
| Jet fuel | litres | an airport | 1.28 | no: exported |
| Diesel | litres | rail, vans and trucks, farms, builders, mines, ships' marine diesel | 1.35 | **yes** (rail) |
| Lubricants | litres | Manufacturing, Automotive, rail, cars' motor oil | 1.89 | no |
| Fuel oil | litres (or tonnes) | ships' bunkers at a port; oil-fired power | 0.98 | no: exported |
| Bitumen | tonnes | paved roads and highways (Construction) | 1.2 | no: exported |
| Coke (by-product of the coker) | tonnes | steel mills, power plants | 0.12–0.19 per tonne | — |

Import price = world × 1.08; export price = world × 0.92 (est.). That is the same wedge idea as `Good`'s import and export prices today.

### 2.2 Phasing the buyers

Until a buyer exists, its product goes abroad at the export price. The refinery still works without any of them; it just exports more.

1. **With the oil run:** split `FUEL` into petrol (cars) and diesel (rail). Add diesel to vans and trucks. Add bitumen to paving, which ties into Jerus's gravel → paved upgrade. Add lubricants to Manufacturing and Automotive.
2. **With ports:** fuel oil and marine diesel for ships' bunkers; crude and product tankers.
3. **Later:**
   - an airport (jet fuel);
   - LPG for homes and industry;
   - a petrochemical plant (naphtha and gas into plastics);
   - an oil-fired power plant (fuel oil).

### 2.3 The rule: what the refinery builds

A rule the simulation can follow, not an optimiser:

1. **Value every product at the city's own price** this month:
   - the import price if the city imported any of it;
   - the export price if it exported any;
   - in between if it did neither.
2. **A unit's spread** = the value of what it makes from one litre of feed − the value of that litre of feed. A feed with no buyer is worth what it would become if the unit were not there. For example, gas oil left over becomes fuel oil.
3. **Gates, in order:**
   1. **feed:** spare feed covers at least 60% of one unit, as rail's `MIN_LINE_UTILISATION`;
   2. **land:** the unit's land is free on the refinery's site;
   3. **staff:** the existing `Staffing.why()`;
   4. **money:** the existing rule that it earns at least 1.25× its interest.
4. **Build:** each month, the unit with the highest spread × feed ÷ capital among those passing every gate gets one more, the same pace as today's planners.
5. **Idle:** a unit whose spread stays below zero for 6 months idles, and later sheds, by the existing distress rule.
6. **More crude units:** a new crude unit is built when the standing ones run above 90% and the light products are short.

**Worked example.** The big city of the mockups, June 2183. Spreads are D$ per litre of feed, at the city's own prices.

| Unit | Spread | Gate | Result |
|---|---|---|---|
| Alkylation | **+0.266** | 29.0M L of gas exported: passes | **building** |
| Hydrocracker | +0.199 | 4.5M L of gas oil spare, needs 48.4M L | no feed |
| Lube plant | +0.145 | no gas oil spare | — |
| Cracking unit | +0.105 | no gas oil spare | — |
| Reformer | +0.080 | no naphtha spare | — |
| Asphalt unit | +0.063 | roads take 71% of its bitumen | enough |
| Coker | **−0.031** | — | not built (see Q5) |

The rule gives Jerus's example on its own: "if demand for petrol is higher, it builds the buildings that turn X into petrol". No product is scripted. Petrol is imported, so it is valued at the import price. Gas is exported, so it is valued at the export price. The spread between them is widest for the unit that turns gas into petrol.

**A finding (mockup 1, state A).** A simple 2,000 b/d plant pays only on the town's own crude, which is valued at the export price. On imported crude it loses D$0.05M a month before wages. That matches the real economics of small simple refineries, and it is why the rule must see crude's price the same way it sees the products'.

### 2.4 Game buildings (proposal, est.)

Capital uses §1.3's cost per b/d. Power uses its kWh per barrel. Staff are 10–15 operators a unit plus maintenance.

| Building | Size, b/d of feed | Capital, D$ | Posts | Power, kW | Land |
|---|---|---|---|---|---|
| Topping plant (today's Oil Refinery) | 2,000 | 60M (as today) | 100 | 1,000 | 8 ha |
| Crude unit (large) | 100,000 | 400M | 160 | 4,400 | 40 ha |
| Reformer | 12,000 | 108M | 30 | 1,450 | 4 ha |
| Cracking unit | 15,000 | 105M | 40 | 2,300 | 6 ha |
| Hydrocracker | 10,000 | 83M | 35 | 4,750 | 5 ha |
| Alkylation unit | 5,000 | 115M | 30 | 2,500 | 3 ha |
| Coker | 10,000 | 84M | 40 | 1,500 | 8 ha |
| Lube plant | 2,000 | 36M | 30 | 1,500 | 4 ha |
| Asphalt unit | 3,000 | 5.4M | 10 | 60 | 2 ha |

---

## 3. Oil wells: land against offshore

### 3.1 The research

| | Onshore, conventional | Shallow sea, fixed platform (under 150 m) | Deep sea, floating (over 500 m) |
|---|---|---|---|
| **Capital** | $2.5M a well, vertical (2014) [W1]; shale $4.9–8.3M [W1] | about $13–15M a well (est.) [W13]; a 24-slot jacket about $18M (est.) [W13]; North Sea fields $3–3.5bn [W17] | $81–240M a well (2015) [W1]; FPSO projects about $9bn [W21] |
| **Output** | US average 30 b/d; a new vertical well 20–100 b/d (est.) [W3]; shale about 730 b/d falling to 200 in a year [W4] | Gulf shelf wells about 100 b/d [W15]; North Sea, about 100,000 b/d from one field (est.) [W17] | about 2,400 b/d a well; 75,000–220,000 b/d a facility [W1][W15][W21] |
| **Wells per site** | 1 (shale 5–10 a pad) [W10] | 1–5 small; 18–24 slots typical [W14] | 6–20+ subsea [W1] |
| **Decline** | 10–25% in year one (est.); fields 4.2% a year [W6] | ramp, 2–3 year plateau, then 8.5% a year [W6][W16] | plateau, then 10.3% a year [W6] |
| **Life** | 20-year wells; fields 15–30 years [W1][W16] | platforms 20–40 years (est.) [W19] | designed for 20–30+ years [W1][W24] |
| **Crew** | 0.19–0.39 full-time workers a well [W12] | 6 to 60+ on board; a third staffed round the clock; rotations [W14] | 20–200 beds; 2-on/2-off, so the headcount doubles [W14][W27] |
| **Area** | about 0.8 ha a well with roads [W8] | a 500 m safety zone, about 1 km² [W32][W33] | FPSO 333 × 60 m, plus the safety zone [W22] |
| **Lifting cost** | $2–15/boe (2014–15) [W1] | $8–25/boe [W14][W29] | $9–17/boe [W14] |
| **Lead time** | 3 months [W6] | 1–2 years [W6][W13] | 5+ years [W6] |

### 3.2 Proposal

**Land Well (keep).** Its 100 b/d is a good new vertical well, and its 2-acre pad matches the USGS 0.8 ha [W8].

- **Add a decline:** each well loses 10% a year (est.) and retires below 10 b/d or when its field is dry. The sector's one-well-a-month rule already refills.
- **Flag:** the game gives a well 3 posts against a real 0.2–0.4 (Q13).

**Offshore Platform (new).** All figures are est.

| Item | Value |
|---|---|
| Structure | a fixed steel platform, 12 wells |
| Output | 18,000 b/d for 3 years, then 8.5% less a year; life about 25 years |
| Capital | about D$500M |
| Posts | 100: 40 on board on 2-on/2-off, plus 20 ashore |
| Running cost | about D$8.2M a month (US$15 a barrel) |
| Lead time | about 24 months |
| Site | 1 km² of owned sea with an oil field, at most 150 m deep |
| Link ashore | by shuttle tanker to a Tanker Terminal, until a Pipeline pays for itself (§4.5) |

**Deep water: leave it out.** Real deepwater sits 150–450 km out [W1][W31], off any city's map. It could come back as an off-map "frontier" field if wanted.

*DW1 (2026-10-31, Jerus: "Allow deep, costlier"): a sea field past 150 m takes a floating platform (an FPSO) down to 2,914 m, the Turritella FPSO's record at Shell's Stones field [W34]; its price, its wells' and its upkeep follow the depth by the ratios of the NEMS offshore cost equations [W13] (eq. 3-4 to 3-7, 3-10, 3-15). See `docs/queue/DW1-notes.md` and `sectors/Oil.java`, DEEP WATER.*

### 3.3 On the map

- **Where it sits:** on the city's own sea blocks over an offshore oil field. Shallow fields lie on the shelf, 20–100 km out in reality [W14][W15]. The map draws only what the city has, so the platform is drawn on its owned sea.
- **Footprint:** draw the 500 m safety zone as a faint ring.
- **Link ashore:** shuttle tankers to the Tanker Terminal, or a dashed Pipeline once one is built.
- **Water depth:** the map needs a depth per sea block (Q7).

---

## 4. Ports

### 4.1 Terminal kinds

| | Tanker (liquid bulk) | Dry bulk | Container | General cargo |
|---|---|---|---|---|
| **A berth handles a year** | 2–4.5 Mt [P1][P2] | 25–30 Mt [P3][P5] | 0.5–1M TEU [P6][P7] | about 0.3–0.6 Mt (500–650 t per m of quay) [P6][P8] |
| **Quay per berth** | 270–345 m [P1] | about 300 m for Capesize (est.) | 350–475 m [P6][P7] | 180 m [P12] |
| **Land** | 27 ha of tanks for 1.3M m³ [P13] | 22 ha of stockyard for 27 Mt a year [P14] | about 20 ha per berth (est.) [P15] | 0.4–1.5 ha per berth [P12] |
| **Capital** | about $96M a berth (2022) [P11] | about $84 per t a year of capacity (2014) [P3] | about $461M a berth (2024) [P7] | $70M for a 5 Mt multipurpose berth (2015) [P9] |
| **Staff** | about 1.5–3 per Mt a year (est.) [P13][P28] | 6–11 per Mt a year [P22] | about 0.65–0.9 per 1,000 TEU [P25][P26] | 18–33 per Mt a year (est.) [P27] |

UNCTAD's limits on how busy berths may be: 40% with one berth, 50% with two, rising to 70% with six to ten [P10].

### 4.2 Ships

| Class | Cargo a voyage | Speed | Time in port |
|---|---|---|---|
| MR product tanker | 37,000–38,000 t; 190k–345k bbl of petrol [P30][P31] | 14.5 kn [P32] | about 24 h [P33] |
| LR1 / LR2 | 55,000–65,000 t / 75,000–90,000 t [P30] | about 14.5 kn | about 24 h |
| Aframax (crude) | 70,000–80,000 t, about 0.55M bbl [P30] | — | about 24 h |
| Suezmax / VLCC | 130,000–145,000 t / 260,000–270,000 t [P30] | 16 kn [P36] | about 24 h |
| Handysize / Supramax | about 30,000–35,000 t / 55,000–60,000 t (est.) [P37] | 11–14 kn [P38][P39] | about 51 h [P33] |
| Panamax / Capesize (bulk) | 66,000 t / 160,000–170,000 t [P40][P42] | 12–14.5 kn [P36][P41] | about 51 h |
| Feeder / Panamax / neo-Panamax (boxes) | under 3,000 / 3,000–5,100 / 10,000–15,500 TEU [P43] | 16–22 kn [P36][P43] | about 19 h |
| General cargo | about 5,000–6,000 t (est.) [P46] | 9–20 kn [P36] | about 28 h |

A loaded TEU carries about 8–10 t of cargo (est.) [P43][P44][P45].

### 4.3 The rule: how many boats

1. **Calls a month** = tonnes a month ÷ the cargo of one ship of the class that carries it. To get tonnes from litres, use litres per tonne: crude 1,165, petrol 1,320, diesel 1,180, jet 1,260, fuel oil 1,010, LPG 1,850 [P35] (est. for LPG and fuel oil).
2. **The class** is the largest the berth's depth allows whose cargo fits in the tank or yard stock.
3. **Containers:** TEU = tonnes ÷ 9. One call carries both directions, so count the larger direction.
4. **Tankers and bulk carriers carry one way.** They arrive full and leave empty, or the reverse.
5. **Berths needed** = calls × hours in port ÷ (730 × UNCTAD's limit for that many berths).

**Worked examples.** Two from [P30][P42][P5]; the city's port.

- **1,000,000 t a month of iron ore out:** 1,000,000 ÷ 170,000 = 5.9 Capesize calls. Port Hedland's real figure is 172,000 t a visit (est.) [P5].
- **50M litres a month of petrol in:** 50M ÷ 1,320 = 37,900 t, so about 1 MR call.
- **The big city's port (mockup 3):** 15.8 calls a month in total:

  | Trade | Ship | Calls a month |
  |---|---|---|
  | Crude in, 144,200 t | Aframax | 1.9 |
  | Petrol and diesel in | MR | 0.9 |
  | Fuel oil out | LR1 | 0.8 |
  | Jet fuel out | MR | 0.4 |
  | LPG out | gas carrier | 0.8 |
  | Bitumen out | small tanker | 0.8 |
  | Iron ore out | Capesize | 5.9 |
  | Containers | feeder | 4.3 |

  Its tanker berths are 9% busy and its bulk berth 41%.

### 4.4 Drawing the boats: only what the city has, only where you look

- **A call is a schedule entry:** a route, a ship class, a direction, and an arrival time spread through the month by a hash of (good, month, call number).
- **A boat's position is a pure function of time:** position = route(call, t). The map asks which calls' routes cross the screen right now and draws only those. Nothing is simulated off screen. Panning away and back shows the same boat in the same place.
- **Count follows trade exactly.** A city with no seaborne trade has no boats.
- **Colours:**
  - imports arriving full: blue;
  - exports leaving full: orange;
  - the empty leg: grey, riding high;
  - container ships, both ways: white.
- **Shape gives the kind:** tanker, bulk, boxes or gas.
- **Scale:** at district zoom a 245 m Aframax is 40 px at true scale. Draw boats at 1.6× (stated in the legend), and at true size from close zoom in.
- **Map clock:** see Q8.

### 4.5 Storage, pipelines and ships

**Tank Farm.** Today crude is a flow good: a well ships what it lifts, the same month. Ports break that. One Aframax lands 75,000 t at once while the refinery takes it in steadily, so the cargo needs somewhere to go.

| Item | Proposal |
|---|---|
| What it does | stores crude or products up to its capacity; a tanker is unloaded only into free tank space |
| Size | 500,000 m³ (about 3.1M barrels), about 10 ha of land, at 27 ha per 1.3M m³ [P13] |
| Capital | about D$190M (est., at about US$60 a barrel of capacity). The one figure found, Edmonton's Base Line Terminal, cost $728M for 4.8M barrels including pipeline links [S1], so about $150 a barrel all-in is the upper bound |
| Owner | Refining by default, as working stock. The **city** can also own one as a **strategic reserve**: it buys when oil is cheap and releases in a price shock, a lever in the spirit of the game |

**Pipeline.** A network like rail, priced per km. Crude or products moved by pipe pay only the pipe's running cost and put no load on the roads, the way rail relieves them today.

| Item | Proposal |
|---|---|
| Capital | onshore about D$3M a km, offshore about D$4.5M a km (est.). The US record for 2024–25 was $12.1M a mile ($7.5M a km), up from $7.8M a mile the year before, for gas lines [S2]; US costs are the world's highest |
| Runs | light: a pump station's power and a small crew (est.) |
| Built by | whoever owns the flow: Oil for field → refinery, Refining for refinery → port or → city |

**The pipeline rule.** This is Jerus's "expensive, but long-term worth it", as a rule. A sector builds a pipeline when the freight it saves over the flow's life pays the pipe back with interest:

> (freight a tonne by truck, rail or tanker − the pipe's running cost a tonne) × tonnes a month × the months the flow will last ≥ capital × 1.25

- **The months the flow will last** is the field's reserves left ÷ its lifting rate, capped at the pipe's 40-year life (est.). A big, long-lived field passes; a small one never does.
- **The flow must be steady:** the refinery is making money and the field is not past its plateau.
- **Otherwise** crude goes by truck (road load), rail, or shuttle tanker, as today. On a small field, or at a refinery that may not last, that stays the cheaper choice.

**Offshore crude: tanker or pipe.** Offshore crude does not have to go by tanker. Real shelf platforms mostly pipe ashore [W17][W18]; remote ones use shuttle tankers [W23]. In the game, a new platform ships by **shuttle tanker** to a Tanker Terminal, paying freight each month. The pipeline rule above then decides when a Pipeline replaces the tankers. A platform 38 km out needs about D$171M of pipe (est.); against 74,750 t a month of crude for a decade, it usually pays.

**Shipyard and the city's fleet.** By default the world's ships carry the city's sea trade, and the freight they earn leaves the city as an import of services. That is realistic: most countries charter.

| Item | Proposal |
|---|---|
| Shipyard | a Heavy Industry building that turns fabricated steel and machinery into ships |
| Who buys its ships | a new **Shipping** sector, the city's own fleet, which carries the city's trade and keeps that freight money at home, the way the railway does for land freight; and shuttle tankers for the platforms |
| Export | only with **bounded world demand**. The rolling-stock lesson (19 Locomotive Works when the world would buy without limit) applies to ships too |
| Ship prices | a 5-year-old MR tanker was worth about US$45M in February 2024 [S3]; new ones cost more, about US$50M for an MR and US$70M for an Aframax (est.) |

**Between the city's own ports.** With two or more ports, goods moving between distant districts can go by sea when that is cheaper than road or rail. Coastal shipping relieves the roads as rail does, and its boats are drawn on the map like any other call; both ends are on the city's map, so the whole voyage is visible. It needs the block grid's real distances between districts.

---

## 5. Questions for Jerus

Each has a recommendation; most can be answered with "yes" or a number.

1. **Plant sizes.** Should refinery buildings come in two sizes, as rail has three rungs?
   - **Recommend yes.** Today's 2,000 b/d Oil Refinery becomes the small topping plant. Add a large 100,000 b/d crude unit and two sizes of each conversion unit.
   - Otherwise a big city repeats the 5,000-daycares problem with thousands of small refineries.
2. **Products.** The eight in §2.1, with coke as a by-product, and `FUEL` split into petrol and diesel?
   - **Recommend yes.** It is a save-format break.
3. **Crude grades.** Should each oil field be light, medium or heavy, setting its cuts, with imports a medium blend?
   - **Recommend yes.** It is what makes a heavy field want a coker and an asphalt unit.
4. **Buyers.** Phase them as in §2.2: petrol, diesel, bitumen and lubricants first; ports' fuels next; jet, LPG and naphtha last?
   - **Recommend yes.** Until a buyer exists, the product goes abroad.
5. **Residue and the coker.** As the rule stands, leftover residue sells as fuel oil, so a coker never pays. Mockup 2 shows it at −D$0.031 a litre.
   - **Recommend a mechanism, not a modifier:** residue sells as fuel oil only when cut with diesel, 3 parts residue to 1 of diesel, as in reality.
   - That values residue at D$0.31 a litre against fuel oil's D$0.39, and the coker's spread becomes +D$0.05.
6. **Well decline.** Land wells lose 10% a year each; platforms hold a 3-year plateau, then lose 8.5% a year?
   - **Recommend yes.** It is the real mechanism behind "the oil runs out".
7. **Offshore.** Only shallow fixed platforms, on owned sea at most 150 m deep, shipped ashore by shuttle tanker until a pipeline pays (§4.5)?
   - **Recommend yes.** Deepwater is left out; it lies off any city's map.
   - **Needed:** does the map's sea have a depth per block? If not, add one with the Earth-map work (the after-the-grid list, §6).
8. **The map's clock.** One game month = about 60 s of boats on the map, with arrival times hashed as in §4.4?
   - **Recommend yes.** At that rate the big city shows about one boat every 4 s at district zoom.
9. **Who owns ports.** A city building, like roads, or a sector business charging fees?
   - **Recommend a city building.** Its benefit is a lower freight wedge on the city's seaborne trade.
   - At believable fees a tanker terminal earns about D$0.9M a month on D$205M of capital (est.), so no investor would build one.
10. **What goes by sea.** Should the freight wedge fall for a coastal city with the right terminal, so each good takes the cheaper of sea and rail?
    - **Recommend yes:** tankers for crude and products, bulk for ore, coal and grain, containers for manufactured goods.
11. **The refinery on the map.** Draw it as a campus of separate unit buildings on one site?
    - **Recommend yes.** It is how "the refinery is many buildings" reads at a glance.
12. **Hydrotreating.** Leave it out at first?
    - **Recommend yes.** Later, a sulphur standard on the Policy tab could require treating capacity for fuel sold here.
13. **Land Well jobs.** The game has 3 posts a well against a real 0.2–0.4 [W12].
    - **Recommend 1 post a well,** or keep 3 if the jobs matter to play.
14. **Tank Farm.** A building that makes crude and products storable, with the city able to own one as a strategic reserve?
    - **Recommend yes, in the oil run.** Ports depend on it: a tanker's cargo must land in free tank space.
15. **Pipeline.** Built by the sector that owns the flow, by the rule in §4.5, with trucks, rail and shuttle tankers as the default?
    - **Recommend yes.** It gives "expensive, but worth it long-term" as arithmetic: a big field passes, a small one doesn't.
16. **Shipyard and a city fleet.** A Shipyard building ships for a new Shipping sector, exported only with bounded world demand?
    - **Recommend yes, in the run after ports.** The first ports run uses the world's ships for everything, so the fleet can be added without changing them.
17. **Coastal shipping.** With two or more ports, goods between districts may go by sea when cheaper?
    - **Recommend yes,** once the block grid gives distances between districts.

---

## 6. The mockups

Static HTML, one file each. Open them in a browser; nothing is fetched except the IBM Plex fonts.

| File | Shows |
|---|---|
| `1-refinery-pictogram.html` | Refining's Operations page. Crude in, the column filled with cuts, the conversion units, the refinery pictogram filled by product, and the buyers, all as ribbons to scale. A strip gives each product's made, imported and exported. Two states: a small town with one simple refinery (one reformer), and a big city with four units and a fifth on site. |
| `2-oil-industry-screen.html` | Wells by type (count, output, reserves left, decline) with a ten-year forecast, the refinery's units with each unit's spread and gate, and the product table (price, × crude, made, used, imported, exported, where it went). |
| `3-ports-and-boats.html` | The port district at 7.6 km (four terminals, lanes to the map's edge, ten boats, the port's calls this month, and an inset showing what is drawn and what is only scheduled), and the tanker terminal close up (an Aframax at T1, an MR with tugs for T2). |
| `4-build-cards.html` | Offshore Platform (blocked: no free sea field), Alkylation Unit (an investor's order on site) and Tanker Terminal (the player's order pending), in the 0.7.25 card layout, with a new Ports category. Pipeline, Tank Farm and Shipyard appear in the group lists under the cards. |

All four use the brief's palette and type: stage `#0b1118`, IBM Plex Sans and Mono. All figures come from one worked model of each city (§2.3), so the pages agree with each other.

---

## 7. Sources

**Refining [R]**
1. TotalEnergies, Brent assay (2006): https://trading.totalenergies.com/wp-content/uploads/2025/11/BRENT.pdf
2. Oil & Gas Journal, Guide to World Crudes: Maya: https://www.ogj.com/home/article/17215393/guide-to-world-crudes
3. TotalEnergies, Fort Hills dilbit assay (2017): https://trading.totalenergies.com/wp-content/uploads/2025/11/FORT-HILLS-REDUCED-CARBON-LIFE-CYCLE-DILBIT.pdf
4. Crude Monitor, WCS: https://crudemonitor.ca/crudes/crude.php?acr=WCS
5. SINTEF, ReCAP reference refineries, economics (2017): https://www.sintef.no/globalassets/project/recap/deliverable-d3_reference-plants-economic-evaluation_final_code.pdf
6. US DOE, Energy and Environmental Profile of US Petroleum Refining (2007): https://www.energy.gov/sites/prod/files/2013/11/f4/profile.pdf
7. ICCT/MathPro, Petroleum Refining and the Production of ULSG and ULSD (2011): https://theicct.org/sites/default/files/publications/ICCT05_Refining_Tutorial_FINAL_R1.pdf
8. Jechura, Colorado School of Mines, catalytic cracking: https://people.mines.edu/jjechura/wp-content/uploads/sites/120/2019/02/CBEN409_07_Catalytic_Cracking.pdf
9. Jechura, hydroprocessing: https://people.mines.edu/jjechura/wp-content/uploads/sites/120/2019/02/CBEN409_08_Hydroprocessing.pdf
10. PTQ, the FCC unit as a propylene source (2007): https://cdn.digitalrefining.com/data/articles/file/791692227.pdf
11. P+ portfolio, UOP CCR Platforming: https://portfolio-pplus.com/Technologies/Details/280
12. Oil & Gas Journal, Valero Louisiana projects (2019): https://www.ogj.com/home/article/17222741/valero-energy-invests-in-louisiana-refining-projects
13. Penn State FSC 432, delayed coking: https://courses.ems.psu.edu/fsc432/node/699
14. Jechura, coking: https://people.mines.edu/jjechura/wp-content/uploads/sites/120/2019/02/CBEN409_06_Coking.pdf
15. Lubes'n'Greases, base oils break-even (2014): https://www.lubesngreases.com/magazine/18_10/base-oils-break-even-puzzle/
16. Lotos, HBO base-oil project (2021): https://lotos.pl/en/1031/n,5310/hbo_project_moves_on_to_execution
17. Eurobitume, bitumen production: https://www.eurobitume.eu/feature-production/
18. Asphalt Institute and Eurobitume, IS-230 (2024): https://www.asphaltinstitute.org/download/47275
19. EIA, Refinery Capacity Report, table 8 (2026): https://www.eia.gov/petroleum/refinerycapacity/table8.pdf
20. EIA, Today in Energy 67807 (2026): https://www.eia.gov/todayinenergy/detail.php?id=67807
21. Oil & Gas Journal, complexity index (1996): https://ogj.com/home/article/17234421/refining-report-complexity-index-indicates-refinery-capability-value
22. Wikipedia, Nelson complexity index: https://en.wikipedia.org/wiki/Nelson_complexity_index
23. Hydrocarbon Processing, Dangote (2024): https://hydrocarbonprocessing.com/news/2024/06/nigerias-dangote-owes-27-b-for-refinery-construction/
24. Expansión, Dos Bocas costs (2025): https://expansion.mx/empresas/2025/07/17/dos-bocas-entre-las-refinerias-mas-caras-del-mundo-por-sobrecosto
25. Chevron, Pascagoula fact sheet (2019): https://pascagoula.chevron.com/-/media/pascagoula/about/documents/refinery-2019-fact-sheet-final.pdf
26. Hydrocarbon Processing, LyondellBasell Houston (2025): https://www.hydrocarbonprocessing.com/news/2025/02/lyondellbasell-to-lay-off-86-of-workforce-at-shuttered-houston-refinery-filing-shows/
27. KQED, Valero Benicia (2025): https://www.kqed.org/news/12036695/shocking-news-valero-announces-plans-to-end-operations-at-benicia-refinery
28. EIA, annual spot prices: https://www.eia.gov/dnav/pet/pet_pri_spt_s1_a.htm
29. EIA, monthly Brent spot: https://www.eia.gov/dnav/pet/hist/LeafHandler.ashx?n=PET&s=RBRTE&f=M
31. Argus Asia-Pacific Products sample (Feb 2025): https://view.argusmedia.com/rs/584-BUW-606/images/PRO-Argus-Asia-Pacific-Products.pdf?version=0
32. Ship & Bunker, Singapore 2025: https://shipandbunker.com/news/apac/456201-analysis-singapore-annual-bunker-sales-reach-new-record-high-in-2025
33. Ship & Bunker, Singapore prices (2026): https://shipandbunker.com/prices/apac/sg/sg-sin-singapore
34. Energy Argus Petroleum Coke sample (Aug 2025): https://view.argusmedia.com/rs/584-BUW-606/images/GEN-%20Sample-Report-Energy-Argus-Petroleum-Coke-%282025-08-13%29.pdf
35. Argus, US Group II base oil margins (2026): https://www.argusmedia.com/en/news-and-insights/latest-market-news/2774259-us-2025-group-ii-base-oil-margins-over-fuels-tighten
36. Indiana DOT, PG asphalt binder index: https://www.in.gov/indot/doing-business-with-indot/files/PGAsphaltBinderIndex.pdf
37. EIA, refinery yield: https://www.eia.gov/dnav/pet/pet_pnp_pct_dc_nus_pct_a.htm
38. Engineering ToolBox, crude oil yield structure by density: https://engineeringtoolbox.com/crude-oil-petroleum-specific-gravity-density-yield-structure-gasoil-VGO-diesel-naphtha-residue-d_1970.html

**Wells [W]**
1. EIA and IHS, Trends in US Oil and Gas Upstream Costs (2016): https://www.eia.gov/analysis/studies/drilling/pdf/upstream.pdf
2. EIA, crude oil well costs: https://www.eia.gov/dnav/pet/pet_crd_wellcost_s1_a.htm
3. EIA, Well Distribution FAQ 2024: https://www.eia.gov/petroleum/wells/pdf/WDR2024_FAQ.pdf
4. API, with Rystad data: https://www.api.org/energy-insights/charts-analysis/continued-investment-is-needed
5. EIA, Eagle Ford decline: https://www.eia.gov/todayinenergy/detail.php?id=18171
6. IEA, The Implications of Oil and Gas Field Decline Rates (2025): https://iea.blob.core.windows.net/assets/9ea2076e-5a0d-4a0d-9767-a1eec20aff23/TheImplicationsofOilandGasFieldDeclineRates.pdf
7. EIA, AEO2020 decline curves: https://www.eia.gov/analysis/drilling/curve_analysis/2020/
8. USGS, WLCI fact sheet 7: https://pubs.usgs.gov/wlci/fs/7/pdf/wlci7.pdf
10. EIA, pad drilling (2012): https://www.eia.gov/todayinenergy/detail.php?id=7910
12. MSETC, Pennsylvania workforce assessment (2011): https://www.e-education.psu.edu/earth109/sites/www.e-education.psu.edu.earth109/files/Lesson_05/Files/PennsylvaniaStatewideWorkforceAssessmentv1_Final.pdf
13. EIA, NEMS Oil and Gas Supply Module (2023): https://www.eia.gov/outlooks/aeo/nems/documentation/ogsm/pdf/m063(2023).pdf
14. BOEM 2019-023 (Kaiser, LSU): https://www.boem.gov/oil-gas-energy/boem-2019-023
15. BOEM, Deepwater Gulf of Mexico (2016): https://boem.gov/Deepwater-Gulf-of-Mexico-Report-2014
16. Planète Énergies, field life cycle: https://www.planete-energies.com/en/medias/close/life-cycle-oil-and-gas-fields
17. Norsk Petroleum, Edvard Grieg: https://norskpetroleum.no/en/facts/field/edvard-grieg
19. Offshore Magazine, Gulf shelf structures (2018): https://offshore-mag.com/production/article/16762214/review-of-shallow-water-gom-structure-inventory-offers-preview-of-decommissioning-requirements
21. Oil & Gas Journal, Payara: https://www.ogj.com/exploration-development/article/14184506/exxonmobil-moves-ahead-with-payara-development-offshore-guyana
22. Baird Maritime, FPSO Prosperity: https://www.bairdmaritime.com/amp/story/offshore/refining-processing/offshore-processing-storage/vessel-review-prosperity-exxonmobil-guyana-expands-operations-with-third-fpso
24. JPT, Johan Castberg: https://jpt.spe.org/johan-castberg-fpso-reaches-peak-production
27. SPE OGF, Liza Destiny: https://pubs.spe.org/en/ogf/ogf-article-detail/?art=6706
29. NSTA, 2024 unit operating cost: https://www.nstauthority.co.uk/news-publications/2024-benchmarking-dashboards-production-efficiency-and-unit-operating-cost/
31. SBM Offshore, Liza Unity: https://sbmoffshore.com/newsroom/sbm-offshore-completes-us114-billion-financing-liza-unity
32. 33 CFR 147.15, safety zones: https://www.law.cornell.edu/cfr/text/33/147.15
33. UNCLOS, article 60: https://www.un.org/depts/los/convention_agreements/texts/unclos/part5.htm
34. BOEM, Deepwater Gulf of Mexico 2019, OCS Report BOEM 2021-005 (a US government work, public domain): https://www.boem.gov/sites/default/files/documents/about-boem/Deepwater-Gulf-of-Mexico-Report-2019.pdf

**Ports [P]**
1. Indian Ports Association, Haldia OT-2 feasibility (2016): https://environmentclearance.nic.in/writereaddata/modification/Amendment/Attach_file/010720162OM72HCSrevFeasibilityReportOT2.pdf
2. Maritime Gateway, JNPT liquid berth (2023): https://www.maritimegateway.com/bpcl-j-m-baxi-jsw-infra-vie-for-liquid-cargo-berth-at-j-n-port/
3. IEEFA, WICET (2014): https://ieefa.org/sites/default/files/resources/IEEFA-Briefing-Note_WICET_May-2014.pdf
5. Pilbara Ports, Port Hedland fact sheet: https://www.pilbaraports.com.au/about-pilbara-ports/publications/forms-and-publications/forms-and-publications/other/2024/november/port-of-port-hedland-fact-sheet
6. JICA, Sihanoukville port study: https://openjicareport.jica.go.jp/pdf/12079786_08.pdf
7. Kuehne+Nagel, Ningbo-Zhoushan terminal (2024): https://mykn.kuehne-nagel.com/news/article/ningbozhoushan-adding-2m-teu-capacity-with-92-24-Sep-2024
8. Valenciaport Foundation, terminal capacity manual: https://www.fundacion.valenciaport.com/wp-content/uploads/2020/06/Capacity_EN-1.pdf
9. Splash247, Paradip multipurpose berth (2015): https://splash247.com/?p=15079
10. JICA, citing UNCTAD port benchmarks: https://openjicareport.jica.go.jp/pdf/11691854_11.pdf
11. Tank News International, Kipevu (2022): https://tanknewsinternational.com/kipevu-oil-terminal-in-mombasa-kenya-completed/
12. Möwe Bay port study: https://www.klausdierks.com/Moewe_Bay/5.htm
13. Offshore Technology, HES Hartel (2017): https://www.offshore-technology.com/projects/hes-hartel-tank-terminal-rotterdam/
14. DBM Vircon, WICET: https://www.dbmvircon.com/?p=1382
15. Port of Rotterdam, container terminals (2021): https://www.portofrotterdam.com/sites/default/files/2021-06/container-terminals-and-depots-in-the-rotterdam-port-area.pdf
22. DBCT, history: https://www.dbct.com.au/history
25. Port of Felixstowe: https://en.wikipedia.org/wiki/Port_of_Felixstowe
26. UCLA Labor Center, Automation and Dockwork (2023): https://labor.ucla.edu/wp-content/uploads/2023/11/Automation-and-Dockwork-Report.pdf
27. Port Economics, Management and Policy, port labour: https://porteconomicsmanagement.org/pemp/contents/part3/port-labor
28. Port Economics, Management and Policy, cargo handling values: https://porteconomicsmanagement.org/intrinsic-cargo-handling-values/
30. Baltic Exchange, tanker report week 8 (2026): https://www.balticexchange.com/en/data-services/WeeklyRoundup/tanker/news/2026/tanker-report-week-8.html
31. EIA, tanker sizes (2014): https://www.eia.gov/todayinenergy/detail.php?id=17991
32. MAN ES, MR tanker propulsion (2019): https://www.man-es.com/docs/default-source/document-sync/propulsion-of-46-000-50-000-dwt-mr-tankers-eng.pdf
33. UNCTAD, Review of Maritime Transport 2022, chapter 4: https://unctad.org/node/39790
35. JODI, volume-to-mass conversion factors (2017): https://www.jodidata.org/_resources/files/events/joint-apec-jodi-training-workshop-on-oil-and-gas-statistics/04.-conversion-factors-volume-to-mass-and-vice-versa.pdf
36. CE Delft, regulating speed (2021): https://ce.nl/wp-content/uploads/2021/03/CE_Delft_7L90_Regulating_speed_DEF.pdf
37. Port Economics, Management and Policy, bulk ship classes: https://porteconomicsmanagement.org/bulk-ship-classes/
38. Baltic Exchange, BHSI 38 benchmark vessel: https://www.balticexchange.com/content/dam/balticexchange/consumer/data-services-/historicalcircular_docs/BHSI_38_benchmarkvessel+routes_4917.pdf
39. Baltic Exchange, BSI 63 (2023): https://www.balticexchange.com/en/data-services/Circulars/market-announcements-/category-a/2023/circular-09-23---new-index--bsi-63-500dwt.html
40. Baltic Exchange, P7 (2019): https://www.balticexchange.com/en/data-services/Circulars/market-announcements-/category-a/2019/circular-45-19---panamax-grain-route-p7-commencing-live-reportin.html
41. MAN ES, bulk carrier propulsion (2022): https://man-es.com/docs/default-source/marine/tools/propulsion-trends-in-bulk-carriers.pdf
42. Baltic Exchange, BCI routes (2024): https://www.balticexchange.com/en/data-services/Circulars/market-announcements-/category-a/2024/circular--44-24-consultation--bci-route-descriptions.html
43. MAN ES, container vessel propulsion: https://man-es.com/docs/default-source/marine/tools/propulsion-trends-in-container-vessels.pdf
44. Taylor & Francis, TEU: https://taylorandfrancis.com/knowledge/Engineering_and_technology/Engineering_support_and_special_topics/TEU
45. FreshFruitPortal, Rotterdam 2024: https://www.freshfruitportal.com/news/2025/03/03/2024-cargo-throughput-in-the-port-of-rotterdam-slightly-decreased/
46. UNCTAD, Review of Maritime Transport 2021, chapter 4: https://unctad.org/node/35551

**Storage, pipes and ships [S]**
1. Oilsands Magazine, Edmonton's Base Line Terminal (2018): https://www.oilsandsmagazine.com/news/2018/1/16/edmonton-crude-storage-45-million-barrels-base-line-terminal-up-and-running
2. Oil & Gas Journal, pipeline construction costs reach a record $12.1M a mile (2025): https://www.ogj.com/pipelines-transportation/pipelines/article/55322093/pipeline-construction-costs-reach-record-121-million-mile
3. Hellenic Shipping News, tanker newbuilding prices up 50% since 2020 (2024): https://www.hellenicshippingnews.com/?p=1029854

**Wells, added**
18. Norsk Petroleum, Johan Sverdrup (exports by pipeline): https://norskpetroleum.no/en/facts/field/johan-sverdrup/
23. Sodir, Johan Castberg (shuttle tankers): https://factpages.sodir.no/en/field/pageview/all/32017325

*Numbering follows the research passes, so a few numbers are unused.*
