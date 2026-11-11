# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit bf20a92b+dirty (2026-11-11T00:18Z), label GV1, 80 of 80 runs.
Scored against `docs/ensemble/0.9.89.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.64% [2.10% to 3.30%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (13/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.58% [1.66% to 5.48%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 12 [1 to 33]; 0 [0 to 12] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 27 [11 to 55] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 6 [1 to 17]; 100.00% [96.19% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 5.44% [4.39% to 6.60%]; 26.53% [24.12% to 35.65%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.75% [2.20% to 3.46%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.09% [0.59% to 4.59%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) |
| ZERO_SLACK | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.13% [1.16% to 6.58%]; 2.00% [1.14% to 3.80%] | - | info |
| ZERO_SLACK | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.09% [0.59% to 4.59%]; longest spell 85 months | - | would fail |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.91% [0.14% to 8.16%]; 15.62% [10.51% to 27.03%]; 11.20% [7.46% to 13.14%]; 9.60% [4.44% to 27.39%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 12.87% [5.59% to 31.09%]; 13.71% [5.94% to 32.46%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 83.26% [25.95% to 260.90%]; 48.24% [6.13% to 137.43%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 13.07% [4.90% to 18.96%]; 5.39% [-0.92% to 9.54%]; 80.88% [-1.96% to 246.85%]; 95.07% [24.36% to 414.86%]; 104.05% [11.67% to 423.21%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.53% [-0.21% to 13.75%]; 1.69% [-1.91% to 7.99%]; -8.45% [-12.46% to 0.80%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.14% [-3.60% to 3.80%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | FAIL (10/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.63% [0.19% to 7.31%]; 4.01% [0.55% to 6.37%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.60% [-6.00% to 5.30%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 158.91% [34.05% to 555.38%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 16.43% [12.15% to 34.09%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.01% [-1.64% to 3.82%]; -0.09% [-1.86% to 2.41%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 23.52% [12.74% to 63.45%]; 6.46% [-4.58% to 58.91%]; -32.42% [-41.96% to -9.61%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -2.75% [-5.98% to -0.55%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 2.02% [-7.91% to 25.26%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -2.25% [-10.33% to 2.48%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) |
| HIGH | info | deposit rate in the window | 2.21% [0.24% to 8.35%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -37.64% [-54.76% to -10.60%]; -7.09% [-40.77% to 24.69%]; -48.00% [-62.52% to -22.15%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,100 [749 to 1,935]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 268,306 [138,743 to 599,954] | ≥ 75.00% of baseline 618,792* | FAIL |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 173,386 [49,927 to 395,965] | ≥ 75.00% of baseline 409,599* | FAIL |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 99.43% [92.78% to 158.81%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 10,502 [5,777 to 11,956] | ≥ 75.00% of baseline 12,898* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 80.74% [55.33% to 169.76%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 23,388 [15,205 to 75,241] | ≥ 75.00% of baseline 34,660* | FAIL |
| SHOCK | info | final population over the AUTO2 twin's, same month | 97.66% [44.13% to 172.94%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 18,155 [5,948 to 31,893] | ≥ 75.00% of baseline 14,570* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 95.61% [18.24% to 111.69%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 20/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 81.6, ZERO_SLACK 64.5, ZERO_HOT 18.8, SHOCK 37.5, HIGH 25.6; total 61.3 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 268305.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 173386.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 10501.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 23387.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 18155.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
