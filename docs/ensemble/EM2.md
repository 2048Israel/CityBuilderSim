# Ensemble baseline: 0.8.77

The ensemble on the model of **0.8.77**: classes snapshot of commit 1002e1e4+dirty (2026-10-30T23:16Z), label EM2w, 80 of 80 runs.
Scored against `docs/ensemble/0.8.77.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 1.88% [1.74% to 2.26%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.09% [0.85% to 3.85%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 1 [0 to 9]; 0 [0 to 0] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 12 [3 to 23] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 10]; 100.00% [99.79% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 3.97% [3.26% to 4.47%]; 25.69% [19.17% to 30.51%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 1.97% [1.78% to 2.35%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 0.82% [0.25% to 3.87%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 35 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 2.86% [1.67% to 7.50%]; 10.66% [4.60% to 17.93%]; 7.63% [4.18% to 12.71%]; 7.39% [1.25% to 17.17%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 13.78% [7.66% to 17.15%]; 14.02% [9.43% to 17.51%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 22.99% [9.79% to 147.87%]; 14.95% [2.54% to 102.11%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 10.20% [7.21% to 14.66%]; 5.24% [1.64% to 13.23%]; 43.11% [19.07% to 86.76%]; 78.61% [30.80% to 250.25%]; 70.22% [33.99% to 263.78%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 3.89% [1.86% to 6.94%]; 2.98% [-0.40% to 9.05%]; -4.98% [-8.89% to 2.60%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.31% [-2.44% to 4.65%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.41% [0.57% to 9.04%]; 4.01% [2.40% to 6.85%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -2.18% [-5.71% to 1.97%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 116.73% [47.78% to 358.23%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 15.09% [12.32% to 25.49%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 2.81% [0.53% to 4.41%]; -0.12% [-2.08% to 0.86%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 26.44% [18.74% to 49.32%]; 3.46% [2.37% to 14.24%]; -25.08% [-38.01% to -5.61%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -2.13% [-4.40% to 1.71%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 2.62% [-11.03% to 6.18%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -1.50% [-5.43% to 3.34%] | median ≤ 0, ≤ 2 seeds > +1 pt* | FAIL (6 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.11% [0.62% to 1.72%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -25.41% [-41.63% to 7.49%]; -5.83% [-35.66% to 82.61%]; -33.35% [-51.81% to -1.27%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,052 [935 to 1,147]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 320,481 [276,549 to 369,123] | ≥ 75.00% of baseline 317,518* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 240,905 [192,201 to 285,467] | ≥ 75.00% of baseline 239,652* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 103.09% [97.50% to 109.87%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 5,664 [3,300 to 11,496] | ≥ 75.00% of baseline 5,368* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 109.26% [67.32% to 155.73%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 25,860 [15,173 to 33,367] | ≥ 75.00% of baseline 21,852* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 102.69% [73.83% to 135.57%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 11,442 [9,108 to 18,326] | ≥ 75.00% of baseline 10,660* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 91.05% [73.32% to 130.87%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 41.9, ZERO_SLACK 38.6, ZERO_HOT 8.0, SHOCK 17.7, HIGH 16.7; total 33.2 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 320481.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 240905.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 5664.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 25860.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 11442.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
