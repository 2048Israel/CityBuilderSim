# R2c — Licence fees on the current account

Built from main d8e9c12b on batch/R2c. No known-reds entry names R2c.

## Design calls (★)

- ★ Book IP charges through the existing imported-service ledger before the
  monthly statement strikes. Its cash settlement, VAT, MoneyAudit TRADE scope,
  foreign current account and DataSave/SectorState statement restore stay one
  accounting path. Keep the named charge in HistorySave as well.
- ★ Only foundedUnfitted cities pay. The catalogue fee flag, the world's
  month through the next 240 months (exclusive), and local discovery decide
  liability in the production month; no fee on a loss or on an entry the city found itself.

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
The initial model charged at startOfMonthUpdate's strike. REV-R2c-2 below
supersedes that live calculation with a captured production bill; settlement
still uses the strike in the one Game.nextMonth window.

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

## REV-R2c-2: capture the dispatched flow

The second delta review confirmed the default guards and the cash/current-
account/audit settlement, with no softened assertion, but found live refinery
flow was reread after endOfMonth had changed its mix and run. The held fixture
hid a run cut after production.

- ★ Capture every private IP bill in Markets.clearMonth immediately after
  production and before sector endOfMonth decisions. The existing pending
  imported-service ledger saves the exact bill through DataSave/SectorState;
  the following strike banks that amount. Do not reconstruct a flow from the
  next run, mix, prices or recipe. This supersedes the initial live-at-strike
  design described above.
- ★ Orchestrator ruling 20261115-063259-369-orchestrator-7933: liability is
  the production month's patent window. Last-month production still owes its
  bill at the next strike after expiry. First post-expiry production accrues
  nothing. The proposed next-strike-date test was rejected and not retained.
- The unheld fixture starts at RUN_PACE, makes output on patented crackers,
  and cuts the next run to zero because crude is dear. It must retain a
  positive pending bill, round-trip it between production and strike, and
  pay it with closed money and foreign accounts.
- Exemption fixtures establish own discovery or an unpatented entry before
  production; they do not retroactively relabel a prior earned bill.
  The expiry fixture separately checks last-term accrual, settlement after
  expiry, and no new post-expiry bill.

The common capture route is docs/month-order.md, Game.nextMonth step 65
(finalUpdateEconomy), immediately after step 64's
SimulationEngine.simulateMonth. finalUpdateEconomy calls
EconomyManager.finalEconUpdate, which calls Markets.clearMonth. Inside that
single market pass, the capture is after substep 4 (produceStock) and before
substep 5 (sector endOfMonth). The clock, button and skip take the same route;
SimulationEngine is the middle half, with no separate market pass or duplicate
capture. Settlement remains the next startOfMonthUpdate's strike inside the
MoneyAudit window.

The final causing fixtures pass 33/33 (target/checks/R2c-capture-fixture.log).
The unheld refinery accrued and then paid exactly 29.642364640 after its next
run fell to zero; MoneyAudit residual -0.000000000175. Its pending bill and
zero run survive JSON through DataSave/SectorState restore before the next
full Game month. Both paid and pending flows also survive generic save/restore
and repeated reads. No slot is written.

On 7403e115's captured LicenceFees/Game/Markets classes, the same final harness
reports 25 passed, 8 FAILED (target/checks/R2c-capture-before.log): the boundary,
run-cut payment, pending save and pending read assertions fail. The unheld
production-and-cut premise itself passes on that code, so the fixture exposes
the erased bill directly. The initial cold village had no staffed power or
residents and made no production; the final fixture first lets the village
fill and its grid run, then stands the patented plant and asserts its actual
positive production. No condition was waived.

The 7403e115 default rerun also passed, with all eight CSVs byte-identical to
d8e9c12b (target/R2c-mended). A final capture-model comparison follows.

Final capture-model validation: LicenceFeesCheck 33/33; SaveFileCheck and
ReadPathCheck each finish All checks passed, exit 0
(target/checks/R2c-capture-save.log and R2c-capture-read.log). The other four
focused harnesses passed on the capture model. The final --fast compiled,
regenerated maps and reported zero firm Stale findings, exit 0
(target/checks/R2c-capture-fast.log). Source/record diff whitespace checks pass.
