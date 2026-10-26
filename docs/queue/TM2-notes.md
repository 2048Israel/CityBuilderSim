# TM2 - the ship gate's timing waits for a quiet minute (no tag, 2026-10-25)

The batch's record beside its brief, `TM2.md`. It is tooling: no game
change, no version, no trace moved, no ensemble. Drafted by Sonnet 5.5 at
xhigh as one subagent slice, reviewed and finished by Opus.

## 1. What was there

`--timing` (the gate's hard timing run, in `batch-check.sh` and `Batch
check.bat`) waited, 15 s a look and 30 minutes at most, until one look of
`tools/jvms.ps1` counted no other batch's JVM. A runner that starts its JVMs
one after another (AllChecks, the ensemble's runner) leaves a gap of a second
or two between them. A look that fell in the gap started the hard run, and
the runner's next JVM ran beside it: TM1's own gate did that on 2026-10-18,
beside FR2's ensemble. The two scripts each had their own loop.
`Batch check.bat --pick` took `--pick` for a harness name, ran 0 harnesses and
said All clear (BP1). DeepSeek's `agent.py` ran a harness hard with no look
at the machine.

## 2. What changed

- **`tools/jvms.ps1 -Quiet`** is the PC's one wait, used by both scripts. It
  looks every 15 s and ends at the fourth empty look in a row; a look that
  finds another batch's JVM starts the count over. At 30 minutes it ends all
  the same, exit 3, and its line says it runs "without the quiet minute".
  It prints a line only when something changes (the first busy look, a
  count started over, the end), as it happens, through a pipe too. Without
  `-Quiet` it prints `<batch> <game>` as before. `CBS_JVMS_MARK` narrows
  every look to the command lines that carry a mark (the self-test's).
- **`batch-check.sh --timing`** runs it on the PC, its lines passed through
  as they come, and the same loop in sh over `ps` elsewhere
  (`quiet_minute`). `others()` moved up beside it.
- **`Batch check.bat --timing`** calls it directly (not through `for /f`, so
  its lines show live). An unknown `--option` is refused, exit 2.
- Both scripts take `CBS_TIMING_WAIT_ONLY=1` with `--timing`: the wait
  alone, before the compile, its status as the exit. Only the self-test
  sets it.
- **`agent.py`** takes one look just before a harness JVM and times it soft,
  saying so on the result's first line, while another batch's JVM or the
  game runs. `docs/deepseek.md` tells a Sonnet subagent's brief the same.
- `docs/batch.md`, `docs/deepseek.md`, `docs/tree.md` and a comment in
  `ship.sh` say it. `ship.sh` calls `--timing` as it did.

## 3. The proof

`sh tools/quiet-selftest.sh` (new, about three minutes) runs a stand-in JVM,
`Nap.java` as a single-file program whose command line names
`ham.citybuildersim.QuietSelftest` (or `.CityBuilderSim`, for a fake game),
in loops with gaps, and tries each waiter: `batch-check.sh` through the
hook, `jvms.ps1 -Quiet`, and `Batch check.bat` through a wrapper `.cmd`. It
covers the one look ("1 0", "0 1", the javapath shim's parent not counted
twice), alone (exit 0 after the quiet looks, no waiting line), beside a
runner whose gaps are longer than a look (not ended before the runner's
last JVM, the count started over at least once), the cap (exit 3, "without
the quiet minute"), the game (not waited for and said; waited for with
`CBS_QUIET_GAME=1`) and the .bat's refusal of `--pick`. 17 of 17, 168 s.

Then the real gate, `./batch-check.sh --timing`, started beside a scratch
loop of five 10-s JVMs 10 s apart (and another lane's JVM). A look at about
20:58:47 fell between two of the loop's JVMs and counted quiet, and so did the
next; at 20:59:18 a look found the loop's fourth JVM and started the count
over. The quiet minute came at 21:00:18, 35 s after the loop's last JVM ended, and the
timing harnesses ran alone: 5.5 minutes, exit 3, MapCheck only (main's
recorded red, FW11's). TM1's one empty look would have started the hard run
at about 20:58:47, beside the loop's last three JVMs.

## 4. Decisions

- ★1 **The game is not waited for.** By default a look counts it, says it
  runs, and does not hold the hard run for it. Its timing red is judged
  against main's own run, beside the same game (`ship.sh`, lesson d), and the
  game is often open for hours (it was during this batch): waiting for it
  would cost every gate its 30-minute cap and gain nothing. `CBS_QUIET_GAME=1`
  on the gate's command waits for it as for a JVM. One switch in each wait.
  The orchestrator was told.
- ★2 The quiet minute is four looks, so the hard run starts about 45 s after
  the first empty look, not 60 s. The brief's "four looks of 15 s" is kept
  as the dial.
- The wait lives in PowerShell, not in each script, so that the gate's
  scripts on the PC run one loop and the self-test tries that loop. Off the
  PC the sh loop mirrors it, with the same dials, words and exits.

## 5. Checks

`./batch-check.sh --pick`: exit 0 (compile OK, no harness picked, maps
unchanged, Stale 0 firm). `sh tools/quiet-selftest.sh`: 17 of 17. The real
`--timing` above: exit 3, main's recorded red only. `sh -n` on both scripts.

## 6. What this week taught

- **A check decides soft or hard once, at its start:** a suite that began in
  a quiet gap counted a later miss as hard. BP1 made every `batch-check.sh`
  run soft but `--timing`. `Batch check.bat` still decides once, so a run
  of it that began alone stays hard. The gate decides once too, after the
  quiet minute, and a runner that starts after that runs beside it: the
  comparison with main's own run is the answer there.
- **Jerus's game counts as load:** the orchestrator's gate waited for every
  CityBuilderSim JVM, the game included. TM2 says the game in a line and
  waits for it only on `CBS_QUIET_GAME=1` (★1).
- **The PC's speed changed after a restart:** main itself missed several
  budgets that day, so a gate's miss is judged against main's own run, the
  same day on the same machine (`ship.sh`, since BP1).

## 7. Left

- ★1 and ★2 are the calls.
- A JVM shorter than a look that starts just after one and ends before the
  next is not seen: in the real run the loop's fifth JVM (10 s) was missed.
  The four empty looks still held the hard run until 35 s after it ended.
- Two gates waiting at once would start at the same quiet look. Only the
  orchestrator gates, one at a time.
- The sh loop off the PC was tried under `dash` with stand-in `uname` and
  `ps`, not on a real Linux. `agent.py`'s `ps` branch was tried with a
  stand-in only.
