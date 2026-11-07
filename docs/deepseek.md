# DeepSeek - the coder a Claude session hands a slice to

A batch's Claude session hands the coding a brief can pin down to a coder:
Sonnet for now (below), DeepSeek V4 Pro through `tools/deepseek/agent.py` when
Jerus has the budget. Claude decides the design, writes the brief, reviews the
diff, runs the checks and commits; the coder does the reading and writing in
between. DeepSeek is not an assistant like ChatGPT or Gemini: it has no branch
and no brief of its own, and nothing it writes is committed until a Claude
session has read it (Jerus, 2026-10-17: "update the required docs so that new
batches with claude remember to use deepseek").

**Sonnet first, for now** (Jerus, 2026-10-20): "for a long while there will
be no deepseek so its sonnet". DeepSeek is billed by the token on top of his
20x subscription and Sonnet runs inside it, so every slice goes to an Agent
subagent of type `slice` (`.claude/agents/slice.md`: Sonnet 5.5 at xhigh, six
tools, 120 steps, no `CLAUDE.md`; CX1), handled as this page handles DeepSeek:
the same brief, the subagent edits the worktree and commits nothing, runs only
the harness its brief names, and Claude reviews the diff as a stranger's and
commits. Since BP1 (2026-10-24) the slices are fewer and larger and Claude
reviews **without re-reading what the brief sent the subagent to read**: an
Opus batch session cost 400k-770k tokens with Sonnet already editing, much of
it that re-reading. A small fix batch - no money, saves or threading, a few
hundred lines - runs on Sonnet as the whole session, Opus at the gate only
(amends "Which model", `docs/orchestrator.md`). A model slice's review is the
careful one (the FR1-FX4 trial: the map, drawing and harness slices were nearly
always right, the model slices needed real fixes - FR3's dues never reaching the
treasury, FR2's Haulage rules, every id across a save), most of all where money,
saves or threading change (BP1): the bank, bonds, the treasury and funds;
`DataSave`, `HistorySave`, the load path and `SAVE_FORMAT`; threads and anything
UI-wide. Those get a stranger's review at the gate as well, and `tools/pick.sh`'s
review line names them. When DeepSeek returns, Sonnet is its fallback whenever
`DEEPSEEK_API_KEY` is unset or the API fails.

## What a slice costs (CX1, 2026-11-07)

A week of 167 Sonnet slices was 4.46B tokens, a median 95 steps each, every step
re-sending the whole context (`docs/queue/CX1-notes.md` has the tables):

- **Start every slice as `slice`, never the default agent.** WHY: the default
  began at 62k, 65% of it 38 tool definitions of which a slice calls six, and
  re-sent them on all 17.5k steps; those, the skill list, `CLAUDE.md` and memory
  were 21% of the week's re-sent tokens.
- **The brief carries what the slice needs**: each file, each method with its
  `Where X -print`, the harness, and any rule beyond the definition's. WHY:
  orienting itself lifted a slice from 62k to 104k in ten steps.
- **Size a slice to about 80 steps.** One that reports partial at 100 or stops
  at 120 gets a fresh successor briefed on the rest, not a resume (which
  re-sends all). WHY: steps past 100 held 44% of the re-sent tokens (72 of 167
  slices), at 324k of context; a fresh `slice` starts near 15k (estimated).
- **A run's output goes to a file and is grepped; independent reads share a
  step.** WHY: Bash results (reads, greps, harness runs) were 37% of the re-sent
  tokens, and 89% of steps made no more than one call.

## What to hand it, and what to keep

**Hand it** what a brief can settle: a pure model class and its harness; a
harness section; a decided mechanic with its dials; mechanical edits over
several files (a rename; a saved field through `DataSave`, `HistorySave`, the
load path and `SaveFileCheck`); a first draft of a screen section, which Claude
then checks by eye.

**Keep** anything that waits on Jerus or decides what the spec leaves open;
where a line goes in the month, unless the brief names the place
(`docs/month-order.md`); a `SAVE_FORMAT` bump; the batch's ship file
(`docs/queue/<id>-ship.md`, its changelog block and queue rows), its notes,
the todo, README's counts and the maps; the review, the checks and the
commit. A batch writes no version: `ship.sh` sets `GameVersion.VERSION` at
the gate, and the full suite is the gate's (BP1). A fix of a few lines is
cheaper made than briefed.

## How

1. **The brief** goes in `target/deepseek/brief-<slice>.md` (`target/` is not
   tracked). The template is below; the more of the design it settles, the
   shorter the review.
2. **Run it in the background** and wait for the notification (a slice takes
   10-30 minutes); never poll:
   `python3 tools/deepseek/agent.py --brief target/deepseek/brief-<slice>.md`
   (on the PC, `py` or `python`). It prints only DeepSeek's report, one stats
   line and the dollars spent; its transcript is in
   `target/deepseek/run-*/transcript.jsonl`, for grep, never for reading whole.
3. **Review** `git diff --stat`, then the diff, against *The review* below.
4. **One follow-up**, the points numbered:
   `python3 tools/deepseek/agent.py --followup target/deepseek/review-<slice>.md`
   continues the newest run (`--run <folder>` another). Make the last small
   fixes yourself rather than brief a third round.
5. **Your checks** (`./batch-check.sh --pick`: the build, the maps, Stale
   and the harnesses that read what the batch changed; the full suite is
   the gate's), then commit; the commit body names
   the files DeepSeek drafted ("drafted by DeepSeek V4 Pro through
   tools/deepseek, reviewed by Claude").

`agent.py --help` lists the options (`--model deepseek-flash` for lookups;
`--write` the prefixes it may write, the generated docs refused whatever is given).

### The brief

- **Title:** the batch, the slice and what it is.
- **What the game wants:** the spec's section with its lines, the decision
  taken (who, when), and the nearest existing mechanic to mirror, with its
  file and lines.
- **What to write:** each file, its rough size, the class's shape and
  signatures, the dials and the rule each sets, and what is NOT this slice
  (the next slices, `Game`, the screen).
- **The harness:** a small existing one to model, each section and the
  fixture that causes it, and its line in `AllChecks.HARNESSES`.
- **Done means:** `compile` clean and the harness exits 0 with no FAIL line
  (a `slice` runs it through `./batch-check.sh`, which times soft; a bare
  `java ... <Name>` takes `-Dcbs.timing=soft` while another JVM runs, TM2).

### The review

What the gate sent back in the test, and what DeepSeek cannot do itself:

- The exit code is the count of fails; the harness's header lists what it
  proves, matching its sections.
- Fixtures are built from the model's constants, not bare numbers, and a
  threshold is caused from both sides.
- The premise was moved to get green (a seed or fixture swapped because the
  default showed nothing), or a case already known to pass sits in a sample
  that is meant to measure.
- The harness copies the model's formula instead of calling it.
- A default or a tie carries a bias from loop order (the first bearing:
  every inland city's gateway due east).
- A file the brief did not name was touched.
- Not DeepSeek's to do: the maps; the Claude session regenerates them.

## Access, money and limits

- **The key** is Jerus's DeepSeek account ($20 topped up, 2026-10-17). In a
  cloud session the environment's network secret on `api.deepseek.com`
  injects it; on the PC `DEEPSEEK_API_KEY` is a user environment variable;
  never commit it. The balance: `curl -sS https://api.deepseek.com/user/balance`
  (the script prints `spent_usd` and `balance_after`); below $2, tell Jerus.
- **What leaves the machine:** the brief and every file DeepSeek reads go to
  DeepSeek's servers (Jerus asked for it, 2026-10-17). Never put a save, a
  key or anything personal in a brief.
- **The models** (`curl -sS https://api.deepseek.com/models`):
  `deepseek-v4-pro`, which reasons before it answers, the reasoning counting
  against `--max-tokens`; `deepseek-flash` (V4.1 Flash), which the old name
  `deepseek-chat` now gives.
- **In the cloud** an agent cannot start another agent (depth 1), so a batch run
  as an agent runs DeepSeek through Bash, and DeepSeek's own `ask_deepseek`
  helpers are not to be counted on. The workbench runs one JVM at a time (the lock
  `batch-check.sh` takes, `$CBS_JVM_LOCK`); the PC checks side by side (TM1), and
  a harness that finds another JVM running times soft (TM2). The rest is in
  `docs/notes/batch-history.md`, "The DeepSeek test and its limits".
