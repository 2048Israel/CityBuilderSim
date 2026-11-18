# The orchestrator's own day

What the orchestrator does beside `docs/contract.md`, which binds it as it
binds every builder. This is today's practice: what the orchestrator becomes
once MemWatch dispatches is still Jerus's to decide (`docs/queue/WC1.md`).
Cut to this in WC1 (2026-11-18); the older text is in git history.

## The session

**One orchestrator, compacting in place** (OL1, 2026-11-08: BP1's fresh one a
day did not happen on 11-06 or 11-07), bus name `orchestrator`. Headless
through MemWatch's launcher since 2026-11-13, resumed by its mail; or from the
app environment "CBS orchestrator" (the main checkout, `CBS_ORCHESTRATOR=1`,
`CLAUDE_CODE_AUTO_COMPACT_WINDOW=300000`), whose hooks in
`.claude/settings.local.json` (not in git) put its hand-off note back after a
compaction. The note is kept current at every decision. Replaced only if a
turn fails beyond carrying on, a compaction loses something that mattered, or
Jerus says so.

`sh tools/day.sh` is the morning in about fifteen lines: main's `VERSION`
against the last version tag, main's recorded reds, the next queued ids, the
Pending ids, and each unmerged `batch/` branch on origin as ready (a ship file
pushed without its provisional marker; with the conflicts a merge would meet),
working, stuck (no commit for a day) or off-queue. `--due` prints only the
ready and the stuck, and `nightly due` once a day between 01:00 and 07:00.

**Waking** (lesson b: batches sat unshipped 9 hours on 2026-10-24). A builder's
mail resumes a headless orchestrator, and the batches' and the gate's state are
MemWatch's to draw (BW1). The fallback, for an app session no mail wakes: an
hourly CronCreate check-in running `sh tools/day.sh --due`, ending the turn when
it prints nothing (deleted when the session is replaced: two may gate one
branch), and `sh tools/wait.sh --ship [--skip HELD...]` in the background,
ending when a ship file lands without its marker.

**The nightly**, `sh tools/nightly.sh` on main's tip (4,000 months, the nightly
tier, hard timing; GT1, GR1), is due once a night; its verdict is in the next
`day.sh` and `$HOME/.cbs-nightly.log`, and a red playtest or a slower line is
mail to `jerus` and the gate's keeper (`CBS_GATE_KEEPER`, else `orchestrator`)
naming the batch (BW1). A headless orchestrator has no check-in
to start it: by hand, or a scheduled task (a daily one is Jerus's call, asked
2026-11-17).

## Dispatch

- Builders start through the launcher (`cbs-bus start <ID> --runtime claude|codex
  --model ... --effort xhigh`), the runtime chosen by the plans' usage. Each
  prompt carries the batch, the batches in flight and their files, and the
  contract's state lines and mail protocol (3, 5). A launcher name is never reused: a
  takeover is `<ID>c`.
- How many build at once is Jerus's setting; his latest word (2026-11-17):
  three builders, and a fourth on study, docs or process rows while the plan
  is behind pace: the week's % used plus the last six hours' rate times the
  hours to the reset under 95 (2026-11-07: "Yes, standing rule").
- **The heavy slot:** never two dry runs or two ensembles at once; one dry run
  beside one ensemble, but then no builder's full pick as well (DQ1's dry run
  died on a fork failure, 2026-11-17 20:05, with 1.4 GB free).
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
   a stop mails the batch itself, so a send-back needs no relaying. A ship file without its first-line gate-run comment runs fresh: stop it
   (`sh tools/gate.sh --stop RUN_DIRECTORY`) and gate with `--reuse` the kept
   run. `--version X.Y.Z` is Jerus's call.
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

BP1 item 10 (2026-10-23). When a batch needs a screen looked at, renders or
the game on slots 7-10, the orchestrator starts one session whose only job is
that, or reuses the one running. It takes a list of what to look at, returns
what it saw and the images' paths in 15 lines, and edits no source; on Sonnet
unless a check needs judgement. Slots 7-9 are its to write (the contract,
section 1).

## The docs pass

On Opus (Jerus, 2026-09-21: the implementer and the docs pass run on Opus,
whatever the job's size), by a fresh agent, at most once a day, over
everything merged since the last;
`docs/docs-pass.md` is its brief, `Docs pass.bat` runs it headless (a bare
`claude -p` takes the CLI's default effort: check it, lesson e),
`docs/docs-pass-agent.md` is it as a subagent. It ships `--no-tag` with a ship
file named from its branch, each `/` a `-` (`docs-pass-2026-10-26-ship.md`),
and a pass that changes only `.md` files gates on `--fast`. A manual edition
only at a milestone or when Jerus asks.

## Other assistants' branches

A `chatgpt/` or `gemini/` branch is reviewed by the `assistant-review` agent
or a fresh session, never a long conversation (RV1), and shipped with
`./ship.sh --no-tag <branch>` (no `{next}`: their branches take no version).
`ship.sh` runs `tools.Names` on them and prints the names their text uses that
the tree does not declare: a warning, not a gate.

## On the PC

`Batch check.bat` is Jerus's double-click (`--maps` regenerates the maps;
without it it writes nothing git tracks, because the PC's copy pulls `main`
and must stay clean); it has no `--pick` and no exit 3, and times soft while
another JVM or the game runs, decided once at its start (lesson d). Each gate
run is a `cbs-gate-ID-*` folder under the temp directory (`CBS_GATE_DIRECTORY`
names another); a green run's clone is removed, its log, refs and checks kept;
a stopped one stays whole for inspection (GF1, LM1: deleting gate clones
erased the evidence of stops).
