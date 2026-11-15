# UP6b — The autosave: UP6 review follow-ups

Built from main `a309bf7f` on `batch/UP6b`, 2026-11-15. The four follow-ups are
in QUEUE's UP6b row and UP6's notes, especially sections 2 and 4. No economic
phase or saved field changes; no format bump. No known-reds entry names UP6b.

## Built and calls

- **★ One retained `ranThrough` guard for every autosave path.** A local
  `completedAll` sentinel records whether a skip completed; its `finally`
  latches `ranThrough` false on failure, before any final snapshot or write.
  `autosaveJob()` alone checks the guard, covering both exits, explicit calls,
  both ends of a later skip and twelve-monthly saves. It stays false even if
  later months complete: they cannot repair the partial month. Only
  `buildWorld()` (new city or load) restores it. Keeping it true during a
  healthy skip preserves that skip's periodic saves. The exits still share
  `saveForExit()` and always wait for the writer. Consuming the UI's failure
  message never clears the guard. `UserInterface.stop()` still selects the
  real city during replay and delegates to the model.
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
   history still hold the pre-skip month. It next requests an explicit save,
   runs Simulate more with a probe that reads the return point before its
   first month, completes that skip and reaches later periodic save dates;
   every path must preserve all files. Reload, step and close prove the guard
   resets for a whole city.
2. 7f.2 holds the writer with a directory obstructing the autosave's `.tmp`,
   advances the real city one month, releases, and reports: the notice names
   the job's month.
3. 7f.3 keeps the obstruction and attempts another autosave after that step.
   The old save's exact bytes and month remain while the newer history lands;
   the notice must describe the possible month mismatch.
4. 7f.4 holds a numbered background save, verifies the old last result remains
   through its callback, then verifies `awaitSaves()` reports the new result.

Pre-change experiment: `target/up6b/before.log`, the initial 7f fixture compiled
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
changes concern failure reporting only. The normal gate's default report will be compared with the kept
R2c report; any movement stops this batch and is reported to the orchestrator.

REV-UP6b-1, reviewing `ce07a9f4`, found an escape through Simulate more's
pre-skip autosave. The initial exit-only guard did not protect that route.
The stale dry run `/tmp/cbs-gate-UP6b-Y4MfPiYx` (branch
`340eaf1b39749a8c8f67840246aee41aef61b7c0`, main
`a309bf7f131a457dab540513000efcafd8ab45b2`) was stopped via
`tools/gate.sh --stop` on the orchestrator's instruction, exit 130, origin refs
unmoved; its recorded ship PID 34808 and detached host PID 44676 were confirmed
gone. The worktree was clean before the delta. This run is not reusable.

Delta `b9d1a337908f34df3b88fa5e147215054108ec69` moves the guard to the
job and latches it until reload, as described above. The added fixtures
produce **4 FAIL** on `ce07a9f4`, all caused-condition premises OK
(`target/up6b/delta-before.log`), and **0 FAIL** after
(`target/up6b/delta-after.log`). Delta fast check exit 0, no firm Stale finding.
A stranger's delta review and replacement normal-route dry run are pending.

Named check on final code `ce07a9f4`:
`./batch-check.sh SaveSlotCheck SaveFileCheck InboxCheck` **exit 0**, all three
harnesses OK; SaveSlotCheck's one timing assertion SOFT, not counted. Maps
regenerated, Stale 0 firm findings. Log: `target/up6b/named.log`; detailed
`target/checks/{SaveSlotCheck,SaveFileCheck,InboxCheck}.log`.

Final delta named check on `b9d1a337`:
`./batch-check.sh SaveSlotCheck SaveFileCheck InboxCheck` **exit 0**, all three
harnesses pass, the one SaveSlotCheck timing assertion SOFT; maps regenerated,
Stale 0 firm findings. Log: `target/up6b/delta-named.log`.

REV-UP6b-2 accepted the guard delta but found that its persistent suspension
was silent to a player who saved by hand and carried on. **★ Keep the guard,
explain its state.** `32916cfaeed0bce5b0257bff2f70e99063c2dfba` adds the
model's `autosaveStatus()` words to the failure report, a standing
`autosave-paused` inbox notice, and Quit's dialog. They say autosaves are off
until the city is loaded again, and that the player can save by hand then
load that save to resume them. Loading settles a saved pause notice. Close
has no dialog; its comment and the exit docs now describe the conditional
save rather than promising one. Longer failure and Quit labels wrap.

The fixture now asserts these words after the real injected failure, checks
that hand-saving succeeds while the warning remains, and loads that manual
save to settle the saved warning before restoring the original autosave.
`target/up6b/words-before.log`: **3 FAIL** against the guard-only code, every
condition reached. `target/up6b/words-after.log`: **0 FAIL**. Fast check exit 0;
serial SaveSlotCheck, SaveFileCheck and InboxCheck are rerunning on this delta.

Final words-delta named check on `32916cfa`:
`./batch-check.sh SaveSlotCheck SaveFileCheck InboxCheck` **exit 0**, all three
harnesses pass, the one SaveSlotCheck timing assertion SOFT; maps regenerated,
Stale 0 firm findings. Log: `target/up6b/words-named.log`.

Orchestrator accepted the final delta in mail
`20261115-120118-385-orchestrator-d644` after reading its shared status words,
paused-notice lifecycle and unchanged guard, and granted the replacement
normal-route dry run. The two stranger Codex reviews and this delta review
are complete; their findings were mended before readiness.

## Final kept dry run — green

Normal route, on the orchestrator's go, from hidden persistent PowerShell host
PID 18716 (which outlived every tool call):
`sh tools/gate.sh UP6b --dry-run`.

- Kept run: `/tmp/cbs-gate-UP6b-sZT3N9s3`.
- Windows: `C:/Users/Jerus/AppData/Local/Temp/cbs-gate-UP6b-sZT3N9s3`.
- Branch: `0f8968358679d2b69197fd85d20baeb9e309b7ec`.
- Main: `a309bf7f131a457dab540513000efcafd8ab45b2`.
- Publisher log: the run's `ship.log`; classified results: `checks/suite.txt`;
  launcher log: `target/up6b/gate-launch.log` in this worktree.
- Gate **exit 0**; suite **exit 3**, **192 harnesses: 186 passed, 6 known red**.
  The normal ALL selection chooses its gate tier itself; no local ALL run.
- Known-red harnesses: MoneyDatesCheck, InvestCheck, MapCheck,
  DeathRecordCheck, PrizeCheck and ResearchViewCheck. **Zero unnamed red
  lines**, no new covers or transfers. SaveSlotCheck, SaveFileCheck, InboxCheck
  and LongPlaytest finish with all checks passed.
- Five timing assertions SOFT (SaveSlotCheck 1, MapCheck 4); three path
  assertions OBSERVED (HealthCheck 1, MapCheck 2), under the normal route.
  Stale: 0 firm findings. `checks.complete` and `checks.sha256` are present.
  The launcher's before/after HEAD and origin refs compare unchanged.

**Default trace unchanged.** Compared the kept `checks/LongPlaytest.log`
against `/tmp/cbs-gate-R2c-AaDf8ZRE/checks/LongPlaytest.log`, the same main
model before the harness-only CB2 merge. The raw diff changes only the elapsed
run header (440s vs 203s), CL3's measured window/run time (3.3s/440s vs
2.3s/203s), and the wall-clock timestamps of two vault-purchase log lines.
All other lines, including every model figure, decision, assertion and money
fixture, match. After normalizing only those timing fields and the JVM-options
banner, SHA-256 is
`5efd1490cd87a84c32b9f711315158b70d83737a74d755de4a6b04cce83a19c1`
for both reports. Local comparison records:
`target/up6b/default-report.diff` and `default-report-comparison.txt`.
No main rerun and no ensemble.

No source or fixture changes after this kept run. Only these notes and the
ship file advance. The provisional marker is removed last in its own commit
and checked absent before the readiness message.
