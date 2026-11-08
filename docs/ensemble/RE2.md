# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 3d063845 (2026-11-08T11:34Z), label RE2, 80 of 80 runs.
Scored against `docs/ensemble/0.9.50.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.27% [1.86% to 2.86%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.44% [1.69% to 4.28%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 9 [0 to 28]; 0 [0 to 0] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 12 [7 to 13] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 1 [0 to 6]; 100.00% [97.90% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.25% [3.75% to 5.11%]; 21.09% [15.74% to 26.71%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.34% [1.92% to 2.91%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.12% [1.18% to 5.99%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 129 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.21% [1.74% to 9.56%]; 13.79% [11.65% to 24.78%]; 10.07% [5.37% to 15.34%]; 12.69% [6.70% to 22.36%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 6.75% [3.33% to 13.41%]; 7.32% [4.52% to 13.89%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 78.61% [29.36% to 286.02%]; 37.59% [14.24% to 185.63%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 9.92% [5.04% to 13.34%]; 9.20% [3.23% to 12.23%]; 49.34% [13.16% to 104.21%]; 142.15% [49.50% to 255.32%]; 162.19% [51.43% to 312.80%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 4.34% [1.29% to 7.76%]; 5.57% [0.38% to 8.95%]; -1.29% [-7.42% to 5.31%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.12% [-2.71% to 1.46%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.17% [2.10% to 6.22%]; 3.78% [1.84% to 6.05%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.29% [-11.51% to 4.70%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 235.32% [101.59% to 465.07%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 19.62% [12.68% to 30.65%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 2.71% [0.21% to 4.16%]; -0.53% [-1.33% to 1.39%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 28.89% [14.58% to 52.86%]; 10.32% [4.13% to 47.91%]; -21.45% [-37.48% to -12.31%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -1.86% [-5.84% to 0.83%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 1.68% [-3.72% to 26.91%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -0.97% [-4.77% to 2.42%] | median ≤ 0, ≤ 2 seeds > +1 pt* | FAIL (3 above +1 pt) |
| HIGH | info | deposit rate in the window | 0.88% [0.68% to 1.54%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -33.69% [-42.39% to 5.28%]; 10.90% [-47.02% to 77.66%]; -32.33% [-53.00% to -17.58%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,118 [835 to 1,486]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 532,042 [297,201 to 737,284] | ≥ 75.00% of baseline 522,706* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 349,699 [164,471 to 573,143] | ≥ 75.00% of baseline 356,096* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 101.55% [91.29% to 106.06%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 9,254 [5,792 to 14,018] | ≥ 75.00% of baseline 9,558* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 115.44% [81.28% to 413.18%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 20,528 [13,396 to 41,305] | ≥ 75.00% of baseline 24,664* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 103.43% [86.95% to 142.47%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 12,402 [3,575 to 19,140] | ≥ 75.00% of baseline 10,527* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 84.75% [49.82% to 103.68%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 69.1, ZERO_SLACK 63.8, ZERO_HOT 15.2, SHOCK 32.2, HIGH 29.9; total 56.8 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 532042.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 349699.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 9253.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 20527.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 12401.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
