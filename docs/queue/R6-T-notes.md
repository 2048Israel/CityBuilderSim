# R6-T notes: the speculative tier, 2030-2060, a study

Batch R6-T, a Claude session (Opus 5.5 at xhigh) started by the orchestrator on
2026-11-16 after the night's Codex builders ran out of plan; worktree
`cl-R6-T`, branch `batch/R6-T` from origin/main 205ceca1 (0.9.124), main merged
in at fad1af99 and again before the dry run. The brief: QUEUE.md's R6-T row and
`docs/queue/R6-notes.md`, R6b's row, `docs/studies/research-near-future.md`.

## What was written, and where

- `docs/studies/research-speculative.md`: the study. Sections 0 in short, 1 the
  rules kept, 2 the allowance, 3 the eight rows (3.1 the table in R6's columns,
  3.2 each row with the event that moves it), 4 what stayed out (4.1 dated with a
  figure and nothing in the game for it, 4.2 dated with no effect target, 4.3
  goals for outcomes, 4.4 carbon), 5 the work rule, 6 four calls, 7 for the
  batches that build them, 8 the sources X1-X15.
- `docs/studies/research-speculative/`: the four gatherers' sheets (energy,
  industry, medicine, business and law), every source with its mark (page,
  snippet, memory), what was searched where a target was not found.
- `docs/studies/README.md`: its row, at the top (newest first).
- `docs/queue/R6-T-ship.md`: the changelog block, `## pending` R6-T-K (the
  calls) and R6-T-P (six rows for their owners), `## queued` R6d.
- No `research.json`, no Java, no harness; `docs/tree.md` needs no line (R6's
  study added none, and the tree check is clean).

**Why `docs/studies/` and not `docs/queue/`:** it is a study, its sources and
sheets beside it, as CLAUDE.md puts studies since 2026-10-11; and it is the
second half of R6's, whose study `research-near-future.md` stands there beside
`research-design.md` while its brief, `R6-notes.md`, is in `docs/queue/`.
Read together, the three are the catalogue's design.

## How it was gathered

Four gatherers, `general-purpose` agents on Sonnet at xhigh, one a subject list
each (energy 7 subjects, industry 10, medicine 11, business and law 8, from R6's
watch list and the queue row's "a builder's roadmap, a national plan, an
agency's target"), each told to cite only pages it read, to mark each figure and
to say where a target was not found. I ended a turn while they ran and the
session stopped them; on resume two (medicine, business and law) were continued
by SendMessage and finished. Those two sheets were trimmed by their agents after
I had read them (about 31-32 KB to 24-25 KB); every figure the study uses was
grepped in the committed sheets before the study was committed.

Opened again from this session, the pages of X1-X4, X7-X10 and X12-X15 (12 of
the 15): the M72 registry and the Gates MRI launch, POWER on Kemmerer 1,
Precision Farming Dealer on Deere, pv magazine on Oxford PV, DOE's PEM targets,
ANS on Helion, CFS's ARC page, ThinkGeoEnergy and Renewable Energy World on
Quaise, CleanTechnica on Space Solar, AlCircle on OHISAMA. X5, X6 and X11 are the
gatherers' page marks.

## The calls

For Jerus (study §6; pending row R6-T-K), each recommended (a):

1. ★1 the allowance, staged by what had been shown when the date was given:
   every date traced slipped, by more the less was shown (Natrium, Stegra,
   Salzgitter, Electra, Boston Metal 2-3 years before building; ITER, DEMO,
   China's thorium reactor 4-12 with the deciding test unread).
2. ★2 a row's target is physical; a cost is noted and never the figure, as R6's
   rule 4: the game's prices decide what pays.
3. ★3 R6b's one mark; the window's width says how sure a date is, and a third
   key would change nothing in play.
4. ★4 2036-2060 left empty, read again at the named events and when
   `HISTORY_ENDS` moves: a goal for deaths is not a discovery, and no builder
   dates a new thing there with a figure.

Taken here, each with its reason:

- **Codes** FE7-FE13 and FM3, continuing R6's (★7); source keys X1-X15 (R6 used
  N, S1 exists).
- **A builder's words as a year** ("early 2030s" 2032, "mid-2030s" 2035, a range
  its later end) and **the window** (opens at the earliest stated date, closes at
  the later of the latest stated date and the central date plus one more slip of
  the stage, never after 2060): both give R6's windows back on R6's rows.
- **Fusion's date is the leader's** (Helion's Orion, 2028 on line, full power a
  year later, + 5 = 2034), as R6 took Linglong One's for FE6; CFS's 400 MW and
  STEP's 2040 are the window and the effect's range.
- **FE7 graded likely**, the others speculative: it is being built.
- **Dropped:** a gigawatt orbital array "within 25 years" (about 2051), whose
  article names nobody; Europe's DEMO (no current date found, its 2017 date
  after 2054).
- **R6d takes FE8 and FE10** because each has something in the game or in R6b
  (the farms' operator posts; FE4's works); FE8 also needs R2-R's tractor gain,
  so the row says FE8 goes to Pending beside R2-R if that is not done.
- **R6-G's codes:** R2e used E19 and E20, so the gap's rows begin at E21 (§7).

## Checks

`./batch-check.sh --fast` (JDK_JAVA_OPTIONS naming data/earth-2.bin and
data/world-1.bin): see below. The dry run: below, with its kept log and the two
commits it was of.
