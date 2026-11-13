# HA1 notes ? the gate's harnesses

Started from origin/main `5a87e102` on 2026-11-13. HA1 is **no tag
(process)**, confirmed by lanekeeper's cbs-bus instruction
`20261113-112358-677-lanekeeper-880b`.

## Built and decisions

- ? Keep every moved assertion and its original fixture, in scoped helpers in
  LongPlaytest, SaveFileCheck, SectorStatementCheck and TruckRouteCheck.
  RestructureCheck 5b?5d now belongs to BuybackCheck. No duplicate labels were
  dropped in these five merges, so there are no dropped-label/twin pairs.
- ? Keep current-format land allocation for AW2. Delete the unreachable old
  load branches and GridConversion, LandConversion, LegacyLand and
  ConversionCheck. `GameVersion.java:8316`, `OLDEST_LOADABLE_FORMAT = 51` already refuses
  both formats those converters served (30 and 31); SAVE_FORMAT is also 51.
  `Game.refusal`, line 20380 before the load, calls
  `GameVersion.isNoLongerReadable(loaded.getSaveFormat())` and returns without
  applying the save; `GameVersion.java:8349` compares it with the floor.
- The dependency audit found live helpers: CityLand's plotted-area counts,
  PartField and redraw fixtures, LandSellers' class-to-area counts,
  EarthTerrainCheck's class checks, and LandCheck's drawing, site and distance
  helpers. Their algorithms survive in LandDrawing and CityLand.PartField.
  `setOwnedSqFt` retains its drawing behavior; normal load no longer converts.
- ? A soft skip belongs only around clock-only work and its timing fixture.
  Mixed sections keep correctness work. Hard/nightly runs retain the rounds
  and budgets. SaveSlotCheck's autosave correctness checks keep running.
- The registry has one 15-harness nightly list. ALL excludes that tier; a
  direct pick still includes its named members, even alongside ALL. Longest
  first uses local times.tsv, then the last completed gate's retained times,
  then the refreshed median order. Malformed/nonfinite times are ignored.
- The five explicit observation assertions in brief item 7 (called ?six? in
  its heading) are removed: the mean-index gap, two pass-through run shares,
  the building count and foreign-account array length. The material-price
  identity and OrderSearchCheck's fuzz remain.
- QUEUE.md is publisher-owned. Lanekeeper confirms the publisher removes only HA1's own row and will not
  make a queue-only commit. The CB1-L row is therefore left for Jerus below.

## Checks

Initial `./batch-check.sh --fast`: exit 0, compile 41s; Stale 0 firm.
The requested pre-change measurement runs only the 18 timing harnesses,
soft, on the original compiled classes: **1906 seconds**, 15 passed and
3 with known reds (MapCheck, PlanCheck, KeptCheck); 14 soft timing misses.
This is a timing-harness measurement, not a baseline full suite.
The same 18 harnesses after the soft skips took **2058 seconds**, again only
known reds in MapCheck, PlanCheck and KeptCheck. EarthTerrainCheck fell from
108.4 to 57.0 seconds and PartnerCallsCheck from 5.7 to 3.9; MapCheck and
KeptCheck were slower on this shared PC. These unpaired, soft suite figures
do not establish a total speedup. No budget was changed.

| Timing harness | Before seconds | After seconds |
|---|---:|---:|
| BridgeCheck | 25.4 | 22.1 |
| EarthCheck | 2.0 | 1.9 |
| EarthTerrainCheck | 108.4 | 57.0 |
| GlobeCheck | 219.0 | 203.9 |
| GridCheck | 1.6 | 1.3 |
| IslandNetCheck | 19.5 | 22.9 |
| KeptCheck | 847.4 | 1014.5 |
| LandNationsCheck | 20.5 | 17.7 |
| MapCheck | 428.5 | 487.8 |
| PartnerCallsCheck | 5.7 | 3.9 |
| PlanCheck | 65.9 | 64.4 |
| PortCheck | 7.7 | 8.2 |
| QuayCraneCheck | 0.7 | 0.6 |
| ReplayCheck | 59.4 | 63.7 |
| SaveSlotCheck | 38.4 | 34.5 |
| ScaleCheck | 16.1 | 13.6 |
| WorldCheck | 18.8 | 18.8 |
| WorldMarketCheck | 20.1 | 20.1 |

Before logs: `target/checks/ha1-timing-before.txt` and
`target/checks/ha1-times-before.tsv`. After logs:
`target/checks/ha1-timing-after.txt`, `target/checks/ha1-times-after.tsv`;
KeptCheck's after log is also preserved as `target/checks/ha1-kept-after.log`.
WorldCheck, ScaleCheck, SaveSlotCheck, IslandNetCheck, BridgeCheck, ReplayCheck
and LandNationsCheck retain their mixed correctness/timing work. Clock-only
sections in the other eleven harnesses skip before their timing rounds.

The original default LongPlaytest passed (exit 0); its model trace is kept
in `target/checks/ha1-playtest-before.txt`, from the original classes copied
to `target/ha1-baseline-classes` before recompiling.

TimingCheck passes its new soft/hard skipped-round fixture, local/gate duration
fallback, exact direct nightly inclusion, and full registry partition checks.
`tools/pick-selftest.sh`: 22/22 passed, including a class named only by
GlobeCheck and the ALL-plus-direct-pick case. `tools/nightly-selftest.sh`
passes the production tier runner with five merges since its last green;
a red preserves that green marker. `--fast` compiled the model/harness changes
and Stale reported 0 firm findings (193 soft).

A mechanical label audit finds every moved literal label in its new owner:
MoneyCheck, ConservationCheck, SectorBooksCheck and TruckCheck have no missing
labels. The serial/bill/yield section is moved intact. A token-stream comparison
also confirms the four whole fixtures are identical after only their scope,
entry-point and exit changes (comments/whitespace ignored).

The publisher selftest passed 136/136 cases, including forced interruptions,
rollback and Ready-flow reuse. The final rerun also passes 136/136 after the selection-metadata
and preceding-publisher fallback changes. Shell syntax checks pass.

The registry has 196 harnesses: the default gate tier runs 181, the nightly
15. HA1 itself directly picks 13 nightly members, so its own full gate runs
194; its elapsed time includes those deliberately picked heavy checks.

Final `./batch-check.sh --pick`: **exit 3**, ALL selected, **194 harnesses
in 1927 seconds (32.1 minutes)**: 187 passed, seven only known reds
(MonetaryCheck, CentralBankCheck, HealthCheck, MapCheck, PlanCheck, KeptCheck,
DeathRecordCheck). Compile 49 seconds; Stale 0 firm, 193 soft. All five merged
fixture groups pass under their new owners. Logs:
`target/checks/ha1-pick-final.txt`, `target/checks/ha1-times-pick.tsv`.

Compared with the brief's historical 38-minute full gate, this working-tree
pick is 32.1 minutes while retaining HA1's 13 directly picked nightly members.
The fresh-clone gate measurement is still pending. Simulating three slots,
longest first, from this run's measured seconds for the 181 ordinary harnesses
gives 1172.5 seconds (19.5 minutes); that is a scheduling estimate, not a
measured default-tier gate.

EN1 before the AW2 merge: the original default LongPlaytest passed, and the final one plus its
moved money fixtures passes. The default-model report prefix is byte-identical
after replacing only runtime figures on two lines: the playtest headline and
CL3's window-duration suffix. Every model count, financial figure and finding
is unchanged. The extra moved-fixture report begins at
`--- HA1 money audit fixtures ---`. No ensemble is needed: HA1 moves no default
model trace. The exact prefix diff is
`target/checks/ha1-playtest-prefix.diff`.

Generated maps/indexes are committed. Maps regenerates current sources but
leaves old per-class files behind, so the eight maps for the deleted converter
and harness classes are explicitly removed. The branch is pushed without a
ship file (the no-tag dry-run contract).

## Merge with AW2 and superseded dry run

AW2 shipped first as `b8d4750d5e7882f7590ecdc905a2a83ab8525334`
(version 0.9.99). The first HA1 launcher run,
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-HA1-dI1YmXFZ`, had branch
`fb2c1bf8605d16004ccc2f8508cda6342c440232` and main
`5a87e102142ce5b3641b514f7ba1edf2ea775a4f`. It was stopped with the
launcher's own `--stop` at the main advance (exit 130); it is **not reused**.
Only that run's process tree was ended. No ship file had been published.

Merged origin/main as instructed by lanekeeper message
`20261113-131735-002-lanekeeper-926f`. The source merged cleanly, including
AW2's saved allocated-land and footprint fields, load delta, normal-load
SaveFileCheck fixtures and LongPlaytest's measurement. Eight generated index
conflicts took main's version before regeneration. `./batch-check.sh --fast`
then exits 0: compile 44s, Stale 0 firm and 185 soft.

The earlier full pick and 18-harness before/after measurement are retained
as measurements on 5a87e102. A new fresh-clone gate on AW2's main is required
and will be the readiness evidence. The final merged model's LongPlaytest exits 0. All eight CSV traces are
**byte-identical** to AW2's recorded final main-equivalent traces (which AW2
also matched to exact 5a87e102). The baseline was copied into HA1's target
before comparison so it survives AW2 worktree cleanup. No ensemble is
required against the unchanged `docs/ensemble/0.9.98.md` baseline.

Run: `target/ha1-final-traces.sh`; output:
`target/checks/ha1-final-trace.log`; comparisons and hashes:
`target/checks/ha1-final-traces-result.txt`. Baselines are
`target/checks/ha1-baseline-aw2-*.csv`, final traces `ha1-final-*.csv`.

| Trace | SHA-256 (baseline and final identical) |
|---|---|
| bank | `b6270c8e852d7a36a69100f43a5c9fa8ec3a22be7318c69e727452cd48c59604` |
| bonds | `086c974c92161a39c0740b5f3bd7d7dbd409df0f693d9f8b692af6e283424f2b` |
| borrow | `70d395e9ae0960e323cc3387c087f28f536de21b8369427997f42dca73357da4` |
| build | `fd5dd6fd814ea9512b3d5220ddd5ae73852f2a52af385c74ddf6c299997560a1` |
| house | `8247d157386c9a1170908e86945756a8ddef9976bd6412425db967f9853bf77d` |
| labour | `58253f59ab6b132da63e66a68b7d8b4e4060f9385a8710a75a6ddcb7803ca8b3` |
| month | `ae2f1905f699d2805b535bf4c189568bc085e33a09874531e0888f93750c166b` |
| pop | `0429d2c39085493bcdf7a436e969224ef90cbc1516e44c41aa4b057b686e1fc3` |

The one-in/one-out count is unchanged after the merge: +2378/-2871, net
-493 across 44 process source/script files, excluding generated indexes and
HA1's own records. Protected publisher files have no HA1 diff from main.

In progress: push this merged branch and run the fresh no-tag gate on
`b8d4750d5e7882f7590ecdc905a2a83ab8525334`.



## Pending for the gate box

SaveSlotCheck CLOCK_MS re-measurement remains unchanged until the quiet gate
box arrives November 14?15. No other speed budget changes. An actual nightly
on main with the new tier can run only after publication; production-tier
selection and red merge attribution are tested with the five-merge selftest.

## Left for Jerus

Remove the obsolete CB1-L Pending row from QUEUE.md: its longest-first order
is implemented in HA1. QUEUE.md stays untouched under the batch contract;
lanekeeper confirmed this disposition in cbs-bus message
`20261113-113113-465-lanekeeper-f187`.

## One in, one out

Process source and scripts (the `*Check.java` files, LongPlaytest, AllChecks,
Timing, and changed `*.sh`; generated maps, these notes and the ship file
excluded): **2378 lines added, 2871 removed, net -493** across 44 files.
The obsolete 702-line ConversionCheck leaves the machinery, while the four
whole moved fixtures retain their token streams. The brief's narrower runner
and routing files, from `git diff --numstat origin/main`, are:

```text
3	2	ship.sh
45	14	src/main/java/ham/citybuildersim/AllChecks.java
23	0	tools/nightly-timing.sh
5	4	tools/nightly.sh
8	2	tools/pick.sh
```

Their additions are offset by the deleted obsolete harness and the trimmed,
obsolete gate-timing history in Timing. CB1-L's protected queue row is left
for Jerus, as recorded above.

## Stranger review repairs (2026-11-13)

Lanekeeper's `20261113-134203-110-lanekeeper-a409` arrived before readiness.
No ship file existed. Stopped only the active launcher run
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-HA1-drVJGLrb` with its `--stop`
(exit 130). It held branch `0f8152fc58aba840054f20dac1ba72a259f5f198`
and main `b8d4750d5e7882f7590ecdc905a2a83ab8525334`; it will not be reused.

1. **Correctness inside timing skips:** narrowed the shallow-sea skip to the
   tile timing rounds; generated-world D19, the four shallow-share comparisons
   and the earth-1 checks now run soft too. KeptCheck's section 9 forget and
   nested-pool checks are outside the section 10 measurement guard. Audited
   every other skip: whole skipped methods hold only a clock and its timing
   fixture; mixed sections retain their laws. The earlier after-times for
   EarthTerrainCheck/KeptCheck are superseded and will be replaced below.
2. **Nightly known reds:** the tier now invokes `batch-check.sh --judge` on its
   log, the same main gate judge, accepting 0/3 and failing on NEW or incomplete
   runs. Selftests cause raw known-red exits with judged 3, NEW 4, and incomplete
   1, checking green-marker advancement and preservation of attribution.
3. **Current-format fallbacks:** restored the plot-mismatch redraw through
   LandDrawing, unchanged in behavior. ? A missing/wrong-width current centre
   is refused before rebuild rather than guessing a city from a damaged file.
   CityLand and Game share the width predicate; SaveFileCheck causes both
   missing and wrong-width centres through normal and direct load, checks the
   played city and file unchanged, and causes a current-format figure mismatch
   to prove its actual ground redraws. AW2's allocation/footprint delta remains.
4. **Unrun harness records:** `gate-record.awk` retains old reds of every harness
   absent from the completed `times.tsv`, plus the existing unseen-Earth rule.
   The publisher selftest exercises an unrun KeptCheck red, a run-green record
   cleared, unseen Globe coverage and an empty run list. The fake runner now
   emits its actual run names as the production runner does.
5. **Truck's tier:** ? Follow the brief's two explicit instructions together:
   merge TruckCheck into TruckRouteCheck, and put TruckRouteCheck in the nightly
   tier. Its 67 assertion lines remain there word for word, run at the gate
   whenever directly picked and every night otherwise. Its merged source names
   the original truck classes, so their under-fanout diffs directly pick the
   owner; no separate default-tier TruckCheck or exception list is added.
6. **Independent sides and dead resource:** GridCheck uses worked coordinate
   cases at axes, diagonals and either side of a tie, rather than reproducing
   the function under test. Removed unreferenced `conversion-saves.json`.

Focused post-review `batch-check.sh TimingCheck SaveFileCheck GridCheck
EarthTerrainCheck` exits **0**: four pass, compile 41s, Stale 0 firm/185 soft.
The soft EarthTerrainCheck log explicitly contains passing D19, all four full
shallow-share comparisons and the earth-1 no-SHALLOW assertion. SaveFileCheck
prints passing current-format redraw plus missing/wrong-width refusal through
both load entry points, with each file unchanged. The common-judge nightly
selftest passes known=3, NEW=4 and incomplete=1 cases.

Repeating the 18-harness serial soft measurement on the corrected code;
publisher record selftest and final CSV verification are also running. Then
regenerate indexes and make a new retained full dry run. The two interrupted
runs are not readiness evidence.

