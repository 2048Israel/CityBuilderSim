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
  less this month's (the net), their children last month's children at home at that household's rate to an adult,
  band by band; `placeTogether()` forms their households by the builder's own order and half a shape, into the
  shapes that hold a child, pass after pass (`HOME_PASSES`, 64) until every child is placed while their adults last;
  their leftover adults join the pool. Memory on only. Counters `getLastBackAdults()`, `getLastBackChildren()`,
  `getLastBackChildrenHome()`, reset with the memory's four. The bare `.5` is `SHAPE_TAKE`, unchanged, with its sentence.
- **Nothing saved, no format bump.** Last month's adults at home and their children are already in the families'
  save (the kin slots, `toSaveArray()` and `restore()`), and the load path restores the households without a
  rebuild, so a loaded city's next month brings the same children home. The counters are this month's and read in it.
- **`DeathRecordCheck`** section 4, `waveEndsAtOnce()` (above; its fixture lines read the model's own rule, the
  children out at `outOfWork / adults` of each band, and the mend's counters), and the m60 pair now prints its months.

## What each check showed

- DeathRecordCheck on the merged model: section 4 green (5.26 against 6.58 the month it ends, 0.000 at most in the
  year after; 1,200 back, 470 children back, all 470 home); section 3's "does not fill behind it" green (peak 7.92
  against the twin's 7.73). The m60 line red at 3.851, below: re-filed.
- In the default playtest on the final model (77a7ccc9, a probe line not committed): the adults at home fell in 998 of
  2,400 months and the mend brought 41,999.5 children home with them; one month left any over (m1634, 0.018 of a
  child, 0.335 adults back with 0.185 children: the two-adult shapes ran their adults out). The adults at home fell by
  a quarter and 50 or more at once in 25 months.

## The m60 line is not OX1's own: re-filed to OX2

"The layoff adds at most one orphan against its twin in every month of the two years" (known-reds, OX1, 2026-11-15)
was the rehire fault on main (8.404). With the mend its months read (closed / open, adults at home in brackets):
1.0/1.3 (239/215), 3.1/1.5 (0/215), 5.4/1.6 (0/215), 6.7/3.4 (0/0) ... 11.3/10.5. The closed town's wave ends two
months before its twin's (the eighth month after the closing against the tenth); behind each end the arrivals rise
(9 to 114 a month on 9a5d8113's model, the same months; the labour short)
and the builder's half a shape leaves part of their children over, as it does any month, so the closed town's
orphans fill two months sooner. That is the builder's residue on arrivals, not a child of a re-employed adult;
the twins' timing and the one-orphan allowance stand. Measured ways to green it, all beyond this batch:

| way | m60 excess | what it does |
|---|---|---|
| OX1 as shipped | 3.851 | the re-employed's children home |
| + arrivals placed as families, after the kept households | 0.387 on 9a5d8113, 2.162 merged | the played town's orphans fall from about 7 to about 2 |
| + newcomers (back and arrivals) before the kept households | 1.099 merged | a layoff month's kept households still take every adult, so that month's births and last orphans wait |

Queued OX2 (the ship file) with the call for Jerus.

## Calls (★ the way taken)

1. ★ **The re-employed's children only.** Placing arriving children with their adults too greens m60 on one
   model and not the next, and empties most of the orphan section of a growing city, which Jerus named ("they're
   the orphans", the half a shape's residue); his call, queued as OX2. Others: arrivals as families in OX1; every
   child of the pool placed by passes while adults are free (the residue would end everywhere).
2. ★ **Passes at half a shape, not the full take.** The full take puts nearly every returning baby in a large
   family (the first shape eats the band); the passes keep the builder's own mix. Other: the shapes of last
   month's formed households in proportion (three bands, one scale: it does not close).
3. ★ **The net fall of the adults at home**, the only flow an aggregate model has; a month that lays off and
   rehires in equal numbers brings nobody back, as it took nobody out.
4. ★ **Memory on only**: a bare model rebuilds everybody, and the population and housing fixtures stay as written.

## The trace

The default playtest traced (`-Dplaytest.trace`, both data files named) on 77a7ccc9, the merged final model, against
main's reference `C:/Users/Jerus/AppData/Local/Temp/cbs-trace-main-ec481b4a/`; ours is
`C:/Users/Jerus/AppData/Local/Temp/cbs-trace-OX1-77a7ccc9/`. It ran 2,400 months, "All checks passed.", exit 0 (main's
reference exits 1 on TQ1's recorded finding, households with no home m692-745; this path does not reach it). All eight
files move: `house`, `pop` and `build` from month 14, `month`, `bank`, `bonds` and `labour` from 15, `borrow` from its
first paper (main's month 59). One city's path, so the ensemble judges. The size, main then OX1:

| month | people | out of work | jobs | households | children | inflation, a year | unskilled premium |
|---|---|---|---|---|---|---|---|
| 60 | 977; 888 | 0; 0 | 596; 559 | 351; 320 | 291; 264 | 15.5%; 10.7% | 1.079; 1.060 |
| 120 | 1,471; 1,204 | 0; 1.0% | 767; 623 | 498; 406 | 519; 426 | 10.6%; 14.1% | 0.963; 0.960 |
| 240 | 1,833; 1,294 | 9.4%; 5.9% | 819; 603 | 522; 376 | 761; 541 | -4.2%; -4.8% | 0.939; 0.977 |
| 600 | 14,441; 9,088 | 0; 0 | 7,244; 4,484 | 4,347; 2,682 | 6,085; 3,856 | 15.1%; 10.5% | 1.037; 1.024 |
| 1,200 | 33,302; 35,335 | 9.4%; 7.0% | 13,852; 15,091 | 9,420; 10,006 | 14,027; 14,941 | 7.4%; 11.6% | 0.850; 0.865 |
| 1,800 | 40,436; 32,829 | 9.1%; 10.8% | 16,856; 13,086 | 11,394; 9,168 | 16,965; 13,732 | -3.8%; -1.8% | 0.868; 0.850 |
| 2,399 | 630,320; 238,511 | 5.9%; 0 | 258,580; 99,183 | 179,388; 70,583 | 263,204; 99,259 | -7.8%; -4.8% | 0.971; 1.037 |

People to a household: 2.78 and 2.78 at month 60, 3.51 and 3.38 at month 2,399, in cities of very different size.

## The pick

`./batch-check.sh --pick` on 77a7ccc9 (both data files named): 15 of 213 harnesses, 13 green, 2 red; review line
"not needed - no money, saves or threading in the diff". The reds:

- **DeathRecordCheck**: the m60 line alone (3.851), carried to OX2 by the ship file's `## known-reds` row; the
  m48 entry filed under OX1 is green and so is vetoed by `tools/known-reds.sh` as the batch's own.
- **EducationCheck**, "a poor city at x3 enrols fewer than at x1: the trap is back": main's. A clean origin/main
  ec481b4a checkout fails it the same (1,652 enrolled at x1, 1,615 at x3, the willing share 0.72 and 0.35), and so
  does OX1 with the mend switched off; TQ1's gate record does not have it (that gate did not run it). Filed by name
  under a new queued TF1 (the ship file), its cause in the row.

Judged again on the saved log with both rows (`CBS_REJUDGE=--rejudge ./batch-check.sh --judge`): "known red only -
2 harnesses", exit 3.
