# RES2 — aggregate housing inventory

Built from 52c79806b (0.9.187), integrated with shipped RS1/main 01e6c2d4b
(0.9.188) in initial model commit 32c81f469. The reviewed-model mends are
51f417367; final post-review verification is against that newest main. No new design call remains for Jerus.

## What changed and the calls

- ★ BuildingManager/BuildingsStacks remain physical authority. HousingInventory
  projects standing structures and pipeline through immutable dwelling mixes;
  there is no saved parallel structure counter. Existing aggregate Completion
  carries its mix once; zero work, repeated work and loading add no event or homes.
- ★ Keys identify recipe/class cohorts, never people, homes or individual
  buildings. One equivalent service cohort per recipe suffices for the current
  forms. HousingRecipe.MAX_CLASSES bounds physical class rows at 32; four owner
  roles (RE, household, estate, outbound) bound saved rows at 128. Equivalent
  quantity parts share one row, and empty rows disappear. Future distinct common
  or loan obligations need explicit bounded cohorts in HO1, not building IDs.
- ★ The founding 100 places per founding endowment have a named
  foundingCapacity() getter and no saleable doors/title. Existing size and door
  totals exclude them; capacity includes them. RE portfolio capacity excludes
  the endowment and includes its actual sites.
- ★ Only the three existing forms/IDs are supplied: House 0, Studio Apartments 1,
  Low-Rise Apartments 6. Their capacity, doors, prices, work, materials, staffing,
  utilities and era stay compatible. Immutable metadata supplies form/mix,
  per-home capacity, area, bedrooms, quality, private/included/shared parking and
  fixed cost weights. The compatibility metadata uses 20 square metres per
  capacity place, zero studio bedrooms and half capacity rounded up otherwise,
  quality 1 and area weights. These are disclosed initial metadata, not a claim
  of completed RES3 calibration. Existing forms activate no new parking rights.
  Built-in/mod fallback preserves former rounded sizes and declared capacity.
- ★ Ownership, occupancy and listings are separate quantities. Transfers validate
  the whole operation before mutation, retain the stated occupants and consume
  accepted occupied/vacant offer quantities once. Listings remain subsets; they
  create no homes or money. Conveyed titles prevent physical removal. Existing
  anonymous tenants are recounted by the legacy match after its retirement
  policy acts; no new sale or demolition policy is switched on here.
- ★ allocate(key, owner, amount, structureBook, landBook, collateralBook) partitions
  actual whole completed recipe-cohort bases by fixed title weights. Inputs are
  the whole cohort across all titles, not an already reduced RE-only balance
  scaled again. All title allocations reconcile to the project, including its
  common facility share. Partial allocations sum. HO1/RES5 settle funded closes,
  release debt/collateral and keep/derive the basis coherently; this interface
  quotes no sale price and pays nobody. Pipeline books are separate.
- ★ RN2's last-counted capacity remains a saved accounting baseline in RealEstate
  extras, distinct from physical stock. Economy inputs derive each next count
  from the inventory; the load restores the saved last count after its rebuild.
  Both changed-capacity and zero-count findings remain. No RN2 premise or save
  comparison was removed.
- ★ New inventory state goes through DataSave and Game's load rebuild. Under the
  old-saves rule a bump is owed: missing authoritative assignment/title/listing
  state is not a correct inventory. SAVE_FORMAT and OLDEST_LOADABLE_FORMAT are
  both 61, advancing from current shipped main's 60, with no conversion code.
  If HC1 ships first during review, integrate it and advance again before gating.

Own review mended two findings. A cold direct Game.loadGame skipped the physical
catalogue, so strict inventory restore correctly refused unknown classes. It
now routes an uninitialized game through loadGameSave's canonical initialization;
HousingCheck's existing cold-load assertions remain unchanged. Also, merely
clamping seller listings could retain accepted offers after a partial transfer;
transfer now subtracts each accepted subset, proved explicitly. No conservation
or save check was weakened.

RS1 merged without a source conflict. Four generated-map conflicts kept RES2's
side temporarily and were regenerated from the merged source by the checks.
No sector, building ID, new activated form, UI or publisher record is added here.

## Consumer contracts and BO1-H

BuildingManager.getHousingInventory() is the entry point.

| Consumer | Actual API |
|---|---|
| RES3 | stock(): recipe/form/mix, class metadata, standing and pipeline structures; homes(), capacity(), homesBySize(), pipelineHomes(), occupied(), vacant(); match(double[] occupiedBySize) accepts the legacy match |
| RES4 | stock().recipe()/dwelling(): privateParking, includedParking, sharedParking, fixed area/cost weights |
| HO1 / RES5 | owned(), ownedCapacity(owner, includePipeline), occupied(), vacant(), listed(), occupy(), list(), transfer(), allocate(); forSave()/restore() go through DataSave and the actual load path |
| Legacy reports | BuildingManager door/size/capacity and RE portfolio getters project the registry. RealEstate.getOwnedHousingCapacity() explicitly remains the last-counted debt-allocation baseline; inventory.ownedCapacity() is the current fractional title/pipeline read. The old long portfolio getter truncates only at its compatibility boundary. |

FamilyModel.occupiedBySize() returns actual lets from its existing match, rather
than reconstructing occupancy from rent weights. The live month passes that
result to HousingInventory.match after the final housing pass. Construction
still completes before demographics in SimulationEngine; loading restores
quantities and no trade/price/month advances.

BO1-H remains its own queued row. Business-owner families belong in FamilyModel's
seeker groups, matching and rental bills; they are not a new title-owner class.
Its seam is FamilyModel.occupiedBySize()/HousingInventory.match. RES3/BO1-H must
add the missing owner-family seeker group there while preserving stock counts.
No takeover or additional household-cell axis is implied.

## Caused findings and checks

HousingInventoryCheck causes the brief's acceptance through recipes, actual
BuildingManager construction work, quantity APIs, DataSave and scratch Game
inputs. The two completed mixed structures deliver 2 small and 4 family homes,
20 capacity plus the named endowment, with one event and an emptied pipeline.
Zero work/quantities create no event/row. Fractional title and occupant transfers
conserve stock; offers are consumed as subsets. Split/merge removes empty estate
rows; invalid quantities, excess offers, impossible removals and corrupt/excess
saved titles are rejected atomically. Book allocations reconcile by partial
quantity and across all titles. The 1000x represented stock retains 2 physical
class rows / 2 saved cohorts. The current catalogue has 3 compatible classes.

The actual scratch save uses slot 10 in GameFiles.scratch, retains a half title
and quarter occupied home, and changes RE's counted-capacity baseline by 5%.
The reviewed fixture also includes two Houses and a Studio on site, with partial
House progress. Its RE portfolio capacity is 217 (pipeline included), saved at
206.15 with a stranded share of .05. Both first months count 217 and dilute that
share to .0475, complete the same House and preserve the remaining pipeline. Existing ReloadCheck 3k also
retains its caused changed-capacity and zero-capacity first-month proofs.

| Check | Result |
|---|---|
| HousingInventoryCheck | green initially, final before integration, and on merged main |
| HousingCheck | green, all door/household/rent and RN2 checks |
| HousingDoorCheck | green: months 431–900 finish 960 size-2, 6,237 size-4 and 26 size-6 homes; no unplaced household; all 899 played months close household cash |
| BuildingDataCheck | green: all 155 definitions compatible, housing mix metadata agrees with built-in fallback |
| SaveFileCheck | green, with exact inventory serialization equality after actual rebuild |
| ReadPathCheck | green: 3,154 fields fingerprinted, including new cohorts and pure inventory/quote reads |
| ReloadCheck | exit 3, only main's HT1-H caused material-draw premise; every actual continuation comparison and every RN2 3k premise/comparison passes |
| AutoBuildCheck | exit 3, only main's FS12 zero-new-orders replay premise; twins have no replay drift |
| ReplayCheck, ScaleCheck, BuildingStackCheck, SaveSlotCheck | green |
| --fast | green before and after merging RS1; generated maps refreshed; zero firm Stale findings |
| Default LongPlaytest before integration | green; all eight CSVs observed byte-identical to fresh 52c79806b baseline |
| Fresh main 01e6c2d4b and merged RES2 default LongPlaytests | both exit 3, sole identical CL7 industry/home ring finding at m2400; no new red |

The named focused checks above ran before the RS1 integration; merged verification
reran HousingInventoryCheck, --fast and the full default LongPlaytest. All runs
were sequential, one name per batch-check call, with the required C:/ Earth/world
paths. All fixture cities use scratch/TempDirs. Logs are target/RES2-checks/ and
target/checks/; reference output also remains in target/RES2-main-reference/.
The pick is ALL (242 harnesses), review needed for DataSave and both format
constants. The laptop restriction forbids a local full --pick; the complete ALL
suite is the gate-box dry run after the stranger's saves review grants SHIP.

Known reds are traced, not repaired by changing a fixture. ReloadCheck's native
five-House order did not cause a material draw; main's cause ignores its accepted
result (existing HT1-H). AutoBuildCheck's existing section 4 city remains above
its debt limit and places zero new orders (FS12). Fresh main itself causes the
CL7 map-ring finding at m2400 before any RES2 code is present; RES2 changes no
map placement. All three are already recorded and queued. The ship file names
their exact classes. No new queue row or approval is requested.

## Final trace and measured cost

Fresh main 01e6c2d4b was compiled and ran only the named 2,400-month LongPlaytest
in an isolated detached reference checkout. Integrated model 32c81f469 and the final reviewed-model mend 51f417367 both
ran the same default with the same JDK, seed/options and required Earth/world
files. Every one
of the eight complete CSVs is observed byte-identical, not merely rounded equal.
The comparison and raw files are target/RES2-trace/comparison.json,
main-01e6-*.csv and merged-*.csv. The earlier independent 52c79806b comparison
is retained as pre-RS1-comparison.json, base-*.csv and final-*.csv.

| CSV | bytes | identical main/RES2 SHA-256 |
|---|---:|---|
| bank.csv | 868250 | `937ad04184737e46e47be62badae881889a974153a90150973f563afb1b3065d` |
| bonds.csv | 5076706 | `2a96689c8b5495a487be3390ff1ec853c3fcaa95a094bf909215b7c258d0fdc9` |
| borrow.csv | 3274 | `01c98659ff1ba3e1f76a65e0a1f76e204929a7b7b8b9617965f34a8dae121362` |
| build.csv | 606265 | `7e95cb28153b7052f830ac712e410547a3d3faecdbe04d32e5f27c1e51df13e0` |
| house.csv | 769785 | `e4d6b6465c8726e8cf51fded6bfcb52f6af4d80c9ece2c243ccfac7b3d2d10ad` |
| labour.csv | 730810 | `416106f0a25a89d9b8f84d06d412c029129abfde9fdddd17ef68ae87be3e4786` |
| month.csv | 909444 | `01cdf5413fadd8ff2dd907cc315bcc9a4b551826e34a0c2fae17426b70561565` |
| pop.csv | 413337 | `db1f72f012010d9b004f653955785f8d9d161a99db6493a6c302b2b8e07d3504` |

The default trace is unchanged, so no ensemble is owed. RS1's inherited trace
move and its already shipped ensemble are not attributed to RES2.

Measured merged scratch-city first month: 320.581 ms for the whole integrated
month, with 1 physical class row / 2 saved cohorts; the 1000x mixed fixture is
still 2/2 rows. This one observation is not isolated inventory overhead or a
whole-feature speed promise. Fresh-main and merged default harness elapsed times
were 298.0 s and 294.4 s (including their additional checks, soft on this laptop).
These observations do not replace nightly performance judgement. The study's
0.864 ms synthetic kernel is not asserted as an integrated ceiling.

## Review and gate handoff

The final mended model source is 51f417367; the following records/maps commit
changes prose only. The same stranger reviews the delta, including money and
saves for the restored construction-work input. No sale or new thread is
activated. The delta review is SHIP and the ALL dry run is green, as recorded below.
The marker is removed last in its own commit; no model source changes after
this retained run.


## REV-RES2: three P1 mends

The regression-only commit 57999ec3f leaves the reviewed model unchanged and
adds guards for each reported gap. Its HousingInventoryCheck reports the exact
1.5-versus-1 overcount, invented .5 occupant, .75-versus-.5 residual and 1.75
repeated-match total. Its real-reload premise fails because no pipeline exists.
SaveFileCheck's native-schema premise fails on the relabelled current record.
Logs: target/RES2-review/*-before.txt and *-before.log. Findings 2 and 3 are
coverage defects; no claim is made that the existing format floor accepted old
saves. Both their guards and the old boundary/refusal assertions are retained.

1. Equal-size matching now sums retained non-RE occupants by size first, sums
   the available RE-owned doors, and only then allocates residual demand over
   those doors. Matching neither invents an occupant in a second class nor
   reallocates a conveyed title's occupants. The caused two-class fixture checks
   whole, fractional and repeated demand after conveyance.
2. The native reload fixture now has two Houses on site, one partly worked,
   plus a Studio project, mixed RE/household titles, occupied and vacant homes,
   both listing subsets and the endowment. Every class/owner's owned, occupied,
   vacant and listed quantities, physical structures, pipeline, site progress,
   capacity and endowment are compared immediately and after both native next
   months, in addition to RN2's changed-capacity debt allocation.
   The expanded fixture exposed a real continuation difference: opening work
   fill was 1 live versus 0 reloaded, with the same 160 physical point capacity,
   offered share and road ratio. The source advanced a House while the loaded
   twin did no work. No premise or tolerance was weakened to pass this.
   ★ Construction now saves that previous-period work input in sector extras
   (through DataSave's SectorState) and Game restores it after the load's wage
   rebuild, just as RN2 restores its counted-capacity baseline. The input is a
   flow because sites advance before the next wage count (SimulationEngine).
   The fixture now reports 156/156 opening site points, both cities complete a
   House and retain the remaining pipeline; all registry comparisons pass.
   This justified the additional Construction source change and makes the
   delta a money-and-saves review: the first loaded month's actual work must
   receive exactly the running city's settlement, not a newly recomputed rate.
3. Reused HC1's exact native format-60 save/history pair and producer from
   shipped 01e6c2d4b, under fixtures/hc1-format60, with its existing hashes and
   original 128-slot cell layout. There is one shared pair, no relabelled JSON
   or conversion. SaveFileCheck's old-city case uses those exact bytes; a
   separate synthetic header keeps the exact one-below-floor boundary tested
   even after a later bump. The shared nativeCard60Refusal helper checks the
   fresh-game refusedAtTheFloor path and a current save loaded into a running
   twin. ReadPathCheck also exercises repeated actual reads after refusal.
   Both file byte streams, full saved state/history and manager identities are
   unchanged. HC1 and RES2 use the same artifact paths/helper name so the
   second integration keeps one pair and producer.

The Construction work-input field is added within the still-unshipped RES2
format-61 schema. Current shipped main remains format 60; the existing bump to
61 and matching load floor remain owed, with no second bump until HC1 ships
first. Its absence from native format 60 is not repaired or converted.

Final delta checks are complete. HousingInventoryCheck, SaveFileCheck,
ReadPathCheck, HousingCheck, ConstructionControlCheck, ConstructionTrapCheck and
--fast are green. ReloadCheck exits 3 solely on its already recorded HT1-H
material-draw fixture; every RN2 changed-count and zero-count premise/comparison
passes. LongPlaytest exits 3 solely on the same CL7 m2400 finding as fresh main.
All eight complete post-mend CSVs are observed byte-identical to main 01e6c2d4b;
no ensemble is owed. The final comparison is
target/RES2-trace/review-comparison.json with mended-*.csv, and the named logs
are target/RES2-review/*-after.txt. No model change followed these checks.

The three coverage guards demonstrably fail with the reviewed model/input
fixtures in regression-only commit 57999ec3f and pass after 51f417367. In
addition, the expanded pipeline initially failed actual next-month parity on
the unchanged 1-versus-0 work fill; preserving that period input mended the
failure without changing the fixture or relaxing any conservation/save check.
HC1 has the same native fixture bytes and producer at the same paths. Whichever
ships second merges main, keeps this one pair/shared helper, and updates the
format from the then-shipped value.

Post-mend trace verification is complete at model 51f417367: all eight files
match the fresh 01e6c2d4b main files byte for byte. The mended scratch month was
312.617 ms with 2 physical class rows / 2 saved cohorts, including its genuine
pipeline. ConstructionControlCheck and ConstructionTrapCheck also pass their
native save/load, contractor-book and next-month checks. These additions are
kept in target/RES2-review; no model source changed after final verification.

The final mended LongPlaytest measured 299.5 s on this laptop (known CL7 only);
its timing is observational, and its complete eight-file comparison is exact.


## Final review, ALL dry run and readiness

REV-RES2's delta review of e20e3ea63 is SHIP (2026-11-22). It traced the saved
construction input through the post-wage rebuild restore and the clearing of
its pending value. That SHIP authorized the ALL dry run; no ensemble is owed.

The gate box completed the ALL selection (242 registered harnesses): the
standard gate runner executed 237, with its five nightly-tier harnesses excluded
by normal policy. Of those, 225 passed and 12 had only named main reds. The check
phase took 846 s (the harness runner 827 s), returning 3 for known reds; the
launcher/publisher dry run returned 0.
unnamed.txt is empty. The recorded classes were AutoBuildCheck, BuildAdviceCheck,
CentralBankCheck, CopperRefiningCheck, Forms1900Check, FundCheck, KeptCheck,
LongPlaytest, MapCheck, PublicLicenceCheck, ReloadCheck and RoadCheck. The
existing main record plus this ship file names them; no new red was waived and
no check or conservation bound was relaxed.

Retained run (immutable):
`/home/jerus/cbs/runs/cbs-gate-RES2-OIFTyuGi`.
Its copied logs/checks are at
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-RES2-OIFTyuGi`;
the local relay log is `target/RES2-dry.txt`.
The two exact tested commits, from branch.commit/main.commit and ship.log:

- RES2: `e20e3ea632d224d5f29b20f07ac92f1ce3c5d6b9`.
- Main: `4b5de63e123361c689767fe2c3b0d8fca08ad928`.

Main advanced from the reviewed 01e6c2d4b through queue/docs-only commits; the
run incorporated 4b5de63e1. Subsequent 6ec5ef03d also changes only QUEUE.md and
EDU2/RW1 briefs, so the tested game source is unchanged. The kept-run reuse
contract explicitly permits these main Markdown changes. Only this notes file
and the ship file change after the dry run. The gate-run comment is the ship
file's first line; its marker is dropped last in a separate commit.

PW5 also has a native format-60 pair at checks/ via Format60SaveFixture. Per the
orchestrator's instruction RES2 keeps its already built/reviewed shared HC1
pair. Whichever branch ships second consolidates those artifacts and hooks;
there is no new deferred design decision or queue row here.
