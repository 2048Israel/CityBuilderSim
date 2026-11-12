# GF1 ? the gate fails safe (2026-11-11)

Codex builder session `01a1cd1b-9c4a-7432-b30c-84e0000369d5`, thread `codex-GF1`,
branch `batch/GF1`. Jerus authorized this headless run through Claude host
session `5ed95594-d6fa-4a1f-98e2-b975e0c6b6cc`.

## Built

- `ship.sh` clears every status and consumed result before a check, validates a
  fresh stage-specific result, and records required and finished stages.
  Completion and caller liveness are proved before the gate record and commit.
  Origin's branch must still be the gated commit and carry its ship file.
- Every publisher output line is timestamped. Each check logs its start,
  duration and status. Graceful exits retain stage evidence beside the log.
  `tools/gate.sh ID` creates a fresh clone, names both main-checkout data files,
  returns the publisher's status and retains its logs outside the clone.
  The branch's publisher is extracted before merging, exercising GF1 itself.
  `--red-lines FILE` reads one approval substring per line.
- The public stop marks cancellation and ends its owned process tree. Windows
  validates the native PID, creation time and publisher command line, walks
  MSYS logical descendants across exec gaps, then ends and verifies each native
  subtree. A guardian watches the caller chain and its native host. Stopped
  clones stay for inspection. Successful clones yield check logs before removal.
  Guardian output uses `watch.log`, joined after it exits, preserving Windows
  append offsets. The record uses text logs.
- Every unnamed red is kept beside the log, with a count and twelve lines on
  screen. `AllChecks.HARNESS_SECONDS` is four usual 435-second KeptCheck runs,
  1,740 seconds. Over-limit harnesses and descendants are ended and counted
  failed with `FAIL: timed out after N s`. This completes KD1's first item.
- KnownRedsCheck exercises a hanging child and descendant using a short limit,
  and preserves an in-time child's exit and output. AGENTS routes Codex as a
  batch builder and scopes the old connector brief correctly. Replaced script
  headers and gate/PC prose were shortened. Existing timing and red approval
  semantics include completed paired rounds with no comparison.

## Checks

- Initial `./batch-check.sh --fast` exited 0, compile 51 seconds, zero firm
  Stale findings. Named KnownRedsCheck exited 0 in seven seconds through the
  batch check, including all six timeout assertions.
- Final `./batch-check.sh --pick`, after merging main, exited 0, two of 201
  harnesses in seven seconds, compile three seconds. Maps regenerated and
  Stale had zero firm findings. Picker self-tests exited 0, 13/13.
- Publisher self-tests exited 0, 128/128, including the public stop's own status.
  The exact final four scripts were verified in a temporary worktree using
  `sh tools/ship-selftest.sh C:/Users/Jerus/AppData/Local/Temp/cbs-GF1-verify`.
  Their SHA-256 hashes match the final source byte for byte. That worktree
  was removed. Logs and the manifest are in
  `C:/Users/Jerus/AppData/Local/Temp/cbs-GF1-evidence/`.
- Earlier runs exposed paired-round compatibility, accumulated unnamed output,
  an older publisher resetting the clone directory, a reserved PowerShell
  variable, a child exiting during taskkill, and native append-handle log
  corruption. Each was corrected before the final clean self-test run.
- The ensemble was skipped for this batch, whose changes are confined to
  process instructions, scripts, the runner and its harness.

## Full dry run, exit 0

- Branch `febf9a0de85e38b5968144cd21cd200ede58f201`, merged with main
  `aa2fa42276b78f365fe7566cca90137841416c19`, using `--dry-run --no-tag`.
- All 98 approval substrings came from the newest
  `C:/Users/Jerus/AppData/Local/Temp/cbs-gates/gate-PW1.sh`, read only. The copy
  is `C:/Users/Jerus/AppData/Local/Temp/cbs-GF1-red-lines.txt`. Zero names were
  added and the full run needed no retry.
- Kept log `C:/Users/Jerus/AppData/Local/Temp/cbs-gate-GF1-aD2lMGWb/ship.log`.
  `suite-evidence/` keeps the full suite log and reds, hard timing reds and TSV.
  `checks/` keeps per-harness and paired logs, figures and judgments.
  `refs.before`, `refs.after`, `head.after` and `exit` record provenance and
  outcome. The successful clone was removed.
- All 201 ran in 1,570 seconds. DeathRecordCheck, HealthCheck, KeptCheck,
  LongPlaytest, MonetaryCheck and PlanCheck were main's six recorded-red
  harnesses. Hard timing triggered three paired rounds for SaveSlotCheck,
  MapCheck and KeptCheck, taking 1,527, 1,483 and 1,638 seconds, 4,648 total.
  Every residual timing line passed the existing paired judgment.
  The run ended normally at 22:33 and restored HEAD to `aa2fa422`.
- During the run the author added BW1's queue row and brief on main as
  `203251720cb342b7e6cf141b025c30c12402bc47`. This upstream change was merged
  into GF1 before the second run. The full run's AllChecks Java source is
  byte-identical to final GF1, blob `1b4cd964c6d7d2f9329202d3d7a0a90dd9139e23`.

## Timing stop, exit 130

- Branch `4db826c2063b823806f54d96b6c78f7fd4ee32da`, merged with main
  `203251720cb342b7e6cf141b025c30c12402bc47`, using `--dry-run --no-tag`.
- Kept log `C:/Users/Jerus/AppData/Local/Temp/cbs-gate-GF1-joa9TQqP/ship.log`.
  Its clone, stage files and `watch.log` are retained. `refs.before` and
  `refs.after` are byte-identical, including main, the branch and every tag.
- BrandingCheck and CalendarCheck formed the partial suite, both green. After
  hard timing started at 22:38:52, the public stop cancelled the owned tree.
  Stop returned 0 and the launcher 130. HEAD stayed at main's `20325172`, and
  the gate record matched HEAD byte for byte. There was no new commit or tag.
- MemWatch PID 34864, started at 22:19 before the stop, was alive afterward.
  The existing Claude wait shell PID 38484 was observed immediately afterward.

## Closed caller

Only the identified PowerShell host was killed to simulate the app closing
its task shell. The guardian cancelled the held timing check and recorded 130.
The throwaway origin's complete main/branch/tag refs were identical. The log is
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-RED-6X5mrGZM/ship.log`. This test also
verified the corrected serialized logging through an identical temporary
launcher copy while the long full run held the original source file open.

## Line count and handoff

One in, one out, `git diff --numstat origin/main` over the 13 process files
and generated indexes, excluding this note and the ship file, gives
**710 lines added and 612 removed**. Reductions cover publisher and
self-test headers, runner descriptions and the replaced gate/PC prose.

A fresh Claude session reviews GF1 against its brief, runs script self-tests
and `./batch-check.sh --fast` on the merge, then merges untagged. At that merge,
remove KD1's completed child-JVM cleanup clause from its queue row. Its MG1
memory-guard and EN2 ensemble items are the work still queued there.
