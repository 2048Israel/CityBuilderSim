# PW1-W - water priced from its cost, as PW1 prices power

Builder: Claude (Opus), headless, started by the orchestrator 2026-11-23, on `batch/PW1-W` cut from origin/main
f937d2c22 (0.9.192, PW5, SAVE_FORMAT 63). The row: [PW1 notes §6](PW1-notes.md), with PW1-L and PW1-G.

## 0. Scope

- **PW1-W.** The water's price from its cost: the desalination plant's power bought at the market's price with PW5's
  wires, inside the water's price, so its water covers its power and a patented plant earns its licence.
  PublicLicenceCheck's four reds (REV-PW1c) pass on their own premises and their six known-reds rows go.
- **PW1-L.** A licence's power read from the dispatch's energy served, not the flat rule.
- **PW1-G.** A recipe's own generation netted at its class's price, its surplus credited at the works price.
- Every bill paid by a real payer to a real payee and booked once; each case caused in a harness on both sides.

## 1. What changed

- **The water's price** (`UtilitiesHandler`, sub-banner THE WATER'S POWER). The city's water is of two kinds, the
  land's (the founding wells and the fresh works) first, then the sea's: the order `updateWaterRatio()` and
  `waterOutput()` already make it in. Each update records a `WaterDraw`: the fresh and sea works' draw (kW,
  `ServicesManager`'s `gridDraw` over the WATER category, split by `isSeaWater()`) and the water each kind delivered.
  The price a unit (`getPricePerWaterUnit()`) is the tariff (main's constant, `.05 x FACTOR`, now `getWaterTariff()`)
  plus `getWaterPowerPerUnit()`: while the sea delivers, the sea works' draw over the water the sea delivers, at the
  struck book's commerce price (energy and both wires); 0 while the land's water serves, its tariff paying its works'
  power.
- **Its timing.** The struck book's prices bill month t's draws at the top of t+1; so does the water. The price is set
  in `finalUpdateEconomy()` after `updateServices()` (main already set it there) from book t and month t's record,
  and the statements strike it at the top of t+1. `SimulationEngine.updateEconomy()` also sets it after the strike, so
  the sectors' month runs at the new book's power over last month's water until the update replaces it.
- **The bill** (`Game.buyPowerFuel()`, at the top of t+1 beside the fuel): the water's books pay the power's all the
  works' draw at book t's commerce price (`getWaterPowerBill()` -> `setWaterPowerPaid()`). `getElectricityIncome()`
  adds it, `getWaterIncome()` subtracts it: a pool paying itself, so `servicesNet` (the treasury's line, the national
  accounts' utility income) is unmoved and `getUtilityRevenue()` (the audit's "chk utility revenue") stays the
  sectors' bills. The Services screen's books page and statement show it on both sides ("Sold to the water works",
  "The works' power, at the market's price") and the water statement its part of the price.
- **PW1-L** (`LicenceFees.powerDelivered()`): `UtilitiesHandler.deliveredWith(t, moveKw)` runs the month's dispatch
  again on copies of its rows (`PowerDispatch.Row.offering()`), the template's row moved by `rowFor()` (the plant
  rows' factory `updateEnergyRatio()` now builds through, bitwise the old loop), and returns the demand delivered.
- **PW1-G** (`Recipes.powerCost()`): a recipe's draw past its own generation at its class's price, its generation past
  its draw at the works price the strike pays a surplus (`Sector.getPowerSoldValue()`). `Recipes.Prices` carries
  `worksPricePerWatt` (`pricesOf()` from the struck book; the five-argument constructor keeps a harness's flat prices
  at the draw's price). Used by `Recipes.value()`, `LicenceFees.standingRecipeValue()`, `RecipeChanges.standingValue()`.

## 2. The calls (★), each taken as recommended

- ★ **At the margin, the sea's, as PW1 prices power.** While the sea's water is needed the price carries the sea
  works' power over the water the sea delivers. An average over all the water fails the brief: the land's cheap water
  dilutes it, so a desalination plant never covers its power (the berth: about 617 against 709). A price on the
  plant's output at full use fails too (the berth's plant delivers 56% of its output: about 407). At the margin the
  plant's gain over its running cost is its tariff on its water less its running cost besides its power, whatever the
  power costs: its water covers its power again, and the land's water earns the rest, as the inframarginal plants do
  in PW1. The cost: a desalination plant standing for a sliver of the demand prices the sliver at its whole draw, so
  the water's price can spike when one is barely needed. That is the sliver's cost at the market's price, as a short
  half-block clears at the value of lost load; no cap was added, because any cap below it fails the brief's own
  fixture (the berth). Flagged for the review and the ensemble.
- ★ **The land's works' power is paid from the tariff, not put on top** (the finding: SubstationCheck PW5-F1, "the
  ordinary works fixture uses one district and native world quotes, with its original coal chain working"). Priced
  the same way, the fresh works turned it red twice. Its works town's lone Water Treatment Plant stands for 1,383
  units past its wells, of 4,288, and draws its whole 900 kW: over the sliver the water was 2.33 times the tariff,
  spread over the land's water with the wells' 1.43 times; the coal mines' water lifted their thermal marginal cost
  from main's 25.97 USD/t to 28.89 and 26.91, their export margin fell further below zero and their diesel bid went
  to 0 (the chain's diesel bought 37 of 21,857). The brief names the desalination plant's power; moving PW5-F1's
  premise would be Jerus's. A fresh works draws 900 kW for 60,000 units against the sea's 10,880, and the tariff the
  land's water always sold at pays it (in that town the fresh works' power is 0.43 of the water's bills at the
  tariff). The water's books still pay the power's for every works' draw, the land's included.
- ★ **The whole draw, not the month's ratio**, in the price and the bill. A short grid rations the works' power but
  not their water (`updateWaterRatio()` reads the staffing fill alone), so the water they make is costed at the
  power it needs, as `BuildAdvice.rawRunningCostAt()` costs every city works. At the served share the berth's plant
  (ratio 0.32) would recover a third of its power.
- ★ **The commerce price** (energy and both wires): the works are not high-voltage (`getPowerPriceFor()`).
- ★ **Month t's own record**, not the dispatch's month: the water delivered is known after `updateWaterRatio()` in
  month t, and the sectors' draws are billed the same way (book t's price on month t's draw). With the record of
  t-1 the berth's price lagged its own water (fill .25 -> .31) and the plant fell short by about 19.
- ★ **One transfer, inside the treasury's pool.** The water's books buy the power; nobody outside pays twice.
- ★ **PublicLicenceCheck 6(a)'s expected fee** is worked through the dispatch's energy served (PW1-L's rule), with
  the premise asserted that every half-block is short by more than the turbine makes: 293.81 against the flat
  rule's 308.66, the losses and the season. The label, the fixture and the other assertions are main's.
- ★ **PowerPriceCheck 5's identity** gains the water works' power as a line (the electricity income is revenue less
  payroll less power bought less fuel plus the power sold to the water's books), with a fixture line that the town
  paid it: PW1-W's new line in an identity, not a moved premise. WaterCheck is main's: its town has no sea works.
- ★ **SpreadPlanner.ownCosts()** (gen x the class tariff, many planners and harnesses) has PW1-G's flaw; left as
  queued **PW1-S**, so this batch does not move every spread planner.

## 3. Old saves

Two saved fields, in `DataSave.powerBooks` (`UtilitiesHandler.PowerState`): `pendingWater` (the month's `WaterDraw`,
brought back by `restorePendingPower()` after the load's last rebuild) and `waterPowerPaid`. A save without them
(the build before) loads with the record the load's rebuild makes from the same stock and nothing paid, which is what
the old build paid (WaterPowerCheck 4 strikes them out of a saved file and loads it). No field is read differently:
**no format bump owed** (SAVE_FORMAT stays 63).

## 4. The checks


Each through `./batch-check.sh <name>`, one at a time, on the PC (2026-11-23), after the last code change:

- **WaterPowerCheck** (new, in `AllChecks` after PowerPriceCheck): 36 of 36. Its sections cause each case on both
  sides: 1 a desalination town in surplus (the tariff exactly), 2 the same town short past every works (the sea's
  power over its whole output), 3 every bill once, 4 the save and a save without the fields, 5 PW1-L in a copy in
  surplus and one short in every half-block, 6 PW1-G in the short copy (a Pulp Mill's surplus at the works price, an
  Oxygen Steel Shop netted at its class's), 7 a power price low and high, the sea at the margin in both.
- **PublicLicenceCheck** 68 of 68: the four REV-PW1c reds pass on their own premises; their six known-reds rows are
  deleted, and no assertion is restated but 6(a)'s expected fee (§2).
- **SubstationCheck** green under the rule kept; red (PW5-F1) under both rules that priced the land's works (§2).
- Green besides: PowerPriceCheck, WaterCheck (main's), LicenceFeesCheck, HistoricalLicenceCheck, SaveFileCheck,
  RecipeCheck, PowerDispatchCheck, BooksCheck, DiscoveryCheck, EquipmentCheck, FittingCheck, HydroCheck,
  ElectrolyserCheck, HydrogenSteelCheck, WoodProductsCheck, BuildAdviceCheck, NewUnitCheck, HistoricalRecipeCheck;
  KeptCheck known red only (main's). `./batch-check.sh --fast` green.
- `tools/pick.sh`: ALL (AllChecks changed). Its review line says "not needed"; the brief owes a stranger's money
  review, and saved state is added, so a saves review too.
