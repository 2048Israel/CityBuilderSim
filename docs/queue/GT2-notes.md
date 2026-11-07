# GT2 - A timing red main also misses re-runs nothing ({next}, 2026-11-07)

The row (`QUEUE.md`, Jerus 2026-11-07: "Yes, queue it"): `ship.sh`'s paired
rounds ran for every red timing harness, even when main itself misses the same
budget, so a gate spent 20-80 minutes concluding the red was not the batch's
(PD1's: three rounds, 78 minutes, for SaveSlotCheck, MapCheck and KeptCheck).
A process batch, lane B after SS2 (RL1: cut from SS2's ship file).

## What it does

- **Each gate records the merged main's figure of every miss** in
  `docs/notes/gate.txt`, as `# figure` lines (a `timing.tsv` row and its place
  among its key's): the merge's median over the rounds when its harness ran
  them, else its hard figure, and only while it still misses the budget.
  `tools/timing-pair.awk`'s judge writes them (`-v recout`); `ship.sh` keeps
  them under the `# timing red not the batch's` lines. A `#` line, so never a
  recorded red.
- **The next gate the same day judges a timing red against them first.** A
  miss whose figure there (origin/main's `gate.txt`, its `# date:` today)
  misses the budget too, with the merge's hard figure at most the margin (15%)
  over it, passes with no round: `harnesses` leaves out a harness whose every
  miss is so covered, and the judge says `main 32.8 ms (gate.txt, today),
  batch 27.9 ms (-14.9%), budget 25 ms: main over the budget too, no round`.
  Anything else (another day's record, no record, over the margin, a harness
  with one uncovered miss) is timed in rounds exactly as before.
- `docs/orchestrator.md`, The gate 6, says so in 23 lines (it had 25);
  `ship.sh`'s header steps 9 and 10 likewise.

## Replayed on 11-07's nine gates

Each gate's hard SaveSlotCheck figure against the gate before's merge median:
within +13.5% every time (R0d 32.7 vs 30.7, FX9 33.7 vs 29.7, PD1 27.9 vs
32.8). XR5 (00:31) had only the day before's record: rounds. R3b, R0e, FD1 and
R0g (SaveSlotCheck their only red) would have run **no round at all**; AN2,
R0d, FX9 and PD1 would have run them without SaveSlotCheck. What still costs a
gate rounds: an intermittent MapCheck or KeptCheck miss whose medians come in
within the budget (PD1's), since its record holds no miss; KeptCheck is 9-12
minutes a run, most of a round. Not queued: no call of Jerus's on it.

## ★ calls (each taken as recommended)

- ★1 **The skip needs both: main's figure misses the budget, and the merge's
  is within the margin of it.** The budget alone (the brief's first wording)
  would pass a batch twice as slow on a chronic miss unseen; the record is
  hours old, not paired, so a figure 15% over it is the rounds' to judge. On
  11-07 it costs nothing (above).
- ★2 **The record is the last gate's, the same calendar day; not the
  nightly's.** The nightly runs at night on a quiet PC and lives outside the
  repository; a gate's figure is measured beside the lanes, as the next gate's.
- ★3 **SaveSlotCheck's 25 ms budget is left alone** (a harness's premise; with
  the record it no longer costs rounds).
- ★4 **Coded here, no slice:** about fifty lines in three files already read
  for the design; a brief that pinned them down would have been the code.

## The first gates after it

GT2's own gate runs main's `ship.sh` (the copy, TM3 item 7) with the new awk:
no record is passed, so its rounds run as before and it writes no figure. The
first gate after GT2 writes the first figures; the one after it the same day
is the first to skip.

## Checks

`sh tools/ship-selftest.sh`: 112 of 112 passed, with four new cases (the figure
recorded at TM's ship; the same miss the same day passes with no round; one
over the margin is timed and stops; another day's record is not read). One old
case moved with it: "a 30% slower one the next time still stops" now has a
hard figure of 53 ms, as its rounds have, since at 45 ms main's figure of the
day covers it. `TimingCheck` (section 9 runs the awk) green; `--pick` below.

**One in, one out:** `git diff --numstat origin/main` over `ship.sh`,
`tools/timing-pair.awk`, `tools/ship-selftest.sh` and `docs/orchestrator.md`:
105 added, 96 removed (net +9): the awk's header condensed (its TM3 and SP1
history to a line each), step 6 two lines shorter, `--red-ok`'s usage note,
which repeated step 9, three lines shorter.
