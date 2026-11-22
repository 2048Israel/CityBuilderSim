# RES2 — aggregate housing inventory

Built from 52c79806b (0.9.187), integrated with shipped RS1/main 01e6c2d4b
(0.9.188) in model commit 32c81f469. Final model verification is against that
newest main. No new design call remains for Jerus.

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
and quarter occupied home, changes RE's capacity baseline from 45 to 42.75 and
saves a stranded share of .05. Reload restores those exact counts; both first
months count 45 and dilute the share to .0475. Existing ReloadCheck 3k also
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
in an isolated detached reference checkout. Merged model 32c81f469 ran the same
default with the same JDK, seed/options and required Earth/world files. Every one
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

Model source is final at 32c81f469; the final records/maps commit changes prose
only. A stranger's saves review is owed. No money transfer or new thread is
activated by the batch; the allocation and quantity contracts remain reviewable.
The provisional ship marker stays until that review's SHIP and the ALL dry run.
No dry run has been run yet, so no retained run or two gate commits are claimed.
