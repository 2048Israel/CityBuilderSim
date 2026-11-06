# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 3acf3186 (2026-11-06T04:41Z), label xi1, 80 of 80 runs.
Scored against `docs/ensemble/0.9.23.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.36% [1.87% to 2.79%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.78% [2.87% to 4.86%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [0 to 25]; 0 [0 to 0] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 16 [10 to 35] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 15]; 100.00% [98.43% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.83% [4.35% to 5.50%]; 26.71% [23.84% to 32.31%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.47% [1.98% to 2.92%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.00% [1.11% to 4.79%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 107 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.31% [1.17% to 8.48%]; 13.09% [9.23% to 24.43%]; 11.01% [6.58% to 16.53%]; 12.06% [4.58% to 16.14%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 5.02% [2.94% to 14.94%]; 5.79% [3.27% to 14.82%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 90.10% [44.98% to 231.94%]; 39.29% [18.08% to 141.26%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.86% [7.09% to 18.32%]; 6.11% [2.42% to 9.71%]; 80.29% [5.05% to 151.08%]; 134.87% [8.25% to 363.15%]; 139.06% [1.35% to 380.47%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.47% [0.53% to 10.31%]; 3.30% [0.31% to 6.37%]; -7.79% [-12.28% to -0.90%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.48% [-2.35% to 3.04%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.40% [0.56% to 8.21%]; 3.80% [2.51% to 6.50%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -0.14% [-4.46% to 3.06%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 172.89% [23.76% to 464.91%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 15.91% [12.84% to 29.93%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 0.94% [-2.19% to 3.02%]; -0.21% [-1.70% to 1.34%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 22.88% [18.22% to 62.61%]; 6.92% [3.65% to 18.72%]; -31.74% [-37.43% to -4.10%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -4.16% [-5.79% to -0.90%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 4.25% [-0.70% to 14.60%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -3.52% [-6.90% to -1.05%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) |
| HIGH | info | deposit rate in the window | 0.95% [0.52% to 1.80%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -44.24% [-54.63% to -17.90%]; -26.95% [-40.53% to 20.88%]; -47.56% [-59.98% to -5.11%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m868 [793 to 1,404]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 602,188 [333,502 to 770,803] | ≥ 75.00% of baseline 602,188* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 397,895 [150,293 to 521,216] | ≥ 75.00% of baseline 391,923* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 100.41% [88.48% to 111.93%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 9,323 [2,211 to 12,174] | ≥ 75.00% of baseline 9,323* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 93.32% [30.39% to 365.57%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 29,413 [8,629 to 44,976] | ≥ 75.00% of baseline 29,852* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 98.43% [65.38% to 146.38%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 15,097 [2,688 to 21,731] | ≥ 75.00% of baseline 15,097* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 86.73% [58.92% to 96.42%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 74.8, ZERO_SLACK 71.9, ZERO_HOT 14.1, SHOCK 27.4, HIGH 23.2; total 56.0 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 602188.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 397895.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 9323.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 29413.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 15097.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
