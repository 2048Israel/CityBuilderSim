# GF1 ? the gate fails safe (2026-11-11)

Codex builder session `01a1cd1b-9c4a-7432-b30c-84e0000369d5`, thread `codex-GF1`,
branch `batch/GF1`. Jerus authorized this headless run through the Claude host
session `5ed95594-d6fa-4a1f-98e2-b975e0c6b6cc`.

## Built

- `ship.sh` clears status and consumed outputs for every check, requires a
  fresh stage-specific status, and records required and completed stages.
  Completion and caller liveness are checked before the gate record and commit.
  The branch must still be the exact gated origin tip with its ship file.
- Every publisher output line has a timestamp. `tools/gate.sh ID` creates a
  fresh clone, names both main-checkout data files with Java-compatible paths,
  returns the publisher's status and retains each run's log outside the clone.
  The branch's publisher is extracted before merging so GF1 exercises its own
  runner. `--red-lines FILE` accepts one approval substring per line.
- `tools/gate.sh --stop RUN_DIRECTORY` marks cancellation, then ends only that
  run's process tree. Windows validates the native PID, its creation time and
  publisher command line before `taskkill /PID /T /F`. A guardian watches the
  caller chain, including MSYS exec ancestry and the native host above it.
  Stopped clones stay for inspection. Successful clones yield their check logs
  to the retained run directory before removal. No JSON state was introduced.
- Every unnamed red is retained beside the log, with a count and up to twelve
  lines on screen. The timeout is `AllChecks.HARNESS_SECONDS`, four times the
  usual 435-second KeptCheck run, 1,740 seconds. It ends descendants too and
  records `FAIL: timed out after N s` as a failed harness. This completes KD1's
  first item only. KnownRedsCheck exercises a hanging child and descendant with
  a short limit and preserves an in-time child's exit and output.
- AGENTS now identifies Codex as a batch builder and scopes the old connector
  brief correctly. The publisher header, orchestrator instructions and runner
  descriptions were shortened where replaced. Timing and red-approval policy
  are preserved, including completed paired rounds that produce no comparison.

## Checks

- Initial `./batch-check.sh --fast` exited 0, compile 51 seconds, Stale zero
  firm findings. `KnownRedsCheck` exited 0 through batch-check, seven seconds
  for the harness, including all six timeout assertions.
- `sh tools/pick-selftest.sh` exited 0, 13 of 13 passed.
- `./batch-check.sh --pick` exited 0, two of 201 harnesses, six seconds. Maps
  regenerated and Stale had zero firm findings. Final rerun and publication
  self-test results will be recorded after completion.
- The first publication self-test exposed the paired-round compatibility case
  while running its earlier publisher snapshot. Completed failed rounds retain
  the existing no-comparison judgment. The final self-test reruns that case.
- This changes process and harness code only. The simulation and save format
  are untouched, so no ensemble run was needed.

## Dry runs and retained evidence

The full dry run uses all 98 `--red-line` substrings from the newest
`C:/Users/Jerus/AppData/Local/Temp/cbs-gates/gate-PW1.sh`. That folder was read
only. The copied list is `C:/Users/Jerus/AppData/Local/Temp/cbs-GF1-red-lines.txt`.
Both dry runs use `--dry-run --no-tag` while the ready marker is absent.
Their exact commits, outcomes and log paths will be recorded on completion.

## Line count and handoff

The final one-in-one-out count will be recorded from `git diff --numstat
origin/main`, with this note and the ship file excluded. A fresh Claude
session reviews GF1 against its brief, runs the script self-tests and quick
check on the merge, then merges untagged as GF1's brief directs.
