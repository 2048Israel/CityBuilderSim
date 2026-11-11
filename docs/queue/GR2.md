# GR2 - known reds in the repository, and what runs where

Jerus, 2026-11-11, four decisions. Where the list of main's known reds lives:
"A file in the repo (Recommended)". When a batch runs every check before it
says ready: "When it touches shared code (Recommended)". Whether every batch
that changes no game code skips the whole suite at the gate: "Yes, make it the
rule". Whether the gate reuses a batch's own full run when main has not changed
since: "Yes, reuse it (Recommended)". After GR1, on the same files.

## What the game wants

No gate has been green since 0.9.11 (2026-11-04). A batch passes by main's
recorded reds (`docs/notes/gate.txt`, word for word) and, for the lines whose
words move with the city's path, by a list of `--red-line` parts that the
orchestrator copies by hand from one temp gate script to the next: 40 names on
11-08, 101 on 11-11 (`%TEMP%\cbs-gates\gate-LM1.sh`), passed on Jerus's
standing word (2026-11-04: "always, if checks were not caused by its own code,
then it should always pass"). `ship.sh` takes the names only at its launch, so
five gate runs in five days were repeated whole (38 to 131 minutes each) only
to add names. And a batch's own check sees neither the list nor most of the
suite: `--pick` ran a median 32 of about 190 harnesses and leaves the twelve
classes over `CBS_PICK_FANOUT` to the gate, so four gates in five days stopped
on reds the batch had never run (WG2: 97 lines in 16 harnesses, and a send-back
of 3 h 14 min).

So the suite should run once for a batch, where its builder can act on it, and
only where it can say something: all of it when game code changed, the
harnesses changed when it did not.

## What to build

- **`docs/notes/known-reds.txt`**, in git, read by the gate and by every
  batch's check. A line is `Harness | part of the red line | the queued batch
  that mends it | date | why it is not a batch's own`, under a header that
  quotes his standing word. A red line is known when main's last gate recorded
  it word for word (`gate.txt`, as now) or it holds a part listed for its
  harness. The count-line rule stays: a harness's `exit N` and `N FAILED` pass
  with a known line of it when none of its lines is left over. `AllChecks -f`
  tags by the same file, `batch-check.sh` exits 3 or 4 by it (`--judge` too),
  and `ship.sh` passes by it, the parts it used in the merge's body. One judge
  with three callers; KnownRedsCheck proves they agree.
- **Seeded** from the last gate scripts' names, each with its row: read
  `gate-LM1.sh` and `gate-PW1.sh` in `%TEMP%\cbs-gates`, the gate bodies beside
  them and `QUEUE.md` (HX1, MQ1, KL1, OX1, DM2, DM3, PX1, BQ1, CB2, CL6, KS1,
  KS2, RT3, BS1 and the rest). A name with no row gets one in the ship file's
  `## queued`. The count lines need no entry.
- **Who adds a line.** A batch, in its ship file: a `## known-reds` section of
  such lines for the NEW reds that its whole-suite run showed and its own code
  did not cause, each with its cause traced in the notes and the row that
  mends it; `ship.sh` files them at the merge as it files `## pending` and
  `## queued`. Or whoever keeps the gate, in a commit of that file alone.
  `--red-ok` with `--red-line` stays for a line Jerus passes once, in his own
  words.
- **Who removes one.** `ship.sh`, before it judges: every line whose mending
  batch is the batch at the gate goes, so a fix is judged without the excuse it
  came to remove, and the merge deletes those lines.
- **`tools/pick.sh`**: a changed class over the fan-out picks `ALL`, the whole
  suite, where it picked LongPlaytest and left the rest to the gate. A changed
  harness class picks every harness that names it as well as itself, not
  itself alone: 19 files call `LongPlaytest.run`, and `ChildcareCheck.town()`
  builds other harnesses' towns.
- **What the gate runs.** `ship.sh` asks `tools/pick.sh` about the merge. When
  the merge changes game code (a `.java` file under `src/main/java` that is not
  a harness file, a resource, `pom.xml`) or the suite's own runner
  (`AllChecks.java`, `Timing.java`, the suite's part of `batch-check.sh`), it
  runs the whole suite, as now. When it changes neither (scripts, docs, tools,
  harness files alone), it runs the compile, the harnesses the pick names (a
  changed harness, its users, TimingCheck), the maps, Stale and the self-test
  of each script the merge changes, and its `Gate:` line says so ("no game
  code: N harnesses"). Such a gate leaves `gate.txt`'s recorded reds as they
  were, as a docs-only gate does; step 5's "a partial suite never ships"
  becomes this rule. A docs-only merge stays on `--fast`.
- **A run already made.** The gate takes a kept run in place of its own when
  all of this holds: the launcher made it in a fresh clone (a batch's
  `tools/gate.sh --dry-run`, or the gate's own run that stopped), never a
  session's working folder; its log names the branch's commit and main's, and
  the branch is that commit still; main is that commit still, or has gained
  only `.md` files outside `docs/ensemble/` and `known-reds.txt`; and it ran
  what this gate would run. The gate then judges the kept log against the
  known reds as they are now and goes on to the records, the commit, the tag
  and the push, in about a minute, its `Gate:` line naming the run it took.
  Anything else is a run of its own. This is also how a line is named after a
  stop without a second run: add it to the file, and gate again.
- **Ready.** When the pick is `ALL`, the run that counts is the launcher's:
  the batch pushes its branch, runs `tools/gate.sh --dry-run <id>` (a fresh
  clone, the merge with main, the whole suite) and is ready when that passes,
  every other red mended or entered in its `## known-reds`. Its ship file,
  pushed last, names the kept log, and the gate reuses it. Otherwise ready is
  the pick, as now, and any batch may make the dry run to be gated in a
  minute.
- **The docs**: `docs/batch.md` 4, 5 and 8 and `docs/orchestrator.md`, "The
  gate" 4-5 and 7, say this in fewer lines than they have; `CLAUDE.md`'s Checks
  bullet names the file.

Not in this batch: mending the fixtures the file lists (their own rows, the
redesign's next step), and the ready record in MemWatch's state folder.

## The checks

- KnownRedsCheck: a part listed for another harness does not pass; a count line
  passes only with a known line of its harness; the three callers give one
  verdict on the same log.
- `tools/ship-selftest.sh`: a `## known-reds` section is filed; a batch that
  mends a row is judged without that row's lines, and the merge drops them. A
  merge of a harness file alone runs the pick and leaves `gate.txt`; one that
  also changes a game class, or `AllChecks.java`, runs the whole suite. A kept
  dry run of the same two commits ships without the checks running again; it
  is refused when the branch moved, when main gained a `.java` file, when the
  log is a stopped run's with reds still unnamed, and when it was not the
  launcher's.
- `tools/pick-selftest.sh`: a class over the fan-out gives `ALL`; a changed
  harness picks its users.

## Done means

The self-tests pass. On this branch merged with main the whole suite, by
`tools/gate.sh --dry-run` (the pick is `ALL` here), passes with no `--red-line`
anywhere: every red of main's is in `gate.txt` or the file. One in, one out
(`CLAUDE.md`), the count in the notes. The ship file last.

## How it reaches main

No gate run (Jerus, 2026-11-11: "these three batches shouldnt need a gate no?
sure a fresh session"). The dry run above, by the launcher in a fresh clone, is
the full run: its notes say where its kept log is and which two commits it was
of (the branch's and main's). A fresh Claude session, the gatekeeper for GF1,
GR1 and GR2, reviews the diff against this brief, runs the self-tests and
`./batch-check.sh --fast` on the merge, and merges it untagged; a fault comes
back as a numbered list. The ship file is a process batch's (a `## changelog`
block headed as CX2's was, "no tag (process)", and no `{next}`), pushed last.
Cut from main once GR1 is on it.
