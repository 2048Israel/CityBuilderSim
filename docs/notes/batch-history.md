# The batch process: the lessons and the longer histories

Moved out of `docs/batch.md` (a batch session's checklist) and
`docs/orchestrator.md` (the orchestrator's manual) in BM1 (2026-10-28). The
manuals keep each rule with a one-line WHY naming its incident; this is the
rest of the story, for the day someone wants to change a rule: without it a
tidy-up can bring an old mistake back. Nothing here is a rule, and nobody
reads it for a batch.

## The lessons of 2026-10-23/24

Cited by letter in `docs/deepseek.md` and in the two manuals; verbatim.

- (a) 2026-10-23/24: PT1-M4's reds held the Earth lane about six hours. A
  blocked batch now goes under Pending at once, so its lane continues.
- (b) 2026-10-24: batches finished at 11:25 and 13:53 sat unshipped until
  22:45, because the orchestrator is not woken after many automated turns
  in a row with no message from Jerus. Hence the hourly check-in.
- (c) The real Earth is earth-2.bin since 0.8.41 (ET1, 2026-10-24); a gate
  that day named earth-1.bin by mistake, and the Earth harnesses'
  full-file sections then tested the wrong file.
- (d) 2026-10-24: `batch-check.sh` decides soft or hard once, at its start,
  and the PC's speed can change after a restart. So a timing red at the
  gate is compared with main's own run the same day before it counts as
  the batch's (TM3, 2026-10-27: not minutes later, one run to one: the two are
  timed in turn, in the same minutes, and the median decides).
- (e) 2026-10-24, Jerus: "anything on high effort or lower is banned". A
  medium-effort orchestrator had stalled a lane, clashed versions and named
  the wrong Earth file.
- (f) 2026-10-23/24: batches built side by side each took main's version
  plus one, so their versions clashed and they had to re-merge. The
  version is now set at the gate.

## What the one-line WHYs leave out

- **The ready-branch wait** (GP1): a background Bash command costs no tokens
  while it waits; the notification is the only sign it ended. The hard timing
  run's old quiet minute (TM2) cost up to 30 minutes of a 22-25 minute gate and
  could not make a green run any greener.
- **Timing at the gate** (TM3, SP1): main goes first in odd rounds and the
  batch in even ones because a run straight after a switch of build was the
  slower; HY1's gate stopped on three map lines main itself misses, and SC1's
  on a shifting load, both sides within the budget, then both over.
- **`--red-ok`** (TM3 review): the lines are known before his word, since he is
  asked after a stop with that stop's lines in hand.
- **Costs** (BP1): an Opus batch session 400k-770k tokens, a stranger's review
  190k-260k, the suite about three times a batch; the suite alone about nine
  minutes, 15-22 beside other runs, 800 and 917 s on 2026-10-22.
- **`--pick`'s fan-out** (BP1 call ★1, `CBS_PICK_FANOUT`; `=999` flips it,
  which reopens if more than one of the first four batches bounces at the gate
  on a harness so left).
- **No ship file** (RL1): the old way, with a batch's own version and row, is
  gone; one left on a branch sent back made `day.sh --due` call it ready every
  hour.
- **The first batch after BP1** reported its token cost against BP1's
  figures; BP1-C's Pending row (1) still waits on it, and is now moot.
