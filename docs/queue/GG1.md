# GG1 — the new gate's gaps

Jerus, 2026-11-13: "sure queue and do the gate gaps". The lane keeper found
these on 11-12 and 11-13, the first days the new gate (GF1, GR1, GR2)
published: KS1, DM2, BS1, GDP1, AW1, RT3, AW2 and HA1. Each cost a stop, a
send-back or a hand instruction to a builder. A process batch: no game code.

## The gaps

1. **A versioned dry run needs its ship file first.** `ship.sh` (the ship
   file check near line 160) refuses a branch with no
   `docs/queue/<id>-ship.md` unless `--no-tag`. But `docs/batch.md` 5 says
   to push the ship file last, after the dry run, and a `--no-tag` dry run
   seals the wrong version for reuse. BS1, DM2, GDP1, AW1 and AW2 pushed a
   provisional ship file by hand instruction. Make one rule: either the dry
   run takes its records from the worktree without a pushed ship file, or
   the provisional ship file is the documented step and the readiness
   marker is something else.
2. **A batch cannot re-file its own known-reds entry.** The gate marks every
   entry naming the batch removed ("! ") and `KnownReds.java` (about line
   52) refuses cover to any line matching a removed entry, so a ship-file
   row under another id never covers it. KS1's first gate and DM2's dry run
   stopped on this. The builder's own `batch-check.sh` rejudge does not
   apply the veto, so the line shows green there and red at the gate. The
   workaround was to edit the entry's batch in `known-reds.txt` on the
   branch. Make the rule explicit (a ship-file `## known-reds` row may move
   one of the batch's own entries to another id, with its cause), and make
   `batch-check.sh` apply the same removal as the gate.
3. **The gate will not reuse a kept run once the branch changes
   `known-reds.txt`.** `tools/gate-reuse.sh` allows only the notes and the
   ship file to advance on the branch. Moving an entry (gap 2's workaround)
   then costs a full suite. Allow `docs/notes/known-reds.txt` there too,
   with the rejudge against the new file.
4. **A batch with no `QUEUE.md` row gets its merge title from its last
   commit** (GDP1: "Merge batch/GDP1: docs: mark GDP1 ready ..."). Take the
   title from the ship file's changelog heading when the queue has no row.
5. **`{next}` outside the record files stays literal.** BS1's HoldersCheck
   labels on main still read `{next}` three times; `ship.sh` notes it and
   leaves it. Refuse a branch whose source contains `{next}`, or fill it.
6. ★ **Records written with a lost encoding.** HA1's notes and ship file
   came with every em dash, en dash, ★ and curly quote as a literal `?`
   (written through PowerShell's default encoding). Refuse a ship file
   whose changelog heading lacks its " — " separators, or say why a check
   is not worth its lines.

## Done means

Each gap closed in the scripts and `docs/batch.md` with a self-test case
that fails on today's main and passes after. One in, one out: the notes give
the line count over the process files (`CLAUDE.md`). No tag (process).
After PO1: it changes the same scripts.
