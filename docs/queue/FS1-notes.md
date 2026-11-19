# FS1 notes: the tests that break when another batch moves the played city

A study, untagged, docs only; built 2026-11-18 and 11-19 by a headless Claude
session the orchestrator started, from `docs/queue/FS1.md`, on batch/FS1 cut
from main 1f6a9da1a (0.9.153).

What it wrote:

- [docs/studies/flaky-suite.md](../studies/flaky-suite.md): the study, with the
  sweep briefs FS2-FS17 as its sections 6.1-6.16 and the call FS1-C as its
  section 7;
- its three tables, [by-harness.md](../studies/flaky-suite/by-harness.md)
  (224 rows), [by-section.md](../studies/flaky-suite/by-section.md) (619 rows,
  every played or clocked section) and
  [red-lines.md](../studies/flaky-suite/red-lines.md) (70 rows);
- its row in `docs/studies/README.md`;
- these notes, and the ship file `docs/queue/FS1-ship.md`.

No source, no harness, no JVM but `batch-check.sh --fast`.

## 1. What was read

- `docs/contract.md` whole; the brief; QUEUE.md's rules and rows; PO1's brief
  and its notes' audit (`Observation`); PK1's brief (its Done allows "play
  until a stated condition holds, up to a bound, failing as a fixture", never
  a picked month); HL1-R's brief; `.claude/agents/slice.md`;
  `docs/notes/known-reds.txt` and `gate.txt`; LQ1's ship file in git (the shape
  of a study's ship file: a pending call row, queued rows linking the study's
  sections); `ship.sh` 398-456 (what a docs-only branch is) and 328-356 with
  `tools/known-reds.sh` (how a ship file re-files a known-reds row).
- Every harness, by ten read-only slices (the `slice` agent, Sonnet, xhigh,
  one at a time in the foreground, about 22 harnesses each, grouped by area:
  money, map, the screens in two, play, people, currency, build, industry,
  land). Each read its harnesses' headers, each section's code and the helpers
  it calls, and each borrowed fixture's definition, and wrote one line per
  harness and per played or clocked section with a kind and a verdict, and one
  per red line.
- A throwaway script outside the tree (in the session's temp folder, not
  committed) first marked each printed section with crude markers (LongPlaytest
  calls, month calls, loop bounds, "some month" phrases, clock calls,
  Observation sites, borrowed fixtures) to give each slice a reading list, and
  later wrote the three tables from the slices' lines. The markers missed
  month calls by other names (`simulateMonths`, `advanceMonth`,
  `toggleNextMonth`) until widened, and undercounted harnesses whose titles are
  printed from a variable (MapCheck has 49 sections, not 40; ShortSellingCheck
  9; QuayCraneCheck 6): the slices classed the real sections, so the counts
  are theirs.
- The kept gate runs: 316 `/tmp/cbs-gate-*` folders from 2026-11-11 22:33 to
  11-18 22:29, read only, nothing run inside; 187 have a `checks/suite.txt`,
  and every `NEW red` and `known red` line was tallied by batch and harness.
  R1d (the whole suite red, a process fault) and the SH runs were left out.

## 2. What was found

The counts are the study's section 3. In short: 2,619 sections, 2,027 caused,
543 played (identity 167, found 258, picked 104, order 14; 66 already
Observations), 77 clocked (28 of them also played). Of the 543, 425 can cause
their condition (67 with a kept identity beside the cause) and 118 rightly
stay played. 77 harnesses are caused throughout, 8 played only for
identities, 11 clocked only, 128 have sections to cause.

Corrections to the brief, each from the code:

- **PX2 is a real fault, not a moved path.** PlanCheck 6's + floor is an
  identity (every district's plan keeps it at every 30th month); LX1's city
  found a plan that breaks it (district (0, 0), two + junctions 7 plots apart
  against `LATTICE` 8). It is not folded; FS6 does not run beside it.
- **MQ1 is a played row, not a loaded one.** MonetaryCheck 8 settles on the
  price index's rate at a month found on main's path (97) within an allowance
  measured there: `picked(obs)`, to FS10.
- **KRT1 is unclear.** A `Text`'s bounds, 15.0 kept against 16.0 built new, is
  either a layout that differs run to run or a kept fault on that path: FS4
  traces it first, model and clock held still.
- The brief counted 221 harness files on 79e336def; 1f6a9da1a has 224.

Found on the way:

- **LandNationsCheck 14's red is one wait, not eleven.** The hard red is the
  hover's 10 s `await`, ANDed with `Timing.within(128 ms)`; after it an
  uncaught 10 s `awaitTermination` in the `finally` (L1734) kills main, and
  `AllChecks.FAILING` then reads lines off the dead process's tail, among them
  eight of the section's own `OK` lines and its expected exception: nine of
  QW1's eleven known-reds rows are lines that passed.
- **The four KS1-rest timing rows are vestigial.** Each is a line through
  `Timing` (KeptCheck's `KEPT_ALL_CHROME_MS`, MapCheck's three GT1-T lines),
  soft at every gate since GR1. FS4 and FS6 retire them.
- **Two old reds have been mended since their records.** MoneyDatesCheck's
  (section 4, the exhausted-gold twin, known red at 26 gates) is fixed on
  1f6a9da1a; ResearchViewCheck's (known at 43 gates) was mended by R2b-V and
  FB10.
- **KRT1's slider row is section 78's**, KP6's zone tax dial (`holdDial` in
  `kp6Controls`); the same label is printed by sections 34 and 45, but the red
  was 78's, so it goes to FS5 with the property rate (77), and KRT1's Wood
  Products (50) and Construction (49) go to FS4.
- **The most red harnesses at others' gates** (NEW red at kept gates, 11-11 to
  11-18): KeptCheck at 10 batches' gates; PriceLevel, Map, LandNations and
  ResearchView at 4; Prize, LongPlaytest, Landmass and CopperRefining at 3.
- **The slices sometimes offered `Observation.reached` as a cause.** It is
  not one (★3): the briefs take the cause and drop the Observation.

## 3. The calls

- ★1 **A sweep edits only its own harnesses**: no LongPlaytest, no fixture
  builder another sweep's harness borrows. Why: a sweep's gate is then the
  picked harnesses, not the suite, and no sweep moves another's city, which
  is the fault being mended. Where a borrowed builder must change, its
  borrowers are in the same sweep (MapCheck.playedCity and PlanCheck,
  copperTown and CopperRefining, HaulageCheck's base and HomeRail,
  SaveSlotCheck.grownCity and ReplayCheck); `ChildcareCheck.town`, borrowed
  across FS12 and FS15, stays as it is. CL7's LongPlaytest ring line stays
  with CL7.
- ★2 **The order of causes**: the state set directly, then a town built for
  it, then a threshold from both sides on the model's constant, then the read
  at the caused month; a bounded "play until", failing as a fixture (PK1's
  Done), only when none will do. Why: the earlier ways have no path in them at
  all; the last still has one, but bounded and loud.
- ★3 **A premise now caused is no Observation**: its laws run hard. Why: an
  Observation keeps a gate green by not proving its law; PO1 was the stopgap
  until the premise could be caused.
- ★4 **KEEP identities stay played**, and a red there is a bug row. Why: an
  identity is true of every state, so the played city is breadth, not
  premise, and a red is a real fault (PX2), which re-measuring would hide.
- ★5 **The sweeps run in the order of today's reds**: FS2-FS6 carry 33 of the
  41 re-filed rows and the harnesses most often red at others' gates; FS13 and
  FS15-FS17 are preventive. Why: the reds cost gate time now; the preventive
  sweeps can wait for a quiet queue.
- ★6 **The clocked call (FS1-C), recommended**: each plain wall-clock wait
  becomes a clock-free wait on the work's own completion with a long hang
  guard, its speed half a `Timing` line judged nightly; the 28-minute child
  and KeptCheck's run time nightly; KBP1 and KRT1 traced first; the far hang
  guards kept; no harness "run alone". Why: a correctness harness then never
  fails on load, and a slow machine still shows in the nightly timing record.
  Jerus's; FS3 builds it unless he says otherwise.
- **The sweeps' size**: one gate each, 13-37 sections to cause, grouped by the
  fixture builder or area the harnesses share, so a slice can read one area's
  builders once. KeptCheck is two (FS4 below section 55, FS5 from it): 47
  sections on one city are more than one gate's work, and the split of its
  timed sections (FS4) goes first.
- **The re-filed known-reds rows.** A ship file's `## known-reds` row with the
  same harness and fragment as a registry row replaces it
  (`tools/known-reds.sh`), so FS1's ship file re-files the folded rows' 41
  reds to the sweep that takes each, their causes kept after a "Folded from
  ... by FS1" prefix. Why: a batch's own rows are vetoed at its gate, so a
  sweep then ships only green on the reds it took; left with the old ids, the
  rows would exempt the sweep's own harness at its own gate.

## 4. The batches queued

FS2-FS17, all after HL1, each a harness-only gate (the picked harnesses),
FS4 a dry run (its split of KeptCheck changes `AllChecks` and `Timing`, so the
pick is ALL); FS3 after Jerus's answer to FS1-C. Their briefs are the study's
sections 6.1-6.16; the "after" and "not beside" columns are its section 6's
table. The ids passed `sh tools/batch-id.sh`. Pending: FS1-C, Jerus's call on
the clocked lines.

## 5. The rows folded

Each against the batch that takes it; the orchestrator takes the row off the
queue when FS1 ships (the ship file cannot).

| row | taken by |
|---|---|
| HP1 | item 1 FS10, item 2 FS12, item 3 FS2 |
| HP2 | items 1 and 3 FS2, item 2 FS11, item 4 FS9 |
| SX2 | FS14 |
| KFP1 | FS5 |
| FF1 | item 1 FS7, item 2 FS8 |
| PK1 | ScaleCheck's half FS2, FundCheck's FS13 |
| KRT1 | Wood Products and Construction FS4; the slider and the property rate FS5 |
| KBP1 | FS5 |
| KRW1 | FS5 |
| QW1 | FS3 |
| MQ1 | FS10 |
| BQ1 | FS6 |
| LM1-R | FS6 |
| KS1-rest | KeptCheck 49, KT1, KA1, KP2-N to KP4-N FS4; CD9-K, KP5-N to KP7-N FS5; the GT1-T map lines FS6 |
| HL1-R | item 2 (ScaleCheck's month 165) FS2; the row stays for item 1 (Business Services) |
| PX2 | not folded: a real fault; its row and its two reds stay |

The re-filed reds by sweep: FS2 7, FS3 11, FS4 3, FS5 9, FS6 3, FS7 1, FS8 1,
FS9 2, FS10 1, FS11 1, FS12 1, FS14 1 (41). The harnesses' other real reds
keep their rows (CL7, DK1, EDU1, HS1, MR1, OE1, OX2, PX2, R3b-R); the sweep
that shares a harness with one runs apart from it.

## 6. Checks

- `sh tools/batch-id.sh` for FS1-C and FS2-FS17: free.
- `sh tools/known-reds.sh FS1 docs/queue/FS1-ship.md`: the ship file's 41
  rows read whole.
- `./batch-check.sh --fast`: see the ship file's commit.
