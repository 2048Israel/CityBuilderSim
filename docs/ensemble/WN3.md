# Ensemble baseline: 0.8.69

The ensemble on the model of **0.8.69**: classes snapshot of commit 8aba2e2d (2026-10-29T04:59Z), label wn3, 80 of 80 runs.
Scored against `docs/ensemble/0.8.68.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 1.87% [1.67% to 2.21%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 2.38% [1.72% to 4.34%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 8 [0 to 24]; 0 [0 to 3] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 10 [2 to 12] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 2 [0 to 7]; 100.00% [95.80% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 3.63% [3.23% to 4.23%]; 21.67% [16.44% to 27.08%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 1.91% [1.72% to 2.26%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 1.66% [0.26% to 4.24%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 64 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.95% [2.12% to 7.90%]; 12.51% [4.15% to 21.76%]; 8.92% [3.33% to 13.39%]; 10.24% [2.18% to 17.92%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.18% [3.49% to 14.68%]; 10.23% [4.33% to 15.55%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 57.98% [11.79% to 186.04%]; 30.13% [1.06% to 112.41%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 15.36% [8.70% to 21.73%]; 6.15% [2.97% to 15.34%]; 93.60% [43.21% to 144.10%]; 161.14% [59.03% to 622.90%]; 138.71% [22.23% to 1078.39%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.30% [3.83% to 10.21%]; 3.83% [1.08% to 12.19%]; -8.52% [-14.07% to -5.43%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.25% [-3.41% to 4.90%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 1.58% [0.07% to 8.68%]; 2.78% [1.52% to 4.93%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.21% [-8.60% to 1.67%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 232.74% [65.83% to 689.84%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 15.44% [10.05% to 22.43%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.63% [-0.79% to 4.15%]; -0.27% [-1.84% to 1.43%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 27.12% [8.11% to 59.38%]; 3.67% [1.28% to 19.04%]; -29.55% [-42.13% to -5.56%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -1.04% [-4.67% to 2.39%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 0.68% [-5.95% to 4.25%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -0.12% [-5.31% to 5.17%] | median ≤ 0, ≤ 2 seeds > +1 pt* | FAIL (6 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.07% [0.27% to 3.78%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -15.15% [-52.97% to 22.55%]; 10.09% [-36.78% to 55.46%]; -30.54% [-53.14% to 3.89%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,122 [982 to 1,886]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 304,036 [92,624 to 349,280] | ≥ 75.00% of baseline 255,250* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 206,520 [52,174 to 237,489] | ≥ 75.00% of baseline 165,130* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 103.17% [91.33% to 140.16%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 4,524 [2,655 to 6,962] | ≥ 75.00% of baseline 3,916* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 89.83% [58.09% to 181.73%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 16,546 [3,950 to 27,261] | ≥ 75.00% of baseline 15,096* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 92.99% [71.21% to 138.42%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 8,244 [3,876 to 12,621] | ≥ 75.00% of baseline 7,014* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 88.41% [69.99% to 106.21%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 71.4, ZERO_SLACK 55.0, ZERO_HOT 15.1, SHOCK 31.4, HIGH 25.4; total 53.1 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 304036.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 206520.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 4524.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 16546.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 8244.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
