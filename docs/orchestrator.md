# The orchestrator's own day

What the orchestrator does beside `docs/contract.md`, which binds it as it
binds every builder: today's practice (cut to this in WC1, 2026-11-18); what it
becomes once MemWatch dispatches is still Jerus's to decide (`docs/queue/WC1.md`).

## The session

**One orchestrator, compacting in place** (OL1, 2026-11-08: BP1's fresh one a
day did not happen on 11-06 or 11-07), bus name `orchestrator`: headless
through MemWatch's launcher since 2026-11-13, resumed by its mail, or the app
environment "CBS orchestrator" (the main checkout, `CBS_ORCHESTRATOR=1`,
`CLAUDE_CODE_AUTO_COMPACT_WINDOW=300000`; hooks in the untracked
`.claude/settings.local.json` put its hand-off note back after a compaction).
The note is kept current at every decision. Replaced only if a turn fails
beyond carrying on, a compaction loses what mattered, or Jerus says so.

`sh tools/day.sh` is the morning in about fifteen lines: main's `VERSION`
against the last version tag, main's recorded reds, the next queued ids, the
Pending ids, and each unmerged `batch/` branch on origin as ready (a ship file
pushed without its provisional marker; with the conflicts a merge would meet),
working, stuck (no commit for a day) or off-queue. `--due` prints only the
ready and the stuck, and `nightly due` (below).

**Waking** (lesson b: batches sat unshipped 9 hours on 2026-10-24): mail
resumes a headless orchestrator, and MemWatch draws the batches' and the gate's
state (BW1). An app session no mail wakes keeps an hourly CronCreate check-in
on `sh tools/day.sh --due`, its turn ended when that prints nothing (deleted
when replaced: two may gate one branch), and `sh tools/wait.sh --ship [--skip
HELD...]` running, which ends when a ship file lands without its marker.

**The nightly**, `sh tools/nightly.sh` on main's tip (4,000 months, the nightly
tier, hard timing; GT1, GR1), starts by itself: the laptop's task "CBS nightly"
at 02:30 (its line in GB2's notes) runs it on `nightly due`, once a night and
only while the last night ran on another `VERSION` (Jerus, 2026-11-20, his
answer to 11-17's question: "it should run every night IF the version number
has changed"; WHY: an untagged process batch leaves the game and its `VERSION`
alone, so its night would time the same game again). By hand on `nightly due`
when the task missed it; a second start is refused while one runs. It runs on
the gate box and holds it whole, a gate there waiting for it and it for a gate
(GB2). Its verdict comes home: the next `day.sh`, `$HOME/.cbs-nightly.log`, and
a red playtest or a slower line mailed to `jerus` and the gate's keeper, naming
the batch (BW1).

## Dispatch

- Builders start through the launcher (`cbs-bus start <ID> --runtime claude|codex
  --model ... --effort xhigh`), the runtime chosen by the plans' usage. Each
  prompt carries the batch, the batches in flight and their files, and the
  contract's state lines and mail protocol (3, 5). A launcher name is never
  reused: a takeover is `<ID>c`.
- How many build at once is Jerus's setting; his latest word (2026-11-17):
  three builders, and a fourth on study, docs or process rows while the plan
  is behind pace: the week's % used plus the last six hours' rate times the
  hours to the reset under 95 (2026-11-07: "Yes, standing rule").
- **The heavy slot:** the gate box keeps its own (GB3; Jerus, 2026-11-20,
  "Gatebox first"): gates and the nightly first, an ensemble behind them and
  giving way to one that comes while it plays, the runs it started finishing
  first; with two heavy jobs running or waiting there, or the box down, an
  ensemble runs on the laptop and says why. The laptop's is the
  orchestrator's: never two dry runs or two ensembles at once there, and no
  ensemble beside a dry run or a gate (2026-11-18: R2c-P's dry run went red
  when KeptCheck timed out at 1740 s beside HN1's 80-run ensemble; DQ1's died
  on a fork failure, 2026-11-17 20:05, with 1.4 GB free); no builder's full
  pick beside either.
- Before removing a worktree, check its session is done and its work is on
  origin, and grep `docs/queue` for references to its `target/` (2026-11-18:
  HN1's probe was lost to a `git worktree remove`).

## The gate

Each ready branch, one at a time:

1. Read the diff's stat, the notes and the review line of
   `sh tools/pick.sh origin/main origin/batch/ID`; check the batch did what its
   brief asked, the playtest's trace and, when it moved, the ensemble against
   the last baseline. A process batch's notes give its line count.
2. A stranger's review where the contract asks one, by a fresh reviewer; its
   findings go back to the builder by mail. Take the batch's recommended calls.
3. `sh tools/gate.sh ID [--no-tag] [-m "<title>"]` as a background Bash call
   with timeout 7200000 into a log, then `tools/wait.sh --line` until it ends;
   a stop mails the batch itself. A ship file without its first-line gate-run
   comment runs fresh: stop it (`sh tools/gate.sh --stop RUN_DIRECTORY`) and
   gate with `--reuse` the kept run. `--version X.Y.Z` is Jerus's call.
4. Verify the publication: one merge, its first parent the gated main; the tag
   one up (none for an untagged batch); `known-reds.txt` and `QUEUE.md` changed
   only as the ship file said; `ship.log` ending pushed. Never `--red-ok` or
   `--red-line` unless Jerus himself says so for that red (BA1: "Ship it, pass
   both"), never a hand merge.
5. A branch that edits the publisher (`ship.sh`, `tools/gate*.sh`) has had its
   own `--branch-gate --dry-run` (the contract, 5.5). The gate runs a changed
   script's self-test, but not beside the whole suite: there, run it by hand
   (GF1, TM3: a merge rewrites scripts while their shell reads them).

**Before saying a change works:** the gate green but for main's recorded reds,
the maps regenerated, the ensemble scored if the trace moved, the write-up
written, the docs pass run, the batch's row out of `QUEUE.md`.

## The screen-check session

BP1 item 10 (2026-10-23): when a batch needs a screen looked at, renders or
the game on slots 7-10, the orchestrator starts one session for that alone, or
reuses the one running. It takes a list of what to look at, returns what it
saw and the images' paths in 15 lines, edits no source, and runs on Sonnet
unless a check needs judgement. Slots 7-9 are its to write (the contract, 1).

## The docs pass

On Opus (Jerus, 2026-09-21: the implementer and the docs pass run on Opus,
whatever the job's size), by a fresh agent, at most once a day, over
everything merged since the last. `docs/docs-pass.md` is its brief,
`docs/docs-pass-agent.md` it as a subagent, and `Docs pass.bat` runs it
headless (a bare `claude -p` takes the CLI's default effort: check it, lesson
e). It ships `--no-tag` with a ship file named from its branch, each `/` a `-`
(`docs-pass-2026-10-26-ship.md`). A manual edition only at a milestone or when
Jerus asks.

## Other assistants' branches

A `chatgpt/` or `gemini/` branch is reviewed by the `assistant-review` agent
or a fresh session, never a long conversation (RV1), and shipped with
`./ship.sh --no-tag <branch>` (their branches take no `{next}`); the names its
text uses that the tree does not declare (`tools.Names`) are a warning only.

## On the PC

`Batch check.bat` is Jerus's double-click (`--maps` regenerates the maps;
without it it writes nothing git tracks, as the PC's copy pulls `main` and must
stay clean); it times soft while another JVM or the game runs, decided once at
its start (lesson d). Each gate run is a `cbs-gate-ID-*` folder (or
`CBS_GATE_DIRECTORY`'s name) in `~/cbs/runs` on the gate box, its files but the
clone copied to the temp directory here at the end (GB1; here alone when the box
is down); a green run's clone is removed, its log, refs and checks kept, a
stopped one kept whole (GF1, LM1: deleting gate clones erased the evidence of stops).
