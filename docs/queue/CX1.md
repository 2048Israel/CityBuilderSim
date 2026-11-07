# CX1 - what a Sonnet slice re-sends each step: a lighter start and a slower climb

Jerus, 2026-11-06 ("queue it near the top ... run it very soon"), on the finding
that the Sonnet slices are about half of the game's plan usage. The figures come
from the session transcripts under `~/.claude/projects/*CityBuilderSim*`, for
the 7 days to 2026-11-07 03:13 UTC.

**Why.**

- **Usage:** 168 Sonnet slices ran in the week, a median of 95 steps and 17.6M
  tokens each, 4.48B in all. At API prices that is about $1,480 a week, against
  $1,590 for every Opus session and subagent together (cache writes priced at
  the 1-hour rate). The plan stood at 85% of its weekly limit with a day to go.
- **The start:** a slice begins at a median 62k tokens of context (p25 61k, p75
  64k): the system prompt, every tool's definition, `CLAUDE.md` (17 KB) and the
  brief, before it has read anything.
- **The climb:** the context grows to a median peak of 290k (p75 389k, max
  967k), and the mean step re-sends 187k. Every step re-sends all of it; cached,
  that costs a tenth of the price, but the plan still counts it.
- **The comparison:** DeepSeek's own loop (`tools/deepseek/agent.py`) sent about
  39k per request in the FR0 trial (95 requests, 3.7M tokens).

1. **Find where the 62k and the climb come from.** From the transcripts, with a
   script in `tools/` or the batch's scratch folder, over last week's slices:
   - the starting context by part (tool definitions, `CLAUDE.md`, memory, the
     brief);
   - the climb by kind of tool result (files read whole, harness output, greps,
     compile logs).

   The notes give the table.
2. **A lighter start.** A slice agent definition in `.claude/agents/`, like
   `assistant-review.md`: Sonnet 5.5 at xhigh, with only the tools a slice uses
   (Read, Edit, Write, Grep, Glob, Bash) and no MCP tools. If the table shows
   `CLAUDE.md` is a large part of the 62k, give a slice only what it needs of it,
   on a page of its own. `docs/deepseek.md` and `docs/batch.md` then say to
   start slices with the new definition.
3. **A slower climb.** Rules the table supports, each with its WHY. For example:
   - briefs name methods and line ranges (`Where X -print`), not whole files;
   - a harness run's output goes to a file and is grepped, as `batch-check.sh`
     already does;
   - no file over N lines is read whole;
   - a slice past M steps stops and reports, so the next one starts fresh.
4. **Measured after:** on the next ten or so slices that use the new definition,
   compare against the figures above:
   - the median starting context;
   - the mean context per step;
   - the tokens per slice;
   - the per-slice "what the review changed" table, so the saving does not come
     out of quality.

**Done means:** the mean context per slice step is down by at least half (under
about 95k), the tokens per slice fall to match, and the review quality holds.
This is a process batch, so one in, one out applies: its notes give the line
count, and its rules replace earlier ones rather than add where they can.

**Not this batch:**
- moving slices to DeepSeek, weighed the same night: it would add a bill of
  about US$500-1,050 a month;
- the Opus sessions' own context.

**Files:** `.claude/agents/`, `docs/deepseek.md`, `docs/batch.md`, perhaps
`CLAUDE.md`. There is no game code in it, so it can run beside either lane.
