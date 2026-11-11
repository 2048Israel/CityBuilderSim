# GR2 - main's known reds in the repository, and "ready" on the whole suite

Jerus, 2026-11-11, two decisions. Where the list of main's known reds lives:
"A file in the repo (Recommended)". When a batch runs every check before it
says ready: "When it touches shared code (Recommended)". After GR1, on the same
files.

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
- **Naming without running again.** When a gate stopped only on lines that no
  entry names, and since then main has gained nothing a harness reads (only
  `.md` files outside `docs/ensemble/` and `known-reds.txt` itself: the
  docs-only test `ship.sh` already has) and the branch is the same commit,
  `tools/gate.sh --rejudge <id>` judges the kept suite log again against the
  file as it now is, and goes on to the records, the commit, the tag and the
  push. Anything else is a full run. The stopped run's clone and logs are kept
  until then (GF1 keeps the logs).
- **`tools/pick.sh`**: a changed class over the fan-out picks `ALL`, the whole
  suite, where it picked LongPlaytest and left the rest to the gate. A changed
  harness class picks every harness that names it as well as itself, not
  itself alone: 19 files call `LongPlaytest.run`, and `ChildcareCheck.town()`
  builds other harnesses' towns.
- **Ready.** When the pick is `ALL`, the batch merges `origin/main` first and
  runs the suite on that merge. Ready is exit 0 or 3 there, every other red
  mended or entered in its `## known-reds`, and its notes say which commit of
  main it was run against. Otherwise ready is the pick, as now. The gate still
  runs the whole suite once on its own merge.
- **The docs**: `docs/batch.md` 4, 5 and 8 and `docs/orchestrator.md`, "The
  gate" 5, say this in fewer lines than they have; `CLAUDE.md`'s Checks bullet
  names the file.

Not in this batch: mending the fixtures the file lists (their own rows, the
redesign's next step), and the ready record in MemWatch's state folder.

## The checks

- KnownRedsCheck: a part listed for another harness does not pass; a count line
  passes only with a known line of its harness; the three callers give one
  verdict on the same log.
- `tools/ship-selftest.sh`: a `## known-reds` section is filed; a batch that
  mends a row is judged without that row's lines, and the merge drops them; a
  re-judge after a stop ships when only the file changed, and refuses when main
  gained a `.java` file or the branch moved.
- `tools/pick-selftest.sh`: a class over the fan-out gives `ALL`; a changed
  harness picks its users.

## Done means

The self-tests pass. On this branch merged with main the whole suite, by
`./batch-check.sh --pick` (which is `ALL` here), exits 3 with no `--red-line`
anywhere: every red of main's is in `gate.txt` or the file. A real
`tools/gate.sh --dry-run` of this branch passes with no names on its command
line. One in, one out (`CLAUDE.md`), the count in the notes. The ship file last.
