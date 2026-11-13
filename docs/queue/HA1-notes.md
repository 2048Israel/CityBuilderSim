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

| Timing harness | Before seconds | After seconds |
|---|---:|---:|
| BridgeCheck | 25.4 | pending |
| EarthCheck | 2.0 | pending |
| EarthTerrainCheck | 108.4 | pending |
| GlobeCheck | 219.0 | pending |
| GridCheck | 1.6 | pending |
| IslandNetCheck | 19.5 | pending |
| KeptCheck | 847.4 | pending |
| LandNationsCheck | 20.5 | pending |
| MapCheck | 428.5 | pending |
| PartnerCallsCheck | 5.7 | pending |
| PlanCheck | 65.9 | pending |
| PortCheck | 7.7 | pending |
| QuayCraneCheck | 0.7 | pending |
| ReplayCheck | 59.4 | pending |
| SaveSlotCheck | 38.4 | pending |
| ScaleCheck | 16.1 | pending |
| WorldCheck | 18.8 | pending |
| WorldMarketCheck | 20.1 | pending |

Before logs: `target/checks/ha1-timing-before.txt` and
`target/checks/ha1-times-before.tsv`.

The original default LongPlaytest passed (exit 0); its model trace is kept
in `target/checks/ha1-playtest-before.txt`, from the original classes copied
to `target/ha1-baseline-classes` before recompiling.

In progress: targeted checks, post-change timing, final pick, fresh-clone gate
and the final one-in/one-out line count.

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
