# Electricity: power plants, their fuels and the grid - the study, 2026-11-02

Read-only study of 0.9.2 (`main` at 5602c6d0), asked for by Jerus as one of the problems no batch covers: coal power plants burn no coal, and the electricity run that would fix it (spec-materials Q10) was decided and never queued. His brief: an electricity batch "including fossil fuels, oil, and even uranium and others", "accurate, interesting, and barely uses much compute - by compute I mean in game compute".

Prototype in `electricity/`: `Dispatch.java` (one file: `java docs/studies/electricity/Dispatch.java`), the month's merit order over the plant rows and three demand blocks, the planner's two rules, the tariff at cost, ten scenarios and the timing; its outputs in `electricity/out/` (`scenarios.txt`, `timing.txt`). The two scripts that measured wind and solar against the load from EIA's hourly grid data are `electricity/vblocks.py` and `electricity/cv.py` (they read EIA-930's 2025 files, not kept here: 95 MB).

Each figure is one of four kinds: **from a source**, cited as [S n] or [H] from §7, where each source's licence is given (the game is sold, so a figure that reaches the game must come from a source that allows commercial use: `licences.md`); **derived** [D], with the sum shown; **measured** [M], in the prototype, on EIA's hourly data, or on a save read without loading it (Jerus's slots read, never written); or **★est.**, a named estimate with its reason, listed in §6. Money is in US$ near 2025's level, which is the game's "today" before `PriceLevel.FACTOR`.

## 1. Summary for Jerus
1. **Today a coal plant burns nothing, and power is nearly free.** The tariff is a constant, D$10 a kW a month (1.39¢ a kWh), struck at founding and never moved by inflation; a coal plant would burn 1.8-2.7¢ of coal a kWh at the game's own coal price (0.357 t at D$50-75), more than the tariff (spec-materials Q10). Homes and the city's buildings pay nothing. Your Rotterdam (2.9M people) runs on 1,094 wind farms counted as if the wind never stopped, and a 1905 town of 359 people stands on a 2020s wind farm.
2. **The fix is a merit order once a month.** The month is cut into three blocks of hours (base, shoulder, peak, from EIA's 2025 hourly data), each split in two halves (windy and calm, sunny and dark). Free power runs first (wind, sun, the steelworks' own gas), hydro shaves the peaks with its water, then the fuelled plants run cheapest first. **Measured: about 2 µs a month, 0.003% of a month's time** at 16 months a second (0.2 ms when the planner weighs a power order).
3. **Plants burn real fuel by the MWh.** Coal 0.357 t a MWh, fuel oil 283 L, diesel 260-298 L, wood pellets 0.75 t, uranium 26 g of yellowcake, all bought from the city's market, so the coal mines, the refinery's fuel oil and the pellet mill gain a buyer.
4. **The price covers the cost (your call Q1).** The tariff becomes the utility's cost: fuel, wages, upkeep, the plants' capital over their lives and the wires, over the last twelve months. For a 1 GW coal city that is 14.8¢ a kWh, about the US's 13.6¢ in 2025; industry, on the high-voltage lines, pays 10.8¢ (the US's 8.5¢). A Policy dial can subsidise it or tax it.
5. **Nine plants with real figures** (EIA, the national lab's cost baseline): the coal plant refigured, the hydro station and wind farm kept, and six new: an oil-fired plant, a diesel station, an oil-fired turbine for the peaks, a nuclear plant, a biomass plant and a solar farm.
6. **The trade-offs fall out of the figures, unscripted.** Coal is dear to build and cheap to run; the oil turbine cheap to build and dear to run, so it waits for the peaks; nuclear the dearest to build and cheapest to run. In a coal city an oil shock moves the tariff 3.9%; on an island burning oil it moves it 69%. A diesel town builds a wind farm because the wind saves its diesel.
7. **Wind and solar count for what they deliver at the peak.** Wind counts for about a quarter of its size and solar for about 60%, both falling as they grow (EIA's hourly data). Measured: your Rotterdam's fleet, scaled to 1 GW, leaves 39% of the city's energy unserved; the planner then adds 1,625 MW of coal and 400 MW of turbines behind it.
8. **The planner builds by two rules.** First it keeps enough firm power for the peak hour plus 15%, buying the cheapest plant for the slice of hours that is short; then it builds whatever saves more fuel over its life than it costs. A plant too big for the town's need pays for its idle part, so a 10 MW town builds a diesel station, not a 325 MW coal plant.
9. **Siting matters.** Coal is cheapest beside its mine (D$50 a t against D$75 imported), thermal and nuclear plants need owned water to cool them, hydro needs a river with a drop, and the sun's yield falls with latitude.
10. **Uranium gets a use:** a mine and mill on the uranium sites, a nuclear plant from 1957 (seven years to build), its enrichment and fabrication bought abroad.
11. **1900 (your call Q2).** In 1900 a coal plant burned about 9 times today's coal per kWh and a home's kWh cost 36 times today's, in today's money. Measured: a 1 GW city on 1902's coal plants pays 39¢ a kWh. I recommend each plant from the year it was first built, at today's figures, until research dates demand as well.
12. **Eight batches,** PW1 to PW8: the merit order and the tariff, the plants, wind and solar and the planner, uranium and the reactor, the grid, the screen, natural gas (your call Q3), and the homes paying for power (your call Q4).

**Your calls** (§6, asked in the question box): Q1 the price, Q2 the 1900 city, Q3 natural gas, Q4 the homes' bills, Q5 the grid. Each has a recommendation.

## 2. The design, on the code

### 2.1 What stands today
Every line below was read in the code on 2026-11-02.

- **Three plants, all the city's.** `buildings.json` has three ELECTRICITY rows, owned by no sector, ordered by the city or its `AutoBuilder` as an essential service (`AutoBuilder.essential()`: past the revenue budget, held only by the debt limit):

  | id | row | nameplate, capacity factor (HY1's table) | `production1` | builds for | posts | `since` |
  |---|---|---|---|---|---|---|
  | 9 | Coal Power Plant | 325 MW, 86% | 280,000 | D$1.43B (1,071,816 + 18 × 19,848, in thousands) | 68 | none |
  | 41 | Wind Farm | 20 MW, 40% | 8,100 | D$31.0M | 7 | none |
  | 144 | Hydro Station (HY1) | 10 MW, 45% | 4,560 | D$29.2M | 5 | 1900 |

  `production1` is the month's kWh over 720: average kW, so a capacity factor is already in it. The grid starts with `UtilitiesHandler.BASE_GRID_KW`, 10,000 kW, for nothing.
- **Nothing burns fuel.** The coal plant takes 400 units of water (4M gallons a month) and 15 kW, and no coal. `CoalMining`'s header: coal plants "wait for the electricity run (Jerus's Q10: a coal plant at today's tariff would lose money on its fuel alone)"; `spec-materials.md` §5 lists "WOOD_PELLETS and THERMAL_COAL to power: the electricity run (Q10)".
- **Supply is a sum, and the month is one block.** `UtilitiesHandler.updateEnergyRatio()`: production is the rows' `production1` times the utility's staffing fill (one workforce for power and water), plus the works' surplus; `energyRatio = min(production / consumption, 1)`. A brownout multiplies every sector's output by that ratio (`Sector`: `averageFill × energyRatio × waterRatio × ...`), so a 10% shortfall at the peak hour and a 10% shortfall all month are the same thing to the model, and there is no peak hour.
- **The wind is firm.** The Wind Farm's 40% is counted as if it blew every hour, so the advice, which ranks every power candidate by its life cost per unit served (`BuildAdvice.running()`, `lifeFactor()`), finds wind cheapest almost everywhere (HY1's ★4: per unit of average power the Hydro Station costs 4.8 of cash, the Wind Farm 2.9, the coal plant 3.8).
- **The tariff is a constant.** `UtilitiesHandler.pricePerWatt = .01 × PriceLevel.FACTOR`: D$10 a kW a month at today's level, or D$10 ÷ 720 = **D$0.0139 a kWh** (derived). Nothing re-strikes it: it moves only when the currency is redenominated (`redenominate()`), and `Game` hands it to the sectors each month unchanged (`setPricePerWatt()`). So it falls with every year of inflation.
- **Who pays.** Only the business buildings a sector owns (`BuildingsTemplate.isBilledForUtilities()`): homes are not charged ("the tenants are not charged for utilities in this model"), nor is anything the city or the bank owns. The utility's income is those bills less its payroll less what it pays the works for their surplus, and it goes to the treasury (`ServicesManager.getServiceNetIncome()`).
- **The works that make power** (I2: Coke Ovens 8,000 kW, Blast Furnace 60,000, Oxygen Steel Shop 25,000, Pulp Mill 13,500) sell their surplus to the grid at the same tariff.
- **The fuels the game already has, and their buyers:**

  | good | made by | bought by | world price follows (`tools/world/prices.tsv`) |
  |---|---|---|---|
  | THERMAL_COAL | Coal Mining's three mines | the Pelletising Plant, the Blast Furnace, the two cement plants, the Lime Kiln | the Pink Sheet's Australian (Newcastle) coal |
  | FUEL_OIL | Refining (the residue cut with diesel) | the Paper Mill alone | crude |
  | DIESEL | Refining | the mines' trucks, the railway | crude |
  | LPG (refinery gas) | Refining | nobody: exported | crude |
  | WOOD_PELLETS | the Wood Pellet Mill (since 1970) | nobody: exported | Malaysian logs |
  | uranium | a map resource (`Resource.URANIUM`: 200 t a site, a mean field 2.5 sites and 505 t, one field per 5,000 km²) | nothing: "Uranium waits for its industry" (`Resource`) | none |

  Natural gas has no good ("Natural gas and LNG have no good in the game", `tools/world/goods.tsv`).
- **Dates.** `since` is on 41 rows (the Hydro Station's 1900 among them) and is "read by nothing yet" (`BuildingsTemplate`): the research batches will read it. EP1 (0.8.95) starts the game in January 1900 with today's technology (Jerus, 2026-10-30: "1900 start date with todays tech").

**Measured on Jerus's saves** (read as JSON, never written; the draw is each row's `electricity` less its `gen`, times the count):

| slot | year | people | draw | homes | businesses (billed) | the city's own | what generates |
|---|---|---|---|---|---|---|---|
| 3, Rotterdam | 2555 | 2,913,384 | 4.43 GW | 7.2% | 75.5% | 17.3% | 1,094 Wind Farms (8.86 GW average) and the works' 1.01 GW |
| 1 | 2150 | 1,729,091 | 3.02 GW | 4.5% | 83.3% | 12.2% | 525 Wind Farms, 2 coal plants |
| 2 | 1953 | 160,626 | 0.23 GW | 5.7% | 85.6% | 8.6% | 22 Wind Farms, 1 coal plant |
| autosave | 1905 | 359 | 259 kW | | | | 1 Wind Farm |

- A town of 359 people in 1905 stands on a 20 MW wind farm of 2020s turbines.
- Rotterdam draws 1.52 kW a head (derived: 4.43 GW ÷ 2.91M); its homes draw 7.2% of it, where US homes take 37.6% of retail sales [S1, AEO2026 table 8, 2025]. Slot 1 had 13 months of brownout in 3,001 (the worst at 57%), slot 2 31 in 642 (91%), Rotterdam none.
- Rotterdam's price index reads 857,556 against 1 at its founding (the history's `priceIndex`, across a thousand-to-one redenomination), and its tariff is the founding's, divided by that thousand: its power is all but free.

### 2.2 The month: three blocks and a merit order (new `PowerDispatch`, pure)
A month is cut into three blocks of the load duration curve: **base**, **shoulder** and **peak**. From EIA's hourly demand for the lower 48 in 2025 [S4], each month's own hours sorted and cut 10/40/50 and averaged over the twelve months [D]: the **peak** is the top 10% of a month's hours at 1.20 times the month's mean, the **shoulder** the next 40% at 1.07, the **base** the bottom half at 0.904 (so the three sum to the month: 0.1 × 1.20 + 0.4 × 1.07 + 0.5 × 0.904 = 1.000). The year's own curve is steeper (1.33, 1.07, 0.88) because the seasons are in it; here the season is applied apart. The year's peak hour is 1.549 times its mean (760,649 ÷ 491,034 MW) [D, S4].

**Each block is split into two halves of its hours** for wind and solar: one where they run at (1 + swing) times their block's average, one at (1 − swing). The swing is the hour-to-hour spread of their output inside each block, measured on EIA's hourly data for 11 US regions with wind and 12 with solar (the median coefficient of variation; `cv.py`) [M]: wind 0.59 to 0.64; solar 0.66 in the peak and 1.0 elsewhere (its spread there passes 1: half the hours are dark). Without the halves a block treats the wind as steady: the prototype's diesel town took 98% of its power from one wind farm; with them 58%, and 9% of the wind is curtailed [M].

**The order of a month,** each block (`PowerDispatch.dispatch()`, `Dispatch.java`'s `dispatch()` to port):
1. **The demand** is the draw `ServicesManager` already sums (every building's net draw, `consumption`, in average kW), times the calendar month's season (the US's 2025 monthly means over the year's [D, S4]: January 1.08, April 0.88, July 1.19, October 0.91; flipped by six months south of the equator ★), grossed up for the grid's losses (§2.7), times each block's load over the mean.
2. **What costs nothing to run goes first:** the works' surplus (I2's `gen`, must-run), then wind and solar, each at its capacity factor in that half-block (§2.4). What a block cannot take is **curtailed** and counted.
3. **Hydro levels the load.** A station's month of water (HY1's 45%, nameplate × 0.45 × 730 h) runs wherever the residual load stands above a level, found by halving; so it shaves the peak first and never leaves a block short to lower another's peak. (Measured: the greedy rule, peak first until the water runs out, left the base 185 MW short in the prototype's hydro city; the level leaves none.)
4. **The fuelled rows by marginal cost:** fuel at the city's price for that good (`SpreadPlanner.cityValue()`: the net import price if the city imported any, else the net export price, else the local price) × the row's heat rate ÷ the fuel's heat content, plus its variable upkeep. Each row offers its nameplate × count × the utility's staffing fill (as today) × its availability. The rows are kept sorted by insertion, since fuel prices move a little each month.
5. **The block's price** is the marginal cost of the last row it ran. A block that runs out of rows is short: its price is ★`VOLL` and its shortfall is counted.
6. **The month's sums:** each row's MWh and fuel; the fuel drawn from the goods' rows (§2.5); the energy served over the energy asked, which becomes `energyRatio`. So a peak block 10% short costs the month 1.2% of its energy (its 10% of hours at 1.20 × the mean: derived), where today any shortfall is taken from every hour.

**What it costs, measured** (`out/timing.txt`: Java 21 on Jerus's PC, an i7-13650HX shared with other sessions; the median of 7 rounds of 200,000 months after 5 of warm-up, the fuel prices moving each month, three runs):

| rows | ns a month (the three runs' medians) |
|---|---|
| 9 (this study's) | 1,360 / 1,964 / 2,115 |
| 18 | 2,839 / 3,808 / 4,686 |
| 64 | 4,750 / 6,435 / 6,709 |
| 512 | 21,086 / 22,948 / 25,437 |

At 16 months a second a month has 62.5 ms: the merit order takes **about 2 µs of it, 0.003%**. The advice's spread re-dispatches a year for each candidate row (§2.8), nine rows × twelve months × 2 µs, about **0.2 ms** [D], and only when it weighs a power order; it is cached on the month. Nothing is simulated by the hour, and nothing is stored but twelve months of books (§3).

### 2.3 The rows: today's plants (PW2)
Nine rows, three kept or refigured and six new, each today's (EIA's 2025 dollars [S1], or the source's year where it has none). Costs are US$ a kW of nameplate at today's level; the game splits a row's price into cash and materials by HY1's rule (materials 1.85% of cash, in units).

| row | id | `since` (Q2) | nameplate | builds for | fuel a MWh | heat rate, efficiency | runs | posts | cooling water | land | build time | life |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Coal Power Plant | 9, refigured | 1900 as a kind; a 325 MW unit first ran in 1960 (Eddystone) [H] | 325 MW (kept) | $3,271/kW [S8, coal-new, 2023$] = $1.06B (today D$1.43B) | 0.357 t thermal coal [D] | 8,490 Btu/kWh, 40.2% [S8] | available 85% ★ (NETL's design capacity factor [S9]) | 68, kept (fossil plants employ 29.7 a TWh [S13] × 2.42 TWh = 72 [D]) | 500 gal a MWh consumed with a tower [S5]: 10,085 units a month at full run (today 400); none on owned water, once-through | 200 acres (400 for 650 MW [S10]; today 46) | 60 months [S10] | 40 [S10] |
| Oil-Fired Plant | new | 1900 | 100 MW ★ | ★ $3,054/kW = $305M (NETL's subcritical coal plant [S9] as an upper bound: no source prices a new oil plant) | 283 L fuel oil [D] | 11,227, 30.4% [S5] | 85% ★ | 20 ★ | as coal | 30 acres ★ | 48 months ★ | 40 ★ |
| Diesel Station | new | 1910 ★ | 21 MW, four engines [S10] | ★ $1,810/kW = $38M (the gas-engine case, 2019$ [S10]) | 298 L diesel [D] | 10,847, 31.5% [S5] | 90% ★ | 3 ★ | none: closed loop [S10] | 10 acres [S10] | 30 months [S10] | 30 [S10] |
| Oil-Fired Turbine | new | 1949 (Belle Isle) [H] | 100 MW ★ (two of S&L's four aeroderivatives) | $2,275/kW = $228M [S1] | 260 L diesel [D] | ★ 9,447, 36.1% (its design figure on gas [S10], taken on diesel) | 90% ★ | 6 ★ | none [S10] | 10 acres ★ (20 for 211 MW [S10]) | 24 months [S1] | 40 [S10] |
| Nuclear Plant | new | 1957 (Shippingport) [H] | 1,100 MW ★ (one of an AP1000 pair) | $7,862/kW = $8.65B [S1] | 25.8 g U3O8 and $3.31 of fuel services [D, §2.5] | 10,445, 32.7% [S1] | 91% [S3] | 420 (nuclear plants employ 48.1 a TWh [S13] × 8.77 TWh [D]) | 661 gal a MWh consumed with a tower [S5]; once-through on owned water | 250 acres ★ | 84 months [S10] (EIA's model 72 [S1]) | 60 [S8] |
| Biomass Plant | new | 1970, with the Wood Pellet Mill | 50 MW [S1] | $4,843/kW = $242M [S1] | 0.750 t wood pellets [D] | 13,300, 25.7% [S1] | 85% ★ | 25 ★ | as coal ★ | 100 acres [S10] | 60 months [S1] | 40 [S10] |
| Hydro Station | 144, kept (HY1) | 1900 | 10 MW | $3,000/kW (HY1; EIA's model $3,280-3,627 [S1]) | none | | its month of water, 45% (HY1), by season (§2.4) | 5 | none | 10 acres | as HY1 | 50 [S10] |
| Wind Farm | 41, refigured | 1980 ★ (California's wind farms) | 20 MW (kept) | $1,712/kW = $34.2M [S1] (today D$31.0M) | none | | 33.8% a year [S3], by month, block and half (§2.4) | 2 (wind employs 21.8 a TWh [S13] × 0.059 TWh = 1.3 [D]; today 7) | none | 40 acres (kept) | 36 months [S1] | 25 [S10] |
| Solar Farm | new | 1982 (Lugo, 1 MW) [H] | 50 MW ★ | $1,484/kW = $74M [S1] | none | | 24.0% a year at the US's latitudes [S3]; by latitude ★ (§2.4) | 4 ★ | none | 333 acres (6.67 a MW [S10]) | 24 months [S1] | 35 [S10] |

- **What it costs to run beyond its fuel:** the game's own rules stand for EIA's fixed upkeep (the 1% a year every city building is charged for repairs, and its posts' wages: for the coal plant about $54 a kW-year against EIA's $89 [S8], derived from 1% × $3,271 + 68 posts at about $100,000); the variable upkeep a MWh (coal $9.37 [S8], oil ★ $9.68, diesel $5.69 [S10], turbine $5.97 [S1], nuclear $2.62 [S1], biomass $5.93 [S1], hydro $1.71 [S1]) becomes the row's consumables, bought abroad by the MWh as the works' are (I2).
- **The build time is a floor** (`buildMonths()`, new): Bromilow's rule (`BuildingManager.CREW_SCALE_EXPONENT`) gives a building its crew by its points, and no crew builds these faster than their months. A reactor ordered in 1957 stands in 1964.
- **The cooling gate** (`cooling()`, new): a thermal row (coal, oil, nuclear, biomass) stands only where the city owns sea, lake or river (`NO_WATER`, beside HY1's `NO_RIVER`), draws its cooling from it once-through and takes nothing from the city's water. Measured on EIA's plant data, a tower-cooled coal plant consumes 500 gallons a MWh and a once-through one withdraws 49,155 and consumes little [S5]; the city's fresh water limit (J2) could not carry the first. A dry-cooled row for a city with no water is later work.
- **The small rows** make a small town possible: the planner weighs a unit's whole cost against what the town can use of it (§2.8), so a 10 MW town builds a diesel station and a wind farm, not a 325 MW coal plant [M].
- **What stays out:** offshore wind (it would stand on owned shallow sea, as spec-oil's platforms do), the 4-hour battery, geothermal, a small coal station for 1900's towns (with research's dates), and co-firing pellets in a coal plant. Their figures are in the research note behind this study.

### 2.4 Wind and solar: when they run, and what they count for (PW3)
A variable row's output in a half-block is its nameplate × its capacity factor × its month's index × its block's share × (1 ± its swing):

| | wind | solar |
|---|---|---|
| a year | 33.8% (the US fleet, 2025 [S3]; the game's 40% today) | 24.0% (the US fleet, 2025 [S3]) at the US's latitudes; ★ by latitude, below |
| by month (index on the year) | January 1.11, March 1.28, July 0.81, September 0.68 [D, S3] | December 0.55, March 0.98, June and July 1.33 [D, S3]; flipped south of the equator ★ |
| by block: base, shoulder, peak | 1.081, 0.948, 0.806 [M: `vblocks.py` on S4] | 0.680, 1.136, 2.054 [M] |
| the swing inside a block | 0.64, 0.61, 0.59 [M: `cv.py`] | 1.0, 1.0, 0.66 [M] |
| credit alone (its output at the year's 100 highest-load hours over nameplate) | 0.247 [M: 0.730 × 33.8%] | 0.629 [M: 2.621 × 24.0%] |

- **The credit falls as they grow** ★: in a straight line from the figure above to a tenth of it when the variable rows make half the city's energy. The direction and the ends are NREL's (solar's average credit under 5% at half the energy, wind's falling with its share: TP-7A40-89587) and ERCOT's (tracking solar 72% first in, 6% last in); both are evidence only, their terms being unclear (§7), so the line is the study's estimate.
- **Solar by latitude** ★: the yield of a fixed panel falls from about 1,820 kWh a kWp a year at 30-33°N (Phoenix, Cairo) to 1,028 at London (51.5°N) and 708 at Reykjavik (64°N), and is lower again near the equator for cloud (Singapore 1,382) (PVGIS 5.3, the European Commission's [E-source: CC BY 4.0 by the Commission's notice, PVGIS's own wording silent on commercial use; to confirm before it ships]). The farm's 24.0% is scaled by the place's yield over the US's at 35°N, a short table by latitude band; a generated world has no latitude, and takes the US's. The swing between winter and summer grows with latitude (Berlin's worst month 0.34 of its average, Phoenix's 0.82) ★.
- **Wind has no map.** The Earth file carries no wind speed, so every place takes the US fleet's 33.8%; the national lab's classes run from 18% to 49% by wind speed [S8], which a later map layer could place.
- **Hydro's water by month** is HY1's 45% times the US fleet's monthly index (May 1.24, October 0.72 [D, S3]) ★ for every river; its credit is ★ 0.45, its water in a dry month, since a station on a river's flow cannot hold the peak through a drought.
- **Curtailment** is what a half-block cannot take, counted and shown (PW6); it is what tells the player the next wind farm is worth less than the last.

Measured in the prototype [M, `out/scenarios.txt`]: 2,200 MW of wind for a 1 GW city (your Rotterdam's ratio, 8.86 GW of average wind for 4.43 GW of draw) leaves **39% of the energy unserved** and curtails 14% of the wind; the reserve test then adds 1,625 MW of coal, 400 MW of turbines and 105 MW of diesel behind it, the wind still making 61% of the energy, and the tariff at cost is 16.3¢ against the coal city's 14.8¢.

### 2.5 The fuels: bought from the market, burned by the MWh (PW1, PW2, PW4)
A fuelled row draws its fuel from the goods' market each month, its MWh × its heat rate ÷ the fuel's heat content: from the city's sellers first, the rest imported, at the struck price, as the railway's diesel is drawn (`Markets.rowDrawsBeforeStrike()`, FW5). The utility pays, out of its takings, like any buyer.

| fuel | good | heat content | a MWh | its band today (import / export) | the row's marginal cost at the band's middle |
|---|---|---|---|---|---|
| thermal coal | `THERMAL_COAL` (Coal Mining's) | 25.12 GJ/t: the Pink Sheet's Newcastle grade, 6,000 kcal/kg [S12, D] ★ | 0.357 t [D: 8.957 MJ ÷ 25.12] | D$75 / 50 a t | $31.7 a MWh (imported $36.1, from the city's own mine $27.2) |
| fuel oil | `FUEL_OIL` (Refining's residue) | 41.81 MJ/L [D: 0.150 MMBtu a gallon, S11] | 283 L | D$0.4997 / 0.4256 a L | $140.8 |
| diesel | `DIESEL` | 38.46 MJ/L [D: 0.138 MMBtu a gallon, S11] | 298 L (engines), 260 L (turbine) | D$0.6883 / 0.5864 a L | $195.3 (engines), $171.1 (turbine) |
| wood pellets | `WOOD_PELLETS` (the Pellet Mill's) | 18.70 GJ/t at 8% moisture [D: 40 CFR 98's dry wood, 20.33 GJ/t × 0.92, S11] ★ | 0.750 t | D$220 / 150 a t | $144.8 |
| uranium | `URANIUM`, new: U3O8 by the tonne | | 25.8 g [D: 89.0M lb loaded over 1,565 TWh, 2024-2025, S7] | ★ $128,900 a t [D: $58.46 a lb, 2025's average price paid, S7, × 2,204.6] | $9.3, with $3.31 a MWh of conversion, enrichment and fabrication bought abroad [D: EIA's nuclear fuel expense $6.64 [S2] less the yellowcake's $3.33] |

- **The check against EIA's delivered prices** (2025 [S2]): coal $2.42 a million Btu against the game's band middle at $2.62; petroleum $14.12 against fuel oil's $11.68. The game's world prices already stand where the plants' fuel does.
- **The Q10 problem, in numbers:** the coal plant's coal costs $17.8-26.8 a MWh at the band's ends (0.357 t × D$50-75), against today's tariff of $13.9 a MWh (D$10 ÷ 720 kWh): the fuel alone is 1.3 to 1.9 times what the power sells for.
- **Who gains a buyer:** Coal Mining (a 1 GW coal city burns 3.28 Mt a year, 274,000 t a month [M], a third of a Large Surface Mine's 800,000); Refining's fuel oil, sold now to the Paper Mill alone; the diesel; the Pellet Mill, whose pellets are all exported today. Refinery gas (LPG) finds none in this run: no source gives a plant's heat rate on it; PW7's gas turbine can take it.
- **Uranium's world price** is not on the Pink Sheet. PW4 adds EIA's yearly average price paid by US reactor owners [S7, public domain] to the world file's series (`tools/world`), and before its first year the stand-in century's rule (EP1).
- **A mean uranium field holds 505 t** (spec-earth's hand-written figure) and a Nuclear Plant burns about 192 t of uranium a year [D: 21.9 g of natural uranium a MWh [S7] × 8.77 TWh]: a field is 2.6 years of one reactor. So a city's mine (a mine and mill on a site, `ExtractionSector`, its size ★ to a site's 200 t over a decade) sells mostly to the world, and most reactors buy abroad, as most nations' do.

### 2.6 The price: the tariff at cost (Q1)
**The rule:** each month the tariff is struck on the utility's last twelve months:

    tariff (a kWh delivered) = (fuel + payroll + repairs + capital + grid) over 12 months
                               ÷ kWh delivered over the same months  × (1 + the markup)

- **fuel** is what §2.5's draws cost; **payroll** the power side of the utility's wages (`UtilitiesHandler`'s split books, as today); **repairs** the 1% a year every city building is charged (`EconomyManager.maintenanceBillFor()`), on the plants; **capital** each standing plant's price when built, spread over its life at the real rate the city's debt costs (`BuildAdvice.lifeFactor()`'s rate) - so the plants pay the treasury back over their lives, as a municipal utility's rate base does; **grid** §2.7's substations and losses.
- **Twelve months, struck monthly,** as a fuel adjustment clause works: a fuel shock reaches the bills over a year, not in one month. With fewer than twelve months of books (a new city, a load), it strikes on those it has; with none, on today's `pricePerWatt`.
- **The markup** is a Policy dial, 0 by default (★ range −50% to +100%): below cost the treasury pays the difference, and cheap power is the city's subsidy to its industry; above, the bills are a tax on it.
- **Who pays:** the same buildings as today. The homes' share and the city's own buildings' share of the cost are the treasury's, as they are now (homes 7.2% of Rotterdam's draw, the city's buildings 17.3%: measured, §2.1). Billing the homes is Q4.
- **The works' surplus** is paid each block's price, not the tariff: what it saves the merit order (the avoided cost), at most the tariff.
- **The units stay:** `pricePerWatt` is still "a kW a month", the tariff × 720 kWh; every sector's `getElectricityCost()`, the advice's `running()` and the processing planner's `ownCosts()` read it unchanged.

**Why not the market price** (each block's clearing price, billed as it falls): a short month bills ★`VOLL` on every kWh, and the infra-marginal rent is a profit nobody in the city earns. **Why not keep a constant:** that is the Q10 problem: a constant cannot cover fuel that moves, and today's covers none of it (§2.5).

**Two classes** ★, from the price's own parts: the grid is 14% transmission and 30% distribution of the US's 2025 price (1.885¢ and 4.053¢ of 13.51¢ [S1, AEO2026 table 8, by service category]), and industry takes its power at high voltage, so **Heavy Industry and Mining pay generation and transmission**, the rest (commerce and light industry) all three. Measured on the prototype's 1 GW coal city: generation 8.9¢ a kWh, so industry pays 10.8¢ and commerce 14.8¢ [M]. The US's 2025 prices: industrial 8.51¢, commercial 13.37¢ [S2, EPA table 2.4]. Against today's 1.39¢, the bills rise 7.8 and 10.7 times; that is the move PW1 measures first (§6).

### 2.7 The grid (Q5)
The grid is two numbers and, if Jerus takes it, one row. No line is routed: a city's grid is as wide as the city.
- **Losses:** `GRID_LOSSES` of what is generated never reaches a meter: about 5% today [S6, EIA FAQ 105]; with use unaccounted for, 6.2% in 1912 and about 10% from 1920 to 1950 [H, HSUS S 131 over S 120], 14.5% of net generation in 1949 and 6.4% in 2000 [H, AER table 8.1]. 5% in this run; the curve waits for research's dates (Q2). The month's demand is grossed up by it (§2.2) and the utility pays for the lost fuel.
- **What the wires cost:** 5.94¢ a kWh delivered in 2025, 1.885 transmission and 4.053 distribution [D, S1, AEO2026 table 8]: 44% of the US price. The tariff carries it (§2.6), and it is spent, not banked: on the grid's posts, its repairs and its capital.
- **★ Its size, if it is built:** a kW of peak delivers 5,655 kWh a year (8,760 ÷ 1.549 [D]), so the grid costs $336 a kW of peak a year at 5.94¢ [D]; a Substation for each ★ 100 MW of peak is then $33.6M a year, which at ★ a third in posts and repairs and two thirds capital (4% real over 40 years) is about $440M to build [D]: the price of a district's lines, transformers and meters, not of one yard.
- **★ A Substation row** (Q5's recommendation): a city building, one for each ★`SUBSTATION_MW` of the peak hour, ordered by `AutoBuilder` as the roads are, to keep the peak covered; its price, posts and repairs are the grid's cost in the tariff, so the figure above is what it comes to at a typical load. It stands on the map, and a city without enough of them is held to what they carry (the brownout by blocks). The alternative is the charge alone, a dial in the tariff with nothing to build.
- **Not in this run:** a tie line to the nation's grid, buying and selling power across the city's edge at the nation's price (§6).

### 2.8 What to build: the reserve test, then the spread (`BuildAdvice`, `AutoBuilder`; PW3)
Today the advice ranks every power candidate by its life cost per unit served, which treats the wind as firm (§2.1). Two rules replace it for the POWER measure:
1. **The reserve test.** Firm MW (each row's nameplate × its credit: its availability for a fuelled row, §2.4's credit for wind, solar and hydro) must cover the peak hour × (1 + ★`RESERVE`) at the demand `HORIZON` months ahead (the advice's horizon today). While it does not, the advice orders the row cheapest a firm kW-year for the **slice** the gap lies in, the screening curve: a kW needed all year (base), the shoulder's and the peak's hours, or the peak's alone, priced as its capital at the real rate over its life, its fixed upkeep and its running cost for those hours, over its credit. **A unit bigger than the gap pays for its idle part:** its firm kW-year × its firm MW over the firm MW the gap can use. `BuildAdvice.shortfall(POWER)` becomes that gap and `isServed(POWER)` the test passed.
2. **The spread.** When the test passes, a row is still ordered if one more unit **saves the year's running more than it costs**: the year dispatched again with it (twelve months, a few µs each), the fuel and running it displaces and the shortfall it serves at ★`VOLL`, less its capital at the real rate and its fixed upkeep. The best saving per dollar first, one order a month as now: the spread rule spec-oil gave the refinery, priced by re-dispatch so that a unit is weighed by what it would really run, not by the prices before it stood. (Measured: weighed at the prices alone, a 325 MW coal plant looked best for a 10 MW diesel town; re-dispatched, it saves the town's diesel only, and the town builds a 21 MW diesel station and a 20 MW wind farm [M].)

What the two rules build, from nothing, for a 1 GW city [M, `out/scenarios.txt`; the tariff at cost of §2.6, all-in]:

| the city | what it builds | tariff at cost |
|---|---|---|
| today's menu, coal at the band's middle | 1,625 MW coal, 500 MW oil turbines, 63 MW diesel | 14.8¢ |
| coal imported (D$75 a t) / from its own mine (D$50) | the same | 15.3¢ / 14.4¢ |
| 400 MW of hydro sites, taken first | 975 MW coal, 900 MW turbines, 63 MW diesel, the hydro, 420 MW wind, 150 MW solar | 14.2¢ |
| an island with no coal | 1,200 MW oil-fired, 900 MW turbines, 63 MW diesel, 1,200 MW wind | 21.7¢ |
| a town of 10 MW | 21 MW diesel, 20 MW wind | 21.2¢ |

- **A shock** (oil × 2.76 and coal × 1.23 for a year: 2022 over 2020, EIA's delivered prices [S2]) moves the coal city's tariff **3.9%**, the island's **69%** [M]: the oil turbines barely run in the first and set every price in the second. The US's own 1974 shock moved retail power 25% while coal and oil rose 70-85% [H].
- **No nuclear plant** is built at the band's prices and 4%: the reactor's firm kW-year for the base, $643, loses to coal's $576 [M]. It wins where coal is dear or the real rate is low, and a carbon price would decide it (not in this run).
- **No wind or solar** is built in the coal city: coal's $31.7 of running is what their energy saves, less than their year costs. Hydro, by raising the city's shoulder prices, makes room for 420 MW of wind; oil makes room for 1,200 MW.

`AutoBuilder` follows the advice, as it does today, within the debt limit (power stays an essential service).

### 2.9 The 1900 city (Q2)
**What 1900 was** (the US, §7 [H]):

| | 1900 | 1925-1930 | 1950 | 1960-1965 | 2025 |
|---|---|---|---|---|---|
| coal a kWh | 7.05 lb (1899) | 2.03 lb (1925) | 1.19 lb | 0.88 lb (1960) | |
| fossil heat rate, Btu/kWh | about 92,500 (1902), so 3.7% | 25,175 (1925), 13.6% | 14,030, 24.3% | 10,453 (1965), 32.6% | coal 10,647, 32.0% |
| the average utility plant | 0.54 MW (1902) | 8.0 MW (1930) | 17.8 MW | 48 MW (1960) | |
| largest unit | 1.5 MW turbine (Hartford, 1901) | 208 MW (State Line, 1929) | | 325 MW (Eddystone, 1960) | 650 MW coal, 2,156 MW nuclear (EIA's designs) |
| hydro's share of generation | 36% (1902) | 30% (1930) | 26% | 18% (1960) | 5.6% |
| losses and use unaccounted for | 6.2% (1912) | 10.2% (1930) | 10.4% | 8.4% | about 5% |
| homes with electricity | 8% (1907) | 68% (1930) | 94% | 99% | |
| price, nominal ¢/kWh | homes 16.2 (1902); all 2.7 (1907) | homes 6.03 (1930) | homes 2.88 | homes 2.47 (1960) | homes 17.30, all 13.63 |

The efficiency is the finding: about a ninth of today's in 1900, a third by 1925, today's by 1965 and flat since (steam conditions met the metals' limit). Plant size grew a thousandfold.

**What it would cost the game** (derived, at the game's own factor of 1900 prices to 2025's, 38.6, EP1): a kWh of 1899 coal power burned 3.2 kg of coal; at 1901's US$1.05 a short ton at the mine that is 0.37¢ of fuel, **14.3¢ in today's money**, against 2.58¢ for today's coal fleet (10,647 Btu at 2025's delivered US$2.42 a million Btu): **5.5 times** today's fuel a kWh, with coal at much the same real price. A home's kWh sold for 16.2¢ in 1902, **US$6.25 in today's money, 36 times today's 17.30¢**; the all-services price of 1907, 2.7¢, is 7.6 times today's 13.63¢. In 1900 power was a luxury and few used it: a 1912 home took 264 kWh a year [H, HSUS S 108]; the game's House draws 8,640.

**The game's 1900 city** draws today's power per building (EP1: today's technology), and its town of 1905 already stands on a 2020s wind farm (§2.1). So the three ways:
- **A. Today's plants in every year** until research dates the demand too (EP1's rule). The rows carry their `since` as data and nothing reads it.
- **★ B. Each kind of plant from the year it was first built, at today's figures** (PW2 reads `since` for the ELECTRICITY rows alone): hydro and coal and oil from 1900, the diesel station from 1910 ★ (the first US diesel was built in 1898, and by the mid-1920s diesel sets were replacing steam in many towns [H, ASME #208, facts only]), the gas turbine from 1949, the reactor from 1957, the wind farm from 1980 ★ (California's first wind farms; over 1,000 MW by 1985 [H, a search summary, to open before it ships]), the solar farm from 1982. A 1900 city builds coal and hydro, as 1900 did; it never meets a 1900 coal plant's fuel bill, which on today's draws would make its power 5.5 times dearer in fuel alone. Research (R1) extends the gate to every row and brings the efficiencies of the table above with it.
Measured in the prototype [M, `out/scenarios.txt`], a 1 GW city allowed 1900's kinds (coal and oil, no river): at today's figures it builds 1,950 MW of coal and 300 MW of oil and pays **15.8¢** a kWh at cost; at 1902's heat rate, capital as today's, its coal costs $252 a MWh against $31.7, it burns 3.9 t of coal a MWh (35.8 Mt a year) and pays **39.1¢**.

- **C. 1900's own figures now:** the coal plant a city builds burns its year's coal a kWh (the curve above, a heat rate by year read at the order), and the power of a 1900 city costs about 5.5 times today's in fuel against today's draws per building, until research dates the demand. Not recommended alone.

## 3. The save
- **No format bump.** Ids 9, 41 and 144 keep their meaning; the new rows take the next free ids (148 on today's main) at the end of `buildings.json`; a refigured row is a balance change, not a wrong read. The new fields are absent from an old save, and Gson leaves them alone: the books start empty and the tariff strikes on what it has (§2.6).
- **The books** (a flow, so saved, CLAUDE.md's rule): twelve months of fuel, payroll, repairs, capital, grid and kWh delivered, in `DataSave` (`powerBooks`), with the markup beside the other Policy dials. The spread needs nothing saved: it dispatches the year again on the fleet and the prices it has. `HistorySave` takes the tariff, the fuel bill, each block's price and the energy by row as series for the screen (PW6). Each to `ReadPathCheck` and `SaveFileCheck`.
- **No fuel is stocked:** the month draws what it burns. A coal pile is a later refinement, if a strike or a blockade is ever modelled.
- **Uranium (PW4):** the good `URANIUM` (U3O8) is appended to `Good`; the mine's sector `Uranium Mining` is appended to `Sectors.KEYS`, last, as every sector since I6 was; its load path as theirs.

## 4. Premises that change, and the new assertions
| check | before | after |
|---|---|---|
| MaterialsCheck L745 | "the game's founding tariffs are the study's: D$10 a kW a month" | still at founding (the books are empty); the tariff struck from the books after (PW1) |
| MaterialsCheck L497-501, L1231; WoodProductsCheck 4 | the works' surplus "at the tariff" | at each block's price, at most the tariff (PW1) |
| HydroCheck L299 | the station's power is its `production1` | its nameplate × its month of water, levelled (PW1) |
| BuildAdviceCheck, AutoBuildCheck | power ranked by life cost per unit served | the reserve test, then the spread (PW3) |
| BuildingDataCheck, BuildMenuCheck | 148 rows | plus §2.3's rows (PW2) |
| CoalMiningCheck, OilCheck, RefineryCheck, WoodProductsCheck | coal, fuel oil, refinery gas and pellets with their present buyers | plus the power plants (PW2) |

- **PowerDispatchCheck** (PW1), every case on a fixture that causes it: a block short (its price ★`VOLL`, the month's ratio the energy served over asked, to the bit); wind past a block's load (the curtailment counted, nothing served twice); hydro levelling (the water used equals the month's when it binds, and no block left short to lower another); the order by marginal cost after a fuel price moves; generated + unserved = asked in each block; fuel = MWh × heat ÷ content to the bit; the works' surplus taken first.
- **TariffCheck** (PW1): the tariff is the books over the kWh × (1 + markup) exactly; a fuel shock fixture reaches the tariff over twelve months; the fuel's money reaches its sellers (Coal Mining, Refining, the world) and the money audit closes; a markup below cost is the treasury's line.
- **PowerPlanCheck** (PW3): the reserve test orders the slice's cheapest row (a fixture short at the base orders coal or nuclear, short at the peak a peaker); wind alone never passes the test (its credit); the spread orders a wind farm when the fuelled rows run dear and none when they run cheap; how often each fires in the default run, counted (README, *The checks*).
- **The traces move** at PW1 (the tariff, the fuel bills, the ratio by blocks), PW2 (the rows), PW3 (what is built), PW4 (from the first reactor) and PW5 (the grid's cost); byte-identical at PW6, the screen. Each move explained, the ensemble run where it moves.

## 5. The batches
**PW** for power: no batch, branch, note or commit uses the prefix (checked in `QUEUE.md`, `docs/notes/`, `docs/queue/`, `docs/studies/` and every branch's log, 2026-11-02). Each batch is a model slice a brief can pin down (`docs/batch.md` 3), with a version and its ship file; the traces move where the table says, each move explained, and the ensemble runs where they move.

| batch | what | files and methods | saved · checks | the trace |
|---|---|---|---|---|
| **PW1 ★ The merit order and the tariff at cost** | the month in three blocks; the Coal Power Plant burns thermal coal; hydro levels the load; the brownout by blocks; the tariff struck on twelve months of books (Q1) with a Policy markup; the works paid each block's price; `GRID_LOSSES` | new `PowerDispatch` (from `Dispatch.java`'s `dispatch()` and `use()`); `UtilitiesHandler` (`updateEnergyRatio()`, the books, `pricePerWatt` struck each month); `ServicesManager`; the coal draw beside the railway's diesel (`Markets.rowDrawsBeforeStrike()`); `Sector.getPowerSoldValue()`; `TaxPolicy` for the markup | `DataSave.powerBooks`, the markup · PowerDispatchCheck, TariffCheck; MaterialsCheck, WoodProductsCheck, HydroCheck premises | moves from month 1: the tariff and every business's power bill, the treasury's utility line, Coal Mining's buyers |
| **PW2 ★ The rows and their fuels** | §2.3's rows (oil, diesel, the turbine, biomass, solar, the small coal station) and the three refigured; nameplate and capacity factor for the variable rows; each fuel's row in §2.5; the cooling gate (`NO_WATER`); the build-time floor; `since` on every row (Q2 decides whether it gates) | `buildings.json`, `BuildingsTemplate` (`heatRate()`, `fuel()`, `cooling()`, `buildMonths()`, `credit()`), `BuildingManager`'s crew cap, `Game`'s gate beside `NO_RIVER`, `BuildCard` | none · BuildingDataCheck, BuildMenuCheck, BuildCardCheck; CoalMining-, Oil-, Refinery-, WoodProductsCheck buyers | moves from the first order of a new row |
| **PW3 ★ Wind and solar by place, and the planner** | capacity factors by latitude, month and block; capacity credits falling with their share; the reserve test and the spread in the advice and `AutoBuilder` (§2.8) | `PowerDispatch` (`credit()`), `BuildAdvice` (POWER's `shortfall()`, `isServed()`, the ranking), `AutoBuilder` | none · PowerPlanCheck; BuildAdviceCheck, AutoBuildCheck premises | moves: what every city builds for power |
| **PW4 ★ Uranium and the reactor** | `URANIUM` (U3O8) as a good; Uranium Mining (a mine and mill on the uranium sites, an `ExtractionSector`); the Nuclear Plant row; the fuel's conversion, enrichment and fabrication bought abroad as the reactor's consumables; uranium's world price in the world file | `Good`, `Resource.URANIUM.good`, new `sectors/UraniumMining`, `Sectors.KEYS` (last), `buildings.json`, `tools/world` (`goods.tsv`, `prices.tsv`, its series) | the new sector · UraniumMiningCheck (modelled on `CopperMiningCheck`), PowerDispatchCheck's reactor | moves from the first reactor or mine |
| **PW5 The grid** (Q5) | the Substation row and its order; the grid's cost in the tariff; the losses by year if Q2 dates them | `buildings.json`, `AutoBuilder`, `UtilitiesHandler` | none · TariffCheck's grid; BuildingDataCheck | moves: the tariff, the city's builds |
| **PW6 The power screen** (UI) | on Services: the month's merit order drawn as a supply stack against the three blocks, each row's run and fuel, the tariff's five parts, the reserve gauge (firm against the peak), the curtailed and the unserved; the markup dial on Policy | `PowerView` (pure, checked like `SectorFlow`), `ui/ServicesScreen`'s power section, `Levers` | `HistorySave` series · PowerViewCheck, words · none | byte-identical |
| **PW7 Natural gas** (Q3) | `NATURAL_GAS` as an imported good on the Pink Sheet's gas series, a gas terminal or a pipeline gate, the combined-cycle and the gas turbine rows | `Good`, `tools/world`, `buildings.json` | none · PowerDispatchCheck's gas; BuildingDataCheck | moves from the first gas row |
| **PW8 The homes pay for their power** (Q4) | a household line for power at the tariff, the homes' draw billed | `HouseholdBalance`, `consumption.json`, `BuildingsTemplate.isBilledForUtilities()` | its line · the household checks | moves from month 1 |

**The order:** PW1, then PW2 and PW3 (PW3 reads PW2's credits; one can follow the other in a lane), then PW4; PW5 and PW6 after PW1 in any order; PW7 and PW8 wait for Jerus's calls.

## 6. Still to decide, the estimates, and the risks
**Jerus's calls** (asked in the question box; each with its recommendation):
- **Q1, before PW1:** the price. ★ The tariff at cost (§2.6), or today's constant kept, or the market's block prices billed as they fall.
- **Q2, before PW2:** the 1900 city (§2.9). ★ Plants offered from the year they were first built, at today's figures; or today's plants in every year until research dates demand too (EP1's rule); or 1900's own figures now.
- **Q3:** natural gas (PW7). ★ An imported good on the Pink Sheet's series, with the combined cycle and the gas turbine; or none.
- **Q4:** the homes pay for their power (PW8). ★ Yes, as its own batch after PW1; or the treasury goes on paying it.
- **Q5, before PW5:** the grid. ★ A Substation row the city builds; or a charge in the tariff with nothing to build.

**★est. to confirm** (none blocks a batch; each with its reason):
- **The blocks' hours, 10/40/50,** the cut EIA's data was measured at; the base's 0.904 is 0.90 rounded so the month's energy sums.
- **`RESERVE` 15%:** NERC's reference margin, cited as evidence only (its reports reserve every right); real planning margins run 8% to 26%.
- **`VOLL` $3,000 a MWh,** read by the spread alone, never billed: high enough that a shortfall always pays for a plant.
- **The US's seasons, months, wind and sun for every place,** flipped south of the equator: the only open hourly data found is EIA's.
- **The credit's fall** to a tenth at half the city's energy (§2.4); **hydro's credit** 0.45.
- **The two-point swing** inside a block: an even split stands for a spread of hours.
- **Availability:** 85% for the steam rows (NETL's design capacity factor), 90% for the engines and the turbine (NERC's forced outage rates, 6.5-10%, as evidence), 91% for the reactor (the US fleet's capacity factor, sourced).
- **Thermal coal at 25.12 GJ/t** is the Pink Sheet's grade net as received, against EIA's heat rates on the gross value: the coal a MWh may be up to about 5% high.
- **Wood pellets at 18.70 GJ/t:** 40 CFR 98's dry wood at 8% moisture; no government figure for pellets was found.
- **The Oil-Fired Plant's price, upkeep, build time and size:** no source prices a new one (EIA treats oil steam as existing plant only), so NETL's subcritical coal plant stands as an upper bound.
- **The Diesel Station's price:** EIA's gas-engine case (2019 dollars) stands for a diesel one; its 1910 date.
- **The turbine's heat rate on diesel:** its design figure on gas; US oil turbines measured today run at 13,359 Btu/kWh, but they are old and start often.
- **Sizes:** the turbine's 100 MW, the reactor's 1,100 MW, the solar farm's 50 MW, chosen for the game's step; the substation's 100 MW and its split.
- **Posts** where no source counts them: the oil plant's 20, the turbine's 6, the diesel station's 3, the biomass plant's 25, the solar farm's 4 (each bounded by its fixed upkeep in dollars).
- **Land:** the reactor's 250 acres and the oil plant's 30.
- **Dollar years:** 2019 (the engines) and 2023 (the coal plant) taken as 2025's: the sources' own spread is wider than the years' inflation.
- **The prototype's real rate,** 4%, and its fuel at the band's middle: the game uses its own rate and its own prices.
- **The markup's range,** −50% to +100%.
- **The 1900 dates** of the diesel station (1910) and the wind farm (1980).
- **Solar by latitude** from PVGIS, its licence to confirm (§7).

**Risks:**
- **Every business's power bill rises** by the tariff's move (§2.6: 7.8 times for industry, 10.7 for commerce, in the prototype's coal city). Heavy industry feels it most: a Large Mini-Mill draws 77,000 kW. The processing planner and the advice read the new tariff the month it moves, so the trace will shed works that only paid at D$0.0139. Measure at PW1: the works shed, and Heavy Industry's and Copper Refining's margins, in the default run and on a copy of each of slots 1 to 3.
- **The homes are drawn light.** A House draws 720 kWh a month (derived: 1 kW × 720 h); a studio 72 and a low-rise flat 286 a dwelling. Homes are 4.5-7.2% of the measured cities' draw against 37.6% of US retail sales [S1]. It matters to Q4's bills and to the load's shape, not to the merit order: its blocks are the city's whole draw.
- **Uranium in the ground is small.** A mean field holds 505 t (spec-earth's hand-written figure): 2.6 years of one Nuclear Plant (§2.5). A city's mine sells to the world more than to its own reactor, and most reactors buy their uranium abroad, as most nations' do.
- **The coal plant's water** is 400 units a month today, against 10,085 for a tower-cooled 325 MW plant at full run (500 gallons a MWh [S5] × 201,700 MWh); PW2's cooling gate stands it on owned water instead, so a city that owns none cannot build one (nor a reactor), and says why.
- **Scale.** The month is O(rows × blocks); the advice's spread re-dispatches once per candidate row, about 15. Both cached on the month. Measured in §2.2.

**Measure first:**
1. PW1's tariff in the default run and on copies of slots 1-3, and what it does to the businesses (the works shed).
2. PW1's coal: tonnes a month bought, and Coal Mining's sales to the city against the world's.
3. PW3: what the default run builds, decade by decade, against §2.9's table.
4. PW4: a reactor's share of the fleet where it is offered, and the uranium the world sells the city.

**Not in this run:** a tie line to the nation's grid; storage (a 4-hour battery moves energy from the base block to the peak at 85% round trip, $1,521 a kW [S1]); offshore wind on shallow owned sea; co-firing pellets in a coal plant; carbon (a price per tonne of CO2 is a Policy dial the merit order would take for free: coal emits 0.815 t a MWh at the new plant's heat rate [D: 8.49 MMBtu × 95.99 kg, EIA's coefficients, S2]); demand that answers the price.

## 7. Sources and their licences
Every page was read on 2026-11-02 by three research agents (their notes and the figures they computed stay in this session's scratch folder, not shipped) and checked here. A figure from a source marked *evidence* never reaches the game.

| | source | licence | commercial use | credit asked |
|---|---|---|---|---|
| S1 | EIA, *Annual Energy Outlook 2026*: the Electricity Market Module assumptions, table 3 (cost and performance of new plants, 2025 dollars), https://www.eia.gov/outlooks/aeo/assumptions/pdf/EMM_Assumptions.pdf; table 8 (sales by sector, prices by service category), https://www.eia.gov/outlooks/aeo/excel/aeotab8.xlsx | US public domain (https://www.eia.gov/about/copyrights_reuse.php) | yes | "Source: U.S. Energy Information Administration" with the date |
| S2 | EIA, *Electric Power Annual*: tables 2.4 (prices by sector), 7.3-7.4 (fuel received, its heat content and cost), 8.1 (heat rates), 8.4 (nuclear fuel expense); EIA's CO2 coefficients | US public domain | yes | as S1 |
| S3 | EIA, *Electric Power Monthly*, tables 6.07.A and 6.07.B (capacity factors by type and month, 2016-2025) | US public domain | yes | as S1 |
| S4 | EIA-930, the Hourly Electric Grid Monitor, 2025 (`EIA930_BALANCE_2025_*.csv`): demand, wind and solar by balancing authority and hour | US public domain | yes | as S1 |
| S5 | EIA Forms 860 and 923, 2025 final: heat rates by fuel and prime mover, cooling water, unit sizes (computed by the research agent from the files) | US public domain | yes | as S1 |
| S6 | EIA FAQ 105, transmission and distribution losses | US public domain | yes | as S1 |
| S7 | EIA, *Uranium Marketing Annual Report* (2025 data, released 2026): uranium loaded and bought, prices, enrichment | US public domain | yes | as S1 |
| S8 | NLR (the national laboratory once NREL), *2025 Annual Technology Baseline*, its data (`ATBe.csv`, OEDI submission 8759) | CC BY 4.0 | yes | "NLR. 2026. 2025 Electricity Annual Technology Baseline", and the licence |
| S9 | DOE/NETL, *Cost and Performance Baseline for Fossil Energy Plants*, vol. 1 (DOE/NETL-2025/5313), https://www.osti.gov/biblio/2580491 | a US government report with no reservation | yes (likely) | the report number |
| S10 | EIA / Sargent & Lundy, *Capital Cost and Performance Characteristic Estimates for Utility Scale Electric Power Generating Technologies* (January 2024; January 2020 for the engines) | published by EIA with no copyright notice; written by a contractor | unclear, likely yes: numbers only, and EIA's own S1 used wherever it has the figure | EIA / Sargent & Lundy, the date |
| S11 | 40 CFR 98, subpart C, table C-1 (heat contents and CO2 factors of fuels) | US public domain | yes | none asked |
| S12 | World Bank, *Commodity Price Data* (the Pink Sheet): the Newcastle thermal coal grade | CC BY 4.0 (`licences.md`) | yes | "The World Bank: Commodity Price Data (The Pink Sheet)" |
| S13 | BLS, *Occupational Employment and Wage Statistics*, May 2023, NAICS 221111-221115 (workers in hydro, fossil, nuclear, solar and wind generation) | US public domain | yes | the BLS as the source |
| H | *Historical Statistics of the United States, Colonial Times to 1970* (Census Bureau, 1975), chapters S and M; EIA, *Annual Energy Review 2011*, tables 5.21, 7.9, 8.1, 8.2a, 8.10, A6; US Bureau of Mines, *Minerals Yearbook* (1899's 7.05 lb and 1913's 4.02 lb of coal a kWh, read from a search summary: to open before they ship) | US public domain (federal publications; HSUS names EEI and *Electrical World* among its upstream compilers, and its figures are compiled facts) | yes | the Census Bureau and EIA as the sources |
| H | ASME's landmark pages (the first turbines, Belle Isle, Philo 6, Shippingport, Port Washington); Wikipedia | ASME: all rights reserved; Wikipedia: CC BY-SA 4.0 | dates and ratings are facts: yes; their wording: no | none: facts only |
| evidence | NERC (reference margins, GADS outage rates), PJM and ERCOT (capacity credits), NREL's technical reports (TP-7A40-89587 on credits; the water, land and jobs reports), LBNL's wind and solar reports, IEA/NEA *Projected Costs 2020*, OECD NEA/IAEA's *Red Book* | all rights reserved, no licence stated, or contradictory | no or unclear: not shipped | - |
| to confirm | PVGIS 5.3 (European Commission, JRC): solar yield by latitude | the Commission's content is CC BY 4.0 "unless otherwise indicated"; PVGIS asks only for its source to be named | probably yes; confirm before PW3 ships, or fall back to the US fleet's figure everywhere | "PVGIS © European Communities" |

**Not used:** Lazard, the World Nuclear Association, BloombergNEF (their terms forbid it or were never read). The research notes list what each agent could not open.
