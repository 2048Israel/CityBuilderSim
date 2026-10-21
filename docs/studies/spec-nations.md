# Nations: the world as a market with places in it, a design study

*2026-10-20. A read-only design study: no game code was changed. Jerus, in this session: "right now each run results in one path, why? cause thats how it works if everything else is static, i thought it was a bug, its a feature, truth is its caused caus ethe outside world is static. now basically what im planning is to add sort of other nations ... basically they also grow, and basically its just nodes on teh map which you can see, and shown on teh leaderboard ... each nation has its productions and demands, and distance and routes, and basically, that creates a market ... it affects the price, for example say you spawn in nova scotia, canada is nearby so its imports (your exports) are cheap transport wise, but if canda only needs 10 tons of wood, more wood would have to be sold esle where say portugal ... map borders... how? and also if you buy land you are buying from the nation who has it, so basically there are mutliple currencies... and fx reserves is for each ... perhaps a seed for the path? one being historical and the other being random? ... or you think that no, cause for example if a city is big enough, it becomes the market? ... and also compute ingame... sketchy business". Then, on "197 x 197": "wtf? thats not good". He answered ten questions (§10) and said "do rec" for the rest.*

*What was read:*
- *The code at 2631c1c (main, 0.8.6), read and not changed: `Good` (the delivered prices, `Pricing`), `GoodsMarket` (the band and `strike()`), `Markets`, `WorldEconomy`, `ForeignAccounts` (the vault, `repriceCurrency()`, the land payments), `DebtManager`'s dollar paper, `LandMarket` (`price()`, the crowding premium), `CityCalendar`, `Founding` and `FoundingScreen`, `BoatSchedule`, `SeaRoutes`, `Gateways`, `EarthData` (the layers and the places), `HistorySave`, `MoneyAudit`'s scopes, `ui/TradeScreen`, `ui/MapView` (the expanded map, its head and legend), `ui/UserInterface`'s header and `ui/Palette`.*
- *The studies: `spec-leaderboards.md` (the peer list, the world files it planned), `spec-freight-and-infrastructure.md` (the legs, the gateways), `spec-earth.md`.*

*The figures marked "measured" are the prototype `nations/NationsModel.java`, run on this PC (Java 21) with its output in `nations/nations-model-output.txt`. The map board's ground was drawn by the game's own `EarthProj` and `TileRaster` colours from `earth-1.bin` (`nations/RenderWorld.java`). The boards are `nations/1-` to `4-*.html`. Figures marked (est.) are estimates; [R#] are the sources at the end.*

---

## 0. In one page

**Why every run is one path.** The world outside the city is a price list. Each good has an import price and an export price, fixed in US dollars at founding (`Good`), and the city can buy or sell any amount at them. The dollar's inflation is the only thing outside that moves, and its draws are seeded by the month alone, so every city of the same setting meets the same world (§1.4). Jerus was right: it is not a bug, it is a static world.

**The proposal in one sentence.** The world becomes **196 nations**, each with what it makes, what it uses, its money, its borders and its own path through the years. Each good's price in each nation comes out of one well-known calculation, the **spatial price equilibrium**. The city is one more node in it, so its import and export prices depend on where it stands and how much it trades.

**The market (§2, §4):**
- **One good at a time.** For each good, every nation has a supply and a demand. Freight between any two is known. The solve finds a price in every nation such that no route pays more than its freight. This is Samuelson's problem (1952) [R1] in its smooth form, where buyers spread their orders over many sellers, nearer ones more, as real trade does. That form is Wilson's gravity model [R3], solved by repeated scaling (Sinkhorn's) [R4].
- **Who trades with whom: Jerus's rule.** The top 20 economies trade with everyone. Every other nation trades with those 20, the good's top 10 exporters and its 10 nearest makers of that good. The city trades with all 196. That is about 5,900 pairs per good instead of about 15,700 (measured).
- **Once a year, every January** (Jerus's choice). The prototype's 50 goods solve in **about 50 ms** at a game's precision, the full table in about 90 ms (measured); the game trades 60. Prices on the rule are within 0.8% of the full table's on average and 4% at worst.
- **Every month, the city's step** (Jerus's choice) costs **under 10 microseconds** for every good (measured, 5 to 10 across runs). The city sells to the best-paying buyers first: the price there less the freight to get there. Each sale lowers that buyer's price by its slope until the next buyer pays as well. That is Jerus's "Canada only needs 10 tons, the rest goes to Portugal", done exactly.
- **The band becomes the world's.** The export floor is what the next tonne nets abroad. The import ceiling is what the next tonne costs landed from the cheapest seller. Both depend on where the city was founded and how much it trades.
- **The city becomes the market** by itself, as Jerus guessed. Selling a twentieth of the world's lumber nets about 8% less a cubic metre than a small city gets; a tenth, about 16% less (measured, §4.8). An ordinary city barely moves a price. What it feels most is **where it stands**.

**The moneys (§5).** Every nation prices in its own money at its own rate to the dollar, with about 145 moneys in all. The vault holds the **eight** that central banks hold: US$, euro, yen, pound, yuan, Canadian and Australian dollars, Swiss franc. Land is paid in the seller's money.

**Borders and land (§6).** Natural Earth's country borders [R10] are baked into a world file, drawn on the map with a toggle, and decide who sells each plot. A hectare costs what land costs in that nation, in its money. Canada is the reference, so Halifax cities pay what they pay today.

**The path (§7).** Picked at founding. **Historical** replays the real world from January 2000: each nation's output, people and money, and the real price of each good against the others. It then follows the UN's projection of people and the game's own rule for output, with small shocks drawn from a seed. **Random** starts from the same January 2000 and draws its own path from the seed, at the sizes the real world's booms and slumps have had. Both are seeded, so a run still repeats exactly.

**The screens (§8).** On the expanded map: nations as dots sized by output, with name, output and people on their land. Each one opens a card on a click. This month's trade is drawn as routes, the borders are dashed, and Nations, Trade routes and Borders each have a toggle. The map's head carries the header's POPULATION and GDP tiles, and the legend folds. Trade gets a **Partners** page: who buys what, and where the next tonne goes. The reserves page holds the vault in eight. Founding gets a **World** step.

**Batches (§9):** **WN0** the world file; **WN1** the nations and their path; **WN2** the map; **WN3** the market (every trace moves); **WN4** the moneys; **WN5** land from its nation; **WN6** the screens; **WN7** ships to their partners. All are queued: Jerus answered the large calls and said "do rec" for the rest.

---

## 1. What the game does today

### 1.1 One price list for the whole world

- **A good's two world prices are literals**, stored as the price delivered: what an import costs landed here, and what an export fetches delivered there. They are in thousands of world dollars a unit: iron ore .115 to import and .085 to export (US$115 and US$85 a tonne); lumber .340 and .250 a m³; crude .60 and .50 a tonne (`Good.java:276, 1080, 753`). The world's own price is derived from them, with the freight taken off or added back (`Good.java:1205, 1216`), and the freight is "3/4 of the wedge", a game decision.
- **63 of the 68 goods clear in a band** between the export floor and the import ceiling (`Pricing.BAND`). Five are seller-priced: groceries, housing, building work, luxury trade, meals. Four can only be imported (vans, rolling stock, luxuries, trucks) and four only exported (support, back-office and engineering work, fabricated steel).
- **The band is fixed for the month.** `GoodsMarket.strike()` puts the local price between floor and ceiling by scarcity (`GoodsMarket.java:314-335`), and quantity moves only the position inside the band. **Imports and exports are unbounded at the edges**: a shortfall is imported at the ceiling and an unsold flow exported at the floor (`Markets.java:340, 449`; `Sector.java:2523`). No buyer abroad ever runs out of appetite.
- **Only two things move a band:** the exchange rate, times the world's price level (`Game.java:10071-10072`), and Freight's freight factor and rail charge (`sectors/Freight.java:812-816`).

### 1.2 One foreign money

- **The vault is US dollars** (`ForeignAccounts.reservesUsd`, `:954`), bought and sold by the treasury (`buyReserves()` `:1291`, `sellReserves()` `:1321`) and spent by the central bank defending the rate.
- **The rate** opens at 1.00 and moves on the trade's pressure, the expected drift and a pull to parity (`repriceCurrency()`, `:816-879`).
- **Dollar bonds, foreign deposits and shares held abroad** are all in US$. No other currency exists anywhere in the model.

### 1.3 Land, in US dollars

- **An offer's price** is its ground's area times US$0.70 a ft² (`LandMarket.BASE_PRICE_PER_SQ_FT`, about US$75,000 a hectare) times the world's price level times a crowding premium of 1 to 120 by the city's density. The resources under it are added at their export price (`LandMarket.java:424, 635-643`). Wet ground is priced at .45 of dry for fresh water and .08 for sea.
- **It is paid** by converting the treasury's cash at the rate, or from the vault when the player says so (`Game.java:1642-1665`). Nobody is the seller: the land comes from nowhere.

### 1.4 Why every run is one path

- **`WorldEconomy`** is the dollar's inflation. It is a weighted moving average of uniform draws around the founding chip's mean (0, 1, 2, 3.33 or 5%), and its level is pulled back to a flat trend (`WorldEconomy.java:263-286`).
- **Its draws are `new Random(scramble(SEED + month))`**: seeded by the month alone, not by the city's seed. Every city founded with the same chip meets the same dollar on the same month.
- **Nothing else outside moves.** So two runs of one city are identical, and ForeignCheck asserts it, over 90 months to 1e-6 (`ForeignCheck.java:389-412`). Two cities in different places differ only by their own ground.
- **Jerus's reading is the right one:** with a static outside, the path is a property of the city alone.

### 1.5 The Earth's places, and no borders

- **`earth-1.bin` has no country, border or admin layer** (`tools/earth/FORMAT.txt`; `EarthSite.java:29-30`: "never a country"). `earth-2.bin` adds terrain and no borders.
- **It has Natural Earth's 7,342 places**, each with its country's name, read for search, the founding card and the gateways' bearing (`Gateways.java:132-150`). The country field is parsed and read nowhere but EarthCheck.
- **The places name 228 countries and territories**, but not all 193 UN members: **Nauru has no place** (§11). The list of nations cannot be taken from the places.
- **The ground's finest layer is 240 cells a degree** (15", about 460 m); land cover is 30, the far view 15. A plot is 30 m.

### 1.6 What the leaderboard study planned

`spec-leaderboards.md` (2026-10-08, not built) chose the peer list this study takes (every UN member, plus Taiwan, Hong Kong and Macau) and planned three data files: `world-2025.json` (196 nations and about 60 city regions, 70 measures), `world-path.json` (people and GDP a head, 2000 to 2100) and `world-sources.json`, made by an offline builder. Its D1 recommended that **a founding game dollar is a 2025 dollar**: the game's prices were struck from 2025-26 figures though the calendar starts in January 2000. This study keeps that anchor (§7.1) and shares the paths (§3.3, D24). *(2026-10-21, the leaderboard's refresh: Jerus changed its D1 to "2015 so that its calibrated to the other": a founding game dollar counts as one real 2015 dollar, D29's base, while the game's prices stay as struck from 2025's; `spec-leaderboards.md` §0, §10.)*

---

## 2. The rule: the world is a market with places in it

**The problem has a name.** Given, for one good, each place's supply and demand and the cost of moving a tonne between any two places, find each place's price and each route's flow. Samuelson posed it in 1952 and showed it is a single optimisation [R1]; Takayama and Judge solved it with sloping supply and demand [R2]. The answer has one property that makes it a market:

- **No route pays more than its freight.** If wood costs 100 in Canada and 130 in Portugal and the freight is 25, someone buys in Canada and sells in Portugal until the gap is 25. Where a route carries anything, the gap is exactly its freight; where it carries nothing, the gap is less.

**The sharp form and the smooth form.** In the textbook form every buyer takes only its cheapest delivered source. Prices are exact, but trade is lumpy: Portugal buys all its wood from one country, and a price jumps when a route flips. Real trade is not like that. Every nation buys most goods from many sellers, nearer ones more, and the share falls smoothly with distance. That is the gravity model, the best-tested regularity in economics: across 1,467 estimates, a tenth more distance means about 9% less trade [R5][R6]. Wilson showed the gravity model is what the transport problem becomes when each buyer's choice has a spread, an entropy term [R3], and the same problem is solved by repeated scaling, Sinkhorn's iteration, which machine learning made fast and famous [R4]. **Jerus chose the smooth form** ("smooth buyers spread").

**In the smooth form, for each good:**
- every maker i has a mill price p_i and a supply S_i that rises with it;
- every buyer j has a price index P_j, the "soft minimum" of p_i plus the freight t_ij over its sellers, and a demand D_j that falls as P_j rises;
- j buys from each seller in proportion to exp(-(p_i + t_ij) / θ). θ is the spread: how much a buyer's sellers may differ in price and still each get some of its orders. A small θ is the sharp form; a large one ignores price.
- **The solve** raises or lowers each maker's p_i until its sales meet its supply. It repeats until every maker clears.

**Why one good at a time.** A nation's demand for lumber does not depend on the price of copper within a year, to any accuracy the game needs. So the world market is about 50 small problems, not one large one. Goods that feed each other (ore into steel) are linked through the nations' supply and demand from year to year, not inside a solve.

**The city is one more node.** It sells to and buys from the nations at their prices, less or plus the freight from where it stands. Its prices are therefore exact, and, as Jerus asked, a big city moves them (§4.8).

---

## 3. The nations

### 3.1 The list

**196** (Jerus: "All 196"): every UN member, plus Taiwan, Hong Kong and Macau, the leaderboard's peer list (§1.6).
- **Territories go to their sovereign** for land and money: Greenland's ground is sold by Denmark, Puerto Rico's by the United States. They keep their own place on the map as part of that nation.
- **Each nation's node** is its largest port city, or its largest city where it has no coast. That is where its trade is measured from. Its label sits on its land's centre in view (board 1).

### 3.2 What a nation carries

Per nation:
- its ISO code, name, sovereign, node, money and the money's regime (floating, pegged to the dollar or the euro, using another's), and for land a price a hectare (§6.3);
- **its path**, year by year: real output (GDP at 2025 prices), people, the change in its prices (CPI) and its money's rate to the dollar (§7).

Per nation and traded good:
- what it makes and what it uses in the base year, and how each grows with its output;
- its base-year trade with each of its partners (the rule, §4.2).

Each figure carries a mark of where it came from: ● published, ◐ a published projection, ○ the game's estimate. These are the leaderboard's three marks, with the same meaning.

### 3.3 The world file and its pipeline

- **One file, `world-1.bin`**, beside the Earth files in `data/`, read by a new `WorldData` as `EarthData` reads the Earth. A small fixture of a dozen nations sits in the resources for the harnesses, as `earth-1-fixture.bin` does. The release is Jerus's, as `earth-2.bin`'s was.
- **Made by an offline pipeline in `tools/world/`** (Python, as `tools/earth/`; the game never fetches anything) from public downloads whose licences let a game ship numbers made from them. Each is named with its licence in the file's meta:
  - **The nations, borders and nodes:** Natural Earth's admin-0 countries and boundaries at 1:10m, public domain. All 196 are there, matched on `ISO_A3_EH` [R10].
  - **Output, people, prices and moneys, 2000 to 2025:** the World Bank's WDI (CC BY 4.0) [R11]. It has no Taiwan, and no recent output for Cuba, Eritrea, North Korea, South Sudan, Syria or Yemen; for those the Penn World Table and the Maddison Project (both CC BY 4.0) [R33] or the UN's figures fill in, marked ○.
  - **People to 2100:** the UN's World Population Prospects 2024, medium variant [R13].
  - **Output after the data:** the game's own projection rule (§7.1), not the IMF's WEO. The WEO runs five years ahead, but its terms ask for the IMF's permission for any commercial reuse [R12].
  - **Trade by pair and good:** CEPII's BACI, HS6 for about 200 countries to 2024, reconciled from UN Comtrade under the Etalab 2.0 licence (Comtrade's own data may not be passed on) [R14]. Taiwan appears there only as "Asia, not elsewhere specified". A concordance written for the file maps each HS heading to one of the game's goods.
  - **What each nation makes:**
    - food and wood: FAOSTAT (CC BY 4.0, with FAO's own terms) [R15];
    - ores and metals, steel among them: the USGS's Mineral Commodity Summaries (public domain) [R16];
    - crude, products and coal: the EIA's international data (public domain) [R18].
    - Not used: worldsteel's and the British Geological Survey's tables are copyright, and the Energy Institute's terms are not open [R17][R18].
    - Where no maker's figure exists, what a nation makes is what it uses (estimated from its output) less what it imports, plus what it exports, marked ○.
  - **Real prices, 2000 to 2025:** the World Bank's Pink Sheet, monthly since 1960 (CC BY 4.0) [R19], over the US CPI.
  - **Sea distances between nations:** CERDI-seadistance (CC BY 4.0), port to port for 227 countries and territories [R20]. South Sudan is missing, and takes its road to its neighbours' ports, as the table does for every landlocked nation. Land neighbours are measured on the great circle times a detour.
- **Size (est.):** about 6 MB of borders and 4 MB of tables, against earth-1's 53 MB.

**The leaderboard reads the same file** (D24): its people and output are these, and its 70 measures stay in its own `world-2025.json`. Its refresh (2026-10-21) asks the file's real output per nation in constant 2015 US$, the WDI's own series, for its constant-rate rankings (`spec-leaderboards.md` §8, its D14); its Your world board ranks the ten measures the path carries (its D15).

---

## 4. The world market

### 4.1 One good at a time

- **The traded goods** are the 63 BAND goods less the three services (support, back-office and engineering work): 60. The prototype solves 50.
- **The services keep today's fixed band** (D17). Remote work has no freight and no nation-by-nation market the data can give.
- **The one-sided goods keep their side:** the city never exports a van or imports fabricated steel. The nations trade them among themselves all the same.

### 4.2 Who trades with whom: the partner rule

Jerus: "how about top 20 in gdp trade with all, the rest only with top 10 exporters and 10 nearest. your own city with all."

For each good, a pair (seller, buyer) is in the market when:
- either end is one of the **20 largest economies** that year; or
- the seller is one of the good's **10 largest net exporters**; or
- either is one of the other's **10 nearest makers of that good** (D11); or
- either end is **the city**.

The "nearest makers" reading is measured against "nearest nations" in the prototype (196 nations, 50 goods):

| partners | pairs a good | prices off the full table: mean | worst |
|---|---:|---:|---:|
| the full table | 15,703 | 0 | 0 |
| top 20, top 10 exporters, **10 nearest nations** | 4,972 | 1.16% | 8.47% |
| top 20, top 10 exporters, **10 nearest makers** | 5,855 | 0.81% | 4.11% |

A nation's nearest nations may make none of the good, so it loses its cheapest near supply. Its nearest makers cannot. The worst nation is a small one whose best seller is neither near, big nor a top exporter, and its error is how much dearer its second-best is.

### 4.3 The January solve

**Once a year** (Jerus: "World yearly, city monthly"), at the turn of the year, every traded good is solved with:
- each nation's supply and demand for that year (its path, §7);
- **the city's trade of the year just ended**, fixed, by partner. Its sales add to each buyer's supply and its purchases to each seller's demand, so the world takes in what the city did.

The solve starts from last January's answer (warm). It stops when every maker's sales meet its supply to 0.01%.

**Measured** (the prototype, Jerus's rule with nearest makers; 12 years of 3% growth with 2% and 4% noise):

| precision | a January, 50 goods | the full table | sweeps a good |
|---|---:|---:|---:|
| 0.1% | 24 ms | 45 ms | 31 |
| **0.01%** | **49 ms** | **89 ms** | **61** |
| 0.00001% | 121 ms | 222 ms | 154 |

From cold, as at founding, it takes about 250 sweeps. The file can carry the base year solved, so founding needs no cold solve. The solve runs once a year, between December's month and January's, and costs less than one of the game's months.

**The base year reproduces real trade.** With a spread alone, the model's flows would follow price and freight only. Real trade also follows language, history, deals and habit. So each kept pair gets a fixed preference, set by the pipeline so that the base year's solve gives BACI's flows (D18). The preference never moves after that: prices and freight move the trade, and the habits stay. This is how trade models in policy work start [R7].

**θ, the spread,** is each good's price divided by its trade elasticity (D18). Trade's elasticity to its cost is about 5 (Head and Mayer's preferred estimate, -5.03 [R6]); the Armington literature's median is 3.8 [R8]. Bulk commodities, near-identical from any seller, sit higher (est. 8 to 10), and manufactured goods near the median. The prototype used 8% of the price for every good.

### 4.4 The city's month: the slopes

Every month, for each good it trades, the city takes the January answer as given and does what Jerus described:

1. **For a sale:** each nation j nets the city its price index P_j less the freight t_cj from the city. Sort the nations by that netback.
2. **Fill from the top.** The city's tonnes go to the best netback first. Each tonne sold into j lowers P_j by j's slope: its own demand's response plus its other sellers' giving way. Once the best has fallen to the second's netback, both take tonnes, and so on.
3. **The city's export price** is where the fill stops: the netback every buyer it serves pays alike, for the last tonne. This is exact for straight slopes, one sort and one pass (about 200 steps a good).
4. **The world's answer within the year:** every nation's price also falls by the city's sales over the whole world's slope, one number a good. This anticipates what January will do.

**A purchase** is the same fill from the cheapest landed seller (its mill price plus the freight to the city), each tonne raising that seller's price by its slope.

**Measured, against a full re-solve with the city's tonnes in** (lumber shaped like the world's, the city at Halifax):

| the city sells, of world use | buyers | nets this month | with the world's answer | re-solved | off |
|---|---|---:|---:|---:|---:|
| 0.1% | US 89%, Dominican Republic 7%, Iceland 3% | 81.32 | 81.21 | 81.25 | 0.0% |
| 1% | US 98% | 81.03 | 79.93 | 79.97 | -0.1% |
| 5% | US 98%, Canada | 79.75 | 74.23 | 74.65 | -0.6% |
| 10% | US 93%, Canada 5%, Portugal | 78.32 | 67.28 | 68.46 | -1.7% |

The month's step lands within 2% of a re-solve even at a tenth of the world's lumber. Without the world's answer it would miss by up to 14%, so the world's slope is in the month (D4). **For all goods it takes under 10 microseconds a month** (measured).

**The wood case, plainly.** A Halifax city does not sell its lumber to Canada first. Canada saws more than it uses, so Canada's own price is low. Its first buyer is the United States, about 1,200 km by road and rail, whose price less that freight is the best there is. The US takes everything an ordinary city can saw. **Canada starts buying when the city sells 4.8% of the world's lumber, Portugal at 5.6%, Mexico at 10.3%** (measured). Jerus's "Portugal" is right: it is the third buyer, after the two neighbours.

### 4.5 The band, from the world

- **The export floor** is the netback of the next tonne: §4.4's fill at **last month's** exports of the good (D16). There is no iteration inside a month, so the market keeps its one-pass strike.
- **The import ceiling** is the landed cost of the next tonne from the cheapest seller, at last month's imports.
- **The floor is never above the ceiling.** P_j is a soft minimum, so it is no more than any seller's price plus its freight, and the freight table keeps the triangle rule (the sea distances are shortest paths). WN3's harness asserts it for every good, every month.
- **Calibration** (D12): **the game's literals stay the prices.** Each good's reference world price is set so that a Halifax city in 2025, selling and buying nothing yet, sees today's floor and ceiling. The rebalance's prices hold, and the data gives the nations' quantities, their changes and the geography. A city elsewhere sees a different band, by its freight to its buyers and sellers.
- **Freight's legs stay** (spec-freight §2): the band is "delivered at the city's gateway or berth", and Freight's city leg is billed on top, as today.

### 4.6 Freight between nations, and from the city

- **Between nations:** the sea distance of their nodes' ports (CERDI) [R20], or the land distance for neighbours, times a cost per tonne-km by the good's class. Ocean bulk costs about 0.1 to 0.2 US cents a tonne-km. Iron ore from Brazil to China, 21,500 km, cost US$21 a tonne in January 2026 and US$42 in September [R21][R20]. US rail costs about 3 cents and a truck 5 to 10 [R34]. A 40-foot box from Shanghai to Rotterdam cost US$3,522 in October 2026 [R35]: a tonne of machinery pays more per kilometre than a tonne of coal, but less as a share of its price. The sea carries 99% of world trade by weight; about a quarter by value crosses a land border, more in Europe and North America [R9].
- **From the city:** to each nation, from the city's own berth or gateway. By sea, the berth's offing (SeaRoutes) to the nearest node's port, then CERDI's table. By land, FR0's gateways and the great circle times a detour. Worked out once at founding and again when a berth or gateway is first built: 196 numbers a good class.
- **Handling:** a fixed cost per tonne for crossing a border (the port or the border post), so a nation's own makers keep an edge at home.

### 4.7 What it costs: measured

| | when | cost |
|---|---|---|
| the January solve, all goods, 0.01% | once a year | about 50 ms (90 ms on the full table) |
| the city's step, all goods | each month | 5 to 10 µs |
| the nations' year (output, people, prices, rates) | once a year | 196 x 4 numbers, nothing to speak of |
| the freight from the city | at founding, and on a new berth | about 200 great circles a class |
| memory | always | about 6,000 pairs x 50 goods, a few MB |

Jerus's worry ("compute ingame... sketchy business") is answered by the measurement: the world costs less a year than one of the game's months.

### 4.8 The city becomes the market

There is no special rule: it follows from §4.4. A city selling a small share of a good's world use moves its price by little. One selling a large share moves it a lot. Lumber, measured (re-solved, §4.4):

| the city sells, of the world's use | its netback, against a small city's |
|---|---:|
| 0.1% (a large mill town, about 37,000 m³ a month: FAO counts 445 Mm³ of sawnwood in 2023 [R15]) | -0.1% |
| 1% | -1.7% |
| 5% | -8% |
| 10% | -16% |

Goods with a thin world market move sooner: a city making 1% of the world's copper rod is a giant in a small pond. Every January the full solve takes the city's year in, so a city that floods a market finds the market lower the next year everywhere.

### 4.9 What does not change

- `GoodsMarket.strike()`: the local price is still struck in the band by scarcity.
- Freight's city leg, the berths and gateways, the ships' tonnes.
- The seller-priced goods, and the services' fixed band.
- The month's order: the January solve is one step at the turn of the year, before January's month starts, in both `Game.nextMonth()` and `SimulationEngine.simulateMonth()` (CLAUDE.md, "the month is a sequence").

---

## 5. The moneys

### 5.1 Each nation's money

- **About 145 moneys** among the 196 [R22]:
  - **The euro:** 25 UN members use it, the eurozone's 21 (Bulgaria joined on 1 January 2026) and Andorra, Monaco, San Marino and Montenegro.
  - **Pegged to the euro:** fourteen West and Central African nations use the two CFA francs.
  - **The US dollar:** Ecuador, El Salvador, Panama, East Timor and three Pacific states use it, and the Gulf states but Kuwait peg to it.
  - **Others' moneys:** six Caribbean states share the Eastern Caribbean dollar; Kiribati, Nauru and Tuvalu use the Australian dollar, and Liechtenstein the Swiss franc.
- **Each money's rate to the dollar follows its nation's path:**
  - **Historical:** the real annual rates 2000-2025 [R11], then the difference between its inflation and the dollar's (relative purchasing-power parity), plus the seed's shocks.
  - **Random:** that same drift and the seed's shocks from the start.
  - **Pegs:** they hold their peg in both.
- **Cross rates** go through the dollar: the city's D$ against the euro is D$ per US$ times US$ per euro.

### 5.2 The vault in eight

Jerus: "Own prices, vault in 8".
- **The eight** are the dollar, euro, yen, pound, yuan, and the Canadian and Australian dollars and the Swiss franc: the currencies the IMF's COFER names. In 2026's second quarter they held about 94% of the world's reserves: the dollar 56.7%, the euro 20.6%, the yen 5.0%, the yuan 2.1%, and the pound, the Canadian and Australian dollars and the Swiss franc about 9.5% together [R23].
- **The vault** keeps a balance in each, valued in US$ for the cover and the backing as today (`reservesUsd` becomes their sum at the day's cross rates).
- **The treasury's buys** go into the eight by a **reserve mix** (D20):
  - **all US$**, today's behaviour and the default;
  - **match our trade**: the city's trade by money, the eight's shares;
  - **the world's mix**: COFER's shares;
  - **choose**.
  Sells come from the largest holding unless the player names one. The exchange card on Trade › The reserves gets a money picker.
- **Dollar bonds stay dollar bonds.** Debt in other moneys is later, with CD5's floaters (§11).

### 5.3 Trade settled in the partner's money

- **A sale to Canada is earned in Canadian dollars** and a purchase from Germany paid in euros, each converted at the month's cross rate into D$ for the city's books. Prices in the solve are in dollars, the world's unit of account (about 40% of world trade is invoiced in dollars, far more than the US's share [R24]). The money only says which money changes hands.
- **What it changes:** the Partners page and the cards show each partner's money. "Match our trade" has something to match. The pressure on the city's rate is the same sum as today, by trade, in dollars.

### 5.4 What moves the vault's worth

- **A vault in eight is worth more or less in dollars as the eight move against each other.** That is not spending or earning: it is a revaluation, booked in MoneyAudit's VALUATION scope, as the D$ value of today's dollar vault already is.
- **The reserves page** says it in one line: "This month the vault's worth moved -US$0.6M: the euro fell 0.9% against the dollar" (board 3).

---

## 6. Borders and land

### 6.1 Natural Earth's borders

Jerus: "Borders baked in".
- **Two parts in the world file:**
  - **The lines,** Natural Earth's admin-0 boundary lines at 1:10m [R10], kept as polylines to draw.
  - **A raster of who owns each cell** at 240 a degree, the sea layer's own grid. A tile wholly inside one nation is stored as that nation's number alone; only the tiles a land border crosses are stored in full. About 4,000 such tiles, a few MB (est.).
- **De facto lines.** Natural Earth publishes the de facto boundaries and a set of points of view; the file uses the de facto set, and a disputed area belongs to whoever administers it.
- **The sea:** a sea plot within 12 nautical miles of a coast belongs to that coast's nation, the territorial sea of the Law of the Sea [R25]. Beyond, the land office sells it as the host's, as today's wet offers are sold.
- **On the map:** dashed white, at the far view and on the globe, and at the near view only where a border crosses the city's ground. The Borders chip turns them off (§8).

### 6.2 Land bought from its nation

- **Each offer is sold by the nation its ground lies in.** An offer across a border is split, each part priced and paid to its own nation. The offer card names them: "Offer 4 · 1.44 km² · United States 62%, Canada 38%" (board 3, a city at Windsor).
- **The city's own ground stays the city's.** The city is a state of its own, with its money and its central bank. Buying from Canada makes the plot the city's, as today.
- **Paid in the seller's money:** from the vault's holding of it when it is one of the eight and the player pays from the vault, otherwise converted from the treasury's cash at the cross rate, as today's default.

### 6.3 A hectare's price by nation

Jerus: "land costs different prices purely by who you are buying it from".
- **Today's formula stays,** times the seller's **land factor**: its land's price a hectare against Canada's, in its own money at its rate.
  - **Canada is 1** (D21), so a Halifax city pays what it pays today.
  - Where a nation's farmland price is published, the factor is that over Canada's: the EU's from Eurostat [R26], the US's from the USDA [R27], Canada's from FCC [R28].
  - Elsewhere it is a fit on output a head and people per km² over the nations that publish, marked ○.
- **The spread is large and real:**
  - Arable land in the EU runs from €4,800 a hectare in Latvia and €6,400 in France to €96,600 in the Netherlands (2024) [R26].
  - US cropland averages US$14,880 [R27].
  - Within Canada, northeast Saskatchewan's cropland is about C$12,000 and southwest Ontario's about C$83,000 [R28].
  - So a city at Windsor pays 1.7 times as much a hectare on the Detroit bank as on its own (board 3, est.).
- **Canada's own level** is not on FCC's public page (its report gives each province's change). WN0 takes Statistics Canada's farm land value an acre; until then it is about C$12,000 a hectare (est.).
- **The factor moves with the nation's path:** with its output a head, and with its money in D$ terms.

### 6.4 A generated world

- **A generated world has no Earth.** For the market, the city stands at a real coastal place drawn from its ground's seed (Bergen for one seed, Durban for another), and the nations are as far as they are from there (D23).
- **Its land is the host's,** at factor 1 (as today), so a generated world's land prices do not move.
- **Its map** draws no borders; each partner is an arrow at the map's edge on its bearing, as the world lines are "to the world ›" today.

---

## 7. The path: Historical or Random

Jerus picked "Historical or Random". Both are picked at founding and both are seeded.

### 7.1 Historical

- **What it replays, from January 2000:** each nation's real output and people, its money's rate, and the real price of each good against the dollar's general prices, year by year, from the world file.
- **What it does not replay: the dollar's own price level.** That stays the founding chip's, as today (D13). The game's prices are 2025's (the leaderboard's D1), so the history is laid on them as ratios to 2025. In January 2000 crude is at 0.77 of the game's price today, its real price that year against 2025's. It rises to 2.30 in 2011 and falls to 0.76 in 2020 (Pink Sheet annual averages over the US CPI-U) [R19]. The calendar and the history agree, and the game's prices are what the world's were in 2025.
- **After 2025:** the UN's people to 2100 [R13], and each nation's output by the game's own rule: Random's trend with its convergence (§7.2), without Random's shocks, the IMF's terms ruling out its WEO (§3.3). Past 2100 comes the leaderboard's rule. Shocks are drawn from the seed on top, sized as Random's but smaller. The future is not known, even in a Historical world.
- **The founding balance moves.** In January 2000 most commodities were far cheaper than in 2025. Against 2025's real prices: crude 0.77, iron ore 0.54, copper 0.34, thermal coal 0.45, wheat 0.88 (Pink Sheet over the US CPI-U) [R19]. A mining city founded in 2000 earns less for its first five years and much more in 2005-2013, as real ones did. WN3's ensemble measures how much this moves the game's balance (§9).

### 7.2 Random

- **The same January 2000,** then each nation's growth is drawn each year as:
  - a **trend** that falls as the nation nears the richest (the poorer grow faster, as the convergence literature finds, at about 2% of the gap a year) [R29];
  - a **world factor** shared by every nation. Business cycles are partly global: across 106 countries a world factor explains about 11% of output-growth swings, and 27% in the industrial ones [R30];
  - a **region's factor**;
  - **its own shock**, persistent (this year's carries 0.3 into the next), larger for poorer nations. A year's growth swings by 1.8 points in the G7 against 2.7 in fifteen large emerging economies (one standard deviation, 2000-2019) [R11]; quarter by quarter, Aguiar and Gopinath find twice [R31];
  - **a crisis now and then.** Barro counts a fall of 15% or more in output a head, over a few years, in 1.7% of country-years over a century [R32]. In 2000-2024, one country-year in twenty grew less than -5% [R11].
- **People** follow the UN's medium projection in both worlds; the seed does not draw them.
- **Each good's real price** moves as its supply is shocked each year: a persistent shock (0.7 of it carries over) at the good's real volatility. The yearly change of its real price, one standard deviation, 2000-2025: crude and iron ore about 30%, thermal coal about 40%, copper and wheat about 20%, aluminium 17% [R19]; manufactured goods little. The solve turns those shocks into prices; a good that swings in supply swings in price.
- **Inflation and the moneys:** each nation's inflation is drawn around its income group's mean, and its money's rate follows relative parity plus a shock. Pegs hold.

### 7.3 Determinism, saves and the harnesses

- **A nation's year is a pure function of (path, seed, year).** Every draw uses `new Random(scramble(seed, nation, year, what))`, so nothing about the nations needs saving but the path and the seed.
- **The dollar's draws join the world's seed** (§11, item 1). Two cities with one seed meet one world; two seeds meet two.
- **What is saved:** the path and the seed (WN1); the January solve's mill prices, about 5,000 numbers, from which every price index, demand and slope is recomputed exactly in one pass; and the city's trade by partner this year (WN3). A reload is exact, because the solve is not re-run.
- **The harnesses:** a harness city founds a Random world on a fixed seed and stays identical run to run (ForeignCheck holds). A market check founds two seeds and asserts they differ. LongPlaytest takes `-Dplaytest.world=historical|random` and `-Dplaytest.worldSeed`.

---

## 8. The screens

Four boards in `nations/`, drawn in the game's palette (`ui/Palette`):

1. **The world map** (`1-the-world-map.html`): the expanded map at the far view over the North Atlantic, its ground the game's own render. Jerus's asks, each placed:
   - **"dot and routes":** a dot at each nation's node, its area by output. This month's trade is drawn as routes from the city, violet what we sell and sand what we buy, width by money, under D$0.3M a month none.
   - **"a toggle on the map ui":** three chips in the map's head, **Nations**, **Trade routes** and **Borders**, remembered with the map's settings.
   - **"show your population and gdp on the top":** the header's POPULATION and GDP tiles, the same figures and sparklines, in the map's head. The map lies over the whole window and hides the header, so it carries its own.
   - **"legend should be collapsable":** the legend gets a head that folds it. Today it is mouse-transparent and never folds (`MapView.legend()`). Only the head takes the click.
   - **"a map button to ... toggle borders":** the Borders chip.
   - **"each nation stats on their land ... canada would show gdp and ... population":** name, output and people written on the nation's land where its ground in view can hold them. A small one keeps its dot, and hovering it shows the same.
   - **"clicking would show more stats based on the leaderboard":** a card. It shows output, people and output a head, each with its rank of 196; this year's growth and prices; its money and rate to D$; its land's price; what it buys from us and sells us this year; the haul; and doors to "Its trade with us" (Trade › Partners) and "All its measures" (Rankings, when the leaderboard is built).
   - **"or is that too much?"** No: it is one batch of the map's (WN2), and the card's figures are the world file's.
2. **Who buys your wood** (`2-who-buys-your-wood.html`): a new **Partners** page on Trade & the world. It has:
   - who we trade with, by nation, with each one's money and haul;
   - for a chosen good, **where the next tonne goes**: the export curve, what the next tonne nets against how much the city sells, with the month's line and the world's, and the points where each buyer joins;
   - the buyers at what we sell now, all netting the same;
   - the good's band this month, the floor and ceiling named by where they come from;
   - what moves it.
3. **The vault in eight, and a border offer** (`3-the-vault-in-eight.html`): Trade › The reserves with the eight balances, their worth in US$ and D$, this month's revaluation, the reserve mix and the exchange card's money picker. Below it, the land office's card for a Windsor city's offer across the Detroit River, split and priced by nation, each part in its own money.
4. **Found the world** (`4-found-the-world.html`): the founding screen's new **World** step: Historical or Random, the seed with its dice, the dollar's inflation chips (renamed so: they are the dollar's), the generated world's stand-in place, and a chart of the world's output and crude for the chosen path.

---

## 9. Batches

| Batch | What | Traces |
|---|---|---|
| **WN0** | The world file: `tools/world/` (the pipeline, the concordance from HS to the game's goods, the partner rule, the base-year solve and its preferences), `data/world-1.bin` and a resources fixture, `WorldData` (the reader), `WorldDataCheck` (every UN member and the three present, the world's sums close, the freight table keeps the triangle rule, the base year reproduces BACI's flows on the kept pairs, a nation per land cell inside every border tile) | identical |
| **WN1** | The nations in the model: `Nations` (each nation's year from the path, the seed and the file), the founding screen's World step (Historical or Random, the seed; the inflation chips renamed the dollar's), the path and the seed saved; nothing in the economy reads them yet; `NationsCheck` (one seed one path; Historical equals the file to 2025; Random's swings match its dials over a thousand seeded years; pegs hold; output never negative) | identical |
| **WN2** | The map: the borders drawn and their chip, the nations' dots, labels and card, the Nations chip, the head's POPULATION and GDP tiles, the legend that folds, Trade routes' chip (drawn once WN3 records partners); a generated world's partners as arrows at the edge; MapCheck's sections for the borders and labels | identical |
| **WN3 ★** | The market: the January solve at the turn of the year in both month paths, the city's step by the slopes, the band from the world (§4.5), the base prices calibrated to the literals, the freight table from the city (berths and gateways), the city's trade by partner (kept, saved, a History series by partner and good), `WorldEconomy`'s draws from the world's seed; `SAVE_FORMAT` bumped and the floor raised with it (an old save has no path or seed); `WorldMarketCheck` (the floor never above the ceiling, the month's step within 3% of a re-solve, a city at a tenth of a good's world use moves it, two seeds differ, one seed repeats) | every trace moves; re-baseline, the ensemble, Historical and Random both |
| **WN4** | The moneys: each nation's money and rate, the cross rates, the vault in eight and its reserve mix (default all US$), trade labelled with its money, revaluation in VALUATION; saved balances | identical (the default holds all in US$) |
| **WN5** | Land from its nation: the seller from the borders, the land factor in the seller's money, an offer split at a border, the territorial sea, payment in the seller's money; a generated world's host at factor 1 | identical on a generated world and in Canada; moves on Earth worlds elsewhere |
| **WN6** | The screens: Trade › Partners (board 2), The reserves in eight (board 3), the nation's card's full page, the Overview's partners strip; the manual | identical |
| **WN7** | Ships and trains to their partners: each call carries its partner, and a boat leaves on its partner's bearing (an offing per bearing); the world line's trains toward the neighbours' network | identical |

**Order:**
- WN0 first, then WN1.
- WN2 and WN3 can go in either order after WN1, on files of their own (the map's, and the market's).
- WN4 after WN3, WN5 after WN4, WN6 after WN5. WN7 after WN3.

**WN3 is the heart.** Its brief fixes the base year, θ by good class, the founding balance's measure (Historical's 2000 against 2025's prices) and the ensemble's two paths.

**Against the rest of the queue:**
- FR4 (the freight screens) touches Trade's band, which WN3 redefines. FR4 goes first, as queued.
- CD1-CD6 keep dollar paper.
- **The exchange-rate study** (2026-10-21, [spec-exchange-rates.md](spec-exchange-rates.md)): WN4 builds its moneys' rates on that study's rule (§3.7: each money's PPP path, the dollar cycle, risk-offs, terms of trade from the January solve, its own swing, pegs held and broken; XR3 folded in), and WN0's world file carries the real monthly rates 2000-2025 for every money (Jerus, its ★10 and ★11). XR1 and XR2 (the D$ and its regimes) come before WN3; XR4 (interest abroad) right after WN4.
- R0 (the calendar from 1900) would carry Historical back with Maddison's output [R33] when it comes.

---

## 10. ★ Decisions

**Answered, Jerus, 2026-10-20**, in this session's questions, and then "Yes, do rec" for every call still open. Every recommendation below stands; the batches are queued.

**His calls**
1. **D1. The nations: all 196**, the leaderboard's peer list. **Jerus: "All 196".**
2. **D2. The solve: the smooth spatial equilibrium**, buyers spreading over many sellers. **Jerus: "smooth buyers sperad"**, after asking "how do we solve every with every via math? im sure there is an algorirthm". There is one, and it is §2.
3. **D3. The world solves yearly, the city monthly.** **Jerus: "World yearly, city monthly".**
4. **D4. The city moves prices within the year, by the slopes.** **Jerus: "Yes, monthly by the slopes".** It includes the world's slope (§4.4), without which the month misses by up to 14%.
5. **D5. The partner rule:** the top 20 with all; the rest with the top 10 exporters and the 10 nearest; the city with all. **Jerus's own**, with D11.
6. **D6. Each nation prices in its own money; the vault holds eight.** **Jerus.**
7. **D7. Borders baked in; land bought from its nation.** **Jerus.**
8. **D8. The world answers the city in prices only**, never in a nation's growth. **Jerus.**
9. **D9. Historical or Random, both seeded.** **Jerus.**
10. **D10. The map:** dots and routes, the toggles, the head's tiles, the legend that folds, the Borders button, nations' figures on their land and the card on a click. **Jerus**, who asked "or is that too much?" It is not: it is WN2.

**Taken on recommendation ("do rec")**
11. **D11. "10 nearest" means the 10 nearest makers of the good**, not the 10 nearest nations. It halves the worst error (8.5% to 4.1%) for 18% more pairs (§4.2). **Recommend yes.**
12. **D12. The game's literals stay the prices;** the data gives quantities, changes and geography (§4.5). **Recommend yes.** The rebalance's work is not undone, and a Halifax city sees today's band in 2025.
13. **D13. Both paths start in the real January 2000; the dollar's own price level stays the founding chip's** (§7.1). **Recommend yes.** The calendar and the history agree. The alternative, starting from 2025's world whatever the calendar says, keeps today's founding balance, but a "Historical" world would then replay nothing.
14. **D14. Historical after 2025:** the UN's people to 2100; output by the game's own rule (Random's trend, without its shocks), then the leaderboard's rule; seeded shocks on top. **Recommend yes.** The IMF's WEO would be the obvious source, but its terms ask for permission for commercial reuse. If Jerus obtains it, the rule's first five years can follow the WEO instead.
15. **D15. Random's rule** as §7.2: trend with convergence, world, region and own factors, crises at Barro's rate, supply shocks at each good's real volatility. **Recommend yes.**
16. **D16. The band from last month's quantities,** with no iteration inside a month. **Recommend yes.** It keeps the one-pass strike; WN3's harness checks a steady city sees a steady band.
17. **D17. The services keep today's fixed band;** the one-sided goods keep their side. **Recommend yes.**
18. **D18. θ is each good's price over its trade elasticity** (about 5 on average, est. 8 to 10 for bulk commodities, §4.3), with **fixed pair preferences** so the base year reproduces BACI's flows. **Recommend yes.**
19. **D19. The city is an ordinary seller** in each market (no preference, no penalty). **Recommend yes.**
20. **D20. The reserve mix defaults to all US$**, as today; "match our trade" and "the world's mix" are one click. **Recommend yes.** WN4's traces stay identical and the choice is the player's.
21. **D21. Canada is the land's reference (factor 1),** others by their land price against it. A generated world's host is 1. **Recommend yes.**
22. **D22. The territorial sea** (12 nautical miles) is the coast's nation's; beyond, the host's. **Recommend yes.**
23. **D23. A generated world stands, for the market, at a real coastal place drawn from its seed.** **Recommend yes.** The alternative, no nations on a generated world, would leave its cities on today's static world.
24. **D24. One world file** (`data/world-1.bin`, a fixture in the resources, the pipeline in `tools/world/`, the release Jerus's), and the leaderboard reads its nations and paths. **Recommend yes.**
25. **D25. Ships and trains to their partners** (WN7). **Recommend yes, last.**
26. **D26. Tariffs, sanctions and nations' events** (wars, embargoes, a nation's default) are not in these batches. **Recommend a study of their own** once WN3 has been played. Tariffs have open data (WITS) and slot into the freight term as a cost on a pair.
27. **D27. The leaderboard's Projected board reads the game's world:** the city's own nations on its own path, not the real projections. **Recommend yes**, once both exist. In a Random world the real projections would be another world's.
28. **D28. The save:** the path and the seed (WN1); the solve's mill prices and the city's trade by partner (WN3), with the format bumped and the floor raised. **Recommend yes.**
29. **D29. Two US dollars: the real one at constant 2015 dollars** (Jerus, 2026-10-21: "lets keep something, the real usd dollar, aka two us dollars, one is the in game us dollar which inflates over time, and the other is the real us dollar, and that one is what real gdp is shown in, as well as an exchange rate with it, purely just for comparision purposes"; base year: "2015, cause later the start date will move back anyways"). The in-game US$ is the US nation's money, inflating on its path. The real US$ is it deflated by the US price path to constant 2015 dollars, as the World Bank reports: real GDP is shown in it (the real GDP at base prices at the base year's rate), with an exchange rate against it, for comparison only. Nothing in the model reads either. The foreign inflation slider goes: each nation inflates on its own path (WN1), Random paths giving the variety the slider gave. Built in WN4 (the moneys) and shown in WN6 (the screens).

---

## 11. Found on the way

1. **The dollar's inflation ignores the city's seed.** `WorldEconomy.advanceMonth()` draws from `new Random(scramble(SEED + month))`, so every city founded on the same chip meets the same dollar every month. This is Jerus's "one path", literally one path for all. WN3 seeds it from the world's seed.
2. **Nauru has no place in earth-1.bin's list**, and Natural Earth's names there include a misspelt "Necw Zealand" (its own data's, for an outlying place). The places name 228 countries and territories, not the 193 members. WN0 takes the nations from Natural Earth's admin-0 table, not from the places.
3. **A place's country is parsed and read nowhere** but EarthCheck. WN0's nodes can use the places for their town names.
4. **Exports and imports are unbounded at the band's edges.** No buyer abroad ever runs out of appetite, which is why a city could never become a market. It is the root of this study.
5. **No record keeps trade by good or by partner over time.** History has the totals abroad and one good's price (iron's export price as `orePrice`), and the month's trade by good is wiped each month. The Partners page needs WN3's series.
6. **The map's legend covers its corner.** About 52 rows in three or four columns, mouse-transparent, never folded (`MapView.legend()`). WN2 folds it.
7. **The founding chips are called the world's inflation but are the dollar's.** WN1 names them so.
8. **The leaderboard's planned `world-path.json` and this study's world file overlap.** D24 makes them one.
9. **One land factor a nation misses its regions.** Southwest Ontario's farmland is about seven times northeast Saskatchewan's [R28]. WN5 prices by nation; a regional factor (provinces, states) is a later batch if Jerus wants it.
10. **2026's prices are a shock, not a base.** Brent was US$120 in April 2026 and US$117 in September, and Capesize freight doubled [R19][R21]. The file's base year is 2025's averages (BACI's trade to 2024, grown a year), never 2026's spot.
11. **Debt in other moneys.** With eight moneys in the vault, a treasury might borrow in euros or yen. The city-debt study's CD5 (floaters and linkers) is the place to ask; not in these batches.

---

## Sources

Figures from the research for this study (2026-10-20); a figure marked (est.) is derived from them or estimated. The game's own figures are from the code at 2631c1c (0.8.6); the measured figures are `nations/NationsModel.java`'s.

- [R1] Samuelson, P. A. (1952). Spatial price equilibrium and linear programming. *American Economic Review* 42(3): 283-303 (JSTOR); cited through https://journals.sagepub.com/doi/10.1068/a210099
- [R2] Takayama, T. and Judge, G. G. (1964). Spatial equilibrium and quadratic programming. *Journal of Farm Economics* 46(1): 67-93. https://doi.org/10.2307/1236473 ; and their *Spatial and Temporal Price and Allocation Models* (North-Holland, 1971)
- [R3] Wilson, A. G. (1967). A statistical theory of spatial distribution models. *Transportation Research* 1(3): 253-269. https://doi.org/10.1016/0041-1647(67)90035-4 ; and his *Entropy in Urban and Regional Modelling* (Pion, 1970)
- [R4] Sinkhorn, R. (1964). A relationship between arbitrary positive matrices and doubly stochastic matrices. *Annals of Mathematical Statistics* 35(2): 876-879. https://doi.org/10.1214/aoms/1177703591 ; Cuturi, M. (2013). Sinkhorn distances: lightspeed computation of optimal transport. NIPS 26. https://arxiv.org/abs/1306.0895
- [R5] Disdier, A.-C. and Head, K. (2008). The puzzling persistence of the distance effect on bilateral trade. *Review of Economics and Statistics* 90(1): 37-48 (1,467 estimates, mean distance elasticity 0.91). https://doi.org/10.1162/rest.90.1.37
- [R6] Head, K. and Mayer, T. (2014). Gravity equations: workhorse, toolkit, and cookbook. *Handbook of International Economics* 4: 131-195 (the trade-cost elasticity, preferred -5.03). https://doi.org/10.1016/B978-0-444-54314-1.00003-3 ; working paper: https://www.cepii.fr/PDF_PUB/wp/2013/wp2013-27.pdf
- [R7] Anderson, J. E. and van Wincoop, E. (2003). Gravity with gravitas. *American Economic Review* 93(1): 170-192. https://doi.org/10.1257/000282803321455214 ; Fally, T. (2015). Structural gravity and fixed effects. *Journal of International Economics* 97(1): 76-85. https://doi.org/10.1016/j.jinteco.2015.05.005
- [R8] Bajzik, J., Havranek, T., Irsova, Z. and Schwarz, J. (2020). Estimating the Armington elasticity. *Journal of International Economics* 127: 103383 (median 3.8). https://doi.org/10.1016/j.jinteco.2020.103383 ; Armington, P. S. (1969). *IMF Staff Papers* 16(1): 159-178. https://doi.org/10.2307/3866403
- [R9] Hummels, D. (2007). Transportation costs and international trade in the second era of globalization. *Journal of Economic Perspectives* 21(3): 131-154. https://business.purdue.edu/faculty/hummelsd/papers/hummels%20jep%20rewrite%20final%20with%20tables.pdf
- [R10] Natural Earth, admin-0 countries at 1:10m, v5.1.1 (258 features; public domain): https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-0-countries/ ; terms: https://www.naturalearthdata.com/about/terms-of-use/ ; the GeoJSON: https://github.com/nvkelso/natural-earth-vector
- [R11] World Bank, World Development Indicators (CC BY 4.0): https://datacatalog.worldbank.org/public-licenses ; the API, growth: https://api.worldbank.org/v2/country/all/indicator/NY.GDP.MKTP.KD.ZG?format=json&date=2000:2025 (the volatility and crisis shares are this study's arithmetic on it)
- [R12] IMF, World Economic Outlook database: https://data.imf.org/datasets/WEO ; IMF copyright and terms (commercial reuse by permission): https://www.imf.org/en/About/copyright-and-terms
- [R13] UN, World Population Prospects 2024: https://www.un.org/development/desa/pd/content/world-population-prospects-2024
- [R14] CEPII, BACI, version 202601 (HS6, 1995-2024; Etalab 2.0): https://www.cepii.fr/DATA_DOWNLOAD/baci/doc/baci_webpage.html ; Gaulier, G. and Zignago, S. (2010), CEPII WP 2010-23
- [R15] FAO, FAOSTAT terms of use: https://www.fao.org/contact-us/terms/db-terms-of-use ; forestry statistics: https://www.fao.org/forestry/statistics/data/en ; 2024's forest products: https://www.fao.org/newsroom/detail/global-forest-products-trade-and-production-show-signs-of-recovery-in-2024/en
- [R16] USGS, Mineral Commodity Summaries 2026, data release (CC0): https://www.usgs.gov/data/mineral-commodity-summaries-2026-data-release
- [R17] worldsteel, World Steel in Figures 2026 (copyright): https://worldsteel.org/wp-content/uploads/World-Steel-in-Figures-2026.pdf ; BGS, World Mineral Production (copyright): https://www.bgs.ac.uk/mineralsuk/statistics/world-mineral-statistics/
- [R18] EIA, copyright and reuse (public domain): https://www.eia.gov/about/copyrights_reuse.php ; Energy Institute, Statistical Review of World Energy: https://energyinst.org/exploring-energy/statistical-review
- [R19] World Bank, Commodity Markets (the Pink Sheet; CC BY 4.0): https://www.worldbank.org/en/research/commodity-markets ; the monthly file: https://thedocs.worldbank.org/en/doc/74e8be41ceb20fa0da750cda2f6b9e4e-0050012026/related/CMO-Historical-Data-Monthly.xlsx (the ratios and volatilities are this study's arithmetic on it, over the US CPI-U: WDI's FP.CPI.TOTL to 2024, 2025 est.)
- [R20] Bertoli, S., Goujon, M. and Santoni, O. (2016). The CERDI-seadistance database, v1.1 (CC BY 4.0): https://zenodo.org/record/240493
- [R21] Capesize C3, Tubarao to Qingdao: https://www.hellenicshippingnews.com/january-snapshot-capesize-ballasters-demand/ ; https://www.thesignalgroup.com/weekly-market-monitor/weekly-dry-market-monitor-week-36-2026
- [R22] ECB, Bulgaria joins the euro area, 1 January 2026: https://www.ecb.europa.eu/press/pr/date/2026/html/ecb.pr260101~c830245e42.en.html ; ISO 4217 codes: https://raw.githubusercontent.com/datasets/currency-codes/main/data/codes-all.csv ; the CFA franc zone: https://www.diplomatie.gouv.fr/en/the-ministry-in-action/contributing-to-sustainable-balanced-globalization/combating-global-social-inequality/africa-a-priority-for-french-official-development-assistance/franc-zone
- [R23] IMF, COFER data brief, 30 September 2026: https://data.imf.org/en/news/imf%20data%20brief%20september%2030 ; the 2025 technical note: https://www.imf.org/-/media/files/publications/tnm/2025/english/tnmea2025014.pdf
- [R24] ECB, the international role of the euro, June 2025 (invoicing): https://www.ecb.europa.eu//press/other-publications/ire/article/html/ecb.ireart202506_02~a8e66f5ea3.en.html ; Boz, E. et al. (2022), *Journal of International Economics* 136: 103604. https://doi.org/10.1016/j.jinteco.2022.103604 ; Gopinath, G. et al. (2020). Dominant currency paradigm. *American Economic Review* 110(3): 677-719. https://doi.org/10.1257/aer.20171201
- [R25] UN Convention on the Law of the Sea, Part II, article 3 (the territorial sea, 12 nautical miles): https://www.un.org/depts/los/convention_agreements/texts/unclos/part2.htm
- [R26] Eurostat, agricultural land prices, `apri_lprc` (2024): https://ec.europa.eu/eurostat/api/dissemination/statistics/1.0/data/apri_lprc?format=JSON&lang=EN&time=2024
- [R27] USDA NASS, Land Values 2026 Summary: https://esmis.nal.usda.gov/sites/default/release-files/795999/land0726.txt
- [R28] FCC, Farmland Values Report 2025: https://www.fcc-fac.ca/en/knowledge/economics/farmland-values-report ; the regions' levels in the farm press: https://farmtario.com/news/farmland-value-stagnates-in-ontario-in-2025/ ; https://www.producer.com/news/farmland-climbs-higher-in-spite-of-headwinds/
- [R29] Barro, R. J. and Sala-i-Martin, X. (1992). Convergence. *Journal of Political Economy* 100(2): 223-251. https://doi.org/10.1086/261816
- [R30] Kose, M. A., Otrok, C. and Prasad, E. (2012). Global business cycles: convergence or decoupling? *International Economic Review*; NBER w14292: https://www.nber.org/system/files/working_papers/w14292/w14292.pdf ; Kose, Otrok and Whiteman (2003), *American Economic Review* 93(4). https://doi.org/10.1257/000282803769206278
- [R31] Aguiar, M. and Gopinath, G. (2007). Emerging market business cycles: the cycle is the trend. *Journal of Political Economy* 115(1): 69-102. https://doi.org/10.1086/511283
- [R32] Barro, R. J. (2006). Rare disasters and asset markets in the twentieth century. *Quarterly Journal of Economics* 121(3): 823-866. https://doi.org/10.1162/qjec.121.3.823
- [R33] Penn World Table 11.0 (CC BY 4.0): https://www.rug.nl/ggdc/productivity/pwt/pwt-releases/pwt110 ; Maddison Project Database 2023 (CC BY 4.0): https://www.rug.nl/ggdc/historicaldevelopment/maddison/releases/maddison-project-database-2023
- [R34] Freight by mode, order of magnitude: BTS, rail revenue a ton-mile: https://www.bts.gov/archive/publications/national_transportation_statistics/table_03_21 ; ATRI's truck costs, 2025: https://www.ttnews.com/articles/atri-truck-costs-2025
- [R35] Drewry, World Container Index, 15 October 2026: https://www.drewry.co.uk/supply-chain-advisors/supply-chain-expertise/world-container-index-assessed-by-drewry
