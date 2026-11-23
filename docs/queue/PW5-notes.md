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
  The first mends put it on literals and multiplied six quotes by 100. REV-PW5-2
  correctly rejected that untraced premise change. That choice is withdrawn:
  the current fixture keeps the native world market and its unmodified quotes.
  PW5-F1 below traces the actual cause and the correction. Every feed,
  captive-flow, inventory, roof, money and save assertion is retained.

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

At the first mends tip 004aba039, IndustryViewCheck was green: all nine feed equalities, 3,641 tonnes of
coke made with 614 captive and coking coal bought locally, and nine roofs on nine
pictures. All original inventory, mass, input, layout, read-purity and save
assertions remained, but the review rejected its controlled-price premise.

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

### REV-PW5-2: PW5-F1, utilization cost and an overbuilt fixture

Mail 20261122-145535-083-orchestrator-13c1 requires a trace before changing the
fixture's prices. It also confirms the other wires mends and all nine HT1-H
pins. This section supersedes the rejected IndustryView price adjustment above.

★ Keep the study's whole 100 MW district and its capital/repairs/posts split.
Spec-electricity 2.7 says one Substation per 100 MW of peak, prices a district's
lines/transformers/meters at about 440M, and says 5.94¢ is its cost at a typical
load. Section 6 names the 100 MW step and its split as estimates; it specifies
no fractional ownership or utilization discount. A built district's whole
cost therefore remains in the tariff even before it fills. The free 10 MW
founding grid remains. This is a named model finding, **PW5-F1: underused
districts cost more per kWh**. It is now an asserted, reproducible counterfactual
in SubstationCheck, rather than a price cap or a hidden subsidy.

The rejected helper had sized the IndustryView town's wires to 260 MW of
standing generation, building **three districts**. That was the error: its
standing draw before savings is 40,551 kW; the highest seasonal peak is 61,108 kW,
so it needs **one** 100 MW district. Its struck book observed at m4 asks a
45,024 kW peak and delivers 25,663,917 kWh. Unused generation needs no extra
district. Default GRID advice already sizes from forecast peak demand; no game
model, building row, bill formula or saved field changes in this mend.

The trace uses native world quotes throughout. Values below strip denomination
and the struck local price-level shift using PowerPriceCheck.unitOf; dollar
figures are equivalent baseline-price dollars. Mine margins are current local
or export prices less the model's marginal cost per tonne, not a new mine's
capital-return estimate or a not-yet-settled startup income statement.

| Same works stock at m4 | Districts | People | Wires ¢/kWh | Thermal coal home margin/t | Coking coal home margin/t |
| --- | --- | --- | --- | --- | --- |
| Native market, corrected wires | 1 | 840 | 11.382486 | +8.839134 | +35.592793 |
| Native market, rejected generation-sized wires | 3 | 978 | 33.989438 | -20.700447 | -83.221346 |
| Same three districts, PW1 flat-wire counterfactual | 3 | 978 | 5.938000 | +13.884554 | +55.819629 |
| Main counterfactual, no district and flat wires | 0 | 776 | 5.938000 | positive | positive |

All four serve the same 45,024 kW peak in full, with the same 2.866213¢/kWh
energy price. The one-district book captures monthly capital 2,284,748.58,
repairs 452,215.12 and actual filled-post payroll 184,228.07: total 2,921,191.77
over its delivered kWh is 11.382486¢. The catalogue's base 440.004M/140-post
district is unchanged; these closing costs read the existing current replacement
quote, builders' tax and filled wages, as the independently worked tariff check
already proves. Three districts triple its capital and repairs at the same
load, while payroll follows the different fills. The model's home prices versus
marginal costs show the cause directly:

- One district: thermal 34.812312 versus 25.973178/t; coking 140.179727 versus 104.586934/t.
- Three: thermal 34.877053 versus 55.577500/t; coking 140.215098 versus 223.436443/t.
- The same three with flat wires: thermal 34.877053 versus 20.992499/t;
  coking 140.215098 versus 84.395469/t.

The overbuilt town bids zero process diesel and sells no coking coal locally.
Flat wires restore that same-stock chain. One correctly sized district also
restores it at the native market quotes: 21,857.041 process litres bid,
21,893.963 litres received including its fleet, 2,965.571 t of local coking coal,
3,842.089 t coke made and 648.352 t captive. Its export margins remain negative
(-1.072196/t thermal and -5.780804/t coking); the trace does not pretend all exports
or a new investment pay. The home chain the view fixture needs works without
changing a single commodity quote. **The least price factor is 1: no factor at all.**

works()'s javadoc now says why this town needs one district. The shared view
preparation sizes wires from the buildings' powerBought draw at the highest
season, conservatively before steam/electrolyser savings. It reads stock and
adds real districts; it does not dispatch a fictitious preceding month or force
a ratio. The copper town retains its original, separately explained literals
choice. Every original IndustryView assertion stays and passes.

**RailCheck m37 is chiefly a different squeeze.** It owns zero substations, so
its wires charge is zero. Its peak exceeds the free founding carry; the current
struck energy price reaches 315.268782¢ and only 29.545060% of its energy is served.
It cannot be losing trade because it was charged for a whole owned district.
The unchanged steel-town setup is extracted into a shared harness method so
these counterfactuals run the exact same 36-month construction/hold sequence:

| m37 steel town | Districts | Served ratio | Wires ¢/kWh | Energy ¢/kWh | Trade tonnes | Lorry bill, model thousands |
| --- | --- | --- | --- | --- | --- | --- |
| PW5 as the pin found it | 0 | .295450596 | 0 | 315.268782 | 15,327.013293 | 77.698418 |
| Main's flat wires and unlimited carry | 0 | 1 | 5.938000 | 3.753717 | 48,543.036587 | 217.796984 |
| One real district for its demand | 1 | 1 | 14.361365 | 3.751932 | 45,369.229275 | 203.127264 |

Thus the 68% trade fall comes with missing carry and the shortage-cap price;
one real district restores all supply and most of the trade. The residual
versus the main counterfactual includes the real wires charge and its posts;
it is not attributed to price alone. The HT1-H premise record remains valid.
The trace uses the existing grid-cost-source seam's PW1 fallback to isolate
the flat-wire counterfactual, without changing production code.

The new PW5-F1 lines assert native quotes/minimum districts/working chain,
whole capital at an unchanged load, loss under three unnecessary districts
and recovery at flat wires, and RailCheck's distinct carry/VOLL cause.
Reinstating 004aba039's generation-sized wires plus literals and 100× quotes
makes all three new works-finding lines fail (named runner exit 4), while the
existing core checks pass. The original good source was restored by SHA-256.
Mutation log: target/PW5-review3/old-fixture-mutation.log. Initial diagnostic
records: target/PW5-wires-finding-overbuilt.log and PW5-wires-finding-peak.log.
These trace-only diagnostics are not claimed as normal verification runs.

Minor mends: EquipmentCheck's label now names dispatch and settlement instead
of one month; BuildAdviceCheck and the fixture carry helper use
Substations.SUBSTATION_MW (through CARRY in the helper), not a literal 100,000.
Normal verification and its final commit are recorded next. Production sources
remain byte-identical to ensemble 069504753, so the existing 80-run M1/F1 PASS
and 24/26 score remain the final-model evidence; no replacement ensemble is owed.

The final normal runs pass SubstationCheck (including all four PW5-F1 lines,
the independent tariff and both real old-save refusals), IndustryViewCheck
at native quotes, and EquipmentCheck, all exit 0. BuildAdviceCheck exits 3
only for its existing FS12 line. RailCheck's selection runs serially with
cbs.parallel=1 and exits 3 only for the existing HT1-H rail/home premises;
its other selected rail harnesses pass. Final --fast exits 0 with zero firm
Stale findings (196 soft). Logs: target/PW5-review3/; final-status.txt records
the normal checks, and the final SubstationCheck.txt/log are refreshed after
the last trace-format edit. No trace-only property is left in the source.

The extracted RailCheck setup is checked textually against004aba039: removing
only its optional pre-month callback gives the exact original quiet block,
including all orders, cash, holds and36 monthly advances. No other RailCheck
assertion or threshold changes. The nine HT1-H patterns remain unchanged; only
the first cause now states the measured carry/VOLL mechanism. The known-reds
parser passes and the pick remains ALL243 with its saves review line. The latest
main is stilldb9b1fd99; format/floor62 and the native60/61 pairs remain unchanged.
Keep the provisional marker and send these mends to the same reviewer. No own
JVM or gate process remains; the next ALL dry-run slot is requested only on SHIP.

### REV-PW5-2 delta: stand the rail fixtures' real wires

Mail 20261122-155812-905-orchestrator-680f correctly identifies an error in the
preceding ownership decision. The rail towns are hand-built with automation
off. Omitting their substations is a fixture defect exposed by PW5's physical
carry limit, not a played-city premise to pass to HT1-H. The earlier assertion
that all nine pins belonged there is withdrawn. No rail assertion, threshold,
money identity or save bound is changed in this mend.

RailCheck.prepareTradeTown and HaulageCheck.base now call peakWires after their
buildings are standing and before their first monthly advance. The helper
reads the buildings' powerBought draw at the highest season and stands real
districts. It does not force energy ratios, tariffs or freight. Each of these
towns needs one district. The omitted-wire and flat-wire counterfactuals in
SubstationCheck now explicitly retire that stock before their months; their
physical-shortage finding and every PW5-F1 assertion still pass.

The fixture trace now prints current stock, closing and billed peaks, both
served ratios, and the struck energy and wires prices. These are readings of
actual dispatch books, not prices or loads assigned by a harness. This gives
the following results after real wires, in baseline-price cents/kWh:

| Fixture | Month | Districts / carry kW | Closing / billed peak kW | Closing / billed served | Wires / energy cents |
| --- | --- | --- | --- | --- | --- |
| RailCheck steel town | 37 | 1 / 100,000 | 43,006 / 40,448 | 1 / 1 | 14.361365 / 3.751932 |
| RailDepotCheck bulk town | 18 | 1 / 100,000 | 29,346 / 24,787 | 1 / 1 | 23.213349 / 3.801464 |
| HomeRailCheck full-boundary town | 23 | 1 / 100,000 | 17,229 / 17,298 | 1 / 1 | 33.598504 / 3.778053 |
| HomeRailCheck fractional-share search | 78 | 1 / 100,000 | 30,880 / 25,732 | 1 / 1 | 21.850946 / 3.478324 |
| OrderDoorCheck seed 12 | 200 | 0 / 10,000 | 1,901 / 2,027 | 1 / 1 | 0 / 0 |

Six of the eight rail pins now pass: all three RailCheck pins, both
RailDepotCheck pins and HomeRailCheck's full-boundary case. RailCheck m37
carries 45,369 tonnes, beyond the unchanged 30,000-tonne premise, with a
203,127 model-dollar lorry bill. Its later m67 billing and sizing premises
also pass. RailDepotCheck offers 120,988 bulk tonnes before laying one spur;
after three months it offers 129,025 bulk tonnes and deals exactly the spur's
50,000-tonne sidings. No missing wires exemption remains for either harness.

Only the following three lines remain with HT1-H:

- HomeRailCheck.java:480, `...and one the boundary leaves room in`: at m78,
  boundary traffic is 205,767 of 248,958 tonnes, leaving 43,191 tonnes of room.
  All 32,614 home tonnes fit, so the home share is 1, not strictly between 0
  and 1. The unchanged sixty-month search does not reach its intended premise.
- HomeRailCheck.java:497, `fixture: the railway carries a share of the city's
  own bulk, strictly between nothing and all`: the same wired m78 city has
  share 1. The physical bounds, actual legs, bills and save identities pass.
- OrderDoorCheck.java:273, `fixture: there the landlord stands at HN1-A's
  edge`: at seed 12 m200 both lender-pass conditions are false. All other
  edge components hold; no order is attempted or lost. The city has all the
  wires it needs at this month: its roughly 2 MW peaks are below the existing
  free 10 MW founding network, and both dispatch books serve it fully.

OrderDoorCheck does not own a district because none is required. Substations
costs() retains max(founding carry, districts times 100 MW), and GRID only
orders above the founding carry. Adding a 440M district to this 2 MW town
would change the lending premise unnecessarily. A new strict assertion checks
both its closing and billed carry against their actual peaks and passes;
the printed figures above make the absence of a physical shortage explicit.

The ship file removes all eight old rail exemptions and keeps only the two
remaining HomeRail patterns plus OrderDoor. Its changelog now includes PW5-F1:
a 45 MW town pays 11.38 cents/kWh for its one whole district against the
5.94-cent typical-load benchmark. The changelog remains within twelve lines.

Mail 20261122-161104-087-orchestrator-6e3a resolves the queue-record mechanism:
the orchestrator will narrow HT1-H on main to the named remaining lines and
figures in the mends mail. The ship parser has no existing-row replacement
section. No QUEUE.md edit, duplicate queue row, HTML instruction or publisher
change is made here.

Verification for this delta is in target/PW5-review4/. RailCheck's serial
selection first ran with no rail exemptions: its three former reds, QuayRail
and IslandRail pass; only the two HomeRail premises fail. RailDepotCheck and
HaulageCheck each exit 0. After recording exactly the remaining patterns,
HomeRailCheck and OrderDoorCheck each exit 3, with no unnamed reds;
SubstationCheck exits 0, including the native old-save refusals, tariff,
currency reform and all PW5-F1 counterfactuals. The strict OrderDoor wires
coverage line passes. Original bounds and money/save comparisons are retained.
The first-run files use Name.txt/log; the final refreshed files use
Name-final.txt/log, with their exit codes in final-status.txt.

Production sources are still identical to ensemble commit 069504753 (only
*Check.java changes under src/main/java). No saved field or floor changes in
this delta; the existing final-model ensemble, M1/F1 10/10 and 24/26, remains
applicable. The pick is still ALL 243 and requests saves review for DataSave
and the raised format/floor, already reviewed with the native 60/61 pairs.
The known-reds parser exits 0 with exactly three PW5 rows. The provisional
marker remains; this delta goes back to the same reviewer before requesting
the next dry-run slot.

Final --fast exits 0: maps regenerated, zero firm Stale findings and 196 soft.
git diff --check passes; edited files remain LF. The changelog is eleven lines
including its heading. The check loop and its waiter have ended; no own JVM
or gate process remains. Keep the prior stopped dry-run directory as evidence,
not as a passing reusable run. A fresh ALL dry run awaits the delta SHIP and
the orchestrator's newly granted slot.

### REV-PW5-2 delta: cause HomeRail's partial share and check the borrowers

Mail 20261122-163348-763-orchestrator-88ff correctly rejects the two remaining
HomeRail exemptions. Adequate physical carry does not isolate a tariff change:
this hand-built town pays 21.85 cents/kWh for its real district, versus main's
5.94-cent flat wires. It offers 32,614 home tonnes rather than main's 36,137,
into 43,191 tonnes of room rather than 35,377. Its share of 1 is a fixture
condition PW5 must re-cause. Those two exemptions are withdrawn; only the
played OrderDoor premise remains in the ship file.

★ Cause the strict interior through real stock and real months. HomeRail's
fractional-share fixture now has two copies of the base town's buildings and
iron sites/reserves, with real substations sized again from their peak draw.
This makes the home offer wider than one spur's indivisible capacity step.
After the town plays, its actual boundary offer sizes the count of real spurs
beside its one depot: the first whole count putting their room above that
offer. At sizing only, the excess is at most one spur's step, short of the
larger home offer. That bound does not survive subsequent traffic and fleet
drift. The existing sixty-month guard still searches for the actual model's
strictly partial share, including its normal fleet purchases and trade drift;
this restores parity in main's own guarded fixture, not a guaranteed interior
throughout the following months.
A new strict fixture line requires the observed home offer to exceed that
step before laying the spurs. No share, trade row, capacity reading or market
price is assigned to manufacture the answer. The existing literals/pellet
quote preparation and its original documented cause are unchanged.

The depot/three-spur town still causes the full-boundary zero-share side.
The new town causes the other side from its played offer, then every original
leg, actual bill, diesel and save identity runs against it. Both original
0 < share < 1 conditions and their comparisons are retained; the descriptive
label now says spurs sized from the offer instead of claiming exactly four.
The larger stock is local to this one fixture. The shared HaulageCheck.base
and every other caller keep the preceding real-wire preparation unchanged.

OrderDoor remains HT1-H by the review's decision: seed 12, builder on, and no
order attempted or lost. Its energy charge is zero for a concrete dispatch
reason as well as its wires being free: UtilitiesHandler adds the founding
generator as a zero-marginal-cost row (UtilitiesHandler.java:901). This town's
roughly 2 MW peak is below that row's capacity throughout the billed month.
No fuelled plant is needed; every half-block clears at zero, so the weighted
energy price is zero (PowerDispatch.java:250-278). Zero owned districts also
means no capital, repairs or district posts in the wires part. Main still
charged its flat 5.94-cent wires even here. OrderDoor now prints the actual
struck book's generator kWh, fuel and highest block price beside the carry
reading, making both zero bill parts traceable. The lender-edge conditions
remain assertions, not values changed to reach the order.

HomeRailCheck now exits 0, including the full 1,250-month real-run section.
Before its new spurs, the doubled town offers 76,785 home tonnes and 427,623
boundary tonnes; the 50,000-tonne step selects eight spurs beside the depot.
Normal months reach the strict interior after 15 of the permitted 60 months,
at m33: the boundary deals 385,113 of 448,125 tonnes, leaving 63,012 tonnes
for 63,868 home tonnes. The observed share is 0.9866, strictly between zero
and one, and the tonnes taken equal the room to the unchanged tolerance.
The original each-leg partial-share premise and every bill/diesel/save line
pass. One district carries its closing 41.783 MW and billed 45.430 MW peaks
fully, with actual wires 12.601571 cents/kWh and energy 3.762613 cents/kWh.
The zero-share town remains fully wired and its original bounds also pass.
Log: target/PW5-review5/HomeRailCheck.txt and HomeRailCheck.log.

All five named borrowers of HaulageCheck.base were rerun, one named script
call at a time with cbs.parallel=1:

| Harness | Result |
| --- | --- |
| EquipmentCheck | 0; every fitting, dispatch and settlement comparison passes |
| Forms1900Check | 3; only its two existing FS8 lamp-oil/door-count premises |
| LandlordPortCheck | 0; all port, tariff and financing comparisons pass |
| TradeBandCheck | 0; all band and reload comparisons pass |
| TruckRouteCheck | 0; all route, leg, bill and save comparisons pass |

Forms1900's recorded FS8 lines are the month having petrol duty (actual zero,
378.2 homes and 7,450.7 litres) and the billed versus closing door counts
(404.154565 versus 378.213948). These are the existing named assertions in
docs/notes/known-reds.txt, not new exemptions; no assertion is weakened.
Logs and return codes are target/PW5-review5/Name.txt/log and status.txt.

The refreshed OrderDoor run has only its accepted edge premise red, now at
OrderDoorCheck.java:277. Its billed book serves 1,216,106.526 kWh, all from
the free founding grid: fuelled, wind, hydro and works output are each zero,
the fuel map is empty and the highest block price is exactly zero. Both
bill components are therefore zero for the traced reason above. Its strict
closing/billed carry comparison passes, and both lender-pass predicates
remain false. Only this OrderDoor row remains in PW5's ship known-reds; the
orchestrator can narrow HT1-H on main to it after the delta review.

Final --fast exits 0, maps regenerated, zero firm Stale findings and 196 soft.
git diff --check passes and edited files are LF. The known-reds parser exits
0 with the single remaining OrderDoor row; the changelog stays eleven lines.
Pick remains ALL 243, review needed for DataSave and SAVE_FORMAT/floor.
Only harness sources changed since the final-model ensemble 069504753;
production code, format/floor 62, native save fixtures and the ensemble's
M1/F1 10/10 and 24/26 result remain unchanged. The serial check loop ended
with no own JVM or gate process left. Send this delta for the same reviewer's
verdict with the provisional marker, then request the dry-run slot on SHIP.

### Final delta SHIP and second ALL dry run

Mail 20261122-165801-090-orchestrator-9e2f grants SHIP on 9fd267c7d and the
dry-run slot, with the sizing qualification corrected above. The m17 played
offer used for sizing leaves 20,502 tonnes of room against 76,785 home tonnes.
At the actually asserted m33, room is instead 63,012 tonnes, exceeding the
50,000-tonne step: boundary traffic has drifted from 427,623 offered to 385,113
dealt. Its strict interior holds by only 856 tonnes (1.3%). The unchanged
guarded search finds this month; parity is restored, and HT1-H retains the
latent search fixture to cause more robustly. The trace is not a proof that
the initial sizing preserves the room bound as the town plays.

Merged origin/main 7b60b9a8d in fc9e72c48. Since the previous reviewed main
db9b1fd99, only the orchestrator's QUEUE.md ownership record changed; no
production source moved. Main still ships save format/floor 61, so PW5's
62 remains the next format, with the one shared native-60 fixture and the
native-61 fixture unchanged. The production-source comparison to final-model
ensemble 069504753 still finds only *Check.java differences. Its completed
80 runs, M1/F1 10/10 and 24/26 against 0.9.188, remain applicable; no model
change calls for another ensemble. The slot is explicitly granted for the
ALL dry run by the same SHIP mail.

### Second ALL result: repair capacity in three construction fixtures

The gate-box run /home/jerus/cbs/runs/cbs-gate-PW5-tm3677MG stops with exit 1.
Its ALL 243 suite finishes in 908 seconds with 32 unnamed log lines, comprising
26 actual failed assertions: PostwarRecipeCheck 3, NewUnitCheck 21 and
FitoutCheck 2 (the other six lines are their exits and counts). It is of
branch 2b0246ac8effcd1e930505d8388d8c175488376c and main
7b60b9a8d3b0aa3cb2eea1421c99654bc54db8bc. The sealed copy is
C:/Users/Jerus/AppData/Local/Temp/cbs-gate-PW5-tm3677MG; launcher output is
target/PW5-dry-final.txt. No file in either kept run is changed, and neither
stopped run is entered as a passing gate-run. The provisional marker stays.
All the previous HomeRail mends pass; OrderDoor remains its accepted named red.

These are PW5's fixture defects. HistoricalLicenceCheck.town and stand supply
real wires, and all three tests borrow that town. One district adds 110 real
repair points per month. Their old numbers of builders no longer guarantee
site work after those repairs. NewUnit's first premise reads 355 builder
points against 377.904670 of repairs, leaving no site output and hence NaN
completion waits; its remaining schedule/cache assertions follow from that
missing premise. Fitout's wait becomes NaN and its queue multiple cannot move.
Postwar's refit remains unfinished at its original 120-month guard, so its
three original fitting/royalty checks fail. No money or save bound is relaxed.

★ Supply real depots for the lost site budget, in these three isolated
fixtures only. SubstationCheck.buildersForSite reads the standing districts'
construction points and their own repairsPerYear rate. The target is the
current potential builder output less all repairs plus the wires' repair
points, with the caller's positive minimum too: the site budget it would
have before paying the wires' repairs. It adds whole depots one at a time,
supplies the fixture's existing handWorkers input and rereads actual output
after every addition. Its 64-depot bound throws if that target is not met.
Every repair remains paid and consumes points; no ratio, bill, repair or
completion reading is assigned. Shared HistoricalLicenceCheck preparation,
production code and the default model remain unchanged in this mend.

The first diagnostic minimum of one positive point removes most of NewUnit's
cascade and fixes Fitout, but is insufficient for Postwar's played refit.
At its start the potential site budget is 117, yet after twenty months
builder output is 337 against 377.727326 of repairs. The refit then stalls
near 4,499 points left for the rest of the guard. A trace every twenty months
now makes that loss of capacity explicit. Restoring the pre-wire budget
requires two extra real depots: 641 potential builder points, 324.256667 of
repairs, and 317 site points against a target of 227 at the initial month.
The site continues through its later hiring/health path and finishes within
the unchanged 120-month guard. The exact wet-form coal figure and both real
royalty comparisons pass. All original 230 assertions remain, plus one
strict construction-premise assertion.

NewUnit similarly adds two real depots after its original four-month warmup:
516 potential builder points, 378.571337 repairs and 137 site points against
the pre-wire target of 87. Its independent priority test also turns builders
first off before moving a depot behind the kilns, then restores it before
the separate builders-first stamp test. This isolates city priority from
the independent depot-first rule; its actual power vectors must still move,
and every held/bare/thread/second-city identity stays bitwise. All 83
original assertions pass, including the previously hidden priority premise.

Fitout's farm fixtures add two depots to the previous two: 441 potential
builder points against 285.125000 repairs leave 156 site points, beyond the
pre-wire target of 66. All original 66 assertions, including actual receipts,
VAT, saved lots and work recognition, pass, plus seven strict positive-site
premises on the separate farm fixtures. The finite waits and moving multiple
are caused again through stock; their expected figures and bounds do not move.

The initial post-mend normal runs of all three exit 0. The final serial
fixture-caller sweep, including the helper's added failure bound, follows.
Diagnostics and those first runs are in target/PW5-dry2-mends/; the final
complete coverage logs are in target/PW5-fixture-coverage/.

### The changed fixtures' callers

Mail 20261122-172150-041-orchestrator-ae4f requires every direct and transitive
fixture caller before another dry-run request. The source audit follows
qualified calls and local wrappers, checks static imports and method
references with git grep, and checks the one constructor call outside the
method-body extraction: ModernEngineeringCheck.Fx initializes its city with
HistoricalLicenceCheck.town at line 91. Its SaveFile/ReadPath users are
included. The method-reference search finds only the already covered
HomeRailCheck::doubleTown; there are no relevant bare static imports.
BuildAdviceCheck's own changed GRID fixture is included explicitly.

| Fixture entry | Direct and transitive harnesses |
| --- | --- |
| Wire-stock helpers | SubstationCheck, CopperRefiningCheck, ElectrolyserCheck, IndustryViewCheck, LicenceFeesCheck |
| HistoricalLicenceCheck.town / stand | HistoricalLicenceCheck, FitoutCheck, ModernEngineeringCheck, NewUnitCheck, PostwarRecipeCheck, PowerPriceCheck, PublicLicenceCheck, ReadPathCheck, SaveFileCheck |
| HaulageCheck.base / played | HaulageCheck, EquipmentCheck, Forms1900Check, HomeRailCheck, LandlordPortCheck, RailDepotCheck, TradeBandCheck, TruckRouteCheck, FittingCheck, FreightViewCheck, PolicyPreviewCheck |
| RailCheck.prepareTradeTown | RailCheck and SubstationCheck (already listed) |
| EssentialWorksCheck.serveGrid / serveOtherEssentials | EssentialWorksCheck, WorksFinanceCheck |
| Own GRID advice fixture | BuildAdviceCheck |

That is 29 distinct harnesses. The less direct paths include
FittingCheck.sectionNine -> EquipmentCheck.cartTown -> HaulageCheck.base;
FreightViewCheck.full -> HomeRailCheck.railed -> HaulageCheck.base;
PolicyPreviewCheck.portCity -> LandlordPortCheck.seaHanded ->
HaulageCheck.played -> base; ReadPathCheck.main ->
HistoricalLicenceCheck.readChecks -> closedSteamInvoice -> stand; and
SaveFileCheck.main -> PowerPriceCheck.saveChecks -> HistoricalLicenceCheck.stand.
WorksFinanceCheck's pavingHold uses serveOtherEssentials, whose remit now
includes GRID even though that helper's body did not change, so it is covered
too. The detailed source-call paths and audit script are retained in
target/PW5-dry2-mends/; the canonical selected names are
target/PW5-fixture-coverage/callers.txt.

The final sweep runs one name per batch-check call, cbs.parallel=1. The
RailCheck substring also selects HomeRailCheck, QuayRailCheck and
IslandRailCheck, serially; HomeRail is therefore covered there rather than
run twice. Its siblings' individual logs are retained as well. Results are
recorded below when the complete sweep ends.

The complete serial sweep finishes successfully on source c34ab27e6. All 29
fixture callers are covered, with no new reds. The four exit-3 results have
only their existing named assertions; no new exemption is added.

| Harness | Final result |
| --- | --- |
| BuildAdviceCheck | 3; existing FS12 streets-card line only |
| CopperRefiningCheck | 3; existing FS8 played cut only; its seed-1 child finishes 2400 months, exit 0, in 9.7 minutes |
| ElectrolyserCheck | 0 |
| EquipmentCheck | 0 |
| EssentialWorksCheck | 0 |
| FitoutCheck | 0; all 66 original assertions plus seven strict fixture premises |
| FittingCheck | 0 |
| Forms1900Check | 3; the two existing FS8 lamp-oil/door-count lines only |
| FreightViewCheck | 0 |
| HaulageCheck | 0 |
| HistoricalLicenceCheck | 0 |
| HomeRailCheck | 0, in the serial RailCheck selection |
| IndustryViewCheck | 0 |
| LandlordPortCheck | 0 |
| LicenceFeesCheck | 0 |
| ModernEngineeringCheck | 0 |
| NewUnitCheck | 0; all 83 original assertions |
| PolicyPreviewCheck | 0 |
| PostwarRecipeCheck | 0; all 230 original assertions plus the strict site premise |
| PowerPriceCheck | 0 |
| PublicLicenceCheck | 3; the four existing PW1-W desalination lines only |
| RailCheck | 0; all four matched rail harnesses pass |
| RailDepotCheck | 0 |
| ReadPathCheck | 0 |
| SaveFileCheck | 0 |
| SubstationCheck | 0 |
| TradeBandCheck | 0 |
| TruckRouteCheck | 0 |
| WorksFinanceCheck | 0 |

QuayRailCheck and IslandRailCheck also pass in that RailCheck selection.
The final --fast exits 0, maps regenerated, zero firm Stale findings and
196 soft. Every selected suite and individual log is retained under
target/PW5-fixture-coverage/, with return codes in status.txt. The complete
sweep's shell has exited; no own JVM or gate process remains.

The final fetch finds origin/main 591fc92d8. Since the dry run's main
7b60b9a8d, it adds only QUEUE.md and docs/queue/FC2.md; no production,
harness, build or save source changes. The documentation update is merged
before sending these mends. Format/floor 62 remains next after main's 61,
the canonical native-60 and native-61 pairs remain valid, and the 069504753
final-model ensemble remains applicable. The failed run is not reused; send
these mends with the provisional marker for review, then request another
dry-run slot on its SHIP.

Merged that documentation-only main in ce7ccf7dc. A fresh --fast on the merged
tree exits 0: maps regenerated, zero firm Stale findings, 196 soft, clean
tree. Log: target/PW5-fixture-coverage/fast-merged-main.txt. The known-reds
parser exits 0 with only the accepted OrderDoor row. The pick is ALL 243
(96 files), review needed for DataSave, SAVE_FORMAT and its floor. PowerPriceCheck
passes all 77 checks in the final caller sweep, SaveFileCheck passes, and the
kept ALL run's BuildingDataCheck remains green for the identical building
data and production code. git diff --check and LF checks pass. No own job
remains; the marker is still provisional, awaiting this delta's review and
a newly granted dry-run slot.

### REV-PW5-2: remove the shared town's unnecessary district

Mail 20261122-183124-896-orchestrator-09f1 correctly identifies the deeper
cause. The previous depot mends treated the upkeep of an unnecessary district
as a fixture requirement. That decision is withdrawn. HistoricalLicenceCheck
was sizing every town and newly standing building from generation nameplate,
including a district when demand fit the free founding network. This repeats
the IndustryView fixture's earlier error. A construction fixture needs its
real peak covered; a positive wires price belongs only to the licence case
that explicitly needs one.

The shared town and stand now use peakWires. The measured ordinary town's
conservative gross peak at the highest season is 3,050.061474 kW, below the
10,000 kW founding carry. At m4 its actual closing peak is 2,107.553684 kW,
its billed peak 2,105.159 kW, both fully served. GRID's current supply/demand
is 10,000 / 2,107.553684 kW: no district is needed. It owns none, and both
energy and wires prices are zero. SubstationCheck asserts this through the
actual stock, dispatch book and advice readings, not a zero assigned by a
fixture.

★ Isolate the priced premise. HistoricalLicenceCheck.pricedTown explicitly
stands one district before the existing three-month warmup. Only PE6/PE16
in publicPieces and PublicLicenceCheck.noGain use it. The latter retains
its strict positive gross turbine value while its surplus stack and new
plant royalties must remain zero. PE5 and ordinary construction/recipe/saves
borrowers use the peak-sized town. No price, energy ratio or repair is set.
The paired priced town has the identical measured load and one district;
its actual wires price is 231.269707 cents/kWh, energy zero. Its whole-district
maintenance increment is exactly 110 points, asserted independently against
the study's worked figure in SubstationCheck.

The buildersForSite helper and its added depots are removed. PostwarRecipeCheck
and FitoutCheck return to their original four- and two-depot preparations,
their original 120-month guard and all original 230 and 66 assertions. The
extra fixture assertions and diagnostic refit loop from the rejected mend
are removed with that premise. NewUnitCheck retains its original four depots,
all 83 assertions, and its original priority setup; the extra builders-first
override is removed. Its original separate builders-first cache-stamp test
is retained. All three normal named runs pass with no extra depots or switch.
HistoricalLicenceCheck passes; PublicLicenceCheck has only its four existing
PW1-W lines.

**PW5-F1 also consumes builder points.** The catalogue's whole district has
132,000 construction points at 1% repairs a year: 110 points every month,
even at a sliver of its carry. This is the same utilization lump as the
11.38-cent wires charge of the 45 MW works town. The earlier 87-point
"pre-wire budget" was arithmetic in the *wired* NewUnit snapshot:
355 - (377.904670 - 110), rounded. It was not a replayed no-district town.
The honest peak-sized fixture now measures 384 potential builder points,
263.795795 total repairs and **120** spare site points, with zero districts.
Removing the district also changes jobs, bills and the subsequent town;
the measured state must not be replaced with the old arithmetic figure.
NewUnit prints that construction reading beside its unchanged positive-site
assertion. A default-seed city's first district and its real builder budget
are measured separately below.

The additional SubstationCheck measurement initially found an uninitialized
FittingCheck output stream when called outside the licence harness. Wiring
its existing output streams fixes that harness setup; the normal rerun passes
both new historical-town lines and every existing substation/save assertion.
Logs: target/PW5-peak-mends/. Earlier preliminary exits are retained there,
not counted as final passes.

LongPlaytest has an opt-in harness-only census under
-Dcbs.pw5.firstDistrict=true. It observes the first completed district on
its ordinary monthly loop, printing the month, district count, actual and
potential builder output, charged and standing repairs, and spare site
points. It changes no simulation input or saved field and prints nothing
when the option is unset; the default trace remains unchanged beyond runtime.
The separate named run uses playtest.seed=0, its default, with the normal
world-path seed. This observation is not another ensemble or a model change.

The corrected named LongPlaytest run exits 0 through its normal 2400 months.
Its first standing district appears at **closing m576**, after the press
from m575, count one. This is the default city seed 0 and default world-path
seed 48,213. Both actual and potential builder output read **6,176 points**.
Standing repairs total 420.054779 points, including this district's whole
110 points; 5,756 site points remain. The already charged month's repair
tally is 303.468529: the district has just completed, so the standing-stock
reading describes its new continuing load, not a second charge in that press.
The complete line is retained in
target/PW5-peak-mends/LongPlaytest-default.log under FIRST_DISTRICT.

The changelog now pairs PW5-F1's cents figure with its 110-point repair lump
and the default city's m576 builder budget, within twelve lines including
the heading. The 87-point illustration remains explicitly qualified above;
the measured no-district construction fixture has 120 points available.
The ordinary town measurement and the original construction runs pass; the
same 29-caller serial sweep now runs in target/PW5-peak-coverage/, preserving
the previous sweep's logs separately. The census launcher was moved from
an inline shell argument to a script after that argument failed to start
the second command; only the completed, exit-0 named run is used as evidence.

### Final peak-sized caller rerun

The complete rerun finishes on source f36fa1af0 with no new reds. The caller
set is the same audited 29: pricedTown adds no new caller class. The separate
default-seed census above also exits 0. Every call is serial with
cbs.parallel=1, one name per batch-check invocation.

| Harness | Result |
| --- | --- |
| BuildAdviceCheck | 3; existing FS12 streets-card line only |
| CopperRefiningCheck | 3; existing FS8 played cut only |
| ElectrolyserCheck | 0 |
| EquipmentCheck | 0 |
| EssentialWorksCheck | 0 |
| FitoutCheck | 0; original 66 assertions, original two depots |
| FittingCheck | 0 |
| Forms1900Check | 3; the two existing FS8 lines only |
| FreightViewCheck | 0 |
| HaulageCheck | 0 |
| HistoricalLicenceCheck | 0 |
| HomeRailCheck | 0, covered by the serial RailCheck selection |
| IndustryViewCheck | 0 |
| LandlordPortCheck | 0 |
| LicenceFeesCheck | 0 |
| ModernEngineeringCheck | 0 |
| NewUnitCheck | 0; original 83 assertions and four depots, no added priority override |
| PolicyPreviewCheck | 0 |
| PostwarRecipeCheck | 0; original 230 assertions, four depots and 120-month guard |
| PowerPriceCheck | 0 |
| PublicLicenceCheck | 3; the four existing PW1-W lines only |
| RailCheck | 0; all four selected rail harnesses |
| RailDepotCheck | 0 |
| ReadPathCheck | 0 |
| SaveFileCheck | 0 |
| SubstationCheck | 0; both new historical-town assertions and every original line |
| TradeBandCheck | 0 |
| TruckRouteCheck | 0 |
| WorksFinanceCheck | 0 |

QuayRailCheck and IslandRailCheck pass too in that selection. All selected
suite logs, individual logs and return codes are retained separately in
target/PW5-peak-coverage/. Final --fast exits 0, maps regenerated, zero firm
Stale findings and 196 soft. The validation shell has exited; no own JVM or
gate process remains. The final fetch still finds main 591fc92d8, so no
merge, format bump or replacement native-save fixture is required in this
pass: main remains at 61, PW5 at 62. No simulator, tariff, saved field or
building data changed; the optional LongPlaytest observation is disabled on
normal runs. The final-model ensemble 069504753 remains applicable, M1/F1
10/10 and 24/26. The only ship exemption remains OrderDoorCheck:277.

Keep the provisional marker and send this one-pass delta and the complete
caller list to the same reviewer. HC1 holds the dry-run slot; a fresh slot
is requested after this delta's SHIP. Neither stopped ALL run is reused.

### Final delta SHIP and the granted ALL run

REV-PW5-2 gives d091e79e5 SHIP. Its earlier comparison of 110 repair points
against 87 is corrected to **110 against the measured 120** spare points
in the ordinary, peak-sized NewUnit town; the arithmetic distinction above
remains the reason. The changelog now says explicitly that 110 of the
default city's 420 standing repair points belongs to its first district.

The orchestrator grants the ALL dry-run slot on 2026-11-22. HC1's gate may
run beside it. The final-model ensemble at 069504753 was run once before
HC1's merge; it does not measure that later combined model. On HC1's ship
mail, merge its shipped format 62, raise PW5's format and floor to 63 under
the old-saves rule, prove refusal of a real native previous-format pair,
then run --fast and the save checks. The orchestrator accepts this merge
after the dry run without a second ALL if conflicts are confined to
GameVersion and records; any broader conflict requires another granted run.

### Passed ALL, then the HC1 format-63 integration

The granted ALL passes on the box: gate exit 0, suite exit 3 (named known
reds only), 243 harnesses in 1,007 seconds. Its kept run is
`/home/jerus/cbs/runs/cbs-gate-PW5-RnLpjOXm`, copied to
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-PW5-RnLpjOXm`.
The two tested commits are branch
`4d706651123af843ac140ee88de29286d75f99be` and main
`591fc92d859b438b02e093f1dbe66a1615c02018`. No unnamed red remains.
The named harnesses with reds are BuildAdviceCheck, CopperRefiningCheck,
Forms1900Check, FundCheck, KeptCheck, MapCheck, OrderDoorCheck,
PublicLicenceCheck and RoadCheck. Their existing records name them; PW5's
ship file still adds only its accepted OrderDoorCheck premise.

HC1 then ships Civic Ledger 0.9.190 at efca2c5fd, format and floor 62. Its
merge into PW5 conflicts in SaveFileCheck and ReadPathCheck, as well as
GameVersion and generated records. The save conflicts are competing
insertions of native-refusal assertions and the common fixture reader;
both batches' native pairs and assertions are retained. One common refusal
implementation checks both old cell shapes, hashes, files, running loaded
state and repeated read paths. All HC1 changes outside those insertions
merge normally. Generated maps, dials and month order are rebuilt together.
These two source conflicts require a second granted ALL under the
orchestrator's explicit condition; the passed run above is retained as
evidence and will not be represented as the merged model's ALL.

**Old-saves call:** raise PW5's SAVE_FORMAT and OLDEST_LOADABLE_FORMAT from
the shipped 62 to **63**. A native HC1 save has neither the struck nor
pending closing grid costs; loading it as a current save would incorrectly
erase the carried flow and change the next bill. Refuse it without a
conversion. The native preceding pair is captured from the unedited
published efca2c5fd checkout using GridLegacyFixture, playing the probe to
month 31 and writing scratch slot 10. Its 30-row history includes HC1's
debtSurveys, and its 141 household cells have 144 slots apiece. The README
and raw SHA-256 pins accompany fixtures/pw5-format62. It is never restamped.
SaveFileCheck, ReadPathCheck and SubstationCheck add the real format-62
refusal through the same intact-files/running-twin checks. The canonical
format-60 pair remains a single pair under fixtures/hc1-format60; HC1's and
PW5's independently produced format-61 pairs both retain their assertions.

The 80-run PW5 ensemble at 069504753 **predates HC1's merge**. The
orchestrator explicitly accepts that once-run final PW5-model ensemble,
with M1/F1 10/10 and 24/26, and asks for this integration's --fast and save
checks and a second ALL if the merge has broader conflicts. No replacement
ensemble is claimed for the combined HC1/PW5 model.

The merged format-63 validation is complete: --fast, SubstationCheck,
SaveFileCheck, ReadPathCheck, CardCheck, PowerPriceCheck and
BuildingDataCheck all exit **0**, one name per serial invocation with
cbs.parallel=1. --fast regenerates the complete merged indexes and reports
zero firm Stale findings and 199 soft. Logs and return codes are kept in
target/PW5-format63-results/. The native capture's separate --fast also
exits 0; its production executable is the published HC1, not the merged
PW5 classes. The new refusal line therefore proves the immediately previous
shipped format rather than a changed header on a current save.

The final integration fetch still finds main
efca2c5fd718895fae43ac4e05c73a9cd71f5933. All conflict markers are resolved;
the source and PW5 record diffs pass whitespace checks, and the edited
source and records retain LF. No repair, conservation, tariff or save bound
has been weakened. The provisional marker remains. Request the required
second ALL slot on this pushed integration; do not reuse RnLpjOXm for it.

### The merged ALL: new findings and fixture causes

The second ALL is retained at
`/home/jerus/cbs/runs/cbs-gate-PW5-MozBD0WW`, copied to
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-PW5-MozBD0WW`. Its exact commits
are PW5 `5544037a0dcdfa9f7498cddc7b782535dd21a1f9` and main
`efca2c5fd718895fae43ac4e05c73a9cd71f5933`. In 1,013 seconds, ALL 245 exits
4 and the gate exits 1. The 19 unnamed lines include OrderDoorCheck's three
assertions, PriceLevelCheck's one, ScaleCheck's one, KeptCheck's two and
LongPlaytest's two, their count/exit lines, and a separate default-city F1
finding. The sealed logs are not changed or reused. The marker stays.
REV-PW5-2 gives the merged save side SHIP: main's refusals are retained,
the format-62 pair is genuine, the bump is owed and continuation is asserted.
That SHIP does not turn this red ALL green.

**OrderDoor's cases spent each other's inputs.** The previously accepted
played-city edge now holds on the HC1 merge. Its later cases instead fail:
after the first mortgage, the two-block case raises 252.2893 against
278.1498 down, and properly trims to one. The later supposed short till
holds 105.4207 and actually buys a store. These are distinct from the old
edge's free-grid premise. Each mortgage now starts from an independent
native snapshot of the same prepared town, with its own original cash,
materials and holdings. The short-till bank case is likewise independent
of the earlier successful store order. The existing explicit dear-material
inputs and order observer are installed on the loaded copy; the snapshot
uses scratch slot 10 and both real files. No shareholder cash is topped up.
All original assertions pass. The two-block door now quotes 1,538.2090,
the owners raise its exact 244.5579 down, and both blocks are mortgaged.
The independent short till is 10.3809 and is held for bank capital.

**Scale's custom player never bought wires.** At its original m225 strike,
it has zero districts, 14,855.754 kW closing peak, only 80.7768% served;
the billed peak is 16,317.140 kW, 73.5423% served, at 315.311027 cents/kWh.
Its keepAhead plan adds wind farms whenever power is short, but no wires.
The plan now orders the missing districts through LongPlaytest.build, using
the standing draw at its highest season, the free founding carry and
Substations.SUBSTATION_MW; standing and on-site districts are counted once.
These are paid, grounded native construction orders, with the same player
build path as its other works. At m225 it now has one real district,
13,595.444 kW closing peak, full service and 32.934782 cents/kWh wires.
The original 60-month search, m225 start, six-month comparison and every
bound remain. It causes the calm growing town at m237 on its third try,
and the full named ScaleCheck exits 0. OrderDoorCheck also exits 0.

**The HL1 bond town also lacks physical wires.** The read-only trace at
m121 shows zero districts, peak 12,477.069 kW, 85.4336% served; by m182,
peak 17,077.776 kW is 70.2668% served, with the billed month only 64.2249%
served. Its energy price is about 315 cents/kWh. Retail's selected bond,
due m184, is written off completely at m182: face zero, cumulative writeoff
28.070412087. The fixture treats any removal as maturity and consequently
mistakes a 0.004 orphaned coupon for the principal. Every monthly household
identity still closes; the native maturity and principal assertions remain.
Calling peakWires only before the warmup is insufficient: its private stock
grows past the free carry during the original 120 preparation months.
The added SubstationCheck warmup guard demonstrates this directly (one
failure). Preparation now supplies real peak wires before each of those
same 120 months, with native capital tariffs, repairs and filled posts.
It neither supplies nameplate-sized wires nor adds a district below the
free threshold. The new guard and the full HL1 run are being rechecked.

**PriceLevel's first differing field is traced, without changing tolerance.**
For --twin-built at m81 it is Agriculture cash, scaled to
1181.7235841924300 versus 1181.7235829937686. The 1.1986614e-6 difference
is just above the existing 1e-9 relative onset bound, with zero same-level
and one across-level pair. The native bounds and nudge cases remain; this
FS2 classification must retain that exact field and figure. Read-only
diagnostics are opt-in under cbs.pw5.traceFixtures. Initial diagnostic
results are kept in target/PW5-post-HC1-traces/; fixture-mend results are
separate in target/PW5-post-HC1-mends/.

**Kept's two lines remain under investigation.** The held household grid
closes one cell in both the held and ordinary month; all 106 retained
tooltip identities match. Its box heap is 1673.2 MiB against the unchanged
1536 MiB limit. FS5's existing queued row already records main's box
Monocle/SW heap class at 1711.9 MiB, against Windows SW 1502.8 MiB. The
standalone Windows named rerun is in progress; no heap bound is relaxed.

**The default F1 is a separate model finding.** It reports households with
no home for 16 months, m792-807, peaking at 12.283 beside 1,496 homes at
m807. The pre-HC1 ALL had no such finding. On 2026-11-22 the orchestrator
therefore requires a new 80-run ensemble on the merged final model before
the next dry run, with M1/F1 stopping a ship. This supersedes the earlier
acceptance of the once-run pre-HC1 ensemble for the combined model. Trace
the actual power service, standing and charged repairs, builder output,
home pipeline, mortgage capital and landlords' orders through m760-825;
mend PW5's model if it caused the hold, and name only a class main also
shows with a queued owner, following CU2-notes lines 263-290. No merged
ensemble or green F1 verdict is claimed yet.

The corrected growing-stock bond preparation now passes SubstationCheck's
new full-warmup guard. In the m830 diagnostic, its selected bond stays until
the real m184 maturity and the original principal and household-book
assertions pass. Power generation can still be scarce early; the physical
wires cover the billed and closing peaks, which is the guard's premise.
The m830 replay is diagnostic, not a replacement for the normal 2,400-month
run: it reproduces the exact m792-807 housing window (12.283026838 at m807),
and its post-limit continuation separately flags three unsettled-line bounds
at m834 that the retained normal run did not flag. No exception is added
for those diagnostic-tail lines and no full-run verdict is inferred from it.

**The housing orders wait on land.** The trace from m760 through m800 has
zero home pipeline, unchanged 1,496 homes and an unlimited mortgage-capital
room. Every sampled landlord order says "Holding: no land - needs 5,570 m²,
922 m² free". Its grid is fully served in both billed and closing states,
one district with 100,000 kW carry against roughly 10,000-13,600 kW peaks.
Standing wire repairs are 110 points; total charged repairs are about 304-307.
Actual builders rise from 418 to 446 and leave 114-139 points for sites.
At m807, 568 homes are on site, builders are 1,648 and 1,341 points remain
after repairs. At m808, homes rise to 1,559 and unplaced households fall to
zero. This rules out missing wire capacity as the direct hold and shows
that no home site was competing for points before the land hold broke.
The land buyer's actual budget and offers are the next trace, before a
model attribution or queued owner is decided.

The standalone Windows KeptCheck completes with only the held-grid line
red: its heap is **1,336.7 MiB** and the unchanged quarter-free bound passes.
The grid still closes one cell in both held and ordinary months while all
106 retained tooltip identities match. The box's heap line is the existing
FS5 platform class recorded on main (1,711.9 MiB versus Windows 1,502.8);
PW5's box figure is 1,673.2, with no UI or heap-rule change. Full results
remain under target/PW5-post-HC1-mends/.

FS2 has already shipped (2026-11-20). It cannot own a new known-red row.
The price-level diagnosis therefore remains open; retain its bounds and
trace the physical grid and cash divergence before naming an unshipped
owner or making a mend. AP2 has now shipped 0.9.191 at 1b89c5c4c, speed
only with every figure exact and format still 62; merge it before the
required merged-model ensemble and next dry run. PW5's format stays 63.

AP2 is merged from 1b89c5c4c with no source conflict; four generated-index
conflicts are resolved by regenerating the indexes. The merged --fast exits
0. VERSION is inherited as 0.9.191; PW5's format and floor stay 63 because
the shipped format is still 62. The new read-only ground trace records
the actual look, cash-share budget, chosen offer, price and hold reason at
the buyer's own phase. The price digest now also compares native grid
count, carry, peak, service, tariffs per MWh, capital, repairs and payroll;
grid count is exact, and all old monetary/real tolerances remain. Its cash
trace records the progression before the first Agriculture-cash mismatch.
The replay through m830 and the full price-level harness are running on
this merged source, serially; their results are not yet claimed.

The AP2-merged replay again matches the original default housing window.
At m743 the player leaves 1,534,346.965 square feet free, used share
0.934660113003 against its 0.945640734565 forecast line. It does not make
another ground look until **m805**, when it has 216,902.323709505 cash and
a 21,690.232370951 budget. Three parcels costing 2,992.643026560,
3,471.090657765 and 3,673.727301570 are bought within that budget; the next
15,308.447933355 offer is properly held against the remaining budget.
The cause of the preceding hold is the gap between looks, not that later
budget hold. Housing orders had waited for ground throughout the gap.

For a current-main control, an isolated checkout of **1b89c5c4c**, format
62 and without any Substation class, runs the same m830 diagnostic. Only
read-only observations in LongPlaytest are added; production source is
unedited. It shows the same ground-order hold at m760, m792 and m800:
5,570 m² needed, 286 m² free, no home pipeline, unlimited mortgage capital,
full power and 268-367 site points. Main's looks are **m741 then m804**.
Its 1,951 homes leave a larger buffer, so it has no unplaced household in
that window. This is the same deferred-land class, not an assertion that
the two cities have the same housing counts. Both diagnostics' unrelated
post-limit unsettled-line flags occur after m830 (PW5 m834, main m835);
neither is represented as a normal-run finding or granted an exception.
The control's --fast exits 0. Its logs remain at target/PW5-native-main-land/;
PW5's AP2 replay is at target/PW5-main191-traces/.

**★ Queue LP-G1 for the player's land horizon and skip timing.** The model
has power and construction points available while no housing site can be
ordered, and current main shows the same no-land hold and over-sixty-month
gap. keepGroundAhead uses BuildAdvice.HORIZON's six months but is called
at advisor looks separated here by 62/63 months. Preserve the player and
its cash-share dial in PW5; queue a fixture that causes the gap and a mend
that aligns the next look with the reserve. The default seed-0 F1 line is
named to LP-G1 for review, with no permission to pass other ensemble seeds
without diagnosing them. The mandatory full merged ensemble remains to run.

OrderDoor's old edge row is removed because the named harness now passes.
The played-edge/search work still belongs to HT1-H as a latent fixture;
these independent-order mends do not claim to have made that edge causal.
Kept's two native classes are named to the existing, unshipped FS5 row,
with the box and Windows measurements above and every bound retained.
PriceLevel is still open: no row or waiver is added while its cash-writing
phase is being traced. Temporary cash observation in Sector is diagnostic
only and must be restored before final checks, review or ensemble capture.

### The price-level onset's extremal controls

The cash observer is restored out of Sector and the twin before a fresh
`--fast` (exit 0) and full PriceLevelCheck. Its stored eight built-city
digests resolve which pair the onset names: **both +1e-12 nudges**, factors
0.025883463843004304 and 1.000000000001, not the unperturbed twin. The
unperturbed pair's live series never part through all 120 months under
the existing `parting()` bounds. Its m81 Agriculture cash is
1181.7235830395964 against 1181.7235832180160, a 1.7841967e-7 gap.
No numerical nondeterminism is inferred from the earlier observer: it
recorded the unperturbed pair too, whose numbers match the fresh run.

At m81 the four founding-level controls span 1181.7235830302513 to
1181.7235841924300 (width 1.162178716e-6); today's four span
1181.7235829937686 to 1181.7235840809797 (width 1.087211100e-6).
The intervals overlap. Their one extremal cross-pair gap, 1.198661494e-6,
exceeds the same 1.181723584e-6 onset band by only 1.4%, while each
within-level diameter remains below it. Thus the sampling guard finds
zero same-level pairs and one across; it does not find a failed live
law between the two unperturbed cities. This is FS2's documented latent
rounding-onset premise, exposed at a different balance-sheet edge, not
evidence that a wires amount missed its scale factor. FS2 already shipped.

The phase trace corroborates the class without changing inputs. The
flagged pair's Agriculture-cash gap is 6.460822988e-10 entering m81,
1.183479981e-9 after supplier credit, then 1.196799985e-6 at the equity
raise in `Game.consider()` (the `Equity.raiseFor()` / `offer()` path).
The same raise amplifies a **within-founding-level** nudge by
1.151022389e-6. The two levels both have zero districts, free carry
10,000 kW, the identical 6,432.322105263157 kW peak and service ratio 1;
their native grid capital, repairs and payroll are all zero. That rules
out either wire rationing or a mis-scaled district cost at this onset.
The source's equity target is the existing income-variance ratio and its
raise is a balance-sheet subtraction; neither is changed by PW5.

**★ Retain every price-level bound and all three original nudges; name
only this built-city onset line to HT1-H.** No extra nudge is chosen,
no district is forced below the founding carry, and no comparison horizon
is shortened to make the line pass. HT1-H already owns moved played
premises whose sweeps shipped; its eventual cause must distinguish this
overlapping, extremal-control case from a real factor error. The opt-in
onset report now names each child's actual factor so that the pair cannot
be mistaken for the unperturbed twins. The raw digests and independent
pair readings are kept at target/PW5-restored-source/digests/ and
target/PW5-restored-source/pair-readings.txt; the earlier cash phases are
at target/PW5-pricecash-probe/. This classification and LP-G1 still need
the stranger's delta review and the required merged-model ensemble.

Main's later ea16576cd (AP3/AP4 records only) is merged with no conflict;
no further model code changes arrive with it. The publication sentence now
qualifies the m576 / 6,176-builder-point census as **before HC1**. It is
the measured default run recorded above, rather than a claim that the new
merged city's first district completes in that identical month. The
district's own 110-point continuing repair load is unchanged.

### Merged fixture-mend validation and review handoff

The final serial pass on 7e8fe7eda (with only publication records changed
after it) uses the real data paths and cbs.parallel=1. No model, save,
money-audit, conservation or comparison bound is changed in these mends.
The temporary cash observer is absent from source and rebuilt classes.

| check | result |
| --- | --- |
| --fast | 0; indexes regenerated, 0 firm / 199 soft Stale findings, tree unchanged |
| LongPlaytest, normal 2,400 months | 3; only the named LP-G1 finding, 16 months m792-807, worst 12.283 households beside 1,496 homes |
| SubstationCheck | 0; includes the growing money-town's full-warmup wire-carry guard |
| PowerPriceCheck | 0 |
| BuildingDataCheck | 0 |
| SaveFileCheck | 0; real native shipped-format-62 refusal preserves both files and the loaded running twin |
| ReadPathCheck | 0 |
| PriceLevelCheck, full normal run on the restored model | 4 before the new classification; explicit saved-log rejudge 3, only the traced HT1-H onset line |
| KeptCheck, retained standalone Windows run | 4 before the new classification; explicit saved-log rejudge 3, only the FS5 held-grid line; its unchanged heap bound passes |

The complete LongPlaytest confirms the HA1 mend: **63 presses to actual
maturity at m184**, drift 3.7882 before it and 3.7992 in the maturity
month. The unchanged principal-floor and household-book assertions both
pass. Its only failure is the separately traced default housing window;
none of the short m830 diagnostic's post-limit unsettled-line flags
appears in the full run. OrderDoorCheck and ScaleCheck already exited 0
on these fixture mends; the subsequent main merges are exact-speed AP2
and record-only AP3/AP4.

The first saved-log judge call returns 1 because the earlier log labels
the line NEW while the new ship row names it. The supported
`CBS_REJUDGE=1 ./batch-check.sh --judge LOG` then returns 3 for both
retained PriceLevel and Kept logs. These are classifications of unchanged
standalone logs, not a replay or a change inside a sealed gate run. The
final raw logs, status and judge results are at
target/PW5-final-fixture-mends/. The published exception rows still await
the stranger's delta review.

A final fetch leaves main at ea16576cd; no newer merge is pending.
`tools/pick.sh origin/main HEAD` is **ALL 245**, with review needed for
DataSave, SAVE_FORMAT and OLDEST_LOADABLE_FORMAT. The earlier money and
merge-save SHIPs remain recorded above; this handoff asks for the new
fixture and finding classifications. The ship parser and scoped
whitespace checks pass, all edited files retain LF, and publisher-owned
records and VERSION match main. The next required work is the fresh
merged-model 80-run ensemble against the newest 0.9.190 baseline, then
another granted ALL dry run; neither is claimed complete here.

### Delta SHIP: scope the exact exception lines, then the merged ensemble

REV-PW5-2 on 3d61452e4 is SHIP once its exception parts are scoped, with
no further review required (orchestrator, 2026-11-22 23:58). KnownReds
matches substrings: the LP-G1 part now includes the native padding and
**16 time(s), months 792-807**; the HT1-H PriceLevel part includes
**(month 81: 0 same-level pairs and 1 across)**. A different window or
onset is not covered. The Kept heap row is removed from PW5's ship file
because main already records that exact line; its measurements remain
above. The separate held-grid row remains scoped to that native label.
Main's HT1-H ownership record at cbf8a362e is merged before the ensemble.

The reviewed LP-G1 attribution is main's class, but **"a larger buffer"
understates the city-size change**. At m760, native main 1b89c5c4c has
3,250 households, housing capacity 8,200 and 593 builders. PW5 has 1,776
households, capacity 5,944 and 418 builders: about **45% fewer households**,
28% less housing capacity and 30% fewer builders. This is a substantial
default-city change even though the immediate no-land order hold and long
look gap occur on both models. These figures must be read beside the new
ensemble's population score, rather than treating the two cities as the
same size with a slightly different buffer.

The final merged model now runs 80 cities against **0.9.190** on the gate
box, using that baseline's options without laptop data-path overrides.
Only M1 or F1 stops the ship; any new finding still needs its own trace.
The older pre-HC1 ensemble is not the merged-model verdict.

### Merged final ensemble: 80 complete, M1 clean, seed-9 F1 traced

The fresh run uses pushed **9c769adf3**, label
PW5-HC1-final-9c769adf3-20261123, on gatebox, OpenJDK 21.0.13. It waits
behind RES3's ensemble, then runs eight cities at once on the box; there
is no laptop fallback. JAVA_OPTS is empty and JDK_JAVA_OPTIONS is unset,
so the box uses its own earth-2/world-1 data copies, named in the stored
baseline. All **80/80** runs finish, none short or missing. The complete
102,352 recorded audit months pass, with none unread: **all five M1s pass**.
The new baseline at docs/ensemble/PW5.md replaces the pre-HC1 capture and
retains the raw score and JSON, including its failures.

The raw score is **20/26 versus 0.9.190's 25/26**; hard criteria are 6/10.
Four F1s change to FAIL, each solely seed 9 at m605. SHOCK's T2 population
is the sole newly failing informational criterion; it does not stop a
ship. Final population medians, read beside the earlier default-city gap:

| policy | PW5 | 0.9.190 | share of baseline | T2 |
| --- | ---: | ---: | ---: | --- |
| AUTO2 | 4,649,724.5 | 5,217,451 | 89.1% | PASS |
| ZERO_SLACK | 2,883,284.5 | 3,579,897.5 | 80.5% | PASS |
| ZERO_HOT | 7,632.5 | 7,562.5 | 100.9% | PASS |
| SHOCK | 67,498 | 102,120.5 | 66.1% | FAIL, info |
| HIGH | 32,356 | 31,959.5 | 101.2% | PASS |

These aggregate medians do not erase the large normal default-city
change: at m760, main has **3,250 households / 8,200 capacity / 593
builders**, against PW5's **1,776 / 5,944 / 418**. PW5 has about **45%
fewer households**, 28% less capacity and 30% fewer builders there.
The study's lumpy district charge remains in the model and in PW5-F1;
none of those effects is removed to improve this population score.

**F1 is a separate seed-9 finding, not the default window's exception.**
AUTO2, ZERO_SLACK, SHOCK and HIGH each flag only m605, households with
no home; ZERO_HOT has no finding. The four seed-9 CSVs match every field
through m600 except the phase label, before their policy paths split.
All four have full power and a clean money audit at m605. The normal
LongPlaytest also runs annual GovernmentPlayer actions; the monetary
controller intentionally omits them. Its seed-0 default window is not
silently substituted for this ensemble's seed-9 case, and its exact ship
part remains limited to 16 months at m792-807.

A read-only AUTO2 replay through m700 matches **all 699 CSV rows and every
value** of the box run. At m560-603 the landlord repeatedly holds for
land, with no home pipeline. At m600 it needs 5,570 m2 with 24.6 m2 free,
has unlimited mortgage capital, full power, one district carrying the
27.188 MW peak, and **1,354 site points after 574 repairs**, including
the district's 110. It makes **no ground look m500-603**. At m604 the
player finally buys five parcels within its unchanged 24,744.685 budget,
then correctly holds the next 9,034.633 offer against 7,278.795 left.
At m605, 17.895817409 households are unplaced with 3,087 homes now on
site; builders supply 7,277 points, repairs take 574 and 6,703 remain for
sites. Power is fully served at a 24.690 MW peak against 100 MW of carry,
with a native wires bill of 42.14 cents/kWh. At m606 homes stand and the
finding clears. There is no home site competing for points during the
preceding exhausted-land hold; no wires or repair charge is refunded.

Native **main 1b89c5c4c**, without a Substations class or model edits,
replays the same monetary seed 9. It makes **no ground look m500-602**.
At m540 its landlord also needs 5,570 m2 with just 21 m2 free, has no
pipeline, full power, unlimited mortgage capital and **1,132 site points**.
The next look is m603, at least 103 months after the preceding look,
against the same six-month forecast. Main's different, larger housing
stock avoids the m605 flag; at m610 its studio order again holds for
land with no pipeline, full power and 1,010 site points. This establishes
the long-skip ground class on current native main, rather than claiming
that the two cities or their finance paths are equal.

**★ Name the four seed-9 F1s to LP-G1's deferred-ground class.** Its brief
now includes this precise monetary-controller case beside the separately
accepted normal default case. No other seed or window is passed, and the
raw ensemble remains FAIL; the outside-class attribution must be taken
by name. No scorer threshold, housing bound or money check is changed.
The dry-run-slot request reports that verdict and evidence explicitly.

The replay uses the existing named EnsembleCheck to compile and invoke
the unchanged driver, with temporary read-only LongPlaytest logging.
The first temporary logger has a compile typo, corrected before the run;
the PW5 and native-main diagnostic runs then both exit 0. Both source
backups are restored, the diagnostic driver classes are removed, and
the final restored --fast exits 0 (0 firm / 199 soft findings). Regenerated
indexes differ only in their date stamp, so the already committed indexes
are retained. No production or harness logic change follows the pushed
ensemble model. Logs and CSV comparisons are at target/PW5-ensemble-seed9/;
the complete run and score are at
target/ensemble/PW5-HC1-final-9c769adf3-20261123/.
