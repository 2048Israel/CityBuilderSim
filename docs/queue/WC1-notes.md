# WC1 - one contract for both builders: notes

Built 2026-11-18 by Claude (Opus 5.5, xhigh, headless), from origin/main
c17b92f0f (0.9.148). Docs and process only, untagged: **no game code changes**
(no file under `src/` is touched; the pick is NONE of 220 harnesses).

## What changed

- **`docs/contract.md` (new, 308 lines)** is the one file Claude and Codex
  builders, the orchestrator and reviewers read. Six sections: the standing
  rules (every one of `CLAUDE.md`'s, kept word for word in substance), who
  does what, starting a batch, building, ready, the gate and the
  orchestrator's answers. Every settled decision of `docs/queue/WC1.md` is in
  it, with his words; the three items still his are named in section 2 as
  his, unanswered. Where WC1.md is silent it writes down today's practice,
  the orchestrator's night practice included: the provisional ship file and
  the "ID mends at <commit>" mail; no dry run unless the pick is ALL or the
  branch edits the publisher (then `--branch-gate`), and "ID dry run?"
  first; the gate-run comment on the ship file's first line; no JVM beyond
  `--fast` while a gate or dry run is on the machine; a headless builder's
  subagents in the foreground; a red caused outside the batch passed by name
  under a queued fix, `--red-ok` only on Jerus's own word; the marker dropped
  last and checked gone; "ID ready at <commit>".
- **After REV-WC1-1** (a stranger's review, a fresh session, on 060748d69),
  restored or added: the implementer and the docs pass on Opus (Jerus,
  2026-09-21: contract 2, `docs/orchestrator.md`'s docs pass); a publisher
  edit's own `--branch-gate --dry-run` (GF1, LM1: contract 5.5 and 6,
  `docs/orchestrator.md`'s gate step 5); `--red-ok` when he himself says so
  (BA1, "Ship it, pass both"), not never; his open call on who returns a
  cleared Pending row to Queued (BP1 call 2, recommended: the orchestrator;
  contract 6); five rules that lived only in memory notes, each with its WHY:
  no DeepSeek unless he raises the budget (2), one Sonnet subagent per
  session at a time (4), never a baseline suite of main (4), the todo under
  100 KB (4), nothing done while waiting on `wait.sh --row` (5.7).
  `gemini-notes.md` and `docs/docs-pass.md` pointed at `CLAUDE.md` for rules
  now the contract's; mended.
- **Cut to what is not process:** `CLAUDE.md` is the map and the tools (its
  standing rules, "Coding goes to Sonnet" and "How a batch is done" moved;
  it opens by pointing at the contract); `docs/orchestrator.md` is the
  orchestrator's own day (the session, waking, the nightly, dispatch, the
  heavy slot, its gate steps and the publication check, the screen-check
  session, the docs pass, other assistants' branches, the PC);
  `docs/deepseek.md` is the DeepSeek tool alone; `openai-dontread.md` is
  ChatGPT's role, its rules now the contract's; `docs/batch.md` is a
  five-line pointer. It is kept, not removed, because 75 notes in
  `docs/queue/` and `QUEUE.md` cite it by its steps: removed, each would be
  a Stale missing file, and the history should not be rewritten.
- **Pointing at it:** `AGENTS.md`, `CLAUDE.md`, `.claude/agents/slice.md`,
  `.claude/agents/assistant-review.md`, `README.md`, `docs/docs-pass.md`,
  `docs/docs-pass-agent.md`, `gemini-notes.md`.
- **RL1's machinery removed**, as WC1.md's "After it ships" decision lists:
  `tools/carries.sh`; `ship.sh`'s stop on a branch built on a batch main
  lacks; `pick.sh`'s diff against a predecessor's tip; `wait.sh --row`'s
  mended-predecessor ending; `day.sh`'s "waiting" state; their self-test
  cases and `docs/tree.md`'s lines. Before removing it I checked that no
  `batch/` branch on origin is built on an unmerged one (none is: the held
  LM1-D is not built on `batch/LM1`). The list's other two items were already
  gone (`docs/batch.md` 7's second half, GG1) or live only in a memory note
  (the brief template in `night-line-of-sessions`, below).
- **Left:** `ship.sh`'s five stop messages still cite `docs/batch.md`, whose
  pointer leads to the contract. I left them so the publisher that ships is
  the one the 37-minute self-test passed; BW1, which edits `ship.sh` next,
  can change "(docs/batch.md)" to "(docs/contract.md)". The changelog's own
  header cites `CLAUDE.md`, "Each batch is told once", for the 12-line rule,
  now the contract's section 1; that file is the publisher's, so it is left.

## What it changes in practice, for Jerus

Mostly it moves words. Beyond that:

1. **Codex builders now follow the same rules as Claude ones.** About twenty
   rules lived only in Claude's memory notes, which Codex cannot read: name
   the map files in a worktree, keep helpers in the foreground, ask before a
   dry run, put the gate-run line first, never pass a red with your words,
   ask before pushing main. They are in the repository now.
2. **A batch built on top of another unshipped batch is no longer supported
   by the scripts.** Since your 11-11 answer the next batch of a chain starts
   from main after the first one ships; the tools that made the other way
   work are gone. Nothing in flight used them.
3. **A red that is not the batch's fault** is passed by naming it beside the
   batch queued to fix it, never by quoting your 11-04 words at the gate.
   This is what the orchestrator has done since GR2; the contract makes it
   the rule for everyone. A red you yourself say to pass ("Ship it, pass
   both") still passes on your words.
4. **A builder checks less before saying it is done**: the quick check and
   the harnesses its change touches, one at a time, instead of the old full
   "pick" run, unless its change touches shared code or the shipping script,
   when a dry run decides. This is the night practice that kept the gates
   from timing out.

Nothing else about how batches are chosen, built, gated or shipped changes.
Still yours, unanswered: what the orchestrator becomes once MemWatch
dispatches; who keeps the gate day to day and the gate box's part; and which
standing rules stay (save slots 1-9 still say "never write them"); and, from
BP1, who moves a Pending row back to Queued when its block clears by itself
(recommended: the orchestrator).

## One in, one out

`git diff --numstat origin/main` over the process files (this notes file and
the ship file left out): 559 added, 722 removed, **163 lines fewer** (194
before REV-WC1-1's mends, which added 31 net). The contract (308) is shorter
than the process text it replaces (579 lines taken out of `CLAUDE.md` 110,
`docs/batch.md` 91, `docs/orchestrator.md` 225, `docs/deepseek.md` 121,
`openai-dontread.md` 32).

      4   3  .claude/agents/assistant-review.md
      3   2  .claude/agents/slice.md
      7   7  AGENTS.md
     16 110  CLAUDE.md
      4   4  README.md
      3  91  docs/batch.md
    308   0  docs/contract.md
     28 121  docs/deepseek.md
      1   1  docs/docs-pass-agent.md
      8   8  docs/docs-pass.md
    117 225  docs/orchestrator.md
      6  11  docs/tree.md
      3   3  gemini-notes.md
     23  32  openai-dontread.md
      0   2  ship.sh
      0  24  tools/carries.sh
      4   7  tools/day.sh
      1  10  tools/pick-selftest.sh
      2   3  tools/pick.sh
      3  18  tools/ship-selftest.sh
     12  21  tools/wait-selftest.sh
      6  19  tools/wait.sh

## Checks (each run alone, the map files named)

- `./batch-check.sh --fast`: exit 0; compile OK, maps regenerated (none
  changed), Stale 0 firm (187 soft, none in the changed pages), tree clean.
- `java -cp target/classes ham.citybuildersim.tools.Stale`: exit 0, 0 firm
  ("the prose and the tree agree on everything this tool can check").
- `./batch-check.sh StaleCheck`: exit 0.
- `sh tools/pick.sh origin/main HEAD`: NONE of 220 harnesses; review not
  needed (no money, saves or threading). The pick is not ALL, but the branch
  edits `ship.sh`, so it owes `sh tools/gate.sh WC1 --branch-gate --dry-run
  --no-tag` (REV-WC1-1), asked as "WC1 dry run?".
- That dry run, on the orchestrator's go: run `/tmp/cbs-gate-WC1-oM23NTYF`,
  branch b6bf51152 on main c17b92f0f, the branch's own `ship.sh` (the run's
  `publisher.sh`). Records: WC1's block on top, 0 Pending rows, its Queued
  row out; `--fast` exit 0 (65 s); `pick-selftest` 19 of 19, `ship-selftest`
  149 of 149, `wait-selftest` 20 of 20; "the tree is as it was"; **gate exit
  0**, 08:44 to 09:16. Its would-be merge title is the Queued row's whole
  text; `-m "one contract for both builders"` at the gate gives a short one.
- The changed scripts' self-tests: `ship-selftest.sh` 149 of 149 (main's
  less its 7 RL1 cases; it ran 37 minutes), `pick-selftest.sh` 19 of 19,
  `wait-selftest.sh` 20 of 20 (its RL1 section rewritten as a plain `--row`
  test: queued, ready but not landed, landed, `day.sh` ready).
- The gate will run `--fast` and those three self-tests (scripts changed, no
  game code). The contract's statements were checked against the scripts on
  main by grep: `ship.sh`'s scope rules (docs only, no game code, full), its
  heading check and record stops, `pick.sh`'s ALL rules and fan-out, the
  gate-run and provisional markers, `gate.sh`'s run directory, `wait.sh`,
  `day.sh`, `batch-check.sh`'s exits, `AllChecks -f`.

## The memory notes, for the orchestrator to apply after the ship

In `C:\Users\Jerus\.claude\projects\C--Users-Jerus-Documents-NetBeansProjects-CityBuilderSim\memory\`,
with their lines in `MEMORY.md`. I read them and edited none.

Delete (the rule is now the contract's, or is obsolete):

| note | where it is now |
|---|---|
| effort-xhigh-or-max | contract 2 |
| headless-turn-kills-subagents | contract 4 |
| pick-in-a-worktree-needs-data-named | contract 3 |
| reds-not-the-batchs-pass | contract 5.3 (`--red-ok` only on his own word) |
| ship-past-known-reds | its hand merge is obsolete since BP1; `--red-ok` on his word, contract 5.3 |
| two-batches-at-once | contract 3 (by files), orchestrator.md, Dispatch |
| night-line-of-sessions | RL1's wait and brief template are gone; waking is orchestrator.md's; nothing done on `wait.sh --row`, contract 5.7 |
| sonnet-when-no-deepseek | contract 2 (Opus, Sonnet, no DeepSeek unless the budget) and 4 (slices) |
| slice-worktrees | contract 4 (`EnterWorktree`) |
| ask-before-pushing-main | contract 4 |
| eye-checks-not-pending | contract 4 (the 40 old rows stay in `docs/notes/eye-checks.md`) |
| questions-carry-their-content | contract 6 |
| commercial-use-sources | contract 4 (LC1 is on the record) |

Cut:

- **saves-never-matter** to its two facts the repository does not hold: "my
  saves never matter" (2026-10-18: a format or floor raise needs no warning
  and holds nothing back) and "the 1-9 save slots are NOT important" (11-11:
  build no guards for them, add no caveats). The rules themselves are the
  contract's section 1.
- **pc-shipping** to: start sessions from the repository root (one in
  `src/main/java/ham/citybuildersim` puts its worktrees inside the source
  tree); memory, not CPU, is what runs short (its one-Sonnet-subagent rule is
  the contract's 4). Its temp-clone steps are obsolete (`tools/gate.sh` makes
  the clone), and so is its timing paragraph (GR1).
- **ensemble-on-the-pc** to the PC's setup: pandas at
  `C:\Users\Jerus\.cache\cbs-ensemble\py`, `python3` the Store stub,
  `--stop <label>`, `-Densemble.extra` and `-Densemble.months`. The rule
  (only when the trace moves, `docs/ensemble/<id>.md`) is the contract's 4.
- **workflow-redesign** to a pointer: his 11-11 decisions are the contract's
  and `docs/queue/WC1.md`'s; keep its measured figures (ready to tagged, a
  median 22 minutes in the week of 10-18 and 110 in that of 11-08; 34 of 81
  gate-hours in timing rounds) and the three items still his. The lane
  histories of 11-12 to 11-16 are in the changelog.
- **codex-cli**: drop "a batch message has to say that brief does not
  apply" (`AGENTS.md` has pointed Codex past `openai-dontread.md` since GF1,
  and now at the contract).
Keep as they are: orchestrator-handoff (its "The rule" paragraph too: the 40
lines with its contents, edited in the same message, the ~115k threshold and
the replace steps are the hand-off's own; REV-WC1-1), pc-subagent-depth, one-pc, bus-mail-to-a-dead-session,
idle-is-not-stopped, archive-when-on-github, civic-ledger-on-itch,
steam-app-setup, gate-box, memwatch, plan-ration, exe-smoke-run.
