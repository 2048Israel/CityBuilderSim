# Ensemble baseline: 0.8.93

The ensemble on the model of **0.8.93**: classes snapshot of commit fbf4d323 (2026-11-01T11:21Z), label EP1-mended, 80 of 80 runs.
Scored against `docs/ensemble/0.8.91.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.63% [2.08% to 3.18%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (15/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 4.26% [3.46% to 6.85%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 6 [0 to 20]; 0 [0 to 1] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 18 [10 to 46] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 2 [0 to 12]; 100.00% [99.85% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 5.16% [4.20% to 6.02%]; 25.93% [23.52% to 32.96%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.74% [2.18% to 3.31%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.15% [0.55% to 5.56%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 96 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.64% [0.10% to 8.72%]; 15.78% [5.74% to 29.53%]; 10.38% [5.26% to 14.48%]; 10.18% [3.28% to 24.68%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 11.08% [3.93% to 14.02%]; 11.29% [4.70% to 15.21%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 89.51% [14.99% to 263.15%]; 40.81% [9.35% to 156.86%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 14.62% [9.72% to 18.09%]; 6.01% [1.60% to 11.60%]; 93.63% [55.88% to 152.10%]; 176.12% [52.80% to 346.55%]; 211.67% [35.26% to 434.95%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.28% [4.85% to 10.43%]; 3.00% [-0.23% to 8.89%]; -8.82% [-10.29% to 1.59%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.30% [-2.73% to 2.96%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (13/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.45% [1.34% to 9.32%]; 3.99% [3.45% to 9.30%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -4.75% [-9.38% to -1.34%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 226.00% [86.09% to 430.05%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 16.19% [14.96% to 25.69%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 2.17% [-0.13% to 5.08%]; -0.23% [-2.05% to 2.43%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 24.29% [11.22% to 49.00%]; 5.40% [3.50% to 25.08%]; -31.53% [-35.53% to -13.40%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.27% [-7.52% to -0.75%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 0.89% [-7.33% to 9.41%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -3.33% [-8.48% to 2.20%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.09% [0.41% to 2.85%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -33.24% [-48.22% to -19.14%]; -12.56% [-54.30% to 42.29%]; -41.74% [-56.74% to -25.02%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,276 [872 to 2,012]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 267,046 [103,848 to 384,433] | ≥ 75.00% of baseline 335,916* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 162,106 [50,359 to 285,928] | ≥ 75.00% of baseline 236,141* | FAIL |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 100.20% [96.65% to 105.71%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 8,652 [5,974 to 14,376] | ≥ 75.00% of baseline 8,764* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 205.85% [116.43% to 386.01%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 16,811 [6,165 to 30,340] | ≥ 75.00% of baseline 26,598* | FAIL |
| SHOCK | info | final population over the AUTO2 twin's, same month | 101.68% [59.27% to 147.73%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 9,606 [3,017 to 16,583] | ≥ 75.00% of baseline 12,758* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 95.75% [59.92% to 112.57%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 21/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 45.6, ZERO_SLACK 41.5, ZERO_HOT 9.4, SHOCK 16.5, HIGH 13.5; total 34.0 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 267046.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 162106.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 8651.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 16811.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 9605.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
