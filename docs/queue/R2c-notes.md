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
f746d394 produced the same trace. All eight CSVs compare byte-identical with cmp:
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

Final capture-model LongPlaytest produced all eight CSVs, which compare byte-
identical to the retained main model (target/R2c-captured/default-*.csv
against target/R2c-main/default-*.csv). Its log and exit are
target/R2c-captured/playtest.log and target/R2c-captured/exit (1; see the final correction below).
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

## REV-R2c-4: freeze each producer's own production bill

The review found that checking isWorkedOut after groundLimit read the ground
AFTER the profitable final lift. The last magnetite dispatch therefore owed
nothing. This is the same state-boundary class as the earlier run change.

- ★ A market-pass Production record freezes each kind's whole Bill immediately
  after its own sector's output loop. Flow kinds are captured after its flow
  goods; stock kinds and refinery conversion units after its stock goods.
  No other producer, clearing, endOfMonth, endOrders or next-month decision
  intervenes. finish only books immutable owner/id/quantity/amount records
  and publishes their memo; it rereads no economics. Pending settlement
  remains at the next strike.
- ★ Flow extraction uses the actual opening-to-closing typed ground lift,
  not later ore availability or a sector-wide output supplied by another
  field type. Static nameplate, dig and installed counts allocate that lift
  among the works on that type. No installed gain path asks whether ground
  is worked out, or appraises an additional building.
- ★ Prices and trade splits are the producer boundary's values, frozen into
  its bill at production, rather than revalued by a later clearing. Later
  readers use the saved memo. Default cities create no Production record.

State audit by kind (all current private patented catalogue paths):

| Kind | Production figures and other inputs captured | Why there is no changed-state gap |
|---|---|---|
| E4 magnetite mines/complexes | Actual typed rock lifted between the sector's before/made hooks; site dig, same-type nameplate and counts; wages, utilities, repairs, tax, consumables and freight; standing output/input values | Hooks bracket only this sector's flow loop. Depletion is measured dispatch, never a later zero-ore veto. Hematite cannot supply magnetite's fee. |
| E14 forestry operations | Actual forest lifted by that flow loop, divided over standing crews' combined sawlog/pulpwood nameplate; rate and standing costs/values | The cut is already made. Salvage cannot masquerade as a fresh field lift. Planting and next forest state occur after the bill is frozen. |
| E12 leach/SX-EW and other stock extractors | Produced/export-bound/captive rows and capacity; actual operating rate; standing costs/values | madeAtRate has physically limited output to the ground. Capture follows its stock loop, before endOfMonth depletes or changes the field. No remaining-ground query determines this bill. |
| PE1/E13 steel, E4 pelletising, PE4 cement; E1/E9 recipes/fittings | Actual produced/export-bound/captive share of capacity; standing values, wages, utilities, repairs, tax, own inputs and existing freight/off-market-feed costs; recipe and fitted counts | Captured after the producer's complete output phase. No additional-unit feed appraisal is read; no later market or next run can alter the numeric Bill. |
| B2 contact centres and other ordinary private traded makers | Actual output share, capacity and operating rate; standing values and costs | Captured directly after flow output; later sales and clearing cannot revalue the frozen number. |
| PE27/PE11/E7/PE25/PE23 refinery conversion units | Kind's actual dispatched feed, installed feed capacity, current flow/rate, stream spread, wages/utilities/standing costs | Captured directly after its stock loop. Crude run/mix and tanks are not struck anew until endOfMonth; no later flow() call is used for payment or standing reads. |

Common inputs are month and Research catalogue/local-found status, installed
quantities, recipes and fitted counts, wage/tariff and exchange schedules,
policy/property costs and freight quotes. Game applies these before Markets;
the producer loop changes none. Goods' prices and trade tallies are read
only at that producer boundary and reduced to the numeric Bill before
another loop can change them. There is no mutable economic frame left for
finish or next month's strike. Public works/berths remain R2c-P.

The final exhausted-field fixture dispatches one tonne of concentrate from
the final three tonnes of magnetite rock, with independently positive
standing gain 2206895.359928730. It accrues and pays 746450.937943978;
MoneyAudit and the foreign current account close. A second month actually
makes 3996.840203359 tonnes from unpatented hematite while magnetite stays
empty, accruing no new magnetite royalty. Acquiring hematite can buy parcels
containing new magnetite, so the fixture explicitly exhausts that new ground
before the second month and holds quantities/land purchases fixed. This
causes the empty-type premise instead of assuming it.

On b9a75e7a's unchanged LicenceFees and Markets classes, the final fixture is
2 passed / 2 FAILED: its profitable final dispatch occurs, but accrual and
payment are zero (target/checks/R2c-last-ore-before-final.log). The new model
is 41/41, exit 0 (target/checks/R2c-production-frame-final.log), including all
prior boundaries, planners, audits and exact normal scratch save/load.
No RT2 changed method was edited.

Production-record validation: all nine focused harnesses passed, exit 0
(target/checks/R2c-production-focused.log): LicenceFeesCheck 41/41,
SaveFileCheck, ReadPathCheck, ForeignCheck, RecipeCheck, EquipmentCheck,
MaterialsCheck, SaveSlotCheck and ScaleCheck. The one SaveSlotCheck timing
assertion is soft on this route. Maps regenerated, Stale has zero firm
findings. Source and authored-record whitespace checks pass. The money mend
is 13b407a6; the following commit only records results and generated indexes.

## Final order, review and default invariance

Orchestrator 20261115-084749-965-orchestrator-5832: REV-R2c-5 is SHIP at
13b407a6. R2c now ships before RT2, whose fixture changes need review. No
new merge is made: main remains 4343a815. The earlier plan to merge RT2 is
superseded. Go was given for a versioned normal dry run, with the final trace
as its one additional JVM job. No source changes follow that review.

The final 13b407a6 model (b825662a adds only notes/generated indexes) gives
eight byte-identical CSVs against the retained main model: month, borrow,
house, pop, bank, bonds, build and labour. Final files are
target/R2c-production-final/default-*.csv; comparison marker trace-changed
is 0. Main 4343a815 changes only KD1's runner/check code from d8e9c12b, with
no gameplay Java or model resource changes. No main run was duplicated and
no ensemble was needed.

Correction to the earlier trace verdict wording: all retained direct trace
exit markers (main, initial, mended, captured and final) are 1, not 0. Their
logs finish All checks passed from their later checks and contain no FAIL
line; that tail alone was insufficient to call the whole program passed.
The eight-file identity claim is verified independently with cmp. The
coordinated ALL gate supplies the classified whole-suite verdict.

The normal dry run below failed; see the full-gate mend record. Directory: /tmp/cbs-gate-R2c-ZOb3FdRH
(C:/Users/Jerus/AppData/Local/Temp/cbs-gate-R2c-ZOb3FdRH).
Branch: b825662a8a9ae52c2e7b221024146d49e5008049
Main: 4343a81525f831df83511ccae1cd26d5ef71c478
Launcher: target/checks/R2c-final-gate-launcher.log
The nonzero direct exit is now traced: main and final reports have exactly
one identical finding, 'a building on the map moved' at m2389, the same
12 of 4301 boxes in district (-1,-1). It is already covered by known-reds
DM3-R (and the no-FAIL exit line is covered by CL6), not an R2c finding.
The gate's own LongPlaytest report has the identical finding too. All HA1
money fixtures at the tail pass. This explains the earlier misleading tail.

## Full-gate YearBook mend and PrizeCheck attribution

The ZOb3FdRH dry run is FAILED, not kept: suite exit 4 / gate exit 1,
27 unnamed lines. ALL selected 204 harnesses; the normal soft route executed
194, with 185 passing and 9 failing. The new IP history fields lacked
YearBook rules. That is this batch's omission. It caused YearBookCheck's
five failures and the dependent WorldMoneysCheck, InterestAbroadCheck and
ResearchViewCheck completeness failures. The refusal lines ending OK in
InterestAbroadCheck were only misclassified in that already-red report;
no classifier change is needed when its actual YearBook premise is mended.

- ★ Both ipCharges and ipAccrued are yearly FLOWS: add the month's bills,
  rather than taking December. The notes distinguish paid imports already
  inside importsAbroad from production accrued for its next strike.
- A two-year nonzero ramp for each key now requires 78 and 222, rather than
  the old fallback's 12 and 24, plus the flow mark. Against the old YearBook
  class it causes six more failures, 11/724 total
  (target/checks/R2c-yearbook-before.log). This is not an all-zero rule check.

PrizeCheck was proved on the captured whole pre-change main classes:
d8e9c12b's gameplay and PrizeCheck are unchanged by main4343's KD1 runner
updates. It reproduces both primary failures EXACTLY, including raised
1017.9557536877466 against 1014.5221050525304 and till 406 against a zero
cushion. Exit 2, 2 FAILED of 284, in target/checks/R2c-main-PrizeCheck.log.
The labels and figures match RT2's unchanged-main proofs. The same RT2-P
queued fixture repair owns the live-order quote and active-cost-cushion
premises; the prefixed derived summary is the existing R2b-J classifier
repair. The provisional ship file records those three rows. PrizeCheck's
premises and laws are unchanged here.

Orchestrator 20261115-092239-252-orchestrator-e071 supersedes the preceding
publication order: RT2 takes the dry-run slot and ships first. After these
mends, push and report mends; then merge the shipped main, compare all eight
traces against RT2's retained CSVs, and ask for a fresh R2c dry-run go.
The failed ZOb3FdRH directory must never be advertised for reuse.
The repaired YearBook passes all 724 assertions, exit 0
(target/checks/R2c-yearbook-after.log). The economic transfer/capture model
is unchanged; this completes the annual interpretation of its saved flows.
