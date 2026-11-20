# GB4 notes - the gate box's measurements on record

Jerus, 2026-11-20, of the overnight report on the box: "you should probably put it
on github so that if other sessions ask its there, specifically since im 100% sure
they ll ask about the last one where more actually was faster even tho slower".

## What changed and why

- **`docs/gatebox.md`** (new) covers:
  - the machine and its tuning;
  - the 7-hour soak, every test passed;
  - one job alone, and what each kind of neighbour costs it;
  - many jobs at once: at 16 each is about 40% slower, yet the lot ends about 30%
    sooner than two rounds of 8, and why;
  - the real gate and ensemble timings measured on the box so far.

  The figures are the box's own logs in `~/overnight` (`bench.log`, `para.log`),
  read again for this note: three rounds at each count.
- **`docs/contract.md`**, section 2, gets one line in the gate box's bullet that
  points to it, so a session that reads the contract finds it.

## Checks

- `./batch-check.sh --fast`: exit 0, with compile OK, maps regenerated and Stale at 0
  firm. On the first run, its one firm finding was the note naming a file that lives
  on the box, not in the tree. The line was reworded.
- `tools/pick.sh origin/main HEAD`: NONE of 229 harnesses. No review is needed, since
  the diff has no money, saves or threading.

## Calls taken

1. A note of its own beside `docs/orchestrator.md`, not a study. It is the record
   of the machine that runs the gate, and no prototype goes with it.
2. The contract gets a pointer, not the figures, to keep the contract short.
   CLAUDE.md is unchanged, since every session reads it whole.
3. GB3's ensemble timing is cited as "measured on its branch", because GB3 is not
   on main yet.

## Left for Jerus

- Whether the suite or an ensemble runs better at 16 at once than at 8 on the box is
  not measured yet. Today the suite's 80% CPU guard and GB3's 8 at once decide it.
