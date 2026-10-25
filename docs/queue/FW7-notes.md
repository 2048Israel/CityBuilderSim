# FW7 - a known red told from a NEW one, in AllChecks as in the scripts (no tag, 2026-10-25)

The brief: `QUEUE.md`'s row, from `docs/notes/todo-archive.md` (FOUND ON THE
WAY IN 0.7.42 TO 0.7.45): at 0.7.42 HealthCheck's upkeep check went red
inside a harness that was already red, and `AllChecks -q` showed only
"HealthCheck FAILED", the same as the day before. A per-label baseline of the
known reds would catch it. This past week the orchestrator's gate filtered
the known reds by hand with grep and miscounted once, reading a label with
"FAILS" in it as a failure.

## 1. What was there

BP1 (2026-10-25) had built most of the baseline already: `docs/notes/gate.txt`,
written by `ship.sh` at every gate and by nothing else, holds main's red
lines, one `Harness | <line>` each: the harness's exit, each failing line as
`AllChecks -f` prints it (the assertion's label and its FAIL), and its count.
`batch-check.sh` read the red lines of its own suite log the same way
(`reds_of`) and compared them word for word (exit 3 when all are recorded, 4
with the lines that are not). So the scripts already caught the HealthCheck
case. `AllChecks` itself knew nothing of the record: `-f` printed the failing
lines with no sign of which were main's, and `-q` threw every harness's output
away, so it could not know.

## 2. What changed

- **`AllChecks`** reads the record under `-q` and `-f`: `docs/notes/gate.txt`,
  or the file `-Dcbs.knownReds` names. After a failed harness's time it says
  `known red` when every red line is in the record, else `NEW red: N of M red
  lines`; `-f` tags each red line `[known]` or `[NEW]`; a line under the
  summary names the harnesses of each kind (`Against <file>: known red only -
  MapCheck; NEW red - ...`). `-q` now keeps each output in
  `target/checks/NAME.log`, as `-f` does, so it has something to judge. A red
  line is made by the same rules as `reds_of` (`AllChecks.redLines`). With no
  record nothing is tagged and `-f` prints as before. The plain mode and the
  exit status (the number of harnesses that failed) are unchanged.
- **`batch-check.sh`** writes the record it judges against
  (`target/checks/known-reds.txt`) before the suite and hands it to
  `AllChecks`, with `-Dstdout.encoding=UTF-8`, so that a non-ASCII label reads
  the same to Java and to awk on the PC, where a piped JVM writes Cp1252.
  `reds_of` strips the tags, so the red lines, `reds.txt` and what `ship.sh`
  records are as before. The verdicts say `suite     known red only - ...`
  and `suite     FAILED - NEW red: N line(s), the known reds aside:`, exit 3
  and 4 as before. A new guard: when the log carries `AllChecks`' verdicts,
  their N must add up to the script's own count of new lines, or the run
  fails (exit 1) saying the two judges disagree. A new mode, `--judge LOG`,
  gives the verdict on a saved `-f` log without building, running or locking
  anything, from the log's own folder; only there does `CBS_KNOWN_REDS` name
  another record.
- **`ship.sh`** words its gate the same: `Gate: known red only: <harnesses>`,
  `NEW red on Jerus's word: ...`, and its stop names NEW red lines.
  `tools/ship-selftest.sh` greps the new words (its 81 cases pass).
- `CLAUDE.md`, `docs/batch.md` (a batch session, 4; the gate, 5), README's
  harness count and the tree's.

## 3. The self-test: KnownRedsCheck (new, in the suite)

A fixture harness of its own (`KnownRedsCheck.Fixture`), started as
`AllChecks` starts a harness (`AllChecks.command`, its own JVM, its output to
a log), fails one assertion and, when asked, a second; between them a passing
line whose label says FAILS.

1. The first run's red lines are its exit, its failing line and its count:
   three, no section title, not the passing FAILS line. They are written as
   `ship.sh` writes `gate.txt`: a `#` line, a blank, the lines.
2. The same red again reads as a known red: one line ending `known red` under
   `-q`, every red line tagged `[known]` under `-f`.
3. The run with the second assertion: the header says `NEW red: N of M`, N the
   lines not in the record, the second assertion's line is tagged `[NEW]` and
   the first still `[known]`.
4. With no record nothing is tagged: `-f` prints exactly the lines it printed
   before FW7.
5. `batch-check.sh --judge` on the same logs: exit 3 and `known red` for the
   first, exit 4 with `NEW red` and the second assertion's line for the
   second, exit 3 for an untagged log (from before FW7, which `gate.txt`'s
   lines came from), and exit 1, "disagree", for a header whose count was
   made wrong. Where `sh` cannot be started (NetBeans on Windows) it says so
   in one line and counts nothing; the suite run by `batch-check.sh` has it.

41 assertions, about five seconds.

## 4. Decisions

- **The baseline is `gate.txt`**, not a second file: it already names each
  known red by harness and by its failing line, which carries the
  assertion's label; only `ship.sh` writes it (a batch branch that changes it
  never ships), so it cannot be edited to let a red through.
- **Word for word, as the gate is.** A known red whose figures moved (a label
  with a number in it, after a model change) reads as NEW. ★ Recommendation:
  keep it so. Masking digits would also let a different assertion of the same
  shape through ("month 12" for "month 13"), and the gate's own record is
  word for word; the timing step's digit-masked comparison with main's run
  stays where it is.
- **The two judges must agree.** `AllChecks` and `reds_of` both make the red
  lines; the guard turns a drift between them into a failed run, not a quiet
  wrong verdict either way. A log whose header cannot be read gets the one
  line it says instead as a red line of its own, so a broken log is NEW, never
  known.

## 5. Checks

The model is untouched: no trace, no ensemble. `./batch-check.sh KnownRedsCheck`
OK; `./batch-check.sh MapCheck` exit 3, `suite     known red only - MapCheck
(docs/notes/gate.txt)`, its log's header `FAILED - exit 1 (188.3s) known red`
and both red lines `[known]`. The whole suite (`./batch-check.sh`, earth-2.bin named, timing soft beside
another batch's JVM and the game): 142 run in 1048 s, 141 passed, exit 3,
`suite     known red only - MapCheck (docs/notes/gate.txt)`, MapCheck 8's
network line being main's one recorded red; maps regenerated, Stale 0 firm.

## 6. Left

- `Batch check.bat`, Jerus's double-click, has no verdict line of its own: on
  a red it types `suite.txt`, which now carries the tags and the `Against`
  line. AllChecks with neither `-q` nor `-f` prints the harnesses' output as
  it goes and judges nothing.
