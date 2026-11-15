# UP6b — The autosave: UP6 review follow-ups

Built from main `a309bf7f` on `batch/UP6b`, 2026-11-15. The four follow-ups are
in QUEUE's UP6b row and UP6's notes, especially sections 2 and 4. No economic
phase or saved field changes; no format bump. No known-reds entry names UP6b.

## Built and calls

- **★ Keep the existing `ranThrough` flag for both exits.** It now survives
  `simulateMonths()` as a Game field, starts true for a founded or loaded city,
  and is the skip-end flag as before. Both `saveForClose()` and `saveForQuit()`
  call `saveForExit()`, which consults it once before asking for an autosave
  and always waits for the writer. Consuming the UI's skip-failure message
  does not erase this guard. On this main, Quit also saved unconditionally;
  repairing both prevents their rules diverging. `UserInterface.stop()` still
  selects the real city during replay and delegates to the model.
- **★ Carry the snapshot's month through the writer.** `Written` holds the job's
  `DataSave.getMonth()` with its slot and result; `reportSaves()` hands that
  month to the inbox. The writer reads its detached job, never the live city's
  moving month. Both autosave and numbered background callbacks use this record.
- **★ Correct the words, retain the disk behavior.** SaveWriter still attempts
  the history even when the save fails. The failure notice now says the save
  and history may be from different months and explains why. It no longer
  promises a whole earlier autosave. No SaveWriter or write ordering changes.
- Corrected `lastSaveResult`'s comment (the review's Game.java:17159 in the
  UP6 merge): synchronous `save()` sets it before returning; autosaves and
  numbered background saves reach it through `reportSaves()`. The player's
  callback receives the result on the writer thread without waiting for that
  report. A prose correction has no runtime pre-change failure; 7f.4 causes
  and verifies the behavior the corrected comment describes, without a
  source-text assertion.

## Fixtures and checks

SaveSlotCheck **7f**, all on `GameFiles.scratch("saveslot-7f")`; the numbered
background fixture uses slot 10 in that scratch folder. No real save folder
or slot 1–9 is used by the new fixtures.

1. 7f.1 uses the existing settlement probe to throw in the second month of a
   real three-month skip, before its history is recorded. It proves one month
   completed and the next is partial, consumes the UI's failure message,
   closes, then quits: every file's SHA-256 stays unchanged and the save and
   history still hold the pre-skip month. Reload, step and close prove the
   guard resets for a whole city.
2. 7f.2 holds the writer with a directory obstructing the autosave's `.tmp`,
   advances the real city one month, releases, and reports: the notice names
   the job's month.
3. 7f.3 keeps the obstruction and attempts another autosave after that step.
   The old save's exact bytes and month remain while the newer history lands;
   the notice must describe the possible month mismatch.
4. 7f.4 holds a numbered background save, verifies the old last result remains
   through its callback, then verifies `awaitSaves()` reports the new result.

Pre-change experiment: `target/up6b/before.log`, the final fixture compiled
against the unchanged pre-mend Game/Inbox classes: all caused-condition
premises OK, **6 FAIL**, solely the close/Quit preservation assertions, notice
month and disk wording. `target/up6b/after.log`: all assertions OK, **0 FAIL**.
The initial whole-harness run found my history-axis reader used a named series;
corrected it to `HistorySave.getMonth()` and repeated the pre-change experiment,
keeping the same partial-month premise. The result-report fixture passes both
versions because the fourth request is only a comment correction.

`./batch-check.sh --fast`: exit 0, compiled, maps regenerated, 0 firm Stale
findings. `sh tools/pick.sh`: **ALL 205**, Game has 169 readers. Per the batch
instruction no local ALL suite or ensemble is run; the normal fresh-clone gate
will run the widest selection. Named checks run serially (`cbs.parallel=1`).

## Trace, review and gate

The default trace must remain unchanged: LongPlaytest steps complete single
months, the retained skip flag changes no simulation phase, and the other
changes concern failure reporting only. The gate's default trace is still to
be checked; any movement stops this batch and is reported to the orchestrator.

Stranger's Codex review and the retained normal-route dry run are pending.

Named check on final code `ce07a9f4`:
`./batch-check.sh SaveSlotCheck SaveFileCheck InboxCheck` **exit 0**, all three
harnesses OK; SaveSlotCheck's one timing assertion SOFT, not counted. Maps
regenerated, Stale 0 firm findings. Log: `target/up6b/named.log`; detailed
`target/checks/{SaveSlotCheck,SaveFileCheck,InboxCheck}.log`.
