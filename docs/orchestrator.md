# The orchestrator's manual

The orchestrator's part of a batch: the day, the gate `ship.sh` runs, and the
sessions and rules around them. A batch session reads `docs/batch.md` instead;
a slice reads neither. `CLAUDE.md`'s standing rules bind every line. Each rule
has a one-line WHY naming its incident; the longer story and the lessons cited
by letter (a-f) are `docs/notes/batch-history.md`. Split out of
`docs/batch.md` in BM1 (2026-10-28).

## The day

**A fresh orchestrator each day** (BP1, 2026-10-23: one two days deep was ~500k
tokens, a fresh one ~70k, and every wake-up re-sends all of it). At the end of
its day it CronDeletes its hourly check-in, then overwrites the PC memory note
`orchestrator-handoff.md`, at most 40 lines: its own session id, the lanes,
each running session's name and id, the gates waiting, Jerus's open questions,
the screen-check session's id. The next one archives the session that note
names, arms its own check-in, then starts from the note, `sh tools/day.sh` and
the top of `docs/notes/changelog.md`, and reads nothing else until a session
needs it.

`sh tools/day.sh` is the morning in about fifteen lines: main's `VERSION`
against the last version tag, main's recorded reds (`docs/notes/gate.txt`), the
next queued ids, the Pending ids, and each unmerged `batch/` branch on origin
as ready (ship file pushed; with the conflicts a merge would meet), waiting
(ship file pushed, but built on a batch main lacks), working, stuck (no commit
for a day) or off-queue.

**The hourly check-in** (lesson b: batches sat unshipped 9 hours on 2026-10-24,
the orchestrator not being woken after many automated turns). A CronCreate job
at minute 7 (`7 * * * *`) whose prompt is: run `sh tools/day.sh --due`, and if
it prints nothing, end the turn with no other call (`--due` prints only the
ready and the stuck branches). It lives in the session that made it, so each
orchestrator arms its own and deletes yesterday's: left, it wakes yesterday's
whole context hourly, and two orchestrators may gate one branch.

**The ready-branch wait** (GP1, 2026-10-27: a gate started a median ~60
minutes after the ship file, because the check-in was what found it). Each day,
once, as a background Bash command (`run_in_background`):

```
sh tools/wait.sh --ship [--skip HELD...]
```

It ends in one line when an unmerged `batch/` branch on origin has its
`docs/queue/<id>-ship.md` (`wait      done - ready to ship: A B (...)`); a
branch built on a batch main lacks does not count. The orchestrator gates each
id named, one at a time, and **arms the wait again after each gate**. `--skip`
names ids it holds with the ship file on (a gate stopped for Jerus's words), or
the wait would end at once. Exit 4 (origin unreachable) is armed again after a
look at why. **The check-in stays**, as the fallback for a wait lost with its
session or a lane stuck. `sh tools/wait-selftest.sh` tries the wait.

**Two lanes, on files of their own**: the next two queued batches side by
side, as sessions, never more than two. **A lane's next batch starts at its
predecessor's ship file, not its merge** (RL1, 2026-10-27: a lane no longer
idles for a gate): when `wait.sh --ship` names PRED, the orchestrator starts
the next batch, whose brief's first step is `git fetch origin && git checkout
-b batch/<id> origin/batch/PRED`, and gates PRED. `day.sh` calls the successor
waiting until PRED is on main; a PRED sent back needs no message to it.

**A blocked batch goes under Pending at once** (lesson a: PT1-M4's reds held
the Earth lane six hours): its Queued row out and a Pending row in, saying
why, in a queue commit pushed to `main` the same turn. Who returns it to Queued
when its block clears by itself is Jerus's call (BP1's call 2; recommended: the
orchestrator, saying so in the queue commit; not built); until he says, only
his review moves a Pending row, save one the next paragraph settles.

**The orchestrator answers what it safely can** (BP1 item 9, Jerus 2026-10-23:
"dont most end up being rec? its only a few that arent"). A session's question
about the process, the code or its brief it answers itself. It takes the
recommended option on a batch's ★ calls, writes in the notes which and why, and
the batch ships without waiting; he can overturn one later. A Pending row that
waits only on calls with a recommendation it settles the same way. It sends
Jerus only what is his: a call with no clear recommendation, one that moves a
standing rule or a premise, one that spends money, an eye check of his.

## The gate

Each ready branch (`sh tools/day.sh` names them; a waiting one goes after the
batch it is built on):

1. Read the diff's stat (`git diff --stat origin/main...origin/batch/<id>`),
   the notes, and the review line of `sh tools/pick.sh origin/main
   origin/batch/<id>`. Check that the batch did what its brief asked, the
   playtest's trace and, when it moved, the ensemble's score against the last
   baseline. A process batch's notes give its line count (one in, one out,
   `CLAUDE.md`).
2. **A stranger's review only where money, saves or threading change** (BP1
   item 5; a review costs 190k-260k tokens): the bank, bonds, the treasury and
   funds; `DataSave`, `HistorySave`, the load path, `SAVE_FORMAT`; threads and
   the UI-wide. Otherwise the stat and the notes are the review. The review
   line names these from the diff and is a floor: add one, never skip one it
   names.
3. Take the batch's recommended ★ calls and say which and why in the notes,
   sending Jerus only his.
4. Then `./ship.sh batch/<id>` does the rest; its header lists the steps:

   ```
   ./ship.sh [--dry-run] [--no-tag] [--version X.Y.Z] [--red-ok "his words" --red-line "<part of a line>" ...] <branch> [-m title] [-F body] [-- harness names]
   ```

   `main` clean and current, its `VERSION` equal to the last tag (or it
   stops); the merge (a conflict only in the generated docs takes the branch's
   side and regenerates them; any other stops it); the records from the ship
   file, before the checks, so that they see the new version (`{next}` is the
   last tag's patch + 1, lesson f); the one full suite (BP1 item 3), timing
   soft, then the timing harnesses alone and hard (step 6); `docs/notes/gate.txt`
   rewritten, by `ship.sh` alone; the commit `Merge batch/<id>: <title>,
   <version>` with a `Gate:` line, the tag, one atomic push. A push refused
   because `main` moved takes the merge and the tag back: run it again.
5. **Reds**. Main's recorded reds pass, word for word (`Gate: known red only:
   <harnesses>`). A NEW red line ships only with `--red-ok "his words"` and a
   `--red-line` for each line he approved, his words and the lines in the
   body. **His words pass the lines they name and no others** (TM3 review,
   2026-10-27: a broad `--red-ok` let another flaky line pass under words given
   for one, CC1-S's gate). So `--red-ok` without `--red-line` is refused (exit
   2); a red line passes only if it contains a `--red-line` part, as written
   (a fixed string); a harness's own `exit N` and `N FAILED` lines pass with a
   named line of it, when no other line of it is left unnamed; any other red
   stops, its lines marked `NOT NAMED`. Both the suite's and the timing step
   read it. After a stop, show Jerus its red lines; for each he approves, copy
   a distinctive part into `--red-line`, e.g. `./ship.sh --red-ok "ship it"
   --red-line "EarthTerrainCheck | exit 1" batch/X`, and run again. Any other
   red goes back to the batch as its failing lines (`target/checks/reds.txt`
   in the gate's clone), its ship file off first (`docs/batch.md`, 5).
6. **Timing** (lesson d; TM3, 2026-10-27: a wall-clock budget measured once on a
   PC that also runs Jerus's game measures the load as much as the code; HY1's
   gate stopped on three map lines main itself misses). A timing red is judged
   side by side with main. `ship.sh` takes its figure from
   `target/checks/timing.tsv`, which every timing assertion writes through
   `Timing.verdict(ok, label)`, never from the label's prose, and times the red
   harnesses again with main's classes (a worktree of `main`) and the merge's in
   turn, back to back, **three rounds** (`CBS_TIMING_ROUNDS`, at least two), main
   first in odd rounds (SP1: a run straight after a switch of build was the
   slower). It counts against the batch only if its median is **more than 15%**
   over main's (`tools/timing-pair.awk`; `CBS_TIMING_MARGIN` moves it), main's
   median within the budget and the batch's over it; not when main's median
   misses the same budget too, or the batch's is within it (SP1 item 7: SC1
   stopped twice on a shifting load, both sides within the budget, then both
   over). Otherwise it passes, named in the merge's `Gate-timing:` line with
   both medians and why, and in `gate.txt` only as a `#` line, so it never
   becomes a recorded red. A red over the margin with main within the budget and
   the batch over it, one with nothing to compare (main has no such assertion, a
   round did not run) and one that is no clock's (a failure behind no missed
   figure) stops - unless `--red-ok` and a `--red-line` name it (step 5), said in
   `Gate-timing:` and `gate.txt` `#` lines; a timing red not named stops even
   with `--red-ok`. Wall-clock comparisons that are not a budget are `Timing`
   assertions too (TimingCheck 6): soft in the suite, judged here. **The hard run
   starts at once** (GP1: TM2's quiet minute, a wait of up to 30 minutes for no
   other JVM, is gone, because this comparison shares one load by design).
7. `--version X.Y.Z` only on Jerus's word. `--dry-run` runs everything, the gate
   included, prints what it would commit, tag and push, and keeps nothing;
   harness names after `--` only with it (a partial suite never ships). A
   docs-only merge (every path ends in `.md`, none under `docs/ensemble/`) gates
   on `--fast`: no suite, no timing, `gate.txt` untouched. `ship.sh` runs from a
   copy of itself (TM3: a shell reads its script as it goes, and the merge of a
   batch that changes it rewrites the file). After a change to `ship.sh` or
   `tools/timing-pair.awk`, `sh tools/ship-selftest.sh` (a throwaway origin and
   a stub `batch-check.sh`; no JVM, about two minutes); after one to
   `tools/wait.sh`, `sh tools/wait-selftest.sh` (about 20 seconds).

**Before saying a change works:** the gate's suite is green but for main's
recorded reds (say which: `docs/notes/gate.txt` and the merge's body), the maps
are regenerated, the ensemble scored if the trace moved, the write-up exists,
the docs pass has run, and the batch's row is out of `QUEUE.md`.

## The screen-check session

BP1 item 10 (Jerus, 2026-10-23). When a batch needs a screen looked at -
renders, or the game on slots 7-10 - it asks the orchestrator, which starts one
session whose only job is that, or reuses the one running (its id is in the
hand-off note). It takes a list of what to look at, returns what it saw and the
images' paths in 15 lines, and edits no source. One at a time, on Sonnet unless
a check needs judgement. **Slots 7-9 are its to write, even over a city of
his**, and slot 10 as before; slots 1-6 it never writes. That is `CLAUDE.md`'s
standing rule, for this session alone: every other session keeps off 1-9.

## On the PC

Claude Code, NetBeans beside it: the same steps. Build with NetBeans *Clean and
Build* or `mvn -q compile`. Sessions check with Git Bash's `./batch-check.sh`,
which alone has `--pick` and the exits 3 and 4; `Batch check.bat` is Jerus's
double-click (`--maps` regenerates the maps too; without it it writes nothing
git tracks, because the PC's copy pulls `main` and must stay clean). Batches
check side by side here, alongside Jerus's game, which nobody waits on him to
close: `batch-check.sh` times every run soft but the gate's `--timing`, and
`ship.sh` judges a timing red side by side with main's classes (TM3).
`Batch check.bat` times soft only while another batch's JVM or the game runs,
and decides **once, at its start** (lesson d).

A batch shipped from the PC ships from a temp clone of `origin`, never the
checkout: `%TEMP%\cbs-<id>` (the scratchpad's path is past Windows' 260
characters), `JDK_JAVA_OPTIONS` naming the PC's `data\earth-2.bin` (lesson c),
in Git Bash:

```
JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin ./ship.sh batch/<id>
```

Once shipped, delete the clone and every other `%TEMP%\cbs-<id>*` folder the
session made (`rm -rf "$TEMP"/cbs-<id>*`, from outside them): 24 left behind
held 5 GB by 2026-10-21 (TD1). The full suite is

```
java -cp "target/classes;%USERPROFILE%\.m2\repository\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar" ham.citybuildersim.AllChecks -f
```

(about nine minutes alone, 15-22 beside other runs; `AllChecks money bank` runs
two).

## The Earth file

The real Earth is earth-2.bin since 0.8.41 (ET1; lesson c: a gate named
earth-1.bin and tested the wrong file), at `data/earth-2.bin` beside the game
(`EarthData.DATA_FILE`; `data/` is git-ignored, and a batch's worktree has
none). The full-file sections of EarthCheck, EarthTerrainCheck,
EarthFoundingCheck, EarthDepositsCheck, GlobeCheck, GatewayCheck, LandCheck and
MapCheck run only where it is named (`-Dcbs.earth=<path>`, through
`JDK_JAVA_OPTIONS` for the suite) or lies at `data/earth-2.bin`, and say so in
one line where it does not; `batch-check.sh` adds a note when they did not run,
and `gate.txt` says whether the gate named the file. `LongPlaytest --earth
rotterdam` plays the run on the real Earth; its trace is a baseline of its own.

## Other assistants

Since 2026-10-13: ChatGPT (the GitHub connector: reads and writes, runs
nothing) and Gemini (AI Studio: a shell, git, Java and Maven) work on branches
`chatgpt/<topic>` and `gemini/<topic>`, never `main`, by their briefs
`openai-dontread.md` and `gemini-notes.md`. A Claude session ships such a
branch with `./ship.sh --no-tag <branch>` after its review (no `{next}`: their
branches take no version, BP1); what they report as run is checked, not taken.
The review goes to the `assistant-review` subagent (`.claude/agents/`) or a
fresh session, never a long conversation (RV1). `ship.sh` runs `tools.Names` on
their branches and prints the names their text uses that the tree does not
declare: a warning, not a gate. DeepSeek is not one of these: no branch, no
brief, a Claude session drives it (`docs/deepseek.md`).

## Which model

Jerus, 2026-09-21: the implementer and the docs pass run on Opus, whatever the
job's size; the orchestrating session keeps the gate. Amended by BP1 item 7
(2026-10-23): a small fix batch (`docs/batch.md`, 3) runs on Sonnet as the whole
session, Opus at the gate only; an Opus batch hands its coding to Sonnet
slices; the screen-check session runs on Sonnet unless a check needs judgement.
Lesson e (Jerus, 2026-10-24: "anything on high effort or lower is banned"; a
medium-effort orchestrator stalled a lane, clashed versions and named the wrong
Earth file): every orchestrator, batch session, review agent, slice and
screen-check session runs at xhigh or max. A session cannot raise its own
effort, so one started below xhigh is handed to a fresh one at xhigh.

## The docs pass

By a fresh agent, at most once a day (2026-10-12), over everything merged since
the last one, on its own branch; `docs/docs-pass.md` is its brief, `Docs
pass.bat` runs it headless, `docs/docs-pass-agent.md` is it as a subagent. It
makes every comment, header, index and count agree with the code, by a context
that did not make the change. Its mechanical half is `tools.Stale` (StaleCheck,
in the suite); the pass is for what a tool cannot judge. A batch is not done
until it has run. A manual edition only at a milestone or when Jerus asks.
Since BP1 it ships `--no-tag`, with a ship file for its changelog block named
from its branch with each `/` a `-` (on `docs/pass-2026-10-26`,
`docs-pass-2026-10-26-ship.md`; `ship.sh` stops any other) and no `{next}`; a
pass that changes only `.md` files gates on `--fast`. `Docs pass.bat` runs a
bare `claude -p`, which takes the CLI's default effort: check it before
trusting a headless pass (lesson e).
