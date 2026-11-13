# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 80445b2c (2026-11-13T20:49Z), label HW1, 80 of 80 runs.
Scored against `docs/ensemble/0.9.98.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.78% [2.40% to 3.23%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (12/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 4.00% [2.39% to 5.74%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 12 [4 to 26]; 0 [0 to 0] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 30 [15 to 48] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 0 [0 to 13]; 100.00% [98.68% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 5.13% [4.54% to 6.04%]; 23.01% [18.29% to 28.01%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.86% [2.49% to 3.32%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.67% [1.02% to 4.31%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) |
| ZERO_SLACK | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.65% [1.09% to 5.73%]; 1.94% [1.07% to 2.76%] | - | info |
| ZERO_SLACK | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.67% [1.02% to 4.31%]; longest spell 87 months | - | would fail |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.84% [1.55% to 7.78%]; 16.73% [8.16% to 27.20%]; 10.80% [5.38% to 15.80%]; 9.56% [4.11% to 15.92%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 7.52% [2.36% to 8.30%]; 7.61% [2.25% to 8.59%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 103.14% [24.39% to 193.43%]; 55.81% [12.36% to 115.28%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 16.85% [10.94% to 18.68%]; 9.72% [2.37% to 13.84%]; 140.95% [62.97% to 231.86%]; 253.31% [157.71% to 366.82%]; 259.29% [137.04% to 658.04%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 9.90% [5.40% to 13.28%]; 5.00% [-1.67% to 8.01%]; -6.90% [-15.12% to -1.28%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.02% [-3.31% to 2.90%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.07% [1.44% to 7.60%]; 4.53% [2.68% to 8.68%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | 0.01% [-3.12% to 1.48%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 426.11% [194.13% to 604.63%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 16.35% [11.93% to 22.47%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.12% [-4.57% to 4.92%]; 0.07% [-2.53% to 2.21%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 23.78% [15.99% to 29.53%]; 5.48% [3.78% to 32.53%]; -30.28% [-37.71% to -10.91%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -4.38% [-6.06% to -0.27%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 2.02% [-1.18% to 10.58%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -4.09% [-7.84% to -0.47%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.00% [0.31% to 2.73%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -38.51% [-52.83% to -6.01%]; -24.52% [-37.98% to 7.80%]; -49.75% [-61.91% to -24.91%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m882 [469 to 1,505]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 2,156,835 [1,264,414 to 4,018,855] | ≥ 75.00% of baseline 1,538,083* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 1,485,133 [652,538 to 2,688,242] | ≥ 75.00% of baseline 1,126,109* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 98.43% [92.03% to 105.37%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 6,528 [3,142 to 13,479] | ≥ 75.00% of baseline 10,574* | FAIL |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 63.83% [24.19% to 134.33%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 34,224 [17,123 to 350,464] | ≥ 75.00% of baseline 103,965* | FAIL |
| SHOCK | info | final population over the AUTO2 twin's, same month | 100.29% [62.33% to 326.47%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 29,300 [5,680 to 198,209] | ≥ 75.00% of baseline 55,503* | FAIL |
| HIGH | info | final population over the AUTO2 twin's, same month | 88.66% [65.50% to 124.93%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 21/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 252.1, ZERO_SLACK 193.6, ZERO_HOT 21.6, SHOCK 48.8, HIGH 44.0; total 159.2 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2156835.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1485133.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6527.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 34224.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 29300.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
