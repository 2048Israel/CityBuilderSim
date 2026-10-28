# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit e1f535df (2026-10-28T13:36Z), label fr8, 80 of 80 runs.
Scored against `docs/ensemble/0.8.57.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.21% [2.01% to 2.33%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.26% [2.47% to 5.01%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 4 [0 to 12]; 0 [0 to 2] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 10 [3 to 31] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 2 [0 to 6]; 100.00% [99.82% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.06% [3.65% to 4.46%]; 18.31% [13.19% to 23.52%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.26% [2.06% to 2.38%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 1.88% [0.33% to 4.77%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 63 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.22% [2.15% to 8.24%]; 12.46% [5.33% to 22.37%]; 8.56% [3.85% to 15.99%]; 10.59% [3.94% to 18.50%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 11.06% [6.78% to 14.03%]; 11.31% [5.37% to 14.05%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 69.70% [2.81% to 200.95%]; 32.27% [3.08% to 113.73%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 2.32% [0.29% to 4.40%]; 2.42% [-0.96% to 9.07%]; 13.72% [-7.59% to 23.44%]; 7.67% [-10.57% to 89.89%]; -0.42% [-14.56% to 96.64%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 1.31% [-0.79% to 2.15%]; 0.18% [-2.56% to 5.81%]; 0.94% [-3.93% to 5.66%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.84% [-3.02% to 1.40%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would pass) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.37% [0.41% to 6.65%]; 4.51% [1.16% to 7.19%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | 0.58% [-4.50% to 3.21%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 57.10% [7.90% to 214.38%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 15.88% [12.77% to 22.89%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.58% [-0.37% to 4.70%]; -0.50% [-2.40% to 1.49%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 26.78% [16.11% to 65.90%]; 2.93% [1.70% to 31.67%]; -26.23% [-41.82% to -16.61%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.13% [-4.39% to 2.19%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 4.85% [-4.39% to 11.48%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -2.80% [-5.50% to 7.56%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (2 above +1 pt) |
| HIGH | info | deposit rate in the window | 0.59% [0.45% to 1.45%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -24.57% [-47.77% to -1.35%]; -18.51% [-38.24% to 91.92%]; -41.52% [-55.20% to 2.43%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,243 [1,110 to 1,480]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 246,893 [199,706 to 317,402] | ≥ 75.00% of baseline 258,838* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 159,060 [133,244 to 197,127] | ≥ 75.00% of baseline 171,863* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 100.84% [96.49% to 108.60%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 3,916 [2,314 to 6,167] | ≥ 75.00% of baseline 3,744* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 115.20% [75.58% to 156.41%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 15,106 [9,977 to 19,149] | ≥ 75.00% of baseline 15,149* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 101.87% [80.09% to 142.78%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 7,014 [4,329 to 10,750] | ≥ 75.00% of baseline 7,164* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 82.80% [60.21% to 99.40%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 41.0, ZERO_SLACK 30.4, ZERO_HOT 6.5, SHOCK 10.7, HIGH 9.0; total 26.2 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 246893.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 159059.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 3916.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 15106.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 7014.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
