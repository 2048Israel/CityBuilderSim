# Ensemble baseline: 0.8.90

The ensemble on the model of **0.8.90**: classes snapshot of commit 9e590801 (2026-10-31T23:47Z), label ld1, 80 of 80 runs.
Scored against `docs/ensemble/0.8.88.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.39% [2.01% to 2.96%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 4.65% [3.13% to 5.85%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 12 [5 to 33]; 0 [0 to 0] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 16 [12 to 32] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 11]; 100.00% [99.88% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.93% [4.29% to 5.78%]; 27.04% [24.35% to 29.86%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.49% [2.11% to 3.10%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.59% [0.49% to 5.06%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 58 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.00% [2.26% to 8.96%]; 16.33% [10.98% to 22.20%]; 10.72% [8.46% to 20.18%]; 9.57% [6.34% to 15.81%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.90% [5.65% to 15.04%]; 10.89% [5.46% to 15.17%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 140.87% [29.61% to 287.97%]; 51.99% [9.43% to 139.71%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 13.35% [6.68% to 18.38%]; 9.58% [7.46% to 13.46%]; 83.57% [6.48% to 131.76%]; 182.11% [52.47% to 402.56%]; 176.52% [49.82% to 317.01%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.70% [0.67% to 9.54%]; 4.43% [2.59% to 8.44%]; -4.43% [-8.47% to 3.03%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.01% [-2.58% to 1.71%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.13% [0.99% to 8.52%]; 4.29% [2.43% to 7.63%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.46% [-4.32% to 2.62%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 278.81% [117.98% to 591.18%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 18.10% [14.85% to 22.12%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.68% [-4.30% to 3.43%]; -0.69% [-4.06% to 0.92%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 27.52% [16.28% to 46.92%]; 5.34% [2.86% to 28.89%]; -26.03% [-36.31% to -9.68%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.48% [-6.27% to -0.39%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 0.38% [-2.74% to 6.05%] | median ≥ +0.50%* | FAIL |
| HIGH | R3 | inflation m660-720 minus twin | -3.92% [-6.54% to 4.15%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (2 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.26% [0.65% to 2.61%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -32.95% [-48.82% to 3.73%]; -23.74% [-43.31% to 21.08%]; -39.82% [-57.26% to -20.60%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,033 [857 to 1,141]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 335,916 [290,726 to 398,232] | ≥ 75.00% of baseline 347,140* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 236,141 [183,896 to 290,054] | ≥ 75.00% of baseline 249,707* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 100.07% [95.87% to 107.53%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 8,764 [4,150 to 11,315] | ≥ 75.00% of baseline 5,674* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 119.22% [75.28% to 274.70%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 26,598 [15,890 to 39,060] | ≥ 75.00% of baseline 24,498* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 98.27% [65.58% to 138.40%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 12,758 [9,680 to 20,254] | ≥ 75.00% of baseline 12,180* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 96.86% [66.21% to 118.97%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 102.8, ZERO_SLACK 87.9, ZERO_HOT 16.6, SHOCK 37.2, HIGH 35.1; total 74.7 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 335916.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 236141.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 8764.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 26598.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 12758.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
