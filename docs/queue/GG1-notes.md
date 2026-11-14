# GG1 — The new gate's gaps

Built from main b27168e2 after reading HA1, PO1 and EI1's runner, publisher,
nightly and workflow changes. No Java source or game resources
changed. The default playtest trace is unchanged by construction, so no ensemble.
No known-reds entry on this base names GG1.

## Built and calls

- ★ Push provisional ship records before a versioned dry run, marked
  `<!-- readiness: provisional -->`. Day, wait and real publication exclude
  them; dry runs retain the requested version. Remove the marker in the last
  ship-file commit. Existing marker-free ready branches remain compatible.
- ★ One shell helper assembles registry entries for builder and publisher.
  A ship row with the identical harness and part transfers an entry to its
  named fix and cause. Remaining own entries veto exact or broader cover.
  This keeps KnownReds' Java judge unchanged.
- Registry-only branch advancement can reuse a retained launcher run, which
  is rejudged against the new registry. Revoked cover must still stop.
- A missing queue row uses the ship heading's title, before the commit subject.
- ★ Refuse added source containing `{next}`. Main already has eleven source
  comments containing it; blocking all inherited occurrences would prevent
  every batch from shipping or require game-source edits outside this brief.
  The guard catches labels and comments introduced or rewritten by a batch.
- Require the changelog heading's two UTF-8 em-dash separators. This catches
  HA1's lost encoding without guessing whether every question mark is damage.
- Check IDs before branch creation and at publication using changelog headings,
  merged branch refs and merge history, so an advanced old branch is refused.
- ★ Remove duplicated day/wait header examples and the batch guide's superseded
  predecessor and sliced-builder instructions (WC1) to fund the process changes.

## Verification

- `./batch-check.sh --fast`: exit 0; compiled, maps regenerated, 0 firm Stale findings,
  LF retained. Final results are below.
- Self-test cases cover every numbered gap, with negative controls for still
  retired reds, revoked registry cover and unrelated source advancement.
- Main-control comparison uses the expanded self-test against b27168e2;
  all seven groups fail there; final results and line accounting are below.

## Process accounting

`git diff --numstat origin/main` over all nine changed process files, excluding
GG1-notes.md and GG1-ship.md: **190 added, 181 removed** (net +9).
The offsets are duplicated script-header examples and WC1's superseded
predecessor/slice instructions; executable legacy predecessor checks remain.
`sh -n` passed for every changed shell script; all tracked changes retain LF.
`tools/wait-selftest.sh`: exit 0, 24/24 passed.

## Final builder checks

- ALL pick: exit 3, 182 harnesses in 1055 seconds: 180 passed; PlanCheck
  (PX1's two comb-junction lines) and DeathRecordCheck (OX1's orphan line)
  were known reds only. Maps regenerated; Stale had 0 firm findings.
  Logs: `target/checks/gg1-pick.txt`, `target/checks/suite.txt`.
- Publisher regression summary:
  `selftest: 156 of 156 passed`. Log: `target/checks/gg1-ship-selftest.txt`.
- Unchanged-main control at b27168e2: `selftest: 141 of 156 passed`.
  Every numbered GG1 group fails on that control and passes on GG1. The
  changed Ready-flow assertion also fails there because provisional versioned
  records are published early. Log: `target/checks/gg1-main-selftest.txt`.
- A new local branch at main's tip remains available to the ID helper;
  `PD1` is refused. No GG1 exemption or new unrelated red was filed.
- `git diff --check` and shell syntax checks passed. No Java source, game
  resources or publisher-owned records were changed by this batch.

## Retained fresh-clone gates

Both no-tag dry runs passed. The second explicitly exercises GG1's branch
publisher and reuses the first launcher's completed suite; its registry is
rejudged. Main's publisher remains authoritative for the first run. These
are launcher-produced retained runs, not working-tree suite evidence.

- Run: `/c/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/codex-GG1/target/gg1-gate-main`
  Branch: `33c402d04499ffd81187099aed5c4b71d1dd2ca7`; main: `b27168e27a939b4b74a37c604a3075366d2cf0d1`; exit: `0`.
  Selection: `full version=0.9.99`.

- Run: `/c/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/codex-GG1/target/gg1-gate-candidate`
  Branch: `33c402d04499ffd81187099aed5c4b71d1dd2ca7`; main: `b27168e27a939b4b74a37c604a3075366d2cf0d1`; exit: `0`.
  Selection: `full version=0.9.99`.

The ready ship file names `/c/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/codex-GG1/target/gg1-gate-candidate` for reuse.
Only GG1 notes and ship records advance after these runs.

Fresh gate: 182 harnesses in 1037 seconds, 180 passed and the same PX1/OX1
known reds only. Candidate publisher: retained-suite rejudge in 2 seconds,
no suite rerun, the same two known-red harnesses, both launcher exits 0.
