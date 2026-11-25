# PW1-R - PW1's review follow-ups: power billed on the kWh served

Builder: Claude (Opus), headless, started by the orchestrator 2026-11-25, on `batch/PW1-R` cut from origin/main d657b3a83
(0.9.202, SAVE_FORMAT 66). The brief: [PW1-R](PW1-R.md), REV-PW1's four follow-ups in order of weight (2026-11-11), read
against PW3 (0.9.202): the power planner's plant rule (`BuildAdvice.timely()`) is PW3's and is not touched here; item 2's
prices are the works' choices (`Recipes.pricesOf`, `SpreadPlanner.ownCosts`), kept apart from it.

## 1. Bill the kWh served (item 1)

**The finding.** `PowerDispatch.dispatch()` serves draw x season x the month's hours (SEASON at the city's calendar and
latitude, losses in), and the fuel is bought on those kWh, but `Sector.getElectricityCost()` billed draw x ratio x the
price a kW-month, the price struck as (energy + wires) x `MONTH_HOURS`: the year's mean kWh. July billed 16% under,
March and April 14% over, and the utility's margin took the gap.

**Check reality.** A utility bills metered energy: the kWh delivered in the billing period at the tariff a kWh (a
demand charge, where one exists, is a kW figure, but this model's prices are energy and wires a kWh delivered,
`UtilitiesHandler`'s THE MERIT ORDER). So the bill is the kWh served, and the check is restated to that law.

**What changed.**
- `PowerDispatch.Month.season` (new, default 1): the season the month was dispatched at, set in `dispatch()`, copied
  in `copy()`.
- `UtilitiesHandler.Book.season` (new, default 1): struck from the month in `strikePower()`, copied in `copy()`, 1 in
  the founding book; `getPowerSeason()` reads it.
- `Sector.powerSeason` (default 1, `setPowerSeason()`), handed down by `EconomyManager.setPowerSeason()` beside
  `setPowerPrices()` in all three places the book's prices are: `SimulationEngine.updateEconomy()` (the strike),
  `Game.finalUpdateEconomy()` and the load path, `Game.rebuildSimulationState()`. `getElectricityCost()` is draw x
  ratio x season x the class's price. Nothing new is saved per sector: the load hands the saved book's season down.
- ★ **The prices stay a kW of the year's mean.** The season goes in the bill, not in the struck prices. Folded into
  the prices, every planner reading `getPricePerWatt()`/`tariff()` (the works' recipes, the licences, the advice's
  costs) would have seen July's price as the tariff and chosen by the season; the mean is what a year of running
  costs. The Services screen's "price per kW a month" keeps its meaning, and a new line beside it shows the month's
  load of the year's mean, the factor the bills carry.
- ★ **The water works' power** (PW1-W's transfer, `getWaterPowerBill()`) and the water's power a unit
  (`getWaterPowerPerUnit()`) carry the season too: the dispatch serves the water works' draw at the season like
  every other, the fuel is bought on it, and PW1-W bills them as a class. The fallback `getElectricityRevenue()` (no
  statements handed in) carries it as well.
- ★ **The water's two prices** (REV-PW1-R's note, mended before the ensemble): as first built, the water's price with
  the season in reached the planners too (`EconomyManager.getPricePerWaterUnit()`, `Recipes.pricesOf()` at Recipes.java:176,
  the advice's and the investment's running costs, the licences), against the call above. Now
  `EconomyManager.setPricePerWaterUnit(billed, planned)`: every sector is billed at `UtilitiesHandler.getPricePerWaterUnit()`,
  the works' power at the book's season, so the water's books are unchanged; the economy's own price, which the planners
  read, is `getMeanPricePerWaterUnit()`, the same power part at the year's mean. The one-price setter stays for fixtures.
  Handed down in the same three places as the power prices.
- **The lag** (item 4's third low) is unchanged and now said: a statement bills the kWh of the dispatch its book was
  struck from, the end of the month before the one it bills, so July's bill is at June's season (1.088, not 1.193).
  The fuel bought at the same strike is that dispatch's, so bills and fuel agree on the kWh; the margin takes none of
  the season. Said in `UtilitiesHandler`'s THE STRUCK BOOK banner (THE LAG, replacing its stale "nothing calls
  strikePower() yet") and in PowerPriceCheck's TIMING.

**The checks.**
- PowerPriceCheck 7 (new): the mine town played through its own calendar to a statement struck at a July dispatch's
  book (season 1.193) and one at an April dispatch's (0.880); in each, Retail's bill (commerce) and Coal Mining's
  (all high-voltage) are their kWh served (draw x ratio x season x hours) at the energy and the wires a kWh, to 1e-9;
  the bill over the mean-kWh bill is the season, to 1e-9 (red under main's bill: 1 against 1.193 and 0.880); and
  the utility's revenue is every billed class's kWh served at its price a kWh. The season is read off the game's
  calendar (`CityCalendar.monthOfYear(book.dispatch.month())` at the city's latitude), never off the book alone.
- PowerPriceCheck 2 and 3 restated to the same law (draw x ratio x season x price), with the book's season held to
  the calendar's; the audit is now section 8. All 93 green.
- MaterialsCheck 4 and 14: their exact bill identities carry the sector's season, held equal to the utility's.

## 2. The planner's power prices (item 2)

- **`Recipes.pricesOf()`'s half was already mended** by PW1 itself after REV-PW1 (ee8296036, REV-PW1c):
  `pricesOf(game, t)` and `forTemplate()` price each template's power at its class's bill
  (`EconomyManager.getPowerPriceFor()`), and `Recipes.choose()` weighs each template at it. The untemplated
  `pricesOf(game)` callers left in the model read wages or goods only, or (LicenceFees `served()`) value power the
  city's grid delivers at the commerce price, which is not a works' bill. MaterialsCheck 23 (e) confirms the class
  prices on a struck book.
- **`SpreadPlanner.ownCosts` credited all of a works' own generation at the class price.** The bill pays its draw
  replaced at the class price and its surplus past the draw at the works' price (`Sector.getPowerSoldValue()`, as
  `Recipes.powerCost()` already reads it). Now `ownCosts(t, rate, factor, tariff, worksTariff)` credits
  min(gen, draw) x tariff + max(0, gen - draw) x worksTariff. `worksTariff()` moved from `ProcessingSector` up to
  `Sector`; `ProcessingSector.estimate()`/`lastEarns()` take it; every model caller (ProcessingSector,
  ExtractionSector, CoalMining, Quarrying, Manufacturing, LicenceFees) passes it. ★ The one-price forms stay as a
  check's or the study's flat frame (`caseEarns()`, the study's four cases, keeps one tariff), identical to the bit
  when the two prices are equal; no model file calls them.
- **MaterialsCheck 23** (new): section 14's town (the Coke Ovens' 8,000 kW past a 2,200 kW draw, the furnace and
  the shop loading the grid so a book is struck at industry 0.0589, works 0.0388 a kW): (b) ownCosts is the
  consumables less the draw replaced at the industry price less the surplus at the works' price; (c) the standing
  works' `lastEarns()` rises by exactly the surplus times a rise in the works' price; (d) a rise in the industry price
  moves the credit by the draw replaced alone; (e) the recipes' prices are the class's. (b), (c) and (d) are red
  under main's `gen x tariff` ((c) moves 0 against 22.5k).

## 3. powerBooks through a save (item 3)

PowerViewCheck's `pendingSaveChecks()` (PW1, later) already compares the struck book's JSON across a real save and
load, but a field zero on both sides, or one Gson and `copy()` both drop, passes it. New: `PowerPriceCheck.bookChecks()`
on its own SaveFileCheck line: the mine town played WARM months (coal burned, its dispatch and grid costs held), every
number of its book then given a value of its own (26 fields, 37 numbers by reflection, so a field added later is held
too), saved to its scratch folder's slot 10 and loaded: every field reads back, the arrays element by element, the
fuel map, the dispatch record and the grid costs by their JSON. Green.

## 4. The lows (item 4)

- **Saved fuel names**: `Game.buyPowerFuel()` and `getTradeByGood()` read the book's fuel names through
  `Good.byName()` (null for a name this build has no good for: the line is dropped, not the load), and
  `buyPowerFuel()` draws no fuel and pays the upkeep at par when the city has no markets (a bare fixture).
- **The plants' coal moves no freight** (the utility has no buyer's row): not mended. ★ Freight carrying the city's own
  draws is a new money flow (the utility paying a haul, Freight's capacity and fleet for it, the audit's lines, the
  planner's delivered price of coal), a mechanic and not a low; it is the ship file's queued row PFR1.
- **The lag**: said (section 1).
- **BuildCardCheck's POWER row** was held to `BuildAdvice.served()`, the figure the row reads: itself. It is held to
  the grid's own figure at the peak hour's draw (`getPowerServed() / PLAN_PEAK_HOUR`), to 1e-9. **The ring
  assertion** folded its premise into the law (`!variable`, PW3), so a town with a wind or solar farm would go red on
  the premise; now both the row and the ring read whether wind or sun stands: none, equal to the grid's figure; one,
  at most it (a variable row's average past its credit comes off the firm supply, PW3's credit). The label prints
  which; the town stands none today, so the run takes the equal branch, and the other is the law for a town that
  builds one (no second two-year town was built to cause it: the section's town is shared by the later sections).
- **ScaleCheck ~196** (watch): PW1 picked section 9's month (177) as the middle of the run of months that hold (174-180)
  in a scan of 160-200. Unmoved: ScaleCheck green at 177 on this branch once its scaler leaves the new season alone
  (the checks below); the run's width was not rescanned (40 copies of the 225-month town), so the watch stands.

## 5. The checks

- `./batch-check.sh --fast`: green.
- PowerPriceCheck: all ok (93). MaterialsCheck: all passed (23 sections). BuildCardCheck: green.
- SaveFileCheck: the new line green; one red, **REV-HC1 P5** ("the populated format-62 city continues its card
  stocks, services, loan book and capital"): traced to `sectors[1].extras.familyRentWeight`, live 15564.000000000002
  against loaded 15564.0, one ulp, by a leaf diff of `CardCheck.continues()`'s opening tree (temporary, reverted).
  That is **FRW1** (QUEUE.md): the load rebuilds the family rent weight as the total less the studio weight, 1 ulp
  off the live sum once the takes carry dust; this branch's different path carries the dust. Main's latent fault, a
  known-reds row naming FRW1.
- The harness set the diff reads (39, `-Dcbs.parallel=1`): the power, materials, works and recipe harnesses, BuildCardCheck,
  SaveFileCheck, ReloadCheck, RoadCheck among them. NEW reds on the first run, each the new law read by an old identity and
  restated to it (check reality, as section 1):
  - **WaterPowerCheck** (8): the water works' power and the water's price at the struck commerce price, the season left
    out. Restated to commercePrice x the book's season, the header saying "at the struck commerce price" is on the kWh
    served throughout; the cross-copy comparison (section 2's two copies) is unmoved, both books at one season.
  - **HydrogenSteelCheck** (1): "billed at the ratio the grid delivered and the tariff". Now draw x ratio x season x
    tariff, the sector's season held equal to the utility's.
  - **ElectrolyserCheck** (3): its `draw()` reads Heavy Industry's draw as its bill at price 1 and ratio 1
    (`billAtOne()`); after months played the bill carried the season. `billAtOne()` sets the season to 1 as well: the
    fixture's own unit, not a weakened law.
  Rerun: all three green. Known, not this batch's: RoadCheck's two lines (`docs/notes/gate.txt`) and CopperRefiningCheck's
  "the rule cut an order" (`gate.txt`, FS8). PriceLevelCheck 1 is observed, not counted.
- **ScaleCheck** (not in the set; run alone for the watch item): 6 reds, two of them "the month's money audit closes",
  the K = 617,055 copy's treasury 1.6e10 to 3.1e13 in its first month. The scaler's, not the model's: `ScaleCheck.scale()`
  multiplies every number of `powerBooks` by K but the names its INTENSIVE pattern leaves (prices, ratios), and the
  book's new `season` is neither, so the copy billed every class K times its kWh. ★ `(^|\.)season$` joins INTENSIVE with
  its line, as FC2's workFill did; ScaleCheck and BuildingStackCheck (the same `scale()`) green. The game's own
  redenomination (`UtilitiesHandler.redenominate()`) scales the book's money and never the season.
- Every other harness reading a bill, a price or the ratio was looked for (`getElectricityCost()`, the book's prices,
  the water's power, `getElectricityRevenue()`): FoodProcessingCheck, HaulageCheck, NewGameCheck, ReadPathCheck and
  VanCheck read the ratios only; LongPlaytest's power gap is the revenue against the statements, both on the one bill.
- **The water's two prices** (REV-PW1-R): WaterPowerCheck's lines that read the economy's price as the bill (sections 2,
  7, 8 and the save without the record) read the utility's, the bills' price; sections 2 and 8 hold the economy's and the
  recipes' to the same rule at the year's mean. New in section 4: the same save loaded with its book struck at July's
  season (1.193) and at April's (0.880): a planner's water price is the same to the bit (0.00998178220 in both), the
  economy's and the recipes'; the bills' power part moves by the seasons' ratio and every sector's water bill with it. Red
  under the reviewed code (the planners' price carried the season). WaterPowerCheck: all ok (44).
