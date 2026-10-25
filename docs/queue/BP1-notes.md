# BP1 - the batch process, cheaper and quicker (no tag, 2026-10-25)

Jerus, 2026-10-23, in the process-cost session: the batch process is "quite
the slow and token heavy process"; on the list, "yes, all recs". On
2026-10-24 he asked to start it at once, from the end of the queue, and to
"expirement with the setting ultracode": every phase of this batch ran as a
multi-agent workflow (the last section). The brief is `docs/queue/BP1.md`.
No game file changed; the version stays main's and the merge takes no tag.

## What it built, item by item

| item | where it lives now |
|---|---|
| 1 a fresh orchestrator each day | `docs/batch.md` "The day"; the PC memory note `orchestrator-handoff.md` (40 lines, overwritten each evening); `sh tools/day.sh`, the morning in about fifteen lines |
| 2 a turn ends only when done or blocked | `docs/batch.md` "A batch session" 7; `tools/wait.sh --line FILE ERE --for 9` (a wait inside a turn) and `--row PRED` (a lane's wait, in the background) |
| 3 one full suite, at the gate | `./batch-check.sh --pick` and `tools/pick.sh`: the harnesses that read what the diff changed; no baseline - main's reds are in `docs/notes/gate.txt`, written by every gate; exit 3 = only main's recorded reds, 4 = new ones |
| 4 the version at the gate | a batch writes `{next}` and its records in `docs/queue/<id>-ship.md`; `ship.sh` sets VERSION to the last tag's patch + 1, fills `{next}`, splices the changelog block and the queue rows, renames `docs/ensemble/<id>.md` |
| 5 stranger's reviews for money, saves, threading | `tools/pick.sh`'s review line, a floor; `docs/batch.md` "The gate" 2 |
| 6, 7 fewer, larger slices; Sonnet as the whole session for a small fix | `CLAUDE.md` "Coding goes to Sonnet" (one sentence added); `docs/deepseek.md`; `docs/batch.md` "Which model" |
| 8 a shorter CLAUDE.md | 309 to 246 lines: "How a batch is done" is `docs/batch.md`, which a slice never loads |
| 9 the orchestrator answers and takes the recommended calls | one sentence added to the standing rule on Pending; `QUEUE.md`'s rules; `docs/batch.md` "The day" |
| 10 a screen-check session | one sentence added to the standing rule on save slots (7-9 its to write, 1-6 nobody's); `docs/batch.md`; the memory note `saves-never-matter.md` |

The night's lessons: (a) a blocked batch to Pending at once - `QUEUE.md`'s
"Not done" rule and "The day"; (b) the hourly check-in - "The day": a
CronCreate job at minute 7 running `sh tools/day.sh --due`, deleted at the
day's end, the old orchestrator archived by the next; (c) earth-2.bin at the
gate - "On the PC", "The Earth file", `pc-shipping.md`; (d) every
`batch-check.sh` run but `--timing` now times soft, and `ship.sh` compares a
timing red with main's own run the same day; (e) xhigh or max - `CLAUDE.md`,
"Which model", `effort-xhigh-or-max.md`; (f) the version set at the gate.

Beyond the brief, on Jerus's "feel free to build tools that would help other
sessions" (2026-10-24): the ship file (records conflicts gone, not just the
version's); a conflict only in the generated docs taken from the branch and
regenerated (ET1's re-merge was mostly that); the docs-only gate on `--fast`;
one atomic push of main and the tag; the dry run showing the branch's own
version (it read main's after the abort before); `tools/ship-selftest.sh`
(81 cases, no JVM, about two minutes); `tools/day.sh` also calls an
old-style finished branch ready (ET2 was, unreported); HarnessMap's index now
has rows for ui/ and tools/ classes.

## How it was proved

- `./batch-check.sh --fast`: green after each phase; Stale 0 firm findings.
- `./batch-check.sh --pick` for real on BP1's own diff: 1 harness
  (EnsembleCheck, from the ensemble hint) in 6 s, where the suite is 15-22
  minutes. `sh tools/pick.sh M^1 M` on real merges: FW3 18 harnesses (its 11
  GameVersion readers go under the new way), ET1 50 with Game.java and
  Founding.java left to the gate and its save and threading changes named on
  the review line, FW4 12.
- `tools/ship-selftest.sh`: 81 of 81 under bash and under dash (stubbed
  checks, a local bare origin); the same cases give 65 of 81 against the
  first `ship.sh`, the 16 failing being the review's fixes.
- The one full suite, on the final tree merged with origin/main (0.8.43)
  and with earth-2.bin named as a gate names it: 139 harnesses in 1,025 s,
  138 passed, MapCheck 8 red (main's, FW11). With no `gate.txt` anywhere
  yet it said exit 4. Its red lines seeded `docs/notes/gate.txt`, and a
  batch now reads them as exit 3.
- The real `./ship.sh --dry-run batch/TST -- StaleCheck` in a temp clone
  whose origin was a local bare copy at BP1's tip (main 0.8.43). TST was a
  throwaway branch with a comment in Mining.java, a ship file headed
  `### {next}` with a Pending row, a notes file with `{next}`, and a Queued
  row on main. It printed `{next}=0.8.44 in 2 file(s), TST's block on top,
  1 Pending row(s), its Queued row out`. The suite step was green. The hard
  timing run read MapCheck's lines as "red as main only", which needs no run
  of main's. It ended `would commit "Merge batch/TST: ..., 0.8.44", tag
  0.8.44 and push` and `the tree is as it was`: no change, HEAD unchanged,
  no worktree left, no tag. The clone was then deleted.
- The merge of origin/main (FW4, ET2) met conflicts only in
  `docs/harnesses.md` and `docs/map/README.md`, the generated docs, which the
  new gate takes from the branch and regenerates.

## The calls (★), each built as recommended but the second

1. A class most harnesses read (Game.java 120 of 138, GameFiles, Founding and
   the like, more than `CBS_PICK_FANOUT`, 50) is left to the gate's full
   suite, LongPlaytest run in its place, rather than the whole suite in the
   batch. Recommended: item 3's "one full suite per batch, at the gate".
   `CBS_PICK_FANOUT=999` flips it; reopen if more than one of the first four
   batches bounces at the gate on a harness so left.
2. Who returns a blocked batch to Queued when its block clears by itself.
   Recommended: the orchestrator, saying so in the queue commit. Not built:
   it moves the standing rule on Pending, so it is his.
3. No more paragraphs above VERSION in GameVersion's javadoc (two lanes
   writing one collide; the changelog is the record). Recommended.
4. Main's recorded reds pass the gate word for word; a new red only with
   `--red-ok` and his words. Recommended: his by-hand rule
   (`ship-past-known-reds.md`) made mechanical.
5. A timing red that main shows too, the same day on the same machine,
   passes and is named in the merge body. Lesson d. Recommended.

## Left undone (BP1-C under Pending)

- The first batch after BP1 reports its token cost against BP1's figures
  (Opus batch session 400k-770k, stranger's review 190k-260k, three suites).
- `Docs pass.bat` runs a bare `claude -p`: its effort is the CLI's default
  (lesson e), unchecked.
- `Batch check.bat` has no `--pick` and takes `--pick` for a harness name
  (0 run, "All clear"): told to TM2, whose file it is.
- The change-over code (the old-style path in `ship.sh` and in
  `tools/day.sh`) goes once no branch from before BP1 remains.

## Shipping BP1 (for the orchestrator)

BP1 ships with no tag, and **by hand, without `ship.sh`, this once**:
- Main's `ship.sh` is the old one.
- Main carries MapCheck 8 (FW11), so the new `batch-check.sh` on the merge exits 3. That means main's reds only, read from the `gate.txt` this branch seeds.
- The old script reads any exit but 0 as a failure. It would spend a 17-minute suite and then stop.

So do it in a fresh `%TEMP%\cbs-BP1` clone of origin with main checked out (`pc-shipping.md`), in Git Bash:

```
git merge --no-ff --no-commit origin/batch/BP1
export JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin
./batch-check.sh; echo "exit $?"          # 0, or 3: only the reds gate.txt records
cp target/checks/reds.txt target/checks/suite-reds.txt; S=$(grep 'harness.* run in ' target/checks/suite.txt)
./batch-check.sh --timing; echo "exit $?" # 0 or 3; on 4, run main's own --timing today and compare
V=$(sed -n 's/^ *public static final String VERSION = "\([^"]*\)";.*/\1/p' src/main/java/ham/citybuildersim/GameVersion.java)
{ echo "# gate of Merge batch/BP1: the batch process, cheaper and quicker, $V"; echo "# date: $(date +%Y-%m-%d)"
  echo "# earth-2.bin: named"; echo "# suite: $S"; echo "# timing: by hand, under the old ship.sh"
  cat target/checks/suite-reds.txt; } > docs/notes/gate.txt
git add -A docs/ && git commit -m "Merge batch/BP1: the batch process, cheaper and quicker, $V" -m "Gate: red as main's recorded: MapCheck (by hand, the last ship under the old ship.sh)"
git push origin main
```

From the next batch on, `./ship.sh batch/<id>` does all of it, a batch built
the old way included (no ship file: its records as it wrote them; a VERSION
main has tagged already is stopped - merge main and bump, or write a ship
file).

## The ultracode experiment

Every substantive phase ran as a Workflow, each agent at xhigh (Jerus's
rule since 2026-10-24); the coding agents on Sonnet, the reading, judging
and document agents on Opus. Figures from each run's own usage line.

| phase | agents | subagent tokens | tool calls | wall time |
|---|---|---|---|---|
| understand: four scouts (harness index, version, CLAUDE.md's readers, process docs) | 4 | 470,928 | 140 | 9 min |
| design: three designers by lens (least change, cost, safety), two judges | 5 | 1,048,276 | 132 | 37 min |
| implement: five slices on files of their own | 5 | 1,030,338 | 331 | 56 min |
| review: five finders by lens, a skeptic per finding (two for a high one) | 40 | 4,389,925 | 739 | 7 h 49 min, of it about 25 min of work |
| fix: four slices | 4 | 592,194 | 161 | 28 min |
| **in all** | **58** | **7,531,661** | **1,503** | about 2.6 h of work |

The orchestrating session (Opus) added its own context, about 370k at the
end, re-sent on each of its calls. The account's weekly limit read 94% when
it reset during the run (2026-10-25 04:00 UTC), with extra usage enabled.

**What the workflows caught that one pass would likely have missed:**
- The scouts found the traps the design had to close: AllChecks runs the
  WHOLE suite when given no names and skips an unknown name without a word;
  `docs/dials.md` carries VERSION, so the version must be set before the
  maps; today's `--dry-run` reads main's VERSION after its abort; the
  ensemble baseline 0.8.37.md was PT1-M4's run, shipped as 0.8.39.
- The design panel produced the ship file and the generated-docs rule. A
  single design would most likely have stopped at `{next}`, which leaves the
  changelog, QUEUE.md and the maps colliding, so "no longer collide" would not
  have held. The judges caught four bugs in the designs before any code: a
  final exit of max(FAILED, SUITE) that would ship a Stale failure beside
  main's reds; `--fast` dropping typed names; a `docs/queue/FW1*` glob
  catching FW10; one shared `next.md` baseline for two lanes.
- The review: 30 findings, 22 upheld by their skeptics, 8 refuted (three of
  them one finding seen by three lenses). The upheld high ones crossed
  components, which is where a self-review is weakest: `--red-ok` could not
  ship a red in any of the eleven timing harnesses; a timing red once
  recorded would later pass with no same-day comparison; `day.sh` never
  called an old-style finished branch ready (ET2 was, that hour); `gate.txt`
  was never seeded. Mediums: an in-flight branch whose VERSION main had
  tagged already would have shipped a second 0.8.42 untagged without a word;
  yesterday's orchestrator's cron would keep waking it; `Batch check.bat
  --pick` says "All clear" having run nothing; a crashed harness with a
  negative exit code read as main's red.

**What it cost and where it went wrong:**
- About 7.5M subagent tokens: ten to nineteen times BP1's own figure for an
  Opus batch session (400k-770k). The review alone was 58% of it.
- The review's wall time was 7 h 49 min for about 25 minutes of work. One
  finder's brute-force comparison outran its 10-minute call; its next model
  response was cut off mid-stream at 04:37 UTC and retried at 11:42. A
  workflow waits for its slowest agent and has no per-agent time limit.
- The design phase re-read what the scouts had read: each designer scouted
  again.
- Thirty-five skeptics for thirty findings: most refutations took a minute,
  and the two-skeptic rule for high findings never changed a verdict.

**Verdict:** worth it for this batch, which rewrote the gate every later
batch passes through: the review's high findings would each have surfaced as
a stopped or wrong ship. Not for an ordinary batch: at ten times the cost it
undoes what BP1 is for. If used again: the adversarial review alone, three
lenses and one skeptic each, on a batch that changes money, saves, threading
or the gate (item 5's list); a design panel only where the solution space is
wide; no separate understand fan-out (a designer reads for itself); and a
time limit on every agent.
