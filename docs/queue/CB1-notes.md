# CB1 - the central bank's strictness measured over twelve cities, not one probe city ({next}, 2026-11-02)

> WIP, 2026-11-02 evening (paused on the orchestrator's word; delete this block before the ship file). Done on local
> branch claude/cb1-311310 (built on main 37d65b2e, not pushed): the slice (d3b20601), green on main, and these notes.
> Next: when step 0's wait says BA1 ready, `git checkout -b batch/CB1 origin/batch/BA1`, cherry-pick d3b20601 and
> the notes, `./batch-check.sh --pick`, update probeCity()'s "0.05 to 0.74" and 2/5 with the run's figures, push the
> branch; `sh tools/wait.sh --row BA1`, merge origin/main, then the ship file last (`{next}`, pending row CB1-L).

Jerus, 2026-11-01: "queue, but after 0.9.0", from the day's question whether a check that tests one city's history
should exist. The brief is `docs/queue/CB1.md`; EP1's notes, 11, the evidence. Lane 1's last batch on the night of
2026-11-02, built on BA1. One slice (Sonnet 5.5, xhigh), reviewed here; the header's prose written here.

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

On main before BA1 (a scratch driver of the same code, 2026-11-02), all twelve hold: very strict under Standard by
0.047 (41 houses, 4 months) to 0.736 points a year (40, 5), the median -0.351; seeds 12 to 15's shapes hold too (-0.19 to
-0.64). `STRICT_SHARE` is three in four, from EP1's 11 of 12 and FR7's 15 of 16: a path reverses about one city in
twelve, three may before the line is red, and a rule that made no difference (each city a coin's toss) would pass one
time in fourteen (299 of 4,096). ★1.

## 3. The run time

In turn, in the harness's own JVM, not child JVMs: children would each start cold (a city costs about 29 s in a fresh
JVM against about 13 s warm) and would grow memory under the suite's three harnesses past AllChecks' commit-charge gate
(SP1: a suite died at 43 of 54 GB), which only gates a harness's start. CentralBankCheck alone (`./batch-check.sh
CentralBankCheck`, timing soft, other sessions' JVMs on the PC): 163 s, against 83 s on main the same evening. In a
suite three wide that is about 27 s more of the suite's wall if it is packed, more if it starts late; it is now the
second-longest harness after MapCheck (192 s), and `AllChecks.LONGEST_FIRST` (lane 2's file tonight) does not name it.
★2.

## 4. The calls

- ★1 `STRICT_SHARE` at three in four (9 of 12). **Recommended.** Five in six (10 of 12) leaves two reversals before
  red, nearer EP1's worst; two in three (8 of 12) lets a no-difference rule pass one time in five.
- ★2 `CentralBankCheck` into `AllChecks.LONGEST_FIRST`, after MapCheck, so the suite starts it first. **Recommended**,
  as the pending row CB1-L (GT1 measures the suite and owns AllChecks tonight).

## 5. The checks

The ensemble is skipped: only a harness changed, so the default playtest's trace is main's.
