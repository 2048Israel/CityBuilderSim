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

MemWatch history-2026-11-15.csv at 01:26?01:50 gives commitment 41.04?46.24 GiB, mean 43.15, against a 63.26 GiB limit. Free commitment ranged 17.02?22.22 GiB. RAM use was 68.4?82.4%, mean 73.51%. CPU was 17.1?75.0%, mean 47.23%. These are the minute samples, with the old guard's 75% commitment threshold. The after measurement will use KD1's retained launcher dry run under its own recorded load.

## Pending verification

The orchestrator queued KD1's ensemble and dry run after R0f and RT2. Before its slot, merge landed main so the first data-backed baseline represents main's current model. The final baseline scores, process line count, retained run identities and after figures will be added when those runs finish.

## Final lightweight verification before review

Final batch-check.sh --fast exited 0. EnsembleCheck then exited 0, including EN2's same-version precedence and later-version retirement fixtures. The ensemble runner/scorer self-tests passed all three CLI tests after the selection and space-preserving argument changes. The final provenance-only rerun is in kd1-ensemble-selftest.txt.

The publisher also rejects newly added numeric docs/ensemble/<version>.md files in ship.sh lines 182?183, so the orchestrator's EN2.md fallback is required. Its version comparison retires EN2 as soon as a newer numeric baseline is committed. The orchestrator was informed of both publisher guards.

Pre-baseline one-in, one-out count from git diff --numstat origin/main over docs/batch.md, tools/ensemble and the four changed runner/check sources is 447 added and 426 removed. Generated dials, harness index and maps add 489 and remove 546. Across that process scope including the generated session indexes, 936 lines are added and 972 removed. Own notes/ship and the future baseline are excluded. Recompute against main after its pending merges before readiness.

## Review REV-KD1-1

? The reviewer found that CPU was sampled before the Windows memory probe, whose timeout can span several CPU samples. Read CPU after memory through the shared readResources helper, immediately before admission. An injected memory read moves CPU from below the ceiling to the ceiling while the read is in progress. KnownRedsCheck then proves the refreshed CPU holds the start, without sampling this PC. This corrects the review finding before the baseline and launcher run.

The review repair's batch-check.sh KnownRedsCheck run exited 0, including the CPU-crossing fixture, regenerated maps and zero firm Stale findings. Its wrapper log is target/checks/kd1-review-check.txt.
