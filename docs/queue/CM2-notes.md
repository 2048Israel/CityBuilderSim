# CM2 ? the replay's loaded move on the globe

Built from main `3ca9a8bc` on `batch/CM2`. QB1's four files are untouched. No save slot or real save directory was opened; the probe founded its own Andorra recording in the initially empty `target/CM2-recording` (months 363?485, 123 months, 1,655 people at the end). All records were written as UTF-8 through Git Bash.

## What changed and why

? The loaded move asks and draws chart nodes across the whole visible disc in GROUND. The ordinary `GlobeFrame.ground` and `chartHolds` APIs delegate with the wider coverage off; MapView enables it only while `move != null` in GROUND. The cached range includes the coverage flag, so an unchanged view cannot reuse the ordinary range on entering a move. The heap guard and its `LOAD_HEAP_SHARE` are unchanged. GlobeCheck renders a wider GROUND fixture both ways and checks complete coverage, agreement with chartHolds, and identical ordinary pixels.

? Retain each distinct month's decoded, painted map for a move across months. Land-change months get a separate whole replay Game during the load, keeping earlier land contexts intact. The run selects the retained Game and its opaque owner-bound map token; ReplayMode points `ui.game` at that context. The token has already forced the lazy sea/rail/land/bridge pictures through `getCityMap()`, so playback neither redecodes the sidecar nor repeats the whole load. Copies share the replay's scratch folder, never the player's. Finish, cancellation, replacement and refusal release the retained-month map with the existing MoveLoad lifetime. Outside a loaded move the replay's original stepping/loading path remains.

Everything here is on the FX thread, including the load and the new context/map ownership. No new background work reads a live Game. Globe bands receive the boolean coverage value and the existing pixel snapshots; existing WORKER jobs and their FX result adoption remain unchanged. Maps retained for an earlier land epoch refer to a context the load no longer reloads. ReplayCheck hides a fixture month file after preparing it, then checks the exact map and context can still be selected, rejects tokens in a different replay or normal Game, releases the move, and proves the ordinary step once again needs its file.

The probe gained `-Dcbs.probe.film=true` for an exact 1920 ? 1080 cinematic canvas off every screen, `-Dcbs.probe.moves=M6` (or comma-separated moves), and `-Dcbs.replay.stepProfile=true` for month read/decode/whole-load/prepared-switch times on stderr with LOAD/RUN boundaries. These are manual probe controls, not player UI.

## Measurements

Before: the unchanged model plus diagnostic timing only, same recording. Initial primary-screen canvas 1492 ? 814 reproduced the CM1 result: M1/M2/M3/M4/M7 coarse totals 40,813 / 37,862 / 29,226 / 28,274 / 29,658. M6 worst gap 222.7 ms. Exact 1920 ? 1080 baseline is `target/checks/CM2-before-1080.txt`, with step diagnostics in the matching `-steps.txt`; M6 worst gap 270.3 ms. Quick steps take roughly 1?4 ms to read/decode; months 459, 473, 474, 475 and 483 fail the land validation and require whole loads again during the run. The slow work is repeated whole loading on land changes, not a slow decode of a valid map.

The machine runs other batches concurrently; these are paired functional/performance probe observations, not an isolated nightly speed verdict. Every window was off screen and closed when its probe JVM exited.

At 1920 ? 1080, before/after (the shipped half-resolution rendering):

| Move | Before coarse | After coarse | After load s | Before / after frames | Before / after worst gap ms |
|---|---:|---:|---:|---:|---:|
| M1 | 82,030 | 0 | 34.2 | 422 / 428 | 111.2 / 130.2 |
| M2 | 90,226 | 0 | 15.7 | 457 / 463 | 89.0 / 92.2 |
| M3 | 71,326 | 0 | 24.2 | 357 / 359 | 99.0 / 95.0 |
| M4 | 72,024 | 0 | 23.0 | 361 / 362 | 91.3 / 89.4 |
| M5 | 0 | 0 | 7.2 | 262 / 294 | 123.2 / 55.5 |
| M6 | 0 | 0 | 23.9 | 563 / 585 | 270.3 / 35.6 |
| M7 | 70,940 | 0 | 24.8 | 348 / 360 | 95.4 / 88.2 |

All seven after moves reached Ready with no view passed. Every after move has zero black, coarse, stale, unpainted frames, worker asks, evictions and off-plan frames. M6's mean gap is 17.1 ms; the retained month switches are around 0.01 ms. The whole loads occur during LOAD, or after the move has ended for the ordinary full-month rest. After logs are `target/checks/CM2-after-half.txt` and `CM2-after-half-steps.txt`. Final figures, resolution choice, checks, trace comparison and the retained gate run are recorded below before readiness.
