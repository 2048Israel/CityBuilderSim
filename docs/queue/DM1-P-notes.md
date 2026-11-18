# DM1-P — batch-check.sh names the Earth and world files

Built from origin/main 231f16270 (0.9.140), on batch/DM1-P.

## Built and calls

- ★ One helper, `tools/check-data.sh`, now serves batch-check, nightly and
  gate. It replaces nightly's private helper and the gate's unconditional
  properties, so discovery and caller precedence cannot drift between them.
- ★ Choose each existing file from the current checkout's `data/` first,
  then from the parent of the absolute Git common directory. Nightly and
  gate pass their original main checkout because their fresh clones cannot
  discover that checkout. A missing local world file can fall back separately
  from a local Earth file. Windows paths use `cygpath -m` and quoted values.
- ★ A nonempty `JDK_JAVA_OPTIONS` is preserved in full; it is not augmented.
  The single `data` line names automatic properties, reports none found, or
  says the caller's options were kept. `--judge` retains its saved-log-only path.
- Searched tracked docs for the advice that worktree checks must manually
  export these properties, including docs/batch.md, docs/deepseek.md and
  AGENTS.md: no such active advice was present. Historical incident reports
  and ensemble metadata remain true records. No DM1-P known-red entry exists.
- No files under src/ changed. The default model trace remains the gate's
  named-data path; the bug was the builder selecting different inputs.
  No ensemble is needed for this process repair.

## Checks

- Shell syntax: the changed shell scripts pass `sh -n`.
- `sh tools/nightly-selftest.sh`: exit 0, 57 PASS assertions. Missing files,
  Earth alone, both files, checkout preference with a missing world file,
  paths containing spaces, and caller options all pass on Windows.
- With JDK_JAVA_OPTIONS unset, `./batch-check.sh --fast`: exit 0; both
  main-checkout data files named as C:/ paths, compile OK, maps unchanged,
  Stale 0 firm findings (187 soft). Log: target/dm1-p/fast.log.
- With JDK_JAVA_OPTIONS unset, `./batch-check.sh MapCheck`: exit 3,
  two matching harnesses in 497s (MapCheck 496.3s). Its four known assertions
  and derived exit/count lines match all six MapCheck lines in the main
  record exactly after newline normalization; no NEW reds. Six timing
  assertions soft. Logs: target/dm1-p/map-check.log, map-suite.txt,
  map-reds-lf.txt and main-map-reds.txt.
- With `JDK_JAVA_OPTIONS=-Ddm1.fixture=kept`, `./batch-check.sh --fast`:
  exit 0, data line reports options already set, and Maven logs exactly
  that property with no cbs.earth or cbs.world additions. Logs:
  target/dm1-p/caller-fast.log and caller-compile.txt.
- `sh tools/ship-selftest.sh`: exit 0, 156 of 156 passed, including
  actual launcher/guardian cases against a throwaway origin and stub suite.
  Log: target/dm1-p/ship-selftest.log. No real gate suite was started.
- Pick: ALL (218 harnesses), because batch-check.sh runs the suite.
  Review: not needed — no money, saves or threading in the diff.
  The full launcher dry run awaits the orchestrator's review and machine slot.

## One in, one out

`git diff --numstat origin/main` over changed scripts, excluding these notes
and the ship file (32 added, 22 removed):

```
4	4	batch-check.sh
15	0	tools/check-data.sh
1	3	tools/gate.sh
10	5	tools/nightly-selftest.sh
1	9	tools/nightly-timing.sh
1	1	tools/nightly.sh
```

## Provisional handoff

Code is 1a86c525c; all local checks above ran on that code. The ship file
keeps its provisional marker for the orchestrator's review and machine slot.
The pending ALL dry run is `sh tools/gate.sh DM1-P --dry-run --no-tag`;
no full `--pick`, ensemble or real dry run was launched, and no kept gate
run exists yet. Main must be refreshed before that scheduled run as briefed.
