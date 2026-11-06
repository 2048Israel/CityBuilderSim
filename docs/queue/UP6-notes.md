# UP6 - the autosave: a skip's end, the window's close, the player's Save off the clock ({next}, 2026-11-06)

**WHERE IT STANDS** (2026-11-06): in progress.

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

(to fill after the review)

## 4. What checks it

(to fill)

## 5. The ★ calls

(to fill)

## 6. Found, left

(to fill)
