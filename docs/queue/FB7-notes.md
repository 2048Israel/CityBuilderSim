# FB7 — counters read under load (SS1, GR2-R), 2026-11-17

Built on main 886bd92dc (0.9.138) on `batch/FB7`. Harnesses, the probe and the map's worker count; the model, the saves and
the playtest are main's, so no ensemble (the default trace cannot move: nothing the month runs changed). No save slot written:
every city and file is in a `TempDirs` / `GameFiles.scratch` folder.

## Read

`CLAUDE.md`, `docs/batch.md`, QUEUE.md's FB7 row, `docs/queue/FB7.md`, GR2's "historical approvals and follow-ups",
`docs/notes/known-reds.txt` (3 entries under FB7), SS2's and CM1b's notes, `SaveSlotCheck.heldOpen`, `GameFiles.swapIn`,
`SaveWriter`, `ReplayCheck` 5d and `movedChecks`, `ReplayMoveProbe.measureGlide`, `MapView`'s `MoveTally`, `loadStep` and
`WORKER`, and the 124 kept `SaveSlotCheck.log`s in the gate folders (11-11 to 11-17). No kept log of either red survives; the
approvals came from the launchers' carried `--red-line` lists (46 copies, 11-08 to 11-11).

## SS1: SaveSlotCheck 7d's swap counter

- **What it was.** The two lines first went red at RT1's gate (11-05) from 7d's old release thread timed on the wall clock;
  SS2 (11-07) tied the release to the swap's own refused try, and all 123 kept logs since show both lines OK. What GR2 filed
  stays true of the fixture, though: `GameFiles.swapsThatWaited()` and `GameFiles.onSwapRefused` are the whole program's. A
  swap that waits and lands on any other thread inside 7d's window moves the counter, and its refused tries call 7d's seam,
  which then records their count and lets go of 7d's reader on *their* second try.
- **Caused by hand** (`target/fb7-driver/Fb7CrossTalk`, never shipped): 7d run while the driver hands the shared save writer a
  job whose file it holds open, released 30 or 200 ms on. Main's 7d: red in 36 of 40 runs (10/10 and 10/10 idle, 6/10 and 10/10
  beside a 20-thread CPU burner); both counter lines red, and the seam's "refused exactly that many times" too.
- **Now.** 7d settles the shared `SaveWriter` (`awaitIdle()`), holds it (a job handed in meanwhile waits for 7d's end), counts,
  and releases it in a `finally`; the seam returns at once on any thread but 7d's. A new fixture line asserts the writer wrote
  nothing while 7d counted. The counter assertions, the write that lands, the refused write and the intact file are unchanged.
  Same driver on FB7: 0 of 40 red. Why not a per-thread counter in `GameFiles`: the save writer is the only other writer in
  the program, and holding it keeps the fix in the harness.

## GR2-R: ReplayCheck 5d's worker asks

- **What it was.** `MapView` counted a glide frame's asks as `WORKER.getTaskCount()` at the paint minus the same at the camera
  step. The JDK calls `getTaskCount()` an approximation: it is completed + running (the worker's lock held) + queued, so a job
  the thread has taken off the queue and not yet locked is counted nowhere (-1), and one just finished is counted twice until
  the unlock (+1). A job still draining from the load when the glide runs, on a starved MIN_PRIORITY thread, makes that
  moment long: asks -1 with nothing asked, and a real ask can be hidden by a later -1. The load step's "this paint asked
  nothing" and the probe's follow-up read the same count.
- **Caused in a fixture** (`ReplayMoveProbe.inTransit()`, in 5d): a `MapView.Worker` whose queue stops the thread between
  taking a job and beginning it, by latches. `getTaskCount()` reads 2 then 1 across that moment, every run.
- **Now.** `MapView.WORKER` is a `MapView.Worker`, a `ThreadPoolExecutor` built as before that counts each job in `execute()`
  (which `submit()` goes through), and the tally, `loadStep` and the probe read `asked()`. It only grows, so no delta can be
  negative. `MoveTally.asksBelowNothing` counts frames whose asks did, and 5d asserts none for each glide. 5d's zero-asks line
  is unchanged. **Outside the brief's file list:** `ui/MapView.java` (the counter and the tally; no model, no playtest).
- **Main under load:** main's ReplayCheck, two copies beside the burner, stayed green (asks 0 both glides, both copies). That
  run never had a job in flight during a glide frame, so the fixture is the red-before.

## Checks

- `./batch-check.sh --fast` exit 0. `./batch-check.sh SaveSlotCheck ReplayCheck` exit 0 (the one SOFT line is 7c's median
  due month, main's in `gate.txt`); `./batch-check.sh ReloadCheck TimingCheck` exit 0. Stale 0 firm.
- Under load (two copies at once beside a 20-thread burner, `-Dcbs.timing=soft` as the suite): ReplayCheck 4 of 4 green
  (fixture -1 / 0 / +1 each run; both glides asks 0, below nothing 0); SaveSlotCheck 4 of 4 "All checks passed", 278 OK each,
  7d's waited try 3 and 0 writes while counting. Logs: `target/fb7-proof/` in this worktree.
- `sh tools/pick.sh origin/main HEAD`: ReloadCheck ReplayCheck SaveSlotCheck TimingCheck. Review: threading.
  `MapView.Worker` changes nothing about the jobs: one thread, same queue and factory, same order; `execute()` adds one
  `AtomicLong` increment before `super.execute()`, read on the FX thread. `inTransit()` builds its own Worker on its own daemon
  thread, never `MapView.WORKER`; its latches wait 10 s at most and a `finally` lets go and shuts it down. 7d's hold is
  released in a `finally`, and its seam is cleared in one as before.

## Known reds

The three entries filed under FB7 (SaveSlotCheck's two counter lines, ReplayCheck's "(asks -1, unpainted frames 0") are
this batch's to retire; no entry is added or moved, and no batch is queued.

## The tag

The orchestrator's call (15:53): versioned, not "no tag (harness)": `ui/MapView.java` ships in the game, so the build gets its
own version though the model and the playtest are unchanged. The ship file's heading carries `0.9.139`; the dry run is the
gate's normal route without `--no-tag`. REV-FB7-1, a stranger's threading review of e5fcfb922, runs beside it.

## The dry run

`sh tools/gate.sh FB7 --dry-run` (versioned, 0.9.139 = 0.9.139), kept at `/tmp/cbs-gate-FB7-Gss1vBX6`: branch 3e300b15e on
main 886bd92dc, exit 0. The whole suite ran (MapView is a game class) in 3,171 s, exit 3, known red only: DeathRecordCheck,
DistressBleedCheck, HealthCheck, MapCheck, PlanCheck, PopulationCheck, PriceLevelCheck, RoadCheck, ScaleCheck, YoungBankCheck,
none of them FB7's. SaveSlotCheck and ReplayCheck had no FAIL line, FB7's three entries retired; 14 timing lines SOFT; maps
regenerated, Stale 0 firm, the tree as it was.

## Review (REV-FB7-1, on e5fcfb922): SHIP

The worker's tally is sound, the probe's latches are bounded, 7d's hold cannot deadlock, and nothing in the model reads them.
Three minors, none blocking, left as they are:
(a) 5d's "asks below nothing" line cannot fail now. It is a tripwire against a return to getTaskCount(); the transit fixture
is the red-before.
(b) The second transit line folds `transit.ended()` into its condition without naming it in the label.
(c) Older than FB7: `MapView.loadStep`'s stuck test, `getActiveCount() == 0 && getQueue().isEmpty()`, has the same gap for
a job taken and not yet started. That is harmless while STUCK_S asks for a sustained idle.
