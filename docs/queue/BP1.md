# BP1 - the batch process, cheaper and quicker

Jerus, 2026-10-23: the batch process is "quite the slow and token heavy
process"; on the list below, "yes, all recs", and that a sub-session
messages the orchestrator only when it is done or needs it.

Only the process changes: `CLAUDE.md`, `batch-check.sh`, `ship.sh`,
`docs/deepseek.md`, and the PC's memory notes (`pc-shipping.md`,
`night-line-of-sessions.md`, `sonnet-when-no-deepseek.md`). No game file.
`batch-check.sh` is TM2's file too: run after TM2, or take TM2 in.

## What was found (2026-10-23, the orchestrator's figures)

- The orchestrator was about 500k tokens deep after two days. It is not
  near its limit, but every wake-up (a session's turn end, a report, a
  background task) re-sends all of it, cached or not; a fresh one is about
  70k.
- The full suite ran about three times a batch (15-22 min each): the
  baseline, the batch's final run, the gate's rerun on the merge.
- The Opus batch session cost 400k-770k a batch (UP4 765k, FX7 547k, CD5
  468k) with Sonnet already doing the edits: maps, notes, briefs, a review
  per slice, checks, changelog, and its own context re-sent on every slice.
- Stranger's reviews cost 190k-260k each.
- Batches built side by side each took main+1 as their version and had to
  re-merge.

## The changes

1. **A fresh orchestrator each day.** At the end of its day it writes a
   hand-off note of at most 40 lines (the lanes, each running session's name
   and id, gates waiting, Jerus's open questions), and the next day's
   session starts from that note, the queue and the changelog's top.
2. **A session ends a turn only when it is done or blocked.** It does all
   its waiting (for a suite, a playtest, its predecessor's queue row) inside
   one long background command, so the orchestrator is woken only by a
   finished report (15 lines) or a question it must answer.
3. **One full suite per batch, at the gate.** No baseline: main was green
   at its last ship, and that gate's log says so. The batch runs `--fast`
   plus the harnesses that name the classes it touched (`docs/harnesses.md`
   has the index; a `batch-check.sh` option that picks them from the diff
   against main would do it). The gate runs the full suite and the timing
   hard; a red there goes back to the batch.
4. **The version set at the gate.** A batch writes no version number:
   its changelog block and notes say `next`, and `ship.sh` sets
   `GameVersion.VERSION` to the last tag's patch + 1, fills the `next`s in,
   commits and tags. Two batches side by side no longer collide.
5. **A stranger's review only where money, saves or threading change**
   (the bank, bonds, the treasury and funds; `DataSave`, `HistorySave`, the
   load path, `SAVE_FORMAT`; threads and the UI-wide). Otherwise the gate
   reads the diff's stat and the notes.
6. **Fewer, larger Sonnet slices,** and the Opus batch session does not
   re-read what the brief sent Sonnet to read; it reviews the diff.
7. **Sonnet as the whole batch session for a small fix batch** (no money,
   saves or threading, a few hundred lines), Opus at the gate only. This
   amends "Which model" (2026-09-21).
8. **A shorter CLAUDE.md.** Every layer, each slice included, loads it
   whole (about 15k tokens). Move "How a batch is done" and its detail into
   a `docs/batch.md` the orchestrator and batch sessions read; leave in
   CLAUDE.md the standing rules, the token rules and the pointers.

9. **The orchestrator answers what it safely can** (Jerus, 2026-10-23):
   a session's question about the process, the code or a reading of a brief
   it answers itself. What it sends to Jerus is only his: how the game
   plays, a standing rule, a ★ call, money he spends.
10. **A screen-check session** (Jerus, 2026-10-23): when a batch needs a
    screen looked at (renders, the game on slot 10, never slots 1-9), it
    asks the orchestrator, which starts or reuses one session whose only job
    is that. It takes a list of what to look at, returns what it saw and the
    images' paths in 15 lines, and edits nothing. One at a time, on Sonnet
    unless a check needs judgement.

## Done when

CLAUDE.md, the scripts and `docs/deepseek.md` say the above; `ship.sh
--dry-run` sets a version on a branch that wrote `next`; `batch-check.sh`'s
new option runs the right harnesses on a sample diff; the memory notes on
the PC agree. The first batch after it reports its token cost against the
figures above.
