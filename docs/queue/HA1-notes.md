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

EN1: the original default LongPlaytest passed, and the final one plus its
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
and will be the readiness evidence. The final eight CSV traces are also
being checked against AW2's recorded final main-equivalent traces.


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
