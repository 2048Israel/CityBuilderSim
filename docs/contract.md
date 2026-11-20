# The contract: how a batch is built, checked and shipped

Every builder reads this page whole, Claude or Codex, headless or in an app,
and so do the orchestrator and a reviewer. It holds Jerus's standing rules and
the process; `CLAUDE.md` is the map and the tools. A `slice` subagent reads
only its brief and `.claude/agents/slice.md`. Each rule keeps a one-line WHY
naming its incident; the long stories are `docs/notes/batch-history.md`.
WHY (WC1, 2026-11-18): the rules sat in five files and thirty memory notes, and
Codex could read none of the notes. His decisions behind it: `docs/queue/WC1.md`.

## 1. The standing rules

Jerus's; they do not move. A comment that starts `Jerus:` is his brief in his
own words, and it is the requirement.

- **Save slots 1-9 are his cities. Never write them.** Slot 10 is the
  assistant's playtest slot. The screen-check session alone may also write
  slots 7-9, even over a city of his (2026-10-23: "it can do whatever it wants
  with slots 7-9"); slots 1-6 nobody writes. A harness city uses
  `GameFiles.scratch(label)` or a folder from `TempDirs.create(prefix)`, never
  `new Game()` (the real save folder) and never `Files.createTempDirectory`
  (TD1: nothing deleted its folders; 37 GB by 2026-10-21).
- **Ship fixes without asking** ("yes, always"), through GitHub, the one source
  of truth (`2048Israel/CityBuilderSim`): a batch is built in a worktree on a
  branch of its own; the orchestrator alone merges it into `main`, tags it and
  pushes; the PC pulls `main` every ten minutes. Nothing is written to the PC
  by hand. Ask as many questions as you like; do not wait on answers to ship
  what is already decided.
- **Each batch is told once** (2026-10-12): one changelog paragraph per
  version, at most 12 lines; the todo only what is still open; a write-up to
  the project as `claude/<title>.md`, in prose (what was found, what was
  decided), only for a batch that changes how the game plays or leaves calls
  for Jerus. The README's counts are not the record; the changelog is.
- **A batch that is decided is queued the same turn** (2026-10-11), by whoever
  takes the decision; one only in a note, a todo line or a chat is not queued.
  A shipped batch's row is deleted in its merge; what it left undone goes under
  Pending, and only Jerus's review moves a row out of there, save one the
  orchestrator settles (section 6). A batch's name is never reused (childcare
  is CC1, because C1 became the build card's).
- **One in, one out** (2026-10-27: "the beaurcracy has grown way too much"; the
  rules a session read went from 309 to 724 lines in four days): a batch that
  changes the process (the scripts, the checks' machinery, the rules a session
  reads) removes about as many lines as it adds, and its notes give the count,
  `git diff --numstat origin/main` over those files, its notes and ship file
  left out. A rule it adds keeps a one-line WHY naming its incident.
- **A harness is the finding** (`README.md`, *The checks*): a fixture must
  cause the condition; assert against the model's own constants; count how
  often a mechanic fires in a real run; never move a harness's premise to let a
  change through.
- **`Sectors.KEYS` order is load-bearing** (equity index, household share
  arrays, audit pools): a new sector goes at the end. The same for
  `BuildingType` and for ids in `buildings.json`, which saves are keyed by:
  take the next id, never renumber.
- **A flow cannot be reconstructed from the state a month ended in.** If a
  screen or a check needs it next month, save it (`DataSave`, `HistorySave`,
  the load path; `ReadPathCheck` and `SaveFileCheck` catch a field in one and
  not the other).
- **The month is a sequence, not a set.** `Game.nextMonth()` and
  `SimulationEngine.simulateMonth()` hold it; `docs/month-order.md` lists it.
  A line that lands in one phase and not the other is the classic bug here.
- **Old saves need not load until the game is published** (2026-10-10; being on
  itch.io is not that yet, 2026-10-27: "Not yet"; he says when). Write no
  conversion code. A change that would make an old save load *wrongly* (not
  merely incompletely: Gson leaves a missing key alone) bumps `SAVE_FORMAT`
  and raises `GameVersion.OLDEST_LOADABLE_FORMAT` to it in the same batch, said
  in its notes; the loader then refuses the save and names the build that
  still opens it.
- **Every `println` is the log.** There is no separate logging call.
- **Line endings: the repository stores LF** (`.gitattributes`), the `.bat`
  files CRLF. Never convert a file wholesale; `git ls-files --eol` shows no
  `w/crlf` but the `.bat` files.

## 2. Who does what

- **Builders** (Jerus, 2026-11-11: "Yes, that split"). Codex builds whole
  batches, in its app or headless, not slices under a Claude session; Claude
  builds the batches he gives it, reviews, and takes the decisions with him.
  Claude builds on Opus (2026-09-21: the implementer and the docs pass run on
  Opus, whatever the job's size), or on Sonnet as the whole session for a
  small fix batch (no money, saves or threading, a few hundred lines; BP1).
  DeepSeek is never suggested, nor a key asked for, unless he raises the
  budget (2026-10-20: "for a long while there will be no deepseek so its
  sonnet").
- **Effort xhigh or max** for every session, agent, slice, reviewer and
  workflow agent (2026-10-24: "anything on high effort or lower is banned").
  A session cannot raise its own; one started lower is replaced by a fresh one.
- **Sessions** are started and stopped by MemWatch's launcher, headless
  (`claude -p`, `codex exec`): "sessions decide, MemWatch dispatches". How
  many run at once is his setting. Mail is `cbs-bus`
  (`C:\Users\Jerus\Tools\MemWatch\cbs-bus.exe`: `send`, `inbox`, `read`,
  `done`); a session's bus name is in `CBS_ME`.
- **The orchestrator** queues, starts builders, answers what it safely can,
  orders the reviews and the dry runs, and gates (`docs/orchestrator.md`).
- **Other assistants** work on `chatgpt/<topic>` (the ChatGPT connector, which
  runs nothing: `openai-dontread.md`) or `gemini/<topic>` (`gemini-notes.md`),
  never `main`; the `assistant-review` agent reviews such a branch and the
  orchestrator ships it `--no-tag`. What they report as run is checked, not
  taken.
- **The gate box** (GB1, 2026-11-20): `tools/gate.sh` runs on `gatebox`
  (`~/.cbs-gate-box`), started headless as before; when no box answers it
  mails `jerus` and runs here ("notify me then run on laptop"). Its runs are in
  `~/cbs/runs` there, copied home at the end; `--reuse` and a ship file's
  gate-run name the box's run, as `gate: run` prints it. `tools/nightly.sh` too
  runs there, the box held whole, its verdict copied home (GB2). What the box
  is and what it measured, alone and 8 or 16 jobs at once: `docs/gatebox.md`.
- **Still his to decide** (`docs/queue/WC1.md`), unanswered: what the
  orchestrator becomes once MemWatch dispatches; who keeps the gate day to day;
  which of the standing rules stay as they are (he said in the MemWatch session
  that save slots 1-9 "are NOT important"; the rule stands until he drops it).

## 3. Starting a batch

- **The branch.** `git fetch origin`, then `sh tools/batch-id.sh ID` (GG1, PD1:
  a reused merged branch was pushed again), then a worktree on `batch/ID` cut
  from `origin/main` (`.claude/worktrees/cl-ID` for Claude, `codex-ID` for
  Codex). A chain's next batch is cut from main once its predecessor is on main,
  never from its tip (2026-11-11: "After it ships (Recommended)").
- **The brief** names the source files it expects to change and says whether
  it moves the playtest; the prompt names the batches in flight, to keep off.
- **Where it is** goes to MemWatch by `sh tools/bus.sh` (BW1: MemWatch guessed
  it from logs): `state ID building builder=claude|codex model=M session=$CBS_ME
  branch=batch/ID` once cut; `blocked` or `held` with `--note WHY`; ready, 5.6.
- **Which batches run together** (2026-11-11: "by files only, but like in a
  smart way"). Whoever starts a batch lays its files beside those each batch in
  flight has changed (`git diff --name-only origin/main...origin/batch/<id>`)
  and holds it when a file in common is one where the two would get in each
  other's way: the month's flow (`Game`, `EconomyManager`, `Sector`,
  `MoneyAudit`), a harness or fixture builder both change, one screen's class.
  A list every batch appends to (`AllChecks.HARNESSES`, `docs/studies/README.md`)
  holds nothing, nor a large file two batches touch in sections that do not
  meet. Two that both move the playtest may run together on files of their
  own; the second to ship redoes its ensemble on the new main.
- **Held:** HL1, PW1 and LM1-D, with the leftover `batch/LM1`, stay on their
  branches, ungated, until he says ("Leave them for now").
- **Pending rows** that name the batch ("For ID") are settled in its notes.
- **A worktree has no `data/`**, so every JVM there names main's files, written
  `C:/...` (LF1: 25 false NEW reds unnamed; WN4c: 42 with `/c/...`):
  `JDK_JAVA_OPTIONS="-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin"`.
  The Earth is `earth-2.bin` since 0.8.41 (ET1: a gate named earth-1.bin).

## 4. Building

- **Every line read is paid for again on each later call** (2026-10-13). The
  map first (`docs/map/README.md`, the touched files' pages), `Where Name
  -print` for one method; never open `Game.java` or a large screen whole; grep
  `docs/harnesses.md` and `docs/dials.md`, never read them; long output to a
  file under `target/`, then grep it; independent reads in one step; reports
  in 15 lines.
- **Order:** the model, the save, then the screen; a harness proves a mechanic
  before the screen is looked at. An eye check is not pending work: the notes
  say what to look at, and no eye-check row goes in Pending (2026-11-04: "its
  not really pending").
- **Data the game ships** comes from sources that allow commercial use, each
  licence named beside its source (2026-10-31: the game is sold).
- **Slices, a Claude builder's.** Coding a brief can pin down goes to an Agent
  of type `slice` (Sonnet at xhigh, six tools, no `CLAUDE.md`), never the
  default agent (CX1: it began at 62k tokens, 65% of them tool definitions).
  The brief carries each file, each method with its `Where X -print`, the
  harness and any rule beyond the agent's own; about 80 steps, and the rest to
  a fresh slice, not a resume (CX1: steps past 100 held 44% of re-sent
  tokens). The builder keeps what waits on Jerus or decides what the spec
  leaves open, where a line goes in the month, a `SAVE_FORMAT` bump, the
  records, the review, the checks and the commit. It reviews the diff as a
  stranger's without re-reading what the slice read (BP1), the model slices
  most carefully (FR1-FX4: map, drawing and harness slices were nearly always
  right, model slices needed real fixes), for: fixtures from the model's
  constants, a threshold caused from both sides; a premise moved to get green;
  the model's formula copied instead of called; a default or tie biased by
  loop order; a file the brief did not name. If a slice's Edit refuses the
  worktree, brief it to `EnterWorktree` that path (KP7). DeepSeek, when he has
  the budget, takes the same brief through `docs/deepseek.md`.
- **Headless, a builder runs its subagents in the foreground** (`Agent
  run_in_background: false`) and never ends its turn while a subagent or a job
  it started runs: the process exits at the turn's end and kills them (SQ1,
  CU2, 2026-11-16). One Sonnet subagent per session at a time (2026-10-21: the
  commit charge reached 43 of 54 GB and a suite died with CreateProcess error
  1450).
- **Checks while building:** `./batch-check.sh --fast` and the harnesses you
  name (`./batch-check.sh Name ...`), one at a time; `AllChecks -f` gives a red
  harness's assertions, never a full rerun to find one. Never a baseline suite
  of main: main's last gate gives its reds (BP1: a fresh baseline suite repeats
  the recorded gate). While a gate or a dry run is on the machine, run no JVM
  beyond `--fast` (FB8's gate timed out under builders' long runs, 2026-11-17
  23:01). Builders' runs time soft; speed is judged nightly on main
  (`tools/nightly.sh`; 2026-11-11: "Judge it nightly").
- **The ensemble** (`tools/ensemble/ensemble.sh <label>`) runs once on the
  final model only when the default trace moves beyond runtime, against the
  newest `docs/ensemble/` baseline, never a rerun of main; its `baseline.md`,
  naming its data files (EN2), is committed as `docs/ensemble/<id>.md` before
  any dry run. Only M1 (the money audit) and F1 (findings) stop a ship; the
  other scores go in the notes beside the baseline (EI1, HW1's T2 stop). An
  unchanged trace is said in the notes.
- **Records are the publisher's:** a branch never edits `GameVersion`'s
  `VERSION`, `QUEUE.md`, `docs/notes/changelog.md` or `docs/notes/gate.txt`
  (BP1: concurrent builders wrote the same records and version; `ship.sh`
  stops such a branch); they go in its ship file. A builder never pushes
  `main` (a side session that must asks the orchestrator first and pushes on
  its "clear": 2026-10-27, a push during a ship cost a rerun), never runs
  `./ship.sh` for real, and ends only process trees it started, by their ids.
  The project's `claude/todo.md` stays under 100 KB, archived to
  `docs/notes/todo-archive.md` when it passes (2026-10-12, the project's
  `git-workflow.md`, "Keeping usage down": it had reached 735 KB).

## 5. Ready

1. **The notes**, `docs/queue/ID-notes.md`: what was found, what changed and
   why, the checks run and their results, the calls taken with their
   recommendations, what is left for Jerus; a process batch's line count; a
   batch with slices, what the review changed in each.
2. **The ship file**, `docs/queue/ID-ship.md`, the publication records apart
   from the notes, written UTF-8 through Git Bash:

       <!-- readiness: provisional -->
       ## changelog
       ### {next} — ID, title — date, ready, see docs/queue/ID-notes.md
       <one paragraph, at most 12 lines including its heading>
       ## pending
       | ID-X | what remains ({next}) | why | link |
       ## queued
       | <new id: tools/batch-id.sh> | what was decided | brief |
       ## known-reds
       <Harness> | <part of the red line> | <queued fix id> | <date> | <cause>

   Only the changelog is required; a section holds rows, never a header row;
   a known-reds row is one red. An untagged batch's heading has no `{next}`
   (`### ID — title — date, ready, see docs/queue/ID-notes.md`); every heading
   needs its two em-dash separators (GG1: lost bytes reached the record).
   Only the record files and ensemble names take `{next}` (GG1: labels shipped
   with it unfilled). A queued row's first cell is the new batch's own id,
   never the word NEW. `sh tools/known-reds.sh ID docs/queue/ID-ship.md`
   checks the rows.
3. **A red caused outside the batch** (main's latent fault, a known class)
   passes by name: a `## known-reds` row naming its queued fix (in `## queued`
   or already on the queue) and its cause, traced in the notes (2026-11-04:
   "if checks were not caused by its own code, then it should always pass").
   Never `--red-ok`, unless Jerus himself says so for that red (BA1: "Ship it,
   pass both"): his words and `--red-line` the lines they pass, recorded in
   `docs/notes/gate.txt`. A red the batch's own code causes is mended.
4. **Mends.** When the code is final and `--fast` and the harnesses the pick
   names are green (exit 0, or 3: only main's known reds), push the ship file
   with its marker and mail the orchestrator "ID mends at <commit>" with the
   review line and the picked harnesses of `sh tools/pick.sh origin/main HEAD`;
   then end the turn. A stranger's review may run first; its findings and the
   go come by mail, which resumes the builder.
5. **A dry run only when the pick is ALL, or when the branch edits the
   publisher** (`ship.sh`, `tools/gate*.sh`), which adds `--branch-gate` to run
   the branch's own `ship.sh` (GF1, LM1: the gate runs main's, so a publisher
   edit otherwise first runs for real at the next batch's gate). Ask "ID dry
   run?" and wait for the go (the orchestrator keeps one heavy slot). Merge
   origin/main first if it moved. Start
   `sh tools/gate.sh ID --dry-run > target/ID-dry.txt 2>&1`
   (`--no-tag` too for an untagged batch) as a background Bash call with
   timeout 7200000, never `nohup ... &` (SR1c: the gate cancels itself when the
   shell that started it returns), then repeat
   `sh tools/wait.sh --line target/ID-dry.txt 'gate +exit [0-9]|^exit [0-9]' --for 9`
   in the foreground (timeout 600000) until it ends, pushing nothing meanwhile.
   Exit 0 is green; otherwise read its `ship.log` and `unnamed.txt`. Put
   `<!-- gate-run: RUN_DIRECTORY -->` on the ship file's **first line** (lower,
   it falls inside `## known-reds` and the gate stops on a malformed row;
   without it the gate replays the whole suite, R1d). After a dry run change
   only the notes and the ship file. To judge a kept run again, copy its
   `checks/suite.txt` into your own `target/checks`; never run anything inside
   a `/tmp/cbs-gate-*` folder (R6b: a changed file breaks its hashes, and the
   gate reruns the suite).
6. **Ready.** On the go (after a green dry run, or at once when none is owed):
   drop the marker last, in its own commit, check it is gone on origin, and
   mail "ID ready at <commit>" (GR2, WG2: a change to shared code needs the
   whole merge's verdict first; 2026-11-11: "When it touches shared code"),
   then `sh tools/bus.sh state ID ready commit=C base=MAIN files=SRC,SRC
   moves_playtest=yes|no [run=DRY_RUN]`. A stopped gate mails the batch why:
   sent back, restore and push the marker first.
7. **Waiting** for the orchestrator: mail the question and end the turn; the
   reply resumes you. A session told to wait on `sh tools/wait.sh --row ID`
   runs, reads and plans nothing until the wait ends (2026-11-02: eight of ten
   night sessions worked ahead, 12 JVMs beside FC1's gate). A long job of your
   own starts in the background and is waited on in the foreground with
   `tools/wait.sh --line`; a session ends only done or blocked (BP1), its last
   message at most eight lines.

## 6. The gate and the orchestrator's answers

- **The gate** is `sh tools/gate.sh ID`: main's `ship.sh` in a fresh clone,
  never the branch's (a publisher edit tests its own first with `--branch-gate
  --dry-run`, 5.5), the data files named, the run's logs kept in its run
  directory; it publishes only on green (the merge, the records from the ship file, the version, the
  tag, one atomic push), posting its steps to MemWatch and mailing the batch a
  publication or a stop (BW1). What it checks follows the diff (2026-11-11: "Yes,
  make it the rule"): only `.md` outside `docs/ensemble/`, `--fast` alone; no
  game code (scripts, docs, tools, harness-only repairs), the compile, the
  picked harnesses, the maps, Stale and the changed scripts' self-tests; game
  classes, resources, `pom.xml` or `batch-check.sh`, the whole suite. A kept
  launcher run of the same commits is reused, not rerun ("Yes, reuse it"); only
  a run the launcher made in a fresh clone counts. Main's known reds are
  `docs/notes/known-reds.txt` and `docs/notes/gate.txt`, one judge for
  `AllChecks`, `batch-check.sh` and the gate (2026-11-11: "A file in the repo").
  GF1, GR1 and GR2, the redesign's own, merged with no gate run, on a fresh
  Opus session's review and the builder's dry run in a fresh clone.
- **The pick** (`sh tools/pick.sh`) is ALL for a class with more than
  `CBS_PICK_FANOUT` (50) readers and for `pom.xml`, `AllChecks`, `Timing`,
  `KnownReds` or `batch-check.sh`; a changed harness picks itself, the
  harnesses that borrow its fixtures, and `TimingCheck`. Its review line names
  money, saves and threading in the diff.
- **A stranger's review** only where money, saves or threading change (the
  bank, bonds, the treasury and funds; `DataSave`, `HistorySave`, the load
  path, `SAVE_FORMAT`; threads and the UI-wide; BP1: a review costs 190k-260k
  tokens). The review line is a floor.
- **The orchestrator answers what it safely can** (BP1, 2026-10-23: "dont most
  end up being rec?"): a session's question on the process, the code or its
  brief; a batch's recommended calls, taken and said in the notes; a Pending
  row that waits only on recommended calls. Jerus gets only what is his: a call
  with no clear recommendation, one that moves a standing rule or a premise,
  one that spends money. A question to him carries its content inside it (what
  the thing does in the game, the options, any table): he answers from the
  question alone (2026-10-20: "if you dont post the stuff in the chat i wont
  have a clue").
- **A blocked batch goes under Pending at once**, in a queue commit pushed the
  same turn (2026-10-24: PT1-M4's reds held a lane six hours). Who returns
  such a row to Queued when its block clears by itself is still Jerus's call
  (BP1, call 2; recommended: the orchestrator; unanswered, so until then his
  review moves it).
