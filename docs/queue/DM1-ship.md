## changelog
### {next} — DM1, boxes moving after a reserve: the wall events found, the fix held for a journal — 2026-11-03, ready, see docs/queue/DM1-notes.md
No change a player sees: MapCheck's comment and DM2's brief. DM1 traced FC1's moved boxes (MapCheck 2h, 12 and 23 four
months on) to FW11's way kept clear for a street piece the buildings wall in, which is worked out again from each
month's arrivals, so it drifts (6, 5, 20, 22 plots) and first-fit cascades each change into moved boxes. A planner fix
that dated the way from the replay alone stopped the drift, but moved 40 boxes of a real city in SlopeCheck 9 (m608 to
m609, after a land purchase re-formed the street pieces), so it is not shipped (it stays in the branch history, commit
77e40c87). MapCheck keeps FC1 off through section 2h, its comment now naming the wall events (2d four months on and its
yard that goes, 2h's reserve month) and the drift. DM2, queued, dates the ways in a journal in the map's sidecar.
## pending
| DM1-P | A batch session's `batch-check.sh` builds cities without the Earth and world files, unlike the gate: its MapCheck is red in 16 places on BD1's tip and its 2h shows a drift the gate's city does not have. Name `data/earth-2.bin` and `data/world-1.bin` as `tools/nightly.sh` does (`$MAIN/data/..` when they exist and `JDK_JAVA_OPTIONS` is empty); one in, one out. Recommended: queue it as a process batch | orchestrator | docs/queue/DM1-notes.md ★1 |
## queued
| DM2 | The wall ways dated in a journal in the map's sidecar (`CityMap.FORMAT` bump): the way kept clear for a street piece the buildings wall in stays put as the arrivals come and a wall event moves only the boxes it must. Turn FC1 back on in MapCheck (found by DM1: MapCheck 2h, 2d; SlopeCheck 9's m608-609 jump after a land purchase; LD2-D, the playtest's m3010-3013 moved boxes) | [DM2](docs/queue/DM2.md) |
