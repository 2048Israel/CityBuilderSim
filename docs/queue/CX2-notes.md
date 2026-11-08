# CX2 - what an Opus session re-sends each step: a lighter start and a slower climb (2026-11-07)

Brief: `docs/queue/CX2.md`. Process only: `.claude/settings.json` (new),
`CLAUDE.md`, `docs/batch.md`; the measuring scripts as text in
`docs/queue/CX2-scripts.md`. No game code.

## 1. The week measured

The Opus transcripts begun in the 7 days to 2026-11-07 13:31 UTC under
`~/.claude/projects/*CityBuilderSim*`: 120 (66 sessions, 54 subagents), 3,785M
tokens, 98-99% of them cache reads. Left out: three personal chats of Jerus's
that ran in this folder, and CX2 itself. `tokens.py` over the same days (by
reply, so with sessions begun earlier) gives Opus 4.21B.

| kind | n | steps, median (all) | start | mean context a step: median session (step-weighted) | peak, median (max) | tokens | compactions |
|---|---|---|---|---|---|---|---|
| batch sessions | 49 | 161 (8,494) | 86.7k | 224k (249k) | 349k (778k) | 2,124M | 17 |
| the orchestrator | 3 | 322 (1,235) | 84.6k | 291k (393k) | 457k (792k) | 487M | 0 |
| studies | 7 | 153 (1,244) | 86.4k | 307k (310k) | 442k (613k) | 388M | 0 |
| gate reviews (Opus subagents) | 36 | 50 (1,787) | 61.4k | 123k (148k) | 176k (367k) | 266M | 0 |
| chats with Jerus | 7 | 79 (673) | 84.4k | 217k (315k) | 298k (756k) | 213M | 0 |
| a batch run as an agent | 9 | 106 (986) | 62.1k | 182k (212k) | 256k (418k) | 210M | 0 |
| Explore and other subagents | 9 | 52-82 (665) | 39-63k | 131-149k | 185-226k | 97M | 0 |
| all Opus | 120 | 106 (15,084) | 84.5k | 184k (250k) | 274k (792k) | 3,785M | 17 |

`assistant-review` and the screen-check session did not run in the week. The
orchestrator begun 2026-11-05 13:03 was still running at 13:31 today at 792k
a step: 672 steps, a mean of 480k, 323M tokens, 8.5% of the week's Opus alone.
BP1's fresh orchestrator each day did not happen on 11-06 or 11-07; CX2 told it.

## 2. The start, by part

Median characters in a batch session's first request, at 2.93 characters a
token (its first step's context over the start's characters; every kind gives
2.91-2.95). The orchestrator, studies and chats differ by under 2k.

| part | chars | tokens |
|---|---|---|
| built-in tool definitions (36) | 96.4k | 32.9k |
| - of which Artifact | 36.6k | 12.5k |
| - PowerShell | 9.5k | 3.2k |
| - Agent, Workflow, ScheduleWakeup, AskUserQuestion | 22.2k | 7.6k |
| system prompt | 43.2k | 14.7k |
| the browser pane (17 tools) | 22.4k | 7.7k |
| skill listing | 19.5k | 6.7k |
| `CLAUDE.md` | 17.0k | 5.8k |
| deferred-tool names (190) | 15.6k | 5.3k |
| the app's plan, wrap-up and session tools | 11.6k | 4.0k |
| MCP instructions (Chrome, computer use) | 6.3k | 2.2k |
| the brief | 4.8k | 1.6k |
| the visualizer (2 tools) | 4.2k | 1.4k |
| agent listing, memory index (1.1k each), the rest | 12.7k | 4.3k |

`get_usage` on CX2's own session agrees: system tools 44.9k, MCP tools 20.5k,
system prompt 14.4k, memory files 7.1k, skills 6.0k. A gate review starts at
61k: the same lists and `CLAUDE.md`, 70k characters of built-ins, a 3.6k
system prompt. An `Explore` agent starts at 39k (no `CLAUDE.md`).

## 3. The climb

Tokens per result character 0.284 (3.53 characters a token) and 1.11 per own
output token: a regression of each step's growth on what came between, over
12,643 steps. Shares of each kind's re-sent tokens:

| | batch | orchestrator | gate review | study |
|---|---|---|---|---|
| the start, re-sent each step | 35% | 22% | 42% | 28% |
| own output (thinking, calls) | 33% | 40% | 14% | 33% |
| results and messages in | 33% | 25% | 32% | 26% |
| - files read by `sed`/`cat`/`head` | 14.4% | 3.0% | 10.7% | 17.7% |
| - `git diff`/`show` | 2.8% | 2.0% | 9.9% | 0.3% |
| - check and harness output (grepped; raw) | 2.7%; 2.3% | 0.6% | 2.1%; 0.8% | 0.8% |
| - tool lists re-announced mid-session | 2.0% | 0.8% | 2.6% | 0.7% |
| - task notifications; session tools | 1.0% | 2.9%; 3.1% | - | 0.8% |
| - tool schemas loaded (ToolSearch) | 0.5% | 2.6% | - | 0.5% |
| - slice reports | under 0.2% | - | - | - |

A batch session's file reads, 51k tokens each: source 33%, studies, notes and
the project 19%, logs 14%, `docs/queue` 12%, maps 8%, process docs 5%.

What each step was for, since every step re-sends all of it: a batch session's
steps are reads and searches 37% (32% of its re-sent tokens), checks and
greps naming a harness 26% (28%), waits 10% (11%: 17 a session at 290k each),
edits 5%, git 11%; **88% of its steps make one call**. The orchestrator: 28%
of its steps make no call (a reply at a wake, 31% of its tokens), reads 23%,
sessions and messages 9%.

Context by step, batch sessions: 87k at 1, 108k at 10, 145k at 25, 198k at
50, 277k at 100, 340k at 150, 397k at 200. Past step 100 lies 56% of their
re-sent tokens (45 of 49 sessions get there), past 150 31% (31), past 200
16% (12). The orchestrator: 70% past step 200. The week's 17 compactions (and
the folder's others since 10-26) were all automatic, between 224k and 779k,
left 93-109k, and the sessions went on to ship.

**Tools called.** Batch sessions: Bash 7,382, Edit, Write, Read, Agent 127,
the plan and wrap-up, PowerShell 94, ToolSearch, TaskStop, SendMessage,
ReadNotifications, Grep, Skill, Monitor, WebFetch, AskUserQuestion; Chrome and
computer use only in the releases (IR1-IR3), the browser pane in IR1 and LC3.
The orchestrator: Bash, SendMessage 97, archive 58, start_session 46, Agent 32
(the gate's reviews), list_events, Cron*, get_usage. Gate reviews: Bash 1,781,
Read 11, Grep 1. Chats with Jerus: Claude Docs 91 (the manual), Artifact 7,
Chrome (the Steam test), the browser pane, the session tools. The personal
chats: Gmail, Calendar, computer use. **Never called** by any session or agent
in the folder's transcripts (back to 10-11 at least): ScheduleWakeup,
SuggestPluginInstall, SearchPlugins, SuggestSkills, ReportFindings, the
visualizer, the MCP registry, scheduled tasks; the GitHub plugin 7 times, the
last on 10-27 (`gh` does the same). Skills called: the three browser and
computer-use guides, dataviz, docs, xlsx, run, claude-api.

## 4. What was done

- **The start.** `.claude/settings.json` (checked in, so every worktree and the
  PC's checkout read it) denies by name the never-called tools above, and
  `read_widget_context`, which only reads the visualizer's widgets. A deny rule
  with no specifier filters the tool out of what is sent (the CLI's blanket-deny
  filter, 2.1.317): 17.2k characters of definitions and about 4k of the GitHub
  plugin's names and instructions, about 7k tokens, 8% of the start. Nothing a
  session in this folder has called is taken, Jerus's chats included.
- **The climb.** In the same file, `modelSettings."claude-opus-5-5".
  autoCompactWindow: 300000`: an Opus session or subagent compacts near 300k
  instead of at 97% of 1M. Sonnet slices are untouched (CX1-M measures them).
  Compaction keeps the session's id, worktree and lane, which a hand-over to a
  fresh session cannot: a session started by another session cannot start one
  (the app refuses), so a hand-over would wait on Jerus's click.
- **Rules,** in `CLAUDE.md`'s "Sessions", in place of the old bullet: what must
  survive a compaction goes in the notes file when it is decided; independent
  reads go in one step; a wide search to an `Explore` agent on Sonnet. Each
  carries its figure from section 3.
- **Not made a rule:** a `CLAUDE.md` cut (5.8k of the start, 7%; batch
  sessions read `docs/batch.md` whole, so a section moved there saves them
  nothing, and the other kinds about 0.2% of the week): two sentences
  `docs/deepseek.md` already holds came out, and `docs/batch.md`'s copy of the
  effort rule. `git diff --stat` first: diffs are 2.8% of a batch session's
  tokens, 10% of a review's, and reviews are 7% of the week. Waits: ★3.

## 5. What it should save (a model, not a measurement)

The week's contexts replayed less 7.5k a step, and at a cap restarted at the
start plus 30k, with 10 steps of orientation (a compaction's summary is about
13k, so this is the dear side):

| | batch | orchestrator | studies | gate reviews | all Opus |
|---|---|---|---|---|---|
| the week | 2,117M | 486M | 386M | 264M | 3,772M |
| the deny list | -3% | -2% | -2% | -5% | -3% |
| + compaction at 400k | -16% | -37% | -25% | -5% | -19% |
| + compaction at 300k | **-32%** | **-50%** | -37% | -5% | **-32%** |
| + compaction at 250k | -39% | -55% | -42% | -5% | -38% |

At 300k a batch session's step-weighted mean falls from 249k to about 165k
and the median session's from 224k to 174k, with 32 compactions over the 49.
300k, not 250k: it lies inside the range the week's compactions already ran
at, and 250k buys 7 points for 24 more of them. **The bar:** the Opus tokens
a week down by about 30%, a batch session's mean context per step (step-
weighted) down by a third to about 165k and its tokens to match (median
36.6M to about 25M), its start near 80k.

## 6. Recommendations to Jerus (outside the repository)

1. **Artifact and the browser pane load whole at every Opus step** (12.5k
   and 7.7k tokens). Batch sessions never called Artifact and the browser pane
   twice in 49. If the app can defer them (names only until ToolSearch loads
   one, as Chrome is), the start falls about 20k, worth about 8% of the Opus
   tokens. The repo cannot deny them: the manual is an Artifact, and releases
   and studies use the browser pane.
2. **Personal chats in a folder of their own.** Three ran here, the only
   callers of Gmail and Calendar; elsewhere, this folder could deny both (39
   names, about 1k tokens a step) and its transcripts would hold only the game.
3. **Skills nobody here calls** (docx, pptx, pdf, google-workspace, morning,
   import-memory, setup-claude, skill-creator and others): off for this
   folder, if the app allows it, would take 4-5k of the 6.7k listing.
4. **The memory index** is 4.2k characters in every session; a hook per line
   at half the length would save about 0.7k tokens a step.
5. **A per-kind start** (a batch session on an agent definition with only
   the tools a batch calls) would start near 45k, if `start_session` takes an
   agent; the deny list is what a folder can do alone.

## 7. Measured after (CX2-M)

Over the next ten Opus batch sessions begun after CX2 reaches main, rerun the
scripts and compare with section 1: the start (aim about 80k against 86.7k),
the step-weighted mean context (aim 170k or under against 249k), tokens per
batch session (median 36.6M, aim about 25M), the compactions (how many, at
what context) and whether any gate found work lost after one. Unverified
until then: that the deny rules drop the definitions (if the start does not
fall about 7k, the list does nothing and comes out) and that the
`modelSettings` key matches the session's model (a `compact_boundary` near
290-300k shows it).

## 8. The count (one in, one out)

`git diff --numstat origin/main`, the notes, the scripts and the ship file
left out:

| file | added | removed |
|---|---|---|
| `CLAUDE.md` | 18 | 18 |
| `docs/batch.md` | 2 | 3 |
| `.claude/settings.json` (new) | 8 | 0 |
| total | 28 | 21 |

+7 lines net, the new settings file's. The new rule replaces the old
"Sessions" bullet.

## 9. Calls

- ★1 The compaction window: **300k (recommended)**, or 250k for 7 points more
  and twice the compactions; CX2-M can lower it once 300k is seen to hold.
- ★2 No tag: CX2 changes no player's game. Ship it `--no-tag`, as CX1.
- ★3 Waits stay in the foreground (recommended): 11% of a batch session's
  tokens, but a Bash call waits at most 10 minutes, and ending the turn to wait
  is what BP1 item 2 stopped (the orchestrator woken by every interim report).
  Compaction caps a wait step's cost at 300k.
- Seen in passing: CX1's changelog block is in `docs/notes/changelog.md`
  twice (lines 105-108).
