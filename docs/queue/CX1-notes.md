# CX1 - what a Sonnet slice re-sends each step: a lighter start and a slower climb (2026-11-07)

Brief: `docs/queue/CX1.md`. Process only: `.claude/agents/slice.md` (new),
`docs/deepseek.md`, `docs/batch.md`, `CLAUDE.md`, plus `docs/notes/batch-history.md`
(history moved out) and `docs/queue/CX1-scripts.md` (the measuring scripts, as
text; `tools/` untouched). No game code, so `./batch-check.sh --fast` only.

## 1. The week measured

167 Sonnet subagents (163 `general-purpose`, 4 `Explore`; the brief counted 168)
in the 7 days to 2026-11-07, from the transcripts. 17.5k steps, 4.46B tokens:
4.34B read from cache, 0.10B written to it, 19.3M output, none uncached.

| per slice | median | p25 | p75 | max |
|---|---|---|---|---|
| steps | 95 | 59 | 128 | 746 |
| context at step 1 | 62.2k | 60.9k | 64.1k | 69.0k |
| peak context | 290k | 207k | 378k | 967k |
| mean context per step | 187k | 143k | 243k | 531k |
| tokens | 17.8M | 9.1M | 29.9M | 360M |

Tokens are characters / 2.68 for the start's text (166.6k characters, 62.2k
tokens) and / 2.5 for tool results (a regression of context growth on result
characters over 8,758 steps gave 2.47; the output coefficient came out 1.00, so
a reply's thinking and calls are fully re-sent).

## 2. The start: 62k, by part

Median characters in the first request, from each transcript's recorded request
shape and attachments:

| part | chars | tokens | share |
|---|---|---|---|
| tool definitions (38 tools) | 108.3k | 40.4k | 65% |
| skill listing | 17.8k | 6.6k | 11% |
| `CLAUDE.md` | 17.0k | 6.4k | 10% |
| deferred-tool names | 9.7k | 3.6k | 6% |
| the brief | 5.5k | 2.1k | 3% |
| system prompt | 2.9k | 1.1k | 2% |
| auto memory | 2.8k | 1.0k | 2% |
| git status | 1.4k | 0.5k | 1% |
| environment, model, date | 1.2k | 0.5k | 1% |

The 38 tools: Artifact 36.6k chars, 17 browser tools 22.4k, PowerShell 9.5k,
Agent 4.9k, the other MCP tools, Skill, ToolSearch and SearchPlugins 35.2k, and
the six a slice calls (Bash, Read, Edit, Write, Grep, Glob) 11.3k. Cross-check:
the six `Explore` runs (no `CLAUDE.md`, 41 tools of 65k chars) started at 38.6k.
Cutting the tools to six, the skill list, the deferred names, `CLAUDE.md` and
memory would take 36.2k + 10.3k + 7.4k = 54k off the start, 21% of the week's
re-sent tokens (the start times the steps was 25% of them in all).

## 3. The climb, by kind of result

A result's weight is its tokens times the steps that re-send it. Of the 4.44B
re-sent: the start 25%, the slice's own output (thinking and calls) 31%, the
tool results 44% (the three estimates sum to 102%).

| kind of result | calls | mean tokens | share |
|---|---|---|---|
| `sed -n` range | 2,327 | 2,388 | 13.6% |
| `grep` in Bash | 3,421 | 814 | 6.2% |
| a command naming a harness or `batch-check`, output raw | 1,522 | 1,677 | 5.5% |
| the same piped to `tail`/`grep` | 2,494 | 822 | 4.0% |
| Read with offset/limit | 535 | 3,936 | 4.6% |
| other Bash (scripts, waits, setup) | 2,983 | 730 | 4.2% |
| Read whole | 232 | 7,205 | 2.5% |
| `cat`, `git`, `Where -print`, `ls`/`find`, scripts | 2,016 | 460-1,440 | 3.7% |
| Edit/Write, and other tools | 3,960 | 85-550 | 1.2% |

By size: 105 results over 10k tokens hold 3.4% of the re-sent tokens; 4-10k, 973
results, 13.5%; 1.5-4k, 3,495 results, 17.9%; under 1.5k, 15.4k results, 11%.
No giant file is the cost: the many mid-size reads are. A slice fetches about
2,700 lines in 44 `sed` ranges (median 40 lines, p90 131), and only 5% of the
lines fetched had already been shown to it, so re-reading is not the lever.
89% of steps make no more than one call (1.14 a step).

Context by step: 62k at 1, 104k at 10, 132k at 20, 186k at 40, 236k at 60, 283k at
80, 324k at 100, 433k at 150, 510k at 200. The share of the re-sent tokens held
by steps past a cap M: 60, 69% (123 slices run past it); 80, 56% (104); 100, 44%
(72); 120, 34% (51); 150, 25% (26); 200, 17% (11); 300, 7.5% (5).

## 4. What was done, and what the table did not support

- **The definition** (`.claude/agents/slice.md`): Sonnet, `effort: xhigh`
  (the frontmatter sets it; the launching call need not), six tools, no MCP,
  `omitClaudeMd: true` with the standing rules a slice meets in its own body
  (47 lines against `CLAUDE.md`'s 260), `maxTurns: 120`.
- **Rules** (`docs/deepseek.md`, "What a slice costs"; `docs/batch.md`, 3):
  start as `slice`; the brief carries the files, the methods and the harness;
  size to about 80 steps, a fresh successor not a resume; output to a file,
  independent reads in one step. Each has its WHY from section 2 or 3.
- **Not made a rule:** "no file over N lines whole" (Read whole is 2.5%, and the
  definition keeps a one-line 600-line limit only because it costs nothing),
  and "do not re-read" (5%). The effort stays at xhigh by `CLAUDE.md`'s rule, so
  the 31% of output is left alone.
- **Not tried:** `autoCompactWindow` (100k-1M) in the definition, which would cap
  the climb at the window but replaces the transcript with a summary mid-slice;
  CX1-M can try it if the climb stays high.
- **Moved, not lost:** the DeepSeek test's figures and the cloud-nesting and
  JVM-lock paragraphs, to `docs/notes/batch-history.md`.

## 5. What it should save (a model, not a measurement)

The week's per-step contexts replayed with a 14k start (8.3k tokens of system,
six tools, environment, git status and a median brief, plus up to 10k if the
skill and deferred lists survive the tool list) and a cap that hands the rest to
a fresh slice, which pays 10 steps of orientation again:

| | tokens re-sent | median slice's mean context | slices |
|---|---|---|---|
| the week | 4.43B | 188k | 167 |
| lighter start | 3.59B (-19%) | 140k | 167 |
| + cap 120 | 2.77B (-37%) | 127k | 232 |
| + cap 100 | 2.55B (-42%) | 119k | 261 |
| + cap 80 | 2.30B (-48%) | 105k | 317 |
| cap 120, climb 15% lower | 2.40B (-46%) | 110k | 232 |
| cap 120, climb 30% lower | 2.02B (-54%) | 93k | 232 |

The done bar (under about 95k) is reached in the model only if the climb also
falls by a third; the definition and the cap alone reach about 127k. More slices
means more briefs and reviews on the Opus side, which the table leaves out.

## 6. Not verified, and measured after (CX1-M)

- Agent definitions load at session start, so this session could not start a
  `slice`. Unconfirmed until one runs: that `tools` drops the skill listing
  (17.8k chars) and the deferred-tool names (9.7k) with Skill and ToolSearch
  (`Explore` has both and carried both), that `omitClaudeMd` is honoured by
  Claude Code 2.1.309, and that `maxTurns` counts steps as the table does. If
  the first `slice` starts above about 19k, one of them did not hold.
- CX1-M: for the next ten slices started as `slice`, rerun
  `docs/queue/CX1-scripts.md` and compare with section 1: the median start
  (aim 9-19k against 62k), the mean context per step (aim under 95k against
  187k), the tokens per slice (against 17.8M), and the batch's own table
  `| slice | by | what | the review changed |` with the steps, start and
  mean context added, against the FR1-FX4 trial's "about half needed nothing"
  (last week's batches kept no such table, so that is the only baseline).

## 7. The count (one in, one out)

`git diff --numstat origin/main`, the notes, the scripts file and the moved
history left out:

| file | added | removed |
|---|---|---|
| `docs/deepseek.md` | 59 | 77 |
| `docs/batch.md` | 12 | 11 |
| `CLAUDE.md` | 9 | 9 |
| the three session-read files | 80 | 97 |
| `.claude/agents/slice.md` (new) | 47 | 0 |

The files an Opus or orchestrator session reads shrank by 17 lines; the new
definition adds 47 that only a slice reads, in place of the 260-line `CLAUDE.md`
it no longer loads. Together +30 net, which is more than "about as many";
`docs/notes/batch-history.md` took 41 of the removed lines as history.

## 8. Calls

- ★1 `maxTurns` 120 (recommended) or 100: 100 saves 5 points more in the model
  but cuts 72 of the week's 167 slices (43%) against 51 (31%) at 120.
- ★2 CX1 changes no player's game: ship it `--no-tag`. Its ship file has no
  version, and its `CX1-M` row no `{next}`, for that.
