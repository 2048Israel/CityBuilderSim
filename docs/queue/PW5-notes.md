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

The merged SubstationCheck, BuildingDataCheck, HousingInventoryCheck,
PowerPriceCheck, SaveFileCheck, ReadPathCheck and PowerViewCheck all exit 0.
Before re-running the ensemble, the coordinator additionally required a real
format-61 refusal (mail 20261122-123121-187-orchestrator-5504). Capture published
7e484c4da/0.9.189 itself with GridLegacyFixture: native format61 at month31,
thirty history rows, housingInventory present and both PW5 grid-cost fields absent.
No JSON is restamped or converted. The history SHA-256 matches the canonical
format60 history exactly, consistent with RES2's unchanged monetary trace.

Use one refusal implementation for both native pairs. Keep every format60
schema/hash check, add the literal format61 hashes/schema, preserve both files
after each refusal, retain full DataSave/history and manager/history identities,
and run readers before and after the loaded twin's refusal. All three callers
have their own format61 assertion. Provenance: src/main/resources/fixtures/pw5-format61/README.md;
producer: tools/fixtures/GridLegacyFixture.java, guarded against the wrong build
or an executable already carrying PW5's fields. The new pair is a different old
format, not a duplicate of the shared format60 pair.

The format62/native61 integration recheck passes SubstationCheck (including the
literal format60 and format61 refusals), SaveFileCheck, ReadPathCheck, PowerPriceCheck
and PowerViewCheck, all exit 0 and one at a time. The canonical shared format60
checks and exact old hashes remain; there is one shared implementation and one
pair per historical format. No budget, conservation, continuation or save-equality
assertion was loosened during consolidation. Named logs: target/PW5-format62-results/.
The final format62 `--fast` also exits 0, regenerating maps with zero firm Stale
findings. Main remains 7e484c4da/0.9.189 at shipped format61; no further integration
is outstanding before the new box ensemble, again against the newest monetary
baseline, docs/ensemble/0.9.188.md (RES2 did not move that trace).

### Final merged-model ensemble: PASS

`PW5-RES2-069504753-20261122` ran on gatebox from pushed 069504753, with the
merged 0.9.189 code and PW5 format/floor62. All 80 runs finish all 2,400 months;
no missing/short run, failed/unread money audit or LongPlaytest finding. M1/F1
pass 10/10; overall 24/26 versus the baseline's 23/26. Informational verdict
changes are the same AUTO2/ZERO_SLACK T2 improvements and HIGH R2 failure noted
above. All eighty CSVs are byte-identical to the earlier pre-RES2 model's CSVs,
confirming RES2 did not change the monetary trace, while its new save shape and
PW5's format62/native61 refusals are now included in the checked model.

docs/ensemble/PW5.md now records this final snapshot, superseding the first
report for shipping. Actual data paths, host and OpenJDK 21.0.13 are recorded
there. Local kept files: target/ensemble/PW5-RES2-069504753-20261122/; launcher:
target/PW5-ensemble-RES2.txt (exit 0). The box run is
/home/jerus/cbs/tools/06950475380397d39da789e1101374135d75a129/target/ensemble/PW5-RES2-069504753-20261122.

The final pre-request fetch still has main at 7e484c4da/0.9.189, shipped format61.
The branch keeps one shared format60 pair plus the distinct real format61 pair,
and floor62. The orchestrator's separate dry-run slot is requested next; no dry
run has started. Re-fetch main on that reply before starting the ALL gate.

### Stopped first dry run and the harness mends

The orchestrator granted the box slot in mail 20261122-131650-449-orchestrator-53fd.
The pre-launch fetch still had main at 7e484c4da. The launcher waited for HC1's
ensemble, then ran ALL 243 harnesses on the box, from branch
a26a8ec58bfe2ca8c77922040a0ee1f334f83ebd against main
7e484c4da555ac6492a6e55d6101e1a5976de65f. Compilation passed; the gate stopped
with exit1 and 53 unnamed lines. Kept run:
/home/jerus/cbs/runs/cbs-gate-PW5-qJ4M44lT; its sealed ship.log, unnamed.txt and
checks are copied to C:/Users/Jerus/AppData/Local/Temp/cbs-gate-PW5-qJ4M44lT.
Launcher: target/PW5-dry.txt. This is failed evidence, never a passing gate-run
reference. Nothing in the kept run is changed. SubstationCheck, PowerPriceCheck
and SaveFileCheck passed in it, but that did not make the other new reds acceptable.

Mend the fixtures that must now provide physical wires for the generation or
price they isolate. SubstationCheck's shared fixture helper adds standing
districts from a literal 100,000 kW carry; its generation variant covers the
standing nameplate and the founding supply. It never forces a ratio, a tariff,
a dispatch result, or a money-audit answer. The existing carry-edge and tariff
oracles remain unchanged.

- BuildAdviceCheck reads GRID independently from the catalogue's capacity and
  the literal carry, the season and peak draw. Its new row previously fell through
  to NaN. The existing advice comparison is retained.
- EssentialWorksCheck gives its isolated orders wires for their forecast. Their
  real repairs initially exhausted the old town's narrow site output: add actual
  Construction Depots until the fixture retains its previous site output, and
  assert both premises. Its original cash, priority, forecast and exact-card
  assertions now all pass. No builder rule is bypassed.
- ElectrolyserCheck gives the generation comparison wires for the unfitted peak;
  the original exact served/asked assertions pass, with a new carry precondition.
- EquipmentCheck gives its steam-price fixture wires, then plays dispatch and
  settlement (two months): one month still read the starter grid's previous
  zero-cost bill. The positive-price and every fitting/break-even assertion stay.
- HistoricalLicenceCheck and LicenceFeesCheck provide wires before their normal
  setup months. That also repairs PublicLicenceCheck's new zero-gross turbine
  line. Their money and saved-dispatch comparisons stay exact.
- CopperRefiningCheck's forty-year price-chain town gets wires for its standing
  generation. With them its original annual production and positive-result
  assertions pass again: all 39 post-founding results are positive, least D$99.
- IndustryViewCheck's first wire-only recheck still failed its three lines.
  Funding and holding its operators changed none of them; remove that trial.
  Diagnostics show Coal Mining bids zero diesel, yet the pooled-capacity reading
  is 20,506.5168 litres plus 34.6410 fleet litres, all it actually buys. Its
  unprofitable mines correctly fail THE ORDER, make no product roof, and sell no
  local coking coal. The fixture must cause a working chain for these cases.
  ★ Use the literals, as the adjacent copper fixture already does, then raise
  thermal coal, coking coal, coke, pig iron, steel and lime's world quotes together by
  100 in this fixture, retaining their relative goods prices while
  covering fixed costs at the new tariff. Keep every feed, captive-flow, roof,
  money and save assertion. New failure diagnostics report the bid and the
  unroofed industry instead of merely the aggregate mismatch. Setting quotes
  on the world-market path alone has no effect: its cached quotes replace them.
  Lime is included because its kiln also burns the repriced thermal coal: leaving
  its sale price behind caused its own loss hold and stock write-off, outside
  the working-chain premise. No inventory or ribbon equality is relaxed.

The four moved-city pins below belong to queued HT1-H, assigned by the
orchestrator's mail 20261122-135841-230-orchestrator-a442 and main db9b1fd99.
Merge that main change (QUEUE.md only); the shipped format remains61, PW5's
format/floor62, and the model is unchanged. FS7 and FS9 have shipped and are
not used as queued fixes. No own QUEUE.md edit and no new pin bundle is made.

A detached current-main worktree at db9b1fd99 independently passes
IndustryViewCheck, RailCheck, RailDepotCheck, HomeRailCheck and OrderDoorCheck.
Logs: target/PW5-main-pin-results/. RailCheck's name also matched three other
rail harnesses; its call ran four and passed all four. Subsequent calls explicitly
set cbs.parallel=1; this builder starts no other workload alongside a harness. The
temporary main worktree was removed after its checks and log copies completed.

| Pin and month | Current main versus PW5's sealed first dry run | Why it is a premise, not a broken law |
| --- | --- | --- |
| RailCheck, m37 | 48,543 t / 218k lorry bill becomes 15,327 t / 78k. | The played steel town no longer fills the fixture's minimum railway threshold. |
| RailCheck, m67 | Main bills 252k against 244k allowed, target83.9%; PW5 bills86k against253k, target clipped at100%. | The supposed sized railway is overbuilt for the moved trade. Its boundary discount no longer covers the additional home leg. |
| RailCheck's three-way line, m67 | Main:291k=252k+0+39k kept; PW5:86k=86k+0+0 at printed precision. | Freight.java's pre-existing getPaidAbroad/getKept TODO says FR6's home bill is outside the cross-border truck bill; the comparison needs enough boundary discount to cover it. At the ceiling it clips kept to zero. All actual shipper-cost, railway revenue, fuel/import and input-sum comparisons still pass; the model's money flow was not changed here. |
| RailDepotCheck, m18 then m21 | Main offers122,791 t bulk, lays1 spur and deals50,000 t; PW5 offers61,825 t, so floor(bulk/50,000)-1 is0, then offers48,341 t with no sidings. | The played town no longer offers two spurs' bulk. It orders zero spurs; it does not lose access to a laid one. |
| HomeRailCheck tight town, m23 | Main boundary199,167/199,167 t, home37,811 t, share0; PW5 boundary130,442/199,167 t, home23,054 t, share1. | The boundary no longer fills the railway. All the home offer fits legitimately. |
| HomeRailCheck fractional town, m78 after its60-month guard | Main finds share0.9790:35,377 t room for36,137 t home; PW5 ends with53,687 t room for25,373 t home, share1. | The old search never finds the strict interior share, including the later landed-ore fixture gate. Randomized dealing bounds and the actual leg/bill identities still pass. |
| OrderDoorCheck seed12, m200 | Main planner683.3898, door689.4086, lender passes one and two; PW5 planner545.3506, door551.0154, both lender-pass flags false. | All other printed conditions hold: ground, listing/regime, no raise, quote support, no foreign assets, open credit, door above planner, bank capital, shopkeeper ready. It stops at this fixture gate before attempting an order. |

The four pin counterfactuals use identical harness sources and seed: only PW5's model
changes between these runs. The wires' physical constraint, new construction
and posted costs change the towns' trade, population, output and financial path;
the rail dealing, mortgage test and order-door implementations are unchanged.
This is why the old played figures move. HT1-H will cause those conditions
directly. The ship file names only the nine failed assertions, not broad harness
waivers. The fresh reviewer is to check this classification as well as the mends.

All mends above are in harnesses. No production money, save, building or dispatch
line changes after the final 80-run ensemble; its exact model and M1/F1 PASS
remain the final-model evidence. A new ALL dry run is still owed after review
SHIP and a new box-slot grant.

The final IndustryViewCheck is green: all nine feed equalities, 3,641 tonnes of
coke made with 614 captive and coking coal bought locally, and nine roofs on nine
pictures. All original inventory, mass, input, layout, read-purity and save
assertions remain. Its controlled prices change this fixture alone.

Final mends verification (one named call at a time, no laptop full pick):

| Check | Final result |
| --- | --- |
| EssentialWorksCheck | 0; every original assertion and the new wires/repair-output premises pass |
| BuildAdviceCheck | 3; only main's existing streets-card red (FS12), GRID is green |
| ElectrolyserCheck | 0; all 72 assertions |
| EquipmentCheck | 0; positive tariff and all fitting/continuation comparisons |
| HistoricalLicenceCheck | 0 |
| LicenceFeesCheck | 0; both new Mining-price lines green and every money comparison kept |
| PublicLicenceCheck | 3; only the four existing desalination lines (PW1-W) |
| IndustryViewCheck | 0 |
| SubstationCheck | 0; independent worked tariff, reform, carry, order, saved dispatch, real 60/61 refusals |
| CopperRefiningCheck | 3; only the existing FS8 played-cut line; its seed 1 child finishes 2400 months with zero findings (10.0 min on this laptop) |
| RailCheck selection, including HomeRailCheck | 3; only the six HT1-H rail/home assertions; other matched rail harnesses pass, run serially with cbs.parallel=1 |
| RailDepotCheck | 3; only the two HT1-H premises |
| OrderDoorCheck | 3; only the HT1-H edge premise |
| final --fast | 0; zero firm Stale findings, 196 soft |

Logs: target/PW5-dry-mends-EssentialWorksCheck.txt,
target/PW5-dry-mends-results/ and target/PW5-dry-mends-final/.
The last directory retains the final pin suite logs and final fast result.
The shared known-reds parser exits 0 with the nine specific ship rows; no broad
harness exit is waived. The first ALL gate's BuildingDataCheck, PowerPriceCheck (77)
and SaveFileCheck passes remain for the identical production model. A source
comparison to ensemble 069504753 finds changes only in *Check.java, plus the
documentation/ownership records. Main's latest pre-mends fetch is still db9b1fd99.
The provisional marker stays: send mends for the requested fresh review, then
ask for the next dry-run slot on its SHIP. No own check or gate process remains.
