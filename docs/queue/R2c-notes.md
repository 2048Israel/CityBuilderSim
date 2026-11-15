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

## Main merge for KD1

KD1 shipped untagged as main 4343a815. It changes only AllChecks,
EnsembleCheck, KnownRedsCheck and TimingCheck among Java sources; the game
model used by the retained main trace remains d8e9c12b's. Merge source is
clean, with LicenceFeesCheck's registry entry and KD1's runner both retained.
Only generated dials, harnesses and map README conflicted; they are regenerated
from the combined source before the merge commit. No RT2 method was touched.

Final capture-model LongPlaytest passed and all eight CSVs compare byte-
identical to the retained main model (target/R2c-captured/default-*.csv
against target/R2c-main/default-*.csv). Its log and exit are
target/R2c-captured/playtest.log and target/R2c-captured/exit (0).
KD1 changes no gameplay Java or packaged model resources, so this is also
the current main 4343a815 model baseline. No ensemble was run: the default
trace never moved. The final code is f59d577c, carried through the main merge.

## Coordinated versioned dry run

Orchestrator go: 20261115-070424-442-orchestrator-1fdf. Normal route:
sh tools/gate.sh R2c --dry-run. No local ALL run was duplicated (the
orchestrator explicitly assigned the gate's --pick as the ALL run).

Retained directory: /tmp/cbs-gate-R2c-kgeGFGIP
Branch: 6ee40b29ae72523b5d28180b50089e9d51ef7c85
Main: 4343a81525f831df83511ccae1cd26d5ef71c478
Launcher log: target/checks/R2c-gate-launcher.log
Verdict and final review follow.

## REV-R2c-3: standing valuation across private kinds

The first dry run was stopped at the reviewer's instruction, exit 130;
origin refs were unmoved. /tmp/cbs-gate-R2c-kgeGFGIP is diagnostic only and
will not be reused. The third delta review accepted capture timing, pending
save/load, accrual-month expiry, settlement/audit and default guards, but
found the root installed-vs-additional appraisal error also affected furnaces.

- ★ No installed building path calls estimatedMonthlyProfit. Processing works
  use their standing/removal values and their existing feed/freight costs;
  refinery units use dispatched feed; other private makers use grossStanding
  with running, standing, own and freight costs. Extraction also refuses a
  gain on worked-out ground.
- ★ Actual dispatched output (including direct exports and captive output)
  limits the standing rate. A staffed nameplate is not production. The new
  ProcessingSector.lastEarnsAt takes that already-limited rate, retaining
  standing price roles and costs without reapplying feed limits.
- ★ Standing recipe deltas also use the removal price roles: output at the
  replacement value, inputs at the value freed. Prospective appraisal remains
  the separate additional-unit question.
- The causing fixture has one real Machine Works buying local steel from the
  installed patented furnace. It waits for actual local sales, positive
  standing gain and a negative extra-furnace appraisal; it does not set trade
  tallies by hand. A captured positive bill then pays exactly and both accounts
  close. On the new model: standing 16453.692637449, addition -7.857775213,
  accrued and paid 1008.313026286. The final harness currently passes 36/36.
- On f59d577c's captured LicenceFees class, the same fixture reaches its
  economic premise but accrues and pays zero: precisely the two new
  assertions fail, 34 passed / 2 FAILED (R2c-standing-before.log).

- ★ Orchestrator ruling 20261115-073638-682-orchestrator-d42a: standing
  planners must net the standing royalty too; prospective appraisal stays
  separate. BusinessInvestment now has standingRunningCostOf alongside its
  prospective runningCostOf. ProcessingSector's standing readers use that
  path. Production capture values the standing stack; raw valuation suppresses
  both fee hooks to avoid recursion.
  No RT2 quantity hook or BusinessInvestment.planRetirement method changed.
- The net-planner assertion clears and reinstates only the captured memo for
  a pure read and verifies standing earnings rise by exactly that fee despite
  the additional-unit estimate remaining a loss. A later patent-status change
  cannot cancel a bill already earned. Repeated standing reads preserve paid
  and pending flows. The expanded harness passes 37/37.
- On the pre-mend model (f59d577c's relevant integration classes; the read-only
  helper addition leaves its standing read unchanged), exactly three new
  assertions fail: positive accrual, standing-planner netting, and settlement.
  34 passed / 3 FAILED, target/checks/R2c-standing-net-before.log.

- ★ Standing readers use a saved per-template, per-building memo of the last
  captured royalty. They never revalue empty loaded production rows or the
  next month's mix and prices. Capture publishes one immutable map after
  booking the exact total to the pending ledger; the memo does not pay again.
  DataSave copies and snapshots the map, Game resets and scales it with the
  other money, and the load restores it after the notional market rebuild,
  beside restoreSectorBills. Older saves read an empty memo.
- ★ Orchestrator 20261115-080557-063-orchestrator-3403 directed the existing
  normal scratch slot-10 save/load route. No new JSON load seam remains;
  readTheSave keeps its existing shape. Only a temporary GameFiles scratch
  folder is written, never a player's save. Both SaveFileCheck and
  ReadPathCheck exercise the full roundtrip between capture and the next
  standing read, requiring exactly the same positive memo and standing net.
  HistorySave retains accrued IP charges as well as paid IP charges.
- The final local fixture run passes 37/37
  (target/checks/R2c-standing-loaded2.log), including a full ordinary load.
  The new furnace's net standing gain is 15445.379611163, gross standing gain
  16453.692637449, extra-unit gain -7.857775213, and accrued/paid royalty
  1008.313026286.

Final standing-model focused validation: LicenceFeesCheck, SaveFileCheck,
ReadPathCheck, ForeignCheck, RecipeCheck, EquipmentCheck, MaterialsCheck,
SaveSlotCheck and ScaleCheck all passed, exit 0
(target/checks/R2c-standing-final-focused.log). The one SaveSlotCheck timing
assertion is soft under the normal soft route; no functional red appeared.
Maps regenerated with zero firm Stale findings. The ordinary scratch-load
comparison was then tightened from monetary tolerance to exact equality of
standing net and memo: LicenceFeesCheck remains 37/37, exit 0
(target/checks/R2c-exact-loaded.log). The ALL pick is reserved for the
coordinated normal gate, not duplicated locally.
