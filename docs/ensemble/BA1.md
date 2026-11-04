# Ensemble baseline: 0.9.9

The ensemble on the model of **0.9.9**: classes snapshot of commit c0c7afaa (2026-11-04T14:39Z), label BA1, 80 of 80 runs.
Scored against `docs/ensemble/0.9.6.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.54% [2.01% to 3.20%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (14/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 4.09% [3.08% to 5.32%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [0 to 15]; 0 [0 to 0] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 24 [13 to 47] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 13]; 100.00% [99.68% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 5.16% [4.66% to 5.85%]; 27.20% [20.23% to 32.45%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.66% [2.12% to 3.31%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.81% [0.87% to 5.59%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 85 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.13% [0.46% to 9.34%]; 15.56% [8.68% to 26.17%]; 10.72% [6.39% to 14.25%]; 12.88% [6.79% to 17.50%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 5.22% [3.52% to 14.21%]; 5.62% [3.44% to 14.30%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 117.13% [37.09% to 295.01%]; 63.81% [11.48% to 171.95%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 17.40% [13.51% to 23.48%]; 8.35% [1.47% to 12.15%]; 113.70% [61.40% to 215.54%]; 243.58% [72.58% to 438.71%]; 268.41% [72.24% to 538.09%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 8.55% [5.38% to 13.40%]; 4.29% [-0.75% to 7.31%]; -9.25% [-16.71% to -1.86%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.04% [-2.87% to 2.73%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.14% [1.55% to 8.38%]; 4.27% [2.54% to 6.61%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.25% [-5.08% to 2.79%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 347.25% [73.99% to 625.79%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 17.69% [13.44% to 28.17%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 0.79% [-1.21% to 3.22%]; -0.26% [-2.51% to 1.31%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 25.39% [17.63% to 58.28%]; 6.40% [3.50% to 24.00%]; -31.09% [-36.05% to -9.91%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -4.44% [-6.90% to 2.71%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 2.72% [-6.59% to 7.73%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -3.98% [-7.82% to 11.13%] | median ≤ 0, ≤ 2 seeds > +1 pt* | FAIL (4 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.21% [0.64% to 3.65%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -41.17% [-56.37% to -17.00%]; -28.76% [-47.67% to 83.81%]; -48.21% [-66.88% to -10.58%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m980 [843 to 1,437]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 616,952 [373,392 to 707,088] | ≥ 75.00% of baseline 335,674* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 393,902 [224,667 to 550,599] | ≥ 75.00% of baseline 225,911* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 100.34% [96.02% to 110.67%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 7,648 [5,974 to 11,830] | ≥ 75.00% of baseline 8,916* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 109.21% [53.08% to 198.41%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 30,492 [14,436 to 45,822] | ≥ 75.00% of baseline 32,442* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 115.78% [68.80% to 166.50%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 14,699 [9,535 to 19,037] | ≥ 75.00% of baseline 15,496* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 85.01% [61.67% to 104.36%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 22/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 87.3, ZERO_SLACK 62.6, ZERO_HOT 20.6, SHOCK 34.8, HIGH 16.9; total 70.6 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 616951.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 393902.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7648.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 30492.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 14699.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
