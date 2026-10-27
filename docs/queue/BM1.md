# BM1 - the batch manual split by reader, and one in, one out

Queued 2026-10-27 from a side session with Jerus, on his word ("yes please,
do it as soon as humanly possible"; `docs/queue/GP1.md` has his words and
the figures). Last of the four process batches, since GP1, RL1 and SP1
each change `docs/batch.md`.

## Why

Every batch session reads `docs/batch.md` (476 lines on 2026-10-27), but it
needs about 120 of them: `batch.md:67-186`, "A batch session". The rest is
the orchestrator's: the day, the gate, the screen-check session, the PC, the
Earth file, the other assistants, the docs pass. Between Oct 23 and Oct 27
the rules a session reads (`CLAUDE.md` plus `docs/batch.md`) went from 309
to 724 lines, `ship.sh` plus `batch-check.sh` from 355 to 1,151, and the
scripts in `tools/` from 342 to 1,784. Each rule came from an incident;
none has left.

## What BM1 does

1. **Split by reader**: a batch session's file, a checklist of what it
   reads and does, and the orchestrator's. `CLAUDE.md`'s pointer names both
   and says who reads which.
2. **Each rule keeps a one-line WHY** naming its incident (date, batch). The
   long history moves to `docs/notes/`. The WHY is not dropped: without it
   a later tidy-up can bring an old mistake back (CC1-S's broad
   `--red-ok`, for one).
3. **The batch's report** is "ready, see the ship file" plus the calls, each
   with its recommendation; not a second summary of what the ship file and
   notes already say.
4. **One in, one out** written into `CLAUDE.md`'s standing rules (Jerus,
   2026-10-27): a batch that changes the process (the scripts, the checks'
   machinery, the rules a session reads) removes about as many lines as it
   adds, and its notes give the count. BM1 itself ends shorter than it
   started.
5. **The notes and the ship file stay two files.** Folding them together
   runs into `ship.sh`'s heading check (header, step 5) and saves little.

## Scope and checks

`docs/` and `CLAUDE.md` only; gates on `--fast`; ships `--no-tag`. The
notes give the line counts before and after. Tell the orchestrator so it
updates the PC's hand-off note, which points at `batch.md` sections.
