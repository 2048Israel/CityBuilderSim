# DeepSeek - the coder a Claude session hands a slice to

A batch's Claude session hands the coding a brief can pin down to DeepSeek V4
Pro, through `tools/deepseek/agent.py`: Claude decides the design, writes the
brief, reviews the diff, runs the checks and commits (in a small fix batch
Sonnet is the whole session, below); DeepSeek does the reading and writing in between, at about a ninth of Claude's price per token.
Jerus, 2026-10-17: "update the required docs so that new batches with claude
remember to use deepseek". DeepSeek is not an assistant like ChatGPT or Gemini:
it has no branch and no brief of its own, and nothing it writes is committed
until a Claude session has read it.

**Sonnet first, for now** (Jerus, 2026-10-20): "for a long while there will
be no deepseek so its sonnet". DeepSeek is billed by the token on top of his
20x subscription, and Sonnet runs inside it, so until he has the budget for
more agents every slice goes to an Agent subagent with model "sonnet"
(Sonnet 5.5) and effort "xhigh", handled as this page handles DeepSeek: the
same brief, the subagent edits the worktree and commits nothing, runs only
the harness its brief names, and Claude reviews the diff as a stranger's and
commits. Since BP1 (2026-10-24) the slices are fewer and larger, and Claude
reviews the diff without re-reading what the brief sent the subagent to
read: an Opus batch session cost 400k-770k tokens with Sonnet already doing
the edits, much of it that re-reading and its own context re-sent on every
slice. A small fix batch - no money, saves or threading, a few hundred
lines - runs on Sonnet as the whole batch session, Opus seeing it only at
the gate (which amends "Which model", `docs/batch.md`). The trial ran from FR1 to FX4 (2026-10-18/19), about forty slices
in eight batches, each batch's notes keeping a table of what the review
changed: about half needed nothing; the map, drawing and harness slices
were nearly always right, and the slices sometimes corrected the brief; the
model slices needed real fixes (FR3's dues never reaching the treasury,
FR2's Haulage rules, every id across a save). So a model slice's review is
the careful one, and most of all where money, saves or threading change
(BP1): the bank, bonds, the treasury and funds; `DataSave`, `HistorySave`,
the load path and `SAVE_FORMAT`; threads and anything UI-wide. Those get a
stranger's review at the gate as well, and `tools/pick.sh`'s review line
names them. When DeepSeek returns, Sonnet is its fallback whenever
`DEEPSEEK_API_KEY` is unset or the API fails.

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

## What it costs (the test, 2026-10-17)

- DeepSeek did FR0's land gateway bearing (a model class of 128 lines, a
  harness of 174, two rounds) in 95 requests and 3.7M tokens for **$0.29**.
  The same tokens on Claude Opus would have cost about $2.50.
- The Claude agent supervising it cost about **$4.80**, as much as writing
  the slice itself (about $3-4). It built this script from scratch (about
  $1-1.50 of that), wrote two briefs and reviewed twice. With the script here,
  the aim is the Claude side at **$1.50 or less a slice**: one brief, one
  review, at most one follow-up. The saving grows with the slice.
- Quality: it compiled and its harness passed the first time; 6/10 as
  delivered, 7/10 after one review. It follows the conventions; its harnesses
  took shortcuts until told (the list under *The review*, now in
  `tools/deepseek/system.md`, so it is told up front). The slice is commit
  `6fd8dda`, reverted in `b24f009`: `git show 6fd8dda` is the worked example.

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

Options: `--model deepseek-flash` (cheaper and faster; for lookups);
`--write src/,docs/studies/` (the prefixes it may write, `src/` by default,
`''` for a read-only run; the generated docs are refused whatever is given);
`--turns 40`; `--depth 3`.

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
- **Done means:** `compile` clean and the harness exits 0 with no FAIL line.

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
- Not DeepSeek's to do: README's harness count (StaleCheck goes red when a
  harness is added) and the maps; the Claude session does both.

## Access, money and limits

- **The key** is Jerus's DeepSeek account ($20 topped up, 2026-10-17). In a
  cloud session the environment's network secret on `api.deepseek.com`
  injects it, and the script sends a dummy. On the PC, set `DEEPSEEK_API_KEY`
  as a user environment variable; never commit it.
- **The balance:** `curl -sS https://api.deepseek.com/user/balance`; the
  script prints `spent_usd` and `balance_after`. Below $2, tell Jerus.
- **What leaves the machine:** the brief and every file DeepSeek reads go to
  DeepSeek's servers (Jerus asked for it, 2026-10-17). Never put a save, a
  key or anything personal in a brief.
- **The models** (`curl -sS https://api.deepseek.com/models`):
  `deepseek-v4-pro`, which reasons before it answers, the reasoning counting
  against `--max-tokens`; `deepseek-flash` (V4.1 Flash), which the old name
  `deepseek-chat` now gives.
- **Agents in the cloud nest one level.** The cloud launcher sets
  `CLAUDE_CODE_MAX_SUBAGENT_SPAWN_DEPTH=1`, and Claude Code keeps a launcher's
  variables over the environment's own and over settings files (checked
  2026-10-17), so a session can start agents but an agent cannot start
  another. A batch run as an agent runs DeepSeek through Bash, which works at
  any level. DeepSeek's own helpers (`ask_deepseek`) nest to `--depth`; in the
  test it delegated only when told to, and two of its four helpers ran out of
  turns: do not count on them.
- **One JVM at a time on the cloud workbench:** there the script's compile
  and harness wait on the lock `batch-check.sh` takes (`$CBS_JVM_LOCK`). The
  PC has no lock: batches check side by side (TM1), and a harness the script
  runs times hard, so a timing red it reports while another batch or the game
  runs is rerun through `batch-check.sh` or `Batch check.bat`, which time soft.
  They decide soft or hard once, as the suite starts, never per harness
  (lesson d, `docs/batch.md`); a timing red at the gate `ship.sh` compares
  with main's own timing run, the same day on the same machine.
