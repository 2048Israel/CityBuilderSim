# CM1b - the replay loaded move, the review follow-ups

Built from CM2 main `2853630b` on `batch/CM1b`. Read CM2's notes and `git log -p 8a67da4d..2853630b -- src` before editing. No known-reds row names CM1b at this starting main; ReplayCheck's historical negative-ask entry belongs to GR2-R. LD3's named files, including Game.java, are untouched. No save slot was written; all cities and windows use scratch folders.

## Decisions and implementation

★ Keep CM2's synchronous `MapView.forget()` release. It already fixes leaving replay while Ready; CM1b adds synchronous cancellation through the expanded map while its reference still exists, and before collapse detaches it. Esc and X while Ready release the move, copies, pins and prepared contexts immediately. A load cancelled or losing its map restores its starting month and view; an in-flight move stops where it stands. Completion still releases after the final frame is tallied, retaining CM2's zero-eviction law.

★ Cap the whole load at `MapView.LOAD_MAX_S = 120`, including busy time. CM2 measured successful loads above the old eight-second warm-up limit (up to 34.2 seconds at 1920 x 1080); 120 seconds leaves room for those loads while bounding failure. The refusal names the dial on the replay card and restores the starting view. Keep the separate two-second idle give-up for a view. Remember district pictures and all missing visible cell types, including cells never asked for. Failed worker jobs are recorded on FX and suppressed for this move.

★ Preserve `LOAD_HEAP_SHARE = 0.8` as a share of the heap available when the load starts, rather than the whole maximum heap. Check against that fixed per-load budget, and collect at most once across its views. A deterministic sampler fixture covers a city already over the old threshold, growth at and above the budget, and repeated excessive samples without allocating most of the test JVM's heap.

All keys except Escape are consumed while loading, before text/modifier and ordinary replay handling. Ready keeps its click/any-key trigger, with X taking its documented stop action. ReplayMoveProbe.check deliberately re-enters replay at the end so its finally must turn replay off and explicitly stop both registered pumps before section 6's timing begins.

## Verification in progress

The first fast check passed, exit 0 (compile, maps, Stale 0 firm). Two initial focused runs exposed fixture setup mistakes (reflecting inherited Map.clear as a declared method, and scanning after a synthetic month change without first adopting its map). Fixed the fixtures; these were not model findings. Those exception exits left JavaFX alive, so only the verified owned ReplayCheck process trees (native parent PIDs 43840 and 36048) were ended by PID. Other sessions and MemWatch were untouched.

Final named checks, regression demonstrations, default trace comparison and serialized gate evidence follow below.

## Final named replay check

Source mends are `9e2c10d7` (implementation) and `a0a53660` (fixtures and provisional ship file). `./batch-check.sh ReplayCheck` passed, exit 0: one harness, zero failures, Stale 0 firm. Kept output: `target/checks/CM1b-named-final.txt` and `CM1b-ReplayCheck-final.log`. The zoom-out loaded 481 views in 8.4 s, none passed, then drew 50 frames over L0/L1/FAR; across months loaded the same 481 views in 0.7 s and drew 41 frames. Both had zero unpainted frames, worker asks, evictions and off-plan frames, released their stores afterwards, and retained two distinct land contexts across the real purchase. All review follow-up assertions pass, including unasked FAR and globe-chart cells, and cleanup before section 6.

The CM1 forgetting behavior was restored by removing only CM2's synchronous `endMove()` line in a temporary compiled MapView, leaving the current fixtures and other behavior intact. In the same FX call after leave it reports `heldAfter=true, pinnedAfter=6, preparedAfter=2`, exit 1 (`target/CM1b-mutants/cm1-forget/run.log`). This demonstrates the half CM2 already covers; CM1b does not duplicate that repair. Temporary mutant classes precede ordinary classes only in their diagnostic JVMs, and are never shipped.

★ Failure callbacks capture the move's serial, not its MoveLoad object: a queued worker must not extend the pictures/store lifetime after cancellation. Adoption and changes to the remembered failures stay on FX. No new worker reads a live Game.

★ Cleanup also detaches the harness scene root and stops its ordinary UI clock. A late worker adoption calls MapView.requestDraw(); a canvas left attached to a closed stage could restart the just-stopped pump. The finally fixture explicitly makes that late request and requires both nodes detached and both pumps still unregistered. This is harness cleanup only.

## Original-behavior regression demonstrations

All ten focused variants exited 1 with their expected finding, and the mended driver exited 0. Summary: `target/checks/CM1b-regressions.txt`; individual compiled source, compile log and window output: `target/CM1b-mutants/CASE/`. These are minimal reversions of the old behavior in current code, so CM2's independent prepared-context and geometry changes remain present.

| Variant | Finding |
|---|---|
| cm1-forget | Removing CM2's one synchronous release line leaves the move, all six pins and both prepared months held immediately after leave. |
| ready-stop | Removing the synchronous camera-to-map cancellation fails Esc and X while Ready, and load-time map loss. |
| stalls | Restoring the old remembered kinds fails the unasked tile/district-picture, FAR-node and globe-chart fixtures. |
| failed-job | Omitting FX adoption of the failed node fails the given-up/no-retry assertion after a real worker job throws. |
| cap | Removing the overall elapsed-time check fails while a real worker task is held busy. |
| heap-fixed | Restoring `LOAD_HEAP_SHARE * maxHeap` fails the city already above that threshold. |
| heap-gc | Restoring collection on every excessive sample fails both repeated-overflow and recovery-then-overflow fixtures. |
| keys | Restoring the old handler changes the half-view's marks, month or camera instead of consuming its keys. |
| mapgone | Restoring collapse without notifying the camera fails immediate starting-view/month recovery. |
| cleanup | Restoring timer/close-only cleanup leaves replay or pumps active and fails the cleanup assertion. |

The heap fixture derives its above-threshold starting use from `LOAD_HEAP_SHARE`, rather than pinning the old percentage in its premise. Each fixture restores its state explicitly after sampling, including with the broken behavior, so failures do not cascade into another fixture. This batch changes no process scripts or suite framework; one-in, one-out does not apply.

## Review, trace and film

REV-CM1b-1 reviewed `2853630b..a0a53660` and returned **SHIP**: the five follow-ups, callback guards, ordinary-game branches and caused fixtures all hold. The orchestrator separately accepted the probe-only `6cb75a1e` and `84a836dd` follow-ups (mail `20261114-234557-269`). No game-code changes follow `a0a53660`.

The final probe driver with detached-scene cleanup and the dial-derived heap premise passes all twelve review assertions, exit 0 (`target/checks/CM1b-final-probe.txt`). Removing only scene-root detachment makes the late worker draw re-register the map pump and fails cleanup, exit 1 (`CM1b-late-callback.txt`, `target/CM1b-mutants/late-callback/run.log`). Together with the earlier ten variants this covers each original review finding and the cleanup's asynchronous edge.

The default trace pair is against the starting CM2 main `2853630b`, compiled from an untouched source archive under `target/CM1b-main`. All **eight CSV files are byte-identical**. SHA-256 evidence: `target/checks/CM1b-trace-compare.txt`. After excluding only the startup option line, run elapsed time, layout-watch elapsed time and two narration clocks, all **523 report lines** also match (`CM1b-report-compare.json`, normalized hash `7de4abe436793d9854768744f103ba8cb64ad5c89f606a65bb7695292fc70ead`). Both runs exit 1 for the same registered layout finding: twelve boxes move at m2389; every money fixture and the final money footer pass. No model figure differs. The ensemble is skipped under batch.md section 6. Direct diagnostic console output used Windows' native encoding; those fourteen completed diagnostic logs were explicitly decoded from CP1252 and rewritten as UTF-8 through Git Bash before comparing reports. CSV bytes were not converted or normalized.

The film runs use CM2's unchanged 123-month Andorra recording, `../codex-CM2/target/CM2-recording`, and the exact 1920 x 1080 off-screen canvas. Final `CM1b-film-final.txt`: M3 loads 2,401 views in 23.5 s, draws 204 frames, worst gap 140.5 ms; M6 loads 2,401 views in 17.9 s, draws 316 FAR frames, mean gap 31.8 ms, worst **49.1 ms**. Both reach Ready with no view passed, and both have zero black/coarse/stale/unpainted frames, worker asks, evictions and off-plan frames. Paired CM2-main M6 (`CM1b-film-main.txt`) loads in 18.8 s, mean 31.8 ms, worst **48.7 ms**; CM2's retained final measurement was 49.2 ms. These are functional/performance observations on the shared PC, not a replacement for the nightly timing verdict; no floor or premise is relaxed. Both film JVMs exit 0.

## Incoming LD3 main and final gate preparation

The orchestrator released the serialized dry-run slot after LD3 shipped (mail `20261114-235420-425`). Merged main `c0120d77` (0.9.108) as `5514d367` before final verification. The only conflict was the generated `docs/map/README.md`, resolved by regenerating all indexes from the combined source. LD3's named source files and the publisher-owned version, queue, changelog and gate record match origin/main exactly; CM1b authored no hunk in them. LD3 removes the always-on harness switch and the equivalent true branch in repriceLabour; its own retained gate compares all 523 normalized default-report lines byte-identically to CM2's main. The final merged gate report will also be compared with CM1b's report above.

Final pick and retained normal-route dry-run metadata follow below.

## Final pick

On the merged main, `./batch-check.sh --pick` exits **3**, known reds only (`target/checks/CM1b-pick.txt`, kept suite `CM1b-pick-suite.txt`, replay log `CM1b-ReplayCheck-merged.log`). All seven selected harnesses ran: BuildMenuCheck, ClockCheck, LandCheck, MapCheck, ReplayCheck, TimingCheck and ZoningViewCheck. MapCheck alone is red, for its three registered main findings (drawing tally, road network, road surface). No NEW line appears. ReplayCheck, including the final late-callback cleanup and dial-derived heap fixture, passes. Stale has 0 firm findings. Eight timing assertions are SOFT and two played-path premises OBSERVED under the normal policy. One harness start waited for the standard 75% commit-charge guard. No timing floor or assertion is changed.

The indexes were regenerated after midnight, so their generated date headers advance to November 15 across the map set. The provisional ship file is pushed before the authorized versioned normal-route dry run. Source and generated indexes are frozen from this checkpoint; after that run only these notes and the ship file will advance.

## Retained gate run

Normal route: `sh tools/gate.sh CM1b --dry-run`, authorized by the orchestrator after LD3 shipped. Retained run: **C:/Users/Jerus/AppData/Local/Temp/cbs-gate-CM1b-lA470zVh** (Git Bash `/tmp/cbs-gate-CM1b-lA470zVh`). It judges branch **ff33161622371f1f8a7566b760a499ba75ab44d3** against main **c0120d7747d2671b9a9a78fe25ac22116a555b6c**. The launcher accepted the provisional UTF-8 records, assigned prospective version 0.9.109 in its fresh clone, and started the full suite. No tracked file except these notes and the ship file advances from the gated branch commit. The completed verdict follows below.

The run completed on **2026-11-15 at 00:34:06 local time**: **launcher exit 0, suite exit 3 (known reds only)**. AllChecks ran **187 harnesses in 1,196 s: 184 passed, 3 known red, zero NEW reds**. The whole check step took 1,271 s including compile and generated checks. The three red harnesses are the existing DeathRecordCheck orphan law (OX1), LongPlaytest's registered CL6/no-FAIL verdict and m2389 movement (DM3-R), and MapCheck's three registered tally/network/surface laws (DM3/BF1-M/DM3-R). No known-red transfer, new approval or queued repair is needed.

ReplayCheck passes in the full merge. Its zoom-out loads in 8.0 s and runs 51 frames; the land-change glide loads in 0.7 s and runs 40 frames. Both pass zero views, have zero asks/unpainted frames/evictions/off-plan frames, and release their store and pins. Every follow-up fixture passes, including same-FX Ready cancellation, unasked globe cells, busy cap, incremental heap, loading keys, map-loss recovery and late-callback cleanup. Eight timing assertions are SOFT and four path premises OBSERVED under the normal policy. Stale has 0 firm findings.

The **final merged gate's default report is also byte-identical after clocks only** to CM1b's report above: all 523 normalized lines and hash `7de4abe436793d9854768744f103ba8cb64ad5c89f606a65bb7695292fc70ead` (`target/checks/CM1b-merged-report-compare.json`). This verifies the incoming LD3 main on the final code as well as the eight-file CSV comparison against CM2. No model figure or finding moved, and the ensemble remains skipped.

The retained run contains `ship.log`, `checks/suite.txt`, per-harness `checks/NAME.log`, `checks.complete`, `checks.sha256`, selection and both commit identities. `unnamed.txt` is empty. Remote refs before/after are identical: nothing was published by the dry run. The ship file points at this exact run for reuse. Only these notes and the ship file advance from the gated commit; the provisional marker is removed in the final ship-file-only commit.

During fixture development, a diagnostic initially handed askNodes an immutable list despite its documented sorting, and an idle-timeout fixture depended on unrelated worker scheduling. The list is now mutable. ★ The missing-cell fixture calls the real passView path directly after proving missing cells/requests, so it tests what a given-up view remembers; the separate busy-worker cap fixture causes real worker occupancy. The old-stalls variant still fails all three remembered-kind assertions. These fixture setup corrections do not relax any map or playback law.
