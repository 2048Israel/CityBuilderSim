# FS6: the map sweep (MapCheck, PlanCheck, OrderSearchCheck, BuildingStackCheck, GatewayCheck)

Builder: Claude (Opus 5.5), headless, 2026-11-25. It works on the branch the
Codex builder left on 2026-11-21: e04875784 and 968e7db94 over 9d4467e8a,
about 0.9.180. Its record is kept below.

The frame is `docs/studies/flaky-suite.md` 6.5 and
`flaky-suite/by-harness.md`: each premise is caused by its fixture and
asserted hard, with no waiver. The batch takes BQ1, LM1-R and KS1-rest's
three GT1-T map lines. It changes harness classes only, so
`moves_playtest=no` and no ensemble is owed (section 2).

## 1. The merge

I merged origin/main into batch/FS6; no fresh branch was cut. There were two
merges:
- ff1d69779, of bb155e205 (0.9.207);
- 1b0efd0ba, of 4f6bb2b5c (OA1 and PF1, 0.9.208).

There was one source conflict, in `Observation.HARNESSES`. Main had dropped
KeptCheck, BankCheck and CapitalFlowCheck, and the branch had dropped
MapCheck. Both removals are kept, since MapCheck has no Observation call
left. The generated indexes were taken from main and regenerated. 644 of the
regenerated maps changed only in their date line (main's say 11-26); those
were restored, not committed.

## 2. Re-measured on 0.9.207 and 0.9.208

| Harness | 0.9.207 (ff1d69779) | final (0.9.208 merge) |
|---|---|---|
| MapCheck | 3 FAILED: 2c's S4; 1's JUNCTION_APART; 8d's "every building drawn" | 1 FAILED: 1's JUNCTION_APART (PX2's, section 5) |
| PlanCheck | 2 FAILED: (g) crescents (PW2-B's row); 17's "every district ... keeps every check" | the same two |
| OrderSearchCheck, BuildingStackCheck, GatewayCheck | green | green |
| ObservationCheck (the registry) | not run | green |

The Codex builder's findings, as they stand now:
- The four primaries it found green are still green.
- The S4 red stood, with the same figures: 169/496 against 1345/1771, and
  the same open count with the streets off. Neither of its two static
  confounds was the cause: all 496 plots were plan-eligible, and the
  streets-off replay is not what differs. The cause is in section 3.
- The mine raster red stood, with the same figures: 1,805 of 1,854 drawn and
  49 with no ground. Its reading that the fixture's inventory was at fault is
  confirmed (section 4).
- Two things it worked on are now green, and their FS6 rows retire:
  - 2h's reserve premise (the known-reds rows "the highways reaching a
    street");
  - section 8's one network on its copy.
- PlanCheck 17 is now red on the + floor alone. So is MapCheck 1's
  JUNCTION_APART, which is R1g's row, queued to FS6 on 11-25. Both are PX2's
  (section 5).
- BQ1: Map 14's card bond and note are covered by the bank's own capital
  injection, and the section is green.

The default trace:
- Against origin/main, the diff in `src/` is five `*Check.java` files and
  `Observation.java`. `Observation.java` is a harness registry that only the
  nightly reads.
- No model class changed, so the trace cannot move and no ensemble is owed.
- MapCheck's played city, under R1g's 1900 default, drew the same figures on
  0.9.207 and 0.9.208: 742 road plots and 5 + junctions.

## 3. S4's share law: the sweep's fixture, re-caused (LM1-R)

A helper traced it: a slice, with its diagnostic and output in
`target/FS6-trace-S4/`.

What happened:
1. After the first block, the land's level (`LandGrid.levelFor(ownedPlots)`)
   rose from 3 to 4.
2. The fixture's rule, a wholly unowned whole block of the level, could then
   pick only a 16-aligned second block. That block started at x=144 and left
   an unowned 8-plot gap (x 136-143) between it and the district's streets.
3. `cellsOnGrown` laid cell 12's layout: 154 street plots.
4. `DistrictPlan.pruneApart` took up all 154, because they did not join the
   district's main network. That is its documented design (FX9): a piece the
   ground parts is not grown.
5. So `boughtStreets` stayed at 188 with the streets on, the same as with
   them off.

Reality agrees with the model. A parcel with no road to it is not built up.
Subdivision rules require a building lot to front a street, and a landlocked
parcel can be used only through an easement of necessity across a
neighbour's land. The law's real premise is therefore ground bought beside
the district's streets. The law is restated to that, and the effect the old
fixture hit gets a hard assertion of its own.

★ The purchase follows the game's market. `LandMarket` offers and prices a
draft by its unowned plots (about lines 1362 and 1407), and skips a block
only when none of it is unowned. So in the fixture now:
- both blocks are whole blocks of the level, and may hold plots the city
  owns already;
- every count is taken over the plots that were unowned when the block was
  chosen;
- each block's open-cell ground must lie beside the standing streets, a new
  hard fixture check.

Rejected alternatives:
- An 8-aligned second block at level 4: the game cannot sell it.
- A 16-aligned first block: the trace found its streets became an island at
  m130, and the first-block law fell to 0/496.

The figures (frame-local):

| | Rectangle | Unowned plots | Owned already |
|---|---|---|---|
| b1 | [32,96) x [40,104) | 3,776 | 320 |
| b2 (level 3) | [24,88) x [48,112) | 960 | 3,136 |

| Share | Streets on | Streets off |
|---|---|---|
| S3 | 1,234/1,535 against 1,304/1,824 | 699/1,535 against 1,360/1,824 |
| S4 | 287/389 against 217/465 | 179/389 against 301/465 |

- The S4 streets-off case misses the 3/4 share narrowly, 0.460 against
  0.485. The fixture is deterministic.
- Arrivals now reach the closed cells: 217 of their 465 plots are built on.
  The share law checks something at last, which LM1-R asked for.

The control is planned and not bought. It is the nearest wholly unowned
block whose open-cell ground touches none of the streets, 56 plots across:
once b1 moved, no 64-plot block of that kind exists, and the label says so.
All 232 of the street plots laid on it are taken up, and `boughtStreets`
stays at 388, S3's figure.

## 4. The mine copy's draw identity (8d): the sweep's fixture, re-caused

A helper traced it, with its output in `target/FS6-trace-mine/`.

`CityMap.packAtEdge` (about line 4541) packs a building only within
`LAID_MOST + 1` plots of a plan street. The old copy held a house on every
owned dry plot plus Jerus's whole road inventory: 1,853 houses and 19,858
road plots on 1,853 owned dry plots. That is 11.7 plots of building and road
to each plot owned. By comparison:
- the played city has 0.39;
- the worst any game path has reached is 1.06, FB5's AQ1 city.

`AutoBuilder.groundFor` and `overFull` keep a city's allocated ground at or
under what it owns, and `Game.groundUse` counts a road plot as ground. No
game city holds the old copy. Every real building stands on its city's
ground, so the law stays as it is.

★ The copy follows the game's ground rule. Its road is the played city's
road per building, for two reasons: no model function decides road from
buildings (`BuildAdvice.roadsOver` reads traffic), and the fixture town has
no road of its own. It then takes houses until the model's ground use (each
type's `sqFt()`) reaches the owned ground, and no further. That is a new
hard fixture check: 900 houses and 1,058 road plots use 17,950,000 of
17,950,973 sq ft, and one more house would not fit. I rejected the trace's
road at 5% of `JERUS_COUNTS`, which is a tuned number.

The draw identity is now asserted before the birth as well as after:

| | Drawn | With no ground | Past owned ground |
|---|---|---|---|
| Before the birth | 900 of 900 | 0 | 0 |
| After the birth | 901 of 901 | 0 | 0 |

The mine is still born on plots its plans had streets on: born 36, with 11
street plots before and 11 after. The pixel law holds at 3 and 6 px.

## 5. The + floor: PX2's condition, named and left to its row

Two lines read MapCheck 1's played city at m120/m121, district (0, 0):
- MapCheck 1's "no two + junctions ... nearer than JUNCTION_APART" finds 5 +
  junctions, 1 pair of them nearer than 8.
- PlanCheck 17's "every district as the map draws it keeps every check"
  finds the + floor kept in 1 of 2 districts (closest + 3), with every other
  check kept.

`LATTICE` = `JUNCTION_APART` = 8, so both are the district plan's + floor.
That is PX2's row: "find the pair and why `floorBought()` leaves it, mend it
in `DistrictPlan`". Flaky-suite 6.5 had already named PX2 as Plan 17's
played `allHold` fault, and both lines are in main's `gate.txt`.

The ship file passes both lines with known-reds rows naming PX2. PX2's Done
line names only PlanCheck 6, which is green on 0.9.208. Whether PX2's row
should also name Map 1 and Plan 17 is the orchestrator's call for the
queue.

## 6. Known reds

ship.sh drops every row that names FS6, so these retire at the ship:
- the three GT1-T map timing rows: they run through `Timing`, have been soft
  at a gate since GR1, and this sweep retires them (flaky-suite 6.5);
- 2h's two reserve rows, now green;
- section 8's copy rows, its network and its draw, now green;
- Plan 17's row and Map 1's junction row (R1g's), which return in the ship
  file as rows naming PX2.

PW2-B's crescent row and PX2's two PlanCheck 6 rows are untouched.

## 7. Checks

Every run used main's data files, one harness per `batch-check.sh` call.

After the first merge (logs in `target/FS6-r1/`):
- `--fast` exited 0.
- MapCheck and PlanCheck exited 4, with the reds in section 2's table.
- OrderSearchCheck, BuildingStackCheck and GatewayCheck exited 0.

After each mend, MapCheck had 2c all OK, then 8d all OK.

After the second merge, at c6b7368d3 (logs in `target/FS6-r2/`):
- `--fast` exited 0.
- MapCheck had 1 FAILED, PX2's line.
- PlanCheck had 2 FAILED, PX2's line and PW2-B's.
- OrderSearchCheck, BuildingStackCheck, GatewayCheck and ObservationCheck
  exited 0.

One slip: on 2026-11-25 at about 20:3x, a stray `java ... AllChecks` with no
arguments started the full suite. I stopped it within seconds and confirmed
no JVM of this worktree was left running.

## 8. Scope, pick and review

The pick and review line:
- `sh tools/pick.sh origin/main HEAD` picks 38 of 257 harnesses. That is not
  ALL, so no dry run is owed (contract 5.5).
- Its review line reads "not needed - no money, saves or threading in the
  diff". Whether a stranger reviews the restated laws and fixtures is the
  orchestrator's call, per the brief.

There were four slice helpers, one at a time: two traces and two mends. I
reviewed both mends' diffs as a stranger's:
- 2c:
  - `Bought` carries its unowned-plot mask;
  - `nearestBlock`, `joinsStreets` and `markBought` are new;
  - `soundBlock` reads only the unowned plots;
  - the spill block keeps its wholly-unowned rule, now checked explicitly;
  - none of these has a caller outside MapCheck.
- 8d:
  - the inventory comes from the ground rule;
  - `drawnOneForOne` was extracted from `networkOn`, and both draw checks
    call it.

## The Codex builder's record, 2026-11-21 (at 968e7db94), kept as it was

What it was held on, S4 and the mine raster, is settled in sections 3 and 4
above. Its "no further execution", "held" and "awaiting ordering" lines date
from that day.

### Section decisions

The accepted inventory is flaky-suite.md sections 5 and 6.5. KEEP laws remain
hard. No junction spacing, identity, surface law or accounting allowance is
relaxed; PX2's seven plots against LATTICE eight remains its finding.

| Section | Fixture or retained identity | Required proof (actual results below) |
|---|---|---|
| Map 1, 2, 6 | Original played map/model, ground and movement identities retained; 1 already buys iron through makeRoom and orders the mine | Monthly counts and drawn samples; no main replay |
| Map 2c | Both purchases selected on dry interiors in open and closed cells; S4 receives enough houses to fill the added room | Hard open/closed occupation counts and unchanged 3/4 law; streetsOnBought off must fail both shares |
| Map 2h | Both reserve scenarios start on founded ground; guard gets its own bought iron and a mine | None-before reserve, event-date journal and intersected-box counts; blocked and clear guard boxes |
| Map 2h-c, 8 | Independent makeRoom ground and deliberately dense houses; direct fixture road counts | Actual mine/yard/shore intersections and born street plots; the played raster identity remains |
| Map 4 | Played save/live and next-month identities retained; two directly reconciled house batches cause the dated-log premise | Two-batch sidecar and source plans round-trip |
| Map 7 | Bought-iron scratch town for picks/overlay; founded stacks and funded purchase for draft | Bought/offered/outside plots, iron hover and intervening purchase; existing draft equality laws |
| Map 9 | New Freight with no recorded trade supplies the zero-container case | Native container flow and both rail pictures; opposite case stays in RailCheck 7 |
| Map 11 | Original pixel/plot identities on Squares and played city; new map receives alternate arrival and quiet months | Every moved picture redrawn; every unmoved picture same object; far run/edge pixels remain hard |
| Map 12 | Existing minimap identities retained on all fixtures | Frame, stride, margins and far purchased block |
| Map 14, 14b | Separate founded shelves; native bank capital injection covers bond and note; explicit office updates relist places | Cover, quote equality and issued purchase hard; held picks survive each relist |
| Map 14d-f | Each gets an independent founded frontier; existing hand-made partial blocks and shelves retained | Free three-block chains, complete offers and exact ground/press assertions |
| Map 18 | Founded ground and directly supplied houses for internal purchase; Squares kept/full redraw laws retained | Existing explicit link/carry corruption causes failed lazy job and back-off; natural-band premise removed |
| Plan 6 | Original allHold played audit retained, including the second PX2 label; avenue occurrence asserted on x1/13 | Counts printed on the played path, no occurrence premise there |
| Plan 13, 14 | x1 plus existing own avenue/crescent/shore fixtures | Played half removed from occurrence assertions; existing opposite-switch controls retained |
| Plan 17 | Played allHold/purity/bounds retained; river fixture owns the enclosed-piece premise, field belt in 18 retained | River wall count and all districts' hard identity |
| OrderSearch 1, 2 | All natural orders still audited over 1200+120 months and scaled/twin states; explicit stores/tills cause deep trims/refusal | Native sliceFor versus retained countdown; mortgage grid causes trims/refusals/carry; loaded currency equals source before play |
| OrderSearch 5 | Founded city prices the explicit window order; no 500-month search for prices | Window counts and doubling-only counterexample remain hard |
| BuildingStack L195/L241 | Direct construction finish gives exactly ORDER_HOUSES before the standing save; ordered save remains on site | Known stack count supplies below/above integer ceilings and load identities |
| Gateway coast/save | Final coast counts cause line/routes; full counts before first save cause sea stop and subsequent arm | Every growing coast state checked; 129 live/loaded states after save, including growth and holds |

### Actual primary checks

All used normal `batch-check.sh Name` wrappers sequentially, with main's named
earth-2/world-1 assets, compilation, map regeneration and Stale. No other JVM
was launched beside an owned named run. Every wrapper is terminal. The local
slot was handed to root by bus 20261121-182230-685-FS6codex1-511a, following
root's 20261121-182107-479-orchestrator-b3da. No further execution is authorized.

| Check | Actual result | Preserved evidence under target/FS6-primary-1 |
|---|---|---|
| BuildingStack first | Two new fixture failures: yard stock and its price effect were zero after removing the three simulated months | BuildingStack-wrapper.log, BuildingStack-checks/, BuildingStack-source.java |
| BuildingStack corrected | Wrapper exit 0; compile 38s; Stale zero firm | BuildingStack-recheck-wrapper.log, BuildingStack-recheck-exit.txt, BuildingStack-recheck-checks/ |
| Gateway | Wrapper exit 0; compile 4s; Stale zero firm | Gateway-wrapper.log, Gateway-exit.txt, Gateway-checks/ |
| OrderSearch | Wrapper exit 0; compile 3s; Stale zero firm | OrderSearch-wrapper.log, OrderSearch-exit.txt, OrderSearch-checks/ |
| Plan | Wrapper exit 0; compile 4s; Stale zero firm; one existing Timing assertion soft | Plan-wrapper.log, Plan-exit.txt, Plan-checks/ |
| Map | Wrapper exit 4, harness exit 1: two hard assertions fail; compile 3s; maps regenerated; Stale zero firm; six Timing assertions soft | Map-wrapper.log, Map-exit.txt, Map-checks/ (complete log and suite), Map-source.java |

The sole runtime-code correction after checkpoint e048757845f17f2db83fac1fbcee6c3c1185f6a8
sets BuildingStack's yard stock through the existing setter to one House's
template material (10 units). No month is simulated to find it. Its price
effect is now 5.481204032897949. Exactly 40 Houses finish before the standing
save; the scaled stack is 2,147,483,720, the later reload 2,147,483,748, with
the ordered save retaining the unfinished-count checks. Both earlier fixture
failures remain preserved; no production or law change mended them.

Earlier build history also remains: initial fast exit 0/compile 42s; an edited
fast failed after obsolete picture-fixture prose removal swept two adjacent
constants. Both were restored unchanged (PICTURE_SIGMAS=4 and
NODE_ROUNDS=TIMED_ROUNDS). Corrected fast exit 0/compile 39s/maps/Stale zero
firm. Subsequent named wrappers compiled final source. A scratch encoding
mistake and extra generated blank-line whitespace were restored before the
primary snapshot; no unrelated string or allowance change remains.

### Actual firing counts and row disposition

- Gateway: 61 of 120 states had world lines and routes, zero bad states.
  Full counts at the first saved state caused the gate arm to wait at sea
  and three later spine-order arms to be laid. All 129 live/loaded states
  (months 2-130) match, including byte equality and stoppedBy; FORMAT 12 is
  still refused. Both sea-stop premises are hard green.
- OrderSearch: 1,510 natural interest orders and 475 mortgages in the long
  audit; 720 interest refusals, 153 mortgage trims, 282 mortgage refusals.
  The explicit 64-store order on the old-rule twin returns 0/16/32 for
  tills of 0/25/50% of cost. The mortgage grid has 105,840 cases, 22,209
  trims and 13,650 carry effects. The founded window city causes 618 windows
  in 816 orders; doubling alone misses 5,176 of 6,867 independent windows.
  All retained countdown equalities and desk-call bounds are hard green.
- Plan: original played allHold checks pass, including both PX2 labels.
  Played avenues: 239 plots/8 open cells; x1: 963 avenue plots/15 open cells
  and two cut-street curves. The independently caused river case has one
  walled district. Plan 17's FS6-owned allHold line is hard green. PX2's
  seven-against-eight finding and its owner rows remain untouched.
- Map reserves: the all-ground case has zero reserve before month 131, then
  324 plots, all 324 journalled there; exactly six intersected boxes move,
  versus 15 with the reserve undated. The native guard asks 121 boxes about
  one mine: four blocked, 117 clear, zero local/list disagreements. Both
  FS6 reserve rows are hard green. The block-left scenario is also green.
- Map actual source transitions: mine, yard and shore all cause boxes on
  their future ground; all event-date/movement laws pass. Two directly
  reconciled house batches round-trip with their source plans. Bought,
  offered and outside picks and the iron hover all fire. No played map,
  save, pixel, movement, accounting or source-journal identity was softened.
- Map auction cover: a D$10.0k gap has the 20-year bond (11.0k face/10.7k
  cash), the six-month note, and three offers bought once the bond issues.
  Cover, quote equality and issued purchases are hard green; no Observation.
- Map pictures: 120 constructed months/240 district-months, 61 count changes,
  12 actual redraws and 228 identical kept objects. The internal 32x32
  purchase is at (371764,344320), the frame stays fixed and the land layer
  redraws once for it. Kept/full pixel laws pass. The explicit carry/link
  control causes 93 lazy handouts, 69 failed link takes and 65 noncurrent
  pictures, then 0 lazy/0 failures at the next epoch; back-off laws pass.
- Three inherited GT1-T clock rows are still measured: 5B reconcile 6.5ms
  and 10B 6.2ms against 5ms, FX-side median 35.0ms against 19ms (nine judged
  months). Timing makes these SOFT, no allowance edited. Their correctness
  counts pass; these three gate-waiver rows are vestigial, nightly still
  judges speed. No unchanged main/nightly/ensemble replay was run.
- The mine-raster network FS6 row is hard green (419 road plots/one piece,
  no nearer-than-LATTICE plus pair). Its building-count row remains hard
  RED. The newly reached S4 share is also hard RED. These two assertions
  block mends/readiness; they are not waived or reassigned.

Thus seven of the eight inherited FS6 registry rows have actual hard-green
or vestigial-timing evidence, but the publisher removes every FS6 row and
the remaining owned red still blocks. All other owners' rows are unchanged.

### Static S4 diagnosis; missing evidence stays missing

No diagnostic execution, candidate search, model edit or changed recheck has
been performed after terminal. Source references here are the current snapshot.

| State | Recorded geometry/counts |
|---|---|
| S1, month 130 | 117 Houses, 212 room left; first purchase [371784,371848) x [344360,344424), 3,809 dry plots |
| S2, month 131 | 130 Houses, no S1 box moved |
| S3, month 347 | District full at 2,919 Houses; second purchase [371856,371920) x [344320,344384), 2,436 dry plots; owned 4,293 -> 6,729 |
| S4, month 348 | 4,575 district Houses, all placed; no old box moved; 1,514 new boxes wholly on the second block |

The new arrival call adds 2,436 Houses to the map model (one per purchased
dry plot because the House takes one visual cell), not a searched month or
a count tuned to the inequality. The full district accepts 1,656 of them;
the rest go elsewhere by the native allocation. Both purchases pass the
existing whole-block/unowned/at-least-half-dry predicates, and their dating
and logs pass. This proves occupancy, not comparable buildability.

The district frame is (371712,344320), by CityMap's globally aligned frame
construction and the recorded inside-frame rectangles. At CELL=32 and eight
columns, first-block candidate cells are 10/11/12, 18/19/20, 26/27/28;
second-block candidates are 4/5/6, 12/13/14. Cell 12 is geometrically shared;
the rectangles have an eight-plot horizontal gap. The output does not say
which of those cells was open or its opening month/layout, nor whether the
gap/new pieces are owned, dry, eligible and connected. No such fact is
inferred from an ending map or claimed as a recorded flow.

The first block's hard positive/negative control still fires:
on open 533/667 versus closed 2147/2913; off 233/667 versus 2277/2913.
For S4, on is 169/496 versus 1345/1771; off is the same open 169/496 versus
1439/1771. Both on and off fail the unchanged 3/4 inequality. The on buckets
sum to 1,514, exactly the recorded new boxes wholly on the second block.

Two explicit confounds remain unmeasured:

1. MapCheck.builtOn (1432) counts all after-plan box coverage over RAW DRY
   interior plots, using the before-plan cellKind for its two buckets. It
   does not count eligible owned ground, street/cut bands, sites/fixed
   exclusions, available building slots, or occupancy when the cell opened.
   CityMap.planInputHere (10724; buildableOwnership at 10745) filters owned
   ground through ET5's island rule. Raw purchased dry plots need not be
   plan-eligible; the actual masks at these events were not saved.
2. planWithoutStreetsOnBought disables FX9 for the ENTIRE replay, both
   purchases. Its first-purchase history differs before S4 (2,761 placed
   versus 2,919 with streets). It therefore does not isolate a second-event
   street effect with matched opening/layout/occupancy history.

DistrictPlan.cellsOnGrown (2770) lays the open cell's recorded ring/layout
on new eligible ground only. pruneApart (2864) removes changed streets off
the piece with most standing old streets. Cut joins and floorBought may
also affect the result. Their ACTUAL per-event counts/masks at B2 are absent;
none is declared the cause merely because its path exists in the source.
FX9-notes section 4 proves only first-block S3; its original second block
had zero open plots. Historical scope is evidence, not a waiver.

Before a correction can be justified, a separately authorized fixed diagnostic
would need snapshots at S1/S3 and just after each purchase/arrival: per-cell
kind/layout/direction/boulevard and opening event, raw dry versus eligible
owned masks, old boxes/cuts/streets/fixed/site exclusions, new street/slot
counts, boughtCells/boughtStreets/boughtFloored/pruning and actual new boxes
for both paths. These transient facts cannot be recovered from the saved
aggregate output. No new production fault is yet established.

Proposed causal correction scope FOR ROOT REVIEW ONLY: a separate fixed flat
dated Input, using Plan 12's existing cellsOwned/westOf/boughtOn/Log helpers,
with an adjacent bought half of already open cells AND newly bought closed
cells, a sole purchase and matched starting history. Hard prerequisites:
known open/closed identities; all compared plots eligible/connected; matched
opening/layout facts; new open-cell streets on > 0/off = 0; actual arrivals
in both buckets; unchanged 3/4 plus opposite-off law, movement/accounting and
purity. Existing map purchase/movement/log identities would remain. This
does not authorize replacing the red, changing a metric or searching until
green; the missing evidence and root scope decision come first.

### Static mine-raster diagnosis

MapCheck.mineOnStreets (4346) now buys iron directly. Its one-mine transition
causes 36 new site plots, 12 prior street plots, and 10 painted road plots.
However its 1,853-owned-dry-plot town is given 1,853 Houses and, at 4353, the
ENTIRE JERUS road inventory: 17,710 gravel/2,106 paved/42 highway model plots.
The focused premise and the inventory identity have not been isolated.

The resulting raster has 101 ordinary buildings, two mine-site marks and
1,703 packed boxes; 49 packed boxes have no ground and 481 lie past owned
ground. It draws 1,805 of the model's 1,854 buildings. Street-network, reach,
surface accounting, mine birth and preserved/pixel streets all pass. The
count mismatch equals the 49 boxes with no ground. This is an explicit
fixture-inventory/packing confound, not proof of a new production defect.
The independent actual mine-source-transition fixture in 2h-c passes.

A future authorized correction must use a coherent focused inventory/native
capacity and establish WHOLE model draw both before and after mine birth,
while still hard-causing prior streets under the actual newly mined plots.
It must retain network and count identities. No count/road-size/location
tuning, assertion removal, production edit or waiver has been made now.

### Scope, pick and review floor

The approved Observation token retirement leaves KeptCheck and every other
token untouched (20261121-174818-679-orchestrator-c7ec). It makes main's normal
publisher run the full merge suite. Root acknowledged that consequence; no
publisher/filter change or override is proposed. Pick is not ALL, so contract
5.5 does not require a builder dry run. Remaining picked-borrower/Kept checks
and final root untagged gate are unrun, awaiting ordering. No full pick, full
suite, standalone LongPlaytest, full Kept, ensemble, dry, gate, readiness or
publication was launched. The provisional ship marker remains.

Externally shared playedCity/Squares/playtestCity/rhythm are unchanged. Source
search finds no external caller of the changed fixture methods. The four
primary greens remain usable per root's instruction; they are not replayed.

Current pick: 40/237. Review floor: **not needed — no money, saves or threading
in the diff**. Exact committed pick will be sent with the checkpoint SHA:

```text
AutoBuildCheck BankCheck BondCheck BridgeMapCheck BuildAdviceCheck BuildingStackCheck CapitalFlowCheck ConstructionControlCheck CrossingCheck EarthTerrainCheck FittingCheck GatewayCheck GlobeCheck HealthCheck HousingCheck InvestCheck IslandNetCheck JunctionCheck KeptCheck LandCheck MainLineCheck MapCheck MiningCheck ObservationCheck OrderSearchCheck PartnerCallsCheck PlanCheck PolicyPreviewCheck PortCheck PriceLevelCheck PrizeCheck QuayCraneCheck RailDrawCheck ReloadCheck RunWorksCheck ScaleCheck ShortSellingCheck SlopeCheck TimingCheck TruckRouteCheck
```

Exact `git diff --numstat origin/main`, notes/ship excluded:
**3204 added, 3229 removed**;
source **151 added/175 removed**;
generated indexes **3053 added/3054 removed**.
No consolidation exclusion is claimed. Replaced fixture code, dead fields
and obsolete path history supply removals; KEEP laws remain hard.

```text
226	229	docs/dials.md
1432	1431	docs/harnesses.md
38	37	docs/map/BuildingStackCheck.md
93	93	docs/map/GatewayCheck.md
922	923	docs/map/MapCheck.md
1	1	docs/map/Observation.md
75	74	docs/map/OrderSearchCheck.md
259	259	docs/map/PlanCheck.md
7	7	docs/map/README.md
6	4	src/main/java/ham/citybuildersim/BuildingStackCheck.java
13	10	src/main/java/ham/citybuildersim/GatewayCheck.java
103	105	src/main/java/ham/citybuildersim/MapCheck.java
1	1	src/main/java/ham/citybuildersim/Observation.java
18	42	src/main/java/ham/citybuildersim/OrderSearchCheck.java
10	13	src/main/java/ham/citybuildersim/PlanCheck.java
```
