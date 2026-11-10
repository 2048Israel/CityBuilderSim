# LB5 - LB4's review follow-ups: the city in play and the slot's stamp (0.9.76, 2026-11-09)

The brief is `docs/queue/LB5.md` (the review at LB4's gate, verdict SHIP + FOLLOW-UP). A small fix batch, Sonnet whole
(`docs/batch.md`, 3); no money, saves or threading, so no slice and no `review` step. `--pick` exit 3 (below).

## Decided (2026-11-09)

1. **Same name, same seed, another city: done.** `SavedCities.Played` is `(name, seed, foundingCash)`;
   `Played.of(game)` takes `Game.getFoundingCash()`; `matches` needs the slot's name, seed and founding cash all present
   and equal. The brief said "the first history month or the founding cash, whichever the header already holds"; the
   header holds neither, but the first history month is the same for every city (a new game starts at month 1, and its
   history records from there), so it cannot tell two foundings apart, while `foundingCash` is a key `DataSave` already writes at the top of every
   save (set once at the founding, never changed, and the founding screen lets the player choose it). `SaveHeader`
   reads that key (`Double foundingCash`, `getFoundingCash()`; its field names are DataSave's by rule). **Nothing is
   added to the save**, so no `SAVE_FORMAT` change. A save without the key never matches, as one without a seed does.
   `SavedCities.Holding` carries it and answers `isTheCityIn(played)` for `now`, `at` and `stoppedAt`.
   Harness: `RankingsCheck` 19 gains slot 6 (a copy of slot 1 with `foundingCash` doubled: the same name and the same
   seed, another founding, a re-found) and slot 7 (no `foundingCash`): neither is marked in play, slot 1 and 2 still
   are, and at your age slots 3 to 7 stay as rows (3, 4 and 5 as before).
   A limit, said once: two cities founded on one name, one seed AND one founding cash cannot be told apart by what a
   save holds; only a founding stamp saved with the city could, and that adds to the save. Not done (no call needed:
   the same name, world and cash is the same founding in every figure the board shows).
2. **The stamp misses the header: not a gap in the stamp.** `SaveHeader` is the save file parsed (its own javadoc:
   "this is not a separate file"), so the save's size and modified time in `Stamp` already are the header's; there is
   no further file to stamp. What is true is the window the brief names: `SaveWriter.Job.write` lands the save, its map,
   then the history, so between the save and the history landing a read pairs the new header with the old history (a
   new name over the old city's figures) until the history, the next file to move, lands and moves the stamp. The
   stamp does follow each step. So: `stampOf` carries a javadoc saying so, and `RankingsCheck` 21 (new) proves it
   with real files: the save alone with its size moved (the time put back) and with its time moved (the size
   unchanged) each read the slot again and the row has the new name; the history alone moves it and the row ends at
   the longer history's last month; and a city landing in SaveWriter's order (the save, then the history) is read at
   each step, not again in between, and ends as the finished slot reads (name, last month, people).
   The window itself is left as it is, for the call below (Pending LB5-B). Checked for it: a healthy slot's header
   month equals its history's last month (45 and 45, 62 and 62 in the fixture), so a pair that disagrees is a save
   whose history is owed or failed (LB3-S).
3. **`placeSaved` per redraw: not taken.** The brief said only if the timing harness shows it, and the timing
   harnesses are the gate's. As written `placeSaved` does not call `snap.now(played)` per row: it is built behind the
   cache check (`snap`, month, Now / Your age and the city in play unchanged returns at once), at most three short
   lists (one slot each, nine at most) when something did change. Nothing to measure.

## The pending call (LB5-B), with a recommendation

While the header's month and the history's last month disagree, keep the slot's previous entry (the old city whole,
its old header and its old history) instead of the new header over the old history, and read the slot again only when
a file moves (the stamp already does that); a slot first seen with them disagreeing is read as it is today. Recommended:
the board then never shows a name over another city's figures. Not done here because it is a change of what the
board shows (a slot whose history write failed, LB3-S, would keep showing its last whole city, not its new save), which
the brief did not ask for, and it wants its fixture (RankingsCheck 21's two steps, with the row held between them).

## Checks

`./batch-check.sh --pick` (RankingsCheck, SaveFileCheck, SaveSlotCheck, ScaleCheck, TimingCheck; the two data files named
for a worktree): **exit 3**, the only red ScaleCheck's two known lines (main's, `docs/notes/gate.txt`, lane A's town);
SaveSlotCheck 1's one timing assertion SOFT beside another batch's JVM, uncounted (the gate times it hard). `--timing`
not run, by the night line's rule. No `Game.java` change, the playtest trace is main's: no ensemble.
