# AP6 notes — slot 9 at 50x

Built from `b7c7c19af` (0.9.212, format 66). Brief: `docs/queue/AP6.md`.
Jerus: “ok once again, slow down, slot 9”. No saved field or format change.

## 1. Measurements and kept evidence

Evidence is in `C:/Users/Jerus/AppData/Local/Temp/cbs-AP6-evidence/`.
AP5's kept `scratchpad/orig` was copied to its own `scratchpad/original` before any run.
The original AP2 `Perf9`, `PausedProbe`, and `JfrTop` sources were copied into `drivers/`.
`main-classes/` is the unmodified b7c7c19af compile. Every run uses `-Xmx4g`, explicit
`cbs.earth`/`cbs.world`, and `GameFiles` scratch directories. No player's slot was opened.
The first attempted headless run put the files one directory too high and read an empty
scratch city; discarded. The valid run loads dojha at m4866, 21,918,046 people.

Headless, 12 months after 2 warm: main mean **368.4**, median **346.1**, max **526.8 ms**.
Windowed: AP2's driver, rung 8 (50x), 5 s a screen, no save/load phase, 0.1 s paused,
2 redraw samples. This laptop was under other builders' load: compare the paired runs,
not AP5's earlier absolute figures. Full figures and after measurements follow below.

## 2. The calls and implementation

- ★ The posts past are read once per `getTotalJobs()` call, never held between calls.
  They depend on farms' horses and autonomous machines, independently of `POSTS_EDITS`.
  Each new call reads them live; every type retains the original integer arithmetic.
- ★ Reuse AP5's raw integer post sums and ordered sector grouping across the month.
  `BuildingManager` owns one transient, volatile `HeldPosts` outside `WorksValue`'s
  pass, keyed on AP5's atomic `POSTS_EDITS`; the pass keeps its own cache and counters.
  `getSectorStacks` exposes immutable lists of live stack references. A group survives
  a month boundary when its inputs have not moved. Each quantity and template getter
  in `totalBySector` stays live: the same terms, added in the same order. Offered
  shares, posts past and instances also remain live; no new floating sum is retained.
  `WorksValue.bareWhile` bypasses every held raw-post/group reading, including nested
  bare readings. The harness warms both reading threads before a writer hands off.
- ★ Publish the completed, unmodified raw-post/group value through `volatile`,
  owned by the building manager. A per-manager `ThreadLocal` could retain a
  discarded city through its stack callbacks and the thread's value map. Owning
  the value in the manager avoids that retention; no raw getter depends on a
  reading thread's NewUnit/LicenceFees context (audited below). This is a threading
  change for the orchestrator's stranger review, alongside the bond money change.
- The bond mechanism was diagnosed by a MemWatch helper (read-only, 15-line report).
  It found one sorted holdings-map fold per settlement, and recommended a narrow
  posting scope rather than a persistent money cache. A second helper implements that
  scope and its BondCheck fixtures; the builder reviewed both files and kept the
  settlement expressions and their order. Only its two household face writers
  use `putBondFace` during posting, collecting touched identities. A `finally`
  recounts each with the original sorted-id fold; its nested `finally` clears the
  scope even after failure. Every other settlement remains eager. No map delta
  arithmetic and no money figure held after the posting call. Counters and an
  eager-mode twin switch are transient, per market, and used by BondCheck.
  The helper found no aggregate reader between the precomputed weights/targets
  and this scope's end: capacity uses live face, cash and posting budgets.
- ★ Keep bond aggregates eager outside that narrow call. Primary issues, maturity,
  defaults, census/migration/estates/folding, redenomination, clear/reset and load
  retain their existing writers and recounts. There is no new retained money
  cache for any of those paths to invalidate. The two changed settlement writers
  are exercised on warmed native monthly twins, with partial fills on three ids,
  manual and waterfall trades, and an exception after the seller has changed.
- The private-investment trace was delegated, with one offline reader of the kept
  baseline JFR. It has 475/4,177 FX samples (**11.37%**). `getPostsWithheld` has 80
  inclusive (**1.92% FX**, 16.84% of that subtree; 79 first-app), `totalBySector`
  57 (**1.36% FX**, 12.00%; 55 self). Both are the same raw-post/sector walks:
  the monthly `HeldPosts` reuse fixes them. `EquipmentFit.choose` is only 13 samples
  (0.31% FX), `rank` 6 (0.14%), and `refreshLand` zero: no profile case to expand
  those paths. Dynamic new-unit pricing revisits fees and recipes, not a fixed
  figure that can be lifted over investment orders. The full offline report/source
  is kept under `codex-AP6-bonds/target/AP6-investment/` and copied into the evidence.
- A read-only helper audited the longer cache lifetime against every writer and
  getter context; no missing invalidator was found. It caught the new load fixture
  reading an old manager: `loadGameSave` replaces it. The warmed same-manager case
  now uses initialized `loadGame`; separate `loadGameSave` and `newGame` cases read
  the replacement manager's held figures against its own bare ones. This causes
  both real load paths instead of assuming they share a manager.

## 3. Checks, mutations, and trace

Initial `./batch-check.sh --fast`: green, compile 55 s, Stale 0 firm.
`LabourCheck` on the final model: exit 0 (one named harness).
`BondCheck`: exit 0. Its native eager/deferred twins each trade 9 times and write
18 faces per month on ids 2, 17, 101: **18 eager folds become 4 deferred folds**,
in each of two months. Cell/aggregate/book/saved/audit figures match raw bits;
manual/waterfall settlement stays eager, including after a deliberately thrown fill.
★ `MonthPostsCheck` runs LabourCheck's two controlled held-figure sections for
the mutation trials. The full LabourCheck keeps both sections; no assertion is
removed. This avoids replaying unrelated census and wages for every removed raise.
`MonthPostsCheck`: exit 0 before the trials. All 20 trials at `83389d532` were
new assertion reds; sources restored after each. A read-only evidence helper
confirmed post-writer bare-bit/rebuild failures for clear, finish and both recipes.
It found the instant-stack trial's missing `addStack` raise was masked by
`addQuantity`'s independent raise (the two reds were AP5's existing-order counts).
★ Added a separate **new on-site stack** witness, preserving the instant-stack
premise: warm the grouping, place an absent template with zero finished quantity,
then compare its ordered identities and every held reading with the bare walk.
The bare finished totals must stay unchanged, while membership grows by one.
The strengthened `MonthPostsCheck` is green (exit 0). Its 16 writer/input/work
mutations are repeated on that final harness; the bond harness and its four
mutations are unchanged by this test-only addition.
The final 16 cache trials at `a667a6afd` all produced assertion reds (exit 4),
not compile failures or exceptions. The four bond trials are at `83389d532`;
their model and harness are unchanged by the on-site membership addition.
Each notification below was removed alone, or the input/reuse was disabled,
then the named harness run, then the original source restored in `finally`.
The full normal compile/maps/Stale and both focused harnesses are green again
after restoration. Logs and the trial driver are kept with the evidence.

| mutation | harness | new assertion reds |
|---|---|---:|
| no raise in `BuildingsStacks.addQuantity` | MonthPostsCheck | 6 |
| no raise when `advanceConstruction` finishes buildings | MonthPostsCheck | 4 |
| no raise in `removeQuantity` (retirement/sale) | MonthPostsCheck | 7 |
| no raise in `increaseQuantity` | MonthPostsCheck | 6 |
| no raise in `BuildingsTemplate.setSector` | MonthPostsCheck | 5 |
| no raise in `runRecipes`' blend | MonthPostsCheck | 4 |
| no raise in `setFigures` (one recipe) | MonthPostsCheck | 4 |
| no raise in `setJobs` | MonthPostsCheck | 8 |
| no raise in `orderStacks` | MonthPostsCheck | 4 |
| no raise in `addStack`'s `finally`, including the new site | MonthPostsCheck | 4 |
| no raise in `clearStacks` | MonthPostsCheck | 4 |
| `heldPosts`' edit stamp fixed at 0 | MonthPostsCheck | 72 |
| always rebuild the month's raw posts/grouping | MonthPostsCheck | 12 |
| read posts past again for each job type | MonthPostsCheck | 3 |
| omit the live posts-past reading | MonthPostsCheck | 8 |
| omit the live offered-share reading | MonthPostsCheck | 3 |
| omit the final touched-cell recount | BondCheck | 20 |
| seller puts face without joining the touched set | BondCheck | 20 |
| buyer puts face without joining the touched set | BondCheck | 10 |
| leave the deferral scope set after posting | BondCheck | 49 |

The month reads every value `postsReading` covers as raw bits: jobs by type,
every post, withheld by type and sector, and sector totals by jobs, capacity
and an order-sensitive weight. Writers are warmed first, including a site
finishing, both recipes, ownership, reorder, clear, initialized load and sale.
`resetBuildingManager` uses `clearStacks`; `loadGameSave`/`newGame` replace the
manager and get fresh caches. Every sale path reaches `retire`/`removeQuantity`.
Instances, changing offered callbacks, horse/operator shares and arbitrary
sector getters are live; a newly added instance is checked against the bare
array. AP5's people-input cases remain in both harnesses: heads/licences through
live arrays, prisoners, workforce, owners, studying through both setters and
the kept array, and the wage ladder's bases. No conservation or save law changed.

## 4. Trace and final validation

The unmodified b7c7c19af default `LongPlaytest`, `-Dplaytest.trace`, `-Xmx4g`,
explicit Earth/world paths, exits 0. All eight hashes are also AP5's post-QR1
hashes. Main's e02e09bc0 queue-only update was merged at d06efc365: no model
file changed. The final AP6 replay and remaining validation are recorded below.

After all mutations were restored, a normal `./batch-check.sh --fast` is green,
Stale 0 firm; `MonthPostsCheck`, `BondCheck`, `CapitalGainsCheck`, `OrderBookCheck`,
`SaveFileCheck`, `ReadPathCheck`, and `ReloadCheck` each exit 0, one per call.
`AutoBuildCheck` exits 3, only main's recorded care-fee fixture red (FS12), as in
AP5. `LabourCheck` also exits 0 on the same model. No conservation, save or
round-trip assertion was weakened.

The final AP6 default replay (class snapshot `a667a6afd`, after all mutations
were restored) also exits **0**. Both runs use the same explicit Earth/world
files and `-Xmx4g`, one model JVM at a time. All eight complete trace files are
identical; verified automatically again after both processes exited:

| trace file | main b7c7c19af (e02 docs only) | AP6 a667a6afd |
|---|---|---|
| t-bank.csv | aa2af5f1316c33dde8ba9366884ce152 | aa2af5f1316c33dde8ba9366884ce152 |
| t-bonds.csv | fa135be296fe76e98685d040a2d05a33 | fa135be296fe76e98685d040a2d05a33 |
| t-borrow.csv | 36b47896b1f126a8b75ef1102132319d | 36b47896b1f126a8b75ef1102132319d |
| t-build.csv | d2e6afe778756cef8b4c8186b0c316df | d2e6afe778756cef8b4c8186b0c316df |
| t-house.csv | 96cf152bb7b38f96e2198749252b88b2 | 96cf152bb7b38f96e2198749252b88b2 |
| t-labour.csv | e9cf9e0947a832addd388e041eaeccaf | e9cf9e0947a832addd388e041eaeccaf |
| t-month.csv | 9989337eb618a85564fa3d65307aeabe | 9989337eb618a85564fa3d65307aeabe |
| t-pop.csv | c6f5e3bacc4ae22a60a9be9775855794 | c6f5e3bacc4ae22a60a9be9775855794 |

No ensemble: the trace is unchanged. Full logs, CSVs and classes are kept in
`main-trace/`, `ap6-trace/`, `main-classes/` and `ap6-classes/` under the evidence
directory. No player save slot was written.

## 5. Slot 9, the adjacent pair

All three headless runs load the same m4866 city, population 21,918,046, auto on.
12 measured months after 2 warm; median is the driver's upper middle observation.

| classes/run | mean | median | max (ms/month) |
|---|---:|---:|---:|
| main b7c7c19af, initial | 368.4 | 346.1 | 526.8 |
| main b7c7c19af, adjacent | 293.4 | 278.6 | 402.6 |
| AP6 a667a6afd, adjacent | 271.3 | 257.4 | 404.9 |

The adjacent mean improves **7.53%**, median **7.61%**; the max is 0.57% higher.
The changed laptop load is visible in the two main runs: do not compare AP6 with
the initial run or AP5's absolute figures as if they were one pair.

The same AP2 driver at 50x, 5 seconds a screen; each entry is months/second,
then frame p50 / p95 in ms. Full maxima and source-launch commands are in
`after-profile/report.md` and the kept reproduction scripts.

| screen | main, adjacent | AP6 | FRAME_P95 |
|---|---|---|---:|
| Build | 3.7; 174 / 335 | 6.7; 128 / 201 | 99 |
| Land | 3.9; 240 / 340 | 6.1; 152 / 280 | 57 |
| People | 3.6; 258 / 436 | 8.1; 114 / 164 | 34 |
| Services | 4.8; 197 / 272 | 7.9; 118 / 192 | 37 |
| Infra | 4.3; 247 / 273 | 10.0; 59 / 132 | 43 |
| Sectors | 4.8; 196 / 272 | 9.7; 59 / 122 | 65 |
| Govt | 5.4; 179 / 251 | 9.9; 15 / 89 | 36 |
| Finances | 4.3; 237 / 264 | 9.8; 14 / 84 | 32 |
| Bank | 5.7; 171 / 240 | 10.0; 15 / 92 | 42 |
| Trade | 7.1; 138 / 190 | 9.9; 16 / 86 | 20 |
| Policy | 9.0; 102 / 150 | 9.8; 72 / 109 | 32 |
| History | 6.4; 136 / 271 | 9.9; 17 / 91 | 60 |
| Rankings | 7.7; 112 / 224 | 9.9; 17 / 98 | 60 |

All p95s still fail their budgets; Build, Land, People and Services remain
materially below 10 months/second. The driver's separate clock-month median/max
is 218.2 / 2834 ms on main, 184.5 / 2267 on AP6 (max includes opening autosave).
The adjacent headless MODEL logs match after removing paths/load/timing/heap,
month/window and world-solver timing lines: **515 retained lines, zero differences**;
all 14 monthly populations match. This supports, not replaces, the raw-bit proofs.

## 6. One final profile; AP7 queued, not built

As required while still short of 10, the final window recording was read once
offline: `ap6-ui/ui-4866.jfr`, 66.56 s, 9,366 total ExecutionSamples, **3,696 FX**,
no missing/truncated FX stacks. The reader used `-Xmx256m`, one active processor,
Serial GC; exited 0 and left no JVM. Full source/counts in `after-profile/`.

| inclusive method | samples | % FX |
|---|---:|---:|
| Game.nextMonth | 3110 | 84.15 |
| AutoBuilder.pass | 1312 | 35.50 |
| WorksValue.relief | 1023 | 27.68 |
| BuildAdvice.supplyDemand | 497 | 13.45 |
| EquipmentFit.priced / fittedForNew (each) | 269 | 7.28 |
| BuildingManager.getKnownTemplates | 258 | 6.98 |
| BondMarket.takeMonth | 566 | 15.31 |
| BondMarket.postHouseholds | 521 | 14.10 |
| Household.recountBonds | 68 | 1.84 |
| BuildingManager.totalBySector | 63 | 1.70 |
| BuildingManager.getPostsWithheld | 29 | 0.78 |
| Game.runPrivateInvestment | 371 | 10.04 |

Inclusive rows overlap. `getPostsPast` has zero sampled frames, which does not
mean zero runtime: the array getter now reads the supplier directly once.
`totalBySector` self is 13 samples (0.35%); its nearest app frames 22 (0.60%).

★ Queue **AP7**, validated free by `tools/batch-id.sh`, for the repeated city
eligibility reads in the maintenance valuation, not another change in AP6.
Under relief: `warranted` 357 samples (9.66% FX), the direct count call 276
(7.47%), known-template scans 216 (5.84%); the latter repeatedly ask research.
`warranted` takes no projected date or added-buildings argument. AP7 must first
prove which answers are fixed within an unchanged valuation, then reuse those
without changing projected arithmetic or order. Expected `WorksValue` and
`BuildAdvice`; the brief is `docs/queue/AP7.md`. No AP7 code was built.

## 7. Handoff

`tools/pick.sh origin/main HEAD` picks **ALL, 258 harnesses**. Its review line:
money `BondMarket.java`; threading `+volatile` in `BuildingManager.java` and
`+new ThreadLocal` in `WorksValue.java`. Saves are unchanged. The required
stranger review is the orchestrator's on the provisional mends handoff.
At that provisional handoff, no local `--pick`/ALL run, dry run or ensemble
had been started. The marker stayed provisional pending review/go and the
approved gate-box dry run; no gate-run comment had yet been added. Main was
fetched again after the trace: e02e09bc0, still only the queue update, already
merged.

REV-AP6 cleared `c98f7dd66` for shipment (2026-11-26): no P1. The review found
no aggregate-bonds reader during the deferral window, confirmed the fold on a
throw, every held-post writer's notification after its write, no new race/CME,
and the mutation table matched. Its one non-blocking note is addressed: the
public `getSectorStacks` javadoc now explicitly describes snapshot membership
and says to re-query after an edit. Documentation only; arithmetic unchanged.
The orchestrator approved the ALL dry run and reserved the box for AP6.
The documentation clarification passes `--fast` (compile, maps, Stale 0 firm).
Origin/main was fetched before starting it: unchanged e02e09bc0.

## 8. Approved dry run and the funded-sale red

The approved ALL dry run is kept remotely at
`/home/jerus/cbs/runs/cbs-gate-AP6-n2pixxVo`, copied home to
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-AP6-n2pixxVo`.
Its branch was `e89531244f0db5da2cf3a4e550f4ae144f7333ec` and its main
was `e02e09bc00d9b79ff2194c8164bf932a0c006a93`; the run's main before/after
heads agree. Compilation passed in 9 seconds. The pick was ALL, 258 names;
the actual suite ran 245 harnesses in 1109 seconds: 229 green, 15 known-red
and one unnamed-red harness. The suite step finished in 1127 seconds with
exit 4, and the gate/client exited 1 at 19:32:13 UTC on 2026-11-26.

The three unnamed lines are `KeptCheck | exit 1`,
`KeptCheck | fixture: Term 10 sold through the model, its proceeds fund the treasury and its buyers owe the settle FAIL`,
and `KeptCheck | 1 FAILED`. The failure is section 92, at
`checks/KeptCheck.log:5285`; the following line reports a sold LongTermBond
due month 4487 and the subsequent book/redraw assertions pass. The retained
log does not print the seven operands combined by the funding assertion.
At the stop, the signature was absent from both pinned main's records and
the provisional ship file.

★ Keep the red hard until its cause is proved. A read-only helper traced
the fixture and unchanged treasury booking code, but could not assign the
failure from the log alone. Authorize one named `KeptCheck` on pinned main
in the separate detached `codex-AP6-kept-main` scratch worktree, with only
failure diagnostic printing: the original assertion and tolerance remain
verbatim. No source changes on AP6 after its dry run, no full baseline suite,
and no ensemble. The provisional marker remains. A second read-only helper
was briefly started before a queued resume notification arrived; it was
stopped, made no edits or JVM runs, and the first helper retains the task.

The dry-run result was mailed to the orchestrator. Origin/main was fetched
again during the diagnosis and is still the pinned `e02e09bc0`.

### Pinned-main control: exact signature reproduced

The helper ran exactly `./batch-check.sh KeptCheck`, with the required named
Earth/world data, in detached main `e02e09bc00d9b79ff2194c8164bf932a0c006a93`.
It completed in 1597.2 seconds: KeptCheck exit 1, batch exit 4, foreground
waiter exit 0. All owned run/wait sessions ended. It reproduces the same
Term 10 funding assertion at month 4367, due month 4487. The only scratch
source edit adds 34 failure-only diagnostic lines; the original funding
expression and tolerance are preserved verbatim. No scratch change is on AP6.

| Operand | Main diagnostic value |
| --- | --- |
| Cash before | 8505577380096.2520 (`429ef16fac640102`) |
| Cash after | 33402234151322.850 (`42be61104d219ada`) |
| Quoted proceeds | 24896656771226.598 (`42b6a4b462089a99`) |
| Measured cash difference | 24896656771226.600 (`42b6a4b462089a9a`) |
| Cash residual / allowance | 0.00390625 / 0.001953125 |
| ULP of old cash / proceeds and new cash | 0.0009765625 / 0.00390625 |
| Auction count | 230 to 231 |
| Settlement due | 0 to 24896656771226.598, residual 0 |
| Sale face / unsettled face | 25000000000000 / 25000000000000 |

All six other conditions pass: auction tag, positive face, positive proceeds,
auction count, settlement-due delta and unsettled face. The cash-difference
condition alone fails. Its allowance is twice the old-cash ULP, smaller than
one ULP at the proceeds/new-cash scale; normal addition/subtraction rounding
therefore produces a residual of one larger-scale ULP, above that allowance.
This is an exact reproduction of the new assertion on pinned main, rather than a guess from
the different AP5 city. `KeptCheck`, `Game`, `Debt`, `DebtManager` and the
treasury booking path have no AP6 source changes.

★ Name the exact red under the existing queued **HT1-H**, which owns the
remaining played-city fixture findings (including rounding edges). FS5 swept
this section but has shipped, so it is not the queued fix. Do not change the
funding check or fixture in AP6. The source/math, conservation/save checks,
and trace proof remain unchanged. The stopped suite may be rejudged with the
new row without replaying any harness or mutating the kept run.

The complete diagnostic evidence is preserved under
`C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/codex-AP6-kept-main/target/`:
`AP6-main-kept-diagnostic.diff`, `AP6-main-kept-diagnostics.txt`,
`AP6-main-kept-result.json`, `AP6-main-kept.txt`, and `checks/KeptCheck.log`
and `checks/suite.txt`. The helper made no commits/push, real-slot writes,
parent-source edits or kept-run changes. Origin/main was fetched once more
after the control and is still `e02e09bc0`.

The main diagnostic files, command/pins and the two check logs were also
copied to `C:/Users/Jerus/AppData/Local/Temp/cbs-AP6-evidence/kept-main/`.
The original helper worktree and the gate's kept directory remain intact.

Rejudgement of the copied ALL suite used `CBS_REJUDGE=1` and
`./batch-check.sh --judge target/AP6-dry-rejudge/checks/suite.txt`.
Its immediately recorded exit is **3**, `known red only - 16 harnesses`,
and `checks/new-reds.txt` is empty. The combined known-red rows validate
with `tools/known-reds.sh` (exit 0). No harness was replayed for this verdict.
The copy, combined rows and verdict are in `target/AP6-dry-rejudge/`.

★ Read the judge's recorded status rather than treating PowerShell's outer
exit 1 as a new failure: the helper verified `exit.txt` contains 3 and the
saved verdict agrees. The Bash wrapper captured the judge status immediately;
PowerShell normalizes an unpropagated native nonzero exit. No additional row,
assertion or tolerance was changed after that outer status, and it was not rerun.

Only these notes and the ship file changed after the dry run. The code,
harnesses and generated maps remain the reviewed dry-run snapshot. All helper
and owned run/wait sessions have ended. The stopped gate asked for a renewed
`AP6 mends at` handoff after naming its new red; keep the provisional marker
for that handoff, with the kept ALL run and its now-known-only verdict.

The orchestrator accepted the renewed handoff on 2026-11-26
(`20261126-162209-873-orchestrator-a1fc`): the Term 10 signature is main's,
its HT1-H row is accepted, and REV-AP6's SHIP stands because only these notes
and the ship file moved. Its explicit ready go authorizes dropping the
provisional marker and mailing ready. Record this go before the marker's
separate final commit; no code, harness, map or kept-run changes, and no
additional model runs.
