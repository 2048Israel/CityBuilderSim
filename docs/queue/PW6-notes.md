# PW6 — the recorded power month (2026-11-21)

> P2 provisional mends after REV-PW6c1-1, based on `215d3a39cb5997fed7f7dd96444503f2a98fea0e`.
> The new negative control reproduced the three intended failures with all hard premises
> held. Positive PowerViewCheck, YearBookCheck and SaveFileCheck each exited 0 after exact
> checkpoint restoration. Scoped delta review remains owed; the source is not SHIP.
> The provisional ship marker remains; no ensemble/dry/screen/readiness GO was used.

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

## REV-PW6c1-1 P2 — optional marginal-cost history omitted

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

Scoped source-delta review against `215d` remains owed, including the field-only adapter's
unpublished-prototype omission versus the no-conversion rule; green runtime proofs do
not substitute for that review. Final-model ensemble against .180 and ALL-238 dry
ordering remain owed, without execution GO. The provisional marker remains.
