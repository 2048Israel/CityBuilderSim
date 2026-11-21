# PW6 — the recorded power month (2026-11-21)

> Independent reviewer 8541: SHIP for `8079445ee..8b8e565eb`; both P2s closed, original
> whole-batch money/save/phase conclusions retained. Root's native receipt pins `8b8`,
> the hard two-failure negative, positive PowerView/SaveFile, stale-class proof and
> unchanged old archives. Source is final. Historical adapter results below remain
> labelled for `8079`. Marker retained: root authorized ONE final-model BOX-only ensemble,
> not dry/screen/readiness/publication. That single box ensemble is now complete and
> verified: 80/80, hard PASS 10/10, no changed verdicts; all 80 CSVs byte-match the
> retained published .180 artifact. ALL-238 dry ordering remains owed, without GO.

PW6 implements the accepted electricity study §5 power screen after shipped PW1, from
main `9d4467e8ae610a777f7ce445702c205f3984042c` / game 0.9.180. `moves_playtest=yes`
conservatively for the completed-flow save/load continuation mend below.
PW2–PW5/PW8 model work remains separate; no new fleet, fuel, tariff, household bill,
capacity credit, planner target, cooling, research or affordability rule is introduced.

## Scope and findings

`PowerDispatch` observes its existing passes and stable fuel sort without changing their
arithmetic or order. Immutable order indices let `UtilitiesHandler` capture bounded
catalogue-row records at dispatch: permanent id, actual half-block run, energy, fuel,
clearing receipts, offered/nameplate kW and marginal cost. Works' surplus and the founding
grid have reserved aggregate ids, not per-building records.

The timing matters: `SimulationEngine.updateEconomy()` strikes the preceding dispatch
before `updateServices()` dispatches the closing month. The following month's opening
statements and fuel purchase use that tariff/book. The view therefore presents one
complete retained dispatch, names its source month and the tariff's billing period,
and labels the older fuel payment's source and payment month separately. Row receipts
are clearing valuations before operating costs, not net earnings. Actual settled fuel
and upkeep are aggregate payments; the model does not allocate them to individual rows.

The firm gauge calls the existing pure `BuildAdvice.supplyDemand(POWER, NOW)` query at
dispatch, against the corresponding actual dispatch peak. The orchestrator accepted
this on bus instruction `20261121-173314-966-orchestrator-661a`. It adds neither PW3's
credits nor a reserve-margin target. Nameplate remains separate from firm capacity.

`PowerView` is a pure projection. `ServicesScreen` renders its six supply stacks through
Kept/Pieces, with stable row scopes, month/units, asked/generated/delivered/curtailed/
unserved energy, row fuel and receipts, the six clearing prices and tariff parts.
Industry excludes distribution; homes' actual draw is not presented as a household bill.

## Saves and history

Optional power observations append inside existing `DataSave.powerBooks`; no tax,
household, estate or other concurrent-owned save fields change. `HistorySave.powerSeries`
keeps tariffs, six clearing prices and loads, source/payment months, fuel bills and
permanent-row energy/fuel/receipts. Absent rows get genuine zero flows while absent;
older months are not back-filled. `YearBook` declares energy/fuel/receipts/payments FLOW,
prices RATE and source/capacity observations LEVEL. Snapshot/cut and currency-unit paths
include the new series; physical energy/fuel do not scale with currency.

Older saves lack optional row/firm observations and say so honestly until the next
relevant strike. No layout is reinterpreted and no conversion or format/version bump
is needed. The pending completed `PowerDispatch.Month` and all row facts the existing
strike consumes are also saved, including original source order, water, fuel/upkeep
rates and actual six runs. `Game.rebuildSimulationState()` dispatches twice while
rebuilding derived services. `restorePendingPower()` restores the saved completed
flow after the LAST rebuild, before its next native strike. Restoring after the first
rebuild was insufficient: the second overwrote it. No dispatch runs during restoration.

The causing fixture saved a real coal dispatch with nonzero generation/fuel and energy
price `2.4430446575743907E-6` city-thousands/kWh; without final restoration load rebuilt
it as `0.0`. This is completed-flow loss, not evidence that fuel price alone caused the
gap: the native fixture also changes House draw, and load reads native catalogue draw.
The orchestrator approved the narrow retention mend in bus instruction
`20261121-175054-595-orchestrator-c1ef`, with aligned future fixture inputs clarified in
`20261121-175308-641-orchestrator-4ffe`.

## Meaningful proofs and checks

`PowerViewCheck` causes native windy-peak/calm-peak shortage at the real cap, curtailment,
coal fuel burn, hydro and works generation, an empty built fleet with the founding grid,
and a thermal merit-order reversal when the fuel price moves. It compares all six prices,
run arrays, row totals, fuel/receipts, tariff wire parts and firm/peak with native records.
It verifies actual phase stamps, real scratch-slot save/load, older incomplete records,
unchanged retained records after later fleet/fuel changes, and actual monthly-to-year
FLOW folds using nonzero row receipts. Repeated view and existing firm-query reads leave
the serialized model, balances, investment and quotes unchanged. SaveFileCheck invokes
the real save proof; ReadPathCheck includes the view in its getter sweep.

Completed sequential wrappers before the final pending-dispatch proof: `--fast`,
`PowerDispatchCheck`, `PowerPriceCheck` (73 assertions, 39 audited fixture months),
`PowerViewCheck`, `SaveFileCheck`, `ReadPathCheck`, `YearBookCheck` (838 assertions), all
exit 0, maps regenerated and Stale zero firm findings. One own harness error tried to
JSON-encode existing not-applicable NaN advice figures; it was mended by comparing the
quote maps directly. No model premise or threshold moved.

Continuous-month arithmetic, dispatch order, fuel purchases and tariff formulas are
unchanged, but new-save continuation now consumes the retained completed power flow.
The fixture demonstrates that this changes a loaded continuation. Default LongPlaytest's
`roundTrip()` creates and discards a loaded `back` city rather than advancing/replacing
the live one, but its saved-versus-loaded service observations can change. An unsaved
month's purity alone therefore does not establish byte-identical default checks/trace.
`moves_playtest=yes` is declared conservatively; a final-model ensemble against
`docs/ensemble/0.9.180.md` is owed pending separate orchestrator execution GO. None was
run, and no new seed-length/default/baseline trace was started just to decide this.
The strengthened continuation proof keeps complete pending Month/row/source-order,
original tariff/payment/history equality and repeated-save premises. It then resets
both future fuel suppliers to the same native world-price fallback and House draw to
the same native catalogue value, checks matching fleet/current draw, and proves the
already-completed flow was untouched. Both twins play a real native month: next tariff,
carried bills, actual fuel-market draws and native audits agree. A separate nonzero
pending snapshot proves currency scaling of receipts/prices, with energy/fuel unchanged.

The explicitly NEW negative control disabled only the final `Game.restorePendingPower()`
call. Its wrapper exited 4: hard coal premise, older facts, aligned future fleet/draw,
bills, fuel and audits held; pending retention and next tariff failed. Exact source,
raw harness log and suite were archived BEFORE restoring positive source in
`target/PW6-negative-20261121/` and left untouched. The earlier original raw red was
overwritten by a wrapper before archival; it is not claimed as a preserved full log.
`target/PW6-pending-red-checkpoint.log` is actually a later green copy, despite its name.
The new control is the complete retained negative evidence.

The final positive PowerViewCheck passes every hard premise, complete retention,
older facts, aligned future fleet/draw, next tariff, bill/fuel identities and audits.
Final focused wrappers `PowerViewCheck`, `SaveFileCheck`, `ReadPathCheck` and
`YearBookCheck` all exited 0 after the pending-flow mend, with compile/maps/Stale green.
The last additional PowerViewCheck coverage explicitly removes optional pending rows/
Month and payment stamps from the restored state: older tariff facts survive while
unrecorded detail/payment timing remain unknown. Earlier PowerDispatchCheck and
PowerPriceCheck results precede the load-only retention mend; their dispatch/pricing
arithmetic was untouched. No unchanged named replay was used to claim new coverage.

## Review and ordering

Fresh stranger review is owed for saved model state, monetary load continuation and
Services UI scope. The actual `tools/pick.sh origin/main HEAD` result is `ALL 238
harnesses` (AllChecks append, 34 changed files from base 9d4467e8). Its exact review line:
`review    needed - saves: HistorySave.java`. That is a floor; the new pending model
state/monetary continuation and Services rendering also need review. A dry run is owed,
only on orchestrator GO, after the final-model ensemble and review ordering.
No full pick, LongPlaytest, ensemble, dry run, full Kept sweep or readiness/publication
has been started. This is a domain projection and its harness, not check machinery;
one-in/one-out does not apply. No publisher-owned records were edited.

Ask root for a separate screen-check session: inspect narrow/wide wrapping, all six
stack ticks and row colours, dates versus billing/payment periods, empty/founding and
old-save messages, month updates and scroll position. An eye check is not a Pending row.

## Historical `8079445ee` P2 attempt — optional marginal-cost history omitted

This section records the earlier adapter attempt and its actual evidence. Scoped review
55f3/root rejected the adapter as conversion code. It is absent from current source;
the corrected native-only implementation and evidence are recorded in the next section.

The review found that `recordPower()` filled an absent row's optional marginal-price
RATE with zero, reducing its annual mean. Zero is correct for absent energy, fuel and
receipt FLOWs. Root chose to omit the optional per-row marginal series entirely. The
accepted study §3 requires tariff, fuel bill, block prices and energy by row in history;
it does not require marginal-cost history. Current retained row marginal cost and its
Services display are unchanged, as are global tariffs and six clearing prices.

The bounded source delta is `PowerView.java` (omit the one metric), `HistorySave.java`
(a Gson adapter on `powerSeries` only, dropping exactly `powerPrice:row:*:marginal` on
read/write, including unpublished optional prototype content), and `PowerViewCheck.java`
(new native leave/gap/rejoin and actual fold proof). Existing plain-array JSON values,
shared folding rules, null/NaN encoding, pending state, money/physical scaling, load
restore phases and screen code are untouched. No compatibility conversion is added.

The proof uses a real coal row with nonzero generation, fuel, clearing receipts
and marginal cost. Native retirement removes its full quantity; fixed native months
retain the already-completed final run, then cause its absence. The same permanent id
rejoins through the native stack API. The proof checks three absent strikes and the
rejoin at exact history positions against retained row facts, while checking actual
tariff and retained current cost. It then ages the actual native power arrays by the
existing `MONTHLY_KEPT` constant, calls `foldOldYears()`, and compares yearly FLOW sums
and the global price's true mean. Optional prototype JSON must not export, snapshot,
resave or fold a row marginal RATE. Those assertions now pass on the checkpoint source.

Before any later proof wrapper can overwrite evidence, pre-mend production source from
`215d` and proposed fixture snapshots are retained in `target/PW6-P2-proof-checkpoint/`.
That initial checkpoint was fast-only; its unexecuted status was reported truthfully.
The earlier immutable `target/PW6-negative-20261121/` archive is untouched. Root then
released the named slot in `20261121-183419-610-orchestrator-d7fb` for the controls and
three focused wrappers below. Prior green runtime results elsewhere in these notes
still belong to the earlier source checkpoint unless explicitly superseded here.

A static consumer trace found that `HistorySave.slim()` does not seed optional dynamic
power-series keys in its empty destination map, and `keptFields()` cannot map such keys
from its empty probe. Current production consumer `SavedCities.readSlot()` (L217, call
L227) requests `SavedCities.KEPT`: `CityMeasures.SERIES` plus fxRate/fxParity/usPrices,
none of the accepted power-history prefixes. UiProbe's old-reader benchmark also uses
that exact set; other callers are HistoryCheck/RankingsCheck. Full power-history save,
Game's full Gson load/restoreFrom, direct series/folds and the retained-book PowerView
do not use that path. Thus no current accepted PW6 power-history consumer loses its
data through slim/readKept. The latent explicit-request limitation is not changed here.
If a new consumer needs power keys, seed only requested destination entries and map
power prefixes to the powerSeries field in keptFields. Root was told this bounded impact.

Fast-only checkpoint validation exited 0 with regenerated maps and zero Stale firm
findings; no named/model/display JVM ran during that earlier held-slot resume.

The NEW P2 negative control retained the f68 fixture and reverted ONLY PowerView.java
and HistorySave.java to immutable `215d`. Its normal PowerViewCheck wrapper exited 4,
with exactly three intended failures: current marginal-series export, optional prototype
load/snapshot/JSON omission, and marginal-series omission after actual fold/JSON. Setup
held: nonzero native coal/fuel/receipt/cost, complete retirement and same-id rejoin,
three absent strikes, exact zero/rejoin FLOW positions, retained cost/tariff, actual
annual FLOW totals and real global price mean. It was a conclusive semantic negative,
not a setup miss. Full raw harness log, suite, exact three source files and exit were
archived BEFORE restoration in `target/PW6-P2-negative-20261121-1834/`. The two archived
production blob hashes match `215d`. This archive is left untouched.

Exact f68 production bytes were restored, verified by an empty source diff. Sequential
normal wrappers PowerViewCheck, YearBookCheck (836 assertions), and SaveFileCheck each
exited 0, including compile/maps/Stale with zero firm findings. Full raw logs, per-wrapper
suite/exit and exact source are in `target/PW6-P2-positive-20261121-1838/`. All native
phase/currency/save-continuation laws still pass. No unrelated named/fast replay,
fullpick, Kept, display, ensemble or dry run was added. All owned jobs are terminal;
the local named slot was released to root.

At `8079`, scoped source review of the adapter versus the no-conversion rule remained
owed; that review subsequently rejected it. Those greens do not stand for current
source approval or current reduced native-proof results. The provisional marker remains.

## Scoped turn 2 correction — plain map and native-only proof

Root instruction `20261121-185026-446-orchestrator-c254` accepted reviewer 55f3's P2:
the field adapter was conversion code, contrary to contract §1, "Write no conversion
code." Shipped .180 has no powerSeries and its generic Map schema needs no prototype
migration. The annotation and PowerSeriesAdapter implementation are removed entirely.
Existing Gson/TypeToken/TypeAdapter/JsonAdapter imports remain used elsewhere in the
class. No alternative cleaning/filtering route, format bump or shared fold change is
introduced. Current HistorySave.java bytes equal `215d`'s plain-map implementation.

The one-line PowerView.series marginal-key omission remains. rowLeaveRejoinHistory now
uses the actual native history snapshot; synthetic prototype injection and migration
assertions are removed. Observed nonzero coal generation/fuel/receipts/current cost,
native complete retirement and same-id rejoin, the three absent strikes, exact zero
FLOW/rejoin positions, actual tariff, yearly FLOW totals and global RATE mean remain
hard. Native history/export/fold JSON must lack the newly omitted row metric. The
pending-flow/source-order/money/currency/Services/save conclusions are unchanged.

The NEW minimal negative control restored ONLY the single PowerView marginal series
line from `215d`; HistorySave stayed the corrected plain map, and the corrected native
fixture stayed intact. Its normal PowerViewCheck wrapper exited 4 (class exit 2), with
exactly two intended failures: native marginal history/series export and its presence
after actual folding/JSON. Every hard setup/phase/FLOW/current-cost/tariff/year-total/
global-rate premise held. Exact raw source/log/suite/exit were archived BEFORE restoration
at `target/PW6-P2-native-negative-20261121-1851/`; it is immutable evidence. Both earlier
negative archives, their source checkpoints and the historical `8079` greens remain
untouched.

An attempted positive run reused the negative bytecode: my Copy-Item restore preserved
PowerView.java's 18:37:51 source timestamp, older than the 18:54:02 negative class, and
Maven's 3-second compile did not rebuild it. The class still contained the omitted key.
That attempt exited 4 and was stopped before SaveFileCheck; it is not a corrected-source
semantic result. Its exact failed raw/source/suite/exit, stale class and timestamp/hash
evidence are retained at `target/PW6-P2-native-positive-20261121-1855/`.

Writing the IDENTICAL corrected bytes with a fresh timestamp forced the ordinary full
compile (38 seconds). The compiled class then lacked the retired key. Sequential normal
PowerViewCheck and SaveFileCheck wrappers each exited 0, including compile/maps/Stale
with zero firm findings. Exact source/raw logs/suites/exits and build verification are
archived at `target/PW6-P2-native-positive-20261121-1857/`. No code, fixture, threshold
or causal premise was adjusted to obtain these results. Prior YearBookCheck's 836
assertions remain valid for its unchanged fold rules; no unchanged YearBook or separate
fast replay was run. The current native annual-fold proof ran in PowerViewCheck.

All owned jobs are terminal and the named slot was released. Relative to `8079`, this
correction changes HistorySave.java and PowerViewCheck.java plus generated indexes and
these notes; relative to `215d`, the remaining source delta is PowerView.java's one-line
omission and the native history/fold proof in PowerViewCheck.java. No slim/readKept,
pending restore, planner, money, Services or YearBook rule was reopened. Scoped source
review, final-model ensemble against .180 and ALL-238 dry ordering remain owed. No
ensemble/dry/fullpick/Kept/screen/readiness/publication GO was used. Marker retained.

## Final source review and record-only main integration

Root instruction `20261121-190619-534-orchestrator-d48c` records independent reviewer
8541 SHIP for `8079445eee57007712f5f102104b7ac09b937241..8b8e565eb31d7f24b74460bcdd7601bab5f00c8b`:
both P2s closed; the original whole money/save/phase conclusions remain. Root verified
the exact hard native negatives, positive wrappers, stale-class evidence and old archives.
No prototype/slim/native-proof or other source path was reopened after that verdict.

Fetched main `6b52db7a2` differed from the launch base `9d4467e8` only in QUEUE.md.
Merged it normally, without hand-editing any publisher-owned record, from branch
`8b8e565eb31d7f24b74460bcdd7601bab5f00c8b` to merge commit
`96bdb84b9edfcf5759483c1be6cf72d18ca74dd6`. Exact pre/post tree/blob hashes matched:

| protected path | identical Git tree/blob |
|---|---|
| src | fcd87bdfd0023c9314a7b1e254020aafd722adb7 |
| src/main/java (source and native proofs) | f9e9344408e95d8cf681d99eaeed62e18a6fefbb |
| src/main/resources | 4b4ecb83ff60907b3e2ab5f9025328db59570b3a |
| tools | 4acc0fee245d73633717373a18e78e9daee58210 |
| pom.xml | 299d33b1324a0397d7d4f83b5c1237da3493925d |
| batch-check.sh | bdd6cb5ca999fc6d25b4b673d716b8125406b228 |
| docs/harnesses.md | 762fc1a010c9339c78d5768a1338b33272950832 |
| docs/dials.md | effe5d4a9a4b9678f02ef150e783a89f69638cc3 |
| docs/month-order.md | ee8efe4b5078b962089477d7a192fa7433544c66 |
| docs/studies/spec-electricity.md | dda74c27bfff393c77689b5b61598d0a9a916871 |
| docs/queue/PW6-ship.md | 7dfdc6bc86f6e297aa349d5a18c66bc0a9daf377 |

No semantic integration, fast or focused replay was needed or run for that Queue-only
merge. Exact pre/post records are under `target/pw6-final-ensemble/`. A manifest pins
all 70 original PW6 evidence files before ensemble setup; its SHA256 is
`280bb50da6ba26d0801673acbc28d4ed2c2ad827c2a7e884ba52465fa9a40113`.
The source/proof/tools/pom trees and original proof bytes stay frozen through the one
authorized box-only 80-run ensemble, homecopy and score. Final ALL-238 dry, screen,
readiness and publication remain separately ordered, without GO here.

## One final-model box ensemble — terminal, sealed and hard PASS

The approved normal runner invocation was `PW6codex1-final180`, fresh/unused locally
and across the box before launch. Source snapshot
`72fa024acb7e97fb1b6d4553accf16c824a923fe` was pushed before execution and all tracked
source/branch/records stayed frozen through remote completion, homecopy and scoring.
No baseline replay, extra playtest, named/fast/fullpick/Kept/display run or restart was
performed. Existing PW1/WG3 ownership and local-fallback controls were reused under
ignored `target/pw6-final-ensemble/`; tracked ensemble/gate tools were not changed.

The guard proof executed Java/javac/Maven refusal shims and the production wrapper's
actual empty-HOST fallback. Maven was refused before any local model could start; the
normal runner and scorer then left that guard log unchanged. Its before/after SHA256:
`5bddbb0af187029dd609c666640d8fead4e08e8bac0e308dc801df87061afcea`.
Remote native PATH was unaffected. Preflight found no active owners/models or held box
locks. The normal box gate/nightly priority/yield protocol stayed in control.

| provenance | actual value |
|---|---|
| label | PW6codex1-final180 |
| native version | 0.9.180 |
| baseline | docs/ensemble/0.9.180.md |
| box/run | jerus@192.168.0.16:/home/jerus/cbs/tools/72fa024acb7e97fb1b6d4553accf16c824a923fe/target/ensemble/PW6codex1-final180 |
| native JDK | /home/jerus/cbs/jdk, Eclipse Adoptium 21.0.13 |
| full classes SHA256 | 6ac829c19d358b922c3580945f03c79c160a44a0f2859ac229acefa812f49e6a |
| classes digest definition | SHA256 of sorted relative-path file SHA256 manifest, 2,445 snapshot files |
| earth-2.bin SHA256 | c897b2421d0b742899c034eeba84a4668f6534cae85e9c5500fa9a26172ae33b |
| world-1.bin SHA256 | fbd11a6a3b880ffeeae4e5c07e7a9d950b3d28f17f7c77656aea7a1071a6fcc1 |
| actual invocation UTC | 2026-11-21T23:13:29Z to 23:28:14Z |
| execution | 80 native runs, 8 at once; native run wall 14.2 minutes; summed run time 111.985 minutes |
| start ownership | serve PID 2696670, parent 2696669; box/ensemble lock owner 2697175 |
| terminal ownership | no remaining owned serve/model/one-run processes; remote terminal sealed |

Terminal verification counted 80 UNIQUE DONE entries, each reaching its own requested
end month, all 80 CSV/timing pairs, no missing/short/NO DONE, and no finding rows.
All remote sealed CSV/log/time/snapshot/dependency hashes match the homecopy; data and
classes hashes still match the start. All 70 original pending-flow/P2/native/stale/old
prototype evidence files match their pre-run SHA256 manifest. Raw runner, ownership,
guard, start/terminal receipts, remote seal and validation are in
`target/pw6-final-ensemble/`; raw copied results remain
`target/ensemble/PW6codex1-final180/`. No kept folder or raw output was edited.

Every policy (AUTO2, ZERO_SLACK, ZERO_HOT, SHOCK, HIGH) passed M1 with `0 (0)` failed/
unread audit months and F1 with `0/16 []` runs with findings: all ten hard scores PASS.
Overall 25/26 active criteria passed; zero vacuous and one retired, unchanged. The
informational AUTO2 A2 remains FAIL (longest high-inflation spell 15 months, range 4–36,
against the below-12 threshold), as in .180. Changed verdicts: none. Timing medians in
seconds were AUTO2 194.2, ZERO_SLACK 156.25, ZERO_HOT 18.45, SHOCK 41.25, HIGH 34.0;
these builder timings are recorded, not a new speed gate.

A read-only SHA256 comparison also matched all 80 complete CSVs byte-for-byte to the
retained published .180 artifact `PW1c2-final` / model commit `7a06097bc`. That artifact's
raw snapshot retains its pre-publication native version 0.9.179; .180's committed
baseline identifies that exact label/commit. No baseline run or raw relabelling was
done. This verifies the ensemble traces stayed identical despite the independently
proved corrected save-continuation path; `moves_playtest=yes` remains the conservative
declaration used for that path.

`docs/ensemble/PW6.md` is the truthful producer baseline/summary, with only the normal
heading/intro version placeholders for the publisher. Source/proof/tools/pom hashes
remain those reviewed/frozen above. The one ensemble is discharged, every owned job
is terminal, and no local model was run. Pick remains ALL 238, review floor
`review    needed - saves: HistorySave.java`, covered by the recorded whole/scoped SHIP
reviews. Ask root for ALL-238 dry ordering next; no dry, screen, readiness or publication
is authorized by this ensemble GO. Provisional marker retained.
