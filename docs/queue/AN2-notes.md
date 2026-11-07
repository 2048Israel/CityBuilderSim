# AN2 - AN1's leftovers: the struck D$ on Trade › The currency, and the Earth playtest (0.9.37, 2026-11-06)

The brief is [AN2](AN2.md): Jerus, 2026-10-23, on AN1-C's "left undone", "Queue both, small".

## WHERE IT STANDS

**Ready.**
- **The branch.** `batch/AN2` was built on XR5's ship file (RL1, 25b1e5c4); main with XR5
  in (92f33f13, CX1 with it) was merged at bc44336a, main's side taken in the generated
  `docs/map/README.md`. `git merge-tree` with origin/main 92f33f13: clean.
- **The commit.** 3064ead9: the getter, the line and the harness, drafted by Sonnet 5.5 at
  xhigh and reviewed by Opus.
- **The checks.** `./batch-check.sh --pick KeptCheck` on the merge: exit 3, main's recorded
  KeptCheck red only (PassThroughCheck, LongPlaytest for Game.java, TimingCheck; KeptCheck
  named, since `pick` counts no harness reading TradeScreen while KeptCheck 72 draws Trade ›
  The currency: kept, 274 nodes handed back a month, none made). Before the merge the same
  run said exit 4 with XR5's two KeptCheck lines (KF1), not this batch's code; they are in
  main's record since XR5's gate.
- **The trace.** The default playtest ran traced (`-Dplaytest.trace`, both data files named)
  on XR5's tip (25b1e5c4, built apart) and on 3064ead9: all eight CSVs are byte-identical,
  and the logs differ only in their run times. So there is no ensemble (EN1).

## 1. The line on Trade › The currency

- **Where.** THE RATE card, under the D$, one of ours in theirs and the month's move:
  "The goods are struck at D$0.7905", its (i) `STRUCK_INFO`, and under it "4.3% weaker
  than the rate, closing 1/36 of the gap a month" (PassThroughCheck 7's city) ("Level with the rate: the goods have
  caught up." within .05%). Both are figures of the model's constants (`PASS_THROUGH`; the
  (i)'s "about 25 months" is its half-life). The page is kept (KP6): the line is the scope
  `"struck"`, drawn once something is struck, so a city before its first month shows none.
- **The getter.** `Game.struckFromRate()`: `EconomyManager.getStruckRate()` over the D$,
  less one; NaN before the first strike. The screen divides nothing. Nothing in the model
  reads it.
- **No chart, no save.** The brief asks for a `HistorySave` series only if the screen charts
  the struck rate; it does not, so nothing is saved and `SAVE_FORMAT` stands.
- **PassThroughCheck 7** (the screen's reading): NaN while nothing is struck (a new city; the
  city's struck rate cleared, then put back); over section 2's 239 month-pairs, the gap the
  line reads at a month's end is the one the next month's strike closes `PASS_THROUGH` of,
  in logs (widest miss 1.95e-16). So the line's words hold every month: nothing moves the D$
  between a month's end and the next strike. How often the line speaks: 199 of 240
  month-ends more than 5% either way, median 14.74%, widest 53.38%; 110 say "stronger",
  130 "weaker", none "level".
- **Not looked at by eye:** the line and its (i) on THE RATE, past a city's first month and
  at founding (no line).

## 2. The Earth playtest: Rotterdam, with AN1's anchor

`LongPlaytest --earth rotterdam`, 4,000 months (Rotterdam, 51.92° N 4.48° E, earth-2.bin
c897b242), in 163 s; the default playtest, 4,000 months, on the same build in 195 s. Both ran
on XR5's tip (25b1e5c4: main 0.9.34 and XR5) before AN2's commit; AN2 moves nothing in the
model (the trace above), so the figures stand for this branch. A harness city in a
`TempDirs` folder, deleted at its exit; Jerus's Rotterdam (slot 3) was not opened.

| at month 4,000 | Rotterdam (Earth) | the default |
|---|---:|---:|
| the city's price index against founding | 327,729 | 2,696,394 |
| ...a year, on average | +3.88% | +4.54% |
| the world's prices against founding (realised a year) | 431 (+4.5%) | 437 (+4.2%) |
| inflation, the last year | -0.5% | +3.8% |
| people | 973,877 | 914,836 |
| the D$ per US$ (parity; under it) | 601 (771; 22%) | 4,804 (6,144; 22%) |
| its dearest dollar | 801.7 (m3974) | 6,390 (m3693) |
| months at its guard (1e9 per US$) | 0 | 0 |
| hunger | 12% short | 35% short |
| findings | a building on the map moved 3 times, m3008-3013 | none |

AN1's default, for the brief (0.8.21): 13,374 times founding, 625,815 people, never at its
guard. The default has moved since (2.7 million times founding, +4.5% a year), still never
near its guard; the nightly follows it, and it is not this batch's.

**The anchor holds on the Earth.** Rotterdam's prices rise with the world's (+3.88% a year
against its +3.9% headline), and its D$ ends 22% under parity, never past twice parity but
one month, nowhere near its guard. No hyperinflation.

**The finding: Rotterdam stalls for a century.** From m618 to m1778, about 97 years, the
city holds at 10-13 thousand people, while the default, in the same months, goes from 15
thousand to 104 thousand. Then it grows, and by m4000 it is the larger city. The
checkpoints (the playtest's own lines):

| Rotterdam | people | prices | hunger | land | fields | the default | people |
|---|---:|---:|---:|---:|---:|---|---:|
| m618 | 12,407 | 25.8 | 19% | 99% | 36% | m613 | 15,121 |
| m769 | 10,528 | 43.0 | 19% | 100% | 36% | m761 | 18,012 |
| m1074 | 10,205 | 65.7 | 22% | 85% | 38% | m1067 | 11,349 |
| m1321 | 11,658 | 127.1 | 19% | 78% | 26% | m1314 | 37,165 |
| m1537 | 12,972 | 232.2 | 13% | 54% | 34% | m1530 | 76,216 |
| m1778 | 12,107 | 567.5 | 60% | 54% | 19% | m1771 | 103,643 |
| m2025 | 35,791 | 1,471.6 | 17% | 91% | 100% | m2018 | 169,130 |
| m2252 | 107,224 | 3,330.0 | 9% | 92% | 100% | m2370 | 333,810 |

Jobs held at 4.5-6.8 thousand through it, and went to 20 thousand by m2025 and 61 thousand
by m2252. Not diagnosed
here: the brief says a stall is the finding, to say and stop, not tune (★1).

**The map's finding** (a building moved, m3008-3013, two Truck Depots in district (-2,-1)) is
the class the nightly found on the default at 0.9.24 (one move, m3012): past the suite's
2,400 months, on a build without AN2's code; not this batch's (Jerus, 2026-11-04).

## ★ calls

- **★1 Rotterdam's century at 10-13 thousand people (m618-m1778).** Recommended: queue
  **ER1**, a diagnosis only: what holds an Earth city at Rotterdam's site at about 12
  thousand people for 97 years when the default grows sevenfold in the same months (the
  land in use at 99-100% by m618 and only a third of the fields; then land falling to 54%
  and hunger at 60% by m1778; what changes at m1778-m2025, when the fields reach 100% and
  jobs fourfold), with a fixture that holds the city there; the fix is its own batch. The
  other way: leave it, since the city ends larger than the default's.
</content>
</invoke>
