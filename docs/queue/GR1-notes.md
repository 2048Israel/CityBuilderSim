# GR1 — timing judged nightly, not at the gate (2026-11-12)

## Built

The gate runs its soft-timed suite and records suite reds. Its hard timing
stage, residual judge, paired builds, timing trailers and figure records are
removed. GF1's completion proofs, cancellation checks and publication guards
remain exercised by the ship self-test, with interruption fixtures moved to
the suite stage. A passing stub gate reaches its version tag in one check.

Nightly runs hard timing after the 4,000-month playtest, including when the
playtest is red. It measures the fetched main commit in the same temporary
clone and retains figures, commit, loaded flag, verdict and logs under
`$HOME/.cbs-timing/<hostname>/<date>-<commit>-<run>/`. Each machine keeps its
last quiet run as its baseline. First nights report budget misses with
"no figure of last night's here". Loaded figures retain their verdict and
leave that baseline in place. The last verdict in `$HOME/.cbs-nightly.txt`
includes timing, which `tools/day.sh` already displays with the playtest.

The quiet wait requires four quiet observations 15 seconds apart, with no
Java process and CPU below 20 percent. `CBS_TIMING_WAIT_SECONDS` defaults to
1,800 seconds. Its deadline leads to a marked loaded run. A post-run load
check can also mark the figures loaded. Unique temporary clones and an
exclusive nightly lock protect concurrent invocations.

An over-budget crossing or increase above `CBS_TIMING_MARGIN` (default 15
percent) triggers the harness alone, alternating baseline/current sides for
`CBS_TIMING_ROUNDS` (default three, minimum two). `timing-pair.awk` shares its
key matching and median arithmetic with the unchanged `TimingCheck`. GR1's
nightly mode confirms a worsening standing miss too, as the brief requires.
A confirmed slowdown is searched across first-parent merges by halving,
with the located merge and its first parent confirmed again. Its batch id,
version, assertion, paired figures and budget reach the nightly verdict.
Missing pairs and an unconfirmed boundary produce an incomplete attribution.
The old gate-only daily-figure exemption and its record writer are removed.

## Verification

- `./batch-check.sh --fast` exited 0.
- `./batch-check.sh TimingCheck` exited 0, including the original arithmetic
  fixtures and timing mechanism. `TimingCheck.java` is unchanged.
- `sh tools/nightly-selftest.sh` exited 0. Made-up figures cover standing and
  new misses, rises inside budget, first-night budget judgments, each night's
  own budget, exact and exceeded margins, noisy paired medians, missing pairs
  and alternating sides,
  the third of five stub merges, loaded retention and machine isolation.
- `sh tools/ship-selftest.sh` exited 0, 116 of 116 passed. The tests use a
  throwaway origin, including real tags and pushes there, cancellation,
  changed/withdrawn branches, stale status, stopped launcher and push races.
- `./batch-check.sh --pick` exited 0. NONE of 201 harnesses read these process
  changes. Maps regenerated, Stale had 0 firm findings and 181 soft findings.
- The full fresh-clone dry run exited 0. The suite exited 3 in 2,161 seconds,
  with main's recorded reds in DeathRecordCheck, HealthCheck, KeptCheck,
  LongPlaytest, MonetaryCheck and PlanCheck. Nine assertions went SOFT
  (SaveSlotCheck 1, MapCheck 3, KeptCheck 5). Only the suite stage ran. No
  hard timing, paired rounds, timing trailer or timing figure records ran.
  The tree and HEAD were restored. Origin refs were unchanged and the retained
  unnamed file is empty. All reds were recorded, so none of the supplied
  approval substrings was needed. No extra names or reruns were needed.
- `sh tools/nightly.sh` completed on the laptop at 01:57. It measured main
  `15066de86e0e0c2530119d4c9f6815272764eafe` (0.9.94). The 4,000-month
  playtest exited 1 in 195 seconds with the recorded map-movement finding
  twice in months 3002-3004. Hard timing completed with 151 figures from
  18 harnesses, 139 inside budget and 12 first-night budget misses. Those
  misses account for the hard check's exit 4 and its 20 NEW lines including
  harness counts/exits. The nightly verdict has 0 SLOWER and 0 incomplete.
  The script exited 1 for the playtest finding.
- The real run reached the 1,800-second quiet-wait cap while the dry-run suite
  was active, so its metadata says loaded=1. Its figures were retained and
  the machine baseline remains unset. The first eligible quiet night will
  establish it. The nightly lock and temporary clone were cleaned up.

The process-only diff preserves the deterministic model trace. The ensemble
is skipped under EN1. The harnesses use their scratch cities.

## One in, one out

`git diff --numstat origin/main` over process files, excluding these notes
and the ship file, now counts 384 added and 348 removed, net 36 added.
The accepted pre-review diff was 311 added and 344 removed, net 33 removed.
The ownership guard and its regression cases account for the review increase.

## Kept runs

The implementation was pushed at `04572f29` without a ship marker. The full
dry run uses `tools/gate.sh GR1 --branch-gate --dry-run --no-tag`, with the
97 distinct approved substrings copied from the read-only
`C:/Users/Jerus/AppData/Local/Temp/cbs-gates/gate-PW1.sh` into
`target/checks/GR1-red-lines.txt`. Its retained directory is
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-GR1-enfz2EXW`.
The launcher's `refs.before` names branch
`04572f29acbee3e1030e9391c9566ef553782ed3` and main
`15066de86e0e0c2530119d4c9f6815272764eafe`. The kept log is `ship.log`,
with its soft-suite outputs in `checks/` and stage proofs in `stages/`.
Later commits refine nightly budget matching, measurement metadata, fixture
isolation and verdict readability. The gate implementation is the one
exercised by this full run.

The real nightly retained its figures, commit, loaded flag, per-assertion verdict
and full log in
`C:/Users/Jerus/.cbs-timing/Jerusalem/2026-11-12-15066de8-O7Tpqtr5/`.
`timing.tsv` has all 151 figures, `verdict.tsv` the 151 judgments,
`check.log` the hard check's result, and `nightly.log` the full playtest and
nightly detail. The same last summary is in `C:/Users/Jerus/.cbs-nightly.txt`,
with its latest full log in `C:/Users/Jerus/.cbs-nightly.log`. Timing is appended
to the existing single-line playtest summary, so day.sh displays both results.

The builder's other kept logs are `target/checks/GR1-fast.txt`,
`GR1-timing-check.txt`, `GR1-pick-final.txt`, `GR1-ship-selftest.txt` and
`GR1-nightly-selftest.txt` in that directory. Script self-tests passed against
the final implementations. The original TimingCheck remains unchanged.

## Gatekeeper review corrections (2026-11-12)

Readiness was withdrawn in `b2bb0cea`, pushed before source changes. The
review accepted the retained full dry run and its two commits above. The
publisher, launcher, batch-check, AllChecks and timing arithmetic are the
versions already covered by that run and the gatekeeper's final TimingCheck.

The nightly lock now publishes a prepared nonempty directory containing its
owner identity. Windows records the native PID and UTC process-start ticks.
Linux records PID, boot ID and kernel process-start ticks. A new contender
checks the full recorded identity before recovering a stale entry. The prepared
publication keeps an ownership record present throughout live contention.
Empty legacy locks, dead owners and reused PIDs are recovered. Recovery writes
one line to the nightly log, retained through playtest and failure logging.
A live owner still refuses with exit 3 and preserves its existing log.

Automatic data discovery adds each property only while its file exists.
Windows paths use drive-letter form and quoted values support spaces. A
nonempty caller JDK_JAVA_OPTIONS takes precedence. The no-JVM fixtures cover
missing files, Earth alone, both files and explicit caller options.

`sh tools/nightly-selftest.sh` exited 0 after these changes. Its new ownership
cases exercise fresh ownership, live refusal, preservation of the owner and
log, missing owners, reused PIDs and empty legacy directories. Existing figure,
paired-round, five-merge search and loaded-baseline fixtures also pass. The
kept output is `target/checks/GR1-review-nightly-selftest.txt`.

The requested `sh tools/ship-selftest.sh` exited 0, 116 of 116 passed, in
its own unique temporary area. Its kept output is
`target/checks/GR1-review-ship-selftest.txt`.
The additional pick check exited 0, regenerated maps with no generated diff,
and reported 0 firm Stale findings. Its output is
`target/checks/GR1-review-pick-final.txt`. A separate installed-JDK launcher
check preserved a quoted value containing spaces, recorded in
`target/checks/GR1-review-java-options.txt`. The ownership tests ran in Git Bash on
Windows. The Linux branch was inspected but could not be executed here because
Windows Subsystem for Linux is unavailable. The same self-test exercises the
native ownership branch when run on Linux.
