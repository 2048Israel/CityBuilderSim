# BM1 - the batch manual split by reader, and one in, one out (no tag, 2026-10-28)

The batch's record beside its brief, `BM1.md`. `docs/` and `CLAUDE.md` only: no
code, no version, no trace, no ensemble. Run whole on Sonnet 5.5 at xhigh, Opus
at the gate only (BP1 item 7).

## 1. What changed

1. **Split by reader.** `docs/batch.md` is now a batch session's checklist (10
   numbered steps: the maps, no baseline suite, the build order, `--pick`, the
   ship file, the ensemble, the turn and the lane's successor, the report, the
   write-up, one in one out). `docs/orchestrator.md` is new: The day, The gate
   (1-7), The screen-check session, On the PC, The Earth file, Other
   assistants, Which model, The docs pass. `CLAUDE.md`'s table and "How a batch
   is done" name both and say who reads which.
2. **A one-line WHY per rule**, naming its incident (date, batch). Every rule
   TM3, GP1, RL1 and SP1 added is kept (`--red-ok` needs `--red-line`; no quiet
   minute and `wait.sh --ship`; a lane's next batch from its predecessor's ship
   file; the suite three at a time, a timing red passing when main also misses
   it or the batch stays within budget, alternating rounds). The long stories
   and lessons (a)-(f), verbatim, are `docs/notes/batch-history.md`; the
   lessons are still cited by letter.
3. **The report** is "ready, see the ship file", the ★ calls each with its
   recommendation, and the checks' exit and red lines only if not green
   (`docs/batch.md`, 8). The "first batch after BP1 reports its token cost"
   clause is gone with it.
4. **One in, one out** is a standing rule in `CLAUDE.md`, and `docs/batch.md`
   10 and `docs/orchestrator.md` The gate 1 carry it to the session and the gate.
5. **The notes and the ship file stay two files** (`docs/batch.md`, 5, says why:
   `ship.sh`'s heading check reads the ship file alone).
- Citations repointed: `AGENTS.md`, `README.md`, `CLAUDE.md`, `docs/deepseek.md`
  (two), `docs/docs-pass.md` (two), `docs/docs-pass-agent.md`,
  `docs/notes/pending.md`. The cites to "A batch session", the ship file's
  shape and the ensemble rule (`ship.sh`'s `die` messages and header, `BL1.md`,
  `FR8.md`) stay true. Shipped batches' notes cite the old sections: a record,
  left.

## 2. One in, one out

Lines: before / after. `CLAUDE.md` 251 / 259; `docs/batch.md` 499 / 132;
`docs/orchestrator.md` 0 / 266; `docs/notes/batch-history.md` 0 / 56. **What a
session reads: an orchestrator 750 / 657 (CLAUDE.md, batch.md,
orchestrator.md), a batch session 750 / 391 (CLAUDE.md and batch.md alone).**
With the history file, which no batch reads, 750 / 713. `git diff --numstat
origin/main`, this batch's notes and ship file left out: **479 added, 511
removed, 32 fewer** (`docs/batch.md` 112 / 479, `orchestrator.md` 266 / 0,
`batch-history.md` 56 / 0, `CLAUDE.md` 28 / 20, the citations 14 / 12).
`CLAUDE.md` gains 8: the rule and the longer two-file pointer, less a shorter
"Coding goes to Sonnet" (its slice sizes are `docs/batch.md` 3).
Dropped without a replacement: the lessons section's repeat of each WHY, the
`-Xms` and held-JVM detail of the parallel suite (`docs/dials.md` has the
dials), ship.sh's thirteen steps retold in The gate 4 (its header has them),
and BP1's figures paragraph.

## 3. Calls

- **★1 Two script comments still cite the old sections**: `ship.sh:73`
  ("docs/batch.md, The gate 6") and `tools/wait.sh:55` ("docs/batch.md, The
  day"), now `docs/orchestrator.md`'s. Editing a script makes the merge not
  docs-only, so `ship.sh` would run the full suite and the timing harnesses for
  two comment lines. Recommendation: leave them, repoint with the next batch
  that touches either script. **Taken: left**, as a Pending row (BM1-C), which
  the orchestrator may settle.
- **★2 BP1-C's Pending row, item (1)** (the first batch after BP1 reports its
  token cost) is long moot and its clause has left the manual. Recommendation:
  drop that item at Jerus's next review. **Taken: the row is left alone**, his.
- **For the PC's hand-off note** (the orchestrator's memory): the sections it
  cited in `docs/batch.md` are now `docs/orchestrator.md`'s, same names: "The
  day", "The gate", "The screen-check session", "On the PC", "The Earth file",
  "Other assistants", "Which model", "The docs pass". `docs/batch.md` keeps "A
  batch session" 1-10 as its one body.

## 4. Checks

`./batch-check.sh --fast`: exit 0, Stale 0 firm (150 soft; none names these
files but an old `README.md:8` sector count).
