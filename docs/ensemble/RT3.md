# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 40ea73b0 (2026-11-12T16:07Z), label RT3, 80 of 80 runs.
Scored against `docs/ensemble/0.9.93.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.61% [2.18% to 3.38%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (13/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.75% [1.66% to 4.83%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [0 to 23]; 0 [0 to 6] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 22 [13 to 55] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 5 [0 to 12]; 99.98% [96.12% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 5.48% [4.56% to 7.11%]; 26.62% [23.24% to 33.52%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.73% [2.33% to 3.60%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.98% [0.99% to 6.21%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) |
| ZERO_SLACK | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 4.02% [1.01% to 7.71%]; 2.14% [1.02% to 2.56%] | - | info |
| ZERO_SLACK | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.98% [0.99% to 6.21%]; longest spell 85 months | - | would fail |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.12% [0.73% to 10.55%]; 17.72% [10.36% to 34.21%]; 10.89% [7.86% to 14.59%]; 12.64% [5.16% to 27.33%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.86% [6.78% to 12.69%]; 10.02% [6.84% to 11.44%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 117.12% [22.54% to 311.94%]; 64.36% [8.85% to 201.26%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 14.04% [8.83% to 22.57%]; 6.87% [2.87% to 12.54%]; 112.39% [40.82% to 273.61%]; 243.20% [96.80% to 578.19%]; 230.36% [84.98% to 455.63%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 8.24% [3.66% to 15.14%]; 4.99% [1.58% to 10.39%]; -7.16% [-12.64% to 2.11%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.09% [-2.24% to 3.17%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.00% [1.28% to 8.21%]; 3.51% [2.00% to 5.61%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.80% [-6.14% to -0.03%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 298.87% [123.60% to 655.72%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 17.94% [12.83% to 30.53%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 0.71% [-1.69% to 2.67%]; -0.44% [-1.62% to 2.21%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 24.64% [13.93% to 56.46%]; 12.53% [4.18% to 33.38%]; -20.77% [-47.74% to -3.12%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.53% [-7.28% to -0.31%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 1.20% [-4.43% to 11.05%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -2.50% [-6.39% to 1.32%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) |
| HIGH | info | deposit rate in the window | 2.65% [0.93% to 6.62%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -38.56% [-53.36% to 2.98%]; -17.89% [-43.54% to 16.73%]; -47.60% [-70.86% to -21.91%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,204 [812 to 1,914]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 199,646 [140,803 to 635,077] | ≥ 75.00% of baseline 224,453* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 1/16 ['negative monthly GDP'] | ≤ baseline 0 | FAIL |
| ZERO_SLACK | T2 | final population | 139,848 [42,738 to 402,077] | ≥ 75.00% of baseline 147,125* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 98.60% [83.33% to 118.17%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 1/16 ['negative monthly GDP'] | ≤ baseline 0 | FAIL |
| ZERO_HOT | T2 | final population | 6,958 [2,938 to 9,906] | ≥ 75.00% of baseline 7,704* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 71.63% [33.65% to 154.56%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 18,786 [12,013 to 35,287] | ≥ 75.00% of baseline 19,820* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 113.43% [54.69% to 221.12%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 12,540 [9,027 to 23,516] | ≥ 75.00% of baseline 14,282* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 87.03% [62.53% to 115.71%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 22/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 132.9, ZERO_SLACK 119.8, ZERO_HOT 16.4, SHOCK 41.8, HIGH 41.5; total 94.7 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 199646.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 139848.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6958.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 18785.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 12539.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
