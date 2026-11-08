# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 49366c39 (2026-11-08T20:19Z), label CD9m, 80 of 80 runs.
Scored against `docs/ensemble/0.9.54.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.23% [1.86% to 2.85%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.48% [1.69% to 4.28%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 9 [0 to 28]; 0 [0 to 0] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 11 [6 to 13] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 1 [0 to 11]; 100.00% [97.90% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.27% [3.66% to 5.18%]; 19.65% [15.51% to 26.71%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.29% [1.92% to 2.94%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.05% [0.05% to 5.57%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 57 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.22% [1.46% to 8.86%]; 13.48% [9.70% to 17.35%]; 9.92% [3.30% to 13.54%]; 12.47% [7.47% to 18.01%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 6.58% [3.18% to 12.00%]; 7.27% [4.29% to 12.90%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 87.88% [10.64% to 274.86%]; 38.30% [0.36% to 164.69%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 9.92% [5.04% to 13.34%]; 9.20% [3.23% to 12.23%]; 49.34% [13.16% to 104.21%]; 142.15% [49.50% to 255.32%]; 162.19% [51.43% to 312.80%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 4.34% [1.29% to 7.76%]; 5.57% [0.38% to 9.06%]; -1.29% [-7.42% to 5.31%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.12% [-2.71% to 1.46%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.17% [2.10% to 6.22%]; 3.78% [1.84% to 6.05%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.29% [-11.51% to 4.70%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 235.32% [101.59% to 458.59%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 19.08% [12.68% to 26.30%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 2.60% [0.96% to 4.48%]; 0.10% [-1.37% to 1.39%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 27.22% [14.58% to 52.86%]; 7.18% [2.77% to 43.69%]; -23.64% [-37.48% to -12.31%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -1.94% [-6.98% to 0.86%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 1.65% [-3.72% to 26.91%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -1.18% [-5.75% to 2.42%] | median ≤ 0, ≤ 2 seeds > +1 pt* | FAIL (3 above +1 pt) |
| HIGH | info | deposit rate in the window | 0.85% [0.68% to 1.49%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -33.73% [-52.60% to 5.28%]; 6.06% [-48.71% to 77.52%]; -33.37% [-59.47% to -17.81%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,126 [835 to 1,486]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 538,393 [297,201 to 732,423] | ≥ 75.00% of baseline 532,042* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 332,066 [164,471 to 517,238] | ≥ 75.00% of baseline 349,699* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 101.05% [79.69% to 115.10%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 9,254 [5,792 to 14,018] | ≥ 75.00% of baseline 9,254* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 121.25% [81.28% to 413.18%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 21,942 [13,396 to 41,305] | ≥ 75.00% of baseline 20,528* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 101.25% [73.45% to 150.42%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 12,414 [3,575 to 19,140] | ≥ 75.00% of baseline 12,402* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 84.83% [49.80% to 103.99%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 105.6, ZERO_SLACK 84.3, ZERO_HOT 12.3, SHOCK 26.9, HIGH 17.4; total 64.1 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 538393.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 332065.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 9253.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 21942.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 12413.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
