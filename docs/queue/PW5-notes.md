# PW5 — substations

Brief: QUEUE's PW5 row, spec-electricity sections 2.7 and 5, and PW1-notes section 0.
Built from origin/main 01e6c2d4b (0.9.188). Money and saved flows change; stranger review owed.

## What changed

Substation is appended as permanent building id 155, a city-owned Electricity building.
It supplies carry, never generation. The existing utility staffing, construction, repairs,
city capital spending, money audit and national accounts pay its actual costs once.
The tariff's capital recovery is revenue to that same owner, the treasury, rather than
a second capital payment. Repairs go to Construction through CITY_REPAIRS; filled posts
go to households through the utility payroll. No additional audit debit or GDP output is invented.

BuildAdvice's Power grid measure uses the normal AutoBuilder essential-work path, as
the roads do: peak coverage, sites counted once, money and debt, land and real construction.
The Build screen offers the new measure and its peak-carry card. Generation remains its
own measure. Each dispatch half is held to the wires before fuel or hydro water is used;
the difference is in the existing unserved kWh, short-block count and cap-price books.

## Calls

- ★ The study's recommended 100 MW per substation, 4% real capital rate and 40-year life.
  Capital recovery uses that annuity, over twelve monthly bills; today's month's delivered
  kWh times twelve supplies the annual denominator. No demand means no wires invoice.
- ★ $440M replacement value: cash 264,000 and 9,778 material units at $18,000, $440.004M
  before tax. Construction is 132,000 points. A five-acre yard represents the district's
  lines, transformers and meters; no wires are routed. The normal 1% annual repairs and
  140 posts (100 diploma, 30 college engineering, 10 university engineering) approximate
  the study's one-third posts/repairs, two-thirds capital split at founding wages. Actual
  filled payroll, current replacement cost and builders' tax set the bill, not this estimate.
- ★ Preserve PW1's free founding 10 MW network, including its existing wires. Substations
  replace that starter carry: max(founding carry, substations × 100 MW), not an extra 10 MW
  on each developed grid. Otherwise a new town would lose its promised founding supply.
- ★ Heavy industry and mining retain PW1's transmission-only class. The AEO's 1.885/5.938
  transmission share divides actual grid cost; other customers pay the whole wires share.
  Homes remain unbilled until PW8, as the brief specifies, so full cost recovery is not guaranteed.
- ★ Snapshot costs when dispatching; strike them once with that dispatch. DataSave.powerBooks
  carries Book.gridCosts and pendingGridCosts; restorePendingPower restores the completed
  flow after the last load rebuild. Redenomination scales both snapshots. No HistorySave
  series is added. Old pending dispatches cannot supply those costs: SAVE_FORMAT and the
  oldest-loadable floor rise together, with no conversion, from the shipped value after merging main.

## Checks so far

- Initial `--fast`: green.
- SubstationCheck: nine assertions green, including 99/101 MW with one substation, the
  starter-only city, per-half conservation and cap price, actual building costs, cost changes
  leaving the earlier book alone, sites 0/1/1 after peak growth, and exact save/load snapshots
  and the first post-load strike. Cities use GameFiles.scratch and slot 10 only.
- First PowerPriceCheck found its deliberately large generation-short fixture could not
  fund eight substations. The fixture now funds the quoted wires before the audit window;
  its generation-short premise remains, with enough wires to isolate it.

Final named checks, pre-PW5 rule failures, review, ensemble and gate results follow below.

## The finding against main

Compiled untouched 01e6c2d4b in a detached scratch worktree (`--fast`, no baseline suite).
Copied only the compiled SubstationCheck.class into those classes and ran that named
harness's `--rules` section. It uses the old model's existing inputs and reads its book
through Gson, so no new model class or data row was transplanted. Exit 1, six assertions
failed: the 99/101 MW carry boundary, per-half conservation at the carry and shortage
price, the city held to its starter grid, costs in the wires' bill, a changed building cost
changing the next bill, and the peak-growth order with its site counted once. They all
pass on PW5. Raw output: target/PW5-main-rules.txt; the six labels are the harness's
first six assertions. This is a mechanic finding, not a compile-failure comparison.

PowerPriceCheck now passes all 77 checks, including filled grid payroll, actual bills,
the treasury's margin, fuel sellers and all fixture-month money audits. BuildingDataCheck's
first pass identified its exact dated-id map needed the appended 1900 Substation entry;
the map was extended, preserving all earlier dates and the exact equality check.

SaveFileCheck's 150-month city exposed a residual FS17 premise, rather than a failed save
comparison: its poorest cell had zero income and negative money after fixed bills, yet
the first struck month still bought every basket. Emptying savings, foreign cash and
city paper left shares/corporate bonds for the normal liquidity waterfall to sell. A
diagnostic showed first-month food money 0.0569964 and 2 of 2 baskets asked; the second
month had only 0.00000194776 and asked 0.000208009 of 2, one strike too late for the
hunger-at-full-shelves reading. A credit lockout experiment changed neither result and
was removed. Removing securities outright caused fourteen issuer/holder/debt save
comparisons to fail: the load correctly recounted the missing bonds. The fixture instead
gives those securities to another populated cell, with the proportional acquisition/tax
stocks and bond faces, preserving issuer debt and holder totals. Its chosen cell is
without means before both strikes. Every nonzero hunger assertion and every
save/continuation comparison remains. Kept diagnostic:
target/PW5-results/SaveFileCheck-premise.txt. No production household rule changes.

## Final checks before review

One harness per batch-check invocation, sequentially, with the required Earth/world paths:
PowerPriceCheck (77 assertions), BuildingDataCheck, PowerDispatchCheck, SubstationCheck
(all nine assertions), SaveFileCheck (including the repaired premise and every save identity),
ReadPathCheck, GdpCheck and PowerViewCheck all exit 0. Raw named logs and batch summaries
are retained under target/PW5-results/. No known-red waiver was needed for these runs.
AutoBuildCheck also exits 0; its one existing path assertion remains OBSERVED, and no
assertion was changed for PW5. The new substation rules are nine strict assertions.

Visual cues: Utilities' Power grid ring and Substation card say what peak kW the wires
carry; the yard appears through the existing utility building drawing. Services' existing
unserved-energy and short-half-block lines include a grid shortage.

The default trace changes: the city builds substations and its bills follow their costs.
The 80-run ensemble against the newest versioned baseline, and the ALL dry run, remain
for the stranger's SHIP/go, as the brief orders. No local full suite or ensemble was run.

Final `--fast` exits 0: compile, regenerated maps, zero firm Stale findings (196 soft,
unchanged from the initial main build), and the tree check. Source/notes whitespace and
LF checks pass; the generated month-order Markdown retains its intended hard breaks.
`known-reds.sh PW5 docs/queue/PW5-ship.md` exits 0 with no branch waiver rows.
`pick.sh origin/main HEAD`: ALL, 242 harnesses. Its review floor is saves: DataSave.java,
+SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT; money review is also explicitly owed for the
changed grid tariff and treasury receipts. There are no threading changes.

At the review tip origin/main is still 01e6c2d4b (0.9.188); save format and its floor are
61, raised from main's 60. Merge main again on SHIP/go, reconciling that number above
HC1/RES2's shipped value before the ensemble and dry run. The provisional ship marker
stays in place for that review and the remaining checks.

## REV-PW5 mends of ccff8b92c

1. **Native old-save refusal.** Format60SaveFixture is shared by SubstationCheck.saved,
   SaveFileCheck's format-floor section and ReadPathCheck. Its two gzip resources are
   the actual save/history written by unmodified production classes of 01e6c2d4b,
   0.9.188/format60, at month 5 after four native played months. Only the fixture
   capture class was added to that detached scratch checkout. No current save was
   restamped, stripped or converted. The capture rejects a different executable
   version/format; resource provenance and SHA-256 hashes are beside the files.
   The checks validate the old field shape and four history rows, refuse into a fresh
   Game through refusedAtTheFloor, and refuse into a running current-format loaded
   twin. Full DataSave (apart from the save timestamp), full HistorySave, manager/history
   object identities, the named refusal and both files' bytes must stay intact.
   ReadPathCheck runs real readers before and after that refusal. HC1/RES2's equivalent
   fixture will be consolidated to one after the first batch lands and main is merged.
2. **Independent tariff oracle.** The harness no longer reads REAL_RATE/LIFE_YEARS or
   repeats production's annuity expression. A worked $440.004M, 4%, 40-year case pins
   capital at $1,852,544.78305859 a month and repairs at $366,670. Its controlled 90 MW
   peak, 720-hour, 5%-loss fixture delivers 51.3M kWh and independently pins the wires
   bills at $31.1468741481907 commerce and $9.88748025755129 industry per kW-month.
   It has no filled posts, tax or wage drift; the ordinary tariff check and the real
   PowerPriceCheck cities retain the actual filled-payroll coverage.
3. **Currency reform.** Removed the second multiplication of transmissionPrice and
   distributionPrice from UtilitiesHandler.scale(Book,...). SubstationCheck.reform
   tests a half-scale on both saved price components, both tariffs, struck and pending
   grid costs, restored state, and unchanged physical kWh/carry. The exact new line
   failed before the model fix (target/PW5-review-results/before-fix-SubstationCheck.log)
   and passes afterwards.

Findings 1 and 2 are coverage defects: once supplied, the stronger native refusal and
worked tariff cases agree with ccff8b92c's otherwise correct ordinary calculations.
Unlike finding 3, they do not claim a newly reproduced runtime error on its unmutated
production code. The missing real-fixture premise and the tariff oracle's shared wrong
inputs are the defects being prevented; mutation results and final checks follow.

- A control classpath restores UtilitiesHandler.java from ccff8b92c and omits the two
  new fixture resources; all other production classes are unchanged since that tip.
  Transplanting only the stronger checks gives exactly two failing assertions: the
  currency breakdown and missing native old-save fixture. With all three mends the
  named SubstationCheck passes. Logs: target/PW5-review-results/review-tip-no-fixture/.
- For the independent-oracle finding, compiled the original ccff8b92c harness and the
  new harness separately with deliberate 8% and 20-year Substations mutations, so each
  old oracle sees the same wrong dials as production. The old oracle passes both
  mutations (exit 0); the new oracle fails three exact capital/wires assertions on
  each (exit 1). The unchanged 4%/40-year model passes. These are fault-injection
  controls, not claimed ordinary-production failures. Logs: rate-old, rate-new,
  life-old and life-new under target/PW5-review-results/; no tracked source was mutated.
- SubstationCheck, SaveFileCheck, ReadPathCheck, PowerPriceCheck and PowerViewCheck all
  exit 0, one harness per batch-check invocation and one JVM at a time. The native
  old-save checks pass inside all three callers, including actual readers on the
  running loaded twin. The substation harness now has twelve strict assertions plus
  the shared native-fixture checks. Final fast/map and push checks follow.
- Final review `--fast` exits 0 (compile, regenerated maps, zero firm Stale findings
  and the tree check). Source/prose whitespace checks pass; no branch known-red
  waiver was introduced. The provisional marker remains for the delta review.

The review go remains outstanding. On its SHIP: merge main, reconcile the save format
above the shipped floor, then run the ensemble against docs/ensemble/0.9.188.md and
the ALL dry run, in that order, before dropping the marker.

## Delta SHIP and ensemble

REV-PW5's delta review of f1239be04 is SHIP (orchestrator mail
20261122-114759-597-orchestrator-cc5e). Fetched and merged the newest origin/main:
still 01e6c2d4b/0.9.188, already an ancestor. HC1/RES2 have not shipped, so the
save format/floor remain 61 above shipped 60, and there is one format-60 fixture.
No model or harness changes were needed after review.

The newest baseline remains docs/ensemble/0.9.188.md. Its only data overrides are
Earth/world paths; the box run omits those overrides so it uses its MD5-equal copies.
JAVA_OPTS is otherwise empty. The gate box is reachable at its configured IP and
had no serve jobs at the preflight read. Run the full 16 seeds by five policies on
the pushed commit, and keep the provisional marker. Before the ALL dry run, the
orchestrator now requires a separate `PW5 dry run?` mail and a wait for its slot.

### Ensemble result: PASS

`PW5-final-af641b6be-20261122` completed all 80 runs, all 2,400 months, with no
missing or short run, on gatebox's OpenJDK 21.0.13. The box paused it at 26 completed
cities for RES2's dry run, then resumed the same snapshot; it never ran on the laptop.
The result is read from the run's score.md, progress.txt, summary.json and snapshot.txt.

| measure | 0.9.188 baseline | PW5 |
|---|---|---|
| criteria passed | 23/26 | 24/26 |
| hard M1/F1 criteria | 10/10 PASS | 10/10 PASS |
| failed/unread audit months, every policy | 0/0 | 0/0 |
| F1 runs with findings, AUTO2 / ZERO_SLACK / ZERO_HOT / SHOCK / HIGH | 1 / 1 / 0 / 1 / 1 of 16 | 0 / 0 / 0 / 0 / 0 of 16 |

Informational verdicts changed: AUTO2 and ZERO_SLACK T2 become PASS (median final
populations 3,422,172 vs 2,470,540 and 1,827,872 vs 1,277,612); HIGH R2 becomes FAIL
(the reported unemployment spread is 0.50 points vs 2.29). No threshold or premise
was changed for those scores. M1 and F1, the ship stoppers, pass for every policy.

The baseline is committed as docs/ensemble/PW5.md, with the publication-version
placeholders only in its heading/model introduction. The snapshot is af641b6be;
the box used its own earth-2.bin/world-1.bin, whose full paths are in the baseline.
Kept files are under target/ensemble/PW5-final-af641b6be-20261122/ (including the
eighty CSVs and logs); launcher output is target/PW5-ensemble.txt. The box source
run was /home/jerus/cbs/tools/af641b6be82957745f7ab1285c4a4530571f4b0a/target/ensemble/PW5-final-af641b6be-20261122.

Fetched main again and merged its queue-only updates 6ec5ef03d in c96a5427d.
The model remains exactly the scored one: src tree c1977ce96c8ea099ad09cb6d3c309c9b0afffcf5;
`git diff af641b6be HEAD -- src pom.xml tools/ensemble` is empty. Shipped version
and format remain 0.9.188/60; PW5's floor is 61 and the native fixture remains single.
No dry run has started. The dry-run slot is requested after this result is committed;
re-fetch main again on the orchestrator's go and reconcile a newly shipped batch then.

### RES2 landed before the dry-run request

The final pre-request fetch found shipped RES2, 7e484c4da/0.9.189, at format/floor 61.
Merge that main, raise PW5 to format/floor 62, and retain the shipped native format60
fixture and SaveFileCheck.nativeCard60Refusal helper. Drop PW5's duplicate helper,
pair and provenance; SubstationCheck delegates to the shipped helper, while SaveFileCheck
and ReadPathCheck keep one shipped native-refusal hook each. No save assertion is
weakened. The merge preserves the appended Substation id 155 and RES2's housing data.

Generated-map conflicts are resolved by regeneration. Two small source conflicts keep
the shipped shared refusal hook and raise the format above the shipped value. A shell
quoting error in a first merge-edit command changed no source; direct patches resolved
the conflicts before checking or committing. The complete first ensemble above remains
valid evidence for its earlier model, but is superseded for shipping by the merged model's
checks and a new full ensemble. No dry-run request has been sent yet.
