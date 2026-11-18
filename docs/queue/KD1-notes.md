# KD1 ? suite resource admission and ensemble data

## Built and choices

- ? The per-harness budget is HEAP_MB plus NON_HEAP_MB, 2.5 GiB at the current dials. Free commitment must cover a new full budget and each running child's remaining growth. Windows reads private committed bytes for this suite's child PIDs before reading machine free commitment. Linux uses CommitLimit less Committed_AS and conservatively credits anonymous resident pages. Missing child figures reserve full growth. This addresses MG1's commitment failures while allowing starts with ample free GB.
- ? NON_HEAP_MB is 512. SP1 measured 103 MiB beyond the initial heap. A KD1 EnsembleCheck run with native-memory tracking committed 728,418,890 bytes total and 532,676,608 heap, about 186.7 MiB outside the heap. The allowance leaves growth room for compiler, classes and JavaFX. AVAILABLE_RAM_GB is 4, above the approximately 2.9 GiB left at MG1's 91% RAM incident on this 32 GiB PC. CPU_PCT is 80, below MG1's measured 85?94% contention.
- ? CPU admission averages three two-second samples. Extra starts wait during warmup and unavailable readings. The first harness always starts, retaining forward progress. A held start reports the resource, GB or CPU percent, and the threshold. Counts identify how many starts each resource held, with one count per resource per waiting start.
- ? KnownRedsCheck owns the resource fixtures beside its existing runner checks, preserving AllChecks.HARNESSES for RT2/R0f. Injected readings cause admission and holds at each boundary, running-growth reservation, unknown readings and CPU-window replacement. Fixtures use the dials and never this PC's current state. GF1's process-tree cleanup stays as shipped.
- ? The ensemble discovers the main checkout through git worktree list and passes each available earth/world path as one Java argument. Explicit JAVA_OPTS properties override those defaults. Inherited JDK_JAVA_OPTIONS suppresses discovery. Snapshot provenance records the options, and resuming unfinished work with changed settings requires a fresh label to keep its score coherent. Self-tests include paths containing spaces, inherited settings, missing files and actual --one argument forwarding.
- ? EN2's baseline is docs/ensemble/EN2.md, as its brief specifies. KD1 ships untagged, and the publisher requires a versioned ship file for a same-ID baseline. The baseline keeps its model version in the heading. Runner and EnsembleCheck prefer EN2 over same-version/older literal-world scores until a later versioned baseline exists. CLI fixtures prove both selections.
- Process text was shortened in the runner/check headers, ensemble driver/README and batch checklist. The checklist describes the new resource dials and data-backed baselines. No game model or save code changed.

## Checks before the heavy slot

- Initial batch-check.sh --fast exited 0, compile and generated maps passed, Stale had 0 firm findings.
- batch-check.sh KnownRedsCheck TimingCheck EnsembleCheck with cbs.parallel=1 exited 0, all three passed. The guard fixtures exercise both sides of commit, RAM and CPU thresholds and conservative growth reservation.
- tools/ensemble/ensemble-selftest.sh passed its three CLI tests. Later additions also exercise EN2 baseline selection and paths with spaces through --one. Final rerun results are recorded below.
- EnsembleCheck native-memory measurement exited 0. Its log is target/checks/kd1-native-memory.txt. Other local logs are kd1-fast.txt, kd1-named.txt, kd1-final-fast.txt, kd1-ensemble-check.txt and kd1-ensemble-selftest.txt in target/checks.
- tools/pick.sh selects ALL because AllChecks runs the suite. The launcher dry run will supply the full merge verdict. No entry in known-reds.txt names KD1.

## Before measurement

The orchestrator supplied the existing RT1b dry run, /tmp/cbs-gate-RT1b-ylIvHZQ9, for MG1's before measurement. It tested branch 3f755a84084cdb47226ee0dfef90151c13c58ed0 against main e8ac5a53b1bbc604ce984dbe7b366a69d00e3fff on the preceding runner. checks/suite.txt reports 191 harnesses in 1,420 seconds, 188 passed and three known-red harnesses. No start-hold line was printed. ship.log records 1,483 seconds for the complete check step. Another builder's single JVM ran beside it.

MemWatch history-2026-11-15.csv at 01:26?01:50 gives commitment 41.04?46.24 GiB, mean 43.15, against a 63.26 GiB limit. Free commitment ranged 17.02?22.22 GiB. RAM use was 68.4?82.4%, mean 73.51%. CPU was 17.1?75.0%, mean 47.23%. These are the minute samples, with the old guard's 75% commitment threshold.

## Final lightweight verification before review

Final batch-check.sh --fast exited 0. EnsembleCheck then exited 0, including EN2's same-version precedence and later-version retirement fixtures. The ensemble runner/scorer self-tests passed all three CLI tests after the selection and space-preserving argument changes. The final provenance-only rerun is in kd1-ensemble-selftest.txt.

The publisher also rejects newly added numeric docs/ensemble/<version>.md files in ship.sh lines 182?183, so the orchestrator's EN2.md fallback is required. Its version comparison retires EN2 as soon as a newer numeric baseline is committed. The orchestrator was informed of both publisher guards.


## Review REV-KD1-1

? The reviewer found that CPU was sampled before the Windows memory probe, whose timeout can span several CPU samples. Read CPU after memory through the shared readResources helper, immediately before admission. An injected memory read moves CPU from below the ceiling to the ceiling while the read is in progress. KnownRedsCheck then proves the refreshed CPU holds the start, without sampling this PC. This corrects the review finding before the baseline and launcher run.

The review repair's batch-check.sh KnownRedsCheck run exited 0, including the CPU-crossing fixture, regenerated maps and zero firm Stale findings. Its wrapper log is target/checks/kd1-review-check.txt.

## Main integration and accepted review

The orchestrator accepted REV-KD1-1 on 38cdf1eb at 02:26. At 04:36 it named main b49a6023c892e61e575f39c57b4d16774cf6fdd7 for the EN2 baseline and said R2b leaves the default trace unchanged. Merged origin/main b49a6023 into KD1. The three conflicts were generated dials, harness index and map README, resolved by regenerating from the merged sources. batch-check.sh --fast exited 0 with zero firm Stale findings. KD1's source diff against main still contains only AllChecks, KnownRedsCheck, EnsembleCheck and TimingCheck.

★ MG1 supersedes Pending SP1-E's manual pagefile request. The new rule uses available commitment directly and includes RAM and CPU. Recommend dropping SP1-E on publication, as MG1's brief requests.

## EN2 baseline run

★ The orchestrator revised its operational scheduling rule at 04:39, explicitly permitting one dry run beside one ensemble while continuing to forbid two dry runs or two ensembles together. It cited R2b's 03:18–04:05 dry run beside RT2's five-worker ensemble, approximately 42% CPU and 7 GiB free, with KeptCheck ALL OK. KD1 follows that revised go. The earlier 1,740-second timeout involved two suites and five ensemble JVMs.

Started the 80-run baseline at 04:39 on c5f5df20, whose game model is main b49a6023. The run is target/ensemble/KD1-EN2, with JOBS=5, all sixteen seeds and all five policies. JDK_JAVA_OPTIONS is unset and JAVA_OPTS empty, so this exercises the runner's own worktree discovery. Both main data files exist, earth-2.bin approximately 269 MiB and world-1.bin approximately 8.4 MiB. Its wrapper log is target/checks/kd1-ensemble-run.txt.

The ensemble finished at 06:04, exit 0, 80/80 runs, no missing or short runs, 83.7 minutes wall time. Summed per-run elapsed time is 24,884.9 seconds (414.7 JVM-minutes). The README now points to measured run times instead of retaining its obsolete 20-minute estimate. The committed baseline is docs/ensemble/EN2.md, model 0.9.111, snapshot c5f5df20 at 2026-11-15T08:40Z, scored against docs/ensemble/0.9.106.md.

New score is 23/26 against the prior baseline's 22/26, with all 10 hard criteria passing. ZERO_SLACK T2 changed from informational FAIL to PASS, median final population 513,902 [100,209 to 1,932,923]. The three remaining informational failures match the baseline readings and verdicts. AUTO2 A1 inflation is 3.15% [2.62% to 3.69%], four of sixteen seeds in band. AUTO2 A2's runaway spell is 22 [12 to 36] months. ZERO_HOT H2 is -0.36% [-5.42% to 4.53%], nine of sixteen below the earlier excess. M1 and F1 pass for all policies.

At 05:30 the orchestrator reported R2b shipped on main d8e9c12b, with all eight default traces byte-identical, and confirmed this baseline remains valid. Main was merged before the launcher dry run, as requested.

## Final main integration and process count

Merged main d8e9c12b before the dry run. The only conflicts were generated docs/harnesses.md and docs/map/README.md. Regeneration resolved them, batch-check.sh --fast exited 0, and EnsembleCheck read the committed EN2 baseline successfully against game 0.9.112. Its log is target/checks/kd1-en2-readable.txt. The source diff from this main contains the same four runner/check files. Selected source/rules/scripts retain LF line endings.

Final git diff --numstat origin/main over docs/batch.md, tools/ensemble and the four changed runner/check sources gives 461 added and 426 removed. Generated dials, harness index and maps add 491 and remove 546. Combined process and session-index scope is 952 added, 972 removed. Own notes, ship and ensemble score are excluded. The core rows are 75/103 batch checklist, 141/48 AllChecks, 25/64 EnsembleCheck, 45/1 KnownRedsCheck, 8/60 TimingCheck, 10/49 ensemble driver, 62/90 ensemble README, 32/10 runner and 63/1 runner/scorer self-tests.

The orchestrator granted the no-tag dry run at 06:05 after main integration and a pushed provisional ship file, with RT2's single ensemble beside it under the revised scheduling rule. No other heavy job runs for KD1.

## Retained launcher run

The normal route is sh tools/gate.sh KD1 --dry-run --no-tag, started at 06:08. Its retained directory is /tmp/cbs-gate-KD1-8gf7LpIs. It tests branch 4259c05569ba09d21a1fb5715ac491a0eb0ba662 against main d8e9c12b98ffcc50fb7c0bbbad7d9c32f1064477. The worktree wrapper log is target/checks/kd1-gate-launch.txt and the retained publisher log is ship.log.

An early Win32_Process snapshot confirmed three simultaneous direct harness children of the actual suite runner, CentralBankCheck, LongPlaytest and MapCheck, matching AllChecks.PARALLEL. This is a measured live run alongside RT2's one ensemble under the granted slot.

### Final verdict and after measurement

Launcher exit 0 at 06:45. Its full normal selection was ALL, 203 registered harnesses, with the gate tier and directly selected BridgeMapCheck producing 189 runs. AllChecks reports 184 passed and five failed in 2,075 seconds. The complete build/check step took 2,148 seconds and exited 3. All five were registered known-red harnesses, MoneyDatesCheck, MapCheck, DeathRecordCheck, LongPlaytest and ResearchViewCheck. There are zero [NEW] tags. Maps regenerated and Stale reported zero firm findings. The local changed-script self-tests had passed all three CLI tests. checks.complete is retained with the run.

Thirty-six starts waited for CPU, including two during its initial sampling window and 34 with measured six-second CPU averages from 80.4% to 89.6%. Commit and RAM each held zero starts. No process-start memory error or harness timeout was reported. The suite reached its three-harness cap. Summed harness time was 5,908.0 seconds, against 4,184.1 seconds in the before run.

| run | suite seconds | complete check seconds | harnesses | starts held | other load |
|---|---:|---:|---:|---|---|
| Before, RT1b's retained run | 1,420 | 1,483 | 191 | 0 | one builder JVM |
| After, KD1's retained run | 2,075 | 2,148 | 189 | 36 CPU, 0 commit, 0 RAM | RT2's five-worker ensemble |

MemWatch's 06:09–06:44 minute samples give commitment 43.18–48.81 GiB, mean 45.96, against a 63.26 GiB limit. Free commitment is 14.45–20.08 GiB, mean 17.30. RAM use is 73.1–90.1%, mean 80.29%. CPU is 44.4–80.5%, mean 60.46%. The admission log's CPU figures use the guard's two-second samples over six seconds. The before figures and its two commit identities are above. Both workload selections and the shared-PC load are recorded for this comparison.

After the kept run, only this notes file and KD1-ship.md advance. The ship file names /tmp/cbs-gate-KD1-8gf7LpIs for reuse. Remote main remained d8e9c12b98ffcc50fb7c0bbbad7d9c32f1064477 at the final check. The readiness marker is removed in the final ship-only commit.

The first final marker-removal command failed its UTF-8 heading assertion after the PowerShell pipe changed that assertion's text. The shell continued and sent a premature readiness message for 5a48e08c, while the branch retained its provisional marker. That notification was corrected immediately. The final command uses explicit UTF-8 pipe encoding and stop-on-error handling, then verifies the marker removal, remote tip and commit footers before sending readiness again. The tested source and retained run are unchanged.
