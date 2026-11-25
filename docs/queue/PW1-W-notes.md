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
  `ServicesManager`'s `gridDraw` over the WATER category, split by `isSeaWater()`), the water each kind delivered,
  and the sea works' output at the fill. The price a unit (`getPricePerWaterUnit()`) is the tariff (main's constant,
  `.05 x FACTOR`, now `getWaterTariff()`) plus `getWaterPowerPerUnit()`: while the sea delivers, the sea works' draw
  at the struck book's commerce price (energy and both wires) over max(the sea's water, min(the water served,
  `SEA_MARGIN_SHARE` (.5) x the sea's output at the fill)); 0 while the land's water serves, its tariff paying its
  works' power.
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

- ★ **At the margin, the sea's, as PW1 prices power, and bounded (REV-PW1-W 1).** While the sea delivers at least
  half its output at the fill, the price carries the sea works' power over the water the sea delivers: the plant's
  gain over its running cost is its tariff on its water less its running cost besides its power, whatever the power
  costs, so its water covers its power again (the berth's plant at 56% untouched). Under half, the plant stands
  mostly idle and that rule had no bound (one unit past the land's water paid the whole draw on every unit, then the
  tariff at 0; on a 1,383-unit sliver about 17 times the tariff, the businesses paying served/sea times the plant's
  power). There the power is spread over the water served, recovered once (WaterPowerCheck 7: the water served at
  the price is the plant's power to 1e-12, the bills the billed part of it), but over no more than half the sea's
  output, so a unit pays at most the plant's power over the water served (at least the land's water) or over half
  its output (WaterPowerCheck 8). At half the two divisors are one, so the price has no step there.
  Each of these alternatives fails one ask. Spread over the water served everywhere, the berth's plant earns
  sea/served of its power and loses its licence; an average over all the water failed it the same way (about 617
  against 709). The
  divisor floored at half the output alone recovers billed/(half the output) times the plant's power on a sliver,
  short of it or past it, once only by chance. A price on the output at full use fails the berth (about 407).
  **An option for Jerus, not built** (REV-PW1-W, P2): a bound continuous from the sliver to the share, the divisor
  interpolated from the water served as the sea goes to 0 up to half the output at the share, served + (half the
  output - served) x sea / (half the output). It recovers the plant's power once on the thinnest sliver and meets the
  margin at the share, so the berth does not move; between them a small town's water recovers less than once and a
  large city's from once up to the margin's at the share, where the rule built recovers served/(half the output)
  times. Both rules step once, as the sea first becomes needed: at sea = 0+ the rule built goes from the tariff to
  the tariff and the plant's power over min(the water served, half its output), bounded; the interpolation to the
  tariff and the plant's power over the water served.
  What stays: where the city serves past half the plant's output, a sliver pays the plant's power over half its
  output, more than once (WaterPowerCheck 8: 78.60 against 27.44); that is what a plant delivering just half would
  price its water at, and the margin recovers more than once wherever the plant is the margin (the berth), as PW1's
  power price does; the surplus stays inside the treasury's pool (the water's books). The share is a dial:
  `SEA_MARGIN_SHARE` .5, under the berth's 56%.
- ★ **The land's works' power is paid from the tariff, not put on top.** The reason is not the sliver's spike: the
  bound would treat a fresh works' sliver as it treats the sea's, its power spread over the water served. In
  SubstationCheck's works town (PW5-F1, "the ordinary works fixture uses one district and native world quotes, with
  its original coal chain working") that is the rule measured red: its lone Water Treatment Plant's 900 kW over
  the 4,288 units served is 0.43 of the tariff on top (1.43 times), the coal mines' thermal marginal cost rises from
  main's 25.97 USD/t to 26.91, their export margin falls further below zero and their diesel bid goes to 0 (37
  bought of 21,857). Over the sliver alone it was 2.33 times (28.89 USD/t). The fresh works' power recovered once is
  itself what prices that chain out, so pricing it in is not harmless and the call stays: the brief names the
  desalination plant's power, and moving PW5-F1's premise would be Jerus's. The tariff the land's water always sold
  at pays it (in that town the fresh works' power is 0.43 of the water's bills at the tariff), and the water's books
  still pay the power's for every works' draw.
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
  paid it: PW1-W's new line in an identity, not a moved premise. **MaterialsCheck's** (the utility pays the works
  out of its electricity income) gains the same line and fixture line: the first dry run found it red, a harness this
  batch had not run. WaterCheck is main's: its town has no sea works.
- ★ **SpreadPlanner.ownCosts()** (gen x the class tariff, many planners and harnesses) has PW1-G's flaw; left as
  queued **PW1-S**, so this batch does not move every spread planner.

## 3. Old saves

Two saved fields, in `DataSave.powerBooks` (`UtilitiesHandler.PowerState`): `pendingWater` (the month's
`WaterDraw`, the sea's output at the fill among its fields, brought back by `restorePendingPower()` after the load's
last rebuild) and `waterPowerPaid`. A save without them (the build before) loads with the record the load's rebuild
makes from the same stock and nothing paid, which is what the old build paid (WaterPowerCheck 4 strikes them out of
a saved file and loads it). No field is read differently: **no format bump owed** (SAVE_FORMAT stays 63).

## 4. The checks

Each through `./batch-check.sh <name>`, one at a time, on the PC (2026-11-23), after the last code change (the bound,
REV-PW1-W 1):

- **WaterPowerCheck** (new, in `AllChecks` after PowerPriceCheck): 41 of 41. Its sections cause each case on both
  sides: 1 a desalination town in surplus (the tariff exactly); 2 the same town short past every works (the sea at
  its whole output, past `SEA_MARGIN_SHARE`: its power over its water); 3 every bill once; 4 the save and a save
  without the fields; 5 PW1-L in a copy in surplus and one short in every half-block; 6 PW1-G in the short copy (a
  Pulp Mill's surplus at the works price, an Oxygen Steel Shop netted at its class's); 7 the sea on a sliver a
  little past the wells (7,432 and 8,599 of 60,000, the water served 12,348 and 12,607): the price spread over the
  water served, bounded below the old rule's, the water served recovering the plant's power once to 1e-12 and the
  bills no more, the dear power's copy the dearer water; 8 the sea on a sliver of a city serving past half its
  output (9,886 of 33,073, served 47,369): the plant's power over half its output.
- **PublicLicenceCheck** 68 of 68: the four REV-PW1c reds pass on their own premises, the berth's plant at 56%
  untouched by the bound; their six known-reds rows are deleted, and no assertion is restated but 6(a)'s expected fee.
- **ReloadCheck** (the month after a reload), **SaveFileCheck**, **TreasuryCheck**: green. The review named a
  MoneyCheck, which the tree does not have; TreasuryCheck holds the treasury's lines the water's money reaches, and
  the money audit closes in WaterPowerCheck 3, PowerPriceCheck, PublicLicenceCheck and every month of LongPlaytest.
- **LongPlaytest** (2,400 months): known red only (main's). Its count (new, beside the conservation line): the sea
  delivered in **0 of 2,401 months**, under half its output in 0, under a tenth of the water served in 0. The
  default city never needs a desalination plant (it ends with 154 km2 of lake and river), so the sea's rule and its
  bound never fire in the default path; the price there is the tariff every month, as on main.
- **SubstationCheck** green; red (PW5-F1) under both rules that priced the land's works (§2).
- Green besides: WaterCheck (main's), PowerPriceCheck, LicenceFeesCheck, HistoricalLicenceCheck, EssentialWorksCheck,
  LandmassCheck, BuildingDataCheck, ReadPathCheck. Before the bound (the sea's rule unbounded), also green:
  RecipeCheck, PowerDispatchCheck, BooksCheck, DiscoveryCheck, EquipmentCheck, FittingCheck, HydroCheck,
  ElectrolyserCheck, HydrogenSteelCheck, WoodProductsCheck, BuildAdviceCheck, NewUnitCheck, HistoricalRecipeCheck;
  KeptCheck known red only (main's). `./batch-check.sh --fast` green.
- **The first dry run** (68f01ebb7, 2026-11-23): one new red, MaterialsCheck's "the utility pays the works what their
  statements booked, out of its electricity income": its identity left out the water's payment for the works'
  power, which this batch adds to the electricity income. Mended as PowerPriceCheck 5 (§2); MaterialsCheck green.
- `tools/pick.sh`: ALL (AllChecks changed). Its review line says "not needed"; the brief owes a stranger's money
  review, and saved state is added, so a saves review too.

## 5. The trace: unchanged, so no ensemble

The default playtest, traced (`-Dplaytest.trace`, both data files named, one JVM each, one after the other) on main
ecbc3b975 (0.9.192) built apart and on this branch merged with it (82d5015aa, the code of e3bc5bc56):

- All eight CSVs are byte-identical (`month`, `house`, `pop`, `bank`, `bonds`, `build`, `labour`: 2,402 lines each,
  26 for `borrow`; 947,599 bytes `month`, 5,306,480 `bonds`).
- The log differs in nine lines and nothing else: the run time (742 s against 758 s, in two lines), the wall-clock
  stamps on two lines in each, and the branch's new count line (the sea's water: 0 months). Both exit 1 on main's
  known red alone.
- So PW1-L and PW1-G move no figure the trace records in the default path (why each is idle there was not
  traced); the sea never delivers (§4), and the water's power transfer stays inside the treasury's pool. The
  ensemble does not run (docs/contract.md, section 4: an unchanged trace is said in the notes).

## 6. The dry run

The second dry run (`sh tools/gate.sh PW1-W --dry-run`, the gate box, 2026-11-23) passed: branch 6fc091ac1 merged
with main cdbe83dc0, the pick ALL (246 harnesses), the suite in 1,001 s, exit 3 on known reds alone
(CopperRefiningCheck, CurrencyCheck, DeathRecordCheck, Forms1900Check, FundCheck, HoldersCheck, KeptCheck, LongPlaytest, PriceLevelCheck,
RoadCheck), no unnamed line, Stale no firm findings, gate exit 0. Run `/home/jerus/cbs/runs/cbs-gate-PW1-W-3D5i6fZ2`.
