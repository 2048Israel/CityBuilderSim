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

## Launcher dry run and unrelated QW1 finding

- Orchestrator go: 20261118-002333-968. Main 873de9427 (0.9.143, CR1-L)
  merged and pushed before the run. Branch commit:
  `5711390a0d3160ef0d51fd71195d1391b7c2e956`; main commit:
  `873de9427d846fa8605e0e8e29a1bce4f02b3d1b`.
- `sh tools/gate.sh DM1-P --dry-run --no-tag`, launcher timeout 7200s.
  Retained run: `C:/Users/Jerus/AppData/Local/Temp/cbs-gate-DM1-P-20261118-002805-6934`.
  It ran from 00:28 to **gate exit 1 at 01:16:42** on 2026-11-18.
  The 01:25 cutoff was lifted by mail 20261118-011206-568; its watcher
  was disabled. No cutoff or timeout ended this run.
- Checks completed and were sealed (`checks.complete`, `checks.sha256`):
  203 harnesses, 192 passed, 11 failed, 2773s. Ten failing harnesses have
  only existing known reds. Only LandNationsCheck is NEW (314.0s).
- **QW1, not the helper:** LandNationsCheck's already-covered real level-12
  hover line misses at 10004.40ms. Its unchanged finally block then calls
  shutdownNow and awaitTermination(10s), throwing at L1734 when that clock
  expires too. This is beside CPB1's expressly allowed ensemble, the same
  load class as QW1's queued 10-second fixture repair. No src/ differs from
  main (`git diff --quiet origin/main HEAD -- src/`, exit 0); the gate
  names exactly the same C:/ Earth/world files as main's previous launcher.
- **The diagnostic lines are not failed assertions.** AllChecks.FAILING
  (L109) matches Exception anywhere and Error at the start; failingLines
  (L565) copies those lines whenever a harness exits nonzero. It thus copies
  the deliberate `fixture quote failure` throws (HoverFixture L1547) and
  eight passing RuntimeException/Error-labelled assertions, each ending OK.
  They became NEW only when the later QW1 fixture crashed. The two identical
  intentional-exception lines need one entry. No main baseline was rerun.
- The ship file has ten precise QW1 rows: the cleanup timeout, that intentional
  diagnostic, and the eight complete passing lines including their OK suffix.
  A future FAIL cannot match those OK entries. The exit count follows the
  covered assertions; no broad exit approval was added. QW1 is already queued
  (QUEUE.md and docs/queue/FB3-notes.md, The dry run).
- These changes affect notes/ship only. The source, main and selection remain
  eligible for rejudging the retained completed run. The gate-run comment is
  line 1. The marker was held pending the decision recorded below; no further
  JVM or suite was started.

## Accepted outcome and readiness

The orchestrator's mail **20261118-012156-922-orchestrator-0ad0** approves
LandNationsCheck by name under QW1, Jerus's 2026-11-04 rule: it is main's
latent fault and this batch's src/ is byte-identical to main. He instructed
the marker to be dropped last and readiness reported now. The original gate
exit remains 1; no green rejudge is claimed. The ten ship entries are the
unique non-count unnamed lines exactly, and the derived exit follows all
covered assertions. Shell known-reds validation passes. The completed run's
origin main/branch/tag refs before and after are identical.

No JVM was started after the gate ended. The nightly owns the machine from
01:20 for about an hour; the orchestrator's no-tag gate with reuse follows
it. The first-line gate-run reference names the sealed completed checks.
Only notes and ship advanced after source commit 5711390a0. Readiness is on
this explicit QW1 approval; the provisional marker is dropped last in its
own commit and its absence checked on origin.
