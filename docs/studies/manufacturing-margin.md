# Manufacturing's and Automotive's margins (MA1, 2026-11-17)

A study, no model change. CU2 chained the world's prices, and its notes read
the default playtest's kept city (seed 0, m2400) exporting $1.64B a month of
cars and $0.75B of fabricated steel, 70% of its exports and 41% of GDP
(spec-world-prices' ★8). This is the question CU1 asked of copper, asked of
these two sectors: what a works there buys, pays and keeps, against the 2022
Economic Census and BEA's input-output use tables, why the game differs, what
would bring each in line, and what the city lives on then.

**WHY.** CU1 found the old city's wealth was a pricing artifact (rod priced
apart from its cathode), and CU2 mended it; the city moved onto fabricated
steel and cars. Whether that is the next artifact or the right answer decides
what an old city earns, and every sector of the city is paid from it.

**In plain words, for Jerus.**

- **The two still carry the city on 0.9.138**, together 52-76% of its exports
  and 42-51% of GDP on four cities. Manufacturing is the larger now, the
  Fabrication Works (and on most seeds two hundred Machine Works) ahead of the
  car plants.
- **The goods the works buy are right.** A Fabrication Works' steel is 38% of
  its sales at the literals' prices, a structural fabricator's 24-39%; a car's
  metal and machinery 26%, the industry's 21-23%. Their wages are right too:
  $49-62 thousand a worker a year, the census's $62 thousand for fabricated
  metal.
- **What is missing is everything else a works buys.** A real fabricator buys
  about half its sales from other firms, a machinery or car maker 55-67%; the
  game's buy 8-43%, and nothing else, so they keep 21-41% of their sales
  where the real industries keep 13-19%.
- **And fabricated steel's world price runs high.** Its band at the city's
  site stands 1.1-1.8 times what its steel and its conversion make it, most
  years since m360 on the default path; a car's 0.67-0.77 of its metal since
  m1560. The
  fabricator's margin is wider than its literals give it, the car plant's
  narrower.
- **The Machine Works has three times the real crew** for what it sells
  (posts a sale), and once it buys its other purchases it does not pay.
- **And a works costs two to four times the real to build:** 0.6-1.2 years
  of its sales, where a real industry's plant at cost is 0.3-0.4. With real
  purchases and today's build cost, two cities of three never pass 41,000
  people.
- **With everything in line, the two still carry the city that takes off**
  (section 6): two cities of three, at the census's margin while the price
  holds, as rich a head as main's or richer. The third stays at about 40,000
  people.
- **The study recommends two batches, in order, and a study if needed**
  (section 7): MA2 holds a manufactured good's world price at the city to its
  chain (its inputs and its own part); MA3 then brings each works' books in
  line, its other purchases, a build cost at its industry's assets and the
  Machine Works' crew at a machine shop's; MA4 asks what starts a city on real
  margins, if MA3's ensemble finds more cities that never take off. Five
  calls (★1-★5), each with a recommended way.

## 1. How it was measured

- **The city runs:** `LongPlaytest` itself, with one line added to a copy
  after `g.simulateMonths(1)` calling `MarginProbe.month(g)`
  (`manufacturing-margin/run.sh`, LS1's and CU1's way); the tree is not
  touched and every city lives in a `TempDirs` folder. Main at 0.9.138
  (886bd92d): seeds 0, 1 and 2, and seed 0 on the Historical path
  (`-Dplaytest.world=HISTORICAL`), each to m2400. Seeds 0-2 share one world
  path (the seed moves only the founding).
- **The probe** (`MarginProbe.java`) sums a year of every sector's
  `SectorBooks` months and its exports at the export price, each metal good's
  prices and tonnes and who made and bought them, and each January reads every
  metal good's band with nothing traded (`WorldMarket.band(good, 0, 0)`, the
  world's own price at the city's site) and, for each Manufacturing and
  Automotive works, what the planner weighs (`Sector.estimatedMonthlyProfit`
  over `BusinessInvestment.getCostOf`). `table.py` prints its tables, in 2025
  US dollars where a price is compared with a literal (a price over the
  exchange factor and `PriceLevel.HISTORICAL_FACTOR`, 0.02588).
- **The census:** the 2022 Economic Census's manufacturing tables
  (`EC2231BASIC`, `EC2231MATFUEL`; `census.py`, its output kept as
  `census-2022-basic.csv` and `census-2022-materials.csv`): shipments, the
  cost of materials, payroll, benefits, other operating expenses, rent, value
  added and employees, for the United States, by industry; and the materials
  consumed by kind.
- **BEA's use tables:** the summary use table before redefinitions at
  producers' prices, 1997-2023 (`bea_use.py` reads the workbook;
  `report.py` prints `bea-use.md` from `bea-use-summary.csv`, the three
  industries' rows in five years). For fabricated metal (332), machinery (333)
  and motor vehicles, bodies and trailers, and parts (3361MV), over the
  industry's output **net of what it buys from itself** (a game works makes
  its own parts: a car plant here is the assembler and its suppliers in one),
  what it buys outside, split into the goods the game's works buy, what the
  game books on a line of its own (power and water, repairs, freight, rent,
  finance), and the rest; and compensation, taxes on production and the
  operating surplus.
- **The literals** (`literal.py`): each works' month at nameplate at the
  literals' mid-band prices and at the exporter's corner (its good at the
  export side, what it buys at the import side), payroll at $60 thousand a
  post a year.
- **The prototypes** (section 6): `proto.py` writes a `buildings.json` with
  consumables on the eight works and a `Sector.java` whose planner weighs them;
  `hold.py` a `WorldMarket.java` that holds a made good's band to its chain.
  `probes.md` lists every run and its table.

## 2. What carries the city

The year to m2400 on main 0.9.138, each sector's exports at the export price
(`table.py`, `probes.md`):

| city | people | exports / GDP | Automotive, of exports | Manufacturing | the two, of exports | of GDP | next |
|---|---|---|---|---|---|---|---|
| seed 0 | 916,962 | 67.4% | 21.6% | 40.9% | 62.5% | 41.9% | Business Services 19.6%, Copper Refining 10.0% |
| seed 1 | 293,135 | 85.6% | 13.3% | 39.0% | 52.3% | 44.8% | Business Services 20.5%, Copper Refining 11.1% |
| seed 2 | 866,794 | 67.7% | 33.8% | 42.0% | 75.9% | 51.2% | Business Services 20.2% |
| seed 0, Historical | 683,854 | 65.5% | 31.4% | 32.7% | 64.1% | 41.7% | Business Services 22.2% |

- **CU2's 41% of GDP holds; its split does not.** CU2 read one month of a
  kept city on its own model (dce27a4a), off the screen, cars 47% of exports
  and fabricated steel 22%. A year on 0.9.138 has Manufacturing ahead on every
  city but the Historical one. Seed 0 at m2400 stands 148 Fabrication Works,
  6 Fabrication Shops, 230 Machine Works and one Wire & Cable Works, and 45
  Assembly Plants, a Vehicle Works and a Heavy Truck Plant. Its year's exports are D$6.48B on a GDP
  of D$9.61B (the city's own money; CU2's dollars were a screen's, and the
  shares are what compare).
- **What the city sells is fabricated steel, cars and machinery, made of
  imported steel.** Seed 0 imports 62% of the steel it uses in the year;
  Manufacturing sells 79% of its sales abroad and Automotive 91%.
- **Before m1800 it is Manufacturing alone.** The car plants come late: on
  seed 0 one Vehicle Works stands from m600 to m1800, and the 45 Assembly
  Plants are built after m1800.

## 3. The books against the census

Each sector's year to m2400 over its sales, on the four cities, against the
census (over shipments) and BEA (over output net of what the industry buys
from itself, 2017-2023):

| | the game's Manufacturing | fabricated metal (332) | machinery (333) | the game's Automotive | motor vehicles (3361) | vehicles and parts (3361MV, BEA) |
|---|---|---|---|---|---|---|
| goods bought | 17.5-28.5% | materials 47.6%; bought outside 50-56% | 52.7%; 52-59% | 42.3-43.7% | 76.1% | bought outside 62.5-69% |
| of them the goods the game models | all of it | primary metals 22-26% (BEA) | 11-13% | all of it | | 21-23% (primary and fabricated metal, machinery) |
| payroll | 22.9-42.7% | pay and benefits 23.8%; compensation 29-32% | 22.2%; 26-30% | 19.5-25.1% | 7.0% (parts makers 15.9%) | compensation 17-21% |
| power and water | 0.0% | electricity 0.8%, fuels 0.3% | 0.5%, 0.2% | 0.0% | 0.2%, 0.1% | |
| upkeep and property tax | 2.0-3.0% | | | 3.1-4.8% | | |
| what it keeps | net 25-41% | cash margin 20.4%; operating surplus 13.5-17% | 17.0%; 11.5-19% | net 21-29% | 13.4% | 13-19% |
| sales a worker a year, 2025 US$ | $133-218k | $327k | $417k | $219-265k | $1.55M | about $510k, the three together |
| payroll a worker a year | $50-62k | $62k | $72k | $49-61k | $77k | |

- **What it keeps** is the game's net after interest and tax (its tax was
  0-11.5% of sales, its interest 0-3.7%); the census's cash margin is
  shipments less materials, payroll, benefits, other operating expenses and
  rent, before depreciation, interest and tax; BEA's operating surplus is
  before depreciation too. The game keeps half again to three times as much.
- **"About $510 thousand"** is BEA's 3361MV output net of its own purchases
  (US$513 billion in 2022) over the census's employees of the three
  industries it covers (1.00 million: 3361 252 thousand, 3362 177 thousand,
  3363 575 thousand).

**The works, as the planner weighed them each January** (seed 0, m2400;
`probes.md` has every city): the Fabrication Works' steel and lubricants were
23.2% of its sales at the month's prices and its wages 16.2%; the Machine
Works' 9.5% and 54.9%; the Assembly Plant's 41.3% and 21.6%. The planner's
score (a month's profit over the build cost) put the Fabrication Works first,
0.031, then the Machine Works, 0.019, and the Assembly Plant, 0.016; on seeds 1
and 2 the same order.

## 4. Why they differ

**The goods the works buy are the industries' own.** At the literals'
mid-band prices (`literal.py`):

| works | its goods, of its sales | the real | source |
|---|---|---|---|
| Fabrication Works and Shop | steel 38% (44% on imported steel at the export floor) | a structural fabricator's steel 24-39% of shipments; fabricated metal's primary metals 22-26% | census 332312 materials by kind: steel US$13.5B of US$55.5B, and US$9.8B "not specified by kind" (the upper figure counts it as steel in the specified kinds' proportion); BEA 332 |
| Machine Works | steel 8% | machinery's primary metals 11-13% | BEA 333 |
| Assembly Plant, Vehicle Works | fabricated steel 15%, machinery 11%: 26% | 21-23% | BEA 3361MV's 331, 332 and 333 |
| Commercial Vehicle Plant, Locomotive Works, Heavy Truck Plant | 17-21% | the same | |

**And their wages are near real.** The game paid a Manufacturing worker $50-62
thousand a year in 2025 dollars at m2400 (an Automotive one $49-61 thousand);
the census's fabricated metal $62 thousand, machinery $72 thousand, cars $77
thousand. A works' payroll share then depends on its posts:

| works | posts | sales a post a year (mid-band) | payroll at $60k | the real |
|---|---|---|---|---|
| Fabrication Works | 276 | $202k | 29.8% | fabricated metal $327k, structural fabricators $508k; compensation 29-32% (BEA) |
| Fabrication Shop | 135 | $165k | 36.4% | |
| Machine Works | 173 | $134k ($101k at the export floor) | 44.7% (59.3%) | machinery $417k, metalworking machinery $253k, machine shops $197k; compensation 26-30%, machine shops' pay and benefits 37.4% |
| Assembly Plant | 700 | $320k | 18.8% | about $510k; compensation 17-21% |
| Vehicle Works | 170 | $316k | 19.0% | |
| Commercial Vehicle Plant, Locomotive Works, Heavy Truck Plant | 42-330 | $356-372k | 16-17% | |

The Fabrication Works' and the car plants' payrolls are the industries'. The
Machine Works' is not: it sells a third of what a machinery maker's worker
sells and two thirds of a machine shop's, and its wages are 45-59% of its
sales. `ManufacturingCheck` holds that on purpose ("the machine works is a
fifth steel and half wages", its section "the three plants"): a design call
(★2).

**What is missing is everything else.** A real works buys more than its metal:
parts, paint, gases and tooling, packaging, services, repairs, freight. BEA
splits a works' purchases outside its own industry into the goods the game
models, what the game books on a line of its own, and the rest
(`bea-use.md`):

| industry | bought outside | the game's goods | booked elsewhere in the game | no line in the game | compensation | operating surplus |
|---|---|---|---|---|---|---|
| fabricated metal (332) | 50-56% | 22-26% | 6.6-7.4% | 21.5-24.1% | 29-32% | 13.5-17% |
| machinery (333) | 52-59% | 11-13% | 4.7-5.5% | 35.5-41.5% | 26-30% | 11.5-19% |
| vehicles and parts (3361MV) | 62.5-69% | 21-23% | 5.2-7.2% | 35.6-39.8% | 17-21% | 13-19% |

(1997-2023; each industry over its output net of its own purchases.) The
game's works buy none of the last column. The Wire & Cable Works alone has
consumables (3,176 thousand dollars a month at nameplate, 0.7.133;
`Sector.buyConsumables`), and nothing else in the two sectors does.

**Fabricated steel's band floats above its chain.** CU2 prices a made good
at its inputs' cost to each nation plus its own part, the own part solved on
its own market with its makers' mean held to the manufactures index's level
(`WorldMarket.solveYear`); only a root is anchored at the city's site. The
band the city meets is the cheapest landed makers', not the mean, and for
fabricated steel it stands above its chain (`table.py ratios`, the band with
nothing traded over its literal, over the same of its inputs):

| default path | m360 | m600 | m960 | m1200 | m1560 | m1800 | m2040 | m2280 | m2400 |
|---|---|---|---|---|---|---|---|---|---|
| fabricated steel over steel, of the literals' ratio | 1.51 | 1.11 | 1.57 | 1.20 | 1.30 | 1.53 | 1.53 | 1.56 | 1.31 |
| a car over its metal (3.5 t fabricated, 0.4 t machinery) | 0.83 | 1.03 | 0.76 | 0.93 | 0.77 | 0.76 | 0.67 | 0.67 | 0.74 |

On the Historical path the first runs 1.53-1.64 at m600-1200, 0.93 at m1800
and 1.42 at m2400. Steel itself stands at 0.8-1.3 of its literal, machinery
and cars at 0.9-1.2. So the Fabrication Works' steel and lubricants were 22-23%
of its sales at m1800-2400 on seed 0 where its literals give 38%, and an
Assembly Plant's metal and machinery 36-42% on the four cities where they
give 26%: the fabricator is the wider margin twice over, and the planner
builds it first. (CU2's study measured the Fabrication Works at 0.40-0.42 of
its sales over the drawn worlds' median at 2100 and 2200; on this one path the
own part wanders, a decade at a time, from 0.74 to 1.57.)

**Power is all but free.** Electricity and water are 0.0% of both sectors'
sales on every city, where the census has electricity 0.2-0.8% and fuels
0.1-0.3%. Too small to move a margin; the PW batches of spec-electricity own
the price.

## 5. The real margin

| | fabricated metal | machinery | cars |
|---|---|---|---|
| census 2022, cash margin over shipments | 332: 20.4%; 332312: 22.8%; 3323: 21.3% | 333: 17.0%; 3335: 11.9%; 332710 (machine shops): 15.6% | 336110: 13.5%; 3361: 13.4%; 3363 (parts): 10.6% |
| BEA, operating surplus over output net of own purchases, 1997-2023 | 13.5-17% | 11.5-19% | 13-19% |
| what the works buy, of their sales | census materials 47.6% (332), 54.5% (332312) | 52.7% (333), 36.6% (332710) | 76.1% (3361) |

**Sources.** U.S. Census Bureau, 2022 Economic Census, manufacturing
(EC2231BASIC, EC2231MATFUEL), and U.S. Bureau of Economic Analysis,
input-output use tables before redefinitions, summary 1997-2023: both US
public domain (17 U.S.C. 105; https://www.bea.gov/help/faq/145), so a batch
may ship a figure taken from them, credited "Source: U.S. Bureau of Economic
Analysis" or "Source: U.S. Census Bureau", implying no endorsement
(`licences.md`, two rows added by this study).

**So:** a real works keeps 13-23% of its sales before depreciation, interest
and tax, and buys half to three quarters of them. The game's goods are the
real works' metal, at real shares, and its wages real wages; it keeps 21-41%
because it buys nothing else, and the fabricator more because its good's price
floats above its chain. Against the literals, each works' month
(`literal.py`, payroll at $60 thousand a post, before upkeep, power and tax):

| works | left today, mid-band | at the exporter's corner | with its industry's purchases (way b), mid-band | corner |
|---|---|---|---|---|
| Fabrication Works | 32.1% | 25.9% | 24.2% | 18.1% |
| Fabrication Shop | 25.4% | 19.3% | 17.6% | 11.4% |
| Machine Works | 47.7% | 28.9% | 5.2% | -27.5% |
| Assembly Plant | 55.2% | 46.6% | 21.2% | 8.6% |
| Vehicle Works | 55.0% | 46.4% | 21.0% | 8.3% |
| Commercial Vehicle Plant | 63.0% | 60.7% | 23.9% | 21.5% |
| Locomotive Works | 65.8% | 63.5% | 23.2% | 20.9% |
| Heavy Truck Plant | 66.5% | 64.1% | 23.7% | 21.3% |

With its industry's purchases every works but the Machine Works stands where
the census's cash margin does (the corner is a city exporting at the floor
on imported inputs, the least a works can earn). The Machine Works needs its
crew too (★2).

## 6. The ways, played

Each change as a prototype on seeds 0-2 to m2400 (`proto.py`, `hold.py`;
`probes.md`): people at m2400, and the month the city first passes 60,000,
about where main's take off.

| run | what it changes | seed 0 | seed 1 | seed 2 |
|---|---|---|---|---|
| main | nothing | 916,962, m1692 | 293,135, m2112 | 866,794, m1812 |
| hold | the six manufactures' band held to their chain | 1,905,582, m1356 | 1,706,995, m1200 | 1,466,425, m1500 |
| full | way (a): BEA's "no line in the game" share as consumables | 967,835, m1776 | 1,463,976, m1512 | 637,841, m1860 |
| rule | way (b), the industry's purchases, and the hold | 40,008, never | 38,650, never | 1,570,417, m1560 |
| books | way (b), the census's build cost, the machine shop's crew; no hold | 386,322, m1800 | 38,383, never | 2,356,075, m1272 |
| cap | books and the hold: everything in line | 155,830, m2124 | 41,662, never | 1,087,743, m1500 |

**The three changes to the books** (`proto.py rule OUT census crew` writes
them; the planner weighs the consumables in every run that has them, as it
weighs the Wire & Cable Works', by `SpreadPlanner.ownCosts`):

- **Way (b), the purchases.** Each works buys its industry's purchases: BEA's
  bought outside, less what the game books on a line of its own, less the
  goods its recipe buys at the literals' mid-band, as `consumables` at
  nameplate (2025 k$ a month). Way (a) is BEA's "no line in the game" column
  alone, 22% of the sales (fabricated metal) or 38% (machinery, vehicles),
  whatever the recipe already buys.

  | works (row) | consumables, of its sales at mid-band | k$ a month |
  |---|---|---|
  | Fabrication Shop (49), Fabrication Works (51) | 7.9% | 145.7, 364.1 |
  | Machine Works (50) | 42.5% | 822.2 |
  | Vehicle Works (65), Assembly Plant (66) | 34.0% | 1,520.7, 6,336.4 |
  | Commercial Vehicle Plant (67) | 39.2% | 4,004.8 |
  | Locomotive Works (68) | 42.6% | 3,923.7 |
  | Heavy Truck Plant (140) | 42.8% | 552.1 |

- **The census's build cost.** A works' cash cost, construction points and
  materials, scaled as one, to its industry's depreciable assets at the year's
  end (at what they cost when bought, `ASTEND`) a dollar of a year's
  shipments, fabricated metal 0.41, machinery 0.33, motor vehicles 0.29
  (`census-2022-basic.csv`), times its year's sales at mid-band; only ever
  down. Assets at cost are less than the plant would cost to build again; the
  works stand today at two to four times them.

  | works | build cost today, k$ | years of its sales | at the census's | years |
  |---|---|---|---|---|
  | Fabrication Shop | 22,260 | 1.00 | 9,059 | 0.41 |
  | Machine Works | 14,580 | 0.63 | 7,612 | 0.33 |
  | Fabrication Works | 55,690 | 1.00 | 22,648 | 0.41 |
  | Vehicle Works | 66,000 | 1.23 | 15,324 | 0.29 |
  | Assembly Plant | 221,000 | 0.99 | 63,849 | 0.29 |
  | Commercial Vehicle Plant | 132,000 | 1.08 | 34,978 | 0.29 |
  | Locomotive Works | 130,000 | 1.18 | 31,516 | 0.29 |
  | Heavy Truck Plant | 16,600 | 1.07 | 4,415 | 0.29 |

- **The machine shop's crew.** The Machine Works' 173 posts cut to what a
  machine shop's worker sells (US$197 thousand a year, census 332710): 117,
  no diploma 20, diploma 71, engineering 23, science 3, and its recipe
  changes' undone posts in proportion (571.1, 214.2 and 7.9, from 840, 315
  and 11.6).

**The hold** (`hold.py`): each January, after a made good is priced off its
inputs, the makers of the six goods Manufacturing and Automotive make are
moved as one, by a root's own anchor, until the good's band at the city's site
with nothing traded is its inputs' bands there by their shares plus its own
part on the manufactures index's level: CU2's chain, read where the city meets
it.

What the runs show:

- **The hold alone makes the old city richer, sooner.** Every seed passes
  60,000 people 300-900 months before main's and ends larger, 1.47-1.91
  million. A car held to its chain is worth 0.89-1.05 of its metal at the
  literals' ratio to m1440, where main's floats at 0.76-1.03, and with no
  other purchases the car plants keep 24-32% of their sales and Manufacturing
  48-62%.
- **Way (a) keeps the old margin.** A bill fixed in 2025 money is a small
  share of a fabricated steel whose band floats high and a large one of a
  machine or a car: Manufacturing still keeps 33% of its sales, its
  consumables 14%, while the Machine Works and the car plants close (0-2
  Machine Works and at most one Assembly Plant at m2400; Automotive's exports
  0.0% on every seed).
- **Real purchases at today's build cost stall the city** (rule). Two seeds
  of three never pass 41,000 people. A works that keeps 18-24% of its sales
  and cost a year of them earns 1.5-2% of its cost a month before upkeep and
  tax: the Fabrication Works' planner score at m2400 was 0.005-0.007, a
  quarter of main's 0.023-0.031.
- **With the census's build cost and the machine shop's crew, two cities of
  three take off** (books, cap). Seed 2 at m1272 without the hold, sooner
  than main's m1812, and at m1500 with it; seed 0 at m1800 and m2124. Seed 1,
  main's latest (m2112), never does: 38-42 thousand people at m2400 and a
  real GDP a head (a month's GDP a head over the price index) of 4.4-4.5,
  where main's is 7.5.
- **The car plants need the hold.** Under way (b) an Assembly Plant's
  purchases are 34% of its sales, and main's city sells 91% of its cars abroad
  at the export side, where the literals leave a plant 8.6% before upkeep and tax
  (section 5). With fabricated steel floating (books: a car 0.74 of its metal
  at m2400) Automotive lost 1.3-13.8% of its sales in the year to m2400; held
  (cap), it kept 2.8-3.0%.
- **Held and in line, Manufacturing keeps the census's margin.** On cap seed
  1, held throughout, Manufacturing kept 22.5% of its sales in the year to
  m2400, and on seed 0 23.7% to m1800, before its hold slipped; the census's
  cash margin is 20.4% (332), BEA's surplus 13.5-17%.
- **The prototype's hold slips once the city is large.** Held, fabricated
  steel's band stood at 1.28-1.41 of its literal at seven Januaries from m1320
  to m2400, main's at 1.33-1.79. Then the world's solve tips into a second
  branch, where fabricated steel alone leaves its chain: at m2160-2400 steel,
  machinery and cars stand within 0.03-0.11 of the held runs' and fabricated
  steel at 1.64-1.75. Every tipped run reads the same band from then on,
  whatever its city (fabricated steel over steel 1.64, 1.68, 1.74, 1.56 and
  1.65 at m2148-2196 on cap seed 0, 74 thousand people, and on cap seed 2 and
  rule seed 2, 660-980 thousand). It tipped in every run whose city took off,
  at 76-333 thousand people (hold seed 1 at m1344, seed 0 m1524, seed 2 m1896;
  cap seed 2 m1572; rule seed 2 m1596; cap seed 0 m2148), and in none that did
  not. Fabricated steel is the one held good with no import side
  (`Good.FABRICATED_STEEL`'s import literal is NaN), where MA2 starts. The
  slip follows the take-off and does not start it (cap seed 2 grew from 41 to
  70 thousand people at m1440-1524 with the ratio at 1.11-1.24), but it hands
  the Fabrication Works its old margin back: cap seed 2's Manufacturing kept
  48% to m2400.

**What the city lives on, in line.** The cities that take off still live on
the two. On cap's seeds 0 and 2 Manufacturing is 52% of the exports and
Automotive 17-30%, the two 57-67% of GDP; on books', 58-72% of the exports and
48-57% of GDP. They sell fabricated steel, machinery and cars made of imported
steel, as main's cities do, and a head they are as rich or richer (real GDP a
head: cap 11.0-11.1, books 7.3-10.5, main 9.5-9.7). A city that does not take
off lives on Business Services (rule seeds 0 and 1, books seed 1: 61-68% of
its exports) or on a few Fabrication Works (cap seed 1: Manufacturing 91%), at
38-42 thousand people and 4.4-5.1 a head. Real margins do not change what
carries the city; they change whether a city gets there, and when.

## 7. What to do, in order

### 7.1 The findings

Every real figure is the U.S. Census Bureau's (2022 Economic Census) or BEA's
(use tables), both US public domain (section 5).

| finding | the real | the game, 0.9.138 | where | batch |
|---|---|---|---|---|
| A works buys only its recipe | bought outside 50-56% of output net of its own purchases (332), 52-59% (333), 62.5-69% (3361MV), of which no line in the game 21.5-41.5% (BEA) | none: the eight works have no `consumables` | `buildings.json` rows 49-51, 65-68 and 140; `Sector.estimatedMonthlyProfit` (L3470) leaves consumables out, and `Manufacturing.estimatedMonthlyProfit` (L290) weighs only the Wire & Cable Works' | MA3, ★1 |
| So a works keeps two to three times the real | cash margin 20.4% (332), 17.0% (333), 13.4% (3361) (census); operating surplus 11.5-19% (BEA) | net 21-41% of its sales | the rows above and below | MA3 |
| Fabricated steel's band floats above its chain, and a car's below its metal | CU2's chain: the inputs by their shares, the own part on the level | fabricated steel 1.1-1.8 of the literals' ratio over steel; a car 0.67-1.03 of its metal | `WorldMarket.solveYear` (L1813), the made branch after `base.price` (L1854): only roots are anchored | MA2, ★4 |
| A works costs a year of its sales to build | depreciable assets at cost 0.41 of a year's shipments (332), 0.33 (333), 0.29 (3361) (census) | 0.63-1.23 years | the eight rows' `cashCost`, `constructionPoints` and `constructionMaterials`; `ManufacturingCheck` "the three plants" (each plant 1-4% a month on its cost: "the ground is the brake") | MA3, ★3 |
| The Machine Works' crew is three times a machinery maker's | sales a worker US$197 thousand (machine shops, 332710), US$417 thousand (machinery, 333) (census) | US$134 thousand at mid-band (173 posts) | row 50's `jobs` and `recipeChanges`; `ManufacturingCheck` "the machine works on the wage bill" | MA3, ★2 |
| Power is all but free | electricity 0.2-0.8% of shipments, fuels 0.1-0.3% (census) | 0.0% | spec-electricity's PW batches own it | none here |

### 7.2 The order, and why

1. **MA2, the hold, first.** Alone it leaves every seed taking off sooner
   (section 6), so main never stands between the two batches with a city that
   stalls; and MA3's rows are set against the literals, which a held band
   makes the city's prices. With fabricated steel floating, the same rows
   leave Manufacturing anywhere from 26% to 48% of its sales and the car
   plants losing money (books).
2. **MA3, the books, after MA2**, its four parts in one batch: purchases
   without the build cost stall two cities of three (rule), and the build cost
   without the purchases is the old margin on a cheaper plant.
3. **MA4, a study, after MA3,** only if MA3's ensemble has more cities under
   60,000 people at m2400 than main's (★5); otherwise it is closed unread.

MA2 and MA3 each move the trace, so each runs the ensemble on the PC.

### 7.3 MA2

**A manufactured good's band held to its chain** (after MA1; one builder).
In `WorldMarket.solveYear`'s made branch, after `Base.price`, move the makers
of FABRICATED_STEEL, MACHINERY, CARS, VANS, ROLLING_STOCK and TRUCKS as one,
by `Base.anchor`, until the good's band at the city's site with nothing
traded, over its literals', is its inputs' same ratios by their chain shares
plus its own part times the manufactures index's level over its literals'
(★4; `hold.py` is the prototype, its patch exact). First find why the
prototype's hold lets fabricated steel go once the world tips (section 6:
the one held good with no import side; from then every run reads the same
band, whatever its city) and hold on both branches, every
January of the default and Historical paths and of a drawn world. The harness,
a `WorldMarketCheck` section: each held good's band at the site is its chain's
within a tolerance every January to 2100 on the default and Historical paths,
and with a city exporting fabricated steel at hold seed 0's scale (a fixture
that causes it); a made good not held is priced as on main. `ManufacturingCheck`
and `CarCheck` price at the literals and stand. An ensemble. Not beside:
`WorldMarket.java`, `WorldChain.java`, `WorldMarketCheck.java`, or any batch
that moves the world's prices.

### 7.4 MA3

**Manufacturing's and Automotive's books in line** (after MA2; one builder).
`proto.py rule OUT census crew` writes every figure:

- the eight rows' `consumables`, way (b) (★1, the table in section 6);
- `Manufacturing.estimatedMonthlyProfit` weighs every works' consumables by
  `SpreadPlanner.ownCosts` (the Wire & Cable Works keeping its freight), and
  `Automotive` the same, by an override of its own;
- the eight rows' `cashCost`, `constructionPoints` and `constructionMaterials`
  at the census's assets (★3);
- row 50's crew at 117 posts and its `recipeChanges`' undone posts (★2);
- `Manufacturing.steelShare` and `costShare` (L170-182) count the
  consumables beside the steel, so the screen's cost share is the books'.

The harness: `ManufacturingCheck`'s "the three plants" re-derived on ★3 (each
works at about its industry's cash margin at the literals' mid-band, and above
nothing at the exporter's corner), "the machine works on the wage bill" at 117
posts, and "the two together are under one" with the consumables; `CarCheck`'s
costs; `BuildingDataCheck` as it stands. An ensemble, counting the cities under
60,000 people at m2400 against main's (★5). Not beside: `buildings.json` rows
49-51, 65-68 and 140, `sectors/Manufacturing.java`, `sectors/Automotive.java`,
`ManufacturingCheck.java`, `CarCheck.java`.

### 7.5 MA4

**What starts a city on real margins** (a study, after MA3; only if its
ensemble has more cities under 60,000 people at m2400 than main's, otherwise
closed unread). Cap seed 1 stays at 38-42 thousand people on Business
Services or a few Fabrication Works. What the city that stalls lacks (the
hurdle, its ground, the steel it imports, its wage), and what a real town of
40,000 sells, 1900-2100. No model change.

### 7.6 The calls for Jerus

Each in the Pending row MA1-K, recommended (a); MA2 and MA3 take the
recommended ways unless he says otherwise.

- **★1, a works' other purchases.** (a) Its industry's purchases less what
  its recipe buys (way b in sections 5 and 6), recommended: a works then buys
  what its industry buys in all, whatever share its recipe is. (b) BEA's "no
  line in the game" share alone (way a): it closes the Machine Works and the
  car plants and leaves the fabricator at 33%. (c) None, as today: every works
  keeps two to three times the real.
- **★2, the Machine Works' crew.** (a) A machine shop's, 117 posts,
  recommended: the least cut that keeps it paying with its purchases (cap:
  10-51 standing at m2400). (b) A machinery maker's, about 56 posts. (c) Keep
  173 posts and give it no purchases: `ManufacturingCheck`'s "half wages" as
  designed, keeping 48% of its sales at the literals' mid-band before upkeep
  and tax. (d) Keep 173 posts with the purchases: it closes (rule: 0-3
  standing).
- **★3, the build cost.** (a) The census's assets a sale, recommended:
  0.29-0.41 years of sales; the ground stays the brake for the land, and "the
  three plants" are re-derived. (b) Today's: with real purchases, two cities
  of three never take off (rule). (c) Between, 1.25-1.5 times the census's:
  assets at cost understate a plant built new; unplayed.
- **★4, which goods are held.** (a) The six manufactures, recommended: the
  goods measured here and found floating. (b) Every made good with an own part
  (copper rod, wire, cement, paper and the rest): the same mechanism,
  unmeasured (wire's band stood at 0.68-1.13 of its literals' in the cap runs).
- **★5, a city that never takes off.** (a) Ship MA3 and let its ensemble count
  them; if there are more than main's, MA4 asks why, recommended. (b) Hold MA3
  until MA4 has studied cap seed 1 on the prototype. (c) A build cost between
  today's and the census's, so that more take off: not in line.
