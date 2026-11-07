# CX2 - what an Opus session re-sends each step: a lighter start and a slower climb

Jerus, 2026-11-07 ("yes start it if orchestrator says yes"; the orchestrator:
start now as a third lane), on the hand-off of 2026-11-07: after CX1, the Opus
sessions are about three-quarters of the plan's usage. CX1 did this for the
Sonnet slices (`docs/queue/CX1.md`, its notes and `CX1-scripts.md`). This is
the same work for the Opus sessions.

**Why.**

- **Usage:** last week, at API prices, the Sonnet slices cost about $1,480 and
  the Opus sessions and subagents about $1,590. 1% of the weekly plan is about
  $31. Jerus rations about 14% of the week a day. The first 7 slices after CX1
  used 4.8M tokens each against 17.8M before, so Opus is now most of what
  is left.
- **The start:** an Opus session in this folder holds about 93k tokens before
  it reads anything. Measured on 2026-11-07 at 09:20 by `get_usage` on a fresh
  session:

  | part | tokens |
  |---|---|
  | built-in tool definitions | 42k |
  | MCP tools | 20.5k |
  | system prompt | 14k |
  | memory files (`CLAUDE.md`, `MEMORY.md`) | 7k |
  | skills | 6k |
  | MCP server instructions | 3k |

  The connectors loaded include some a batch session never calls: Gmail,
  Google Calendar, Claude Docs, the built-in browser, the visualizer, Chrome,
  computer use and the GitHub plugin.
- **The climb:** not yet measured. A batch session runs for hours: it reviews
  slices' diffs, runs `batch-check.sh` and reads gate output. Every step
  re-sends all of it. Cached, that costs a tenth of the price, but the plan
  still counts it.

1. **Measure.** Use the transcripts under `~/.claude/projects/*CityBuilderSim*`
   only, never another project's folder (Jerus's personal sessions live
   there), for the 7 days to the batch's start. Cover the Opus sessions and
   Opus subagents, by kind: batch sessions, the orchestrator, the gate's
   reviews, `assistant-review`, the screen-check session. For each kind give:
   - the starting context by part, the steps, the mean and peak context per
     step, the tokens, and the compactions;
   - the climb by kind of tool result: files read, `batch-check.sh` and
     `ship.sh` output, git diffs, slice reports, cross-session messages,
     ToolSearch loads, subagent results;
   - which tools and connectors each kind actually called.

   Use CX1's scripts and `C:\Users\Jerus\Tools\usage\tokens.py` before writing
   new ones. The notes give the table.
2. **A lighter start, where the table supports it.** For example:
   - the connectors no kind of session calls, turned off for this folder's
     sessions in the project's `.claude/settings.json`;
   - `CLAUDE.md` and the memory index cut to what a session needs at its
     start, the rest moved to the page that uses it.

   Keep every tool that some kind calls: the orchestrator's session tools, the
   screen-check session's computer use, the manual's Claude Docs, and so on.
   Whatever is outside the repository is not changed, only recommended: the
   app's own tools, its system prompt, `~/.claude` and the connector
   switches. That includes a per-kind start, if one is worth having. List
   which other sessions have run in this folder, so a setting here does not
   take a tool from one of Jerus's.
3. **A slower climb.** Rules the table supports, each with its WHY. For example:
   - a slice's diff reviewed from `git diff --stat` and then file by file;
   - gate and check output read only through grep, as `batch-check.sh`
     already intends;
   - a batch session past N steps hands over to a fresh one, with a 15-line
     note;
   - wide reading given to a subagent.
4. **Measured after.** A Pending row, CX2-M, like CX1-M. Over the next ten or
   so Opus batch sessions, compare with step 1's figures: the start, the mean
   context per step, and the tokens per batch.

**Done means:** the table; the cuts it supports, made in the repository or put
to Jerus with a recommendation; and a bar set from the table. For example: a
batch session's mean context per step down by a third, and its tokens to
match. The notes say why they chose the bar. This is a process batch with no
tag, so one in, one out applies: the notes give the line count (`git diff
--numstat origin/main` over the process files), and a new rule replaces an old
one where it can.

**Not this batch:**
- the slices (CX1; CX1-M measures them);
- DeepSeek (weighed 2026-11-06 and not taken);
- which batches run on Sonnet whole, which is `docs/batch.md` 3's call. The
  notes may recommend a change and leave the call to Jerus.
