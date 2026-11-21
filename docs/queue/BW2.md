# BW2 - the gate's word "published" from our own push, not from main moving

Found by the orchestrator, 2026-11-18, on BW1's first day live.

## What is wrong

`tools/gate.sh` (BW1's outcome block, about line 160) sets `PUBLISHED=yes`
whenever origin's `refs/heads/main` differs between `$RUN/refs.before` and
`$RUN/refs.after`. Main also moves when someone else pushes during the run.
At 17:3x a `--dry-run` of LM1-D ran while Jerus's SZ1 queue commit
(79e336def) reached main: the gate mailed LM1-D "shipped untagged on
0.9.151" and posted state `shipped` to `cbs\state\gate.txt`, though the dry
run publishes nothing. At 18:15 LM1-D's real gate stopped with "main moved on
origin while the checks ran" (2e862ae69, FS1's queue commit). That case read
right only because the push-refused outcome comes first.

## What is wanted

- `PUBLISHED=yes` only when origin's main after the run is the clone's own
  merge (`$RUN/head.after`), and never on `--dry-run`. A tag counts as
  published only when `refs.after` holds it at our commit.
- A self-test in BW1's set: a dry run while a stranger's commit lands on a
  scratch origin posts `passed`, `published=no`, and mails no "shipped".
- The same check for any other reader of `refs.before`/`refs.after` in
  `tools/gate.sh`, `tools/ship.sh` and `tools/gate-report.sh`.

## Where it touches, and the gate

`tools/gate.sh`, BW1's self-test, `docs/orchestrator.md` if it names the
rule. Process only, untagged, `--fast` at the gate.
