# GF1 - the gate fails safe

The first step of the workflow redesign (Jerus, 2026-11-11: "Yes, that split":
Codex builds this one). `ship.sh` is the one publisher, and on 2026-11-11 it
published after it was stopped. Nothing here changes what a gate checks or what
it lets through: the timing rounds, `--red-ok`/`--red-line` and `--pick` stay as
they are. Those are decisions Jerus is taking separately, and their batch comes
after this one, on the same files.

## What went wrong, with its evidence

1. **A stopped check published.** LM1's gate, 2026-11-11: the orchestrator
   stopped the run during the hard timing step (its `batch-check.sh` processes
   killed 16:31:18-20, the 11th of 18 timing harnesses running); `ship.sh`
   committed at 16:31:24, tagged 0.9.94 and pushed. Main's `gate.txt` reads
   `# timing: none`. `run_check` (`ship.sh:409-412`) writes the exit status to
   `$T/code` only when the command ends and never clears it first, so the
   killed timing run left the suite step's status there, which
   `case "$CHECKED" in 0|3|4)` accepts; line 488 then copied the suite's
   `reds.txt`, which a `--timing` run rewrites only at its end. The trap at
   line 188 did not fire: on Windows the app's stop ends the task's shell, not
   `sh.exe ./ship.sh`.
2. **The branch had moved.** LM1's session took its ship file off at 16:01,
   seven minutes into the gate, and pushed a newer commit at 16:29. The gate
   shipped the commit it had merged at 15:54.
3. **The launcher hides the status.** Each gate is run by a hand-copied script
   outside git, `%TEMP%\cbs-gates\gate-<id>.sh`, whose last line is
   `./ship.sh ... > ship-<id>.log 2>&1; echo "exit $?"; date +%H:%M; grep ... | tail -16 | cut -c1-260`:
   its own exit status is `cut`'s, always 0.
4. **A hung harness holds the gate.** CD9's gate, 2026-11-08: KeptCheck died of
   `OutOfMemoryError` at 13:11, its JavaFX thread kept the JVM up, and the gate
   sat 62 minutes until it was killed by hand. `AllChecks.java:218` waits with
   `p.waitFor()`, no limit.
5. **The stop names too few lines.** `ship.sh:475` and `:555` print the unnamed
   red lines through `head -12`. WG2's gate (2026-11-10) had 97; the send-back
   named three harnesses from the twelve, and WG2 then found sixteen.
6. **No gate leaves a record.** The clone and its log go at the push, so the
   time each step took, the rounds and the stopped runs had to be rebuilt from
   session transcripts.

## What to build

- **Fresh results.** `run_check` removes `$T/code` before it runs and stops
  ("the check did not finish") when none is written. The same for every file a
  later step reads from an earlier one (`target/checks/reds.txt`, `suite.txt`,
  `timing.tsv`): cleared first, or proved to be this step's own.
- **A stop that stops.** However a run ends early (a signal, a killed child, a
  closed shell), nothing after the checks happens: no `gate.txt`, commit, tag
  or push. Before step 12 `ship.sh` proves that each stage it needed finished
  in this run, from a list it writes as it goes, and names any that did not.
- **The branch as gated.** Before the commit, origin's `batch/<id>` must still
  be the commit that was merged and still carry its ship file; otherwise stop
  and say which changed.
- **The launcher in the repo.** `tools/gate.sh <id> [ship.sh's options]`: the
  fresh clone under `$TMP`, the two data files named from the main checkout,
  `ship.sh` run with its whole output in a log kept outside the clone, the last
  lines printed, and **`ship.sh`'s own exit status returned**. It replaces the
  copied temp script. The `--red-line` list still comes on its command line, or
  from a file it is given, until the decision on known reds lands. It also
  stops a running gate by its whole process tree, on Windows too (find
  `sh.exe ./ship.sh` by its command line), and says that main and the tags are
  where they were. MemWatch's Stop button will call that.
- **A record of every gate.** Each line `ship.sh` prints carries its time, and
  the launcher keeps every run's log (never deleted with the clone), stopped
  runs too. It is what the redesign is measured against until MemWatch's state
  folder exists; write no JSON yet.
- **A limit on a harness.** `AllChecks` ends a harness that runs past a dial (a
  `static final` with its sentence; a multiple of the longest harness's usual
  time, KeptCheck about 435 s) together with its descendants
  (`ProcessHandle.descendants()`), and reports it as a failed harness whose
  line says "timed out after N s". Ending the descendants is KD1's first item:
  say so in the ship file, so that its row loses that part.
- **Every unnamed line kept.** The count on the screen and all of the lines in
  a file the launcher keeps beside the log. The screen may still show twelve.
- **`AGENTS.md`**, its last paragraph: Codex (OpenAI's coding agent, in its app
  or through `codex exec`) is a builder here. It builds, runs the checks and
  pushes a `batch/<id>` branch as `docs/batch.md` says. `openai-dontread.md` is
  the ChatGPT connector's brief alone and does not bind it.

## The checks

- `tools/ship-selftest.sh` (a throwaway origin, a stub `batch-check.sh`, no
  JVM) gains the cases that pass wrongly on main today: a check killed mid-run
  after an earlier stage passed; a stale `$T/code`; the branch moved during the
  gate; the ship file withdrawn during the gate; the launcher's status on a
  stopped gate. Each must end with main and the tags unmoved.
- A harness section for the limit (grep `docs/harnesses.md` for what reads
  `AllChecks`; KnownRedsCheck is the nearest): a child that never ends is ended
  at the dial and counted failed, and one that ends in time is untouched.

## Done means

`sh tools/ship-selftest.sh` and `sh tools/pick-selftest.sh` pass, and
`./batch-check.sh --pick` exits 0 or 3. A real `tools/gate.sh --dry-run` of
this branch runs to its end; a second one, stopped during its timing step with
the launcher's stop, leaves nothing committed and returns non-zero. One in, one
out (`CLAUDE.md`): where this makes text untrue (`ship.sh`'s header, "The gate"
4-7 and "On the PC" in `docs/orchestrator.md`), shorten that text and give the
count in the notes. The ship file last.
