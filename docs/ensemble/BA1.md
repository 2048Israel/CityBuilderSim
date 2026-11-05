# Ensemble baseline: 0.9.11

The ensemble on the model of **0.9.11**: classes snapshot of commit ab5f3f41 (2026-11-04T23:56Z), label BA1d, 80 of 80 runs.
Scored against `docs/ensemble/0.9.6.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.61% [1.93% to 4.52%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (15/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 4.42% [3.13% to 6.25%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 11 [2 to 28]; 0 [0 to 8] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 16 [13 to 39] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 5 [0 to 10]; 100.00% [98.78% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 5.19% [4.49% to 7.73%]; 26.32% [16.25% to 32.04%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.71% [2.03% to 4.62%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.68% [1.11% to 6.20%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 107 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.23% [1.04% to 10.59%]; 16.19% [10.53% to 27.03%]; 10.23% [5.84% to 16.87%]; 14.52% [6.07% to 18.80%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 4.75% [3.48% to 9.91%]; 4.97% [3.67% to 10.36%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 105.80% [37.84% to 360.78%]; 55.99% [17.27% to 213.90%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 18.04% [13.54% to 23.48%]; 8.30% [2.60% to 12.15%]; 122.99% [60.07% to 215.54%]; 254.06% [92.41% to 400.44%]; 272.99% [88.52% to 537.82%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 9.21% [5.22% to 13.40%]; 4.32% [-0.70% to 7.77%]; -10.05% [-16.71% to -1.86%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.15% [-1.73% to 2.17%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.25% [2.75% to 8.38%]; 4.54% [2.27% to 6.21%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -2.35% [-7.15% to 2.55%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 376.16% [105.81% to 590.76%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 16.29% [12.53% to 25.55%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 0.92% [-1.55% to 6.58%]; 0.20% [-1.79% to 2.06%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 24.09% [13.72% to 55.72%]; 5.63% [3.58% to 25.70%]; -29.96% [-34.48% to -0.65%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -4.01% [-5.93% to -1.18%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 2.07% [-5.35% to 8.00%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -2.87% [-6.18% to 0.99%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.25% [0.59% to 2.17%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -42.09% [-53.10% to -9.41%]; -20.97% [-44.71% to 7.70%]; -50.07% [-64.96% to -22.98%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,050 [843 to 1,548]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 605,524 [322,648 to 836,076] | ≥ 75.00% of baseline 335,674* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 398,576 [179,424 to 595,115] | ≥ 75.00% of baseline 225,911* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 98.82% [95.73% to 108.14%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 8,126 [5,974 to 14,055] | ≥ 75.00% of baseline 8,916* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 111.97% [55.54% to 284.60%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 27,900 [15,739 to 38,256] | ≥ 75.00% of baseline 32,442* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 106.05% [63.24% to 140.95%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 14,090 [8,576 to 16,778] | ≥ 75.00% of baseline 15,496* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 88.49% [66.09% to 107.00%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 62.4, ZERO_SLACK 66.5, ZERO_HOT 11.9, SHOCK 20.9, HIGH 15.1; total 50.6 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 605524.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 398576.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 8126.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 27899.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 14089.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
