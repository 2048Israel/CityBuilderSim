# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 237f12d4+dirty (2026-11-02T13:40Z), label FC1, 80 of 80 runs.
Scored against `docs/ensemble/0.8.95.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.24% [1.98% to 2.43%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.50% [2.30% to 5.30%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 8 [0 to 14]; 0 [0 to 8] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 13 [10 to 19] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 0 [0 to 9]; 100.00% [99.94% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.50% [4.13% to 4.98%]; 27.18% [22.08% to 31.39%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.32% [2.06% to 2.52%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.32% [0.50% to 4.49%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 65 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.84% [0.94% to 7.76%]; 14.45% [7.11% to 23.04%]; 9.69% [5.58% to 13.09%]; 10.74% [5.06% to 21.09%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 8.42% [5.13% to 14.58%]; 9.02% [5.67% to 14.47%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 91.76% [21.71% to 210.61%]; 50.10% [6.81% to 123.24%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.48% [6.60% to 19.03%]; 5.59% [1.73% to 11.99%]; 69.65% [20.18% to 191.29%]; 128.17% [11.91% to 369.59%]; 137.24% [-6.29% to 415.64%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.76% [1.94% to 12.02%]; 2.31% [-1.19% to 7.72%]; -6.93% [-16.85% to -0.93%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.81% [-2.55% to 1.13%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.06% [0.13% to 7.49%]; 4.31% [2.47% to 6.51%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | 1.22% [-3.21% to 4.02%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 157.50% [33.46% to 688.14%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 16.18% [13.92% to 31.31%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.04% [-2.17% to 2.06%]; -0.14% [-3.84% to 1.44%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 23.24% [10.08% to 66.11%]; 3.67% [2.06% to 31.22%]; -31.26% [-33.23% to -14.15%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -2.74% [-6.05% to 2.88%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 0.53% [-7.17% to 11.84%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -1.60% [-6.72% to 4.83%] | median ≤ 0, ≤ 2 seeds > +1 pt* | FAIL (3 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.23% [0.69% to 4.20%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -36.04% [-51.09% to 14.21%]; 4.25% [-33.98% to 129.01%]; -42.69% [-61.18% to -6.09%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m968 [849 to 1,085]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 341,460 [283,059 to 387,488] | ≥ 75.00% of baseline 336,202* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 243,102 [180,459 to 281,849] | ≥ 75.00% of baseline 231,491* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 101.46% [97.48% to 113.72%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 9,025 [7,189 to 11,915] | ≥ 75.00% of baseline 10,135* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 88.21% [65.22% to 148.09%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 31,678 [21,204 to 39,495] | ≥ 75.00% of baseline 29,954* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 103.39% [65.08% to 141.84%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 14,387 [11,230 to 23,313] | ≥ 75.00% of baseline 14,993* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 84.81% [57.77% to 107.06%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 58.7, ZERO_SLACK 58.9, ZERO_HOT 10.9, SHOCK 20.1, HIGH 17.3; total 47.5 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 341460.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 243101.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 9025.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 31677.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 14387.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
