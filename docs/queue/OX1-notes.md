# OX1 — the orphans behind a wave that ends at once — notes

Built by a Claude session (Opus 5.5, Codex out of plan) on `batch/OX1`, cut from origin/main 9a5d8113 (0.9.131),
merged with ec481b4a (0.9.132, TQ1) at d9490b90 before any trace or ensemble. Brief: QUEUE.md's OX1 row and
[GW1 notes §4](GW1-notes.md) ("Red, and not GW1's to mend"). Files: `FamilyModel.java` (the mend) and
`DeathRecordCheck.java` (section 4, and the m60 pair's month line); nothing in Game or in TQ1's and R6b's files.

## First, measured (main's model, before any change)

**Where the children leave their parents.** `FamilyModel.rebuild()`: the adults at home (Game hands it the out of
work and the students, `Game.java` at the `families.rebuild` call) take the children with them at the city's own
rate, band by band (`followed = atHome / allAdults`), out of the pyramid before the builder runs; last month's
households are then kept where they still fit (2b), every working shape shrunk alike by the adults' shortfall. The
layoff month is not where the orphans come from: on section 4's bare model the wave adds 0.000 over its twin in each
of its six months, and on the played town the closed town's orphans stayed under its twin's while its out of work
rose (5.7 against 7.2 at 13 out of work, 2.3 against 4.0 at 80).

**Where the orphans come from: the month the adults go back.** The children come back to the builder as loose
children in the pool; the half a shape leaves about one in seven over, and the memory (`REFORMING_EACH_MONTH`, 1%),
which already kept every adult in last month's households, frees an adult to take one only that fast. Section 4's
bare model, red on main by hand (fb82d649 on 9a5d8113's FamilyModel): a fifth of 6,000 adults out for six months
then all back at once, 470 children came back, and the orphans read 74.33 against the twin's 6.58 that month, 58.50,
46.51, 35.02 ... 7.89 against 6.58 nine months on. Its two lines FAIL ("the month it ends adds at most one orphan
against the twin" and "...and so does every month of the year after it"; `target/OX1-drc-red.txt`, 4 FAILED with
main's two). DeathRecordCheck's played town on main: the jumps land in the months the out of work fell, 452 to 180
(orphans 2.6 to 22.2) and 234 to 0 (12.6 to 28.2); the open twin jumps 11.6 to 27.1 a month later behind its own
wave. Peak 28.16 against 27.15 + 1 (the known red); the m60 pair's same-month excess 8.404 (the second).

**How often, in a default playtest.** LongPlaytest, 2,400 months on 9a5d8113, read off its existing PEOPLE line
(`-Dplaytest.bankfrom=0 -Dplaytest.bankto=2400`): the out-of-work pool fell in 707 months; in 109 it fell by a
quarter or more and by 50 people or more at once (71 by half or more), a wave ending at once about every 22 months.
Those 109 months added 3,646 of the run's net 27,548 orphans; the other months the pool fell added 9,922, the months it
was flat or rose 13,980. The run ends with 27,549 orphans (4.4% of 619,725 children) and 13,007 orphan deaths of the
19,416 under eighteen: most of the orphan section is the builder's residue on the city's other flows (arrivals,
births, the re-forming), Jerus's "they're the orphans" (FamilyModel's banner, 2026-09-11), not the waves.

## Built

- **`FamilyModel`**: a banner, THE CHILDREN COME HOME WITH THEIR PARENTS. In `rebuild()`, step 2c, after the kept
  households and before the pool, `bringHome()`: the adults back in the families are last month's adults at home
  less this month's (the net), never more than the families' own adults grew by since last month's builder
  (`adultsFormed()`, REV-OX1-1), their children last month's children at home at that household's rate to an adult,
  band by band; `placeTogether()` forms their households by the builder's own order and half a shape, into the
  shapes that hold a child, pass after pass (`HOME_PASSES`, 64) until every child is placed while their adults last;
  their leftover adults join the pool. Memory on only. Counters `getLastBackAdults()`, `getLastBackChildren()`,
  `getLastBackChildrenHome()`, reset with the memory's four. The bare `.5` is `SHAPE_TAKE`, unchanged, with its sentence.
- **Nothing saved, no format bump.** Last month's adults at home and their children are already in the families'
  save (the kin slots, `toSaveArray()` and `restore()`), and the load path restores the households without a
  rebuild, so a loaded city's next month brings the same children home. The counters are this month's and read in it.
- **`DeathRecordCheck`** section 4, `waveEndsAtOnce()` (above; its fixture lines read the model's own rule, the
  children out at `outOfWork / adults` of each band, and the mend's counters); 4b, `nobodyComesHome()`, the same wave
  ending in prison or in leaving the city, with nobody brought home (REV-OX1-1); and the m60 pair prints its months,
  with the children back and placed with them in each town.

## What each check showed

- DeathRecordCheck on 1acfc4f3 (`target/OX1-drc-8.txt`): section 4 green (5.26 against 6.58 the month it ends, 0.000
  at most in the year after; 1,200 back, 470 children back, all 470 home); 4b green (nobody brought home, 0.000000
  adults and children, in prison and in leaving); section 3's "does not fill behind it" green (peak 7.92 against the
  twin's 7.73). The m60 line red at 3.996 (3.851 before the cap), below: re-filed.
- The default playtest on 1acfc4f3 (a probe line not committed): the adults at home fell in 977 of 2,400 months; the
  mend brought adults home in 838 of them and 62,470.6 children with them, every one placed. In 598 of the 977 the cap
  held the adults brought home below the fall, by 7,368 adults in all, who left the at-home count without coming home.
  Before the cap (e4e238a0's model, run on 77a7ccc9): 998 months, 41,999.5 children, one month 0.018 of a child over.

## The review, REV-OX1-1 (not SHIP), and what was mended

1. **The net fall counted adults who never came home.** The adults at home also fall when the out of work leave the
   city (`Migration.HEALTHY_OUT_OF_WORK`), go to prison, are set aside as owners, die or turn 65, and `bringHome()`
   then took that many from the pool and placed the pool's children with them: arrivals as families, which call 1
   leaves to Jerus. Mended: the adults brought home are capped by the families' own adult gain, this month's adults in
   the families less those in last month's formed households (`adultsFormed()`, read off `formed`, which is saved, so
   a loaded city caps the same). Red by hand once: the new 4b against e4e238a0's FamilyModel (`target/OX1-drc-red2.txt`)
   read "adults at home 1200 then 0 (prison), 1200 then 0 (leaving); brought home 48.000000 adults with 18.800000
   children (prison), 48.000000 with 18.800000 (leaving)", FAIL on "nobody is brought home when the out of work go to
   prison" and "...nor when they leave the city" (3 FAILED with the m60 line). What the cap cannot part, the aggregate
   holding no flow by person: a month in which the families grow by others (arrivals hired, eighteenth birthdays)
   while the at-home count falls by leaving still brings up to the smaller of the two home.
2. **The m60 record.** Section 3's twin trace prints the children back and placed in each town. In each wave-end month
   they are equal: the closed town's (month 8, 239 at home to 0) 119.70 back, 119.70 placed; the twin's (month 10, 216
   to 0) 108.57 and 108.57; the small ones too (11.25, 3.46, 2.56). Every child who came back went home with their
   adults, so the line stays re-filed to OX2.
3. **The nit.** Section 4's fixture line, "nobody is out of work the month after", is read off the model the month
   after the wave ends, not twelve months on.

## The m60 line is not OX1's own: re-filed to OX2

"The layoff adds at most one orphan against its twin in every month of the two years" (known-reds, OX1, 2026-11-15)
was the rehire fault on main (8.404). With the mend its months read (closed / open, adults at home in brackets):
1.3/1.4 (239/215), 3.2/1.6 (0/215, the closed town's 119.70 children back, all placed), 5.5/1.5 (0/216), 6.8/3.4 (0/0,
the twin's 108.57 back, all placed) ... 11.3/10.5; the greatest same-month excess 3.996 on 1acfc4f3. The closed town's wave ends two
months before its twin's (the eighth month after the closing against the tenth); behind each end the arrivals rise
(9 to 114 a month on 9a5d8113's model, the same months; the labour short)
and the builder's half a shape leaves part of their children over, as it does any month, so the closed town's
orphans fill two months sooner. That is the builder's residue on arrivals, not a child of a re-employed adult;
the twins' timing and the one-orphan allowance stand. Measured ways to green it, all beyond this batch (before the cap):

| way | m60 excess | what it does |
|---|---|---|
| OX1, e4e238a0; with the cap, 1acfc4f3 | 3.851; 3.996 | the re-employed's children home |
| + arrivals placed as families, after the kept households | 0.387 on 9a5d8113, 2.162 merged | the played town's orphans fall from about 7 to about 2 |
| + newcomers (back and arrivals) before the kept households | 1.099 merged | a layoff month's kept households still take every adult, so that month's births and last orphans wait |

Queued OX2 (the ship file), built on the recommended way of call 1 (arriving children come with their adults), Jerus's to overturn.

## Calls (★ the way taken)

1. ★ **The re-employed's children only.** Placing arriving children with their adults too greens m60 on one
   model and not the next, and empties most of the orphan section of a growing city, which Jerus named ("they're
   the orphans", the half a shape's residue); queued as OX2 on the recommended way, they come with their adults (the orchestrator, 11-17), Jerus's to overturn. Others: arrivals as families in OX1; every
   child of the pool placed by passes while adults are free (the residue would end everywhere).
2. ★ **Passes at half a shape, not the full take.** The full take puts nearly every returning baby in a large
   family (the first shape eats the band); the passes keep the builder's own mix. Other: the shapes of last
   month's formed households in proportion (three bands, one scale: it does not close).
3. ★ **The net fall of the adults at home**, the only flow an aggregate model has; a month that lays off and
   rehires in equal numbers brings nobody back, as it took nobody out.
4. ★ **Memory on only**: a bare model rebuilds everybody, and the population and housing fixtures stay as written.

## The trace

The default playtest traced (`-Dplaytest.trace`, both data files named) on 1acfc4f3, the capped model, against main's
reference `C:/Users/Jerus/AppData/Local/Temp/cbs-trace-main-ec481b4a/`; ours is
`C:/Users/Jerus/AppData/Local/Temp/cbs-trace-OX1-1acfc4f3/`. It ran 2,400 months, "All checks passed.", exit 0 (main's
reference exits 1 on TQ1's recorded finding, households with no home m692-745; a run of ec481b4a with the same probe
reproduced it, and gave main's orphans). All eight files move: `house`, `pop` and `build` from month 14, `month`,
`bank`, `bonds` and `labour` from 15, `borrow` from its first paper (main's month 59). The size, main then OX1, with the
orphans and the orphans who died to date:

| month | people | out of work | jobs | households | children | orphans | orphan deaths | inflation, a year | unskilled premium |
|---|---|---|---|---|---|---|---|---|---|
| 60 | 977; 888 | 0; 0 | 596; 559 | 351; 320 | 291; 264 | 3.9; 2.1 | 0.4; 0.3 | 15.5%; 10.7% | 1.079; 1.060 |
| 120 | 1,471; 1,205 | 0; 1.1% | 767; 623 | 498; 405 | 519; 426 | 19.1; 11.9 | 2.2; 1.2 | 10.6%; 14.0% | 0.963; 0.960 |
| 240 | 1,833; 1,295 | 9.4%; 6.0% | 819; 603 | 522; 375 | 761; 542 | 38.9; 26.8 | 8.9; 5.8 | -4.2%; -4.8% | 0.939; 0.976 |
| 600 | 14,441; 3,496 | 0; 12.8% | 7,244; 1,509 | 4,347; 997 | 6,085; 1,469 | 318.1; 49.3 | 68.0; 23.8 | 15.1%; 2.9% | 1.037; 0.907 |
| 1,200 | 33,302; 36,343 | 9.4%; 7.4% | 13,852; 15,261 | 9,420; 10,602 | 14,027; 15,222 | 710.0; 676.7 | 539.5; 295.4 | 7.4%; 8.2% | 0.850; 0.879 |
| 1,800 | 40,436; 49,857 | 9.1%; 3.5% | 16,856; 22,082 | 11,394; 14,889 | 16,965; 20,925 | 782.5; 810.8 | 1,000.7; 729.1 | -3.8%; -2.3% | 0.868; 0.915 |
| 2,399 | 630,320; 819,808 | 5.9%; 4.3% | 258,580; 331,441 | 179,388; 239,453 | 263,204; 341,457 | 12,666.1; 18,717.2 | 4,010.0; 5,126.1 | -7.8%; -3.9% | 0.971; 0.987 |

Orphans to a hundred children, main then OX1: 1.3 and 0.8 at month 60, 5.2 and 3.4 at 600, 5.1 and 4.4 at 1,200, 4.6
and 3.9 at 1,800, 4.8 and 5.5 at 2,399, where both cities grow tenfold and more in their last 600 months and the
section is the builder's residue on the arrivals. The runs' closing summaries: main 13,074 orphans and 4,101 orphan
deaths over the run, OX1 19,856 and 5,265. The end size is the path's, not the mend's: e4e238a0's model, the same
until month 60, ended at 238,511 people (9,088 at month 600) and 1acfc4f3's at 819,808 (3,496 at 600), against main's
630,320 (14,441). One city's path each, so the ensemble judges.

## The ensemble

`tools/ensemble/ensemble.sh OX1` on 8214515f (the capped model, main ec481b4a merged) against `docs/ensemble/0.9.132.md`,
80 of 80 runs in 45.9 min, exit 0, committed as `docs/ensemble/OX1.md`. **M1 passes** in every policy (0 months the
money audit failed). **F1 passes**, 10 of 10 hard: one city in AUTO2, ZERO_SLACK, SHOCK and HIGH, 0 in ZERO_HOT, each
`households with no home`, against the baseline's one each of the same class; it is one city, seed 9, at month 390,
before every policy's switch, so its twins share it (the baseline's is seed 0 at month 182, the same way). The class
is the default playtest's recorded finding on main (m692-745, FB11's). Changed verdicts, information only: ZERO_HOT H2
VACUOUS to PASS; SHOCK K2 PASS to FAIL (m720-840 inflation 3.01% [-0.49% to 6.75%] against a 3.00% bar, 0.48% over
its twin; the baseline 2.86% [0.30% to 6.48%] and -0.20%, PASS); HIGH R2 PASS to FAIL (unemployment over the twin
0.32 points [-1.40 to 6.78] against a +0.50 bar; the baseline 1.55 [-0.77 to 7.87], PASS); 22 of 26 criteria.

**The default city's m600 is the path's, not the rule's.** AUTO2's people at month 600, OX1 then 0.9.132's run (TQ1's
CSVs): median 19,786 and 24,510, quartiles 14,796-31,891 and 14,660-28,492, range 6,716-47,576 and 8,873-37,360.
Seed by seed, OX1's city is the larger at m600 in 9 of 16 (Mann-Whitney U 137 of 256, 128 being no difference), the
ratio OX1 to main median 1.10 [0.35 to 2.13]; at m1200 larger in 7 of 16, ratio 0.98 [0.20 to 4.38]. T1, the month a
city reaches 40,000: OX1 m872 [493 to 1,320], 0.9.132 m1,071 [671 to 1,306]; the final population (T2) 1,949,996
against 1,836,044. The default playtest's 3,496 against 14,441 at m600 (0.24) sits below the seeds' lowest ratio
(0.35) and is 1.09 by m1200 (36,343 against 33,302): one path's take-off coming later, where the seeds' take-offs
come earlier in the median.

## The pick

`./batch-check.sh --pick` on 77a7ccc9, before the review (both data files named): 15 of 213 harnesses, 13 green, 2 red; review line
"not needed - no money, saves or threading in the diff". The reds:

- **DeathRecordCheck**: the m60 line alone (3.851), carried to OX2 by the ship file's `## known-reds` row; the
  m48 entry filed under OX1 is green and so is vetoed by `tools/known-reds.sh` as the batch's own.
- **EducationCheck**, "a poor city at x3 enrols fewer than at x1: the trap is back": main's. A clean origin/main
  ec481b4a checkout fails it the same (1,652 enrolled at x1, 1,615 at x3, the willing share 0.72 and 0.35), and so
  does OX1 with the mend switched off; TQ1's gate record does not have it (that gate did not run it). R6b files the
  same line as EDU1 with its queued row (the orchestrator, 11-17), so OX1 files nothing for it (a TF1 row was dropped):
  once R6b ships, main is merged here and the line is named.

Judged again on the saved log with a TF1 row standing in for EDU1's (`CBS_REJUDGE=--rejudge ./batch-check.sh --judge`):
"known red only - 2 harnesses", exit 3.

Since: R6b (0.9.133, merged before the dry run) names that EducationCheck line as EDU1, so OX1 files nothing for it.

## The dry run

`sh tools/gate.sh OX1 --dry-run` on bb29c4f6 (main 48eb9b42, 0.9.133, merged in; its four conflicting generated
indexes taken from main and regenerated), run `/tmp/cbs-gate-OX1-9eG8xQIY`, its log kept as `target/OX1-dry.txt`.
**Gate exit 1.** The suite ran in 1,318 s, exit 4, 37 red lines named nowhere, in four harnesses, none of them in
OX1's files and each green on main's tree (R6b's gate runs on 48eb9b42: GoldStandardCheck and RoadCheck with no
FAIL, PriceLevelCheck's twin-town horizon 120 of 120, MapCheck FB5's month-91 line alone). Each is main's code
meeting the path OX1's households moved, the class Jerus passes by name (2026-11-04), so each is named in the ship
file's `## known-reds`, the fixes queued:

- **GoldStandardCheck**, "the month's policy rate is goldRate(currencyGap(), getGoldCover()) as Game struck it": the
  rate struck, 0.110136, is goldRate at a cover of about 0.5993 (0.000136 of spread over goldSpread's 0.202 a unit
  of cover), read inside the month, below both ends the line brackets it by (0.600 and 0.601). HP1.
- **RoadCheck**, the busy street: the advice's choice holds (2 x Gravel Road, plan -66,414.1 against the Paved
  Road's -65,763.4, every same-rank card above it) and every candidate's figure agrees to the bit but the Light Rail
  Line's, one ulp off when `BuildAdviceCheck.lifeFigure` recomputes it (a probe not committed, `target/OX1-road2.txt`).
  HP1.
- **PriceLevelCheck**, 245 failed, printed as 24 `FAIL reload.` lines and the two summaries: section 9's twin-town (ScaleCheck's town,
  saved and loaded) has a nudge horizon of 20 of 120 months on OX1's path, so its twins' equalities are trimmed to
  month 20 (observed, skipped), but `Child.prefix()` keeps the `reload.*` series whole and the reload is sampled after
  month 120, where the twins have parted (2e-9 to 2.5e-5). The twins agree through the horizon. HP1.
- **MapCheck**, "...and every building drawn, one for one", section 8's played city: OX1's packs 784 buildings at the
  city's edge, 135 with no ground (main's: 732, 0), and those are not drawn: FB5's placement class.

HP1 is queued in the ship file (the three harness premises). Judged again on a copy of the run's `suite.txt`
(`target/checks/OX1-dry-suite.txt`, `CBS_REJUDGE=--rejudge ./batch-check.sh --judge`) with the five rows: "known red
only - 9 harnesses" (DeathRecordCheck, EducationCheck, GoldStandardCheck, InvestCheck, MapCheck, PlanCheck,
PriceLevelCheck, RoadCheck, YoungBankCheck), exit 3. The gate's tree took `{next}` as 0.9.134 (the ensemble as
`docs/ensemble/0.9.134.md`). The dry run's two commits: bb29c4f6 (the branch) and 48eb9b42 (main).
