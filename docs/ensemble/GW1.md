# Ensemble baseline: 0.9.86

The ensemble on the model of **0.9.86**: classes snapshot of commit 99429bf8 (2026-11-10T12:10Z), label GW1s, 80 of 80 runs.
Scored against `docs/ensemble/0.9.83.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.06% [1.78% to 2.42%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 2.52% [1.49% to 4.60%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 8 [0 to 24]; 1 [0 to 19] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 15 [0 to 30] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 11]; 100.00% [98.73% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.38% [3.21% to 5.06%]; 26.88% [16.53% to 34.72%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.15% [1.88% to 2.53%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 1.88% [0.58% to 4.59%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) |
| ZERO_SLACK | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 2.86% [0.93% to 6.73%]; 2.06% [1.31% to 3.13%] | - | info |
| ZERO_SLACK | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 1.88% [0.58% to 4.59%]; longest spell 82 months | - | would fail |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.96% [0.96% to 8.05%]; 17.53% [6.15% to 25.83%]; 7.77% [5.31% to 14.26%]; 13.47% [2.15% to 19.91%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 4.04% [0.62% to 14.00%]; 4.40% [0.73% to 14.81%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 73.97% [20.17% to 253.21%]; 32.08% [8.66% to 121.88%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 9.58% [-0.73% to 18.77%]; 2.25% [-2.49% to 15.03%]; 70.41% [3.10% to 185.64%]; 62.51% [0.24% to 607.28%]; 65.19% [9.98% to 570.80%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.58% [0.30% to 11.83%]; -0.14% [-3.12% to 12.57%]; -7.59% [-15.99% to 4.02%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.82% [-3.42% to 3.34%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would fail) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.83% [-0.00% to 6.02%]; 2.60% [-0.34% to 5.00%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -0.01% [-4.21% to 5.56%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 85.59% [14.54% to 932.31%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 15.57% [11.60% to 32.06%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.08% [-2.72% to 2.79%]; -0.43% [-3.73% to 1.17%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 23.17% [11.41% to 83.35%]; 6.83% [2.17% to 34.37%]; -26.60% [-38.90% to 25.30%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.34% [-5.59% to -0.80%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 2.77% [-2.66% to 16.93%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -3.03% [-8.69% to 0.06%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.05% [0.39% to 3.97%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -27.60% [-55.89% to -10.10%]; -22.70% [-35.73% to 10.74%]; -43.04% [-54.89% to -9.24%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m770 [536 to 1,076]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 737,604 [619,884 to 898,444] | ≥ 75.00% of baseline 581,556* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 539,172 [418,899 to 707,338] | ≥ 75.00% of baseline 389,740* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 104.51% [94.00% to 109.95%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 9,468 [6,285 to 14,290] | ≥ 75.00% of baseline 9,262* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 98.80% [44.44% to 136.74%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 56,563 [19,296 to 105,827] | ≥ 75.00% of baseline 29,630* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 103.10% [65.83% to 123.57%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 24,096 [8,340 to 55,508] | ≥ 75.00% of baseline 13,722* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 89.96% [66.26% to 98.89%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 24/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 88.4, ZERO_SLACK 95.9, ZERO_HOT 23.5, SHOCK 44.2, HIGH 34.2; total 79.1 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 737604.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 539172.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 9468.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 56563.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 24096.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
