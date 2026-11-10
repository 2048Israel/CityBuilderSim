# Ensemble baseline: 0.9.86

The ensemble on the model of **0.9.86**: classes snapshot of commit 49c2d522 (2026-11-10T15:08Z), label GW1r, 80 of 80 runs.
Scored against `docs/ensemble/0.9.83.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.08% [1.86% to 2.81%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 2.74% [1.94% to 3.78%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 13 [0 to 27]; 0 [0 to 5] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 10 [7 to 24] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 1 [0 to 12]; 100.00% [99.57% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.00% [3.70% to 4.93%]; 22.04% [16.53% to 26.53%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.14% [1.94% to 2.86%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 1.97% [0.19% to 5.48%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) |
| ZERO_SLACK | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 2.53% [0.62% to 6.61%]; 1.74% [0.94% to 2.23%] | - | info |
| ZERO_SLACK | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 1.97% [0.19% to 5.48%]; longest spell 70 months | - | would fail |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.91% [0.56% to 9.00%]; 13.88% [10.41% to 19.75%]; 10.88% [3.83% to 12.94%]; 13.67% [8.75% to 17.16%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 6.30% [3.16% to 16.06%]; 7.29% [3.97% to 17.18%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 63.05% [13.10% to 243.67%]; 40.97% [-1.52% to 171.82%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.19% [6.14% to 20.44%]; 9.22% [1.09% to 12.52%]; 70.52% [12.16% to 278.61%]; 149.57% [72.88% to 790.29%]; 149.58% [49.01% to 532.38%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.84% [1.21% to 15.01%]; 4.88% [-1.02% to 9.28%]; -3.93% [-11.19% to 4.30%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.13% [-3.54% to 1.53%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.50% [1.08% to 6.77%]; 3.17% [0.56% to 5.78%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -2.10% [-4.97% to 2.30%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 266.79% [93.60% to 1122.10%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 17.41% [13.60% to 31.77%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.95% [-0.78% to 5.36%]; -0.21% [-1.37% to 1.25%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 30.03% [21.69% to 62.39%]; 9.21% [2.96% to 20.49%]; -24.70% [-35.84% to -10.24%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -4.70% [-5.67% to -1.29%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 3.77% [1.35% to 23.93%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -3.87% [-5.13% to 1.64%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.40% [0.77% to 3.83%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -41.06% [-55.14% to -9.90%]; -27.81% [-35.51% to 30.12%]; -52.75% [-61.35% to -36.64%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m853 [753 to 1,779]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 618,792 [231,274 to 764,286] | ≥ 75.00% of baseline 581,556* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 409,599 [106,202 to 645,609] | ≥ 75.00% of baseline 389,740* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 105.25% [94.71% to 128.80%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 12,898 [3,939 to 20,325] | ≥ 75.00% of baseline 9,262* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 107.18% [32.22% to 134.60%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 34,660 [20,249 to 54,266] | ≥ 75.00% of baseline 29,630* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 99.41% [79.56% to 122.92%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 14,570 [10,710 to 23,978] | ≥ 75.00% of baseline 13,722* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 84.31% [69.64% to 98.17%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 108.0, ZERO_SLACK 81.2, ZERO_HOT 19.5, SHOCK 34.5, HIGH 27.1; total 73.3 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 618792.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 409599.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 12898.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 34660.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 14570.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
