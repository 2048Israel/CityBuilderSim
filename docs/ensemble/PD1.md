# Ensemble baseline: 0.9.40

The ensemble on the model of **0.9.40**: classes snapshot of commit 71037d15 (2026-11-07T10:24Z), label PD1, 80 of 80 runs.
Scored against `docs/ensemble/0.9.26.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.35% [2.03% to 2.52%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.79% [2.77% to 4.64%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [0 to 25]; 0 [0 to 0] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 18 [11 to 38] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 15]; 100.00% [98.58% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.78% [4.45% to 5.23%]; 26.71% [24.35% to 32.55%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.46% [2.13% to 2.62%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.17% [1.35% to 5.92%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 92 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.72% [1.27% to 9.30%]; 14.09% [10.39% to 31.40%]; 10.65% [7.56% to 15.02%]; 10.19% [6.04% to 21.53%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 4.91% [2.77% to 12.65%]; 5.83% [3.23% to 12.75%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 86.72% [41.68% to 301.87%]; 44.40% [24.04% to 177.37%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.80% [7.09% to 19.01%]; 6.10% [2.42% to 9.71%]; 80.50% [5.06% to 151.07%]; 135.23% [8.26% to 361.60%]; 141.01% [2.51% to 375.35%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.50% [0.53% to 10.31%]; 3.44% [0.26% to 6.33%]; -7.80% [-12.09% to -0.90%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.26% [-3.84% to 1.80%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.38% [-0.01% to 8.21%]; 3.81% [2.57% to 6.68%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -0.69% [-9.79% to 2.61%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 173.52% [23.95% to 468.91%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 15.98% [13.29% to 25.70%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.76% [-0.72% to 4.52%]; -0.13% [-0.79% to 2.65%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 23.98% [15.14% to 60.82%]; 6.42% [2.95% to 14.42%]; -29.03% [-35.07% to -2.71%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -4.64% [-6.36% to 3.27%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 4.20% [-0.41% to 10.69%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -3.80% [-8.63% to 4.84%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (2 above +1 pt) |
| HIGH | info | deposit rate in the window | 0.85% [0.58% to 2.58%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -40.78% [-60.03% to 36.14%]; -28.34% [-38.20% to 68.94%]; -49.25% [-62.09% to 4.93%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m864 [780 to 1,194]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 567,766 [418,457 to 744,177] | ≥ 75.00% of baseline 602,188* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 387,966 [246,363 to 490,177] | ≥ 75.00% of baseline 397,895* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 100.20% [93.73% to 104.74%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 9,296 [2,234 to 12,611] | ≥ 75.00% of baseline 9,323* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 93.34% [30.73% to 345.87%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 38,240 [12,279 to 48,064] | ≥ 75.00% of baseline 29,413* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 101.47% [73.74% to 144.70%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 13,924 [7,027 to 17,009] | ≥ 75.00% of baseline 15,097* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 83.43% [70.93% to 101.46%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 74.2, ZERO_SLACK 57.6, ZERO_HOT 12.4, SHOCK 25.6, HIGH 20.1; total 50.9 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 567765.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 387966.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 9296.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 38240.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 13924.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
