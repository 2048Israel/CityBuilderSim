# Ensemble baseline: 0.9.12

The ensemble on the model of **0.9.12**: classes snapshot of commit b4bf96f4 (2026-11-05T06:04Z), label BA1e, 80 of 80 runs.
Scored against `docs/ensemble/0.9.12.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.14% [1.87% to 2.74%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.34% [2.24% to 4.43%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 8 [0 to 22]; 0 [0 to 8] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 14 [12 to 35] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 5 [0 to 13]; 100.00% [98.63% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.50% [4.19% to 5.18%]; 27.87% [24.26% to 32.50%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.21% [1.99% to 2.85%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.84% [0.59% to 4.89%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 84 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.05% [1.22% to 8.55%]; 15.13% [9.83% to 25.57%]; 11.14% [5.05% to 16.51%]; 11.63% [7.37% to 26.72%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 4.80% [3.43% to 13.69%]; 4.85% [3.73% to 14.24%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 115.17% [19.32% to 215.77%]; 58.51% [8.07% to 134.10%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 13.45% [7.99% to 19.41%]; 6.19% [0.64% to 9.98%]; 84.65% [24.14% to 189.12%]; 176.44% [58.00% to 572.43%]; 165.98% [38.92% to 657.52%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.76% [2.31% to 11.78%]; 3.64% [-0.81% to 8.90%]; -7.70% [-13.23% to -1.63%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.84% [-2.52% to 0.70%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (14/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.01% [0.83% to 6.16%]; 3.72% [2.23% to 5.96%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.68% [-8.48% to 1.36%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 225.57% [82.18% to 725.64%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 15.95% [13.76% to 32.20%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.06% [-2.40% to 3.59%]; -0.00% [-2.57% to 1.84%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 23.60% [8.62% to 32.23%]; 4.04% [2.46% to 77.10%]; -32.05% [-39.18% to -5.62%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.85% [-7.13% to 5.50%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 4.38% [-10.64% to 22.56%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -3.06% [-7.38% to 4.03%] | median ≤ 0, ≤ 2 seeds > +1 pt* | FAIL (4 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.52% [0.73% to 2.36%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -42.29% [-71.68% to 6.14%]; -18.37% [-50.40% to 209.05%]; -45.42% [-65.79% to 10.48%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m878 [782 to 1,128]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 572,286 [451,625 to 772,745] | ≥ 75.00% of baseline 323,765* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 392,749 [265,331 to 474,622] | ≥ 75.00% of baseline 216,458* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 100.51% [96.43% to 111.09%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 6,734 [1,783 to 12,993] | ≥ 75.00% of baseline 8,657* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 64.83% [16.68% to 199.31%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 31,934 [19,081 to 49,937] | ≥ 75.00% of baseline 26,158* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 105.48% [66.79% to 131.02%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 13,306 [8,621 to 22,326] | ≥ 75.00% of baseline 12,726* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 81.28% [47.21% to 102.29%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 22/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 69.7, ZERO_SLACK 57.5, ZERO_HOT 12.1, SHOCK 20.6, HIGH 17.1; total 49.1 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 572286.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 392749.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6734.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 31933.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 13306.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
