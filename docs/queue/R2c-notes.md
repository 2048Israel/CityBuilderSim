# R2c — Licence fees on the current account

Built from main d8e9c12b on batch/R2c. No known-reds entry names R2c.

## Design calls (★)

- ★ Book IP charges through the existing imported-service ledger before the
  monthly statement strikes. Its cash settlement, VAT, MoneyAudit TRADE scope,
  foreign current account and DataSave/SectorState statement restore stay one
  accounting path. Keep the named charge in HistorySave as well.
- ★ Only foundedUnfitted cities pay. The catalogue fee flag, the world's
  month through the next 240 months (exclusive), and local discovery decide
  liability; no fee on a loss or on an entry the city found itself.

## Verification

Initial --fast on unchanged main: exit 0. Default eight-file trace captured
before changing source; comparison and final checks follow.

- ★ A new building's gain uses its sector's own pre-licence earnings estimate,
  so refinery streams and service output use the same valuation as their
  planners. A thread-local pricing guard avoids charging the licence recursively
  when that estimate reads running costs. No pricing read changes city state.
- ★ A recipe's gain is its spread over the best earlier way on its template,
  at the same city prices. A later alternative does not erase its patent rent.
  Recipe/equipment gains scale by the firm's operating rate and actual fitted
  counts; a single discovery unlocking both is charged once.
- Only existing methods outside the concurrent RT2/KD1 methods were touched;
  Sector adds a small imported-service entry point, AllChecks adds its registry row.

- ★ Orchestrator ruling, bus 20261115-054740-579-orchestrator-04ac:
  R2c covers private firms only, as its brief says. City-operated works pay
  nothing here. Sea terminals' gain is public port savings, not Freight's
  earnings, so charging Freight would take money it never earned. The public
  treasury path and its planners are queued separately in the ship file.

## Fixture evidence

The first fixture exposed two setup errors: borrowing FittingCheck's village
without its main's output initialization, then an ordinary-price furnace
that made a loss and could not cause a positive fee. The final fixture keeps
the actual PE1 furnace and private firm, explicitly bids steel's world prices
up, and asserts positive gross gain and payment. No law or existing premise
was relaxed.

On pre-change main d8e9c12b's captured classes, the final 19-assertion harness
reports 5 passed, 14 FAILED (target/checks/R2c-before.log): the payment, expiry,
unpatented/local exemptions with positive-payment premises, both equal-gross
rankings, the net fitting gain, and nonzero save/read checks all fail. The same
fixtures on R2c report 19 passed, 0 FAILED. The paid flow was 361.077289966;
MoneyAudit residual 0.000000000477. The baseline harness uses unchanged main
classes first on the classpath, plus only the new check and its read-only fee
helpers from R2c; none of main's integration classes are replaced.

R2c-P passed tools/batch-id.sh and is queued in the ship file. This batch adds
no process rules or runner behavior; AllChecks only registers its new harness.

Focused final check: LicenceFeesCheck, RecipeCheck, EquipmentCheck,
FittingCheck, ResearchCheck, SaveFileCheck and ReadPathCheck, sequential JVMs:
exit 0; no red assertion. Maps regenerated, Stale zero firm findings. New
fixtures write no slot: prepareSaveForChecks snapshots in memory, JSON is
round-tripped in memory, and the actual sector/history load restorers read it.
The model charges once at startOfMonthUpdate's strike, before
SimulationEngine's construction/demographic phase; both halves are the one
Game.nextMonth window, with no duplicate payment in the engine.

Fixture review tightened the equal-recipe offer: PB2 arrives naturally in 1916,
inside PE1's patent term. The earlier draft restored B5 as an impossibly early
local find; the final premise now needs no forced research history. This is
a harness-only correction after f746d394; the money model is unchanged.

## Default trace

The default LongPlaytest on unchanged main d8e9c12b and the final model
f746d394 passed. All eight CSVs compare byte-identical with cmp:
target/R2c-main/default-*.csv and target/R2c-final/default-*.csv.
The logs are target/R2c-main/playtest.log and
target/R2c-final/playtest.log; both finish All checks passed. The eight main
hashes are retained in target/checks/R2c-main-traces.sha256. No ensemble:
the default trace did not move (batch.md section 6).

## REV-R2c-1 mends

The money review found P1 (standing refinery units valued as additional units
on spare feed) and P2 (fee-netted recipe gains reached default-city fitting
reads before the founding guard). Both are mended with causing fixtures.

- ★ Standing conversion units now use the feed their installed kind actually
  runs, at the city's stream spread and the firm's actual operating rate,
  less running and standing costs. The model dispatches kinds rather than
  individual units, so their feed is allocated by installed feed capacity.
  Additional-unit appraisals remain the planners' spare-feed estimate.
- ★ Default-city fitting returns before any fee netting. Its recipe selection
  and game-layer recipe gain use gross values, preserving its reads during a
  patent term as well as its monthly trace.

The expanded review harness passes 27/27. On f746d394's captured
LicenceFees/Recipes/EquipmentFit classes ahead of the final harness it reports
22 passed, 5 FAILED (target/checks/R2c-review-before.log): both installed-unit
payment assertions and all three default fitting/recipe assertions fail.
The causing refinery has two patented Small Cracking Units consuming all the
gas-oil feed, so the estimate for an additional cracker loses money; its
standing units now pay 426.487251068 and the audit residual is 0.000000000759.
The default fitting fixture bids coal up so E1's oxygen recipe truly saves
money; a zero-gain licence cannot conceal the regression.

RecipeCheck, EquipmentCheck and FittingCheck passed with the model mends;
the final causing-fixture run passed LicenceFeesCheck and Stale (zero firm
findings), regenerated maps, exit 0. Repeat default trace follows.
