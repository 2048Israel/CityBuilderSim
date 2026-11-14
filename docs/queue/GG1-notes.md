# GG1 — The new gate's gaps

Built from main b27168e2 after reading HA1, PO1 and EI1's runner, publisher,
nightly and workflow changes. No model, save, resource, harness or version code
changes. The default playtest trace is unchanged by construction, so no ensemble.
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

- `./batch-check.sh --fast`: exit 0; compiled, maps regenerated, Stale clear,
  LF retained. Named script regression checks and the full pick follow.
- Self-test cases cover every numbered gap, with negative controls for still
  retired reds, revoked registry cover and unrelated source advancement.
- Main-control comparison uses the expanded self-test against b27168e2;
  results and final line accounting are recorded before readiness.

## Process accounting

`git diff --numstat origin/main` over all nine changed process files, excluding
GG1-notes.md and GG1-ship.md: **190 added, 181 removed** (net +9).
The offsets are duplicated script-header examples and WC1's superseded
predecessor/slice instructions; executable legacy predecessor checks remain.
`sh -n` passed for every changed shell script; all tracked changes retain LF.
`tools/wait-selftest.sh`: exit 0, 24/24 passed.
