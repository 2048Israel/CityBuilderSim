# TM2 - the ship gate's timing waits for a quiet minute

Jerus, 2026-10-20, at the review desk: "Queue a small fix" (TM1-C), and
DeepSeek's script with it. Tooling:
no game change, no version bump, shipped untagged.

## The gap (TM1-notes, Left undone; QUEUE.md's TM1-C)

`--timing` (`batch-check.sh` and `Batch check.bat`) waits, 15 s a look and
up to 30 minutes, until `tools/jvms.ps1` counts no other batch's JVM, then
runs the timing harnesses alone and hard. A runner that starts its JVMs one
after another (the ensemble's `runpc.sh`, `AllChecks` itself) leaves a gap
of a second or two between them; a look that falls in the gap starts the
hard run, and the runner's next JVM runs beside it. TM1's own gate did just
that on 2026-10-18: it waited two minutes, then ran beside FR2's ensemble.
It was green, but a hard red so placed may be load.

## What to build

- Both scripts start the hard run only after a dial's worth of empty looks
  in a row (one sentence above it: four looks of 15 s, a quiet minute); a
  look that counts a JVM starts the count again. The 30-minute cap stays,
  and its line says it ran without the quiet minute.
- The line they print while waiting says how many quiet looks it has.
- `ship.sh` calls `--timing` as it does.
- **DeepSeek's `tools/deepseek/agent.py`** (Jerus, 2026-10-20: "Add it to
  TM2"): its harness runs time hard with no look at the machine. It runs
  them as the check scripts do, soft while `tools/jvms.ps1` (or `ps`
  elsewhere) counts another batch's JVM or the game, and says so in its
  output; a Sonnet subagent's runs are told the same in its brief.

## Done when

On the PC, `Batch check.bat --timing` and `./batch-check.sh --timing`
started beside a runner of short JVMs one after another (a scratch loop,
not in the tree) wait until the loop ends, then run; started alone they run
after the quiet minute. The suite green but what was already red.
