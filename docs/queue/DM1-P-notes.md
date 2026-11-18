# DM1-P — batch-check.sh names the Earth and world files

Built from origin/main 231f16270 (0.9.140), on batch/DM1-P; refreshed to
0e32d5330 (0.9.141) before the final per-property checks. Code: bb9c0ff11.

## Built and calls

- ★ One helper, `tools/check-data.sh`, serves batch-check, nightly and gate.
  It replaces nightly's private helper and the gate's duplicate properties.
- ★ Choose each existing file from the checkout's own `data/` first, then
  from the parent of the absolute Git common directory. Nightly and gate
  pass their original main checkout because their clones cannot find it.
  A missing local world file can fall back independently of a local Earth
  file. Windows paths use `cygpath -m` and quoted values.
- ★ Preserve options per property: an explicit `-Dcbs.earth=` or
  `-Dcbs.world=` stays unchanged, and each missing property gets its default.
  Unrelated flags stay beside the appended properties. The orchestrator's
  20261117-214639-663 review settled this: an unrelated heap flag must not
  disable the gate's Earth. One output line labels each property named,
  already set, or not found. `--judge` keeps its saved-log-only path.
- Searched tracked docs, including docs/batch.md, docs/deepseek.md and
  AGENTS.md: no active advice required manual worktree exports. Historical
  incident reports and ensemble metadata remain true records. No DM1-P
  known-red entry exists to retire.
- No src/ changes; the default model trace remains main/gate's named-data
  path. The builder's missing inputs were the bug; no ensemble is needed.

## Checks

- Shell syntax: all six changed shell scripts pass `sh -n`.
- `sh tools/nightly-selftest.sh`: exit 0, 60 PASS assertions. Missing files,
  Earth alone, both files, local preference with partial fallback, paths
  with spaces, unrelated flags, explicit Earth and both explicit properties,
  and the named/already-set report pass. Log: target/dm1-p/mend-nightly-selftest.log.
- With JDK_JAVA_OPTIONS unset, `./batch-check.sh --fast`: exit 0; both main
  files named as C:/ paths, compile OK, maps unchanged, Stale 0 firm findings
  (187 soft). Log: target/dm1-p/mend-fast.log.
- `JDK_JAVA_OPTIONS='-Xmx8g -Ddm1.fixture=kept' ./batch-check.sh --fast`:
  exit 0; Maven receives those exact flags followed by both default data
  properties. Logs: target/dm1-p/mend-unrelated-fast.log and mend-unrelated-compile.txt.
- With `-Xmx8g` and an explicit Earth path containing `data/../data/`,
  `./batch-check.sh --fast`: exit 0; Maven receives that exact caller path
  and only world appended. The data line labels Earth already set and world
  named. Logs: target/dm1-p/mend-explicit-fast.log and mend-explicit-compile.txt.
- An initial flag proof with `-Xmx512m` ran out of heap in Maps and Stale;
  reran with the review's 8g example, without a code change. Kept diagnostics:
  target/dm1-p/mend-low-heap-{fast.log,maps.txt,stale.txt}.
- Fresh `./batch-check.sh MapCheck`, JDK_JAVA_OPTIONS unset: exit 3, two
  matching harnesses in 1746s (MapCheck 1740.0s). All six red-record lines
  (four assertions plus derived exit/count) exactly match refreshed main
  0e32d5330 after newline normalization; no NEW reds. Thirteen timing
  assertions soft. The second-block FX9 premise was not reached; only its
  dependent share law was skipped, informational. Logs: target/dm1-p/
  mend-map-check.log, mend-map-suite.txt, mend-map-reds-lf.txt and
  mend-main-map-reds.txt.
- `sh tools/ship-selftest.sh`: exit 0, 156 of 156 passed on the amended
  code, including launcher, guardian, reuse and registry cases against the
  throwaway origin. Log: target/dm1-p/mend-ship-selftest.log.
- Pick: ALL (218 harnesses), because batch-check.sh runs the suite.
  Review line: not needed — no money, saves or threading in the diff.
  The orchestrator reviewed the mend; no further review requested.

## One in, one out

`git diff --numstat origin/main` over changed scripts, excluding these notes
and the ship file (40 added, 23 removed):

```
4	4	batch-check.sh
17	0	tools/check-data.sh
1	3	tools/gate.sh
16	6	tools/nightly-selftest.sh
1	9	tools/nightly-timing.sh
1	1	tools/nightly.sh
```

## Provisional handoff

The ship file keeps its provisional marker. The ALL dry run is
`sh tools/gate.sh DM1-P --dry-run --no-tag`, waiting for the orchestrator's
explicit go after CR1-L's dry run and gate (about 23:45, not permission by
time alone). No full --pick, ensemble or real dry run has started; no kept
gate run exists yet. Refresh main before the scheduled run as briefed.
