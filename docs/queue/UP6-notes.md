# UP6 - the autosave: a skip's end, the window's close, the player's Save off the clock (0.9.32, 2026-11-06)

**WHERE IT STANDS** (2026-11-06, 13:10): ready, see the ship file. The slice in and reviewed (cf683672); the full suite
exit 3 on the batch (§4); KP2b and R4 (0.9.30) merged in from main (55150026) and `--pick` on the merge exit 3, main's
recorded MapCheck red only.

The batch's record. `QUEUE.md`'s UP6 row and [UP6.md](UP6.md): Jerus's calls on UP1's ★4, ★1 and ★2 (2026-10-23:
"Autosave at the skip's end", "Yes, autosave on close", "Background + 'Saving...'") and LB3-S (2026-10-31, "Take all
(Recommended)"). Lane 2 (screens), last in it, built on `origin/batch/KP2b` at its ship file (eab86dba). Opus wrote the
brief, reviewed the diff and committed; one Sonnet 5.5 slice at xhigh wrote the code. No model change, no saved field, no
format bump; the default playtest steps single months (`LongPlaytest.run`, `simulateMonths(1)`), which write no skip
autosave, so its trace cannot move: no ensemble (EN1).

## 1. The brief, item by item

| item | how |
|---|---|
| 1. an unfloored autosave at a multi-month skip's end | `Game.simulateMonths()`: after the end's `awaitSaves()` (every autosave its months queued), `autosave("after skipping N months")`, asked for and not waited for (the fill and snapshot, about 5 ms at month 4,000 by UP1's probe). Not after a skip that threw: its city stops part-way through a month, and the "before skipping" autosave stays the point to come back to. The new autosave restarts the floor, as every autosave does, so the running clock keeps it. The comments that said a skip inside the floor ends with the one from before it are rewritten (the brief's "about 6087" is now the `finally` of `simulateMonths` and the banner THE AUTOSAVE OFF THE CLOCK). |
| 2. the window's close autosaves like Quit | `UserInterface.stop()` (the platform calls it as the last window goes) calls `Game.saveForClose()`: `autosave("on close")` then `awaitSaves()`, so the files are whole before the JVM ends. The real city, never a replay's copy (`ReplayMode.realGame()`), as Quit leaves the replay first. |
| 3. the player's own Save on the writer, a "Saving..." line | `Game.saveInBackground(slot, name, done)`: the save filled and snapshotted on the FX thread as an autosave's is, written on the writer's; returns at once. The Save page shows "Saving..." and the file it goes to, and its lines are set in place when the write lands (`fillSaveResult()`); "Saved." and the path, or the failure, as before. |
| 4. a history that could not be written is told | `SaveWriter.Job.write()` returns the save's result with the history's beside it (`GameFiles.Result.withHistory`, `historyError`, `whole()`); the player's page says "Saved, but its history could not be written: <reason>" (`Result.outcome()`), the autosave's is an inbox notice (`Inbox.autosaveWritten`), and the .bak is kept (nothing deletes it; the page and the notice name it). |

## 2. The threading, for the gate's review

**Which thread writes.** Every save the player can cause is written on `SaveWriter`'s one thread (`save-writer`, not a
daemon), one file at a time, from a snapshot taken on the FX thread (`prepareSave(slot, name, true)`: `DataSave.snapshot()`
and the history's owed copy, GT1). The FX thread only fills. `Game.save()` (and `saveGame()`) still writes on the calling
thread with the live objects after `awaitSaves()`, for every caller that wants the files on disk when it returns (harnesses,
benches, probes); the window no longer calls it.

**What waits for what.**
- A second Save while the player's last is still in the writer: `saveInBackground` calls `awaitSaves()` first when
  `playerSavesInWriter > 0`, so the two are written in the order asked; without it the writer would replace the waiting
  first job (a newer job for a file replaces the waiting one) and its `done` would never be called. Going back to the
  Save menu waits too (`showSavingMenu()` now `awaitSaves()` first, as Load's menu does), so its slot rows are the files
  on disk.
- Load's menu: `showLoadMenu()` keeps its `awaitSaves()`; a load (`loadGameSave`) waits at its start as before.
- Quit: `saveForQuit()` = `autosave("on quit")` + `awaitSaves()`, then `System.exit` - waits for a player's Save in flight too.
- The close: `stop()` -> `saveForClose()` = `autosave("on close")` + `awaitSaves()`; the JVM ends after `stop()` returns.
- A skip: `awaitSaves()` before its "before skipping" autosave, again at its end, then its own end autosave, not waited for.
- The player's `done` runs on the writer's thread, after the three files are swapped in; the window hands it to the FX
  thread with `Platform.runLater`, keyed by a ticket so an older Save's landing never overwrites a newer one's page. The
  result reaches `getLastSaveResult()` on the FX thread at the next `reportSaves()`.

**The slots.** The writer writes a numbered slot only for the player's own Save there: an autosave's job is
`GameFiles.AUTOSAVE_SLOT`, a recording's is the recordings folder, and `saveInBackground` refuses the autosave's slot and
anything past `SLOT_COUNT`. SaveSlotCheck 7e.3c holds it: a Save to slot 5 changes slot 5's files and no other file in the
saves folder.

## 3. The slice

One Sonnet 5.5 slice at xhigh (`target/deepseek/brief-UP6.md`), reviewed as a stranger's and committed (cf683672). It
touched `GameFiles` (+32/-6), `SaveWriter` (+9/-4), `Game` (+110/-25), `Inbox` (+28), `ui/UserInterface` (+123/-28),
`ui/ReplayMode` (+3), `SaveSlotCheck` (+341/-9) and one label of `ui/UiProbe` (its numbered-save line no longer calls the
sync save "the Save button"). Mended in review, by Opus: the skip's end autosave keyed on a flag the `catch
(RuntimeException)` set, so an `Error` mid-month would have autosaved a city stopped inside a month over the "before
skipping" one; it now needs every month to have run (`ranThrough`, set after the loop). Two banners cited UP1's "star 3"
for "Background + 'Saving...'", which was star 2.

The result page is a menu screen, built by `clearMenu` as the menu's screens still are since KP7 (`Kept` draws the
rail's screens; the clock never redraws a game menu). It is built once on arrival, and when the Save lands its three
lines (outcome, file, advice) are set in place by `fillSaveResult()`, the one method the build calls too, with every
value set on both sides of its condition: the kept rule's one code path, without a pool for a page drawn twice in its
life. A Save that failed, or saved without its history, opens its page even for a player who has left it (★1); one that
landed whole is not forced on him.

What else leaned on the old way: the slice grepped the harnesses for a read of the autosave's file, or a count of
`SaveWriter.writes()`, right after a multi-month skip with no wait between. Three, all in SaveSlotCheck (7's 30-month
skip, 7b, 7c.5), now `awaitSaves()` first. Every other harness that skips either saves through the sync `save()`, which
waits first, loads (which waits), or never reads the autosave. The default playtest and DeterminismCheck step single
months.

## 4. What checks it

SaveSlotCheck, UP1's section 7, holds each of the brief's fixtures on a young city whose save clock stands still:
- **7e.1** a 3-month skip inside the floor ends with its own last month on disk, named "Autosave - after skipping 3
  months", whole; the counter starts again and twelve single months with the clock still write nothing.
- **7e.2** a 2-month skip and at once the close (`saveForClose()`): the autosave is the close's at the city's month;
  with the writer held the close returns only after it is let go.
- **7e.3** the player's Save returns with the writer held, slot 4 still empty; let go, it lands ok and whole, and the
  slot is then named and at the city's month with its whole history. **7e.3b** a second Save while the first is held
  returns after the release with the first already written (2 writes, the slot named for the second). **7e.3c** a Save
  to slot 5 changes no other file in the saves folder; the autosave's slot and one past the last are refused.
- **7e.4** with slot 6's history `.tmp` a folder (7c.6's obstacle; not 7d's held file, which races under load), the
  Save is ok and not whole, `outcome()` "Saved, but its history could not be written: <reason>", the slot holds the new
  save beside the old history and the `.bak` is the save from before; the sync `saveGame` says it too. The autosave's:
  the inbox's "autosave" notice, titled for the history, then for a failed save, settled by one that lands whole.
- **7b** the autosave at a skip's end is loaded and played on, and the one before the skip, now the end's `.bak` trio,
  loaded the same way from a copy. **7c.5** a skip waits for a held autosave: 3 writes (the held one, the skip's before
  and its end's), the file the end's.

`./batch-check.sh --pick` on cf683672: exit 3, 14 harnesses, main's recorded MapCheck red only (`docs/notes/gate.txt`);
SaveSlotCheck 253 OK, SaveFileCheck 974, InboxCheck 43, ReplayCheck 118, no FAIL; SaveSlotCheck's and MapCheck's timing
lines SOFT (side by side). The full suite (`./batch-check.sh`, all 180 harnesses, on 61dfcfa7): exit 3, nothing red but main's six recorded reds (SlopeCheck, MonetaryCheck, DenominationCheck, ScaleCheck, MapCheck, KeptCheck), each word for word by the suite's judge.

On the merge with main (55150026: KP2b, R4 0.9.30), `--pick` exit 3, 15 harnesses, main's recorded MapCheck red only.

Not run in a window: the Save page's "Saving..." line and the close. **To look at, if anything seems off:** Save to a
slot in a long city (the page says "Saving..." for a moment, then "Saved." and the path); close the window with the X
and open Load: the autosave row says "Autosave - on close" at the month the city stood.

## 5. The ★ calls

- **★1 A Save that failed, or saved without its history, opens its page even after the player left it** (the clock
  stops under it, as under any menu page); a whole one is shown only on its page. The other way: a toast or an inbox
  notice for the player's own Save, like the autosave's. Recommended: as built - he asked for that save, and it is rare
  (he must leave the page within the write, about 0.1 s at month 4,000).
- **★2 An autosave that could not be written at all is an inbox notice too**, not only one whose history failed (the
  brief named the history; until now a failed autosave was a log line, "Autosave failed:"). One standing notice, keyed
  "autosave", retitled as the trouble changes and settled by the next autosave that lands whole. Recommended: as built.

## 6. Found, left

- The map's sidecar that cannot be written is still a log line only (`Job.write`), as before: a load redraws a missing
  or stale map from the save (`CityMap`'s canonical draw), so nothing is lost. No row.
- A notice raised by `reportSaves()` inside a load's `awaitSaves()` goes into the inbox the load then replaces: an
  autosave's trouble found at that moment is in the log only. Rare (a failed write and a load inside one write's time);
  no row.
