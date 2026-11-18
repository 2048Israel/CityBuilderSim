# LB5-B - a slot's previous city kept whole while its header and history say two months (2026-11-18)

Row: QUEUE.md, "LB5-B" (from Pending on 2026-11-16; the call was waiting). Brief: `docs/queue/LB5-notes.md`, "The pending call
(LB5-B), with a recommendation": while a slot's header month and its history's last month disagree, the Rankings board keeps
the slot's previous city (its old header and its old history together), not the new header over the old history. Done as
recommended, in the model; one session (Sonnet, no slice: the change is one method and its fixture).

## What I read

`docs/batch.md`, `docs/queue/LB5-notes.md` (the finding) and `LB5.md`, `docs/map/SavedCities.md`, `SavedCities.java` whole (304
lines), `SaveHeader` (its `month` is DataSave's), `RankingsCheck` 21 and its fixture (`Saves`, `copySlot`, `rewriteSave`),
`Game.nextMonth`'s autosave comment (0.7.52: "the counter and every month-end record agree") and `HistorySave.recordMonth`
(`month.add(game.getMonth())`), to be sure a healthy slot's two months are equal.

## What I found

- The window is `SaveWriter.Job.write`'s order, the save then its map then the history: read between, or after a history
  write that failed for good (LB3-S), the slot is `SaveHeader` (name, money, land, people by band) over the old city's
  `HistorySave` (months, figures). `SavedCities.refresh` built exactly that into a `Rankings.City` and kept it.
- A healthy slot's two months are equal: every save path writes the file after `recordMonth()` (0.7.52's comment lists
  them: autosave, before and after skipping, on quit, on close, the numbered slots), and a history cut for a replay
  (`cutAt`) ends at the save's month. The fixture's saves read 42 and 42, 57 and 57. The autosave (slot 0), the one
  place pre-0.7.52 files disagreed, is not read by the board. So a pair that disagrees is a save whose history is owed.

## What I decided

1. **The mend is in `SavedCities.refresh`, in the model, not in the screen** ★. New `private static Entry keepWhole(Entry old,
   Entry read)`: a read that is a city (`kept != null`) whose `Holding.isWhole()` is false (`headerMonth != lastMonth`; the
   header's month is a new field of the in-memory `Holding`, from `SaveHeader.getMonth()`), over an `old` entry that is a
   whole city, is replaced by `new Entry(slot, newStamp, old.kept(), null)`. `RankingsScreen`, every `ui/` class and so
   `KeptCheck`'s rules for the tab are untouched: the screen reads `SavedCities.now/at/stoppedAt/left` as before.
2. **The held entry takes the NEW stamp** ★. The brief: "read the slot again only when a file moves (the stamp already does
   that)". With the new stamp a refresh with nothing moved reuses the held entry (reads 0); the history landing moves the
   stamp and the slot is read again, whole this time. The cost: a history that never lands (LB3-S) leaves the row at the
   last whole city until the next file moves, which is the recommended behaviour and the one change of what the board shows.
3. **Only a whole entry is held** ★ (`old.kept().isWhole()`). A slot first seen with its months apart is read as it stands,
   as the brief says (no whole city of it exists to keep). And a second save over a held slot (history still owed) keeps
   the FIRST whole city, not the mixed city the first save's read would have made: the held entry is whole, so the chain
   holds.
4. **Only a city is held** ★. A read that is not a city on the board (a newer or older build, damaged, no history) or an
   empty slot (the save deleted) is taken as it is, so `left()` and the empty slot stay true to the disk; a held city does
   not outlive a deleted save or one the board cannot take.
5. **The count of reads** is the old rule (a slot whose stamp moved is parsed and counted), so `reads()` is 1 for a read
   that ended in a held city: the harness asserts it.
6. **No save change, no model change**: nothing new is saved (`Holding.headerMonth` is memory, read from a field of the
   header the save already holds), `SAVE_FORMAT` unchanged, `OLDEST_LOADABLE_FORMAT` unchanged. `SavedCities` is read by the
   Rankings board alone: no `Game`, `SimulationEngine` or month-order line moves, **the default trace is unchanged: no ensemble**.

## The harness

`RankingsCheck` (what 21 and 22 prove is in its header, "LB5-B"):

- **21**, the two steps with the row held between them: the fixture precondition (a whole slot's header month is its
  history's last month, both slots); the save ahead of its history (a new name and the month a year on, the history
  still to the old month) leaves the row the very `Rankings.City` it was (same object, name, month) and reads no more
  while nothing moves; the history landing ends it at the new header's name and the longer history's month; the landing
  fixture (SaveWriter's order, the old and the new city told apart by name) holds the old city between the two files and
  the finished slot's name, month and people at the end.
- **22**, new: a slot first read with its months apart is read as it stands; a whole slot whose save lands and whose
  history never does keeps its very same city through a refresh, in `stoppedAt` (the old name), in the in-play marker
  (the held founding) and in `left()`; a second save over it keeps the same city; the history landing ends it as the
  finished slot reads (name, month, people); a healthy re-save is read as it is; a newer-build save over a whole city
  is left off and counted and a deleted one empties the slot.
- The fixture causes the disagreement itself: copies of the game's own saves in `GameFiles.scratch` folders, the header
  (and name) edited by `rewriteSave` and the history copied from the other slot; slots 1-9 of the real folder are never
  touched. Asserted against the model's own objects and the fixture's `youngLast` / `oldLast`, `GameVersion.SAVE_FORMAT`.
- **Fails with the mend removed**: with `keepWhole` returning `read` at once, RankingsCheck is red in 8 places (21: the
  row ahead, "no more", between; 22: failed, a refresh later, "stopped at", in play, a second save), 0 with it.

## Checks (the final build)

`./batch-check.sh --fast` green (compile, maps, Stale 0 firm, tree). `RankingsCheck` green (exit 0, 11 s). `HistoryCheck`
green. The pick (`sh tools/pick.sh origin/main HEAD`): HistoryCheck KeptCheck RankingsCheck TimingCheck, not ALL, so no dry
run; review "not needed - no money, saves or threading in the diff". SaveFileCheck and ReadPathCheck are not picked: no
saved field is touched. **KeptCheck** did not finish: under the machine's night load it hit AllChecks' own 1740 s cap
inside its section 87 (the Rankings tab's); sections 85-87, the tab's, all OK to that point; its one FAIL is main's known red
(`docs/notes/gate.txt` line 80 and `known-reds.txt` 47, KRT1). **TimingCheck** not run here: hard timing is the gate's and
nightly's and the machine runs an ensemble; the diff touches no month path.

## Batches queued

None. Left as it is, by design: nothing tells the player a row is held (the board shows the last whole city as it was
saved; a history that never lands is LB3-S's, and the next successful save lands both files).

## Kept dry-run log

None: the pick is not ALL, so there is no dry run.
