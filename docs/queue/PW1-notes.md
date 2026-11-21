# PW1 - power by the merit order at the market's price ({next}, 2026-11-11)

The brief is PW1's row in `QUEUE.md` and [the electricity study](../studies/spec-electricity.md) §2.2-2.7 and §3,
Jerus's Q1 (2026-11-02: "Market price"), with its prototype `docs/studies/electricity/Dispatch.java`. The row folds
in PW5 (substations) and PW8 (homes pay for their power). A model batch: Opus, with Sonnet slices for the code.

## 0. Scope (decided at the start)

PW1's core ships here. **PW5 and PW8 go under Pending** in the ship file:

- PW5 (substations, one per 100 MW of peak, by the auto-builder, their cost the wires' part of the bills) is a new
  `buildings.json` row and an AutoBuilder rule that changes every city's builds; the wires' cost as a building wants
  the bills PW1 measures first, so it is priced from them, not guessed beside them.
- PW8 (a household power bill) touches `HouseholdBalance`, which HL1 (the households' books closed) is changing in a
  session beside this one.
- The south of the equator's season flip (spec 2.2) waits for PW3: nothing in the tree hands the dispatch the city's
  latitude yet. Every city reads the northern calendar.

## 1. The design (as built in slices A-F)

- **The month**: `PowerDispatch` (new, pure) cuts it in three load blocks (base 50% of hours at 0.904 of the mean,
  shoulder 40% at 1.07, peak 10% at 1.20), each in a windy and a calm half. In each half-block: the works' surplus
  first (must-run), the wind at its block's share x (1 +/- swing) with the excess curtailed, the hydro levelling the
  residual above a level found by halving (its month of water under its nameplate cap), then the fuelled rows by
  marginal cost. A half-block's price is the last fuelled row's marginal; a short one clears at VOLL ($3,000/MWh).
- **The struck book** (the timing): `updateServices()` runs twice a month and on load, so the dispatch is pure. The
  dispatch at the end of month t-1 is struck in month t's `SimulationEngine.updateEconomy()` (`strikePower()`): its
  ratio is month t's energy ratio, its prices what month t's power is billed at when the statements strike at the top
  of month t+1, its fuel what the utility buys from the market just before that strike and pays in t+1's
  `servicesNet`. One dispatch for the ratio, the bills and the fuel. Saved (`DataSave.powerBooks`); a load hands the
  struck ratio and prices, never re-strikes.
- **The bills**: commerce and light industry pay the energy price plus both wires (AEO2026 table 8: 1.885¢
  transmission, 4.053¢ distribution); Heavy Industry and Mining the energy plus transmission. The works' surplus is
  paid its half-blocks' prices. Until the first strike, the founding tariff (D$10 a kW a month) for all three.
- **The fuel**: the Coal Power Plant (id 9) is 325 MW at 8,490 Btu/kWh, burning thermal coal at 25.12 GJ/t (0.357
  t/MWh) bought through `Markets.draw` as the city (the mines' stock first, the world's for the rest), plus $9.37/MWh
  variable upkeep paid abroad; hydro $1.71/MWh. The audit's two TRADE lines: `utilities FuelImports`, `utilities
  PlantUpkeep`; the local coal is the treasury paying Coal Mining, pool to pool.
- **The advice**: the build advice plans power against the calm peak: a Wind Farm counts `WIND_FIRM` (0.33) of its
  average, the demand `PLAN_PEAK` (1.20 / 0.95) of the mean; with yellow at 0.75 about 12% over July's peak block.
  Without it the advisor's wind cities brown out in every calm half-block. `unit()`, `built()`, `figure()` and
  `supplyDemand()` carry it, so the Build ring's power reads firm supply over the peak's draw (the advice's own
  figure), and a Wind Farm's card its firm kW. The NEEDS YOU row (`CityNeeds`) reads the same pair, worded "81% served
  at the calm peak" (`CityNeeds.AT_THE_CALM_PEAK`). On the average it read a city of wind farms clear while its calm
  half-blocks were half served, and the Build screen's cards, which the rows drive, never offered power (slice F).
- **The playtest's player buys power as the advice says** (`LongPlaytest.powerByNeed()`), as it buys roads: the advice's
  card and the next in its ranking, each valued at the output at risk: the month's ratio short, or the share of the calm
  peak's draw the firm supply leaves uncovered at the advice's yellow line (`1 - served x NETWORK_YELLOW`), whichever is
  larger, so it orders when the advice does. Before, it offered a Wind Farm and a Coal Power Plant by one throttle at the
  same value, the wind farm listed first: on 1395e06c its city built 103 Wind Farms and no coal by month 2,400, and from
  month 960 was short in 2-3 calm half-blocks in 164 of 240 sampled months, its energy at the cap. Valued at the shortage
  alone (`1 - served`) it ordered a plant only once a block was short: 66 of 240 sampled months short.
- **The fuel at the margin** (`Game`, the fuel price source): the import price if the city imports the coal, the
  export netback if it exports it, its own market's price if its own mines sell it, else the import price.
  `SpreadPlanner.cityValue()`, the refiners' first unit's worth, priced a city's coal at D$1.82 a tonne (the export
  netback of a good it trades none of) while its plant would import at about D$75.

- **The screen** (Services, the power statement and the utility's books): the three prices and the energy's a MWh,
  "Still at founding" until a month clears, the coal burned, the wind and the works' surplus turned away and the load
  not served (in how many of the six half-blocks), each line only when there is some; the books carry the power
  bought, the fuel at home and abroad and the upkeep abroad, and the net is the model's `getElectricityIncome()`. The
  build card's power sold reads "at the works' price". The power screen of its own (the blocks, the series) is PW6's.

- **The founding grid** (`BASE_GRID_KW`, 10,000 kW, "for nothing", spec 1.2) stays free: a fuelled row at marginal 0. A town
  it covers clears at an energy price of 0 and pays the wires alone; its works' surplus is paid nothing. Three fixtures
  (MoneyCheck, MaterialsCheck's power town, WoodProductsCheck 4) raise the homes' draw so the coal plant is the margin.

## 2. Measured

The probe city (`LongPlaytest.probeCity()`, the default playtest's), 2,400 months on 12112224 with the player at every
turn (`LongPlaytest.probePlay()` to each month, the city read at each turn's end); main's on 0a5b74ef (one batch
before 0.9.93) for its energy ratio alone. Money is the city's, in the founding's dollars as the game strikes it.

- **Short**: on main the energy ratio was under 1 at every turn from about month 600 to the end (0.88-0.96 at each
  decade), 370 short turns in all. On PW1 a half-block was short at 31 turns, all before month 607 (22 before month 300:
  the founding town past its free 10 MW while its first wind farms build), and at none after; the worst ratio 0.625.
- **The plants**: 52 Coal Power Plants and 70 Wind Farms at month 2,401, for 4.33 million people drawing 8,611 MW
  (main: 4.22 million, 8,284 MW). Wind first (22 farms by month 244, all 70 by 361), coal from 361. Coal's share of
  what is served rises from 21% at month 481 to 93-94% from 2,161; wind's falls from 74% to 6%.
- **The prices**: the energy $0-25 a MWh from month 244 to 1,801 and $43-156 after, as the city's money falls against coal
  ($48-387 a tonne). No turn after month 607 clears at the cap; the dearest is $164 a MWh at month 2,293, coal's
  margin. At month 1,201 the energy is $5.53 a kW-month ($7.68 a MWh): commerce pays $15.44 (both wires), industry
  $8.68 (transmission), and the works are paid $5.25 for their flat surplus.
- **The coal and the margin**: all of it imported (the probe city mines none). Over the run the fuel bought abroad is
  41% of the power's revenue, the upkeep abroad 14%, the works' surplus bought 9%; the utility keeps
  36.3% (20-60% a decade), the wires' parts of the bills being in no cost until PW5's substations.
- **Wind turned away**: 8.8% of the wind's energy over the run, but 95% at month 244 and 71% at 361. The works' surplus
  (80% of what was served at month 244) runs first, and the advice does not count it as supply, so the town built wind
  for load the works already covered (pending, PW1-P).
- **The plant's build**: on another path of the same city (the player every 15 months, `PlayerProbe`), the ratio fell
  to 0.857 at month 710 while its first Coal Power Plant built (ordered by month 620, open at 725, the draw 67 to 173
  MW), and to 0.97 while the next two built (785-860). The advice sizes an order to its opening and counts the plant
  under construction as on site, so nothing bridges the wait (pending, PW1-P).
- **The ensemble**: 24 of 26 (`docs/ensemble/PW1.md`; 0.9.93 23). A3 and R3, main's reds, pass. A2 is main's (the
  longest spell 18 months, 32 on 0.9.93). **R2 newly fails**: HIGH's unemployment over its twin is 0.32 points against
  at least 0.50 (0.83 on 0.9.93), the seeds -8.81 to +13.04 (★2).
- **After §3's fixes** (94bc714c: the founding grid on the books, the Trade tab's goods, ScaleCheck): the ensemble
  again 24 of 26, every figure the same to the digit, and the probe city's 2,400 months the same, 31 short turns; the
  playtest's player builds its own power at founding, so neither trace moved.

## 3. Found on the way

- **ReloadCheck's `.mapStamp` red is main's** (a fault in the map, not this batch's code). A month after a load, the
  reloaded map lays rail on plots the live map leaves bare (kind 5, `TilePainter.RAIL`), with the same builds in both and
  the two maps byte-equal at the load. There are no reserves and no mined sites in either city. On PW1's earlier commits it
  was district 3, 6 plots, at month 1205; on the final tree (94bc714c) ReloadCheck is green, its path missing it. Clean main (0a5b74ef) shows the same at month 2105 (district 11, 2 plots), once the probe follows
  ReloadCheck past its last checkpoint. PW1 changes the probe city's builds, so the fault shows at 1205. The probe is in
  the orchestrator's notes folder, `PW1-map-rail-after-load/`. A fix batch is queued in the ship file.
- **The plants' upkeep was read in today's dollars** (found measuring the default run, fixed here): `buildings.json`'s
  US$9.37 a MWh went into the dispatch over 1e6 times the market's exchange rate, without `PriceLevel.FACTOR`, so it was
  38.6 times too dear: $523 a MWh at month 1,206 against $22 of coal, nine-tenths of the coal plant's marginal and of the
  energy price. It is struck as a good's world price is now (x FACTOR), and the exchange rate (which carries the world's
  price level, `GoodsMarket.setExchangeRate`) makes it the month's city money. PowerPriceCheck's premise had copied the
  code's own formula; it checks the row's upkeep against FACTOR first now.
- **AuctionCheck section 10's flat-price premise is false on main too** (slice G's trace). The fixture holds the
  builders' order multiplier at 1 but not their wages, which rise 1.8% a month (wage index 1.1326 to 1.1533, months
  39-40, the same on both trees), so each ask's cost drifts 0.4-0.6% a month. The "a granule's top-up" assertion passes on
  the rounding remainder each ask holds after its first whole-granule sale: on main two top-ups against a bound of three;
  on PW1 three, exactly the bound (the cheaper power bill moves the Paved Road 0.1%). It was red on PW1 while the upkeep
  was too dear (the Health Centre's top-up two granules). A fix is queued in the ship file; the bound is not loosened here.
- **The founding month read power as short** (main's, which PW1's ranking made costly; fixed here). The auto-builder's
  founding step runs before the first services update, so the founding grid stood at 0 kW and the advice's power figure
  was 0 over 0, Infinity: every founded city ordered a plant in month 1. On main that is a Wind Farm; on PW1's ranking
  (firm kW, a Wind Farm counted a third) a Coal Power Plant, 27.7k of an island's 64k cash and 125.7 months to build, so
  the island's port link waited and IslandLinkCheck 9 and LandmassCheck 12 went red. `UtilitiesHandler.baseProduction`
  now opens at the grid's 10,000 kW: a founded city orders its first plant when its draw nears the grid. Water is as it was.
- **The Trade tab's goods missed the power plants' imports** (PW1's; fixed here). The coal bought abroad and the upkeep
  were on the balance of payments (the audit's two utility lines) but in no good, so the table stood 9k a month short
  (ForeignCheck, RailCheck). They are the power plants' now (`Sectors.UTILITY`): the coal among its good's buyers, the
  upkeep an import with no good, and the tab names them "the power plants".
- **ScaleCheck section 9's prices** (PW1's path, not its dispatch). The scaler now scales the save's power book (the
  copy had bought one city's coal; nothing moved, since the town burns none). The miss was a knife edge: at month 172 the
  town's shops hand over 9.9 thousand baskets a K against 9.5 thousand wanted, the road ratio the limit; the copy's road
  ratio ran 3% lower (its cars, the granule ScaleCheck documents), it fell short, and its grocery clearing price went to the
  band's top (0.0153 against 0.00024), while the power book tracked to five digits (`ScaleProbe`, scratch). The month is
  re-picked as GW1 and WG2 did, by a scan of every line at 160-200: 177, the middle of 174-180.
- **Two reds are main's, passed by name**: HealthCheck's "...and it buries more of its people over the run" (15.72
  against 15.72 deaths a year per 1,000; main 15.67 against 15.68: the fee twins differ by hundredths, HX1 and RN1-F),
  and KeptCheck's "the BuybackLadder on Close it now: no month draws it again with nothing changed in it (1)" (KL1: the
  ladder compared bit for bit; on PW1's path KeptCheck's city has paper to close, so the section is reached at all).
- **CallCheck section 4's sale found no bank** (PW1's path, the gate's run 1 on 0.9.94; the fixture restated). The two
  years before the sale ran the town's Manufacturing, on 1.9 of cash, into a 5,750 loan for a second Wire & Cable Works
  at month 90; it defaulted two months on and the bank failed (equity -1,013, in resolution), so at the sale its room
  was 0 (24,539 on main) and both quotes stopped short at the cap's tail, the households alone taking 259 of 1,492. The
  firms now build nothing new over those two years and the bank's standing is asserted before the sale: green on PW1
  (room 40,928) and on 0.9.94 (33,261). A bank that lends a sector with 1.9 of cash 5,750 at once and fails on it two
  months on is main's lending, not this batch's; not queued, a note for whoever takes the bank next.
- **PlanCheck 17 on PW1's path is PX1's class**: "every district as the map draws it keeps every check" fails in
  MapCheck 1's city at month 121, district (0, 0), on the + floor (the closest + 5 plots from another, `LATTICE` 8),
  with 14 streets along a cut drawn as curves - PX1's city, month, district and case (CL4's curves), which 0.9.94 shows
  in PlanCheck 6. PX1's done-means should add PlanCheck 17 green on PW1's path.

## 4. The calls (★), each taken as recommended

- **★1 The founding grid stays free** (`BASE_GRID_KW`, 10,000 kW, spec 1.2 "for nothing"): a fuelled row at marginal 0,
  so a town it covers clears at an energy price of 0 and pays the wires alone. Recommended and taken: priced at a coal
  plant's marginal it would bill a founding town for fuel no plant burns. The other way is one dial.
- **★2 Ship with R2 red** (the ensemble, §2): 24 of 26 against 23, R2's median 0.51 points lower with seeds 21.85
  points apart. Recommended and taken: nothing in PW1 aims at the labour market under HIGH, and a cause found by moving
  the power model back would undo what it fixes (main's city short at every turn). Pending as PW1-E for the next model
  batch's ensemble to say whether it holds.

## 5. The checks

- **New**: `PowerDispatchCheck` (the dispatch alone, 1-10: the blocks sum to the month, generated + unserved = load, a
  short half-block at VOLL, wind past a block's load, the calm half, hydro levelling, the merit order, fuel a kWh, the
  works first, the struck book); `PowerPriceCheck` (a coal town through the game, 0-7: the struck price is the
  dispatch's, each class's bill exactly, a short half-block bills the cap, a fuel shock moves the next month's bills,
  the utility's margin is the treasury's line, the fuel's money reaches its sellers, the audit closes; the plant's
  upkeep against `PriceLevel.FACTOR`).
- **Added sections**: `BuildAdviceCheck` 14 (the calm peak: a wind farm's firm kW, the peak's draw over the firm
  supply) and 15 (the NEEDS YOU row on the calm peak).
- **Premises restated, not loosened**: `BuildCardCheck` (power served read through `BuildAdvice.served()`), and three
  fixtures whose town the free founding grid would cover at a price of 0 - `MoneyCheck`, `MaterialsCheck`'s power town,
  `WoodProductsCheck` 4 - raise the homes' draw so the coal plant is the margin. `ScaleCheck`: the scaler scales
  `powerBooks`, and `FREE_FIXTURE_MONTHS` is 177 (§3, the scan). `CallCheck` 4: the firms held over its two years and
  the bank's standing asserted before the sale (§3).
- **Not done here**: the write-up for the project (`claude/<title>.md`; this batch changes how the game plays). This
  session has no way to write to the project; sections 1-4 are its matter.

## 6. The takeover (PW1c, 2026-11-20 to 21)

Jerus took the hold off ("Yes, un-hold PW1"); PW1c took the branch at 3d5d27519 (cut on 0.9.94) to its gate.

**The merges.** Two, each one commit: 6e4112018 (main at 1557daaaf, 0.9.173) and d7f1f89d1 (main at 4f1056f11,
OE1's 0.9.174, FS4 and after). The generated docs are main's, regenerated. The conflicts that needed thought:
- `CallCheck` 4: FS14 rewrote the section (`saleOf`, `handBankRoom`). FS14's plays kept, with PW1's held firms
  around them and the bank's standing asserted before the sale (the PW1 fixture of §3, restated on FS14's form).
- `MoneyCheck`: main folded it into `LongPlaytest` (bb03a1fae). PW1's fixture, the homes drawing 50 kW so the coal
  plant is the margin, carried into LongPlaytest's "a works making its own power and buying consumables abroad".
- `MoneyAudit`, `EconomyManager`: PW1's utility imports (FuelImports, PlantUpkeep) beside R2c-P's city IP charges;
  both enter the raw imports and both reset with the month.
- `EquipmentFit`: R6d's electrolyser gain at the industry price (`getPowerPriceFor(t)`), as PW1 prices the steam.
- `BuildAdvice`, `AdviceValueOracle`, the four sectors: QN1's `NewUnit` form carrying PW1's firm wind and price
  a template's class; OE1's `plannedRate(u)` beside PW1's `powerPriceFor(t)` (the second merge's three conflicts).
- `ScaleCheck`: FS2's `FREE_FIXTURE_MONTHS` 225 with `FREE_FIXTURE_SEARCH` 60 stand (§5's 177 is superseded);
  `powerBooks` stays in the scaled fields. Main's long counts reach PW1's plant maps (`UtilitiesHandler`,
  `ServicesManager`, `PowerDispatchCheck`, `BuildAdviceCheck` 14-15).

**Old saves.** Today's floor and format are 58 and 58. PW1 adds one field, `powerBooks`; a format-58 save without it
loads the founding book's prices (the founding tariff, main's flat price) with the ratio its load dispatched, as main
handed the sectors its computed ratio (REV-PW1c, below), until the next month strikes a book. No bump.

**Main's new harnesses, restated to cause their premises under PW1** (each a fixture, none a loosened bound):
- ★ `EssentialWorksCheck.powerDrawAt(g, share)`: the draw at which the advice's own reading (the calm peak's draw
  over the firm supply) is `share` of supply, found from two readings of `supplyDemand`. Main's fixtures set the
  physical draw a fraction under production, which PW1's advice reads as short. Used for its "served with 5% to
  spare" and "a fifth inside" towns and for WorksFinanceCheck's highway town.
- `EssentialWorksCheck`: the forecast step is `PLAN_PEAK` times the mill's draw; the overdue section builds
  `ceil(100 / WIND_FIRM)` farms where it built 100.
- ★ The Wind Farm built `1 / WIND_FIRM` larger in `EssentialWorksCheck` interim power and `WorksFinanceCheck`'s
  highway promise, so its firm part is the nameplate each was written against; the sections are the timing and the
  shared site, not wind's firm price. Measured first: in the highway town (290,000 kW of utilities at a pooled
  fill of 0.69) one farm's posts lower the pooled fill more than its firm third adds (firm supply 199,387 kW, 199,015
  with one farm, 198,884 with two), so the advice proposed no reserve (now in PW1-P); in the interim town the auto path
  ranked a coal plant (20-year cost $192.6M, opening at 104 months) over wind ($284.8M with 212 later farms, opening at
  29), the prevented loss $1.5M against $1.8M (WorksValue's lost value added, small in a town of little industry).
  That is HW1's ranking reading PW1's firm price as designed.
- ★ `ElectrolyserCheck` 7: the grid read at the peak block (`PowerDispatch.PEAK`), the plants cleared so the
  production written stands, and the served share summed over the half-blocks, the ratio PW1 strikes.
- ★ `HydrogenSteelCheck`: Heavy Industry pays the industry tariff, the energy and transmission.
- `NewUnitCheck`: the expected figure from the advice's own `supplyDemand`, the calm peak.
- `PublicLicenceCheck`, both desalination sections, are main's (REV-PW1c restored them; PW1c had set the plant's
  draw to 0, which moved the premise). At the market's price the commerce bill carries both wires (4.3 times the
  founding tariff in that town) and the plant's water at the water tariff, a constant, no longer covers its power,
  so a patented plant earns no licence: four reds, passed by name in the ship file's known-reds, naming PW1-W.

**What each check showed** (one harness at a time, `./batch-check.sh <name>`, the merged tree). Before the mends, NEW reds only in main's new harnesses above; `BuildAdviceCheck` red only on main's
streets' card line (`docs/notes/gate.txt`, FS12's in known-reds); LongPlaytest, holding MoneyCheck's audit, green.

**The review.** The merge changed what PW1-R read: R6d's electrolyser saving nets off the draw the grid is asked
for (`ServicesManager.lessElectrolysers`), which on PW1 is the load the dispatch clears and the kWh each bill strikes
on; the audit's scope gained the city's IP charges beside PW1's fuel and upkeep; MoneyCheck's fixture now runs inside
LongPlaytest. Review: needed - money.

**The rows.** PW1-W added (the water tariff). PW1-P gains the pooled-fill gap. MR1 (the map's rail after a load) is
renamed ML1, MR1 being main's own batch since. AU1 dropped: FS15 rewrote AuctionCheck 10 to cause its premise. The old
known-reds for HealthCheck and KeptCheck dropped (HX1 and KL1 shipped).

**REV-PW1c** (money, on 39c5b7819: not SHIP, two P1), mended:
- ★ A works' power at its class's price everywhere a licence or a recipe prices it. `LicenceFees` priced a mine's or
  a works' own generation at the commerce price (`getPricePerWatt()`), and `Recipes.pricesOf(game)`, which the recipe
  fees, `RecipeChanges` and `EquipmentFit`'s recipe gain read, priced every recipe's power so. Now
  `owner.powerPriceFor(t)` and `Recipes.pricesOf(game, t)` (`getPowerPriceFor(t)`: Heavy Industry and Mining the
  energy and transmission); `Recipes.choose()` scores each template's recipes at its own price. Harnessed by
  `LicenceFeesCheck`'s last-ore section, restated to cause it: a month struck before the mine opens (so the book is
  the merit order's, not the founding tariff's single price) and the mine given a generating set the size of its
  draw, whose licence gain is its set times what Mining is billed, not what commerce is; on the old line it failed.
  `RecipeChanges`' rail service and a utility's own output keep the commerce price (their buyer is the city).
- ★ A save from before PW1 loads at the ratio its load dispatched. The founding book carried ratio 1 into
  `rebuildSimulationState()`, where main handed the sectors the ratio `updateServices()` had just computed; now a load
  whose save had no `powerBooks` seeds the founding book's ratio from that dispatch
  (`UtilitiesHandler.seedLoadedRatio()`), as main did. Mended, no bump. The sectors then take the ratios their month
  ran at back from the save (`Sector.restoreBills()`, 0.7.63), as on main, so the first month bills at the saved
  ratio and the founding tariff, main's figures. `SaveFileCheck` (through `PowerPriceCheck.saveChecks()`) loads a
  format-58 power-short town, stock templates only (a fixture's template draws are not in a save: the first
  attempt, section 3's short town, loaded unshort), and asserts both; on the old line its book read 1.
- Not before the gate: `LicenceFees.powerDelivered()` still the flat rule, PW1-L beside PW1-W.
- REV-PW1c-2: SHIP, all four closed; its P2, a recipe's surplus generation credited at the industry price where PW1 pays the works
  price (`Recipes.value()`), is PW1-G, not before the gate.

**The ensemble** (`docs/ensemble/PW1.md`): PW1d on the box, the classes of eacfd6d37 (the REV-PW1c mends; 5806f5200's
bytes), against 0.9.174, `JAVA_OPTS` empty, 80 of 80. PASS, hard criteria 10 of 10, 24 of 26. One verdict changed:
AUTO2 T2 (info, the final population), PASS to FAIL, a median of 1,666,304 against at least 75% of 0.9.174's
2,226,328 (1,669,746). R2, PW1's earlier R2 row (§4), passes against 0.9.174: 2.30 points. PW1-E restated. The
first run, PW1c on 39c5b7819 before the mends, gave the same verdicts (24 of 26, the same change); the mends move the
playtest's bytes (the Pulp Mill's recipe, Heavy Industry's and Mining's licences), so PW1d is the record.
