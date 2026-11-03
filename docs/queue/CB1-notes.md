# CB1 - the central bank's strictness measured over twelve cities, not one probe city (0.9.9, 2026-11-03)

Jerus, 2026-11-01: "queue, but after 0.9.0", from the day's question whether a check that tests one city's history
should exist. The brief is `docs/queue/CB1.md`; EP1's notes, 11, the evidence. Lane 1's last batch of the night of
2026-11-02; written on main that evening while BA1 was building, then moved onto main at cd3a506e (0.9.8) on the
orchestrator's word of 2026-11-03 (BA1 cannot ship today), so it carries no other batch. One slice (Sonnet 5.5, xhigh),
reviewed here; the header's prose written here.

## 1. What changed, in `CentralBankCheck` alone

Section 21's "very strict holds prices lower than Standard over the horizon" was one comparison on the probe city,
which had been re-picked six times (seed 5's, 2's, 11's, 8's, 5's, 7's shape) as batches moved its path. It is now
read over `STRICT_SHAPES`, twelve founding shapes: 40 to 43 houses before the next twenty, 3 to 5 months before them,
the shapes the playtest's seeds 0 to 11 found (LongPlaytest, ENSEMBLE MODE). The probe city's own shape (43, 4; seed
7's) is one of them and its very strict and Standard twins are reused; the other eleven are founded by
`shapedCity(files, houses, months)` (probeCity's body with the shape as arguments; `probeCity()` calls it with
`PROBE_HOUSES`, `PROBE_MONTHS`), played to `STRICT_BRANCH` the same way, saved, and twinned on `STRICT_HORIZON`
months (`strictAgainstStandard()`).

The line is now, in order: every city of the set at its branch on the rule at Standard (a fixture); very strict under
Standard at the median of the twelve; and in at least `STRICT_SHARE` of them (9 of 12); `STRICT_SHARE` over a half, so
the share carries the median. Then the line's own fixture, from both sides: the twelve measured gaps made very
strict's cheaper, then cities reversed by hand one at a time (a city's two inflations swapped, very strict dearer by
what it was cheaper): one reversed leaves it green (11 of 12, 9 needed); it is green at every count that leaves the
share (up to 3) and red from 4 on. The verdict is `strictHolds()`, pure, beside `strictNeed()` and `median()`.

Kept as they were (the brief): the very loose twin's two claims, the fourth game (the world's prices falling), the dial's
stop and the last year (on the probe city's very strict twin), trust, the decision and the save. The probe city's
re-pick history stays in `probeCity()`'s javadoc as the reason, with a paragraph for CB1; the class header's 21 says
what the line reads now. No other assertion moved.

## 2. The set and the share

The set is the ensemble seeds' founding shapes, the brief's second choice: the grid the playtest's seeds walk (houses
by months), and the shapes every re-pick since 0.7.67 probed. Seeds 12 to 15 differ only in the second twenty (21) and
are left out, for the run time (3).

Very strict less Standard, points a year over 240 months from month 600:

| shape (houses, months) | main 37d65b2e (2026-11-02) | CB1 on cd3a506e (0.9.8) |
|---|---|---|
| 40, 3 | -0.162 | -0.434 |
| 41, 3 | -0.243 | -0.423 |
| 42, 3 | -0.385 | **+0.434** |
| 43, 3 | -0.204 | -0.524 |
| 40, 4 | -0.316 | -0.320 |
| 41, 4 | -0.047 | **+0.527** |
| 42, 4 | -0.435 | -0.178 |
| 43, 4 (the probe city) | -0.410 | -0.433 |
| 40, 5 | -0.736 | -0.423 |
| 41, 5 | -0.401 | -0.355 |
| 42, 5 | -0.427 | -0.931 |
| 43, 5 | -0.230 | -0.598 |
| hold / median | 12 of 12 / -0.351 | 10 of 12 / -0.423 |

Seeds 12 to 15's shapes held on 37d65b2e too (-0.19 to -0.64). Four batches (LD2, BD1, FC1b, DM1) moved two of the
twelve paths across the line overnight while the probe city held: the draw this batch is for.

`STRICT_SHARE` is three in four: EP1 saw 11 of 12, FR7 15 of 16, 0.9.8 10 of 12, so a path reverses one or two cities
in twelve; three may before the line is red, while a rule that made no difference (each city a coin's toss) would pass
one time in fourteen (299 of 4,096). ★1.

## 3. The run time

In turn, in the harness's own JVM, not child JVMs: children would each start cold (a city costs about 29 s in a fresh
JVM against about 13 s warm) and would grow memory under the suite's three harnesses past AllChecks' commit-charge gate
(SP1: a suite died at 43 of 54 GB), which only gates a harness's start. CentralBankCheck alone: 83 s on 37d65b2e
(2026-11-02, this PC busy); 153 s with CB1 on cd3a506e (`--pick`, one JVM at a time, the gate's data files named,
WN5b running beside it), so about 70 to 80 s more. In a suite three wide that is about 25 s more of the suite's wall if
packed, more if it starts late: it is now the second-longest harness after MapCheck (192 s), and
`AllChecks.LONGEST_FIRST` (lane 2's file) does not name it. ★2.

## 4. The calls

- ★1 `STRICT_SHARE` at three in four (9 of 12). **Recommended.** On 0.9.8 10 hold, so one more reversal is room
  and a second is red; two in three (8 of 12) leaves more room but lets a no-difference rule pass one time in five
  (794 of 4,096), and five in six (10 of 12) would sit on today's count.
- ★2 `CentralBankCheck` into `AllChecks.LONGEST_FIRST`, after MapCheck, so the suite starts it first. **Recommended**,
  as the pending row CB1-L (lane 2 owns AllChecks).

## 5. The checks

`./batch-check.sh --pick` on cd3a506e + CB1, `-Dcbs.earth` and `-Dcbs.world` named, one JVM at a time: exit 0
(CentralBankCheck and TimingCheck picked, both OK; maps regenerated; Stale 0 firm; the tree only the generated docs).
The ensemble is skipped: only a harness changed, so the default playtest's trace is main's.
