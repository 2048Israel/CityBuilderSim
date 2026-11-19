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
