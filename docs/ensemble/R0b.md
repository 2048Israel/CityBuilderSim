# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 2af0f13f (2026-11-05T03:43Z), label R0b, 80 of 80 runs.
Scored against `docs/ensemble/0.9.12.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.43% [2.01% to 2.82%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 4.28% [3.16% to 5.43%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 12 [3 to 20]; 0 [0 to 2] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 18 [12 to 36] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 9]; 100.00% [100.00% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.94% [4.46% to 5.79%]; 28.01% [23.10% to 33.01%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.55% [2.10% to 2.93%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.62% [0.77% to 5.09%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 83 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.89% [0.69% to 8.74%]; 17.08% [5.80% to 25.24%]; 9.58% [6.39% to 13.59%]; 10.48% [6.45% to 18.55%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.18% [5.74% to 15.12%]; 10.79% [6.03% to 15.70%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 99.27% [19.54% to 237.19%]; 54.22% [12.29% to 139.17%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 16.54% [10.27% to 21.99%]; 7.09% [3.09% to 13.06%]; 100.30% [24.15% to 172.03%]; 152.51% [79.44% to 388.47%]; 152.95% [73.99% to 433.71%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.81% [2.36% to 11.55%]; 3.18% [-0.36% to 7.38%]; -9.41% [-13.97% to 2.26%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.00% [-1.00% to 0.95%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.95% [3.17% to 7.59%]; 4.53% [2.87% to 7.21%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -0.43% [-7.04% to 3.16%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 226.72% [96.61% to 583.65%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 17.85% [13.65% to 30.85%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.38% [-1.02% to 4.67%]; -0.36% [-2.08% to 1.85%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 23.59% [15.03% to 60.51%]; 10.09% [3.77% to 69.30%]; -27.88% [-36.45% to -1.87%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -2.98% [-5.53% to 0.98%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 1.45% [-7.84% to 20.42%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -2.60% [-5.65% to 3.01%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (2 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.22% [0.71% to 4.16%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -31.33% [-48.00% to 2.23%]; -16.59% [-38.15% to 64.96%]; -41.25% [-56.77% to -12.45%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,016 [853 to 1,200]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 331,626 [259,106 to 381,301] | ≥ 75.00% of baseline 323,765* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 220,368 [150,402 to 284,009] | ≥ 75.00% of baseline 216,458* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 100.14% [92.74% to 106.69%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 9,289 [3,945 to 12,706] | ≥ 75.00% of baseline 8,657* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 103.95% [51.90% to 343.17%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 22,788 [13,733 to 37,260] | ≥ 75.00% of baseline 26,158* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 101.08% [65.33% to 135.68%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 13,490 [7,218 to 18,947] | ≥ 75.00% of baseline 12,726* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 90.88% [61.69% to 110.95%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 47.9, ZERO_SLACK 37.1, ZERO_HOT 8.2, SHOCK 15.9, HIGH 13.4; total 33.7 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 331626.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 220368.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 9289.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 22788.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 13489.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
