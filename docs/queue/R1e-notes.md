# R1e — a new building's equipment priced and saved at its order — notes

Builder: Claude (Opus 5.5), started by the orchestrator 2026-11-19; branch `batch/R1e` from origin/main ba734bf85 (0.9.158,
QN1 just shipped). Brief: `docs/queue/R1e.md` (R1b-R's ★3, left open by R1d).

## Before the code: who prices or delivers a new building's equipment today

From QN1's reader inventory (`docs/queue/QN1-notes.md`): QN1 resolved a new building's RUNNING figures (`NewUnit`) and left
"Land, GroundPlan, ConstructionControl, quotes and invoices: unchanged ... cash, materials or points". Those are this batch's.

Priced (every reader pays or estimates the template's WHOLE cost, whatever the building will come with):

| reader | how it prices a new building today |
|---|---|
| `Game.quoteBuild` / `quoteBuildOn` (the invoice) | `BuildingManager.nonMaterialCost(t)` × n × the scarcity multiple on `t.getConstructionPoints()`, + preparation, works, span, deep water, material, bitumen, tax |
| through it: `processBuildOrder` (the city), `buildFor` (a business), `buildStack`, BuildCard's quote doors, AutoBuilder, BuildAdvice, WorksValue, AdviceValueOracle, BankScreen, the receipts | the quote |
| `BusinessInvestment.getCostOf` / `getCostOfAt` / `totalCostOf` (the investors) | `nonMaterialCost(t)` × n × scarcity on `t.getConstructionPoints()`, + material, works, ground, tax |
| through it: SpreadPlanner's candidates, every sector plan, `EquipmentFit.choose` (the building's cost of one), `Game.orderGate()` | getCostOf |
| `Game.quoteMonths` / `quoteCityMonths` (the waits) | the template's points per building |
| the builders' bill (`Construction.bill`, `billedPoints` in processBuildOrder and buildFor) | `t.getConstructionPoints()` × n |
| `EquipmentFit.verdictForNew` / `fittedForNew` | a piece's cost C at the planners' city prices (step 2), to decide the set; never a discount |

Delivered:

| reader | what it does today |
|---|---|
| `BuildingsStacks.advanceConstruction` | one pool: every building on site owes the template's points |
| `BuildingManager.advanceConstruction` → `Completion` | the month's completions by building, no set |
| `Game.fitWhatFinished` | at the FINISH: `fittedForNew()` asked then; pieces not in it raise the stack's unfitted counts (a 1900 city only) |
| `buildStack(t, n, true)` (instant) | comes as placed, unhooked |
| a shell (`stopForShell`, `restartShell`) | the stopped buildings, priced on the template's points left |

The default city's orders: a city founded the default way fits every piece to every building (`fittedForNew()` keeps them all,
and `fitWhatFinished()` returns at once), so its order is the whole template and nothing changes its price. This batch keeps
that exactly: there the order's fitout is the whole template, chosen by no one, with no lot on site and no saved key. The
default trace should not move; the eight default traces are compared byte for byte (below). If it holds, no ensemble.

## The split of a template's cost, sourced

The pieces' goods and work were SIZED as shares of their templates (R1c call 10, `docs/queue/R1c-notes.md`; R2e's
"New equipment uses R1c call 10's capital estimate, 25% of plant cash at MACHINERY 13.4, 10% construction points",
`docs/queue/R2e-notes.md`): "A piece built into the plant ... buys a quarter of the plant's cash cost in MACHINERY at its import
price, and its work is a tenth of the plant's points (R1's drive); a piece carried in (the cutter, the shovel, the drill) buys
its machines and takes no work (R1's truck and tractor)". The catalogue bears it out: the Vehicle Works's carbide tools are
1,231.343 t × 13.4 = 16,500 = 25% of its 66,000 cash and 726 = 10% of its 7,260 points; the Coal Power Plant's turbine 19,996.567 t
× 13.4 = 25% of 1,071,816; the depot's 100 trucks at 215 are 21,500 against the depot's 12,000 (carried in: not in its price).
So the split is read back off the catalogue, not off today's market:

- a piece with work (built in) is in its template's price: its part of the cash is its goods at the table's import price
  (`Good.worldImportPrice()`, the price its quantity was sized at) over the template's catalogue cash, a fixed share (0.25 for
  the 25% pieces); its part of the points is its work;
- a piece with no work (carried in: the truck, the tractor, the shovel, the drill, the cutter, the near-future kits) is bought
  apart and is no part of the template's price, so a building without it costs the same (as on main);
- the shell is the template less every built-in piece; an order pays the shell and the pieces it comes with, so an order
  with every piece pays the template exactly. The shares are capped so no shell goes below nothing (the largest in the
  catalogue is three 25% pieces, 75%, on the Cement Plant and the Oxygen Steel Shop).

Not subtracted: `Game.quoteFitting()` (a refit's goods drawn from the market today, with its own work price), which is not
bounded by what the template charges and could price a shell below nothing.

## What was built

- **`Fitout`** (new): one new building's equipment as its order fixes it, the pieces it comes WITHOUT, and what that makes
  one building's cash cost and points: `cashShare()` (the split above), `cashCost()`, `points()`, `pointsLeftOff()`;
  `whole()` (chosen by no one), `without()` (a save's or a shell's), `of()` (the order's: the pieces `fittedForNew()` keeps,
  in a city founded the 1900 way; whole in a default city, for a template with no equipment, and inside NewUnit's resolving
  or a licence's pricing, as `NewUnit.of()` reads there). `NewUnit.resolving()` exposes the thread-local.
- **The price** (`Game.quoteBuild`): `nonMaterialCost(fit)` × n × the scarcity multiple on `fit.points()`; the city wait
  (`quoteCityMonths`) on the work the set owes (a negative extra, `BuildingManager.orderPoints()`, never below nothing);
  `BuildQuote.fitout()` carries the set. `BuildingManager.nonMaterialCost(Fitout)`: the same arithmetic on the fit's cash
  and points, `nonMaterialCost(t)` the same numbers to the bit. The investors (`BusinessInvestment.totalCostOf`, `dearOf`,
  `leadTime`) read the same set through `setFitout()`, wired by Game; `Game.quoteMonths` and AutoBuilder's projection too.
- **The contract**: `processBuildOrder` and `buildFor` hand the quote's fitout to `addStack(t, n, noConstruction, fit)`,
  which puts it on site as a LOT (`BuildingsStacks.Lot`: so many buildings, the pieces they come without); the builders
  are billed the set's points. An instant build stays the template's cash price and comes as placed.
- **The site** (`BuildingsStacks`, "THE ORDERS' EQUIPMENT ON SITE"): one pool of progress, lots behind the LOOSE (buildings
  no order fitted out: a default city's, a harness's, an old save's), finished first placed first (`finishFront()`); each
  building owes its set's points (`pointsOnSite()`, `pointsOfFirst()`), read by every owed-points reader in BuildingManager
  (the share rule, the waits, the plan, the remaining points, the old-contract inference, the stop). With no lot every
  figure is the old one to the bit.
- **The finish** (`Game.fitWhatFinished`): `Completion.without` (what the month's lots came without) raises the unfitted
  counts; only `Completion.loose` is decided by `fittedForNew()` at the finish, as before R1e.
- **The shell**: `ConstructionControl.Shell.lots`, kept at the stop, put back at the restart; `quoteRestart` prices the
  shell's work left at its sets' price a point.
- **The save**: `DataSave.lotsById` (`BuildingManager.getLotsById()` / `restoreLots()`, after the sites' counts), written
  only where a site has a lot; the shell's lots ride in the control state.
- **`FitoutCheck`** (new, in AllChecks), eight sections from the brief's proof list.

## The calls (★ = a design decision a reviewer should weigh)

1. ★ **The split is the catalogue's own sizing (R1c call 10), not the market.** A piece with work is built in: its part of
   the cash is its goods at `Good.worldImportPrice()` (a constant, the price it was sized at) over the template's founding
   cash, its part of the points its work; a piece with no work is carried in and no part of the price. A ratio, so it does
   not move with prices; `Game.quoteFitting()` is not subtracted. "At the prices the planners use": the order's work is
   priced by the one builders' price (`nonMaterialCost`: the cash struck at the expected level, its labour at today's
   builders' wages) on the shell's cash and points, which the quote, the investors, the money test, the bill and the restart
   all read; so what a planner reckons is what the invoice charges.
2. ★ **The set is chosen by the rule the finish used to ask** (`fittedForNew()`, research-design §2.2 step 2's C), asked at
   the quote and frozen at acceptance. A discovery or a price change afterwards changes a NEW order's set only; a building
   delivered without a piece is a standing building with an unfitted count, whose fitting is a fitting decision
   (`EquipmentFit.choose()`, `fitForCity()`).
3. ★ **No SAVE_FORMAT bump.** A save without `lotsById` (every save before R1e) reads every building on site as loose: the
   whole template's points, decided at the finish, which is how main runs it and what it was paid for; a shell without
   `lots` likewise. Gson leaves the missing key alone; `Lot.fit` is transient. FitoutCheck 5 loads a mid-build save with the
   key removed and finishes it as main would.
4. ★ **The loose are the front of the site**, then the lots in the order placed: one pool of progress, finished first placed
   first, so each order gets its set and the old saves' buildings finish first, as they would have.
5. ★ **Left on the template's figures:** an instant build (`buildStack(t, n, true)`: its cash price and every piece, as
   before); the repairs, depreciation, replacement value and capital stock of STANDING buildings (EconomyManager,
   BuildAdvice, AdviceValueOracle: a standing building's missing piece is its unfitted count); `basePricePerPoint()` (the
   contractor's price a point weights each site by its template's price a point); the hypothetical orders in the share rule
   (`siteShares`' extra). In a default city all of these are unchanged anyway.
6. ★ **Preparation, tax, escalation and finite work follow the contract.** The pad is priced on the ground, unchanged, at the
   order's one multiple (read on the set's points); the tax is the builders' rate on the set's price; the contract, the
   recovered share and the escalation are booked on what was paid; the site owes the set's points and the builders are
   billed them. R1d's credit (the builders take what `treasuryPays()` paid) and its no-refund scrap rule are untouched and
   re-proved on R1e's delivered buildings (FitoutCheck 8).
7. ★ **No recursion:** inside `fittedForNew()`'s own pricing (NewUnit's resolving) and a licence's, `Fitout.of()` is the whole
   template, as `NewUnit.of()` is standing there.

## Checks

- `FitoutCheck`: green, 58 assertions. With `Fitout.of()` forced to the whole template (the old price, and no lot, so the
  finish decides): 24 FAIL, in sections 3 to 8. With only the finish put back (every building decided by `fittedForNew()`
  at its finish): 4 FAIL, in sections 4, 5 and 7. Both temporary edits reverted (`git checkout`), the harness green again.
- `FittingCheck`: green on the change.
- The named readers, each alone through `./batch-check.sh <name>` on the final classes: ConstructionControlCheck,
  BuildingStackCheck, SaveFileCheck, InvestCheck, NewUnitCheck, PostwarRecipeCheck, BuildCardCheck, EquipmentCheck,
  HistoricalRecipeCheck, HistoricalLicenceCheck, DiscoveryCheck, ConstructionTrapCheck, BuildersFirstCheck,
  LandmassCheck, BridgeCheck, RecipeCheck: green (exit 0). AutoBuildCheck: exit 3, its one red main's (section 4's
  replay city above its debt limit, FS12, `docs/notes/gate.txt`), the line exactly main's.
- `./batch-check.sh --fast`: green (exit 0); Stale 0 firm findings; the maps regenerated and committed.
- The eight default traces (LongPlaytest, `-Dplaytest.trace`, 2,400 months) on main's ba734bf85 and on the final classes
  (b67525431): all eight byte for byte (sha256: bank 3dde2f48, bonds 90de0ef2, borrow 45081590, build 14b74f91, house
  763548ef, labour 69cae966, month 69d84440, pop 4056e91a). The two reports differ only in their clock times and one
  object's identity hash in a printed works decision; both runs' one red is main's (`an old works moved`, RN1-H-M). No
  ensemble: the default trace does not move.
- `sh tools/pick.sh origin/main HEAD`: ALL (`AllChecks.java` registers FitoutCheck, so the suite reads the diff); review
  needed (saves: `DataSave.java`). The whole suite is the dry run's.
